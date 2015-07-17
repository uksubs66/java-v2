/***************************************************************************************
 *   Member_teelist:  This servlet will search teecurr for this member and display
 *                    a list of current tee times.  Also, search evntsup for any
 *                    events and lreqs for any lottery requests.
 *
 *
 *   called by:  member_main.htm
 *
 *   created: 1/10/2002   Bob P.
 *
 *   last updated:
 *
 *        5/18/10   Fix noAccessAfterLottery custom processing to check for lottery_color in teecurr to determine if lottery processed (case 1827).
 *        4/26/10   Black Rock CC (blackrockcountryclub) - allow 6 months to be displayed.
 *        4/26/10   Brae Burn - do not allow members to access tee times after the lottery has been processed (case 1827).
 *        4/20/10   Bonnie Briar - do not allow members to access tee times after the lottery has been processed (case 1822).
 *        4/16/10   Wollaston GC - do not allow members to access tee times on Monday - all day (case 1819).
 *       12/09/09   When looking for events only check those that are active.
 *       10/30/09   Don't display links for calendar items when logged in under one activity id and viewing another
 *       10/27/09   Changes to allow access to Individual and Group lessons from the Tee Time Calendar
 *       10/07/09   If more than one activity in system, allow members to select the activity.
 *       10/01/09   Check for FlexRez (aka genres) defined for club before checking for activities.
 *        9/02/09   Do not include lottery tee times that have been pre-booked before lottery has been approved (case 1703).
 *        5/06/09   If the days in advance equals the days to view, use the time value to determine if member can view the sheet.
 *        4/30/09   Added status to event signup display to show if the member is Registered or on the Wait List (case 1587).
 *        1/02/09   Timarron CC - add custom days in advance to view tee sheets (case 1595).
 *       10/23/08   Added Wait List signups to list
 *       10/03/08   Check for replacement text for the word "Lottery" when email is for a lottery request.
 *        5/27/08   Do not allow member to access a tee time from calendar if cutoff date/time reached.
 *        5/03/08   Patterson Club - display 6 months of calendars (case 1471).
 *        3/26/08   Do not allow member to select a tee time that has already passed (case 1431).
 *        2/10/08   Robert Trent Jones - display 6 months of calendars.
 *        7/14/07   Desert Highlands - display 12 months of calendars.
 *        6/20/07   Get all lesson times for the day but filter any with num=0 (subsequent times).
 *        5/22/07   Remove custom days to view tee sheets.
 *        5/09/07   DaysAdv array no longer stored in session block - Using call to SystemUtils.daysInAdv
 *        4/12/07   The CC - custom, show all events, including guest only (case #1103).
 *        2/14/07   Mission Viejo - custom, only allow 10 days in advance for members to view tee sheets.
 *       02/05/07   Fix verbiage for TLT system.
 *       01/24/07   Changes for Interlachen Spa.
 *       01/05/07   Changes for TLT system - display notifications on calanders / redirect to MemberTLT_sheet instead
 *       10/18/06   Westchester - allow access to tee times 90 days in adv.
 *        7/11/06   If tee time is during a shotgun event, do not allow link and show as shotgun time.
 *        7/20/05   Forest Highlands - custom, only allow 5 days in advance for members to view tee sheets.
 *        1/24/05   Ver 5 - change club2 to club5.
 *        1/10/05   RDP Add check for member restriction with ALL member types specified.
 *       11/18/04   Ver 5 - Change to a calendar format.
 *       10/06/04   Ver 5 - add sub-menu support.
 *        2/25/04   RDP Check for Unaccompanied Guest tee times tied to the member.
 *        1/13/04   JAG Modified to match new color scheme
 *        7/18/03   Enhancements for Version 3 of the software.
 *                  Add lottery processing.
 *        2/24/03   Add event sign up processing.
 *        9/18/02   Enhancements for Version 2 of the software.
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
import com.foretees.common.DaysAdv;
import com.foretees.common.getRests;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.getActivity;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyCustom;


public class Member_teelist extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     
         doGet(req, resp);
 }
 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   PreparedStatement pstmt1 = null;
   //Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rsev = null;

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //String omit = "";
   String ampm = "";
   //String player1 = "";
   //String sfb = "";
   //String submit = "";
   String monthName = "";
   //String name = "";
   String course = "";
   String zone = "";
   String lname = "";
   String lgname = "";
   String ename = "";
   String dayname = "";
   String rest = "";
   String rest5 = "";
   String p5 = "";
   String stime = "";
   String stime2 = "";
   String ltype = "";
   //String url = "";
   String lotteryName = "";
   String lottery_color = "";
   String lotteryText = "";         // replacement text for "Lottery"
   String activity_name = "";

   int root_id = 0;
   long date = 0;
   long edate = 0;
   long sdate = 0;
   long ldate = 0;
   long lottid = 0;
   //long temp = 0;
   long todayDate = 0;
   long tomorrowDate = 0;

   int mm = 0;
   int dd = 0;
   int yy = 0;
   int month = 0;
   int months = 0;
   int day = 0;
   int numDays = 0;
   int today = 0;
   int day_num = 0;
   int year = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   //int ptime = 0;
   int ctime = 0;
   int index = 0;
   int i = 0;
   int i2 = 0;
   int max = 30;    // default
   int col = 0;
   int multi = 0;
   int lottery = 0;
   int lstate = 0;
   //int sdays = 0;
   //int sdtime = 0;
   //int edays = 0;
   //int edtime = 0;
   //int pdays = 0;
   //int pdtime = 0;
   int slots = 0;
   //int advance_days = 0;
   int fives = 0;
   //int fivesomes = 0;
   int signUp = 0;
   int fb = 0;
   int proid = 0;
   int etype = 0;
   int wait = 0;
   int lesson_id = 0;
   int selected_act_id = 0;         // selected activity id (which activities to include in calendar)
   int returned_activity_id = 0;         // activity id returned from queries (used to filter which items to display as links when displaying ALL)

   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;

   //
   // See what activity mode we are in
   //
   int sess_activity_id = 0;

   try { sess_activity_id = (Integer)session.getAttribute("activity_id"); }
   catch (Exception ignore) { }

   String user = (String)session.getAttribute("user");      // get username
   String club = (String)session.getAttribute("club");      // get club name
   String caller = (String)session.getAttribute("caller");
   String mship = (String)session.getAttribute("mship");             // get member's mship type
   String mtype = (String)session.getAttribute("mtype");             // get member's mtype
   //DaysAdv daysArray = (DaysAdv)session.getAttribute("daysArray");   // get array object for 'days in adv' from Login
   
   // Setup the daysArray
   DaysAdv daysArray = new DaysAdv();          // allocate an array object for 'days in adv'
   daysArray = SystemUtils.daysInAdv(daysArray, club, mship, mtype, user, con);
   
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);

   //boolean events = false;
   boolean didone = false;
   boolean allRest = false;
   boolean allAct = false;
   boolean restrictAllTees = false;         // restrict all tee times

   //
   //  boolean for clubs that want to block member access to tee times after a lottery has been processed.
   //
   //  NOTE:  see same flag in Member_sheet and Member_teelist_list and Member_teelist_mobile !!!!!!!!!!!!!!!!!!
   //
   boolean noAccessAfterLottery = false;

   if (club.equals( "bonniebriar" ) || club.equals("braeburncc")) {   // add other clubs here!!
       
       noAccessAfterLottery = true;      // no member access after lottery processed
   }
   
   
   String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
                          "September", "October", "November", "December" };

   String [] day_table = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //
   //  Num of days in each month
   //
   int [] numDays_table = { 0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

   //
   //  Num of days in Feb indexed by year starting with 2000 - 2040
   //
   int [] feb_table = { 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29,  +
                            28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29 };

   //
   //  Arrays to hold the event indicators - one entry per day (is there one of the events on this day)
   //
   int [] eventA = new int [32];          //  events (32 entries so we can index by day #)
   int [] teetimeA = new int [32];        //  tee times
   int [] lessonA = new int [32];         //  lesson times
   int [] lessongrpA = new int [32];      //  lesson group times
   int [] lotteryA = new int [32];        //  lotteries
   int [] waitlistA = new int [32];       //  wait list signups
   int [] activitiesA = new int [32];       //  activity signups
   
   
   // 
   //  Get activity id if user changed his selection
   //
   if (req.getParameter("activity_id") != null) {

      String tempId = req.getParameter("activity_id");

      try {
         selected_act_id = Integer.parseInt(tempId);
      }
      catch (NumberFormatException e) {  
         selected_act_id = sess_activity_id;      // default to current activity
      }
      
   } else {
      
      selected_act_id = sess_activity_id;      // default to current activity
   }

   // Set a boolean for easy reference later
   if (selected_act_id == 999) {
       allAct = true;
   } else {
       allAct = false;
   }


   try {
      
      //
      // Get the days in advance and time for advance from the club db
      //
      getClub.getParms(con, parm, sess_activity_id);        // get the club parms

      multi = parm.multi;
      lottery = parm.lottery;
      zone = parm.adv_zone;

      //
      //  use the member's mship type to determine which 'days in advance' parms to use
      //
      verifySlot.getDaysInAdv(con, parm, mship, sess_activity_id);        // get the days in adv data for this member

      max = parm.memviewdays +1;        // days this member can view tee sheets
      
   }
   catch (Exception exc) {
   }


   //
   //  get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);     // get current time
   int cal_min = cal.get(Calendar.MINUTE);

   //
   //    Adjust the time based on the club's time zone (we are Central)
   //
   ctime = (cal_hourDay * 100) + cal_min;        // get time in hhmm format

   ctime = SystemUtils.adjustTime(con, ctime);   // adjust the time

   if (ctime < 0) {                // if negative, then we went back or ahead one day

      ctime = 0 - ctime;           // convert back to positive value

      if (ctime < 1200) {           // if AM, then we rolled ahead 1 day

         //
         // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
         //
         cal.add(Calendar.DATE,1);                     // get next day's date

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         cal.add(Calendar.DATE,-1);                     // get yesterday's date
      }
   }

   cal_hourDay = ctime / 100;                      // get adjusted hour
   cal_min = ctime - (cal_hourDay * 100);          // get minute value

   yy = cal.get(Calendar.YEAR);
   mm = cal.get(Calendar.MONTH) +1;
   dd = cal.get(Calendar.DAY_OF_MONTH);
   day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

   todayDate = (yy * 10000) + (mm * 100) + dd;     // create a date field of yyyymmdd

   year = yy;
   month = mm;
   day = dd;

   today = day;                                  // save today's number
   
   
   //
   //  Get tomorrow's date for cutoff test
   //
   cal.add(Calendar.DATE,1);                     // get next day's date
   yy = cal.get(Calendar.YEAR);
   mm = cal.get(Calendar.MONTH) +1;
   dd = cal.get(Calendar.DAY_OF_MONTH);

   tomorrowDate = (yy * 10000) + (mm * 100) + dd;     // create a date field of yyyymmdd
   
   yy = 0;       // reset
   mm = 0;
   dd = 0;
     

   //
   //  Check if its earlier than the time specified for days in advance
   //
   if (parm.advtime1 > ctime) {

      //
      //  If this club set the max days to view equal to the days in advance, and all days in advance are the same, and
      //  all times of the days are the same, then adjust the days to view.  This is the only way we can do this!!
      //
      if (parm.memviewdays > 0 && parm.memviewdays == parm.advdays1 && parm.memviewdays == parm.advdays2 && parm.memviewdays == parm.advdays3 && 
          parm.memviewdays == parm.advdays4 && parm.memviewdays == parm.advdays5 && parm.memviewdays == parm.advdays6 && parm.memviewdays == parm.advdays7 &&
          parm.advtime1 == parm.advtime2 && parm.advtime1 == parm.advtime3 && parm.advtime1 == parm.advtime4 && parm.advtime1 == parm.advtime5 && 
          parm.advtime1 == parm.advtime6 &&  parm.advtime1 == parm.advtime7) {

         max--;
      }
   }

   
   //
   //  Custom 1595 for Timarron - days to view must match the days in advance so members cannot view any sooner than they can book.
   //
   if (club.equals( "timarroncc" ) && sess_activity_id == 0) {   // per Pro's request

      max = 5;                               // normally 4 days in advance (this method requires max+1 value)

      if (day_num == 2 || ctime < 700) {     // if Monday (any time), then do not allow access to Friday, OR if before 7:00 AM

         max = 4;
      }
   }

    
   //
   //  Wollaston GC - if today is Monday, then do not allow access to any tee times, no matter what day the tee sheet is for (case 1819). 
   //
   if (club.equals( "wollastongc" )) {

      restrictAllTees = verifyCustom.checkWollastonMon();         // restrict all day if today is Monday
   }
   

   //
   //   build the HTML page for the display
   //
   out.println(SystemUtils.HeadTitle2("Member My " + ((IS_TLT) ? "Activities" : "Tee Times")));
   //
   //*******************************************************************
   //  Scripts to complete and submit the forms
   //*******************************************************************
   //
    out.println("<script type=\"text/javascript\">");
    out.println("<!--");
    out.println("function editNotify(pId, pTime) {");
    out.println(" var f = document.forms['frmEditNotify'];");
    out.println(" f.notifyId.value = pId;");
    out.println(" f.stime.value = pTime;");
    out.println(" f.submit();");
    out.println("}");
    out.println("// -->");
    out.println("</script>");

    out.println("<form method=post action=/" + rev + "/servlet/MemberTLT_slot name=frmEditNotify id=frmEditNotify target=_top>");
    out.println("<input type=hidden name=notifyId value=\"\">");
    out.println("<input type=hidden name=stime value=\"\">");  
    out.println("<input type=hidden name=index value=\"999\">");  
    out.println("</form>");
      
   out.println("<script type=\"text/javascript\">");            // Tee Time Script
   out.println("<!--");
   out.println("function exeTtimeForm(date, day, p5, course, fb, stime) {");
      //     out.println("alert(date);");
      out.println("document.forms['teeTime'].date.value = date;");
      out.println("document.forms['teeTime'].day.value = day;");
      out.println("document.forms['teeTime'].p5.value = p5;");
      out.println("document.forms['teeTime'].course.value = course;");
      out.println("document.forms['teeTime'].fb.value = fb;");
      out.println("document.forms['teeTime'].stime.value = stime;");
      out.println("document.forms['teeTime'].submit();");        // submit the form
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script


   out.println("<script type=\"text/javascript\">");            // Lottery State 2
   out.println("<!--");
   out.println("function exeLott2Form(date, day, p5, course, fb, stime, lname, lottid, slots, lstate) {");
      out.println("document.forms['lott2'].date.value = date;");
      out.println("document.forms['lott2'].day.value = day;");
      out.println("document.forms['lott2'].p5.value = p5;");
      out.println("document.forms['lott2'].course.value = course;");
      out.println("document.forms['lott2'].fb.value = fb;");
      out.println("document.forms['lott2'].stime.value = stime;");
      out.println("document.forms['lott2'].lname.value = lname;");
      out.println("document.forms['lott2'].lottid.value = lottid;");
      out.println("document.forms['lott2'].slots.value = slots;");
      out.println("document.forms['lott2'].lstate.value = lstate;");
      out.println("document.forms['lott2'].submit();");        // submit the form
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script


   out.println("<script type=\"text/javascript\">");                    // Lottery State 5
   out.println("<!--");
   out.println("function exeLott5Form(date, day, p5, course, fb, stime, lname, lottid, lstate) {");
      out.println("document.forms['lott5'].date.value = date;");
      out.println("document.forms['lott5'].day.value = day;");
      out.println("document.forms['lott5'].p5.value = p5;");
      out.println("document.forms['lott5'].course.value = course;");
      out.println("document.forms['lott5'].fb.value = fb;");
      out.println("document.forms['lott5'].stime.value = stime;");
      out.println("document.forms['lott5'].lname.value = lname;");
      out.println("document.forms['lott5'].lottid.value = lottid;");
      out.println("document.forms['lott5'].lstate.value = lstate;");
      out.println("document.forms['lott5'].submit();");        // submit the form
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script


   out.println("<script type=\"text/javascript\">");                     // Events
   out.println("<!--");
   out.println("function exeEventForm(name, course) {");
      out.println("document.forms['eventForm'].course.value = course;");
      out.println("document.forms['eventForm'].name.value = name;");
      out.println("document.forms['eventForm'].index.value = '999';");
      out.println("document.forms['eventForm'].submit();");        // submit the form
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script


   out.println("<script type=\"text/javascript\">");                     // Non-Signup Events
   out.println("<!--");
   out.println("function exeEventForm2(name) {");
      out.println("document.forms['eventForm2'].event.value = name;");
      out.println("document.forms['eventForm2'].submit();");        // submit the form
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script


   out.println("<script type=\"text/javascript\">");                     // Lesson Times
   out.println("<!--");
   out.println("function exeLtimeForm(proid, calDate, date, time, day, ltype, lesson_id, activity_id) {");
      out.println("document.forms['LtimeForm'].proid.value = proid;");
      out.println("document.forms['LtimeForm'].calDate.value = calDate;");
      out.println("document.forms['LtimeForm'].date.value = date;");
      out.println("document.forms['LtimeForm'].time.value = time;");
      out.println("document.forms['LtimeForm'].day.value = day;");
      out.println("document.forms['LtimeForm'].ltype.value = ltype;");
      out.println("document.forms['LtimeForm'].lesson_id.value = lesson_id;");
      out.println("document.forms['LtimeForm'].activity_id.value = activity_id;");
      out.println("document.forms['LtimeForm'].submit();");        // submit the form
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script


   out.println("<script type=\"text/javascript\">");                     // Lesson Groups
   out.println("<!--");
   out.println("function exeLgroupForm(proid, date, lgname, lesson_id, activity_id) {");
      out.println("document.forms['LgroupForm'].proid.value = proid;");
      out.println("document.forms['LgroupForm'].date.value = date;");
      out.println("document.forms['LgroupForm'].lgname.value = lgname;");
      out.println("document.forms['LgroupForm'].lesson_id.value = lesson_id;");
      out.println("document.forms['LgroupForm'].activity_id.value = activity_id;");
      out.println("document.forms['LgroupForm'].submit();");        // submit the form
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script


   out.println("</head>");
   out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#336633\" vlink=\"#336633\" >");
   SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\" valign=\"top\">");   // table for main page
   out.println("<tr><td align=\"center\">");

   out.println("<font size=\"3\">");
   
   String title = "";
   String activity_text = "Activities";
   
   if (parm.foretees_mode == 1 && (selected_act_id == 0 || selected_act_id == 999)) {  // if Golf in system and Golf or ALL selected
      
      title = "Your Current " + ((IS_TLT) ? "Notifications" : "Tee Times") + " and Other Activities";
   
   } else if (selected_act_id == 999) {       // was ALL Activities selected
      
      title = "Your Current Scheduled Activities";
      
   } else {             // an Individual Activity was selected - ********* CHANGE THIS TO GET THE Activity Name!!!!!!!!
      
      title = "Your Current Tennis Activities";
      activity_text = "Tennis Times";      
   }
   
   out.println("<b>" + title + "</b></font>");
   out.println("<font size=\"2\"><br><br>");
     out.println("<table border=\"0\" align=\"center\" valign=\"top\" width=\"510\" bgcolor=\"#F5F5DC\">"); // legend
     out.println("<tr bgcolor=\"#336633\"><td align=\"center\" colspan=\"3\">");
     out.println("<font color=\"#FFFFFF\" size=\"2\">");
     out.println("<b>Legend</b>");
     out.println("</font></td></tr>");
       
     out.println("<tr>");

     if (club.equals( "interlachenspa" )) {

        out.println("<td width=\"170\" align=\"center\"><font size=\"1\" color=\"goldenrod\">");
        out.println("Your Scheduled Spa Services");
        out.println("</font></td>");
        out.println("<td width=\"170\" align=\"center\"><font size=\"1\" color=\"black\">");
        out.println("Other Events");
        out.println("</font></td>");
          
     } else {  
       
        out.println("<td width=\"170\" align=\"center\"><font size=\"1\" color=\"blue\">");
        out.println("Your Scheduled Events");
        out.println("</font></td>");
        out.println("<td width=\"170\" align=\"center\"><font size=\"1\" color=\"red\">");
        out.println("Events You May Join");
        out.println("</font></td>");
        out.println("<td width=\"170\" align=\"center\"><font size=\"1\" color=\"black\">");
        out.println("Other Events");
        out.println("</font></td>");
        out.println("</tr>");

        out.println("<tr>");
  
        out.println("<td width=\"170\" align=\"center\"><font size=\"1\" color=\"green\">");
        
        if (parm.foretees_mode == 1 && (selected_act_id == 0 || selected_act_id == 999)) {

            out.println("Your " + ((IS_TLT) ? "Submitted Notifications" : "Scheduled Tee Times"));
        } else {
           out.println("&nbsp;");
        }
        out.println("</font></td>");
        
        out.println("<td width=\"170\" align=\"center\"><font size=\"1\" color=\"goldenrod\">");
        out.println("Your Scheduled Lessons");
        out.println("</font></td>");
        out.println("<td width=\"170\" align=\"center\"><font size=\"1\" color=\"brown\">");
        
        if (lottery > 0 && parm.foretees_mode == 1 && (selected_act_id == 0 || selected_act_id == 999)) {
           
           //
           //  Get the replacement text for the word "Lottery" if club requested one - added to club5 by Support Club Options
           //
           lotteryText = getClub.getLotteryText(con);         // get replacement text for "Lottery" if provided  
           
           if (club.equals( "oldoaks" )) {
              out.println("Your Tee Time Requests");
           } else if (!lotteryText.equals("")) {
              out.println("Your " +lotteryText+ "s");
           } else {
              out.println("Your Lottery Requests");
           }
        } else {
           out.println("&nbsp;");
        }
        out.println("</font></td>");
        out.println("</tr>");

        out.println("<td width=\"170\" align=\"center\"><font size=\"1\" color=\"#114411\">");
        out.println("Your Wait List Signups");
        out.println("</font></td>");
        
        if (parm.genrez_mode != 0 && (selected_act_id != 0 || selected_act_id == 999)) {

            out.println("<td width=\"170\" align=\"center\"><font size=\"1\" color=\"brown\">");
            out.println("Your Scheduled " +activity_text);
            out.println("</font></td>");

        } else {

            out.println("<td></td>");

        }
        out.println("<td></td>");
        out.println("</tr>");
        
        out.println("<tr><td align=\"center\" colspan=\"3\">");
        out.println("<font color=\"#336633\" size=\"1\">");
        if (parm.genrez_mode != 0) {
           out.println("If the day number is underlined, you may click it to access that day's time sheet.");
        } else {
           out.println("If the day number is underlined, you may click it to access that day's tee sheet.");
        }
        out.println("</font></td>");
     }
     out.println("</tr></table>");

  if (parm.genrez_mode != 0) {       // if any activities defined in system - add a drop down selection list for activities

      out.println("<font size=\"2\"><br>");
      out.println("<form action=\"/" +rev+ "/servlet/Member_teelist\" method=\"post\" name=\"cform\" target=\"bot\">");
      out.println("<div id=\"awmobject1\">");        // allow menus to show over this box

      out.println("<b>Select the Activity to List:</b>&nbsp;&nbsp;");
      out.println("<select size=\"1\" name=\"activity_id\" onChange=\"document.cform.submit()\">");
      if (parm.foretees_mode == 1) {
         if (selected_act_id == 0) {
            out.println("<option selected value=\"0\">Golf</option>");
         } else {
            out.println("<option value=\"0\">Golf</option>");
         }
      }
      if (selected_act_id == 1) {
         out.println("<option selected value=\"1\">Tennis</option>");        // *************** CHANGE THIS TO INCLUDE ALL ACTIVITIES ************
      } else {
         out.println("<option value=\"1\">Tennis</option>");
      }
      if (selected_act_id == 999) {
         out.println("<option selected value=\"999\">ALL</option>");
      } else {
         out.println("<option value=\"999\">ALL</option>");
      }
      out.println("</select></div>");
      out.println("</form></font>");
  }


   out.println("<font size=\"1\"><br>");
   out.println("<form method=\"link\" action=\"javascript:self.print()\">");
   out.println("Click image to print this page. &nbsp;&nbsp;");
   out.println("<input type=\"image\" src=\"/" +rev+ "/images/print_sm.gif\" alt=\"Print\">");
   out.println(" &nbsp;&nbsp;<b>Hint:</b> Print in landscape mode.");
   out.println("</form></font>");


   //
   //  Display 3 tables - 1 for each of the next 3 months
   //
   months = 3;                                      // default = 3 months
     
   if (club.equals( "deserthighlands" ) && sess_activity_id == 0) {
  
      months = 12;                               // they want 12 months
   }

   if ((club.equals( "rtjgc" ) || club.equals( "pattersonclub" ) || club.equals("blackrockcountryclub")) && sess_activity_id == 0) {     // Robert Trent Jones GC
  
      months = 6;                               // they want 6 months
   }


   for (i2 = 0; i2 < months; i2++) {                 // do each month

      monthName = mm_table[month];                  // month name

      numDays = numDays_table[month];               // number of days in month

      if (numDays == 0) {                           // if Feb

         int leapYear = year - 2000;
         numDays = feb_table[leapYear];             // get days in Feb
      }

      //
      //  Adjust values to start at the beginning of the month
      //
      cal.set(Calendar.YEAR, year);                 // set year in case it changed below
      cal.set(Calendar.MONTH, month-1);             // set the current month value
      cal.set(Calendar.DAY_OF_MONTH, 1);            // start with the 1st
      day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)
      day = 1;
      col = 0;

      //
      //  init the indicator arrays to start new month
      //
      for (i = 0; i < 32; i++) {   
         eventA[i] = 0;
         teetimeA[i] = 0;
         lessonA[i] = 0;
         lotteryA[i] = 0;
         waitlistA[i] = 0;
         activitiesA[i] = 0;
      }

      //
      //  Locate all the Events for this member & month and set the array indicators for each day
      //
      sdate = (year * 10000) + (month * 100) + 0;       // start of the month (for searches)
      edate = (year * 10000) + (month * 100) + 32;      // end of the month

      if (parm.foretees_mode == 1 && (selected_act_id == 0 || selected_act_id == 999)) {

         try {
          
            //
            // search for this user's tee times for this month
            //
            pstmt1 = con.prepareStatement (
               "SELECT dd " +
               "FROM teecurr2 " +
               "WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? " +
               "OR username5 = ? OR userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) " +
               "AND (date > ? AND date < ?) " +
               "ORDER BY date");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, user);
            pstmt1.setString(2, user);
            pstmt1.setString(3, user);
            pstmt1.setString(4, user);
            pstmt1.setString(5, user);
            pstmt1.setString(6, user);
            pstmt1.setString(7, user);
            pstmt1.setString(8, user);
            pstmt1.setString(9, user);
            pstmt1.setString(10, user);
            pstmt1.setLong(11, sdate);
            pstmt1.setLong(12, edate);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               dd = rs.getInt(1);
               teetimeA[dd] = 1;       // set indicator for this day (tee time exists)

            }

            pstmt1.close();

         } catch (Exception e1) {

            SystemUtils.logError("Member_teelist: Error getting tee times for " +club+ ", User: " +user+ ", Error: " +e1.getMessage());
         }

         //
         //  Check for any lottery requests, if supported
         //
         if (lottery > 0) {

            try {
               pstmt1 = con.prepareStatement (
                  "SELECT dd " +
                  "FROM lreqs3 " +
                  "WHERE (user1 LIKE ? OR user2 LIKE ? OR user3 LIKE ? OR user4 LIKE ? OR user5 LIKE ? OR " +
                  "user6 LIKE ? OR user7 LIKE ? OR user8 LIKE ? OR user9 LIKE ? OR user10 LIKE ? OR " +
                  "user11 LIKE ? OR user12 LIKE ? OR user13 LIKE ? OR user14 LIKE ? OR user15 LIKE ? OR " +
                  "user16 LIKE ? OR user17 LIKE ? OR user18 LIKE ? OR user19 LIKE ? OR user20 LIKE ? OR " +
                  "user21 LIKE ? OR user22 LIKE ? OR user23 LIKE ? OR user24 LIKE ? OR user25 LIKE ?) " +
                  "AND (date > ? AND date < ?) " +
                  "ORDER BY date");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setString(1, user);
               pstmt1.setString(2, user);
               pstmt1.setString(3, user);
               pstmt1.setString(4, user);
               pstmt1.setString(5, user);
               pstmt1.setString(6, user);
               pstmt1.setString(7, user);
               pstmt1.setString(8, user);
               pstmt1.setString(9, user);
               pstmt1.setString(10, user);
               pstmt1.setString(11, user);
               pstmt1.setString(12, user);
               pstmt1.setString(13, user);
               pstmt1.setString(14, user);
               pstmt1.setString(15, user);
               pstmt1.setString(16, user);
               pstmt1.setString(17, user);
               pstmt1.setString(18, user);
               pstmt1.setString(19, user);
               pstmt1.setString(20, user);
               pstmt1.setString(21, user);
               pstmt1.setString(22, user);
               pstmt1.setString(23, user);
               pstmt1.setString(24, user);
               pstmt1.setString(25, user);
               pstmt1.setLong(26, sdate);
               pstmt1.setLong(27, edate);
               rs = pstmt1.executeQuery();     

               while (rs.next()) {

                  dd = rs.getInt(1);

                  lotteryA[dd] = 1;       // set indicator for this day (lottery req exists)
               }
               pstmt1.close();

            }
            catch (Exception e1) {

               SystemUtils.logError("Member_teelist: Error getting lottery reqs for " +club+ ", User: " +user+ ", Error: " +e1.getMessage());
            }
         }         // end of IF lottery
      }

      //
      //  Get all lesson times for this user this month 
      //
      try {
       
         pstmt1 = con.prepareStatement (
            "SELECT date " +
            "FROM lessonbook5 " +
            "WHERE date > ? AND date < ? AND memid = ? " +
            (!allAct ? "AND activity_id = ? " : "") +
            "ORDER BY date");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setLong(1, sdate);
         pstmt1.setLong(2, edate);
         pstmt1.setString(3, user);
         if (!allAct) pstmt1.setInt(4, selected_act_id);
         rs = pstmt1.executeQuery();

         while (rs.next()) {

            ldate = rs.getLong(1);

            ldate = ldate - ((ldate / 100) * 100);     // get day
            dd = (int)ldate;

            lessonA[dd] = 1;       // set indicator for this day (lesson time exists)

         }
         pstmt1.close();

         pstmt1 = con.prepareStatement (
            "SELECT date " +
            "FROM lgrpsignup5 " +
            "WHERE date > ? AND date < ? AND memid = ? " +
            "ORDER BY date");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setLong(1, sdate);
         pstmt1.setLong(2, edate);
         pstmt1.setString(3, user);
         rs = pstmt1.executeQuery();

         while (rs.next()) {

            ldate = rs.getLong(1);

            ldate = ldate - ((ldate / 100) * 100);     // get day
            dd = (int)ldate;

            lessongrpA[dd] = 1;       // set indicator for this day (lesson time exists)
         }
         pstmt1.close();

      }
      catch (Exception e1) {
          
         SystemUtils.logError("Member_teelist: Error getting lesson times for " +club+ ", User: " +user+ ", Error: " +e1.getMessage());                           // log it
      }

      //
      //  Get all events for this month
      //
      try {

         if (parm.genrez_mode == 0 || selected_act_id == 999) {     // if no Activities or ALL selected

            if (club.equals( "tcclub" )) {

               pstmt1 = con.prepareStatement (
                  "SELECT day " +
                  "FROM events2b WHERE date > ? AND date < ? AND inactive = 0");

            } else {

               pstmt1 = con.prepareStatement (
                  "SELECT day " +
                  "FROM events2b WHERE date > ? AND date < ? AND gstOnly = 0 AND inactive = 0");
            }

            pstmt1.setLong(1, sdate);
            pstmt1.setLong(2, edate);
            
         } else {


            pstmt1 = con.prepareStatement (
                    "SELECT day " +
                    "FROM events2b WHERE date > ? AND date < ? AND gstOnly = 0 AND activity_id = ? AND inactive = 0");


            pstmt1.setLong(1, sdate);
            pstmt1.setLong(2, edate);
            pstmt1.setInt(3, selected_act_id);
         }

         rs = pstmt1.executeQuery();

         while (rs.next()) {

            dd = rs.getInt(1);

            eventA[dd] = 1;       // set indicator for this day (event exists)
         }
         pstmt1.close();

      } catch (Exception e1) {
          
         SystemUtils.logError("Member_teelist: Error getting events for " +club+ ", Error: " +e1.getMessage());
      }
        
      
      if (parm.foretees_mode == 1 && (selected_act_id == 0 || selected_act_id == 999)) {

         try {

            //
            // search for this user's unconverted wait list signups for this month
            //
            pstmt1 = con.prepareStatement (
               "SELECT DATE_FORMAT(wls.date, '%d') AS dd, wls.wait_list_signup_id " + 
               "FROM wait_list_signups wls " +
               "LEFT OUTER JOIN wait_list_signups_players wlp ON wlp.wait_list_signup_id = wls.wait_list_signup_id " +
               "WHERE DATE_FORMAT(wls.date, '%Y%m%d') >= ? AND DATE_FORMAT(wls.date, '%Y%m%d') <= ? AND wlp.username = ? AND converted = 0 " +
               "ORDER BY wls.date");

            pstmt1.clearParameters();
            pstmt1.setLong(1, sdate);
            pstmt1.setLong(2, edate);
            pstmt1.setString(3, user);

            rs = pstmt1.executeQuery();

            while (rs.next()) {

               waitlistA[rs.getInt(1)] = rs.getInt(2);       // set indicator for this day (wait list signup exists)

            }

            pstmt1.close();

         } catch (Exception e1) {

            SystemUtils.logError("Member_teelist: Error getting wait list signups for " +club+ ", User: " +user+ ", Error: " +e1.getMessage());
         }
      }
      

      if (parm.genrez_mode != 0 && selected_act_id > 0) {      // if any activites defined for this club

         try {

            //
            // search for this user's activity signups for this month
            //
            pstmt1 = con.prepareStatement (
               "SELECT DATE_FORMAT(a.date_time, '%d') AS dd, a.sheet_id " +
               "FROM activity_sheets a " +
               "LEFT OUTER JOIN activity_sheets_players ap ON ap.activity_sheet_id = a.sheet_id " +
               "WHERE DATE_FORMAT(a.date_time, '%Y%m%d') >= ? AND DATE_FORMAT(a.date_time, '%Y%m%d') <= ? AND ap.username = ? " +
               "ORDER BY a.date_time");

            pstmt1.clearParameters();
            pstmt1.setLong(1, sdate);
            pstmt1.setLong(2, edate);
            pstmt1.setString(3, user);

            rs = pstmt1.executeQuery();

            while (rs.next()) {

               activitiesA[rs.getInt(1)] = rs.getInt(2);       // set indicator for this day (activity signup exists)

            }
            pstmt1.close();

         } catch (Exception e1) {
           
            SystemUtils.logError("Member_teelist: Error getting activity signups for " +club+ ", User: " +user+ ", Error: " +e1.getMessage());
         }
      }


      //
      //  table for the month
      //
      out.println("<table border=\"1\" width=\"770\" bgcolor=\"#F5F5DC\">");
      out.println("<tr><td colspan=\"7\" align=\"center\" bgcolor=\"#336633\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\"><b>" + monthName + "&nbsp;&nbsp;" + year + "</b></font>");
      out.println("</td></tr><tr>");
         out.println("<td width=\"110\" align=\"center\"><font size=\"1\"><b>Sunday</b></font></td>");
         out.println("<td width=\"110\" align=\"center\"><font size=\"1\"><b>Monday</b></font></td>");
         out.println("<td width=\"110\" align=\"center\"><font size=\"1\"><b>Tuesday</b></font></td>");
         out.println("<td width=\"110\" align=\"center\"><font size=\"1\"><b>Wednesday</b></font></td>");
         out.println("<td width=\"110\" align=\"center\"><font size=\"1\"><b>Thursday</b></font></td>");
         out.println("<td width=\"110\" align=\"center\"><font size=\"1\"><b>Friday</b></font></td>");
         out.println("<td width=\"110\" align=\"center\"><font size=\"1\"><b>Saturday</b></font></td>");
      out.println("</tr>");
      out.println("<tr>");        // first row of days

      for (i = 1; i < day_num; i++) {    // skip to the first day
         out.println("<td>&nbsp;<br><br></td>");
         col++;
      }

      while (day < today) {
         out.println("<td align=\"left\" valign=\"top\"><font size=\"1\">" + day + "</font>");  // put in day of month
         out.println("<br><br></td>");  // put in day of month
         col++;
         day++;

         if (col == 7) {
            col = 0;                             // start new week
            out.println("</tr>");
         }
      }

      //
      // start with today, or 1st day of month, and go to end of month
      //
      while (day <= numDays) {

         //
         //  create a date field for queries
         //
         date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd
         String mysql_date = year + "-" + SystemUtils.ensureDoubleDigit(month) + "-" + SystemUtils.ensureDoubleDigit(day);
         didone = false;        // init 'did one' flag

         if (col == 0) {      // if new row

            out.println("<tr>");
         }

         out.println("<td align=\"left\" valign=\"top\"><font size=\"1\">");   // day of month

         if (max > 0) {            // if member can view tee sheet for this date

            //
            //  Add link to Member_sheet (do not specify a course, it will default to 1st one)
            //
            if (sess_activity_id == 0) {          // if Golf
               out.println("<a href=\"/" +rev+ "/servlet/Member" + ((IS_TLT) ? "TLT" : "") + "_sheet?index=" +index+ "\">");
            } else {
               out.println("<a href=\"/" +rev+ "/servlet/Member_gensheets?date=" +date+ "\">");
            }
            out.println(day+ "</a>");

            index++;              // next day
            max--;

         } else {

            out.println(day);         // just put in day of month
         }
         out.println("<br>");


     if (IS_TLT) {

  
        // check to see if there are any notifications for this day

        try {

           pstmt1 = con.prepareStatement (
                "SELECT n.notification_id, DATE_FORMAT(n.req_datetime, '%l:%i %p') AS pretty_time " +
                "FROM notifications n, notifications_players np " +
                "WHERE n.notification_id = np.notification_id " +
                   "AND np.username = ? " +
                   "AND DATE(n.req_datetime) = ? ");

           pstmt1.clearParameters();        // clear the parms
           pstmt1.setString(1, user);
           pstmt1.setString(2, mysql_date);
           rs = pstmt1.executeQuery();

           while (rs.next()) {

               if (sess_activity_id == 0) {
                   out.println("" +
                       "<a href=\"javascript:editNotify(" + rs.getInt("notification_id") + ", '" + rs.getString("pretty_time") + "')\">" +
                       "<font color=darkGreen>Golf at " + rs.getString("pretty_time") + "</font></a>");
               } else {
                   out.println("" +
                       "<font color=darkGreen>Golf at " + rs.getString("pretty_time") + "</font>");
               }
           }
           
        } catch (Exception exp) {
            
        }
             
     } else {
             
         //*******************************************************************************
         //  Check for any tee times for this day
         //*******************************************************************************
         //
         if (teetimeA[day] == 1) {        // if any tee times exist for this day

            try {

               pstmt1 = con.prepareStatement (
                  "SELECT mm, dd, yy, day, hr, min, time, event, event_type, fb, " +
                  "lottery, courseName, rest5, lottery_color " +
                  "FROM teecurr2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? " +
                  "OR username5 = ? OR userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) " +
                  "AND date = ? ORDER BY time");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setString(1, user);
               pstmt1.setString(2, user);
               pstmt1.setString(3, user);
               pstmt1.setString(4, user);
               pstmt1.setString(5, user);
               pstmt1.setString(6, user);
               pstmt1.setString(7, user);
               pstmt1.setString(8, user);
               pstmt1.setString(9, user);
               pstmt1.setString(10, user);
               pstmt1.setLong(11, date);
               rs = pstmt1.executeQuery();      // execute the prepared stmt

               while (rs.next()) {

                  mm = rs.getInt("mm");
                  dd = rs.getInt("dd");
                  yy = rs.getInt("yy");
                  dayname = rs.getString("day");
                  hr = rs.getInt("hr");
                  min = rs.getInt("min");
                  time = rs.getInt("time");
                  ename = rs.getString("event");
                  etype = rs.getInt("event_type");
                  fb = rs.getInt("fb");
                  lotteryName = rs.getString("lottery");
                  course = rs.getString("courseName");
                  rest5 = rs.getString("rest5");
                  lottery_color = rs.getString("lottery_color");

                  //
                  //  Check if a member restriction has been set up to block ALL mem types or mship types for this date & time
                  //
                  allRest = getRests.checkRests(date, time, fb, course, dayname, con);

                  ampm = " AM";
                  if (hr == 12) {
                     ampm = " PM";
                  }
                  if (hr > 12) {
                     ampm = " PM";
                     hr = hr - 12;    // convert to conventional time
                  }

                  if (min < 10) {
                     stime2 = hr+ ":0" +min;    // create a value for display on calendar
                  } else {
                     stime2 = hr+ ":" +min;
                  }

                  stime = stime2 + ampm;        // create a value for time parm

                  p5 = "No";                   // default = no 5-somes

                  //
                  //  check if 5-somes allowed on this course
                  //
                  PreparedStatement pstmt3 = con.prepareStatement (
                     "SELECT fives FROM clubparm2 WHERE courseName = ?");

                  pstmt3.clearParameters();        // clear the parms
                  pstmt3.setString(1, course);
                  rs2 = pstmt3.executeQuery();      // execute the prepared pstmt3

                  if (rs2.next()) {

                     fives = rs2.getInt(1);

                     if ((fives != 0 ) && (rest5.equals( "" ))) {   // if 5-somes and not restricted

                        p5 = "Yes";
                     }
                  }
                  pstmt3.close();

                  //
                  // Check for a shotgun event during this time
                  //
                  if (!ename.equals( "" ) && etype == 1) {  // tee time during a shotgun event

                     try {

                        //
                        //   Get the parms for this event
                        //
                        PreparedStatement pstmtev = con.prepareStatement (
                              "SELECT act_hr, act_min FROM events2b " +
                              "WHERE name = ?");

                        pstmtev.clearParameters();        // clear the parms
                        pstmtev.setString(1, ename);
                        rsev = pstmtev.executeQuery();      // execute the prepared stmt

                        if (rsev.next()) {

                           hr = rsev.getInt("act_hr");
                           min = rsev.getInt("act_min");
                        }
                        pstmtev.close();

                        //
                        //  Create time value for email msg
                        //
                        ampm = " AM";

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

                        //
                        //  convert time to hour and minutes for email msg
                        //
                        if (min > 9) {

                           stime2 = hr + ":" + min + ampm;

                        } else {

                           stime2 = hr + ":0" + min + ampm;
                        }

                     }
                     catch (Exception e) {
                     }

                     out.println("Shotgun Start at " +stime2+ "<br>");

                  } else {
                     
                     boolean cutoff = false;
                     
                     //
                     //  Check for Member Cutoff specified in Club Options
                     //
                     if (parm.cutoffdays < 99) {        // if option specified

                        if (parm.cutoffdays == 0 && date == todayDate && ctime > parm.cutofftime) {  // if cutoff day of and we are doing today and current time is later than cutoff time             

                           cutoff = true;         // indicate no member access

                        } else {

                           if (parm.cutoffdays == 1 && (date == todayDate || (date == tomorrowDate && ctime > parm.cutofftime))) {    // if cutoff day is the day before           

                              cutoff = true;         // indicate no member access
                           }
                        }
                     }
                     
                     //
                     //  Check for lottery time that has already been processed
                     //
                     if (!lotteryName.equals("")) {
                        
                        //
                        //  Get the current state of this lottery on the day of this tee time
                        //
                        lstate = SystemUtils.getLotteryState(date, mm, dd, yy, lotteryName, course, con);
                        
                        if (lstate < 5 || noAccessAfterLottery == true) {    // if lottery not approved OR access not allowed after approval
                           
                           cutoff = true;        // do not allow access to this tee time (pre-booked lottery time)
                        }
                        
                     } else {
                        
                        if (!lottery_color.equals("") && noAccessAfterLottery == true) {   // if it was a lottery time and access not allowed after processed
                           
                              cutoff = true;                    // do not allow access to this tee time
                        }
                     }
                    

                     if (restrictAllTees == true) {    // if all tee times are restricted

                        cutoff = true;        // do not allow access to this tee time
                     }
   

                     if (allRest == false && cutoff == false && !(date == todayDate && time <= ctime) && sess_activity_id == 0) {     // if mem can edit tee time

                        //
                        //  Display the tee time as a clickable link (see form & js above)
                        //
                        //     refer to 'web utilities/foretees2.css' for style info
                        //
                        out.println("<a href=\"javascript: exeTtimeForm('" +date+ "','" +dayname+ "','" +p5+ "','" +course+ "','" +fb+ "','" +stime+ "')\" class=mteetime>");
                        out.println("Tee Time at " +stime2+ "</a><br>");

                     } else {

                        out.println("Tee Time at " +stime2+ "<br>");
                     }
                  }

                  didone = true;        // set 'did one' flag

               }                    // end of WHILE
               pstmt1.close();

            }
            catch (Exception e1) {
               String errorMsg = "Member_teelist: Error getting tee times for " +club+ ", Day=" +day+ ", User: " +user+ ", Error: " +e1.getMessage(); // build error msg
               SystemUtils.logError(errorMsg);                           // log it
            }
         }                       // end of IF tee times
   
     } // end if tlt

         //*******************************************************************************
         //  Check for any lotteries for this day (all will be zero if not supported)
         //*******************************************************************************
         //
         if (lotteryA[day] == 1) {        // if any lotteries  exist for this day

            try {

               pstmt1 = con.prepareStatement (
                  "SELECT name, mm, dd, yy, day, hr, min, time, " +
                  "fb, courseName, id " +
                  "FROM lreqs3 " +
                  "WHERE (user1 LIKE ? OR user2 LIKE ? OR user3 LIKE ? OR user4 LIKE ? OR user5 LIKE ? OR " +
                  "user6 LIKE ? OR user7 LIKE ? OR user8 LIKE ? OR user9 LIKE ? OR user10 LIKE ? OR " +
                  "user11 LIKE ? OR user12 LIKE ? OR user13 LIKE ? OR user14 LIKE ? OR user15 LIKE ? OR " +
                  "user16 LIKE ? OR user17 LIKE ? OR user18 LIKE ? OR user19 LIKE ? OR user20 LIKE ? OR " +
                  "user21 LIKE ? OR user22 LIKE ? OR user23 LIKE ? OR user24 LIKE ? OR user25 LIKE ?) " +
                  "AND date = ? ORDER BY time");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setString(1, user);
               pstmt1.setString(2, user);
               pstmt1.setString(3, user);
               pstmt1.setString(4, user);
               pstmt1.setString(5, user);
               pstmt1.setString(6, user);
               pstmt1.setString(7, user);
               pstmt1.setString(8, user);
               pstmt1.setString(9, user);
               pstmt1.setString(10, user);
               pstmt1.setString(11, user);
               pstmt1.setString(12, user);
               pstmt1.setString(13, user);
               pstmt1.setString(14, user);
               pstmt1.setString(15, user);
               pstmt1.setString(16, user);
               pstmt1.setString(17, user);
               pstmt1.setString(18, user);
               pstmt1.setString(19, user);
               pstmt1.setString(20, user);
               pstmt1.setString(21, user);
               pstmt1.setString(22, user);
               pstmt1.setString(23, user);
               pstmt1.setString(24, user);
               pstmt1.setString(25, user);
               pstmt1.setLong(26, date);
               rs = pstmt1.executeQuery();      // execute the prepared stmt

               while (rs.next()) {

                  lname = rs.getString(1);
                  mm = rs.getInt(2);
                  dd = rs.getInt(3);
                  yy = rs.getInt(4);
                  dayname = rs.getString(5);
                  hr = rs.getInt(6);
                  min = rs.getInt(7);
                  time = rs.getInt(8);
                  fb = rs.getInt(9);
                  course = rs.getString(10);
                  lottid = rs.getLong(11);

                  ampm = " AM";
                  if (hr == 12) {
                     ampm = " PM";
                  }
                  if (hr > 12) {
                     ampm = " PM";
                     hr = hr - 12;    // convert to conventional time
                  }

                  if (min < 10) {
                     stime2 = hr+ ":0" +min;    // create a value for display on calendar
                  } else {
                     stime2 = hr+ ":" +min;
                  }

                  stime = stime2 + ampm;        // create a value for time parm

                  //
                  //  Check if 5-somes supported for this course
                  //
                  fives = 0;        // init

                  PreparedStatement pstmtc = con.prepareStatement (
                     "SELECT fives " +
                     "FROM clubparm2 WHERE first_hr != 0 AND courseName = ?");

                  pstmtc.clearParameters();        // clear the parms
                  pstmtc.setString(1, course);
                  rs2 = pstmtc.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     fives = rs2.getInt(1);          // 5-somes
                  }
                  pstmtc.close();

                  //
                  //  check if 5-somes restricted for this time
                  //
                  rest = "";     // no rest5

                  if (fives != 0) {

                     PreparedStatement pstmtr = con.prepareStatement (
                        "SELECT rest5 " +
                        "FROM teecurr2 WHERE date = ? AND time =? AND fb = ? AND courseName = ?");

                     pstmtr.clearParameters();        // clear the parms
                     pstmtr.setLong(1, date);
                     pstmtr.setInt(2, time);
                     pstmtr.setInt(3, fb);
                     pstmtr.setString(4, course);
                     rs2 = pstmtr.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        rest = rs2.getString(1);
                     }
                     pstmtr.close();
                  }

                  p5 = "No";                   // default = no 5-somes

                  if (fives != 0 && rest.equals( "" )) {     // if 5-somes are supported & not restricted

                     p5 = "Yes";                   // 5-somes ok
                  }

                  
                  //
                  //  get the slots value and determine the current state for this lottery
                  //
                  PreparedStatement pstmt7d = con.prepareStatement (
                     "SELECT slots " +
                     "FROM lottery3 WHERE name = ?");

                  pstmt7d.clearParameters();          // clear the parms
                  pstmt7d.setString(1, lname);

                  rs2 = pstmt7d.executeQuery();      // find all matching lotteries, if any

                  if (rs2.next()) {

                     slots = rs2.getInt(1);
                  }                
                  pstmt7d.close();
                  
                  
                  
                  //
                  //  Get the current state of this lottery on the day of this tee time
                  //
                  lstate = SystemUtils.getLotteryState(date, mm, dd, yy, lname, course, con);
                  

                  //
                  //  Form depends on the state
                  //
                  if (lstate == 2) {       // if still ok to process lottery requests

                     //
                     //  Display the lottery time as a clickable link (see form & js above)
                     //
                     if (sess_activity_id == 0) {
                         out.println("<a href=\"javascript: exeLott2Form('" +date+ "','" +dayname+ "','" +p5+ "','" +course+ "','" +fb+ "','" +stime+ "','" +lname+ "','" +lottid+ "','" +slots+ "','" +lstate+ "')\" class=mlottery>");
                         if (club.equals( "oldoaks" )) {
                            out.println("T Time Req at " +stime2+ "</a><br>");
                         } else if (!lotteryText.equals("")) {
                            out.println(lotteryText+ " at " +stime2+ "</a><br>");
                         } else {
                            out.println("Lottery Req at " +stime2+ "</a><br>");
                         }
                     } else {
                         out.println("<span class=mlottery>");
                         if (club.equals( "oldoaks" )) {
                            out.println("T Time Req at " +stime2+ "<br>");
                         } else if (!lotteryText.equals("")) {
                            out.println(lotteryText+ " at " +stime2+ "<br>");
                         } else {
                            out.println("Lottery Req at " +stime2+ "<br>");
                         }
                         out.println("</span>");
                     }

                  } else {

                     if (lstate == 5) {       // if lottery has already been processed

                        //
                        //  Display the lottery time as a clickable link (see form & js above)
                        //
                        out.println("<a href=\"javascript: exeLott5Form('" +date+ "','" +dayname+ "','" +p5+ "','" +course+ "','" +fb+ "','" +stime+ "','" +lname+ "','" +lottid+ "','" +lstate+ "')\" class=mlottery>");
                        if (club.equals( "oldoaks" )) {
                           out.println("T Time Req at " +stime2+ "</a><br>");
                        } else if (!lotteryText.equals("")) {
                           out.println(lotteryText+ " at " +stime2+ "</a><br>");
                        } else {
                           out.println("Lottery Req at " +stime2+ "</a><br>");
                        }

                     } else {

                        out.println("<div style=\"text-decoration:underline; color:brown\">");
                        if (club.equals( "oldoaks" )) {
                           out.println("T Time Req at " +stime2+ "</div>");
                        } else if (!lotteryText.equals("")) {
                           out.println(lotteryText+ " at " +stime2+ "</div>");
                        } else {
                           out.println("Lottery Req at " +stime2+ "</div>");
                        }
                     }
                  }

                  didone = true;        // set 'did one' flag

               }                    // end of WHILE
               pstmt1.close();

            }
            catch (Exception e1) {
               String errorMsg = "Member_teelist: Error getting tee times for " +club+ ", Day=" +day+ ", User: " +user+ ", Error: " +e1.getMessage(); // build error msg
               SystemUtils.logError(errorMsg);                           // log it
            }
         }    // end of IF lotteries


         //**********************************************************
         //  Check for any lessons for this day
         //**********************************************************
         //
         if (lessonA[day] == 1) {        // if any lessons  exist for this day

            try {
               lesson_id = 0;

               pstmt1 = con.prepareStatement (
                  "SELECT proid, time, ltype, recid, activity_id " +
                  "FROM lessonbook5 " +
                  "WHERE memid = ? AND date = ? AND num > 0 " +
                  (!allAct ? "AND activity_id = ? " : "") +
                  "ORDER BY time");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setString(1, user);
               pstmt1.setLong(2, date);
               if (!allAct) pstmt1.setInt(3, selected_act_id);
               rs = pstmt1.executeQuery();      // execute the prepared stmt

//               if (rs.next()) {               // just get the first one as each lesson can have multiple entries
               while (rs.next()) {              // get all lesson times that have a num greater than zero (not a subsequent time)

                  proid = rs.getInt(1);
                  time = rs.getInt(2);
                  ltype = rs.getString(3);
                  lesson_id = rs.getInt("recid");
                  returned_activity_id = rs.getInt("activity_id");

                  hr = time / 100;
                  min = time - (hr * 100);

                  if (hr > 12) {
                     hr = hr - 12;    // convert to conventional time
                  }

                  if (min < 10) {
                     stime2 = hr+ ":0" +min;    // create a value for display on calendar
                  } else {
                     stime2 = hr+ ":" +min;
                  }

                  dayname = day_table[col];                           // get the name of this day

                  String calDate = month+ "/" +day+ "/" + year;       // set parm for form

                  //
                  //  Display the lesson time as a clickable link (see form & js above)
                  //
                  //     refer to 'web utilities/foretees2.css' for style info
                  //
                  // Only display as selectable link if on current activity
                  if (returned_activity_id == sess_activity_id) {
                      
                      out.println("<a href=\"javascript: exeLtimeForm('" +proid+ "','" +calDate+ "','" +date+ "','" +time+ "','" +dayname+ "','" +ltype+ "','" +lesson_id+ "','" +sess_activity_id+ "')\" class=mlesson>");
                      if (club.equals( "interlachenspa" )) {
                          out.println("Spa Reservation at " +stime2+ "</a><br>");
                      } else {
                          out.println("Lesson at " +stime2+ "</a><br>");
                      }
                  
                  } else {
                      if (club.equals( "interlachenspa" )) {
                          out.println("<span class=mlesson>Spa Reservation at " +stime2+ "</span><br>");
                      } else {
                          out.println("<span class=mlesson>Lesson at " +stime2+ "</span><br>");
                      }
                  }

                  didone = true;        // set 'did one' flag

               }                    // end of WHILE
               pstmt1.close();

            }
            catch (Exception e1) {
               String errorMsg = "Member_teelist: Error getting lesson times for " +club+ ", Day=" +day+ ", User: " +user+ ", Error: " +e1.getMessage(); // build error msg
               SystemUtils.logError(errorMsg);                           // log it
            }
         }    // end of IF lessons


         //**********************************************************
         //  Check for any lessongrps for this day
         //**********************************************************
         //
         if (lessongrpA[day] == 1) {        // if any lessongrps  exist for this day

            try {
               lesson_id = 0;

               pstmt1 = con.prepareStatement (
                  "SELECT proid, lname " +
                  "FROM lgrpsignup5 " +
                  "WHERE memid = ? AND date = ?");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setString(1, user);
               pstmt1.setLong(2, date);
               rs = pstmt1.executeQuery();      // execute the prepared stmt

               while (rs.next()) {

                  proid = rs.getInt(1);
                  lgname = rs.getString(2);

                  //
                  //  Get the start time for this group lesson
                  //
                  PreparedStatement pstmt7d = con.prepareStatement (
                     "SELECT stime, activity_id, lesson_id " +
                     "FROM lessongrp5 WHERE proid = ? AND lname = ? " +
                     (!allAct ? "AND activity_id = ?" : ""));

                  pstmt7d.clearParameters();          // clear the parms
                  pstmt7d.setInt(1, proid);
                  pstmt7d.setString(2, lgname);
                  if (!allAct) pstmt7d.setInt(3, selected_act_id);

                  rs2 = pstmt7d.executeQuery();      // find all matching lotteries, if any

                  if (rs2.next()) {

                     time = rs2.getInt("stime");
                     returned_activity_id = rs2.getInt("activity_id");
                     lesson_id = rs2.getInt("lesson_id");

                     hr = time / 100;
                     min = time - (hr * 100);

                     if (hr > 12) {
                        hr = hr - 12;    // convert to conventional time
                     }

                     if (min < 10) {
                        stime2 = hr+ ":0" +min;    // create a value for display on calendar
                     } else {
                        stime2 = hr+ ":" +min;
                     }

                  } else {                 // end of IF
                      continue;  // If no data found (means it was a different activity), skip over the rest of the loop for this iteration
                  }
                  pstmt7d.close();

                  //
                  //  Display the lesson time as a clickable link (see form & js above)
                  //
                  //     refer to 'web utilities/foretees2.css' for style info
                  //
                  if (returned_activity_id == sess_activity_id) {
                      out.println("<a href=\"javascript: exeLgroupForm('" +proid+ "','" +date+ "','" +lgname+ "','" +lesson_id+ "','" +sess_activity_id+ "')\" class=mlesson>");
                      out.println("Group Lesson at " +stime2+ "</a><br>");
                  } else {
                      out.println("<span class=mlesson>Group Lesson at " +stime2+ "</span><br>");
                  }

                  didone = true;        // set 'did one' flag

               }                    // end of WHILE
               pstmt1.close();

            }
            catch (Exception e1) {
               String errorMsg = "Member_teelist: Error getting group lesson times for " +club+ ", Day=" +day+ ", User: " +user+ ", Error: " +e1.getMessage(); // build error msg
               SystemUtils.logError(errorMsg);                           // log it
            }

         }    // end of IF lessongrps


         //**********************************************************
         //  Check for any events for this day
         //**********************************************************
         //
         if (eventA[day] == 1) {        // if any events  exist for this day

            try {

               if (club.equals( "tcclub" )) {
                 
                  pstmt1 = con.prepareStatement (
                     "SELECT name, coursename, signup, activity_id " +
                     "FROM events2b WHERE date = ? AND inactive = 0 " +
                     (!allAct ? "AND activity_id = ?" : ""));

               } else {

                  pstmt1 = con.prepareStatement (
                     "SELECT name, coursename, signup, activity_id " +
                     "FROM events2b WHERE date = ? AND gstOnly = 0 AND inactive = 0 " +
                     (!allAct ? "AND activity_id = ?" : ""));
               }

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setLong(1, date);
               if (!allAct) pstmt1.setInt(2, selected_act_id);
               rs = pstmt1.executeQuery();      // execute the prepared stmt

               while (rs.next()) {

                  ename = rs.getString(1);
                  course = rs.getString(2);
                  signUp = rs.getInt(3);
                  returned_activity_id = rs.getInt("activity_id");

                  //
                  //  Check if this member is signed up
                  //
                  boolean signedup = false;

                  if (signUp != 0) {           // if members can signup

                     PreparedStatement pstmte = con.prepareStatement (
                        "SELECT player1, wait " +
                        "FROM evntsup2b WHERE name = ? AND courseName = ? AND inactive = 0 " +
                        "AND (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? " +
                        "OR username5 = ?)");

                     pstmte.clearParameters();        // clear the parms
                     pstmte.setString(1, ename);
                     pstmte.setString(2, course);
                     pstmte.setString(3, user);
                     pstmte.setString(4, user);
                     pstmte.setString(5, user);
                     pstmte.setString(6, user);
                     pstmte.setString(7, user);
                     rs2 = pstmte.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        signedup = true;              // set member signed up
                        wait = rs2.getInt("wait");
                     }
                     pstmte.close();

                     //
                     //  display a link to the signup
                     //
                     if (signedup == true) {     // if member already registered

                        //
                        //  Display the event name as a clickable link (see form & js above)
                        //
                        if (returned_activity_id == sess_activity_id) {
                            out.println("<a href=\"javascript: exeEventForm('" +ename+ "','" +course+ "')\" class=meventblue>");
                            if (wait == 0) {
                                out.println(ename + " - Registered</a><br>");
                            } else {
                                out.println(ename + " - Wait List</a><br>");
                            }
                        } else {
                            out.println("");
                            if (wait == 0) {
                                out.println("<span class=meventblue>" + ename + " - Registered<span><br>");
                            } else {
                                out.println("<span class=meventblue>" + ename + " - Wait List<span><br>");
                            }
                        }

                     } else {     // not registered yet

                        if (returned_activity_id == sess_activity_id) {
                            out.println("<a href=\"javascript: exeEventForm('" +ename+ "','" +course+ "')\" class=meventred>");
                            out.println(ename+ "</a><br>");
                        } else {
                            out.println("<span class=meventred>" +ename+ "</span><br>");
                        }
                     }

                  } else {     // no sign up available

                     //
                     //  Go to Member_sheet to display the event info
                     //
                     if (returned_activity_id == sess_activity_id) {
                         out.println("<a href=\"javascript: exeEventForm2('" +ename+ "')\" class=meventblack>");
                         out.println(ename+ "</a><br>");
                     } else {
                         out.println("<span class=meventblack>" +ename+ "</span><br>");
                     }

                  }       // end of IF signup

               }          // end of WHILE events
               pstmt1.close();

            }
            catch (Exception e1) {
               String errorMsg = "Member_teelist: Error processing events for " +club+ ", Day=" +day+ ", User: " +user+ ", Error: " +e1.getMessage(); // build error msg
               SystemUtils.logError(errorMsg);                           // log it
            }

         }    // end of IF events


         //
         // Check for any wait list signups for this day
         //
         if (waitlistA[day] != 0) {
             
            try {

               pstmt1 = con.prepareStatement (
                   "SELECT wl.wait_list_id, wl.course, wls.date, wls.ok_stime, wls.ok_etime, wls.wait_list_signup_id, " + 
                   "DATE_FORMAT(wls.date, '%W') AS day_name, DATE_FORMAT(wls.date, '%Y%m%d') AS dateymd " + 
                   "FROM wait_list_signups wls " + 
                   "LEFT OUTER JOIN wait_list wl ON wls.wait_list_id = wl.wait_list_id " + 
                   "WHERE wls.wait_list_signup_id = ?");

               pstmt1.clearParameters();
               pstmt1.setInt(1, waitlistA[day]);
               rs = pstmt1.executeQuery();
               
               if (rs.next()) {

                   if (sess_activity_id == 0) {
                       out.print("<a href=\"javascript:void(0)\" onclick=\"top.location.href='/" + rev + "/servlet/Member_waitlist?waitListId=" + rs.getInt("wait_list_id") + "&date=" + rs.getInt("dateymd") + "&index=999&day="+rs.getString("day_name")+"&course=" +rs.getString("course")+"&returnCourse=" +rs.getString("course")+"'\" class=mWLsignup>");
                       //out.print(SystemUtils.getSimpleTime(rs.getInt("ok_stime")) + " - " + SystemUtils.getSimpleTime(rs.getInt("ok_etime")));
                       out.print("Wait List Sign-up");
                       out.println("</a>");
                   } else {
                       out.print("<span class=mWLsignup>Wait List Sign-up</span>");
                   }
               }
               
               pstmt1.close();

            } catch (Exception exp) {}
            
         } // end IF wait list signup found


         if ( parm.genrez_mode != 0 ) {
             //
             // Check for any activity signups for this day
             //
             if (activitiesA[day] != 0) {

                try {

                   pstmt1 = con.prepareStatement (
                        "SELECT a.activity_id, " +
                           "DATE_FORMAT(a.date_time, '%Y%m%d') AS dateymd, " +
                           "DATE_FORMAT(a.date_time, '%l:%i %p') AS pretty_time " +
                        "FROM activity_sheets a, activity_sheets_players ap " +
                        "WHERE a.sheet_id = ap.activity_sheet_id " +
                           "AND a.sheet_id = ?");

                   pstmt1.clearParameters();
                   pstmt1.setInt(1, activitiesA[day]);
                   rs = pstmt1.executeQuery();

                   if (rs.next()) {

                       try { root_id = getActivity.getRootIdFromActivityId(rs.getInt("activity_id"), con); }
                       catch (Exception ignore) { root_id = rs.getInt("activity_id"); }

                       activity_name = getActivity.getActivityName(root_id, con);

                       if (root_id == sess_activity_id) {
                           out.print("<a href=\"javascript:void(0)\" onclick=\"top.location.href='/" + rev + "/servlet/Member_activity_slot?slot_id=" + activitiesA[day] + "&date=" + rs.getInt("dateymd") + "&index=999'\" style=\"color: brown\">");
                           out.print(activity_name + " at " + rs.getString("pretty_time"));
                           out.println("</a>");
                       } else {
                           out.print("<span style=\"color: brown\">" + activity_name + " at " + rs.getString("pretty_time") + "</span>");
                       }
                   }

                   pstmt1.close();

                } catch (Exception exp) {}

             } // end IF wait list signup found
         }

         //
         //**********************************************************
         //  End of display for this day - get next day
         //**********************************************************
         //
         if (didone == true) {          // if we added something to the day
            out.println("</td>");       // end of column (day)
         } else {
            out.println("<br><br><br></td>");
         }
         col++;
         day++;

         if (col == 7) {
            col = 0;                             // start new week
            out.println("</tr>");
         }
      }

      if (col != 0) {      // if not at the start

         while (col != 0 && col < 7) {      // finish off this row if not at the end

            out.println("<td>&nbsp;</td>");
            col++;
         }
         out.println("</tr>");
      }

      //
      // end of calendar row
      //
      out.println("</table>");
      out.println("<br>");
        
      today = 1;       // ready for next month
      month++;
        
      if (month > 12) {     // if end of year
         year++;         
         month = 1;
      }
      
   } // end if month loop

   out.println("</font></td>");
   out.println("</tr>");
   out.println("</table>");                   // end of table for main page

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;\">");
   out.println("</input></form></font>");

   //
   //  Build forms for submitting requests in calendars
   //
   out.println("<form name=\"teeTime\" action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"date\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"day\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"p5\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"fb\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"stime\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"999\">");  // indicate from here
   out.println("</form>");

   //
   //  Lottery Forms
   //
   out.println("<form name=\"lott2\" action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"date\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"day\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"lname\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"lottid\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"slots\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"lstate\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"p5\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"fb\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"stime\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"index\" value=999>");  // indicate from teelist
   out.println("</form>");

   out.println("<form name=\"lott5\" action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"date\" value=>");
   out.println("<input type=\"hidden\" name=\"day\" value=>");
   out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"lname\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"lottid\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"lstate\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"p5\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"fb\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"stime\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"index\" value=999>");  // indicate from teelist
   out.println("</form>");

   //
   //  Event Form
   //
   out.println("<form name=\"eventForm\" action=\"/" +rev+ "/servlet/Member_events2\" method=\"post\" target=\"bot\">");
   out.println("<input type=\"hidden\" name=\"name\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"\">");  // indicate from teelist
   out.println("</form>");

   //
   //  Event Form for Non_Sign-up Events
   //
   out.println("<form name=\"eventForm2\" action=\"/" +rev+ "/servlet/Member_sheet\" method=\"post\" target=\"_blank\">");
   out.println("<input type=\"hidden\" name=\"event\" value=\"\">");
   out.println("</form>");

   //
   //  Lesson Time Form
   //
   out.println("<form name=\"LtimeForm\" action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"proid\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"date\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"time\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"day\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"ltype\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"activity_id\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"reqtime\" value=\"yes\">");       // indicate a request
   out.println("<input type=\"hidden\" name=\"index\" value=999>");             // indicate from teelist
   out.println("</form>");

   //
   //  Lesson Group Form
   //
   out.println("<form name=\"LgroupForm\" action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\" target=\"bot\">");
   out.println("<input type=\"hidden\" name=\"proid\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"date\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"lgname\" value=\"\">");       // name of group lesson
   out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"activity_id\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"groupLesson\" value=\"yes\">");
   out.println("<input type=\"hidden\" name=\"index\" value=999>");             // indicate from teelist
   out.println("</form>");

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");
   out.close();

 }   // end of doGet
  
}
