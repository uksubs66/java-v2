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
import com.foretees.common.Utilities;


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

   Connection con = SystemUtils.getCon(session);                      // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_EVENT", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_EVENT", out);
   }

   //
   //  Check if call is to copy an existing event
   //
   if (req.getParameter("copy") != null) {

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
      out.println("To change or remove an event, click on the Select button within the event.");
      out.println("<br></font></td></tr></table>");
      out.println("<br><br>");

      out.println("<table border=\"2\" cellpadding=\"5\"><tr bgcolor=\"#F5F5DC\">");
      if (multi != 0) {           // if multiple courses supported for this club
         out.println("<td colspan=\"8\" align=\"center\">");
      } else {
         out.println("<td colspan=\"7\" align=\"center\">");
      }
      out.println("<font size=\"2\">");
      out.println("<p align=\"center\"><font size=3><b>Currently Active Events</b></font></p>");
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

      stmt = con.createStatement();        // create a statement

      String sql = "" +
                   "SELECT *, IF(season=1, c_date, date) as date2 " +
                   "FROM events2b " +
                   "WHERE activity_id = " + sess_activity_id + " AND inactive = 0 ";
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
         out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events\" target=\"bot\">");
         out.println("<td align=\"center\">");
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
            out.println("<font size=\"2\"><p>" + first_hr + ":" + Utilities.ensureDoubleDigit(first_min) + " " + f_ampm + "</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (season == 1) {
             out.println("<font size=\"2\"><p>N/A</p>");
         } else {
            out.println("<font size=\"2\"><p>" + last_hr + ":" + Utilities.ensureDoubleDigit(last_min) + " " + l_ampm + "</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" + Labels.gender_opts[gender] + "</p>");
         out.println("</font></td>");

         out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");    // must pass whole name!!!!
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<td align=\"center\">");
         out.println("<input type=\"submit\" value=\"Select\">");
         out.println("</td></form></tr>");
         
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
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;

   }  // end of try

   //
   //  End of HTML page
   //
   out.println("</table><br>");                           // end of event table

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   out.println("</center></font></body></html>");

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
   ResultSet rs = null;

   //String [] month_table = { "inv", "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
   //                          "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_EVENT", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_EVENT", out);
   }
   
   boolean copy = false;

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
      
   long su_date = 0;
   long c_date = 0;

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

   String club = (String)session.getAttribute("club");      // get club name
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
/*
   boolean USE_NEW_SIGNUP = false;         // for testing new features

   if (club.startsWith("demo") ||
       club.equals("ridgeclub") 
      ) USE_NEW_SIGNUP = true;
*/
   boolean ext_Q = false;

   if (club.equals("johnsisland")) {

       ext_Q = true;
   }

   //
   //  Array to hold the course names
   //
   String courseName = "";
   ArrayList<String> course = new ArrayList<String>();

   String courseName1 = "";
   String oldCourse = "";      

   //
   // Get the name of the event 
   //
   String name = req.getParameter("name");
   String oldName = name;                // save original event name

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

      rs = stmtc.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);
      }
      stmtc.close();

      if (multi != 0) {           // if multiple courses supported for this club

         course = Utilities.getCourseNames(con);     // get all the course names
      }

      //
      // Get the event from the events table
      //
      PreparedStatement pstmt1 = con.prepareStatement (
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
   out.println(SystemUtils.HeadTitle2("Proshop - Events"));
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
   out.println("</HEAD>");
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" topmargin=\"0\">"); //  onLoad=\"moveitin()\"
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page

   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");
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
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_editevnt\" method=\"post\" name=\"eventform\">");
         } else {
            if (copy == true) {
                out.println("<form action=\"/" +rev+ "/servlet/Proshop_addevnt\" method=\"post\" target=\"bot\" name=\"eventform\" id=\"eventform\">");
                out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");
                out.println("<input type=\"hidden\" name=\"copyname\" value=\"" +name.trim()+ "\">");  // event to copy
            } else {
                out.println("<form action=\"/" +rev+ "/servlet/Proshop_editevnt\" method=\"post\" target=\"bot\" name=\"eventform\">");
            }
         }
         out.println("<input type=\"hidden\" name=\"event_id\" value=\"" + event_id +"\">");
                 
               out.println("<tr><td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"left\">Event name (Use A-Z, a-z, 0-9 and spaces <b>only</b>):&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"event_name\" value=\"" +name+ "\" size=\"30\" maxlength=\"30\">");
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

                   for (int tmp=2004; tmp <= 2014; tmp++) {
                       Common_Config.buildOption(tmp, tmp, year, out);
                   }

                    out.println("</select><br><br>");


                 if (activity_id != 0) {

                    //ArrayList<Integer> locations = new ArrayList<Integer>();
                    ResultSet rs2 = null;
                    Statement stmt2 = null;

                    // display a series of checkboxes for each court (sub-activity w/ time sheets) that this event will take place on
                    // SELECT * FROM activities WHERE activity_id NOT IN (SELECT parent_id FROM activities)

                    out.println("Choose the locations for this event:&nbsp;&nbsp;");

                    String in = getActivity.buildInString(activity_id, 1, con);
                    i = 0;
                    try {

                       boolean checked = false;
                       String sql = "" +
                               "SELECT activity_id, activity_name " +
                               "FROM activities " +
                               "WHERE activity_id IN (" + in + ") AND " +
                                   "activity_id NOT IN (SELECT parent_id FROM activities) " +
                               "ORDER BY activity_name";
                       out.println("<!-- " + sql + " -->");
                       stmt = con.createStatement();
                       rs = stmt.executeQuery( sql );

                       while ( rs.next() ) {

                           stmt2 = con.createStatement();
                           rs2 = stmt2.executeQuery( "SELECT true FROM club5 WHERE " + rs.getInt("activity_id") + " IN (" + locations_csv + ")" );

                           checked = rs2.next();

                           out.println("<br>&nbsp;&nbsp;&nbsp; <input type=checkbox name='actChkBox_" + i + "' value='" + rs.getInt("activity_id") + "'" + ((checked) ? " checked" : "") + ">&nbsp; " + rs.getString("activity_name"));
                           i++;
                       }

                    } catch (Exception exc) {

                        Utilities.logError("Error in Proshop_events loading activities: i=" + i + ", err=" + exc.toString());

                    } finally {

                        try { rs.close(); }
                        catch (Exception ignore) {}

                        try { stmt.close(); }
                        catch (Exception ignore) {}

                        try { rs2.close(); }
                        catch (Exception ignore) {}

                        try { stmt2.close(); }
                        catch (Exception ignore) {}

                        out.println("<input type=hidden name=location_count value='" + i + "'>");

                    }

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

                   out.println("</div>");

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
                    out.println("Gender Based Event?&nbsp;&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"gender\">");
                    if (gender == 0) out.println("<option value=\"-1\">Choose...</option>");
                    Common_Config.buildOption(1, Labels.gender_opts[1], gender, out);
                    Common_Config.buildOption(2, Labels.gender_opts[2], gender, out);
                    Common_Config.buildOption(3, Labels.gender_opts[3], gender, out);
                    out.println("</select>");
                    if (gender == 0) out.println("&nbsp; &nbsp; <font color=red><b>* Must Specify</b></font>");
                    out.println("<br><br>");
                  out.println("Color to make this event on the tee sheet:&nbsp;&nbsp;");

                  Common_Config.displayColorsAll(color, out);       // output the color options

                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                    out.println("<a href=\"/" +rev+ "/proshop_color.htm\" target=\"_blank\">View Colors</a>");
                    out.println("<br><br>");
                    //
                    //   Script will put any existing text in the itinerary textarea (value= doesn't work)
                    //
                    //out.println("<input type=\"hidden\" name=\"olditin\" value=\"" + itin + "\">"); // hold itin for script

                    out.println("Itinerary: (Optional explaination of event - double quotes not allowed)<br>");
                    out.println("<textarea name=\"itin\" id=\"itin\" cols=\"55\" rows=\"10\">");
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
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_hdcp value=1 onchange=\"updateCheckbox(this.name, 'req_hdcp')\"" + ((ask_hdcp == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_hdcp value=1" + ((req_hdcp == 1) ? " checked" : "") + "></td><td><font size=2>Include HDCP Numbers?</td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_gender value=1 onchange=\"updateCheckbox(this.name, 'req_gender')\"" + ((ask_gender == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_gender value=1" + ((req_gender == 1) ? " checked" : "") + "></td><td><font size=2>Include Gender?</td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_homeclub value=1 onchange=\"updateCheckbox(this.name, 'req_homeclub')\"" + ((ask_homeclub == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_homeclub value=1" + ((req_homeclub == 1) ? " checked" : "") + "></td><td><font size=2>Include Home Club, ST? *</td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_phone value=1 onchange=\"updateCheckbox(this.name, 'req_phone')\"" + ((ask_phone == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_phone value=1" + ((req_phone == 1) ? " checked" : "") + "></td><td><font size=2>Include Phone Number? *</td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_email value=1 onchange=\"updateCheckbox(this.name, 'req_email')\"" + ((ask_email == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_email value=1" + ((req_email == 1) ? " checked" : "") + "></td><td><font size=2>Include Email Address? *</td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_address value=1 onchange=\"updateCheckbox(this.name, 'req_address')\"" + ((ask_address == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_address value=1" + ((req_address == 1) ? " checked" : "") + "></td><td><font size=2>Include Mailing Address? *</td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_shirtsize value=1 onchange=\"updateCheckbox(this.name, 'req_shirtsize')\"" + ((ask_shirtsize == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_shirtsize value=1" + ((req_shirtsize == 1) ? " checked" : "") + "></td><td><font size=2>Include Shirt Size?</td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_shoesize value=1 onchange=\"updateCheckbox(this.name, 'req_shoesize')\"" + ((ask_shoesize == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_shoesize value=1" + ((req_shoesize == 1) ? " checked" : "") + "></td><td><font size=2>Include Shoe Size?</td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_otherQ1 value=1 onchange=\"updateCheckbox(this.name, 'req_otherQ1')\"" + ((ask_otherQ1 == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_otherQ1 value=1" + ((req_otherQ1 == 1) ? " checked" : "") + "></td><td><font size=2>Include Custom Question #1?&nbsp; &nbsp;<input type=text name=otherQ1 size=26 maxlength=" + ((ext_Q) ? "64" : "32") + " value=\"" + otherQ1 + "\"></td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_otherQ2 value=1 onchange=\"updateCheckbox(this.name, 'req_otherQ2')\"" + ((ask_otherQ2 == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_otherQ2 value=1" + ((req_otherQ2 == 1) ? " checked" : "") + "></td><td><font size=2>Include Custom Question #2?&nbsp; &nbsp;<input type=text name=otherQ2 size=26 maxlength=" + ((ext_Q) ? "64" : "32") + " value=\"" + otherQ2 + "\"></td></tr>");
                out.println("<tr><td>&nbsp;</td><td><input type=checkbox name=ask_otherQ3 value=1 onchange=\"updateCheckbox(this.name, 'req_otherQ3')\"" + ((ask_otherQ3 == 1) ? " checked" : "") + "></td><td><input type=checkbox name=req_otherQ3 value=1" + ((req_otherQ3 == 1) ? " checked" : "") + "></td><td><font size=2>Include Custom Question #3?&nbsp; &nbsp;<input type=text name=otherQ3 size=26 maxlength=" + ((ext_Q) ? "64" : "32") + " value=\"" + otherQ3 + "\"></td></tr>");
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
               out.println("if (ret) ret = confirm('Note: This action is permanent and CAN NOT be undone!\\n\\nAre you absolutly sure you want to delete this event?');");
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
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_events\">");
      out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
   } else {
      out.println("<form><input type=\"button\" value=\"Cancel\" onclick='self.close();'>");
      out.println("</form>");
   }
   out.println("</font>");
   out.println("</center></font>");
   
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
   String templott = (String)session.getAttribute("lottery");
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   //
   //  Build the HTML page to display the existing events
   //
   out.println(SystemUtils.HeadTitle("Proshop Member Events Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

   out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"3\">");
   out.println("<b>Copy an Event</b><br>");
   out.println("</font>");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<b>Instructions:</b>&nbsp;&nbsp;Use this feature to create a new event by copying an existing event.<br>");
   out.println("Select the event you wish to copy from the list below.");
   out.println("</font></td></tr></table>");
   out.println("<br><br>");

   out.println("<!-- LOADING ALL EVENTS FOR ACTIVITY ID# " + sess_activity_id + " -->");

   //
   //  Get and display the existing events
   //
   try {

      stmt = con.createStatement();
      rs = stmt.executeQuery("SELECT name FROM events2b WHERE activity_id = " + sess_activity_id + " AND inactive = 0 ORDER BY name");

      // if we found any then output the form.
      rs.last();

      if (rs.getRow() > 0) {

         out.println("<font size=\"2\">");
         out.println("<p>Select the event you wish to copy.</p>");

         out.println("<form action=\"/" +rev+ "/servlet/Proshop_events\" method=\"post\" target=\"bot\">");
         out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");     // tell addevent its a copy
         out.println("<select size=\"1\" name=\"name\">");
         
         rs.beforeFirst();

         while (rs.next()) {

            out.println("<option>" +rs.getString("name")+ "</option>");
         }

         out.println("</select><br><br>");

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
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");

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
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_events\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center></font></body></html>");
   out.close();

 }


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
   out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }
}
