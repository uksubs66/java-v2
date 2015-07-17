/***************************************************************************************
 *   Member_slot:  This servlet will process the 'Reserve Tee Time' request from
 *                    the Member's Sheet page.
 *
 *
 *   called by:  Member_sheet (doPost)
 *               self on cancel request
 *               Member_teelist (doPost)
 *
 *
 *   created: 1/14/2002   Bob P.
 *
 *   last updated:             ******* keep this accurate *******
 *
 *        3/19/05   fix some javascript to be more compatible [Paul S]
 *        3/08/05   Add check in doGet processing for valid parameter values - reject if not.
 *        3/02/05   Ver 5 - add support for pre-checkin feature
 *        2/24/05   Piedmont - only allow 2 players during the 1st 4 tee times on Sat & Sun.
 *        2/17/05   Piedmont - do not display 2 of their modes of trans options..
 *                             Also, display custom messages based on the day and time of day. 
 *        2/17/05   Ver 5 - add support for option to force members to specify a guest's name.
 *        2/16/05   Include the name of the guest restriction in error message.
 *        1/19/05   Ver 5 - if member does not have a default mode of trans, leave box blank in tee slot
 *                          to force them to specify the correct mode.
 *        1/06/05   Oakmont CC - change guest processing.
 *        1/05/05   Westchester CC - allow member to include 1 guest any time if 3 members in slot.
 *        1/05/05   Ver 5 - allow member to make up to 5 consecutive tee times at once.
 *       12/09/04   Ver 5 - Change Member Name Alphabit table to common table.
 *       11/30/04   Add special processing for Belle Haven CC.
 *       11/30/04   Add special processing for Milwaukee CC.
 *       10/12/04   Ver 5 - 'index' parm now passed as actual index rather than 'i + index'.
 *        9/22/04   Add special processing for Oakmont CC.
 *        9/20/04   Ver 5 - change getClub from SystemUtils to common.
 *        9/07/04   Change submit buttons to help prevent members from cancelling a tee time when they
 *                  meant to remove themselves only.
 *        6/30/04   If club = Old Oaks, force members to enter a name for their guests.
 *        6/24/04   If no POS system for club, do not force guest names to follow members.
 *        5/24/04   Make some improvements to reduce the processing required to process a tee time request.
 *                  Remove the pop-up warning based on number of visits.
 *                  Also, output a 'Please Be Patient' page in case we are busy.
 *        2/09/04   Add separate 9-hole option.
 *        2/06/04   Add support for configurable transportation modes.
 *        1/22/04   Add checks for 'days in advance' violations based on mship type.
 *       12/18/03   Enhancements for Version 4 of the software.
 *        1/11/04   JAG Modified to match new color scheme
 *        7/18/03   Enhancements for Version 3 of the software.
 *                  Add lottery processing - do not allow adds if lottery time (after processed).
 *        2/04/03   Add Member Number Restriction processing.
 *
 *        1/15/03   Add notes and other V2 changes.
 *
 *       12/18/02   Add changes for V2.
 *                  Add processing for X now optional, also number of hours can be specified.
 *                  Inform user if X is specifed that it must be filled 'xhrs' before tee time.
 *                  Add multiple 'Guest' names.
 *                  Add support for 5-somes.
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
import com.foretees.common.BigDate;


public class Member_slot extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*****************************************************
 // Process the request from Member_sheet
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

   //
   //  Otherwise this is a call from Member_sheet
   //
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


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
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }
     
   String pname = "";
   String jump = "0";                               // jump index - default to zero (for _sheet)
   String lstate = "";
   String stime = "";
   String sfb = "";
   String temps = "";

   int contimes = 0;


   //
   // Get all the parameters entered
   //
   String sdate = req.getParameter("date");         //  date of tee time requested (yyyymmdd)
   String day = req.getParameter("day");            //  name of the day
   String index = req.getParameter("index");        //  index value of day (needed by Member_sheet when returning)
   String p5 = req.getParameter("p5");              //  5-somes supported
   String course = req.getParameter("course");      //  Name of Course

   if (req.getParameter("lstate") != null) {         // if lottery state was passed

      lstate = req.getParameter("lstate");
   }
   if (req.getParameter("jump") != null) {            // if jump index provided

      jump = req.getParameter("jump");
   }
   if (req.getParameter("contimes") != null) {        // if 'consecutive tee times' count provided

      temps = req.getParameter("contimes");
      contimes = Integer.parseInt(temps);
   }

   if (req.getParameter("stime") != null) {            // if call from Member_teelist

      stime = req.getParameter("stime");
      sfb = req.getParameter("fb");
        
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

   //
   //  Verify Parms
   //
   if (sdate == null || sdate.equals( "" ) || stime.equals( "" )) {

      out.println(SystemUtils.HeadTitle("DB Communication Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>Communication Error</H3>");
      out.println("<BR><BR>Sorry, there was a procedural error.");
      out.println("<BR>Please try again. You may have to logout and log back in to correct the problem.");
      out.println("<BR><BR>If problem persists, contact your golf shop.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Output 'Be Patient' message in case we are busy
   //
   out.println(SystemUtils.HeadTitle("Tee Time Request In Process"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center><font size=\"3\" color=\"red\">");
   out.println("<BR><BR><H2>Processing Your Request - Please Be Patient!</H2>");
   out.println("<BR><BR>The system is preparing the tee time for processing.");
   out.println("<BR><BR><b>DO NOT use your browser's Back Button or leave this page</b>");
   out.println("<BR>unless the tee time does not appear within 3 or 4 minutes.");
   out.println("<BR><BR>Exiting this page will only add to the system load and will hang the tee time.");
   out.println("</font></center></body></html>");

   //  
   //  Create a script to jump to doPost processing
   //
   if (contimes > 1) {                                   // if more than one tee time requested

      out.println("<script type=\"text/javascript\">");
      out.println("document.location.href='/" +rev+ "/servlet/Member_slotm?go=yes&lstate=" +lstate+ "&sdate=" +sdate+ "&stime=" +stime+ "&fb=" +sfb+ "&day=" +day+ "&index=" +index+ "&course=" +course+ "&jump=" +jump+ "&p5=" +p5+ "&contimes=" +contimes+ "'");
      out.println("</script>");

   } else {       // go to Member_slot

      out.println("<script type=\"text/javascript\">");
      out.println("document.location.href='/" +rev+ "/servlet/Member_slot?go=yes&lstate=" +lstate+ "&sdate=" +sdate+ "&stime=" +stime+ "&fb=" +sfb+ "&day=" +day+ "&index=" +index+ "&course=" +course+ "&jump=" +jump+ "&p5=" +p5+ "'");
      out.println("</script>");
   }
   out.close();
 }


 //*************************************************************
 // Process the request from doGet above and processing below
 //*************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


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
   String name = (String)session.getAttribute("name");    // get users full name
   String pcw = (String)session.getAttribute("wc");       // get users walk/cart preference

   //
   //  parm block to hold the tee time parms
   //
   parmSlot slotParms = new parmSlot();          // allocate a parm block

   slotParms.club = club;                        // save club name

   //
   // Process request according to which 'submit' button was selected
   //
   //      'time:fb' - a request from Member_sheet
   //      'cancel'  - a cancel request from user via Member_slot (return with no changes)
   //      'letter'  - a request to list member names (from self)
   //      'submitForm'  - a reservation request (from self)
   //      'remove'  - a 'cancel reservation' request (from self - Cancel Tee Time)
   //      'return'  - a return from verify
   //
   if (req.getParameter("cancel") != null) {

      cancel(req, out, club, con);       // process cancel request
      return;
   }

   if ((req.getParameter("submitForm") != null) || (req.getParameter("remove") != null)) {

      verify(req, out, con, session, resp);                 // process reservation requests request

      return;

   }

   String jump = "0";                     // jump index - default to zero (for _sheet)

   if (req.getParameter("jump") != null) {            // if jump index provided

      jump = req.getParameter("jump");
   }

   //
   //   Submit = 'time:fb' or 'letter'
   //
   int in_use = 0;
   int count = 0;
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
   int nowc = 0;
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

   String sdate = "";
   String stime = "";
   String ampm = "";
   String sfb = "";
   String notes = "";
   String hides = "";
   String msg = "";
   String slstate = "";

   //
   //  arrays to hold partner list
   //
   String [] buddy = new String [25];            // max of 25 partners
   String [] bcw = new String [25];

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   // Get all the parameters entered
   //
   String day_name = req.getParameter("day");       //  name of the day
   String index = req.getParameter("index");        //  index value of day (needed by Member_sheet when returning)
   String p5 = req.getParameter("p5");              //  5-somes supported
   String course = req.getParameter("course");      //  Name of Course
   sdate = req.getParameter("sdate");
   stime = req.getParameter("stime");
   sfb = req.getParameter("fb");

   if (req.getParameter("lstate") != null) {         // if lottery state was passed

      slstate = req.getParameter("lstate");

      if (!slstate.equals( "" )) {

         lstate = Integer.parseInt(slstate);
      }
   }

   //
   //  Get this year
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);            // get the year

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

   if (req.getParameter("return") != null) {     // if this is a return from verify - time = hhmm

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

      ampm = " AM";

      if (hr > 11) {

         ampm = " PM";

         if (hr > 12) {

            hr = hr - 12;
         }
      }
      if (min < 10) {
         stime = hr + ":0" + min + ampm;
      } else {
         stime = hr + ":" + min + ampm;
      }

   } else {

      //
      //  Parse the time parm to separate hh, mm, am/pm and convert to military time
      //  (received as 'hh:mm xx'   where xx = am or pm)
      //
      StringTokenizer tok = new StringTokenizer( stime, ": " );     // space is the default token

      String shr = tok.nextToken();
      String smin = tok.nextToken();
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

         if (hr != 12) {                    // 12xx will be PM, 00xx will be midnight

            hr = hr + 12;
         }
      }

      time = hr * 100;
      time = time + min;          // military time
   }

   if ((req.getParameter("letter") != null) || (req.getParameter("return") != null)) {

      player1 = req.getParameter("player1");     // get the player info from the parms passed
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
      if (!hides.equals( "0" )) {     // if not zero
         hide = 1;
      }

   } else {

      //
      //  Get the players' names and check if this tee slot is already in use
      //
      slotParms.day = day_name;            // save day name
        
      try {

         in_use = verifySlot.checkInUse(date, time, fb, course, user, slotParms, con);

      }
      catch (Exception e1) {

         msg = "Check in use flag failed. ";

         dbError(out, e1, msg);
         return;
      }

      if (in_use != 0) {              // if time slot already in use

         out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<CENTER><BR><BR><H1>Tee Time Slot Busy</H1>");
         out.println("<BR><BR>Sorry, but this tee time slot is currently busy.<BR>");
         out.println("<BR>Please select another time or try again later.");
         out.println("<BR><BR>");
         if (index.equals( "999" )) {       // if from Member_teelist (my tee times)

            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain.htm\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("</input></form></font>");
         } else {

            if (index.equals( "888" )) {       // if from Member_searchmem

               out.println("<font size=\"2\">");
               out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_searchmem\">");
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
               out.println("</input></form></font>");

            } else {                           // from tee sheet

               out.println("<font size=\"2\">");
               out.println("<form method=\"get\" action=\"/" +rev+ "/member_selmain.htm\">");
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
               out.println("</input></form></font>");
            }
         }
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;

      }

      //
      //  tee time is available - get current player info
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
   }

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
   //  Custom for Oakmont CC - if less than 30 days in advance, guest tee times cannot be changed -
   //                          players may be added, but not removed.  Also, do not allow cancel from
   //                          My Tee Times or Search (can't tell how far in advance).
   //
   boolean oakguests = false;

   if (club.equals( "oakmont" )) {      // if Oakmont CC

      if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) ||
          !player4.equals( "" ) || !player5.equals( "" )) {                      // did any players exist ?

         oakguests = checkOakGuests(date, time, fb, course, con);   // check if any guests in tee time (if within 30 days)
      }
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

   //
   //  if Piedmont Driving Club, remove 2 trans modes that are for events only
   //
   int piedmontStatus = 0;

   if (club.equals( "piedmont" )) {
     
      piedmontStatus = verifySlot.checkPiedmont(date, time, day_name);     // check if special time

      for (i = 0; i < parmc.tmode_limit; i++) {

         //
         //  If "Walking No Caddie" and time requires a Caddie, remove this option
         //
         if (parmc.tmodea[i].equalsIgnoreCase( "wnc" ) && piedmontStatus == 2) {

            parmc.tmodea[i] = "";      // remove it
         }

         //
         //  If "Cart With ForeCaddie" or "Walk With Caddie, remove this option (used for events only)
         //
         if (parmc.tmodea[i].equalsIgnoreCase( "cfc" ) || parmc.tmodea[i].equalsIgnoreCase( "wwc" )) {

            parmc.tmodea[i] = "";      // remove it
         }
      }
   }

   //
   //  Make sure the user's c/w option is still supported (pro may have changed config)
   //
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

   //
   //  Build the HTML page to prompt user for names
   //
   out.println("<HTML>");
   out.println("<HEAD>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
   out.println("<Title>Member Tee Slot Page</Title>");
   //
   //*******************************************************************
   //  User clicked on a letter - submit the form for the letter
   //*******************************************************************
   //
   out.println("<script language='JavaScript'>");            // Submit the form when clicking on a letter
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
   out.println("<script language='JavaScript'>");            // Erase name script    (Note:  Put these in file???)  what other files use these scripts, just proshop_slot?
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
   out.println("<script language='JavaScript'>");            // Erase text area script
   out.println("<!--");
   out.println("function erasetext(pos1) {");
   out.println("eval(\"document.forms['playerform'].\" + pos1 + \".value = '';\")");           // clear the player field
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script

   out.println("<script language='JavaScript'>");             // Move Notes into textarea
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
   out.println("<script language='JavaScript'>");            // Move name script
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

   if (!club.equals( "piedmont" ) || piedmontStatus != 3) {   // Custom for Piedmont (only 2 players)

      out.println("var player3 = f.player3.value;");
      out.println("var player4 = f.player4.value;");

      if (p5.equals( "Yes" )) {
         out.println("var player5 = f.player5.value;");
      }
   }

   out.println("if (( name != 'x') && ( name != 'X')) {");


   if (!club.equals( "piedmont" ) || piedmontStatus != 3) {   // Custom for Piedmont (only 2 players)

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
            out.println("f.p1cw.value = wc;");
         out.println("}");
      out.println("} else {");

      out.println("if (player2 == '') {");                    // if player2 is empty
         out.println("f.player2.value = name;");
         out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
            out.println("f.p2cw.value = wc;");
         out.println("}");

   if (!club.equals( "piedmont" ) || piedmontStatus != 3) {   // Custom for Piedmont (only 2 players)
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

      if (p5.equals( "Yes" )) {
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
   }                       // end of IF piedmont
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
   out.println("<script language='JavaScript'>");            // Move Guest Name script
   out.println("<!--");

   out.println("function moveguest(namewc) {");

   out.println("var f = document.forms['playerform'];");
   out.println("var name = namewc;");

   out.println("var player1 = f.player1.value;");
   out.println("var player2 = f.player2.value;");

   if (!club.equals( "piedmont" ) || piedmontStatus != 3) {   // Custom for Piedmont (only 2 players)

      out.println("var player3 = f.player3.value;");
      out.println("var player4 = f.player4.value;");

      if (p5.equals( "Yes" )) {
         out.println("var player5 = f.player5.value;");
      }
   }

      out.println("if (player1 == '') {");                    // if player1 is empty
         out.println("f.player1.value = name;");
      out.println("} else {");

      out.println("if (player2 == '') {");                    // if player2 is empty
         out.println("f.player2.value = name;");
           
   if (!club.equals( "piedmont" ) || piedmontStatus != 3) {   // Custom for Piedmont (only 2 players)

      out.println("} else {");

      out.println("if (player3 == '') {");                    // if player3 is empty
         out.println("f.player3.value = name;");
      out.println("} else {");

      out.println("if (player4 == '') {");                    // if player4 is empty
         out.println("f.player4.value = name;");

      if (p5.equals( "Yes" )) {
         out.println("} else {");
         out.println("if (player5 == '') {");                    // if player5 is empty
            out.println("f.player5.value = name;");
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
   out.println("<body onLoad=\"movenotes()\" bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
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
     out.println("<font color=\"#ffffff\" size=\"5\">ForeTees Member Reservation</font>");
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
            out.println("<b>Warning</b>:&nbsp;&nbsp;You have <b>6 minutes</b> to complete this reservation.");
            out.println("&nbsp; If you want to return without completing a reservation, <b>do not ");
            out.println("use your browser's BACK</b> button/option.&nbsp; Instead select the <b>Go Back</b> ");
            out.println("option below.");
            if (lstate == 5) {
               if (club.equals( "oldoaks" )) {
                  out.println("<br><br><b>Note:</b> This time is reserved for requests. You are not allowed to add players after the requests have been processed.");
               } else {
                  out.println("<br><br><b>Note:</b> This is a lottery time. You are not allowed to add players after the lottery has been processed.");
               }
            }
         out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<font size=\"2\" color=\"black\">");
      out.println("<br>Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b>");
        out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Tee Time:&nbsp;&nbsp;<b>" + stime + "</b>");
        if (!course.equals( "" )) {
           out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>");
        }
        out.println("<br></font>");

      out.println("<table border=\"0\" cellpadding=\"5\" cellspacing=\"5\" align=\"center\">"); // table to contain 4 tables below

         out.println("<tr>");
         out.println("<td align=\"center\">");         // col for Instructions and Go Back button

            out.println("<font size=\"2\" color=\"Darkred\">");
            out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" name=\"can\">");
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
            if (club.equals( "oldoaks" )) {
               out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/member_help_slot_instructo.htm', 'newwindow', config='Height=500, width=680, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            } else {
               out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/member_help_slot_instruct.htm', 'newwindow', config='Height=500, width=680, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            }
            out.println("<img src=\"/" +rev+ "/images/instructions.gif\" border=0>");
            out.println("<br>Click for Help</a>");

         out.println("</font></td>");

         out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" name=\"playerform\" id=\"playerform\">");

         out.println("<td align=\"center\" valign=\"top\">");

            if (lstate == 5) {
               out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" align=\"center\" width=\"400\">");  // table for player selection
            } else {
               out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" align=\"center\" width=\"370\">");  // table for player selection
            }
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<b>Add or Remove Players</b>&nbsp;&nbsp; Note: Click on Names -->");
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

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player2', 'p2cw')\" style=\"cursor:hand\">");
               out.println("2:&nbsp;<input type=\"text\" id=\"player2\" name=\"player2\" value=\""+player2+"\" size=\"20\" maxlength=\"30\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p2cw\" id=\"p2cw\">");
              
                 out.println("<option selected value=" + p2cw + ">" + p2cw + "</option>");
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

            //
            //  Custom - Piedmont DC - do not allow more than 2 players on Sat & Sun mornings
            //
            if (!club.equals( "piedmont" ) || piedmontStatus != 3) {

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player3', 'p3cw')\" style=\"cursor:hand\">");
               out.println("3:&nbsp;<input type=\"text\" id=\"player3\" name=\"player3\" value=\""+player3+"\" size=\"20\" maxlength=\"30\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p3cw\" id=\"p3cw\">");
            
                 out.println("<option selected value=" + p3cw + ">" + p3cw + "</option>");
                 for (i=0; i<16; i++) {         // get all c/w options

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

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player4', 'p4cw')\" style=\"cursor:hand\">");
               out.println("4:&nbsp;<input type=\"text\" id=\"player4\" name=\"player4\" value=\""+player4+"\" size=\"20\" maxlength=\"30\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p4cw\" id=\"p4cw\">");
                 
                 out.println("<option selected value=" + p4cw + ">" + p4cw + "</option>");
                 for (i=0; i<16; i++) {       // get all c/w options

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

               if (p5.equals( "Yes" )) {

                  out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player5', 'p5cw')\" style=\"cursor:hand\">");
                  out.println("5:&nbsp;<input type=\"text\" id=\"player5\" name=\"player5\" value=\""+player5+"\" size=\"20\" maxlength=\"30\">");
                    out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p5cw\" id=\"p5cw\">");

                    out.println("<option selected value=" + p5cw + ">" + p5cw + "</option>");
                    for (i=0; i<16; i++) {      // get all c/w options

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

                  out.println("<br><br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasetext('notes')\" style=\"cursor:hand\">");
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
               for (i=0; i<16; i++) {
                  if (!parmc.tmodea[i].equals( "" )) {
                     out.println(parmc.tmodea[i]+ " = " +parmc.tmode[i]+ "&nbsp;&nbsp;");
                  }
               }
               out.println("</font><br>");
                 
               //
               //  If Oakmont and guests already in this tee time, then do not allow cancel
               //
               if (oakguests == false) {      // if no guests existed

                  //
                  //  Do not allow user to cancel the tee time if not already in it
                  //
                  if (user.equalsIgnoreCase(slotParms.user1) || user.equalsIgnoreCase(slotParms.user2) ||
                  user.equalsIgnoreCase(slotParms.user3) || user.equalsIgnoreCase(slotParms.user4) ||
                  user.equalsIgnoreCase(slotParms.user5)) {
                     out.println("<input type=submit value=\"Cancel ENTIRE Tee Time\" name=\"remove\">&nbsp;&nbsp;&nbsp;");
                  }
               }
               if (!slotParms.player1.equals("")) {   // if this is a change (not a new tee time)
                  out.println("<input type=submit value=\"Submit Changes\" name=\"submitForm\">");
               } else {
                  out.println("<input type=submit value=\"Submit\" name=\"submitForm\">");
               }
               out.println("</font></td></tr>");
               out.println("</table>");
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
                     "WHERE name_last LIKE ? ORDER BY name_last, name_first, name_mi");

            stmt2.clearParameters();               // clear the parms
            stmt2.setString(1, letter);            // put the parm in stmt
            rs = stmt2.executeQuery();             // execute the prepared stmt

            out.println("<tr><td align=\"left\"><font size=\"2\">");
            out.println("<select size=\"20\" name=\"bname\" onClick=\"movename(this.value)\">"); // movename(this.form.bname.value)

            while(rs.next()) {

               last = rs.getString(1);
               first = rs.getString(2);
               mid = rs.getString(3);
               wc = rs.getString(4);           // walk/cart preference

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
      }        // end of IF Partner List or letter

   }           // not letter display

   if (letter.equals( "" ) || letter.equals( "Partner List" )) {  // if no letter or Partner List request

      if (lstate != 5) {         // do not allow buddy list or member list of lottery time

         String nameBud = "";
         int countBud = 0;

         //
         //     table for buddy list
         //
         //  Get the current buddy list for this user
         //
         try {

            PreparedStatement pstmt = con.prepareStatement (
                     "SELECT * FROM buddy WHERE username = ?");

            out.println("<table border=\"1\" width=\"160\" bgcolor=\"#f5f5dc\">");      // buddy list
            out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
            out.println("<font  color=\"#ffffff\"size=\"2\">");
            out.println("<b>Partner List</b>");
            out.println("</font></td>");
            out.println("</tr><tr>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("Click on name to add");
            out.println("</font></td></tr>");


            pstmt.clearParameters();            // clear the parms
            pstmt.setString(1, user);           // put username in statement
            rs = pstmt.executeQuery();          // execute the prepared stmt

            if (rs.next()) {

               buddy[0] = rs.getString("buddy1");
               buddy[1] = rs.getString("buddy2");
               buddy[2] = rs.getString("buddy3");
               buddy[3] = rs.getString("buddy4");
               buddy[4] = rs.getString("buddy5");
               buddy[5] = rs.getString("buddy6");
               buddy[6] = rs.getString("buddy7");
               buddy[7] = rs.getString("buddy8");
               buddy[8] = rs.getString("buddy9");
               buddy[9] = rs.getString("buddy10");
               buddy[10] = rs.getString("buddy11");
               buddy[11] = rs.getString("buddy12");
               buddy[12] = rs.getString("buddy13");
               buddy[13] = rs.getString("buddy14");
               buddy[14] = rs.getString("buddy15");
               buddy[15] = rs.getString("buddy16");
               buddy[16] = rs.getString("buddy17");
               buddy[17] = rs.getString("buddy18");
               buddy[18] = rs.getString("buddy19");
               buddy[19] = rs.getString("buddy20");
               buddy[20] = rs.getString("buddy21");
               buddy[21] = rs.getString("buddy22");
               buddy[22] = rs.getString("buddy23");
               buddy[23] = rs.getString("buddy24");
               buddy[24] = rs.getString("buddy25");
               bcw[0] = rs.getString("b1cw");
               bcw[1] = rs.getString("b2cw");
               bcw[2] = rs.getString("b3cw");
               bcw[3] = rs.getString("b4cw");
               bcw[4] = rs.getString("b5cw");
               bcw[5] = rs.getString("b6cw");
               bcw[6] = rs.getString("b7cw");
               bcw[7] = rs.getString("b8cw");
               bcw[8] = rs.getString("b9cw");
               bcw[9] = rs.getString("b10cw");
               bcw[10] = rs.getString("b11cw");
               bcw[11] = rs.getString("b12cw");
               bcw[12] = rs.getString("b13cw");
               bcw[13] = rs.getString("b14cw");
               bcw[14] = rs.getString("b15cw");
               bcw[15] = rs.getString("b16cw");
               bcw[16] = rs.getString("b17cw");
               bcw[17] = rs.getString("b18cw");
               bcw[18] = rs.getString("b19cw");
               bcw[19] = rs.getString("b20cw");
               bcw[20] = rs.getString("b21cw");
               bcw[21] = rs.getString("b22cw");
               bcw[22] = rs.getString("b23cw");
               bcw[23] = rs.getString("b24cw");
               bcw[24] = rs.getString("b25cw");

               countBud = 0;

               for (i = 0; i < 25; i++) {         // check all 25 partners

                  if (!buddy[i].equals("")) {

                     nowc = 0;
                     countBud++;                     // count buddies

                     //
                     //  make sure the CW option is supported for this course
                     //
                     int i2 = 0;
                     loopi2:
                     while (i2 < 16) {

                        if (parmc.tmodea[i2].equals( bcw[i] )) {

                           break loopi2;
                        }
                        i2++;
                     }
                     if (i2 > 15) {       // if we went all the way without a match

                        bcw[i] = parmc.tmodea[0];    // use default option
                     }
                  }
               }

               if (countBud != 0) {

                  if (countBud > 20) {

                     countBud = 20;        // max of 20 showing
                  }
                  out.println("<tr><td align=\"left\">");
                  out.println("<font size=\"2\">");
                  out.println("<select size=\"" + countBud + "\" name=\"bud\" onClick=\"movename(this.value)\">"); // movename(this.form.bud.value)

                  for (i = 0; i < 25; i++) {         // check all 25 partners

                     if (!buddy[i].equals("")) {

                        nameBud = buddy[i] + ":" + bcw[i];                      // combine name:wc for script
                        out.println("<option value=\"" + nameBud + "\">" + buddy[i] + "</option>");
                     }
                  }
                  out.println("</select>");

               } else {

                  out.println("<tr><td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println("No names - <br>");
                  out.println("Select 'Partner List' <br>");
                  out.println("on main menu to add.");
               }

            } else {

               out.println("<tr><td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println("No names - <br>");
               out.println("Select 'Partner List' <br>");
               out.println("on main menu to add.");
            }

            out.println("</font></td></tr>");

         }
         catch (Exception exc) {             // SQL Error - ignore buddy list

         }

      }     // end of if lottery time
   }        // end of if letter display

   out.println("</table></td>");

   out.println("</td>");                                      // end of this column
   out.println("<td width=\"200\" valign=\"top\">");

   if (lstate == 5) {        // do not show member list if lottery time

      out.println("&nbsp;");       // put space in empty column

   } else {       // not lottery time

      //
      //   Output the Alphabit Table for Members' Last Names
      //
      alphaTable.getTable(out, user);

   }     // end of IF lottery time

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
   //     Check the club db table for X and Guest parms specified by proshop
   //
   try {
      getClub.getParms(con, parm);        // get the club parms

      x = parm.x;

      //
      //  first we must count how many fields there will be
      //
      xCount = 0;
      if (x != 0) {

         xCount = 1;
      }
      for (i = 0; i < parm.MAX_Guests; i++) {

         if (!parm.guest[i].equals( "" ) && parm.gOpt[i] == 0) {   // count the X and guest names

            xCount++;
         }
      }
      i = 0;
      if (xCount != 0) {                       // if X and/or guest names, display them in list

         if (xCount < 2) {

            xCount = 2;             // set size to at least 2
         }
         if (xCount > 8) {

            xCount = 8;             // set size to no more than 8 showing at once (it will scroll)
         }
         out.println("<tr><td align=\"left\"><font size=\"1\" face=\"Helvetica, Arial, Sans-serif\">");
         out.println("<b>**</b> Add guests immediately<br><b>after</b> host member.<br>X indicates player TBD.<br>");
         out.println("</font><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<select size=\"" + xCount + "\" name=\"xname\" onClick=\"moveguest(this.form.xname.value)\">");
         if (x != 0) {
            out.println("<option value=\"X\">X</option>");
         }
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
   }
   catch (Exception exc) {             // SQL Error - ignore guest and x

      out.println("</table>");
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
 //  Process reservation request from Member_slot (HTML)
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
   String club = (String)session.getAttribute("club");
   String posType = (String)session.getAttribute("posType");

   //
   // init all variables
   //
   int time = 0;
   int dd = 0;
   int mm = 0;
   int yy = 0;
   int fb = 0;
   int fb2 = 0;
   int t_fb = 0;
   int x = 0;
   int xhrs = 0;
   int calYear = 0;
   int calMonth = 0;
   int calDay = 0;
   int calHr = 0;
   int calMin = 0;
   int memNew = 0;
   int memMod = 0;
   int i = 0;
   int ind = 0;
   int xcount = 0;
   int year = 0;
   int month = 0;
   int dayNum = 0;
   int mtimes = 0;
   int sendemail = 0;
   int emailNew = 0;
   int emailMod = 0;
   int emailCan = 0;
   int mems = 0;
   int players = 0;
   int oldplayers = 0;
   int lstate = 0;
   int gi = 0;
   int adv_time = 0;

   long temp = 0;
   long ldd = 0;
   long date = 0;
   long adv_date = 0;
   long dateStart = 0;
   long dateEnd = 0;

   String player = "";
   String sfb = "";
   String sfb2 = "";
   String course2 = "";
   String notes = "";
   String notes2 = "";
   String rcourse = "";
   String period = "";
   String mperiod = "";
   String msg = "";
   String plyr1 = "";
   String plyr2 = "";
   String plyr3 = "";
   String plyr4 = "";
   String plyr5 = "";
   String memberName = "";
   String p9s = "";

   boolean error = false;
   boolean guestError = false;
   boolean oakskip = false;

   //
   //  Arrays to hold member & guest names to tie guests to members
   //
   String [] memA = new String [5];     // members
   String [] usergA = new String [5];   // guests' associated member (username)

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   //
   //  parm block to hold the tee time parms
   //
   parmSlot slotParms = new parmSlot();          // allocate a parm block

   slotParms.hndcp1 = 99;     // init handicaps
   slotParms.hndcp2 = 99;
   slotParms.hndcp3 = 99;
   slotParms.hndcp4 = 99;
   slotParms.hndcp5 = 99;

   //
   // Get all the parameters entered
   //
   String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
   String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
   String smm = req.getParameter("mm");               //  month of tee time
   String syy = req.getParameter("yy");               //  year of tee time
   String index = req.getParameter("index");          //  day index value (needed by _sheet on return)
   slotParms.p5 = req.getParameter("p5");                //  5-somes supported for this slot
   slotParms.course = req.getParameter("course");        //  name of course
   String slstate = req.getParameter("lstate");       //  lottery state (if any)
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
   slotParms.day = req.getParameter("day");                      // name of day
   sfb = req.getParameter("fb");                       // Front/Back indicator
   slotParms.notes = req.getParameter("notes");                  // Notes
   slotParms.hides = req.getParameter("hide");            // Hide Notes

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
   if (slotParms.player1 == null ) {
      slotParms.player1 = "";
   }
   if (slotParms.player2 == null ) {
      slotParms.player2 = "";
   }
   if (slotParms.player3 == null ) {
      slotParms.player3 = "";
   }
   if (slotParms.player4 == null ) {
      slotParms.player4 = "";
   }
   if (slotParms.player5 == null ) {
      slotParms.player5 = "";
   }
   if (slotParms.p1cw == null ) {
      slotParms.p1cw = "";
   }
   if (slotParms.p2cw == null ) {
      slotParms.p2cw = "";
   }
   if (slotParms.p3cw == null ) {
      slotParms.p3cw = "";
   }
   if (slotParms.p4cw == null ) {
      slotParms.p4cw = "";
   }
   if (slotParms.p5cw == null ) {
      slotParms.p5cw = "";
   }

   //
   //  Convert date & time from string to int
   //
   try {
      date = Long.parseLong(sdate);
      time = Integer.parseInt(stime);
      mm = Integer.parseInt(smm);
      yy = Integer.parseInt(syy);
      fb = Integer.parseInt(sfb);
      lstate = Integer.parseInt(slstate);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  convert the index value from string to numeric - save both
   //
   try {
      ind = Integer.parseInt(index);
   }
   catch (NumberFormatException e) {
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

   //
   //  put parms in Parameter Object for portability
   //
   slotParms.date = date;
   slotParms.time = time;
   slotParms.mm = mm;
   slotParms.yy = yy;
   slotParms.dd = dd;
   slotParms.fb = fb;
   slotParms.ind = ind;      // index value
   slotParms.sfb = sfb; 
   slotParms.club = club;    // name of club
     
  
   //
   //  Check if this tee slot is still 'in use' and still in use by this user??
   //
   //  This is necessary because the user may have gone away while holding this slot.  If the
   //  slot timed out (system timer), the slot would be marked 'not in use' and another
   //  user could pick it up.  The original holder could be trying to use it now.
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
         "SELECT player1, player2, player3, player4, username1, username2, username3, " +
         "username4, p1cw, p2cw, p3cw, p4cw, in_use, in_use_by, " +
         "show1, show2, show3, show4, player5, username5, p5cw, show5, " +
         "lottery, memNew, memMod, orig_by " +
         "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);         // put the parm in pstmt
      pstmt.setInt(2, time);
      pstmt.setInt(3, fb);
      pstmt.setString(4, slotParms.course);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         slotParms.oldPlayer1 = rs.getString(1);
         slotParms.oldPlayer2 = rs.getString(2);
         slotParms.oldPlayer3 = rs.getString(3);
         slotParms.oldPlayer4 = rs.getString(4);
         slotParms.oldUser1 = rs.getString(5);
         slotParms.oldUser2 = rs.getString(6);
         slotParms.oldUser3 = rs.getString(7);
         slotParms.oldUser4 = rs.getString(8);
         slotParms.oldp1cw = rs.getString(9);
         slotParms.oldp2cw = rs.getString(10);
         slotParms.oldp3cw = rs.getString(11);
         slotParms.oldp4cw = rs.getString(12);
         slotParms.in_use = rs.getInt(13);
         slotParms.in_use_by = rs.getString(14);
         slotParms.show1 = rs.getShort(15);
         slotParms.show2 = rs.getShort(16);
         slotParms.show3 = rs.getShort(17);
         slotParms.show4 = rs.getShort(18);
         slotParms.oldPlayer5 = rs.getString(19);
         slotParms.oldUser5 = rs.getString(20);
         slotParms.oldp5cw = rs.getString(21);
         slotParms.show5 = rs.getShort(22);
         slotParms.lottery = rs.getString(23);
         memNew = rs.getInt(24);
         memMod = rs.getInt(25);
         slotParms.orig_by = rs.getString(26);
      }
      pstmt.close();

      if (slotParms.orig_by.equals( "" )) {    // if originator field still empty

         slotParms.orig_by = user;             // set this user as the originator
      }

      if ((slotParms.in_use == 0) || (!slotParms.in_use_by.equals( user ))) {    // if time slot in use and not by this user

         out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<CENTER><BR><BR><H1>Reservation Timer Expired</H1>");
         out.println("<BR><BR>Sorry, but this tee time slot has been returned to the system.<BR>");
         out.println("<BR>The system timed out and released the tee time.");
         out.println("<BR><BR>");
         if (index.equals( "999" )) {      // if from Member_teelist

            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain.htm\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("</input></form></font>");

         } else {

            if (index.equals( "888" )) {       // if from Member_searchmem

               out.println("<font size=\"2\">");
               out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_searchmem\">");
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
               out.println("</input></form></font>");

            } else {

               out.println("<font size=\"2\">");
               out.println("<form method=\"get\" action=\"/" +rev+ "/member_selmain.htm\">");
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
               out.println("</input></form></font>");
            }
         }
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }
   catch (Exception e) {

      msg = "Check if busy. ";

      dbError(out, e, msg);
      return;
   }

   //
   //  If request is to 'Cancel This Res', then clear all fields for this slot
   //
   //  First, make sure user is already on tee slot or originated it for unaccompanied guests
   //
   if (req.getParameter("remove") != null) {

      try {
         PreparedStatement pstmt4 = con.prepareStatement (
            "SELECT player1, player2, player3, player4, player5 " +
               "FROM teecurr2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? " +
               "OR username5 = ? OR userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) " +
               "AND date = ? AND time = ? AND fb = ? AND courseName = ?");


         pstmt4.clearParameters();        // clear the parms
         pstmt4.setString(1, user);
         pstmt4.setString(2, user);
         pstmt4.setString(3, user);
         pstmt4.setString(4, user);
         pstmt4.setString(5, user);
         pstmt4.setString(6, user);
         pstmt4.setString(7, user);
         pstmt4.setString(8, user);
         pstmt4.setString(9, user);
         pstmt4.setString(10, user);
         pstmt4.setLong(11, date);
         pstmt4.setInt(12, time);
         pstmt4.setInt(13, fb);
         pstmt4.setString(14, slotParms.course);
         rs = pstmt4.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            plyr1 = rs.getString(1);
            plyr2 = rs.getString(2);
            plyr3 = rs.getString(3);
            plyr4 = rs.getString(4);
            plyr5 = rs.getString(5);

         } else {

            out.println(SystemUtils.HeadTitle("Procedure Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='� 2001 ForeTees, LLC';\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Procedure Error</H3>");
            out.println("<BR><BR>You cannot cancel a reservation that you are not part of.");
            out.println("<BR><BR>You must be a member currently on the tee slot in order to cancel it.");
            out.println("<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }

         pstmt4.close();

      }
      catch (Exception e4) {

         msg = "Check user on tee time. ";

         dbError(out, e4, msg);
         return;
      }

      //
      //  See if we need to ask for a confirmation
      //
      players = 0;

      if (!plyr1.equals( "" ) && !plyr1.equalsIgnoreCase( "x" )) {       // if member name or guest

         players++;
      }
      if (!plyr2.equals( "" ) && !plyr2.equalsIgnoreCase( "x" )) {

         players++;
      }
      if (!plyr3.equals( "" ) && !plyr3.equalsIgnoreCase( "x" )) {

         players++;
      }
      if (!plyr4.equals( "" ) && !plyr4.equalsIgnoreCase( "x" )) {

         players++;
      }
      if (!plyr5.equals( "" ) && !plyr5.equalsIgnoreCase( "x" )) {

         players++;
      }

      //
      //  Now see if this action has been confirmed yet
      //
      if ((req.getParameter("ack_remove") != null) || (players < 2)) {  // if remove has been confirmed or 1 player

         slotParms.player1 = "";                  // set reservation fields to null
         slotParms.player2 = "";
         slotParms.player3 = "";
         slotParms.player4 = "";
         slotParms.player5 = "";
         slotParms.p1cw = "";
         slotParms.p2cw = "";
         slotParms.p3cw = "";
         slotParms.p4cw = "";
         slotParms.p5cw = "";
         slotParms.user1 = "";
         slotParms.user2 = "";
         slotParms.user3 = "";
         slotParms.user4 = "";
         slotParms.user5 = "";
         slotParms.userg1 = "";
         slotParms.userg2 = "";
         slotParms.userg3 = "";
         slotParms.userg4 = "";
         slotParms.userg5 = "";
         slotParms.show1 = 0;
         slotParms.show2 = 0;
         slotParms.show3 = 0;
         slotParms.show4 = 0;
         slotParms.show5 = 0;
         slotParms.notes = "";
         slotParms.mNum1 = "";
         slotParms.mNum2 = "";
         slotParms.mNum3 = "";
         slotParms.mNum4 = "";
         slotParms.mNum5 = "";
         slotParms.orig_by = "";
         slotParms.p91 = 0;
         slotParms.p92 = 0;
         slotParms.p93 = 0;
         slotParms.p94 = 0;
         slotParms.p95 = 0;
         emailCan = 1;      // send email notification for Cancel Request
         sendemail = 1;

         memMod++;      // increment number of mods for reports

      } else {    // not acked yet - display confirmation page

         out.println(SystemUtils.HeadTitle("Cancel Tee Time Confirmation Prompt"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><font size=\"6\" color=\"red\"><b>***WARNING***</b><BR>");
         out.println("</font><font size=\"3\"><BR>This will remove ALL players from the tee time.<BR>");
         out.println("<BR>If this is what you want to do, then click on 'Continue' below.<BR>");
         out.println("<BR>");

         out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"remove\" value=\"yes\">");
         out.println("<input type=\"hidden\" name=\"ack_remove\" value=\"yes\">");
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
         out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
         out.println("<input type=\"hidden\" name=\"mm\" value=\"" + mm + "\">");
         out.println("<input type=\"hidden\" name=\"yy\" value=\"" + yy + "\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
         out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
         out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
         out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
         out.println("<input type=\"submit\" value=\"Continue\" name=\"submitForm\"></form>");

         out.println("<BR>If you only want to remove yourself, or a portion of the players,<BR>");
         out.println("click on 'Return' below. Then use the 'erase' and 'Submit' buttons<BR>");
         out.println("to remove only those players you wish to remove.<BR>");
         out.println("<BR>");

            //
            //  Return to _slot to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;    // wait for acknowledgement
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
      //  Is this time during a lottery block (after lottery is done)??
      //
      if (lstate == 5) {          // if lottery time - no new players

         //
         //  Check if weighted lottery
         //
         try {
            verifySlot.getLottery(slotParms, con);
         }
         catch (Exception ignore) {
         }

         if (slotParms.lottery_type.equalsIgnoreCase( "weighted" )) {       // if weighted lottery

            players = 0;

            if (!slotParms.player1.equals( "" )) {       // if member name or guest

               players++;
            }
            if (!slotParms.player2.equals( "" )) {

               players++;
            }
            if (!slotParms.player3.equals( "" )) {

               players++;
            }
            if (!slotParms.player4.equals( "" )) {

               players++;
            }
            if (!slotParms.player5.equals( "" )) {

               players++;
            }

            oldplayers = 0;

            if (!slotParms.oldPlayer1.equals( "" )) {       // if member name or guest

               oldplayers++;
            }
            if (!slotParms.oldPlayer2.equals( "" )) {

               oldplayers++;
            }
            if (!slotParms.oldPlayer3.equals( "" )) {

               oldplayers++;
            }
            if (!slotParms.oldPlayer4.equals( "" )) {

               oldplayers++;
            }
            if (!slotParms.oldPlayer5.equals( "" )) {

               oldplayers++;
            }

            if (players > oldplayers) {          // if member trying to add a player

               out.println(SystemUtils.HeadTitle("Data Entry Error"));
               out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='� 2001 ForeTees, LLC';\">");
               out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<center>");
               out.println("<BR><BR><H3>Data Entry Error</H3>");
               if (club.equals( "oldoaks" )) {
                  out.println("<BR><BR>You cannot add a player to a tee time request after the requests have been assigned times.");
               } else {
                  out.println("<BR><BR>You cannot add a player to a lottery time after the lottery times have been assigned.");
               }
               out.println("<BR>You can only remove players, add/change notes or cancel the tee time.");
               out.println("<BR><BR>Please contact your golf shop if you would like to add a player to this tee time.");
               out.println("<BR><BR>");
               //
               //  Return to _slot to change the player order
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
               out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
               out.println("</form></font>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;
            }
         }
      }      // end of IF lottery time

      //
      //  Check if any player names are guest names
      //
      try {

         verifySlot.parseGuests(slotParms, con);

      }
      catch (Exception ignore) {
      }


      //
      //  Reject if any player was a guest type that is not allowed for members
      //
      if (!slotParms.gplayer.equals( "" )) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='� 2001 ForeTees, LLC';\">");
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
         out.println("<BR><BR>If the Proshop had originally entered this guest, then it <b>must not</b> be changed.");
         out.println("<BR><BR>Please correct this and try again.");
         out.println("<BR><BR>");
         //
         //  Return to _slot to change the player order
         //
         out.println("<font size=\"2\">");
         out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
         out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
         out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
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
         out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='� 2001 ForeTees, LLC';\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR>Required field has not been completed or is invalid.");
         out.println("<BR><BR>At least one player field must contain a name.");
         out.println("<BR>If you want to cancel the reservation, use the 'Cancel Tee Time' button under the player fields.");
         out.println("<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      //
      //  Check the number of X's against max specified by proshop
      //
      xcount = 0;

      if (slotParms.player1.equalsIgnoreCase( "x" )) {

         xcount++;
      }

      if (slotParms.player2.equalsIgnoreCase( "x" )) {

         xcount++;
      }

      if (slotParms.player3.equalsIgnoreCase( "x" )) {

         xcount++;
      }

      if (slotParms.player4.equalsIgnoreCase( "x" )) {

         xcount++;
      }

      if (slotParms.player5.equalsIgnoreCase( "x" )) {

         xcount++;
      }

      if (xcount > x) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='� 2001 ForeTees, LLC';\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR>The number of X's requested (" + xcount + ") exceeds the number allowed (" + x + ").");
         out.println("<BR>Please try again.");
         out.println("<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
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
         out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='� 2001 ForeTees, LLC';\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR>Required field has not been completed or is invalid.");
         out.println("<BR><BR>You must specify a Cart or Walk option for all players.");
         out.println("<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
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
         out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='� 2001 ForeTees, LLC';\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR><b>" + player + "</b> was specified more than once.");
         out.println("<BR><BR>Please correct this and try again.");
         out.println("<BR><BR>");
         //
         //  Return to _slot to change the player order
         //
         out.println("<font size=\"2\">");
         out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
         out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
         out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      //
      //  Parse the names to separate first, last & mi
      //
      try {

         error = verifySlot.parseNames(slotParms, "mem");

      }
      catch (Exception ignore) {
      }

      if ( error == true ) {          // if problem

         invData(out, slotParms.player1, slotParms.player2, slotParms.player3, slotParms.player4, slotParms.player5);                        // reject
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
      //  Save the members' usernames for guest association
      //
      memA[0] = slotParms.user1;
      memA[1] = slotParms.user2;
      memA[2] = slotParms.user3;
      memA[3] = slotParms.user4;
      memA[4] = slotParms.user5;

      //
      //  Check if any of the names are invalid.  If so, ask proshop if they want to ignore the error.
      //
      if (slotParms.inval1 != 0) {

         invMem(out, slotParms.player1);                        // reject
         return;
      }
      if (slotParms.inval2 != 0) {

         invMem(out, slotParms.player2);                        // reject
         return;
      }
      if (slotParms.inval3 != 0) {

         invMem(out, slotParms.player3);                        // reject
         return;
      }
      if (slotParms.inval4 != 0) {

         invMem(out, slotParms.player4);                        // reject
         return;
      }
      if (slotParms.inval5 != 0) {

         invMem(out, slotParms.player5);                        // reject
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
               out.println("<BR><BR>Sorry, 'X' is not allowed for this tee time.<BR>");
               out.println("It is not far enough in advance to reserve a player position with an X.");
               out.println("<BR><BR>");
               //
               //  Return to _slot to change the player order
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
               out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
               out.println("</form></font>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
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

            if (error == false) {       // if ok, check for Hazeltine special processing
              
               //
               //  If Hazeltine National, then process the Unaccompanied Guests (Sponsored Group)
               //
               if (club.equals( "hazeltine" )) {      // if Hazeltine National

                  error = verifySlot.checkNational(slotParms, con);  // check for max rounds for National mships
               }
            }

         }
         catch (Exception ignore) {
         }

         if (error == true) {      // a member exceed the max allowed tee times per week, month or year

            out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Member Exceeded Max Allowed Rounds</H3><BR>");
            out.println("<BR><BR>Sorry, " + slotParms.player + " is a " + slotParms.mship + " member and has exceeded the<BR>");
            out.println("maximum number of tee times allowed for this " + slotParms.period + ".");
            out.println("<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
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
         //  Custom for Westchester - if 1 guest and 3 members, then always ok (do not check restrictions)
         //
         if (!club.equals( "westchester" ) || slotParms.guests != 1 || slotParms.members < 3) {

            try {

               error = verifySlot.checkMaxGuests(slotParms, con);

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
               out.println("time you are requesting is " + slotParms.grest_num + " per " +slotParms.grest_per+ ".");
               out.println("<BR><BR>Guest Restriction = " + slotParms.rest_name);
               out.println("<BR><BR>");
               //
               //  Return to _slot to change the player order
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
               out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
               out.println("</form></font>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;
            }

            //
            //  Custom for Oakmont CC - if more than 7 days in advance, must be at least 1 guest for each member
            //
            if (club.equals( "oakmont" )) {      // if Oakmont CC

               if (slotParms.ind > 7) {          // if this date is more than 7 days ahead

                  //
                  //  More than 7 days in advance - must have at least 1 guest per member !!
                  //
                  error = false;
                  oakskip = true;                 // set in case we make it through here (for later)

                  if (slotParms.members > 2) {

                     error = true;             // must be error

                  } else {

                     if (slotParms.members > 1 && slotParms.guests < 2) {

                        error = true;             // must be error
                     }

                     if (slotParms.members > 0 && slotParms.guests < 1) {

                        error = true;             // must be error
                     }
                  }

                  if (error == true) {        // if too many guests

                     out.println(SystemUtils.HeadTitle("Min Num Guests Not Specified - Reject"));
                     out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                     out.println("<hr width=\"40%\">");
                     out.println("<BR><H3>Insufficient Number of Guests Specified</H3><BR>");
                     out.println("<BR><BR>Sorry, you must have at least 1 guest per member when making<BR>");
                     out.println("a tee time more than 7 days in advance.");
                     out.println("<BR><BR>");
                     //
                     //  Return to _slot to change the player order
                     //
                     out.println("<font size=\"2\">");
                     out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
                     out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                     out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
                     out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                     out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
                     out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
                     out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
                     out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
                     out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
                     out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
                     out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
                     out.println("</form></font>");
                     out.println("</CENTER></BODY></HTML>");
                     out.close();
                     return;
                  }
                    
               }

               if (slotParms.guests > 1) {       // if 2 or more guests and Oakmont CC

                  //
                  // **********************************************************************
                  //  Oakmont - Check for max # of guest tee times exceeded (Wed & Fri)
                  // **********************************************************************
                  //
                  error = false;                             // init error indicator

                  try {

                     error = verifySlot.oakmontGuests(slotParms, con);

                  }
                  catch (Exception e5) {

                     msg = "Check Oakmont Guest Tee Times. ";

                     dbError(out, e5, msg);
                     return;
                  }

                  if (error == true) {      // a member exceed the max allowed tee times per month

                     out.println(SystemUtils.HeadTitle("Max Num Guest Tee Times Exceeded - Reject"));
                     out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                     out.println("<hr width=\"40%\">");

                     if (slotParms.day.equals( "Wednesday" ) || slotParms.day.equals( "Friday" )) {  // if Wednesday or Friday
                        out.println("<BR><H3>Multiple Guests Not Allowed</H3><BR>");
                        out.println("<BR><BR>Sorry, multiple guests are not allowed during the selected time<BR>");
                        out.println("for this day.  You are allowed one guest per member.");
                     } else {  // must be Sat, Sun or Holiday
                        out.println("<BR><H3>Number of Family Guest Tee Times Exceeded Limit For The Day</H3><BR>");
                        out.println("<BR><BR>Sorry, there are already 2 tee times with family guests<BR>");
                        out.println("scheduled for today.  You are only allowed one family guest per member.");
                     }
                     out.println("<BR><BR>");
                     //
                     //  Return to _slot to change the player order
                     //
                     out.println("<font size=\"2\">");
                     out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
                     out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                     out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
                     out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                     out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
                     out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
                     out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
                     out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
                     out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
                     out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
                     out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
                     out.println("</form></font>");
                     out.println("</CENTER></BODY></HTML>");
                     out.close();
                     return;
                  }
               }
                 
            }      // end of IF Oakmont

            //
            //  If Hazeltine National or Old Oaks, then make sure member entered a guest's name after each guest type
            //
            if (club.equals( "hazeltine" ) || club.equals( "oldoaks" )) { // if Hazeltine National or Old Oaks

               error = verifySlot.checkGuestNames(slotParms, con);

               if (error == true) {      // a member exceed the max allowed tee times per month

                  out.println(SystemUtils.HeadTitle("Guest Name Not Provided - Reject"));
                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><H3>Invalid Guest Request</H3><BR>");
                  out.println("<BR><BR>Sorry, you must provide the full name of your guest(s).");
                  out.println("<BR>Please enter a space followed by the guest's name immediately after the guest type");
                  out.println("<BR>in the player field.  Click your mouse in the player field, move the cursor");
                  out.println("<BR>to the end of the guest type, hit the space bar and then type the full name.");
                  out.println("<BR><BR>");
                  //
                  //  Return to _slot to change the player name
                  //
                  out.println("<font size=\"2\">");
                  out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                  out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                  out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
                  out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
                  out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
                  out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
                  out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
                  out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
                  out.println("</form></font>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;
               }
            }      // end of if Hazeltine
         }      // end of if Westchester
      }      // end of if guests

      //
      //  Custom for Oakmont CC - if less than 30 days in advance, guest tee times cannot be changed -
      //                          players may be added, but not removed.
      //
      if (club.equals( "oakmont" ) && slotParms.ind < 30) {      // if Oakmont CC

         //
         //  See if any guests were in tee time prior to this request (player was specified, but not member)
         //
         if ((!slotParms.oldPlayer1.equals( "" ) && !slotParms.oldPlayer1.equalsIgnoreCase( "x" ) && slotParms.oldUser1.equals( "" )) ||
             (!slotParms.oldPlayer2.equals( "" ) && !slotParms.oldPlayer2.equalsIgnoreCase( "x" ) && slotParms.oldUser2.equals( "" )) ||
             (!slotParms.oldPlayer3.equals( "" ) && !slotParms.oldPlayer3.equalsIgnoreCase( "x" ) && slotParms.oldUser3.equals( "" )) ||
             (!slotParms.oldPlayer4.equals( "" ) && !slotParms.oldPlayer4.equalsIgnoreCase( "x" ) && slotParms.oldUser4.equals( "" )) ||
             (!slotParms.oldPlayer5.equals( "" ) && !slotParms.oldPlayer5.equalsIgnoreCase( "x" ) && slotParms.oldUser5.equals( "" ))) {

            //
            //   At least 1 guest had already been in the tee time - see if any players changed
            //
            if (!slotParms.oldPlayer1.equals( "" ) && !slotParms.oldPlayer1.equals( slotParms.player1 )) {

               error = true;
            }
            if (!slotParms.oldPlayer2.equals( "" ) && !slotParms.oldPlayer2.equals( slotParms.player2 )) {

               error = true;
            }
            if (!slotParms.oldPlayer3.equals( "" ) && !slotParms.oldPlayer3.equals( slotParms.player3 )) {

               error = true;
            }
            if (!slotParms.oldPlayer4.equals( "" ) && !slotParms.oldPlayer4.equals( slotParms.player4 )) {

               error = true;
            }

            if (error == true) {        // guest time changed - old player changed

               out.println(SystemUtils.HeadTitle("Min Num Guests Not Specified - Reject"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H3>Players Changed in a Guest Time</H3><BR>");
               out.println("<BR><BR>Sorry, you cannot remove players from a tee time containing guests<BR>");
               out.println("within 30 days of the tee time.  You may only add new members or guests.");
               out.println("<BR><BR>");
               //
               //  Return to _slot to change the player order
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
               out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
               out.println("</form></font>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;
            }
         }
      }      // end of if Oakmont

      //
      //  Custom for Milwaukee CC - if more than 7 days in advance, must be 1 member with 2 or 3 guests
      //
      boolean mccskip = false;            
        
      if (club.equals( "milwaukee" ) && slotParms.ind > 7) {

         if (!slotParms.user1.equals( user ) || !slotParms.user4.equals( "" ) || slotParms.guests < 2) {

            out.println(SystemUtils.HeadTitle("Min Num Guests Not Specified - Reject"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Invalid Guest Time Request</H3><BR>");
            out.println("<BR><BR>Sorry, you must specify at least 2 guests following your name<BR>");
            out.println("when making a tee time more than 7 days in advance. Only 1 member allowed.");
            out.println("<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;

         } else {   // MCC Guest Time ok

            mccskip = true;     // ok to skip the 'days in advance' test
         }
      }

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
         out.println("Contact the proshop if you have any questions.<br>");
         out.println("<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
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
         out.println("Contact the proshop if you have any questions.<br>");
         out.println("<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
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
               if (club.equals( "oldoaks" )) {
                  out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is part of a tee time request for this date.<br><br>");
               } else {
                  out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is part of a lottery request for this date.<br><br>");
               }
            } else {
               out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is already scheduled to play on this date at <b>" +tmsg+ "</b>.<br><br>");
            }
            out.println("A player can only be scheduled once per day.<br><br>");
         }
         out.println("Please remove this player or try a different date.<br>");
         out.println("Contact the proshop if you have any questions.");
         out.println("<BR><BR>");
         out.println("If you are already scheduled for this date and would like to remove yourself<br>");
         out.println("from that tee time, use the 'Go Back' button to return to the tee sheet and <br>");
         out.println("locate the time stated above, or click on the 'My Tee Times' tab.");
         out.println("<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
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

         //
         //  skip if Oakmont guest time, or Milwaukee Guest Time
         //
         if (!club.equals( "oakmont" ) || oakskip == false) {

            if (mccskip == false) {                                 // skip if Milwaukee Guest Time

               try {

                  error = verifySlot.checkDaysAdv(slotParms, con);

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
                  //
                  //  Return to _slot
                  //
                  out.println("<font size=\"2\">");
                  out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                  out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                  out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
                  out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
                  out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
                  out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
                  out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
                  out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
                  out.println("</form></font>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;
               }
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

               msg = "Check Belle Haven Error. ";

               dbError(out, e22, msg);
               return;
            }

            if (error == true) {          // if we hit on a violation

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Weekend Tee Time Limit Exceeded for Member</H3><BR>");
               out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is an Elective member and has");
               out.println("<BR>already played 10 times on weekends or holidays this year.<br><br>");
               out.println("Remove this player and try again.<br>");
               out.println("Contact the golf shop if you have any questions.<br><br>");
               out.println("<BR><BR>");
               //
               //  Return to _slot
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
               out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
               out.println("</form></font>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;
            }
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

               //
               //  If Hazeltine National, then process the Unaccompanied Guests (Sponsored Group)
               //
               if (club.equals( "hazeltine" )) {      // if Hazeltine National

                  int rcode = 0;

                  try {

                     rcode = verifySlot.checkSponsGrp(slotParms, con);  // verify Sponsored Group for Hazeltine
                  }
                  catch (Exception e29) {

                     msg = "Check Hazeltine's Sponsored Group - Member_slot. ";

                     dbError(out, e29, msg);
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
                           out.println("You already have 2 Sponsored Groups scheduled today.<br><br>");
                        }
                     }
                     out.println("Please change this request or try a different date.<br>");
                     out.println("Contact the  golf shop if you have any questions.<br>");
                     out.println("<BR><BR>");
                     //
                     //  Return to _slot to change the player order
                     //
                     out.println("<font size=\"2\">");
                     out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
                     out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                     out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
                     out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                     out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
                     out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
                     out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
                     out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
                     out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
                     out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
                     out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
                     out.println("</form></font>");
                     out.println("</CENTER></BODY></HTML>");
                     out.close();
                     return;
                  }
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
                  out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                  out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
                     out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
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
                     out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
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
            //
            //  Return to _slot
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
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
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }


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

   //
   //  Verification complete -
   //  Update the tee slot in teecurr
   //
   try {

      PreparedStatement pstmt6 = con.prepareStatement (
         "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
         "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
         "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
         "hndcp4 = ?, show1 = ?, show2 = ?, show3 = ?, show4 = ?, player5 = ?, username5 = ?, " +
         "p5cw = ?, hndcp5 = ?, show5 = ?, notes = ?, memNew = ?, memMod = ?, " +
         "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
         "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
         "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ? " +
         "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt6.clearParameters();        // clear the parms
      pstmt6.setString(1, slotParms.player1);
      pstmt6.setString(2, slotParms.player2);
      pstmt6.setString(3, slotParms.player3);
      pstmt6.setString(4, slotParms.player4);
      pstmt6.setString(5, slotParms.user1);
      pstmt6.setString(6, slotParms.user2);
      pstmt6.setString(7, slotParms.user3);
      pstmt6.setString(8, slotParms.user4);
      pstmt6.setString(9, slotParms.p1cw);
      pstmt6.setString(10, slotParms.p2cw);
      pstmt6.setString(11, slotParms.p3cw);
      pstmt6.setString(12, slotParms.p4cw);
      pstmt6.setFloat(13, slotParms.hndcp1);
      pstmt6.setFloat(14, slotParms.hndcp2);
      pstmt6.setFloat(15, slotParms.hndcp3);
      pstmt6.setFloat(16, slotParms.hndcp4);
      pstmt6.setShort(17, slotParms.show1);
      pstmt6.setShort(18, slotParms.show2);
      pstmt6.setShort(19, slotParms.show3);
      pstmt6.setShort(20, slotParms.show4);
      pstmt6.setString(21, slotParms.player5);
      pstmt6.setString(22, slotParms.user5);
      pstmt6.setString(23, slotParms.p5cw);
      pstmt6.setFloat(24, slotParms.hndcp5);
      pstmt6.setShort(25, slotParms.show5);
      pstmt6.setString(26, slotParms.notes);
      pstmt6.setInt(27, memNew);
      pstmt6.setInt(28, memMod);
      pstmt6.setString(29, slotParms.mNum1);
      pstmt6.setString(30, slotParms.mNum2);
      pstmt6.setString(31, slotParms.mNum3);
      pstmt6.setString(32, slotParms.mNum4);
      pstmt6.setString(33, slotParms.mNum5);
      pstmt6.setString(34, slotParms.userg1);
      pstmt6.setString(35, slotParms.userg2);
      pstmt6.setString(36, slotParms.userg3);
      pstmt6.setString(37, slotParms.userg4);
      pstmt6.setString(38, slotParms.userg5);
      pstmt6.setString(39, slotParms.orig_by);
      pstmt6.setInt(40, slotParms.p91);
      pstmt6.setInt(41, slotParms.p92);
      pstmt6.setInt(42, slotParms.p93);
      pstmt6.setInt(43, slotParms.p94);
      pstmt6.setInt(44, slotParms.p95);

      pstmt6.setLong(45, date);
      pstmt6.setInt(46, time);
      pstmt6.setInt(47, fb);
      pstmt6.setString(48, slotParms.course);
      pstmt6.executeUpdate();      // execute the prepared stmt

      pstmt6.close();

   }
   catch (Exception e6) {

      msg = "Update Tee Time. ";

      dbError(out, e6, msg);
      return;
   }

   //
   //  If Hazeltine National, then check for an associated tee time (w/e's and holidays)
   //
   if (club.equals( "hazeltine" )) {      // if Hazeltine National

      verifySlot.Htoggle(date, time, fb, slotParms, con);
   }


   //
   //  Build the HTML page to confirm reservation for user
   //
   out.println(SystemUtils.HeadTitle("Member Tee Slot Page"));
   out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<center><img src=\"/" +rev+ "/images/foretees.gif\"><hr width=\"40%\">");
   out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

   if (req.getParameter("remove") != null) {

      out.println("<p>&nbsp;</p><p>&nbsp;Thank you!&nbsp;&nbsp;The reservation has been cancelled.</p>");
   } else {

      out.println("<p>&nbsp;</p><p>&nbsp;Thank you!&nbsp;&nbsp;Your reservation has been accepted and processed.</p>");

      if (xcount > 0 && xhrs > 0) {            // if any X's were specified 

         out.println("<p>&nbsp;</p>All player positions reserved by an 'X' must be filled within " + xhrs + " hours of the tee time.");
         out.println("<br>If not, the system will automatically remove the X.<br>");
      }

      if (club.equals( "piedmont" )) {        // if Piedmont Driving Club
        
         int piedmontStatus = verifySlot.checkPiedmont(date, time, slotParms.day);     // check if special time
  
         if (piedmontStatus == 1) {      // if Sat or Sun and before noon
           
            out.println("<p><b>Notice From Golf Shop:</b>&nbsp;&nbsp;Please be aware that your group will be assigned");
            out.println("<br>a caddie or forecaddie regardless of the number of players.</p>");
  
         } else {
           
            if (piedmontStatus == 2) {      // if other special time

               out.println("<p><b>Notice From Golf Shop:</b>&nbsp;&nbsp;If this tee time becomes a threesome or ");
               out.println("foursome and <br>a caddie is not already requested, a forecaddie will be assigned ");
               out.println("to your group.</p>");
            }
         }
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
      out.println("</input></form></font>");

   } else {

      if (index.equals( "888" )) {       // if from Member_searchmem

         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_searchmem\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</input></form></font>");

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
   if (sendemail != 0) {

      //
      //  allocate a parm block to hold the email parms
      //
      parmEmail parme = new parmEmail();          // allocate an Email parm block

      //
      //  Set the values in the email parm block
      //
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

      parme.p91 = slotParms.p91;
      parme.p92 = slotParms.p92;
      parme.p93 = slotParms.p93;
      parme.p94 = slotParms.p94;
      parme.p95 = slotParms.p95;

      parme.course = slotParms.course;
      parme.day = slotParms.day;

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
 //  Process 'check for guests' request for Oalmont CC
 // ************************************************************************

 private boolean checkOakGuests(long date, int time, int fb, String course, Connection con) {


   ResultSet rs = null;

   boolean guests = false;

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

   //
   //  break down date of tee time
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
     
   if (ind < 30) {  
  
      //
      //   Within 30 days - check if there are already guests in this tee time
      //
      try {

         PreparedStatement pstmt = con.prepareStatement (
            "SELECT * " +
            "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);         // put the parm in pstmt
         pstmt.setInt(2, time);
         pstmt.setInt(3, fb);
         pstmt.setString(4, course);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            player1 = rs.getString( "player1" );
            player2 = rs.getString( "player2" );
            player3 = rs.getString( "player3" );
            player4 = rs.getString( "player4" );
            user1 = rs.getString( "username1" );
            user2 = rs.getString( "username2" );
            user3 = rs.getString( "username3" );
            user4 = rs.getString( "username4" );
            player5 = rs.getString( "player5" );
            user5 = rs.getString( "username5" );
         }
         pstmt.close();

         //
         //  Check if any guests - if player specified, but not a member
         //
         if ((!player1.equals( "" ) && !player1.equalsIgnoreCase( "x" ) && user1.equals( "" )) ||
             (!player2.equals( "" ) && !player2.equalsIgnoreCase( "x" ) && user2.equals( "" )) ||
             (!player3.equals( "" ) && !player3.equalsIgnoreCase( "x" ) && user3.equals( "" )) ||
             (!player4.equals( "" ) && !player4.equalsIgnoreCase( "x" ) && user4.equals( "" )) ||
             (!player5.equals( "" ) && !player5.equalsIgnoreCase( "x" ) && user5.equals( "" ))) {

            guests = true;
         }

      }
      catch (Exception ignore) {
      }
   }

   return(guests);

 }       // end of Oakmont checkOakGuests


 // ************************************************************************
 //  Process cancel request (Return w/o changes) from Member_slot (HTML)
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
   String day = req.getParameter("day");              //  name of the day

   //
   //  Convert the values from string to int
   //
   try {
      date = Long.parseLong(sdate);
      time = Integer.parseInt(stime);
      fb = Integer.parseInt(sfb);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

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
      count = pstmt1.executeUpdate();      // execute the prepared stmt

      pstmt1.close();

   }
   catch (Exception ignore) {

   }

   //
   //  If Hazeltine National, then check for an associated tee time (w/e's and holidays)
   //
   if (club.equals( "hazeltine" )) {      // if Hazeltine National

      verifySlot.HclearInUse(date, time, fb, course, day, con);
   }

   //
   //  Prompt user to return to Member_sheet or Member_teelist (index = 999)
   //
   out.println("<HTML>");
   out.println("<HEAD>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
   out.println("<Title>Member Tee Slot Page</Title>");

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
      out.println("</input></form></font>");

   } else {

      if (index.equals( "888" )) {       // if from Member_searchmem

         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Member_searchmem\">");
         out.println("</HEAD>");
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
         out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
         out.println("<BR><BR>");

         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_searchmem\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</input></form></font>");

      } else {

         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Member_jump?index=" + index + "&course=" + course + "\">");
         out.println("</HEAD>");
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
         out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
         out.println("<BR><BR>");

         out.println("<font size=\"2\">");
         out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</form></font>");

      }
   }
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 // Invalid data received - reject request
 // *********************************************************

 private void invData(PrintWriter out, String p1, String p2, String p3, String p4, String p5) {


   if (p1.equals( "" )) {

      p1 = " ";             // use space instead of null
   }

   if (p2.equals( "" )) {

      p2 = " ";             // use space instead of null
   }

   if (p3.equals( "" )) {

      p3 = " ";             // use space instead of null
   }

   if (p4.equals( "" )) {

      p4 = " ";             // use space instead of null
   }

   if (p5.equals( "" )) {

      p5 = " ";             // use space instead of null
   }

   out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
   out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H3>Invalid Data Received</H3><BR>");
   out.println("<BR><BR>Sorry, a name you entered is not valid.<BR>");
   out.println("<BR>You entered:&nbsp;&nbsp;&nbsp;'" + p1 + "',&nbsp;&nbsp;&nbsp;'" + p2 + "',&nbsp;&nbsp;&nbsp;'" + p3 + "',&nbsp;&nbsp;&nbsp;'" + p4 + "',&nbsp;&nbsp;&nbsp;'" + p5 + "'");
   out.println("<BR><BR>Member names must be entered exactly as they exist in the system (so we can identify them).");
   out.println("<BR><BR>");
   out.println("Please use the Partner List or Member List on the right side of the page to select the member names.");
   out.println("<BR>Simply <b>click on the desired name</b> in the list to add the member to the tee time.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
   return;
 }


 // *********************************************************
 // Invalid member name received - reject request
 // *********************************************************

 private void invMem(PrintWriter out, String p1) {


   if (p1.equals( "" )) {

      p1 = " ";             // use space instead of null
   }

   out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
   out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H3>Invalid Member Name Received</H3><BR>");
   out.println("<BR><BR>Sorry, a name you entered is not recognized as a valid member.<BR>");
   out.println("<BR>You entered:&nbsp;&nbsp;&nbsp;'" + p1 + "'");
   out.println("<BR><BR>Member names must be entered exactly as they exist in the system (so we can identify them).");
   out.println("<BR><BR>");
   out.println("Please use the Partner List or Member List on the right side of the page to select the member names.");
   out.println("<BR>Simply <b>click on the desired name</b> in the list to add the member to the tee time.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
   return;
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

}