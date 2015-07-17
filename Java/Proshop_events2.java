/***************************************************************************************     
 *   Proshop_events2:  This servlet will display event information and sign-up info
 *                     for the event selected in Proshop_events.
 *
 *
 *
 *   called by:  Proshop_events1
 *               Proshop_jump (on return from Proshop_evntSignUp)
 *
 *
 *   created: 2/18/2003   Bob P.
 *
 *   last updated:
 *
 *       12/08/09   Do not delete events or event signups - mark them inactive instead so we can easily restore them.
 *       11/10/09   Updated tourExPrintPlayer & golfNetPrintPlayer to make it handle parsing of middle initials better
 *       10/19/09   Updated eventManPrintPlayer to make it handle parsing of middle initials better
 *        9/28/09   Added support for Activities
 *        9/04/09   Updated tppPrintPlayer to either strip off minus sign or add a plus sign to hndcp
 *        7/24/09   Updated tppPrintPlayer to seperate player names in to last, first, middle
 *        6/25/09   Add Tournament Expert (TourEx) export support.
 *        3/24/09   Updated eventMan/golfNetPrintPlayer to try and pull ghin & gender if not in signup. (for old events)
 *        3/19/09   Changed team/count calcuation to seperate registered vs. waitlist and changed related display text
 *        1/28/09   Updated tppPrintPlayer to try and pull ghin & gender if not in signup. (for old events)
 *        1/16/09   Fixed issue where gender value was not being retrieved from the database.  All events were coming up as "Unknown"
 *        1/09/08   Updated tppPrintPlayer to remove dash from ghin number (TPP2004 failed w/ dash)
 *       11/03/08   Add GolfNet export support
 *       11/05/08   Add Detailed Listing for displaying the extra signup information
 *        9/25/08   Add order by clause to print list output
 *        9/10/08   Commented out the Rounding of hndcp values before being displayed
 *        9/03/08   Modifications to limited access restrictions
 *        9/02/08   Restricted access to registering guests to events so not allowed without EVNTSUP_UPDATE access
 *        8/07/08   Changed links from dlott to new dsheet
 *        7/18/08   Added limited access proshop users checks
 *        6/07/08   The CC - display member numbers next to names (Case #1488)
 *        5/05/08   Add exportEvent method for TPP export functionality
 *        3/27/08   Add gender and season information to event summary and removed C/W info for season long events
 *        3/30/08   TCC - Allow access to Register Guests button for member events w/o online signup (case 1407)
 *        3/19/08   Fix cut-off time not adjusting to clubs local time
 *        2/06/08   Added hi-lites and js popup to the New Team button if the signup period has passed
 *       12/13/07   Added hi-lites and js popup to the New Team button if event is not yet avail for signups
 *       12/11/07   Added "On Tee Sheet" to status if a signup is marked as moved
 *        9/27/07   Display the new minimum sign-up size as part of the event details
 *        8/21/07   Changed the Drag-n-Drop link to only appear for current events
 *        8/01/07   Congressional - Add them to new signup lockout feature
 *        4/25/07   Congressional - pass the date for the ourse Name Labeling.
 *        3/20/07   Custom for Congressional - abstract the course name depending on the day (Course Name Labeling)
 *       12/01/05   RDP - Add a 'Move To Tee Sheet w/o Check In' option.
 *        1/24/05   Ver 5 - change club2 to club5.
 *        9/14/04   Add a 'List Notes' option for reports.
 *        7/07/04   Add a 'List Members Alphabetically' option for reports.
 *        6/01/04   Add Excel option for reports.
 *        7/18/03   Enhancements for Version 3 of the software.
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
import com.foretees.common.congressionalCustom;
import com.foretees.common.Labels;
import com.foretees.common.Utilities;


public class Proshop_events2 extends HttpServlet {
                               

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

//********************************************************************************
//
//  doGet - call doPost processing (gets control from Proshop_jump)
//
//********************************************************************************
//
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

     doPost(req, resp);                          // call doPost processing

 }   // end of doGet


 //
 //******************************************************************************
 //
 //  doPost processing - gets control from Proshop_events1 when user selects an event.
 //
 //  Get the event info and display a sign up sheet
 //
 //******************************************************************************
   
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   Statement stmtm = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;
   
   if (req.getParameter("export") != null){

       exportEvent(req, resp);
       return;
   }

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
   
   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) return;

   String user = (String)session.getAttribute("user");      // get this user's username
   String club = (String)session.getAttribute("club");      // get club name
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   boolean USE_NEW_SIGNUP = true;  // set everyone to yes now

   Connection con = SystemUtils.getCon(session);            // get DB connection
   
   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "EVNTSUP_VIEW", con, out)) {
       SystemUtils.restrictProshop("EVNTSUP_VIEW", out);
       return;
   }
   
   boolean updateAccess = true;         // true if proshop user is allowed to make/change event regs
   if (!SystemUtils.verifyProAccess(req, "EVNTSUP_UPDATE", con, out)) {
       updateAccess = false;
   }
   
   boolean manageAccess = true;         // true if proshop user is allowed to manage (i.e. move to tee sheet) event regs
   if (!SystemUtils.verifyProAccess(req, "EVNTSUP_MANAGE", con, out)) {
       manageAccess = false;
   }
   
   //
   //   Get the parms received
   //
   String name = req.getParameter("name");
   String course = req.getParameter("course");
   
   String format = "";
   String pairings = "";
   String memcost = "";
   String gstcost = "";
   String itin = "";
   String c_ampm = "";
   String su_ampm = "";
   String act_ampm = "";
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
   String submit = "";
   String r_ampm = "";
   String notes = "";
   String fb = "";
     
   String p1 = "";
   String p2 = "";
   String p3 = "";
   String p4 = "";
   String p5 = "";
   
   String mnum1 = "";
   String mnum2 = "";
   String mnum3 = "";
   String mnum4 = "";
   String mnum5 = "";

   int month  = 0;
   int day = 0;
   int year = 0;
   int type = 0;
   int holes = 0;
   int act_hr = 0;
   int act_min = 0;
   int size = 0;
   int minsize = 0;
   int max = 0;
   int guests = 0;
   int su_month = 0;
   int su_day = 0;
   int su_year = 0;
   int su_time = 0;
   int su_hr = 0;
   int su_min = 0;
   int c_month = 0;
   int c_day = 0;
   int c_year = 0;
   int c_time = 0;
   int c_hr = 0;
   int c_min = 0;
   int cmonth = 0;
   int cday = 0;
   int cyear = 0;
   //int ctime = 0;
   int count_reg = 0;
   int count_wait = 0;
   int teams_reg = 0;
   int teams_wait = 0;
   int t = 0;
   //int full = 0;
   int id = 0;
   //int skip = 0;
   int r_time = 0;
   int r_hr = 0;
   int r_min = 0;
   int gstOnly = 0;
   int in_use = 0;
   int wait = 0;
   int moved = 0;
   int signup = 0;
   int hndcpOpt = 0;
   int now_time = 0;
   int now_date = 0;
   int season = 0;
   int gender = 0;
   int export_type = 0;
   int activity_id = 0; // this is the root activity id for this event (same as sess_activity_id)
     
   int ask_hdcp = 0;
   int ask_homeclub = 0;
   int ask_gender = 0;
   int ask_phone = 0;
   int ask_email = 0;
   int ask_address = 0;
   int ask_shirtsize = 0;
   int ask_shoesize = 0;
   int ask_otherA1 = 0;
   int ask_otherA2 = 0;
   int ask_otherA3 = 0;
      
   long date = 0;
   long cdate = 0;
   long c_date = 0;
   long r_date = 0;
   long su_date = 0;
   long temp = 0;
   long r_yr = 0;
   long r_mm = 0;
   long r_dd = 0;
   //float hndcp = 0;
   float hndcp1 = 0;
   float hndcp2 = 0;
   float hndcp3 = 0;
   float hndcp4 = 0;
   float hndcp5 = 0;

   boolean disp_hndcp = true;
   boolean disp_mnum = false;
   boolean guestsFound = false;
   boolean okToMove1 = false;
   boolean okToMove2 = false;
   boolean lockout = false;
   boolean signUpOK = false;
   boolean use_lockout = false;
   
   // Add a club here to disable the 'Select' button and block access to the signups after the cut off date/time.
   if ( (club.equals("congressional") && !user.equalsIgnoreCase("proshop4")) ) {
       
      use_lockout = true;
   }
   
   //
   //  If club wants to display member numbers after names
   //
   if (club.equals("tcclub")) {
       
      disp_mnum = true;
   }
   
   //
   //   if call was for Show Notes then get the notes and display a new page
   //
   if (req.getParameter("notes") != null) {

      String sid = req.getParameter("id");             //  entry id in evntsup

      try {
         id = Short.parseShort(sid);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      try {

         PreparedStatement pstmt2s = con.prepareStatement (
            "SELECT player1, notes " +
            "FROM evntsup2b WHERE name = ? AND courseName = ? AND id = ?");

         pstmt2s.clearParameters();        // clear the parms
         pstmt2s.setString(1, name);
         pstmt2s.setString(2, course);
         pstmt2s.setInt(3, id);

         rs = pstmt2s.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            player1 = rs.getString(1);
            notes = rs.getString(2);
         }

         pstmt2s.close();

         out.println(SystemUtils.HeadTitle("Show Notes"));
         out.println("<BODY><CENTER><BR>");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<font size=\"3\"><b>Event Notes</b></font><br><BR>");
         out.println("<font size=\"2\">For Event <b>" + name + "</b> ");
         // we can abstract the name for congressional here if needed, by including the day # in the sql above or pass it w/ the request
         if (!course.equals( "" )) {
            out.println("on course <b>" + course + "</b> ");
         }
         out.println("where Player 1 is <b>" + player1);
         out.println("</b><BR><br>");
         out.println( notes );
         out.println("<BR>");
         out.println("<form>");
         out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" value=\"Close\" onclick='self.close()' alt=\"Close\">");
         out.println("</form>");
         out.println("</font></CENTER></BODY></HTML>");

      }
      catch (Exception ignore) {

      }
      return;       // exit
   }

   try {

      stmtm = con.createStatement();        // create a statement
      rs = stmtm.executeQuery("SELECT hndcpProEvent " +
                              "FROM club5 WHERE clubName != ''");         // get the display hndcp option

      if (rs.next()) {

         hndcpOpt = rs.getInt(1);
      }
      stmtm.close();

      //
      //  Check if club wants to display handicaps
      //
      if (hndcpOpt == 0) {

         disp_hndcp = false;      // if NO
      }

      //
      //  First, count the number of players and teams already signed up
      //
      PreparedStatement pstmt = con.prepareStatement (
         "SELECT player1, player2, player3, player4, player5, wait FROM evntsup2b " +
         "WHERE name = ? AND inactive = 0");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      while (rs.next()) {

         player1 = rs.getString(1);
         player2 = rs.getString(2);
         player3 = rs.getString(3);
         player4 = rs.getString(4);
         player5 = rs.getString(5);
         wait = rs.getInt(6);

         t = 0;

         // check each player position and bump the appropriate count  (NOTE: events signups do not call shift up!)
         if (!player1.equals( "" )) {

             t = 1;
             if (wait == 0) { count_reg++; }
             else { count_wait++; }
         }
         if (!player2.equals( "" )) {

             t = 1;
             if (wait == 0) { count_reg++; }
             else { count_wait++; }
         }
         if (!player3.equals( "" )) {

            t = 1;
            if (wait == 0) { count_reg++; }
            else { count_wait++; }
         }
         if (!player4.equals( "" )) {

            t = 1;
            if (wait == 0) { count_reg++; }
            else { count_wait++; }
         }
         if (!player5.equals( "" )) {

            t = 1;
            if (wait == 0) { count_reg++; }
            else { count_wait++; }
         }

         // if we found a team then bump the appropriate count
         if (t==1) {

             if (wait == 0) { teams_reg++; }
             else { teams_wait++; }
         }

      }
      pstmt.close();

      
      //
      //   get the event requested
      //
      PreparedStatement stmt = con.prepareStatement (
         "SELECT *, DATE_FORMAT(now(), '%k%i') AS now_time, DATE_FORMAT(now(), '%Y%m%d') AS now_date " +
         "FROM events2b " +
         "WHERE name = ?");
      
      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);
      rs = stmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         date = rs.getLong("date");
         year = rs.getInt("year");
         month = rs.getInt("month");
         day = rs.getInt("day");
         type = rs.getInt("type");
         act_hr = rs.getInt("act_hr");
         act_min = rs.getInt("act_min");
         signup = rs.getInt("signUp");
         format = rs.getString("format");
         pairings = rs.getString("pairings");
         size = rs.getInt("size");
         minsize = rs.getInt("minsize");
         max = rs.getInt("max");
         guests = rs.getInt("guests");
         memcost = rs.getString("memcost");
         gstcost = rs.getString("gstcost");
         c_month = rs.getInt("c_month");
         c_day = rs.getInt("c_day");
         c_year = rs.getInt("c_year");
         c_date = rs.getLong("c_date");
         c_time = rs.getInt("c_time");
         itin = rs.getString("itin");
         gstOnly = rs.getInt("gstOnly");
         holes = rs.getInt("holes");
         su_month = rs.getInt("su_month");
         su_day = rs.getInt("su_day");
         su_year = rs.getInt("su_year");
         su_date = rs.getLong("su_date");
         su_time = rs.getInt("su_time");
         fb = rs.getString("fb");
         now_time = rs.getInt("now_time");
         now_date = rs.getInt("now_date");
         season = rs.getInt("season");
         export_type = rs.getInt("export_type");
         gender = rs.getInt("gender");
         activity_id = rs.getInt("activity_id"); // this is the root activity id for this event (same as sess_activity_id)
         
         ask_homeclub = rs.getInt("ask_homeclub");
         ask_phone = rs.getInt("ask_phone");
         ask_address = rs.getInt("ask_address");
         ask_hdcp = rs.getInt("ask_hdcp");
         ask_email = rs.getInt("ask_email");
         ask_gender = rs.getInt("ask_gender");
         ask_shirtsize = rs.getInt("ask_shirtsize");
         ask_shoesize = rs.getInt("ask_shoesize");
         ask_otherA1 = rs.getInt("ask_otherA1");
         ask_otherA2 = rs.getInt("ask_otherA2");
         ask_otherA3 = rs.getInt("ask_otherA3");
         
         now_time = SystemUtils.adjustTime(con, now_time); // adjust to their localtime
         
         // if it's past the cutoff date/time then set out lockout indicator
         lockout = (now_date > c_date || (now_date == c_date && now_time >= c_time));
         
         // if the signup has not yet started, set flag for notifiying user
         signUpOK = (now_date > su_date || (now_date == su_date && now_time >= su_time));
         
      } else {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the event you requested.");
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact support (provide this information).");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }   // end of IF event

      stmt.close();

      //out.println("<!-- now_date=" + now_date + " now_time=" + now_time + " -->");
      //out.println("<!-- c_date=" + c_date + " c_time=" + c_time + " -->");
      
      //out.println("<!-- use_lockout=" + use_lockout + " -->");
      out.println("<!-- lockout=" + lockout + " -->");
      out.println("<!-- signUpOK=" + signUpOK + " -->");
      
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

      su_hr = su_time / 100;
      su_min = su_time - (su_hr * 100);

      su_ampm = "AM";

      if (su_hr == 0) {

         su_hr = 12;                 // change to 12 AM (midnight)

      } else {

         if (su_hr == 12) {

            su_ampm = "PM";         // change to Noon
         }
      }
      if (su_hr > 12) {

         su_hr = su_hr - 12;
         su_ampm = "PM";             // change to 12 hr clock
      }

      //
      //  Override team size if proshop pairings (just in case)
      //
      if (!pairings.equalsIgnoreCase( "Member" )) {

         size = 1;       // set size to one for proshop pairings (size is # per team)
      }

      if (req.getParameter("print") == null) {

         //
         //  Check if its too early to move players to the tee sheet
         //
         Calendar cal = new GregorianCalendar();        // get current date and time
         cyear = cal.get(Calendar.YEAR);
         cmonth = cal.get(Calendar.MONTH)+1;
         cday = cal.get(Calendar.DAY_OF_MONTH);

         cdate = (cyear * 10000) + (cmonth * 100) + cday;    // convert to yyyymmdd value

         if (date <= cdate) {       // if ok to check in (event date <= today)

            okToMove1 = true;      
         }

         if (c_date <= cdate) {       // if ok to move w/o check in (event end signup date <= today)

            okToMove2 = true;      
         }


         //
         //   build the HTML page for the display
         //
         out.println(SystemUtils.HeadTitle("Proshop Event Sign Up Page"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
         SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
         out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

         out.println("<table border=\"0\" valign=\"top\">");       // table for main page
         out.println("<tr><td align=\"center\" valign=\"top\">");
         out.println("<font size=\"2\">");

         out.println("<table border=\"0\" align=\"left\" valign=\"top\">");      // table for cmd tbl & header
         out.println("<tr align=\"left\"><td align=\"left\">");

         if (gstOnly == 0 && signup != 0) {     // if not guest only event and members can signup

            out.println("<table border=\"1\" cellpadding=\"3\" bgcolor=\"8B8970\" align=\"left\">");
            out.println("<tr>");
            out.println("<td align=\"center\"><font color=\"#FFFFFF\" size=\"3\"><b> Control Panel </b><br><br>");
            out.println("</font><font size=\"2\">");
            
            // Only restrict if user is View-Only for EVNTSUP restrictions
            if (updateAccess || manageAccess) {
                out.println("<a href=\"/" +rev+ "/servlet/Proshop_events2?name=" +name+ "&course=" +course+ "&print=print\" target=\"_blank\" title=\"Print This Page\" alt=\"Print\">");
                out.println("Print List</a><br><br>");

                if (activity_id == 0) {
                    out.println("<a href=\"/" +rev+ "/servlet/Proshop_events2?name=" +name+ "&course=" +course+ "&print=bag\" target=\"_blank\" title=\"Print Bag Room Report\" alt=\"Bag Report\">");
                    out.println("Print Bag Report</a><br><br>");
                }
                out.println("<a href=\"/" +rev+ "/servlet/Proshop_events2?name=" +name+ "&course=" +course+ "&print=mnum\" target=\"_blank\" title=\"Print Member Number Report\" alt=\"Member Id Report\">");
                out.println("Print Member Id Report</a><br><br>");

                out.println("<a href=\"/" +rev+ "/servlet/Proshop_events2?name=" +name+ "&course=" +course+ "&print=notes\" target=\"_blank\" title=\"List All Player Notes\" alt=\"Player Notes Report\">");
                out.println("List All Notes</a><br><br>");
            }

            if (manageAccess) {
                if (okToMove1 == true) {       // if ok to move and checkin players

                   out.println("<a href=\"/" +rev+ "/servlet/Proshop_evntChkAll?name=" +name+ "&course=" +course+ "\" title=\"Move Players to " + ((activity_id == 0) ? "Tee" : "Time") + " Sheet & Check Them In\" alt=\"Check All In\">");
                   out.println("Move Players & Check All In</a><br><br>");
                }

                if (okToMove2 == true) {       // if ok to move players w/o checkin 

                   out.println("<a href=\"/" +rev+ "/servlet/Proshop_evntChkAll?name=" +name+ "&course=" +course+ "&nocheck=yes\" title=\"Move Players to " + ((activity_id == 0) ? "Tee" : "Time") + " Sheet w/o Check In\" alt=\"Move All w/o Check In\">");
                   out.println("Move Players & Do Not Check In</a><br><br>");
                }
            }

            if (USE_NEW_SIGNUP && export_type != 0 && activity_id == 0) {
                out.println("<a href=\"/" +rev+ "/servlet/Proshop_events2?name=" +name+ "&course=" +course+ "&export\" target=\"_blank\" title=\"Export Players\" alt=\"Export Players\">");
                out.println("Export Players</a><br><br>");
            }
            
            // link to new drag-n-drop feature
            if (manageAccess && activity_id == 0) {
                int tmp_index = SystemUtils.getIndexFromToday(date, con);
                if (tmp_index >= 0) {
                    out.println("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?mode=EVENT&index=" + tmp_index + "&name=" +name+ "&course=" +course+ "&hide=1\" title=\"Manually Move Players to Tee Sheet\" alt=\"Drag-n-Drop\" target=\"_top\">");
                    out.println("Drag-n-Drop</a><br><br>");
                }
            }
            out.println("</font></td></tr></table>");
            out.println("</td>");                                 // end of column for control panel

            out.println("<td align=\"left\" width=\"40\">");              // separator
            out.println("&nbsp;</td>");

            out.println("<td align=\"left\" valign=\"top\">");     // column for event header

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_evntSignUp\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         }

         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"530\" cellpadding=\"5\" cellspacing=\"3\" valign=\"top\">");
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<b>" + name + "</b>");
            out.println("</font></td></tr>");
            out.println("<tr><td align=\"left\">");
            out.println("<font size=\"2\">");
            out.println("<b>Date:</b>&nbsp;&nbsp; " + ((season == 0) ? month + "/" + day + "/" + year : "Season Long"));
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            if (season == 0) {
                out.println("<b>Time:</b>&nbsp;&nbsp; " + act_hr + ":" + Utilities.ensureDoubleDigit(act_min) + " " + act_ampm);
                if (activity_id == 0) {
                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                    if (type != 0) {
                       out.println("<b>Type:</b>&nbsp;&nbsp; Shotgun<br><br>");
                    } else {
                       out.println("<b>Type:</b>&nbsp;&nbsp; Tee Times<br><br>");
                    }
                } else {
                   out.println("<br><br>");
                }
            } else {
               out.println("<br><br>");
            }
            if (activity_id == 0) {
                if (!course.equals( "" )) {

                   if (club.equals("congressional")) {
                       out.println("<b>Course:</b>&nbsp;&nbsp; " + congressionalCustom.getFullCourseName(date, day, course) + "</b>");
                   } else {
                       out.println("<b>Course:</b>&nbsp;&nbsp; " + course);
                   }

                   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                }
                out.println("<b>Front/Back:</b>&nbsp;&nbsp; " + fb + "<br><br>");
            }

            if (gstOnly == 0) {     // if not guest only event
              
               out.println("<b>Format:</b>&nbsp;&nbsp; " + format);
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<b>Gender:</b>&nbsp;&nbsp; " + Labels.gender_opts[gender] + "<br><br>");
               out.println("<b>Teams Selected By:</b>&nbsp;&nbsp; " + pairings);
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<b># of Teams:</b>&nbsp;&nbsp; " + max);
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<b>Team Size:</b>&nbsp;&nbsp; " + size);
               //out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<br><br><b>Min. Sign-Up Size:</b>&nbsp;&nbsp; " + minsize);
               if (activity_id == 0) {
                   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                   out.println("<b>Holes:</b>&nbsp;&nbsp; " + holes);
               }
               out.println("<br><br><nobr><b>Guests per Member:</b>&nbsp;&nbsp;" + guests);
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<b>Cost per Guest:</b>&nbsp;&nbsp;" + gstcost);
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<b>Cost per Member:</b>&nbsp;&nbsp;" + memcost + "</nobr><br><br>");
               
               out.println("<b>Member Sign Up Starts at:</b>&nbsp;&nbsp; " +
                       "" + ((signUpOK) ? "" : "<span style='background-color:yellow'>") + 
                       su_hr + ":" + Utilities.ensureDoubleDigit(su_min) + " " + su_ampm +  " on " + su_month + "/" + su_day + "/" + su_year + "" +
                       "" + ((signUpOK) ? "" : "</span>"));
               
               out.println("<br><br>");
               
               out.println("<b>Must Sign Up By:</b>&nbsp;&nbsp; " +
                       "" + ((lockout) ? "<span style='background-color:yellow'>" : "") + 
                       c_hr + ":" + Utilities.ensureDoubleDigit(c_min) + " " + c_ampm +  " on " + c_month + "/" + c_day + "/" + c_year + "" +
                       "" + ((lockout) ? "</span>" : ""));
               
               out.println("<br><br>");
               out.println("<b>Itinerary:</b>&nbsp;&nbsp; " + itin + "<br>");
               out.println("</font></td></tr>");

               if (updateAccess) {
                   
                   out.println("<tr><td align=\"center\">");
                   out.println("<font size=\"2\">");

                   if (signup != 0) {

                      if (pairings.equalsIgnoreCase( "Proshop" )) {

                         if (count_reg >= (max * size)) {  // if no room for more members (max = # of teams, size = # per team)

                            out.print("<b>Warning:</b> This event already has " + count_reg + " members registered");
                            if (count_wait > 0) {
                                out.print(" and " + count_wait + " members on the wait list.<br><br>");
                            } else { out.println(".<br><br>"); }
                         }

                      } else {

                         if (teams_reg >= max) {         // if no room for more teams

                            out.print("<b>Warning:</b> This event already has " + teams_reg + " teams registered");
                            if (teams_wait > 0) {
                                out.print(" and " + teams_wait + " teams on the wait list.<br><br>");
                            } else { out.println(".<br><br>"); }
                         }
                      }
                      out.println("<input type=\"hidden\" name=\"new\" value=\"new\">");
                      out.println("To update an existing " + ((pairings.equals( "Member" )) ? "team" : "entry") + " click on the Select button in the table below.");

                      if (pairings.equalsIgnoreCase( "Proshop" ) && (count_reg >= (max * size))) {

                          out.println("<br>To add a new entry to the wait list, click here: ");

                      } else if (pairings.equalsIgnoreCase( "Proshop" )) {

                          out.println("<br>To register a new entry, click here: ");

                      } else if (pairings.equals( "Member" ) && (teams_reg >= max)) {

                          out.println("<br>To add a team to the wait list, click here: ");

                      } else {

                          out.println("<br>To register a new team, click here: ");

                      }

                      if (signUpOK && !lockout) {
                          out.println("<input type=\"submit\" value=\"New Team\" style=\"background:#B0C4DE\">");
                      } else if (!signUpOK) {
                          out.println("<input type=\"submit\" value=\"New Team\" style=\"background:yellow\" onclick=\"return confirm('Sign up has not started for this event yet.\\n\\nAre you sure you want to continue?')\">");
                      } else if (lockout) {
                          out.println("<input type=\"submit\" value=\"New Team\" style=\"background:yellow\" onclick=\"return confirm('The sign up period has already passed for this event.\\n\\nAre you sure you want to continue?')\">");
                      }

                   } else {

                      out.println("Event is not configured for member sign-up.<br>");
                      out.println("Use the tee sheet to register players for this event.<br>");

                      if ( (club.equals("tcclub")) && signup == 0) {

                          //tmp_form_closed = true;
                          //out.println("</form>");

                          out.println("<form action=\"/" +rev+ "/servlet/Proshop_evntChkAll\" method=\"post\" target=\"bot\">");
                          out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                          out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                          out.println("<input type=\"hidden\" name=\"gstOnly\" value=\"gstOnly\">");
                          out.println("<input type=\"submit\" value=\"Register Guests\" name=\"check\" style=\"text-decoration:underline; background:#8B8970\">");
                          //out.println("</form>");
                      }
                   }
               }
               out.println("<br>");
               out.println("</font></td></tr>");
                 
            } else {       // if guest only event

               out.println("<b>Format:</b>&nbsp;&nbsp;Guest Only (Outside) Event<br><br>");
               out.println("</font></td></tr>");

               out.println("<form action=\"/" +rev+ "/servlet/Proshop_evntChkAll\" method=\"post\" target=\"bot\">");
               out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"gstOnly\" value=\"gstOnly\">");

               // Only allow guest registration if EVNTSUP_UPDATE access is allowed
               if (updateAccess) {
                   out.println("<tr><td align=\"center\">");
                   out.println("<font size=\"2\">");
                   out.println("Since this is an outside event (no members), there is no need to sign up players.");
                   out.println("<br>However, in order to maintain accurate records you should register the guests.");
                   out.println("<br><br>This can be done the day of the event or at a later date (not before).");
                   out.println("<br><br>To register/record the guest rounds, click here: ");

                   out.println("<input type=\"submit\" value=\"Register Guests\" name=\"check\" style=\"text-decoration:underline; background:#8B8970\">");
                   out.println("<br>");
                   out.println("</font></td></tr>");
               }
            }
            
            out.println("</table></form>");
            //if (!tmp_form_closed) out.println("</form>");             // end of header table

            out.println("</font></td>");
            out.println("</tr></table>");             // end of table for cmd table and header table
            out.println("</td></tr>");                 // end of first row of page table

            if (gstOnly == 0) {     // if not guest only event

               out.println("<tr><td align=\"center\">");  // 2nd row
               out.println("<font size=\"2\">");

               if (count_reg == 0) {      // if no one signed up

                  out.println("<br><br>There are currently no players registered for this event.<br>");

               } else {

                  if (pairings.equals( "Member" )) {

                     out.print("<br>There are currently " + teams_reg + " teams (" + count_reg + " players) registered for this event");
                     if (teams_wait > 0) {
                         out.print(" and " + teams_wait + " teams (" + count_wait + " players) on the wait list.<br><br>");
                     } else { out.println(".<br><br>"); }

                  } else {

                     out.println("<br>There are currently " + count_reg + " players registered for this event");
                     if (count_wait > 0) {
                        out.println(" and " + count_wait + " players on the wait list.<br><br>");
                     } else { out.println(".<br><br>"); }
                  }

                  if (teams_reg > 5) {

                     out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_events1\">");
                     out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                     out.println("</form>");
                  }

                  out.println("<br><b>Players currently signed up for this event:</b>");
                  //
                  //  Get all entries for this Event Sign Up Sheet
                  //
                  PreparedStatement pstmte = con.prepareStatement (
                     "SELECT * FROM evntsup2b " +
                     "WHERE name = ? AND inactive = 0 ORDER BY r_date, r_time");

                  pstmte.clearParameters();        // clear the parms
                  pstmte.setString(1, name);
                  rs2 = pstmte.executeQuery();      // execute the prepared pstmt

                  out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" valign=\"top\">");
                  out.println("<tr bgcolor=\"#336633\">");

                     if (updateAccess) {
                         out.println("<td align=\"center\">");
                         out.println("<font color=\"#FFFFFF\" size=\"2\">");
                         out.println("<u><b>Select</b></u>");
                         out.println("</font></td>");
                     }

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Player 1</b></u> ");
                     if (disp_hndcp == true) {
                        out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
                     }
                     out.println("</font></td>");
                     
                     if (season == 0 && activity_id == 0) {
                         out.println("<td align=\"center\">");
                         out.println("<font color=\"#FFFFFF\" size=\"1\">");
                         out.println("<u><b>C/W</b></u>");
                         out.println("</font></td>");
                     }

                  if (size > 1) {

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Player 2</b></u> ");
                     if (disp_hndcp == true) {
                        out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
                     }
                     out.println("</font></td>");

                     if (season == 0 && activity_id == 0) {
                         out.println("<td align=\"center\">");
                         out.println("<font color=\"#FFFFFF\" size=\"1\">");
                         out.println("<u><b>C/W</b></u>");
                         out.println("</font></td>");
                     }

                  }
                  if (size > 2) {

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Player 3</b></u> ");
                     if (disp_hndcp == true) {
                        out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
                     }
                     out.println("</font></td>");

                     if (season == 0 && activity_id == 0) {
                         out.println("<td align=\"center\">");
                         out.println("<font color=\"#FFFFFF\" size=\"1\">");
                         out.println("<u><b>C/W</b></u>");
                         out.println("</font></td>");
                     }
                  }
                  if (size > 3) {

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Player 4</b></u> ");
                     if (disp_hndcp == true) {
                        out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
                     }
                     out.println("</font></td>");

                     if (season == 0 && activity_id == 0) {
                         out.println("<td align=\"center\">");
                         out.println("<font color=\"#FFFFFF\" size=\"1\">");
                         out.println("<u><b>C/W</b></u>");
                         out.println("</font></td>");
                     }
                  }
                  if (size > 4) {

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Player 5</b></u> ");
                     if (disp_hndcp == true) {
                        out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
                     }
                     out.println("</font></td>");

                     if (season == 0 && activity_id == 0) {
                         out.println("<td align=\"center\">");
                         out.println("<font color=\"#FFFFFF\" size=\"1\">");
                         out.println("<u><b>C/W</b></u>");
                         out.println("</font></td>");
                     }
                  }

                  out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Registered</b></u>");
                  out.println("</font></td>");

                  out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Status</b></u>");
                  out.println("</font></td>");

                  out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"1\">");
                  out.println("<u><b>N</b></u>");
                  out.println("</font></td>");

                  out.println("</tr>");

                  //
                  //  get all the players and list them
                  //
                  while (rs2.next()) {

                     player1 = rs2.getString("player1");
                     player2 = rs2.getString("player2");
                     player3 = rs2.getString("player3");
                     player4 = rs2.getString("player4");
                     player5 = rs2.getString("player5");
                     user1 = rs2.getString("username1");
                     user2 = rs2.getString("username2");
                     user3 = rs2.getString("username3");
                     user4 = rs2.getString("username4");
                     user5 = rs2.getString("username5");
                     p1cw = rs2.getString("p1cw");
                     p2cw = rs2.getString("p2cw");
                     p3cw = rs2.getString("p3cw");
                     p4cw = rs2.getString("p4cw");
                     p5cw = rs2.getString("p5cw");
                     in_use = rs2.getInt("in_use");
                     hndcp1 = rs2.getFloat("hndcp1");
                     hndcp2 = rs2.getFloat("hndcp2");
                     hndcp3 = rs2.getFloat("hndcp3");
                     hndcp4 = rs2.getFloat("hndcp4");
                     hndcp5 = rs2.getFloat("hndcp5");
                     notes = rs2.getString("notes");
                     id = rs2.getInt("id");
                     r_date = rs2.getLong("r_date");          // date/time registered
                     r_time = rs2.getInt("r_time");
                     wait = rs2.getInt("wait");               // wait list indicator
                     moved = rs2.getInt("moved");             // moved to tee sheet indicator

                     //
                     //   skip this entry if all players are empty (someone cancelled)
                     //
                     if (!player1.equals( "" ) || !player2.equals( "") ||
                         !player3.equals( "" ) || !player4.equals( "") || !player5.equals( "")) {

                        //
                        //  set up some fields needed for the table
                        //
                        submit = "id:" + id;       // create a name for the submit button (to pass the id)

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

                        if (disp_mnum == true) {
                           
                           if (user1.equals("")) {
                              mnum1 = "";
                           } else {
                              mnum1 = getMnum(user1, con);      // get mNum
                           }
                           if (user2.equals("")) {
                              mnum2 = "";
                           } else {
                              mnum2 = getMnum(user2, con);      // get mNum
                           }
                           if (user3.equals("")) {
                              mnum3 = "";
                           } else {
                              mnum3 = getMnum(user3, con);      // get mNum
                           }
                           if (user4.equals("")) {
                              mnum4 = "";
                           } else {
                              mnum4 = getMnum(user4, con);      // get mNum
                           }
                           if (user5.equals("")) {
                              mnum5 = "";
                           } else {
                              mnum5 = getMnum(user5, con);      // get mNum
                           }
                        }
                              

                        //
                        //  get date and time members first registered
                        //
                        r_yr = r_date / 10000;
                        temp = r_yr * 10000;
                        r_mm = r_date - temp;
                        temp = r_mm / 100;
                        temp = temp * 100;
                        r_dd = r_mm - temp;
                        r_mm = r_mm / 100;

                        r_hr = r_time / 100;
                        r_min = r_time - (r_hr * 100);

                        r_ampm = " AM";
                        if (r_hr == 12) {
                           r_ampm = " PM";
                        }
                        if (r_hr > 12) {
                           r_ampm = " PM";
                           r_hr = r_hr - 12;    // convert to conventional time
                        }
                        if (r_hr == 0) {
                           r_hr = 12;
                        }

                     out.println("<tr>");

                        //
                        //  list the current teams (use 'submit' to id the entry)
                        //
                     if (in_use == 0) {
                         
                        if (updateAccess) {
                            out.println("<form action=\"/" +rev+ "/servlet/Proshop_evntSignUp\" method=\"post\" target=\"_top\">");
                            out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");

                            out.println("<td align=\"center\">");
                            out.println("<font size=\"2\">");
                            out.println("<input " + ((use_lockout && lockout) ? "disabled" : "") + " type=\"submit\" value=\"Select\" name=\"" + submit + "\" id=\"" + submit + "\" style=\"background:#B0C4DE\">");
                            out.println("</font></td></form>");
                        }

                     } else {

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println("Busy");
                        out.println("</font></td>");
                     }

                        //
                        //  Add Player 1
                        //
                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");

                        if (!player1.equals("")) {

                           if (player1.equalsIgnoreCase("x")) {   // if 'x'

                              out.println(player1);

                           } else {       // not 'x'

                              if (disp_mnum == true && !mnum1.equals("")) {
                                 player1 = player1 + " " + mnum1;
                              }
                           
                              if (disp_hndcp == false) {
                                 out.println(player1);
                              } else {
                                 if ((hndcp1 == 99) || (hndcp1 == -99)) {
                                    out.println(player1);
                                 } else {
                                    if (hndcp1 <= 0) {
                                       hndcp1 = 0 - hndcp1;                       // convert to non-negative
                                    }
                                    //hndcp = Math.round(hndcp1);                   // round it off
                                    out.println(player1 + "  " + hndcp1);
                                 }
                              }
                           }
                        } else {     // player is empty

                           out.println("&nbsp;");
                        }
                        out.println("</font></td>");
                        
                     if (season == 0 && activity_id == 0) {
                            
                        out.println("<td bgcolor=\"white\" align=\"center\">");

                        if ((!player1.equals("")) && (!player1.equalsIgnoreCase( "x" ))) {
                           out.println("<font size=\"1\">");
                           out.println(p1cw);
                        } else {
                           out.println("<font size=\"2\">");
                           out.println("&nbsp;");
                        }
                        out.println("</font></td>");
                     }

                     if (size > 1) {

                        //
                        //  Add Player 2
                        //
                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");

                        if (!player2.equals("")) {

                           if (player2.equalsIgnoreCase("x")) {   // if 'x'

                              out.println(player2);

                           } else {       // not 'x'

                              if (disp_mnum == true && !mnum2.equals("")) {
                                 player2 = player2 + " " + mnum2;
                              }
                           
                              if (disp_hndcp == false) {
                                 out.println(player2);
                              } else {
                                 if ((hndcp2 == 99) || (hndcp2 == -99)) {
                                    out.println(player2);
                                 } else {
                                    if (hndcp2 <= 0) {
                                       hndcp2 = 0 - hndcp2;                       // convert to non-negative
                                    }
                                    //hndcp = Math.round(hndcp2);                   // round it off
                                    out.println(player2 + "  " + hndcp2);
                                 }
                              }
                           }
                        } else {     // player is empty

                           out.println("&nbsp;");
                        }
                        out.println("</font></td>");
                        if (season == 0 && activity_id == 0) {
                        
                           out.println("<td bgcolor=\"white\" align=\"center\">");

                           if ((!player2.equals("")) && (!player2.equalsIgnoreCase( "x" ))) {
                              out.println("<font size=\"1\">");
                              out.println(p2cw);
                           } else {
                              out.println("<font size=\"2\">");
                              out.println("&nbsp;");
                           }
                           out.println("</font></td>");
                        }
                     }
                     if (size > 2) {

                        //
                        //  Add Player 3
                        //
                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");

                        if (!player3.equals("")) {

                           if (player3.equalsIgnoreCase("x")) {   // if 'x'

                              out.println(player3);

                           } else {       // not 'x'

                              if (disp_mnum == true && !mnum3.equals("")) {
                                 player3 = player3 + " " + mnum3;
                              }
                           
                              if (disp_hndcp == false) {
                                 out.println(player3);
                              } else {
                                 if ((hndcp3 == 99) || (hndcp3 == -99)) {
                                    out.println(player3);
                                 } else {
                                    if (hndcp3 <= 0) {
                                       hndcp3 = 0 - hndcp3;                       // convert to non-negative
                                    }
                                    //hndcp = Math.round(hndcp3);                   // round it off
                                    out.println(player3 + "  " + hndcp3);
                                 }
                              }
                           }
                        } else {     // player is empty

                           out.println("&nbsp;");
                        }
                        out.println("</font></td>");
                        if (season == 0 && activity_id == 0) {
                            
                            out.println("<td bgcolor=\"white\" align=\"center\">");

                            if ((!player3.equals("")) && (!player3.equalsIgnoreCase( "x" ))) {
                               out.println("<font size=\"1\">");
                               out.println(p3cw);
                            } else {
                               out.println("<font size=\"2\">");
                               out.println("&nbsp;");
                            }
                            out.println("</font></td>");
                        }
                     }
                     if (size > 3) {

                        //
                        //  Add Player 4
                        //
                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");

                        if (!player4.equals("")) {

                           if (player4.equalsIgnoreCase("x")) {   // if 'x'

                              out.println(player4);

                           } else {       // not 'x'

                              if (disp_mnum == true && !mnum4.equals("")) {
                                 player4 = player4 + " " + mnum4;
                              }
                           
                              if (disp_hndcp == false) {
                                 out.println(player4);
                              } else {
                                 if ((hndcp4 == 99) || (hndcp4 == -99)) {
                                    out.println(player4);
                                 } else {
                                    if (hndcp4 <= 0) {
                                       hndcp4 = 0 - hndcp4;                       // convert to non-negative
                                    }
                                    //hndcp = Math.round(hndcp4);                   // round it off
                                    out.println(player4 + "  " + hndcp4);
                                 }
                              }
                           }
                        } else {     // player is empty

                           out.println("&nbsp;");
                        }
                        out.println("</font></td>");
                        if (season == 0 && activity_id == 0) {
                            out.println("<td bgcolor=\"white\" align=\"center\">");

                            if ((!player4.equals("")) && (!player4.equalsIgnoreCase( "x" ))) {
                               out.println("<font size=\"1\">");
                               out.println(p4cw);
                            } else {
                               out.println("<font size=\"2\">");
                               out.println("&nbsp;");
                            }
                            out.println("</font></td>");
                        }
                     }
                     if (size > 4) {

                        //
                        //  Add Player 5
                        //
                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");

                        if (!player5.equals("")) {

                           if (player5.equalsIgnoreCase("x")) {   // if 'x'

                              out.println(player5);

                           } else {       // not 'x'

                              if (disp_mnum == true && !mnum5.equals("")) {
                                 player5 = player5 + " " + mnum5;
                              }
                           
                              if (disp_hndcp == false) {
                                 out.println(player5);
                              } else {
                                 if ((hndcp5 == 99) || (hndcp5 == -99)) {
                                    out.println(player5);
                                 } else {
                                    if (hndcp5 <= 0) {
                                       hndcp5 = 0 - hndcp5;                       // convert to non-negative
                                    }
                                    //hndcp = Math.round(hndcp5);                   // round it off
                                    out.println(player5 + "  " + hndcp5);
                                 }
                              }
                           }
                        } else {     // player is empty

                           out.println("&nbsp;");
                        }
                        out.println("</font></td>");
                        if (season == 0 && activity_id == 0) {
                            out.println("<td bgcolor=\"white\" align=\"center\">");

                            if ((!player5.equals("")) && (!player5.equalsIgnoreCase( "x" ))) {
                               out.println("<font size=\"1\">");
                               out.println(p5cw);
                            } else {
                               out.println("<font size=\"2\">");
                               out.println("&nbsp;");
                            }
                            out.println("</font></td>");
                        }
                     }

                        //
                        //  add date member registered
                        //
                        out.println("<td align=\"center\">");
                        out.println("<font size=\"1\">");
                        out.println(r_mm + "/" + r_dd + "/" + r_yr + "<br>");
                        out.println(r_hr + ":" + Utilities.ensureDoubleDigit(r_min) + r_ampm);
                        out.println("</font></td>");
                          
                        //
                        //  add status (on wait list?)
                        //
                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        if (moved == 1) {
                           out.println((activity_id == 0) ? "On Tee Sheet" : "On Time Sheet");
                        } else if (wait == 0) {
                           out.println("Registered");
                        } else {
                           out.println("Wait List");
                        }
                        out.println("</font></td>");

                        //
                        //  Last column for 'Notes' box
                        //
                        out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\" target=\"_blank\">");
                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        if (!notes.equals("")) {

                           out.println("<input type=\"hidden\" name=\"notes\" value=\"yes\">");
                           out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                           out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                           out.println("<input type=\"hidden\" name=\"id\" value=\"" + id + "\">");
                           out.println("<input type=\"image\" src=\"/" +rev+ "/images/notes.jpg\" border=\"0\" name=\"showNotes\" title=\"Click here to view notes.\">");
                        } else {
                            out.println("&nbsp;");
                        }
                        out.println("</font></td></form>");    

                        out.println("</tr>");              // end of row

                     }  // end of IF entry is empty (all players = null)

                  }                   // end of while
                  pstmte.close();

                  out.println("</table>");              // end of player table
               }

               out.println("</font></td></tr>");

            }  // end of if gstOnly

         out.println("</table>");                   // end of main page table

         out.println("<br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_events1\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");

      } else {     // print= specified

         //
         //************************************************
         //  User elected to print the sign up sheet
         //************************************************
         //
         String printType = req.getParameter("print");       // get the report type requested
         String bag = "";
         String mnum = "";

         try{
            if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format

               resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
            }
         }
         catch (Exception exc) {
         }

         out.println(SystemUtils.HeadTitle("Proshop Event Sign Up Page"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
         SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
         out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

         out.println("<table border=\"0\" valign=\"top\">");       // table for main page
         out.println("<tr><td align=\"center\" valign=\"top\">");
         out.println("<font size=\"2\">");

         if (req.getParameter("excel") == null) {     // if normal request

            out.println("<table border=\"0\" align=\"center\"><tr>");
            out.println("<td align=\"center\">");
            out.println("<form method=\"link\" action=\"javascript:self.print()\">");
            if (printType.equals( "print" ) || printType.equals( "full" )) {
               out.println("<button type=\"submit\" style=\"text-decoration:underline; background:#8B8970\">Print This Event Sheet</button>");
            }
            if (printType.equals( "bag" )) {
               out.println("<button type=\"submit\" style=\"text-decoration:underline; background:#8B8970\">Print This Bag Report</button>");
            }
            if (printType.equals( "mnum" )) {
               out.println("<button type=\"submit\" style=\"text-decoration:underline; background:#8B8970\">Print This Member Id Report</button>");
            }
            if (printType.equals( "alpha" )) {
               out.println("<button type=\"submit\" style=\"text-decoration:underline; background:#8B8970\">Print This Alphabetical List</button>");
            }
            if (printType.equals( "notes" )) {
               out.println("<button type=\"submit\" style=\"text-decoration:underline; background:#8B8970\">Print This List of Notes</button>");
            }
            out.println("</td></form>");

            out.println("<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>");

            if (printType.equals( "print" )) { 
                
               //
               //  Col for 'Alphabetical List' button
               //
               out.println("<td align=\"center\"><font size=\"2\">");
                  out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
                  out.println("<input type=\"hidden\" name=\"name\" value=\"" +name+ "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
                  out.println("<input type=\"hidden\" name=\"print\" value=\"alpha\">");
                  out.println("<input type=\"submit\" value=\"Alphabetical Member List\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("</font></td>");
               out.println("</form>");
               
               if (USE_NEW_SIGNUP) {
               
               //
               //  Col for 'Detailed (Full) List' button
               //
               out.println("<td align=\"center\"><font size=\"2\">");
                  out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
                  out.println("<input type=\"hidden\" name=\"name\" value=\"" +name+ "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
                  out.println("<input type=\"hidden\" name=\"print\" value=\"full\">");
                  out.println("<input type=\"submit\" value=\"Detailed Listing\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("</font></td>");
               out.println("</form>");
               
               }
            }
            
            if (printType.equals( "full" )) {
               //
               //  Col for 'Standard List' button
               //
               out.println("<td align=\"center\"><font size=\"2\">");
                  out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
                  out.println("<input type=\"hidden\" name=\"name\" value=\"" +name+ "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
                  out.println("<input type=\"hidden\" name=\"print\" value=\"print\">");
                  out.println("<input type=\"submit\" value=\"Standard List\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("</font></td>");
               out.println("</form>");
            }

            //
            //  Col for Excel button
            //
            if (!printType.equals( "full" )) {
                out.println("<td align=\"center\"><font size=\"2\">");
                   out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
                   out.println("<input type=\"hidden\" name=\"name\" value=\"" +name+ "\">");
                   out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
                   out.println("<input type=\"hidden\" name=\"print\" value=\"" +printType+ "\">");
                   out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
                   out.println("<input type=\"submit\" value=\"Excel Format\" style=\"text-decoration:underline; background:#8B8970\">");
                   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                out.println("</font></td>");
                out.println("</form>");
            }
            
            out.println("<td align=\"left\"><form>");
            out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" value=\"Close\" onclick='self.close()' alt=\"Close\">");
            out.println("</td></form></tr></table>");
         }

         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"500\" cellpadding=\"5\" cellspacing=\"3\" valign=\"top\">");
         out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"3\">");
         out.println("<b>" + name + "</b>");
         out.println("</font></td></tr>");
         out.println("<tr><td align=\"left\">");
         out.println("<font size=\"2\">");
         out.println("<b>Date:</b>&nbsp;&nbsp; " + ((season == 0) ? month + "/" + day + "/" + year : "Season Long"));
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         if (season == 0) {
            out.println("<b>Time:</b>&nbsp;&nbsp; " + act_hr + ":" + Utilities.ensureDoubleDigit(act_min) + " " + act_ampm);
            if (activity_id == 0) {
                out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                if (type != 0) {
                   out.println("<b>Type:</b>&nbsp;&nbsp; Shotgun<br><br>");
                } else {
                   out.println("<b>Type:</b>&nbsp;&nbsp; Tee Times<br><br>");
                }
            } else {
                out.println("<br><br>");
            }
         } else {
            out.println("<br><br>");
         }
         
         if (activity_id == 0) {
             if (!course.equals( "" )) {

                 if (club.equals("congressional")) {
                     out.println("<b>Course:</b>&nbsp;&nbsp; " + congressionalCustom.getFullCourseName(date, day, course) + "</b>");
                 } else {
                     out.println("<b>Course:</b>&nbsp;&nbsp; " + course);
                 }

                 out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
             }
             out.println("<b>Front/Back:</b>&nbsp;&nbsp; " + fb + "<br><br>");
         }
         if (printType.equals( "print" ) || printType.equals( "alpha" ) || printType.equals( "full" ) ) {
            out.println("<b>Format:</b>&nbsp;&nbsp; " + format);
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<b>Gender:</b>&nbsp;&nbsp; " + Labels.gender_opts[gender] + "<br><br>");
            out.println("<b>Teams Selected By:</b>&nbsp;&nbsp; " + pairings);
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<b>Team Sizes:</b>&nbsp;&nbsp; " + size);
            if (activity_id == 0) {
                out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                out.println("<b>Holes:</b>&nbsp;&nbsp; " + holes);
            }
            out.println("<br><br><b>Guests per Member:</b>&nbsp;&nbsp;" + guests);
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<b>Cost per Guest:</b>&nbsp;&nbsp;" + gstcost);
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<b>Cost per Member:</b>&nbsp;&nbsp;" + memcost + "<br><br>");
            out.println("<b>Must Sign Up By:</b>&nbsp;&nbsp; " + c_hr + ":" + Utilities.ensureDoubleDigit(c_min) + " " + c_ampm +  " on " + c_month + "/" + c_day + "/" + c_year);
            out.println("<br><br>");
            out.println("<b>Itinerary:</b>&nbsp;&nbsp; " + itin + "<br>");
         }
         out.println("</font></td></tr>");
         out.println("</table>");             // end of header table

         out.println("</td></tr>");                 // end of first row of page table

         out.println("<tr><td align=\"center\">");  // 2nd row
         out.println("<font size=\"2\">");

         if (count_reg == 0) {      // if no one signed up

            out.println("<br><br>There are currently no players registered for this event.<br>");

         } else {

            if (count_reg > 5) {

               if (pairings.equals( "Member" )) {

                  out.print("<br>There are currently " + teams_reg + " teams (" + count_reg + " players) registered for this event");
                  if (teams_wait > 0) {
                      out.print(" and " + teams_wait + " teams (" + count_wait + " players) on the wait list.<br><br>");
                  } else { out.println(".<br><br>"); }

               } else {

                  out.println("<br>There are currently " + count_reg + " players registered for this event");
                  if (count_wait > 0) {
                     out.println(" and " + count_wait + " players on the wait list.<br><br>");
                  } else { out.println(".<br><br>"); }
               }

               /*
               out.println("<br>There are currently " + count_reg + " sign-ups registered for this event");
               if (count_wait > 0) {
                  out.println(" and " + count_wait + " sign-ups on the wait list.<br>");
               } else { out.println(".<br>"); }
               */
            }

            out.println("<br>");

            //
            //  Get all entries for this Event Sign Up Sheet
            //
            PreparedStatement pstmte = con.prepareStatement (
               "SELECT * FROM evntsup2b " +
               "WHERE name = ? AND courseName = ? AND inactive = 0 ORDER BY r_date, r_time");

            pstmte.clearParameters();           // clear the parms
            pstmte.setString(1, name);
            pstmte.setString(2, course);        // why are we passing course name here? name is forced unique
            rs2 = pstmte.executeQuery();        // execute the prepared pstmt

            if (printType.equals( "alpha" )) {

               //
               //  Find all the members in the event signup table and copy them into a temp table
               //
               while (rs2.next()) {

                  player1 = rs2.getString("player1");
                  player2 = rs2.getString("player2");
                  player3 = rs2.getString("player3");
                  player4 = rs2.getString("player4");
                  player5 = rs2.getString("player5");
                  user1 = rs2.getString("username1");
                  user2 = rs2.getString("username2");
                  user3 = rs2.getString("username3");
                  user4 = rs2.getString("username4");
                  user5 = rs2.getString("username5");

                  p1 = "";
                  p2 = "";
                  p3 = "";
                  p4 = "";
                  p5 = "";

                  if (!user1.equals( "" )) {        // if member

                     PreparedStatement pstmt3 = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b " +
                        "WHERE username = ?");

                     pstmt3.clearParameters();        // clear the parms
                     pstmt3.setString(1, user1);
                     rs3 = pstmt3.executeQuery();      // execute the prepared pstmt

                     if (rs3.next()) {

                        String lname = rs3.getString(1);
                        String fname = rs3.getString(2);
                        String mname = rs3.getString(3);

                        // Get the member's full name.......(lname, fname mi)

                        StringBuffer mem_name = new StringBuffer(lname);  // get last name

                        mem_name.append(", " + fname);                     // first name

                        if (!mname.equals( "" )) {
                           mem_name.append(" " +mname);                        // mi
                        }
                        p1 = mem_name.toString();                    // convert to one string
                     
                        //
                        //  check for guests with this member
                        //
                        if (!player2.equals( "" ) && user2.equals( "" )) {   // if player 2 is not a member
                          
                           p2 = player2;                  // save guest name if present
                           guestsFound = true;

                           if (!player3.equals( "" ) && user3.equals( "" )) {  // if player 3 is not a member

                              p3 = player3;                  // save guest name if present

                              if (!player4.equals( "" ) && user4.equals( "" )) {  // if player 4 is not a member

                                 p4 = player4;                  // save guest name if present

                                 if (!player5.equals( "" ) && user5.equals( "" )) {  // if player 5 is not a member

                                    p5 = player5;                  // save guest name if present
                                 }
                              }
                           }
                        }
                        //
                        //  Now save this entry to be displayed below (must save to alphabetize)
                        //
                        PreparedStatement pstmta = con.prepareStatement (
                          "INSERT INTO eventa4 (name, p1, p2, p3, p4, p5) " +
                          "VALUES (?,?,?,?,?,?)");

                        pstmta.clearParameters();        // clear the parms
                        pstmta.setString(1, name);       // put the parm in pstmta
                        pstmta.setString(2, p1);
                        pstmta.setString(3, p2);
                        pstmta.setString(4, p3);
                        pstmta.setString(5, p4);
                        pstmta.setString(6, p5);

                        pstmta.executeUpdate();          // execute the prepared stmt

                        pstmta.close();   // close the stmt

                     }
                     pstmt3.close();
                  }                        // end of IF user1
                    
                  //
                  //  Now check player 2 for member
                  //
                  if (!user2.equals( "" )) {        // if member

                     p1 = "";      // init 
                     p2 = "";
                     p3 = "";
                     p4 = "";
                     p5 = "";
                       
                     PreparedStatement pstmt3 = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b " +
                        "WHERE username = ?");

                     pstmt3.clearParameters();        // clear the parms
                     pstmt3.setString(1, user2);
                     rs3 = pstmt3.executeQuery();      // execute the prepared pstmt

                     if (rs3.next()) {

                        String lname = rs3.getString(1);
                        String fname = rs3.getString(2);
                        String mname = rs3.getString(3);

                        // Get the member's full name.......(lname, fname mi)

                        StringBuffer mem_name = new StringBuffer(lname);  // get last name

                        mem_name.append(", " + fname);                     // first name

                        if (!mname.equals( "" )) {
                           mem_name.append(" " +mname);                        // mi
                        }
                        p1 = mem_name.toString();                    // convert to one string

                        //
                        //  check for guests with this member
                        //
                        if (!player3.equals( "" ) && user3.equals( "" )) {   // if player 3 is not a member

                           p2 = player3;                  // save guest name if present
                           guestsFound = true;

                           if (!player4.equals( "" ) && user4.equals( "" )) {  // if player 4 is not a member

                              p3 = player4;                  // save guest name if present

                              if (!player5.equals( "" ) && user5.equals( "" )) {  // if player 5 is not a member

                                 p4 = player5;                  // save guest name if present
                              }
                           }
                        }
                        //
                        //  Now save this entry to be displayed below (must save to alphabetize)
                        //
                        PreparedStatement pstmta = con.prepareStatement (
                          "INSERT INTO eventa4 (name, p1, p2, p3, p4, p5) " +
                          "VALUES (?,?,?,?,?,?)");

                        pstmta.clearParameters();        // clear the parms
                        pstmta.setString(1, name);       // put the parm in pstmta
                        pstmta.setString(2, p1);
                        pstmta.setString(3, p2);
                        pstmta.setString(4, p3);
                        pstmta.setString(5, p4);
                        pstmta.setString(6, p5);

                        pstmta.executeUpdate();          // execute the prepared stmt

                        pstmta.close();   // close the stmt

                     }
                     pstmt3.close();
                  }                        // end of IF user2

                  //
                  //  Now check player 3 for member
                  //
                  if (!user3.equals( "" )) {        // if member

                     p1 = "";      // init
                     p2 = "";
                     p3 = "";
                     p4 = "";
                     p5 = "";

                     PreparedStatement pstmt3 = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b " +
                        "WHERE username = ?");

                     pstmt3.clearParameters();        // clear the parms
                     pstmt3.setString(1, user3);
                     rs3 = pstmt3.executeQuery();      // execute the prepared pstmt

                     if (rs3.next()) {

                        String lname = rs3.getString(1);
                        String fname = rs3.getString(2);
                        String mname = rs3.getString(3);

                        // Get the member's full name.......(lname, fname mi)

                        StringBuffer mem_name = new StringBuffer(lname);  // get last name

                        mem_name.append(", " + fname);                     // first name

                        if (!mname.equals( "" )) {
                           mem_name.append(" " +mname);                        // mi
                        }
                        p1 = mem_name.toString();                    // convert to one string

                        //
                        //  check for guests with this member
                        //
                        if (!player4.equals( "" ) && user4.equals( "" )) {   // if player 4 is not a member

                           p2 = player4;                  // save guest name if present
                           guestsFound = true;

                           if (!player5.equals( "" ) && user5.equals( "" )) {  // if player 5 is not a member

                              p3 = player5;                  // save guest name if present
                           }
                        }
                        //
                        //  Now save this entry to be displayed below (must save to alphabetize)
                        //
                        PreparedStatement pstmta = con.prepareStatement (
                          "INSERT INTO eventa4 (name, p1, p2, p3, p4, p5) " +
                          "VALUES (?,?,?,?,?,?)");

                        pstmta.clearParameters();        // clear the parms
                        pstmta.setString(1, name);       // put the parm in pstmta
                        pstmta.setString(2, p1);
                        pstmta.setString(3, p2);
                        pstmta.setString(4, p3);
                        pstmta.setString(5, p4);
                        pstmta.setString(6, p5);

                        pstmta.executeUpdate();          // execute the prepared stmt

                        pstmta.close();   // close the stmt

                     }
                     pstmt3.close();
                  }                        // end of IF user3

                  //
                  //  Now check player 4 for member
                  //
                  if (!user4.equals( "" )) {        // if member

                     p1 = "";      // init
                     p2 = "";
                     p3 = "";
                     p4 = "";
                     p5 = "";

                     PreparedStatement pstmt3 = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b " +
                        "WHERE username = ?");

                     pstmt3.clearParameters();        // clear the parms
                     pstmt3.setString(1, user4);
                     rs3 = pstmt3.executeQuery();      // execute the prepared pstmt

                     if (rs3.next()) {

                        String lname = rs3.getString(1);
                        String fname = rs3.getString(2);
                        String mname = rs3.getString(3);

                        // Get the member's full name.......(lname, fname mi)

                        StringBuffer mem_name = new StringBuffer(lname);  // get last name

                        mem_name.append(", " + fname);                     // first name

                        if (!mname.equals( "" )) {
                           mem_name.append(" " +mname);                        // mi
                        }
                        p1 = mem_name.toString();                    // convert to one string

                        //
                        //  check for guests with this member
                        //
                        if (!player5.equals( "" ) && user5.equals( "" )) {   // if player 5 is not a member

                           p2 = player5;                  // save guest name if present
                           guestsFound = true;

                        }
                        //
                        //  Now save this entry to be displayed below (must save to alphabetize)
                        //
                        PreparedStatement pstmta = con.prepareStatement (
                          "INSERT INTO eventa4 (name, p1, p2, p3, p4, p5) " +
                          "VALUES (?,?,?,?,?,?)");

                        pstmta.clearParameters();        // clear the parms
                        pstmta.setString(1, name);       // put the parm in pstmta
                        pstmta.setString(2, p1);
                        pstmta.setString(3, p2);
                        pstmta.setString(4, p3);
                        pstmta.setString(5, p4);
                        pstmta.setString(6, p5);

                        pstmta.executeUpdate();          // execute the prepared stmt

                        pstmta.close();   // close the stmt

                     }
                     pstmt3.close();
                  }                        // end of IF user4

                  //
                  //  Now check player 5 for member
                  //
                  if (!user5.equals( "" )) {        // if member

                     p1 = "";      // init
                     p2 = "";
                     p3 = "";
                     p4 = "";
                     p5 = "";

                     PreparedStatement pstmt3 = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b " +
                        "WHERE username = ?");

                     pstmt3.clearParameters();        // clear the parms
                     pstmt3.setString(1, user5);
                     rs3 = pstmt3.executeQuery();      // execute the prepared pstmt

                     if (rs3.next()) {

                        String lname = rs3.getString(1);
                        String fname = rs3.getString(2);
                        String mname = rs3.getString(3);

                        // Get the member's full name.......(lname, fname mi)

                        StringBuffer mem_name = new StringBuffer(lname);  // get last name

                        mem_name.append(", " + fname);                     // first name

                        if (!mname.equals( "" )) {
                           mem_name.append(" " +mname);                        // mi
                        }
                        p1 = mem_name.toString();                    // convert to one string

                        //
                        //  Now save this entry to be displayed below (must save to alphabetize)
                        //
                        PreparedStatement pstmta = con.prepareStatement (
                          "INSERT INTO eventa4 (name, p1, p2, p3, p4, p5) " +
                          "VALUES (?,?,?,?,?,?)");

                        pstmta.clearParameters();        // clear the parms
                        pstmta.setString(1, name);       // put the parm in pstmta
                        pstmta.setString(2, p1);
                        pstmta.setString(3, p2);
                        pstmta.setString(4, p3);
                        pstmta.setString(5, p4);
                        pstmta.setString(6, p5);

                        pstmta.executeUpdate();          // execute the prepared stmt

                        pstmta.close();   // close the stmt

                     }
                     pstmt3.close();
                  }                        // end of IF user5
               }                           // end of WHILE event signups
               pstmte.close();

               //
               //  Now get the members from the eventa4 table and list them alphabetically
               //
               out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" valign=\"top\">");
               out.println("<tr bgcolor=\"#336633\">");
               out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>Member</b></u> ");
               out.println("</font></td>");

               if (guestsFound == true) {

                  out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Guest(s)</b></u>");
                  out.println("</font></td>");
               }

               PreparedStatement pstmte2 = con.prepareStatement (
                  "SELECT p1, p2, p3, p4, p5 FROM eventa4 " +
                  "WHERE name = ? ORDER BY p1");

               pstmte2.clearParameters();        // clear the parms
               pstmte2.setString(1, name);
               rs3 = pstmte2.executeQuery();      // execute the prepared pstmt

               while (rs3.next()) {

                  p1 = rs3.getString(1);
                  p2 = rs3.getString(2);
                  p3 = rs3.getString(3);
                  p4 = rs3.getString(4);
                  p5 = rs3.getString(5);
                 
                  out.println("</tr><tr>");

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println(p1);
                  out.println("</font></td>");

                  if (guestsFound == true) {

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println(p2);
                     if (!p3.equals( "" )) {
                        out.println(", " +p3);
                     }
                     if (!p4.equals( "" )) {
                        out.println(", " +p4);
                     }
                     if (!p5.equals( "" )) {
                        out.println(", " +p5);
                     }
                     out.println("</font></td>");
                  }

               }                           // end of WHILE members
               pstmte2.close();

               out.println("</tr>");
               out.println("</table>");              // end of player table

               //
               //  Delete the entries from the temporary table
               //
               pstmte2 = con.prepareStatement (
                  "DELETE FROM eventa4 " +
                  "WHERE name = ?");

               pstmte2.clearParameters();        // clear the parms
               pstmte2.setString(1, name);
               pstmte2.executeUpdate();          // execute the prepared pstmt

               pstmte2.close();

            } else {    // not a alphabetical list request

               if (printType.equals( "full" )) {
                   
                  out.println("<style>");
                  out.println(".rptHeaderRow {font-family:verdana;font-size:10pt;font-weight:bold; background-color:#336633;color:white;text-decoration:underline}");
                  out.println(".rptPlayerRow {font-family:verdana;font-size:8pt;font-weight:normal; background-color:#F5F5DC;color:black;text-decoration:none}");
                  out.println(".rptTeamRow {font-family:verdana;font-size:8pt;font-weight:normal; background-color:#8B8970;color:black;text-decoration:none}");
                  out.println("</style>");
                   
                  out.println("<table border=\"1\" cellpadding=\"5\" align=\"center\">");
                  out.println("<tr class=rptHeaderRow align=center>");
                  out.println("<td>#</td>");
                  out.println("<td>Name</td>");
                  if (ask_gender == 1) out.println("<td>Gender</td>");
                  if (ask_hdcp == 1) out.println("<td>HDCP #</td>");
                  if (ask_homeclub == 1) out.println("<td nowrap>Home Club</td>");
                  if (ask_phone == 1) out.println("<td>Phone</td>");
                  if (ask_address == 1) out.println("<td nowrap>Mailing Address</td>");
                  if (ask_email == 1) out.println("<td>Email Address</td>");
                  if (ask_shirtsize == 1) out.println("<td>Shirt Size</td>");
                  if (ask_shoesize == 1) out.println("<td>Show Size</td>");
                  if (ask_otherA1 == 1) out.println("<td>Question #1</td>");
                  if (ask_otherA2 == 1) out.println("<td>Question #2</td>");
                  if (ask_otherA3 == 1) out.println("<td>Question #3</td>");
                  out.println("</tr>");
                  
                  int team_num = 1;
                  int colspan = 2 + ask_gender + ask_hdcp + ask_homeclub + ask_hdcp + ask_address + ask_email + ask_shirtsize + ask_shoesize + ask_otherA1 + ask_otherA2 + ask_otherA3;
                  String tmp_wait = "";
                  
                  while (rs2.next()) {
                  
                    if (rs2.getString("player1") != null && !rs2.getString("player1").equals("")) {
                     
                        tmp_wait = (rs2.getInt("wait") == 1) ? ((size == 1) ? "*" : "(on wait list)") : "";
                            
                        // only print the team separator if no single signups
                        if (size > 1) {
                            out.println("<tr class=rptTeamRow align=center>");
                            out.println("<td colspan=" + colspan + ">Team #" + team_num + " &nbsp;" + tmp_wait + "</td>");
                            out.println("</tr>");
                        }
                        
                        for (int p = 1; p <= size; p++) { // 5

                            if (rs2.getString("player"+p) != null && !rs2.getString("player"+p).equals("")) {

                                out.println("<tr class=rptPlayerRow align=center>");
                                out.println("<td>" + ((size > 1) ? p : team_num + " " + tmp_wait) + "</td>");
                                out.println("<td>" + rs2.getString("player" + p) + "</td>");
                                if (ask_gender == 1) out.println("<td>" + rs2.getString("gender" + p) + "&nbsp;</td>");
                                if (ask_hdcp == 1) out.println("<td>" + rs2.getString("ghin" + p) + "&nbsp;</td>");
                                if (ask_homeclub == 1) out.println("<td nowrap>" + rs2.getString("homeclub" + p) + "&nbsp;</td>");
                                if (ask_phone == 1) out.println("<td>" + rs2.getString("phone" + p) + "&nbsp;</td>");
                                if (ask_address == 1) out.println("<td nowrap>" + rs2.getString("address" + p) + "&nbsp;</td>");
                                if (ask_email == 1) out.println("<td>" + rs2.getString("email" + p) + "&nbsp;</td>");
                                if (ask_shirtsize == 1) out.println("<td>" + rs2.getString("shirtsize" + p) + "&nbsp;</td>");
                                if (ask_shoesize == 1) out.println("<td>" + rs2.getString("shoesize" + p) + "&nbsp;</td>");
                                if (ask_otherA1 == 1) out.println("<td>" + rs2.getString("other" + p + "a1") + "&nbsp;</td>");
                                if (ask_otherA2 == 1) out.println("<td>" + rs2.getString("other" + p + "a2") + "&nbsp;</td>");
                                if (ask_otherA3 == 1) out.println("<td>" + rs2.getString("other" + p + "a3") + "&nbsp;</td>");
                                out.println("</tr>");

                            } // end if player empty
                            
                        } // end player loop
                        
                        team_num++;
                        
                    } // end if player1 present (not an canceled signup)
                      
                  } // end while loop

                  out.println("</table>");
                  
                  if (size == 1) out.println("<br><center><font size=2>* = on wait list</font></center>");

               } else if (printType.equals( "notes" )) {

                  out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" valign=\"top\">");
                  out.println("<tr bgcolor=\"#336633\">");
                  out.println("<td align=\"left\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Players</b></u> ");
                  out.println("</font></td>");
                  out.println("<td align=\"left\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Notes</b></u> ");
                  out.println("</font></td>");
                  out.println("</tr>");

                  //
                  //  Get each group and display the players and notes
                  //
                  while (rs2.next()) {

                     player1 = rs2.getString("player1");
                     player2 = rs2.getString("player2");
                     player3 = rs2.getString("player3");
                     player4 = rs2.getString("player4");
                     player5 = rs2.getString("player5");
                     notes = rs2.getString("notes");

                     if (!notes.equals( "" )) {        // if notes

                        out.println("<tr><td align=\"left\">");
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
                        out.println("<td align=\"left\">");
                        out.println("<font size=\"2\">");
                        out.println(notes);
                        out.println("</font></td></tr>");
                     }

                  }                           // end of WHILE members
                  pstmte.close();

                  out.println("</table>");              // end of player table

               } else {    // not a notes list request

                  out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" valign=\"top\">");
                  out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" valign=\"top\">");
                  out.println("<tr bgcolor=\"#336633\">");

                  if (size == 1) {      // make multiple columns

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Player</b></u> ");
                     out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
                     if (printType.equals( "print" )) {
                        if (disp_hndcp == true) {
                           out.println("<u>hndcp</u>");
                        }
                     } else {
                        if (printType.equals( "bag" )) {
                           out.println("<u>Bag#</u>");
                        } else {
                           if (printType.equals( "mnum" )) {
                              out.println("<u>Mem#</u>");
                           }
                        }
                     }
                     out.println("</font></td>");

                     if (activity_id == 0 && season == 0) {
                         out.println("<td align=\"center\">");
                         out.println("<font color=\"#FFFFFF\" size=\"2\">");
                         out.println("<u><b>C/W</b></u>");
                         out.println("</font></td>");
                     }

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Registered</b></u>");
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Status</b></u>");
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"1\">&nbsp;");      // empty column for spacing
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Player</b></u> ");
                     out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
                     if (printType.equals( "print" )) {
                        if (disp_hndcp == true) {
                           out.println("<u>hndcp</u>");
                        }
                     } else {
                        if (printType.equals( "bag" )) {
                           out.println("<u>Bag#</u>");
                        } else {
                           if (printType.equals( "mnum" )) {
                              out.println("<u>Mem#</u>");
                           }
                        }
                     }
                     out.println("</font></td>");

                     if (activity_id == 0 && season == 0) {
                         out.println("<td align=\"center\">");
                         out.println("<font color=\"#FFFFFF\" size=\"2\">");
                         out.println("<u><b>C/W</b></u>");
                         out.println("</font></td>");
                     }

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Registered</b></u>");
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Status</b></u>");
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"1\">&nbsp;");      // empty column for spacing
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Player</b></u> ");
                     out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
                     if (printType.equals( "print" )) {
                        if (disp_hndcp == true) {
                           out.println("<u>hndcp</u>");
                        }
                     } else {
                        if (printType.equals( "bag" )) {
                           out.println("<u>Bag#</u>");
                        } else {
                           if (printType.equals( "mnum" )) {
                              out.println("<u>Mem#</u>");
                           }
                        }
                     }
                     out.println("</font></td>");

                     if (activity_id == 0 && season == 0) {
                         out.println("<td align=\"center\">");
                         out.println("<font color=\"#FFFFFF\" size=\"2\">");
                         out.println("<u><b>C/W</b></u>");
                         out.println("</font></td>");
                     }

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Registered</b></u>");
                     out.println("</font></td>");

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Status</b></u>");
                     out.println("</font></td>");             // end of row is below

                     player1 = "";
                     player2 = "";

                     //
                     //  get all the players and list them
                     //
                     while (rs2.next()) {

                        player = rs2.getString("player1");
                        user1 = rs2.getString("username1");
                        p1cw = rs2.getString("p1cw");
                        hndcp1 = rs2.getFloat("hndcp1");
                        r_date = rs2.getLong("r_date");          // date/time registered
                        r_time = rs2.getInt("r_time");
                        wait = rs2.getInt("wait");

                        //
                        //   skip this entry if player is null (someone cancelled)
                        //
                        if (!player.equals( "" )) {

                           //
                           //  get date and time member first registered
                           //
                           r_yr = r_date / 10000;
                           temp = r_yr * 10000;
                           r_mm = r_date - temp;
                           temp = r_mm / 100;
                           temp = temp * 100;
                           r_dd = r_mm - temp;
                           r_mm = r_mm / 100;

                           r_hr = r_time / 100;
                           r_min = r_time - (r_hr * 100);

                           r_ampm = " AM";
                           if (r_hr == 12) {
                              r_ampm = " PM";
                           }
                           if (r_hr > 12) {
                              r_ampm = " PM";
                              r_hr = r_hr - 12;    // convert to conventional time
                           }
                           if (r_hr == 0) {
                              r_hr = 12;
                           }

                           if (player1.equals( "" )) {      // starting new row - process 3 members per row

                              player1 = player;             // indicate column 1 done

                              out.println("</tr><tr>");

                              //
                              //  Add Player 1
                              //
                              out.println("<td align=\"center\">");
                              out.println("<font size=\"2\">");

                              if (player.equalsIgnoreCase("x")) {   // if 'x'

                                 out.println(player);

                              } else {       // not 'x'

                                 if (printType.equals( "print" )) {
                                    if (disp_hndcp == false) {
                                       out.println(player);
                                    } else {
                                       if ((hndcp1 == 99) || (hndcp1 == -99)) {
                                          out.println(player);
                                       } else {
                                          if (hndcp1 <= 0) {
                                             hndcp1 = 0 - hndcp1;                       // convert to non-negative
                                          }
                                          //hndcp = Math.round(hndcp1);                   // round it off
                                          out.println(player + "  " + hndcp1);
                                       }
                                    }
                                 } else {
                                    if (printType.equals( "bag" )) {  // if bag report
                                       if (user1.equals( "" )) {
                                          out.println(player);
                                       } else {
                                          bag = getBag(user1, con);           // get bag number
                                          out.println(player + "  " +bag);
                                       }
                                    } else {   // mnum report
                                       if (user1.equals( "" )) {
                                          out.println(player);
                                       } else {
                                          mnum = getMnum(user1, con);           // get member number
                                          out.println(player + "  " +mnum);
                                       }
                                    }
                                 }
                              }
                              out.println("</font></td>");
                              if (activity_id == 0 && season == 0) {
                                  
                                  out.println("<td bgcolor=\"white\" align=\"center\">");

                                  if ((!player.equals("")) && (!player.equalsIgnoreCase( "x" ))) {
                                     out.println("<font size=\"1\">");
                                     out.println(p1cw);
                                  } else {
                                     out.println("<font size=\"2\">");
                                     out.println("&nbsp;");
                                  }
                                  out.println("</font></td>");
                              }

                              out.println("<td align=\"center\">");                  // date/time registered
                              out.println("<font size=\"1\">");
                              out.println(r_mm + "/" + r_dd + "/" + r_yr + "<br>");
                              out.println(r_hr + ":" + Utilities.ensureDoubleDigit(r_min) + r_ampm);
                              out.println("</font></td>");

                              out.println("<td align=\"center\">");
                              out.println("<font size=\"2\">");
                              if (wait == 0) {
                                 out.println("Reg");
                              } else {
                                 out.println("Wait");
                              }
                              out.println("</font></td>");

                              out.println("<td align=\"center\" bgcolor=\"#336633\">");
                              out.println("<font size=\"1\">&nbsp;");      // empty column for spacing
                              out.println("</font></td>");

                           } else {

                              if (player2.equals( "" )) {

                                 player2 = player;             // indicate column 2 done

                                 //
                                 //  Add Player 2 (column 2)
                                 //
                                 out.println("<td align=\"center\">");
                                 out.println("<font size=\"2\">");

                                 if (player.equalsIgnoreCase("x")) {   // if 'x'

                                    out.println(player);

                                 } else {       // not 'x'

                                    if (printType.equals( "print" )) {  // if hndcp report
                                       if (disp_hndcp == false) {
                                          out.println(player);
                                       } else {
                                          if ((hndcp1 == 99) || (hndcp1 == -99)) {
                                             out.println(player);
                                          } else {
                                             if (hndcp1 <= 0) {
                                                hndcp1 = 0 - hndcp1;                       // convert to non-negative
                                             }
                                             //hndcp = Math.round(hndcp1);                   // round it off
                                             out.println(player + "  " + hndcp1);
                                          }
                                       }
                                    } else {
                                       if (printType.equals( "bag" )) {  // if bag report
                                          if (user1.equals( "" )) {
                                             out.println(player);
                                          } else {
                                             bag = getBag(user1, con);           // get bag number
                                             out.println(player + "  " +bag);
                                          }
                                       } else {   // mnum report
                                          if (user1.equals( "" )) {
                                             out.println(player);
                                          } else {
                                             mnum = getMnum(user1, con);           // get member number
                                             out.println(player + "  " +mnum);
                                          }
                                       }
                                    }
                                 }
                                 out.println("</font></td>");
                              if (activity_id == 0 && season == 0) {

                                 out.println("<td bgcolor=\"white\" align=\"center\">");

                                 if ((!player.equals("")) && (!player.equalsIgnoreCase( "x" ))) {
                                    out.println("<font size=\"1\">");
                                    out.println(p1cw);
                                 } else {
                                    out.println("<font size=\"2\">");
                                    out.println("&nbsp;");
                                 }
                                 out.println("</font></td>");
                              }
                                 out.println("<td align=\"center\">");                  // date/time registered
                                 out.println("<font size=\"1\">");
                                 out.println(r_mm + "/" + r_dd + "/" + r_yr + "<br>");
                                 out.println(r_hr + ":" + Utilities.ensureDoubleDigit(r_min) + r_ampm);
                                 out.println("</font></td>");

                                 out.println("<td align=\"center\">");
                                 out.println("<font size=\"2\">");
                                 if (wait == 0) {
                                    out.println("Reg");
                                 } else {
                                    out.println("Wait");
                                 }
                                 out.println("</font></td>");

                                 out.println("<td align=\"center\" bgcolor=\"#336633\">");
                                 out.println("<font size=\"1\">&nbsp;");      // empty column for spacing
                                 out.println("</font></td>");

                              } else {

                                 //
                                 //  Add Player 3 (column 3)
                                 //
                                 out.println("<td align=\"center\">");
                                 out.println("<font size=\"2\">");

                                 if (player.equalsIgnoreCase("x")) {   // if 'x'

                                    out.println(player);

                                 } else {       // not 'x'

                                    if (printType.equals( "print" )) {  // if hndcp report
                                       if (disp_hndcp == false) {
                                          out.println(player);
                                       } else {
                                          if ((hndcp1 == 99) || (hndcp1 == -99)) {
                                             out.println(player);
                                          } else {
                                             if (hndcp1 <= 0) {
                                                hndcp1 = 0 - hndcp1;                       // convert to non-negative
                                             }
                                             //hndcp = Math.round(hndcp1);                   // round it off
                                             out.println(player + "  " + hndcp1);
                                          }
                                       }
                                    } else {
                                       if (printType.equals( "bag" )) {  // if bag report
                                          if (user1.equals( "" )) {
                                             out.println(player);
                                          } else {
                                             bag = getBag(user1, con);           // get bag number
                                             out.println(player + "  " +bag);
                                          }
                                       } else {   // mnum report
                                          if (user1.equals( "" )) {
                                             out.println(player);
                                          } else {
                                             mnum = getMnum(user1, con);           // get member number
                                             out.println(player + "  " +mnum);
                                          }
                                       }
                                    }
                                 }
                                 out.println("</font></td>");

                              if (activity_id == 0 && season == 0) {
                                 
                                 out.println("<td bgcolor=\"white\" align=\"center\">");

                                 if ((!player.equals("")) && (!player.equalsIgnoreCase( "x" ))) {
                                    out.println("<font size=\"1\">");
                                    out.println(p1cw);
                                 } else {
                                    out.println("<font size=\"2\">");
                                    out.println("&nbsp;");
                                 }
                                 out.println("</font></td>");
                              }
                                 out.println("<td align=\"center\">");                  // date/time registered
                                 out.println("<font size=\"1\">");
                                 out.println(r_mm + "/" + r_dd + "/" + r_yr + "<br>");
                                 out.println(r_hr + ":" + Utilities.ensureDoubleDigit(r_min) + r_ampm);
                                 out.println("</font></td>");

                                 out.println("<td align=\"center\">");
                                 out.println("<font size=\"2\">");
                                 if (wait == 0) {
                                    out.println("Reg");
                                 } else {
                                    out.println("Wait");
                                 }
                                 out.println("</font></td>");

                                 player1 = "";                 // start new row on next player
                                 player2 = "";

                              }   // end of IF player2

                           }   // end of IF player1

                        }   // end of IF player exist
                     }      // end of WHILE

                     out.println("</tr>");
                     out.println("</table>");              // end of player table

                  } else {

                     //
                     // more than one player per entry
                     //
                     out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Player 1</b></u> ");
                     if (printType.equals( "print" )) {
                        if (disp_hndcp == true) {
                           out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
                        }
                     } else {
                        if (printType.equals( "bag" )) {
                           out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>Bag#</u>");
                        } else {
                           if (printType.equals( "mnum" )) {
                              out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>Mem#</u>");
                           }
                        }
                     }
                     out.println("</font></td>");

                     if (activity_id == 0 && season == 0) {

                         out.println("<td align=\"center\">");
                         out.println("<font color=\"#FFFFFF\" size=\"1\">");
                         out.println("<u><b>C/W</b></u>");
                         out.println("</font></td>");
                     }

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<u><b>Player 2</b></u> ");
                     if (printType.equals( "print" )) {
                        if (disp_hndcp == true) {
                           out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
                        }
                     } else {
                        if (printType.equals( "bag" )) {
                           out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>Bag#</u>");
                        } else {
                           if (printType.equals( "mnum" )) {
                              out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>Mem#</u>");
                           }
                        }
                     }
                     out.println("</font></td>");

                     if (activity_id == 0 && season == 0) {

                         out.println("<td align=\"center\">");
                         out.println("<font color=\"#FFFFFF\" size=\"1\">");
                         out.println("<u><b>C/W</b></u>");
                         out.println("</font></td>");
                     }
                     if (size > 2) {

                        out.println("<td align=\"center\">");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<u><b>Player 3</b></u> ");
                        if (printType.equals( "print" )) {
                           if (disp_hndcp == true) {
                              out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
                           }
                        } else {
                           if (printType.equals( "bag" )) {
                              out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>Bag#</u>");
                           } else {
                              if (printType.equals( "mnum" )) {
                                 out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>Mem#</u>");
                              }
                           }
                        }
                        out.println("</font></td>");

                        if (activity_id == 0 && season == 0) {
                           out.println("<td align=\"center\">");
                           out.println("<font color=\"#FFFFFF\" size=\"1\">");
                           out.println("<u><b>C/W</b></u>");
                           out.println("</font></td>");
                        }
                     }
                     if (size > 3) {

                        out.println("<td align=\"center\">");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<u><b>Player 4</b></u> ");
                        if (printType.equals( "print" )) {
                           if (disp_hndcp == true) {
                              out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
                           }
                        } else {
                           if (printType.equals( "bag" )) {
                              out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>Bag#</u>");
                           } else {
                              if (printType.equals( "mnum" )) {
                                 out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>Mem#</u>");
                              }
                           }
                        }
                        out.println("</font></td>");

                        if (activity_id == 0 && season == 0) {
                           out.println("<td align=\"center\">");
                           out.println("<font color=\"#FFFFFF\" size=\"1\">");
                           out.println("<u><b>C/W</b></u>");
                           out.println("</font></td>");
                        }
                     }
                     if (size > 4) {

                        out.println("<td align=\"center\">");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<u><b>Player 5</b></u> ");
                        if (printType.equals( "print" )) {
                           if (disp_hndcp == true) {
                              out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
                           }
                        } else {
                           if (printType.equals( "bag" )) {
                              out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>Bag#</u>");
                           } else {
                              if (printType.equals( "mnum" )) {
                                 out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>Mem#</u>");
                              }
                           }
                        }
                        out.println("</font></td>");

                        if (activity_id == 0 && season == 0) {
                           out.println("<td align=\"center\">");
                           out.println("<font color=\"#FFFFFF\" size=\"1\">");
                           out.println("<u><b>C/W</b></u>");
                           out.println("</font></td>");
                        }
                     }

                        out.println("<td align=\"center\">");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<u><b>Registered</b></u>");
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font color=\"#FFFFFF\" size=\"2\">");
                        out.println("<u><b>Status</b></u>");
                        out.println("</font></td>");

                     out.println("</tr>");

                     //
                     //  get all the players and list them
                     //
                     while (rs2.next()) {

                        player1 = rs2.getString("player1");
                        player2 = rs2.getString("player2");
                        player3 = rs2.getString("player3");
                        player4 = rs2.getString("player4");
                        player5 = rs2.getString("player5");
                        user1 = rs2.getString("username1");
                        user2 = rs2.getString("username2");
                        user3 = rs2.getString("username3");
                        user4 = rs2.getString("username4");
                        user5 = rs2.getString("username5");
                        p1cw = rs2.getString("p1cw");
                        p2cw = rs2.getString("p2cw");
                        p3cw = rs2.getString("p3cw");
                        p4cw = rs2.getString("p4cw");
                        p5cw = rs2.getString("p5cw");
                        hndcp1 = rs2.getFloat("hndcp1");
                        hndcp2 = rs2.getFloat("hndcp2");
                        hndcp3 = rs2.getFloat("hndcp3");
                        hndcp4 = rs2.getFloat("hndcp4");
                        hndcp5 = rs2.getFloat("hndcp5");
                        id = rs2.getInt("id");
                        r_date = rs2.getLong("r_date");          // date/time registered
                        r_time = rs2.getInt("r_time");
                        wait = rs2.getInt("wait");

                        //
                        //   skip this entry if all players are null (someone cancelled)
                        //
                        if (!player1.equals( "" ) || !player2.equals( "") ||
                            !player3.equals( "" ) || !player4.equals( "") || !player5.equals( "")) {

                           //
                           //  set up some fields needed for the table
                           //
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

                           //
                           //  get date and time members first registered
                           //
                           r_yr = r_date / 10000;
                           temp = r_yr * 10000;
                           r_mm = r_date - temp;
                           temp = r_mm / 100;
                           temp = temp * 100;
                           r_dd = r_mm - temp;
                           r_mm = r_mm / 100;

                           r_hr = r_time / 100;
                           r_min = r_time - (r_hr * 100);

                           r_ampm = " AM";
                           if (r_hr == 12) {
                              r_ampm = " PM";
                           }
                           if (r_hr > 12) {
                              r_ampm = " PM";
                              r_hr = r_hr - 12;    // convert to conventional time
                           }
                           if (r_hr == 0) {
                              r_hr = 12;
                           }

                        out.println("<tr>");

                           //
                           //  Add Player 1
                           //
                           out.println("<td align=\"center\">");
                           out.println("<font size=\"2\">");

                           if (!player1.equals("")) {

                              if (player1.equalsIgnoreCase("x")) {   // if 'x'

                                 out.println(player1);

                              } else {       // not 'x'

                                 if (printType.equals( "print" )) {  // if hndcp report
                                    if (disp_hndcp == false) {
                                       out.println(player1);
                                    } else {
                                       if ((hndcp1 == 99) || (hndcp1 == -99)) {
                                          out.println(player1);
                                       } else {
                                          if (hndcp1 <= 0) {
                                             hndcp1 = 0 - hndcp1;                       // convert to non-negative
                                          }
                                          //hndcp = Math.round(hndcp1);                   // round it off
                                          out.println(player1 + "  " + hndcp1);
                                       }
                                    }
                                 } else {
                                    if (printType.equals( "bag" )) {  // if bag report
                                       if (user1.equals( "" )) {
                                          out.println(player1);
                                       } else {
                                          bag = getBag(user1, con);           // get bag number
                                          out.println(player1 + "  " +bag);
                                       }
                                    } else {   // mnum report
                                       if (user1.equals( "" )) {
                                          out.println(player1);
                                       } else {
                                          mnum = getMnum(user1, con);           // get member number
                                          out.println(player1 + "  " +mnum);
                                       }
                                    }
                                 }
                              }
                           } else {     // player is empty

                              out.println("&nbsp;");
                           }
                           out.println("</font></td>");
                           if (activity_id == 0 && season == 0) {
                               out.println("<td bgcolor=\"white\" align=\"center\">");

                               if ((!player1.equals("")) && (!player1.equalsIgnoreCase( "x" ))) {
                                  out.println("<font size=\"1\">");
                                  out.println(p1cw);
                               } else {
                                  out.println("<font size=\"2\">");
                                  out.println("&nbsp;");
                               }
                               out.println("</font></td>");
                           }

                        if (size > 1) {

                           //
                           //  Add Player 2
                           //
                           out.println("<td align=\"center\">");
                           out.println("<font size=\"2\">");

                           if (!player2.equals("")) {

                              if (player2.equalsIgnoreCase("x")) {   // if 'x'

                                 out.println(player2);

                              } else {       // not 'x'

                                 if (printType.equals( "print" )) {  // if hndcp report
                                    if (disp_hndcp == false) {
                                       out.println(player2);
                                    } else {
                                       if ((hndcp2 == 99) || (hndcp2 == -99)) {
                                          out.println(player2);
                                       } else {
                                          if (hndcp2 <= 0) {
                                             hndcp2 = 0 - hndcp2;                       // convert to non-negative
                                          }
                                          //hndcp = Math.round(hndcp2);                   // round it off
                                          out.println(player2 + "  " + hndcp2);
                                       }
                                    }
                                 } else {
                                    if (printType.equals( "bag" )) {  // if bag report
                                       if (user2.equals( "" )) {
                                          out.println(player2);
                                       } else {
                                          bag = getBag(user2, con);           // get bag number
                                          out.println(player2 + "  " +bag);
                                       }
                                    } else {   // mnum report
                                       if (user2.equals( "" )) {
                                          out.println(player2);
                                       } else {
                                          mnum = getMnum(user2, con);           // get member number
                                          out.println(player2 + "  " +mnum);
                                       }
                                    }
                                 }
                              }
                           } else {     // player is empty

                              out.println("&nbsp;");
                           }
                           out.println("</font></td>");
                           if (activity_id == 0 && season == 0) {
                               out.println("<td bgcolor=\"white\" align=\"center\">");

                               if ((!player2.equals("")) && (!player2.equalsIgnoreCase( "x" ))) {
                                  out.println("<font size=\"1\">");
                                  out.println(p2cw);
                               } else {
                                  out.println("<font size=\"2\">");
                                  out.println("&nbsp;");
                               }
                               out.println("</font></td>");
                           }
                        }
                        if (size > 2) {

                           //
                           //  Add Player 3
                           //
                           out.println("<td align=\"center\">");
                           out.println("<font size=\"2\">");

                           if (!player3.equals("")) {

                              if (player3.equalsIgnoreCase("x")) {   // if 'x'

                                 out.println(player3);

                              } else {       // not 'x'

                                 if (printType.equals( "print" )) {  // if hndcp report
                                    if (disp_hndcp == false) {
                                       out.println(player3);
                                    } else {
                                       if ((hndcp3 == 99) || (hndcp3 == -99)) {
                                          out.println(player3);
                                       } else {
                                          if (hndcp3 <= 0) {
                                             hndcp3 = 0 - hndcp3;                       // convert to non-negative
                                          }
                                          //hndcp = Math.round(hndcp3);                   // round it off
                                          out.println(player3 + "  " + hndcp3);
                                       }
                                    }
                                 } else {
                                    if (printType.equals( "bag" )) {  // if bag report
                                       if (user3.equals( "" )) {
                                          out.println(player3);
                                       } else {
                                          bag = getBag(user3, con);           // get bag number
                                          out.println(player3 + "  " +bag);
                                       }
                                    } else {   // mnum report
                                       if (user3.equals( "" )) {
                                          out.println(player3);
                                       } else {
                                          mnum = getMnum(user3, con);           // get member number
                                          out.println(player3 + "  " +mnum);
                                       }
                                    }
                                 }
                              }
                           } else {     // player is empty

                              out.println("&nbsp;");
                           }
                           out.println("</font></td>");
                           if (activity_id == 0 && season == 0) {
                               out.println("<td bgcolor=\"white\" align=\"center\">");

                               if ((!player3.equals("")) && (!player3.equalsIgnoreCase( "x" ))) {
                                  out.println("<font size=\"1\">");
                                  out.println(p3cw);
                               } else {
                                  out.println("<font size=\"2\">");
                                  out.println("&nbsp;");
                               }
                               out.println("</font></td>");
                           }
                        }
                        if (size > 3) {

                           //
                           //  Add Player 4
                           //
                           out.println("<td align=\"center\">");
                           out.println("<font size=\"2\">");

                           if (!player4.equals("")) {

                              if (player4.equalsIgnoreCase("x")) {   // if 'x'

                                 out.println(player4);

                              } else {       // not 'x'

                                 if (printType.equals( "print" )) {  // if hndcp report
                                    if (disp_hndcp == false) {
                                       out.println(player4);
                                    } else {
                                       if ((hndcp4 == 99) || (hndcp4 == -99)) {
                                          out.println(player4);
                                       } else {
                                          if (hndcp4 <= 0) {
                                             hndcp4 = 0 - hndcp4;                       // convert to non-negative
                                          }
                                          //hndcp = Math.round(hndcp4);                   // round it off
                                          out.println(player4 + "  " + hndcp4);
                                       }
                                    }
                                 } else {
                                    if (printType.equals( "bag" )) {  // if bag report
                                       if (user4.equals( "" )) {
                                          out.println(player4);
                                       } else {
                                          bag = getBag(user4, con);           // get bag number
                                          out.println(player4 + "  " +bag);
                                       }
                                    } else {   // mnum report
                                       if (user4.equals( "" )) {
                                          out.println(player4);
                                       } else {
                                          mnum = getMnum(user4, con);           // get member number
                                          out.println(player4 + "  " +mnum);
                                       }
                                    }
                                 }
                              }
                           } else {     // player is empty

                              out.println("&nbsp;");
                           }
                           out.println("</font></td>");
                           if (activity_id == 0 && season == 0) {
                               out.println("<td bgcolor=\"white\" align=\"center\">");

                               if ((!player4.equals("")) && (!player4.equalsIgnoreCase( "x" ))) {
                                  out.println("<font size=\"1\">");
                                  out.println(p4cw);
                               } else {
                                  out.println("<font size=\"2\">");
                                  out.println("&nbsp;");
                               }
                               out.println("</font></td>");
                           }
                        }
                        if (size > 4) {

                           //
                           //  Add Player 5
                           //
                           out.println("<td align=\"center\">");
                           out.println("<font size=\"2\">");

                           if (!player5.equals("")) {

                              if (player5.equalsIgnoreCase("x")) {   // if 'x'

                                 out.println(player5);

                              } else {       // not 'x'

                                 if (printType.equals( "print" )) {  // if hndcp report
                                    if (disp_hndcp == false) {
                                       out.println(player5);
                                    } else {
                                       if ((hndcp5 == 99) || (hndcp5 == -99)) {
                                          out.println(player5);
                                       } else {
                                          if (hndcp5 <= 0) {
                                             hndcp5 = 0 - hndcp5;                       // convert to non-negative
                                          }
                                          //hndcp = Math.round(hndcp5);                   // round it off
                                          out.println(player5 + "  " + hndcp5);
                                       }
                                    }
                                 } else {
                                    if (printType.equals( "bag" )) {  // if bag report
                                       if (user5.equals( "" )) {
                                          out.println(player5);
                                       } else {
                                          bag = getBag(user5, con);           // get bag number
                                          out.println(player5 + "  " +bag);
                                       }
                                    } else {   // mnum report
                                       if (user5.equals( "" )) {
                                          out.println(player5);
                                       } else {
                                          mnum = getMnum(user5, con);           // get member number
                                          out.println(player5 + "  " +mnum);
                                       }
                                    }
                                 }
                              }
                           } else {     // player is empty

                              out.println("&nbsp;");
                           }
                           out.println("</font></td>");

                           if (activity_id == 0 && season == 0) {
                               
                               out.println("<td bgcolor=\"white\" align=\"center\">");
                               if ((!player5.equals("")) && (!player5.equalsIgnoreCase( "x" ))) {
                                  out.println("<font size=\"1\">");
                                  out.println(p5cw);
                               } else {
                                  out.println("<font size=\"2\">");
                                  out.println("&nbsp;");
                               }
                               out.println("</font></td>");
                           }
                        }  // end of IF size>4

                           //
                           //  add date member registered
                           //
                           out.println("<td align=\"center\">");
                           out.println("<font size=\"1\">");
                           out.println(r_mm + "/" + r_dd + "/" + r_yr + "<br>");
                           out.println(r_hr + ":" + Utilities.ensureDoubleDigit(r_min) + r_ampm);
                           out.println("</font></td>");

                           out.println("<td align=\"center\">");
                           out.println("<font size=\"2\">");
                           if (wait == 0) {
                              out.println("Reg");
                           } else {
                              out.println("Wait");
                           }
                           out.println("</font></td>");
                           out.println("</tr>");              // end of row

                        }  // end of IF entry is empty (all players = null)

                     }     // end of WHILE

                     pstmte.close();

                     out.println("</table>");              // end of player table

                  }      // end of IF SIZE=1

               }      // end of IF notes list

            }      // end of IF alphabetical list

         }      // end of IF no one signed up

         out.println("</font></td></tr>");
         out.println("</table>");                   // end of main page table

      }  // end of IF PRINT

      //
      //  End of HTML page
      //
      out.println("</center></font></body></html>");

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact support (provide this information).");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
   }
 }   // end of doPost


 // ********************************************************************
 //  Get member's bag number
 // ********************************************************************

 private String getBag(String user, Connection con) {


    ResultSet rs = null;
    PreparedStatement pstmt = null;

    String bag = "";

    try {

        pstmt = con.prepareStatement (
            "SELECT bag FROM member2b WHERE username = ?");

        pstmt.clearParameters();
        pstmt.setString(1, user);
        rs = pstmt.executeQuery();

        if (rs.next()) bag = rs.getString(1);         // user's bag room slot#

    } catch (Exception ignore) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return(bag);

 }   // end of getBag


 // ********************************************************************
 //  Get member number
 // ********************************************************************

 private String getMnum(String user, Connection con) {


    ResultSet rs = null;
    PreparedStatement pstmt = null;

    String mnum = "";

    try {

        pstmt = con.prepareStatement (
               "SELECT memNum FROM member2b WHERE username = ?");

        pstmt.clearParameters();
        pstmt.setString(1, user);
        rs = pstmt.executeQuery();

        if (rs.next()) mnum = rs.getString(1);         // user's mnum

   } catch (Exception ignore) {

   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

   return(mnum);

 }   // end of getMnum


 
 private void exportEvent(HttpServletRequest req, HttpServletResponse resp) {
    
    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
     
    PrintWriter out = null;
     
    String name = "";
    /*
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
    String gender = "";
    String clubName = "";
    
    float hndcp1 = 0;
    float hndcp2 = 0;
    float hndcp3 = 0;
    float hndcp4 = 0;
    float hndcp5 = 0;
    */
    String pairings = "";
    int team_num = 1;
    int size = 0;
    int export_type = 0;
    
    if (req.getParameter("name") != null)
        name = req.getParameter("name");
    else
        return;
    
    resp.setContentType("application/csv");
    resp.setHeader("Content-Disposition", "filename=\"" + name + ".csv\"");
    
    try{
        out = resp.getWriter();         
    } catch (Exception ignore) { }
     
    HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

    if (session == null) return;
    
    Connection con = SystemUtils.getCon(session);

    if (con == null) {

        out.println("<MTHL><BODY><CENTER>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact support.");
        out.println("<BR><BR>");
        out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
        out.println("</CENTER></BODY></HTML>");
        return;
        
    }
    
    
    try {
        
        pstmt = con.prepareStatement (
            "SELECT size, export_type, pairings FROM events2b WHERE name = ?");

        pstmt.clearParameters();
        pstmt.setString(1, name);
        
        rs = pstmt.executeQuery();

        while (rs.next()) {

            size = rs.getInt(1);
            export_type = rs.getInt(2);
            pairings = rs.getString(3);
        }
        
        pstmt.close();

    } catch (Exception exc) { 
        
        out.println("<br>Error occurred loading event information! Error= " + exc.getMessage());
        
    } finally {
        
        try { rs.close(); }
        catch (Exception ignore) {}
        
        try { pstmt.close(); }
        catch (Exception ignore) {}
        
    }
    
    
    try {
        
        pstmt = con.prepareStatement(
                "SELECT " +
                    "player1, player2, player3, player4, player5, " +
                    "username1, username2, username3, username4, username5, " +
                    "p1cw, p2cw, p3cw, p4cw, p5cw, " +
                    "homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, " +
                    "ghin1, ghin2, ghin3, ghin4, ghin5, " +
                    "hndcp1, hndcp2, hndcp3, hndcp4, hndcp5, " +
                    "gender1, gender2, gender3, gender4, gender5 " +
                "FROM evntsup2b " +
                "WHERE name = ? AND player1 <> '' AND wait = 0 AND inactive = 0 " +
                "ORDER BY id");    // grab all rows with data for this event
        
        pstmt.clearParameters();
        pstmt.setString(1, name);
        
        rs = pstmt.executeQuery();
        
        /*  ???????? is this needed??
        pstmt2 = con.prepareStatement("SELECT clubName FROM club5");
        rs2 = pstmt2.executeQuery();
        
        if (rs2.next()) clubName = rs2.getString("clubName");    // get clubname
         */
        
        // iterate through data and print to tppExport.csv file
        while (rs.next()) {
            
            if (export_type == 1) {
                
                                                         tppPrintPlayer(rs.getString("player1"), rs.getString("username1"), rs.getString("homeclub1"), rs.getString("ghin1"), rs.getString("gender1"), rs.getString("hndcp1"), con, out);
                if (!rs.getString("player2").equals("")) tppPrintPlayer(rs.getString("player2"), rs.getString("username2"), rs.getString("homeclub2"), rs.getString("ghin2"), rs.getString("gender2"), rs.getString("hndcp2"), con, out);
                if (!rs.getString("player3").equals("")) tppPrintPlayer(rs.getString("player3"), rs.getString("username3"), rs.getString("homeclub3"), rs.getString("ghin3"), rs.getString("gender3"), rs.getString("hndcp3"), con, out);
                if (!rs.getString("player4").equals("")) tppPrintPlayer(rs.getString("player4"), rs.getString("username4"), rs.getString("homeclub4"), rs.getString("ghin4"), rs.getString("gender4"), rs.getString("hndcp4"), con, out);
                if (!rs.getString("player5").equals("")) tppPrintPlayer(rs.getString("player5"), rs.getString("username5"), rs.getString("homeclub5"), rs.getString("ghin5"), rs.getString("gender5"), rs.getString("hndcp5"), con, out);
            
            } else if (export_type == 2) {
                
                                                         eventManPrintPlayer(rs.getString("player1"), rs.getString("username1"), rs.getString("homeclub1"), rs.getString("ghin1"), rs.getString("gender1"), rs.getString("hndcp1"), team_num, 1, con, out);
                if (!rs.getString("player2").equals("")) eventManPrintPlayer(rs.getString("player2"), rs.getString("username2"), rs.getString("homeclub2"), rs.getString("ghin2"), rs.getString("gender2"), rs.getString("hndcp2"), team_num, 2, con, out);
                if (!rs.getString("player3").equals("")) eventManPrintPlayer(rs.getString("player3"), rs.getString("username3"), rs.getString("homeclub3"), rs.getString("ghin3"), rs.getString("gender3"), rs.getString("hndcp3"), team_num, 3, con, out);
                if (!rs.getString("player4").equals("")) eventManPrintPlayer(rs.getString("player4"), rs.getString("username4"), rs.getString("homeclub4"), rs.getString("ghin4"), rs.getString("gender4"), rs.getString("hndcp4"), team_num, 4, con, out);
                if (!rs.getString("player5").equals("")) eventManPrintPlayer(rs.getString("player5"), rs.getString("username5"), rs.getString("homeclub5"), rs.getString("ghin5"), rs.getString("gender5"), rs.getString("hndcp5"), team_num, 5, con, out);
            
            } else if (export_type == 3) {

                                                         golfNetPrintPlayer(rs.getString("player1"), rs.getString("username1"), rs.getString("homeclub1"), rs.getString("ghin1"), rs.getString("gender1"), rs.getString("hndcp1"), team_num, con, out);
                if (!rs.getString("player2").equals("")) golfNetPrintPlayer(rs.getString("player2"), rs.getString("username2"), rs.getString("homeclub2"), rs.getString("ghin2"), rs.getString("gender2"), rs.getString("hndcp2"), team_num, con, out);
                if (!rs.getString("player3").equals("")) golfNetPrintPlayer(rs.getString("player3"), rs.getString("username3"), rs.getString("homeclub3"), rs.getString("ghin3"), rs.getString("gender3"), rs.getString("hndcp3"), team_num, con, out);
                if (!rs.getString("player4").equals("")) golfNetPrintPlayer(rs.getString("player4"), rs.getString("username4"), rs.getString("homeclub4"), rs.getString("ghin4"), rs.getString("gender4"), rs.getString("hndcp4"), team_num, con, out);
                if (!rs.getString("player5").equals("")) golfNetPrintPlayer(rs.getString("player5"), rs.getString("username5"), rs.getString("homeclub5"), rs.getString("ghin5"), rs.getString("gender5"), rs.getString("hndcp5"), team_num, con, out);

            } else if (export_type == 4) {      

                                                         tourExPrintPlayer(rs.getString("player1"), rs.getString("username1"), rs.getString("homeclub1"), rs.getString("ghin1"), rs.getString("gender1"), rs.getString("hndcp1"), rs.getString("p1cw"), team_num, con, out);
                if (!rs.getString("player2").equals("")) tourExPrintPlayer(rs.getString("player2"), rs.getString("username2"), rs.getString("homeclub2"), rs.getString("ghin2"), rs.getString("gender2"), rs.getString("hndcp2"), rs.getString("p2cw"), team_num, con, out);
                if (!rs.getString("player3").equals("")) tourExPrintPlayer(rs.getString("player3"), rs.getString("username3"), rs.getString("homeclub3"), rs.getString("ghin3"), rs.getString("gender3"), rs.getString("hndcp3"), rs.getString("p3cw"), team_num, con, out);
                if (!rs.getString("player4").equals("")) tourExPrintPlayer(rs.getString("player4"), rs.getString("username4"), rs.getString("homeclub4"), rs.getString("ghin4"), rs.getString("gender4"), rs.getString("hndcp4"), rs.getString("p4cw"), team_num, con, out);
                if (!rs.getString("player5").equals("")) tourExPrintPlayer(rs.getString("player5"), rs.getString("username5"), rs.getString("homeclub5"), rs.getString("ghin5"), rs.getString("gender5"), rs.getString("hndcp5"), rs.getString("p5cw"), team_num, con, out);

            }
            
            team_num++;
        }
        
        pstmt.close();
        out.close();
        
    } catch (Exception exc) { 
        
        out.println("<br>Error occurred during operation! Error= " + exc.getMessage());
        
    } finally {
        
        try { rs.close(); }
        catch (Exception ignore) {}
        
        try { pstmt.close(); }
        catch (Exception ignore) {}
        
        try { rs2.close(); }
        catch (Exception ignore) {}
        
        try { pstmt2.close(); }
        catch (Exception ignore) {}
    }
    
    /*
    try {

        PreparedStatement pstmt = con.prepareStatement (
            "SELECT * FROM evntsup2b WHERE name = ? AND player1 <> '' ORDER BY id");

        pstmt.clearParameters();
        pstmt.setString(1, name);
        
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {

            
            
        }
        
        pstmt.close();

    }
    catch (Exception exc) {
        
    }
   */
 }
 
 // **************************************************
 // Prints info for one player to file using TPP standardized output format
 // **************************************************
 private void tppPrintPlayer(String player, String username, String clubName, String ghin, String gender, String hndcp, Connection con, PrintWriter out) {

    if (player.equals("")) return;

    String memNum = "";
    String tmp_player = "";
    boolean isGuest = false;

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
        
        pstmt = con.prepareStatement("SELECT guest FROM guest5 WHERE LEFT(?, LENGTH(guest)) = guest");
        pstmt.clearParameters();
        pstmt.setString(1, player);
        
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            
            isGuest = true;
            tmp_player = player.substring(rs.getString(1).length()).trim(); // trim off the guest type
            
            // if there is no player name, just guest name then revert back to the guest name
            if (!tmp_player.equals("")) player = tmp_player;
        }
        
    } catch (Exception exc) {
    
        out.println("Error occurred determining guest type for player: " + player + " Error= " + exc.getMessage());
    
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
    
    String first = "";
    String middle = "";
    String last = "";

    // check to see see if this is a member or a guest (if name but no username then it should be a guest)
    if ( !isGuest ) {

        clubName = ""; // don't include the club name for members'

        if (!username.equals("")) {

            try {

                pstmt = con.prepareStatement("SELECT memNum, name_last, name_first, name_mi, ghin, gender FROM member2b WHERE username = ?");
                pstmt.clearParameters();
                pstmt.setString(1, username);

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    /*
                    if (clubName == null) clubName = "";
                    if (username == null) username = "";
                    if (hndcp == null) hndcp = "";

                    if (ghin == null || ghin.equals("")) {

                    ghin = rs.getString("ghin");
                    }

                    if (gender == null || gender.equals("")) {

                    gender = rs.getString("gender");
                    }
                    */

                    first = rs.getString("name_first");
                    middle = rs.getString("name_mi");
                    last = rs.getString("name_last");

                    if (rs.getString("memNum") != null) memNum = rs.getString("memNum");

                    /*
                    // print out results to file
                    out.println("" +
                    "\"" + rs.getString("name_last") + ", " + rs.getString("name_first") + "\"," +
                    "\"" + clubName + "\"," +
                    "\"" + ghin + "\"," +
                    "\"" + memNum + "\"," +
                    "\"" + gender + "\"," +
                    "\"" + hndcp + "\"");
                    */
                } // end if rs

                rs.close();
                pstmt.close();
            
            } catch (Exception exc) {

                out.println("Error occurred while loading player: " + player + " details. Error= " + exc.getMessage());
            
            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}

            }

            // end if empty username

        }
        // end if not a guest

    } else {

        // this should be a guest so let's breakdown their name in to parts

        StringTokenizer tok = new StringTokenizer( player );

        if ( tok.countTokens() == 2 ) {         // first name, last name

            first = tok.nextToken();
            last = tok.nextToken();

        } else if ( tok.countTokens() == 3 ) {  // first name, mi, last name

            first = tok.nextToken();
            middle = tok.nextToken();
            last = tok.nextToken();

        } else {

            // there appears to be only one part to the name
            last = player;

        }

    }
    
    if (ghin == null) ghin = "";
    if (gender == null) gender = "";
    if (clubName == null || clubName.equalsIgnoreCase("null")) clubName = "";
    middle = middle.replace(".", "");

    if (hndcp.equals("99.0") || hndcp.equals("-99.0") || hndcp == null) hndcp = "";
    if (hndcp.startsWith("-")) {
        
        hndcp = hndcp.substring(1, hndcp.length() - 1);
    
    } else {
    
        hndcp = "+" + hndcp;
        
    }

    // if member
    if (!username.equals("")) {

        // if the ghin # is not here (old events) try to look it up.
        if (ghin.equals("")) ghin = Utilities.getHdcpNum(username, con);

        // if the gender is not here (old events) try to look it up.
        if (gender.equals("")) gender = Utilities.getGender(username, con);
    }

    // remove dash from ghin
    ghin = ghin.replace("-","");

    out.println("\"" + last + "\",\"" + first + "\",\"" + middle + "\"," + // "\"" + player + "\"," +
                "\"" + clubName + "\"," +
                "\"" + ghin + "\"," + 
                "\"\"," +                  // no member #
                "\"" + gender + "\"," +
                "\"" + hndcp + "\"");

 }
 
 
 private void eventManPrintPlayer(String player, String username, String clubName, String ghin, String gender, String hndcp, int team_num, int seq_num, Connection con, PrintWriter out) {

    boolean isGuest = false;
    String tmp_memGst = "M";   // default to member
    String tmp_player = "";

    int tmp_count = 0;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    // determin is this is a guest or a member
    try {
         
        pstmt = con.prepareStatement("SELECT guest FROM guest5 WHERE LEFT(?, LENGTH(guest)) = guest");
        pstmt.clearParameters();
        pstmt.setString(1, player);

        rs = pstmt.executeQuery();

        if (rs.next()) {

            isGuest = true;
            tmp_memGst = "G";
            tmp_player = player.substring(rs.getString(1).length()).trim(); // trim off the guest type

            // if there is no player name, just guest name then revert back to the guest name
            if (!tmp_player.equals("")) player = tmp_player;
        }

    } catch (Exception exc) {

        out.println("Error occurred determining guest type for player: " + player + " Error= " + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
     
    // parse the name (guest type has been removed)
    String fname = "";
    String lname = "";
    StringTokenizer tok = new StringTokenizer(player, " ");

    if (tok.countTokens() == 3) {

        fname = tok.nextToken();
        lname = tok.nextToken(); // skip mi
        lname = tok.nextToken();
    
    } else if (tok.countTokens() == 2) {

        fname = tok.nextToken();
        lname = tok.nextToken();

    } else {
    
        // in this case player is likely a single word guest type OR just one part of the guests name
        lname = player;
    }

    // make sure no nulls
    if (fname == null) fname = "";
    if (lname == null) lname = "";
    if (ghin == null) ghin = "";
    if (gender == null) gender = "";
    if (hndcp == null) hndcp = "";

    // if member
    if (!username.equals("")) {

        // if the ghin # is not here (old events) try to look it up.
        if (ghin.equals("")) ghin = Utilities.getHdcpNum(username, con);

        // if the gender is not here (old events) try to look it up.
        if (gender.equals("")) gender = Utilities.getGender(username, con);
    }

    // output the row
    out.println("\"" + ghin + "\"," + 
                "\"" + lname + "\"," +     // last name
                "\"" + fname + "\"," +     // first name
                "\"\"," +                  // hdcp index  " + hndcp + "
                "\"" + gender + "\"," +    // gender
                "\"" + team_num + "\"," +  // team #
                "\"" + seq_num + "\"," +   // player pos within team #
                "\"" + tmp_memGst + "\""); // member or guest
     
 }


 private void golfNetPrintPlayer(String player, String username, String clubName, String ghin, String gender, String hndcp, int team_num, Connection con, PrintWriter out) {

    boolean isGuest = false;
    String tmp_memGst = "M";   // default to member
    String tmp_player = "";

    int tmp_count = 0;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    // determin is this is a guest or a member
    try {

        pstmt = con.prepareStatement("SELECT guest FROM guest5 WHERE LEFT(?, LENGTH(guest)) = guest");
        pstmt.clearParameters();
        pstmt.setString(1, player);

        rs = pstmt.executeQuery();

        if (rs.next()) {

            isGuest = true;
            tmp_memGst = "G";
            tmp_player = player.substring(rs.getString(1).length()).trim(); // trim off the guest type

            // if there is no player name, just guest name then revert back to the guest name
            if (!tmp_player.equals("")) player = tmp_player;
        }

    } catch (Exception exc) {

        out.println("Error occurred determining guest type for player: " + player + " Error= " + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    // parse the name (guest type has been removed)
    String fname = "";
    String lname = "";
    StringTokenizer tok = new StringTokenizer(player, " ");

    if (tok.countTokens() == 3) {

        fname = tok.nextToken();
        lname = tok.nextToken(); // skip mi
        lname = tok.nextToken();

    } else if (tok.countTokens() == 2) {

        fname = tok.nextToken();
        lname = tok.nextToken();

    } else {

        // in this case player is likely a single word guest type OR just one part of the guests name
        lname = player;
    }

    // make sure no nulls
    if (fname == null) fname = "";
    if (lname == null) lname = "";
    if (ghin == null) ghin = "";
    if (gender == null) gender = "";
    if (hndcp == null) hndcp = "";

    // if member
    if (!username.equals("")) {

        // if the ghin # is not here (old events) try to look it up.
        if (ghin.equals("")) ghin = Utilities.getHdcpNum(username, con);

        // if the gender is not here (old events) try to look it up.
        if (gender.equals("")) gender = Utilities.getGender(username, con);
    }

    // output the row
    out.println(
                "\"" + team_num + "\"," +  // team #
                "\"" + ghin + "\"," +
                "\"" + lname + "\"," +     // last name
                "\"" + fname + "\"," +     // first name
                "\"" + gender + "\"," +    // gender
                "\"\"," +                  // hdcp index  " + hndcp + "
                "\"" + tmp_memGst + "\"," +// member or guest
                "\"" + clubName + "\""); 
    
    //          "\"" + seq_num + "\"," +   // player pos within team #

 }


 //
 //  Export method for Tournament Expert (TourEx)
 //
 private void tourExPrintPlayer(String player, String username, String clubName, String ghin, String gender, String hndcp, String cw, int team_num, Connection con, PrintWriter out) {

    boolean isGuest = false;
    String tmp_memGst = "M";   // default to member
    String tmp_player = "";

    int tmp_count = 0;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    // determin is this is a guest or a member
    try {

        pstmt = con.prepareStatement("SELECT guest FROM guest5 WHERE LEFT(?, LENGTH(guest)) = guest");
        pstmt.clearParameters();
        pstmt.setString(1, player);

        rs = pstmt.executeQuery();

        if (rs.next()) {

            isGuest = true;
            tmp_memGst = "G";
            tmp_player = player.substring(rs.getString(1).length()).trim(); // trim off the guest type

            // if there is no player name, just guest name then revert back to the guest name
            if (!tmp_player.equals("")) player = tmp_player;
        }

    } catch (Exception exc) {

        out.println("Error occurred determining guest type for player: " + player + " Error= " + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    // parse the name (guest type has been removed)
    String fname = "";
    String lname = "";
    StringTokenizer tok = new StringTokenizer(player, " ");

    if (tok.countTokens() == 3) {

        fname = tok.nextToken();
        lname = tok.nextToken(); // skip mi
        lname = tok.nextToken();

    } else if (tok.countTokens() == 2) {

        fname = tok.nextToken();
        lname = tok.nextToken();

    } else {

        // in this case player is likely a single word guest type OR just one part of the guests name
        lname = player;
    }

    // make sure no nulls
    if (clubName == null || clubName.equals("null")) clubName = "";
    if (fname == null) fname = "";
    if (lname == null) lname = "";
    if (ghin == null) ghin = "";
    if (gender == null) gender = "";
    if (hndcp == null) hndcp = "";

    // if member
    if (!username.equals("")) {

        // if the ghin # is not here (old events) try to look it up.
        if (ghin.equals("")) ghin = Utilities.getHdcpNum(username, con);

        // if the gender is not here (old events) try to look it up.
        if (gender.equals("")) gender = Utilities.getGender(username, con);
    }

    // output the row
    out.println(
                "\"" + team_num + "\"," +   // team #
                "\"" + ghin + "\"," +       // hndcp number
                "\"" + lname + "\"," +      // last name
                "\"" + fname + "\"," +      // first name
                "\"" + gender + "\"," +     // gender
                "\"" + hndcp + "\"," +      // hdcp index
                "\"" + tmp_memGst + "\"," + // member or guest (M or G)
                "\"" + clubName + "\"," +   // name of home club - if prompted
                "\"" + cw + "\"");          // mode of trans
    
 }

}