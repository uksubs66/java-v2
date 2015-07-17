/***************************************************************************************
 *   Member_lott:  This servlet will process the Lottery request from
 *                    the Member's Sheet page.
 *
 *
 *   called by:  Member_sheet (doPost)
 *               Member_teelist (doPost)
 *
 *
 *   created: 7/24/2003   Bob P.
 *
 *   last updated:       ******* keep this accurate ******
 *
 *        6/08/10   Added code to guest tracking verification to allow for the guest "TBA" option
 *        5/19/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        4/26/10   Brae Burn CC - Only the originator can remove players or cancel an existing request.  Any player can add to a request. (case 1826).
 *        4/22/10   Brae Burn CC (braeburncc) - set default mode of trans to 'NAP' for all guest types.
 *        4/16/10   Added guest tracking processing
 *        2/02/10   Trim the notes in verify
 *       12/03/09   Call to alphaTable.displayPartnerList added to print the partner list, outdated code removed
 *       11/20/09   If user selects an existing request, limit the number of groups to the original number requested.
 *       11/03/09   Do not force members to specify a guest name if guest type is 'Lottery TBA' or similar.
 *       10/25/09   Add support for Mobile users.
 *       10/04/09   Added activity isolation to the buddy list
 *        9/24/09   Black Diamond Ranch (blackdiamondranch) - Set default mode of trans to 'CCT' for all guest types
 *        9/03/09   Changed processing to pull mships from mship5 instead of club5.
 *        8/20/09   Change how 'slots' value is passed when course name is changed to prevent # of slots selection from resetting each time
 *        7/30/09   Add focus setting for moveguest js method to improve efficiency
 *        6/24/09   Mirasol CC (mirasolcc) - Added custom to restrict 'Tennant Lease' members from submitting lottery registrations (case 1430).
 *        3/11/09   Brookhaven - do not allow guests in lottery requests (case 1625).
 *        2/05/09   Changed minsbefore for Pecan Plantation.  Now 30 minutes instead of 420 minutes
 *       12/04/08   Added restriction suspension checking for member restrictions
 *       11/05/08   Black Diamond Ranch - only allow 2 consecutive times on Saturdays (case 1578).
 *       10/06/08   Save the course selected in the lottery req so the pro will know when approving the req (case 1557).
 *       10/03/08   Add a space after any guest names are put in a tee time slot (case 1551).
 *       10/03/08   Check for replacement text for the word "Lottery" when email is for a lottery request.
 *        8/05/08   Added javascript to reload the page when the course selection is changed while setting up a lottery request
 *        6/27/08   Added javascript to dynamically add/remove Pro Only tmodes to wc selection drop down boxes
 *        6/23/08   Changes to ProOnly tmode checking
 *        6/20/08   Added checkTmodes method to verify Pro-Only tmodes
 *        4/22/08   Add mins-before and mins-after options - later remove all the customs that force these options (case #1459).
 *        4/18/08   MN Valley - remove mins before and mins after - force to 2 hours (case 1352).
 *        4/14/08   Tamarack - remove mins before and mins after - force to 2 hours (case 1448).
 *        3/24/08   Tom's Demo - remove mins before and mins after - force to 6 hours.
 *        3/07/08   Sugar Mill - remove mins before and mins after - force to 4 hours, and force other course option (case #1391).
 *        3/07/08   Mirasol CC - change mins before and mins after to 6 hours.
 *        2/10/08   Marbella CC - change default mins before and after to 2 hours (case 1379).
 *        1/10/08   Trim incoming player names & fix so members can't manually put an X in as a player
 *       11/17/07   Pecan Plantation - change default mins before and after to 8 hours.
 *       10/30/07   Pecan Plantation - remove mins before and mins after (case #1307)
 *        9/24/07   Pinery CC - Hide front/back text in ltime select box for signup - new bln_hideFrontBack var (Case #1045)
 *        8/20/07   Modified call to verifySlot.checkMemNotice
 *        7/24/07   Bonnie Briar - remove mins before and mins after - force to 2 hours (case #1224).
 *        6/12/07   Allow for course=-ALL- option - return to course=all if selected.
 *        5/24/07   Mirasol CC - remove mins before and mins after - force to 6 hours (case #1175).
 *        5/24/07   Tamarack CC - remove mins before and mins after - force to 6 hours (case #1125).
 *        5/04/07   Brantford CC - remove mins before and mins after - force to 2 hours (case #1163).
 *        5/03/07   Burlington Country Club - remove mins before and mins after - force to 2 hours (case #1064).
 *        5/03/07   Correct guest restriction processing - save the # om members in each group prior to guest rest call.
 *        4/24/07   Blue Hill - default mins before & after to 6 hours (case #1117).
 *        4/19/07   Columbine - default mins before & after to 6 hours (case #1106).
 *        4/06/07   Do not include members that are inactive (new inact flag in member2b).
 *        4/05/07   Sunset Ridge - default mins before & after to 6 hours (case #1106).
 *        3/28/07   Denver CC - default mins before & after to 8 hours (case 1078).
 *        2/27/07   Add new 'Member Notice' processing - display pro-defined message if found.
 *        1/24/07   Martin Downs - default mins before & after to 6 hours.
 *       12/13/06   Do not include cross-over times in list of tee times for lottery request.
 *       11/18/06   Martin Downs - remove mins before and mins after - default them to 1/2 hour.
 *                               - also always set 'check other courses' option.
 *       10/24/06   Set the course name in lreqs3 when updating a request in case it changed.
 *       10/10/06   Use checkGuests in Common_lott as it has been updated.
 *        9/18/06   Move checkGuestQuota, checkMnums & countGuests to Common_lott.
 *        9/05/06   Rancho Bernardo - do not send email notifications.
 *        7/11/06   Cherry Hills - force members to specify their modes of trans.
 *        6/14/06   Lakewood CC - remove mins before and mins after - default them to high value.
 *        5/17/06   Rancho Bernardo - force the mode of trans to 'CCH' for all guest types.
 *        5/02/06   Rancho Bernardo - remove mins before and mins after - default them to high value.
 *        4/01/06   Add check for guest names required.
 *        3/10/06   Cherry Hills - add a custom restriction for member types.
 *        3/07/06   Cherry Hills - remove mins before and mins after - default them to high value.
 *       10/27/05   Do not allow member to change lottery request parms if they did not originate the request.
 *        8/07/05   Mission Viejo and Pecan Plantation - change default mins before & after to 4 hours.
 *        6/06/05   Allow for multiple courses and 'Weighted Proximity' lottery type.
 *        4/26/05   Do not display tee times if event or blocker in teecurr.
 *        4/21/05   For new requests, change default mins before & after from 30 to 60.
 *        4/11/05   Do not filter the Lottery name - caused problems with times not displayed.
 *        3/30/05   Westchester Custom - must be > 2 players on w/e before 2 PM.
 *        1/24/05   Ver 5 - change club2 to club5.
 *       12/09/04   Ver 5 - Change Member Name Alphabit table to common table.
 *        9/21/04   For new requests, change default mins before & after from zero to 30.
 *        9/20/04   Ver 5 - change getClub from SystemUtils to common.
 *        7/01/04   Do not display the word 'Lottery' for Old Oaks.
 *        2/09/04   Add separate 9-hole option.
 *        2/06/04   Add support for configurable transportation modes.
 *        1/14/04   JAG Modifications to match new color scheme
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
import com.foretees.common.parmSlot;
import com.foretees.common.verifySlot;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.alphaTable;
import com.foretees.common.Utilities;


public class Member_lott extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*****************************************************
 // Process the request from Member_sheet
 //*****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   PreparedStatement pstmt3 = null;
   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }


   String jump = "0";                     // jump index - default to zero (for _sheet)
   String returnCourse = "";
   String displayOpt = "";              // display option for Mobile devices
   String letter = "";
     
   int mobile = 0;

   //
   //  Get this session's username
   //
   String club = (String)session.getAttribute("club");
   String user = (String)session.getAttribute("user");
   String name = (String)session.getAttribute("name");   // get users full name

   //
   //  See if Mobile user
   //
   try {      
      mobile = (Integer)session.getAttribute("mobile");        
   }
   catch (Exception ignore) {   
      mobile = 0;
   }
      
   
   //
   //  check for letter prompt from mobile device (user clicked on a letter or guest type)
   //
   if (mobile > 0 && (req.getParameter("letterPrompt") != null || req.getParameter("gstPrompt") != null)) {  
      
      String gstPrompt = "";
      String letterPrompt = "";
      
      if (req.getParameter("gstPrompt") != null) {
         gstPrompt = req.getParameter("gstPrompt").trim();           // get the Guest Type, if selected
      }
      if (req.getParameter("letterPrompt") != null) {
         letterPrompt = req.getParameter("letterPrompt").trim();         // get the Letter, if selected
      }

      if (!gstPrompt.equals("") || !letterPrompt.equals("")) {          // only go there if one was actaully selected

         Common_mobile.namePrompt("lottery", club, user, req, out, con);      // prompt user for name
         return;                                                        // exit and wait for reply
      }
   }

   
   //
   // Process request according to which 'submit' button was selected
   //
   //      'time:fb'   - request from Member_sheet or Member_teelist
   //      'continue'  - request details from Member_lott
   //      'submitForm'    - lottery request from Member_lott
   //      'letter'    - request to list member names from Member_lott
   //      'remove'    - a 'cancel lottery req' request from Member_lott (remove all names)
   //      'cancel'    - user clicked on the 'Go Back' button (return w/o changes)
   //      'return'  - a return to Member_lott from verify
   //
   if (req.getParameter("cancel") != null) {

      cancel(mobile, req, out, con);                      // process cancel request
      return;
   }

   if (req.getParameter("submitForm") != null) {     // if user submitted a lottery request

      verify(req, out, con, session, resp);                 // process reservation requests
      return;
   }

   
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   
   //
   //  Process all other calls
   //
   String index = req.getParameter("index");          //  index value of day (needed by Member_sheet when returning)
   String course = req.getParameter("course");        //  Name of Course

   if (req.getParameter("returnCourse") != null) {        // if returnCourse provided

      returnCourse = req.getParameter("returnCourse");
   }

   //
   //  Get the Display Option if specified (Mobile Devices)
   //
   if (req.getParameter("displayOpt") != null) {

      displayOpt = req.getParameter("displayOpt");        // display option - morning, afternoon, etc.
   }
   
   

   if (req.getParameter("jump") != null) {            // if jump index provided

      jump = req.getParameter("jump");
   }
      

   long lottid = 0;
   String slottid = "";

   boolean bln_hideFrontBack = false; // hide front/back text in ltime select box during signup
   if ( club.equals("pinery") ) bln_hideFrontBack = true;
   
   //
   //  Get the replacement text for the word "Lottery" if club requested one - added to club5 by Support Club Options
   //
   String lotteryText = getClub.getLotteryText(con);         // get replacement text for "Lottery" if provided  
        
   
   
   if (req.getParameter("remove") != null) {         // user wants to delete the request

      slottid = req.getParameter("lottid");          // get the lottery id

      try {
         lottid = Long.parseLong(slottid);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      if (lottid > 0) {
         try {

            //
            //  First, make sure this user is part of this request
            //
            PreparedStatement pstmtl = con.prepareStatement (
               "SELECT name " +
               "FROM lreqs3 " +
               "WHERE user1 = ? OR user2 = ? OR user3 = ? OR user4 = ? OR user5 = ? OR " +
               "user6 = ? OR user7 = ? OR user8 = ? OR user9 = ? OR user10 = ? OR " +
               "user11 = ? OR user12 = ? OR user13 = ? OR user14 = ? OR user15 = ? OR " +
               "user16 = ? OR user17 = ? OR user18 = ? OR user19 = ? OR user20 = ? OR " +
               "user21 = ? OR user22 = ? OR user23 = ? OR user24 = ? OR user25 = ?");

            pstmtl.clearParameters();        // clear the parms
            pstmtl.setString(1, user);
            pstmtl.setString(2, user);
            pstmtl.setString(3, user);
            pstmtl.setString(4, user);
            pstmtl.setString(5, user);
            pstmtl.setString(6, user);
            pstmtl.setString(7, user);
            pstmtl.setString(8, user);
            pstmtl.setString(9, user);
            pstmtl.setString(10, user);
            pstmtl.setString(11, user);
            pstmtl.setString(12, user);
            pstmtl.setString(13, user);
            pstmtl.setString(14, user);
            pstmtl.setString(15, user);
            pstmtl.setString(16, user);
            pstmtl.setString(17, user);
            pstmtl.setString(18, user);
            pstmtl.setString(19, user);
            pstmtl.setString(20, user);
            pstmtl.setString(21, user);
            pstmtl.setString(22, user);
            pstmtl.setString(23, user);
            pstmtl.setString(24, user);
            pstmtl.setString(25, user);
            rs = pstmtl.executeQuery();      // execute the prepared stmt

            if (!rs.next()) {
               
               displayError("Procedure Error", "You cannot cancel a request that you are not part of.<BR><BR>You must be a member currently on the request in order to cancel it.", 
                            "", "back", mobile, out);
               return;
            }
            pstmtl.close();

            //
            //  Now see if this action has been confirmed yet
            //
            if (req.getParameter("ack_remove") != null) {     // if remove has been confirmed

               pstmt3 = con.prepareStatement (
                        "Delete FROM lreqs3 WHERE id = ?");

               pstmt3.clearParameters();               // clear the parms
               pstmt3.setLong(1, lottid);
               pstmt3.executeUpdate();                 // execute the prepared stmt

               pstmt3.close();

            } else {    // not acked yet - display confirmation page
               
               displayCancelConf(mobile, index, course, returnCourse, jump, lottid, out);
               return;    // wait for acknowledgement
            }
         }
         catch (Exception e1) {
            dbError(out, e1);
            return;
         }
      }
      //
      //  Prompt user to return to Member_sheet, Member_searchmem or Member_teelist
      //
      displayCancelDone(mobile, index, course, returnCourse, jump, lotteryText, club, lottid, out);     
      return;
   }                            // end of delete processing

   
   
   //
   //  Request from Member_sheet, Member_lott or Member_teelist
   //
   int count = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int fb = 0;
   int xCount = 0;
   int courseCount = 0;
   int i = 0;
   int hide = 0;
   //int nowc = 0;
   int slots = 0;
   int mins_before = 0;
   int mins_after = 0;
   int players = 0;
   int checkothers = 0;
   int lstate = 0;
   int dhr = 0;
   int dmin = 0;
   int lmin = 0;
   int dfb = 0;
   int in_use = 0;
   int groups = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int p96 = 0;
   int p97 = 0;
   int p98 = 0;
   int p99 = 0;
   int p910 = 0;
   int p911 = 0;
   int p912 = 0;
   int p913 = 0;
   int p914 = 0;
   int p915 = 0;
   int p916 = 0;
   int p917 = 0;
   int p918 = 0;
   int p919 = 0;
   int p920 = 0;
   int p921 = 0;
   int p922 = 0;
   int p923 = 0;
   int p924 = 0;
   int p925 = 0;
   int guest_id1 = 0;
   int guest_id2 = 0;
   int guest_id3 = 0;
   int guest_id4 = 0;
   int guest_id5 = 0;
   int guest_id6 = 0;
   int guest_id7 = 0;
   int guest_id8 = 0;
   int guest_id9 = 0;
   int guest_id10 = 0;
   int guest_id11 = 0;
   int guest_id12 = 0;
   int guest_id13 = 0;
   int guest_id14 = 0;
   int guest_id15 = 0;
   int guest_id16 = 0;
   int guest_id17 = 0;
   int guest_id18 = 0;
   int guest_id19 = 0;
   int guest_id20 = 0;
   int guest_id21 = 0;
   int guest_id22 = 0;
   int guest_id23 = 0;
   int guest_id24 = 0;
   int guest_id25 = 0;
   int allowmins = 0;    // config options from lottery3
   int minsbefore = 0;
   int minsafter = 0;
   int slotSelection = 0;

   long mm = 0;
   long dd = 0;
   long yy = 0;
   long temp = 0;
   long date = 0;

   String player1 = "";            // allow for 5 groups of five
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String player6 = "";
   String player7 = "";
   String player8 = "";
   String player9 = "";
   String player10 = "";
   String player11 = "";
   String player12 = "";
   String player13 = "";
   String player14 = "";
   String player15 = "";
   String player16 = "";
   String player17 = "";
   String player18 = "";
   String player19 = "";
   String player20 = "";
   String player21 = "";
   String player22 = "";
   String player23 = "";
   String player24 = "";
   String player25 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String p6cw = "";
   String p7cw = "";
   String p8cw = "";
   String p9cw = "";
   String p10cw = "";
   String p11cw = "";
   String p12cw = "";
   String p13cw = "";
   String p14cw = "";
   String p15cw = "";
   String p16cw = "";
   String p17cw = "";
   String p18cw = "";
   String p19cw = "";
   String p20cw = "";
   String p21cw = "";
   String p22cw = "";
   String p23cw = "";
   String p24cw = "";
   String p25cw = "";
   String pcw = "";

   String dampm = "";
   String sdate = "";
   String stime = "";
   String ltime = "";
   String sfb = "";
   String dsfb = "";
   String notes = "";
   String hides = "";
   String smins_before = "";              // 'minutes before' selected time
   String smins_after = "";               // 'minutes after' selected time
   String shr = "";
   String smin = "";
   String ampm = "";
   String in_use_by = "";
   String courseName = "";
   String courseL = "";
   String temps = "";
   String orig_by = "";

   boolean reload = false;

   //
   //  array to hold course names
   //
   ArrayList<String> courseA = new ArrayList<String>();

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // hard code a zero for the root activity since FlxRez doesn't support lotteries

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   // Get all the parameters entered
   //
   String day_name = req.getParameter("day");         //  name of the day
   String p5 = req.getParameter("p5");                //  5-somes supported
   String lottName = req.getParameter("lname");       //  Name of the Lottery
   String sslots = "";                                //  # of groups allowed for the Lottery
   if (req.getParameter("reload") != null) {
       sslots = req.getParameter("maxSlots");
       slotSelection = Integer.parseInt(req.getParameter("slots"));
       reload = true;
   } else {
       sslots = req.getParameter("slots");
       slotSelection = 0;
       reload = false;
   }
   String slstate = req.getParameter("lstate");       //  Current state of the Lottery (must be < 4 to get here)
                                        //    1 = before time to take requests (too early for requests)
                                        //    2 = after start time, before stop time (ok to take requests)
                                        //    3 = after stop time, before process time (late, but still ok for pro)
                                        //    4 = requests have already been processed (ok for all tee times now)

   //
   //  Convert the string values to ints
   //
   try {
      slots = Integer.parseInt(sslots);
      lstate = Integer.parseInt(slstate);
   }
   catch (NumberFormatException e) {
      // ignore error
   }
   
   //
   //  check if lottery req id was passed - if not, new request
   //
   lottid = 0;          // new request

   if (req.getParameter("lottid") != null) {

      slottid = req.getParameter("lottid");       // get lottery id if provided

      try {
         lottid = Long.parseLong(slottid);
      }
      catch (NumberFormatException e) {
         // ignore error
      }
   }

   if (((req.getParameter("letter") != null) || (req.getParameter("buddy") != null) ||
       (req.getParameter("mins_before") != null) || (req.getParameter("return") != null) ||
       (req.getParameter("memNotice") != null)) && req.getParameter("reload") == null) {
         
      //
      // a re-entry - user prompted for details or user clicked on a name letter
      //
      sdate = req.getParameter("sdate");

      if (req.getParameter("stime") != null) {

         stime = req.getParameter("stime");
         sfb = req.getParameter("fb");

      } else {                               // time and fb contained in ltime (from lottery info prompt)

         ltime = req.getParameter("ltime");
      }
      smins_before = req.getParameter("mins_before");
      smins_after = req.getParameter("mins_after");
      temps = req.getParameter("checkothers");

      try {
         mins_before = Integer.parseInt(smins_before);
         mins_after = Integer.parseInt(smins_after);
         checkothers = Integer.parseInt(temps);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      //
      //  determine # of players allowed for this request
      //
      //   Players will = 4, 5, 8, 10, 12, 15, 16, 20 or 25 (based on 4 or 5-somes and number of slots/groups)
      //
      if (p5.equals( "Yes" )) {                  // 5-somes allowed ?

         p5 = "Yes";
         players = slots * 5;                              // Yes, set total # of players

      } else {

         p5 = "No";
         players = slots * 4;                              // No
      }

   } else {
      
      //
      //    This is the first call (from _sheet, etc.) -
      //
      mins_before = 999;         // indicate new request (will get overridden if not)
      mins_after = 999;

      
      
      //
      //   ******** REMOVE this after changing the following clubs' lottery tables - set allowmins = 1 (NO) !!!!!!!!!!!!!!!!!!!!!!
      //
      boolean skipmins = false;        // init custom flag to skip mins before and mins after parms
        
      if (club.equals( "cherryhills" ) || club.equals( "ranchobernardo" ) || club.equals( "lakewood" ) ||
          club.equals( "martindowns" ) || club.equals( "denvercc" ) || club.equals( "sunsetridge" ) ||
          club.equals( "columbine" ) || club.equals( "bluehill" ) || club.equals("burlington") || club.equals("pecanplantation") || 
          club.equals( "brantford" ) || club.equals("tamarack") || club.equals("mirasolcc") || club.equals("bonniebriar") ||
          club.equals("marbellacc") || club.equals("sugarmill") || club.equals("mnvalleycc") ) {

         skipmins = true;        // set flag to skip mins before and mins after parms
      }

      
      
      if (lottid > 0) {     // if not a new request

         int checkCount = 0;

         //
         //  Check if this request is already in use
         //
         try {
            
            PreparedStatement pstmt1 = con.prepareStatement (
               "UPDATE lreqs3 SET in_use = 1, in_use_by = ? WHERE id = ? AND in_use = 0");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, user);
            pstmt1.setLong(2, lottid);
            
            checkCount = pstmt1.executeUpdate();      // execute the prepared stmt

            pstmt1.close();
         }
         catch (Exception e2) {
            dbError(out, e2);
            return;
         }
         
         if (checkCount == 0) {              // if time slot already in use

            displayError("Request is Busy", "Sorry, but this request is currently busy.<BR><BR>Please try again later.", 
                            "", "back", mobile, out);
            return;
         }
         
         //
         //  Request is not in use - ok to proceed
         //
         try {
            PreparedStatement pstmt = con.prepareStatement (
               "SELECT minsbefore, minsafter, groups, orig_by, checkothers, in_use_by " +
               "FROM lreqs3 WHERE id = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, lottid);       // put the parm in pstmt
            rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               mins_before = rs.getInt(1);
               mins_after = rs.getInt(2);
               groups = rs.getInt(3);       // # of tee times requested
               orig_by = rs.getString(4);
               checkothers = rs.getInt(5);
               in_use_by = rs.getString(6);
            }

            pstmt.close();

         }
         catch (Exception e1) {
            dbError(out, e1);
            return;
         }

      }                // end of IF lottid
        
      if (req.getParameter("stime") != null) {            // if call from Member_teelist

         stime = req.getParameter("stime");
         sfb = req.getParameter("fb");

      } else {                                           // call from Member_sheet

         //
         //    The name of the submit button (time) has the front/back indicator appended to it ('time:fb')
         //    The value contains the time value.
         //
         Enumeration enum1 = req.getParameterNames();        // get the parm name passed

         while (enum1.hasMoreElements()) {

            String pname = (String) enum1.nextElement();

            if (pname.startsWith( "time" )) {

               stime = req.getParameter(pname);              //  get value: time of tee time requested (hhmm AM/PM:)

               StringTokenizer tok = new StringTokenizer( pname, ":" );     // space is the default token, use ':'

               sfb = tok.nextToken();                        // skip past 'time:'
               sfb = tok.nextToken();                        // get the front/back indicator from name of submit button
            }
         }
      }
        
      if (!stime.equals( "" )) {

         StringTokenizer tok = new StringTokenizer( stime, ": " );     // space is the default token

         shr = tok.nextToken();
         smin = tok.nextToken();
         ampm = tok.nextToken();
      }

      sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)

      //
      //  Convert the values from string to int
      //
      try {
         date = Long.parseLong(sdate);
         fb = Integer.parseInt(sfb);
         hr = Integer.parseInt(shr);
         min = Integer.parseInt(smin);
      }
      catch (NumberFormatException e) {
         // ignore error
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
      //  If Black Diamond Ranch and Saturday - limit slots to 2 (normally is 5)
      //
      if (club.equals( "blackdiamondranch" ) && day_name.equals( "Saturday" ) && date >= 20081122) {

         slots = 2;
      }
      
      try {
         //
         //  Get the names of all courses if multiple
         //
         if (!course.equals( "" )) {           // if course specified, then multiple courses supported for this club

            //
            //  Get the names of all courses for this club
            //
            courseA = Utilities.getCourseNames(con);     // get all the course names

            courseCount = courseA.size();            // save the total # of courses for later
         }

         //
         //  Get the course option for this lottery
         //
         pstmt3 = con.prepareStatement (
                  "SELECT courseName, minsbefore, minsafter, allowmins FROM lottery3 WHERE name = ?");

         pstmt3.clearParameters();        // clear the parms
         pstmt3.setString(1, lottName);       // put the parm in stmt
         rs = pstmt3.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            courseL = rs.getString(1);    // course supported
            minsbefore = rs.getInt(2);    
            minsafter = rs.getInt(3);    
            allowmins = rs.getInt(4);    
         }
         pstmt3.close();              // close the stmt

      }
      catch (Exception e1) {

         dbError(out, e1);
         return;
      }
        
      //
      //********************************************************************
      //   Build a page to prompt user for lottery request details
      //********************************************************************
      //
      if (mobile == 0) {        // if NOT a mobile user
         
         out.println("<HTML>");
         out.println("<HEAD>");
         out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
         if (club.equals( "old Oaks" )) {
            out.println("<Title>Member Tee Time Registration Page</Title>");
         } else {
            out.println("<Title>Member Tee Time Request Page</Title>");
         }
      
         //
         //**********************************************************************************
         //  Handle onChange event from course selection drop down window
         //**********************************************************************************
         //
         out.println("<script type=\"text/javascript\">");
         out.println("<!--");
         out.println("function courseChange() {");  
         out.println("  var e1 = document.createElement(\"input\");");
         out.println("  e1.type = \"hidden\";");
         out.println("  e1.name = \"reload\";");
         out.println("  document.forms['reqForm'].appendChild(e1);");
         out.println("  var e2 = document.createElement(\"input\");");
         out.println("  e2.type = \"hidden\";");
         out.println("  e2.name = \"date\";");
         out.println("  e2.value = \"" + date + "\";");
         out.println("  document.forms['reqForm'].appendChild(e2);");
         out.println("  var e3 = document.createElement(\"input\");");
         out.println("  e3.type = \"hidden\";");
         out.println("  e3.name = \"stime\";");
         out.println("  e3.value = \"" + stime + "\";");
         out.println("  document.forms['reqForm'].appendChild(e3);");
         out.println("  var e4 = document.createElement(\"input\");");
         out.println("  e4.type = \"hidden\";");
         out.println("  e4.name = \"sfb\";");
         out.println("  e4.value = \"" + sfb + "\";");
         out.println("  document.forms['reqForm'].appendChild(e4);");
         out.println("  var e5 = document.createElement(\"input\");");
         out.println("  e5.type = \"hidden\";");
         out.println("  e5.name = \"maxSlots\";");
         out.println("  e5.value = \"" + slots + "\";");
         out.println("  document.forms['reqForm'].appendChild(e5);");
         out.println("  document.forms['reqForm'].submit();");
         out.println("}");        // End of function onCourseChange()
         out.println("// -->");
         out.println("</script>");    // End of script

         out.println("</HEAD>");

         out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

         out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
         out.println("<tr><td valign=\"top\" align=\"center\">");

         out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"336633\" align=\"center\" valign=\"top\">");
         out.println("<tr><td align=\"left\" width=\"300\">&nbsp;");
         out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
         out.println("</td>");

         out.println("<td align=\"center\">");
         out.println("<font color=\"ffffff\" size=\"5\">Member Tee Time Request</font>");
         out.println("</font></td>");

         out.println("<td align=\"center\" width=\"300\">");
         out.println("<font size=\"1\" color=\"#ffffff\">Copyright&nbsp;</font>");
         out.println("<font size=\"2\" color=\"#ffffff\">&#169;&nbsp;</font>");
         out.println("<font size=\"1\" color=\"#ffffff\">ForeTees, LLC <br> " +yy+ " All rights reserved.");
         out.println("</font><font size=\"3\">");
         out.println("<br><br><a href=\"/" +rev+ "/member_help.htm\" target=\"_blank\"><b>Help</b></a>");
         out.println("</font></td>");
         out.println("</tr></table>");

         out.println("<br>");

         out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"620\" align=\"center\">");
         out.println("<font size=\"3\">");
         if (club.equals( "oldoaks" )) {
            out.println("<b>Tee Time Registration</b><br></font>");
         } else if (!lotteryText.equals("")) {
            out.println("<b>" +lotteryText+ " Registration</b><br></font>");       // use replacement text
         } else {
            out.println("<b>Lottery Registration</b><br></font>");
         }
         out.println("<font size=\"2\">");
         out.println("Provide the requested information below and click on 'Continue With Request' to contnue.");
         out.println("<br>OR click on 'Cancel Request' to delete the request. To return without changes click on 'Go Back'.");

         out.println("<br><br><b>NOTE:</b> Only the person that originates the request will be allowed to cancel it or change these values.");

         out.println("</font></td></tr>");
         out.println("</table>");

         out.println("<font size=\"2\"><br><br>");
         out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b>");
         if (!course.equals( "" )) {
           out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>");
         }
         out.println("</font>");

         out.println("<table border=\"0\" align=\"center\" cellpadding=\"5\" cellspacing=\"5\">"); // table to contain 2 tables below

         out.println("<tr>");
         out.println("<form action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\" name=\"reqForm\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + sdate + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
               out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lottName + "\">");
               out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
               out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
         out.println("<td align=\"center\" valign=\"top\">");

         out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" align=\"center\" width=\"100%\">");  // table for request details
         out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
            out.println("<font color=\"ffffff\" size=\"2\">");
            if (club.equals( "oldoaks" )) {
               out.println("<b>Request Details</b>");
            } else if (!lotteryText.equals("")) {
               out.println("<b>" +lotteryText+ " Details</b>");    // use replacement text
            } else {
               out.println("<b>Lottery Request Details</b>");
            }
         out.println("</font></td></tr>");

         out.println("<tr><td align=\"left\">");
         out.println("<font size=\"2\"><br>");

         out.println(" &nbsp;&nbsp;Time and Tee Requested:&nbsp;&nbsp;");
         
         
      } else {
         
         //
         //  Mobile user - output the top of the page
         //
         out.println(SystemUtils.HeadTitleMobile("Foretees Tee Time Request"));
         out.println(SystemUtils.BannerMobileSlot());                              // banner w/o Home and Logout links

         out.println("<div class=\"content\">");
         out.println("<div class=\"headertext\">");                          // output the heading         
         if (!lotteryText.equals("")) {
            out.println("<b>" +lotteryText+ " Registration</b><br>");       // use replacement text
         } else {
            out.println("<b>Tee Time Registration</b><br>");
         }
         out.println("</div>");         
         
         out.println("<div class=\"smheadertext\">");     
         out.println("Complete the form below and click 'Continue'.");
         if (lottid > 0) {
            out.println(" OR click 'Cancel Request' to delete the request.");
         }
         out.println(" To return without changes click on 'Go Back'.");
         out.println("</div>");

         out.println("<div class=\"smheadertext\">");                                
         out.println("Date:&nbsp;&nbsp;" + day_name + ", " + mm + "/" + dd + "/" + yy);
         if (!course.equals( "" )) {
           out.println("<BR>Course:&nbsp;&nbsp;" + course);
         }
         out.println("</div>");

         out.println("<form action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\" name=\"reqForm\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + sdate + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
               out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lottName + "\">");
               out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
               out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
               out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");

         out.println("<table cellpadding=\"0\" cellspacing=\"1\" border=\"1\" id=\"trequest\">");
         out.println("<tr class=\"tableheader\"><td>");

            if (!lotteryText.equals("")) {
               out.println("&nbsp;&nbsp;<strong>" +lotteryText+ " Details");    // use replacement text
            } else {
               out.println("&nbsp;&nbsp;<strong>Request Details");
            }
         out.println("</td></tr>");

         out.println("<tr class=\"tablerow\"><td>");
         out.println("<BR>Time Requested:&nbsp;&nbsp;");
         
      }      // end of IF mobile user

      
      
      if (lottid == 0 || user.equalsIgnoreCase( orig_by )) {  // if new request OR user originated the request

         out.println("<select size=\"1\" name=\"ltime\">");                    // ltime parm (time and f/b)
         //
         //  Find all the matching available tee times for this lottery
         //
         try {
            PreparedStatement pstmtd1 = con.prepareStatement (
               "SELECT hr, min, fb " +
               "FROM teecurr2 WHERE date = ? AND event = '' AND lottery = ? AND courseName = ? AND fb < 2 AND blocker = '' " +
               "ORDER BY time, fb");

            pstmtd1.clearParameters();          // clear the parms
            pstmtd1.setLong(1, date);
            pstmtd1.setString(2, lottName);
            pstmtd1.setString(3, course);

            rs = pstmtd1.executeQuery();      // find all matching lottery times

            while (rs.next()) {

              dhr  = rs.getInt(1);
              dmin  = rs.getInt(2);
              dfb  = rs.getInt(3);

              if (dfb < 2) {          // not a cross-over time

                 dampm = "AM";
                 if (dhr == 12) {
                    dampm = "PM";
                 }
                 if (dhr > 12) {
                    dhr = dhr - 12;
                    dampm = "PM";
                 }
                 if (dfb == 0) {
                    dsfb = " Front";
                 } else {
                    dsfb = " Back";
                 }

                 if (bln_hideFrontBack) dsfb = ""; // Hide Front/Back text in select box

                 if ((dhr == hr) && (dmin == min) && (dfb == fb) && (dampm.equals( ampm ))) {
                     out.println("<option selected value=\"" + dhr + ":" + dmin + " " + dampm + " " + dfb + "\">" + dhr + ":" + SystemUtils.ensureDoubleDigit(dmin) + " " + dampm + " " + dsfb + "</option>");
                 } else {
                     out.println("<option value=\"" + dhr + ":" + dmin + " " + dampm + " " + dfb + "\">" + dhr + ":" + SystemUtils.ensureDoubleDigit(dmin) + " " + dampm + " " + dsfb + "</option>");
                 }

              }
            }                  // end of while
            pstmtd1.close();

            out.println("</select>");

         }
         catch (Exception e1) {

            dbError(out, e1);
            return;
         }

      } else {              // not new and not originator

         if (fb == 0) {
            dsfb = " Front";
            if (mobile > 0) dsfb = " F";
         } else {
            dsfb = " Back";
            if (mobile > 0) dsfb = " B";
         }

         out.println(hr + ":" + SystemUtils.ensureDoubleDigit(min) + " " + ampm + " " + dsfb);

         out.println("<input type=\"hidden\" name=\"ltime\" value=\"" +shr+ ":" +smin+ " " +ampm+ " " +sfb+ "\">");
      }

      out.println("&nbsp;&nbsp;<br><br>");

      if (courseCount > 0 && courseL.equals( "-ALL-" )) {     // if multiple courses supported for this club and lottery

         out.println("&nbsp;&nbsp;Preferred Course:&nbsp;&nbsp;");

         if (lottid == 0 || user.equalsIgnoreCase( orig_by )) {  // if new request OR user originated the request

            out.println("&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"course\" onChange=\"courseChange()\">");

            for (i=0; i < courseA.size(); i++) {

               courseName = courseA.get(i);      // get first course name from array

               if (courseName.equals( course )) {
                  out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
               } else {
                  out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
               }
            }
            out.println("</select>");

         } else {              // not new and not originator

            out.println("&nbsp;&nbsp;" +course);
            out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
         }
         out.println("&nbsp;&nbsp;<br><br>");

         if (club.equals( "martindowns" ) || club.equals( "sugarmill" )) {    // if Martin Downs

            out.println("<input type=\"hidden\" name=\"checkothers\" value=\"1\">");

         } else {

            if (mobile == 0) {
               if (courseCount > 2) {
                  out.println("&nbsp;&nbsp;Try other courses if times not available?:&nbsp;&nbsp;");
               } else {
                  out.println("&nbsp;&nbsp;Try the other course if times not available?:&nbsp;&nbsp;");
               }
            } else {
               if (courseCount > 2) {
                  out.println("&nbsp;&nbsp;Try other courses?:&nbsp;&nbsp;");
               } else {
                  out.println("&nbsp;&nbsp;Try the other course?:&nbsp;&nbsp;");
               }
            }

            if (lottid == 0 || user.equalsIgnoreCase( orig_by )) {  // if new request OR user originated the request

               out.println("<select size=\"1\" name=\"checkothers\">");

               if (mins_before == 999) {                             // if new request (default to Yes)
                     out.println("<option value=\"0\">No</option>");
                     out.println("<option selected value=\"1\">Yes</option>");
               } else {
                  if (checkothers == 0) {
                     out.println("<option selected value=\"0\">No</option>");
                     out.println("<option value=\"1\">Yes</option>");
                  } else {
                     out.println("<option value=\"0\">No</option>");
                     out.println("<option selected value=\"1\">Yes</option>");
                  }
               }
               out.println("</select>");

            } else {              // not new and not originator

               if (checkothers == 0) {
                  out.println("No");
                  out.println("<input type=\"hidden\" name=\"checkothers\" value=\"0\">");
               } else {
                  out.println("Yes");
                  out.println("<input type=\"hidden\" name=\"checkothers\" value=\"1\">");
               }
            }
            out.println("&nbsp;&nbsp;<br><br>");
         }

      } else {

         out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
         out.println("<input type=\"hidden\" name=\"checkothers\" value=\"0\">");
      }

      if (slots > 1) {                // if lottery allows more than 1 consecutive tee time

         if (mobile == 0) {
            out.println("&nbsp;&nbsp;Number of consecutive tee times you wish to request:&nbsp;&nbsp;");
         } else {
            out.println("Consecutive times:&nbsp;&nbsp;");
         }

         if (lottid == 0 || user.equalsIgnoreCase( orig_by )) {  // if new request OR user originated the request

            out.println("<select size=\"1\" name=\"slots\">");
            if ((!reload && groups < 2) || (reload && slotSelection == 1)) {
                out.println("<option selected value=\"01\">1</option>");
            } else {
                out.println("<option value=\"01\">1</option>");
            }
            if ((!reload && groups == 2) || (reload && slotSelection == 2)) {
                out.println("<option selected value=\"02\">2</option>");
            } else {
                out.println("<option value=\"02\">2</option>");
            }

            if (slots > 2) {
                if ((!reload && groups == 3) || (reload && slotSelection == 3)) {
                    out.println("<option selected value=\"03\">3</option>");
                } else {
                    out.println("<option value=\"03\">3</option>");
                }
            }
            if (slots > 3) {
                if ((!reload && groups == 4) || (reload && slotSelection == 4)) {
                    out.println("<option selected value=\"04\">4</option>");
                } else {
                    out.println("<option value=\"04\">4</option>");
                }
            }
            if (slots > 4) {
                if ((!reload && groups == 5) || (reload && slotSelection == 5)) {
                    out.println("<option selected value=\"05\">5</option>");
                } else {
                    out.println("<option value=\"05\">5</option>");
                }
            }
            out.println("</select>");

         } else {
            out.println(slots);
            out.println("<input type=\"hidden\" name=\"slots\" value=\"" + groups + "\">");   // limit request to original # of groups requested
           // out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
         }
         out.println("&nbsp;&nbsp;<br><br>");
      } else {
         out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
      }




      if (skipmins == false && allowmins == 0) {     // skip this if club does not want these options (if true) 
                                                     // 0 means Allow in allowmins!!! (from lottery config)

         if (mins_before == 999) {          // if new request (default to 120)

            mins_before = minsbefore;       // use default from lottery config

            if (club.equals( "missionviejo" )) { 
               mins_before = 240;            // 4 hours  - remove this after changing their configs !!
            }
         }

         if (mobile == 0) {
            out.println("&nbsp;&nbsp;Number of hours/minutes <b>before</b> this time you will accept:&nbsp;&nbsp;");
         } else {
            out.println("&nbsp;&nbsp;Hrs/mins before you will accept:&nbsp;&nbsp;");
         }

         if (lottid == 0 || user.equalsIgnoreCase( orig_by )) {  // if new request OR user originated the request

            out.println("<select size=\"1\" name=\"mins_before\">");
            if (mins_before == 0) {
               out.println("<option selected value=\"0\">None</option>");
            } else {
               out.println("<option value=\"0\">None</option>");
            }
            if (mins_before == 10) {
               out.println("<option selected value=\"10\">10 mins</option>");
            } else {
               out.println("<option value=\"10\">10 mins</option>");
            }
            if (mins_before == 20) {
               out.println("<option selected value=\"20\">20 mins</option>");
            } else {
               out.println("<option value=\"20\">20 mins</option>");
            }
            if (mins_before == 30) {
               out.println("<option selected value=\"30\">30 mins</option>");
            } else {
               out.println("<option value=\"30\">30 mins</option>");
            }
            if (mins_before == 40) {
               out.println("<option selected value=\"40\">40 mins</option>");
            } else {
               out.println("<option value=\"40\">40 mins</option>");
            }
            if (mins_before == 50) {
               out.println("<option selected value=\"50\">50 mins</option>");
            } else {
               out.println("<option value=\"50\">50 mins</option>");
            }
            if (mins_before == 60) {
               out.println("<option selected value=\"60\">1 hr</option>");
            } else {
               out.println("<option value=\"60\">1 hr</option>");
            }
            if (mins_before == 75) {
               out.println("<option selected value=\"75\">1 hr 15 mins</option>");
            } else {
               out.println("<option value=\"75\">1 hr 15 mins</option>");
            }
            if (mins_before == 90) {
               out.println("<option selected value=\"90\">1 hr 30 mins</option>");
            } else {
               out.println("<option value=\"90\">1 hr 30 mins</option>");
            }
            if (mins_before == 120) {
               out.println("<option selected value=\"120\">2 hrs</option>");
            } else {
               out.println("<option value=\"120\">2 hrs</option>");
            }
            if (mins_before == 150) {
               out.println("<option selected value=\"150\">2 hrs 30 mins</option>");
            } else {
               out.println("<option value=\"150\">2 hrs 30 mins</option>");
            }
            if (mins_before == 180) {
               out.println("<option selected value=\"180\">3 hrs</option>");
            } else {
               out.println("<option value=\"180\">3 hrs</option>");
            }
            if (mins_before == 210) {
               out.println("<option selected value=\"210\">3 hrs 30 mins</option>");
            } else {
               out.println("<option value=\"210\">3 hrs 30 mins</option>");
            }
            if (mins_before == 240) {
               out.println("<option selected value=\"240\">4 hrs</option>");
            } else {
               out.println("<option value=\"240\">4 hrs</option>");
            }
            if (mins_before == 300) {
               out.println("<option selected value=\"300\">5 hrs</option>");
            } else {
               out.println("<option value=\"300\">5 hrs</option>");
            }
            if (mins_before == 360) {
               out.println("<option selected value=\"360\">6 hrs</option>");
            } else {
               out.println("<option value=\"360\">6 hrs</option>");
            }
            out.println("</select>");

         } else {
            out.println(mins_before);
            out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" + mins_before + "\">");
         }
         out.println("&nbsp;&nbsp;<br><br>");


         if (mins_after == 999) {                  // if new request (default to 120)

            mins_after = minsafter;                // use default from lottery config

            if (club.equals( "westchester" )) {             // CHANGE THESE in lottery3, then remove !!!!!!!!!!!!!
               mins_after = 180;            // 3 hours
            }
            if (club.equals( "missionviejo" )) {
               mins_after = 360;            // 6 hours
            }
         }


         if (mobile == 0) {
            out.println("&nbsp;&nbsp;Number of hours/minutes <b>after</b> this time you will accept:&nbsp;&nbsp;");
         } else {
            out.println("&nbsp;&nbsp;Hrs/mins after you will accept:&nbsp;&nbsp;");
         }

         if (lottid == 0 || user.equalsIgnoreCase( orig_by )) {  // if new request OR user originated the request

            out.println("<select size=\"1\" name=\"mins_after\">");
            if (mins_after == 0) {
               out.println("<option selected value=\"0\">None</option>");
            } else {
               out.println("<option value=\"0\">None</option>");
            }
            if (mins_after == 10) {
               out.println("<option selected value=\"10\">10 mins</option>");
            } else {
               out.println("<option value=\"10\">10 mins</option>");
            }
            if (mins_after == 20) {
               out.println("<option selected value=\"20\">20 mins</option>");
            } else {
               out.println("<option value=\"20\">20 mins</option>");
            }
            if (mins_after == 30) {
               out.println("<option selected value=\"30\">30 mins</option>");
            } else {
               out.println("<option value=\"30\">30 mins</option>");
            }
            if (mins_after == 40) {
               out.println("<option selected value=\"40\">40 mins</option>");
            } else {
               out.println("<option value=\"40\">40 mins</option>");
            }
            if (mins_after == 50) {
               out.println("<option selected value=\"50\">50 mins</option>");
            } else {
               out.println("<option value=\"50\">50 mins</option>");
            }
            if (mins_after == 60) {
               out.println("<option selected value=\"60\">1 hr</option>");
            } else {
               out.println("<option value=\"60\">1 hr</option>");
            }
            if (mins_after == 75) {
               out.println("<option selected value=\"75\">1 hr 15 mins</option>");
            } else {
               out.println("<option value=\"75\">1 hr 15 mins</option>");
            }
            if (mins_after == 90) {
               out.println("<option selected value=\"90\">1 hr 30 mins</option>");
            } else {
               out.println("<option value=\"90\">1 hr 30 mins</option>");
            }
            if (mins_after == 120) {
               out.println("<option selected value=\"120\">2 hrs</option>");
            } else {
               out.println("<option value=\"120\">2 hrs</option>");
            }
            if (mins_after == 150) {
               out.println("<option selected value=\"150\">2 hrs 30 mins</option>");
            } else {
               out.println("<option value=\"150\">2 hrs 30 mins</option>");
            }
            if (mins_after == 180) {
               out.println("<option selected value=\"180\">3 hrs</option>");
            } else {
               out.println("<option value=\"180\">3 hrs</option>");
            }
            if (mins_after == 210) {
               out.println("<option selected value=\"210\">3 hrs 30 mins</option>");
            } else {
               out.println("<option value=\"210\">3 hrs 30 mins</option>");
            }
            if (mins_after == 240) {
               out.println("<option selected value=\"240\">4 hrs</option>");
            } else {
               out.println("<option value=\"240\">4 hrs</option>");
            }
            if (mins_after == 300) {
               out.println("<option selected value=\"300\">5 hrs</option>");
            } else {
               out.println("<option value=\"300\">5 hrs</option>");
            }
            if (mins_after == 360) {
               out.println("<option selected value=\"360\">6 hrs</option>");
            } else {
               out.println("<option value=\"360\">6 hrs</option>");
            }
            out.println("</select>");

         } else {
            out.println(mins_after);
            out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" + mins_after + "\">");
         }
         out.println("&nbsp;&nbsp;<br><br>");

      } else {    // club does not allow mins before and after

         if (allowmins == 1) {        // if lottery config says DO NOT allow mins before & after

               out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" +minsbefore+ "\">");    // use config values
               out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" +minsafter+ "\">");               

         } else {             // do customs umtil we change their lottery3 tables !!! - then remove this

            //
            //  Cherry Hills & Rancho Bernardo & Lakewood, etc. - do not allow them to specify the mins before or after
            //
            if (club.equals( "denvercc" )) {

               out.println("<input type=\"hidden\" name=\"mins_before\" value=\"480\">");    // hard code to 8 hours (always)
               out.println("<input type=\"hidden\" name=\"mins_after\" value=\"480\">");

            } else if (club.equals( "pecanplantation" )) {

               out.println("<input type=\"hidden\" name=\"mins_before\" value=\"30\">");    // hard code to 8 hours (always)
               out.println("<input type=\"hidden\" name=\"mins_after\" value=\"480\">");

            } else if (club.equals( "burlington" ) || club.equals( "brantford" ) || club.equals("bonniebriar") || 
                       club.equals("tamarack") || club.equals("marbellacc") || club.equals("mnvalleycc") ) {

               out.println("<input type=\"hidden\" name=\"mins_before\" value=\"120\">");    // hard code to 2 hours (always)
               out.println("<input type=\"hidden\" name=\"mins_after\" value=\"120\">");

            } else if (club.equals( "mirasolcc") ) {

               out.println("<input type=\"hidden\" name=\"mins_before\" value=\"180\">");    // hard code to 3 hours (always)
               out.println("<input type=\"hidden\" name=\"mins_after\" value=\"180\">");

            } else if (club.equals( "sugarmill") ) {

               out.println("<input type=\"hidden\" name=\"mins_before\" value=\"240\">");    // hard code to 4 hours (always)
               out.println("<input type=\"hidden\" name=\"mins_after\" value=\"240\">");

            } else {

               out.println("<input type=\"hidden\" name=\"mins_before\" value=\"360\">");    // hard code to 6 hours (always)
               out.println("<input type=\"hidden\" name=\"mins_after\" value=\"360\">");
            }
         }
      }

      if (mobile == 0) {
         out.println("&nbsp;&nbsp;<b>Note:</b> Tee times to be awarded within the boundaries established by your golf professionals.<br></font>");
         out.println("</td></tr>");
         out.println("<tr><td align=\"center\">");
         out.println("<font size=\"2\"><br>");
      } else {         
         out.println("</td></tr>");
         out.println("<tr class=\"tablerow\"><td><br>");
      }

      if (lottid > 0 && user.equalsIgnoreCase( orig_by )) {  // if old request AND user originated the request

         out.println("<input type=submit value=\"Cancel Request\" name=\"remove\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      }
      out.println("<input type=submit value=\"Continue With Request\" name=\"continue\"><br><br>");
      out.println("</td></tr>");
      out.println("</table>");
         
      if (mobile == 0) {   
         out.println("</td></form>");
         out.println("</tr><tr>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
      } else {           
         out.println("</form>");
      }

      if (lottid > 0) {

         out.println("<form action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\" name=\"can\">");
         out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
         out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
         out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
         out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
         out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
         out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");

      } else {            // go directly back to caller (must have been call from tee sheet)

         if (mobile == 0) {
            
            out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
            if (!returnCourse.equals( "" )) {
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
            } else {
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            }
            
         } else {
            
            out.println("<form action=\"/" +rev+ "/servlet/Member_sheet\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
         }
      }
         
      if (mobile == 0) {
         out.println("Return w/o Changes:<br>");
         out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");

         out.println("</font></td>");
         out.println("</tr>");

         out.println("</table>");
         out.println("</td>");
         out.println("</tr>");
         out.println("</table>");
         out.println("</font></center>");
      } else {         
         out.println("<div class=\"smheadertext\">Return w/o Changes: <input type=\"submit\" value=\"Go Back\" name=\"cancel\">");     
         out.println("</form></div></div>");
      }
      out.println("</body></html>");
      out.close();
      return;                 // exit and wait for reply
   }



   //*****************************************************************
   //  NOT the first call to this class
   //*****************************************************************
   //  
   if (lottid > 0) {          // if request already exists - get orig_by, etc for later

      try {

         PreparedStatement pstmt = con.prepareStatement (
            "SELECT in_use, in_use_by, orig_by " +
            "FROM lreqs3 WHERE id = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, lottid);         // put the parm in pstmt
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            in_use = rs.getInt(1);
            in_use_by = rs.getString(2);
            orig_by = rs.getString(3);
         }

         pstmt.close();

      }
      catch (Exception ignore) {
      }
   }
   
   
   if ((req.getParameter("return") != null) || (req.getParameter("memNotice") != null)) {   // if this is a return from verify - time = hhmm

      try {
         time = Integer.parseInt(stime);
         date = Integer.parseInt(sdate);
         fb = Integer.parseInt(sfb);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      //
      //  create a time string for display
      //
      hr = time / 100;
      min = time - (hr * 100);

      ampm = "AM";

      if (hr > 11) {

         ampm = "PM";

         if (hr > 12) {

            hr = hr - 12;
         }
      }
      stime = hr + ":" + SystemUtils.ensureDoubleDigit(min) + " " + ampm;

   } else {

      if (!stime.equals( "" )) {           // if call from sheet or teelist or search
         //
         //  Parse the time parm to separate hh, mm, am/pm and convert to military time
         //  (received as 'hh:mm xx'   where xx = am or pm)
         //
         StringTokenizer tok = new StringTokenizer( stime, ": " );     // space is the default token

         shr = tok.nextToken();
         smin = tok.nextToken();
         ampm = tok.nextToken();

         //
         //  Convert the values from string to int
         //
         try {
            date = Long.parseLong(sdate);
            fb = Integer.parseInt(sfb);
            hr = Integer.parseInt(shr);
            min = Integer.parseInt(smin);
         }
         catch (NumberFormatException e) {
            // ignore error
         }

         if (ampm.equalsIgnoreCase ( "PM" )) {

            if (hr != 12) {

               hr = hr + 12;
            }
         }

         time = hr * 100;
         time = time + min;          // military time
           
      } else {                     // call is from self (lottery info prompt above) - time is in ltime
        
         //
         //  Parse the time parm to separate hh, mm, am/pm and convert to military time
         //  (received as 'hh:mm xx fb'   where xx = am or pm, and fb = 0 for front and 1 for back)
         //
         StringTokenizer tok = new StringTokenizer( ltime, ": " );     // space is the default token

         shr = tok.nextToken();
         smin = tok.nextToken();
         ampm = tok.nextToken();
         sfb = tok.nextToken();

         try {
            lmin = Integer.parseInt(smin);
         }
         catch (NumberFormatException e) {
         }

         stime = shr + ":" + SystemUtils.ensureDoubleDigit(lmin) + " " + ampm;          // create stime value


         if (lottid > 0) {          // if request already exists
            
            //
            //  Check if this request is still 'in use' and still in use by this user??
            //
            //  This is necessary because the user may have gone away while holding this req.  If the
            //  slot timed out (system timer), the slot would be marked 'not in use' and another
            //  user could pick it up.  The original holder could be trying to use it now.
            //
            if ((in_use == 0) || (!in_use_by.equalsIgnoreCase( user ))) {    // if time slot in use and not by this user

               if (mobile == 0) {         // if NOT mobile user

                  out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
                  out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                  out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                  out.println("<CENTER><BR><BR><H3>Reservation Timer Expired</H3>");
                  out.println("<BR><BR>Sorry, but this request has been returned to the system.<BR>");
                  out.println("<BR>The system timed out and released the request.");
                  out.println("<BR><BR>");

                  if (index.equals( "999" )) {         // if came from Member_teelist

                     out.println("<font size=\"2\">");
                     out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain.htm\">");
                     out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                     out.println("</form></font>");

                  } else {

                     if (index.equals( "995" )) {         // if came from Member_teelist_list

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
                           out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
                           if (!returnCourse.equals( "" )) {
                              out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
                           } else {
                              out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                           }
                           out.println("</form></font>");
                        }
                     }
                  }
                  out.println("</CENTER>");

               } else {

                  //
                  //  Mobile user
                  //
                  out.println(SystemUtils.HeadTitleMobile("Foretees Request List"));
                  out.println(SystemUtils.BannerMobile());

                  out.println("<div class=\"content\">");
                  out.println("<div class=\"headertext\">");    // output the heading
                     out.println("Reservation Timer Expired");
                  out.println("</div>");

                  out.println("<div class=\"smheadertext\">Sorry, the request has timed out.<BR>Please try again.</div>");     

                  out.println("<ul>");

                  if (index.equals( "995" )) {         // if came from Member_teelist_list

                     out.println("<li>");
                     out.println("<form action=\"/" +rev+ "/servlet/Member_teelist_mobile\" method=\"get\">");
                     out.println("<input type=\"submit\" value=\"Return to List\"></form>");
                     out.println("</li>");

                  } else {

                     out.println("<li>");
                     out.println("<form action=\"/" +rev+ "/servlet/Member_sheet\" method=\"post\">");
                     out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                     out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                     out.println("<input type=\"submit\" value=\"Return To Tee Sheet\"></form>");
                     out.println("</li>");
                  }

                  out.println("</ul></div>");
               }
               out.println("</body></html>");                 
               out.close();
               return;
            }
         }
           
         //
         //  Convert the values from string to int
         //
         try {
            date = Long.parseLong(sdate);
            fb = Integer.parseInt(sfb);
            hr = Integer.parseInt(shr);
            min = Integer.parseInt(smin);
         }
         catch (NumberFormatException e) {
            // ignore error
         }

         if (ampm.equalsIgnoreCase ( "PM" )) {

            if (hr != 12) {

               hr = hr + 12;
            }
         }

         time = hr * 100;
         time = time + min;          // military time
           
         //
         //   Check for Member Notice from Pro
         //
         String memNotice = verifySlot.checkMemNotice(date, time, fb, course, day_name, "teetime", false, con);    
  
         if (!memNotice.equals( "" )) {      // if message to display
           
            //
            //  Display the Pro's Message and then prompt the user to either accept or return to the tee sheet
            //
            if (mobile == 0) {                     // if NOT a mobile user
               
               out.println("<HTML><HEAD>");
               out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
               out.println("<Title>Member Notice For Tee Time Request</Title>");
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

                     if (lottid > 0) {

                        out.println("<form action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\" name=\"can\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                        out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                        out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                        out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                        out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");

                     } else {            // go directly back to caller

                        if (index.equals( "999" )) {         // if came from Member_teelist

                           out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain.htm\">");

                        } else {

                           if (index.equals( "995" )) {         // if came from Member_teelist_list

                              out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain2.htm\">");

                           } else {

                              if (index.equals( "888" )) {       // if from Member_searchmem

                                 out.println("<form method=\"get\" action=\"/" +rev+ "/member_searchmem.htm\">");

                              } else {                                // return to Member_sheet - must rebuild frames first

                                 out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
                                 out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                                 out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                                 out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                              }
                           }
                        }
                     }
                     out.println("<input type=\"submit\" value=\"NO - Return\" name=\"cancel\"></form>");

                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                        out.println("<form action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\">");
                        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                        out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lottName + "\">");
                        out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
                        out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                        out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                        out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
                        out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
                        out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                        out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
                        out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
                        out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" + mins_before + "\">");
                        out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" + mins_after + "\">");
                        out.println("<input type=\"hidden\" name=\"checkothers\" value=\"" + checkothers + "\">");
                        out.println("<input type=\"hidden\" name=\"memNotice\" value=\"yes\">");
                        out.println("<input type=\"submit\" value=\"YES - Continue\"></form>");
                     out.println("</font></td></tr>");
                     out.println("</table>");

                     out.println("</td>");
                     out.println("</tr>");
                  out.println("</table>");
               out.println("</font></center></body></html>");
               
            } else {
               
               //
               //  Mobile user
               //
               out.println(SystemUtils.HeadTitleMobile("Foretees Member Notice"));
               out.println(SystemUtils.BannerMobile());

               out.println("<div class=\"content\">");
               out.println("<div class=\"headertext\">");    // output the heading
                  out.println("NOTICE FROM YOUR GOLF SHOP");
               out.println("</div>");

               out.println("<div class=\"smheadertext\">" +memNotice+ "</div>");     

               out.println("<div>Would you like to continue with this request?</div>");
                     
               out.println("<ul><li>");

               if (lottid > 0) {

                  out.println("<form action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\" name=\"can\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                  out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                  out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                  out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                  out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");

               } else {            // go directly back to caller

                  if (index.equals( "995" )) {         // if came from Member_teelist_list

                     out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_teelist_mobile\">");

                  } else {

                     out.println("<form action=\"/" +rev+ "/servlet/Member_sheet\" method=\"post\">");
                     out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                     out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                     out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                  }
               }
               out.println("<input type=\"submit\" value=\"NO - Return\" name=\"cancel\"></form></li>");
                     
               out.println("<li><form action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
               out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lottName + "\">");
               out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
               out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
               out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
               out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" + mins_before + "\">");
               out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" + mins_after + "\">");
               out.println("<input type=\"hidden\" name=\"checkothers\" value=\"" + checkothers + "\">");
               out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
               out.println("<input type=\"hidden\" name=\"memNotice\" value=\"yes\">");
               out.println("<input type=\"submit\" value=\"YES - Continue\"></form></li>");
                     
               out.println("</ul></div>");
               out.println("</body></html>");                 
            }
            out.close();
            return;
         }
      }
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
   //  if this is a call from self (a return of some type)
   //
   if ((req.getParameter("letter") != null) || (req.getParameter("return") != null)) {

      player1 = req.getParameter("player1").trim();
      player2 = req.getParameter("player2").trim();
      player3 = req.getParameter("player3").trim();
      player4 = req.getParameter("player4").trim();
      if (req.getParameter("player5") != null) {
         player5 = req.getParameter("player5").trim();
      }
      if (req.getParameter("player6") != null) {
         player6 = req.getParameter("player6").trim();
      }
      if (req.getParameter("player7") != null) {
         player7 = req.getParameter("player7").trim();
      }
      if (req.getParameter("player8") != null) {
         player8 = req.getParameter("player8").trim();
      }
      if (req.getParameter("player9") != null) {
         player9 = req.getParameter("player9").trim();
      }
      if (req.getParameter("player10") != null) {
         player10 = req.getParameter("player10").trim();
      }
      if (req.getParameter("player11") != null) {
         player11 = req.getParameter("player11").trim();
      }
      if (req.getParameter("player12") != null) {
         player12 = req.getParameter("player12").trim();
      }
      if (req.getParameter("player13") != null) {
         player13 = req.getParameter("player13").trim();
      }
      if (req.getParameter("player14") != null) {
         player14 = req.getParameter("player14").trim();
      }
      if (req.getParameter("player15") != null) {
         player15 = req.getParameter("player15").trim();
      }
      if (req.getParameter("player16") != null) {
         player16 = req.getParameter("player16").trim();
      }
      if (req.getParameter("player17") != null) {
         player17 = req.getParameter("player17").trim();
      }
      if (req.getParameter("player18") != null) {
         player18 = req.getParameter("player18").trim();
      }
      if (req.getParameter("player19") != null) {
         player19 = req.getParameter("player19").trim();
      }
      if (req.getParameter("player20") != null) {
         player20 = req.getParameter("player20").trim();
      }
      if (req.getParameter("player21") != null) {
         player21 = req.getParameter("player21").trim();
      }
      if (req.getParameter("player22") != null) {
         player22 = req.getParameter("player22").trim();
      }
      if (req.getParameter("player23") != null) {
         player23 = req.getParameter("player23").trim();
      }
      if (req.getParameter("player24") != null) {
         player24 = req.getParameter("player24").trim();
      }
      if (req.getParameter("player25") != null) {
         player25 = req.getParameter("player25").trim();
      }
      if (req.getParameter("p1cw") != null) {
         p1cw = req.getParameter("p1cw");
      }
      if (req.getParameter("p2cw") != null) {
         p2cw = req.getParameter("p2cw");
      }
      if (req.getParameter("p3cw") != null) {
         p3cw = req.getParameter("p3cw");
      }
      if (req.getParameter("p4cw") != null) {
         p4cw = req.getParameter("p4cw");
      }
      if (req.getParameter("p5cw") != null) {
         p5cw = req.getParameter("p5cw");
      }
      if (req.getParameter("p6cw") != null) {
         p6cw = req.getParameter("p6cw");
      }
      if (req.getParameter("p7cw") != null) {
         p7cw = req.getParameter("p7cw");
      }
      if (req.getParameter("p8cw") != null) {
         p8cw = req.getParameter("p8cw");
      }
      if (req.getParameter("p9cw") != null) {
         p9cw = req.getParameter("p9cw");
      }
      if (req.getParameter("p10cw") != null) {
         p10cw = req.getParameter("p10cw");
      }
      if (req.getParameter("p11cw") != null) {
         p11cw = req.getParameter("p11cw");
      }
      if (req.getParameter("p12cw") != null) {
         p12cw = req.getParameter("p12cw");
      }
      if (req.getParameter("p13cw") != null) {
         p13cw = req.getParameter("p13cw");
      }
      if (req.getParameter("p14cw") != null) {
         p14cw = req.getParameter("p14cw");
      }
      if (req.getParameter("p15cw") != null) {
         p15cw = req.getParameter("p15cw");
      }
      if (req.getParameter("p16cw") != null) {
         p16cw = req.getParameter("p16cw");
      }
      if (req.getParameter("p17cw") != null) {
         p17cw = req.getParameter("p17cw");
      }
      if (req.getParameter("p18cw") != null) {
         p18cw = req.getParameter("p18cw");
      }
      if (req.getParameter("p19cw") != null) {
         p19cw = req.getParameter("p19cw");
      }
      if (req.getParameter("p20cw") != null) {
         p20cw = req.getParameter("p20cw");
      }
      if (req.getParameter("p21cw") != null) {
         p21cw = req.getParameter("p21cw");
      }
      if (req.getParameter("p22cw") != null) {
         p22cw = req.getParameter("p22cw");
      }
      if (req.getParameter("p23cw") != null) {
         p23cw = req.getParameter("p23cw");
      }
      if (req.getParameter("p24cw") != null) {
         p24cw = req.getParameter("p24cw");
      }
      if (req.getParameter("p25cw") != null) {
         p25cw = req.getParameter("p25cw");
      }
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
      if (req.getParameter("p96") != null) {
         p9s = req.getParameter("p96");
         p96 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p97") != null) {
         p9s = req.getParameter("p97");
         p97 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p98") != null) {
         p9s = req.getParameter("p98");
         p98 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p99") != null) {
         p9s = req.getParameter("p99");
         p99 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p910") != null) {
         p9s = req.getParameter("p910");
         p910 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p911") != null) {
         p9s = req.getParameter("p911");
         p911 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p912") != null) {
         p9s = req.getParameter("p912");
         p912 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p913") != null) {
         p9s = req.getParameter("p913");
         p913 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p914") != null) {
         p9s = req.getParameter("p914");
         p914 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p915") != null) {
         p9s = req.getParameter("p915");
         p915 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p916") != null) {
         p9s = req.getParameter("p916");
         p916 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p917") != null) {
         p9s = req.getParameter("p917");
         p917 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p918") != null) {
         p9s = req.getParameter("p918");
         p918 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p919") != null) {
         p9s = req.getParameter("p919");
         p919 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p920") != null) {
         p9s = req.getParameter("p920");
         p920 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p921") != null) {
         p9s = req.getParameter("p921");
         p921 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p922") != null) {
         p9s = req.getParameter("p922");
         p922 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p923") != null) {
         p9s = req.getParameter("p923");
         p923 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p924") != null) {
         p9s = req.getParameter("p924");
         p924 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p925") != null) {
         p9s = req.getParameter("p925");
         p925 = Integer.parseInt(p9s);
      }

      guest_id1 = (req.getParameter("guest_id1") != null ? Integer.parseInt(req.getParameter("guest_id1")) : 0);
      guest_id2 = (req.getParameter("guest_id2") != null ? Integer.parseInt(req.getParameter("guest_id2")) : 0);
      guest_id3 = (req.getParameter("guest_id3") != null ? Integer.parseInt(req.getParameter("guest_id3")) : 0);
      guest_id4 = (req.getParameter("guest_id4") != null ? Integer.parseInt(req.getParameter("guest_id4")) : 0);
      guest_id5 = (req.getParameter("guest_id5") != null ? Integer.parseInt(req.getParameter("guest_id5")) : 0);
      guest_id6 = (req.getParameter("guest_id6") != null ? Integer.parseInt(req.getParameter("guest_id6")) : 0);
      guest_id7 = (req.getParameter("guest_id7") != null ? Integer.parseInt(req.getParameter("guest_id7")) : 0);
      guest_id8 = (req.getParameter("guest_id8") != null ? Integer.parseInt(req.getParameter("guest_id8")) : 0);
      guest_id9 = (req.getParameter("guest_id9") != null ? Integer.parseInt(req.getParameter("guest_id9")) : 0);
      guest_id10 = (req.getParameter("guest_id10") != null ? Integer.parseInt(req.getParameter("guest_id10")) : 0);
      guest_id11 = (req.getParameter("guest_id11") != null ? Integer.parseInt(req.getParameter("guest_id11")) : 0);
      guest_id12 = (req.getParameter("guest_id12") != null ? Integer.parseInt(req.getParameter("guest_id12")) : 0);
      guest_id13 = (req.getParameter("guest_id13") != null ? Integer.parseInt(req.getParameter("guest_id13")) : 0);
      guest_id14 = (req.getParameter("guest_id14") != null ? Integer.parseInt(req.getParameter("guest_id14")) : 0);
      guest_id15 = (req.getParameter("guest_id15") != null ? Integer.parseInt(req.getParameter("guest_id15")) : 0);
      guest_id16 = (req.getParameter("guest_id16") != null ? Integer.parseInt(req.getParameter("guest_id16")) : 0);
      guest_id17 = (req.getParameter("guest_id17") != null ? Integer.parseInt(req.getParameter("guest_id17")) : 0);
      guest_id18 = (req.getParameter("guest_id18") != null ? Integer.parseInt(req.getParameter("guest_id18")) : 0);
      guest_id19 = (req.getParameter("guest_id19") != null ? Integer.parseInt(req.getParameter("guest_id19")) : 0);
      guest_id20 = (req.getParameter("guest_id20") != null ? Integer.parseInt(req.getParameter("guest_id20")) : 0);
      guest_id21 = (req.getParameter("guest_id21") != null ? Integer.parseInt(req.getParameter("guest_id21")) : 0);
      guest_id22 = (req.getParameter("guest_id22") != null ? Integer.parseInt(req.getParameter("guest_id22")) : 0);
      guest_id23 = (req.getParameter("guest_id23") != null ? Integer.parseInt(req.getParameter("guest_id23")) : 0);
      guest_id24 = (req.getParameter("guest_id24") != null ? Integer.parseInt(req.getParameter("guest_id24")) : 0);
      guest_id25 = (req.getParameter("guest_id25") != null ? Integer.parseInt(req.getParameter("guest_id25")) : 0);

      //
      //  Convert hide from string to int
      //
      try {
         hide = Integer.parseInt(hides);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

   } else {        // not a letter request

      //
      //  if existing lottery request, get the players, etc.
      //
      if (lottid > 0) {

         try {

            PreparedStatement pstmt = con.prepareStatement (
               "SELECT player1, player2, player3, player4, player5, player6, player7, player8, " +
               "player9, player10, player11, player12, player13, player14, player15, player16, player17, " +
               "player18, player19, player20, player21, player22, player23, player24, player25, " +
               "p1cw, p2cw, p3cw, p4cw, p5cw, p6cw, p7cw, p8cw, " +
               "p9cw, p10cw, p11cw, p12cw, p13cw, p14cw, p15cw, p16cw, p17cw, " +
               "p18cw, p19cw, p20cw, p21cw, p22cw, p23cw, p24cw, p25cw, " +
               "notes, hideNotes, " +
               "p91, p92, p93, p94, p95, p96, p97, p98, " +
               "p99, p910, p911, p912, p913, p914, p915, p916, p917, " +
               "p918, p919, p920, p921, p922, p923, p924, p925, " +
               "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5, " +
               "guest_id6, guest_id7, guest_id8, guest_id9, guest_id10, " +
               "guest_id11, guest_id12, guest_id13, guest_id14, guest_id15, " +
               "guest_id16, guest_id17, guest_id18, guest_id19, guest_id20, " +
               "guest_id21, guest_id22, guest_id23, guest_id24, guest_id25, orig_by " +
               "FROM lreqs3 WHERE id = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, lottid);         // put the parm in pstmt
            rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               player1 = rs.getString(1);
               player2 = rs.getString(2);
               player3 = rs.getString(3);
               player4 = rs.getString(4);
               player5 = rs.getString(5);
               player6 = rs.getString(6);
               player7 = rs.getString(7);
               player8 = rs.getString(8);
               player9 = rs.getString(9);
               player10 = rs.getString(10);
               player11 = rs.getString(11);
               player12 = rs.getString(12);
               player13 = rs.getString(13);
               player14 = rs.getString(14);
               player15 = rs.getString(15);
               player16 = rs.getString(16);
               player17 = rs.getString(17);
               player18 = rs.getString(18);
               player19 = rs.getString(19);
               player20 = rs.getString(20);
               player21 = rs.getString(21);
               player22 = rs.getString(22);
               player23 = rs.getString(23);
               player24 = rs.getString(24);
               player25 = rs.getString(25);
               p1cw = rs.getString(26);
               p2cw = rs.getString(27);
               p3cw = rs.getString(28);
               p4cw = rs.getString(29);
               p5cw = rs.getString(30);
               p6cw = rs.getString(31);
               p7cw = rs.getString(32);
               p8cw = rs.getString(33);
               p9cw = rs.getString(34);
               p10cw = rs.getString(35);
               p11cw = rs.getString(36);
               p12cw = rs.getString(37);
               p13cw = rs.getString(38);
               p14cw = rs.getString(39);
               p15cw = rs.getString(40);
               p16cw = rs.getString(41);
               p17cw = rs.getString(42);
               p18cw = rs.getString(43);
               p19cw = rs.getString(44);
               p20cw = rs.getString(45);
               p21cw = rs.getString(46);
               p22cw = rs.getString(47);
               p23cw = rs.getString(48);
               p24cw = rs.getString(49);
               p25cw = rs.getString(50);
               notes = rs.getString(51);
               hide = rs.getInt(52);
               p91 = rs.getInt(53);
               p92 = rs.getInt(54);
               p93 = rs.getInt(55);
               p94 = rs.getInt(56);
               p95 = rs.getInt(57);
               p96 = rs.getInt(58);
               p97 = rs.getInt(59);
               p98 = rs.getInt(60);
               p99 = rs.getInt(61);
               p910 = rs.getInt(62);
               p911 = rs.getInt(63);
               p912 = rs.getInt(64);
               p913 = rs.getInt(65);
               p914 = rs.getInt(66);
               p915 = rs.getInt(67);
               p916 = rs.getInt(68);
               p917 = rs.getInt(69);
               p918 = rs.getInt(70);
               p919 = rs.getInt(71);
               p920 = rs.getInt(72);
               p921 = rs.getInt(73);
               p922 = rs.getInt(74);
               p923 = rs.getInt(75);
               p924 = rs.getInt(76);
               p925 = rs.getInt(77);
               guest_id1 = rs.getInt("guest_id1");
               guest_id2 = rs.getInt("guest_id2");
               guest_id3 = rs.getInt("guest_id3");
               guest_id4 = rs.getInt("guest_id4");
               guest_id5 = rs.getInt("guest_id5");
               guest_id6 = rs.getInt("guest_id6");
               guest_id7 = rs.getInt("guest_id7");
               guest_id8 = rs.getInt("guest_id8");
               guest_id9 = rs.getInt("guest_id9");
               guest_id10 = rs.getInt("guest_id10");
               guest_id11 = rs.getInt("guest_id11");
               guest_id12 = rs.getInt("guest_id12");
               guest_id13 = rs.getInt("guest_id13");
               guest_id14 = rs.getInt("guest_id14");
               guest_id15 = rs.getInt("guest_id15");
               guest_id16 = rs.getInt("guest_id16");
               guest_id17 = rs.getInt("guest_id17");
               guest_id18 = rs.getInt("guest_id18");
               guest_id19 = rs.getInt("guest_id19");
               guest_id20 = rs.getInt("guest_id20");
               guest_id21 = rs.getInt("guest_id21");
               guest_id22 = rs.getInt("guest_id22");
               guest_id23 = rs.getInt("guest_id23");
               guest_id24 = rs.getInt("guest_id24");
               guest_id25 = rs.getInt("guest_id25");
               orig_by = rs.getString("orig_by");
            }
            pstmt.close();
         }
         catch (Exception e1) {
            dbError(out, e1);                                                     
            return;
         }
      }
   }              // end of 'letter' if

   if (club.equals( "cherryhills" )) {        // if cherry hills, no defualt c/w 
     
      pcw = "";
        
   } else {
     
      //
      //  get user's walk/cart preference
      //
      try {

         PreparedStatement pstmt2 = con.prepareStatement (
                  "SELECT wc FROM member2b WHERE username = ?");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.setString(1, user);       // put the parm in pstmt2
         rs = pstmt2.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            pcw = rs.getString(1);        // user's walk/cart pref
         }
         pstmt2.close();              // close the stmt

      }
      catch (Exception e9) {

         dbError(out, e9);
         return;
      }
   }

   //
   //  Get the walk/cart options available
   //
   try {

      getParms.getCourse(con, parmc, course);  // get course parms
      
      getClub.getParms(con, parm);             // get the club parms

   }
   catch (Exception e1) {

      dbError(out, e1);
      return;
   }

   //
   //  Make sure the user's c/w option is still supported (pro may have changed config)
   //
   if (!club.equals( "cherryhills" )) {        // if cherry hills, no defualt c/w

      i = 0;
      loopi1:
      while (i < 16) {

         if (parmc.tmodea[i].equals( pcw )) {

            break loopi1;
         }
         i++;
      }
      if (i > 15) {       // if we went all the way without a match

         pcw = parmc.tmodea[0];    // default to first option
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

                  if ((p5.equals( "Yes" )) && (player5.equals(""))) {

                     player5 = name;
                     p5cw = pcw;

                  }
               }
            }
         }
      }
   }
   
   
   boolean blockP1 = false;           // flags for the erase player option
   boolean blockP2 = false;     
   boolean blockP3 = false;     
   boolean blockP4 = false;     
   boolean blockP5 = false;     
   boolean blockP6 = false;     
   boolean blockP7 = false;     
   boolean blockP8 = false;     
   boolean blockP9 = false;     
   boolean blockP10 = false;     
   boolean blockP11 = false;          
   boolean blockP12 = false;     
   boolean blockP13 = false;     
   boolean blockP14 = false;     
   boolean blockP15 = false;     
   boolean blockP16 = false;     
   boolean blockP17 = false;     
   boolean blockP18 = false;     
   boolean blockP19 = false;     
   boolean blockP20 = false;     
   boolean blockP21 = false;          
   boolean blockP22 = false;     
   boolean blockP23 = false;     
   boolean blockP24 = false;     
   boolean blockP25 = false;     
   
   //
   //  Custom to only allow the originator of the request to cancel it or remove players
   //
   if (club.equals("braeburncc")) {  

      if (lottid > 0 && !orig_by.equals("") && !user.equalsIgnoreCase(orig_by)) {   // if existing lottery request and NOT the originator

         if (!player1.equals("")) {
            blockP1 = true;        // not originator - do not allow erase on any player
         }
         if (!player2.equals("")) {
            blockP2 = true;      
         }
         if (!player3.equals("")) {
            blockP3 = true;      
         }
         if (!player4.equals("")) {
            blockP4 = true;      
         }
         if (!player5.equals("")) {
            blockP5 = true;      
         }
         if (!player6.equals("")) {
            blockP6 = true;      
         }
         if (!player7.equals("")) {
            blockP7 = true;      
         }
         if (!player8.equals("")) {
            blockP8 = true;      
         }
         if (!player9.equals("")) {
            blockP9 = true;      
         }
         if (!player10.equals("")) {
            blockP10 = true;      
         }
         if (!player11.equals("")) {
            blockP11 = true;       
         }
         if (!player12.equals("")) {
            blockP12 = true;      
         }
         if (!player13.equals("")) {
            blockP13 = true;      
         }
         if (!player14.equals("")) {
            blockP14 = true;      
         }
         if (!player15.equals("")) {
            blockP15 = true;      
         }
         if (!player16.equals("")) {
            blockP16 = true;      
         }
         if (!player17.equals("")) {
            blockP17 = true;      
         }
         if (!player18.equals("")) {
            blockP18 = true;      
         }
         if (!player19.equals("")) {
            blockP19 = true;      
         }
         if (!player20.equals("")) {
            blockP20 = true;      
         }
         if (!player21.equals("")) {
            blockP21 = true;       
         }
         if (!player22.equals("")) {
            blockP22 = true;      
         }
         if (!player23.equals("")) {
            blockP23 = true;      
         }
         if (!player24.equals("")) {
            blockP24 = true;      
         }
         if (!player25.equals("")) {
            blockP25 = true;  
         }
      }
   }
   
   
   //
   //  If mobile user, call common processing (shares with Member_slot)
   //
   if (mobile > 0) {
   
      parmMobile parmM = new parmMobile();              // allocate a parm block to hold parameters for mobile processing
      
      // populate parmM with current values
      
      parmM.type = "lottery";            // indicate this is a lottery request
      parmM.mobile = mobile;  
      parmM.user = user;
      parmM.day = day_name;
      parmM.time = time;
      parmM.stime = stime;
      parmM.mm = (int)mm;
      parmM.dd = (int)dd;
      parmM.yy = (int)yy;
      parmM.club = club;
      parmM.course = course;
      parmM.date = date;
      parmM.sdate = sdate;
      parmM.fb = fb;
      parmM.index = index;
      parmM.returnCourse = returnCourse;
      parmM.p5 = p5;
      parmM.lottName = lottName;
      parmM.lstate = lstate;
      parmM.lottid = lottid;
      parmM.slots = slots;                // # of groups requested
      parmM.players = players;            // # of players allowed in this request (4 or 5 per group times # of slots selected)
      parmM.mins_before = mins_before;
      parmM.mins_after = mins_after;
      parmM.checkothers = checkothers;
      parmM.notes = notes;
      parmM.hide = hide;
      parmM.displayOpt = displayOpt;
         
      parmM.playerA[0] = player1;              // save player info in parmM
      parmM.playerA[1] = player2;           
      parmM.playerA[2] = player3;           
      parmM.playerA[3] = player4;           
      parmM.playerA[4] = player5;           
      parmM.playerA[5] = player6;           
      parmM.playerA[6] = player7;           
      parmM.playerA[7] = player8;           
      parmM.playerA[8] = player9;           
      parmM.playerA[9] = player10;           
      parmM.playerA[10] = player11;           
      parmM.playerA[11] = player12;           
      parmM.playerA[12] = player13;           
      parmM.playerA[13] = player14;           
      parmM.playerA[14] = player15;           
      parmM.playerA[15] = player16;           
      parmM.playerA[16] = player17;           
      parmM.playerA[17] = player18;           
      parmM.playerA[18] = player19;           
      parmM.playerA[19] = player20;           
      parmM.playerA[20] = player21;           
      parmM.playerA[21] = player22;           
      parmM.playerA[22] = player23;           
      parmM.playerA[23] = player24;           
      parmM.playerA[24] = player25;           
      parmM.pcwA[0] = p1cw;
      parmM.pcwA[1] = p2cw;
      parmM.pcwA[2] = p3cw;
      parmM.pcwA[3] = p4cw;
      parmM.pcwA[4] = p5cw;
      parmM.pcwA[5] = p6cw;
      parmM.pcwA[6] = p7cw;
      parmM.pcwA[7] = p8cw;
      parmM.pcwA[8] = p9cw;
      parmM.pcwA[9] = p10cw;
      parmM.pcwA[10] = p11cw;
      parmM.pcwA[11] = p12cw;
      parmM.pcwA[12] = p13cw;
      parmM.pcwA[13] = p14cw;
      parmM.pcwA[14] = p15cw;
      parmM.pcwA[15] = p16cw;
      parmM.pcwA[16] = p17cw;
      parmM.pcwA[17] = p18cw;
      parmM.pcwA[18] = p19cw;
      parmM.pcwA[19] = p20cw;
      parmM.pcwA[20] = p21cw;
      parmM.pcwA[21] = p22cw;
      parmM.pcwA[22] = p23cw;
      parmM.pcwA[23] = p24cw;
      parmM.pcwA[24] = p25cw;
      parmM.p9A[0] = p91;
      parmM.p9A[1] = p92;
      parmM.p9A[2] = p93;
      parmM.p9A[3] = p94;
      parmM.p9A[4] = p95;
      parmM.p9A[5] = p96;
      parmM.p9A[6] = p97;
      parmM.p9A[7] = p98;
      parmM.p9A[8] = p99;
      parmM.p9A[9] = p910;
      parmM.p9A[10] = p911;
      parmM.p9A[11] = p912;
      parmM.p9A[12] = p913;
      parmM.p9A[13] = p914;
      parmM.p9A[14] = p915;
      parmM.p9A[15] = p916;
      parmM.p9A[16] = p917;
      parmM.p9A[17] = p918;
      parmM.p9A[18] = p919;
      parmM.p9A[19] = p920;
      parmM.p9A[20] = p921;
      parmM.p9A[21] = p922;
      parmM.p9A[22] = p923;
      parmM.p9A[23] = p924;
      parmM.p9A[24] = p925;
      parmM.blockPA[0] = blockP1;     // set the custom player 'block erase' options
      parmM.blockPA[1] = blockP2;
      parmM.blockPA[2] = blockP3;
      parmM.blockPA[3] = blockP4;
      parmM.blockPA[4] = blockP5;
      parmM.blockPA[5] = blockP6;
      parmM.blockPA[6] = blockP7;
      parmM.blockPA[7] = blockP8;
      parmM.blockPA[8] = blockP9;
      parmM.blockPA[9] = blockP10;
      parmM.blockPA[10] = blockP11;
      parmM.blockPA[11] = blockP12;
      parmM.blockPA[12] = blockP13;
      parmM.blockPA[13] = blockP14;
      parmM.blockPA[14] = blockP15;
      parmM.blockPA[15] = blockP16;
      parmM.blockPA[16] = blockP17;
      parmM.blockPA[17] = blockP18;
      parmM.blockPA[18] = blockP19;
      parmM.blockPA[19] = blockP20;
      parmM.blockPA[20] = blockP21;   
      parmM.blockPA[21] = blockP22;
      parmM.blockPA[22] = blockP23;
      parmM.blockPA[23] = blockP24;
      parmM.blockPA[24] = blockP25;
      
      Common_mobile.doSlot(parm, parmc, parmM, out, con);    // prompt user for players
      
      return;                                           // exit and wait for user input
   }
   

   //
   //*****************************************************************************
   //  NOT a Mobile user - Build the HTML page to prompt user for names
   //*****************************************************************************
   //
   out.println("<HTML>");
   out.println("<HEAD>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
   if (club.equals( "oldoaks" )) {
      out.println("<Title>Member Tee Time Request Page</Title>");
   } else if (!lotteryText.equals("")) {
      out.println("<Title>Member " +lotteryText+ " Page</Title>");
   } else {
      out.println("<Title>Member Lottery Registration Page</Title>");
   }

   //  Add script code to allow modal windows to be used
   out.println("<!-- ******** BEGIN LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->" +
           "<script type=\"text/javascript\">var lwmwLinkedBy=\"LiknoWebModalWindows [1]\",lwmwName=\"foretees-modal\",lwmwBN=\"128\";awmAltUrl=\"\";</script>" +
           "<script charset=\"UTF-8\" src=\"/" + rev + "/web%20utilities/foretees-modal.js\" type=\"text/javascript\"></script>" +
           "<!-- ******** END LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->");

   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("function resizeIFrame(divHeight, iframeName) {");
   out.println("document.getElementById(iframeName).style.height = divHeight;");
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
   out.println(" document.forms['playerform'].letter.value = x;");         // put the letter in the parm
   out.println(" document.forms['playerform'].submit();");        // submit the form
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script

   //
   //*******************************************************************
   //  Erase player name (erase button selected next to player's name)
   //
   //    Remove the player's name and shift any other names up starting at player1
   //*******************************************************************
   //

   out.println("<script type=\"text/javascript\">");            // Erase name script    (Note:  Put these in file???)  what other files use these scripts, just proshop_slot?
   out.println("<!--");

   out.println("function erasename(pPlayerPos, pCWoption) {");
       out.println("var p = eval(\"document.forms['playerform'].\" + pPlayerPos + \";\")");
       out.println("var o = eval(\"document.forms['playerform'].\" + pCWoption + \";\")");
       out.println("p.value = '';");        // clear player field

       out.println("var pPlayerPos2 = pPlayerPos.replace('player', 'guest_id');");
       out.println("document.playerform[pPlayerPos2].value = '0';");
       
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
   //  Erase text area - (Notes)
   //*******************************************************************
   //
   out.println("<script type=\"text/javascript\">");            // Erase text area script
   out.println("<!--");
   out.println(" function erasetext(pos1) {");
   out.println(" document.playerform[pos1].value = '';");           // clear the text field
   out.println("}"); 
   
   out.println("function movenotes() {");
   out.println(" var oldnotes = document.forms['playerform'].oldnotes.value;");
   out.println(" document.forms['playerform'].notes.value = oldnotes;");   // put notes in text area
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
   out.println("<!--");
   out.println("function add(e, wc) {");
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
   //*******************************************************************
   //  Move a member name into the tee slot
   //*******************************************************************
   //
   out.println("<script type=\"text/javascript\">");            // Move name script
   out.println("<!--");

   out.println("function movename(namewc) {");

   out.println("del = ':';");                               // deliminator is a colon
   out.println("array = namewc.split(del);");                 // split string into 2 pieces (name, wc)
   out.println("var name = array[0];");
   out.println("var wc = array[1];");
   out.println("var skip = 0;");
   out.println("var f = document.forms['playerform'];");

   out.println("var player1 = f.player1.value;");
   out.println("var player2 = f.player2.value;");
   out.println("var player3 = f.player3.value;");
   out.println("var player4 = f.player4.value;");

   if (players > 4) {
      out.println("var player5 = f.player5.value;");
   } else {
      out.println("var player5 = '';");
   }
   if (players > 5) {
      out.println("var player6 = f.player6.value;");
      out.println("var player7 = f.player7.value;");
      out.println("var player8 = f.player8.value;");
   } else {
      out.println("var player6 = '';");
      out.println("var player7 = '';");
      out.println("var player8 = '';");
   }
   if (players > 8) {
      out.println("var player9 = f.player9.value;");
      out.println("var player10 = f.player10.value;");
   } else {
      out.println("var player9 = '';");
      out.println("var player10 = '';");
   }
   if (players > 10) {
      out.println("var player11 = f.player11.value;");
      out.println("var player12 = f.player12.value;");
   } else {
      out.println("var player11 = '';");
      out.println("var player12 = '';");
   }
   if (players > 12) {
      out.println("var player13 = f.player13.value;");
      out.println("var player14 = f.player14.value;");
      out.println("var player15 = f.player15.value;");
   } else {
      out.println("var player13 = '';");
      out.println("var player14 = '';");
      out.println("var player15 = '';");
   }
   if (players > 15) {
      out.println("var player16 = f.player16.value;");
   } else {
      out.println("var player16 = '';");
   }
   if (players > 16) {
      out.println("var player17 = f.player17.value;");
      out.println("var player18 = f.player18.value;");
      out.println("var player19 = f.player19.value;");
      out.println("var player20 = f.player20.value;");
   } else {
      out.println("var player17 = '';");
      out.println("var player18 = '';");
      out.println("var player19 = '';");
      out.println("var player20 = '';");
   }
   if (players > 20) {
      out.println("var player21 = f.player21.value;");
      out.println("var player22 = f.player22.value;");
      out.println("var player23 = f.player23.value;");
      out.println("var player24 = f.player24.value;");
      out.println("var player25 = f.player25.value;");
   } else {
      out.println("var player21 = '';");
      out.println("var player22 = '';");
      out.println("var player23 = '';");
      out.println("var player24 = '';");
      out.println("var player25 = '';");
   }

   out.println("if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4) || ");
   out.println("    ( name == player5) || ( name == player6) || ( name == player7) || ( name == player8) || ");
   out.println("    ( name == player9) || ( name == player10) || ( name == player11) || ( name == player12) || ");
   out.println("    ( name == player13) || ( name == player14) || ( name == player15) || ( name == player16) || ");
   out.println("    ( name == player17) || ( name == player18) || ( name == player19) || ( name == player20) || ");
   out.println("    ( name == player21) || ( name == player22) || ( name == player23) || ( name == player24) || ");
   out.println("    ( name == player25)) {");
      out.println("skip = 1;");
   out.println("}");

   out.println("if (skip == 0) {");

      out.println("if (player1 == '') {");                    // if player1 is empty
         out.println("f.player1.value = name;");
         out.println("f.guest_id1.value = '0';");
         out.println("add(f.p1cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p1cw.value = wc;");
      out.println("} else {");

      out.println("if (player2 == '') {");                    // if player2 is empty
         out.println("f.player2.value = name;");
         out.println("f.guest_id2.value = '0';");
         out.println("add(f.p2cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p2cw.value = wc;");
      out.println("} else {");

      out.println("if (player3 == '') {");                    // if player3 is empty
         out.println("f.player3.value = name;");
         out.println("f.guest_id3.value = '0';");
         out.println("add(f.p3cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p3cw.value = wc;");
      out.println("} else {");

      out.println("if (player4 == '') {");                    // if player4 is empty
         out.println("f.player4.value = name;");
         out.println("f.guest_id4.value = '0';");
         out.println("add(f.p4cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p4cw.value = wc;");

    if (players > 4) {
      out.println("} else {");
      out.println("if (player5 == '') {");                    // if player5 is empty
         out.println("f.player5.value = name;");
         out.println("f.guest_id5.value = '0';");
         out.println("add(f.p5cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p5cw.value = wc;");
    }
    if (players > 5) {
      out.println("} else {");
      out.println("if (player6 == '') {");                    // if player6 is empty
         out.println("f.player6.value = name;");
         out.println("f.guest_id6.value = '0';");
         out.println("add(f.p6cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p6cw.value = wc;");
      out.println("} else {");
      out.println("if (player7 == '') {");                    // if player7 is empty
         out.println("f.player7.value = name;");
         out.println("f.guest_id7.value = '0';");
         out.println("add(f.p7cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p7cw.value = wc;");
      out.println("} else {");
      out.println("if (player8 == '') {");                    // if player8 is empty
         out.println("f.player8.value = name;");
         out.println("f.guest_id8.value = '0';");
         out.println("add(f.p8cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p8cw.value = wc;");
    }
    if (players > 8) {
      out.println("} else {");
      out.println("if (player9 == '') {");                    // if player9 is empty
         out.println("f.player9.value = name;");
         out.println("f.guest_id9.value = '0';");
         out.println("add(f.p9cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p9cw.value = wc;");
      out.println("} else {");
      out.println("if (player10 == '') {");                    // if player10 is empty
         out.println("f.player10.value = name;");
         out.println("f.guest_id10.value = '0';");
         out.println("add(f.p10cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p10cw.value = wc;");
    }
    if (players > 10) {
      out.println("} else {");
      out.println("if (player11 == '') {");                    // if player11 is empty
         out.println("f.player11.value = name;");
         out.println("f.guest_id11.value = '0';");
         out.println("add(f.p11cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p11cw.value = wc;");
      out.println("} else {");
      out.println("if (player12 == '') {");                    // if player12 is empty
         out.println("f.player12.value = name;");
         out.println("f.guest_id12.value = '0';");
         out.println("add(f.p12cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p12cw.value = wc;");
    }
    if (players > 12) {
      out.println("} else {");
      out.println("if (player13 == '') {");                    // if player13 is empty
         out.println("f.player13.value = name;");
         out.println("f.guest_id13.value = '0';");
         out.println("add(f.p13cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p13cw.value = wc;");
      out.println("} else {");
      out.println("if (player14 == '') {");                    // if player14 is empty
         out.println("f.player14.value = name;");
         out.println("f.guest_id14.value = '0';");
         out.println("add(f.p14cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p14cw.value = wc;");
      out.println("} else {");
      out.println("if (player15 == '') {");                    // if player15 is empty
         out.println("f.player15.value = name;");
         out.println("f.guest_id15.value = '0';");
         out.println("add(f.p15cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p15cw.value = wc;");
    }
    if (players > 15) {
      out.println("} else {");
      out.println("if (player16 == '') {");                    // if player16 is empty
         out.println("f.player16.value = name;");
         out.println("f.guest_id16.value = '0';");
         out.println("add(f.p16cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p16cw.value = wc;");
    }
    if (players > 16) {
      out.println("} else {");
      out.println("if (player17 == '') {");                    // if player17 is empty
         out.println("f.player17.value = name;");
         out.println("f.guest_id17.value = '0';");
         out.println("add(f.p17cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p17cw.value = wc;");
      out.println("} else {");
      out.println("if (player18 == '') {");                    // if player18 is empty
         out.println("f.player18.value = name;");
         out.println("f.guest_id18.value = '0';");
         out.println("add(f.p18cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p18cw.value = wc;");
      out.println("} else {");
      out.println("if (player19 == '') {");                    // if player19 is empty
         out.println("f.player19.value = name;");
         out.println("f.guest_id19.value = '0';");
         out.println("add(f.p19cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p19cw.value = wc;");
      out.println("} else {");
      out.println("if (player20 == '') {");                    // if player20 is empty
         out.println("f.player20.value = name;");
         out.println("f.guest_id20.value = '0';");
         out.println("add(f.p20cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p20cw.value = wc;");
    }
    if (players > 20) {
      out.println("} else {");
      out.println("if (player21 == '') {");                    // if player21 is empty
         out.println("f.player21.value = name;");
         out.println("f.guest_id21.value = '0';");
         out.println("add(f.p21cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p21cw.value = wc;");
      out.println("} else {");
      out.println("if (player22 == '') {");                    // if player22 is empty
         out.println("f.player22.value = name;");
         out.println("f.guest_id22.value = '0';");
         out.println("add(f.p22cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p22cw.value = wc;");
      out.println("} else {");
      out.println("if (player23 == '') {");                    // if player23 is empty
         out.println("f.player23.value = name;");
         out.println("f.guest_id23.value = '0';");
         out.println("add(f.p23cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p23cw.value = wc;");
      out.println("} else {");
      out.println("if (player24 == '') {");                    // if player24 is empty
         out.println("f.player24.value = name;");
         out.println("f.guest_id24.value = '0';");
         out.println("add(f.p24cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p24cw.value = wc;");
      out.println("} else {");
      out.println("if (player25 == '') {");                    // if player25 is empty
         out.println("f.player25.value = name;");
         out.println("f.guest_id25.value = '0';");
         out.println("add(f.p25cw, wc);");                    // add wc option if Pro Only and player default
         out.println("f.p25cw.value = wc;");

      out.println("}");         // p25
      out.println("}");         // p24
      out.println("}");         // p23
      out.println("}");         // p22
      out.println("}");         // p21
      out.println("}");         // p20
      out.println("}");         // p19
      out.println("}");         // p18
      out.println("}");         // p17
      out.println("}");         // p16
      out.println("}");         // p15
      out.println("}");         // p14
      out.println("}");         // p13
      out.println("}");         // p12
      out.println("}");         // p11
      out.println("}");         // p10
      out.println("}");         // p9
      out.println("}");         // p8
      out.println("}");         // p7
      out.println("}");         // p6
      out.println("}");         // p5
    } else {
       if (players > 16) {
         out.println("}");         // p20
         out.println("}");         // p19
         out.println("}");         // p18
         out.println("}");         // p17
         out.println("}");         // p16
         out.println("}");         // p15
         out.println("}");         // p14
         out.println("}");         // p13
         out.println("}");         // p12
         out.println("}");         // p11
         out.println("}");         // p10
         out.println("}");         // p9
         out.println("}");         // p8
         out.println("}");         // p7
         out.println("}");         // p6
         out.println("}");         // p5
       } else {
          if (players > 15) {
            out.println("}");         // p16
            out.println("}");         // p15
            out.println("}");         // p14
            out.println("}");         // p13
            out.println("}");         // p12
            out.println("}");         // p11
            out.println("}");         // p10
            out.println("}");         // p9
            out.println("}");         // p8
            out.println("}");         // p7
            out.println("}");         // p6
            out.println("}");         // p5
          } else {
             if (players > 12) {
               out.println("}");         // p15
               out.println("}");         // p14
               out.println("}");         // p13
               out.println("}");         // p12
               out.println("}");         // p11
               out.println("}");         // p10
               out.println("}");         // p9
               out.println("}");         // p8
               out.println("}");         // p7
               out.println("}");         // p6
               out.println("}");         // p5
             } else {
                if (players > 10) {
                  out.println("}");         // p12
                  out.println("}");         // p11
                  out.println("}");         // p10
                  out.println("}");         // p9
                  out.println("}");         // p8
                  out.println("}");         // p7
                  out.println("}");         // p6
                  out.println("}");         // p5
                } else {
                   if (players > 8) {
                     out.println("}");         // p10
                     out.println("}");         // p9
                     out.println("}");         // p8
                     out.println("}");         // p7
                     out.println("}");         // p6
                     out.println("}");         // p5
                   } else {
                      if (players > 5) {
                        out.println("}");         // p8
                        out.println("}");         // p7
                        out.println("}");         // p6
                        out.println("}");         // p5
                      } else {
                         if (players > 4) {
                           out.println("}");         // p5

                         }
                      }
                   }
                }
             }
          }
       }
    }
    out.println("}");         // p4
    out.println("}");         // p3
    out.println("}");         // p2
    out.println("}");         // p1

   out.println("}");                  // end of dup name chack (if skip = 0)

   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");                               // End of script


   //
   //*******************************************************************
   //  Move a Guest Name into the tee slot
   //*******************************************************************
   //
   out.println("<script type=\"text/javascript\">");            // Move Guest Name script
   out.println("<!--");

   out.println("var guestid_slot;");
   out.println("var player_slot;");

   out.println("function moveguest(namewc) {");

   //out.println("var name = namewc;");

   out.println("array = namewc.split('|');"); // split string (partner_name, partner_id)
   out.println("var name = array[0];");
   out.println("var use_guestdb = array[1]");
   
   out.println("var f = document.forms['playerform'];");

   out.println("var player1 = f.player1.value;");
   out.println("var player2 = f.player2.value;");
   out.println("var player3 = f.player3.value;");
   out.println("var player4 = f.player4.value;");

   if (players > 4) {
      out.println("var player5 = f.player5.value;");
   } else {
      out.println("var player5 = '';");
   }
   if (players > 5) {
      out.println("var player6 = f.player6.value;");
      out.println("var player7 = f.player7.value;");
      out.println("var player8 = f.player8.value;");
   } else {
      out.println("var player6 = '';");
      out.println("var player7 = '';");
      out.println("var player8 = '';");
   }
   if (players > 8) {
      out.println("var player9 = f.player9.value;");
      out.println("var player10 = f.player10.value;");
   } else {
      out.println("var player9 = '';");
      out.println("var player10 = '';");
   }
   if (players > 10) {
      out.println("var player11 = f.player11.value;");
      out.println("var player12 = f.player12.value;");
   } else {
      out.println("var player11 = '';");
      out.println("var player12 = '';");
   }
   if (players > 12) {
      out.println("var player13 = f.player13.value;");
      out.println("var player14 = f.player14.value;");
      out.println("var player15 = f.player15.value;");
   } else {
      out.println("var player13 = '';");
      out.println("var player14 = '';");
      out.println("var player15 = '';");
   }
   if (players > 15) {
      out.println("var player16 = f.player16.value;");
   } else {
      out.println("var player16 = '';");
   }
   if (players > 16) {
      out.println("var player17 = f.player17.value;");
      out.println("var player18 = f.player18.value;");
      out.println("var player19 = f.player19.value;");
      out.println("var player20 = f.player20.value;");
   } else {
      out.println("var player17 = '';");
      out.println("var player18 = '';");
      out.println("var player19 = '';");
      out.println("var player20 = '';");
   }
   if (players > 20) {
      out.println("var player21 = f.player21.value;");
      out.println("var player22 = f.player22.value;");
      out.println("var player23 = f.player23.value;");
      out.println("var player24 = f.player24.value;");
      out.println("var player25 = f.player25.value;");
   } else {
      out.println("var player21 = '';");
      out.println("var player22 = '';");
      out.println("var player23 = '';");
      out.println("var player24 = '';");
      out.println("var player25 = '';");
   }

   out.println("var defCW = '';");
   if (club.equals( "ranchobernardo" )) {
      out.println("defCW = 'CCH';");       // set default Mode of Trans
   }
   if (club.equals("blackdiamondranch")) {
      out.println("defCW = 'CCT';");
   }
   if (club.equals("braeburncc")) {
      out.println("defCW = 'NAP';");
   }

   // If guest tracking is turned on and in use for this guest type and at least one player slot is open, display the modal window
   out.println("if (use_guestdb == 1 && (player1 == '' || player2 == '' || player3 == '' || player4 == ''" +
           (players > 4 ? " || player5 == ''" : "") +
           (players > 5 ? " || player6 == '' || player7 == '' || player8 == ''" : "") +
           (players > 8 ? " || player9 == '' || player10 == ''" : "") +
           (players > 10 ? " || player11 == '' || player12 == ''" : "") +
           (players > 12 ? " || player13 == '' || player14 == '' || player15 == ''" : "") +
           (players > 15 ? " || player16 == ''" : "") +
           (players > 16 ? " || player17 == '' || player18 == '' || player19 == '' || player20 == ''" : "") +
           (players > 20 ? " || player21 == '' || player22 == '' || player23 == '' || player24 == '' || player25 == ''" : "") +
           ")) {");
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
            out.println("f.player1.focus();"); // here for IE compat
            out.println("f.player1.value = name + spc;");
            out.println("f.player1.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p1cw.value = defCW;");
         out.println("}");
   out.println("} else {");

   out.println("if (player2 == '') {");                    // if player2 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player2;");
            out.println("guestid_slot = f.guest_id2;");
            out.println("f.player2.value = name + spc;");
         out.println("} else {");
            out.println("f.player2.focus();"); // here for IE compat
            out.println("f.player2.value = name + spc;");
            out.println("f.player2.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p2cw.value = defCW;");
         out.println("}");
   out.println("} else {");

   out.println("if (player3 == '') {");                    // if player3 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player3;");
            out.println("guestid_slot = f.guest_id3;");
            out.println("f.player3.value = name + spc;");
         out.println("} else {");
            out.println("f.player3.focus();"); // here for IE compat
            out.println("f.player3.value = name + spc;");
            out.println("f.player3.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p3cw.value = defCW;");
         out.println("}");
   out.println("} else {");

   out.println("if (player4 == '') {");                    // if player4 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player4;");
            out.println("guestid_slot = f.guest_id4;");
            out.println("f.player4.value = name + spc;");
         out.println("} else {");
            out.println("f.player4.focus();"); // here for IE compat
            out.println("f.player4.value = name + spc;");
            out.println("f.player4.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p4cw.value = defCW;");
         out.println("}");

   if (players > 4) {
     out.println("} else {");
     out.println("if (player5 == '') {");                    // if player5 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player5;");
            out.println("guestid_slot = f.guest_id5;");
            out.println("f.player5.value = name + spc;");
         out.println("} else {");
            out.println("f.player5.focus();"); // here for IE compat
            out.println("f.player5.value = name + spc;");
            out.println("f.player5.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p5cw.value = defCW;");
         out.println("}");
   }
   if (players > 5) {
     out.println("} else {");
     out.println("if (player6 == '') {");                    // if player6 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player6;");
            out.println("guestid_slot = f.guest_id6;");
            out.println("f.player6.value = name + spc;");
         out.println("} else {");
            out.println("f.player6.focus();"); // here for IE compat
            out.println("f.player6.value = name + spc;");
            out.println("f.player6.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p6cw.value = defCW;");
         out.println("}");
     out.println("} else {");
     out.println("if (player7 == '') {");                    // if player7 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player7;");
            out.println("guestid_slot = f.guest_id7;");
            out.println("f.player7.value = name + spc;");
         out.println("} else {");
            out.println("f.player7.focus();"); // here for IE compat
            out.println("f.player7.value = name + spc;");
            out.println("f.player7.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p7cw.value = defCW;");
         out.println("}");
     out.println("} else {");
     out.println("if (player8 == '') {");                    // if player8 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player8;");
            out.println("guestid_slot = f.guest_id8;");
            out.println("f.player8.value = name + spc;");
         out.println("} else {");
            out.println("f.player8.focus();"); // here for IE compat
            out.println("f.player8.value = name + spc;");
            out.println("f.player8.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p8cw.value = defCW;");
         out.println("}");
   }
   if (players > 8) {
     out.println("} else {");
     out.println("if (player9 == '') {");                    // if player9 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player9;");
            out.println("guestid_slot = f.guest_id9;");
            out.println("f.player9.value = name + spc;");
         out.println("} else {");
            out.println("f.player9.focus();"); // here for IE compat
            out.println("f.player9.value = name + spc;");
            out.println("f.player9.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p9cw.value = defCW;");
         out.println("}");
     out.println("} else {");
     out.println("if (player10 == '') {");                    // if player10 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player10;");
            out.println("guestid_slot = f.guest_id10;");
            out.println("f.player10.value = name + spc;");
         out.println("} else {");
            out.println("f.player10.focus();"); // here for IE compat
            out.println("f.player10.value = name + spc;");
            out.println("f.player10.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p10cw.value = defCW;");
         out.println("}");
   }
   if (players > 10) {
     out.println("} else {");
     out.println("if (player11 == '') {");                    // if player11 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player11;");
            out.println("guestid_slot = f.guest_id11;");
            out.println("f.player11.value = name + spc;");
         out.println("} else {");
            out.println("f.player11.focus();"); // here for IE compat
            out.println("f.player11.value = name + spc;");
            out.println("f.player11.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p11cw.value = defCW;");
         out.println("}");
     out.println("} else {");
     out.println("if (player12 == '') {");                    // if player12 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player12;");
            out.println("guestid_slot = f.guest_id12;");
            out.println("f.player12.value = name + spc;");
         out.println("} else {");
            out.println("f.player12.focus();"); // here for IE compat
            out.println("f.player12.value = name + spc;");
            out.println("f.player12.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p12cw.value = defCW;");
         out.println("}");
   }
   if (players > 12) {
     out.println("} else {");
     out.println("if (player13 == '') {");                    // if player13 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player13;");
            out.println("guestid_slot = f.guest_id13;");
            out.println("f.player13.value = name + spc;");
         out.println("} else {");
            out.println("f.player13.focus();"); // here for IE compat
            out.println("f.player13.value = name + spc;");
            out.println("f.player13.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p13cw.value = defCW;");
         out.println("}");
     out.println("} else {");
     out.println("if (player14 == '') {");                    // if player14 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player14;");
            out.println("guestid_slot = f.guest_id14;");
            out.println("f.player14.value = name + spc;");
         out.println("} else {");
            out.println("f.player14.focus();"); // here for IE compat
            out.println("f.player14.value = name + spc;");
            out.println("f.player14.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p14cw.value = defCW;");
         out.println("}");
     out.println("} else {");
     out.println("if (player15 == '') {");                    // if player15 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player15;");
            out.println("guestid_slot = f.guest_id15;");
            out.println("f.player15.value = name + spc;");
         out.println("} else {");
            out.println("f.player15.focus();"); // here for IE compat
            out.println("f.player15.value = name + spc;");
            out.println("f.player15.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p15cw.value = defCW;");
         out.println("}");
   }
   if (players > 15) {
     out.println("} else {");
     out.println("if (player16 == '') {");                    // if player16 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player16;");
            out.println("guestid_slot = f.guest_id16;");
            out.println("f.player16.value = name + spc;");
         out.println("} else {");
            out.println("f.player16.focus();"); // here for IE compat
            out.println("f.player16.value = name + spc;");
            out.println("f.player16.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p16cw.value = defCW;");
         out.println("}");
   }
   if (players > 16) {
     out.println("} else {");
     out.println("if (player17 == '') {");                    // if player17 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player17;");
            out.println("guestid_slot = f.guest_id17;");
            out.println("f.player17.value = name + spc;");
         out.println("} else {");
            out.println("f.player17.focus();"); // here for IE compat
            out.println("f.player17.value = name + spc;");
            out.println("f.player17.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p17cw.value = defCW;");
         out.println("}");
     out.println("} else {");
     out.println("if (player18 == '') {");                    // if player18 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player18;");
            out.println("guestid_slot = f.guest_id18;");
            out.println("f.player18.value = name + spc;");
         out.println("} else {");
            out.println("f.player18.focus();"); // here for IE compat
            out.println("f.player18.value = name + spc;");
            out.println("f.player18.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p18cw.value = defCW;");
         out.println("}");
     out.println("} else {");
     out.println("if (player19 == '') {");                    // if player19 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player19;");
            out.println("guestid_slot = f.guest_id19;");
            out.println("f.player19.value = name + spc;");
         out.println("} else {");
            out.println("f.player19.focus();"); // here for IE compat
            out.println("f.player19.value = name + spc;");
            out.println("f.player19.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p19cw.value = defCW;");
         out.println("}");
     out.println("} else {");
     out.println("if (player20 == '') {");                    // if player20 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player20;");
            out.println("guestid_slot = f.guest_id20;");
            out.println("f.player20.value = name + spc;");
         out.println("} else {");
            out.println("f.player20.focus();"); // here for IE compat
            out.println("f.player20.value = name + spc;");
            out.println("f.player20.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p20cw.value = defCW;");
         out.println("}");
   }
   if (players > 20) {
     out.println("} else {");
     out.println("if (player21 == '') {");                    // if player21 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player21;");
            out.println("guestid_slot = f.guest_id21;");
            out.println("f.player21.value = name + spc;");
         out.println("} else {");
            out.println("f.player21.focus();"); // here for IE compat
            out.println("f.player21.value = name + spc;");
            out.println("f.player21.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p21cw.value = defCW;");
         out.println("}");
     out.println("} else {");
     out.println("if (player22 == '') {");                    // if player22 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player22;");
            out.println("guestid_slot = f.guest_id22;");
            out.println("f.player22.value = name + spc;");
         out.println("} else {");
            out.println("f.player22.focus();"); // here for IE compat
            out.println("f.player22.value = name + spc;");
            out.println("f.player22.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p22cw.value = defCW;");
         out.println("}");
     out.println("} else {");
     out.println("if (player23 == '') {");                    // if player23 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player23;");
            out.println("guestid_slot = f.guest_id23;");
            out.println("f.player23.value = name + spc;");
         out.println("} else {");
            out.println("f.player23.focus();"); // here for IE compat
            out.println("f.player23.value = name + spc;");
            out.println("f.player23.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p23cw.value = defCW;");
         out.println("}");
     out.println("} else {");
     out.println("if (player24 == '') {");                    // if player24 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player24;");
            out.println("guestid_slot = f.guest_id24;");
            out.println("f.player24.value = name + spc;");
         out.println("} else {");
            out.println("f.player24.focus();"); // here for IE compat
            out.println("f.player24.value = name + spc;");
            out.println("f.player24.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p24cw.value = defCW;");
         out.println("}");
     out.println("} else {");
     out.println("if (player25 == '') {");                    // if player25 is empty
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player25;");
            out.println("guestid_slot = f.guest_id25;");
            out.println("f.player25.value = name + spc;");
         out.println("} else {");
            out.println("f.player25.focus();"); // here for IE compat
            out.println("f.player25.value = name + spc;");
            out.println("f.player25.focus();");
         out.println("}");
         out.println("if (defCW != '') {");
            out.println("f.p25cw.value = defCW;");
         out.println("}");

     out.println("}");         // p25
     out.println("}");         // p24
     out.println("}");         // p23
     out.println("}");         // p22
     out.println("}");         // p21
     out.println("}");         // p20
     out.println("}");         // p19
     out.println("}");         // p18
     out.println("}");         // p17
     out.println("}");         // p16
     out.println("}");         // p15
     out.println("}");         // p14
     out.println("}");         // p13
     out.println("}");         // p12
     out.println("}");         // p11
     out.println("}");         // p10
     out.println("}");         // p9
     out.println("}");         // p8
     out.println("}");         // p7
     out.println("}");         // p6
     out.println("}");         // p5
   } else {
      if (players > 16) {
        out.println("}");         // p20
        out.println("}");         // p19
        out.println("}");         // p18
        out.println("}");         // p17
        out.println("}");         // p16
        out.println("}");         // p15
        out.println("}");         // p14
        out.println("}");         // p13
        out.println("}");         // p12
        out.println("}");         // p11
        out.println("}");         // p10
        out.println("}");         // p9
        out.println("}");         // p8
        out.println("}");         // p7
        out.println("}");         // p6
        out.println("}");         // p5
      } else {
         if (players > 15) {
           out.println("}");         // p16
           out.println("}");         // p15
           out.println("}");         // p14
           out.println("}");         // p13
           out.println("}");         // p12
           out.println("}");         // p11
           out.println("}");         // p10
           out.println("}");         // p9
           out.println("}");         // p8
           out.println("}");         // p7
           out.println("}");         // p6
           out.println("}");         // p5
         } else {
            if (players > 12) {
              out.println("}");         // p15
              out.println("}");         // p14
              out.println("}");         // p13
              out.println("}");         // p12
              out.println("}");         // p11
              out.println("}");         // p10
              out.println("}");         // p9
              out.println("}");         // p8
              out.println("}");         // p7
              out.println("}");         // p6
              out.println("}");         // p5
            } else {
               if (players > 10) {
                 out.println("}");         // p12
                 out.println("}");         // p11
                 out.println("}");         // p10
                 out.println("}");         // p9
                 out.println("}");         // p8
                 out.println("}");         // p7
                 out.println("}");         // p6
                 out.println("}");         // p5
               } else {
                  if (players > 8) {
                    out.println("}");         // p10
                    out.println("}");         // p9
                    out.println("}");         // p8
                    out.println("}");         // p7
                    out.println("}");         // p6
                    out.println("}");         // p5
                  } else {
                     if (players > 5) {
                       out.println("}");         // p8
                       out.println("}");         // p7
                       out.println("}");         // p6
                       out.println("}");         // p5
                     } else {
                        if (players > 4) {
                          out.println("}");         // p5

                        }
                     }
                  }
               }
            }
         }
      }
   }
   out.println("}");         // p4
   out.println("}");         // p3
   out.println("}");         // p2
   out.println("}");         // p1

   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");                               // End of script

   out.println("</HEAD>");

   // ********* end of scripts **********

   out.println("<body onLoad=\"movenotes()\" bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

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
   out.println("var nonProCount = " + nonProCount + ";");
   out.println("var nonProTmodes = Array()");
   for (int j=0; j<nonProCount; j++) {
       out.println("nonProTmodes[" + j + "] = \"" + nonProTmodes[j] + "\";");
   }   
   out.println("</script>");
   
   out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
   out.println("<tr><td valign=\"top\" align=\"center\">");

   out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"336633\" align=\"center\" valign=\"top\">");
     out.println("<tr><td align=\"left\" width=\"300\">");
     out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
     out.println("</td>");

     out.println("<td align=\"center\">");
     if (club.equals( "oldoaks" )) {
        out.println("<font color=\"#ffffff\" size=\"5\">Member Tee Time Request</font>");
     } else if (!lotteryText.equals("")) {
        out.println("<font color=\"#ffffff\" size=\"5\">Member " +lotteryText+ "</font>");
     } else {
        out.println("<font color=\"#ffffff\" size=\"5\">Member Lottery Request</font>");
     }
     out.println("</font></td>");

     out.println("<td align=\"center\" width=\"300\">");
     out.println("<font size=\"1\" color=\"#ffffff\">Copyright&nbsp;</font>");
     out.println("<font size=\"2\" color=\"#ffffff\">&#169;&nbsp;</font>");
     out.println("<font size=\"1\" color=\"#ffffff\">ForeTees, LLC <br> 2008 All rights reserved.");
     out.println("</font><font size=\"3\">");
      out.println("<br><br><a href=\"/" +rev+ "/member_help.htm\" target=\"_blank\"><b>Help</b></a>");
     out.println("</font></td>");
   out.println("</tr></table>");

   out.println("<table border=\"0\" align=\"center\">");                           // table for main page
   out.println("<tr><td align=\"center\">");

      out.println("<br>");
      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"620\" align=\"center\">");
         out.println("<font size=\"3\">");
         if (club.equals( "oldoaks" )) {
            out.println("<b>Tee Time Registration</b><br></font>");
         } else if (!lotteryText.equals("")) {
            out.println("<b>" +lotteryText+ " Registration</b><br></font>");
         } else {
            out.println("<b>Lottery Registration</b><br></font>");
         }
         out.println("<font size=\"2\">");
         out.println(" Add players to the group(s) and click on 'Submit Request' to enter the request. ");
         out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<font size=\"2\"><br>");
      out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b>");
        out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Time Requested:&nbsp;&nbsp;<b>" + stime + "</b>");
     if (!course.equals( "" )) {
        out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>");
     }

      out.println("<table border=\"0\" align=\"center\" valign=\"top\" cellpadding=\"5\" cellspacing=\"5\">"); // table to contain 4 tables below

         out.println("<tr>");
         out.println("<td align=\"center\" valign=\"top\">");         // col for Instructions

            out.println("<font size=\"1\">");
            out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/Member_help_lott_instruct.htm', 'newwindow', config='Height=460, width=500, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            out.println("<img src=\"/" +rev+ "/images/instructions.gif\" border=0>");
            out.println("<br>Click for Help</a>");

            out.println("</font><font size=\"2\">");
            out.println("<br><br><br>");

            if (lottid > 0) {

               out.println("<form action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\" name=\"can\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
               out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");

            } else {            // go directly back to caller

               if (index.equals( "999" )) {         // if came from Member_teelist

                  out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain.htm\">");

               } else {

                  if (index.equals( "995" )) {         // if came from Member_teelist_list

                     out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain2.htm\">");

                  } else {

                     if (index.equals( "888" )) {       // if from Member_searchmem

                        out.println("<form method=\"get\" action=\"/" +rev+ "/member_searchmem.htm\">");

                     } else {                                // return to Member_sheet - must rebuild frames first

                        out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                        if (!returnCourse.equals( "" )) {
                           out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
                        } else {
                           out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        }
                     }
                  }
               }
            }
            out.println("Return<br>w/o Changes:<br>");
            out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");

          out.println("</font></td>");

          out.println("<form action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\" name=\"playerform\">");
          out.println("<td align=\"center\" valign=\"top\">");

            out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" align=\"center\" width=\"370\">");  // table for player selection
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<b>Add or Remove Players</b>");
            out.println("</font></td></tr>");

            out.println("<tr><td align=\"left\">");
               out.println("<font size=\"2\">");

               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Players");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Trans&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9-Holes</b>");

               //
               //   Output the player rows in groups of 4 or 5
               //
               outPlayerRow(1, player1, p1cw, p91, parmc, blockP1, out);
               
               outPlayerRow(2, player2, p2cw, p92, parmc, blockP2, out);
               
               outPlayerRow(3, player3, p3cw, p93, parmc, blockP3, out);
               
               outPlayerRow(4, player4, p4cw, p94, parmc, blockP4, out);
                                                 
               if ((p5.equals( "Yes" )) || (players > 4)) {

                  if (p5.equals( "No" )) {   // if 4-somes only

                    out.println("</font></td></tr>");

                    out.println("<tr><td align=\"left\">");     // new row for new group
                    out.println("<font size=\"2\">");
                  }

                  outPlayerRow(5, player5, p5cw, p95, parmc, blockP5, out);
               
               } else {

                  out.println("<input type=\"hidden\" name=\"player5\" value=\"\">");
                  out.println("<input type=\"hidden\" name=\"p5cw\" value=\"\">");
               }

              if (players > 5) {

                 if (p5.equals( "Yes" )) {

                    out.println("</font></td></tr>");

                    out.println("<tr><td align=\"left\">");     // new row for new group
                    out.println("<font size=\"2\">");
                 }

                 outPlayerRow(6, player6, p6cw, p96, parmc, blockP6, out);
               
                 outPlayerRow(7, player7, p7cw, p97, parmc, blockP7, out);
               
                 outPlayerRow(8, player8, p8cw, p98, parmc, blockP8, out);                               
              }

              if (players > 8) {

                if (p5.equals( "No" )) {   // if 4-somes only

                  out.println("</font></td></tr>");

                  out.println("<tr><td align=\"left\">");     // new row for new group
                  out.println("<font size=\"2\">");
                }

                 outPlayerRow(9, player9, p9cw, p99, parmc, blockP9, out);
              
                 outPlayerRow(10, player10, p10cw, p910, parmc, blockP10, out);              
              }

              if (players > 10) {

                if (p5.equals( "Yes" )) {   // if 5-somes

                  out.println("</font></td></tr>");

                  out.println("<tr><td align=\"left\">");     // new row for new group
                  out.println("<font size=\"2\">");
                }

                outPlayerRow(11, player11, p11cw, p911, parmc, blockP11, out);
              
                outPlayerRow(12, player12, p12cw, p912, parmc, blockP12, out);              
              }

              if (players > 12) {

                if (p5.equals( "No" )) {   // if 4-somes only

                  out.println("</font></td></tr>");

                  out.println("<tr><td align=\"left\">");     // new row for new group
                  out.println("<font size=\"2\">");
                }

                outPlayerRow(13, player13, p13cw, p913, parmc, blockP13, out);
              
                outPlayerRow(14, player14, p14cw, p914, parmc, blockP14, out);
              
                outPlayerRow(15, player15, p15cw, p915, parmc, blockP15, out);              
              }

              if (players > 15) {

                if (p5.equals( "Yes" )) {   // if 5-somes

                  out.println("</font></td></tr>");

                  out.println("<tr><td align=\"left\">");     // new row for new group
                  out.println("<font size=\"2\">");
                }

                outPlayerRow(16, player16, p16cw, p916, parmc, blockP16, out);              
              }
               
              if (players > 16) {

                if (p5.equals( "No" )) {   // if 4-somes only

                  out.println("</font></td></tr>");

                  out.println("<tr><td align=\"left\">");     // new row for new group
                  out.println("<font size=\"2\">");
                }

                outPlayerRow(17, player17, p17cw, p917, parmc, blockP17, out);
              
                outPlayerRow(18, player18, p18cw, p918, parmc, blockP18, out);
              
                outPlayerRow(19, player19, p19cw, p919, parmc, blockP19, out);
              
                outPlayerRow(20, player20, p20cw, p920, parmc, blockP20, out);
              }

              if (players > 20) {

                 out.println("</font></td></tr>");

                 out.println("<tr><td align=\"left\">");     // new row for new group
                 out.println("<font size=\"2\"><br>");

                outPlayerRow(21, player21, p21cw, p921, parmc, blockP21, out);

                outPlayerRow(22, player22, p22cw, p922, parmc, blockP22, out);

                outPlayerRow(23, player23, p23cw, p923, parmc, blockP23, out);

                outPlayerRow(24, player24, p24cw, p924, parmc, blockP24, out);

                outPlayerRow(25, player25, p25cw, p925, parmc, blockP25, out);
              }

              out.println("</font></td></tr>");

              out.println("<tr><td align=\"center\">");     // new row for notes, etc.
              out.println("<font size=\"2\">");

               //
               //   Notes
               //
               //   Script will put any existing notes in the textarea (value= doesn't work)
               //
               out.println("<input type=\"hidden\" name=\"oldnotes\" value=\"" + notes + "\">"); // hold notes for script

               if (hide != 0) {      // if proshop wants to hide the notes, do not display the text box or notes

                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">"); // pass existing notes

               } else {

                  out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasetext('notes')\" style=\"cursor:hand\">");
                  out.println("Notes to Pro:&nbsp;<textarea name=\"notes\" value=\"\" id=\"notes\" cols=\"22\" rows=\"2\">");
                  out.println("</textarea>");
               }

               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + sdate + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stime + "\">");
               out.println("<input type=\"hidden\" name=\"mm\" value=\"" + mm + "\">");
               out.println("<input type=\"hidden\" name=\"yy\" value=\"" + yy + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
               out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lottName + "\">");
               out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
               out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
               out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
               out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" + mins_before + "\">");
               out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" + mins_after + "\">");
               out.println("<input type=\"hidden\" name=\"checkothers\" value=\"" + checkothers + "\">");

               out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id6\" value=\"" + guest_id6 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id7\" value=\"" + guest_id7 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id8\" value=\"" + guest_id8 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id9\" value=\"" + guest_id9 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id10\" value=\"" + guest_id10 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id11\" value=\"" + guest_id11 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id12\" value=\"" + guest_id12 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id13\" value=\"" + guest_id13 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id14\" value=\"" + guest_id14 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id15\" value=\"" + guest_id15 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id16\" value=\"" + guest_id16 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id17\" value=\"" + guest_id17 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id18\" value=\"" + guest_id18 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id19\" value=\"" + guest_id19 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id20\" value=\"" + guest_id20 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id21\" value=\"" + guest_id21 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id22\" value=\"" + guest_id22 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id23\" value=\"" + guest_id23 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id24\" value=\"" + guest_id24 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id25\" value=\"" + guest_id25 + "\">");

               out.println("<font size=\"1\"><br>");
               for (i=0; i<16; i++) {
                  if (!parmc.tmodea[i].equals( "" )) {
                     out.println(parmc.tmodea[i]+ " = " +parmc.tmode[i]+ "&nbsp;&nbsp;");
                  }
               }
               out.println("(9 = 9 holes)</font><br>");
               out.println("<input type=submit value=\"Submit Request\" name=\"submitForm\">");
               out.println("</font></td></tr>");
               out.println("</table>");
               

               if (club.equals( "brookhavenclub" )) {       // Brookhaven - guests NOT allowed in lottery reqs
      
                  out.println("<p align=center>Please contact the golf shop to make a lottery request with guests.</p>");
               }
               
               
               
         out.println("</td>");
         out.println("<td valign=\"top\">");

   // ********************************************************************************
   //   If we got control from user clicking on a letter in the Member List,
   //   then we must build the name list.
   // ********************************************************************************
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
                     "SELECT name_last, name_first, name_mi, wc FROM member2b " +
                     "WHERE name_last LIKE ? AND inact = 0 ORDER BY name_last, name_first, name_mi");

            stmt2.clearParameters();               // clear the parms
            stmt2.setString(1, letter);            // put the parm in stmt
            rs = stmt2.executeQuery();             // execute the prepared stmt

            out.println("<tr><td align=\"left\"><font size=\"2\">");
            out.println("<select size=\"20\" name=\"bname\" onClick=\"movename(this.form.bname.value)\" style=\"cursor:hand\">");

            while(rs.next()) {

               last = rs.getString(1);
               first = rs.getString(2);
               mid = rs.getString(3);
               wc = rs.getString(4);           // walk/cart preference

               if (club.equals( "cherryhills" )) {        // if cherry hills, no defualt c/w

                  wc = "";

               } else {

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

   out.println("</td>");

   out.println("</td>");                                      // end of this column
   out.println("<td width=\"200\" valign=\"top\">");

   //
   //   Output the Alphabit Table for Members' Last Names
   //
   alphaTable.getTable(out, user);                               // ALPHABIT TABLE


   //
   //  add a table for 'guest' and 'x'
   //
   out.println("<br><table border=\"1\" bgcolor=\"#F5F5DC\">");
      out.println("<tr bgcolor=\"#336633\">");
      out.println("<td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<b>Guest Types</b>");
      out.println("</font></td>");
   out.println("</tr>");

   //
   //     Check the club db table for Guest parms specified by proshop
   //
   try {
      //
      //  first we must count how many fields there will be
      //
      xCount = 0;
      
      if (!club.equals( "brookhavenclub" )) {              // skip guest types if Brookhaven - not allowed in lottery reqs
      
         for (i = 0; i < parm.MAX_Guests; i++) {

            if (!parm.guest[i].equals( "" ) && parm.gOpt[i] == 0) {   // count the guest names

               xCount++;
            }
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

         out.println("<select size=\"" + xCount + "\" name=\"xname\" onClick=\"moveguest(this.form.xname.value)\" style=\"cursor:hand\">");
         for (i = 0; i < parm.MAX_Guests; i++) {

            if (!parm.guest[i].equals( "" ) && parm.gOpt[i] == 0) {   // if guest name is open for members

               out.println("<option value=\"" + parm.guest[i] + "|" + parm.gDb[i] + "\">" + parm.guest[i] + "</option>");
            }
         }
         out.println("</select>");
         out.println("</font></td></tr></table>");      // end of this table and column

      } else {

         out.println("</table>");      // end the table and column if none specified
      }
   }
   catch (Exception exc) {             // SQL Error - ignore guest 

      out.println("</table>");
   }

   out.println("</td>");
   out.println("</form>");
   out.println("</tr>");
   out.println("</table>");      // end of large table containg 3 smaller tables

   out.println("</font></td></tr>");
   out.println("</table>");                      // end of main page table

   //
   //  End of HTML page
   //
   out.println("</td></tr>");
   out.println("</table>");                      // end of whole page table
   out.println("</font></center></body></html>");
   out.close();

 }  // end of doPost



 // *********************************************************
 //  Process reservation request from Member_lott (HTML)
 // *********************************************************

 private void verify(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


   Statement stmt = null;
   Statement stmtN = null;
   ResultSet rs = null;
   ResultSet rs7 = null;
   
   int mobile = 0;              // Mobile user indicator
   

   //
   //  Get this session's user name
   //
   String user = (String)session.getAttribute("user");
   String club = (String)session.getAttribute("club");

   //
   //  get Mobile user indicator
   //
   try {      
      mobile = (Integer)session.getAttribute("mobile");        
   }
   catch (Exception ignore) {   
      mobile = 0;
   }
   
   
   int players = 0;
   int slots = 0;
   int lstate = 0;
   int reject = 0;
   int count = 0;
   int time = 0;
   int time2 = 0;
   int hr = 0;
   int min = 0;
   int dd = 0;
   int mm = 0;
   int yy = 0;
   int grest_num = 0;
   int fb = 0;
   int fb2 = 0;
   int t_fb = 0;
   int members = 0;
   int minMembers = 0;
   int minPlayers = 0;
   int proNew = 0;
   int proMod = 0;
   int memNew = 0;
   int memMod = 0;
   int i = 0;
   int i2 = 0;
   int mtimes = 0;
   int year = 0;
   int month = 0;
   int dayNum = 0;
   int temp = 0;
   int sendEmail = 0;
   int emailNew = 0;
   int emailMod = 0;
   int emailCan = 0;
   int mems = 0;
   int rest_stime = 0;
   int rest_etime = 0;
   int mins_before = 0;
   int mins_after = 0;
   int in_use = 0;
   int hide = 0;
   int gi = 0;
   int hit = 0;
   int ind = 0;

   int inval1 = 0;
   int inval2 = 0;
   int inval3 = 0;
   int inval4 = 0;
   int inval5 = 0;
   int inval6 = 0;
   int inval7 = 0;
   int inval8 = 0;
   int inval9 = 0;
   int inval10 = 0;
   int inval11 = 0;
   int inval12 = 0;
   int inval13 = 0;
   int inval14 = 0;
   int inval15 = 0;
   int inval16 = 0;
   int inval17 = 0;
   int inval18 = 0;
   int inval19 = 0;
   int inval20 = 0;
   int inval21 = 0;
   int inval22 = 0;
   int inval23 = 0;
   int inval24 = 0;
   int inval25 = 0;

   int guest_id1 = 0;
   int guest_id2 = 0;
   int guest_id3 = 0;
   int guest_id4 = 0;
   int guest_id5 = 0;
   int guest_id6 = 0;
   int guest_id7 = 0;
   int guest_id8 = 0;
   int guest_id9 = 0;
   int guest_id10 = 0;
   int guest_id11 = 0;
   int guest_id12 = 0;
   int guest_id13 = 0;
   int guest_id14 = 0;
   int guest_id15 = 0;
   int guest_id16 = 0;
   int guest_id17 = 0;
   int guest_id18 = 0;
   int guest_id19 = 0;
   int guest_id20 = 0;
   int guest_id21 = 0;
   int guest_id22 = 0;
   int guest_id23 = 0;
   int guest_id24 = 0;
   int guest_id25 = 0;

   long date = 0;
   long dateStart = 0;
   long dateEnd = 0;
   long lottid = 0;

   String player = "";
   String gplayer = "";
   String mship = "";
   String mtype = "";
   String rest_name = "";
   String rest_recurr = "";
   String grest_recurr = "";
   String day = "";
   String in_use_by = "";
   String err_name = "";
   String sfb = "";
   String sfb2 = "";
   String rest_fb = "";
   String rest_course = "";
   String notes = "";
   String notes2 = "";
   String rcourse = "";
   String period = "";
   String mperiod = "";
   String course2 = "";
   String returnCourse = "";
   String memberName = "";
   String orig_by = user;
   String p9s = "";
   String displayOpt = "";              // display option for Mobile devices
   String msgHdr = "";
   String msgBody = "";

   //
   //  Get the replacement text for the word "Lottery" if club requested one - added to club5 by Support Club Options
   //
   String lotteryText = getClub.getLotteryText(con);         // get replacement text for "Lottery" if provided  
        
   
   boolean check = false;
   boolean error = false;
   boolean guestError = false;

   String [] playerA = new String [25];   // array to hold all possible player names
   String [] userA = new String [25];     // and usernames
   String [] pcwA = new String [25];      // and transportation modes
   String [] userg = new String [25];     // and user guest names
   String [] rguest = new String [36];    // array to hold the Guest Restriction guest names
   int [] guest_idA = new int [25];       // guest ids

   //
   //  Arrays to hold member & guest names to tie guests to members (gstA[] resides in parmLott)
   //
   String [] memA = new String [25];     // members
   String [] usergA = new String [25];   // guests' associated member (username)

   //
   //  parm block to hold verify's parms
   //
   parmLott parm = new parmLott();          // allocate a parm block

   //
   // Get all the parameters entered
   //
   String slottid = req.getParameter("lottid");       //  id of the lottery request
   String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
   String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
   String smm = req.getParameter("mm");               //  month of tee time
   String syy = req.getParameter("yy");               //  year of tee time
   String index = req.getParameter("index");          //  day index value (needed by _sheet on return)
   String p5 = req.getParameter("p5");                //  5-somes supported for this slot
   String course = req.getParameter("course");        //  name of course
   returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
   String sslots = req.getParameter("slots");         //  # of groups allowed for the Lottery
   String slstate = req.getParameter("lstate");       //  Current state of the Lottery (must be < 4 to get here)
                                        //    1 = before time to take requests (too early for requests)
                                        //    2 = after start time, before stop time (ok to take requests)
                                        //    3 = after stop time, before process time (late, but still ok for pro)
                                        //    4 = requests have already been processed (ok for all tee times now)

   //
   //  Put all the parms in a parm block
   //
   parm.player1 = req.getParameter("player1").trim();
   parm.player2 = req.getParameter("player2").trim();
   parm.player3 = req.getParameter("player3").trim();
   parm.player4 = req.getParameter("player4").trim();
   if (req.getParameter("player5") != null) {
      parm.player5 = req.getParameter("player5").trim();
   }
   if (req.getParameter("player6") != null) {
      parm.player6 = req.getParameter("player6").trim();
   }
   if (req.getParameter("player7") != null) {
      parm.player7 = req.getParameter("player7").trim();
   }
   if (req.getParameter("player8") != null) {
      parm.player8 = req.getParameter("player8").trim();
   }
   if (req.getParameter("player9") != null) {
      parm.player9 = req.getParameter("player9").trim();
   }
   if (req.getParameter("player10") != null) {
      parm.player10 = req.getParameter("player10").trim();
   }
   if (req.getParameter("player11") != null) {
      parm.player11 = req.getParameter("player11").trim();
   }
   if (req.getParameter("player12") != null) {
      parm.player12 = req.getParameter("player12").trim();
   }
   if (req.getParameter("player13") != null) {
      parm.player13 = req.getParameter("player13").trim();
   }
   if (req.getParameter("player14") != null) {
      parm.player14 = req.getParameter("player14").trim();
   }
   if (req.getParameter("player15") != null) {
      parm.player15 = req.getParameter("player15").trim();
   }
   if (req.getParameter("player16") != null) {
      parm.player16 = req.getParameter("player16").trim();
   }
   if (req.getParameter("player17") != null) {
      parm.player17 = req.getParameter("player17").trim();
   }
   if (req.getParameter("player18") != null) {
      parm.player18 = req.getParameter("player18").trim();
   }
   if (req.getParameter("player19") != null) {
      parm.player19 = req.getParameter("player19").trim();
   }
   if (req.getParameter("player20") != null) {
      parm.player20 = req.getParameter("player20").trim();
   }
   if (req.getParameter("player21") != null) {
      parm.player21 = req.getParameter("player21").trim();
   }
   if (req.getParameter("player22") != null) {
      parm.player22 = req.getParameter("player22").trim();
   }
   if (req.getParameter("player23") != null) {
      parm.player23 = req.getParameter("player23").trim();
   }
   if (req.getParameter("player24") != null) {
      parm.player24 = req.getParameter("player24").trim();
   }
   if (req.getParameter("player25") != null) {
      parm.player25 = req.getParameter("player25").trim();
   }
   if (req.getParameter("p1cw") != null) {
      parm.pcw1 = req.getParameter("p1cw");
   }
   if (req.getParameter("p2cw") != null) {
      parm.pcw2 = req.getParameter("p2cw");
   }
   if (req.getParameter("p3cw") != null) {
      parm.pcw3 = req.getParameter("p3cw");
   }
   if (req.getParameter("p4cw") != null) {
      parm.pcw4 = req.getParameter("p4cw");
   }
   if (req.getParameter("p5cw") != null) {
      parm.pcw5 = req.getParameter("p5cw");
   }
   if (req.getParameter("p6cw") != null) {
      parm.pcw6 = req.getParameter("p6cw");
   }
   if (req.getParameter("p7cw") != null) {
      parm.pcw7 = req.getParameter("p7cw");
   }
   if (req.getParameter("p8cw") != null) {
      parm.pcw8 = req.getParameter("p8cw");
   }
   if (req.getParameter("p9cw") != null) {
      parm.pcw9 = req.getParameter("p9cw");
   }
   if (req.getParameter("p10cw") != null) {
      parm.pcw10 = req.getParameter("p10cw");
   }
   if (req.getParameter("p11cw") != null) {
      parm.pcw11 = req.getParameter("p11cw");
   }
   if (req.getParameter("p12cw") != null) {
      parm.pcw12 = req.getParameter("p12cw");
   }
   if (req.getParameter("p13cw") != null) {
      parm.pcw13 = req.getParameter("p13cw");
   }
   if (req.getParameter("p14cw") != null) {
      parm.pcw14 = req.getParameter("p14cw");
   }
   if (req.getParameter("p15cw") != null) {
      parm.pcw15 = req.getParameter("p15cw");
   }
   if (req.getParameter("p16cw") != null) {
      parm.pcw16 = req.getParameter("p16cw");
   }
   if (req.getParameter("p17cw") != null) {
      parm.pcw17 = req.getParameter("p17cw");
   }
   if (req.getParameter("p18cw") != null) {
      parm.pcw18 = req.getParameter("p18cw");
   }
   if (req.getParameter("p19cw") != null) {
      parm.pcw19 = req.getParameter("p19cw");
   }
   if (req.getParameter("p20cw") != null) {
      parm.pcw20 = req.getParameter("p20cw");
   }
   if (req.getParameter("p21cw") != null) {
      parm.pcw21 = req.getParameter("p21cw");
   }
   if (req.getParameter("p22cw") != null) {
      parm.pcw22 = req.getParameter("p22cw");
   }
   if (req.getParameter("p23cw") != null) {
      parm.pcw23 = req.getParameter("p23cw");
   }
   if (req.getParameter("p24cw") != null) {
      parm.pcw24 = req.getParameter("p24cw");
   }
   if (req.getParameter("p25cw") != null) {
      parm.pcw25 = req.getParameter("p25cw");
   }

   parm.guest_id1 = (req.getParameter("guest_id1") != null) ? Integer.parseInt(req.getParameter("guest_id1")) : 0;
   parm.guest_id2 = (req.getParameter("guest_id2") != null) ? Integer.parseInt(req.getParameter("guest_id2")) : 0;
   parm.guest_id3 = (req.getParameter("guest_id3") != null) ? Integer.parseInt(req.getParameter("guest_id3")) : 0;
   parm.guest_id4 = (req.getParameter("guest_id4") != null) ? Integer.parseInt(req.getParameter("guest_id4")) : 0;
   parm.guest_id5 = (req.getParameter("guest_id5") != null) ? Integer.parseInt(req.getParameter("guest_id5")) : 0;
   parm.guest_id6 = (req.getParameter("guest_id6") != null) ? Integer.parseInt(req.getParameter("guest_id6")) : 0;
   parm.guest_id7 = (req.getParameter("guest_id7") != null) ? Integer.parseInt(req.getParameter("guest_id7")) : 0;
   parm.guest_id8 = (req.getParameter("guest_id8") != null) ? Integer.parseInt(req.getParameter("guest_id8")) : 0;
   parm.guest_id9 = (req.getParameter("guest_id9") != null) ? Integer.parseInt(req.getParameter("guest_id9")) : 0;
   parm.guest_id10 = (req.getParameter("guest_id10") != null) ? Integer.parseInt(req.getParameter("guest_id10")) : 0;
   parm.guest_id11 = (req.getParameter("guest_id11") != null) ? Integer.parseInt(req.getParameter("guest_id11")) : 0;
   parm.guest_id12 = (req.getParameter("guest_id12") != null) ? Integer.parseInt(req.getParameter("guest_id12")) : 0;
   parm.guest_id13 = (req.getParameter("guest_id13") != null) ? Integer.parseInt(req.getParameter("guest_id13")) : 0;
   parm.guest_id14 = (req.getParameter("guest_id14") != null) ? Integer.parseInt(req.getParameter("guest_id14")) : 0;
   parm.guest_id15 = (req.getParameter("guest_id15") != null) ? Integer.parseInt(req.getParameter("guest_id15")) : 0;
   parm.guest_id16 = (req.getParameter("guest_id16") != null) ? Integer.parseInt(req.getParameter("guest_id16")) : 0;
   parm.guest_id17 = (req.getParameter("guest_id17") != null) ? Integer.parseInt(req.getParameter("guest_id17")) : 0;
   parm.guest_id18 = (req.getParameter("guest_id18") != null) ? Integer.parseInt(req.getParameter("guest_id18")) : 0;
   parm.guest_id19 = (req.getParameter("guest_id19") != null) ? Integer.parseInt(req.getParameter("guest_id19")) : 0;
   parm.guest_id20 = (req.getParameter("guest_id20") != null) ? Integer.parseInt(req.getParameter("guest_id20")) : 0;
   parm.guest_id21 = (req.getParameter("guest_id21") != null) ? Integer.parseInt(req.getParameter("guest_id21")) : 0;
   parm.guest_id22 = (req.getParameter("guest_id22") != null) ? Integer.parseInt(req.getParameter("guest_id22")) : 0;
   parm.guest_id23 = (req.getParameter("guest_id23") != null) ? Integer.parseInt(req.getParameter("guest_id23")) : 0;
   parm.guest_id24 = (req.getParameter("guest_id24") != null) ? Integer.parseInt(req.getParameter("guest_id24")) : 0;
   parm.guest_id25 = (req.getParameter("guest_id25") != null) ? Integer.parseInt(req.getParameter("guest_id25")) : 0;

   day = req.getParameter("day");                      // name of day
   sfb = req.getParameter("fb");                       // Front/Back indicator
   notes = req.getParameter("notes").trim();           // Member Notes
   String hides = req.getParameter("hide");            // Hide Notes Indicator
   String jump = req.getParameter("jump");             // jump index for _sheet
   String lottName = req.getParameter("lname");        // lottery name
   String smins_before = req.getParameter("mins_before");
   String smins_after = req.getParameter("mins_after");
   String checkothers = req.getParameter("checkothers");

   //
   //  Get the Display Option if specified (Mobile Devices)
   //
   if (req.getParameter("displayOpt") != null) {

      displayOpt = req.getParameter("displayOpt");
   }
      

   //
   //  set 9-hole options
   //
   parm.p91 = 0;                       // init to 18 holes
   parm.p92 = 0;
   parm.p93 = 0;
   parm.p94 = 0;
   parm.p95 = 0;
   parm.p96 = 0;
   parm.p97 = 0;
   parm.p98 = 0;
   parm.p99 = 0;
   parm.p910 = 0;
   parm.p911 = 0;                       // init to 18 holes
   parm.p912 = 0;
   parm.p913 = 0;
   parm.p914 = 0;
   parm.p915 = 0;
   parm.p916 = 0;
   parm.p917 = 0;
   parm.p918 = 0;
   parm.p919 = 0;
   parm.p920 = 0;
   parm.p921 = 0;                       // init to 18 holes
   parm.p922 = 0;
   parm.p923 = 0;
   parm.p924 = 0;
   parm.p925 = 0;

   if (req.getParameter("p91") != null) {             // get 9-hole indicators if they were checked
      p9s = req.getParameter("p91");
      parm.p91 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p92") != null) {
      p9s = req.getParameter("p92");
      parm.p92 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p93") != null) {
      p9s = req.getParameter("p93");
      parm.p93 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p94") != null) {
      p9s = req.getParameter("p94");
      parm.p94 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p95") != null) {
      p9s = req.getParameter("p95");
      parm.p95 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p96") != null) {
      p9s = req.getParameter("p96");
      parm.p96 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p97") != null) {
      p9s = req.getParameter("p97");
      parm.p97 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p98") != null) {
      p9s = req.getParameter("p98");
      parm.p98 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p99") != null) {
      p9s = req.getParameter("p99");
      parm.p99 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p910") != null) {
      p9s = req.getParameter("p910");
      parm.p910 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p911") != null) {             // get 9-hole indicators if they were checked
      p9s = req.getParameter("p911");
      parm.p911 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p912") != null) {
      p9s = req.getParameter("p912");
      parm.p912 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p913") != null) {
      p9s = req.getParameter("p913");
      parm.p913 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p914") != null) {
      p9s = req.getParameter("p914");
      parm.p914 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p915") != null) {
      p9s = req.getParameter("p915");
      parm.p915 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p916") != null) {
      p9s = req.getParameter("p916");
      parm.p916 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p917") != null) {
      p9s = req.getParameter("p917");
      parm.p917 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p918") != null) {
      p9s = req.getParameter("p918");
      parm.p918 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p919") != null) {
      p9s = req.getParameter("p919");
      parm.p919 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p920") != null) {
      p9s = req.getParameter("p920");
      parm.p920 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p921") != null) {             // get 9-hole indicators if they were checked
      p9s = req.getParameter("p921");
      parm.p921 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p922") != null) {
      p9s = req.getParameter("p922");
      parm.p922 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p923") != null) {
      p9s = req.getParameter("p923");
      parm.p923 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p924") != null) {
      p9s = req.getParameter("p924");
      parm.p924 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p925") != null) {
      p9s = req.getParameter("p925");
      parm.p925 = Integer.parseInt(p9s);
   }

   //
   //  Convert the string values to ints
   //
   try {
      lottid = Long.parseLong(slottid);
      slots = Integer.parseInt(sslots);
      lstate = Integer.parseInt(slstate);
      time = Integer.parseInt(stime);
      mm = Integer.parseInt(smm);
      yy = Integer.parseInt(syy);
      fb = Integer.parseInt(sfb);
      mins_before = Integer.parseInt(smins_before);
      mins_after = Integer.parseInt(smins_after);
      parm.checkothers = Integer.parseInt(checkothers);
      date = Long.parseLong(sdate);
      ind = Integer.parseInt(index);                        // get numeric value of index
   }
   catch (NumberFormatException e) {
      String errorMsg = "NumberFormat Error in Member_lott: ";
      errorMsg = errorMsg + e;                                 // build error msg
      SystemUtils.logError(errorMsg);                           // log it
   }

   //
   //  Save some of the parms in the parm table
   //
   parm.club = club;
   parm.date = date;
   parm.time = time;
   parm.fb = fb;
   parm.mm = mm;
   parm.dd = dd;
   parm.yy = yy;
   parm.day = day;
   parm.course = course;
   parm.returnCourse = returnCourse;
   parm.lottid = lottid;
   parm.jump = jump;
   parm.index = index;
   parm.ind = ind;
   parm.p5 = p5;
   parm.notes = notes;
   parm.hides = hides;
   parm.lottName = lottName;
   parm.lstate = lstate;
   parm.mins_before = mins_before;
   parm.mins_after = mins_after;

   //
   //  convert the index value from string to numeric - save both
   //
   try {
      parm.ind = Integer.parseInt(index);
   }
   catch (NumberFormatException e) {
   }

   //
   //  See if user wants to hide any notes from the Members
   //
   hide = 0;      // init

   if (hides.equals( "Yes" )) {

      hide = 1;
   }

   //
   //  Get the length of Notes (max length of 254 chars)
   //
   int notesL = 0;

   if (!notes.equals( "" )) {

      notesL = notes.length();       // get length of notes
   }

   //
   //   use yy and mm and date to determine dd (from tee time's date)
   //
   temp = yy * 10000;
   temp = temp + (mm * 100);
   parm.dd = (int) date - temp;            // get day of month from date

   if (lottid > 0) {          // if request already exists
      //
      //  Check if this request is still 'in use' and still in use by this user??
      //
      checkInUseBy(con, lottid, parm);

      notes = parm.notes;
      hide = parm.hide;
      memNew = parm.memNew;
      memMod = parm.memMod;
      in_use = parm.in_use;
      in_use_by = parm.in_use_by;
      orig_by = parm.orig_by;

      if (orig_by.equals( "" )) {    // if originator field still empty

         orig_by = user;             // set this user as the originator
      }

      if ((in_use == 0) || (!in_use_by.equalsIgnoreCase( user ))) {    // if time slot in use and not by this user
         
         if (mobile == 0) {       // if not mobile user

            out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<CENTER><BR><BR><H3>Reservation Timer Expired</H3>");
            out.println("<BR><BR>Sorry, but this request has been returned to the system.<BR>");
            out.println("<BR>The system timed out and released the request.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");

            if (index.equals( "999" )) {         // if came from Member_teelist

               out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain.htm\">");

            } else {

               if (index.equals( "995" )) {         // if came from Member_teelist_list

                  out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain2.htm\">");

               } else {

                  if (index.equals( "888" )) {       // if from Member_searchmem

                     out.println("<form method=\"get\" action=\"/" +rev+ "/member_searchmem.htm\">");

                  } else {                                // return to Member_sheet - must rebuild frames first

                     out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\">");
                     out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                     if (!returnCourse.equals( "" )) {
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
                     } else {
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                     }
                     out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
                  }
               }
            }
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            
         } else {        // Mobile user
            
            out.println(SystemUtils.HeadTitleMobile("Foretees Request"));
            out.println(SystemUtils.BannerMobile());

            out.println("<div class=\"content\">");
            out.println("<div class=\"headertext\">");    // output the heading
            out.println("Reservation Timer Expired");
            out.println("</div>");

            out.println("<div class=\"smheadertext\">Sorry, the request has timed out.<BR>Please try again.</div>");     

            out.println("<ul>");

            if (index.equals( "995" )) {         // if came from Member_teelist_list

               out.println("<li>");
               out.println("<form action=\"/" +rev+ "/servlet/Member_teelist_mobile\" method=\"get\">");
               out.println("<input type=\"submit\" value=\"Return to List\"></form>");
               out.println("</li>");

            } else {

               out.println("<li>");
               out.println("<form action=\"/" +rev+ "/servlet/Member_sheet\" method=\"post\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
               out.println("<input type=\"submit\" value=\"Return To Tee Sheet\"></form>");
               out.println("</li>");
            }

            out.println("</ul></div>");
            out.println("</body></html>");                 
         }
         out.close();
         return;
      }                   // end of IF in use by other user
   }

   //
   //  Slide players if necessary so they start at Player1 and run continuously.
   //
   i = 0;                     // init index
   while (i < 25) {
      playerA[i] = "";       // init the player arrays
      userA[i] = "";
      pcwA[i] = "";
      guest_idA[i] = 0;
      i++;
   }

   i = 0;                     // init index

   if (!parm.player1.equals( "" )) {

      playerA[i] = parm.player1;
      userA[i] = parm.user1;
      pcwA[i] = parm.pcw1;
      guest_idA[i] = parm.guest_id1;
      i++;
   }
   if (!parm.player2.equals( "" )) {

      playerA[i] = parm.player2;
      userA[i] = parm.user2;
      pcwA[i] = parm.pcw2;
      guest_idA[i] = parm.guest_id2;
      i++;
   }
   if (!parm.player3.equals( "" )) {

      playerA[i] = parm.player3;
      userA[i] = parm.user3;
      pcwA[i] = parm.pcw3;
      guest_idA[i] = parm.guest_id3;
      i++;
   }
   if (!parm.player4.equals( "" )) {

      playerA[i] = parm.player4;
      userA[i] = parm.user4;
      pcwA[i] = parm.pcw4;
      guest_idA[i] = parm.guest_id4;
      i++;
   }
   if (!parm.player5.equals( "" )) {

      playerA[i] = parm.player5;
      userA[i] = parm.user5;
      pcwA[i] = parm.pcw5;
      guest_idA[i] = parm.guest_id5;
      i++;
   }
   if (!parm.player6.equals( "" )) {

      playerA[i] = parm.player6;
      userA[i] = parm.user6;
      pcwA[i] = parm.pcw6;
      guest_idA[i] = parm.guest_id6;
      i++;
   }
   if (!parm.player7.equals( "" )) {

      playerA[i] = parm.player7;
      userA[i] = parm.user7;
      pcwA[i] = parm.pcw7;
      guest_idA[i] = parm.guest_id7;
      i++;
   }
   if (!parm.player8.equals( "" )) {

      playerA[i] = parm.player8;
      userA[i] = parm.user8;
      pcwA[i] = parm.pcw8;
      guest_idA[i] = parm.guest_id8;
      i++;
   }
   if (!parm.player9.equals( "" )) {

      playerA[i] = parm.player9;
      userA[i] = parm.user9;
      pcwA[i] = parm.pcw9;
      guest_idA[i] = parm.guest_id9;
      i++;
   }
   if (!parm.player10.equals( "" )) {

      playerA[i] = parm.player10;
      userA[i] = parm.user10;
      pcwA[i] = parm.pcw10;
      guest_idA[i] = parm.guest_id10;
      i++;
   }
   if (!parm.player11.equals( "" )) {

      playerA[i] = parm.player11;
      userA[i] = parm.user11;
      pcwA[i] = parm.pcw11;
      guest_idA[i] = parm.guest_id11;
      i++;
   }
   if (!parm.player12.equals( "" )) {

      playerA[i] = parm.player12;
      userA[i] = parm.user12;
      pcwA[i] = parm.pcw12;
      guest_idA[i] = parm.guest_id12;
      i++;
   }
   if (!parm.player13.equals( "" )) {

      playerA[i] = parm.player13;
      userA[i] = parm.user13;
      pcwA[i] = parm.pcw13;
      guest_idA[i] = parm.guest_id13;
      i++;
   }
   if (!parm.player14.equals( "" )) {

      playerA[i] = parm.player14;
      userA[i] = parm.user14;
      pcwA[i] = parm.pcw14;
      guest_idA[i] = parm.guest_id14;
      i++;
   }
   if (!parm.player15.equals( "" )) {

      playerA[i] = parm.player15;
      userA[i] = parm.user15;
      pcwA[i] = parm.pcw15;
      guest_idA[i] = parm.guest_id15;
      i++;
   }
   if (!parm.player16.equals( "" )) {

      playerA[i] = parm.player16;
      userA[i] = parm.user16;
      pcwA[i] = parm.pcw16;
      guest_idA[i] = parm.guest_id16;
      i++;
   }
   if (!parm.player17.equals( "" )) {

      playerA[i] = parm.player17;
      userA[i] = parm.user17;
      pcwA[i] = parm.pcw17;
      guest_idA[i] = parm.guest_id17;
      i++;
   }
   if (!parm.player18.equals( "" )) {

      playerA[i] = parm.player18;
      userA[i] = parm.user18;
      pcwA[i] = parm.pcw18;
      guest_idA[i] = parm.guest_id18;
      i++;
   }
   if (!parm.player19.equals( "" )) {

      playerA[i] = parm.player19;
      userA[i] = parm.user19;
      pcwA[i] = parm.pcw19;
      guest_idA[i] = parm.guest_id19;
      i++;
   }
   if (!parm.player20.equals( "" )) {

      playerA[i] = parm.player20;
      userA[i] = parm.user20;
      pcwA[i] = parm.pcw20;
      guest_idA[i] = parm.guest_id20;
      i++;
   }
   if (!parm.player21.equals( "" )) {

      playerA[i] = parm.player21;
      userA[i] = parm.user21;
      pcwA[i] = parm.pcw21;
      guest_idA[i] = parm.guest_id21;
      i++;
   }
   if (!parm.player22.equals( "" )) {

      playerA[i] = parm.player22;
      userA[i] = parm.user22;
      pcwA[i] = parm.pcw22;
      guest_idA[i] = parm.guest_id22;
      i++;
   }
   if (!parm.player23.equals( "" )) {

      playerA[i] = parm.player23;
      userA[i] = parm.user23;
      pcwA[i] = parm.pcw23;
      guest_idA[i] = parm.guest_id23;
      i++;
   }
   if (!parm.player24.equals( "" )) {

      playerA[i] = parm.player24;
      userA[i] = parm.user24;
      pcwA[i] = parm.pcw24;
      guest_idA[i] = parm.guest_id24;
      i++;
   }
   if (!parm.player25.equals( "" )) {

      playerA[i] = parm.player25;
      userA[i] = parm.user25;
      pcwA[i] = parm.pcw25;
      guest_idA[i] = parm.guest_id25;
      i++;
   }

   //
   //  determine actual number of groups requested
   //
   if (p5.equals( "Yes" )) {

      slots = (i + 4)/5;         // add 4 to allow for fractions

   } else {

      slots = (i + 3)/4;
   }
   parm.slots = slots;
   players = i;             // save count for request

   //
   //  Reset the players starting with Player1
   //
   parm.player1 = playerA[0];
   parm.player2 = playerA[1];
   parm.player3 = playerA[2];
   parm.player4 = playerA[3];
   parm.player5 = playerA[4];
   parm.player6 = playerA[5];
   parm.player7 = playerA[6];
   parm.player8 = playerA[7];
   parm.player9 = playerA[8];
   parm.player10 = playerA[9];
   parm.player11 = playerA[10];
   parm.player12 = playerA[11];
   parm.player13 = playerA[12];
   parm.player14 = playerA[13];
   parm.player15 = playerA[14];
   parm.player16 = playerA[15];
   parm.player17 = playerA[16];
   parm.player18 = playerA[17];
   parm.player19 = playerA[18];
   parm.player20 = playerA[19];
   parm.player21 = playerA[20];
   parm.player22 = playerA[21];
   parm.player23 = playerA[22];
   parm.player24 = playerA[23];
   parm.player25 = playerA[24];

   parm.user1 = userA[0];
   parm.user2 = userA[1];
   parm.user3 = userA[2];
   parm.user4 = userA[3];
   parm.user5 = userA[4];
   parm.user6 = userA[5];
   parm.user7 = userA[6];
   parm.user8 = userA[7];
   parm.user9 = userA[8];
   parm.user10 = userA[9];
   parm.user11 = userA[10];
   parm.user12 = userA[11];
   parm.user13 = userA[12];
   parm.user14 = userA[13];
   parm.user15 = userA[14];
   parm.user16 = userA[15];
   parm.user17 = userA[16];
   parm.user18 = userA[17];
   parm.user19 = userA[18];
   parm.user20 = userA[19];
   parm.user21 = userA[20];
   parm.user22 = userA[21];
   parm.user23 = userA[22];
   parm.user24 = userA[23];
   parm.user25 = userA[24];

   parm.pcw1 = pcwA[0];
   parm.pcw2 = pcwA[1];
   parm.pcw3 = pcwA[2];
   parm.pcw4 = pcwA[3];
   parm.pcw5 = pcwA[4];
   parm.pcw6 = pcwA[5];
   parm.pcw7 = pcwA[6];
   parm.pcw8 = pcwA[7];
   parm.pcw9 = pcwA[8];
   parm.pcw10 = pcwA[9];
   parm.pcw11 = pcwA[10];
   parm.pcw12 = pcwA[11];
   parm.pcw13 = pcwA[12];
   parm.pcw14 = pcwA[13];
   parm.pcw15 = pcwA[14];
   parm.pcw16 = pcwA[15];
   parm.pcw17 = pcwA[16];
   parm.pcw18 = pcwA[17];
   parm.pcw19 = pcwA[18];
   parm.pcw20 = pcwA[19];
   parm.pcw21 = pcwA[20];
   parm.pcw22 = pcwA[21];
   parm.pcw23 = pcwA[22];
   parm.pcw24 = pcwA[23];
   parm.pcw25 = pcwA[24];

   parm.guest_id1 = guest_idA[0];
   parm.guest_id2 = guest_idA[1];
   parm.guest_id3 = guest_idA[2];
   parm.guest_id4 = guest_idA[3];
   parm.guest_id5 = guest_idA[4];
   parm.guest_id6 = guest_idA[5];
   parm.guest_id7 = guest_idA[6];
   parm.guest_id8 = guest_idA[7];
   parm.guest_id9 = guest_idA[8];
   parm.guest_id10 = guest_idA[9];
   parm.guest_id11 = guest_idA[10];
   parm.guest_id12 = guest_idA[11];
   parm.guest_id13 = guest_idA[12];
   parm.guest_id14 = guest_idA[13];
   parm.guest_id15 = guest_idA[14];
   parm.guest_id16 = guest_idA[15];
   parm.guest_id17 = guest_idA[16];
   parm.guest_id18 = guest_idA[17];
   parm.guest_id19 = guest_idA[18];
   parm.guest_id20 = guest_idA[19];
   parm.guest_id21 = guest_idA[20];
   parm.guest_id22 = guest_idA[21];
   parm.guest_id23 = guest_idA[22];
   parm.guest_id24 = guest_idA[23];
   parm.guest_id25 = guest_idA[24];

   //
   //  Make sure at least 1 player contains a name
   //
   if ((parm.player1.equals( "" )) && (parm.player2.equals( "" )) && (parm.player3.equals( "" )) && (parm.player4.equals( "" )) &&
       (parm.player5.equals( "" )) && (parm.player6.equals( "" )) && (parm.player7.equals( "" )) && (parm.player8.equals( "" )) &&
       (parm.player9.equals( "" )) && (parm.player10.equals( "" )) && (parm.player11.equals( "" )) && (parm.player12.equals( "" )) &&
       (parm.player13.equals( "" )) && (parm.player14.equals( "" )) && (parm.player15.equals( "" )) && (parm.player16.equals( "" )) &&
       (parm.player17.equals( "" )) && (parm.player18.equals( "" )) && (parm.player19.equals( "" )) && (parm.player20.equals( "" )) &&
       (parm.player21.equals( "" )) && (parm.player22.equals( "" )) && (parm.player23.equals( "" )) && (parm.player24.equals( "" )) &&
       (parm.player25.equals( "" ))) {
      
      msgHdr = "Data Entry Error";
      
      msgBody = "Required field has not been completed or is invalid.<BR><BR>At least 1 Player field must contain a valid entry.";
      
      buildError(msgHdr, msgBody, mobile, out);       // output the error message

      //
      //  Return to _lott
      //
      goReturn(out, parm);
      return;
   }

   //
   //  Make sure at least 1 player in the first 4 contains a name
   //
   if ((parm.player1.equals( "" )) && (parm.player2.equals( "" )) && (parm.player3.equals( "" )) && (parm.player4.equals( "" ))) {

      msgHdr = "Data Entry Error";
      
      msgBody = "Required field has not been completed or is invalid.<BR><BR>At least 1 of the first 4 Player fields must contain a valid entry.";
      
      buildError(msgHdr, msgBody, mobile, out);       // output the error message

      //
      //  Return to _lott
      //
      goReturn(out, parm);
      return;
   }

   //
   //  At least 1 Player field is present - Make sure a C/W was specified for all players
   //
   if ((!parm.player1.equals( "" ) && parm.pcw1.equals( "" )) ||
       (!parm.player2.equals( "" ) && parm.pcw2.equals( "" )) ||
       (!parm.player3.equals( "" ) && parm.pcw3.equals( "" )) ||
       (!parm.player4.equals( "" ) && parm.pcw4.equals( "" )) ||
       (!parm.player5.equals( "" ) && parm.pcw5.equals( "" )) ||
       (!parm.player6.equals( "" ) && parm.pcw6.equals( "" )) ||
       (!parm.player7.equals( "" ) && parm.pcw7.equals( "" )) ||
       (!parm.player8.equals( "" ) && parm.pcw8.equals( "" )) ||
       (!parm.player9.equals( "" ) && parm.pcw9.equals( "" )) ||
       (!parm.player10.equals( "" ) && parm.pcw10.equals( "" )) ||
       (!parm.player11.equals( "" ) && parm.pcw11.equals( "" )) ||
       (!parm.player12.equals( "" ) && parm.pcw12.equals( "" )) ||
       (!parm.player13.equals( "" ) && parm.pcw13.equals( "" )) ||
       (!parm.player14.equals( "" ) && parm.pcw14.equals( "" )) ||
       (!parm.player15.equals( "" ) && parm.pcw15.equals( "" )) ||
       (!parm.player16.equals( "" ) && parm.pcw16.equals( "" )) ||
       (!parm.player17.equals( "" ) && parm.pcw17.equals( "" )) ||
       (!parm.player18.equals( "" ) && parm.pcw18.equals( "" )) ||
       (!parm.player19.equals( "" ) && parm.pcw19.equals( "" )) ||
       (!parm.player20.equals( "" ) && parm.pcw20.equals( "" )) ||
       (!parm.player21.equals( "" ) && parm.pcw21.equals( "" )) ||
       (!parm.player22.equals( "" ) && parm.pcw22.equals( "" )) ||
       (!parm.player23.equals( "" ) && parm.pcw23.equals( "" )) ||
       (!parm.player24.equals( "" ) && parm.pcw24.equals( "" )) ||
       (!parm.player25.equals( "" ) && parm.pcw25.equals( "" ))) {

      msgHdr = "Data Entry Error";
      
      msgBody = "Required field has not been completed or is invalid.<BR><BR>You must specify a Cart or Walk option for all players.";
      
      buildError(msgHdr, msgBody, mobile, out);       // output the error message

      //
      //  Return to _lott
      //
      goReturn(out, parm);
      return;
   }

   //
   //  Process any guests that were specified
   //
   int guestErr = processGuests(con, parm);

   if (guestErr > 0) {           // if error 
      
      //
      //  Reject if any player was a guest type that is not allowed for members
      //
      gplayer = parm.player;     // get guest name in error
     
      msgHdr = "Data Entry Error";
      
      if (guestErr == 1) {

         msgBody = "<b>" + gplayer + "</b> specifies a Guest Type that is not allowed for member use." +
            "<BR><BR>If the Proshop had originally entered this guest, then that guest player" +
            "<BR><b>must</b> remain in its original position in this request." +
            "<BR><BR>Please correct this and try again.";

      } else if (guestErr == 2) {
        
         msgBody = "<BR><BR>You must specify the name of your guest(s)." +
            "<BR><b>" + gplayer + "</b> does not include a valid name (must be at least first & last names)." +
            "<BR><BR>To specify the name, click in the player box where the guest is specified, " +
            "<BR>move the cursor (use the arrow keys or mouse) to the end of the guest type value, " +
            "<BR>use the space bar to enter a space and then type the guest's name.";
      } else if (guestErr == 3) {
         msgBody = "<BR><BR><b>" + parm.gplayer + "</b> appears to have been manually entered " +
             "<br>or modified after selecting a different guest from the Guest Selection window." +
             "<BR><BR>Since this guest type uses the Guest Tracking feature, please click 'erase' " +
             "<BR>next to the current guest's name, then click the desired guest type from the Guest " +
             "<BR>Types list, and finally select a guest from the displayed guest selection window.";
      }
      
      buildError(msgHdr, msgBody, mobile, out);       // output the error message

      //  Return to _lott
      //
      goReturn(out, parm);
      return;
   }

   //
   //  make sure that position 1 is not a guest
   //
   if (!parm.g[0].equals( "" )) {

      msgHdr = "Data Entry Error";
      
      msgBody = "Player position #1 contains a guest.<BR><BR>The first player position must contain a member name.";
      
      buildError(msgHdr, msgBody, mobile, out);       // output the error message

      //
      //  Return to _lott
      //
      goReturn(out, parm);
      return;
   }

   //
   //  Make sure there are no duplicate names.
   //  Also, Parse the names to separate first, last & mi
   //  (Member does not verify single tokens - check for guest)
   //
   error = parseNames(out, parm);

   if (error == true) {

      return;           // exit if error encountered and reported
   }



   inval1 = 0;   // init invalid indicator
   inval2 = 0;   // init invalid indicator
   inval3 = 0;   // init invalid indicator
   inval4 = 0;   // init invalid indicator
   inval5 = 0;   // init invalid indicator
   inval6 = 0;
   inval7 = 0;
   inval8 = 0;
   inval9 = 0;
   inval10 = 0;
   inval11 = 0;
   inval12 = 0;
   inval13 = 0;
   inval14 = 0;
   inval15 = 0;
   inval16 = 0;
   inval17 = 0;
   inval18 = 0;
   inval19 = 0;
   inval20 = 0;
   inval21 = 0;
   inval22 = 0;
   inval23 = 0;
   inval24 = 0;
   inval25 = 0;
   members = 0;   // init Total member counter
   int memg1 = 0;     // init members per slot counters
   int memg2 = 0;
   int memg3 = 0;
   int memg4 = 0;
   int memg5 = 0;

   //
   //  Get the usernames, membership types, etc. for players if matching name found
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT username, m_ship, m_type, memNum FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ? AND inact = 0");

      if ((!parm.fname1.equals( "" )) && (!parm.lname1.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname1);
         pstmt1.setString(2, parm.fname1);
         pstmt1.setString(3, parm.mi1);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user1 = rs.getString(1);
            parm.mship1 = rs.getString(2);
            parm.mtype1 = rs.getString(3);
            parm.mNum1 = rs.getString(4);

            members++;         // increment number of members this res.
            memg1++;           // group 1

         } else {
            inval1 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname2.equals( "" )) && (!parm.lname2.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname2);
         pstmt1.setString(2, parm.fname2);
         pstmt1.setString(3, parm.mi2);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user2 = rs.getString(1);
            parm.mship2 = rs.getString(2);
            parm.mtype2 = rs.getString(3);
            parm.mNum2 = rs.getString(4);

            members++;         // increment number of members this res.
            memg1++;           // group 1
         } else {
            inval2 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname3.equals( "" )) && (!parm.lname3.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname3);
         pstmt1.setString(2, parm.fname3);
         pstmt1.setString(3, parm.mi3);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user3 = rs.getString(1);
            parm.mship3 = rs.getString(2);
            parm.mtype3 = rs.getString(3);
            parm.mNum3 = rs.getString(4);

            members++;         // increment number of members this res.
            memg1++;           // group 1
         } else {
            inval3 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname4.equals( "" )) && (!parm.lname4.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname4);
         pstmt1.setString(2, parm.fname4);
         pstmt1.setString(3, parm.mi4);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user4 = rs.getString(1);
            parm.mship4 = rs.getString(2);
            parm.mtype4 = rs.getString(3);
            parm.mNum4 = rs.getString(4);

            members++;         // increment number of members this res.
            memg1++;           // group 1
         } else {
            inval4 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname5.equals( "" )) && (!parm.lname5.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname5);
         pstmt1.setString(2, parm.fname5);
         pstmt1.setString(3, parm.mi5);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user5 = rs.getString(1);
            parm.mship5 = rs.getString(2);
            parm.mtype5 = rs.getString(3);
            parm.mNum5 = rs.getString(4);

            members++;         // increment number of members this res.
            if (p5.equals( "Yes" )) {
               memg1++;           // group 1
            } else {
               memg2++;           // group 2
            }
              
         } else {
            inval5 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname6.equals( "" )) && (!parm.lname6.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname6);
         pstmt1.setString(2, parm.fname6);
         pstmt1.setString(3, parm.mi6);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user6 = rs.getString(1);
            parm.mship6 = rs.getString(2);
            parm.mtype6 = rs.getString(3);
            parm.mNum6 = rs.getString(4);

            members++;         // increment number of members this res.
            memg2++;           // group 2
         } else {
            inval6 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname7.equals( "" )) && (!parm.lname7.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname7);
         pstmt1.setString(2, parm.fname7);
         pstmt1.setString(3, parm.mi7);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user7 = rs.getString(1);
            parm.mship7 = rs.getString(2);
            parm.mtype7 = rs.getString(3);
            parm.mNum7 = rs.getString(4);

            members++;         // increment number of members this res.
            memg2++;           // group 2
         } else {
            inval7 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname8.equals( "" )) && (!parm.lname8.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname8);
         pstmt1.setString(2, parm.fname8);
         pstmt1.setString(3, parm.mi8);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user8 = rs.getString(1);
            parm.mship8 = rs.getString(2);
            parm.mtype8 = rs.getString(3);
            parm.mNum8 = rs.getString(4);

            members++;         // increment number of members this res.
            memg2++;           // group 2
         } else {
            inval8 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname9.equals( "" )) && (!parm.lname9.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname9);
         pstmt1.setString(2, parm.fname9);
         pstmt1.setString(3, parm.mi9);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user9 = rs.getString(1);
            parm.mship9 = rs.getString(2);
            parm.mtype9 = rs.getString(3);
            parm.mNum9 = rs.getString(4);

            members++;         // increment number of members this res.
            if (p5.equals( "Yes" )) {
               memg2++;           // group 2
            } else {
               memg3++;           // group 3
            }

         } else {
            inval9 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname10.equals( "" )) && (!parm.lname10.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname10);
         pstmt1.setString(2, parm.fname10);
         pstmt1.setString(3, parm.mi10);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user10 = rs.getString(1);
            parm.mship10 = rs.getString(2);
            parm.mtype10 = rs.getString(3);
            parm.mNum10 = rs.getString(4);

            members++;         // increment number of members this res.
            if (p5.equals( "Yes" )) {
               memg2++;           // group 2
            } else {
               memg3++;           // group 3
            }

         } else {
            inval10 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname11.equals( "" )) && (!parm.lname11.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname11);
         pstmt1.setString(2, parm.fname11);
         pstmt1.setString(3, parm.mi11);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user11 = rs.getString(1);
            parm.mship11 = rs.getString(2);
            parm.mtype11 = rs.getString(3);
            parm.mNum11 = rs.getString(4);

            members++;         // increment number of members this res.
            memg3++;           // group 3
         } else {
            inval11 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname12.equals( "" )) && (!parm.lname12.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname12);
         pstmt1.setString(2, parm.fname12);
         pstmt1.setString(3, parm.mi12);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user12 = rs.getString(1);
            parm.mship12 = rs.getString(2);
            parm.mtype12 = rs.getString(3);
            parm.mNum12 = rs.getString(4);

            members++;         // increment number of members this res.
            memg3++;           // group 3
         } else {
            inval12 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname13.equals( "" )) && (!parm.lname13.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname13);
         pstmt1.setString(2, parm.fname13);
         pstmt1.setString(3, parm.mi13);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user13 = rs.getString(1);
            parm.mship13 = rs.getString(2);
            parm.mtype13 = rs.getString(3);
            parm.mNum13 = rs.getString(4);

            members++;         // increment number of members this res.
            if (p5.equals( "Yes" )) {
               memg3++;           // group 3
            } else {
               memg4++;           // group 4
            }

         } else {
            inval13 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname14.equals( "" )) && (!parm.lname14.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname14);
         pstmt1.setString(2, parm.fname14);
         pstmt1.setString(3, parm.mi14);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user14 = rs.getString(1);
            parm.mship14 = rs.getString(2);
            parm.mtype14 = rs.getString(3);
            parm.mNum14 = rs.getString(4);

            members++;         // increment number of members this res.
            if (p5.equals( "Yes" )) {
               memg3++;           // group 3
            } else {
               memg4++;           // group 4
            }

         } else {
            inval14 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname15.equals( "" )) && (!parm.lname15.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname15);
         pstmt1.setString(2, parm.fname15);
         pstmt1.setString(3, parm.mi15);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user15 = rs.getString(1);
            parm.mship15 = rs.getString(2);
            parm.mtype15 = rs.getString(3);
            parm.mNum15 = rs.getString(4);

            members++;         // increment number of members this res.
            if (p5.equals( "Yes" )) {
               memg3++;           // group 3
            } else {
               memg4++;           // group 4
            }

         } else {
            inval15 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname16.equals( "" )) && (!parm.lname16.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname16);
         pstmt1.setString(2, parm.fname16);
         pstmt1.setString(3, parm.mi16);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user16 = rs.getString(1);
            parm.mship16 = rs.getString(2);
            parm.mtype16 = rs.getString(3);
            parm.mNum16 = rs.getString(4);

            members++;         // increment number of members this res.
            memg4++;           // group 4
         } else {
            inval16 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname17.equals( "" )) && (!parm.lname17.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname17);
         pstmt1.setString(2, parm.fname17);
         pstmt1.setString(3, parm.mi17);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user17 = rs.getString(1);
            parm.mship17 = rs.getString(2);
            parm.mtype17 = rs.getString(3);
            parm.mNum17 = rs.getString(4);

            members++;         // increment number of members this res.
            if (p5.equals( "Yes" )) {
               memg4++;           // group 4
            } else {
               memg5++;           // group 5
            }

         } else {
            inval17 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname18.equals( "" )) && (!parm.lname18.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname18);
         pstmt1.setString(2, parm.fname18);
         pstmt1.setString(3, parm.mi18);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user18 = rs.getString(1);
            parm.mship18 = rs.getString(2);
            parm.mtype18 = rs.getString(3);
            parm.mNum18 = rs.getString(4);

            members++;         // increment number of members this res.
            if (p5.equals( "Yes" )) {
               memg4++;           // group 4
            } else {
               memg5++;           // group 5
            }

         } else {
            inval18 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname19.equals( "" )) && (!parm.lname19.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname19);
         pstmt1.setString(2, parm.fname19);
         pstmt1.setString(3, parm.mi19);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user19 = rs.getString(1);
            parm.mship19 = rs.getString(2);
            parm.mtype19 = rs.getString(3);
            parm.mNum19 = rs.getString(4);

            members++;         // increment number of members this res.
            if (p5.equals( "Yes" )) {
               memg4++;           // group 4
            } else {
               memg5++;           // group 5
            }

         } else {
            inval19 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname20.equals( "" )) && (!parm.lname20.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname20);
         pstmt1.setString(2, parm.fname20);
         pstmt1.setString(3, parm.mi20);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user20 = rs.getString(1);
            parm.mship20 = rs.getString(2);
            parm.mtype20 = rs.getString(3);
            parm.mNum20 = rs.getString(4);

            members++;         // increment number of members this res.
            if (p5.equals( "Yes" )) {
               memg4++;           // group 4
            } else {
               memg5++;           // group 5
            }

         } else {
            inval20 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname21.equals( "" )) && (!parm.lname21.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname21);
         pstmt1.setString(2, parm.fname21);
         pstmt1.setString(3, parm.mi21);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user21 = rs.getString(1);
            parm.mship21 = rs.getString(2);
            parm.mtype21 = rs.getString(3);
            parm.mNum21 = rs.getString(4);

            members++;         // increment number of members this res.
            memg5++;           // group 5
         } else {
            inval21 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname22.equals( "" )) && (!parm.lname22.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname22);
         pstmt1.setString(2, parm.fname22);
         pstmt1.setString(3, parm.mi22);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user22 = rs.getString(1);
            parm.mship22 = rs.getString(2);
            parm.mtype22 = rs.getString(3);
            parm.mNum22 = rs.getString(4);

            members++;         // increment number of members this res.
            memg5++;           // group 5
         } else {
            inval22 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname23.equals( "" )) && (!parm.lname23.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname23);
         pstmt1.setString(2, parm.fname23);
         pstmt1.setString(3, parm.mi23);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user23 = rs.getString(1);
            parm.mship23 = rs.getString(2);
            parm.mtype23 = rs.getString(3);
            parm.mNum23 = rs.getString(4);

            members++;         // increment number of members this res.
            memg5++;           // group 5
         } else {
            inval23 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname24.equals( "" )) && (!parm.lname24.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname24);
         pstmt1.setString(2, parm.fname24);
         pstmt1.setString(3, parm.mi24);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user24 = rs.getString(1);
            parm.mship24 = rs.getString(2);
            parm.mtype24 = rs.getString(3);
            parm.mNum24 = rs.getString(4);

            members++;         // increment number of members this res.
            memg5++;           // group 5
         } else {
            inval24 = 1;        // indicate invalid name entered
         }
      }

      if ((!parm.fname25.equals( "" )) && (!parm.lname25.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, parm.lname25);
         pstmt1.setString(2, parm.fname25);
         pstmt1.setString(3, parm.mi25);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.user25 = rs.getString(1);
            parm.mship25 = rs.getString(2);
            parm.mtype25 = rs.getString(3);
            parm.mNum25 = rs.getString(4);

            members++;         // increment number of members this res.
            memg5++;           // group 5
         } else {
            inval25 = 1;        // indicate invalid name entered
         }
      }

      pstmt1.close();

   }
   catch (Exception ignore) {
   }

   //
   //  Save member counts in parm
   //
   parm.members = members;          // total # of members in request
   parm.memg1 = memg1;                // # of mems in each group
   parm.memg2 = memg2;
   parm.memg3 = memg3;
   parm.memg4 = memg4;
   parm.memg5 = memg5;

   //
   //  Save the members' usernames for guest association
   //
   memA[0] = parm.user1;
   memA[1] = parm.user2;
   memA[2] = parm.user3;
   memA[3] = parm.user4;
   memA[4] = parm.user5;
   memA[5] = parm.user6;
   memA[6] = parm.user7;
   memA[7] = parm.user8;
   memA[8] = parm.user9;
   memA[9] = parm.user10;
   memA[10] = parm.user11;
   memA[11] = parm.user12;
   memA[12] = parm.user13;
   memA[13] = parm.user14;
   memA[14] = parm.user15;
   memA[15] = parm.user16;
   memA[16] = parm.user17;
   memA[17] = parm.user18;
   memA[18] = parm.user19;
   memA[19] = parm.user20;
   memA[20] = parm.user21;
   memA[21] = parm.user22;
   memA[22] = parm.user23;
   memA[23] = parm.user24;
   memA[24] = parm.user25;

   //
   //  Check if any of the names are invalid.  If so, ask Member if they want to ignore the error.
   //
   if (inval25 != 0) {

      err_name = parm.player25;
   }
   if (inval24 != 0) {

      err_name = parm.player24;
   }
   if (inval23 != 0) {

      err_name = parm.player23;
   }
   if (inval22 != 0) {

      err_name = parm.player22;
   }
   if (inval21 != 0) {

      err_name = parm.player21;
   }
   if (inval20 != 0) {

      err_name = parm.player20;
   }
   if (inval19 != 0) {

      err_name = parm.player19;
   }
   if (inval18 != 0) {

      err_name = parm.player18;
   }
   if (inval17 != 0) {

      err_name = parm.player17;
   }
   if (inval16 != 0) {

      err_name = parm.player16;
   }
   if (inval15 != 0) {

      err_name = parm.player15;
   }
   if (inval14 != 0) {

      err_name = parm.player14;
   }
   if (inval13 != 0) {

      err_name = parm.player13;
   }
   if (inval12 != 0) {

      err_name = parm.player12;
   }
   if (inval11 != 0) {

      err_name = parm.player11;
   }
   if (inval10 != 0) {

      err_name = parm.player10;
   }
   if (inval9 != 0) {

      err_name = parm.player9;
   }
   if (inval8 != 0) {

      err_name = parm.player8;
   }
   if (inval7 != 0) {

      err_name = parm.player7;
   }
   if (inval6 != 0) {

      err_name = parm.player6;
   }
   if (inval5 != 0) {

      err_name = parm.player5;
   }
   if (inval4 != 0) {

      err_name = parm.player4;
   }
   if (inval3 != 0) {

      err_name = parm.player3;
   }
   if (inval2 != 0) {

      err_name = parm.player2;
   }
   if (inval1 != 0) {

      err_name = parm.player1;
   }

   if (!err_name.equals( "" )) {      // invalid name received

      msgHdr = "Player's Name Not Found in System";
      
      msgBody = "Warning:  " + err_name + " does not exist in the system database.<BR><BR>Please use the correct name (from Member List) or specify a guest.";
      
      buildError(msgHdr, msgBody, mobile, out);       // output the error message

      //
      //  Return to _lott
      //
      goReturn(out, parm);
      return;
   }
   
      
   // Ensure that Pro-Only modes of transportation aren't being used without permission
   if (!checkTmodes(con, out, parm)) {
       
       msgHdr = "Access Error";
      
       msgBody = "<b>'" + parm.player + "'</b> is not authorized to use that mode of transportation.<BR><BR>Please select another mode of transportation.";
      
       buildError(msgHdr, msgBody, mobile, out);       // output the error message

       goReturn(out, parm);
       return;
   }

   //
   //************************************************************************
   //  Check for minimum number of members & players
   //************************************************************************
   //
   try {
      //
      PreparedStatement pstmtl3 = con.prepareStatement (
               "SELECT members, players FROM lottery3 WHERE name = ?");

      pstmtl3.clearParameters();        // clear the parms
      pstmtl3.setString(1, lottName);       // put the parm in stmt
      rs = pstmtl3.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         minMembers = rs.getInt(1);    // minimum # of members per request
         minPlayers = rs.getInt(2);    // minimum # of players per request
      }
      pstmtl3.close();              // close the stmt

      if (minMembers > 0) {

         if (p5.equals( "No" )) {

            if (minMembers > 4) {

               minMembers = 4;         // reduce for 4-somes
            }
         }
         //
         //  Reject request if not enough members in request
         //
         if (members < minMembers) {

            msgHdr = "Not Enough Members in Request";

            msgBody = "Warning: Your request does not contain the minimum number of members required.<BR><BR>Your request contains " + members + " members but you need " + minMembers + ".";

            buildError(msgHdr, msgBody, mobile, out);       // output the error message

            //
            //  Return to _lott
            //
            goReturn(out, parm);
            return;
         }
      }
      if (minPlayers > 0) {

         if (p5.equals( "No" )) {

            if (minPlayers > 4) {

               minPlayers = 4;         // reduce for 4-somes
            }
         }
         //
         //  Reject request if not enough members in request
         //
         if (players < minPlayers) {

            msgHdr = "Not Enough Players in Request";

            msgBody = "Warning: Your request does not contain the minimum number of players required.<BR><BR>Your request contains " + players + " members but you need " + minPlayers + ".";

            buildError(msgHdr, msgBody, mobile, out);       // output the error message

            //
            //  Return to _lott
            //
            goReturn(out, parm);
            return;
         }
      }
   }
   catch (Exception ignore) {
   }

   if (club.equals( "westchester" )) {

      // ***************************************************************************************
      //  Custom check for Westchester - Sat & Sun before 2 PM - no singles or 2-somes!!
      // ***************************************************************************************
      //
      if ((parm.day.equalsIgnoreCase( "saturday" ) || parm.day.equalsIgnoreCase( "sunday" )) &&
           parm.time < 1400) {

         if ((members + parm.guests) < 3) {    // if total # of members & guests are < 3

            msgHdr = "Insufficient Number of Players";

            msgBody = "Sorry, you have not specified enough players for this day and time.<BR><BR>All requests must include at least 3 players on Sat & Sun before 2 PM.";

            buildError(msgHdr, msgBody, mobile, out);       // output the error message

            //
            //  Return to _lott
            //
            goReturn(out, parm);
            return;
         }
      }
   }
      
   //
   //************************************************************************
   //  Check any membership types for max rounds per week, month or year
   //************************************************************************
   //
   if (!parm.mship1.equals( "" ) ||
       !parm.mship2.equals( "" ) ||
       !parm.mship3.equals( "" ) ||
       !parm.mship4.equals( "" ) ||
       !parm.mship5.equals( "" ) ||
       !parm.mship6.equals( "" ) ||
       !parm.mship7.equals( "" ) ||
       !parm.mship8.equals( "" ) ||
       !parm.mship9.equals( "" ) ||
       !parm.mship10.equals( "" ) ||
       !parm.mship11.equals( "" ) ||
       !parm.mship12.equals( "" ) ||
       !parm.mship13.equals( "" ) ||
       !parm.mship14.equals( "" ) ||
       !parm.mship15.equals( "" ) ||
       !parm.mship16.equals( "" ) ||
       !parm.mship17.equals( "" ) ||
       !parm.mship18.equals( "" ) ||
       !parm.mship19.equals( "" ) ||
       !parm.mship20.equals( "" ) ||
       !parm.mship21.equals( "" ) ||
       !parm.mship22.equals( "" ) ||
       !parm.mship23.equals( "" ) ||
       !parm.mship24.equals( "" ) ||
       !parm.mship25.equals( "" )) {                // if at least one name exists then check number of rounds

      //
      // *******************************************************************************
      //  Check Membership Restrictions for Max Rounds
      // *******************************************************************************
      //
      check = checkMemship(con, out, parm, day);      // go check

      if (parm.error == true) {          // if we hit a db error

         return;
      }

      if (check == true) {      // a member exceed the max allowed tee times per month

         msgHdr = "Member Exceeded Limit";

         msgBody = "Warning:  " + parm.player + " is a " + parm.mship + " member and has exceeded the<BR>maximum number of tee times allowed for this " + parm.period + ".";

         buildError(msgHdr, msgBody, mobile, out);       // output the error message

         //
         //  Return to _lott
         //
         goReturn(out, parm);
         return;
      }
   }      // end of mship if

   //
   //************************************************************************
   //  Check for max # of guests exceeded (per member or per tee time)
   //************************************************************************
   //
   if (parm.guests > 0) {              // if any guests were included

      guestError = Common_Lott.checkGuests(parm, con, parm.guests);      // go check in Common_Lott (updated method)

      if (guestError == true) {         // if too many guests

         msgHdr = "Number of Guests Exceeded Limit";

         msgBody = "Sorry, the maximum number of guests allowed for the<BR>time you are requesting is " + parm.grest_num + " per " +parm.period+ ".";

         buildError(msgHdr, msgBody, mobile, out);       // output the error message

         //
         //  Return to _lott
         //
         goReturn(out, parm);
         return;
      }

      
      //
      //  Brookhaven Custom - no guests allowed
      //
      if (club.equals( "brookhavenclub" )) {       // Brookhaven - guests NOT allowed in lottery reqs
      
         msgHdr = "Number of Guests Exceeded Limit";

         msgBody = "Sorry, guests are not allowed in lottery requests.<BR><BR>Please contact the golf shop to make a lottery request with guests.";

         buildError(msgHdr, msgBody, mobile, out);       // output the error message

         //
         //  Return to _lott
         //
         goReturn(out, parm);
         return;
      }
                           
   }     // end of IF guests


   //
   // *******************************************************************************
   //  Check Member Restrictions
   // *******************************************************************************
   //
   check = checkMemRes(con, out, parm, day);      // go check

   if (parm.error == true) {          // if we hit a db error

      return;
   }

   if (check == true) {          // if we hit on a restriction

      msgHdr = "Member Restricted";

      msgBody = "Sorry, <b>" + parm.player + "</b> is restricted from playing during this time.<BR><BR>This time slot has the following restriction:  <b>" + parm.rest_name + "</b>.";

      buildError(msgHdr, msgBody, mobile, out);       // output the error message

      //
      //  Return to _lott
      //
      goReturn(out, parm);
      return;
   }

   if (club.equals( "cherryhills" )) {

      //
      // *******************************************************************************
      //  Cherry Hills - custom member type and membership restrictions
      // *******************************************************************************
      //
      check = checkCherryRes(parm);         // go check

      if (check == true) {          // if we hit on a restriction

         msgHdr = "Player Not Allowed";

         msgBody = "Sorry, one or more players are not allowed to be part of a tee time for this day and time.";

         if (day.equals( "Monday" ) || day.equals( "Wednesday" ) || day.equals( "Friday" )) {
            msgBody += "<BR><BR>A Member must be included when making the request more than 1 day in advance.";
         } else {
            if (day.equals( "Tuesday" )) {
               if (time > 1100) {
                  msgBody += "<BR><BR>A Member must be included when making the request more than 1 day in advance.";
               } else {
                  msgBody += "<BR><BR>Only Spouses may make a request more than 1 day in advance for a tee time before 11 AM on Tuesdays.";
               }
            } else {
               if (day.equals( "Thursday" )) {
                  if (time > 1000) {
                     msgBody += "<BR><BR>A Member must be included when making the request more than 1 day in advance.";
                  } else {
                     msgBody += "<BR><BR>Only Spouses may make a request more than 1 day in advance for a tee time before 10 AM on Thursdays.";
                  }
               } else {
                  if (day.equals( "Sunday" )) {
                     if (time > 1000) {
                        msgBody += "<BR><BR>A Member must be included when making the request more than 1 day in advance.";
                     } else {
                        msgBody += "<BR><BR>Only Members may be included in a tee time before 10 AM on Sundays.";
                     }
                  } else {       // Saturday or Holiday
                     if (time > 1100) {
                        msgBody += "<BR><BR>A Member must be included when making the request more than 1 day in advance.";
                     } else {
                        msgBody += "<BR><BR>Player not allowed to make a tee time more than 24 hours in advance on Saturdays and Holidays before 11 AM.";
                     }
                  }
               }
            }
         }
         
         buildError(msgHdr, msgBody, mobile, out);       // output the error message

         //
         //  Return to _lott
         //
         goReturn(out, parm);
         return;
      }
        
      //
      //  Cherry Hills - check for Juniors with guests and no parent
      //
      if (parm.guests > 0) {                // if guests included

         check = checkCherryJrs(parm);      // go check

         if (check == true) {               // no guests allowed w/o an adult

            msgHdr = "Guests Not Allowed";

            msgBody = "Sorry, but you are not allowed to request a time with guests when an adult is not included.<BR><BR>Please remove the guests or add an adult.";

            buildError(msgHdr, msgBody, mobile, out);       // output the error message

            goReturn(out, parm);
            return;
         }
      }
        
   }       // end of IF Cherry Hills

   //
   // Custom for Mirasol CC - 'Tennant Lease' members cannot submit lottery requests
   //
   if (club.equals("mirasolcc")) {

       boolean error2 = false;

       if (!error2 && parm.mship1.equals("Tennant Lease")) {
           error2 = true;
           parm.player = parm.player1;
       }

       if (!error2 && parm.mship2.equals("Tennant Lease")) {
           error2 = true;
           parm.player = parm.player2;
       }

       if (!error2 && parm.mship3.equals("Tennant Lease")) {
           error2 = true;
           parm.player = parm.player3;
       }

       if (!error2 && parm.mship4.equals("Tennant Lease")) {
           error2 = true;
           parm.player = parm.player4;
       }

       if (!error2 && parm.mship5.equals("Tennant Lease")) {
           error2 = true;
           parm.player = parm.player5;
       }

       if (error2) {
           
           msgHdr = "Member Restricted";

           msgBody = "Sorry, <b>" + parm.player + "</b> is restricted from submitting lottery requests.";

           buildError(msgHdr, msgBody, mobile, out);       // output the error message

           //  Return to _lott
           goReturn(out, parm);
           return;
       }

   }

   //
   // *******************************************************************************
   //  Check Member Number restrictions
   //
   // *******************************************************************************
   //
   check = checkMemNum(con, out, parm, day);      // go check

   if (parm.error == true) {          // if we hit a db error

      return;
   }

   if (check == true) {          // if we hit on a restriction
      
      String mnumtemp = "";

      if (!parm.pNum1.equals( "" )) {
         mnumtemp = parm.pNum1;
      }
      if (!parm.pNum2.equals( "" )) {
         mnumtemp = parm.pNum2;
      }
      if (!parm.pNum3.equals( "" )) {
         mnumtemp = parm.pNum3;
      }
      if (!parm.pNum4.equals( "" )) {
         mnumtemp = parm.pNum4;
      }
      if (!parm.pNum5.equals( "" )) {
         mnumtemp = parm.pNum5;
      }
      if (!parm.pNum6.equals( "" )) {
         mnumtemp = parm.pNum6;
      }
      if (!parm.pNum7.equals( "" )) {
         mnumtemp = parm.pNum7;
      }
      if (!parm.pNum8.equals( "" )) {
         mnumtemp = parm.pNum8;
      }
      if (!parm.pNum9.equals( "" )) {
         mnumtemp = parm.pNum9;
      }
      if (!parm.pNum10.equals( "" )) {
         mnumtemp = parm.pNum10;
      }
      if (!parm.pNum11.equals( "" )) {
         mnumtemp = parm.pNum11;
      }
      if (!parm.pNum12.equals( "" )) {
         mnumtemp = parm.pNum12;
      }
      if (!parm.pNum13.equals( "" )) {
         mnumtemp = parm.pNum13;
      }
      if (!parm.pNum14.equals( "" )) {
         mnumtemp = parm.pNum14;
      }
      if (!parm.pNum15.equals( "" )) {
         mnumtemp = parm.pNum15;
      }
      if (!parm.pNum16.equals( "" )) {
         mnumtemp = parm.pNum16;
      }
      if (!parm.pNum17.equals( "" )) {
         mnumtemp = parm.pNum17;
      }
      if (!parm.pNum18.equals( "" )) {
         mnumtemp = parm.pNum18;
      }
      if (!parm.pNum19.equals( "" )) {
         mnumtemp = parm.pNum19;
      }
      if (!parm.pNum20.equals( "" )) {
         mnumtemp = parm.pNum20;
      }
      if (!parm.pNum21.equals( "" )) {
         mnumtemp = parm.pNum21;
      }
      if (!parm.pNum22.equals( "" )) {
         mnumtemp = parm.pNum22;
      }
      if (!parm.pNum23.equals( "" )) {
         mnumtemp = parm.pNum23;
      }
      if (!parm.pNum24.equals( "" )) {
         mnumtemp = parm.pNum24;
      }
      if (!parm.pNum25.equals( "" )) {
         mnumtemp = parm.pNum25;
      }
      
      msgHdr = "Member Restricted";

      msgBody = "Sorry, <b>" +mnumtemp+ "</b> is/are restricted from playing during this time because the<BR>number of members with the same member number has exceeded the maximum allowed.";

      buildError(msgHdr, msgBody, mobile, out);       // output the error message

      //
      //  Return to _lott
      //
      goReturn(out, parm);
      return;
   }

   //
   //***********************************************************************************************
   //
   //    Now check if any of the players are already scheduled today (only 1 res per day)
   //
   //***********************************************************************************************
   //
   hit = 0;

   if (!parm.player1.equals( "" ) && parm.g[0].equals( "" )) {

      player = parm.player1;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player2.equals( "" ) && parm.g[1].equals( "" ) && hit == 0) {

      player = parm.player2;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player3.equals( "" ) && parm.g[2].equals( "" ) && hit == 0) {

      player = parm.player3;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player4.equals( "" ) && parm.g[3].equals( "" ) && hit == 0) {

      player = parm.player4;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player5.equals( "" ) && parm.g[4].equals( "" ) && hit == 0) {

      player = parm.player5;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player6.equals( "" ) && parm.g[5].equals( "" ) && hit == 0) {

      player = parm.player6;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player7.equals( "" ) && parm.g[6].equals( "" ) && hit == 0) {

      player = parm.player7;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player8.equals( "" ) && parm.g[7].equals( "" ) && hit == 0) {

      player = parm.player8;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player9.equals( "" ) && parm.g[8].equals( "" ) && hit == 0) {

      player = parm.player9;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player10.equals( "" ) && parm.g[9].equals( "" ) && hit == 0) {

      player = parm.player10;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player11.equals( "" ) && parm.g[10].equals( "" ) && hit == 0) {

      player = parm.player11;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player12.equals( "" ) && parm.g[11].equals( "" ) && hit == 0) {

      player = parm.player12;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player13.equals( "" ) && parm.g[12].equals( "" ) && hit == 0) {

      player = parm.player13;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player14.equals( "" ) && parm.g[13].equals( "" ) && hit == 0) {

      player = parm.player14;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player15.equals( "" ) && parm.g[14].equals( "" ) && hit == 0) {

      player = parm.player15;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player16.equals( "" ) && parm.g[15].equals( "" ) && hit == 0) {

      player = parm.player16;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player17.equals( "" ) && parm.g[16].equals( "" ) && hit == 0) {

      player = parm.player17;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player18.equals( "" ) && parm.g[17].equals( "" ) && hit == 0) {

      player = parm.player18;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player19.equals( "" ) && parm.g[18].equals( "" ) && hit == 0) {

      player = parm.player19;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player20.equals( "" ) && parm.g[19].equals( "" ) && hit == 0) {

      player = parm.player20;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player21.equals( "" ) && parm.g[20].equals( "" ) && hit == 0) {

      player = parm.player21;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player22.equals( "" ) && parm.g[21].equals( "" ) && hit == 0) {

      player = parm.player22;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player23.equals( "" ) && parm.g[22].equals( "" ) && hit == 0) {

      player = parm.player23;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player24.equals( "" ) && parm.g[23].equals( "" ) && hit == 0) {

      player = parm.player24;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (!parm.player25.equals( "" ) && parm.g[24].equals( "" ) && hit == 0) {

      player = parm.player25;              // get player for message

      hit = chkPlayer(con, player, date, time, fb, course, lottid);
   }

   if (hit != 0) {          // if we hit on a duplicate res

      msgHdr = "Member Already Playing";

      msgBody = "Sorry, <b>" + player + "</b> is already scheduled to play on this date.<br><br>";

      if (hit == 1) {
         msgBody += "The player is already scheduled the maximum number of times allowed per day.";
      } else {
         if (hit == 2) {
            msgBody += "The player has another tee time that is too close to the time requested.";
         } else {
            msgBody += "The player has another request that is too close to the time of this request.";
         }
      }
      
      buildError(msgHdr, msgBody, mobile, out);       // output the error message

      //
      //  Return to _lott
      //
      goReturn(out, parm);
      return;
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
      //        members = # of members requested
      //        guests  = # of guests requested
      //***********************************************************************************************
      //
      for (i = 0; i < 25; i++) {           // init the arrays

         userg[i] = "";
         parm.userg[i] = "";
         usergA[i] = "";
      }

      if (parm.guests > 0) {

         if (members > 0) {             // if at least one member

            //
            //  Both guests and members specified - determine guest owners by order
            //
            memberName = "";

            for (gi = 0; gi < 25; gi++) {            // cycle thru arrays and find guests/members

               if (!parm.gstA[gi].equals( "" )) {    // if player is a guest

                  usergA[gi] = memberName;           // get last member's username
               } else {
                  usergA[gi] = "";                   // not a guest
                  memberName = memA[gi];             // get member's username
               }
            }
              
            for (i = 0; i < 25; i++) {

               userg[i] = usergA[i];             // set usernames for guests in teecurr
               parm.userg[i] = usergA[i];        // save in parms
            }
         }

         if (members > 1 || !parm.gstA[0].equals( "" )) {  // if multiple members OR slot 1 is a guest

            //
            //  At least one guest and one member have been specified.
            //  Prompt user to verify the order.
            //
            
            msgHdr = "Player/Guest Association Prompt";

            msgBody = "Guests must be specified <b>immediately after</b> the member they belong to.<br><br>" +
                      "Please verify that the following order is correct:<br><br>" +parm.player1 + " <BR>" + parm.player2 + " <BR>";

            
            if (!parm.player3.equals( "" )) {
               msgBody += parm.player3 + " <BR>";
            }
            if (!parm.player4.equals( "" )) {
               msgBody += parm.player4 + " <BR>";
            }
            if (!parm.player5.equals( "" )) {
               msgBody += parm.player5 + " <BR>";
            }
            if (!parm.player6.equals( "" )) {
               msgBody += parm.player6 + " <BR>";
            }
            if (!parm.player7.equals( "" )) {
               msgBody += parm.player7 + " <BR>";
            }
            if (!parm.player8.equals( "" )) {
               msgBody += parm.player8 + " <BR>";
            }
            if (!parm.player9.equals( "" )) {
               msgBody += parm.player9 + " <BR>";
            }
            if (!parm.player10.equals( "" )) {
               msgBody += parm.player10 + " <BR>";
            }
            if (!parm.player11.equals( "" )) {
               msgBody += parm.player11 + " <BR>";
            }
            if (!parm.player12.equals( "" )) {
               msgBody += parm.player12 + " <BR>";
            }
            if (!parm.player13.equals( "" )) {
               msgBody += parm.player13 + " <BR>";
            }
            if (!parm.player14.equals( "" )) {
               msgBody += parm.player14 + " <BR>";
            }
            if (!parm.player15.equals( "" )) {
               msgBody += parm.player15 + " <BR>";
            }
            if (!parm.player16.equals( "" )) {
               msgBody += parm.player16 + " <BR>";
            }
            if (!parm.player17.equals( "" )) {
               msgBody += parm.player17 + " <BR>";
            }
            if (!parm.player18.equals( "" )) {
               msgBody += parm.player18 + " <BR>";
            }
            if (!parm.player19.equals( "" )) {
               msgBody += parm.player19 + " <BR>";
            }
            if (!parm.player20.equals( "" )) {
               msgBody += parm.player20 + " <BR>";
            }
            if (!parm.player21.equals( "" )) {
               msgBody += parm.player21 + " <BR>";
            }
            if (!parm.player22.equals( "" )) {
               msgBody += parm.player22 + " <BR>";
            }
            if (!parm.player23.equals( "" )) {
               msgBody += parm.player23 + " <BR>";
            }
            if (!parm.player24.equals( "" )) {
               msgBody += parm.player24 + " <BR>";
            }
            if (!parm.player25.equals( "" )) {
               msgBody += parm.player25 + " <BR>";
            }        
            
            msgBody += "<BR>Would you like to process the request as is?";
            
            buildError(msgHdr, msgBody, mobile, out);       // output the error message

            //
            //  Return to _lott to change the player order
            //
            goReturn2(out, parm);
            return;

         }   // end of IF more than 1 member specified OR guest name in slot #1
      }      // end of IF any guests specified

   } else {   // skip 8 requested
      //
      //  User has responded to the guest association prompt - process tee time request in specified order
      //
      for (i = 0; i < 25; i++) {
         userg[i] = req.getParameter("userg" + i);
         parm.userg[i] = userg[i];                   // save in parms
      }
   }         // end of IF skip8

   //
   //***********************************************************************************************
   //
   //  Now that the guests are assigned, check for any Guest Quotas - if any guests requested
   //
   //***********************************************************************************************
   //
   boolean guest_ass = false;
     
   //  Any guests assigned?
   for (i = 0; i < 25; i++) {
      if (!userg[i].equals( "" )) {
         guest_ass = true;
      }
   }

   if (guest_ass == true) {
     
      check = Common_Lott.checkGuestQuota(parm, con);      // go check
   }

   if (check == true) {          // if we hit on a violation

      msgHdr = "Guest Quota Exceeded for Member";

      msgBody = "Sorry, requesting <b>" + parm.player + "</b> exceeds the guest quota established by the Golf Shop." +
                "<br><br>You will have to remove the guest in order to complete this request.";

      buildError(msgHdr, msgBody, mobile, out);       // output the error message

      //
      //  Return to _lott
      //
      goReturn(out, parm);
      return;
   }              


   //
   //  Verification complete -
   //   Enter request and Send email notifications of request
   //
   sendEmail = 0;         // init email flags
   emailNew = 0;
   emailMod = 0;

   //
   //  If any player has changed, then set email flag
   //
   if ((!parm.player1.equals( parm.oldplayer1 )) || (!parm.player2.equals( parm.oldplayer2 )) || (!parm.player3.equals( parm.oldplayer3 )) ||
       (!parm.player4.equals( parm.oldplayer4 )) || (!parm.player5.equals( parm.oldplayer5 )) || (!parm.player6.equals( parm.oldplayer6 )) ||
       (!parm.player7.equals( parm.oldplayer7 )) || (!parm.player8.equals( parm.oldplayer8 )) || (!parm.player9.equals( parm.oldplayer9 )) ||
       (!parm.player10.equals( parm.oldplayer10 )) || (!parm.player11.equals( parm.oldplayer11 )) || (!parm.player12.equals( parm.oldplayer12 )) ||
       (!parm.player13.equals( parm.oldplayer13 )) || (!parm.player14.equals( parm.oldplayer14 )) || (!parm.player15.equals( parm.oldplayer15 )) ||
       (!parm.player16.equals( parm.oldplayer16 )) || (!parm.player17.equals( parm.oldplayer17 )) || (!parm.player18.equals( parm.oldplayer18 )) ||
       (!parm.player19.equals( parm.oldplayer19 )) || (!parm.player20.equals( parm.oldplayer20 )) || (!parm.player21.equals( parm.oldplayer21 )) ||
       (!parm.player22.equals( parm.oldplayer22 )) || (!parm.player23.equals( parm.oldplayer23 )) || (!parm.player24.equals( parm.oldplayer24 )) ||
       (!parm.player25.equals( parm.oldplayer25 ))) {

      sendEmail = 1;    // player changed - send email notification to all
   }

   //
   //  get time values
   //
   hr = time / 100;            // get hour value
   min = time - (hr * 100);    // get minute value

   //
   //  Check if new request or update
   //
   if (lottid != 0) {

      memMod++;      // increment number of mods
      emailMod = 1;  // tee time was modified

      //
      //   Update the Lottery Request
      //
      try {
         PreparedStatement pstmt6 = con.prepareStatement (
            "UPDATE lreqs3 SET hr = ?, min = ?, time = ?, minsbefore = ?, minsafter = ?, " +
            "player1 = ?, player2 = ?, player3 = ?, player4 = ?, player5 = ?, " +
            "player6 = ?, player7 = ?, player8 = ?, player9 = ?, player10 = ?, " +
            "player11 = ?, player12 = ?, player13 = ?, player14 = ?, player15 = ?, " +
            "player16 = ?, player17 = ?, player18 = ?, player19 = ?, player20 = ?, " +
            "player21 = ?, player22 = ?, player23 = ?, player24 = ?, player25 = ?, " +
            "user1 = ?, user2 = ?, user3 = ?, user4 = ?, user5 = ?, " +
            "user6 = ?, user7 = ?, user8 = ?, user9 = ?, user10 = ?, " +
            "user11 = ?, user12 = ?, user13 = ?, user14 = ?, user15 = ?, " +
            "user16 = ?, user17 = ?, user18 = ?, user19 = ?, user20 = ?, " +
            "user21 = ?, user22 = ?, user23 = ?, user24 = ?, user25 = ?, " +
            "p1cw = ?, p2cw = ?, p3cw = ?, p4cw = ?, p5cw = ?, " +
            "p6cw = ?, p7cw = ?, p8cw = ?, p9cw = ?, p10cw = ?, " +
            "p11cw = ?, p12cw = ?, p13cw = ?, p14cw = ?, p15cw = ?, " +
            "p16cw = ?, p17cw = ?, p18cw = ?, p19cw = ?, p20cw = ?, " +
            "p21cw = ?, p22cw = ?, p23cw = ?, p24cw = ?, p25cw = ?, " +
            "notes = ?, fb = ?, courseName = ?, memMod = ?, in_use = 0, groups = ?, p5 = ?, " +
            "players = ?, userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, userg6 = ?, " +
            "userg7 = ?, userg8 = ?, userg9 = ?, userg10 = ?, userg11 = ?, userg12 = ?, userg13 = ?, " +
            "userg14 = ?, userg15 = ?, userg16 = ?, userg17 = ?, userg18 = ?, userg19 = ?, userg20 = ?, " +
            "userg21 = ?, userg22 = ?, userg23 = ?, userg24 = ?, userg25 = ?, orig_by = ?, " +
            "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, p96 = ?, " +
            "p97 = ?, p98 = ?, p99 = ?, p910 = ?, p911 = ?, p912 = ?, p913 = ?, " +
            "p914 = ?, p915 = ?, p916 = ?, p917 = ?, p918 = ?, p919 = ?, p920 = ?, " +
            "p921 = ?, p922 = ?, p923 = ?, p924 = ?, p925 = ?, checkothers = ?, courseReq = ?, " +
            "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ?, " +
            "guest_id6 = ?, guest_id7 = ?, guest_id8 = ?, guest_id9 = ?, guest_id10 = ?, " +
            "guest_id11 = ?, guest_id12 = ?, guest_id13 = ?, guest_id14 = ?, guest_id15 = ?, " +
            "guest_id16 = ?, guest_id17 = ?, guest_id18 = ?, guest_id19 = ?, guest_id20 = ?, " +
            "guest_id21 = ?, guest_id22 = ?, guest_id23 = ?, guest_id24 = ?, guest_id25 = ? " +
            "WHERE id = ?");

         pstmt6.clearParameters();        // clear the parms
         pstmt6.setInt(1, hr);
         pstmt6.setInt(2, min);
         pstmt6.setInt(3, time);
         pstmt6.setInt(4, mins_before);
         pstmt6.setInt(5, mins_after);
         pstmt6.setString(6, parm.player1);
         pstmt6.setString(7, parm.player2);
         pstmt6.setString(8, parm.player3);
         pstmt6.setString(9, parm.player4);
         pstmt6.setString(10, parm.player5);
         pstmt6.setString(11, parm.player6);
         pstmt6.setString(12, parm.player7);
         pstmt6.setString(13, parm.player8);
         pstmt6.setString(14, parm.player9);
         pstmt6.setString(15, parm.player10);
         pstmt6.setString(16, parm.player11);
         pstmt6.setString(17, parm.player12);
         pstmt6.setString(18, parm.player13);
         pstmt6.setString(19, parm.player14);
         pstmt6.setString(20, parm.player15);
         pstmt6.setString(21, parm.player16);
         pstmt6.setString(22, parm.player17);
         pstmt6.setString(23, parm.player18);
         pstmt6.setString(24, parm.player19);
         pstmt6.setString(25, parm.player20);
         pstmt6.setString(26, parm.player21);
         pstmt6.setString(27, parm.player22);
         pstmt6.setString(28, parm.player23);
         pstmt6.setString(29, parm.player24);
         pstmt6.setString(30, parm.player25);
         pstmt6.setString(31, parm.user1);
         pstmt6.setString(32, parm.user2);
         pstmt6.setString(33, parm.user3);
         pstmt6.setString(34, parm.user4);
         pstmt6.setString(35, parm.user5);
         pstmt6.setString(36, parm.user6);
         pstmt6.setString(37, parm.user7);
         pstmt6.setString(38, parm.user8);
         pstmt6.setString(39, parm.user9);
         pstmt6.setString(40, parm.user10);
         pstmt6.setString(41, parm.user11);
         pstmt6.setString(42, parm.user12);
         pstmt6.setString(43, parm.user13);
         pstmt6.setString(44, parm.user14);
         pstmt6.setString(45, parm.user15);
         pstmt6.setString(46, parm.user16);
         pstmt6.setString(47, parm.user17);
         pstmt6.setString(48, parm.user18);
         pstmt6.setString(49, parm.user19);
         pstmt6.setString(50, parm.user20);
         pstmt6.setString(51, parm.user21);
         pstmt6.setString(52, parm.user22);
         pstmt6.setString(53, parm.user23);
         pstmt6.setString(54, parm.user24);
         pstmt6.setString(55, parm.user25);
         pstmt6.setString(56, parm.pcw1);
         pstmt6.setString(57, parm.pcw2);
         pstmt6.setString(58, parm.pcw3);
         pstmt6.setString(59, parm.pcw4);
         pstmt6.setString(60, parm.pcw5);
         pstmt6.setString(61, parm.pcw6);
         pstmt6.setString(62, parm.pcw7);
         pstmt6.setString(63, parm.pcw8);
         pstmt6.setString(64, parm.pcw9);
         pstmt6.setString(65, parm.pcw10);
         pstmt6.setString(66, parm.pcw11);
         pstmt6.setString(67, parm.pcw12);
         pstmt6.setString(68, parm.pcw13);
         pstmt6.setString(69, parm.pcw14);
         pstmt6.setString(70, parm.pcw15);
         pstmt6.setString(71, parm.pcw16);
         pstmt6.setString(72, parm.pcw17);
         pstmt6.setString(73, parm.pcw18);
         pstmt6.setString(74, parm.pcw19);
         pstmt6.setString(75, parm.pcw20);
         pstmt6.setString(76, parm.pcw21);
         pstmt6.setString(77, parm.pcw22);
         pstmt6.setString(78, parm.pcw23);
         pstmt6.setString(79, parm.pcw24);
         pstmt6.setString(80, parm.pcw25);
         pstmt6.setString(81, notes);
         pstmt6.setInt(82, fb);
         pstmt6.setString(83, course);
         pstmt6.setInt(84, memMod);
         pstmt6.setInt(85, slots);
         pstmt6.setString(86, p5);
         pstmt6.setInt(87, players);
         pstmt6.setString(88, userg[0]);
         pstmt6.setString(89, userg[1]);
         pstmt6.setString(90, userg[2]);
         pstmt6.setString(91, userg[3]);
         pstmt6.setString(92, userg[4]);
         pstmt6.setString(93, userg[5]);
         pstmt6.setString(94, userg[6]);
         pstmt6.setString(95, userg[7]);
         pstmt6.setString(96, userg[8]);
         pstmt6.setString(97, userg[9]);
         pstmt6.setString(98, userg[10]);
         pstmt6.setString(99, userg[11]);
         pstmt6.setString(100, userg[12]);
         pstmt6.setString(101, userg[13]);
         pstmt6.setString(102, userg[14]);
         pstmt6.setString(103, userg[15]);
         pstmt6.setString(104, userg[16]);
         pstmt6.setString(105, userg[17]);
         pstmt6.setString(106, userg[18]);
         pstmt6.setString(107, userg[19]);
         pstmt6.setString(108, userg[20]);
         pstmt6.setString(109, userg[21]);
         pstmt6.setString(110, userg[22]);
         pstmt6.setString(111, userg[23]);
         pstmt6.setString(112, userg[24]);
         pstmt6.setString(113, orig_by);
         pstmt6.setInt(114, parm.p91);
         pstmt6.setInt(115, parm.p92);
         pstmt6.setInt(116, parm.p93);
         pstmt6.setInt(117, parm.p94);
         pstmt6.setInt(118, parm.p95);
         pstmt6.setInt(119, parm.p96);
         pstmt6.setInt(120, parm.p97);
         pstmt6.setInt(121, parm.p98);
         pstmt6.setInt(122, parm.p99);
         pstmt6.setInt(123, parm.p910);
         pstmt6.setInt(124, parm.p911);
         pstmt6.setInt(125, parm.p912);
         pstmt6.setInt(126, parm.p913);
         pstmt6.setInt(127, parm.p914);
         pstmt6.setInt(128, parm.p915);
         pstmt6.setInt(129, parm.p916);
         pstmt6.setInt(130, parm.p917);
         pstmt6.setInt(131, parm.p918);
         pstmt6.setInt(132, parm.p919);
         pstmt6.setInt(133, parm.p920);
         pstmt6.setInt(134, parm.p921);
         pstmt6.setInt(135, parm.p922);
         pstmt6.setInt(136, parm.p923);
         pstmt6.setInt(137, parm.p924);
         pstmt6.setInt(138, parm.p925);
         pstmt6.setInt(139, parm.checkothers);
         pstmt6.setString(140, course);            // save the requested course for pro approval process
         pstmt6.setInt(141, parm.guest_id1);
         pstmt6.setInt(142, parm.guest_id2);
         pstmt6.setInt(143, parm.guest_id3);
         pstmt6.setInt(144, parm.guest_id4);
         pstmt6.setInt(145, parm.guest_id5);
         pstmt6.setInt(146, parm.guest_id6);
         pstmt6.setInt(147, parm.guest_id7);
         pstmt6.setInt(148, parm.guest_id8);
         pstmt6.setInt(149, parm.guest_id9);
         pstmt6.setInt(150, parm.guest_id10);
         pstmt6.setInt(151, parm.guest_id11);
         pstmt6.setInt(152, parm.guest_id12);
         pstmt6.setInt(153, parm.guest_id13);
         pstmt6.setInt(154, parm.guest_id14);
         pstmt6.setInt(155, parm.guest_id15);
         pstmt6.setInt(156, parm.guest_id16);
         pstmt6.setInt(157, parm.guest_id17);
         pstmt6.setInt(158, parm.guest_id18);
         pstmt6.setInt(159, parm.guest_id19);
         pstmt6.setInt(160, parm.guest_id20);
         pstmt6.setInt(161, parm.guest_id21);
         pstmt6.setInt(162, parm.guest_id22);
         pstmt6.setInt(163, parm.guest_id23);
         pstmt6.setInt(164, parm.guest_id24);
         pstmt6.setInt(165, parm.guest_id25);

         pstmt6.setLong(166, lottid);
         pstmt6.executeUpdate();      // execute the prepared stmt

         pstmt6.close();
      }
      catch (Exception e1) {
         
         msgHdr = "Database Access Error";

         msgBody = "Sorry, we are unable to access the Database at this time.<br><br>Please try again later.";

         buildError(msgHdr, msgBody, mobile, out);       // output the error message

         //
         //  Return to _lott
         //
         goReturn(out, parm);
         return;
      }

   } else {          // new request

      memNew++;      // increment number of new tee times
      emailNew = 1;  // tee time is new

      //
      //  Get the next available id for the lottery request
      //
      lottid = SystemUtils.getLottId(con);      // allocate a new entry

      if (lottid == 0) {

         msgHdr = "Tee Time Request Error";

         msgBody = "Sorry, we were unable to allocate a new entry for this Tee Time Request.<br><br>Please try again later.";

         buildError(msgHdr, msgBody, mobile, out);       // output the error message

         //
         //  Return to _lott
         //
         goReturn(out, parm);
         return;
      }

      //
      //   Add a new Lottery Request
      //
      try {
         PreparedStatement pstmt3 = con.prepareStatement (
            "INSERT INTO lreqs3 (name, date, mm, dd, yy, day, hr, min, time, minsbefore, minsafter, " +
            "player1, player2, player3, player4, player5, player6, player7, player8, player9, player10, " +
            "player11, player12, player13, player14, player15, player16, player17, player18, player19, player20, " +
            "player21, player22, player23, player24, player25, " +
            "user1, user2, user3, user4, user5, user6, user7, user8, user9, user10, " +
            "user11, user12, user13, user14, user15, user16, user17, user18, user19, user20, " +
            "user21, user22, user23, user24, user25, " +
            "p1cw, p2cw, p3cw, p4cw, p5cw, p6cw, p7cw, p8cw, p9cw, p10cw, " +
            "p11cw, p12cw, p13cw, p14cw, p15cw, p16cw, p17cw, p18cw, p19cw, p20cw, " +
            "p21cw, p22cw, p23cw, p24cw, p25cw, notes, hideNotes, fb, courseName, proNew, " +
            "proMod, memNew, memMod, id, in_use, in_use_by, groups, type, state, atime1, " +
            "atime2, atime3, atime4, atime5, afb, p5, players, userg1, userg2, userg3, userg4, userg5, " +
            "userg6, userg7, userg8, userg9, userg10, userg11, userg12, userg13, userg14, userg15, " +
            "userg16, userg17, userg18, userg19, userg20, userg21, userg22, userg23, userg24, userg25, " +
            "weight, orig_by, p91, p92, p93, p94, p95, " +
            "p96, p97, p98, p99, p910, p911, p912, p913, p914, p915, " +
            "p916, p917, p918, p919, p920, p921, p922, p923, p924, p925, checkothers, courseReq, " +
            "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5, guest_id6, guest_id7, guest_id8, guest_id9, guest_id10, " +
            "guest_id11, guest_id12, guest_id13, guest_id14, guest_id15, guest_id16, guest_id17, guest_id18, guest_id19, guest_id20, " +
            "guest_id21, guest_id22, guest_id23, guest_id24, guest_id25) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, 0, ?, ?, 0, " +
            "0, ?, 0, ?, 0, '', ?, '', 0, 0, " +
            "0, 0, 0, 0, 0, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "0, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?)");

         pstmt3.clearParameters();        // clear the parms
         pstmt3.setString(1, lottName);
         pstmt3.setLong(2, date);
         pstmt3.setInt(3, mm);
         pstmt3.setInt(4, parm.dd);
         pstmt3.setInt(5, yy);
         pstmt3.setString(6, day);
         pstmt3.setInt(7, hr);
         pstmt3.setInt(8, min);
         pstmt3.setInt(9, time);
         pstmt3.setInt(10, mins_before);
         pstmt3.setInt(11, mins_after);
         pstmt3.setString(12, parm.player1);
         pstmt3.setString(13, parm.player2);
         pstmt3.setString(14, parm.player3);
         pstmt3.setString(15, parm.player4);
         pstmt3.setString(16, parm.player5);
         pstmt3.setString(17, parm.player6);
         pstmt3.setString(18, parm.player7);
         pstmt3.setString(19, parm.player8);
         pstmt3.setString(20, parm.player9);
         pstmt3.setString(21, parm.player10);
         pstmt3.setString(22, parm.player11);
         pstmt3.setString(23, parm.player12);
         pstmt3.setString(24, parm.player13);
         pstmt3.setString(25, parm.player14);
         pstmt3.setString(26, parm.player15);
         pstmt3.setString(27, parm.player16);
         pstmt3.setString(28, parm.player17);
         pstmt3.setString(29, parm.player18);
         pstmt3.setString(30, parm.player19);
         pstmt3.setString(31, parm.player20);
         pstmt3.setString(32, parm.player21);
         pstmt3.setString(33, parm.player22);
         pstmt3.setString(34, parm.player23);
         pstmt3.setString(35, parm.player24);
         pstmt3.setString(36, parm.player25);
         pstmt3.setString(37, parm.user1);
         pstmt3.setString(38, parm.user2);
         pstmt3.setString(39, parm.user3);
         pstmt3.setString(40, parm.user4);
         pstmt3.setString(41, parm.user5);
         pstmt3.setString(42, parm.user6);
         pstmt3.setString(43, parm.user7);
         pstmt3.setString(44, parm.user8);
         pstmt3.setString(45, parm.user9);
         pstmt3.setString(46, parm.user10);
         pstmt3.setString(47, parm.user11);
         pstmt3.setString(48, parm.user12);
         pstmt3.setString(49, parm.user13);
         pstmt3.setString(50, parm.user14);
         pstmt3.setString(51, parm.user15);
         pstmt3.setString(52, parm.user16);
         pstmt3.setString(53, parm.user17);
         pstmt3.setString(54, parm.user18);
         pstmt3.setString(55, parm.user19);
         pstmt3.setString(56, parm.user20);
         pstmt3.setString(57, parm.user21);
         pstmt3.setString(58, parm.user22);
         pstmt3.setString(59, parm.user23);
         pstmt3.setString(60, parm.user24);
         pstmt3.setString(61, parm.user25);
         pstmt3.setString(62, parm.pcw1);
         pstmt3.setString(63, parm.pcw2);
         pstmt3.setString(64, parm.pcw3);
         pstmt3.setString(65, parm.pcw4);
         pstmt3.setString(66, parm.pcw5);
         pstmt3.setString(67, parm.pcw6);
         pstmt3.setString(68, parm.pcw7);
         pstmt3.setString(69, parm.pcw8);
         pstmt3.setString(70, parm.pcw9);
         pstmt3.setString(71, parm.pcw10);
         pstmt3.setString(72, parm.pcw11);
         pstmt3.setString(73, parm.pcw12);
         pstmt3.setString(74, parm.pcw13);
         pstmt3.setString(75, parm.pcw14);
         pstmt3.setString(76, parm.pcw15);
         pstmt3.setString(77, parm.pcw16);
         pstmt3.setString(78, parm.pcw17);
         pstmt3.setString(79, parm.pcw18);
         pstmt3.setString(80, parm.pcw19);
         pstmt3.setString(81, parm.pcw20);
         pstmt3.setString(82, parm.pcw21);
         pstmt3.setString(83, parm.pcw22);
         pstmt3.setString(84, parm.pcw23);
         pstmt3.setString(85, parm.pcw24);
         pstmt3.setString(86, parm.pcw25);
         pstmt3.setString(87, notes);
         pstmt3.setInt(88, fb);
         pstmt3.setString(89, course);
         pstmt3.setInt(90, memNew);
         pstmt3.setLong(91, lottid);
         pstmt3.setInt(92, slots);
         pstmt3.setString(93, p5);
         pstmt3.setInt(94, players);
         pstmt3.setString(95, userg[0]);
         pstmt3.setString(96, userg[1]);
         pstmt3.setString(97, userg[2]);
         pstmt3.setString(98, userg[3]);
         pstmt3.setString(99, userg[4]);
         pstmt3.setString(100, userg[5]);
         pstmt3.setString(101, userg[6]);
         pstmt3.setString(102, userg[7]);
         pstmt3.setString(103, userg[8]);
         pstmt3.setString(104, userg[9]);
         pstmt3.setString(105, userg[10]);
         pstmt3.setString(106, userg[11]);
         pstmt3.setString(107, userg[12]);
         pstmt3.setString(108, userg[13]);
         pstmt3.setString(109, userg[14]);
         pstmt3.setString(110, userg[15]);
         pstmt3.setString(111, userg[16]);
         pstmt3.setString(112, userg[17]);
         pstmt3.setString(113, userg[18]);
         pstmt3.setString(114, userg[19]);
         pstmt3.setString(115, userg[20]);
         pstmt3.setString(116, userg[21]);
         pstmt3.setString(117, userg[22]);
         pstmt3.setString(118, userg[23]);
         pstmt3.setString(119, userg[24]);
         pstmt3.setString(120, orig_by);
         pstmt3.setInt(121, parm.p91);
         pstmt3.setInt(122, parm.p92);
         pstmt3.setInt(123, parm.p93);
         pstmt3.setInt(124, parm.p94);
         pstmt3.setInt(125, parm.p95);
         pstmt3.setInt(126, parm.p96);
         pstmt3.setInt(127, parm.p97);
         pstmt3.setInt(128, parm.p98);
         pstmt3.setInt(129, parm.p99);
         pstmt3.setInt(130, parm.p910);
         pstmt3.setInt(131, parm.p911);
         pstmt3.setInt(132, parm.p912);
         pstmt3.setInt(133, parm.p913);
         pstmt3.setInt(134, parm.p914);
         pstmt3.setInt(135, parm.p915);
         pstmt3.setInt(136, parm.p916);
         pstmt3.setInt(137, parm.p917);
         pstmt3.setInt(138, parm.p918);
         pstmt3.setInt(139, parm.p919);
         pstmt3.setInt(140, parm.p920);
         pstmt3.setInt(141, parm.p921);
         pstmt3.setInt(142, parm.p922);
         pstmt3.setInt(143, parm.p923);
         pstmt3.setInt(144, parm.p924);
         pstmt3.setInt(145, parm.p925);
         pstmt3.setInt(146, parm.checkothers);
         pstmt3.setString(147, course);            // save course requested for pro approval process (drag-n-drop)
         pstmt3.setInt(148, parm.guest_id1);
         pstmt3.setInt(149, parm.guest_id2);
         pstmt3.setInt(150, parm.guest_id3);
         pstmt3.setInt(151, parm.guest_id4);
         pstmt3.setInt(152, parm.guest_id5);
         pstmt3.setInt(153, parm.guest_id6);
         pstmt3.setInt(154, parm.guest_id7);
         pstmt3.setInt(155, parm.guest_id8);
         pstmt3.setInt(156, parm.guest_id9);
         pstmt3.setInt(157, parm.guest_id10);
         pstmt3.setInt(158, parm.guest_id11);
         pstmt3.setInt(159, parm.guest_id12);
         pstmt3.setInt(160, parm.guest_id13);
         pstmt3.setInt(161, parm.guest_id14);
         pstmt3.setInt(162, parm.guest_id15);
         pstmt3.setInt(163, parm.guest_id16);
         pstmt3.setInt(164, parm.guest_id17);
         pstmt3.setInt(165, parm.guest_id18);
         pstmt3.setInt(166, parm.guest_id19);
         pstmt3.setInt(167, parm.guest_id20);
         pstmt3.setInt(168, parm.guest_id21);
         pstmt3.setInt(169, parm.guest_id22);
         pstmt3.setInt(170, parm.guest_id23);
         pstmt3.setInt(171, parm.guest_id24);
         pstmt3.setInt(172, parm.guest_id25);

         pstmt3.executeUpdate();        // execute the prepared stmt

         pstmt3.close();
      }
      catch (Exception e1) {
         msgHdr = "Database Access Error";

         msgBody = "Sorry, we are unable to access the Database at this time.<br><br>Please try again later.";

         buildError(msgHdr, msgBody, mobile, out);       // output the error message

         //
         //  Return to _lott
         //
         goReturn(out, parm);
         return;
      }

   }  // end of if new or mod

   //
   //  Now see if a record exists for this in the Active Lotteries Table (actlott3).
   //  If not, add one so SystemUtils can process it when the time comes.
   //
   int pdays = 0;
   int ptime = 0;
   int phr = 0;
   long pdd = parm.dd;
   long pmm = mm;
   long pyy = yy;
   long pdate = 0;

   try {

      PreparedStatement pstmta = con.prepareStatement (
               "SELECT pdate FROM actlott3 WHERE name = ? AND date = ? AND courseName = ?");

      pstmta.clearParameters();            // clear the parms
      pstmta.setString(1, lottName);       // Lottery Name
      pstmta.setLong(2, date);             // Date for this lottery request
      pstmta.setString(3, course);
      rs = pstmta.executeQuery();

      if (rs.next()) {

         pdate = rs.getLong(1);
      }
      pstmta.close();              // close the stmt

      if (pdate != 0) {            // already exists

         pyy = pdate / 10000;                            // get processing date - year
         pmm = (pdate - (pyy * 10000)) / 100;            // month
         pdd = pdate - ((pyy * 10000) + (pmm * 100));    // day

      } else {

         //
         //  This lottery has not been entered for processing - enter it now.
         //
         //  Calculate the date to process this request - save date and time in record
         //
         PreparedStatement pstmt = con.prepareStatement (
                  "SELECT pdays, ptime FROM lottery3 WHERE name = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, lottName);       // put the parm in stmt
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            pdays = rs.getInt(1);            // days in advance to process requests
            ptime = rs.getInt(2);            // time of day to process lottery
         }
         pstmt.close();              // close the stmt

         //
         //  adjust the time for the club's time zone
         //
         ptime = SystemUtils.adjustTimeBack(con, ptime);

         if (ptime < 0) {          // if negative, then roll back one day or ahead one day

            ptime = 0 - ptime;     // convert back to positive

            phr = ptime / 100;     // get hr value

            if (phr < 12) {        // if AM, then we rolled ahead one day

               pdays--;               // one less day (now AM instead of PM)

            } else {               // else we rolled back one day

               pdays++;               // one more day (now PM instead of AM)
            }
         }

         //
         //  use the lottery's date and then roll back 'pdays' to determine the processing date
         //
         while (pdays > 0) {

            pdd--;                  // go back one day

            if (pdd == 0) {         // adjust month and year if necessary

               if (pmm == 1) {      // must go back to 12/31 of prev year

                  pyy--;
                  pmm = 12;
                  pdd = 31;
                    
               } else {

                  if (pmm == 2 || pmm == 4 || pmm == 6 || pmm == 8 || pmm == 9 || pmm == 11) { // new month has 31 days

                     pmm--;
                     pdd = 31;
                       
                  } else {

                     if (pmm == 5 || pmm == 7 || pmm == 10 || pmm == 12) {     // new month has 30 days

                        pmm--;
                        pdd = 30;

                     } else {

                        if (pmm == 3) {     // new month to be Feb

                           pmm = 2;
                           pdd = 28;

                           if (pyy == 2004 ||  pyy == 2008 ||  pyy == 2012 ||  pyy == 2016 ||  pyy == 2020 ||  pyy == 2024 ||
                               pyy == 2028 ||  pyy == 2032 ||  pyy == 2036 ||  pyy == 2040) {

                              pdd = 29;           // leap year
                           }
                        }
                     }
                  }
               }
            }
            pdays--;                  // continue for specified days in advance
         }                       // end of while

         pdate = (pyy * 10000) + (pmm * 100) + pdd;    // processing date = yyyymmdd (for comparisons)

         //
         //  Save the lottery info for processing
         //
         PreparedStatement pstmt3a = con.prepareStatement (
            "INSERT INTO actlott3 (name, date, pdate, ptime, courseName) " +
            "VALUES (?, ?, ?, ?, ?)");

         pstmt3a.clearParameters();        // clear the parms
         pstmt3a.setString(1, lottName);
         pstmt3a.setLong(2, date);
         pstmt3a.setLong(3, pdate);
         pstmt3a.setInt(4, ptime);
         pstmt3a.setString(5, course);

         pstmt3a.executeUpdate();        // execute the prepared stmt

         pstmt3a.close();
      }
   }
   catch (Exception e1) {
      //
      //  save error message in /" +rev+ "/error.txt
      //
      String errorMsg = "Error in Member_lott: ";
      errorMsg = errorMsg + e1;                                 // build error msg
      SystemUtils.logError(errorMsg);                           // log it
   }

   //
   //  Build the HTML page to confirm reservation for user
   //
   //
   if (mobile == 0) {                 // if NOT a Mobile user
      
      if (club.equals( "oldoaks" )) {
         out.println(SystemUtils.HeadTitle("Member Tee Time Request Page"));
      } else if (!lotteryText.equals("")) {
         out.println(SystemUtils.HeadTitle("Member " +lotteryText+ " Page"));
      } else {
         out.println(SystemUtils.HeadTitle("Member Lottery Request Page"));
      }
      out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

      out.println("<center><img src=\"/" +rev+ "/images/foretees.gif\"><hr width=\"40%\">");
      out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

      out.println("<p>&nbsp;</p><p><b>Thank you!</b>&nbsp;&nbsp;Your request has been accepted and processed.</p>");
      out.println("</font><font size=\"2\">");
      out.println("<br><p>The tee times will be assigned on <b>" +pmm+ "/" +pdd+ "/" +pyy+ "</b>.");
      out.println("<br>If your email address is registered (refer to 'Settings'), you will receive an email with your assigned time.</p>");
      out.println("<br><p>To view or change your request at a later time just click on the <b>'My Tee Times'</b> tab.</p>");

      if (notesL > 254) {

      out.println("</font><font size=\"3\">");
      out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
      }

      out.println("<p>&nbsp;</p></font>");

      if (index.equals( "999" )) {         // if came from Member_teelist

         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain.htm\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</form></font>");

      } else {

         if (index.equals( "995" )) {         // if came from Member_teelist_list

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
               if (!returnCourse.equals( "" )) {
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
               } else {
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               }
               out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
               out.println("</form></font>");
            }
         }
      }
      out.println("</center></font>");

   } else {       // Mobile user
      
      //
      //  Mobile user
      //
      out.println(SystemUtils.HeadTitleMobile("Foretees Request Complete"));
      out.println(SystemUtils.BannerMobile());

      out.println("<div class=\"content\">");
      out.println("<div class=\"headertext\">");    // output the heading
      out.println("Request Accepted");
      out.println("</div>");
      out.println("<div class=\"smheadertext\">The tee times will be assigned on " +pmm+ "/" +pdd+ "/" +pyy+ ".</div>");     
      out.println("<div>To view or change your request at a later time just click on the 'My Tee Times' tab.</div>");
      out.println("<ul>");

      if (index.equals( "995" )) {         // if came from Member_teelist_list

         out.println("<li>");
         out.println("<form action=\"/" +rev+ "/servlet/Member_teelist_mobile\" method=\"get\">");
         out.println("<input type=\"submit\" value=\"Return to List\"></form>");
         out.println("</li>");

      } else {

         out.println("<li>");
         out.println("<form action=\"/" +rev+ "/servlet/Member_sheet\" method=\"post\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
         out.println("<input type=\"submit\" value=\"Return To Tee Sheet\"></form>");
         out.println("</li>");
      }
      out.println("</ul></div>");
   }
   
   //
   //  End of HTML page
   //
   out.println("</body></html>");

   try {

      resp.flushBuffer();      // force the repsonse to complete

   }
   catch (Exception ignore) {
   }

   out.close();
      
   //
   //***********************************************
   //  Send email notification if necessary
   //***********************************************
   //
   if (sendEmail != 0 && !club.equals( "ranchobernardo" )) {      // do not send for Rancho Bernardo

      sendMail(con, parm, emailNew, emailMod, user);        // send emails

   }     // end of IF sendEmail

 }       // end of Verify
 // *******************************************************************************


 // *******************************************************************************
 //  Check if request is in use by user
 // *******************************************************************************
 //
 private void checkInUseBy(Connection con, long lottid, parmLott parm) {


   ResultSet rs = null;

   //
   //  Check if this request is still 'in use' and still in use by this user??
   //
   //  This is necessary because the user may have gone away while holding this req.  If the
   //  slot timed out (system timer), the slot would be marked 'not in use' and another
   //  user could pick it up.  The original holder could be trying to use it now.
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
         "SELECT player1, player2, player3, player4, player5, player6, player7, player8, " +
         "player9, player10, player11, player12, player13, player14, player15, player16, player17, " +
         "player18, player19, player20, player21, player22, player23, player24, player25, " +
         "user1, user2, user3, user4, user5, user6, user7, user8, " +
         "user9, user10, user11, user12, user13, user14, user15, user16, user17, " +
         "user18, user19, user20, user21, user22, user23, user24, user25, " +
         "p1cw, p2cw, p3cw, p4cw, p5cw, p6cw, p7cw, p8cw, " +
         "p9cw, p10cw, p11cw, p12cw, p13cw, p14cw, p15cw, p16cw, p17cw, " +
         "p18cw, p19cw, p20cw, p21cw, p22cw, p23cw, p24cw, p25cw, " +
         "notes, hideNotes, memNew, memMod, in_use, in_use_by, orig_by, " +
         "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5, " +
         "guest_id6, guest_id7, guest_id8, guest_id9, guest_id10, " +
         "guest_id11, guest_id12, guest_id13, guest_id14, guest_id15, " +
         "guest_id16, guest_id17, guest_id18, guest_id19, guest_id20, " +
         "guest_id21, guest_id22, guest_id23, guest_id24, guest_id25 " +
         "FROM lreqs3 WHERE id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, lottid);         // put the parm in pstmt
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         parm.oldplayer1 = rs.getString(1);
         parm.oldplayer2 = rs.getString(2);
         parm.oldplayer3 = rs.getString(3);
         parm.oldplayer4 = rs.getString(4);
         parm.oldplayer5 = rs.getString(5);
         parm.oldplayer6 = rs.getString(6);
         parm.oldplayer7 = rs.getString(7);
         parm.oldplayer8 = rs.getString(8);
         parm.oldplayer9 = rs.getString(9);
         parm.oldplayer10 = rs.getString(10);
         parm.oldplayer11 = rs.getString(11);
         parm.oldplayer12 = rs.getString(12);
         parm.oldplayer13 = rs.getString(13);
         parm.oldplayer14 = rs.getString(14);
         parm.oldplayer15 = rs.getString(15);
         parm.oldplayer16 = rs.getString(16);
         parm.oldplayer17 = rs.getString(17);
         parm.oldplayer18 = rs.getString(18);
         parm.oldplayer19 = rs.getString(19);
         parm.oldplayer20 = rs.getString(20);
         parm.oldplayer21 = rs.getString(21);
         parm.oldplayer22 = rs.getString(22);
         parm.oldplayer23 = rs.getString(23);
         parm.oldplayer24 = rs.getString(24);
         parm.oldplayer25 = rs.getString(25);
         parm.olduser1 = rs.getString(26);
         parm.olduser2 = rs.getString(27);
         parm.olduser3 = rs.getString(28);
         parm.olduser4 = rs.getString(29);
         parm.olduser5 = rs.getString(30);
         parm.olduser6 = rs.getString(31);
         parm.olduser7 = rs.getString(32);
         parm.olduser8 = rs.getString(33);
         parm.olduser9 = rs.getString(34);
         parm.olduser10 = rs.getString(35);
         parm.olduser11 = rs.getString(36);
         parm.olduser12 = rs.getString(37);
         parm.olduser13 = rs.getString(38);
         parm.olduser14 = rs.getString(39);
         parm.olduser15 = rs.getString(40);
         parm.olduser16 = rs.getString(41);
         parm.olduser17 = rs.getString(42);
         parm.olduser18 = rs.getString(43);
         parm.olduser19 = rs.getString(44);
         parm.olduser20 = rs.getString(45);
         parm.olduser21 = rs.getString(46);
         parm.olduser22 = rs.getString(47);
         parm.olduser23 = rs.getString(48);
         parm.olduser24 = rs.getString(49);
         parm.olduser25 = rs.getString(50);
         parm.oldpcw1 = rs.getString(51);
         parm.oldpcw2 = rs.getString(52);
         parm.oldpcw3 = rs.getString(53);
         parm.oldpcw4 = rs.getString(54);
         parm.oldpcw5 = rs.getString(55);
         parm.oldpcw6 = rs.getString(56);
         parm.oldpcw7 = rs.getString(57);
         parm.oldpcw8 = rs.getString(58);
         parm.oldpcw9 = rs.getString(59);
         parm.oldpcw10 = rs.getString(60);
         parm.oldpcw11 = rs.getString(61);
         parm.oldpcw12 = rs.getString(62);
         parm.oldpcw13 = rs.getString(63);
         parm.oldpcw14 = rs.getString(64);
         parm.oldpcw15 = rs.getString(65);
         parm.oldpcw16 = rs.getString(66);
         parm.oldpcw17 = rs.getString(67);
         parm.oldpcw18 = rs.getString(68);
         parm.oldpcw19 = rs.getString(69);
         parm.oldpcw20 = rs.getString(70);
         parm.oldpcw21 = rs.getString(71);
         parm.oldpcw22 = rs.getString(72);
         parm.oldpcw23 = rs.getString(73);
         parm.oldpcw24 = rs.getString(74);
         parm.oldpcw25 = rs.getString(75);
         parm.notes = rs.getString(76);
         parm.hide = rs.getInt(77);
         parm.memNew = rs.getInt(78);
         parm.memMod = rs.getInt(79);
         parm.in_use = rs.getInt(80);
         parm.in_use_by = rs.getString(81);
         parm.orig_by = rs.getString(82);
         parm.oldguest_id1 = rs.getInt("guest_id1");
         parm.oldguest_id2 = rs.getInt("guest_id2");
         parm.oldguest_id3 = rs.getInt("guest_id3");
         parm.oldguest_id4 = rs.getInt("guest_id4");
         parm.oldguest_id5 = rs.getInt("guest_id5");
         parm.oldguest_id6 = rs.getInt("guest_id6");
         parm.oldguest_id7 = rs.getInt("guest_id7");
         parm.oldguest_id8 = rs.getInt("guest_id8");
         parm.oldguest_id9 = rs.getInt("guest_id9");
         parm.oldguest_id10 = rs.getInt("guest_id10");
         parm.oldguest_id11 = rs.getInt("guest_id11");
         parm.oldguest_id12 = rs.getInt("guest_id12");
         parm.oldguest_id13 = rs.getInt("guest_id13");
         parm.oldguest_id14 = rs.getInt("guest_id14");
         parm.oldguest_id15 = rs.getInt("guest_id15");
         parm.oldguest_id16 = rs.getInt("guest_id16");
         parm.oldguest_id17 = rs.getInt("guest_id17");
         parm.oldguest_id18 = rs.getInt("guest_id18");
         parm.oldguest_id19 = rs.getInt("guest_id19");
         parm.oldguest_id20 = rs.getInt("guest_id20");
         parm.oldguest_id21 = rs.getInt("guest_id21");
         parm.oldguest_id22 = rs.getInt("guest_id22");
         parm.oldguest_id23 = rs.getInt("guest_id23");
         parm.oldguest_id24 = rs.getInt("guest_id24");
         parm.oldguest_id25 = rs.getInt("guest_id25");
      }
      pstmt.close();
   }
   catch (Exception ignore) {         // let next test catch any errors
   }
 }       // end of checkInUseBy


 // *******************************************************************************
 //  Parse Member Names
 // *******************************************************************************
 //
 private boolean parseNames(PrintWriter out, parmLott parm) {


   boolean error = false;


   if (!parm.player1.equals( "" ) && parm.g[0].equals( "" )) {

      if ((parm.player1.equalsIgnoreCase( parm.player2 )) || (parm.player1.equalsIgnoreCase( parm.player3 )) ||
          (parm.player1.equalsIgnoreCase( parm.player4 )) || (parm.player1.equalsIgnoreCase( parm.player5 )) ||
          (parm.player1.equalsIgnoreCase( parm.player6 )) || (parm.player1.equalsIgnoreCase( parm.player7 )) ||
          (parm.player1.equalsIgnoreCase( parm.player8 )) || (parm.player1.equalsIgnoreCase( parm.player9 )) ||
          (parm.player1.equalsIgnoreCase( parm.player10 )) || (parm.player1.equalsIgnoreCase( parm.player11 )) ||
          (parm.player1.equalsIgnoreCase( parm.player12 )) || (parm.player1.equalsIgnoreCase( parm.player13 )) ||
          (parm.player1.equalsIgnoreCase( parm.player14 )) || (parm.player1.equalsIgnoreCase( parm.player15 )) ||
          (parm.player1.equalsIgnoreCase( parm.player16 )) || (parm.player1.equalsIgnoreCase( parm.player17 )) ||
          (parm.player1.equalsIgnoreCase( parm.player18 )) || (parm.player1.equalsIgnoreCase( parm.player19 )) ||
          (parm.player1.equalsIgnoreCase( parm.player20 )) || (parm.player1.equalsIgnoreCase( parm.player21 )) ||
          (parm.player1.equalsIgnoreCase( parm.player22 )) || (parm.player1.equalsIgnoreCase( parm.player23 )) ||
          (parm.player1.equalsIgnoreCase( parm.player24 )) || (parm.player1.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player1);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player1 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player1);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() < 2 ) {          // single names or X not allowed in player1 spot

         invData(out, parm.player1);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname1 = tok.nextToken();
         parm.lname1 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname1 = tok.nextToken();
         parm.mi1 = tok.nextToken();
         parm.lname1 = tok.nextToken();
      }
   }

   if (!parm.player2.equals( "" ) && parm.g[1].equals( "" )) {

      if ((parm.player2.equalsIgnoreCase( parm.player3 )) ||
          (parm.player2.equalsIgnoreCase( parm.player4 )) || (parm.player2.equalsIgnoreCase( parm.player5 )) ||
          (parm.player2.equalsIgnoreCase( parm.player6 )) || (parm.player2.equalsIgnoreCase( parm.player7 )) ||
          (parm.player2.equalsIgnoreCase( parm.player8 )) || (parm.player2.equalsIgnoreCase( parm.player9 )) ||
          (parm.player2.equalsIgnoreCase( parm.player10 )) || (parm.player2.equalsIgnoreCase( parm.player11 )) ||
          (parm.player2.equalsIgnoreCase( parm.player12 )) || (parm.player2.equalsIgnoreCase( parm.player13 )) ||
          (parm.player2.equalsIgnoreCase( parm.player14 )) || (parm.player2.equalsIgnoreCase( parm.player15 )) ||
          (parm.player2.equalsIgnoreCase( parm.player16 )) || (parm.player2.equalsIgnoreCase( parm.player17 )) ||
          (parm.player2.equalsIgnoreCase( parm.player18 )) || (parm.player2.equalsIgnoreCase( parm.player19 )) ||
          (parm.player2.equalsIgnoreCase( parm.player20 )) || (parm.player2.equalsIgnoreCase( parm.player21 )) ||
          (parm.player2.equalsIgnoreCase( parm.player22 )) || (parm.player2.equalsIgnoreCase( parm.player23 )) ||
          (parm.player2.equalsIgnoreCase( parm.player24 )) || (parm.player2.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player2);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player2 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player2);                        // reject
         error = true;
         return(error);
      }
      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player2);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname2 = tok.nextToken();
         parm.lname2 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname2 = tok.nextToken();
         parm.mi2 = tok.nextToken();
         parm.lname2 = tok.nextToken();
      }
   }

   if (!parm.player3.equals( "" ) && parm.g[2].equals( "" )) {

      if ((parm.player3.equalsIgnoreCase( parm.player4 )) || (parm.player3.equalsIgnoreCase( parm.player5 )) ||
          (parm.player3.equalsIgnoreCase( parm.player6 )) || (parm.player3.equalsIgnoreCase( parm.player7 )) ||
          (parm.player3.equalsIgnoreCase( parm.player8 )) || (parm.player3.equalsIgnoreCase( parm.player9 )) ||
          (parm.player3.equalsIgnoreCase( parm.player10 )) || (parm.player3.equalsIgnoreCase( parm.player11 )) ||
          (parm.player3.equalsIgnoreCase( parm.player12 )) || (parm.player3.equalsIgnoreCase( parm.player13 )) ||
          (parm.player3.equalsIgnoreCase( parm.player14 )) || (parm.player3.equalsIgnoreCase( parm.player15 )) ||
          (parm.player3.equalsIgnoreCase( parm.player16 )) || (parm.player3.equalsIgnoreCase( parm.player17 )) ||
          (parm.player3.equalsIgnoreCase( parm.player18 )) || (parm.player3.equalsIgnoreCase( parm.player19 )) ||
          (parm.player3.equalsIgnoreCase( parm.player20 )) || (parm.player3.equalsIgnoreCase( parm.player21 )) ||
          (parm.player3.equalsIgnoreCase( parm.player22 )) || (parm.player3.equalsIgnoreCase( parm.player23 )) ||
          (parm.player3.equalsIgnoreCase( parm.player24 )) || (parm.player3.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player3);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player3 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player3);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player3);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname3 = tok.nextToken();
         parm.lname3 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname3 = tok.nextToken();
         parm.mi3 = tok.nextToken();
         parm.lname3 = tok.nextToken();
      }
   }

   if (!parm.player4.equals( "" ) && parm.g[3].equals( "" )) {

      if ((parm.player4.equalsIgnoreCase( parm.player5 )) ||
          (parm.player4.equalsIgnoreCase( parm.player6 )) || (parm.player4.equalsIgnoreCase( parm.player7 )) ||
          (parm.player4.equalsIgnoreCase( parm.player8 )) || (parm.player4.equalsIgnoreCase( parm.player9 )) ||
          (parm.player4.equalsIgnoreCase( parm.player10 )) || (parm.player4.equalsIgnoreCase( parm.player11 )) ||
          (parm.player4.equalsIgnoreCase( parm.player12 )) || (parm.player4.equalsIgnoreCase( parm.player13 )) ||
          (parm.player4.equalsIgnoreCase( parm.player14 )) || (parm.player4.equalsIgnoreCase( parm.player15 )) ||
          (parm.player4.equalsIgnoreCase( parm.player16 )) || (parm.player4.equalsIgnoreCase( parm.player17 )) ||
          (parm.player4.equalsIgnoreCase( parm.player18 )) || (parm.player4.equalsIgnoreCase( parm.player19 )) ||
          (parm.player4.equalsIgnoreCase( parm.player20 )) || (parm.player4.equalsIgnoreCase( parm.player21 )) ||
          (parm.player4.equalsIgnoreCase( parm.player22 )) || (parm.player4.equalsIgnoreCase( parm.player23 )) ||
          (parm.player4.equalsIgnoreCase( parm.player24 )) || (parm.player4.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player4);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player4 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player4);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player4);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname4 = tok.nextToken();
         parm.lname4 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname4 = tok.nextToken();
         parm.mi4 = tok.nextToken();
         parm.lname4 = tok.nextToken();
      }
   }

   if (!parm.player5.equals( "" ) && parm.g[4].equals( "" )) {

      if ((parm.player5.equalsIgnoreCase( parm.player6 )) || (parm.player5.equalsIgnoreCase( parm.player7 )) ||
          (parm.player5.equalsIgnoreCase( parm.player8 )) || (parm.player5.equalsIgnoreCase( parm.player9 )) ||
          (parm.player5.equalsIgnoreCase( parm.player10 )) || (parm.player5.equalsIgnoreCase( parm.player11 )) ||
          (parm.player5.equalsIgnoreCase( parm.player12 )) || (parm.player5.equalsIgnoreCase( parm.player13 )) ||
          (parm.player5.equalsIgnoreCase( parm.player14 )) || (parm.player5.equalsIgnoreCase( parm.player15 )) ||
          (parm.player5.equalsIgnoreCase( parm.player16 )) || (parm.player5.equalsIgnoreCase( parm.player17 )) ||
          (parm.player5.equalsIgnoreCase( parm.player18 )) || (parm.player5.equalsIgnoreCase( parm.player19 )) ||
          (parm.player5.equalsIgnoreCase( parm.player20 )) || (parm.player5.equalsIgnoreCase( parm.player21 )) ||
          (parm.player5.equalsIgnoreCase( parm.player22 )) || (parm.player5.equalsIgnoreCase( parm.player23 )) ||
          (parm.player5.equalsIgnoreCase( parm.player24 )) || (parm.player5.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player5);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player5 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player5);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player5);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname5 = tok.nextToken();
         parm.lname5 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname5 = tok.nextToken();
         parm.mi5 = tok.nextToken();
         parm.lname5 = tok.nextToken();
      }
   }

   if (!parm.player6.equals( "" ) && parm.g[5].equals( "" )) {

      if ((parm.player6.equalsIgnoreCase( parm.player7 )) ||
          (parm.player6.equalsIgnoreCase( parm.player8 )) || (parm.player6.equalsIgnoreCase( parm.player9 )) ||
          (parm.player6.equalsIgnoreCase( parm.player10 )) || (parm.player6.equalsIgnoreCase( parm.player11 )) ||
          (parm.player6.equalsIgnoreCase( parm.player12 )) || (parm.player6.equalsIgnoreCase( parm.player13 )) ||
          (parm.player6.equalsIgnoreCase( parm.player14 )) || (parm.player6.equalsIgnoreCase( parm.player15 )) ||
          (parm.player6.equalsIgnoreCase( parm.player16 )) || (parm.player6.equalsIgnoreCase( parm.player17 )) ||
          (parm.player6.equalsIgnoreCase( parm.player18 )) || (parm.player6.equalsIgnoreCase( parm.player19 )) ||
          (parm.player6.equalsIgnoreCase( parm.player20 )) || (parm.player6.equalsIgnoreCase( parm.player21 )) ||
          (parm.player6.equalsIgnoreCase( parm.player22 )) || (parm.player6.equalsIgnoreCase( parm.player23 )) ||
          (parm.player6.equalsIgnoreCase( parm.player24 )) || (parm.player6.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player6);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player6 );     // space is the default token

      if ( tok.countTokens() == 1 ) {
          
         invData(out, parm.player6);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player6);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname6 = tok.nextToken();
         parm.lname6 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname6 = tok.nextToken();
         parm.mi6 = tok.nextToken();
         parm.lname6 = tok.nextToken();
      }
   }

   if (!parm.player7.equals( "" ) && parm.g[6].equals( "" )) {

      if ((parm.player7.equalsIgnoreCase( parm.player8 )) || (parm.player7.equalsIgnoreCase( parm.player9 )) ||
          (parm.player7.equalsIgnoreCase( parm.player10 )) || (parm.player7.equalsIgnoreCase( parm.player11 )) ||
          (parm.player7.equalsIgnoreCase( parm.player12 )) || (parm.player7.equalsIgnoreCase( parm.player13 )) ||
          (parm.player7.equalsIgnoreCase( parm.player14 )) || (parm.player7.equalsIgnoreCase( parm.player15 )) ||
          (parm.player7.equalsIgnoreCase( parm.player16 )) || (parm.player7.equalsIgnoreCase( parm.player17 )) ||
          (parm.player7.equalsIgnoreCase( parm.player18 )) || (parm.player7.equalsIgnoreCase( parm.player19 )) ||
          (parm.player7.equalsIgnoreCase( parm.player20 )) || (parm.player7.equalsIgnoreCase( parm.player21 )) ||
          (parm.player7.equalsIgnoreCase( parm.player22 )) || (parm.player7.equalsIgnoreCase( parm.player23 )) ||
          (parm.player7.equalsIgnoreCase( parm.player24 )) || (parm.player7.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player7);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player7 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player7);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player7);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname7 = tok.nextToken();
         parm.lname7 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname7 = tok.nextToken();
         parm.mi7 = tok.nextToken();
         parm.lname7 = tok.nextToken();
      }
   }

   if (!parm.player8.equals( "" ) && parm.g[7].equals( "" )) {

      if ((parm.player8.equalsIgnoreCase( parm.player9 )) ||
          (parm.player8.equalsIgnoreCase( parm.player10 )) || (parm.player8.equalsIgnoreCase( parm.player11 )) ||
          (parm.player8.equalsIgnoreCase( parm.player12 )) || (parm.player8.equalsIgnoreCase( parm.player13 )) ||
          (parm.player8.equalsIgnoreCase( parm.player14 )) || (parm.player8.equalsIgnoreCase( parm.player15 )) ||
          (parm.player8.equalsIgnoreCase( parm.player16 )) || (parm.player8.equalsIgnoreCase( parm.player17 )) ||
          (parm.player8.equalsIgnoreCase( parm.player18 )) || (parm.player8.equalsIgnoreCase( parm.player19 )) ||
          (parm.player8.equalsIgnoreCase( parm.player20 )) || (parm.player8.equalsIgnoreCase( parm.player21 )) ||
          (parm.player8.equalsIgnoreCase( parm.player22 )) || (parm.player8.equalsIgnoreCase( parm.player23 )) ||
          (parm.player8.equalsIgnoreCase( parm.player24 )) || (parm.player8.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player8);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player8 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player8);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player8);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname8 = tok.nextToken();
         parm.lname8 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname8 = tok.nextToken();
         parm.mi8 = tok.nextToken();
         parm.lname8 = tok.nextToken();
      }
   }

   if (!parm.player9.equals( "" ) && parm.g[8].equals( "" )) {

      if ((parm.player9.equalsIgnoreCase( parm.player10 )) || (parm.player9.equalsIgnoreCase( parm.player11 )) ||
          (parm.player9.equalsIgnoreCase( parm.player12 )) || (parm.player9.equalsIgnoreCase( parm.player13 )) ||
          (parm.player9.equalsIgnoreCase( parm.player14 )) || (parm.player9.equalsIgnoreCase( parm.player15 )) ||
          (parm.player9.equalsIgnoreCase( parm.player16 )) || (parm.player9.equalsIgnoreCase( parm.player17 )) ||
          (parm.player9.equalsIgnoreCase( parm.player18 )) || (parm.player9.equalsIgnoreCase( parm.player19 )) ||
          (parm.player9.equalsIgnoreCase( parm.player20 )) || (parm.player9.equalsIgnoreCase( parm.player21 )) ||
          (parm.player9.equalsIgnoreCase( parm.player22 )) || (parm.player9.equalsIgnoreCase( parm.player23 )) ||
          (parm.player9.equalsIgnoreCase( parm.player24 )) || (parm.player9.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player9);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player9 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player9);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player9);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname9 = tok.nextToken();
         parm.lname9 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname9 = tok.nextToken();
         parm.mi9 = tok.nextToken();
         parm.lname9 = tok.nextToken();
      }
   }

   if (!parm.player10.equals( "" ) && parm.g[9].equals( "" )) {

      if ((parm.player10.equalsIgnoreCase( parm.player11 )) ||
          (parm.player10.equalsIgnoreCase( parm.player12 )) || (parm.player10.equalsIgnoreCase( parm.player13 )) ||
          (parm.player10.equalsIgnoreCase( parm.player14 )) || (parm.player10.equalsIgnoreCase( parm.player15 )) ||
          (parm.player10.equalsIgnoreCase( parm.player16 )) || (parm.player10.equalsIgnoreCase( parm.player17 )) ||
          (parm.player10.equalsIgnoreCase( parm.player18 )) || (parm.player10.equalsIgnoreCase( parm.player19 )) ||
          (parm.player10.equalsIgnoreCase( parm.player20 )) || (parm.player10.equalsIgnoreCase( parm.player21 )) ||
          (parm.player10.equalsIgnoreCase( parm.player22 )) || (parm.player10.equalsIgnoreCase( parm.player23 )) ||
          (parm.player10.equalsIgnoreCase( parm.player24 )) || (parm.player10.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player10);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player10 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player10);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player10);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname10 = tok.nextToken();
         parm.lname10 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname10 = tok.nextToken();
         parm.mi10 = tok.nextToken();
         parm.lname10 = tok.nextToken();
      }
   }

   if (!parm.player11.equals( "" ) && parm.g[10].equals( "" )) {

      if ((parm.player11.equalsIgnoreCase( parm.player12 )) || (parm.player11.equalsIgnoreCase( parm.player13 )) ||
          (parm.player11.equalsIgnoreCase( parm.player14 )) || (parm.player11.equalsIgnoreCase( parm.player15 )) ||
          (parm.player11.equalsIgnoreCase( parm.player16 )) || (parm.player11.equalsIgnoreCase( parm.player17 )) ||
          (parm.player11.equalsIgnoreCase( parm.player18 )) || (parm.player11.equalsIgnoreCase( parm.player19 )) ||
          (parm.player11.equalsIgnoreCase( parm.player20 )) || (parm.player11.equalsIgnoreCase( parm.player21 )) ||
          (parm.player11.equalsIgnoreCase( parm.player22 )) || (parm.player11.equalsIgnoreCase( parm.player23 )) ||
          (parm.player11.equalsIgnoreCase( parm.player24 )) || (parm.player11.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player11);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player11 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player11);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player11);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname11 = tok.nextToken();
         parm.lname11 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname11 = tok.nextToken();
         parm.mi11 = tok.nextToken();
         parm.lname11 = tok.nextToken();
      }
   }

   if (!parm.player12.equals( "" ) && parm.g[11].equals( "" )) {

      if ((parm.player12.equalsIgnoreCase( parm.player13 )) ||
          (parm.player12.equalsIgnoreCase( parm.player14 )) || (parm.player12.equalsIgnoreCase( parm.player15 )) ||
          (parm.player12.equalsIgnoreCase( parm.player16 )) || (parm.player12.equalsIgnoreCase( parm.player17 )) ||
          (parm.player12.equalsIgnoreCase( parm.player18 )) || (parm.player12.equalsIgnoreCase( parm.player19 )) ||
          (parm.player12.equalsIgnoreCase( parm.player20 )) || (parm.player12.equalsIgnoreCase( parm.player21 )) ||
          (parm.player12.equalsIgnoreCase( parm.player22 )) || (parm.player12.equalsIgnoreCase( parm.player23 )) ||
          (parm.player12.equalsIgnoreCase( parm.player24 )) || (parm.player12.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player12);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player12 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player12);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player12);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname12 = tok.nextToken();
         parm.lname12 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname12 = tok.nextToken();
         parm.mi12 = tok.nextToken();
         parm.lname12 = tok.nextToken();
      }
   }

   if (!parm.player13.equals( "" ) && parm.g[12].equals( "" )) {

      if ((parm.player13.equalsIgnoreCase( parm.player14 )) || (parm.player13.equalsIgnoreCase( parm.player15 )) ||
          (parm.player13.equalsIgnoreCase( parm.player16 )) || (parm.player13.equalsIgnoreCase( parm.player17 )) ||
          (parm.player13.equalsIgnoreCase( parm.player18 )) || (parm.player13.equalsIgnoreCase( parm.player19 )) ||
          (parm.player13.equalsIgnoreCase( parm.player20 )) || (parm.player13.equalsIgnoreCase( parm.player21 )) ||
          (parm.player13.equalsIgnoreCase( parm.player22 )) || (parm.player13.equalsIgnoreCase( parm.player23 )) ||
          (parm.player13.equalsIgnoreCase( parm.player24 )) || (parm.player13.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player13);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player13 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player13);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player13);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname13 = tok.nextToken();
         parm.lname13 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname13 = tok.nextToken();
         parm.mi13 = tok.nextToken();
         parm.lname13 = tok.nextToken();
      }
   }

   if (!parm.player14.equals( "" ) && parm.g[13].equals( "" )) {

      if ((parm.player14.equalsIgnoreCase( parm.player15 )) ||
          (parm.player14.equalsIgnoreCase( parm.player16 )) || (parm.player14.equalsIgnoreCase( parm.player17 )) ||
          (parm.player14.equalsIgnoreCase( parm.player18 )) || (parm.player14.equalsIgnoreCase( parm.player19 )) ||
          (parm.player14.equalsIgnoreCase( parm.player20 )) || (parm.player14.equalsIgnoreCase( parm.player21 )) ||
          (parm.player14.equalsIgnoreCase( parm.player22 )) || (parm.player14.equalsIgnoreCase( parm.player23 )) ||
          (parm.player14.equalsIgnoreCase( parm.player24 )) || (parm.player14.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player14);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player14 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player14);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player14);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname14 = tok.nextToken();
         parm.lname14 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname14 = tok.nextToken();
         parm.mi14 = tok.nextToken();
         parm.lname14 = tok.nextToken();
      }
   }

   if (!parm.player15.equals( "" ) && parm.g[14].equals( "" )) {

      if ((parm.player15.equalsIgnoreCase( parm.player16 )) || (parm.player15.equalsIgnoreCase( parm.player17 )) ||
          (parm.player15.equalsIgnoreCase( parm.player18 )) || (parm.player15.equalsIgnoreCase( parm.player19 )) ||
          (parm.player15.equalsIgnoreCase( parm.player20 )) || (parm.player15.equalsIgnoreCase( parm.player21 )) ||
          (parm.player15.equalsIgnoreCase( parm.player22 )) || (parm.player15.equalsIgnoreCase( parm.player23 )) ||
          (parm.player15.equalsIgnoreCase( parm.player24 )) || (parm.player15.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player15);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player15 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player15);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player15);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname15 = tok.nextToken();
         parm.lname15 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname15 = tok.nextToken();
         parm.mi15 = tok.nextToken();
         parm.lname15 = tok.nextToken();
      }
   }

   if (!parm.player16.equals( "" ) && parm.g[15].equals( "" )) {

      if ((parm.player16.equalsIgnoreCase( parm.player17 )) ||
          (parm.player16.equalsIgnoreCase( parm.player18 )) || (parm.player16.equalsIgnoreCase( parm.player19 )) ||
          (parm.player16.equalsIgnoreCase( parm.player20 )) || (parm.player16.equalsIgnoreCase( parm.player21 )) ||
          (parm.player16.equalsIgnoreCase( parm.player22 )) || (parm.player16.equalsIgnoreCase( parm.player23 )) ||
          (parm.player16.equalsIgnoreCase( parm.player24 )) || (parm.player16.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player16);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player16 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player16);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player16);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname16 = tok.nextToken();
         parm.lname16 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname16 = tok.nextToken();
         parm.mi16 = tok.nextToken();
         parm.lname16 = tok.nextToken();
      }
   }

   if (!parm.player17.equals( "" ) && parm.g[16].equals( "" )) {

      if ((parm.player17.equalsIgnoreCase( parm.player18 )) || (parm.player17.equalsIgnoreCase( parm.player19 )) ||
          (parm.player17.equalsIgnoreCase( parm.player20 )) || (parm.player17.equalsIgnoreCase( parm.player21 )) ||
          (parm.player17.equalsIgnoreCase( parm.player22 )) || (parm.player17.equalsIgnoreCase( parm.player23 )) ||
          (parm.player17.equalsIgnoreCase( parm.player24 )) || (parm.player17.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player17);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player17 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player17);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player17);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname17 = tok.nextToken();
         parm.lname17 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname17 = tok.nextToken();
         parm.mi17 = tok.nextToken();
         parm.lname17 = tok.nextToken();
      }
   }

   if (!parm.player18.equals( "" ) && parm.g[17].equals( "" )) {

      if ((parm.player18.equalsIgnoreCase( parm.player19 )) ||
          (parm.player18.equalsIgnoreCase( parm.player20 )) || (parm.player18.equalsIgnoreCase( parm.player21 )) ||
          (parm.player18.equalsIgnoreCase( parm.player22 )) || (parm.player18.equalsIgnoreCase( parm.player23 )) ||
          (parm.player18.equalsIgnoreCase( parm.player24 )) || (parm.player18.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player18);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player18 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player18);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player18);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname18 = tok.nextToken();
         parm.lname18 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname18 = tok.nextToken();
         parm.mi18 = tok.nextToken();
         parm.lname18 = tok.nextToken();
      }
   }

   if (!parm.player19.equals( "" ) && parm.g[18].equals( "" )) {

      if ((parm.player19.equalsIgnoreCase( parm.player20 )) || (parm.player19.equalsIgnoreCase( parm.player21 )) ||
          (parm.player19.equalsIgnoreCase( parm.player22 )) || (parm.player19.equalsIgnoreCase( parm.player23 )) ||
          (parm.player19.equalsIgnoreCase( parm.player24 )) || (parm.player19.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player19);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player19 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player19);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player19);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname19 = tok.nextToken();
         parm.lname19 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname19 = tok.nextToken();
         parm.mi19 = tok.nextToken();
         parm.lname19 = tok.nextToken();
      }
   }

   if (!parm.player20.equals( "" ) && parm.g[19].equals( "" )) {

      if ((parm.player20.equalsIgnoreCase( parm.player21 )) ||
          (parm.player20.equalsIgnoreCase( parm.player22 )) || (parm.player20.equalsIgnoreCase( parm.player23 )) ||
          (parm.player20.equalsIgnoreCase( parm.player24 )) || (parm.player20.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player20);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player20 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player20);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player20);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname20 = tok.nextToken();
         parm.lname20 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname20 = tok.nextToken();
         parm.mi20 = tok.nextToken();
         parm.lname20 = tok.nextToken();
      }
   }

   if (!parm.player21.equals( "" ) && parm.g[20].equals( "" )) {

      if ((parm.player21.equalsIgnoreCase( parm.player22 )) || (parm.player21.equalsIgnoreCase( parm.player23 )) ||
          (parm.player21.equalsIgnoreCase( parm.player24 )) || (parm.player21.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player21);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player21 );     // space is the default token

      if ( tok.countTokens() == 1 ) {
          
         invData(out, parm.player21);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player21);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname21 = tok.nextToken();
         parm.lname21 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname21 = tok.nextToken();
         parm.mi21 = tok.nextToken();
         parm.lname21 = tok.nextToken();
      }
   }

   if (!parm.player22.equals( "" ) && parm.g[21].equals( "" )) {

      if ((parm.player22.equalsIgnoreCase( parm.player23 )) ||
          (parm.player22.equalsIgnoreCase( parm.player24 )) || (parm.player22.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player22);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player22 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player22);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player22);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname22 = tok.nextToken();
         parm.lname22 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname22 = tok.nextToken();
         parm.mi22 = tok.nextToken();
         parm.lname22 = tok.nextToken();
      }
   }

   if (!parm.player23.equals( "" ) && parm.g[22].equals( "" )) {

      if ((parm.player23.equalsIgnoreCase( parm.player24 )) || (parm.player23.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player23);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player23 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player23);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player23);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname23 = tok.nextToken();
         parm.lname23 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname23 = tok.nextToken();
         parm.mi23 = tok.nextToken();
         parm.lname23 = tok.nextToken();
      }
   }

   if (!parm.player24.equals( "" ) && parm.g[23].equals( "" )) {

      if ((parm.player24.equalsIgnoreCase( parm.player25 ))) {

         dupData(out, parm.player24);                        // reject
         error = true;
         return(error);
      }

      StringTokenizer tok = new StringTokenizer( parm.player24 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player24);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player24);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname24 = tok.nextToken();
         parm.lname24 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname24 = tok.nextToken();
         parm.mi24 = tok.nextToken();
         parm.lname24 = tok.nextToken();
      }
   }

   if (!parm.player25.equals( "" ) && parm.g[24].equals( "" )) {

      StringTokenizer tok = new StringTokenizer( parm.player25 );     // space is the default token

      if ( tok.countTokens() == 1 ) {

         invData(out, parm.player25);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() > 3 ) {          // too many name fields

         invData(out, parm.player25);                        // reject
         error = true;
         return(error);
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         parm.fname25 = tok.nextToken();
         parm.lname25 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         parm.fname25 = tok.nextToken();
         parm.mi25 = tok.nextToken();
         parm.lname25 = tok.nextToken();
      }
   }
   return(error);
 }


 // *******************************************************************************
 //  Check for guests
 // *******************************************************************************
 //
 private int processGuests(Connection con, parmLott parm) {


   //Statement stmtx2 = null;
   //ResultSet rs = null;

   int guestErr = 0;
   int i = 0;
   int i2 = 0;
     
   String gplayer = "";
   String club = parm.club;

   String [] playerA = new String [25];       // array to hold the player values
   String [] oldplayerA = new String [25];    // array to hold the old player values
   int [] guest_idA = new int [25];

   boolean invalid = false;
   boolean guestdbTbaAllowed = false;


   //  If guest tracking is in use, determine whether names are optional or required
   if (Utilities.isGuestTrackingConfigured(0, con) && Utilities.isGuestTrackingTbaAllowed(0, false, con)) {
       guestdbTbaAllowed = true;
   }

   //
   //  save the player values in the arrays
   //
   playerA[0] = parm.player1;
   playerA[1] = parm.player2;
   playerA[2] = parm.player3;
   playerA[3] = parm.player4;
   playerA[4] = parm.player5;
   playerA[5] = parm.player6;
   playerA[6] = parm.player7;
   playerA[7] = parm.player8;
   playerA[8] = parm.player9;
   playerA[9] = parm.player10;
   playerA[10] = parm.player11;
   playerA[11] = parm.player12;
   playerA[12] = parm.player13;
   playerA[13] = parm.player14;
   playerA[14] = parm.player15;
   playerA[15] = parm.player16;
   playerA[16] = parm.player17;
   playerA[17] = parm.player18;
   playerA[18] = parm.player19;
   playerA[19] = parm.player20;
   playerA[20] = parm.player21;
   playerA[21] = parm.player22;
   playerA[22] = parm.player23;
   playerA[23] = parm.player24;
   playerA[24] = parm.player25;
   oldplayerA[0] = parm.oldplayer1;
   oldplayerA[1] = parm.oldplayer2;
   oldplayerA[2] = parm.oldplayer3;
   oldplayerA[3] = parm.oldplayer4;
   oldplayerA[4] = parm.oldplayer5;
   oldplayerA[5] = parm.oldplayer6;
   oldplayerA[6] = parm.oldplayer7;
   oldplayerA[7] = parm.oldplayer8;
   oldplayerA[8] = parm.oldplayer9;
   oldplayerA[9] = parm.oldplayer10;
   oldplayerA[10] = parm.oldplayer11;
   oldplayerA[11] = parm.oldplayer12;
   oldplayerA[12] = parm.oldplayer13;
   oldplayerA[13] = parm.oldplayer14;
   oldplayerA[14] = parm.oldplayer15;
   oldplayerA[15] = parm.oldplayer16;
   oldplayerA[16] = parm.oldplayer17;
   oldplayerA[17] = parm.oldplayer18;
   oldplayerA[18] = parm.oldplayer19;
   oldplayerA[19] = parm.oldplayer20;
   oldplayerA[20] = parm.oldplayer21;
   oldplayerA[21] = parm.oldplayer22;
   oldplayerA[22] = parm.oldplayer23;
   oldplayerA[23] = parm.oldplayer24;
   oldplayerA[24] = parm.oldplayer25;
   guest_idA[0] = parm.guest_id1;
   guest_idA[1] = parm.guest_id2;
   guest_idA[2] = parm.guest_id3;
   guest_idA[3] = parm.guest_id4;
   guest_idA[4] = parm.guest_id5;
   guest_idA[5] = parm.guest_id6;
   guest_idA[6] = parm.guest_id7;
   guest_idA[7] = parm.guest_id8;
   guest_idA[8] = parm.guest_id9;
   guest_idA[9] = parm.guest_id10;
   guest_idA[10] = parm.guest_id11;
   guest_idA[11] = parm.guest_id12;
   guest_idA[12] = parm.guest_id13;
   guest_idA[13] = parm.guest_id14;
   guest_idA[14] = parm.guest_id15;
   guest_idA[15] = parm.guest_id16;
   guest_idA[16] = parm.guest_id17;
   guest_idA[17] = parm.guest_id18;
   guest_idA[18] = parm.guest_id19;
   guest_idA[19] = parm.guest_id20;
   guest_idA[20] = parm.guest_id21;
   guest_idA[21] = parm.guest_id22;
   guest_idA[22] = parm.guest_id23;
   guest_idA[23] = parm.guest_id24;
   guest_idA[24] = parm.guest_id25;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm2 = new parmClub(0, con); // hard code a zero for the root activity since FlxRez doesn't support lotteries


   //
   //   Get the guest names specified for this club
   //
   try {
      getClub.getParms(con, parm2);        // get the club parms

   }
   catch (Exception ignore) {
   }

   //
   //   Remove any guest types that are null - for tests below
   //
   for (i = 0; i < parm2.MAX_Guests; i++) {

      if (parm2.guest[i].equals( "" )) {

         parm2.guest[i] = "$@#!^&*";      // make so it won't match player name
      }
   }         // end of while loop

   //
   //  Check if any player names are guest names
   //
   i = 0;
   while (i < 25) {

      parm.gstA[i] = "";    // init guest array and indicators
      i++;
   }

   //
   //  Process each player
   //
   loop1:
   for (i2=0; i2<25; i2++) {

      parm.g[i2] = "";
      gplayer = "";
      if (!playerA[i2].equals( "" )) {

         loop2:
         for (i=0; i<parm2.MAX_Guests; i++) {

            if (playerA[i2].startsWith( parm2.guest[i] )) {

               parm.g[i2] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[i2] = playerA[i2];       // save guest value
               parm.guests++;                    // increment number of guests this request
               parm.guestsg1++;                  // increment number of guests this slot

               if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                  if (!playerA[i2].equals( oldplayerA[i2] )) {      // if new or changed player name

                     gplayer = playerA[i2];                         // indicate error (ok if it was already entered by pro)
                     guestErr = 1;
                     break loop1;                                   // exit both loops
                  }
               }
                 
               //
               //  Check for guest name is requested for this club and its NOT a 'Lottery TBA'
               //
               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || guest_idA[i2] != 0 || !playerA[i2].equals(parm2.guest[i] + " TBA")) {
                   
                       if (guest_idA[i2] == 0) {
                           invalid = true;
                       } else {
                           invalid = verifySlot.checkTrackedGuestName(playerA[i2], guest_idA[i2], parm2.guest[i], club, con);
                       }

                       if (invalid) {
                           parm.gplayer = parm.player1;    // indicate error
                           guestErr = 3;
                           break loop1;                                   // exit both loops
                       }
                   }
                   
               } else if (parm2.forceg > 0 && !parm2.guest[i].startsWith("TBA Lottery") && !parm2.guest[i].startsWith("Lottery TBA")) {   // if force names config'ed and NOT 'TBA Lottery'

                   invalid = verifySlot.checkGstName(playerA[i2], parm2.guest[i], club);      // go check for a name

                   if (invalid == true) {                                    // if name not specified

                       if (!playerA[i2].equals( oldplayerA[i2] )) {      // if new or changed player name

                           gplayer = playerA[i2];                         // indicate error (ok if it was already entered by pro)
                           guestErr = 2;
                           break loop1;                                   // exit both loops
                       }
                   }
               }

               break loop2;
            }
         }         // end of while loop
      }
   }

   parm.player = gplayer;            // save player name if error
     
   return(guestErr);
 }


 // *******************************************************************************
 //  Check membership restrictions - max rounds per week, month or year
 // *******************************************************************************
 //
 private boolean checkMemship(Connection con, PrintWriter out, parmLott parm, String day) {


   ResultSet rs = null;

   boolean check = false;
   parm.error = false;               // init

   //String rest_name = "";
   //String rest_recurr = "";
   //String rest_course = "";
   //String rest_fb = "";
   //String sfb = "";
   String mship = "";
   String player = "";
   String period = "";
   String mperiod = "";
   //String course = parm.course;

   //int rest_stime = 0;
   //int rest_etime = 0;
   //int mems = 0;
   int mtimes = 0;
   int ind = 0;
   int i = 0;
   //int time = parm.time;
   int year = 0;
   int month = 0;
   int dayNum = 0;
   int count = 0;
   int mm = parm.mm;
   int yy = parm.yy;
   int dd = parm.dd;

   long date = parm.date;
   long dateEnd = 0;
   long dateStart = 0;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm2 = new parmClub(0, con); // hard code a zero for the root activity since FlxRez doesn't support lotteries

   int [] mtimesA = new int [parm2.MAX_Mships+1];       // array to hold the membership time values
   String [] mshipA = new String [parm2.MAX_Mships+1];     // array to hold the membership names
   String [] periodA = new String [parm2.MAX_Mships+1];    // array to hold the membership periods

    //
    //  Get this date's calendar and then determine start and end of week.
    //
    int calmm = mm - 1;                            // adjust month value for cal

    Calendar cal = new GregorianCalendar();       // get todays date

    //
    //  set cal to tee time's date
    //
    cal.set(Calendar.YEAR,yy);               // set year in cal
    cal.set(Calendar.MONTH,calmm);                // set month in cal
    cal.set(Calendar.DAY_OF_MONTH,dd);       // set day in cal

    ind = cal.get(Calendar.DAY_OF_WEEK);          // day of week (01 - 07)
    ind = 7 - ind;                                // number of days to end of week

    //
    // roll cal ahead to find Saturday's date (end of week)
    //
    if (ind != 0) {                               // if not today

       cal.add(Calendar.DATE,ind);                // roll ahead (ind) days
    }

    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    dayNum = cal.get(Calendar.DAY_OF_MONTH);

    month = month + 1;                            // month starts at zero

    dateEnd = year * 10000;                       // create a date field of yyyymmdd
    dateEnd = dateEnd + (month * 100);
    dateEnd = dateEnd + dayNum;                      // date = yyyymmdd (for comparisons)

    //
    // roll cal back 6 days to find Sunday's date (start of week)
    //
    cal.add(Calendar.DATE,-6);                    // roll back 6 days

    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    dayNum = cal.get(Calendar.DAY_OF_MONTH);

    month = month + 1;                            // month starts at zero

    dateStart = year * 10000;                     // create a date field of yyyymmdd
    dateStart = dateStart + (month * 100);
    dateStart = dateStart + dayNum;                  // date = yyyymmdd (for comparisons)

   //
   //  init the string arrays
   //
   for (i=0; i<parm2.MAX_Mships+1; i++) {
      mshipA[i] = "";
      mtimesA[i] = 0;
      periodA[i] = "";
   }

    //
    //  Get membership types, number of rounds and time periods (week, month, year)
    //
    try {

       Statement stmt = con.createStatement();

       rs = stmt.executeQuery("SELECT mship, mtimes, period FROM mship5 WHERE activity_id = 0 LIMIT " + parm2.MAX_Mships);

       i = 1;

       while (rs.next()) {

           mshipA[i] = rs.getString("mship");
           mtimesA[i] = rs.getInt("mtimes");
           periodA[i] = rs.getString("period");

           i++;
       }

       //
       //   Check each player's mship
       //
       if (!parm.mship1.equals( "" )) {          // check if player 1 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop1:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship1.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop1;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user1, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship1;
                player = parm.player1;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 1 if

       if (!parm.mship2.equals( "" )) {          // check if player 2 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop2:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship2.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop2;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user2, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship2;
                player = parm.player2;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 2 if

       if (!parm.mship3.equals( "" )) {          // check if player 3 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop3:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship3.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop3;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user3, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship3;
                player = parm.player3;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 3 if

       if (!parm.mship4.equals( "" )) {          // check if player 4 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop4:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship4.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop4;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user4, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship4;
                player = parm.player4;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 4 if

       if (!parm.mship5.equals( "" )) {          // check if player 5 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop5:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship5.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop5;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user5, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship5;
                player = parm.player5;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 5 if

       if (!parm.mship6.equals( "" )) {          // check if player 6 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop6:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship6.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop6;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user6, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship6;
                player = parm.player6;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 6 if

       if (!parm.mship7.equals( "" )) {          // check if player 7 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop7:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship7.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop7;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user7, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship7;
                player = parm.player7;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 7 if

       if (!parm.mship8.equals( "" )) {          // check if player 8 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop8:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship8.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop8;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user8, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship8;
                player = parm.player8;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 8 if

       if (!parm.mship9.equals( "" )) {          // check if player 9 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop9:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship9.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop9;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user9, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship9;
                player = parm.player9;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 9 if

       if (!parm.mship10.equals( "" )) {          // check if player 10 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop10:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship10.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop10;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user10, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship10;
                player = parm.player10;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 10 if

       if (!parm.mship11.equals( "" )) {          // check if player 11 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop11:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship11.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop11;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user11, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship11;
                player = parm.player11;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 11 if

       if (!parm.mship12.equals( "" )) {          // check if player 12 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop12:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship12.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop12;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user12, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship12;
                player = parm.player12;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 12 if

       if (!parm.mship13.equals( "" )) {          // check if player 13 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop13:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship13.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop13;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user13, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship13;
                player = parm.player13;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 13 if

       if (!parm.mship14.equals( "" )) {          // check if player 14 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop14:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship14.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop14;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user14, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship14;
                player = parm.player14;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 14 if

       if (!parm.mship15.equals( "" )) {          // check if player 15 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop15:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship15.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop15;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user15, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship15;
                player = parm.player15;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 15 if

       if (!parm.mship16.equals( "" )) {          // check if player 16 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop16:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship16.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop16;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user16, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship16;
                player = parm.player16;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 16 if

       if (!parm.mship17.equals( "" )) {          // check if player 17 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop17:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship17.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop17;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user17, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship17;
                player = parm.player17;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 17 if

       if (!parm.mship18.equals( "" )) {          // check if player 18 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop18:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship18.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop18;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user18, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship18;
                player = parm.player18;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 18 if

       if (!parm.mship19.equals( "" )) {          // check if player 19 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop19:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship19.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop19;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user19, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship19;
                player = parm.player19;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 19 if

       if (!parm.mship20.equals( "" )) {          // check if player 20 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop20:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship20.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop20;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user20, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship20;
                player = parm.player20;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 20 if

       if (!parm.mship21.equals( "" )) {          // check if player 21 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop21:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship21.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop21;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user21, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship21;
                player = parm.player21;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 21 if

       if (!parm.mship22.equals( "" )) {          // check if player 22 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop22:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship22.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop22;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user22, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship22;
                player = parm.player22;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 22 if

       if (!parm.mship23.equals( "" )) {          // check if player 23 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop23:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship23.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop23;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user23, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship23;
                player = parm.player23;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 23 if

       if (!parm.mship24.equals( "" )) {          // check if player 24 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop24:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship24.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop24;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user24, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship24;
                player = parm.player24;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 24 if

       if (!parm.mship25.equals( "" )) {          // check if player 25 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop25:
          while (ind < parm2.MAX_Mships+1) {

             if (parm.mship25.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop25;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user25, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship25;
                player = parm.player25;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 25 if

   }
   catch (Exception e7) {

     dbError(out, e7);
     parm.error = true;               // inform caller of error
   }

   //
   //  save parms if error
   //
   parm.player = player;
   parm.mship= mship;
   parm.period = period;

   return(check);

 }         // end of checkMemShip



 // *******************************************************************************
 //  Check member restrictions
 //
 //     First, find all restrictions within date & time constraints on this course.
 //     Then, find the ones for this day.
 //     Then, find any for this member type or membership type (all 25 possible players).
 //
 // *******************************************************************************
 //
 private boolean checkMemRes(Connection con, PrintWriter out, parmLott parm, String day) {


   ResultSet rs = null;

   boolean check = false;
   parm.error = false;               // init

   String rest_name = "";
   String rest_recurr = "";
   //String rest_course = "";
   String rest_fb = "";
   String sfb = "";
   String player = "";
   String course = parm.course;

   //int rest_stime = 0;
   //int rest_etime = 0;
   //int mems = 0;
   int ind = 0;
   int time = parm.time;
   int mrest_id = 0;

   long date = parm.date;

   String [] mtypeA = new String [24];     // array to hold the member type names
   String [] mshipA = new String [24];     // array to hold the membership names

   try {

      PreparedStatement pstmt7 = con.prepareStatement (
         "SELECT * FROM restriction2 " +
         "WHERE activity_id = 0 AND sdate <= ? AND edate >= ? AND " +
         "stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-')");


      pstmt7.clearParameters();          // clear the parms
      pstmt7.setLong(1, date);
      pstmt7.setLong(2, date);
      pstmt7.setInt(3, time);
      pstmt7.setInt(4, time);
      pstmt7.setString(5, course);

      rs = pstmt7.executeQuery();      // find all matching restrictions, if any

      check = false;                     // init 'hit' flag

      if (parm.fb == 0) {                   // is Tee time for Front 9?

         sfb = "Front";
      }

      if (parm.fb == 1) {                   // is it Back 9?

         sfb = "Back";
      }

      loop2:
      while (rs.next()) {              // check all matching restrictions for this day, mship, mtype & F/B

         rest_name = rs.getString("name");
         rest_recurr = rs.getString("recurr");
         mrest_id = rs.getInt("id");
         mtypeA[0] = rs.getString("mem1");
         mtypeA[1] = rs.getString("mem2");
         mtypeA[2] = rs.getString("mem3");
         mtypeA[3] = rs.getString("mem4");
         mtypeA[4] = rs.getString("mem5");
         mtypeA[5] = rs.getString("mem6");
         mtypeA[6] = rs.getString("mem7");
         mtypeA[7] = rs.getString("mem8");
         mtypeA[8] = rs.getString("mem9");
         mtypeA[9] = rs.getString("mem10");
         mtypeA[10] = rs.getString("mem11");
         mtypeA[11] = rs.getString("mem12");
         mtypeA[12] = rs.getString("mem13");
         mtypeA[13] = rs.getString("mem14");
         mtypeA[14] = rs.getString("mem15");
         mtypeA[15] = rs.getString("mem16");
         mtypeA[16] = rs.getString("mem17");
         mtypeA[17] = rs.getString("mem18");
         mtypeA[18] = rs.getString("mem19");
         mtypeA[19] = rs.getString("mem20");
         mtypeA[20] = rs.getString("mem21");
         mtypeA[21] = rs.getString("mem22");
         mtypeA[22] = rs.getString("mem23");
         mtypeA[23] = rs.getString("mem24");
         mshipA[0] = rs.getString("mship1");
         mshipA[1] = rs.getString("mship2");
         mshipA[2] = rs.getString("mship3");
         mshipA[3] = rs.getString("mship4");
         mshipA[4] = rs.getString("mship5");
         mshipA[5] = rs.getString("mship6");
         mshipA[6] = rs.getString("mship7");
         mshipA[7] = rs.getString("mship8");
         mshipA[8] = rs.getString("mship9");
         mshipA[9] = rs.getString("mship10");
         mshipA[10] = rs.getString("mship11");
         mshipA[11] = rs.getString("mship12");
         mshipA[12] = rs.getString("mship13");
         mshipA[13] = rs.getString("mship14");
         mshipA[14] = rs.getString("mship15");
         mshipA[15] = rs.getString("mship16");
         mshipA[16] = rs.getString("mship17");
         mshipA[17] = rs.getString("mship18");
         mshipA[18] = rs.getString("mship19");
         mshipA[19] = rs.getString("mship20");
         mshipA[20] = rs.getString("mship21");
         mshipA[21] = rs.getString("mship22");
         mshipA[22] = rs.getString("mship23");
         mshipA[23] = rs.getString("mship24");
         rest_fb = rs.getString("fb");

         //
         //  We must check the recurrence for this day (Monday, etc.)
         //
         if ((rest_recurr.equals( "Every " + day )) ||               // if this day
             (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
             ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
               (!day.equalsIgnoreCase( "saturday" )) &&
               (!day.equalsIgnoreCase( "sunday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
              (day.equalsIgnoreCase( "saturday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
              (day.equalsIgnoreCase( "sunday" )))) {

            //
            //  Now check if F/B matches
            //
            if ((rest_fb.equals( "Both" )) || (rest_fb.equals( sfb ))) {

               check = false;
               
               //
               // Make sure restriction isn't suspended
               //
               if (!verifySlot.checkRestSuspend(mrest_id, -99, (int)parm.date, parm.time, parm.day, parm.course, con)) {
                   //
                   //  Found a restriction that matches date, time, day & F/B - check mtype & mship of each member player
                   //
                   if (!parm.mship1.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player1;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship1.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype1.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 1 restrictions if

                   if (!parm.mship2.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player2;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship2.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype2.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 2 restrictions if

                   if (!parm.mship3.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player3;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship3.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype3.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 3 restrictions if

                   if (!parm.mship4.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player4;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship4.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype4.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 4 restrictions if

                   if (!parm.mship5.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player5;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship5.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype5.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 5 restrictions if

                   if (!parm.mship6.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player6;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship6.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype6.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 6 restrictions if

                   if (!parm.mship7.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player7;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship7.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype7.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 7 restrictions if

                   if (!parm.mship8.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player8;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship8.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype8.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 8 restrictions if

                   if (!parm.mship9.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player9;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship9.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype9.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 9 restrictions if

                   if (!parm.mship10.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player10;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship10.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype10.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 10 restrictions if

                   if (!parm.mship11.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player11;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship11.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype11.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 11 restrictions if

                   if (!parm.mship12.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player12;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship12.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype12.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 12 restrictions if

                   if (!parm.mship13.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player13;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship13.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype13.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 13 restrictions if

                   if (!parm.mship14.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player14;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship14.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype14.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 14 restrictions if

                   if (!parm.mship15.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player15;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship15.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype15.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 15 restrictions if

                   if (!parm.mship16.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player16;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship16.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype16.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 16 restrictions if

                   if (!parm.mship17.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player17;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship17.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype17.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 17 restrictions if

                   if (!parm.mship18.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player18;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship18.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype18.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 18 restrictions if

                   if (!parm.mship19.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player19;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship19.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype19.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 19 restrictions if

                   if (!parm.mship20.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player20;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship20.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype20.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 20 restrictions if

                   if (!parm.mship21.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player21;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship21.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype21.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 21 restrictions if

                   if (!parm.mship22.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player22;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship22.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype22.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 22 restrictions if

                   if (!parm.mship23.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player23;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship23.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype23.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 23 restrictions if

                   if (!parm.mship24.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player24;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship24.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype24.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 24 restrictions if

                   if (!parm.mship25.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player25;                  // save current player name

                      while (ind < 24) {

                         if ((parm.mship25.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype25.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 25 restrictions if
               }
            }     // end of IF F/B matches
         }     // end of 'day' if
      }       // end of while (no more restrictions)

      pstmt7.close();

   }
   catch (Exception e7) {

      dbError(out, e7);
      parm.error = true;               // inform caller of error
   }

   //
   //  save parms if error
   //
   parm.player = player;
   parm.rest_name = rest_name;

   return(check);

 }         // end of checkMemRes


 // *******************************************************************************
 //  Check custom Cherry Hills guest restrictions (Juniors w/o an adult)
 // *******************************************************************************
 //
 private boolean checkCherryJrs(parmLott parm) {


   boolean error = false;
     
   //
   //  Check if any Juniors in the request
   //
   if (parm.mtype1.startsWith( "Junior" ) || parm.mtype2.startsWith( "Junior" ) || parm.mtype3.startsWith( "Junior" ) ||
       parm.mtype4.startsWith( "Junior" ) || parm.mtype5.startsWith( "Junior" ) ||
       parm.mtype6.startsWith( "Junior" ) || parm.mtype7.startsWith( "Junior" ) || parm.mtype8.startsWith( "Junior" ) ||
       parm.mtype9.startsWith( "Junior" ) || parm.mtype10.startsWith( "Junior" ) ||
       parm.mtype11.startsWith( "Junior" ) || parm.mtype12.startsWith( "Junior" ) || parm.mtype13.startsWith( "Junior" ) ||
       parm.mtype14.startsWith( "Junior" ) || parm.mtype15.startsWith( "Junior" ) ||
       parm.mtype16.startsWith( "Junior" ) || parm.mtype17.startsWith( "Junior" ) || parm.mtype18.startsWith( "Junior" ) ||
       parm.mtype19.startsWith( "Junior" ) || parm.mtype20.startsWith( "Junior" ) ||
       parm.mtype21.startsWith( "Junior" ) || parm.mtype22.startsWith( "Junior" ) || parm.mtype23.startsWith( "Junior" ) ||
       parm.mtype24.startsWith( "Junior" ) || parm.mtype25.startsWith( "Junior" )) {

      error = true;     // default = error

      //
      //  Now check if any Adults
      //
      if (parm.mtype1.equals( "Member" ) || parm.mtype2.equals( "Member" ) || parm.mtype3.equals( "Member" ) ||
          parm.mtype4.equals( "Member" ) || parm.mtype5.equals( "Member" ) ||
          parm.mtype6.equals( "Member" ) || parm.mtype7.equals( "Member" ) || parm.mtype8.equals( "Member" ) ||
          parm.mtype9.equals( "Member" ) || parm.mtype10.equals( "Member" ) ||
          parm.mtype11.equals( "Member" ) || parm.mtype12.equals( "Member" ) || parm.mtype13.equals( "Member" ) ||
          parm.mtype14.equals( "Member" ) || parm.mtype15.equals( "Member" ) ||
          parm.mtype16.equals( "Member" ) || parm.mtype17.equals( "Member" ) || parm.mtype18.equals( "Member" ) ||
          parm.mtype19.equals( "Member" ) || parm.mtype20.equals( "Member" ) ||
          parm.mtype21.equals( "Member" ) || parm.mtype22.equals( "Member" ) || parm.mtype23.equals( "Member" ) ||
          parm.mtype24.equals( "Member" ) || parm.mtype25.equals( "Member" ) ||
          parm.mtype1.equals( "Spouse" ) || parm.mtype2.equals( "Spouse" ) || parm.mtype3.equals( "Spouse" ) ||
          parm.mtype4.equals( "Spouse" ) || parm.mtype5.equals( "Spouse" ) ||
          parm.mtype6.equals( "Spouse" ) || parm.mtype7.equals( "Spouse" ) || parm.mtype8.equals( "Spouse" ) ||
          parm.mtype9.equals( "Spouse" ) || parm.mtype10.equals( "Spouse" ) ||
          parm.mtype11.equals( "Spouse" ) || parm.mtype12.equals( "Spouse" ) || parm.mtype13.equals( "Spouse" ) ||
          parm.mtype14.equals( "Spouse" ) || parm.mtype15.equals( "Spouse" ) ||
          parm.mtype16.equals( "Spouse" ) || parm.mtype17.equals( "Spouse" ) || parm.mtype18.equals( "Spouse" ) ||
          parm.mtype19.equals( "Spouse" ) || parm.mtype20.equals( "Spouse" ) ||
          parm.mtype21.equals( "Spouse" ) || parm.mtype22.equals( "Spouse" ) || parm.mtype23.equals( "Spouse" ) ||
          parm.mtype24.equals( "Spouse" ) || parm.mtype25.equals( "Spouse" )) {

         error = false;     // ok if adult included
      }
   }

   return(error);

 }         // end of checkCherryJrs


 // *******************************************************************************
 //  Check custom Cherry Hills member restrictions
 // *******************************************************************************
 //
 private boolean checkCherryRes(parmLott parm) {


   boolean error = false;
   boolean go = false;

   //
   //  Allocate a new parm block for each tee time and call common method to process each.
   //
   parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

   //
   //  Setup the new single parm block
   //
   parm1.date = parm.date;
   parm1.time = parm.time;
   parm1.mm = parm.mm;
   parm1.yy = parm.yy;
   parm1.dd = parm.dd;
   parm1.course = parm.course;
   parm1.p5 = parm.p5;
   parm1.day = parm.day;
   parm1.oldPlayer1 = "";       // always empty from here
   parm1.oldPlayer2 = "";
   parm1.oldPlayer3 = "";
   parm1.oldPlayer4 = "";
   parm1.oldPlayer5 = "";
   parm1.fb = parm.fb;
   parm1.ind = parm.ind;      // index value
   parm1.sfb = parm.sfb;


   //
   //  Do all players, one group at a time
   //
   go = false;                             // init to 'No Go'

   if (parm.p5.equals( "Yes" )) {

      if (!parm.player1.equals( "" ) || !parm.player2.equals( "" ) || !parm.player3.equals( "" ) ||
          !parm.player4.equals( "" ) || !parm.player5.equals( "" )) {

         go = true;                // go process this group

         //
         //  set parms for first group
         //
         parm1.player1 = parm.player1;
         parm1.player2 = parm.player2;
         parm1.player3 = parm.player3;
         parm1.player4 = parm.player4;
         parm1.player5 = parm.player5;
         parm1.user1 = parm.user1;
         parm1.user2 = parm.user2;
         parm1.user3 = parm.user3;
         parm1.user4 = parm.user4;
         parm1.user5 = parm.user5;
         parm1.mship1 = parm.mship1;
         parm1.mship2 = parm.mship2;
         parm1.mship3 = parm.mship3;
         parm1.mship4 = parm.mship4;
         parm1.mship5 = parm.mship5;
         parm1.mtype1 = parm.mtype1;
         parm1.mtype2 = parm.mtype2;
         parm1.mtype3 = parm.mtype3;
         parm1.mtype4 = parm.mtype4;
         parm1.mtype5 = parm.mtype5;
         parm1.mNum1 = parm.mNum1;
         parm1.mNum2 = parm.mNum2;
         parm1.mNum3 = parm.mNum3;
         parm1.mNum4 = parm.mNum4;
         parm1.mNum5 = parm.mNum5;
      }

   } else {                       // 4-somes only

      if (!parm.player1.equals( "" ) || !parm.player2.equals( "" ) || !parm.player3.equals( "" ) ||
          !parm.player4.equals( "" )) {

         go = true;                // go process this group

         //
         //  set parms for first group
         //
         parm1.player1 = parm.player1;
         parm1.player2 = parm.player2;
         parm1.player3 = parm.player3;
         parm1.player4 = parm.player4;
         parm1.user1 = parm.user1;
         parm1.user2 = parm.user2;
         parm1.user3 = parm.user3;
         parm1.user4 = parm.user4;
         parm1.mship1 = parm.mship1;
         parm1.mship2 = parm.mship2;
         parm1.mship3 = parm.mship3;
         parm1.mship4 = parm.mship4;
         parm1.mtype1 = parm.mtype1;
         parm1.mtype2 = parm.mtype2;
         parm1.mtype3 = parm.mtype3;
         parm1.mtype4 = parm.mtype4;
         parm1.mNum1 = parm.mNum1;
         parm1.mNum2 = parm.mNum2;
         parm1.mNum3 = parm.mNum3;
         parm1.mNum4 = parm.mNum4;
         parm1.player5 = "";
         parm1.user5 = "";
         parm1.mship5 = "";
         parm1.mtype5 = "";
         parm1.mNum5 = "";
      }
   }

   if (go == true) {          // if players found

      error = verifySlot.checkCherryHills(parm1);    // process custom restrictions
   }

   if (error == false) {           // if we can keep going

      //
      //  Do 2nd group
      //
      go = false;                             // init to 'No Go'

      if (parm.p5.equals( "Yes" )) {

         if (!parm.player6.equals( "" ) || !parm.player7.equals( "" ) || !parm.player8.equals( "" ) ||
             !parm.player9.equals( "" ) || !parm.player10.equals( "" )) {

            go = true;                // go process this group

            //
            //  set parms for this group
            //
            parm1.player1 = parm.player6;
            parm1.player2 = parm.player7;
            parm1.player3 = parm.player8;
            parm1.player4 = parm.player9;
            parm1.player5 = parm.player10;
            parm1.user1 = parm.user6;
            parm1.user2 = parm.user7;
            parm1.user3 = parm.user8;
            parm1.user4 = parm.user9;
            parm1.user5 = parm.user10;
            parm1.mship1 = parm.mship6;
            parm1.mship2 = parm.mship7;
            parm1.mship3 = parm.mship8;
            parm1.mship4 = parm.mship9;
            parm1.mship5 = parm.mship10;
            parm1.mtype1 = parm.mtype6;
            parm1.mtype2 = parm.mtype7;
            parm1.mtype3 = parm.mtype8;
            parm1.mtype4 = parm.mtype9;
            parm1.mtype5 = parm.mtype10;
            parm1.mNum1 = parm.mNum6;
            parm1.mNum2 = parm.mNum7;
            parm1.mNum3 = parm.mNum8;
            parm1.mNum4 = parm.mNum9;
            parm1.mNum5 = parm.mNum10;
         }

      } else {                       // 4-somes only

         if (!parm.player5.equals( "" ) || !parm.player6.equals( "" ) || !parm.player7.equals( "" ) ||
             !parm.player8.equals( "" )) {

            go = true;                // go process this group

            //
            //  set parms for this group
            //
            parm1.player1 = parm.player5;
            parm1.player2 = parm.player6;
            parm1.player3 = parm.player7;
            parm1.player4 = parm.player8;
            parm1.user1 = parm.user5;
            parm1.user2 = parm.user6;
            parm1.user3 = parm.user7;
            parm1.user4 = parm.user8;
            parm1.mship1 = parm.mship5;
            parm1.mship2 = parm.mship6;
            parm1.mship3 = parm.mship7;
            parm1.mship4 = parm.mship8;
            parm1.mtype1 = parm.mtype5;
            parm1.mtype2 = parm.mtype6;
            parm1.mtype3 = parm.mtype7;
            parm1.mtype4 = parm.mtype8;
            parm1.mNum1 = parm.mNum5;
            parm1.mNum2 = parm.mNum6;
            parm1.mNum3 = parm.mNum7;
            parm1.mNum4 = parm.mNum8;
            parm1.player5 = "";
            parm1.user5 = "";
            parm1.mship5 = "";
            parm1.mtype5 = "";
            parm1.mNum5 = "";
         }
      }

      if (go == true) {          // if mships found

         error = verifySlot.checkCherryHills(parm1);    // process custom restrictions
      }

      if (error == false) {           // if we can keep going

         //
         //  Do 3rd group
         //
         go = false;                             // init to 'No Go'

         if (parm.p5.equals( "Yes" )) {

            if (!parm.player11.equals( "" ) || !parm.player12.equals( "" ) || !parm.player13.equals( "" ) ||
                !parm.player14.equals( "" ) || !parm.player15.equals( "" )) {

               go = true;                // go process this group

               //
               //  set parms for this group
               //
               parm1.player1 = parm.player11;
               parm1.player2 = parm.player12;
               parm1.player3 = parm.player13;
               parm1.player4 = parm.player14;
               parm1.player5 = parm.player15;
               parm1.user1 = parm.user11;
               parm1.user2 = parm.user12;
               parm1.user3 = parm.user13;
               parm1.user4 = parm.user14;
               parm1.user5 = parm.user15;
               parm1.mship1 = parm.mship11;
               parm1.mship2 = parm.mship12;
               parm1.mship3 = parm.mship13;
               parm1.mship4 = parm.mship14;
               parm1.mship5 = parm.mship15;
               parm1.mtype1 = parm.mtype11;
               parm1.mtype2 = parm.mtype12;
               parm1.mtype3 = parm.mtype13;
               parm1.mtype4 = parm.mtype14;
               parm1.mtype5 = parm.mtype15;
               parm1.mNum1 = parm.mNum11;
               parm1.mNum2 = parm.mNum12;
               parm1.mNum3 = parm.mNum13;
               parm1.mNum4 = parm.mNum14;
               parm1.mNum5 = parm.mNum15;
            }

         } else {                       // 4-somes only

            if (!parm.player9.equals( "" ) || !parm.player10.equals( "" ) || !parm.player11.equals( "" ) ||
                !parm.player12.equals( "" )) {

               go = true;                // go process this group

               //
               //  set parms for this group
               //
               parm1.player1 = parm.player9;
               parm1.player2 = parm.player10;
               parm1.player3 = parm.player11;
               parm1.player4 = parm.player12;
               parm1.user1 = parm.user9;
               parm1.user2 = parm.user10;
               parm1.user3 = parm.user11;
               parm1.user4 = parm.user12;
               parm1.mship1 = parm.mship9;
               parm1.mship2 = parm.mship10;
               parm1.mship3 = parm.mship11;
               parm1.mship4 = parm.mship12;
               parm1.mtype1 = parm.mtype9;
               parm1.mtype2 = parm.mtype10;
               parm1.mtype3 = parm.mtype11;
               parm1.mtype4 = parm.mtype12;
               parm1.mNum1 = parm.mNum9;
               parm1.mNum2 = parm.mNum10;
               parm1.mNum3 = parm.mNum11;
               parm1.mNum4 = parm.mNum12;
               parm1.player5 = "";
               parm1.user5 = "";
               parm1.mship5 = "";
               parm1.mtype5 = "";
               parm1.mNum5 = "";
            }
         }

         if (go == true) {          // if mships found

            error = verifySlot.checkCherryHills(parm1);    // process custom restrictions
         }

         if (error == false) {           // if we can keep going

            //
            //  Do 4th group
            //
            go = false;                             // init to 'No Go'

            if (parm.p5.equals( "Yes" )) {

               if (!parm.player16.equals( "" ) || !parm.player17.equals( "" ) || !parm.player18.equals( "" ) ||
                   !parm.player19.equals( "" ) || !parm.player20.equals( "" )) {

                  go = true;                // go process this group

                  //
                  //  set parms for this group
                  //
                  parm1.player1 = parm.player16;
                  parm1.player2 = parm.player17;
                  parm1.player3 = parm.player18;
                  parm1.player4 = parm.player19;
                  parm1.player5 = parm.player20;
                  parm1.user1 = parm.user16;
                  parm1.user2 = parm.user17;
                  parm1.user3 = parm.user18;
                  parm1.user4 = parm.user19;
                  parm1.user5 = parm.user20;
                  parm1.mship1 = parm.mship16;
                  parm1.mship2 = parm.mship17;
                  parm1.mship3 = parm.mship18;
                  parm1.mship4 = parm.mship19;
                  parm1.mship5 = parm.mship20;
                  parm1.mtype1 = parm.mtype16;
                  parm1.mtype2 = parm.mtype17;
                  parm1.mtype3 = parm.mtype18;
                  parm1.mtype4 = parm.mtype19;
                  parm1.mtype5 = parm.mtype20;
                  parm1.mNum1 = parm.mNum16;
                  parm1.mNum2 = parm.mNum17;
                  parm1.mNum3 = parm.mNum18;
                  parm1.mNum4 = parm.mNum19;
                  parm1.mNum5 = parm.mNum20;
               }

            } else {                       // 4-somes only

               if (!parm.player13.equals( "" ) || !parm.player14.equals( "" ) || !parm.player15.equals( "" ) ||
                   !parm.player16.equals( "" )) {

                  go = true;                // go process this group

                  //
                  //  set parms for this group
                  //
                  parm1.player1 = parm.player13;
                  parm1.player2 = parm.player14;
                  parm1.player3 = parm.player15;
                  parm1.player4 = parm.player16;
                  parm1.user1 = parm.user13;
                  parm1.user2 = parm.user14;
                  parm1.user3 = parm.user15;
                  parm1.user4 = parm.user16;
                  parm1.mship1 = parm.mship13;
                  parm1.mship2 = parm.mship14;
                  parm1.mship3 = parm.mship15;
                  parm1.mship4 = parm.mship16;
                  parm1.mtype1 = parm.mtype13;
                  parm1.mtype2 = parm.mtype14;
                  parm1.mtype3 = parm.mtype15;
                  parm1.mtype4 = parm.mtype16;
                  parm1.mNum1 = parm.mNum13;
                  parm1.mNum2 = parm.mNum14;
                  parm1.mNum3 = parm.mNum15;
                  parm1.mNum4 = parm.mNum16;
                  parm1.player5 = "";
                  parm1.user5 = "";
                  parm1.mship5 = "";
                  parm1.mtype5 = "";
                  parm1.mNum5 = "";
               }
            }

            if (go == true) {          // if mships found

               error = verifySlot.checkCherryHills(parm1);    // process custom restrictions

            }

            if (error == false) {           // if we can keep going

               //
               //  Do 5th group
               //
               go = false;                             // init to 'No Go'

               if (parm.p5.equals( "Yes" )) {

                  if (!parm.player21.equals( "" ) || !parm.player22.equals( "" ) || !parm.player23.equals( "" ) ||
                      !parm.player24.equals( "" ) || !parm.player25.equals( "" )) {

                     go = true;                // go process this group

                     //
                     //  set parms for this group
                     //
                     parm1.player1 = parm.player21;
                     parm1.player2 = parm.player22;
                     parm1.player3 = parm.player23;
                     parm1.player4 = parm.player24;
                     parm1.player5 = parm.player25;
                     parm1.user1 = parm.user21;
                     parm1.user2 = parm.user22;
                     parm1.user3 = parm.user23;
                     parm1.user4 = parm.user24;
                     parm1.user5 = parm.user25;
                     parm1.mship1 = parm.mship21;
                     parm1.mship2 = parm.mship22;
                     parm1.mship3 = parm.mship23;
                     parm1.mship4 = parm.mship24;
                     parm1.mship5 = parm.mship25;
                     parm1.mtype1 = parm.mtype21;
                     parm1.mtype2 = parm.mtype22;
                     parm1.mtype3 = parm.mtype23;
                     parm1.mtype4 = parm.mtype24;
                     parm1.mtype5 = parm.mtype25;
                     parm1.mNum1 = parm.mNum21;
                     parm1.mNum2 = parm.mNum22;
                     parm1.mNum3 = parm.mNum23;
                     parm1.mNum4 = parm.mNum24;
                     parm1.mNum5 = parm.mNum25;
                  }

               } else {                       // 4-somes only

                  if (!parm.player17.equals( "" ) || !parm.player18.equals( "" ) || !parm.player19.equals( "" ) ||
                      !parm.player20.equals( "" )) {

                     go = true;                // go process this group

                     //
                     //  set parms for this group
                     //
                     parm1.player1 = parm.player17;
                     parm1.player2 = parm.player18;
                     parm1.player3 = parm.player19;
                     parm1.player4 = parm.player20;
                     parm1.user1 = parm.user17;
                     parm1.user2 = parm.user18;
                     parm1.user3 = parm.user19;
                     parm1.user4 = parm.user20;
                     parm1.mship1 = parm.mship17;
                     parm1.mship2 = parm.mship18;
                     parm1.mship3 = parm.mship19;
                     parm1.mship4 = parm.mship20;
                     parm1.mtype1 = parm.mtype17;
                     parm1.mtype2 = parm.mtype18;
                     parm1.mtype3 = parm.mtype19;
                     parm1.mtype4 = parm.mtype20;
                     parm1.mNum1 = parm.mNum17;
                     parm1.mNum2 = parm.mNum18;
                     parm1.mNum3 = parm.mNum19;
                     parm1.mNum4 = parm.mNum20;
                     parm1.player5 = "";
                     parm1.user5 = "";
                     parm1.mship5 = "";
                     parm1.mtype5 = "";
                     parm1.mNum5 = "";
                  }
               }

               if (go == true) {          // if mships found

                  error = verifySlot.checkCherryHills(parm1);    // process custom restrictions

               }
            }
         }
      }
   }

   return(error);

 }         // end of checkCherryRes


 // *******************************************************************************
 //  Check Member Number restrictions
 //
 //     First, find all restrictions within date & time constraints
 //     Then, find the ones for this day
 //     Then, check all players' member numbers against all others in the time period
 //
 // *******************************************************************************
 //
 private boolean checkMemNum(Connection con, PrintWriter out, parmLott parm, String day) {


   ResultSet rs = null;

   boolean check = false;
   parm.error = false;               // init

   String rest_name = "";
   String rest_recurr = "";
   String rest_course = "";
   String rest_fb = "";
   String sfb = "";
   String course = parm.course;

   int rest_stime = 0;
   int rest_etime = 0;
   int mems = 0;
   int ind = 0;
   int time = parm.time;

   long date = parm.date;


   try {

      PreparedStatement pstmt7b = con.prepareStatement (
         "SELECT name, stime, etime, recurr, courseName, fb, num_mems " +
         "FROM mnumres2 " +
         "WHERE activity_id = 0 AND sdate <= ? AND edate >= ? AND " +
         "stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-')");


      pstmt7b.clearParameters();          // clear the parms
      pstmt7b.setLong(1, date);
      pstmt7b.setLong(2, date);
      pstmt7b.setInt(3, time);
      pstmt7b.setInt(4, time);
      pstmt7b.setString(5, course);

      rs = pstmt7b.executeQuery();      // find all matching restrictions, if any

      check = false;                    // init 'hit' flag
      ind = 0;                          // init matching member count

      if (parm.fb == 0) {                    // is Tee time for Front 9?

         sfb = "Front";
      }

      if (parm.fb == 1) {                    // is it Back 9?

         sfb = "Back";
      }

      loop3:
      while (rs.next()) {              // check all matching restrictions for this day & F/B

         rest_name = rs.getString("name");
         rest_stime = rs.getInt("stime");
         rest_etime = rs.getInt("etime");
         rest_recurr = rs.getString("recurr");
         rest_course = rs.getString("courseName");
         rest_fb = rs.getString("fb");
         mems = rs.getInt("num_mems");

         //
         //  We must check the recurrence for this day (Monday, etc.)
         //
         if ((rest_recurr.equals( "Every " + day )) ||               // if this day
             (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
             ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
               (!day.equalsIgnoreCase( "saturday" )) &&
               (!day.equalsIgnoreCase( "sunday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
              (day.equalsIgnoreCase( "saturday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
              (day.equalsIgnoreCase( "sunday" )))) {

            //
            //  Now check if F/B matches this tee time
            //
            if (rest_fb.equals( "Both" ) || rest_fb.equals( sfb )) {

               //
               //  Found a restriction that matches date, time, day, course & F/B - check each member player
               //
               //   Check Player 1
               //
               if (!parm.mNum1.equals( "" )) {           // if this player is a member and member number exists

                  ind = checkmNum(parm.mNum1, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum1 = parm.player1;  // save this player name for error msg

                  if (parm.mNum1.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }

               }  // end of member 1 restrictions if

               //
               //   Check Player 2
               //
               if ((check == false) && (!parm.mNum2.equals( "" ))) {   // if this player is a member

                  ind = checkmNum(parm.mNum2, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum2 = parm.player2;  // save this player name for error msg

                  if (parm.mNum2.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }

               }  // end of member 2 restrictions if

               //
               //   Check Player 3
               //
               if ((check == false) && (!parm.mNum3.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum3, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum3 = parm.player3;  // save this player name for error msg

                  if (parm.mNum3.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }

               }  // end of member 3 restrictions if

               //
               //   Check Player 4
               //
               if ((check == false) && (!parm.mNum4.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum4, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum4 = parm.player4;  // save this player name for error msg

                  if (parm.mNum4.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 4 restrictions if

               //
               //   Check Player 5
               //
               if ((check == false) && (!parm.mNum5.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum5, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum5 = parm.player5;  // save this player name for error msg

                  if (parm.mNum5.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 5 restrictions if

               //
               //   Check Player 6
               //
               if ((check == false) && (!parm.mNum6.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum6, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum6 = parm.player6;  // save this player name for error msg

                  if (parm.mNum6.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 6 restrictions if

               //
               //   Check player 7
               //
               if ((check == false) && (!parm.mNum7.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum7, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum7 = parm.player7;  // save this player name for error msg

                  if (parm.mNum7.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 7 restrictions if

               //
               //   Check Player 8
               //
               if ((check == false) && (!parm.mNum8.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum8, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum8 = parm.player8;  // save this player name for error msg

                  if (parm.mNum8.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 8 restrictions if

               //
               //   Check Player 9
               //
               if ((check == false) && (!parm.mNum9.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum9, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum9 = parm.player9;  // save this player name for error msg

                  if (parm.mNum9.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 9 restrictions if

               //
               //   Check Player 10
               //
               if ((check == false) && (!parm.mNum10.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum10, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum10 = parm.player10;  // save this player name for error msg

                  if (parm.mNum10.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 10 restrictions if

               //
               //   Check Player 11
               //
               if ((check == false) && (!parm.mNum11.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum11, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum11 = parm.player11;  // save this player name for error msg

                  if (parm.mNum11.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 11 restrictions if

               //
               //   Check Player 12
               //
               if ((check == false) && (!parm.mNum12.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum12, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum12 = parm.player12;  // save this player name for error msg

                  if (parm.mNum12.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 12 restrictions if

               //
               //   Check Player 13
               //
               if ((check == false) && (!parm.mNum13.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum13, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum13 = parm.player13;  // save this player name for error msg

                  if (parm.mNum13.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 13 restrictions if

               //
               //   Check Player 14
               //
               if ((check == false) && (!parm.mNum14.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum14, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum14 = parm.player14;  // save this player name for error msg

                  if (parm.mNum14.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 14 restrictions if

               //
               //   Check Player 15
               //
               if ((check == false) && (!parm.mNum15.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum15, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum15 = parm.player15;  // save this player name for error msg

                  if (parm.mNum15.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 15 restrictions if

               //
               //   Check Player 16
               //
               if ((check == false) && (!parm.mNum16.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum16, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum16 = parm.player16;  // save this player name for error msg

                  if (parm.mNum16.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 16 restrictions if

               //
               //   Check player 17
               //
               if ((check == false) && (!parm.mNum17.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum17, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum17 = parm.player17;  // save this player name for error msg

                  if (parm.mNum17.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 17 restrictions if

               //
               //   Check Player 18
               //
               if ((check == false) && (!parm.mNum18.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum18, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum18 = parm.player18;  // save this player name for error msg

                  if (parm.mNum18.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 18 restrictions if

               //
               //   Check Player 19
               //
               if ((check == false) && (!parm.mNum19.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum19, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum19 = parm.player19;  // save this player name for error msg

                  if (parm.mNum19.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 19 restrictions if

               //
               //   Check Player 20
               //
               if ((check == false) && (!parm.mNum20.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum20, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum20 = parm.player20;  // save this player name for error msg

                  if (parm.mNum20.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 20 restrictions if

               //
               //   Check Player 21
               //
               if ((check == false) && (!parm.mNum21.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum21, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum21 = parm.player21;  // save this player name for error msg

                  if (parm.mNum21.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 21 restrictions if

               //
               //   Check Player 22
               //
               if ((check == false) && (!parm.mNum22.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum22, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum22 = parm.player22;  // save this player name for error msg

                  if (parm.mNum22.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 22 restrictions if

               //
               //   Check Player 23
               //
               if ((check == false) && (!parm.mNum23.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum23, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum23 = parm.player23;  // save this player name for error msg

                  if (parm.mNum23.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 23 restrictions if

               //
               //   Check Player 24
               //
               if ((check == false) && (!parm.mNum24.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum24, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum24 = parm.player24;  // save this player name for error msg

                  if (parm.mNum24.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;    // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }       // end of member 24 restrictions if

               //
               //   Check Player 25
               //
               if ((check == false) && (!parm.mNum25.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum25, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum25 = parm.player25;  // save this player name for error msg

                  if (parm.mNum25.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 25 restrictions if

               if (check == true ) {          // if restriction hit

                  break loop3;
               }
            }     // end of IF F/B matches
         }     // end of 'day' if
      }       // end of while (no more restrictions)

      pstmt7b.close();

   }
   catch (Exception e7) {

      dbError(out, e7);
      parm.error = true;               // inform caller of error
   }

   //
   //  save parms if error
   //
   parm.rest_name = rest_name;

   return(check);
 }          // end of member restriction tests


 // *********************************************************
 //  Send email to members in this request
 // *********************************************************

 private void sendMail(Connection con, parmLott parms, int emailNew, int emailMod, String user) {


   //
   //  parm block to hold verify's parms
   //
   parmEmail parme = new parmEmail();          // allocate an Email parm block

   //
   //  Get the parms passed in the parm block and put them in the Email Parm Block
   //
   parme.date = parms.date;
   parme.time = parms.time;
   parme.fb = parms.fb;
   parme.mm = parms.mm;
   parme.dd = parms.dd;
   parme.yy = parms.yy;
     
   parme.type = "lottery";          // indicate from lottery
   parme.user = user;
   parme.emailNew = emailNew;
   parme.emailMod = emailMod;
   parme.emailCan = 0;

   parme.p91 = parms.p91;
   parme.p92 = parms.p92;
   parme.p93 = parms.p93;
   parme.p94 = parms.p94;
   parme.p95 = parms.p95;
   parme.p96 = parms.p96;
   parme.p97 = parms.p97;
   parme.p98 = parms.p98;
   parme.p99 = parms.p99;
   parme.p910 = parms.p910;
   parme.p911 = parms.p911;
   parme.p912 = parms.p912;
   parme.p913 = parms.p913;
   parme.p914 = parms.p914;
   parme.p915 = parms.p915;
   parme.p916 = parms.p916;
   parme.p917 = parms.p917;
   parme.p918 = parms.p918;
   parme.p919 = parms.p919;
   parme.p920 = parms.p920;
   parme.p921 = parms.p921;
   parme.p922 = parms.p922;
   parme.p923 = parms.p923;
   parme.p924 = parms.p924;
   parme.p925 = parms.p925;

   parme.course = parms.course;
   parme.day = parms.day;
   
   parme.player1 = parms.player1;
   parme.player2 = parms.player2;
   parme.player3 = parms.player3;
   parme.player4 = parms.player4;
   parme.player5 = parms.player5;
   parme.player6 = parms.player6;
   parme.player7 = parms.player7;
   parme.player8 = parms.player8;
   parme.player9 = parms.player9;
   parme.player10 = parms.player10;
   parme.player11 = parms.player11;
   parme.player12 = parms.player12;
   parme.player13 = parms.player13;
   parme.player14 = parms.player14;
   parme.player15 = parms.player15;
   parme.player16 = parms.player16;
   parme.player17 = parms.player17;
   parme.player18 = parms.player18;
   parme.player19 = parms.player19;
   parme.player20 = parms.player20;
   parme.player21 = parms.player21;
   parme.player22 = parms.player22;
   parme.player23 = parms.player23;
   parme.player24 = parms.player24;
   parme.player25 = parms.player25;

   parme.oldplayer1 = parms.oldplayer1;
   parme.oldplayer2 = parms.oldplayer2;
   parme.oldplayer3 = parms.oldplayer3;
   parme.oldplayer4 = parms.oldplayer4;
   parme.oldplayer5 = parms.oldplayer5;
   parme.oldplayer6 = parms.oldplayer6;
   parme.oldplayer7 = parms.oldplayer7;
   parme.oldplayer8 = parms.oldplayer8;
   parme.oldplayer9 = parms.oldplayer9;
   parme.oldplayer10 = parms.oldplayer10;
   parme.oldplayer11 = parms.oldplayer11;
   parme.oldplayer12 = parms.oldplayer12;
   parme.oldplayer13 = parms.oldplayer13;
   parme.oldplayer14 = parms.oldplayer14;
   parme.oldplayer15 = parms.oldplayer15;
   parme.oldplayer16 = parms.oldplayer16;
   parme.oldplayer17 = parms.oldplayer17;
   parme.oldplayer18 = parms.oldplayer18;
   parme.oldplayer19 = parms.oldplayer19;
   parme.oldplayer20 = parms.oldplayer20;
   parme.oldplayer21 = parms.oldplayer21;
   parme.oldplayer22 = parms.oldplayer22;
   parme.oldplayer23 = parms.oldplayer23;
   parme.oldplayer24 = parms.oldplayer24;
   parme.oldplayer25 = parms.oldplayer25;

   parme.user1 = parms.user1;
   parme.user2 = parms.user2;
   parme.user3 = parms.user3;
   parme.user4 = parms.user4;
   parme.user5 = parms.user5;
   parme.user6 = parms.user6;
   parme.user7 = parms.user7;
   parme.user8 = parms.user8;
   parme.user9 = parms.user9;
   parme.user10 = parms.user10;
   parme.user11 = parms.user11;
   parme.user12 = parms.user12;
   parme.user13 = parms.user13;
   parme.user14 = parms.user14;
   parme.user15 = parms.user15;
   parme.user16 = parms.user16;
   parme.user17 = parms.user17;
   parme.user18 = parms.user18;
   parme.user19 = parms.user19;
   parme.user20 = parms.user20;
   parme.user21 = parms.user21;
   parme.user22 = parms.user22;
   parme.user23 = parms.user23;
   parme.user24 = parms.user24;
   parme.user25 = parms.user25;

   parme.olduser1 = parms.olduser1;
   parme.olduser2 = parms.olduser2;
   parme.olduser3 = parms.olduser3;
   parme.olduser4 = parms.olduser4;
   parme.olduser5 = parms.olduser5;
   parme.olduser6 = parms.olduser6;
   parme.olduser7 = parms.olduser7;
   parme.olduser8 = parms.olduser8;
   parme.olduser9 = parms.olduser9;
   parme.olduser10 = parms.olduser10;
   parme.olduser11 = parms.olduser11;
   parme.olduser12 = parms.olduser12;
   parme.olduser13 = parms.olduser13;
   parme.olduser14 = parms.olduser14;
   parme.olduser15 = parms.olduser15;
   parme.olduser16 = parms.olduser16;
   parme.olduser17 = parms.olduser17;
   parme.olduser18 = parms.olduser18;
   parme.olduser19 = parms.olduser19;
   parme.olduser20 = parms.olduser20;
   parme.olduser21 = parms.olduser21;
   parme.olduser22 = parms.olduser22;
   parme.olduser23 = parms.olduser23;
   parme.olduser24 = parms.olduser24;
   parme.olduser25 = parms.olduser25;

   parme.pcw1 = parms.pcw1;
   parme.pcw2 = parms.pcw2;
   parme.pcw3 = parms.pcw3;
   parme.pcw4 = parms.pcw4;
   parme.pcw5 = parms.pcw5;
   parme.pcw6 = parms.pcw6;
   parme.pcw7 = parms.pcw7;
   parme.pcw8 = parms.pcw8;
   parme.pcw9 = parms.pcw9;
   parme.pcw10 = parms.pcw10;
   parme.pcw11 = parms.pcw11;
   parme.pcw12 = parms.pcw12;
   parme.pcw13 = parms.pcw13;
   parme.pcw14 = parms.pcw14;
   parme.pcw15 = parms.pcw15;
   parme.pcw16 = parms.pcw16;
   parme.pcw17 = parms.pcw17;
   parme.pcw18 = parms.pcw18;
   parme.pcw19 = parms.pcw19;
   parme.pcw20 = parms.pcw20;
   parme.pcw21 = parms.pcw21;
   parme.pcw22 = parms.pcw22;
   parme.pcw23 = parms.pcw23;
   parme.pcw24 = parms.pcw24;
   parme.pcw25 = parms.pcw25;

   parme.oldpcw1 = parms.oldpcw1;
   parme.oldpcw2 = parms.oldpcw2;
   parme.oldpcw3 = parms.oldpcw3;
   parme.oldpcw4 = parms.oldpcw4;
   parme.oldpcw5 = parms.oldpcw5;
   parme.oldpcw6 = parms.oldpcw6;
   parme.oldpcw7 = parms.oldpcw7;
   parme.oldpcw8 = parms.oldpcw8;
   parme.oldpcw9 = parms.oldpcw9;
   parme.oldpcw10 = parms.oldpcw10;
   parme.oldpcw11 = parms.oldpcw11;
   parme.oldpcw12 = parms.oldpcw12;
   parme.oldpcw13 = parms.oldpcw13;
   parme.oldpcw14 = parms.oldpcw14;
   parme.oldpcw15 = parms.oldpcw15;
   parme.oldpcw16 = parms.oldpcw16;
   parme.oldpcw17 = parms.oldpcw17;
   parme.oldpcw18 = parms.oldpcw18;
   parme.oldpcw19 = parms.oldpcw19;
   parme.oldpcw20 = parms.oldpcw20;
   parme.oldpcw21 = parms.oldpcw21;
   parme.oldpcw22 = parms.oldpcw22;
   parme.oldpcw23 = parms.oldpcw23;
   parme.oldpcw24 = parms.oldpcw24;
   parme.oldpcw25 = parms.oldpcw25;

   parme.guest_id1 = parms.guest_id1;
   parme.guest_id2 = parms.guest_id2;
   parme.guest_id3 = parms.guest_id3;
   parme.guest_id4 = parms.guest_id4;
   parme.guest_id5 = parms.guest_id5;
   parme.guest_id6 = parms.guest_id6;
   parme.guest_id7 = parms.guest_id7;
   parme.guest_id8 = parms.guest_id8;
   parme.guest_id9 = parms.guest_id9;
   parme.guest_id10 = parms.guest_id10;
   parme.guest_id11 = parms.guest_id11;
   parme.guest_id12 = parms.guest_id12;
   parme.guest_id13 = parms.guest_id13;
   parme.guest_id14 = parms.guest_id14;
   parme.guest_id15 = parms.guest_id15;
   parme.guest_id16 = parms.guest_id16;
   parme.guest_id17 = parms.guest_id17;
   parme.guest_id18 = parms.guest_id18;
   parme.guest_id19 = parms.guest_id19;
   parme.guest_id20 = parms.guest_id20;
   parme.guest_id21 = parms.guest_id21;
   parme.guest_id22 = parms.guest_id22;
   parme.guest_id23 = parms.guest_id23;
   parme.guest_id24 = parms.guest_id24;
   parme.guest_id25 = parms.guest_id25;

   parme.oldguest_id1 = parms.oldguest_id1;
   parme.oldguest_id2 = parms.oldguest_id2;
   parme.oldguest_id3 = parms.oldguest_id3;
   parme.oldguest_id4 = parms.oldguest_id4;
   parme.oldguest_id5 = parms.oldguest_id5;
   parme.oldguest_id6 = parms.oldguest_id6;
   parme.oldguest_id7 = parms.oldguest_id7;
   parme.oldguest_id8 = parms.oldguest_id8;
   parme.oldguest_id9 = parms.oldguest_id9;
   parme.oldguest_id10 = parms.oldguest_id10;
   parme.oldguest_id11 = parms.oldguest_id11;
   parme.oldguest_id12 = parms.oldguest_id12;
   parme.oldguest_id13 = parms.oldguest_id13;
   parme.oldguest_id14 = parms.oldguest_id14;
   parme.oldguest_id15 = parms.oldguest_id15;
   parme.oldguest_id16 = parms.oldguest_id16;
   parme.oldguest_id17 = parms.oldguest_id17;
   parme.oldguest_id18 = parms.oldguest_id18;
   parme.oldguest_id19 = parms.oldguest_id19;
   parme.oldguest_id20 = parms.oldguest_id20;
   parme.oldguest_id21 = parms.oldguest_id21;
   parme.oldguest_id22 = parms.oldguest_id22;
   parme.oldguest_id23 = parms.oldguest_id23;
   parme.oldguest_id24 = parms.oldguest_id24;
   parme.oldguest_id25 = parms.oldguest_id25;

   //
   //  Send the email
   //
   sendEmail.sendIt(parme, con);      // in common

 }    // end of sendMail method


 // *********************************************************
 // Check each member for # of rounds played in a period
 // *********************************************************

 private int checkRounds(Connection con, String mperiod, String user, long date, long dateStart, long dateEnd, int mm, int yy) {


   ResultSet rs = null;

   int count = 0;

   try {
      //
      // statements for week
      //
      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT dd FROM teecurr2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND date >= ? AND date <= ?");

      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT dd FROM teepast2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND date >= ? AND date <= ?");
      //
      // statements for month
      //
      PreparedStatement pstmt2m = con.prepareStatement (
         "SELECT dd FROM teecurr2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND mm = ? AND yy = ?");

      PreparedStatement pstmt3m = con.prepareStatement (
         "SELECT dd FROM teepast2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND mm = ? AND yy = ?");
      //
      // statements for year
      //
      PreparedStatement pstmt2y = con.prepareStatement (
         "SELECT dd FROM teecurr2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND yy = ?");

      PreparedStatement pstmt3y = con.prepareStatement (
         "SELECT dd FROM teepast2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND yy = ?");

      if (mperiod.equals( "Week" )) {       // if WEEK

         pstmt2.clearParameters();        // get count from teecurr
         pstmt2.setString(1, user);
         pstmt2.setString(2, user);
         pstmt2.setString(3, user);
         pstmt2.setString(4, user);
         pstmt2.setString(5, user);
         pstmt2.setLong(6, date);
         pstmt2.setLong(7, dateStart);
         pstmt2.setLong(8, dateEnd);
         rs = pstmt2.executeQuery();

         count = 0;

         while (rs.next()) {

            count++;                      // count number or tee times in this week
         }

         pstmt3.clearParameters();        // get count from teepast
         pstmt3.setString(1, user);
         pstmt3.setString(2, user);
         pstmt3.setString(3, user);
         pstmt3.setString(4, user);
         pstmt3.setString(5, user);
         pstmt3.setLong(6, date);
         pstmt3.setLong(7, dateStart);
         pstmt3.setLong(8, dateEnd);
         rs = pstmt3.executeQuery();

         while (rs.next()) {

            count++;                      // count number or tee times in this week
         }
      }       // end of IF mperiod = week

      if (mperiod.equals( "Month" )) {      // if MONTH

         pstmt2m.clearParameters();        // get count from teecurr
         pstmt2m.setString(1, user);
         pstmt2m.setString(2, user);
         pstmt2m.setString(3, user);
         pstmt2m.setString(4, user);
         pstmt2m.setString(5, user);
         pstmt2m.setLong(6, date);
         pstmt2m.setInt(7, mm);
         pstmt2m.setInt(8, yy);
         rs = pstmt2m.executeQuery();

         count = 0;

         while (rs.next()) {

            count++;                      // count number or tee times in this month
         }

         pstmt3m.clearParameters();        // get count from teepast
         pstmt3m.setString(1, user);
         pstmt3m.setString(2, user);
         pstmt3m.setString(3, user);
         pstmt3m.setString(4, user);
         pstmt3m.setString(5, user);
         pstmt3m.setLong(6, date);
         pstmt3m.setInt(7, mm);
         pstmt3m.setInt(8, yy);
         rs = pstmt3m.executeQuery();

         while (rs.next()) {

            count++;                         // count number or tee times in this month
         }
      }       // end of IF mperiod = Month

      if (mperiod.equals( "Year" )) {            // if Year

         pstmt2y.clearParameters();             // get count from teecurr
         pstmt2y.setString(1, user);
         pstmt2y.setString(2, user);
         pstmt2y.setString(3, user);
         pstmt2y.setString(4, user);
         pstmt2y.setString(5, user);
         pstmt2y.setLong(6, date);
         pstmt2y.setInt(7, mm);
         pstmt2y.setInt(8, yy);
         rs = pstmt2y.executeQuery();

         count = 0;

         while (rs.next()) {

            count++;                      // count number or tee times in this year
         }

         pstmt3y.clearParameters();        // get count from teepast
         pstmt3y.setString(1, user);
         pstmt3y.setString(2, user);
         pstmt3y.setString(3, user);
         pstmt3y.setString(4, user);
         pstmt3y.setString(5, user);
         pstmt3y.setLong(6, date);
         pstmt3y.setInt(7, mm);
         pstmt3y.setInt(8, yy);
         rs = pstmt3y.executeQuery();

         while (rs.next()) {

            count++;                      // count number or tee times in this year
         }
      }       // end of IF mperiod = Year

      pstmt2.close();
      pstmt3.close();
      pstmt2m.close();
      pstmt3m.close();
      pstmt2y.close();
      pstmt3y.close();

   }
   catch (Exception ignore) {
   }
   return count;
 }       // end of checkRounds


 // *********************************************************
 //  Return to _lott 
 // *********************************************************

 private void goReturn(PrintWriter out, parmLott parm) {

      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + parm.date + "\">");
      out.println("<input type=\"hidden\" name=\"stime\" value=\"" + parm.time + "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
      out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
      out.println("<input type=\"hidden\" name=\"player1\" value=\"" + parm.player1 + "\">");
      out.println("<input type=\"hidden\" name=\"player2\" value=\"" + parm.player2 + "\">");
      out.println("<input type=\"hidden\" name=\"player3\" value=\"" + parm.player3 + "\">");
      out.println("<input type=\"hidden\" name=\"player4\" value=\"" + parm.player4 + "\">");
      out.println("<input type=\"hidden\" name=\"player5\" value=\"" + parm.player5 + "\">");
      out.println("<input type=\"hidden\" name=\"player6\" value=\"" + parm.player6 + "\">");
      out.println("<input type=\"hidden\" name=\"player7\" value=\"" + parm.player7 + "\">");
      out.println("<input type=\"hidden\" name=\"player8\" value=\"" + parm.player8 + "\">");
      out.println("<input type=\"hidden\" name=\"player9\" value=\"" + parm.player9 + "\">");
      out.println("<input type=\"hidden\" name=\"player10\" value=\"" + parm.player10 + "\">");
      out.println("<input type=\"hidden\" name=\"player11\" value=\"" + parm.player11 + "\">");
      out.println("<input type=\"hidden\" name=\"player12\" value=\"" + parm.player12 + "\">");
      out.println("<input type=\"hidden\" name=\"player13\" value=\"" + parm.player13 + "\">");
      out.println("<input type=\"hidden\" name=\"player14\" value=\"" + parm.player14 + "\">");
      out.println("<input type=\"hidden\" name=\"player15\" value=\"" + parm.player15 + "\">");
      out.println("<input type=\"hidden\" name=\"player16\" value=\"" + parm.player16 + "\">");
      out.println("<input type=\"hidden\" name=\"player17\" value=\"" + parm.player17 + "\">");
      out.println("<input type=\"hidden\" name=\"player18\" value=\"" + parm.player18 + "\">");
      out.println("<input type=\"hidden\" name=\"player19\" value=\"" + parm.player19 + "\">");
      out.println("<input type=\"hidden\" name=\"player20\" value=\"" + parm.player20 + "\">");
      out.println("<input type=\"hidden\" name=\"player21\" value=\"" + parm.player21 + "\">");
      out.println("<input type=\"hidden\" name=\"player22\" value=\"" + parm.player22 + "\">");
      out.println("<input type=\"hidden\" name=\"player23\" value=\"" + parm.player23 + "\">");
      out.println("<input type=\"hidden\" name=\"player24\" value=\"" + parm.player24 + "\">");
      out.println("<input type=\"hidden\" name=\"player25\" value=\"" + parm.player25 + "\">");
      out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + parm.pcw1 + "\">");
      out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + parm.pcw2 + "\">");
      out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + parm.pcw3 + "\">");
      out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + parm.pcw4 + "\">");
      out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + parm.pcw5 + "\">");
      out.println("<input type=\"hidden\" name=\"p6cw\" value=\"" + parm.pcw6 + "\">");
      out.println("<input type=\"hidden\" name=\"p7cw\" value=\"" + parm.pcw7 + "\">");
      out.println("<input type=\"hidden\" name=\"p8cw\" value=\"" + parm.pcw8 + "\">");
      out.println("<input type=\"hidden\" name=\"p9cw\" value=\"" + parm.pcw9 + "\">");
      out.println("<input type=\"hidden\" name=\"p10cw\" value=\"" + parm.pcw10 + "\">");
      out.println("<input type=\"hidden\" name=\"p11cw\" value=\"" + parm.pcw11 + "\">");
      out.println("<input type=\"hidden\" name=\"p12cw\" value=\"" + parm.pcw12 + "\">");
      out.println("<input type=\"hidden\" name=\"p13cw\" value=\"" + parm.pcw13 + "\">");
      out.println("<input type=\"hidden\" name=\"p14cw\" value=\"" + parm.pcw14 + "\">");
      out.println("<input type=\"hidden\" name=\"p15cw\" value=\"" + parm.pcw15 + "\">");
      out.println("<input type=\"hidden\" name=\"p16cw\" value=\"" + parm.pcw16 + "\">");
      out.println("<input type=\"hidden\" name=\"p17cw\" value=\"" + parm.pcw17 + "\">");
      out.println("<input type=\"hidden\" name=\"p18cw\" value=\"" + parm.pcw18 + "\">");
      out.println("<input type=\"hidden\" name=\"p19cw\" value=\"" + parm.pcw19 + "\">");
      out.println("<input type=\"hidden\" name=\"p20cw\" value=\"" + parm.pcw20 + "\">");
      out.println("<input type=\"hidden\" name=\"p21cw\" value=\"" + parm.pcw21 + "\">");
      out.println("<input type=\"hidden\" name=\"p22cw\" value=\"" + parm.pcw22 + "\">");
      out.println("<input type=\"hidden\" name=\"p23cw\" value=\"" + parm.pcw23 + "\">");
      out.println("<input type=\"hidden\" name=\"p24cw\" value=\"" + parm.pcw24 + "\">");
      out.println("<input type=\"hidden\" name=\"p25cw\" value=\"" + parm.pcw25 + "\">");
      out.println("<input type=\"hidden\" name=\"p91\" value=\"" + parm.p91 + "\">");
      out.println("<input type=\"hidden\" name=\"p92\" value=\"" + parm.p92 + "\">");
      out.println("<input type=\"hidden\" name=\"p93\" value=\"" + parm.p93 + "\">");
      out.println("<input type=\"hidden\" name=\"p94\" value=\"" + parm.p94 + "\">");
      out.println("<input type=\"hidden\" name=\"p95\" value=\"" + parm.p95 + "\">");
      out.println("<input type=\"hidden\" name=\"p96\" value=\"" + parm.p96 + "\">");
      out.println("<input type=\"hidden\" name=\"p97\" value=\"" + parm.p97 + "\">");
      out.println("<input type=\"hidden\" name=\"p98\" value=\"" + parm.p98 + "\">");
      out.println("<input type=\"hidden\" name=\"p99\" value=\"" + parm.p99 + "\">");
      out.println("<input type=\"hidden\" name=\"p910\" value=\"" + parm.p910 + "\">");
      out.println("<input type=\"hidden\" name=\"p911\" value=\"" + parm.p911 + "\">");
      out.println("<input type=\"hidden\" name=\"p912\" value=\"" + parm.p912 + "\">");
      out.println("<input type=\"hidden\" name=\"p913\" value=\"" + parm.p913 + "\">");
      out.println("<input type=\"hidden\" name=\"p914\" value=\"" + parm.p914 + "\">");
      out.println("<input type=\"hidden\" name=\"p915\" value=\"" + parm.p915 + "\">");
      out.println("<input type=\"hidden\" name=\"p916\" value=\"" + parm.p916 + "\">");
      out.println("<input type=\"hidden\" name=\"p917\" value=\"" + parm.p917 + "\">");
      out.println("<input type=\"hidden\" name=\"p918\" value=\"" + parm.p918 + "\">");
      out.println("<input type=\"hidden\" name=\"p919\" value=\"" + parm.p919 + "\">");
      out.println("<input type=\"hidden\" name=\"p920\" value=\"" + parm.p920 + "\">");
      out.println("<input type=\"hidden\" name=\"p921\" value=\"" + parm.p921 + "\">");
      out.println("<input type=\"hidden\" name=\"p922\" value=\"" + parm.p922 + "\">");
      out.println("<input type=\"hidden\" name=\"p923\" value=\"" + parm.p923 + "\">");
      out.println("<input type=\"hidden\" name=\"p924\" value=\"" + parm.p924 + "\">");
      out.println("<input type=\"hidden\" name=\"p925\" value=\"" + parm.p925 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + parm.guest_id1 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + parm.guest_id2 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + parm.guest_id3 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + parm.guest_id4 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + parm.guest_id5 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id6\" value=\"" + parm.guest_id6 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id7\" value=\"" + parm.guest_id7 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id8\" value=\"" + parm.guest_id8 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id9\" value=\"" + parm.guest_id9 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id10\" value=\"" + parm.guest_id10 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id11\" value=\"" + parm.guest_id11 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id12\" value=\"" + parm.guest_id12 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id13\" value=\"" + parm.guest_id13 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id14\" value=\"" + parm.guest_id14 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id15\" value=\"" + parm.guest_id15 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id16\" value=\"" + parm.guest_id16 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id17\" value=\"" + parm.guest_id17 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id18\" value=\"" + parm.guest_id18 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id19\" value=\"" + parm.guest_id19 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id20\" value=\"" + parm.guest_id20 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id21\" value=\"" + parm.guest_id21 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id22\" value=\"" + parm.guest_id22 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id23\" value=\"" + parm.guest_id23 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id24\" value=\"" + parm.guest_id24 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id25\" value=\"" + parm.guest_id25 + "\">");
      out.println("<input type=\"hidden\" name=\"notes\" value=\"" + parm.notes + "\">");
      out.println("<input type=\"hidden\" name=\"hide\" value=\"" + parm.hides + "\">");
      out.println("<input type=\"hidden\" name=\"lname\" value=\"" + parm.lottName + "\">");
      out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + parm.lstate + "\">");
      out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + parm.lottid + "\">");
      out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");
      out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" + parm.mins_before + "\">");
      out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" + parm.mins_after + "\">");
      out.println("<input type=\"hidden\" name=\"checkothers\" value=\"" + parm.checkothers + "\">");
      out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
 }


 // *********************************************************
 //  Return to _lott
 // *********************************************************

 private void goReturn2(PrintWriter out, parmLott parm) {

      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + parm.date + "\">");
      out.println("<input type=\"hidden\" name=\"stime\" value=\"" + parm.time + "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
      out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
      out.println("<input type=\"hidden\" name=\"player1\" value=\"" + parm.player1 + "\">");
      out.println("<input type=\"hidden\" name=\"player2\" value=\"" + parm.player2 + "\">");
      out.println("<input type=\"hidden\" name=\"player3\" value=\"" + parm.player3 + "\">");
      out.println("<input type=\"hidden\" name=\"player4\" value=\"" + parm.player4 + "\">");
      out.println("<input type=\"hidden\" name=\"player5\" value=\"" + parm.player5 + "\">");
      out.println("<input type=\"hidden\" name=\"player6\" value=\"" + parm.player6 + "\">");
      out.println("<input type=\"hidden\" name=\"player7\" value=\"" + parm.player7 + "\">");
      out.println("<input type=\"hidden\" name=\"player8\" value=\"" + parm.player8 + "\">");
      out.println("<input type=\"hidden\" name=\"player9\" value=\"" + parm.player9 + "\">");
      out.println("<input type=\"hidden\" name=\"player10\" value=\"" + parm.player10 + "\">");
      out.println("<input type=\"hidden\" name=\"player11\" value=\"" + parm.player11 + "\">");
      out.println("<input type=\"hidden\" name=\"player12\" value=\"" + parm.player12 + "\">");
      out.println("<input type=\"hidden\" name=\"player13\" value=\"" + parm.player13 + "\">");
      out.println("<input type=\"hidden\" name=\"player14\" value=\"" + parm.player14 + "\">");
      out.println("<input type=\"hidden\" name=\"player15\" value=\"" + parm.player15 + "\">");
      out.println("<input type=\"hidden\" name=\"player16\" value=\"" + parm.player16 + "\">");
      out.println("<input type=\"hidden\" name=\"player17\" value=\"" + parm.player17 + "\">");
      out.println("<input type=\"hidden\" name=\"player18\" value=\"" + parm.player18 + "\">");
      out.println("<input type=\"hidden\" name=\"player19\" value=\"" + parm.player19 + "\">");
      out.println("<input type=\"hidden\" name=\"player20\" value=\"" + parm.player20 + "\">");
      out.println("<input type=\"hidden\" name=\"player21\" value=\"" + parm.player21 + "\">");
      out.println("<input type=\"hidden\" name=\"player22\" value=\"" + parm.player22 + "\">");
      out.println("<input type=\"hidden\" name=\"player23\" value=\"" + parm.player23 + "\">");
      out.println("<input type=\"hidden\" name=\"player24\" value=\"" + parm.player24 + "\">");
      out.println("<input type=\"hidden\" name=\"player25\" value=\"" + parm.player25 + "\">");
      out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + parm.pcw1 + "\">");
      out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + parm.pcw2 + "\">");
      out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + parm.pcw3 + "\">");
      out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + parm.pcw4 + "\">");
      out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + parm.pcw5 + "\">");
      out.println("<input type=\"hidden\" name=\"p6cw\" value=\"" + parm.pcw6 + "\">");
      out.println("<input type=\"hidden\" name=\"p7cw\" value=\"" + parm.pcw7 + "\">");
      out.println("<input type=\"hidden\" name=\"p8cw\" value=\"" + parm.pcw8 + "\">");
      out.println("<input type=\"hidden\" name=\"p9cw\" value=\"" + parm.pcw9 + "\">");
      out.println("<input type=\"hidden\" name=\"p10cw\" value=\"" + parm.pcw10 + "\">");
      out.println("<input type=\"hidden\" name=\"p11cw\" value=\"" + parm.pcw11 + "\">");
      out.println("<input type=\"hidden\" name=\"p12cw\" value=\"" + parm.pcw12 + "\">");
      out.println("<input type=\"hidden\" name=\"p13cw\" value=\"" + parm.pcw13 + "\">");
      out.println("<input type=\"hidden\" name=\"p14cw\" value=\"" + parm.pcw14 + "\">");
      out.println("<input type=\"hidden\" name=\"p15cw\" value=\"" + parm.pcw15 + "\">");
      out.println("<input type=\"hidden\" name=\"p16cw\" value=\"" + parm.pcw16 + "\">");
      out.println("<input type=\"hidden\" name=\"p17cw\" value=\"" + parm.pcw17 + "\">");
      out.println("<input type=\"hidden\" name=\"p18cw\" value=\"" + parm.pcw18 + "\">");
      out.println("<input type=\"hidden\" name=\"p19cw\" value=\"" + parm.pcw19 + "\">");
      out.println("<input type=\"hidden\" name=\"p20cw\" value=\"" + parm.pcw20 + "\">");
      out.println("<input type=\"hidden\" name=\"p21cw\" value=\"" + parm.pcw21 + "\">");
      out.println("<input type=\"hidden\" name=\"p22cw\" value=\"" + parm.pcw22 + "\">");
      out.println("<input type=\"hidden\" name=\"p23cw\" value=\"" + parm.pcw23 + "\">");
      out.println("<input type=\"hidden\" name=\"p24cw\" value=\"" + parm.pcw24 + "\">");
      out.println("<input type=\"hidden\" name=\"p25cw\" value=\"" + parm.pcw25 + "\">");
      out.println("<input type=\"hidden\" name=\"p91\" value=\"" + parm.p91 + "\">");
      out.println("<input type=\"hidden\" name=\"p92\" value=\"" + parm.p92 + "\">");
      out.println("<input type=\"hidden\" name=\"p93\" value=\"" + parm.p93 + "\">");
      out.println("<input type=\"hidden\" name=\"p94\" value=\"" + parm.p94 + "\">");
      out.println("<input type=\"hidden\" name=\"p95\" value=\"" + parm.p95 + "\">");
      out.println("<input type=\"hidden\" name=\"p96\" value=\"" + parm.p96 + "\">");
      out.println("<input type=\"hidden\" name=\"p97\" value=\"" + parm.p97 + "\">");
      out.println("<input type=\"hidden\" name=\"p98\" value=\"" + parm.p98 + "\">");
      out.println("<input type=\"hidden\" name=\"p99\" value=\"" + parm.p99 + "\">");
      out.println("<input type=\"hidden\" name=\"p910\" value=\"" + parm.p910 + "\">");
      out.println("<input type=\"hidden\" name=\"p911\" value=\"" + parm.p911 + "\">");
      out.println("<input type=\"hidden\" name=\"p912\" value=\"" + parm.p912 + "\">");
      out.println("<input type=\"hidden\" name=\"p913\" value=\"" + parm.p913 + "\">");
      out.println("<input type=\"hidden\" name=\"p914\" value=\"" + parm.p914 + "\">");
      out.println("<input type=\"hidden\" name=\"p915\" value=\"" + parm.p915 + "\">");
      out.println("<input type=\"hidden\" name=\"p916\" value=\"" + parm.p916 + "\">");
      out.println("<input type=\"hidden\" name=\"p917\" value=\"" + parm.p917 + "\">");
      out.println("<input type=\"hidden\" name=\"p918\" value=\"" + parm.p918 + "\">");
      out.println("<input type=\"hidden\" name=\"p919\" value=\"" + parm.p919 + "\">");
      out.println("<input type=\"hidden\" name=\"p920\" value=\"" + parm.p920 + "\">");
      out.println("<input type=\"hidden\" name=\"p921\" value=\"" + parm.p921 + "\">");
      out.println("<input type=\"hidden\" name=\"p922\" value=\"" + parm.p922 + "\">");
      out.println("<input type=\"hidden\" name=\"p923\" value=\"" + parm.p923 + "\">");
      out.println("<input type=\"hidden\" name=\"p924\" value=\"" + parm.p924 + "\">");
      out.println("<input type=\"hidden\" name=\"p925\" value=\"" + parm.p925 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + parm.guest_id1 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + parm.guest_id2 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + parm.guest_id3 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + parm.guest_id4 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + parm.guest_id5 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id6\" value=\"" + parm.guest_id6 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id7\" value=\"" + parm.guest_id7 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id8\" value=\"" + parm.guest_id8 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id9\" value=\"" + parm.guest_id9 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id10\" value=\"" + parm.guest_id10 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id11\" value=\"" + parm.guest_id11 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id12\" value=\"" + parm.guest_id12 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id13\" value=\"" + parm.guest_id13 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id14\" value=\"" + parm.guest_id14 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id15\" value=\"" + parm.guest_id15 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id16\" value=\"" + parm.guest_id16 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id17\" value=\"" + parm.guest_id17 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id18\" value=\"" + parm.guest_id18 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id19\" value=\"" + parm.guest_id19 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id20\" value=\"" + parm.guest_id20 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id21\" value=\"" + parm.guest_id21 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id22\" value=\"" + parm.guest_id22 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id23\" value=\"" + parm.guest_id23 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id24\" value=\"" + parm.guest_id24 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id25\" value=\"" + parm.guest_id25 + "\">");
      out.println("<input type=\"hidden\" name=\"notes\" value=\"" + parm.notes + "\">");
      out.println("<input type=\"hidden\" name=\"hide\" value=\"" + parm.hides + "\">");
      out.println("<input type=\"hidden\" name=\"lname\" value=\"" + parm.lottName + "\">");
      out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + parm.lstate + "\">");
      out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + parm.lottid + "\">");
      out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");
      out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" + parm.mins_before + "\">");
      out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" + parm.mins_after + "\">");
      out.println("<input type=\"hidden\" name=\"checkothers\" value=\"" + parm.checkothers + "\">");
      out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline;\">");
      out.println("</form></font>");
      //
      //  Return to process the players as they are
      //
      out.println("<font size=\"2\"><BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"skip8\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"player1\" value=\"" + parm.player1 + "\">");
      out.println("<input type=\"hidden\" name=\"player2\" value=\"" + parm.player2 + "\">");
      out.println("<input type=\"hidden\" name=\"player3\" value=\"" + parm.player3 + "\">");
      out.println("<input type=\"hidden\" name=\"player4\" value=\"" + parm.player4 + "\">");
      out.println("<input type=\"hidden\" name=\"player5\" value=\"" + parm.player5 + "\">");
      out.println("<input type=\"hidden\" name=\"player6\" value=\"" + parm.player6 + "\">");
      out.println("<input type=\"hidden\" name=\"player7\" value=\"" + parm.player7 + "\">");
      out.println("<input type=\"hidden\" name=\"player8\" value=\"" + parm.player8 + "\">");
      out.println("<input type=\"hidden\" name=\"player9\" value=\"" + parm.player9 + "\">");
      out.println("<input type=\"hidden\" name=\"player10\" value=\"" + parm.player10 + "\">");
      out.println("<input type=\"hidden\" name=\"player11\" value=\"" + parm.player11 + "\">");
      out.println("<input type=\"hidden\" name=\"player12\" value=\"" + parm.player12 + "\">");
      out.println("<input type=\"hidden\" name=\"player13\" value=\"" + parm.player13 + "\">");
      out.println("<input type=\"hidden\" name=\"player14\" value=\"" + parm.player14 + "\">");
      out.println("<input type=\"hidden\" name=\"player15\" value=\"" + parm.player15 + "\">");
      out.println("<input type=\"hidden\" name=\"player16\" value=\"" + parm.player16 + "\">");
      out.println("<input type=\"hidden\" name=\"player17\" value=\"" + parm.player17 + "\">");
      out.println("<input type=\"hidden\" name=\"player18\" value=\"" + parm.player18 + "\">");
      out.println("<input type=\"hidden\" name=\"player19\" value=\"" + parm.player19 + "\">");
      out.println("<input type=\"hidden\" name=\"player20\" value=\"" + parm.player20 + "\">");
      out.println("<input type=\"hidden\" name=\"player21\" value=\"" + parm.player21 + "\">");
      out.println("<input type=\"hidden\" name=\"player22\" value=\"" + parm.player22 + "\">");
      out.println("<input type=\"hidden\" name=\"player23\" value=\"" + parm.player23 + "\">");
      out.println("<input type=\"hidden\" name=\"player24\" value=\"" + parm.player24 + "\">");
      out.println("<input type=\"hidden\" name=\"player25\" value=\"" + parm.player25 + "\">");
      out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + parm.pcw1 + "\">");
      out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + parm.pcw2 + "\">");
      out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + parm.pcw3 + "\">");
      out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + parm.pcw4 + "\">");
      out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + parm.pcw5 + "\">");
      out.println("<input type=\"hidden\" name=\"p6cw\" value=\"" + parm.pcw6 + "\">");
      out.println("<input type=\"hidden\" name=\"p7cw\" value=\"" + parm.pcw7 + "\">");
      out.println("<input type=\"hidden\" name=\"p8cw\" value=\"" + parm.pcw8 + "\">");
      out.println("<input type=\"hidden\" name=\"p9cw\" value=\"" + parm.pcw9 + "\">");
      out.println("<input type=\"hidden\" name=\"p10cw\" value=\"" + parm.pcw10 + "\">");
      out.println("<input type=\"hidden\" name=\"p11cw\" value=\"" + parm.pcw11 + "\">");
      out.println("<input type=\"hidden\" name=\"p12cw\" value=\"" + parm.pcw12 + "\">");
      out.println("<input type=\"hidden\" name=\"p13cw\" value=\"" + parm.pcw13 + "\">");
      out.println("<input type=\"hidden\" name=\"p14cw\" value=\"" + parm.pcw14 + "\">");
      out.println("<input type=\"hidden\" name=\"p15cw\" value=\"" + parm.pcw15 + "\">");
      out.println("<input type=\"hidden\" name=\"p16cw\" value=\"" + parm.pcw16 + "\">");
      out.println("<input type=\"hidden\" name=\"p17cw\" value=\"" + parm.pcw17 + "\">");
      out.println("<input type=\"hidden\" name=\"p18cw\" value=\"" + parm.pcw18 + "\">");
      out.println("<input type=\"hidden\" name=\"p19cw\" value=\"" + parm.pcw19 + "\">");
      out.println("<input type=\"hidden\" name=\"p20cw\" value=\"" + parm.pcw20 + "\">");
      out.println("<input type=\"hidden\" name=\"p21cw\" value=\"" + parm.pcw21 + "\">");
      out.println("<input type=\"hidden\" name=\"p22cw\" value=\"" + parm.pcw22 + "\">");
      out.println("<input type=\"hidden\" name=\"p23cw\" value=\"" + parm.pcw23 + "\">");
      out.println("<input type=\"hidden\" name=\"p24cw\" value=\"" + parm.pcw24 + "\">");
      out.println("<input type=\"hidden\" name=\"p25cw\" value=\"" + parm.pcw25 + "\">");
      out.println("<input type=\"hidden\" name=\"p91\" value=\"" + parm.p91 + "\">");
      out.println("<input type=\"hidden\" name=\"p92\" value=\"" + parm.p92 + "\">");
      out.println("<input type=\"hidden\" name=\"p93\" value=\"" + parm.p93 + "\">");
      out.println("<input type=\"hidden\" name=\"p94\" value=\"" + parm.p94 + "\">");
      out.println("<input type=\"hidden\" name=\"p95\" value=\"" + parm.p95 + "\">");
      out.println("<input type=\"hidden\" name=\"p96\" value=\"" + parm.p96 + "\">");
      out.println("<input type=\"hidden\" name=\"p97\" value=\"" + parm.p97 + "\">");
      out.println("<input type=\"hidden\" name=\"p98\" value=\"" + parm.p98 + "\">");
      out.println("<input type=\"hidden\" name=\"p99\" value=\"" + parm.p99 + "\">");
      out.println("<input type=\"hidden\" name=\"p910\" value=\"" + parm.p910 + "\">");
      out.println("<input type=\"hidden\" name=\"p911\" value=\"" + parm.p911 + "\">");
      out.println("<input type=\"hidden\" name=\"p912\" value=\"" + parm.p912 + "\">");
      out.println("<input type=\"hidden\" name=\"p913\" value=\"" + parm.p913 + "\">");
      out.println("<input type=\"hidden\" name=\"p914\" value=\"" + parm.p914 + "\">");
      out.println("<input type=\"hidden\" name=\"p915\" value=\"" + parm.p915 + "\">");
      out.println("<input type=\"hidden\" name=\"p916\" value=\"" + parm.p916 + "\">");
      out.println("<input type=\"hidden\" name=\"p917\" value=\"" + parm.p917 + "\">");
      out.println("<input type=\"hidden\" name=\"p918\" value=\"" + parm.p918 + "\">");
      out.println("<input type=\"hidden\" name=\"p919\" value=\"" + parm.p919 + "\">");
      out.println("<input type=\"hidden\" name=\"p920\" value=\"" + parm.p920 + "\">");
      out.println("<input type=\"hidden\" name=\"p921\" value=\"" + parm.p921 + "\">");
      out.println("<input type=\"hidden\" name=\"p922\" value=\"" + parm.p922 + "\">");
      out.println("<input type=\"hidden\" name=\"p923\" value=\"" + parm.p923 + "\">");
      out.println("<input type=\"hidden\" name=\"p924\" value=\"" + parm.p924 + "\">");
      out.println("<input type=\"hidden\" name=\"p925\" value=\"" + parm.p925 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + parm.guest_id1 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + parm.guest_id2 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + parm.guest_id3 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + parm.guest_id4 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + parm.guest_id5 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id6\" value=\"" + parm.guest_id6 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id7\" value=\"" + parm.guest_id7 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id8\" value=\"" + parm.guest_id8 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id9\" value=\"" + parm.guest_id9 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id10\" value=\"" + parm.guest_id10 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id11\" value=\"" + parm.guest_id11 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id12\" value=\"" + parm.guest_id12 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id13\" value=\"" + parm.guest_id13 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id14\" value=\"" + parm.guest_id14 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id15\" value=\"" + parm.guest_id15 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id16\" value=\"" + parm.guest_id16 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id17\" value=\"" + parm.guest_id17 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id18\" value=\"" + parm.guest_id18 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id19\" value=\"" + parm.guest_id19 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id20\" value=\"" + parm.guest_id20 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id21\" value=\"" + parm.guest_id21 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id22\" value=\"" + parm.guest_id22 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id23\" value=\"" + parm.guest_id23 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id24\" value=\"" + parm.guest_id24 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id25\" value=\"" + parm.guest_id25 + "\">");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" + parm.date + "\">");
      out.println("<input type=\"hidden\" name=\"time\" value=\"" + parm.time + "\">");
      out.println("<input type=\"hidden\" name=\"mm\" value=\"" + parm.mm + "\">");
      out.println("<input type=\"hidden\" name=\"yy\" value=\"" + parm.yy + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
      out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
      out.println("<input type=\"hidden\" name=\"notes\" value=\"" + parm.notes + "\">");
      out.println("<input type=\"hidden\" name=\"hide\" value=\"" + parm.hides + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
      out.println("<input type=\"hidden\" name=\"lname\" value=\"" + parm.lottName + "\">");
      out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + parm.lstate + "\">");
      out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + parm.lottid + "\">");
      out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");
      out.println("<input type=\"hidden\" name=\"mins_before\" value=\"" + parm.mins_before + "\">");
      out.println("<input type=\"hidden\" name=\"mins_after\" value=\"" + parm.mins_after + "\">");
      out.println("<input type=\"hidden\" name=\"checkothers\" value=\"" + parm.checkothers + "\">");
      for (int i = 0; i < 25; i++) {
         out.println("<input type=\"hidden\" name=\"userg" + i + "\" value=\"" + parm.userg[i] + "\">");
      }
      out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submitForm\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
 }


 // *********************************************************
 // Check if player already scheduled
 // *********************************************************

 private int chkPlayer(Connection con, String player, long date, int time, int fb, String course, long id) {


   ResultSet rs = null;

   int hit = 0;
   int time2 = 0;
   int fb2 = 0;
   int count = 0;

   long id2 = 0;

   String course2 = "";

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // hard code a zero for the root activity since FlxRez doesn't support lotteries

   //
   //   Get the guest names specified for this club
   //
   try {
      getClub.getParms(con, parm);        // get the club parms

   }
   catch (Exception ignore) {
   }

   int max = parm.rnds;           // max allowed rounds per day for members (club option)
   int hrsbtwn = parm.hrsbtwn;    // minumum hours between tee times (club option when rnds > 1)


   try {

      PreparedStatement pstmt21 = con.prepareStatement (
         "SELECT time, fb, courseName FROM teecurr2 " +
         "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ?) AND date = ?");

      pstmt21.clearParameters();        // clear the parms and check player 1
      pstmt21.setString(1, player);
      pstmt21.setString(2, player);
      pstmt21.setString(3, player);
      pstmt21.setString(4, player);
      pstmt21.setString(5, player);
      pstmt21.setLong(6, date);
      rs = pstmt21.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         time2 = rs.getInt("time");
         fb2 = rs.getInt("fb");
         course2 = rs.getString("courseName");

         if ((time2 != time) || (fb2 != fb) || (!course2.equals( course ))) {      // if not this tee time

            count++;         // add to tee time counter for member

            //
            //  check if requested tee time is too close to this one
            //
            if (max > 1 && hrsbtwn > 0) {

               if (time2 < time) {            // if this tee time is before the time requested

                  if (time < (time2 + (hrsbtwn * 100))) {     // if this tee time is within range

                     hit = 2;                       // tee times not far enough apart
                  }

               } else {                                 // this time is after the requested time

                  if (time2 < (time + (hrsbtwn * 100))) {     // if this tee time is within range

                     hit = 2;                       // tee times not far enough apart
                  }
               }
            }
         }
      }
      pstmt21.close();

      //
      //  check if player already on a lottery request
      //
      PreparedStatement pstmt22 = con.prepareStatement (
         "SELECT time, id FROM lreqs3 " +
         "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ? OR " +
                "player6 = ? OR player7 = ? OR player8 = ? OR player9 = ? OR player10 = ? OR " +
                "player11 = ? OR player12 = ? OR player13 = ? OR player14 = ? OR player15 = ? OR " +
                "player16 = ? OR player17 = ? OR player18 = ? OR player19 = ? OR player20 = ? OR " +
                "player21 = ? OR player22 = ? OR player23 = ? OR player24 = ? OR player25 = ?) AND date = ?");

      pstmt22.clearParameters();        // clear the parms and check player 1
      pstmt22.setString(1, player);
      pstmt22.setString(2, player);
      pstmt22.setString(3, player);
      pstmt22.setString(4, player);
      pstmt22.setString(5, player);
      pstmt22.setString(6, player);
      pstmt22.setString(7, player);
      pstmt22.setString(8, player);
      pstmt22.setString(9, player);
      pstmt22.setString(10, player);
      pstmt22.setString(11, player);
      pstmt22.setString(12, player);
      pstmt22.setString(13, player);
      pstmt22.setString(14, player);
      pstmt22.setString(15, player);
      pstmt22.setString(16, player);
      pstmt22.setString(17, player);
      pstmt22.setString(18, player);
      pstmt22.setString(19, player);
      pstmt22.setString(20, player);
      pstmt22.setString(21, player);
      pstmt22.setString(22, player);
      pstmt22.setString(23, player);
      pstmt22.setString(24, player);
      pstmt22.setString(25, player);
      pstmt22.setLong(26, date);
      rs = pstmt22.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         time2 = rs.getInt("time");
         id2 = rs.getLong("id");

         if (id2 != id) {              // if not this req

            count++;         // add to tee time counter for member

            //
            //  check if requested tee time is too close to this one
            //
            if (max > 1 && hrsbtwn > 0) {

               if (time2 < time) {            // if this tee time is before the time requested

                  if (time < (time2 + (hrsbtwn * 100))) {     // if this tee time is within range

                     hit = 3;                       // tee times not far enough apart
                  }

               } else {                                 // this time is after the requested time

                  if (time2 < (time + (hrsbtwn * 100))) {     // if this tee time is within range

                     hit = 3;                       // tee times not far enough apart
                  }
               }
            }
         }
      }
      pstmt22.close();
        
      //
      //  See if we exceeded max allowed for day - if so, set indicator
      //
      if (count >= max) {

         hit = 1;                       // player already scheduled on this date (max times allowed)
      }
  
   }
   catch (Exception ignore) {
   }

   return hit;
 }


 // *********************************************************
 // Check Member Number Restrictions
 // *********************************************************

 private int checkmNum(String mNum, long date, int rest_etime, int rest_stime, int time, String course, String rest_fb, String rest_course, Connection con) {


   ResultSet rs7 = null;

   int ind = 0;
   int time2 = 0;
   int t_fb = 0;

   String course2 = "";
   String sfb2 = "";
   String rmNum1 = "";
   String rmNum2 = "";
   String rmNum3 = "";
   String rmNum4 = "";
   String rmNum5 = "";

   try {

      PreparedStatement pstmt7c = con.prepareStatement (
         "SELECT time, fb, courseName, mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 " +
         "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND date = ? " +
         "AND time <= ? AND time >= ?");

      pstmt7c.clearParameters();        // clear the parms and check player 1
      pstmt7c.setString(1, mNum);
      pstmt7c.setString(2, mNum);
      pstmt7c.setString(3, mNum);
      pstmt7c.setString(4, mNum);
      pstmt7c.setString(5, mNum);
      pstmt7c.setLong(6, date);
      pstmt7c.setInt(7, rest_etime);
      pstmt7c.setInt(8, rest_stime);
      rs7 = pstmt7c.executeQuery();      // execute the prepared stmt

      while (rs7.next()) {

         time2 = rs7.getInt("time");
         t_fb = rs7.getInt("fb");
         course2 = rs7.getString("courseName");
         rmNum1 = rs7.getString("mNum1");
         rmNum2 = rs7.getString("mNum2");
         rmNum3 = rs7.getString("mNum3");
         rmNum4 = rs7.getString("mNum4");
         rmNum5 = rs7.getString("mNum5");

         //
         //  matching member number found in teecurr - check if course and f/b match
         //
         if (t_fb == 0) {                   // is Tee time for Front 9?

            sfb2 = "Front";
         }

         if (t_fb == 1) {                   // is it Back 9?

            sfb2 = "Back";
         }

         //
         //  First make sure this is not this tee time before changes,
         //  Then check if it matches the criteria for the restriction.
         //
         if ((time2 != time) || (!course2.equals( course ))) {  // either time or course is diff

            if ((rest_fb.equals( "Both" ) || rest_fb.equals( sfb2 )) &&
                (rest_course.equals( "-ALL-" ) || rest_course.equals( course2 ))) {

               if (mNum.equals( rmNum1 )) {
                  ind++;
               }
               if (mNum.equals( rmNum2 )) {
                  ind++;
               }
               if (mNum.equals( rmNum3 )) {
                  ind++;
               }
               if (mNum.equals( rmNum4 )) {
                  ind++;
               }
               if (mNum.equals( rmNum5 )) {
                  ind++;
               }
            }
         }

      } // end of while members

      pstmt7c.close();

   }
   catch (Exception ignore) {
   }

   return (ind);
 }


 // *********************************************************
 //  Process cancel request from Member_lott (HTML)
 // *********************************************************

 private void cancel(int mobile, HttpServletRequest req, PrintWriter out, Connection con) {


   long lottid = 0;
   String displayOpt = "";

   //
   // Get all the parameters entered
   //
   String index = req.getParameter("index");          //  index value of day (needed by Member_sheet when returning)
   String course = req.getParameter("course");        //  name of course
   String sid = req.getParameter("lottid");           //  lottery id
   String returnCourse = req.getParameter("returnCourse");        //  name of course to return to


   //
   //  Get the Display Option if specified (Mobile Devices)
   //
   if (req.getParameter("displayOpt") != null) {

      displayOpt = req.getParameter("displayOpt");
   }

   //
   //  Convert the values from string to int
   //
   try {
      lottid = Long.parseLong(sid);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  Clear the 'in_use' flag for this request
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "UPDATE lreqs3 SET in_use = 0 WHERE id = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, lottid);         // put the parm in pstmt1
      pstmt1.executeUpdate();

      pstmt1.close();

   }
   catch (Exception ignore) {
   }

   //
   //  Prompt user to return to Member_sheet or Member_teelist (index = 888)
   //
   if (mobile == 0) {
      
      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
      out.println("<title>Member Tee Time Request Page</title>");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
      out.println("<BR><BR>Thank you, the request has been returned to the system without changes.");
      out.println("<BR><BR>");

      if (index.equals( "999" )) {         // if came from Member_teelist

         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain.htm\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</form></font>");

      } else {

         if (index.equals( "995" )) {         // if came from Member_teelist_list

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

               if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
                  course = returnCourse;
               }

               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
               out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("</form></font>");
            }
         }
      }
      out.println("</CENTER></BODY></HTML>");
      
   } else {
      
      //
      //  Mobile user
      //
      out.println(SystemUtils.HeadTitleMobile("Foretees Request List"));
      out.println(SystemUtils.BannerMobile());

      out.println("<div class=\"content\">");
      out.println("<div class=\"headertext\">");    // output the heading
         out.println("Cancel Requested");
      out.println("</div>");
      
      out.println("<div class=\"smheadertext\">The request has been returned to the system without changes</div>");     

      out.println("<ul>");
      
      if (index.equals( "995" )) {         // if came from Member_teelist_list

         out.println("<li>");
         out.println("<form action=\"/" +rev+ "/servlet/Member_teelist_mobile\" method=\"get\">");
         out.println("<input type=\"submit\" value=\"Return to List\"></form>");
         out.println("</li>");

      } else {

         out.println("<li>");
         out.println("<form action=\"/" +rev+ "/servlet/Member_sheet\" method=\"post\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
         out.println("<input type=\"submit\" value=\"Return To Tee Sheet\"></form>");
         out.println("</li>");
      }
  
      out.println("</ul></div>");
      out.println("</body></html>");
   }
   
   out.close();
 }


 // *********************************************************
 //  Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

 }


 // *********************************************************
 // Invalid data received - reject request
 // *********************************************************

 private void invData(PrintWriter out, String player) {

   out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
   out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H3>Invalid Data Received</H3><BR>");
   out.println("<BR><BR>Sorry, a name you entered (<b>" + player + "</b>) is not valid.<BR>");
   out.println("Please check the names and try again.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
   return;
 }


 // *********************************************************
 // Invalid data received - reject request
 // *********************************************************

 private void dupData(PrintWriter out, String player) {

   out.println(SystemUtils.HeadTitle("Data Entry Error"));
   out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");
   out.println("<BR><BR><H3>Data Entry Error</H3>");
   out.println("<BR><BR><b>" + player + "</b> was specified more than once.");
   out.println("<BR><BR>Please correct this and try again.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
   return;
 }
 
 
 // *********************************************************
 // checkTmodes - Checks if Pro-Only tmodes are being used without permission
 // returns false if errors found
 // *********************************************************
 private boolean checkTmodes(Connection con, PrintWriter out, parmLott parm) {


   parm.error = false;               // init
   boolean check = false;

   String player = "";
   String rest_name = "";

   
   //
   //  Allocate a new parm block for each tee time and call common meehtod to process each.
   //
   parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time
   
   //  Allocate a new parm block for the course parameters
   parmCourse parmc = new parmCourse();
   
   //
   //  Setup the new single parm block
   //
   parm1.date = parm.date;
   parm1.mm = parm.mm;
   parm1.yy = parm.yy;
   parm1.dd = parm.dd;
   parm1.course = parm.course;
   parm1.p5 = parm.p5;
   parm1.day = parm.day;
   parm1.fb = parm.fb;
   parm1.ind = parm.ind;      // index value
   parm1.sfb = parm.sfb;
   parm1.club = parm.club;    // name of club

   //  Get the walk/cart options available  //
   try {
      getParms.getTmodes(con, parmc, parm.course);
   }
   catch (Exception e1) {
      dbError(out, e1);
   }

   //
   //  Check in groups of 5
   //
   if (parmc.hasProOnlyTmodes) {
       try {
          // if first group of 5 contains any players, check their tmodes
          if (!parm.player1.equals("") || !parm.player2.equals("") || !parm.player3.equals("") || !parm.player4.equals("") || !parm.player5.equals("")) {

              parm1.player1 = parm.player1;
              parm1.player2 = parm.player2;
              parm1.player3 = parm.player3;
              parm1.player4 = parm.player4;
              parm1.player5 = parm.player5;
              parm1.oldPlayer1 = parm.oldplayer1;
              parm1.oldPlayer2 = parm.oldplayer2;
              parm1.oldPlayer3 = parm.oldplayer3;
              parm1.oldPlayer4 = parm.oldplayer4;
              parm1.oldPlayer5 = parm.oldplayer5;
              parm1.user1 = parm.user1;
              parm1.user2 = parm.user2;
              parm1.user3 = parm.user3;
              parm1.user4 = parm.user4;
              parm1.user5 = parm.user5;
              parm1.p1cw = parm.pcw1;
              parm1.p2cw = parm.pcw2;
              parm1.p3cw = parm.pcw3;
              parm1.p4cw = parm.pcw4;
              parm1.p5cw = parm.pcw5;
              parm1.oldp1cw = parm.oldpcw1;
              parm1.oldp2cw = parm.oldpcw2;
              parm1.oldp3cw = parm.oldpcw3;
              parm1.oldp4cw = parm.oldpcw4;
              parm1.oldp5cw = parm.oldpcw5;

              if (!verifySlot.checkProOnlyMOT(parm1, parmc, con)) {                   // check for Pro-Only tmodes

                  parm.player = parm1.player;
                  return false;
              }
          }

          // check 2nd group
          if (!parm.player6.equals("") || !parm.player7.equals("") || !parm.player8.equals("") || !parm.player9.equals("") || !parm.player10.equals("")) {

             parm1.player1 = parm.player6;
             parm1.player2 = parm.player7;
             parm1.player3 = parm.player8;
             parm1.player4 = parm.player9;
             parm1.player5 = parm.player10;
             parm1.oldPlayer1 = parm.oldplayer6;
             parm1.oldPlayer2 = parm.oldplayer7;
             parm1.oldPlayer3 = parm.oldplayer8;
             parm1.oldPlayer4 = parm.oldplayer9;
             parm1.oldPlayer5 = parm.oldplayer10;
             parm1.user1 = parm.user6;
             parm1.user2 = parm.user7;
             parm1.user3 = parm.user8;
             parm1.user4 = parm.user9;
             parm1.user5 = parm.user10;
             parm1.p1cw = parm.pcw6;
             parm1.p2cw = parm.pcw7;
             parm1.p3cw = parm.pcw8;
             parm1.p4cw = parm.pcw9;
             parm1.p5cw = parm.pcw10;
             parm1.oldp1cw = parm.oldpcw6;
             parm1.oldp2cw = parm.oldpcw7;
             parm1.oldp3cw = parm.oldpcw8;
             parm1.oldp4cw = parm.oldpcw9;
             parm1.oldp5cw = parm.oldpcw10;

             if (!verifySlot.checkProOnlyMOT(parm1, parmc, con)) {                   // check for Pro-Only tmodes

                 parm.player = parm1.player;
                 return false;

             } 
          }

          // check 3rd group
          if (!parm.player11.equals("") || !parm.player12.equals("") || !parm.player13.equals("") || !parm.player14.equals("") || !parm.player15.equals("")) {

            parm1.player1 = parm.player11;
            parm1.player2 = parm.player12;
            parm1.player3 = parm.player13;
            parm1.player4 = parm.player14;
            parm1.player5 = parm.player15;
            parm1.oldPlayer1 = parm.oldplayer11;
            parm1.oldPlayer2 = parm.oldplayer12;
            parm1.oldPlayer3 = parm.oldplayer13;
            parm1.oldPlayer4 = parm.oldplayer14;
            parm1.oldPlayer5 = parm.oldplayer15;
            parm1.user1 = parm.user11;
            parm1.user2 = parm.user12;
            parm1.user3 = parm.user13;
            parm1.user4 = parm.user14;
            parm1.user5 = parm.user15;
            parm1.p1cw = parm.pcw11;
            parm1.p2cw = parm.pcw12;
            parm1.p3cw = parm.pcw13;
            parm1.p4cw = parm.pcw14;
            parm1.p5cw = parm.pcw15;
            parm1.oldp1cw = parm.oldpcw11;
            parm1.oldp2cw = parm.oldpcw12;
            parm1.oldp3cw = parm.oldpcw13;
            parm1.oldp4cw = parm.oldpcw14;
            parm1.oldp5cw = parm.oldpcw15;

            if (!verifySlot.checkProOnlyMOT(parm1, parmc, con)) {                   // check for Pro-Only tmodes

                parm.player = parm1.player;
                return false;
            }
          }

          // check 4th group
          if (!parm.player16.equals("") || !parm.player17.equals("") || !parm.player18.equals("") || !parm.player19.equals("") || !parm.player20.equals("")) {

            parm1.player1 = parm.player16;
            parm1.player2 = parm.player17;
            parm1.player3 = parm.player18;
            parm1.player4 = parm.player19;
            parm1.player5 = parm.player20;
            parm1.oldPlayer1 = parm.oldplayer16;
            parm1.oldPlayer2 = parm.oldplayer17;
            parm1.oldPlayer3 = parm.oldplayer18;
            parm1.oldPlayer4 = parm.oldplayer19;
            parm1.oldPlayer5 = parm.oldplayer20;
            parm1.user1 = parm.user16;
            parm1.user2 = parm.user17;
            parm1.user3 = parm.user18;
            parm1.user4 = parm.user19;
            parm1.user5 = parm.user20;
            parm1.p1cw = parm.pcw16;
            parm1.p2cw = parm.pcw17;
            parm1.p3cw = parm.pcw18;
            parm1.p4cw = parm.pcw19;
            parm1.p5cw = parm.pcw20;
            parm1.oldp1cw = parm.oldpcw16;
            parm1.oldp2cw = parm.oldpcw17;
            parm1.oldp3cw = parm.oldpcw18;
            parm1.oldp4cw = parm.oldpcw19;
            parm1.oldp5cw = parm.oldpcw20;

            if (!verifySlot.checkProOnlyMOT(parm1, parmc, con)) {                   // check for Pro-Only tmodes

               parm.player = parm1.player;
               return false;
            } 
          }

          // check 5th group
          if (!parm.player21.equals("") || !parm.player22.equals("") || !parm.player23.equals("") || !parm.player24.equals("") || !parm.player25.equals("")) {

              parm1.player1 = parm.player21;
              parm1.player2 = parm.player22;
              parm1.player3 = parm.player23;
              parm1.player4 = parm.player24;
              parm1.player5 = parm.player25;
              parm1.oldPlayer1 = parm.oldplayer21;
              parm1.oldPlayer2 = parm.oldplayer22;
              parm1.oldPlayer3 = parm.oldplayer23;
              parm1.oldPlayer4 = parm.oldplayer24;
              parm1.oldPlayer5 = parm.oldplayer25;
              parm1.user1 = parm.user21;
              parm1.user2 = parm.user22;
              parm1.user3 = parm.user23;
              parm1.user4 = parm.user24;
              parm1.user5 = parm.user25;
              parm1.p1cw = parm.pcw21;
              parm1.p2cw = parm.pcw22;
              parm1.p3cw = parm.pcw23;
              parm1.p4cw = parm.pcw24;
              parm1.p5cw = parm.pcw25;
              parm1.oldp1cw = parm.oldpcw21;
              parm1.oldp2cw = parm.oldpcw22;
              parm1.oldp3cw = parm.oldpcw23;
              parm1.oldp4cw = parm.oldpcw24;
              parm1.oldp5cw = parm.oldpcw25;

              if (!verifySlot.checkProOnlyMOT(parm1, parmc, con)) {                   // check for Pro-Only tmodes

                  parm.player = parm1.player;
                  return false;
              }
          }
       }
       catch (Exception e7) {

          dbError(out, e7);
          parm.error = true;               // inform caller of error
       }
   }

   return true;

 }         // end of checkTmodes
 
 
 
 // ***************************************************************************************
 //   Output the specified Player row in the Lottery Request table
 // ***************************************************************************************

 private void outPlayerRow(int row, String player, String pcw, int p9, parmCourse parmc, boolean blockP, PrintWriter out) {

    
    int i = 0;
               
    if (blockP == false) {            // if ok to change the player
       
       out.println("<br>&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player" +row+ "', 'p" +row+ "cw')\" style=\"cursor:hand\">");
       if (row < 10) {
          out.println("&nbsp;" +row + ": &nbsp;&nbsp;<input type=\"text\" name=\"player" +row+ "\" value=\"" + player + "\" size=\"20\" maxlength=\"30\">");
       } else {
          out.println(row + ": &nbsp;<input type=\"text\" name=\"player" +row+ "\" value=\"" + player + "\" size=\"20\" maxlength=\"30\">");
       }
       
       out.println("&nbsp;&nbsp;<select size=\"1\" name=\"p" +row+ "cw\">");
       out.println("<option selected value=" + pcw + ">" + pcw + "</option>");

       for (i=0; i<parmc.tmode_limit; i++) {

           if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( pcw ) && parmc.tOpt[i] == 0) {
              out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
           }
       }
       out.println("</select>");
       
       if (p9 == 1) {
          out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p9" +row+ "\" value=\"1\">");
       } else {
          out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p9" +row+ "\" value=\"1\">");
       }

    } else {      // do not allow user to erase or change this player
       
       out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
       if (row < 10) {
          out.println("&nbsp;" +row + ": &nbsp;&nbsp;<input disabled type=\"text\" name=\"player" +row+ "\" value=\"" + player + "\" size=\"20\" maxlength=\"30\">");       
       } else {
          out.println(row + ": &nbsp;<input disabled type=\"text\" name=\"player" +row+ "\" value=\"" + player + "\" size=\"20\" maxlength=\"30\">");       
       }
       out.println("<input type=\"hidden\" name=\"player" +row+ "\" value=\"" +player+ "\">");
       
       out.println("&nbsp;&nbsp;<select disabled size=\"1\" name=\"p" +row+ "cw\">");
       out.println("<option selected value=" + pcw + ">" + pcw + "</option>");
       
       if (pcw.equals("")) {
           out.println("<option style=\"width:25px; height:15px\" value=\"\"></option>");
       } else {
           out.println("<option selected value=" + pcw + ">" + pcw + "</option>");
       }
       out.println("</select>");
       out.println("<input type=\"hidden\" name=\"p" +row+ "cw\" value=\"" + pcw + "\">");      
       
       if (p9 == 1) {
          out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p9" +row+ "\" value=\"1\">");
       } else {
          out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p9" +row+ "\" value=\"1\">");
       }                 
    }          

 }         // end of outPlayerRow
 
 
 
 // ***************************************************************************************
 //   Display Error Msg based on user - used when we do not need to return to _lott page
 // ***************************************************************************************

 private void displayError(String title, String content, String exception, String url, int mobile, PrintWriter out) {

    if (mobile == 0) {       // if NOT mobile user
            
      out.println(SystemUtils.HeadTitle("Member Lottery Error"));
      out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");
      out.println("<BR><BR><H3>" +title+ "</H3>");
      out.println("<BR>" + content );
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");

      if (!exception.equals( "" )) {
         out.println("Exception: " +exception+ "<BR><BR>");
      }
      
      if (!url.equals( "" ) && !url.equals( "back" )) {
         
         out.println("<a href=\"/" + rev + "/" + url + "\">Return</a>&nbsp;&nbsp;&nbsp;&nbsp;");
         
      } else if (url.equals( "back" )) {
         
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</form>");
      }
      
      out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Home</a>");
      out.println("</font></CENTER></BODY></HTML>");
      out.close();
      
    } else {
       
       //  Mobile user
       
       if (url.equals( "back" )) url = "";
      
       SystemUtils.displayMobileError(content, url, out);    // ouput error message
       out.close();
    }
    
 }
 
 
 
 
 // ***************************************************************************************
 //   Display Error Msg based on user - used when we do not need to return to _lott page
 // ***************************************************************************************

 private void buildError(String title, String content, int mobile, PrintWriter out) {

    if (mobile == 0) {       // if NOT mobile user
            
      out.println(SystemUtils.HeadTitle("Member Request Error"));
      out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");
      out.println("<BR><BR><H3>" +title+ "</H3>");
      out.println("<BR>" + content );
      out.println("<BR><BR>");
      
    } else {
       
       //  Mobile user
       
       out.println(SystemUtils.HeadTitleMobile("Member Request Error"));
       out.println("<div class=\"headertext\">" +title+ "</div>");
       out.println("<div class=\"smheadertext\">" +content+ "</div>");     
       out.println("<div class=\"content\">&nbsp;</div>"); 
    }
    
 }
 
 
       
 // ***************************************************************************************
 //   Display Cancel Confirmation Msg based on user 
 // ***************************************************************************************

 private void displayCancelConf(int mobile, String index, String course, String returnCourse, String jump, long lottid, PrintWriter out) {


    if (mobile == 0) {       // if NOT mobile user
            
      out.println(SystemUtils.HeadTitle("Cancel Request Confirmation Prompt"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><font size=\"6\" color=\"red\"><b>***WARNING***</b><BR>");
      out.println("</font><font size=\"3\"><BR>This will remove ALL players from the Request.<BR>");
      out.println("<BR>If this is what you want to do, then click on 'Continue' below.<BR>");
      out.println("<BR>");

      out.println("<form action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"remove\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"ack_remove\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
      out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
      out.println("<input type=\"submit\" name=\"Continue\" value=\"Continue\"></form>");

      out.println("<BR>If you only want to remove yourself, or a portion of the players,<BR>");
      out.println("click on 'Return' below. Then use the 'erase' button in the Add/Remove Players<BR>");
      out.println("box to remove only those players you wish to remove.<BR>");
      out.println("<BR>If you want to return without changing the request, click on 'Return' below");
      out.println("<BR>and then select the 'Go Back' button.");
      out.println("<BR><BR>");

      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      
    } else {
       
       //  Mobile user
       
       out.println(SystemUtils.HeadTitleMobile("Lottery Cancel Confirmation"));
       out.println(SystemUtils.BannerMobile());
      
       out.println("<div class=\"headertext\">***WARNING***</div>");
       out.println("<div class=\"smheadertext\">This will remove ALL players from the Request.</div>");

       out.println("<form action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\" target=\"_top\">");
       out.println("<input type=\"hidden\" name=\"remove\" value=\"yes\">");
       out.println("<input type=\"hidden\" name=\"ack_remove\" value=\"yes\">");
       out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
       out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
       out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
       out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
       out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
       out.println("<input type=\"submit\" name=\"Continue\" value=\"OK - Remove All\"></form>");

       out.println("<div class=\"smheadertext\">If you want to return without changing the request, click on 'Return' below and then select the 'Go Back' button.</div>");

       out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
       out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
       out.println("</form>");     
       out.println("</body>");
       out.println("</html>");        
       out.close();
    }    
 }
 
 
 // ***************************************************************************************
 //   Display Cancel Done Msg based on user
 // ***************************************************************************************

 private void displayCancelDone(int mobile, String index, String course, String returnCourse, String jump, String lotteryText, String club, long lottid, PrintWriter out) {


    if (mobile == 0) {       // if NOT mobile user
            
      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
      if (club.equals( "oldoaks" )) {
         out.println("<Title>Member Tee Time Request Page</Title>");
      } else if (!lotteryText.equals("")) {
         out.println("<Title>Member " +lotteryText+ " Page</Title>");   // use replacement text
      } else {
         out.println("<Title>Member Lottery Request Page</Title>");
      }
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>Cancel Requested</H3>");
      if (lottid > 0) {
         out.println("<BR><BR>Thank you, the request has been removed from the system.");
      } else {
         out.println("<BR><BR>You are attempting to cancel a request that does not yet exist.");
      }
      out.println("<BR><BR>");

      if (index.equals( "999" )) {         // if came from Member_teelist

         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain.htm\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</form></font>");

      } else {

         if (index.equals( "995" )) {         // if came from Member_teelist_list

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

               if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
                  course = returnCourse;
               }

               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
               out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
               out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("</form></font>");
            }
         }
      }
      out.println("</CENTER></BODY></HTML>");
      out.close();
      
    } else {
       
       //  Mobile user
       
       out.println(SystemUtils.HeadTitleMobile("Lottery Cancel Confirmation"));
       out.println(SystemUtils.BannerMobile());
      
       out.println("<div class=\"headertext\">Cancel Requested</div>");
       
       out.println("<div class=\"smheadertext\">");
       if (lottid > 0) {
         out.println("Thank you, the request has been removed.");
       } else {
         out.println("You are attempting to cancel a request that does not yet exist.");
       }
       out.println("</div>");
       
       out.println("<div class=\"content\">");
       
      if (index.equals( "995" )) {         // if came from Member_teelist_mobile

         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_teelist_mobile\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</form></font>");

      } else {

         out.println("<form action=\"/" +rev+ "/servlet/Member_sheet\" method=\"post\">");
         out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</form>");
      }          
       out.println("</div></body>");
       out.println("</html>");        
       out.close();
    }    
 }
 
 
}
