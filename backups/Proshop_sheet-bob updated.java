/***************************************************************************************
 *   Proshop_sheet:  This servlet will process the 'View Tee Sheet' request from
 *                    the Proshop's Select page.
 *
 *
 *   called by:  Proshop_select (doPost) - via Proshop_jump
 *               Proshop_sheet (on a refresh)
 *               Proshop_sheet (on a print)
 *               Proshop_sheet (on a 'check-in', 'no-show' or 'check-all', 'checkallin')
 *               Proshop_sheet (on a 'show notes')
 *               Proshop_slot (on a cancel) - via Proshop_jump
 *               Proshop_slot (on a return) -    "
 *               Proshop_lott (on a cancel) - via Proshop_jump
 *               Proshop_lott (on a return) -    "
 *
 *
 *   created: 1/04/2002   Bob P.
 *
 *   last updated:
 *
 *        7/19/06   Sunnehanna - special processing for proshop2 - 10 minute refresh and show bag #s next to names.
 *        7/19/06   Desert Highlands - make the time button a different color if there are notes in the tee time.
 *        7/18/06   POS Build Line methods - check if player is a member before processing mship type charges.
 *        6/26/06   Add support for TAI Club Management POS system.
 *        6/14/06   Outdoor CC - special processing for proshop2 - 10 minute refresh.
 *        5/30/06   Added support for displaying shotgun hole assignments
 *        5/04/06   Add POS Interface for ClubSystems Group (CSG) POS system.
 *        5/03/06   In promptPOS2 - if Jonas, set the Content Type before building the file so it works with large files.
 *        5/03/06   In buildJonas - skip charges if mNum/posid is empty.
 *        4/25/06   Remove test for numeric only member numbers in member lookup js.
 *        4/20/06   All clubs - change tee time button to 'Shotgun' during shotgun event.
 *        4/19/06   Use the correct time values when determining the lottery states.
 *        4/19/06   Pass 'shotgunevent' parm to Proshop_slot if tee time is during a shotgun event.
 *        4/19/06   Cherry Hills & Mission Viejo - change tee time button to 'Shotgun' during shotgun event.
 *        4/17/06   Medinah - customize the POS interface - manually set the item codes based on course.
 *        4/07/06   Cherry Hills - special processing for proshop4 - 9 minute refresh and show bag #s next to names.
 *        4/03/06   Change the filename for Abacus21 (Medinah) and customize the item codes, etc.
 *        3/20/06   Added "Check All In" as a Control Panel option - only available for today's sheet and not using ProShopKeeper POS
 *        3/15/06   Bearpath - color code the member-only times.
 *        3/14/06   Meadow Springs - change tee time button to 'Shotgun' during shotgun event.
 *        3/07/06   Oswego lake - make the time button a different color if there are notes in the tee time.
 *        3/03/06   Change the path for the pos folder for northstar for new servers.
 *        3/01/06   Cherry Hills - add new column for "# of Caddies Assigned" for the group.
 *                                The caddie master sets this when caddies have been assigned.
 *        2/03/06   Cordillera - add custom member restrictions that correspond to the custom hotel restrictions (checkCordillera).
 *        1/30/06   The Lakes - change tee time button to 'Shotgun' during shotgun event.
 *        1/12/06   Add div tags around selects for menus to work properly.
 *        1/12/06   Diablo CC - display the member number neext to each members' name (like Medinah).
 *        1/12/06   The Ranch - add new column for "Group has Teed Off" for the group.
 *        1/03/06   When checking in players from course=ALL, display all courses when done.
 *       12/19/05   Change sql statements for tee sheet summary to reflect partially full tee times as well as full times.
 *       12/15/05   Lakewood - use posid instead of mNum in buildJonas.
 *       12/10/05   Add Tee Sheet Summary feature to show how much of the tee sheet is occupied.
 *       11/03/05   Cherry Hills - refresh the page every 9 minutes for bag room & caddie master (proshop4).
 *       10/24/05   Allow for 20 courses and -ALL- for multi course clubs.
 *       10/03/05   Add 'div' tag to form for drop-down course selector so main menus will show over the drop-down box.
 *        9/14/05   Add processing for Abacus21 POS system - similar to Jonas.
 *        9/05/05   Pecan Plantation - add new column for "Group has Teed Off" for the group.
 *        9/01/05   Make sure 'num' (the index value) is not null - default to zero.
 *        8/19/05   Move displayNaotes method to SystemUtils so others can use it.
 *        7/21/05   Medinah - display Member Number with player name (members).
 *        7/15/05   Add processing for NorthStar POS system - similar to Jonas.
 *        7/07/05   Custom for Forest Highlands - default course depends on login id.
 *        6/27/05   Do not allow multiple tee time request if F/B is a cross-over.
 *        6/16/05   Add new report - Alphabetical List by Members and Tee Times.
 *        6/01/05   Medinah CC - add 'Print Tee Time' feature - ability to print an individual tee time.
 *        5/19/05   Updated calendars to use new calv30 version - the dynamic calendar is now sticky
 *        5/17/05   Medinah CC - add custom to check for 'Member Times' (walk-up times).
 *        5/03/05   Cordillera - add a column for a Forecaddie indicator (Y or N).
 *        5/03/05   Old Warson - refresh the page every 4 minutes for bag room (proshop5).
 *        5/01/05   Add counters for # of tee sheets - in SystemUtils.
 *        4/29/05   RDP Add new report to List All Notes for the day.
 *        4/20/05   RDP Add global Gzip counters to count number of tee sheets using gzip.
 *        4/10/05   Interlachen - add new column for "# of Caddies Assigned" for the group.
 *                                The caddie master sets this when caddies have been assigned.
 *        4/04/05   Cordillera - do not display the Short course when course=ALL.
 *        3/10/05   Ver 5 - add course column shading to tee sheet printing when displaying all courses
 *        3/09/05   Ver 5 - add double line option for tee sheet printing
 *        3/02/05   Ver 5 - add visual pre-checking notifiction to tee sheet and printed reports
 *        2/19/05   Ver 5 - add Control Panel item for Diary feature.
 *        2/18/05   Ironwood - change tee time button to 'Shotgun' during shotgun event.
 *        2/18/05   Do not display lottery in legend or use color on sheet if state = 5 (processed).
 *        1/19/05   Ver 5 - add Control Panel item to send emails to all on tee sheet.
 *        1/18/05   Ver 5 - display up to 4 events in the legend.
 *        1/10/05   Oakmont CC - change some of the Wed guest times for April, Sept & Oct.
 *        1/05/05   Add a new Edit Tee Sheet link to request that emails not be sent.
 *        1/05/05   Ver 5 - allow pro to make up to 5 consecutive tee times at once.
 *        1/05/05   Ver 5 - add Frost Delay link in Control Panel.
 *       12/29/04   Blackhawk CC - List alphabetical mode of trans report in the order that the
 *                  modes are specified in the course setup.
 *       12/14/04   Ver 5 - Change Control Panel to be a Menu with drop-downs.
 *       11/29/04   Milwaukee CC - add checks for special tee times that allow Multiple Guests per member.
 *       11/17/04   Oakmont CC - add checks for special tee times that allow Multiple Guests per member.
 *       10/06/04   Change the Jonas POS reporting to support the "POS 3rd Party Tee Time to Jonas Interface".
 *        9/16/04   Ver 5 - change getClub from SystemUtils to common.
 *        9/01/04   Add a report that lists all players, their guests, member #'s and mode of trans for the day.
 *        8/20/04   Add fivesAll option to indicate if 5-somes are supported on any course (course=ALL).
 *        7/14/04   Do not use GZIP if call is for Notes display.
 *        7/07/04   Get the events, restrictions & lotteries in order for the legend display.
 *        6/17/04   Change the way we use gzip - use byte buffer so the length of output is
 *                  set properly.  Fixes problem with garbled screen.
 *        6/09/04   RDP Add an 'ALL' option for multiple course facilities.
 *        5/27/04   Add support for 4 lotteries per tee sheet.
 *        5/25/04   Change call to Proshop_slot a Get instead of a Post to allow 'Be Patient' page.
 *        5/11/04   RDP Make legend items (events, restrictions, lotteries) buttons so user
 *                  can click to view pop-up window describing the item.
 *        2/25/04   Add support for Jonas POS - 'end of day' report
 *        2/12/04   Add support Pro-shopKeeper POS - Check-in and Check-out.
 *        2/06/04   Add support for configurable transportation modes.
 *        1/16/04   Change calendar to use js to allow for 365 days. (by Paul S.)
 *       12/15/02   Bob P.   Do not show member restriction in legend if show=no.
 *        7/18/03   Enhancements for Version 3 of the software.
 *                  Add Lottery processing.
 *       12/18/02   Enhancements for Version 2 of the software.
 *                  Add support for 'courseName' parm - select tee times for specific course.
 *                  Add support for Tee Time 'Blockers'.
 *
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.zip.*;
import java.sql.*;
import java.lang.Math;
import java.text.*;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.parmPOS;
import com.foretees.common.getClub;
import com.foretees.common.medinahCustom;
import com.foretees.client.action.ActionHelper;
import com.foretees.common.cordilleraCustom;
import com.foretees.common.ProcessConstants;
import com.foretees.common.verifySlot;


public class Proshop_sheet extends HttpServlet {


   String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
   
   int filen = 0;                             // Jonas filename (unique id)
   int resn = 0;                              // Jonas reservation number (unique id)
   int ttidn = 0;                             // Jonas tee time id (unique id)

   //
   //  Holidays
   //
   private static long Hdate1 = ProcessConstants.memDay;     // Memorial Day
   private static long Hdate2 = ProcessConstants.july4;      // 4th of July - Monday
   private static long Hdate2b = ProcessConstants.july4b;    // 4th of July - other
   private static long Hdate3 = ProcessConstants.laborDay;   // Labor Day


 //*****************************************************
 // Process the return from Proshop_slot (no longer used)
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing

 }

 //*****************************************************
 // Process the request from Proshop_jump
 //*****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   //
   //  Prevent caching so sessions are not mangled (do not do here so the images can be cached!!!)
   //
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   PrintWriter out;

   PreparedStatement pstmtc = null;
   Statement stmt = null;
   Statement stmtc = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   ByteArrayOutputStream buf = null;
   String encodings = "";               // browser encodings

   boolean Gzip = false;        // default = no gzip

   String report_type = "";
   String excel = "";

   //
   //  If report or print and excel format requested
   //
   if (req.getParameter("excel") != null) {

      excel = req.getParameter("excel");
   }

   if (req.getParameter("print") != null) {

      report_type = req.getParameter("print");
   }

   //
   // if notes disply, event/restriction/lottery display, POS (Jonas) Report Requested, or Notes Report
   //
   if (req.getParameter("notes") != null || req.getParameter("event") != null || req.getParameter("rest") != null ||
       req.getParameter("lottery") != null || req.getParameter("caddynum") != null || report_type.equals( "pos" ) ||
       report_type.equals( "notes" ) || req.getParameter("prtTeeTime") != null) {

      out = resp.getWriter();                                         // normal output stream

   } else {
      //
      //  use GZip (compression) if supported by browser
      //
      encodings = req.getHeader("Accept-Encoding");               // browser encodings

      if ((encodings != null) && (encodings.indexOf("gzip") != -1)) {    // if browser supports gzip

         Gzip = true;
         resp.setHeader("Content-Encoding", "gzip");                     // indicate gzip

         buf = new ByteArrayOutputStream();

         GZIPOutputStream gzipOut = new GZIPOutputStream(buf);
         OutputStreamWriter outputStreamWriter = new OutputStreamWriter(gzipOut, "UTF-8");
         out = new PrintWriter(outputStreamWriter);

      } else {
         out = resp.getWriter();                                         // normal output stream
      }
   }


   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);                      // get DB connection

   if (con == null) {

      resp.setContentType("text/html");

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      if (Gzip == true) {
         resp.setContentLength(buf.size());                 // set output length
         resp.getOutputStream().write(buf.toByteArray());
      }
      return;
   }

   //
   //  Check for POS report selected from Control Panel - do before setting resp content type
   //
   if (report_type.equals( "pos" )) {         // if POS - Jonas Report Requested

      promptPOS2(req, resp, out, session, con);      // go prompt user
      return;
   }

   resp.setContentType("text/html");      // ok to set content type now

   //
   //  First, check for Event or Restriction calls - user clicked on an event or restriction in the Legend
   //
   if (req.getParameter("event") != null) {

      String eventName = req.getParameter("event");

      displayEvent(eventName, out, con);             // display the information
      return;
   }
   if (req.getParameter("rest") != null) {

      String restName = req.getParameter("rest");

      displayRest(restName, out, con);             // display the information
      return;
   }
   if (req.getParameter("lottery") != null) {

      String lottName = req.getParameter("lottery");

      displayLottery(lottName, out, con);             // display the information
      return;
   }

   //
   //  Check for 'Print Tee Time' request - print button on individual tee time (currently just Medinah)
   //
   if (req.getParameter("prtTeeTime") != null) {

      printTeeTime(req, resp, out, session, con);      // go prompt user
      return;
   }


   //
   //   get name of club for this user
   //
   String club = (String)session.getAttribute("club");      // get club name
   String user = (String)session.getAttribute("user");      // get user
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lotteryS = Integer.parseInt(templott);


   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
                          "September", "October", "November", "December" };

   //
   //  Num of days in each month
   //
   int [] numDays_table = { 0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

   //
   //  Num of days in Feb indexed by year starting with 2000 - 2040
   //
   int [] feb_table = { 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29,  +
                            28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29 };

   int count = 0;
   int courseCount = 0;
   int p = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int type = 0;                         
   int in_use = 0;                         // event type
   int shotgun = 1;                      // event type = shotgun
   int pos1 = 0;
   int pos2 = 0;
   int pos3 = 0;
   int pos4 = 0;
   int pos5 = 0;
   int noSubmit = 0;
   int numCaddies = 0;             // Interlachen - number of caddies assigned, Cordillera - forecaddie indicator
   int k = 0;                      // Interlachen & Cordillera & Pecan Plantation - form counter
     
   short show = 0;
   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;
   short fb = 0;
   short hideNotes = 0;
   String courseNameT = "";
   String courseTemp = "";
   String event = "";
   String ecolor = "";
   String rest = "";
   String rcolor = "";
   String rest_recurr = "";
   String rest5 = "";
   String bgcolor5 = "";
   String player = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String mnum1 = "";
   String mnum2 = "";
   String mnum3 = "";
   String mnum4 = "";
   String mnum5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String p1 = "";
   String p2 = "";
   String p3 = "";
   String p4 = "";
   String p5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String ampm = "";
   String event_rest = "";
   String bgcolor = "";
   String stime = "";
   String sshow = "";
   String sfb = "";
   String submit = "";
   String num = "";
   String jumps = "";
   String hole = ""; // hole assignment

   String event1 = "";       // for legend - max 4 events, 4 rest's, 2 lotteries
   String ecolor1 = "";
   String event2 = "";
   String ecolor2 = "";
   String event3 = "";
   String ecolor3 = "";
   String event4 = "";
   String ecolor4 = "";
   String rest1 = "";
   String rcolor1 = "";
   String rest2 = "";
   String rcolor2 = "";
   String rest3 = "";
   String rcolor3 = "";
   String rest4 = "";
   String rcolor4 = "";
   String blocker = "";
   String notes = "";
   String bag = "";
   String conf = "";
   String orig_by = "";
   String orig_name = "";
   String lname = "";
   String fname = "";
   String mi = "";
   String tmode = "";
   String order = "";
     
   String cordRestColor = "burlywood";        // color for Cordillera's custom member restriction

   //
   //  Lottery information storage area
   //
   //    lottery calculations done only once so we don't have to check each time while building sheet
   //
   String lottery = "";
   String lottery_color = "";
   String lottery_recurr = "";
     
   int sdays = 0;
   int sdtime = 0;
   int edays = 0;
   int edtime = 0;
   int pdays = 0;
   int ptime = 0;
   int slots = 0;
   int curr_time = 0;
   int lskip = 0;
   int lstate = 0;           // lottery state
   int templstate = 0;       // temp lottery state
     
   String lott1 = "";        // name
   String lcolor1 = "";      // color
   int sdays1 = 0;           // days in advance to start taking requests
   int sdtime1 = 0;          // time of day to start taking requests
   int edays1 = 0;           // days in advance to stop taking requests
   int edtime1 = 0;          // time of day to stop taking requests
   int pdays1 = 0;           // days in advance to process the lottery
   int ptime1 = 0;           // time of day to process the lottery
   int slots1 = 0;           // # of consecutive groups allowed
   int lskip1 = 0;           // skip tee time displays 
   int lstate1 = 0;          // lottery state
                               //    1 = before time to take requests (too early for requests)
                               //    2 = after start time, before stop time (ok to take requests)
                               //    3 = after stop time, before process time (late, but still ok for pro)
                               //    4 = requests have been processed but not approved (no new tee times now)
                               //    5 = requests have been processed & approved (ok for all tee times now)
                               //

   String lott2 = "";        // ditto for 2nd lottery on this day
   String lcolor2 = "";
   int sdays2 = 0;
   int sdtime2 = 0;
   int edays2 = 0;
   int edtime2 = 0;
   int pdays2 = 0;
   int ptime2 = 0;
   int slots2 = 0;
   int lskip2 = 0;         
   int lstate2 = 0;            

   String lott3 = "";        // ditto for 3rd lottery on this day 
   String lcolor3 = "";
   int sdays3 = 0;
   int sdtime3 = 0;
   int edays3 = 0;
   int edtime3 = 0;
   int pdays3 = 0;
   int ptime3 = 0;
   int slots3 = 0;
   int lskip3 = 0;         
   int lstate3 = 0;

   String lott4 = "";        // ditto for 4th lottery on this day (max of 4 for now)!!!!!!
   String lcolor4 = "";
   int sdays4 = 0;
   int sdtime4 = 0;
   int edays4 = 0;
   int edtime4 = 0;
   int pdays4 = 0;
   int ptime4 = 0;
   int slots4 = 0;
   int lskip4 = 0;
   int lstate4 = 0;

   int lyear = 0;
   int lmonth = 0;
   int lday = 0;
   int advance_days = 0;       // copy of 'index' = # of days between today and the day of this sheet


   // **************** end of lottery save area ***********

   float hndcp1 = 0;
   float hndcp2 = 0;
   float hndcp3 = 0;
   float hndcp4 = 0;
   float hndcp5 = 0;

   int hndcp = 0;
   int j = 0;
   int jump = 0;

   int index = 0;
   int multi = 0;               // multiple course support
   int i = 0;
   int fives = 0;
   int fivesALL = 0;
   int g1 = 0;                  // guest indicators (1 per player)
   int g2 = 0;
   int g3 = 0;
   int g4 = 0;
   int g5 = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int st2 = 2;
   int POS = 0;
   
   boolean charges = false;
   boolean noShow = false;
   boolean checkedOut = false;
   boolean disp_hndcp = true;
   boolean disp_mnum = false;
   boolean medinahMemTime = false;        // for Medinah Custom
   boolean restrictAll = false;

   String pos6 = "";
   String updShow = "";

   String updShow1 = "UPDATE teecurr2 SET show1 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow1c = "UPDATE teecurr2 SET show1 = ?, pos1 = 1 WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow1r = "UPDATE teecurr2 SET show1 = ?, pos1 = 0 WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow2 = "UPDATE teecurr2 SET show2 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow2c = "UPDATE teecurr2 SET show2 = ?, pos2 = 1 WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow2r = "UPDATE teecurr2 SET show2 = ?, pos2 = 0 WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow3 = "UPDATE teecurr2 SET show3 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow3c = "UPDATE teecurr2 SET show3 = ?, pos3 = 1 WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow3r = "UPDATE teecurr2 SET show3 = ?, pos3 = 0 WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow4 = "UPDATE teecurr2 SET show4 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow4c = "UPDATE teecurr2 SET show4 = ?, pos4 = 1 WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow4r = "UPDATE teecurr2 SET show4 = ?, pos4 = 0 WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow5 = "UPDATE teecurr2 SET show5 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow5c = "UPDATE teecurr2 SET show5 = ?, pos5 = 1 WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow5r = "UPDATE teecurr2 SET show5 = ?, pos5 = 0 WHERE date = ? AND time = ? AND fb = ? AND courseName=?";

   String updShowAll = "UPDATE teecurr2 SET show1=1, show2=1, show3=1, show4=1, show5=1 WHERE date=? AND time=? AND fb=? AND courseName=?";
   String updShowAll2 = "UPDATE teecurr2 SET show1=1, show2=1, show3=1, show4=1, show5=1, pos1=1, pos2=1, pos3=1, pos4=1, pos5=1 " +
                        "WHERE date=? AND time=? AND fb=? AND courseName=?";

   //
   //  Array to hold the course names
   //
   int cMax = 21;                                       // max of 20 courses plus allow room for '-ALL-'
   String courseName = "";
   String [] course = new String [cMax];
   String [] course_color = new String [cMax];
   
   int tmp_i = 0; // counter for course[], shading of course field
   
   // set default course colors  // NOTE: CHANES TO THIS ARRAY NEED TO BE DUPLICATED IN Proshop_insert.java:doBlockers()
   course_color[0] = "#F5F5DC";   // beige shades
   course_color[1] = "#DDDDBE";
   course_color[2] = "#B3B392";
   course_color[3] = "#8B8970";
   course_color[4] = "#E7F0E7";   // greens shades
   course_color[5] = "#C6D3C6";
   course_color[6] = "#648A64";
   course_color[7] = "#407340";
   course_color[8] = "#FFE4C4";   // bisque
   course_color[9] = "#95B795";
   course_color[10] = "#66CDAA";  // medium aquamarine
   course_color[11] = "#20B2AA";  // light seagreen
   course_color[12] = "#3CB371";  // medium seagreen
   course_color[13] = "#F5DEB3";  // wheat
   course_color[14] = "#D2B48C";  // tan
   course_color[15] = "#999900";  //
   course_color[16] = "#FF9900";  // red-orange??
   course_color[17] = "#33FF66";  //
   course_color[18] = "#7FFFD4";  // aquamarine
   course_color[19] = "#33FFFF";  //
   course_color[20] = "#FFFFFF";  // white

   int [] fivesA = new int [cMax];                  // array to hold 5-some option for each course

   //**********************************************************************************
   //  Oakmont tee time arrays for Wednesday & Friday (special guest restrictions) see also verifySlot
   //**********************************************************************************
   //
   int wedcount = 17;    // 17 tee times
   int [] wedtimes = { 820, 830, 840, 1010, 1020, 1030, 1040, 1050, 1100, 1110, 1120, 1130, 1140, 1150, 1440, 1450, 1500 };

   int fricount = 17;    // 17 tee times
   int [] fritimes = { 820, 830, 840, 910, 920, 940, 1010, 1020, 1040, 1110, 1120, 1140, 1150, 1310, 1320, 1410, 1420 };

   boolean oakshotgun = false;    // indicator for shotgun event this day

   //**********************************************************************************

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   //  parm block to hold the POS parameters
   //
   parmPOS parmp = new parmPOS();          // allocate a parm block for POS parms


   //
   //  Get the golf course name requested
   //
   String courseName1 = "";
   String courseCheckIn = "";

   if (req.getParameter("course") != null) {

      courseName1 = req.getParameter("course");
   }

   if (req.getParameter("courseCheckIn") != null) {

      courseCheckIn = req.getParameter("courseCheckIn");      // get actual course name for check-in calls (in case course=ALL)
   }

   
   
   //
   // Let check to see if the checkallin parameter is present
   //
   if (req.getParameter("checkallin") != null) {
      
      // run sql statement to set show# values to 1
      String tmpWhereClause = "";
      String tmpSQL = "";
      long today = 0;
      
      try {
          
        //
        //  Get today's date and current time and calculate date & time values
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        int cal_hour = cal.get(Calendar.HOUR_OF_DAY);       // 24 hr clock (0 - 23)
        int cal_min = cal.get(Calendar.MINUTE);
        curr_time = (cal_hour * 100) + cal_min;    // get time in hhmm format
        curr_time = SystemUtils.adjustTime(con, curr_time);   // adjust the time

        if (curr_time < 0) {          // if negative, then we went back or ahead one day

            curr_time = 0 - curr_time;        // convert back to positive value

            if (curr_time < 100) {           // if hour is zero, then we rolled ahead 1 day

                //
                // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                //
                cal.add(Calendar.DATE,1);                     // get next day's date

                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);

            } else {                        // we rolled back 1 day

                //
                // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                //
                cal.add(Calendar.DATE,-1);                     // get yesterday's date

                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
            }
        }

        month = month + 1;                           // month starts at zero

        today = (year * 10000) + (month * 100);             // create value of yyyymmdd
        today += day;
     
         PreparedStatement pstmt3 = null;
         
         // if course is -ALL- then we'll won't be including the course name in the clause
         tmpWhereClause = (courseName1.equals("-ALL-")) ? ";" : " AND courseName = ?;";
         
         // shouldn't have to worry about supporting 5somes, since the update should
         // will only affect rows the the player# field is already set
         for (int i7=1; i7<6; i7++) {
             tmpSQL = "UPDATE teecurr2 SET show" + i7 + " = 1 WHERE player" + i7 + " <> '' AND date = ?" + tmpWhereClause;
             pstmt3 = con.prepareStatement (tmpSQL);
             pstmt3.clearParameters();        // clear the parms
             pstmt3.setLong(1, today);
             if (!courseName1.equals("-ALL-")) pstmt3.setString(2, courseName1);
             pstmt3.executeUpdate();      // execute the prepared stmt
             pstmt3.close();
         }
    
      }
      catch (Exception exp) {
        displayDatabaseErrMsg("Error checking in all players for this tee-sheet.", exp.getMessage(), out);
      }
      
   } 
   
   
   
   
   //
   //  get the jump parm if provided (location on page to jump to)
   //
   if (req.getParameter("jump") != null) {

      jumps = req.getParameter("jump");         //  jump index value for where to jump to on the page

      try {
         jump = Integer.parseInt(jumps);
      }
      catch (NumberFormatException e) {
         jump = 0;
      }
   }

   //
   //   Adjust jump so we jump to the selected line minus 3 so its not on top of page
   //
   if (jump > 3) {

      jump = jump - 3;

   } else {

      jump = 0;         // jump to top of page
   }

   try {
      //
      // Get the Multiple Course Option from the club db
      //
      getClub.getParms(con, parm);        // get the club parms

      multi = parm.multi;

      //
      //   Get course names if multi-course facility
      //
      if (multi != 0) {           // if multiple courses supported for this club

         while (i < cMax) {

            course[i] = "";       // init the course array
            fivesA[i] = 0;
            i++;
         }

         i = 0;

         //
         //  Get the names of all courses for this club
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName " +
                                 "FROM clubparm2 WHERE first_hr != 0");

         while (rs.next() && i < cMax) {

            courseName = rs.getString(1);

            course[i] = courseName;      // add course name to array
            i++;
         }
         stmt.close();

         if (i > cMax) {               // make sure we didn't go past max

            courseCount = cMax;
              
         } else {

            courseCount = i;              // save number of courses
         }
 
         if (i > 1 && i < cMax) {
            course[i] = "-ALL-";        // add '-ALL-' option
         }
           
         //
         //  Make sure we have a course (in case we came directly from the Today's Tee Sheet menu)
         //
         if (courseName1.equals( "" )) {
           
            courseName1 = course[0];    // grab the first one
              
            if (club.equals( "foresthighlands" )) {         // change for Forest Highlands 

               if (user.equalsIgnoreCase( "proshop3" ) || user.equalsIgnoreCase( "proshop4" )) {

                  courseName1 = "Meadow";      // setup default course (top of the list)

               } else {

                  courseName1 = "Canyon";
               }
            }
         }
      }
   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database at this time (get multi parms).");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</BODY></HTML>");
      out.close();
      if (Gzip == true) {
         resp.setContentLength(buf.size());                 // set output length
         resp.getOutputStream().write(buf.toByteArray());
      }
      return;
   }

   //
   //  Get POS parms if course not '-ALL-' (Jonus Report will not show when course = ALL)
   //
   courseTemp = "$%&*";                         // init

   if (!courseName1.equals( "-ALL-" )) {
     
      courseTemp = courseName1;             // use this course
        
   } else {

      if (!courseCheckIn.equals( "" )) {         // if individual course specified on check-in form

         courseTemp = courseCheckIn;             // use this course
      }
   }

   if (!courseTemp.equals( "$%&*" )) {         // if ok to proceed

      try {
         //
         //  Get the POS System Parameters for this Club & Course
         //
         getClub.getPOS(con, parmp, courseTemp);

      }
      catch (Exception e1) {

         out.println(SystemUtils.HeadTitle("DB Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Unable to access the Database at this time (get course parms).");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>" + e1.getMessage());
         out.println("<BR><BR>");
         out.println("<a href=\"javascript:history.back(1)\">Return</a>");
         out.println("</BODY></HTML>");
         out.close();
         if (Gzip == true) {
            resp.setContentLength(buf.size());                 // set output length
            resp.getOutputStream().write(buf.toByteArray());
         }
         return;
      }
   }

   if ((req.getParameter("noShow") != null) || (req.getParameter("checkAll") != null)) {

      //
      //  proshop clicked on the box next to player's name to toggle the 'show' parm (checked-in or no-show)
      //  or proshop clicked on the 'check all' box to check in all players in slot
      //
      //
      //  Determine if we need to tell the POS system of this event (Pro-ShopKeeper)
      //
      POS = 0;     // default = NO
        
      if (parmp.posType.equals( "Pro-ShopKeeper" )) {
        
         POS = 1;     // YES
      }
        
      //
      //  If POS must be updated then we must first prompt the user for confirmation
      //
      if (POS == 1 && req.getParameter("POScontinue") == null) { 
        
         try {

            charges = promptPOS(parmp, req, resp, out, con, Gzip, buf);      // go prompt user

            if (charges == true) {              // were there any charges to process?
               return;                          // Yes - exit and wait for reply
            }
         }
         catch (Exception ignore) {
         }
      }

      stime = req.getParameter("time");         //  time of the slot
      sfb = req.getParameter("fb");             //  front/back indicator
      num = req.getParameter("name");           //  index value for the day of this sheet

      if (req.getParameter("noShow") != null) {
        
         noShow = true;
         player = req.getParameter("noShow");      //  number of the player selected (1-4)
         sshow = req.getParameter("show");         //  current show value for player selected
         
         p = Integer.parseInt(player);
         show = Short.parseShort(sshow);
      }

      //
      //  Convert the common string values to int's
      //
      time = Integer.parseInt(stime);
      fb = Short.parseShort(sfb);

   } else {                                // not a 'check-in' request
      //
      //    'index' contains an index value representing the date selected
      //    (0 = today, 1 = tomorrow, etc.)
      //
      num = req.getParameter("index");         // get the index value of the day selected
   }

   //
   //  Convert the index value from string to int
   //
   if (num == null || num.equals( "" ) || num.equalsIgnoreCase( "null" )) {
     
      index = 0;
      num = "0";
        
   } else {
     
      index = Integer.parseInt(num);
   }

   //
   //  save the index value for lottery computations
   //
   advance_days = index;

   //
   //  Get today's date and then use the value passed to locate the requested date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   cal.add(Calendar.DATE,index);                  // roll ahead 'index' days

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

   month = month + 1;                            // month starts at zero

   String day_name = day_table[day_num];         // get name for day

   long date = (year * 10000) + (month * 100) + day;    // create a date field of yyyymmdd

   //
   //   if call was for Show Notes then get the notes and display a new page
   //
   if (req.getParameter("notes") != null) {

      stime = req.getParameter("time");         //  time of the slot
      sfb = req.getParameter("fb");             //  front/back indicator
        
      SystemUtils.displayNotes(stime, sfb, date, courseName1, out, con);             // display the information
      return;
   }

   //
   //   if Interlachen and call was to update number of caddies assigned to the group - go process
   //
   if ((club.equals( "interlachen" ) || club.equals( "cherryhills" )) && req.getParameter("caddynum") != null) {

      String caddies = req.getParameter("caddynum");         //  get number of caddies
      stime = req.getParameter("time");                      //  time of the slot
      sfb = req.getParameter("fb");                          //  front/back indicator

      updateCaddies(stime, sfb, date, caddies, out, con);   // update the record & continue to display the sheet
   }

   //
   //   if Pecan Plantation or The Ranch and call was to update the 'teed off' indicator for the group - go process
   //
   if ((club.equals( "pecanplantation" ) || club.equals( "theranchcc" )) && req.getParameter("teedOff") != null) {

      String teedOff = req.getParameter("teedOff");          //  get 'teed off' indicator
      stime = req.getParameter("time");                      //  time of the slot
      sfb = req.getParameter("fb");                          //  front/back indicator

      updateTeedOff(stime, sfb, date, teedOff, out, con);   // update the record & continue to display the sheet
   }

   //
   //   if Cordillera and call was to update the forecaddie indicator - go process
   //
   if (club.equals( "cordillera" ) && req.getParameter("forecaddy") != null) {

      String forecaddy = req.getParameter("forecaddy");      //  get forecaddie indicator
      stime = req.getParameter("time");                      //  time of the slot
      sfb = req.getParameter("fb");                          //  front/back indicator
      courseNameT = req.getParameter("courseT");             //  course name for tee time

      updateForeCaddie(stime, sfb, date, forecaddy, courseNameT, out, con);   // update the record & continue to display the sheet
   }

   //
   //   if call was for Check All, then check-in all players in the specified slot
   //
   if (req.getParameter("checkAll") != null) {

      if (!courseName1.equals( "-ALL-" )) {

         courseTemp = courseName1;             // use this course

      } else {

         courseTemp = courseCheckIn;             // use the individual course provided
      }

      try {

         PreparedStatement pstmt2 = null;

         //
         //  If POS request was processed for this tee time
         //
         if (req.getParameter("POScontinue") != null && req.getParameter("POSdone") != null) {

            pstmt2 = con.prepareStatement (updShowAll2);
     
         } else {
           
            pstmt2 = con.prepareStatement (updShowAll);
         }

         pstmt2.clearParameters();        // clear the parms
         pstmt2.setLong(1, date);
         pstmt2.setInt(2, time);
         pstmt2.setShort(3, fb);
         pstmt2.setString(4, courseTemp);
         count = pstmt2.executeUpdate();      // execute the prepared stmt

         pstmt2.close();

      }
      catch (Exception ignore) {
      }
   }

   //
   //   if call was for noShow, then toggle the show indicator in the specified slot for the specified player
   //
   if (noShow == true) {

      if (!courseName1.equals( "-ALL-" )) {

         courseTemp = courseName1;             // use this course

      } else {

         courseTemp = courseCheckIn;             // use the individual course provided
      }

      if (req.getParameter("pos6") != null) {      // if POS was done

         pos6 = req.getParameter("pos6");          //  the pos indicator for the player that was checked-in/out
      }
      
      if (show == 0) {

         show = 1;              // toggle the show value (0 = no-show, 1 = show)

      } else {

         if (show == 2) {          //  if pre-check in

            show = 0;              // toggle the show value to no-show

         } else {

            show = 0;              // player was checked in - now check him out
            checkedOut = true;     // indicate player was check out
         }
      }
      
      boolean tmp_pos_done = (req.getParameter("POScontinue") != null && req.getParameter("POSdone") != null);
      boolean tmp_charge = pos6.equals( "1" );
      
      switch (p) {
          case 1:
              updShow = (!tmp_pos_done) ? updShow1 : (tmp_charge) ? updShow1c : updShow1r;
              break;
          case 2:
              updShow = (!tmp_pos_done) ? updShow2 : (tmp_charge) ? updShow2c : updShow2r;
              break;
          case 3:
              updShow = (!tmp_pos_done) ? updShow3 : (tmp_charge) ? updShow3c : updShow3r;
              break;
          case 4:
              updShow = (!tmp_pos_done) ? updShow4 : (tmp_charge) ? updShow4c : updShow4r;
              break;
          case 5:
              updShow = (!tmp_pos_done) ? updShow5 : (tmp_charge) ? updShow5c : updShow5r;
      }
      
      //
      //  update teecurr to reflect the new 'show' setting for this player
      //
      try {

         PreparedStatement pstmt1 = con.prepareStatement (updShow);  // use selected stmt

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setShort(1, show);       // put the parm in pstmt1
         pstmt1.setLong(2, date);
         pstmt1.setInt(3, time);
         pstmt1.setShort(4, fb);
         pstmt1.setString(5, courseTemp);
         count = pstmt1.executeUpdate();      // execute the prepared stmt

         pstmt1.close();

         //
         //  If player was just checked out and POS system is Abacus21, Jonas, CSG or NorthStar, 
         //  check if any charges had already been sent
         //
         if (checkedOut == true && (parmp.posType.equals( "Jonas" ) || parmp.posType.equals( "NorthStar" ) ||
             parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "ClubSystems Group" ) ||
             parmp.posType.equals( "TAI Club Management" ))) {

            checkedOut = false;      // reset indicator

            //
            //  Get the pos indicators for the specified tee time
            //
            PreparedStatement pstmt2p = con.prepareStatement (
               "SELECT pos1, pos2, pos3, pos4, pos5 " +
               "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? and courseName = ?");

            pstmt2p.clearParameters();        // clear the parms
            pstmt2p.setLong(1, date);
            pstmt2p.setInt(2, time);
            pstmt2p.setShort(3, fb);
            pstmt2p.setString(4, courseTemp);   // ok as actual course is specifed for check-out calls (never ALL)

            rs = pstmt2p.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               pos1 = rs.getInt("pos1");
               pos2 = rs.getInt("pos2");
               pos3 = rs.getInt("pos3");
               pos4 = rs.getInt("pos4");
               pos5 = rs.getInt("pos5");

               //
               // Check if the POS system (Jonas) has already been sent charges for this player.
               //
               if (p == 1) {         // which player was checked out?

                  if (pos1 != 0) {    // were pos charges sent?

                     checkedOut = true;      // reset indicator
                  }

               } else {

                  if (p == 2) {         // which player was checked out?

                     if (pos2 != 0) {    // were pos charges sent?

                        checkedOut = true;      // reset indicator
                     }

                  } else {

                     if (p == 3) {         // which player was checked out?

                        if (pos3 != 0) {    // were pos charges sent?

                           checkedOut = true;      // reset indicator
                        }

                     } else {

                        if (p == 4) {         // which player was checked out?

                           if (pos4 != 0) {    // were pos charges sent?

                              checkedOut = true;      // reset indicator
                           }

                        } else {

                           if (p == 5) {         // which player was checked out?

                              if (pos5 != 0) {    // were pos charges sent?

                                 checkedOut = true;      // reset indicator
                              }
                           }
                        }
                     }
                  }
               }
            }
            pstmt2p.close();

            //
            //  If a player was checked out and charges were already sent, warn the pro.
            //
            if (checkedOut == true) {

               out.println(SystemUtils.HeadTitle("POS Warning"));
               out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
               out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<center>");
               out.println("<BR><BR><H3>*** Warning ***</H3>");
               out.println("<BR><BR>POS charges were previously sent to the POS system for the player you just checked out.");
               out.println("<BR>To change or remove those charges you must refer to the POS system.");
               out.println("<BR><BR>");
               out.println("<a href=\"/" +rev+ "/servlet/Proshop_sheet?index=" +num+ "&course=" +courseName1+ "\" title=\"Continue\" alt=\"Continue\">");
               out.println("Continue</a>");
               out.println("</center></BODY></HTML>");
               out.close();
               if (Gzip == true) {
                  resp.setContentLength(buf.size());                 // set output length
                  resp.getOutputStream().write(buf.toByteArray());
               }
               return;
            }
         }

      }
      catch (Exception e2) {

         out.println(SystemUtils.HeadTitle("DB Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Unable to access the Database at this time (check-in/out player).");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>" + e2.getMessage());
         out.println("<BR><BR>");
         out.println("<a href=\"javascript:history.back(1)\">Return</a>");
         out.println("</BODY></HTML>");
         out.close();
         if (Gzip == true) {
            resp.setContentLength(buf.size());                 // set output length
            resp.getOutputStream().write(buf.toByteArray());
         }
         return;
      }
   }             // end of show unique processing

   //
   //  Determine if this club wants to display handicaps for the members  (changed - converted if block 3-2-05 paul)
   //
   disp_hndcp = (parm.hndcpProSheet != 0);

   //
   //  Determine if this club wants to display Member Numbers for the members (Medinah only, for now)
   //
   if (club.equals( "medinahcc" ) || club.equals( "bellerive2006" ) || club.equals( "diablocc" )) {
     
      disp_mnum = true;
   }

   // all computation and db queries are wrapped inside this one try/catch block
   // the end of this block is the end of doPost
   try {

      //
      //  Get the System Parameters for this Course
      //
      if (courseName1.equals( "-ALL-" )) {

         //
         //  Check all courses for 5-some support
         //
         i = 0;
         loopc:
         while (i < cMax) {

            courseName = course[i];       // get a course name

            if (!courseName.equals( "-ALL-" )) {   // skip if -ALL-

               if (courseName.equals( "" ) || courseName == null) {      // done if null
                  break loopc;
               }
               getParms.getCourse(con, parmc, courseName);
               fivesA[i] = parmc.fives;      // get fivesome option
               if (fivesA[i] == 1) {
                  fives = 1;
               }
            }
            i++;
         }

      } else {       // single course requested

         getParms.getCourse(con, parmc, courseName1);

         fives = parmc.fives;      // get fivesome option
      }

      fivesALL = fives;            // save 5-somes option for table display below

      //
      //   Remove any guest types that are null - for tests below
      //
      i = 0;
      while (i < parm.MAX_Guests) {

         if (parm.guest[i].equals( "" )) {

            parm.guest[i] = "$@#!^&*";      // make so it won't match player name  (hey that's my cousins name!)
         }
         i++;
      }         // end of while loop

      //
      //   Statements to find any restrictions, events or lotteries for today
      //
      String string7b = "";
      String string7c = "";
      String string7d = "";

      if (courseName1.equals( "-ALL-" )) {
         string7b = "SELECT name, recurr, color FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
                    "AND showit = 'Yes' ORDER BY stime";
      } else {
         string7b = "SELECT name, recurr, color FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
                    "AND (courseName = ? OR courseName = '-ALL-') AND showit = 'Yes' ORDER BY stime";
      }

      if (courseName1.equals( "-ALL-" )) {
         string7c = "SELECT name, color FROM events2b WHERE date = ? ORDER BY stime";
      } else {
         string7c = "SELECT name, color FROM events2b WHERE date = ? " +
                    "AND (courseName = ? OR courseName = '-ALL-') ORDER BY stime";
      }

      if (courseName1.equals( "-ALL-" )) {
         string7d = "SELECT name, recurr, color, sdays, sdtime, edays, edtime, pdays, ptime, slots " +
                    "FROM lottery3 WHERE sdate <= ? AND edate >= ? ORDER BY stime";
      } else {
         string7d = "SELECT name, recurr, color, sdays, sdtime, edays, edtime, pdays, ptime, slots " +
                    "FROM lottery3 WHERE sdate <= ? AND edate >= ? " +
                    "AND (courseName = ? OR courseName = '-ALL-') ORDER BY stime";
      }

      PreparedStatement pstmt7b = con.prepareStatement (string7b);
      PreparedStatement pstmt7c = con.prepareStatement (string7c);
      PreparedStatement pstmt7d = con.prepareStatement (string7d);

      //
      //  Scan the events, restrictions and lotteries to build the legend
      //
      pstmt7b.clearParameters();          // clear the parms
      pstmt7b.setLong(1, date);
      pstmt7b.setLong(2, date);
        
      if (!courseName1.equals( "-ALL-" )) {
         pstmt7b.setString(3, courseName1);
      }

      rs = pstmt7b.executeQuery();      // find all matching restrictions, if any

      while (rs.next()) {

         rest = rs.getString(1);
         rest_recurr = rs.getString(2);
         rcolor = rs.getString(3);

         //
         //  We must check the recurrence for this day (Monday, etc.)
         //
         if ((rest_recurr.equals( "Every " + day_name )) ||          // if this day
             (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
             ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
               (!day_name.equalsIgnoreCase( "saturday" )) &&
               (!day_name.equalsIgnoreCase( "sunday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
              (day_name.equalsIgnoreCase( "saturday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
              (day_name.equalsIgnoreCase( "sunday" )))) {


            if ((!rest.equals( rest1 )) && (rest1.equals( "" ))) {

               rest1 = rest;
               rcolor1 = rcolor;
                 
               if (rcolor.equalsIgnoreCase( "default" )) {
                 
                  rcolor1 = "#F5F5DC";
               }

            } else {

               if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (rest2.equals( "" ))) {

                  rest2 = rest;
                  rcolor2 = rcolor;

                  if (rcolor.equalsIgnoreCase( "default" )) {

                     rcolor2 = "#F5F5DC";
                  }

               } else {

                  if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rest.equals( rest3 )) && (rest3.equals( "" ))) {

                     rest3 = rest;
                     rcolor3 = rcolor;

                     if (rcolor.equalsIgnoreCase( "default" )) {

                        rcolor3 = "#F5F5DC";
                     }

                  } else {

                     if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rest.equals( rest3 )) &&
                         (!rest.equals( rest4 )) && (rest4.equals( "" ))) {

                        rest4 = rest;
                        rcolor4 = rcolor;

                        if (rcolor.equalsIgnoreCase( "default" )) {

                           rcolor4 = "#F5F5DC";
                        }
                     }
                  }
               }
            }
         }
      }                  // end of while
      pstmt7b.close();

      pstmt7c.clearParameters();          // clear the parms
      pstmt7c.setLong(1, date);

      if (!courseName1.equals( "-ALL-" )) {
         pstmt7c.setString(2, courseName1);
      }

      rs = pstmt7c.executeQuery();      // find all matching events, if any

      while (rs.next()) {

         event = rs.getString(1);
         ecolor = rs.getString(2);

         if (!event.equals( event1 ) && event1.equals( "" )) {

            event1 = event;
            ecolor1 = ecolor;

            if (ecolor.equalsIgnoreCase( "default" )) {

               ecolor1 = "#F5F5DC";
            }

          } else {

            if (!event.equals( event1 ) && !event.equals( event2 ) && event2.equals( "" )) {

               event2 = event;
               ecolor2 = ecolor;

               if (ecolor.equalsIgnoreCase( "default" )) {

                  ecolor2 = "#F5F5DC";
               }

             } else {

               if (!event.equals( event1 ) && !event.equals( event2 ) && !event.equals( event3 ) && 
                   event3.equals( "" )) {

                  event3 = event;
                  ecolor3 = ecolor;

                  if (ecolor.equalsIgnoreCase( "default" )) {

                     ecolor3 = "#F5F5DC";
                  }

                } else {

                  if (!event.equals( event1 ) && !event.equals( event2 ) && !event.equals( event3 ) &&
                      !event.equals( event4 ) && event4.equals( "" )) {

                     event4 = event;
                     ecolor4 = ecolor;

                     if (ecolor.equalsIgnoreCase( "default" )) {

                        ecolor4 = "#F5F5DC";
                     }
                  }
               }
            }
         }
      }                  // end of while
      pstmt7c.close();

      //
      //  check for lotteries
      //
      pstmt7d.clearParameters();          // clear the parms
      pstmt7d.setLong(1, date);
      pstmt7d.setLong(2, date);

      if (!courseName1.equals( "-ALL-" )) {
         pstmt7d.setString(3, courseName1);
      }

      rs = pstmt7d.executeQuery();      // find all matching lotteries, if any

      while (rs.next()) {

         lottery = rs.getString(1);
         lottery_recurr = rs.getString(2);
         lottery_color = rs.getString(3);
         sdays = rs.getInt(4);
         sdtime = rs.getInt(5);
         edays = rs.getInt(6);
         edtime = rs.getInt(7);
         pdays = rs.getInt(8);
         ptime = rs.getInt(9);
         slots = rs.getInt(10);

         //
         //  We must check the recurrence for this day (Monday, etc.)
         //
         if ((lottery_recurr.equals( "Every " + day_name )) ||          // if this day
             (lottery_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
             ((lottery_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
               (!day_name.equalsIgnoreCase( "saturday" )) &&
               (!day_name.equalsIgnoreCase( "sunday" ))) ||
             ((lottery_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
              (day_name.equalsIgnoreCase( "saturday" ))) ||
             ((lottery_recurr.equalsIgnoreCase( "all weekends" )) &&
              (day_name.equalsIgnoreCase( "sunday" )))) {


            if ((!lottery.equals( lott1 )) && (lott1.equals( "" ))) {

               lott1 = lottery;
               lcolor1 = lottery_color;
               sdays1 = sdays;
               sdtime1 = sdtime;
               edays1 = edays;
               edtime1 = edtime;
               pdays1 = pdays;
               ptime1 = ptime;
               slots1 = slots;

               if (lottery_color.equalsIgnoreCase( "default" )) {

                  lcolor1 = "#F5F5DC";
               }

            } else {

               if ((!lottery.equals( lott1 )) && (!lottery.equals( lott2 )) && (lott2.equals( "" ))) {

                  lott2 = lottery;
                  lcolor2 = lottery_color;
                  sdays2 = sdays;
                  sdtime2 = sdtime;
                  edays2 = edays;
                  edtime2 = edtime;
                  pdays2 = pdays;
                  ptime2 = ptime;
                  slots2 = slots;

                  if (lottery_color.equalsIgnoreCase( "default" )) {

                     lcolor2 = "#F5F5DC";
                  }
               } else {

                  if ((!lottery.equals( lott1 )) && (!lottery.equals( lott2 )) && (!lottery.equals( lott3 )) && (lott3.equals( "" ))) {

                     lott3 = lottery;
                     lcolor3 = lottery_color;
                     sdays3 = sdays;
                     sdtime3 = sdtime;
                     edays3 = edays;
                     edtime3 = edtime;
                     pdays3 = pdays;
                     ptime3 = ptime;
                     slots3 = slots;

                     if (lottery_color.equalsIgnoreCase( "default" )) {

                        lcolor3 = "#F5F5DC";
                     }
                  } else {

                     if ((!lottery.equals( lott1 )) && (!lottery.equals( lott2 )) && (!lottery.equals( lott3 )) && (!lottery.equals( lott4 )) && (lott4.equals( "" ))) {

                        lott4 = lottery;
                        lcolor4 = lottery_color;
                        sdays4 = sdays;
                        sdtime4 = sdtime;
                        edays4 = edays;
                        edtime4 = edtime;
                        pdays4 = pdays;
                        ptime4 = ptime;
                        slots4 = slots;

                        if (lottery_color.equalsIgnoreCase( "default" )) {

                           lcolor4 = "#F5F5DC";
                        }
                     }
                  }
               }
            }
         }
      }                  // end of while
      pstmt7d.close();

      //
      //  Process the lotteries if there are any for this day
      //
      //    Determine which state we are in (before req's, during req's, before process, after process)
      //
      String string12 = "";

      if (courseName1.equals( "-ALL-" )) {

         string12 = "SELECT state FROM lreqs3 " +
                    "WHERE name = ? AND date = ?";
      } else {
         string12 = "SELECT state FROM lreqs3 " +
                    "WHERE name = ? AND date = ? AND courseName = ?";
      }

      if (!lott1.equals( "" )) {

         //
         //  Get the current time
         //
         Calendar cal3 = new GregorianCalendar();    // get todays date
         int cal_hour = cal3.get(Calendar.HOUR_OF_DAY);  // 00 - 23 (military time)
         int cal_min = cal3.get(Calendar.MINUTE);
           
         curr_time = (cal_hour * 100) + cal_min;

         curr_time = SystemUtils.adjustTime(con, curr_time);   // adjust the time

         if (curr_time < 0) {          // if negative, then we went back or ahead one day

            curr_time = 0 - curr_time;        // convert back to positive value
         }
           
         //
         //  now check the day and time values        
         //
         if (advance_days > sdays1) {       // if we haven't reached the start day yet
           
            lstate1 = 1;                    // before time to take requests

         } else {

            if (advance_days == sdays1) {   // if this is the start day

               if (curr_time >= sdtime1) {   // have we reached the start time?

                  lstate1 = 2;              // after start time, before stop time to take requests

               } else {

                  lstate1 = 1;              // before time to take requests
               }
            } else {                        // we are past the start day

               lstate1 = 2;                 // after start time, before stop time to take requests
            }

            if (advance_days == edays1) {   // if this is the stop day

               if (curr_time >= edtime1) {   // have we reached the stop time?

                  lstate1 = 3;              // after stop time, before process time 
               }
            }

            if (advance_days < edays1) {   // if we are past the stop day

               lstate1 = 3;                // after stop time, before process time
            }
         }

         if (lstate1 == 3) {                // if we are now in state 3, check for state 4
          
            if (advance_days == pdays1) {   // if this is the process day
                                                                    
               if (curr_time >= ptime1) {    // have we reached the process time?

                  lstate1 = 4;              // after process time
               }
            }

            if (advance_days < pdays1) {   // if we are past the process day

               lstate1 = 4;                // after process time
            }
         }

         if (lstate1 == 4) {                // if we are now in state 4, check for pending approval

            PreparedStatement pstmt12 = con.prepareStatement (string12);

            pstmt12.clearParameters();        // clear the parms
            pstmt12.setString(1, lott1);
            pstmt12.setLong(2, date);

            if (!courseName1.equals( "-ALL-" )) {
               pstmt12.setString(3, courseName1);
            }

            rs = pstmt12.executeQuery();

            if (!rs.next()) {             // if none waiting approval

               lstate1 = 5;              // state 5 - after process & approval time

            } else {                     // still some reqs waiting

               templstate = rs.getInt(1);  // get its state

               if (templstate == 5) {      // if we processed already (some not assigned)

                  lstate1 = 5;
               }
            }
            pstmt12.close();

         }
      }   // end of if lott1
        
      if (!lott2.equals( "" )) {

         //
         //  check the day and time values
         //
         if (advance_days > sdays2) {       // if we haven't reached the start day yet

            lstate2 = 1;                    // before time to take requests

         } else {

            if (advance_days == sdays2) {   // if this is the start day

               if (curr_time >= sdtime2) {   // have we reached the start time?

                  lstate2 = 2;              // after start time, before stop time to take requests

               } else {

                  lstate2 = 1;              // before time to take requests
               }
            } else {                        // we are past the start day

               lstate2 = 2;                 // after start time, before stop time to take requests
            }

            if (advance_days == edays2) {   // if this is the stop day

               if (curr_time >= edtime2) {   // have we reached the stop time?

                  lstate2 = 3;              // after stop time, before process time
               }
            }

            if (advance_days < edays2) {   // if we are past the stop day

               lstate2 = 3;                // after stop time, before process time
            }
         }

         if (lstate2 == 3) {                // if we are now in state 3, check for state 4

            if (advance_days == pdays2) {   // if this is the process day

               if (curr_time >= ptime2) {    // have we reached the process time?

                  lstate2 = 4;              // after process time
               }
            }

            if (advance_days < pdays2) {   // if we are past the process day

               lstate2 = 4;                // after process time
            }
         }

         if (lstate2 == 4) {                // if we are now in state 4, check for pending approval

            PreparedStatement pstmt12 = con.prepareStatement (string12);

            pstmt12.clearParameters();        // clear the parms
            pstmt12.setString(1, lott2);
            pstmt12.setLong(2, date);

            if (!courseName1.equals( "-ALL-" )) {
               pstmt12.setString(3, courseName1);
            }

            rs = pstmt12.executeQuery();

            if (!rs.next()) {             // if none waiting approval

               lstate2 = 5;              // state 5 - after process & approval time

            } else {                     // still some reqs waiting

               templstate = rs.getInt(1);  // get its state

               if (templstate == 5) {      // if we processed already (some not assigned)

                  lstate2 = 5;
               }
            }
            pstmt12.close();

         }
      }   // end of if lott2

      if (!lott3.equals( "" )) {

         //
         //  check the day and time values
         //
         if (advance_days > sdays3) {       // if we haven't reached the start day yet

            lstate3 = 1;                    // before time to take requests

         } else {

            if (advance_days == sdays3) {   // if this is the start day

               if (curr_time >= sdtime3) {   // have we reached the start time?

                  lstate3 = 2;              // after start time, before stop time to take requests

               } else {

                  lstate3 = 1;              // before time to take requests
               }
            } else {                        // we are past the start day

               lstate3 = 2;                 // after start time, before stop time to take requests
            }

            if (advance_days == edays3) {   // if this is the stop day

               if (curr_time >= edtime3) {   // have we reached the stop time?

                  lstate3 = 3;              // after stop time, before process time
               }
            }

            if (advance_days < edays3) {   // if we are past the stop day

               lstate3 = 3;                // after stop time, before process time
            }
         }

         if (lstate3 == 3) {                // if we are now in state 3, check for state 4

            if (advance_days == pdays3) {   // if this is the process day

               if (curr_time >= ptime3) {    // have we reached the process time?

                  lstate3 = 4;              // after process time
               }
            }

            if (advance_days < pdays3) {   // if we are past the process day

               lstate3 = 4;                // after process time
            }
         }

         if (lstate3 == 4) {                // if we are now in state 4, check for pending approval

            PreparedStatement pstmt12 = con.prepareStatement (string12);

            pstmt12.clearParameters();        // clear the parms
            pstmt12.setString(1, lott3);
            pstmt12.setLong(2, date);

            if (!courseName1.equals( "-ALL-" )) {
               pstmt12.setString(3, courseName1);
            }

            rs = pstmt12.executeQuery();

            if (!rs.next()) {             // if none waiting approval

               lstate3 = 5;              // state 5 - after process & approval time

            } else {                     // still some reqs waiting

               templstate = rs.getInt(1);  // get its state

               if (templstate == 5) {      // if we processed already (some not assigned)

                  lstate3 = 5;
               }
            }
            pstmt12.close();

         }
      }   // end of if lott3

      if (!lott4.equals( "" )) {

         //
         //  check the day and time values
         //
         if (advance_days > sdays4) {       // if we haven't reached the start day yet

            lstate4 = 1;                    // before time to take requests

         } else {

            if (advance_days == sdays4) {   // if this is the start day

               if (curr_time >= sdtime4) {   // have we reached the start time?

                  lstate4 = 2;              // after start time, before stop time to take requests

               } else {

                  lstate4 = 1;              // before time to take requests
               }
            } else {                        // we are past the start day

               lstate4 = 2;                 // after start time, before stop time to take requests
            }

            if (advance_days == edays4) {   // if this is the stop day

               if (curr_time >= edtime4) {   // have we reached the stop time?

                  lstate4 = 3;              // after stop time, before process time
               }
            }

            if (advance_days < edays4) {   // if we are past the stop day

               lstate4 = 3;                // after stop time, before process time
            }
         }

         if (lstate4 == 3) {                // if we are now in state 3, check for state 4

            if (advance_days == pdays4) {   // if this is the process day

               if (curr_time >= ptime4) {    // have we reached the process time?

                  lstate4 = 4;              // after process time
               }
            }

            if (advance_days < pdays4) {   // if we are past the process day

               lstate4 = 4;                // after process time
            }
         }

         if (lstate4 == 4) {                // if we are now in state 4, check for pending approval

            PreparedStatement pstmt12 = con.prepareStatement (string12);

            pstmt12.clearParameters();        // clear the parms
            pstmt12.setString(1, lott4);
            pstmt12.setLong(2, date);
              
            if (!courseName1.equals( "-ALL-" )) {
               pstmt12.setString(3, courseName1);
            }

            rs = pstmt12.executeQuery();

            if (!rs.next()) {             // if none waiting approval

               lstate4 = 5;              // state 5 - after process & approval time

            } else {                     // still some reqs waiting

               templstate = rs.getInt(1);  // get its state

               if (templstate == 5) {      // if we processed already (some not assigned)

                  lstate4 = 5;
               }
            }
            pstmt12.close();

         }
      }   // end of if lott4

      //
      //  Special processing for Oakmont CC - check if there is a Shotgun Event for today (if Friday)
      //
      if (club.equals( "oakmont" ) && day_name.equals("Friday")) {

         pstmtc = con.prepareStatement (
            "SELECT dd " +
            "FROM teecurr2 " +
            "WHERE date = ? AND event_type = ?");

         pstmtc.clearParameters();        // clear the parms
         pstmtc.setLong(1, date);
         pstmtc.setInt(2, shotgun);
         rs = pstmtc.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            oakshotgun = true;         // shotgun event today
         }
         pstmtc.close();
      }

      //
      //  Special processing for Milwaukee CC - check if there are back tees
      //                                        after noon, and it is a weekday (Tues - Fri)
      //
      boolean mccGuestDay = false;

      if (club.equals( "milwaukee" ) && (day_name.equals("Tuesday") || day_name.equals("Wednesday") ||
          day_name.equals("Thursday") || day_name.equals("Friday"))) {

         pstmtc = con.prepareStatement (
            "SELECT dd " +
            "FROM teecurr2 " +
            "WHERE date = ? AND fb = 1 AND time > 1200 AND time < 1431");  // this day, back tee, noon to 2:30

         pstmtc.clearParameters();        // clear the parms
         pstmtc.setLong(1, date);
         rs = pstmtc.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            mccGuestDay = true;         // Guest Times exist for this day
         }
         pstmtc.close();
      }

      //*********************************************************************************************
      //  Display tee sheet page or Print?
      //*********************************************************************************************
      //
      if (req.getParameter("print") == null) {

         //
         //  Count the number of tee sheets displayed since last tomcat bounce
         //
         Calendar calCount = new GregorianCalendar();       // get todays date

         int hourCount = calCount.get(Calendar.HOUR_OF_DAY);     // 24 hr clock (0 - 23) Central Time

         SystemUtils.sheetCountsPro[hourCount]++;

         //
         //  Auto Refresh Controls
         //
         boolean autoRefresh = false;
         int arSecs = 0;

         //
         //  Custom for Old Warson - refresh the tee sheet every 4 minutes for proshop5 (bag room) between 7 AM and 6 PM.
         //
         if (club.equals( "oldwarson" ) && user.equalsIgnoreCase( "proshop5" )) {
           
            if (hourCount > 6 && hourCount < 18) {        // if between 7 AM and 6 PM 
  
               autoRefresh = true;                        // use auto refresh
               arSecs = 240;                              // every 4 minutes
            }
         }

         //
         //  Custom for Cherry Hills - refresh the tee sheet every 10 minutes for proshop4 (bag room) between 7 AM and 7 PM.
         //
         if (club.equals( "cherryhills" ) && user.equalsIgnoreCase( "proshop4" )) {

            if (hourCount > 7 && hourCount < 19) {        // if between 7 AM and 7 PM CT (6 AM - 6 PM MT)

               autoRefresh = true;                        // use auto refresh
               arSecs = 600;                              // every 10 minutes
            }
         }
         
         //
         //  Custom for Sunnehanna - refresh the tee sheet every 10 minutes for proshop2 (bag room) between 7 AM and 7 PM.
         //
         if (club.equals( "sunnehanna" ) && user.equalsIgnoreCase( "proshop2" )) {

            if (hourCount > 5 && hourCount < 17) {        // if between 5 AM and 5 PM CT (6 AM - 6 PM ET) 

               autoRefresh = true;                        // use auto refresh
               arSecs = 600;                              // every 10 minutes
            }
         }

         //
         //  Custom for Outdoor CC - refresh the tee sheet every 10 minutes for proshop2 (bag room) between 6 AM and 6 PM.
         //
         if (club.equals( "outdoor" ) && user.equalsIgnoreCase( "proshop4" )) {

            if (hourCount > 5 && hourCount < 17) {        // if between 5 AM and 5 PM CT (6 AM - 6 PM ET)

               autoRefresh = true;                        // use auto refresh
               arSecs = 600;                              // every 10 minutes
            }
         }

        //
        // Start Tee Sheet Summary
        //
        String courseClause = (courseName1.equals("-ALL-")) ? "" : "courseName = ? AND";
        String sqlQuery1 =  "";
        String sqlQuery1a =  "";
        String sqlQuery2 = "";
        String sqlQuery3 = "";
        String tmp_error = "";
        
        if (fives == 0) {
            // query  to count the total # of tee times available
            sqlQuery1 =  "SELECT COUNT(date) AS total FROM teecurr2 WHERE " + courseClause + " date = ? AND blocker = \"\";"; //  AND restriction = \"\"
            // query to count the total # of tee time player positions occupied
            sqlQuery2 = "SELECT SUM(subtotal) AS total FROM (" +
                "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player1 <> '' " +
                "UNION ALL " +
                "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player2 <> '' " +
                "UNION ALL " +
                "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player3 <> '' " +
                "UNION ALL " +
                "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player4 <> '' " +
                ") AS gtotal;";
            // query to count the # of occupied tee times (full or partially full)
            sqlQuery3 = "SELECT COUNT(date) AS total FROM teecurr2 WHERE " + courseClause + " date = ? AND blocker = '' AND (player1 <> '' OR player2 <> '' OR player3 <> '' OR player4 <> '');"; // AND restriction = '' 
        } else {
            // query  to count the total # of tee times available
            sqlQuery1 =  "SELECT COUNT(date) AS total FROM teecurr2 WHERE " + courseClause + " date = ? AND blocker = \"\";"; //  AND restriction = \"\"
            sqlQuery1a =  "SELECT COUNT(date) AS total FROM teecurr2 WHERE " + courseClause + " date = ? AND blocker = \"\" AND rest5 = \"\";";  // restriction = \"\" AND
            // query to count the total # of tee time player positions occupied
           sqlQuery2 = "SELECT SUM(subtotal) AS total FROM (" +
                "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player1 <> '' " +
                "UNION ALL " +
                "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player2 <> '' " +
                "UNION ALL " +
                "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player3 <> '' " +
                "UNION ALL " +
                "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player4 <> '' " +
                "UNION ALL " +
                "SELECT COUNT(date) AS subtotal FROM teecurr2 WHERE " + courseClause + " date = ? AND player5 <> '' " +
                ") AS gtotal;";
            // query to count the # of occupied tee times (full or partially full)
            sqlQuery3 = "SELECT COUNT(date) AS total FROM teecurr2 WHERE " + courseClause + " date = ? AND blocker = '' AND (player1 <> '' OR player2 <> '' OR player3 <> '' OR player4 <> '' OR player5 <> '');"; // AND restriction = ''
            
        }
        
        int available_teetimes = 0;
        int available_slots = 0;
        int occupied_slots = 0;
        int full_teetimes = 0;
        int available_teetimes5 = 0;
        
        //out.println("<!-- " + sqlQuery1 + " -->");
        //out.println("<!-- " + sqlQuery1a + " -->");
        //out.println("<!-- " + sqlQuery2 + " -->");
        //out.println("<!-- " + sqlQuery3 + " -->");
            
        // 1st query
        tmp_error = "SQL - Available Tee Times";
        PreparedStatement pstmt1 = con.prepareStatement (sqlQuery1);
        pstmt1.clearParameters();

        if (courseName1.equals("-ALL-")) {
            pstmt1.setLong(1, (year * 10000) + (month * 100) + day);
        } else {
            pstmt1.setString(1, courseName1);
            pstmt1.setLong(2, (year * 10000) + (month * 100) + day);
        }

        rs = pstmt1.executeQuery();
        tmp_error = "SQL - Available Tee Times - Query ran";
        if (rs.next()) {
            available_teetimes = rs.getInt("total");
            //available_slots = available_teetimes * ((fives == 0) ? 4 : 5);
            available_slots = available_teetimes * 4; // fives will be added to this value from query 1a
        }
        
        // only run query 1a if fives==1
        if (fives == 1) {
            tmp_error = "SQL - Available 5th slot times";
            pstmt1 = con.prepareStatement (sqlQuery1a);
            pstmt1.clearParameters();

            if (courseName1.equals("-ALL-")) {
                pstmt1.setLong(1, (year * 10000) + (month * 100) + day);
            } else {
                pstmt1.setString(1, courseName1);
                pstmt1.setLong(2, (year * 10000) + (month * 100) + day);
            }

            rs = pstmt1.executeQuery();
            tmp_error = "SQL - Available 5th slot times - Query ran";
            if (rs.next()) {
                available_teetimes5 = rs.getInt("total");
                available_slots += available_teetimes5;
            }
        }
        
        // 2nd query
        tmp_error = "SQL - Occupied Player Positions";

        pstmt1 = con.prepareStatement (sqlQuery2);
        pstmt1.clearParameters();

        if (courseName1.equals("-ALL-")) {
            pstmt1.setLong(1, (year * 10000) + (month * 100) + day);
            pstmt1.setLong(2, (year * 10000) + (month * 100) + day);
            pstmt1.setLong(3, (year * 10000) + (month * 100) + day);
            pstmt1.setLong(4, (year * 10000) + (month * 100) + day);
            if (fives == 1) pstmt1.setLong(5, (year * 10000) + (month * 100) + day);
        } else {
            pstmt1.setString(1, courseName1);
            pstmt1.setLong(2, (year * 10000) + (month * 100) + day);
            pstmt1.setString(3, courseName1);
            pstmt1.setLong(4, (year * 10000) + (month * 100) + day);
            pstmt1.setString(5, courseName1);
            pstmt1.setLong(6, (year * 10000) + (month * 100) + day);
            pstmt1.setString(7, courseName1);
            pstmt1.setLong(8, (year * 10000) + (month * 100) + day);
            if (fives == 1) {
                pstmt1.setString(9, courseName1);
                pstmt1.setLong(10, (year * 10000) + (month * 100) + day);
            }
        }

        rs = pstmt1.executeQuery();
        tmp_error = "SQL - Occupied Player Positions - Query ran";
        if (rs.next()) occupied_slots = rs.getInt("total");
        
        
        // 3rd query
        tmp_error = "SQL - Full Tee Times";

        pstmt1 = con.prepareStatement (sqlQuery3);
        pstmt1.clearParameters();

        if (courseName1.equals("-ALL-")) {
            pstmt1.setLong(1, (year * 10000) + (month * 100) + day);
        } else {
            pstmt1.setString(1, courseName1);
            pstmt1.setLong(2, (year * 10000) + (month * 100) + day);
        }

        rs = pstmt1.executeQuery();
        tmp_error = "SQL - Full Tee Times - Query ran";
        if (rs.next()) full_teetimes = rs.getInt("total");
        
        String tmpA_percent = "";
        String tmpB_percent = "";
        
        NumberFormat nf;
        nf = NumberFormat.getNumberInstance();
/*
        out.println("<!-- occupied_slots=" + occupied_slots + " -->");
        out.println("<!-- available_slots=" + available_slots + " -->");
        out.println("<!-- full_teetimes=" + full_teetimes + " -->");
        out.println("<!-- available_teetimes=" + available_teetimes + " -->");
        out.println("<!-- slots%=" + (100 * occupied_slots / available_slots) + "  -->");
        out.println("<!-- times%=" + (100 * full_teetimes / available_teetimes) + "  -->");
*/
        if (occupied_slots != 0 && available_slots != 0) {
            tmpA_percent = " &nbsp;(";
            if ((100 * occupied_slots / available_slots) < 1) {
                tmpA_percent += "<1";
            } else {
                tmpA_percent += nf.format(100 * occupied_slots / available_slots);
            }
            tmpA_percent += "%)";
        }
        if (full_teetimes != 0 && available_teetimes != 0) {
            tmpB_percent = " &nbsp;(";
            if ((100 * full_teetimes / available_teetimes) < 1) {
                tmpB_percent += "<1";
            } else {
                tmpB_percent += nf.format(100 * full_teetimes / available_teetimes);
            }
            tmpB_percent += "%)";
        }
        
        
        //
        // done running tee time summary queries
        
        
         

        //
        //  Build the HTML page to prompt user for a specific time slot
        //
        out.println(SystemUtils.HeadTitle2("Proshop Tee Sheet"));

        if (autoRefresh == true && arSecs > 29) {         // should we refresh the tee sheet automatically??  

            out.println("<meta http-equiv=\"Refresh\" content=\"" +arSecs+ "; url=/" +rev+ "/servlet/Proshop_sheet?index=" +num+ "&course=" +courseName1+ "\">");
        }

        out.println("<script language='JavaScript'>");          // Jump script
        out.println("<!--");

        out.println("function jumpToHref(anchorstr) {");

        out.println("if (location.href.indexOf(anchorstr)<0) {");

        out.println("location.href=anchorstr; }");
        out.println("}");
        out.println("// -->");
        out.println("</script>");                               // End of script

        out.println("</HEAD>");

        // include files for dynamic calendars
        out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
        out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");

        out.println("<body onLoad='jumpToHref(\"#jump" + jump + "\");' bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");

        SystemUtils.getProshopSubMenu(req, out, lotteryS);        // required to allow submenus on this page

        //
        //  Add Print Options Menu
        //
        out.println("<span id='xawmMenuPathImg-foreteesControlPanel' style='position:absolute;top:-50px'>");
        out.println("<img name='awmMenuPathImg-foreteesControlPanel' id='awmMenuPathImg-foreteesControlPanel' src='/" +rev+ "/web utilities/proshop/awmmenupath.gif' alt=''></span>");
        out.println("<script type='text/javascript'>var MenuLinkedBy='AllWebMenus [2]', awmBN='528'; awmAltUrl='';</script>");
        out.println("<script src='/" +rev+ "/web utilities/proshop/foreteesControlPanel.js' language='JavaScript1.2' type='text/javascript'></script>");
        out.println("<script type='text/javascript'>awmBuildMenu();</script>");

        //
        //  Build Tee Sheet Page
        //
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

        out.println("<a name=\"jump0\"></a>");     // create a default jump label (start of page)

        out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // whole page
        out.println("<tr><td align=\"center\">");

        out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // table for cmd tbl & cals
        out.println("<tr valign=\"top\"><td align=\"center\" rowspan=\"2\" width=\"150\">");

            //
            //  Add Print Menu Form
            //
            out.println("<form name=\"cpHlp\" action=\"/" +rev+ "/servlet/Proshop_sheet\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
            out.println("<input type=\"hidden\" name=\"print\" value=\"\">");
            out.println("<input type=\"hidden\" name=\"fsz\" value=\"\">");   // to be completed by foretees.js
            out.println("<input type=\"hidden\" name=\"excel\" value=\"\">");
            out.println("<input type=\"hidden\" name=\"order\" value=\"\">");
            out.println("<input type=\"hidden\" name=\"double_line\" value=\"\">");  // added by Paul S
            out.println("<input type=\"hidden\" name=\"revLevel\" value=\"" +rev+ "\">");
            out.println("</form>");

            //
            //  Build Control Panel
            //
            out.println("<script type=\"text/javascript\">");
            out.println("function openDiaryWindow() {");
            out.println("w = window.open ('/" +rev+ "/servlet/Proshop_diary?index=" +num+ "&course=" +courseName1+ "&year=" +year+ "&month=" +month+ "&day=" +day+ "','diaryPopup','width=640,height=455,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
            out.println("w.creator = self;");
            out.println("}");
            out.println("function checkAllIn() {");
            out.println("var y = confirm('Are you sure you want to check in all players on this tee-sheet?');");
            out.println("if (y != true) return;");
            out.println("document.location.href=\"/" +rev+ "/servlet/Proshop_sheet?index=" +num+ "&course=" +courseName1+ "&checkallin=true\";");
            out.println("}");
            out.println("function memberLookup() {");
            out.println("var y = prompt('Enter the member number you would like to lookup.', '');");
            out.println("if (y==null) return;");
            out.println("y=y.replace(/^\\s+|\\s+$/g, '');"); // trim leading & trailing 
            out.println("if (y == '') return;");
//            out.println("var ex = /^[0-9]{1,10}$/;"); // regex to enforce numeric only and 1-10 digits
//            out.println("if (!ex.test(y)) { alert('Enter numeric characters only.'); return; }");
            //out.println("w = window.open ('/" +rev+ "/servlet/Proshop_member_lookup?mem_num='+y,'memberLookupPopup','width=480,height=200,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');"); // add modal=yes to non-ie browsers
            //out.println("w.creator = self;");
            out.println("window.showModalDialog('/" +rev+ "/servlet/Proshop_member_lookup?mem_num='+y,'memberLookupPopup','status:no;dialogWidth:620px;dialogHeight:280px;resizable:yes;center:yes;help=no');");
            out.println("}");
            out.println("</script>");
    
            out.println("<br><br>");
            out.println("<table border=\"1\" width=\"150\" cellspacing=\"3\" cellpadding=\"3\" bgcolor=\"8B8970\" align=\"center\">");
            out.println("<tr>");
            out.println("<td align=\"center\"><font color=\"#000000\" size=\"3\"><b>Control Panel</b><br>");
            out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
            out.println("<a href=\"/" +rev+ "/servlet/Proshop_sheet?index=" +num+ "&course=" +courseName1+ "\" title=\"Refresh This Page\" alt=\"Refresh\">");
            out.println("Refresh Sheet</a>");

            //
            // if Abacus21, Jonas, Northstar, or TAI, and today and not course=ALL
            //
            if ((parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" ) || parmp.posType.equals( "NorthStar" ) ||
                 parmp.posType.equals( "TAI Club Management" )) &&
                 index == 0 && !courseName1.equals( "-ALL-" )) {

               out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
               out.println("<a href=\"/" +rev+ "/servlet/Proshop_sheet?index=" +num+ "&course=" +courseName1+ "&print=pos\" target=\"_blank\" title=\"Generate POS Charges\" alt=\"Send POS Charges\">");
               out.println("Send POS Charges</a>");
            }

            if (index == 0 && !courseName1.equals( "-ALL-" )) {   // if today and not course=all

               out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
               out.println("<a href=\"/" +rev+ "/servlet/Proshop_frost?course=" +courseName1+ "\" title=\"Adjust Tee Times for Frost Delay\" alt=\"Frost Delay\">");
               out.println("Frost Delay</a>");
            }

            out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
            out.println("<a href=\"/" +rev+ "/servlet/Proshop_insert?index=" +num+ "&course=" +courseName1+ "&email=yes\" target=\"_top\" title=\"Edit This Tee Sheet - Send Emails\" alt=\"Edit Tee Sheet w/ Emails\">");
            out.println("Edit Sheet w/ Emails</a>");

            out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
            out.println("<a href=\"/" +rev+ "/servlet/Proshop_insert?index=" +num+ "&course=" +courseName1+ "&email=no\" target=\"_top\" title=\"Edit This Tee Sheet - Do Not Send Emails\" alt=\"Edit Tee Sheet w/o Emails\">");
            out.println("Edit Sheet w/o Emails</a>");

            out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
            
            if (index == 0)             // only display when tee sheet is on current day
            {
            out.println("<a href=\"javascript:void(0)\" onclick=\"openDiaryWindow(); return false;\" title=\"Diary\" alt=\"Diary\">");
            out.println("Make Diary Entry</a>");            
            out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
            }
            
            out.println("<a href=\"javascript:sendEmail('/" +rev+ "/servlet/Send_email', 'addToList')\" target=\"bot\" title=\"Send Email To All Members On This Tee Sheet\" alt=\"Send Email\">");
            out.println("Send Email to Members</a>");

            // build a form for sending the email
            out.println("<form name=\"pgFrm\" >");
            out.println("<input type=\"hidden\" name=\"nextAction\" value=\"\">");
            out.println("<input type=\"hidden\" name=\"" + ActionHelper.SEARCH_TYPE + "\" value=\"" + ActionHelper.SEARCH_TEESHEET + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
            out.println("</form></td></tr><tr><td align=\"center\"><font size=\"2\">");

            if (index == 0 && (!parmp.posType.equals( "Pro-ShopKeeper" )))             // only display when tee sheet is on current day and POS is not Pro-ShopKeeper
            {
            out.println("<a href=\"javascript:void(0)\" onclick=\"checkAllIn(); return false;\" title=\"Check In All\" alt=\"Check In All\">");
            out.println("Check In All</a>");            
            out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
            }
            
            out.println("<a href=\"javascript:void(0)\" onclick=\"memberLookup(); return false;\" title=\"Member Look-Up\" alt=\"Member Look-Up\">");
            out.println("Member Look-Up</a>");            
            //out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
          
            out.println("</font></td></tr></table>");

         out.println("</td>");                                 // end of column for control panel
         
         //
         // end construction of control panel
         
         
         
         out.println("<td align=\"center\">");     // column for calendars & course selector

         //
         //  If multiple courses, then add a drop-down box for course names
         //
         if (multi != 0) {           // if multiple courses supported for this club

            String caldate = month + "/" + day + "/" + year;       // create date for _jump

            //
            //  use 2 forms so you can switch by clicking either a course or a date
            //
            if (courseCount < 5) {        // if < 5 courses, use buttons

               i = 0;
               courseName = course[i];      // get first course name from array

               out.println("<p><font size=\"3\">");
               out.println("<b>Select Course or Date:</b>&nbsp;&nbsp;");

               while ((!courseName.equals( "" )) && (i < 6)) {    // allow one more for -ALL-

                  out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?i" +num+ "=a&jump=select&calDate=" +caldate+ "&course=" +courseName+ "\" style=\"color:blue\" target=\"_top\" title=\"Switch to new course\" alt=\"" +courseName+ "\">");
                  out.println(courseName + "</a>");
                  out.println("&nbsp;&nbsp;&nbsp;");

                  i++;
                  courseName = course[i];      // get course name from array
               }
               out.println("</p>");

            } else {     // use drop-down menu

               out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" name=\"cform\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"i" + num + "\" value=\"\">");   // use current date
               out.println("<input type=\"hidden\" name=\"jump\" value=\"select\">");
               out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +caldate+ "\">");

               i = 0;
               courseName = course[i];      // get first course name from array

               out.println("<b>Course:</b>&nbsp;&nbsp;");
               out.println("<div id=\"awmobject1\">");                      // allow menus to show over this box
               out.println("<select size=\"1\" name=\"course\" onChange=\"document.cform.submit()\">");

               while ((!courseName.equals( "" )) && (i < cMax)) {

                  if (courseName.equals( courseName1 )) {
                     out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
                  } else {
                     out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
                  }
                  i++;
                  if (i < cMax) {
                     courseName = course[i];      // get course name from array
                  }
               }
               out.println("</select></div>");
               out.println("</form>");
            }
         } // end if multi

        //
        //  start a new form for the dates so you can switch by clicking either a course or a date
        //
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\" name=\"frmLoadDay\">");
        out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" +courseName1+ "\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"select\">");
        out.println("</form>");

        // table for calendars built by js
        out.println("<table align=center border=0 height=150>\n<tr valign=top>\n<td>");    // was 190 !!!

        out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

        out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td>");

        out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

        out.println("</td>\n<tr>\n</table>");

        Calendar cal_date = new GregorianCalendar(); //Calendar.getInstance();
        int cal_year = cal_date.get(Calendar.YEAR);
        int cal_month = cal_date.get(Calendar.MONTH) + 1;
        int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);
        int cal_year2 = cal_year;
        int cal_month2 = cal_month;

        out.println("<script type=\"text/javascript\">");

        out.println("var g_cal_bg_color = '#F5F5DC';");
        out.println("var g_cal_header_color = '#8B8970';");
        out.println("var g_cal_border_color = '#8B8970';");

        out.println("var g_cal_count = 2;"); // number of calendars on this page
        out.println("var g_cal_year = new Array(g_cal_count - 1);");
        out.println("var g_cal_month = new Array(g_cal_count - 1);");
        out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);");
        out.println("var g_cal_ending_month = new Array(g_cal_count - 1);");
        out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);");
        out.println("var g_cal_ending_day = new Array(g_cal_count - 1);");
        out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);");
        out.println("var g_cal_ending_year = new Array(g_cal_count - 1);");

        // set calendar date parts
        out.println("g_cal_month[0] = " + cal_month + ";");
        out.println("g_cal_year[0] = " + cal_year + ";");
        out.println("g_cal_beginning_month[0] = " + cal_month + ";");
        out.println("g_cal_beginning_year[0] = " + cal_year + ";");
        out.println("g_cal_beginning_day[0] = " + cal_day + ";");
        out.println("g_cal_ending_month[0] = " + cal_month + ";");
        out.println("g_cal_ending_day[0] = 31;");
        out.println("g_cal_ending_year[0] = " + cal_year + ";");

        cal_date.add(Calendar.MONTH, 1); // add a month
        cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
        cal_year = cal_date.get(Calendar.YEAR);
        out.println("g_cal_beginning_month[1] = " + cal_month + ";");
        out.println("g_cal_beginning_year[1] = " + cal_year + ";");
        out.println("g_cal_beginning_day[1] = 0;");
        cal_date.add(Calendar.MONTH, -1); // subtract a month

        cal_date.add(Calendar.YEAR, 1); // add a year
        cal_year = cal_date.get(Calendar.YEAR);
        cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
        out.println("g_cal_ending_month[1] = " + cal_month + ";");
        out.println("g_cal_ending_day[1] = " + cal_day + ";");
        out.println("g_cal_ending_year[1] = " + cal_year + ";");
        cal_date.add(Calendar.YEAR, -1); // subtract a year

        cal_date.add(Calendar.DAY_OF_MONTH, index); // add the # of days ahead of today this tee sheet is for
        if (cal_date.get(Calendar.MONTH) + 1 == cal_month2 && cal_date.get(Calendar.YEAR) == cal_year2) cal_date.add(Calendar.MONTH, 1);
        cal_year = cal_date.get(Calendar.YEAR);
        cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based

        out.println("g_cal_month[1] = " + cal_month + ";");
        out.println("g_cal_year[1] = " + cal_year + ";");

        out.println("</script>");

        out.println("<script language=\"javascript\">\ndoCalendar('0');\n</script>");
        out.println("<script language=\"javascript\">\ndoCalendar('1');\n</script>");
        
        out.println("<script type=\"text/javascript\">");
         out.println("function openHistoryWindow(index, course, time, fb) {");
         out.println("w = window.open ('/" +rev+ "/servlet/Proshop_teetime_history?index=' +index+ '&course=' +course+ '&time=' +time+ '&fb=' +fb+ '&history=yes','historyPopup','width=800,height=455,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
         out.println("w.creator = self;");
         out.println("}");
         out.println("function openCSGWindow(index, course, time, fb) {");
         out.println("w = window.open ('/" +rev+ "/servlet/Proshop_sheet?index=' +index+ '&course=' +course+ '&time=' +time+ '&fb=' +fb+ '&print=pos','csgPopup','width=800,height=455,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
         out.println("w.creator = self;");
         out.println("}");
         out.println("function openNotesWindow(index, course, time, fb) {");
         //out.println("if (w != null) w.focus();");
         out.println("w = window.open ('/" +rev+ "/servlet/Proshop_sheet?index=' +index+ '&course=' +course+ '&time=' +time+ '&fb=' +fb+ '&notes=yes','notesyPopup','width=640,height=360,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
         out.println("w.creator = self;");
         out.println("}");
        out.println("</script>");
        
        out.println("</td><td width=\"150\" align=\"right\">");

        //
        // Start Tee Sheet Summary
        //
        out.println("<br><br><table width=\"150\" border=\"1\" cellspacing=\"3\" cellpadding=\"3\" bgcolor=\"8B8970\" align=\"center\">");
        out.println("<tr><td align=\"center\"><font color=\"#000000\" size=\"3\"><b>Tee Sheet Summary</b><br></font></td></tr>");

        out.println("<tr><td>");

        out.println("<table border=\"0\" cellspacing=\"3\" cellpadding=\"1\">");

        out.println("<tr>");
        out.println("<th></th>");
        out.println("<th nowrap><font size=\"2\" color=\"#FFFFFF\">Total</th>");
        out.println("<th><font size=\"2\" color=\"#FFFFFF\">Taken</font></th>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td nowrap><font size=\"2\" color=\"#FFFFFF\">");
        out.println("Tee Times:</td>");
        out.println("<td><font size=\"2\" color=\"#FFFFFF\">" + available_teetimes + "</font></td>");
        out.println("<td nowrap><font size=\"2\" color=\"#FFFFFF\">" + full_teetimes + tmpB_percent + "</font></td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td nowrap><font size=\"2\" color=\"#FFFFFF\">");
        out.println("Player Slots:</td>");
        out.println("<td><font size=\"2\" color=\"#FFFFFF\">" + available_slots + "</font></td>");
        out.println("<td nowrap><font size=\"2\" color=\"#FFFFFF\">" + occupied_slots + tmpA_percent + "</font></td>");
        out.println("</tr>");

        out.println("</table>");

        out.println("</td></tr></table>"); // end tee sheet summary
        
        out.println("</td></tr>"); 
        out.println("<tr><td colspan=2>"); 
        
        //**********************************************************
        //  Continue with instructions and tee sheet
        //**********************************************************

        out.println("<table cellpadding=\"3\" align=\"center\" width=\"90%\">");
        out.println("<tr><td bgcolor=\"#336633\"><font color=\"#FFFFFF\" size=\"2\">");
        out.println("<b>Instructions:</b>  To select a tee time, ");
        out.println("just click on the button containing the time (1st column). ");
        out.println("Empty 'player' cells indicate available positions. Click on empty box near names to ");
        out.println("'check them in'.  Special Events and Restrictions, if any, are colored (see legend below).");
        if ((club.equals( "oakmont" ) && oakshotgun == false && (day_name.equals("Wednesday") || 
            day_name.equals("Friday"))) || (mccGuestDay == true)) {
            out.println("<br>Times when Multiple Guests are allowed are indicated by the green time button.");
        }
        if (parm.constimesp > 1) {              // if consecutive tee times supported
            out.println("<br>To make multiple consecutive tee times, select the number of tee times next to the ");
            out.println("earliest time desired.  Then select that time.  The following time(s) must be available.");
        }
        if (club.equals( "medinahcc" ) || club.equals( "bearpath" )) {
            out.println("<br>Colored Time Buttons: Member Times are green, Starter Times are red, Outside Play Times are blue.");
        }
        out.println("</font></td></tr></table>");
        
        //
        //
        // end upper portion of tee sheet
        out.println("</td></tr></table><br>"); 
        
        // display date and course name for tee sheet
        out.println("<font size=\"5\">");
        out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
        if (!courseName1.equals( "" )) {
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + courseName1 + "</b>");
        }
        out.println("</font><font size=\"2\">");

         //
         // If there is an event, restriction or lottery then show the applicable legend
         //
         if (!event1.equals( "" ) || !rest1.equals( "" ) || !lott1.equals( "" )) {

            // legend title
            out.println("<br><b>Tee Sheet Legend</b> (click on buttons to view info)<br>");

            if (!event1.equals( "" )) {

               out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?event=" +event1+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
               out.println("<button type=\"button\" style=\"text-decoration:none; background:" + ecolor1 + "\">" + event1 + "</button></a>");
               out.println("&nbsp;&nbsp;&nbsp;");

               if (!event2.equals( "" )) {

                  out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?event=" +event2+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
                  out.println("<button type=\"button\" style=\"text-decoration:none; background:" + ecolor2 + "\">" + event2 + "</button></a>");
                  out.println("&nbsp;&nbsp;&nbsp;");

                  if (!event3.equals( "" )) {

                     out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?event=" +event3+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
                     out.println("<button type=\"button\" style=\"text-decoration:none; background:" + ecolor3 + "\">" + event3 + "</button></a>");
                     out.println("&nbsp;&nbsp;&nbsp;");

                     if (!event4.equals( "" )) {

                        out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?event=" +event4+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
                        out.println("<button type=\"button\" style=\"text-decoration:none; background:" + ecolor4 + "\">" + event4 + "</button></a>");
                        out.println("&nbsp;&nbsp;&nbsp;");
                     }
                  }
               }
            }

            // show any restrictions
            if (!rest1.equals( "" )) {

               out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?rest=" +rest1+ "', 'newwindow', 'height=380, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
               out.println("<button type=\"button\" style=\"text-decoration:none; background:" +rcolor1+ "\">" +rest1+ "</button></a>");
               out.println("&nbsp;&nbsp;&nbsp;");

               if (!rest2.equals( "" )) {

                  out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?rest=" +rest2+ "', 'newwindow', 'height=380, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
                  out.println("<button type=\"button\" style=\"text-decoration:none; background:" +rcolor2+ "\">" +rest2+ "</button></a>");
                  out.println("&nbsp;&nbsp;&nbsp;");

                  if (!rest3.equals( "" )) {

                     out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?rest=" +rest3+ "', 'newwindow', 'height=380, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
                     out.println("<button type=\"button\" style=\"text-decoration:none; background:" +rcolor3+ "\">" +rest3+ "</button></a>");
                     out.println("&nbsp;&nbsp;&nbsp;");

                     if ((!rest4.equals( "" )) && (event1.equals( "" ))) {   // do 4 rest's if no events

                        out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?rest=" +rest4+ "', 'newwindow', 'height=380, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
                        out.println("<button type=\"button\" style=\"text-decoration:none; background:" +rcolor4+ "\">" +rest4+ "</button></a>");
                        out.println("&nbsp;&nbsp;&nbsp;");
                     }
                  }
               }
            }

            //
            //  Custom check for Cordillera Canned Restrictions - add to legend
            //
            if (club.equals( "cordillera" ) && date > 20060413 && date < 20061030) {

               out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<button type=\"button\" style=\"background:" +cordRestColor+ "\">Lodge Tee Times</button>");
            }

            // show any lottery
            if (!lott1.equals( "" ) && lstate1 < 5) {

               out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?lottery=" +lott1+ "', 'newwindow', 'height=480, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
               out.println("<button type=\"button\" style=\"text-decoration:none; background:" +lcolor1+ "\">" +lott1+ "</button></a>");
               out.println("&nbsp;&nbsp;&nbsp;");
            }

            if (!lott2.equals( "" ) && lstate2 < 5) {

               out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?lottery=" +lott2+ "', 'newwindow', 'height=480, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
               out.println("<button type=\"button\" style=\"text-decoration:none; background:" +lcolor2+ "\">" +lott2+ "</button></a>");
               out.println("&nbsp;&nbsp;&nbsp;");
            }

            if (!lott3.equals( "" ) && lstate3 < 5) {

               out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?lottery=" +lott3+ "', 'newwindow', 'height=480, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
               out.println("<button type=\"button\" style=\"text-decoration:none; background:" +lcolor3+ "\">" +lott3+ "</button></a>");
               out.println("&nbsp;&nbsp;&nbsp;");
            }

            if (!lott4.equals( "" ) && lstate4 < 5) {

               out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?lottery=" +lott4+ "', 'newwindow', 'height=480, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
               out.println("<button type=\"button\" style=\"text-decoration:none; background:" +lcolor4+ "\">" +lott4+ "</button></a>");
            }

         } else {          // no events, restrictions or lotteries for this day

            out.println("<br><b>Tee Sheet Legend</b>");

            //
            //  Custom check for Cordillera Canned Restrictions - add to legend
            //
            if (club.equals( "cordillera" ) && date > 20060413 && date < 20061030) {

               out.println("<br>");
               out.println("<button type=\"button\" style=\"background:" +cordRestColor+ "\">Lodge Tee Times</button>");
            }
         }
         out.println("<br></font><font size=\"1\">");

         out.println("<b>F/B:</b>&nbsp;&nbsp;&nbsp;&nbsp;F = Front Nine,&nbsp;&nbsp;&nbsp;B = Back Nine,&nbsp;&nbsp;&nbsp;O = Open (for cross-overs),&nbsp;&nbsp;&nbsp;S = Shotgun Event<br>");

         out.println("<b>C/W:</b>&nbsp;&nbsp;&nbsp;&nbsp;");

         for (int ic=0; ic<16; ic++) {

            if (!parmc.tmodea[ic].equals( "" )) {
               out.println(parmc.tmodea[ic]+ " = " +parmc.tmode[ic]+ "&nbsp;&nbsp;&nbsp;");
            }
         }
         out.println("<br>(9 = 9 holes)&nbsp;&nbsp;&nbsp;<b>X:</b> Check In/Out Players");
         out.println("&nbsp;&nbsp;&nbsp;<b>N:</b> Notes Attached to Reservation (click to view)");
         out.println("&nbsp;&nbsp;&nbsp;<b>H:</b> Tee Time History");

         if (parmp.posType.equals( "ClubSystems Group" ) && index == 0 && !courseName1.equals( "-ALL-" )) {

            out.println("&nbsp;&nbsp;&nbsp;<b>P:</b> Send POS Charges");
         }

         if (club.equals( "interlachen" ) || club.equals( "cherryhills" )) {      // if Interlachen or Cherry Hills

            out.println("&nbsp;&nbsp;&nbsp;<b>CA:</b> # of Caddies Assigned to Group");
         }
         if (club.equals( "cordillera" )) {                  // if Cordillera 

            out.println("&nbsp;&nbsp;&nbsp;<b>FC:</b> Forecaddie Requested");
         }
         if ((club.equals( "pecanplantation" ) || club.equals( "theranchcc" )) && index == 0) {        // if Pecan Plantation and today

            out.println("&nbsp;&nbsp;&nbsp;<b>TO:</b> Teed Off");
         }
         if (club.equals( "medinahcc" )) {                  // if Medinah 

            out.println("&nbsp;&nbsp;&nbsp;<b>P:</b> Print Tee Time");
         }
         out.println("</font>");
         
         //
         //  Custom for Cherry Hills - proshop4 (bag room) - display view-only tee sheet with bag room #s
         //
         if (club.equals( "cherryhills" ) && user.equalsIgnoreCase( "proshop4" )) {

            restrictAll = true;           // restrict all tee times to view only
            disp_mnum = false;            // use this flag to display the bag numbers
            disp_hndcp = false;           // do not use this flag 
         } 
           
         //
         //  Custom for Sunnehanna - proshop2 (bag room) - display view-only tee sheet with bag room #s
         //
         if (club.equals( "sunnehanna" ) && user.equalsIgnoreCase( "proshop2" )) {

            restrictAll = true;           // restrict all tee times to view only
            disp_mnum = false;            // display the bag slot numbers
            disp_hndcp = false;           // do not use this flag
         }

         //
         // start tee sheet header
         //
         out.println("<br>");
         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" width=\"90%\">");

         out.println("<tr bgcolor=\"#336633\">");
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>Time</b></u>");
               out.println("</font></td>");

            if (parm.constimesp > 1) {         // if Consecutive Tee Times allowed

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>#</b></u>");
               out.println("</font></td>");
            }

            if (courseName1.equals( "-ALL-" )) {

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Course</b></u>");
                  out.println("</font></td>");
            }

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>F/B</b></u>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 1</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               if (disp_hndcp == true) {
                  out.println("&nbsp;&nbsp;<u>hndcp</u>");
               }
               if (disp_mnum == true) {
                  out.println("&nbsp;&nbsp;<u>Mem#</u>");
               }
               out.println("&nbsp;");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>C/W</b></u>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 2</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               if (disp_hndcp == true) {
                  out.println("&nbsp;&nbsp;<u>hndcp</u>");
               }
               if (disp_mnum == true) {
                  out.println("&nbsp;&nbsp;<u>Mem#</u>");
               }
               out.println("&nbsp;");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>C/W</b></u>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 3</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               if (disp_hndcp == true) {
                  out.println("&nbsp;&nbsp;<u>hndcp</u>");
               }
               if (disp_mnum == true) {
                  out.println("&nbsp;&nbsp;<u>Mem#</u>");
               }
               out.println("&nbsp;");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>C/W</b></u>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 4</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               if (disp_hndcp == true) {
                  out.println("&nbsp;&nbsp;<u>hndcp</u>");
               }
               if (disp_mnum == true) {
                  out.println("&nbsp;&nbsp;<u>Mem#</u>");
               }
               out.println("&nbsp;");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>C/W</b></u>");
               out.println("</font></td>");

            if (fivesALL != 0) {

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("&nbsp;<u><b>Player 5</b></u>");
                  out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
                  if (disp_hndcp == true) {
                     out.println("&nbsp;&nbsp;<u>hndcp</u>");
                  }
                  if (disp_mnum == true) {
                     out.println("&nbsp;&nbsp;<u>Mem#</u>");
                  }
                  out.println("&nbsp;");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>C/W</b></u>");
                  out.println("</font></td>");
            }

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>X</b></u>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>N</b></u>");
               out.println("</font></td>");
                 
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>H</b></u>");
               out.println("</font></td>");
               
            if (parmp.posType.equals( "ClubSystems Group" ) && index == 0 && !courseName1.equals( "-ALL-" )) {

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>P</b></u>");
                  out.println("</font></td>");
            }

            if (club.equals( "interlachen" ) || club.equals( "cherryhills" )) {    // if Interlachen or Cherry Hills
              
               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>CA</b></u>");
                  out.println("</font></td>");
            }
            if (club.equals( "cordillera" )) {          // if Cordillera

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>FC</b></u>");
                  out.println("</font></td>");
            }
            if ((club.equals( "pecanplantation" ) || club.equals( "theranchcc" )) && index == 0) {          // if Pecan Plantation and today

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>TO</b></u>");
                  out.println("</font></td>");
            }
            if (club.equals( "medinahcc" )) {          // if Medinah

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>P</b></u>");
                  out.println("</font></td>");
            }

            out.println("</tr>");


         //
         //  Get the tee sheet for this date
         //
         String stringTee = "";

         if (courseName1.equals( "-ALL-" )) {

            if (club.equals( "cordillera" )) {         // do not show the Short course if Cordillera

               stringTee = "SELECT * " +
                           "FROM teecurr2 WHERE date = ? AND courseName != 'Short' ORDER BY time, courseName, fb";

            } else {

               // select all tee times for all courses
               stringTee = "SELECT * " +
                           "FROM teecurr2 WHERE date = ? ORDER BY time, courseName, fb";
            } // end if block - Cordillera customization
            
         } else {
             
            // select all tee times for a particular course
            stringTee = "SELECT * " +
                        "FROM teecurr2 WHERE date = ? AND courseName = ? ORDER BY time, fb";
            
         } // end if all or 1 course
         
         PreparedStatement pstmt = con.prepareStatement (stringTee);

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);         // put the parm in pstmt

         if (!courseName1.equals( "-ALL-" )) {
            pstmt.setString(2, courseName1);
         }

         rs = pstmt.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            hr = rs.getInt("hr");
            min = rs.getInt("min");
            time = rs.getInt("time");
            event = rs.getString("event");
            ecolor = rs.getString("event_color");
            rest = rs.getString("restriction");
            rcolor = rs.getString("rest_color");
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            user1 = rs.getString("username1");
            user2 = rs.getString("username2");
            user3 = rs.getString("username3");
            user4 = rs.getString("username4");
            p1cw = rs.getString("p1cw");
            p2cw = rs.getString("p2cw");
            p3cw = rs.getString("p3cw");
            p4cw = rs.getString("p4cw");
            in_use = rs.getInt("in_use");
            type = rs.getInt("event_type");
            hndcp1 = rs.getFloat("hndcp1");
            hndcp2 = rs.getFloat("hndcp2");
            hndcp3 = rs.getFloat("hndcp3");
            hndcp4 = rs.getFloat("hndcp4");
            show1 = rs.getShort("show1");
            show2 = rs.getShort("show2");
            show3 = rs.getShort("show3");
            show4 = rs.getShort("show4");
            fb = rs.getShort("fb");
            player5 = rs.getString("player5");
            user5 = rs.getString("username5");
            p5cw = rs.getString("p5cw");
            hndcp5 = rs.getFloat("hndcp5");
            show5 = rs.getShort("show5");
            notes = rs.getString("notes");
            hideNotes = rs.getShort("hideNotes");
            lottery = rs.getString("lottery");
            courseNameT = rs.getString("courseName");
            blocker = rs.getString("blocker");
            rest5 = rs.getString("rest5");
            bgcolor5 = rs.getString("rest5_color");
            mnum1 = rs.getString("mNum1");
            mnum2 = rs.getString("mNum2");
            mnum3 = rs.getString("mNum3");
            mnum4 = rs.getString("mNum4");
            mnum5 = rs.getString("mNum5");
            lottery_color = rs.getString("lottery_color");
            conf = rs.getString("conf");
            p91 = rs.getInt("p91");
            p92 = rs.getInt("p92");
            p93 = rs.getInt("p93");
            p94 = rs.getInt("p94");
            p95 = rs.getInt("p95");
            if (club.equals( "interlachen" ) || club.equals( "cherryhills" )) {         // If Interlachen - get # of caddies assigned
               numCaddies = rs.getInt("hotelNew");      // use this field since not used by Interlachen
            }
            if (club.equals( "pecanplantation" ) || club.equals( "theranchcc" )) {     // If Pecan Plantation - get teed off indicator
               numCaddies = rs.getInt("hotelNew");                                     // use this field since not used by this club
            }
            if (club.equals( "cordillera" )) {          // If Cordillera - get forecaddie indicator (saved in pos5)
               numCaddies = rs.getInt("pos5");          // use this field since not used by Cordillera
            }
            pos1 = rs.getInt("pos1");
            pos2 = rs.getInt("pos2");
            pos3 = rs.getInt("pos3");
            pos4 = rs.getInt("pos4");
            pos5 = rs.getInt("pos5");
            hole = rs.getString("hole");

            //
            //  If course=ALL requested, then set 'fives' option according to this course
            //
            if (courseName1.equals( "-ALL-" )) {
              
               if (club.equals("bellerive2006")) {

                  fives = 0;
                    
               } else {
           
                  i = 0;
                  loopall:
                  while (i < cMax) {
                     if (courseNameT.equals( course[i] )) {
                        fives = fivesA[i];          // get the 5-some option for this course
                        break loopall;              // exit loop
                     }
                     i++;
                  }
               }
            }

            //
            //  Custom check for Blackhawk -
            //
            //      If proshop1 or proshop5, limit to 8 days in advance for accessing tee times
            //
            noSubmit = 0;
            if ((user.equalsIgnoreCase( "proshop1" ) || user.equalsIgnoreCase( "proshop5" )) && 
                index > 8 && club.equals( "blackhawk" )) {

               noSubmit = 1;     // do not allow submit button
            }
              

            lskip = 0;                      // init skip switch

            //
            //  if not event, then check for lottery time (events override lotteries)
            //
            //  determine if we should skip this slot - display only one slot per lottery before its processed
            //
            if (blocker.equals( "" )) {     // check for lottery if tee time not blocked

               if (event.equals("") && !lottery.equals("")) {

                  if (lottery.equals( lott1 )) {

                     if (lstate1 < 5) {             // if lottery has not been processed (times allotted)

                        if (lskip1 != 0) {          // if we were here already

                           lskip = 1;               // skip this slot
                        }
                        lskip1 = 1;                 // make sure its set now
                     } else {
                        lottery_color = "";         // already processed, do not use color
                     }
                  } else {

                     if (lottery.equals( lott2 )) {

                        if (lstate2 < 5) {

                           if (lskip2 != 0) {          // if we were here already

                              lskip = 1;               // skip this slot
                           }
                           lskip2 = 1;                 // make sure its set now
                        } else {
                           lottery_color = "";         // already processed, do not use color
                        }
                     } else {

                        if (lottery.equals( lott3 )) {

                           if (lstate3 < 5) {

                              if (lskip3 != 0) {          // if we were here already

                                 lskip = 1;               // skip this slot
                              }
                              lskip3 = 1;                 // make sure its set now
                           } else {
                              lottery_color = "";         // already processed, do not use color
                           }
                        } else {

                           if (lottery.equals( lott4 )) {

                              if (lstate4 < 5) {

                                 if (lskip4 != 0) {          // if we were here already

                                    lskip = 1;               // skip this slot
                                 }
                                 lskip4 = 1;                 // make sure its set now
                              } else {
                                 lottery_color = "";         // already processed, do not use color
                              }
                           }
                        }
                     }
                  }
               }          // end of IF lottery
            }          // end of IF blocker

            if (blocker.equals( "" ) && lskip == 0) {    // continue if tee time not blocked & not lottery - else skip

               ampm = " AM";
               if (hr == 12) {
                  ampm = " PM";
               }
               if (hr > 12) {
                  ampm = " PM";
                  hr = hr - 12;    // convert to conventional time
               }

               bgcolor = "#F5F5DC";               //default

               if (!event.equals("")) {
                  bgcolor = ecolor;
               } else {

                  if (!lottery.equals("") && !lottery_color.equals("")) {
                     bgcolor = lottery_color;
                  } else {

                     if (!rest.equals("")) {
                        bgcolor = rcolor;
                     }
                  }
               }

               if (bgcolor.equals("Default")) {
                  bgcolor = "#F5F5DC";              //default
               }

               //
               //  Custom check for Cordillera Canned Restrictions
               //
               if (club.equals( "cordillera" )) {

                  if (date > 20060413 && date < 20061030) {         // if this is within the custom date range

                     boolean corRest = cordilleraCustom.checkCordillera(date, time, courseNameT, "proshop"); // go check if member time

                     if (corRest == false) {                    // if restricted to members (hotel only time)
                        bgcolor = cordRestColor;                // set color for this slot
                     }
                  }
               }

               if (bgcolor5.equals("")) {
                  bgcolor5 = bgcolor;               // player5 bgcolor = others if 5-somes not restricted
               }

               if (p91 == 1) {          // if 9 hole round
                  p1cw = p1cw + "9";
               }
               if (p92 == 1) {
                  p2cw = p2cw + "9";
               }
               if (p93 == 1) {
                  p3cw = p3cw + "9";
               }
               if (p94 == 1) {
                  p4cw = p4cw + "9";
               }
               if (p95 == 1) {
                  p5cw = p5cw + "9";
               }

               if (player1.equals("")) {
                  p1cw = "";
               }
               if (player2.equals("")) {
                  p2cw = "";
               }
               if (player3.equals("")) {
                  p3cw = "";
               }
               if (player4.equals("")) {
                  p4cw = "";
               }
               if (player5.equals("")) {
                  p5cw = "";
               }

               g1 = 0;     // init guest indicators
               g2 = 0;
               g3 = 0;
               g4 = 0;
               g5 = 0;

               //
               //  Check if any player names are guest names
               //
               if (!player1.equals( "" )) {

                  i = 0;
                  ploop1:
                  while (i < parm.MAX_Guests) {
                     if (player1.startsWith( parm.guest[i] )) {

                        g1 = 1;       // indicate player1 is a guest name
                        break ploop1;
                     }
                     i++;
                  }
               }
               if (!player2.equals( "" )) {

                  i = 0;
                  ploop2:
                  while (i < parm.MAX_Guests) {
                     if (player2.startsWith( parm.guest[i] )) {

                        g2 = 1;       // indicate player2 is a guest name
                        break ploop2;
                     }
                     i++;
                  }
               }
               if (!player3.equals( "" )) {

                  i = 0;
                  ploop3:
                  while (i < parm.MAX_Guests) {
                     if (player3.startsWith( parm.guest[i] )) {

                        g3 = 1;       // indicate player3 is a guest name
                        break ploop3;
                     }
                     i++;
                  }
               }
               if (!player4.equals( "" )) {

                  i = 0;
                  ploop4:
                  while (i < parm.MAX_Guests) {
                     if (player4.startsWith( parm.guest[i] )) {

                        g4 = 1;       // indicate player4 is a guest name
                        break ploop4;
                     }
                     i++;
                  }
               }
               if (!player5.equals( "" )) {

                  i = 0;
                  ploop5:
                  while (i < parm.MAX_Guests) {
                     if (player5.startsWith( parm.guest[i] )) {

                        g5 = 1;       // indicate player5 is a guest name
                        break ploop5;
                     }
                     i++;
                  }
               }

               //
               //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
               //
               sfb = "F";       // default Front 9

               if (fb == 1) {

                  sfb = "B";
               }

               if (fb == 9) {

                  sfb = "O";
               }

               if (type == shotgun) {

                  sfb = "S";            // there's an event and its type is 'shotgun'
               }

               submit = "time:" + fb;       // create a name for the submit button to include 'time:' & fb

               out.println("<tr>");         // new table row

               j++;                                       // increment the jump label index (where to jump on page)
               out.println("<a name=\"jump" + j + "\"></a>"); // create a jump label for 'noshow' returns

               if (in_use == 0 && noSubmit == 0 && restrictAll == false) {     // if not in use, then create submit button

                  //
                  //  if not event, then check for lottery time (events override lotteries)
                  //
                  if (event.equals("") && !lottery.equals("")) {  // lotteries are not in use before processing date/time

                     if (lottery.equals( lott1 )) {

                        if (lstate1 < 4) {

                           out.println("<form action=\"/" +rev+ "/servlet/Proshop_lott\" method=\"post\" target=\"_top\">");
                           out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate1 + "\">");
                           out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lott1 + "\">");
                           out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots1 + "\">");

                        } else {

                           if (lstate1 == 4) {         // waiting for approval - force to Proshop_mlottery

                              out.println("<form action=\"/" +rev+ "/servlet/Proshop_mlottery\" method=\"post\">");
                              out.println("<input type=\"hidden\" name=\"sheet\" value=\"yes\">");
                              out.println("<input type=\"hidden\" name=\"approve\" value=\"yes\">");
                              out.println("<input type=\"hidden\" name=\"dd\" value=\"" + day + "\">");
                              out.println("<input type=\"hidden\" name=\"mm\" value=\"" + month + "\">");
                              out.println("<input type=\"hidden\" name=\"yy\" value=\"" + year + "\">");

                           } else {      // state 5

                              out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"get\" target=\"_top\">");
                           }
                        }
                        lstate = lstate1;

                     } else {

                        if (lottery.equals( lott2 )) {

                           if (lstate2 < 4) {

                              out.println("<form action=\"/" +rev+ "/servlet/Proshop_lott\" method=\"post\" target=\"_top\">");
                              out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate2 + "\">");
                              out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lott2 + "\">");
                              out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots2 + "\">");

                           } else {

                              if (lstate2 == 4) {         // waiting for approval - force to Proshop_mlottery

                                 out.println("<form action=\"/" +rev+ "/servlet/Proshop_mlottery\" method=\"post\">");
                                 out.println("<input type=\"hidden\" name=\"approve\" value=\"yes\">");
                                 out.println("<input type=\"hidden\" name=\"sheet\" value=\"yes\">");
                                 out.println("<input type=\"hidden\" name=\"dd\" value=\"" + day + "\">");
                                 out.println("<input type=\"hidden\" name=\"mm\" value=\"" + month + "\">");
                                 out.println("<input type=\"hidden\" name=\"yy\" value=\"" + year + "\">");

                              } else {      // state 5

                                 out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"get\" target=\"_top\">");
                              }
                           }
                           lstate = lstate2;

                        } else {

                           if (lottery.equals( lott3 )) {

                              if (lstate3 < 4) {

                                 out.println("<form action=\"/" +rev+ "/servlet/Proshop_lott\" method=\"post\" target=\"_top\">");
                                 out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate3 + "\">");
                                 out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lott3 + "\">");
                                 out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots3 + "\">");

                              } else {

                                 if (lstate3 == 4) {         // waiting for approval - force to Proshop_mlottery

                                    out.println("<form action=\"/" +rev+ "/servlet/Proshop_mlottery\" method=\"post\">");
                                    out.println("<input type=\"hidden\" name=\"approve\" value=\"yes\">");
                                    out.println("<input type=\"hidden\" name=\"sheet\" value=\"yes\">");
                                    out.println("<input type=\"hidden\" name=\"dd\" value=\"" + day + "\">");
                                    out.println("<input type=\"hidden\" name=\"mm\" value=\"" + month + "\">");
                                    out.println("<input type=\"hidden\" name=\"yy\" value=\"" + year + "\">");

                                 } else {

                                    out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"get\" target=\"_top\">");
                                 }
                              }
                              lstate = lstate3;

                           } else {

                              if (lottery.equals( lott4 )) {

                                 if (lstate4 < 4) {

                                    out.println("<form action=\"/" +rev+ "/servlet/Proshop_lott\" method=\"post\" target=\"_top\">");
                                    out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate4 + "\">");
                                    out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lott4 + "\">");
                                    out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots4 + "\">");

                                 } else {

                                    if (lstate4 == 4) {         // waiting for approval - force to Proshop_mlottery

                                       out.println("<form action=\"/" +rev+ "/servlet/Proshop_mlottery\" method=\"post\">");
                                       out.println("<input type=\"hidden\" name=\"approve\" value=\"yes\">");
                                       out.println("<input type=\"hidden\" name=\"sheet\" value=\"yes\">");
                                       out.println("<input type=\"hidden\" name=\"dd\" value=\"" + day + "\">");
                                       out.println("<input type=\"hidden\" name=\"mm\" value=\"" + month + "\">");
                                       out.println("<input type=\"hidden\" name=\"yy\" value=\"" + year + "\">");

                                    } else {

                                       out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"get\" target=\"_top\">");
                                    }
                                 }
                                 lstate = lstate4;

                              } else {     // more than 4 lotteries in one day !!!

                                 //
                                 //  save error message in /" +rev+ "/error.txt
                                 //
                                 String errorMsg = "Proshop_sheet: More than 4 lotteries defined for one day at " + club;    // build error msg
                                 SystemUtils.logError(errorMsg);                           // log it

                                 out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"get\" target=\"_top\">");
                              }
                           }
                        }
                     }
                  } else {   // no lottery for this tee time

                     lstate = 0;
                     out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"get\" target=\"_top\">");
                       
                     if (type == shotgun) {    // if shotgun event - inform _slot

                        out.println("<input type=\"hidden\" name=\"shotgunevent\" value=\"yes\">");
                     }

                  }
                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                  out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseNameT + "\">");
                  out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + courseName1 + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
                  //
                  //  **** Check for 5-some restriction ********* Always allow for proshop.
                  //             But inform Proshop_slot so we can warn proshop.
                  //
                  if (fives != 0) {

                     out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");

                     if (!rest5.equals( "" )) {          // if 5-somes are restricted

                        out.println("<input type=\"hidden\" name=\"p5rest\" value=\"Yes\">");  // tell _slot
                     } else {
                        out.println("<input type=\"hidden\" name=\"p5rest\" value=\"No\">");
                     }
                  } else {
                     out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                     out.println("<input type=\"hidden\" name=\"p5rest\" value=\"Yes\">");  // 5-somes restricted
                  }

                  if (lstate > 0 && lstate < 5) {

                     if (lstate == 4) {

                        out.println("<input type=\"submit\" name=\"submit\" value=\"Approve\">");

                     } else {

                        out.println("<input type=\"submit\" value=\"Lottery\">");
                        out.println("<input type=\"hidden\" name=\"" + submit + "\" value=\"" + hr + ":" + min + ampm + "\" alt=\"submit\">");
                     }
                  } else {

                     //
                     //  If Milwaukee CC, check for Special Guest Time
                     //
                     boolean mcctime = false;

                     if (club.equals("milwaukee") && mccGuestDay == true) {

                        if (fb == 1 && time > 1200 && time < 1431) {

                           mcctime = true;      // special guest time - make it a green button
                        }
                     }

                     //
                     //  If Bearpath CC check for member-only times
                     //
                     boolean beartime = false;

                     if (club.equals("bearpath")) {

                        beartime = verifySlot.checkBearpathGuests(day_name, date, time, index);
                     }

                     //
                     //  If Oakmont CC and Wed or Fir, and not a shotgun today, check if this time is one when multiple guests are allowed.
                     //  If so, make the submit button green.
                     //
                     boolean oaktime = false;

                     if (event.equals("") && club.equals("oakmont") && oakshotgun == false && (day_name.equals("Wednesday") || day_name.equals("Friday"))) {

                        //
                        //  Check the time of this tee time against those allowed for this day
                        //
                        if (day_name.equals( "Friday" )) {       // if Friday

                           oakloop1:
                           for (int oak = 0; oak < fricount; oak++) {

                              if (time == fritimes[oak]) {

                                 oaktime = true;       // tee time is ok
                                 break oakloop1;
                              }
                           }

                        } else {    // Wednesday

                           oakloop3:
                           for (int oak = 0; oak < wedcount; oak++) {      // check normal Wed times

                              if (time == wedtimes[oak]) {

                                 oaktime = true;       // tee time is ok
                                 break oakloop3;
                              }
                           }
                        }
                     }
                       
                     //
                     //  If Medinah CC check for Member Time (walk-up time)
                     //
                     if (club.equals( "medinahcc" )) {

                        medinahMemTime = medinahCustom.checkMemTime(courseNameT, day_name, time, date);
                     }


                     //
                     //  Create the Submit Button (time)
                     //
                     if (medinahMemTime == true) {    // if Medinah special time

                        if (time == 736 && courseNameT.equals( "No 3" )) {   // if Outside Play time 
                           out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\" style=\"text-decoration:none; background:lightblue\">");
                           
                        } else {
                           if ((time == 900 || time == 1100) && courseNameT.equals( "No 3" )) {   // if Starter time
                              out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\" style=\"text-decoration:none; background:#A0522D\">");
                              
                           } else {                           // Member time - light green
                              out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\" style=\"text-decoration:none; background:lightgreen\">");
                              
                           }
                        }
                     } else {
  
                        if (oaktime == true || mcctime == true || beartime == true) { // if Bearpath, Oakmont or Milwaukee special time

                           out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\" style=\"text-decoration:none; background:lightgreen\">");
                           
                        } else {

                           if (type == shotgun) {                             // if Shotgun Event

                              out.println("<input type=\"submit\" name=\"shotgun\" value=\"Shotgun\" alt=\"submit\">");
                              out.println("<input type=\"hidden\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\">");
                              
                           } else {

                              //
                              // if Oswego Lake or Desert Highlands and Notes included - orange button
                              //
                              if ((club.equals( "oswegolake" ) || club.equals( "deserthighlands" )) && !notes.equals( "" )) { 

                                 out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\" style=\"text-decoration:none; background:orange\">");
                                 
                              } else {

                                 out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\">");
                                 
                              }
                           }
                        }
                     }
                  }
                  out.println("</font></td>");

                  //
                  //  if Consecutive Tee Times allowed, no lottery or event, and tee time is empty,
                  //  adn tee is not a cros-over, allow member to select more than one tee time
                  //
                  if (parm.constimesp > 1) {

                     out.println("<td align=\"center\">");

                     if (player1.equals( "" ) && player2.equals( "" ) && player3.equals( "" ) &&
                         player4.equals( "" ) && player5.equals( "" ) &&
                         event.equals("") && lottery.equals("") && fb != 9) {

                        out.println("<select size=\"1\" name=\"contimes\">");
                        out.println("<option value=\"1\">1</option>");
                        out.println("<option value=\"2\">2</option>");
                        if (parm.constimesp > 2) {
                           out.println("<option value=\"3\">3</option>");
                        }
                        if (parm.constimesp > 3) {
                           out.println("<option value=\"4\">4</option>");
                        }
                        if (parm.constimesp > 4) {
                           out.println("<option value=\"5\">5</option>");
                        }
                        out.println("</select>");

                     } else {

                        out.println("&nbsp;");
                     }
                     out.println("</td>");
                  }
                  out.println("</form>");

               } else {                          // slot is currently in use - no submit button

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm);
                  out.println("</font></td>");

                  if (parm.constimesp > 1) {
                     out.println("<td align=\"center\">");
                     out.println("&nbsp;");
                     out.println("</td>");
                  }
               }

               if (courseName1.equals( "-ALL-" )) {
                 
                  //
                  //  Course Name
                  //
                  // set tmp_i equal to course index #
                  //
                  for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                      if (courseNameT.equals(course[tmp_i])) break;                      
                  }
               
                  out.println("<td bgcolor=\"" + course_color[tmp_i] + "\" align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println(courseNameT);
                     out.println("</font></td>");
               }

               //
               //  Front/Back Indicator
               //
               out.println("<td bgcolor=\"white\" align=\"center\">");
                  out.println("<font size=\"2\">");
                  // display if there is something in hole, then display that instead of the fb
                  out.println((!hole.equals("")) ? "  " + hole + "  " : sfb);
                  out.println("</font></td>");
  
               //
               //  Custom for Cherry Hills - proshop4 (bag room) - display view-only tee sheet with bag room #s
               //
               if ((club.equals( "cherryhills" ) && user.equalsIgnoreCase( "proshop4" )) || 
                   (club.equals( "sunnehanna" ) && user.equalsIgnoreCase( "proshop2" ))) {

                  disp_mnum = true;                 // use this flag to display the bag numbers

                  mnum1 = "";                       // init
                  mnum2 = "";
                  mnum3 = "";
                  mnum4 = "";
                  mnum5 = "";

                  if (!user1.equals( "" )) {
                    
                     mnum1 = getBag(user1, con);    // get bag# and save in mnum for display
                  }
                  if (!user2.equals( "" )) {

                     mnum2 = getBag(user2, con);
                  }
                  if (!user3.equals( "" )) {

                     mnum3 = getBag(user3, con);
                  }
                  if (!user4.equals( "" )) {

                     mnum4 = getBag(user4, con);
                  }
                  if (!user5.equals( "" )) {

                     mnum5 = getBag(user5, con);
                  }
               }

               //
               //  Build each of the player TD's for this row
               //
               buildPlayerTD(out, 1, show1, player1, bgcolor, time, num, courseNameT, fb, j, disp_hndcp, hndcp, hndcp1, g1, p1cw, disp_mnum, mnum1, courseName1);
               buildPlayerTD(out, 2, show2, player2, bgcolor, time, num, courseNameT, fb, j, disp_hndcp, hndcp, hndcp2, g2, p2cw, disp_mnum, mnum2, courseName1); 
               buildPlayerTD(out, 3, show3, player3, bgcolor, time, num, courseNameT, fb, j, disp_hndcp, hndcp, hndcp3, g3, p3cw, disp_mnum, mnum3, courseName1); 
               buildPlayerTD(out, 4, show4, player4, bgcolor, time, num, courseNameT, fb, j, disp_hndcp, hndcp, hndcp4, g4, p4cw, disp_mnum, mnum4, courseName1); 
  
               //
               //  Add Player 5 if supported
               //
               if (fivesALL != 0) {        // if 5-somes supported on any course

                  if (fives != 0) {        // if 5-somes on this course
                      
                    buildPlayerTD(out, 5, show5, player5, bgcolor5, time, num, courseNameT, fb, j, disp_hndcp, hndcp, hndcp5, g5, p5cw, disp_mnum, mnum5, courseName1); 

                  } else {         // 5-somes supported on at least 1 course, but not this one (if course=ALL)

                     out.println("<td bgcolor=\"black\" align=\"center\">");   // no 5-somes
                     out.println("<font size=\"2\">");
                     out.println("&nbsp;");
                     out.println("</font></td>");
                     out.println("<td bgcolor=\"black\" align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println("&nbsp;");
                     out.println("</font></td>");
                  }
               }

               //
               //  Next column for 'check-in all' box (add box if any players in slot)
               //
               out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet\">");
               out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               out.println("<font size=\"1\">");
               if (((!player1.equals("")) && (!player1.equalsIgnoreCase( "x" ))) ||
                   ((!player2.equals("")) && (!player2.equalsIgnoreCase( "x" ))) ||
                   ((!player3.equals("")) && (!player3.equalsIgnoreCase( "x" ))) ||
                   ((!player4.equals("")) && (!player4.equalsIgnoreCase( "x" ))) ||
                   ((!player5.equals("")) && (!player5.equalsIgnoreCase( "x" )))) {

                  out.println("<input type=\"hidden\" name=\"checkAll\" value=\"yes\">");
                  out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                  out.println("<input type=\"hidden\" name=\"name\" value=\"" + num + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
                  out.println("<input type=\"hidden\" name=\"courseCheckIn\" value=\"" + courseNameT + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
                  out.println("<input type=\"image\" src=\"/" +rev+ "/images/checkall.gif\" border=\"1\" name=\"subAll\" title=\"Click here to check all players in.\" alt=\"check all\">");
               }
               out.println("</font></td></form>");

               //
               boolean unaccomp = false;

               if (g1 + g2 + g3 + g4 + g5 != 0) {    // if 1 or more guests present

                  unaccomp = true;     // default = unaccompanied guests
                                    
                  // if any of these players are a member then set unaccomp to false (changed 3-2-05 paul)
                  unaccomp = (!player1.equals( "" ) && !player1.equalsIgnoreCase( "x" ) && g1 == 0) ? false : unaccomp; // (player1 is here, not an x, and g1 is 0)
                  unaccomp = (!player2.equals( "" ) && !player2.equalsIgnoreCase( "x" ) && g2 == 0) ? false : unaccomp;
                  unaccomp = (!player3.equals( "" ) && !player3.equalsIgnoreCase( "x" ) && g3 == 0) ? false : unaccomp;
                  unaccomp = (!player4.equals( "" ) && !player4.equalsIgnoreCase( "x" ) && g4 == 0) ? false : unaccomp;
                  unaccomp = (!player5.equals( "" ) && !player5.equalsIgnoreCase( "x" ) && g5 == 0) ? false : unaccomp;
               }
               
               //
               //  Column for 'Notes' box
               //
               //out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet\" target=\"_blank\">");
               out.println("<form>");
               out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               out.println("<font size=\"2\">");
               
               if (!notes.equals("") || !conf.equals( "" ) || unaccomp == true) {
                    
                  out.println("<input type=\"image\" src=\"/" +rev+ "/images/notes.jpg\" border=\"0\" onclick=\"openNotesWindow(" + index + ",'" + courseNameT + "', " + time + "," + fb + ");return false;\" title=\"Click here to view notes.\">");
               /*
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"yes\">");
                  out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseNameT + "\">");
                  out.println("<input type=\"image\" src=\"/" +rev+ "/images/notes.jpg\" border=\"0\" name=\"showNotes\" title=\"Click here to view notes.\">");
               */
                }
               out.println("</font></td></form>");         // end of the notes col

               
               //
               //  Column for 'TeeTime History' box
               //
               /*
               out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_teetime_history\" target=\"_tthistory\">");
               out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               out.println("<font size=\"2\">");
               
               out.println("<input type=\"hidden\" name=\"history\" value=\"yes\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseNameT + "\">");
               out.println("<input type=\"image\" src=\"/" +rev+ "/images/history.gif\" width=\"12\" height=\"13\" border=\"0\" name=\"showHistory\" title=\"Click here to view tee time history.\">");
               out.println("</font></td></form>");         // end of the teetime history col
               */
               out.println("<form><td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               out.println("<input type=\"image\" src=\"/" +rev+ "/images/history.gif\" width=\"12\" height=\"13\" border=\"0\" onclick=\"openHistoryWindow(" + index + ",'" + courseNameT + "', " + time + "," + fb + ");return false;\" title=\"Click here to view tee time history.\">");
               out.println("</td></form>");         // end of the teetime history col
               
               //
               // if CSG and today and checked in - add 'P' button to process POS charges for this tee time
               //
               if (parmp.posType.equals( "ClubSystems Group" ) && index == 0 && !courseName1.equals( "-ALL-" )) {

                  if ((show1 == 1 && pos1 == 0) || (show2 == 1 && pos2 == 0) || (show3 == 1 && pos3 == 0) ||
                      (show4 == 1 && pos4 == 0) || (show5 == 1 && pos5 == 0)) {

                     out.println("<form><td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                     out.println("<input type=\"image\" src=\"/" +rev+ "/images/sendpos.gif\" width=\"12\" height=\"13\" border=\"0\" onclick=\"openCSGWindow(" + index + ",'" + courseName1 + "', " + time + "," + fb + ");return false;\" title=\"Click here to generate POS charges.\">");
                     out.println("</td></form>");         // end of the teetime history col

/*
                     out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println("<a href=\"/" +rev+ "/servlet/Proshop_sheet?index=" +num+ "&course=" +courseName1+ "&time=" +time+ "&fb=" +fb+ "&print=pos\" target=\"_blank\" title=\"Generate POS Charges\" alt=\"Send POS Charges\">");
                     out.println("P</a>");
                     out.println("</font></td>");
*/

                  } else {

                     out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                     out.println("<font size=\"2\">&nbsp;");
                     out.println("</font></td>");
                  }
               }

               if (club.equals( "medinahcc" )) {        // if Medinah - add Print Tee Time col

                  out.println("<td bgcolor=\"#8B8970\" align=\"center\">");
                  out.println("<font size=\"2\">");

                  if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) || !player4.equals( "" )) {
                     out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/servlet/Proshop_sheet?prtTeeTime=yes&time=" +time+ "&fb=" +fb+ "&date=" +date+ "&course=" +courseNameT+ "', 'newwindow', 'Height=380, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
                     out.println("P</a>");
                  } else {
                     out.println("&nbsp;");
                  }
                  out.println("</font></td></form>");         // end of the col
               }

               if (club.equals( "interlachen" )) {      // if Interlachen - add Caddies Assigned col

                  out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet\" name=\"caform" +k+ "\">");
                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<font size=\"2\">");

                  if (p1cw.startsWith ( "CA" ) || p2cw.startsWith ( "CA" ) || p3cw.startsWith ( "CA" ) || 
                      p4cw.startsWith ( "CA" ) || p5cw.startsWith ( "CA" ) ||
                      p1cw.startsWith ( "WA" ) || p2cw.startsWith ( "WA" ) || p3cw.startsWith ( "WA" ) ||
                      p4cw.startsWith ( "WA" ) || p5cw.startsWith ( "WA" )) {

                     out.println("<select size=\"1\" name=\"caddynum\" onChange=\"document.caform" +k+ ".submit()\">");
                     if (numCaddies == 0) {
                        out.println("<option selected value=\"0\">0</option>");
                     } else {
                        out.println("<option value=\"0\">0</option>");
                     }
                     if (numCaddies == 1) {
                        out.println("<option selected value=\"1\">1</option>");
                     } else {
                        out.println("<option value=\"1\">1</option>");
                     }
                     if (numCaddies == 2) {
                        out.println("<option selected value=\"2\">2</option>");
                     } else {
                        out.println("<option value=\"2\">2</option>");
                     }
                     if (numCaddies == 3) {
                        out.println("<option selected value=\"3\">3</option>");
                     } else {
                        out.println("<option value=\"3\">3</option>");
                     }
                     if (numCaddies == 4) {
                        out.println("<option selected value=\"4\">4</option>");
                     } else {
                        out.println("<option value=\"4\">4</option>");
                     }
                     if (numCaddies == 5) {
                        out.println("<option selected value=\"5\">5</option>");
                     } else {
                        out.println("<option value=\"5\">5</option>");
                     }
                     out.println("</select>");
                    
                     out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                     out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                     out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseNameT + "\">");
                     out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

                  } else {
                     out.println("&nbsp;");
                  }
                  k++;                                     // increment form id counter
                  out.println("</font></td></form>");       
               }

               if (club.equals( "cherryhills" )) {      // if Cherry Hills - add Caddies Assigned col

                  out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet\" name=\"caform" +k+ "\">");
                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<font size=\"2\">");

                  if (p1cw.startsWith ( "CDH" ) || p2cw.startsWith ( "CDH" ) || p3cw.startsWith ( "CDH" ) ||
                      p4cw.startsWith ( "CDH" ) || p5cw.startsWith ( "CDH" ) ||
                      p1cw.startsWith ( "CDA" ) || p2cw.startsWith ( "CDA" ) || p3cw.startsWith ( "CDA" ) ||
                      p4cw.startsWith ( "CDA" ) || p5cw.startsWith ( "CDA" ) ||
                      p1cw.startsWith ( "CDB" ) || p2cw.startsWith ( "CDB" ) || p3cw.startsWith ( "CDB" ) ||
                      p4cw.startsWith ( "CDB" ) || p5cw.startsWith ( "CDB" )) {

                     out.println("<select size=\"1\" name=\"caddynum\" onChange=\"document.caform" +k+ ".submit()\">");
                     if (numCaddies == 0) {
                        out.println("<option selected value=\"0\">0</option>");
                     } else {
                        out.println("<option value=\"0\">0</option>");
                     }
                     if (numCaddies == 1) {
                        out.println("<option selected value=\"1\">1</option>");
                     } else {
                        out.println("<option value=\"1\">1</option>");
                     }
                     if (numCaddies == 2) {
                        out.println("<option selected value=\"2\">2</option>");
                     } else {
                        out.println("<option value=\"2\">2</option>");
                     }
                     if (numCaddies == 3) {
                        out.println("<option selected value=\"3\">3</option>");
                     } else {
                        out.println("<option value=\"3\">3</option>");
                     }
                     if (numCaddies == 4) {
                        out.println("<option selected value=\"4\">4</option>");
                     } else {
                        out.println("<option value=\"4\">4</option>");
                     }
                     if (numCaddies == 5) {
                        out.println("<option selected value=\"5\">5</option>");
                     } else {
                        out.println("<option value=\"5\">5</option>");
                     }
                     out.println("</select>");

                     out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                     out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                     out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseNameT + "\">");
                     out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

                  } else {
                     out.println("&nbsp;");
                  }
                  k++;                                     // increment form id counter
                  out.println("</font></td></form>");
               }

               if (club.equals( "cordillera" )) {         // if Cordillera - add ForeCaddie Assigned col

                  out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet\" name=\"caform" +k+ "\">");
                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<font size=\"2\">");

                  out.println("<select size=\"1\" name=\"forecaddy\" onChange=\"document.caform" +k+ ".submit()\">");
                  if (numCaddies == 0) {
                     out.println("<option selected value=\" \"> </option>");
                     out.println("<option value=\"Y\">Y</option>");
                  } else {
                     out.println("<option selected value=\"Y\">Y</option>");
                     out.println("<option value=\" \"> </option>");
                  }
                  out.println("</select>");

                  out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
                  out.println("<input type=\"hidden\" name=\"courseT\" value=\"" + courseNameT + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

                  k++;                                     // increment form id counter
                  out.println("</font></td></form>");
               }

               if (club.equals( "pecanplantation" ) && index == 0) {   // add 'teed off' col for Pecan Plantation

                  out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet\" name=\"caform" +k+ "\">");
                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<font size=\"1\">");

                  out.println("<select size=\"1\" name=\"teedOff\" onChange=\"document.caform" +k+ ".submit()\">");
                  if (numCaddies == 0) {
                     out.println("<option selected value=\" \"> </option>");
                     out.println("<option value=\"X\">X</option>");
                  } else {
                     out.println("<option selected value=\"X\">X</option>");
                     out.println("<option value=\" \"> </option>");
                  }
                  out.println("</select>");

                  out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

                  k++;                                     // increment form id counter
                  out.println("</font></td></form>");
               }

               if (club.equals( "theranchcc" ) && index == 0) {   // add 'teed off' col for The Ranch CC

                  out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet\" name=\"caform" +k+ "\">");
                  out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                  out.println("<font size=\"1\">");

                  out.println("<select size=\"1\" name=\"teedOff\" onChange=\"document.caform" +k+ ".submit()\">");
                  if (numCaddies == 0) {
                     out.println("<option selected value=\" \"> </option>");
                     out.println("<option value=\"O\">O</option>");
                     out.println("<option value=\"X\">X</option>");
                  } else {
                     if (numCaddies == 1) {
                        out.println("<option selected value=\"X\">X</option>");
                        out.println("<option value=\"O\">O</option>");
                        out.println("<option value=\" \"> </option>");
                     } else {
                        out.println("<option selected value=\"O\">O</option>");
                        out.println("<option value=\"X\">X</option>");
                        out.println("<option value=\" \"> </option>");
                     }
                  }
                  out.println("</select>");

                  out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

                  k++;                                     // increment form id counter
                  out.println("</font></td></form>");
               }

               out.println("</tr>");         // end of the row

            }  // end of IF Blocker

         }  // end of while (rs loop of all tee times)

         pstmt.close();

         out.println("</table>");                         // end of tee sheet table
         out.println("</td></tr>");
         out.println("</table><br>");                            // end of main page table


      } else {     // selected to print the tee sheet, bag room report or member id report

         //
         //************************************************************************
         //  Build page for printing the sheet
         //************************************************************************
         //
         String fsz = "1";
         String double_line = ""; // flag for seeing if a double_line report was requested

         if (req.getParameter("fsz") != null) {

            fsz = req.getParameter("fsz");       // get font size if requested
         }

         if (report_type.equals( "alpha" )) {            // if Alphabetical Member List Requested

            if (req.getParameter("order") != null) {     // if user requested the alpha order (member or trans)

               order = req.getParameter("order");
            }
         }

         if (excel.equalsIgnoreCase( "yes" )) {                  // if user requested Excel Spreadsheet Format

            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
         }

         //
         // see if double lines have been requested - added by Paul S.
         //
         if (req.getParameter("double_line") != null) double_line = req.getParameter("double_line");  // get double lines request
           
         if (!double_line.equals("1")) double_line = "0";         // enforce two possible values
           
         int dbl_row_colspan = (fives != 0) ? 12 : 10;            // used for html colspan property for the double (empty) line
           
         if (courseName1.equals("-ALL-")) dbl_row_colspan++;      // if all courses then add one more column for the course name
         
         //
         //  Output the page
         //
         out.println(SystemUtils.HeadTitle("Proshop Print Tee Sheet"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

         if (!excel.equalsIgnoreCase( "yes" )) {        // if normal request

            out.println("<table border=\"0\" align=\"center\"><tr><td align=\"center\">");
            out.println("<form method=\"link\" action=\"javascript:self.print()\">");

            if (report_type.equals( "print" )) {

               out.println("<button type=\"submit\" style=\"text-decoration:underline; background:#8B8970\">Print Sheet</button>");

            } else {

               out.println("<button type=\"submit\" style=\"text-decoration:underline; background:#8B8970\">Print Report</button>");
            }
            out.println("</form></td>");

            out.println("<td align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;");     // gap between buttons
            out.println("</td>");

            out.println("<td align=\"left\"><form>");
            out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" value=\"Close\" onClick='self.close()' alt=\"Close\">");
            out.println("</form></td></tr></table>");

            if (report_type.equals( "notes" )) {              // if NOTES report (display all notes in sheet)

               out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");
                  out.println("<br><b>Notes Report</b><br><br>");
               out.println("</font><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

            } else {

               out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
               if (fives != 0 && !report_type.equals( "alpha" ) && !report_type.equals( "alphat" )) {
                  out.println("<b>Hint:</b> Print in Landscape Mode.<br><br>");
               }
            }
         }

         out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
         if (!courseName1.equals( "" )) {
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + courseName1 + "</b>");
         }
         out.println("</font><font size=\"2\"><br><br>");

         //
         //  Process according to the report type requested
         //
         if (report_type.equals( "notes" )) {              // if NOTES report (display all notes in sheet)

            notesReport(date, courseName1, out, con);      // do report
            return;
         }
           
         if (!report_type.equals( "alpha" ) && !report_type.equals( "alphat" )) {   // if NOT alphabetical list

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"85%\">");
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>Time</b></u>");
                  out.println("</font></td>");

                  if (courseName1.equals( "-ALL-" )) {

                     out.println("<td align=\"center\">");
                        out.println("<font color=\"#FFFFFF\" size=\"1\">");
                        out.println("<u><b>Course</b></u>");
                        out.println("</font></td>");
                  }

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>F/B</b></u>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>Player 1</b></u> ");

                  if (report_type.equals( "print" )) {
                     if (disp_hndcp == true) {
                        out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>hndcp</u>");
                     }
                  } else {
                     if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                        out.println("&nbsp;&nbsp;&nbsp;<u><b> Member#</b></u>");
                     } else {
                        out.println("&nbsp;&nbsp;&nbsp;<u><b> Bag#</b></u>");
                     }
                  }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>C/W</b></u>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>Player 2</b></u> ");

                  if (report_type.equals( "print" )) {
                     if (disp_hndcp == true) {
                        out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>hndcp</u>");
                     }
                  } else {
                     if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                        out.println("&nbsp;&nbsp;&nbsp;<u><b> Member#</b></u>");
                     } else {
                        out.println("&nbsp;&nbsp;&nbsp;<u><b> Bag#</b></u>");
                     }
                  }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>C/W</b></u>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>Player 3</b></u> ");

                  if (report_type.equals( "print" )) {
                     if (disp_hndcp == true) {
                        out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>hndcp</u>");
                     }
                  } else {
                     if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                        out.println("&nbsp;&nbsp;&nbsp;<u><b> Member#</b></u>");
                     } else {
                        out.println("&nbsp;&nbsp;&nbsp;<u><b> Bag#</b></u>");
                     }
                  }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>C/W</b></u>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>Player 4</b></u> ");

                  if (report_type.equals( "print" )) {
                     if (disp_hndcp == true) {
                        out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>hndcp</u>");
                     }
                  } else {
                     if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                        out.println("&nbsp;&nbsp;&nbsp;<u><b> Member#</b></u>");
                     } else {
                        out.println("&nbsp;&nbsp;&nbsp;<u><b> Bag#</b></u>");
                     }
                  }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>C/W</b></u>");
                  out.println("</font></td>");

               if (fives != 0 ) {

                  out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"1\">");
                     out.println("<u><b>Player 5</b></u> ");

                     if (report_type.equals( "print" )) {
                        if (disp_hndcp == true) {
                           out.println("</font><font color=\"#FFFFFF\" size=\"1\">&nbsp;&nbsp;<u>hndcp</u>");
                        }
                     } else {
                        if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                           out.println("&nbsp;&nbsp;&nbsp;<u><b> Member#</b></u>");
                        } else {
                           out.println("&nbsp;&nbsp;&nbsp;<u><b> Bag#</b></u>");
                        }
                     }
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"1\">");
                     out.println("<u><b>C/W</b></u>");
                     out.println("</font></td>");
               }
                 
               if (club.equals( "cordillera" )) {          // if Cordillera

                  out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"1\">");
                     out.println("<u><b>FC</b></u>");
                     out.println("</font></td>");
               }

               out.println("</tr>");
            //
            //  Done outputting header row

               
            //
            //  Get the tee sheet for this date
            //
            String stringTee2 = "";

            if (courseName1.equals( "-ALL-" )) {

               stringTee2 = "SELECT " +
                        "hr, min, time, event, event_color, restriction, rest_color, player1, player2, " +
                        "player3, player4, username1, username2, username3, username4, p1cw, p2cw, p3cw, p4cw, " +
                        "event_type, hndcp1, hndcp2, hndcp3, hndcp4, show1, show2, show3, show4, " +
                        "fb, player5, username5, p5cw, hndcp5, show5, lottery, courseName, blocker, " +
                        "mNum1, mNum2, mNum3, mNum4, mNum5, " +
                        "lottery_color, p91, p92, p93, p94, p95, pos5 " +
                        "FROM teecurr2 WHERE date = ? ORDER BY time, courseName, fb";
            } else {
               stringTee2 = "SELECT " +
                        "hr, min, time, event, event_color, restriction, rest_color, player1, player2, " +
                        "player3, player4, username1, username2, username3, username4, p1cw, p2cw, p3cw, p4cw, " +
                        "event_type, hndcp1, hndcp2, hndcp3, hndcp4, show1, show2, show3, show4, " +
                        "fb, player5, username5, p5cw, hndcp5, show5, lottery, courseName, blocker, " +
                        "mNum1, mNum2, mNum3, mNum4, mNum5, " +
                        "lottery_color, p91, p92, p93, p94, p95, pos5 " +
                        "FROM teecurr2 WHERE date = ? AND courseName = ? ORDER BY time, fb";
            }
            PreparedStatement pstmt = con.prepareStatement (stringTee2);

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, date);         // put the parm in pstmt

            if (!courseName1.equals( "-ALL-" )) {
               pstmt.setString(2, courseName1);
            }
            rs = pstmt.executeQuery();      // execute the prepared stmt

            // loop thru the rs and display each timeslot as a row
            while (rs.next()) {

               hr = rs.getInt(1);
               min = rs.getInt(2);
               time = rs.getInt(3);
               event = rs.getString(4);
               ecolor = rs.getString(5);
               rest = rs.getString(6);
               rcolor = rs.getString(7);
               player1 = rs.getString(8);
               player2 = rs.getString(9);
               player3 = rs.getString(10);
               player4 = rs.getString(11);
               user1 = rs.getString(12);
               user2 = rs.getString(13);
               user3 = rs.getString(14);
               user4 = rs.getString(15);
               p1cw = rs.getString(16);
               p2cw = rs.getString(17);
               p3cw = rs.getString(18);
               p4cw = rs.getString(19);
               type = rs.getInt(20);
               hndcp1 = rs.getFloat(21);
               hndcp2 = rs.getFloat(22);
               hndcp3 = rs.getFloat(23);
               hndcp4 = rs.getFloat(24);
               show1 = rs.getShort(25);
               show2 = rs.getShort(26);
               show3 = rs.getShort(27);
               show4 = rs.getShort(28);
               fb = rs.getShort(29);
               player5 = rs.getString(30);
               user5 = rs.getString(31);
               p5cw = rs.getString(32);
               hndcp5 = rs.getFloat(33);
               show5 = rs.getShort(34);
               lottery = rs.getString(35);
               courseNameT = rs.getString(36);
               blocker = rs.getString(37);
               mnum1 = rs.getString(38);
               mnum2 = rs.getString(39);
               mnum3 = rs.getString(40);
               mnum4 = rs.getString(41);
               mnum5 = rs.getString(42);
               lottery_color = rs.getString(43);
               p91 = rs.getInt(44);
               p92 = rs.getInt(45);
               p93 = rs.getInt(46);
               p94 = rs.getInt(47);
               p95 = rs.getInt(48);
               if (club.equals( "cordillera" )) {          // If Cordillera - get forecaddie indicator (saved in pos5)
                  numCaddies = rs.getInt("pos5");          // use this field since not used by Cordillera
               }


               if (blocker.equals( "" )) {    // continue if tee time not blocked - else skip

                  ampm = " AM";
                  if (hr == 12) {
                     ampm = " PM";
                  }
                  if (hr > 12) {
                     ampm = " PM";
                     hr = hr - 12;    // convert to conventional time
                  }

                  bgcolor = "#F5F5DC";               //default

                  if (!event.equals("")) {
                     bgcolor = ecolor;
                  } else {

                     if (!rest.equals("")) {
                        bgcolor = rcolor;
                     } else {

                        if (!lottery.equals("")) {
                           bgcolor = lottery_color;
                        }
                     }
                  }

                  if (bgcolor.equals("Default")) {
                     bgcolor = "#F5F5DC";              //default
                  }

                  if (p91 == 1) {          // if 9 hole round
                     p1cw = p1cw + "9";
                  }
                  if (p92 == 1) {
                     p2cw = p2cw + "9";
                  }
                  if (p93 == 1) {
                     p3cw = p3cw + "9";
                  }
                  if (p94 == 1) {
                     p4cw = p4cw + "9";
                  }
                  if (p95 == 1) {
                     p5cw = p5cw + "9";
                  }

                  if (player1.equals("")) {
                     p1cw = "";
                  }
                  if (player2.equals("")) {
                     p2cw = "";
                  }
                  if (player3.equals("")) {
                     p3cw = "";
                  }
                  if (player4.equals("")) {
                     p4cw = "";
                  }
                  if (player5.equals("")) {
                     p5cw = "";
                  }

                  g1 = 0;     // init guest indicators
                  g2 = 0;
                  g3 = 0;
                  g4 = 0;
                  g5 = 0;

                  //
                  //  Check if any player names are guest names
                  //
                  if (!player1.equals( "" )) {

                     i = 0;
                     ploop1:
                     while (i < parm.MAX_Guests) {
                        if (player1.startsWith( parm.guest[i] )) {

                           g1 = 1;       // indicate player1 is a guest name
                           break ploop1;
                        }
                        i++;
                     }
                  }
                  if (!player2.equals( "" )) {

                     i = 0;
                     ploop2:
                     while (i < parm.MAX_Guests) {
                        if (player2.startsWith( parm.guest[i] )) {

                           g2 = 1;       // indicate player2 is a guest name
                           break ploop2;
                        }
                        i++;
                     }
                  }
                  if (!player3.equals( "" )) {

                     i = 0;
                     ploop3:
                     while (i < parm.MAX_Guests) {
                        if (player3.startsWith( parm.guest[i] )) {

                           g3 = 1;       // indicate player3 is a guest name
                           break ploop3;
                        }
                        i++;
                     }
                  }
                  if (!player4.equals( "" )) {

                     i = 0;
                     ploop4:
                     while (i < parm.MAX_Guests) {
                        if (player4.startsWith( parm.guest[i] )) {

                           g4 = 1;       // indicate player4 is a guest name
                           break ploop4;
                        }
                        i++;
                     }
                  }
                  if (!player5.equals( "" )) {

                     i = 0;
                     ploop5:
                     while (i < parm.MAX_Guests) {
                        if (player5.startsWith( parm.guest[i] )) {

                           g5 = 1;       // indicate player5 is a guest name
                           break ploop5;
                        }
                        i++;
                     }
                  }

                  //
                  //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
                  //
                  sfb = (fb == 1) ? "B" : (fb == 9) ? "0" : (type == shotgun) ? "S" : "F";
                  
                  //
                  // Start a html row for this tee time slot
                  out.println("<tr>");
                  out.println("<td align=\"center\" nowrap>&nbsp;");
                  out.println("<font size=\"" +fsz+ "\">");
                  out.println(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm);
                  out.println("&nbsp;</font></td>");
                  
                  if (courseName1.equals( "-ALL-" )) {     

                     //
                     //  Course Name
                     //
                     // set tmp_i equal to course index #
                     //
                     for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                         if (courseNameT.equals(course[tmp_i])) break;
                     }

                     out.println("<td bgcolor=\"" + course_color[tmp_i] + "\" align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println(courseNameT);
                     out.println("</font></td>");
                  }

                  out.println("<td bgcolor=\"white\" align=\"center\">");
                     out.println("<font size=\"1\">");
                     out.println(sfb);
                     out.println("</font></td>");

                  if (!player1.equals("")) {

                     if (player1.equalsIgnoreCase("x")) {

                        out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println(player1);
                        out.println("</font></td>");

                     } else {

                        out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");

                        p1 = player1;                                       // copy name only

                        if (g1 == 0) {        // if not guest then add hndcp

                           if (!report_type.equals( "print" )) {   // get bag# if this is report call

                              if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                                 if (!mnum1.equals( "" )) {
                                    p1 = (p1 + "  #" + mnum1);
                                 }
                              } else {
                              
                                 bag = getBag(user1, con);         // get user's bag #

                                 if (!bag.equals( "" )) {

                                    p1 = (p1 + "  #" + bag);
                                 }
                              }

                           } else {   // call for print sheet

                              if (disp_hndcp == true) {
                                 if ((hndcp1 == 99) || (hndcp1 == -99)) {
                                    p1 = p1 + "  NH";
                                 } else {
                                    if (hndcp1 <= 0) {
                                       hndcp1 = 0 - hndcp1;                          // convert to non-negative
                                       hndcp = Math.round(hndcp1);                   // round it off
                                       p1 = (p1 + "  " + hndcp);
                                    } else {
                                       hndcp = Math.round(hndcp1);                   // round it off
                                       p1 = (p1 + "  +" + hndcp);
                                    }
                                 }
                              }
                           }    // end of IF report
                        }       // end of IF guest
                        
                        out.println(getShowImageForPrint(show1));
                        out.println("&nbsp;" + p1);
                        out.println("</font></td>");
                     }

                  } else {
                     out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                     out.println("<font size=\"1\">");
                     out.println("&nbsp;");
                     out.println("</font></td>");
                  }

                  if ((!player1.equals("")) && (!player1.equalsIgnoreCase( "x" ))) {
                     out.println("<td bgcolor=\"white\" align=\"center\">");
                     out.println("<font size=\"1\">");
                     out.println(p1cw);
                  } else {
                     out.println("<td bgcolor=\"white\" align=\"center\">");
                     out.println("<font size=\"1\">");
                     out.println("&nbsp;");
                  }
                     out.println("</font></td>");

                  if (!player2.equals("")) {

                     if (player2.equalsIgnoreCase("x")) {

                        out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println(player2);
                        out.println("</font></td>");

                     } else {

                        out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");

                        p2 = player2;                                       // copy name only

                        if (g2 == 0) {         // if not guest then add ndcp

                           if (!report_type.equals( "print" )) {   // get bag# if this is report call

                              if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                                 if (!mnum2.equals( "" )) {
                                    p2 = (p2 + "  #" + mnum2);
                                 }
                              } else {
                              
                                 bag = getBag(user2, con);         // get user's bag #

                                 if (!bag.equals( "" )) {

                                    p2 = (p2 + "  #" + bag);
                                 }
                              }

                           } else {   // call for print sheet

                              if (disp_hndcp == true) {
                                 if ((hndcp2 == 99) || (hndcp2 == -99)) {
                                    p2 = p2 + "  NH";
                                 } else {
                                    if (hndcp2 <= 0) {
                                       hndcp2 = 0 - hndcp2;                          // convert to non-negative
                                       hndcp = Math.round(hndcp2);                   // round it off
                                       p2 = (p2 + "  " + hndcp);
                                    } else {
                                       hndcp = Math.round(hndcp2);                   // round it off
                                       p2 = (p2 + "  +" + hndcp);
                                    }
                                 }
                              }
                           }
                        }
                        out.println(getShowImageForPrint(show2));
                        out.println("&nbsp;" + p2);
                        out.println("</font></td>");
                     }
                  } else {
                     out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                     out.println("<font size=\"1\">");
                     out.println("&nbsp;");
                     out.println("</font></td>");
                  }

                  if ((!player2.equals("")) && (!player2.equalsIgnoreCase( "x" ))) {
                     out.println("<td bgcolor=\"white\" align=\"center\">");
                     out.println("<font size=\"1\">");
                     out.println(p2cw);
                  } else {
                     out.println("<td bgcolor=\"white\">");
                     out.println("<font size=\"1\">");
                     out.println("&nbsp;");
                  }
                     out.println("</font></td>");

                  if (!player3.equals("")) {

                     if (player3.equalsIgnoreCase("x")) {

                        out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println(player3);
                        out.println("</font></td>");

                     } else {

                        out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");

                        p3 = player3;                                       // copy name only

                        if (g3 == 0) {         // if not guest then add hndcp

                           if (!report_type.equals( "print" )) {   // get bag# if this is report call

                              if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                                 if (!mnum3.equals( "" )) {
                                    p3 = (p3 + "  #" + mnum3);
                                 }
                              } else {
                               
                                 bag = getBag(user3, con);         // get user's bag #

                                 if (!bag.equals( "" )) {

                                    p3 = (p3 + "  #" + bag);
                                 }
                              }

                           } else {   // call for print sheet

                              if (disp_hndcp == true) {
                                 if ((hndcp3 == 99) || (hndcp3 == -99)) {
                                    p3 = p3 + "  NH";
                                 } else {
                                    if (hndcp3 <= 0) {
                                       hndcp3 = 0 - hndcp3;                          // convert to non-negative
                                       hndcp = Math.round(hndcp3);                   // round it off
                                       p3 = (p3 + "  " + hndcp);
                                    } else {
                                       hndcp = Math.round(hndcp3);                   // round it off
                                       p3 = (p3 + "  +" + hndcp);
                                    }
                                 }
                              }
                           }
                        }
                        out.println(getShowImageForPrint(show3));
                        out.println("&nbsp;" + p3);
                        out.println("</font></td>");
                     }

                  } else {
                     out.println("<td bgcolor=\"" + bgcolor + "\">");
                     out.println("<font size=\"1\">");
                     out.println("&nbsp;");
                     out.println("</font></td>");
                  }

                  if ((!player3.equals("")) && (!player3.equalsIgnoreCase( "x" ))) {
                     out.println("<td bgcolor=\"white\" align=\"center\">");
                     out.println("<font size=\"1\">");
                     out.println(p3cw);
                  } else {
                     out.println("<td bgcolor=\"white\">");
                     out.println("<font size=\"1\">");
                     out.println("&nbsp;");
                  }
                     out.println("</font></td>");

                  if (!player4.equals("")) {

                     if (player4.equalsIgnoreCase("x")) {

                        out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println(player4);
                        out.println("</font></td>");

                     } else {

                        out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");

                        p4 = player4;                                       // copy name only

                        if (g4 == 0) {         // if not guest then add hndcp

                           if (!report_type.equals( "print" )) {   // get bag# if this is report call

                              if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                                 if (!mnum4.equals( "" )) {
                                    p4 = (p4 + "  #" + mnum4);
                                 }
                              } else {
                               
                                 bag = getBag(user4, con);         // get user's bag #

                                 if (!bag.equals( "" )) {

                                    p4 = (p4 + "  #" + bag);
                                 }
                              }

                           } else {   // call for print sheet

                              if (disp_hndcp == true) {
                                 if ((hndcp4 == 99) || (hndcp4 == -99)) {
                                    p4 = p4 + "  NH";
                                 } else {
                                    if (hndcp4 <= 0) {
                                       hndcp4 = 0 - hndcp4;                          // convert to non-negative
                                       hndcp = Math.round(hndcp4);                   // round it off
                                       p4 = (p4 + "  " + hndcp);
                                    } else {
                                       hndcp = Math.round(hndcp4);                   // round it off
                                       p4 = (p4 + "  +" + hndcp);
                                    }
                                 }
                              }
                           }
                        }
                        out.println(getShowImageForPrint(show4));
                        out.println("&nbsp;" + p4);
                        out.println("</font></td>");
                     }
                  } else {
                     out.println("<td bgcolor=\"" + bgcolor + "\">");
                     out.println("<font size=\"1\">");
                     out.println("&nbsp;");
                     out.println("</font></td>");
                  }

                  if ((!player4.equals("")) && (!player4.equalsIgnoreCase( "x" ))) {
                     out.println("<td bgcolor=\"white\" align=\"center\">");
                     out.println("<font size=\"1\">");
                     out.println(p4cw);
                  } else {
                     out.println("<td bgcolor=\"white\" align=\"center\">");
                     out.println("<font size=\"1\">");
                     out.println("&nbsp;");
                  }
                  out.println("</font></td>");

                  if (fives != 0) {

                     if (!player5.equals("")) {

                        if (player5.equalsIgnoreCase("x")) {

                           out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player5);
                           out.println("</font></td>");

                        } else {

                           out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");

                           p5 = player5;                                       // copy name only

                           if (g5 == 0) {

                              if (!report_type.equals( "print" )) {   // get bag# if this is report call

                                 if (report_type.equals( "mnum" )) {                      // if Member Id Requested
                                    if (!mnum5.equals( "" )) {
                                       p5 = (p5 + "  #" + mnum5);
                                    }
                                 } else {

                                    bag = getBag(user5, con);         // get user's bag #

                                    if (!bag.equals( "" )) {

                                       p5 = (p5 + "  #" + bag);
                                    }
                                 }

                              } else {   // call for print sheet

                                 if (disp_hndcp == true) {
                                    if ((hndcp5 == 99) || (hndcp5 == -99)) {
                                       p5 = p5 + "  NH";
                                    } else {
                                       if (hndcp5 <= 0) {
                                          hndcp5 = 0 - hndcp5;                          // convert to non-negative
                                          hndcp = Math.round(hndcp5);                   // round it off
                                          p5 = (p5 + "  " + hndcp);
                                       } else {
                                          hndcp = Math.round(hndcp5);                   // round it off
                                          p5 = (p5 + "  +" + hndcp);
                                       }
                                    }
                                 }
                              }
                           }
                           out.println(getShowImageForPrint(show5));
                           out.println("&nbsp;" + p5);
                           out.println("</font></td>");
                        }
                     } else {
                        out.println("<td bgcolor=\"" + bgcolor + "\">");
                        out.println("<font size=\"1\">");
                        out.println("&nbsp;");
                        out.println("</font></td>");
                     }

                     if ((!player5.equals("")) && (!player5.equalsIgnoreCase( "x" ))) {
                        out.println("<td bgcolor=\"white\" align=\"center\">");
                        out.println("<font size=\"1\">");
                        out.println(p5cw);
                     } else {
                        out.println("<td bgcolor=\"white\" align=\"center\">");
                        out.println("<font size=\"1\">");
                        out.println("&nbsp;");
                     }
                     out.println("</font></td>");
                  }
                    
                  if (club.equals( "cordillera" )) {         // if Cordillera - add ForeCaddie Assigned col

                     out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     if (numCaddies == 0) {
                        out.println("&nbsp;");
                     } else {
                        out.println("Y");
                     }
                     out.println("</font></td>");
                  }

                  out.println("</tr>");
                  
                  
                  // if a blank line (double_line) was requested then add one   [Paul S]
                  if (double_line.equals("1")) {
                      
                      out.println("<tr><td colspan=" + dbl_row_colspan + ">&nbsp;</td></tr>");
                  }

               }  // end of IF Blocker

            }  // end of while

            pstmt.close();
            out.println("</td></tr></table>");                  // end of whole print page

         } else {

            //
            //  Request is for an alphabetical list of members on this day's tee sheet
            //
            out.println("<font size=\"3\">");
              
            if (report_type.equals( "alphat" )) {   // if list by member and tee time
               out.println("<b>Alphabetical List of Members and Tee Times</b><br>");
            } else {
               if (order.equals( "trans" )) {
                  out.println("<b>Alphabetical List of Members and Guests by Mode of Transportation</b>");
               } else {
                  out.println("<b>Alphabetical List of Members and Guests</b>");
               }
            }
            out.println("</font>");
            out.println("<br><font size=\"2\">");
            if (report_type.equals( "alpha" )) {  
               out.println("<b>(Guests are listed directly under their host.)</b>");
               out.println("<br><br></font>");
            }

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellspacing=\"3\" cellpadding=\"3\">");
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>Member Name</b></u>");
               out.println("</font></td>");

            if (report_type.equals( "alpha" )) {

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Guest</b></u> ");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Member #</b></u>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Mode of Trans</b></u>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Checked In?</b></u>");
                  out.println("</font></td>");

            } else {      // list by member & tee times

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Tee Time</b></u>");
                  out.println("</font></td>");
                    
               if (!courseName1.equals( "" )) {         // if course specified or ALL, then include

                  out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Course</b></u>");
                     out.println("</font></td>");
               }
            }

            out.println("</tr>");

            //
            //  Gather all the members from the tee sheet and save the info in teereport4 for processing
            //
            PreparedStatement pstmta1 = null;
            String stringTee4 = "";
            String omit = "";
            String currCW = "";
            String lastCW = "";
            String rowColor = "#F5F5DC";

            if (courseName1.equals( "-ALL-" )) {

               stringTee4 = "SELECT " +
                        "time, username1, username2, username3, username4, p1cw, p2cw, p3cw, p4cw, " +
                        "show1, show2, show3, show4, " +
                        "username5, p5cw, show5, courseName, mNum1, mNum2, mNum3, mNum4, mNum5, " +
                        "userg1, userg2, userg3, userg4, userg5, p91, p92, p93, p94, p95 " +
                        "FROM teecurr2 WHERE date = ? AND " +
                        "(username1 != ? OR username2 != ? OR username3 != ? OR username4 != ? OR username5 != ? OR " +
                        "userg1 != ? OR userg2 != ? OR userg3 != ? OR userg4 != ? OR userg5 != ?)";
            } else {
               stringTee4 = "SELECT " +
                        "time, username1, username2, username3, username4, p1cw, p2cw, p3cw, p4cw, " +
                        "show1, show2, show3, show4, " +
                        "username5, p5cw, show5, courseName, mNum1, mNum2, mNum3, mNum4, mNum5, " +
                        "userg1, userg2, userg3, userg4, userg5, p91, p92, p93, p94, p95 " +
                        "FROM teecurr2 WHERE date = ? AND " +
                        "(username1 != ? OR username2 != ? OR username3 != ? OR username4 != ? OR username5 != ? OR " +
                        "userg1 != ? OR userg2 != ? OR userg3 != ? OR userg4 != ? OR userg5 != ?) " +
                        "AND courseName = ?";
            }
            PreparedStatement pstmt = con.prepareStatement (stringTee4);

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, date);         // put the parm in pstmt
            pstmt.setString(2, omit);
            pstmt.setString(3, omit);
            pstmt.setString(4, omit);
            pstmt.setString(5, omit);
            pstmt.setString(6, omit);
            pstmt.setString(7, omit);
            pstmt.setString(8, omit);
            pstmt.setString(9, omit);
            pstmt.setString(10, omit);
            pstmt.setString(11, omit);

            if (!courseName1.equals( "-ALL-" )) {
               pstmt.setString(12, courseName1);
            }
            rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               time = rs.getInt(1);
               user1 = rs.getString(2);
               user2 = rs.getString(3);
               user3 = rs.getString(4);
               user4 = rs.getString(5);
               p1cw = rs.getString(6);
               p2cw = rs.getString(7);
               p3cw = rs.getString(8);
               p4cw = rs.getString(9);
               show1 = rs.getShort(10);
               show2 = rs.getShort(11);
               show3 = rs.getShort(12);
               show4 = rs.getShort(13);
               user5 = rs.getString(14);
               p5cw = rs.getString(15);
               show5 = rs.getShort(16);
               courseNameT = rs.getString(17);
               mnum1 = rs.getString(18);
               mnum2 = rs.getString(19);
               mnum3 = rs.getString(20);
               mnum4 = rs.getString(21);
               mnum5 = rs.getString(22);
               userg1 = rs.getString(23);
               userg2 = rs.getString(24);
               userg3 = rs.getString(25);
               userg4 = rs.getString(26);
               userg5 = rs.getString(27);
               p91 = rs.getInt(28);
               p92 = rs.getInt(29);
               p93 = rs.getInt(30);
               p94 = rs.getInt(31);
               p95 = rs.getInt(32);

               if (p91 == 1) {          // if 9 hole round
                  p1cw = p1cw + "9";
               }
               if (p92 == 1) {
                  p2cw = p2cw + "9";
               }
               if (p93 == 1) {
                  p3cw = p3cw + "9";
               }
               if (p94 == 1) {
                  p4cw = p4cw + "9";
               }
               if (p95 == 1) {
                  p5cw = p5cw + "9";
               }

               //
               //  Save each member's info for later
               //
               if (!user1.equals( "" )) {

                  lname = "";
                  fname = "";
                  mi = "";

                  pstmta1 = con.prepareStatement (
                           "SELECT name_last, name_first, name_mi " +
                           "FROM member2b WHERE username = ?");

                  pstmta1.clearParameters();        // clear the parms
                  pstmta1.setString(1, user1);
                  rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     lname = rs2.getString(1);
                     fname = rs2.getString(2);
                     mi = rs2.getString(3);
                  }
                  pstmta1.close();                  // close the stmt

                  if (!lname.equals( "" )) {       // if name found

                     //
                     // save info in teereport4 (temporary)
                     //
                     pstmta1 = con.prepareStatement (
                       "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time) " +
                       "VALUES (?,?,?,?,?,?,?,?,?)");

                     pstmta1.clearParameters();        // clear the parms
                     pstmta1.setString(1, lname);
                     pstmta1.setString(2, fname);
                     pstmta1.setString(3, mi);
                     pstmta1.setString(4, user1);
                     pstmta1.setString(5, p1cw);
                     pstmta1.setString(6, mnum1);
                     pstmta1.setShort(7, show1);
                     pstmta1.setString(8, courseNameT);
                     pstmta1.setInt(9, time);
                     pstmta1.executeUpdate();          // execute the prepared stmt

                     pstmta1.close();   // close the stmt
                  }
               }      // end of if user

               if (!user2.equals( "" )) {

                  lname = "";
                  fname = "";
                  mi = "";

                  pstmta1 = con.prepareStatement (
                           "SELECT name_last, name_first, name_mi " +
                           "FROM member2b WHERE username = ?");

                  pstmta1.clearParameters();        // clear the parms
                  pstmta1.setString(1, user2);
                  rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     lname = rs2.getString(1);
                     fname = rs2.getString(2);
                     mi = rs2.getString(3);
                  }
                  pstmta1.close();                  // close the stmt

                  if (!lname.equals( "" )) {       // if name found

                     //
                     // save info in teereport4 (temporary)
                     //
                     pstmta1 = con.prepareStatement (
                       "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time) " +
                       "VALUES (?,?,?,?,?,?,?,?,?)");

                     pstmta1.clearParameters();        // clear the parms
                     pstmta1.setString(1, lname);
                     pstmta1.setString(2, fname);
                     pstmta1.setString(3, mi);
                     pstmta1.setString(4, user2);
                     pstmta1.setString(5, p2cw);
                     pstmta1.setString(6, mnum2);
                     pstmta1.setShort(7, show2);
                     pstmta1.setString(8, courseNameT);
                     pstmta1.setInt(9, time);
                     pstmta1.executeUpdate();          // execute the prepared stmt

                     pstmta1.close();   // close the stmt
                  }
               }      // end of if user

               if (!user3.equals( "" )) {

                  lname = "";
                  fname = "";
                  mi = "";

                  pstmta1 = con.prepareStatement (
                           "SELECT name_last, name_first, name_mi " +
                           "FROM member2b WHERE username = ?");

                  pstmta1.clearParameters();        // clear the parms
                  pstmta1.setString(1, user3);
                  rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     lname = rs2.getString(1);
                     fname = rs2.getString(2);
                     mi = rs2.getString(3);
                  }
                  pstmta1.close();                  // close the stmt

                  if (!lname.equals( "" )) {       // if name found

                     //
                     // save info in teereport4 (temporary)
                     //
                     pstmta1 = con.prepareStatement (
                       "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time) " +
                       "VALUES (?,?,?,?,?,?,?,?,?)");

                     pstmta1.clearParameters();        // clear the parms
                     pstmta1.setString(1, lname);
                     pstmta1.setString(2, fname);
                     pstmta1.setString(3, mi);
                     pstmta1.setString(4, user3);
                     pstmta1.setString(5, p3cw);
                     pstmta1.setString(6, mnum3);
                     pstmta1.setShort(7, show3);
                     pstmta1.setString(8, courseNameT);
                     pstmta1.setInt(9, time);
                     pstmta1.executeUpdate();          // execute the prepared stmt

                     pstmta1.close();   // close the stmt
                  }
               }      // end of if user

               if (!user4.equals( "" )) {

                  lname = "";
                  fname = "";
                  mi = "";

                  pstmta1 = con.prepareStatement (
                           "SELECT name_last, name_first, name_mi " +
                           "FROM member2b WHERE username = ?");

                  pstmta1.clearParameters();        // clear the parms
                  pstmta1.setString(1, user4);
                  rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     lname = rs2.getString(1);
                     fname = rs2.getString(2);
                     mi = rs2.getString(3);
                  }
                  pstmta1.close();                  // close the stmt

                  if (!lname.equals( "" )) {       // if name found

                     //
                     // save info in teereport4 (temporary)
                     //
                     pstmta1 = con.prepareStatement (
                       "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time) " +
                       "VALUES (?,?,?,?,?,?,?,?,?)");

                     pstmta1.clearParameters();        // clear the parms
                     pstmta1.setString(1, lname);
                     pstmta1.setString(2, fname);
                     pstmta1.setString(3, mi);
                     pstmta1.setString(4, user4);
                     pstmta1.setString(5, p4cw);
                     pstmta1.setString(6, mnum4);
                     pstmta1.setShort(7, show4);
                     pstmta1.setString(8, courseNameT);
                     pstmta1.setInt(9, time);
                     pstmta1.executeUpdate();          // execute the prepared stmt

                     pstmta1.close();   // close the stmt
                  }
               }      // end of if user

               if (!user5.equals( "" )) {

                  lname = "";
                  fname = "";
                  mi = "";

                  pstmta1 = con.prepareStatement (
                           "SELECT name_last, name_first, name_mi " +
                           "FROM member2b WHERE username = ?");

                  pstmta1.clearParameters();        // clear the parms
                  pstmta1.setString(1, user5);
                  rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     lname = rs2.getString(1);
                     fname = rs2.getString(2);
                     mi = rs2.getString(3);
                  }
                  pstmta1.close();                  // close the stmt

                  if (!lname.equals( "" )) {       // if name found

                     //
                     // save info in teereport4 (temporary)
                     //
                     pstmta1 = con.prepareStatement (
                       "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time) " +
                       "VALUES (?,?,?,?,?,?,?,?,?)");

                     pstmta1.clearParameters();        // clear the parms
                     pstmta1.setString(1, lname);
                     pstmta1.setString(2, fname);
                     pstmta1.setString(3, mi);
                     pstmta1.setString(4, user5);
                     pstmta1.setString(5, p5cw);
                     pstmta1.setString(6, mnum5);
                     pstmta1.setShort(7, show5);
                     pstmta1.setString(8, courseNameT);
                     pstmta1.setInt(9, time);
                     pstmta1.executeUpdate();          // execute the prepared stmt

                     pstmta1.close();   // close the stmt
                  }
               }      // end of if user

            }      // end of WHILE

            pstmt.close();                  // close the stmt

            //
            //  All members are now saved in teereport4 - list them and their guests alphabetically by mode of trans
            //
            String userT = "";

            if (club.equals( "blackhawk" ) && order.equals( "trans" )) {

               String bhwk1 = "";
               String bhwk9 = "";

               //
               //  Blackhawk CC - they want a list by mode of trans in the order they specify them in club setup
               //
               for (int blk=0; blk<16; blk++) {         // check all 16 modes of trans

                  if (!parmc.tmodea[blk].equals( "" )) {      // if tmode specified

                     bhwk1 = parmc.tmodea[blk];            // get tmode
                     bhwk9 = bhwk1 + "9";                  // get 9-hole version

                     pstmt = con.prepareStatement (
                           "SELECT lname, fname, mi, username, tmode, mNum, checkIn " +
                           "FROM teereport4 WHERE tmode = ? OR tmode = ? " +
                           "ORDER BY lname, fname, mi");

                     pstmt.clearParameters();        // clear the parms
                     pstmt.setString(1, bhwk1);
                     pstmt.setString(2, bhwk9);
                     rs = pstmt.executeQuery();      // execute the prepared pstmt

                     while (rs.next()) {

                        lname = rs.getString(1);
                        fname = rs.getString(2);
                        mi = rs.getString(3);
                        userT = rs.getString(4);
                        tmode = rs.getString(5);
                        mnum1 = rs.getString(6);
                        show1 = rs.getShort(7);

                        StringBuffer mem_name = new StringBuffer(lname);  // get last name

                        mem_name.append(", ");
                        mem_name.append(fname);
                        if (!mi.equals( "" )) {
                           mem_name.append(" ");
                           mem_name.append(mi);
                        }
                        lname = mem_name.toString();                      // convert to one string

                        //
                        //  Determine color to use in table - alternate for each new mode of trans or member
                        //
                        if (order.equals( "trans" )) {

                           currCW = tmode;                         // get this mode of trans

                           if (currCW.endsWith( "9" )) {            // if 9-hole CW

                              currCW = strip9( currCW );            // remove the '9'
                           }

                           if (lastCW.equals( "" )) {     // if first time

                              lastCW = currCW;             // set this as current, use current color

                           } else {

                              if (!currCW.equals( lastCW )) {   // if tmode has changed

                                 lastCW = currCW;               // set this as current and switch color

                                 if (rowColor.equals( "#F5F5DC" )) {

                                    rowColor = "#CDCDB4";         // switch

                                 } else {

                                    rowColor = "#F5F5DC";         // switch
                                 }
                              }
                           }

                        } else {   // change per member

                           if (rowColor.equals( "#F5F5DC" )) {

                              rowColor = "#CDCDB4";         // switch

                           } else {

                              rowColor = "#F5F5DC";         // switch
                           }

                        }

                        out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"left\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println(lname);                               // Member name
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println("n/a");                                // Guest - empty in this row
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println(mnum1);                                // Member Number
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println(tmode);                                // T Mode
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        
                        //
                        //  Changed for pre-checkin feature support on 02-28-05 [Paul S]
                        //
                        out.println((show1 == 1) ? "Yes" : "No");         // Checked In ?
                        out.println("</font></td></tr>");       // end of row

                        //
                        //  Now see if there are any guests for this member in the tee sheet
                        //
                        if (courseName1.equals( "-ALL-" )) {

                           stringTee4 = "SELECT " +
                                    "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                                    "show1, show2, show3, show4, " +
                                    "player5, p5cw, show5, userg1, userg2, userg3, userg4, userg5, " +
                                    "p91, p92, p93, p94, p95 " +
                                    "FROM teecurr2 WHERE date = ? AND " +
                                    "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)";
                        } else {
                           stringTee4 = "SELECT " +
                                    "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                                    "show1, show2, show3, show4, " +
                                    "player5, p5cw, show5, userg1, userg2, userg3, userg4, userg5, " +
                                    "p91, p92, p93, p94, p95 " +
                                    "FROM teecurr2 WHERE date = ? AND " +
                                    "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) " +
                                    "AND courseName = ?";
                        }
                        PreparedStatement pstmt2 = con.prepareStatement (stringTee4);

                        pstmt2.clearParameters();        // clear the parms
                        pstmt2.setLong(1, date);         // put the parm in pstmt2
                        pstmt2.setString(2, userT);
                        pstmt2.setString(3, userT);
                        pstmt2.setString(4, userT);
                        pstmt2.setString(5, userT);
                        pstmt2.setString(6, userT);

                        if (!courseName1.equals( "-ALL-" )) {
                           pstmt2.setString(7, courseName1);
                        }
                        rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                        while (rs2.next()) {

                           player1 = rs2.getString(1);
                           player2 = rs2.getString(2);
                           player3 = rs2.getString(3);
                           player4 = rs2.getString(4);
                           p1cw = rs2.getString(5);
                           p2cw = rs2.getString(6);
                           p3cw = rs2.getString(7);
                           p4cw = rs2.getString(8);
                           show1 = rs2.getShort(9);
                           show2 = rs2.getShort(10);
                           show3 = rs2.getShort(11);
                           show4 = rs2.getShort(12);
                           player5 = rs2.getString(13);
                           p5cw = rs2.getString(14);
                           show5 = rs2.getShort(15);
                           user1 = rs2.getString(16);
                           user2 = rs2.getString(17);
                           user3 = rs2.getString(18);
                           user4 = rs2.getString(19);
                           user5 = rs2.getString(20);
                           p91 = rs2.getInt(21);
                           p92 = rs2.getInt(22);
                           p93 = rs2.getInt(23);
                           p94 = rs2.getInt(24);
                           p95 = rs2.getInt(25);

                           if (p91 == 1) {          // if 9 hole round
                              p1cw = p1cw + "9";
                           }
                           if (p92 == 1) {
                              p2cw = p2cw + "9";
                           }
                           if (p93 == 1) {
                              p3cw = p3cw + "9";
                           }
                           if (p94 == 1) {
                              p4cw = p4cw + "9";
                           }
                           if (p95 == 1) {
                              p5cw = p5cw + "9";
                           }

                           //
                           //  Add a row for each guest of this member
                           //
                           if (user1.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                              out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println("''");                               // Member name - blank for guests
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(player1);                                // Guest
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(mnum1);                                // Member Number
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(p1cw);                                // T Mode
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println((show1 == 1) ? "Yes" : "No");         // Checked In ?
                              out.println("</font></td></tr>");       // end of row
                           }

                           if (user2.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                              out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println("''");                               // Member name - blank for guests
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(player2);                                // Guest
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(mnum1);                                // Member Number
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(p2cw);                                // T Mode
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println((show2 == 1) ? "Yes" : "No");         // Checked In ?
                              out.println("</font></td></tr>");       // end of row
                           }

                           if (user3.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                              out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println("''");                               // Member name - blank for guests
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(player3);                                // Guest
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(mnum1);                                // Member Number
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(p3cw);                                // T Mode
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println((show3 == 1) ? "Yes" : "No");         // Checked In ?
                              out.println("</font></td></tr>");       // end of row
                           }

                           if (user4.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                              out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println("''");                               // Member name - blank for guests
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(player4);                                // Guest
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(mnum1);                                // Member Number
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(p4cw);                                // T Mode
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println((show4 == 1) ? "Yes" : "No");         // Checked In ?
                              out.println("</font></td></tr>");       // end of row
                           }

                           if (user5.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                              out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println("''");                               // Member name - blank for guests
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(player5);                                // Guest
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(mnum1);                                // Member Number
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println(p5cw);                                // T Mode
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"" +fsz+ "\">");
                              out.println((show5 == 1) ? "Yes" : "No");         // Checked In ?
                              out.println("</font></td></tr>");       // end of row
                           }
                        }               // end of WHILE guests
                        pstmt2.close();

                     }               // end of WHILE members
                     pstmt.close();
                  }
               }         // end of FOR loop for Blackhawk

            } else {   // NOT Blackhawk - do normal lists

               if (order.equals( "trans" )) {

                  pstmt = con.prepareStatement (
                     "SELECT lname, fname, mi, username, tmode, mNum, checkIn, course, time " +
                     "FROM teereport4 " +
                     "ORDER BY tmode, lname, fname, mi");

               } else {   // order by member only

                  pstmt = con.prepareStatement (
                     "SELECT lname, fname, mi, username, tmode, mNum, checkIn, course, time " +
                     "FROM teereport4 " +
                     "ORDER BY lname, fname, mi");
               }

               pstmt.clearParameters();        // clear the parms
               rs = pstmt.executeQuery();      // execute the prepared pstmt

               while (rs.next()) {

                  lname = rs.getString(1);
                  fname = rs.getString(2);
                  mi = rs.getString(3);
                  userT = rs.getString(4);
                  tmode = rs.getString(5);
                  mnum1 = rs.getString(6);
                  show1 = rs.getShort(7);
                  courseNameT = rs.getString(8);
                  time = rs.getInt(9);

                  StringBuffer mem_name = new StringBuffer(lname);  // get last name

                  mem_name.append(", ");
                  mem_name.append(fname);
                  if (!mi.equals( "" )) {
                     mem_name.append(" ");
                     mem_name.append(mi);
                  }
                  lname = mem_name.toString();                      // convert to one string

                  //
                  //  Determine color to use in table - alternate for each new mode of trans
                  //
                  if (order.equals( "trans" )) {

                     currCW = tmode;                         // get this mode of trans

                     if (currCW.endsWith( "9" )) {            // if 9-hole CW

                        currCW = strip9( currCW );            // remove the '9'
                     }

                     if (lastCW.equals( "" )) {     // if first time

                        lastCW = currCW;             // set this as current, use current color

                     } else {

                        if (!currCW.equals( lastCW )) {   // if tmode has changed

                           lastCW = currCW;               // set this as current and switch color

                           if (rowColor.equals( "#F5F5DC" )) {

                              rowColor = "#CDCDB4";         // switch

                           } else {

                              rowColor = "#F5F5DC";         // switch
                           }
                        }
                     }

                  } else {   // change per member

                     if (rowColor.equals( "#F5F5DC" )) {

                        rowColor = "#CDCDB4";         // switch

                     } else {

                        rowColor = "#F5F5DC";         // switch
                     }

                  }

                  out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"left\">");
                  out.println("<font size=\"" +fsz+ "\">");
                  out.println(lname);                               // Member name
                  out.println("</font></td>");

                  if (report_type.equals( "alphat" )) {          // Members & Tee Times report?

                     int temp1 = time / 100;             // get hour
                     int temp2 = time - (temp1 * 100);   // get min
                       
                     String timeString = "";

                     if (temp1 == 12) {

                        timeString = " PM";
                          
                     } else {
                       
                        if (temp1 > 12) {
                          
                           temp1 -= 12;
                           timeString = " PM";
                             
                        } else {
                          
                           timeString = " AM";
                        }
                     }

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     if (temp2 < 10) {
                        out.println(temp1+ ":0" +temp2+ timeString);        // Time
                     } else {
                        out.println(temp1+ ":" +temp2+ timeString);        // Time
                     }
                     out.println("</font></td>");

                     if (!courseNameT.equals( "" )) {

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"" +fsz+ "\">");
                        out.println( courseNameT );                           // Course
                        out.println("</font></td></tr>");    
                     }

                  } else {

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("n/a");                                // Guest - empty in this row
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(mnum1);                                // Member Number
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(tmode);                                // T Mode
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println((show1 == 1) ? "Yes" : "No");         // Checked In ?
                     out.println("</font></td></tr>");       // end of row

                     //
                     //  Now see if there are any guests for this member in the tee sheet
                     //
                     if (courseName1.equals( "-ALL-" )) {

                        stringTee4 = "SELECT " +
                                 "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                                 "show1, show2, show3, show4, " +
                                 "player5, p5cw, show5, userg1, userg2, userg3, userg4, userg5, " +
                                 "p91, p92, p93, p94, p95 " +
                                 "FROM teecurr2 WHERE date = ? AND " +
                                 "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)";
                     } else {
                        stringTee4 = "SELECT " +
                                 "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                                 "show1, show2, show3, show4, " +
                                 "player5, p5cw, show5, userg1, userg2, userg3, userg4, userg5, " +
                                 "p91, p92, p93, p94, p95 " +
                                 "FROM teecurr2 WHERE date = ? AND " +
                                 "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) " +
                                 "AND courseName = ?";
                     }
                     PreparedStatement pstmt2 = con.prepareStatement (stringTee4);

                     pstmt2.clearParameters();        // clear the parms
                     pstmt2.setLong(1, date);         // put the parm in pstmt2
                     pstmt2.setString(2, userT);
                     pstmt2.setString(3, userT);
                     pstmt2.setString(4, userT);
                     pstmt2.setString(5, userT);
                     pstmt2.setString(6, userT);

                     if (!courseName1.equals( "-ALL-" )) {
                        pstmt2.setString(7, courseName1);
                     }
                     rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                     while (rs2.next()) {

                        player1 = rs2.getString(1);
                        player2 = rs2.getString(2);
                        player3 = rs2.getString(3);
                        player4 = rs2.getString(4);
                        p1cw = rs2.getString(5);
                        p2cw = rs2.getString(6);
                        p3cw = rs2.getString(7);
                        p4cw = rs2.getString(8);
                        show1 = rs2.getShort(9);
                        show2 = rs2.getShort(10);
                        show3 = rs2.getShort(11);
                        show4 = rs2.getShort(12);
                        player5 = rs2.getString(13);
                        p5cw = rs2.getString(14);
                        show5 = rs2.getShort(15);
                        user1 = rs2.getString(16);
                        user2 = rs2.getString(17);
                        user3 = rs2.getString(18);
                        user4 = rs2.getString(19);
                        user5 = rs2.getString(20);
                        p91 = rs2.getInt(21);
                        p92 = rs2.getInt(22);
                        p93 = rs2.getInt(23);
                        p94 = rs2.getInt(24);
                        p95 = rs2.getInt(25);

                        if (p91 == 1) {          // if 9 hole round
                           p1cw = p1cw + "9";
                        }
                        if (p92 == 1) {
                           p2cw = p2cw + "9";
                        }
                        if (p93 == 1) {
                           p3cw = p3cw + "9";
                        }
                        if (p94 == 1) {
                           p4cw = p4cw + "9";
                        }
                        if (p95 == 1) {
                           p5cw = p5cw + "9";
                        }

                        //
                        //  Add a row for each guest of this member
                        //
                        if (user1.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player1);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p1cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show1 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td></tr>");       // end of row
                        }

                        if (user2.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player2);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p2cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show2 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td></tr>");       // end of row
                        }

                        if (user3.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player3);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p3cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show3 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td></tr>");       // end of row
                        }

                        if (user4.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player4);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p4cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show4 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td></tr>");       // end of row
                        }

                        if (user5.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player5);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p5cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show5 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td></tr>");       // end of row
                        }
                     }               // end of WHILE guests
                     pstmt2.close();
                       
                  }                  // end of IF alpha (not alphat)

               }               // end of WHILE members
               pstmt.close();
            }                    // end of IF Blackhawk

            //
            //  Delete the entries from the temporary table
            //
            pstmt = con.prepareStatement (
               "DELETE FROM teereport4");

            pstmt.clearParameters();        // clear the parms
            pstmt.executeUpdate();          // execute the prepared pstmt

            pstmt.close();

            if (report_type.equals( "alpha" )) {

               //
               //  Now check for Unaccompanied Guests (first, list those that have a member sponsor)
               //
               boolean foundSome = false;

               if (courseName1.equals( "-ALL-" ) || courseName1.equals( "" )) {

                  stringTee4 = "SELECT " +
                           "userg1, userg2, userg3, userg4, userg5 " +
                           "FROM teecurr2 WHERE date = ? " +
                           "AND (username1 = ? AND username2 = ? AND username3 = ? AND username4 = ? AND username5 = ?) " +
                           "AND (userg1 != ? OR userg2 != ? OR userg3 != ? OR userg4 != ? OR userg5 != ?)";
               } else {
                  stringTee4 = "SELECT " +
                           "userg1, userg2, userg3, userg4, userg5 " +
                           "FROM teecurr2 WHERE date = ? " +
                           "AND (username1 = ? AND username2 = ? AND username3 = ? AND username4 = ? AND username5 = ?) " +
                           "AND (userg1 != ? OR userg2 != ? OR userg3 != ? OR userg4 != ? OR userg5 != ?) " +
                           "AND courseName = ?";
               }
               pstmt = con.prepareStatement (stringTee4);

               pstmt.clearParameters();        // clear the parms
               pstmt.setLong(1, date);         // put the parm in pstmt
               pstmt.setString(2, omit);
               pstmt.setString(3, omit);
               pstmt.setString(4, omit);
               pstmt.setString(5, omit);
               pstmt.setString(6, omit);
               pstmt.setString(7, omit);
               pstmt.setString(8, omit);
               pstmt.setString(9, omit);
               pstmt.setString(10, omit);
               pstmt.setString(11, omit);

               if (!courseName1.equals( "-ALL-" ) && !courseName1.equals( "" )) {
                  pstmt.setString(12, courseName1);
               }
               rs = pstmt.executeQuery();      // execute the prepared stmt

               while (rs.next()) {

                  userg1 = rs.getString(1);
                  userg2 = rs.getString(2);
                  userg3 = rs.getString(3);
                  userg4 = rs.getString(4);
                  userg5 = rs.getString(5);

                  //
                  //  Save each member's info for later
                  //
                  if (!userg1.equals( "" )) {

                     lname = "";
                     fname = "";
                     mi = "";

                     pstmta1 = con.prepareStatement (
                              "SELECT lname " +
                              "FROM teereport4 WHERE username = ?");

                     pstmta1.clearParameters();        // clear the parms
                     pstmta1.setString(1, userg1);
                     rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        lname = rs2.getString(1);
                     }
                     pstmta1.close();                  // close the stmt

                     if (lname.equals( "" )) {        // if user not already saved

                        pstmta1 = con.prepareStatement (
                                 "SELECT name_last, name_first, name_mi, memNum " +
                                 "FROM member2b WHERE username = ?");

                        pstmta1.clearParameters();        // clear the parms
                        pstmta1.setString(1, userg1);
                        rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           lname = rs2.getString(1);
                           fname = rs2.getString(2);
                           mi = rs2.getString(3);
                           mnum1 = rs2.getString(4);
                        }
                        pstmta1.close();                  // close the stmt

                        if (!lname.equals( "" )) {       // if name found

                           //
                           // save info in teereport4 (temporary)
                           //
                           pstmta1 = con.prepareStatement (
                             "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time) " +
                             "VALUES (?,?,?,?,'',?,0,'',0)");

                           pstmta1.clearParameters();        // clear the parms
                           pstmta1.setString(1, lname);
                           pstmta1.setString(2, fname);
                           pstmta1.setString(3, mi);
                           pstmta1.setString(4, userg1);
                           pstmta1.setString(5, mnum1);
                           pstmta1.executeUpdate();          // execute the prepared stmt

                           pstmta1.close();   // close the stmt

                           foundSome = true;                  // found at least one
                        }
                     }
                  }      // end of if userg

                  if (!userg2.equals( "" ) && !userg2.equalsIgnoreCase( userg1 )) {

                     lname = "";
                     fname = "";
                     mi = "";

                     pstmta1 = con.prepareStatement (
                              "SELECT lname " +
                              "FROM teereport4 WHERE username = ?");

                     pstmta1.clearParameters();        // clear the parms
                     pstmta1.setString(1, userg2);
                     rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        lname = rs2.getString(1);
                     }
                     pstmta1.close();                  // close the stmt

                     if (lname.equals( "" )) {        // if user not already saved

                        pstmta1 = con.prepareStatement (
                                 "SELECT name_last, name_first, name_mi, memNum " +
                                 "FROM member2b WHERE username = ?");

                        pstmta1.clearParameters();        // clear the parms
                        pstmta1.setString(1, userg2);
                        rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           lname = rs2.getString(1);
                           fname = rs2.getString(2);
                           mi = rs2.getString(3);
                           mnum2 = rs2.getString(4);
                        }
                        pstmta1.close();                  // close the stmt

                        if (!lname.equals( "" )) {       // if name found

                           //
                           // save info in teereport4 (temporary)
                           //
                           pstmta1 = con.prepareStatement (
                             "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time) " +
                             "VALUES (?,?,?,?,'',?,0,'',0)");

                           pstmta1.clearParameters();        // clear the parms
                           pstmta1.setString(1, lname);
                           pstmta1.setString(2, fname);
                           pstmta1.setString(3, mi);
                           pstmta1.setString(4, userg2);
                           pstmta1.setString(5, mnum2);
                           pstmta1.executeUpdate();          // execute the prepared stmt

                           pstmta1.close();   // close the stmt

                           foundSome = true;                  // found at least one
                        }
                     }
                  }      // end of if userg

                  if (!userg3.equals( "" ) && !userg3.equalsIgnoreCase( userg1 ) && !userg3.equalsIgnoreCase( userg2 )) {

                     lname = "";
                     fname = "";
                     mi = "";

                     pstmta1 = con.prepareStatement (
                              "SELECT lname " +
                              "FROM teereport4 WHERE username = ?");

                     pstmta1.clearParameters();        // clear the parms
                     pstmta1.setString(1, userg3);
                     rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        lname = rs2.getString(1);
                     }
                     pstmta1.close();                  // close the stmt

                     if (lname.equals( "" )) {        // if user not already saved

                        pstmta1 = con.prepareStatement (
                                 "SELECT name_last, name_first, name_mi, memNum " +
                                 "FROM member2b WHERE username = ?");

                        pstmta1.clearParameters();        // clear the parms
                        pstmta1.setString(1, userg3);
                        rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           lname = rs2.getString(1);
                           fname = rs2.getString(2);
                           mi = rs2.getString(3);
                           mnum3 = rs2.getString(4);
                        }
                        pstmta1.close();                  // close the stmt

                        if (!lname.equals( "" )) {       // if name found

                           //
                           // save info in teereport4 (temporary)
                           //
                           pstmta1 = con.prepareStatement (
                             "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time) " +
                             "VALUES (?,?,?,?,'',?,0,'',0)");

                           pstmta1.clearParameters();        // clear the parms
                           pstmta1.setString(1, lname);
                           pstmta1.setString(2, fname);
                           pstmta1.setString(3, mi);
                           pstmta1.setString(4, userg3);
                           pstmta1.setString(5, mnum3);
                           pstmta1.executeUpdate();          // execute the prepared stmt

                           pstmta1.close();   // close the stmt

                           foundSome = true;                  // found at least one
                        }
                     }
                  }      // end of if userg

                  if (!userg4.equals( "" ) && !userg4.equalsIgnoreCase( userg1 ) && !userg4.equalsIgnoreCase( userg2 ) &&
                      !userg4.equalsIgnoreCase( userg3 )) {

                     lname = "";
                     fname = "";
                     mi = "";

                     pstmta1 = con.prepareStatement (
                              "SELECT lname " +
                              "FROM teereport4 WHERE username = ?");

                     pstmta1.clearParameters();        // clear the parms
                     pstmta1.setString(1, userg4);
                     rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        lname = rs2.getString(1);
                     }
                     pstmta1.close();                  // close the stmt

                     if (lname.equals( "" )) {        // if user not already saved

                        pstmta1 = con.prepareStatement (
                                 "SELECT name_last, name_first, name_mi, memNum " +
                                 "FROM member2b WHERE username = ?");

                        pstmta1.clearParameters();        // clear the parms
                        pstmta1.setString(1, userg4);
                        rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           lname = rs2.getString(1);
                           fname = rs2.getString(2);
                           mi = rs2.getString(3);
                           mnum4 = rs2.getString(4);
                        }
                        pstmta1.close();                  // close the stmt

                        if (!lname.equals( "" )) {       // if name found

                           //
                           // save info in teereport4 (temporary)
                           //
                           pstmta1 = con.prepareStatement (
                             "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time) " +
                             "VALUES (?,?,?,?,'',?,0,'',0)");

                           pstmta1.clearParameters();        // clear the parms
                           pstmta1.setString(1, lname);
                           pstmta1.setString(2, fname);
                           pstmta1.setString(3, mi);
                           pstmta1.setString(4, userg4);
                           pstmta1.setString(5, mnum4);
                           pstmta1.executeUpdate();          // execute the prepared stmt

                           pstmta1.close();   // close the stmt

                           foundSome = true;                  // found at least one
                        }
                     }
                  }      // end of if userg

                  if (!userg5.equals( "" ) && !userg5.equalsIgnoreCase( userg1 ) && !userg5.equalsIgnoreCase( userg2 ) &&
                      !userg5.equalsIgnoreCase( userg3 ) && !userg5.equalsIgnoreCase( userg4 )) {

                     lname = "";
                     fname = "";
                     mi = "";

                     pstmta1 = con.prepareStatement (
                              "SELECT lname " +
                              "FROM teereport4 WHERE username = ?");

                     pstmta1.clearParameters();        // clear the parms
                     pstmta1.setString(1, userg5);
                     rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        lname = rs2.getString(1);
                     }
                     pstmta1.close();                  // close the stmt

                     if (lname.equals( "" )) {        // if user not already saved

                        pstmta1 = con.prepareStatement (
                                 "SELECT name_last, name_first, name_mi, memNum " +
                                 "FROM member2b WHERE username = ?");

                        pstmta1.clearParameters();        // clear the parms
                        pstmta1.setString(1, userg5);
                        rs2 = pstmta1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           lname = rs2.getString(1);
                           fname = rs2.getString(2);
                           mi = rs2.getString(3);
                           mnum5 = rs2.getString(4);
                        }
                        pstmta1.close();                  // close the stmt

                        if (!lname.equals( "" )) {       // if name found

                           //
                           // save info in teereport4 (temporary)
                           //
                           pstmta1 = con.prepareStatement (
                             "INSERT INTO teereport4 (lname, fname, mi, username, tmode, mNum, checkIn, course, time) " +
                             "VALUES (?,?,?,?,'',?,0,'',0)");

                           pstmta1.clearParameters();        // clear the parms
                           pstmta1.setString(1, lname);
                           pstmta1.setString(2, fname);
                           pstmta1.setString(3, mi);
                           pstmta1.setString(4, userg5);
                           pstmta1.setString(5, mnum5);
                           pstmta1.executeUpdate();          // execute the prepared stmt

                           pstmta1.close();   // close the stmt

                           foundSome = true;                  // found at least one
                        }
                     }
                  }      // end of if user

               }      // end of WHILE

               pstmt.close();                  // close the stmt

               if (foundSome == true) {            // if we found some unaccompanied guest times above

                  //
                  //  add a header row
                  //
                  out.println("<tr bgcolor=\"#336633\"><td align=\"center\" colspan=\"5\">");
                  out.println("<font color=\"#FFFFFF\" size=\"" +fsz+ "\">");
                  out.println("Unaccompanied Guests");
                  out.println("</font></td></tr>");       // end of row


                  //
                  //  All members are now saved in teereport4 - list them and their guests alphabetically by mode of trans
                  //
                  userT = "";

                  pstmt = con.prepareStatement (
                     "SELECT lname, fname, mi, username, mNum " +
                     "FROM teereport4 " +
                     "ORDER BY lname, fname, mi");

                  pstmt.clearParameters();        // clear the parms
                  rs = pstmt.executeQuery();      // execute the prepared pstmt

                  while (rs.next()) {

                     lname = rs.getString(1);
                     fname = rs.getString(2);
                     mi = rs.getString(3);
                     userT = rs.getString(4);
                     mnum1 = rs.getString(5);

                     StringBuffer mem_name = new StringBuffer(lname);  // get last name

                     mem_name.append(", ");
                     mem_name.append(fname);
                     if (!mi.equals( "" )) {
                        mem_name.append(" ");
                        mem_name.append(mi);
                     }
                     lname = mem_name.toString();                      // convert to one string

                     //
                     //  Determine color to use in table - alternate
                     //
                     if (rowColor.equals( "#F5F5DC" )) {

                        rowColor = "#CDCDB4";         // switch

                     } else {

                        rowColor = "#F5F5DC";         // switch
                     }

                     out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"left\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(lname);                               // Member name
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("n/a");                                // Guest - empty in this row
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(mnum1);                                // Member Number
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                                // T Mode
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");
                     out.println("</font></td></tr>");       // end of row

                     //
                     //  Now see if there are any guests for this member in the tee sheet
                     //
                     if (courseName1.equals( "-ALL-" )) {

                        stringTee4 = "SELECT " +
                                 "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                                 "show1, show2, show3, show4, " +
                                 "player5, p5cw, show5, userg1, userg2, userg3, userg4, userg5, " +
                                 "p91, p92, p93, p94, p95 " +
                                 "FROM teecurr2 WHERE date = ? AND " +
                                 "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)";
                     } else {
                        stringTee4 = "SELECT " +
                                 "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                                 "show1, show2, show3, show4, " +
                                 "player5, p5cw, show5, userg1, userg2, userg3, userg4, userg5, " +
                                 "p91, p92, p93, p94, p95 " +
                                 "FROM teecurr2 WHERE date = ? AND " +
                                 "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) " +
                                 "AND courseName = ?";
                     }
                     PreparedStatement pstmt2 = con.prepareStatement (stringTee4);

                     pstmt2.clearParameters();        // clear the parms
                     pstmt2.setLong(1, date);         // put the parm in pstmt2
                     pstmt2.setString(2, userT);
                     pstmt2.setString(3, userT);
                     pstmt2.setString(4, userT);
                     pstmt2.setString(5, userT);
                     pstmt2.setString(6, userT);

                     if (!courseName1.equals( "-ALL-" )) {
                        pstmt2.setString(7, courseName1);
                     }
                     rs2 = pstmt2.executeQuery();      // execute the prepared stmt

                     while (rs2.next()) {

                        player1 = rs2.getString(1);
                        player2 = rs2.getString(2);
                        player3 = rs2.getString(3);
                        player4 = rs2.getString(4);
                        p1cw = rs2.getString(5);
                        p2cw = rs2.getString(6);
                        p3cw = rs2.getString(7);
                        p4cw = rs2.getString(8);
                        show1 = rs2.getShort(9);
                        show2 = rs2.getShort(10);
                        show3 = rs2.getShort(11);
                        show4 = rs2.getShort(12);
                        player5 = rs2.getString(13);
                        p5cw = rs2.getString(14);
                        show5 = rs2.getShort(15);
                        user1 = rs2.getString(16);
                        user2 = rs2.getString(17);
                        user3 = rs2.getString(18);
                        user4 = rs2.getString(19);
                        user5 = rs2.getString(20);
                        p91 = rs2.getInt(21);
                        p92 = rs2.getInt(22);
                        p93 = rs2.getInt(23);
                        p94 = rs2.getInt(24);
                        p95 = rs2.getInt(25);

                        if (p91 == 1) {          // if 9 hole round
                           p1cw = p1cw + "9";
                        }
                        if (p92 == 1) {
                           p2cw = p2cw + "9";
                        }
                        if (p93 == 1) {
                           p3cw = p3cw + "9";
                        }
                        if (p94 == 1) {
                           p4cw = p4cw + "9";
                        }
                        if (p95 == 1) {
                           p5cw = p5cw + "9";
                        }

                        //
                        //  Add a row for each guest of this member
                        //
                        if (user1.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player1);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p1cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show1 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td></tr>");       // end of row
                        }

                        if (user2.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player2);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p2cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show2 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td></tr>");       // end of row
                        }

                        if (user3.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player3);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p3cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show3 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td></tr>");       // end of row
                        }

                        if (user4.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player4);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p4cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show4 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td></tr>");       // end of row
                        }

                        if (user5.equalsIgnoreCase( userT )) {        // if this is a guest of the member from above

                           out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println("''");                               // Member name - blank for guests
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(player5);                                // Guest
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(mnum1);                                // Member Number
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println(p5cw);                                // T Mode
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"" +fsz+ "\">");
                           out.println((show5 == 1) ? "Yes" : "No");         // Checked In ?
                           out.println("</font></td></tr>");       // end of row
                        }
                     }               // end of WHILE guests
                     pstmt2.close();

                  }               // end of WHILE members
                  pstmt.close();

               }                  // end of IF foundSome

               //
               //  Delete the entries from the temporary table
               //
               pstmt = con.prepareStatement (
                  "DELETE FROM teereport4");

               pstmt.clearParameters();        // clear the parms
               pstmt.executeUpdate();          // execute the prepared pstmt

               pstmt.close();


               //
               //  Now check for Unaccompanied Guests that have no sponsor member
               //
               foundSome = false;        // init flag

               if (courseName1.equals( "-ALL-" ) || courseName1.equals( "" )) {

                  stringTee4 = "SELECT " +
                           "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                           "show1, show2, show3, show4, player5, p5cw, show5, " +
                           "p91, p92, p93, p94, p95 " +
                           "FROM teecurr2 WHERE date = ? " +
                           "AND (player1 != ? OR player2 != ? OR player3 != ? OR player4 != ? OR player5 != ?) " +
                           "AND (username1 = ? AND username2 = ? AND username3 = ? AND username4 = ? AND username5 = ?) " +
                           "AND (userg1 = ? AND userg2 = ? AND userg3 = ? AND userg4 = ? AND userg5 = ?)";
               } else {
                  stringTee4 = "SELECT " +
                           "player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                           "show1, show2, show3, show4, player5, p5cw, show5, " +
                           "p91, p92, p93, p94, p95 " +
                           "FROM teecurr2 WHERE date = ? " +
                           "AND (player1 != ? OR player2 != ? OR player3 != ? OR player4 != ? OR player5 != ?) " +
                           "AND (username1 = ? AND username2 = ? AND username3 = ? AND username4 = ? AND username5 = ?) " +
                           "AND (userg1 = ? AND userg2 = ? AND userg3 = ? AND userg4 = ? AND userg5 = ?) " +
                           "AND courseName = ?";
               }
               pstmt = con.prepareStatement (stringTee4);

               pstmt.clearParameters();        // clear the parms
               pstmt.setLong(1, date);         // put the parm in pstmt
               pstmt.setString(2, omit);
               pstmt.setString(3, omit);
               pstmt.setString(4, omit);
               pstmt.setString(5, omit);
               pstmt.setString(6, omit);
               pstmt.setString(7, omit);
               pstmt.setString(8, omit);
               pstmt.setString(9, omit);
               pstmt.setString(10, omit);
               pstmt.setString(11, omit);
               pstmt.setString(12, omit);
               pstmt.setString(13, omit);
               pstmt.setString(14, omit);
               pstmt.setString(15, omit);
               pstmt.setString(16, omit);

               if (!courseName1.equals( "-ALL-" ) && !courseName1.equals( "" )) {
                  pstmt.setString(17, courseName1);
               }
               rs = pstmt.executeQuery();      // execute the prepared stmt

               while (rs.next()) {

                  player1 = rs.getString(1);
                  player2 = rs.getString(2);
                  player3 = rs.getString(3);
                  player4 = rs.getString(4);
                  p1cw = rs.getString(5);
                  p2cw = rs.getString(6);
                  p3cw = rs.getString(7);
                  p4cw = rs.getString(8);
                  show1 = rs.getShort(9);
                  show2 = rs.getShort(10);
                  show3 = rs.getShort(11);
                  show4 = rs.getShort(12);
                  player5 = rs.getString(13);
                  p5cw = rs.getString(14);
                  show5 = rs.getShort(15);
                  p91 = rs.getInt(16);
                  p92 = rs.getInt(17);
                  p93 = rs.getInt(18);
                  p94 = rs.getInt(19);
                  p95 = rs.getInt(20);

                  if (p91 == 1) {          // if 9 hole round
                     p1cw = p1cw + "9";
                  }
                  if (p92 == 1) {
                     p2cw = p2cw + "9";
                  }
                  if (p93 == 1) {
                     p3cw = p3cw + "9";
                  }
                  if (p94 == 1) {
                     p4cw = p4cw + "9";
                  }
                  if (p95 == 1) {
                     p5cw = p5cw + "9";
                  }

                  if (foundSome == false) {            // if we haven't found some unaccompanied guest times above or here yet

                     //
                     //  add a header row
                     //
                     out.println("<tr bgcolor=\"#336633\"><td align=\"center\" colspan=\"5\">");
                     out.println("<font color=\"#FFFFFF\" size=\"" +fsz+ "\">");
                     out.println("Non-Sponsored Guests");
                     out.println("</font></td></tr>");       // end of row

                     foundSome = true;
                  }

                  //
                  //  Determine color to use in table - alternate
                  //
                  if (rowColor.equals( "#F5F5DC" )) {

                     rowColor = "#CDCDB4";         // switch

                  } else {

                     rowColor = "#F5F5DC";         // switch
                  }


                  //
                  //  Add a row for each unsponsored guest
                  //
                  if (!player1.equals( "" )) {        // if guest exists

                     out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                               // Member name - blank for guests
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(player1);                                // Guest
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                                // Member Number
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(p1cw);                                // T Mode
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println((show1 == 1) ? "Yes" : "No");         // Checked In ?
                     out.println("</font></td></tr>");       // end of row
                  }

                  if (!player2.equals( "" )) {

                     out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                               // Member name - blank for guests
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(player2);                                // Guest
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                                // Member Number
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(p2cw);                                // T Mode
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println((show2 == 1) ? "Yes" : "No");         // Checked In ?
                     out.println("</font></td></tr>");       // end of row
                  }

                  if (!player3.equals( "" )) {

                     out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                               // Member name - blank for guests
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(player3);                                // Guest
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                                // Member Number
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(p3cw);                                // T Mode
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println((show3 == 1) ? "Yes" : "No");         // Checked In ?
                     out.println("</font></td></tr>");       // end of row
                  }

                  if (!player4.equals( "" )) {

                     out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                               // Member name - blank for guests
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(player4);                                // Guest
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                                // Member Number
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(p4cw);                                // T Mode
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println((show4 == 1) ? "Yes" : "No");         // Checked In ?
                     out.println("</font></td></tr>");       // end of row
                  }

                  if (!player5.equals( "" )) {

                     out.println("<tr bgcolor=\"" +rowColor+ "\"><td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                               // Member name - blank for guests
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(player5);                                // Guest
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println("&nbsp;");                                // Member Number
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println(p5cw);                                // T Mode
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"" +fsz+ "\">");
                     out.println((show5 == 1) ? "Yes" : "No");         // Checked In ?
                     out.println("</font></td></tr>");       // end of row
                  }

               }               // end of WHILE unsponsored guest tee times
               pstmt.close();
                 
            }                  // end of if alpha (not alphat)

            out.println("</table>");                  // end of alphabetical list page

         }  // end of report type if

      }  // end of print if

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center><BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>" + e1.toString());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</BODY></HTML>");
      out.close();
      if (Gzip == true) {
         resp.setContentLength(buf.size());                 // set output length
         resp.getOutputStream().write(buf.toByteArray());
      }
      return;
   }

   //
   //  End of HTML page
   //
   out.println("</center></body></html>");
   out.close();
   if (Gzip == true) {
      resp.setContentLength(buf.size());                 // set output length
      resp.getOutputStream().write(buf.toByteArray());
   }

 }  // end of doPost


 // ********************************************************************
 //  Process the POS charges for this tee time or individual player (Pro-ShopKeeper)
 // ********************************************************************

 private boolean promptPOS(parmPOS parmp, HttpServletRequest req, HttpServletResponse resp, PrintWriter out, 
                           Connection con, boolean Gzip, ByteArrayOutputStream buf) 
                throws Exception, IOException {

   ResultSet rs = null;
   ResultSet rs2 = null;

   String player = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String player6 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String user6 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String p6cw = "";
   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String userg6 = "";
   String stime = "";
   String show = "";
   String sfb = "";
   String num = "";
   String j = "";
   String course = "";
   String courseOrig = "";

   int i = 0;
   int time = 0;
   int index = 0;
   int guest = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int p96 = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   int show6 = 0;
   int pos1 = 0;
   int pos2 = 0;
   int pos3 = 0;
   int pos4 = 0;
   int pos5 = 0;
   int pos6 = 0;
   int p = 0;

   short fb = 0;

   boolean charges = false;
   boolean noShow = false;
   boolean returnchg = false;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   //
   //  String and fields to provide the charge info to the POS
   //
   //    format = count, memid, date time, 9/18, lname, fname, *zip, *phone, *email, mship_class, charge_code
   //
   //                1 entry per 'count' (only 1 count field),  * = skip these fields
   //
   String sdate = "";         // date and time field for POS string


   //
   //  Get the parms
   //
   course = req.getParameter("course");      //  course name or null
   stime = req.getParameter("time");         //  time of the slot
   sfb = req.getParameter("fb");             //  front/back indicator
   j = req.getParameter("jump");             //  jump index value for where to jump to on the page
   num = req.getParameter("name");           //  index value for the day of this sheet

   if (req.getParameter("noShow") != null) {  // if single player check-in/out

      noShow = true;
      player = req.getParameter("noShow");      //  number of the player selected (1-4)
      show = req.getParameter("show");          //  current show value for player selected

      p = Integer.parseInt(player);
   }

   //
   //  Convert the common string values to int's
   //
   try {
      index = Integer.parseInt(num);
      time = Integer.parseInt(stime);
      fb = Short.parseShort(sfb);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  Get individual course if course=ALL
   //
   courseOrig = course;                     // save orig course
     
   if (course.equals( "-ALL-")) {

      if (req.getParameter("courseCheckIn") != null) {

         course = req.getParameter("courseCheckIn");      // get actual course name for check-in calls (in case course=ALL)
      }
   }


   //
   //   Get the club parms
   //
   getClub.getParms(con, parm);        // get the club parms

   //
   //  Get today's date and then use the value passed to locate the requested date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   cal.add(Calendar.DATE,index);                  // roll ahead 'index' days

   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);

   month = month + 1;                            // month starts at zero

   long date = year * 10000;                     // create a date field of yyyymmdd
   date = date + (month * 100);
   date = date + day;                            // date = yyyymmdd (for comparisons)

   int hr = time/100;
   int min = time - (hr * 100);

   sdate = month + "/" + day + "/" + year + " " + hr + ":" + SystemUtils.ensureDoubleDigit(min);  // create date and time string
   
   
   //
   //  Determine if there will be any charges for this request
   //
   try {

      PreparedStatement pstmt2s = con.prepareStatement (
         "SELECT * " +
         "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? and courseName = ?");

      pstmt2s.clearParameters();        // clear the parms
      pstmt2s.setLong(1, date);
      pstmt2s.setInt(2, time);
      pstmt2s.setShort(3, fb);
      pstmt2s.setString(4, course);

      rs = pstmt2s.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         user1 = rs.getString("username1");
         user2 = rs.getString("username2");
         user3 = rs.getString("username3");
         user4 = rs.getString("username4");
         p1cw = rs.getString("p1cw");
         p2cw = rs.getString("p2cw");
         p3cw = rs.getString("p3cw");
         p4cw = rs.getString("p4cw");
         show1 = rs.getInt("show1");
         show2 = rs.getInt("show2");
         show3 = rs.getInt("show3");
         show4 = rs.getInt("show4");
         player5 = rs.getString("player5");
         user5 = rs.getString("username5");
         p5cw = rs.getString("p5cw");
         show5 = rs.getInt("show5");
         mNum1 = rs.getString("mNum1");
         mNum2 = rs.getString("mNum2");
         mNum3 = rs.getString("mNum3");
         mNum4 = rs.getString("mNum4");
         mNum5 = rs.getString("mNum5");
         userg1 = rs.getString("userg1");
         userg2 = rs.getString("userg2");
         userg3 = rs.getString("userg3");
         userg4 = rs.getString("userg4");
         userg5 = rs.getString("userg5");
         p91 = rs.getInt("p91");
         p92 = rs.getInt("p92");
         p93 = rs.getInt("p93");
         p94 = rs.getInt("p94");
         p95 = rs.getInt("p95");
         pos1 = rs.getInt("pos1");
         pos2 = rs.getInt("pos2");
         pos3 = rs.getInt("pos3");
         pos4 = rs.getInt("pos4");
         pos5 = rs.getInt("pos5");
      }
      pstmt2s.close();

   }
   catch (Exception ignore) {
   }
     
   //
   //   Init fields in pos parm block
   //
   parmp.count = 0;       
   parmp.sdate = sdate;
   parmp.poslist = "";
     
   //
   //   if call was for Check All, then process all players in the specified slot
   //
   if (req.getParameter("checkAll") != null) {

      //
      //  Process one player at a time to determine any charges
      //
      if (!player1.equalsIgnoreCase( "x" ) && !player1.equals( "" )) {

         //
         //  Check if player name is member or guest
         //
         i = 0;
         guest = 0;
           
         if (user1.equals( "" )) {            // if no username for this player
           
            ploop1:
            while (i < parm.MAX_Guests) {
               if (player1.startsWith( parm.guest[i] )) {

                  guest = 1;       // indicate player1 is a guest name
                  break ploop1;
               }
               i++;
            }
         }
         parmp.pcw = p1cw;
         parmp.p9 = p91;
           
         if (guest == 0) {        // if member

            if (!user1.equals( "" ) && pos1 == 0) {      // skip if no user name found or already processed

               parmp.player = "";   // indicate member
               parmp.user = user1;

               buildCharge(parmp, con);
            }

         } else {          // else guest

            if (!userg1.equals( "" ) && pos1 ==0) {      // skip if no member associated with this guest

               parmp.player = player1;   // indicate guest - pass the guest type
               parmp.user = userg1;

               buildCharge(parmp, con);
            }
         }   // end of IF member or guest
      }      // end of IF player not X and not null

      if (!player2.equalsIgnoreCase( "x" ) && !player2.equals( "" )) {

         //
         //  Check if player name is member or guest
         //
         i = 0;
         guest = 0;
           
         if (user2.equals( "" )) {            // if no username for this player

            ploop2:
            while (i < parm.MAX_Guests) {
               if (player2.startsWith( parm.guest[i] )) {

                  guest = 1;       // indicate player2 is a guest name
                  break ploop2;
               }
               i++;
            }
         }
         parmp.pcw = p2cw;
         parmp.p9 = p92;

         if (guest == 0) {        // if member

            if (!user2.equals( "" ) && pos2 ==0) {      // skip if no user name found

               parmp.player = "";   // indicate member
               parmp.user = user2;

               buildCharge(parmp, con);
            }

         } else {          // else guest

            if (!userg2.equals( "" ) && pos2 ==0) {      // skip if no member associated with this guest

               parmp.player = player2;   // indicate guest - pass the guest type
               parmp.user = userg2;

               buildCharge(parmp, con);
            }
         }   // end of IF member or guest
      }      // end of IF player not X and not null

      if (!player3.equalsIgnoreCase( "x" ) && !player3.equals( "" )) {

         //
         //  Check if player name is member or guest
         //
         i = 0;
         guest = 0;

         if (user3.equals( "" )) {            // if no username for this player

            ploop3:
            while (i < parm.MAX_Guests) {
               if (player3.startsWith( parm.guest[i] )) {

                  guest = 1;       // indicate player3 is a guest name
                  break ploop3;
               }
               i++;
            }
         }
         parmp.pcw = p3cw;
         parmp.p9 = p93;

         if (guest == 0) {        // if member

            if (!user3.equals( "" ) && pos3 ==0) {      // skip if no user name found

               parmp.player = "";   // indicate member
               parmp.user = user3;

               buildCharge(parmp, con);
            }

         } else {          // else guest

            if (!userg3.equals( "" ) && pos3 ==0) {      // skip if no member associated with this guest

               parmp.player = player3;   // indicate guest - pass the guest type
               parmp.user = userg3;

               buildCharge(parmp, con);
            }
         }   // end of IF member or guest
      }      // end of IF player not X and not null

      if (!player4.equalsIgnoreCase( "x" ) && !player4.equals( "" )) {

         //
         //  Check if player name is member or guest
         //
         i = 0;
         guest = 0;

         if (user4.equals( "" )) {            // if no username for this player

            ploop4:
            while (i < parm.MAX_Guests) {
               if (player4.startsWith( parm.guest[i] )) {

                  guest = 1;       // indicate player4 is a guest name
                  break ploop4;
               }
               i++;
            }
         }
         parmp.pcw = p4cw;
         parmp.p9 = p94;

         if (guest == 0) {        // if member

            if (!user4.equals( "" ) && pos4 ==0) {      // skip if no user name found

               parmp.player = "";   // indicate member
               parmp.user = user4;

               buildCharge(parmp, con);
            }

         } else {          // else guest

            if (!userg4.equals( "" ) && pos4 ==0) {      // skip if no member associated with this guest

               parmp.player = player4;   // indicate guest - pass the guest type
               parmp.user = userg4;

               buildCharge(parmp, con);
            }
         }   // end of IF member or guest
      }      // end of IF player not X and not null

      if (!player5.equalsIgnoreCase( "x" ) && !player5.equals( "" )) {

         //
         //  Check if player name is member or guest
         //
         i = 0;
         guest = 0;

         if (user5.equals( "" )) {            // if no username for this player

            ploop5:
            while (i < parm.MAX_Guests) {
               if (player5.startsWith( parm.guest[i] )) {

                  guest = 1;       // indicate player5 is a guest name
                  break ploop5;
               }
               i++;
            }
         }
         parmp.pcw = p5cw;
         parmp.p9 = p95;

         if (guest == 0) {        // if member

            if (!user5.equals( "" ) && pos5 ==0) {      // skip if no user name found

               parmp.player = "";   // indicate member
               parmp.user = user5;

               buildCharge(parmp, con);
            }

         } else {          // else guest

            if (!userg5.equals( "" ) && pos5 ==0) {      // skip if no member associated with this guest

               parmp.player = player5;   // indicate guest - pass the guest type
               parmp.user = userg5;

               buildCharge(parmp, con);
            }
         }   // end of IF member or guest
      }      // end of IF player not X and not null

   } else {        // this was an individual check-in/out

      //
      //  Determine which player is to be processed
      //
      if (p == 1) {              // get player based on player #
         player6 = player1;
         user6 = user1;
         userg6 = userg1;
         p6cw = p1cw;
         p96 = p91;
         pos6 = pos1;
         show6 = show1;
      }
      if (p == 2) {
         player6 = player2;
         user6 = user2;
         userg6 = userg2;
         p6cw = p2cw;
         p96 = p92;
         pos6 = pos2;
         show6 = show2;
      }
      if (p == 3) {
         player6 = player3;
         user6 = user3;
         userg6 = userg3;
         p6cw = p3cw;
         p96 = p93;
         pos6 = pos3;
         show6 = show3;
      }
      if (p == 4) {
         player6 = player4;
         user6 = user4;
         userg6 = userg4;
         p6cw = p4cw;
         p96 = p94;
         pos6 = pos4;
         show6 = show4;
      }
      if (p == 5) {
         player6 = player5;
         user6 = user5;
         userg6 = userg5;
         p6cw = p5cw;
         p96 = p95;
         pos6 = pos5;
         show6 = show5;
      }

      //
      //  Make sure we should process the charge/return.
      //  User may have done a check-in/out without doing the charge prior to this.
      //  If so, the show & pos flags will be out of sync.  Only do charge if in sync. 
      //
      if ((show6 == 0 && pos6 == 0) || (show6 == 1 && pos6 == 1)) { 

         //
         //  Request to check-in or check-out a single player
         //
         if (pos6 == 1) {

            returnchg = true;      // this is a return (credit)
         }

         if (!player6.equalsIgnoreCase( "x" ) && !player6.equals( "" )) {

            //
            //  Check if player name is member or guest
            //
            i = 0;
            guest = 0;

            if (user6.equals( "" )) {            // if no username for this player

               ploop6:
               while (i < parm.MAX_Guests) {
                  if (player6.startsWith( parm.guest[i] )) {

                     guest = 1;       // indicate player is a guest name
                     break ploop6;
                  }
                  i++;
               }
            }
            parmp.pcw = p6cw;
            parmp.p9 = p96;

            if (guest == 0) {        // if member

               if (!user6.equals( "" )) {      // skip if no user name found

                  parmp.player = "";   // indicate member
                  parmp.user = user6;

                  buildCharge(parmp, con);
               }

            } else {          // else guest

               if (!userg6.equals( "" )) {      // skip if no member associated with this guest

                  parmp.player = player6;   // indicate guest - pass the guest type
                  parmp.user = userg6;

                  buildCharge(parmp, con);
               }
            }   // end of IF member or guest
              
            //
            //  if charge/credit was built, toggle the pos indicator for this player
            //
            if (parmp.count > 0) {
              
               if (pos6 == 0) {
                  pos6 = 1;
               } else {
                  pos6 = 0;
               }
            }
         }      // end of IF player not X and not null
      }      // end of IF we should do a pos charge/return
   }

   if (parmp.count > 0) {        // if charges found

      charges = true;        // indicate charges to process

      String counts = String.valueOf( parmp.count );     // create string value from count

      if (returnchg == true) {         // credit ??

         parmp.poslist = "-" + counts + "," + parmp.poslist;   // create credit string

      } else {

         parmp.poslist = counts + "," + parmp.poslist;   // create full charge string
      }

      parmp.poslist = "ChangeStatus(" + parmp.poslist + ")";  // wrap pos list with command

      //
      //  Output a prompt to see how pro wants to proceed
      //
      out.println(SystemUtils.HeadTitle2("Proshop POS Page"));

      //
      //*******************************************************************
      //  Send Status to Process POS Charge
      //       The window.status will generate a StatusTextChange browser control.
      //       Do this once to send the pos charge, then again to clear it.
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Erase name script
      out.println("<!--");

      out.println("function sendstatus(list) {");

      out.println("window.status = list;");
      out.println("window.status = 'Transaction Complete';");
      out.println("return true;");

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      out.println("</head><BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>POS Transaction Request</H3><BR>");
      out.println("Member charges for this tee time are about to be transferred to the POS system.");
      out.println("<BR><BR>Would you like to proceed with these charges?");
      out.println("<BR><b>NOTE:</b> This must be done from the browser window within the Pro-ShopKeeper program.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");

      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet\">");
      out.println("<input type=\"hidden\" name=\"POScontinue\" value=\"yes\">");   // skip POS process on return
      out.println("<input type=\"hidden\" name=\"POSdone\" value=\"yes\">");       // POS charge was done
      out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + num + "\">");       // num = index
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseOrig + "\">");
      out.println("<input type=\"hidden\" name=\"courseCheckIn\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
      if (req.getParameter("checkAll") != null) {
         out.println("<input type=\"hidden\" name=\"checkAll\" value=\"yes\">");
      } else {   // single cehck-in/out
         out.println("<input type=\"hidden\" name=\"noShow\" value=\"" +p+ "\">");  // player #
         out.println("<input type=\"hidden\" name=\"show\" value=\"" +show6+ "\">");
         out.println("<input type=\"hidden\" name=\"pos6\" value=\"" +pos6+ "\">");
      }
      //
      //  Issue the window.status twice to generate the StatusTextChange event once, then clear it to
      //  prevent multiple triggers.
      //
      out.println("<input type=\"submit\" value=\"Yes - Continue\" ONCLICK=\"sendstatus('" +parmp.poslist+ "')\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</font></form>");

      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet\">");
      out.println("<input type=\"hidden\" name=\"POScontinue\" value=\"yes\">");   // skip POS process on return
      out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + num + "\">");       // num = index
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseOrig + "\">");
      out.println("<input type=\"hidden\" name=\"courseCheckIn\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
      if (req.getParameter("checkAll") != null) {
         out.println("<input type=\"hidden\" name=\"checkAll\" value=\"yes\">");
      } else {   // single cehck-in/out
         out.println("<input type=\"hidden\" name=\"noShow\" value=\"" +p+ "\">");  // player #
         out.println("<input type=\"hidden\" name=\"show\" value=\"" +show6+ "\">");
      }
      out.println("<input type=\"submit\" value=\"No - Just Process Check-in/out\" name=\"return\" ONCLICK=\"window.status='';return true;\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</font></form>");

      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + num + "\">");       // num = index
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseOrig + "\">");
      out.println("<input type=\"hidden\" name=\"courseCheckIn\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
      out.println("<input type=\"submit\" value=\"No - Skip Everything and Return\" name=\"return\" ONCLICK=\"window.status='';return true;\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</font></form>");

      out.println("</CENTER></BODY></HTML>");
      out.close();
      if (Gzip == true) {
         resp.setContentLength(buf.size());                 // set output length
         resp.getOutputStream().write(buf.toByteArray());
      }
   }

   return(charges);
 }                   // end of promptPOS


 // ********************************************************************
 //  Process the POS charges for an individual member (Pro-ShopKeeper)
 //
 //    Check the mode of trans for charges
 // ********************************************************************

 public void buildCharge(parmPOS parmp, Connection con) {


   ResultSet rs = null;

   String fname = "";
   String lname = "";
   String mship = "";
   String posid = "";
   String tpos = "";
   String mpos = "";
   String mposc = "";
   String gpos = "";

   int i = 0;
   int p9c = 0;


   try {

      //
      //  First check if there is a charge code associated with this member's mode of trans
      //
      i = 0;
      loop1:
      while (i < parmp.MAX_Tmodes) {

         if (parmp.tmodea[i].equals( parmp.pcw )) {     // if matching mode of trans found

            if (parmp.p9 == 0) {                  // if 18 hole round

               tpos = parmp.tpos[i];               // get 18 hole charge

            } else {

               tpos = parmp.t9pos[i];              // get 9 hole charge
            }
            break loop1;
         }
         i++;
      }

      //
      //  get the member's name and mship info
      //
      PreparedStatement pstmtc = con.prepareStatement (
         "SELECT name_last, name_first, m_ship, posid FROM member2b WHERE username= ?");

      pstmtc.clearParameters();        // clear the parms
      pstmtc.setString(1, parmp.user);

      rs = pstmtc.executeQuery();

      if (rs.next()) {

         lname = rs.getString(1);
         fname = rs.getString(2);
         mship = rs.getString(3);
         posid = rs.getString(4);
      }
      pstmtc.close();

      //
      //  get the mship class and charge code, if any
      //
      i = 0;
      loop2:
      while (i < parmp.MAX_Mships) {

         if (parmp.mship[i].equalsIgnoreCase( mship )) {     // if matching mode mship type

            mpos = parmp.mpos[i];               // get mship charge class
              
            if (parmp.p9 == 0) {                  // if 18 hole round

               mposc = parmp.mposc[i];             // get mship charge code
                  
            } else {
              
               mposc = parmp.m9posc[i];             // get mship charge code
            }
            break loop2;
         }
         i++;
      }

      if (!mposc.equals( "" )) {          // if pos charge found for membership (non-golf mship charge)

         //
         //  We can now build the charge string - append it to the existing string
         //
         parmp.count++;          // bump charge counter

         if (parmp.p9 == 1) {        // 9 hole round ?
            p9c = 9;
         } else {
            p9c = 18;
         }

         if (parmp.count > 1) {      // if others already exist

            parmp.poslist = parmp.poslist + ",";      // add comma for seperator
         }

         parmp.poslist = parmp.poslist + posid + ",";          // player id
         parmp.poslist = parmp.poslist + parmp.sdate + ",";    // date and time of tee time
         parmp.poslist = parmp.poslist + p9c + ",";             // 9 or 18 holes
         parmp.poslist = parmp.poslist + lname + ",";          // last name
         parmp.poslist = parmp.poslist + fname + ",,,,";       // first name - skip zip, phone, email
         parmp.poslist = parmp.poslist + mpos + ",";           // mship class code
         parmp.poslist = parmp.poslist + mposc;                // charge code for non-golf mship

      }      // end of Mship Charge processing

      if (!tpos.equals( "" )) {          // if pos charge found

         //
         //  We can now build the charge string - append it to the existing string
         //
         parmp.count++;          // bump charge counter

         if (parmp.p9 == 1) {        // 9 hole round ?
            p9c = 9;
         } else {
            p9c = 18;
         }

         if (parmp.count > 1) {      // if others already exist

            parmp.poslist = parmp.poslist + ",";      // add comma for seperator
         }

         parmp.poslist = parmp.poslist + posid + ",";          // player id
         parmp.poslist = parmp.poslist + parmp.sdate + ",";    // date and time of tee time
         parmp.poslist = parmp.poslist + p9c + ",";             // 9 or 18 holes
         parmp.poslist = parmp.poslist + lname + ",";          // last name
         parmp.poslist = parmp.poslist + fname + ",,,,";       // first name - skip zip, phone, email
         parmp.poslist = parmp.poslist + mpos + ",";           // mship class code
         parmp.poslist = parmp.poslist + tpos;                 // charge code for item (caddy, cart, etc.)

      }      // end of trans mode processing

      //
      //  if the player passed is a guest, charge the member for this too
      //
      if (!parmp.player.equals( "" )) {

         //
         //  First check if there is a charge code associated with this guest type
         //
         i = 0;
         loop3:
         while (i < parmp.MAX_Guests) {

            if (parmp.player.startsWith( parmp.gtype[i] )) {

               if (parmp.p9 == 0) {                  // if 18 hole round

                  gpos = parmp.gpos[i];               // get 18 hole charge

               } else {

                  gpos = parmp.g9pos[i];              // get 9 hole charge
               }
               break loop3;
            }
            i++;
         }

         if (!gpos.equals( "" )) {          // if pos charge found

            //
            //  We can now build the charge string - append it to the existing string
            //
            parmp.count++;          // bump charge counter

            if (parmp.p9 == 1) {        // 9 hole round ?
               p9c = 9;
            } else {
               p9c = 18;
            }

            if (parmp.count > 1) {      // if others already exist

               parmp.poslist = parmp.poslist + ",";      // add comma for seperator
            }

            parmp.poslist = parmp.poslist + posid + ",";           // player id
            parmp.poslist = parmp.poslist + parmp.sdate + ",";     // date and time of tee time
            parmp.poslist = parmp.poslist + p9c + ",";             // 9 or 18 holes
            parmp.poslist = parmp.poslist + lname + ",";          // last name
            parmp.poslist = parmp.poslist + fname + ",,,,";       // first name - skip zip, phone, email
            parmp.poslist = parmp.poslist + mpos + ",";           // mship class code
            parmp.poslist = parmp.poslist + gpos;                 // charge code for guest
         }

      }     // end of guest processing

   }
   catch (Exception ignore) {
   }

   return;
 }                   // end of buildCharge


 // ********************************************************************************************
 //  Process the POS charges for this day - Abacus21, Jonas, CSG, NorthStar, or TAI
 // ********************************************************************************************

 private void promptPOS2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session, Connection con) 
         throws ServletException, IOException {


   PreparedStatement pstmt2s = null;

   ResultSet rs = null;
   ResultSet rs2 = null;

   String player = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String lineNS = "";
   String filename = "";
   String temp = "";
   String day = "";
   String sfb = "";
   String stime = "";
   String ampm = "";


   int guest = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   int pos1 = 0;
   int pos2 = 0;
   int pos3 = 0;
   int pos4 = 0;
   int pos5 = 0;
   int i = 0;
   int p = 0;
   int ttid = 0;
   int fb = 0;
   int tfb = 0;
   int hr = 0;
   int min = 0;
   int sec = 0;
   int time = 0;
   int ttime = 0;
   int time2 = 0;
   int done = 0;
   int masterDone = 0;
   int singleDone = 0;

   long resnum = 0;
   long resnum2 = 0;
   long ttidnum = 0;
   long ttidnum2 = 0;

   //
   //  get the club name from the session
   //
   String club = (String)session.getAttribute("club");      // get club name

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();      

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();   

   //
   //  parm block to hold the POS parameters
   //
   parmPOS parmp = new parmPOS();     

   //
   //  Get the golf course name requested
   //
   String course = req.getParameter("course");
   String index = req.getParameter("index");         // get the index value of the day selected

   if (req.getParameter("time") != null) {

      stime = req.getParameter("time");         // get time if provided (CSG) 
   }

   if (req.getParameter("fb") != null) {

      sfb = req.getParameter("fb");            // get fb if provided (CSG)
   }


   try {
      //
      // Get the Guest Types from the club db
      //
      getClub.getParms(con, parm);        // get the club parms

      //
      //  Get the POS System Parameters for this Club & Course
      //
      getClub.getPOS(con, parmp, course);

   }
   catch (Exception e1) {

      resp.setContentType("text/html");                   // normal html response

      out.println(SystemUtils.HeadTitle("Proshop - POS Prompt"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Error processing the POS Request");
      out.println("<BR><BR>Error: " + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<form>");
      out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Close  \" onClick='self.close()' alt=\"Close\">");
      out.println("</form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Check if first time here, or we should build the report
   //
   if (req.getParameter("POScontinue") == null) {

      //  First time here - prompt to continue
        
      resp.setContentType("text/html");                   // normal html response

      out.println(SystemUtils.HeadTitle("Proshop - POS Prompt"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><H3>Send POS Charges Request</H3>");
      out.println("You have requested that all current POS related charges be sent to the POS system.<br><br>");
      out.println("Only charges for players that have been 'Checked In' will be transferred.");
      out.println("<BR><BR>Would you like to proceed with this report?");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
        
      if (parmp.posType.equals( "Jonas" )) {

         out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_sheet_jonas.htm', 'newwindow', 'height=540, width=500, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
         out.println("Click Here For Instructions</a>");
         out.println("<BR><BR>");
      }

      if (parmp.posType.equals( "Abacus21" )) {

         out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_sheet_abacus21.htm', 'newwindow', 'height=540, width=500, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
         out.println("Click Here For Instructions</a>");
         out.println("<BR><BR>");
      }

      if (parmp.posType.equals( "ClubSystems Group" )) {

         out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_sheet_csg.htm', 'newwindow', 'height=540, width=560, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
         out.println("Click Here For Instructions</a>");
         out.println("<BR><BR>");
      }

      if (parmp.posType.equals( "TAI Club Management" )) {

         out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_sheet_csg.htm', 'newwindow', 'height=540, width=560, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
         out.println("Click Here For Instructions</a>");
         out.println("<BR><BR>");
      }

      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet\">");
      out.println("<input type=\"hidden\" name=\"POScontinue\" value=\"yes\">");  
      out.println("<input type=\"hidden\" name=\"print\" value=\"pos\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"time\" value=\"" + stime + "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + sfb + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"0\">");
      out.println("<input type=\"submit\" value=\"Yes - Continue\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

      out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Return  \" onClick='self.close()' alt=\"Close\">");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

   } else {
     
      //********************************************************************
      //  User was prompted and opted to continue - build report
      //********************************************************************
      //
      //  Get today's date
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) +1;
      int daynum = cal.get(Calendar.DAY_OF_MONTH);
      hr = cal.get(Calendar.HOUR_OF_DAY);           // 24 hr clock (0 - 23)
      min = cal.get(Calendar.MINUTE);
      sec = cal.get(Calendar.SECOND);

      long date = year * 10000;                     // create a date field of yyyymmdd
      date = date + (month * 100);
      date = date + daynum;                            // date = yyyymmdd

      //
      //  Get time and fb if provided (CSG)
      //
      if (!stime.equals( "" )) {
        
         ttime = Integer.parseInt(stime);
      }
      if (!sfb.equals( "" )) {

         tfb = Integer.parseInt(sfb);
      }

      if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" ) || parmp.posType.equals( "TAI Club Management" )) {

         //
         //  Set the values for the Reservation Number and Tee Time Id fields (yymmddnn, where nn changes)
         //
         resnum2 = date - 20000000;                     // resnum = yymmdd
         resnum = resnum2 * 100;                        // shift over for nn
         ttidnum = resnum;                             // ditto
         ttidnum2 = resnum;                            // ditto - save this portion

         resn++;                                       // get the next res #
         ttidn++;                                      // get the next tee time num

         if (resn > 990) {
            resn = 1;          // init
         }
         if (ttidn > 990) {
            ttidn = 1;          // init
         }

         resnum += resn;                               // create new res num
         ttidnum += ttidn;                             // create new tee time id num

         parmp.sdate = String.valueOf(date);           // save string value of date for file

         filen++;                                                     // get next unique id value
         if (filen > 999) {
            filen = 1;         // reset
         }

         //
         // establish an ASCII filename
         //
         if (club.equals( "medinahcc" ) && parmp.posType.equals( "Abacus21" )) {    // if Medinah and Abacus21
           
            filename = String.valueOf( resnum2 );           // yymmdd
            temp = String.valueOf( filen );                 // nnn
              
            if (course.endsWith( "1" )) {                    // file name based on course (No 1, No 2, or No 3)
              
               filename = "C1-" + filename + "-" + temp + ".ASC";  // C1-yymmdd-nnn.ASC (where nnn = sequential value)
  
            } else {

               if (course.endsWith( "2" )) {

                  filename = "C2-" + filename + "-" + temp + ".ASC";  // C2-yymmdd-nnn.ASC (where nnn = sequential value)

               } else {

                  filename = "C3-" + filename + "-" + temp + ".ASC";  // C3-yymmdd-nnn.ASC (where nnn = sequential value)
               }
            }

         } else {     // NOT Medinah & Abacus

            filename = String.valueOf( filen );
            filename = "ForeTees" + filename + ".ASC";         // name = ForeTeesnn.ASC
         }
           
         if (parmp.posType.equals( "TAI Club Management" )) {

            if (daynum < 10) {

               parmp.sdate = String.valueOf(month) + "/0" + String.valueOf(daynum) + "/" + String.valueOf(year);  // date for file

            } else {

               parmp.sdate = String.valueOf(month) + "/" + String.valueOf(daynum) + "/" + String.valueOf(year);  // date for file
            }
         }

      } else {         // NorthStar or CSG - date = mm/dd/yyyy

         parmp.count = 0;                 // init record counter

         //
         //  build the header for the text file - mm/dd/yyyy hh:mm:ss
         //
         //  establish an ASCII filename       (clubname-mmddyyyy-hhmmss.txt)
         //
         String tempNS = String.valueOf(month);
           
         if (month < 10) {
           
            tempNS = "0" + String.valueOf(month);
         }
         
         filename = club + "-" + tempNS;          // clubname-mm

         parmp.sdate = tempNS + "/";              // mm/

         tempNS = String.valueOf(daynum);

         if (daynum < 10) {

            tempNS = "0" + String.valueOf(daynum);
         }

         filename = filename + tempNS + String.valueOf(year) + "-";                 // clubname-mmddyyyy-

         parmp.sdate = parmp.sdate + tempNS + "/" + String.valueOf(year);     // mm/dd/yyyy


         if (parmp.posType.equals( "ClubSystems Group" )) {    // if CSG - only 1 tee time per file!!

            hr = ttime / 100;              // use tee time for time value
            min = ttime - (hr * 100);
            sec = 0;
              
            ampm = " AM";
              
            if (hr == 12) {
              
               ampm = " PM";
            }
              
            if (hr > 12) {
              
               hr = hr - 12;
               ampm = " PM";
            }
              
            tempNS = String.valueOf(hr);

            if (hr < 10) {

               tempNS = "0" + String.valueOf(hr);
            }

            filename = filename + tempNS;                           // clubname-mmddyy-hh

            parmp.stime = tempNS + ":";                             // hh:

            tempNS = String.valueOf(min);

            if (min < 10) {

               tempNS = "0" + String.valueOf(min);
            }

            filename = filename + tempNS;                           // clubname-mmddyy-hhmm

            parmp.stime = parmp.stime + tempNS + ":";               // hh:mm:

            tempNS = String.valueOf(sec);

            if (sec < 10) {

               tempNS = "0" + String.valueOf(sec);
            }

            filename = filename + tempNS + ".dat";                // clubname-mmddyy-hhmmss.dat (CSG file name)

            parmp.stime = parmp.stime + tempNS + " " +ampm;       // hh:mm:ss AM (or PM)

            //
            //  Set the output type to Text File - must do this now!!
            //
            resp.setContentType("Content-type: application/txt");                         // text file
            resp.setHeader("Content-Disposition", "filename=\"" +filename+ "\"");         // default file name


         } else {            // Northstar
           
            tempNS = String.valueOf(hr);

            if (hr < 10) {

               tempNS = "0" + String.valueOf(hr);
            }

            filename = filename + tempNS;                           // clubname-mmddyy-hh

            parmp.sdate = parmp.sdate + " " + tempNS + ":";         // mm/dd/yyyy hh:

            tempNS = String.valueOf(min);

            if (min < 10) {

               tempNS = "0" + String.valueOf(min);
            }

            filename = filename + tempNS;                           // clubname-mmddyy-hhmm

            parmp.sdate = parmp.sdate + tempNS + ":";               // mm/dd/yyyy hh:mm:

            tempNS = String.valueOf(sec);

            if (sec < 10) {

               tempNS = "0" + String.valueOf(sec);
            }

            filename = filename + tempNS;                      // clubname-mmddyy-hhmmss (".txt" to be added below)

            parmp.sdate = parmp.sdate + tempNS;                // mm/dd/yyyy hh:mm:ss
         }
      }

      masterDone = 0;             // init master done flag

      //
      // create text file (one row per charge)
      //
      //  Get today's tee sheet and process the players one at a time.
      //
      try {

         if (parmp.posType.equals( "ClubSystems Group" )) {    // if CSG - only 1 tee time per file!!

            pstmt2s = con.prepareStatement (
               "SELECT * " +
               "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

            pstmt2s.clearParameters();        // clear the parms
            pstmt2s.setLong(1, date);
            pstmt2s.setInt(2, ttime);
            pstmt2s.setInt(3, tfb);
            pstmt2s.setString(4, course);

         } else {

            pstmt2s = con.prepareStatement (
               "SELECT * " +
               "FROM teecurr2 WHERE date = ? AND courseName = ? ORDER BY time, fb");

            pstmt2s.clearParameters();        // clear the parms
            pstmt2s.setLong(1, date);
            pstmt2s.setString(2, course);
         }

         rs = pstmt2s.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            day = rs.getString("day");
            hr = rs.getInt("hr");
            min = rs.getInt("min");
            time = rs.getInt("time");
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            user1 = rs.getString("username1");
            user2 = rs.getString("username2");
            user3 = rs.getString("username3");
            user4 = rs.getString("username4");
            p1cw = rs.getString("p1cw");
            p2cw = rs.getString("p2cw");
            p3cw = rs.getString("p3cw");
            p4cw = rs.getString("p4cw");
            show1 = rs.getInt("show1");
            show2 = rs.getInt("show2");
            show3 = rs.getInt("show3");
            show4 = rs.getInt("show4");
            fb = rs.getInt("fb");
            player5 = rs.getString("player5");
            user5 = rs.getString("username5");
            p5cw = rs.getString("p5cw");
            show5 = rs.getInt("show5");
            userg1 = rs.getString("userg1");
            userg2 = rs.getString("userg2");
            userg3 = rs.getString("userg3");
            userg4 = rs.getString("userg4");
            userg5 = rs.getString("userg5");
            p91 = rs.getInt("p91");
            p92 = rs.getInt("p92");
            p93 = rs.getInt("p93");
            p94 = rs.getInt("p94");
            p95 = rs.getInt("p95");
            pos1 = rs.getInt("pos1");
            pos2 = rs.getInt("pos2");
            pos3 = rs.getInt("pos3");
            pos4 = rs.getInt("pos4");
            pos5 = rs.getInt("pos5");

            done = 0;            // init done some flag for this tee time
            singleDone = 0;      // init single tee time done flag

            parmp.day = day;       // save tee time values in parmp
            parmp.time = time;
            parmp.date = date;
            parmp.course = course;

            if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

               //
               //  Determine the time and save string for charge records
               //
               StringBuffer tempSB = null;
               time2 = time;                    // save time

               if (time2 > 1159) {               // if PM

                  if (time2 > 1259) {            // if 1 PM or later

                     time2 = time2 - 1200;
                  }

                  parmp.stime = String.valueOf( time2 );      // convert time to string value
                  tempSB = new StringBuffer(parmp.stime);     // put in string buffer
                  tempSB.append(",P,");                       // indicate PM

               } else {

                  parmp.stime = String.valueOf( time2 );      // convert time to string value
                  tempSB = new StringBuffer(parmp.stime);     // put in string buffer
                  tempSB.append(",A,");                       // indicate AM
               }
               parmp.stime = tempSB.toString();               // save as string value
                 
               //
               //  Set the output type to Text File - must do this now!!
               //
               resp.setContentType("Content-type: application/txt");                         // text file
               resp.setHeader("Content-Disposition", "filename=\"" +filename+ "\"");         // default file name
            }

            if (parmp.posType.equals( "TAI Club Management" )) {

               if (min < 10) {
                 
                  parmp.stime = String.valueOf(hr) + ":0" + String.valueOf(min);  // tee time for file
                     
               } else {
                 
                  parmp.stime = String.valueOf(hr) + ":" + String.valueOf(min);  // tee time for file
               }

               //
               //  Set the output type to Text File - must do this now!!
               //
               resp.setContentType("Content-type: application/txt");                         // text file
               resp.setHeader("Content-Disposition", "filename=\"" +filename+ "\"");         // default file name
            }

            //
            //  Set TTID (player id) for CSG (must be 10 digits)
            //
            ttid = 1000000010;

            //
            //  Process one player at a time to determine any charges
            //
            if (!player1.equalsIgnoreCase( "x" ) && !player1.equals( "" ) && show1 == 1) {

               //
               //  Check if player name is member or guest
               //
               i = 0;
               guest = 0;

               if (user1.equals( "" )) {            // if no username for this player

                  ploop1:
                  while (i < parm.MAX_Guests) {
                     if (player1.startsWith( parm.guest[i] )) {

                        guest = 1;       // indicate player1 is a guest name
                        break ploop1;
                     }
                     i++;
                  }
               }
               parmp.pcw = p1cw;
               parmp.p9 = p91;

               if (guest == 0) {        // if member

                  if (!user1.equals( "" ) && pos1 == 0) {      // skip if no user name found or already processed

                     parmp.player = "";   // indicate member
                     parmp.user = user1;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos1 = 1;            // set as processed
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                     }
                  }

               } else {          // else guest

                  if (!userg1.equals( "" ) && pos1 == 0) {      // skip if no member associated with this guest

                     parmp.player = player1;   // indicate guest - pass the guest type
                     parmp.user = userg1;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        ttid+= 1;               // indicate guest
                          
                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos1 = 1;            // set as processed
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                     }
                  }
               }   // end of IF member or guest
            }      // end of IF player not X and not null

            ttid+= 10;                // next player id
              
            if (!player2.equalsIgnoreCase( "x" ) && !player2.equals( "" ) && show2 == 1) {

               //
               //  Check if player name is member or guest
               //
               i = 0;
               guest = 0;

               if (user2.equals( "" )) {            // if no username for this player

                  ploop2:
                  while (i < parm.MAX_Guests) {
                     if (player2.startsWith( parm.guest[i] )) {

                        guest = 1;       // indicate player2 is a guest name
                        break ploop2;
                     }
                     i++;
                  }
               }
               parmp.pcw = p2cw;
               parmp.p9 = p92;

               if (guest == 0) {        // if member

                  if (!user2.equals( "" ) && pos2 == 0) {      // skip if no user name found

                     parmp.player = "";   // indicate member
                     parmp.user = user2;
                       
                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos2 = 1;            // set as processed
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                     }
                  }

               } else {          // else guest

                  if (!userg2.equals( "" ) && pos2 == 0) {      // skip if no member associated with this guest

                     parmp.player = player2;   // indicate guest - pass the guest type
                     parmp.user = userg2;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        ttid+= 1;               // indicate guest

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos2 = 1;            // set as processed
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                     }
                  }
               }   // end of IF member or guest
            }      // end of IF player not X and not null

            ttid+= 10;                // next player id

            if (!player3.equalsIgnoreCase( "x" ) && !player3.equals( "" ) && show3 == 1) {

               //
               //  Check if player name is member or guest
               //
               i = 0;
               guest = 0;

               if (user3.equals( "" )) {            // if no username for this player

                  ploop3:
                  while (i < parm.MAX_Guests) {
                     if (player3.startsWith( parm.guest[i] )) {

                        guest = 1;       // indicate player3 is a guest name
                        break ploop3;
                     }
                     i++;
                  }
               }
               parmp.pcw = p3cw;
               parmp.p9 = p93;

               if (guest == 0) {        // if member

                  if (!user3.equals( "" ) && pos3 == 0) {      // skip if no user name found

                     parmp.player = "";   // indicate member
                     parmp.user = user3;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos3 = 1;            // set as processed
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                     }
                  }

               } else {          // else guest

                  if (!userg3.equals( "" ) && pos3 == 0) {      // skip if no member associated with this guest

                     parmp.player = player3;   // indicate guest - pass the guest type
                     parmp.user = userg3;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        ttid+= 1;               // indicate guest

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos3 = 1;            // set as processed
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                     }
                  }
               }   // end of IF member or guest
            }      // end of IF player not X and not null

            ttid+= 10;                // next player id

            if (!player4.equalsIgnoreCase( "x" ) && !player4.equals( "" ) && show4 == 1) {

               //
               //  Check if player name is member or guest
               //
               i = 0;
               guest = 0;

               if (user4.equals( "" )) {            // if no username for this player

                  ploop4:
                  while (i < parm.MAX_Guests) {
                     if (player4.startsWith( parm.guest[i] )) {

                        guest = 1;       // indicate player4 is a guest name
                        break ploop4;
                     }
                     i++;
                  }
               }
               parmp.pcw = p4cw;
               parmp.p9 = p94;

               if (guest == 0) {        // if member

                  if (!user4.equals( "" ) && pos4 == 0) {      // skip if no user name found

                     parmp.player = "";   // indicate member
                     parmp.user = user4;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos4 = 1;            // set as processed
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                     }
                  }

               } else {          // else guest

                  if (!userg4.equals( "" ) && pos4 == 0) {      // skip if no member associated with this guest

                     parmp.player = player4;   // indicate guest - pass the guest type
                     parmp.user = userg4;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        ttid+= 1;               // indicate guest

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos4 = 1;            // set as processed
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                     }
                  }
               }   // end of IF member or guest
            }      // end of IF player not X and not null

            ttid+= 10;                // next player id

            if (!player5.equalsIgnoreCase( "x" ) && !player5.equals( "" ) && show5 == 1) {

               //
               //  Check if player name is member or guest
               //
               i = 0;
               guest = 0;

               if (user5.equals( "" )) {            // if no username for this player

                  ploop5:
                  while (i < parm.MAX_Guests) {
                     if (player5.startsWith( parm.guest[i] )) {

                        guest = 1;       // indicate player5 is a guest name
                        break ploop5;
                     }
                     i++;
                  }
               }
               parmp.pcw = p5cw;
               parmp.p9 = p95;

               if (guest == 0) {        // if member

                  if (!user5.equals( "" ) && pos5 == 0) {      // skip if no user name found

                     parmp.player = "";   // indicate member
                     parmp.user = user5;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos5 = 1;            // set as processed
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                     }
                  }

               } else {          // else guest

                  if (!userg5.equals( "" ) && pos5 == 0) {      // skip if no member associated with this guest

                     parmp.player = player5;   // indicate guest - pass the guest type
                     parmp.user = userg5;

                     if (parmp.posType.equals( "Abacus21" ) || parmp.posType.equals( "Jonas" )) {

                        done = buildJonas(parmp, out, con, resnum, ttidnum, club);
                     }

                     if (parmp.posType.equals( "TAI Club Management" )) {

                        done = buildTAI(parmp, out, con, club);
                     }

                     if (parmp.posType.equals( "ClubSystems Group" )) {

                        ttid+= 1;               // indicate guest

                        done = buildCSG(parmp, out, con, club, ttid);
                     }

                     if (parmp.posType.equals( "NorthStar" )) {

                        done = buildLineNS(parmp, filename, out, con);      // add charges to file, if any
                     }

                     if (done == 1) {        // if 'done some' returned
                        pos5 = 1;            // set as processed
                        singleDone = 1;      // set single tee time done flag
                        masterDone = 1;      // set master done flag

                        ttidnum = ttidnum2 + ttidn;    // create new tee time id num
                     }
                  }
               }   // end of IF member or guest
            }      // end of IF player not X and not null
  
            //
            //  Now set this tee time as processed (if we did)
            //
            if (singleDone == 1) {          // if we did any in this tee time
              
               PreparedStatement pstmt3 = con.prepareStatement (
                   "UPDATE teecurr2 SET pos1 = ?, pos2 = ?, pos3 = ?, pos4 = ?, pos5 = ? " +
                   "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               //
               //  execute the prepared statement to update the tee time slot
               //
               pstmt3.clearParameters();        // clear the parms
               pstmt3.setInt(1, pos1);
               pstmt3.setInt(2, pos2);
               pstmt3.setInt(3, pos3);
               pstmt3.setInt(4, pos4);
               pstmt3.setInt(5, pos5);

               pstmt3.setLong(6, date);
               pstmt3.setInt(7, time);
               pstmt3.setInt(8, fb);
               pstmt3.setString(9, course);
               pstmt3.executeUpdate();

               pstmt3.close();
            }
  
         }     // end of WHILE

         pstmt2s.close();

      }
      catch (Exception e1) {

         String errorMsg1 = "Error1 in Proshop_sheet promptPOS2: ";
         errorMsg1 = errorMsg1 + e1.getMessage();                                // build error msg

         SystemUtils.logError(errorMsg1);                                       // log it
      }

      //
      // done with text file - check if we built any records
      //
      if (masterDone == 1) {   // if we built any records

         if (parmp.posType.equals( "NorthStar" )) {

            //
            //  NorthStar - close out the file (add counter and footer)
            //
            String counts = String.valueOf( parmp.count );     // create string value from count

            addLineNS(filename, counts);                       // put in file

            addLineNS(filename, "EOF");                        // add End Of File indicator (footer)
              
            resp.setContentType("text/html");                   // normal html response

            out.println(SystemUtils.HeadTitle("Proshop - POS Prompt"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>POS Charges Sent</H3>");
            out.println("<br>The POS charges have been sent to the POS system.");
            out.println("<BR><BR>");
            out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Return  \" onClick='self.close()' alt=\"Close\">");
            out.println("</CENTER></BODY></HTML>");
         }

      } else {

         resp.setContentType("text/html");                   // normal html response

         out.println(SystemUtils.HeadTitle("Proshop - POS Prompt"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><H3>No POS Charges To Send</H3>");
         out.println("<br>There are no new POS charges to send.");
         out.println("<BR><BR>");
         out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Return  \" onClick='self.close()' alt=\"Close\">");
         out.println("</CENTER></BODY></HTML>");
      }

      out.close();              // close the file
  
   }

   return;
 }                   // end of promptPOS2


 // ********************************************************************
 //  Process the Abacus or Jonas POS charges for an individual member
 //
 //  Build an ASCII file containing the following:
 //
 //   Columns:
 //       1 Member Number or POSID                                Alpha 10
 //       2 Member? (Y/N)     (always 'Y')                        Alpha 1
 //       3 Number Of Guests                                      Alpha 3
 //       4 Last Name                                             Alpha 30
 //       5 First Name                                            Alpha 20
 //       6 Telephone Number   (skip)                             Alpha 15
 //       7 Tee off Date                                          Date YYYYMMDD
 //       8 Tee Off Time                                          Alpha HHMM
 //       9 AM / PM (A or P)                                      Alpha 1
 //       10 Course Code       ('A' if only 1 course)             Alpha 2
 //       11 Start Tee/Group Number    (skip)                     Alpha 3
 //       12 UNUSED (reserved for Deposit Amount)                 Numeric 0000.00
 //       13 Credit Card Type          (skip)                     Alpha 4
 //       14 Credit Card Number        (skip)                     Alpha 20
 //       15 Credit Card Expiry        (skip)                     Alpha YYYYMM
 //       16 Tee Time Sales Item                                  Alpha 6
 //       17 Units Sold                                           Numeric 0000
 //       18 Confirmation/Reservation Number                      Alpha 10
 //       19 UNUSED (Tee Time Sales Item Price) (skip)            Numeric 0000.00
 //       20 Tee-Time ID number                                   Alpha 10
 //       21 Green Fee Credit Card Authorization Code  (skip)     Alpha 9
 //       22 Credit Card Authorization Date            (skip)     Date YYYYMMDD
 //       23 Prepaid Indicator (Y-yes, N-no)           ('N')      Alpha 1
 //       24 Res. Fee Credit Card Authorization Code   (skip)     Alpha 9
 //       25 Reservation Fee Amount                    (skip)     Numeric ####.00
 //       26 Checked In Indicator (Y-yes, N-no)        ('Y')      Alpha 1
 //
 //    Check the mode of trans for charges
 // ********************************************************************

 public int buildJonas(parmPOS parmp, PrintWriter out, Connection con, long resnum, long ttidnum, String club) {


   ResultSet rs = null;

   String mship = "";
   String mnum = "";
   String posid = "";
   String fname = "";
   String lname = "";
   String tpos = "";
   String mpos = "";
   String mposc = "";
   String gpos = "";
   String gtype = "";
   String tmode = "";
   String tmodea = "";
   String item = "";
   String line = "";

   int i = 0;
   int p9c = 0;
   int done = 0;
     

   try {
      //
      //  get the member's mship info
      //
      PreparedStatement pstmtc = con.prepareStatement (
         "SELECT name_last, name_first, m_ship, memNum, posid FROM member2b WHERE username= ?");

      pstmtc.clearParameters();        // clear the parms
      pstmtc.setString(1, parmp.user);

      rs = pstmtc.executeQuery();

      if (rs.next()) {

         lname = rs.getString(1);
         fname = rs.getString(2);
         mship = rs.getString(3);
         mnum = rs.getString(4);
         posid = rs.getString(5);
      }
      pstmtc.close();

   }
   catch (Exception e1) {

      String errorMsg1 = "Error1 in Proshop_sheet buildJonas for club: " + club;
      errorMsg1 = errorMsg1 + ", Exception: " + e1.getMessage();      // build error msg

      SystemUtils.logError(errorMsg1);                                       // log it
   }

   //
   //  if Lakewood CC - use the member posid instead of the mnum
   //
   if (club.equals( "lakewood" ) & !posid.equals( "" )) {
     
      mnum = posid;
   }

   //
   //  Skip if no mNum/posid - otherwise entire file will fail
   //
   if (!mnum.equals( "" )) {

      try {

         //
         //  First check if there is a charge amount associated with this member's mode of trans
         //
         if (club.equals( "medinahcc" )) {      // if Medinah do manual item codes

            if (parmp.pcw.equals( "4BG" )) {          // 4 Bagger

               item = "20190";
            }

            if (parmp.pcw.equals( "CAR" )) {          // Half Cart

               if (parmp.p9 == 1) {                   // if 9 holes
                  item = "20130";
               } else {
                  item = "20110";
               }
            }

            if (parmp.pcw.equals( "FCA" )) {          // Full Cart

               if (parmp.p9 == 1) {                   // if 9 holes
                  item = "20120";
               } else {
                  item = "20100";
               }
            }

         } else {        // not Medinah

            i = 0;
            loop1:
            while (i < parmp.MAX_Tmodes) {

               if (parmp.tmodea[i].equals( parmp.pcw )) {     // if matching mode of trans found

                  tmode = parmp.tmode[i];             // get full description of tmode
                  if (parmp.p9 == 1) {                   // if 9 holes
                     item = parmp.t9pos[i];               // get Item Group # for tmode
                  } else {
                     item = parmp.tpos[i];               // get Item Group # for tmode
                  }
                  break loop1;
               }
               i++;
            }
         }

         if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for Mode of Trans selected

            //
            //  We can now build the charge string
            //
            StringBuffer tempSB = new StringBuffer(mnum);     // put member # in string buffer
            tempSB.append(",Y,1,");                           // indicator, # of guests (players?)
            tempSB.append(lname);                             // last name
            tempSB.append(",");
            tempSB.append(fname);                             // first name
            tempSB.append(",,");                              // skip phone #
            tempSB.append(parmp.sdate);                       // date of tee time
            tempSB.append(",");
            tempSB.append(parmp.stime);                       // time (includes A or P for AM or PM and ,)
            tempSB.append(parmp.courseid);                    // Course Code
            tempSB.append(",,,,,,");                          // skips
            tempSB.append(item);                              // Sales Item #
            tempSB.append(",1,");                             // units sold
            tempSB.append(resnum);                            // Reservation #
            tempSB.append(",,");                              // unused
            tempSB.append(ttidnum);                           // Tee Time Id #
            tempSB.append(",,,N,,,Y");                        // units sold, skips, Prepaid, Checked-in

            line = tempSB.toString();                         // save as string value

            out.print(line);
            out.println();      // output the line

            done = 1;           // indicate charge sent

            ttidn++;            // bump tee time id number
            ttidnum++;
         }

         //
         //  get the mship class and charge amount, if any and if member!
         //
         if (parmp.player.equals( "" )) {       // if member

            i = 0;
            item = "";
            loop2:
            while (i < parmp.MAX_Mships) {

               if (parmp.mship[i].equalsIgnoreCase( mship )) {     // if matching mode mship type

                  if (parmp.p9 == 1) {                   // if 9 holes
                     item = parmp.mship9I[i];               // get mship item group #
                  } else {
                     item = parmp.mshipI[i];               // get mship item group #
                  }
                  break loop2;
               }
               i++;
            }

            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for membership (non-golf mship charge)

               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(mnum);     // put member # in string buffer
               tempSB.append(",Y,1,");                           // indicator, # of guests (players?)
               tempSB.append(lname);                             // last name
               tempSB.append(",");
               tempSB.append(fname);                             // first name
               tempSB.append(",,");                              // skip phone #
               tempSB.append(parmp.sdate);                       // date of tee time
               tempSB.append(",");
               tempSB.append(parmp.stime);                       // time (includes A or P for AM or PM and ,)
               tempSB.append(parmp.courseid);                    // Course Code
               tempSB.append(",,,,,,");                          // skips
               tempSB.append(item);                              // Sales Item #
               tempSB.append(",1,");                             // units sold
               tempSB.append(resnum);                            // Reservation #
               tempSB.append(",,");                              // unused
               tempSB.append(ttidnum);                           // Tee Time Id #
               tempSB.append(",,,N,,,Y");                        // units sold, skips, Prepaid, Checked-in

               line = tempSB.toString();                         // save as string value

               out.print(line);
               out.println();      // output the line

               done = 1;           // indicate charge sent

               ttidn++;            // bump tee time id number
               ttidnum++;
                 
            }      // end of Mship Charge processing

         } else {

            //
            //  player passed is a guest - charge the member for this too
            //
            //
            //  First check if there is a charge amount associated with this guest type
            //
            if (club.equals( "medinahcc" )) {      // if Medinah do manual item codes

               if (parmp.course.equals( "No 1" )) {         // Course #1

                  if (parmp.player.startsWith( "Guest" )) {         // normal Guest

                     if (parmp.time > 1600) {

                        item = "120100";              // after 4 PM

                     } else {

                        if ((parmp.day.equals( "Saturday" ) || parmp.day.equals( "Sunday" ) ||
                            parmp.date == Hdate1 || parmp.date == Hdate2 || parmp.date == Hdate3) && parmp.time < 1200) {

                           item = "130100";           // S-S-H before noon

                        } else {

                           item = "110100";           // T-F and others
                        }
                     }
                  }

                  if (parmp.player.startsWith( "Unaccom" )) {       // Unaccompanied Guest

                     item = "140100";
                  }

                  if (parmp.player.startsWith( "Replay" )) {        // Replay Guest

                     item = "140400";
                  }
               }                             // end of course #1

               if (parmp.course.equals( "No 2" )) {         // Course #2

                  if (parmp.player.startsWith( "Guest" )) {         // normal Guest

                     if (parmp.time > 1600) {

                        item = "120200";              // after 4 PM

                     } else {

                        if ((parmp.day.equals( "Saturday" ) || parmp.day.equals( "Sunday" ) ||
                            parmp.date == Hdate1 || parmp.date == Hdate2 || parmp.date == Hdate3) && parmp.time < 1200) {

                           item = "130200";           // S-S-H before noon

                        } else {

                           item = "110200";           // T-F and others
                        }
                     }
                  }

                  if (parmp.player.startsWith( "Unaccom" )) {       // Unaccompanied Guest

                     item = "140200";
                  }

                  if (parmp.player.startsWith( "Replay" )) {        // Replay Guest

                     item = "140400";
                  }
               }                             // end of course #2

               if (parmp.course.equals( "No 3" )) {         // Course #3

                  if (parmp.player.startsWith( "Guest" )) {         // normal Guest

                     if (parmp.time > 1600) {

                        item = "120300";              // after 4 PM

                     } else {

                        if ((parmp.day.equals( "Saturday" ) || parmp.day.equals( "Sunday" ) ||
                            parmp.date == Hdate1 || parmp.date == Hdate2 || parmp.date == Hdate3) && parmp.time < 1200) {

                           item = "130300";           // S-S-H before noon

                        } else {

                           item = "110300";           // T-F and others
                        }
                     }
                  }

                  if (parmp.player.startsWith( "Unaccom" )) {       // Unaccompanied Guest

                     item = "140300";
                  }
               }                             // end of course #3

            } else {          // not Medinah

               i = 0;
               item = "";
               loop3:
               while (i < parmp.MAX_Guests) {

                  if (parmp.player.startsWith( parmp.gtype[i] )) {

                     gtype = parmp.gtype[i];               // set guest type description
                     if (parmp.p9 == 1) {                   // if 9 holes
                        item = parmp.gst9I[i];                 // set guest item group #
                     } else {
                        item = parmp.gstI[i];                 // set guest item group #
                     }
                     break loop3;
                  }
                  i++;
               }
            }

            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found

               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(mnum);     // put member # in string buffer
               tempSB.append(",Y,1,");                           // indicator, # of guests (players?)
               tempSB.append(lname);                             // last name
               tempSB.append(",");
               tempSB.append(fname);                             // first name
               tempSB.append(",,");                              // skip phone #
               tempSB.append(parmp.sdate);                       // date of tee time
               tempSB.append(",");
               tempSB.append(parmp.stime);                       // time (includes A or P for AM or PM and ,)
               tempSB.append(parmp.courseid);                    // Course Code
               tempSB.append(",,,,,,");                          // skips
               tempSB.append(item);                              // Sales Item #
               tempSB.append(",1,");                             // units sold
               tempSB.append(resnum);                            // Reservation #
               tempSB.append(",,");                              // unused
               tempSB.append(ttidnum);                           // Tee Time Id #
               tempSB.append(",,,N,,,Y");                        // units sold, skips, Prepaid, Checked-in

               line = tempSB.toString();                         // save as string value

               out.print(line);
               out.println();      // output the line

               done = 1;           // indicate charge sent

               ttidn++;            // bump tee time id number
               ttidnum++;
            }
         }     // end of guest processing

      }
      catch (Exception e2) {

         String errorMsg2 = "Error2 in Proshop_sheet buildJonas for club: " +club;
         errorMsg2 = errorMsg2 + ", Exception: " + e2.getMessage();                 // build error msg

         SystemUtils.logError(errorMsg2);                                           // log it
      }
   }

   return(done);
 }                   // end of buildJonas


 // ********************************************************************
 //  Process the TAI POS charges for an individual member
 //
 //  Build an ASCII file containing the following:
 //
 //   Columns:
 //       1 Course Id
 //       2 Date of Tee Time (mm/dd/yyyy)
 //       3 Time of Tee Time (hh:mm)
 //       4 Member Id (mNum)
 //       5 Member Name (member responsible for the charge)
 //       6 Quantity (always 1)
 //       7 SKU Number (item charge code)
 //
 //    Check the mode of trans for charges
 // ********************************************************************

 public int buildTAI(parmPOS parmp, PrintWriter out, Connection con, String club) {


   ResultSet rs = null;

   String mship = "";
   String mnum = "";
   String posid = "";
   String fname = "";
   String lname = "";
   String tpos = "";
   String mpos = "";
   String mposc = "";
   String gpos = "";
   String gtype = "";
   String tmode = "";
   String tmodea = "";
   String item = "";
   String line = "";

   int i = 0;
   int p9c = 0;
   int done = 0;


   try {
      //
      //  get the member's mship info
      //
      PreparedStatement pstmtc = con.prepareStatement (
         "SELECT name_last, name_first, m_ship, memNum, posid FROM member2b WHERE username= ?");

      pstmtc.clearParameters();        // clear the parms
      pstmtc.setString(1, parmp.user);

      rs = pstmtc.executeQuery();

      if (rs.next()) {

         lname = rs.getString(1);
         fname = rs.getString(2);
         mship = rs.getString(3);
         mnum = rs.getString(4);
         posid = rs.getString(5);
      }
      pstmtc.close();

   }
   catch (Exception e1) {

      String errorMsg1 = "Error1 in Proshop_sheet buildTAI for club: " + club;
      errorMsg1 = errorMsg1 + ", Exception: " + e1.getMessage();      // build error msg

      SystemUtils.logError(errorMsg1);                                       // log it
   }

   //
   //  Skip if no mNum/posid - otherwise entire file will fail
   //
   if (!mnum.equals( "" )) {

      try {

         //
         //  First check if there is a charge amount associated with this member's mode of trans
         //
         i = 0;
         loop1:
         while (i < parmp.MAX_Tmodes) {

            if (parmp.tmodea[i].equals( parmp.pcw )) {     // if matching mode of trans found

               tmode = parmp.tmode[i];             // get full description of tmode
               if (parmp.p9 == 1) {                   // if 9 holes
                  item = parmp.t9pos[i];               // get Item Group # for tmode
               } else {
                  item = parmp.tpos[i];               // get Item Group # for tmode
               }
               break loop1;
            }
            i++;
         }

         if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for Mode of Trans selected

            //
            //  We can now build the charge string
            //
            StringBuffer tempSB = new StringBuffer(parmp.courseid);  // put Course Id in string buffer
            tempSB.append(",");
            tempSB.append(parmp.sdate);                              // date of tee time
            tempSB.append(",");
            tempSB.append(parmp.stime);                              // time (includes A or P for AM or PM and ,)
            tempSB.append(",");
            tempSB.append(mnum);                                     // member number
            tempSB.append(",");
            tempSB.append(fname);                                    // first name
            tempSB.append(" ");                                      // space
            tempSB.append(lname);                                    // last name
            tempSB.append(",1,");                                    // quantity
            tempSB.append(item);                                     // Sales Item Code
              
            line = tempSB.toString();                         // save as string value

            out.print(line);
            out.println();      // output the line

            done = 1;           // indicate charge sent
         }

         //
         //  get the mship class and charge amount, if any and if player is a member!
         //
         if (parmp.player.equals( "" )) {    // if member
           
            i = 0;
            item = "";
            loop2:
            while (i < parmp.MAX_Mships) {

               if (parmp.mship[i].equalsIgnoreCase( mship )) {     // if matching mode mship type

                  if (parmp.p9 == 1) {                   // if 9 holes
                     item = parmp.mship9I[i];               // get mship item group #
                  } else {
                     item = parmp.mshipI[i];               // get mship item group #
                  }
                  break loop2;
               }
               i++;
            }

            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for membership (non-golf mship charge)

               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(parmp.courseid);  // put Course Id in string buffer
               tempSB.append(",");
               tempSB.append(parmp.sdate);                              // date of tee time
               tempSB.append(",");
               tempSB.append(parmp.stime);                              // time (includes A or P for AM or PM and ,)
               tempSB.append(",");
               tempSB.append(mnum);                                     // member number
               tempSB.append(",");
               tempSB.append(fname);                                    // first name
               tempSB.append(" ");                                      // space
               tempSB.append(lname);                                    // last name
               tempSB.append(",1,");                                    // quantity
               tempSB.append(item);                                     // Sales Item Code

               line = tempSB.toString();                         // save as string value

               out.print(line);
               out.println();      // output the line

               done = 1;           // indicate charge sent
                 
            }      // end of Mship Charge processing

         } else {

            //
            //  player passed is a guest - charge the member for this too
            //
            //
            //  First check if there is a charge amount associated with this guest type
            //
            i = 0;
            item = "";
            loop3:
            while (i < parmp.MAX_Guests) {

               if (parmp.player.startsWith( parmp.gtype[i] )) {

                  gtype = parmp.gtype[i];               // set guest type description
                  if (parmp.p9 == 1) {                   // if 9 holes
                     item = parmp.gst9I[i];                 // set guest item group #
                  } else {
                     item = parmp.gstI[i];                 // set guest item group #
                  }
                  break loop3;
               }
               i++;
            }

            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found

               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(parmp.courseid);  // put Course Id in string buffer
               tempSB.append(",");
               tempSB.append(parmp.sdate);                              // date of tee time
               tempSB.append(",");
               tempSB.append(parmp.stime);                              // time (includes A or P for AM or PM and ,)
               tempSB.append(",");
               tempSB.append(mnum);                                     // member number
               tempSB.append(",");
               tempSB.append(fname);                                    // first name
               tempSB.append(" ");                                      // space
               tempSB.append(lname);                                    // last name
               tempSB.append(",1,");                                    // quantity
               tempSB.append(item);                                     // Sales Item Code

               line = tempSB.toString();                         // save as string value

               out.print(line);
               out.println();      // output the line

               done = 1;           // indicate charge sent
            }
         }     // end of guest processing

      }
      catch (Exception e2) {

         String errorMsg2 = "Error2 in Proshop_sheet buildTAI for club: " +club;
         errorMsg2 = errorMsg2 + ", Exception: " + e2.getMessage();                 // build error msg

         SystemUtils.logError(errorMsg2);                                           // log it
      }
   }

   return(done);
 }                   // end of buildTAI


 // ********************************************************************
 //  Process the CSG POS charges for an individual member
 //
 //  Build an ASCII file (.dat) containing the following:
 //
 //   Columns:
 //       1 Reservation ID                                        Text 27
 //       2 TTID                                                  Num  10
 //       3 Course ID                                             Text  4
 //       4 Course Name                                           Text 30
 //       5 Reservation Date (mm/dd/yyyy)                         Date 10
 //       6 Reservation Time (hh:mm:ss AM/PM)                     Time 11   
 //       7 Member Number                                         Text 10
 //       8 Player First Name                                     Text 15
 //       9 Player Mid Name                                       Text  1
 //       10 Player Last Name                                     Text 25
 //       11 Item Name (leave blank - spaces)                     Text 30
 //       12 Item SKU                                             Text 12
 //       13 Item Price (leave blank - spaces)                    Text 10
 //       14 Player Status (1 = member, 2 = guest)                Text  1
 //       15 Address (not used - spaces)                          Text 50
 //       16 City (not used - spaces)                             Text 15
 //       17 State (not used - spaces)                            Text  2
 //       18 Zip Code (not used - spaces)                         Text 10
 //       19 Email Address (not used - spaces)                    Text 40
 //       20 Instruction (not used - spaces)                      Text 30
 //       21 CR LF                                                Text  2
 //
 //    Check for charges and build the record
 // ********************************************************************

 public int buildCSG(parmPOS parmp, PrintWriter out, Connection con, String club, int ttid) {


   ResultSet rs = null;

   String mship = "";
   String mnum = "";
   String posid = "";
   String fname = "";
   String lname = "";
   String mi = "";
   String tpos = "";
   String mpos = "";
   String mposc = "";
   String gpos = "";
   String gtype = "";
   String tmode = "";
   String tmodea = "";
   String line = "";
   String item = "";
   String itemName = "                              ";        // 30 spaces
   String itemPrice = "          ";                           // 10 spaces
   String address = "                                                  "; // 50 spaces
   String city = "               ";                           // 15 spaces
   String state = "  ";                                       //  2 spaces
   String zip = "          ";                                 // 10 spaces
   String email = "                                        "; // 40 spaces
   String instruct = "                              ";        // 30 spaces
   String courseid = parmp.courseid;
   String course = parmp.course;
   String resid = parmp.sdate+ " " +parmp.stime+ " ";       // 23 chars counting the spaces

   int i = 0;
   int p9c = 0;
   int done = 0;

   //
   //  Build the reservation id field
   //
   int len = parmp.courseid.length();        // get length of course id     
  
   if (len < 5) {                            // if 4 or less characters
     
      resid = resid + parmp.courseid;        // combine
   }
        
   len = resid.length();                     // get length of resid now

   while (len < 27) {

      resid = resid + " ";                  // add a space filler
      len++;
   }
        
   //
   //  Build the course id field (4 chars)
   //
   len = courseid.length();                   // get length of course id

   if (len < 4) {                            // if < 4 characters

      while (len < 4) {

         courseid = courseid + " ";         // add a space filler
         len++;
      }
        
   } else {

      if (len > 4) {                            // if > 4 characters

         while (len > 4) {

            courseid = stripOne(courseid);     // strip the first char from id
            len--;
         }
      }
   }

   //
   //  Get course name (30 chars) - can't be more than 30 since we define it as 30
   //
   len = course.length();                   // get length of course name

   if (len < 30) {                            // if < 4 characters

      while (len < 30) {

         course = course + " ";         // add a space filler
         len++;
      }
   }


   try {
      //
      //  get the member's mship info
      //
      PreparedStatement pstmtc = con.prepareStatement (
         "SELECT name_last, name_first, name_mi, m_ship, memNum, posid FROM member2b WHERE username= ?");

      pstmtc.clearParameters();        // clear the parms
      pstmtc.setString(1, parmp.user);

      rs = pstmtc.executeQuery();

      if (rs.next()) {

         lname = rs.getString(1);
         fname = rs.getString(2);
         mi = rs.getString(3);
         mship = rs.getString(4);
         mnum = rs.getString(5);
         posid = rs.getString(6);
      }
      pstmtc.close();

   }
   catch (Exception e1) {

      String errorMsg1 = "Error1 in Proshop_sheet buildCSG for club: " + club;
      errorMsg1 = errorMsg1 + ", Exception: " + e1.getMessage();      // build error msg

      SystemUtils.logError(errorMsg1);                                       // log it
   }

   //
   //  Skip if no posid - otherwise entire file will fail
   //
   if (!posid.equals( "" )) {

      //
      //  Build the POS ID field (10 chars)
      //
      len = posid.length();                   // get length of pos id

      if (len < 10) {                         // if < 10 characters

         while (len < 10) {

            posid = posid + " ";             // add a space filler
            len++;
         }

      } else {

         if (len > 10) {                         // if > 10 characters

            while (len > 10) {

               posid = stripOne(posid);         // strip the first char from id
               len--;
            }
         }
      }

      //
      //  Build the Name fields (15 & 25 chars)
      //
      len = fname.length();                   // get length of first name

      if (len < 15) {                         // if < 15 characters

         while (len < 15) {

            fname = fname + " ";             // add a space filler
            len++;
         }

      } else {

         if (len > 15) {                         // if > 15 characters

            while (len > 15) {

               fname = stripOne(fname);         // strip the first char
               len--;
            }
         }
      }

      len = lname.length();                   // get length of last name

      if (len < 25) {                         // if < 25 characters (will be - we max at 20)

         while (len < 25) {

            lname = lname + " ";             // add a space filler
            len++;
         }
      }

      if (mi.equals( "" )) {                // if mi not provided
        
         mi = " ";
      }


      try {

         //
         //  First check if there is a charge amount associated with this player's mode of trans
         //
         i = 0;
         loop1:
         while (i < parmp.MAX_Tmodes) {

            if (parmp.tmodea[i].equals( parmp.pcw )) {     // if matching mode of trans found

               tmode = parmp.tmode[i];             // get full description of tmode
               if (parmp.p9 == 1) {                   // if 9 holes
                  item = parmp.t9pos[i];               // get Item Group # for tmode
               } else {
                  item = parmp.tpos[i];               // get Item Group # for tmode
               }
               break loop1;
            }
            i++;
         }
           
         if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for Mode of Trans selected

            //
            //  Build the Item/SKU field (12 chars)
            //
            len = item.length();                   // get length of item code

            if (len < 12) {                         // if < 12 characters

               while (len < 12) {

                  item = item + " ";             // add a space filler
                  len++;
               }

            } else {

               if (len > 12) {                         // if > 12 characters

                  while (len > 12) {

                     item = stripOne(item);         // strip the first char
                     len--;
                  }
               }
            }

            //
            //  We can now build the charge string
            //
            StringBuffer tempSB = new StringBuffer(resid);    // put reservation id in string buffer
            tempSB.append(ttid);                              // player id
            tempSB.append(courseid);                          // course id
            tempSB.append(course);                            // course name
            tempSB.append(parmp.sdate);                       // date - mm/dd/yyyy
            tempSB.append(parmp.stime);                       // tee time - hh:mm:ss AM (or PM)
            tempSB.append(posid);                             // POS ID
            tempSB.append(fname);                             // first name
            tempSB.append(mi);                                // mi
            tempSB.append(lname);                             // last name
            tempSB.append(itemName);                          // blank
            tempSB.append(item);                              // item code (SKU)
            tempSB.append(itemPrice);                         // blank
            tempSB.append("2");                               // player status (1 = main member, 2 = reg member or guest)
            tempSB.append(address);                           // blank
            tempSB.append(city);                              // blank
            tempSB.append(state);                             // blank
            tempSB.append(zip);                               // blank
            tempSB.append(email);                             // blank
            tempSB.append(instruct);                          // blank
            tempSB.append("\r\n");                            // EOF = CR LF

            line = tempSB.toString();                         // save as string value

            out.print(line);
            out.println();      // output the line

            done = 1;           // indicate charge sent
         }

         //
         //  get the mship class and charge amount, if any and if player is a member!
         //
         if (parmp.player.equals( "" )) {      // if member
           
            i = 0;
            item = "";
            loop2:
            while (i < parmp.MAX_Mships) {

               if (parmp.mship[i].equalsIgnoreCase( mship )) {     // if matching mode mship type

                  if (parmp.p9 == 1) {                   // if 9 holes
                     item = parmp.mship9I[i];               // get mship item group #
                  } else {
                     item = parmp.mshipI[i];               // get mship item group #
                  }
                  break loop2;
               }
               i++;
            }

            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for membership (non-golf mship charge)

               //
               //  Build the Item/SKU field (12 chars)
               //
               len = item.length();                   // get length of item code

               if (len < 12) {                         // if < 12 characters

                  while (len < 12) {

                     item = item + " ";             // add a space filler
                     len++;
                  }

               } else {

                  if (len > 12) {                         // if > 12 characters

                     while (len > 12) {

                        item = stripOne(item);         // strip the first char
                        len--;
                     }
                  }
               }

               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(resid);    // put reservation id in string buffer
               tempSB.append(ttid);                              // player id
               tempSB.append(courseid);                          // course id
               tempSB.append(course);                            // course name
               tempSB.append(parmp.sdate);                       // date - mm/dd/yyyy
               tempSB.append(parmp.stime);                       // tee time - hh:mm:ss AM (or PM)
               tempSB.append(posid);                             // POS ID
               tempSB.append(fname);                             // first name
               tempSB.append(mi);                                // mi
               tempSB.append(lname);                             // last name
               tempSB.append(itemName);                          // blank
               tempSB.append(item);                              // item code (SKU)
               tempSB.append(itemPrice);                         // blank
               tempSB.append("2");                               // player status (1 = main member, 2 = reg member or guest)
               tempSB.append(address);                           // blank
               tempSB.append(city);                              // blank
               tempSB.append(state);                             // blank
               tempSB.append(zip);                               // blank
               tempSB.append(email);                             // blank
               tempSB.append(instruct);                          // blank
               tempSB.append("\r\n");                            // EOF = CR LF

               line = tempSB.toString();                         // save as string value

               out.print(line);
               out.println();      // output the line

               done = 1;           // indicate charge sent

            }      // end of Mship Charge processing

         } else {

            //
            //  player passed is a guest - charge the member for this too
            //
            //
            //  First check if there is a charge amount associated with this guest type
            //
            i = 0;
            item = "";
            loop3:
            while (i < parmp.MAX_Guests) {

               if (parmp.player.startsWith( parmp.gtype[i] )) {

                  gtype = parmp.gtype[i];               // set guest type description
                  if (parmp.p9 == 1) {                   // if 9 holes
                     item = parmp.gst9I[i];                 // set guest item group #
                  } else {
                     item = parmp.gstI[i];                 // set guest item group #
                  }
                  break loop3;
               }
               i++;
            }

            if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found

               //
               //  Build the Item/SKU field (12 chars)
               //
               len = item.length();                   // get length of item code

               if (len < 12) {                         // if < 12 characters

                  while (len < 12) {

                     item = item + " ";             // add a space filler
                     len++;
                  }

               } else {

                  if (len > 12) {                         // if > 12 characters

                     while (len > 12) {

                        item = stripOne(item);         // strip the first char
                        len--;
                     }
                  }
               }

               //
               //  We can now build the charge string
               //
               StringBuffer tempSB = new StringBuffer(resid);    // put reservation id in string buffer
               tempSB.append(ttid);                              // player id
               tempSB.append(courseid);                          // course id
               tempSB.append(course);                            // course name
               tempSB.append(parmp.sdate);                       // date - mm/dd/yyyy
               tempSB.append(parmp.stime);                       // tee time - hh:mm:ss AM (or PM)
               tempSB.append(posid);                             // POS ID
               tempSB.append(fname);                             // first name
               tempSB.append(mi);                                // mi
               tempSB.append(lname);                             // last name
               tempSB.append(itemName);                          // blank
               tempSB.append(item);                              // item code (SKU)
               tempSB.append(itemPrice);                         // blank
               tempSB.append("2");                               // player status (1 = main member, 2 = reg member or guest)
               tempSB.append(address);                           // blank
               tempSB.append(city);                              // blank
               tempSB.append(state);                             // blank
               tempSB.append(zip);                               // blank
               tempSB.append(email);                             // blank
               tempSB.append(instruct);                          // blank
               tempSB.append("\r\n");                            // EOF = CR LF

               line = tempSB.toString();                         // save as string value

               out.print(line);
               out.println();      // output the line

               done = 1;           // indicate charge sent
            }
         }     // end of guest processing

      }
      catch (Exception e2) {

         String errorMsg2 = "Error2 in Proshop_sheet buildCSG for club: " +club;
         errorMsg2 = errorMsg2 + ", Exception: " + e2.getMessage();                 // build error msg

         SystemUtils.logError(errorMsg2);                                           // log it
      }
   }

   return(done);
 }                   // end of buildCSG


 // ********************************************************************
 //  Process the NorthStar POS charges for an individual member
 //
 //  Build an ASCII file containing the following (this will build one record):
 //
 //
 //      mm/dd/yyyy hh:mm:ss                    (current date & time)
 //      mNum, mm/dd/yyyy, pos item#, quantity  (onoe record per charge) 
 //      #                                      (total # of records included)
 //      EOF
 //
 //
 //    Check the mode of trans for charges
 // ********************************************************************

 public int buildLineNS(parmPOS parmp, String filename, PrintWriter out, Connection con) {


   ResultSet rs = null;

   String mship = "";
   String posid = "";
   String fname = "";
   String lname = "";
   String tpos = "";
   String mpos = "";
   String mposc = "";
   String gpos = "";
   String gtype = "";
   String tmode = "";
   String tmodea = "";
   String item = "";
   String line = "";

   int i = 0;
   int p9c = 0;
   int done = 0;


   try {
      //
      //  get the member's mship info
      //
      PreparedStatement pstmtc = con.prepareStatement (
         "SELECT name_last, name_first, m_ship, posid FROM member2b WHERE username= ?");

      pstmtc.clearParameters();        // clear the parms
      pstmtc.setString(1, parmp.user);

      rs = pstmtc.executeQuery();

      if (rs.next()) {

         lname = rs.getString(1);
         fname = rs.getString(2);
         mship = rs.getString(3);
         posid = rs.getString(4);
      }
      pstmtc.close();

      //
      //  First check if there is a charge amount associated with this player's mode of trans
      //
      i = 0;
      loop1:
      while (i < parmp.MAX_Tmodes) {

         if (parmp.tmodea[i].equals( parmp.pcw )) {     // if matching mode of trans found

            tmode = parmp.tmode[i];                // get full description of tmode
            if (parmp.p9 == 1) {                   // if 9 holes
               item = parmp.t9pos[i];              // get Item Group # for tmode
            } else {
               item = parmp.tpos[i];               // get Item Group # for tmode
            }
            break loop1;
         }
         i++;
      }

      if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for Mode of Trans selected

         if (parmp.count == 0) {                         // if first item to be charged
           
            addHdrNS(parmp, filename);                   // go create the file and build the header
         }

         //
         //  We can now build the charge string
         //
         StringBuffer tempSB = new StringBuffer(posid);     // put member's posid in string buffer
         tempSB.append(",");                              
         tempSB.append(parmp.sdate);                        // date (mm/dd/yyyy) 
         tempSB.append(",");
         tempSB.append(item);                               // item's POS Id
         tempSB.append(",1");                               // quantity = 1

         line = tempSB.toString();                         // save as string value

         addLineNS(filename, line);                        // go add this record

         parmp.count++;      // bump record counter

         done = 1;           // indicate charge sent
      }

      //
      //  get the mship class and charge amount, if any and if player is a member!
      //
      if (parmp.player.equals( "" )) {     // if player is a member

         i = 0;
         item = "";
         loop2:
         while (i < parmp.MAX_Mships) {

            if (parmp.mship[i].equalsIgnoreCase( mship )) {     // if matching mode mship type

               if (parmp.p9 == 1) {                   // if 9 holes
                  item = parmp.mship9I[i];               // get mship item group #
               } else {
                  item = parmp.mshipI[i];               // get mship item group #
               }
               break loop2;
            }
            i++;
         }

         if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found for membership (non-golf mship charge)

            if (parmp.count == 0) {                         // if first item to be charged

               addHdrNS(parmp, filename);                   // go create the file and build the header
            }

            //
            //  We can now build the charge string
            //
            StringBuffer tempSB = new StringBuffer(posid);     // put member's posid in string buffer
            tempSB.append(",");
            tempSB.append(parmp.sdate);                        // date (mm/dd/yyyy)
            tempSB.append(",");
            tempSB.append(item);                               // item's POS Id
            tempSB.append(",1");                               // quantity = 1

            line = tempSB.toString();                         // save as string value

            addLineNS(filename, line);                        // go add this record

            parmp.count++;      // bump record counter

            done = 1;           // indicate charge sent
              
         }      // end of Mship Charge processing
           
      } else {

         //
         //  player passed is a guest - charge the member for this too
         //

         //
         //  First check if there is a charge amount associated with this guest type
         //
         i = 0;
         item = "";
         loop3:
         while (i < parmp.MAX_Guests) {

            if (parmp.player.startsWith( parmp.gtype[i] )) {

               gtype = parmp.gtype[i];               // set guest type description
               if (parmp.p9 == 1) {                   // if 9 holes
                  item = parmp.gst9I[i];                 // set guest item group #
               } else {
                  item = parmp.gstI[i];                 // set guest item group #
               }
               break loop3;
            }
            i++;
         }

         if (!item.equals( "" ) && !item.equals( "0" )) {   // if pos charge found

            if (parmp.count == 0) {                         // if first item to be charged

               addHdrNS(parmp, filename);                   // go create the file and build the header
            }

            //
            //  We can now build the charge string
            //
            StringBuffer tempSB = new StringBuffer(posid);     // put member's posid in string buffer
            tempSB.append(",");
            tempSB.append(parmp.sdate);                        // date (mm/dd/yyyy)
            tempSB.append(",");
            tempSB.append(item);                               // item's POS Id
            tempSB.append(",1");                               // quantity = 1

            line = tempSB.toString();                         // save as string value

            addLineNS(filename, line);                        // go add this record

            parmp.count++;      // bump record counter

            done = 1;           // indicate charge sent
         }
      }     // end of guest processing

   }
   catch (Exception e1) {

      String errorMsg1 = "Error1 in Proshop_sheet buildLineNS: ";
      errorMsg1 = errorMsg1 + e1.getMessage();                                // build error msg

      SystemUtils.logError(errorMsg1);                                       // log it
   }

   return(done);
 }                   // end of buildLineNS


 // ********************************************************************
 //
 //  NorthStar POS - Create a new ASCII file and add the header.
 //
 // ********************************************************************

 public void addHdrNS(parmPOS parmp, String filename) {


    addLineNS(filename, parmp.sdate);                // go create file and add header

    //
    //  Now strip the time stamp from sdate so we can use the date for each record
    //
    //   "mm/dd/yyyy hh:mm:ss" -> "mm/dd/yyyy"
    //
    StringTokenizer tok = new StringTokenizer( parmp.sdate, " " );     // delimiters are space

    parmp.sdate = tok.nextToken();        // date only
 }
   

 // ********************************************************************
 //  Print an Individual Tee Time (prtTeeTime=yes)
 // ********************************************************************

 private void printTeeTime(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session, 
                           Connection con) {


   ResultSet rs = null;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String day = "";
   String ampm = " AM";
   String course = "";
   String fbs = "";
   String dates = "";
   String times = "";

   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   int fb = 0;
   int time = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;

   long date = 0;

   //
   //  get the club name from the session
   //
   String club = (String)session.getAttribute("club");      // get club name

   //
   //  Get the tee time parms passed
   //
   course = req.getParameter("course");
   dates = req.getParameter("date");
   times = req.getParameter("time");
   fbs = req.getParameter("fb");

   fb = Integer.parseInt(fbs);
   time = Integer.parseInt(times);
   date = Long.parseLong(dates);


   try {

      //
      //  Get the tee time info for the time requested and display it
      //
      PreparedStatement pstmt2s = con.prepareStatement (
         "SELECT * " +
         "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt2s.clearParameters();        // clear the parms
      pstmt2s.setLong(1, date);
      pstmt2s.setInt(2, time);
      pstmt2s.setInt(3, fb);
      pstmt2s.setString(4, course);

      rs = pstmt2s.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         mm = rs.getInt("mm");
         dd = rs.getInt("dd");
         yy = rs.getInt("yy");
         day = rs.getString("day");
         hr = rs.getInt("hr");
         min = rs.getInt("min");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         p1cw = rs.getString("p1cw");
         p2cw = rs.getString("p2cw");
         p3cw = rs.getString("p3cw");
         p4cw = rs.getString("p4cw");
         show1 = rs.getInt("show1");
         show2 = rs.getInt("show2");
         show3 = rs.getInt("show3");
         show4 = rs.getInt("show4");
         player5 = rs.getString("player5");
         p5cw = rs.getString("p5cw");
         show5 = rs.getInt("show5");
         mNum1 = rs.getString("mNum1");
         mNum2 = rs.getString("mNum2");
         mNum3 = rs.getString("mNum3");
         mNum4 = rs.getString("mNum4");
         mNum5 = rs.getString("mNum5");
         p91 = rs.getInt("p91");
         p92 = rs.getInt("p92");
         p93 = rs.getInt("p93");
         p94 = rs.getInt("p94");
         p95 = rs.getInt("p95");
      }
        
      pstmt2s.close();

   }
   catch (Exception e1) {

      String errorMsg1 = "Error1 in Proshop_sheet printTeeTime: ";
      errorMsg1 = errorMsg1 + e1.getMessage();                                // build error msg

      SystemUtils.logError(errorMsg1);                                       // log it

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error in Proshop_sheet printTeeTime.");
      out.println("<BR>Error:" + e1.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact ForeTees support (provide this information).");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</center></font></body></html>");
      out.close();
      return;
   }

   //
   //  Create time value
   //
   if (hr == 0) {
      hr = 12;                 // change to 12 AM (midnight)
   } else {
      if (hr == 12) {
         ampm = " PM";         // change to Noon
      }
   }
   if (hr > 12) {
      hr = hr - 12;
      ampm = " PM";             // change to 12 hr clock
   }

   fbs = "Front";
     
   if (fb == 1) {
     
      fbs = "Back";
   }

   if (player1.equals( "" ) || player1 == null) {
     
      player1 = "&nbsp;";
      p1cw = "&nbsp;";
      mNum1 = "&nbsp;";
      p91 = 0;
      show1 = 0;
   }
   if (player2.equals( "" ) || player2 == null) {

      player2 = "&nbsp;";
      p2cw = "&nbsp;";
      mNum2 = "&nbsp;";
      p92 = 0;
      show2 = 0;
   }
   if (player3.equals( "" ) || player3 == null) {

      player3 = "&nbsp;";
      p3cw = "&nbsp;";
      mNum3 = "&nbsp;";
      p93 = 0;
      show3 = 0;
   }
   if (player4.equals( "" ) || player4 == null) {

      player4 = "&nbsp;";
      p4cw = "&nbsp;";
      mNum4 = "&nbsp;";
      p94 = 0;
      show4 = 0;
   }

   if (mNum1.equals( "" ) || mNum1 == null) {

      mNum1 = "&nbsp;";
   }
   if (mNum2.equals( "" ) || mNum2 == null) {

      mNum2 = "&nbsp;";
   }
   if (mNum3.equals( "" ) || mNum3 == null) {

      mNum3 = "&nbsp;";
   }
   if (mNum4.equals( "" ) || mNum4 == null) {

      mNum4 = "&nbsp;";
   }
   if (mNum5.equals( "" ) || mNum5 == null) {

      mNum5 = "&nbsp;";
   }

   if (p91 == 1) {
      p1cw = p1cw + "9";
   }
   if (p92 == 1) {
      p2cw = p2cw + "9";
   }
   if (p93 == 1) {
      p3cw = p3cw + "9";
   }
   if (p94 == 1) {
      p4cw = p4cw + "9";
   }
   if (p95 == 1) {
      p5cw = p5cw + "9";
   }


   //
   //  Output the page to display the tee time (new window)
   //
   out.println(SystemUtils.HeadTitle("Proshop Tee Time Display"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<font size=\"3\">");
   out.println("<b>Individual Tee Time Display</b>");
   out.println("</font>");
   out.println("<font size=\"2\"><br><br>");
   out.println("<b>Date:</b>&nbsp;&nbsp;" +day+ ",&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy);
   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("<b>Time:</b>&nbsp;&nbsp; " + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm);
   out.println("<br><br>");

   if (!course.equals( "" )) {

      out.println("<b>Course:</b>&nbsp;&nbsp; " + course);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
   }

   out.println("<b>Front/Back Nine:</b>&nbsp;&nbsp; " + fbs + "<br><br>");

   out.println("<table border=\"1\" valign=\"top\" cellpadding=\"5\">");       // table for player info
   out.println("<tr>");
   out.println("<td align=\"left\">");
   out.println("<font size=\"2\">");
   out.println("<b>Player</b>");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println("<b>Member #</b>");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println("<b>Mode of Trans</b>");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println("<b>Checked In?</b>");
   out.println("</font></td>");
   out.println("</tr>");

   out.println("<tr>");
   out.println("<td align=\"left\">");
   out.println("<font size=\"2\">");
   out.println(player1);
   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println(mNum1);
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println(p1cw);
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   if (!player1.equals( "&nbsp;" )) {
      if (show1 == 1) {
         out.println("Y");
      } else {
         out.println("N");
      }
   } else {
      out.println("&nbsp;");
   }
   out.println("</font></td>");
   out.println("</tr>");

   out.println("<tr>");
   out.println("<td align=\"left\">");
   out.println("<font size=\"2\">");
   out.println(player2);
   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println(mNum2);
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println(p2cw);
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   if (!player2.equals( "&nbsp;" )) {
      if (show2 == 1) {
         out.println("Y");
      } else {
         out.println("N");
      }
   } else {
      out.println("&nbsp;");
   }
   out.println("</font></td>");
   out.println("</tr>");

   out.println("<tr>");
   out.println("<td align=\"left\">");
   out.println("<font size=\"2\">");
   out.println(player3);
   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println(mNum3);
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println(p3cw);
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   if (!player3.equals( "&nbsp;" )) {
      if (show3 == 1) {
         out.println("Y");
      } else {
         out.println("N");
      }
   } else {
      out.println("&nbsp;");
   }
   out.println("</font></td>");
   out.println("</tr>");

   out.println("<tr>");
   out.println("<td align=\"left\">");
   out.println("<font size=\"2\">");
   out.println(player4);
   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println(mNum4);
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println(p4cw);
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   if (!player4.equals( "&nbsp;" )) {
      if (show4 == 1) {
         out.println("Y");
      } else {
         out.println("N");
      }
   } else {
      out.println("&nbsp;");
   }
   out.println("</font></td>");
   out.println("</tr>");

   if (!player5.equals( "" ) && player5 != null) {

      out.println("<tr>");
      out.println("<td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println(player5);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println(mNum5);
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println(p5cw);
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      if (show5 == 1) {
         out.println("Y");
      } else {
         out.println("N");
      }
      out.println("</font></td>");
      out.println("</tr>");
   }

   out.println("</table><br>");
   out.println("<table border=\"0\" valign=\"top\">");       // table for main page
   out.println("<tr><td align=\"center\">");
   out.println("<form>");
   out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"Close\" onClick='self.close()' alt=\"Close\">");
   out.println("</form>");
   out.println("</td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
   out.println("</td><td align=\"center\">");
   out.println("<form method=\"link\" action=\"javascript:self.print()\">");
   out.println("<button type=\"submit\" style=\"text-decoration:underline; background:#8B8970\">Print</button>");
   out.println("</form>");
   out.println("</td></tr></table>");
   out.println("</center></font></body></html>");
   out.close();
   return;
 }                   // end of printTeeTime


 // ******************************************************************************
 //  Get a member's bag slot number
 // ******************************************************************************

 private String getBag(String user, Connection con) { 


   ResultSet rs = null;

   String bag = "";
   

   try {
     
      if (!user.equals( "" )) {

         PreparedStatement pstmte1 = con.prepareStatement (
                  "SELECT bag FROM member2b WHERE username = ?");

         pstmte1.clearParameters();        // clear the parms
         pstmte1.setString(1, user);
         rs = pstmte1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            bag = rs.getString(1);         // user's bag room slot#
         }

         pstmte1.close();                  // close the stmt
      }

   }
   catch (Exception ignore) {
   }

   return(bag);
     
 }                   // end of getBag


 // ******************************************************************************
 //  Interlachen & Cherry Hills Custom - update # of caddies assigned to group
 // ******************************************************************************

 private void updateCaddies(String stime, String sfb, long date, String numCaddies, PrintWriter out, Connection con) {


   //
   //  Convert the common string values to int's
   //
   int caddies = Integer.parseInt(numCaddies);
   int time = Integer.parseInt(stime);
   short fb = Short.parseShort(sfb);

   if (caddies < 6) {     // ensure correct values
     
      try {

         PreparedStatement pstmt3 = con.prepareStatement (
              "UPDATE teecurr2 SET hotelNew = ? " +
              "WHERE date = ? AND time = ? AND fb = ?");

          //
          //  execute the prepared statement to update the tee time slot
          //
          pstmt3.clearParameters();        // clear the parms
          pstmt3.setInt(1, caddies);
          pstmt3.setLong(2, date);
          pstmt3.setInt(3, time);
          pstmt3.setInt(4, fb);
          pstmt3.executeUpdate();

          pstmt3.close();

      }
      catch (Exception ignore) {
      }
   }
 }      // end of updateCaddies


 // **************************************************************************************
 //  Pecan Plantation and The Ranch Custom - update 'teed off' indicator for group
 // **************************************************************************************

 private void updateTeedOff(String stime, String sfb, long date, String teedOff, PrintWriter out, Connection con) {


   int teed = 0;
     
   //
   //  Convert the common string values to int's
   //
   int time = Integer.parseInt(stime);
   short fb = Short.parseShort(sfb);

   if (teedOff.equalsIgnoreCase( "x" )) {     // if 'teed off' specified

      teed = 1;
        
   } else {

      if (teedOff.equalsIgnoreCase( "o" )) {     // if 'on deck' specified

         teed = 2;
      }
   }

   try {

      PreparedStatement pstmt3 = con.prepareStatement (
           "UPDATE teecurr2 SET hotelNew = ? " +
           "WHERE date = ? AND time = ? AND fb = ?");

       //
       //  execute the prepared statement to update the tee time slot
       //
       pstmt3.clearParameters();        // clear the parms
       pstmt3.setInt(1, teed);
       pstmt3.setLong(2, date);
       pstmt3.setInt(3, time);
       pstmt3.setInt(4, fb);
       pstmt3.executeUpdate();

       pstmt3.close();

   }
   catch (Exception ignore) {
   }
     
 }      // end of updateCaddies


 // *********************************************************
 //  Cordillera Custom - update the forecaddie indicator for group
 // *********************************************************

 private void updateForeCaddie(String stime, String sfb, long date, String foreCaddie, String course, 
                               PrintWriter out, Connection con) {


   //
   //  Convert the common string values to int's
   //
   int fc = 0;
   int time = Integer.parseInt(stime);
   short fb = Short.parseShort(sfb);

   if (foreCaddie.equals( "Y" )) {     // if ForeCaddies = Yes
     
      fc = 1;
   }

   try {

      PreparedStatement pstmt3 = con.prepareStatement (
           "UPDATE teecurr2 SET pos5 = ? " +
           "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

       //
       //  execute the prepared statement to update the tee time slot
       //
       pstmt3.clearParameters();        // clear the parms
       pstmt3.setInt(1, fc);
       pstmt3.setLong(2, date);
       pstmt3.setInt(3, time);
       pstmt3.setInt(4, fb);
       pstmt3.setString(5, course);
       pstmt3.executeUpdate();

       pstmt3.close();

   }
   catch (Exception ignore) {
   }
     
 }      // end of updateForeCaddie


 // *********************************************************
 //  Display event information in new pop-up window
 // *********************************************************

 private void displayEvent(String name, PrintWriter out, Connection con) {

   ResultSet rs = null;

   int year = 0;
   int month = 0;
   int day = 0;
   int act_hr = 0;
   int act_min = 0;
   int signUp = 0;
   int type = 0;
   int holes = 0;
   int max = 0;
   int size = 0;
   int guests = 0;
   int teams = 0;
   int c_month = 0;
   int c_day = 0;
   int c_year = 0;
   int c_time = 0;
   int c_hr = 0;
   int c_min = 0;

   String course = "";
   String format = "";
   String pairings = "";
   String memcost = "";
   String gstcost = "";
   String itin = "";
   String c_ampm = "";
   String act_ampm = "";
   String fb = "";

   //
   //  Locate the event and display the content
   //
   try {

      PreparedStatement stmt = con.prepareStatement (
         "SELECT * FROM events2b " +
         "WHERE name = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);
      rs = stmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         year = rs.getInt("year");
         month = rs.getInt("month");
         day = rs.getInt("day");
         type = rs.getInt("type");
         act_hr = rs.getInt("act_hr");
         act_min = rs.getInt("act_min");
         course = rs.getString("courseName");
         signUp = rs.getInt("signUp");
         format = rs.getString("format");
         pairings = rs.getString("pairings");
         size = rs.getInt("size");
         max = rs.getInt("max");
         guests = rs.getInt("guests");
         memcost = rs.getString("memcost");
         gstcost = rs.getString("gstcost");
         c_month = rs.getInt("c_month");
         c_day = rs.getInt("c_day");
         c_year = rs.getInt("c_year");
         c_time = rs.getInt("c_time");
         itin = rs.getString("itin");
         holes = rs.getInt("holes");
         fb = rs.getString("fb");

      } else {           // name not found - try filtering it

         name = SystemUtils.filter(name);

         stmt.clearParameters();        // clear the parms
         stmt.setString(1, name);
         rs = stmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            year = rs.getInt("year");
            month = rs.getInt("month");
            day = rs.getInt("day");
            type = rs.getInt("type");
            act_hr = rs.getInt("act_hr");
            act_min = rs.getInt("act_min");
            course = rs.getString("courseName");
            signUp = rs.getInt("signUp");
            format = rs.getString("format");
            pairings = rs.getString("pairings");
            size = rs.getInt("size");
            max = rs.getInt("max");
            guests = rs.getInt("guests");
            memcost = rs.getString("memcost");
            gstcost = rs.getString("gstcost");
            c_month = rs.getInt("c_month");
            c_day = rs.getInt("c_day");
            c_year = rs.getInt("c_year");
            c_time = rs.getInt("c_time");
            itin = rs.getString("itin");
            holes = rs.getInt("holes");
            fb = rs.getString("fb");
         }
      }
      stmt.close();

      //
      //  Create time values
      //
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

      c_hr = c_time / 100;
      c_min = c_time - (c_hr * 100);

      c_ampm = "AM";

      if (c_hr == 0) {

         c_hr = 12;                 // change to 12 AM (midnight)

      } else {

         if (c_hr == 12) {

            c_ampm = "PM";         // change to Noon
         }
      }
      if (c_hr > 12) {

         c_hr = c_hr - 12;
         c_ampm = "PM";             // change to 12 hr clock
      }

      //
      //   Build the html page
      //
      out.println(SystemUtils.HeadTitle("Proshop Event Information"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\" valign=\"top\">");

      out.println("<font size=\"3\">");
      out.println("Event: <b>" + name + "</b>");
      out.println("</font></td></tr>");
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\"><br><br>");
      out.println("<b>Date:</b>&nbsp;&nbsp; " + month + "/" + day + "/" + year);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (act_min < 10) {
         out.println("<b>Time:</b>&nbsp;&nbsp; " + act_hr + ":0" + act_min + " " + act_ampm);
      } else {
         out.println("<b>Time:</b>&nbsp;&nbsp; " + act_hr + ":" + act_min + " " + act_ampm);
      }
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (type != 0) {
         out.println("<b>Type:</b>&nbsp;&nbsp; Shotgun<br><br>");
      } else {
         out.println("<b>Type:</b>&nbsp;&nbsp; Tee Times<br><br>");
      }

      if (!course.equals( "" )) {

         out.println("<b>Course:</b>&nbsp;&nbsp; " + course);
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      }

      out.println("<b>Front/Back Tees:</b>&nbsp;&nbsp; " + fb + "<br><br>");

      if (signUp != 0) {       // if members can sign up

         out.println("<b>Format:</b>&nbsp;&nbsp; " + format + "<br><br>");
         out.println("<b>Pairings by:</b>&nbsp;&nbsp; " + pairings);
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("<b># of Teams:</b>&nbsp;&nbsp; " + max);
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("<b>Team Size:</b>&nbsp;&nbsp; " + size);
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("<b>Holes:</b>&nbsp;&nbsp; " + holes + "<br><br>");
         out.println("<b>Guests per Member:</b>&nbsp;&nbsp;" + guests);
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("<b>Cost per Guest:</b>&nbsp;&nbsp;" + gstcost);
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("<b>Cost per Member:</b>&nbsp;&nbsp;" + memcost + "<br><br>");
         if (c_min < 10) {
            out.println("<b>Must Sign Up By:</b>&nbsp;&nbsp; " + c_hr + ":0" + c_min + " " + c_ampm +  " on " + c_month + "/" + c_day + "/" + c_year);
         } else {
            out.println("<b>Must Sign Up By:</b>&nbsp;&nbsp; " + c_hr + ":" + c_min + " " + c_ampm +  " on " + c_month + "/" + c_day + "/" + c_year);
         }
         out.println("<br><br>");
         out.println("<b>Itinerary:</b>&nbsp;&nbsp; " + itin + "<br>");

      } else {
         out.println("<b>Itinerary:</b>&nbsp;&nbsp; " + itin + "<br><br>");
         out.println("Online sign up was not selected for this event.");
      }

      //
      //  End of HTML page
      //
      out.println("</font></td></tr></table><br>");
      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\">");
      out.println("<form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</td><td>&nbsp;&nbsp;");
      out.println("</td><td align=\"center\">");
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"sheet\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Edit Event\">");
      out.println("</form>");
      out.println("</td></tr></table>");
      out.println("</center></font></body></html>");
      out.close();

   }
   catch (Exception exc) {
      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact ForeTees support (provide this information).");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</center></font></body></html>");
      out.close();
   }
 }


 // *********************************************************
 //  Display restriction information in new pop-up window
 // *********************************************************

 private void displayRest(String name, PrintWriter out, Connection con) {

   ResultSet rs = null;

   int year1 = 0;
   int month1 = 0;
   int day1 = 0;
   int year2 = 0;
   int month2 = 0;
   int day2 = 0;
   int hr1 = 0;
   int min1 = 0;
   int hr2 = 0;
   int min2 = 0;
   int i = 0;

   String course = "";
   String recurr = "";
   String fb = "";
   String ampm1 = "AM";
   String ampm2 = "AM";

   String [] mtype = new String [8];                     // member types
   String [] mship = new String [8];                     // membership types

   //
   //  Locate the event and display the content
   //
   try {

      PreparedStatement stmt = con.prepareStatement (
         "SELECT * FROM restriction2 " +
         "WHERE name = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);
      rs = stmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         month1 = rs.getInt("start_mm");
         day1 = rs.getInt("start_dd");
         year1 = rs.getInt("start_yy");
         hr1 = rs.getInt("start_hr");
         min1 = rs.getInt("start_min");
         month2 = rs.getInt("end_mm");
         day2 = rs.getInt("end_dd");
         year2 = rs.getInt("end_yy");
         hr2 = rs.getInt("end_hr");
         min2 = rs.getInt("end_min");
         recurr = rs.getString("recurr");
         mtype[0] = rs.getString("mem1");
         mtype[1] = rs.getString("mem2");
         mtype[2] = rs.getString("mem3");
         mtype[3] = rs.getString("mem4");
         mtype[4] = rs.getString("mem5");
         mtype[5] = rs.getString("mem6");
         mtype[6] = rs.getString("mem7");
         mtype[7] = rs.getString("mem8");
         mship[0] = rs.getString("mship1");
         mship[1] = rs.getString("mship2");
         mship[2] = rs.getString("mship3");
         mship[3] = rs.getString("mship4");
         mship[4] = rs.getString("mship5");
         mship[5] = rs.getString("mship6");
         mship[6] = rs.getString("mship7");
         mship[7] = rs.getString("mship8");
         course = rs.getString("courseName");
         fb = rs.getString("fb");

      } else {         // not found - try filtering the name

         name = SystemUtils.filter(name);

         stmt.clearParameters();        // clear the parms
         stmt.setString(1, name);
         rs = stmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            month1 = rs.getInt("start_mm");
            day1 = rs.getInt("start_dd");
            year1 = rs.getInt("start_yy");
            hr1 = rs.getInt("start_hr");
            min1 = rs.getInt("start_min");
            month2 = rs.getInt("end_mm");
            day2 = rs.getInt("end_dd");
            year2 = rs.getInt("end_yy");
            hr2 = rs.getInt("end_hr");
            min2 = rs.getInt("end_min");
            recurr = rs.getString("recurr");
            mtype[0] = rs.getString("mem1");
            mtype[1] = rs.getString("mem2");
            mtype[2] = rs.getString("mem3");
            mtype[3] = rs.getString("mem4");
            mtype[4] = rs.getString("mem5");
            mtype[5] = rs.getString("mem6");
            mtype[6] = rs.getString("mem7");
            mtype[7] = rs.getString("mem8");
            mship[0] = rs.getString("mship1");
            mship[1] = rs.getString("mship2");
            mship[2] = rs.getString("mship3");
            mship[3] = rs.getString("mship4");
            mship[4] = rs.getString("mship5");
            mship[5] = rs.getString("mship6");
            mship[6] = rs.getString("mship7");
            mship[7] = rs.getString("mship8");
            course = rs.getString("courseName");
            fb = rs.getString("fb");
         }
      }
      stmt.close();

      //
      //  Create time values
      //
      if (hr1 == 0) {
         hr1 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr1 == 12) {
            ampm1 = "PM";         // change to Noon
         }
      }
      if (hr1 > 12) {
         hr1 = hr1 - 12;
         ampm1 = "PM";             // change to 12 hr clock
      }

      if (hr2 == 0) {
         hr2 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr2 == 12) {
            ampm2 = "PM";         // change to Noon
         }
      }
      if (hr2 > 12) {
         hr2 = hr2 - 12;
         ampm2 = "PM";             // change to 12 hr clock
      }

      //
      //   Build the html page
      //
      out.println(SystemUtils.HeadTitle("Proshop Restriction Information"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
        
      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\" valign=\"top\">");
      out.println("<font size=\"3\">");
      out.println("Restriction: <b>" + name + "</b>");
      out.println("</font></td></tr>");
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\"><br><br>");
      out.println("<b>Start Date of Restriction:</b>&nbsp;&nbsp; " + month1 + "/" + day1 + "/" + year1);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (min1 < 10) {
         out.println("<b>Start Time:</b>&nbsp;&nbsp; " + hr1 + ":0" + min1 + " " + ampm1);
      } else {
         out.println("<b>Start Time:</b>&nbsp;&nbsp; " + hr1 + ":" + min1 + " " + ampm1);
      }
      out.println("<br><br><b>End Date of Restriction:</b>&nbsp;&nbsp; " + month2 + "/" + day2 + "/" + year2);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (min2 < 10) {
         out.println("<b>End Time:</b>&nbsp;&nbsp; " + hr2 + ":0" + min2 + " " + ampm2);
      } else {
         out.println("<b>End Time:</b>&nbsp;&nbsp; " + hr2 + ":" + min2 + " " + ampm2);
      }

      out.println("<br><br><b>Recurrence:</b>&nbsp;&nbsp; " +recurr+ "<br><br>");

      if (!course.equals( "" )) {

         out.println("<b>Course:</b>&nbsp;&nbsp; " + course);
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      }

      out.println("<b>Front/Back Tees:</b>&nbsp;&nbsp; " + fb + "<br><br>");

      // if any member types specified
      if (!mtype[0].equals("") || !mtype[1].equals("") || !mtype[2].equals("") || !mtype[3].equals("") ||
          !mtype[4].equals("") || !mtype[5].equals("") || !mtype[6].equals("") || !mtype[7].equals("")) {

         out.println("<b>Member Types Restricted:</b>");
         if (!mtype[0].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[0]);
         }
         if (!mtype[1].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[1]);
         }
         if (!mtype[2].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[2]);
         }
         if (!mtype[3].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[3]);
         }
         if (!mtype[4].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[4]);
         }
         if (!mtype[5].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[5]);
         }
         if (!mtype[6].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[6]);
         }
         if (!mtype[7].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[7]);
         }
         out.println("<br><br>");
      }

      // if any membership types specified
      if (!mship[0].equals("") || !mship[1].equals("") || !mship[2].equals("") || !mship[3].equals("") ||
          !mship[4].equals("") || !mship[5].equals("") || !mship[6].equals("") || !mship[7].equals("")) {

         out.println("<b>Membership Types Restricted:</b>");
         if (!mship[0].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[0]);
         }
         if (!mship[1].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[1]);
         }
         if (!mship[2].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[2]);
         }
         if (!mship[3].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[3]);
         }
         if (!mship[4].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[4]);
         }
         if (!mship[5].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[5]);
         }
         if (!mship[6].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[6]);
         }
         if (!mship[7].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[7]);
         }
         out.println("<br><br>");
      }

      //
      //  End of HTML page
      //
      out.println("</font></td></tr></table><br>");
      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\">");
      out.println("<form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</td><td>&nbsp;&nbsp;");
      out.println("</td><td align=\"center\">");
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_mrest\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"sheet\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Edit Restriction\">");
      out.println("</form>");
      out.println("</td></tr></table>");
      out.println("</center></font></body></html>");
      out.close();

   }
   catch (Exception exc) {
      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact ForeTees support (provide this information).");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</center></font></body></html>");
      out.close();
   }
 }


 // *********************************************************
 //  Display Lottery information in new pop-up window
 // *********************************************************

 private void displayLottery(String name, PrintWriter out, Connection con) {

   ResultSet rs = null;

   int year1 = 0;
   int month1 = 0;
   int day1 = 0;
   int year2 = 0;
   int month2 = 0;
   int day2 = 0;
   int hr1 = 0;
   int min1 = 0;
   int hr2 = 0;
   int min2 = 0;
   int hr3 = 0;
   int min3 = 0;
   int hr4 = 0;
   int min4 = 0;
   int hr5 = 0;
   int min5 = 0;
   int sdays = 0;
   int edays = 0;
   int pdays = 0;
   int slots = 0;
   int players = 0;
   int members = 0;

   String course = "";
   String recurr = "";
   String fb = "";
   String ampm1 = "AM";
   String ampm2 = "AM";
   String ampm3 = "AM";
   String ampm4 = "AM";
   String ampm5 = "AM";

   //
   //  Locate the lottery and display the content
   //
   try {

      PreparedStatement stmt = con.prepareStatement (
         "SELECT * FROM lottery3 " +
         "WHERE name = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);
      rs = stmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         month1 = rs.getInt("start_mm");
         day1 = rs.getInt("start_dd");
         year1 = rs.getInt("start_yy");
         hr1 = rs.getInt("start_hr");
         min1 = rs.getInt("start_min");
         month2 = rs.getInt("end_mm");
         day2 = rs.getInt("end_dd");
         year2 = rs.getInt("end_yy");
         hr2 = rs.getInt("end_hr");
         min2 = rs.getInt("end_min");
         recurr = rs.getString("recurr");
         course = rs.getString("courseName");
         fb = rs.getString("fb");
         sdays = rs.getInt("sdays");
         hr3 = rs.getInt("sd_hr");
         min3 = rs.getInt("sd_min");
         edays = rs.getInt("edays");
         hr4 = rs.getInt("ed_hr");
         min4 = rs.getInt("ed_min");
         pdays = rs.getInt("pdays");
         hr5 = rs.getInt("p_hr");
         min5 = rs.getInt("p_min");
         slots = rs.getInt("slots");
         members = rs.getInt("members");
         players = rs.getInt("players");

      } else {        // not found - try filtering the name

         name = SystemUtils.filter(name);

         stmt.clearParameters();        // clear the parms
         stmt.setString(1, name);
         rs = stmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            month1 = rs.getInt("start_mm");
            day1 = rs.getInt("start_dd");
            year1 = rs.getInt("start_yy");
            hr1 = rs.getInt("start_hr");
            min1 = rs.getInt("start_min");
            month2 = rs.getInt("end_mm");
            day2 = rs.getInt("end_dd");
            year2 = rs.getInt("end_yy");
            hr2 = rs.getInt("end_hr");
            min2 = rs.getInt("end_min");
            recurr = rs.getString("recurr");
            course = rs.getString("courseName");
            fb = rs.getString("fb");
            sdays = rs.getInt("sdays");
            hr3 = rs.getInt("sd_hr");
            min3 = rs.getInt("sd_min");
            edays = rs.getInt("edays");
            hr4 = rs.getInt("ed_hr");
            min4 = rs.getInt("ed_min");
            pdays = rs.getInt("pdays");
            hr5 = rs.getInt("p_hr");
            min5 = rs.getInt("p_min");
            slots = rs.getInt("slots");
            members = rs.getInt("members");
            players = rs.getInt("players");
         }
      }
      stmt.close();

      //
      //  Create time values
      //
      if (hr1 == 0) {
         hr1 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr1 == 12) {
            ampm1 = "PM";         // change to Noon
         }
      }
      if (hr1 > 12) {
         hr1 = hr1 - 12;
         ampm1 = "PM";             // change to 12 hr clock
      }

      if (hr2 == 0) {
         hr2 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr2 == 12) {
            ampm2 = "PM";         // change to Noon
         }
      }
      if (hr2 > 12) {
         hr2 = hr2 - 12;
         ampm2 = "PM";             // change to 12 hr clock
      }

      if (hr3 == 0) {
         hr3 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr3 == 12) {
            ampm3 = "PM";         // change to Noon
         }
      }
      if (hr3 > 12) {
         hr3 = hr3 - 12;
         ampm3 = "PM";             // change to 12 hr clock
      }

      if (hr4 == 0) {
         hr4 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr4 == 12) {
            ampm4 = "PM";         // change to Noon
         }
      }
      if (hr4 > 12) {
         hr4 = hr4 - 12;
         ampm4 = "PM";             // change to 12 hr clock
      }

      if (hr5 == 0) {
         hr5 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr5 == 12) {
            ampm5 = "PM";         // change to Noon
         }
      }
      if (hr5 > 12) {
         hr5 = hr5 - 12;
         ampm5 = "PM";             // change to 12 hr clock
      }

      //
      //   Build the html page
      //
      out.println(SystemUtils.HeadTitle("Proshop Lottery Information"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\" valign=\"top\">");
      out.println("<font size=\"3\">");
      out.println("Lottery: <b>" + name + "</b>");
      out.println("</font></td></tr>");
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\"><br><br>");
      out.println("</font></td></tr>");
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println("Lottery Period:&nbsp;&nbsp; From<b>&nbsp;&nbsp; " + month1 + "/" + day1 + "/" + year1+ "</b>");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("To<b>&nbsp;&nbsp; " + month2 + "/" + day2 + "/" + year2+ "</b>");
      out.println("<br><br>");
      if (min1 < 10) {
         out.println("Start Time:<b>&nbsp;&nbsp; " + hr1 + ":0" + min1 + " " + ampm1+ "</b>");
      } else {
         out.println("Start Time:<b>&nbsp;&nbsp; " + hr1 + ":" + min1 + " " + ampm1+ "</b>");
      }
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (min2 < 10) {
         out.println("End Time:<b>&nbsp;&nbsp; " + hr2 + ":0" + min2 + " " + ampm2+ "</b>");
      } else {
         out.println("End Time:<b>&nbsp;&nbsp; " + hr2 + ":" + min2 + " " + ampm2+ "</b>");
      }

      out.println("<br><br>Recurrence:<b>&nbsp;&nbsp; " +recurr+ "</b><br><br>");

      if (!course.equals( "" )) {

         out.println("Course:<b>&nbsp;&nbsp; " + course+ "</b>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      }

      out.println("Front/Back Tees:<b>&nbsp;&nbsp; " + fb + "</b><br><br>");

      out.println("Days in Advance to Start Taking Requests:<b>&nbsp;&nbsp; " +sdays+ "</b>");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (min3 < 10) {
         out.println("Time:<b>&nbsp;&nbsp; " + hr3 + ":0" + min3 + " " + ampm3+ "</b>");
      } else {
         out.println("Time:<b>&nbsp;&nbsp; " + hr3 + ":" + min3 + " " + ampm3+ "</b>");
      }
      out.println("<br><br>");

      out.println("Days in Advance to Stop Taking Requests:<b>&nbsp;&nbsp; " +edays+ "</b>");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (min4 < 10) {
         out.println("Time:<b>&nbsp;&nbsp; " + hr4 + ":0" + min4 + " " + ampm4+ "</b>");
      } else {
         out.println("Time:<b>&nbsp;&nbsp; " + hr4 + ":" + min4 + " " + ampm4+ "</b>");
      }
      out.println("<br><br>");

      out.println("Days in Advance to Process Requests:<b>&nbsp;&nbsp; " +pdays+ "</b>");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (min5 < 10) {
         out.println("Time:<b>&nbsp;&nbsp; " + hr5 + ":0" + min5 + " " + ampm5+ "</b>");
      } else {
         out.println("Time:<b>&nbsp;&nbsp; " + hr5 + ":" + min5 + " " + ampm5+ "</b>");
      }

      out.println("<br><br>Number of consecutive tee times member can request:<b>&nbsp;&nbsp; " +slots+ "</b>");
      out.println("<br><br>Minimum number of players per request:<b>&nbsp;&nbsp; " +players+ "</b>");
      out.println("<br><br>Minimum number of members per request:<b>&nbsp;&nbsp; " +members+ "</b>");

      //
      //  End of HTML page
      //
      out.println("</font></td></tr></table><br>");
      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\">");
      out.println("<form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</td><td>&nbsp;&nbsp;");
      out.println("</td><td align=\"center\">");
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_lottery\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"sheet\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Edit Lottery\">");
      out.println("</form>");
      out.println("</td></tr></table>");
      out.println("</center></font></body></html>");
      out.close();

   }
   catch (Exception exc) {
      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact ForeTees support (provide this information).");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</center></font></body></html>");
      out.close();
   }
 }


 // *********************************************************
 //  Display Notes Report in new pop-up window
 // *********************************************************

 private void notesReport(long date, String course, PrintWriter out, Connection con) {

   ResultSet rs = null;

   String fbs = "";
   String notes = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String ampm = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String courseNameT = "";
     
   int hr = 0;
   int min = 0;
   int fb = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;


   //
   //  Display all tee times that contain notes
   //
   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" valign=\"top\">");
   out.println("<tr bgcolor=\"#336633\">");
   out.println("<td align=\"left\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<u><b>Time</b></u> ");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<u><b>F/B</b></u> ");
   out.println("</font></td>");
   if (course.equals( "-ALL-" )) {
      out.println("<td align=\"left\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<u><b>Course</b></u> ");
      out.println("</font></td>");
   }
   out.println("<td align=\"left\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<u><b>Players</b></u> ");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<u><b>C/W</b></u> ");
   out.println("</font></td>");
   if (course.equals( "-ALL-" )) {
      out.println("<td align=\"left\" width=\"300\">");
   } else {
      out.println("<td align=\"left\" width=\"380\">");
   }
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<u><b>Notes</b></u> ");
   out.println("</font></td>");
   out.println("</tr>");

   try {

      //
      //  Get the tee sheet for this date
      //
      String stringTee2 = "";

      if (course.equals( "-ALL-" )) {

         stringTee2 = "SELECT " +
                  "hr, min, player1, player2, player3, player4, " +
                  "p1cw, p2cw, p3cw, p4cw, " +
                  "fb, player5, p5cw, notes, courseName, " +
                  "p91, p92, p93, p94, p95 " +
                  "FROM teecurr2 WHERE date = ? AND notes != '' ORDER BY time, courseName, fb";
      } else {
         stringTee2 = "SELECT " +
                  "hr, min, player1, player2, player3, player4, " +
                  "p1cw, p2cw, p3cw, p4cw, " +
                  "fb, player5, p5cw, notes, courseName, " +
                  "p91, p92, p93, p94, p95 " +
                  "FROM teecurr2 WHERE date = ? AND courseName = ? AND notes != '' ORDER BY time, fb";
      }
      PreparedStatement pstmt = con.prepareStatement (stringTee2);

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);         // put the parm in pstmt

      if (!course.equals( "-ALL-" )) {
         pstmt.setString(2, course);
      }
      rs = pstmt.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         hr = rs.getInt(1);
         min = rs.getInt(2);
         player1 = rs.getString(3);
         player2 = rs.getString(4);
         player3 = rs.getString(5);
         player4 = rs.getString(6);
         p1cw = rs.getString(7);
         p2cw = rs.getString(8);
         p3cw = rs.getString(9);
         p4cw = rs.getString(10);
         fb = rs.getInt(11);
         player5 = rs.getString(12);
         p5cw = rs.getString(13);
         notes = rs.getString(14);
         courseNameT = rs.getString(15);
         p91 = rs.getInt(16);
         p92 = rs.getInt(17);
         p93 = rs.getInt(18);
         p94 = rs.getInt(19);
         p95 = rs.getInt(20);

         if (!notes.equals( "" )) {        // if notes

            ampm = " AM";
            if (hr == 12) {
               ampm = " PM";
            }
            if (hr > 12) {
               ampm = " PM";
               hr = hr - 12;    // convert to conventional time
            }

            if (p91 == 1) {          // if 9 hole round
               p1cw = p1cw + "9";
            }
            if (p92 == 1) {
               p2cw = p2cw + "9";
            }
            if (p93 == 1) {
               p3cw = p3cw + "9";
            }
            if (p94 == 1) {
               p4cw = p4cw + "9";
            }
            if (p95 == 1) {
               p5cw = p5cw + "9";
            }

            if (player1.equals("")) {
               p1cw = "";
            }
            if (player2.equals("")) {
               p2cw = "";
            }
            if (player3.equals("")) {
               p3cw = "";
            }
            if (player4.equals("")) {
               p4cw = "";
            }
            if (player5.equals("")) {
               p5cw = "";
            }
              
            fbs = "F";
              
            if (fb == 1) {
              
               fbs = "B";
            }

            //
            //  Display the tee time info with notes
            //
            out.println("<tr>");
            out.println("<td align=\"left\">");
            out.println("<font size=\"2\">");
            if (min < 10) {
               out.println(hr + ":0" + min + ampm);
            } else {
               out.println(hr + ":" + min + ampm);
            }
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(fbs);
            out.println("</font></td>");
            if (course.equals( "-ALL-" )) {
               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(courseNameT);
               out.println("</font></td>");
            }
            out.println("<td align=\"left\">");
            out.println("<font size=\"2\">");
            if (!player1.equals( "" )) {
               out.println(player1+ "<br>");
            }
            if (!player2.equals( "" )) {
               out.println(player2+ "<br>");
            }
            if (!player3.equals( "" )) {
               out.println(player3+ "<br>");
            }
            if (!player4.equals( "" )) {
               out.println(player4+ "<br>");
            }
            if (!player5.equals( "" )) {
               out.println(player5+ "<br>");
            }
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (!player1.equals( "" )) {
               out.println(p1cw+ "<br>");
            }
            if (!player2.equals( "" )) {
               out.println(p2cw+ "<br>");
            }
            if (!player3.equals( "" )) {
               out.println(p3cw+ "<br>");
            }
            if (!player4.equals( "" )) {
               out.println(p4cw+ "<br>");
            }
            if (!player5.equals( "" )) {
               out.println(p5cw+ "<br>");
            }
            out.println("</font></td>");
            if (course.equals( "-ALL-" )) {
               out.println("<td align=\"left\" width=\"300\">");
            } else {
               out.println("<td align=\"left\" width=\"380\">");
            }
            out.println("<font size=\"2\">");
            out.println(notes);
            out.println("</font></td></tr>");
         }

      }                           // end of WHILE tee times
      pstmt.close();

      out.println("</table>");              // end of player table
      out.println("</center></font></body></html>");
      out.close();

   }
   catch (Exception exc) {
      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact ForeTees support (provide this information).");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</center></font></body></html>");
      out.close();
   }
 }


 //************************************************************************
 //  addLineNS - create a text file (if not already done) and add a line to it.
 //
 //  Text file = clubname__.txt for NorthStar POS charges (__ is date and time)
 //
 //    The file is built as "clubname__.temp".  Once it is complete it is renamed.
 //    This prevents the possibility of NS picking up the file before it is complete.
 //
 //************************************************************************

 public void addLineNS(String fname, String line) {

   int fail = 0;

   String dirname = "//home//northstar//pos//";  // create directory name
   String filename = fname + ".temp";            // create full file name (temp file)
   String filename2 = fname + ".txt";            // create full file name (complete file)
   String fileDest = dirname + filename;         // destination (temp file)
   String fileDest2 = dirname + filename2;       // destination (complete file)


   try {
      //
      //  Dir path for the real server
      //
      PrintWriter fout1 = new PrintWriter(new FileWriter(fileDest, true));

      //
      //  Put header line in text file
      //
      fout1.print(line);
      fout1.println();                            // output the line

      fout1.close();

      //
      //  Rename the file if this is the end
      //
      if (line.equals( "EOF" )) {                  // if end of the file
        
         File tempf = new File(fileDest);          // get temp file
         File tempf2 = new File(fileDest2);        // get complete file name
         tempf.renameTo(tempf2);                   // rename it (file is now complete and ready)
      }

   }
   catch (Exception e2) {

      fail = 1;
   }

   //
   //  if above failed, try local pc
   //
   if (fail != 0) {

      dirname = "c:\\java\\tomcat\\webapps\\" +rev+ "\\northstar\\";     // create directory name
      filename = fname + ".temp";                                        // create full file name (temp file)
      filename2 = fname + ".txt";                                        // create full file name (complete file)
      fileDest = dirname + filename;                                     // destination (temp file)
      fileDest2 = dirname + filename2;                                   // destination (complete file)

      try {
         //
         //  dir path for test pc
         //
         PrintWriter fout = new PrintWriter(new FileWriter(fileDest, true));

         //
         //  Put header line in text file
         //
         fout.print(line);
         fout.println();                              // output the line

         fout.close();

         //
         //  Rename the file if this is the end
         //
         if (line.equals( "EOF" )) {                  // if end of the file

            File tempf = new File(fileDest);          // get temp file
            File tempf2 = new File(fileDest2);        // get complete file name
            tempf.renameTo(tempf2);                   // rename it (file is now complete and ready)
         }

      }
      catch (Exception ignore) {
      }
   }
 }  // end of addLineNS


 // *********************************************************
 //  Strip the '9' from end of string
 // *********************************************************

 private final static String strip9( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length - 1];


      for ( int i=0; i<(ca.length-1); i++ ) {
         char oldLetter = ca[i];
         ca2[i] = oldLetter;
      } 

      return new String (ca2);

 } // end strip9



 // *********************************************************
 //  Strip 1 char from the start of a string
 // *********************************************************

 private final static String stripOne( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length - 1];


      for ( int i=0; i<(ca.length-1); i++ ) {
         char oldLetter = ca[i+1];
         ca2[i] = oldLetter;
      } 

      return new String (ca2);

 } // end stripOne



 // *********************************************************
 //  Output the image based on the check-in value
 // *********************************************************

 private String getShowImageForPrint(short pShow) {

    String tmp;
    
    switch (pShow) {
    case 1:
        tmp = "<img src=\"/" +rev+ "/images/xboxsm.gif\" border=\"2\">";
        break;
    case 2:
        tmp = "<img src=\"/" +rev+ "/images/ymtboxsm.gif\" border=\"2\">";
        break;
    default: // if not 1 or 2 then assume 0 and return the small empty image box
        tmp = "<img src=\"/" +rev+ "/images/mtboxsm.gif\" border=\"2\">";
    }
 
    return tmp;
 }

 
 // *********************************************************
 //  Build a player cell for the tee sheet
 // *********************************************************

 private void buildPlayerTD(PrintWriter out, int playern, short show, String player, String bgcolor, int time, 
                            String num, String courseNameT, short fb, int j, boolean disp_hndcp, int hndcp, 
                            float hndcpn, int guest, String pcw, boolean disp_mnum, String mnum, String courseName1) {
    //
    //  Add Player
    //
    if (!player.equals("")) {

      if (player.equalsIgnoreCase("x")) {

         out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
         out.println("<font size=\"2\">" + player + "</font></td>");

      } else {      // not 'x'

         out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_sheet\" target=\"bot\">");
         out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<input type=\"hidden\" name=\"noShow\" value=\"" + playern + "\">");  // player #
         out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
         out.println("<input type=\"hidden\" name=\"show\" value=\"" + show + "\">");
         out.println("<input type=\"hidden\" name=\"name\" value=\"" + num + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
         out.println("<input type=\"hidden\" name=\"courseCheckIn\" value=\"" + courseNameT + "\">");
         out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

         String p;
         p = player;                                       // copy name only

         if (guest == 0) {                                     // if not a guest

            if (disp_hndcp == true) {
               if ((hndcpn == 99) || (hndcpn == -99)) {
                  p = p + "  NH";
               } else {
                  if (hndcpn <= 0) {
                     hndcpn = 0 - hndcpn;                          // convert to non-negative
                     hndcp = Math.round(hndcpn);                   // round it off
                     p = (p + "  " + hndcp);
                  } else {
                     hndcp = Math.round(hndcpn);                   // round it off
                     p = (p + "  +" + hndcp);
                  }
               }
            }
            if (disp_mnum == true && !mnum.equals( "" )) {        // add Member # if present and requested
               p = p + "  " +mnum;
            }
         } // end if guest
         
         String tmp_title;
         out.print("&nbsp;<input type=\"image\" src=\"/" +rev+ "/images/");
         
         switch (show) {
             case 1:
                tmp_title = "Click here to set as a no-show (blank).";
                out.print("xbox.gif");
                break;
             case 2:
                tmp_title = "Click here to acknowledge new signup (pre-check in).";
                out.print("rmtbox.gif");
                break;
             default:
                tmp_title = "Click here to check player in (x).";
                out.print("mtbox.gif");
                break;
         }
         
         out.print("\" border=\"1\" name=\"sub\" value=\"" + p + "\" title=\"" + tmp_title + 
                   "\" alt=\"submit\">&nbsp;" + p + "</font></td></form>");
         
      } // end if not "x"

    } // end if player != ""
    else 
    { 
        // player string was empty
        out.println("<td bgcolor=\"" + bgcolor + "\">");
        out.println("<font size=\"2\">");
        out.println("&nbsp;");
        out.println("</font></td>");
    }
    
    out.println("<td bgcolor=\"white\" align=\"center\">");
    out.println("<font size=\"1\">");
    out.println(((!player.equals("")) && (!player.equalsIgnoreCase( "x" ))) ? pcw : "&nbsp;");
    out.println("</font></td>");
    
 }
 
private void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {
    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H1>Database Access Error</H1>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Fatal Error: " + pMessage);
    out.println("<BR><br>Exception: " + pException);
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
}

}
