/***************************************************************************************     
 *   Proshop_events:  This servlet will process the 'Special Events' request from 
 *                    the Proshop's Config page.
 *
 *
 *   called by:  proshop menu (to doGet) and Proshop_events (to doPost from HTML built here)
 *
 *   created: 12/18/2001   Bob P.
 *
 *   last updated:
 *
 *        1/31/15   Add ID column to event listing table
 *        9/26/14   Add POS charge data for Abacus21 (at Waialae).
 *        2/18/14   Interlachen - add a question for the tees that member will play from (custom).
 *        2/18/14   Remove ext_Q custom - event questions have been increased to 64 chars (from 32).
 *        1/23/14   The Oaks Club (theoaksclub) - Allow them to use the recurring event feature.
 *        1/13/14   Add recur processing.
 *        1/12/14   Add 'Delete' button to event list.  Also, change 'Select' button to 'Edit'.
 *        1/08/14   Add Golf Genius export type.
 *        9/27/13   User will now be returned to the 'Show Previous' page if they entered an event configuration from there.
 *        6/18/13   Added a "Previous Events" button so that events that occurred in the past will not be displayed by default. This will allow users to toggle between current and previous.
 *        2/13/13   Tweak TinyMCE settings
 *        1/06/13   Add TinyMCE to the edit event form, cleaned up some html for more consistant rendering
 *       11/08/12   Add option (memedit) to allow members to edit event times after moved to tee sheet. 
 *        5/01/12   Fixed a typo reported in the delete event dialog ("absolutely" was written "absolutly").
 *       12/20/11   Added support for selecting event categories when adding/editing events (case 2076).
 *        8/23/10   Fixed and instance where "tee sheet" was being referenced on FlxRez side.
 *        5/20/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *       12/16/09   Added ext_Q boolean to allow clubs to ask custom questions over 32 chars in length
 *                  note: need to update otherQ1-3 in the clubs db table before adding them to this custom
 *       12/08/09   Do not delete events or event signups - mark them inactive instead so we can easily restore them.
 *       10/12/09   Changes to prevent doubles spaces in event names
 *        9/28/09   Added support for Activities
 *        6/25/09   Add Tournament Expert (TourEx) option to export types
 *        5/01/09   Don't allow users to convert a non-season long event to season long event
 *        3/02/09   Add asterisk to guest only questions
 *        2/05/09   Added 72 holes as an option during event configuration
 *       12/03/08   Add GolfNet TMS option to export types
 *       11/10/08   Added changes for additional signup information
 *        8/15/08   Add more years to the start and end dates until we come up with a better method.
 *        8/11/08   Update to limited access user restrictions
 *        7/24/08   Added limited access proshop users checks
 *        5/05/08   Remove Event man option until ready for implementation.
 *        4/26/08   Fix for gender = -1 issue
 *        4/07/08   Fixes for season long events
 *        4/02/08   Change sorting for season long events, now sorts by cutoff date
 *        3/27/08   Added sorting to Event Name, Date, Gender
 *        3/24/08   Add gender field to event config
 *       11/15/07   Fix menus on copy event page
 *       10/24/07   Added double confirmation to the delete event button
 *        3/21/07   Removed 254 char restriction on Itinerary textarea field (db chaned to from varchar to text)
 *        6/09/06   Added javascript confirmation to delete button
 *        1/24/05   Ver 5 - change club2 to club5.
 *        9/16/04   V5 - change getClub from SystemUtils to common.
 *        7/08/04   Add 'Make Copy' option to allow pro to copy an existing event.
 *        5/12/04   Add sheet= parm for call from Proshop_sheet.
 *        5/05/04   Add 2nd set of block times for events.
 *        2/17/04   Add POS charge codes, new Modes of Trans, and allow user to change the name of the event.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        2/10/03   Add processing for member signup.
 *        1/10/03   Enhancements for Version 2 of the software.
 *                  Allow for multiple courses.
 *                  Add option to allow members to sign up for event online.
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
import com.foretees.common.Labels;
import com.foretees.common.Utilities;
import com.foretees.common.dateOpts;
import com.foretees.common.getActivity;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Connect;

public class Proshop_events extends HttpServlet {

   
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //**************************************************
 // Process the initial request from Proshop_main
 //**************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) return;

   Connection con = Connect.getCon(req);                      // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_EVENT", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_EVENT", out);
   }

   //
   //  Check if call is to copy an existing event
   //
   if (req.getParameter("copy") != null || req.getParameter("recur") != null) {

      doCopy(req, out, con);        // process the copy request
      return;
   }

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   

   //
   // Define some parms to use in the html
   //
   String name = "";        // name of event
   int event_id = 0;
   int first_hr = 0;        // start time hr
   int first_min = 0;       // start time min
   int last_hr = 0;         // end time hr
   int last_min = 0;        // end time min
   int hour = 0;            // end time min
   int year = 0;            // year
   int month = 0;           // month
   int day = 0;             // day
   int multi = 0;           // multi
   int season = 0;          // season
   int gender = 0;          // gender
   
   long cur_date = 0;       // current date
     
   String color = "";       // color for event displays
   String course = "";      // course name
   String fb = "";          // Front/back Indicator
       
   String f_ampm = "";
   String l_ampm = "";

   boolean b = false;

   boolean showOld = (req.getParameter("showOld") != null) ? true : false;
   boolean sortDesc = (req.getParameter("desc") != null) ? true : false;
   
   String sortBy = (req.getParameter("sortBy") != null) ? req.getParameter("sortBy") : "date"; // default is to sort by date

   if (sess_activity_id == 0) {

       //
       //  parm block to hold the club parameters
       //
       parmClub parm = new parmClub(sess_activity_id, con);

       //
       // Get the Multiple Course Option
       //
       try {
          getClub.getParms(con, parm);        // get the club parms
          multi = parm.multi;
       } catch (Exception ignore) { }

   }

   //
   //  Build the HTML page to display the existing events
   //
   out.println(SystemUtils.HeadTitle("Proshop Events Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>Events</b>");
      out.println("</font>");
      out.println("<font color=\"#FFFFFF\" size=\"2\"><br><br>");
      out.println("To change or remove an event, click on the Edit or Delete button within the event.");
      out.println("<br></font></td></tr></table>");
      out.println("<br><br>");
      
      out.println("<div style=\"margin-left:auto; margin-right:auto; width:50%;\">");
      out.println("<form action=\"Proshop_events\" method=\"GET\">");
      if (showOld) {
          out.println("<input type=\"submit\" value=\"Show Current\"><br><br>");
      } else {
          out.println("<input type=\"submit\" name=\"showOld\" value=\"Show Previous\"><br><br>");
      }
      out.println("</form>");
      out.println("</div>");

      out.println("<table border=\"2\" cellpadding=\"5\"><tr bgcolor=\"#F5F5DC\">");
      if (multi != 0) {           // if multiple courses supported for this club
         out.println("<td colspan=\"9\" align=\"center\">");
      } else {
         out.println("<td colspan=\"8\" align=\"center\">");
      }
      out.println("<font size=\"2\">");
      out.println("<p align=\"center\"><font size=3><b>" + (showOld ? "Previous" : "Currently Active") + " Events</b></font></p>");
      out.println("</font></td></tr>");
      out.println("<tr bgcolor=\"#F5F5DC\"><td align=\"center\">");
      out.println("<font size=\"2\"><p><b>");
      out.println("<a href='?sortBy=name" + ((sortBy.equals("name") && !sortDesc) ? "&desc" : "") + ((showOld) ? "&showOld" : "") + "' style='color:#336633'>Event Name</a>");
      out.println("</b></p>");
      out.println("</font></td>");

      if (sess_activity_id == 0) {
          if (multi != 0) {
             out.println("<td align=\"center\">");
             out.println("<font size=\"2\"><p><b>Course</b></p>");
             out.println("</font></td>");
          }
          out.println("<td align=\"center\">");
          out.println("<font size=\"2\"><p><b>Tees</b></p>");
          out.println("</font></td>");
      }

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>");
      out.println("<a href='?sortBy=date" + ((sortBy.equals("date") && !sortDesc) ? "&desc" : "") + ((showOld) ? "&showOld" : "") + "' style='color:#336633'>Date</a>");
      out.println("</b></p></font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Start Time</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>End Time</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>");
      out.println("<a href='?sortBy=gender" + ((sortBy.equals("gender") && !sortDesc) ? "&desc" : "") + ((showOld) ? "&showOld" : "") + "' style='color:#336633'>Gender</a>");
      out.println("</b></p></font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>ID</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p>&nbsp;</p>");      // empty for select button
      out.println("</font></td></tr>");

   //
   //  Get and display the existing events (one table row per event)
   //
   
      /*
       *
       select *, if(season=1, c_date, date) as date2 from events2b
       
       
       *
       */
      
   try {
       
      cur_date = Utilities.getDate(con);

      stmt = con.createStatement();        // create a statement

      String sql = "" +
                   "SELECT *, IF(season=1, c_date, date) as date2 " +
                   "FROM events2b " +
                   "WHERE activity_id = " + sess_activity_id + " AND inactive = 0 ";
      if (showOld) {
          sql += "AND date < " + cur_date + " ";
      } else {
          sql += "AND date >= " + cur_date + " ";
      }
      if (sortBy.equals("date")) {               // sort by event date, time
          sql += "ORDER BY date2 " + ((sortDesc) ? "DESC" : "") + ", act_hr;";
      } else if (sortBy.equals("name")) {        // sort by event name
          sql += "ORDER BY name " + ((sortDesc) ? "DESC" : "") + ";";
      } else if (sortBy.equals("gender")) {        // sort by gender
          if (sortDesc) {
              // womens first
              sql += "ORDER BY gender = 0, gender = 1, gender = 2, gender = 3, date2, act_hr;";
          } else {
              // mens first
              sql += "ORDER BY gender = 0, gender = 1, gender = 3, gender = 2, date2, act_hr;";
          }
          //sql += "ORDER BY gender " + ((sortDesc) ? "DESC" : "") + ";";
      }
      
      //  ((showOld) ? "WHERE date < " + date : "WHERE date >= " + date) + " "
      
      rs = stmt.executeQuery( sql );

      while (rs.next()) {

         b = true;                     // indicate events exist

         event_id = rs.getInt("event_id");
         name = rs.getString("name");
         year = rs.getInt("year");
         month = rs.getInt("month");
         day = rs.getInt("day");
         first_hr = rs.getInt("start_hr");
         first_min = rs.getInt("start_min");
         last_hr = rs.getInt("end_hr");
         last_min = rs.getInt("end_min");
         color = rs.getString("color");
         course = rs.getString("courseName");
         fb = rs.getString("fb");
         season = rs.getInt("season");
         gender = rs.getInt("gender");

         if (season == 1) fb = "N/A";
         
         //
         //  some values must be converted for display
         //
         f_ampm = " AM";         // default to AM
         if (first_hr > 12) {
            f_ampm = " PM";
            first_hr = first_hr - 12;                // convert to 12 hr clock value
         }

         if (first_hr == 12) {
            f_ampm = " PM";
         }

         l_ampm = " AM";         // default to AM
         if (last_hr > 12) {
            l_ampm = " PM";
            last_hr = last_hr - 12;                  // convert to 12 hr clock value
         }

         if (last_hr == 12) {
            l_ampm = " PM";
         }

         if (color.equals( "Default" )) {
            
            out.println("<tr bgcolor=\"#F5F5DC\">");
         } else {
            out.println("<tr bgcolor=" + color + ">");
         }
         out.println("<td align=\"center\">");
         out.println("<form method=\"post\" action=\"Proshop_events\" target=\"bot\">");
         out.println("<font size=\"2\"><p>" +name+ "</p>");
         out.println("</font></td>");

         if (sess_activity_id == 0) {
             if (multi != 0) {
                out.println("<td align=\"center\">");
                out.println("<font size=\"2\"><p>" +course+ "</p>");
                out.println("</font></td>");
             }
             out.println("<td align=\"center\">");
             out.println("<font size=\"2\"><p>" +fb+ "</p>");
             out.println("</font></td>");
         }
         out.println("<td align=\"center\">");
         if (season == 1) {
             out.println("<font size=\"2\"><p>Season Long</p>");
         } else {
             out.println("<font size=\"2\"><p>" +month+ "/" + day + "/" + year + "</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (season == 1) {
             out.println("<font size=\"2\"><p>N/A</p>");
         } else {
            out.println("<font size=\"2\"><p>" + first_hr + ":" + String.format("%02d", first_min) + " " + f_ampm + "</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (season == 1) {
             out.println("<font size=\"2\"><p>N/A</p>");
         } else {
            out.println("<font size=\"2\"><p>" + last_hr + ":" + String.format("%02d", last_min) + " " + l_ampm + "</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" + Labels.gender_opts[gender] + "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" + event_id + "</p>");
         out.println("</font></td>");
         
         out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");    // must pass whole name!!!!
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         
         if (showOld) {
             out.println("<input type=\"hidden\" name=\"showOld\" value=\"Yes\">");
         }
         
         out.println("<td align=\"center\">");
         out.println("<table><tr>");
         out.println("<td align=\"center\"><input type=\"submit\" value=\"Edit\"></form></td>");
         
         out.println("<form action=\"Proshop_editevnt\" method=\"post\">");
         out.println("<input type=\"hidden\" name=\"event_id\" value=\"" + event_id +"\">");
         out.println("<input type=\"hidden\" name=\"oldName\" value=\"" + name + "\">");   
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");         
         out.println("<input type=\"hidden\" name=\"sheet\" value=\"no\">");         // not from Proshop_sheet
         if (showOld) {
             out.println("<input type=\"hidden\" name=\"showOld\" value=\"Yes\">");
         }
         out.println("<td align=\"center\"><input type=\"submit\" name=\"Delete\" value=\"Delete\" onclick=\"return confirmDelete()\"></form></td></tr></table>");
               out.println("<script type='text/javascript'>");
               out.println("function confirmDelete() {");
               out.println("ret = confirm('WARNING! You are about to PERMANENTLY delete an event and ALL the sign-ups for the event!\\n\\nAre you sure you want to continue?');");
               out.println("if (ret) ret = confirm('Note: This action is permanent and CAN NOT be undone!\\n\\nAre you absolutely sure you want to delete this event?');");
               out.println("return ret;");
               out.println("}");
               out.println("</script>");
         out.println("</td></tr>");
         
      }  // end of while loop

      stmt.close();

      if (!b) {
        
         out.println("<p>No Events Exist at This Time</p>");
      }
   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;

   }  // end of try

   //
   //  End of HTML page
   //
   out.println("</table><br>");                           // end of event table

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   out.println("</center></font><br></body></html>");

 }  // end of doGet

 
 //
 //****************************************************************
 // Process the form request from Proshop_events page displayed above.
 //
 // Use the name provided to locate the event record and then display
 // the record to the user and prompt for edit or delete action. 
 //
 //****************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   Statement stmtc = null;
   PreparedStatement pstmt1 = null;
   ResultSet rs = null;

   //String [] month_table = { "inv", "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
   //                          "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = Connect.getCon(req);            // get DB connection
   
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_EVENT", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_EVENT", out);
   }

   
   if (req.getParameter("recur") != null) {    // if this is a recur request

      doRecur(req, resp, out, con);    // go prompt the user for the recur options
   }
   
   if (req.getParameter("recur2") != null) {    // if this is the 2nd recur request

      doRecur2(req, resp, out, con);    // go process the recur
   }
   
   
   String club = (String)session.getAttribute("club");      // get club name
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   
   
   boolean copy = false;
   boolean showOld = (req.getParameter("showOld") != null) ? true : false;

   int activity_id = 0;
   int event_id = 0;
   long date = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int shr = 0;
   int smin = 0;
   int ehr = 0;
   int emin = 0;
   int ahr = 0;
   int amin = 0;
   int type = 0;
   int multi = 0;
   int i = 0;
   int signUp = 0;          // member sign up option
   int size = 0;
   int max = 0;
   int guests = 0;
   int mc = 0;
   int pc = 0;
   int wa = 0;
   int ca = 0;
   int x = 0;
   int xhrs = 0;
   int holes = 0;
   int gstOnly = 0;
   int gender = 0;
   int su_month = 0;
   int su_day = 0;
   int su_year = 0;
   //int su_hr = 0;
   //int su_min = 0;
   int su_time = 0;
   int c_time = 0;
   int c_month = 0;
   int c_day = 0;
   int c_year = 0;
   //int c_hr = 0;
   //int c_min = 0;
   int stime2 = 0;
   int etime2 = 0;
   int shr2 = 0;
   int ehr2 = 0;
   int smin2 = 0;
   int emin2 = 0;
   int export_type = 0;
   int season = 0;

   int [] tmodei = new int [16];   // Modes of trans indicators

   int ask_hdcp = 0;
   int ask_homeclub = 0;
   int ask_gender = 0;
   int ask_phone = 0;
   int ask_email = 0;
   int ask_address = 0;
   int ask_shirtsize = 0;
   int ask_shoesize = 0;
   int ask_otherQ1 = 0;
   int ask_otherQ2 = 0;
   int ask_otherQ3 = 0;
   int ask_custom1 = 0;
   int req_guestname = 0;
   int req_hdcp = 0;
   int req_homeclub = 0;
   int req_gender = 0;
   int req_phone = 0;
   int req_email = 0;
   int req_address = 0;
   int req_shirtsize = 0;
   int req_shoesize = 0;
   int req_otherQ1 = 0;
   int req_otherQ2 = 0;
   int req_otherQ3 = 0;
   int req_custom1 = 0;
   int memedit = 0;
      
   long su_date = 0;
   long c_date = 0;

   double member_fee = 0;
   double member_tax = 0;
   double guest_fee = 0;
   double guest_tax = 0;

   String color = "";
   String format = "";
   String pairings = "";
   String memcost = "";
   String gstcost = "";
   //String c_ampm = "";
   String su_ampm = "";
   //String sampm2 = "";
   //String eampm2 = "";
   String itin = "";
   String fb = "";
   String fb2 = "";
   String mempos = "";
   String gstpos = "";
   String sheet = "no";                 // default to NOT a call from Proshop_sheet
   String email1 = "";
   String email2 = "";
   String otherQ1 = "";
   String otherQ2 = "";
   String otherQ3 = "";
   String locations_csv = "";
   String member_item_code = "";
   String guest_item_code = "";
   String posType = "";

