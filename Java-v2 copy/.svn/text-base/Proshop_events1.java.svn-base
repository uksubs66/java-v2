                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          /***************************************************************************************     
 *   Proshop_events1:  This servlet will search the events table for all events scheduled
 *                     and list them.  User can then select the event to manage its sign up.
 *
 *
 *
 *   called by:  Proshop_main
 *
 *   created: 1/11/2002   Bob P.
 *
 *   last updated:
 *
 *       11/19/13   Merion GC (merion) - Added to tcclub custom to ensure that events are always displayed in the event list (case 2325).
 *        6/14/13   The Country Club (tcclub) - Added custom to ensure that events are always displayed in the event list; avoiding the usual skip mechanics such as players being moved to the sheets.
 *       11/01/12   Fixed a bug that was causing events to get filtered out of the event list unintentionally.
 *       12/20/11   Added support for filtering the list of available events by defined event categories (case 2076).
 *        4/06/11   Ramsey CC (ramseycountryclub) - Sort by 'season' field first when sorting by date so season long events rise to the top of the list
 *        5/21/10   Awbrey Glen (awbreyglen) - Sort by 'season' field first when sorting by date so season long events rise to the top of the list
 *        3/30/10   When pulling events, if season long event, substitute cut-off date in place of date.
 *        3/11/10   Elmcrest CC (elmcrestcc) - Sort by 'season' field first when sorting by date so season long events rise to the top of the list
 *        3/11/10   Belle Haven (bellehaven) - Sort by 'season' field first when sorting by date so season long events rise to the top of the list
 *       12/08/09   Do not delete events or event signups - mark them inactive instead so we can easily restore them.
 *       11/04/09   Blackstone - Sort by 'season' field first when sorting by date so season long events rise to the top of the list
 *        9/28/09   Added support for Activities
 *        9/03/08   Added limited access proshop restrictions
 *        6/26/08   Fix season long event with no signups from being hidden in event listings
 *        5/02/08   Add html comment output for events that are not shown for easy debug
 *        4/22/08   Fix outside events not showing up on Previous Event listing
 *        4/02/08   Change sorting for season long events, now sorts by cutoff date
 *       12/12/07   Seperated listing into current/previous, added column sorting, and count of signups not moved to tee sheet in ()
 *        9/24/07   Add new column to event list for showing the number of sign-ups for each event  (Case #1092)
 *        4/25/07   Congressional - pass the date for the Course Name Labeling.
 *        3/20/07   Custom for Congressional - abstract the course name depending on the day (Course Name Labeling)
 *        1/12/06   Check teepast for any players during event by checking time of event plus 1 minute.
 *       11/02/05   Do not list events that are older than 1 year.
 *        1/24/05   Ver 5 - change club2 to club5.
 *        1/06/05   Do not list old events that have already been checked in.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        2/18/03   New - Add event sign up processing - call Proshop_events2
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
import com.foretees.common.congressionalCustom;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;


public class Proshop_events1 extends HttpServlet {
                               

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

      doPost(req, resp);          // call doGet processing

 }   // end of doGet


 //********************************************************************************
 //
 //  doPost - gets control from proshop_main to display a list of events
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
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if (!SystemUtils.verifyProAccess(req, "EVNTSUP_VIEW", con, out)) {
       SystemUtils.restrictProshop("EVNTSUP_VIEW", out);
       return;
   }
   
   String club = (String)session.getAttribute("club");      // get club name
   
   String omit = "";
   String name = "";
   String sampm = "";
   String eampm = "";
   String course = "";
   String ecolor = "";
   String sfb = "";
   String player = "";
   String categoryFilters = "";

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
   
   ArrayList<Integer> category_ids = new ArrayList<Integer>();
   ArrayList<Integer> selected_category_ids = new ArrayList<Integer>();
   ArrayList<String> category_names = new ArrayList<String>();
   
   String sortBy = (req.getParameter("sortBy") != null) ? req.getParameter("sortBy") : "date"; // default is to sort by date

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

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
      out.println("<br><br><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
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
   
   // Build a list of all event category_ids that have been configured for this activity
   category_ids = Utilities.buildEventCategoryIdList(sess_activity_id, con);

   //
   //   build the HTML page for the display
   //
   out.println(SystemUtils.HeadTitle("Proshop Events Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" valign=\"top\">");       // table for main page

   out.println("<tr><td align=\"center\" valign=\"top\">");
   //out.println("<font size=\"2\">");

      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" valign=\"top\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font size=\"3\">");
         out.println("<b>Event Registration</b>");
         out.println("</font><font size=\"2\"><br><br>");
         out.println("To access the sign up sheet, click on the event name.<br>");
         out.println("Use the Show Current/Previous button to toggle the listing between future/past events.<br>");
         out.println("The number in parentheses next to the sign-up count shows the number of signups<br>" +
                     "not on the wait list that have not been moved to the " + ((sess_activity_id == 0) ? "tee sheet" : "time sheet") + ".<br>");
         out.println("You can click on the underlined column headers to sort that column.");
         if (category_ids.size() > 0) out.println("<br><br>To filter the event list and only display events in a certain category, check the<br>" +
                 "checkboxes next to the desired categories and click Apply Filters.");
         if (category_ids.size() > 0) out.println("<br><br><b>NOTE:</b> If multiple categories are selected, only events that match all selected categories will be listed.");
         out.println("</font></td>");
      out.println("</tr></table>");

      out.println("</td></tr><tr>");
      
      out.println("<td align=center><br><form>");
      if (showOld) {
        out.println("<input type=submit value='Show Current'>");
      } else {
        out.println("<input type=submit value='Show Previous'><input type=hidden name=showOld>");
      }
      out.println("<input type=hidden name=sortBy value='" + sortBy + "'>");
      out.println("</form>");
              
      out.println("</td></tr>");
      
      if (category_ids.size() > 0) {      // Only proceed if at least one event category was found
                 
          boolean checked = false;
          
          // Now build a corresponding list of all the event category names for display (ordering in the ArrayList will match the list of Ids), and a list of any currently selected ids
          category_names = Utilities.buildEventCategoryNameList(sess_activity_id, con);
          
          if (req.getParameter("clearFilters") == null) selected_category_ids = Utilities.buildEventCategoryListFromReq(req, sess_activity_id, con);   // Don't load checked categories if "Reset Filters" was clicked
          
          for (int i=0; i<selected_category_ids.size(); i++) {
              categoryFilters += "&category_id_" + selected_category_ids.get(i);
          }
          
          out.println("<tr><td align=\"center\">");
          out.println("<form action=\"Proshop_events1\" method=\"POST\" name=\"frmFilters\">");
          out.println("<table border=\"0\" valign=\"top\" cellpadding=\"5\">");
          out.println("<tr><td align=\"center\" colspan=\"5\"><b>Filter by Category</b></td></tr>");

          out.println("<tr>");
          
          for (int i=0; i<category_ids.size(); i++) {
              
              checked = false;
              
              // Move to a new row every 5 categories
              if (i % 5 == 0) {
                  out.println("</tr><tr>");
              }
              
              for (int j=0; j<selected_category_ids.size(); j++) {
                  
                  if (category_ids.get(i) == selected_category_ids.get(j)) {
                      checked = true;
                      break;
                  }
              }
              
              out.println("<td align=\"left\">");
              out.println("<input type=\"checkbox\" name=\"category_id_" + category_ids.get(i) + "\" value=\"1\"" + (checked ? " checked" : "") + " id=\"category_id_" + category_ids.get(i) + "\"><label for=\"category_id_" + category_ids.get(i) + "\"><font size=\"2\">&nbsp;&nbsp;" + category_names.get(i) + "</font></label><br>");
              out.println("</td>");
          }

          out.println("</tr>");
          if (!sortBy.equals("")) out.println("<input type=\"hidden\" name=\"sortBy\" value=\"" + sortBy + "\">");
          if (sortDesc) out.println("<input type=\"hidden\" name=\"sortDesc\" value=\"1\">");
          if (showOld) out.println("<input type=\"hidden\" name=\"showOld\" value=\"1\">");
          out.println("<tr><td align=\"center\" colspan=\"5\">");
          out.println("<input type=\"submit\" name=\"applyFilters\" value=\"Apply Filters\">&nbsp;&nbsp;");
          out.println("<input type=\"submit\" name=\"clearFilters\" value=\"Clear Filters\">");
          out.println("</td></tr>");
          out.println("</table>");
          out.println("</form>");
          out.println("</td></tr>");
      }
      
      out.println("<tr><td align=\"center\" valign=\"top\">");
      out.println("<font size=\"2\">");
      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" valign=\"top\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\"><b>");
               out.println("<a href='?sortBy=sudate" + ((sortBy.equals("sudate") && !sortDesc) ? "&desc" : "") + ((showOld) ? "&showOld" : "") + categoryFilters + "' style='color:white'>Sign-up Date</a>");
               out.println("</b></font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Sign-ups</b>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\"><b>");
               out.println("<a href='?sortBy=name" + ((sortBy.equals("name") && !sortDesc) ? "&desc" : "") + ((showOld) ? "&showOld" : "") + categoryFilters + "' style='color:white'>Event Name</a>");
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
               out.println("<a href='?sortBy=date" + ((sortBy.equals("date") && !sortDesc) ? "&desc" : "") + ((showOld) ? "&showOld" : "") + categoryFilters + "' style='color:white'>Date</a>");
               out.println("</b></font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Time</b>");
               out.println("</font></td></tr>");


   //
   // get all events in the table - order by date & time
   //
   try {

      if (club.equals("blackstone") || club.equals("bellehaven") || club.equals("elmcrestcc") || club.equals("awbreyglen") || club.equals("ramseycountryclub")) {
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


            // Do not skip displaying events for the standard filtering reasons for the following clubs. Selected event categories will still filter the list.
            if (!club.equals("tcclub") && !club.equals("merion")) {
                
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
                
            }
            
            if (skip == false && selected_category_ids.size() > 0) {
                
               String tempCat = Utilities.checkEventCategoryBindings(event_id, selected_category_ids, con);
             
               if (!tempCat.equals("")) {
                   skip = true;
                   out.println("<!-- SKIPPING " + name + " - Event did not match the following filter category (" + tempCat + ") -->");
               }
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
               out.println("<form action=\"Proshop_events2\" method=\"post\" target=\"bot\">");

               out.println("<tr>");
                  out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (signUp != 0) {
                     out.println("<p align=\"center\">" + su_month + "/" + su_day + "/" + su_year + "</p>");
                  } else {
                     out.println("<p align=\"center\">N/A</p>");
                  }
                  out.println("</font></td>");

               out.println("<td align=center>");
                  out.println("<font size=\"2\">");
                  if (gstOnly != 1) {
                      out.print(c); // # of signups found
                      if (date > edate) out.print(" (" + c2 +")");
                  } else {
                     out.print("N/A");
                  }
                  out.println("</font></td>");
                  
               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\">");
                  out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
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

      out.println("</font></table></font></td></tr>");

      if (found == false) {

         out.println("<tr><td><br>");
         if (showOld) {
            out.println("<p align=\"center\">There are no previous events to be displayed.</p>");
         } else {
            out.println("<p align=\"center\">There are no events scheduled at this time.</p>");
         }
         out.println("</td></tr>");
         
      }    // end of if

      stmt.close();

      //out.println("</font></td></tr>");
      out.println("</table>");                   // end of main page table

      out.println("<br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

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
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
   }

 }   // end of doPost

}
