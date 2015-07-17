/***************************************************************************************
 *   Proshop_slotm:  This servlet will process the 'Consecutive Tee Times' request from
 *                    the Proshop's Sheet page (via Proshop_slot) for Sawgrass only.
 *
 *
 *   called by:  Proshop_sheet via Proshop_slot (doGet)
 *
 *
 *   created: 12/28/2004   Bob P. for Sawgrass
 *
 *   last updated:       ******* keep this accurate *******
 *
 *        7/21/10   Indian Hills CC (indianhillscc) - Default Suppress Emails box to checked (case 1840).
 *        7/12/10   Burloaks CC (burloaks) - Set default MoT for 'Corp' guest type to 'Car'
 *        6/24/10   Modified alphaTable calls to pass the new enableAdvAssist parameter which is used for iPad compatability
 *        6/07/10   Added code to guest tracking verification to allow for the guest "TBA" option
 *        6/04/10   Pass the jump value back on the Return W/O Changes link.
 *        5/21/10   Turner Hill (turnerhill) - set default mode of trans to 'CRT' for all guest types.
 *        5/14/10   Fixed error in parseNames where is was miscouting the number of members (was adding to memg2 when it should of been bumping memg1)
 *        5/11/10   Cherry Creek CC (cherrycreek) - include 'Employee' guset type in default mode of trans custom
 *        5/04/10   Cherry Creek CC (cherrycreek) - set default mode of trans to 'CI' for all guest types.
 *        4/22/10   Brae Burn CC (braeburncc) - set default mode of trans to 'NAP' for all guest types.
 *        4/22/10   Tartan Fields (tartanfields) - set default mode of trans to 'CRT' for all guest types.
 *        4/21/10   The Club At Nevillewood (theclubatnevillewood) - set default mode of trans to 'C' for all guest types.
 *        4/15/10   Silver Creek CC (silvercreekcountryclub) - set default mode of trans to 'GR' for all guest types.
 *        4/09/10   Added guest tracking processing
 *        3/26/10   Add userg, members and guest counts to parm in checkCustoms1.
 *        2/02/10   Trim the notes in verify
 *        1/25/10   Updated moveguest Javascript function to handle the new use_guestdb value being passed to it.
 *        1/06/10   Oakmont - change the max # of guests per month in checkOakGuestQuota from 10 to 8 (case 1364).
 *       12/04/09   Champion Hills (championhills) - Set default mode of trans to 'CF' for all guest types
 *       11/11/09   The Reserve Club (thereserveclub) - Default Suppress Emails box to checked
 *       10/23/09   At the end of the movename script we now send focus back to the DYN_search text box
 *       10/22/09   Round Hill CC (roundhill) - Update default MoT for guest types from 'RCH' to 'RHC'
 *       10/08/09   Ocean Reef CC (oceanreef) - set default mode of trans to 'CT' for all guest types.
 *       10/01/09   No longer perform the check to see if a player is in a tee time more than once (ONLY for proshop side!! and only in slotm!)
 *        9/21/09   Added support for the REST_OVERRIDE limited access proshop user field to block users without access from overriding restrictions.
 *        9/10/09   Woodlands CC - set default mode of trans to 'CRT' for all guest types (case 1722).
 *        9/09/09   Black Diamond Ranch (blackdiamondranch) - set default mode of trans to 'CCT' for all guest types
 *        8/28/09   Add showlott option to tee sheet so pro can pre-book lottery times - make sure we always return this to sheet (case 1703).
 *        8/28/09   MountainGate CC (mtngatecc) - set default mode of trans to 'CF' for all guest types
 *        8/26/09   Round Hill CC (roundhill) - set default mode of trans to 'RCH' for all guest types (case 1714).
 *        8/11/09   Minnetonka CC (minnetonkacc) - set default mode of trans to 'WLK' for all guest types (case 1708).
 *        7/30/09   Add focus setting for moveguest js method to improve efficiency and disabled return key in player positions
 *        7/28/09   Sawgrass CC (sawgrass) - Set default mode of trans to 'CRT' for all guest types
 *        7/14/09   Desert Forest GC (desertforestgolfclub) - always set 'Suppress Emails' option to yes on entry (case 1701).
 *        6/25/09   Forest Highlands (foresthighlands) - Change 'Employee' guest type to default to 'EMP' instead of 'CMP'
 *        6/24/09   Mid Pacific (midpacific) - set default mode of trans to 'GC' for all guest types.
 *        5/27/09   Changed wording on "Player Not Found" override warning to warn that player will be reported as an Unknown Round
 *        5/20/09   Lakewood CC - set default mode of trans to 'CRT' for all guest types (sup req 265).
 *        5/20/09   CC of the Rockies - set default mode of trans to 'GCF' for all guest types (sup req 264).
 *        5/20/09   Forest Highlands - set default mode of trans to 'CMP' for certain guest types (sup req 262).
 *        5/13/09   Shadow Ridge - default to hide notes.
 *        5/07/09   Save the user's username in slotParms for custom processing.
 *        4/04/09   The Oaks Club - use the non-consecutive option for checkInUseMN (case 1671).
 *        4/24/09   Add call to verifyCustom.checkCustomsGst after guests are assigned for custom guest restrictions.
 *                  This was done as part of a TPC unaccompanied guest custom (case 1663).
 *        4/21/09   Chartwell GCC - Allow booking of consecutive tee times for shotgun event tee times (case 1556).
 *        4/17/09   Castle Pines - Highlight player's name in lime if their birthday matches the date of one of their booked tee times (case 1607).
 *        4/17/09   Mayfield Sand Ridge - Added custom checking for twoSomeOnly times.
 *        4/09/09   Blue Bell CC - set default mode of trans to 'CAR' for all guest types.
 *        3/27/09   Woodway CC - Custom guest restriction - mship Restricted Golf may not have guests on Fri/Sat/Sun/Holidays (case 1510).
 *        3/19/09   TPC Sugarloaf - set default mode of trans to 'GCT' for all guest types.
 *        3/06/09   Imperial GC - Change pre-checkin from 2pm to 1 PM - Case #1327
 *       02/22/09   Check for Club Prophet in addition to ProshopKeeper for check-in.
 *       02/17/09   Added Dining Request prompt/link and checkbox on slot page to skip the dining request
 *       01/07/09   Default to automatically hide notes from members for Loxahatchee (Case 1575).
 *       12/26/08   Gulf Harbour GCC - set default mode of trans to 'CRT' for all guest types.
 *       12/18/08   Pelican Marsh GC - set default mode of trans to 'GC' for all guest types.
 *       12/09/08   Added guest restriction suspension handling
 *       12/09/08   Fixed sendMail method to obey proper 4/5 some player grouping
 *       12/04/08   Update parseNames to rebuild the players' names if members to make sure we get the proper titlecase (case 1576).
 *       12/04/08   Updated sendMail so that emails from consecutive tee times include assigned times (fix for case# 1231)
 *       11/12/08   Lakewood Ranch - use the non-consecutive option for checkInUseMN (case 1246).
 *       10/16/08   Add call verifyCustom.checkCustoms1 to check for custom restrictions - this will be the NEW
 *                  process for adding customs and should make it much easier.  Only verifyCustom should have to
 *                  be modified for future customs.
 *       10/13/08   Added "Suppress Email" checkbox when booking multiple tee times at once on proshop side (case 1454).
 *       10/03/08   Add a space after any guest names are put in a tee time slot (case 1551).
 *        9/22/08   Bay Hill - always set 'hidenotes' to Yes (case 1549).
 *        8/21/08   North Ridge - set the default mode of trans to 'CC' for all guest types (case #1535).
 *        7/24/08   Updated limited access proshop users checks
 *        7/18/08   Added limited access proshop users checks
 *        7/07/08   Admirals Cove - set the default mode of trans to 'CF' for all guest types (case #1514).
 *        6/26/08   Baltusrol - add custom guest quota - max of 3 per member outstanding (case 1455).
 *        6/26/08   Get the teecurr_id from each tee time and save in parm so we can more easily id the tee times.
 *        6/16/08   Chartwell GC - set the default mode of trans to 'CAR' for all guest types (case #1504).
 *        6/11/08   Belle Meade - custom restriction for Primary Females on Sundays (case 1496).
 *        6/07/08   Trophy Club - set the default mode of trans to 'GC' for all guest types (case #1489).
 *        6/03/08   St. Clair CC - always default the Terrace course to 9 holes (case 1476).
 *        5/16/08   checkInUseMN was moved from verifyCusotm to verifySlot.
 *        5/05/08   Comment out customs for mship types (custom_disp) - replace by new tflag feature.
 *        4/24/08   Add member tag feature (tflag) where we flag certain members on pro tee sheet (case 1357).
 *        4/16/08   Pass p5rest after displaying the member message.
 *        4/15/08   Wellesley - add Limited mship type when adding an special char to player name.
 *        4/02/08   MN Valley - set the default mode of trans to 'WK' for all guest types (case #1439).
 *        3/27/08   The CC - Changes for including notes in emails (Case #1406)
 *        1/24/08   Oakmont - add new guest quota - members can book up to 10 guest times in Feb, Mar, & Apr (case # 1364).
 *       12/12/07   Plantation at Ponte Vedra - use the non-consecutive option for checkInUseMN.
 *       11/29/07   Jonathans landing - force the mode of trans to 'CRT' for all guest types (case #1334).
 *       11/08/07   Imperial GC - use the non-consecutive option for checkInUseMN (Case #1319).
 *       11/05/07   Mediterra - force the mode of trans to 'R' for all guest types (case #1315).
 *       10/16/07   Red Rocks - force the mode of trans to 'NA' for all guest types (case #1138).
 *       10/16/07   Pinery - force the mode of trans to 'GC' for all guest types (case #1252).
 *       10/16/07   Sonnenalp - do not prompt to assign unaccompanied guests (case #1168).
 *       10/16/07   Crane Creek - always set 'hidenotes' to Yes.
 *       10/06/07   Eagle Creek CC - use the non-consecutive option for checkInUseMN (case #1283).
 *        9/22/07   Pelicans Nest - use the non-consecutive option for checkInUseMN.
 *        9/21/07   Make the custom to prompt user with next available tee time standard for all clubs.
 *        9/05/07   Lakewood Ranch (FL) - Use checkInUseMn to allocate tee times in case one or more is busy (case #1246).
 *        8/28/07   Meadowbrook CC (WI) - Use checkInUseMn to allocate tee times in case one or more is busy (case #1220).
 *        8/20/07   Added Member Notice proshop side display as a configurable option (per notice basis)
 *        8/08/07   Muirfield & Inverness GC - Added Member Notice display to Proshop side
 *        7/17/07   Wilmington - add range privilege indicator to the tee time for display on tee sheet (case #1204).
 *        6/29/07   Merrill Hills - check for special mships so they can be marked on pro tee sheet (case #1183).
 *        6/26/07   When pulling a handicap from member2b get it from the g_hancap field instead of c_hancap
 *        6/18/07   Catamount Ranch - change max number of advance tee times back to 5, except for Founder members - they are unlimited (case #1124).
 *        6/18/07   Sonnenalp - check for max number of advance tee times (case #1089).
 *        6/14/07   Wellesley - custom to check mship types and flag in teecurr for Proshop_sheet display (case #1167).
 *        5/29/07   Sonnenalp - add guest fees to the tee time for display on tee sheet (case #1070).
 *        5/24/07   Catamount Ranch - change max number of advance tee times from 5 to 14 (case #1124).
 *        5/24/07   The CC - allow multiple players with the same name (case #1169).
 *        4/26/07   Long Cove - Use checkInUseMn to allocate tee times in case one or more is busy (case #1149).
 *        4/25/07   Congressional - pass the date for the ourse Name Labeling.
 *        4/23/07   Change call to verifyCustom.checkInUseMn to pass a new 'consecutive' option,
 *        4/10/07   Congressional - custom guest restriction - only 1 guest per 'Junior A' mship type (case #1048).
 *        4/09/07   Congressional - custom guest restriction - Cert Jr Guest must follow a Certified Dependent (case #1058).
 *        4/09/07   Modified call to alphaTable.guestList to pass new boolean parameter
 *        4/04/07   Inverness Club - always set 'hidenotes' to Yes.
 *        4/02/07   Add call to verifyCustom.checkInUseMn for CC of Jackson - if one time of a multi request is busy,
 *                  check for other available times (case 1074).
 *        3/20/07   Custom for Congressional - abstract the course name depending on the day (Course Name Labeling)
 *        3/14/07   Spokane - set the default c/w value for specified guest types.
 *        2/20/07   Merion - if more than 7 days in adv, and a w/e, check for more than 4 tee times.
 *        2/15/07   Modified the call to alphaTable.nameList to include new boolean for ghin integration
 *        2/15/07   Set the clubname and course name for getClub.getParms.
 *        2/09/07   Wilmington CC - check for max of 12 guests total during specified times.
 *        2/07/07   Fort Collins, Greeley Course - always set 'hidenotes' to Yes.
 *        1/08/07   Royal Oaks Houston - always set 'hidenotes' to Yes.
 *       12/08/06   Long Cove - if one time is not available, grab the ones that are (verifyCustom.checkInUseMc).
 *                              Prompt user to see if they want to accept less times.
 *       11/01/06   Long Cove - TEMP to allow members to book more than one time per day.
 *       10/10/06   Correct the error returns so they return parms, not 'back 1'.
 *        9/14/06   Lakewood Ranch - do not allow pro to override the 'hrs between tee times' limit.
 *        8/31/06   Long Cove - allow multiple players with the same name.
 *        7/26/06   Bearpath - custom restriciton for member types of 'CD plus'.
 *        5/17/06   Rancho Bernardo - force the mode of trans to 'CCH' for all guest types.
 *        4/27/06   Added 'Check In' option while editing tee times feature
 *        4/25/06   CC at Castle Pines - Juniors must be with an adult at all times.
 *        4/24/06   CC at Castle Pines - force the mode of trans to 'CRT' for all guest types.
 *        4/11/06   Catamount Ranch - check for max number of advance tee times.
 *        3/15/06   CC of the Rockies - check for max number of advance tee times.
 *        3/15/06   Bearpath - check for guests on weekdays during member-only times.
 *        3/14/06   Oakland Hills - if tee time is 8 days in advance, then no guests and no X's.
 *        3/14/06   Oakland Hills - add a custom restriction for tee time requests more than 8 days in advance.
 *        3/14/06   Oakland Hills - add a custom restriction for member type of Dependent.
 *        3/10/06   Add ability to list names by mship and/or mtype groups.
 *        2/28/06   Merion - color 'House' members' names red in the name lists.
 *        2/27/06   Whenever tee time is added, call SystemUtils.updateHist to track it.
 *        2/27/06   Merion - add custom member restriction (checkMerionSched).
 *        2/27/06   Merion - add custom guest restriction (checkMerionGres).
 *        2/23/06   Merion - add custom guest restriction (checkMerionG).
 *        2/15/06   Allow for new call from Member_slot doGet method.
 *        1/18/06   Lakewood - allow multiple players with the same name, and change the 'Guest Types' box title.
 *       12/21/05   Skaneateles - add a custom restriction for member type of Dependent.
 *       12/05/05   Add call to checkRitz method for custom member restrictions for RitzCarlton.
 *       11/08/05   Always check for 'Exception' on the catch from calls to verifySlot.
 *       11/06/05   Cherry Hills - add a custom restriction for member types.
 *       11/03/05   The Lakes - force the mode of trans to 'NON' for all guest types.
 *       11/03/05   Cherry Hills - let the mode of trans default to null when player field is empty.
 *       10/17/05   The Stanwich Club - Juniors must be with an adult at specified times.
 *        2/16/05   Include the name of the guest restriction in error message.
 *        2/07/05   Do not move names up - allow empty slots in groups.
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
import com.foretees.common.parmPOS;
import com.foretees.common.parmSlotm;
import com.foretees.common.parmSlot;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyCustom;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.alphaTable;
import com.foretees.common.congressionalCustom;
import com.foretees.common.Utilities;


public class Proshop_slotm extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)



 //*****************************************************
 // Process the request from Proshop_slot
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   //
   //  If call from self, then go to doPost
   //
   if (req.getParameter("go") != null) {         // if call from the following process

      doPost(req, resp);      // call doPost processing
   }
 }

 //*****************************************************
 // Process the request from doGet above or self
 //*****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   //ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
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

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "TS_UPDATE", con, out)) {
       SystemUtils.restrictProshop("TS_UPDATE", out);
       return;
   }

   //
   // Process request according to which 'submit' button was selected
   //
   //      'time:fb'   - request from Proshop_sheet
   //      'continue'  - request details from Proshop_slotm
   //      'submitForm'    - submit request from Proshop_slotm
   //      'letter'    - request to list Proshop names from Proshop_slotm
   //      'cancel'    - user clicked on the 'Go Back' button (return w/o changes)
   //      'return'    - a return to Proshop_slotm from verify
   //
   if (req.getParameter("cancel") != null) {

      cancel(req, out, con);                      // process cancel request
      return;
   }

   if (req.getParameter("submitForm") != null) {

      verify(req, out, con, session, resp);                 // process reservation requests
      return;
   }

   //
   //  Get this session's username
   //
   String club = (String)session.getAttribute("club");
   String user = (String)session.getAttribute("user");
   String mshipOpt = (String)session.getAttribute("mshipOpt");
   String mtypeOpt = (String)session.getAttribute("mtypeOpt");

   if (mshipOpt.equals( "" ) || mshipOpt == null) {

      mshipOpt = "ALL";
   }
   if (mtypeOpt.equals( "" ) || mtypeOpt == null) {

      mtypeOpt = "ALL";
   }

   //
   //  Request from Proshop_sheet or Proshop_slotm
   //
   //int count = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int fb = 0;
   //int xCount = 0;
   int i = 0;
   int hide = 0;
   //int nowc = 0;
   int slots = 0;
   int players = 0;
   //int dhr = 0;
   //int dmin = 0;
   //int lmin = 0;
   //int dfb = 0;
   int in_use = 0;
   //int groups = 0;
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
   int assign = 0;

   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;
   short show6 = 0;
   short show7 = 0;
   short show8 = 0;
   short show9 = 0;
   short show10 = 0;
   short show11 = 0;
   short show12 = 0;
   short show13 = 0;
   short show14 = 0;
   short show15 = 0;
   short show16 = 0;
   short show17 = 0;
   short show18 = 0;
   short show19 = 0;
   short show20 = 0;
   short show21 = 0;
   short show22 = 0;
   short show23 = 0;
   short show24 = 0;
   short show25 = 0;


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
   String mem1 = "";
   String mem2 = "";
   String mem3 = "";
   String mem4 = "";
   String mem5 = "";
   String mem6 = "";
   String mem7 = "";
   String mem8 = "";
   String mem9 = "";
   String mem10 = "";
   String mem11 = "";
   String mem12 = "";
   String mem13 = "";
   String mem14 = "";
   String mem15 = "";
   String mem16 = "";
   String mem17 = "";
   String mem18 = "";
   String mem19 = "";
   String mem20 = "";
   String mem21 = "";
   String mem22 = "";
   String mem23 = "";
   String mem24 = "";
   String mem25 = "";/*
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String user6 = "";
   String user7 = "";
   String user8 = "";
   String user9 = "";
   String user10 = "";
   String user11 = "";
   String user12 = "";
   String user13 = "";
   String user14 = "";
   String user15 = "";
   String user16 = "";
   String user17 = "";
   String user18 = "";
   String user19 = "";
   String user20 = "";
   String user21 = "";
   String user22 = "";
   String user23 = "";
   String user24 = "";
   String user25 = "";*/
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
   //String pcw = "";

   //String dampm = "";
   String sdate = "";
   String stime = "";
   //String ltime = "";
   String sfb = "";
   //String dsfb = "";
   String notes = "";
   String hides = "";
   //String smins_before = "";              // 'minutes before' selected time
   //String smins_after = "";               // 'minutes after' selected time
   String shr = "";
   String smin = "";
   String ampm = "";
   //String in_use_by = "";
   String temps = "";
   String returnCourse = "";
   String suppressEmails = "";
   String skipDining = "";
   String pname = "";

   // Check proshop user feature access for appropriate access rights
   boolean diningAccess = SystemUtils.verifyProAccess(req, "DINING_REQUEST", con, out);

   boolean enableAdvAssist = Utilities.enableAdvAssist(req);

   //  array to hold guest bag tag indicators for Oakland Hills
   String[] gbt = new String[25];
   String[] checked = new String[25];

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con);         // golf only

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   //  parm block to hold the tee time parms
   //
   parmSlotm slotParms = new parmSlotm();          // allocate a parm block

   //
   //  parm block to hold the POS parameters
   //
   parmPOS parmp = new parmPOS();          // allocate a parm block for POS parms

   //
   // Get all the parameters entered
   //
   String index = req.getParameter("index");          //  index value of day (needed by Proshop_sheet when returning)
   String course = req.getParameter("course");        //  Name of Course
   String day_name = req.getParameter("day");         //  name of the day
   String p5 = req.getParameter("p5");                //  5-somes supported
   String p5rest = req.getParameter("p5rest");        //  5-somes restricted

   //
   //  Check if pro selected a tee time during a lottery before it was ready (pre-book) - if so, warn him
   //
   String showlott = "";
   if (req.getParameter("showlott") != null) {        // if time during a lottery

      showlott = req.getParameter("showlott");        // get the value
   }


   course = SystemUtils.filter(course);              // replace html special characters with real ones

   slotParms.club = club;                          // save club name & other parms
   slotParms.course = course;
   slotParms.index = index;
   slotParms.p5 = p5;
   slotParms.p5rest = p5rest;

   String sslots = "";

   if (req.getParameter("contimes") != null) {        // if this is a new call from _sheet

      sslots = req.getParameter("contimes");          //  # of groups requested

   } else {                                           // call from self

      sslots = req.getParameter("slots");
   }

   //
   //   Save club info in club parm table
   //
   parm.club = club;
   parm.course = course;

   // get pos info for this club
   try {

       //
       //  Get the POS System Parameters for this Club & Course
       //
       getClub.getPOS(con, parmp, course);
   }
   catch (Exception e) {

       out.println(SystemUtils.HeadTitle("DB Error"));
       out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
       out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
       out.println("<BR><BR><H3>Database Access Error</H3>");
       out.println("<BR><BR>Unable to access the Database at this time (get pos parms).");
       out.println("<BR>Please try again later.");
       out.println("<BR><BR>If problem persists, contact customer support.");
       out.println("<BR><BR>" + e.getMessage());
       out.println("<BR><BR>");
       out.println("<a href=\"javascript:history.back(1)\">Return</a>");
       out.println("</BODY></HTML>");
       out.close();

       return;

   } // end try/catch

   //
   //  Get this year
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);            // get the year

   //
   //  Convert the string values to ints
   //
   try {
      slots = Integer.parseInt(sslots);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   String jump = "0";                     // jump index - default to zero (for _sheet)

   if (req.getParameter("jump") != null) {            // if jump index provided

      jump = req.getParameter("jump");
   }

   if (req.getParameter("returnCourse") != null) {        // if returnCourse provided

      returnCourse = req.getParameter("returnCourse");
   }

   if (req.getParameter("assign") != null) {     // if this is to assign members to guests

      assign = 1;                                // indicate 'assign members to guest' (Unaccompanied Guests)
   }

   if (req.getParameter("skipDining") != null) {    // user wish to skip dining prompt

       skipDining = req.getParameter("skipDining");
   }

   if (req.getParameter("suppressEmails") != null) {   // user wish to suppress emails? (option on this page only)

      suppressEmails = req.getParameter("suppressEmails");

   } else {

      if (club.equals("charlottecc") || club.equals("shadowridgecc") || club.equals("desertforestgolfclub") || club.equals("thereserveclub") || club.equals("indianhillscc")) {         // always set to Yes initially for Charlotte

         suppressEmails = "yes";

      } else {

         suppressEmails = "no";
      }
   }

   if (req.getParameter("fb") != null) {            // if date was passed in sdate

      sfb = req.getParameter("fb");
   }

   if (req.getParameter("sdate") != null) {         // if date was passed in sdate

      sdate = req.getParameter("sdate");
   }

   if (req.getParameter("date") != null) {          // if date was passed in date

      sdate = req.getParameter("date");
   }

   if (req.getParameter("stime") != null) {         // if time was passed in stime

      stime = req.getParameter("stime");

   } else {                                           // call from Member_sheet

      //
      //    The name of the submit button (time) has the front/back indicator appended to it ('time:fb')
      //
      Enumeration enum1 = req.getParameterNames();     // get the parm name passed

      while (enum1.hasMoreElements()) {

         pname = (String) enum1.nextElement();             // get parm name

         if (pname.startsWith( "time" )) {

            stime = req.getParameter(pname);              //  value = time of tee time requested (hh:mm AM/PM)

            StringTokenizer tok = new StringTokenizer( pname, ":" );     // separate name around the colon

            sfb = tok.nextToken();                        // skip past 'time '
            sfb = tok.nextToken();                        // get the front/back indicator value
         }
      }
   }


   slotParms.jump = jump;                                // save jump value


   //
   //  determine # of players allowed for this request
   //
   //   Players will = 4, 5, 8, 10, 12, 15, 16, 20 or 25 (based on 4 or 5-somes and number of slots/groups)
   //
   if (p5.equals( "Yes" ) && p5rest.equals( "No" )) {       // 5-somes allowed ?

      p5 = "Yes";
      players = slots * 5;                    // Yes, set total # of players

   } else {

      p5 = "No";
      players = slots * 4;                    // No
   }

   //
   //  Convert the values from string to int
   //
   try {
      date = Long.parseLong(sdate);
      fb = Integer.parseInt(sfb);
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

   if (req.getParameter("return") != null || req.getParameter("memNotice") != null) {     // if this is a return from verify - time = hhmm

      try {
         time = Integer.parseInt(stime);
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
      if (min < 10) {
         stime = hr + ":0" + min + " " + ampm;
      } else {
         stime = hr + ":" + min + " " + ampm;
      }

   } else {

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
   }

   boolean show_checkin = (
       (!parmp.posType.equals( "Pro-ShopKeeper" ) && !parmp.posType.equals( "ClubProphetV3" )) &&
       mm == (cal.get(cal.MONTH) + 1) &&
       dd == cal.get(cal.DAY_OF_MONTH) &&
       yy == cal.get(cal.YEAR));
   //show_checkin = true;


   // restrict display of check-in is proshop user does not have checkin feature access
   if (show_checkin) {
       show_checkin = SystemUtils.verifyProAccess(req, "TS_CHECKIN", con, out);
   }

   //
   //  if this is a call from self (a return of some type)
   //
   if ((req.getParameter("letter") != null) || (req.getParameter("return") != null) ||
       (req.getParameter("mtypeopt") != null) || (req.getParameter("promptLessTimes") != null) || (req.getParameter("memNotice") != null)) {  // if user clicked on a name letter or mtype

      temps = req.getParameter("time2");                  // get the time values
      slotParms.time2 = Integer.parseInt(temps);
      temps = req.getParameter("time3");                  // get the time values
      slotParms.time3 = Integer.parseInt(temps);
      temps = req.getParameter("time4");                  // get the time values
      slotParms.time4 = Integer.parseInt(temps);
      temps = req.getParameter("time5");                  // get the time values
      slotParms.time5 = Integer.parseInt(temps);

      if (req.getParameter("player1") != null) {
         player1 = req.getParameter("player1");     // get the player info from the player table
      }
      if (req.getParameter("player2") != null) {
         player2 = req.getParameter("player2");
      }
      if (req.getParameter("player3") != null) {
         player3 = req.getParameter("player3");
      }
      if (req.getParameter("player4") != null) {
         player4 = req.getParameter("player4");
      }
      if (req.getParameter("player5") != null) {
         player5 = req.getParameter("player5");
      }
      if (req.getParameter("player6") != null) {
         player6 = req.getParameter("player6");
      }
      if (req.getParameter("player7") != null) {
         player7 = req.getParameter("player7");
      }
      if (req.getParameter("player8") != null) {
         player8 = req.getParameter("player8");
      }
      if (req.getParameter("player9") != null) {
         player9 = req.getParameter("player9");
      }
      if (req.getParameter("player10") != null) {
         player10 = req.getParameter("player10");
      }
      if (req.getParameter("player11") != null) {
         player11 = req.getParameter("player11");
      }
      if (req.getParameter("player12") != null) {
         player12 = req.getParameter("player12");
      }
      if (req.getParameter("player13") != null) {
         player13 = req.getParameter("player13");
      }
      if (req.getParameter("player14") != null) {
         player14 = req.getParameter("player14");
      }
      if (req.getParameter("player15") != null) {
         player15 = req.getParameter("player15");
      }
      if (req.getParameter("player16") != null) {
         player16 = req.getParameter("player16");
      }
      if (req.getParameter("player17") != null) {
         player17 = req.getParameter("player17");
      }
      if (req.getParameter("player18") != null) {
         player18 = req.getParameter("player18");
      }
      if (req.getParameter("player19") != null) {
         player19 = req.getParameter("player19");
      }
      if (req.getParameter("player20") != null) {
         player20 = req.getParameter("player20");
      }
      if (req.getParameter("player21") != null) {
         player21 = req.getParameter("player21");
      }
      if (req.getParameter("player22") != null) {
         player22 = req.getParameter("player22");
      }
      if (req.getParameter("player23") != null) {
         player23 = req.getParameter("player23");
      }
      if (req.getParameter("player24") != null) {
         player24 = req.getParameter("player24");
      }
      if (req.getParameter("player25") != null) {
         player25 = req.getParameter("player25");
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

      if (req.getParameter("mem1") != null) {
         mem1 = req.getParameter("mem1");
      }
      if (req.getParameter("mem2") != null) {
         mem2 = req.getParameter("mem2");
      }
      if (req.getParameter("mem3") != null) {
         mem3 = req.getParameter("mem3");
      }
      if (req.getParameter("mem4") != null) {
         mem4 = req.getParameter("mem4");
      }
      if (req.getParameter("mem5") != null) {
         mem5 = req.getParameter("mem5");
      }
      if (req.getParameter("mem6") != null) {
         mem6 = req.getParameter("mem6");
      }
      if (req.getParameter("mem7") != null) {
         mem7 = req.getParameter("mem7");
      }
      if (req.getParameter("mem8") != null) {
         mem8 = req.getParameter("mem8");
      }
      if (req.getParameter("mem9") != null) {
         mem9 = req.getParameter("mem9");
      }
      if (req.getParameter("mem10") != null) {
         mem10 = req.getParameter("mem10");
      }
      if (req.getParameter("mem11") != null) {
         mem11 = req.getParameter("mem11");
      }
      if (req.getParameter("mem12") != null) {
         mem12 = req.getParameter("mem12");
      }
      if (req.getParameter("mem13") != null) {
         mem13 = req.getParameter("mem13");
      }
      if (req.getParameter("mem14") != null) {
         mem14 = req.getParameter("mem14");
      }
      if (req.getParameter("mem15") != null) {
         mem15 = req.getParameter("mem15");
      }
      if (req.getParameter("mem16") != null) {
         mem16 = req.getParameter("mem16");
      }
      if (req.getParameter("mem17") != null) {
         mem17 = req.getParameter("mem17");
      }
      if (req.getParameter("mem18") != null) {
         mem18 = req.getParameter("mem18");
      }
      if (req.getParameter("mem19") != null) {
         mem19 = req.getParameter("mem19");
      }
      if (req.getParameter("mem20") != null) {
         mem20 = req.getParameter("mem20");
      }
      if (req.getParameter("mem21") != null) {
         mem21 = req.getParameter("mem21");
      }
      if (req.getParameter("mem22") != null) {
         mem22 = req.getParameter("mem22");
      }
      if (req.getParameter("mem23") != null) {
         mem23 = req.getParameter("mem23");
      }
      if (req.getParameter("mem24") != null) {
         mem24 = req.getParameter("mem24");
      }
      if (req.getParameter("mem25") != null) {
         mem25 = req.getParameter("mem25");
      }

      guest_id1 = (req.getParameter("guest_id1") != null) ? Integer.parseInt(req.getParameter("guest_id1")) : 0;
      guest_id2 = (req.getParameter("guest_id2") != null) ? Integer.parseInt(req.getParameter("guest_id2")) : 0;
      guest_id3 = (req.getParameter("guest_id3") != null) ? Integer.parseInt(req.getParameter("guest_id3")) : 0;
      guest_id4 = (req.getParameter("guest_id4") != null) ? Integer.parseInt(req.getParameter("guest_id4")) : 0;
      guest_id5 = (req.getParameter("guest_id5") != null) ? Integer.parseInt(req.getParameter("guest_id5")) : 0;
      guest_id6 = (req.getParameter("guest_id6") != null) ? Integer.parseInt(req.getParameter("guest_id6")) : 0;
      guest_id7 = (req.getParameter("guest_id7") != null) ? Integer.parseInt(req.getParameter("guest_id7")) : 0;
      guest_id8 = (req.getParameter("guest_id8") != null) ? Integer.parseInt(req.getParameter("guest_id8")) : 0;
      guest_id9 = (req.getParameter("guest_id9") != null) ? Integer.parseInt(req.getParameter("guest_id9")) : 0;
      guest_id10 = (req.getParameter("guest_id10") != null) ? Integer.parseInt(req.getParameter("guest_id10")) : 0;
      guest_id11 = (req.getParameter("guest_id11") != null) ? Integer.parseInt(req.getParameter("guest_id11")) : 0;
      guest_id12 = (req.getParameter("guest_id12") != null) ? Integer.parseInt(req.getParameter("guest_id12")) : 0;
      guest_id13 = (req.getParameter("guest_id13") != null) ? Integer.parseInt(req.getParameter("guest_id13")) : 0;
      guest_id14 = (req.getParameter("guest_id14") != null) ? Integer.parseInt(req.getParameter("guest_id14")) : 0;
      guest_id15 = (req.getParameter("guest_id15") != null) ? Integer.parseInt(req.getParameter("guest_id15")) : 0;
      guest_id16 = (req.getParameter("guest_id16") != null) ? Integer.parseInt(req.getParameter("guest_id16")) : 0;
      guest_id17 = (req.getParameter("guest_id17") != null) ? Integer.parseInt(req.getParameter("guest_id17")) : 0;
      guest_id18 = (req.getParameter("guest_id18") != null) ? Integer.parseInt(req.getParameter("guest_id18")) : 0;
      guest_id19 = (req.getParameter("guest_id19") != null) ? Integer.parseInt(req.getParameter("guest_id19")) : 0;
      guest_id20 = (req.getParameter("guest_id20") != null) ? Integer.parseInt(req.getParameter("guest_id20")) : 0;
      guest_id21 = (req.getParameter("guest_id21") != null) ? Integer.parseInt(req.getParameter("guest_id21")) : 0;
      guest_id22 = (req.getParameter("guest_id22") != null) ? Integer.parseInt(req.getParameter("guest_id22")) : 0;
      guest_id23 = (req.getParameter("guest_id23") != null) ? Integer.parseInt(req.getParameter("guest_id23")) : 0;
      guest_id24 = (req.getParameter("guest_id24") != null) ? Integer.parseInt(req.getParameter("guest_id24")) : 0;
      guest_id25 = (req.getParameter("guest_id25") != null) ? Integer.parseInt(req.getParameter("guest_id25")) : 0;

      if (req.getParameter("notes") != null) {
         notes = req.getParameter("notes");
         hides = req.getParameter("hide");

         //
         //  Convert hide from string to int
         //
         try {
            hide = Integer.parseInt(hides);
         }
         catch (NumberFormatException e) {
            // ignore error
         }
      }

      if (req.getParameter("mtypeopt") != null) {

         mtypeOpt = req.getParameter("mtypeopt");
         session.setAttribute("mtypeOpt", mtypeOpt);   //  Save the member class options in the session for next time
      }
      if (req.getParameter("mshipopt") != null) {
         mshipOpt = req.getParameter("mshipopt");
         session.setAttribute("mshipOpt", mshipOpt);
      }

      show1 = (req.getParameter("show1") != null) ? Short.parseShort(req.getParameter("show1")) : 0;
      show2 = (req.getParameter("show2") != null) ? Short.parseShort(req.getParameter("show2")) : 0;
      show3 = (req.getParameter("show3") != null) ? Short.parseShort(req.getParameter("show3")) : 0;
      show4 = (req.getParameter("show4") != null) ? Short.parseShort(req.getParameter("show4")) : 0;
      show5 = (req.getParameter("show5") != null) ? Short.parseShort(req.getParameter("show5")) : 0;
      show6 = (req.getParameter("show6") != null) ? Short.parseShort(req.getParameter("show6")) : 0;
      show7 = (req.getParameter("show7") != null) ? Short.parseShort(req.getParameter("show7")) : 0;
      show8 = (req.getParameter("show8") != null) ? Short.parseShort(req.getParameter("show8")) : 0;
      show9 = (req.getParameter("show9") != null) ? Short.parseShort(req.getParameter("show9")) : 0;
      show10 = (req.getParameter("show10") != null) ? Short.parseShort(req.getParameter("show10")) : 0;
      show11 = (req.getParameter("show11") != null) ? Short.parseShort(req.getParameter("show11")) : 0;
      show12 = (req.getParameter("show12") != null) ? Short.parseShort(req.getParameter("show12")) : 0;
      show13 = (req.getParameter("show13") != null) ? Short.parseShort(req.getParameter("show13")) : 0;
      show14 = (req.getParameter("show14") != null) ? Short.parseShort(req.getParameter("show14")) : 0;
      show15 = (req.getParameter("show15") != null) ? Short.parseShort(req.getParameter("show15")) : 0;
      show16 = (req.getParameter("show16") != null) ? Short.parseShort(req.getParameter("show16")) : 0;
      show17 = (req.getParameter("show17") != null) ? Short.parseShort(req.getParameter("show17")) : 0;
      show18 = (req.getParameter("show18") != null) ? Short.parseShort(req.getParameter("show18")) : 0;
      show19 = (req.getParameter("show19") != null) ? Short.parseShort(req.getParameter("show19")) : 0;
      show20 = (req.getParameter("show20") != null) ? Short.parseShort(req.getParameter("show20")) : 0;
      show21 = (req.getParameter("show21") != null) ? Short.parseShort(req.getParameter("show21")) : 0;
      show22 = (req.getParameter("show22") != null) ? Short.parseShort(req.getParameter("show22")) : 0;
      show23 = (req.getParameter("show23") != null) ? Short.parseShort(req.getParameter("show23")) : 0;
      show24 = (req.getParameter("show24") != null) ? Short.parseShort(req.getParameter("show24")) : 0;
      show25 = (req.getParameter("show25") != null) ? Short.parseShort(req.getParameter("show25")) : 0;

      if (club.equals("oaklandhills")) {        // grab all the guest bag tag parameters

          for (int k=0; k<25; k++) {
              if (req.getParameter("custom" + String.valueOf(k + 1)) != null && req.getParameter("custom" + String.valueOf(k + 1)).equals("1")) {
                  gbt[k] = "1";
                  checked[k] = " checked";
              } else {
                  gbt[k] = "";
                  checked[k] = "";
              }
          }
      }

   } else {        // not a letter request or return

      //
      //  if Royal Oaks Houston or Inverness Club - always hide notes from members, but allow pro to override (do not force on returns)
      //
      if (club.equals( "royaloakscc" ) || club.equals( "invernessclub" ) || club.equals( "cranecreek" ) || club.equals( "bayhill" )) {

         hide = 1;
      }

      //
      //  if Fort Collins, Greeley Course, or Loxahatchee - always hide notes from members, but allow pro to override (do not force on returns)
      //
      if ((club.equals( "fortcollins" ) && course.equals( "Greeley CC" )) || club.equals("loxahatchee") ||
           club.equals("shadowridgecc")) {

         hide = 1;
      }

      // If Chartwell GCC and is a consecutive shotgun time (custom), set special flag in slotParms
      if (club.equals("chartwellgcc") && req.getParameter("sgcons") != null) {
          slotParms.custom_string1 = "sgcons";
      }

      //
      //  Get the players' names and check if any of the tee slots are already in use
      //
      //  NOTE: All tee times requested MUST be empty and NOT in use at this time.
      //
      slotParms.day = day_name;            // save day name
      slotParms.slots = slots;             // save # of tee times requested
      slotParms.p5 = p5;
      slotParms.fb = fb;
      slotParms.date = date;
      slotParms.returnCourse = returnCourse;
      slotParms.showlott = showlott;

      //
      //  Verify the required parms exist
      //
      if (date == 0 || time == 0 || course == null || user.equals( "" ) || user == null) {

         //
         //  save message in /" +rev+ "/error.txt
         //
         String msg = "Error in Proshop_slotm - checkInUseM Parms - for user " +user+ " at " +club+ ".  Date= " +date+ ", time= " +time+ ", course= " +course+ ", fb= " +fb;   // build msg
         SystemUtils.logError(msg);                                   // log it

         in_use = 1;          // make like the time is busy

      } else {               // continue if parms ok

         try {

            //
            //  Set tee times busy if not already, and if not during an event or blocker!!!!!!!!
            //
            boolean consecutive = true;         // return consecutive tee times only

            if (club.equals( "ccjackson" ) || club.equals( "longcove" ) || club.equals( "pelicansnest" ) ||
                club.equals( "eaglecreek" ) || club.equals( "imperialgc" ) || club.equals( "plantationpv" ) ||
                club.equals( "lakewoodranch" ) || club.equals( "theoaksclub" )) {

               consecutive = false;         // consecutive tee times not necessary
            }

            in_use = verifySlot.checkInUseMn(consecutive, date, time, fb, course, user, slotParms, con);   // custom check

            //
            //  If we did not get all the tee times requested, then ask the user if they want to proceed or go back.
            //
            if (in_use == 9) {                     // if found, but different than requested

               promptLessTimes(out, slotParms);    // send prompt
               return;                             // exit and wait for answer
            }

         }
         catch (Exception e1) {

            String msg = "Proshop_slotm Check in use flag failed - Exception: " + e1.getMessage();

            SystemUtils.logError(msg);                                   // log it

            in_use = 1;          // make like the time is busy
         }
      }

      if (in_use == 1) {              // if time slot already in use

         if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
            course = returnCourse;
         }
         out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<CENTER><BR><BR><H3>Tee Time Slot Busy</H3>");
         out.println("<BR><BR>Sorry, but at least one of the requested tee times are currently busy.");
         out.println("<BR><BR>All " +slots+ " tee times must be on the same nine and completely unoccupied.");
         out.println("<BR><BR>Please select another time or try again later.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"showlott\" value=\"" + showlott + "\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      if (in_use == 2) {              // if one or more tee times during an event

         if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
            course = returnCourse;
         }
         out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<CENTER><BR><BR><H3>Tee Time Slot Busy</H3>");
         out.println("<BR><BR>Sorry, but at least one of the requested tee times are reserved for an event.");
         out.println("<BR><BR>Please select another time or try again later.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"showlott\" value=\"" + showlott + "\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      if (in_use == 3) {              // if one or more tee times during a blocker

         if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
            course = returnCourse;
         }
         out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<CENTER><BR><BR><H3>Tee Time Slot Busy</H3>");
         out.println("<BR><BR>Sorry, but at least one of the requested tee times has been blocked.");
         out.println("<BR><BR>Please select another time or try again later.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"showlott\" value=\"" + showlott + "\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      if (in_use == 4) {              // db error - most likely not enough tee times left in the day

         if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
            course = returnCourse;
         }
         out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<CENTER><BR><BR><H3>Tee Time Slots Not Available</H3>");
         out.println("<BR><BR>Sorry, but at least one of the requested tee times are not currently available.");
         out.println("<BR>It could be that you requesting too many times for this time of the day.");
         out.println("<BR><BR>Please select another time or try again later.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"showlott\" value=\"" + showlott + "\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      //
      //**********************************************
      //   Check for Member Notice from Pro
      //**********************************************
      //
      String memNotice = verifySlot.checkMemNotice(date, time, fb, course, day_name, "teetime", true, con);

      if (!memNotice.equals( "" )) {      // if message to display

         //
         //  Display the Pro's Message and then prompt the user to either accept or return to the tee sheet
         //
         out.println("<HTML><HEAD>");
         out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
         out.println("<title>Member Notice For Tee Time Request</Title>");
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
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_slotm\" method=\"post\" name=\"can\">");
               out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
               out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
               out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
               out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
               out.println("<input type=\"hidden\" name=\"time2\" value=\"" + slotParms.time2 + "\">");
               out.println("<input type=\"hidden\" name=\"time3\" value=\"" + slotParms.time3 + "\">");
               out.println("<input type=\"hidden\" name=\"time4\" value=\"" + slotParms.time4 + "\">");
               out.println("<input type=\"hidden\" name=\"time5\" value=\"" + slotParms.time5 + "\">");
               out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
               out.println("<input type=\"hidden\" name=\"showlott\" value=\"" + showlott + "\">");
               out.println("<input type=\"submit\" value=\"NO - Return\" name=\"cancel\"></form>");

               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_slotm\" method=\"post\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + p5rest + "\">");
               out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
               out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
               out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
               out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
               out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
               out.println("<input type=\"hidden\" name=\"player6\" value=\"" + player6 + "\">");
               out.println("<input type=\"hidden\" name=\"player7\" value=\"" + player7 + "\">");
               out.println("<input type=\"hidden\" name=\"player8\" value=\"" + player8 + "\">");
               out.println("<input type=\"hidden\" name=\"player9\" value=\"" + player9 + "\">");
               out.println("<input type=\"hidden\" name=\"player10\" value=\"" + player10 + "\">");
               out.println("<input type=\"hidden\" name=\"player11\" value=\"" + player11 + "\">");
               out.println("<input type=\"hidden\" name=\"player12\" value=\"" + player12 + "\">");
               out.println("<input type=\"hidden\" name=\"player13\" value=\"" + player13 + "\">");
               out.println("<input type=\"hidden\" name=\"player14\" value=\"" + player14 + "\">");
               out.println("<input type=\"hidden\" name=\"player15\" value=\"" + player15 + "\">");
               out.println("<input type=\"hidden\" name=\"player16\" value=\"" + player16 + "\">");
               out.println("<input type=\"hidden\" name=\"player17\" value=\"" + player17 + "\">");
               out.println("<input type=\"hidden\" name=\"player18\" value=\"" + player18 + "\">");
               out.println("<input type=\"hidden\" name=\"player19\" value=\"" + player19 + "\">");
               out.println("<input type=\"hidden\" name=\"player20\" value=\"" + player20 + "\">");
               out.println("<input type=\"hidden\" name=\"player21\" value=\"" + player21 + "\">");
               out.println("<input type=\"hidden\" name=\"player22\" value=\"" + player22 + "\">");
               out.println("<input type=\"hidden\" name=\"player23\" value=\"" + player23 + "\">");
               out.println("<input type=\"hidden\" name=\"player24\" value=\"" + player24 + "\">");
               out.println("<input type=\"hidden\" name=\"player25\" value=\"" + player25 + "\">");
               out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
               out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
               out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
               out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
               out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
               out.println("<input type=\"hidden\" name=\"p6cw\" value=\"" + p6cw + "\">");
               out.println("<input type=\"hidden\" name=\"p7cw\" value=\"" + p7cw + "\">");
               out.println("<input type=\"hidden\" name=\"p8cw\" value=\"" + p8cw + "\">");
               out.println("<input type=\"hidden\" name=\"p9cw\" value=\"" + p9cw + "\">");
               out.println("<input type=\"hidden\" name=\"p10cw\" value=\"" + p10cw + "\">");
               out.println("<input type=\"hidden\" name=\"p11cw\" value=\"" + p11cw + "\">");
               out.println("<input type=\"hidden\" name=\"p12cw\" value=\"" + p12cw + "\">");
               out.println("<input type=\"hidden\" name=\"p13cw\" value=\"" + p13cw + "\">");
               out.println("<input type=\"hidden\" name=\"p14cw\" value=\"" + p14cw + "\">");
               out.println("<input type=\"hidden\" name=\"p15cw\" value=\"" + p15cw + "\">");
               out.println("<input type=\"hidden\" name=\"p16cw\" value=\"" + p16cw + "\">");
               out.println("<input type=\"hidden\" name=\"p17cw\" value=\"" + p17cw + "\">");
               out.println("<input type=\"hidden\" name=\"p18cw\" value=\"" + p18cw + "\">");
               out.println("<input type=\"hidden\" name=\"p19cw\" value=\"" + p19cw + "\">");
               out.println("<input type=\"hidden\" name=\"p20cw\" value=\"" + p20cw + "\">");
               out.println("<input type=\"hidden\" name=\"p21cw\" value=\"" + p21cw + "\">");
               out.println("<input type=\"hidden\" name=\"p22cw\" value=\"" + p22cw + "\">");
               out.println("<input type=\"hidden\" name=\"p23cw\" value=\"" + p23cw + "\">");
               out.println("<input type=\"hidden\" name=\"p24cw\" value=\"" + p24cw + "\">");
               out.println("<input type=\"hidden\" name=\"p25cw\" value=\"" + p25cw + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + p95 + "\">");
               out.println("<input type=\"hidden\" name=\"p96\" value=\"" + p96 + "\">");
               out.println("<input type=\"hidden\" name=\"p97\" value=\"" + p97 + "\">");
               out.println("<input type=\"hidden\" name=\"p98\" value=\"" + p98 + "\">");
               out.println("<input type=\"hidden\" name=\"p99\" value=\"" + p99 + "\">");
               out.println("<input type=\"hidden\" name=\"p910\" value=\"" + p910 + "\">");
               out.println("<input type=\"hidden\" name=\"p911\" value=\"" + p911 + "\">");
               out.println("<input type=\"hidden\" name=\"p912\" value=\"" + p912 + "\">");
               out.println("<input type=\"hidden\" name=\"p913\" value=\"" + p913 + "\">");
               out.println("<input type=\"hidden\" name=\"p914\" value=\"" + p914 + "\">");
               out.println("<input type=\"hidden\" name=\"p915\" value=\"" + p915 + "\">");
               out.println("<input type=\"hidden\" name=\"p916\" value=\"" + p916 + "\">");
               out.println("<input type=\"hidden\" name=\"p917\" value=\"" + p917 + "\">");
               out.println("<input type=\"hidden\" name=\"p918\" value=\"" + p918 + "\">");
               out.println("<input type=\"hidden\" name=\"p919\" value=\"" + p919 + "\">");
               out.println("<input type=\"hidden\" name=\"p920\" value=\"" + p920 + "\">");
               out.println("<input type=\"hidden\" name=\"p921\" value=\"" + p921 + "\">");
               out.println("<input type=\"hidden\" name=\"p922\" value=\"" + p922 + "\">");
               out.println("<input type=\"hidden\" name=\"p923\" value=\"" + p923 + "\">");
               out.println("<input type=\"hidden\" name=\"p924\" value=\"" + p924 + "\">");
               out.println("<input type=\"hidden\" name=\"p925\" value=\"" + p925 + "\">");
               out.println("<input type=\"hidden\" name=\"show1\" value=\"" + show1 + "\">");
               out.println("<input type=\"hidden\" name=\"show2\" value=\"" + show2 + "\">");
               out.println("<input type=\"hidden\" name=\"show3\" value=\"" + show3 + "\">");
               out.println("<input type=\"hidden\" name=\"show4\" value=\"" + show4 + "\">");
               out.println("<input type=\"hidden\" name=\"show5\" value=\"" + show5 + "\">");
               out.println("<input type=\"hidden\" name=\"show6\" value=\"" + show6 + "\">");
               out.println("<input type=\"hidden\" name=\"show7\" value=\"" + show7 + "\">");
               out.println("<input type=\"hidden\" name=\"show8\" value=\"" + show8 + "\">");
               out.println("<input type=\"hidden\" name=\"show9\" value=\"" + show9 + "\">");
               out.println("<input type=\"hidden\" name=\"show10\" value=\"" + show10 + "\">");
               out.println("<input type=\"hidden\" name=\"show11\" value=\"" + show11 + "\">");
               out.println("<input type=\"hidden\" name=\"show12\" value=\"" + show12 + "\">");
               out.println("<input type=\"hidden\" name=\"show13\" value=\"" + show13 + "\">");
               out.println("<input type=\"hidden\" name=\"show14\" value=\"" + show14 + "\">");
               out.println("<input type=\"hidden\" name=\"show15\" value=\"" + show15 + "\">");
               out.println("<input type=\"hidden\" name=\"show16\" value=\"" + show16 + "\">");
               out.println("<input type=\"hidden\" name=\"show17\" value=\"" + show17 + "\">");
               out.println("<input type=\"hidden\" name=\"show18\" value=\"" + show18 + "\">");
               out.println("<input type=\"hidden\" name=\"show19\" value=\"" + show19 + "\">");
               out.println("<input type=\"hidden\" name=\"show20\" value=\"" + show20 + "\">");
               out.println("<input type=\"hidden\" name=\"show21\" value=\"" + show21 + "\">");
               out.println("<input type=\"hidden\" name=\"show22\" value=\"" + show22 + "\">");
               out.println("<input type=\"hidden\" name=\"show23\" value=\"" + show23 + "\">");
               out.println("<input type=\"hidden\" name=\"show24\" value=\"" + show24 + "\">");
               out.println("<input type=\"hidden\" name=\"show25\" value=\"" + show25 + "\">");
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
               out.println("<input type=\"hidden\" name=\"time2\" value=\"" + slotParms.time2 + "\">");
               out.println("<input type=\"hidden\" name=\"time3\" value=\"" + slotParms.time3 + "\">");
               out.println("<input type=\"hidden\" name=\"time4\" value=\"" + slotParms.time4 + "\">");
               out.println("<input type=\"hidden\" name=\"time5\" value=\"" + slotParms.time5 + "\">");
               out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
               out.println("<input type=\"hidden\" name=\"showlott\" value=\"" + showlott + "\">");
               out.println("<input type=\"hidden\" name=\"memNotice\" value=\"yes\">");

               if (club.equals("oaklandhills")) {
                   for (int k=0; i<25; i++) {
                       out.println("<input type=\"hidden\" name=\"custom" + String.valueOf(k + 1) + "\" value=\"" + gbt[k] + "\">");
                   }
               }

               out.println("<input type=\"submit\" value=\"YES - Continue\"></form>");
               out.println("</font></td></tr>");
               out.println("</table>");

               out.println("</td>");
               out.println("</tr>");
            out.println("</table>");
         out.println("</font></center></body></html>");
         out.close();
         return;
      } // end Notice display

   }              // end of 'letter' if


   //
   //  St Clair CC - Terrace course is always 9 holes
   //
   if (club.equals("stclaircc") && course.equals( "Terrace" )) {

      p91 = 1;
      p92 = 1;
      p93 = 1;
      p94 = 1;
      p95 = 1;
      p96 = 1;
      p97 = 1;
      p98 = 1;
      p99 = 1;
      p910 = 1;
      p911 = 1;
      p912 = 1;
      p913 = 1;
      p914 = 1;
      p915 = 1;
      p916 = 1;
      p917 = 1;
      p918 = 1;
      p919 = 1;
      p920 = 1;
      p921 = 1;
      p922 = 1;
      p923 = 1;
      p924 = 1;
      p925 = 1;
   }


   //
   //  Get the walk/cart options available
   //
   try {

      getParms.getCourse(con, parmc, course);
   }
   catch (Exception e1) {

      dbError2(index, course, out, e1);
      return;
   }


   //
   //  Build the HTML page to prompt user for names
   //
   out.println("<HTML>");
   out.println("<HEAD>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
   out.println("<Title>Proshop Tee Time Request Page</Title>");

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
      out.println("<script language='JavaScript'>");            // Submit the form when clicking on a letter
      out.println("<!--");
      out.println("function subletter(x) {");

      out.println("document.playerform.letter.value = x;");         // put the letter in the parm
      out.println("playerform.submit();");                // submit the form
      out.println("}");                                   // end of script function
      out.println("// -->");
      out.println("</script>");                   // End of script

   if (assign == 0) {   // if normal tee time

      //
      //*******************************************************************
      //  Erase player name (erase button selected next to player's name)
      //
      //    Remove the player's name and shift any other names up starting at player1
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Erase name script
      out.println("<!--");

      out.println("function erasename(pos1) {");

      out.println("document.playerform[pos1].value = '';");           // clear the player field

      out.println("var pos2 = pos1.replace('player', 'guest_id');");
      out.println("document.playerform[pos2].value = '0';");           // clear the guest_id field
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      //
      //*******************************************************************
      //  Erase text area - (Notes)
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Erase text area script
      out.println("<!--");
      out.println("function erasetext(pos1) {");
      out.println("document.playerform[pos1].value = '';");           // clear the text field
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      out.println("<script language='JavaScript'>");             // Move Notes into textarea
      out.println("<!--");
      out.println("function movenotes() {");
      out.println("var oldnotes = document.playerform.oldnotes.value;");
      out.println("document.playerform.notes.value = oldnotes;");   // put notes in text area
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      //
      //*******************************************************************
      //  Move a member name into the tee slot
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Move name script
      out.println("<!--");

      out.println("function movename(namewc) {");

      out.println("del = ':';");                               // deliminator is a colon
      out.println("array = namewc.split(del);");                 // split string into 2 pieces (name, wc)
      out.println("var name = array[0];");
      out.println("var wc = array[1];");
      out.println("skip = 0;");

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

      if (!club.equals( "lakewood" ) && !club.equals( "longcove" ) && !club.equals( "tcclub" )) {
         out.println("if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4) || ");
         out.println("    ( name == player5) || ( name == player6) || ( name == player7) || ( name == player8) || ");
         out.println("    ( name == player9) || ( name == player10) || ( name == player11) || ( name == player12) || ");
         out.println("    ( name == player13) || ( name == player14) || ( name == player15) || ( name == player16) || ");
         out.println("    ( name == player17) || ( name == player18) || ( name == player19) || ( name == player20) || ");
         out.println("    ( name == player21) || ( name == player22) || ( name == player23) || ( name == player24) || ");
         out.println("    ( name == player25)) {");
            out.println("skip = 1;");
         out.println("}");
      }

      out.println("if (skip == 0) {");

         out.println("if (player1 == '') {");                    // if player1 is empty
            out.println("f.player1.value = name;");
            out.println("f.guest_id1.value = '0';");
            out.println("f.p1cw.value = wc;");
         out.println("} else {");

         out.println("if (player2 == '') {");                    // if player2 is empty
            out.println("f.player2.value = name;");
            out.println("f.guest_id2.value = '0';");
            out.println("f.p2cw.value = wc;");
         out.println("} else {");

         out.println("if (player3 == '') {");                    // if player3 is empty
            out.println("f.player3.value = name;");
            out.println("f.guest_id3.value = '0';");
            out.println("f.p3cw.value = wc;");
         out.println("} else {");

         out.println("if (player4 == '') {");                    // if player4 is empty
            out.println("f.player4.value = name;");
            out.println("f.guest_id4.value = '0';");
            out.println("f.p4cw.value = wc;");

       if (players > 4) {
         out.println("} else {");
         out.println("if (player5 == '') {");                    // if player5 is empty
            out.println("f.player5.value = name;");
            out.println("f.guest_id5.value = '0';");
            out.println("f.p5cw.value = wc;");
       }
       if (players > 5) {
         out.println("} else {");
         out.println("if (player6 == '') {");                    // if player6 is empty
            out.println("f.player6.value = name;");
            out.println("f.guest_id6.value = '0';");
            out.println("f.p6cw.value = wc;");
         out.println("} else {");
         out.println("if (player7 == '') {");                    // if player7 is empty
            out.println("f.player7.value = name;");
            out.println("f.guest_id7.value = '0';");
            out.println("f.p7cw.value = wc;");
         out.println("} else {");
         out.println("if (player8 == '') {");                    // if player8 is empty
            out.println("f.player8.value = name;");
            out.println("f.guest_id8.value = '0';");
            out.println("f.p8cw.value = wc;");
       }
       if (players > 8) {
         out.println("} else {");
         out.println("if (player9 == '') {");                    // if player9 is empty
            out.println("f.player9.value = name;");
            out.println("f.guest_id9.value = '0';");
            out.println("f.p9cw.value = wc;");
         out.println("} else {");
         out.println("if (player10 == '') {");                    // if player10 is empty
            out.println("f.player10.value = name;");
            out.println("f.guest_id10.value = '0';");
            out.println("f.p10cw.value = wc;");
       }
       if (players > 10) {
         out.println("} else {");
         out.println("if (player11 == '') {");                    // if player11 is empty
            out.println("f.player11.value = name;");
            out.println("f.guest_id11.value = '0';");
            out.println("f.p11cw.value = wc;");
         out.println("} else {");
         out.println("if (player12 == '') {");                    // if player12 is empty
            out.println("f.player12.value = name;");
            out.println("f.guest_id12.value = '0';");
            out.println("f.p12cw.value = wc;");
       }
       if (players > 12) {
         out.println("} else {");
         out.println("if (player13 == '') {");                    // if player13 is empty
            out.println("f.player13.value = name;");
            out.println("f.guest_id13.value = '0';");
            out.println("f.p13cw.value = wc;");
         out.println("} else {");
         out.println("if (player14 == '') {");                    // if player14 is empty
            out.println("f.player14.value = name;");
            out.println("f.guest_id14.value = '0';");
            out.println("f.p14cw.value = wc;");
         out.println("} else {");
         out.println("if (player15 == '') {");                    // if player15 is empty
            out.println("f.player15.value = name;");
            out.println("f.guest_id15.value = '0';");
            out.println("f.p15cw.value = wc;");
       }
       if (players > 15) {
         out.println("} else {");
         out.println("if (player16 == '') {");                    // if player16 is empty
            out.println("f.player16.value = name;");
            out.println("f.guest_id16.value = '0';");
            out.println("f.p16cw.value = wc;");
       }
       if (players > 16) {
         out.println("} else {");
         out.println("if (player17 == '') {");                    // if player17 is empty
            out.println("f.player17.value = name;");
            out.println("f.guest_id17.value = '0';");
            out.println("f.p17cw.value = wc;");
         out.println("} else {");
         out.println("if (player18 == '') {");                    // if player18 is empty
            out.println("f.player18.value = name;");
            out.println("f.guest_id18.value = '0';");
            out.println("f.p18cw.value = wc;");
         out.println("} else {");
         out.println("if (player19 == '') {");                    // if player19 is empty
            out.println("f.player19.value = name;");
            out.println("f.guest_id19.value = '0';");
            out.println("f.p19cw.value = wc;");
         out.println("} else {");
         out.println("if (player20 == '') {");                    // if player20 is empty
            out.println("f.player20.value = name;");
            out.println("f.guest_id20.value = '0';");
            out.println("f.p20cw.value = wc;");
       }
       if (players > 20) {
         out.println("} else {");
         out.println("if (player21 == '') {");                    // if player21 is empty
            out.println("f.player21.value = name;");
            out.println("f.guest_id21.value = '0';");
            out.println("f.p21cw.value = wc;");
         out.println("} else {");
         out.println("if (player22 == '') {");                    // if player22 is empty
            out.println("f.player22.value = name;");
            out.println("f.guest_id22.value = '0';");
            out.println("f.p22cw.value = wc;");
         out.println("} else {");
         out.println("if (player23 == '') {");                    // if player23 is empty
            out.println("f.player23.value = name;");
            out.println("f.guest_id23.value = '0';");
            out.println("f.p23cw.value = wc;");
         out.println("} else {");
         out.println("if (player24 == '') {");                    // if player24 is empty
            out.println("f.player24.value = name;");
            out.println("f.guest_id24.value = '0';");
            out.println("f.p24cw.value = wc;");
         out.println("} else {");
         out.println("if (player25 == '') {");                    // if player25 is empty
            out.println("f.player25.value = name;");
            out.println("f.guest_id25.value = '0';");
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

      out.println("f.DYN_search.focus();");
      out.println("f.DYN_search.select();");

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");                               // End of script


      //
      //*******************************************************************
      //  Move a Guest Name into the tee slot
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Move Guest Name script
      out.println("<!--");

      out.println("var guestid_slot;");
      out.println("var player_slot;");

      out.println("function moveguest(namewc) {");

      //out.println("var name = namewc;");

      out.println("array = namewc.split('|');"); // split string (partner_name, partner_id)
      out.println("var name = array[0];");

      if (enableAdvAssist) {
          out.println("var use_guestdb = array[1];");
      } else {
          out.println("var use_guestdb = 0; // force to off on iPad");
      }

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
      if (club.equals( "lakes" )) {
         out.println("defCW = 'NON';");       // set default Mode of Trans
      }
      if (club.equals( "castlepines" ) || club.equals( "jonathanslanding" ) || club.equals("gulfharbourgcc") || club.equals("lakewoodcc") ||
          club.equals("sawgrass") || club.equals("woodlandscountryclub") || club.equals("tartanfields") || club.equals("turnerhill")) {
         out.println("defCW = 'CRT';");       // set default Mode of Trans
      }
      if (club.equals( "ranchobernardo" )) {
         out.println("defCW = 'CCH';");       // set default Mode of Trans
      }

      if (club.equals( "pinery" ) || club.equals( "trophyclubcc" ) || club.equals( "pmarshgc" ) || club.equals("midpacific")) {
         out.println("defCW = 'GC';");       // set default Mode of Trans
      }

      if (club.equals( "redrocks" )) {
         out.println("defCW = 'NA';");       // set default Mode of Trans
      }

      if (club.equals( "chartwellgcc" ) || club.equals( "bluebellcc" )) {
         out.println("defCW = 'CAR';");       // set default Mode of Trans
      }

      if (club.equals( "admiralscove" ) || club.equals( "mtngatecc" ) || club.equals("championhills")) {
         out.println("defCW = 'CF';");       // set default Mode of Trans
      }

      if (club.equals( "mediterra" )) {
         out.println("defCW = 'R';");       // set default Mode of Trans
      }

      if (club.equals( "mnvalleycc" )) {
         out.println("defCW = 'WK';");       // set default Mode of Trans
      }

      if (club.equals( "northridge" )) {
         out.println("defCW = 'CC';");       // set default Mode of Trans
      }

      if (club.equals( "spokane" )) {
          out.println("if (name == 'Unaccompanied' || name == 'Tournament' || name == 'Mini Group' ) {");
            out.println("defCW = 'CC';");       // set default Mode of Trans
          out.println("}");
      }

      if (club.equals( "cherrycreek" )) {
          out.println("if (name == 'Accompanied' || name == 'PGA Member' || name == 'Family' || name == 'Employee') {");
            out.println("defCW = 'CI';");       // set default Mode of Trans
          out.println("}");
      }

      if (club.equals( "minnetonkacc" )) {
          out.println("defCW = 'WLK';");
      }

      if (club.equals( "blackdiamondranch" )) {
          out.println("defCW = 'CCT';");
      }

      if (club.equals( "tpcsugarloaf" )) {

          out.println("defCW = 'GCT';");
      }

      if (club.equals( "ccrockies" )) {
          out.println("defCW = 'GCF';");
      }

      if (club.equals( "roundhill" )) {

          out.println("defCW = 'RHC';");
      }
      if (club.equals( "oceanreef" )) {

          out.println("defCW = 'CT';");
      }

      if (club.equals("silvercreekcountryclub")) {
          out.println("defCW = 'GR';");
      }

      if (club.equals("theclubatnevillewood")) {
          out.println("defCW = 'C';");
      }

      if (club.equals("braeburncc")) {
          out.println("defCW = 'NAP';");
      }

      if (club.equals( "foresthighlands" )) {
          out.println("if (name == 'PGA' || name == 'Token' || name == 'Tourney' || name == 'Gift Certificate' || name == 'Comp') {");
            out.println("defCW = 'CMP';");
          out.println("} else if (name == 'Employee') {");
            out.println("defCW = 'EMP';");
          out.println("}");
      }

      if (club.equals("burloaks")) {
          out.println("if (name == 'Corp') {");
          out.println("defCW = 'Car';");       // set default Mode of Trans
          out.println("}");
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

   } else {   // this is a prompt to assign a member to an Unaccompanied Guest

      //
      //*******************************************************************
      //  Erase Associated Member name (for Unaccompanied Guests Prompt)
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Erase name script
      out.println("<!--");

      out.println("function erasemem(pos1) {");
      out.println("document.playerform[pos1].value = '';");           // clear the member name field
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      //
      //*******************************************************************
      //  Move a member name into the Associated Member slot for Unaccomp. Guests
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Move name script
      out.println("<!--");

      out.println("function movename(namewc) {");

      out.println("del = ':';");                                // deliminator is a colon
      out.println("array = namewc.split(del);");                // split string into 2 pieces (name, wc)
      out.println("var name = array[0];");                      // just get the name

      out.println("var f = document.forms['playerform'];");

      out.println("var mem1 = f.mem1.value;");
      out.println("var mem2 = f.mem2.value;");
      out.println("var mem3 = f.mem3.value;");
      out.println("var mem4 = f.mem4.value;");

      if (players > 4) {
         out.println("var mem5 = f.mem5.value;");
      } else {
         out.println("var mem5 = '';");
      }
      if (players > 5) {
         out.println("var mem6 = f.mem6.value;");
         out.println("var mem7 = f.mem7.value;");
         out.println("var mem8 = f.mem8.value;");
      } else {
         out.println("var mem6 = '';");
         out.println("var mem7 = '';");
         out.println("var mem8 = '';");
      }
      if (players > 8) {
         out.println("var mem9 = f.mem9.value;");
         out.println("var mem10 = f.mem10.value;");
      } else {
         out.println("var mem9 = '';");
         out.println("var mem10 = '';");
      }
      if (players > 10) {
         out.println("var mem11 = f.mem11.value;");
         out.println("var mem12 = f.mem12.value;");
      } else {
         out.println("var mem11 = '';");
         out.println("var mem12 = '';");
      }
      if (players > 12) {
         out.println("var mem13 = f.mem13.value;");
         out.println("var mem14 = f.mem14.value;");
         out.println("var mem15 = f.mem15.value;");
      } else {
         out.println("var mem13 = '';");
         out.println("var mem14 = '';");
         out.println("var mem15 = '';");
      }
      if (players > 15) {
         out.println("var mem16 = f.mem16.value;");
      } else {
         out.println("var mem16 = '';");
      }
      if (players > 16) {
         out.println("var mem17 = f.mem17.value;");
         out.println("var mem18 = f.mem18.value;");
         out.println("var mem19 = f.mem19.value;");
         out.println("var mem20 = f.mem20.value;");
      } else {
         out.println("var mem17 = '';");
         out.println("var mem18 = '';");
         out.println("var mem19 = '';");
         out.println("var mem20 = '';");
      }
      if (players > 20) {
         out.println("var mem21 = f.mem21.value;");
         out.println("var mem22 = f.mem22.value;");
         out.println("var mem23 = f.mem23.value;");
         out.println("var mem24 = f.mem24.value;");
         out.println("var mem25 = f.mem25.value;");
      } else {
         out.println("var mem21 = '';");
         out.println("var mem22 = '';");
         out.println("var mem23 = '';");
         out.println("var mem24 = '';");
         out.println("var mem25 = '';");
      }

      out.println("if (( name != 'x') && ( name != 'X')) {");



         out.println("if (mem1 == '') {");                    // if mem1 is empty
            out.println("f.mem1.value = name;");
         out.println("} else {");

         out.println("if (mem2 == '') {");                    // if mem2 is empty
            out.println("f.mem2.value = name;");
         out.println("} else {");

         out.println("if (mem3 == '') {");                    // if mem3 is empty
            out.println("f.mem3.value = name;");
         out.println("} else {");

         out.println("if (mem4 == '') {");                    // if mem4 is empty
            out.println("f.mem4.value = name;");

         if (players > 4) {
           out.println("} else {");
           out.println("if (mem5 == '') {");                    // if mem5 is empty
              out.println("f.mem5.value = name;");
         }
         if (players > 5) {
           out.println("} else {");
           out.println("if (mem6 == '') {");                    // if mem6 is empty
              out.println("f.mem6.value = name;");
           out.println("} else {");
           out.println("if (mem7 == '') {");                    // if mem7 is empty
              out.println("f.mem7.value = name;");
           out.println("} else {");
           out.println("if (mem8 == '') {");                    // if mem8 is empty
              out.println("f.mem8.value = name;");
         }
         if (players > 8) {
           out.println("} else {");
           out.println("if (mem9 == '') {");                    // if mem9 is empty
              out.println("f.mem9.value = name;");
           out.println("} else {");
           out.println("if (mem10 == '') {");                    // if mem10 is empty
              out.println("f.mem10.value = name;");
         }
         if (players > 10) {
           out.println("} else {");
           out.println("if (mem11 == '') {");                    // if mem11 is empty
              out.println("f.mem11.value = name;");
           out.println("} else {");
           out.println("if (mem12 == '') {");                    // if mem12 is empty
              out.println("f.mem12.value = name;");
         }
         if (players > 12) {
           out.println("} else {");
           out.println("if (mem13 == '') {");                    // if mem13 is empty
              out.println("f.mem13.value = name;");
           out.println("} else {");
           out.println("if (mem14 == '') {");                    // if mem14 is empty
              out.println("f.mem14.value = name;");
           out.println("} else {");
           out.println("if (mem15 == '') {");                    // if mem15 is empty
              out.println("f.mem15.value = name;");
         }
         if (players > 15) {
           out.println("} else {");
           out.println("if (mem16 == '') {");                    // if mem16 is empty
              out.println("f.mem16.value = name;");
         }
         if (players > 16) {
           out.println("} else {");
           out.println("if (mem17 == '') {");                    // if mem17 is empty
              out.println("f.mem17.value = name;");
           out.println("} else {");
           out.println("if (mem18 == '') {");                    // if mem18 is empty
              out.println("f.mem18.value = name;");
           out.println("} else {");
           out.println("if (mem19 == '') {");                    // if mem19 is empty
              out.println("f.mem19.value = name;");
           out.println("} else {");
           out.println("if (mem20 == '') {");                    // if mem20 is empty
              out.println("f.mem20.value = name;");
         }
         if (players > 20) {
           out.println("} else {");
           out.println("if (mem21 == '') {");                    // if mem21 is empty
              out.println("f.mem21.value = name;");
           out.println("} else {");
           out.println("if (mem22 == '') {");                    // if mem22 is empty
              out.println("f.mem22.value = name;");
           out.println("} else {");
           out.println("if (mem23 == '') {");                    // if mem23 is empty
              out.println("f.mem23.value = name;");
           out.println("} else {");
           out.println("if (mem24 == '') {");                    // if mem24 is empty
              out.println("f.mem24.value = name;");
           out.println("} else {");
           out.println("if (mem25 == '') {");                    // if mem25 is empty
              out.println("f.mem25.value = name;");

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

      out.println("}");                  // end of IF name != x

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");                               // End of script
   }

   out.println("</HEAD>");

   // ********* end of scripts **********

   out.println("<body onLoad=\"movenotes()\" bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
   out.println("<tr><td valign=\"top\" align=\"center\">");

   out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"CCCCAA\" align=\"center\" valign=\"top\">");
     out.println("<tr><td align=\"left\" width=\"300\">");
     out.println("&nbsp;&nbsp;&nbsp;<b>ForeTees</b>");
     out.println("</td>");

     out.println("<td align=\"center\">");
     out.println("<font size=\"5\">Golf Shop Reservation</font>");
     out.println("</font></td>");

     out.println("<td align=\"center\" width=\"300\">");
     out.println("<font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
     out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
     out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC <br> " +thisYear+ " All rights reserved.");
     out.println("</font></td>");
   out.println("</tr></table>");

   out.println("<table border=\"0\" align=\"center\">");                           // table for main page
   out.println("<tr><td align=\"center\">");

      out.println("<br>");
      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"620\" align=\"center\">");
         out.println("<font size=\"3\">");
         out.println("<b>Tee Time Registration</b><br></font>");
         out.println("<font size=\"1\">");
         if (assign == 0) {
            out.println("<b>Warning</b>:&nbsp;&nbsp;You have <b>6 minutes</b> to complete this reservation.");
            out.println("&nbsp; If you want to return without completing a reservation, <b>do not ");
            out.println("use your browser's BACK</b> button/option.&nbsp; Instead select the <b>Go Back</b> ");
            out.println("option below.");
         } else {
            out.println("<b>Assign a Member for each Unaccompanied Guest.<br>");
            out.println("You can specify a member more than once.</b>");
         }
         out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<font size=\"2\"><br>");
      out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b>");
        out.println(" &nbsp;&nbsp;&nbsp;&nbsp;First Time Requested:&nbsp;&nbsp;<b>" + stime + "</b>");
     if (!course.equals( "" )) {

        if (club.equals("congressional")) {
            out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + congressionalCustom.getFullCourseName(date, (int)dd, course) + "</b>");
        } else {
            out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>");
        }

     }

      out.println("<table border=\"0\" align=\"center\" valign=\"top\" cellpadding=\"5\" cellspacing=\"5\">"); // table to contain 4 tables below

         out.println("<tr>");
         out.println("<td align=\"center\" valign=\"top\">");         // col for Instructions

            out.println("<font size=\"1\">");
            if (assign == 0) {      // if normal tee time
               out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/proshop_help_slot_instruct.htm', 'newwindow', config='Height=460, width=500, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            } else {
               out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/proshop_help_slot_unacomp.htm', 'newwindow', config='Height=380, width=500, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            }
            out.println("<img src=\"/" +rev+ "/images/instructions.gif\" border=0>");
            out.println("<br>Click for Help</a>");

            out.println("</font><font size=\"2\">");
            out.println("<br><br><br>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_slotm\" method=\"post\" name=\"can\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
            out.println("<input type=\"hidden\" name=\"time2\" value=\"" + slotParms.time2 + "\">");
            out.println("<input type=\"hidden\" name=\"time3\" value=\"" + slotParms.time3 + "\">");
            out.println("<input type=\"hidden\" name=\"time4\" value=\"" + slotParms.time4 + "\">");
            out.println("<input type=\"hidden\" name=\"time5\" value=\"" + slotParms.time5 + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
            out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
            out.println("<input type=\"hidden\" name=\"showlott\" value=\"" + showlott + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
            out.println("Return<br>w/o Changes:<br>");
            out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");
          out.println("</font></td>");

          out.println("<form action=\"/" +rev+ "/servlet/Proshop_slotm\" method=\"POST\" name=\"playerform\">");
          out.println("<td align=\"center\" valign=\"top\">");

          if (assign == 0) {      // if normal tee time

              if (club.equals("oaklandhills")) {
                  out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" width=\"525\">");  // table for player selection
              } else {
                  out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" width=\"425\">");  // table for player selection - width was 370 before chkin
              }

            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<b>Add or Remove Players</b>");
            out.println("</font></td></tr>");

            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\"><nobr>");

            //out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            //out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Players");
            //out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            //out.println("&nbsp;&nbsp;&nbsp;Trans&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9-Holes</b><br>");

            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            if (club.equals("oaklandhills")) {
                out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            }
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Players");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("&nbsp;&nbsp;&nbsp;Trans&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"/" +rev+ "/images/9hole.gif\" height=17 width=22>&nbsp;");

            if (show_checkin) {
                out.println("&nbsp;&nbsp;&nbsp;<img src=\"/" +rev+ "/images/checkin.gif\" width=30 height=17>&nbsp;");
                //out.println("&nbsp;&nbsp;&nbsp;&radic; In&nbsp;");
            }

            if (club.equals("oaklandhills")) {
               out.println("Guest Bag Tag");
            }

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

            out.println("</b></nobr>");

            out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasename('player1')\" style=\"cursor:hand\">");
            out.println("1: &nbsp;&nbsp;<input type=\"text\" name=\"player1\" value=\"" + player1 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p1cw\">");
              if (!p1cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p1cw + ">" + p1cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p1cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p91 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p91\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p91\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show1 == 1) ? "checked " : "") + "name=\"show1\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom1\"" + checked[0] + " value=\"1\">");
              }


            out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasename('player2')\" style=\"cursor:hand\">");
            out.println("2: &nbsp;&nbsp;<input type=\"text\" name=\"player2\" value=\"" + player2 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p2cw\">");
              if (!p2cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p2cw + ">" + p2cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p2cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p92 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p92\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p92\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show2 == 1) ? "checked " : "") + "name=\"show2\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom2\"" + checked[1] + " value=\"1\">");
              }

            out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasename('player3')\" style=\"cursor:hand\">");
            out.println("3: &nbsp;&nbsp;<input type=\"text\" name=\"player3\" value=\"" + player3 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p3cw\">");
              if (!p3cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p3cw + ">" + p3cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p3cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p93 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p93\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p93\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show3 == 1) ? "checked " : "") + "name=\"show3\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom3\"" + checked[2] + " value=\"1\">");
              }

            out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasename('player4')\" style=\"cursor:hand\">");
            out.println("4: &nbsp;&nbsp;<input type=\"text\" name=\"player4\" value=\"" + player4 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p4cw\">");
              if (!p4cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p4cw + ">" + p4cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p4cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p94 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p94\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p94\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show4 == 1) ? "checked " : "") + "name=\"show4\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom4\"" + checked[3] + " value=\"1\">");
              }

            if ((p5.equals( "Yes" )) || (players > 4)) {

               if (p5.equals( "No" )) {   // if 4-somes only

                 out.println("</font></td></tr>");

                 out.println("<tr><td align=\"center\">");     // new row for new group
                 out.println("<font size=\"2\">");
               }

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasename('player5')\" style=\"cursor:hand\">");
               out.println("5: &nbsp;&nbsp;<input type=\"text\" id=\"player5\" name=\"player5\" value=\"" + player5 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
               out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p5cw\" id=\"p5cw\">");
               if (!p5cw.equals( "" ) || club.equals( "cherryhills" )) {
                  out.println("<option selected value=" + p5cw + ">" + p5cw + "</option>");
               }
               for (i=0; i<16; i++) {

                  if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p5cw )) {
                     out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                  }
               }
               out.println("</select>");
              if (p95 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p95\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p95\" value=\"1\">");
              }
            } else {

               out.println("<input type=\"hidden\" name=\"player5\" value=\"\">");
               out.println("<input type=\"hidden\" name=\"p5cw\" value=\"\">");
            }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show5 == 1) ? "checked " : "") + "name=\"show5\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom5\"" + checked[4] + " value=\"1\">");
              }

           if (players > 5) {

              if (p5.equals( "Yes" )) {

                 out.println("</font></td></tr>");

                 out.println("<tr><td align=\"center\">");     // new row for new group
                 out.println("<font size=\"2\">");
              }

              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasename('player6')\" style=\"cursor:hand\">");
              out.println("6: &nbsp;&nbsp;<input type=\"text\" name=\"player6\" value=\"" + player6 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p6cw\">");
              if (!p6cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p6cw + ">" + p6cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p6cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p96 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p96\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p96\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show6 == 1) ? "checked " : "") + "name=\"show6\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom6\"" + checked[5] + " value=\"1\">");
              }

              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player7')\" style=\"cursor:hand\">");
              out.println("7: &nbsp;&nbsp;<input type=\"text\" name=\"player7\" value=\"" + player7 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p7cw\">");
              if (!p7cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p7cw + ">" + p7cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p7cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p97 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p97\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p97\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show7 == 1) ? "checked " : "") + "name=\"show7\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom7\"" + checked[6] + " value=\"1\">");
              }

              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player8')\" style=\"cursor:hand\">");
              out.println("8: &nbsp;&nbsp;<input type=\"text\" name=\"player8\" value=\"" + player8 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p8cw\">");
              if (!p8cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p8cw + ">" + p8cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p8cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p98 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p98\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p98\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show8 == 1) ? "checked " : "") + "name=\"show8\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom8\"" + checked[7] + " value=\"1\">");
              }

           }

           if (players > 8) {

             if (p5.equals( "No" )) {   // if 4-somes only

               out.println("</font></td></tr>");

               out.println("<tr><td align=\"center\">");     // new row for new group
               out.println("<font size=\"2\">");
             }

              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player9')\" style=\"cursor:hand\">");
              out.println("9: &nbsp;&nbsp;<input type=\"text\" name=\"player9\" value=\"" + player9 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p9cw\">");
              if (!p9cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p9cw + ">" + p9cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p9cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p99 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p99\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p99\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show9 == 1) ? "checked " : "") + "name=\"show9\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom9\"" + checked[8] + " value=\"1\">");
              }

              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player10')\" style=\"cursor:hand\">");
              out.println("10:&nbsp;&nbsp;<input type=\"text\" id=\"player10\" name=\"player10\" value=\"" + player10 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p10cw\" id=\"p10cw\">");
              if (!p10cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p10cw + ">" + p10cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p10cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p910 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p910\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p910\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show10 == 1) ? "checked " : "") + "name=\"show10\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom10\"" + checked[9] + " value=\"1\">");
              }

           }

           if (players > 10) {

             if (p5.equals( "Yes" )) {   // if 5-somes

               out.println("</font></td></tr>");

               out.println("<tr><td align=\"center\">");     // new row for new group
               out.println("<font size=\"2\">");
             }


              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player11')\" style=\"cursor:hand\">");
              out.println("11:&nbsp;&nbsp;<input type=\"text\" name=\"player11\" value=\"" + player11 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p11cw\">");
              if (!p11cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p11cw + ">" + p11cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p11cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p911 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p911\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p911\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show11 == 1) ? "checked " : "") + "name=\"show11\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom11\"" + checked[10] + " value=\"1\">");
              }

              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player12')\" style=\"cursor:hand\">");
              out.println("12:&nbsp;&nbsp;<input type=\"text\" name=\"player12\" value=\"" + player12 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p12cw\">");
              if (!p12cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p12cw + ">" + p12cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p12cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p912 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p912\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p912\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show12 == 1) ? "checked " : "") + "name=\"show12\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom12\"" + checked[11] + " value=\"1\">");
              }

           }

           if (players > 12) {

             if (p5.equals( "No" )) {   // if 4-somes only

               out.println("</font></td></tr>");

               out.println("<tr><td align=\"center\">");     // new row for new group
               out.println("<font size=\"2\">");
             }


              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player13')\" style=\"cursor:hand\">");
              out.println("13:&nbsp;&nbsp;<input type=\"text\" name=\"player13\" value=\"" + player13 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p13cw\">");
              if (!p13cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p13cw + ">" + p13cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p13cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p913 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p913\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p913\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show13 == 1) ? "checked " : "") + "name=\"show13\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom13\"" + checked[12] + " value=\"1\">");
              }

              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player14')\" style=\"cursor:hand\">");
              out.println("14:&nbsp;&nbsp;<input type=\"text\" name=\"player14\" value=\"" + player14 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p14cw\">");
              if (!p14cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p14cw + ">" + p14cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p14cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p914 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p914\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p914\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show14 == 1) ? "checked " : "") + "name=\"show14\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom14\"" + checked[13] + " value=\"1\">");
              }

            out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player15')\" style=\"cursor:hand\">");
            out.println("15:&nbsp;&nbsp;<input type=\"text\" id=\"player15\" name=\"player15\" value=\"" + player15 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p15cw\" id=\"p15cw\">");
              if (!p15cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p15cw + ">" + p15cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p15cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p915 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p915\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p915\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show15 == 1) ? "checked " : "") + "name=\"show15\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom15\"" + checked[14] + " value=\"1\">");
              }

           }

           if (players > 15) {

             if (p5.equals( "Yes" )) {   // if 5-somes

               out.println("</font></td></tr>");

               out.println("<tr><td align=\"center\">");     // new row for new group
               out.println("<font size=\"2\">");
             }


              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player16')\" style=\"cursor:hand\">");
              out.println("16:&nbsp;&nbsp;<input type=\"text\" name=\"player16\" value=\"" + player16 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p16cw\">");
              if (!p16cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p16cw + ">" + p16cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p16cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p916 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p916\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p916\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show16 == 1) ? "checked " : "") + "name=\"show16\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom16\"" + checked[15] + " value=\"1\">");
              }

           }
           if (players > 16) {

             if (p5.equals( "No" )) {   // if 4-somes only

               out.println("</font></td></tr>");

               out.println("<tr><td align=\"center\">");     // new row for new group
               out.println("<font size=\"2\">");
             }

              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player17')\" style=\"cursor:hand\">");
              out.println("17:&nbsp;&nbsp;<input type=\"text\" name=\"player17\" value=\"" + player17 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p17cw\">");
              if (!p17cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p17cw + ">" + p17cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p17cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p917 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p917\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p917\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show17 == 1) ? "checked " : "") + "name=\"show17\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom17\"" + checked[16] + " value=\"1\">");
              }

              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player18')\" style=\"cursor:hand\">");
              out.println("18:&nbsp;&nbsp;<input type=\"text\" name=\"player18\" value=\"" + player18 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p18cw\">");
              if (!p18cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p18cw + ">" + p18cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p18cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p918 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p918\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p918\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show18 == 1) ? "checked " : "") + "name=\"show18\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom18\"" + checked[17] + " value=\"1\">");
              }

              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player19')\" style=\"cursor:hand\">");
              out.println("19:&nbsp;&nbsp;<input type=\"text\" name=\"player19\" value=\"" + player19 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p19cw\">");
              if (!p19cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p19cw + ">" + p19cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p19cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p919 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p919\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p919\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show19 == 1) ? "checked " : "") + "name=\"show19\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom19\"" + checked[18] + " value=\"1\">");
              }

              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player20')\" style=\"cursor:hand\">");
              out.println("20:&nbsp;&nbsp;<input type=\"text\" id=\"player20\" name=\"player20\" value=\"" + player20 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p20cw\" id=\"p20cw\">");
              if (!p20cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p20cw + ">" + p20cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p20cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p920 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p920\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p920\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show20 == 1) ? "checked " : "") + "name=\"show20\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom20\"" + checked[19] + " value=\"1\">");
              }

           }

           if (players > 20) {

              out.println("</font></td></tr>");

              out.println("<tr><td align=\"center\">");     // new row for new group
              out.println("<font size=\"2\"><br>");

              out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player21')\" style=\"cursor:hand\">");
              out.println("21:&nbsp;&nbsp;<input type=\"text\" name=\"player21\" value=\"" + player21 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p21cw\">");
              if (!p21cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p21cw + ">" + p21cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p21cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p921 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p921\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p921\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show21 == 1) ? "checked " : "") + "name=\"show21\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom21\"" + checked[20] + " value=\"1\">");
              }

              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player22')\" style=\"cursor:hand\">");
              out.println("22:&nbsp;&nbsp;<input type=\"text\" name=\"player22\" value=\"" + player22 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p22cw\">");
              if (!p22cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p22cw + ">" + p22cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p22cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p922 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p922\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p922\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show22 == 1) ? "checked " : "") + "name=\"show22\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom22\"" + checked[21] + " value=\"1\">");
              }

              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player23')\" style=\"cursor:hand\">");
              out.println("23:&nbsp;&nbsp;<input type=\"text\" name=\"player23\" value=\"" + player23 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p23cw\">");
              if (!p23cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p23cw + ">" + p23cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p23cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p923 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p923\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p923\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show23 == 1) ? "checked " : "") + "name=\"show23\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom23\"" + checked[22] + " value=\"1\">");
              }

              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player24')\" style=\"cursor:hand\">");
              out.println("24:&nbsp;&nbsp;<input type=\"text\" name=\"player24\" value=\"" + player24 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p24cw\">");
              if (!p24cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p24cw + ">" + p24cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p24cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p924 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p924\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p924\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show24 == 1) ? "checked " : "") + "name=\"show24\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom24\"" + checked[23] + " value=\"1\">");
              }

            out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player25')\" style=\"cursor:hand\">");
            out.println("25:&nbsp;&nbsp;<input type=\"text\" id=\"player25\" name=\"player25\" value=\"" + player25 + "\" size=\"20\" maxlength=\"43\" onkeypress=\"return DYN_disableEnterKey(event)\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p25cw\" id=\"p25cw\">");
              if (!p25cw.equals( "" ) || club.equals( "cherryhills" )) {
                 out.println("<option selected value=" + p25cw + ">" + p25cw + "</option>");
              }
              for (i=0; i<16; i++) {

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p25cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              if (p925 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p925\" value=\"1\">");
              } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p925\" value=\"1\">");
              }
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show25 == 1) ? "checked " : "") + "name=\"show25\" value=\"1\">");

              if (club.equals("oaklandhills")) {   // Oakland Hills guest bag tag option

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"custom25\"" + checked[24] + " value=\"1\">");
              }


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

            out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasetext('notes')\" style=\"cursor:hand\">");
            out.println("Notes:&nbsp;<textarea name=\"notes\" value=\"\" id=\"notes\" cols=\"22\" rows=\"2\">");
            out.println("</textarea>");

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

            out.println("<br>Suppress email notification?:&nbsp;&nbsp; ");
            if (suppressEmails.equalsIgnoreCase( "yes" )) {
               out.println("<input type=\"checkbox\" checked name=\"suppressEmails\" value=\"Yes\">");
            } else {
               out.println("<input type=\"checkbox\" name=\"suppressEmails\" value=\"Yes\">");
            }
            out.println("</font><font size=\"1\">&nbsp;(checked = yes)</font><font size=\"2\">");

            if (Utilities.checkDiningLink("pro_teetime", con) && diningAccess) {
                out.println("<br>Skip dining request prompt?:&nbsp;&nbsp; ");
                if (skipDining.equalsIgnoreCase( "yes" )) {
                   out.println("<input type=\"checkbox\" checked name=\"skipDining\" value=\"Yes\">");
                } else {
                   out.println("<input type=\"checkbox\" name=\"skipDining\" value=\"Yes\">");
                }
                out.println("</font><font size=\"1\">&nbsp;(checked = yes)</font><font size=\"2\">");
            } else {
                out.println("<input type=\"hidden\" name=\"skipDining\" value=\"yes\">");
            }

         } else {     // assign = 1

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\">");  // table for member selection
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Select a Member for Each Guest</b>");
            out.println("</font></td></tr>");
            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\">");

            //
            //  Prompt the user for member names to assign to Unaccompanied Guests
            //
            out.println("<p align=\"left\">");
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Associated Member");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("Guests</b><br>");

            out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem1')\" style=\"cursor:hand\">");
            out.println("1:&nbsp;&nbsp;<input type=\"text\" name=\"mem1\" value=\"" + mem1 + "\" size=\"20\" maxlength=\"43\">");
            out.println("&nbsp;&nbsp;&nbsp;" + player1 + "&nbsp;&nbsp;&nbsp;<br>");

            if (!player2.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem2')\" style=\"cursor:hand\">");
               out.println("2:&nbsp;&nbsp;<input type=\"text\" name=\"mem2\" value=\"" + mem2 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player2 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem2\" value=\"\">");
            }
            if (!player3.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem3')\" style=\"cursor:hand\">");
               out.println("3:&nbsp;&nbsp;<input type=\"text\" name=\"mem3\" value=\"" + mem3 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player3 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem3\" value=\"\">");
            }
            if (!player4.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem4')\" style=\"cursor:hand\">");
               out.println("4:&nbsp;&nbsp;<input type=\"text\" name=\"mem4\" value=\"" + mem4 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player4 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem4\" value=\"\">");
            }
            if (!player5.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem5')\" style=\"cursor:hand\">");
               out.println("5:&nbsp;&nbsp;<input type=\"text\" name=\"mem5\" value=\"" + mem5 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player5 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem5\" value=\"\">");
            }
            if (!player6.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem6')\" style=\"cursor:hand\">");
               out.println("6:&nbsp;&nbsp;<input type=\"text\" name=\"mem6\" value=\"" + mem6 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player6 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem6\" value=\"\">");
            }
            if (!player7.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem7')\" style=\"cursor:hand\">");
               out.println("7:&nbsp;&nbsp;<input type=\"text\" name=\"mem7\" value=\"" + mem7 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player7 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem7\" value=\"\">");
            }
            if (!player8.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem8')\" style=\"cursor:hand\">");
               out.println("8:&nbsp;&nbsp;<input type=\"text\" name=\"mem8\" value=\"" + mem8 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player8 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem8\" value=\"\">");
            }
            if (!player9.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem9')\" style=\"cursor:hand\">");
               out.println("9:&nbsp;&nbsp;<input type=\"text\" name=\"mem9\" value=\"" + mem9 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player9 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem9\" value=\"\">");
            }
            if (!player10.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem10')\" style=\"cursor:hand\">");
               out.println("10:&nbsp;&nbsp;<input type=\"text\" name=\"mem10\" value=\"" + mem10 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player10 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem10\" value=\"\">");
            }
            if (!player11.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem11')\" style=\"cursor:hand\">");
               out.println("11:&nbsp;&nbsp;<input type=\"text\" name=\"mem11\" value=\"" + mem11 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player11 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem11\" value=\"\">");
            }
            if (!player12.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem12')\" style=\"cursor:hand\">");
               out.println("12:&nbsp;&nbsp;<input type=\"text\" name=\"mem12\" value=\"" + mem12 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player12 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem12\" value=\"\">");
            }
            if (!player13.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem13')\" style=\"cursor:hand\">");
               out.println("13:&nbsp;&nbsp;<input type=\"text\" name=\"mem13\" value=\"" + mem13 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player13 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem13\" value=\"\">");
            }
            if (!player14.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem14')\" style=\"cursor:hand\">");
               out.println("14:&nbsp;&nbsp;<input type=\"text\" name=\"mem14\" value=\"" + mem14 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player14 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem14\" value=\"\">");
            }
            if (!player15.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem15')\" style=\"cursor:hand\">");
               out.println("15:&nbsp;&nbsp;<input type=\"text\" name=\"mem15\" value=\"" + mem15 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player15 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem15\" value=\"\">");
            }
            if (!player16.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem16')\" style=\"cursor:hand\">");
               out.println("16:&nbsp;&nbsp;<input type=\"text\" name=\"mem16\" value=\"" + mem16 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player16 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem16\" value=\"\">");
            }
            if (!player17.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem17')\" style=\"cursor:hand\">");
               out.println("17:&nbsp;&nbsp;<input type=\"text\" name=\"mem17\" value=\"" + mem17 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player17 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem17\" value=\"\">");
            }
            if (!player18.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem18')\" style=\"cursor:hand\">");
               out.println("18:&nbsp;&nbsp;<input type=\"text\" name=\"mem18\" value=\"" + mem18 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player18 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem18\" value=\"\">");
            }
            if (!player19.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem19')\" style=\"cursor:hand\">");
               out.println("19:&nbsp;&nbsp;<input type=\"text\" name=\"mem19\" value=\"" + mem19 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player19 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem19\" value=\"\">");
            }
            if (!player20.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem20')\" style=\"cursor:hand\">");
               out.println("20:&nbsp;&nbsp;<input type=\"text\" name=\"mem20\" value=\"" + mem20 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player20 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem20\" value=\"\">");
            }
            if (!player21.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem21')\" style=\"cursor:hand\">");
               out.println("21:&nbsp;&nbsp;<input type=\"text\" name=\"mem21\" value=\"" + mem21 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player21 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem21\" value=\"\">");
            }
            if (!player22.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem22')\" style=\"cursor:hand\">");
               out.println("22:&nbsp;&nbsp;<input type=\"text\" name=\"mem22\" value=\"" + mem22 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player22 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem22\" value=\"\">");
            }
            if (!player23.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem23')\" style=\"cursor:hand\">");
               out.println("23:&nbsp;&nbsp;<input type=\"text\" name=\"mem23\" value=\"" + mem23 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player23 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem23\" value=\"\">");
            }
            if (!player24.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem24')\" style=\"cursor:hand\">");
               out.println("24:&nbsp;&nbsp;<input type=\"text\" name=\"mem24\" value=\"" + mem24 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player24 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem24\" value=\"\">");
            }
            if (!player25.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem25')\" style=\"cursor:hand\">");
               out.println("25:&nbsp;&nbsp;<input type=\"text\" name=\"mem25\" value=\"" + mem25 + "\" size=\"20\" maxlength=\"43\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player25 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem25\" value=\"\">");
            }

            out.println("</p>");

         }    // end of IF assign

         out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
         out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + sdate + "\">");
         out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
         out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
         out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stime + "\">");
         out.println("<input type=\"hidden\" name=\"time2\" value=\"" + slotParms.time2 + "\">");
         out.println("<input type=\"hidden\" name=\"time3\" value=\"" + slotParms.time3 + "\">");
         out.println("<input type=\"hidden\" name=\"time4\" value=\"" + slotParms.time4 + "\">");
         out.println("<input type=\"hidden\" name=\"time5\" value=\"" + slotParms.time5 + "\">");
         out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
         out.println("<input type=\"hidden\" name=\"mm\" value=\"" + mm + "\">");
         out.println("<input type=\"hidden\" name=\"yy\" value=\"" + yy + "\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
         out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
         out.println("<input type=\"hidden\" name=\"p5rest\" value=" + p5rest + ">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
         out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
         out.println("<input type=\"hidden\" name=\"showlott\" value=\"" + showlott + "\">");
       //  out.println("<input type=\"hidden\" name=\"conf\" value=\"" + conf + "\">");

         if (assign == 0) {
            out.println("<br><br><font size=\"1\">");
            for (i=0; i<16; i++) {
               if (!parmc.tmodea[i].equals( "" )) {
                  out.println(parmc.tmodea[i]+ " = " +parmc.tmode[i]+ "&nbsp;&nbsp;");
               }
            }
            out.println("</font><br>");

         } else {

            out.println("<input type=\"hidden\" name=\"skip\" value=\"10\">");      // skip right to assign
            out.println("<input type=\"hidden\" name=\"assign\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
            out.println("<input type=\"hidden\" name=\"player6\" value=\"" + player6 + "\">");
            out.println("<input type=\"hidden\" name=\"player7\" value=\"" + player7 + "\">");
            out.println("<input type=\"hidden\" name=\"player8\" value=\"" + player8 + "\">");
            out.println("<input type=\"hidden\" name=\"player9\" value=\"" + player9 + "\">");
            out.println("<input type=\"hidden\" name=\"player10\" value=\"" + player10 + "\">");
            out.println("<input type=\"hidden\" name=\"player11\" value=\"" + player11 + "\">");
            out.println("<input type=\"hidden\" name=\"player12\" value=\"" + player12 + "\">");
            out.println("<input type=\"hidden\" name=\"player13\" value=\"" + player13 + "\">");
            out.println("<input type=\"hidden\" name=\"player14\" value=\"" + player14 + "\">");
            out.println("<input type=\"hidden\" name=\"player15\" value=\"" + player15 + "\">");
            out.println("<input type=\"hidden\" name=\"player16\" value=\"" + player16 + "\">");
            out.println("<input type=\"hidden\" name=\"player17\" value=\"" + player17 + "\">");
            out.println("<input type=\"hidden\" name=\"player18\" value=\"" + player18 + "\">");
            out.println("<input type=\"hidden\" name=\"player19\" value=\"" + player19 + "\">");
            out.println("<input type=\"hidden\" name=\"player20\" value=\"" + player20 + "\">");
            out.println("<input type=\"hidden\" name=\"player21\" value=\"" + player21 + "\">");
            out.println("<input type=\"hidden\" name=\"player22\" value=\"" + player22 + "\">");
            out.println("<input type=\"hidden\" name=\"player23\" value=\"" + player23 + "\">");
            out.println("<input type=\"hidden\" name=\"player24\" value=\"" + player24 + "\">");
            out.println("<input type=\"hidden\" name=\"player25\" value=\"" + player25 + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
            out.println("<input type=\"hidden\" name=\"p6cw\" value=\"" + p6cw + "\">");
            out.println("<input type=\"hidden\" name=\"p7cw\" value=\"" + p7cw + "\">");
            out.println("<input type=\"hidden\" name=\"p8cw\" value=\"" + p8cw + "\">");
            out.println("<input type=\"hidden\" name=\"p9cw\" value=\"" + p9cw + "\">");
            out.println("<input type=\"hidden\" name=\"p10cw\" value=\"" + p10cw + "\">");
            out.println("<input type=\"hidden\" name=\"p11cw\" value=\"" + p11cw + "\">");
            out.println("<input type=\"hidden\" name=\"p12cw\" value=\"" + p12cw + "\">");
            out.println("<input type=\"hidden\" name=\"p13cw\" value=\"" + p13cw + "\">");
            out.println("<input type=\"hidden\" name=\"p14cw\" value=\"" + p14cw + "\">");
            out.println("<input type=\"hidden\" name=\"p15cw\" value=\"" + p15cw + "\">");
            out.println("<input type=\"hidden\" name=\"p16cw\" value=\"" + p16cw + "\">");
            out.println("<input type=\"hidden\" name=\"p17cw\" value=\"" + p17cw + "\">");
            out.println("<input type=\"hidden\" name=\"p18cw\" value=\"" + p18cw + "\">");
            out.println("<input type=\"hidden\" name=\"p19cw\" value=\"" + p19cw + "\">");
            out.println("<input type=\"hidden\" name=\"p20cw\" value=\"" + p20cw + "\">");
            out.println("<input type=\"hidden\" name=\"p21cw\" value=\"" + p21cw + "\">");
            out.println("<input type=\"hidden\" name=\"p22cw\" value=\"" + p22cw + "\">");
            out.println("<input type=\"hidden\" name=\"p23cw\" value=\"" + p23cw + "\">");
            out.println("<input type=\"hidden\" name=\"p24cw\" value=\"" + p24cw + "\">");
            out.println("<input type=\"hidden\" name=\"p25cw\" value=\"" + p25cw + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + p95 + "\">");
            out.println("<input type=\"hidden\" name=\"p96\" value=\"" + p96 + "\">");
            out.println("<input type=\"hidden\" name=\"p97\" value=\"" + p97 + "\">");
            out.println("<input type=\"hidden\" name=\"p98\" value=\"" + p98 + "\">");
            out.println("<input type=\"hidden\" name=\"p99\" value=\"" + p99 + "\">");
            out.println("<input type=\"hidden\" name=\"p910\" value=\"" + p910 + "\">");
            out.println("<input type=\"hidden\" name=\"p911\" value=\"" + p911 + "\">");
            out.println("<input type=\"hidden\" name=\"p912\" value=\"" + p912 + "\">");
            out.println("<input type=\"hidden\" name=\"p913\" value=\"" + p913 + "\">");
            out.println("<input type=\"hidden\" name=\"p914\" value=\"" + p914 + "\">");
            out.println("<input type=\"hidden\" name=\"p915\" value=\"" + p915 + "\">");
            out.println("<input type=\"hidden\" name=\"p916\" value=\"" + p916 + "\">");
            out.println("<input type=\"hidden\" name=\"p917\" value=\"" + p917 + "\">");
            out.println("<input type=\"hidden\" name=\"p918\" value=\"" + p918 + "\">");
            out.println("<input type=\"hidden\" name=\"p919\" value=\"" + p919 + "\">");
            out.println("<input type=\"hidden\" name=\"p920\" value=\"" + p920 + "\">");
            out.println("<input type=\"hidden\" name=\"p921\" value=\"" + p921 + "\">");
            out.println("<input type=\"hidden\" name=\"p922\" value=\"" + p922 + "\">");
            out.println("<input type=\"hidden\" name=\"p923\" value=\"" + p923 + "\">");
            out.println("<input type=\"hidden\" name=\"p924\" value=\"" + p924 + "\">");
            out.println("<input type=\"hidden\" name=\"p925\" value=\"" + p925 + "\">");
            out.println("<input type=\"hidden\" name=\"show1\" value=\"" + show1 + "\">");
            out.println("<input type=\"hidden\" name=\"show2\" value=\"" + show2 + "\">");
            out.println("<input type=\"hidden\" name=\"show3\" value=\"" + show3 + "\">");
            out.println("<input type=\"hidden\" name=\"show4\" value=\"" + show4 + "\">");
            out.println("<input type=\"hidden\" name=\"show5\" value=\"" + show5 + "\">");
            out.println("<input type=\"hidden\" name=\"show6\" value=\"" + show6 + "\">");
            out.println("<input type=\"hidden\" name=\"show7\" value=\"" + show7 + "\">");
            out.println("<input type=\"hidden\" name=\"show8\" value=\"" + show8 + "\">");
            out.println("<input type=\"hidden\" name=\"show9\" value=\"" + show9 + "\">");
            out.println("<input type=\"hidden\" name=\"show10\" value=\"" + show10 + "\">");
            out.println("<input type=\"hidden\" name=\"show11\" value=\"" + show11 + "\">");
            out.println("<input type=\"hidden\" name=\"show12\" value=\"" + show12 + "\">");
            out.println("<input type=\"hidden\" name=\"show13\" value=\"" + show13 + "\">");
            out.println("<input type=\"hidden\" name=\"show14\" value=\"" + show14 + "\">");
            out.println("<input type=\"hidden\" name=\"show15\" value=\"" + show15 + "\">");
            out.println("<input type=\"hidden\" name=\"show16\" value=\"" + show16 + "\">");
            out.println("<input type=\"hidden\" name=\"show17\" value=\"" + show17 + "\">");
            out.println("<input type=\"hidden\" name=\"show18\" value=\"" + show18 + "\">");
            out.println("<input type=\"hidden\" name=\"show19\" value=\"" + show19 + "\">");
            out.println("<input type=\"hidden\" name=\"show20\" value=\"" + show20 + "\">");
            out.println("<input type=\"hidden\" name=\"show21\" value=\"" + show21 + "\">");
            out.println("<input type=\"hidden\" name=\"show22\" value=\"" + show22 + "\">");
            out.println("<input type=\"hidden\" name=\"show23\" value=\"" + show23 + "\">");
            out.println("<input type=\"hidden\" name=\"show24\" value=\"" + show24 + "\">");
            out.println("<input type=\"hidden\" name=\"show25\" value=\"" + show25 + "\">");
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
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
            if (hide != 0) {
               out.println("<input type=\"hidden\" name=\"hide\" value=\"Yes\">");
            } else {
               out.println("<input type=\"hidden\" name=\"hide\" value=\"No\">");
            }
            if (suppressEmails.equalsIgnoreCase( "yes" )) {
               out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"Yes\">");
            } else {
               out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"No\">");
            }
            if (skipDining.equalsIgnoreCase( "yes" )) {
               out.println("<input type=\"hidden\" name=\"skipDining\" value=\"Yes\">");
            } else {
               out.println("<input type=\"hidden\" name=\"skipDining\" value=\"No\">");
            }
         }
         out.println("<input type=submit value=\"Submit\" name=\"submitForm\"></input>");
         out.println("</font></td></tr>");
         out.println("</table>");
      out.println("</td>");
      out.println("<td valign=\"top\">");

      // ********************************************************************************
      //   Always build the name list.
      // ********************************************************************************
      String letter = "";

      if (req.getParameter("letter") == null) {     // if no letter, then must be first call

         letter = "%";                              // default to List All

      } else {

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
      alphaTable.nameList(club, letter, mshipOpt, mtypeOpt, false, parmc, enableAdvAssist, out, con);


      out.println("</td><td valign=\"top\">");         // new column


      //
      //   Output the Alphabit Table for Members' Last Names
      //
      alphaTable.getTable(out, user);


      //
      //   Output the Mship and Mtype Options
      //
      alphaTable.typeOptions(club, mshipOpt, mtypeOpt, out, con);


      if (assign == 0) {           // if normal req

         //
         //   Output the List of Guests
         //
         alphaTable.guestList(club, course, day_name, time, parm, false, false, 0, enableAdvAssist, out, con);

      }      // end of IF assign

      out.println("</td>");        // end of this main column
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
 //  Process reservation request from Proshop_slotm (HTML)
 // *********************************************************

 private void verify(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


   ResultSet rs = null;

   //
   //  Get this session's user name
   //
   String user = "";
   String club = "";
   String posType = "";
   user = (String)session.getAttribute("user");
   club = (String)session.getAttribute("club");
   posType = (String)session.getAttribute("posType");

   int players = 0;
   int slots = 0;
   //int reject = 0;
   //int count = 0;
   int time = 0;
   //int time2 = 0;
   //int hr = 0;
   //int min = 0;
   int dd = 0;
   int mm = 0;
   int yy = 0;
   int calYear = 0;
   int calMonth = 0;
   int thisMonth = 0;
   int calDay = 0;
   int grest_num = 0;
   int fb = 0;
   //int fb2 = 0;
   //int t_fb = 0;
   //int members = 0;
   //int minMembers = 0;
   //int minPlayers = 0;
   //int proNew = 0;
   //int proMod = 0;
   //int memNew = 0;
   //int memMod = 0;
   int i = 0;
   int i2 = 0;
   //int mtimes = 0;
   //int year = 0;
   //int month = 0;
   //int dayNum = 0;
   int temp = 0;
   //int sendEmail = 0;
   //int emailNew = 0;
   //int emailMod = 0;
   //int emailCan = 0;
   //int mems = 0;
   //int rest_stime = 0;
   //int rest_etime = 0;
   //int mins_before = 0;
   //int mins_after = 0;
   int in_use = 0;
   int hide = 0;
   int gi = 0;
   int ind = 0;
   int skip = 0;
   int skipValue = 0;
   int msgPlayerCount = 0;
/*
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
   int inval25 = 0;*/

   long date = 0;
   //long dateStart = 0;
   //long dateEnd = 0;
   long todayDate = 0;

   String player = "";
   //String gplayer = "";
   //String mship = "";
   //String mtype = "";
   String rest_name = "";
   //String rest_recurr = "";
   String grest_recurr = "";
   String day = "";
   String in_use_by = "";
   String err_name = "";
   String sfb = "";
   //String sfb2 = "";
   String rest_fb = "";
   //String rest_course = "";
   String notes = "";
   //String notes2 = "";
   String rcourse = "";
   //String period = "";
   //String mperiod = "";
   //String course2 = "";
   String returnCourse = "";
   String msgDate = "";
   String suppressEmails = "no";
   String skipDining = "no";
   String memberName = "";
   String orig_by = user;
   String p9s = "";
   String temps = "";
   String errorMsg = "";
   String skips = "";

   boolean hit = false;
   boolean check = false;
   boolean error = false;
   boolean guestError = false;

   // Check proshop user feature access for appropriate access rights
   boolean diningAccess = SystemUtils.verifyProAccess(req, "DINING_REQUEST", con, out);
   boolean overrideAccess = SystemUtils.verifyProAccess(req, "REST_OVERRIDE", con, out);

   String [] userg = new String [25];     // and user guest names
   //String [] rguest = new String [36];    // array to hold the Guest Restriction guest names
   ArrayList<String> rguest = new ArrayList<String>();

   //
   //  Arrays to hold member & guest names to tie guests to members (gstA[] resides in parmSlotm)
   //
   String [] memA = new String [25];     // members
   String [] usergA = new String [25];   // guests' associated member (username)

   //
   //  Arrays to hold guest bag tag indicators for Oakland Hills
   //
   String [] gbt = new String [25];

   //
   //  parm block to hold the club parameters
   //
   parmClub clubParm = new parmClub(0, con); // golf only

   //
   //  parm block to hold verify's parms
   //
   parmSlotm parm = new parmSlotm();          // allocate a parm block

   parm.club = club;                          // save club name

   for (i = 0; i < 25; i++) {           // init the arrays

      userg[i] = "";
      parm.userg[i] = "";
      usergA[i] = "";
      parm.memA[i] = "";
   }

   //
   // Get all the parameters entered
   //
   String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
   String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
   String smm = req.getParameter("mm");               //  month of tee time
   String syy = req.getParameter("yy");               //  year of tee time
   String index = req.getParameter("index");          //  day index value (needed by _sheet on return)
   String p5 = req.getParameter("p5");                //  5-somes supported for this slot
   String p5rest = req.getParameter("p5rest");        //  5-somes restricted for this slot
   String course = req.getParameter("course");        //  name of course
   String sslots = req.getParameter("slots");         //  # of groups allowed for the Lottery

   day = req.getParameter("day");                     // name of day
   sfb = req.getParameter("fb");                      // Front/Back indicator
   notes = req.getParameter("notes").trim();          // Member Notes
   String hides = req.getParameter("hide");           // Hide Notes Indicator
   String jump = req.getParameter("jump");            // jump index for _sheet
//   parm.conf = req.getParameter("conf");            // confirmation # (or Id) for Hotels
   parm.conf = "";                                    // temp - confirmation # (or Id) for Hotels

   if (req.getParameter("suppressEmails") != null) {             // if email parm exists
      suppressEmails = req.getParameter("suppressEmails");
   }
   if (req.getParameter("returnCourse") != null) {
      returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
   }
   if (req.getParameter("skipDining") != null) {
      skipDining = "yes";
   }

   String showlott = "";
   if (req.getParameter("showlott") != null) {        // if time during a lottery

      showlott = req.getParameter("showlott");        // get the value
   }


   //
   //  Put all the parms in a parm block
   //
   parm.player1 = req.getParameter("player1");
   parm.player2 = req.getParameter("player2");
   parm.player3 = req.getParameter("player3");
   parm.player4 = req.getParameter("player4");
   if (req.getParameter("player5") != null) {
      parm.player5 = req.getParameter("player5");
   }
   if (req.getParameter("player6") != null) {
      parm.player6 = req.getParameter("player6");
   }
   if (req.getParameter("player7") != null) {
      parm.player7 = req.getParameter("player7");
   }
   if (req.getParameter("player8") != null) {
      parm.player8 = req.getParameter("player8");
   }
   if (req.getParameter("player9") != null) {
      parm.player9 = req.getParameter("player9");
   }
   if (req.getParameter("player10") != null) {
      parm.player10 = req.getParameter("player10");
   }
   if (req.getParameter("player11") != null) {
      parm.player11 = req.getParameter("player11");
   }
   if (req.getParameter("player12") != null) {
      parm.player12 = req.getParameter("player12");
   }
   if (req.getParameter("player13") != null) {
      parm.player13 = req.getParameter("player13");
   }
   if (req.getParameter("player14") != null) {
      parm.player14 = req.getParameter("player14");
   }
   if (req.getParameter("player15") != null) {
      parm.player15 = req.getParameter("player15");
   }
   if (req.getParameter("player16") != null) {
      parm.player16 = req.getParameter("player16");
   }
   if (req.getParameter("player17") != null) {
      parm.player17 = req.getParameter("player17");
   }
   if (req.getParameter("player18") != null) {
      parm.player18 = req.getParameter("player18");
   }
   if (req.getParameter("player19") != null) {
      parm.player19 = req.getParameter("player19");
   }
   if (req.getParameter("player20") != null) {
      parm.player20 = req.getParameter("player20");
   }
   if (req.getParameter("player21") != null) {
      parm.player21 = req.getParameter("player21");
   }
   if (req.getParameter("player22") != null) {
      parm.player22 = req.getParameter("player22");
   }
   if (req.getParameter("player23") != null) {
      parm.player23 = req.getParameter("player23");
   }
   if (req.getParameter("player24") != null) {
      parm.player24 = req.getParameter("player24");
   }
   if (req.getParameter("player25") != null) {
      parm.player25 = req.getParameter("player25");
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

   if (req.getParameter("mem1") != null) {
      parm.memA[0] = req.getParameter("mem1");
   }
   if (req.getParameter("mem2") != null) {
      parm.memA[1] = req.getParameter("mem2");
   }
   if (req.getParameter("mem3") != null) {
      parm.memA[2] = req.getParameter("mem3");
   }
   if (req.getParameter("mem4") != null) {
      parm.memA[3] = req.getParameter("mem4");
   }
   if (req.getParameter("mem5") != null) {
      parm.memA[4] = req.getParameter("mem5");
   }
   if (req.getParameter("mem6") != null) {
      parm.memA[5] = req.getParameter("mem6");
   }
   if (req.getParameter("mem7") != null) {
      parm.memA[6] = req.getParameter("mem7");
   }
   if (req.getParameter("mem8") != null) {
      parm.memA[7] = req.getParameter("mem8");
   }
   if (req.getParameter("mem9") != null) {
      parm.memA[8] = req.getParameter("mem9");
   }
   if (req.getParameter("mem10") != null) {
      parm.memA[9] = req.getParameter("mem10");
   }
   if (req.getParameter("mem11") != null) {
      parm.memA[10] = req.getParameter("mem11");
   }
   if (req.getParameter("mem12") != null) {
      parm.memA[11] = req.getParameter("mem12");
   }
   if (req.getParameter("mem13") != null) {
      parm.memA[12] = req.getParameter("mem13");
   }
   if (req.getParameter("mem14") != null) {
      parm.memA[13] = req.getParameter("mem14");
   }
   if (req.getParameter("mem15") != null) {
      parm.memA[14] = req.getParameter("mem15");
   }
   if (req.getParameter("mem16") != null) {
      parm.memA[15] = req.getParameter("mem16");
   }
   if (req.getParameter("mem17") != null) {
      parm.memA[16] = req.getParameter("mem17");
   }
   if (req.getParameter("mem18") != null) {
      parm.memA[17] = req.getParameter("mem18");
   }
   if (req.getParameter("mem19") != null) {
      parm.memA[18] = req.getParameter("mem19");
   }
   if (req.getParameter("mem20") != null) {
      parm.memA[19] = req.getParameter("mem20");
   }
   if (req.getParameter("mem21") != null) {
      parm.memA[20] = req.getParameter("mem21");
   }
   if (req.getParameter("mem22") != null) {
      parm.memA[21] = req.getParameter("mem22");
   }
   if (req.getParameter("mem23") != null) {
      parm.memA[22] = req.getParameter("mem23");
   }
   if (req.getParameter("mem24") != null) {
      parm.memA[23] = req.getParameter("mem24");
   }
   if (req.getParameter("mem25") != null) {
      parm.memA[24] = req.getParameter("mem25");
   }

   parm.show1 = (req.getParameter("show1") != null) ? Short.parseShort(req.getParameter("show1")) : 0;
   parm.show2 = (req.getParameter("show2") != null) ? Short.parseShort(req.getParameter("show2")) : 0;
   parm.show3 = (req.getParameter("show3") != null) ? Short.parseShort(req.getParameter("show3")) : 0;
   parm.show4 = (req.getParameter("show4") != null) ? Short.parseShort(req.getParameter("show4")) : 0;
   parm.show5 = (req.getParameter("show5") != null) ? Short.parseShort(req.getParameter("show5")) : 0;
   parm.show6 = (req.getParameter("show6") != null) ? Short.parseShort(req.getParameter("show6")) : 0;
   parm.show7 = (req.getParameter("show7") != null) ? Short.parseShort(req.getParameter("show7")) : 0;
   parm.show8 = (req.getParameter("show8") != null) ? Short.parseShort(req.getParameter("show8")) : 0;
   parm.show9 = (req.getParameter("show9") != null) ? Short.parseShort(req.getParameter("show9")) : 0;
   parm.show10 = (req.getParameter("show10") != null) ? Short.parseShort(req.getParameter("show10")) : 0;
   parm.show11 = (req.getParameter("show11") != null) ? Short.parseShort(req.getParameter("show11")) : 0;
   parm.show12 = (req.getParameter("show12") != null) ? Short.parseShort(req.getParameter("show12")) : 0;
   parm.show13 = (req.getParameter("show13") != null) ? Short.parseShort(req.getParameter("show13")) : 0;
   parm.show14 = (req.getParameter("show14") != null) ? Short.parseShort(req.getParameter("show14")) : 0;
   parm.show15 = (req.getParameter("show15") != null) ? Short.parseShort(req.getParameter("show15")) : 0;
   parm.show16 = (req.getParameter("show16") != null) ? Short.parseShort(req.getParameter("show16")) : 0;
   parm.show17 = (req.getParameter("show17") != null) ? Short.parseShort(req.getParameter("show17")) : 0;
   parm.show18 = (req.getParameter("show18") != null) ? Short.parseShort(req.getParameter("show18")) : 0;
   parm.show19 = (req.getParameter("show19") != null) ? Short.parseShort(req.getParameter("show19")) : 0;
   parm.show20 = (req.getParameter("show20") != null) ? Short.parseShort(req.getParameter("show20")) : 0;
   parm.show21 = (req.getParameter("show21") != null) ? Short.parseShort(req.getParameter("show21")) : 0;
   parm.show22 = (req.getParameter("show22") != null) ? Short.parseShort(req.getParameter("show22")) : 0;
   parm.show23 = (req.getParameter("show23") != null) ? Short.parseShort(req.getParameter("show23")) : 0;
   parm.show24 = (req.getParameter("show24") != null) ? Short.parseShort(req.getParameter("show24")) : 0;
   parm.show25 = (req.getParameter("show25") != null) ? Short.parseShort(req.getParameter("show25")) : 0;

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
   //  Gather guest bag tag parameters if Oakland Hills
   //
   if (club.equals("oaklandhills")) {

       for (int k=0; k<25; k++) {
           if (req.getParameter("custom" + String.valueOf(k + 1)) != null && req.getParameter("custom" + String.valueOf(k + 1)).equals("1")) {
               gbt[k] = req.getParameter("custom" + String.valueOf(k + 1));
           } else {
               gbt[k] = "";
           }
       }
   }

   //
   //  init and get the other tee times
   //
   parm.time2 = 0;
   parm.time3 = 0;
   parm.time4 = 0;
   parm.time5 = 0;

   if (req.getParameter("time2") != null) {
      temps = req.getParameter("time2");
      parm.time2 = Integer.parseInt(temps);
   }
   if (req.getParameter("time3") != null) {
      temps = req.getParameter("time3");
      parm.time3 = Integer.parseInt(temps);
   }
   if (req.getParameter("time4") != null) {
      temps = req.getParameter("time4");
      parm.time4 = Integer.parseInt(temps);
   }
   if (req.getParameter("time5") != null) {
      temps = req.getParameter("time5");
      parm.time5 = Integer.parseInt(temps);
   }

   //
   //  Get skip parm if provided
   //
   if (req.getParameter("skip") != null) {

      skips = req.getParameter("skip");
      skip = Integer.parseInt(skips);
   }

   //
   //  Convert the string values to ints
   //
   try {
      slots = Integer.parseInt(sslots);
      time = Integer.parseInt(stime);
      mm = Integer.parseInt(smm);
      yy = Integer.parseInt(syy);
      fb = Integer.parseInt(sfb);
      date = Long.parseLong(sdate);
   }
   catch (NumberFormatException e) {
      SystemUtils.logError("NumberFormat Error in Proshop_slotm: " + e);
   }


   if (!course.equals( "" )) {
      //
      //  Scan name for special characters and replace with HTML supported chars (i.e. '>' = &gt)
      //
      course = SystemUtils.filter(course);
   }

   //
   //  convert the index value from string to numeric - save both
   //
   String num = index;

   if (num.startsWith( "i" )) {

      StringTokenizer tok = new StringTokenizer( num, "i" );     // space is the default token - use 'i'

      num = tok.nextToken();      // get just the index number
   }

   try {
      ind = Integer.parseInt(num);
   } catch (NumberFormatException e) { }

   //
   //  Save some of the parms in the parm table
   //
   parm.date = date;
   parm.time1 = time;
   parm.fb = fb;
   parm.mm = mm;
   parm.dd = dd;
   parm.yy = yy;
   parm.day = day;
   parm.course = course;
   parm.returnCourse = returnCourse;
   parm.jump = jump;
   parm.index = index;
   parm.ind = ind;
   parm.p5 = p5;
   parm.p5rest = p5rest;
   parm.notes = notes;
   parm.hides = hides;
   parm.suppressEmails = suppressEmails;
   parm.skipDining = skipDining;
   parm.showlott = showlott;

   //
   //  Init other parm fields
   //
   parm.custom_disp1 = "";
   parm.custom_disp2 = "";
   parm.custom_disp3 = "";
   parm.custom_disp4 = "";
   parm.custom_disp5 = "";
   parm.custom_disp6 = "";
   parm.custom_disp7 = "";
   parm.custom_disp8 = "";
   parm.custom_disp9 = "";
   parm.custom_disp10 = "";
   parm.custom_disp11 = "";
   parm.custom_disp12 = "";
   parm.custom_disp13 = "";
   parm.custom_disp14 = "";
   parm.custom_disp15 = "";
   parm.custom_disp16 = "";
   parm.custom_disp17 = "";
   parm.custom_disp18 = "";
   parm.custom_disp19 = "";
   parm.custom_disp20 = "";
   parm.custom_disp21 = "";
   parm.custom_disp22 = "";
   parm.custom_disp23 = "";
   parm.custom_disp24 = "";
   parm.custom_disp25 = "";


   //
   //  Get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   calYear = cal.get(Calendar.YEAR);
   calMonth = cal.get(Calendar.MONTH) +1;
   calDay = cal.get(Calendar.DAY_OF_MONTH);

   thisMonth = calMonth;                          // save this month

   todayDate = calYear * 10000;                      // create a date field of yyyymmdd
   todayDate = todayDate + (calMonth * 100);
   todayDate = todayDate + calDay;                    // date = yyyymmdd (for comparisons)


   try {

      clubParm.club = club;                   // set club name
      clubParm.course = course;               // and course name

      getClub.getParms(con, clubParm);        // get the club parms

   }
   catch (Exception ignore) {
   }

   // set the precheckin value in parmSlotm to 1 if we are using the feature for this teetime
   parm.precheckin = (clubParm.precheckin == 1 && index.equals("0")) ? 1 : 0;


   //
   //  Custom changes for Pre-checkin - make sure the time is adjusted for time zone!!!!!!!!!!!
   //

   // Custom for Imperial GC - Utilize pre-checkin for tomorrow bookings if it's after 1pm ET today - Case# 1327
   if (club.equals("imperialgc") && index.equals("1")) {

      GregorianCalendar cal_pci = new GregorianCalendar();
      if (cal_pci.get(Calendar.HOUR_OF_DAY) >= 12) {

          parm.precheckin = 1;
      }
   }


   //
   //  Check and populate guest bag tag custom displays for Oakland Hills
   //
   if (club.equals("oaklandhills")) {

       parm.custom_disp1 = gbt[0];
       parm.custom_disp2 = gbt[1];
       parm.custom_disp3 = gbt[2];
       parm.custom_disp4 = gbt[3];
       parm.custom_disp5 = gbt[4];
       parm.custom_disp6 = gbt[5];
       parm.custom_disp7 = gbt[6];
       parm.custom_disp8 = gbt[7];
       parm.custom_disp9 = gbt[8];
       parm.custom_disp10 = gbt[9];
       parm.custom_disp11 = gbt[10];
       parm.custom_disp12 = gbt[11];
       parm.custom_disp13 = gbt[12];
       parm.custom_disp14 = gbt[13];
       parm.custom_disp15 = gbt[14];
       parm.custom_disp16 = gbt[15];
       parm.custom_disp17 = gbt[16];
       parm.custom_disp18 = gbt[17];
       parm.custom_disp19 = gbt[18];
       parm.custom_disp20 = gbt[19];
       parm.custom_disp21 = gbt[20];
       parm.custom_disp22 = gbt[21];
       parm.custom_disp23 = gbt[22];
       parm.custom_disp24 = gbt[23];
       parm.custom_disp25 = gbt[24];
   }


   //
   //  See if user wants to hide any notes from the Members
   //
   hide = 0;      // init

   if (hides.equals( "Yes" )) {

      hide = 1;
   }

   parm.hides = "" + hide;
   parm.notes = notes;

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

   //
   //  Check if this request is still 'in use' and still in use by this user??
   //
   checkInUseBy(con, parm);

   in_use = parm.in_use;
   in_use_by = parm.in_use_by;
   parm.orig_by = orig_by;          // this is new tee time, so set this user as originator

   if ((in_use == 0) || (!in_use_by.equalsIgnoreCase( user ))) {    // if time slot in use and not by this user

      out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>Reservation Timer Expired</H3>");
      out.println("<BR><BR>Sorry, but this request has been returned to the system.<BR>");
      out.println("<BR>The system timed out and released the request.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
      if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
      } else {
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      }
      out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
      out.println("<input type=\"hidden\" name=\"showlott\" value=" + showlott + ">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  determine # of groups and # of players requested
   //
   parm.slots = slots;
   i = 0;

   if (!parm.player1.equals( "" )) {
      i++;
   }
   if (!parm.player2.equals( "" )) {
      i++;
   }
   if (!parm.player3.equals( "" )) {
      i++;
   }
   if (!parm.player4.equals( "" )) {
      i++;
   }
   if (!parm.player5.equals( "" )) {
      i++;
   }
   if (!parm.player6.equals( "" )) {
      i++;
   }
   if (!parm.player7.equals( "" )) {
      i++;
   }
   if (!parm.player8.equals( "" )) {
      i++;
   }
   if (!parm.player9.equals( "" )) {
      i++;
   }
   if (!parm.player10.equals( "" )) {
      i++;
   }
   if (!parm.player11.equals( "" )) {
      i++;
   }
   if (!parm.player12.equals( "" )) {
      i++;
   }
   if (!parm.player13.equals( "" )) {
      i++;
   }
   if (!parm.player14.equals( "" )) {
      i++;
   }
   if (!parm.player15.equals( "" )) {
      i++;
   }
   if (!parm.player16.equals( "" )) {
      i++;
   }
   if (!parm.player17.equals( "" )) {
      i++;
   }
   if (!parm.player18.equals( "" )) {
      i++;
   }
   if (!parm.player19.equals( "" )) {
      i++;
   }
   if (!parm.player20.equals( "" )) {
      i++;
   }
   if (!parm.player21.equals( "" )) {
      i++;
   }
   if (!parm.player22.equals( "" )) {
      i++;
   }
   if (!parm.player23.equals( "" )) {
      i++;
   }
   if (!parm.player24.equals( "" )) {
      i++;
   }
   if (!parm.player25.equals( "" )) {
      i++;
   }

   parm.players = i;
   players = i;             // save count for request

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

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");
      out.println("<BR><BR><H3>Data Entry Error</H3>");
      out.println("<BR><BR>Required field has not been completed or is invalid.");
      out.println("<BR><BR>At least 1 Player field must contain a valid entry.");
      out.println("<BR><BR>");
      //
      //  Return to _lott
      //
      goReturnPrompt(out, parm);
      return;
   }

   //
   //  Make sure at least 1 player in the first 4 contains a name
   //
   if ((parm.player1.equals( "" )) && (parm.player2.equals( "" )) && (parm.player3.equals( "" )) && (parm.player4.equals( "" ))) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");
      out.println("<BR><BR><H3>Data Entry Error</H3>");
      out.println("<BR><BR>Required field has not been completed or is invalid.");
      out.println("<BR><BR>At least 1 of the first 4 Player fields must contain a valid entry.");
      out.println("<BR><BR>");
      //
      //  Return to _lott
      //
      goReturnPrompt(out, parm);
      return;
   }

   //
   //  At least 1 Player field is present - Make sure a C/W was specified for all players
   //
   if ((!parm.player1.equals( "" ) && !parm.player1.equalsIgnoreCase( "x" ) && parm.pcw1.equals( "" )) ||
       (!parm.player2.equals( "" ) && !parm.player2.equalsIgnoreCase( "x" ) && parm.pcw2.equals( "" )) ||
       (!parm.player3.equals( "" ) && !parm.player3.equalsIgnoreCase( "x" ) && parm.pcw3.equals( "" )) ||
       (!parm.player4.equals( "" ) && !parm.player4.equalsIgnoreCase( "x" ) && parm.pcw4.equals( "" )) ||
       (!parm.player5.equals( "" ) && !parm.player5.equalsIgnoreCase( "x" ) && parm.pcw5.equals( "" )) ||
       (!parm.player6.equals( "" ) && !parm.player6.equalsIgnoreCase( "x" ) && parm.pcw6.equals( "" )) ||
       (!parm.player7.equals( "" ) && !parm.player7.equalsIgnoreCase( "x" ) && parm.pcw7.equals( "" )) ||
       (!parm.player8.equals( "" ) && !parm.player8.equalsIgnoreCase( "x" ) && parm.pcw8.equals( "" )) ||
       (!parm.player9.equals( "" ) && !parm.player9.equalsIgnoreCase( "x" ) && parm.pcw9.equals( "" )) ||
       (!parm.player10.equals( "" ) && !parm.player10.equalsIgnoreCase( "x" ) && parm.pcw10.equals( "" )) ||
       (!parm.player11.equals( "" ) && !parm.player11.equalsIgnoreCase( "x" ) && parm.pcw11.equals( "" )) ||
       (!parm.player12.equals( "" ) && !parm.player12.equalsIgnoreCase( "x" ) && parm.pcw12.equals( "" )) ||
       (!parm.player13.equals( "" ) && !parm.player13.equalsIgnoreCase( "x" ) && parm.pcw13.equals( "" )) ||
       (!parm.player14.equals( "" ) && !parm.player14.equalsIgnoreCase( "x" ) && parm.pcw14.equals( "" )) ||
       (!parm.player15.equals( "" ) && !parm.player15.equalsIgnoreCase( "x" ) && parm.pcw15.equals( "" )) ||
       (!parm.player16.equals( "" ) && !parm.player16.equalsIgnoreCase( "x" ) && parm.pcw16.equals( "" )) ||
       (!parm.player17.equals( "" ) && !parm.player17.equalsIgnoreCase( "x" ) && parm.pcw17.equals( "" )) ||
       (!parm.player18.equals( "" ) && !parm.player18.equalsIgnoreCase( "x" ) && parm.pcw18.equals( "" )) ||
       (!parm.player19.equals( "" ) && !parm.player19.equalsIgnoreCase( "x" ) && parm.pcw19.equals( "" )) ||
       (!parm.player20.equals( "" ) && !parm.player20.equalsIgnoreCase( "x" ) && parm.pcw20.equals( "" )) ||
       (!parm.player21.equals( "" ) && !parm.player21.equalsIgnoreCase( "x" ) && parm.pcw21.equals( "" )) ||
       (!parm.player22.equals( "" ) && !parm.player22.equalsIgnoreCase( "x" ) && parm.pcw22.equals( "" )) ||
       (!parm.player23.equals( "" ) && !parm.player23.equalsIgnoreCase( "x" ) && parm.pcw23.equals( "" )) ||
       (!parm.player24.equals( "" ) && !parm.player24.equalsIgnoreCase( "x" ) && parm.pcw24.equals( "" )) ||
       (!parm.player25.equals( "" ) && !parm.player25.equalsIgnoreCase( "x" ) && parm.pcw25.equals( "" ))) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");
      out.println("<BR><BR><H3>Data Entry Error</H3>");
      out.println("<BR><BR>Required field has not been completed or is invalid.");
      out.println("<BR><BR>You must specify a Mode of Transportation option for all players.");
      out.println("<BR><BR>");
      //
      //  Return to _lott
      //
      goReturnPrompt(out, parm);
      return;
   }

   //
   //  parm block to hold the club parameters
   //
   parmClub parm2 = new parmClub(0, con);  // golf only

   //
   //   Get the guest names specified for this club
   //
   try {
      parm2.club = club;                   // set club name
      parm2.course = course;               // and course name

      getClub.getParms(con, parm2);        // get the club parms

   }
   catch (Exception ignore) {
   }

   //
   //  Make sure there are no duplicate names.
   //  Also, Parse the names to separate first, last & mi
   //  (Member does not verify single tokens - check for guest)
   //
   String perror = parseNames(out, parm, parm2, con);               // process members and guests

   if (perror.equals("dupData")) {
       perror = "";
       parm.player = "";
   }

   if (!perror.equals( "" )) {

      if (perror.equals( "dupData" )) {

          out.println(SystemUtils.HeadTitle("Data Entry Error"));
          out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
          out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
          out.println("<center>");
          out.println("<BR><BR><H3>Data Entry Error</H3>");
          out.println("<BR><BR><b>" + parm.player + "</b> was specified more than once.");
          out.println("<BR><BR>Please correct this and try again.");
          out.println("<BR><BR>");

      } else if (perror.equals( "invData" )) {

          out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
          out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
          out.println("<hr width=\"40%\">");
          out.println("<BR><H3>Invalid Data Received</H3><BR>");
          out.println("<BR><BR>Sorry, a name you entered (<b>" + parm.player + "</b>) is not valid.<BR>");
          out.println("Please check the names and try again.");
          out.println("<BR><BR>");

      } else if (perror.equals("invGuest")) {

          out.println(SystemUtils.HeadTitle("Data Entry Error"));
          out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
          out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
          out.println("<center>");
          out.println("<BR><BR><H3>Data Entry Error</H3>");
          out.println("<BR><BR><b>" + parm.gplayer + "</b> appears to have been manually enetered or " +
                  "<br>modified after selecting a different guest from the Guest Selection window.");
          out.println("<BR><BR>Since this guest type uses the Guest Tracking feature, please click 'erase' ");
          out.println("<BR>next to the current guest's name, then click the desired guest type from the Guest ");
          out.println("<BR>Types list, and finally select a guest from the displayed guest selection window.<br>");

      } else {       // db error

          out.println(SystemUtils.HeadTitle("DB Error"));
          out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
          out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
          out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
          out.println("<BR><BR>Unable to access the Database.");
          out.println("<BR>Please try again later.");
          out.println("<BR><BR>If problem persists, please contact customer support.");
          out.println("<BR><BR>" + perror);
          out.println("<BR><BR>");
      }

      goReturnPrompt(out, parm);
      return;           // exit if error encountered and reported
   }

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
   //  Check if proshop user requested that we skip the following name test.
   //
   //  If any skips are set, then we've already been through here.
   //
   if (skip == 0) {

      //
      //  Check if any of the names are invalid.  If so, ask Proshop if they want to ignore the error.
      //
      if (parm.inval25 != 0) {

         err_name = parm.player25;
      }
      if (parm.inval24 != 0) {

         err_name = parm.player24;
      }
      if (parm.inval23 != 0) {

         err_name = parm.player23;
      }
      if (parm.inval22 != 0) {

         err_name = parm.player22;
      }
      if (parm.inval21 != 0) {

         err_name = parm.player21;
      }
      if (parm.inval20 != 0) {

         err_name = parm.player20;
      }
      if (parm.inval19 != 0) {

         err_name = parm.player19;
      }
      if (parm.inval18 != 0) {

         err_name = parm.player18;
      }
      if (parm.inval17 != 0) {

         err_name = parm.player17;
      }
      if (parm.inval16 != 0) {

         err_name = parm.player16;
      }
      if (parm.inval15 != 0) {

         err_name = parm.player15;
      }
      if (parm.inval14 != 0) {

         err_name = parm.player14;
      }
      if (parm.inval13 != 0) {

         err_name = parm.player13;
      }
      if (parm.inval12 != 0) {

         err_name = parm.player12;
      }
      if (parm.inval11 != 0) {

         err_name = parm.player11;
      }
      if (parm.inval10 != 0) {

         err_name = parm.player10;
      }
      if (parm.inval9 != 0) {

         err_name = parm.player9;
      }
      if (parm.inval8 != 0) {

         err_name = parm.player8;
      }
      if (parm.inval7 != 0) {

         err_name = parm.player7;
      }
      if (parm.inval6 != 0) {

         err_name = parm.player6;
      }
      if (parm.inval5 != 0) {

         err_name = parm.player5;
      }
      if (parm.inval4 != 0) {

         err_name = parm.player4;
      }
      if (parm.inval3 != 0) {

         err_name = parm.player3;
      }
      if (parm.inval2 != 0) {

         err_name = parm.player2;
      }
      if (parm.inval1 != 0) {

         err_name = parm.player1;
      }

      if (!err_name.equals( "" )) {      // invalid name received

         out.println(SystemUtils.HeadTitle("Player Not Found - Prompt"));
         out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><H3>Player's Name Not Found in System</H3><BR>");
         out.println("<BR><BR>Warning:  " + err_name + " does not exist in the system database.");

         if (overrideAccess) {
             out.println("<br><br>(<b>Warning: If overridden, this player will be reported as an Unknown Round</b>!)");
         }

         //
         //  Return to _lott
         //
         skipValue = 1;                 // set skip value for return

         goReturnPrompt(skipValue, out, parm, overrideAccess, "");
         return;
      }

      //
      //  If Oakland Hills and more than 7 days in adv - no X's allowed
      //
      if (club.equals( "oaklandhills" ) && parm.ind > 7) {

         if (parm.player1.equalsIgnoreCase( "x" ) || parm.player2.equalsIgnoreCase( "x" ) ||
             parm.player3.equalsIgnoreCase( "x" ) || parm.player4.equalsIgnoreCase( "x" ) ||
             parm.player5.equalsIgnoreCase( "x" ) || parm.player6.equalsIgnoreCase( "x" ) || parm.player7.equalsIgnoreCase( "x" ) ||
             parm.player8.equalsIgnoreCase( "x" ) || parm.player9.equalsIgnoreCase( "x" ) || parm.player10.equalsIgnoreCase( "x" ) ||
             parm.player11.equalsIgnoreCase( "x" ) || parm.player12.equalsIgnoreCase( "x" ) ||
             parm.player13.equalsIgnoreCase( "x" ) || parm.player14.equalsIgnoreCase( "x" ) ||
             parm.player15.equalsIgnoreCase( "x" ) || parm.player16.equalsIgnoreCase( "x" ) || parm.player17.equalsIgnoreCase( "x" ) ||
             parm.player18.equalsIgnoreCase( "x" ) || parm.player19.equalsIgnoreCase( "x" ) || parm.player20.equalsIgnoreCase( "x" ) ||
             parm.player21.equalsIgnoreCase( "x" ) || parm.player22.equalsIgnoreCase( "x" ) ||
             parm.player23.equalsIgnoreCase( "x" ) || parm.player24.equalsIgnoreCase( "x" ) ||
             parm.player25.equalsIgnoreCase( "x" )) {

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Invalid Player Selection</H3>");
            out.println("<BR>Sorry, you cannot reserve player positions with an X more than 7 days in advance.<br>");

            skipValue = 1;                 // set skip value for return

            goReturnPrompt(skipValue, out, parm, overrideAccess, "");
            return;
         }
      }
   }

   //
   //  Check for twoSomeOnly times for Mayfield Sand Ridge and restrict even proshop users
   //  DO *NOT* ALLOW OVERRIDE!
   //
   if (club.equals("mayfieldsr") && (verifyCustom.checkMayfieldSR(parm.date, parm.time1, parm.day) ||
           verifyCustom.checkMayfieldSR(parm.date, parm.time2, parm.day) || verifyCustom.checkMayfieldSR(parm.date, parm.time3, parm.day))) {

       // Check the 4 players for each time to see if more than 2 are populated
       int playerCount = 0;
       int maxPlayerError = 0;

       if (verifyCustom.checkMayfieldSR(parm.date, parm.time1, parm.day)) {

           playerCount = 0;
           if (!parm.player1.equals("")) playerCount++;
           if (!parm.player2.equals("")) playerCount++;
           if (!parm.player3.equals("")) playerCount++;
           if (!parm.player4.equals("")) playerCount++;

           if (playerCount > 2) {
               maxPlayerError = 1;
           }
       }

       if (maxPlayerError == 0 && verifyCustom.checkMayfieldSR(parm.date, parm.time2, parm.day)) {

           playerCount = 0;
           if (!parm.player5.equals("")) playerCount++;
           if (!parm.player6.equals("")) playerCount++;
           if (!parm.player7.equals("")) playerCount++;
           if (!parm.player8.equals("")) playerCount++;

           if (playerCount > 2) {
               maxPlayerError = 2;
           }
       }

       if (parm.slots == 3 && maxPlayerError == 0 && verifyCustom.checkMayfieldSR(parm.date, parm.time3, parm.day)) {

           playerCount = 0;
           if (!parm.player9.equals("")) playerCount++;
           if (!parm.player10.equals("")) playerCount++;
           if (!parm.player11.equals("")) playerCount++;
           if (!parm.player12.equals("")) playerCount++;

           if (playerCount > 2) {
               maxPlayerError = 3;
           }
       }

       if (maxPlayerError > 0) {

           out.println(SystemUtils.HeadTitle("Max Player Limit Exceeded"));
           out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
           out.println("<hr width=\"40%\">");
           out.println("<BR><H3>Round Exceeding Max Number of Players</H3><BR>");
           out.println("<BR>Sorry, a maximum of two players are allowed in tee time #" + maxPlayerError + ".");
           out.println("<BR><BR>Please remove any excess players and submit the reservation again.");
           out.println("<BR><BR>");

           skipValue = 0;

           goReturnPrompt(skipValue, out, parm, overrideAccess, "");
           return;
       }
   }

   //
   //  Check if proshop user requested that we skip the mship test (member exceeded max and proshop
   //  wants to override the violation).
   //
   //  If this skip, or any of the following skips are set, then we've already been through these tests.
   //
   if (skip < 2) {

      //
      //************************************************************************
      //  Check any membership types for max rounds per week, month or year
      //  Also, check for 'days in advance' limits based on membership types
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

            out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
            out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Member Exceeded Limit</H3><BR>");
            out.println("<BR><BR>Warning:  " + parm.player + " is a " + parm.mship + " member and has exceeded the<BR>");
            out.println("maximum number of tee times allowed for this " + parm.period + ".");
            //
            //  Return to _lott
            //
            skipValue = 2;                 // set skip value for return

            goReturnPrompt(skipValue, out, parm, overrideAccess, "");
            return;
         }
      }
   }         // end of skip2 if

   //
   //  Check if proshop user requested that we skip the Mem Rest for Days in Adv
   //
   //  If this skip, or any of the following skips are set, then we've already been through these tests.
   //
   if (skip < 3) {

      //
      // *******************************************************************************
      //  Check Membership Restrictions for Days in Advance Limits
      // *******************************************************************************
      //
      check = checkDaysInAdv(parm, out, con);      // go check

      if (parm.error == true) {          // if we hit a db error

         return;
      }

      if (check == true) {      // a member is in violation of the 'days in advance' for his/her mship type

         out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
         out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");

         if (club.equals( "oaklandhills" )) {   // custom checks for Oakland Hills

            if (!parm.player.equals( "" )) {         // if we hit on a member violation

               out.println("<BR><BR><H3>Member Has Already Used An Advance Request</H3>");
               out.println("<BR>Sorry, each membership is entitled to only one advance tee time request.<br>");
               out.println("<BR>" +parm.player+ " has already used his/her advance tee time request for the season.");

            } else {

               out.println("<BR><BR><H3>Maximum Allowed Advanced Tee Times Exist</H3>");
               out.println("<BR>Sorry, the maximum number of advanced tee time requests already exist on the selected date.");
            }

         } else {

            if (club.equals( "ccrockies" )) {       // if C of the Rockies

               out.println("<BR><BR><H3>Member Already Has Max Allowed Advance Requests</H3>");
               out.println("<BR>Sorry, " +parm.player+ " already has 5 advance tee time requests scheduled.<br>");

            } else {

               if (club.equals( "catamount" ) && parm.player.startsWith( "Founder Error" )) {       // if Catamount Ranch

                  StringTokenizer tok = new StringTokenizer( parm.player, "/" );   // parse the error message

                  parm.player = tok.nextToken();     // skip Founder Error
                  parm.player = tok.nextToken();     // get Player name

                  out.println("<BR><BR><H3>Member Already Has Max Allowed Advance Requests</H3>");
                  out.println("<BR>Sorry, " +parm.player+ " already has 5 advance tee time requests scheduled.<br>");

               } else {

                  if (club.equals( "sonnenalp" ) && parm.player.startsWith( "Sonnenalp Advance Error" )) {       // if Sonnenalp Advance Error

                     StringTokenizer tok = new StringTokenizer( parm.player, "/" );   // parse the error message

                     parm.player = tok.nextToken();     // skip Advance Error
                     parm.player = tok.nextToken();     // get Player name

                     out.println("<BR><BR><H3>Member Already Has Max Allowed Advance Requests</H3>");
                     out.println("<BR>Sorry, " +parm.player+ " already has 12 advance tee time requests scheduled.<br>");

                  } else {

                     out.println("<BR><BR><H3>Days in Advance Exceeded for Member</H3><BR>");
                     out.println("<BR><BR>Warning:  " + parm.player + " cannot ");
                     out.println("be part of a tee time this far in advance.");
                  }
               }
            }
         }

         //
         //  Return to _lott
         //
         skipValue = 3;                 // set skip value for return

         goReturnPrompt(skipValue, out, parm, overrideAccess, "");
         return;
      }

   }      // end of skip3


   //
   //   Perform this function outside of the skips so it is updated every time (updates slotParms.custom_disp fields)
   //
   if (parm.guests > 0) {      // if any guests were included

      //
      //  If Sonnenalp - we know we have guests so go get their rates to be displayed on the tee sheet (saved in custom_dispx)
      //
      if (club.equals( "sonnenalp" )) {                 // if Sonnenalp

         verifyCustom.addGuestRatesM(parm);             // get rates for each guest
      }
   }


   //
   //  Check if proshop user requested that we skip the max # of guests test
   //
   //  If this skip, or any of the following skips are set, then we've already been through these tests.
   //
   if (skip < 4) {

      //
      //************************************************************************
      //  Check for max # of guests exceeded (per member or per tee time)
      //************************************************************************
      //
      if (parm.guests > 0 && parm.members > 0) {      // if any guests and members were included

         int guests = 0;                  // init # of guests count

         if (fb == 0) {                   // is Tee time for Front 9?

            sfb = "Front";
         }

         if (fb == 1) {                   // is it Back 9?

            sfb = "Back";
         }

         String per = "Member";

         try {

            PreparedStatement pstmt5 = con.prepareStatement (
               "SELECT * " +
               "FROM guestres2 " +
               "WHERE activity_id = 0 AND sdate <= ? AND edate >= ? AND stime <= ? AND etime >= ?");

            pstmt5.clearParameters();        // clear the parms
            pstmt5.setLong(1, date);
            pstmt5.setLong(2, date);
            pstmt5.setInt(3, time);
            pstmt5.setInt(4, time);
            rs = pstmt5.executeQuery();      // execute the prepared stmt

            loop1:
            while (rs.next()) {

               rest_name = rs.getString("name");
               grest_recurr = rs.getString("recurr");
               grest_num = rs.getInt("num_guests");
               rcourse = rs.getString("courseName");
               rest_fb = rs.getString("fb");
               per = rs.getString("per");

               // now look up the guest types for this restriction
               PreparedStatement pstmt3 = con.prepareStatement (
                      "SELECT guest_type FROM guestres2_gtypes WHERE guestres_id = ?");

               pstmt3.clearParameters();
               pstmt3.setInt(1, rs.getInt("id"));

               ResultSet rs2 = pstmt3.executeQuery();

               while ( rs2.next() ) {

                  rguest.add(rs2.getString("guest_type"));

               }
               pstmt3.close();

               check = false;       // init 'check guests' flag

               guests = 0;          // reset # of guests in tee time

               //
               //  Check if course matches that specified in restriction
               //
               if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( course ))) {

                  if (!verifySlot.checkRestSuspend(-99, rs.getInt("id"), (int)date, time, day, course, con)) {        // check if this guest restriction is suspended for this time)

                      //
                      //  We must check the recurrence for this day (Monday, etc.) and guest types
                      //
                      //     parm.g[x] = guest types specified in player name fields
                      //     rguest[x] = guest types from restriction gotten above
                      //
                      if (grest_recurr.equalsIgnoreCase( "every " + day )) {

                         if ((rest_fb.equals( "Both" )) || (rest_fb.equals( sfb ))) {

                            i = 0;
                            while (i < 25) {                        // check all possible players

                               if (!parm.g[i].equals( "" )) {      // if player is a guest

                                  i2 = 0;
                                  gloop1:
                                  while (i2 < rguest.size()) {     // check all guest types for this restriction

                                     if ( rguest.get(i2).equals( parm.g[i] )) {

                                        check = true;                  // indicate check num of guests
                                        guests++;                      // bump number of restricted guests in tee time
                                        break gloop1;
                                     }
                                     i2++;
                                  }
                               }
                               i++;
                            }
                         }
                      }

                      if (grest_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                         if ((rest_fb.equals( "Both" )) || (rest_fb.equals( sfb ))) {

                            i = 0;
                            while (i < 25) {                        // check all possible players

                               if (!parm.g[i].equals( "" )) {      // if player is a guest

                                  i2 = 0;
                                  gloop2:
                                  while (i2 < rguest.size()) {     // check all guest types for this restriction

                                     if ( rguest.get(i2).equals( parm.g[i] )) {

                                        check = true;                  // indicate check num of guests
                                        guests++;                      // bump number of restricted guests in tee time
                                        break gloop2;
                                     }
                                     i2++;
                                  }
                               }
                               i++;
                            }
                         }
                      }

                      if ((grest_recurr.equalsIgnoreCase( "all weekdays" )) &&
                          (!day.equalsIgnoreCase( "saturday" )) &&
                          (!day.equalsIgnoreCase( "sunday" ))) {

                         if ((rest_fb.equals( "Both" )) || (rest_fb.equals( sfb ))) {

                            i = 0;
                            while (i < 25) {                        // check all possible players

                               if (!parm.g[i].equals( "" )) {      // if player is a guest

                                  i2 = 0;
                                  gloop3:
                                  while (i2 < rguest.size()) {     // check all guest types for this restriction

                                     if ( rguest.get(i2).equals( parm.g[i] )) {

                                        check = true;                  // indicate check num of guests
                                        guests++;                      // bump number of restricted guests in tee time
                                        break gloop3;
                                     }
                                     i2++;
                                  }
                               }
                               i++;
                            }
                         }
                      }

                      //
                      //  if Weekends and its Saturday
                      //
                      if ((grest_recurr.equalsIgnoreCase( "all weekends" )) &&
                          (day.equalsIgnoreCase( "saturday" ))) {

                         if ((rest_fb.equals( "Both" )) || (rest_fb.equals( sfb ))) {

                            i = 0;
                            while (i < 25) {                        // check all possible players

                               if (!parm.g[i].equals( "" )) {      // if player is a guest

                                  i2 = 0;
                                  gloop4:
                                  while (i2 < rguest.size()) {     // check all guest types for this restriction

                                     if ( rguest.get(i2).equals( parm.g[i] )) {

                                        check = true;                  // indicate check num of guests
                                        guests++;                      // bump number of restricted guests in tee time
                                        break gloop4;
                                     }
                                     i2++;
                                  }
                               }
                               i++;
                            }
                         }
                      }

                      //
                      //  if Weekends and its Sunday
                      //
                      if ((grest_recurr.equalsIgnoreCase( "all weekends" )) &&
                          (day.equalsIgnoreCase( "sunday" ))) {

                         if ((rest_fb.equals( "Both" )) || (rest_fb.equals( sfb ))) {

                            i = 0;
                            while (i < 25) {                        // check all possible players

                               if (!parm.g[i].equals( "" )) {      // if player is a guest

                                  i2 = 0;
                                  gloop5:
                                  while (i2 < rguest.size()) {     // check all guest types for this restriction

                                     if ( rguest.get(i2).equals( parm.g[i] )) {

                                        check = true;                  // indicate check num of guests
                                        guests++;                      // bump number of restricted guests in tee time
                                        break gloop5;
                                     }
                                     i2++;
                                  }
                               }
                               i++;
                            }
                         }
                      }
                  }
               }      // end of IF course matches

               if (check == true) {   // if restriction exists for this day and time

                  // if num of guests req'd (guests) > num allowed (grest_num) per member or per tee time (slot)
                  //
                  //       to get here guests is > 0
                  //       grest_num is 0 - 4 (max allowed)
                  //       memg1-5 is number of members for each group/slot
                  //       parm.guestsg1-5 is number of guests for each group/slot
                  //
                  float xguests = 0;               // number of guests per member requested
                  int memcount = 0;
                  guestError = false;              // init error flag

                  if (per.equals( "Member" )) {       // if restriction is per member

                     memcount = parm.memg1 + parm.memg2 + parm.memg3 + parm.memg4 + parm.memg5;  // total members in req

                     if (memcount == 0) {

                        guestError = true;
                        break loop1;

                     } else {

                        xguests = guests / memcount;          // # of guests per member (in entire request)

                        if (xguests > grest_num) {            // too many guests per member?

                           guestError = true;
                           break loop1;
                        }
                     }

                  } else {          // restriction is per tee time

                     if (parm.guestsg1 > grest_num || parm.guestsg2 > grest_num || parm.guestsg3 > grest_num ||
                         parm.guestsg4 > grest_num || parm.guestsg5 > grest_num) {

                        guestError = true;
                        break loop1;
                     }
                  }
               }

            }   // end of loop1 while loop

            pstmt5.close();

            if (guestError == true) {         // if too many guests

               out.println(SystemUtils.HeadTitle("Max Num Guests Exceeded - Reject"));
               out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H3>Number of Guests Exceeded Limit</H3><BR>");
               out.println("<BR><BR>Sorry, the maximum number of guests allowed for the<BR>");
               out.println("time you are requesting is " + grest_num + " per " +per+ ".");
               out.println("<BR><BR>Restriction Name = " +rest_name);
               //
               //  Return to _lott
               //
               skipValue = 4;                 // set skip value for return

               goReturnPrompt(skipValue, out, parm, overrideAccess, "");
               return;
            }
         }
         catch (Exception e5) {

            dbError(out, e5);
            return;
         }
      }      // end of if guests and member(s)

      if (parm.guests > 0) {      // if any guests in this request

         if (club.equals( "merion" ) && parm.course.equals( "East" ) &&
             (parm.day.equals( "Saturday" ) || parm.day.equals( "Sunday" ))) {   // Merion, East course, and a w/e

            guestError = checkMerionRes(con, out, parm);      // go check

            if (guestError == true) {      // max guest times exceeded this hour

               out.println(SystemUtils.HeadTitle("Max Number of Guest Times Exceeded - Reject"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Maximum Number of Guest Times Exceeded</H3>");
               out.println("<BR>Sorry, but this request would exceed the maximum number of guest times allowed during this hour.");
               out.println("<BR><BR>Please try another time of the day.");

               //
               //  Return to _lott
               //
               skipValue = 4;                 // set skip value for return
               goReturnPrompt(skipValue, out, parm, overrideAccess, "");
               return;
            }
         }   // end of if Merion


         //
         //  If Congressional, then check for 'Cert Jr Guest' types - must only follow a Certified Dependent
         //
         if (club.equals( "congressional" )) {           // if congressional

            error = checkCongressionalGuests(parm);

            if (error == true) {      // no guests allowed

               out.println(SystemUtils.HeadTitle("Guest Restriction - Reject"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Guest Type Not Allowed</H3>");
               out.println("<BR>Sorry, but the guest type 'Cert Jr Guest' can only follow a Certified Dependent");
               out.println("<BR>and a dependent may only have one guest.");

               //
               //  Return to _lott
               //
               skipValue = 4;                 // set skip value for return
               goReturnPrompt(skipValue, out, parm, overrideAccess, "");
               return;
            }
         }   // end of if Congressional


         //
         //  If Bearpath, then check for member-only time
         //
         if (club.equals( "bearpath" )) {           // if Bearpath

            error = verifySlot.checkBearpathGuests(parm.day, parm.date, time, parm.ind);

            if (error == true) {      // no guests allowed

               out.println(SystemUtils.HeadTitle("Max Number of Guest Times Exceeded - Reject"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Guests Not Allowed</H3>");
               out.println("<BR>Sorry, but guests are not allowed during this time.  This is a member-only time.");
               out.println("<BR><BR>Please try another time of the day.");

               skipValue = 4;                 // set skip value for return
               goReturnPrompt(skipValue, out, parm, overrideAccess, "");
               return;
            }
         }      // end of if Bearpath

         //
         //  Guest requested - if Wilmington, check for maxx guests
         //
         if (club.equals( "wilmington" )) {

            check = checkWilGuests(con, parm);

            if (check == true) {      // more than 12 guests

               out.println(SystemUtils.HeadTitle("Max Number of Guests Exceeded - Reject"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Maximum Number of Guests Exceeded</H3>");
               out.println("<BR>Sorry, but there are already 12 guests scheduled today.");
               out.println("<BR>No more than 12 guests are allowed during the selected time period.  This request would exceed that total.");
               out.println("<BR><BR>Please remove one or more guests, or try another time of the day.");

               skipValue = 4;                 // set skip value for return
               goReturnPrompt(skipValue, out, parm, overrideAccess, "");
               return;
            }
         }      // end of if Wilmington

      }      // end of if any guests

   }      // end of skip4

   //
   //  Check if proshop user requested that we skip the member restrictions test
   //
   //  If this skip, or any following skips are set, then we've already been through these tests.
   //
   if (skip < 5) {

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

         //
         //  If Stanwich Error - process dependent restriction error
         //
         if (club.equals( "stanwichclub" ) && parm.player.equals( "Stanwich Dependent Error" )) {    // if error found

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><BR><H3>Dependent Without An Adult</H3>");
            out.println("<BR><BR>Sorry, dependents must be accompanied by an adult for this day and time.");
            out.println("<BR><BR>All Tee Times with a Dependent must include at least 1 Adult during times specified by the golf shop.");
            out.println("<BR><BR>Please add an adult player or select a different time of the day or a different day.");
            //
            //  Return to _lott
            //
            skipValue = 5;                 // set skip value for return

            goReturnPrompt(skipValue, out, parm, overrideAccess, "");
            return;
         }

         //
         //  If Castle Pines Error - process dependent restriction error
         //
         if (club.equals( "castlepines" ) && parm.player.equals( "Castle Dependent Error" )) {    // if error found

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><BR><H3>Dependent Without An Adult</H3>");
            out.println("<BR><BR>Sorry, dependents must be accompanied by an adult at all times.");
            out.println("<BR><BR>Please add an adult player or return to the tee sheet.");
            //
            //  Return to _lott
            //
            skipValue = 5;                 // set skip value for return

            goReturnPrompt(skipValue, out, parm, overrideAccess, "");
            return;
         }

         //
         //  If Ritz-Carlton Error - process dependent restriction error
         //
         if (club.equals( "ritzcarlton" ) && parm.player.equals( "Ritz Custom Error" )) {    // if error found

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Special Tee Time Quota Exceeded</H3>");
            out.println("<BR>Sorry, there are already 2 tee times with Club Golf members");
            out.println("<BR>or Recip guests scheduled this hour.<br><br>");
            out.println("Please select a different time of day, or change the players.<br><br>");
            //
            //  Return to _lott
            //
            skipValue = 5;                 // set skip value for return

            goReturnPrompt(skipValue, out, parm, overrideAccess, "");
            return;
         }

         //
         //  If Skaneateles - check for Dependent Restriction
         //
         if (club.equals( "skaneateles" ) && parm.player.equals( "Skaneateles Custom Error" )) {

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Unaccompanied Dependents Not Allowed</H3>");
            out.println("<BR>Sorry, dependents must be accompanied by an adult after 4:00 PM each day.<br><br>");
            out.println("Please select a different time of day, or change the players.<br><br>");
            //
            //  Return to _lott
            //
            skipValue = 5;                 // set skip value for return

            goReturnPrompt(skipValue, out, parm, overrideAccess, "");
            return;
         }

         //
         //  If Oakland Hills - check for Dependents - must be accompanied by adult (always)
         //
         if (club.equals( "oaklandhills" ) && parm.player.equals( "Oakland Hills Custom Error" )) {

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Unaccompanied Dependents Not Allowed</H3>");
            out.println("<BR>Sorry, dependents must be accompanied by an adult.<br><br>");

            skipValue = 5;                 // set skip value for return
            goReturnPrompt(skipValue, out, parm, overrideAccess, "");
            return;
         }

         //
         //  If Bearpath - check member types
         //
         if (club.equals( "bearpath" ) && parm.player.equals( "Bearpath Custom Error" )) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><BR><H3>Request Not Allowed</H3>");
            out.println("<BR><BR>Sorry, CD Plus members are not allowed to play at this time ");
            out.println("<BR>unless accompanied by an authorized member.");

            skipValue = 5;                 // set skip value for return
            goReturnPrompt(skipValue, out, parm, overrideAccess, "");
            return;
         }

         //
         //  If Belle Meade - check custom restriction hit
         //
         if (club.equals( "bellemeadecc" ) && parm.player.equals( "Belle Meade Custom Error" )) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><BR><H3>Member Not Allowed</H3>");
            out.println("<BR><BR>Sorry, Primary Females must be accompanied by a Primary Male between 8 AM and 12:50 PM on Sundays.");
            out.println("<BR><BR>Please add a Primary Male member or return to the tee sheet.");

            skipValue = 5;                 // set skip value for return
            goReturnPrompt(skipValue, out, parm, overrideAccess, "");
            return;
         }

         //
         //  If Cherry Hills Error - process mtype restriction error
         //
         if (club.equals( "cherryhills" ) && parm.player.equals( "Cherry Hills Mtype Error" )) {    // if error found

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><BR><H3>Player Not Allowed</H3>");
            out.println("<BR><BR>Sorry, one or more players are not allowed to be part of a tee time for this day and time.");
            if (parm.day.equals( "Monday" ) || parm.day.equals( "Wednesday" ) || parm.day.equals( "Friday" )) {
               out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
            } else {
               if (parm.day.equals( "Tuesday" )) {
                  if (parm.time1 > 1100) {
                     out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
                  } else {
                     out.println("<BR><BR>Only Spouses may make a request more than 1 day in advance for a tee time before 11 AM on Tuesdays.");
                  }
               } else {
                  if (parm.day.equals( "Thursday" )) {
                     if (parm.time1 > 1000) {
                        out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
                     } else {
                        out.println("<BR><BR>Only Spouses may make a request more than 1 day in advance for a tee time before 10 AM on Thursdays.");
                     }
                  } else {
                     if (parm.day.equals( "Sunday" )) {
                        if (parm.time1 > 1000) {
                           out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
                        } else {
                           out.println("<BR><BR>Only Members may be included in a tee time before 10 AM on Sundays.");
                        }
                     } else {       // Saturday or Holiday
                        if (parm.time1 > 1100) {
                           out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
                        } else {
                           out.println("<BR><BR>Player not allowed to make a tee time more than 24 hours in advance on Saturdays and Holidays before 11 AM.");
                        }
                     }
                  }
               }
            }
            out.println("<BR><BR>Please change players or select a different day or time of day.");
            //
            //  Return to _lott
            //
            skipValue = 5;                 // set skip value for return

            goReturnPrompt(skipValue, out, parm, overrideAccess, "");
            return;
         }

         out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
         out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Member Restricted</H3><BR>");
         out.println("<BR>Sorry, <b>" + parm.player + "</b> is restricted from playing during this time.<br><br>");
         out.println("This time slot has the following restriction:  <b>" + parm.rest_name + "</b><br><br>");
         //
         //  Return to _lott
         //
         skipValue = 5;                 // set skip value for return

         goReturnPrompt(skipValue, out, parm, overrideAccess, "");
         return;
      }
   }      // end of skip5

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
      // *******************************************************************************
      //
      check = checkMemNum(con, out, parm, day);      // go check

      if (parm.error == true) {          // if we hit a db error

         return;
      }

      if (check == true) {          // if we hit on a restriction

         out.println(SystemUtils.HeadTitle("Member Number Restricted - Reject"));
         out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Member Restricted by Member Number</H3><BR>");
         out.println("<BR>Sorry, ");
         if (!parm.pNum1.equals( "" )) {
            out.println("<b>" + parm.pNum1 + "</b> ");
         }
         if (!parm.pNum2.equals( "" )) {
            out.println("<b>" + parm.pNum2 + "</b> ");
         }
         if (!parm.pNum3.equals( "" )) {
            out.println("<b>" + parm.pNum3 + "</b> ");
         }
         if (!parm.pNum4.equals( "" )) {
            out.println("<b>" + parm.pNum4 + "</b> ");
         }
         if (!parm.pNum5.equals( "" )) {
            out.println("<b>" + parm.pNum5 + "</b> ");
         }
         if (!parm.pNum6.equals( "" )) {
            out.println("<b>" + parm.pNum6 + "</b> ");
         }
         if (!parm.pNum7.equals( "" )) {
            out.println("<b>" + parm.pNum7 + "</b> ");
         }
         if (!parm.pNum8.equals( "" )) {
            out.println("<b>" + parm.pNum8 + "</b> ");
         }
         if (!parm.pNum9.equals( "" )) {
            out.println("<b>" + parm.pNum9 + "</b> ");
         }
         if (!parm.pNum10.equals( "" )) {
            out.println("<b>" + parm.pNum10 + "</b> ");
         }
         if (!parm.pNum11.equals( "" )) {
            out.println("<b>" + parm.pNum11 + "</b> ");
         }
         if (!parm.pNum12.equals( "" )) {
            out.println("<b>" + parm.pNum12 + "</b> ");
         }
         if (!parm.pNum13.equals( "" )) {
            out.println("<b>" + parm.pNum13 + "</b> ");
         }
         if (!parm.pNum14.equals( "" )) {
            out.println("<b>" + parm.pNum14 + "</b> ");
         }
         if (!parm.pNum15.equals( "" )) {
            out.println("<b>" + parm.pNum15 + "</b> ");
         }
         if (!parm.pNum16.equals( "" )) {
            out.println("<b>" + parm.pNum16 + "</b> ");
         }
         if (!parm.pNum17.equals( "" )) {
            out.println("<b>" + parm.pNum17 + "</b> ");
         }
         if (!parm.pNum18.equals( "" )) {
            out.println("<b>" + parm.pNum18 + "</b> ");
         }
         if (!parm.pNum19.equals( "" )) {
            out.println("<b>" + parm.pNum19 + "</b> ");
         }
         if (!parm.pNum20.equals( "" )) {
            out.println("<b>" + parm.pNum20 + "</b> ");
         }
         if (!parm.pNum21.equals( "" )) {
            out.println("<b>" + parm.pNum21 + "</b> ");
         }
         if (!parm.pNum22.equals( "" )) {
            out.println("<b>" + parm.pNum22 + "</b> ");
         }
         if (!parm.pNum23.equals( "" )) {
            out.println("<b>" + parm.pNum23 + "</b> ");
         }
         if (!parm.pNum24.equals( "" )) {
            out.println("<b>" + parm.pNum24 + "</b> ");
         }
         if (!parm.pNum25.equals( "" )) {
            out.println("<b>" + parm.pNum25 + "</b> ");
         }
         out.println("is/are restricted from playing during this time because the");
         out.println("<BR> number of members with the same member number has exceeded the maximum allowed.");
         out.println("<br><br>This time slot has the following restriction:  <b>" + parm.rest_name + "</b>");
         //
         //  Return to _lott
         //
         skipValue = 6;                 // set skip value for return

         goReturnPrompt(skipValue, out, parm, overrideAccess, "");
         return;
      }
   }         // end of IF skip6


   //
   //  Check if proshop user requested that we skip the following test
   //
   //  If this skip is set, then we've already been through these tests.
   //
   if (skip < 7) {

      //
      //***********************************************************************************************
      //
      //    Now check if any of the players are already scheduled today (only 1 res per day)
      //
      //***********************************************************************************************
      //
      hit = false;

      if (!club.equals( "longcove" )) {       // TEMP ***********

         if (!parm.player1.equals( "" ) && !parm.player1.equalsIgnoreCase( "x" ) && parm.g[0].equals( "" )) {

            player = parm.player1;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player2.equals( "" ) && !parm.player2.equalsIgnoreCase( "x" ) && parm.g[1].equals( "" ) && hit == false) {

            player = parm.player2;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player3.equals( "" ) && !parm.player3.equalsIgnoreCase( "x" ) && parm.g[2].equals( "" ) && hit == false) {

            player = parm.player3;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player4.equals( "" ) && !parm.player4.equalsIgnoreCase( "x" ) && parm.g[3].equals( "" ) && hit == false) {

            player = parm.player4;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player5.equals( "" ) && !parm.player5.equalsIgnoreCase( "x" ) && parm.g[4].equals( "" ) && hit == false) {

            player = parm.player5;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player6.equals( "" ) && !parm.player6.equalsIgnoreCase( "x" ) && parm.g[5].equals( "" ) && hit == false) {

            player = parm.player6;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player7.equals( "" ) && !parm.player7.equalsIgnoreCase( "x" ) && parm.g[6].equals( "" ) && hit == false) {

            player = parm.player7;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player8.equals( "" ) && !parm.player8.equalsIgnoreCase( "x" ) && parm.g[7].equals( "" ) && hit == false) {

            player = parm.player8;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player9.equals( "" ) && !parm.player9.equalsIgnoreCase( "x" ) && parm.g[8].equals( "" ) && hit == false) {

            player = parm.player9;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player10.equals( "" ) && !parm.player10.equalsIgnoreCase( "x" ) && parm.g[9].equals( "" ) && hit == false) {

            player = parm.player10;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player11.equals( "" ) && !parm.player11.equalsIgnoreCase( "x" ) && parm.g[10].equals( "" ) && hit == false) {

            player = parm.player11;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player12.equals( "" ) && !parm.player12.equalsIgnoreCase( "x" ) && parm.g[11].equals( "" ) && hit == false) {

            player = parm.player12;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player13.equals( "" ) && !parm.player13.equalsIgnoreCase( "x" ) && parm.g[12].equals( "" ) && hit == false) {

            player = parm.player13;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player14.equals( "" ) && !parm.player14.equalsIgnoreCase( "x" ) && parm.g[13].equals( "" ) && hit == false) {

            player = parm.player14;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player15.equals( "" ) && !parm.player15.equalsIgnoreCase( "x" ) && parm.g[14].equals( "" ) && hit == false) {

            player = parm.player15;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player16.equals( "" ) && !parm.player16.equalsIgnoreCase( "x" ) && parm.g[15].equals( "" ) && hit == false) {

            player = parm.player16;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player17.equals( "" ) && !parm.player17.equalsIgnoreCase( "x" ) && parm.g[16].equals( "" ) && hit == false) {

            player = parm.player17;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player18.equals( "" ) && !parm.player18.equalsIgnoreCase( "x" ) && parm.g[17].equals( "" ) && hit == false) {

            player = parm.player18;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player19.equals( "" ) && !parm.player19.equalsIgnoreCase( "x" ) && parm.g[18].equals( "" ) && hit == false) {

            player = parm.player19;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player20.equals( "" ) && !parm.player20.equalsIgnoreCase( "x" ) && parm.g[19].equals( "" ) && hit == false) {

            player = parm.player20;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player21.equals( "" ) && !parm.player21.equalsIgnoreCase( "x" ) && parm.g[20].equals( "" ) && hit == false) {

            player = parm.player21;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player22.equals( "" ) && !parm.player22.equalsIgnoreCase( "x" ) && parm.g[21].equals( "" ) && hit == false) {

            player = parm.player22;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player23.equals( "" ) && !parm.player23.equalsIgnoreCase( "x" ) && parm.g[22].equals( "" ) && hit == false) {

            player = parm.player23;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player24.equals( "" ) && !parm.player24.equalsIgnoreCase( "x" ) && parm.g[23].equals( "" ) && hit == false) {

            player = parm.player24;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (!parm.player25.equals( "" ) && !parm.player25.equalsIgnoreCase( "x" ) && parm.g[24].equals( "" ) && hit == false) {

            player = parm.player25;              // get player for message

            hit = chkPlayer(con, player, date, time, fb, course);
         }

         if (hit == true) {          // if we hit on a duplicate res

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Already Playing</H3><BR>");
            out.println("<BR>Sorry, <b>" + player + "</b> is already scheduled to play on this date.<br><br>");
            out.println("A player can only be scheduled once per day.<br><br>");

            boolean allowOverride = true;

            if (!overrideAccess || club.equals( "lakewoodranch" )) {    // skip if lakewood ranch
                allowOverride = false;
            }
            //
            //  Return to _slot
            //
            skipValue = 7;                 // set skip value for return

            goReturnPrompt(skipValue, out, parm, allowOverride, "");
            return;
         }

      }   // end of TEMP ****************

      //
      //   If Merion and East course, then check if any other family members are scheduled today - not allowed.
      //
      if (club.equals( "merion" ) && parm.course.equals( "East" )) {

         hit = verifySlot.checkMerionSchedm(parm, con);

         if (hit == true) {                 // if another family member is already booked today

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Already Scheduled</H3><BR>");
            out.println("<BR>Sorry, <b>" + parm.player + "</b> already has a family member scheduled to play today.<br><br>");
            out.println("Only one player per membership is allowed each day.<br><br>");
            out.println("Please remove this player or try a different date.<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            skipValue = 7;                 // set skip value for return
            goReturnPrompt(skipValue, out, parm, overrideAccess, "");
            return;
         }


         //
         //  Merion - Now check if more than 7 days in adv and a w/e, no more than 4 adv tee times per day
         //
         if (parm.ind > 7) {      // if this date is more than 7 days ahead

            if ((parm.day.equals( "Saturday" ) && time > 1030) || (parm.day.equals( "Sunday" ) && time > 900)) {

               hit = verifySlot.checkMerionWEm(parm, con);

               if (hit == true) {                 // if max adv times already booked

                  out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Advance Tee Time Limit</H3><BR>");
                  out.println("<BR>Sorry, this multi tee time request would exceed the limit of 4 advance tee times for this day.<br><br>");
                  out.println("Please reduce the number of tee times or try a different date.<br>");
                  //
                  //  Return to _slot to change the player order
                  //
                  skipValue = 7;                 // set skip value for return
                  goReturnPrompt(skipValue, out, parm, overrideAccess, "");
                  return;
               }
            }
         }
      }    // end of IF Merion

   }           // end of skip7


   //
   //  CUSTOM CHECKS
   //
   //  If this skip is set, then we've already been through these tests.
   //
   if (skip < 8) {

      //
      //***************************************************************************************************
      //
      //  CUSTOMS - check all possible customs here - those that are not dependent on guest info!!!!!!!!
      //
      //     verifyCustom.checkCustoms1 will process the individual custom and return any error message.
      //
      //    *** USE THIS FOR ALL FUTURE CUSTOMS WHEN APPROPRIATE !!!!!!!!!!!!!  ***   10/16/08
      //
      //***************************************************************************************************
      //
      String custErrorMsg = checkCustoms1(con, parm, user);     // go check for customs

      if (!custErrorMsg.equals( "" )) {         // if error encountered - reject

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR><BR><BR>");
         out.println("<hr width=\"40%\"><BR>");
         out.println( custErrorMsg );           // add custom error msg
         out.println("<BR><BR>");

         skipValue = 8;                 // set skip value for return
         goReturnPrompt(skipValue, out, parm, overrideAccess, "");
         return;
      }

      //
      //  MOVE ANY APPROPRIATE CUSTOMS THAT FOLLOW THIS SO THEY USE ABOVE PROCESS !!!!!!!!!!!!!!
      //

   }       // end of SKIP 8


   //
   //  Check if user has approved of the member/guest sequence (guest association)
   //
   //  If this skip is set, then we've already been through these tests.
   //
   if (skip < 9) {

      //
      //***********************************************************************************************
      //
      //    Now check the order of guests and members (guests must follow a member) - prompt to verify order
      //
      //        members = # of members requested
      //        guests  = # of guests requested
      //***********************************************************************************************
      //
      if (parm.guests > 0 && parm.members > 0) {

         if (parm.gstA[0].equals( "" )) {             // if player 1 is not a guest

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

         if (parm.members > 1 || !parm.gstA[0].equals( "" )) {  // if multiple members OR slot 1 is a guest

            //
            //  At least one guest and one member have been specified.
            //  Prompt user to verify the order.
            //
            out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
            out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");

            if (!parm.gstA[0].equals( "" ) && !posType.equals( "" )) {   // if slot 1 is not a guest & POS

               out.println("Guests must be specified <b>immediately after</b> the member they belong to.<br><br>");
               out.println("You cannot have a guest in the first player position when one or more members are also specified.");
               out.println("<BR><BR>");
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_slotm\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + parm.date + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + parm.time1 + "\">");
               out.println("<input type=\"hidden\" name=\"time2\" value=\"" + parm.time2 + "\">");
               out.println("<input type=\"hidden\" name=\"time3\" value=\"" + parm.time3 + "\">");
               out.println("<input type=\"hidden\" name=\"time4\" value=\"" + parm.time4 + "\">");
               out.println("<input type=\"hidden\" name=\"time5\" value=\"" + parm.time5 + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + parm.p5rest + "\">");
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
               out.println("<input type=\"hidden\" name=\"show1\" value=\"" + parm.show1 + "\">");
               out.println("<input type=\"hidden\" name=\"show2\" value=\"" + parm.show2 + "\">");
               out.println("<input type=\"hidden\" name=\"show3\" value=\"" + parm.show3 + "\">");
               out.println("<input type=\"hidden\" name=\"show4\" value=\"" + parm.show4 + "\">");
               out.println("<input type=\"hidden\" name=\"show5\" value=\"" + parm.show5 + "\">");
               out.println("<input type=\"hidden\" name=\"show6\" value=\"" + parm.show6 + "\">");
               out.println("<input type=\"hidden\" name=\"show7\" value=\"" + parm.show7 + "\">");
               out.println("<input type=\"hidden\" name=\"show8\" value=\"" + parm.show8 + "\">");
               out.println("<input type=\"hidden\" name=\"show9\" value=\"" + parm.show9 + "\">");
               out.println("<input type=\"hidden\" name=\"show10\" value=\"" + parm.show10 + "\">");
               out.println("<input type=\"hidden\" name=\"show11\" value=\"" + parm.show11 + "\">");
               out.println("<input type=\"hidden\" name=\"show12\" value=\"" + parm.show12 + "\">");
               out.println("<input type=\"hidden\" name=\"show13\" value=\"" + parm.show13 + "\">");
               out.println("<input type=\"hidden\" name=\"show14\" value=\"" + parm.show14 + "\">");
               out.println("<input type=\"hidden\" name=\"show15\" value=\"" + parm.show15 + "\">");
               out.println("<input type=\"hidden\" name=\"show16\" value=\"" + parm.show16 + "\">");
               out.println("<input type=\"hidden\" name=\"show17\" value=\"" + parm.show17 + "\">");
               out.println("<input type=\"hidden\" name=\"show18\" value=\"" + parm.show18 + "\">");
               out.println("<input type=\"hidden\" name=\"show19\" value=\"" + parm.show19 + "\">");
               out.println("<input type=\"hidden\" name=\"show20\" value=\"" + parm.show20 + "\">");
               out.println("<input type=\"hidden\" name=\"show21\" value=\"" + parm.show21 + "\">");
               out.println("<input type=\"hidden\" name=\"show22\" value=\"" + parm.show22 + "\">");
               out.println("<input type=\"hidden\" name=\"show23\" value=\"" + parm.show23 + "\">");
               out.println("<input type=\"hidden\" name=\"show24\" value=\"" + parm.show24 + "\">");
               out.println("<input type=\"hidden\" name=\"show25\" value=\"" + parm.show25 + "\">");
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
               out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
               out.println("<input type=\"hidden\" name=\"skipDining\" value=\"" + skipDining + "\">");
               out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");

               if (club.equals("oaklandhills")) {
                   for (int k=0; k<25; k++) {
                       out.println("<input type=\"hidden\" name=\"custom" + String.valueOf(k + 1) + "\" value=\"" + gbt[k] + "\">");
                   }
               }

               out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
               out.println("</form></font>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;

            } else {
               out.println("Guests should be specified <b>immediately after</b> the member they belong to.<br><br>");
               out.println("Please verify that the following order is correct:");
               out.println("<BR><BR>");
               out.println(parm.player1 + " <BR>");
               out.println(parm.player2 + " <BR>");
               if (!parm.player3.equals( "" )) {
                  out.println(parm.player3 + " <BR>");
               }
               if (!parm.player4.equals( "" )) {
                  out.println(parm.player4 + " <BR>");
               }
               if (!parm.player5.equals( "" )) {
                  out.println(parm.player5 + " <BR>");
               }
               if (!parm.player6.equals( "" )) {
                  out.println(parm.player6 + " <BR>");
               }
               if (!parm.player7.equals( "" )) {
                  out.println(parm.player7 + " <BR>");
               }
               if (!parm.player8.equals( "" )) {
                  out.println(parm.player8 + " <BR>");
               }
               if (!parm.player9.equals( "" )) {
                  out.println(parm.player9 + " <BR>");
               }
               if (!parm.player10.equals( "" )) {
                  out.println(parm.player10 + " <BR>");
               }
               if (!parm.player11.equals( "" )) {
                  out.println(parm.player11 + " <BR>");
               }
               if (!parm.player12.equals( "" )) {
                  out.println(parm.player12 + " <BR>");
               }
               if (!parm.player13.equals( "" )) {
                  out.println(parm.player13 + " <BR>");
               }
               if (!parm.player14.equals( "" )) {
                  out.println(parm.player14 + " <BR>");
               }
               if (!parm.player15.equals( "" )) {
                  out.println(parm.player15 + " <BR>");
               }
               if (!parm.player16.equals( "" )) {
                  out.println(parm.player16 + " <BR>");
               }
               if (!parm.player17.equals( "" )) {
                  out.println(parm.player17 + " <BR>");
               }
               if (!parm.player18.equals( "" )) {
                  out.println(parm.player18 + " <BR>");
               }
               if (!parm.player19.equals( "" )) {
                  out.println(parm.player19 + " <BR>");
               }
               if (!parm.player20.equals( "" )) {
                  out.println(parm.player20 + " <BR>");
               }
               if (!parm.player21.equals( "" )) {
                  out.println(parm.player21 + " <BR>");
               }
               if (!parm.player22.equals( "" )) {
                  out.println(parm.player22 + " <BR>");
               }
               if (!parm.player23.equals( "" )) {
                  out.println(parm.player23 + " <BR>");
               }
               if (!parm.player24.equals( "" )) {
                  out.println(parm.player24 + " <BR>");
               }
               if (!parm.player25.equals( "" )) {
                  out.println(parm.player25 + " <BR>");
               }
               out.println("<BR>Would you like to process the request as is?");

               //
               //  Return to _lott to change the player order
               //
               skipValue = 9;                 // set skip value for return

               goReturn2(skipValue, out, parm);
               return;
            }

         }   // end of IF more than 1 member specified OR guest name in slot #1

      } else {

         //
         //  Either all members or all guests - check for all guests (Unaccompanied Guests)
         //
         if (parm.guests > 0 && !club.equals( "sonnenalp")) {      // if all guests and NOT Sonnenalp

            //
            //  At least one guest and no member has been specified.
            //
            //  Prompt user to specify associated member(s) or skip.
            //
            out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");

            if (parm.guests == 1) {      // if one guest
               out.println("You are requesting a tee time for an unaccompanied guest.<br>");
               out.println("The guest should be associated with a member.<br><br>");
               out.println("<BR>Would you like to assign a member to the guest, or change the assignment?");
            } else {
               out.println("You are requesting a tee time for unaccompanied guests.<br>");
               out.println("Guests should be associated with a member.<br><br>");
               out.println("<BR>Would you like to assign a member to the guests, or change the assignments?");
            }

            //
            //  Return to _lott to change the player order
            //
            skipValue = 99;                 // set skip value for return (gets changed to '9')

            goReturn2(skipValue, out, parm);
            return;
         }

      }      // end of IF any guests and members specified

   } else {       // skip is >= 9

      if (skip == 9) {            // if this is a return from an assignment

         //
         //  User has responded to the guest association prompt - process tee time request in specified order
         //
         for (i = 0; i < 25; i++) {
            if (req.getParameter("userg" +i) != null) {
               userg[i] = req.getParameter("userg" + i);
               parm.userg[i] = userg[i];                   // save in parms
            }
         }
      }

   }         // end of IF skip9


   if (skip < 12) {

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
         if (!parm.userg[i].equals( "" )) {
            guest_ass = true;
         }
      }

      if (guest_ass == true) {

         check = checkGuestQuota(parm, con);      // go check

         if (check == true) {          // if we hit on a violation

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Guest Quota Exceeded for Member</H3><BR>");
            out.println("<BR>Sorry, requesting <b>" + parm.player + "</b> exceeds the guest quota established by the Golf Shop.");
            out.println("<br><br>You will have to remove the guest in order to complete this request.");
            out.println("<br><br>Contact the Golf Shop if you have any questions.<br>");
            //
            //  Return to _sheet
            //
            skipValue = 12;                 // set skip value for return

            goReturnPrompt(skipValue, out, parm, overrideAccess, "");
            return;
         }

         //
         //*******************************************************************************************
         //
         //   Guests were included in the tee time and processed (and assigned) -
         //
         //        Now perform any guest related customs !!!!!!!!!!!!!!!
         //
         //*******************************************************************************************
         //
         String customGstMsg = checkCustomsGst(con, parm, user);     // go check for customs

         if (!customGstMsg.equals( "" )) {         // if error encountered - reject

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center><BR>");
            out.println( customGstMsg );           // add custom error msg
            out.println("<BR><BR>");

            skipValue = 12;                 // set skip value for return

            goReturnPrompt(skipValue, out, parm, overrideAccess, "");
            return;
         }

         //
         //  MOVE THE FOLLOWING CUSTOMS TO USE ABOVE PROCESS !!!!!!!!!!!!!!
         //




         if (club.equals( "merion" ) && parm.course.equals( "East" )) {    // if Merion, East course

            String checkPlayer = checkMerionGRes(con, out, parm);      // go check for custom guest quota violations

            if (!checkPlayer.equals( "" )) {

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Guest Time Quota Exceeded for Member</H3><BR>");
               out.println("<BR>Sorry, <b>" + checkPlayer + "</b> has already met the quota for Guest Times.");
               out.println("<br><br>You will have to remove the guest(s) in order to complete this request.");

               //
               //  Return to _sheet
               //
               skipValue = 12;                 // set skip value for return

               goReturnPrompt(skipValue, out, parm, overrideAccess, "");
               return;
            }
         }   // end of if Merion


         //
         //  Congressional Custom - check for guest quotas for 'Junior A' mships
         //
         if (club.equals( "congressional" )) {

            String congPlayer = checkCongressionalJrAGuests(parm);

            if (!congPlayer.equals( "" )) {

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Guest Quota Exceeded for Member</H3><BR>");
               out.println("<BR>Sorry, Junior A members (" +congPlayer+ ") can only have one guest per member");
               out.println("<br>on the Open Course on weekdays.");
               out.println("<br><br>You will have to remove the extra guest(s) in order to complete this request.");

               //
               //  Return to _sheet
               //
               skipValue = 12;                 // set skip value for return

               goReturnPrompt(skipValue, out, parm, overrideAccess, "");
               return;
            }
         }   // end of if Congressional


         //
         //  Custom for Oakmont -
         //      If Feb, Mar or Apr check if member already has 10 advance guest times scheduled (any time during year).
         //      If so, then reject.  Members can only reserve 10 guest times in each month, but the guest times can be any time
         //      during the season (advance times).  After Apr they can book an unlimited number of guest times.
         //
         //      The month (01 = Jan, 02 = Feb, etc.) is saved in custom_int so we know when the tee time was booked.
         //
         if (club.equals( "oakmont" )) {                  // oakmont

            if (thisMonth > 0 && thisMonth < 5) {         // if Jan, Feb, Mar or Apr (tee sheets closed in Jan, but check anyway)

               String oakPlayer = checkOakGuestQuota(parm, thisMonth, con);

               if (!oakPlayer.equals( "" )) {

                   out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
                   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                   out.println("<hr width=\"40%\">");
                   out.println("<BR><H3>Monthly Guest Quota Exceeded</H3><BR>");
                   out.println("<BR><BR>Sorry,  " +oakPlayer+ " has already scheduled the max allowed guest times this month.<BR>");
                   out.println("There is a limit to the number of advance guest rounds that can be scheduled in Feb, Mar, and Apr.");

                   skipValue = 12;                 // set skip value for return
                   goReturnPrompt(skipValue, out, parm, overrideAccess, "");
                   return;
               }
            }

            parm.custom_int1 = thisMonth;           // save this month value for each tee time in teecurr
            parm.custom_int2 = thisMonth;
            parm.custom_int3 = thisMonth;
            parm.custom_int4 = thisMonth;
            parm.custom_int5 = thisMonth;

         }          // end of IF oakmont and new tee time request


         //
         //  Baltusrol Guest Quota
         //
         if (club.equals( "baltusrolgc" )) {                  // Baltusrol

            String baltPlayer = checkBaltGuestQuota(parm, con);

            if (!baltPlayer.equals( "" )) {

                out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
                out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                out.println("<hr width=\"40%\">");
                out.println("<BR><H3>Guest Quota Exceeded</H3><BR>");
                out.println("<BR><BR>Sorry,  " +baltPlayer+ " has already scheduled the max allowed advance guest times.<BR>");
                out.println("There is a limit to the number of advance guest rounds that can be scheduled (3).");

                skipValue = 12;                 // set skip value for return
                goReturnPrompt(skipValue, out, parm, overrideAccess, "");
                return;
            }
         }          // end of IF baltusrol

         //
         //  Woodway guests not allowed for Restricted Golf mship
         //
         if (club.equals("woodway")) {

            String woodPlayer = checkWoodwayGuests(parm, con);

            if (!woodPlayer.equals("")) {

                out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                out.println("<hr width=\"40%\">");
                out.println("<BR><H3>Guests Restricted for Member</H3><BR>");
                out.println("<BR><BR>Sorry,  " + woodPlayer + " is not allowed to have a guest on this date.");
                out.println("<br><br>You will have to remove the guest(s) in order to complete this request.");

                skipValue = 12;                 // set skip value for return
                goReturnPrompt(skipValue, out, parm, overrideAccess, "");
                return;
            }

         }


      }     // end of IF guest assigned

   }                 // end of skip12


   //
   //  Before we update the tee time, go check for any flags to be added to members' names for the pro tee sheet
   //
   checkTFlag(parm, con);


   // If castle pines, check the birthdates to see if any are today, if they are, set the custom_disp for them to 'bday'
   if (club.equals("castlepines")) {
       if (Utilities.checkBirthday(parm.user1, parm.date, con)) parm.custom_disp1 = "bday";
       if (Utilities.checkBirthday(parm.user2, parm.date, con)) parm.custom_disp2 = "bday";
       if (Utilities.checkBirthday(parm.user3, parm.date, con)) parm.custom_disp3 = "bday";
       if (Utilities.checkBirthday(parm.user4, parm.date, con)) parm.custom_disp4 = "bday";
       if (Utilities.checkBirthday(parm.user5, parm.date, con)) parm.custom_disp5 = "bday";
       if (Utilities.checkBirthday(parm.user6, parm.date, con)) parm.custom_disp6 = "bday";
       if (Utilities.checkBirthday(parm.user7, parm.date, con)) parm.custom_disp7 = "bday";
       if (Utilities.checkBirthday(parm.user8, parm.date, con)) parm.custom_disp8 = "bday";
       if (Utilities.checkBirthday(parm.user9, parm.date, con)) parm.custom_disp9 = "bday";
       if (Utilities.checkBirthday(parm.user10, parm.date, con)) parm.custom_disp10 = "bday";
       if (Utilities.checkBirthday(parm.user11, parm.date, con)) parm.custom_disp11 = "bday";
       if (Utilities.checkBirthday(parm.user12, parm.date, con)) parm.custom_disp12 = "bday";
       if (Utilities.checkBirthday(parm.user13, parm.date, con)) parm.custom_disp13 = "bday";
       if (Utilities.checkBirthday(parm.user14, parm.date, con)) parm.custom_disp14 = "bday";
       if (Utilities.checkBirthday(parm.user15, parm.date, con)) parm.custom_disp15 = "bday";
       if (Utilities.checkBirthday(parm.user16, parm.date, con)) parm.custom_disp16 = "bday";
       if (Utilities.checkBirthday(parm.user17, parm.date, con)) parm.custom_disp17 = "bday";
       if (Utilities.checkBirthday(parm.user18, parm.date, con)) parm.custom_disp18 = "bday";
       if (Utilities.checkBirthday(parm.user19, parm.date, con)) parm.custom_disp19 = "bday";
       if (Utilities.checkBirthday(parm.user20, parm.date, con)) parm.custom_disp20 = "bday";
       if (Utilities.checkBirthday(parm.user21, parm.date, con)) parm.custom_disp21 = "bday";
       if (Utilities.checkBirthday(parm.user22, parm.date, con)) parm.custom_disp22 = "bday";
       if (Utilities.checkBirthday(parm.user23, parm.date, con)) parm.custom_disp23 = "bday";
       if (Utilities.checkBirthday(parm.user24, parm.date, con)) parm.custom_disp24 = "bday";
       if (Utilities.checkBirthday(parm.user25, parm.date, con)) parm.custom_disp25 = "bday";
   }

   //
   //  Wilmington Custom - check mship subtypes for those that have range privileges
   //
   if (club.equals( "wilmington" )) {

      verifyCustom.checkWilmington( parm );         // go flag mships
   }



   //
   //  Verification complete -
   //    Add tee times and send email notifications
   //
   int newTimes = 0;

   try {

      newTimes = verifySlot.addMteeTime(parm, con);

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");
      out.println("<BR><BR><H2>Database Access Error</H2>");
      out.println("<BR><BR>Error encountered while attempting to save the request.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      //
      //  Return to _slotm
      //
      goReturnPrompt(out, parm);
      return;
   }

   //
   //  Track the new tee times in the history table (teehist)
   //
   if (newTimes > 0) {      // if times added

      addHistm(parm, user, con);       // update the history
   }

   //  Attempt to add hosts for any accompanied tracked guests
   if (parm.guest_id1 > 0 && !parm.userg[0].equals("")) Common_guestdb.addHost(parm.guest_id1, parm.userg[0], con);
   if (parm.guest_id2 > 0 && !parm.userg[1].equals("")) Common_guestdb.addHost(parm.guest_id2, parm.userg[1], con);
   if (parm.guest_id3 > 0 && !parm.userg[2].equals("")) Common_guestdb.addHost(parm.guest_id3, parm.userg[2], con);
   if (parm.guest_id4 > 0 && !parm.userg[3].equals("")) Common_guestdb.addHost(parm.guest_id4, parm.userg[3], con);
   if (parm.guest_id5 > 0 && !parm.userg[4].equals("")) Common_guestdb.addHost(parm.guest_id5, parm.userg[4], con);
   if (parm.guest_id6 > 0 && !parm.userg[5].equals("")) Common_guestdb.addHost(parm.guest_id6, parm.userg[5], con);
   if (parm.guest_id7 > 0 && !parm.userg[6].equals("")) Common_guestdb.addHost(parm.guest_id7, parm.userg[6], con);
   if (parm.guest_id8 > 0 && !parm.userg[7].equals("")) Common_guestdb.addHost(parm.guest_id8, parm.userg[7], con);
   if (parm.guest_id9 > 0 && !parm.userg[8].equals("")) Common_guestdb.addHost(parm.guest_id9, parm.userg[8], con);
   if (parm.guest_id10 > 0 && !parm.userg[9].equals("")) Common_guestdb.addHost(parm.guest_id10, parm.userg[9], con);
   if (parm.guest_id11 > 0 && !parm.userg[10].equals("")) Common_guestdb.addHost(parm.guest_id11, parm.userg[10], con);
   if (parm.guest_id12 > 0 && !parm.userg[11].equals("")) Common_guestdb.addHost(parm.guest_id12, parm.userg[11], con);
   if (parm.guest_id13 > 0 && !parm.userg[12].equals("")) Common_guestdb.addHost(parm.guest_id13, parm.userg[12], con);
   if (parm.guest_id14 > 0 && !parm.userg[13].equals("")) Common_guestdb.addHost(parm.guest_id14, parm.userg[13], con);
   if (parm.guest_id15 > 0 && !parm.userg[14].equals("")) Common_guestdb.addHost(parm.guest_id15, parm.userg[14], con);
   if (parm.guest_id16 > 0 && !parm.userg[15].equals("")) Common_guestdb.addHost(parm.guest_id16, parm.userg[15], con);
   if (parm.guest_id17 > 0 && !parm.userg[16].equals("")) Common_guestdb.addHost(parm.guest_id17, parm.userg[16], con);
   if (parm.guest_id18 > 0 && !parm.userg[17].equals("")) Common_guestdb.addHost(parm.guest_id18, parm.userg[17], con);
   if (parm.guest_id19 > 0 && !parm.userg[18].equals("")) Common_guestdb.addHost(parm.guest_id19, parm.userg[18], con);
   if (parm.guest_id20 > 0 && !parm.userg[19].equals("")) Common_guestdb.addHost(parm.guest_id20, parm.userg[19], con);
   if (parm.guest_id21 > 0 && !parm.userg[20].equals("")) Common_guestdb.addHost(parm.guest_id21, parm.userg[20], con);
   if (parm.guest_id22 > 0 && !parm.userg[21].equals("")) Common_guestdb.addHost(parm.guest_id22, parm.userg[21], con);
   if (parm.guest_id23 > 0 && !parm.userg[22].equals("")) Common_guestdb.addHost(parm.guest_id23, parm.userg[22], con);
   if (parm.guest_id24 > 0 && !parm.userg[23].equals("")) Common_guestdb.addHost(parm.guest_id24, parm.userg[23], con);
   if (parm.guest_id25 > 0 && !parm.userg[24].equals("")) Common_guestdb.addHost(parm.guest_id25, parm.userg[24], con);

   //
   //  Build the HTML page to confirm reservation for user
   //
   //
   msgDate = yy + "-" + mm + "-" + parm.dd;
   msgPlayerCount = 1;  // count player1 by default
   if (!parm.player2.equals("")) { msgPlayerCount++; }
   if (!parm.player3.equals("")) { msgPlayerCount++; }
   if (!parm.player4.equals("")) { msgPlayerCount++; }
   if (!parm.player5.equals("")) { msgPlayerCount++; }
   if (!parm.player6.equals("")) { msgPlayerCount++; }
   if (!parm.player7.equals("")) { msgPlayerCount++; }
   if (!parm.player8.equals("")) { msgPlayerCount++; }
   if (!parm.player9.equals("")) { msgPlayerCount++; }
   if (!parm.player10.equals("")) { msgPlayerCount++; }
   if (!parm.player11.equals("")) { msgPlayerCount++; }
   if (!parm.player12.equals("")) { msgPlayerCount++; }
   if (!parm.player13.equals("")) { msgPlayerCount++; }
   if (!parm.player14.equals("")) { msgPlayerCount++; }
   if (!parm.player15.equals("")) { msgPlayerCount++; }
   if (!parm.player16.equals("")) { msgPlayerCount++; }
   if (!parm.player17.equals("")) { msgPlayerCount++; }
   if (!parm.player18.equals("")) { msgPlayerCount++; }
   if (!parm.player19.equals("")) { msgPlayerCount++; }
   if (!parm.player20.equals("")) { msgPlayerCount++; }
   if (!parm.player21.equals("")) { msgPlayerCount++; }
   if (!parm.player22.equals("")) { msgPlayerCount++; }
   if (!parm.player23.equals("")) { msgPlayerCount++; }
   if (!parm.player24.equals("")) { msgPlayerCount++; }
   if (!parm.player25.equals("")) { msgPlayerCount++; }

   out.println("<HTML>");
   out.println("<HEAD>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
   out.println("<Title>Proshop Tee Slot Page</Title>");
   if (skipDining.equalsIgnoreCase("yes")) {
       if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
          out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_jump?index=" + index + "&course=" + parm.returnCourse + "&jump=" + parm.jump + "&showlott=" + parm.showlott + "\">");
       } else {
          out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_jump?index=" + index + "&course=" + parm.course + "&jump=" + parm.jump + "&showlott=" + parm.showlott + "\">");
       }
   }
   out.println("</HEAD>");
   out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<center><img src=\"/" +rev+ "/images/foretees.gif\"><hr width=\"40%\">");
   out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<p>&nbsp;</p><p><b>Thank you!</b>&nbsp;&nbsp;Your request has been accepted and processed.</p>");
   out.println("</font><font size=\"2\">");
   out.println("<br><p>You will be able to access each tee time individually should changes be required.</p>");

   //
   //  Print dining request prompt
   //
   if (Utilities.checkDiningLink("pro_teetime", con) && req.getParameter("remove") == null && diningAccess) {
       String dCourse = "";
       if (!parm.returnCourse.equals("")) {
           dCourse = parm.returnCourse;
       } else {
           dCourse = parm.course;
       }
       Utilities.printDiningPrompt(out, con, msgDate, parm.day, parm.user1, msgPlayerCount, "teetime", "&index=" + index + "&course=" + dCourse + "&jump=" + parm.jump, true);
   }

   if (notesL > 254) {

      out.println("</font><font size=\"3\">");
      out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
   }
   out.println("<p>There were " +newTimes+ " tee times reserved.</p>");
   out.println("<p>&nbsp;</p></font>");
   out.println("<font size=\"2\">");
   out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
   if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.returnCourse + "\">");
   } else {
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
   }
   out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
   out.println("<input type=\"hidden\" name=\"showlott\" value=" + showlott + ">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</form></font>");
   out.println("</center></font></body></html>");
   out.close();

   try {

      resp.flushBuffer();      // force the repsonse to complete

   }
   catch (Exception ignore) {
   }

   //
   //***********************************************
   //  Send email notifications
   //***********************************************
   //
   if (suppressEmails.equalsIgnoreCase("no")) {
       sendMail(con, parm, user);        // send emails
   }


 }       // end of Verify
 // *******************************************************************************


 // *******************************************************************************
 //  Track all tee times - add to history
 // *******************************************************************************
 //
 private void addHistm(parmSlotm parm, String user, Connection con) {


   String p1 = "";
   String p2 = "";
   String p3 = "";
   String p4 = "";
   String p5 = "";
   String fullName = "Proshop User";

   int count = parm.slots;    // # of tee times to be added

   p1 = parm.player1;
   p2 = parm.player2;
   p3 = parm.player3;
   p4 = parm.player4;

   if (parm.p5.equals( "Yes" )) {

      p5 = parm.player5;

   } else {

      p5 = "";
   }

   //
   //  track the new tee time
   //
   SystemUtils.updateHist(parm.date, parm.day, parm.time1, parm.fb, parm.course, p1, p2, p3,
                          p4, p5, user, fullName, 0, con);


   //
   //  Add next tee time, if present
   //
   if (count > 1) {

      if (parm.p5.equals( "Yes" )) {

         p1 = parm.player6;
         p2 = parm.player7;
         p3 = parm.player8;
         p4 = parm.player9;
         p5 = parm.player10;

      } else {

         p1 = parm.player5;
         p2 = parm.player6;
         p3 = parm.player7;
         p4 = parm.player8;
         p5 = "";
      }

      //
      //  track the new tee time if players exist
      //
      if (!p1.equals( "" ) || !p2.equals( "" ) || !p3.equals( "" ) || !p4.equals( "" ) || !p5.equals( "" )) {

         SystemUtils.updateHist(parm.date, parm.day, parm.time2, parm.fb, parm.course, p1, p2, p3,
                                p4, p5, user, fullName, 0, con);
      }
   }

   //
   //  Add next tee time, if present
   //
   if (count > 2) {

      if (parm.p5.equals( "Yes" )) {

         p1 = parm.player11;
         p2 = parm.player12;
         p3 = parm.player13;
         p4 = parm.player14;
         p5 = parm.player15;

      } else {

         p1 = parm.player9;
         p2 = parm.player10;
         p3 = parm.player11;
         p4 = parm.player12;
         p5 = "";
      }

      //
      //  track the new tee time if players exist
      //
      if (!p1.equals( "" ) || !p2.equals( "" ) || !p3.equals( "" ) || !p4.equals( "" ) || !p5.equals( "" )) {

         SystemUtils.updateHist(parm.date, parm.day, parm.time3, parm.fb, parm.course, p1, p2, p3,
                                p4, p5, user, fullName, 0, con);
      }
   }

   //
   //  Add next tee time, if present
   //
   if (count > 3) {

      if (parm.p5.equals( "Yes" )) {

         p1 = parm.player16;
         p2 = parm.player17;
         p3 = parm.player18;
         p4 = parm.player19;
         p5 = parm.player20;

      } else {

         p1 = parm.player13;
         p2 = parm.player14;
         p3 = parm.player15;
         p4 = parm.player16;
         p5 = "";
      }

      //
      //  track the new tee time if players exist
      //
      if (!p1.equals( "" ) || !p2.equals( "" ) || !p3.equals( "" ) || !p4.equals( "" ) || !p5.equals( "" )) {

         SystemUtils.updateHist(parm.date, parm.day, parm.time4, parm.fb, parm.course, p1, p2, p3,
                                p4, p5, user, fullName, 0, con);
      }
   }

   //
   //  Add next tee time, if present
   //
   if (count > 4) {

      if (parm.p5.equals( "Yes" )) {

         p1 = parm.player21;
         p2 = parm.player22;
         p3 = parm.player23;
         p4 = parm.player24;
         p5 = parm.player25;

      } else {

         p1 = parm.player17;
         p2 = parm.player18;
         p3 = parm.player19;
         p4 = parm.player20;
         p5 = "";
      }

      //
      //  track the new tee time if players exist
      //
      if (!p1.equals( "" ) || !p2.equals( "" ) || !p3.equals( "" ) || !p4.equals( "" ) || !p5.equals( "" )) {

         SystemUtils.updateHist(parm.date, parm.day, parm.time5, parm.fb, parm.course, p1, p2, p3,
                                p4, p5, user, fullName, 0, con);
      }
   }

 }       // end of addHistm


 // *******************************************************************************
 //  Check if request is in use by user
 // *******************************************************************************
 //
 private void checkInUseBy(Connection con, parmSlotm parm) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   //
   //  Check if this request is still 'in use' and still in use by this user??
   //
   //  This is necessary because the user may have gone away while holding this req.  If the
   //  slot timed out (system timer), the slot would be marked 'not in use' and another
   //  user could pick it up.  The original holder could be trying to use it now.
   //
   try {

      pstmt = con.prepareStatement (
         "SELECT teecurr_id, in_use, in_use_by " +
         "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, parm.date);         // put the parm in pstmt
      pstmt.setInt(2, parm.time1);
      pstmt.setInt(3, parm.fb);
      pstmt.setString(4, parm.course);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         parm.teecurr_idA[0] = rs.getInt(1);
         parm.in_use = rs.getInt(2);
         parm.in_use_by = rs.getString(3);
      }
      pstmt.close();

      //
      //  Get the teecurr_id's of the others
      //
      if (parm.time2 > 0) {         // if another tee time

         pstmt = con.prepareStatement (
            "SELECT teecurr_id " +
            "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, parm.date);         // put the parm in pstmt
         pstmt.setInt(2, parm.time2);
         pstmt.setInt(3, parm.fb);
         pstmt.setString(4, parm.course);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.teecurr_idA[1] = rs.getInt(1);
         }
         pstmt.close();
      }

      if (parm.time3 > 0) {         // if another tee time

         pstmt = con.prepareStatement (
            "SELECT teecurr_id " +
            "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, parm.date);         // put the parm in pstmt
         pstmt.setInt(2, parm.time3);
         pstmt.setInt(3, parm.fb);
         pstmt.setString(4, parm.course);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.teecurr_idA[2] = rs.getInt(1);
         }
         pstmt.close();
      }

      if (parm.time4 > 0) {         // if another tee time

         pstmt = con.prepareStatement (
            "SELECT teecurr_id " +
            "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, parm.date);         // put the parm in pstmt
         pstmt.setInt(2, parm.time4);
         pstmt.setInt(3, parm.fb);
         pstmt.setString(4, parm.course);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.teecurr_idA[3] = rs.getInt(1);
         }
         pstmt.close();
      }

      if (parm.time5 > 0) {         // if another tee time

         pstmt = con.prepareStatement (
            "SELECT teecurr_id " +
            "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, parm.date);         // put the parm in pstmt
         pstmt.setInt(2, parm.time5);
         pstmt.setInt(3, parm.fb);
         pstmt.setString(4, parm.course);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            parm.teecurr_idA[4] = rs.getInt(1);
         }
         pstmt.close();
      }

   }
   catch (Exception ignore) {         // let next test catch any errors
   }

 }       // end of checkInUseBy


 // *******************************************************************************
 //  Count X's and check against max allowed and time frame allowed
 // *******************************************************************************
 //
 private boolean checkXcount(PrintWriter out, parmSlotm parm, parmClub parm2) {


   boolean error = false;

   int count1 = 0;
   int count2 = 0;
   int count3 = 0;
   int count4 = 0;
   int count5 = 0;

   int x = parm2.x;                // get max allowed X's per group
   int xhrs = parm2.xhrs;          // get hours in advance that x's are allowed


   //
   //  Count the number of X's in each group
   //
   if (parm.p5.equals( "Yes" )) {          // if 5-somes allowed

      if (parm.player1.equalsIgnoreCase( "x" )) {

         count1++;
      }
      if (parm.player2.equalsIgnoreCase( "x" )) {

         count1++;
      }
      if (parm.player3.equalsIgnoreCase( "x" )) {

         count1++;
      }
      if (parm.player4.equalsIgnoreCase( "x" )) {

         count1++;
      }
      if (parm.player5.equalsIgnoreCase( "x" )) {

         count1++;
      }

      if (parm.player6.equalsIgnoreCase( "x" )) {       // count 2nd group

         count2++;
      }
      if (parm.player7.equalsIgnoreCase( "x" )) {

         count2++;
      }
      if (parm.player8.equalsIgnoreCase( "x" )) {

         count2++;
      }
      if (parm.player9.equalsIgnoreCase( "x" )) {

         count2++;
      }
      if (parm.player10.equalsIgnoreCase( "x" )) {

         count2++;
      }

      if (parm.player11.equalsIgnoreCase( "x" )) {       // count 3rd group

         count3++;
      }
      if (parm.player12.equalsIgnoreCase( "x" )) {

         count3++;
      }
      if (parm.player13.equalsIgnoreCase( "x" )) {

         count3++;
      }
      if (parm.player14.equalsIgnoreCase( "x" )) {

         count3++;
      }
      if (parm.player15.equalsIgnoreCase( "x" )) {

         count3++;
      }

      if (parm.player16.equalsIgnoreCase( "x" )) {       // count 4th group

         count4++;
      }
      if (parm.player17.equalsIgnoreCase( "x" )) {

         count4++;
      }
      if (parm.player18.equalsIgnoreCase( "x" )) {

         count4++;
      }
      if (parm.player19.equalsIgnoreCase( "x" )) {

         count4++;
      }
      if (parm.player20.equalsIgnoreCase( "x" )) {

         count4++;
      }

      if (parm.player21.equalsIgnoreCase( "x" )) {          // count 5th group

         count5++;
      }
      if (parm.player22.equalsIgnoreCase( "x" )) {

         count5++;
      }
      if (parm.player23.equalsIgnoreCase( "x" )) {

         count5++;
      }
      if (parm.player24.equalsIgnoreCase( "x" )) {

         count5++;
      }
      if (parm.player25.equalsIgnoreCase( "x" )) {

         count5++;
      }

   } else {                                // 4-somes only

      if (parm.player1.equalsIgnoreCase( "x" )) {

         count1++;
      }
      if (parm.player2.equalsIgnoreCase( "x" )) {

         count1++;
      }
      if (parm.player3.equalsIgnoreCase( "x" )) {

         count1++;
      }
      if (parm.player4.equalsIgnoreCase( "x" )) {

         count1++;
      }

      if (parm.player5.equalsIgnoreCase( "x" )) {       // count 2nd group

         count2++;
      }
      if (parm.player6.equalsIgnoreCase( "x" )) {

         count2++;
      }
      if (parm.player7.equalsIgnoreCase( "x" )) {

         count2++;
      }
      if (parm.player8.equalsIgnoreCase( "x" )) {

         count2++;
      }

      if (parm.player9.equalsIgnoreCase( "x" )) {         // count 3rd group

         count3++;
      }
      if (parm.player10.equalsIgnoreCase( "x" )) {

         count3++;
      }
      if (parm.player11.equalsIgnoreCase( "x" )) {

         count3++;
      }
      if (parm.player12.equalsIgnoreCase( "x" )) {

         count3++;
      }

      if (parm.player13.equalsIgnoreCase( "x" )) {        // count 4th group

         count4++;
      }
      if (parm.player14.equalsIgnoreCase( "x" )) {

         count4++;
      }
      if (parm.player15.equalsIgnoreCase( "x" )) {

         count4++;
      }
      if (parm.player16.equalsIgnoreCase( "x" )) {

         count4++;
      }

      if (parm.player17.equalsIgnoreCase( "x" )) {        // count 5th group

         count5++;
      }
      if (parm.player18.equalsIgnoreCase( "x" )) {

         count5++;
      }
      if (parm.player19.equalsIgnoreCase( "x" )) {

         count5++;
      }
      if (parm.player20.equalsIgnoreCase( "x" )) {

         count5++;
      }
   }

   //
   //   Verify the date/time - are X's allowed
   //
   if (count1 > 0 || count2 > 0 || count3 > 0 || count4 > 0 || count5 > 0) {

      if (xhrs > 0) {           // if limit was specified

         //
         //  Set date/time values to be used to check for X's in tee sheet
         //
         //  Get today's date and then go up by 'xhrs' hours
         //
         Calendar cal = new GregorianCalendar();       // get todays date

         cal.add(Calendar.HOUR_OF_DAY,xhrs);           // roll ahead 'xhrs' hours (rest should adjust)

         int calYear = cal.get(Calendar.YEAR);
         int calMonth = cal.get(Calendar.MONTH);
         int calDay = cal.get(Calendar.DAY_OF_MONTH);
         int calHr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value
         int calMin = cal.get(Calendar.MINUTE);

         calMonth = calMonth + 1;                             // month starts at zero

         long adv_date = calYear * 10000;                     // create a date field of yyyymmdd
         adv_date = adv_date + (calMonth * 100);
         adv_date = adv_date + calDay;                        // date = yyyymmdd (for comparisons)

         int adv_time = calHr * 100;                          // create time field of hhmm
         adv_time = adv_time + calMin;

         //
         //  Compare the tee time's date/time to the X deadline
         //
         if ((parm.date < adv_date) || ((parm.date == adv_date) && (parm.time1 <= adv_time))) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>Sorry, 'X' is not allowed at this time.");
            out.println("<BR><BR>It is too close to the actual tee time to reserve positions with an X.");
            out.println("<BR><BR>Please correct this and try again.");
            out.println("<BR><BR>");

            goReturnPrompt(out, parm);
            error = true;               // return error
         }
      }
   }

   if (error == false) {          // if still ok

      //
      //   Verify the counts
      //
      if (count1 > x || count2 > x || count3 > x || count4 > x || count5 > x) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR>You are attempting to reserve more player positions than allowed.");
         out.println("<BR><BR>The maximum number of X's allowed per group is " +x+ ".");
         out.println("<BR><BR>Please correct this and try again.");
         out.println("<BR><BR>");

         goReturnPrompt(out, parm);
         error = true;               // return error
      }
   }

   return(error);
 }


 // *******************************************************************************
 //  Parse Member Names and identify Guests
 // *******************************************************************************
 //
 private String parseNames(PrintWriter out, parmSlotm parm, parmClub parm2, Connection con) {

   ResultSet rs = null;

   StringTokenizer tok = null;

   String fname = "";
   String lname = "";
   String mi = "";
   String gplayer = "";
   String user = "";
   String error = "";

   int i = 0;

   boolean found = false;
   boolean invalidGuest = false;
   boolean guestdbTbaAllowed = false;


   //  If guest tracking is in use, determine whether names are optional or required
   if (Utilities.isGuestTrackingConfigured(0, con) && Utilities.isGuestTrackingTbaAllowed(0, true, con)) {
       guestdbTbaAllowed = true;
   }

   //
   //   Remove any guest types that are null - for tests below
   //
   for (i = 0; i < parm2.MAX_Guests; i++) {

      if (parm2.guest[i].equals( "" )) {

         parm2.guest[i] = "$@#!^&*";      // make so it won't match player name
      }
   }         // end of while loop

   i = 0;
   while (i < 25) {

      parm.gstA[i] = "";    // init guest array and indicators
      i++;
   }


   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT username, m_ship, m_type, g_hancap, memNum, msub_type, name_last, name_first, name_mi " +
         "FROM member2b " +
         "WHERE name_last = ? AND name_first = ? AND name_mi = ?");

      //
      //  Check each player for Member or Guest
      //
      parm.g[0] = "";
      gplayer = "";
      parm.userg[0] = "";
      found = false;
      if (!parm.player1.equals( "" ) && !parm.player1.equalsIgnoreCase( "x" )) {      // if not empty and not 'x'

         i = 0;
         loop1:
         while (i < parm2.MAX_Guests) {                                                     // is it a guest ?

            if (parm.player1.startsWith( parm2.guest[i] )) {

               parm.g[0] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[0] = parm.player1;       // save guest value
               parm.guests++;                     // increment number of guests this request
               parm.guestsg1++;                   // increment number of guests this slot

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id1 != 0 || !parm.player1.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id1 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player1, parm.guest_id1, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player1;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               break loop1;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player1 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player1;
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
                  parm.hndcp1 = rs.getFloat(4);
                  parm.mNum1 = rs.getString(5);
                  parm.mstype1 = rs.getString(6);
                  parm.lname1 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname1 = rs.getString(8);
                  parm.mi1 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  parm.memg1++;           // group 1
                  user = parm.user1;      // save as last member (for guests)

                  parm.player1 = buildName(parm.lname1, parm.fname1, parm.mi1);    // rebuild the player name using the db name values

               } else {
                  parm.inval1 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[1] = "";
      parm.userg[1] = "";
      found = false;
      if (!parm.player2.equals( "" ) && !parm.player2.equalsIgnoreCase( "x" )) {

         i = 0;
         loop2:
         while (i < parm2.MAX_Guests) {

            if (parm.player2.startsWith( parm2.guest[i] )) {

               parm.g[1] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[1] = parm.player2;    // save guest value
               parm.guests++;                 // increment number of guests this request
               parm.guestsg1++;                 // increment number of guests this slot

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id2 != 0 || !parm.player2.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id2 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player2, parm.guest_id2, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player2;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[1] = user;             // set guest of last member found
               break loop2;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player2 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player2;
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
                  parm.hndcp2 = rs.getFloat(4);
                  parm.mNum2 = rs.getString(5);
                  parm.mstype2 = rs.getString(6);
                  parm.lname2 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname2 = rs.getString(8);
                  parm.mi2 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  parm.memg1++;           // group 1
                  user = parm.user2;      // save as last member (for guests)

                  parm.player2 = buildName(parm.lname2, parm.fname2, parm.mi2);    // rebuild the player name using the db name values

               } else {
                  parm.inval2 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[2] = "";
      parm.userg[2] = "";
      found = false;
      if (!parm.player3.equals( "" ) && !parm.player3.equalsIgnoreCase( "x" )) {

         i = 0;
         loop3:
         while (i < parm2.MAX_Guests) {

            if (parm.player3.startsWith( parm2.guest[i] )) {

               parm.g[2] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[2] = parm.player3;    // save guest value
               parm.guests++;                 // increment number of guests this request
               parm.guestsg1++;                 // increment number of guests this slot

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id3 != 0 || !parm.player3.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id3 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player3, parm.guest_id3, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player3;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[2] = user;             // set guest of last member found
               break loop3;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member


            tok = new StringTokenizer( parm.player3 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player3;
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
                  parm.hndcp3 = rs.getFloat(4);
                  parm.mNum3 = rs.getString(5);
                  parm.mstype3 = rs.getString(6);
                  parm.lname3 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname3 = rs.getString(8);
                  parm.mi3 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  parm.memg1++;           // group 1
                  user = parm.user3;      // save as last member (for guests)

                  parm.player3 = buildName(parm.lname3, parm.fname3, parm.mi3);    // rebuild the player name using the db name values

               } else {
                  parm.inval3 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[3] = "";
      parm.userg[3] = "";
      found = false;
      if (!parm.player4.equals( "" ) && !parm.player4.equalsIgnoreCase( "x" )) {

         i = 0;
         loop4:
         while (i < parm2.MAX_Guests) {

            if (parm.player4.startsWith( parm2.guest[i] )) {

               parm.g[3] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[3] = parm.player4;    // save guest value
               parm.guests++;                 // increment number of guests this request
               parm.guestsg1++;                 // increment number of guests this slot

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id4 != 0 || !parm.player4.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id4 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player4, parm.guest_id4, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player4;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[3] = user;             // set guest of last member found
               break loop4;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player4 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player4;
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
                  parm.hndcp4 = rs.getFloat(4);
                  parm.mNum4 = rs.getString(5);
                  parm.mstype4 = rs.getString(6);
                  parm.lname4 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname4 = rs.getString(8);
                  parm.mi4 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  parm.memg1++;           // group 1
                  user = parm.user4;      // save as last member (for guests)

                  parm.player4 = buildName(parm.lname4, parm.fname4, parm.mi4);    // rebuild the player name using the db name values

               } else {
                  parm.inval4 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[4] = "";
      parm.userg[4] = "";
      found = false;
      if (!parm.player5.equals( "" ) && !parm.player5.equalsIgnoreCase( "x" )) {

         i = 0;
         loop5:
         while (i < parm2.MAX_Guests) {

            if (parm.player5.startsWith( parm2.guest[i] )) {

               parm.g[4] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[4] = parm.player5;    // save guest value
               parm.guests++;                 // increment number of guests this request
               if (parm.p5.equals( "Yes" )) {
                  parm.guestsg1++;                 // increment number of guests this slot
               } else {
                  parm.guestsg2++;                 // increment number of guests this slot
               }

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id5 != 0 || !parm.player5.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id5 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player5, parm.guest_id5, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player5;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[4] = user;             // set guest of last member found
               break loop5;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player5 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player5;
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
                  parm.hndcp5 = rs.getFloat(4);
                  parm.mNum5 = rs.getString(5);
                  parm.mstype5 = rs.getString(6);
                  parm.lname5 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname5 = rs.getString(8);
                  parm.mi5 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  if (parm.p5.equals( "Yes" )) {
                     parm.memg1++;           // group 1
                  } else {
                     parm.memg2++;           // group 2
                  }
                  user = parm.user5;      // save as last member (for guests)

                  parm.player5 = buildName(parm.lname5, parm.fname5, parm.mi5);    // rebuild the player name using the db name values

               } else {
                  parm.inval5 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[5] = "";
      parm.userg[5] = "";
      found = false;
      if (!parm.player6.equals( "" ) && !parm.player6.equalsIgnoreCase( "x" )) {

         i = 0;
         loop6:
         while (i < parm2.MAX_Guests) {

            if (parm.player6.startsWith( parm2.guest[i] )) {

               parm.g[5] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[5] = parm.player6;    // save guest value
               parm.guests++;                 // increment number of guests this request
               parm.guestsg2++;                 // increment number of guests this slot

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id6 != 0 || !parm.player6.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id6 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player6, parm.guest_id6, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player6;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[5] = user;             // set guest of last member found
               break loop6;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player6 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player6;
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
                  parm.hndcp6 = rs.getFloat(4);
                  parm.mNum6 = rs.getString(5);
                  parm.mstype6 = rs.getString(6);
                  parm.lname6 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname6 = rs.getString(8);
                  parm.mi6 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  parm.memg2++;           // group 2
                  user = parm.user6;      // save as last member (for guests)

                  parm.player6 = buildName(parm.lname6, parm.fname6, parm.mi6);    // rebuild the player name using the db name values

               } else {
                  parm.inval6 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[6] = "";
      parm.userg[6] = "";
      found = false;
      if (!parm.player7.equals( "" ) && !parm.player7.equalsIgnoreCase( "x" )) {

         i = 0;
         loop7:
         while (i < parm2.MAX_Guests) {

            if (parm.player7.startsWith( parm2.guest[i] )) {

               parm.g[6] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[6] = parm.player7;    // save guest value
               parm.guests++;                 // increment number of guests this request
               parm.guestsg2++;                 // increment number of guests this slot

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id7 != 0 || !parm.player7.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id7 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player7, parm.guest_id7, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player7;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[6] = user;             // set guest of last member found
               break loop7;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player7 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player7;
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
                  parm.hndcp7 = rs.getFloat(4);
                  parm.mNum7 = rs.getString(5);
                  parm.mstype7 = rs.getString(6);
                  parm.lname7 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname7 = rs.getString(8);
                  parm.mi7 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  parm.memg2++;           // group 2
                  user = parm.user7;      // save as last member (for guests)

                  parm.player7 = buildName(parm.lname7, parm.fname7, parm.mi7);    // rebuild the player name using the db name values

               } else {
                  parm.inval7 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[7] = "";
      parm.userg[7] = "";
      found = false;
      if (!parm.player8.equals( "" ) && !parm.player8.equalsIgnoreCase( "x" )) {

         i = 0;
         loop8:
         while (i < parm2.MAX_Guests) {

            if (parm.player8.startsWith( parm2.guest[i] )) {

               parm.g[7] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[7] = parm.player8;    // save guest value
               parm.guests++;                 // increment number of guests this request
               parm.guestsg2++;                 // increment number of guests this slot

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id8 != 0 || !parm.player8.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id8 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player8, parm.guest_id8, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player8;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[7] = user;             // set guest of last member found
               break loop8;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player8 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player8;
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
                  parm.hndcp8 = rs.getFloat(4);
                  parm.mNum8 = rs.getString(5);
                  parm.mstype8 = rs.getString(6);
                  parm.lname8 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname8 = rs.getString(8);
                  parm.mi8 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  parm.memg2++;           // group 2
                  user = parm.user8;      // save as last member (for guests)

                  parm.player8 = buildName(parm.lname8, parm.fname8, parm.mi8);    // rebuild the player name using the db name values

               } else {
                  parm.inval8 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[8] = "";
      parm.userg[8] = "";
      found = false;
      if (!parm.player9.equals( "" ) && !parm.player9.equalsIgnoreCase( "x" )) {

         i = 0;
         loop9:
         while (i < parm2.MAX_Guests) {

            if (parm.player9.startsWith( parm2.guest[i] )) {

               parm.g[8] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[8] = parm.player9;    // save guest value
               parm.guests++;                 // increment number of guests this request
               if (parm.p5.equals( "Yes" )) {
                  parm.guestsg2++;                 // increment number of guests this slot
               } else {
                  parm.guestsg3++;                 // increment number of guests this slot
               }

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id9 != 0 || !parm.player9.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id9 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player9, parm.guest_id9, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player9;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[8] = user;             // set guest of last member found
               break loop9;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player9 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player9;
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
                  parm.hndcp9 = rs.getFloat(4);
                  parm.mNum9 = rs.getString(5);
                  parm.mstype9 = rs.getString(6);
                  parm.lname9 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname9 = rs.getString(8);
                  parm.mi9 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  if (parm.p5.equals( "Yes" )) {
                     parm.memg2++;           // group 2
                  } else {
                     parm.memg3++;           // group 3
                  }
                  user = parm.user9;      // save as last member (for guests)

                  parm.player9 = buildName(parm.lname9, parm.fname9, parm.mi9);    // rebuild the player name using the db name values

               } else {
                  parm.inval9 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[9] = "";
      parm.userg[9] = "";
      found = false;
      if (!parm.player10.equals( "" ) && !parm.player10.equalsIgnoreCase( "x" )) {

         i = 0;
         loop10:
         while (i < parm2.MAX_Guests) {

            if (parm.player10.startsWith( parm2.guest[i] )) {

               parm.g[9] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[9] = parm.player10;    // save guest value
               parm.guests++;                 // increment number of guests this request
               if (parm.p5.equals( "Yes" )) {
                  parm.guestsg2++;                 // increment number of guests this slot
               } else {
                  parm.guestsg3++;                 // increment number of guests this slot
               }

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id10 != 0 || !parm.player10.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id10 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player10, parm.guest_id10, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player10;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[9] = user;             // set guest of last member found
               break loop10;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player10 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player10;
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
                  parm.hndcp10 = rs.getFloat(4);
                  parm.mNum10 = rs.getString(5);
                  parm.mstype10 = rs.getString(6);
                  parm.lname10 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname10 = rs.getString(8);
                  parm.mi10 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  if (parm.p5.equals( "Yes" )) {
                     parm.memg2++;           // group 2
                  } else {
                     parm.memg3++;           // group 3
                  }
                  user = parm.user10;      // save as last member (for guests)

                  parm.player10 = buildName(parm.lname10, parm.fname10, parm.mi10);    // rebuild the player name using the db name values

               } else {
                  parm.inval10 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[10] = "";
      parm.userg[10] = "";
      found = false;
      if (!parm.player11.equals( "" ) && !parm.player11.equalsIgnoreCase( "x" )) {

         i = 0;
         loop11:
         while (i < parm2.MAX_Guests) {

            if (parm.player11.startsWith( parm2.guest[i] )) {

               parm.g[10] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[10] = parm.player11;    // save guest value
               parm.guests++;                 // increment number of guests this request
               parm.guestsg3++;                 // increment number of guests this slot

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id11 != 0 || !parm.player11.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id11 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player11, parm.guest_id11, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player11;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[10] = user;             // set guest of last member found
               break loop11;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player11 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player11;
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
                  parm.hndcp11 = rs.getFloat(4);
                  parm.mNum11 = rs.getString(5);
                  parm.mstype11 = rs.getString(6);
                  parm.lname11 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname11 = rs.getString(8);
                  parm.mi11 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  parm.memg3++;           // group 3
                  user = parm.user11;      // save as last member (for guests)

                  parm.player11 = buildName(parm.lname11, parm.fname11, parm.mi11);    // rebuild the player name using the db name values

               } else {
                  parm.inval11 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[11] = "";
      parm.userg[11] = "";
      found = false;
      if (!parm.player12.equals( "" ) && !parm.player12.equalsIgnoreCase( "x" )) {

         i = 0;
         loop12:
         while (i < parm2.MAX_Guests) {

            if (parm.player12.startsWith( parm2.guest[i] )) {

               parm.g[11] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[11] = parm.player12;    // save guest value
               parm.guests++;                 // increment number of guests this request
               parm.guestsg3++;                 // increment number of guests this slot

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id12 != 0 || !parm.player12.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id12 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player12, parm.guest_id12, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player12;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[11] = user;             // set guest of last member found
               break loop12;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player12 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player12;
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
                  parm.hndcp12 = rs.getFloat(4);
                  parm.mNum12 = rs.getString(5);
                  parm.mstype12 = rs.getString(6);
                  parm.lname12 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname12 = rs.getString(8);
                  parm.mi12 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  parm.memg3++;           // group 3
                  user = parm.user12;      // save as last member (for guests)

                  parm.player12 = buildName(parm.lname12, parm.fname12, parm.mi12);    // rebuild the player name using the db name values

               } else {
                  parm.inval12 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[12] = "";
      parm.userg[12] = "";
      found = false;
      if (!parm.player13.equals( "" ) && !parm.player13.equalsIgnoreCase( "x" )) {

         i = 0;
         loop13:
         while (i < parm2.MAX_Guests) {

            if (parm.player13.startsWith( parm2.guest[i] )) {

               parm.g[12] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[12] = parm.player13;    // save guest value
               parm.guests++;                 // increment number of guests this request
               if (parm.p5.equals( "Yes" )) {
                  parm.guestsg3++;                 // increment number of guests this slot
               } else {
                  parm.guestsg4++;                 // increment number of guests this slot
               }

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id13 != 0 || !parm.player13.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id13 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player13, parm.guest_id13, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player13;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[12] = user;             // set guest of last member found
               break loop13;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player13 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player13;
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
                  parm.hndcp13 = rs.getFloat(4);
                  parm.mNum13 = rs.getString(5);
                  parm.mstype13 = rs.getString(6);
                  parm.lname13 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname13 = rs.getString(8);
                  parm.mi13 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  if (parm.p5.equals( "Yes" )) {
                     parm.memg3++;           // group 3
                  } else {
                     parm.memg4++;           // group 4
                  }
                  user = parm.user13;      // save as last member (for guests)

                  parm.player13 = buildName(parm.lname13, parm.fname13, parm.mi13);    // rebuild the player name using the db name values

               } else {
                  parm.inval13 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[13] = "";
      parm.userg[13] = "";
      found = false;
      if (!parm.player14.equals( "" ) && !parm.player14.equalsIgnoreCase( "x" )) {

         i = 0;
         loop14:
         while (i < parm2.MAX_Guests) {

            if (parm.player14.startsWith( parm2.guest[i] )) {

               parm.g[13] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[13] = parm.player14;    // save guest value
               parm.guests++;                 // increment number of guests this request
               if (parm.p5.equals( "Yes" )) {
                  parm.guestsg3++;                 // increment number of guests this slot
               } else {
                  parm.guestsg4++;                 // increment number of guests this slot
               }

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id14 != 0 || !parm.player14.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id14 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player14, parm.guest_id14, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player14;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[13] = user;             // set guest of last member found
               break loop14;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player14 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player14;
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
                  parm.hndcp14 = rs.getFloat(4);
                  parm.mNum14 = rs.getString(5);
                  parm.mstype14 = rs.getString(6);
                  parm.lname14 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname14 = rs.getString(8);
                  parm.mi14 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  if (parm.p5.equals( "Yes" )) {
                     parm.memg3++;           // group 3
                  } else {
                     parm.memg4++;           // group 4
                  }
                  user = parm.user14;      // save as last member (for guests)

                  parm.player14 = buildName(parm.lname14, parm.fname14, parm.mi14);    // rebuild the player name using the db name values

               } else {
                  parm.inval14 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[14] = "";
      parm.userg[14] = "";
      found = false;
      if (!parm.player15.equals( "" ) && !parm.player15.equalsIgnoreCase( "x" )) {

         i = 0;
         loop15:
         while (i < parm2.MAX_Guests) {

            if (parm.player15.startsWith( parm2.guest[i] )) {

               parm.g[14] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[14] = parm.player15;    // save guest value
               parm.guests++;                 // increment number of guests this request
               if (parm.p5.equals( "Yes" )) {
                  parm.guestsg3++;                 // increment number of guests this slot
               } else {
                  parm.guestsg4++;                 // increment number of guests this slot
               }

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id15 != 0 || !parm.player15.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id15 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player15, parm.guest_id15, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player15;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[14] = user;             // set guest of last member found
               break loop15;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player15 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player15;
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
                  parm.hndcp15 = rs.getFloat(4);
                  parm.mNum15 = rs.getString(5);
                  parm.mstype15 = rs.getString(6);
                  parm.lname15 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname15 = rs.getString(8);
                  parm.mi15 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  if (parm.p5.equals( "Yes" )) {
                     parm.memg3++;           // group 3
                  } else {
                     parm.memg4++;           // group 4
                  }
                  user = parm.user15;      // save as last member (for guests)

                  parm.player15 = buildName(parm.lname15, parm.fname15, parm.mi15);    // rebuild the player name using the db name values

               } else {
                  parm.inval15 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[15] = "";
      parm.userg[15] = "";
      found = false;
      if (!parm.player16.equals( "" ) && !parm.player16.equalsIgnoreCase( "x" )) {

         i = 0;
         loop16:
         while (i < parm2.MAX_Guests) {

            if (parm.player16.startsWith( parm2.guest[i] )) {

               parm.g[15] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[15] = parm.player16;    // save guest value
               parm.guests++;                 // increment number of guests this request
               parm.guestsg4++;                 // increment number of guests this slot

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id16 != 0 || !parm.player16.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id16 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player16, parm.guest_id16, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player16;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[15] = user;             // set guest of last member found
               break loop16;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player16 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player16;
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
                  parm.hndcp16 = rs.getFloat(4);
                  parm.mNum16 = rs.getString(5);
                  parm.mstype16 = rs.getString(6);
                  parm.lname16 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname16 = rs.getString(8);
                  parm.mi16 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  parm.memg4++;           // group 4
                  user = parm.user16;      // save as last member (for guests)

                  parm.player16 = buildName(parm.lname16, parm.fname16, parm.mi16);    // rebuild the player name using the db name values

               } else {
                  parm.inval16 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[16] = "";
      parm.userg[16] = "";
      found = false;
      if (!parm.player17.equals( "" ) && !parm.player17.equalsIgnoreCase( "x" )) {

         i = 0;
         loop17:
         while (i < parm2.MAX_Guests) {

            if (parm.player17.startsWith( parm2.guest[i] )) {

               parm.g[16] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[16] = parm.player17;    // save guest value
               parm.guests++;                 // increment number of guests this request
               if (parm.p5.equals( "Yes" )) {
                  parm.guestsg4++;                 // increment number of guests this slot
               } else {
                  parm.guestsg5++;                 // increment number of guests this slot
               }

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id17 != 0 || !parm.player17.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id17 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player17, parm.guest_id17, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player17;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[16] = user;             // set guest of last member found
               break loop17;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player17 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player17;
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
                  parm.hndcp17 = rs.getFloat(4);
                  parm.mNum17 = rs.getString(5);
                  parm.mstype17 = rs.getString(6);
                  parm.lname17 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname17 = rs.getString(8);
                  parm.mi17 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  if (parm.p5.equals( "Yes" )) {
                     parm.memg4++;           // group 4
                  } else {
                     parm.memg5++;           // group 5
                  }
                  user = parm.user17;      // save as last member (for guests)

                  parm.player17 = buildName(parm.lname17, parm.fname17, parm.mi17);    // rebuild the player name using the db name values

               } else {
                  parm.inval17 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[17] = "";
      parm.userg[17] = "";
      found = false;
      if (!parm.player18.equals( "" ) && !parm.player18.equalsIgnoreCase( "x" )) {

         i = 0;
         loop18:
         while (i < parm2.MAX_Guests) {

            if (parm.player18.startsWith( parm2.guest[i] )) {

               parm.g[17] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[17] = parm.player18;    // save guest value
               parm.guests++;                 // increment number of guests this request
               if (parm.p5.equals( "Yes" )) {
                  parm.guestsg4++;                 // increment number of guests this slot
               } else {
                  parm.guestsg5++;                 // increment number of guests this slot
               }

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id18 != 0 || !parm.player18.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id18 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player18, parm.guest_id18, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player18;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[17] = user;             // set guest of last member found
               break loop18;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player18 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player18;
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
                  parm.hndcp18 = rs.getFloat(4);
                  parm.mNum18 = rs.getString(5);
                  parm.mstype18 = rs.getString(6);
                  parm.lname18 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname18 = rs.getString(8);
                  parm.mi18 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  if (parm.p5.equals( "Yes" )) {
                     parm.memg4++;           // group 4
                  } else {
                     parm.memg5++;           // group 5
                  }
                  user = parm.user18;      // save as last member (for guests)

                  parm.player18 = buildName(parm.lname18, parm.fname18, parm.mi18);    // rebuild the player name using the db name values

               } else {
                  parm.inval18 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[18] = "";
      parm.userg[18] = "";
      found = false;
      if (!parm.player19.equals( "" ) && !parm.player19.equalsIgnoreCase( "x" )) {

         i = 0;
         loop19:
         while (i < parm2.MAX_Guests) {

            if (parm.player19.startsWith( parm2.guest[i] )) {

               parm.g[18] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[18] = parm.player19;    // save guest value
               parm.guests++;                 // increment number of guests this request
               if (parm.p5.equals( "Yes" )) {
                  parm.guestsg4++;                 // increment number of guests this slot
               } else {
                  parm.guestsg5++;                 // increment number of guests this slot
               }

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id19 != 0 || !parm.player19.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id19 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player19, parm.guest_id19, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player19;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[18] = user;             // set guest of last member found
               break loop19;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player19 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player19;
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
                  parm.hndcp19 = rs.getFloat(4);
                  parm.mNum19 = rs.getString(5);
                  parm.mstype19 = rs.getString(6);
                  parm.lname19 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname19 = rs.getString(8);
                  parm.mi19 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  if (parm.p5.equals( "Yes" )) {
                     parm.memg4++;           // group 4
                  } else {
                     parm.memg5++;           // group 5
                  }
                  user = parm.user19;      // save as last member (for guests)

                  parm.player19 = buildName(parm.lname19, parm.fname19, parm.mi19);    // rebuild the player name using the db name values

               } else {
                  parm.inval19 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[19] = "";
      parm.userg[19] = "";
      found = false;
      if (!parm.player20.equals( "" ) && !parm.player20.equalsIgnoreCase( "x" )) {

         i = 0;
         loop20:
         while (i < parm2.MAX_Guests) {

            if (parm.player20.startsWith( parm2.guest[i] )) {

               parm.g[19] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[19] = parm.player20;    // save guest value
               parm.guests++;                 // increment number of guests this request
               if (parm.p5.equals( "Yes" )) {
                  parm.guestsg4++;                 // increment number of guests this slot
               } else {
                  parm.guestsg5++;                 // increment number of guests this slot
               }

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id20 != 0 || !parm.player20.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id20 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player20, parm.guest_id20, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player20;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[19] = user;             // set guest of last member found
               break loop20;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player20 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player20;
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
                  parm.hndcp20 = rs.getFloat(4);
                  parm.mNum20 = rs.getString(5);
                  parm.mstype20 = rs.getString(6);
                  parm.lname20 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname20 = rs.getString(8);
                  parm.mi20 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  if (parm.p5.equals( "Yes" )) {
                     parm.memg4++;           // group 4
                  } else {
                     parm.memg5++;           // group 5
                  }
                  user = parm.user20;      // save as last member (for guests)

                  parm.player20 = buildName(parm.lname20, parm.fname20, parm.mi20);    // rebuild the player name using the db name values

               } else {
                  parm.inval20 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[20] = "";
      parm.userg[20] = "";
      found = false;
      if (!parm.player21.equals( "" ) && !parm.player21.equalsIgnoreCase( "x" )) {

         i = 0;
         loop21:
         while (i < parm2.MAX_Guests) {

            if (parm.player21.startsWith( parm2.guest[i] )) {

               parm.g[20] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[20] = parm.player21;    // save guest value
               parm.guests++;                 // increment number of guests this request
               parm.guestsg5++;                 // increment number of guests this slot

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id21 != 0 || !parm.player21.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id21 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player21, parm.guest_id21, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player21;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[20] = user;             // set guest of last member found
               break loop21;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player21 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player21;
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
                  parm.hndcp21 = rs.getFloat(4);
                  parm.mNum21 = rs.getString(5);
                  parm.mstype21 = rs.getString(6);
                  parm.lname21 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname21 = rs.getString(8);
                  parm.mi21 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  parm.memg5++;           // group 5
                  user = parm.user21;      // save as last member (for guests)

                  parm.player21 = buildName(parm.lname21, parm.fname21, parm.mi21);    // rebuild the player name using the db name values

               } else {
                  parm.inval21 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[21] = "";
      parm.userg[21] = "";
      found = false;
      if (!parm.player22.equals( "" ) && !parm.player22.equalsIgnoreCase( "x" )) {

         i = 0;
         loop22:
         while (i < parm2.MAX_Guests) {

            if (parm.player22.startsWith( parm2.guest[i] )) {

               parm.g[21] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[21] = parm.player22;    // save guest value
               parm.guests++;                 // increment number of guests this request
               parm.guestsg5++;                 // increment number of guests this slot

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id22 != 0 || !parm.player22.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id22 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player22, parm.guest_id22, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player22;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[21] = user;             // set guest of last member found
               break loop22;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player22 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player22;
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
                  parm.hndcp22 = rs.getFloat(4);
                  parm.mNum22 = rs.getString(5);
                  parm.mstype22 = rs.getString(6);
                  parm.lname22 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname22 = rs.getString(8);
                  parm.mi22 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  parm.memg5++;           // group 5
                  user = parm.user22;      // save as last member (for guests)

                  parm.player22 = buildName(parm.lname22, parm.fname22, parm.mi22);    // rebuild the player name using the db name values

               } else {
                  parm.inval22 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[22] = "";
      parm.userg[22] = "";
      found = false;
      if (!parm.player23.equals( "" ) && !parm.player23.equalsIgnoreCase( "x" )) {

         i = 0;
         loop23:
         while (i < parm2.MAX_Guests) {

            if (parm.player23.startsWith( parm2.guest[i] )) {

               parm.g[22] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[22] = parm.player23;    // save guest value
               parm.guests++;                 // increment number of guests this request
               parm.guestsg5++;                 // increment number of guests this slot

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id23 != 0 || !parm.player23.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id23 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player23, parm.guest_id23, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player23;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[22] = user;             // set guest of last member found
               break loop23;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player23 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player23;
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
                  parm.hndcp23 = rs.getFloat(4);
                  parm.mNum23 = rs.getString(5);
                  parm.mstype23 = rs.getString(6);
                  parm.lname23 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname23 = rs.getString(8);
                  parm.mi23 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  parm.memg5++;           // group 5
                  user = parm.user23;      // save as last member (for guests)

                  parm.player23 = buildName(parm.lname23, parm.fname23, parm.mi23);    // rebuild the player name using the db name values

               } else {
                  parm.inval23 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[23] = "";
      parm.userg[23] = "";
      found = false;
      if (!parm.player24.equals( "" ) && !parm.player24.equalsIgnoreCase( "x" )) {

         i = 0;
         loop24:
         while (i < parm2.MAX_Guests) {

            if (parm.player24.startsWith( parm2.guest[i] )) {

               parm.g[23] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[23] = parm.player24;    // save guest value
               parm.guests++;                 // increment number of guests this request
               parm.guestsg5++;                 // increment number of guests this slot

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id24 != 0 || !parm.player24.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id24 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player24, parm.guest_id24, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player24;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[23] = user;             // set guest of last member found
               break loop24;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player24 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player24;
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
                  parm.hndcp24 = rs.getFloat(4);
                  parm.mNum24 = rs.getString(5);
                  parm.mstype24 = rs.getString(6);
                  parm.lname24 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname24 = rs.getString(8);
                  parm.mi24 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  parm.memg5++;           // group 5
                  user = parm.user24;      // save as last member (for guests)

                  parm.player24 = buildName(parm.lname24, parm.fname24, parm.mi24);    // rebuild the player name using the db name values

               } else {
                  parm.inval24 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      parm.g[24] = "";
      parm.userg[24] = "";
      found = false;
      if (!parm.player25.equals( "" ) && !parm.player25.equalsIgnoreCase( "x" )) {

         i = 0;
         loop25:
         while (i < parm2.MAX_Guests) {

            if (parm.player25.startsWith( parm2.guest[i] )) {

               parm.g[24] = parm2.guest[i];      // indicate player is a guest name and save name
               parm.gstA[24] = parm.player25;     // save guest value
               parm.guests++;                     // increment number of guests this request
               parm.guestsg5++;                   // increment number of guests this slot

               if (parm2.gDb[i] == 1) {

                   if (!guestdbTbaAllowed || parm.guest_id25 != 0 || !parm.player25.equals(parm2.guest[i] + " TBA")) {

                       if (parm.guest_id25 == 0) {
                           invalidGuest = true;
                       } else {
                           invalidGuest = verifySlot.checkTrackedGuestName(parm.player25, parm.guest_id25, parm2.guest[i], parm.club, con);
                       }

                       if (invalidGuest) {
                           parm.gplayer = parm.player25;    // indicate error
                           error = "invGuest";
                           return(error);
                       }
                   }
               }

               found = true;
               parm.userg[24] = user;             // set guest of last member found
               break loop25;
            }
            i++;
         }         // end of while loop

         if (found == false) {               // if guest type not found - must be member

            tok = new StringTokenizer( parm.player25 );     // space is the default token

            if ( tok.countTokens() > 3 ) {          // too many name fields

               error = "invData";                   // reject
               parm.player = parm.player25;
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
                  parm.hndcp25 = rs.getFloat(4);
                  parm.mNum25 = rs.getString(5);
                  parm.mstype25 = rs.getString(6);
                  parm.lname25 = rs.getString(7);      // use name from db table to ensure correct titlecase
                  parm.fname25 = rs.getString(8);
                  parm.mi25 = rs.getString(9);

                  parm.members++;         // increment number of members this res.
                  parm.memg5++;           // group 5
                  user = parm.user25;      // save as last member (for guests)

                  parm.player25 = buildName(parm.lname25, parm.fname25, parm.mi25);    // rebuild the player name using the db name values

               } else {
                  parm.inval25 = 1;        // indicate invalid name entered
               }
            }
         }
      }

      pstmt1.close();

      //
      //  Check for Unaccompanied Guests - see if Associated Member Name provided
      //
      user = "";

      for (i=0; i<25; i++) {

         if (!parm.memA[i].equals( "" )) {     // if name provided, then find username

            tok = new StringTokenizer( parm.memA[i] );     // space is the default token

            if ( tok.countTokens() == 2 ) {         // first name, last name

               fname = tok.nextToken();
               lname = tok.nextToken();
               mi = "";
            }

            if ( tok.countTokens() == 3 ) {         // first name, mi, last name

               fname = tok.nextToken();
               mi = tok.nextToken();
               lname = tok.nextToken();
            }

            if (!lname.equals( "" )) {

               pstmt1 = con.prepareStatement (
                  "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setString(1, lname);
               pstmt1.setString(2, fname);
               pstmt1.setString(3, mi);
               rs = pstmt1.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  parm.userg[i] = rs.getString(1);
                  user = parm.userg[i];               // save
               }
               pstmt1.close();
            }

         } else {

            if (!parm.g[i].equals( "" ) && parm.userg[i].equals( "" )) {  // if guest but not assigned

               parm.userg[i] = user;               // assign to last assigned, if any
            }
         }
      }

   }
   catch (Exception e1) {

      error = "Database Error.  Exception = " +e1.getMessage();                   // reject
   }

   return(error);
 }


/**
 //************************************************************************
 //
 //   buildName - build the player name with the values received
 //
 //************************************************************************
 **/

 private String buildName(String lname, String fname, String mi) {


   StringBuffer mem_name = new StringBuffer();      // for name


   //
   //  Rebuild the player name using the name values received
   //
   mem_name = new StringBuffer(fname);             // get first name

   if (!mi.equals( "" )) {
      mem_name.append(" ");
      mem_name.append(mi);                          // add mi if present
   }
   mem_name.append(" " + lname);                    // add last name

   String mname = mem_name.toString();             // set as player name

   return(mname);
 }


/**
 //************************************************************************
 //
 //  checkGuestQuota - checks for maximum number of guests exceeded per member
 //                    or per membership during a specified period.
 //
 //************************************************************************
 **/

 public boolean checkGuestQuota(parmSlotm slotParms, Connection con) {


   ResultSet rs = null;

   boolean error = false;
   boolean check = false;

   int i = 0;
   int i2 = 0;
   int guests = 0;

   int stime = 0;
   int etime = 0;

   long sdate = 0;
   long edate = 0;

   String rcourse = "";
   String rest_fb = "";
   String per = "";                        // per = 'Member' or 'Tee Time'

   int [] guestsA = new int [25];         // array to hold the Guest Counts for each player position

   String [] userg = new String [25];     // array to hold the Associated Member's username for each Guest

   //String [] rguest = new String [36];    // array to hold the Guest Restriction's guest types
   ArrayList<String> rguest = new ArrayList<String>();

   if (slotParms.fb == 0) {                   // is Tee time for Front 9?

      slotParms.sfb = "Front";
   }

   if (slotParms.fb == 1) {                   // is it Back 9?

      slotParms.sfb = "Back";
   }

   try {

      PreparedStatement pstmt5 = con.prepareStatement (
         "SELECT * " +
         "FROM guestqta4 " +
         "WHERE activity_id = 0 AND sdate <= ? AND edate >= ? AND stime <= ? AND etime >= ?");

      pstmt5.clearParameters();        // clear the parms
      pstmt5.setLong(1, slotParms.date);
      pstmt5.setLong(2, slotParms.date);
      pstmt5.setInt(3, slotParms.time1);
      pstmt5.setInt(4, slotParms.time1);
      rs = pstmt5.executeQuery();      // execute the prepared stmt

      loop1:
      while (rs.next()) {

         sdate = rs.getLong("sdate");
         edate = rs.getLong("edate");
         stime = rs.getInt("stime");
         etime = rs.getInt("etime");
         slotParms.grest_num = rs.getInt("num_guests");
         rcourse = rs.getString("courseName");
         rest_fb = rs.getString("fb");
         per = rs.getString("per");

         // now look up the guest types for this restriction
         PreparedStatement pstmt3 = con.prepareStatement (
                "SELECT guest_type FROM guestres2_gtypes WHERE guestres_id = ?");

         pstmt3.clearParameters();
         pstmt3.setInt(1, rs.getInt("id"));

         ResultSet rs2 = pstmt3.executeQuery();

         while ( rs2.next() ) {

            rguest.add(rs2.getString("guest_type"));

         }
         pstmt3.close();

         check = false;       // init 'check guests' flag

         for (i = 0; i < 25; i++) {
            userg[i] = "";         // init usernames
         }

         //
         //  Check if course and f/b match that specified in restriction
         //
         if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( slotParms.course ))) {

            if ((rest_fb.equals( "Both" )) || (rest_fb.equals( slotParms.sfb ))) {  // if f/b matches

               //  compare guest types in tee time against those specified in restriction
               i = 0;
               ploop1:
               while (i < rguest.size()) {

                  //
                  //     slotParms.gx = guest types specified in player name fields
                  //     rguest[x] = guest types from restriction gotten above
                  //
                  for (i2 = 0; i2 < 25; i2++) {

                     if (!slotParms.g[i2].equals( "" )) {
                        if (slotParms.g[i2].equals( rguest.get(i) )) {
                           check = true;                          // indicate check num of guests
                           userg[i2] = slotParms.userg[i2];       // save member associated with this guest
                        }
                     }
                  }
                  i++;
               }
            }
         }      // end of IF course matches

         if (check == true) {   // if restriction exists for this day and time and there are guests in tee time

            //
            //  Determine the member assigned to the guest and calculate their quota count
            //
            for (i = 0; i < 25; i++) {
               guestsA[i] = 0;          // init # of guests for each member
            }

            //
            //  Check each member for duplicates and count these guests first
            //
            for (i = 0; i < 25; i++) {

               i2 = i + 1;              // point to next user

               if (!userg[i].equals( "" )) {

                  guestsA[i]++;               // count the guest

                  while (i2 < 25) {           // loop for dups

                     if (userg[i].equals( userg[i2] )) {

                        guestsA[i]++;            // count the guest
                        userg[i2] = "";          // remove dup
                     }
                     i2++;
                  }

                  // go count the number of guests for this member
                  guestsA[i] += countGuests(con, userg[i], slotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

               }
            }

            //
            //  Process according to the 'per' value; member or member number
            //
            if (per.startsWith( "Membership" )) {

               for (i = 0; i < 25; i++) {

                  if (!userg[i].equals( "" )) {

                     // go count the number of guests for this member number
                     guestsA[i] += checkMnums(con, userg[i], slotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

                  }
               }
            }

            // if num of guests in quota count (guests_) > num allowed (grest_num) per member
            //
            //       to get here guests_ is = # of guests accumulated for member
            //       grest_num is 0 - 999 (restriction quota)
            //       per is 'Member' or 'Membership Number'
            //
            slotParms.grest_per = per;    // save this rest's per value

            if (guestsA[0] > slotParms.grest_num) {

               slotParms.player = slotParms.player1;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[1] > slotParms.grest_num) {

               slotParms.player = slotParms.player2;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[2] > slotParms.grest_num) {

               slotParms.player = slotParms.player3;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[3] > slotParms.grest_num) {

               slotParms.player = slotParms.player4;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[4] > slotParms.grest_num) {

               slotParms.player = slotParms.player5;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[5] > slotParms.grest_num) {

               slotParms.player = slotParms.player6;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[6] > slotParms.grest_num) {

               slotParms.player = slotParms.player7;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[7] > slotParms.grest_num) {

               slotParms.player = slotParms.player8;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[8] > slotParms.grest_num) {

               slotParms.player = slotParms.player9;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[9] > slotParms.grest_num) {

               slotParms.player = slotParms.player10;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[10] > slotParms.grest_num) {

               slotParms.player = slotParms.player11;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[11] > slotParms.grest_num) {

               slotParms.player = slotParms.player12;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[12] > slotParms.grest_num) {

               slotParms.player = slotParms.player13;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[13] > slotParms.grest_num) {

               slotParms.player = slotParms.player14;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[14] > slotParms.grest_num) {

               slotParms.player = slotParms.player15;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[15] > slotParms.grest_num) {

               slotParms.player = slotParms.player16;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[16] > slotParms.grest_num) {

               slotParms.player = slotParms.player17;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[17] > slotParms.grest_num) {

               slotParms.player = slotParms.player18;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[18] > slotParms.grest_num) {

               slotParms.player = slotParms.player19;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[19] > slotParms.grest_num) {

               slotParms.player = slotParms.player20;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[20] > slotParms.grest_num) {

               slotParms.player = slotParms.player21;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[21] > slotParms.grest_num) {

               slotParms.player = slotParms.player22;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[22] > slotParms.grest_num) {

               slotParms.player = slotParms.player23;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[23] > slotParms.grest_num) {

               slotParms.player = slotParms.player24;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[24] > slotParms.grest_num) {

               slotParms.player = slotParms.player25;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }

         }    // end of IF true
      }       // end of loop1 while loop

      pstmt5.close();

   }
   catch (Exception e) {
   }

   return(error);

 }


/**
 //************************************************************************
 //
 //  Find any other members with the same Member Number and count their guests.
 //
 //    Called by:  checkGuestQuota above
 //
 //************************************************************************
 **/

 public static int checkMnums(Connection con, String user, parmSlotm slotParms, long sdate, long edate, int stime, int etime,
                               String rfb, String rcourse, ArrayList<String> rguest)
                           throws Exception {

   ResultSet rs = null;

   int guests = 0;
   int guests2 = 0;
   int i = 0;

   String mNum = "";
   String tuser = "";


   //****************************************************************************
   //  per = Membership Number  -  check all members with the same Member Number
   //****************************************************************************

   try {

      //  get this user's mNum
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT memNum FROM member2b WHERE username = ?");

      pstmt3.clearParameters();        // clear the parms
      pstmt3.setString(1, user);
      rs = pstmt3.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         mNum = rs.getString(1);       // get this user's member number
      }
      pstmt3.close();

      if (!mNum.equals( "" )) {     // if there is one specified

         //  get all users with matching mNum and put in userm array
         PreparedStatement pstmt4 = con.prepareStatement (
            "SELECT username FROM member2b WHERE memNum = ?");

         pstmt4.clearParameters();        // clear the parms
         pstmt4.setString(1, mNum);
         rs = pstmt4.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            tuser = rs.getString(1);       // get the username
            if (!tuser.equals( "" ) && !tuser.equalsIgnoreCase( user )) {   // if exists and not this user

               // go count the number of guests for this member
               guests += countGuests(con, tuser, slotParms, sdate, edate, stime, etime, rfb, rcourse, rguest);

            }
         }
         pstmt4.close();
      }

   }
   catch (Exception e) {

      throw new Exception("Error Checking Mnums for Guest Rest - verifySlot: " + e.getMessage());
   }

   return(guests);
 }


/**
 //************************************************************************
 //
 //  Count the number of guests that a member has scheduled in the specified time.
 //
 //    Called by:  checkGuestQuota and checkMnums above
 //
 //    Check teecurr and tee past for all specified guest types that are
 //    associated with this member or member number.
 //
 //************************************************************************
 **/

 public static int countGuests(Connection con, String user, parmSlotm slotParms, long sdate, long edate, int stime, int etime,
                               String rfb, String rcourse, ArrayList<String> rguest)
                           throws Exception {

   ResultSet rs = null;

   int guests = 0;
   int time = 0;
   int fb = 0;
   int rest_fb = 0;
   int i = 0;

   long date = 0;

   //String sfb = "";
   String course = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";

   //  convert restriction's f/b
   if (rfb.equalsIgnoreCase( "Front" )) {
      rest_fb = 0;
   } else {
      rest_fb = 1;
   }


   //
   //  Count all guests with matching guest types that are associated with this member (teecurr)
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT date, time, courseName, player1, player2, player3, player4, fb, player5, " +
         "userg1, userg2, userg3, userg4, userg5 " +
         "FROM teecurr2 " +
         "WHERE (userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) " +
         "AND date <= ? AND date >= ? AND time <= ? AND time >= ?");

      pstmt1.clearParameters();        // clear the parms and check player 1
      pstmt1.setString(1, user);
      pstmt1.setString(2, user);
      pstmt1.setString(3, user);
      pstmt1.setString(4, user);
      pstmt1.setString(5, user);
      pstmt1.setLong(6, edate);
      pstmt1.setLong(7, sdate);
      pstmt1.setInt(8, etime);
      pstmt1.setInt(9, stime);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         date = rs.getLong("date");
         time = rs.getInt("time");
         course = rs.getString("courseName");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         fb = rs.getInt("fb");
         player5 = rs.getString("player5");
         userg1 = rs.getString("userg1");
         userg2 = rs.getString("userg2");
         userg3 = rs.getString("userg3");
         userg4 = rs.getString("userg4");
         userg5 = rs.getString("userg5");

         //
         //  matching tee time found in teecurr - check if course, date and time match
         //  Make sure this is not this tee time before changes,
         //
         if ((date != slotParms.date) || (time != slotParms.time1) || (!course.equals( slotParms.course ))) {

            //
            //  Check if course and f/b match that specified in restriction
            //
            if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( course ))) {

               if ((rfb.equals( "Both" )) || (rest_fb == fb )) {  // if f/b matches

                  // check if any players from the tee time are a restricted guest
                  if (user.equalsIgnoreCase( userg1 )) {
                     i = 0;
                     loop1:
                     while (i < rguest.size()) {
                        if (player1.startsWith( rguest.get(i) )) {    // if matchiing guest type & associated with this user
                           guests++;             // bump count of guests for quota check
                           break loop1;          // exit loop
                        }
                        i++;
                     }
                  }
                  if (user.equalsIgnoreCase( userg2 )) {
                     i = 0;
                     loop2:
                     while (i < rguest.size()) {
                        if (player2.startsWith( rguest.get(i) )) {    // if matchiing guest type & associated with this user
                           guests++;             // bump count of guests for quota check
                           break loop2;          // exit loop
                        }
                        i++;
                     }
                  }
                  if (user.equalsIgnoreCase( userg3 )) {
                     i = 0;
                     loop3:
                     while (i < rguest.size()) {
                        if (player3.startsWith( rguest.get(i) )) {    // if matchiing guest type & associated with this user
                           guests++;             // bump count of guests for quota check
                           break loop3;          // exit loop
                        }
                        i++;
                     }
                  }
                  if (user.equalsIgnoreCase( userg4 )) {
                     i = 0;
                     loop4:
                     while (i < rguest.size()) {
                        if (player4.startsWith( rguest.get(i) )) {    // if matchiing guest type & associated with this user
                           guests++;             // bump count of guests for quota check
                           break loop4;          // exit loop
                        }
                        i++;
                     }
                  }
                  if (user.equalsIgnoreCase( userg5 )) {
                     i = 0;
                     loop5:
                     while (i < rguest.size()) {
                        if (player5.startsWith( rguest.get(i) )) {    // if matchiing guest type & associated with this user
                           guests++;             // bump count of guests for quota check
                           break loop5;          // exit loop
                        }
                        i++;
                     }
                  }
               }
            }
         }   // end of IF tee time not 'this tee time'
      }   // end of WHILE

      pstmt1.close();

      //
      //  Count all guests with matching guest types that are associated with this member (teepast)
      //
      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT courseName, player1, player2, player3, player4, fb, player5, " +
         "userg1, userg2, userg3, userg4, userg5 " +
         "FROM teepast2 " +
         "WHERE (userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) " +
         "AND date <= ? AND date >= ? AND time <= ? AND time >= ?");

      pstmt2.clearParameters();        // clear the parms and check player 1
      pstmt2.setString(1, user);
      pstmt2.setString(2, user);
      pstmt2.setString(3, user);
      pstmt2.setString(4, user);
      pstmt2.setString(5, user);
      pstmt2.setLong(6, edate);
      pstmt2.setLong(7, sdate);
      pstmt2.setInt(8, etime);
      pstmt2.setInt(9, stime);
      rs = pstmt2.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         course = rs.getString("courseName");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         fb = rs.getInt("fb");
         player5 = rs.getString("player5");
         userg1 = rs.getString("userg1");
         userg2 = rs.getString("userg2");
         userg3 = rs.getString("userg3");
         userg4 = rs.getString("userg4");
         userg5 = rs.getString("userg5");

         //
         //  Check if course and f/b match that specified in restriction
         //
         if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( course ))) {

            if ((rfb.equals( "Both" )) || (rest_fb == fb )) {  // if f/b matches

               //
               //  matching tee time found in teecurr
               //  check if any players from the tee time are a restricted guest
               //
               if (user.equalsIgnoreCase( userg1 )) {
                  i = 0;
                  loop11:
                  while (i < rguest.size()) {
                     if (player1.startsWith( rguest.get(i) )) {    // if matchiing guest type & associated with this user
                        guests++;             // bump count of guests for quota check
                        break loop11;          // exit loop
                     }
                     i++;
                  }
               }
               if (user.equalsIgnoreCase( userg2 )) {
                  i = 0;
                  loop12:
                  while (i < rguest.size()) {
                     if (player2.startsWith( rguest.get(i) )) {    // if matchiing guest type & associated with this user
                        guests++;             // bump count of guests for quota check
                        break loop12;          // exit loop
                     }
                     i++;
                  }
               }
               if (user.equalsIgnoreCase( userg3 )) {
                  i = 0;
                  loop13:
                  while (i < rguest.size()) {
                     if (player3.startsWith( rguest.get(i) )) {    // if matchiing guest type & associated with this user
                        guests++;             // bump count of guests for quota check
                        break loop13;          // exit loop
                     }
                     i++;
                  }
               }
               if (user.equalsIgnoreCase( userg4 )) {
                  i = 0;
                  loop14:
                  while (i < rguest.size()) {
                     if (player4.startsWith( rguest.get(i) )) {    // if matchiing guest type & associated with this user
                        guests++;             // bump count of guests for quota check
                        break loop14;          // exit loop
                     }
                     i++;
                  }
               }
               if (user.equalsIgnoreCase( userg5 )) {
                  i = 0;
                  loop15:
                  while (i < rguest.size()) {
                     if (player5.startsWith( rguest.get(i) )) {    // if matchiing guest type & associated with this user
                        guests++;             // bump count of guests for quota check
                        break loop15;          // exit loop
                     }
                     i++;
                  }
               }
            }
         }
      }   // end of WHILE

      pstmt2.close();

   }
   catch (Exception e) {

      throw new Exception("Error Counting Guests for Guest Rest - Proshop_slotm: " + e.getMessage());
   }

   return(guests);
 }


 // *******************************************************************************
 //  Check membership restrictions - max rounds per week, month or year
 // *******************************************************************************
 //
 private boolean checkMemship(Connection con, PrintWriter out, parmSlotm parm, String day) {


   boolean check = false;
   boolean go = false;
   parm.error = false;               // init

   String mship = "";
   String player = "";
   String period = "";

   //
   //  Allocate a new parm block for each tee time and call common method to process each.
   //
   parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

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
   parm1.oldPlayer1 = "";       // always empty from here
   parm1.oldPlayer2 = "";
   parm1.oldPlayer3 = "";
   parm1.oldPlayer4 = "";
   parm1.oldPlayer5 = "";
   parm1.fb = parm.fb;
   parm1.ind = parm.ind;      // index value
   parm1.sfb = parm.sfb;
   parm1.club = parm.club;    // name of club


   //
   //  Do all members, one group at a time
   //
   try {

      go = false;                             // init to 'No Go'

      if (parm.p5.equals( "Yes" )) {

         if (!parm.mship1.equals( "" ) || !parm.mship2.equals( "" ) || !parm.mship3.equals( "" ) ||
             !parm.mship4.equals( "" ) || !parm.mship5.equals( "" )) {

            go = true;                // go process this group

            //
            //  set parms for first group
            //
            parm1.time = parm.time1;
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
            parm1.mstype1 = parm.mstype1;
            parm1.mstype2 = parm.mstype2;
            parm1.mstype3 = parm.mstype3;
            parm1.mstype4 = parm.mstype4;
            parm1.mstype5 = parm.mstype5;
            parm1.mNum1 = parm.mNum1;
            parm1.mNum2 = parm.mNum2;
            parm1.mNum3 = parm.mNum3;
            parm1.mNum4 = parm.mNum4;
            parm1.mNum5 = parm.mNum5;
         }

      } else {                       // 4-somes only

         if (!parm.mship1.equals( "" ) || !parm.mship2.equals( "" ) || !parm.mship3.equals( "" ) ||
             !parm.mship4.equals( "" )) {

            go = true;                // go process this group

            //
            //  set parms for first group
            //
            parm1.time = parm.time1;
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
            parm1.mstype1 = parm.mstype1;
            parm1.mstype2 = parm.mstype2;
            parm1.mstype3 = parm.mstype3;
            parm1.mstype4 = parm.mstype4;
            parm1.mNum1 = parm.mNum1;
            parm1.mNum2 = parm.mNum2;
            parm1.mNum3 = parm.mNum3;
            parm1.mNum4 = parm.mNum4;
            parm1.player5 = "";
            parm1.user5 = "";
            parm1.mship5 = "";
            parm1.mtype5 = "";
            parm1.mstype5 = "";
            parm1.mNum5 = "";
         }
      }

      if (go == true) {          // if mships found

         check = verifySlot.checkMaxRounds(parm1, con);         // check mships!!!!!!!!!!!!

         if (check == true) {          // if we hit on a restriction

            player = parm1.player;
            period = parm1.period;
            mship = parm1.mship;
         }
      }

      if (check == false) {           // if we can keep going

         //
         //  Do 2nd group
         //
         go = false;                             // init to 'No Go'

         if (parm.p5.equals( "Yes" )) {

            if (!parm.mship6.equals( "" ) || !parm.mship7.equals( "" ) || !parm.mship8.equals( "" ) ||
                !parm.mship9.equals( "" ) || !parm.mship10.equals( "" )) {

               go = true;                // go process this group

               //
               //  set parms for this group
               //
               parm1.time = parm.time2;
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
               parm1.mstype1 = parm.mstype6;
               parm1.mstype2 = parm.mstype7;
               parm1.mstype3 = parm.mstype8;
               parm1.mstype4 = parm.mstype9;
               parm1.mstype5 = parm.mstype10;
               parm1.mNum1 = parm.mNum6;
               parm1.mNum2 = parm.mNum7;
               parm1.mNum3 = parm.mNum8;
               parm1.mNum4 = parm.mNum9;
               parm1.mNum5 = parm.mNum10;
            }

         } else {                       // 4-somes only

            if (!parm.mship5.equals( "" ) || !parm.mship6.equals( "" ) || !parm.mship7.equals( "" ) ||
                !parm.mship8.equals( "" )) {

               go = true;                // go process this group

               //
               //  set parms for this group
               //
               parm1.time = parm.time2;
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
               parm1.mstype1 = parm.mstype5;
               parm1.mstype2 = parm.mstype6;
               parm1.mstype3 = parm.mstype7;
               parm1.mstype4 = parm.mstype8;
               parm1.mNum1 = parm.mNum5;
               parm1.mNum2 = parm.mNum6;
               parm1.mNum3 = parm.mNum7;
               parm1.mNum4 = parm.mNum8;
               parm1.player5 = "";
               parm1.user5 = "";
               parm1.mship5 = "";
               parm1.mtype5 = "";
               parm1.mstype5 = "";
               parm1.mNum5 = "";
            }
         }

         if (go == true) {          // if mships found

            check = verifySlot.checkMaxRounds(parm1, con);         // check mships!!!!!!!!!!!!

            if (check == true) {          // if we hit on a restriction

               player = parm1.player;
               period = parm1.period;
               mship = parm1.mship;
            }
         }

         if (check == false) {           // if we can keep going

            //
            //  Do 3rd group
            //
            go = false;                             // init to 'No Go'

            if (parm.p5.equals( "Yes" )) {

               if (!parm.mship11.equals( "" ) || !parm.mship12.equals( "" ) || !parm.mship13.equals( "" ) ||
                   !parm.mship14.equals( "" ) || !parm.mship15.equals( "" )) {

                  go = true;                // go process this group

                  //
                  //  set parms for this group
                  //
                  parm1.time = parm.time3;
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
                  parm1.mstype1 = parm.mstype11;
                  parm1.mstype2 = parm.mstype12;
                  parm1.mstype3 = parm.mstype13;
                  parm1.mstype4 = parm.mstype14;
                  parm1.mstype5 = parm.mstype15;
                  parm1.mNum1 = parm.mNum11;
                  parm1.mNum2 = parm.mNum12;
                  parm1.mNum3 = parm.mNum13;
                  parm1.mNum4 = parm.mNum14;
                  parm1.mNum5 = parm.mNum15;
               }

            } else {                       // 4-somes only

               if (!parm.mship9.equals( "" ) || !parm.mship10.equals( "" ) || !parm.mship11.equals( "" ) ||
                   !parm.mship12.equals( "" )) {

                  go = true;                // go process this group

                  //
                  //  set parms for this group
                  //
                  parm1.time = parm.time3;
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
                  parm1.mstype1 = parm.mstype9;
                  parm1.mstype2 = parm.mstype10;
                  parm1.mstype3 = parm.mstype11;
                  parm1.mstype4 = parm.mstype12;
                  parm1.mNum1 = parm.mNum9;
                  parm1.mNum2 = parm.mNum10;
                  parm1.mNum3 = parm.mNum11;
                  parm1.mNum4 = parm.mNum12;
                  parm1.player5 = "";
                  parm1.user5 = "";
                  parm1.mship5 = "";
                  parm1.mtype5 = "";
                  parm1.mstype5 = "";
                  parm1.mNum5 = "";
               }
            }

            if (go == true) {          // if mships found

               check = verifySlot.checkMaxRounds(parm1, con);         // check mships!!!!!!!!!!!!

               if (check == true) {          // if we hit on a restriction

                  player = parm1.player;
                  period = parm1.period;
                  mship = parm1.mship;
               }
            }

            if (check == false) {           // if we can keep going

               //
               //  Do 4th group
               //
               go = false;                             // init to 'No Go'

               if (parm.p5.equals( "Yes" )) {

                  if (!parm.mship16.equals( "" ) || !parm.mship17.equals( "" ) || !parm.mship18.equals( "" ) ||
                      !parm.mship19.equals( "" ) || !parm.mship20.equals( "" )) {

                     go = true;                // go process this group

                     //
                     //  set parms for this group
                     //
                     parm1.time = parm.time4;
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
                     parm1.mstype1 = parm.mstype16;
                     parm1.mstype2 = parm.mstype17;
                     parm1.mstype3 = parm.mstype18;
                     parm1.mstype4 = parm.mstype19;
                     parm1.mstype5 = parm.mstype20;
                     parm1.mNum1 = parm.mNum16;
                     parm1.mNum2 = parm.mNum17;
                     parm1.mNum3 = parm.mNum18;
                     parm1.mNum4 = parm.mNum19;
                     parm1.mNum5 = parm.mNum20;
                  }

               } else {                       // 4-somes only

                  if (!parm.mship13.equals( "" ) || !parm.mship14.equals( "" ) || !parm.mship15.equals( "" ) ||
                      !parm.mship16.equals( "" )) {

                     go = true;                // go process this group

                     //
                     //  set parms for this group
                     //
                     parm1.time = parm.time4;
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
                     parm1.mstype1 = parm.mstype13;
                     parm1.mstype2 = parm.mstype14;
                     parm1.mstype3 = parm.mstype15;
                     parm1.mstype4 = parm.mstype16;
                     parm1.mNum1 = parm.mNum13;
                     parm1.mNum2 = parm.mNum14;
                     parm1.mNum3 = parm.mNum15;
                     parm1.mNum4 = parm.mNum16;
                     parm1.player5 = "";
                     parm1.user5 = "";
                     parm1.mship5 = "";
                     parm1.mtype5 = "";
                     parm1.mstype5 = "";
                     parm1.mNum5 = "";
                  }
               }

               if (go == true) {          // if mships found

                  check = verifySlot.checkMaxRounds(parm1, con);         // check mships!!!!!!!!!!!!

                  if (check == true) {          // if we hit on a restriction

                     player = parm1.player;
                     period = parm1.period;
                     mship = parm1.mship;
                  }
               }

               if (check == false) {           // if we can keep going

                  //
                  //  Do 5th group
                  //
                  go = false;                             // init to 'No Go'

                  if (parm.p5.equals( "Yes" )) {

                     if (!parm.mship21.equals( "" ) || !parm.mship22.equals( "" ) || !parm.mship23.equals( "" ) ||
                         !parm.mship24.equals( "" ) || !parm.mship25.equals( "" )) {

                        go = true;                // go process this group

                        //
                        //  set parms for this group
                        //
                        parm1.time = parm.time5;
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
                        parm1.mstype1 = parm.mstype21;
                        parm1.mstype2 = parm.mstype22;
                        parm1.mstype3 = parm.mstype23;
                        parm1.mstype4 = parm.mstype24;
                        parm1.mstype5 = parm.mstype25;
                        parm1.mNum1 = parm.mNum21;
                        parm1.mNum2 = parm.mNum22;
                        parm1.mNum3 = parm.mNum23;
                        parm1.mNum4 = parm.mNum24;
                        parm1.mNum5 = parm.mNum25;
                     }

                  } else {                       // 4-somes only

                     if (!parm.mship17.equals( "" ) || !parm.mship18.equals( "" ) || !parm.mship19.equals( "" ) ||
                         !parm.mship20.equals( "" )) {

                        go = true;                // go process this group

                        //
                        //  set parms for this group
                        //
                        parm1.time = parm.time5;
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
                        parm1.mstype1 = parm.mstype17;
                        parm1.mstype2 = parm.mstype18;
                        parm1.mstype3 = parm.mstype19;
                        parm1.mstype4 = parm.mstype20;
                        parm1.mNum1 = parm.mNum17;
                        parm1.mNum2 = parm.mNum18;
                        parm1.mNum3 = parm.mNum19;
                        parm1.mNum4 = parm.mNum20;
                        parm1.player5 = "";
                        parm1.user5 = "";
                        parm1.mship5 = "";
                        parm1.mtype5 = "";
                        parm1.mstype5 = "";
                        parm1.mNum5 = "";
                     }
                  }

                  if (go == true) {          // if mships found

                     check = verifySlot.checkMaxRounds(parm1, con);         // check mships!!!!!!!!!!!!

                     if (check == true) {          // if we hit on a restriction

                        player = parm1.player;
                        period = parm1.period;
                        mship = parm1.mship;
                     }
                  }
               }
            }
         }
      }
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
 private boolean checkMemRes(Connection con, PrintWriter out, parmSlotm parm, String day) {


   boolean check = false;
   parm.error = false;               // init

   String player = "";
   String rest_name = "";

   //
   //  Allocate a new parm block for each tee time and call common method to process each.
   //
   parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

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
   parm1.oldPlayer1 = "";       // always empty from here
   parm1.oldPlayer2 = "";
   parm1.oldPlayer3 = "";
   parm1.oldPlayer4 = "";
   parm1.oldPlayer5 = "";
   parm1.fb = parm.fb;
   parm1.ind = parm.ind;      // index value
   parm1.sfb = parm.sfb;
   parm1.club = parm.club;    // name of club


   //
   //  Do each tee time - one at a time
   //
   try {


      //
      //  set parms for first tee time
      //
      parm1.time = parm.time1;
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

      if (parm.p5.equals( "Yes" )) {

         parm1.player5 = parm.player5;
         parm1.user5 = parm.user5;
         parm1.mship5 = parm.mship5;
         parm1.mtype5 = parm.mtype5;
         parm1.mNum5 = parm.mNum5;

      } else {

         parm1.player5 = "";
         parm1.user5 = "";
         parm1.mship5 = "";
         parm1.mtype5 = "";
         parm1.mNum5 = "";
      }

      check = verifySlot.checkMemRests(parm1, con);                   // check for restrictions !!!!!

      if (check == true) {          // if we hit on a restriction

         player = parm1.player;
         rest_name = parm1.rest_name;

      } else {

         //
         //  Custom checks for individual clubs
         //
         if (parm1.club.equals( "stanwichclub" )) {

            check = verifySlot.checkStanwichDependents(parm1);     // check for Dependent w/o an Adult

            if (check == true) {

               player = "Stanwich Dependent Error";          // indicate stanwich error
            }
         }

         if (parm1.club.equals( "castlepines" )) {

            check = verifySlot.checkCastleDependents(parm1);     // check for Dependent w/o an Adult

            if (check == true) {

               player = "Castle Dependent Error";          // indicate castle pines error
            }
         }

         if (parm1.club.equals( "cherryhills" )) {

            check = verifySlot.checkCherryHills(parm1);     // check custom mtype rests

            if (check == true) {

               player = "Cherry Hills Mtype Error";          // indicate Cherry Hills error
            }
         }

         if (parm1.club.equals( "ritzcarlton" )) {

            check = verifySlot.checkRitz(parm1, con);     // check custom rests

            if (check == true) {

               player = "Ritz Custom Error";          // indicate Ritz error
            }
         }

         if (parm1.club.equals( "skaneateles" )) {

            check = verifySlot.checkSkaneateles(parm1);     // check custom rests

            if (check == true) {

               player = "Skaneateles Custom Error";          // indicate Skaneateles error
            }
         }

         if (parm1.club.equals( "oaklandhills" )) {

            check = verifySlot.checkOaklandKids(parm1);     // check custom rests

            if (check == true) {

               player = "Oakland Hills Custom Error";          // indicate Skaneateles error
            }
         }

         if (parm1.club.equals( "bearpath" )) {

            check = verifyCustom.checkBearpathMems(parm1);     // check custom rests

            if (check == true) {

               player = "Bearpath Custom Error";          // indicate error
            }
         }
      }

      //   Custom for case 1496
      if (parm.club.equals( "bellemeadecc" ) && parm.day.equals( "Sunday" ) && parm.time1 > 759 && parm.time1 < 1251) {   // if Sunday, 8 - 12:50

         check = verifyCustom.checkBelleMeadeFems(parm1);     // check for Female w/o a Male

         if (check == true) {

            player = "Belle Meade Custom Error";          // indicate error
         }
      }



      if (check == false && parm.time2 > 0) {        // if we can keep going

         //
         //  do next tee time
         //
         parm1.time = parm.time2;

         if (parm.p5.equals( "Yes" )) {

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

         } else {

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

         check = verifySlot.checkMemRests(parm1, con);                      // check for restrictions !!!!!

         if (check == true) {          // if we hit on a restriction

            player = parm1.player;
            rest_name = parm1.rest_name;

         } else {

            //
            //  If The Stanwich Club - check for dependents w/o adults
            //
            if (parm1.club.equals( "stanwichclub" )) {

               check = verifySlot.checkStanwichDependents(parm1);     // check for Dependent w/o an Adult

               if (check == true) {

                  player = "Stanwich Dependent Error";          // indicate stanwich error
               }
            }

            if (parm1.club.equals( "castlepines" )) {

               check = verifySlot.checkCastleDependents(parm1);     // check for Dependent w/o an Adult

               if (check == true) {

                  player = "Castle Dependent Error";          // indicate castle pines error
               }
            }

            if (parm1.club.equals( "cherryhills" )) {

               check = verifySlot.checkCherryHills(parm1);     // check custom mtype rests

               if (check == true) {

                  player = "Cherry Hills Mtype Error";          // indicate Cherry Hills error
               }
            }

            if (parm1.club.equals( "ritzcarlton" )) {

               check = verifySlot.checkRitz(parm1, con);     // check custom rests

               if (check == true) {

                  player = "Ritz Custom Error";          // indicate Ritz error
               }
            }

            if (parm1.club.equals( "skaneateles" )) {

               check = verifySlot.checkSkaneateles(parm1);     // check custom rests

               if (check == true) {

                  player = "Skaneateles Custom Error";          // indicate Skaneateles error
               }
            }

            if (parm1.club.equals( "oaklandhills" )) {

               check = verifySlot.checkOaklandKids(parm1);     // check custom rests

               if (check == true) {

                  player = "Oakland Hills Custom Error";          // indicate Skaneateles error
               }
            }

            if (parm1.club.equals( "bearpath" )) {

               check = verifyCustom.checkBearpathMems(parm1);     // check custom rests

               if (check == true) {

                  player = "Bearpath Custom Error";          // indicate Skaneateles error
               }
            }
         }

         //   Custom for case 1496
         if (parm.club.equals( "bellemeadecc" ) && parm.day.equals( "Sunday" ) && parm.time2 > 759 && parm.time2 < 1251) {   // if Sunday, 8 - 12:50

            check = verifyCustom.checkBelleMeadeFems(parm1);     // check for Female w/o a Male

            if (check == true) {

               player = "Belle Meade Custom Error";          // indicate error
            }
         }


         if (check == false && parm.time3 > 0) {        // if we can keep going

            //
            //  do next tee time
            //
            parm1.time = parm.time3;

            if (parm.p5.equals( "Yes" )) {

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

            } else {

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

            check = verifySlot.checkMemRests(parm1, con);                      // check for restrictions !!!!!

            if (check == true) {          // if we hit on a restriction

               player = parm1.player;
               rest_name = parm1.rest_name;

            } else {

               //
               //  If The Stanwich Club - check for dependents w/o adults
               //
               if (parm1.club.equals( "stanwichclub" )) {

                  check = verifySlot.checkStanwichDependents(parm1);     // check for Dependent w/o an Adult

                  if (check == true) {

                     player = "Stanwich Dependent Error";          // indicate stanwich error
                  }
               }

               if (parm1.club.equals( "castlepines" )) {

                  check = verifySlot.checkCastleDependents(parm1);     // check for Dependent w/o an Adult

                  if (check == true) {

                     player = "Castle Dependent Error";          // indicate castle pines error
                  }
               }

               if (parm1.club.equals( "cherryhills" )) {

                  check = verifySlot.checkCherryHills(parm1);     // check custom mtype rests

                  if (check == true) {

                     player = "Cherry Hills Mtype Error";          // indicate Cherry Hills error
                  }
               }

               if (parm1.club.equals( "ritzcarlton" )) {

                  check = verifySlot.checkRitz(parm1, con);     // check custom rests

                  if (check == true) {

                     player = "Ritz Custom Error";          // indicate Ritz error
                  }
               }

               if (parm1.club.equals( "skaneateles" )) {

                  check = verifySlot.checkSkaneateles(parm1);     // check custom rests

                  if (check == true) {

                     player = "Skaneateles Custom Error";          // indicate Skaneateles error
                  }
               }

               if (parm1.club.equals( "oaklandhills" )) {

                  check = verifySlot.checkOaklandKids(parm1);     // check custom rests

                  if (check == true) {

                     player = "Oakland Hills Custom Error";          // indicate Skaneateles error
                  }
               }

               if (parm1.club.equals( "bearpath" )) {

                  check = verifyCustom.checkBearpathMems(parm1);     // check custom rests

                  if (check == true) {

                     player = "Bearpath Custom Error";          // indicate Skaneateles error
                  }
               }
            }

            //   Custom for case 1496
            if (parm.club.equals( "bellemeadecc" ) && parm.day.equals( "Sunday" ) && parm.time3 > 759 && parm.time3 < 1251) {   // if Sunday, 8 - 12:50

               check = verifyCustom.checkBelleMeadeFems(parm1);     // check for Female w/o a Male

               if (check == true) {

                  player = "Belle Meade Custom Error";          // indicate error
               }
            }


            if (check == false && parm.time4 > 0) {        // if we can keep going

               //
               //  do next tee time
               //
               parm1.time = parm.time4;

               if (parm.p5.equals( "Yes" )) {

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

               } else {

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

               check = verifySlot.checkMemRests(parm1, con);         // check for restrictions !!!!!

               if (check == true) {          // if we hit on a restriction

                  player = parm1.player;
                  rest_name = parm1.rest_name;

               } else {

                  //
                  //  If The Stanwich Club - check for dependents w/o adults
                  //
                  if (parm1.club.equals( "stanwichclub" )) {

                     check = verifySlot.checkStanwichDependents(parm1);     // check for Dependent w/o an Adult

                     if (check == true) {

                        player = "Stanwich Dependent Error";          // indicate stanwich error
                     }
                  }

                  if (parm1.club.equals( "castlepines" )) {

                     check = verifySlot.checkCastleDependents(parm1);     // check for Dependent w/o an Adult

                     if (check == true) {

                        player = "Castle Dependent Error";          // indicate castle pines error
                     }
                  }

                  if (parm1.club.equals( "cherryhills" )) {

                     check = verifySlot.checkCherryHills(parm1);     // check custom mtype rests

                     if (check == true) {

                        player = "Cherry Hills Mtype Error";          // indicate Cherry Hills error
                     }
                  }

                  if (parm1.club.equals( "ritzcarlton" )) {

                     check = verifySlot.checkRitz(parm1, con);     // check custom rests

                     if (check == true) {

                        player = "Ritz Custom Error";          // indicate Ritz error
                     }
                  }

                  if (parm1.club.equals( "skaneateles" )) {

                     check = verifySlot.checkSkaneateles(parm1);     // check custom rests

                     if (check == true) {

                        player = "Skaneateles Custom Error";          // indicate Skaneateles error
                     }
                  }

                  if (parm1.club.equals( "oaklandhills" )) {

                     check = verifySlot.checkOaklandKids(parm1);     // check custom rests

                     if (check == true) {

                        player = "Oakland Hills Custom Error";          // indicate Skaneateles error
                     }
                  }

                  if (parm1.club.equals( "bearpath" )) {

                     check = verifyCustom.checkBearpathMems(parm1);     // check custom rests

                     if (check == true) {

                        player = "Bearpath Custom Error";          // indicate Skaneateles error
                     }
                  }
               }

               //   Custom for case 1496
               if (parm.club.equals( "bellemeadecc" ) && parm.day.equals( "Sunday" ) && parm.time4 > 759 && parm.time4 < 1251) {   // if Sunday, 8 - 12:50

                  check = verifyCustom.checkBelleMeadeFems(parm1);     // check for Female w/o a Male

                  if (check == true) {

                     player = "Belle Meade Custom Error";          // indicate error
                  }
               }


               if (check == false && parm.time5 > 0) {        // if we can keep going

                  //
                  //  do next tee time
                  //
                  parm1.time = parm.time5;

                  if (parm.p5.equals( "Yes" )) {

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

                  } else {

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

                  check = verifySlot.checkMemRests(parm1, con);         // check for restrictions !!!!!

                  if (check == true) {          // if we hit on a restriction

                     player = parm1.player;
                     rest_name = parm1.rest_name;

                  } else {

                     //
                     //  If The Stanwich Club - check for dependents w/o adults
                     //
                     if (parm1.club.equals( "stanwichclub" )) {

                        check = verifySlot.checkStanwichDependents(parm1);     // check for Dependent w/o an Adult

                        if (check == true) {

                           player = "Stanwich Dependent Error";          // indicate stanwich error
                        }
                     }

                     if (parm1.club.equals( "castlepines" )) {

                        check = verifySlot.checkCastleDependents(parm1);     // check for Dependent w/o an Adult

                        if (check == true) {

                           player = "Castle Dependent Error";          // indicate castle pines error
                        }
                     }

                     if (parm1.club.equals( "cherryhills" )) {

                        check = verifySlot.checkCherryHills(parm1);     // check custom mtype rests

                        if (check == true) {

                           player = "Cherry Hills Mtype Error";          // indicate Cherry Hills error
                        }
                     }

                     if (parm1.club.equals( "ritzcarlton" )) {

                        check = verifySlot.checkRitz(parm1, con);     // check custom rests

                        if (check == true) {

                           player = "Ritz Custom Error";              // indicate Ritz error
                        }
                     }

                     if (parm1.club.equals( "skaneateles" )) {

                        check = verifySlot.checkSkaneateles(parm1);     // check custom rests

                        if (check == true) {

                           player = "Skaneateles Custom Error";          // indicate Skaneateles error
                        }
                     }

                     if (parm1.club.equals( "oaklandhills" )) {

                        check = verifySlot.checkOaklandKids(parm1);     // check custom rests

                        if (check == true) {

                           player = "Oakland Hills Custom Error";          // indicate Skaneateles error
                        }
                     }

                     if (parm1.club.equals( "bearpath" )) {

                        check = verifyCustom.checkBearpathMems(parm1);     // check custom rests

                        if (check == true) {

                           player = "Bearpath Custom Error";          // indicate Skaneateles error
                        }
                     }

                     //   Custom for case 1496
                     if (parm.club.equals( "bellemeadecc" ) && parm.day.equals( "Sunday" ) && parm.time5 > 759 && parm.time5 < 1251) {   // if Sunday, 8 - 12:50

                        check = verifyCustom.checkBelleMeadeFems(parm1);     // check for Female w/o a Male

                        if (check == true) {

                           player = "Belle Meade Custom Error";          // indicate error
                        }
                     }
                  }
               }
            }
         }
      }

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
 //  Check CUSTOMS
 //
 //     This will group the request into 4-somes or 5-somes and process each
 //     group individually for customs.
 //
 //  NOTE:  The individual customs are processed in verifyCustom!!!
 //         No special code needed here - refer to verifyCustom.checkCustoms1
 //
 // *******************************************************************************
 //
 private String checkCustoms1(Connection con, parmSlotm parm, String user) {


   parm.error = false;               // init

   String errorMsg = "";
   String rest_name = "";

   //
   //  Allocate a new parm block for each tee time and call common method to process each.
   //
   parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

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
   parm1.oldPlayer1 = "";       // always empty from here
   parm1.oldPlayer2 = "";
   parm1.oldPlayer3 = "";
   parm1.oldPlayer4 = "";
   parm1.oldPlayer5 = "";
   parm1.fb = parm.fb;
   parm1.ind = parm.ind;      // index value
   parm1.sfb = parm.sfb;
   parm1.club = parm.club;    // name of club
   parm1.user = user;         // username of this user


   //
   //  Do each tee time - one at a time
   //

   //
   //  set parms for first tee time
   //
   parm1.time = parm.time1;
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
   parm1.userg1 = parm.userg[0];
   parm1.userg2 = parm.userg[1];
   parm1.userg3 = parm.userg[2];
   parm1.userg4 = parm.userg[3];
   parm1.members = parm.memg1;
   parm1.guests = parm.guestsg1;
   parm1.groups = 1; // to indicate this is the first group from the request

   if (parm.p5.equals( "Yes" )) {

      parm1.player5 = parm.player5;
      parm1.user5 = parm.user5;
      parm1.mship5 = parm.mship5;
      parm1.mtype5 = parm.mtype5;
      parm1.mNum5 = parm.mNum5;
      parm1.userg5 = parm.userg[4];

   } else {

      parm1.player5 = "";
      parm1.user5 = "";
      parm1.mship5 = "";
      parm1.mtype5 = "";
      parm1.mNum5 = "";
      parm1.userg5 = "";
   }


   //
   //  Go check for any customs
   //
   errorMsg = verifyCustom.checkCustoms1(parm1, con);     // go check for customs

   if (errorMsg.equals("") && parm.time2 > 0) {        // if we can keep going

      //
      //  do next tee time
      //
      parm1.time = parm.time2;
      parm1.members = parm.memg2;
      parm1.guests = parm.guestsg2;
      parm1.groups = 2;

      if (parm.p5.equals( "Yes" )) {

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
         parm1.userg1 = parm.userg[5];
         parm1.userg2 = parm.userg[6];
         parm1.userg3 = parm.userg[7];
         parm1.userg4 = parm.userg[8];
         parm1.userg5 = parm.userg[9];

      } else {

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
         parm1.userg1 = parm.userg[4];
         parm1.userg2 = parm.userg[5];
         parm1.userg3 = parm.userg[6];
         parm1.userg4 = parm.userg[7];

         parm1.player5 = "";
         parm1.user5 = "";
         parm1.mship5 = "";
         parm1.mtype5 = "";
         parm1.mNum5 = "";
         parm1.userg5 = "";
      }

      //
      //  Go check for any customs
      //
      errorMsg = verifyCustom.checkCustoms1(parm1, con);     // go check for customs


      if (errorMsg.equals("") && parm.time3 > 0) {        // if we can keep going

         //
         //  do next tee time
         //
         parm1.time = parm.time3;
         parm1.members = parm.memg3;
         parm1.guests = parm.guestsg3;
         parm1.groups = 3;

         if (parm.p5.equals( "Yes" )) {

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
            parm1.userg1 = parm.userg[10];
            parm1.userg2 = parm.userg[11];
            parm1.userg3 = parm.userg[12];
            parm1.userg4 = parm.userg[13];
            parm1.userg5 = parm.userg[14];

         } else {

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
            parm1.userg1 = parm.userg[8];
            parm1.userg2 = parm.userg[9];
            parm1.userg3 = parm.userg[10];
            parm1.userg4 = parm.userg[11];

            parm1.player5 = "";
            parm1.user5 = "";
            parm1.mship5 = "";
            parm1.mtype5 = "";
            parm1.mNum5 = "";
            parm1.userg5 = "";
         }

         //
         //  Go check for any customs
         //
         errorMsg = verifyCustom.checkCustoms1(parm1, con);     // go check for customs

         if (errorMsg.equals("") && parm.time4 > 0) {        // if we can keep going

            //
            //  do next tee time
            //
            parm1.time = parm.time4;
            parm1.members = parm.memg4;
            parm1.guests = parm.guestsg4;
            parm1.groups = 4;

            if (parm.p5.equals( "Yes" )) {

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
               parm1.userg1 = parm.userg[15];
               parm1.userg2 = parm.userg[16];
               parm1.userg3 = parm.userg[17];
               parm1.userg4 = parm.userg[18];
               parm1.userg5 = parm.userg[19];

            } else {

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
               parm1.userg1 = parm.userg[12];
               parm1.userg2 = parm.userg[13];
               parm1.userg3 = parm.userg[14];
               parm1.userg4 = parm.userg[15];

               parm1.player5 = "";
               parm1.user5 = "";
               parm1.mship5 = "";
               parm1.mtype5 = "";
               parm1.mNum5 = "";
               parm1.userg5 = "";
            }

            //
            //  Go check for any customs
            //
            errorMsg = verifyCustom.checkCustoms1(parm1, con);     // go check for customs

            if (errorMsg.equals("") && parm.time5 > 0) {        // if we can keep going

               //
               //  do next tee time
               //
               parm1.time = parm.time5;
               parm1.members = parm.memg5;
               parm1.guests = parm.guestsg5;
               parm1.groups = 5;

               if (parm.p5.equals( "Yes" )) {

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
                  parm1.userg1 = parm.userg[20];
                  parm1.userg2 = parm.userg[21];
                  parm1.userg3 = parm.userg[22];
                  parm1.userg4 = parm.userg[23];
                  parm1.userg5 = parm.userg[24];

               } else {

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
                  parm1.userg1 = parm.userg[16];
                  parm1.userg2 = parm.userg[17];
                  parm1.userg3 = parm.userg[18];
                  parm1.userg4 = parm.userg[19];

                  parm1.player5 = "";
                  parm1.user5 = "";
                  parm1.mship5 = "";
                  parm1.mtype5 = "";
                  parm1.mNum5 = "";
                  parm1.userg5 = "";
               }

               //
               //  Go check for any customs
               //
               errorMsg = verifyCustom.checkCustoms1(parm1, con);     // go check for customs

            }
         }
      }
   }

   return(errorMsg);

 }         // end of checkCustoms1


 // *******************************************************************************
 //  Check CUSTOMS - Guests (after Guests are assigned)
 //
 //     This will group the request into 4-somes or 5-somes and process each
 //     group individually for customs.
 //
 //  NOTE:  The individual customs are processed in verifyCustom!!!
 //         No special code needed here - refer to verifyCustom.checkCustomsGst
 //
 // *******************************************************************************
 //
 private String checkCustomsGst(Connection con, parmSlotm parm, String user) {


   parm.error = false;               // init

   String errorMsg = "";

   //
   //  Allocate a new parm block for each tee time and call common method to process each.
   //
   parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

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
   parm1.oldPlayer1 = "";       // always empty from here
   parm1.oldPlayer2 = "";
   parm1.oldPlayer3 = "";
   parm1.oldPlayer4 = "";
   parm1.oldPlayer5 = "";
   parm1.fb = parm.fb;
   parm1.ind = parm.ind;      // index value
   parm1.sfb = parm.sfb;
   parm1.club = parm.club;    // name of club
   parm1.user = user;         // username of this user


   //
   //  Do each tee time - one at a time
   //

   //
   //  set parms for first tee time
   //
   parm1.time = parm.time1;
   parm1.player1 = parm.player1;
   parm1.player2 = parm.player2;
   parm1.player3 = parm.player3;
   parm1.player4 = parm.player4;
   parm1.user1 = parm.user1;
   parm1.user2 = parm.user2;
   parm1.user3 = parm.user3;
   parm1.user4 = parm.user4;
   parm1.userg1 = parm.userg[0];
   parm1.userg2 = parm.userg[1];
   parm1.userg3 = parm.userg[2];
   parm1.userg4 = parm.userg[3];
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

   parm1.members = parm.memg1;
   parm1.guests = parm.guestsg1;

   if (parm.p5.equals( "Yes" )) {

      parm1.player5 = parm.player5;
      parm1.user5 = parm.user5;
      parm1.userg5 = parm.userg[4];
      parm1.mship5 = parm.mship5;
      parm1.mtype5 = parm.mtype5;
      parm1.mNum5 = parm.mNum5;

   } else {

      parm1.player5 = "";
      parm1.user5 = "";
      parm1.userg5 = "";
      parm1.mship5 = "";
      parm1.mtype5 = "";
      parm1.mNum5 = "";
   }


   //
   //  Go check for any customs
   //
   errorMsg = verifyCustom.checkCustomsGst(parm1, con);     // go check for customs


   if (errorMsg.equals("") && parm.time2 > 0) {        // if we can keep going

      //
      //  do next tee time
      //
      parm1.time = parm.time2;

      parm1.members = parm.memg2;
      parm1.guests = parm.guestsg2;

      if (parm.p5.equals( "Yes" )) {

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
         parm1.userg1 = parm.userg[5];
         parm1.userg2 = parm.userg[6];
         parm1.userg3 = parm.userg[7];
         parm1.userg4 = parm.userg[8];
         parm1.userg5 = parm.userg[9];
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

      } else {

         parm1.player1 = parm.player5;
         parm1.player2 = parm.player6;
         parm1.player3 = parm.player7;
         parm1.player4 = parm.player8;
         parm1.user1 = parm.user5;
         parm1.user2 = parm.user6;
         parm1.user3 = parm.user7;
         parm1.user4 = parm.user8;
         parm1.userg1 = parm.userg[4];
         parm1.userg2 = parm.userg[5];
         parm1.userg3 = parm.userg[6];
         parm1.userg4 = parm.userg[7];
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
         parm1.userg5 = "";
         parm1.mship5 = "";
         parm1.mtype5 = "";
         parm1.mNum5 = "";
      }

      //
      //  Go check for any customs
      //
      errorMsg = verifyCustom.checkCustomsGst(parm1, con);     // go check for customs


      if (errorMsg.equals("") && parm.time3 > 0) {        // if we can keep going

         //
         //  do next tee time
         //
         parm1.time = parm.time3;

         parm1.members = parm.memg3;
         parm1.guests = parm.guestsg3;

         if (parm.p5.equals( "Yes" )) {

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
            parm1.userg1 = parm.userg[10];
            parm1.userg2 = parm.userg[11];
            parm1.userg3 = parm.userg[12];
            parm1.userg4 = parm.userg[13];
            parm1.userg5 = parm.userg[14];
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

         } else {

            parm1.player1 = parm.player9;
            parm1.player2 = parm.player10;
            parm1.player3 = parm.player11;
            parm1.player4 = parm.player12;
            parm1.user1 = parm.user9;
            parm1.user2 = parm.user10;
            parm1.user3 = parm.user11;
            parm1.user4 = parm.user12;
            parm1.userg1 = parm.userg[8];
            parm1.userg2 = parm.userg[9];
            parm1.userg3 = parm.userg[10];
            parm1.userg4 = parm.userg[11];
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
            parm1.userg5 = "";
            parm1.mship5 = "";
            parm1.mtype5 = "";
            parm1.mNum5 = "";
         }

         //
         //  Go check for any customs
         //
         errorMsg = verifyCustom.checkCustomsGst(parm1, con);     // go check for customs

         if (errorMsg.equals("") && parm.time4 > 0) {        // if we can keep going

            //
            //  do next tee time
            //
            parm1.time = parm.time4;

            parm1.members = parm.memg4;
            parm1.guests = parm.guestsg4;

            if (parm.p5.equals( "Yes" )) {

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
               parm1.userg1 = parm.userg[15];
               parm1.userg2 = parm.userg[16];
               parm1.userg3 = parm.userg[17];
               parm1.userg4 = parm.userg[18];
               parm1.userg5 = parm.userg[19];
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

            } else {

               parm1.player1 = parm.player13;
               parm1.player2 = parm.player14;
               parm1.player3 = parm.player15;
               parm1.player4 = parm.player16;
               parm1.user1 = parm.user13;
               parm1.user2 = parm.user14;
               parm1.user3 = parm.user15;
               parm1.user4 = parm.user16;
               parm1.userg1 = parm.userg[12];
               parm1.userg2 = parm.userg[13];
               parm1.userg3 = parm.userg[14];
               parm1.userg4 = parm.userg[15];
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
               parm1.userg5 = "";
               parm1.mship5 = "";
               parm1.mtype5 = "";
               parm1.mNum5 = "";
            }

            //
            //  Go check for any customs
            //
            errorMsg = verifyCustom.checkCustomsGst(parm1, con);     // go check for customs

            if (errorMsg.equals("") && parm.time5 > 0) {        // if we can keep going

               //
               //  do next tee time
               //
               parm1.time = parm.time5;

               parm1.members = parm.memg5;
               parm1.guests = parm.guestsg5;

               if (parm.p5.equals( "Yes" )) {

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
                  parm1.userg1 = parm.userg[20];
                  parm1.userg2 = parm.userg[21];
                  parm1.userg3 = parm.userg[22];
                  parm1.userg4 = parm.userg[23];
                  parm1.userg5 = parm.userg[24];
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

               } else {

                  parm1.player1 = parm.player17;
                  parm1.player2 = parm.player18;
                  parm1.player3 = parm.player19;
                  parm1.player4 = parm.player20;
                  parm1.user1 = parm.user17;
                  parm1.user2 = parm.user18;
                  parm1.user3 = parm.user19;
                  parm1.user4 = parm.user20;
                  parm1.userg1 = parm.userg[16];
                  parm1.userg2 = parm.userg[17];
                  parm1.userg3 = parm.userg[18];
                  parm1.userg4 = parm.userg[19];
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
                  parm1.userg5 = "";
                  parm1.mship5 = "";
                  parm1.mtype5 = "";
                  parm1.mNum5 = "";
               }

               //
               //  Go check for any customs
               //
               errorMsg = verifyCustom.checkCustomsGst(parm1, con);     // go check for customs

            }
         }
      }
   }

   return(errorMsg);

 }         // end of checkCustomsGst


 // *******************************************************************************
 //  Check For Tee Sheet Flags
 //
 //     Checks each member in the tee times for any configured flags to be shown on pro tee sheet.
 //
 // *******************************************************************************
 //
 private void checkTFlag(parmSlotm parm, Connection con) {


   parm.error = false;               // init

   //
   //  Allocate a new parm block for each tee time and call common method to process each.
   //
   parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

   //
   //  Setup the new single parm block
   //
   parm1.date = parm.date;

   parm.tflag1 = "";       // init the flags
   parm.tflag2 = "";
   parm.tflag3 = "";
   parm.tflag4 = "";
   parm.tflag5 = "";
   parm.tflag6 = "";
   parm.tflag7 = "";
   parm.tflag8 = "";
   parm.tflag9 = "";
   parm.tflag10 = "";
   parm.tflag11 = "";
   parm.tflag12 = "";
   parm.tflag13 = "";
   parm.tflag14 = "";
   parm.tflag15 = "";
   parm.tflag16 = "";
   parm.tflag17 = "";
   parm.tflag18 = "";
   parm.tflag19 = "";
   parm.tflag20 = "";
   parm.tflag21 = "";
   parm.tflag22 = "";
   parm.tflag23 = "";
   parm.tflag24 = "";
   parm.tflag25 = "";


   if (parm.p5.equals( "Yes" )) {

      if (!parm.user1.equals( "" ) || !parm.user2.equals( "" ) || !parm.user3.equals( "" ) ||
          !parm.user4.equals( "" ) || !parm.user5.equals( "" )) {

         //
         //  set parms for this group - only need usernames
         //
         parm1.time = parm.time1;
         parm1.user1 = parm.user1;
         parm1.user2 = parm.user2;
         parm1.user3 = parm.user3;
         parm1.user4 = parm.user4;
         parm1.user5 = parm.user5;

         verifySlot.checkTFlag(parm1, con);         // check for tflags

         parm.tflag1 = parm1.tflag1;                // save the flags
         parm.tflag2 = parm1.tflag2;
         parm.tflag3 = parm1.tflag3;
         parm.tflag4 = parm1.tflag4;
         parm.tflag5 = parm1.tflag5;
      }

      if (!parm.user6.equals( "" ) || !parm.user7.equals( "" ) || !parm.user8.equals( "" ) ||
          !parm.user9.equals( "" ) || !parm.user10.equals( "" )) {

         //
         //  set parms for this group - only need usernames
         //
         parm1.time = parm.time2;
         parm1.user1 = parm.user6;
         parm1.user2 = parm.user7;
         parm1.user3 = parm.user8;
         parm1.user4 = parm.user9;
         parm1.user5 = parm.user10;

         verifySlot.checkTFlag(parm1, con);         // check for tflags

         parm.tflag6 = parm1.tflag1;                // save the flags
         parm.tflag7 = parm1.tflag2;
         parm.tflag8 = parm1.tflag3;
         parm.tflag9 = parm1.tflag4;
         parm.tflag10 = parm1.tflag5;
      }

      if (!parm.user11.equals( "" ) || !parm.user12.equals( "" ) || !parm.user13.equals( "" ) ||
          !parm.user14.equals( "" ) || !parm.user15.equals( "" )) {

         //
         //  set parms for this group - only need usernames
         //
         parm1.time = parm.time3;
         parm1.user1 = parm.user11;
         parm1.user2 = parm.user12;
         parm1.user3 = parm.user13;
         parm1.user4 = parm.user14;
         parm1.user5 = parm.user15;

         verifySlot.checkTFlag(parm1, con);         // check for tflags

         parm.tflag11 = parm1.tflag1;                // save the flags
         parm.tflag12 = parm1.tflag2;
         parm.tflag13 = parm1.tflag3;
         parm.tflag14 = parm1.tflag4;
         parm.tflag15 = parm1.tflag5;
      }

      if (!parm.user16.equals( "" ) || !parm.user17.equals( "" ) || !parm.user18.equals( "" ) ||
          !parm.user19.equals( "" ) || !parm.user20.equals( "" )) {

         //
         //  set parms for this group - only need usernames
         //
         parm1.time = parm.time4;
         parm1.user1 = parm.user16;
         parm1.user2 = parm.user17;
         parm1.user3 = parm.user18;
         parm1.user4 = parm.user19;
         parm1.user5 = parm.user20;

         verifySlot.checkTFlag(parm1, con);         // check for tflags

         parm.tflag16 = parm1.tflag1;                // save the flags
         parm.tflag17 = parm1.tflag2;
         parm.tflag18 = parm1.tflag3;
         parm.tflag19 = parm1.tflag4;
         parm.tflag20 = parm1.tflag5;
      }

      if (!parm.user21.equals( "" ) || !parm.user22.equals( "" ) || !parm.user23.equals( "" ) ||
          !parm.user24.equals( "" ) || !parm.user25.equals( "" )) {

         //
         //  set parms for this group - only need usernames
         //
         parm1.time = parm.time5;
         parm1.user1 = parm.user21;
         parm1.user2 = parm.user22;
         parm1.user3 = parm.user23;
         parm1.user4 = parm.user24;
         parm1.user5 = parm.user25;

         verifySlot.checkTFlag(parm1, con);         // check for tflags

         parm.tflag21 = parm1.tflag1;                // save the flags
         parm.tflag22 = parm1.tflag2;
         parm.tflag23 = parm1.tflag3;
         parm.tflag24 = parm1.tflag4;
         parm.tflag25 = parm1.tflag5;
      }

   } else {                       // 4-somes only

      if (!parm.user1.equals( "" ) || !parm.user2.equals( "" ) || !parm.user3.equals( "" ) ||
          !parm.user4.equals( "" )) {

         //
         //  set parms for this group
         //
         parm1.time = parm.time1;
         parm1.user1 = parm.user1;
         parm1.user2 = parm.user2;
         parm1.user3 = parm.user3;
         parm1.user4 = parm.user4;
         parm1.user5 = "";

         verifySlot.checkTFlag(parm1, con);         // check for tflags

         parm.tflag1 = parm1.tflag1;                // save the flags
         parm.tflag2 = parm1.tflag2;
         parm.tflag3 = parm1.tflag3;
         parm.tflag4 = parm1.tflag4;
      }

      if (!parm.user5.equals( "" ) || !parm.user6.equals( "" ) || !parm.user7.equals( "" ) ||
          !parm.user8.equals( "" )) {

         //
         //  set parms for this group
         //
         parm1.time = parm.time2;
         parm1.user1 = parm.user5;
         parm1.user2 = parm.user6;
         parm1.user3 = parm.user7;
         parm1.user4 = parm.user8;
         parm1.user5 = "";

         verifySlot.checkTFlag(parm1, con);         // check for tflags

         parm.tflag5 = parm1.tflag1;                // save the flags
         parm.tflag6 = parm1.tflag2;
         parm.tflag7 = parm1.tflag3;
         parm.tflag8 = parm1.tflag4;
      }

      if (!parm.user9.equals( "" ) || !parm.user10.equals( "" ) || !parm.user11.equals( "" ) ||
          !parm.user12.equals( "" )) {

         //
         //  set parms for this group
         //
         parm1.time = parm.time3;
         parm1.user1 = parm.user9;
         parm1.user2 = parm.user10;
         parm1.user3 = parm.user11;
         parm1.user4 = parm.user12;
         parm1.user5 = "";

         verifySlot.checkTFlag(parm1, con);         // check for tflags

         parm.tflag9 = parm1.tflag1;                // save the flags
         parm.tflag10 = parm1.tflag2;
         parm.tflag11 = parm1.tflag3;
         parm.tflag12 = parm1.tflag4;
      }

      if (!parm.user13.equals( "" ) || !parm.user14.equals( "" ) || !parm.user15.equals( "" ) ||
          !parm.user16.equals( "" )) {

         //
         //  set parms for this group
         //
         parm1.time = parm.time4;
         parm1.user1 = parm.user13;
         parm1.user2 = parm.user14;
         parm1.user3 = parm.user15;
         parm1.user4 = parm.user16;
         parm1.user5 = "";

         verifySlot.checkTFlag(parm1, con);         // check for tflags

         parm.tflag13 = parm1.tflag1;                // save the flags
         parm.tflag14 = parm1.tflag2;
         parm.tflag15 = parm1.tflag3;
         parm.tflag16 = parm1.tflag4;
      }

      if (!parm.user17.equals( "" ) || !parm.user18.equals( "" ) || !parm.user19.equals( "" ) ||
          !parm.user20.equals( "" )) {

         //
         //  set parms for this group
         //
         parm1.time = parm.time5;
         parm1.user1 = parm.user17;
         parm1.user2 = parm.user18;
         parm1.user3 = parm.user19;
         parm1.user4 = parm.user20;
         parm1.user5 = "";

         verifySlot.checkTFlag(parm1, con);         // check for tflags

         parm.tflag17 = parm1.tflag1;                // save the flags
         parm.tflag18 = parm1.tflag2;
         parm.tflag19 = parm1.tflag3;
         parm.tflag20 = parm1.tflag4;
      }
   }

 }         // end of checkTFlag



 // *******************************************************************************
 //  Check Merion Guest Restrictions
 //
 //     On East course, check for max allowed guest times per hour.
 //
 // *******************************************************************************
 //
 private boolean checkMerionRes(Connection con, PrintWriter out, parmSlotm parm) {


   boolean check = false;
   parm.error = false;               // init

   //
   //  Allocate a new parm block for each tee time and call common method to process each.
   //
   parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

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
   parm1.oldPlayer1 = "";       // always empty from here
   parm1.oldPlayer2 = "";
   parm1.oldPlayer3 = "";
   parm1.oldPlayer4 = "";
   parm1.oldPlayer5 = "";
   parm1.fb = parm.fb;
   parm1.ind = parm.ind;      // index value
   parm1.sfb = parm.sfb;
   parm1.club = parm.club;    // name of club


   //
   //  Do each tee time - one at a time
   //
   try {

      if (parm.guestsg1 > 0) {           // if any guests in this group

         //
         //  set parms for first tee time
         //
         parm1.time = parm.time1;
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

         if (parm.p5.equals( "Yes" )) {

            parm1.player5 = parm.player5;
            parm1.user5 = parm.user5;
            parm1.mship5 = parm.mship5;
            parm1.mtype5 = parm.mtype5;
            parm1.mNum5 = parm.mNum5;

         } else {

            parm1.player5 = "";
            parm1.user5 = "";
            parm1.mship5 = "";
            parm1.mtype5 = "";
            parm1.mNum5 = "";
         }

         check = verifySlot.checkMerionG(parm1, con);                   // check for restrictions !!!!!

      }

      if (check == false && parm.time2 > 0) {        // if we can keep going

         if (parm.guestsg2 > 0) {           // if any guests in this group

            //
            //  do next tee time
            //
            parm1.time = parm.time2;

            if (parm.p5.equals( "Yes" )) {

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

            } else {

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

            check = verifySlot.checkMerionG(parm1, con);                   // check for restrictions !!!!!

         }

         if (check == false && parm.time3 > 0) {        // if we can keep going

            if (parm.guestsg3 > 0) {           // if any guests in this group

               //
               //  do next tee time
               //
               parm1.time = parm.time3;

               if (parm.p5.equals( "Yes" )) {

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

               } else {

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

               check = verifySlot.checkMerionG(parm1, con);                   // check for restrictions !!!!!

            }

            if (check == false && parm.time4 > 0) {        // if we can keep going

               if (parm.guestsg4 > 0) {           // if any guests in this group

                  //
                  //  do next tee time
                  //
                  parm1.time = parm.time4;

                  if (parm.p5.equals( "Yes" )) {

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

                  } else {

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

                  check = verifySlot.checkMerionG(parm1, con);                   // check for restrictions !!!!!

               }

               if (check == false && parm.time5 > 0) {        // if we can keep going

                  if (parm.guestsg5 > 0) {           // if any guests in this group

                     //
                     //  do next tee time
                     //
                     parm1.time = parm.time5;

                     if (parm.p5.equals( "Yes" )) {

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

                     } else {

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

                     check = verifySlot.checkMerionG(parm1, con);                   // check for restrictions !!!!!

                  }
               }
            }
         }
      }

   }
   catch (Exception e7) {

      dbError(out, e7);
      parm.error = true;               // inform caller of error
   }

   return(check);

 }         // end of checkMerionRes


 // *******************************************************************************
 //  Check Merion Guest Quota Restrictions
 //
 //     On East course, check for max allowed guest times outstanding per family.
 //
 // *******************************************************************************
 //
 private String checkMerionGRes(Connection con, PrintWriter out, parmSlotm parm) {


   String checkPlayer = "";
   boolean check = false;
   parm.error = false;               // init

   //
   //  Allocate a new parm block for each tee time and call common method to process each.
   //
   parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

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
   parm1.oldPlayer1 = "";       // always empty from here
   parm1.oldPlayer2 = "";
   parm1.oldPlayer3 = "";
   parm1.oldPlayer4 = "";
   parm1.oldPlayer5 = "";
   parm1.fb = parm.fb;
   parm1.ind = parm.ind;      // index value
   parm1.sfb = parm.sfb;
   parm1.club = parm.club;    // name of club


   //
   //  Do each tee time - one at a time
   //
   try {

      if (parm.guestsg1 > 0) {           // if any guests in this group

         //
         //  set parms for first tee time
         //
         parm1.time = parm.time1;
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
         parm1.userg1 = parm.userg[0];
         parm1.userg2 = parm.userg[1];
         parm1.userg3 = parm.userg[2];
         parm1.userg4 = parm.userg[3];

         if (parm.p5.equals( "Yes" )) {

            parm1.player5 = parm.player5;
            parm1.user5 = parm.user5;
            parm1.mship5 = parm.mship5;
            parm1.mtype5 = parm.mtype5;
            parm1.mNum5 = parm.mNum5;
            parm1.userg5 = parm.userg[4];

         } else {

            parm1.player5 = "";
            parm1.user5 = "";
            parm1.mship5 = "";
            parm1.mtype5 = "";
            parm1.mNum5 = "";
            parm1.userg5 = "";
         }

         check = verifySlot.checkMerionGres(parm1, con);                   // check for restrictions !!!!!

         if (check == true) {                // if error found

            checkPlayer = parm1.player;     // get player name
         }

      }

      if (check == false && parm.time2 > 0) {        // if we can keep going

         if (parm.guestsg2 > 0) {           // if any guests in this group

            //
            //  do next tee time
            //
            parm1.time = parm.time2;

            if (parm.p5.equals( "Yes" )) {

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
               parm1.userg1 = parm.userg[5];
               parm1.userg2 = parm.userg[6];
               parm1.userg3 = parm.userg[7];
               parm1.userg4 = parm.userg[8];
               parm1.userg5 = parm.userg[9];

            } else {

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
               parm1.userg1 = parm.userg[4];
               parm1.userg2 = parm.userg[5];
               parm1.userg3 = parm.userg[6];
               parm1.userg4 = parm.userg[7];

               parm1.player5 = "";
               parm1.user5 = "";
               parm1.mship5 = "";
               parm1.mtype5 = "";
               parm1.mNum5 = "";
               parm1.userg5 = "";
            }

            check = verifySlot.checkMerionGres(parm1, con);                   // check for restrictions !!!!!

            if (check == true) {                // if error found

               checkPlayer = parm1.player;     // get player name
            }

         }

         if (check == false && parm.time3 > 0) {        // if we can keep going

            if (parm.guestsg3 > 0) {           // if any guests in this group

               //
               //  do next tee time
               //
               parm1.time = parm.time3;

               if (parm.p5.equals( "Yes" )) {

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
                  parm1.userg1 = parm.userg[10];
                  parm1.userg2 = parm.userg[11];
                  parm1.userg3 = parm.userg[12];
                  parm1.userg4 = parm.userg[13];
                  parm1.userg5 = parm.userg[14];

               } else {

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
                  parm1.userg1 = parm.userg[8];
                  parm1.userg2 = parm.userg[9];
                  parm1.userg3 = parm.userg[10];
                  parm1.userg4 = parm.userg[11];

                  parm1.player5 = "";
                  parm1.user5 = "";
                  parm1.mship5 = "";
                  parm1.mtype5 = "";
                  parm1.mNum5 = "";
                  parm1.userg5 = "";
               }

               check = verifySlot.checkMerionGres(parm1, con);                   // check for restrictions !!!!!

               if (check == true) {                // if error found

                  checkPlayer = parm1.player;     // get player name
               }

            }

            if (check == false && parm.time4 > 0) {        // if we can keep going

               if (parm.guestsg4 > 0) {           // if any guests in this group

                  //
                  //  do next tee time
                  //
                  parm1.time = parm.time4;

                  if (parm.p5.equals( "Yes" )) {

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
                     parm1.userg1 = parm.userg[15];
                     parm1.userg2 = parm.userg[16];
                     parm1.userg3 = parm.userg[17];
                     parm1.userg4 = parm.userg[18];
                     parm1.userg5 = parm.userg[19];

                  } else {

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
                     parm1.userg1 = parm.userg[12];
                     parm1.userg2 = parm.userg[13];
                     parm1.userg3 = parm.userg[14];
                     parm1.userg4 = parm.userg[15];

                     parm1.player5 = "";
                     parm1.user5 = "";
                     parm1.mship5 = "";
                     parm1.mtype5 = "";
                     parm1.mNum5 = "";
                     parm1.userg5 = "";
                  }

                  check = verifySlot.checkMerionGres(parm1, con);                   // check for restrictions !!!!!

                  if (check == true) {                // if error found

                     checkPlayer = parm1.player;     // get player name
                  }

               }

               if (check == false && parm.time5 > 0) {        // if we can keep going

                  if (parm.guestsg5 > 0) {           // if any guests in this group

                     //
                     //  do next tee time
                     //
                     parm1.time = parm.time5;

                     if (parm.p5.equals( "Yes" )) {

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
                        parm1.userg1 = parm.userg[20];
                        parm1.userg2 = parm.userg[21];
                        parm1.userg3 = parm.userg[22];
                        parm1.userg4 = parm.userg[23];
                        parm1.userg5 = parm.userg[24];

                     } else {

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
                        parm1.userg1 = parm.userg[16];
                        parm1.userg2 = parm.userg[17];
                        parm1.userg3 = parm.userg[18];
                        parm1.userg4 = parm.userg[19];

                        parm1.player5 = "";
                        parm1.user5 = "";
                        parm1.mship5 = "";
                        parm1.mtype5 = "";
                        parm1.mNum5 = "";
                        parm1.userg5 = "";
                     }

                     check = verifySlot.checkMerionGres(parm1, con);                   // check for restrictions !!!!!

                     if (check == true) {                // if error found

                        checkPlayer = parm1.player;     // get player name
                     }
                  }
               }
            }
         }
      }

   }
   catch (Exception e7) {

      dbError(out, e7);
      parm.error = true;               // inform caller of error
   }

   return(checkPlayer);

 }         // end of checkMerionGRes


 // *********************************************************
 //
 //  checkOakGuestQuota - special Guest processing for Oakmont CC.
 //
 //     At this point we know this is Oakmont, there is more than one guest
 //     in this tee time and it is Feb, Mar or Apr.
 //
 //     Restrictions:
 //
 //         Members can book guest times in advance (all year), however they
 //         can only book up to 10 per month during Feb, Mar and Apr.  The guest times
 //         can be for any time of the season, we only check when the time was booked.
 //         Therefore, we must track when the tee time was actually created.
 //
 //      Note:  5-somes not allowed at Oakmont
 //
 //      **** See also verifyCustom.checkOakmontGuestQuota ************************
 //
 // *********************************************************
 //
 private String checkOakGuestQuota(parmSlotm slotParms, int month, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean error = false;

   String player = "";
   String errorMsg = "";

   int i = 0;
   int count = 0;
   int max = 7;                             // max is 8 advance guest times per month - use > 7 for compare

   String [] userA = new String [20];        // array to hold the usernames
   String [] playerA = new String [20];      // array to hold the player names

   userA[0] = slotParms.user1;              // get values from this request
   userA[1] = slotParms.user2;
   userA[2] = slotParms.user3;
   userA[3] = slotParms.user4;
   userA[4] = slotParms.user5;
   userA[5] = slotParms.user6;
   userA[6] = slotParms.user7;
   userA[7] = slotParms.user8;
   userA[8] = slotParms.user9;
   userA[9] = slotParms.user10;
   userA[10] = slotParms.user11;
   userA[11] = slotParms.user12;
   userA[12] = slotParms.user13;
   userA[13] = slotParms.user14;
   userA[14] = slotParms.user15;
   userA[15] = slotParms.user16;
   userA[16] = slotParms.user17;
   userA[17] = slotParms.user18;
   userA[18] = slotParms.user19;
   userA[19] = slotParms.user20;

   playerA[0] = slotParms.player1;
   playerA[1] = slotParms.player2;
   playerA[2] = slotParms.player3;
   playerA[3] = slotParms.player4;
   playerA[4] = slotParms.player5;
   playerA[5] = slotParms.player6;
   playerA[6] = slotParms.player7;
   playerA[7] = slotParms.player8;
   playerA[8] = slotParms.player9;
   playerA[9] = slotParms.player10;
   playerA[10] = slotParms.player11;
   playerA[11] = slotParms.player12;
   playerA[12] = slotParms.player13;
   playerA[13] = slotParms.player14;
   playerA[14] = slotParms.player15;
   playerA[15] = slotParms.player16;
   playerA[16] = slotParms.player17;
   playerA[17] = slotParms.player18;
   playerA[18] = slotParms.player19;
   playerA[19] = slotParms.player20;


   try {

      //
      //  Check each player for member followed by guest
      //
      for (i = 0; i < 19; i++) {             // check first 19 players (no 5-somes at Oakmont, if 20th not guest then doesn't matter)

         if (error == false) {              // if error not already hit

            if (!userA[i].equals( "" ) && slotParms.userg[i+1].equals( userA[i] )) {       // if player followed by his/her guest

               count = 0;

               //
               //   Check teecurr for other guest times for this member that were scheduled during this month
               //
               pstmt = con.prepareStatement (
                  "SELECT COUNT(*) " +
                  "FROM teecurr2 " +
                  "WHERE custom_int = ? AND " +
                  "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ?)");

               pstmt.clearParameters();
               pstmt.setInt(1, month);
               pstmt.setString(2, userA[i]);
               pstmt.setString(3, userA[i]);
               pstmt.setString(4, userA[i]);
               pstmt.setString(5, userA[i]);
               rs = pstmt.executeQuery();

               if (rs.next()) {

                  count = rs.getInt("COUNT(*)");
               }

               pstmt.close();


               if (count > max) {                         // if 10 advance guest times already created this month

                  error = true;                           // indicate error
                  player = playerA[i];                    // save player name for error message
               }
            }
         }
      }              // end of FOR loop (do each player)

   }
   catch (Exception e) {

      errorMsg = "Error checking for Oakmont guests - Proshop_slotm.checkOakGuestQuota " + e.getMessage();
      SystemUtils.logError(errorMsg);                                   // log it
   }

   return(player);

 }         // end of checkOakGuestQuota



 // *********************************************************
 //
 //  checkBaltGuestQuota - special Guest processing for Baltusrol GC.
 //
 //     At this point we know this is baltusrol and there is more than one guest
 //     in this tee time.
 //
 //     Restrictions:
 //
 //         Members can have up to 3 guest times scheduled in advance.
 //
 //      Note:  5-somes not allowed at Baltusrol
 //
 //      **** See also verifyCustom.checkBaltusrolGuestQuota ************************
 //
 // *********************************************************
 //
 private String checkBaltGuestQuota(parmSlotm slotParms, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean error = false;

   String player = "";
   String errorMsg = "";

   int i = 0;
   int count = 0;
   int max = 2;                             // max is 3 advance guest times - use 2 to include this one

   String [] userA = new String [20];        // array to hold the usernames
   String [] playerA = new String [20];      // array to hold the player names

   userA[0] = slotParms.user1;              // get values from this request
   userA[1] = slotParms.user2;
   userA[2] = slotParms.user3;
   userA[3] = slotParms.user4;
   userA[4] = slotParms.user5;
   userA[5] = slotParms.user6;
   userA[6] = slotParms.user7;
   userA[7] = slotParms.user8;
   userA[8] = slotParms.user9;
   userA[9] = slotParms.user10;
   userA[10] = slotParms.user11;
   userA[11] = slotParms.user12;
   userA[12] = slotParms.user13;
   userA[13] = slotParms.user14;
   userA[14] = slotParms.user15;
   userA[15] = slotParms.user16;
   userA[16] = slotParms.user17;
   userA[17] = slotParms.user18;
   userA[18] = slotParms.user19;
   userA[19] = slotParms.user20;

   playerA[0] = slotParms.player1;
   playerA[1] = slotParms.player2;
   playerA[2] = slotParms.player3;
   playerA[3] = slotParms.player4;
   playerA[4] = slotParms.player5;
   playerA[5] = slotParms.player6;
   playerA[6] = slotParms.player7;
   playerA[7] = slotParms.player8;
   playerA[8] = slotParms.player9;
   playerA[9] = slotParms.player10;
   playerA[10] = slotParms.player11;
   playerA[11] = slotParms.player12;
   playerA[12] = slotParms.player13;
   playerA[13] = slotParms.player14;
   playerA[14] = slotParms.player15;
   playerA[15] = slotParms.player16;
   playerA[16] = slotParms.player17;
   playerA[17] = slotParms.player18;
   playerA[18] = slotParms.player19;
   playerA[19] = slotParms.player20;


   try {

      //
      //  Check each player for member followed by guest
      //
      for (i = 0; i < 19; i++) {             // check first 19 players (no 5-somes at Baltusrol, if 20th not guest then doesn't matter)

         if (error == false) {              // if error not already hit

            if (!userA[i].equals( "" ) && slotParms.userg[i+1].equals( userA[i] )) {       // if player followed by his/her guest

               count = 0;

               //
               //   Check teecurr for other guest times for this member that were scheduled during this month
               //
               pstmt = con.prepareStatement (
                  "SELECT COUNT(*) " +
                  "FROM teecurr2 " +
                  "WHERE teecurr_id != ? AND teecurr_id != ? AND teecurr_id != ? AND teecurr_id != ? AND teecurr_id != ? AND " +
                  "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ?)");

               pstmt.clearParameters();
               pstmt.setInt(1, slotParms.teecurr_idA[0]);
               pstmt.setInt(2, slotParms.teecurr_idA[1]);
               pstmt.setInt(3, slotParms.teecurr_idA[2]);
               pstmt.setInt(4, slotParms.teecurr_idA[3]);
               pstmt.setInt(5, slotParms.teecurr_idA[4]);
               pstmt.setString(6, userA[i]);
               pstmt.setString(7, userA[i]);
               pstmt.setString(8, userA[i]);
               pstmt.setString(9, userA[i]);
               rs = pstmt.executeQuery();

               if (rs.next()) {

                  count = rs.getInt("COUNT(*)");
               }

               pstmt.close();


               if (count > max) {                         // if 3 advance guest times already exist

                  error = true;                           // indicate error
                  player = playerA[i];                    // save player name for error message
               }
            }
         }
      }              // end of FOR loop (do each player)

   }
   catch (Exception e) {

      errorMsg = "Error checking for Baltusrol guests - Proshop_slotm.checkBaltGuestQuota " + e.getMessage();
      SystemUtils.logError(errorMsg);                                   // log it
   }

   return(player);

 }         // end of checkBaltGuestQuota


 private String checkWoodwayGuests(parmSlotm slotParms, Connection con) {

    boolean error = false;

    String player = "";

    String [] userA = new String [25];        // array to hold the usernames
    String [] usergA = new String [25];
    String [] mshipA = new String [25];       // array to hold the players' membership types
    String [] playerA = new String [25];      // array to hold the player names

    for (int i=0; i<25; i++) {
        usergA[i] = slotParms.userg[i];              // get values from this request
    }

    mshipA[0] = slotParms.mship1;              // get values from this request
    mshipA[1] = slotParms.mship2;
    mshipA[2] = slotParms.mship3;
    mshipA[3] = slotParms.mship4;
    mshipA[4] = slotParms.mship5;
    mshipA[5] = slotParms.mship6;
    mshipA[6] = slotParms.mship7;
    mshipA[7] = slotParms.mship8;
    mshipA[8] = slotParms.mship9;
    mshipA[9] = slotParms.mship10;
    mshipA[10] = slotParms.mship11;
    mshipA[11] = slotParms.mship12;
    mshipA[12] = slotParms.mship13;
    mshipA[13] = slotParms.mship14;
    mshipA[14] = slotParms.mship15;
    mshipA[15] = slotParms.mship16;
    mshipA[16] = slotParms.mship17;
    mshipA[17] = slotParms.mship18;
    mshipA[18] = slotParms.mship19;
    mshipA[19] = slotParms.mship20;
    mshipA[20] = slotParms.mship21;
    mshipA[21] = slotParms.mship22;
    mshipA[22] = slotParms.mship23;
    mshipA[23] = slotParms.mship24;
    mshipA[24] = slotParms.mship25;

    userA[0] = slotParms.user1;              // get values from this request
    userA[1] = slotParms.user2;
    userA[2] = slotParms.user3;
    userA[3] = slotParms.user4;
    userA[4] = slotParms.user5;
    userA[5] = slotParms.user6;
    userA[6] = slotParms.user7;
    userA[7] = slotParms.user8;
    userA[8] = slotParms.user9;
    userA[9] = slotParms.user10;
    userA[10] = slotParms.user11;
    userA[11] = slotParms.user12;
    userA[12] = slotParms.user13;
    userA[13] = slotParms.user14;
    userA[14] = slotParms.user15;
    userA[15] = slotParms.user16;
    userA[16] = slotParms.user17;
    userA[17] = slotParms.user18;
    userA[18] = slotParms.user19;
    userA[19] = slotParms.user20;
    userA[20] = slotParms.user21;
    userA[21] = slotParms.user22;
    userA[22] = slotParms.user23;
    userA[23] = slotParms.user24;
    userA[24] = slotParms.user25;

    playerA[0] = slotParms.player1;
    playerA[1] = slotParms.player2;
    playerA[2] = slotParms.player3;
    playerA[3] = slotParms.player4;
    playerA[4] = slotParms.player5;
    playerA[5] = slotParms.player6;
    playerA[6] = slotParms.player7;
    playerA[7] = slotParms.player8;
    playerA[8] = slotParms.player9;
    playerA[9] = slotParms.player10;
    playerA[10] = slotParms.player11;
    playerA[11] = slotParms.player12;
    playerA[12] = slotParms.player13;
    playerA[13] = slotParms.player14;
    playerA[14] = slotParms.player15;
    playerA[15] = slotParms.player16;
    playerA[16] = slotParms.player17;
    playerA[17] = slotParms.player18;
    playerA[18] = slotParms.player19;
    playerA[19] = slotParms.player20;
    playerA[20] = slotParms.player21;
    playerA[21] = slotParms.player22;
    playerA[22] = slotParms.player23;
    playerA[23] = slotParms.player24;
    playerA[24] = slotParms.player25;

    for (int i=0; i<5; i++) {

        if (!error) {
            int j = i * 5;

            parmSlot parm = new parmSlot();

            parm.day = slotParms.day;
            parm.date = slotParms.date;

            parm.player1 = playerA[j];
            parm.player2 = playerA[j+1];
            parm.player3 = playerA[j+2];
            parm.player4 = playerA[j+3];
            parm.player5 = playerA[j+4];

            parm.user1 = userA[j];
            parm.user2 = userA[j+1];
            parm.user3 = userA[j+2];
            parm.user4 = userA[j+3];
            parm.user5 = userA[j+4];

            parm.userg1 = usergA[j];
            parm.userg2 = usergA[j+1];
            parm.userg3 = usergA[j+2];
            parm.userg4 = usergA[j+3];
            parm.userg5 = usergA[j+4];

            parm.mship1 = mshipA[j];
            parm.mship2 = mshipA[j+1];
            parm.mship3 = mshipA[j+2];
            parm.mship4 = mshipA[j+3];
            parm.mship5 = mshipA[j+4];

            error = verifyCustom.checkWoodwayGuests(parm, con);

            if (error) {
                player = parm.player;
            }
        }
    }

    return player;
 }


 // *********************************************************
 //
 //  Congressional CC - check number of guests per 'Junior A' mships.
 //
 //     'Junior A' Membership Types can only have one
 //     guest per member on the Open Course Tues - Fri.
 //
 // *********************************************************
 //
 private String checkCongressionalJrAGuests(parmSlotm slotParms) {


   String player = "";
   String user = "";
   String day = slotParms.day;
   String mship = "Junior A";

   boolean error = false;


   //
   //  Check if Open Course and Tues - Fri
   //
   if (slotParms.course.equals( "Open Course" ) && (day.equals( "Tuesday" ) || day.equals( "Wednesday" ) ||
       day.equals( "Thursday" ) || day.equals( "Friday" ))) {

      //
      //  Now check if any members are 'Junior A' mship types
      //
      if (slotParms.mship1.equals( mship )) {

         user = slotParms.user1;

         if (slotParms.userg[1].equals( user ) &&
             (slotParms.userg[2].equals( user ) || slotParms.userg[3].equals( user ) ||
             slotParms.userg[4].equals( user ) || slotParms.userg[5].equals( user ) ||
             slotParms.userg[6].equals( user ) || slotParms.userg[7].equals( user ) ||
             slotParms.userg[8].equals( user ) || slotParms.userg[9].equals( user ) ||
             slotParms.userg[10].equals( user ) || slotParms.userg[1].equals( user ) ||
             slotParms.userg[12].equals( user ) || slotParms.userg[13].equals( user ) ||
             slotParms.userg[14].equals( user ) || slotParms.userg[15].equals( user ) ||
             slotParms.userg[16].equals( user ) || slotParms.userg[17].equals( user ) ||
             slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player1;
         }
      }

      if (slotParms.mship2.equals( mship ) && error == false) {

         user = slotParms.user2;

         if (slotParms.userg[2].equals( user ) &&
             (slotParms.userg[3].equals( user ) ||
             slotParms.userg[4].equals( user ) || slotParms.userg[5].equals( user ) ||
             slotParms.userg[6].equals( user ) || slotParms.userg[7].equals( user ) ||
             slotParms.userg[8].equals( user ) || slotParms.userg[9].equals( user ) ||
             slotParms.userg[10].equals( user ) || slotParms.userg[1].equals( user ) ||
             slotParms.userg[12].equals( user ) || slotParms.userg[13].equals( user ) ||
             slotParms.userg[14].equals( user ) || slotParms.userg[15].equals( user ) ||
             slotParms.userg[16].equals( user ) || slotParms.userg[17].equals( user ) ||
             slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player2;
         }
      }

      if (slotParms.mship3.equals( mship ) && error == false) {

         user = slotParms.user3;

         if (slotParms.userg[3].equals( user ) &&
             (slotParms.userg[4].equals( user ) || slotParms.userg[5].equals( user ) ||
             slotParms.userg[6].equals( user ) || slotParms.userg[7].equals( user ) ||
             slotParms.userg[8].equals( user ) || slotParms.userg[9].equals( user ) ||
             slotParms.userg[10].equals( user ) || slotParms.userg[1].equals( user ) ||
             slotParms.userg[12].equals( user ) || slotParms.userg[13].equals( user ) ||
             slotParms.userg[14].equals( user ) || slotParms.userg[15].equals( user ) ||
             slotParms.userg[16].equals( user ) || slotParms.userg[17].equals( user ) ||
             slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player3;
         }
      }

      if (slotParms.mship4.equals( mship ) && error == false) {

         user = slotParms.user4;

         if (slotParms.userg[4].equals( user ) &&
             (slotParms.userg[5].equals( user ) ||
             slotParms.userg[6].equals( user ) || slotParms.userg[7].equals( user ) ||
             slotParms.userg[8].equals( user ) || slotParms.userg[9].equals( user ) ||
             slotParms.userg[10].equals( user ) || slotParms.userg[1].equals( user ) ||
             slotParms.userg[12].equals( user ) || slotParms.userg[13].equals( user ) ||
             slotParms.userg[14].equals( user ) || slotParms.userg[15].equals( user ) ||
             slotParms.userg[16].equals( user ) || slotParms.userg[17].equals( user ) ||
             slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player4;
         }
      }

      if (slotParms.mship5.equals( mship ) && error == false) {

         user = slotParms.user5;

         if (slotParms.userg[5].equals( user ) &&
             (slotParms.userg[6].equals( user ) || slotParms.userg[7].equals( user ) ||
             slotParms.userg[8].equals( user ) || slotParms.userg[9].equals( user ) ||
             slotParms.userg[10].equals( user ) || slotParms.userg[1].equals( user ) ||
             slotParms.userg[12].equals( user ) || slotParms.userg[13].equals( user ) ||
             slotParms.userg[14].equals( user ) || slotParms.userg[15].equals( user ) ||
             slotParms.userg[16].equals( user ) || slotParms.userg[17].equals( user ) ||
             slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player5;
         }
      }

      if (slotParms.mship6.equals( mship ) && error == false) {

         user = slotParms.user6;

         if (slotParms.userg[6].equals( user ) &&
             (slotParms.userg[7].equals( user ) ||
             slotParms.userg[8].equals( user ) || slotParms.userg[9].equals( user ) ||
             slotParms.userg[10].equals( user ) || slotParms.userg[1].equals( user ) ||
             slotParms.userg[12].equals( user ) || slotParms.userg[13].equals( user ) ||
             slotParms.userg[14].equals( user ) || slotParms.userg[15].equals( user ) ||
             slotParms.userg[16].equals( user ) || slotParms.userg[17].equals( user ) ||
             slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player6;
         }
      }

      if (slotParms.mship7.equals( mship ) && error == false) {

         user = slotParms.user7;

         if (slotParms.userg[7].equals( user ) &&
             (slotParms.userg[8].equals( user ) || slotParms.userg[9].equals( user ) ||
             slotParms.userg[10].equals( user ) || slotParms.userg[1].equals( user ) ||
             slotParms.userg[12].equals( user ) || slotParms.userg[13].equals( user ) ||
             slotParms.userg[14].equals( user ) || slotParms.userg[15].equals( user ) ||
             slotParms.userg[16].equals( user ) || slotParms.userg[17].equals( user ) ||
             slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player7;
         }
      }

      if (slotParms.mship8.equals( mship ) && error == false) {

         user = slotParms.user8;

         if (slotParms.userg[8].equals( user ) &&
             (slotParms.userg[9].equals( user ) ||
             slotParms.userg[10].equals( user ) || slotParms.userg[1].equals( user ) ||
             slotParms.userg[12].equals( user ) || slotParms.userg[13].equals( user ) ||
             slotParms.userg[14].equals( user ) || slotParms.userg[15].equals( user ) ||
             slotParms.userg[16].equals( user ) || slotParms.userg[17].equals( user ) ||
             slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player8;
         }
      }

      if (slotParms.mship9.equals( mship ) && error == false) {

         user = slotParms.user9;

         if (slotParms.userg[9].equals( user ) &&
             (slotParms.userg[10].equals( user ) || slotParms.userg[1].equals( user ) ||
             slotParms.userg[12].equals( user ) || slotParms.userg[13].equals( user ) ||
             slotParms.userg[14].equals( user ) || slotParms.userg[15].equals( user ) ||
             slotParms.userg[16].equals( user ) || slotParms.userg[17].equals( user ) ||
             slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player9;
         }
      }

      if (slotParms.mship10.equals( mship ) && error == false) {

         user = slotParms.user10;

         if (slotParms.userg[10].equals( user ) &&
             (slotParms.userg[1].equals( user ) ||
             slotParms.userg[12].equals( user ) || slotParms.userg[13].equals( user ) ||
             slotParms.userg[14].equals( user ) || slotParms.userg[15].equals( user ) ||
             slotParms.userg[16].equals( user ) || slotParms.userg[17].equals( user ) ||
             slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player10;
         }
      }

      if (slotParms.mship11.equals( mship ) && error == false) {

         user = slotParms.user11;

         if (slotParms.userg[1].equals( user ) &&
             (slotParms.userg[12].equals( user ) || slotParms.userg[13].equals( user ) ||
             slotParms.userg[14].equals( user ) || slotParms.userg[15].equals( user ) ||
             slotParms.userg[16].equals( user ) || slotParms.userg[17].equals( user ) ||
             slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player11;
         }
      }

      if (slotParms.mship12.equals( mship ) && error == false) {

         user = slotParms.user12;

         if (slotParms.userg[12].equals( user ) &&
             (slotParms.userg[13].equals( user ) ||
             slotParms.userg[14].equals( user ) || slotParms.userg[15].equals( user ) ||
             slotParms.userg[16].equals( user ) || slotParms.userg[17].equals( user ) ||
             slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player12;
         }
      }

      if (slotParms.mship13.equals( mship ) && error == false) {

         user = slotParms.user13;

         if (slotParms.userg[13].equals( user ) &&
             (slotParms.userg[14].equals( user ) || slotParms.userg[15].equals( user ) ||
             slotParms.userg[16].equals( user ) || slotParms.userg[17].equals( user ) ||
             slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player13;
         }
      }

      if (slotParms.mship14.equals( mship ) && error == false) {

         user = slotParms.user14;

         if (slotParms.userg[14].equals( user ) &&
             (slotParms.userg[15].equals( user ) ||
             slotParms.userg[16].equals( user ) || slotParms.userg[17].equals( user ) ||
             slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player14;
         }
      }

      if (slotParms.mship15.equals( mship ) && error == false) {

         user = slotParms.user15;

         if (slotParms.userg[15].equals( user ) &&
             (slotParms.userg[16].equals( user ) || slotParms.userg[17].equals( user ) ||
             slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player15;
         }
      }

      if (slotParms.mship16.equals( mship ) && error == false) {

         user = slotParms.user16;

         if (slotParms.userg[16].equals( user ) &&
             (slotParms.userg[17].equals( user ) ||
             slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player16;
         }
      }

      if (slotParms.mship17.equals( mship ) && error == false) {

         user = slotParms.user17;

         if (slotParms.userg[17].equals( user ) &&
             (slotParms.userg[18].equals( user ) || slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player17;
         }
      }

      if (slotParms.mship18.equals( mship ) && error == false) {

         user = slotParms.user18;

         if (slotParms.userg[18].equals( user ) &&
             (slotParms.userg[19].equals( user ) ||
             slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player18;
         }
      }

      if (slotParms.mship19.equals( mship ) && error == false) {

         user = slotParms.user19;

         if (slotParms.userg[19].equals( user ) &&
             (slotParms.userg[20].equals( user ) || slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player19;
         }
      }

      if (slotParms.mship20.equals( mship ) && error == false) {

         user = slotParms.user20;

         if (slotParms.userg[20].equals( user ) &&
             (slotParms.userg[21].equals( user ) ||
             slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player20;
         }
      }

      if (slotParms.mship21.equals( mship ) && error == false) {

         user = slotParms.user21;

         if (slotParms.userg[21].equals( user ) &&
             (slotParms.userg[22].equals( user ) || slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player21;
         }
      }

      if (slotParms.mship22.equals( mship ) && error == false) {

         user = slotParms.user22;

         if (slotParms.userg[22].equals( user ) &&
             (slotParms.userg[23].equals( user ) ||
             slotParms.userg[24].equals( user ))) {

            error = true;

            player = slotParms.player22;
         }
      }

      if (slotParms.mship23.equals( mship ) && error == false) {

         user = slotParms.user23;

         if (slotParms.userg[23].equals( user ) &&
             slotParms.userg[24].equals( user )) {

            player = slotParms.player23;
         }
      }
   }

   return(player);

 }         // end of checkCongressionalJrAGuests


 // *******************************************************************************
 //
 //  Congressional CC - check if guest type is allowed
 //
 // *******************************************************************************
 //
 private boolean checkCongressionalGuests(parmSlotm slotParms) {


   boolean error = false;


   //
   //  'Cert Jr Guest' must follow a Certified Dependent only
   //
   if (slotParms.player1.startsWith( "Cert Jr Guest" )) {

      error = true;         // illegal

   } else {

      if (slotParms.player2.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype1.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player3.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype2.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player4.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype3.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player5.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype4.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player6.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype5.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player7.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype6.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player8.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype7.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player9.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype8.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player10.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype9.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player11.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype10.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player12.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype11.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player13.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype12.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player14.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype13.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player15.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype14.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player16.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype15.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player17.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype16.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player18.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype17.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player19.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype18.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player20.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype19.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player21.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype20.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player22.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype21.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player23.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype22.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player24.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype23.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player25.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype24.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }
   }

   return(error);

 }         // end of checkCongressionalGuests


 // *******************************************************************************
 //
 //  Wilmington CC - Check for max guests during time frame
 //
 // *******************************************************************************
 //
 private boolean checkWilGuests(Connection con, parmSlotm parm) {


   boolean check = false;
   parm.error = false;               // init

   //
   //  Allocate a new parm block and call common method to process each.
   //
   parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

   //
   //  Setup the new single parm block
   //
   parm1.date = parm.date;
   parm1.mm = parm.mm;
   parm1.yy = parm.yy;
   parm1.dd = parm.dd;
   parm1.course = parm.course;
   parm1.day = parm.day;
   parm1.fb = parm.fb;
   parm1.ind = parm.ind;      // index value
   parm1.sfb = parm.sfb;
   parm1.club = parm.club;    // name of club
   parm1.time = parm.time1;
   parm1.player1 = parm.player1;
   parm1.player2 = parm.player2;
   parm1.player3 = parm.player3;
   parm1.player4 = parm.player4;
   parm1.user1 = parm.user1;
   parm1.user2 = parm.user2;
   parm1.user3 = parm.user3;
   parm1.user4 = parm.user4;
   parm1.player5 = parm.player5;
   parm1.user5 = parm.user5;
   parm1.g5 = parm.g[4];
   parm1.guests = parm.guests;          // # of guests in this multi request

   //
   //  Do both tee times together - Wilmington only allows 2 tee times for members
   //
   check = verifyCustom.checkWilmingtonGuests(parm1, con);             // check for max guests

   return(check);

 }         // end of checkWilGuests


 // *******************************************************************************
 //  Check Days In Advance limits for membership types
 // *******************************************************************************
 //
 private boolean checkDaysInAdv(parmSlotm parm, PrintWriter out, Connection con) {


   boolean check = false;
   boolean go = false;
   parm.error = false;               // init

   String player = "";

   //
   //  Allocate a new parm block for each tee time and call common meehtod to process each.
   //
   parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

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
   parm1.oldPlayer1 = "";       // always empty from here
   parm1.oldPlayer2 = "";
   parm1.oldPlayer3 = "";
   parm1.oldPlayer4 = "";
   parm1.oldPlayer5 = "";
   parm1.fb = parm.fb;
   parm1.ind = parm.ind;      // index value
   parm1.sfb = parm.sfb;
   parm1.club = parm.club;    // name of club


   //
   //  Do all members, one group at a time
   //
   try {

      go = false;                             // init to 'No Go'

      if (parm.p5.equals( "Yes" )) {

         if (!parm.mship1.equals( "" ) || !parm.mship2.equals( "" ) || !parm.mship3.equals( "" ) ||
             !parm.mship4.equals( "" ) || !parm.mship5.equals( "" )) {

            go = true;                // go process this group

            //
            //  set parms for first group
            //
            parm1.time = parm.time1;
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
            parm1.mstype1 = parm.mstype1;
            parm1.mstype2 = parm.mstype2;
            parm1.mstype3 = parm.mstype3;
            parm1.mstype4 = parm.mstype4;
            parm1.mstype5 = parm.mstype5;
            parm1.mNum1 = parm.mNum1;
            parm1.mNum2 = parm.mNum2;
            parm1.mNum3 = parm.mNum3;
            parm1.mNum4 = parm.mNum4;
            parm1.mNum5 = parm.mNum5;
         }

      } else {                       // 4-somes only

         if (!parm.mship1.equals( "" ) || !parm.mship2.equals( "" ) || !parm.mship3.equals( "" ) ||
             !parm.mship4.equals( "" )) {

            go = true;                // go process this group

            //
            //  set parms for first group
            //
            parm1.time = parm.time1;
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
            parm1.mstype1 = parm.mstype1;
            parm1.mstype2 = parm.mstype2;
            parm1.mstype3 = parm.mstype3;
            parm1.mstype4 = parm.mstype4;
            parm1.mNum1 = parm.mNum1;
            parm1.mNum2 = parm.mNum2;
            parm1.mNum3 = parm.mNum3;
            parm1.mNum4 = parm.mNum4;
            parm1.player5 = "";
            parm1.user5 = "";
            parm1.mship5 = "";
            parm1.mtype5 = "";
            parm1.mstype5 = "";
            parm1.mNum5 = "";
         }
      }

      if (go == true) {          // if mships found

         check = verifySlot.checkDaysAdv(parm1, con);         // check mships!!!!!!!!!!!!

         if (check == true) {          // if we hit on a restriction

            player = parm1.player;

         } else {

            //
            //  If Oakland Hills check for advance reservations
            //
            if (parm.club.equals( "oaklandhills" ) && parm.ind > 5) {

               //
               //   check for advance times if more than 5 days in adv
               //
               check = verifySlot.checkOaklandAdvTime1(parm1, con);

               if (check == false) {          // if ok

                  check = verifySlot.checkOaklandAdvTime2(parm1, con);

               } else {

                  player = parm1.player;         // save player name for error msg
               }
            }

            //
            //  If CC of the Rockies or Catamount Ranch check for advance reservations
            //
            if ((parm.club.equals( "ccrockies" ) || parm.club.equals( "catamount" ) || parm.club.equals( "sonnenalp" )) && parm.ind > 0) {

               //
               //   check for 5 or more advance times
               //
               check = verifySlot.checkRockies(parm1, con);

               if (check == true) {          // if too many

                  if (parm.club.equals( "catamount" )) {

                     player = "Founder Error/" +parm1.player;

                  } else {

                     if (parm.club.equals( "sonnenalp" )) {

                        player = "Sonnenalp Advance Error/" +parm1.player;

                     } else {

                        player = parm1.player;         // save player name for error msg
                     }
                  }
               }
            }
         }
      }


      if (check == false) {           // if we can keep going

         //
         //  Do 2nd group
         //
         go = false;                             // init to 'No Go'

         if (parm.p5.equals( "Yes" )) {

            if (!parm.mship6.equals( "" ) || !parm.mship7.equals( "" ) || !parm.mship8.equals( "" ) ||
                !parm.mship9.equals( "" ) || !parm.mship10.equals( "" )) {

               go = true;                // go process this group

               //
               //  set parms for this group
               //
               parm1.time = parm.time2;
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
               parm1.mstype1 = parm.mstype6;
               parm1.mstype2 = parm.mstype7;
               parm1.mstype3 = parm.mstype8;
               parm1.mstype4 = parm.mstype9;
               parm1.mstype5 = parm.mstype10;
               parm1.mNum1 = parm.mNum6;
               parm1.mNum2 = parm.mNum7;
               parm1.mNum3 = parm.mNum8;
               parm1.mNum4 = parm.mNum9;
               parm1.mNum5 = parm.mNum10;
            }

         } else {                       // 4-somes only

            if (!parm.mship5.equals( "" ) || !parm.mship6.equals( "" ) || !parm.mship7.equals( "" ) ||
                !parm.mship8.equals( "" )) {

               go = true;                // go process this group

               //
               //  set parms for this group
               //
               parm1.time = parm.time2;
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
               parm1.mstype1 = parm.mstype5;
               parm1.mstype2 = parm.mstype6;
               parm1.mstype3 = parm.mstype7;
               parm1.mstype4 = parm.mstype8;
               parm1.mNum1 = parm.mNum5;
               parm1.mNum2 = parm.mNum6;
               parm1.mNum3 = parm.mNum7;
               parm1.mNum4 = parm.mNum8;
               parm1.player5 = "";
               parm1.user5 = "";
               parm1.mship5 = "";
               parm1.mtype5 = "";
               parm1.mstype5 = "";
               parm1.mNum5 = "";
            }
         }

         if (go == true) {          // if mships found

            check = verifySlot.checkDaysAdv(parm1, con);         // check mships!!!!!!!!!!!!

            if (check == true) {          // if we hit on a restriction

               player = parm1.player;

            } else {

               //
               //  If Oakland Hills check for advance reservations
               //
               if (parm.club.equals( "oaklandhills" ) && parm.ind > 5) {

                  //
                  //   check for advance times if more than 5 days in adv
                  //
                  check = verifySlot.checkOaklandAdvTime1(parm1, con);

                  if (check == false) {          // if ok

                     check = verifySlot.checkOaklandAdvTime2(parm1, con);

                  } else {

                     player = parm1.player;         // save player name for error msg
                  }
               }

               //
               //  If CC of the Rockies or Catamount Ranch check for advance reservations
               //
               if ((parm.club.equals( "ccrockies" ) || parm.club.equals( "catamount" ) || parm.club.equals( "sonnenalp" )) && parm.ind > 0) {

                  //
                  //   check for 'max' or more advance times
                  //
                  check = verifySlot.checkRockies(parm1, con);

                  if (check == true) {          // if too many

                     if (parm.club.equals( "catamount" )) {

                        player = "Founder Error/" +parm1.player;

                     } else {

                        if (parm.club.equals( "sonnenalp" )) {

                           player = "Sonnenalp Advance Error/" +parm1.player;

                        } else {

                           player = parm1.player;         // save player name for error msg
                        }
                     }
                  }
               }
            }
         }

         if (check == false) {           // if we can keep going

            //
            //  Do 3rd group
            //
            go = false;                             // init to 'No Go'

            if (parm.p5.equals( "Yes" )) {

               if (!parm.mship11.equals( "" ) || !parm.mship12.equals( "" ) || !parm.mship13.equals( "" ) ||
                   !parm.mship14.equals( "" ) || !parm.mship15.equals( "" )) {

                  go = true;                // go process this group

                  //
                  //  set parms for this group
                  //
                  parm1.time = parm.time3;
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
                  parm1.mstype1 = parm.mstype11;
                  parm1.mstype2 = parm.mstype12;
                  parm1.mstype3 = parm.mstype13;
                  parm1.mstype4 = parm.mstype14;
                  parm1.mstype5 = parm.mstype15;
                  parm1.mNum1 = parm.mNum11;
                  parm1.mNum2 = parm.mNum12;
                  parm1.mNum3 = parm.mNum13;
                  parm1.mNum4 = parm.mNum14;
                  parm1.mNum5 = parm.mNum15;
               }

            } else {                       // 4-somes only

               if (!parm.mship9.equals( "" ) || !parm.mship10.equals( "" ) || !parm.mship11.equals( "" ) ||
                   !parm.mship12.equals( "" )) {

                  go = true;                // go process this group

                  //
                  //  set parms for this group
                  //
                  parm1.time = parm.time3;
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
                  parm1.mstype1 = parm.mstype9;
                  parm1.mstype2 = parm.mstype10;
                  parm1.mstype3 = parm.mstype11;
                  parm1.mstype4 = parm.mstype12;
                  parm1.mNum1 = parm.mNum9;
                  parm1.mNum2 = parm.mNum10;
                  parm1.mNum3 = parm.mNum11;
                  parm1.mNum4 = parm.mNum12;
                  parm1.player5 = "";
                  parm1.user5 = "";
                  parm1.mship5 = "";
                  parm1.mtype5 = "";
                  parm1.mstype5 = "";
                  parm1.mNum5 = "";
               }
            }

            if (go == true) {          // if mships found

               check = verifySlot.checkDaysAdv(parm1, con);         // check mships!!!!!!!!!!!!

               if (check == true) {          // if we hit on a restriction

                  player = parm1.player;

               } else {

                  //
                  //  If Oakland Hills check for advance reservations
                  //
                  if (parm.club.equals( "oaklandhills" ) && parm.ind > 5) {

                     //
                     //   check for advance times if more than 5 days in adv
                     //
                     check = verifySlot.checkOaklandAdvTime1(parm1, con);

                     if (check == false) {          // if ok

                        check = verifySlot.checkOaklandAdvTime2(parm1, con);

                     } else {

                        player = parm1.player;         // save player name for error msg
                     }
                  }

                  //
                  //  If CC of the Rockies or Catamount Ranch check for advance reservations
                  //
                  if ((parm.club.equals( "ccrockies" ) || parm.club.equals( "catamount" ) || parm.club.equals( "sonnenalp" )) && parm.ind > 0) {

                     //
                     //   check for 5 or more advance times
                     //
                     check = verifySlot.checkRockies(parm1, con);

                     if (check == true) {          // if too many

                        if (parm.club.equals( "catamount" )) {

                           player = "Founder Error/" +parm1.player;

                        } else {

                           if (parm.club.equals( "sonnenalp" )) {

                              player = "Sonnenalp Advance Error/" +parm1.player;

                           } else {

                              player = parm1.player;         // save player name for error msg
                           }
                        }
                     }
                  }
               }
            }

            if (check == false) {           // if we can keep going

               //
               //  Do 4th group
               //
               go = false;                             // init to 'No Go'

               if (parm.p5.equals( "Yes" )) {

                  if (!parm.mship16.equals( "" ) || !parm.mship17.equals( "" ) || !parm.mship18.equals( "" ) ||
                      !parm.mship19.equals( "" ) || !parm.mship20.equals( "" )) {

                     go = true;                // go process this group

                     //
                     //  set parms for this group
                     //
                     parm1.time = parm.time4;
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
                     parm1.mstype1 = parm.mstype16;
                     parm1.mstype2 = parm.mstype17;
                     parm1.mstype3 = parm.mstype18;
                     parm1.mstype4 = parm.mstype19;
                     parm1.mstype5 = parm.mstype20;
                     parm1.mNum1 = parm.mNum16;
                     parm1.mNum2 = parm.mNum17;
                     parm1.mNum3 = parm.mNum18;
                     parm1.mNum4 = parm.mNum19;
                     parm1.mNum5 = parm.mNum20;
                  }

               } else {                       // 4-somes only

                  if (!parm.mship13.equals( "" ) || !parm.mship14.equals( "" ) || !parm.mship15.equals( "" ) ||
                      !parm.mship16.equals( "" )) {

                     go = true;                // go process this group

                     //
                     //  set parms for this group
                     //
                     parm1.time = parm.time4;
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
                     parm1.mstype1 = parm.mstype13;
                     parm1.mstype2 = parm.mstype14;
                     parm1.mstype3 = parm.mstype15;
                     parm1.mstype4 = parm.mstype16;
                     parm1.mNum1 = parm.mNum13;
                     parm1.mNum2 = parm.mNum14;
                     parm1.mNum3 = parm.mNum15;
                     parm1.mNum4 = parm.mNum16;
                     parm1.player5 = "";
                     parm1.user5 = "";
                     parm1.mship5 = "";
                     parm1.mtype5 = "";
                     parm1.mstype5 = "";
                     parm1.mNum5 = "";
                  }
               }

               if (go == true) {          // if mships found

                  check = verifySlot.checkDaysAdv(parm1, con);         // check mships!!!!!!!!!!!!

                  if (check == true) {          // if we hit on a restriction

                     player = parm1.player;

                  } else {

                     //
                     //  If Oakland Hills check for advance reservations
                     //
                     if (parm.club.equals( "oaklandhills" ) && parm.ind > 5) {

                        //
                        //   check for advance times if more than 5 days in adv
                        //
                        check = verifySlot.checkOaklandAdvTime1(parm1, con);

                        if (check == false) {          // if ok

                           check = verifySlot.checkOaklandAdvTime2(parm1, con);

                        } else {

                           player = parm1.player;         // save player name for error msg
                        }
                     }

                     //
                     //  If CC of the Rockies or Catamount Ranch check for advance reservations
                     //
                     if ((parm.club.equals( "ccrockies" ) || parm.club.equals( "catamount" ) || parm.club.equals( "sonnenalp" )) && parm.ind > 0) {

                        //
                        //   check for 5 or more advance times
                        //
                        check = verifySlot.checkRockies(parm1, con);

                        if (check == true) {          // if too many

                           if (parm.club.equals( "catamount" )) {

                              player = "Founder Error/" +parm1.player;

                           } else {

                              if (parm.club.equals( "sonnenalp" )) {

                                 player = "Sonnenalp Advance Error/" +parm1.player;

                              } else {

                                 player = parm1.player;         // save player name for error msg
                              }
                           }
                        }
                     }
                  }
               }

               if (check == false) {           // if we can keep going

                  //
                  //  Do 5th group
                  //
                  go = false;                             // init to 'No Go'

                  if (parm.p5.equals( "Yes" )) {

                     if (!parm.mship21.equals( "" ) || !parm.mship22.equals( "" ) || !parm.mship23.equals( "" ) ||
                         !parm.mship24.equals( "" ) || !parm.mship25.equals( "" )) {

                        go = true;                // go process this group

                        //
                        //  set parms for this group
                        //
                        parm1.time = parm.time5;
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
                        parm1.mstype1 = parm.mstype21;
                        parm1.mstype2 = parm.mstype22;
                        parm1.mstype3 = parm.mstype23;
                        parm1.mstype4 = parm.mstype24;
                        parm1.mstype5 = parm.mstype25;
                        parm1.mNum1 = parm.mNum21;
                        parm1.mNum2 = parm.mNum22;
                        parm1.mNum3 = parm.mNum23;
                        parm1.mNum4 = parm.mNum24;
                        parm1.mNum5 = parm.mNum25;
                     }

                  } else {                       // 4-somes only

                     if (!parm.mship17.equals( "" ) || !parm.mship18.equals( "" ) || !parm.mship19.equals( "" ) ||
                         !parm.mship20.equals( "" )) {

                        go = true;                // go process this group

                        //
                        //  set parms for this group
                        //
                        parm1.time = parm.time5;
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
                        parm1.mstype1 = parm.mstype17;
                        parm1.mstype2 = parm.mstype18;
                        parm1.mstype3 = parm.mstype19;
                        parm1.mstype4 = parm.mstype20;
                        parm1.mNum1 = parm.mNum17;
                        parm1.mNum2 = parm.mNum18;
                        parm1.mNum3 = parm.mNum19;
                        parm1.mNum4 = parm.mNum20;
                        parm1.player5 = "";
                        parm1.user5 = "";
                        parm1.mship5 = "";
                        parm1.mtype5 = "";
                        parm1.mstype5 = "";
                        parm1.mNum5 = "";
                     }
                  }

                  if (go == true) {          // if mships found

                     check = verifySlot.checkDaysAdv(parm1, con);         // check mships!!!!!!!!!!!!

                     if (check == true) {          // if we hit on a restriction

                        player = parm1.player;

                     } else {

                        //
                        //  If Oakland Hills check for advance reservations
                        //
                        if (parm.club.equals( "oaklandhills" ) && parm.ind > 5) {

                           //
                           //   check for advance times if more than 5 days in adv
                           //
                           check = verifySlot.checkOaklandAdvTime1(parm1, con);

                           if (check == false) {          // if ok

                              check = verifySlot.checkOaklandAdvTime2(parm1, con);

                           } else {

                              player = parm1.player;         // save player name for error msg
                           }
                        }

                        //
                        //  If CC of the Rockies or Catamount Ranch check for advance reservations
                        //
                        if ((parm.club.equals( "ccrockies" ) || parm.club.equals( "catamount" ) || parm.club.equals( "sonnenalp" )) && parm.ind > 0) {

                           //
                           //   check for 5 or more advance times
                           //
                           check = verifySlot.checkRockies(parm1, con);

                           if (check == true) {          // if too many

                              if (parm.club.equals( "catamount" )) {

                                 player = "Founder Error/" +parm1.player;

                              } else {

                                 if (parm.club.equals( "sonnenalp" )) {

                                    player = "Sonnenalp Advance Error/" +parm1.player;

                                 } else {

                                    player = parm1.player;         // save player name for error msg
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }
   catch (Exception e7) {

      dbError(out, e7);
      parm.error = true;               // inform caller of error
   }

   //
   //  save parms if error
   //
   parm.player = player;

   return(check);

 }         // end of checkDaysInAdv


 // *******************************************************************************
 //  Check Member Number restrictions
 //
 //     First, find all restrictions within date & time constraints
 //     Then, find the ones for this day
 //     Then, check all players' member numbers against all others in the time period
 //
 // *******************************************************************************
 //
 private boolean checkMemNum(Connection con, PrintWriter out, parmSlotm parm, String day) {


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
   int time = parm.time1;

   long date = parm.date;


   try {

      PreparedStatement pstmt7b = con.prepareStatement (
         "SELECT name, stime, etime, recurr, courseName, fb, num_mems " +
         "FROM mnumres2 WHERE sdate <= ? AND edate >= ? AND " +
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

 private void sendMail(Connection con, parmSlotm parms, String user) {


   //
   //  parm block to hold verify's parms
   //
   parmEmail parme = new parmEmail();          // allocate an Email parm block

   //
   //  Get the parms passed in the parm block and put them in the Email Parm Block
   //
   parme.date = parms.date;
   parme.time = parms.time1;        // first tee time
   parme.time2 = parms.time2;
   parme.time3 = parms.time3;
   parme.time4 = parms.time4;
   parme.time5 = parms.time5;
   parme.fb = parms.fb;
   parme.mm = parms.mm;
   parme.dd = parms.dd;
   parme.yy = parms.yy;

   parme.type = "tee";          // use tee time message
   parme.user = user;
   parme.emailNew = 1;          // always new tee times from here
   parme.emailMod = 0;
   parme.emailCan = 0;

   parme.course = parms.course;
   parme.day = parms.day;

   parme.p91 = parms.p91;
   parme.p92 = parms.p92;
   parme.p93 = parms.p93;
   parme.p94 = parms.p94;

   parme.player1 = parms.player1;
   parme.player2 = parms.player2;
   parme.player3 = parms.player3;
   parme.player4 = parms.player4;

   parme.user1 = parms.user1;
   parme.user2 = parms.user2;
   parme.user3 = parms.user3;
   parme.user4 = parms.user4;

   parme.pcw1 = parms.pcw1;
   parme.pcw2 = parms.pcw2;
   parme.pcw3 = parms.pcw3;
   parme.pcw4 = parms.pcw4;

   parme.guest_id1 = parms.guest_id1;
   parme.guest_id2 = parms.guest_id2;
   parme.guest_id3 = parms.guest_id3;
   parme.guest_id4 = parms.guest_id4;

   //
   //  Add remainder of players based on 4-somes or 5-somes
   //
   if (parms.p5.equals("Yes")) {     // if 5-somes

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

   } else {                          // 4-somes

      parme.p95 = 0;
      parme.p96 = parms.p95;
      parme.p97 = parms.p96;
      parme.p98 = parms.p97;
      parme.p99 = parms.p98;
      parme.p910 = 0;
      parme.p911 = parms.p99;
      parme.p912 = parms.p910;
      parme.p913 = parms.p911;
      parme.p914 = parms.p912;
      parme.p915 = 0;
      parme.p916 = parms.p913;
      parme.p917 = parms.p914;
      parme.p918 = parms.p915;
      parme.p919 = parms.p916;
      parme.p920 = 0;
      parme.p921 = parms.p917;
      parme.p922 = parms.p918;
      parme.p923 = parms.p919;
      parme.p924 = parms.p920;
      parme.p925 = 0;

      parme.player5 = "";
      parme.player6 = parms.player5;
      parme.player7 = parms.player6;
      parme.player8 = parms.player7;
      parme.player9 = parms.player8;
      parme.player10 = "";
      parme.player11 = parms.player9;
      parme.player12 = parms.player10;
      parme.player13 = parms.player11;
      parme.player14 = parms.player12;
      parme.player15 = "";
      parme.player16 = parms.player13;
      parme.player17 = parms.player14;
      parme.player18 = parms.player15;
      parme.player19 = parms.player16;
      parme.player20 = "";
      parme.player21 = parms.player17;
      parme.player22 = parms.player18;
      parme.player23 = parms.player19;
      parme.player24 = parms.player20;
      parme.player25 = "";

      parme.user5 = "";
      parme.user6 = parms.user5;
      parme.user7 = parms.user6;
      parme.user8 = parms.user7;
      parme.user9 = parms.user8;
      parme.user10 = "";
      parme.user11 = parms.user9;
      parme.user12 = parms.user10;
      parme.user13 = parms.user11;
      parme.user14 = parms.user12;
      parme.user15 = "";
      parme.user16 = parms.user13;
      parme.user17 = parms.user14;
      parme.user18 = parms.user15;
      parme.user19 = parms.user16;
      parme.user20 = "";
      parme.user21 = parms.user17;
      parme.user22 = parms.user18;
      parme.user23 = parms.user19;
      parme.user24 = parms.user20;
      parme.user25 = "";

      parme.pcw5 = "";
      parme.pcw6 = parms.pcw5;
      parme.pcw7 = parms.pcw6;
      parme.pcw8 = parms.pcw7;
      parme.pcw9 = parms.pcw8;
      parme.pcw10 = "";
      parme.pcw11 = parms.pcw9;
      parme.pcw12 = parms.pcw10;
      parme.pcw13 = parms.pcw11;
      parme.pcw14 = parms.pcw12;
      parme.pcw15 = "";
      parme.pcw16 = parms.pcw13;
      parme.pcw17 = parms.pcw14;
      parme.pcw18 = parms.pcw15;
      parme.pcw19 = parms.pcw16;
      parme.pcw20 = "";
      parme.pcw21 = parms.pcw17;
      parme.pcw22 = parms.pcw18;
      parme.pcw23 = parms.pcw19;
      parme.pcw24 = parms.pcw20;
      parme.pcw25 = "";

      parme.guest_id5 = 0;
      parme.guest_id6 = parms.guest_id5;
      parme.guest_id7 = parms.guest_id6;
      parme.guest_id8 = parms.guest_id7;
      parme.guest_id9 = parms.guest_id8;
      parme.guest_id10 = 0;
      parme.guest_id11 = parms.guest_id9;
      parme.guest_id12 = parms.guest_id10;
      parme.guest_id13 = parms.guest_id11;
      parme.guest_id14 = parms.guest_id12;
      parme.guest_id15 = 0;
      parme.guest_id16 = parms.guest_id13;
      parme.guest_id17 = parms.guest_id14;
      parme.guest_id18 = parms.guest_id15;
      parme.guest_id19 = parms.guest_id16;
      parme.guest_id20 = 0;
      parme.guest_id21 = parms.guest_id17;
      parme.guest_id22 = parms.guest_id18;
      parme.guest_id23 = parms.guest_id19;
      parme.guest_id24 = parms.guest_id20;
      parme.guest_id25 = 0;
   }

   parme.oldplayer1 = "";
   parme.oldplayer2 = "";
   parme.oldplayer3 = "";
   parme.oldplayer4 = "";
   parme.oldplayer5 = "";
   parme.oldplayer6 = "";
   parme.oldplayer7 = "";
   parme.oldplayer8 = "";
   parme.oldplayer9 = "";
   parme.oldplayer10 = "";
   parme.oldplayer11 = "";
   parme.oldplayer12 = "";
   parme.oldplayer13 = "";
   parme.oldplayer14 = "";
   parme.oldplayer15 = "";
   parme.oldplayer16 = "";
   parme.oldplayer17 = "";
   parme.oldplayer18 = "";
   parme.oldplayer19 = "";
   parme.oldplayer20 = "";
   parme.oldplayer21 = "";
   parme.oldplayer22 = "";
   parme.oldplayer23 = "";
   parme.oldplayer24 = "";
   parme.oldplayer25 = "";

   parme.olduser1 = "";
   parme.olduser2 = "";
   parme.olduser3 = "";
   parme.olduser4 = "";
   parme.olduser5 = "";
   parme.olduser6 = "";
   parme.olduser7 = "";
   parme.olduser8 = "";
   parme.olduser9 = "";
   parme.olduser10 = "";
   parme.olduser11 = "";
   parme.olduser12 = "";
   parme.olduser13 = "";
   parme.olduser14 = "";
   parme.olduser15 = "";
   parme.olduser16 = "";
   parme.olduser17 = "";
   parme.olduser18 = "";
   parme.olduser19 = "";
   parme.olduser20 = "";
   parme.olduser21 = "";
   parme.olduser22 = "";
   parme.olduser23 = "";
   parme.olduser24 = "";
   parme.olduser25 = "";

   parme.oldpcw1 = "";
   parme.oldpcw2 = "";
   parme.oldpcw3 = "";
   parme.oldpcw4 = "";
   parme.oldpcw5 = "";
   parme.oldpcw6 = "";
   parme.oldpcw7 = "";
   parme.oldpcw8 = "";
   parme.oldpcw9 = "";
   parme.oldpcw10 = "";
   parme.oldpcw11 = "";
   parme.oldpcw12 = "";
   parme.oldpcw13 = "";
   parme.oldpcw14 = "";
   parme.oldpcw15 = "";
   parme.oldpcw16 = "";
   parme.oldpcw17 = "";
   parme.oldpcw18 = "";
   parme.oldpcw19 = "";
   parme.oldpcw20 = "";
   parme.oldpcw21 = "";
   parme.oldpcw22 = "";
   parme.oldpcw23 = "";
   parme.oldpcw24 = "";
   parme.oldpcw25 = "";

   parme.oldguest_id1 = 0;
   parme.oldguest_id2 = 0;
   parme.oldguest_id3 = 0;
   parme.oldguest_id4 = 0;
   parme.oldguest_id5 = 0;
   parme.oldguest_id6 = 0;
   parme.oldguest_id7 = 0;
   parme.oldguest_id8 = 0;
   parme.oldguest_id9 = 0;
   parme.oldguest_id10 = 0;
   parme.oldguest_id11 = 0;
   parme.oldguest_id12 = 0;
   parme.oldguest_id13 = 0;
   parme.oldguest_id14 = 0;
   parme.oldguest_id15 = 0;
   parme.oldguest_id16 = 0;
   parme.oldguest_id17 = 0;
   parme.oldguest_id18 = 0;
   parme.oldguest_id19 = 0;
   parme.oldguest_id20 = 0;
   parme.oldguest_id21 = 0;
   parme.oldguest_id22 = 0;
   parme.oldguest_id23 = 0;
   parme.oldguest_id24 = 0;
   parme.oldguest_id25 = 0;

   parme.notes = parms.notes;
   parme.hideNotes = Integer.parseInt(parms.hides);

   //
   //  Send the email
   //
   sendEmail.sendIt(parme, con);      // in common

 }    // end of sendMail method


 // *********************************************************
 //  Return to _slotm
 // *********************************************************

 private void goReturn2(int skip, PrintWriter out, parmSlotm parm) {

      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_slotm\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + parm.date + "\">");
      out.println("<input type=\"hidden\" name=\"stime\" value=\"" + parm.time1 + "\">");
      out.println("<input type=\"hidden\" name=\"time2\" value=\"" + parm.time2 + "\">");
      out.println("<input type=\"hidden\" name=\"time3\" value=\"" + parm.time3 + "\">");
      out.println("<input type=\"hidden\" name=\"time4\" value=\"" + parm.time4 + "\">");
      out.println("<input type=\"hidden\" name=\"time5\" value=\"" + parm.time5 + "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
      out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
      out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + parm.p5rest + "\">");
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
      out.println("<input type=\"hidden\" name=\"show1\" value=\"" + parm.show1 + "\">");
      out.println("<input type=\"hidden\" name=\"show2\" value=\"" + parm.show2 + "\">");
      out.println("<input type=\"hidden\" name=\"show3\" value=\"" + parm.show3 + "\">");
      out.println("<input type=\"hidden\" name=\"show4\" value=\"" + parm.show4 + "\">");
      out.println("<input type=\"hidden\" name=\"show5\" value=\"" + parm.show5 + "\">");
      out.println("<input type=\"hidden\" name=\"show6\" value=\"" + parm.show6 + "\">");
      out.println("<input type=\"hidden\" name=\"show7\" value=\"" + parm.show7 + "\">");
      out.println("<input type=\"hidden\" name=\"show8\" value=\"" + parm.show8 + "\">");
      out.println("<input type=\"hidden\" name=\"show9\" value=\"" + parm.show9 + "\">");
      out.println("<input type=\"hidden\" name=\"show10\" value=\"" + parm.show10 + "\">");
      out.println("<input type=\"hidden\" name=\"show11\" value=\"" + parm.show11 + "\">");
      out.println("<input type=\"hidden\" name=\"show12\" value=\"" + parm.show12 + "\">");
      out.println("<input type=\"hidden\" name=\"show13\" value=\"" + parm.show13 + "\">");
      out.println("<input type=\"hidden\" name=\"show14\" value=\"" + parm.show14 + "\">");
      out.println("<input type=\"hidden\" name=\"show15\" value=\"" + parm.show15 + "\">");
      out.println("<input type=\"hidden\" name=\"show16\" value=\"" + parm.show16 + "\">");
      out.println("<input type=\"hidden\" name=\"show17\" value=\"" + parm.show17 + "\">");
      out.println("<input type=\"hidden\" name=\"show18\" value=\"" + parm.show18 + "\">");
      out.println("<input type=\"hidden\" name=\"show19\" value=\"" + parm.show19 + "\">");
      out.println("<input type=\"hidden\" name=\"show20\" value=\"" + parm.show20 + "\">");
      out.println("<input type=\"hidden\" name=\"show21\" value=\"" + parm.show21 + "\">");
      out.println("<input type=\"hidden\" name=\"show22\" value=\"" + parm.show22 + "\">");
      out.println("<input type=\"hidden\" name=\"show23\" value=\"" + parm.show23 + "\">");
      out.println("<input type=\"hidden\" name=\"show24\" value=\"" + parm.show24 + "\">");
      out.println("<input type=\"hidden\" name=\"show25\" value=\"" + parm.show25 + "\">");
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
      out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");
      out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + parm.suppressEmails + "\">");
      out.println("<input type=\"hidden\" name=\"skipDining\" value=\"" + parm.skipDining + "\">");
      out.println("<input type=\"hidden\" name=\"showlott\" value=\"" + parm.showlott + "\">");

      if (parm.club.equals("oaklandhills")) {        // Include custom_disp values if present for oaklandhills

          out.println("<input type=\"hidden\" name=\"custom1\" value=\"" + parm.custom_disp1 + "\">");
          out.println("<input type=\"hidden\" name=\"custom2\" value=\"" + parm.custom_disp2 + "\">");
          out.println("<input type=\"hidden\" name=\"custom3\" value=\"" + parm.custom_disp3 + "\">");
          out.println("<input type=\"hidden\" name=\"custom4\" value=\"" + parm.custom_disp4 + "\">");
          out.println("<input type=\"hidden\" name=\"custom5\" value=\"" + parm.custom_disp5 + "\">");
          out.println("<input type=\"hidden\" name=\"custom6\" value=\"" + parm.custom_disp6 + "\">");
          out.println("<input type=\"hidden\" name=\"custom7\" value=\"" + parm.custom_disp7 + "\">");
          out.println("<input type=\"hidden\" name=\"custom8\" value=\"" + parm.custom_disp8 + "\">");
          out.println("<input type=\"hidden\" name=\"custom9\" value=\"" + parm.custom_disp9 + "\">");
          out.println("<input type=\"hidden\" name=\"custom10\" value=\"" + parm.custom_disp10 + "\">");
          out.println("<input type=\"hidden\" name=\"custom11\" value=\"" + parm.custom_disp11 + "\">");
          out.println("<input type=\"hidden\" name=\"custom12\" value=\"" + parm.custom_disp12 + "\">");
          out.println("<input type=\"hidden\" name=\"custom13\" value=\"" + parm.custom_disp13 + "\">");
          out.println("<input type=\"hidden\" name=\"custom14\" value=\"" + parm.custom_disp14 + "\">");
          out.println("<input type=\"hidden\" name=\"custom15\" value=\"" + parm.custom_disp15 + "\">");
          out.println("<input type=\"hidden\" name=\"custom16\" value=\"" + parm.custom_disp16 + "\">");
          out.println("<input type=\"hidden\" name=\"custom17\" value=\"" + parm.custom_disp17 + "\">");
          out.println("<input type=\"hidden\" name=\"custom18\" value=\"" + parm.custom_disp18 + "\">");
          out.println("<input type=\"hidden\" name=\"custom19\" value=\"" + parm.custom_disp19 + "\">");
          out.println("<input type=\"hidden\" name=\"custom20\" value=\"" + parm.custom_disp20 + "\">");
          out.println("<input type=\"hidden\" name=\"custom21\" value=\"" + parm.custom_disp21 + "\">");
          out.println("<input type=\"hidden\" name=\"custom22\" value=\"" + parm.custom_disp22 + "\">");
          out.println("<input type=\"hidden\" name=\"custom23\" value=\"" + parm.custom_disp23 + "\">");
          out.println("<input type=\"hidden\" name=\"custom24\" value=\"" + parm.custom_disp24 + "\">");
          out.println("<input type=\"hidden\" name=\"custom25\" value=\"" + parm.custom_disp25 + "\">");
      }

      if (skip == 99) {         // if return to assign guests a member

         out.println("<input type=\"hidden\" name=\"assign\" value=\"yes\">");  // assign member to guests
         out.println("<input type=\"submit\" value=\"Yes - Assign Member\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");

      } else {

         out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline;\">");
      }
      out.println("</form></font>");
      //
      //  Return to process the players as they are
      //
      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_slotm\" method=\"post\" target=\"_top\">");
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
      out.println("<input type=\"hidden\" name=\"show1\" value=\"" + parm.show1 + "\">");
      out.println("<input type=\"hidden\" name=\"show2\" value=\"" + parm.show2 + "\">");
      out.println("<input type=\"hidden\" name=\"show3\" value=\"" + parm.show3 + "\">");
      out.println("<input type=\"hidden\" name=\"show4\" value=\"" + parm.show4 + "\">");
      out.println("<input type=\"hidden\" name=\"show5\" value=\"" + parm.show5 + "\">");
      out.println("<input type=\"hidden\" name=\"show6\" value=\"" + parm.show6 + "\">");
      out.println("<input type=\"hidden\" name=\"show7\" value=\"" + parm.show7 + "\">");
      out.println("<input type=\"hidden\" name=\"show8\" value=\"" + parm.show8 + "\">");
      out.println("<input type=\"hidden\" name=\"show9\" value=\"" + parm.show9 + "\">");
      out.println("<input type=\"hidden\" name=\"show10\" value=\"" + parm.show10 + "\">");
      out.println("<input type=\"hidden\" name=\"show11\" value=\"" + parm.show11 + "\">");
      out.println("<input type=\"hidden\" name=\"show12\" value=\"" + parm.show12 + "\">");
      out.println("<input type=\"hidden\" name=\"show13\" value=\"" + parm.show13 + "\">");
      out.println("<input type=\"hidden\" name=\"show14\" value=\"" + parm.show14 + "\">");
      out.println("<input type=\"hidden\" name=\"show15\" value=\"" + parm.show15 + "\">");
      out.println("<input type=\"hidden\" name=\"show16\" value=\"" + parm.show16 + "\">");
      out.println("<input type=\"hidden\" name=\"show17\" value=\"" + parm.show17 + "\">");
      out.println("<input type=\"hidden\" name=\"show18\" value=\"" + parm.show18 + "\">");
      out.println("<input type=\"hidden\" name=\"show19\" value=\"" + parm.show19 + "\">");
      out.println("<input type=\"hidden\" name=\"show20\" value=\"" + parm.show20 + "\">");
      out.println("<input type=\"hidden\" name=\"show21\" value=\"" + parm.show21 + "\">");
      out.println("<input type=\"hidden\" name=\"show22\" value=\"" + parm.show22 + "\">");
      out.println("<input type=\"hidden\" name=\"show23\" value=\"" + parm.show23 + "\">");
      out.println("<input type=\"hidden\" name=\"show24\" value=\"" + parm.show24 + "\">");
      out.println("<input type=\"hidden\" name=\"show25\" value=\"" + parm.show25 + "\">");
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
      out.println("<input type=\"hidden\" name=\"time\" value=\"" + parm.time1 + "\">");
      out.println("<input type=\"hidden\" name=\"time2\" value=\"" + parm.time2 + "\">");
      out.println("<input type=\"hidden\" name=\"time3\" value=\"" + parm.time3 + "\">");
      out.println("<input type=\"hidden\" name=\"time4\" value=\"" + parm.time4 + "\">");
      out.println("<input type=\"hidden\" name=\"time5\" value=\"" + parm.time5 + "\">");
      out.println("<input type=\"hidden\" name=\"mm\" value=\"" + parm.mm + "\">");
      out.println("<input type=\"hidden\" name=\"yy\" value=\"" + parm.yy + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
      out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
      out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + parm.p5rest + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
      out.println("<input type=\"hidden\" name=\"notes\" value=\"" + parm.notes + "\">");
      out.println("<input type=\"hidden\" name=\"hide\" value=\"" + parm.hides + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
      out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");
      out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + parm.suppressEmails + "\">");
      out.println("<input type=\"hidden\" name=\"skipDining\" value=\"" + parm.skipDining + "\">");
      out.println("<input type=\"hidden\" name=\"showlott\" value=\"" + parm.showlott + "\">");

      if (parm.club.equals("oaklandhills")) {        // Include custom_disp values if present for oaklandhills

          out.println("<input type=\"hidden\" name=\"custom1\" value=\"" + parm.custom_disp1 + "\">");
          out.println("<input type=\"hidden\" name=\"custom2\" value=\"" + parm.custom_disp2 + "\">");
          out.println("<input type=\"hidden\" name=\"custom3\" value=\"" + parm.custom_disp3 + "\">");
          out.println("<input type=\"hidden\" name=\"custom4\" value=\"" + parm.custom_disp4 + "\">");
          out.println("<input type=\"hidden\" name=\"custom5\" value=\"" + parm.custom_disp5 + "\">");
          out.println("<input type=\"hidden\" name=\"custom6\" value=\"" + parm.custom_disp6 + "\">");
          out.println("<input type=\"hidden\" name=\"custom7\" value=\"" + parm.custom_disp7 + "\">");
          out.println("<input type=\"hidden\" name=\"custom8\" value=\"" + parm.custom_disp8 + "\">");
          out.println("<input type=\"hidden\" name=\"custom9\" value=\"" + parm.custom_disp9 + "\">");
          out.println("<input type=\"hidden\" name=\"custom10\" value=\"" + parm.custom_disp10 + "\">");
          out.println("<input type=\"hidden\" name=\"custom11\" value=\"" + parm.custom_disp11 + "\">");
          out.println("<input type=\"hidden\" name=\"custom12\" value=\"" + parm.custom_disp12 + "\">");
          out.println("<input type=\"hidden\" name=\"custom13\" value=\"" + parm.custom_disp13 + "\">");
          out.println("<input type=\"hidden\" name=\"custom14\" value=\"" + parm.custom_disp14 + "\">");
          out.println("<input type=\"hidden\" name=\"custom15\" value=\"" + parm.custom_disp15 + "\">");
          out.println("<input type=\"hidden\" name=\"custom16\" value=\"" + parm.custom_disp16 + "\">");
          out.println("<input type=\"hidden\" name=\"custom17\" value=\"" + parm.custom_disp17 + "\">");
          out.println("<input type=\"hidden\" name=\"custom18\" value=\"" + parm.custom_disp18 + "\">");
          out.println("<input type=\"hidden\" name=\"custom19\" value=\"" + parm.custom_disp19 + "\">");
          out.println("<input type=\"hidden\" name=\"custom20\" value=\"" + parm.custom_disp20 + "\">");
          out.println("<input type=\"hidden\" name=\"custom21\" value=\"" + parm.custom_disp21 + "\">");
          out.println("<input type=\"hidden\" name=\"custom22\" value=\"" + parm.custom_disp22 + "\">");
          out.println("<input type=\"hidden\" name=\"custom23\" value=\"" + parm.custom_disp23 + "\">");
          out.println("<input type=\"hidden\" name=\"custom24\" value=\"" + parm.custom_disp24 + "\">");
          out.println("<input type=\"hidden\" name=\"custom25\" value=\"" + parm.custom_disp25 + "\">");
      }

      if (skip == 99) {         // if return to assign guests a member

         out.println("<input type=\"hidden\" name=\"skip\" value=\"9\">");
         out.println("<input type=\"submit\" value=\"No - Continue\" name=\"submitForm\" style=\"text-decoration:underline; background:#8B8970\">");

      } else {

         out.println("<input type=\"hidden\" name=\"skip\" value=\"" +skip+ "\">");
         for (int i = 0; i < 25; i++) {
            out.println("<input type=\"hidden\" name=\"userg" + i + "\" value=\"" + parm.userg[i] + "\">");
         }
         out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submitForm\">");
      }
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
 }

 // *********************************************************
 //  Return to _slotm
 // *********************************************************

 private void goReturnPrompt(PrintWriter out, parmSlotm parm) {
     goReturnPrompt(0, out, parm, false, "");
 }

 private void goReturnPrompt(int skip, PrintWriter out, parmSlotm parm, boolean allowOverride, String user) {

      if (allowOverride) {
          out.println("<BR><BR>Would you like to override this and allow the reservation?");
      }

      out.println("<BR><BR>");

      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_slotm\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + parm.date + "\">");
      out.println("<input type=\"hidden\" name=\"stime\" value=\"" + parm.time1 + "\">");
      out.println("<input type=\"hidden\" name=\"time2\" value=\"" + parm.time2 + "\">");
      out.println("<input type=\"hidden\" name=\"time3\" value=\"" + parm.time3 + "\">");
      out.println("<input type=\"hidden\" name=\"time4\" value=\"" + parm.time4 + "\">");
      out.println("<input type=\"hidden\" name=\"time5\" value=\"" + parm.time5 + "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
      out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
      out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + parm.p5rest + "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
      out.println("<input type=\"hidden\" name=\"conf\" value=\"" + parm.conf + "\">");
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
      out.println("<input type=\"hidden\" name=\"show1\" value=\"" + parm.show1 + "\">");
      out.println("<input type=\"hidden\" name=\"show2\" value=\"" + parm.show2 + "\">");
      out.println("<input type=\"hidden\" name=\"show3\" value=\"" + parm.show3 + "\">");
      out.println("<input type=\"hidden\" name=\"show4\" value=\"" + parm.show4 + "\">");
      out.println("<input type=\"hidden\" name=\"show5\" value=\"" + parm.show5 + "\">");
      out.println("<input type=\"hidden\" name=\"show6\" value=\"" + parm.show6 + "\">");
      out.println("<input type=\"hidden\" name=\"show7\" value=\"" + parm.show7 + "\">");
      out.println("<input type=\"hidden\" name=\"show8\" value=\"" + parm.show8 + "\">");
      out.println("<input type=\"hidden\" name=\"show9\" value=\"" + parm.show9 + "\">");
      out.println("<input type=\"hidden\" name=\"show10\" value=\"" + parm.show10 + "\">");
      out.println("<input type=\"hidden\" name=\"show11\" value=\"" + parm.show11 + "\">");
      out.println("<input type=\"hidden\" name=\"show12\" value=\"" + parm.show12 + "\">");
      out.println("<input type=\"hidden\" name=\"show13\" value=\"" + parm.show13 + "\">");
      out.println("<input type=\"hidden\" name=\"show14\" value=\"" + parm.show14 + "\">");
      out.println("<input type=\"hidden\" name=\"show15\" value=\"" + parm.show15 + "\">");
      out.println("<input type=\"hidden\" name=\"show16\" value=\"" + parm.show16 + "\">");
      out.println("<input type=\"hidden\" name=\"show17\" value=\"" + parm.show17 + "\">");
      out.println("<input type=\"hidden\" name=\"show18\" value=\"" + parm.show18 + "\">");
      out.println("<input type=\"hidden\" name=\"show19\" value=\"" + parm.show19 + "\">");
      out.println("<input type=\"hidden\" name=\"show20\" value=\"" + parm.show20 + "\">");
      out.println("<input type=\"hidden\" name=\"show21\" value=\"" + parm.show21 + "\">");
      out.println("<input type=\"hidden\" name=\"show22\" value=\"" + parm.show22 + "\">");
      out.println("<input type=\"hidden\" name=\"show23\" value=\"" + parm.show23 + "\">");
      out.println("<input type=\"hidden\" name=\"show24\" value=\"" + parm.show24 + "\">");
      out.println("<input type=\"hidden\" name=\"show25\" value=\"" + parm.show25 + "\">");
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
      out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");
      out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + parm.suppressEmails + "\">");
      out.println("<input type=\"hidden\" name=\"skipDining\" value=\"" + parm.skipDining + "\">");
      out.println("<input type=\"hidden\" name=\"showlott\" value=\"" + parm.showlott + "\">");

      if (parm.club.equals("oaklandhills")) {        // Include custom_disp values if present for oaklandhills

          out.println("<input type=\"hidden\" name=\"custom1\" value=\"" + parm.custom_disp1 + "\">");
          out.println("<input type=\"hidden\" name=\"custom2\" value=\"" + parm.custom_disp2 + "\">");
          out.println("<input type=\"hidden\" name=\"custom3\" value=\"" + parm.custom_disp3 + "\">");
          out.println("<input type=\"hidden\" name=\"custom4\" value=\"" + parm.custom_disp4 + "\">");
          out.println("<input type=\"hidden\" name=\"custom5\" value=\"" + parm.custom_disp5 + "\">");
          out.println("<input type=\"hidden\" name=\"custom6\" value=\"" + parm.custom_disp6 + "\">");
          out.println("<input type=\"hidden\" name=\"custom7\" value=\"" + parm.custom_disp7 + "\">");
          out.println("<input type=\"hidden\" name=\"custom8\" value=\"" + parm.custom_disp8 + "\">");
          out.println("<input type=\"hidden\" name=\"custom9\" value=\"" + parm.custom_disp9 + "\">");
          out.println("<input type=\"hidden\" name=\"custom10\" value=\"" + parm.custom_disp10 + "\">");
          out.println("<input type=\"hidden\" name=\"custom11\" value=\"" + parm.custom_disp11 + "\">");
          out.println("<input type=\"hidden\" name=\"custom12\" value=\"" + parm.custom_disp12 + "\">");
          out.println("<input type=\"hidden\" name=\"custom13\" value=\"" + parm.custom_disp13 + "\">");
          out.println("<input type=\"hidden\" name=\"custom14\" value=\"" + parm.custom_disp14 + "\">");
          out.println("<input type=\"hidden\" name=\"custom15\" value=\"" + parm.custom_disp15 + "\">");
          out.println("<input type=\"hidden\" name=\"custom16\" value=\"" + parm.custom_disp16 + "\">");
          out.println("<input type=\"hidden\" name=\"custom17\" value=\"" + parm.custom_disp17 + "\">");
          out.println("<input type=\"hidden\" name=\"custom18\" value=\"" + parm.custom_disp18 + "\">");
          out.println("<input type=\"hidden\" name=\"custom19\" value=\"" + parm.custom_disp19 + "\">");
          out.println("<input type=\"hidden\" name=\"custom20\" value=\"" + parm.custom_disp20 + "\">");
          out.println("<input type=\"hidden\" name=\"custom21\" value=\"" + parm.custom_disp21 + "\">");
          out.println("<input type=\"hidden\" name=\"custom22\" value=\"" + parm.custom_disp22 + "\">");
          out.println("<input type=\"hidden\" name=\"custom23\" value=\"" + parm.custom_disp23 + "\">");
          out.println("<input type=\"hidden\" name=\"custom24\" value=\"" + parm.custom_disp24 + "\">");
          out.println("<input type=\"hidden\" name=\"custom25\" value=\"" + parm.custom_disp25 + "\">");
      }

      if (!allowOverride || (parm.club.equals("mayfieldsr") && (verifyCustom.checkMayfieldSR(parm.date, parm.time1, parm.day) ||
              verifyCustom.checkMayfieldSR(parm.date, parm.time2, parm.day) ||
              verifyCustom.checkMayfieldSR(parm.date, parm.time3, parm.day)) && skip == 0)) {
          out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
      } else {
          out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline;\">");
      }
      out.println("</form></font>");

      if (allowOverride && ((!parm.club.equals( "lakewoodranch" ) || skip != 7)  &&
              (!parm.club.equals("mayfieldsr") || (!verifyCustom.checkMayfieldSR(parm.date, parm.time1, parm.day) &&
              !verifyCustom.checkMayfieldSR(parm.date, parm.time2, parm.day) &&
              !verifyCustom.checkMayfieldSR(parm.date, parm.time3, parm.day)) || skip != 0))) {    // skip if lakewood ranch and player already playing within x hrs

         //
         //  Return to process the players as they are
         //
         out.println("<font size=\"2\">");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_slotm\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"skip\" value=\"" +skip+ "\">");
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
         out.println("<input type=\"hidden\" name=\"show1\" value=\"" + parm.show1 + "\">");
         out.println("<input type=\"hidden\" name=\"show2\" value=\"" + parm.show2 + "\">");
         out.println("<input type=\"hidden\" name=\"show3\" value=\"" + parm.show3 + "\">");
         out.println("<input type=\"hidden\" name=\"show4\" value=\"" + parm.show4 + "\">");
         out.println("<input type=\"hidden\" name=\"show5\" value=\"" + parm.show5 + "\">");
         out.println("<input type=\"hidden\" name=\"show6\" value=\"" + parm.show6 + "\">");
         out.println("<input type=\"hidden\" name=\"show7\" value=\"" + parm.show7 + "\">");
         out.println("<input type=\"hidden\" name=\"show8\" value=\"" + parm.show8 + "\">");
         out.println("<input type=\"hidden\" name=\"show9\" value=\"" + parm.show9 + "\">");
         out.println("<input type=\"hidden\" name=\"show10\" value=\"" + parm.show10 + "\">");
         out.println("<input type=\"hidden\" name=\"show11\" value=\"" + parm.show11 + "\">");
         out.println("<input type=\"hidden\" name=\"show12\" value=\"" + parm.show12 + "\">");
         out.println("<input type=\"hidden\" name=\"show13\" value=\"" + parm.show13 + "\">");
         out.println("<input type=\"hidden\" name=\"show14\" value=\"" + parm.show14 + "\">");
         out.println("<input type=\"hidden\" name=\"show15\" value=\"" + parm.show15 + "\">");
         out.println("<input type=\"hidden\" name=\"show16\" value=\"" + parm.show16 + "\">");
         out.println("<input type=\"hidden\" name=\"show17\" value=\"" + parm.show17 + "\">");
         out.println("<input type=\"hidden\" name=\"show18\" value=\"" + parm.show18 + "\">");
         out.println("<input type=\"hidden\" name=\"show19\" value=\"" + parm.show19 + "\">");
         out.println("<input type=\"hidden\" name=\"show20\" value=\"" + parm.show20 + "\">");
         out.println("<input type=\"hidden\" name=\"show21\" value=\"" + parm.show21 + "\">");
         out.println("<input type=\"hidden\" name=\"show22\" value=\"" + parm.show22 + "\">");
         out.println("<input type=\"hidden\" name=\"show23\" value=\"" + parm.show23 + "\">");
         out.println("<input type=\"hidden\" name=\"show24\" value=\"" + parm.show24 + "\">");
         out.println("<input type=\"hidden\" name=\"show25\" value=\"" + parm.show25 + "\">");
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
         out.println("<input type=\"hidden\" name=\"time\" value=\"" + parm.time1 + "\">");
         out.println("<input type=\"hidden\" name=\"time2\" value=\"" + parm.time2 + "\">");
         out.println("<input type=\"hidden\" name=\"time3\" value=\"" + parm.time3 + "\">");
         out.println("<input type=\"hidden\" name=\"time4\" value=\"" + parm.time4 + "\">");
         out.println("<input type=\"hidden\" name=\"time5\" value=\"" + parm.time5 + "\">");
         out.println("<input type=\"hidden\" name=\"mm\" value=\"" + parm.mm + "\">");
         out.println("<input type=\"hidden\" name=\"yy\" value=\"" + parm.yy + "\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
         out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
         out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
         out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
         out.println("<input type=\"hidden\" name=\"notes\" value=\"" + parm.notes + "\">");
         out.println("<input type=\"hidden\" name=\"hide\" value=\"" + parm.hides + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
         out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");
         out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + parm.suppressEmails + "\">");
         out.println("<input type=\"hidden\" name=\"skipDining\" value=\"" + parm.skipDining + "\">");
         out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
         out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + parm.p5rest + "\">");
         out.println("<input type=\"hidden\" name=\"conf\" value=\"" + parm.conf + "\">");
         out.println("<input type=\"hidden\" name=\"showlott\" value=\"" + parm.showlott + "\">");

         if (parm.club.equals("oaklandhills")) {        // Include custom_disp values if present for oaklandhills

             out.println("<input type=\"hidden\" name=\"custom1\" value=\"" + parm.custom_disp1 + "\">");
             out.println("<input type=\"hidden\" name=\"custom2\" value=\"" + parm.custom_disp2 + "\">");
             out.println("<input type=\"hidden\" name=\"custom3\" value=\"" + parm.custom_disp3 + "\">");
             out.println("<input type=\"hidden\" name=\"custom4\" value=\"" + parm.custom_disp4 + "\">");
             out.println("<input type=\"hidden\" name=\"custom5\" value=\"" + parm.custom_disp5 + "\">");
             out.println("<input type=\"hidden\" name=\"custom6\" value=\"" + parm.custom_disp6 + "\">");
             out.println("<input type=\"hidden\" name=\"custom7\" value=\"" + parm.custom_disp7 + "\">");
             out.println("<input type=\"hidden\" name=\"custom8\" value=\"" + parm.custom_disp8 + "\">");
             out.println("<input type=\"hidden\" name=\"custom9\" value=\"" + parm.custom_disp9 + "\">");
             out.println("<input type=\"hidden\" name=\"custom10\" value=\"" + parm.custom_disp10 + "\">");
             out.println("<input type=\"hidden\" name=\"custom11\" value=\"" + parm.custom_disp11 + "\">");
             out.println("<input type=\"hidden\" name=\"custom12\" value=\"" + parm.custom_disp12 + "\">");
             out.println("<input type=\"hidden\" name=\"custom13\" value=\"" + parm.custom_disp13 + "\">");
             out.println("<input type=\"hidden\" name=\"custom14\" value=\"" + parm.custom_disp14 + "\">");
             out.println("<input type=\"hidden\" name=\"custom15\" value=\"" + parm.custom_disp15 + "\">");
             out.println("<input type=\"hidden\" name=\"custom16\" value=\"" + parm.custom_disp16 + "\">");
             out.println("<input type=\"hidden\" name=\"custom17\" value=\"" + parm.custom_disp17 + "\">");
             out.println("<input type=\"hidden\" name=\"custom18\" value=\"" + parm.custom_disp18 + "\">");
             out.println("<input type=\"hidden\" name=\"custom19\" value=\"" + parm.custom_disp19 + "\">");
             out.println("<input type=\"hidden\" name=\"custom20\" value=\"" + parm.custom_disp20 + "\">");
             out.println("<input type=\"hidden\" name=\"custom21\" value=\"" + parm.custom_disp21 + "\">");
             out.println("<input type=\"hidden\" name=\"custom22\" value=\"" + parm.custom_disp22 + "\">");
             out.println("<input type=\"hidden\" name=\"custom23\" value=\"" + parm.custom_disp23 + "\">");
             out.println("<input type=\"hidden\" name=\"custom24\" value=\"" + parm.custom_disp24 + "\">");
             out.println("<input type=\"hidden\" name=\"custom25\" value=\"" + parm.custom_disp25 + "\">");
      }

         out.println("<input type=\"submit\" value=\"YES\" name=\"submitForm\">");
         out.println("</form></font>");
      }
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
 }


 // *********************************************************
 // Check if player already scheduled
 // *********************************************************

 private boolean chkPlayer(Connection con, String player, long date, int time, int fb, String course) {


   boolean hit = false;

   ResultSet rs = null;

   int time2 = 0;
   int fb2 = 0;
   long id2 = 0;

   String course2 = "";

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

            hit = true;                    // player already scheduled on this date
         }
      }
      pstmt21.close();

      //
      //  check if player already on a lottery request
      //
      PreparedStatement pstmt22 = con.prepareStatement (
         "SELECT id FROM lreqs3 " +
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

      if (rs.next()) {

         hit = true;                    // player already scheduled on this date
      }
      pstmt22.close();
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
 //  Process cancel request from Proshop_slotm (HTML)
 // *********************************************************

 private void cancel(HttpServletRequest req, PrintWriter out, Connection con) {


   int count = 0;
   int time  = 0;
   int time2  = 0;
   int time3  = 0;
   int time4  = 0;
   int time5  = 0;
   int slots  = 0;
   int fb  = 0;
   long date  = 0;

   //
   // Get all the parameters entered
   //
   String index = req.getParameter("index");          //  index value of day (needed by Proshop_sheet when returning)
   String course = req.getParameter("course");        //  name of course
   String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
   String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
   String stime2 = req.getParameter("time2");
   String stime3 = req.getParameter("time3");
   String stime4 = req.getParameter("time4");
   String stime5 = req.getParameter("time5");
   String sfb = req.getParameter("fb");               //  front/back indicator
   String slotss = req.getParameter("slots");         //  # of times requested
   String returnCourse = req.getParameter("returnCourse");        //  name of course to return to

   String showlott = "";
   if (req.getParameter("showlott") != null) {        // if time during a lottery

      showlott = req.getParameter("showlott");        // get the value
   }

   String jump = "0";
   if (req.getParameter("jump") != null) {       

      jump = req.getParameter("jump");        // get the value
   }

   //
   //  Convert the values from string to int
   //
   try {
      date = Long.parseLong(sdate);
      time = Integer.parseInt(stime);
      time2 = Integer.parseInt(stime2);
      time3 = Integer.parseInt(stime3);
      time4 = Integer.parseInt(stime4);
      time5 = Integer.parseInt(stime5);
      slots = Integer.parseInt(slotss);
      fb = Integer.parseInt(sfb);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  Clear the 'in_use' flag for this request
   //
   try {

      verifySlot.clearInUseM(date, time, time2, time3, time4, time5, fb, course, con);

   }
   catch (Exception ignore) {
   }

   if (!returnCourse.equals( "" )) {       // if multi course club, get course to return to (ALL?)
      course = returnCourse;
   }

   //
   //  Prompt user to return to Proshop_sheet or Proshop_teelist (index = 888)
   //
   out.println("<HTML>");
   out.println("<HEAD>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
   out.println("<Title>Member Tee Time Request Page</Title>");
   out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_jump?index=" + index + "&course=" + course + "&jump=" + jump + "&showlott=" + showlott + "\">");
   out.println("</HEAD>");
   out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
   out.println("<BR><BR>Thank you, no changes were made to the tee times selected.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
   out.println("<input type=\"hidden\" name=\"showlott\" value=" + showlott + ">");
   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 //  Prompt user when not all tee times requested are available.
 // *********************************************************

 private void promptLessTimes(PrintWriter out, parmSlotm parm) {


   String [] stimeA = new String [5];
   String ampm = "";

   int [] timeA = new int [5];
   int hr = 0;
   int min = 0;


   timeA[0] = parm.time1;        // put time values in array for working
   timeA[1] = parm.time2;
   timeA[2] = parm.time3;
   timeA[3] = parm.time4;
   timeA[4] = parm.time5;

   //
   //  create a time string for display
   //
   for (int i=0; i<5; i++) {

      if (timeA[i] > 0) {         // if time exists

         hr = timeA[i] / 100;
         min = timeA[i] - (hr * 100);

         ampm = "AM";

         if (hr > 11) {

            ampm = "PM";

            if (hr > 12) {

               hr = hr - 12;
            }
         }
         if (min < 10) {
            stimeA[i] = hr + ":0" + min + " " + ampm;
         } else {
            stimeA[i] = hr + ":" + min + " " + ampm;
         }

      } else {

         stimeA[i] = "";
      }
   }

   //
   //  Prompt the user to either accept the times available or return to the tee sheet
   //
   out.println("<HTML><HEAD>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
   out.println("<Title>Proshop Prompt - Multiple Tee Time Request</Title>");
   out.println("</HEAD>");

   out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

      out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
      out.println("<tr><td valign=\"top\" align=\"center\">");
         out.println("<p>&nbsp;&nbsp;</p>");
         out.println("<p>&nbsp;&nbsp;</p>");

      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"580\" align=\"center\">");
         out.println("<font size=\"3\">");
         out.println("<b>NOTICE</b><br></font>");
         out.println("<font size=\"2\">");
         out.println("<br>One or more of the tee times you requested is currently busy.<br>");
         out.println("There are " +parm.slots+ " tee times available, as follows:<br><br>");
         out.println("&nbsp;&nbsp;&nbsp;" +stimeA[0]+ "<br>");
         out.println("&nbsp;&nbsp;&nbsp;" +stimeA[1]+ "<br>");
         if (!stimeA[2].equals( "" )) {
            out.println("&nbsp;&nbsp;&nbsp;" +stimeA[2]+ "<br>");
         }
         if (!stimeA[3].equals( "" )) {
            out.println("&nbsp;&nbsp;&nbsp;" +stimeA[3]+ "<br>");
         }
         if (!stimeA[4].equals( "" )) {
            out.println("&nbsp;&nbsp;&nbsp;" +stimeA[4]+ "<br>");
         }
         out.println("<br>Would you like to accept these times?<br>");
         out.println("</font><font size=\"3\">");
         out.println("<br><b>Please select your choice below. DO NOT use you browser's BACK button!</b><br>");
         out.println("</font></td></tr>");
         out.println("</table><br>");

         out.println("<table border=\"0\" cols=\"1\" cellpadding=\"3\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_slotm\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + parm.date + "\">");
            out.println("<input type=\"hidden\" name=\"time\" value=\"" + timeA[0] + "\">");
            out.println("<input type=\"hidden\" name=\"time2\" value=\"" + timeA[1] + "\">");
            out.println("<input type=\"hidden\" name=\"time3\" value=\"" + timeA[2] + "\">");
            out.println("<input type=\"hidden\" name=\"time4\" value=\"" + timeA[3] + "\">");
            out.println("<input type=\"hidden\" name=\"time5\" value=\"" + timeA[4] + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
            out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + parm.p5rest + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
            out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");
            out.println("<input type=\"hidden\" name=\"showlott\" value=" + parm.showlott + ">");
            out.println("<input type=\"hidden\" name=\"cancel\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"NO - Return to Tee Sheet\"></form>");

         out.println("</font></td></tr>");

         out.println("<tr><td align=\"center\">");
         out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_slotm\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + parm.index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + parm.course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + parm.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + parm.date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + parm.day + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stimeA[0] + "\">");
            out.println("<input type=\"hidden\" name=\"time2\" value=\"" + timeA[1] + "\">");
            out.println("<input type=\"hidden\" name=\"time3\" value=\"" + timeA[2] + "\">");
            out.println("<input type=\"hidden\" name=\"time4\" value=\"" + timeA[3] + "\">");
            out.println("<input type=\"hidden\" name=\"time5\" value=\"" + timeA[4] + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + parm.fb + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + parm.p5 + "\">");
            out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + parm.p5rest + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + parm.jump + "\">");
            out.println("<input type=\"hidden\" name=\"slots\" value=\"" + parm.slots + "\">");
            out.println("<input type=\"hidden\" name=\"showlott\" value=" + parm.showlott + ">");
            out.println("<input type=\"hidden\" name=\"promptLessTimes\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\"></form>");
         out.println("</font></td></tr>");
         out.println("</table>");

         out.println("</td>");
         out.println("</tr>");
      out.println("</table>");
      out.println("</font></center></body></html>");
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

 private void dbError2(String index, String course, PrintWriter out, Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
      out.println("</form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
 }

}