/*
   boolean USE_NEW_SIGNUP = false;         // for testing new features

   if (club.startsWith("demo") ||
       club.equals("ridgeclub") 
      ) USE_NEW_SIGNUP = true;
*/

   //
   //  Array to hold the course names
   //
   String courseName = "";
   ArrayList<String> course = new ArrayList<String>();

   String courseName1 = "";
   String oldCourse = "";      
   String name = "";
   String oldName = "";
   
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);        // get the year
   int thisMonth = cal.get(Calendar.MONTH) + 1;
   int thisDay = cal.get(Calendar.DAY_OF_MONTH);

   //
   // Get the name of the event 
   //
   if (req.getParameter("name") != null) {

      name = req.getParameter("name");
   }
   
   if (req.getParameter("eventId") != null) {   // if event_id provided instead of the name (from doCopy)

      event_id = Integer.parseInt(req.getParameter("eventId"));
      
      try {
         
         pstmt1 = con.prepareStatement (
                  "SELECT name FROM events2b WHERE event_id = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setInt(1, event_id);
         rs = pstmt1.executeQuery();      // execute the prepared pstmt1

         if (rs.next()) {

            name = rs.getString("name");
         }
         pstmt1.close();
         
      }
      catch (Exception exc) {

         dbError(exc.toString(), out);
         return;
      }
   }
   
   
   oldName = name;                // save original event name

   if (req.getParameter("sheet") != null) {

      sheet = req.getParameter("sheet");
   }

   if (req.getParameter("copy") != null) {

      copy = true;            // this is a copy request (from doCopy below)
   }


   try {
      //
      // Get the Multiple Course Option from the club db
      //
      stmtc = con.createStatement();        // create a statement

      rs = stmtc.executeQuery("SELECT multi, posType FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);
         posType = rs.getString(2);
      }
      stmtc.close();

      if (multi != 0) {           // if multiple courses supported for this club

         course = Utilities.getCourseNames(con);     // get all the course names
      }

      //
      // Get the event from the events table
      //
      pstmt1 = con.prepareStatement (
               "SELECT * FROM events2b WHERE name = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);
      rs = pstmt1.executeQuery();      // execute the prepared pstmt1
      
      if (rs.next()) {
      
         //
         //  Found the event record - get it
         //
         activity_id = rs.getInt("activity_id");
         event_id = rs.getInt("event_id");
         date = rs.getLong("date");
         year = rs.getInt("year");
         month = rs.getInt("month");
         day = rs.getInt("day");
         shr = rs.getInt("start_hr");
         smin = rs.getInt("start_min");
         ehr = rs.getInt("end_hr");
         emin = rs.getInt("end_min");
         color = rs.getString("color");
         type = rs.getInt("type");
         ahr = rs.getInt("act_hr");
         amin = rs.getInt("act_min");
         courseName1 = rs.getString("courseName");
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
         c_date = rs.getLong("c_date");
         c_time = rs.getInt("c_time");
         itin = rs.getString("itin");
         mc = rs.getInt("mc");
         pc = rs.getInt("pc");
         wa = rs.getInt("wa");
         ca = rs.getInt("ca");
         gstOnly = rs.getInt("gstOnly");
         gender = rs.getInt("gender");
         x = rs.getInt("x");
         xhrs = rs.getInt("xhrs");
         holes = rs.getInt("holes");
         su_month = rs.getInt("su_month");
         su_day = rs.getInt("su_day");
         su_year = rs.getInt("su_year");
         su_date = rs.getLong("su_date");
         su_time = rs.getInt("su_time");
         fb = rs.getString("fb");
         mempos = rs.getString("mempos");
         gstpos = rs.getString("gstpos");
         tmodei[0] = rs.getInt("tmode1");
         tmodei[1] = rs.getInt("tmode2");
         tmodei[2] = rs.getInt("tmode3");
         tmodei[3] = rs.getInt("tmode4");
         tmodei[4] = rs.getInt("tmode5");
         tmodei[5] = rs.getInt("tmode6");
         tmodei[6] = rs.getInt("tmode7");
         tmodei[7] = rs.getInt("tmode8");
         tmodei[8] = rs.getInt("tmode9");
         tmodei[9] = rs.getInt("tmode10");
         tmodei[10] = rs.getInt("tmode11");
         tmodei[11] = rs.getInt("tmode12");
         tmodei[12] = rs.getInt("tmode13");
         tmodei[13] = rs.getInt("tmode14");
         tmodei[14] = rs.getInt("tmode15");
         tmodei[15] = rs.getInt("tmode16");
         stime2 = rs.getInt("stime2");
         etime2 = rs.getInt("etime2");
         fb2 = rs.getString("fb2");
         season = rs.getInt("season");
         export_type = rs.getInt("export_type");
         email1 = rs.getString("email1");
         email2 = rs.getString("email2");
         
         ask_homeclub = rs.getInt("ask_homeclub");
         ask_hdcp = rs.getInt("ask_hdcp");
         ask_gender = rs.getInt("ask_gender");
         ask_phone = rs.getInt("ask_phone");
         ask_email = rs.getInt("ask_email");
         ask_address = rs.getInt("ask_address");
         ask_shirtsize = rs.getInt("ask_shirtsize");
         ask_shoesize = rs.getInt("ask_shoesize");
         ask_otherQ1 = rs.getInt("ask_otherA1");
         ask_otherQ2 = rs.getInt("ask_otherA2");
         ask_otherQ3 = rs.getInt("ask_otherA3");
         req_guestname = rs.getInt("req_guestname");
         req_hdcp = rs.getInt("req_hdcp");
         req_homeclub = rs.getInt("req_homeclub");
         req_gender = rs.getInt("req_gender");
         req_phone = rs.getInt("req_phone");
         req_email = rs.getInt("req_email");
         req_address = rs.getInt("req_address");
         req_shirtsize = rs.getInt("req_shirtsize");
         req_shoesize = rs.getInt("req_shoesize");
         req_otherQ1 = rs.getInt("req_otherA1");
         req_otherQ2 = rs.getInt("req_otherA2");
         req_otherQ3 = rs.getInt("req_otherA3");
         otherQ1 = rs.getString("otherQ1");
         otherQ2 = rs.getString("otherQ2");
         otherQ3 = rs.getString("otherQ3");
         locations_csv = rs.getString("locations");
         memedit = rs.getInt("memedit");
         ask_custom1 = rs.getInt("ask_custom1");   // custom question - tee to use (fields must be added to db table if other clubs use this custom!!!)  
         req_custom1 = rs.getInt("req_custom1");     
         member_item_code = rs.getString("member_item_code");
         guest_item_code = rs.getString("guest_item_code");
         member_fee = rs.getDouble("member_fee");
         member_tax = rs.getDouble("member_tax");
         guest_fee = rs.getDouble("guest_fee");
         guest_tax = rs.getDouble("guest_tax");

      } else {      // not found - try filtering the name

         name = SystemUtils.filter(name);
         oldName = name;                  // save original event name

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, name);
         rs = pstmt1.executeQuery();      // execute the prepared pstmt1

         if (rs.next()) {

            //
            //  Found the event record - get it
            //
            activity_id = rs.getInt("activity_id");
            date = rs.getLong("date");
            year = rs.getInt("year");
            month = rs.getInt("month");
            day = rs.getInt("day");
            shr = rs.getInt("start_hr");
            smin = rs.getInt("start_min");
            ehr = rs.getInt("end_hr");
            emin = rs.getInt("end_min");
            color = rs.getString("color");
            type = rs.getInt("type");
            ahr = rs.getInt("act_hr");
            amin = rs.getInt("act_min");
            courseName1 = rs.getString("courseName");
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
            c_date = rs.getLong("c_date");
            c_time = rs.getInt("c_time");
            itin = rs.getString("itin");
            mc = rs.getInt("mc");
            pc = rs.getInt("pc");
            wa = rs.getInt("wa");
            ca = rs.getInt("ca");
            gstOnly = rs.getInt("gstOnly");
            gender = rs.getInt("gender");
            x = rs.getInt("x");
            xhrs = rs.getInt("xhrs");
            holes = rs.getInt("holes");
            su_month = rs.getInt("su_month");
            su_day = rs.getInt("su_day");
            su_year = rs.getInt("su_year");
            su_date = rs.getLong("su_date");
            su_time = rs.getInt("su_time");
            fb = rs.getString("fb");
            mempos = rs.getString("mempos");
            gstpos = rs.getString("gstpos");
            tmodei[0] = rs.getInt("tmode1");
            tmodei[1] = rs.getInt("tmode2");
            tmodei[2] = rs.getInt("tmode3");
            tmodei[3] = rs.getInt("tmode4");
            tmodei[4] = rs.getInt("tmode5");
            tmodei[5] = rs.getInt("tmode6");
            tmodei[6] = rs.getInt("tmode7");
            tmodei[7] = rs.getInt("tmode8");
            tmodei[8] = rs.getInt("tmode9");
            tmodei[9] = rs.getInt("tmode10");
            tmodei[10] = rs.getInt("tmode11");
            tmodei[11] = rs.getInt("tmode12");
            tmodei[12] = rs.getInt("tmode13");
            tmodei[13] = rs.getInt("tmode14");
            tmodei[14] = rs.getInt("tmode15");
            tmodei[15] = rs.getInt("tmode16");
            stime2 = rs.getInt("stime2");
            etime2 = rs.getInt("etime2");
            fb2 = rs.getString("fb2");
            season = rs.getInt("season");
            export_type = rs.getInt("export_type");
            email1 = rs.getString("email1");
            email2 = rs.getString("email2");
            ask_homeclub = rs.getInt("ask_homeclub");
            ask_hdcp = rs.getInt("ask_hdcp");
            ask_gender = rs.getInt("ask_gender");
            ask_phone = rs.getInt("ask_phone");
            ask_email = rs.getInt("ask_email");
            ask_address = rs.getInt("ask_address");
            ask_shirtsize = rs.getInt("ask_shirtsize");
            ask_shoesize = rs.getInt("ask_shoesize");
            ask_otherQ1 = rs.getInt("ask_otherA1");
            ask_otherQ2 = rs.getInt("ask_otherA2");
            ask_otherQ3 = rs.getInt("ask_otherA3");
            req_guestname = rs.getInt("req_guestname");
            req_hdcp = rs.getInt("req_hdcp");
            req_homeclub = rs.getInt("req_homeclub");
            req_gender = rs.getInt("req_gender");
            req_phone = rs.getInt("req_phone");
            req_email = rs.getInt("req_email");
            req_address = rs.getInt("req_address");
            req_shirtsize = rs.getInt("req_shirtsize");
            req_shoesize = rs.getInt("req_shoesize");
            req_otherQ1 = rs.getInt("req_otherA1");
            req_otherQ2 = rs.getInt("req_otherA2");
            req_otherQ3 = rs.getInt("req_otherA3");
            otherQ1 = rs.getString("otherQ1");
            otherQ2 = rs.getString("otherQ2");
            otherQ3 = rs.getString("otherQ3");
            locations_csv = rs.getString("locations");
            memedit = rs.getInt("memedit");
            ask_custom1 = rs.getInt("ask_custom1");   // custom question - tee to use (fields must be added to db table if other clubs use this custom!!!)  
            req_custom1 = rs.getInt("req_custom1");     
            member_item_code = rs.getString("member_item_code");
            guest_item_code = rs.getString("guest_item_code");
            member_fee = rs.getDouble("member_fee");
            member_tax = rs.getDouble("member_tax");
            guest_fee = rs.getDouble("guest_fee");
            guest_tax = rs.getDouble("guest_tax");
         }
      }
      pstmt1.close();              // close the stmt

   }
   catch (Exception exc) {

      dbError(exc.toString(), out);
      return;
   }

   oldCourse = courseName1;     // save original course name

   name = Utilities.trimDoubleSpaces(name);

   String ssampm = "AM";     // AM or PM for display (start hour)
   String seampm = "AM";     // AM or PM for display (end hour)
   String saampm = "AM";     // AM or PM for display (actual hour)
   String ssampm2 = "AM";    
   String seampm2 = "AM";
   int sampm = 0;
   int eampm = 0;
   int aampm = 0;

   if (shr > 12) {
      shr = shr - 12;        // convert to 12 hour value
      ssampm = "PM";         // indicate PM
      sampm = 12;
   }
   if (shr == 12) {
      ssampm = "PM";         // indicate PM
   }
   if (ehr > 12) {
      ehr = ehr - 12;        // convert to 12 hour value
      seampm = "PM";         // indicate PM
      eampm = 12;
   }
   if (ehr == 12) {
      seampm = "PM";         // indicate PM
   }
   if (ahr > 12) {
      ahr = ahr - 12;        // convert to 12 hour value
      saampm = "PM";         // indicate PM
      aampm = 12;
   }
   if (ahr == 12) {
      saampm = "PM";         // indicate PM
   }
   shr2 = stime2 / 100;
   smin2 = stime2 - (shr2 * 100);
   ehr2 = etime2 / 100;
   emin2 = etime2 - (ehr2 * 100);
   if (shr2 > 12) {
      shr2 = shr2 - 12;        // convert to 12 hour value
      ssampm2 = "PM";         // indicate PM
   }
   if (ehr2 > 12) {
      ehr2 = ehr2 - 12;        // convert to 12 hour value
      seampm2 = "PM";         // indicate PM
   }
   
   //String alphaMonth = month_table[month];  // get name for month



   //
   // Database record found - output an edit page
   //
   out.println(SystemUtils.HeadTitleEditor("Proshop - Events"));
/*
      out.println("<script language='JavaScript'>");             // Move Itin into textarea
      out.println("<!--");
      out.println("function moveitin() {");
      out.println("var olditin = document.eventform.olditin.value;");
      out.println("document.eventform.itin.value = olditin;");   // put itin in text area
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script
*/
   //out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/tiny_mce/tiny_mce.js\"></script>");
   //out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/assets/jquery/tiny_mce/tiny_mce.js\"></script>");
   out.println("<script type=\"text/javascript\">");

   out.println("tinyMCE.init({");

        // General options
        out.println("relative_urls : false,");      // convert all URLs to absolute URLs
        //out.println("remove_script_host : false,"); // don't strip the protocol and host part of the URLs
        out.println("document_base_url : \"http://www1.foretees.com/\",");

        out.println("mode : \"textareas\",");
        out.println("theme : \"advanced\",");
        out.println("plugins : \"safari,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,imagemanager\",");

        // Theme options
        //out.println("theme_advanced_buttons1 : \"save,|,cut,copy,paste,pastetext,pasteword,|,search,replace,|,undo,redo,|,tablecontrols,|,removeformat,visualaid,|,charmap,insertdate,inserttime,emotions,hr,advhr,|,print,|,ltr,rtl,|,fullscreen,|,insertlayer,moveforward,movebackward,absolute,|,iespell,spellchecker\",");
        //out.println("theme_advanced_buttons2 : \"formatselect,fontselect,fontsizeselect,styleprops,|,bold,italic,underline,strikethrough,|,forecolor,backcolor,|,justifyleft,justifycenter,justifyright,justifyfull,|,bullist,numlist,|,outdent,indent,blockquote,|,sub,sup,|,link,unlink,anchor,image,insertimage,|,cleanup,code,preview\",");

        out.println("theme_advanced_buttons1 : \"cut,copy,paste,pastetext,pasteword,|,undo,redo,|,hr,advhr,|,link,unlink,anchor,image,insertimage,|,code\",");
      //out.println("theme_advanced_buttons2 : \"tablecontrols\",");
        out.println("theme_advanced_buttons2 : \"bold,italic,underline,strikethrough,|,forecolor,backcolor,|,justifyleft,justifycenter,justifyright,justifyfull,|,bullist,numlist,|,outdent,indent,blockquote\",");
        out.println("theme_advanced_buttons3 : \"formatselect,fontselect,fontsizeselect,styleprops\",");
        out.println("theme_advanced_buttons4 : \"\",");

        out.println("theme_advanced_fonts : \"Andale Mono=andale mono,times;Arial=arial,helvetica,sans-serif;Arial Black=arial black,avant garde;Book Antiqua=book antiqua,palatino;Comic Sans MS=comic sans ms,sans-serif;Courier New=courier new,courier;Georgia=georgia,palatino;Helvetica=helvetica;Impact=impact,chicago;Palatino Linotype=palatino linotype,palatino,book antiqua;Symbol=symbol;Tahoma=tahoma,arial,helvetica,sans-serif;Terminal=terminal,monaco;Times New Roman=times new roman,times;Trebuchet MS=trebuchet ms,geneva;Verdana=verdana,geneva;Webdings=webdings;Wingdings=wingdings,zapf dingbats\",");

        out.println("theme_advanced_toolbar_location : \"top\",");
        out.println("theme_advanced_toolbar_align : \"left\",");
        out.println("theme_advanced_resizing : true,");
        // out.println("theme_advanced_statusbar_location : \"bottom\",");      // we don't need to show the file location info

        // Example content CSS (should be your site CSS)
        // out.println("content_css : \"css/example.css\",");

        // Drop lists for link/image/media/template dialogs
        out.println("template_external_list_url : \"js/template_list.js\",");
        out.println("external_link_list_url : \"js/link_list.js\",");
        out.println("external_image_list_url : \"js/image_list.js\",");
        out.println("media_external_list_url : \"js/media_list.js\",");

        //out.println("}");

   out.println("});");
   out.println("</script>");

   out.println("</head>");
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" topmargin=\"0\">"); //  onLoad=\"moveitin()\"
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page

   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr><td align=\"center\">");

         out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"8\" cellspacing=\"5\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
           
         if (copy == false) {
            out.println("<b>Edit Event</b><br>");
            out.println("<br>Change the desired information for the event below.<br>");
            out.println("<br>Click on <b>Continue</b> to submit the changes.");
            out.println("<br>Click on <b>Remove</b> to delete the event.");
         } else {
            out.println("<b>Copy Event</b><br>");
            out.println("<br>Change the desired information for the event below.<br>");
            out.println("Click on <b>Add</b> to create the new event.");
            out.println("<br><br><b>NOTE:</b> You must change the name of the event.");
         }
           
         out.println("</font>");
         out.println("</td></tr></table><br>");

         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"8\" cellspacing=\"5\">");
         if (sheet.equals( "yes" )) {
            out.println("<form action=\"Proshop_editevnt\" method=\"post\" name=\"eventform\">");
         } else {
            if (copy == true) {
                out.println("<form action=\"Proshop_addevnt\" method=\"post\" target=\"bot\" name=\"eventform\" id=\"eventform\">");
                out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");
                out.println("<input type=\"hidden\" name=\"copyname\" value=\"" +name.trim()+ "\">");  // event to copy
            } else {
                out.println("<form action=\"Proshop_editevnt\" method=\"post\" target=\"bot\" name=\"eventform\">");
            }
         }
         out.println("<input type=\"hidden\" name=\"event_id\" value=\"" + event_id +"\">");
         
         if (showOld) {
             out.println("<input type=\"hidden\" name=\"showOld\" value=\"Yes\">");
         }
                 
               out.println("<tr><td align=\"left\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"left\">Event name (Use A-Z, a-z, 0-9 and spaces <b>only</b>):&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"event_name\" value=\"" +name+ "\" size=\"42\" maxlength=\"42\">");
                     if (copy == false) {
                        out.println("<br>&nbsp;&nbsp;&nbsp;* Must be unique");
                     } else {
                        out.println("<br>&nbsp;&nbsp;&nbsp;* Must be changed!!");
                     }
                     out.println("<br><br>");

         if (activity_id == 0) {

           // if this is a season long event - let them change it to a non-season long event
           // otherwise do not allow them to change a non-season to a season long
           if (season==1) {
               out.println("Season Long Event?&nbsp;&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"season\" onchange=\"toggleSeason(this.selectedIndex)\">");
                 Common_Config.buildOption(0, "No", season, out);
                 Common_Config.buildOption(1, "Yes", season, out);
               out.println("</select><br><br>");
           } else {
               out.println("Season Long Event?&nbsp;&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"season\" onchange=\"toggleSeason(this.selectedIndex)\" disabled>");
                 Common_Config.buildOption(0, "No", season, out);
                 Common_Config.buildOption(1, "Yes", season, out);
               out.println("</select><br>");
               out.println("<nobr>&nbsp;&nbsp;&nbsp;* <i>Existing non-season long events can't not be converted to season long. Create a new event instead.</i></nobr><br><br>");


               //out.println("<input type=hidden name=season value=0>");
               //out.println("<i><nobr>Existing non-season long events can't not be converted to season long. Create a new event instead.</nobr></i><br><br>");
           }

         } else {

            out.println("<input type=hidden name=season value='0'>");
            out.println("<input type=hidden name=activity_id value='" + activity_id + "'>");

         }
               /*
               out.println("Season Long Event?&nbsp;&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"season\" onchange=\"toggleSeason(this.selectedIndex)\" " + ((season==1) ? "disabled" : "") + ">");
                 Common_Config.buildOption(0, "No", season, out);
                 Common_Config.buildOption(1, "Yes", season, out);
               out.println("</select>" + ((season==1) ? "Existing events can't not be converted to season long." : "") + "<br><br>");

               */

               //
               //
               //  If multiple courses, then add a drop-down box for course names
               //
               if (activity_id == 0 && multi != 0) {           // if multiple courses supported for this club

                  out.println("&nbsp;&nbsp;Course:&nbsp;&nbsp;");
                  out.println("<select size=\"1\" name=\"course\">");

                  if (courseName1.equals( "-ALL-" )) {             // if existing name is ALL

                     out.println("<option selected value=\"-ALL-\">ALL</option>");
                  } else {
                     out.println("<option value=\"-ALL-\">ALL</option>");
                  }
               
                  for (i=0; i < course.size(); i++) {

                     courseName = course.get(i);      // get course name from array

                     if (courseName.equals( courseName1 )) {             // if same as existing name

                        out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
                     } else {
                        out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
                     }
                  }
                  out.println("</select>");
                  out.println("<br><br>");

               } else {
                  out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
               }

               out.println("<div id=\"season_data\" style=\"height: auto; width: auto\">"); 

               if (activity_id == 0) {
                 out.println("&nbsp;&nbsp;Tees:&nbsp;&nbsp;");
                   out.println("<select size=\"1\" name=\"fb\">");
                   if (fb.equals( "Both" )) {
                      out.println("<option selected value=\"Both\">Both</option>");
                   } else {
                      out.println("<option value=\"Both\">Both</option>");
                   }
                   if (fb.equals( "Front" )) {
                      out.println("<option selected value=\"Front\">Front</option>");
                   } else {
                      out.println("<option value=\"Front\">Front</option>");
                   }
                   if (fb.equals( "Back" )) {
                      out.println("<option selected value=\"Back\">Back</option>");
                   } else {
                      out.println("<option value=\"Back\">Back</option>");
                   }
                   out.println("</select>");
                  out.println("<br><br>");
               }

                  out.println("Date of Event:&nbsp;&nbsp;");
                    out.println("Month:&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"month\">");
                        dateOpts.opMonth(out, month);         // output the month options
                    out.println("</select>");

                    out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"day\">");
                        dateOpts.opDay(out, day);         // output the day options
                    out.println("</select>");

                    out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"year\">");
                        //dateOpts.opYear(out, year);         // output the year options

                     if (year < thisYear) {
                         Common_Config.buildOption(year, year, year, out);
                     }
                     for (int j = thisYear; j <= (thisYear + 5); j++) {
                         Common_Config.buildOption(j, j, year, out);
                     }

                    out.println("</select><br><br>");


                 if (activity_id != 0) {

                    //ArrayList<Integer> locations = new ArrayList<Integer>();
                    ResultSet rs2 = null;
                    Statement stmt2 = null;

                    // display a series of checkboxes for each court (sub-activity w/ time sheets) that this event will take place on

                    out.println("Choose the locations for this event:&nbsp;&nbsp;");

                    Common_Config.displayActivitySheetSelect(locations_csv, sess_activity_id, false, con, out);
                    
                    out.println("<br><br>");

                 }


                  out.println("Time to Start Blocking " + ((activity_id == 0) ? "Tees" : "Times") + ":");
                  out.println("&nbsp;&nbsp;&nbsp; hr &nbsp;");
                    out.println("<select size=\"1\" name=\"start_hr\">");
                       for (int tmp = 1; tmp <= 12; tmp++) {
                           Common_Config.buildOption(tmp, Utilities.ensureDoubleDigit(tmp), shr, out);
                       }
                    out.println("</select>");
                    out.println("&nbsp; min &nbsp;");
                   out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + Utilities.ensureDoubleDigit(smin) + " name=\"start_min\">");
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"start_ampm\">");
                   if (ssampm.equals( "AM" )) {
                      out.println("<option selected value=\"00\">AM</option>");
                   } else {
                      out.println("<option value=\"00\">AM</option>");
                   }
                   if (ssampm.equals( "PM" )) {
                      out.println("<option selected value=\"12\">PM</option>");
                   } else {
                      out.println("<option value=\"12\">PM</option>");
                   }
                    out.println("</select><br><br>");
                  out.println("Time to Stop Blocking " + ((activity_id == 0) ? "Tees" : "Times") + ":");
                  out.println("&nbsp;&nbsp;&nbsp; hr &nbsp;");
                    out.println("<select size=\"1\" name=\"end_hr\">");
                       for (int tmp = 1; tmp <= 12; tmp++) {
                           Common_Config.buildOption(tmp, Utilities.ensureDoubleDigit(tmp), ehr, out);
                       }
                    out.println("</select>");
                    out.println("&nbsp; min &nbsp;");
                    out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + Utilities.ensureDoubleDigit(emin) + " name=\"end_min\">");
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"end_ampm\">");
                   if (seampm.equals( "AM" )) {
                      out.println("<option selected value=\"00\">AM</option>");
                   } else {
                      out.println("<option value=\"00\">AM</option>");
                   }
                   if (seampm.equals( "PM" )) {
                      out.println("<option selected value=\"12\">PM</option>");
                   } else {
                      out.println("<option value=\"12\">PM</option>");
                   }
                    out.println("</select><br><br>");
                  out.println("Actual Start Time of Event:");
                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;");
                    out.println("<select size=\"1\" name=\"act_hr\">");
                       for (int tmp = 1; tmp <= 12; tmp++) {
                           Common_Config.buildOption(tmp, Utilities.ensureDoubleDigit(tmp), ahr, out);
                       }
                    out.println("</select>");
                    out.println("&nbsp; min &nbsp;");
                    out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + Utilities.ensureDoubleDigit(amin) + " name=\"act_min\">");
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"act_ampm\">");
                   if (saampm.equals( "AM" )) {
                      out.println("<option selected value=\"00\">AM</option>");
                   } else {
                      out.println("<option value=\"00\">AM</option>");
                   }
                   if (saampm.equals( "PM" )) {
                      out.println("<option selected value=\"12\">PM</option>");
                   } else {
                      out.println("<option value=\"12\">PM</option>");
                   }
                    out.println("</select><br><br>");
                      
                   out.println("<hr width=\"400\">");             // separate with bar

                if (activity_id == 0) {

                   out.println("Optional 2nd Set of Tee Times to Block (e.g. for cross-overs)<br>");
                   out.println("&nbsp;&nbsp;Tees:&nbsp;&nbsp;");
                   out.println("<select size=\"1\" name=\"fb2\">");
                   if (fb2.equals( "Both" )) {
                      out.println("<option selected value=\"Both\">Both</option>");
                   } else {
                      out.println("<option value=\"Both\">Both</option>");
                   }
                   if (fb2.equals( "Front" )) {
                      out.println("<option selected value=\"Front\">Front</option>");
                   } else {
                      out.println("<option value=\"Front\">Front</option>");
                   }
                   if (fb2.equals( "Back" )) {
                      out.println("<option selected value=\"Back\">Back</option>");
                   } else {
                      out.println("<option value=\"Back\">Back</option>");
                   }
                   out.println("</select><br><br>");
                  out.println("Time to Start Blocking Tees:");
                  out.println("&nbsp;&nbsp;&nbsp; hr &nbsp;");
                    out.println("<select size=\"1\" name=\"start_hr2\">");
                       for (int tmp = 1; tmp <= 12; tmp++) {
                           Common_Config.buildOption(tmp, Utilities.ensureDoubleDigit(tmp), shr2, out);
                       }
                    out.println("</select>");
                    out.println("&nbsp; min &nbsp;");
                    out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + Utilities.ensureDoubleDigit(smin2) + " name=\"start_min2\">");
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"start_ampm2\">");
                   if (ssampm2.equals( "AM" )) {
                      out.println("<option selected value=\"00\">AM</option>");
                   } else {
                      out.println("<option value=\"00\">AM</option>");
                   }
                   if (ssampm2.equals( "PM" )) {
                      out.println("<option selected value=\"12\">PM</option>");
                   } else {
                      out.println("<option value=\"12\">PM</option>");
                   }
                    out.println("</select><br><br>");
                  out.println("Time to Stop Blocking Tees:");
                  out.println("&nbsp;&nbsp;&nbsp; hr &nbsp;");
                    out.println("<select size=\"1\" name=\"end_hr2\">");
                       for (int tmp = 1; tmp <= 12; tmp++) {
                           Common_Config.buildOption(tmp, Utilities.ensureDoubleDigit(tmp), ehr2, out);
                       }
                    out.println("</select>");
                    out.println("&nbsp; min &nbsp;");
                   out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + Utilities.ensureDoubleDigit(emin2) + " name=\"end_min2\">");
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"end_ampm2\">");
                   if (seampm2.equals( "AM" )) {
                      out.println("<option selected value=\"00\">AM</option>");
                   } else {
                      out.println("<option value=\"00\">AM</option>");
                   }
                   if (seampm2.equals( "PM" )) {
                      out.println("<option selected value=\"12\">PM</option>");
                   } else {
                      out.println("<option value=\"12\">PM</option>");
                   }
                    out.println("</select>");
                   out.println("<br><hr width=\"400\"><br>");
                   
                    out.println("Shotgun Event:&nbsp;&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"type\">");
                    if (type == 0) {                        
                      out.println("<option selected value=\"00\">No</option>");
                   } else {
                      out.println("<option value=\"00\">No</option>");
                   }
                    if (type == 1) {
                      out.println("<option selected value=\"01\">Yes</option>");
                   } else {
                      out.println("<option value=\"01\">Yes</option>");
                   }
                    out.println("</select><br><br>");

                } else {

                    // pass these to prevent null errors
                    out.println("<input type=hidden name=start_hr2 value='0'>");
                    out.println("<input type=hidden name=start_min2 value='0'>");
                    out.println("<input type=hidden name=start_ampm2 value='00'>");
                    out.println("<input type=hidden name=end_hr2 value='0'>");
                    out.println("<input type=hidden name=end_min2 value='0'>");
                    out.println("<input type=hidden name=end_ampm2 value='00'>");
                    out.println("<input type=hidden name=fb2 value=''>");
                    out.println("<input type=hidden name=type value='00'>");
                }

                out.println("</div>"); // end of season_data div

                if (activity_id == 0) {

                    out.println("Number of holes to be played:&nbsp;&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"holes\">");
                        Common_Config.buildOption(9, 9, holes, out);
                        Common_Config.buildOption(18, 18, holes, out);
                        Common_Config.buildOption(27, 27, holes, out);
                        Common_Config.buildOption(36, 36, holes, out);
                        Common_Config.buildOption(45, 45, holes, out);
                        Common_Config.buildOption(54, 54, holes, out);
                        Common_Config.buildOption(72, 72, holes, out);
                    out.println("</select><br><br>");

                } else {
                    out.println("<input type=hidden name=holes value=0>");
                }
                
                out.println("Guest Only Event (Outside Event)?&nbsp;&nbsp;&nbsp;");
                out.println("<select size=\"1\" name=\"gstOnly\">");
                if (gstOnly == 0) {
                    out.println("<option selected value=\"No\">No</option>");
                } else {
                    out.println("<option value=\"No\">No</option>");
                }
                if (gstOnly == 1) {
                    out.println("<option selected value=\"Yes\">Yes</option>");
                } else {
                    out.println("<option value=\"Yes\">Yes</option>");
                }
                out.println("</select>");
                out.println("<br><br>");

                if (activity_id == 0) {

                    out.println("If not, can members access the event times after <BR>they have been moved to the tee sheet?&nbsp;&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"memedit\">");
                    if (memedit == 0) {
                        out.println("<option selected value=\"No\">No</option>");
                    } else {
                        out.println("<option value=\"No\">No</option>");
                    }
                    if (memedit == 1) {
                        out.println("<option selected value=\"Yes\">Yes</option>");
                    } else {
                        out.println("<option value=\"Yes\">Yes</option>");
                    }
                    out.println("</select>");
                    out.println("<br><br>");                    

                } else {
                    out.println("<input type=hidden name=memedit value=\"No\">");
                }


                out.println("Gender Based Event?&nbsp;&nbsp;&nbsp;");
                out.println("<select size=\"1\" name=\"gender\">");
                if (gender == 0) out.println("<option value=\"-1\">Choose...</option>");
                Common_Config.buildOption(1, Labels.gender_opts[1], gender, out);
                Common_Config.buildOption(2, Labels.gender_opts[2], gender, out);
                Common_Config.buildOption(3, Labels.gender_opts[3], gender, out);
                out.println("</select>");
                if (gender == 0) out.println("&nbsp; &nbsp; <font color=red><b>* Must Specify</b></font>");
                out.println("<br><br>");

                out.println("Event Categories? (Used to filter list on Event Sign Up page)<br><br>");

                Common_Config.buildEventCategoryOptions(sess_activity_id, event_id, out, con);

                out.println("<br><br>");
                
                
                if (sess_activity_id == 0) {     // if Golf

                    if (posType.equals( "Abacus21 Direct" )) {      // allow POS codes for events?

                        out.println("If this is a member event and you wish to send charges to the POS when players are<br>"
                                + "checked in on the tee sheet, specify the POS info here.<br><br>");
                        out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"8\">");
                        out.println("<tr><td width=\"20\">&nbsp;</td><td align=\"right\">");
                        out.println("Item Code for Members: </td><td align=\"left\"> <input type=text name=member_item_code value=\"" + member_item_code + "\" size=20 maxlength=30></td></tr>");
                        out.println("<tr><td width=\"20\">&nbsp;</td><td align=\"right\">");
                        out.println("Event Fee for Members (No $): </td><td align=\"left\"><input type=text name=member_fee value=\"" + member_fee + "\" size=8 maxlength=8></td></tr>");
                        out.println("<tr><td width=\"20\">&nbsp;</td><td align=\"right\">");                
                        out.println("Item Code for Guests: </td><td align=\"left\"><input type=text name=guest_item_code value=\"" + guest_item_code + "\" size=20 maxlength=30></td></tr>");
                        out.println("<tr><td width=\"20\">&nbsp;</td><td align=\"right\">");
                        out.println("Event Fee for Guests (No $): </td><td align=\"left\"><input type=text name=guest_fee value=\"" + guest_fee + "\" size=8 maxlength=8></td></tr>");                             
                        out.println("</table>");
                        out.println("<br><br>");
                    }
                }
                         

                if (sess_activity_id > 0) {
                    out.println("Color to make this event on the time sheets:&nbsp;&nbsp;");
                } else {
                    out.println("Color to make this event on the tee sheet:&nbsp;&nbsp;");
                }

                Common_Config.displayColorsAll(color, out);       // output the color options

                out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                out.println("<a href=\"/" +rev+ "/proshop_color.htm\" target=\"_blank\">View Colors</a>");
                out.println("<br><br>");
                //
                //   Script will put any existing text in the itinerary textarea (value= doesn't work)
                //
                //out.println("<input type=\"hidden\" name=\"olditin\" value=\"" + itin + "\">"); // hold itin for script

                out.println("Itinerary: (Optional explaination of event - double quotes not allowed)<br>");
                out.println("<textarea name=\"itin\" id=\"itin\" cols=\"60\" rows=\"10\">");
                out.println(itin);
                out.println("</textarea>");

                out.println("<br><br>");
                out.println("Allow Online Sign-ups?&nbsp;&nbsp;&nbsp;");
                out.println("<select size=\"1\" name=\"signUp\" onchange=\"toggleExtraQ(this.selectedIndex)\">");
                if (signUp == 0) {
                    out.println("<option selected value=\"No\">No</option>");
                } else {
                    out.println("<option value=\"No\">No</option>");
                }
                if (signUp == 1) {
                    out.println("<option selected value=\"Yes\">Yes</option>");
                } else {
                    out.println("<option value=\"Yes\">Yes</option>");
                }
                out.println("</select>");
                out.println("<br>(Multiple day events - select 'No' if not 1st day. Guest only event - select 'No'.)<br><br>");
                    
               //
               // GUESTS ONLY - home club, phone, address, email
               // ALL PLAYERS - hdcp#, gender, shirt size, shoe size, other1, other2, other3
               //

              out.println("<div id=\"extra_info\" style=\"height: auto; width: auto\">");

               out.println("<table>");
            out.println("<tr align=center><td></td><td><font size=2><b>A</b></td><td><font size=2><b>R</b></td><td align=left><font size=2>&nbsp;&nbsp;(A = Ask Only,&nbsp; R = Require, * = Guest Questions)</font></td></tr>");
                out.println("<tr><td>&nbsp;</td><td></td><td><input type=checkbox name=req_guestname value=1" + ((req_guestname == 1) ? " checked" : "") + "></td><td><font size=2>Force Guest Names to be included? (If allowed)</td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_hdcp value=1 onchange=\"updateCheckbox(this.name, 'req_hdcp')\"" + ((ask_hdcp == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_hdcp value=1" + ((req_hdcp == 1) ? " checked" : "") + "></td><td><font size=2>Include HDCP Numbers? *</td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_gender value=1 onchange=\"updateCheckbox(this.name, 'req_gender')\"" + ((ask_gender == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_gender value=1" + ((req_gender == 1) ? " checked" : "") + "></td><td><font size=2>Include Gender?</td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_homeclub value=1 onchange=\"updateCheckbox(this.name, 'req_homeclub')\"" + ((ask_homeclub == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_homeclub value=1" + ((req_homeclub == 1) ? " checked" : "") + "></td><td><font size=2>Include Home Club, ST? *</td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_phone value=1 onchange=\"updateCheckbox(this.name, 'req_phone')\"" + ((ask_phone == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_phone value=1" + ((req_phone == 1) ? " checked" : "") + "></td><td><font size=2>Include Phone Number? *</td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_email value=1 onchange=\"updateCheckbox(this.name, 'req_email')\"" + ((ask_email == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_email value=1" + ((req_email == 1) ? " checked" : "") + "></td><td><font size=2>Include Email Address? *</td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_address value=1 onchange=\"updateCheckbox(this.name, 'req_address')\"" + ((ask_address == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_address value=1" + ((req_address == 1) ? " checked" : "") + "></td><td><font size=2>Include Mailing Address? *</td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_shirtsize value=1 onchange=\"updateCheckbox(this.name, 'req_shirtsize')\"" + ((ask_shirtsize == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_shirtsize value=1" + ((req_shirtsize == 1) ? " checked" : "") + "></td><td><font size=2>Include Shirt Size?</td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_shoesize value=1 onchange=\"updateCheckbox(this.name, 'req_shoesize')\"" + ((ask_shoesize == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_shoesize value=1" + ((req_shoesize == 1) ? " checked" : "") + "></td><td><font size=2>Include Shoe Size?</td></tr>");
                if (club.equals("interlachen")) {
                   out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_custom1 value=1 onchange=\"updateCheckbox(this.name, 'req_custom1')\"" + ((ask_custom1 == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_custom1 value=1" + ((req_custom1 == 1) ? " checked" : "") + "></td><td><font size=2>Include Tee Selection?</td></tr>");
                }
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_otherQ1 value=1 onchange=\"updateCheckbox(this.name, 'req_otherQ1')\"" + ((ask_otherQ1 == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_otherQ1 value=1" + ((req_otherQ1 == 1) ? " checked" : "") + "></td><td><font size=2>Include Custom Question #1?&nbsp; &nbsp;<input type=text name=otherQ1 size=40 maxlength=64 value=\"" + otherQ1 + "\"></td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_otherQ2 value=1 onchange=\"updateCheckbox(this.name, 'req_otherQ2')\"" + ((ask_otherQ2 == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_otherQ2 value=1" + ((req_otherQ2 == 1) ? " checked" : "") + "></td><td><font size=2>Include Custom Question #2?&nbsp; &nbsp;<input type=text name=otherQ2 size=40 maxlength=64 value=\"" + otherQ2 + "\"></td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_otherQ3 value=1 onchange=\"updateCheckbox(this.name, 'req_otherQ3')\"" + ((ask_otherQ3 == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_otherQ3 value=1" + ((req_otherQ3 == 1) ? " checked" : "") + "></td><td><font size=2>Include Custom Question #3?&nbsp; &nbsp;<input type=text name=otherQ3 size=40 maxlength=64 value=\"" + otherQ3 + "\"></td></tr>");
               out.println("</table>");

               out.println("<br><br>");

              out.println("</div>");
              
               out.println("If you wish to receive an email notification of all signups/modifications/cancelations<br>");
               out.println("provide up to two email address in the boxes below.<br><br>");
                out.println("&nbsp;&nbsp;Email #1: <input type=text name=email1 value=\"" + email1 + "\" size=40 maxlength=50><br><br>");
                out.println("&nbsp;&nbsp;Email #2: <input type=text name=email2 value=\"" + email2 + "\" size=40 maxlength=50>");

               out.println("<br><br>");

               //out.println("If you wish to export this event to an external program, select it from the list below:");
               out.println("Export Type:&nbsp;&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"export_type\">");
                 Common_Config.buildOption(0, "None", export_type, out);
                 Common_Config.buildOption(1, "TPP", export_type, out);
                 Common_Config.buildOption(2, "Event-Man", export_type, out);
                 Common_Config.buildOption(3, "GolfNet TMS", export_type, out);
                 Common_Config.buildOption(4, "TourEx", export_type, out);
                 Common_Config.buildOption(5, "Golf Genius", export_type, out);
               out.println("</select>");
                    
               out.println("<input type=\"hidden\" name=\"oldCourse\" value=\"" + oldCourse + "\">");
               out.println("<input type=\"hidden\" name=\"oldName\" value=\"" + oldName + "\">");
               out.println("<input type=\"hidden\" name=\"oldDate\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"oldMax\" value=\"" + max + "\">");
               out.println("<input type=\"hidden\" name=\"sheet\" value=\"" + sheet + "\">");
            out.println("<p align=\"center\">");
            if (copy == false) {
               out.println("<input type=\"submit\" name=\"Continue\" value=\"Continue\" onclick=\"return checkGender()\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<input type=\"submit\" name=\"Delete\" value=\"Delete\" onclick=\"return confirmDelete()\">");
               out.println("<script type='text/javascript'>");
               out.println("function confirmDelete() {");
               out.println("ret = confirm('WARNING! You are about to PERMANENTLY delete an event and ALL the sign-ups for the event!\\n\\nAre you sure you want to continue?');");
               out.println("if (ret) ret = confirm('Note: This action is permanent and CAN NOT be undone!\\n\\nAre you absolutely sure you want to delete this event?');");
               out.println("return ret;");
               out.println("}");
               out.println("</script>");
            } else {
               out.println("<input type=\"submit\" name=\"Add\" value=\"Add\" onclick=\"return checkGender()\">");
            }
            out.println("</p></font></td></tr></form></table>");
            out.println("</td></tr></table>");                                     // end of main page table

   out.println("<font size=\"2\">");
   if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
      out.println("<p><form method=\"get\" action=\"Proshop_events\">");
      out.println("<div style=\"margin:auto; text-align:center\"><input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970; width:75px\"></div>");
         
      if (showOld) {
          out.println("<input type=\"hidden\" name=\"showOld\" value=\"Yes\">");
      }
      
      out.println("</form></p>");
   } else {
      out.println("<p><form><div style=\"margin:auto; text-align:center\"><input type=\"button\" value=\"Cancel\" onclick=\"self.close();\" style=\"width:75px\"></div>");
      out.println("</form></p>");
   }
   out.println("</font>");
   out.println("</font>");
   out.println("<br>");
   
    out.println("<script type=\"text/javascript\">");
     out.println("<!--");
     out.println("function checkGender() {");
     out.println(" var g = document.forms['eventform'].gender;");
     out.println(" if (g.options[g.selectedIndex].value == -1) {");
     out.println("  alert(\"You must select a gender for this event before continuing.\");");
     out.println("  return false;");
     out.println(" }");
     out.println(" return true;");
     out.println("}");
    out.println("function toggleSeason(id) {");
    out.println(" var b = (id == 0);");
    out.println(" document.getElementById('season_data').style.display = ((b) ? 'block' : 'none');");
    out.println(" document.getElementById('season_data').style.visibility = ((b) ? 'visible' : 'hidden');");
    out.println(" document.getElementById('season_data').style.height = ((b) ? 'auto' : '0px');");
    out.println("}");

    out.println("function updateCheckbox(ask, req) {");
    out.println(" var a = eval(\"document.forms['eventform'].\" + ask + \".checked;\");");
    out.println(" if (a==false) {");
    out.println("  eval(\"document.forms['eventform'].\" + req + \".checked = false;\");");
    out.println("  eval(\"document.forms['eventform'].\" + req + \".disabled = true;\");");
    out.println(" } else {");
    out.println("  eval(\"document.forms['eventform'].\" + req + \".disabled = false;\");");
    out.println(" }");
    out.println("}");
    
    out.println("function toggleExtraQ(id) {");
    out.println(" var showExtra = (id == 1);");
    out.println(" document.getElementById('extra_info').style.display = ((showExtra) ? 'block' : 'none');");
    out.println(" document.getElementById('extra_info').style.visibility = ((showExtra) ? 'visible' : 'hidden');");
    out.println(" document.getElementById('extra_info').style.height = ((showExtra) ? 'auto' : '0px');");
    //out.println(" document.forms['eventform'].btnSubmit.value=\"Save\";");
    out.println("}");
    
    //out.println("toggleSeason(document.forms['eventform'].season.selectedIndex);");
    out.println("toggleExtraQ(document.forms['eventform'].signUp.selectedIndex);");

    if (activity_id == 0) {
        out.println("toggleSeason(document.forms['eventform'].season.selectedIndex);");
    } else {
        out.println("toggleSeason(0);"); // force to the hidden div
    }

        out.println("updateCheckbox('ask_hdcp', 'req_hdcp');");
        out.println("updateCheckbox('ask_homeclub', 'req_homeclub');");
        out.println("updateCheckbox('ask_gender', 'req_gender');");
        out.println("updateCheckbox('ask_phone', 'req_phone');");
        out.println("updateCheckbox('ask_email', 'req_email');");
        out.println("updateCheckbox('ask_address', 'req_address');");
        out.println("updateCheckbox('ask_shirtsize', 'req_shirtsize');");
        out.println("updateCheckbox('ask_shoesize', 'req_shoesize');");
        if (club.equals("interlachen")) {
           out.println("updateCheckbox('ask_custom1', 'req_custom1');");
        }
        out.println("updateCheckbox('ask_otherQ1', 'req_otherQ1');");
        out.println("updateCheckbox('ask_otherQ2', 'req_otherQ2');");
        out.println("updateCheckbox('ask_otherQ3', 'req_otherQ3');");
  
    out.println(" // -->");
    out.println("</script>");
    
   out.println("</body></html>");
   out.close();

 }   // end of doPost   


 // ***************************************************************************
 //  Process the copy request - display a selection list of existing events
 // ***************************************************************************

 private void doCopy(HttpServletRequest req, PrintWriter out, Connection con) {

   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);
   
   String club = (String)session.getAttribute("club");      // get club name
   String templott = (String)session.getAttribute("lottery");
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   
   boolean doRecur = false;
   
   //doRecur = Common_Server.SERVER_ID == 4;    // ********************* FOR TESTING ***********************************************************
   
   if (req.getParameter("recur") != null) {

      doRecur = true;              // this is a recur request rather than a copy
   }

   int year = 0;
   int month = 0;
   int day = 0;
   

   //
   //  Build the HTML page to display the existing events
   //
   out.println(SystemUtils.HeadTitle("Proshop Member Events Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\"><br>");

   out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"3\">");
   if (doRecur == true) {      
       out.println("<b>Create Recurrences of an Existing Event</b><br><br>");
   } else {
       out.println("<b>Copy an Existing Event</b><br><br>");
   }
   out.println("</font>");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   if (doRecur == true) {      
      out.println("<b>Instructions:</b>&nbsp;&nbsp;Use this feature to recur an existing event on one or more other dates.<br>");
      out.println("Select the event you wish to recur from the list below.");
   } else {
      out.println("<b>Instructions:</b>&nbsp;&nbsp;Use this feature to create a new event by copying an existing event.<br>");
      out.println("Select the event you wish to copy from the list below.");
   }
   out.println("</font></td></tr></table><br>");

   out.println("<!-- LOADING ALL EVENTS FOR ACTIVITY ID# " + sess_activity_id + " -->");

   //
   //  Get and display the existing events
   //
   String whereClause = "activity_id = " + sess_activity_id + " AND inactive = 0";
   
   if (doRecur == true) {      

       whereClause = "activity_id = " + sess_activity_id + " AND inactive = 0 AND season = 0";   // exclude season long events 
   }
   
   try {

      stmt = con.createStatement();
      rs = stmt.executeQuery("SELECT event_id, name, year, month, day FROM events2b "
                           + "WHERE " +whereClause+ " "
                           + "ORDER BY year DESC, name ASC");

      // if we found any then output the form.
      rs.last();

      if (rs.getRow() > 0) {

         out.println("<font size=\"2\">");
         if (doRecur == true) {      
            out.println("<p>Select the event you wish to recur. &nbsp;Date of event is listed to the right of the name.</p>");
         } else {
            out.println("<p>Select the event you wish to copy. &nbsp;Date of event is listed to the right of the name.</p>");
         }

         out.println("<p><b>NOTE:</b> &nbsp;The events are listed first by the year (most recent first) and then alphabetically.</p>");
         
         out.println("<form action=\"Proshop_events\" method=\"post\" target=\"bot\">");
         if (doRecur == true) {      
            out.println("<input type=\"hidden\" name=\"recur\" value=\"yes\">");     // this is a recur
         } else {
            out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");     // tell addevent its a copy            
         }
         //out.println("<select size=\"1\" name=\"name\">");
         out.println("<select size=\"1\" name=\"eventId\">");
         
         rs.beforeFirst();

         while (rs.next()) {

            year = rs.getInt("year");
            month = rs.getInt("month");
            day = rs.getInt("day");
         
            //out.println("<option>" +rs.getString("name")+ "</option>");
            out.println("<option value = \"" +rs.getInt("event_id")+ "\">" +rs.getString("name")+ " &nbsp;&nbsp; (" +month+ "/" +Utilities.ensureDoubleDigit(day)+ "/" +year+ ")</option>");
         }

         out.println("</select><br><br>");
     
         
/*         
   //
   //  TEMP FOR TESTING !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
   //
   if (club.startsWith("demo") || club.equals("theoaksclub")) {
      
      out.println("<input type=\"checkbox\" name=\"recur\" value=\"yes\">&nbsp;&nbsp;Do Recur instead of Copy<br><br>");
   }
*/  
         
         
         

         out.println("<input type=\"submit\" name=\"Continue\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");

      } else {            // no event's exist

         out.println("<p><font size=\"2\">No Events Currently Exist</p>");
      }

   } catch (Exception exc) {

      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>Error: " + exc.toString());
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");

   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

   }

   //
   //  End of HTML page
   //
   out.println("</td></tr></table>");                // end of main page table
   out.println("<font size=\"2\"><br><br>");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Cancel - Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center></font></body></html>");
   out.close();

 }    // end of doCopy

 
 
 // ***************************************************************************
 //  Process the recur request - prompt user for the date(s) to recur
 // ***************************************************************************

 private void doRecur(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, Connection con) {

   PreparedStatement pstmt1 = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);
   
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   
   String club = (String)session.getAttribute("club");      // get club name
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   
   //
   // Get any parms that were passed
   //
   int event_id = Integer.parseInt(req.getParameter("eventId"));
   
      
   String name = "";
   
   int year = 0;
   int month = 0;
   int day = 0;
   int signUp = 0;
   int nameLength = 0;
   int days_allow = 9999;       // default since 0 is valid
   int days_stop = 9999;
   
   int date = 0;
   
   boolean noSignUp = false;
 
   //
   //  Get today's date and set the start and end dates for the date picker calendar
   //
   int today = (int)Utilities.getDate(con);
   
   int yy = today / 10000;
   int mm = (today - (yy * 10000)) / 100;
   int dd = (today - (yy * 10000)) - (mm * 100);
   
   
   //
   //  If doRecur2 returned an error - it will pass back any parms it received - reload them
   //
   String dates = (req.getParameter("dates") != null) ? req.getParameter("dates") : "";
   
   String extension = (req.getParameter("extension") != null) ? req.getParameter("extension") : "";

   if (req.getParameter("noSignUp") != null) {

      noSignUp = true;
   }

   if (req.getParameter("days_allow") != null) {

      try {
         days_allow = Integer.parseInt(req.getParameter("days_allow"));
      }
      catch (Exception exc) {
         days_allow = 9999;          // use 9999 if not specified
      }
   }

   if (req.getParameter("days_stop") != null) {

      try {
         days_stop = Integer.parseInt(req.getParameter("days_stop"));
      }
      catch (Exception exc) {
         days_stop = 9999;          // use 9999 if not specified
      }
   }

   
   //
   //  Get the event date and other info needed
   //
   try {
      
      pstmt1 = con.prepareStatement (
               "SELECT name, date, year, month, day, signUp FROM events2b WHERE event_id = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setInt(1, event_id);
      rs = pstmt1.executeQuery();     
      
      if (rs.next()) {
      
         name = rs.getString("name");
         date = rs.getInt("date");
         year = rs.getInt("year");
         month = rs.getInt("month");
         day = rs.getInt("day");
         signUp = rs.getInt("signUp");
      }
      pstmt1.close();              // close the stmt

   }
   catch (Exception exc) {

      dbError(exc.toString(), out);
      return;
   }
   
   nameLength = name.length();      // get length of the name value
   
   
   //
   //  Build the HTML page to display the existing events
   //
   out.println(SystemUtils.getHeadTitle("Proshop - Recur Event", ProcessConstants.SCRIPT_MODE_PROSHOP_TRANSITIONAL));
   
   out.println("</head>");
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" topmargin=\"0\">"); 
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

   out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"3\">");
   out.println("<b>Create Recurrences of an Existing Event</b><br><br>");
   out.println("</font>");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<b>Instructions:</b>&nbsp;&nbsp;Use this feature to recur an existing event on one or more future dates.<br>");
   out.println("Select the dates to recur this event and specify any other options requested below.");
   out.println("</font></td></tr></table><br>");
   
   if (nameLength > 38) {        // if event name is too long to add a unique extension
      
      out.println("<font size=\"2\">");
      out.println("<p align=\"left\">Sorry, but the name of the event you want to recur is too long.<br><br>&nbsp;&nbsp;&nbsp;&nbsp;Name: " +name+ "<br><br>");
      out.println("Event names must be unique and in order to create a unique name we must append at least 4 characters.<br>");
      out.println("The max length allowed is 42 characters and this event's name is already " +nameLength+ " characters.<br><br>");
      out.println("Please shorten the name of this event and try again.<br><br>");
      out.println("<form method=\"get\" action=\"Proshop_events\">");
      out.println("<input type=\"submit\" value=\"Return to Update Event\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</font>");
      out.println("<br><br>");
      
   } else {

      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"8\" cellspacing=\"5\">");

      out.println("<form action=\"Proshop_events\" method=\"post\" target=\"bot\" name=\"eventform\" id=\"eventform\">");
      out.println("<input type=\"hidden\" name=\"recur2\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"event_id\" value=\"" + event_id +"\">");

      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"3\">");
      
      out.println("<p align=\"left\"><b>Event:</b>&nbsp;&nbsp;" +name+ "&nbsp;&nbsp&nbsp;&nbsp&nbsp;&nbsp");
      out.println("<b>Date:</b>&nbsp;&nbsp;" +month+ "/" +Utilities.ensureDoubleDigit(day)+ "/" +year+ "<br></font>");
      
      out.println("<hr width=\"400\">");             // separate with bar      
      
      out.println("<font size=\"2\">");
      out.println("<br>Select the date(s) to recur this event.&nbsp;&nbspClick on the small calendar image to select a date.<br>"
              + "Click on 'Add Recur Date' to select an additional date.<br><br>");

      //
      // Include the date selector to allow user to select multiple dates using a calendar
      //
      out.print("<div class=\"ftMultiRecur\">");

      String [] dateArray = dates.split(",");
      int i = 0;
      
     // if (dateArray.length == 1) {
     if (dates.equals("")) {      // if first time here (no dates)
      
          out.print("<div><label><span>Recur date #<span>1</span>:</span>");
          out.print(" <input data-ftstartdate=\"" +mm+ "/" +dd+ "/" +yy+ "\" data-ftenddate=\"" +mm+ "/" +dd+ "/" +(yy+1)+ "\" value=\"\" type=\"text\" class=\"ft_date_picker_field\" />");  // start with today and go one year
          out.println("</label></div>");
          
      } else {
          
         try {
             
            for(String s : dateArray) {

                i++;
                date = Integer.parseInt(s);
                year = date / 10000;
                month = (date - (year * 10000)) / 100;
                day = date - ((year * 10000) + (month * 100));

                out.print("<div><label><span>Recur date #<span>" + i + "</span>:</span>");
                out.print(" <input data-ftstartdate=\"" +mm+ "/" +dd+ "/" +yy+ "\" data-ftenddate=\"" +mm+ "/" +dd+ "/" +(yy+1)+ "\" data-ftdefaultdate=\"" + String.format("%02d/%02d/%04d", month,day,year) + "\" value=\"" + String.format("%02d/%02d/%04d", month,day,year) + "\" type=\"text\" class=\"ft_date_picker_field\" />");
                out.println("</label></div>");
            }
          
         } catch (Exception ignore) { }
          
      }
      
      out.print("<button type=\"button\">Add Recur Date</button>");
      out.print("<input type=\"hidden\" name=\"dates\" value=\"" + dates + "\">"); 
      out.print("</div>");
            
      out.println("<br><br>");
      out.println("<hr width=\"400\">");             // separate with bar

      out.println("<br><b>Notice:</b><br>The name of each recurred event must be unique, so a unique identifier will be appended.<br>"
            + "Select the value you would like to append to the original name:<br>");

      if (nameLength < 31) {
         if (extension.equals("useDate")) {
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"radio\" checked name=\"extension\" value=\"useDate\">&nbsp;&nbsp;Date of Recurred Event (i.e. " +name+ " 7 12 2016)");
         } else {
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"radio\" name=\"extension\" value=\"useDate\">&nbsp;&nbsp;Date of Recurred Event (i.e. " +name+ " 7 12 2016)");
         }
      }
      if (extension.equals("useDate")) {
         out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"radio\" name=\"extension\" value=\"useNum\">&nbsp;&nbsp;Incremental Number (i.e. " +name+ " 2)");
      } else {
         out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"radio\" checked name=\"extension\" value=\"useNum\">&nbsp;&nbsp;Incremental Number (i.e. " +name+ " 2)");  // default
      }

      if (signUp > 0) {       // online signup for this event?
         
         out.println("<br><br>");
         out.println("<hr width=\"400\">");             // separate with bar

         out.println("<input type=\"hidden\" name=\"signUpEvent\" value=\"yes\">");
         
         out.println("<br><b>This event allows members to register online.</b><br>Indicate how you would like to handle the recurred events:<br><br>"
                 + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         
         if (noSignUp == true) {
            out.println("<input type=\"checkbox\" checked name=\"noSignUp\" value=\"1\">"); 
         } else {
            out.println("<input type=\"checkbox\" name=\"noSignUp\" value=\"1\">"); 
         }
         
         out.println("&nbsp;&nbsp;DO NOT Allow Online Signup<br><br>"
                 + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;------- OR -------<br><br>"
                 + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Days in advance of the event to allow members to register: &nbsp;");
         
         if (days_allow < 9999) {
            out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" name=\"days_allow\" value=\"" +days_allow+ "\"><br><br>");
         } else {
            out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" name=\"days_allow\"><br><br>");
         }
         
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Days in advance of the event to stop members from registering: &nbsp;");
         
         if (days_stop < 9999) {
            out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" name=\"days_stop\" value=\"" +days_stop+ "\"><br><br>");
         } else {         
            out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" name=\"days_stop\"><br><br>");
         }
         
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(The time of day will remain the same as the event being recurred.)");
      }

      out.println("<br><br>");
      out.println("<hr width=\"400\">");             // separate with bar

      out.println("<br></p><p align=center><input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\"></p>");

      out.println("</font></td></tr></form></table>");    // end of config table
   }

   //
   //  End of HTML page
   //
   out.println("</td></tr></table><br>");                // end of main page table
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Cancel - Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center></font></body></html>");
   out.close();

 }      // end of doRecur

 
 
 // ***************************************************************************
 //  Process the 2nd recur request - process the recur options and create the new events
 // ***************************************************************************

 private void doRecur2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, Connection con) {

   PreparedStatement pstmt1 = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);
   
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   
   String club = (String)session.getAttribute("club");      // get club name
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   
   String name = "";
   String newName = "";
   String nameExt = "";
   String dates = "";
   String extension = "";
   String errorMsg = "";
   String resultMsg = "";
   
   ArrayList<Long> dateList = new ArrayList<Long>();    // will hold the list of dates provided
   
   ArrayList<Integer> category_ids = new ArrayList<Integer>();  // ArrayList to hold all category_ids for this event

   ArrayList<String> dateListResults = new ArrayList<String>();    // will hold the list of dates that were successfully added, or an error message if it failed
   
   int days_allow = 9999;   // default since 0 is valid
   int days_stop = 9999;
   int month = 0;
   int day = 0;
   int year = 0;
   int lastNum = 1;
   int dateStart = 0;
   int dateStartDay = 0;
   int dateStartMn = 0;
   int dateStartYr = 0;
   int dateStop = 0;
   int dateStopDay = 0;
   int dateStopMn = 0;
   int dateStopYr = 0;
   int signUp = 0;
   
   long date = 0;
   
   boolean useDate = false;
   boolean useNum = false;
   boolean signUpEvent = false;
   boolean noSignUp = false;
   boolean dupDateReq = false;
   
   //
   // Get the event_id of the event to recur, and the other parms
   //
   int event_id = Integer.parseInt(req.getParameter("event_id"));
      
   if (req.getParameter("dates") != null) {

      dates = req.getParameter("dates");
   }

   if (req.getParameter("extension") != null) {

      extension = req.getParameter("extension");
   }

   if (req.getParameter("signUpEvent") != null) {

      signUpEvent = true;     // original event allows member signup
   }

   if (req.getParameter("noSignUp") != null) {

      noSignUp = true;
   }

   if (req.getParameter("days_allow") != null) {

      try {
         days_allow = Integer.parseInt(req.getParameter("days_allow"));
      }
      catch (Exception exc) {
         days_allow = 9999;          // use 9999 if left blank
      }
      
   } else {
      
      days_allow = 9999;        // indicate not specified
   }

   if (req.getParameter("days_stop") != null) {

      try {
         days_stop = Integer.parseInt(req.getParameter("days_stop"));
      }
      catch (Exception exc) {
         days_stop = 9999;          // use 9999 if left blank
      }
      
   } else {
      
      days_stop = 9999;        // indicate not specified
   }

   //
   //    Get the name of the event being recurred
   //
   try {

      pstmt1 = con.prepareStatement (
               "SELECT name FROM events2b WHERE event_id = ?");

      pstmt1.clearParameters();      
      pstmt1.setInt(1, event_id);
      rs = pstmt1.executeQuery();     

      if (rs.next()) {

         name = rs.getString("name");        // get name of event to be recurred
      }
      pstmt1.close();            

   }
   catch (Exception exc) {

      dbError(exc.toString(), out);
      return;
   }

   
   //
   //  Build the HTML page to display the existing events
   //
   out.println(SystemUtils.HeadTitleEditor("Proshop - Recur Event"));
   out.println("</head>");
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" topmargin=\"0\">"); 
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

   out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"3\">");
   out.println("<b>Create Recurrences of an Existing Event</b><br><br>");
   out.println("<b>Event:</b>&nbsp;&nbsp;" +name+ "<br>");
   out.println("</font></td></tr></table><br>");
   out.println("<font color=\"#000000\" size=\"2\">");

   
   //
   //  Verify the parms received
   //
   if (dates.equals("")) {          // if no dates provided
      
      errorMsg = "No recurring dates were provided. &nbsp;Please select at least one date that you would wish to recur this event.";
      
   } else if (extension.equals("")) {   // if the extension option was not selected
      
      errorMsg = "You must specify a unique identifier to append to the name of the event.";
      
   } else if (signUpEvent == true && noSignUp == false && (days_allow == 9999 || days_stop == 9999)) {
      
      errorMsg = "The event to be recurred allowed members to register online, but you did not indicate how to handle this for the recurred events. &nbsp;Please select the appropriate option(s) for member registration.";
      
   } else if (signUpEvent == true && noSignUp == false && days_stop > days_allow) {
      
      errorMsg = "The 'days in advance to stop members from registering' must be less than or equal to the 'days in advance to start registering'.";
      
   } else {
      
      //  
      //  Parameters all received 
      //
      //  Get all the dates selected and save them in an array list (dates is like a csv file - comma delineated list)
      //
      lastNum = 1;                      // init ext number
      
      dateList.clear();                 // init the array
      
      StringTokenizer tok = new StringTokenizer( dates, "," );

      while (tok.hasMoreTokens()) {

         try {
            
             dateList.add(Long.parseLong(tok.nextToken()));
             
         } catch (Exception ignore) { }
      }

      if (extension.equals("useDate")) {
         
         useDate = true;
         
      } else if (extension.equals("useNum")) {
         
         useNum = true;
      }
       
      for (int i=0; i<dateList.size(); i++) {
         
         date = dateList.get(i);       // get a date
         
         year = (int)(date / 10000);
         month = (int)((date - (year * 10000)) / 100);
         day = (int)((date - (year * 10000)) - (month * 100));
         
         if (useDate) {
            
            nameExt = " " + month + " " + Utilities.ensureDoubleDigit(day) + " " + year;
            
         } else if (useNum) {
            
            lastNum++;          // increment the number extension
            
            nameExt = " " + lastNum;
         }

         newName = name + nameExt;     // create name for recurred event
         
         //
         //  Check to see if any of the new names already exist
         //
         try {
            pstmt1 = con.prepareStatement (
                     "SELECT date FROM events2b WHERE name = ?");

            pstmt1.clearParameters();      
            pstmt1.setString(1, newName);
            rs = pstmt1.executeQuery();     

            if (rs.next()) {

               Long dupDate = rs.getLong("date");        // get date of duplicate event

               year = (int)(dupDate / 10000);
               month = (int)((dupDate - (year * 10000)) / 100);
               day = (int)((dupDate - (year * 10000)) - (month * 100));
      
               errorMsg = "An event already exists with the name specified (" +newName+ ") and a date of " + month + "/" + Utilities.ensureDoubleDigit(day) + "/" + year + ". Event names must be unique. Please use the other name extension option if you must recur this event.";
            }
            pstmt1.close();            

         }
         catch (Exception exc) {

            dbError(exc.toString(), out);
            return;
         }
         
         if (!errorMsg.equals("")) break;      // exit FOR loop if error found
         
      }     // end of FOR dates to check
      
   }        // end of error checks
   
   
   //
   //  Any errors found ?
   //
   if (!errorMsg.equals("")) {        // if error
      
      out.println("<table cellpadding=\"5\" width=\"400\" bgcolor=\"#FFFFCC\" border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\"><H3>Error Encountered</H3><br><br>" +errorMsg);
      out.println("</td></tr></table>");                // end of main page table
      out.println("</font></td></tr></table><br>");                // end of main page table
      out.println("<form method=\"post\" action=\"Proshop_events\">");
      out.println("<input type=\"hidden\" name=\"recur\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"eventId\" value=\"" + event_id + "\">");
      out.println("<input type=\"hidden\" name=\"dates\" value=\"" + dates + "\">");
      out.println("<input type=\"hidden\" name=\"extension\" value=\"" + extension + "\">");
      if (signUpEvent == true) {    // if event uses online signup
         if (noSignUp == true) {    // if user specified no signup for the recur events
            out.println("<input type=\"hidden\" name=\"noSignUp\" value=\"yes\">");
         } else {
            out.println("<input type=\"hidden\" name=\"days_allow\" value=\"" + days_allow + "\">");
            out.println("<input type=\"hidden\" name=\"days_stop\" value=\"" + days_stop + "\">");
         }
      }
      out.println("<input type=\"submit\" value=\"Go Back\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
      out.println("</center></font></body></html>");
      out.close();
      return;
   }
   
   
      
   //**************************************************
   //  All good - process the recurr request
   //**************************************************
   
   lastNum = 1;                      // init name extension number
      
   //
   //  Get the event info needed for the new events
   //
   try {
      
      pstmt1 = con.prepareStatement (
               "SELECT category_id FROM event_category_bindings WHERE event_id = ?");    // get the event category ids used for this event

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setInt(1, event_id);
      rs = pstmt1.executeQuery();     
      
      while (rs.next()) {
         
          category_ids.add(rs.getInt("category_id"));         // save the category ids for the new events
      }

      //
      //  Get all settings from the event to be recurred
      //
      pstmt1 = con.prepareStatement (
               "SELECT * FROM events2b WHERE event_id = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setInt(1, event_id);
      rs = pstmt1.executeQuery();     
      
      if (rs.next()) {

         //
         //  Create the new events
         //
         for (int i=0; i<dateList.size(); i++) {      // process each date specified

            date = dateList.get(i);       // get the next date
            
            dupDateReq = false;              // init

            //
            //  Check for duplicate dates
            //
            for (int i2=0; i2<i; i2++) {

               if (date == dateList.get(i2)) {       // check all dates specified prior to this one

                  dupDateReq = true;
                  break;
               }
            }
            
            if (dupDateReq == false) {        // if ok to continue
      
               year = (int)(date / 10000);
               month = (int)((date - (year * 10000)) / 100);
               day = (int)((date - (year * 10000)) - (month * 100));

               if (useDate) {

                  nameExt = " " + month + " " + Utilities.ensureDoubleDigit(day) + " " + year;

               } else if (useNum) {

                  lastNum++;          // increment the number extension

                  nameExt = " " + lastNum;
               }

               newName = name + nameExt;     // create name for recurred event

               //
               //  If member signup, calculate the dates for signup start and stop
               //
               signUp = 0;              // init to No Signup

               if (signUpEvent == true && noSignUp == false) {

                  signUp = 1;                     // allow member signup on the recurred events

                  if (days_allow > 0) {

                     dateStart = Utilities.getDate((int)date, (0-days_allow));   // get date to start taking signups

                     dateStartYr = (dateStart / 10000);
                     dateStartMn = ((dateStart - (dateStartYr * 10000)) / 100);
                     dateStartDay = ((dateStart - (dateStartYr * 10000)) - (dateStartMn * 100));

                  } else {

                     dateStart = (int)date;     // use date of event

                     dateStartYr = year;
                     dateStartMn = month;
                     dateStartDay = day;
                  }

                  if (days_stop > 0) {

                     dateStop = Utilities.getDate((int)date, (0-days_stop));   // get date to start taking signups            

                     dateStopYr = (dateStop / 10000);
                     dateStopMn = ((dateStop - (dateStopYr * 10000)) / 100);
                     dateStopDay = ((dateStop - (dateStopYr * 10000)) - (dateStopMn * 100));

                  } else {

                     dateStop = (int)date;      // use date of event

                     dateStopYr = year;
                     dateStopMn = month;
                     dateStopDay = day;
                  }
               }

               //
               //  Use the info from the original event to create the new event(s)
               //
               try {

                  pstmt2 = con.prepareStatement (
                  "INSERT INTO events2b (name, date, year, month, day, start_hr, start_min, " +
                  "stime, end_hr, end_min, etime, color, type, act_hr, act_min, courseName, signUp, " +
                  "itin, gstOnly, holes, fb, stime2, etime2, fb2, gender, season, export_type, " +
                  "email1, email2, ask_homeclub, ask_phone, ask_address, ask_hdcp, ask_email, ask_gender, ask_shirtsize, ask_shoesize, " +
                  "ask_otherA1, ask_otherA2, ask_otherA3, req_guestname, req_homeclub, req_phone, req_address, req_hdcp, req_email, req_gender, " + 
                  "req_shirtsize, req_shoesize, req_otherA1, req_otherA2, req_otherA3, otherQ1, otherQ2, otherQ3, locations, memedit, " +
                  "format, pairings, size, max, guests, memcost, gstcost, c_month, c_day, c_year, " +
                  "c_hr, c_min, c_date, c_time, x, xhrs, mc, pc, wa, ca, " +
                  "su_month, su_day, su_year, su_hr, su_min, su_date, su_time, mempos, gstpos, tmode1, " +
                  "tmode2, tmode3, tmode4, tmode5, tmode6, tmode7, tmode8, tmode9, tmode10, tmode11, " +
                  "tmode12, tmode13, tmode14, tmode15, tmode16, mem1, mem2, mem3, mem4, mem5, " +
                  "mem6, mem7, mem8, mem9, mem10, mem11, mem12, mem13, mem14, mem15, " +
                  "mem16, mem17, mem18, mem19, mem20, mem21, mem22, mem23, mem24, mship1, " +
                  "mship2, mship3, mship4, mship5, mship6, mship7, mship8, mship9, mship10, mship11, " +
                  "mship12, mship13, mship14, mship15, mship16, mship17, mship18, mship19, mship20, mship21, " +
                  "mship22, mship23, mship24, member_item_code, guest_item_code, minsize, activity_id, recur_id, " +
                  "ask_custom1, req_custom1, " +
                  "member_fee, guest_fee, member_tax, guest_tax) " +
                  "VALUES (?,?,?,?,?,?,?," +
                  "?,?,?,?,?,?,?,?,?,?," +
                  "?,?,?,?,?,?,?,?,?,?," +
                  "?,?,?,?,?,?,?,?,?,?," +
                  "?,?,?,?,?,?,?,?,?,?," +
                  "?,?,?,?,?,?,?,?,?,?," +
                  "?,?,?,?,?,?,?,?,?,?," +
                  "?,?,?,?,?,?,?,?,?,?," +
                  "?,?,?,?,?,?,?,?,?,?," +
                  "?,?,?,?,?,?,?,?,?,?," +
                  "?,?,?,?,?,?,?,?,?,?," +
                  "?,?,?,?,?,?,?,?,?,?," +
                  "?,?,?,?,?,?,?,?,?,?," +
                  "?,?,?,?,?,?,?,?,?,?," +
                  "?,?,?,?,?,?,?,?,?,?," +
                  "?,?,?,?,?,?,?,?,?,?," +
                  "?,?,?,?)");

                  pstmt2.clearParameters();      
                  pstmt2.setString(1, newName);     // new value
                  pstmt2.setLong(2, date);          // new value
                  pstmt2.setInt(3, year);           // new value
                  pstmt2.setInt(4, month);          // new value
                  pstmt2.setInt(5, day);            // new value
                  pstmt2.setInt(6, rs.getInt("start_hr"));           //  change the rest - get from the rs above
                  pstmt2.setInt(7, rs.getInt("start_min"));
                  pstmt2.setInt(8, rs.getInt("stime"));
                  pstmt2.setInt(9, rs.getInt("end_hr"));
                  pstmt2.setInt(10, rs.getInt("end_min"));
                  pstmt2.setInt(11, rs.getInt("etime"));
                  pstmt2.setString(12, rs.getString("color"));
                  pstmt2.setInt(13, rs.getInt("type"));
                  pstmt2.setInt(14, rs.getInt("act_hr"));
                  pstmt2.setInt(15, rs.getInt("act_min"));
                  pstmt2.setString(16, rs.getString("courseName"));
                  pstmt2.setInt(17, signUp);                          // member signup for the recurred events
                  pstmt2.setString(18, rs.getString("itin"));
                  pstmt2.setInt(19, rs.getInt("gstOnly"));
                  pstmt2.setInt(20, rs.getInt("holes"));
                  pstmt2.setString(21, rs.getString("fb"));
                  pstmt2.setInt(22, rs.getInt("stime2"));
                  pstmt2.setInt(23, rs.getInt("etime2"));
                  pstmt2.setString(24, rs.getString("fb2"));
                  pstmt2.setInt(25, rs.getInt("gender"));
                  pstmt2.setInt(26, rs.getInt("season"));
                  pstmt2.setInt(27, rs.getInt("export_type"));
                  pstmt2.setString(28, rs.getString("email1"));
                  pstmt2.setString(29, rs.getString("email2"));
                  pstmt2.setInt(30, rs.getInt("ask_homeclub"));
                  pstmt2.setInt(31, rs.getInt("ask_phone"));
                  pstmt2.setInt(32, rs.getInt("ask_address"));
                  pstmt2.setInt(33, rs.getInt("ask_hdcp"));
                  pstmt2.setInt(34, rs.getInt("ask_email"));
                  pstmt2.setInt(35, rs.getInt("ask_gender"));
                  pstmt2.setInt(36, rs.getInt("ask_shirtsize"));
                  pstmt2.setInt(37, rs.getInt("ask_shoesize"));
                  pstmt2.setInt(38, rs.getInt("ask_otherA1"));
                  pstmt2.setInt(39, rs.getInt("ask_otherA2"));
                  pstmt2.setInt(40, rs.getInt("ask_otherA3"));
                  pstmt2.setInt(41, rs.getInt("req_guestname"));
                  pstmt2.setInt(42, rs.getInt("req_homeclub"));
                  pstmt2.setInt(43, rs.getInt("req_phone"));
                  pstmt2.setInt(44, rs.getInt("req_address"));
                  pstmt2.setInt(45, rs.getInt("req_hdcp"));
                  pstmt2.setInt(46, rs.getInt("req_email"));
                  pstmt2.setInt(47, rs.getInt("req_gender"));
                  pstmt2.setInt(48, rs.getInt("req_shirtsize"));
                  pstmt2.setInt(49, rs.getInt("req_shoesize"));
                  pstmt2.setInt(50, rs.getInt("req_otherA1"));
                  pstmt2.setInt(51, rs.getInt("req_otherA2"));
                  pstmt2.setInt(52, rs.getInt("req_otherA3"));
                  pstmt2.setString(53, rs.getString("otherQ1"));
                  pstmt2.setString(54, rs.getString("otherQ2"));
                  pstmt2.setString(55, rs.getString("otherQ3"));
                  pstmt2.setString(56, rs.getString("locations"));
                  pstmt2.setInt(57, rs.getInt("memedit"));
                  pstmt2.setString(58, rs.getString("format"));
                  pstmt2.setString(59, rs.getString("pairings"));
                  pstmt2.setInt(60, rs.getInt("size"));
                  pstmt2.setInt(61, rs.getInt("max"));
                  pstmt2.setInt(62, rs.getInt("guests"));
                  pstmt2.setString(63, rs.getString("memcost"));
                  pstmt2.setString(64, rs.getString("gstcost"));
                  pstmt2.setInt(65, dateStopMn);                 // set these from values above
                  pstmt2.setInt(66, dateStopDay);
                  pstmt2.setInt(67, dateStopYr);
                  pstmt2.setInt(68, rs.getInt("c_hr"));
                  pstmt2.setInt(69, rs.getInt("c_min"));
                  pstmt2.setLong(70, dateStop);
                  pstmt2.setInt(71, rs.getInt("c_time"));
                  pstmt2.setInt(72, rs.getInt("x"));
                  pstmt2.setInt(73, rs.getInt("xhrs"));
                  pstmt2.setInt(74, rs.getInt("mc"));
                  pstmt2.setInt(75, rs.getInt("pc"));
                  pstmt2.setInt(76, rs.getInt("wa"));
                  pstmt2.setInt(77, rs.getInt("ca"));
                  pstmt2.setInt(78, dateStartMn);              // set these from values above !!!!!!!
                  pstmt2.setInt(79, dateStartDay);
                  pstmt2.setInt(80, dateStartYr);
                  pstmt2.setInt(81, rs.getInt("su_hr"));
                  pstmt2.setInt(82, rs.getInt("su_min"));
                  pstmt2.setLong(83, dateStart);
                  pstmt2.setInt(84, rs.getInt("su_time"));
                  pstmt2.setString(85, rs.getString("mempos"));
                  pstmt2.setString(86, rs.getString("gstpos"));
                  pstmt2.setInt(87, rs.getInt("tmode1"));
                  pstmt2.setInt(88, rs.getInt("tmode2"));
                  pstmt2.setInt(89, rs.getInt("tmode3"));
                  pstmt2.setInt(90, rs.getInt("tmode4"));
                  pstmt2.setInt(91, rs.getInt("tmode5"));
                  pstmt2.setInt(92, rs.getInt("tmode6"));
                  pstmt2.setInt(93, rs.getInt("tmode7"));
                  pstmt2.setInt(94, rs.getInt("tmode8"));
                  pstmt2.setInt(95, rs.getInt("tmode9"));
                  pstmt2.setInt(96, rs.getInt("tmode10"));
                  pstmt2.setInt(97, rs.getInt("tmode11"));
                  pstmt2.setInt(98, rs.getInt("tmode12"));
                  pstmt2.setInt(99, rs.getInt("tmode13"));
                  pstmt2.setInt(100, rs.getInt("tmode14"));
                  pstmt2.setInt(101, rs.getInt("tmode15"));
                  pstmt2.setInt(102, rs.getInt("tmode16"));
                  pstmt2.setString(103, rs.getString("mem1"));
                  pstmt2.setString(104, rs.getString("mem2"));
                  pstmt2.setString(105, rs.getString("mem3"));
                  pstmt2.setString(106, rs.getString("mem4"));
                  pstmt2.setString(107, rs.getString("mem5"));
                  pstmt2.setString(108, rs.getString("mem6"));
                  pstmt2.setString(109, rs.getString("mem7"));
                  pstmt2.setString(110, rs.getString("mem8"));
                  pstmt2.setString(111, rs.getString("mem9"));
                  pstmt2.setString(112, rs.getString("mem10"));
                  pstmt2.setString(113, rs.getString("mem11"));
                  pstmt2.setString(114, rs.getString("mem12"));
                  pstmt2.setString(115, rs.getString("mem13"));
                  pstmt2.setString(116, rs.getString("mem14"));
                  pstmt2.setString(117, rs.getString("mem15"));
                  pstmt2.setString(118, rs.getString("mem16"));
                  pstmt2.setString(119, rs.getString("mem17"));
                  pstmt2.setString(120, rs.getString("mem18"));
                  pstmt2.setString(121, rs.getString("mem19"));
                  pstmt2.setString(122, rs.getString("mem20"));
                  pstmt2.setString(123, rs.getString("mem21"));
                  pstmt2.setString(124, rs.getString("mem22"));
                  pstmt2.setString(125, rs.getString("mem23"));
                  pstmt2.setString(126, rs.getString("mem24"));
                  pstmt2.setString(127, rs.getString("mship1"));
                  pstmt2.setString(128, rs.getString("mship2"));
                  pstmt2.setString(129, rs.getString("mship3"));
                  pstmt2.setString(130, rs.getString("mship4"));
                  pstmt2.setString(131, rs.getString("mship5"));
                  pstmt2.setString(132, rs.getString("mship6"));
                  pstmt2.setString(133, rs.getString("mship7"));
                  pstmt2.setString(134, rs.getString("mship8"));
                  pstmt2.setString(135, rs.getString("mship9"));
                  pstmt2.setString(136, rs.getString("mship10"));
                  pstmt2.setString(137, rs.getString("mship11"));
                  pstmt2.setString(138, rs.getString("mship12"));
                  pstmt2.setString(139, rs.getString("mship13"));
                  pstmt2.setString(140, rs.getString("mship14"));
                  pstmt2.setString(141, rs.getString("mship15"));
                  pstmt2.setString(142, rs.getString("mship16"));
                  pstmt2.setString(143, rs.getString("mship17"));
                  pstmt2.setString(144, rs.getString("mship18"));
                  pstmt2.setString(145, rs.getString("mship19"));
                  pstmt2.setString(146, rs.getString("mship20"));
                  pstmt2.setString(147, rs.getString("mship21"));
                  pstmt2.setString(148, rs.getString("mship22"));
                  pstmt2.setString(149, rs.getString("mship23"));
                  pstmt2.setString(150, rs.getString("mship24"));
                  pstmt2.setString(151, rs.getString("member_item_code"));
                  pstmt2.setString(152, rs.getString("guest_item_code"));
                  pstmt2.setInt(153, rs.getInt("minsize"));
                  pstmt2.setInt(154, sess_activity_id);
                  pstmt2.setInt(155, event_id);            // link the recurred events together using the original event id (in recur_id)
                  pstmt2.setInt(156, rs.getInt("ask_custom1"));
                  pstmt2.setInt(157, rs.getInt("req_custom1"));
                  pstmt2.setDouble(158, rs.getDouble("member_fee"));
                  pstmt2.setDouble(159, rs.getDouble("guest_fee"));
                  pstmt2.setDouble(160, rs.getDouble("member_tax"));
                  pstmt2.setDouble(161, rs.getDouble("guest_tax"));

                  pstmt2.executeUpdate();          

               }
               catch (Exception exc) {

                  resultMsg = "<b>Error trying to add event on</b> " + month + "/" + Utilities.ensureDoubleDigit(day) + "/" + year;
                  Utilities.logError("Error in Proshop_events.doRecur2 recurring an event for club " +club+ ", event " +newName+ ", date " +date+ ", Error = " +exc.toString());
               }

               pstmt2.close();   // close the stmt

               if (resultMsg.equals("")) {            

                  // Go out and apply the selected event categories for this event
                  Utilities.updateEventCategoryBindings(newName, sess_activity_id, category_ids, con);

                  //
                  //  Now, call utility to scan the event table and update teecurr or time sheets accordingly
                  //
                  SystemUtils.do1Event(con, newName);

                  resultMsg = "Added on " + month + "/" + Utilities.ensureDoubleDigit(day) + "/" + year;             
               }

               dateListResults.add(resultMsg);      // save the result for this date

               resultMsg = "";      // reset
            
            }   // end of IF dupDateReq
         }      // end of FOR loop
         
      }
      pstmt1.close();              // close the stmt

   }
   catch (Exception exc) {

      dbError(exc.toString(), out);
      return;
   }
   
   
   //
   //   Report the results
   //
 
   
   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"8\" cellspacing=\"5\">");

   out.println("<tr><td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println("<p align=\"center\">Results:<br><br>");

   for (int i=0; i<dateListResults.size(); i++) {      // process each date specified

      out.println(dateListResults.get(i)+ "<br><br>");
   }

   out.println("</font></td></tr></form></table>");    // end of table
  
   out.println("<br><form method=\"get\" action=\"Proshop_events\">");
   out.println("<input type=\"submit\" value=\"View All Events\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form>");

   out.println("<br><form method=\"get\" action=\"Proshop_events\">");
   out.println("<input type=\"hidden\" name=\"recur\" value=\"yes\">");
   out.println("<input type=\"submit\" value=\"Recur Another Event\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form>");

   out.println("<br><form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form>");
   
   //
   //  End of HTML page
   //
   out.println("</td></tr></table>");                // end of main page table
   out.println("</center></font></body></html>");
   out.close();

 }      // end of doRecur2

 
 
 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(String err, PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR>Error: " + err);
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }
}
