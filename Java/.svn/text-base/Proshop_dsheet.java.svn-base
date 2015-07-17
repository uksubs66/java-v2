/***************************************************************************************
 *   Proshop_dsheet:  This servlet will process the 'Edit Tee Time' request from
 *                    the Proshop's Tee Sheet page (Control Panel).
 *
 *
 *   called by:  
 *
 *
 *   parms passed (on doGet):  
 *                  'index'       = the date index (0 = today, 1 = tomorrow, etc.)
 *                  'course'      = name of the course for this sheet
 *                  'insert=yes'  = if user selected 'Insert Tee Time' from edit menu
 *                  'delete=yes'  = if user selected 'Delete Tee Time' from edit menu
 *                  'blockers'    = if present the displayBlockers method is run
 *                  'setBlockers' = if present the setBlockers method is run
 *                  'sha'         = if present the displayHoleAssignments method is run
 *                  'ssha'        = if present the setHoleAssignments method is run
 *
 *      secondary calls (from self to doPost):
 *                   (refer to dopost method)
 *
 *
 *   created: 4/30/2002   Bob P.
 *
 *   last updated:
 *
 *        5/18/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        5/18/10   Update setLotteryState5 - delete lreqs if Proshop lottery and in state 4 (requests were not being deleted).
 *        5/14/10   Update sendLotteryEmails to use the shotgun time in email if lottery requests moved into a shotgun event.
 *        4/21/10   Ramsey - Updated the name of the lottery to be used with this custom (case 1823).
 *        4/22/10   Added to_player and from_player to returnToDsheet, were missing.  Will now be included used if populated.
 *        4/21/10   Ramsey - Move the start date of their wait list after the lottery has been processed (case 1823).
 *        4/13/10   Incorporate guest_ids into dsheet processing so guest_ids get moved properly between different types of reservations and slots
 *        4/12/10   Completed changes for limited access proshop user override access option
 *        2/12/10   In doGet changed the backToSheet processing so that if MODE is not set then just return to sheet
 *                  and do not try to determin if there are emails that need to be sent
 *       12/15/09   Changed convertEventSignUp to not look at rest5 or p5 when determining open_slots
 *       12/08/09   When looking for events or event signups only check those that are active.  Also, do not delete event signups - just mark them inactive.
 *       11/12/09   Track the returnCourse for lottery processing so we return properly.  Also, remove the lottery name from teecurr2 for
 *                  the date and course of the lottery after sending emails so left over tee times show up on the sheet.
 *       10/27/09   Pass the event name through the hole assignment process if came from event signup - so we will prompt to send emails
 *                  when done and returning to the tee sheet.
 *       10/22/09   When displaying the tee sheet, if there is a shotgun event diring that time, then show "hh:mm Shot" in place
 *                  of the tee time.  This is what we do on the actual tee sheet (case 1723).
 *       10/21/09   Lottery - add failure code to Unassigned indicator for debug purposes.
 *        9/04/09   Correct the setting of userg in convertNotification.
 *        6/24/09   Pass userg to addPlayer from convertLottReq and convertEventSgnUp so guests are properly assigned in tee times. 
 *                  Also, pass the username instead of the member number in userg in ConvertNotification, convertOld and convertWaitListSgnUp.
 *        6/11/09   Updated convertOld to populate the new mship, mtype, gtype, and grev fields in teepast2
 *        4/22/09   In doGet when checking for unsent emails, use lottery= instead of name= in teecurr query.
 *        4/22/09   Change prepared statement usage in convertLottReq (close them, use common statement, etc.)
 *        4/02/09   Fix for lottery emails (lottery (name) field was getting cleared when being moved to teesheet - then emails would not go out
 *        3/26/09   Update sendLotteryEmails to only send emails for current lottery
 *        3/13/09   Change how we get to state 5 in lottery processing.  When requests are assigned and approved, set the reqs to state 4 instead
 *                  of deleting them.  If unassigned, set to state 4 instead of state 5.  Then when pro is done and has selected to send emails,
 *                  delete all lreqs in state 4 and assigned, and change all unassigned state 4's to state 5.
 *       12/09/08   Added restriction suspension color handling for tee times.
 *       11/26/08   Change the check for course=ALL in lottery when determining if to display the assigned & requested courses (case 1557 fix).
 *       11/25/08   Changed format for checking suspensions for mrest display on the tee sheet legend.
 *       10/13/08   Check for replacement text for the word "Lottery" when email is for a lottery request.
 *       10/06/08   For lotteries when course=ALL, add a column for the course that was requested (case 1557).
 *        9/17/08   Fixed row coloring and now when in lott mode and 'show only lott times' - event times during lott period are hidden
 *        9/03/08   Changed the word Lottery to Tee Time Request for Pecan Plantation (for spam testing).
 *        8/14/08   Added limited access proshop users checks
 *        7/15/08   Fix error in convert js function
 *        7/09/08   Convert - correct the weight calculation when storing weight entry in lassigns5.
 *        6/14/08   Major updates to include all drag-drop funtions in this servlet (dlott & devnt are now defunct)
 *        6/06/08   Changed displayBlockers so that we can unblock booked times
 *        6/05/08   Fixed history bug
 *        4/24/08   Move the tflag varchars when moving a whole tee time or a single, or converting a notification to tee time (case 1357).
 *        2/25/08   Move the custom_disp varchars when moving a whole tee time or a single.
 *       10/01/07   Ensure mNum is copied to teecurr/teepast tables when converting notifications to tee times
 *        7/26/07   Don't inticate shotguns in the F/B column
 *        6/19/07   Removed call to doBlockers in doInsert so new tee time is not blocked.
 *        4/17/07   Changed courseName order by for Edison Club.
 *        1/28/07   Add more info to the tee time history when moving a player or the tee time.
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
//import java.lang.Math;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;

// foretees imports
import com.foretees.common.parmItem;
import com.foretees.common.getItem;
import com.foretees.common.parmSlot;
import com.foretees.common.parmClub;
import com.foretees.common.verifySlot;
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.parmRest;
import com.foretees.common.getRests;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.Utilities;


public class Proshop_dsheet extends HttpServlet {


   String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

   static String host = SystemUtils.HOST;

   static String efrom = SystemUtils.EFROM;

   static String header = SystemUtils.HEADER;

   static String trailer = SystemUtils.TRAILER;

   String delim = "_";

   
 //****************************************************************************
 // Process the request from Proshop_sheet or below after a move,
 // change, insert or delete - display the tee sheet
 //
 //   This doGet method will build the tee sheet and process the
 //   insert and delete requests from that sheet.
 //
 //   parms:  index  - used to calculate the date (0=today, 1=tomorrow, etc.)
 //           course - name of course
 //           day    - name of day
 //           jump   - optional for refreshing the sheet
 //
 //****************************************************************************
 //

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setHeader("Pragma", "no-cache");      // these 3 added to fix 'blank screen' problem
    resp.setHeader("Cache-Control", "no-cache");
    resp.setDateHeader("Expires", 0);
    resp.setContentType("text/html");
    PrintWriter out;

    PreparedStatement pstmtc = null;
    Statement stmt = null;
    Statement stmtc = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    //
    //  use GZip (compression) if supported by browser
    //
    String encodings = req.getHeader("Accept-Encoding");               // browser encodings

    if ((encodings != null) && (encodings.indexOf("gzip") != -1)) {    // if browser supports gzip

      OutputStream out1 = resp.getOutputStream();
      out = new PrintWriter(new GZIPOutputStream(out1), false);       // use compressed output stream
      resp.setHeader("Content-Encoding", "gzip");                     // indicate gzip

    } else {

      out = resp.getWriter();                                         // normal output stream
    }

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) {

      out.println(SystemUtils.HeadTitle("Access Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>System Access Error</H3>");
      out.println("<BR><BR>You have entered this site incorrectly or have lost your session cookie.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
    }

    Connection con = SystemUtils.getCon(session);                     // get DB connection

    if (con == null) {

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
      return;
    }

    //
    //  Check for 'Insert' or 'Delete' request
    //
    if (req.getParameter("insert") != null) {

      doInsert(req, out, con, session);          // process insert request
      return;
    }
    if (req.getParameter("delete") != null) {

      if (req.getParameter("notifyId") != null) {
          doDeleteNotifyReq(req, out, con, session);
      } else if (req.getParameter("wlsId") != null) {
          doDeleteWaitReq(req, out, con, session);
      } else if (req.getParameter("lotteryId") != null) {
          doDeleteLottReq(req, out, con, session);
      } else if (req.getParameter("eventId") != null) {
          doDeleteEventReq(req, out, con, session);
      } else {
          doDelete(req, out, con, session);
      }
      return;
    }

    if (req.getParameter("convert") != null && req.getParameter("convert").equals("all")) {
           
       convert_all(req, out, con, session, resp);
       return;
    }
    
    if (req.getParameter("setBlockers") != null) {

      setBlockers(req, out, con, session);        // process block request
    }

    if (req.getParameter("blockers") != null) {

      displayBlockers(req, out, con, session);        // display blockers tee sheet
      out.close();
      return;
    }


    if (req.getParameter("ssha") != null) {

      setHoleAssignments(req, out, con, session);        // process hole assignments
      //out.close();
      //return;
    }

    if (req.getParameter("sha") != null) {

      displayHoleAssignments(req, out, con, session);        // display hole assignment tee sheet
      out.close();
      return;
    }


    //
    //  See if we are in the timeless tees mode
    //
    boolean IS_TLT = ((Integer)session.getAttribute("tlt") == 1) ? true : false;


    // get mode if passed (if no mode, then default to edit tee sheet & notification actions)
    String mode = (req.getParameter("mode") == null) ? "" : req.getParameter("mode");
   
   
   //*******************************************************************
   //
   //   Call is to build or refresh the tee sheet
   //
   //*******************************************************************
   //
   //   get name of club for this user
   //
   String club = (String)session.getAttribute("club");

   int count = 0;
   int p = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int type = 0;
   int in_use = 0;                       // event type
   int shotgun = 1;                      // event type = shotgun
   int multi_lot = 0;                    // lottery for ALL courses
   int mrest_id = 0;
   short show = 0;
   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;
   short fb = 0;
   short hideNotes = 0;
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
   String sfb2 = "";
   String submit = "";
   String num = "";
   String jumps = "";
   String hole = "";

   String event1 = "";       // for legend - max 2 events, 4 rest's, 2 lotteries
   String ecolor1 = "";
   String rest1 = "";
   String rcolor1 = "";
   String event2 = "";
   String ecolor2 = "";
   String rest2 = "";
   String rcolor2 = "";
   String rest3 = "";
   String rcolor3 = "";
   String rest4 = "";
   String rcolor4 = "";
   String blocker = "";
   String bag = "";
   String conf = "";
   String orig_by = "";
   String orig_name = "";
   String errMsg = "";
   String reqCourse = "";
   String LCourse = "";         // lottery course - course specified in lottery3 table entry

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
/*
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
*/

   String lott2 = "";        // ditto for 2nd lottery on this day
   String lcolor2 = "";
/*
   int sdays2 = 0;
   int sdtime2 = 0;
   int edays2 = 0;
   int edtime2 = 0;
   int pdays2 = 0;
   int ptime2 = 0;
   int slots2 = 0;
   int lskip2 = 0;
   int lstate2 = 0;
*/

   String lott3 = "";        // ditto for 3rd lottery on this day (max of 3 for now)!!!!!!
   String lcolor3 = "";
/*
   int sdays3 = 0;
   int sdtime3 = 0;
   int edays3 = 0;
   int edtime3 = 0;
   int pdays3 = 0;
   int ptime3 = 0;
   int slots3 = 0;
   int lskip3 = 0;
   int lstate3 = 0;
*/

   int lyear = 0;
   int lmonth = 0;
   int lday = 0;
   int advance_days = 0;       // copy of 'index' = # of days between today and the day of this sheet


   // **************** end of lottery save area ***********

   int j = 0;
   int jump = 0;
   int teecurr_id = 0;
   int index = 0;
   int wa = 0;
   int pc = 0;
   int ca = 0;
   int mc = 0;
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
   int guest_id1 = 0;
   int guest_id2 = 0;
   int guest_id3 = 0;
   int guest_id4 = 0;
   int guest_id5 = 0;

   int courseCount = 0;

   // define variables for event/lottery modes
   int group_size = 0;
   int window_stime = 0;
   int window_etime = 0;
   
   
   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con);

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block
   
   //
   //  parm block to hold the member restrictions for this date and member
   //
   parmRest parmr = new parmRest();          // allocate a parm block

   //
   //  parm block to hold the Course Colors
   //
   parmCourseColors colors = new parmCourseColors();          // allocate a parm block

   int cMax = 0;                               // max courses
   int colorMax = colors.colorMax;             // max number of colors defined
   
   //
   //  Array to hold the course names
   //
   ArrayList<String> courseA = new ArrayList<String>();
   ArrayList<Integer> courseTotalN = new ArrayList<Integer> ();        // array list to hold the notification counts
   ArrayList<Integer> fivesA = new ArrayList<Integer> ();              // array list to hold 5-some option for each course
   
   String courseName = "";                          // 
   String courseName1 = "";
   String courseT = "";
   int tmp_i = 0;

   
   //
   // Get the limit variable for events
   //
   int limit = -1;
   String slimit = req.getParameter("limit");
   //if (slimit == null || slimit.equals("ALL")) limit = -1;
   try {
      limit = Integer.parseInt(slimit);
   }
   catch (NumberFormatException e) { }
   
   //
   //  Get the name of the event or lottery requested
   //
   String name = req.getParameter("name");
   if (name == null) name = "";
   
   //
   //  Get the current username
   //
   String user = (String)session.getAttribute("user");
   
   //
   //  Get the golf course name requested and the name of the day passed
   //
   String course = req.getParameter("course");
   if (course == null) course = "";    // change from null string
   
   String returnCourse = req.getParameter("returnCourse");
   if (returnCourse == null) returnCourse = "";    // change from null string
   
   if (course.equals("")) course = returnCourse;
   
   String hideUnavail = req.getParameter("hide");
   //if (mode.equals("")) {
       // if here w/o a mode (legacy mode) then default to show all tee times
       if (hideUnavail == null || !hideUnavail.equals("1")) hideUnavail = "0";
   //} else {
       // if here in a specific mode then default to hiding tee times outside the window
       //if (hideUnavail == null || !hideUnavail.equals("0")) hideUnavail = "1";
   //}
   
   String emailOpt = req.getParameter("email");
   if (emailOpt == null || !emailOpt.equals("yes")) emailOpt = "no";
   
   //
   //  get the jump parm if provided (location on page to jump to)
   //
   if (req.getParameter("jump") != null) {
   
      jumps = req.getParameter("jump");         //  jump index value for where to jump to on the page
      try {
        jump = Integer.parseInt(jumps);
      } catch (Exception ignore) {
      }
   }

   //
   //   Adjust jump so we jump to the selected line minus 14 so its not on top of page
   //
   if (jump > 14) {

      jump = jump - 14;

   } else {

      jump = 0;         // jump to top of page
   }

   //
   //    'index' contains an index value representing the date selected
   //    (0 = today, 1 = tomorrow, etc.)
   //
   num = req.getParameter("index");         // get the index value of the day selected

   //
   //  Convert the index value from string to int
   //
   try {
      index = Integer.parseInt(num);
   }
   catch (NumberFormatException e) {
      // ignore error
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
   int cal_hour = cal.get(Calendar.HOUR_OF_DAY);     // 24 hr clock (0 - 23)
   int cal_min = cal.get(Calendar.MINUTE);
   int cal_time = (cal_hour * 100) + cal_min;     // get time in hhmm format
   cal_time = SystemUtils.adjustTime(con, cal_time);

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

   month = month + 1;                            // month starts at zero

   String day_name = day_table[day_num];         // get name for day

   long date = year * 10000;                     // create a date field of yyyymmdd
   date = date + (month * 100);
   date = date + day;                            // date = yyyymmdd (for comparisons)

   String date_mysql = year + "-" + SystemUtils.ensureDoubleDigit(month) + "-" + SystemUtils.ensureDoubleDigit(day);
   //String time_mysql = (cal_time / 100) + ":" + SystemUtils.ensureDoubleDigit(cal_time % 100) + ":00"; // current time for 
   
   out.println("<!-- date_mysql=" + date_mysql + " -->");
   out.println("<!-- cal_time=" + cal_time + " -->");
   
   //
   // Handle returning to tee sheet - first check for unsent emails, inform user and
   //
   if (req.getParameter("backToSheet") != null) {
       
       out.println("<!-- backToSheet in mode " + mode + " -->");

       int tmp_count = 0;
       int int_mode = 1;

       String tmp_mode = "Lottery";
       
       if (!mode.equals("")) {
         
           String whereClause = "date = ? AND lottery_email = ? AND lottery = ?"; 

           if (mode.equals("WAITLIST")) {

               tmp_mode = "Wait List";
               int_mode = 3;
               whereClause = "date = ? AND lottery_email = ?"; 

           } else if (mode.equals("EVENT")) {

               tmp_mode = "Event";
               int_mode = 2;
               whereClause = "date = ? AND lottery_email = ? AND event = ?";            
           }

           try {

               PreparedStatement pstmt = con.prepareStatement ("" +
                        "SELECT COUNT(*) " +
                        "FROM teecurr2 " +
                        "WHERE " + whereClause);  
                  //      ((name.equals("")) ? "" : " AND lottery = ?;"));    // was name = ?

               pstmt.clearParameters();
               pstmt.setLong(1, date);
               pstmt.setInt(2, int_mode);
          //     if (!name.equals("")) pstmt.setString(3, name);
               if (!mode.equals("WAITLIST")) pstmt.setString(3, name);
               rs = pstmt.executeQuery();

               if ( rs.next() ) tmp_count = rs.getInt(1);

               pstmt.close();

           } catch (Exception exp) {

               SystemUtils.buildDatabaseErrMsg("Fatal error check for unsent emails. club=" + club + ", mode=" + mode, exp.toString(), out, false);  
           }
         
       }
       
       if (tmp_count == 0) {
           
           if (!returnCourse.equals("")) {
               course = returnCourse;
           }
            
           out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_jump?index=" + index + "&course=" + course + "\">");
       
       } else {
            
            out.println("<center>");
            out.println("<h2>Send " + tmp_mode + " Emails</h2>");
            out.println("<p>There are " + tmp_count + " " + tmp_mode.toLowerCase() + " times " + ((!name.equals("")) ? name : "") + " that need to have emails sent.</p>");
            out.println("<p>Do you want to send them now or later?</p>");
            out.println("<form>");
            
            out.println("<input type=hidden name=doEmails value=\"1\">");
            out.println("<input type=hidden name=mode value=\"" + mode + "\">");
            out.println("<input type=hidden name=name value=\"" + name + "\">");
            out.println("<input type=hidden name=date value=\"" + date + "\">");
            out.println("<input type=hidden name=index value=\"" + index + "\">");
            out.println("<input type=hidden name=course value=\"" + course + "\">");
            out.println("<input type=hidden name=returnCourse value=\"" + returnCourse + "\">");
            
            out.println("<input type=submit value=\"Send Now\">");
            out.println("</form>");
            
            out.println("<form method=get action=/" + rev + "/servlet/Proshop_jump>");
            out.println("<input type=hidden name=index value=\"" + index + "\">");
            if (!returnCourse.equals("")) {
               out.println("<input type=hidden name=course value=\"" + returnCourse + "\">");
            } else {
               out.println("<input type=hidden name=course value=\"" + course + "\">");
            }
            out.println("<input type=submit value=\"Send Later\">");
            
            out.println("</form>");
            
            out.println("<p>Note:&nbsp; <i>If players do not have email addresses or have choosen not to receive emails, no emails will be sent to them.</i></p>");
            
            out.println("</center>");
            
       }
       
       out.close();
       return;
       
   } else if (req.getParameter("doEmails") != null) {
       
       out.println("<!-- sending emails for " + mode + " '" + name + "' -->");
       
       int event_date = 0;
       
       String sevent_date = req.getParameter("event_date");
       try {
          event_date = Integer.parseInt(sevent_date);
       }
       catch (NumberFormatException ignore) { }
       
       String clubName = SystemUtils.getClubName(con);
       
       if (mode.equals("WAITLIST")) {
           
           sendWaitListEmails(clubName, name, (int)date, index, course, out, con);
           
       } else if (mode.equals("LOTT")) {
           
           setLotteryState5(clubName, club, name, (int)date, index, out, con);
           
           sendLotteryEmails(clubName, name, (int)date, index, course, returnCourse, out, con);
           
       } else if (mode.equals("EVENT")) {
           
           sendEventEmails(clubName, name, (int)date, index, course, out, con);
           
       }
       // call the send email routine
       
       //
       // Completed update - reload page
       //
       //out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_dlott?index=" + index + "&course=" + course + "&lott_name=" + lott_name + "&hide="+hideUnavail+"&backToSheet\">");
              
       out.close();
       return;
   }
   
    
   // Check access rights of current proshop user based on what mode user is running in
   if (mode.equalsIgnoreCase("WAITLIST") && !SystemUtils.verifyProAccess(req, "WAITLIST_MANAGE", con, out)) {
       SystemUtils.restrictProshop("WAITLIST_MANAGE", out);
       return;
   } else if (mode.equalsIgnoreCase("EVENT") && !SystemUtils.verifyProAccess(req, "EVNTSUP_MANAGE", con, out)) {
       SystemUtils.restrictProshop("EVNTSUP_MANAGE", out);
       return;
   } else if (mode.equalsIgnoreCase("LOTT") && !SystemUtils.verifyProAccess(req, "LOTT_APPROVE", con, out)) {
       SystemUtils.restrictProshop("LOTT_APPROVE", out);
       return;
   } else if (mode.equals("") && !SystemUtils.verifyProAccess(req, "TS_CTRL_TSEDIT", con, out)) {
       SystemUtils.restrictProshop("TS_CTRL_TSEDIT", out);
       return;
   }
   
   try {
      
      errMsg = "Get course names.";
        
      //
      // Get the Guest Types from the club db
      //
      getClub.getParms(con, parm);        // get the club parms

      //
      //   Remove any guest types that are null - for tests below
      //
      i = 0;
      while (i < parm.MAX_Guests) {

         if (parm.guest[i].equals( "" )) {

            parm.guest[i] = "$@#!^&*";      // make so it won't match player name
         }
         i++;
      }         // end of while loop

      //
      //   Get course names if multi-course facility so we can determine if any support 5-somes
      //
      i = 0;
      
      //
      // Get the time window for the event/lottery - we'll use this to hide times
      // from the tee sheet portion if the user selects to only view applicable tee times
      // we get the waitlist time frame later on in one of the sql statements
      //
      if (mode.equals("EVENT")) {
          
          PreparedStatement pstmt = con.prepareStatement ("" +
                "SELECT courseName, stime, etime, size " +
                "FROM events2b " +
                "WHERE name = ?");

          pstmt.clearParameters();
          pstmt.setString(1, name);
          rs = pstmt.executeQuery();

          if (rs.next()) {

              if (parm.multi != 0 && course.equals("")) course = rs.getString(1);  // if no course was specified (default) then set the course variable to the course this event is for
              window_stime = rs.getInt(2);
              window_etime = rs.getInt(3);
              group_size = rs.getInt(4);
          }
          rs.close();
          pstmt.close();
          
      } else if (mode.equals("LOTT")) {
          
          PreparedStatement pstmt = con.prepareStatement ("" +
                "SELECT courseName, stime, etime " +
                "FROM lottery3 " +
                "WHERE name = ?");

          pstmt.clearParameters();
          pstmt.setString(1, name);
          rs = pstmt.executeQuery();

          if (rs.next()) {

              LCourse = rs.getString(1);                                    // get course(s) this lottery is for
              if (parm.multi != 0 && course.equals("")) course = LCourse;   // if no course was specified (default) then set the course variable to the course this lottery is for
              window_stime = rs.getInt(2);
              window_etime = rs.getInt(3);
          }
          rs.close();
          pstmt.close();
          
          if (LCourse.equals( "-ALL-" )) {      // if lottery is for ALL courses
             
             multi_lot = 1;                     // indicate such
          }
             

      } else if (mode.equals("WAITLIST")) {
          
          PreparedStatement pstmt = con.prepareStatement ("" +
                "SELECT DATE_FORMAT(sdatetime, '%H%i') AS window_stime, DATE_FORMAT(edatetime, '%H%i') AS window_etime " +
                "FROM wait_list " +
                "WHERE name = ?");

          pstmt.clearParameters();
          pstmt.setString(1, name);
          rs = pstmt.executeQuery();

          if (rs.next()) {

              window_stime = rs.getInt(1);
              window_etime = rs.getInt(2);
          }
          rs.close();
          pstmt.close();
      }
      

      // end if mode
      
      if (parm.multi != 0) {           // if multiple courses supported for this club

         i = 0;
         int total = 0;
         //
         //  Get the names of all courses for this club
         //
         if (IS_TLT) {
             
            PreparedStatement pstmt = con.prepareStatement("" + 
                 "SELECT c.courseName, (SUM(IF((IFNULL(n.converted, 1) = 0 && DATE(n.req_datetime) = ?), 1, 0))) AS total " + 
                 "FROM clubparm2 c " + 
                 "LEFT JOIN notifications n ON c.clubparm_id = n.course_id " + 
                 "GROUP BY n.course_id;");
             
            pstmt.clearParameters();
            pstmt.setString(1, date_mysql);
            rs = pstmt.executeQuery();

            while (rs.next()) {

               courseName = rs.getString(1);
               courseA.add (courseName);           // add course name to array

               total = rs.getInt(2);
               courseTotalN.add (total);           // add notifcation count
            }
            pstmt.close();
             
         } else {
             
            courseA = Utilities.getCourseNames(con);     // get all the course names
            
            courseTotalN.add (0);                        // indicate NO notifcation count
         }
         
         courseCount = courseA.size();     // number of courses         
 
         if (courseCount > 1) {
             
            courseA.add ("-ALL-");        // add '-ALL-' option
         }
         
         //
         //  Make sure we have a course (in case we came directly from the Today's Tee Sheet menu)
         //
         if (courseName1.equals( "" )) {
           
            courseName1 = courseA.get(0);    // grab the first one
         }
         
      }

      //
      //  Get the walk/cart options available and 5-some support
      //
      i = 0;
      if (course.equals( "-ALL-" )) {

         //
         //  Check all courses for 5-some support
         //
         cMax = courseA.size();     // number of courses         
         i = 0;
         loopc:
         while (i < cMax) {

            courseName = courseA.get(i);       // get a course name

            if (!courseName.equals( "-ALL-" )) {   // skip if -ALL-
              
               if (courseName.equals( "" )) {      // done if null
                  break loopc;
               }
               
               getParms.getCourse(con, parmc, courseName);       // get parms for this course
               
               fivesA.add (parmc.fives);            // get fivesome option
               
               if (parmc.fives == 1) {
                  fives = 1;                          // 5-somes supported on at least one course
               }
            }
            i++;
         }

      } else {       // single course requested

         getParms.getCourse(con, parmc, course);

         fives = parmc.fives;      // get fivesome option
      }
        
      fivesALL = fives;            // save 5-somes option for table display below

      i = 0;

      
      

        //  parm block to hold the club parameters
        //
        parmItem parmLotteries = new parmItem();          // allocate a parm block
        try {
            getItem.getLotteries(date, index, course, day_name, parmLotteries, con);
        } catch (Exception e) {
            dbError(out, e, index, course, "");
        }
      
      
      out.println("<!-- LOTTERIES -->");
      out.print("<!-- count=" + parmLotteries.count + " -->");
      out.print("<!-- name=" + parmLotteries.name[0] + " -->");
      out.print("<!-- course=" + parmLotteries.courseName[0] + " -->");
      out.print("<!-- fb=" + parmLotteries.fb[0] + " -->");
      out.print("<!-- stime=" + parmLotteries.stime[0] + " -->");
      out.print("<!-- etime=" + parmLotteries.etime[0] + " -->");
      out.println("<!-- color=" + parmLotteries.color[0] + " -->");
        

      
      
        //  parm block to hold the club parameters
        //
        parmItem parmEvents = new parmItem();          // allocate a parm block
        try {
            getItem.getEvents(date, course, parmEvents, con);
        } catch (Exception e) {
            dbError(out, e, index, course, "");
        }
        
      
      out.println("<!-- EVENTS -->");
      out.print("<!-- count=" + parmEvents.count + " -->");
      out.print("<!-- name=" + parmEvents.name[0] + " -->");
      out.print("<!-- course=" + parmEvents.courseName[0] + " -->");
      out.print("<!-- fb=" + parmEvents.fb[0] + " -->");
      out.print("<!-- stime=" + parmEvents.stime[0] + " -->");
      out.print("<!-- etime=" + parmEvents.etime[0] + " -->");
      out.println("<!-- color=" + parmEvents.color[0] + " -->");
      
      
      
        //  parm block to hold the club parameters
        //
        parmItem parmWaitLists = new parmItem();          // allocate a parm block
        try {
            getItem.getWaitLists(date, course, day_name, parmWaitLists, con);
        } catch (Exception e) {
            dbError(out, e, index, course, "");
        }
        
      
      out.println("<!-- WAIT LISTS -->");
      out.print("<!-- count=" + parmWaitLists.count + " -->");
      out.print("<!-- name=" + parmWaitLists.name[0] + " -->");
      out.print("<!-- course=" + parmWaitLists.courseName[0] + " -->");
      out.print("<!-- stime=" + parmWaitLists.stime[0] + " -->");
      out.print("<!-- etime=" + parmWaitLists.etime[0] + " -->");
      out.println("<!-- color=" + parmWaitLists.color[0] + " -->");
      
      
    
      String string7b = "";
      String string7c = "";
      String string7d = "";

      out.println("");
      
      errMsg = "Scan Restrictions.";

      //
      //  Get all restrictions for this day and user (for use when checking each tee time below)
      //
      parmr.user = user;
      parmr.mship = "";
      parmr.mtype = "";
      parmr.date = date;
      parmr.day = day_name;
      parmr.course = courseName1;

      getRests.getAll(con, parmr);       // get the restrictions
      
      //
      //   Statements to find any restrictions, events or lotteries for today
      //
      if (course.equals( "-ALL-" )) {
         string7b = "SELECT name, recurr, color, id, stime, etime FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
                    "AND showit = 'Yes'";
      } else {
         string7b = "SELECT name, recurr, color, id, stime, etime FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
                    "AND (courseName = ? OR courseName = '-ALL-') AND showit = 'Yes'";
      }

      if (course.equals( "-ALL-" )) {
         string7c = "SELECT name, color FROM events2b WHERE date = ? AND inactive = 0";
      } else {
         string7c = "SELECT name, color FROM events2b WHERE date = ? AND inactive = 0 " +
                    "AND (courseName = ? OR courseName = '-ALL-')";
      }


      PreparedStatement pstmt7b = con.prepareStatement (string7b);

      //
      //  Scan the events, restrictions and lotteries to build the legend
      //
      pstmt7b.clearParameters();          // clear the parms
      pstmt7b.setLong(1, date);
      pstmt7b.setLong(2, date);
      if (!course.equals( "-ALL-" )) {
         pstmt7b.setString(3, course);
      }

      rs = pstmt7b.executeQuery();      // find all matching restrictions, if any

      while (rs.next()) {

         rest = rs.getString(1);
         rest_recurr = rs.getString(2);
         rcolor = rs.getString(3);
         
         boolean showRest = getRests.showRest(rs.getInt("id"), -99, rs.getInt("stime"), rs.getInt("etime"), date, day_name, course, con);
         
         if (showRest) {    // Only display on legend if not suspended for entire day

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
         }
      }                  // end of while
      pstmt7b.close();
    
   
      errMsg = "Scan Events.";

      PreparedStatement pstmt7c = con.prepareStatement (string7c);
      
      pstmt7c.clearParameters();          // clear the parms
      pstmt7c.setLong(1, date);
      if (!course.equals( "-ALL-" )) {
         pstmt7c.setString(2, course);
      }

      rs = pstmt7c.executeQuery();      // find all matching events, if any

      while (rs.next()) {

         event = rs.getString(1);
         ecolor = rs.getString(2);

         if ((!event.equals( event1 )) && (event1.equals( "" ))) {

            event1 = event;
            ecolor1 = ecolor;

            if (ecolor.equalsIgnoreCase( "default" )) {

               ecolor1 = "#F5F5DC";
            }

          } else {

            if ((!event.equals( event1 )) && (!event.equals( event2 )) && (event2.equals( "" ))) {

               event2 = event;
               ecolor2 = ecolor;

               if (ecolor.equalsIgnoreCase( "default" )) {

                  ecolor2 = "#F5F5DC";
               }
            }
         }

      }                  // end of while
      pstmt7c.close();


   
      errMsg = "Build Tee Sheet Headings.";

      //****************************************************
      // Define tee sheet size and build it
      //****************************************************

      // define our two arrays that describe the column sizes
      // index = column number, value = size in pixels
      
      // define columns for unassigned portion of sheet
      int [] ucol_width = new int [9];
      int ucol_start[] = new int[9]; 
      
      if (mode.equals("WAITLIST")) {
      
          // define columns for waitlist mode
          //int [] ucol_width = new int [8];
          //int ucol_start[] = new int[8]; 
          ucol_width[0] = 0;                                        // unused
          ucol_width[1] = 40;                                       // +/-
          ucol_width[2] = 40;                                       // pos
          ucol_width[3] = 130;                                      // time
          ucol_width[4] = 0;                                        // course (irrelevant)
          ucol_width[5] = 0;                                        // unused (was fb)
          ucol_width[6] = (fivesALL == 0) ? 470 : 620;              // members
          ucol_width[7] = 60;                                       // players
          ucol_width[8] = 60;                                       // holes

          ucol_start[1] = 0;
          ucol_start[2] = ucol_start[1] + ucol_width[1];
          ucol_start[3] = ucol_start[2] + ucol_width[2];
          ucol_start[4] = ucol_start[3] + ucol_width[3];
          ucol_start[5] = ucol_start[4] + ucol_width[4];
          ucol_start[6] = ucol_start[5] + ucol_width[5];
          ucol_start[7] = ucol_start[6] + ucol_width[6];
          ucol_start[8] = ucol_start[7] + ucol_width[7];
      
      } else if (mode.equals("EVENT")) {
          
          if (group_size > 1) {

              ucol_width[0] = 0;                                        // unused
              ucol_width[1] = 160;                                      // player 1
              ucol_width[2] = 160;                                      // player 2
              ucol_width[3] = 160;                                      // player 3
              ucol_width[4] = 160;                                      // player 4
              ucol_width[5] = 160;                                      // player 5
              ucol_width[6] = 20;                                       // notes

          } else {

              ucol_width[0] = 0;                                        // unused
              ucol_width[1] = 180;                                      // player 1
              ucol_width[2] = 0;                                        // unused
              ucol_width[3] = 0;                                        // unused
              ucol_width[4] = 0;                                        // unused
              ucol_width[5] = 0;                                        // unused
              ucol_width[6] = 0;                                        // notes

          }

          ucol_start[1] = 0;
          ucol_start[2] = ucol_start[1] + ucol_width[1];
          ucol_start[3] = ucol_start[2] + ucol_width[2];
          ucol_start[4] = ucol_start[3] + ucol_width[3];
          ucol_start[5] = ucol_start[4] + ucol_width[4];
          ucol_start[6] = ucol_start[5] + ucol_width[5];
          
      } else if (mode.equals("LOTT")) {
      
          ucol_width[0] = 0;                                        // unused
          ucol_width[1] = 40;                                       // +/-
          ucol_width[2] = (multi_lot == 1) ? 71 : 80;               // assigned time or "Unassigned"
          ucol_width[3] = (multi_lot == 1) ? 71 : 80;               // desired time
          ucol_width[4] = 130;                                      // acceptable time
          ucol_width[5] = (multi_lot == 1) ? 90 : 0;                // assigned course
          ucol_width[6] = (multi_lot == 1) ? 90 : 0;                // requested course
          ucol_width[7] = 40;                                       // weight
          ucol_width[8] = (fivesALL == 0) ? 410 : 560;              // members

          ucol_start[1] = 0;
          ucol_start[2] = ucol_start[1] + ucol_width[1];
          ucol_start[3] = ucol_start[2] + ucol_width[2];
          ucol_start[4] = ucol_start[3] + ucol_width[3];
          ucol_start[5] = ucol_start[4] + ucol_width[4];
          ucol_start[6] = ucol_start[5] + ucol_width[5];
          ucol_start[7] = ucol_start[6] + ucol_width[6];
          ucol_start[8] = ucol_start[7] + ucol_width[7];
          
      } else if (IS_TLT) { // mode.equals("NOTIFY")
      
          // define columns for notification mode
          //int [] ucol_width = new int [8];
          //int ucol_start[] = new int[8]; 
          ucol_width[0] = 0;                                        // unused
          ucol_width[1] = 40;                                       // +/-
          ucol_width[2] = (course.equals( "-ALL-" )) ? 71 : 80;     // time
          ucol_width[3] = (course.equals( "-ALL-" )) ? 90 : 0;      // course
          ucol_width[4] = 0;                                        // unused (was fb)
          ucol_width[5] = (fivesALL == 0) ? 510 : 660;              // members
          ucol_width[6] = 60;                                       // friends
          ucol_width[7] = 60;                                       // holes

          ucol_start[1] = 0;
          ucol_start[2] = ucol_start[1] + ucol_width[1];
          ucol_start[3] = ucol_start[2] + ucol_width[2];
          ucol_start[4] = ucol_start[3] + ucol_width[3];
          ucol_start[5] = ucol_start[4] + ucol_width[4];
          ucol_start[6] = ucol_start[5] + ucol_width[5];
          ucol_start[7] = ucol_start[6] + ucol_width[6];
      
      }
      
      // define columns for standard tee sheet body
      int [] col_width = new int [15];
      int col_start[] = new int[15];                    // total width = 962 px (in dts-styles.css)
      if (course.equals( "-ALL-" )) {
         col_width[0] = 0;        // unused
         col_width[1] = 40;       // +/-
         col_width[2] = 71;       // time
         col_width[3] = 90;       // course name col 69
         col_width[4] = 31;       // f/b
         col_width[5] = 111;      // player 1
         col_width[6] = 39;       // player 1 trans opt
         col_width[7] = 111;      // player 2
         col_width[8] = 39;       // player 2 trans opt
         col_width[9] = 111;      // player 3
         col_width[10] = 39;      // player 3 trans opt
         col_width[11] = 111;     // player 4
         col_width[12] = 39;      // player 4 trans opt
         col_width[13] = 111;     // player 5
         col_width[14] = 39;      // player 5 trans opt
      } else {
         col_width[0] = 0;        // unused
         col_width[1] = 40;       // +/-
         col_width[2] = 80;       // time
         col_width[3] = 0;        // empty if no course name
         col_width[4] = 40;       // f/b
         col_width[5] = 120;      // player 1
         col_width[6] = 40;       // player 1 trans opt
         col_width[7] = 120;      // player 2
         col_width[8] = 40;       // player 2 trans opt
         col_width[9] = 120;      // player 3
         col_width[10] = 40;      // player 3 trans opt
         col_width[11] = 120;     // player 4
         col_width[12] = 40;      // player 4 trans opt
         col_width[13] = 120;     // player 5
         col_width[14] = 40;      // player 5 trans opt
      }
      col_start[1] = 0;
      col_start[2] = col_start[1] + col_width[1];
      col_start[3] = col_start[2] + col_width[2];
      col_start[4] = col_start[3] + col_width[3];
      col_start[5] = col_start[4] + col_width[4];
      col_start[6] = col_start[5] + col_width[5];
      col_start[7] = col_start[6] + col_width[6];
      col_start[8] = col_start[7] + col_width[7];
      col_start[9] = col_start[8] + col_width[8];
      col_start[10] = col_start[9] + col_width[9];
      col_start[11] = col_start[10] + col_width[10];
      col_start[12] = col_start[11] + col_width[11];
      col_start[13] = col_start[12] + col_width[12];
      col_start[14] = col_start[13] + col_width[13];

      int total_col_width = col_start[14] + col_width[14];
      
      // temp variable
      String dts_tmp = "";

      //
      // 
      //
      String cp_link_show = "Show Unavailable";             // defaults
      String cp_link_hide = "Hide Unavailable";
      String url_item_name = "";
      
      if (mode.equals("WAITLIST")) {
          
          cp_link_show = "Show All Tee Times";
          cp_link_hide = "Show Only Wait List Times";
          
      } else if (mode.equals("EVENT")) {
          
          cp_link_show = "Show All Tee Times";
          cp_link_hide = "Show Only Event Times";
          url_item_name = "&name=" + name;
          
      } else if (mode.equals("LOTT")) {
          
          cp_link_show = "Show All Tee Times";
          cp_link_hide = "Show Only Lottery Times";
          url_item_name = "&name=" + name;
          
      }
      
      
      //
      //  Build the HTML page
      //
      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
      out.println("<html>\n<!--Copyright notice:  This software (including any images, servlets, applets, photographs, animations, video, music and text incorporated into the software) ");
      out.println("is the proprietary property of ForeTees, LLC or its suppliers and its use, modification and distribution are protected ");
      out.println("and limited by United States copyright laws and international treaty provisions and all other applicable national laws. ");
      out.println("\nReproduction is strictly prohibited.-->");
      out.println("<head>");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
      out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
      out.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");

      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/dts-styles.css\">");

      out.println("<title>Proshop Tee Sheet Management Page</title>");

      out.println("<script language=\"javascript\">");          // Jump script
      out.println("<!--");
      out.println("function jumpToHref(anchorstr) {");
      out.println("if (location.href.indexOf(anchorstr)<0) {");
      out.println(" location.href=anchorstr; }");
      out.println("}");
      out.println("// -->");
      out.println("</script>");                               // End of script

      // include dts javascript source file
      out.println("<script language=\"javascript\" src=\"/" +rev+ "/dts-scripts.js\"></script>");

      out.println("</head>");

      out.println("<body onLoad='jumpToHref(\"#jump" + jump + "\");' bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<a name=\"jump0\"></a>");     // create a default jump label (start of page)

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // whole page
      out.println("<tr><td align=\"center\">");

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // table for cmd tbl & instructions
      out.println("<tr><td align=\"left\" valign=\"middle\">");

         //
         //  Build Control Panel
         //
         out.println("<table border=\"1\" width=\"150\" cellspacing=\"3\" cellpadding=\"3\" bgcolor=\"#8B8970\" align=\"left\">");
         out.println("<tr>");
         out.println("<td align=\"center\"><font size=\"3\" class=cpHead><b>Control Panel</b><br>");
         
         // refresh page
         out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
         out.println("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?mode=" + mode + "&index=" +index+ "&name=" +name+ "&course=" +course+ "&returnCourse=" +returnCourse+ "&email=" +emailOpt+ "&hide=" + hideUnavail + "\" class=cpLink title=\"Refresh This Page\" alt=\"Refresh\">");
         out.println("Refresh This Sheet</a><br>");
        
         // if today or future
         if (index >= 0) {
             
             // block unblock tee times
             out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
             out.println("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?mode=" + mode + "&index=" +index+ "&name=" +name+ "&course=" +course+ "&returnCourse=" +returnCourse+ "&blockers&email=" +emailOpt+ "\" class=cpLink title=\"Block or Unblock Tee Times\" alt=\"Blockers\">");
             out.println("Adjust Blockers</a><br>");

             // hole assignments - only include if there is a shotgun event on this day 
             if (mode.equals("EVENT") || mode.equals("")) {
                 stmt = con.createStatement();        // create a statement
                 rs = stmt.executeQuery("SELECT teecurr_id FROM teecurr2 WHERE event != '' AND event_type = 1 AND date = " +date+ " LIMIT 1"); // was GROUP BY event
                 if (rs.next()) {
                    out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
                    out.println("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?mode=" + mode + "&index=" +index+ "&name=" +name+ "&course=" +course+ "&sha&email=" +emailOpt+ "\" class=cpLink title=\"Shotgun Hole Assignments\" alt=\"Shotgun\">");
                    out.println("Shotgun Hole Assignments</a><br>");
                 }
                 stmt.close();
             }
         }
         
         // toggle show all times
         out.println("</font></td></tr><tr><td align=\"center\" nowrap><font size=\"2\">");
         out.println("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?mode=" + mode + "&index=" +index+ "&name=" +name+ "&course=" +course+ "&returnCourse=" +returnCourse+ "&email=" +emailOpt+ ((hideUnavail.equals("1") ? "" : "&hide=1")) + "\" class=cpLink title=\"Toggle Hiding Tee Times\" alt=\"Toggle Hiding Tee Timess\">");
         out.println((hideUnavail.equals("1") ? cp_link_show : cp_link_hide) + "</a><br>");
         
         //Êshow convert all assigned link if in lott mode
         if (mode.equals("LOTT") && !course.equals("-ALL-")) {
             out.println("</font></td></tr><tr><td align=\"center\" nowrap><font size=\"2\">");
             out.println("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?mode=" + mode + "&index=" +index+ "&date=" + date + "&course=" +course+ "&returnCourse=" +returnCourse+ "&name=" + name + "&hide=" + hideUnavail + "&convert=all&email=" +emailOpt+ "\" title=\"Convert All Requests\" alt=\"Convert All Requests\">");
             out.println("Convert Assigned Requests</a><br>");
         }
         
         // make new notification
         if (IS_TLT && index >= 0) {
             out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
             out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?newnotify=1&course=" +course+ "&mm=" + month + "&dd=" + day + "&yy=" + year + "\" class=cpLink title=\"Make New Notification\" alt=\"Make New Notification\">");
             out.println("<nobr>Make New Notification</nobr></a><br>");
         }
         
         // back to tee sheet (only if not here from old tee sheets)
         if (index >= 0) {
             out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
             
             out.println("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?mode=" + mode + "&index=" +index+ "&course=" +course+ "&returnCourse=" +returnCourse+ "&name=" + name + "&date=" + date + "&backToSheet\" title=\"Return to Tee Sheet\" alt=\"Return\">");

             //out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +index+ "&course=" +course+ "\" class=cpLink title=\"Return to Tee Sheet\" alt=\"Return\">");
             
             
             //out.println("<a href=\"/" +rev+ "/servlet/Proshop_dlott?index=" +index+ "&course=" +course+ "&lott_name=" + lott_name + "&lott_date=" + date + "&backToSheet\" title=\"Return to Tee Sheet\" alt=\"Return\">");
             //out.println("<a href=\"/" +rev+ "/servlet/Proshop_devnt?index=" +index+ "&course=" +course+ "&name=" + event_name + "&event_date=" + date + "&backToSheet\" title=\"Return to Tee Sheet\" alt=\"Return\">");
             out.println("<nobr>Return to Tee Sheet</nobr></a><br>");
         } else {
             // links for old tee sheets
             out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
             out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +index+ "&course=" +course+ "&oldsheets\" class=cpLink title=\"Return to Date Selection\" alt=\"Return\">");
             out.println("<nobr>Return to Date Selection</nobr></a><br>");
             
             out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
             out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?oldsheets&course=" + course + "&calDate=" + month + "/" + day + "/" + year + "\" class=cpLink title=\"Back\" alt=\"Back\" onclick=\"return checkCourse()\">");
             out.println("<nobr>Back</nobr></a><br>");
         }
         
         out.println("</font></td></tr></table>"); // end Control Panel table

      out.println("</td>");                                 // end of column for control panel
      out.println("<td align=\"center\" width=\"20\">&nbsp;");     // empty column for spacer

      out.println("</td>");
      out.println("<td align=\"left\" valign=\"top\">");     // column for instructions, course selector, calendars??

         //**********************************************************
         //  Continue with instructions and tee sheet
         //**********************************************************

         out.println("<table cellpadding=\"5\" cellspacing=\"3\" width=\"80%\">");
         out.println("<tr><td align=\"center\">");
         
         String tmp_mode = "Tee Time";  // default
         if (mode.equals("EVENT")) {
             tmp_mode = "Event";
         } else if (mode.equals("LOTT")) {
             tmp_mode = "Lottery";
         } else if (mode.equals("WAITLIST")) {
             tmp_mode = "Wait List";
         }
             
         out.println("<p align=\"center\"><font size=\"5\">Golf Shop " + ((IS_TLT) ? "Notification" : tmp_mode) + " Management</font></p>");
         out.println("</td></tr></table>");
         
         
        out.println("<table cellpadding=\"3\" width=\"80%\">");
        out.println("<tr><td bgcolor=\"#336633\"><font color=\"#FFFFFF\" size=\"2\">");
        out.println("<b>Instructions:</b>"); 
        if (IS_TLT) out.println("To <b>move a notification</b> to the tee sheet, click on the 'Time' portion of the notification and drag to the desired position on the tee sheet.&nbsp; ");
        if (IS_TLT) out.println("To <b>edit</b> a notification, click on the edit icon <img src=/v5/images/dts_edit.gif width=13 height=13 border=0> for the notification you wish to modify.&nbsp; ");
        out.println("To <b>insert</b> a new tee time, click on the plus icon <img src=/v5/images/dts_newrow.gif width=13 height=13 border=0> in the tee time you wish to insert <i>after</i>.&nbsp; ");
        out.println("To <b>delete</b> a tee time, click on the trash can icon <img src=/v5/images/dts_trash.gif width=13 height=13 border=0> in the tee time you wish to delete.&nbsp; ");
        out.println("To <b>move an entire tee time</b>, click on the 'Time' value and drag the tee time to the new position.&nbsp; ");
        out.println("To <b>move an individual player</b>, click on the Player and drag the name to the new position.&nbsp; ");
        out.println("To <b>change</b> the F/B value or the C/W value, just click on it and make the selection.&nbsp; ");
        out.println("Empty 'player' cells indicate available positions.&nbsp; ");
        out.println("Special Events and Restrictions, if any, are colored (see legend below).");
        if (mode.equals("LOTT")) {
           out.println("<BR><BR>If a lottery request is Unassigned (Unass.), the number in parentheses represents the Reason Code as follows:");
           out.println("<BR>&nbsp;&nbsp;&nbsp;0 = Not Processed, 1 = No Times Available, 2 = Member Restricted, 9 = Other, 99 = System Error");
        }  
        out.println("</font></td></tr></table>");
        
         
         out.println("</td></tr></table>"); // end tbl for instructions
         
        //out.println("<table cellpadding=\"3\" align=\"center\" width=\"80%\"><tr><td>");
        // <img src=/v5/images/dts_newrow.gif height=16 border=1>
        // <img src=/v5/images/dts_trash.gif height=16 border=1>
                
     
       /*
         if (IS_TLT) {
             out.println("<tr><td bgcolor=\"#CCCCAA\" align=\"left\" nowrap>");
             out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;");
             out.println("To <b>move a notification</b> to the tee sheet, click on the 'Time' value and drag to the new position.");
             out.println("</td></tr>");
         }
         
         out.println("<tr><td bgcolor=\"#CCCCAA\" align=\"left\" nowrap>");
         out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("To <b>insert</b> a new tee time, ");
         out.println("click on the red plus sign <b>'+'</b> in the tee time you wish to insert <b>after</b>. ");
         out.println("</td></tr>");

         out.println("<tr><td bgcolor=\"#CCCCAA\" align=\"left\" nowrap>");
         out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("To <b>delete</b> a tee time, click on the red minus sign <b>'-'</b> in the tee time you wish to delete. ");
         out.println("</td></tr>");

         out.println("<tr><td bgcolor=\"#CCCCAA\" align=\"left\" nowrap>");
         out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("To <b>move an entire tee time</b>, click on the 'Time' value and drag the tee time to the new position.");
         out.println("</td></tr>");

         out.println("<tr><td bgcolor=\"#CCCCAA\" align=\"left\" nowrap>");
         out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("To <b>move an individual player</b>, click on the Player and drag the name to the new position.");
         out.println("</td></tr>");

         out.println("<tr><td bgcolor=\"#CCCCAA\" align=\"left\" nowrap>");
         out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("To <b>change</b> the F/B value or the C/W value, just click on it and make the selection.");
         out.println("</font></td></tr></table>");
 
      out.println("</font></td></tr></table>");
      */

      out.println("<p><font size=\"2\">");
      out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
      
    if (!course.equals( "" )) {

        out.println("&nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b></p>");
    }
      
      
      
      errMsg = "Build Course Selection.";

      //
      //  If multiple courses, then add a drop-down box for course names
      //
      String tmp_ncount = "";
      out.println("<!-- courseCount=" + courseCount + " -->");
      if (parm.multi != 0) {           // if multiple courses supported for this club

         //
         //  use 2 forms so you can switch by clicking either a course or a date
         //
         if (courseCount < 5) {        // if < 5 courses, use buttons

            out.println("<p><font size=\"2\">");
            out.println("<b>Select Course:</b>&nbsp;&nbsp;");
            
            for (i=0; i<courseA.size(); i++) {

               courseName = courseA.get(i);      // get course name from array

               tmp_ncount = "";
             
               if (IS_TLT && i<courseTotalN.size() && courseTotalN.get(i) > 0) {
                   tmp_ncount = " (" + courseTotalN.get(i) + ")";
               }
               out.print("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?mode="+mode+"&index=" +index+ "&name=" +name+ "&course=" +courseName+ "&returnCourse=" +returnCourse+ "&hide=" + hideUnavail + "\" style=\"color:blue\" target=\"_top\" title=\"Switch to new course\" alt=\"" +courseName+ "\">");
               out.print(courseName + tmp_ncount + "</a>");
               out.println("&nbsp;&nbsp;&nbsp;");
            }
            out.println("</p>");

         } else {     // use drop-down menu

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" name=\"cform\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");   // use current date
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"select\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" +returnCourse+ "\">");

            out.println("<b>Course:</b>&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"course\" onchange=\"document.cform.submit()\">");

            for (i=0; i<courseA.size(); i++) {

               courseName = courseA.get(i);      // get course name from array

               tmp_ncount = "";
             
               if (IS_TLT && i<courseTotalN.size() && courseTotalN.get(i) > 0) {
                  tmp_ncount = (courseTotalN.get(i) == 0) ? "" : " (" + courseTotalN.get(i) + ")";
               }
               out.println("<option " + ((courseName.equals( course )) ? "selected " : "") + "value=\"" + courseName + "\">" + courseName + tmp_ncount + "</option>");
            }
            
            out.println("</select>");
            out.println("</form>");
            
         } // end either display href links or drop down select
         
      } // end if multi course
      
      
      errMsg = "Build Legend";

      //
      // Display the name of the current event/lottery/wait list we are here to manage
      //
      if (!name.equals("")) out.println("<p align=center><font size=3><b><u>" + name + "</u></b></font></p>");

          int z = 0;
          
          if (SystemUtils.verifyProAccess(req, "EVNTSUP_MANAGE", con, out)) {
              for (z = 0; z < parmEvents.count; z++) {

                 out.println("<button type=\"button\" style=\"background:" + parmEvents.color[z] + "\" " +
                         "onclick=\"location.href='/v5/servlet/Proshop_dsheet?mode=EVENT&name=" + parmEvents.name[z] + "&index=" + index + "&course=" + course + "&hide=" + hideUnavail + "&email=" + emailOpt + "'\">" + parmEvents.name[z] + "</button>");
                 out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

              }
          }
      
      
          if (SystemUtils.verifyProAccess(req, "LOTT_APPROVE", con, out)) {
              for (z = 0; z < parmLotteries.count; z++) {

                 out.println("<button type=\"button\" style=\"background:" + parmLotteries.color[z] + "\" " +
                         "onclick=\"location.href='/v5/servlet/Proshop_dsheet?mode=LOTT&name=" + parmLotteries.name[z] + "&index=" + index + "&course=" + course + "&hide=" + hideUnavail + "&email=" + emailOpt + "'\">" + parmLotteries.name[z] + "</button>");
                 out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

              }
          }


          if (SystemUtils.verifyProAccess(req, "WAITLIST_MANAGE", con, out)) {
              for (z = 0; z < parmWaitLists.count; z++) {

                 out.println("<button type=\"button\" style=\"background:" + parmWaitLists.color[z] + "\" " +
                         "onclick=\"location.href='/v5/servlet/Proshop_dsheet?mode=WAITLIST&name=" + parmWaitLists.name[z] + "&index=" + index + "&course=" + course + "&hide=" + hideUnavail + "&email=" + emailOpt + "'\">" + parmWaitLists.name[z] + "</button>");
                 out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

              }
          }
     /*
      if (!event1.equals( "" )) {

         out.println("<button type=\"button\" style=\"background:" + ecolor1 + "\" " +
                 "onclick=\"location.href='/v5/servlet/Proshop_dsheet?mode=EVENT&name=" + event1 + "&index=" + index + "&hide=" + hideUnavail + "&email=" + emailOpt + "'\">" + event1 + "</button>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

         if (!event2.equals( "" )) {

            out.println("<button type=\"button\" style=\"background:" + ecolor2 + "\">" + event2 + "</button>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
         }
      }
     */
      
      if (!rest1.equals( "" )) {

         out.println("<button type=\"button\" style=\"background:" + rcolor1 + "\">" + rest1 + "</button>");

         if (!rest2.equals( "" )) {

            out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<button type=\"button\" style=\"background:" + rcolor2 + "\">" + rest2 + "</button>");

            if (!rest3.equals( "" )) {

               out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<button type=\"button\" style=\"background:" + rcolor3 + "\">" + rest3 + "</button>");

               if ((!rest4.equals( "" )) && (event1.equals( "" ))) {   // do 4 rest's if no events

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                  out.println("<button type=\"button\" style=\"background:" + rcolor4 + "\">" + rest4 + "</button>");
               }
            }
         }
      }

     /*
      if (!lott1.equals( "" )) {

         out.println("<button type=\"button\" style=\"background:" + lcolor1 + "\" " +
                 "onclick=\"location.href='/v5/servlet/Proshop_dsheet?mode=LOTT&name=" + lott1 + "&index=" + index + "&hide=" + hideUnavail + "&email=" + emailOpt + "'\">" + lott1 + "</button>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

         if (!lott2.equals( "" )) {

            out.println("<button type=\"button\" style=\"background:" + lcolor2 + "\">Lottery Times</button>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

            if (!lott3.equals( "" )) {

               out.println("<button type=\"button\" style=\"background:" + lcolor3 + "\">Lottery Times</button>");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
            }
         }
      }
     */
      if (!event1.equals( "" ) || !rest1.equals( "" ) || !lott1.equals( "" )) {
         out.println("<br>");
      }

      // *** these two lines came up from after tee sheet
      out.println("</td></tr>");
      out.println("</table>");                            // end of main page table
      out.println("</center>");


   
      errMsg = "Build Main Sheet Table.";

       //****************************************************************************
       //
       // start html for display of whatever we are displaying (tee times, etc)
       //
       //****************************************************************************
       //

       // declare these variables here before the if/else block
       PreparedStatement pstmt = null;
       PreparedStatement pstmt2 = null;
       boolean tmp_found = false;
       boolean tmp_found2 = false;
       int notification_id = 0;
       int nineHole = 0;
       int eighteenHole = 0;
       int friends = 0;
       int sum_players = 0;
       String req_time = "";
       String fullName = "";
       String cw = "";
       String notes = "";
       int dts_slot_index = 0; // slot index number
       String dts_defaultF3Color = "#FFFFFF"; // default
       int wait_list_signup_id = 0;

       int total_unassigned_slots = 0;


       // lottery
       int lottery_id = 0;
       int groups = 0;
       int max_players = 0;
       //int req_time = 0;
       int tmp_groups = 1;
       int request_color = 10;
       String in_use_by = "";
       String time_color = "";
       String groupColor = "";
       boolean blnGroupColor = false;
       boolean suspend = false;            // Member restriction suspension

       // events
       int event_id = 0;
       int wait = 0;

       int local_time = SystemUtils.getTime(con);

       // IMAGE MARKER FOR POSITIONING
       out.println("<img src=\"/"+rev+"/images/shim.gif\" width=1 height=1 border=0 id=imgMarker name=imgMarker>");

       if (mode.equals("WAITLIST")) {

          out.println("\n<!-- START OF WAITLIST SHEET HEADER -->");
          out.println(""); //  width=" + total_col_width + " align=center
          out.println("<div id=\"elHContainer2\">");
          out.println("<span class=header style=\"left: " +ucol_start[1]+ "px; width: " +ucol_width[1]+ "px\">e/-</span><span");
          out.println(" class=header style=\"left: " +ucol_start[2]+ "px; width: " +ucol_width[2]+ "px\">Pos</span><span");
          out.println(" class=header style=\"left: " +ucol_start[3]+ "px; width: " +ucol_width[3]+ "px\">Time</span><span");
          out.println(" class=header style=\"left: " +ucol_start[6]+ "px; width: " +ucol_width[6]+ "px\">Members</span><span");
          out.println(" class=header style=\"left: " +ucol_start[7]+ "px; width: " +ucol_width[7]+ "px\">Players</span><span id=widthMarker2 ");
          out.println(" class=header style=\"left: " +ucol_start[8]+ "px; width: " +ucol_width[8]+ "px\">Holes</span>");
          out.print("</div>\n");
          out.println("<!-- END OF WAITLIST HEADER -->\n");


          out.println("\n<!-- START OF WAITLIST SHEET BODY -->");
          out.println("<div id=\"elContainer2\">");

          String sql = "SELECT wls.* " + // , DATE_FORMAT(wls.ok_stime, '%l:%i %p') AS pretty_stime, DATE_FORMAT(wls.ok_etime, '%l:%i %p') AS pretty_etime
                       "FROM wait_list_signups wls, wait_list wl " +
                       "WHERE wls.date = ? AND wl.name = ? AND " +
                       "converted = 0 " +
                       ((!course.equals( "-ALL-" )) ? "AND (wl.course = ? OR course = '-ALL-') " : "") +
                       ((index == 0) ? "AND wls.ok_etime > ? " : "") + 
                       "AND wl.wait_list_id = wls.wait_list_id " + 
                       "ORDER BY wls.created_datetime";

          String sql2 = "SELECT * " +
                        "FROM wait_list_signups_players " +
                        "WHERE wait_list_signup_id = ? " +
                        "ORDER BY pos";

          pstmt = con.prepareStatement (sql);
          pstmt.clearParameters();
          pstmt.setString(1, date_mysql);
          //pstmt.setInt(2, local_time);
          pstmt.setString(2, name);

          tmp_i = 3;
          if (!course.equals( "-ALL-" )) {
              pstmt.setString(3, course);
              tmp_i++;
          }

          if (index == 0) pstmt.setInt(tmp_i, local_time);

          rs = pstmt.executeQuery();


          while (rs.next()) {

              errMsg = "Reading Wait List";
              tmp_found = true;
              sum_players = 0;
              nineHole = 0;
              eighteenHole = 0;

              // we're now setting these above just like lotts and events'
              //window_stime = rs.getInt("window_stime");
              //window_etime = rs.getInt("window_etime");

              wait_list_signup_id = rs.getInt("wait_list_signup_id");
              //courseName = rs.getString("courseName");
              req_time = SystemUtils.getSimpleTime(rs.getInt("ok_stime")) + " - " + SystemUtils.getSimpleTime(rs.getInt("ok_etime"));
              in_use = (rs.getString("in_use_by").equals("")) ? 0 : 1;
              notes = rs.getString("notes").trim();

              out.print("<div id=wait_slot_"+ dts_slot_index +" time=\"" + req_time + "\" course=\"" + courseName + "\" startX=0 startY=0 wlsId="+wait_list_signup_id+" ");
              if (in_use == 0) {
                // not in use
                out.println("class=waitSlot drag=true style=\"background-color: "+ bgcolor +"\" bgc=\""+ bgcolor +"\">");
              } else {
                // in use
                out.println("class=timeSlotInUse>");
              }
              //out.println("class=\"" + ((in_use == 0) ? "timeSlot" : "timeSlotInUse") + "\" drag=true style=\"background-color: "+ bgcolor +"\" bgc=\""+ bgcolor +"\">");

              // col for 'insert' and 'delete' requests
              out.print("<span id=wait_slot_" + dts_slot_index + "_A class=cellDataB style=\"cursor: default; left: " + ucol_start[1] + "px; width: " + ucol_width[1] + "px; background-color: #FFFFFF\">");
              j++;                                           // increment the jump label index (where to jump on page)
              out.print("<a name=\"jump" + j + "\"></a>");     // create a jump label for returns


              if (in_use == 0) {

                  // not in use (use signupId instead of wlsId for waitlist_slot)
                  out.print("<a href=\"/" +rev+ "/servlet/Proshop_waitlist_slot?index=" +index+ "&signupId=" +wait_list_signup_id+ "&returnCourse=" +course+ "&email=" +emailOpt+ "&name=" +name+ "\" title=\"Edit signup\" alt=\"Edit signup\">");
                  out.print("<img src=/" +rev+ "/images/dts_edit.gif width=13 height=13 border=0></a>");
                  out.print("<img src=/" +rev+ "/images/shim.gif width=5 height=1 border=0>");
                  out.print("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?mode=" +mode+ "&index=" +index+ "&wlsId=" +wait_list_signup_id+ "&returnCourse=" +course+ "&email=" +emailOpt+ "&name=" +name+ "&hide=" +hideUnavail+ "&delete=yes\" title=\"Delete signup\" alt=\"Remove signup\" onclick=\"return confirm('Are you sure you want to permanently delete this signup?');\">");
                  out.print("<img src=/" +rev+ "/images/dts_trash.gif width=13 height=13 border=0></a>");

              } else {

                  // in use
                  out.print("<img src=/" +rev+ "/images/busy.gif width=32 height=13 border=0 alt=\"Busy\" title=\"Busy\">");

              }
              out.println("</span>");

              //
              // Position on wait list
              //
              out.print("<span id=wait_slot_" + dts_slot_index + "_time class=cellData hollow=true style=\"left: " + ucol_start[2] + "px; width: " + ucol_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
              out.print(dts_slot_index + 1);
              out.print("</span>");

              //
              // Available Time Window
              //
              if (in_use == 0) {
                out.print("<span id=wait_slot_" + dts_slot_index + "_time class=cellData hollow=true style=\"left: " + ucol_start[3] + "px; width: " + ucol_width[3] + "px; background-color: " +dts_defaultF3Color+ "\">");
              } else {
                out.print("<span id=wait_slot_" + dts_slot_index + "_time class=cellData style=\"cursor: default; left: " + ucol_start[3] + "px; width: " + ucol_width[3] + "px; background-color: " +dts_defaultF3Color+ "\">");
              }
              out.print(req_time);
              out.print("</span>");

            /*        
              //
              // Course
              //
              if (course.equals( "-ALL-" )) { // only display this col if multi course club

                  for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                      if (courseName.equals(courseA[tmp_i])) break;
                  }
                  out.print("<span id=wait_slot_" + dts_slot_index + "_course class=cellDataC style=\"cursor: default; left: " + ucol_start[3] + "px; width: " + ucol_width[3] + "px; background-color:" + course_color[tmp_i] + "\">");
                  if (!courseName.equals("")) { out.print(fitName(courseName)); }
                  out.print("</span>");
              }
             */
              errMsg = "Reading Wait List Players";
              //
              //  Display members
              //
              pstmt2 = con.prepareStatement (sql2);
              pstmt2.clearParameters();
              pstmt2.setInt(1, wait_list_signup_id);
              rs2 = pstmt2.executeQuery();

              out.print("<span id=wait_slot_" + dts_slot_index + "_members class=cellDataB value=\"\" style=\"cursor: default; left: " + ucol_start[6] + "px; width: " + ucol_width[6] + "px; text-align: left\">");

              tmp_found2 = false;

              while (rs2.next()) {

                  fullName = rs2.getString("player_name");
                  cw = rs2.getString("cw");
                  if (rs2.getInt("9hole") == 1) nineHole = 1;
                  if (rs2.getInt("9hole") == 0) eighteenHole = 1;

                  if (tmp_found2) out.print(",&nbsp; "); else out.print("&nbsp;");
                  out.print(fullName + " <font style=\"font-size:8px\">(" + cw + ")</font>");
                  tmp_found2 = true;
                  sum_players++;
              }

              pstmt2.close();

              if (!notes.equals("")) {

                  // this may not work well in rl but is meant to show a dynamic popup, or we can spawn a small popup window that will show the notes
                  out.println("&nbsp; &nbsp; <img src=\"/"+rev+"/images/notes.gif\" width=10 height=12 border=0 alt=\""+notes+"\">");
              }

              out.print("</span>");

              out.print("<span class=cellDataB style=\"cursor: default; left: " + ucol_start[7] + "px; width: " + ucol_width[7] + "px\">");
              out.print(sum_players);
              out.println("</span>");

              out.print("<span class=cellDataB style=\"cursor: default; left: " + ucol_start[8] + "px; width: " + ucol_width[8] + "px\">");
              if (nineHole == 1 && eighteenHole == 1) {
                  out.print("mixed");
              } else if (nineHole == 1) {
                  out.print("9");
              } else if (eighteenHole == 1) {
                  out.print("18");
              }
              out.println("</span>");

              out.println("</div>");
              dts_slot_index++;
          }

          pstmt.close();

          out.println("</div>"); // end container

          out.println("\n<!-- END OF WAITLIST SHEET BODY -->");

          out.println("<p>&nbsp;</p>");

          total_unassigned_slots = dts_slot_index;
          dts_slot_index = 0;
          in_use = 0;          

       } else if (mode.equals("LOTT")) { 

          out.println("\n<!-- START OF LOTTERY REQ SHEET HEADER -->");
          out.println(""); //  width=" + total_col_width + " align=center
          out.println("<div id=\"elHContainer2\">");
          out.println("<span class=header style=\"left: " +ucol_start[1]+ "px; width: " +ucol_width[1]+ "px\">m/-</span><span");
          out.println(" class=header style=\"left: " +ucol_start[2]+ "px; width: " +ucol_width[2]+ "px\">Assigned</span><span");
          out.println(" class=header style=\"left: " +ucol_start[3]+ "px; width: " +ucol_width[3]+ "px\">Desired</span><span");
          out.println(" class=header style=\"left: " +ucol_start[4]+ "px; width: " +ucol_width[4]+ "px\">Acceptable</span><span");
   //       if (course.equals( "-ALL-" )) {
          if (multi_lot == 1) {              // if multi courses
              out.println(" class=header style=\"left: " +ucol_start[5]+ "px; width: " +ucol_width[5]+ "px\">Assigned</span><span ");
              out.println(" class=header style=\"left: " +ucol_start[6]+ "px; width: " +ucol_width[6]+ "px\">Requested</span><span ");
          }
          out.println(" class=header style=\"left: " +ucol_start[7]+ "px; width: " +ucol_width[7]+ "px\">W</span><span id=widthMarker2 ");
          out.println(" class=header style=\"left: " +ucol_start[8]+ "px; width: " +ucol_width[8]+ "px\">Members</span>");
          out.print("</div>\n");
          out.println("<!-- END OF LOTTERY REQ HEADER -->\n");


          out.println("\n<!-- START OF LOTTERY REQ SHEET BODY -->");
          out.println("<div id=\"elContainer2\">");

          errMsg = "Reading Lottery Requests";

          out.println("<!-- name=" + name + " -->");
          out.println("<!-- date=" + date + " -->");

          int lottcount = 0;             // # of lottery requests

          pstmt = con.prepareStatement ("" +
                  "SELECT c.fives, l.* " +
                  "FROM lreqs3 l, clubparm2 c " +
                  "WHERE state <> 4 AND name = ? AND date = ? AND c.courseName = l.courseName " + 
                  (course.equals("-ALL-") ? "" : " AND l.courseName = ? ") + " " +
                  "ORDER BY atime1, time;");

          // added state <> 4     

          pstmt.clearParameters();
          pstmt.setString(1, name);
          pstmt.setLong(2, date);
          if (!course.equals("-ALL-")) pstmt.setString(3, course);

          rs = pstmt.executeQuery();

          while (rs.next()) {

              lottcount++;          // bump count of lreqs found

              tmp_found = true;
              sum_players = 0;
              nineHole = 0;
              eighteenHole = 0;
              in_use_by = "";

              lottery_id = rs.getInt("id");
              courseName = rs.getString("courseName");
              //req_time = rs.getInt("time");
              notes = rs.getString("notes").trim();
              groups = rs.getInt("groups");
              in_use = rs.getInt("in_use");
              max_players = ((rs.getInt("fives") == 0 || rs.getString("p5").equalsIgnoreCase("No")) ? 4 : 5);
              if (in_use == 1) in_use_by = rs.getString("in_use_by");
              reqCourse = rs.getString("courseReq");

              j++; // increment the jump label index (where to jump on page)

              // set tmp_i to the appropriate course color index
              if (course.equals( "-ALL-" )) { // only display this col if multi course club

                  for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                      if (courseName.equals(courseA.get(tmp_i))) break;
                  }
              }

              if (tmp_i >= colorMax) tmp_i = (colorMax - 1);      // use White if # of courses exceeds # of colors defined

              tmp_groups = 1;  // reset
              while (tmp_groups <= groups) {

                  if (request_color > 20) request_color = 0;
                  groupColor = (blnGroupColor) ? "#A2CD5A" : "#6E8B3D";
                  buildRow(dts_slot_index, tmp_groups, course, colors.course_color[tmp_i], bgcolor, max_players, courseCount, ucol_start, ucol_width, index, emailOpt, j, groupColor, (tmp_groups > 1), hideUnavail, name, rs, out, multi_lot); // course_color[request_color]
                  tmp_groups++;
                  dts_slot_index++;

              } // end while

              blnGroupColor = blnGroupColor == false;
              request_color++;

          } // end rs loop of lottery requests

          pstmt.close();

          out.println("</div>"); // end container
          out.println("\n<!-- END OF LOTTERY REQ SHEET BODY -->");
          out.println("<p>&nbsp;</p>");

          total_unassigned_slots = dts_slot_index;
          dts_slot_index = 0;
          in_use = 0;

          //
          // End display Lottery Reqs - see if any lreqs were found for this course and lottery
          //
          /*     NOTE: This is probably not necessary and it hits after a pro moves the last request to the tee sheet (we don't want that).
           *           This was intended to clean up the tee sheet if a time still had the Approve button but there are no lreqs to approve.
           *           The code added to sendLotteyerEmails that removes the lottery name from the tee times should take care of this.
           * 
          if (lottcount == 0) {    // if none found

               out.println("<center>");
               out.println("<h2>No Lottery Requests Found</h2>");
               out.println("<p>There are no lottery requests to process for lottery: <b>" +name+ "</b>");
               if (!course.equals("")) {
                  out.println(" on course: <b>" +course+ "</b>");
               }                         
               out.println("</p><p>Would you like to send any outstanding email notifications and release the unused tee times from the lottery?</p>");
               out.println("<form>");            
               out.println("<input type=hidden name=doEmails value=\"1\">");
               out.println("<input type=hidden name=mode value=\"" + mode + "\">");
               out.println("<input type=hidden name=name value=\"" + name + "\">");
               out.println("<input type=hidden name=date value=\"" + date + "\">");
               out.println("<input type=hidden name=index value=\"" + index + "\">");
               out.println("<input type=hidden name=course value=\"" + course + "\">");
               out.println("<input type=hidden name=returnCourse value=\"" + returnCourse + "\">");

               out.println("<input type=submit value=\"YES\">");
               out.println("</form>");

               out.println("<form method=get action=/" + rev + "/servlet/Proshop_jump>");
               out.println("<input type=hidden name=index value=\"" + index + "\">");
               if (!returnCourse.equals("")) {
                  out.println("<input type=hidden name=course value=\"" + returnCourse + "\">");
               } else {
                  out.println("<input type=hidden name=course value=\"" + course + "\">");
               }
               out.println("<input type=submit value=\"NO - Just Return\">");            
               out.println("</form>");
               out.println("</center>");       
               out.println("<p>&nbsp;</p>");
               out.println("</body>\n</html>");
               out.close();
               return;              // done, no lreqs to approve
          }    
           */
          //
          //  End of Lottery
          //

       } else if (mode.equals("EVENT")) { 

          out.println("\n<!-- START OF EVENT REGISTRATION SHEET HEADER -->");
          out.println(""); //  width=" + total_col_width + " align=center
          out.println("<div id=\"elHContainer2\">");
      /*    
          out.println("<span class=header style=\"left: " +ucol_start[1]+ "px; width: " +ucol_width[1]+ "px\">Submitted</span><span");
          if (course.equals( "-ALL-" )) {
              out.println(" class=header style=\"left: " +ucol_start[2]+ "px; width: " +ucol_width[2]+ "px\">Course</span><span ");
          }
      */    
          if (group_size > 1) {

              out.println("<span");
              out.println(" class=header style=\"left: " +ucol_start[1]+ "px; width: " +ucol_width[1]+ "px\">Player 1</span><span");
              out.println(" class=header style=\"left: " +ucol_start[2]+ "px; width: " +ucol_width[2]+ "px\">Player 2</span><span");
              out.println(" class=header style=\"left: " +ucol_start[3]+ "px; width: " +ucol_width[3]+ "px\">Player 3</span><span"); 
              out.println(" class=header style=\"left: " +ucol_start[4]+ "px; width: " +ucol_width[4]+ "px\">Player 4</span><span");

              if (fivesALL != 0) {
                  out.println(" class=header style=\"left: " +ucol_start[5]+ "px; width: " +ucol_width[5]+ "px\">Player 5</span><span");
              }

              out.println(" class=header style=\"left: " +ucol_start[6]+ "px; width: " +ucol_width[6]+ "px\" id=widthMarker2>N</span>");

          } else {

              out.println("<span class=header style=\"left: " +ucol_start[1]+ "px; width: " +ucol_width[1]+ "px\" id=widthMarker2>Player</span>");
          }

          out.print("</div>\n");
          out.println("<!-- END OF EVENT REQ HEADER -->\n");


          out.println("\n<!-- START OF EVENT REQ SHEET BODY -->");
          out.println("<div id=\"elContainer2\">");

          errMsg = "Reading Event Signups";

          out.println("<!-- name=" + name + " -->");
          out.println("<!-- date=" + date + " -->");

          /*
          pstmt = con.prepareStatement ("" +
                  "SELECT c.fives, e.* , DATE_FORMAT(e.r_date, '%b %D') AS d " +
                  "FROM evntsup2b e, clubparm2 c " +
                  "WHERE e.moved = 0 AND e.name = ? AND c.courseName = e.courseName " + 
                  (course.equals("-ALL-") ? "" : " AND e.courseName = ? ") + " " +
                  "ORDER BY r_date ASC, r_time ASC;");
          */

          pstmt = con.prepareStatement ("" +
                  "SELECT *, DATE_FORMAT(r_date, '%b %D') AS d " +
                  "FROM evntsup2b " +
                  "WHERE moved = 0 AND name = ? AND player1 <> '' AND inactive = 0 " + 
                  (course.equals("-ALL-") ? "" : " AND courseName = ? ") + " " +
                  "ORDER BY r_date ASC, r_time ASC" + 
                  (limit > 0 ? " LIMIT " + limit : ";"));

          pstmt.clearParameters();
          pstmt.setString(1, name);
          //pstmt.setLong(2, date);
          if (!course.equals("-ALL-")) pstmt.setString(2, course);

          rs = pstmt.executeQuery();

          String drag = "drag=true ";
          String cursor = "";
          if (group_size > 1) {
              drag = "";
              cursor = "cursor: default; ";
          }

          while (rs.next()) {

              tmp_found = true;
              sum_players = 0;
              nineHole = 0;
              eighteenHole = 0;
              in_use_by = "";
              wait = 0;

              event_id = rs.getInt("id");
              courseName = rs.getString("courseName");
              player1 = rs.getString("player1");
              player2 = rs.getString("player2");
              player3 = rs.getString("player3");
              player4 = rs.getString("player4");
              player5 = rs.getString("player5");
              in_use = rs.getInt("in_use");
              wait = rs.getInt("wait");
              if (in_use == 1) in_use_by = rs.getString("in_use_by");

              int tmp_time = rs.getInt("r_time");
              int tmp_hr = tmp_time / 100;
              int tmp_min = tmp_time - (tmp_hr * 100);
              int tmp_date = rs.getInt("r_date");

              // only show this entry if there is a player in player1 position
              if (!player1.equals("")) {

                  j++; // increment the jump label index (where to jump on page)

                  bgcolor = "#FFFFFF";  // default
                  if (wait != 0) bgcolor = "yellow";

                  //
                  // Start Row
                  //
                  out.print("<div id=event_slot_"+ dts_slot_index +" time=\"0\" course=\"" + rs.getString("courseName") + "\" startX=0 startY=0 eventId=\"" + rs.getInt("id") + "\" ");
                  if (in_use == 0) {
                      // not in use
                      out.println("class=eventSlot drag=true style=\"background-color: "+ bgcolor +"\" bgc=\""+ bgcolor +"\">");
                  } else {
                      // in use
                      out.println("class=timeSlotInUse>");
                  }


                  //
                  //  Add Player 1
                  //
                  out.print("<span id=event_slot_" + dts_slot_index + "" + ((group_size > 1) ? "_player_1 " : "_player") + " hollow=true class=cellData " + drag + "startX="+ucol_start[1]+" playerSlot=1 style=\"" + cursor + "left: " + ucol_start[1] + "px; width: " + ucol_width[1] + "px\">");
                  if (!player1.equals("")) { out.print(fitName(player1)); }
                  out.println("</span>");


                  if (group_size > 1) {

                      //
                      //  Add Player 2
                      //
                      out.print("<span id=event_slot_" + dts_slot_index + "_player_2 class=cellData " + drag + "startX="+ucol_start[2]+" playerSlot=2 style=\"" + cursor + "left: " + ucol_start[2] + "px; width: " + ucol_width[2] + "px\">");
                      if (!player2.equals("")) { out.print(fitName(player2)); }
                      out.println(" </span>");


                      //
                      //  Add Player 3
                      //
                      out.print("<span id=event_slot_" + dts_slot_index + "_player_3 class=cellData " + drag + "startX="+ucol_start[3]+" playerSlot=3 style=\"" + cursor + "left: " + ucol_start[3] + "px; width: " + ucol_width[3] + "px\">");
                      if (!player3.equals("")) { out.print(fitName(player3)); }
                      out.println("</span>");


                      //
                      //  Add Player 4
                      //
                      out.print("<span id=event_slot_" + dts_slot_index + "_player_4 class=cellData " + drag + "startX="+ucol_start[4]+" playerSlot=4 style=\"" + cursor + "left: " + ucol_start[4] + "px; width: " + ucol_width[4] + "px\">");
                      if (!player4.equals("")) { out.print(fitName(player4)); }
                      out.println("</span>");


                      //
                      //  Add Player 5 if supported
                      //
                      if (fivesALL != 0) {        // if 5-somes on any course
                         if (fives != 0) {        // if 5-somes on this course

                           out.print("<span id=event_slot_" + dts_slot_index + "_player_5 class=cellData " + drag + "startX="+ucol_start[5]+" playerSlot=5 style=\"" + cursor + "left: " + ucol_start[5] + "px; width: " + ucol_width[5] + "px\">");
                           if (!player5.equals("")) { out.print(fitName(player5)); }
                           out.println("</span>");

                         } else {       // 5-somes on at least 1 course, but not this one

                           out.print("<span id=event_slot_" + dts_slot_index + "_player_5 class=cellData " + drag + "startX="+ucol_start[5]+" playerSlot=5 style=\"" + cursor + "left: " + ucol_start[5] + "px; width: " + ucol_width[5] + "px;  background-image: url('/v5/images/shade1.gif')\">");
                           out.println("</span>");

                         } // end if fives
                      } // end if fivesALL

                  } // end players 2-5


                  //
                  // Notes
                  //
                  out.print("<span id=event_slot_" + dts_slot_index + "_notes class=cellDataC drag=false style=\"cursor: default; background-color: white; left: " + ucol_start[6] + "px; width: " + ucol_width[6] + "px\">");
                  //out.print(notes);
                  if (!rs.getString("notes").equals("")) out.println("<img src=\"/"+rev+"/images/notes.gif\" width=10 height=12 border=0 alt=\"" + rs.getString("notes") + "\">");
                  out.println("</span>");

                  dts_slot_index++;
                  blnGroupColor = blnGroupColor == false;
                  request_color++;

                  // end row
                  out.println("</div>"); 

              } // end if player1 empty

          } // end rs loop of event signups

          pstmt.close();

          out.println("</div>"); // end container
          out.println("\n<!-- END OF EVENT REQ SHEET BODY -->");
          out.println("<p>&nbsp;</p>");

          total_unassigned_slots = dts_slot_index;
          dts_slot_index = 0;
          in_use = 0;
          //
          // End display event signups
          //

       } else {   // if (IS_TLT) {

          out.println("\n<!-- START OF NOTIFICATIONS SHEET HEADER -->");
          out.println(""); //  width=" + total_col_width + " align=center
          out.println("<div id=\"elHContainer2\">");
          out.println("<span class=header style=\"left: " +ucol_start[1]+ "px; width: " +ucol_width[1]+ "px\">e/-</span><span");
          out.println(" class=header style=\"left: " +ucol_start[2]+ "px; width: " +ucol_width[2]+ "px\">Time</span><span");
          if (course.equals( "-ALL-" )) {
              out.println(" class=header style=\"left: " +ucol_start[3]+ "px; width: " +ucol_width[3]+ "px\">Course</span><span ");
          }
          //out.println(" class=header style=\"left: " +ucol_start[3]+ "px; width: " +ucol_width[3]+ "px\">F/B</span><span");
          out.println(" class=header style=\"left: " +ucol_start[5]+ "px; width: " +ucol_width[5]+ "px\">Members</span><span");
          out.println(" class=header style=\"left: " +ucol_start[6]+ "px; width: " +ucol_width[6]+ "px\">Players</span><span id=widthMarker2 ");
          out.println(" class=header style=\"left: " +ucol_start[7]+ "px; width: " +ucol_width[7]+ "px\">Holes</span>");
          out.print("</div>\n");
          out.println("<!-- END OF NOTIFCATION HEADER -->\n");


          out.println("\n<!-- START OF NOTIFCATION SHEET BODY -->");
          out.println("<div id=\"elContainer2\">");

          errMsg = "Reading Notifications";
          String sql = "SELECT n.*, c.courseName, DATE_FORMAT(n.req_datetime, '%l:%i %p') AS req_time " +
                       "FROM notifications n, clubparm2 c " +
                       "WHERE DATE(n.req_datetime) = ? AND " +
                       "converted = 0 " +
                       ((!course.equals( "-ALL-" )) ? " AND courseName = ? " : "") +
                       "AND c.clubparm_id = n.course_id " + 
                       "ORDER BY n.req_datetime, c.courseName";

          String sql2 = "SELECT * " +
                        "FROM notifications_players " +
                        "WHERE notification_id = ? " +
                        "ORDER BY pos";

          pstmt = con.prepareStatement (sql);

          pstmt.clearParameters();
          pstmt.setString(1, date_mysql);

          if (!course.equals( "-ALL-" )) {
              pstmt.setString(2, course);
          }

          rs = pstmt.executeQuery();


          while (rs.next()) {

              tmp_found = true;
              sum_players = 0;
              nineHole = 0;
              eighteenHole = 0;

              notification_id = rs.getInt("notification_id");
              courseName = rs.getString("courseName");
              req_time = rs.getString("req_time");
              in_use = (rs.getString("in_use_by").equals("")) ? 0 : 1;
              notes = rs.getString("notes").trim();

              out.print("<div id=notify_slot_"+ dts_slot_index +" time=\"" + req_time + "\" course=\"" + courseName + "\" startX=0 startY=0 notifyId="+notification_id+" ");
              if (in_use == 0) {
                // not in use
                out.println("class=notifySlot drag=true style=\"background-color: "+ bgcolor +"\" bgc=\""+ bgcolor +"\">");
              } else {
                // in use
                out.println("class=timeSlotInUse>");
              }
              //out.println("class=\"" + ((in_use == 0) ? "timeSlot" : "timeSlotInUse") + "\" drag=true style=\"background-color: "+ bgcolor +"\" bgc=\""+ bgcolor +"\">");

              // col for 'insert' and 'delete' requests
              out.print("<span id=time_slot_" + dts_slot_index + "_A class=cellDataB style=\"cursor: default; left: " + ucol_start[1] + "px; width: " + ucol_width[1] + "px; background-color: #FFFFFF\">");
              j++;                                           // increment the jump label index (where to jump on page)
              out.print("<a name=\"jump" + j + "\"></a>");     // create a jump label for returns


              if (in_use == 0) {

                  // not in use
                  out.print("<a href=\"/" +rev+ "/servlet/ProshopTLT_slot?index=" +index+ "&notifyId=" +notification_id+ "&returnCourse=" +course+ "&email=" +emailOpt+ "\" title=\"Edit notification\" alt=\"Edit notification\">");
                  out.print("<img src=/" +rev+ "/images/dts_edit.gif width=13 height=13 border=0></a>");
                  out.print("<img src=/" +rev+ "/images/shim.gif width=5 height=1 border=0>");
                  out.print("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?mode=" +mode+ "&index=" +index+ "&notifyId=" +notification_id+ "&returnCourse=" +course+ "&email=" +emailOpt+ "&delete=yes\" title=\"Delete notification\" alt=\"Remove notification\" onclick=\"return confirm('Are you sure you want to permanently delete this notification?');\">");
                  out.print("<img src=/" +rev+ "/images/dts_trash.gif width=13 height=13 border=0></a>");

              } else {

                  // in use
                  out.print("<img src=/" +rev+ "/images/busy.gif width=32 height=13 border=0 alt=\"Busy\" title=\"Busy\">");

              }
              out.println("</span>");

              //
              // Requested Time
              //
              if (in_use == 0) {
                out.print("<span id=notify_slot_" + dts_slot_index + "_time class=cellData hollow=true style=\"left: " + ucol_start[2] + "px; width: " + ucol_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
              } else {
                out.print("<span id=notify_slot_" + dts_slot_index + "_time class=cellData style=\"cursor: default; left: " + ucol_start[2] + "px; width: " + ucol_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
              }
              //out.print("<span id=notify_slot_" + dts_slot_index + "_time class=cellData hollow=true style=\"left: " + ucol_start[2] + "px; width: " + ucol_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
              out.print(req_time);
              out.print("</span>");

              //
              // Course
              //
              if (course.equals( "-ALL-" )) { // only display this col if multi course club

                  for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                      if (courseName.equals(courseA.get(tmp_i))) break;
                  }
                  if (tmp_i >= colorMax) tmp_i = (colorMax - 1);          // use White if courses exceeds max colors defined
                  out.print("<span id=notify_slot_" + dts_slot_index + "_course class=cellDataC style=\"cursor: default; left: " + ucol_start[3] + "px; width: " + ucol_width[3] + "px; background-color:" + colors.course_color[tmp_i] + "\">");
                  if (!courseName.equals("")) { out.print(fitName(courseName)); }
                  out.print("</span>");
              }

              //
              //  Display members
              //
              pstmt2 = con.prepareStatement (sql2);
              pstmt2.clearParameters();
              pstmt2.setInt(1, notification_id);
              rs2 = pstmt2.executeQuery();

              out.print("<span id=notify_slot_" + dts_slot_index + "_members class=cellDataB value=\"\" style=\"cursor: default; left: " + ucol_start[5] + "px; width: " + ucol_width[5] + "px; text-align: left\">");

              tmp_found2 = false;

              while (rs2.next()) {

                  fullName = rs2.getString("player_name");
                  cw = rs2.getString("cw");
                  if (rs2.getInt("9hole") == 1) nineHole = 1;
                  if (rs2.getInt("9hole") == 0) eighteenHole = 1;

                  if (tmp_found2) out.print(",&nbsp; "); else out.print("&nbsp;");
                  out.print(fullName + " <font style=\"font-size:8px\">(" + cw + ")</font>");
                  tmp_found2 = true;
                  sum_players++;
              }

              pstmt2.close();

              if (!notes.equals("")) {

                  // this won't work in rl but is meant to show a dynamic popup, or we can spawn a small popup window that will show the notes
                  out.println("&nbsp; &nbsp; <img src=\"/"+rev+"/images/notes.gif\" width=10 height=12 border=0 alt=\""+notes+"\">");
              }

              out.print("</span>");

              out.print("<span class=cellDataB style=\"cursor: default; left: " + ucol_start[6] + "px; width: " + ucol_width[6] + "px\">");
              out.print(sum_players);
              out.println("</span>");

              out.print("<span class=cellDataB style=\"cursor: default; left: " + ucol_start[7] + "px; width: " + ucol_width[7] + "px\">");
              //out.print(((nineHole == 0) ? "18" : "9"));
              if (nineHole == 1 && eighteenHole == 1) {
                  out.print("mixed");
              } else if (nineHole == 1) {
                  out.print("9");
              } else if (eighteenHole == 1) {
                  out.print("18");
              }
              out.println("</span>");

              out.println("</div>");
              dts_slot_index++;
          }

          pstmt.close();

          out.println("</div>"); // end container

          out.println("\n<!-- END OF NOTIFCATION SHEET BODY -->");

          out.println("<p>&nbsp;</p>");

          total_unassigned_slots = dts_slot_index;
          dts_slot_index = 0;
          in_use = 0;
          //
          // End display notifications
          //
       } // end if mode


       out.println("<div id=tblLegend style=\"position:absolute\"><p align=center><font size=\"2\">");
       out.println("<b>" + ((index < 0) ? "Old " : "") + "Tee Sheet Legend</b>");
       out.println("</font><br><font size=\"1\">");
       out.println("<b>F/B:</b>&nbsp;&nbsp;&nbsp;&nbsp;F = Front Nine,&nbsp;&nbsp;&nbsp;B = Back Nine,&nbsp;&nbsp;&nbsp;");
       out.println("O = Open (for cross-overs),&nbsp;&nbsp;&nbsp;S = Shotgun Event");
       out.println("</font></p></div>");


       //****************************************************************************
       //
       // start html for tee sheet
       //
       //****************************************************************************
       //
       //  To change the position of the tee sheet (static position from top):
       //
       //   Edit file 'dts-styles.css'
       //                     "top" property for elHContainer (header for the main container)
       //                     "top" property for elContainer (main container that holds the tee sheet elements)
       //                     Increment both numbers equally!!!!!!!!!!!!
       //
       //****************************************************************************

       String tmpCW = "C/W";
       out.println("<br>");
       out.println("\n<!-- START OF TEE SHEET HEADER -->");
       out.println("<div id=\"elHContainer\">"); //  width=" + total_col_width + " align=center
       out.println("<span class=header style=\"left: " +col_start[1]+ "px; width: " +col_width[1]+ "px\">+/-</span><span");
       out.println(" class=header style=\"left: " +col_start[2]+ "px; width: " +col_width[2]+ "px\">Time</span><span");
       if (course.equals( "-ALL-" )) {
          out.println(" class=header style=\"left: " +col_start[3]+ "px; width: " +col_width[3]+ "px\">Course</span><span ");
       }
       out.println(" class=header style=\"left: " +col_start[4]+ "px; width: " +col_width[4]+ "px\">F/B</span><span ");
       out.println(" class=header style=\"left: " +col_start[5]+ "px; width: " +col_width[5]+ "px\">Player 1</span><span ");
       out.println(" class=header style=\"left: " +col_start[6]+ "px; width: " +col_width[6]+ "px\">" +tmpCW+ "</span><span ");
       out.println(" class=header style=\"left: " +col_start[7]+ "px; width: " +col_width[7]+ "px\">Player 2</span><span ");
       out.println(" class=header style=\"left: " +col_start[8]+ "px; width: " +col_width[8]+ "px\">" +tmpCW+ "</span><span ");
       out.println(" class=header style=\"left: " +col_start[9]+ "px; width: " +col_width[9]+ "px\">Player 3</span><span ");
       out.println(" class=header style=\"left: " +col_start[10]+ "px; width: " +col_width[10]+ "px\">" +tmpCW+ "</span><span ");
       out.println(" class=header style=\"left: " +col_start[11]+ "px; width: " +col_width[11]+ "px\">Player 4</span><span ");
       out.print(" class=header style=\"left: " +col_start[12]+ "px; width: " +col_width[12]+ "px\"");

       if (fivesALL == 0)
       {
          out.print(" id=widthMarker>" +tmpCW+"</span>");
       } else {
          out.print(">" +tmpCW+ "</span><span \n class=header style=\"left: " +col_start[13]+ "px; width: " +col_width[13]+ "px\">Player 5</span><span ");
          out.print(" \n class=header style=\"left: " +col_start[14]+ "px; width: " +col_width[14]+ "px\" id=widthMarker>" +tmpCW+ "</span>");
       }

       out.print("</div>\n");
       out.println("<!-- END OF TEE SHEET HEADER -->\n");

       String first = "yes";

       errMsg = "Get tee times.";

       //String table_name = (index < 0) ? "teepast2" : "teecurr2";

       //out.println("<!-- table_name=" + table_name + " | date=" + date);

       //
       //  Get the tee sheet for this date
       //
       String stringTee = "";

       boolean use_time_window = false;

       if (index < 0) {

           stringTee = "SELECT * FROM ( " +
                       "SELECT teepast_id, time, fb, courseName, " +
                           "player1, p1cw, show1, p91, " +
                           "player2, p2cw, show2, p92, " +
                           "player3, p3cw, show3, p93, " +
                           "player4, p4cw, show4, p94, " +
                           "player5, p5cw, show5, p95 " +
                       "FROM teepast2 tp WHERE date = ? " +
                       ((course.equals( "-ALL-" )) ? "" : "AND courseName = ? ") + 
                       "UNION ALL " +
                       "SELECT NULL AS teepast_id, time, fb, courseName, " +
                           "\"\" AS player1, \"\" AS p1cw, 0 AS show1, 0 AS p91, " +
                           "\"\" AS player2, \"\" AS p2cw, 0 AS show2, 0 AS p92, " +
                           "\"\" AS player3, \"\" AS p3cw, 0 AS show3, 0 AS p93, " +
                           "\"\" AS player4, \"\" AS p4cw, 0 AS show4, 0 AS p94, " +
                           "\"\" AS player5, \"\" AS p5cw, 0 AS show5, 0 AS p95 " +
                       "FROM teepastempty te WHERE date = ? " +
                       ((course.equals( "-ALL-" )) ? "" : "AND courseName = ? ") + 
                       ") AS t1 " + 
                       "ORDER BY time, courseName, fb";

       } else {

           if (club.equals( "edisonclub" )) {

               stringTee = "SELECT t.* " +
                           "FROM teecurr2 t, clubparm2 c " + 
                           "WHERE t.date = ? " +
                           ((course.equals( "-ALL-" )) ? "" : "AND t.courseName = ? ") + 
                           "AND t.courseName = c.courseName " + 
                           ((hideUnavail.equals("1") && index == 0) ? "AND t.time > ? " : "") + 
                           "ORDER BY t.time, c.clubparm_id, t.fb";

           } else {

               stringTee = "SELECT * " + 
                           "FROM teecurr2 " +
                           "WHERE date = ? " +
                           ((course.equals( "-ALL-" )) ? "" : "AND courseName = ? ");

               // if hide has been choosen then we'll either only display the time frame for the
               // wait list, event or lottery or if none of those modes then if today only show upcoming times
               if (hideUnavail.equals("1")) {

                   if (mode.equals("WAITLIST") || mode.equals("LOTT") || mode.equals("EVENT")) {

                       use_time_window = true;
                       stringTee += "AND time >= ? AND time <= ? ";

                   } else if (index == 0) {

                       // default to notification mode or normal mode so if today
                       // then hide all the past times and only show upcoming times
                       stringTee += "AND time > ? ";
                       use_time_window = false;

                   }

               }

               stringTee += "ORDER BY time, courseName, fb";
           }
       }

       out.println("<!-- use_time_window=" + use_time_window + ", sql=" + stringTee + " -->");
       out.println("<!-- date=" + date + ", course=" + course + ", cal_time=" + cal_time + ", window_stime=" + window_stime + ", window_etime=" + window_etime + " -->");

       pstmt = con.prepareStatement (stringTee);

       int parm_index = 2;
       int teepast_id = 0;

       pstmt.clearParameters();

       pstmt.setLong(1, date);

       if (!course.equals( "-ALL-" )) {
           pstmt.setString(2, course);
           parm_index = 3;
       }

       if (index < 0) {

           pstmt.setLong(parm_index, date); parm_index++; 
           if (!course.equals( "-ALL-" )) { pstmt.setString(parm_index, course); parm_index++; }

       } else if (hideUnavail.equals("1") && !use_time_window && index == 0) {

           pstmt.setInt(parm_index, cal_time); parm_index++; 

       } else if (hideUnavail.equals("1") && use_time_window) {

           pstmt.setInt(parm_index, window_stime); parm_index++; 
           pstmt.setInt(parm_index, window_etime);

       }

       rs = pstmt.executeQuery();

       out.println("\n<!-- START OF TEE SHEET BODY -->");
       out.println("<div id=\"elContainer\">\n");

       // loop thru each of the tee times
       while (rs.next()) {

           player1 = rs.getString("player1");
           player2 = rs.getString("player2");
           player3 = rs.getString("player3");
           player4 = rs.getString("player4");
           player5 = rs.getString("player5");
           p1cw = rs.getString("p1cw");
           p2cw = rs.getString("p2cw");
           p3cw = rs.getString("p3cw");
           p4cw = rs.getString("p4cw");
           p5cw = rs.getString("p5cw");
           p91 = rs.getInt("p91");
           p92 = rs.getInt("p92");
           p93 = rs.getInt("p93");
           p94 = rs.getInt("p94");
           p95 = rs.getInt("p95");
           guest_id1 = rs.getInt("guest_id1");
           guest_id2 = rs.getInt("guest_id2");
           guest_id3 = rs.getInt("guest_id3");
           guest_id4 = rs.getInt("guest_id4");
           guest_id5 = rs.getInt("guest_id5");
           time = rs.getInt("time");
           fb = rs.getShort("fb");
           courseT = rs.getString("courseName");

           if (index >= 0) {

               teecurr_id = rs.getInt("teecurr_id");
               hr = rs.getInt("hr");
               min = rs.getInt("min");
               event = rs.getString("event");
               ecolor = rs.getString("event_color");
               rest = rs.getString("restriction");
               rcolor = rs.getString("rest_color");
               conf = rs.getString("conf");
               in_use = rs.getInt("in_use");
               type = rs.getInt("event_type");
               hole = rs.getString("hole");
               blocker = rs.getString("blocker");
               rest5 = rs.getString("rest5");
               bgcolor5 = rs.getString("rest5_color");
               lottery = rs.getString("lottery");
               lottery_color = rs.getString("lottery_color");

           } else {

               teepast_id = rs.getInt("teepast_id");
               hr = time / 100;
               min = time - (hr * 100);
           }

           //
           //  If course=ALL requested, then set 'fives' option according to this course
           //
           if (course.equals( "-ALL-" )) {
               i = 0;
               loopall:
               while (i < 20) {
                  if (courseT.equals( courseA.get(i) )) {
                     fives = fivesA.get(i);          // get the 5-some option for this course
                     break loopall;              // exit loop
                  }
                  i++;
               }
           }

           boolean blnHide = false; /*( 
                                hideUnavail.equals("1") && 
                                fives == 0 && 
                                !player1.equals("") && 
                                !player2.equals("") && 
                                !player3.equals("") && 
                                !player4.equals("") 
                               );*/

            // only hide full tee times if not here for lottery, event or wait list
            if (hideUnavail.equals("1") && mode.equals("")) {
                if (fives == 0 || !rest5.equals("") ) {

                    if ( !player1.equals("") && !player2.equals("") && !player3.equals("") && !player4.equals("") ) blnHide = true;

                } else {

                    if ( !player1.equals("") && !player2.equals("") && !player3.equals("") && !player4.equals("")  && !player5.equals("")) blnHide = true;

                }
            }

            // if here for a lottery and this particular time is an event time, then hide it (lotteries maybe configured everyday)
            if (mode.equals("LOTT") && !event.equals("") && hideUnavail.equals("1")) blnHide = true;

            // only show this slot if it is NOT blocked
            // and hide it if fives are disallowed and player1-4 full (all slots full already excluded in sql)
            if ( blocker.equals( "" ) && blnHide == false) {      // continue if tee time not blocked - else skip

               ampm = " AM";
               if (hr == 12) {
                  ampm = " PM";
               }
               if (hr > 12) {
                  ampm = " PM";
                  hr = hr - 12;    // convert to conventional time
               }

               bgcolor = "#FFFFFF";               //default

               if (!event.equals("")) {
                  bgcolor = ecolor;
               } else {

                  if (!rest.equals("")) {
                     bgcolor = rcolor;
                  } else {

                     if (!lottery_color.equals("")) {
                        bgcolor = lottery_color;
                     }
                  }
               }

               if (bgcolor.equals("Default")) {
                  bgcolor = "#FFFFFF";              //default
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
               sfb2 = "Front";

               if (fb == 1) {

                  sfb = "B";
                  sfb2 = "Back";
               }

               if (fb == 9) {

                  sfb = "O";
                  sfb2 = "O";
               }

               //
               // if restriction for this slot and its not the first time for a lottery, check restriction for this member
               //
               if (!rest.equals("") && !rcolor.equals("")) {

                   int ind = 0;
                   while (ind < parmr.MAX && !parmr.restName[ind].equals("")) {

                       if (parmr.restName[ind].equals(rest)) {

                           // Check to make sure no suspensions apply
                           suspend = false;                        
                           for (int m=0; m<parmr.MAX; m++) {

                               if (parmr.susp[ind][m][0] == 0 && parmr.susp[ind][m][1] == 0) {
                                   m = parmr.MAX;   // don't bother checking any more
                               } else if (parmr.susp[ind][m][0] <= time && parmr.susp[ind][m][1] >= time) {    //time falls within a suspension
                                   suspend = true;                       
                                   m = parmr.MAX;     // don't bother checking any more
                               }
                           }      // end of for loop

                           if (suspend) {

                               if (bgcolor5.equals(bgcolor)) { // Reset bgcolor5 if color was that of the suspended restriction
                                   bgcolor5 = "";
                               }

                               if ((parmr.courseName[ind].equals( "-ALL-" )) || (parmr.courseName[ind].equals( courseName ))) {  // course ?

                                   if ((parmr.fb[ind].equals( "Both" )) || (parmr.fb[ind].equals( sfb2 ))) {    // matching f/b ?

                                       //
                                       //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                                       //
                                       if (event.equals("") && lottery.equals("")) {           // change color back to default if no event

                                           // Search for the first non-suspended color to apply, or default if non found
                                           bgcolor = "#FFFFFF";   // default color

                                           int ind2 = 0;
                                           while (ind2 < parmr.MAX && !parmr.restName[ind2].equals("")) {

                                               // make sure it's not the default restriction/color, and has a non-blank, non-default color
                                               // and applies to this time
                                               if (!parmr.restName[ind2].equals(rest) && !parmr.color[ind2].equals("") && !parmr.color[ind2].equalsIgnoreCase("Default") && 
                                                       parmr.stime[ind2] <= time && parmr.etime[ind2] >= time) {      

                                                   // Check to make sure no suspensions apply
                                                   suspend = false;                        
                                                   for (int m=0; m<parmr.MAX; m++) {

                                                       if (parmr.susp[ind2][m][0] == 0 && parmr.susp[ind2][m][1] == 0) {
                                                           m = parmr.MAX;   // don't bother checking any more
                                                       } else if (parmr.susp[ind2][m][0] <= time && parmr.susp[ind2][m][1] >= time) {    //time falls within a suspension
                                                           suspend = true;                       
                                                           m = parmr.MAX;     // don't bother checking any more
                                                       }
                                                   }

                                                   if (!suspend) {

                                                       if ((parmr.courseName[ind2].equals( "-ALL-" )) || (parmr.courseName[ind2].equals( courseName ))) {  // course ?

                                                           if ((parmr.fb[ind2].equals( "Both" )) || (parmr.fb[ind2].equals( sfb2 ))) {    // matching f/b ?

                                                               //
                                                               //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                                                               //
                                                               if (event.equals("") && lottery.equals("")) {           // change color if no event

                                                                   if (!parmr.color[ind2].equals("Default")) {     // if not default

                                                                       bgcolor = parmr.color[ind2];
                                                                       ind2 = parmr.MAX;
                                                                   }
                                                               }
                                                           }
                                                       }
                                                   }
                                               }
                                               ind2++;
                                           }

                                           if (bgcolor5.equals( "" )) {
                                               bgcolor5 = bgcolor;         // same as others if not specified
                                           }
                                       }
                                   }
                                }
                            }
                        }
                        ind++;
                    }      // end of while loop
               }     // end of if rest exists in teecurr

               /*       *** Don't inticate shotguns!
               if (type == shotgun) {

                  sfb = (!hole.equals("")) ? hole : "S";            // there's an event and its type is 'shotgun'
               }
               */

               // set default color for first three columns
               if (in_use != 0) dts_defaultF3Color = "";

               //
               //**********************************
               //  Build the tee time rows
               //**********************************
               //

               out.print("<div id=time_slot_"+ dts_slot_index +" time=\"" + time + "\" course=\"" + courseT + "\" startX=0 startY=0 tid="+((teecurr_id == 0) ? teepast_id : teecurr_id)+" ");
               if (in_use == 0 && index >= 0) {
                 // not in use
                 out.println("class=timeSlot drag=true style=\"background-color:"+ bgcolor +"\" bgc=\""+ bgcolor +"\">");
               } else {
                 // in use
                 out.println("class=timeSlotInUse>");
               }


               // col for 'insert' and 'delete' requests
               out.print("<span id=time_slot_" + dts_slot_index + "_A class=cellDataB style=\"cursor: default; left: " + col_start[1] + "px; width: " + col_width[1] + "px; background-color: #FFFFFF\">");
               j++;                                           // increment the jump label index (where to jump on page)
               out.print("<a name=\"jump" + j + "\"></a>");     // create a jump label for returns

               if (in_use == 0 && index >= 0) {

                   // not in use 
                   out.print("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?mode=" +mode+ "&name=" +name+ "&index=" +index+ "&course=" +courseT+ "&returnCourse=" +course+ "&time=" +time+ "&fb=" +fb+ "&jump=" +j+ "&email=" +emailOpt+ "&first=" +first+ "&insert=yes\" title=\"Insert a time slot\" alt=\"Insert a time slot\">");
                   out.print("<img src=/" +rev+ "/images/dts_newrow.gif width=13 height=13 border=0></a>");
                   out.print("<img src=/" +rev+ "/images/shim.gif width=5 height=1 border=0>");
                   out.print("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?mode=" +mode+ "&name=" +name+ "&index=" +index+ "&course=" +courseT+ "&returnCourse=" +course+ "&time=" +time+ "&fb=" +fb+ "&jump=" +j+ "&email=" +emailOpt+ "&delete=yes\" title=\"Delete time slot\" alt=\"Remove time slot\">");
                   out.print("<img src=/" +rev+ "/images/dts_trash.gif width=13 height=13 border=0></a>");

               } else {

                   // in use
                   out.print("<img src=/" +rev+ "/images/busy.gif width=32 height=13 border=0 alt=\"Busy\" title=\"Busy\">");

               }
               out.println("</span>");

               // time column
               if (in_use == 0 && index >= 0) {
                 out.print("<span id=time_slot_" + dts_slot_index + "_time class=cellData hollow=true style=\"left: " + col_start[2] + "px; width: " + col_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
               } else {
                 out.print("<span id=time_slot_" + dts_slot_index + "_time class=cellData style=\"cursor: default; left: " + col_start[2] + "px; width: " + col_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
               }

               // out.print(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm);    // tee time display
               dts_tmp = hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm;

               if (!event.equals("") && type == shotgun) {    // if Shotgun Event

                  //  find the matching event and get the actual start time of this event to display in place of the tee time
                  eloop1:
                  for (int e=0; e<parmEvents.MAX; e++) {

                     if (event.equals( parmEvents.name[e] )) {    // find matching event

                        int act_hr = parmEvents.act_time[e]/100;                     // get actual hour & min
                        int act_min = parmEvents.act_time[e] - (act_hr * 100);  
                        if (act_hr > 12) act_hr = act_hr - 12;   // convert to standard time (from military)
                        if (act_hr == 0) act_hr = 12;

                        dts_tmp = act_hr + ":" + SystemUtils.ensureDoubleDigit(act_min) + " Shot";
                        break eloop1;
                     }
                  }
               }

               out.print(dts_tmp);       // time display for tee time

               out.println("</span>");

               //
               //  Name of Course
               //
               if (course.equals( "-ALL-" )) { // only display this col if this tee sheet is showing more than one course

                   for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                       if (courseT.equals(courseA.get(tmp_i))) break;
                   }

                   if (tmp_i >= colorMax) tmp_i = (colorMax - 1);       // use White if courses exceeds max colors defined

                   out.print("<span id=time_slot_" + dts_slot_index + "_course class=cellDataC style=\"cursor: default; left: " + col_start[3] + "px; width: " + col_width[3] + "px; background-color:" + colors.course_color[tmp_i] + "\">");
                   if (!courseT.equals("")) { out.print(fitName(courseT)); }
                   out.println("</span>");
               }

               //
               //  Front/Back Indicator  (note:  do we want to display the FBO popup if it's a shotgun event)
               //
               if (in_use == 0 && hole.equals("") && index >= 0) {
                 out.print("<span id=time_slot_" + dts_slot_index + "_FB class=cellData onclick=\"showFBO(this)\" value=\""+sfb+"\" style=\"cursor: pointer; left: " + col_start[4] + "px; width: " + col_width[4] + "px\">"); //  background-color: " +dts_defaultF3Color+ "
               } else {
                 out.print("<span id=time_slot_" + dts_slot_index + "_FB class=cellDataB value=\""+sfb+"\" style=\"cursor: default; left: " + col_start[4] + "px; width: " + col_width[4] + "px\">");
               }
               out.print(sfb);
               out.println("</span>");


               //
               //  Add Player 1
               //
               if (in_use == 0 && index >= 0) {
                 out.print("<span id=time_slot_" + dts_slot_index + "_player_1 class=cellData drag=true startX="+col_start[5]+" playerSlot=1 style=\"left: " + col_start[5] + "px; width: " + col_width[5] + "px\">");
               } else {
                 out.print("<span id=time_slot_" + dts_slot_index + "_player_1 class=cellData startX="+col_start[5]+" playerSlot=1 style=\"cursor: default; left: " + col_start[5] + "px; width: " + col_width[5] + "px\">");
               }
               if (!player1.equals("")) { out.print(fitName(player1)); }
               out.println("</span>");

               // Player 1 CW
               if ((!player1.equals("")) && (!player1.equalsIgnoreCase( "x" ))) {
                  dts_tmp = p1cw;
               } else {
                  dts_tmp = "";
               }
               if (in_use == 0 && index >= 0) {
                 out.print("<span id=time_slot_" + dts_slot_index + "_player_1_CW class=cellDataB onclick=\"showTOPopup(this)\" value=\"" + dts_tmp + "\" style=\"left: " + col_start[6] + "px; width: " + col_width[6] + "px\">");
               } else {
                 out.print("<span id=time_slot_" + dts_slot_index + "_player_1_CW class=cellDataB style=\"cursor: default; left: " + col_start[6] + "px; width: " + col_width[6] + "px\">");
               }
               out.print(dts_tmp);
               out.println("</span>");


               //
               //  Add Player 2
               //
               if (in_use == 0 && index >= 0) {
                 out.print("<span id=time_slot_" + dts_slot_index + "_player_2 class=cellData drag=true startX="+col_start[7]+" playerSlot=2 style=\"left: " + col_start[7] + "px; width: " + col_width[7] + "px\">");
               } else {
                 out.print("<span id=time_slot_" + dts_slot_index + "_player_2 class=cellData startX="+col_start[7]+" playerSlot=2 style=\"cursor: default; left: " + col_start[7] + "px; width: " + col_width[7] + "px\">");
               }
               if (!player2.equals("")) { out.print(fitName(player2)); }
               out.println("</span>");

               // Player 2 CW
               if ((!player2.equals("")) && (!player2.equalsIgnoreCase( "x" ))) {
                  dts_tmp = p2cw;
               } else {
                  dts_tmp = "";
               }
               if (in_use == 0 && index >= 0) {
                 out.print("<span id=time_slot_" + dts_slot_index + "_player_2_CW class=cellDataB onclick=\"showTOPopup(this)\" value=\"" + dts_tmp + "\" style=\"left: " + col_start[8] + "px; width: " + col_width[8] + "px\">");
               } else {
                 out.print("<span id=time_slot_" + dts_slot_index + "_player_2_CW class=cellDataB style=\"cursor: default; left: " + col_start[8] + "px; width: " + col_width[8] + "px\">");
               }
               out.print(dts_tmp);
               out.println("</span>");


               //
               //  Add Player 3
               //
               if (in_use == 0 && index >= 0) {
                 out.print("<span id=time_slot_" + dts_slot_index + "_player_3 class=cellData drag=true startX="+col_start[9]+" playerSlot=3 style=\"left: " + col_start[9] + "px; width: " + col_width[9] + "px\">");
               } else {
                 out.print("<span id=time_slot_" + dts_slot_index + "_player_3 class=cellData startX="+col_start[9]+" playerSlot=3 style=\"cursor: default; left: " + col_start[9] + "px; width: " + col_width[9] + "px\">");
               }
               if (!player3.equals("")) { out.print(fitName(player3)); }
               out.println("</span>");

               // Player 3 CW
               if ((!player3.equals("")) && (!player3.equalsIgnoreCase( "x" ))) {
                  dts_tmp = p3cw;
               } else {
                  dts_tmp = "";
               }
               if (in_use == 0 && index >= 0) {
                 out.print("<span id=time_slot_" + dts_slot_index + "_player_3_CW class=cellDataB onclick=\"showTOPopup(this)\" value=\"" + dts_tmp + "\" style=\"left: " + col_start[10] + "px; width: " + col_width[10] + "px\">");
               } else {
                 out.print("<span id=time_slot_" + dts_slot_index + "_player_3_CW class=cellDataB style=\"cursor: default; left: " + col_start[10] + "px; width: " + col_width[10] + "px\">");
               }
               out.print(dts_tmp);
               out.println("</span>");

               //
               //  Add Player 4
               //
               if (in_use == 0 && index >= 0) {
                 out.print("<span id=time_slot_" + dts_slot_index + "_player_4 class=cellData drag=true startX="+col_start[11]+" playerSlot=4 style=\"left: " + col_start[11] + "px; width: " + col_width[11] + "px\">");
               } else {
                 out.print("<span id=time_slot_" + dts_slot_index + "_player_4 class=cellData startX="+col_start[11]+" playerSlot=4 style=\"cursor: default; left: " + col_start[11] + "px; width: " + col_width[11] + "px\">");
               }
               if (!player4.equals("")) { out.print(fitName(player4)); }
               out.println("</span>");

               // Player 4 CW
               if ((!player4.equals("")) && (!player4.equalsIgnoreCase( "x" ))) {
                  dts_tmp = p4cw;
               } else {
                  dts_tmp = "";
               }
               if (in_use == 0 && index >= 0) {
                 out.print("<span id=time_slot_" + dts_slot_index + "_player_4_CW class=cellDataB onclick=\"showTOPopup(this)\" value=\"" + dts_tmp + "\" style=\"left: " + col_start[12] + "px; width: " + col_width[12] + "px\">");
               } else {
                 out.print("<span id=time_slot_" + dts_slot_index + "_player_4_CW class=cellDataB style=\"cursor: default; left: " + col_start[12] + "px; width: " + col_width[12] + "px\">");
               }
               out.print(dts_tmp);
               out.println("</span>");

               //
               //  Add Player 5 if supported
               //
               if (fivesALL != 0) {        // if 5-somes on any course        (Paul - this is a new flag!!!!)
                  if (fives != 0) {        // if 5-somes on this course
                    if (in_use == 0 && index >= 0) {
                      out.print("<span id=time_slot_" + dts_slot_index + "_player_5 class=cellData drag=true startX="+col_start[13]+" playerSlot=5 style=\"left: " + col_start[13] + "px; width: " + col_width[13] + "px; background-color: " +bgcolor5+ "\">");
                    } else {
                      out.print("<span id=time_slot_" + dts_slot_index + "_player_5 class=cellData startX="+col_start[13]+" playerSlot=5 style=\"cursor: default; left: " + col_start[13] + "px; width: " + col_width[13] + "px\">");
                    }
                    if (!player5.equals("")) { out.print(fitName(player5)); }
                    out.println("</span>");

                    // Player 5 CW
                    if ((!player5.equals("")) && (!player5.equalsIgnoreCase( "x" ))) {
                       dts_tmp = p5cw;
                    } else {
                       dts_tmp = "";
                    }
                    if (in_use == 0 && index >= 0) {
                      out.print("<span id=time_slot_" + dts_slot_index + "_player_5_CW class=cellDataB onclick=\"showTOPopup(this)\" value=\"" + dts_tmp + "\" style=\"left: " + col_start[14] + "px; width: " + col_width[14] + "px; background-color: " +bgcolor5+ "\">");
                    } else {
                      out.print("<span id=time_slot_" + dts_slot_index + "_player_5_CW class=cellDataB style=\"cursor: default; left: " + col_start[14] + "px; width: " + col_width[14] + "px\">");
                    }
                    out.print(dts_tmp);
                    out.println("</span>");

                  } else {       // 5-somes on at least 1 course, but not this one

                    out.print("<span id=time_slot_" + dts_slot_index + "_player_5 class=cellData startX="+col_start[13]+" playerSlot=5 style=\"cursor: default; left: " + col_start[13] + "px; width: " + col_width[13] + "px;  background-image: url('/v5/images/shade1.gif')\">");
                    out.println("</span>");

                    // Player 5 CW
                    dts_tmp = "";
                    out.print("<span id=time_slot_" + dts_slot_index + "_player_5_CW class=cellDataB style=\"cursor: default; left: " + col_start[14] + "px; width: " + col_width[14] + "px; background-image: url('/v5/images/shade1.gif')\">");
                    out.print(dts_tmp);
                    out.println("</span>");

                  } // end if fives
               } // end if fivesALL

               out.println("</div>"); // end timeslot container div

               dts_slot_index++;    // increment timeslot index counter
               first = "no";        // no longer first time displayed

            }  // end of IF Blocker that escapes building and displaying a particular tee time slot in the sheet

         }  // end of while

         pstmt.close();

         out.println("<br>"); // spacer at bottom of tee sheet
         out.println("\n</div>"); // end main container div holding entire tee sheet
         out.println("<!-- END OF TEE SHEET BODY -->\n");
         out.println("<br><br>\n");

       // write out form for posting tee sheet actions to the server for processing
       out.println("<form name=frmSendAction method=POST action=/" +rev+ "/servlet/Proshop_dsheet>");
       out.println("<input type=hidden name=convert value=\"\">");
       out.println("<input type=hidden name=index value=\"" + index + "\">");
       out.println("<input type=hidden name=returnCourse value=\"" + course + "\">");
       out.println("<input type=hidden name=email value=\"" + emailOpt + "\">");
       out.println("<input type=hidden name=mode value=\"" + mode + "\">");
       out.println("<input type=hidden name=name value=\"" + name + "\">");
       out.println("<input type=hidden name=hide value=\"" + hideUnavail + "\">"); 

       if (mode.equals("WAITLIST")) {
           out.println("<input type=hidden name=wlsId value=\"\">");
       } else if (mode.equals("LOTT")) {
           out.println("<input type=hidden name=lotteryId value=\"\">");
           out.println("<input type=hidden name=group value=\"\">");
       } else if (mode.equals("EVENT")) {
           out.println("<input type=hidden name=eventId value=\"\">");
       } else {
           out.println("<input type=hidden name=notifyId value=\"\">");
       }

       out.println("<input type=hidden name=from_tid value=\"\">");
       out.println("<input type=hidden name=to_tid value=\"\">");

       out.println("<input type=hidden name=from_course value=\"\">");
       out.println("<input type=hidden name=to_course value=\"\">");

       out.println("<input type=hidden name=jump value=\"\">");                  // needs to be set in ....js !!!!!
       out.println("<input type=hidden name=from_time value=\"\">");
       out.println("<input type=hidden name=from_fb value=\"\">");
       out.println("<input type=hidden name=to_time value=\"\">");
       out.println("<input type=hidden name=to_fb value=\"\">");
       out.println("<input type=hidden name=from_player value=\"\">");
       out.println("<input type=hidden name=to_player value=\"\">");
       out.println("<input type=hidden name=to_from value=\"\">");
       out.println("<input type=hidden name=to_to value=\"\">");
       out.println("<input type=hidden name=changeAll value=\"\">");
       out.println("<input type=hidden name=ninehole value=\"\">");
       out.println("</form>");

       // START OF FBO POPUP WINDOW //
       out.println("<div id=elFBOPopup defaultValue=\"\" style=\"visibility: hidden\" jump=\"\">");
       out.println("<table width='100%' height='100%' border=0 cellpadding=0 cellspacing=2>");
       out.println("<form name=frmFBO>");
       out.println("<input type=hidden name=jump value=\"\">");
       out.println("<tr><td align=center class=smtext><b><u>Make Selection</u></b></td></tr>");
       out.println("<tr><td class=smtext><input type=radio value=F name=FBO id=FBO_1><label for=\"FBO_1\">Front</label></td></tr>");
       out.println("<tr><td class=smtext><input type=radio value=B name=FBO id=FBO_2><label for=\"FBO_2\">Back</label></td></tr>");
       out.println("<tr><td class=smtext><input type=radio value=O name=FBO id=FBO_3><label for=\"FBO_3\">Crossover</label></td></tr>");
       out.println("<tr><td align=right><a href=\"javascript: cancelFBOPopup()\" class=smtext>cancel</a>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <a href=\"javascript: saveFBOPopup()\" class=smtext>save</a>&nbsp;</td></tr>");
       out.println("</form>");
       out.println("</table>");
       out.println("</div>");


       // START OF TRANSPORTATION POPUP WINDOW //
       //
       //  Note:  There can now be up to 16 dynamic Modes of Transportation (proshop can config).
       //         Both the Full Name/Description and the Acronym are specified by the pro.
       //         These names and acronyms will not contain the '9' to indicate 9 holes.
       //         These values can be found in:
       //
       //               parmc.tmode[i]   =  full name description   
       //               parmc.tmodea[i]  =  1 to 3 character acronym  (i = index of 0 - 15)
       //
       //
       out.println("<div id=elTOPopup defaultValue=\"\" fb=\"\" nh=\"\" jump=\"\">");
       out.println("<table width='100%' height='100%' border=0 cellpadding=0 cellspacing=2>");
       out.println("<form name=frmTransOpt>");
       out.println("<input type=hidden name=jump value=\"\">");
       // loop thru the array and write out a table row for each option

       // set tmp_cols to the # of cols this table will have
       // if the # of trans opts is less then 4 then that's the #, otherwise the max is 4
       //   tmode_limit = max number of tmodes available
       //   tmode_count = actual number of tmodes specified for this course
       int tmp_cols = 0;
       if (parmc.tmode_count < 4) {
          tmp_cols = parmc.tmode_count;
       } else {
          tmp_cols = 4;
       }
       int tmp_count = 0;

       out.println("<tr><td align=center class=smtext colspan="+tmp_cols+"><b><u>Make Selection</u></b></td></tr>");

       out.println("<tr>");
       for (int tmp_loop = 0; tmp_loop < parmc.tmode_limit; tmp_loop++) {
         if (!parmc.tmodea[tmp_loop].equals( "" ) && !parmc.tmodea[tmp_loop].equals( null )) {
           out.println("<td nowrap class=smtext><input type=radio value="+parmc.tmodea[tmp_loop]+" name=to id=to_"+tmp_loop+"><label for=\"to_"+tmp_loop+"\">"+parmc.tmode[tmp_loop]+"</label></td>");
           if (tmp_count == 3 || tmp_count == 7 || tmp_count == 11) {
             out.println("</tr><tr>");         // new row
           }
           tmp_count++;
         }
       }
       out.println("</tr>");

       out.println("<tr><td bgcolor=black colspan="+tmp_cols+"><img src=/" +rev+ "/images/shim.gif width=100 height=1 border=0></td></tr>");
       out.println("<tr><td class=smtext colspan="+tmp_cols+"><input type=checkbox value=yes name=9hole id=nh><label for=\"nh\">9 Hole</label></td></tr>");
       out.println("<tr><td bgcolor=black colspan="+tmp_cols+"><img src=/" +rev+ "/images/shim.gif width=100 height=1 border=0></td></tr>");
       // "CHANGE ALL" DEFAULT OPTION COULD BE SET HERE
       out.println("<tr><td class=smtext colspan="+tmp_cols+"><input type=checkbox value=yes name=changeAll id=ca><label for=\"ca\">Change All</label></td></tr>");
       out.println("<tr><td align=right colspan="+tmp_cols+"><a href=\"javascript: cancelTOPopup()\" class=smtext>cancel</a>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <a href=\"javascript: saveTOPopup()\" class=smtext>save</a>&nbsp;</td></tr>");
       out.println("</form>");
       out.println("</table>");
       out.println("</div>");

       // FINAL JAVASCRIPT FOR THE PAGE, SET VARIABLES THAT WE DIDN'T KNOW TILL AFTER PROCESSING
       out.println("<script type=\"text/javascript\">");
       //out.println("/*if (document.getElementById(\"time_slot_0\")) {");
       //out.println(" slotHeight = document.getElementById(\"time_slot_0\").offsetHeight;");
       //out.println("} else if (document.getElementById(\"time_slot_0\")) {");
       //out.println(" slotHeight = document.getElementById(\"notify_slot_0\").offsetHeight;");
       //out.println("}*/");
       out.println("var slotHeight = 20;");
       out.println("if (!ie) slotHeight = 25;");
       out.println("var shim = 2;");
       out.println("if (!ie) shim = 0;");
       out.println("var g_markerY = document.getElementById(\"imgMarker\").offsetTop;");
       out.println("var g_markerX = document.getElementById(\"imgMarker\").offsetLeft;");
       out.println("var totalTimeSlots = " + (dts_slot_index) + ";");
       out.println("var totalUnassignedSlots = " + total_unassigned_slots + ";");
       out.println("var g_transOptTotal = " + tmp_count + ";");
       out.println("var g_pslot1s = " + col_start[5] + ";");
       out.println("var g_pslot1e = " + col_start[6] + ";");
       out.println("var g_pslot2s = " + col_start[7] + ";");
       out.println("var g_pslot2e = " + col_start[8] + ";");
       out.println("var g_pslot3s = " + col_start[9] + ";");
       out.println("var g_pslot3e = " + col_start[10] + ";");
       out.println("var g_pslot4s = " + col_start[11] + ";");
       out.println("var g_pslot4e = " + col_start[12] + ";");
       out.println("var g_pslot5s = " + col_start[13] + ";");
       out.println("var g_pslot5e = " + col_start[14] + ";");

       // SIZE UP THE CONTAINER ELEMENTS AND THE TIME SLOTS
       out.println("var e = document.getElementById('widthMarker');");
       out.println("var g_slotWidth = e.offsetLeft + e.offsetWidth;");
       out.println("var e2 = document.getElementById('widthMarker2');");
       out.println("var g_notifySlotWidth = e2.offsetLeft + e2.offsetWidth;");
       //out.println("alert('g_slotWidth=' + g_slotWidth + ' | g_notifySlotWidth=' + g_notifySlotWidth);");

       out.println("var rules = getCSSRule(0);");
       out.println("rules[0].style.width = (g_slotWidth + shim) + 'px';");             // elHContainer
       out.println("rules[1].style.width = (g_slotWidth + shim) + 'px';");             // elContainer
       out.println("rules[7].style.width = g_slotWidth + 'px';");                      // header 
       out.println("rules[8].style.width = g_slotWidth + 'px';");                      // timeslot
       out.println("rules[10].style.width = g_notifySlotWidth + 'px';");               // notifyslot
       out.println("rules[12].style.width = (g_notifySlotWidth + shim) + 'px';");      // elHContainer2
       out.println("rules[13].style.width = (g_notifySlotWidth + shim) + 'px';");      // elContainer2

       // IF THERE IS NO 5th PLAYER COLUMN ON THIS TEE SHEET THEN NUDGE THE TEE SHEET OVER A BIT TO CENTER BETTER
       /*if (fivesALL == 0) {
          out.println("document.styleSheets[0].rules(0).style.left = \"80px\";");
          out.println("document.styleSheets[0].rules(1).style.left = \"80px\";");
          out.println("document.styleSheets[0].rules(11).style.left = \"80px\";");
          out.println("document.styleSheets[0].rules(12).style.left = \"80px\";");
       }*/

       /*
       int tmp_offset1 = (total_unassigned_slots == 0) ? 0 : (total_unassigned_slots * 20) + 2;    // height of notification container
       int tmp_offset2 = (dts_slot_index == 0) ? 0 : (dts_slot_index * 20) + 2;                    // height of tee sheet container
       int tmp_offset3 = (total_unassigned_slots == 0) ? 0 : 24;                                   // 24 is height of header
       int tmp_offset4 = 24;                                                                       // 24 is height of header
       int tmp_offset5 = (total_unassigned_slots == 0) ? 40 : 80;
       */

       out.println("var tmp_offset1 = (totalUnassignedSlots * slotHeight) + shim;");
       out.println("var tmp_offset2 = (totalTimeSlots == 0) ? 0 : (totalTimeSlots * slotHeight) + shim;");
       out.println("var tmp_offset4 = 24;");
       out.println("var tmp_offset3 = (totalUnassignedSlots == 0) ? 0 : tmp_offset4;");
       out.println("var tmp_offset5 = (totalUnassignedSlots == 0) ? 40 : 80;");

       // REPOSITION THE CONTAINERS TO THE MARKER
       out.println("document.getElementById(\"elHContainer2\").style.top=g_markerY;");
       out.println("document.getElementById(\"elHContainer2\").style.left=g_markerX;");
       out.println("document.getElementById(\"elContainer2\").style.top=g_markerY+22;");
       out.println("document.getElementById(\"elContainer2\").style.left=g_markerX;");

       if (total_unassigned_slots == 0) {
           out.println("document.getElementById(\"elContainer2\").style.visibility = \"hidden\";");
           out.println("document.getElementById(\"elHContainer2\").style.visibility = \"hidden\";");
           out.println("document.getElementById(\"elContainer2\").style.height = \"0px\";");
           out.println("document.getElementById(\"elHContainer2\").style.height = \"0px\";");
       } else {
           // CALL THE POSITIONING CODE FOR EACH OF UNASSIGNED SLOT WE CREATED
           if (mode.equals("")) {
               out.println("for(x=0;x<=totalUnassignedSlots-1;x++) eval(\"positionElem('notify_slot_\" + x + \"', \"+ x +\")\");");
           } else if (mode.equals("WAITLIST")) {
               out.println("for(x=0;x<=totalUnassignedSlots-1;x++) eval(\"positionElem('wait_slot_\" + x + \"', \"+ x +\")\");");
           } else if (mode.equals("LOTT")) {
               out.println("for(x=0;x<=totalUnassignedSlots-1;x++) eval(\"positionElem('lottery_slot_\" + x + \"', \"+ x +\")\");");
           } else if (mode.equals("EVENT")) {
               out.println("for(x=0;x<=totalUnassignedSlots-1;x++) eval(\"positionElem('event_slot_\" + x + \"', \"+ x +\")\");");
           }
           //out.println("document.getElementById(\"elContainer2\").style.height=\"" + tmp_offset1 + "px\";");

           out.println("document.getElementById(\"elContainer2\").style.height=tmp_offset1");
       }

       // POSITION THE LEGEND
       //out.println("document.getElementById(\"tblLegend\").style.top=g_markerY + " + (tmp_offset1 + (tmp_offset3 * 2)) + ";");
       out.println("document.getElementById(\"tblLegend\").style.top=(g_markerY + tmp_offset1 + (tmp_offset3 * 2));");
       out.println("document.getElementById(\"tblLegend\").style.width=g_slotWidth;");

       // POSITION THE TEE SHEET CONTAINER
       //out.println("document.getElementById(\"elContainer\").style.top=(g_markerY + " + (tmp_offset1 + tmp_offset4 + tmp_offset5) + ");");
       //out.println("document.getElementById(\"elHContainer\").style.top=(g_markerY + " + (tmp_offset1 + tmp_offset5) + ");");
       out.println("document.getElementById(\"elContainer\").style.top=(g_markerY + tmp_offset1 + tmp_offset4 + tmp_offset5);");
       out.println("document.getElementById(\"elHContainer\").style.top=(g_markerY + tmp_offset1 + tmp_offset5);");

       if (dts_slot_index == 0) {
           out.println("document.getElementById(\"elContainer\").style.visibility = \"hidden\";");
       } else {
           // CALL THE POSITIONING CODE FOR EACH OF THE TIME SLOTS WE CREATED
           out.println("for(x=0;x<=totalTimeSlots-1;x++) eval(\"positionElem('time_slot_\" + x + \"', \"+ x +\")\");");
           //out.println("document.getElementById(\"elContainer\").style.height=\"" + tmp_offset2 + "px\";");
           out.println("document.getElementById(\"elContainer\").style.height=tmp_offset2;");
       }

       if (mode.equals("LOTT")) {
           out.println("function convert(lottid) {");
           //out.println(" f = document.getElementById('frmSendAction');");
           out.println(" var f = document.forms[\"frmSendAction\"];"); // .returnCourse
           out.println(" f.lotteryId.value = lottid;");
           out.println(" f.convert.value = 'auto';");
           out.println(" f.submit();");
           out.println("}");
       }

       out.println("</script>");
       
       
      /*    
          out.println("<script type=\"text/javascript\">");
          out.println("function checkCourse() {");
          //out.println(" var f = document.getElementById(\"frmSendAction\").returnCourse;");
          out.println(" var f = document.forms[\"frmSendAction\"].returnCourse;");
          out.println(" if (f.value == \"-ALL-\") {");
          out.println("  alert(\"You must select a specific course before going back.\\nYou are currently viewing ALL courses for this day.\");");
          out.println("  return false;");
          out.println(" }");
          out.println(" return true;");
          out.println("}");
          out.println("</script>");
      */

       // END OF OUT FINAL CLIENT SIDE SCRIPT WRITING
    
    }
    catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center><BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>Error = " +errMsg);
      out.println("<BR><BR>Exception = " + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</center></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  End of HTML page
   //
   out.println("<p>&nbsp;</p>");
   out.println("</body>\n</html>");
   out.close();

 }   // end of doGet


 //************************************************************************************
 //
 //   **** doPost **** 
 //
 //     Refer to the 'doGet' method above to see how the tee sheet is built.
 //     This method needs the following parameters based on the type of drag-n-drop performed.
 //
 //
 // Process drag-n-drop requests from doGet above
 //
 //   Parms passed on every call:
 //
 //            index        = index value for date (0 = today, 1 = tomorrow, etc.)
 //            jump         = jump index for return to tee sheet
 //            returnCourse = name of course for return (could be -ALL-)
 //
 //      Change F/B:  
 //
 //            from_time    = time value of tee time (hhmm) player moved from
 //            from_fb      = front/back value of tee time player moved from (F, B, O)
 //            from_course  = name of course for this tee time
 //            to_fb        = front/back value of tee time player moved to (F, B, O)
 //
 //      Change C/W:
 //
 //            from_player  = player position that is changing (1-5)
 //            from_time    = time value of tee time (hhmm) player moved from
 //            from_fb      = front/back value of tee time player moved from (F, B, O)
 //            from_course  = name of course for this tee time
 //            to_from      = current transporation option for player - (WA, MC, etc.)
 //            to_to        = new transporation option for player(s) - (WA, MC, etc.)
 //            ninehole     = 9 Hole option
 //            changeAll    = change all players to this option (true or false)
 //
 //      Single player move:
 //
 //            from_player  = current player position - moved from (1-5)
 //            from_time    = time value of tee time (hhmm) player moved from
 //            from_fb      = front/back value of tee time player moved from (F, B, O)
 //            from_course  = name of course for this tee time
 //            to_player    = new player position - moved to (1-5)
 //            to_time      = time value of tee time (hhmm) player moved to
 //            to_fb        = front/back value of tee time player moved to (F, B, O)
 //            to_course    = the course name this player moved to
 //
 //      Entire tee time move:
 //
 //            from_time    = time value of tee time (hhmm) player moved from
 //            from_fb      = front/back value of tee time player moved from (F, B, O)
 //            from_course  = name of course for this tee time
 //            to_time      = time value of tee time (hhmm) player moved to
 //            to_fb        = front/back value of tee time player moved to (F, B, O)
 //            to_course    = the course name this tee time moved to
 //            prompt       = provided if user has been prompted to continue (2nd entry)
 //                           'return' if user wants to return w/o changes
 //                           'continue' if user wants to contnue with changes
 //            skip         = provided if user has been prompted, indicates verification
 //                           process to skip (1, 2, 3, etc.)
 //
 //************************************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   if (req.getParameter("setBlockers") != null) {
       doGet(req, resp);
       return; // added 6-5-07 to prevent IllegalStateException: Proshop_dsheet.doPost(Proshop_dsheet.java:2364)
   }  
   
   if (req.getParameter("ssha") != null) { // Set Shotgun Hole Assignments
       doGet(req, resp);
       return;
   }    
     
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   HttpSession session = SystemUtils.verifyPro(req, out);           // check for intruder
   if (session == null) return;
   
   Connection con = SystemUtils.getCon(session);                    // get DB connection
   if (con == null) {

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
      return;
   }
   
   //
   // Handle the conversion of notifications to teecurr2 entries
   //
   if (req.getParameter("notifyId") != null && req.getParameter("convert") != null && req.getParameter("convert").equals("yes")) {

       convertNotification(req, out, con, session, resp);
       return;
       //doGet(req, resp);
   }
       
   //
   // Handle the conversion of old notifications to teepast2 entries
   //
   if (req.getParameter("notifyId") != null && req.getParameter("convert") != null && req.getParameter("convert").equals("old")) {

       convertOld(req, out, con, session, resp);
       return;
   }
   
   
         
   //
   // Handle the conversion of lottery requests to teecurr2 entries (convert all goes to doGet)
   //
   if (req.getParameter("lotteryId") != null && req.getParameter("convert") != null) {
       
       if (req.getParameter("convert").equals("yes")) {

           convertLottReq(req, out, con, session, resp);
           return;
           
       } else if (req.getParameter("convert").equals("auto")) {
           
           auto_convert(req, out, con, session, resp);
           return;
           
       }
       
   }  
   
   //
   // Handle the conversion of wait list signups to teecurr2 entries
   //
   if (req.getParameter("wlsId") != null && req.getParameter("convert") != null && req.getParameter("convert").equals("yes")) {

       convertWaitListSgnUp(req, out, con, session, resp);
       return;
   }
   
   //
   // Handle the conversion of event signups to teecurr2 entries
   //
   if (req.getParameter("eventId") != null && req.getParameter("convert") != null && req.getParameter("convert").equals("yes")) {
       
       convertEventSgnUp(req, out, con, session, resp);
       return;
   }
   
   
   //
   //  parm block to hold the tee time parms
   //
   parmSlot slotParms = new parmSlot();          // allocate a parm block

   String changeAll = "";
   String ninehole = "";
   String dts_tmp = "";
   String prompt = "";
     
   int skip = 0;

   long date = 0;

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //
   //  Get this session's username (to be saved in teecurr)
   //
   slotParms.user = (String)session.getAttribute("user");
   slotParms.club = (String)session.getAttribute("club");

try {

   //
   //  Get the parms passed
   //
   String name = req.getParameter("name");
   if (name == null) name = "";
   slotParms.name = name;
   
   String hideUnavail = req.getParameter("hide");
   if (hideUnavail == null || !hideUnavail.equals("1")) hideUnavail = "0";
   slotParms.hideTimes = hideUnavail;
   
   slotParms.jump = req.getParameter("jump");           // anchor link for page loading
   String indexs = req.getParameter("index");           // # of days ahead of current day

   slotParms.ind = Integer.parseInt(indexs);            // save index value in parm block

   //
   //  Get the optional parms
   //
   if (req.getParameter("email") != null) {

      slotParms.sendEmail = req.getParameter("email");
   } else {
      slotParms.sendEmail = "yes";
   }

   if (req.getParameter("returnCourse") != null) {

      slotParms.returnCourse = req.getParameter("returnCourse");
   } else {
      slotParms.returnCourse = "";
   }

   if (req.getParameter("from_course") != null) {

      slotParms.from_course = req.getParameter("from_course");
   } else {
      slotParms.from_course = "";
   }

   if (req.getParameter("to_course") != null) {

      slotParms.to_course = req.getParameter("to_course");
   } else {
      slotParms.to_course = "";
   }

   if (req.getParameter("from_player") != null) {

      dts_tmp = req.getParameter("from_player");

      if (!dts_tmp.equals( "" )) {
         slotParms.from_player = Integer.parseInt(dts_tmp);
      }
   }
   if (req.getParameter("to_player") != null) {

      dts_tmp = req.getParameter("to_player");

      if (!dts_tmp.equals( "" )) {
         slotParms.to_player = Integer.parseInt(dts_tmp);
      }
   }
   if (req.getParameter("from_time") != null) {

      dts_tmp = req.getParameter("from_time");

      if (!dts_tmp.equals( "" )) {
         slotParms.from_time = Integer.parseInt(dts_tmp);
      }
   }
   if (req.getParameter("to_time") != null) {

      dts_tmp = req.getParameter("to_time");

      if (!dts_tmp.equals( "" )) {
         slotParms.to_time = Integer.parseInt(dts_tmp);
      }
   }
   if (req.getParameter("to_from") != null) {

      slotParms.to_from = req.getParameter("to_from");
   }
   if (req.getParameter("to_to") != null) {

      slotParms.to_to = req.getParameter("to_to");
   }
   if (req.getParameter("changeAll") != null) {

      changeAll = req.getParameter("changeAll");
   }
   if (req.getParameter("ninehole") != null) {

      ninehole = req.getParameter("ninehole");
   }

   if (req.getParameter("prompt") != null) {        // if 2nd entry (return from prompt)

      prompt = req.getParameter("prompt");

      dts_tmp = req.getParameter("date");

      if (!dts_tmp.equals( "" )) {
         date = Integer.parseInt(dts_tmp);
      }

      dts_tmp = req.getParameter("to_fb");

      if (!dts_tmp.equals( "" )) {
         slotParms.to_fb = Integer.parseInt(dts_tmp);
      }

      dts_tmp = req.getParameter("from_fb");

      if (!dts_tmp.equals( "" )) {
         slotParms.from_fb = Integer.parseInt(dts_tmp);
      }

      if (req.getParameter("skip") != null) {        // if 2nd entry and skip returned

         dts_tmp = req.getParameter("skip");

         if (!dts_tmp.equals( "" )) {
            skip = Integer.parseInt(dts_tmp);
         }
      }

   } else {

      if (req.getParameter("from_fb") != null) {

         dts_tmp = req.getParameter("from_fb");
         
         slotParms.from_fb = 0;

         if (dts_tmp.equals( "B" )) {

            slotParms.from_fb = 1;
         }
         if (dts_tmp.equals( "O" )) {

            slotParms.from_fb = 9;
         }
      }
      if (req.getParameter("to_fb") != null) {

         dts_tmp = req.getParameter("to_fb");

         slotParms.to_fb = 0;

         if (dts_tmp.equals( "B" )) {

            slotParms.to_fb = 1;
         }
         if (dts_tmp.equals( "O" )) {

            slotParms.to_fb = 9;
         }
      }
   }

 } catch (Exception e) {
     out.println("Error parsing input variables. " + e.toString());
 }
   
   if (date == 0) {
      //
      //  Get today's date and then use the value passed to locate the requested date
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      if (slotParms.ind > 0) {
         cal.add(Calendar.DATE,slotParms.ind);         // roll ahead 'index' days
      }

      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);
      int day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

      month = month + 1;                            // month starts at zero

      slotParms.dd = day;         
      slotParms.mm = month;
      slotParms.yy = year;
      slotParms.day = day_table[day_num];                // get name for day

      date = (year * 10000) + (month * 100) + day;  // create a date field of yyyymmdd
        
   } else {

      if (req.getParameter("day") != null) {

         slotParms.day = req.getParameter("day");
      }

      long lyy = date / 10000;                               // get year
      long lmm = (date - (lyy * 10000)) / 100;               // get month
      long ldd = (date - (lyy * 10000)) - (lmm * 100);       // get day

      slotParms.dd = (int)ldd;
      slotParms.mm = (int)lmm;
      slotParms.yy = (int)lyy;
   }

   //
   //  determine the type of call: Change F/B, Change C/W, Single Player Move, Whole Tee Time Move
   //
   if (!slotParms.to_from.equals( "" ) && !slotParms.to_to.equals( "" )) {

      changeCW(slotParms, changeAll, ninehole, date, req, out, con, resp);     // process Change C/W
      return;
   }

   if (slotParms.to_time == 0) {  // if not C/W and no 'to_time' specified

      changeFB(slotParms, date, req, out, con, resp);              // process Change F/B
      return;
   }

   if ((slotParms.to_player == 0) && (slotParms.from_player == 0)) {

      moveWhole(slotParms, date, prompt, skip, req, out, con, resp);  // process Move Whole Tee Time
      return;
   }

   if (slotParms.from_player > 0 && slotParms.to_player > 0) {

      moveSingle(slotParms, date, prompt, skip, req, out, con, resp);  // process Move Single Tee Time
      return;
   }
  
   //
   //  If we get here, there is an error
   //
   out.println(SystemUtils.HeadTitle("Error in Proshop_dsheet"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<CENTER><BR><BR><H2>Error While Editing Tee Sheet</H2>");
   out.println("<BR><BR>An error has occurred that prevents the system from completing the task.<BR>");
   out.println("<BR>Please try again.  If problem continues, contact ForeTees.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +slotParms.ind+ "&course=" +slotParms.returnCourse+ "\">");
   out.println("Return to Tee Sheet</a></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

 }  // end of doPost



 // *********************************************************
 //  Process insert request from above
 //
 //  parms:  index         = index value for date
 //          course        = name of course
 //          returnCourse  = name of course for return to sheet
 //          jump          = jump index for return
 //          time          = time of tee time
 //          fb            = f/b indicator
 //          insertSubmit  = if from self
 //          first         = if first tee time
 //
 // *********************************************************

 private void doInsert(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {


   ResultSet rs = null;

   //
   //  variables for this method
   //
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int hr = 0;
   int min = 0;
   int ampm = 0;
   int fb = 0;
   int time = 0;
   int otime = 0;
   int index = 0;
   int event_type = 0;
   int notify_id = 0;

   String event = "";
   String event_color = "";
   String rest = "";
   String rest2 = "";
   String rest_color = "";
   String rest_color2 = "";
   String rest_recurr = "";
   String rest5 = "";                      // default values
   String rest52 = "";
   String rest5_color = "";
   String rest5_color2 = "";
   String rest5_recurr = "";
   String lott = "";                      // lottery name
   String lott2 = "";                      // lottery name
   String lott_color = "";
   String lott_color2 = "";
   String lott_recurr = "";

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };


   //
   //    The 'index' paramter contains an index value
   //    (0 = today, 1 = tomorrow, etc.)
   //
   String indexs = req.getParameter("index");         //  index value of the day
   String course = req.getParameter("course");        //  get the course name for this insert
   String returnCourse = req.getParameter("returnCourse");     //  get the course name for this sheet
   String jump = req.getParameter("jump");            //  get the jump index
   String sfb = req.getParameter("fb");
   String times = req.getParameter("time");            //  get the tee time selected (hhmm)
   String first = req.getParameter("first");           //  get the first tee time indicator (yes or no)
   String emailOpt = req.getParameter("email");        //  get the email option from _sheet
   String snid = snid = req.getParameter("notifyId");
   
   // get mode if passed (if no mode, then default to edit tee sheet & notification actions)
   String mode = (req.getParameter("mode") == null) ? "" : req.getParameter("mode");
   
   // get name if passed (name passed when working on events & lotteries)
   String name = (req.getParameter("name") == null) ? "" : req.getParameter("name");
   
   String hideUnavail = req.getParameter("hide");
   if (hideUnavail == null || !hideUnavail.equals("1")) hideUnavail = "0";
   
   if (course == null) course = "";
   if (snid == null) snid = "";
   if (times == null) times = "";
   if (sfb == null) sfb = "";
   
   //
   //  Convert the index value from string to int
   //
   try {
       
      time = Integer.parseInt(times);
      index = Integer.parseInt(indexs);
      fb = Integer.parseInt(sfb);
      notify_id = Integer.parseInt(snid);
   }
   catch (NumberFormatException e) { }

   //
   //  isolate hr and min values
   //
   hr = time / 100;
   min = time - (hr * 100);

   //
   //  Get today's date and then use the value passed to locate the requested date
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   cal.add(Calendar.DATE,index);                  // roll ahead (or back) 'index' days

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH) + 1;
   day = cal.get(Calendar.DAY_OF_MONTH);
   day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

   String day_name = day_table[day_num];         // get name for day
   
   long date = year * 10000;                     // create a date field of yyyymmdd
   date = date + (month * 100) + day;            // date = yyyymmdd (for comparisons)

   if (req.getParameter("insertSubmit") == null) {      // if not an insert 'submit' request (from self)

      //
      //  Process the initial call to Insert a New Tee Time
      //
      //  Build the HTML page to prompt user for a specific time slot
      //
      out.println(SystemUtils.HeadTitle("Proshop Insert Tee Time Page"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // whole page
      out.println("<tr><td align=\"center\" valign=\"top\">");

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");   // main page
      out.println("<tr><td align=\"center\">");
      out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\" color=\"#000000\">");

      out.println("<font size=\"5\">");
      out.println("<p align=\"center\"><b>Insert Tee Time</b></p></font>");
      out.println("<font size=\"2\">");

      out.println("<table cellpadding=\"5\" align=\"center\" width=\"450\">");
      out.println("<tr><td colspan=\"4\" bgcolor=\"#336633\"><font color=\"#FFFFFF\" size=\"2\">");
      out.println("<b>Instructions:</b>  To insert a tee time for the date shown below, select the time");
      out.println(" and the 'front/back' values.  Select 'Insert' to add the new tee time.");
      out.println("</font></td></tr></table><br>");

      out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"8\" align=\"center\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + indexs + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
      out.println("<input type=\"hidden\" name=\"insert\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");

      out.println("<tr><td width=\"450\">");
         out.println("<font size=\"2\">");
         out.println("<p align=\"left\">");
         out.println("<b>Note:</b> &nbsp;This tee time must be unique from all others on the sheet. &nbsp;");
         out.println("Therefore, at least one of these values must be different than other tee times.");
      out.println("<p align=\"center\">Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b></p>");
      out.println("Time:&nbsp;&nbsp;");
      out.println("<select size=\"1\" name=\"time\">");
      //
      //  Define some variables for this processing
      //
      PreparedStatement pstmt1b = null;
      String dampm = " AM";
      int dhr = hr;
      int i = 0;
      int i2 = 0;
      int mint = min;
      int hrt = hr;
      int last = 0;
      int start = 0;
      int maxtimes = 20;
        
      //
      //  Determine time values to be used for selection 
      //
      loopt:
      while (i < maxtimes) {

         mint++;               // next minute
         if (mint > 59) {

            mint = 0;          // rotate the hour
            hrt++;
         }
         if (hrt > 23) {

            hrt = 23;
            mint = 59;
            break loopt;      // done
         }
         if (i == 0) {                        // if first time
            start = (hrt * 100) + mint;       // save first time for select
         }
         i++;
      }
      last = (hrt * 100) + mint;       // last time for select

      try {

         //
         //   Find the next time - after the time selected - use as the limit for selection list
         //
         pstmt1b = con.prepareStatement (
            "SELECT time FROM teecurr2 " +
            "WHERE date = ? AND time > ? AND time < ? AND fb = ? AND courseName = ? " +
            "ORDER BY time");

         pstmt1b.clearParameters();        // clear the parms
         pstmt1b.setLong(1, date);
         pstmt1b.setInt(2, time);
         pstmt1b.setInt(3, last);
         pstmt1b.setInt(4, fb);
         pstmt1b.setString(5, course);

         rs = pstmt1b.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            last = rs.getInt(1);          // get the first time found - use as upper limit for display
         }
         pstmt1b.close();

      }
      catch (Exception e) {
      }

      i = 0;
        
      //
      //  If first tee time on sheet, then allow 20 tee times prior to this time.
      //
      if (first.equalsIgnoreCase( "yes" )) {

         mint = min;                // get original time
         hrt = hr;

         while (i < maxtimes) {           // determine the first time

            if (mint > 0) {
               mint--;
            } else {               // assume not midnight
               hrt--;
               mint = 59;
            }
            i++;
         }
         start = (hrt * 100) + mint;       // save first time for select
         maxtimes = 40;           // new max for this request
      }

      //
      //  Start with the time selected in case they want a tee time with same time, different f/b
      //
      if (dhr > 11) {

         dampm = " PM";
      }
      if (dhr > 12) {

         dhr = dhr - 12;
      }
      
      out.println("<option value=\"" +time+ "\">" +dhr+ ":" +SystemUtils.ensureDoubleDigit(min)+ " " +dampm+ "</option>");

      //
      //  list tee times that follow the one selected, but less than 'last'
      //
      otime = time;           // save original time value
      i = 0;
      hr = start / 100;             // get values for start time (first in select list)
      min = start - (hr * 100);

      loop1:
      while (i < maxtimes) {

         dhr = hr;            // init as same
         dampm = " AM";

         if (hr == 0) {

            dhr = 12;
         }
         if (hr > 12) {

            dampm = " PM";
            dhr = hr - 12;
         }
         if (hr == 12) {

            dampm = " PM";
         }

         time = (hr * 100) + min;      // set time value

         if (time >= last) {           // if we reached the end

            break loop1;              // done
         }

         if (time != otime) {        // if not same as original time
            
             out.println("<option value=\"" +time+ "\">" +dhr+ ":" +SystemUtils.ensureDoubleDigit(min)+ " " +dampm+ "</option>");
         }
           
         min++;               // next minute
           
         if (min > 59) {

            min = 0;          // rotate the hour
            hr++;
         }
         if (hr > 23) {

            break loop1;      // done
         }

         i++;
      }           // end of while

      out.println("</select>");

      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("Front/Back:&nbsp;&nbsp;");
      out.println("<select size=\"1\" name=\"fb\">");
        out.println("<option value=\"00\">Front</option>");
        out.println("<option value=\"01\">Back</option>");
        out.println("<option value=\"09\">Crossover</option>");
      out.println("</select>");
      out.println("<br><br></p>");
      out.println("<p align=\"center\">");
        out.println("<input type=\"submit\" value=\"Insert\" name=\"insertSubmit\"></p>");
      out.println("</font></td></tr></form></table>");
      out.println("<br><br>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + indexs + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\"></form>");

      //
      //  End of HTML page
      //
      out.println("</td></tr></table>");                           // end of main page
      out.println("</td></tr></table>");                           // end of whole page
      out.println("</center></body></html>");
      out.close();
      
   } else {     // end of Insert Submit processing

      //
      //   Call is from self to process an insert request (submit)
      //
      //   Parms passed:   time = time of the tee time to be inserted
      //                   date = date of the tee sheet
      //                   fb   = the front/back value (see above)
      //                   course = course name
      //                   returnCourse = course name for return

      //
      //  Check to make sure a slot like this doesn't already exist
      //
      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT blocker FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setLong(1, date);
         pstmt1.setInt(2, time);
         pstmt1.setInt(3, fb);
         pstmt1.setString(4, course);

         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            String blocker = rs.getString(1);          // get blocker, if it exists
            
            out.println(SystemUtils.HeadTitle("DB Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center><BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>A tee time with these date, time and F/B values already exists.");
            if (!blocker.equals("")) {
               out.println("<BR>It is currently blocked, so it does not show on the tee sheet.");
            }
            out.println("<BR>One of these values must change so the tee time is unique.");
            out.println("<BR><BR>Please try again.");
            out.println("<BR><BR>");
            out.println("<a href=\"javascript:history.back(1)\">Return</a>");
            out.println("</center></BODY></HTML>");
            return;

         }    // ok if we get here - not matching time slot

         pstmt1.close();   // close the stmt

      }
      catch (Exception ignore) {   // this is good if no match found
         
         out.println(SystemUtils.HeadTitle("DB Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center><BR><BR><H3>Data Processing Error</H3>");
         out.println("<BR><BR>Sorry, an error occurred while processing your request.");
         out.println("<BR><BR>Please try again.");
         out.println("<BR>If problem continues, contact support and provide this message.");
         out.println("<BR><BR>Date=" +date+ ", Time=" +time+ ", FB=" +fb+ ", Course=" +course);
         out.println("<BR><BR>");
         out.println("<a href=\"javascript:history.back(1)\">Return</a>");
         out.println("</center></BODY></HTML>");
         return;
      }

      //
      //  This slot is unique - insert it now
      //
      try {

         SystemUtils.insertTee(date, time, fb, course, day_name, con);     // insert new tee time

      }
      catch (Exception e1) {

         out.println(SystemUtils.HeadTitle("DB Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center><BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Unable to access the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>Error in Proshop_dsheet: " + e1.getMessage());
         out.println("<BR><BR>");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
         out.println("<input type=\"submit\" value=\"Return to Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("</center></BODY></HTML>");
         out.close();
         return;
      }

      //
      //  Insert complete - inform user
      //
      String sampm = "AM";
      if (hr > 11) {        // if PM

         sampm = "PM";
      }

      out.println("<HTML><HEAD><Title>Proshop Insert Confirmation</Title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_dsheet?mode=" +mode+ "&name=" +name+ "&index=" +indexs+ "&course=" +returnCourse+ "&jump=" +jump+ "&email=" +emailOpt+ "\">");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center><BR><BR><H3>Insert Tee Time Confirmation</H3>");
      out.println("<BR><BR>Thank you, the following tee time has been added.");
      if (hr > 12) {
         hr = hr - 12;        // convert back to conventional time
      }
      
      out.println("<BR><BR>Date & Time:  <b>" + day_name + " " + month + "/" + day + "/" + year + " " + hr + ":" + SystemUtils.ensureDoubleDigit(min) + " " + sampm + "</b>");
      
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + indexs + "\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
      out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
      out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\"></form>");
      out.println("</center></BODY></HTML>");
      
      //
      // Refresh the blockers in case the tee time added is covered by a blocker
      //
      //  6/19/07 BP - remove this call to blockers as it has caused a couple of problems for pros that try to insert a time during 
      //               a blocker, then they can't find the new time.  I see no reason to block an inserted tee time.  We can change
      //               this for specific clubs in the future, or add a prompt to notify them and ask how they want to proceed.
      //
      //
//      SystemUtils.doBlockers(con);
      
      
      out.close();
   }

 }      // end of doInsert


 // *********************************************************
 //  Process delete request from above
 //
 //  parms:  index         = index value for date
 //          course        = name of course
 //          returnCourse  = name of course for return to sheet
 //          jump          = jump index for return
 //          time          = time of tee time
 //          fb            = f/b indicator
 //
 // *********************************************************

 private void doDelete(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {


   ResultSet rs = null;

   //
   //  variables for this class
   //
   int index = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int fb = 0;
   int hr = 0;
   int min = 0;
   int time = 0;

   String sampm = "AM";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };


   //
   //    The 'index' paramter contains an index value
   //    (0 = today, 1 = tomorrow, etc.)
   //
   String indexs = req.getParameter("index");        //  index value of the day
   String course = req.getParameter("course");       //  get the course name for this delete request
   String returnCourse = req.getParameter("returnCourse");   //  get the course name for this sheet
   String jump = req.getParameter("jump");           //  get the jump index
   String stime = req.getParameter("time");          //  get the time of tee time
   String sfb = req.getParameter("fb");              //  get the fb indicator
   String emailOpt = req.getParameter("email");      //  get the email indicator

   // get mode if passed (if no mode, then default to edit tee sheet & notification actions)
   String mode = (req.getParameter("mode") == null) ? "" : req.getParameter("mode");
   
   // get name if passed (name passed when working on events & lotteries)
   String name = (req.getParameter("name") == null) ? "" : req.getParameter("name");
   
   String hideUnavail = req.getParameter("hide");
   if (hideUnavail == null || !hideUnavail.equals("1")) hideUnavail = "0";
   
   if (course == null) {

      course = "";    // change to null string
   }

   //
   //  Convert the index value from string to int
   //
   try {
      index = Integer.parseInt(indexs);
      fb = Integer.parseInt(sfb);
      time = Integer.parseInt(stime);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  isolate hr and min values
   //
   hr = time / 100;
   min = time - (hr * 100);

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

   long date = year * 10000;                     // create a date field of yyyymmdd
   date = date + (month * 100);
   date = date + day;                            // date = yyyymmdd (for comparisons)

   if (req.getParameter("deleteSubmit") == null) {      // if this is the first request

      //
      //   Call is from 'edit' processing above to start a delete request
      //
      //
      //  Build the HTML page to prompt user for a confirmation
      //
      out.println(SystemUtils.HeadTitle("Proshop Delete Confirmation"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // whole page
      out.println("<tr><td align=\"center\" valign=\"top\">");

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");   // main page
      out.println("<tr><td align=\"center\">");
      out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\" color=\"#000000\"><br><br>");

         out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"8\" align=\"center\">");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
         out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
         out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + indexs + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
         out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
         out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
         out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
         out.println("<input type=\"hidden\" name=\"delete\" value=\"yes\">");

         out.println("<tr><td width=\"450\">");
         out.println("<font size=\"3\">");
         out.println("<p align=\"center\"><b>Delete Confirmation</b></p></font>");
         out.println("<br><font size=\"2\">");

            out.println("<font size=\"2\">");
            out.println("<p align=\"left\">");
            //
            //  Check to see if any players are in this tee time
            //
            try {

               PreparedStatement pstmt1d = con.prepareStatement (
                  "SELECT player1, player2, player3, player4, player5 " +
                  "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               pstmt1d.clearParameters();        // clear the parms
               pstmt1d.setLong(1, date);
               pstmt1d.setInt(2, time);
               pstmt1d.setInt(3, fb);
               pstmt1d.setString(4, course);

               rs = pstmt1d.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  player1 = rs.getString(1);
                  player2 = rs.getString(2);
                  player3 = rs.getString(3);
                  player4 = rs.getString(4);
                  player5 = rs.getString(5);

               } 

               pstmt1d.close();   // close the stmt

            }
            catch (Exception ignore) {   // this is good if no match found
            }
              
            if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) || !player4.equals( "" ) || !player5.equals( "" )) { 
              
               out.println("<b>Warning:</b> &nbsp;You are about to permanently remove a tee time ");
               out.println("which contains the following player(s):<br>");
                 
               if (!player1.equals( "" )) {
         
                  out.println("<br>" +player1);
               }
               if (!player2.equals( "" )) {

                  out.println("<br>" +player2);
               }
               if (!player3.equals( "" )) {

                  out.println("<br>" +player3);
               }
               if (!player4.equals( "" )) {

                  out.println("<br>" +player4);
               }
               if (!player5.equals( "" )) {

                  out.println("<br>" +player5);
               }
               out.println("<br><br>This will remove the entire tee time slot from the database. ");
               out.println(" If you wish to only remove the players, then return to the tee sheet and select the tee time to update it.");
               out.println("</p>");
            } else {
               out.println("<b>Warning:</b> &nbsp;You are about to permanently remove the following tee time.<br><br>");
               out.println("This will remove the entire tee time slot from the database. ");
               out.println(" If you wish to only remove the players, then return to the tee sheet and select the tee time to update it.");
               out.println("</p>");
            }
            //
            //  build the time string
            //
            sampm = "AM";
            if (hr > 11) {        // if PM

               sampm = "PM";
            }
            if (hr > 12) {
               hr = hr - 12;        // convert back to conventional time
            }
            out.println("<p align=\"center\">");
            
            out.println("Date & Time:  <b>" + day_name + " " + month + "/" + day + "/" + year + " " + hr + ":" + SystemUtils.ensureDoubleDigit(min) + " " + sampm + "</b>");
            
              
         out.println("<BR><BR>Are you sure you want to delete this tee time?</p>");

            out.println("<p align=\"center\">");
              out.println("<input type=\"submit\" value=\"Yes - Delete It\" name=\"deleteSubmit\"></p>");
            out.println("</font></td></tr></form></table>");
            out.println("<br><br>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
         out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
         out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + indexs + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
         out.println("<input type=\"hidden\" name=\"email\" value=\"" + emailOpt + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
      out.println("<input type=\"submit\" value=\"No - Back to Edit\" style=\"text-decoration:underline; background:#8B8970\"></form>");

      out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
         out.println("<input type=\"submit\" value=\"No - Return to Tee Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");

      //
      //  End of HTML page
      //
      out.println("</td></tr></table>");                           // end of main page
      out.println("</td></tr></table>");                           // end of whole page
      out.println("</center></body></html>");
      out.close();
      
   } else { 

      //
      //   Call is from self to process a delete request (final - this is the confirmation)
      //
      //  Check to make sure a slot like this already exists
      //
      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT mm FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setLong(1, date);
         pstmt1.setInt(2, time);
         pstmt1.setInt(3, fb);
         pstmt1.setString(4, course);

         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (!rs.next()) {

            out.println(SystemUtils.HeadTitle("DB Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center><BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>A tee time with these date, time and F/B values does not exist.");
            out.println("<BR><BR>Please try again.");
            out.println("<BR><BR>");
            out.println("<a href=\"javascript:history.back(1)\">Return</a>");
            out.println("</center></BODY></HTML>");
            out.close();
            return;

         }    // ok if we get here - matching time slot found

         pstmt1.close();   // close the stmt

      }
      catch (Exception ignore) {   // this is good if no match found
      }

      //
      //  This slot was found - delete it from the database
      //

      try {

         PreparedStatement pstmt2 = con.prepareStatement (
            "DELETE FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.setLong(1, date);
         pstmt2.setInt(2, time);
         pstmt2.setInt(3, fb);
         pstmt2.setString(4, course);

         int count = pstmt2.executeUpdate();      // execute the prepared stmt

         pstmt2.close();   // close the stmt

      }
      catch (Exception e1) {

         out.println(SystemUtils.HeadTitle("DB Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center><BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Unable to access the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>" + e1.getMessage());
         out.println("<BR><BR>");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"submit\" value=\"Return to Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("</center></BODY></HTML>");
         out.close();
         return;
      }
      //
      //  Delete complete - inform user
      //
      sampm = "AM";
      if (hr > 11) {        // if PM

         sampm = "PM";
      }
      if (hr > 12) {
         hr = hr - 12;        // convert back to conventional time
      }

      out.println("<HTML><HEAD><title>Proshop Delete Confirmation</title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_dsheet?mode=" +mode+ "&name=" +name+ "&index=" +indexs+ "&course=" +returnCourse+ "&jump=" +jump+ "&email=" +emailOpt+ "&jump=" +jump+ "\">");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");
      out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
      out.println("><BR><BR><H3>Delete Tee Time Confirmation</H3>");
      out.println("<BR><BR>Thank you, the following tee time has been removed.");
      if (hr > 12) {
         hr = hr - 12;        // convert back to conventional time
      }
      
      out.println("<BR><BR>Date & Time:  <b>" + day_name + " " + month + "/" + day + "/" + year + " " + hr + ":" + SystemUtils.ensureDoubleDigit(min) + " " + sampm + "</b>");
      
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
         out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
         out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + indexs + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
         out.println("<input type=\"hidden\" name=\"email\" value=\"" + emailOpt + "\">");
      out.println("<input type=\"submit\" value=\"Back to Edit\" style=\"text-decoration:underline; background:#8B8970\"></form>");
      out.println("</center></BODY></HTML>");
      out.close();
   }
 }      // end of doDelete

 
 // *********************************************************
 //  Process a delete request for a notification
 //  
 //  Parms: notifyId    = uid for notitification to be deleted
 //
 // *********************************************************
 
 private void doDeleteNotifyReq(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {

     
    String sindex = req.getParameter("index");        //  index value of the day
    String returnCourse = req.getParameter("returnCourse");   //  get the course name for this sheet
    String jump = req.getParameter("jump");           //  get the jump index
    String emailOpt = req.getParameter("email");      //  get the email indicator
    String snid = req.getParameter("notifyId");
    
    // get mode if passed (if no mode, then default to edit tee sheet & notification actions)
    String mode = (req.getParameter("mode") == null) ? "" : req.getParameter("mode");
    
    String hideUnavail = req.getParameter("hide");
    if (hideUnavail == null || !hideUnavail.equals("1")) hideUnavail = "0";
   
    int index = 0;
    int notify_id = 0;
    if (snid == null) snid = "";

    //
    //  Convert the index value from string to int
    //
    try {

        index = Integer.parseInt(sindex);
        notify_id = Integer.parseInt(snid);
    }
    catch (NumberFormatException e) { }
    
    try {

        PreparedStatement pstmt2 = con.prepareStatement (
        "DELETE FROM notifications WHERE notification_id = ?");

        pstmt2.clearParameters();
        pstmt2.setInt(1, notify_id);
        pstmt2.executeUpdate();

        pstmt2 = con.prepareStatement (
        "DELETE FROM notifications_players WHERE notification_id = ?");

        pstmt2.clearParameters();
        pstmt2.setInt(1, notify_id);
        pstmt2.executeUpdate();

        pstmt2.close();   // close the stmt

    }
    catch (Exception e1) {

        out.println(SystemUtils.HeadTitle("DB Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<center><BR><BR><H3>Database Access Error</H3>");
        out.println("<BR><BR>Unable to access the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>" + e1.getMessage());
        out.println("<BR><BR>");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
        out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
        out.println("<input type=\"submit\" value=\"Return to Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form>");
        out.println("</center></BODY></HTML>");
        out.close();
        return;
    }

    out.println("<HTML><HEAD><title>Proshop Delete Confirmation</title>");
    out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_dsheet?index=" +index+ "&course=" +returnCourse+ "&jump=" +jump+ "&email=" +emailOpt+ "&jump=" +jump+ "\">");
    out.println("</HEAD>");
    out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
    out.println("<center>");
    out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
    out.println("<BR><BR><H3>Delete Notification Confirmation</H3>");
    out.println("<BR><BR>Thank you, the notification has been removed.");
    out.println("<BR><BR>");
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
        out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
        out.println("<input type=\"submit\" value=\"Back to Edit\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form>");
    out.println("</center></BODY></HTML>");
    out.close();
         
 }
 

 
 // *********************************************************
 //  Process a delete request for a lottery
 //  
 //  Parms: lotteryId    = uid for request to be deleted
 //
 // *********************************************************
 
 private void doDeleteLottReq(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {

     
    String sindex = req.getParameter("index");        //  index value of the day
    String returnCourse = req.getParameter("returnCourse");   //  get the course name for this sheet
    String jump = req.getParameter("jump");           //  get the jump index
    String emailOpt = req.getParameter("email");      //  get the email indicator
    String name = req.getParameter("name");
    String hide = req.getParameter("hide");
    String slid = req.getParameter("lotteryId");
    
    int index = 0;
    int lottery_id = 0;
    if (slid == null) slid = "";

    //
    //  Convert the index value from string to int
    //
    try {

        index = Integer.parseInt(sindex);
        lottery_id = Integer.parseInt(slid);
    }
    catch (NumberFormatException e) { }
    
    try {

        PreparedStatement pstmt2 = con.prepareStatement (
        "DELETE FROM lreqs3 WHERE id = ?");

        pstmt2.clearParameters();
        pstmt2.setInt(1, lottery_id);
        pstmt2.executeUpdate();
        pstmt2.close();

    }
    catch (Exception e1) {

        out.println(SystemUtils.HeadTitle("DB Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<center><BR><BR><H3>Database Access Error</H3>");
        out.println("<BR><BR>Unable to access the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>" + e1.getMessage());
        out.println("<BR><BR>");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"LOTT\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
        out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
        out.println("<input type=\"submit\" value=\"Return to Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form>");
        out.println("</center></BODY></HTML>");
        out.close();
        return;
    }

    out.println("<HTML><HEAD><title>Proshop Delete Confirmation</title>");
    out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_dsheet?mode=LOTT&index=" +index+ "&course=" +returnCourse+ "&email=" +emailOpt+ "&jump=" +jump+ "&hide=" +hide+ "&name=" +name+ "\">");
    out.println("</HEAD>");
    out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
    out.println("<center>");
    out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
    out.println("<BR><BR><H3>Delete Lottery Request Confirmation</H3>");
    out.println("<BR><BR>Thank you, the request has been removed.");
    out.println("<BR><BR>");
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"LOTT\">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
        out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
        out.println("<input type=\"submit\" value=\"Back to Edit\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form>");
    out.println("</center></BODY></HTML>");
    out.close();
         
 }
 
 
 // *********************************************************
 //  Process a delete request for a event signup
 //  
 //  Parms: eventId    = uid for request to be deleted
 //
 // *********************************************************
 
 private void doDeleteEventReq(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {

     
    String sindex = req.getParameter("index");              //  index value of the day
    String returnCourse = req.getParameter("returnCourse"); //  get the course name for this sheet
    String jump = req.getParameter("jump");                 //  get the jump index
    String emailOpt = req.getParameter("email");            //  get the email indicator
    String name = req.getParameter("name");
    String hide = req.getParameter("hide");
    String slid = req.getParameter("eventId");
    
    int index = 0;
    int event_id = 0;
    if (slid == null) slid = "";

    //
    //  Convert the index value from string to int
    //
    try {

        index = Integer.parseInt(sindex);
        event_id = Integer.parseInt(slid);
    }
    catch (NumberFormatException e) { }
    
    try {

       // PreparedStatement pstmt2 = con.prepareStatement (
       // "DELETE FROM evntsup2b WHERE id = ?");

        PreparedStatement pstmt2 = con.prepareStatement (
        "UPDATE evntsup2b SET inactive = 1 WHERE id = ?");

        pstmt2.clearParameters();
        pstmt2.setInt(1, event_id);
        pstmt2.executeUpdate();
        pstmt2.close();

    }
    catch (Exception e1) {

        out.println(SystemUtils.HeadTitle("DB Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<center><BR><BR><H3>Database Access Error</H3>");
        out.println("<BR><BR>Unable to access the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>" + e1.getMessage());
        out.println("<BR><BR>");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"EVENT\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
        out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
        out.println("<input type=\"submit\" value=\"Return to Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form>");
        out.println("</center></BODY></HTML>");
        out.close();
        return;
    }

    out.println("<HTML><HEAD><title>Proshop Delete Confirmation</title>");
    out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_dsheet?mode=EVENT&index=" +index+ "&course=" +returnCourse+ "&email=" +emailOpt+ "&jump=" +jump+ "&hide=" +hide+ "&name=" +name+ "\">");
    out.println("</HEAD>");
    out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
    out.println("<center>");
    out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
    out.println("<BR><BR><H3>Delete Event Signup Confirmation</H3>");
    out.println("<BR><BR>Thank you, the request has been removed.");
    out.println("<BR><BR>");
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"EVENT\">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
        out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
        out.println("<input type=\"submit\" value=\"Back to Edit\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form>");
    out.println("</center></BODY></HTML>");
    out.close();
         
 }
         
 
 // *********************************************************
 //  Process a delete signup for a wait list
 //  
 //  Parms: wlsId    = uid for request to be deleted
 //
 // *********************************************************
 
 private void doDeleteWaitReq(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {

     
    String sindex = req.getParameter("index");              //  index value of the day
    String returnCourse = req.getParameter("returnCourse"); //  get the course name for this sheet
    String jump = req.getParameter("jump");                 //  get the jump index
    String emailOpt = req.getParameter("email");            //  get the email indicator
    String name = req.getParameter("name");
    String hide = req.getParameter("hide");
    String swid = req.getParameter("wlsId");
    
    int index = 0;
    int wls_id = 0;
    if (swid == null) swid = "";

    //
    //  Convert the index value from string to int
    //
    try {

        index = Integer.parseInt(sindex);
        wls_id = Integer.parseInt(swid);
    }
    catch (NumberFormatException e) { }
    
    try {

        PreparedStatement pstmt2 = con.prepareStatement (
            "DELETE FROM wait_list_signups WHERE wait_list_signup_id = ?");

        pstmt2.clearParameters();
        pstmt2.setInt(1, wls_id);
        pstmt2.executeUpdate();

        pstmt2 = con.prepareStatement (
            "DELETE FROM wait_list_signups_players WHERE wait_list_signup_id = ?");

        pstmt2.clearParameters();
        pstmt2.setInt(1, wls_id);
        pstmt2.executeUpdate();

        pstmt2.close();   // close the stmt

    }
    catch (Exception e1) {

        out.println(SystemUtils.HeadTitle("DB Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<center><BR><BR><H3>Database Access Error</H3>");
        out.println("<BR><BR>Unable to access the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>" + e1.getMessage());
        out.println("<BR><BR>");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
        out.println("<input type=\"submit\" value=\"Return to Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form>");
        out.println("</center></BODY></HTML>");
        out.close();
        return;
    }

    out.println("<HTML><HEAD><title>Proshop Delete Confirmation</title>");
    out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_dsheet?mode=WAITLIST&index=" +index+ "&course=" +returnCourse+ "&email=" +emailOpt+ "&jump=" +jump+ "&hide=" +hide+ "&name=" +name+ "\">");
    out.println("</HEAD>");
    out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
    out.println("<center>");
    out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
    out.println("<BR><BR><H3>Delete Wait List Signup Confirmation</H3>");
    out.println("<BR><BR>Thank you, the request has been removed.");
    out.println("<BR><BR>");
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
        out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\">");
        out.println("<input type=\"submit\" value=\"Back to Edit\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form>");
    out.println("</center></BODY></HTML>");
    out.close();
         
 }
 
 
 // *********************************************************
 //  changeCW - change the C/W option for 1 or all players in tee time.  
 //
 //  parms:
 //          jump        = jump index for return
 //          from_player = player position being changed (1-5) 
 //          from_time   = tee time being changed
 //          from_fb     = f/b of tee time
 //          from_course = name of course
 //          to_from     = current C/W option 
 //          to_to       = new C/W option
 //          changeAll   = change all players in slot (true or false)
 //          ninehole    = use 9 Hole options (true or false)
 //
 // *********************************************************

 private void changeCW(parmSlot slotParms, String changeAll, String ninehole, long date, HttpServletRequest req, PrintWriter out, Connection con, HttpServletResponse resp) {


   ResultSet rs = null;

   int in_use = 0;
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
   String newcw = "";

   // get mode if passed (if no mode, then default to edit tee sheet & notification actions)
   String mode = (req.getParameter("mode") == null) ? "" : req.getParameter("mode");

   out.println("<!-- changeCW -->");
   
   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.from_time == 0 || slotParms.from_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_dsheet.changeCW - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.from_time+ ", course= " +slotParms.from_course+ ", fb= " +slotParms.from_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      in_use = 1;          // make like the time is busy
      out.println("<!-- Faking busy: " + msg + " -->");

   } else {               // continue if parms ok

      //
      //  Check if the requested tee time is currently in use
      //
      try {
         out.println("<!-- calling checkInUse("+date+", "+slotParms.from_time+", "+slotParms.from_fb+", "+slotParms.from_course+", "+slotParms.user+") -->");
         in_use = verifySlot.checkInUse(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms.user, slotParms, con);
         out.println("<!-- in_use=" + in_use + " -->");
         
      }
      catch (Exception e1) {

         String eMsg = "Error 1 in changeCW. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }

   if (in_use != 0) {              // if time slot already in use

      teeBusy(out, slotParms, mode);
      return;
   }

   //
   //  Ok - get current player info from the parm block (set by checkInUse)
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
   p91 = slotParms.p91;
   p92 = slotParms.p92;
   p93 = slotParms.p93;
   p94 = slotParms.p94;
   p95 = slotParms.p95;
     
   //
   //  If '9 Hole' option selected, then change new C/W to 9 hole type
   //
   newcw = slotParms.to_to;            // get selected C/W option
     
   //
   //  Set the new C/W value for each player requested
   //
   if (!player1.equals( "" )) {

      if ((changeAll.equals( "true" )) || (slotParms.from_player == 1)) {   // change this one?

         p1cw = newcw;
         if (ninehole.equals( "true" )) {
            p91 = 1;             // make it a 9 hole type
         } else {
            p91 = 0;             // make it 18 hole 
         }
      }
   }
   if (!player2.equals( "" )) {

      if ((changeAll.equals( "true" )) || (slotParms.from_player == 2)) {   // change this one?

         p2cw = newcw;
         if (ninehole.equals( "true" )) {
            p92 = 1;             // make it a 9 hole type
         } else {
            p92 = 0;             // make it 18 hole
         }
      }
   }
   if (!player3.equals( "" )) {

      if ((changeAll.equals( "true" )) || (slotParms.from_player == 3)) {   // change this one?

         p3cw = newcw;
         if (ninehole.equals( "true" )) {
            p93 = 1;             // make it a 9 hole type
         } else {
            p93 = 0;             // make it 18 hole
         }
      }
   }
   if (!player4.equals( "" )) {

      if ((changeAll.equals( "true" )) || (slotParms.from_player == 4)) {   // change this one?

         p4cw = newcw;
         if (ninehole.equals( "true" )) {
            p94 = 1;             // make it a 9 hole type
         } else {
            p94 = 0;             // make it 18 hole
         }
      }
   }
   if (!player5.equals( "" )) {

      if ((changeAll.equals( "true" )) || (slotParms.from_player == 5)) {   // change this one?

         p5cw = newcw;
         if (ninehole.equals( "true" )) {
            p95 = 1;             // make it a 9 hole type
         } else {
            p95 = 0;             // make it 18 hole
         }
      }
   }
     
   //
   //  Update the tee time and set it no longer in use
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "UPDATE teecurr2 SET " +
         "p1cw=?, p2cw=?, p3cw=?, p4cw=?, in_use=0, p5cw=?, p91=?, p92=?, p93=?, p94=?, p95=? " +
         "WHERE date=? AND time=? AND fb=? AND courseName=?");

      pstmt1.clearParameters();          // clear the parms
      pstmt1.setString(1, p1cw);      
      pstmt1.setString(2, p2cw);
      pstmt1.setString(3, p3cw);
      pstmt1.setString(4, p4cw);
      pstmt1.setString(5, p5cw);
      pstmt1.setInt(6, p91);
      pstmt1.setInt(7, p92);
      pstmt1.setInt(8, p93);
      pstmt1.setInt(9, p94);
      pstmt1.setInt(10, p95);
      pstmt1.setLong(11, date);
      pstmt1.setInt(12, slotParms.from_time);
      pstmt1.setInt(13, slotParms.from_fb);
      pstmt1.setString(14, slotParms.from_course);
      pstmt1.executeUpdate();            // execute the prepared stmt

      pstmt1.close();
   }
   catch (Exception e1) {

      String eMsg = "Error 2 in changeCW. ";
      dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
      return;
   }

   //
   //  Done - return 
   //
   editDone(out, slotParms, resp, mode);

 }      // end of changeCW


 // *********************************************************
 //  changeFB - change the F/B option for the tee time specified.
 //
 //  parms: 
 //          jump        = jump index for return
 //          from_time   = tee time being changed
 //          from_fb     = current f/b of tee time
 //          from_course = name of course
 //          to_fb       = new f/b of tee time
 //
 // *********************************************************

 private void changeFB(parmSlot slotParms, long date, HttpServletRequest req, PrintWriter out, Connection con, HttpServletResponse resp) {


   ResultSet rs = null;

   int in_use = 0;

   // get mode if passed (if no mode, then default to edit tee sheet & notification actions)
   String mode = (req.getParameter("mode") == null) ? "" : req.getParameter("mode");
   
   out.println("<!-- changeFB -->");
   
   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.from_time == 0 || slotParms.from_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_dsheet.changeFB - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.from_time+ ", course= " +slotParms.from_course+ ", fb= " +slotParms.from_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      out.print("parm: " + msg);
      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Check if the requested tee time is currently in use
      //
      try {

         in_use = verifySlot.checkInUse(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms.user, slotParms, con);

      }
      catch (Exception e1) {

         String eMsg = "Error 1 in changeFB. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }

   if (in_use != 0) {              // if time slot already in use

      teeBusy(out, slotParms, mode);
      return;
   }

   //
   //  Ok, tee time not busy - change the F/B
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "UPDATE teecurr2 SET in_use = 0, fb = ? " +
         "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt1.clearParameters();          // clear the parms
      pstmt1.setInt(1, slotParms.to_fb);
      pstmt1.setLong(2, date);
      pstmt1.setInt(3, slotParms.from_time);
      pstmt1.setInt(4, slotParms.from_fb);
      pstmt1.setString(5, slotParms.from_course);
      pstmt1.executeUpdate();            // execute the prepared stmt

      pstmt1.close();
   }
   catch (Exception e1) {

      String eMsg = "Error 2 in changeFB. ";
      dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
      return;
   }

   //
   //  Done - return
   //
   editDone(out, slotParms, resp, mode);

 }      // end of changeFB


 // *********************************************************
 //  moveWhole - move an entire tee time
 //
 //  parms:
 //          jump        = jump index for return
 //          from_time   = tee time being moved
 //          from_fb     = f/b of tee time being moved
 //          from_course = name of course of tee time being moved
 //          to_time     = tee time to move to
 //          to_fb       = f/b of tee time to move to
 //          to_course   = name of course of tee time to move to
 //
 //          prompt      = null if first call here
 //                      = 'return' if user wants to return w/o changes
 //                      = 'continue' if user wants to continue with changes
 //          skip        = verification process to skip if 2nd return
 //
 // *********************************************************

 private void moveWhole(parmSlot slotParms, long date, String prompt, int skip, HttpServletRequest req, 
                        PrintWriter out, Connection con, HttpServletResponse resp) {


   ResultSet rs = null;
     
   int in_use = 0;

   String p1 = "";
   String p2 = "";
   String p3 = "";
   String p4 = "";
   String p5 = "";
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
   String orig_by = "";
   String conf = "";
   String notes = "";
   String custom_disp1 = "";
   String custom_disp2 = "";
   String custom_disp3 = "";
   String custom_disp4 = "";
   String custom_disp5 = "";
   String tflag1 = "";
   String tflag2 = "";
   String tflag3 = "";
   String tflag4 = "";
   String tflag5 = "";
   String customMsg = "";

   short pos1 = 0;
   short pos2 = 0;
   short pos3 = 0;
   short pos4 = 0;
   short pos5 = 0;
   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;
     
   int hide = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int fives = 0;
   int sendemail = 0;
   int guest_id1 = 0;
   int guest_id2 = 0;
   int guest_id3 = 0;
   int guest_id4 = 0;
   int guest_id5 = 0;

   float hndcp1 = 0;
   float hndcp2 = 0;
   float hndcp3 = 0;
   float hndcp4 = 0;
   float hndcp5 = 0;

   boolean error = false;
   
   boolean overrideAccess = SystemUtils.verifyProAccess(req, "REST_OVERRIDE", con, out);
     
   // get mode if passed (if no mode, then default to edit tee sheet & notification actions)
   String mode = (req.getParameter("mode") == null) ? "" : req.getParameter("mode");
   
   out.println("<!-- moveWhole -->");
   
   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.from_time == 0 || slotParms.from_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_dsheet.moveWhole - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.from_time+ ", course= " +slotParms.from_course+ ", fb= " +slotParms.from_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      out.println("<p>" + msg + "</p>");
      
      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Check if the requested tee time is currently in use (the FROM tee time)
      //
      try {

         //
         //  If we got here by returning from a prompt below, then tee time is already busy
         //
         if (!prompt.equals( "" )) {        // if return, then tee time is already busy

            in_use = 0;
              
            getTeeTimeData(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms, con);

         } else {

            in_use = verifySlot.checkInUse(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms.user, slotParms, con);
         }

      }
      catch (Exception e1) {

         String eMsg = "Error 1 in moveWhole. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }

   if (in_use != 0) {                 // if time slot already in use

      teeBusy(out, slotParms, mode);       // reject as busy
      return;
   }

   //
   //  Ok - get current 'FROM' player info from the parm block (set by checkInUse) and save it
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
   user1 = slotParms.user1;
   user2 = slotParms.user2;
   user3 = slotParms.user3;
   user4 = slotParms.user4;
   user5 = slotParms.user5;
   hndcp1 = slotParms.hndcp1;
   hndcp2 = slotParms.hndcp2;
   hndcp3 = slotParms.hndcp3;
   hndcp4 = slotParms.hndcp4;
   hndcp5 = slotParms.hndcp5;
   show1 = slotParms.show1;
   show2 = slotParms.show2;
   show3 = slotParms.show3;
   show4 = slotParms.show4;
   show5 = slotParms.show5;
   pos1 = slotParms.pos1;
   pos2 = slotParms.pos2;
   pos3 = slotParms.pos3;
   pos4 = slotParms.pos4;
   pos5 = slotParms.pos5;
   mNum1 = slotParms.mNum1;
   mNum2 = slotParms.mNum2;
   mNum3 = slotParms.mNum3;
   mNum4 = slotParms.mNum4;
   mNum5 = slotParms.mNum5;
   userg1 = slotParms.userg1;
   userg2 = slotParms.userg2;
   userg3 = slotParms.userg3;
   userg4 = slotParms.userg4;
   userg5 = slotParms.userg5;
   notes = slotParms.notes;
   hide = slotParms.hide;
   orig_by = slotParms.orig_by;
   conf = slotParms.conf;
   p91 = slotParms.p91;
   p92 = slotParms.p92;
   p93 = slotParms.p93;
   p94 = slotParms.p94;
   p95 = slotParms.p95;
   custom_disp1 = slotParms.custom_disp1;
   custom_disp2 = slotParms.custom_disp2;
   custom_disp3 = slotParms.custom_disp3;
   custom_disp4 = slotParms.custom_disp4;
   custom_disp5 = slotParms.custom_disp5;
   tflag1 = slotParms.tflag1;
   tflag2 = slotParms.tflag2;
   tflag3 = slotParms.tflag3;
   tflag4 = slotParms.tflag4;
   tflag5 = slotParms.tflag5;
   guest_id1 = slotParms.guest_id1;
   guest_id2 = slotParms.guest_id2;
   guest_id3 = slotParms.guest_id3;
   guest_id4 = slotParms.guest_id4;
   guest_id5 = slotParms.guest_id5;

   slotParms.player1 = "";       // init parmSlot player fields (verifySlot will fill) 
   slotParms.player2 = "";
   slotParms.player3 = "";
   slotParms.player4 = "";
   slotParms.player5 = "";

   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.to_time == 0 || slotParms.to_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_dsheet.moveWhole2 - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.to_time+ ", course= " +slotParms.to_course+ ", fb= " +slotParms.to_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Now check if the 'TO' tee time is currently in use (this will put its info in slotParms)
      //
      try {

         //
         //  If we got here by returning from a prompt below, then tee time is already busy
         //
         if (!prompt.equals( "" )) {        // if return, tee time already busy

            in_use = 0;

            getTeeTimeData(date, slotParms.to_time, slotParms.to_fb, slotParms.to_course, slotParms, con);

         } else {

            in_use = verifySlot.checkInUse(date, slotParms.to_time, slotParms.to_fb, slotParms.to_course, slotParms.user, slotParms, con);
         }

      }
      catch (Exception e1) {

         String eMsg = "Error 2 in moveWhole. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }


   //
   //  If 'TO' tee time is in use 
   //
   if (in_use != 0) {   

      //
      //  Error - We must free up the 'FROM' tee time
      //
      in_use = 0;

      try {

         PreparedStatement pstmt4 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt4.clearParameters();        // clear the parms
         pstmt4.setInt(1, in_use);
         pstmt4.setLong(2, date);
         pstmt4.setInt(3, slotParms.from_time);
         pstmt4.setInt(4, slotParms.from_fb);
         pstmt4.setString(5, slotParms.from_course);
         pstmt4.executeUpdate();      // execute the prepared stmt
         pstmt4.close();

      }
      catch (Exception ignore) {
      }

      teeBusy(out, slotParms, mode);
      return;
   }

   //
   //  If user was prompted and opted to return w/o changes, then we must clear the 'in_use' flags
   //  before returning to the tee sheet.
   //
   if (prompt.equals( "return" )) {        // if prompt specified a return

      in_use = 0;

      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();
         pstmt1.setInt(1, in_use);
         pstmt1.setLong(2, date);
         pstmt1.setInt(3, slotParms.from_time);
         pstmt1.setInt(4, slotParms.from_fb);
         pstmt1.setString(5, slotParms.from_course);

         pstmt1.executeUpdate();
         pstmt1.close();

         
         pstmt1 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setInt(1, in_use);
         pstmt1.setLong(2, date);
         pstmt1.setInt(3, slotParms.to_time);
         pstmt1.setInt(4, slotParms.to_fb);
         pstmt1.setString(5, slotParms.to_course);

         pstmt1.executeUpdate();      // execute the prepared stmt
         pstmt1.close();

      }
      catch (Exception ignore) {
      }

      // return to Proshop_dsheet

      out.println("<HTML><HEAD><Title>Proshop Edit Tee Sheet Complete</Title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_dsheet?mode=" +mode+ "&name=" +slotParms.name+ "&index=" + slotParms.ind + "&course=" + slotParms.returnCourse + "&email=" + slotParms.sendEmail + "&jump=" + slotParms.jump + "\">");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR>");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<BR><BR><H2>Return Accepted</H2>");
      out.println("<BR><BR>Thank you, click Return' below if this does not automatically return.<BR>");
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + slotParms.ind + ">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
      out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\"></form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

   } else {    // not a 'return' response from prompt 

      //
      //  This is either the first time here, or a 'Continue' reply to a prompt
      //
      //
      p1 = slotParms.player1;      // get players' names for easier reference
      p2 = slotParms.player2;
      p3 = slotParms.player3;
      p4 = slotParms.player4;
      p5 = slotParms.player5;


      //
      //  If any skips are set, then we've already been through here.
      //
      if (skip == 0) {

         //
         //  Check if 'TO' tee time is empty
         //
         if (!p1.equals( "" ) || !p2.equals( "" ) || !p3.equals( "" ) || !p4.equals( "" ) || !p5.equals( "" )) {

            //
            //  Tee time is occupied - inform user and ask to continue or cancel
            //
            out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Tee Time is Occupied</H3><BR>");
            out.println("<BR>WARNING: The tee time you are trying to move TO is already occupied.");

            customMsg = "If you continue, this tee time will effectively be cancelled." +
                    "<BR><BR>Would you like to continue and overwrite this tee time?";
            
            out.println("<BR><BR>Course = " +slotParms.to_course+ ", p1= " +p1+ ", p2= " +p2+ ", p3= " +p3+ ", p4= " +p4+ ", p5= " +p5+ ".");
            out.println("<BR><BR>");

            //
            //  Return to _insert as directed
            //
            /*
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"1\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
             */

            returnToDsheet(out, slotParms, mode, date, "", customMsg, overrideAccess, 1);
            return;
         }
      }
        
      //
      //  check if we are to skip this test
      //
      if (skip < 2) {
        
         //
         // *******************************************************************************
         //  Check member restrictions in 'TO' tee time, but 'FROM' players
         //
         //     First, find all restrictions within date & time constraints on this course.
         //     Then, find the ones for this day.
         //     Then, find any for this member type or membership type (all 5 players).
         //
         // *******************************************************************************
         //

         //
         //  allocate and setup new parm block to hold the tee time parms for this process
         //
         parmSlot slotParms2 = new parmSlot();          // allocate a parm block

         slotParms2.date = date;                 // get 'TO' info
         slotParms2.time = slotParms.to_time;
         slotParms2.course = slotParms.to_course;
         slotParms2.fb = slotParms.to_fb;
         slotParms2.day = slotParms.day;
            
         slotParms2.player1 = player1;          // get 'FROM' info      
         slotParms2.player2 = player2;
         slotParms2.player3 = player3;
         slotParms2.player4 = player4;
         slotParms2.player5 = player5;

         slotParms2.guest_id1 = guest_id1;
         slotParms2.guest_id2 = guest_id2;
         slotParms2.guest_id3 = guest_id3;
         slotParms2.guest_id4 = guest_id4;
         slotParms2.guest_id5 = guest_id5;
         
         try {

            verifySlot.parseGuests(slotParms2, con);     // check for guests and set guest types

            error = verifySlot.parseNames(slotParms2, "pro");   // get the names (lname, fname, mi)

            verifySlot.getUsers(slotParms2, con);        // get the mship and mtype info (needs lname, fname, mi)

            error = false;                               // init error indicator

            error = verifySlot.checkMemRests(slotParms2, con);      // check restrictions  

         }
         catch (Exception ignore) {
         }                           

         if (error == true) {          // if we hit on a restriction
           
            //
            //  Prompt user to see if he wants to override this violation 
            //
            out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>" + slotParms2.player + "</b> is restricted from playing during this time.<br><br>");
            out.println("This time slot has the following restriction:  <b>" + slotParms2.rest_name + "</b><br><br>");
            /*
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");

            //
            //  Return to _dsheet as directed
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"2\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
             */

            returnToDsheet(out, slotParms, mode, date, "", "", overrideAccess, 2);
            return;
         }
      }

      //
      //  check if we are to skip this test
      //
      if (skip < 3) {

         //
         // *******************************************************************************
         //  Check 5-some restrictions - use 'FROM' player5 and 'TO' tee time slot
         //
         //   If 5-somes are restricted during this tee time, warn the proshop user.
         // *******************************************************************************
         //
         if ((!player5.equals( "" )) && (!slotParms.rest5.equals( "" ))) { // if 5-somes restricted prompt user to skip test

            //
            //  Prompt user to see if he wants to override this violation
            //
            out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>5-somes</b> are restricted during this time.<br><br>");

            /*
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");

            //
            //  Return to _insert as directed
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"3\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
             */

            returnToDsheet(out, slotParms, mode, date, "", "", overrideAccess, 3);
            return;
         }
      }

      //
      //  check if we are to skip this test
      //
      if (skip < 4) {

         //
         // *******************************************************************************
         //  Check 5-somes allowed on 'to course' and from-player5 specified
         // *******************************************************************************
         //
         if (!player5.equals( "" )) {      // if player5 exists in 'from' slot

            fives = 0;

            try {

               PreparedStatement pstmtc = con.prepareStatement (
                  "SELECT fives " +
                  "FROM clubparm2 WHERE courseName = ?");

               pstmtc.clearParameters();        // clear the parms
               pstmtc.setString(1, slotParms.to_course);
               rs = pstmtc.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  fives = rs.getInt("fives");
               }
               pstmtc.close();
            }
            catch (Exception e) {
            }
           
            if (fives == 0) {      // if 5-somes not allowed on to_course

               //
               //  Prompt user to see if he wants to override this violation
               //
               out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>5-Somes Restricted</H3><BR>");
               out.println("<BR>Sorry, <b>5-somes</b> are not allowed on the course player5 is being moved to.");
               out.println("<BR>Player5 will be lost if you continue.<br><br>");
               
               customMsg = "Would you like to move this tee time without player5?";

               /*
               out.println("<BR><BR>Would you like to move this tee time without player5?");
               out.println("<BR><BR>");

               //
               //  Return to _insert as directed
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
               out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
               out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
               out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
               out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
               out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
               out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
               out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
               out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

               out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
               out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
               out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
               out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
               out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
               out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
               out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
               out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
               out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
               out.println("<input type=\"hidden\" name=\"skip\" value=\"4\">");
               out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
                */

               returnToDsheet(out, slotParms, mode, date, "", customMsg, overrideAccess, 4);
               return;
            }
         }
      }

   }     // end of IF 'return' reply from prompt

   //
   //  If we get here, then the  move is OK 
   //
   //   - move 'FROM' tee time info into this one (TO)
   //
   if (skip == 4) {      // if player5 being moved to course that does not allow 5-somes
     
      player5 = "";
      user5 = "";
      userg5 = "";
   }        
  
   in_use = 0;
     

   //
   //  Make sure we have the players and other info (this has failed before!!!)
   //
   if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) || !player4.equals( "" ) || !player5.equals( "" )) {

      try {

         PreparedStatement pstmt6 = con.prepareStatement (
            "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
            "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
            "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = ?, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
            "hndcp4 = ?, show1 = ?, show2 = ?, show3 = ?, show4 = ?, player5 = ?, username5 = ?, " +
            "p5cw = ?, hndcp5 = ?, show5 = ?, notes = ?, hideNotes = ?, " +
            "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
            "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, conf = ?, " +
            "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, pos1 = ?, pos2 = ?, pos3 = ?, pos4 = ?, pos5 = ?, " +
            "custom_disp1 = ?, custom_disp2 = ?, custom_disp3 = ?, custom_disp4 = ?, custom_disp5 = ?, " +
            "tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, " +
            "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt6.clearParameters();        // clear the parms
         pstmt6.setString(1, player1);
         pstmt6.setString(2, player2);
         pstmt6.setString(3, player3);
         pstmt6.setString(4, player4);
         pstmt6.setString(5, user1);
         pstmt6.setString(6, user2);
         pstmt6.setString(7, user3);
         pstmt6.setString(8, user4);
         pstmt6.setString(9, p1cw);
         pstmt6.setString(10, p2cw);
         pstmt6.setString(11, p3cw);
         pstmt6.setString(12, p4cw);
         pstmt6.setInt(13, in_use);            // set in_use to NOT
         pstmt6.setFloat(14, hndcp1);
         pstmt6.setFloat(15, hndcp2);
         pstmt6.setFloat(16, hndcp3);
         pstmt6.setFloat(17, hndcp4);
         pstmt6.setShort(18, show1);
         pstmt6.setShort(19, show2);
         pstmt6.setShort(20, show3);
         pstmt6.setShort(21, show4);
         pstmt6.setString(22, player5);
         pstmt6.setString(23, user5);
         pstmt6.setString(24, p5cw);
         pstmt6.setFloat(25, hndcp5);
         pstmt6.setShort(26, show5);
         pstmt6.setString(27, notes);
         pstmt6.setInt(28, hide);
         pstmt6.setString(29, mNum1);
         pstmt6.setString(30, mNum2);
         pstmt6.setString(31, mNum3);
         pstmt6.setString(32, mNum4);
         pstmt6.setString(33, mNum5);
         pstmt6.setString(34, userg1);
         pstmt6.setString(35, userg2);
         pstmt6.setString(36, userg3);
         pstmt6.setString(37, userg4);
         pstmt6.setString(38, userg5);
         pstmt6.setString(39, orig_by);
         pstmt6.setString(40, conf);
         pstmt6.setInt(41, p91);
         pstmt6.setInt(42, p92);
         pstmt6.setInt(43, p93);
         pstmt6.setInt(44, p94);
         pstmt6.setInt(45, p95);
         pstmt6.setShort(46, pos1);
         pstmt6.setShort(47, pos2);
         pstmt6.setShort(48, pos3);
         pstmt6.setShort(49, pos4);
         pstmt6.setShort(50, pos5);
         pstmt6.setString(51, custom_disp1);
         pstmt6.setString(52, custom_disp2);
         pstmt6.setString(53, custom_disp3);
         pstmt6.setString(54, custom_disp4);
         pstmt6.setString(55, custom_disp5);
         pstmt6.setString(56, tflag1);
         pstmt6.setString(57, tflag2);
         pstmt6.setString(58, tflag3);
         pstmt6.setString(59, tflag4);
         pstmt6.setString(60, tflag5);
         pstmt6.setInt(61, guest_id1);
         pstmt6.setInt(62, guest_id2);
         pstmt6.setInt(63, guest_id3);
         pstmt6.setInt(64, guest_id4);
         pstmt6.setInt(65, guest_id5);

         pstmt6.setLong(66, date);
         pstmt6.setInt(67, slotParms.to_time);
         pstmt6.setInt(68, slotParms.to_fb);
         pstmt6.setString(69, slotParms.to_course);

         pstmt6.executeUpdate();      // execute the prepared stmt

         pstmt6.close();

      }
      catch (Exception e1) {

         String eMsg = "Error 3 in moveWhole. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }

      //
      //  Track the history of this tee time - make entry in 'teehist' table (check if new or update)
      //
      String fullName = "Edit Tsheet Moved From " + slotParms.from_time;

      //  new tee time
      SystemUtils.updateHist(date, slotParms.day, slotParms.to_time, slotParms.to_fb, slotParms.to_course, player1, player2, player3,
                             player4, player5, slotParms.user, fullName, 0, con);


      //
      //  Finally, set the 'FROM' tee time to NOT in use and clear out the players
      //
      try {

         PreparedStatement pstmt5 = con.prepareStatement (
            "UPDATE teecurr2 SET player1 = '', player2 = '', player3 = '', player4 = '', " +
            "username1 = '', username2 = '', username3 = '', username4 = '', " +
            "in_use = 0, show1 = 0, show2 = 0, show3 = 0, show4 = 0, " +
            "player5 = '', username5 = '', show5 = 0, " +
            "notes = '', p1cw = '', p2cw = '', p3cw = '', p4cw = '', p5cw = '', " +
            "hndcp1 = 0, hndcp2 = 0, hndcp3 = 0, hndcp4 = 0, hndcp5 = 0, " +
            "mNum1 = '', mNum2 = '', mNum3 = '', mNum4 = '', mNum5 = '', " +
            "userg1 = '', userg2 = '', userg3 = '', userg4 = '', userg5 = '', orig_by = '', conf = '', " +
            "pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0, pos5 = 0, " +
            "custom_disp1 = '', custom_disp2 = '', custom_disp3 = '', custom_disp4 = '', custom_disp5 = '', " +
            "tflag1 = '', tflag2 = '', tflag3 = '', tflag4 = '', tflag5 = '', " +
            "guest_id1 = 0, guest_id2 = 0, guest_id3 = 0, guest_id4 = 0, guest_id5 = 0 " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt5.clearParameters();        // clear the parms
         pstmt5.setLong(1, date);
         pstmt5.setInt(2, slotParms.from_time);
         pstmt5.setInt(3, slotParms.from_fb);
         pstmt5.setString(4, slotParms.from_course);

         pstmt5.executeUpdate();      // execute the prepared stmt

         pstmt5.close();

         if (slotParms.sendEmail.equalsIgnoreCase( "yes" )) {        // if ok to send emails

            sendemail = 1;          // tee time moved - send email notification
         }

      }
      catch (Exception e1) {

         String eMsg = "Error 4 in moveWhole. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }

      //
      //  Track the history of this tee time - make entry in 'teehist' table (check if new or update)
      //
      String empty = "";
      fullName = "Edit Tsheet Move To " + slotParms.to_time;

      SystemUtils.updateHist(date, slotParms.day, slotParms.from_time, slotParms.from_fb, slotParms.from_course, empty, empty, empty,
                             empty, empty, slotParms.user, fullName, 1, con);

   } else {
     
      //
      //  save message in error log
      //
      String msg = "Error in Proshop_dsheet.moveWhole - Player names lost " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.from_time+ ", course= " +slotParms.from_course+ ", fb= " +slotParms.from_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      teeBusy(out, slotParms, mode);        // pretend its busy
      return;
   }

   //
   //  Done - return
   //
   editDone(out, slotParms, resp, mode);

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
      parme.type = "moveWhole";         // type = Move Whole tee time
      parme.date = date;
      parme.time = 0;
      parme.to_time = slotParms.to_time;
      parme.from_time = slotParms.from_time;
      parme.fb = 0;
      parme.to_fb = slotParms.to_fb;
      parme.from_fb = slotParms.from_fb;
      parme.to_course = slotParms.to_course;
      parme.from_course = slotParms.from_course;
      parme.mm = slotParms.mm;
      parme.dd = slotParms.dd;
      parme.yy = slotParms.yy;

      parme.user = slotParms.user;
      parme.emailNew = 0;
      parme.emailMod = 0;
      parme.emailCan = 0;

      parme.p91 = p91;
      parme.p92 = p92;
      parme.p93 = p93;
      parme.p94 = p94;
      parme.p95 = p95;

      parme.day = slotParms.day;

      parme.player1 = player1;
      parme.player2 = player2;
      parme.player3 = player3;
      parme.player4 = player4;
      parme.player5 = player5;

      parme.user1 = user1;
      parme.user2 = user2;
      parme.user3 = user3;
      parme.user4 = user4;
      parme.user5 = user5;

      parme.pcw1 = p1cw;
      parme.pcw2 = p2cw;
      parme.pcw3 = p3cw;
      parme.pcw4 = p4cw;
      parme.pcw5 = p5cw;

      parme.guest_id1 = guest_id1;
      parme.guest_id2 = guest_id2;
      parme.guest_id3 = guest_id3;
      parme.guest_id4 = guest_id4;
      parme.guest_id5 = guest_id5;

      //
      //  Send the email
      //
      sendEmail.sendIt(parme, con);      // in common

   }     // end of IF sendemail
 }      // end of moveWhole


 // *********************************************************
 //  moveSingle - move a single player
 //
 //  parms:
 //          jump        = jump index for return
 //          from_player = player position being moved (1-5)
 //          from_time   = tee time being moved
 //          from_fb     = f/b of tee time being moved
 //          from_course = name of course
 //          to_player   = player position to move to (1-5)
 //          to_time     = tee time to move to
 //          to_fb       = f/b of tee time to move to
 //          to_course   = name of course
 //
 //          prompt      = null if first call here
 //                      = 'return' if user wants to return w/o changes
 //                      = 'continue' if user wants to continue with changes
 //          skip        = verification process to skip if 2nd return
 //
 // *********************************************************

 private void moveSingle(parmSlot slotParms, long date, String prompt, int skip, HttpServletRequest req, 
                         PrintWriter out, Connection con, HttpServletResponse resp) {


   PreparedStatement pstmt6 = null;
   ResultSet rs = null;

   int fives = 0;
   int in_use = 0;
   int from = slotParms.from_player;     // get the player positions (1-5)
   int fr = from;                        // save original value 
   int to = slotParms.to_player;
   int sendemail = 0;

   String customMsg = "";

   //
   //  arrays to hold the player info (FROM tee time)
   //
   String [] p = new String [5];
   String [] player = new String [5];
   String [] pcw = new String [5];
   String [] user = new String [5];
   String [] mNum = new String [5];
   String [] userg = new String [5];
   String [] custom_disp = new String [5];
   String [] tflag = new String [5];
   short [] show = new short [5];
   short [] pos = new short [5];
   float [] hndcp = new float [5];
   int [] p9 = new int [5];
   int [] guest_id = new int [5];
     
   boolean error = false;

   boolean overrideAccess = SystemUtils.verifyProAccess(req, "REST_OVERRIDE", con, out);

   String fullName = "Proshop Edit Tsheet";      // for tee time history

   // get mode if passed (if no mode, then default to edit tee sheet & notification actions)
   String mode = (req.getParameter("mode") == null) ? "" : req.getParameter("mode");
   
   //
   //  adjust the player positions so they can be used for array indexes
   //
   if (to > 0 && to < 6) {
     
      to--;
        
   } else {
     
      to = 1;    // prevent big problem
   }
   if (from > 0 && from < 6) {

      from--;

   } else {

      from = 1;    // prevent big problem
   }

   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.from_time == 0 || slotParms.from_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_dsheet.moveSingle - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.from_time+ ", course= " +slotParms.from_course+ ", fb= " +slotParms.from_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Check if the requested tee time is currently in use (the FROM tee time)
      //
      try {

         //
         //  If we got here by returning from a prompt below, then tee time is already busy
         //
         if (!prompt.equals( "" )) {        // if return, tee time already busy

            in_use = 0;

            getTeeTimeData(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms, con);

         } else {

            in_use = verifySlot.checkInUse(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms.user, slotParms, con);
         }

      }
      catch (Exception e1) {

         String eMsg = "Error 1 in moveSingle. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }

   if (in_use != 0) {              // if time slot already in use

      teeBusy(out, slotParms, mode);       // first call here - reject as busy
      return;
   }

   //
   //  Ok - get current 'FROM' player info from the parm block (set by checkInUse) and save it
   //
   player[0] = slotParms.player1;
   player[1] = slotParms.player2;
   player[2] = slotParms.player3;
   player[3] = slotParms.player4;
   player[4] = slotParms.player5;
   pcw[0] = slotParms.p1cw;
   pcw[1] = slotParms.p2cw;
   pcw[2] = slotParms.p3cw;
   pcw[3] = slotParms.p4cw;
   pcw[4] = slotParms.p5cw;
   user[0] = slotParms.user1;
   user[1] = slotParms.user2;
   user[2] = slotParms.user3;
   user[3] = slotParms.user4;
   user[4] = slotParms.user5;
   hndcp[0] = slotParms.hndcp1;
   hndcp[1] = slotParms.hndcp2;
   hndcp[2] = slotParms.hndcp3;
   hndcp[3] = slotParms.hndcp4;
   hndcp[4] = slotParms.hndcp5;
   show[0] = slotParms.show1;
   show[1] = slotParms.show2;
   show[2] = slotParms.show3;
   show[3] = slotParms.show4;
   show[4] = slotParms.show5;
   mNum[0] = slotParms.mNum1;
   mNum[1] = slotParms.mNum2;
   mNum[2] = slotParms.mNum3;
   mNum[3] = slotParms.mNum4;
   mNum[4] = slotParms.mNum5;
   userg[0] = slotParms.userg1;
   userg[1] = slotParms.userg2;
   userg[2] = slotParms.userg3;
   userg[3] = slotParms.userg4;
   userg[4] = slotParms.userg5;
   p9[0] = slotParms.p91;
   p9[1] = slotParms.p92;
   p9[2] = slotParms.p93;
   p9[3] = slotParms.p94;
   p9[4] = slotParms.p95;
   pos[0] = slotParms.pos1;
   pos[1] = slotParms.pos2;
   pos[2] = slotParms.pos3;
   pos[3] = slotParms.pos4;
   pos[4] = slotParms.pos5;
   custom_disp[0] = slotParms.custom_disp1;
   custom_disp[1] = slotParms.custom_disp2;
   custom_disp[2] = slotParms.custom_disp3;
   custom_disp[3] = slotParms.custom_disp4;
   custom_disp[4] = slotParms.custom_disp5;
   tflag[0] = slotParms.tflag1;
   tflag[1] = slotParms.tflag2;
   tflag[2] = slotParms.tflag3;
   tflag[3] = slotParms.tflag4;
   tflag[4] = slotParms.tflag5;
   guest_id[0] = slotParms.guest_id1;
   guest_id[1] = slotParms.guest_id2;
   guest_id[2] = slotParms.guest_id3;
   guest_id[3] = slotParms.guest_id4;
   guest_id[4] = slotParms.guest_id5;


   slotParms.player1 = "";       // init parmSlot player fields (verifySlot will fill)
   slotParms.player2 = "";
   slotParms.player3 = "";
   slotParms.player4 = "";
   slotParms.player5 = "";

   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.to_time == 0 || slotParms.to_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_dsheet.moveSingle2 - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.to_time+ ", course= " +slotParms.to_course+ ", fb= " +slotParms.to_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Now check if the 'TO' tee time is currently in use (this will put its info in slotParms)
      //
      try {

         //
         //  If we got here by returning from a prompt below, then tee time is already busy
         //
         if (!prompt.equals( "" )) {        // if return, tee time already busy

            in_use = 0;

            getTeeTimeData(date, slotParms.to_time, slotParms.to_fb, slotParms.to_course, slotParms, con);

         } else {

            in_use = verifySlot.checkInUse(date, slotParms.to_time, slotParms.to_fb, slotParms.to_course, slotParms.user, slotParms, con);
         }

      }
      catch (Exception e1) {

         String eMsg = "Error 2 in moveSingle. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }

   //
   //  If 'TO' tee time is in use 
   //
   if (in_use != 0) { 

      //
      //  Error - We must free up the 'FROM' tee time
      //
      try {

         PreparedStatement pstmt4 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = 0 " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt4.clearParameters();        // clear the parms
         pstmt4.setLong(1, date);
         pstmt4.setInt(2, slotParms.from_time);
         pstmt4.setInt(3, slotParms.from_fb);
         pstmt4.setString(4, slotParms.from_course);
         pstmt4.executeUpdate();      // execute the prepared stmt
         pstmt4.close();

      }
      catch (Exception ignore) {
      }

      teeBusy(out, slotParms, mode);
      return;
   }

   //
   //  If user was prompted and opted to return w/o changes, then we must clear the 'in_use' flags
   //  before returning to the tee sheet.
   //
   if (prompt.equals( "return" )) {        // if prompt specified a return

      in_use = 0;

      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setInt(1, in_use);
         pstmt1.setLong(2, date);
         pstmt1.setInt(3, slotParms.from_time);
         pstmt1.setInt(4, slotParms.from_fb);
         pstmt1.setString(5, slotParms.from_course);

         pstmt1.executeUpdate();      // execute the prepared stmt

         pstmt1.close();

         pstmt1 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setInt(1, in_use);
         pstmt1.setLong(2, date);
         pstmt1.setInt(3, slotParms.to_time);
         pstmt1.setInt(4, slotParms.to_fb);
         pstmt1.setString(5, slotParms.to_course);

         pstmt1.executeUpdate();      // execute the prepared stmt

         pstmt1.close();

      }
      catch (Exception ignore) {
      }

      // return to Proshop_dsheet

      out.println("<HTML><HEAD><Title>Proshop Edit Tee Sheet Complete</Title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_dsheet?mode=" +mode+ "&name=" +slotParms.name+ "&index=" + slotParms.ind + "&course=" + slotParms.returnCourse + "&email=" + slotParms.sendEmail + "&jump=" + slotParms.jump + "\">");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR>");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<BR><BR><H2>Return Accepted</H2>");
      out.println("<BR><BR>Thank you, click Return' below if this does not automatically return.<BR>");
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + slotParms.ind + ">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
      out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\"></form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

   } else {    // not a 'return' response from prompt

      //
      //  This is either the first time here, or a 'Continue' reply to a prompt
      //
      p[0] = slotParms.player1;    // save 'TO' player names
      p[1] = slotParms.player2;
      p[2] = slotParms.player3;
      p[3] = slotParms.player4;
      p[4] = slotParms.player5;


      //
      //  Make sure there are no duplicate names
      //
      if (!user[from].equals( "" )) {         // if player is a member
        
         if ((player[from].equalsIgnoreCase( p[0] )) || (player[from].equalsIgnoreCase( p[1] )) ||
             (player[from].equalsIgnoreCase( p[2] )) || (player[from].equalsIgnoreCase( p[3] )) ||
             (player[from].equalsIgnoreCase( p[4] ))) {

            //
            //  Error - name already exists
            //
            in_use = 0;

            try {

               PreparedStatement pstmt1 = con.prepareStatement (
                  "UPDATE teecurr2 SET in_use = ? " +
                  "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setInt(1, in_use);
               pstmt1.setLong(2, date);
               pstmt1.setInt(3, slotParms.from_time);
               pstmt1.setInt(4, slotParms.from_fb);
               pstmt1.setString(5, slotParms.from_course);

               pstmt1.executeUpdate();      // execute the prepared stmt

               pstmt1.close();

               pstmt1 = con.prepareStatement (
                  "UPDATE teecurr2 SET in_use = ? " +
                  "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setInt(1, in_use);
               pstmt1.setLong(2, date);
               pstmt1.setInt(3, slotParms.to_time);
               pstmt1.setInt(4, slotParms.to_fb);
               pstmt1.setString(5, slotParms.to_course);

               pstmt1.executeUpdate();      // execute the prepared stmt

               pstmt1.close();

            }
            catch (Exception ignore) {
            }

            out.println(SystemUtils.HeadTitle("Player Move Error"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Player Move Error</H3>");
            out.println("<BR><BR>Sorry, but the selected player is already scheduled at the time you are moving to.");
            out.println("<BR><BR>");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
               out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=" + slotParms.ind + ">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"submit\" value=\"Back to Edit\" style=\"text-decoration:underline; background:#8B8970\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }

      //
      //  If any skips are set, then we've already been through here.
      //
      if (skip == 0) {

         //
         //  Check if 'TO' tee time position is empty
         //
         if (!p[to].equals( "" )) {

            //
            //  Tee time is occupied - inform user and ask to continue or cancel
            //
            out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Tee Time Position is Occupied</H3><BR>");
            out.println("<BR>WARNING: The tee time position you are trying to move TO is already occupied.");
            out.println("<BR><BR>If you continue, the current player in this position (" +p[to]+ ") will be replaced.");

            customMsg = "Would you like to continue and replace this player?";

            /*
            out.println("<BR><BR>Would you like to continue and replace this player?");
            out.println("<BR><BR>");
               
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"1\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            */

            returnToDsheet(out, slotParms, mode, date, "", customMsg, overrideAccess, 1);
            return;
         }
           
         //
         // *******************************************************************************
         //  Check 5-somes allowed on 'to course' if moving to player5 slot
         // *******************************************************************************
         //
         if (to == 4) {      // if player being moved to player5 slot

            fives = 0;

            try {

               PreparedStatement pstmtc = con.prepareStatement (
                  "SELECT fives " +
                  "FROM clubparm2 WHERE courseName = ?");

               pstmtc.clearParameters();        // clear the parms
               pstmtc.setString(1, slotParms.to_course);
               rs = pstmtc.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  fives = rs.getInt("fives");
               }
               pstmtc.close();
            }
            catch (Exception e) {
            }

            if (fives == 0) {      // if 5-somes not allowed on to_course

               out.println(SystemUtils.HeadTitle("Player Move Error"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Player Move Error</H3>");
               out.println("<BR><BR>Sorry, but the course you are moving the player to does not support 5-somes.");
               out.println("<BR><BR>");

               /*
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
               out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
               out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
               out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
               out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
               out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
               out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
               out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
               out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
               out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
               out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
                */

               returnToDsheet(out, slotParms, mode, date, "", "", false, 0);
               return;
            }
         }

      }
        
      //
      //  check if we are to skip this test
      //
      if (skip < 2) {

         //
         // *******************************************************************************
         //  Check member restrictions in 'TO' tee time, but 'FROM' players
         //
         //     First, find all restrictions within date & time constraints on this course.
         //     Then, find the ones for this day.
         //     Then, find any for this member type or membership type (all 5 players).
         //
         // *******************************************************************************
         //

         //
         //  allocate and setup new parm block to hold the tee time parms for this process
         //
         parmSlot slotParms2 = new parmSlot();          // allocate a parm block

         slotParms2.date = date;                 // get 'TO' info
         slotParms2.time = slotParms.to_time;
         slotParms2.course = slotParms.to_course;
         slotParms2.fb = slotParms.to_fb;
         slotParms2.day = slotParms.day;

         slotParms2.player1 = player[from];          // get 'FROM' player (only check this player)
         slotParms2.guest_id1 = guest_id[from];

         try {

            verifySlot.parseGuests(slotParms2, con);     // check for guest and set guest type

            error = verifySlot.parseNames(slotParms2, "pro");   // get the name (lname, fname, mi)

            verifySlot.getUsers(slotParms2, con);        // get the mship and mtype info (needs lname, fname, mi)

            error = false;                               // init error indicator

            error = verifySlot.checkMemRests(slotParms2, con);      // check restrictions

         }
         catch (Exception ignore) {
         }

         if (error == true) {          // if we hit on a restriction

            //
            //  Prompt user to see if he wants to override this violation
            //
            out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>" +player[from]+ "</b> is restricted from playing during this time.<br><br>");
            out.println("This time slot has the following restriction:  <b>" + slotParms2.rest_name + "</b><br><br>");

            /*
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");

            //
            //  Return to _insert as directed
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"2\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            */

            returnToDsheet(out, slotParms, mode, date, "", "", overrideAccess, 2);
            return;
         }
      }

      //
      //  check if we are to skip this test
      //
      if (skip < 3) {

         //
         // *******************************************************************************
         //  Check 5-some restrictions - use 'FROM' player5 and 'TO' tee time slot
         //
         //   If moving to position 5 & 5-somes are restricted during this tee time, warn the proshop user.
         // *******************************************************************************
         //
         if ((to == 4) && (!slotParms.rest5.equals( "" ))) { // if 5-somes restricted prompt user to skip test

            //
            //  Prompt user to see if he wants to override this violation
            //
            out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>5-somes</b> are restricted during this time.<br><br>");

            /*
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");

            //
            //  Return to _insert as directed
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"3\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            */

            returnToDsheet(out, slotParms, mode, date, "", "", overrideAccess, 3);
            return;
         }
      }

   }     // end of IF 'return' reply from prompt


   //
   //  OK to move player - move 'FROM' player info into this tee time (TO position)
   //
   to++;      // change index back to position value

   String moveP1 = "UPDATE teecurr2 SET player" +to+ " = ?, username" +to+ " = ?, p" +to+ "cw = ?, in_use = 0, hndcp" +to+ " = ?, show" +to+ " = ?, " +
           "mNum" +to+ " = ?, userg" +to+ " = ?, p9" +to+ " = ?, pos" +to+ " = ?, custom_disp" +to+ " = ?, tflag" +to+ " = ?, guest_id" +to+ " = ? " +
           "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?";
     
   try {

      pstmt6 = con.prepareStatement (moveP1);

      pstmt6.clearParameters();        // clear the parms
      pstmt6.setString(1, player[from]);
      pstmt6.setString(2, user[from]);
      pstmt6.setString(3, pcw[from]);
      pstmt6.setFloat(4, hndcp[from]);
      pstmt6.setShort(5, show[from]);
      pstmt6.setString(6, mNum[from]);
      pstmt6.setString(7, userg[from]);
      pstmt6.setInt(8, p9[from]);
      pstmt6.setShort(9, pos[from]);
      pstmt6.setString(10, custom_disp[from]);
      pstmt6.setString(11, tflag[from]);
      pstmt6.setInt(12, guest_id[from]);

      pstmt6.setLong(13, date);
      pstmt6.setInt(14, slotParms.to_time);
      pstmt6.setInt(15, slotParms.to_fb);
      pstmt6.setString(16, slotParms.to_course);

      pstmt6.executeUpdate();      // execute the prepared stmt

      pstmt6.close();


      if (slotParms.sendEmail.equalsIgnoreCase( "yes" )) {        // if ok to send emails

         sendemail = 1;       // send email notification
      }

      //
      //  Track the history of this tee time - make entry in 'teehist' table (first, get the new players)
      //
      pstmt6 = con.prepareStatement (
      "SELECT player1, player2, player3, player4, player5 " +
      "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt6.clearParameters();        // clear the parms
      pstmt6.setLong(1, date);
      pstmt6.setInt(2, slotParms.to_time);
      pstmt6.setInt(3, slotParms.to_fb);
      pstmt6.setString(4, slotParms.to_course);
      rs = pstmt6.executeQuery();

      if (rs.next()) {

         player[0] = rs.getString(1);
         player[1] = rs.getString(2);
         player[2] = rs.getString(3);
         player[3] = rs.getString(4);
         player[4] = rs.getString(5);
      }
      pstmt6.close();

      fullName = "Edit Tsheet Move Player From " +slotParms.from_time;
      
      SystemUtils.updateHist(date, slotParms.day, slotParms.to_time, slotParms.to_fb, slotParms.to_course, player[0], player[1], player[2],
                             player[3], player[4], slotParms.user, fullName, 1, con);

   }
   catch (Exception e1) {

      String eMsg = "Error 3 in moveSingle. ";
      dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
      return;
   }


   //
   //  Finally, set the 'FROM' tee time to NOT in use and clear out the player info
   //
   String moveP2 = "UPDATE teecurr2 SET player" +fr+ " = '', username" +fr+ " = '', p" +fr+ "cw = '', hndcp" +fr+ " = 0, in_use = 0, show" +fr+ " = 0, " +
           "mNum" +fr+ " = '', userg" +fr+ " = '', pos" +fr+ " = 0, custom_disp" +fr+ " = '', tflag" +fr+ " = '', guest_id" +fr+ " = 0 " +
           "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?";

   try {

      PreparedStatement pstmt5 = con.prepareStatement (moveP2);

      pstmt5.clearParameters();        // clear the parms
      pstmt5.setLong(1, date);
      pstmt5.setInt(2, slotParms.from_time);
      pstmt5.setInt(3, slotParms.from_fb);
      pstmt5.setString(4, slotParms.from_course);

      pstmt5.executeUpdate();      // execute the prepared stmt

      pstmt5.close();


      //
      //  Track the history of this tee time - make entry in 'teehist' table (first get the new player list)
      //
      pstmt6 = con.prepareStatement (
      "SELECT player1, player2, player3, player4, player5 " +
      "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt6.clearParameters();        // clear the parms
      pstmt6.setLong(1, date);
      pstmt6.setInt(2, slotParms.from_time);
      pstmt6.setInt(3, slotParms.from_fb);
      pstmt6.setString(4, slotParms.from_course);
      rs = pstmt6.executeQuery();

      if (rs.next()) {

         player[0] = rs.getString(1);
         player[1] = rs.getString(2);
         player[2] = rs.getString(3);
         player[3] = rs.getString(4);
         player[4] = rs.getString(5);
      }
      pstmt6.close();

      fullName = "Edit Tsheet Move Player To " +slotParms.to_time; 
      
      SystemUtils.updateHist(date, slotParms.day, slotParms.from_time, slotParms.from_fb, slotParms.from_course, player[0], player[1], player[2],
                             player[3], player[4], slotParms.user, fullName, 1, con);


   }
   catch (Exception e1) {

      String eMsg = "Error 4 in moveSingle. ";
      dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
      return;
   }

   //
   //  Done - return
   //
   editDone(out, slotParms, resp, mode);

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

      try {                 // get the new 'to' tee time values

         PreparedStatement pstmt5b = con.prepareStatement (
             "SELECT player1, player2, player3, player4, username1, username2, username3, " +
                 "username4, p1cw, p2cw, p3cw, p4cw, " +
                 "player5, username5, p5cw, p91, p92, p93, p94, p95, " +
                 "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5 " +
             "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt5b.clearParameters();        // clear the parms
         pstmt5b.setLong(1, date);
         pstmt5b.setInt(2, slotParms.to_time);
         pstmt5b.setInt(3, slotParms.to_fb);
         pstmt5b.setString(4, slotParms.to_course);
         rs = pstmt5b.executeQuery();      

         if (rs.next()) {

            player[0] = rs.getString(1);
            player[1] = rs.getString(2);
            player[2] = rs.getString(3);
            player[3] = rs.getString(4);
            user[0] = rs.getString(5);
            user[1] = rs.getString(6);
            user[2] = rs.getString(7);
            user[3] = rs.getString(8);
            pcw[0] = rs.getString(9);
            pcw[1] = rs.getString(10);
            pcw[2] = rs.getString(11);
            pcw[3] = rs.getString(12);
            player[4] = rs.getString(13);
            user[4] = rs.getString(14);
            pcw[4] = rs.getString(15);
            p9[0] = rs.getInt(16);
            p9[1] = rs.getInt(17);
            p9[2] = rs.getInt(18);
            p9[3] = rs.getInt(19);
            p9[4] = rs.getInt(20);
            guest_id[0] = rs.getInt("guest_id1");
            guest_id[1] = rs.getInt("guest_id2");
            guest_id[2] = rs.getInt("guest_id3");
            guest_id[3] = rs.getInt("guest_id4");
            guest_id[4] = rs.getInt("guest_id5");
         }
         pstmt5b.close();

      }
      catch (Exception ignoree) {
      }

      //
      //  allocate a parm block to hold the email parms
      //
      parmEmail parme = new parmEmail();          // allocate an Email parm block

      //
      //  Set the values in the email parm block
      //
      parme.type = "tee";         // type = Move Single tee time (use tee and emailMod) 
      parme.date = date;
      parme.time = slotParms.to_time;
      parme.to_time = 0;
      parme.from_time = 0;
      parme.fb = slotParms.to_fb;
      parme.to_fb = 0;
      parme.from_fb = 0;
      parme.to_course = slotParms.to_course;
      parme.from_course = slotParms.from_course;
      parme.mm = slotParms.mm;
      parme.dd = slotParms.dd;
      parme.yy = slotParms.yy;

      parme.user = slotParms.user;
      parme.emailNew = 0;
      parme.emailMod = 1;
      parme.emailCan = 0;

      parme.p91 = p9[0];
      parme.p92 = p9[1];
      parme.p93 = p9[2];
      parme.p94 = p9[3];
      parme.p95 = p9[4];

      parme.day = slotParms.day;

      parme.player1 = player[0];
      parme.player2 = player[1];
      parme.player3 = player[2];
      parme.player4 = player[3];
      parme.player5 = player[4];

      parme.oldplayer1 = slotParms.player1;
      parme.oldplayer2 = slotParms.player2;
      parme.oldplayer3 = slotParms.player3;
      parme.oldplayer4 = slotParms.player4;
      parme.oldplayer5 = slotParms.player5;

      parme.user1 = user[0];
      parme.user2 = user[1];
      parme.user3 = user[2];
      parme.user4 = user[3];
      parme.user5 = user[4];

      parme.olduser1 = slotParms.user1;
      parme.olduser2 = slotParms.user2;
      parme.olduser3 = slotParms.user3;
      parme.olduser4 = slotParms.user4;
      parme.olduser5 = slotParms.user5;

      parme.pcw1 = pcw[0];
      parme.pcw2 = pcw[1];
      parme.pcw3 = pcw[2];
      parme.pcw4 = pcw[3];
      parme.pcw5 = pcw[4];

      parme.oldpcw1 = slotParms.p1cw;
      parme.oldpcw2 = slotParms.p2cw;
      parme.oldpcw3 = slotParms.p3cw;
      parme.oldpcw4 = slotParms.p4cw;
      parme.oldpcw5 = slotParms.p5cw;

      parme.guest_id1 = guest_id[0];
      parme.guest_id2 = guest_id[1];
      parme.guest_id3 = guest_id[2];
      parme.guest_id4 = guest_id[3];
      parme.guest_id5 = guest_id[4];

      parme.oldguest_id1 = slotParms.guest_id1;
      parme.oldguest_id2 = slotParms.guest_id2;
      parme.oldguest_id3 = slotParms.guest_id3;
      parme.oldguest_id4 = slotParms.guest_id4;
      parme.oldguest_id5 = slotParms.guest_id5;

      //
      //  Send the email
      //
      sendEmail.sendIt(parme, con);      // in common

   }     // end of IF sendemail
 }      // end of moveSingle


/**
 //************************************************************************
 //
 //   Get tee time data
 //
 //************************************************************************
 **/

 private void getTeeTimeData(long date, int time, int fb, String course, parmSlot slotParms, Connection con)
         throws Exception {


   PreparedStatement pstmt = null;
   Statement stmt = null;
   ResultSet rs = null;


   try {

      pstmt = con.prepareStatement (
         "SELECT * " +
         "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);         // put the parm in pstmt
      pstmt.setInt(2, time);
      pstmt.setInt(3, fb);
      pstmt.setString(4, course);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         slotParms.player1 = rs.getString( "player1" );
         slotParms.player2 = rs.getString( "player2" );
         slotParms.player3 = rs.getString( "player3" );
         slotParms.player4 = rs.getString( "player4" );
         slotParms.user1 = rs.getString( "username1" );
         slotParms.user2 = rs.getString( "username2" );
         slotParms.user3 = rs.getString( "username3" );
         slotParms.user4 = rs.getString( "username4" );
         slotParms.p1cw = rs.getString( "p1cw" );
         slotParms.p2cw = rs.getString( "p2cw" );
         slotParms.p3cw = rs.getString( "p3cw" );
         slotParms.p4cw = rs.getString( "p4cw" );
         slotParms.last_user = rs.getString( "in_use_by" );
         slotParms.hndcp1 = rs.getFloat( "hndcp1" );
         slotParms.hndcp2 = rs.getFloat( "hndcp2" );
         slotParms.hndcp3 = rs.getFloat( "hndcp3" );
         slotParms.hndcp4 = rs.getFloat( "hndcp4" );
         slotParms.show1 = rs.getShort( "show1" );
         slotParms.show2 = rs.getShort( "show2" );
         slotParms.show3 = rs.getShort( "show3" );
         slotParms.show4 = rs.getShort( "show4" );
         slotParms.player5 = rs.getString( "player5" );
         slotParms.user5 = rs.getString( "username5" );
         slotParms.p5cw = rs.getString( "p5cw" );
         slotParms.hndcp5 = rs.getFloat( "hndcp5" );
         slotParms.show5 = rs.getShort( "show5" );
         slotParms.notes = rs.getString( "notes" );
         slotParms.hide = rs.getInt( "hideNotes" );
         slotParms.rest5 = rs.getString( "rest5" );
         slotParms.mNum1 = rs.getString( "mNum1" );
         slotParms.mNum2 = rs.getString( "mNum2" );
         slotParms.mNum3 = rs.getString( "mNum3" );
         slotParms.mNum4 = rs.getString( "mNum4" );
         slotParms.mNum5 = rs.getString( "mNum5" );
         slotParms.userg1 = rs.getString( "userg1" );
         slotParms.userg2 = rs.getString( "userg2" );
         slotParms.userg3 = rs.getString( "userg3" );
         slotParms.userg4 = rs.getString( "userg4" );
         slotParms.userg5 = rs.getString( "userg5" );
         slotParms.orig_by = rs.getString( "orig_by" );
         slotParms.conf = rs.getString( "conf" );
         slotParms.p91 = rs.getInt( "p91" );
         slotParms.p92 = rs.getInt( "p92" );
         slotParms.p93 = rs.getInt( "p93" );
         slotParms.p94 = rs.getInt( "p94" );
         slotParms.p95 = rs.getInt( "p95" );
         slotParms.pos1 = rs.getShort( "pos1" );
         slotParms.pos2 = rs.getShort( "pos2" );
         slotParms.pos3 = rs.getShort( "pos3" );
         slotParms.pos4 = rs.getShort( "pos4" );
         slotParms.pos5 = rs.getShort( "pos5" );
         slotParms.custom_disp1 = rs.getString( "custom_disp1" );
         slotParms.custom_disp2 = rs.getString( "custom_disp2" );
         slotParms.custom_disp3 = rs.getString( "custom_disp3" );
         slotParms.custom_disp4 = rs.getString( "custom_disp4" );
         slotParms.custom_disp5 = rs.getString( "custom_disp5" );
         slotParms.tflag1 = rs.getString( "tflag1" );
         slotParms.tflag2 = rs.getString( "tflag2" );
         slotParms.tflag3 = rs.getString( "tflag3" );
         slotParms.tflag4 = rs.getString( "tflag4" );
         slotParms.tflag5 = rs.getString( "tflag5" );
         slotParms.guest_id1 = rs.getInt( "guest_id1" );
         slotParms.guest_id2 = rs.getInt( "guest_id2" );
         slotParms.guest_id3 = rs.getInt( "guest_id3" );
         slotParms.guest_id4 = rs.getInt( "guest_id4" );
         slotParms.guest_id5 = rs.getInt( "guest_id5" );
      }

      pstmt.close();

   }
   catch (Exception e) {

      throw new Exception("Error getting tee time data - Proshop_dsheet.getTeeTimeData - Exception: " + e.getMessage());
   }

 }


 // *********************************************************
 //  Done
 // *********************************************************

 private void editDone(PrintWriter out, parmSlot slotParms, HttpServletResponse resp, String mode) {
   
   try {

       String url="/" +rev+ "/servlet/Proshop_dsheet?mode=" +mode+ "&name=" +slotParms.name+ "&index=" +slotParms.ind+ "&course=" + slotParms.returnCourse + "&jump=" + slotParms.jump + "&email=" + slotParms.sendEmail + "&hide=" + slotParms.hideTimes;
       resp.sendRedirect(url);

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>System Error</H3>");
      out.println("<BR><BR>A system error occurred while trying to return to the edit tee sheet.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +slotParms.ind+ "&course=" +slotParms.returnCourse+ "\">");
      out.println("Return to Tee Sheet</a></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
   }
 }


 // *********************************************************
 //  Tee Time Busy Error
 // *********************************************************

 private void teeBusy(PrintWriter out, parmSlot slotParms, String mode) {

      out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H2>Tee Time Slot Busy</H2>");
      out.println("<BR><BR>Sorry, but this tee time slot is currently busy.");
      out.println("<BR><BR>If you are attempting to move a player to another position within the same tee time,");
      out.println("<BR>you will have to Return to the Tee Sheet and select that tee time to update it.");
      out.println("<BR><BR>Otherwise, please select another time or try again later.");
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
         out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
         out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hideTimes + "\">");
         out.println("<input type=\"hidden\" name=\"index\" value=" + slotParms.ind + ">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
         out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
      out.println("<input type=\"submit\" value=\"Back to Edit\" style=\"text-decoration:underline; background:#8B8970\"></form>");

      out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\">");
         out.println("<input type=\"submit\" value=\"Return to Tee Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }


 // *********************************************************
 //  Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e1, int index, String course, String eMsg) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>Error in dbError in Proshop_dsheet:");
      out.println("<BR><BR>" + eMsg + " Exc= " + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +index+ "&course=" +course+ "\">");
      out.println("Return to Tee Sheet</a></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }

 //
 // returns the player name but enforces a max length for staying in the width allowed
 // change the two positive values to control the output
 //
 private static String fitName(String pName) {
     
   return (pName.length() > 13) ? pName.substring(0, 12) + "..." : pName;
 }

 
 private void displayBlockers(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {
         
    // BEGIN VARIABLE DEFINITIONS
    ResultSet rs = null; 
    Statement stmt = null;
    PreparedStatement pstmtc = null;

    int cMax = 0;                 // # of courses
    int i = 0;
    int tmp_i = 0;                // counter for course[], shading of course field
    int index = 0;
    int day = 0;
    int month = 0;
    int year = 0;
    int courseCount = 0;

    long date = 0;

    String courseName = "";
    
    String emailOpt = req.getParameter("email");
    String course = req.getParameter("course");
    String sindex = req.getParameter("index");

    // get mode if passed (if no mode, then default to edit tee sheet & notification actions)
    String mode = (req.getParameter("mode") == null) ? "" : req.getParameter("mode");

    // get name if passed (name will only be present if they were here from lottery or event management)
    String name = (req.getParameter("name") == null) ? "" : req.getParameter("name");
    
    int fives = 0;
    int fivesALL = 0;
    ArrayList<Integer> fivesA = new ArrayList<Integer> ();        // array list to hold 5-some option for each course
   
    //  array to hold the course names and colors
    ArrayList<String> courseA = new ArrayList<String>();
    
   //
   //  parm block to hold the Course Colors
   //
   parmCourseColors colors = new parmCourseColors();          // allocate a parm block

   int colorMax = colors.colorMax;             // max number of colors defined
   
    //
    // END OF VARIABLES DEFINITIONS

   

    // BEGIN VARIABLE ASSIGNMENT
    //
    
    
    if (course == null) course = ""; // ensure course isn't null

    if (sindex == null || sindex.equals( "" ) || sindex.equalsIgnoreCase( "null" )) 
     { index = 0; } else { index = Integer.parseInt(sindex); }
   
    //
    // get today's date and add the index to get the requested tee-sheet date
    Calendar cal = new GregorianCalendar();       // get todays date
    cal.add(Calendar.DATE,index);                 // roll ahead 'index' days
    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    day = cal.get(Calendar.DAY_OF_MONTH);
    month = month + 1;                            // month starts at zero
    date = (year * 10000) + (month * 100) + day;  // create a date field of yyyymmdd
    
    
    // all I really should have to do is check to see if we are displaying 1 course or -ALL- courses.
    // if 1 then see if it supports 5 somes
    // if -ALL- then see if any supports 5 somes
    // query and display


    parmClub parm = new parmClub(0, con);
    parmCourse parmc = new parmCourse();          // allocate a parm block
    
    try {
        
        getClub.getParms(con, parm);        // get the club parms
    }
    catch (Exception exp) {
        SystemUtils.buildDatabaseErrMsg("Proshop_dsheet - Fatal error allocating parm blocks.", exp.getMessage(), out, true);        
    }
        


    
     //
     //   Get course names if multi-course facility so we can determine if any support 5-somes
     //
     i = 0;
     if (parm.multi != 0) {           // if multiple courses supported for this club

        try {
        
            //
            //  Get the names of all courses for this club
            //
            courseA = Utilities.getCourseNames(con);     // get all the course names

            courseCount = courseA.size();               // number of courses         

            courseA.add ("-ALL-");                   // add '-ALL-' option
            
        }
        catch (Exception exp) {
            SystemUtils.buildDatabaseErrMsg("Proshop_dsheet - Fatal error allocating course names.", exp.getMessage(), out, true);        
        }
     }

    
    //
    //  Get the walk/cart options available and 5-some support
    //
    i = 0;
    
    if (course.equals( "-ALL-" )) {

        try {
            
            //
            //  Check all courses for 5-some support
            //
            loopc:
            while (i < courseCount) {

                courseName = courseA.get(i);       // get a course name

                if (!courseName.equals( "-ALL-" )) {   // skip if -ALL-

                    if (courseName.equals( "" )) break loopc;

                    getParms.getCourse(con, parmc, courseName);
                    
                    fivesA.add (parmc.fives);      // get fivesome option

                    if (parmc.fives == 1) fives = 1;

                } // end if courseName = -ALL-

                i++;
            } // end while loop

        }
        catch(Exception exp)
        {
            SystemUtils.buildDatabaseErrMsg("Fatal error trying to get course parms. (-ALL-)", exp.getMessage(), out, true);   
        }
        
    } else {       // single course requested

        try {
            getParms.getCourse(con, parmc, course);
        }
        catch (Exception exp)
        {
            SystemUtils.buildDatabaseErrMsg("Fatal error trying to get course parms.", exp.getMessage(), out, true); 
        }
        fives = parmc.fives;      // get fivesome option
    }

    fivesALL = fives;            // fives will be 1 if any course allowed 5-somes

    i = 0;

    // done pre-processing
    
    
    // start tee sheet output
    
    out.println(SystemUtils.HeadTitle("Tee Sheet Blockers"));
    //out.println("<script src=\"/" + rev + "/blockers.js\">");
    out.println("<script type=\"text/javascript\">");
    out.println("var last_selected = 0;");
    out.println("function selectRange(evt, i) {");
    out.println(" var fromPos = last_selected;");
    out.println(" var toPos = i;");
    out.println(" if (fromPos > 0) {");
    out.println("  var key = (evt.which) ? evt.which : event.keyCode;");
    out.println("  shift = (evt.shiftKey) ? true : false;");
    out.println("  if (shift == true) {");
    out.println("   if (fromPos < toPos) {");
    out.println("    fromPos++;");
    out.println("   } else if (fromPos > toPos) {");
    out.println("    fromPos = i;");
    out.println("    toPos = last_selected - 1;");
    out.println("   }");
    
    out.println("   for (x=fromPos; x<=toPos; x++) {");
    //out.println("    eval(\"document.forms['frmBlockers'].chk' + x + '.checked = (document.forms['frmBlockers'].chk' + x + '.checked == false)\");");
    out.println("    if (document.getElementById('chk' + x).disabled == false) document.getElementById('chk' + x).checked = (document.getElementById('chk' + x).checked == false);");
    out.println("   }");
    out.println("   last_selected = 0; // reset");
    out.println("   return;");
    out.println("  }");
    out.println(" }");
    out.println(" last_selected = i;");
    //out.println(" eval(\"document.forms['frmBlockers'].chk' + i + '.checked = (document.forms['frmBlockers'].chk' + i + '.checked == false)\");");
    out.println("    document.getElementById('chk' + i).checked = (document.getElementById('chk' + i).checked == false);");
    out.println("}");
    out.println("</script>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("");
    out.println("<center><p><font size=5>Tee Sheet Blockers</font></p>");
    out.println("<b>Date:</b>&nbsp;&nbsp;" + month + "/" + day + "/" + year);
    
    if (!course.equals( "" )) out.println("&nbsp;&nbsp;&nbsp;<b>Course:</b>&nbsp;&nbsp;" + course);
    out.println("</center><br>");

             
    // build tee sheet header
    
        out.println("<table width=\"80%\" align=\"center\"><tr valign=\"top\">");
        
        out.println("<td align=\"center\">");
        out.println("<form method=\"GET\" action=\"/" + rev + "/servlet/Proshop_dsheet\" name=frmRefresh>");
         out.println("<input type=submit value=\" Refresh Sheet \">");
         out.println("<input type=hidden name=blockers value=true>");
         out.println("<input type=hidden name=mode value=\"" + mode + "\">");
         out.println("<input type=hidden name=name value=\"" + name + "\">");
         out.println("<input type=hidden name=index value=\"" + index + "\">");
         out.println("<input type=hidden name=course value=\"" + course + "\">");
        out.println("</form></td>");
        
        out.println("<td align=\"center\">");
        out.println("<form method=\"GET\" action=\"/" + rev + "/servlet/Proshop_jump\" name=frmBack>");
         out.println("<input type=submit value=\" Back To Tee Sheet \">");
         out.println("<input type=hidden name=index value=\"" + index + "\">");
         out.println("<input type=hidden name=course value=\"" + course + "\">");
        out.println("</form></td>");
        
        out.println("<td align=\"center\">");
        out.println("<form method=\"GET\" action=\"/" + rev + "/servlet/Proshop_dsheet\" name=frmBackEdit>");
         out.println("<input type=submit value=\"Back To Edit Tee Sheet\" style=\"width: 190px\">");
         out.println("<input type=hidden name=mode value=\"" + mode + "\">");
         out.println("<input type=hidden name=name value=\"" + name + "\">");
         out.println("<input type=hidden name=email value=\"" + emailOpt + "\">");
         out.println("<input type=hidden name=index value=\"" + index + "\">");
         out.println("<input type=hidden name=course value=\"" + course + "\">");
        out.println("</form></td>");
        
        out.println("<form method=\"POST\" action=\"/" + rev + "/servlet/Proshop_dsheet\" name=frmBlockers>");
        out.println("<td align=\"center\">");
          out.println("<input type=submit value=\" Update Blockers \"></td>");
          out.println("<input type=hidden name=mode value=\"" + mode + "\">");
          out.println("<input type=hidden name=name value=\"" + name + "\">");
          out.println("<input type=hidden name=setBlockers value=true>");
          out.println("<input type=hidden name=blockers value=true>");
          out.println("<input type=hidden name=index value=\"" + index + "\">");
          out.println("<input type=hidden name=course value=\"" + course + "\">");
          
        out.println("</tr></table>");
    
        out.println("<center><font size=2>");
        out.println("<b>Tee Sheet Legend</b></font><font size=1><br>");
        out.println("<b>F/B:</b>&nbsp;&nbsp;&nbsp;&nbsp;F = Front Nine,&nbsp;&nbsp;&nbsp;B = Back Nine,&nbsp;&nbsp;&nbsp;");
        out.println("O = Open (for cross-overs),&nbsp;&nbsp;&nbsp;S = Shotgun Event</font></center>");
      
         out.println("<br>");
         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" width=\"90%\">");

         out.println("<tr bgcolor=\"#336633\">");
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>Time</b></u>");
               out.println("</font></td>");

            if (course.equals( "-ALL-" )) {

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
               out.println("&nbsp;<u><b>Block</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
               
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Blocker</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
               
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 1</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
    
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 2</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
               
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 3</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
               
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 4</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
            
            if (fivesALL == 1) {
                out.println("<td align=\"center\">");
                   out.println("<font color=\"#FFFFFF\" size=\"2\">");
                   out.println("&nbsp;<u><b>Player 5</b></u>");
                   out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
                   out.println("&nbsp;");
                   out.println("</font></td>");
            }
    
               
    //
    //  Get the tee sheet for this date
    //
    String stringTee = "";

    if (course.equals( "-ALL-" )) {

    //if (club.equals( "cordillera" )) {         // do not show the Short course if Cordillera

       //stringTee = "SELECT * " +
                   //"FROM teecurr2 WHERE date = ? AND courseName != 'Short' ORDER BY time, courseName, fb";

    //} else {

       // select all tee times for all courses
       stringTee = "SELECT * " +
                   "FROM teecurr2 WHERE date = ? ORDER BY time, courseName, fb";
    //} // end if block - Cordillera customization

    } else {

    // select all tee times for a particular course
    stringTee = "SELECT * " +
                "FROM teecurr2 WHERE date = ? AND courseName = ? ORDER BY time, fb";

    } // end if all or 1 course

    // define variables to hold rs field data
    String player1 = "";
    String player2 = "";
    String player3 = "";
    String player4 = "";
    String player5 = "";
    String blocker = "";
    String sfb = "";
    String ampm = "";
    String time = "";
    String tmp_tag = "";
    String teetime_id = "";
    boolean checked = false;
    
    int hr = 0;
    int min = 0;
    int event_type = 0;
    int shotgun = 1;
    int fb = 0;
    int row_id = 1;
    
    try {
    
        pstmtc = con.prepareStatement ( stringTee );

        pstmtc.clearParameters();
        pstmtc.setLong(1, date);
        if (!course.equals( "-ALL-" )) pstmtc.setString(2, course);
        rs = pstmtc.executeQuery();
        
        //out.println("<table align=center border=1 cellspacing=5 cellpadding=3>");
        
        // loop over the returned records that comprise the tee-sheet
        while (rs.next()) {
        
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            courseName = rs.getString("courseName");
            fb = rs.getInt("fb");
            blocker = rs.getString("blocker");
            hr = rs.getInt("hr");
            min = rs.getInt("min");
            event_type = rs.getInt("event_type");
            
            teetime_id = fb + delim + courseName + delim + hr + delim + min;
            
            ampm = " AM";
            if (hr == 12) ampm = " PM";
            if (hr > 12) {
                ampm = " PM";
                hr = hr - 12;    // convert to conventional time
            }
            
            time = hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm;
            
            if (player1.equals("")) player1 = "&nbsp;";
            if (player2.equals("")) player2 = "&nbsp;";
            if (player3.equals("")) player3 = "&nbsp;";
            if (player4.equals("")) player4 = "&nbsp;";
            if (player5.equals("")) player5 = "&nbsp;";
            if (blocker.equals("")) blocker = "&nbsp;";
            
            //
            //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
            //
            sfb = (fb == 1) ? "B" : (fb == 9) ? "0" : (event_type == shotgun) ? "S" : "F";
                  
            //
            //  If course=ALL requested, then set 'fives' option according to this course
            //
            if (course.equals( "-ALL-" )) {
              
               i = 0;
               loopall:
               while (i < courseCount) {
                  if (courseName.equals( courseA.get(i) )) {
                     fives = fivesA.get(i);          // get the 5-some option for this course
                     break loopall;              // exit loop
                  }
                  i++;
               }
            }
            
            // start new row
            out.println("<tr>");
            
            //
            //  Time
            //
            out.println("<td align=\"center\" nowrap>");
            out.println("<font size=\"2\"><b>");
            out.println(time);
            out.println("</b></font></td>");


            
            
            // course name (if not single course)
            if (course.equals( "-ALL-" )) {

                //
                //  Course Name
                //
                // set tmp_i equal to course index #
                //
                for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                    if (courseName.equals(courseA.get(tmp_i))) break;                      
                }
                
                if (tmp_i >= colorMax) tmp_i = (colorMax - 1);         // use White if no color defined

                out.println("<td bgcolor=\"" + colors.course_color[tmp_i] + "\" align=\"center\">");
                out.println("<font size=\"2\">");
                out.println(courseName);
                out.println("</font></td>");
            }

            //
            //  Front/Back Indicator
            //
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(sfb);
            out.println("</font></td>");
            
            //
            //  Blocker Check Box
            //
            out.println("<td align=\"center\">");
            
            // if time is blocked then check the box else do not check it
            checked = (!blocker.equals("") && !blocker.equals("&nbsp;"));
            tmp_tag = ((checked) ? " checked" : "") + " onclick=\"return false\" onmousedown=\"selectRange(event, " + row_id + ")\"";
            
            // allow access to the checkbox if player1 is empty or if not empty but is currently blocked 
            if ( player1.equals("") || player1.equals("&nbsp;") ) {
                
                // tee time is empty
                out.println("<input type=checkbox name=\"" + teetime_id + "\" id=\"chk" + row_id + "\" value=\"1\" " + tmp_tag + ">");
                out.println("<input type=hidden name=\"ID_" + teetime_id + "\" value=\"" + ((checked) ? "1" : "0") + "\">");
                
            } else {
                
                // someone is in this tee time
                if (!checked) {
                    
                    tmp_tag = " disabled"; // disable the check box to prevent user intervention (this is normal/expected behavior)
                    
                } else {
                    
                    // not normal - if here then somehow we have a blocked time that has players in it!
                    out.println("<input type=hidden name=\"ID_" + teetime_id + "\" value=\"" + ((checked) ? "1" : "0") + "\">");
                    
                }
                out.println("<input type=checkbox name=\"" + teetime_id + "\" id=\"chk" + row_id + "\" value=\"1\" " + tmp_tag + ">");
                
            }
            out.println("</td>");
            
            //
            //  Blocker Name
            //
            out.println("<td align=\"center\" nowrap>");
            out.println("<font size=\"2\">");
            out.println(blocker);
            out.println("</font></td>");
            
            //
            //  Player1
            //
            out.println("<td align=\"center\" nowrap>");
            out.println("<font size=\"2\">");
            out.println(player1);
            out.println("</font></td>");
            
            //
            //  Player2
            //
            out.println("<td align=\"center\" nowrap>");
            out.println("<font size=\"2\">");
            out.println(player2);
            out.println("</font></td>");
            
            //
            //  Player3
            //
            out.println("<td align=\"center\" nowrap>");
            out.println("<font size=\"2\">");
            out.println(player3);
            out.println("</font></td>");
            
            //
            //  Player4
            //
            out.println("<td align=\"center\" nowrap>");
            out.println("<font size=\"2\">");
            out.println(player4);
            out.println("</font></td>");
            
            if (fivesALL == 1) {
                //
                //  Player5
                //
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\">");
                out.println(player5);
                out.println("</font></td>");
            }
            
            
            // end row
            out.println("</tr>");
            
            row_id++;
            
        } // end teesheet rs while loop
        
        pstmtc.close();
        
    }
    catch (Exception exp) {
        SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false);
    }
    
    out.println("</form>");
    out.println("</body></html>");
    
 }
 
 
 private void setHoleAssignments(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {

    
    Enumeration elems  = req.getParameterNames();
    PreparedStatement pstmt = null;
    
    String name = "";
    String value = "";
    String tmp = "";
    String sql = "";
    String sindex = req.getParameter("index");
    String course = req.getParameter("course");
    String emailOpt = req.getParameter("email");
                
    // get mode if passed (if no mode, then default to edit tee sheet & notification actions)
    String mode = (req.getParameter("mode") == null) ? "" : req.getParameter("mode");
   
    // get Event Name if passed (if originally came from Event Signup)
    String ename = (req.getParameter("name") == null) ? "" : req.getParameter("name");
   
    int index = 0;
    
    if (sindex == null || sindex.equals( "" ) || sindex.equalsIgnoreCase( "null" )) {

        index = 0;

    } else {

        index = Integer.parseInt(sindex);

    }
        
    Calendar cal = new GregorianCalendar();       // get todays date
    cal.add(Calendar.DATE,index);                  // roll ahead 'index' days
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH) + 1;
    int day = cal.get(Calendar.DAY_OF_MONTH);
    long date = (year * 10000) + (month * 100) + day;    // create a date field of yyyymmdd
    
    while (elems.hasMoreElements()) {

        name = (String)elems.nextElement();
        value = req.getParameter(name);
        sql = "";
        
        if(name.startsWith("ID" + delim) == true) {
            
            tmp = name.substring(3);
            sql = "UPDATE teecurr2 SET hole = ? WHERE teecurr_id = ?";                

            try {

                pstmt = null;

                pstmt = con.prepareStatement (sql);
                pstmt.clearParameters();
                pstmt.setString(1, value);
                pstmt.setInt(2, Integer.parseInt(tmp));
                pstmt.executeUpdate();

                if (pstmt != null) pstmt.close();

            }
            catch (Exception exp) {
                SystemUtils.buildDatabaseErrMsg("Fatal error updating hole assignments.", exp.toString(), out, true);
            } // end try/catch
            
        } // end if form elem we are looking for

    } // end while loop
    
    
    out.println(SystemUtils.HeadTitle("Shotgun Hole Assignments"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("");
    out.println("<center><p><font size=5>Shotgun Hole Assignments</font></p>");
    out.println("<p><font color=green size=3><b>Hole Assignments Updated.</b></font></p><br>");
    
    out.println("<table align=center cellspacing=10><tr>");
    out.println("<td><form method=\"GET\" action=\"/" + rev + "/servlet/Proshop_jump\" name=frmBack>");
    out.println("<input type=submit value=\" Back To Tee Sheet \">");
    out.println("<input type=hidden name=index value=\"" + index + "\">");
    out.println("<input type=hidden name=course value=\"" + course + "\">");
    out.println("</form></td>");
     
    out.println("<td><form method=\"GET\" action=\"/" + rev + "/servlet/Proshop_dsheet\" name=frmBackEdit>");
        out.println("<input type=submit value=\"Back To Edit Tee Sheet\" style=\"width: 190px\">"); // using style tag to hold size back
        out.println("<input type=hidden name=mode value=\"" + mode + "\">");
        out.println("<input type=hidden name=email value=\"" + emailOpt + "\">");
        out.println("<input type=hidden name=index value=\"" + index + "\">");
        out.println("<input type=hidden name=course value=\"" + course + "\">");
        out.println("<input type=hidden name=name value=\"" + ename + "\">");
       out.println("</form></td>");
        
    out.println("<td><form method=\"GET\" action=\"/" + rev + "/servlet/Proshop_dsheet\" name=frmBackHoleAssign>");
        out.println("<input type=submit value=\"Back To Hole Assignments\" style=\"width: 190px\">"); // using style tag to hold size back
        out.println("<input type=hidden name=sha value=\"\">");
        out.println("<input type=hidden name=mode value=\"" + mode + "\">");
        out.println("<input type=hidden name=email value=\"" + emailOpt + "\">");
        out.println("<input type=hidden name=index value=\"" + index + "\">");
        out.println("<input type=hidden name=course value=\"" + course + "\">");
        out.println("<input type=hidden name=name value=\"" + ename + "\">");
       out.println("</form></td>");
       
    out.println("</tr></table>");
    
    out.println("</body></html>");
    out.close();
    return;
    
 }
 
 
 private void displayHoleAssignments(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {
         
    // BEGIN VARIABLE DEFINITIONS
    ResultSet rs = null; 
    Statement stmt = null;
    PreparedStatement pstmtc = null;

    int cMax = 0;  // max # of courses + 1 for -ALL-
    int i = 0;
    int tmp_i = 0; // counter for course[], shading of course field
    int index = 0;
    int day = 0;
    int month = 0;
    int year = 0;
    int courseCount = 0;
    int colorMax = 30;                    // max number of colors defined

    long date = 0;

    String emailOpt = req.getParameter("email");
    String course = req.getParameter("course");
    String sindex = req.getParameter("index");
   
    // get mode if passed (if no mode, then default to edit tee sheet & notification actions)
    String mode = (req.getParameter("mode") == null) ? "" : req.getParameter("mode");

    // get Event Name if passed (if came from Event Signup page)
    String name = (req.getParameter("name") == null) ? "" : req.getParameter("name");

    //  array to hold the course names and colors
    String courseName = "";

    ArrayList<String> courseA = new ArrayList<String>();
   
    ArrayList<Integer> fivesA = new ArrayList<Integer> ();        // array list to hold 5-some option for each course
   
    int fives = 0;
    int fivesALL = 0;
   
    String event1 = "";       // for legend - max 4 events, 4 rest's, 2 lotteries
    String ecolor1 = "";
    String event2 = "";
    String ecolor2 = "";
    String event3 = "";
    String ecolor3 = "";
    String event4 = "";
    String ecolor4 = "";
    String sql;
    String event = "";
    String ecolor = "";
   
    //
    // END OF VARIABLES DEFINITIONS

   

    // BEGIN VARIABLE ASSIGNMENT
    //
    
    
    if (course == null) course = ""; // ensure course isn't null

    if (sindex == null || sindex.equals( "" ) || sindex.equalsIgnoreCase( "null" )) 
     { index = 0; } else { index = Integer.parseInt(sindex); }
   
    //
    // get today's date and add the index to get the requested tee-sheet date
    Calendar cal = new GregorianCalendar();       // get todays date
    cal.add(Calendar.DATE,index);                 // roll ahead 'index' days
    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    day = cal.get(Calendar.DAY_OF_MONTH);
    month = month + 1;                            // month starts at zero
    date = (year * 10000) + (month * 100) + day;  // create a date field of yyyymmdd
    
    
    // all I really should have to do is check to see if we are displaying 1 course or -ALL- courses.
    // if 1 then see if it supports 5 somes
    // if -ALL- then see if any supports 5 somes
    // query and display


    parmClub parm = new parmClub(0, con);
    parmCourse parmc = new parmCourse();          // allocate a parm block
    
    try {
        
        getClub.getParms(con, parm);        // get the club parms
    }
    catch (Exception exp) {
        SystemUtils.buildDatabaseErrMsg("Fatal error allocating parm blocks.", exp.getMessage(), out, true);        
    }
        
    // lookup shotgun events for today
    if (course.equals( "-ALL-" )) {
     sql = "SELECT name, color FROM events2b WHERE date = ? AND inactive = 0 ORDER BY stime";
    } else {
     sql = "SELECT name, color FROM events2b WHERE date = ? AND inactive = 0 " +
                "AND (courseName = ? OR courseName = '-ALL-') ORDER BY stime";
    }
    
    try {
        PreparedStatement pstmt = con.prepareStatement (sql);
        pstmt.clearParameters();          // clear the parms
        pstmt.setLong(1, date);
        if (!course.equals( "-ALL-" )) pstmt.setString(2, course);
        rs = pstmt.executeQuery();      // find all matching events, if any
    
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
        pstmt.close();
    }
    catch (Exception exp) {
        SystemUtils.buildDatabaseErrMsg("Proshop_dsheet - Fatal error loading events.", exp.getMessage(), out, true);        
    }
      
    
     //
     //   Get course names if multi-course facility so we can determine if any support 5-somes
     //
     i = 0;
     if (parm.multi != 0) {           // if multiple courses supported for this club
        
        try {

            courseA = Utilities.getCourseNames(con);     // get all the course names

            if (courseA.size() > 1) {

                courseA.add ("-ALL-");        // add '-ALL-' option
            }
            
        }
        catch (Exception exp) {
            SystemUtils.buildDatabaseErrMsg("Proshop_dsheet - Fatal error allocating course names.", exp.getMessage(), out, true);        
        }
     }

    
    //
    //  Get the walk/cart options available and 5-some support
    //
    i = 0;
    if (course.equals( "-ALL-" )) {

        try {
            
            cMax = courseA.size();     // number of courses  
            
            //
            //  Check all courses for 5-some support
            //
            loopc:
            while (i < cMax) {

                courseName = courseA.get(i);       // get a course name

                if (!courseName.equals( "-ALL-" )) {   // skip if -ALL-

                    if (courseName.equals( "" )) break loopc;

                    getParms.getCourse(con, parmc, courseName);
                    
                    fivesA.add (parmc.fives);      // get fivesome option

                    if (parmc.fives == 1) fives = 1;

                } // end if courseName = -ALL-

                i++;
            } // end while loop

        }
        catch(Exception exp)
        {
            SystemUtils.buildDatabaseErrMsg("Fatal error trying to get course parms. (-ALL-)", exp.getMessage(), out, true);   
        }
        
    } else {       // single course requested

        try {
            getParms.getCourse(con, parmc, course);
        }
        catch (Exception exp)
        {
            SystemUtils.buildDatabaseErrMsg("Fatal error trying to get course parms.", exp.getMessage(), out, true); 
        }
        fives = parmc.fives;      // get fivesome option
    }

    fivesALL = fives;            // fives will be 1 if any course allowed 5-somes

    i = 0;

    // done pre-processing
    
    
    // start tee sheet output
    
    out.println(SystemUtils.HeadTitle("Shotgun Hole Assignments"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("");
    out.println("<center><p><font size=5>Shotgun Hole Assignments</font></p>");
    out.println("<font size=3><b>Date:</b>&nbsp;&nbsp;" + month + "/" + day + "/" + year);
    
    if (!course.equals( "" )) out.println("&nbsp;&nbsp;&nbsp;<b>Course:</b>&nbsp;&nbsp;" + course);
    
    out.println("</font></center><br>");

    out.println("<table align=center width=640 bgcolor=#EAEAEA><tr><td width=640>");
    out.println("<b>Instructions:</b>  Using the text boxes in the first column of each row, enter the " +
            "hole assignment for each team.  Once finshed click the 'Submit Assignments' button located to " +
            "on the right just above table.</td></tr></table>");
    out.println("<br>");
             
    // build tee sheet header
    
        out.println("<table width=\"80%\" align=\"center\"><tr valign=middle>");
        /*
        out.println("<td align=\"center\">");
        out.println("<form method=\"GET\" action=\"/" + rev + "/servlet/Proshop_dsheet\" name=frmRefresh>");
         out.println("<input type=submit value=\" Refresh Sheet \">");
         out.println("<input type=hidden name=sha value=true>");
         out.println("<input type=hidden name=email value=\"" + emailOpt + "\">");
         out.println("<input type=hidden name=index value=\"" + index + "\">");
         out.println("<input type=hidden name=course value=\"" + course + "\">");
        out.println("</form></td>");
        */
        out.println("<td align=\"left\">");
        out.println("<form method=\"GET\" action=\"/" + rev + "/servlet/Proshop_jump\" name=frmBack>");
         out.println("<input type=submit value=\" Back To Tee Sheet \">");
         out.println("<input type=hidden name=index value=\"" + index + "\">");
         out.println("<input type=hidden name=course value=\"" + course + "\">");
        out.println("</form><!--</td>-->");
        
        out.println("<!--<td align=\"center\">-->");
        out.println("<form method=\"GET\" action=\"/" + rev + "/servlet/Proshop_dsheet\" name=frmBackEdit>");
         out.println("<input type=submit value=\"Back To Edit Tee Sheet\" style=\"width: 190px\">"); // using style tag to hold size back
         out.println("<input type=hidden name=email value=\"" + emailOpt + "\">");
         out.println("<input type=hidden name=index value=\"" + index + "\">");
         out.println("<input type=hidden name=course value=\"" + course + "\">");
         out.println("<input type=hidden name=mode value=\"" + mode + "\">");
         out.println("<input type=hidden name=name value=\"" + name + "\">");         // event name if came from event signup
        out.println("</form></td>");
        
        //out.println("</tr><tr>"); // put the submit button on its own line   <tr><td colspan=2>&nbsp;</td></tr>
        
        out.println("<td align=\"right\">");
        out.println("<form method=\"POST\" action=\"/" + rev + "/servlet/Proshop_dsheet\" name=frmHoleAssign>");
        //out.println("<td align=\"right\" colspan=\"2\">");
          out.println("<input type=submit value=\"Submit  Assignments\">");
          out.println("<input type=hidden name=mode value=\"" + mode + "\">");
          out.println("<input type=hidden name=ssha value=true>");
          out.println("<input type=hidden name=sha value=true>");
          out.println("<input type=hidden name=index value=\"" + index + "\">");
          out.println("<input type=hidden name=course value=\"" + course + "\">");
          out.println("<input type=hidden name=email value=\"" + emailOpt + "\"></td>");
          out.println("<input type=hidden name=name value=\"" + name + "\">");         // event name if came from event signup
          out.println("<input type=hidden name=jump value=\"0\"><br></td>");
          
        out.println("</tr></table>");
    
        
        
         //
         // If there is an event, restriction or lottery then show the applicable legend
         //
         if (!event1.equals( "" )) {

            // legend title
            out.println("<center><font size=2>");
            out.println("<b>Today's Events</b> (click on buttons to view info)<br></font>");


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
            out.println("</center>");
         }
         
         //
         // START HEADER ROW
         //
        
         out.println("<br>");
         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" width=\"90%\">");

         out.println("<tr bgcolor=\"#336633\">");
/*            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>Time</b></u>");
               out.println("</font></td>");

            if (course.equals( "-ALL-" )) {

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Course</b></u>");
                  out.println("</font></td>");
            }

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>F/B</b></u>");
               out.println("</font></td>");
*/               
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>Hole Assignment</b></u>");
               out.println("</font></td>");
            /*
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Blocker</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
            */   
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 1</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
    
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 2</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
               
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 3</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
               
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 4</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
            
            if (fivesALL == 1) {
                out.println("<td align=\"center\">");
                   out.println("<font color=\"#FFFFFF\" size=\"2\">");
                   out.println("&nbsp;<u><b>Player 5</b></u>");
                   out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
                   out.println("&nbsp;");
                   out.println("</font></td>");
            }
    
               
    //
    //  Get the tee sheet for this date
    //
    String stringTee = "";

    if (course.equals( "-ALL-" )) {

    //if (club.equals( "cordillera" )) {         // do not show the Short course if Cordillera

       //stringTee = "SELECT * " +
                   //"FROM teecurr2 WHERE date = ? AND courseName != 'Short' ORDER BY time, courseName, fb";

    //} else {

       // select all tee times for all courses
       stringTee = "SELECT * " +
                   "FROM teecurr2 WHERE date = ? AND (event <> NULL OR event <> '') AND event_type = 1 ORDER BY time, courseName, fb";
    //} // end if block - Cordillera customization

    } else {

    // select all tee times for a particular course
    stringTee = "SELECT * " +
                "FROM teecurr2 WHERE date = ? AND courseName = ? AND (event <> NULL OR event <> '') AND event_type = 1 ORDER BY time, fb";

    } // end if all or 1 course

    // define variables to hold rs field data
    String player1 = "";
    String player2 = "";
    String player3 = "";
    String player4 = "";
    String player5 = "";
    String hole = "";
    String sfb = "";
    String ampm = "";
    String time = "";
    String checked = "";
    String teetime_id = "";
    String event_color = "";
    
    int hr = 0;
    int min = 0;
    int event_type = 0;
    int shotgun = 1;
    int fb = 0;
    int teecurr_id = 0;
    
    try {
    
        pstmtc = con.prepareStatement ( stringTee );

        pstmtc.clearParameters();
        pstmtc.setLong(1, date);
        if (!course.equals( "-ALL-" )) pstmtc.setString(2, course);
        rs = pstmtc.executeQuery();
        
        //out.println("<table align=center border=1 cellspacing=5 cellpadding=3>");
        
        // loop over the returned records that comprise the tee-sheet
        while (rs.next()) {
        
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            
            event_color = rs.getString("event_color");
            
            // can i not just check player1 here?
            if (!player1.equals("") || !player2.equals("") || !player3.equals("") || !player4.equals("") || !player5.equals("")) {
                courseName = rs.getString("courseName");
                fb = rs.getInt("fb");
                hole = rs.getString("hole");
                hr = rs.getInt("hr");
                min = rs.getInt("min");
                event_type = rs.getInt("event_type");
                teecurr_id = rs.getInt("teecurr_id");
                
                //teetime_id = fb + delim + courseName + delim + hr + delim + min;
                teetime_id = "ID" + delim + teecurr_id;

                ampm = " AM";
                if (hr == 12) ampm = " PM";
                if (hr > 12) {
                    ampm = " PM";
                    hr = hr - 12;    // convert to conventional time
                }
                
                time = hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm;

                if (player1.equals("")) player1 = "&nbsp;";
                if (player2.equals("")) player2 = "&nbsp;";
                if (player3.equals("")) player3 = "&nbsp;";
                if (player4.equals("")) player4 = "&nbsp;";
                if (player5.equals("")) player5 = "&nbsp;";

                //
                //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
                //
                sfb = (fb == 1) ? "B" : (fb == 9) ? "0" : (event_type == shotgun) ? "S" : "F";

                //
                //  If course=ALL requested, then set 'fives' option according to this course
                //
                if (course.equals( "-ALL-" )) {

                      i = 0;
                      loopall:
                      while (i < courseA.size()) {
                         if (courseName.equals( courseA.get(i) )) {
                            fives = fivesA.get(i);           // get the 5-some option for this course
                            break loopall;              // exit loop
                         }
                         i++;
                      }

                }

                // start new row
                out.println("<tr bgcolor=\"" + event_color + "\">");

                //
                //  Time
                //
                /*
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\"><b>");
                out.println(time);
                out.println("</b></font></td>");


                // course name (if not single course)
                if (course.equals( "-ALL-" )) {

                    //
                    //  Course Name
                    //
                    // set tmp_i equal to course index #
                    //
                    for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                        if (courseName.equals(courseA[tmp_i])) break;                      
                    }

                    out.println("<td bgcolor=\"" + course_color[tmp_i] + "\" align=\"center\">");
                    out.println("<font size=\"2\">");
                    out.println(courseName);
                    out.println("</font></td>");
                }

                //
                //  Front/Back Indicator
                //
                out.println("<td bgcolor=\"white\" align=\"center\">");
                out.println("<font size=\"2\">");
                out.println(sfb);
                out.println("</font></td>");
*/
                //
                //  Hole Assignment Text Box
                //
                out.println("<td bgcolor=\"#F5F5DC\" align=\"center\">");
                out.println("<font size=\"2\">");
                out.println("<input type=text name=\"" + teetime_id + "\" value=\"" + hole + "\" size=6 maxlength=4>");
                out.println("</font></td>");
                /*
                //
                //  Blocker Name
                //
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\">");
                out.println(blocker);
                out.println("</font></td>");
                */
                //
                //  Player1
                //
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\">");
                out.println(player1);
                out.println("</font></td>");

                //
                //  Player2
                //
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\">");
                out.println(player2);
                out.println("</font></td>");

                //
                //  Player3
                //
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\">");
                out.println(player3);
                out.println("</font></td>");

                //
                //  Player4
                //
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\">");
                out.println(player4);
                out.println("</font></td>");

                if (fivesALL == 1) {
                    //
                    //  Player5
                    //
                    out.println("<td align=\"center\" nowrap>");
                    out.println("<font size=\"2\">");
                    out.println(player5);
                    out.println("</font></td>");
                }


                // end row
                out.println("</tr>");

            } // end teesheet rs while loop
        
        } // end if there is at least 1 player signed up for the event
        
        pstmtc.close();
        
    }
    catch (Exception exp) {
        SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false);
    }
    
    out.println("</form>");
    out.println("</body></html>");
    
 }
 

 //
 // 
 //
 private void setBlockers(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {
        
    // the form should be here with each tee-time and its new 'block' setting
    // loop thru each element and locate the respective tee-time and update the blocker field

    Enumeration elems  = req.getParameterNames();
    
    String name = "";
    String value = "";
    String tmp = "";
    String sindex = req.getParameter("index");
    int index = 0;
    
    if (sindex == null || sindex.equals( "" ) || sindex.equalsIgnoreCase( "null" )) {

        index = 0;

    } else {

        index = Integer.parseInt(sindex);

    }
    
    Calendar cal = new GregorianCalendar();       // get todays date
    cal.add(Calendar.DATE,index);                  // roll ahead 'index' days
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH) + 1;
    int day = cal.get(Calendar.DAY_OF_MONTH);
    long date = (year * 10000) + (month * 100) + day;    // create a date field of yyyymmdd
    
    String sqlBlock = "UPDATE teecurr2 SET blocker = \"Auto-Blocker\", auto_blocked = 1 WHERE blocker = \"\" AND date = ? AND CONCAT_WS(\"_\", fb, courseName, hr, min) IN ("; 
    String sqlUnblock = "UPDATE teecurr2 SET blocker = \"\", auto_blocked = 1 WHERE date = ? AND CONCAT_WS(\"_\", fb, courseName, hr, min) IN ("; // player1 = \"\" AND   - removed so we can unblock times w/ players in there
    
    boolean skipBlocks = true;
    boolean skipUnblocks = true;
    
    while (elems.hasMoreElements()) {

        name = (String)elems.nextElement();
        value = req.getParameter(name);

        if(name.startsWith("ID_") == true) {

            tmp = name.substring(3);

            //if (value.equals(req.getParameter(tmp))) {
            if (req.getParameter(tmp) != null && req.getParameter(tmp).equals("1")) {

                sqlBlock += "\"" + tmp + "\",";
                skipBlocks = false;
                
            } else {

                if (value.equals("1")) {
                    sqlUnblock += "\"" + tmp + "\",";
                    skipUnblocks = false;
                }
            }

        }

    }
     
    sqlBlock = sqlBlock.substring(0, sqlBlock.length() - 1);
    sqlUnblock = sqlUnblock.substring(0, sqlUnblock.length() - 1);

    sqlBlock += ")";
    sqlUnblock += ")";

    //out.println("<p>" + sqlBlock + "</p>");
    //out.println("<p>" + sqlUnblock + "</p>");
    //out.println("<p>" + skipBlocks + " | " + skipUnblocks + "</p>");
     
    try {

        PreparedStatement pstmt = null;
            
        if (skipUnblocks != true) {
            pstmt = con.prepareStatement (sqlUnblock);
            pstmt.clearParameters();
            pstmt.setLong(1, date);
            pstmt.executeUpdate();
            //out.println("<p>UNBLOCK=" + sqlUnblock + "</p>");
        }
        
        if (skipBlocks != true) {
            pstmt = con.prepareStatement (sqlBlock);
            pstmt.clearParameters();
            pstmt.setLong(1, date);
            pstmt.executeUpdate();
            //out.println("<p>BLOCK=" + sqlBlock + "</p>");
        }
        
        if (pstmt != null) pstmt.close();

    }
    catch (Exception exp) {
        SystemUtils.buildDatabaseErrMsg("Fatal error updating tee time blockers.", exp.toString(), out, false);
    }
     
     
    return;
     
 }  // end setBlockers

 
 
 //
 // Convert a notification and add to teecurr2 table
 //
 private void convertNotification(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


    Statement stmt = null;
    ResultSet rs = null;

    //
    //  Get this session's attributes
    //
    String user = "";
    String club = "";
    user = (String)session.getAttribute("user");
    club = (String)session.getAttribute("club");

    boolean overrideAccess = SystemUtils.verifyProAccess(req, "REST_OVERRIDE", con, out);

    int notify_id = 0;
    int teecurr_id = 0;
    String snid = "";
    String stid = "";
    if (req.getParameter("to_tid") != null) stid = req.getParameter("to_tid");
    if (req.getParameter("notifyId") != null) snid = req.getParameter("notifyId");
    
    //
    //  Convert the values from string to int
    //
    try {
        
        notify_id = Integer.parseInt(snid);
        teecurr_id = Integer.parseInt(stid);
    }
    catch (NumberFormatException e) {
    }
    
    int reject = 0;
    int count = 0;
    int time2 = 0;
    int fb2 = 0;
    int t_fb = 0;
    int x = 0;
    int xhrs = 0;
    int xError = 0;
    int xUsed = 0;
    int hide = 0;
    int i = 0;
    int mm = 0;
    int yy = 0;
    int dd = 0;
    int fb = 0;
    int time = 0;
    int mtimes = 0;
    int year = 0;
    int month = 0;
    int day = 0;
    int ind = 0;
    int index = 0;
    int temp = 0;
    int sendemail = 0;
    int emailNew = 0;
    int emailMod = 0;
    int emailCan = 0;
    int gi = 0;
    int proNew = 0;
    int proMod = 0;
    int skip = 0;
    int pos1 = 0;
    int pos2 = 0;
    int pos3 = 0;
    int pos4 = 0;
    int pos5 = 0;
    int event_type = 0;
    int fives = 0;
    int guest_id1 = 0;
    int guest_id2 = 0;
    int guest_id3 = 0;
    int guest_id4 = 0;
    int guest_id5 = 0;

    long date = 0;
    long dateStart = 0;
    long dateEnd = 0;

    String player = "";
    String mperiod = "";
    String course2 = "";
    String memberName = "";
    String mship = "";
    String mtype = "";
    String skips = "";
    String p9s = "";
    String event = "";
    String suppressEmails = "no";
    String sponsored = "Spons";
    String msg = "";

    boolean hit = false;
    boolean hit2 = false;
    boolean check = false;
    boolean guestError = false;
    boolean error = false;
    boolean posSent = false;
    boolean overRideFives = false;

    int [] mtimesA = new int [8];          // array to hold the mship max # of rounds value
    String [] periodA = new String [8];    // array to hold the mship periods (week, month, year)

    //
    //  Arrays to hold member & guest names to tie guests to members
    //
    String [] memA = new String [5];     // members
    String [] usergA = new String [5];   // guests' associated member (username)

    //
    //  parm block to hold the club parameters
    //
    parmClub parm = new parmClub(0, con);

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
/*
    String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
    String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
    String smm = req.getParameter("mm");               //  month of tee time
    String syy = req.getParameter("yy");               //  year of tee time
 */
    String sindex = req.getParameter("index");          //  day index value (needed by _sheet on return)
    String returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
    if (sindex != null) {

        index = Integer.parseInt(sindex);
    }

    
    if (req.getParameter("suppressEmails") != null) {             // if email parm exists
        suppressEmails = req.getParameter("suppressEmails");
    }
    
    if (req.getParameter("overRideFives") != null && req.getParameter("overRideFives").equals("yes")) {             // if email parm exists
        overRideFives = true;
    }

    // get mode if passed (if no mode, then default to edit tee sheet & notification actions)
    String mode = (req.getParameter("mode") == null) ? "" : req.getParameter("mode");
   
    //
    //  Get skip parm if provided
    //
    if (req.getParameter("skip") != null) {

        skips = req.getParameter("skip");
        skip = Integer.parseInt(skips);
    }
    
    
    try {

        PreparedStatement pstmtc = con.prepareStatement (
            "SELECT fives " + 
            "FROM clubparm2 c, teecurr2 t " + 
            "WHERE c.courseName = t.courseName AND t.teecurr_id = ?");

        pstmtc.clearParameters();
        pstmtc.setInt(1, teecurr_id);
        rs = pstmtc.executeQuery();

        if (rs.next()) {

            fives = rs.getInt("fives");
        }
        pstmtc.close();
    }
    catch (Exception e) {
    }
    
      
    //
    //  put parms in Parameter Object for portability
    //
    
    // commented these out 6-5-08, these haven't been set yet
    //slotParms.date = date;
    //slotParms.time = time;
    //slotParms.mm = mm;
    //slotParms.yy = yy;
    //slotParms.dd = dd;
    //slotParms.fb = fb;
    
    slotParms.ind = index;                      // index value
    slotParms.club = club;                    // name of club
    slotParms.returnCourse = returnCourse;    // name of course for return to _sheet
    slotParms.suppressEmails = suppressEmails;
    
    
    //
    //  Check if this tee slot is still 'in use' and still in use by this user??
    //
    try {

        PreparedStatement pstmt = con.prepareStatement("SELECT * FROM teecurr2 WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, teecurr_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            slotParms.player1 = rs.getString("player1");
            slotParms.player2 = rs.getString("player2");
            slotParms.player3 = rs.getString("player3");
            slotParms.player4 = rs.getString("player4");
            slotParms.player5 = rs.getString("player5");
            slotParms.user1 = rs.getString("username1");
            slotParms.user2 = rs.getString("username2");
            slotParms.user3 = rs.getString("username3");
            slotParms.user4 = rs.getString("username4");
            slotParms.user5 = rs.getString("username5");
            slotParms.p1cw = rs.getString("p1cw");
            slotParms.p2cw = rs.getString("p2cw");
            slotParms.p3cw = rs.getString("p3cw");
            slotParms.p4cw = rs.getString("p4cw");
            slotParms.p5cw = rs.getString("p5cw");
            slotParms.mNum1 = rs.getString("mNum1");
            slotParms.mNum2 = rs.getString("mNum2");
            slotParms.mNum3 = rs.getString("mNum3");
            slotParms.mNum4 = rs.getString("mNum4");
            slotParms.mNum5 = rs.getString("mNum5");
            slotParms.in_use = rs.getInt("in_use");
            slotParms.in_use_by = rs.getString("in_use_by");
            slotParms.userg1 = rs.getString("userg1");
            slotParms.userg2 = rs.getString("userg2");
            slotParms.userg3 = rs.getString("userg3");
            slotParms.userg4 = rs.getString("userg4");
            slotParms.userg5 = rs.getString("userg5");
            slotParms.orig_by = rs.getString("orig_by");
            slotParms.pos1 = rs.getShort("pos1");
            slotParms.pos2 = rs.getShort("pos2");
            slotParms.pos3 = rs.getShort("pos3");
            slotParms.pos4 = rs.getShort("pos4");
            slotParms.pos5 = rs.getShort("pos5");
            slotParms.rest5 = rs.getString("rest5");
            slotParms.guest_id1 = rs.getInt("guest_id1");
            slotParms.guest_id2 = rs.getInt("guest_id2");
            slotParms.guest_id3 = rs.getInt("guest_id3");
            slotParms.guest_id4 = rs.getInt("guest_id4");
            slotParms.guest_id5 = rs.getInt("guest_id5");
            proNew = rs.getInt("proNew");
            proMod = rs.getInt("proMod");               
            event = rs.getString("event");
            event_type = rs.getInt("event_type");
            
            // added this 6-5-08, history not working, history uses slotParm.time 
            // which had been set to the time requested, not the tee time the notification was dragged to
            slotParms.time = rs.getInt("time");
            slotParms.fb = rs.getInt("fb");
            slotParms.course = rs.getString("courseName");
            
        }
        out.println("<!-- DONE LOADING slotParms WITH teecurr2 DATA -->");
        pstmt.close();

    }
    catch (Exception e) {

        out.println("<p>Error: "+e.toString()+"</p>");
    }

    // make sure there are enough open player slots
    int open_slots = 0;
    boolean has_players = false;
    if (slotParms.player1.equals("")) { open_slots++; } else { has_players = true; }
    if (slotParms.player2.equals("")) open_slots++;
    if (slotParms.player3.equals("")) open_slots++;
    if (slotParms.player4.equals("")) open_slots++;
    if (slotParms.player5.equals("") && slotParms.rest5.equals("") && fives == 1) open_slots++;

    if (slotParms.orig_by.equals( "" )) {    // if originator field still empty (allow this person to grab this tee time again)

        slotParms.orig_by = user;             // set this user as the originator
    }
    
    //out.println("<!-- open_slots="+open_slots+" | has_players="+has_players+" -->");
    
    if (slotParms.in_use == 1 && !slotParms.in_use_by.equalsIgnoreCase( user )) {    // if time slot in use and not by this user

        out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<CENTER><BR><BR><H1>Reservation Timer Expired</H1>");
        out.println("<BR><BR>Sorry, but this tee time slot has been returned to the system!<BR>");
        out.println("<BR>The system timed out and released the tee time.");
        out.println("<BR><BR>");

        if (index == 888 ) {      // if from Proshop_searchmem via proshop_main

            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

        } else {

            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
            if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
            } else {
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
            }
            out.println("</form></font>");
        }
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    boolean teeTimeFull = false;
    boolean allowFives = (fives == 1) ? true : false;
    int tmp_added = 0;
    
    //
    try {

        // load general data from notitification
        PreparedStatement pstmt = con.prepareStatement(
            "SELECT *, " +
                "DATE_FORMAT(req_datetime, '%l') AS hh, " + 
                "DATE_FORMAT(req_datetime, '%i') AS min, " + 
                "DATE_FORMAT(req_datetime, '%Y') AS yy, " + 
                "DATE_FORMAT(req_datetime, '%m') AS mm, " + 
                "DATE_FORMAT(req_datetime, '%d') AS dd, " +
                "c.courseName " +
            "FROM notifications, clubparm2 c " +
            "WHERE notification_id = ? AND in_use_by = '' AND course_id = c.clubparm_id");
        
        pstmt.clearParameters();
        pstmt.setInt(1, notify_id);
        rs = pstmt.executeQuery();
        
        if (rs.next()) {

            //slotParms.time = (rs.getInt( "hh" ) * 100 + rs.getInt("min")); // commented out 6-5-08 - should be the tee time dragged to NOT the requested time
            slotParms.date = (rs.getInt( "yy" ) * 10000 + rs.getInt("mm") * 100 + rs.getInt("dd"));
            slotParms.yy = rs.getInt( "yy" );
            slotParms.mm = rs.getInt( "mm" );
            slotParms.dd = rs.getInt( "dd" );
            //slotParms.course = rs.getString( "courseName" ); // commented out 6-5-08 - should be the tee time dragged to NOT the requested time
            slotParms.last_user = rs.getString( "in_use_by" );
            slotParms.in_use = (slotParms.last_user.equals("")) ? 0 : 1; //rs.getInt( "in_use" );
            slotParms.notes = rs.getString( "notes" );

        } else {
            
            out.println(SystemUtils.HeadTitle("Notification User"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<CENTER><BR><BR><H2>The notification you selected seems to be busy.</H2>");
            out.println("<BR>Please try again in a few minutes.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\""+index+"\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
            if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
            } else {
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
            }
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            
            out.close();
            return;
            
        }
        pstmt.close();

        String userg = "";
        String usert = "";
        
        // load the player data from notification
        pstmt = con.prepareStatement (
            "SELECT np.*, (SELECT memNum FROM member2b WHERE np.username = username) AS mNum " +
            "FROM notifications_players np " +
            "WHERE notification_id = ? " +
            "ORDER BY pos");
        
        pstmt.clearParameters();
        pstmt.setInt(1, notify_id);
        rs = pstmt.executeQuery();
        
        // add the first player for this notification
        if (rs.next()) {
           
            usert = rs.getString( "username" );       // get username          
            if (usert == null) usert = "";       
            
            teeTimeFull = addPlayer(slotParms, rs.getString( "player_name" ), usert, rs.getString( "mNum" ), rs.getString( "cw" ), userg, rs.getInt( "9hole" ), rs.getInt("guest_id"), allowFives);
            if (!teeTimeFull) tmp_added++;
            userg = usert;
        }
        
        if (rs.next() && !teeTimeFull) {
            
            usert = rs.getString( "username" );       // get username          
            if (usert == null) usert = "";       
            
            if (!usert.equals("") || rs.getString( "player_name" ).equalsIgnoreCase("x")) userg = "";
            teeTimeFull = addPlayer(slotParms, rs.getString( "player_name" ), usert, rs.getString( "mNum" ), rs.getString( "cw" ), userg, rs.getInt( "9hole" ), rs.getInt("guest_id"), allowFives);
            if (!teeTimeFull) tmp_added++;
            if (!usert.equals("")) userg = usert;
        }
        
        if (rs.next() && !teeTimeFull) {
            
            usert = rs.getString( "username" );       // get username          
            if (usert == null) usert = "";       
            
            if (!usert.equals("") || rs.getString( "player_name" ).equalsIgnoreCase("x")) userg = "";
            teeTimeFull = addPlayer(slotParms, rs.getString( "player_name" ), usert, rs.getString( "mNum" ), rs.getString( "cw" ), userg, rs.getInt( "9hole" ), rs.getInt("guest_id"), allowFives);
            if (!teeTimeFull) tmp_added++;
            if (!usert.equals("")) userg = usert;
        }
        
        if (rs.next() && !teeTimeFull) {
            
            usert = rs.getString( "username" );       // get username          
            if (usert == null) usert = "";       
            
            if (!usert.equals("") || rs.getString( "player_name" ).equalsIgnoreCase("x")) userg = "";
            teeTimeFull = addPlayer(slotParms, rs.getString( "player_name" ), usert, rs.getString( "mNum" ), rs.getString( "cw" ), userg, rs.getInt( "9hole" ), rs.getInt("guest_id"), allowFives);
            if (!teeTimeFull) tmp_added++;
            if (!usert.equals("")) userg = usert;
        }
        
        if (rs.next() && !teeTimeFull && fives == 1) {
            
            usert = rs.getString( "username" );       // get username          
            if (usert == null) usert = "";       
            
            if (!usert.equals("") || rs.getString( "player_name" ).equalsIgnoreCase("x")) userg = "";
            teeTimeFull = addPlayer(slotParms, rs.getString( "player_name" ), usert, rs.getString( "mNum" ), rs.getString( "cw" ), userg, rs.getInt( "9hole" ), rs.getInt("guest_id"), allowFives);
            if (!teeTimeFull) tmp_added++;
        }
        
        pstmt.close();
      
    }
    catch (Exception e) {

        msg = "Loading slotParms with notification data. ";
        out.println("ERROR: " + msg + " -- " + e.toString());
        //dbError(out, e, msg);
        return;
    }  

    //out.println("<!-- notify_id="+notify_id+" | teecurr_id="+teecurr_id+" | tmp_added="+tmp_added+" -->");
    
    // notify user if we couldn't fit all players into this tee time
    if (teeTimeFull) {

        out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<CENTER><BR><BR><H1>Too Many Players</H1>");
        out.println("<BR><BR>Sorry, but it appears you have tried to add too many players to this tee time.<BR>");
        out.println("<BR><BR>");
        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        } else {
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
        }
        out.println("</form></font>");

        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    
    // first lets see if they are trying to fill the 5th player slot when it is restricted
    if ( !slotParms.player5.equals("") && ((!slotParms.rest5.equals("") && overRideFives == false) || fives == 0)) {
    
        out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Member Restricted</H3><BR>");
        out.println("<BR>Sorry, <b>5-somes</b> are restricted during this time.<br><br>");

        if (overrideAccess) {
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");
        }
       
        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");

        if (!overrideAccess) {
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
        } else {
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
            out.println("<input type=\"hidden\" name=\"convert\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"overRideFives\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"notifyId\" value=\"" + notify_id + "\">");
            out.println("<input type=\"hidden\" name=\"to_tid\" value=\"" + teecurr_id + "\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
        }
        
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    //out.println("<!-- notify_id="+notify_id+" | teecurr_id="+teecurr_id+" -->");
    //out.println("<!-- slotParms.player5="+slotParms.player5+" | slotParms.rest5="+slotParms.rest5+" | overRideFives="+overRideFives+" -->");
    //out.println("<!-- open_slots="+open_slots+" | slotParms.players="+slotParms.players+" -->");
    
 
    //
    //  Before we update the tee time, go check for any flags to be added to members' names for the pro tee sheet
    //
    verifySlot.checkTFlag(slotParms, con);


    //
    // Update entry in teecurr2
    //
    try {

        PreparedStatement pstmt6 = con.prepareStatement (
             "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
             "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
             "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
             "hndcp4 = ?, player5 = ?, username5 = ?, " + 
             "p5cw = ?, hndcp5 = ?, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " +
             "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
             "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, conf = ?, " +
             "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, pos1 = ?, pos2 = ?, pos3 = ?, pos4 = ?, pos5 = ?, " +
             "tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, " +
             "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ? " +
             "WHERE teecurr_id = ?");

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
        pstmt6.setString(17, slotParms.player5);
        pstmt6.setString(18, slotParms.user5);
        pstmt6.setString(19, slotParms.p5cw);
        pstmt6.setFloat(20, slotParms.hndcp5);
        pstmt6.setString(21, slotParms.notes);
        pstmt6.setInt(22, hide);
        pstmt6.setInt(23, proNew);
        pstmt6.setInt(24, proMod);
        pstmt6.setString(25, slotParms.mNum1);
        pstmt6.setString(26, slotParms.mNum2);
        pstmt6.setString(27, slotParms.mNum3);
        pstmt6.setString(28, slotParms.mNum4);
        pstmt6.setString(29, slotParms.mNum5);
        pstmt6.setString(30, slotParms.userg1);
        pstmt6.setString(31, slotParms.userg2);
        pstmt6.setString(32, slotParms.userg3);
        pstmt6.setString(33, slotParms.userg4);
        pstmt6.setString(34, slotParms.userg5);
        pstmt6.setString(35, slotParms.orig_by);
        pstmt6.setString(36, slotParms.conf);
        pstmt6.setInt(37, slotParms.p91);
        pstmt6.setInt(38, slotParms.p92);
        pstmt6.setInt(39, slotParms.p93);
        pstmt6.setInt(40, slotParms.p94);
        pstmt6.setInt(41, slotParms.p95);
        pstmt6.setInt(42, slotParms.pos1);
        pstmt6.setInt(43, slotParms.pos2);
        pstmt6.setInt(44, slotParms.pos3);
        pstmt6.setInt(45, slotParms.pos4);
        pstmt6.setInt(46, slotParms.pos5);
        pstmt6.setString(47, slotParms.tflag1);
        pstmt6.setString(48, slotParms.tflag2);
        pstmt6.setString(49, slotParms.tflag3);
        pstmt6.setString(50, slotParms.tflag4);
        pstmt6.setString(51, slotParms.tflag5);
        pstmt6.setInt(52, slotParms.guest_id1);
        pstmt6.setInt(53, slotParms.guest_id2);
        pstmt6.setInt(54, slotParms.guest_id3);
        pstmt6.setInt(55, slotParms.guest_id4);
        pstmt6.setInt(56, slotParms.guest_id5);

        pstmt6.setInt(57, teecurr_id);

        count = pstmt6.executeUpdate();      // execute the prepared stmt

        //
        // Update the converted flag, in_use indicators and timestamp in the notifications table
        //      
        pstmt6 = con.prepareStatement ("" +
                "UPDATE notifications " +
                "SET " +
                    "in_use_by = '', in_use_at = '0000-00-00 00:00:00', " +
                    "converted_at = now(), converted = 1, " +
                    "teecurr_id = ?, converted_by = ? " +
                "WHERE notification_id = ?");
        pstmt6.clearParameters();
        pstmt6.setInt(1, teecurr_id);
        pstmt6.setString(2, user);
        pstmt6.setInt(3, notify_id);
        pstmt6.executeUpdate();
        
        pstmt6.close();

    }
    catch (Exception e1) {

        out.println(SystemUtils.HeadTitle("DB Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
        out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<center>");
        out.println("<BR><BR><H2>Database Access Error</H2>");
        out.println("<BR><BR>Unable to save entry.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>" + e1.getMessage());
        out.println("<BR><BR>");
        out.close();
        return;
    }

   //
   //  Track the history of this tee time - make entry in 'teehist' table (check if new or update)
   //
   String fullName = "Notification -> Tee Sheet";
     
   if (slotParms.oldPlayer1.equals( "" ) && slotParms.oldPlayer2.equals( "" ) && slotParms.oldPlayer3.equals( "" ) &&
       slotParms.oldPlayer4.equals( "" ) && slotParms.oldPlayer5.equals( "" )) {

      //  new tee time
      SystemUtils.updateHist(slotParms.date, slotParms.day, slotParms.time, slotParms.fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
                             slotParms.player4, slotParms.player5, user, fullName, 0, con);

   } else {

      //  update tee time
      SystemUtils.updateHist(slotParms.date, slotParms.day, slotParms.time, slotParms.fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
                             slotParms.player4, slotParms.player5, user, fullName, 1, con);
   }
   

    try {

        resp.flushBuffer();      // force the repsonse to complete
    } catch (Exception ignore) {
    }

    out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_dsheet?mode=" +mode+ "&index=" + slotParms.ind + "&course=" + slotParms.returnCourse + "&jump=" + slotParms.jump + "\">");

    out.close();
    return;
 }
 
 
 //
 // Convert a wait list signup to a tee time
 //
 private void convertWaitListSgnUp(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {
 

    Statement stmt = null;
    ResultSet rs = null;

    //
    //  Get this session's attributes
    //
    String user = "";
    String club = "";
    user = (String)session.getAttribute("user");
    club = (String)session.getAttribute("club");

    int wls_id = 0;
    int teecurr_id = 0;
    String snid = "";
    String stid = "";
    if (req.getParameter("to_tid") != null) stid = req.getParameter("to_tid");
    if (req.getParameter("wlsId") != null) snid = req.getParameter("wlsId");

    boolean overrideAccess = SystemUtils.verifyProAccess(req, "REST_OVERRIDE", con, out);
    
    //
    //  Convert the values from string to int
    //
    try {
        
        wls_id = Integer.parseInt(snid);
        teecurr_id = Integer.parseInt(stid);
    }
    catch (NumberFormatException e) {
    }
    
    int reject = 0;
    int count = 0;
    int time2 = 0;
    int fb2 = 0;
    int t_fb = 0;
    int x = 0;
    int xhrs = 0;
    int xError = 0;
    int xUsed = 0;
    int i = 0;
    int mm = 0;
    int yy = 0;
    int dd = 0;
    int fb = 0;
    int time = 0;
    int mtimes = 0;
    int year = 0;
    int month = 0;
    int day = 0;
    int ind = 0;
    int index = 0;
    int temp = 0;
    int sendemail = 0;
    int emailNew = 0;
    int emailMod = 0;
    int emailCan = 0;
    int gi = 0;
    int proNew = 0;
    int proMod = 0;
    int skip = 0;
    int pos1 = 0;
    int pos2 = 0;
    int pos3 = 0;
    int pos4 = 0;
    int pos5 = 0;
    int event_type = 0;
    int fives = 0;
    int guest_id1 = 0;
    int guest_id2 = 0;
    int guest_id3 = 0;
    int guest_id4 = 0;
    int guest_id5 = 0;

    long date = 0;
    long dateStart = 0;
    long dateEnd = 0;

    String player = "";
    String mperiod = "";
    String course2 = "";
    String memberName = "";
    String mship = "";
    String mtype = "";
    String skips = "";
    String p9s = "";
    String event = "";
    String suppressEmails = "no";
    String sponsored = "Spons";
    String msg = "";
    String mode = "WAITLIST";

    boolean hit = false;
    boolean hit2 = false;
    boolean check = false;
    boolean guestError = false;
    boolean error = false;
    boolean posSent = false;
    boolean overRideFives = false;

    int [] mtimesA = new int [8];          // array to hold the mship max # of rounds value
    String [] periodA = new String [8];    // array to hold the mship periods (week, month, year)

    //
    //  Arrays to hold member & guest names to tie guests to members
    //
    String [] memA = new String [5];     // members
    String [] usergA = new String [5];   // guests' associated member (username)

    //
    //  parm block to hold the club parameters
    //
    parmClub parm = new parmClub(0, con);

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
/*
    String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
    String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
    String smm = req.getParameter("mm");               //  month of tee time
    String syy = req.getParameter("yy");               //  year of tee time
 */
    String sindex = req.getParameter("index");          //  day index value (needed by _sheet on return)
    String returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
    if (sindex != null) {

        index = Integer.parseInt(sindex);
    }
    
    String name = (req.getParameter("name") != null) ? req.getParameter("name") : "";
    String hide = (req.getParameter("hide") != null) ? req.getParameter("hide") : "";
    String email = (req.getParameter("email") != null) ? req.getParameter("email") : ""; // not currently used for wait list, but...
    
    if (req.getParameter("suppressEmails") != null) {             // if email parm exists
        suppressEmails = req.getParameter("suppressEmails");
    }
    
    if (req.getParameter("overRideFives") != null && req.getParameter("overRideFives").equals("yes")) {             // if email parm exists
        overRideFives = true;
    }

    //
    //  Get skip parm if provided
    //
    if (req.getParameter("skip") != null) {

        skips = req.getParameter("skip");
        skip = Integer.parseInt(skips);
    }
    
    
    try {

        PreparedStatement pstmtc = con.prepareStatement (
            "SELECT fives " + 
            "FROM clubparm2 c, teecurr2 t " + 
            "WHERE c.courseName = t.courseName AND t.teecurr_id = ?");

        pstmtc.clearParameters();
        pstmtc.setInt(1, teecurr_id);
        rs = pstmtc.executeQuery();

        if (rs.next()) {

            fives = rs.getInt("fives");
        }
        pstmtc.close();
    }
    catch (Exception e) {
    }
    
      
    //
    //  put parms in Parameter Object for portability
    //
    
    // commented these out 6-5-08, these haven't been set yet
    //slotParms.date = date;
    //slotParms.time = time;
    //slotParms.mm = mm;
    //slotParms.yy = yy;
    //slotParms.dd = dd;
    //slotParms.fb = fb;
    
    slotParms.ind = index;                      // index value
    slotParms.club = club;                      // name of club
    slotParms.returnCourse = returnCourse;      // name of course for return to _sheet
    slotParms.suppressEmails = suppressEmails;
    
    
    //
    //  Check if this tee slot is still 'in use' and still in use by this user??
    //
    try {

        PreparedStatement pstmt = con.prepareStatement("SELECT * FROM teecurr2 WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, teecurr_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            
            slotParms.player1 = rs.getString("player1");
            slotParms.player2 = rs.getString("player2");
            slotParms.player3 = rs.getString("player3");
            slotParms.player4 = rs.getString("player4");
            slotParms.player5 = rs.getString("player5");
            slotParms.user1 = rs.getString("username1");
            slotParms.user2 = rs.getString("username2");
            slotParms.user3 = rs.getString("username3");
            slotParms.user4 = rs.getString("username4");
            slotParms.user5 = rs.getString("username5");
            slotParms.p1cw = rs.getString("p1cw");
            slotParms.p2cw = rs.getString("p2cw");
            slotParms.p3cw = rs.getString("p3cw");
            slotParms.p4cw = rs.getString("p4cw");
            slotParms.p5cw = rs.getString("p5cw");
            slotParms.mNum1 = rs.getString("mNum1");
            slotParms.mNum2 = rs.getString("mNum2");
            slotParms.mNum3 = rs.getString("mNum3");
            slotParms.mNum4 = rs.getString("mNum4");
            slotParms.mNum5 = rs.getString("mNum5");
            slotParms.in_use = rs.getInt("in_use");
            slotParms.in_use_by = rs.getString("in_use_by");
            slotParms.userg1 = rs.getString("userg1");
            slotParms.userg2 = rs.getString("userg2");
            slotParms.userg3 = rs.getString("userg3");
            slotParms.userg4 = rs.getString("userg4");
            slotParms.userg5 = rs.getString("userg5");
            slotParms.orig_by = rs.getString("orig_by");
            slotParms.pos1 = rs.getShort("pos1");
            slotParms.pos2 = rs.getShort("pos2");
            slotParms.pos3 = rs.getShort("pos3");
            slotParms.pos4 = rs.getShort("pos4");
            slotParms.pos5 = rs.getShort("pos5");
            slotParms.rest5 = rs.getString("rest5");
            slotParms.guest_id1 = rs.getInt("guest_id1");
            slotParms.guest_id2 = rs.getInt("guest_id2");
            slotParms.guest_id3 = rs.getInt("guest_id3");
            slotParms.guest_id4 = rs.getInt("guest_id4");
            slotParms.guest_id5 = rs.getInt("guest_id5");
            proNew = rs.getInt("proNew");
            proMod = rs.getInt("proMod");               
            event = rs.getString("event");
            event_type = rs.getInt("event_type");
            
            slotParms.date = rs.getInt("date");
            slotParms.yy = rs.getInt("yy");
            slotParms.mm = rs.getInt("mm");
            slotParms.dd = rs.getInt("dd");
            slotParms.day = rs.getString("day");
            
            // added this 6-5-08, history not working, history uses slotParm.time 
            // which had been set to the time requested, not the tee time the notification was dragged to
            slotParms.time = rs.getInt("time");
            slotParms.fb = rs.getInt("fb");
            slotParms.course = rs.getString("courseName");
            
            // set the existing players to the 'old' parms as well so that we
            // do not check them when doing restrictions and the like below
            slotParms.oldPlayer1 = rs.getString("player1");
            slotParms.oldPlayer2 = rs.getString("player2");
            slotParms.oldPlayer3 = rs.getString("player3");
            slotParms.oldPlayer4 = rs.getString("player4");
            slotParms.oldPlayer5 = rs.getString("player5");
            slotParms.oldUser1 = rs.getString("username1");
            slotParms.oldUser2 = rs.getString("username2");
            slotParms.oldUser3 = rs.getString("username3");
            slotParms.oldUser4 = rs.getString("username4");
            slotParms.oldUser5 = rs.getString("username5");
            slotParms.oldp1cw = rs.getString("p1cw");
            slotParms.oldp2cw = rs.getString("p2cw");
            slotParms.oldp3cw = rs.getString("p3cw");
            slotParms.oldp4cw = rs.getString("p4cw");
            slotParms.oldguest_id1 = rs.getInt("guest_id1");
            slotParms.oldguest_id2 = rs.getInt("guest_id2");
            slotParms.oldguest_id3 = rs.getInt("guest_id3");
            slotParms.oldguest_id4 = rs.getInt("guest_id4");
            slotParms.oldguest_id5 = rs.getInt("guest_id5");
            
        }
        out.println("<!-- DONE LOADING slotParms WITH teecurr2 DATA -->");
        pstmt.close();

    }
    catch (Exception e) {

        out.println("<p>Error: "+e.toString()+"</p>");
    }

    // make sure there are enough open player slots
    int open_slots = 0;
    boolean has_players = false;
    if (slotParms.player1.equals("")) { open_slots++; } else { has_players = true; }
    if (slotParms.player2.equals("")) open_slots++;
    if (slotParms.player3.equals("")) open_slots++;
    if (slotParms.player4.equals("")) open_slots++;
    if (slotParms.player5.equals("") && slotParms.rest5.equals("") && fives == 1) open_slots++;

    if (slotParms.orig_by.equals( "" )) {    // if originator field still empty (allow this person to grab this tee time again)

        slotParms.orig_by = user;             // set this user as the originator
    }
    
    //out.println("<!-- open_slots="+open_slots+" | has_players="+has_players+" -->");
    
    if (slotParms.in_use == 1 && !slotParms.in_use_by.equalsIgnoreCase( user )) {    // if time slot in use and not by this user

        out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<CENTER><BR><BR><H1>Reservation Timer Expired</H1>");
        out.println("<BR><BR>Sorry, but this tee time slot has been returned to the system!<BR>");
        out.println("<BR>The system timed out and released the tee time.");
        out.println("<BR><BR>");

        if (index == 888 ) {      // if from Proshop_searchmem via proshop_main

            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

        } else {

            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
            if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
            } else {
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
            }
            out.println("</form></font>");
        }
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    boolean wl_guests = false;
    boolean teeTimeFull = false;
    boolean allowFives = (fives == 1) ? true : false;
    int tmp_added = 0;
    
    //
    try {

        // make sure wait list signup is not in-use
        PreparedStatement pstmt = con.prepareStatement(
            "SELECT in_use_by, notes, hideNotes " +
            "FROM wait_list_signups " +
            "WHERE wait_list_signup_id = ? AND " +
                "(in_use_by = '' OR (in_use_by <> '' AND DATE_ADD(in_use_at, INTERVAL 6 MINUTE) < now()))");
        
        pstmt.clearParameters();
        pstmt.setInt(1, wls_id);
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            
            slotParms.last_user = rs.getString( "in_use_by" );
            slotParms.in_use = (slotParms.last_user.equals("")) ? 0 : 1;
            slotParms.notes = rs.getString( "notes" );
            slotParms.hideNotes = rs.getInt("hideNotes");
            
        } else {
            
            out.println(SystemUtils.HeadTitle("Wait List Management"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<CENTER><BR><BR><H2>The wait list signup you selected seems to be busy.</H2>");
            out.println("<BR>Please try again in a few minutes.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\""+index+"\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
            if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
            } else {
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
            }
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            
            out.close();
            return;
            
        }
        pstmt.close();

        String userg = "";
        
        // load the player data from wait list signup
        pstmt = con.prepareStatement (
            "SELECT wlp.*, (SELECT memNum FROM member2b WHERE wlp.username = username) AS mNum " +
            "FROM wait_list_signups_players wlp " +
            "WHERE wait_list_signup_id = ? " +
            "ORDER BY pos");
        
        pstmt.clearParameters();
        pstmt.setInt(1, wls_id);
        rs = pstmt.executeQuery();
        
        // add the first player from this signup
        if (rs.next()) {
            
            teeTimeFull = addPlayer(slotParms, rs.getString( "player_name" ), rs.getString( "username" ), rs.getString( "mNum" ), rs.getString( "cw" ), userg, rs.getInt( "9hole" ), rs.getInt("guest_id"), allowFives);
            if (!teeTimeFull) tmp_added++;
            if (!rs.getString( "username" ).equals("")) {
               wl_guests = true;
               userg = rs.getString( "username" );
            }
        }
        
        if (rs.next() && !teeTimeFull) {
            
            if (rs.getString( "username" ) != null || rs.getString( "player_name" ).equalsIgnoreCase("x")) userg = "";
            teeTimeFull = addPlayer(slotParms, rs.getString( "player_name" ), rs.getString( "username" ), rs.getString( "mNum" ), rs.getString( "cw" ), userg, rs.getInt( "9hole" ), rs.getInt("guest_id"), allowFives);
            if (!teeTimeFull) tmp_added++;
            if (!rs.getString( "username" ).equals("")) {
                userg = rs.getString( "username" );
                wl_guests = true;
            }
        }
        
        if (rs.next() && !teeTimeFull) {
            
            if (rs.getString( "username" ) != null || rs.getString( "player_name" ).equalsIgnoreCase("x")) userg = "";
            teeTimeFull = addPlayer(slotParms, rs.getString( "player_name" ), rs.getString( "username" ), rs.getString( "mNum" ), rs.getString( "cw" ), userg, rs.getInt( "9hole" ), rs.getInt("guest_id"), allowFives);
            if (!teeTimeFull) tmp_added++;
            if (!rs.getString( "username" ).equals("")) {
                userg = rs.getString( "username" );
                wl_guests = true;
            }
        }
        
        if (rs.next() && !teeTimeFull) {
            
            if (rs.getString( "username" ) != null || rs.getString( "player_name" ).equalsIgnoreCase("x")) userg = "";
            teeTimeFull = addPlayer(slotParms, rs.getString( "player_name" ), rs.getString( "username" ), rs.getString( "mNum" ), rs.getString( "cw" ), userg, rs.getInt( "9hole" ), rs.getInt("guest_id"), allowFives);
            if (!teeTimeFull) tmp_added++;
            if (!rs.getString( "username" ).equals("")) {
                userg = rs.getString( "username" );
                wl_guests = true;
            }
        }
        
        if (rs.next() && !teeTimeFull && fives == 1) {
            
            if (rs.getString( "username" ) != null || rs.getString( "player_name" ).equalsIgnoreCase("x")) userg = "";
            teeTimeFull = addPlayer(slotParms, rs.getString( "player_name" ), rs.getString( "username" ), rs.getString( "mNum" ), rs.getString( "cw" ), userg, rs.getInt( "9hole" ), rs.getInt("guest_id"), allowFives);
            if (!teeTimeFull) tmp_added++;
            if (!rs.getString( "username" ).equals("")) wl_guests = true;
        }
        
        pstmt.close();
      
    }
    catch (Exception e) {

        msg = "Loading slotParms with wait list signup data. ";
        out.println("ERROR: " + msg + " -- " + e.toString());
        //dbError(out, e, msg);
        return;
    }  

    out.println("<!-- wls_id="+wls_id+" | teecurr_id="+teecurr_id+" | tmp_added="+tmp_added+" -->");
    
    // notify user if we couldn't fit all players into this tee time
    if (teeTimeFull) {

        out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<CENTER><BR><BR><H1>Too Many Players</H1>");
        out.println("<BR><BR>Sorry, but it appears you have tried to add too many players to this tee time.<BR>");
        out.println("<BR><BR>");
        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
        if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        } else {
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
        }
        out.println("</form></font>");

        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    
    // first lets see if they are trying to fill the 5th player slot when it is restricted
    if ( !slotParms.player5.equals("") && ((!slotParms.rest5.equals("") && overRideFives == false) || fives == 0)) {
    
        out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Member Restricted</H3><BR>");
        out.println("<BR>Sorry, <b>5-somes</b> are restricted during this time.<br><br>");

        if (overrideAccess) {
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");
        }
       
        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"hidden\" name=\"email\" value=\"" + email + "\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");

        if (!overrideAccess) {
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
        } else {
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
            out.println("<input type=\"hidden\" name=\"convert\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"overRideFives\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"wlsId\" value=\"" + wls_id + "\">");
            out.println("<input type=\"hidden\" name=\"to_tid\" value=\"" + teecurr_id + "\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + email + "\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
        }
        
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    out.println("<!-- slotParms.player5="+slotParms.player5+" | slotParms.rest5="+slotParms.rest5+" | overRideFives="+overRideFives+" -->");
    out.println("<!-- open_slots="+open_slots+" | slotParms.players="+slotParms.players+" -->");
    
    
    // the slotParm is now populated with any existing players as well as the all the wait list players
    
    
    out.println("<!-- skip="+skip+" | wl_guests="+wl_guests+" -->");
    

    //
    //  Check if any player names are guest names (set userg1-5 if necessary)
    //
    try {

        verifySlot.parseGuests(slotParms, con);

    } catch (Exception ignore) { }
    
        out.println("<!-- slotParms.guests="+slotParms.guests+" -->");
        out.println("<!-- slotParms.userg1="+slotParms.userg1+" -->");
        out.println("<!-- slotParms.userg2="+slotParms.userg2+" -->");
        out.println("<!-- slotParms.userg3="+slotParms.userg3+" -->");
        out.println("<!-- slotParms.userg4="+slotParms.userg4+" -->");
        out.println("<!-- slotParms.userg5="+slotParms.userg5+" -->");
        out.println("<!-- slotParms.g1="+slotParms.g1+" -->");
        out.println("<!-- slotParms.g2="+slotParms.g2+" -->");
        out.println("<!-- slotParms.g3="+slotParms.g3+" -->");
        out.println("<!-- slotParms.g4="+slotParms.g4+" -->");
        out.println("<!-- slotParms.g5="+slotParms.g5+" -->");
        
    //
    //  Parse the names to separate first, last & mi
    //
    try {

        error = verifySlot.parseNames(slotParms, "pro");

    } catch (Exception ignore) { }

        out.println("<!-- slotParms.fname1="+slotParms.fname1+" -->");
        out.println("<!-- slotParms.mi1="+slotParms.mi1+" -->");
        out.println("<!-- slotParms.lname1="+slotParms.lname1+" -->");
        out.println("<!-- slotParms.fname2="+slotParms.fname2+" -->");
        out.println("<!-- slotParms.mi2="+slotParms.mi2+" -->");
        out.println("<!-- slotParms.lname2="+slotParms.lname2+" -->");
        out.println("<!-- slotParms.fname3="+slotParms.fname3+" -->");
        out.println("<!-- slotParms.mi3="+slotParms.mi3+" -->");
        out.println("<!-- slotParms.lname3="+slotParms.lname3+" -->");
        out.println("<!-- slotParms.fname4="+slotParms.fname4+" -->");
        out.println("<!-- slotParms.mi4="+slotParms.mi4+" -->");
        out.println("<!-- slotParms.lname4="+slotParms.lname4+" -->");
        out.println("<!-- slotParms.fname5="+slotParms.fname5+" -->");
        out.println("<!-- slotParms.mi5="+slotParms.mi5+" -->");
        out.println("<!-- slotParms.lname5="+slotParms.lname5+" -->");
            
            
    
    //
    //  Get the usernames, membership types and hndcp's for players if matching name found
    //
    try {

        verifySlot.getUsers(slotParms, con);
        
    } catch (Exception ignore) { }
            
        out.println("<!-- slotParms.user1="+slotParms.user1+" -->");
        out.println("<!-- slotParms.user2="+slotParms.user2+" -->");
        out.println("<!-- slotParms.user3="+slotParms.user3+" -->");
        out.println("<!-- slotParms.user4="+slotParms.user4+" -->");
        out.println("<!-- slotParms.user5="+slotParms.user5+" -->");
        out.println("<!-- slotParms.mship1="+slotParms.mship1+" -->");
        out.println("<!-- slotParms.mship2="+slotParms.mship2+" -->");
        out.println("<!-- slotParms.mship3="+slotParms.mship3+" -->");
        out.println("<!-- slotParms.mship4="+slotParms.mship4+" -->");
        out.println("<!-- slotParms.mship5="+slotParms.mship5+" -->");
        out.println("<!-- slotParms.mtype1="+slotParms.mtype1+" -->");
        out.println("<!-- slotParms.mtype2="+slotParms.mtype2+" -->");
        out.println("<!-- slotParms.mtype3="+slotParms.mtype3+" -->");
        out.println("<!-- slotParms.mtype4="+slotParms.mtype4+" -->");
        out.println("<!-- slotParms.mtype5="+slotParms.mtype5+" -->");
            
            
        out.println("<!-- slotParms.date="+slotParms.date+" -->");
        out.println("<!-- slotParms.time="+slotParms.time+" -->");
        out.println("<!-- slotParms.day="+slotParms.day+" -->");
        out.println("<!-- slotParms.course="+slotParms.course+" -->");
        
        
    //
    //  check if we are to skip this test
    //
    if (skip < 2) {
            
         error = false;
            
         try {
 
            error = verifySlot.checkMemRests(slotParms, con);      // check restrictions  
    
         } catch (Exception ignore) { }
    
         if (error == true) {          // if we hit on a restriction
           
            //
            //  Prompt user to see if he wants to override this violation 
            //
            out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is restricted from playing during this time.<br><br>");
            out.println("This time slot has the following restriction:  <b>" + slotParms.rest_name + "</b><br><br>");

            if (overrideAccess) {
                out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
                out.println("<BR><BR>");
            }

            //
            //  Return to _dsheet as directed
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + email + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");

            if (!overrideAccess) {
                out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                out.println("</form></font>");
            } else {
                out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                out.println("</form></font>");

                out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
                out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
                out.println("<input type=\"hidden\" name=\"convert\" value=\"yes\">");
                out.println("<input type=\"hidden\" name=\"overRideFives\" value=\"yes\">");
                out.println("<input type=\"hidden\" name=\"wlsId\" value=\"" + wls_id + "\">");
                out.println("<input type=\"hidden\" name=\"to_tid\" value=\"" + teecurr_id + "\">");
                out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                out.println("<input type=\"hidden\" name=\"email\" value=\"" + email + "\">");
                out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
                out.println("<input type=\"hidden\" name=\"skip\" value=\"2\">");
                out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            }
            
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
         
    } // end skip2


    if (skip < 3) {

            
        //
        //************************************************************************
        //  Check for max # of guests exceeded (per Member or per Tee Time)
        //************************************************************************
        //
        if (wl_guests) {      // if any guests were included

            error = false;                             // init error indicator

            try {

                error = verifySlot.checkMaxGuests(slotParms, con);
                
            }
            catch (Exception ignore) { }

            if (error == true) {      // a member exceed the max allowed guests

                out.println(SystemUtils.HeadTitle("Max Num Guests Exceeded - Reject"));
                out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR><BR><BR>");
                out.println("<hr width=\"40%\">");
                out.println("<BR><H3>Number of Guests Exceeded Limit</H3>");
                out.println("<BR>Sorry, the maximum number of guests allowed for the<BR>");
                out.println("time you are requesting is " +slotParms.grest_num+ " per " +slotParms.grest_per+ ".");
                out.println("<BR>You have requested " +slotParms.guests+ " guests and " +slotParms.members+ " members.");
                out.println("<BR><BR>Restriction Name = " +slotParms.rest_name);

                if (overrideAccess) {
                    out.println("<BR><BR>Would you like to override the limit and allow this reservation?");
                    out.println("<BR><BR>");
                }
                  
                //
                //  Return to _dsheet as directed
                //
                out.println("<font size=\"2\">");
                out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
                out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
                out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
                out.println("<input type=\"hidden\" name=\"email\" value=\"" + email + "\">");
                out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");

                if (!overrideAccess) {
                    out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                    out.println("</form></font>");
                } else {
                    out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                    out.println("</form></font>");

                    out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
                    out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
                    out.println("<input type=\"hidden\" name=\"convert\" value=\"yes\">");
                    out.println("<input type=\"hidden\" name=\"overRideFives\" value=\"yes\">");
                    out.println("<input type=\"hidden\" name=\"wlsId\" value=\"" + wls_id + "\">");
                    out.println("<input type=\"hidden\" name=\"to_tid\" value=\"" + teecurr_id + "\">");
                    out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                    out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                    out.println("<input type=\"hidden\" name=\"email\" value=\"" + email + "\">");
                    out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
                    out.println("<input type=\"hidden\" name=\"skip\" value=\"3\">");
                    out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
                }
                
                out.println("</CENTER></BODY></HTML>");
                out.close();
                return;
            }
         }
        
    }
    
    
    if (skip < 4) {

           
         //
         //*********************************************************************
         //  Check any membership types for max rounds per week, month or year
         //*********************************************************************
         //
         if ((!slotParms.mship1.equals( "" )) ||
             (!slotParms.mship2.equals( "" )) ||
             (!slotParms.mship3.equals( "" )) ||
             (!slotParms.mship4.equals( "" )) ||
             (!slotParms.mship5.equals( "" ))) {   // if at least one name exists then check number of rounds

            error = false;                             // init error indicator

            try { 
              
               error = verifySlot.checkMaxRounds(slotParms, con);
                
            }
            catch (Exception ignore) { }

            if (error == true) {      // a member exceed the max allowed guests

                out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
                out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                out.println("<hr width=\"40%\">");
                out.println("<BR><H3>Member Exceeded Limit</H3><BR>");
                out.println("<BR><BR>Warning:  " + slotParms.player + " is a " + slotParms.mship + " member and has exceeded the<BR>");
                out.println("maximum number of tee times allowed for this " + slotParms.period + ".");

                if (overrideAccess) {
                    out.println("<BR><BR>Would you like to override the limit and allow this reservation?");
                    out.println("<BR><BR>");
                }
                  
                //
                //  Return to _dsheet as directed
                //
                out.println("<font size=\"2\">");
                out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
                out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
                out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
                out.println("<input type=\"hidden\" name=\"email\" value=\"" + email + "\">");
                out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");

                if (!overrideAccess) {
                    out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                    out.println("</form></font>");
                } else {
                    out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                    out.println("</form></font>");

                    out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
                    out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
                    out.println("<input type=\"hidden\" name=\"convert\" value=\"yes\">");
                    out.println("<input type=\"hidden\" name=\"overRideFives\" value=\"yes\">");
                    out.println("<input type=\"hidden\" name=\"wlsId\" value=\"" + wls_id + "\">");
                    out.println("<input type=\"hidden\" name=\"to_tid\" value=\"" + teecurr_id + "\">");
                    out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                    out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                    out.println("<input type=\"hidden\" name=\"email\" value=\"" + email + "\">");
                    out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
                    out.println("<input type=\"hidden\" name=\"skip\" value=\"4\">");
                    out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
                }

                out.println("</CENTER></BODY></HTML>");
                out.close();
                return;
            }
         }
        
    }
    

    if (skip < 5) {

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

        } catch (Exception ignore) { }

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

            if (overrideAccess) {
                out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
                out.println("<BR><BR>");
            }

            //
            //  Return to _dsheet as directed
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + email + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");

            if (!overrideAccess) {
                out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                out.println("</form></font>");
            } else {
                out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                out.println("</form></font>");

                out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
                out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
                out.println("<input type=\"hidden\" name=\"convert\" value=\"yes\">");
                out.println("<input type=\"hidden\" name=\"overRideFives\" value=\"yes\">");
                out.println("<input type=\"hidden\" name=\"wlsId\" value=\"" + wls_id + "\">");
                out.println("<input type=\"hidden\" name=\"to_tid\" value=\"" + teecurr_id + "\">");
                out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                out.println("<input type=\"hidden\" name=\"email\" value=\"" + email + "\">");
                out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
                out.println("<input type=\"hidden\" name=\"skip\" value=\"5\">");
                out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            }
            
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
        }
    }
    

    if (skip < 6) {


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
            catch (Exception ignore) { }

            if (error == true) {          // if we hit on a violation

                out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                out.println("<hr width=\"40%\">");
                out.println("<BR><BR><H3>Guest Quota Exceeded for Member</H3><BR>");
                out.println("<BR>Sorry, requesting <b>" + slotParms.player + "</b> exceeds the guest quota established for this guest type.");
                out.println("<br><br>You will have to remove the guest in order to complete this request.");
                out.println("<BR><BR>");

                if (overrideAccess) {
                    out.println("<BR>Would you like to override this restriction and allow the tee time request?");
                }
               
                //
                //  Return to _dsheet as directed
                //
                out.println("<font size=\"2\">");
                out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
                out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
                out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
                out.println("<input type=\"hidden\" name=\"email\" value=\"" + email + "\">");
                out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");

                if (!overrideAccess) {
                    out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                    out.println("</form></font>");
                } else {
                    out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                    out.println("</form></font>");

                    out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
                    out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
                    out.println("<input type=\"hidden\" name=\"convert\" value=\"yes\">");
                    out.println("<input type=\"hidden\" name=\"overRideFives\" value=\"yes\">");
                    out.println("<input type=\"hidden\" name=\"wlsId\" value=\"" + wls_id + "\">");
                    out.println("<input type=\"hidden\" name=\"to_tid\" value=\"" + teecurr_id + "\">");
                    out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                    out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                    out.println("<input type=\"hidden\" name=\"email\" value=\"" + email + "\">");
                    out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
                    out.println("<input type=\"hidden\" name=\"skip\" value=\"6\">");
                    out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
                }
                
                out.println("</CENTER></BODY></HTML>");
                out.close();
                return;
            }
        }
    }
        

    if (skip < 99) {

        out.println(SystemUtils.HeadTitle("Wait List - Players Approved"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR><BR><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><H3>Players Approved</H3>");
        out.println("<BR>The players have been approved at the time your dragged them to.<BR>");
        out.println("<BR><BR>This action can not be undone, so it's recommended you contact the members<BR>");
        out.println("before continuing and make sure they are available to play at this time.");
        
        out.println("<BR><BR>Note: Players with an * next to their name are already part of this tee time.<BR><BR>");
        
        out.println("<table align=center style=\"border: 1px black solid\" border=0 cellpadding=5 bgcolor='#EEEEEE'>");
        
        out.println("<tr><td>Time:</td><td>" + SystemUtils.getSimpleTime(slotParms.time) + "</td></tr>");
        out.println("<tr><td>Tees:</td><td>" + ((slotParms.fb == 0) ? "Front" : "Back") + "</td></tr>");
        out.println("<tr><td>Course:</td><td>" + slotParms.course + "</td></tr>");
        out.println("<tr><td>Player 1:&nbsp;</td><td>" + ((!slotParms.player1.equals("") && slotParms.player1.equals(slotParms.oldPlayer1)) ? "* " : "") + slotParms.player1 + " &nbsp; " + getMemberPhone(slotParms.user1, con) + "</td></tr>");
        out.println("<tr><td>Player 2:</td><td>" + ((!slotParms.player2.equals("") && slotParms.player2.equals(slotParms.oldPlayer2)) ? "* " : "") + slotParms.player2 + " &nbsp; " + getMemberPhone(slotParms.user2, con) + "</td></tr>");
        out.println("<tr><td>Player 3:</td><td>" + ((!slotParms.player3.equals("") && slotParms.player3.equals(slotParms.oldPlayer3)) ? "* " : "") + slotParms.player3 + " &nbsp; " + getMemberPhone(slotParms.user3, con) + "</td></tr>");
        out.println("<tr><td>Player 4:</td><td>" + ((!slotParms.player4.equals("") && slotParms.player4.equals(slotParms.oldPlayer4)) ? "* " : "") + slotParms.player4 + " &nbsp; " + getMemberPhone(slotParms.user4, con) + "</td></tr>");
        out.println("<tr><td>Player 5:</td><td>" + ((!slotParms.player5.equals("") && slotParms.player5.equals(slotParms.oldPlayer5)) ? "* " : "") + slotParms.player5 + " &nbsp; " + getMemberPhone(slotParms.user5, con) + "</td></tr>");
        if (!slotParms.notes.equals("")) out.println("<tr><td>Notes:&nbsp;</td><td>" +slotParms.notes+ "</td>");
        
        out.println("</table>");
        
        out.println("<BR><BR>Would you like to finalize this tee time?");
        out.println("<BR><BR>");
        
        //
        //  Return to _dsheet as directed
        //
        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"hidden\" name=\"email\" value=\"" + email + "\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
        out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
        
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\" name=frmConfirm>");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
        out.println("<input type=\"hidden\" name=\"convert\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"overRideFives\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"wlsId\" value=\"" + wls_id + "\">");
        out.println("<input type=\"hidden\" name=\"to_tid\" value=\"" + teecurr_id + "\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"hidden\" name=\"email\" value=\"" + email + "\">");
        out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
        out.println("<input type=\"hidden\" name=\"skip\" value=\"99\">");
        out.println("<input type=\"hidden\" name=\"mailnow\" value=\"\">");
        out.println("<input type=\"submit\" name=\"submit\" value=\"YES - Continue\" onclick=\"sendNow()\">");
        out.println("</form>");
        
        out.println("<script type=\"text/javascript\">");
        out.println("function sendNow() {");
        out.println(" var f = document.forms['frmConfirm'];");
        out.println(" if (confirm('Send confirmation email to members of this tee time?')) {");
        out.println("  f.mailnow.value='yes';");
        out.println(" } else {");
        out.println("  f.mailnow.value='no';");
        out.println(" }");
        out.println("}");
        out.println("</script>");
        
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    
    //
    //  Before we update the tee time, go check for any flags to be added to members' names for the pro tee sheet
    //
    verifySlot.checkTFlag(slotParms, con);


    //
    // Update entry in teecurr2
    //
    try {

        PreparedStatement pstmt6 = con.prepareStatement (
             "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
             "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
             "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
             "hndcp4 = ?, player5 = ?, username5 = ?, " + 
             "p5cw = ?, hndcp5 = ?, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " +
             "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
             "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, conf = ?, " +
             "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, pos1 = ?, pos2 = ?, pos3 = ?, pos4 = ?, pos5 = ?, " +
             "tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ? " +
             "WHERE teecurr_id = ?");

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
        pstmt6.setString(17, slotParms.player5);
        pstmt6.setString(18, slotParms.user5);
        pstmt6.setString(19, slotParms.p5cw);
        pstmt6.setFloat(20, slotParms.hndcp5);
        pstmt6.setString(21, slotParms.notes);
        pstmt6.setInt(22, slotParms.hideNotes);
        pstmt6.setInt(23, proNew);
        pstmt6.setInt(24, proMod);
        pstmt6.setString(25, slotParms.mNum1);
        pstmt6.setString(26, slotParms.mNum2);
        pstmt6.setString(27, slotParms.mNum3);
        pstmt6.setString(28, slotParms.mNum4);
        pstmt6.setString(29, slotParms.mNum5);
        pstmt6.setString(30, slotParms.userg1);
        pstmt6.setString(31, slotParms.userg2);
        pstmt6.setString(32, slotParms.userg3);
        pstmt6.setString(33, slotParms.userg4);
        pstmt6.setString(34, slotParms.userg5);
        pstmt6.setString(35, slotParms.orig_by);
        pstmt6.setString(36, slotParms.conf);
        pstmt6.setInt(37, slotParms.p91);
        pstmt6.setInt(38, slotParms.p92);
        pstmt6.setInt(39, slotParms.p93);
        pstmt6.setInt(40, slotParms.p94);
        pstmt6.setInt(41, slotParms.p95);
        pstmt6.setInt(42, slotParms.pos1);
        pstmt6.setInt(43, slotParms.pos2);
        pstmt6.setInt(44, slotParms.pos3);
        pstmt6.setInt(45, slotParms.pos4);
        pstmt6.setInt(46, slotParms.pos5);
        pstmt6.setString(47, slotParms.tflag1);
        pstmt6.setString(48, slotParms.tflag2);
        pstmt6.setString(49, slotParms.tflag3);
        pstmt6.setString(50, slotParms.tflag4);
        pstmt6.setString(51, slotParms.tflag5);

        pstmt6.setInt(52, teecurr_id);

        count = pstmt6.executeUpdate();      // execute the prepared stmt

        //
        // Update the converted flag, in_use indicators and timestamp in the notifications table
        //      
        pstmt6 = con.prepareStatement ("" +
                "UPDATE wait_list_signups " +
                "SET " +
                    "in_use_by = '', in_use_at = '0000-00-00 00:00:00', " +
                    "converted_at = now(), converted = 1, " +
                    "teecurr_id = ?, converted_by = ? " +
                "WHERE wait_list_signup_id = ?");
        pstmt6.clearParameters();
        pstmt6.setInt(1, teecurr_id);
        pstmt6.setString(2, user);
        pstmt6.setInt(3, wls_id);
        pstmt6.executeUpdate();
        
        pstmt6.close();

    }
    catch (Exception e1) {

        out.println(SystemUtils.HeadTitle("DB Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
        out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<center>");
        out.println("<BR><BR><H2>Database Access Error</H2>");
        out.println("<BR><BR>Unable to properly convert wait list signup to tee time.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>" + e1.getMessage());
        out.println("<BR><BR>");
        out.close();
        return;
    }

    //
    //  Track the history of this tee time - make entry in 'teehist' table (check if new or update)
    //
    String fullName = "Wait List -> Tee Sheet";

    if (slotParms.oldPlayer1.equals( "" ) && slotParms.oldPlayer2.equals( "" ) && slotParms.oldPlayer3.equals( "" ) &&
        slotParms.oldPlayer4.equals( "" ) && slotParms.oldPlayer5.equals( "" )) {

        //  new tee time
        SystemUtils.updateHist(slotParms.date, slotParms.day, slotParms.time, slotParms.fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
                             slotParms.player4, slotParms.player5, user, fullName, 0, con);

    } else {

        //  update tee time
        SystemUtils.updateHist(slotParms.date, slotParms.day, slotParms.time, slotParms.fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
                             slotParms.player4, slotParms.player5, user, fullName, 1, con);
    }
   

    String mailnow = (req.getParameter("mailnow") != null) ? req.getParameter("mailnow") : "";
        out.println("<!-- mailnow=" + mailnow + " -->");
    
    if (mailnow.equals("yes")) {
        
        // send email right now to all members of this tee time
        sendWaitListEmail(teecurr_id, out, con);        
    }
    
    
    try {

        resp.flushBuffer();      // force the repsonse to complete
    } catch (Exception ignore) { }

    out.println("<meta http-equiv=\"Refresh\" content=\"5; url=/" +rev+ "/servlet/Proshop_dsheet?mode=WAITLIST&name=" + name + "&index=" + slotParms.ind + "&course=" + slotParms.returnCourse + "&jump=" + slotParms.jump + "&hide=" + hide + "\">");

    out.close();
    return;
 
 }
         
         
 //
 // Convert an old notification and add to teepast2 table
 //
 private void convertOld(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {

    PreparedStatement pstmt2 = null;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    //
    //  Get this session's attributes
    //
    String user = "";
    String club = "";
    user = (String)session.getAttribute("user");
    club = (String)session.getAttribute("club");

    int notify_id = 0;
    int teepast_id = 0;
    String snid = "";
    String stid = "";
    if (req.getParameter("to_tid") != null) stid = req.getParameter("to_tid");
    if (req.getParameter("notifyId") != null) snid = req.getParameter("notifyId");

    boolean overrideAccess = SystemUtils.verifyProAccess(req, "REST_OVERRIDE", con, out);
    
    //
    //  Convert the values from string to int
    //
    try {
        
        notify_id = Integer.parseInt(snid);
        teepast_id = Integer.parseInt(stid);
    }
    catch (NumberFormatException e) {
    }
    
    
    int reject = 0;
    int count = 0;
    int time2 = 0;
    int fb2 = 0;
    int t_fb = 0;
    int x = 0;
    int xhrs = 0;
    int xError = 0;
    int xUsed = 0;
    int hide = 0;
    int i = 0;
    int mm = 0;
    int yy = 0;
    int dd = 0;
    int day = 0;
    int fb = 0;
    int time = 0;
    int hr = 0;
    int min = 0;
    int mtimes = 0;
    int year = 0;
    int month = 0;
    int ind = 0;
    int index = 0;
    int temp = 0;
    int sendemail = 0;
    int emailNew = 0;
    int emailMod = 0;
    int emailCan = 0;
    int gi = 0;
    int proNew = 0;
    int proMod = 0;
    int skip = 0;
    int fives = 0;
    int grev1 = 0;
    int grev2 = 0;
    int grev3 = 0;
    int grev4 = 0;
    int grev5 = 0;
    int guest_id1 = 0;
    int guest_id2 = 0;
    int guest_id3 = 0;
    int guest_id4 = 0;
    int guest_id5 = 0;

    int date = 0;
    long dateStart = 0;
    long dateEnd = 0;

    String player = "";
    String mperiod = "";
    String course2 = "";
    String memberName = "";
    String mship = "";
    String mtype = "";
    String skips = "";
    String p9s = "";
    String event = "";
    String suppressEmails = "no";
    String sponsored = "Spons";
    String msg = "";
    String mship1 = "";
    String mship2 = "";
    String mship3 = "";
    String mship4 = "";
    String mship5 = "";
    String mtype1 = "";
    String mtype2 = "";
    String mtype3 = "";
    String mtype4 = "";
    String mtype5 = "";
    String gtype1 = "";
    String gtype2 = "";
    String gtype3 = "";
    String gtype4 = "";
    String gtype5 = "";

    boolean hit = false;
    boolean hit2 = false;
    boolean check = false;
    boolean guestError = false;
    boolean error = false;
    boolean posSent = false;
    boolean overRideFives = false;

    int [] mtimesA = new int [8];          // array to hold the mship max # of rounds value
    String [] periodA = new String [8];    // array to hold the mship periods (week, month, year)

    //
    //  Arrays to hold member & guest names to tie guests to members
    //
    String [] memA = new String [5];     // members
    String [] usergA = new String [5];   // guests' associated member (username)

    //
    //  Arrays to hold guest types and revenue generating guest values
    //
    String [] gtypes = new String[35];
    int [] grevs = new int[35];

    //
    //  parm block to hold the club parameters
    //
    parmClub parm = new parmClub(0, con);

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
/*    
    String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
    String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
    String smm = req.getParameter("mm");               //  month of tee time
    String syy = req.getParameter("yy");               //  year of tee time
*/
    String sindex = req.getParameter("index");          //  day index value (needed by _sheet on return)
    String returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)

    if (sindex != null) {

        index = Integer.parseInt(sindex);
    }
    
    if (req.getParameter("suppressEmails") != null) {             // if email parm exists
        suppressEmails = req.getParameter("suppressEmails");
    }
    
    if (req.getParameter("overRideFives") != null && req.getParameter("overRideFives").equals("yes")) {             // if email parm exists
        overRideFives = true;
    }

    //
    //  Get skip parm if provided
    //
    if (req.getParameter("skip") != null) {

        skips = req.getParameter("skip");
        skip = Integer.parseInt(skips);
    }


    String sdate = "";           //  date of tee time requested (yyyymmdd)
    String stime = "";           //  time of tee time requested (hhmm)
    String sfb = "";               //  month of tee time
    String scourse = "";    
    
    //
    // If teepast is zero then we are here to use an entry from teepastempty
    //
    if (teepast_id == 0) {
    
        // don't have date, use index
        
        //
        //  Get today's date and then use the value passed to locate the requested date
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        cal.add(Calendar.DATE, index);                  // roll ahead 'index' days
        int cal_hour = cal.get(Calendar.HOUR_OF_DAY);     // 24 hr clock (0 - 23)
        int cal_min = cal.get(Calendar.MINUTE);
        int cal_time = (cal_hour * 100) + cal_min;     // get time in hhmm format
        cal_time = SystemUtils.adjustTime(con, cal_time);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);

        date = year * 10000;                     // create a date field of yyyymmdd
        date = date + (month * 100);
        date = date + day;                            // date = yyyymmdd (for comparisons)

        String date_mysql = year + "-" + SystemUtils.ensureDoubleDigit(month) + "-" + SystemUtils.ensureDoubleDigit(day);
        //String time_mysql = (cal_time / 100) + ":" + SystemUtils.ensureDoubleDigit(cal_time % 100) + ":00"; // current time for 

        out.println("<!-- date_mysql=" + date_mysql + " -->");
   
        //sdate = req.getParameter("to_date");           //  date of tee time requested (yyyymmdd)
        stime = req.getParameter("to_time");            //  time of tee time requested (hhmm)
        sfb = (req.getParameter("to_fb") != null) ? req.getParameter("to_fb") : "";
        if (sfb.equalsIgnoreCase("F")) fb = 0; else fb = 1;
        scourse = req.getParameter("to_course");   
        String day_name = "";
    
        try {
            
            out.println("<!-- PARSING INTEGERS: sdate=" + sdate + ", stime=" + stime + ", sfb=" + sfb + " -->");
        
            time = Integer.parseInt(stime);
            
            boolean blnFound = false;
            
            out.println("<!-- SELECTING FROM teepastempty: date=" + date + ", time=" + time + ", fb=" + fb + ", scourse='" + scourse + "' -->");
        
            PreparedStatement pstmt = con.prepareStatement (
                "SELECT *, DATE_FORMAT(?, '%W') AS day_name " + 
                "FROM teepastempty " + 
                "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

            pstmt.clearParameters();
            pstmt.setString(1, date_mysql);
            pstmt.setInt(2, date);
            pstmt.setInt(3, time);
            pstmt.setInt(4, fb);
            pstmt.setString(5, scourse);
            rs = pstmt.executeQuery();

            if (rs.next()) {

                blnFound = true;
                day_name = rs.getString("day_name");
                hr = rs.getInt("hr");
                min = rs.getInt("min");
                mm = rs.getInt("mm");
                yy = rs.getInt("yy");
                dd = rs.getInt("dd");
                
            }
            
            pstmt.close();
            
            out.println("<!-- blnFound=" + blnFound + " -->");
            out.println("<!-- ATTEMPTING INSERT VALUES: date=" + date + ", mm=" + mm + ", dd=" + dd + ", yy=" + yy + ", day_name=" + day_name + ", hr=" + hr + ", min=" + min + ", time=" + time + ", fb=" + fb + ", scourse=" + scourse + " -->");
           
            pstmt = con.prepareStatement (
                "INSERT INTO teepast2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
                "restriction, rest_color, player1, player2, player3, player4, username1, " +
                "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, " +
                "show1, show2, show3, show4, fb, " +
                "player5, username5, p5cw, show5, courseName, " +
                "proNew, proMod, memNew, memMod, " +
                "mNum1, mNum2, mNum3, mNum4, mNum5, userg1, userg2, " +
                "userg3, userg4, userg5, hotelNew, hotelMod, orig_by, conf, notes, p91, p92, p93, p94, p95, " +
                "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, '', '', '', '', '', '', " +
                "'', '', '', '', '', '', '', '', '', '', 0, 0, 0, 0, ?, '', '', '', 0, ?, " +
                "0, 0, 0, 0, '', '', '', '', '', '', '', '', '', '', " +
                "0, 0, '', '', '', 0, 0, 0, 0, 0, " +
                "0, 0, 0, 0, 0)");

            //
            //   Add this time slot to teepast
            //
            pstmt.clearParameters();
            pstmt.setLong(1, date);
            pstmt.setInt(2, mm);
            pstmt.setInt(3, dd);
            pstmt.setInt(4, yy);
            pstmt.setString(5, day_name);
            pstmt.setInt(6, hr);
            pstmt.setInt(7, min);
            pstmt.setInt(8, time);
            pstmt.setInt(9, fb);
            pstmt.setString(10, scourse);
            pstmt.executeUpdate(); 

            pstmt.close();
             
        } catch (Exception e) {
            out.println("<!-- DB ERROR:(INSERT) " + e.toString() + " -->");
        }
        
        try {
            
            PreparedStatement pstmt = con.prepareStatement("SELECT LAST_INSERT_ID()");
            ResultSet rsLastID = pstmt.executeQuery();
            while (rsLastID.next()) {
                teepast_id = rsLastID.getInt(1);
            }

            pstmt.close();
            
        } catch (Exception e) {
            out.println("<!-- DB ERROR:(LAST) " + e.toString() + " -->");
        }
    
        out.println("<!-- ADDED ENTRY TO teepast2 w/ UID#" + teepast_id + " for " + date + " at " + time + " on " + scourse + " " + fb + " -->");
    
    }
    
    
    try {

        PreparedStatement pstmtc = con.prepareStatement (
            "SELECT fives " + 
            "FROM clubparm2 c, teepast2 t " + 
            "WHERE c.courseName = t.courseName AND t.teepast_id = ?");

        pstmtc.clearParameters();
        pstmtc.setInt(1, teepast_id);
        rs = pstmtc.executeQuery();

        if (rs.next()) {

            fives = rs.getInt("fives");
        }
        pstmtc.close();
    }
    catch (Exception e) {
        out.println("<!-- DB ERROR:(SELECT 5) " + e.toString() + " -->");
    }
    
      
    //
    //  put parms in Parameter Object for portability
    //
/*
    slotParms.date = date;
    slotParms.time = time;
    slotParms.mm = mm;
    slotParms.yy = yy;
    slotParms.dd = dd;
    slotParms.fb = fb;
*/
    slotParms.ind = index;                      // index value
    slotParms.club = club;                    // name of club
    slotParms.returnCourse = returnCourse;    // name of course for return to _sheet
    slotParms.suppressEmails = suppressEmails;
    
    
    //
    //   Get the tee time data for this teepast time
    //
    try {

        PreparedStatement pstmt = con.prepareStatement("SELECT * FROM teepast2 WHERE teepast_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, teepast_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            slotParms.player1 = rs.getString("player1");
            slotParms.player2 = rs.getString("player2");
            slotParms.player3 = rs.getString("player3");
            slotParms.player4 = rs.getString("player4");
            slotParms.player5 = rs.getString("player5");
            slotParms.user1 = rs.getString("username1");
            slotParms.user2 = rs.getString("username2");
            slotParms.user3 = rs.getString("username3");
            slotParms.user4 = rs.getString("username4");
            slotParms.user5 = rs.getString("username5");
            slotParms.p1cw = rs.getString("p1cw");
            slotParms.p2cw = rs.getString("p2cw");
            slotParms.p3cw = rs.getString("p3cw");
            slotParms.p4cw = rs.getString("p4cw");
            slotParms.p5cw = rs.getString("p5cw");
            //slotParms.in_use = rs.getInt("in_use");
            //slotParms.in_use_by = rs.getString("in_use_by");
            slotParms.userg1 = rs.getString("userg1");
            slotParms.userg2 = rs.getString("userg2");
            slotParms.userg3 = rs.getString("userg3");
            slotParms.userg4 = rs.getString("userg4");
            slotParms.userg5 = rs.getString("userg5");
            //slotParms.orig_by = rs.getString("orig_by");
            //slotParms.pos1 = rs.getShort("pos1");
            //slotParms.pos2 = rs.getShort("pos2");
            //slotParms.pos3 = rs.getShort("pos3");
            //slotParms.pos4 = rs.getShort("pos4");
            //slotParms.pos5 = rs.getShort("pos5");
            //slotParms.rest5 = rs.getString("rest5");
            proNew = rs.getInt("proNew");
            proMod = rs.getInt("proMod");               
            event = rs.getString("event");
            mship1 = rs.getString("mship1");
            mship2 = rs.getString("mship2");
            mship3 = rs.getString("mship3");
            mship4 = rs.getString("mship4");
            mship5 = rs.getString("mship5");
            mtype1 = rs.getString("mtype1");
            mtype2 = rs.getString("mtype2");
            mtype3 = rs.getString("mtype3");
            mtype4 = rs.getString("mtype4");
            mtype5 = rs.getString("mtype5");
            gtype1 = rs.getString("gtype1");
            gtype2 = rs.getString("gtype2");
            gtype3 = rs.getString("gtype3");
            gtype4 = rs.getString("gtype4");
            gtype5 = rs.getString("gtype5");
            grev1 = rs.getInt("grev1");
            grev2 = rs.getInt("grev2");
            grev3 = rs.getInt("grev3");
            grev4 = rs.getInt("grev4");
            grev5 = rs.getInt("grev5");
            slotParms.guest_id1 = rs.getInt("guest_id1");
            slotParms.guest_id2 = rs.getInt("guest_id2");
            slotParms.guest_id3 = rs.getInt("guest_id3");
            slotParms.guest_id4 = rs.getInt("guest_id4");
            slotParms.guest_id5 = rs.getInt("guest_id5");

            //event_type = rs.getInt("event_type");
         
        }
        out.println("<!-- DONE LOADING slotParms WITH teepast2 DATA -->");
        pstmt.close();

    }
    catch (Exception e) {

        out.println("<p>Error: "+e.toString()+"</p>");
    }

    // make sure there are enough open player slots
    int open_slots = 0;
    boolean has_players = false;
    if (slotParms.player1.equals("")) { open_slots++; } else { has_players = true; }
    if (slotParms.player2.equals("")) open_slots++;
    if (slotParms.player3.equals("")) open_slots++;
    if (slotParms.player4.equals("")) open_slots++;
    if (slotParms.player5.equals("") && fives == 1) open_slots++;

    out.println("<!-- open_slots="+open_slots+" | has_players="+has_players+" -->");
        
    boolean teeTimeFull = false;
    boolean allowFives = (fives == 1) ? true : false;
    int tmp_added = 0;
    
    //
    try {

        // load general data from notitification
        PreparedStatement pstmt = con.prepareStatement(
            "SELECT *, " +
                "DATE_FORMAT(req_datetime, '%l') AS hh, " + 
                "DATE_FORMAT(req_datetime, '%i') AS min, " + 
                "DATE_FORMAT(req_datetime, '%Y') AS yy, " + 
                "DATE_FORMAT(req_datetime, '%m') AS mm, " + 
                "DATE_FORMAT(req_datetime, '%d') AS dd, " +
                "c.courseName " +
            "FROM notifications, clubparm2 c " +
            "WHERE notification_id = ? AND in_use_by = '' AND course_id = c.clubparm_id");
        
        pstmt.clearParameters();
        pstmt.setInt(1, notify_id);
        rs = pstmt.executeQuery();
        
        if (rs.next()) {

            slotParms.time = (rs.getInt( "hh" ) * 100 + rs.getInt("min"));
            slotParms.date = (rs.getInt( "yy" ) * 10000 + rs.getInt("mm") * 100 + rs.getInt("dd"));
            slotParms.yy = rs.getInt( "yy" );
            slotParms.mm = rs.getInt( "mm" );
            slotParms.dd = rs.getInt( "dd" );
            slotParms.course = rs.getString( "courseName" );
            //slotParms.last_user = rs.getString( "in_use_by" );
            //slotParms.in_use = (slotParms.last_user.equals("")) ? 0 : 1; //rs.getInt( "in_use" );
            slotParms.notes = rs.getString( "notes" );

        } else {
            
            out.println(SystemUtils.HeadTitle("Notification User"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<CENTER><BR><BR><H2>The notification you selected seems to be busy.</H2>");
            out.println("<BR>Please try again in a few minutes.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\""+slotParms.ind+"\">");
            if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
            } else {
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
            }
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            
            out.close();
            return;
            
        }
        pstmt.close();

        String userg = "";
            
        // load the player data from notification
        pstmt = con.prepareStatement (
            "SELECT np.*, (SELECT memNum FROM member2b WHERE np.username = username) AS mNum " +
            "FROM notifications_players np " +
            "WHERE notification_id = ? " +
            "ORDER BY pos");

        pstmt.clearParameters();
        pstmt.setInt(1, notify_id);
        rs = pstmt.executeQuery();

        // add the first player for this notification
        if (rs.next()) {
            
            teeTimeFull = addPlayer(slotParms, rs.getString( "player_name" ), rs.getString( "username" ), rs.getString( "mNum" ), rs.getString( "cw" ), "", rs.getInt( "9hole" ), rs.getInt("guest_id"), allowFives);
            if (!teeTimeFull) tmp_added++;
            userg = rs.getString( "username" );
        }
        
        if (rs.next() && !teeTimeFull) {
            
            if (rs.getString( "username" ) != null || rs.getString( "player_name" ).equalsIgnoreCase("x")) userg = "";
            teeTimeFull = addPlayer(slotParms, rs.getString( "player_name" ), rs.getString( "username" ), rs.getString( "mNum" ), rs.getString( "cw" ), userg, rs.getInt( "9hole" ), rs.getInt("guest_id"), allowFives);
            if (!teeTimeFull) tmp_added++;
            if (!rs.getString( "username" ).equals("")) userg = rs.getString( "username" );
        }
        
        if (rs.next() && !teeTimeFull) {
            
            if (rs.getString( "username" ) != null || rs.getString( "player_name" ).equalsIgnoreCase("x")) userg = "";
            teeTimeFull = addPlayer(slotParms, rs.getString( "player_name" ), rs.getString( "username" ), rs.getString( "mNum" ), rs.getString( "cw" ), userg, rs.getInt( "9hole" ), rs.getInt("guest_id"), allowFives);
            if (!teeTimeFull) tmp_added++;
            if (!rs.getString( "username" ).equals("")) userg = rs.getString( "username" );
        }
        
        if (rs.next() && !teeTimeFull) {
            
            if (rs.getString( "username" ) != null || rs.getString( "player_name" ).equalsIgnoreCase("x")) userg = "";
            teeTimeFull = addPlayer(slotParms, rs.getString( "player_name" ), rs.getString( "username" ), rs.getString( "mNum" ), rs.getString( "cw" ), userg, rs.getInt( "9hole" ), rs.getInt("guest_id"), allowFives);
            if (!teeTimeFull) tmp_added++;
            if (!rs.getString( "username" ).equals("")) userg = rs.getString( "username" );
        }
        
        if (rs.next() && !teeTimeFull && fives == 1) {
            
            if (rs.getString( "username" ) != null || rs.getString( "player_name" ).equalsIgnoreCase("x")) userg = "";
            teeTimeFull = addPlayer(slotParms, rs.getString( "player_name" ), rs.getString( "username" ), rs.getString( "mNum" ), rs.getString( "cw" ), userg, rs.getInt( "9hole" ), rs.getInt("guest_id"), allowFives);
            if (!teeTimeFull) tmp_added++;
        }
        
        pstmt.close();
      
    }
    catch (Exception e) {

        msg = "Loading slotParms with notification data. ";
        out.println("ERROR: " + msg + " -- " + e.toString());
        //dbError(out, e, msg);
        return;
    }  


    //
    //  Gather information regarding guest types, membership/member types, and revenue generating guest types
    //

    int total_guests = 0;
    int j = 0;

    try {


        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT guest, revenue FROM guest5");

        while (rs.next()) {

            gtypes[j] = rs.getString(1);
            grevs[j] = rs.getInt(2);

            j++;
        }

        stmt.close();

    } catch (Exception exc) {
    }

    if (j > 0) total_guests = j - 1;

    // get mship1, mtype1, gtype1, grev1
    if (!slotParms.user1.equals("") && mship1.equals("") && mtype1.equals("")) {

        try {

            pstmt2 = con.prepareStatement (
                    "SELECT m_ship, m_type FROM member2b WHERE username = ?");

            pstmt2.clearParameters();
            pstmt2.setString(1, slotParms.user1);
            rs2 = pstmt2.executeQuery();

            if (rs2.next()) {

                mship1 = rs2.getString(1);
                mtype1 = rs2.getString(2);
            }

            pstmt2.close();

        } catch (Exception ignore) { }

    } else if (!slotParms.player1.equals("") && gtype1.equals("")) {

        // check to see which type of guest this player is
        loop1:
        for (j=0; j <= total_guests; j++) {

            try {
            if (slotParms.player1.substring(0, gtypes[j].length()).equalsIgnoreCase(gtypes[j])) {

                gtype1 = gtypes[j];
                grev1 = grevs[j];
                break;
            }
            } catch (IndexOutOfBoundsException ignore) {}
        }

    }

    // get mship2, mtype2, gtype2, grev2
    if (!slotParms.user2.equals("") && mship2.equals("") && mtype2.equals("")) {

        try {

            pstmt2 = con.prepareStatement (
                    "SELECT m_ship, m_type FROM member2b WHERE username = ?");

            pstmt2.clearParameters();
            pstmt2.setString(1, slotParms.user2);
            rs2 = pstmt2.executeQuery();

            if (rs2.next()) {

                mship2 = rs2.getString(1);
                mtype2 = rs2.getString(2);
            }

            pstmt2.close();

        } catch (Exception ignore) { }

    } else if (!slotParms.player2.equals("") && gtype2.equals("")) {

        // check to see which type of guest this player is
        loop12:
        for (j=0; j <= total_guests; j++) {

            try {
            if (slotParms.player2.substring(0, gtypes[j].length()).equalsIgnoreCase(gtypes[j])) {

                gtype2 = gtypes[j];
                grev2 = grevs[j];
                break;
            }
            } catch (IndexOutOfBoundsException exc) {}
        }

    }

    // get mship3, mtype3, gtype3, grev3
    if (!slotParms.user3.equals("") && mship3.equals("") && mtype3.equals("")) {

        try {

            pstmt2 = con.prepareStatement (
                    "SELECT m_ship, m_type FROM member2b WHERE username = ?");

            pstmt2.clearParameters();
            pstmt2.setString(1, slotParms.user3);
            rs2 = pstmt2.executeQuery();

            if (rs2.next()) {

                mship3 = rs2.getString(1);
                mtype3 = rs2.getString(2);
            }

            pstmt2.close();

        } catch (Exception ignore) { }

    } else if (!slotParms.player3.equals("") && gtype3.equals("")) {

        // check to see which type of guest this player is
        loop3:
        for (j=0; j <= total_guests; j++) {

            try {
            if (slotParms.player3.substring(0, gtypes[j].length()).equalsIgnoreCase(gtypes[j])) {

                gtype3 = gtypes[j];
                grev3 = grevs[j];
                break;
            }
            } catch (IndexOutOfBoundsException ignore) {}
        }

    }

    // get mship4, mtype4, gtype4, grev4
    if (!slotParms.user4.equals("") && mship4.equals("") && mtype4.equals("")) {

        try {

            pstmt2 = con.prepareStatement (
                    "SELECT m_ship, m_type FROM member2b WHERE username = ?");

            pstmt2.clearParameters();
            pstmt2.setString(1, slotParms.user4);
            rs2 = pstmt2.executeQuery();

            if (rs2.next()) {

                mship4 = rs2.getString(1);
                mtype4 = rs2.getString(2);
            }

            pstmt2.close();

        } catch (Exception ignore) { }

    } else if (!slotParms.player4.equals("") && gtype4.equals("")) {

        // check to see which type of guest this player is
        loop4:
        for (j=0; j <= total_guests; j++) {

            try {
            if (slotParms.player4.substring(0, gtypes[j].length()).equalsIgnoreCase(gtypes[j])) {

                gtype4 = gtypes[j];
                grev4 = grevs[j];
                break;
            }
            } catch (IndexOutOfBoundsException ignore) {}
        }

    }

    // get mship5, mtype5, gtype5, grev5
    if (!slotParms.user5.equals("") && mship5.equals("") && mtype5.equals("")) {

        try {

            pstmt2 = con.prepareStatement (
                    "SELECT m_ship, m_type FROM member2b WHERE username = ?");

            pstmt2.clearParameters();
            pstmt2.setString(1, slotParms.user5);
            rs2 = pstmt2.executeQuery();

            if (rs2.next()) {

                mship5 = rs2.getString(1);
                mtype5 = rs2.getString(2);
            }

            pstmt2.close();

        } catch (Exception ignore) { }

    } else if (!slotParms.player5.equals("") && gtype5.equals("")) {

        // check to see which type of guest this player is
        loop5:
        for (j=0; j <= total_guests; j++) {

            try {
            if (slotParms.player5.substring(0, gtypes[j].length()).equalsIgnoreCase(gtypes[j])) {

                gtype5 = gtypes[j];
                grev5 = grevs[j];
                break;
            }
            } catch (IndexOutOfBoundsException ignore) {}
        }

    }


    out.println("<!-- notify_id="+notify_id+" | teepast_id="+teepast_id+" | tmp_added="+tmp_added+" -->");
    
    // notify user if we couldn't fit all players into this tee time
    if (teeTimeFull) {

        out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<CENTER><BR><BR><H1>Too Many Players</H1>");
        out.println("<BR><BR>Sorry, but it appears you have tried to add too many players to this tee time.<BR>");
        out.println("<BR><BR>");
        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        } else {
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
        }
        out.println("</form></font>");

        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    
    // first lets see if they are trying to fill the 5th player slot when it is restricted
    if ( !slotParms.player5.equals("") && fives == 0 ) {
    
        out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Member Restricted</H3><BR>");
        out.println("<BR>Sorry, <b>5-somes</b> are restricted during this time.<br><br>");

        if (overrideAccess) {
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");
        }
       
        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"\">");

        if (!overrideAccess) {
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
        } else {
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"\">");
            out.println("<input type=\"hidden\" name=\"convert\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"overRideFives\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"notifyId\" value=\"" + notify_id + "\">");
            out.println("<input type=\"hidden\" name=\"to_tid\" value=\"" + teepast_id + "\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
        }
        
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    //out.println("<!-- notify_id="+notify_id+" | teepast_id="+teepast_id+" -->");
    //out.println("<!-- slotParms.player5="+slotParms.player5+" | slotParms.rest5="+slotParms.rest5+" | overRideFives="+overRideFives+" -->");
    //out.println("<!-- open_slots="+open_slots+" | slotParms.players="+slotParms.players+" -->");
    
    out.println("<!-- ATTEMPTING UPDATE OF NEW teepast2 ENTRY #"+teepast_id+" -->");
    
    //
    // Update entry in teepast2
    //
    try {

        PreparedStatement pstmt6 = con.prepareStatement (
             "UPDATE teepast2 " +
             "SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                 "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                 "p2cw = ?, p3cw = ?, p4cw = ?, player5 = ?, username5 = ?, " + 
                 "p5cw = ?, notes = ?, proNew = ?, proMod = ?, " +
                 "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                 "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, " +
                 "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, " +
                 "mship1 = ?, mship2 = ?, mship3 = ?, mship4 = ?, mship5 = ?, " +
                 "mtype1 = ?, mtype2 = ?, mtype3 = ?, mtype4 = ?, mtype5 = ?, " +
                 "gtype1 = ?, gtype2 = ?, gtype3 = ?, gtype4 = ?, gtype5 = ?, " +
                 "grev1 = ?, grev2 = ?, grev3 = ?, grev4 = ?, grev5 = ?, " +
                 "show1 = ?, show2 = ?, show3 = ?, show4 = ?, show5 = ?, " +
                 "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ? " +
             "WHERE teepast_id = ?");

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
        pstmt6.setString(13, slotParms.player5);
        pstmt6.setString(14, slotParms.user5);
        pstmt6.setString(15, slotParms.p5cw);
        pstmt6.setString(16, slotParms.notes);
        pstmt6.setInt(17, proNew);
        pstmt6.setInt(18, proMod);
        pstmt6.setString(19, slotParms.mNum1);
        pstmt6.setString(20, slotParms.mNum2);
        pstmt6.setString(21, slotParms.mNum3);
        pstmt6.setString(22, slotParms.mNum4);
        pstmt6.setString(23, slotParms.mNum5);
        pstmt6.setString(24, slotParms.userg1);
        pstmt6.setString(25, slotParms.userg2);
        pstmt6.setString(26, slotParms.userg3);
        pstmt6.setString(27, slotParms.userg4);
        pstmt6.setString(28, slotParms.userg5);
        pstmt6.setInt(29, slotParms.p91);
        pstmt6.setInt(30, slotParms.p92);
        pstmt6.setInt(31, slotParms.p93);
        pstmt6.setInt(32, slotParms.p94);
        pstmt6.setInt(33, slotParms.p95);
        pstmt6.setString(34, mship1);
        pstmt6.setString(35, mship2);
        pstmt6.setString(36, mship3);
        pstmt6.setString(37, mship4);
        pstmt6.setString(38, mship5);
        pstmt6.setString(39, mtype1);
        pstmt6.setString(40, mtype2);
        pstmt6.setString(41, mtype3);
        pstmt6.setString(42, mtype4);
        pstmt6.setString(43, mtype5);
        pstmt6.setString(44, gtype1);
        pstmt6.setString(45, gtype2);
        pstmt6.setString(46, gtype3);
        pstmt6.setString(47, gtype4);
        pstmt6.setString(48, gtype5);
        pstmt6.setInt(49, grev1);
        pstmt6.setInt(50, grev2);
        pstmt6.setInt(51, grev3);
        pstmt6.setInt(52, grev4);
        pstmt6.setInt(53, grev5);
        pstmt6.setInt(54, (slotParms.player1.equals("") || slotParms.player1.equalsIgnoreCase("x")) ? 0 : 1);
        pstmt6.setInt(55, (slotParms.player2.equals("") || slotParms.player2.equalsIgnoreCase("x")) ? 0 : 1);
        pstmt6.setInt(56, (slotParms.player3.equals("") || slotParms.player3.equalsIgnoreCase("x")) ? 0 : 1);
        pstmt6.setInt(57, (slotParms.player4.equals("") || slotParms.player4.equalsIgnoreCase("x")) ? 0 : 1);
        pstmt6.setInt(58, (slotParms.player5.equals("") || slotParms.player5.equalsIgnoreCase("x")) ? 0 : 1);
        pstmt6.setInt(59, slotParms.guest_id1);
        pstmt6.setInt(60, slotParms.guest_id2);
        pstmt6.setInt(61, slotParms.guest_id3);
        pstmt6.setInt(62, slotParms.guest_id4);
        pstmt6.setInt(63, slotParms.guest_id5);
        pstmt6.setInt(64, teepast_id);

        count = pstmt6.executeUpdate();      // execute the prepared stmt
        out.println("<!-- DONE UPDATING teepast2.  count=" + count + " -->");

        //
        // Update the converted flag, in_use indicators and timestamp in the notifications table
        //      
        pstmt6 = con.prepareStatement ("" +
                "UPDATE notifications " +
                "SET " +
                    "in_use_by = '', in_use_at = '0000-00-00 00:00:00', " +
                    "converted_at = now(), converted = 1, " +
                    "teepast_id = ?, converted_by = ? " +
                "WHERE notification_id = ?");
        
        pstmt6.clearParameters();
        pstmt6.setInt(1, teepast_id);
        pstmt6.setString(2, user);
        pstmt6.setInt(3, notify_id);
        count = pstmt6.executeUpdate();
        
        pstmt6.close();

    }
    catch (Exception e1) {

        out.println(SystemUtils.HeadTitle("DB Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
        out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<center>");
        out.println("<BR><BR><H2>Database Access Error</H2>");
        out.println("<BR><BR>Unable to access the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>" + e1.getMessage());
        out.println("<BR><BR>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }

    out.println("<!-- DONE UPDATING CONVERTED NOTIFCATION.  count=" + count + " -->");
    
    
    try {

        PreparedStatement pstmt6 = con.prepareStatement (
             "DELETE " +
             "FROM teepastempty " +
             "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

        pstmt6.clearParameters();
        pstmt6.setInt(1, date);
        pstmt6.setInt(2, time);
        pstmt6.setInt(3, fb);
        pstmt6.setString(4, scourse);
        count = pstmt6.executeUpdate();
        
        pstmt6.close();

    }
    catch (Exception e1) {

        out.println(SystemUtils.HeadTitle("DB Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
        out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<center>");
        out.println("<BR><BR><H2>Database Access Error</H2>");
        out.println("<BR><BR>Error deleting previously empty slot.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>" + e1.getMessage());
        out.println("<BR><BR>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    
   //
   //  Track the history of this tee time - make entry in 'teehist' table (check if new or update)
   //
   String fullName = "Notification -> Old Tee Sheet";
     
   if (slotParms.oldPlayer1.equals( "" ) && slotParms.oldPlayer2.equals( "" ) && slotParms.oldPlayer3.equals( "" ) &&
       slotParms.oldPlayer4.equals( "" ) && slotParms.oldPlayer5.equals( "" )) {

      //  new tee time
      SystemUtils.updateHist(slotParms.date, slotParms.day, slotParms.time, slotParms.fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
                             slotParms.player4, slotParms.player5, user, fullName, 0, con);

   } else {

      //  update tee time
      SystemUtils.updateHist(slotParms.date, slotParms.day, slotParms.time, slotParms.fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
                             slotParms.player4, slotParms.player5, user, fullName, 1, con);
   }
   

    try {

        resp.flushBuffer();      // force the repsonse to complete
    } catch (Exception ignore) {
    }

    out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_dsheet?mode=&index=" + slotParms.ind + "&course=" + slotParms.returnCourse + "&jump=" + slotParms.jump + "\">");

    out.close();
    return;
    
 }
 
 
 private static boolean addPlayer(parmSlot slotParms, String player_name, String username, String mNum, String cw, String userg, int p9hole, int guest_id, boolean allowFives) {

    if (mNum == null) mNum = "";
    if (userg == null) userg = "";
     
    if (slotParms.player1.equals("")) {

        slotParms.player1 = player_name;
        slotParms.user1 = username;
        slotParms.mNum1 = mNum;
        slotParms.p1cw = cw;
        slotParms.p91 = p9hole;
        slotParms.userg1 = userg;
        slotParms.guest_id1 = guest_id;

    } else if (slotParms.player2.equals("")) {

        slotParms.player2 = player_name;
        slotParms.user2 = username;
        slotParms.mNum2 = mNum;
        slotParms.p2cw = cw;
        slotParms.p92 = p9hole;
        slotParms.userg2 = userg;
        slotParms.guest_id2 = guest_id;

    } else if (slotParms.player3.equals("")) {

        slotParms.player3 = player_name;
        slotParms.user3 = username;
        slotParms.mNum3 = mNum;
        slotParms.p3cw = cw;
        slotParms.p93 = p9hole;
        slotParms.userg3 = userg;
        slotParms.guest_id3 = guest_id;

    } else if (slotParms.player4.equals("")) {

        slotParms.player4 = player_name;
        slotParms.user4 = username;
        slotParms.mNum4 = mNum;
        slotParms.p4cw = cw;
        slotParms.p94 = p9hole;
        slotParms.userg4 = userg;
        slotParms.guest_id4 = guest_id;

    } else if (slotParms.player5.equals("") && allowFives) {

        slotParms.player5 = player_name;
        slotParms.user5 = username;
        slotParms.mNum5 = mNum;
        slotParms.p5cw = cw;
        slotParms.p95 = p9hole;
        slotParms.userg5 = userg;
        slotParms.guest_id5 = guest_id;

    } else {
        
        return (true);
        
    }
    
     return (false);
 }
 
 
 //
 // Add player to slot for lottery
 //
 private static boolean addPlayer(parmSlot slotParms, String player_name, String username, String cw, String userg, int p9hole, int guest_id, boolean allowFives, Connection con) {

    //
    //  Get Member# and Handicap for each member
    //
    String mNum = "";
    float hndcp = 0;
    
    if (!username.equals( "" )) {        // if player is a member

      String parm = SystemUtils.getUser(con, username);     // get mNum and hndcp for member

      StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

      mNum = tok.nextToken();         // member Number
      String hndcps = tok.nextToken();       // handicap

      hndcp = Float.parseFloat(hndcps);   // convert back to floating int
    }
            
    if (slotParms.player1.equals("")) {

        slotParms.player1 = player_name;
        slotParms.user1 = username;
        slotParms.p1cw = cw;
        slotParms.p91 = p9hole;
        slotParms.mNum1 = mNum;
        slotParms.hndcp1 = hndcp;
        slotParms.userg1 = userg;
        slotParms.guest_id1 = guest_id;

    } else if (slotParms.player2.equals("")) {

        slotParms.player2 = player_name;
        slotParms.user2 = username;
        slotParms.p2cw = cw;
        slotParms.p92 = p9hole;
        slotParms.mNum2 = mNum;
        slotParms.hndcp2 = hndcp;
        slotParms.userg2 = userg;
        slotParms.guest_id2 = guest_id;

    } else if (slotParms.player3.equals("")) {

        slotParms.player3 = player_name;
        slotParms.user3 = username;
        slotParms.p3cw = cw;
        slotParms.p93 = p9hole;
        slotParms.mNum3 = mNum;
        slotParms.hndcp3 = hndcp;
        slotParms.userg3 = userg;
        slotParms.guest_id3 = guest_id;

    } else if (slotParms.player4.equals("")) {

        slotParms.player4 = player_name;
        slotParms.user4 = username;
        slotParms.p4cw = cw;
        slotParms.p94 = p9hole;
        slotParms.mNum4 = mNum;
        slotParms.hndcp4 = hndcp;
        slotParms.userg4 = userg;
        slotParms.guest_id4 = guest_id;

    } else if (slotParms.player5.equals("") && allowFives) {

        slotParms.player5 = player_name;
        slotParms.user5 = username;
        slotParms.p5cw = cw;
        slotParms.p95 = p9hole;
        slotParms.mNum5 = mNum;
        slotParms.hndcp5 = hndcp;
        slotParms.userg5 = userg;
        slotParms.guest_id5 = guest_id;

    } else {
        
        return (true);
        
    }
    
     return (false);
 }
 
 
 /*
 //
 // Add player to slot for event
 //
 private static boolean addPlayer(parmSlot slotParms, String player_name, String username, String memNum, String cw, int p9hole, boolean allowFives) {

    if (slotParms.player1.equals("")) {

        slotParms.player1 = player_name;
        slotParms.user1 = username;
        slotParms.mNum1 = memNum;
        slotParms.p1cw = cw;
        slotParms.p91 = p9hole;

    } else if (slotParms.player2.equals("")) {

        slotParms.player2 = player_name;
        slotParms.user2 = username;
        slotParms.mNum2 = memNum;
        slotParms.p2cw = cw;
        slotParms.p92 = p9hole;

    } else if (slotParms.player3.equals("")) {

        slotParms.player3 = player_name;
        slotParms.user3 = username;
        slotParms.mNum3 = memNum;
        slotParms.p3cw = cw;
        slotParms.p93 = p9hole;

    } else if (slotParms.player4.equals("")) {

        slotParms.player4 = player_name;
        slotParms.user4 = username;
        slotParms.mNum4 = memNum;
        slotParms.p4cw = cw;
        slotParms.p94 = p9hole;

    } else if (slotParms.player5.equals("") && allowFives) {

        slotParms.player5 = player_name;
        slotParms.user5 = username;
        slotParms.mNum5 = memNum;
        slotParms.p5cw = cw;
        slotParms.p95 = p9hole;

    } else {
        
        return (true);
        
    }
    
     return (false);
 }
 */
 
 
 private static String getMemberNumber(String username, Connection con) {
     
    String memNum = "";
    
    try {
        
        PreparedStatement pstmt = con.prepareStatement (
          "SELECT memNum FROM member2b WHERE username = ?");

        pstmt.clearParameters();
        pstmt.setString(1, username);

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) memNum = rs.getString(1);

        pstmt.close();
        
    } catch(Exception ignore) { }
    
    return memNum;
    
 } // end getMemberNumber
 
 

 //
 // Builds the div slots for lottery requests
 //
 private void buildRow(int slotIndex, int group, String course, String course_color, String bgcolor, int max_players, int courseCount, int lcol_start[], int lcol_width[], int index, String emailOpt, int j, String dts_defaultF3Color, boolean child, String hideUnavail, String name, ResultSet rs, PrintWriter out, int multi) {

    // out.println("<!-- slotIndex=" + slotIndex + ", group=" + group + ", max_players=" + max_players + " -->");
     
    try {
     
        
    boolean tmp_found2 = false;
    boolean moved = false;
    String fullName = "";
    String player_list = "";
    int nineHole = 0;
    int x2 = 0; // player data pos in rs
    int fail = rs.getInt("fail_code");        // get failure code for request (0 = successful)
    
    
    //int sum_players = 0;

    for (int x = 0; x <= max_players - 1; x++) {

        x2 = x + ((group - 1) * max_players); // position offset
        
        fullName = rs.getString(x2 + 13);
        if (fullName.equals("MOVED")) moved = true;
        nineHole = rs.getInt(x2 + 137);
        //cw = rs.getString(x2 + 63);
        //eighteenHole = 1; //rs.getInt(x + );

        if (!fullName.equals("")) {
            if (tmp_found2) player_list = player_list + (",&nbsp; ");
            player_list = player_list + fullName + ((nineHole == 1) ? "<font style=\"font-size:8px\"> (9)</font>" : "");
            tmp_found2 = true;
            //out.print(fullName + ((nineHole == 1) ? "<font style=\"font-size:8px\"> (9)</font>" : ""));
            //sum_players++;
        }
    }
    
    
    //
    // Start Row
    //
    out.print("<div id=lottery_slot_"+ slotIndex +" time=\"" + rs.getInt("time") + "\" course=\"" + rs.getString("courseName") + "\" startX=0 startY=0 lotteryId=\"" + rs.getInt("id") + "\" group=\"" + group + "\" ");
    if (rs.getInt("in_use") == 0 && !moved) {
        // not in use
        out.println("class=lotterySlot drag=true style=\"background-color: "+ bgcolor +"\" bgc=\""+ bgcolor +"\">");
    } else {
        // in use
        out.println("class=timeSlotInUse>");
    }
    
    
    //
    // Col for 'move' and 'delete' requests
    //
    out.print("<span id=lottery_slot_" + slotIndex + "_A class=cellDataB style=\"cursor: default; left: " + lcol_start[1] + "px; width: " + lcol_width[1] + "px; background-color: #FFFFFF\">");
    j++;                                            // increment the jump label index (where to jump on page)
    out.print("<a name=\"jump" + j + "\"></a>");    // create a jump label for returns

    if (rs.getInt("in_use") == 0) {

        // not in use
        if (!child) {
            if (rs.getInt("atime1") == 0) {
                // unassigned (will need to be dragged to tee sheet)
                out.print("<img src=/" +rev+ "/images/shim.gif width=13 height=13 border=0>");
            } else {
                // had an assigned time
                out.print("<a href=\"javascript:convert('" + rs.getInt("id") + "')\" onclick=\"void(0)\" title=\"Move Request\" alt=\"Move Request\">");
                out.print("<img src=/" +rev+ "/images/dts_move.gif width=13 height=13 border=0></a>");
            }
            out.print("<img src=/" +rev+ "/images/shim.gif width=5 height=1 border=0>");
            out.print("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?mode=LOTT&index=" +index+ "&lotteryId=" + rs.getInt("id") + "&returnCourse=" +course+ "&email=" +emailOpt+ "&name=" +name+ "&hide=" +hideUnavail+ "&delete=yes\" title=\"Delete Request\" alt=\"Remove Request\" onclick=\"return confirm('Are you sure you want to permanently delete this lottery request?');\">");
            out.print("<img src=/" +rev+ "/images/dts_trash.gif width=13 height=13 border=0></a>");
        } else {
            out.print("<img src=/" +rev+ "/images/shim.gif width=13 height=13 border=0>");
            out.print("<img src=/" +rev+ "/images/shim.gif width=5 height=1 border=0>");
            out.print("<img src=/" +rev+ "/images/shim.gif width=13 height=13 border=0>");
        }

    } else {

        // in use
        out.print("<img src=/" +rev+ "/images/busy.gif width=32 height=13 border=0 alt=\"" + rs.getString("in_use_by") + "\" title=\"Busy\">");

    }
    out.println("</span>");
    
    
    //
    // Assigned Time
    //
    if (rs.getInt("in_use") == 0 && !moved) {
        out.print("<span id=lottery_slot_" + slotIndex + "_assignTime hollow=true class=cellData style=\"left: " + lcol_start[2] + "px; width: " + lcol_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
        //out.print(" <span id=lottery_slot_" + slotIndex + "_time class=cellData style=\"cursor: default; left: " + lcol_start[3] + "px; width: " + lcol_width[3] + "px; background-color: " +dts_defaultF3Color+ "\">");
    } else {
        out.print("<span id=lottery_slot_" + slotIndex + "_assignTime class=cellData style=\"cursor: default; left: " + lcol_start[2] + "px; width: " + lcol_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
    }
    
    // this could cause a / by zero error is atime has not been assigned yet (done on timer)
    
    String sfb = (rs.getInt("fb") == 0) ? " F" : " B";
    String safb = (rs.getInt("afb") == 0) ? " F" : " B";
    
    boolean unass = false;      // default to assigned
    
    switch (group) {
        case 1:
            if (rs.getInt("atime1") == 0) {
                out.print("Unass.(" +fail+ ")");
                unass = true;                      // indicate unassigned
            } else {
                out.print(getTime(rs.getInt("atime1")) + safb);
            }
            break;
        case 2:
            if (rs.getInt("atime2") == 0) {
                out.print("Unass.(" +fail+ ")");
                unass = true;                      // indicate unassigned
            } else {
                out.print(getTime(rs.getInt("atime2")) + safb);
            }
            break;
        case 3:
            if (rs.getInt("atime3") == 0) {
                out.print("Unass.(" +fail+ ")");
                unass = true;                      // indicate unassigned
            } else {
                out.print(getTime(rs.getInt("atime3")) + safb);
            }
            break;
        case 4:
            if (rs.getInt("atime4") == 0) {
                out.print("Unass.(" +fail+ ")");
                unass = true;                      // indicate unassigned
            } else {
                out.print(getTime(rs.getInt("atime4")) + safb);
            }
            break;
        case 5:
            if (rs.getInt("atime5") == 0) {
                out.print("Unass.(" +fail+ ")");
                unass = true;                      // indicate unassigned
            } else {
                out.print(getTime(rs.getInt("atime5")) + safb);
            }
            break;
    }
    out.print("</span>");
    
    
    //
    // Requested Time
    //
    out.print("<span id=lottery_slot_" + slotIndex + "_time class=cellData style=\"cursor: default; left: " + lcol_start[3] + "px; width: " + lcol_width[3] + "px; background-color: " +dts_defaultF3Color+ "\">");
    if (!child) out.print(getTime(rs.getInt("time")) + sfb);
    out.print("</span>");
    
    
    //
    // Acceptable Times
    //
    String ftime = "";      // first acceptable time
    String ltime = "";      // last acceptable time
    int before = rs.getInt("minsbefore");
    int after = rs.getInt("minsafter");

    if (before > 0) {
        ftime = getTime(SystemUtils.getFirstTime(rs.getInt("time"), before));    // get earliest time for this request
    } else {
        ftime = getTime(rs.getInt("time")); 
    }
    if (after > 0) {
        ltime = getTime(SystemUtils.getLastTime(rs.getInt("time"), after));     // get latest time for this request
    } else {
        ltime = getTime(rs.getInt("time")); 
    }
    
    out.print("<span id=lottery_slot_" + slotIndex + "_oktimes class=cellData style=\"cursor: default; left: " + lcol_start[4] + "px; width: " + lcol_width[4] + "px; background-color: " +dts_defaultF3Color+ "\">");
    if (!child && !ftime.equals("")) out.print(ftime + " - " + ltime);
    out.print("</span>");
    
    
    //
    // Course
    //
    // if (course.equals( "-ALL-" )) { // only display these cols if multi course club - assigned course & requested course
    if (multi == 1) { // only display these cols if multi course club - assigned course & requested course

        if (unass == false) {
           out.print("<span id=lottery_slot_" + slotIndex + "_course class=cellDataC style=\"cursor: default; left: " + lcol_start[5] + "px; width: " + lcol_width[5] + "px; background-color:" + course_color + "\">");
           if (!rs.getString("courseName").equals("")) { out.print(fitName(rs.getString("courseName"))); }
        } else {
           out.print("<span id=lottery_slot_" + slotIndex + "_course class=cellDataC style=\"cursor: default; left: " + lcol_start[5] + "px; width: " + lcol_width[5] + "px; background-color:white\">");
           out.print("&nbsp;");   // course not assigned yet
        }
        out.print("</span>");
        
        if (rs.getString("courseReq").equals(rs.getString("courseName"))) {   // if assigned course = requested course, use course color, else flag with bg color
           out.print("<span id=lottery_slot_" + slotIndex + "_coursereq class=cellDataC style=\"cursor: default; left: " + lcol_start[6] + "px; width: " + lcol_width[6] + "px; background-color:" + course_color + "\">");
        } else {
           out.print("<span id=lottery_slot_" + slotIndex + "_coursereq class=cellDataC style=\"cursor: default; left: " + lcol_start[6] + "px; width: " + lcol_width[6] + "px; background-color:yellow\">");
        }
        if (!rs.getString("courseReq").equals("")) { out.print(fitName(rs.getString("courseReq"))); }
        out.print("</span>");
    }
    
    
    //
    // Weight
    //
    out.print("<span id=lottery_slot_" + slotIndex + "_weight class=cellData style=\"cursor: default; left: " + lcol_start[7] + "px; width: " + lcol_width[7] + "px;\">");
    out.print(rs.getInt("weight"));
    out.print("</span>");
    
    
    //
    // Players
    //
    out.print("<span id=lottery_slot_" + slotIndex + "_members class=cellDataB value=\"\" style=\"cursor: default; left: " + lcol_start[8] + "px; width: " + lcol_width[8] + "px; text-align: left\">&nbsp;");
    if (child) out.println("&nbsp;&#187;&nbsp;");
    //if (child) out.println("<span style=\"position: relative;text-align: left;\"><img src=\"/" +rev+ "/images/dts_child.gif\" width=12 height=12 border=0 valign=top></span>");
    out.print(player_list);
    
    if (!rs.getString("notes").trim().equals("")) {

        // this won't work in rl but is meant to show a dynamic popup, or we can spawn a small popup window that will show the notes
        out.println("&nbsp; &nbsp; <img src=\"/"+rev+"/images/notes.gif\" width=10 height=12 border=0 alt=\"" + rs.getString("notes") + "\">");
    }

    out.print("</span>");

    /*
    out.print("<span class=cellDataB style=\"cursor: default; left: " + lcol_start[6] + "px; width: " + lcol_width[6] + "px\">");
    out.print(sum_players);
    out.println("</span>");

    out.print("<span class=cellDataB style=\"cursor: default; left: " + lcol_start[7] + "px; width: " + lcol_width[7] + "px\">");
    */
     //out.print(((nineHole == 0) ? "18" : "9"));
    /*
     *
    if (nineHole == 1 && eighteenHole == 1) {
        out.print("mixed");
    } else if (nineHole == 1) {
        out.print("9");
    } else if (eighteenHole == 1) {
        out.print("18");
    }
    */
    //out.println("</span>");

    out.println("</div>");
    
    } catch (SQLException exp) {
        SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false);  
        //out.println("</div>");  
    }
 }
 
 
 private void convert_all(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {
          
    String index = req.getParameter("index");
    String course = req.getParameter("course");
    String lott_date = req.getParameter("date");
    String hide = req.getParameter("hide");
    String name = req.getParameter("name");
    
    long date = 0;
    
    //
    //  Convert the values from string to int
    //
    try {

        date = Long.parseLong(lott_date);
    }
    catch (NumberFormatException exp) {
        
        SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
    }
    
    SystemUtils.moveReqs(name, date, course, false, con);
    
    int count = 0;
    
    try {

        // see if there are any left
        PreparedStatement pstmt = con.prepareStatement (
            "SELECT COUNT(*) " + 
            "FROM lreqs3 " +
            "WHERE name = ? AND date = ? AND courseName = ? AND state = 2");

        pstmt.clearParameters();        // clear the parms
        pstmt.setString(1, name);
        pstmt.setLong(2, date);
        pstmt.setString(3, course);

        ResultSet rs = pstmt.executeQuery();      // execute the prepared stmt again to start with first
    
        if ( rs.next() ) count = rs.getInt(1);
        
        pstmt.close();
    
    } catch (Exception exp) {
        
        SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
    }
    
    if (count > 0) {
        
        out.println(SystemUtils.HeadTitle("Requests Remaining"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<CENTER><BR><BR><H1>Not All Requests Where Moved</H1>");
        out.println("<BR><BR>We were not able to convert all the requests into tee times.<BR>");
        out.println("<BR>Some of the assigned times may be blocked or in use.  Try assigning the remaining requests manually.");
        out.println("<BR><BR>");

        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"LOTT\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
        out.println("<input type=\"submit\" value=\"Try Again\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
            
        out.println("</CENTER></BODY></HTML>");
        
    } else {
        
        out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_dsheet?mode=LOTT&index=" + index + "&name=" + name + "&course=" + course + "&hide=" + hide + "\">");
    
    }
       
    out.close();
 }
 
 
 
 /*
  *   Get comntrol here when a lottery request is dragged onto the tee sheet.
  * 
  */
 private void convertLottReq(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {
     

    Statement stmt = null;
    PreparedStatement pstmt = null;
    PreparedStatement pstmt6 = null;
    ResultSet rs = null;

    //
    //  Get this session's attributes
    //
    String user = "";
    String club = "";
    user = (String)session.getAttribute("user");
    club = (String)session.getAttribute("club");

    int index = 0;
    int fives = 0;      // for tee time we are dragging to
    int count = 0;
    int group = 0;
    int lottery_id = 0;
    int teecurr_id = 0;
    int rtime = 0;
    int time = 0;
    boolean overRideFives = false;
    String blocker = "";
    String [] userA = new String [5];            // array to hold usernames
   
    String hideUnavail = req.getParameter("hide");
    if (hideUnavail == null) hideUnavail = "";
    String sindex = req.getParameter("index");          //  day index value (needed by _sheet on return)
    String returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
    String suppressEmails = "no";
    
    String course = "";
    String slid = "";
    String stid = "";
    String sgroup = "";
    if (req.getParameter("to_tid") != null) stid = req.getParameter("to_tid");
    if (req.getParameter("lotteryId") != null) slid = req.getParameter("lotteryId");
    if (req.getParameter("group") != null) sgroup = req.getParameter("group");
    
    String convert = req.getParameter("convert");
    if (convert == null) convert = "";
    
    String name = req.getParameter("name");
    if (name == null) name = "";
    
    if (req.getParameter("overRideFives") != null && req.getParameter("overRideFives").equals("yes")) {
        overRideFives = true;
    }
    
    if (req.getParameter("suppressEmails") != null) {             // if email parm exists
        suppressEmails = req.getParameter("suppressEmails");
    }
    
    //
    //  parm block to hold the tee time parms
    //
    parmSlot slotParms = new parmSlot();          // allocate a parm block
        
    /*
    if (convert.equals("auto")) {
        
        // lookup the teecurr_id from the date/time/fb/course that was assigned for this request
        try {
            
            PreparedStatement pstmt = con.prepareStatement("" +
                    "SELECT t.teecurr_id " +
                    "FROM teecurr2 t, lreqs3 l " +
                    "WHERE l.id = ? AND l.courseName = t.courseName AND l.atime1 = t.time AND l.afb = t.fb " +
                    "LIMIT 1");
            
            pstmt.clearParameters();
            pstmt.setInt(1, lottery_id);
            rs = pstmt.executeQuery();
            
            if ( rs.next() ) teecurr_id = rs.getInt(1);
            
        }
        catch (NumberFormatException exp) {
            SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
            return;
        }
        
    } else {
*/
    
    //
    //  Convert the values from string to int
    //
    try {

        lottery_id = Integer.parseInt(slid);
        teecurr_id = Integer.parseInt(stid);
        group = Integer.parseInt(sgroup);
        index = Integer.parseInt(sindex);
    }
    catch (NumberFormatException exp) {
        out.println("<!-- slid="+slid+", stid="+stid+", sgroup="+sgroup+", sindex="+sindex+" -->");
        SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
    }
    
    
    //
    // Get fives value for this course (from teecurr_id)
    //
    if (!overRideFives) {
        try {

            pstmt = con.prepareStatement (
                "SELECT fives " + 
                "FROM clubparm2 c, teecurr2 t " + 
                "WHERE c.courseName = t.courseName AND t.teecurr_id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, teecurr_id);
            rs = pstmt.executeQuery();

            if (rs.next()) fives = rs.getInt("fives");

            pstmt.close();
        }
        catch (Exception e) {
        }
    } else {
        fives = 1;
    }
    
    slotParms.ind = index;                      // index value
    slotParms.club = club;                    // name of club
    slotParms.returnCourse = returnCourse;    // name of course for return to _sheet
    slotParms.suppressEmails = suppressEmails;
    
    //
    //  Load parameter object
    //
    try {

        pstmt = con.prepareStatement("SELECT * FROM teecurr2 WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, teecurr_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            slotParms.player1 = rs.getString("player1");
            slotParms.player2 = rs.getString("player2");
            slotParms.player3 = rs.getString("player3");
            slotParms.player4 = rs.getString("player4");
            slotParms.player5 = rs.getString("player5");
            slotParms.user1 = rs.getString("username1");
            slotParms.user2 = rs.getString("username2");
            slotParms.user3 = rs.getString("username3");
            slotParms.user4 = rs.getString("username4");
            slotParms.user5 = rs.getString("username5");
            slotParms.p1cw = rs.getString("p1cw");
            slotParms.p2cw = rs.getString("p2cw");
            slotParms.p3cw = rs.getString("p3cw");
            slotParms.p4cw = rs.getString("p4cw");
            slotParms.p5cw = rs.getString("p5cw");
            slotParms.in_use = rs.getInt("in_use");
            slotParms.in_use_by = rs.getString("in_use_by");
            slotParms.userg1 = rs.getString("userg1");
            slotParms.userg2 = rs.getString("userg2");
            slotParms.userg3 = rs.getString("userg3");
            slotParms.userg4 = rs.getString("userg4");
            slotParms.userg5 = rs.getString("userg5");
            slotParms.orig_by = rs.getString("orig_by");
            slotParms.pos1 = rs.getShort("pos1");
            slotParms.pos2 = rs.getShort("pos2");
            slotParms.pos3 = rs.getShort("pos3");
            slotParms.pos4 = rs.getShort("pos4");
            slotParms.pos5 = rs.getShort("pos5");
            slotParms.rest5 = rs.getString("rest5");
            slotParms.guest_id1 = rs.getInt("guest_id1");
            slotParms.guest_id2 = rs.getInt("guest_id2");
            slotParms.guest_id3 = rs.getInt("guest_id3");
            slotParms.guest_id4 = rs.getInt("guest_id4");
            slotParms.guest_id5 = rs.getInt("guest_id5");
            
            time = rs.getInt("time");
            course = rs.getString("courseName");
            blocker = rs.getString("blocker");
            
        }
        out.println("<!-- DONE LOADING slotParms WITH teecurr2 DATA -->");
        pstmt.close();

    }
    catch (Exception e) {

        out.println("<p>Error: "+e.toString()+"</p>");
    }

    // make sure there are enough open player slots
    int max_group_size = (fives == 0 || slotParms.p5.equalsIgnoreCase("No")) ? 4 : 5;
    int open_slots = 0;
    boolean has_players = false;
    if (slotParms.player1.equals("")) { open_slots++; } else { has_players = true; }
    if (slotParms.player2.equals("")) open_slots++;
    if (slotParms.player3.equals("")) open_slots++;
    if (slotParms.player4.equals("")) open_slots++;
    if (slotParms.player5.equals("") && slotParms.rest5.equals("") && fives == 1 && slotParms.p5.equalsIgnoreCase("Yes")) open_slots++;
/*    
    if (slotParms.orig_by.equals( "" )) {    // if originator field still empty (allow this person to grab this tee time again)
    
        slotParms.orig_by = user;             // set this user as the originator
    }
*/  
    out.println("<!-- open_slots="+open_slots+" | has_players="+has_players+" -->");
    
    //
    // Check in-use indicators
    //
    if (slotParms.in_use == 1 && !slotParms.in_use_by.equalsIgnoreCase( user )) {    // if time slot in use and not by this user
    
        out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<CENTER><BR><BR><H1>Reservation Timer Expired</H1>");
        out.println("<BR><BR>Sorry, but this tee time slot has been returned to the system!<BR>");
        out.println("<BR>The system timed out and released the tee time.");
        out.println("<BR><BR>");

        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        } else {
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
        }
        out.println("</form></font>");
            
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    //
    // Check blocker indicator
    //
    if (!blocker.equals("")) {    // if time slot is blocked
    
        out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<CENTER><BR><BR><H1>Tee Time Blocked</H1>");
        out.println("<BR><BR>Sorry, but this tee time slot you've selected is blocked.<BR>");
        out.println("<BR>Please choose a different time or unblock the desired time.");
        out.println("<BR><BR>");

        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"LOTT\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"submit\" value=\"Try Again\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
            
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    boolean teeTimeFull = false;
    boolean allowFives = (fives == 1) ? true : false; // does the course we are dragging to allow 5-somes?
    
    String notes = "";
    int hideNotes = 0;
    
    String type = "";
    String fields = "";
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
    String userg1 = "";
    String userg2 = "";
    String userg3 = "";
    String userg4 = "";
    String userg5 = "";
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
    int groups = 0;
    int group_size = 0;
    int group_players = 0;
    int tmp_added = 0;
    int field_offset = 0;
    int date = 0;
    
    //
    // Load lottery request data
    //
    try {
        
        pstmt = con.prepareStatement ("" +
                "SELECT l.*, c.fives " +
                "FROM lreqs3 l, clubparm2 c " +
                "WHERE id = ? AND l.courseName = c.courseName;");
        
        pstmt.clearParameters();
        pstmt.setInt(1, lottery_id);
        
        rs = pstmt.executeQuery();
        
        if ( rs.next() ) {
            
            groups = rs.getInt("groups");
            group_size = (rs.getInt("fives") == 0 || !rs.getString("p5").equalsIgnoreCase("Yes")) ? 4 : 5;
            field_offset = (group - 1) * group_size; // player data pos in rs
            
            notes = rs.getString("notes");
            hideNotes = rs.getInt("hideNotes");
            type = rs.getString("type");
            rtime = rs.getInt("time");
            date = rs.getInt("date");
            
            // if orig_by wasn't set from teecurr2 (it was if the teetime already had players)
            if (slotParms.orig_by.equals("")) slotParms.orig_by = rs.getString("orig_by");
            
            player1 = rs.getString(12 + field_offset);
            player2 = rs.getString(13 + field_offset);
            player3 = rs.getString(14 + field_offset);
            player4 = rs.getString(15 + field_offset);
            player5 = rs.getString(16 + field_offset);
            
            user1 = rs.getString(37 + field_offset);
            user2 = rs.getString(38 + field_offset);
            user3 = rs.getString(39 + field_offset);
            user4 = rs.getString(40 + field_offset);
            user5 = rs.getString(41 + field_offset);
            
            p1cw = rs.getString(62 + field_offset);
            p2cw = rs.getString(63 + field_offset);
            p3cw = rs.getString(64 + field_offset);
            p4cw = rs.getString(65 + field_offset);
            p5cw = rs.getString(66 + field_offset);
            
            userg1 = rs.getString(109 + field_offset);
            userg2 = rs.getString(110 + field_offset);
            userg3 = rs.getString(111 + field_offset);
            userg4 = rs.getString(112 + field_offset);
            userg5 = rs.getString(113 + field_offset);
            
            p91 = rs.getInt(136 + field_offset);
            p92 = rs.getInt(137 + field_offset);
            p93 = rs.getInt(138 + field_offset);
            p94 = rs.getInt(139 + field_offset);
            p95 = rs.getInt(140 + field_offset);

            guest_id1 = rs.getInt(168 + field_offset);
            guest_id2 = rs.getInt(169 + field_offset);
            guest_id3 = rs.getInt(170 + field_offset);
            guest_id4 = rs.getInt(171 + field_offset);
            guest_id5 = rs.getInt(172 + field_offset);
            
            userA[0] = user1;
            userA[1] = user2;
            userA[2] = user3;
            userA[3] = user4;
            userA[4] = user5;
            
        }
        
        pstmt.close();

        if (!player1.equals("")) {
            group_players++;
            teeTimeFull = addPlayer(slotParms, player1, user1, p1cw, userg1, p91, guest_id1, allowFives, con);
            if (!teeTimeFull) { tmp_added++; }
        }
        
        if (!player2.equals("")) {
            group_players++;
            teeTimeFull = addPlayer(slotParms, player2, user2, p2cw, userg2, p92, guest_id2, allowFives, con);
            if (!teeTimeFull) { tmp_added++; }
        }
        
        if (!player3.equals("")) {
            group_players++;
            teeTimeFull = addPlayer(slotParms, player3, user3, p3cw, userg3, p93, guest_id3, allowFives, con);
            if (!teeTimeFull) { tmp_added++; }
        }
        
        if (!player4.equals("")) {
            group_players++;
            teeTimeFull = addPlayer(slotParms, player4, user4, p4cw, userg4, p94, guest_id4, allowFives, con);
            if (!teeTimeFull) { tmp_added++; }
        }
        
        if (!player5.equals("") && allowFives && group_size == 5) {
            group_players++;
            teeTimeFull = addPlayer(slotParms, player5, user5, p5cw, userg5, p95, guest_id5, allowFives, con);
            if (!teeTimeFull) { tmp_added++; }
        }
        
    } catch(Exception exp) {
        SystemUtils.buildDatabaseErrMsg("Error Adding Players", exp.getMessage(), out, false);
        //return;
    }
    
    
    // Let see if all players from this request where moved
    if ( teeTimeFull || tmp_added == 0 ) {
    
        out.println(SystemUtils.HeadTitle("Unable to Add All Players"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Unable to Add All Players</H3><BR>");
        out.println("<BR>Sorry we were not able to add all the players to this tee time.<br><br>");
        out.println("<BR><BR>No changes were made to the lottery request or tee sheet.");
        out.println("<BR><BR>");
       
        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"LOTT\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"submit\" value=\"Try Again\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
        
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    out.println("<!-- field_offset=" + field_offset + " -->");
    out.println("<!-- group=" + group + " | max_group_size=" + max_group_size + " -->");
    out.println("<!-- groups=" + groups + " | group_size=" + group_size + " | group_players=" + group_players + " -->");
    out.println("<!-- lottery_id="+lottery_id+" | teecurr_id="+teecurr_id+" | tmp_added="+tmp_added+" -->");
    
    
    
    // first lets see if they are trying to fill the 5th player slot when it is unavailable for this course
    if ( !slotParms.player5.equals("") && fives == 0 ) { // ( (!slotParms.rest5.equals("") && overRideFives == false) || fives == 0)
    
        out.println(SystemUtils.HeadTitle("5-some Restricted - Reject"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Member Restricted</H3><BR>");
        out.println("<BR>Sorry, <b>5-somes</b> are not allowed on this course.<br><br>");
        out.println("<BR><BR>Please move the lottery request to another course.");
        out.println("<BR><BR>");
       
        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"LOTT\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"submit\" value=\"Try Again\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
        
        /*
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"LOTT\">");
        out.println("<input type=\"hidden\" name=\"convert\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"overRideFives\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"lotteryId\" value=\"" + lottery_id + "\">");
        out.println("<input type=\"hidden\" name=\"to_tid\" value=\"" + teecurr_id + "\">");
        out.println("<input type=\"hidden\" name=\"group\" value=\"" + group + "\">");
        out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
        */
        
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
        
    } // end 5-some rejection
    
    
    //
    //  Before we update the tee time, go check for any flags to be added to members' names for the pro tee sheet
    //
    if (!slotParms.user1.equals("") || !slotParms.user2.equals("") || !slotParms.user3.equals("") || 
        !slotParms.user4.equals("") || !slotParms.user5.equals("")) {
       
       verifySlot.checkTFlag(slotParms, con);    // check for tflags if any members in tee time
    }
         
         
    //
    // Update entry in teecurr2
    //
    try {

        pstmt6 = con.prepareStatement (
             "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
             "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
             "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
             "hndcp4 = ?, player5 = ?, username5 = ?, " + 
             "p5cw = ?, hndcp5 = ?, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " +
             "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
             "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, conf = ?, " +
             "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, pos1 = ?, pos2 = ?, pos3 = ?, pos4 = ?, pos5 = ?, lottery_email = 1, " +
             "notes = ?, hideNotes = ?, tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, " +
             "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ? " +
             "WHERE teecurr_id = ?");

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
        pstmt6.setString(17, slotParms.player5);
        pstmt6.setString(18, slotParms.user5);
        pstmt6.setString(19, slotParms.p5cw);
        pstmt6.setFloat(20, slotParms.hndcp5);
        pstmt6.setString(21, slotParms.notes);
        pstmt6.setInt(22, 0); // hide
        pstmt6.setInt(23, 0); // proNew
        pstmt6.setInt(24, 0); // proMod
        pstmt6.setString(25, slotParms.mNum1);
        pstmt6.setString(26, slotParms.mNum2);
        pstmt6.setString(27, slotParms.mNum3);
        pstmt6.setString(28, slotParms.mNum4);
        pstmt6.setString(29, slotParms.mNum5);
        pstmt6.setString(30, slotParms.userg1);
        pstmt6.setString(31, slotParms.userg2);
        pstmt6.setString(32, slotParms.userg3);
        pstmt6.setString(33, slotParms.userg4);
        pstmt6.setString(34, slotParms.userg5);
        pstmt6.setString(35, slotParms.orig_by);
        pstmt6.setString(36, slotParms.conf);
        pstmt6.setInt(37, slotParms.p91);
        pstmt6.setInt(38, slotParms.p92);
        pstmt6.setInt(39, slotParms.p93);
        pstmt6.setInt(40, slotParms.p94);
        pstmt6.setInt(41, slotParms.p95);
        pstmt6.setInt(42, slotParms.pos1);
        pstmt6.setInt(43, slotParms.pos2);
        pstmt6.setInt(44, slotParms.pos3);
        pstmt6.setInt(45, slotParms.pos4);
        pstmt6.setInt(46, slotParms.pos5);
        pstmt6.setString(47, notes);
        pstmt6.setInt(48, hideNotes);
        pstmt6.setString(49, slotParms.tflag1);
        pstmt6.setString(50, slotParms.tflag2);
        pstmt6.setString(51, slotParms.tflag3);
        pstmt6.setString(52, slotParms.tflag4);
        pstmt6.setString(53, slotParms.tflag5);
        pstmt6.setInt(54, slotParms.guest_id1);
        pstmt6.setInt(55, slotParms.guest_id2);
        pstmt6.setInt(56, slotParms.guest_id3);
        pstmt6.setInt(57, slotParms.guest_id4);
        pstmt6.setInt(58, slotParms.guest_id5);

        pstmt6.setInt(59, teecurr_id);

        count = pstmt6.executeUpdate();      // execute the prepared stmt
        
        pstmt6.close();

    } 
    catch (Exception exp) {
        
        SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
        return;
    }
    
    // if the tee time was updated then remove the request
    if (count == 1) {
        
        // depending on how many groups this request had, we'll either delete or modify the request
        if (groups == 1) {
            
            // there was one group in the request - just delete the request
            try {
                
                pstmt = con.prepareStatement("UPDATE lreqs3 SET state = 4 WHERE id = ?");
                pstmt.clearParameters();
                pstmt.setInt(1, lottery_id);
                pstmt.executeUpdate();
                pstmt.close();
                
            } catch (Exception exp) {
                SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false);
                return;
            }
            
        } else {
            
            // there were multiple groups in this request, lets mark the specific group
            int tmp_pos = 0;
            int tmp = 0;
            int tmp_loop = 1;
            
            // get the position of the first player for the group we moved
            if (group == 1) 
                { tmp_pos = 0; } 
            else 
                { tmp_pos = group_size * (group - 1); }
            
            // build the fields string for the sql statement
            for (tmp = tmp_pos; tmp <= tmp_pos + group_size; tmp++) {
                
                if (tmp_loop > tmp_added) break;
                
                fields = fields + " player" + (tmp + 1) + " = 'MOVED',";
                tmp_loop++;
            }
            
            // trim trailing comma
            fields=fields.substring(0, fields.length() - 1);
            
            out.println("<!-- tmp_pos=" + tmp_pos + " -->");
            out.println("<!-- fields=" + fields + " -->");
            
            try {
                
                pstmt = con.prepareStatement("UPDATE lreqs3 SET " + fields + " WHERE id = ?");
                pstmt.clearParameters();
                pstmt.setInt(1, lottery_id);
                pstmt.executeUpdate();
                pstmt.close();
               
            } catch (Exception exp) {
                SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
                return;
            }
        
            
            // now check all the players in this request and if they equal 'MOVED' then we can delete this request
            try {
                
                pstmt = con.prepareStatement ("" +
                        "SELECT * FROM lreqs3 WHERE id = ? AND " + 
                            "(player1 = 'MOVED' OR player1 = '') AND " + 
                            "(player2 = 'MOVED' OR player2 = '') AND " + 
                            "(player3 = 'MOVED' OR player3 = '') AND " + 
                            "(player4 = 'MOVED' OR player4 = '') AND " + 
                            "(player5 = 'MOVED' OR player5 = '') AND " + 
                            "(player6 = 'MOVED' OR player6 = '') AND " + 
                            "(player7 = 'MOVED' OR player7 = '') AND " + 
                            "(player8 = 'MOVED' OR player8 = '') AND " + 
                            "(player9 = 'MOVED' OR player9 = '') AND " + 
                            "(player10 = 'MOVED' OR player10 = '') AND " + 
                            "(player11 = 'MOVED' OR player11 = '') AND " + 
                            "(player12 = 'MOVED' OR player12 = '') AND " + 
                            "(player13 = 'MOVED' OR player13 = '') AND " + 
                            "(player14 = 'MOVED' OR player14 = '') AND " + 
                            "(player15 = 'MOVED' OR player15 = '') AND " + 
                            "(player16 = 'MOVED' OR player16 = '') AND " + 
                            "(player17 = 'MOVED' OR player17 = '') AND " + 
                            "(player18 = 'MOVED' OR player18 = '') AND " + 
                            "(player19 = 'MOVED' OR player19 = '') AND " + 
                            "(player20 = 'MOVED' OR player20 = '') AND " + 
                            "(player21 = 'MOVED' OR player21 = '') AND " + 
                            "(player22 = 'MOVED' OR player22 = '') AND " + 
                            "(player23 = 'MOVED' OR player23 = '') AND " + 
                            "(player24 = 'MOVED' OR player24 = '') AND " + 
                            "(player25 = 'MOVED' OR player25 = '');");

                pstmt.clearParameters();
                pstmt.setInt(1, lottery_id);

                rs = pstmt.executeQuery();

                if ( rs.next() ) {
                    
                    pstmt6 = con.prepareStatement("UPDATE lreqs3 SET state = 4 WHERE id = ?");
                    pstmt6.clearParameters();
                    pstmt6.setInt(1, lottery_id);
                    pstmt6.executeUpdate();
                    pstmt6.close();
                    
                }
                
                pstmt.close();
               
            } catch (Exception exp) {
                SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
                return;
            }
        }
        
    } // end if updated teecurr2 entry

/*
    out.println("<!-- count="+count+" -->");
    
    out.println("<!-- ");
    out.println("slotParms.player1=" + slotParms.player1);
    out.println("slotParms.player2=" + slotParms.player2);
    out.println("slotParms.player3=" + slotParms.player3);
    out.println("slotParms.player4=" + slotParms.player4);
    out.println("slotParms.player5=" + slotParms.player5);
    out.println("");
    out.println("slotParms.p1cw=" + slotParms.p1cw);
    out.println("slotParms.p2cw=" + slotParms.p2cw);
    out.println("slotParms.p3cw=" + slotParms.p3cw);
    out.println("slotParms.p4cw=" + slotParms.p4cw);
    out.println("slotParms.p5cw=" + slotParms.p5cw);
    out.println("");
    out.println("slotParms.p91=" + slotParms.p91);
    out.println("slotParms.p92=" + slotParms.p92);
    out.println("slotParms.p93=" + slotParms.p93);
    out.println("slotParms.p94=" + slotParms.p94);
    out.println("slotParms.p95=" + slotParms.p95);
    out.println("");
    out.println("slotParms.mNum1=" + slotParms.mNum1);
    out.println("slotParms.mNum2=" + slotParms.mNum2);
    out.println("slotParms.mNum3=" + slotParms.mNum3);
    out.println("slotParms.mNum4=" + slotParms.mNum4);
    out.println("slotParms.mNum5=" + slotParms.mNum5);
    out.println("");
    out.println(" -->");
*/

    

    //
    //  If lottery type = Weighted By Proximity, determine time between request and assigned
    //
    if (type.equals( "WeightedBP" )) {

        int interval = 0;

        try {

            pstmt = con.prepareStatement ("" +
                    "SELECT betwn FROM clubparm2 WHERE courseName = ?;");

            pstmt.clearParameters();
            pstmt.setString(1, course);

            rs = pstmt.executeQuery();

            if ( rs.next() ) {

                interval = rs.getInt(1);
            }
            
            pstmt.close();

            // adjust requested time by which group they are (rtime = rtime + (group# * interval)
            // time = drop time
            
            if (group > 1) {           // if not the first group
               
               group--;                // adjust for multiplier (2nd group should be requested time + 1 interval)
               
               rtime = rtime + (group * interval);            

               int tmp_hr = rtime / 100;
               int tmp_min = rtime - (tmp_hr * 100);

            //   tmp_min = tmp_min + interval;               // bump to next segment  ???????????

               if (tmp_min > 59) {

                   tmp_hr++;                               // next hour
                   tmp_min = tmp_min - 60;                 // adjust minutes
               }

               rtime = (tmp_hr * 100) + tmp_min;           // set new time
            }
            
            // calculate mins difference
            int proxMins = SystemUtils.calcProxTime(rtime, time);
            
            PreparedStatement pstmtd2 = con.prepareStatement (
                    "INSERT INTO lassigns5 (username, lname, date, mins) " +
                    "VALUES (?, ?, ?, ?)");
            
            //
            //  Save each members' weight for this request
            //
            for (int i = 0; i < 5; i++) {          // check all 5 possible players

                if (!userA[i].equals( "" )) {      // if player is a member

                    pstmtd2.clearParameters();
                    pstmtd2.setString(1, userA[i]);
                    pstmtd2.setString(2, name);
                    pstmtd2.setLong(3, date);
                    pstmtd2.setInt(4, proxMins);

                    pstmtd2.executeUpdate();
                }
            }
            
            pstmtd2.close();

        } catch (Exception exp) {
            SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false);
            return;
        }
        
    } // end of IF Weighted by Proximity lottery type

    //
    // Completed update - reload page
    //
    out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_dsheet?mode=LOTT&index=" + slotParms.ind + "&course=" + slotParms.returnCourse + "&name=" + name + "&hide="+hideUnavail+"\">");
    out.close();
    return;
 }
 
 
 
 //
 // Auto conversion of lottery requests to tee times
 //
 private void auto_convert(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {
   
   PreparedStatement pstmt = null;
   PreparedStatement pstmtd = null;
   PreparedStatement pstmtd2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   
   
   //
   //  parm block to hold the tee time parms
   //
   parmSlot slotParms = new parmSlot();          // allocate a parm block


   String course = "";
   String player1 = "";
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
   String user25 = "";

   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String userg6 = "";
   String userg7 = "";
   String userg8 = "";
   String userg9 = "";
   String userg10 = "";
   String userg11 = "";
   String userg12 = "";
   String userg13 = "";
   String userg14 = "";
   String userg15 = "";
   String userg16 = "";
   String userg17 = "";
   String userg18 = "";
   String userg19 = "";
   String userg20 = "";
   String userg21 = "";
   String userg22 = "";
   String userg23 = "";
   String userg24 = "";
   String userg25 = "";

   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";

   String color = "";
   String p5 = "";
   String type = "";
   String pref = "";
   String approve = "";
   String day = "";
   String notes = "";
   String in_use_by = "";
   String orig_by = "";
   String parm = "";
   String hndcps = "";

   String player5T = "";
   String user5T = "";
   String p5cwT = "";

   String errorMsg = "";

   String [] userA = new String [25];            // array to hold usernames

   long id = 0;
   
   int date = 0;
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

   int i = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int fb = 0;
   int afb = 0;
   int afb2 = 0;
   int afb3 = 0;
   int afb4 = 0;
   int afb5 = 0;
   int count = 0;
   int groups = 0;
   int time = 0;
   int rtime = 0;
   int atime1 = 0;
   int atime2 = 0;
   int atime3 = 0;
   int atime4 = 0;
   int atime5 = 0;
   int players = 0;
   int hide = 0;
   int proNew = 0;
   int proMod = 0;
   int memNew = 0;
   int memMod = 0;
   int proxMins = 0;

   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;

   float hndcp1 = 99;
   float hndcp2 = 99;
   float hndcp3 = 99;
   float hndcp4 = 99;
   float hndcp5 = 99;

   boolean ok = true;

   int lottery_id = 0;
   int index = 0;
   String slid = "";
   String sindex = req.getParameter("index");
   String hideUnavail = req.getParameter("hide");
   String returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
   if (req.getParameter("lotteryId") != null) slid = req.getParameter("lotteryId");
   String name = req.getParameter("name");
   if (name == null) name = "";
   
    //
    //  Get the lottery_id
    //
    try {

        lottery_id = Integer.parseInt(slid);
        index = Integer.parseInt(sindex);
    }
    catch (NumberFormatException exp) {
        SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
        return;
    }
   

   try {

      errorMsg = "Error in Proshop_dsheet:auto_convert (get lottery request): ";

      //
      //  Get the Lottery Requests for the lottery passed
      //
      pstmt = con.prepareStatement (
         "SELECT mm, dd, yy, day, time, " +
         "player1, player2, player3, player4, player5, player6, player7, player8, player9, player10, " +
         "player11, player12, player13, player14, player15, player16, player17, player18, player19, player20, " +
         "player21, player22, player23, player24, player25, " +
         "user1, user2, user3, user4, user5, user6, user7, user8, user9, user10, " +
         "user11, user12, user13, user14, user15, user16, user17, user18, user19, user20, " +
         "user21, user22, user23, user24, user25, " +
         "p1cw, p2cw, p3cw, p4cw, p5cw, p6cw, p7cw, p8cw, p9cw, p10cw, " +
         "p11cw, p12cw, p13cw, p14cw, p15cw, p16cw, p17cw, p18cw, p19cw, p20cw, " +
         "p21cw, p22cw, p23cw, p24cw, p25cw, " +
         "notes, hideNotes, fb, proNew, proMod, memNew, memMod, id, groups, atime1, atime2, atime3, " +
         "atime4, atime5, afb, p5, players, userg1, userg2, userg3, userg4, userg5, userg6, userg7, userg8, " +
         "userg9, userg10, userg11, userg12, userg13, userg14, userg15, userg16, userg17, userg18, userg19, " +
         "userg20, userg21, userg22, userg23, userg24, userg25, orig_by, " +
         "p91, p92, p93, p94, p95, p96, p97, p98, p99, p910, " +
         "p911, p912, p913, p914, p915, p916, p917, p918, p919, p920, " +
         "p921, p922, p923, p924, p925, afb2, afb3, afb4, afb5, type, courseName, date, " +
         "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5, guest_id6, guest_id7, guest_id8, guest_id9, guest_id10, " +
         "guest_id11, guest_id12, guest_id13, guest_id14, guest_id15, guest_id16, guest_id17, guest_id18, guest_id19, guest_id20, " +
         "guest_id21, guest_id22, guest_id23, guest_id24, guest_id25 " +
         "FROM lreqs3 " +
         "WHERE id = ? AND state = 2");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, lottery_id);

      rs = pstmt.executeQuery();      // execute the prepared stmt again to start with first

      while (rs.next()) {

         mm = rs.getInt(1);
         dd = rs.getInt(2);
         yy = rs.getInt(3);
         day = rs.getString(4);
         rtime = rs.getInt(5);
         player1 = rs.getString(6);
         player2 = rs.getString(7);
         player3 = rs.getString(8);
         player4 = rs.getString(9);
         player5 = rs.getString(10);
         player6 = rs.getString(11);
         player7 = rs.getString(12);
         player8 = rs.getString(13);
         player9 = rs.getString(14);
         player10 = rs.getString(15);
         player11 = rs.getString(16);
         player12 = rs.getString(17);
         player13 = rs.getString(18);
         player14 = rs.getString(19);
         player15 = rs.getString(20);
         player16 = rs.getString(21);
         player17 = rs.getString(22);
         player18 = rs.getString(23);
         player19 = rs.getString(24);
         player20 = rs.getString(25);
         player21 = rs.getString(26);
         player22 = rs.getString(27);
         player23 = rs.getString(28);
         player24 = rs.getString(29);
         player25 = rs.getString(30);
         user1 = rs.getString(31);
         user2 = rs.getString(32);
         user3 = rs.getString(33);
         user4 = rs.getString(34);
         user5 = rs.getString(35);
         user6 = rs.getString(36);
         user7 = rs.getString(37);
         user8 = rs.getString(38);
         user9 = rs.getString(39);
         user10 = rs.getString(40);
         user11 = rs.getString(41);
         user12 = rs.getString(42);
         user13 = rs.getString(43);
         user14 = rs.getString(44);
         user15 = rs.getString(45);
         user16 = rs.getString(46);
         user17 = rs.getString(47);
         user18 = rs.getString(48);
         user19 = rs.getString(49);
         user20 = rs.getString(50);
         user21 = rs.getString(51);
         user22 = rs.getString(52);
         user23 = rs.getString(53);
         user24 = rs.getString(54);
         user25 = rs.getString(55);
         p1cw = rs.getString(56);
         p2cw = rs.getString(57);
         p3cw = rs.getString(58);
         p4cw = rs.getString(59);
         p5cw = rs.getString(60);
         p6cw = rs.getString(61);
         p7cw = rs.getString(62);
         p8cw = rs.getString(63);
         p9cw = rs.getString(64);
         p10cw = rs.getString(65);
         p11cw = rs.getString(66);
         p12cw = rs.getString(67);
         p13cw = rs.getString(68);
         p14cw = rs.getString(69);
         p15cw = rs.getString(70);
         p16cw = rs.getString(71);
         p17cw = rs.getString(72);
         p18cw = rs.getString(73);
         p19cw = rs.getString(74);
         p20cw = rs.getString(75);
         p21cw = rs.getString(76);
         p22cw = rs.getString(77);
         p23cw = rs.getString(78);
         p24cw = rs.getString(79);
         p25cw = rs.getString(80);
         notes = rs.getString(81);
         hide = rs.getInt(82);
         fb = rs.getInt(83);
         proNew = rs.getInt(84);
         proMod = rs.getInt(85);
         memNew = rs.getInt(86);
         memMod = rs.getInt(87);
         id = rs.getLong(88);
         groups = rs.getInt(89);
         atime1 = rs.getInt(90);
         atime2 = rs.getInt(91);
         atime3 = rs.getInt(92);
         atime4 = rs.getInt(93);
         atime5 = rs.getInt(94);
         afb = rs.getInt(95);
         p5 = rs.getString(96);
         players = rs.getInt(97);
         userg1 = rs.getString(98);
         userg2 = rs.getString(99);
         userg3 = rs.getString(100);
         userg4 = rs.getString(101);
         userg5 = rs.getString(102);
         userg6 = rs.getString(103);
         userg7 = rs.getString(104);
         userg8 = rs.getString(105);
         userg9 = rs.getString(106);
         userg10 = rs.getString(107);
         userg11 = rs.getString(108);
         userg12 = rs.getString(109);
         userg13 = rs.getString(110);
         userg14 = rs.getString(111);
         userg15 = rs.getString(112);
         userg16 = rs.getString(113);
         userg17 = rs.getString(114);
         userg18 = rs.getString(115);
         userg19 = rs.getString(116);
         userg20 = rs.getString(117);
         userg21 = rs.getString(118);
         userg22 = rs.getString(119);
         userg23 = rs.getString(120);
         userg24 = rs.getString(121);
         userg25 = rs.getString(122);
         orig_by = rs.getString(123);
         p91 = rs.getInt(124);
         p92 = rs.getInt(125);
         p93 = rs.getInt(126);
         p94 = rs.getInt(127);
         p95 = rs.getInt(128);
         p96 = rs.getInt(129);
         p97 = rs.getInt(130);
         p98 = rs.getInt(131);
         p99 = rs.getInt(132);
         p910 = rs.getInt(133);
         p911 = rs.getInt(134);
         p912 = rs.getInt(135);
         p913 = rs.getInt(136);
         p914 = rs.getInt(137);
         p915 = rs.getInt(138);
         p916 = rs.getInt(139);
         p917 = rs.getInt(140);
         p918 = rs.getInt(141);
         p919 = rs.getInt(142);
         p920 = rs.getInt(143);
         p921 = rs.getInt(144);
         p922 = rs.getInt(145);
         p923 = rs.getInt(146);
         p924 = rs.getInt(147);
         p925 = rs.getInt(148);
         afb2 = rs.getInt(149);
         afb3 = rs.getInt(150);
         afb4 = rs.getInt(151);
         afb5 = rs.getInt(152);
         type = rs.getString(153);
         course = rs.getString(154);
         date = rs.getInt(155);
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

         if (atime1 != 0) {          // only process if its assigned

            ok = SystemUtils.checkBlockers(con, id);    // check if the assigned times are blocked
            
            if (!ok) {    // if time slot is blocked

                out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
                out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<CENTER><BR><BR><H1>Tee Time Blocked</H1>");
                out.println("<BR><BR>Sorry, but this tee time slot you've selected is blocked.<BR>");
                out.println("<BR>Please choose a different time or unblock the desired time.");
                out.println("<BR><BR>");

                out.println("<font size=\"2\">");
                out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
                out.println("<input type=\"hidden\" name=\"mode\" value=\"LOTT\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                out.println("<input type=\"submit\" value=\"Try Again\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                out.println("</form></font>");

                out.println("</CENTER></BODY></HTML>");
                out.close();
                return;
            }
            
            ok = SystemUtils.checkInUse(con, id);     // check if assigned tee times are currently in use
            

            if (ok == true) {             // if ok to proceed (no tee times are in use)

               //
               //  Save the usernames
               //
               userA[0] = user1;
               userA[1] = user2;
               userA[2] = user3;
               userA[3] = user4;
               userA[4] = user5;
               userA[5] = user6;
               userA[6] = user7;
               userA[7] = user8;
               userA[8] = user9;
               userA[9] = user10;
               userA[10] = user11;
               userA[11] = user12;
               userA[12] = user13;
               userA[13] = user14;
               userA[14] = user15;
               userA[15] = user16;
               userA[16] = user17;
               userA[17] = user18;
               userA[18] = user19;
               userA[19] = user20;
               userA[20] = user21;
               userA[21] = user22;
               userA[22] = user23;
               userA[23] = user24;
               userA[24] = user25;

               //
               //  create 1 tee time for each group requested (groups = )
               //
               time = atime1;    // time for this tee time
               hndcp1 = 99;      // init
               hndcp2 = 99;
               hndcp3 = 99;
               hndcp4 = 99;
               hndcp5 = 99;
               mNum1 = "";
               mNum2 = "";
               mNum3 = "";
               mNum4 = "";
               mNum5 = "";

               //
               //  Save area for tee time and email processing - by groups
               //
               String g1user1 = user1;
               String g1user2 = user2;
               String g1user3 = user3;
               String g1user4 = user4;
               String g1user5 = "";
               String g1player1 = player1;
               String g1player2 = player2;
               String g1player3 = player3;
               String g1player4 = player4;
               String g1player5 = "";
               String g1p1cw = p1cw;
               String g1p2cw = p2cw;
               String g1p3cw = p3cw;
               String g1p4cw = p4cw;
               String g1p5cw = "";
               String g1userg1 = userg1;
               String g1userg2 = userg2;
               String g1userg3 = userg3;
               String g1userg4 = userg4;
               String g1userg5 = "";
               int g1p91 = p91;
               int g1p92 = p92;
               int g1p93 = p93;
               int g1p94 = p94;
               int g1p95 = 0;
               int g1guest_id1 = guest_id1;
               int g1guest_id2 = guest_id2;
               int g1guest_id3 = guest_id3;
               int g1guest_id4 = guest_id4;
               int g1guest_id5 = 0;

               String g2user1 = "";
               String g2user2 = "";
               String g2user3 = "";
               String g2user4 = "";
               String g2user5 = "";
               String g2player1 = "";
               String g2player2 = "";
               String g2player3 = "";
               String g2player4 = "";
               String g2player5 = "";
               String g2p1cw = "";
               String g2p2cw = "";
               String g2p3cw = "";
               String g2p4cw = "";
               String g2p5cw = "";
               String g2userg1 = "";
               String g2userg2 = "";
               String g2userg3 = "";
               String g2userg4 = "";
               String g2userg5 = "";
               int g2p91 = 0;
               int g2p92 = 0;
               int g2p93 = 0;
               int g2p94 = 0;
               int g2p95 = 0;
               int g2guest_id1 = 0;
               int g2guest_id2 = 0;
               int g2guest_id3 = 0;
               int g2guest_id4 = 0;
               int g2guest_id5 = 0;

               String g3user1 = "";
               String g3user2 = "";
               String g3user3 = "";
               String g3user4 = "";
               String g3user5 = "";
               String g3player1 = "";
               String g3player2 = "";
               String g3player3 = "";
               String g3player4 = "";
               String g3player5 = "";
               String g3p1cw = "";
               String g3p2cw = "";
               String g3p3cw = "";
               String g3p4cw = "";
               String g3p5cw = "";
               String g3userg1 = "";
               String g3userg2 = "";
               String g3userg3 = "";
               String g3userg4 = "";
               String g3userg5 = "";
               int g3p91 = 0;
               int g3p92 = 0;
               int g3p93 = 0;
               int g3p94 = 0;
               int g3p95 = 0;
               int g3guest_id1 = 0;
               int g3guest_id2 = 0;
               int g3guest_id3 = 0;
               int g3guest_id4 = 0;
               int g3guest_id5 = 0;

               String g4user1 = "";
               String g4user2 = "";
               String g4user3 = "";
               String g4user4 = "";
               String g4user5 = "";
               String g4player1 = "";
               String g4player2 = "";
               String g4player3 = "";
               String g4player4 = "";
               String g4player5 = "";
               String g4p1cw = "";
               String g4p2cw = "";
               String g4p3cw = "";
               String g4p4cw = "";
               String g4p5cw = "";
               String g4userg1 = "";
               String g4userg2 = "";
               String g4userg3 = "";
               String g4userg4 = "";
               String g4userg5 = "";
               int g4p91 = 0;
               int g4p92 = 0;
               int g4p93 = 0;
               int g4p94 = 0;
               int g4p95 = 0;
               int g4guest_id1 = 0;
               int g4guest_id2 = 0;
               int g4guest_id3 = 0;
               int g4guest_id4 = 0;
               int g4guest_id5 = 0;

               String g5user1 = "";
               String g5user2 = "";
               String g5user3 = "";
               String g5user4 = "";
               String g5user5 = "";
               String g5player1 = "";
               String g5player2 = "";
               String g5player3 = "";
               String g5player4 = "";
               String g5player5 = "";
               String g5p1cw = "";
               String g5p2cw = "";
               String g5p3cw = "";
               String g5p4cw = "";
               String g5p5cw = "";
               String g5userg1 = "";
               String g5userg2 = "";
               String g5userg3 = "";
               String g5userg4 = "";
               String g5userg5 = "";
               int g5p91 = 0;
               int g5p92 = 0;
               int g5p93 = 0;
               int g5p94 = 0;
               int g5p95 = 0;
               int g5guest_id1 = 0;
               int g5guest_id2 = 0;
               int g5guest_id3 = 0;
               int g5guest_id4 = 0;
               int g5guest_id5 = 0;
               
               errorMsg = "Error in SystemUtils moveReqs (get mem# and hndcp): ";

               //
               //  Get Member# and Handicap for each member
               //
               if (!user1.equals( "" )) {        // if player is a member

                  parm = SystemUtils.getUser(con, user1);     // get mNum and hndcp for member

                  StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                  mNum1 = tok.nextToken();         // member Number
                  hndcps = tok.nextToken();       // handicap

                  hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int
               }
               if (!user2.equals( "" )) {        // if player is a member

                  parm = SystemUtils.getUser(con, user2);     // get mNum and hndcp for member

                  StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                  mNum2 = tok.nextToken();         // member Number
                  hndcps = tok.nextToken();       // handicap

                  hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int
               }
               if (!user3.equals( "" )) {        // if player is a member

                  parm = SystemUtils.getUser(con, user3);     // get mNum and hndcp for member

                  StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                  mNum3 = tok.nextToken();         // member Number
                  hndcps = tok.nextToken();       // handicap

                  hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int
               }
               if (!user4.equals( "" )) {        // if player is a member

                  parm = SystemUtils.getUser(con, user4);     // get mNum and hndcp for member

                  StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                  mNum4 = tok.nextToken();         // member Number
                  hndcps = tok.nextToken();       // handicap

                  hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int
               }
               if (p5.equals( "Yes" )) {

                  if (!user5.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, user5);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum5 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  g1player5 = player5;
                  g1user5 = user5;
                  g1p5cw = p5cw;
                  g1userg5 = userg5;
                  g1p95 = p95;
                  g1guest_id5 = guest_id5;
               }

               if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                  mNum1 = "";                  // convert back to null
               }
               if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                  mNum2 = "";                  // convert back to null
               }
               if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                  mNum3 = "";                  // convert back to null
               }
               if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                  mNum4 = "";                  // convert back to null
               }
               if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                  mNum5 = "";                  // convert back to null
               }

               
               //
               //  Before we update the tee time, go check for any flags to be added to members' names for the pro tee sheet
               //
               slotParms.user1 = g1user1;
               slotParms.user2 = g1user2;
               slotParms.user3 = g1user3;
               slotParms.user4 = g1user4;
               slotParms.user5 = g1user5;

               verifySlot.checkTFlag(slotParms, con);    // check for tflags if any members in tee time
                    
               
               errorMsg = "Error in SystemUtils moveReqs (put group 1 in tee sheet): ";

               //
               //  Update the tee slot in teecurr
               //
               //  Clear the lottery name so this tee time is displayed in _sheet even though there
               //  may be some requests still outstanding (state = 4).
               //
               PreparedStatement pstmt6 = con.prepareStatement (
                  "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                  "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                  "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                  "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                  "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " + // lottery = ''
                  "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                  "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                  "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, lottery_email = 1, " +
                  "tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, " +
                  "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ? " +
                  "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               pstmt6.clearParameters();        // clear the parms
               pstmt6.setString(1, g1player1);
               pstmt6.setString(2, g1player2);
               pstmt6.setString(3, g1player3);
               pstmt6.setString(4, g1player4);
               pstmt6.setString(5, g1user1);
               pstmt6.setString(6, g1user2);
               pstmt6.setString(7, g1user3);
               pstmt6.setString(8, g1user4);
               pstmt6.setString(9, g1p1cw);
               pstmt6.setString(10, g1p2cw);
               pstmt6.setString(11, g1p3cw);
               pstmt6.setString(12, g1p4cw);
               pstmt6.setFloat(13, hndcp1);
               pstmt6.setFloat(14, hndcp2);
               pstmt6.setFloat(15, hndcp3);
               pstmt6.setFloat(16, hndcp4);
               pstmt6.setString(17, g1player5);
               pstmt6.setString(18, g1user5);
               pstmt6.setString(19, g1p5cw);
               pstmt6.setFloat(20, hndcp5);
               pstmt6.setString(21, notes);
               pstmt6.setInt(22, hide);
               pstmt6.setInt(23, proNew);
               pstmt6.setInt(24, proMod);
               pstmt6.setInt(25, memNew);
               pstmt6.setInt(26, memMod);
               pstmt6.setString(27, mNum1);
               pstmt6.setString(28, mNum2);
               pstmt6.setString(29, mNum3);
               pstmt6.setString(30, mNum4);
               pstmt6.setString(31, mNum5);
               pstmt6.setString(32, g1userg1);
               pstmt6.setString(33, g1userg2);
               pstmt6.setString(34, g1userg3);
               pstmt6.setString(35, g1userg4);
               pstmt6.setString(36, g1userg5);
               pstmt6.setString(37, orig_by);
               pstmt6.setInt(38, g1p91);
               pstmt6.setInt(39, g1p92);
               pstmt6.setInt(40, g1p93);
               pstmt6.setInt(41, g1p94);
               pstmt6.setInt(42, g1p95);
               pstmt6.setString(43, slotParms.tflag1);
               pstmt6.setString(44, slotParms.tflag2);
               pstmt6.setString(45, slotParms.tflag3);
               pstmt6.setString(46, slotParms.tflag4);
               pstmt6.setString(47, slotParms.tflag5);
               pstmt6.setInt(48, g1guest_id1);
               pstmt6.setInt(49, g1guest_id2);
               pstmt6.setInt(50, g1guest_id3);
               pstmt6.setInt(51, g1guest_id4);
               pstmt6.setInt(52, g1guest_id5);

               pstmt6.setLong(53, date);
               pstmt6.setInt(54, time);
               pstmt6.setInt(55, afb);
               pstmt6.setString(56, course);

               count = pstmt6.executeUpdate();      // execute the prepared stmt

               pstmt6.close();

               //
               //  Do next group, if there is one
               //
               if (groups > 1 && count != 0) {

                  time = atime2;    // time for this tee time
                  hndcp1 = 99;      // init
                  hndcp2 = 99;
                  hndcp3 = 99;
                  hndcp4 = 99;
                  hndcp5 = 99;
                  mNum1 = "";
                  mNum2 = "";
                  mNum3 = "";
                  mNum4 = "";
                  mNum5 = "";

                  if (p5.equals( "Yes" )) {

                     g2player1 = player6;
                     g2player2 = player7;
                     g2player3 = player8;
                     g2player4 = player9;
                     g2player5 = player10;
                     g2user1 = user6;
                     g2user2 = user7;
                     g2user3 = user8;
                     g2user4 = user9;
                     g2user5 = user10;
                     g2p1cw = p6cw;
                     g2p2cw = p7cw;
                     g2p3cw = p8cw;
                     g2p4cw = p9cw;
                     g2p5cw = p10cw;
                     g2userg1 = userg6;
                     g2userg2 = userg7;
                     g2userg3 = userg8;
                     g2userg4 = userg9;
                     g2userg5 = userg10;
                     g2p91 = p96;
                     g2p92 = p97;
                     g2p93 = p98;
                     g2p94 = p99;
                     g2p95 = p910;
                     g2guest_id1 = guest_id6;
                     g2guest_id2 = guest_id7;
                     g2guest_id3 = guest_id8;
                     g2guest_id4 = guest_id9;
                     g2guest_id5 = guest_id10;

                  } else {

                     g2player1 = player5;
                     g2player2 = player6;
                     g2player3 = player7;
                     g2player4 = player8;
                     g2user1 = user5;
                     g2user2 = user6;
                     g2user3 = user7;
                     g2user4 = user8;
                     g2p1cw = p5cw;
                     g2p2cw = p6cw;
                     g2p3cw = p7cw;
                     g2p4cw = p8cw;
                     g2userg1 = userg5;
                     g2userg2 = userg6;
                     g2userg3 = userg7;
                     g2userg4 = userg8;
                     g2p91 = p95;
                     g2p92 = p96;
                     g2p93 = p97;
                     g2p94 = p98;
                     g2guest_id1 = guest_id5;
                     g2guest_id2 = guest_id6;
                     g2guest_id3 = guest_id7;
                     g2guest_id4 = guest_id8;
                  }

                  if (!g2user1.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g2user1);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum1 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g2user2.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g2user2);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum2 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g2user3.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g2user3);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum3 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g2user4.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g2user4);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum4 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g2user5.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g2user5);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum5 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int
                  }

                  if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                     mNum1 = "";                  // convert back to null
                  }
                  if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                     mNum2 = "";                  // convert back to null
                  }
                  if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                     mNum3 = "";                  // convert back to null
                  }
                  if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                     mNum4 = "";                  // convert back to null
                  }
                  if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                     mNum5 = "";                  // convert back to null
                  }

                  //
                  //  Before we update the tee time, go check for any flags to be added to members' names for the pro tee sheet
                  //
                  slotParms.user1 = g2user1;
                  slotParms.user2 = g2user2;
                  slotParms.user3 = g2user3;
                  slotParms.user4 = g2user4;
                  slotParms.user5 = g2user5;

                  verifySlot.checkTFlag(slotParms, con);    // check for tflags if any members in tee time

               
                  errorMsg = "Error in SystemUtils moveReqs (put group 2 in tee sheet): ";

                  //
                  //  Update the tee slot in teecurr
                  //
                  //  Clear the lottery name so this tee time is displayed in _sheet even though there
                  //  may be some requests still outstanding (state = 4).
                  //
                  pstmt6 = con.prepareStatement (
                     "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                     "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                     "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                     "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " + // lottery = '',
                     "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, lottery_email = 1, " +
                     "tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, " +
                     "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ? " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt6.clearParameters();        // clear the parms
                  pstmt6.setString(1, g2player1);
                  pstmt6.setString(2, g2player2);
                  pstmt6.setString(3, g2player3);
                  pstmt6.setString(4, g2player4);
                  pstmt6.setString(5, g2user1);
                  pstmt6.setString(6, g2user2);
                  pstmt6.setString(7, g2user3);
                  pstmt6.setString(8, g2user4);
                  pstmt6.setString(9, g2p1cw);
                  pstmt6.setString(10, g2p2cw);
                  pstmt6.setString(11, g2p3cw);
                  pstmt6.setString(12, g2p4cw);
                  pstmt6.setFloat(13, hndcp1);
                  pstmt6.setFloat(14, hndcp2);
                  pstmt6.setFloat(15, hndcp3);
                  pstmt6.setFloat(16, hndcp4);
                  pstmt6.setString(17, g2player5);
                  pstmt6.setString(18, g2user5);
                  pstmt6.setString(19, g2p5cw);
                  pstmt6.setFloat(20, hndcp5);
                  pstmt6.setString(21, notes);
                  pstmt6.setInt(22, hide);
                  pstmt6.setInt(23, proNew);
                  pstmt6.setInt(24, proMod);
                  pstmt6.setInt(25, memNew);
                  pstmt6.setInt(26, memMod);
                  pstmt6.setString(27, mNum1);
                  pstmt6.setString(28, mNum2);
                  pstmt6.setString(29, mNum3);
                  pstmt6.setString(30, mNum4);
                  pstmt6.setString(31, mNum5);
                  pstmt6.setString(32, g2userg1);
                  pstmt6.setString(33, g2userg2);
                  pstmt6.setString(34, g2userg3);
                  pstmt6.setString(35, g2userg4);
                  pstmt6.setString(36, g2userg5);
                  pstmt6.setString(37, orig_by);
                  pstmt6.setInt(38, g2p91);
                  pstmt6.setInt(39, g2p92);
                  pstmt6.setInt(40, g2p93);
                  pstmt6.setInt(41, g2p94);
                  pstmt6.setInt(42, g2p95);
                  pstmt6.setString(43, slotParms.tflag1);
                  pstmt6.setString(44, slotParms.tflag2);
                  pstmt6.setString(45, slotParms.tflag3);
                  pstmt6.setString(46, slotParms.tflag4);
                  pstmt6.setString(47, slotParms.tflag5);
                  pstmt6.setInt(48, g2guest_id1);
                  pstmt6.setInt(49, g2guest_id2);
                  pstmt6.setInt(50, g2guest_id3);
                  pstmt6.setInt(51, g2guest_id4);
                  pstmt6.setInt(52, g2guest_id5);

                  pstmt6.setLong(53, date);
                  pstmt6.setInt(54, time);
                  pstmt6.setInt(55, afb2);
                  pstmt6.setString(56, course);

                  count = pstmt6.executeUpdate();      // execute the prepared stmt

                  pstmt6.close();

               }    // end of IF groups

               //
               //  Do next group, if there is one
               //
               if (groups > 2 && count != 0) {

                  time = atime3;    // time for this tee time
                  hndcp1 = 99;      // init
                  hndcp2 = 99;
                  hndcp3 = 99;
                  hndcp4 = 99;
                  hndcp5 = 99;
                  mNum1 = "";
                  mNum2 = "";
                  mNum3 = "";
                  mNum4 = "";
                  mNum5 = "";

                  if (p5.equals( "Yes" )) {

                     g3player1 = player11;
                     g3player2 = player12;
                     g3player3 = player13;
                     g3player4 = player14;
                     g3player5 = player15;
                     g3user1 = user11;
                     g3user2 = user12;
                     g3user3 = user13;
                     g3user4 = user14;
                     g3user5 = user15;
                     g3p1cw = p11cw;
                     g3p2cw = p12cw;
                     g3p3cw = p13cw;
                     g3p4cw = p14cw;
                     g3p5cw = p15cw;
                     g3userg1 = userg11;
                     g3userg2 = userg12;
                     g3userg3 = userg13;
                     g3userg4 = userg14;
                     g3userg5 = userg15;
                     g3p91 = p911;
                     g3p92 = p912;
                     g3p93 = p913;
                     g3p94 = p914;
                     g3p95 = p915;
                     g3guest_id1 = guest_id11;
                     g3guest_id2 = guest_id12;
                     g3guest_id3 = guest_id13;
                     g3guest_id4 = guest_id14;
                     g3guest_id5 = guest_id15;

                  } else {

                     g3player1 = player9;
                     g3player2 = player10;
                     g3player3 = player11;
                     g3player4 = player12;
                     g3user1 = user9;
                     g3user2 = user10;
                     g3user3 = user11;
                     g3user4 = user12;
                     g3p1cw = p9cw;
                     g3p2cw = p10cw;
                     g3p3cw = p11cw;
                     g3p4cw = p12cw;
                     g3userg1 = userg9;
                     g3userg2 = userg10;
                     g3userg3 = userg11;
                     g3userg4 = userg12;
                     g3p91 = p99;
                     g3p92 = p910;
                     g3p93 = p911;
                     g3p94 = p912;
                     g3guest_id1 = guest_id9;
                     g3guest_id2 = guest_id10;
                     g3guest_id3 = guest_id11;
                     g3guest_id4 = guest_id12;
                  }

                  if (!g3user1.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g3user1);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum1 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g3user2.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g3user2);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum2 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g3user3.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g3user3);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum3 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g3user4.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g3user4);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum4 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (p5.equals( "Yes" )) {

                     if (!g3user5.equals( "" )) {        // if player is a member

                        parm = SystemUtils.getUser(con, g3user5);     // get mNum and hndcp for member

                        StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                        mNum5 = tok.nextToken();         // member Number
                        hndcps = tok.nextToken();       // handicap

                        hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int
                     }
                  }

                  if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                     mNum1 = "";                  // convert back to null
                  }
                  if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                     mNum2 = "";                  // convert back to null
                  }
                  if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                     mNum3 = "";                  // convert back to null
                  }
                  if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                     mNum4 = "";                  // convert back to null
                  }
                  if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                     mNum5 = "";                  // convert back to null
                  }

                  
                  //
                  //  Before we update the tee time, go check for any flags to be added to members' names for the pro tee sheet
                  //
                  slotParms.user1 = g3user1;
                  slotParms.user2 = g3user2;
                  slotParms.user3 = g3user3;
                  slotParms.user4 = g3user4;
                  slotParms.user5 = g3user5;

                  verifySlot.checkTFlag(slotParms, con);    // check for tflags if any members in tee time

               
                  errorMsg = "Error in SystemUtils moveReqs (put group 3 in tee sheet): ";

                  //
                  //  Update the tee slot in teecurr
                  //
                  //  Clear the lottery name so this tee time is displayed in _sheet even though there
                  //  may be some requests still outstanding (state = 4).
                  //
                  pstmt6 = con.prepareStatement (
                     "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                     "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                     "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                     "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " + // lottery = '',
                     "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, lottery_email = 1, " +
                     "tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, " +
                     "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ? " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt6.clearParameters();        // clear the parms
                  pstmt6.setString(1, g3player1);
                  pstmt6.setString(2, g3player2);
                  pstmt6.setString(3, g3player3);
                  pstmt6.setString(4, g3player4);
                  pstmt6.setString(5, g3user1);
                  pstmt6.setString(6, g3user2);
                  pstmt6.setString(7, g3user3);
                  pstmt6.setString(8, g3user4);
                  pstmt6.setString(9, g3p1cw);
                  pstmt6.setString(10, g3p2cw);
                  pstmt6.setString(11, g3p3cw);
                  pstmt6.setString(12, g3p4cw);
                  pstmt6.setFloat(13, hndcp1);
                  pstmt6.setFloat(14, hndcp2);
                  pstmt6.setFloat(15, hndcp3);
                  pstmt6.setFloat(16, hndcp4);
                  pstmt6.setString(17, g3player5);
                  pstmt6.setString(18, g3user5);
                  pstmt6.setString(19, g3p5cw);
                  pstmt6.setFloat(20, hndcp5);
                  pstmt6.setString(21, notes);
                  pstmt6.setInt(22, hide);
                  pstmt6.setInt(23, proNew);
                  pstmt6.setInt(24, proMod);
                  pstmt6.setInt(25, memNew);
                  pstmt6.setInt(26, memMod);
                  pstmt6.setString(27, mNum1);
                  pstmt6.setString(28, mNum2);
                  pstmt6.setString(29, mNum3);
                  pstmt6.setString(30, mNum4);
                  pstmt6.setString(31, mNum5);
                  pstmt6.setString(32, g3userg1);
                  pstmt6.setString(33, g3userg2);
                  pstmt6.setString(34, g3userg3);
                  pstmt6.setString(35, g3userg4);
                  pstmt6.setString(36, g3userg5);
                  pstmt6.setString(37, orig_by);
                  pstmt6.setInt(38, g3p91);
                  pstmt6.setInt(39, g3p92);
                  pstmt6.setInt(40, g3p93);
                  pstmt6.setInt(41, g3p94);
                  pstmt6.setInt(42, g3p95);
                  pstmt6.setString(43, slotParms.tflag1);
                  pstmt6.setString(44, slotParms.tflag2);
                  pstmt6.setString(45, slotParms.tflag3);
                  pstmt6.setString(46, slotParms.tflag4);
                  pstmt6.setString(47, slotParms.tflag5);
                  pstmt6.setInt(48, g3guest_id1);
                  pstmt6.setInt(49, g3guest_id2);
                  pstmt6.setInt(50, g3guest_id3);
                  pstmt6.setInt(51, g3guest_id4);
                  pstmt6.setInt(52, g3guest_id5);

                  pstmt6.setLong(53, date);
                  pstmt6.setInt(54, time);
                  pstmt6.setInt(55, afb3);
                  pstmt6.setString(56, course);

                  count = pstmt6.executeUpdate();      // execute the prepared stmt

                  pstmt6.close();

               }    // end of IF groups

               //
               //  Do next group, if there is one
               //
               if (groups > 3 && count != 0) {

                  time = atime4;    // time for this tee time
                  hndcp1 = 99;      // init
                  hndcp2 = 99;
                  hndcp3 = 99;
                  hndcp4 = 99;
                  hndcp5 = 99;
                  mNum1 = "";
                  mNum2 = "";
                  mNum3 = "";
                  mNum4 = "";
                  mNum5 = "";

                  if (p5.equals( "Yes" )) {

                     g4player1 = player16;
                     g4player2 = player17;
                     g4player3 = player18;
                     g4player4 = player19;
                     g4player5 = player20;
                     g4user1 = user16;
                     g4user2 = user17;
                     g4user3 = user18;
                     g4user4 = user19;
                     g4user5 = user20;
                     g4p1cw = p16cw;
                     g4p2cw = p17cw;
                     g4p3cw = p18cw;
                     g4p4cw = p19cw;
                     g4p5cw = p20cw;
                     g4userg1 = userg16;
                     g4userg2 = userg17;
                     g4userg3 = userg18;
                     g4userg4 = userg19;
                     g4userg5 = userg20;
                     g4p91 = p916;
                     g4p92 = p917;
                     g4p93 = p918;
                     g4p94 = p919;
                     g4p95 = p920;
                     g4guest_id1 = guest_id16;
                     g4guest_id2 = guest_id17;
                     g4guest_id3 = guest_id18;
                     g4guest_id4 = guest_id19;
                     g4guest_id5 = guest_id20;

                  } else {

                     g4player1 = player13;
                     g4player2 = player14;
                     g4player3 = player15;
                     g4player4 = player16;
                     g4user1 = user13;
                     g4user2 = user14;
                     g4user3 = user15;
                     g4user4 = user16;
                     g4p1cw = p13cw;
                     g4p2cw = p14cw;
                     g4p3cw = p15cw;
                     g4p4cw = p16cw;
                     g4userg1 = userg13;
                     g4userg2 = userg14;
                     g4userg3 = userg15;
                     g4userg4 = userg16;
                     g4p91 = p913;
                     g4p92 = p914;
                     g4p93 = p915;
                     g4p94 = p916;
                     g4guest_id1 = guest_id13;
                     g4guest_id2 = guest_id14;
                     g4guest_id3 = guest_id15;
                     g4guest_id4 = guest_id16;
                  }

                  if (!g4user1.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g4user1);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum1 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g4user2.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g4user2);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum2 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g4user3.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g4user3);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum3 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g4user4.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g4user4);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum4 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (p5.equals( "Yes" )) {

                     if (!g4user5.equals( "" )) {        // if player is a member

                        parm = SystemUtils.getUser(con, g4user5);     // get mNum and hndcp for member

                        StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                        mNum5 = tok.nextToken();         // member Number
                        hndcps = tok.nextToken();       // handicap

                        hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int
                     }
                  }

                  if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                     mNum1 = "";                  // convert back to null
                  }
                  if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                     mNum2 = "";                  // convert back to null
                  }
                  if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                     mNum3 = "";                  // convert back to null
                  }
                  if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                     mNum4 = "";                  // convert back to null
                  }
                  if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                     mNum5 = "";                  // convert back to null
                  }

                  
                  //
                  //  Before we update the tee time, go check for any flags to be added to members' names for the pro tee sheet
                  //
                  slotParms.user1 = g4user1;
                  slotParms.user2 = g4user2;
                  slotParms.user3 = g4user3;
                  slotParms.user4 = g4user4;
                  slotParms.user5 = g4user5;

                  verifySlot.checkTFlag(slotParms, con);    // check for tflags if any members in tee time

               
                  errorMsg = "Error in SystemUtils moveReqs (put group 4 in tee sheet): ";

                  //
                  //  Update the tee slot in teecurr
                  //
                  //  Clear the lottery name so this tee time is displayed in _sheet even though there
                  //  may be some requests still outstanding (state = 4).
                  //
                  pstmt6 = con.prepareStatement (
                     "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                     "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                     "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                     "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " + // lottery = '',
                     "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, lottery_email = 1, " +
                     "tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, " +
                     "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ? " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt6.clearParameters();        // clear the parms
                  pstmt6.setString(1, g4player1);
                  pstmt6.setString(2, g4player2);
                  pstmt6.setString(3, g4player3);
                  pstmt6.setString(4, g4player4);
                  pstmt6.setString(5, g4user1);
                  pstmt6.setString(6, g4user2);
                  pstmt6.setString(7, g4user3);
                  pstmt6.setString(8, g4user4);
                  pstmt6.setString(9, g4p1cw);
                  pstmt6.setString(10, g4p2cw);
                  pstmt6.setString(11, g4p3cw);
                  pstmt6.setString(12, g4p4cw);
                  pstmt6.setFloat(13, hndcp1);
                  pstmt6.setFloat(14, hndcp2);
                  pstmt6.setFloat(15, hndcp3);
                  pstmt6.setFloat(16, hndcp4);
                  pstmt6.setString(17, g4player5);
                  pstmt6.setString(18, g4user5);
                  pstmt6.setString(19, g4p5cw);
                  pstmt6.setFloat(20, hndcp5);
                  pstmt6.setString(21, notes);
                  pstmt6.setInt(22, hide);
                  pstmt6.setInt(23, proNew);
                  pstmt6.setInt(24, proMod);
                  pstmt6.setInt(25, memNew);
                  pstmt6.setInt(26, memMod);
                  pstmt6.setString(27, mNum1);
                  pstmt6.setString(28, mNum2);
                  pstmt6.setString(29, mNum3);
                  pstmt6.setString(30, mNum4);
                  pstmt6.setString(31, mNum5);
                  pstmt6.setString(32, g4userg1);
                  pstmt6.setString(33, g4userg2);
                  pstmt6.setString(34, g4userg3);
                  pstmt6.setString(35, g4userg4);
                  pstmt6.setString(36, g4userg5);
                  pstmt6.setString(37, orig_by);
                  pstmt6.setInt(38, g4p91);
                  pstmt6.setInt(39, g4p92);
                  pstmt6.setInt(40, g4p93);
                  pstmt6.setInt(41, g4p94);
                  pstmt6.setInt(42, g4p95);
                  pstmt6.setString(43, slotParms.tflag1);
                  pstmt6.setString(44, slotParms.tflag2);
                  pstmt6.setString(45, slotParms.tflag3);
                  pstmt6.setString(46, slotParms.tflag4);
                  pstmt6.setString(47, slotParms.tflag5);
                  pstmt6.setInt(48, g4guest_id1);
                  pstmt6.setInt(49, g4guest_id2);
                  pstmt6.setInt(50, g4guest_id3);
                  pstmt6.setInt(51, g4guest_id4);
                  pstmt6.setInt(52, g4guest_id5);

                  pstmt6.setLong(53, date);
                  pstmt6.setInt(54, time);
                  pstmt6.setInt(55, afb4);
                  pstmt6.setString(56, course);

                  count = pstmt6.executeUpdate();      // execute the prepared stmt

                  pstmt6.close();

               }    // end of IF groups

               //
               //  Do next group, if there is one
               //
               if (groups > 4 && count != 0) {

                  time = atime5;    // time for this tee time
                  hndcp1 = 99;      // init
                  hndcp2 = 99;
                  hndcp3 = 99;
                  hndcp4 = 99;
                  hndcp5 = 99;
                  mNum1 = "";
                  mNum2 = "";
                  mNum3 = "";
                  mNum4 = "";
                  mNum5 = "";

                  if (p5.equals( "Yes" )) {

                     g5player1 = player21;
                     g5player2 = player22;
                     g5player3 = player23;
                     g5player4 = player24;
                     g5player5 = player25;
                     g5user1 = user21;
                     g5user2 = user22;
                     g5user3 = user23;
                     g5user4 = user24;
                     g5user5 = user25;
                     g5p1cw = p21cw;
                     g5p2cw = p22cw;
                     g5p3cw = p23cw;
                     g5p4cw = p24cw;
                     g5p5cw = p25cw;
                     g5userg1 = userg21;
                     g5userg2 = userg22;
                     g5userg3 = userg23;
                     g5userg4 = userg24;
                     g5userg5 = userg25;
                     g5p91 = p921;
                     g5p92 = p922;
                     g5p93 = p923;
                     g5p94 = p924;
                     g5p95 = p925;
                     g5guest_id1 = guest_id21;
                     g5guest_id2 = guest_id22;
                     g5guest_id3 = guest_id23;
                     g5guest_id4 = guest_id24;
                     g5guest_id5 = guest_id25;

                  } else {

                     g5player1 = player17;
                     g5player2 = player18;
                     g5player3 = player19;
                     g5player4 = player20;
                     g5user1 = user17;
                     g5user2 = user18;
                     g5user3 = user19;
                     g5user4 = user20;
                     g5p1cw = p17cw;
                     g5p2cw = p18cw;
                     g5p3cw = p19cw;
                     g5p4cw = p20cw;
                     g5userg1 = userg17;
                     g5userg2 = userg18;
                     g5userg3 = userg19;
                     g5userg4 = userg20;
                     g5p91 = p917;
                     g5p92 = p918;
                     g5p93 = p919;
                     g5p94 = p920;
                     g5guest_id1 = guest_id17;
                     g5guest_id2 = guest_id18;
                     g5guest_id3 = guest_id19;
                     g5guest_id4 = guest_id20;
                  }

                  if (!g5user1.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g5user1);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum1 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g5user2.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g5user2);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum2 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g5user3.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g5user3);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum3 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g5user4.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g5user4);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum4 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (p5.equals( "Yes" )) {

                     if (!g5user5.equals( "" )) {        // if player is a member

                        parm = SystemUtils.getUser(con, g5user5);     // get mNum and hndcp for member

                        StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                        mNum5 = tok.nextToken();         // member Number
                        hndcps = tok.nextToken();       // handicap

                        hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int
                     }
                  }

                  if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                     mNum1 = "";                  // convert back to null
                  }
                  if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                     mNum2 = "";                  // convert back to null
                  }
                  if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                     mNum3 = "";                  // convert back to null
                  }
                  if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                     mNum4 = "";                  // convert back to null
                  }
                  if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                     mNum5 = "";                  // convert back to null
                  }

                  
                  //
                  //  Before we update the tee time, go check for any flags to be added to members' names for the pro tee sheet
                  //
                  slotParms.user1 = g5user1;
                  slotParms.user2 = g5user2;
                  slotParms.user3 = g5user3;
                  slotParms.user4 = g5user4;
                  slotParms.user5 = g5user5;

                  verifySlot.checkTFlag(slotParms, con);    // check for tflags if any members in tee time

               
                  errorMsg = "Error in SystemUtils moveReqs (put group 5 in tee sheet): ";

                  //
                  //  Update the tee slot in teecurr
                  //
                  //  Clear the lottery name so this tee time is displayed in _sheet even though there
                  //  may be some requests still outstanding (state = 4).
                  //
                  pstmt6 = con.prepareStatement (
                     "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                     "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                     "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                     "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " + // lottery = '',
                     "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, lottery_email = 1, " +
                     "tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, " +
                     "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ? " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt6.clearParameters();        // clear the parms
                  pstmt6.setString(1, g5player1);
                  pstmt6.setString(2, g5player2);
                  pstmt6.setString(3, g5player3);
                  pstmt6.setString(4, g5player4);
                  pstmt6.setString(5, g5user1);
                  pstmt6.setString(6, g5user2);
                  pstmt6.setString(7, g5user3);
                  pstmt6.setString(8, g5user4);
                  pstmt6.setString(9, g5p1cw);
                  pstmt6.setString(10, g5p2cw);
                  pstmt6.setString(11, g5p3cw);
                  pstmt6.setString(12, g5p4cw);
                  pstmt6.setFloat(13, hndcp1);
                  pstmt6.setFloat(14, hndcp2);
                  pstmt6.setFloat(15, hndcp3);
                  pstmt6.setFloat(16, hndcp4);
                  pstmt6.setString(17, g5player5);
                  pstmt6.setString(18, g5user5);
                  pstmt6.setString(19, g5p5cw);
                  pstmt6.setFloat(20, hndcp5);
                  pstmt6.setString(21, notes);
                  pstmt6.setInt(22, hide);
                  pstmt6.setInt(23, proNew);
                  pstmt6.setInt(24, proMod);
                  pstmt6.setInt(25, memNew);
                  pstmt6.setInt(26, memMod);
                  pstmt6.setString(27, mNum1);
                  pstmt6.setString(28, mNum2);
                  pstmt6.setString(29, mNum3);
                  pstmt6.setString(30, mNum4);
                  pstmt6.setString(31, mNum5);
                  pstmt6.setString(32, g5userg1);
                  pstmt6.setString(33, g5userg2);
                  pstmt6.setString(34, g5userg3);
                  pstmt6.setString(35, g5userg4);
                  pstmt6.setString(36, g5userg5);
                  pstmt6.setString(37, orig_by);
                  pstmt6.setInt(38, g5p91);
                  pstmt6.setInt(39, g5p92);
                  pstmt6.setInt(40, g5p93);
                  pstmt6.setInt(41, g5p94);
                  pstmt6.setInt(42, g5p95);
                  pstmt6.setString(43, slotParms.tflag1);
                  pstmt6.setString(44, slotParms.tflag2);
                  pstmt6.setString(45, slotParms.tflag3);
                  pstmt6.setString(46, slotParms.tflag4);
                  pstmt6.setString(47, slotParms.tflag5);
                  pstmt6.setInt(48, g5guest_id1);
                  pstmt6.setInt(49, g5guest_id2);
                  pstmt6.setInt(50, g5guest_id3);
                  pstmt6.setInt(51, g5guest_id4);
                  pstmt6.setInt(52, g5guest_id5);

                  pstmt6.setLong(53, date);
                  pstmt6.setInt(54, time);
                  pstmt6.setInt(55, afb5);
                  pstmt6.setString(56, course);

                  count = pstmt6.executeUpdate();      // execute the prepared stmt

                  pstmt6.close();

               }    // end of IF groups

               
               if (count == 0) {
                   
                   // we were not able to update the tee time(s) to contain all requested times in the lottery
                  out.println(SystemUtils.HeadTitle("Error Converting Lottery Request"));
                  out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                  out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                  out.println("<center><BR><BR><H3>Database Access Error</H3>");
                  out.println("<BR><BR>Error Converting Lottery Request");
                  out.println("<BR>The assigned tee time was not found for this lottery request.  You may have to insert the assigned tee time and try again.");
                  out.println("<BR><BR>If problem persists, contact customer support.");
                  out.println("<BR><BR>");
                  out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"mode\" value=\"LOTT\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
                  out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
                  out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form>");
                  out.println("</center></BODY></HTML>");
                  out.close();
                  return;
                   
               } else {
               
                  /*
                   //
                   // delete the request after players have been moved
                   //
                   pstmtd = con.prepareStatement (
                            "DELETE FROM lreqs3 WHERE id = ?");

                   pstmtd.clearParameters();
                   pstmtd.setLong(1, id);
                   pstmtd.executeUpdate();
                   pstmtd.close();
                   */
                           
                  //
                  //  Change the state to 4 (processed but NOT approved) - wait until pro sends emails to delete !!!!! (changed 3/13/09)
                  //
                  pstmtd = con.prepareStatement (
                      "UPDATE lreqs3 SET state = 4 " +
                      "WHERE id = ?");

                  pstmtd.clearParameters();        // clear the parms
                  pstmtd.setLong(1, id);

                  pstmtd.executeUpdate();

                  pstmtd.close();

               }
               
               
               //
               //  If lottery type = Weighted By Proximity, determine time between request and assigned
               //
               if (type.equals( "WeightedBP" )) {
                 
                  proxMins = SystemUtils.calcProxTime(rtime, atime1);      // calculate mins difference
                    
                  pstmtd2 = con.prepareStatement (
                        "INSERT INTO lassigns5 (username, lname, date, mins) " +
                        "VALUES (?, ?, ?, ?)");

                  //
                  //  Save each members' weight for this request
                  //
                  for (i=0; i<25; i++) {          // check all 25 possible players

                     if (!userA[i].equals( "" )) {     // if player is a member

                        pstmtd2.clearParameters();
                        pstmtd2.setString(1, userA[i]);
                        pstmtd2.setString(2, name);
                        pstmtd2.setLong(3, date);
                        pstmtd2.setInt(4, proxMins);

                        pstmtd2.executeUpdate();
                     }
                  }
                  pstmtd2.close();

               }                // end of IF Weighted by Proximity lottery type

            } // end of IF ok (tee times in use?)
            else {

               // we were not able to update the tee time(s) to contain all requested times in the lottery
              out.println(SystemUtils.HeadTitle("Error Converting Lottery Request"));
              out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
              out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
              out.println("<center><BR><BR><H3>Tee Time Busy or Occupied</H3>");
              out.println("<BR><BR>Error Converting Lottery Request");
              out.println("<BR>The assigned tee time for this lottery request is either busy or is already occupied with players.");
              out.println("<BR>If the lottery request has multiple groups within, it could be one of the groups assigned times that are busy or occupied.");
              out.println("<BR><BR>");
              out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
              out.println("<input type=\"hidden\" name=\"mode\" value=\"LOTT\">");
              out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
              out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
              out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
              out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
              out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
              out.println("</form>");
              out.println("</center></BODY></HTML>");
              out.close();
              return;

            } // end if tee time busy or full

         } else {     // req is NOT assigned

            //
            //  Change the state to 4 (processed but NOT approved) - wait until pro sends emails to change to state 5 !!!!! (changed 3/13/09)
            //
            PreparedStatement pstmt7s = con.prepareStatement (
        //        "UPDATE lreqs3 SET state = 5 " +
                "UPDATE lreqs3 SET state = 4 " +
                "WHERE id = ?");

            pstmt7s.clearParameters();        // clear the parms
            pstmt7s.setLong(1, id);

            pstmt7s.executeUpdate();

            pstmt7s.close();

         }     // end of IF req is assigned

      }    // end of WHILE lreqs - process next request

      pstmt.close();

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e1.getMessage();
      SystemUtils.buildDatabaseErrMsg(e1.toString(), errorMsg, out, false); 
      return;
   }
   

    //
    // Completed update - reload page
    //
    out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_dsheet?mode=LOTT&index=" + index + "&course=" + returnCourse + "&name=" + name + "&hide="+hideUnavail+"\">");

    out.close();

 } // end auto_convert method
 
 
 private static String getTime(int time) {

    try {
        
        String ampm = "AM";
        int hr = time / 100;
        int min = time % (hr * 100);

        if (hr == 12) {
            ampm = "PM";
        } else if (hr > 12) {
            hr -= 12;
            ampm = "PM";
        }

        return hr + ":" + SystemUtils.ensureDoubleDigit(min) + " " + ampm;
        
    } catch (Exception ignore) {
        return "N/A";
    }
    
 }
 
 
 private void convertEventSgnUp(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {
     
    Statement stmt = null;
    ResultSet rs = null;

    //
    //  Get this session's attributes
    //
    String user = "";
    String club = "";
    user = (String)session.getAttribute("user");
    club = (String)session.getAttribute("club");

    int index = 0;
    int fives = 0;      // for tee time we are dragging to
    int count = 0;
    int event_id = 0;
    int teecurr_id = 0;
    boolean overRideFives = false;
    String blocker = "";

    String hideUnavail = req.getParameter("hide");
    if (hideUnavail == null) hideUnavail = "";
    String sindex = req.getParameter("index");          //  day index value (needed by _sheet on return)
    String returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
    String suppressEmails = "no";
    
    String slid = "";
    String stid = "";
    String sgroup = "";
    String limit = "";
    if (req.getParameter("to_tid") != null) stid = req.getParameter("to_tid");
    if (req.getParameter("eventId") != null) slid = req.getParameter("eventId");
    if (req.getParameter("group") != null) sgroup = req.getParameter("group");
    if (req.getParameter("limit") != null) limit = req.getParameter("limit");
    
    String convert = req.getParameter("convert");
    if (convert == null) convert = "";
    
    String name = req.getParameter("name");
    if (name == null) name = "";
    
    if (req.getParameter("overRideFives") != null && req.getParameter("overRideFives").equals("yes")) {
        overRideFives = true;
    }
    
    if (req.getParameter("suppressEmails") != null) {             // if email parm exists
        suppressEmails = req.getParameter("suppressEmails");
    }
    
    //
    //  parm block to hold the tee time parms
    //
    parmSlot slotParms = new parmSlot();          // allocate a parm block
    
    //
    //  Convert the values from string to int
    //
    try {

        event_id = Integer.parseInt(slid);
        teecurr_id = Integer.parseInt(stid);
        index = Integer.parseInt(sindex);
    }
    catch (NumberFormatException exp) {
        SystemUtils.buildDatabaseErrMsg(exp.toString(), "Input variables", out, false); 
        return;
    }
    
    //
    // Get fives value for this course (from teecurr_id)
    //
    int group_size = 0;
    int p9 = 0;
    
    try {

        PreparedStatement pstmtc = con.prepareStatement (
            "SELECT fives, " +
            "(SELECT size FROM events2b WHERE name = ?) AS group_size, " + 
            "(SELECT holes FROM events2b WHERE name = ?) AS holes " + 
            "FROM clubparm2 c, teecurr2 t " + 
            "WHERE c.courseName = t.courseName AND t.teecurr_id = ?");

        pstmtc.clearParameters();
        pstmtc.setString(1, name);
        pstmtc.setString(2, name);
        pstmtc.setInt(3, teecurr_id);
        rs = pstmtc.executeQuery();

        if (rs.next()) {
            fives = rs.getInt("fives");
            group_size = rs.getInt("group_size");
            p9 = (rs.getInt("holes") == 18) ? 0 : 1;
        }

        pstmtc.close();
    }
    catch (Exception e) {
        SystemUtils.buildDatabaseErrMsg(e.toString(), e.getMessage(), out, false); 
        return;
    }
        
    if (overRideFives) fives = 1;
    
    slotParms.ind = index;                      // index value
    slotParms.club = club;                      // name of club
    slotParms.returnCourse = returnCourse;      // name of course for return to _sheet
    slotParms.suppressEmails = suppressEmails;
    
    //
    //  Load parameter object
    //
    try {

        PreparedStatement pstmt = con.prepareStatement("SELECT * FROM teecurr2 WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, teecurr_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            slotParms.player1 = rs.getString("player1");
            slotParms.player2 = rs.getString("player2");
            slotParms.player3 = rs.getString("player3");
            slotParms.player4 = rs.getString("player4");
            slotParms.player5 = rs.getString("player5");
            slotParms.user1 = rs.getString("username1");
            slotParms.user2 = rs.getString("username2");
            slotParms.user3 = rs.getString("username3");
            slotParms.user4 = rs.getString("username4");
            slotParms.user5 = rs.getString("username5");
            slotParms.p1cw = rs.getString("p1cw");
            slotParms.p2cw = rs.getString("p2cw");
            slotParms.p3cw = rs.getString("p3cw");
            slotParms.p4cw = rs.getString("p4cw");
            slotParms.p5cw = rs.getString("p5cw");
            slotParms.p91 = rs.getInt("p91");
            slotParms.p92 = rs.getInt("p92");
            slotParms.p93 = rs.getInt("p93");
            slotParms.p94 = rs.getInt("p94");
            slotParms.p95 = rs.getInt("p95");
            slotParms.in_use = rs.getInt("in_use");
            slotParms.in_use_by = rs.getString("in_use_by");
            slotParms.userg1 = rs.getString("userg1");
            slotParms.userg2 = rs.getString("userg2");
            slotParms.userg3 = rs.getString("userg3");
            slotParms.userg4 = rs.getString("userg4");
            slotParms.userg5 = rs.getString("userg5");
            slotParms.orig_by = rs.getString("orig_by");
            slotParms.pos1 = rs.getShort("pos1");
            slotParms.pos2 = rs.getShort("pos2");
            slotParms.pos3 = rs.getShort("pos3");
            slotParms.pos4 = rs.getShort("pos4");
            slotParms.pos5 = rs.getShort("pos5");
            slotParms.rest5 = rs.getString("rest5");
            slotParms.guest_id1 = rs.getInt("guest_id1");
            slotParms.guest_id2 = rs.getInt("guest_id2");
            slotParms.guest_id3 = rs.getInt("guest_id3");
            slotParms.guest_id4 = rs.getInt("guest_id4");
            slotParms.guest_id5 = rs.getInt("guest_id5");
            
            blocker = rs.getString("blocker");
            
        }
        out.println("<!-- DONE LOADING slotParms WITH teecurr2 DATA -->");
        pstmt.close();

    }
    catch (Exception e) {

        out.println("<p>Error: "+e.toString()+"</p>");
    }

    // make sure there are enough open player slots
    int max_group_size = (fives == 0 || slotParms.p5.equalsIgnoreCase("No")) ? 4 : 5;
    int open_slots = 0;
    boolean has_players = false;
    if (slotParms.player1.equals("")) { open_slots++; } else { has_players = true; }
    if (slotParms.player2.equals("")) open_slots++;
    if (slotParms.player3.equals("")) open_slots++;
    if (slotParms.player4.equals("")) open_slots++;
    if (slotParms.player5.equals("") && fives == 1) open_slots++; // && slotParms.rest5.equals("") && slotParms.p5.equalsIgnoreCase("Yes")
    
    if (slotParms.orig_by.equals( "" )) {    // if originator field still empty (allow this person to grab this tee time again)
    
        slotParms.orig_by = user;             // set this user as the originator
    }
  
    out.println("<!-- open_slots="+open_slots+" | has_players="+has_players+" -->");
    
    //
    // Check in-use indicators
    //
    if (slotParms.in_use == 1 && !slotParms.in_use_by.equalsIgnoreCase( user )) {    // if time slot in use and not by this user
    
        out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<CENTER><BR><BR><H1>Reservation Timer Expired</H1>");
        out.println("<BR><BR>Sorry, but this tee time slot has been returned to the system!<BR>");
        out.println("<BR>The system timed out and released the tee time.");
        out.println("<BR><BR>");

        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        } else {
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
        }
        out.println("</form></font>");
            
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    //
    // Check blocker indicator
    //
    if (!blocker.equals("")) {    // if time slot is blocked
    
        out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<CENTER><BR><BR><H1>Tee Time Blocked</H1>");
        out.println("<BR><BR>Sorry, but this tee time slot you've selected is blocked.<BR>");
        out.println("<BR>Please choose a different time or unblock the desired time.");
        out.println("<BR><BR>");

        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"EVENT\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"submit\" value=\"Try Again\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
            
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    boolean teeTimeFull = false;
    boolean allowFives = (fives == 1) ? true : false; // does the course we are dragging to allow 5-somes?
    
    String notes = "";
    int hideNotes = 0;
    
    String fields = "";
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
    String userg1 = "";
    String userg2 = "";
    String userg3 = "";
    String userg4 = "";
    String userg5 = "";
    String memNum1 = "";
    String memNum2 = "";
    String memNum3 = "";
    String memNum4 = "";
    String memNum5 = "";
    
    int group_players = 0; // # of players added to a group
    int tmp_added = 0;
    int players = 0;
    int wait = 0;
    int guest_id1 = 0;
    int guest_id2 = 0;
    int guest_id3 = 0;
    int guest_id4 = 0;
    int guest_id5 = 0;
    
    //
    // Load Event Signup data
    //
    try {
        
        PreparedStatement pstmt = con.prepareStatement ("" +
                "SELECT e.*, " +
                "IF(player1 = '', 0, IF(player2 = '', 1, IF(player3 = '', 2, IF(player4 = '', 3, IF(player5 = '', 4, 5))))) AS players " +
                "FROM evntsup2b e " +
                "WHERE name = ? AND id = ?;");
        
        pstmt.clearParameters();
        pstmt.setString(1, name);
        pstmt.setInt(2, event_id);
        
        rs = pstmt.executeQuery();
        
        if ( rs.next() ) {
            
            players = rs.getInt("players");
            notes = rs.getString("notes");
            hideNotes = rs.getInt("hideNotes");
            
            wait = rs.getInt("wait");
            
            // if orig_by wasn't set from teecurr2 (it was if the teetime already had players)
            //if (slotParms.orig_by.equals("")) slotParms.orig_by = rs.getString("orig_by");
            
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            
            user1 = rs.getString("username1");
            user2 = rs.getString("username2");
            user3 = rs.getString("username3");
            user4 = rs.getString("username4");
            user5 = rs.getString("username5");
            
            p1cw = rs.getString("p1cw");
            p2cw = rs.getString("p2cw");
            p3cw = rs.getString("p3cw");
            p4cw = rs.getString("p4cw");
            p5cw = rs.getString("p5cw");
            
            userg1 = rs.getString("userg1");
            userg2 = rs.getString("userg2");
            userg3 = rs.getString("userg3");
            userg4 = rs.getString("userg4");
            userg5 = rs.getString("userg5");

            guest_id1 = rs.getInt("guest_id1");
            guest_id2 = rs.getInt("guest_id2");
            guest_id3 = rs.getInt("guest_id3");
            guest_id4 = rs.getInt("guest_id4");
            guest_id5 = rs.getInt("guest_id5");
        }
        
        pstmt.close();
        
        if (players > open_slots) {
            
            out.println(SystemUtils.HeadTitle("Not Enough Open Player Slots"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<CENTER><BR><BR><H1>Not Enough Open Player Positions</H1>");
            out.println("<BR><BR>Sorry, but this tee time slot you've selected does not have enough open positions<BR>");
            out.println("<BR>for the event signup you tried to move.  You tried to move " +players+" on to a tee time with only " + open_slots + " open player positions available.");
            out.println("<BR>Please choose another tee time and try again.");
            out.println("<BR><BR>");

            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("<input type=\"hidden\" name=\"mode\" value=\"EVENT\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
            if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
            } else {
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
            }
            out.println("</form></font>");

            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
            
        }
        
        if (!player1.equals("")) {
            group_players++;
            memNum1 = getMemberNumber(user1, con);
            teeTimeFull = addPlayer(slotParms, player1, user1, memNum1, p1cw, userg1, p9, guest_id1, allowFives);
            if (!teeTimeFull) { tmp_added++; }
        }
        
        if (!player2.equals("")) {
            group_players++;
            memNum2 = getMemberNumber(user2, con);
            teeTimeFull = addPlayer(slotParms, player2, user2, memNum2, p2cw, userg2, p9, guest_id2, allowFives);
            if (!teeTimeFull) { tmp_added++; }
        }
        
        if (!player3.equals("")) {
            group_players++;
            memNum3 = getMemberNumber(user3, con);
            teeTimeFull = addPlayer(slotParms, player3, user3, memNum3, p3cw, userg3, p9, guest_id3, allowFives);
            if (!teeTimeFull) { tmp_added++; }
        }
        
        if (!player4.equals("")) {
            group_players++;
            memNum4 = getMemberNumber(user4, con);
            teeTimeFull = addPlayer(slotParms, player4, user4, memNum4, p4cw, userg4, p9, guest_id4, allowFives);
            if (!teeTimeFull) { tmp_added++; }
        }
        
        if (!player5.equals("") && allowFives && group_size == 5) {
            group_players++;
            memNum5 = getMemberNumber(user5, con);
            teeTimeFull = addPlayer(slotParms, player5, user5, memNum5, p5cw, userg5, p9, guest_id5, allowFives);
            if (!teeTimeFull) { tmp_added++; }
        }
        
    } catch(Exception exp) {
        SystemUtils.buildDatabaseErrMsg(exp.toString(), "LOAD DATA", out, false);
        //return;
    }
    
    
    // debug
    out.println("<!-- max_group_size=" + max_group_size + " -->");
    out.println("<!-- group_size=" + group_size + " | group_players=" + group_players + " -->");
    out.println("<!-- event_id="+event_id+" | teecurr_id="+teecurr_id+" | tmp_added="+tmp_added+" -->");
    out.println("<!-- teeTimeFull=" + teeTimeFull + " -->");
    out.println("<!-- allowFives=" + allowFives + " -->");
    
    // Let see if all players from this request where moved
    if ( teeTimeFull || tmp_added == 0 ) {
    
        out.println(SystemUtils.HeadTitle("Unable to Add All Players"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Unable to Add All Players</H3><BR>");
        out.println("<BR>Sorry we were not able to add all the players to this tee time.<br><br>");
        out.println("<BR><BR>No changes were made to the event signup or tee sheet.");
        out.println("<BR><BR>");
       
        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"EVENT\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"submit\" value=\"Try Again\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
        
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
    
    
    // first lets see if they are trying to fill the 5th player slot when it is unavailable for this course
    if ( !slotParms.player5.equals("") && fives == 0 ) { // ( (!slotParms.rest5.equals("") && overRideFives == false) || fives == 0)
    
        out.println(SystemUtils.HeadTitle("5-some Restricted - Reject"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Member Restricted</H3><BR>");
        out.println("<BR>Sorry, <b>5-somes</b> are not allowed on this course.<br><br>");
        out.println("<BR><BR>Please move the event signup to another course.");
        out.println("<BR><BR>");
       
        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"EVENT\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" +name+ "\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hideUnavail + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"submit\" value=\"Try Again\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
        
        /*
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"EVENT\">");
        out.println("<input type=\"hidden\" name=\"convert\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"overRideFives\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"lotteryId\" value=\"" + lottery_id + "\">");
        out.println("<input type=\"hidden\" name=\"to_tid\" value=\"" + teecurr_id + "\">");
        out.println("<input type=\"hidden\" name=\"group\" value=\"" + group + "\">");
        out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
        */
        
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
        
    } // end 5-some rejection
    
    
    //
    //  Before we update the tee time, go check for any flags to be added to members' names for the pro tee sheet
    //
    if (!slotParms.user1.equals("") || !slotParms.user2.equals("") || !slotParms.user3.equals("") || 
        !slotParms.user4.equals("") || !slotParms.user5.equals("")) {
       
       verifySlot.checkTFlag(slotParms, con);    // check for tflags if any members in tee time
    }
         
         
    int send_email = 2; // default
    if (club.equals("medinahcc") && wait == 1) send_email = 0;
    
    //
    // Update entry in teecurr2
    //
    try {

        PreparedStatement pstmt6 = con.prepareStatement (
             "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
             "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
             "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
             "hndcp4 = ?, player5 = ?, username5 = ?, " + 
             "p5cw = ?, hndcp5 = ?, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " +
             "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
             "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, conf = ?, " +
             "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, " + 
             "pos1 = ?, pos2 = ?, pos3 = ?, pos4 = ?, pos5 = ?, lottery_email = ?, " + // was = 1
             "notes = ?, hideNotes = ?, tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, " +
             "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ? " +
             "WHERE teecurr_id = ?");

        pstmt6.clearParameters();
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
        pstmt6.setString(17, slotParms.player5);
        pstmt6.setString(18, slotParms.user5);
        pstmt6.setString(19, slotParms.p5cw);
        pstmt6.setFloat(20, slotParms.hndcp5);
        pstmt6.setString(21, slotParms.notes);
        pstmt6.setInt(22, 0); // hide
        pstmt6.setInt(23, 0); // proNew
        pstmt6.setInt(24, 0); // proMod
        pstmt6.setString(25, slotParms.mNum1);
        pstmt6.setString(26, slotParms.mNum2);
        pstmt6.setString(27, slotParms.mNum3);
        pstmt6.setString(28, slotParms.mNum4);
        pstmt6.setString(29, slotParms.mNum5);
        pstmt6.setString(30, slotParms.userg1);
        pstmt6.setString(31, slotParms.userg2);
        pstmt6.setString(32, slotParms.userg3);
        pstmt6.setString(33, slotParms.userg4);
        pstmt6.setString(34, slotParms.userg5);
        pstmt6.setString(35, slotParms.orig_by);
        pstmt6.setString(36, slotParms.conf);
        pstmt6.setInt(37, slotParms.p91);
        pstmt6.setInt(38, slotParms.p92);
        pstmt6.setInt(39, slotParms.p93);
        pstmt6.setInt(40, slotParms.p94);
        pstmt6.setInt(41, slotParms.p95);
        pstmt6.setInt(42, slotParms.pos1);
        pstmt6.setInt(43, slotParms.pos2);
        pstmt6.setInt(44, slotParms.pos3);
        pstmt6.setInt(45, slotParms.pos4);
        pstmt6.setInt(46, slotParms.pos5);
        pstmt6.setInt(47, send_email);
        pstmt6.setString(48, notes);
        pstmt6.setInt(49, hideNotes);
        pstmt6.setString(50, slotParms.tflag1);
        pstmt6.setString(51, slotParms.tflag2);
        pstmt6.setString(52, slotParms.tflag3);
        pstmt6.setString(53, slotParms.tflag4);
        pstmt6.setString(54, slotParms.tflag5);
        pstmt6.setInt(55, slotParms.guest_id1);
        pstmt6.setInt(56, slotParms.guest_id2);
        pstmt6.setInt(57, slotParms.guest_id3);
        pstmt6.setInt(58, slotParms.guest_id4);
        pstmt6.setInt(59, slotParms.guest_id5);

        pstmt6.setInt(60, teecurr_id);

        count = pstmt6.executeUpdate();      // execute the prepared stmt
        
        pstmt6.close();
        
    } 
    catch (Exception exp) {
        
        SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
        return;
    }
    
    // if the tee time was updated then remove the request
    if (count == 1) {
                    
        try {

            PreparedStatement pstmt = con.prepareStatement("UPDATE evntsup2b SET moved = 1 WHERE name = ? AND id = ?");
            pstmt.clearParameters();
            pstmt.setString(1, name);
            pstmt.setInt(2, event_id);
            pstmt.executeUpdate();
            pstmt.close();

        } catch (Exception exp) {
            SystemUtils.buildDatabaseErrMsg(exp.toString(), exp.getMessage(), out, false); 
            return;
        }
        
    } // end if updated teecurr2 entry


    out.println("<!-- count="+count+" -->");
    
    out.println("<!-- ");
    out.println("slotParms.player1=" + slotParms.player1);
    out.println("slotParms.player2=" + slotParms.player2);
    out.println("slotParms.player3=" + slotParms.player3);
    out.println("slotParms.player4=" + slotParms.player4);
    out.println("slotParms.player5=" + slotParms.player5);
    out.println("");
    out.println("slotParms.p1cw=" + slotParms.p1cw);
    out.println("slotParms.p2cw=" + slotParms.p2cw);
    out.println("slotParms.p3cw=" + slotParms.p3cw);
    out.println("slotParms.p4cw=" + slotParms.p4cw);
    out.println("slotParms.p5cw=" + slotParms.p5cw);
    out.println("");
    out.println("slotParms.p91=" + slotParms.p91);
    out.println("slotParms.p92=" + slotParms.p92);
    out.println("slotParms.p93=" + slotParms.p93);
    out.println("slotParms.p94=" + slotParms.p94);
    out.println("slotParms.p95=" + slotParms.p95);
    out.println("");
    out.println("slotParms.mNum1=" + slotParms.mNum1);
    out.println("slotParms.mNum2=" + slotParms.mNum2);
    out.println("slotParms.mNum3=" + slotParms.mNum3);
    out.println("slotParms.mNum4=" + slotParms.mNum4);
    out.println("slotParms.mNum5=" + slotParms.mNum5);
    out.println("");
    out.println(" -->");


    //
    // Completed update - reload page
    //
    out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_dsheet?mode=EVENT&index=" + slotParms.ind + "&course=" + slotParms.returnCourse + "&name=" + name + "&hide="+hideUnavail+"&limit=" + limit + "&jump=777\">");

    out.close();
    return;
 }
 
 
 
 //
 //   Lottery approval is done - set lottery state to 5 so tee sheet will open up
 //
 private void setLotteryState5(String clubName, String club, String lott_name, int lott_date, int index, PrintWriter out, Connection con) {

    PreparedStatement pstmtd = null;
    ResultSet rs = null;
    
    String lott_type = "";

    
    try {

       //
       //   Get the lottery type
       //
       pstmtd = con.prepareStatement ("" +
            "SELECT type " +
            "FROM lottery3 " +
            "WHERE name = ?;");

       pstmtd.clearParameters();
       pstmtd.setString(1, lott_name);

       rs = pstmtd.executeQuery();  

       if (rs.next()) {

           lott_type = rs.getString("type");
       }
       pstmtd.close();    
             
    }
    catch (Exception exp) { 
        SystemUtils.buildDatabaseErrMsg("Proshop_dsheet.setLotteryState5 - SQL Exception checking type. Club = " +clubName+ ". Error = ", exp.toString(), out, false);
    } finally {
    }
                
    
    //
    //  Delete all lreqs that have been assigned, set unassigned to state 5
    //
    try {
       
       if (lott_type.equals("Proshop")) {    // if Proshop lottery - delete the requests that have been moved (atime1 is never set for these)
          
          pstmtd = con.prepareStatement (
                   "DELETE FROM lreqs3 WHERE name = ? AND date = ? AND state = 4");

          pstmtd.clearParameters();
          pstmtd.setString(1, lott_name);
          pstmtd.setLong(2, lott_date);
          pstmtd.executeUpdate();
          pstmtd.close();
          
       } else {
       
          pstmtd = con.prepareStatement (
                   "DELETE FROM lreqs3 WHERE name = ? AND date = ? AND atime1 > 0 AND state = 4");

          pstmtd.clearParameters();
          pstmtd.setString(1, lott_name);
          pstmtd.setLong(2, lott_date);
          pstmtd.executeUpdate();
          pstmtd.close();

          pstmtd = con.prepareStatement (
                   "UPDATE lreqs3 SET state = 5 WHERE name = ? AND date = ? AND state = 4");

          pstmtd.clearParameters();
          pstmtd.setString(1, lott_name);
          pstmtd.setLong(2, lott_date);
          pstmtd.executeUpdate();
          pstmtd.close();    
       }
    
    }
    catch (Exception exp) { 
        SystemUtils.buildDatabaseErrMsg("Proshop_dsheet.setLotteryState5 - SQL Exception. Club = " +clubName+ ". Error = ", exp.toString(), out, false);
    } finally {
        out.println("<!-- setLotteryState5 complete -->");
    }
                
    
    //
    //  Custom for Ramsey - once lottery processed, move the wait list.  The wait list is only used during lottery signup.
    //
    if (club.equals("ramseycountryclub")) {
                 
       //
       //  Move the start date of the wait list so members can access the tee times during this lottery.
       //
       boolean found = false;
       
       String wait_name = "Lottery Single/Double Wait List";    // name of wait list to modify
       String sdatetime = "";
       String stime = "";
       
       int start_yy = lott_date / 10000;                        // get the date of this lottery being approved
       int start_mm = (lott_date - (start_yy * 10000)) / 100;
       int start_dd = (lott_date - (start_yy * 10000)) - (start_mm * 100);
       
       try {
          
          //
          //   Get the start time of the wait list so we don't change that
          //
          pstmtd = con.prepareStatement ("" +
               "SELECT *, " +
                    "DATE_FORMAT(sdatetime, '%l:%i %p') AS stime " +
               "FROM wait_list " +
               "WHERE name = ?;");

          pstmtd.clearParameters();
          pstmtd.setString(1, wait_name);

          rs = pstmtd.executeQuery();  

          while (rs.next()) {

               stime = rs.getString("stime");
               
               found = true;          // wait list exists
          }
          pstmtd.close();    
          
          if (found == true) {        // if wait list exists then change the date
             
             Calendar cal = new GregorianCalendar();            // get todays date
             
             cal.set(Calendar.YEAR,start_yy);                    // change to date of lottery that user just processed
             cal.set(Calendar.MONTH,start_mm-1);               
             cal.set(Calendar.DAY_OF_MONTH,start_dd);            

             cal.add(Calendar.DATE,1);                           // bump to next day
                         
             start_yy = cal.get(Calendar.YEAR);                  // get that date
             start_mm = cal.get(Calendar.MONTH) +1;
             start_dd = cal.get(Calendar.DAY_OF_MONTH);
          
             sdatetime = "" + start_yy + "-" + start_mm + "-" + start_dd + " " + stime;   // build new sdate stime value

             //
             //   Move the start date of the waitlist
             //
             pstmtd = con.prepareStatement (
                      "UPDATE wait_list SET sdatetime = ? WHERE name = ?");

             pstmtd.clearParameters();
             pstmtd.setString(1, sdatetime);
             pstmtd.setString(2, wait_name);
             pstmtd.executeUpdate();
             pstmtd.close(); 
          }

       }
       catch (Exception exp) { 
           SystemUtils.buildDatabaseErrMsg("Proshop_dsheet.setLotteryState5 error in Custom. Club = " +clubName+ ". Error = ", exp.toString(), out, false);
       } finally {
           out.println("<!-- setLotteryState5 Custom complete -->");
       }
    }           // end of Ramsey custom
                
 }     // end of setLotteryState5
 
 
 
 private void sendLotteryEmails(String clubName, String lott_name, int lott_date, int index, String course, String returnCourse, PrintWriter out, Connection con) {

    out.println("<center>");
    out.println("<h2>Sending Emails...</h2>");
    out.println("<p>THIS MAY TAKE SEVERAL MINUTES TO COMPLETE.<br><br>DO NOT CLICK YOUR BROWSERS BACK BUTTON!</p>");
    out.println("</center>");
     
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    String errorMsg = "";
    String courseName = "";
    String day = "";

    int time = 0;
    int atime1 = 0;
    int dd = 0;
    int mm = 0;
    int yy = 0;
    int afb = 0;
    int count1 = 0;
    int count2 = 0;

    String user1 = "";
    String user2 = "";
    String user3 = "";
    String user4 = "";
    String user5 = "";

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

    String p1cw = "";
    String p2cw = "";
    String p3cw = "";
    String p4cw = "";
    String p5cw = "";

    String lotteryText = "";
    String event = "";

    int guest_id1 = 0;
    int guest_id2 = 0;
    int guest_id3 = 0;
    int guest_id4 = 0;
    int guest_id5 = 0;
    
    int teecurr_id = 0;
    int lott_email = 0;
    int type = 0;
    
    //
    //  Get today's date and time for email processing
    //
    Calendar ecal = new GregorianCalendar();               // get todays date
    int eyear = ecal.get(Calendar.YEAR);
    int emonth = ecal.get(Calendar.MONTH);
    int eday = ecal.get(Calendar.DAY_OF_MONTH);
    int e_hourDay = ecal.get(Calendar.HOUR_OF_DAY);
    int e_min = ecal.get(Calendar.MINUTE);
    
    int e_time = 0;
    long e_date = 0;

    //
    //  Get replacement text for "Lottery" to be used in email notifications - if club has requested a replacement
    //
    lotteryText = getClub.getLotteryText(con);         // get replacement text for "Lottery" if provided  
        
    //
    //   Adjust the time based on the club's time zone (we are Central)
    //
    e_time = SystemUtils.adjustTime(con, (e_hourDay * 100) + e_min);
    
    if (e_time < 0) {          // if negative, then we went back or ahead one day
        
        e_time = 0 - e_time;        // convert back to positive value
        
        //
        // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
        //
        ecal.add(Calendar.DATE,( (e_time < 1200) ? 1 : -1) );                     // get next day's date

        eyear = ecal.get(Calendar.YEAR);
        emonth = ecal.get(Calendar.MONTH);
        eday = ecal.get(Calendar.DAY_OF_MONTH);
    
    }
    
    emonth++;                            // month starts at zero
    int e_hour = e_time / 100;           // get adjusted hour
    e_min = e_time - (e_hour * 100);     // get minute value
    int e_am_pm = 0;                     // preset to AM
    
    if (e_hour > 11) {
    
        e_am_pm = 1;        // PM
        e_hour -= 12;       // set to 12 hr clock
    }
    
    if (e_hour == 0) e_hour = 12;
    
    //
    //  Build the 'time' string for display
    //
    e_date = (eyear * 10000) + (emonth * 100) + eday;

    //
    //  get date/time string for email message
    //
    // String email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":" + SystemUtils.ensureDoubleDigit(e_min) + ((e_am_pm == 0) ? " AM" : " PM");


            
    //*****************************************************************************
    //  Send an email to all players that are part of this lottery and have not
    //  yet been sent an email.
    //*****************************************************************************
    //
    try {

        pstmt = con.prepareStatement ("" +
            "SELECT * " +
            "FROM teecurr2 " +
            "WHERE date = ? AND lottery = ? AND lottery_email = 1;");

        pstmt.clearParameters();
        pstmt.setInt(1, lott_date);
        pstmt.setString(2, lott_name);


        rs = pstmt.executeQuery();  
      
        while (rs.next()) {
          
            count1++; // inc the tee time count
            
            teecurr_id = rs.getInt("teecurr_id");
            courseName = rs.getString("courseName");
            day = rs.getString("day");
            dd = rs.getInt("dd");
            mm = rs.getInt("mm");
            yy = rs.getInt("yy");
            afb = rs.getInt("fb");
            atime1 = rs.getInt("time");
            lott_email = rs.getInt("lottery_email");

            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            
            user1 = rs.getString("username1");
            user2 = rs.getString("username2");
            user3 = rs.getString("username3");
            user4 = rs.getString("username4");
            user5 = rs.getString("username5");
            
            p1cw = rs.getString("p1cw");
            p2cw = rs.getString("p2cw");
            p3cw = rs.getString("p3cw");
            p4cw = rs.getString("p4cw");
            p5cw = rs.getString("p5cw");

            event = rs.getString("event");
            type = rs.getInt("event_type");          
            
            guest_id1 = rs.getInt("guest_id1");
            guest_id2 = rs.getInt("guest_id2");
            guest_id3 = rs.getInt("guest_id3");
            guest_id4 = rs.getInt("guest_id4");
            guest_id5 = rs.getInt("guest_id5");
            
               
            //
            //***********************************************
            //  Send email notification if necessary
            //***********************************************
            //
            String to = "";                          // to address
            String f_b = "";
            String eampm = "";
            String etime = "";
            String enewMsg = "";
            int emailOpt = 0;                        // user's email option parm
            int ehr = 0;
            int emin = 0;
            int send = 0;

            PreparedStatement pstmte1 = null;

            //
            //  set the front/back value
            //
            f_b = "Front";

            if (afb == 1) {

            f_b = "Back";
            }

            String enew1 = "";
            //String enew2 = "";
            String subject = "";
            String action = (lott_email == 1) ? "ASSIGNED" : "MODIFIED";

            if (clubName.startsWith( "Old Oaks" )) {

                enew1 = "The following Tee Time has been " + action + ".\n\n";
                //enew2 = "The following Tee Times have been ASSIGNED.\n\n";
                subject = "ForeTees Tee Time Assignment Notification";

            } else if (clubName.startsWith( "Westchester" )) {

                    enew1 = "The following Draw Tee Time has been " + action + ".\n\n";
                    //enew2 = "The following Draw Tee Times have been ASSIGNED.\n\n";
                    subject = "Your Tee Time for Weekend Draw";

            } else if (clubName.startsWith( "Pecan Plantation" )) {

                    enew1 = "The following Tee Time Request has been " + action + ".\n\n";
                    subject = "ForeTees Tee Time Assignment Notification";

            } else if (!lotteryText.equals( "" )) {      // if replacement text provided
            
               enew1 = "The following " +lotteryText+ " has been " + action + ".\n\n";
               subject = "ForeTees " +lotteryText+ " Assignment Notification";
            
            } else {

                enew1 = "The following Lottery Tee Time has been " + action + ".\n\n";
                //enew2 = "The following Lottery Tee Times have been ASSIGNED.\n\n";
                subject = "ForeTees Lottery Assignment Notification";
            }

            if (!clubName.equals( "" )) {

                subject = subject + " - " + clubName;
            }

            Properties properties = new Properties();
            properties.put("mail.smtp.host", SystemUtils.host);                      // set outbound host address
            properties.put("mail.smtp.port", SystemUtils.port);                      // set outbound port
            properties.put("mail.smtp.auth", "true");                    // set 'use authentication'

            Session mailSess = Session.getInstance(properties, SystemUtils.getAuthenticator());   // get session properties

            MimeMessage message = new MimeMessage(mailSess);
            
            try {

                message.setFrom(new InternetAddress(SystemUtils.EFROM));                               // set from addr

                message.setSubject( subject );                                            // set subject line
                message.setSentDate(new java.util.Date());                                // set date/time sent
            }
            catch (Exception ignore) { }

            //
            //  Set the recipient addresses
            //
            if (!user1.equals( "" )) {       // if new user exist and not same as old usernames

               try {
                  pstmte1 = con.prepareStatement (
                           "SELECT email, emailOpt FROM member2b WHERE username = ?");

                  pstmte1.clearParameters();        // clear the parms
                  pstmte1.setString(1, user1);
                  rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     to = rs2.getString(1);        // user's email address
                     emailOpt = rs2.getInt(2);        // email option

                     if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                        send = 1;
                        count2++;
                     }
                  }
                  pstmte1.close();              // close the stmt
               }
               catch (Exception ignore) { }
            }

            if (!user2.equals( "" )) {       // if new user exist and not same as old usernames

               try {
                  pstmte1 = con.prepareStatement (
                           "SELECT email, emailOpt FROM member2b WHERE username = ?");

                  pstmte1.clearParameters();        // clear the parms
                  pstmte1.setString(1, user2);
                  rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     to = rs2.getString(1);        // user's email address
                     emailOpt = rs2.getInt(2);        // email option

                     if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                        send = 1;
                        count2++;
                     }
                  }
                  pstmte1.close();              // close the stmt
               }
               catch (Exception ignore) {
               }
            }

            if (!user3.equals( "" )) {       // if new user exist and not same as old usernames

               try {
                  pstmte1 = con.prepareStatement (
                           "SELECT email, emailOpt FROM member2b WHERE username = ?");

                  pstmte1.clearParameters();        // clear the parms
                  pstmte1.setString(1, user3);
                  rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     to = rs2.getString(1);        // user's email address
                     emailOpt = rs2.getInt(2);        // email option

                     if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                        send = 1;
                        count2++;
                     }
                  }
                  pstmte1.close();              // close the stmt
               }
               catch (Exception ignore) {
               }
            }

            if (!user4.equals( "" )) {       // if new user exist and not same as old usernames

               try {
                  pstmte1 = con.prepareStatement (
                           "SELECT email, emailOpt FROM member2b WHERE username = ?");

                  pstmte1.clearParameters();        // clear the parms
                  pstmte1.setString(1, user4);
                  rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     to = rs2.getString(1);        // user's email address
                     emailOpt = rs2.getInt(2);        // email option

                     if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                        send = 1;
                        count2++;
                     }
                  }
                  pstmte1.close();              // close the stmt
               }
               catch (Exception ignore) {
               }
            }

            if (!user5.equals( "" )) {       // if new user exist and not same as old usernames

               try {
                  pstmte1 = con.prepareStatement (
                           "SELECT email, emailOpt FROM member2b WHERE username = ?");

                  pstmte1.clearParameters();        // clear the parms
                  pstmte1.setString(1, user5);
                  rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     to = rs2.getString(1);        // user's email address
                     emailOpt = rs2.getInt(2);        // email option

                     if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                        send = 1;
                        count2++;
                     }
                  }
                  pstmte1.close();              // close the stmt
               }
               catch (Exception ignore) {
               }
            }
               
           
            //
            //  send email if anyone to send it to
            //
            if (send != 0) {        // if any email addresses specified for members

               enewMsg = SystemUtils.header + enew1 + day + " " + mm + "/" + dd + "/" + yy + " on the " + f_b + " tee";
                    
               if (!courseName.equals( "" )) enewMsg = enewMsg + " of Course: " + courseName;
                
               //
               //  If tee time is during a shotgun event, then change this to an event email
               //
               if (!event.equals( "" ) && type == 1) {

                  int act_hr = 0;
                  int act_min = 0;

                  String act_ampm = "";
                  String act_time = "";

                  try {

                     //
                     //   Get the parms for this event
                     //
                     PreparedStatement pstmtev = con.prepareStatement (
                        "SELECT act_hr, act_min FROM events2b " +
                        "WHERE name = ?");

                     pstmtev.clearParameters();        // clear the parms
                     pstmtev.setString(1, event);
                     rs2 = pstmtev.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        act_hr = rs2.getInt("act_hr");
                        act_min = rs2.getInt("act_min");
                     }
                     pstmtev.close();

                  } catch (Exception ignore) { }

                  //
                  //  Create time value for email msg
                  //
                  act_ampm = " AM";

                  if (act_hr == 0) {

                     act_hr = 12;                 // change to 12 AM (midnight)

                  } else {

                     if (act_hr == 12) {

                        act_ampm = " PM";         // change to Noon
                     }
                  }
                  if (act_hr > 12) {

                     act_hr = act_hr - 12;
                     act_ampm = " PM";             // change to 12 hr clock
                  }

                  //
                  //  convert time to hour and minutes for email msg
                  //
                  act_time = act_hr + ":" + SystemUtils.ensureDoubleDigit(act_min) + act_ampm;

                  enewMsg = enewMsg + " at " + act_time + " (Shotgun)\n";

               } else {       // normal tee time
                  
                   //
                   //  convert time to hour and minutes for email msg
                   //
                   time = atime1;              // time for this tee time
                   ehr = time / 100;
                   emin = time - (ehr * 100);
                   eampm = " AM";
                   if (ehr > 12) {

                       eampm = " PM";
                       ehr = ehr - 12;       // convert from military time
                   }
                   if (ehr == 12) eampm = " PM";
                   if (ehr == 0) {

                       ehr = 12;
                       eampm = " AM";
                   }

                   etime = ehr + ":" + SystemUtils.ensureDoubleDigit(emin) + eampm;

                   enewMsg = enewMsg + " at " + etime + "\n";

               } // end if shotgun event or normal tee time
                
                
                if (!player1.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 1:  " + player1 + "  " + p1cw;
                }
                if (!player2.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 2:  " + player2 + "  " + p2cw;
                }
                if (!player3.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 3:  " + player3 + "  " + p3cw;
                }
                if (!player4.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 4:  " + player4 + "  " + p4cw;
                }
                if (!player5.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 5:  " + player5 + "  " + p5cw;
                }

                enewMsg = enewMsg + SystemUtils.trailer;

                try {

                    message.setText( enewMsg );  // put msg in email text area
                    Transport.send(message);     // send it!!
                }
                catch (Exception exp) { 
                    teecurr_id = 0; // reset so that we can detect that we didn't send email
                    SystemUtils.buildDatabaseErrMsg("Can't build message.", exp.toString(), out, false);
                }
                
            } // end of IF send

            if (teecurr_id > 0) {

                try {

                    PreparedStatement pstmt6 = con.prepareStatement (
                         "UPDATE teecurr2 " +
                         "SET lottery_email = 0, lottery = '' " +
                         "WHERE teecurr_id = ?");

                    pstmt6.clearParameters();
                    pstmt6.setInt(1, teecurr_id);
                    pstmt6.executeUpdate();
                    
                    pstmt6.close();

                } catch (Exception exp) {
                    SystemUtils.buildDatabaseErrMsg("Error updating tee time entry.", exp.toString(), out, false);
                }

            } // end if
            
        } // end while loop for teecurr2

        pstmt.close();
    }
    catch (Exception exp) {
        SystemUtils.buildDatabaseErrMsg("Fatal error in Proshop_dsheet.sendLotteryEmails - sending emails.", exp.toString(), out, false);
    }

    //
    //  Pro should be done processing this lottery - remove the lottery from all tee times for this day and course(s) so any extra tee times show on sheet
    //
    try {
       
       String whereString = "";
       
       if (course.equals("") || course.equals("-ALL-")) {
          
          whereString = "date = ? AND lottery = ?";
          
       } else {
          
          whereString = "date = ? AND lottery = ? AND courseName = ?";
       }
    
        pstmt = con.prepareStatement ("" +
           "UPDATE teecurr2 " +
           "SET lottery = '' " +
           "WHERE " +whereString+ ";");

        pstmt.clearParameters();
        pstmt.setInt(1, lott_date);
        pstmt.setString(2, lott_name);
        
        if (!course.equals("") && !course.equals("-ALL-")) {
            pstmt.setString(3, course);
        }

        pstmt.executeUpdate();
    
        pstmt.close();
        
    }
    catch (Exception exp) {
        SystemUtils.buildDatabaseErrMsg("Fatal error in Proshop_dsheet.sendLotteryEmails - removing lottery name from teecurr.", exp.toString(), out, false);
    }

      
    out.println("<center>");
    
    out.println("<h3>Done!</h3>");
    out.println("<p>We sent " + count2 + " emails for " + count1 + " tee times.</p>");
    //out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +index+ "&course=" +returnCourse+ "\" title=\"Return to Tee Sheet\" alt=\"Return\">Back To Tee Sheet</a>");
    
    out.println("<form method=get action=/" + rev + "/servlet/Proshop_jump>");
    out.println("<input type=hidden name=index value=\"" + index + "\">");
    out.println("<input type=hidden name=course value=\"" + returnCourse + "\">");
    out.println("<input type=submit value=\"Continue\">");
    out.println("</form>");
    
    out.println("</center>");

 }

 
 private void sendEventEmails(String clubName, String event_name, int event_date, int index, String returnCourse, PrintWriter out, Connection con) {

    out.println("<center>");
    out.println("<h2>Sending Emails...</h2>");
    out.println("<p>THIS MAY TAKE SEVERAL MINUTES TO COMPLETE.<br><br>DO NOT CLICK YOUR BROWSERS BACK BUTTON!</p>");
    out.println("</center>");
     
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    String errorMsg = "";
    String course = "";
    String day = "";

    int time = 0;
    int atime1 = 0;
    int dd = 0;
    int mm = 0;
    int yy = 0;
    int afb = 0;
    int count1 = 0; // tee times that we tried to send emails to
    int count2 = 0; // number of members we found email addresses for (recipients)
    int count3 = 0; // number of actual email messages we sent to the email server

    String user1 = "";
    String user2 = "";
    String user3 = "";
    String user4 = "";
    String user5 = "";

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

    String p1cw = "";
    String p2cw = "";
    String p3cw = "";
    String p4cw = "";
    String p5cw = "";
    
    String hole = "";

    int guest_id1 = 0;
    int guest_id2 = 0;
    int guest_id3 = 0;
    int guest_id4 = 0;
    int guest_id5 = 0;
    
    int teecurr_id = 0;
    int lott_email = 0;
    int event_type = 0;
    int act_hr = 0;
    int act_min = 0;
    
    //
    //  Get today's date and time for email processing
    //
    Calendar ecal = new GregorianCalendar();               // get todays date
    int eyear = ecal.get(Calendar.YEAR);
    int emonth = ecal.get(Calendar.MONTH);
    int eday = ecal.get(Calendar.DAY_OF_MONTH);
    int e_hourDay = ecal.get(Calendar.HOUR_OF_DAY);
    int e_min = ecal.get(Calendar.MINUTE);
    
    int e_time = 0;
    long e_date = 0;
    
    //
    //   Adjust the time based on the club's time zone (we are Central)
    //
    e_time = SystemUtils.adjustTime(con, (e_hourDay * 100) + e_min);
    
    if (e_time < 0) {          // if negative, then we went back or ahead one day
        
        e_time = 0 - e_time;        // convert back to positive value
        
        //
        // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
        //
        ecal.add(Calendar.DATE,( (e_time < 1200) ? 1 : -1) );                     // get next day's date

        eyear = ecal.get(Calendar.YEAR);
        emonth = ecal.get(Calendar.MONTH);
        eday = ecal.get(Calendar.DAY_OF_MONTH);
    
    }
    
    emonth++;                            // month starts at zero
    int e_hour = e_time / 100;           // get adjusted hour
    e_min = e_time - (e_hour * 100);     // get minute value
    int e_am_pm = 0;                     // preset to AM
    
    if (e_hour > 11) {
    
        e_am_pm = 1;        // PM
        e_hour -= 12;       // set to 12 hr clock
    }
    
    if (e_hour == 0) e_hour = 12;
    
    //
    //  Build the 'time' string for display
    //
    e_date = (eyear * 10000) + (emonth * 100) + eday;

    //
    //  get date/time string for email message
    //
    // String email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":" + SystemUtils.ensureDoubleDigit(e_min) + ((e_am_pm == 0) ? " AM" : " PM");


            
    //*****************************************************************************
    //  Send an email to all players the are part of this event and have not
    //  yet been sent an email.
    //*****************************************************************************
    //
/*    
    out.println("<br>event_name=" + event_name);
    out.println("<br>event_date=" + event_date);
    out.println("<br>clubName=" + clubName);
    out.println("<br>email_time=" + email_time);
*/    
    try {

        pstmt = con.prepareStatement ("" +
            "SELECT t.*, e.act_hr, e.act_min " +
            "FROM teecurr2 t, events2b e " +
            "WHERE t.date = ? AND t.lottery_email = 2 AND e.name = t.event"); // use the lottery email flag for the events (update, this may not work if they simultaneously work on an event and lottery for the same day at the same time)
            
        pstmt.clearParameters();
        pstmt.setInt(1, event_date);
        //pstmt.setString(2, lott_name);AND lottery = ?


        rs = pstmt.executeQuery();  
      
        while (rs.next()) {
          
            count1++; // inc the tee time count
            
            teecurr_id = rs.getInt("teecurr_id");
            course = rs.getString("courseName");
            day = rs.getString("day");
            dd = rs.getInt("dd");
            mm = rs.getInt("mm");
            yy = rs.getInt("yy");
            afb = rs.getInt("fb");
            atime1 = rs.getInt("time");
            lott_email = rs.getInt("lottery_email");
            event_type = rs.getInt("event_type");
            act_hr = rs.getInt("act_hr");
            act_min = rs.getInt("act_min");

            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            
            user1 = rs.getString("username1");
            user2 = rs.getString("username2");
            user3 = rs.getString("username3");
            user4 = rs.getString("username4");
            user5 = rs.getString("username5");
            
            p1cw = rs.getString("p1cw");
            p2cw = rs.getString("p2cw");
            p3cw = rs.getString("p3cw");
            p4cw = rs.getString("p4cw");
            p5cw = rs.getString("p5cw");

            guest_id1 = rs.getInt("guest_id1");
            guest_id2 = rs.getInt("guest_id2");
            guest_id3 = rs.getInt("guest_id3");
            guest_id4 = rs.getInt("guest_id4");
            guest_id5 = rs.getInt("guest_id5");
            
            hole = rs.getString("hole");
               
            //
            //***********************************************
            //  Send email notification if necessary
            //***********************************************
            //
            String to = "";                          // to address
            String f_b = "";
            String eampm = "";
            String etime = "";
            String enewMsg = "";
            int emailOpt = 0;                        // user's email option parm
            int ehr = 0;
            int emin = 0;
            int send = 0;

            PreparedStatement pstmte1 = null;

            //
            //  set the front/back value
            //
            f_b = "Front";

            if (afb == 1) {

            f_b = "Back";
            }

            String enew1 = "";
            //String enew2 = "";
            String subject = "";
            String action = (lott_email == 1) ? "ASSIGNED" : "MODIFIED";

            if (clubName.startsWith( "Old Oaks" )) {

                enew1 = "The following Tee Time has been " + action + ".\n\n";
                //enew2 = "The following Tee Times have been ASSIGNED.\n\n";
                subject = "ForeTees Tee Time Assignment Notification";

            } else if (clubName.startsWith( "Westchester" )) {

                    enew1 = "The following Draw Tee Time has been " + action + ".\n\n";
                    //enew2 = "The following Draw Tee Times have been ASSIGNED.\n\n";
                    subject = "Your Tee Time for Weekend Draw";

            } else {

                enew1 = "The following Event Tee Time has been " + action + ".\n\n";
                //enew2 = "The following Event Tee Times have been ASSIGNED.\n\n";
                subject = "ForeTees Event Assignment Notification";
            }

            if (!clubName.equals( "" )) {

                subject = subject + " - " + clubName;
            }

            Properties properties = new Properties();
            properties.put("mail.smtp.host", SystemUtils.host);                     // set outbound host address
            properties.put("mail.smtp.port", SystemUtils.port);                     // set outbound port
            properties.put("mail.smtp.auth", "true");                               // set 'use authentication'

            Session mailSess = Session.getInstance(properties, SystemUtils.getAuthenticator());   // get session properties

            MimeMessage message = new MimeMessage(mailSess);
            
            try {

                message.setFrom(new InternetAddress(SystemUtils.EFROM));                  // set from addr
                message.setSubject( subject );                                            // set subject line
                message.setSentDate(new java.util.Date());                                // set date/time sent
            }
            catch (Exception exp) { 
                out.println("<p>Can't create message object. " + exp.toString() + "</p>");
            }


            //
            //  Set the recipient addresses
            //
               if (!user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ? AND email_bounced = 0");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                           count2++;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) { }
               }

               if (!user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ? AND email_bounced = 0");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                           count2++;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ? AND email_bounced = 0");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                           count2++;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ? AND email_bounced = 0");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                           count2++;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ? AND email_bounced = 0");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                           count2++;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               

            //
            //  send email if anyone to send it to
            //
            if (send != 0) {        // if any email addresses specified for members
                
                count3++; // inc the message count
                
                if (event_type == 1) {
                    
                    enewMsg = SystemUtils.header + enew1 + day + " " + mm + "/" + dd + "/" + yy + " ";
                    if (!course.equals( "" )) enewMsg = enewMsg + "\nCourse: " + course;
                    enewMsg = enewMsg + "\nThe start time of the event is " + SystemUtils.getSimpleTime(act_hr, act_min) + ".";
                    if (!hole.equals("")) enewMsg = enewMsg + "\nYour assigned starting hole is " + hole + ".";
                    
                } else {
                    
                    enewMsg = SystemUtils.header + enew1 + day + " " + mm + "/" + dd + "/" + yy + " on the " + f_b + " tee ";
                    if (!course.equals( "" )) enewMsg = enewMsg + "of Course: " + course;
                                
                    //
                    //  convert time to hour and minutes for email msg
                    //
                    time = atime1;              // time for this tee time
                    ehr = time / 100;
                    emin = time - (ehr * 100);
                    eampm = " AM";
                    if (ehr > 12) {

                        eampm = " PM";
                        ehr = ehr - 12;       // convert from military time
                    }
                    if (ehr == 12) eampm = " PM";
                    if (ehr == 0) {

                        ehr = 12;
                        eampm = " AM";
                    }

                    etime = ehr + ":" + SystemUtils.ensureDoubleDigit(emin) + eampm;

                    enewMsg = enewMsg + "\n at " + etime;

                }
                
                enewMsg = enewMsg + "\n";
                
                if (!player1.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 1: " + player1 + "  " + p1cw;
                }
                if (!player2.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 2: " + player2 + "  " + p2cw;
                }
                if (!player3.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 3: " + player3 + "  " + p3cw;
                }
                if (!player4.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 4: " + player4 + "  " + p4cw;
                }
                if (!player5.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 5: " + player5 + "  " + p5cw;
                }

                enewMsg = enewMsg + SystemUtils.trailer;

                try {

                    message.setText( enewMsg );  // put msg in email text area
                    //out.println("<p>MESSAGE:<br>" + enewMsg + "</p>");
                    Transport.send(message);     // send it!!
                }
                catch (Exception exp) { 
                    teecurr_id = 0; // reset so that we can detect that we didn't send email
                    count3--;
                    SystemUtils.buildDatabaseErrMsg("Can't build message. " + exp.getMessage() + "<br>", exp.toString(), out, false);
                }
                
            }/* else {
             out.println("<p>We did not send emails because no players had email addresses.</p>");   
            }// end of IF send */

            if (teecurr_id > 0) {

                try {

                    PreparedStatement pstmt6 = con.prepareStatement (
                         "UPDATE teecurr2 " +
                         "SET lottery_email = 0 " +
                         "WHERE teecurr_id = ?");

                    pstmt6.clearParameters();
                    pstmt6.setInt(1, teecurr_id);
                    pstmt6.executeUpdate();
                    
                    pstmt6.close();

                } catch (Exception exp) {
                    SystemUtils.buildDatabaseErrMsg("Error updating tee time entry.", exp.toString(), out, false);
                }

            } // end if
            
        } // end while loop for teecurr2

        pstmt.close();
    }
    catch (Exception exp) {
        SystemUtils.buildDatabaseErrMsg("Fatal error sending emails.", exp.toString(), out, false);
    }

    out.println("<center>");
    
    out.println("<h3>Done!</h3>");
    out.println("<p>We sent " + count3 + " emails to " + count2 + " members for " + count1 + " tee times.</p>");
    //out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +index+ "&course=" +returnCourse+ "\" title=\"Return to Tee Sheet\" alt=\"Return\">Back To Tee Sheet</a>");
    
    out.println("<form method=get action=/" + rev + "/servlet/Proshop_jump>");
    out.println("<input type=hidden name=index value=\"" + index + "\">");
    out.println("<input type=hidden name=course value=\"" + course + "\">");
    out.println("<input type=submit value=\"Continue\">");
    out.println("</form>");
    
    out.println("</center>");

 }
 
 
 private void sendWaitListEmails(String clubName, String event_name, int event_date, int index, String returnCourse, PrintWriter out, Connection con) {

    out.println("<center>");
    out.println("<h2>Sending Emails...</h2>");
    out.println("<p>THIS MAY TAKE SEVERAL MINUTES TO COMPLETE.<br><br>DO NOT CLICK YOUR BROWSERS BACK BUTTON!</p>");
    out.println("</center>");
     
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    String errorMsg = "";
    String course = "";
    String day = "";

    int time = 0;
    int atime1 = 0;
    int dd = 0;
    int mm = 0;
    int yy = 0;
    int afb = 0;
    int count1 = 0; // tee times that we tried to send emails to
    int count2 = 0; // number of members we found email addresses for (recipients)
    int count3 = 0; // number of actual email messages we sent to the email server

    String user1 = "";
    String user2 = "";
    String user3 = "";
    String user4 = "";
    String user5 = "";

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

    String p1cw = "";
    String p2cw = "";
    String p3cw = "";
    String p4cw = "";
    String p5cw = "";
    
    String hole = "";

    int guest_id1 = 0;
    int guest_id2 = 0;
    int guest_id3 = 0;
    int guest_id4 = 0;
    int guest_id5 = 0;
    
    int teecurr_id = 0;
    int lott_email = 0;
    
    //
    //  Get today's date and time for email processing
    //
    Calendar ecal = new GregorianCalendar();               // get todays date
    int eyear = ecal.get(Calendar.YEAR);
    int emonth = ecal.get(Calendar.MONTH);
    int eday = ecal.get(Calendar.DAY_OF_MONTH);
    int e_hourDay = ecal.get(Calendar.HOUR_OF_DAY);
    int e_min = ecal.get(Calendar.MINUTE);
    
    int e_time = 0;
    long e_date = 0;
    
    //
    //   Adjust the time based on the club's time zone (we are Central)
    //
    e_time = SystemUtils.adjustTime(con, (e_hourDay * 100) + e_min);
    
    if (e_time < 0) {          // if negative, then we went back or ahead one day
        
        e_time = 0 - e_time;        // convert back to positive value
        
        //
        // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
        //
        ecal.add(Calendar.DATE,( (e_time < 1200) ? 1 : -1) );                     // get next day's date

        eyear = ecal.get(Calendar.YEAR);
        emonth = ecal.get(Calendar.MONTH);
        eday = ecal.get(Calendar.DAY_OF_MONTH);
    
    }
    
    emonth++;                            // month starts at zero
    int e_hour = e_time / 100;           // get adjusted hour
    e_min = e_time - (e_hour * 100);     // get minute value
    int e_am_pm = 0;                     // preset to AM
    
    if (e_hour > 11) {
    
        e_am_pm = 1;        // PM
        e_hour -= 12;       // set to 12 hr clock
    }
    
    if (e_hour == 0) e_hour = 12;
    
    //
    //  Build the 'time' string for display
    //
    e_date = (eyear * 10000) + (emonth * 100) + eday;

    //
    //  get date/time string for email message
    //
    // String email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":" + SystemUtils.ensureDoubleDigit(e_min) + ((e_am_pm == 0) ? " AM" : " PM");


            
    //*****************************************************************************
    //  Send an email to all players the are part of this event and have not
    //  yet been sent an email.
    //*****************************************************************************
    //
/*    
    out.println("<br>event_name=" + event_name);
    out.println("<br>event_date=" + event_date);
    out.println("<br>clubName=" + clubName);
    out.println("<br>email_time=" + email_time);
*/    
    try {

        pstmt = con.prepareStatement ("" +
            "SELECT * " +
            "FROM teecurr2 " +
            "WHERE date = ? AND lottery_email = 3"); // use the lottery email flag for the events (update, this may not work if they simultaneously work on an event and lottery for the same day at the same time)
            
        pstmt.clearParameters();
        pstmt.setInt(1, event_date);
        //pstmt.setString(2, lott_name);AND lottery = ?


        rs = pstmt.executeQuery();  
      
        while (rs.next()) {
          
            count1++; // inc the tee time count
            
            teecurr_id = rs.getInt("teecurr_id");
            course = rs.getString("courseName");
            day = rs.getString("day");
            dd = rs.getInt("dd");
            mm = rs.getInt("mm");
            yy = rs.getInt("yy");
            afb = rs.getInt("fb");
            atime1 = rs.getInt("time");
            lott_email = rs.getInt("lottery_email");

            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            
            user1 = rs.getString("username1");
            user2 = rs.getString("username2");
            user3 = rs.getString("username3");
            user4 = rs.getString("username4");
            user5 = rs.getString("username5");
            
            p1cw = rs.getString("p1cw");
            p2cw = rs.getString("p2cw");
            p3cw = rs.getString("p3cw");
            p4cw = rs.getString("p4cw");
            p5cw = rs.getString("p5cw");

            guest_id1 = rs.getInt("guest_id1");
            guest_id2 = rs.getInt("guest_id2");
            guest_id3 = rs.getInt("guest_id3");
            guest_id4 = rs.getInt("guest_id4");
            guest_id5 = rs.getInt("guest_id5");
            
            hole = rs.getString("hole");
               
            //
            //***********************************************
            //  Send email notification if necessary
            //***********************************************
            //
            String to = "";                          // to address
            String f_b = "";
            String eampm = "";
            String etime = "";
            String enewMsg = "";
            int emailOpt = 0;                        // user's email option parm
            int ehr = 0;
            int emin = 0;
            int send = 0;

            PreparedStatement pstmte1 = null;

            //
            //  set the front/back value
            //
            f_b = "Front";

            if (afb == 1) {

                f_b = "Back";
            }

            String enew1 = "";
            //String enew2 = "";
            String subject = "";

            enew1 = "The following Tee Time has been assigned from the Wait List.\n\n";
            subject = "ForeTees Tee Time Assignment Notification";

            if (!clubName.equals( "" )) {

                subject = subject + " - " + clubName;
            }

            Properties properties = new Properties();
            properties.put("mail.smtp.host", SystemUtils.host);                     // set outbound host address
            properties.put("mail.smtp.port", SystemUtils.port);                     // set outbound port
            properties.put("mail.smtp.auth", "true");                               // set 'use authentication'

            Session mailSess = Session.getInstance(properties, SystemUtils.getAuthenticator());   // get session properties

            MimeMessage message = new MimeMessage(mailSess);
            
            try {

                message.setFrom(new InternetAddress(SystemUtils.EFROM));                  // set from addr
                message.setSubject( subject );                                            // set subject line
                message.setSentDate(new java.util.Date());                                // set date/time sent
            }
            catch (Exception exp) { 
                out.println("<p>Can't create message object. " + exp.toString() + "</p>");
            }


            //
            //  Set the recipient addresses
            //
               if (!user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ? AND email_bounced = 0");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                           count2++;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) { }
               }

               if (!user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ? AND email_bounced = 0");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                           count2++;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ? AND email_bounced = 0");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                           count2++;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ? AND email_bounced = 0");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                           count2++;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ? AND email_bounced = 0");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                           count2++;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               

            //
            //  send email if anyone to send it to
            //
            if (send != 0) {        // if any email addresses specified for members
                
                count3++; // inc the message count
                
                enewMsg = SystemUtils.header + enew1 + day + " " + mm + "/" + dd + "/" + yy + " on the " + f_b + " tee ";
                    
                if (!course.equals( "" )) enewMsg = enewMsg + "of Course: " + course;
                     
                //
                //  convert time to hour and minutes for email msg
                //
                time = atime1;              // time for this tee time
                ehr = time / 100;
                emin = time - (ehr * 100);
                eampm = " AM";
                if (ehr > 12) {

                    eampm = " PM";
                    ehr = ehr - 12;       // convert from military time
                }
                if (ehr == 12) eampm = " PM";
                if (ehr == 0) {

                    ehr = 12;
                    eampm = " AM";
                }

                etime = ehr + ":" + SystemUtils.ensureDoubleDigit(emin) + eampm;

                enewMsg = enewMsg + "\n at " + etime + "\n";
                
                if (!player1.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 1: " + player1 + "  " + p1cw;
                }
                if (!player2.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 2: " + player2 + "  " + p2cw;
                }
                if (!player3.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 3: " + player3 + "  " + p3cw;
                }
                if (!player4.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 4: " + player4 + "  " + p4cw;
                }
                if (!player5.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 5: " + player5 + "  " + p5cw;
                }

                enewMsg = enewMsg + SystemUtils.trailer;

                try {

                    message.setText( enewMsg );  // put msg in email text area
                    //out.println("<p>MESSAGE:<br>" + enewMsg + "</p>");
                    Transport.send(message);     // send it!!
                }
                catch (Exception exp) { 
                    teecurr_id = 0; // reset so that we can detect that we didn't send email
                    count3--;
                    SystemUtils.buildDatabaseErrMsg("Can't build message. " + exp.getMessage() + "<br>", exp.toString(), out, false);
                }
                
            }/* else {
             out.println("<p>We did not send emails because no players had email addresses.</p>");   
            }// end of IF send */

            if (teecurr_id > 0) {

                try {

                    PreparedStatement pstmt6 = con.prepareStatement (
                         "UPDATE teecurr2 " +
                         "SET lottery_email = 0 " +
                         "WHERE teecurr_id = ?");

                    pstmt6.clearParameters();
                    pstmt6.setInt(1, teecurr_id);
                    pstmt6.executeUpdate();
                    
                    pstmt6.close();

                } catch (Exception exp) {
                    SystemUtils.buildDatabaseErrMsg("Error updating tee time entry.", exp.toString(), out, false);
                }

            } // end if
            
        } // end while loop for teecurr2

        pstmt.close();
    }
    catch (Exception exp) {
        SystemUtils.buildDatabaseErrMsg("Fatal error sending emails.", exp.toString(), out, false);
    }

    out.println("<center>");
    
    out.println("<h3>Done!</h3>");
    out.println("<p>We sent " + count3 + " emails to " + count2 + " members for " + count1 + " tee times.</p>");
    //out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +index+ "&course=" +returnCourse+ "\" title=\"Return to Tee Sheet\" alt=\"Return\">Back To Tee Sheet</a>");
    
    out.println("<form method=get action=/" + rev + "/servlet/Proshop_jump>");
    out.println("<input type=hidden name=index value=\"" + index + "\">");
    out.println("<input type=hidden name=course value=\"" + course + "\">");
    out.println("<input type=submit value=\"Continue\">");
    out.println("</form>");
    
    out.println("</center>");

 }
 
 
 private void sendWaitListEmail(int teecurr_id, PrintWriter out, Connection con) {

     
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    String errorMsg = "";
    String course = "";
    String day = "";

    int time = 0;
    int atime1 = 0;
    int dd = 0;
    int mm = 0;
    int yy = 0;
    int afb = 0;

    String user1 = "";
    String user2 = "";
    String user3 = "";
    String user4 = "";
    String user5 = "";

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

    String p1cw = "";
    String p2cw = "";
    String p3cw = "";
    String p4cw = "";
    String p5cw = "";

    int guest_id1 = 0;
    int guest_id2 = 0;
    int guest_id3 = 0;
    int guest_id4 = 0;
    int guest_id5 = 0;
    
    String clubName = SystemUtils.getClubName(con);
       
    //
    //  Get today's date and time for email processing
    //
    Calendar ecal = new GregorianCalendar();               // get todays date
    int eyear = ecal.get(Calendar.YEAR);
    int emonth = ecal.get(Calendar.MONTH);
    int eday = ecal.get(Calendar.DAY_OF_MONTH);
    int e_hourDay = ecal.get(Calendar.HOUR_OF_DAY);
    int e_min = ecal.get(Calendar.MINUTE);
    
    int e_time = 0;
    long e_date = 0;
    
    //
    //   Adjust the time based on the club's time zone (we are Central)
    //
    e_time = SystemUtils.adjustTime(con, (e_hourDay * 100) + e_min);
    
    if (e_time < 0) {          // if negative, then we went back or ahead one day
        
        e_time = 0 - e_time;        // convert back to positive value
        
        //
        // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
        //
        ecal.add(Calendar.DATE,( (e_time < 1200) ? 1 : -1) );                     // get next day's date

        eyear = ecal.get(Calendar.YEAR);
        emonth = ecal.get(Calendar.MONTH);
        eday = ecal.get(Calendar.DAY_OF_MONTH);
    
    }
    
    emonth++;                            // month starts at zero
    int e_hour = e_time / 100;           // get adjusted hour
    e_min = e_time - (e_hour * 100);     // get minute value
    int e_am_pm = 0;                     // preset to AM
    
    if (e_hour > 11) {
    
        e_am_pm = 1;        // PM
        e_hour -= 12;       // set to 12 hr clock
    }
    
    if (e_hour == 0) e_hour = 12;
    
    //
    //  Build the 'time' string for display
    //
    e_date = (eyear * 10000) + (emonth * 100) + eday;

    //
    //  get date/time string for email message
    //
    // String email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":" + SystemUtils.ensureDoubleDigit(e_min) + ((e_am_pm == 0) ? " AM" : " PM");


            
    //**************************************************************
    //  Send an email to all players the are part of this tee time 
    //**************************************************************
    //
    
    try {

        pstmt = con.prepareStatement ("" +
            "SELECT * " +
            "FROM teecurr2 " +
            "WHERE teecurr_id = ?"); // use the lottery email flag for the events (update, this may not work if they simultaneously work on an event and lottery for the same day at the same time)
            
        pstmt.clearParameters();
        pstmt.setInt(1, teecurr_id);

        rs = pstmt.executeQuery();  
      
        if (rs.next()) {
            
            course = rs.getString("courseName");
            day = rs.getString("day");
            dd = rs.getInt("dd");
            mm = rs.getInt("mm");
            yy = rs.getInt("yy");
            afb = rs.getInt("fb");
            atime1 = rs.getInt("time");

            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            
            user1 = rs.getString("username1");
            user2 = rs.getString("username2");
            user3 = rs.getString("username3");
            user4 = rs.getString("username4");
            user5 = rs.getString("username5");
            
            p1cw = rs.getString("p1cw");
            p2cw = rs.getString("p2cw");
            p3cw = rs.getString("p3cw");
            p4cw = rs.getString("p4cw");
            p5cw = rs.getString("p5cw");

            guest_id1 = rs.getInt("guest_id1");
            guest_id2 = rs.getInt("guest_id2");
            guest_id3 = rs.getInt("guest_id3");
            guest_id4 = rs.getInt("guest_id4");
            guest_id5 = rs.getInt("guest_id5");
               
            //
            //***********************************************
            //  Send email notification if necessary
            //***********************************************
            //
            String to = "";                          // to address
            String f_b = "";
            String eampm = "";
            String etime = "";
            String enewMsg = "";
            int emailOpt = 0;                        // user's email option parm
            int ehr = 0;
            int emin = 0;
            int send = 0;

            PreparedStatement pstmte1 = null;

            //
            //  set the front/back value
            //
            f_b = "Front";

            if (afb == 1) {

                f_b = "Back";
            }

            String enew1 = "";
            String subject = "";

            enew1 = "The following Tee Time has been assigned from the Wait List.\n\n";
            subject = "ForeTees Tee Time Assignment Notification";

            if (!clubName.equals( "" )) {

                subject = subject + " - " + clubName;
            }

            Properties properties = new Properties();
            properties.put("mail.smtp.host", SystemUtils.host);                     // set outbound host address
            properties.put("mail.smtp.port", SystemUtils.port);                     // set outbound port
            properties.put("mail.smtp.auth", "true");                               // set 'use authentication'

            Session mailSess = Session.getInstance(properties, SystemUtils.getAuthenticator());   // get session properties

            MimeMessage message = new MimeMessage(mailSess);
            
            try {

                message.setFrom(new InternetAddress(SystemUtils.EFROM));                  // set from addr
                message.setSubject( subject );                                            // set subject line
                message.setSentDate(new java.util.Date());                                // set date/time sent
            }
            catch (Exception exp) { 
                out.println("<p>Can't create message object. " + exp.toString() + "</p>");
            }


            //
            //  Set the recipient addresses
            //
               if (!user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ? AND email_bounced = 0");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) { }
               }

               if (!user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ? AND email_bounced = 0");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ? AND email_bounced = 0");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ? AND email_bounced = 0");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ? AND email_bounced = 0");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               

            //
            //  send email if anyone to send it to
            //
            if (send != 0) {        // if any email addresses specified for members
                                
                enewMsg = SystemUtils.header + enew1 + day + " " + mm + "/" + dd + "/" + yy + " on the " + f_b + " tee ";
                    
                if (!course.equals( "" )) enewMsg = enewMsg + "of Course: " + course;
                     
                //
                //  convert time to hour and minutes for email msg
                //
                time = atime1;              // time for this tee time
                ehr = time / 100;
                emin = time - (ehr * 100);
                eampm = " AM";
                if (ehr > 12) {

                    eampm = " PM";
                    ehr = ehr - 12;       // convert from military time
                }
                if (ehr == 12) eampm = " PM";
                if (ehr == 0) {

                    ehr = 12;
                    eampm = " AM";
                }

                etime = ehr + ":" + SystemUtils.ensureDoubleDigit(emin) + eampm;

                enewMsg = enewMsg + "\n at " + etime + "\n";
                
                if (!player1.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 1: " + player1 + "  " + p1cw;
                }
                if (!player2.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 2: " + player2 + "  " + p2cw;
                }
                if (!player3.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 3: " + player3 + "  " + p3cw;
                }
                if (!player4.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 4: " + player4 + "  " + p4cw;
                }
                if (!player5.equals( "" )) {

                    enewMsg = enewMsg + "\nPlayer 5: " + player5 + "  " + p5cw;
                }

                enewMsg = enewMsg + SystemUtils.trailer;

                try {

                    message.setText( enewMsg );  // put msg in email text area
                    Transport.send(message);     // send it!!
                }
                catch (Exception exp) { 
                    
                    SystemUtils.buildDatabaseErrMsg("Can't build message. " + exp.getMessage() + "<br>", exp.toString(), out, false);
                }
                
            }/* else {
             out.println("<p>We did not send emails because no players had email addresses.</p>");   
            }// end of IF send */

            if (teecurr_id > 0) {

                try {

                    PreparedStatement pstmt6 = con.prepareStatement (
                         "UPDATE teecurr2 " +
                         "SET lottery_email = 0 " +
                         "WHERE teecurr_id = ?");

                    pstmt6.clearParameters();
                    pstmt6.setInt(1, teecurr_id);
                    pstmt6.executeUpdate();
                    
                    pstmt6.close();

                } catch (Exception exp) {
                    
                    SystemUtils.buildDatabaseErrMsg("Error updating tee time entry.", exp.toString(), out, false);
                }

            } // end if
            
        } // end if rs

        pstmt.close();
    }
    catch (Exception exp) {
        
        SystemUtils.buildDatabaseErrMsg("Fatal error sending email.", exp.toString(), out, false);
    }

 }
 
 
 private static String getMemberPhone(String username, Connection con) {
     
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String ph = "";

    if (!username.equals("")) {

        try {

            pstmt = con.prepareStatement (
                "SELECT phone1, phone2 FROM member2b WHERE username = ?");

            pstmt.clearParameters();
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                
                ph = rs.getString(1);
                if (!rs.getString(2).equals("")) ph = ph + ", " + rs.getString(2);
            }
            
        } catch (Exception ignore) { 
        } finally {
            
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception exc) {}
            }
            
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception exc) {}
            }
            
        }
    
    }

    return ph;
 }



 // *********************************************************
 //  Return to Proshop_dsheet
 // *********************************************************
 private void returnToDsheet(PrintWriter out, parmSlot slotParms, String mode, long date) {

     // call other method and pass default options so the user can not override
     returnToDsheet(out, slotParms, mode, date, "", "", false, 0);

 }


 private void returnToDsheet(PrintWriter out, parmSlot slotParms, String mode, long date, int skip) {

     // call other method and pass default options so the user can override
     returnToDsheet(out, slotParms, mode, date, "", "", true, skip);

 }

 private void returnToDsheet(PrintWriter out, parmSlot slotParms, String mode, long date, String user, String customMsg, boolean allowOverride, int skip) {

     if (allowOverride) {
         if (!customMsg.equals("")) {
             out.println("<BR><BR>" + customMsg);
             out.println("<BR><BR>");
         } else {
             out.println("<BR><BR>Would you like to override this and allow this reservation?");
             out.println("<BR><BR>");
         }
     }

     out.println("<font size=\"2\">");
     out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
     out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
     out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
     out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
     out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
     out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
     out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
     out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
     out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
     if (slotParms.to_player != 0) out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
     if (slotParms.from_player != 0) out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
     out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
     out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
     out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
     out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
     out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
     out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
     out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");

     if (!allowOverride) {
         out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
     } else {
         out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");

         out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"mode\" value=\"" + mode + "\">");
         out.println("<input type=\"hidden\" name=\"name\" value=\"" + slotParms.name + "\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
         out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
         out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
         out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
         out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
         if (slotParms.to_player != 0) out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
         if (slotParms.from_player != 0) out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
         out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
         out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
         out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
         out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
         out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
         out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
         out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
         out.println("<input type=\"hidden\" name=\"skip\" value=\"" + skip + "\">");
         out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
     }

     out.println("</CENTER></BODY></HTML>");
     out.close();
 }
 
} // end servlet
