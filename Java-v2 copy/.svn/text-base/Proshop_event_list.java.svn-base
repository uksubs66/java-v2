/*
 **********************************************************************************************                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          /***************************************************************************************     
 *   Proshop_event_list:  This servlet will display a list of events and allow the user to
 *                        select one.  It will then display a URL for that event to be used
 *                        in custom content for email notifications.  The link is built to
 *                        provide a way for members to get into an event signup.
 *
 * 
 *   called by:  Proshop_content
 *
 *   created: 2/10/2011   Bob P.
 *
 *   last updated:
 *
 *    11/01/12  Fixed a bug that was causing events to get filtered out of the event list unintentionally.
 *     9/01/11  Added support for the Dining system - get dining events.
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
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Connect;
import com.foretees.common.Utilities;


public class Proshop_event_list extends HttpServlet {
                               

 String rev = SystemUtils.REVLEVEL;                          // Software Revision Level (Version)
 
 static String DINING_USER = ProcessConstants.DINING_USER;   // Dining username for Admin user from Dining System
 static int dining_activity_id = ProcessConstants.DINING_ACTIVITY_ID;    // Global activity_id for Dining System  
 


 //********************************************************************************
 //
 //  doGet - gets control from Proshop_content to display a list of events
 //
 //********************************************************************************
 //
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

   Statement stmt = null;
   Statement stmtm = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

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
      out.println("<BR><BR>If problem persists, contact support.");
      out.println("<BR><BR>");
       out.println("<form><p align=center>");
       out.println("<input type=\"button\" value=\"Cancel - Close\" onClick='self.close();'>");
       out.println("</p></form>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   String club = (String)session.getAttribute("club");      // get club name
   String user = (String)session.getAttribute("user");      // get pro's username
   
   if (!user.equals(DINING_USER)) {   // if NOT a Dining Admin user
      
      if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_MANAGECONTENT", con, out)) {
          SystemUtils.restrictProshop("SYSCONFIG_MANAGECONTENT", out);
          return;
      }
   }
   
   String omit = "";
   String name = "";
   String sampm = "";
   String eampm = "";
   String course = "";
   String ecolor = "";
   String sfb = "";
   String player = "";

   long date  = 0;
   long date2 = 0;
   long edate  = 0;
   long su_date  = 0;
   long c_date  = 0;
   long old_date  = 0;
     
   int time  = 0;
   int c_time  = 0;
   int month  = 0;
   int day = 0;
   int year = 0;
   int su_month  = 0;
   int su_day = 0;
   int su_year = 0;
   int mm  = 0;
   int dd = 0;
   int yy = 0;
   int shr = 0;
   int smin = 0;
   int gstOnly = 0;               // guest only event
   int max = 0;                   // max number of teams
   int signUp = 0;                // member sign up
   int multi = 0;                 // multiple course support
   int etime  = 0;
   int etime2  = 0;
   int fb  = 0;
   int c = 0;                     // number of signups for an event
   int c2 = 0;
   int season = 0;
   int actId = 0;
   int event_id = 0;
   
   boolean found = false;
   boolean skip = false;
   boolean sortBySeason = false;
   boolean showOld = (req.getParameter("showOld") != null) ? true : false;
   boolean sortDesc = (req.getParameter("desc") != null) ? true : false;
   
   String sortBy = (req.getParameter("sortBy") != null) ? req.getParameter("sortBy") : "date"; // default is to sort by date

   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   if (!user.equals(DINING_USER)) {   // if NOT a Dining Admin user
      
      //
      //   Get multi option for this club
      //
      try {

         stmtm = con.createStatement();        // create a statement

         rs = stmtm.executeQuery("SELECT multi " +
                                "FROM club5 WHERE clubName != ''");

         if (rs.next()) {

            multi = rs.getInt(1);
         }
         stmtm.close();

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Proshop Events Page - Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error:" + exc.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact support (provide this information).");
         out.println("<br><br>");
          out.println("<form><p align=center>");
          out.println("<input type=\"button\" value=\"Cancel - Close\" onClick='self.close();'>");
          out.println("</p></form>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
   }


   //
   //   Get current date and time
   //
   Calendar cal = new GregorianCalendar();        // get todays date
   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);
   int cal_min = cal.get(Calendar.MINUTE);

   //
   //  Build the 'time' string for display
   //
   //    Adjust the time based on the club's time zone (we are Central)
   //
   time = (cal_hourDay * 100) + cal_min;

   time = SystemUtils.adjustTime(con, time);       // adjust for time zone

   if (time < 0) {                // if negative, then we went back or ahead one day

      time = 0 - time;          // convert back to positive value

      if (time < 1200) {           // if AM, then we rolled ahead 1 day

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

   cal_hourDay = time / 100;                // get adjusted hour
   cal_min = time - (cal_hourDay * 100);          // get minute value

   month = month + 1;                            // month starts at zero
   date = (year * 10000) + (month * 100) + day;

   old_date = date - 10000;                  // current date minus 1 year

   //
   //   build the HTML page for the display
   //
   out.println(SystemUtils.HeadTitle("Proshop Events Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   //SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" valign=\"top\">");       // table for main page

   out.println("<tr><td align=\"center\" valign=\"top\">");
   //out.println("<font size=\"2\">");

   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" valign=\"top\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"3\">");
      out.println("<b>Event List</b>");
      out.println("</font><font size=\"2\"><br><br>");
      out.println("Click on the event name to get the URL for that event.<br>");
      out.println("You can click on the underlined column headers to sort the list by that column.");
      out.println("</font></td>");
   out.println("</tr></table>");

   out.println("</td></tr><tr>");

   out.println("<td align=\"center\" valign=\"top\">");
   out.println("<font size=\"2\"><br>");
   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" valign=\"top\">");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

   if (!user.equals(DINING_USER)) {   // if NOT a Dining Admin user       

      out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\"><b>");
               out.println("<a href='?sortBy=sudate" + ((sortBy.equals("sudate") && !sortDesc) ? "&desc" : "") + ((showOld) ? "&showOld" : "") + "' style='color:white'>Sign-up Date</a>");
            out.println("</b></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\"><b>");
            out.println("<a href='?sortBy=name" + ((sortBy.equals("name") && !sortDesc) ? "&desc" : "") + ((showOld) ? "&showOld" : "") + "' style='color:white'>Event Name</a>");
            out.println("</b></font></td>");

      if (sess_activity_id != 0) {
         /*
         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<b>Activity</b>");
            out.println("</font></td>");
         */
      } else {

          if (multi != 0) {
             out.println("<td align=\"center\">");
                out.println("<font color=\"#FFFFFF\" size=\"2\">");
                out.println("<b>Course</b>");
                out.println("</font></td>");
          }
      }
         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\"><b>");
            out.println("<a href='?sortBy=date" + ((sortBy.equals("date") && !sortDesc) ? "&desc" : "") + ((showOld) ? "&showOld" : "") + "' style='color:white'>Date of Event</a>");
            out.println("</b></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<b>Time of Event</b>");
            out.println("</font></td></tr>");

   } else {     // Dining Admin User
      
      out.println("<tr bgcolor=\"#336633\">");      
         out.println("<td align=\"center\">");                       //  Event Date
            out.println("<font color=\"#FFFFFF\" size=\"2\"><b>");
            out.println("<a href='?sortBy=date" + ((sortBy.equals("date") && !sortDesc) ? "&desc" : "") + ((showOld) ? "&showOld" : "") + "' style='color:white'>Date of Event</a>");
            out.println("</b></font></td>");

         out.println("<td align=\"center\">");                       // Name
            out.println("<font color=\"#FFFFFF\" size=\"2\"><b>");
            out.println("<a href='?sortBy=name" + ((sortBy.equals("name") && !sortDesc) ? "&desc" : "") + ((showOld) ? "&showOld" : "") + "' style='color:white'>Event Name</a>");
            out.println("</b></font></td>");

         out.println("<td align=\"center\">");                       // Signup Date
            out.println("<font color=\"#FFFFFF\" size=\"2\"><b>");
               out.println("Sign Up Date");
            out.println("</b></font></td></tr>");      
   }         

   //
   // get all events in the table - order by date & time
   //
   try {

      if (!user.equals(DINING_USER)) {   // if NOT a Dining Admin user
         
         //
         //  Golf - get the golf events
         //
         if (club.equals("blackstone") || club.equals("bellehaven") || club.equals("elmcrestcc") || club.equals("awbreyglen")) {
             sortBySeason = true;
         } else {
             sortBySeason = false;
         }

         // events will be assigned a root_activity_id and not a traditional activity as in most instances
         String sql = "" +
            "SELECT name, date, year, month, day, color, act_hr, act_min, courseName, " +
               "signUp, max, gstOnly, su_month, su_day, su_year, fb, " +
               "(SELECT COUNT(*) FROM evntsup2b es WHERE es.name = e.name AND es.player1 <> '' AND inactive = 0) AS c, " +
               "(SELECT COUNT(*) FROM evntsup2b es WHERE es.name = e.name AND es.player1 <> '' AND es.wait = 0 AND es.moved = 0 AND inactive = 0) AS c2, " +
               "IF(season=1, c_date, date) AS date2, season, activity_id, event_id " +
            "FROM events2b e " +
            ((showOld) ? "WHERE IF(season=1, c_date, date) < " + date : "WHERE IF(season=1, c_date, date) >= " + date) + " " +
            "AND activity_id = " + sess_activity_id + " AND inactive = 0 ";
            if (sortBy.equals("sudate")) {         // sort by signup date, name
               sql += "ORDER BY su_date " + ((sortDesc) ? "DESC" : "") + ", name;";
            } else if (sortBy.equals("date")) {    // sort by event date, time
               sql += "ORDER BY " + (sortBySeason ? "season DESC, " : "") + "date2 " + ((sortDesc) ? "DESC" : "") + ", act_hr;";
            } else if (sortBy.equals("name")) {    // sort by event name
               sql += "ORDER BY name " + ((sortDesc) ? "DESC" : "") + ";";
            }

         stmt = con.createStatement();
         rs = stmt.executeQuery( sql );

         while (rs.next()) {

            name = rs.getString(1);
            edate = rs.getLong(2);
            yy = rs.getInt(3);
            mm = rs.getInt(4);
            dd = rs.getInt(5);
            ecolor = rs.getString(6);
            shr = rs.getInt(7);
            smin = rs.getInt(8);
            course = rs.getString(9);
            signUp = rs.getInt(10);
            max = rs.getInt(11);
            gstOnly = rs.getInt(12);
            su_month = rs.getInt(13);
            su_day = rs.getInt(14);
            su_year = rs.getInt(15);
            sfb = rs.getString(16);
            c = rs.getInt(17);                     // total count of all signups
            c2 = rs.getInt(18);                    // count of signups not moved, if zero then we'll hide this event
            date2 = rs.getLong(19);
            season = rs.getInt(20);
            actId = rs.getInt(21);
            event_id = rs.getInt(22);

            etime = (shr * 100) + smin;            // create time value for teepast search

            if (smin < 59) {

               etime2 = (shr * 100) + smin + 1;    // create time value (of event + 1) for teepast search

            } else {

               etime2 = (shr + 1) * 100;           // must be xx:59, set time = yy:00 (next hour)
            }

            // If season long event, substitute cut-off date for actual date
            if (season == 1) {
                edate = date2;
            }

            //
            //  prepare F/B indicator
            //
            fb = 0;

            if (sfb != null && sfb.equals( "Back" )) fb = 1;


            //
            //  do not display if event is 2nd day of event or not used for sign up
            //
            // (signUp == 1 || max > 0 || gstOnly == 1) 
            // if sign up is allowed OR if max teams greater than zero OR if guest only event
            if (signUp != 0 || max != 0 || gstOnly != 0) {

               //
               //  now check if this is an old event that has already been checked in (players)
               //
               skip = false;                       // init


               // if event already occurred and its a member sign-up type (only do this check for golf events)
               if (sess_activity_id == 0 && edate < date && (signUp != 0 || gstOnly != 0)) {

                  PreparedStatement pstmt = con.prepareStatement (
                     "SELECT player1 " +
                     "FROM teepast2 WHERE date = ? AND (time = ? OR time = ?) AND fb = ? and courseName = ?");

                  pstmt.clearParameters();         // clear the parms
                  pstmt.setLong(1, edate);         // put the parm in pstmt
                  pstmt.setLong(2, etime);
                  pstmt.setLong(3, etime2);
                  pstmt.setInt(4, fb);
                  pstmt.setString(5, course);
                  rs2 = pstmt.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     player = rs2.getString(1);

                     if (!player.equals( "" )) {

                        skip = true;               // player has been checked in - skip this one
                        out.println("<!-- SKIPPING " + name + " - Players on tee sheet -->");
                     }
                  }

                  pstmt.close();
               }

               // skip this event if it is more than 1 year old
               if (skip == false && edate < old_date) {

                  skip = true;
                  out.println("<!-- SKIPPING " + name + " - Event is older than 1 year (" + edate + ") -->");
               }

               // skip if it's an old event and there are no signups left 
               // that are not moved AND it's not an outside event
               if (skip == false && (date > edate && c2 == 0 && gstOnly != 1 && season != 1)) {

                  skip = true;
                  out.println("<!-- SKIPPING " + name + " - Old event with no signups left to be moved and not guest only (" + edate + ") -->");
               }

               if (skip == false) {                // if ok to display

                  found = true;                    // found some

                  sampm = " AM";
                  if (shr == 12) {
                     sampm = " PM";
                  }
                  if (shr > 12) {
                     sampm = " PM";
                     shr = shr - 12;    // convert to conventional time
                  }

                  //
                  //  Build the HTML for each record found
                  //
                  out.println("<form action=\"Proshop_event_list\" method=\"post\">");

                  out.println("<tr>");
                     out.println("<td>");
                     out.println("<font size=\"2\">");
                     if (signUp != 0) {
                        out.println("<p align=\"center\">" + su_month + "/" + su_day + "/" + su_year + "</p>");
                     } else {
                        out.println("<p align=\"center\">N/A</p>");
                     }
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">");
                     out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                     out.println("<input type=\"hidden\" name=\"event_id\" value=\"" + event_id + "\">");
                     out.println("<input type=\"submit\" value=\"" + name + "\" style=\"background:" +ecolor+ "\">");
                     out.println("</p>");
                     out.println("</font></td>");

                  if (sess_activity_id != 0) {
                     /*
                     out.println("<td>");
                        out.println("<font size=\"2\">");
                        out.println(getActivity.getActivityName(actId, con));
                        out.println("</font></td>");
                     */
                  } else {

                      if (multi != 0) {

                         out.println("<td>");
                            out.println("<font size=\"2\">");

                            if (club.equals("congressional")) {
                                out.println("<p align=\"center\">" + congressionalCustom.getFullCourseName(edate, dd, course) + "</p>");
                            } else {
                                out.println("<p align=\"center\">" + course + "</p>");
                            }

                            out.println("</font></td>");
                      }
                  }
                  out.println("<td>");
                     out.println("<font size=\"2\">");
                     if (season == 0) {
                       out.println("<p align=\"center\">" + mm + "/" + dd + "/" + yy + "</p>");
                     } else {
                       out.println("<p align=\"center\">Season Long</p>");
                     }
                     out.println("</font></td>");

                     out.println("<td>");
                     out.println("<font size=\"2\">");
                  out.println("<p align=\"center\">" + shr + ":" + Utilities.ensureDoubleDigit(smin) + sampm + "</p>");
                  out.println("</font></td></tr></form>");
               }

            } else {

               out.println("<!-- SKIPPING " + name + " - Signups not allowed OR max teams is zero OR guest only -->");

            }

         }    // end of while

         stmt.close();
      
      } else {
         
         //
         //  Dining Admin User - get the dining events
         //
         Connection con_d = null;
         
         String sortOrder = "";
         
         found = false;               // init to no events found

         int organization_id = Utilities.getOrganizationId(con);      // get the Dining org id for this club (identifes the dining database)

         if (organization_id > 0) {

            con_d = Connect.getDiningCon();

            if (con_d != null) {
               
               sortOrder = "e.date, e.name";     // default sort order
               
               if (sortBy.equals("name")) sortOrder = "e.name";
              
               String sql = "" +
                  "SELECT e.id, e.name, " +
                        "e.minimum_advance_days, e.maximum_advance_days, " +
                        "to_char(e.start_time, 'HH24MI') AS stime, " +
                        "to_char(e.end_time, 'HH24MI') AS etime, " +
                        "to_char(e.date, 'YYYYMMDD')::int AS our_date, " +
                        "e.costs, loc.name AS location_name " +
                  "FROM events e " +
                  "LEFT OUTER JOIN locations AS loc ON e.location_id = loc.id " +
                  "WHERE e.organization_id = ? AND " +
                        "to_char(e.date, 'YYYYMMDD')::int >= ? AND " +
                        "e.members_can_make_reservations = true " +
                  "ORDER BY " + sortOrder;

               PreparedStatement pstmt = con_d.prepareStatement (sql);

               pstmt.clearParameters();         // clear the parms
               pstmt.setInt(1, organization_id);
               pstmt.setLong(2, Utilities.getDate(con));

               rs = pstmt.executeQuery();      // execute the prepared stmt

               while (rs.next()) {

                  event_id = rs.getInt(1);
                  name = rs.getString(2);
                  edate = rs.getInt("our_date");
                  int signup_day = rs.getInt("maximum_advance_days");
                  int signup_date = Utilities.getDate(rs.getInt("our_date"), (signup_day * -1));
                     
                  yy = (int)edate / 10000;
                  mm = (int)((edate - (yy * 10000)) / 100);
                  dd = (int)(edate - ((yy * 10000) + (mm * 100)));
                  
                  year = (int)signup_date / 10000;
                  month = (int)((signup_date - (year * 10000)) / 100);
                  day = (int)(signup_date - ((year * 10000) + (month * 100)));                  
                  
                  //
                  //  Build the HTML for each record found
                  //
                  out.println("<form action=\"Proshop_event_list\" method=\"post\">");

                  out.println("<tr>");
                     out.println("<td>");
                     out.println("<font size=\"2\">");     // event date
                     out.println("<p align=\"center\">" + mm + "/" + dd + "/" + yy + "</p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font size=\"2\">");         // name - also submit button
                     out.println("<p align=\"center\">");
                     out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                     out.println("<input type=\"hidden\" name=\"event_id\" value=\"" + event_id + "\">");
                     out.println("<input type=\"submit\" value=\"" + name + "\">");
                     out.println("</p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font size=\"2\">");      // sign up date
                     out.println("<p align=\"center\">" + month + "/" + day + "/" + year + "</p>");
                     out.println("</font></td>");
                  out.println("</tr></form>");
                  
                  found = true;     // at least one event found
               
               }    // end of WHILE events
               
               pstmt.close();
               con_d.close();
      
            }   // end of IF connection found
         }
      
      }   // end of IF Golf or Dining
      

      out.println("</font></table></font></td></tr>");

      if (found == false) {

         out.println("<tr><td><br>");
            out.println("<p align=\"center\">There are no events scheduled at this time.</p>");
         out.println("</td></tr>");
         
      }    // end of if

      //out.println("</font></td></tr>");
      out.println("</table>");                   // end of main page table

       out.println("<br><form><p align=center>");
       out.println("<input type=\"button\" value=\"Cancel - Close\" onClick='self.close();'>");
       out.println("</p></form>");

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
       out.println("<br><br><form><p align=center>");
       out.println("<input type=\"button\" value=\"Cancel - Close\" onClick='self.close();'>");
       out.println("</p></form>");
      out.println("</CENTER></BODY></HTML>");
   }

 }   // end of doGet

 
 
 
 //********************************************************************************
 //
 //  doPost - gets control from doGet to display a URL for the selected event
 //
 //********************************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    

   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

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
      out.println("<BR><BR>If problem persists, contact support.");
      out.println("<BR><BR>");
       out.println("<form><p align=center>");
       out.println("<input type=\"button\" value=\"Cancel - Close\" onClick='self.close();'>");
       out.println("</p></form>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   String club = (String)session.getAttribute("club");      // get club name
   String user = (String)session.getAttribute("user");      // get pro's username
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   
   if (!user.equals(DINING_USER)) {   // if NOT a Dining Admin user
      
      if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_MANAGECONTENT", con, out)) {
          SystemUtils.restrictProshop("SYSCONFIG_MANAGECONTENT", out);
          return;
      }
      
   } else {
      
      sess_activity_id = dining_activity_id;      // use Dining activity id for link below
   }
   
   
   int event_id = 0;

   
    try { event_id = Integer.parseInt(req.getParameter("event_id")); }
    catch (Exception ignore) {}

    String name = req.getParameter("name");
   
   
   //
   //   build the HTML page for the display
   //
   out.println(SystemUtils.HeadTitle("Proshop Events Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" valign=\"top\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"3\">");
      out.println("<b>Event Link URL</b>");
      out.println("</font><font size=\"2\"><br><br>");
      out.println("Use your mouse to select the URL below, then copy it.<br>");
      out.println("When you return to the Email Content page, you can paste the URL into the link editor.<br>");
      out.println("</font></td>");
   out.println("</tr></table>");

   out.println("<BR><p align=\"center\">Use the following URL to link to event: <b>" +name+ "</b>.</p>");
   
   out.println("<BR><p align=\"center\">http://web.foretees.com/"+rev+"/servlet/Login?extlogin=yes&caller=event&act_id=" +sess_activity_id+ "&event=" +event_id+ "&els=##ELS##</p>");
   
   // ************ Use the following for TESTING - DEV SERVER *********************
   //out.println("<BR><p align=\"center\">http://dev.foretees.com/"+rev+"/servlet/Login?extlogin=yes&caller=event&act_id=" +sess_activity_id+ "&event=" +event_id+ "&els=##ELS##</p>");
        
   out.println("<br><form><p align=center>");
   out.println("<input type=\"button\" value=\"Return to Email Content\" onClick='self.close();'>");
   out.println("</p></form>");

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");    

 }   // end of doPost

}
