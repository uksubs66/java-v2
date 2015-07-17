/***************************************************************************************
 *   Member_events:  This servlet will search the events table for any events scheduled
 *                   for anytime after today's date.
 *
 *
 *
 *   called by:  member_main.htm
 *
 *   created: 1/11/2002   Bob P.
 *
 *   last updated:
 *
 *        5/21/10   Awbrey Glen (awbreyglen) - Sort by 'season' field first when sorting by date so season long events rise to the top of the list
 *        3/11/10   Elmcrest CC (elmcrestcc) - Sort by 'season' field first when sorting by date so season long events rise to the top of the list
 *        3/11/10   Belle Haven (bellehaven) - Sort by 'season' field first when sorting by date so season long events rise to the top of the list
 *       12/09/09   When looking for events only check those that are active.
 *       11/04/09   Blackstone - Sort by 'season' field first when sorting by date so season long events rise to the top of the list
 *        9/28/09   Added support for Activities
 *        2/10/09   Sort by gender for Patterson Club
 *       12/10/08   Allow users to access events that do not allow online signup
 *        8/11/08   Stonebridge Ranch - limit the course options based on mship type (case 1529).
 *        4/09/08   Fix for default sorting (was reverse sorting for females)
 *        4/02/08   Change sorting for season long events, now sorts by cutoff date
 *        4/02/08   Change default sorting so only tcclub defaults to gender, all others by date
 *        3/27/08   Add gender column and sorting options to event listing
 *       10/07/07   Highlight the signup date/time cells if current date is within the signup range.
 *        4/25/07   Congressional - pass the date for the ourse Name Labeling.
 *        3/20/07   Custom for Congressional - abstract the course name depending on the day (Course Name Labeling)
 *        3/09/05   Ver 5 - allow members to view all member events.
 *        1/24/05   Ver 5 - change club2 to club5.
 *       10/06/04   Ver 5 - add sub-menus.
 *        7/14/04   RDP Do not show column for date/time event sign-up starts if Old Oaks.
 *        6/30/04   RDP Add column for date/time event sign-up ends.
 *        1/13/04   JAG Modifications to match new color scheme.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        3/12/03   Change events2 to events2b for wait list.
 *        2/14/03   Add event sign up processing - call Member_events2
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
import com.foretees.common.congressionalCustom;
import com.foretees.common.Labels;


public class Member_events extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


//********************************************************************************
//
//  doGet - gets control from member_main to display a list of events
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

   Statement stmtm = null;
   ResultSet rs = null;

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

   String club = (String)session.getAttribute("club");   // get club name
   String caller = (String)session.getAttribute("caller");  
   String user = (String)session.getAttribute("user");  
   String mship = (String)session.getAttribute("mship");             // get member's mship type

   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   
   String omit = "";
   String name = "";
   String sampm = "";
   String eampm = "";
   String su_ampm = "";
   String c_ampm = "";
   String course = "";
   String ecolor = "";
   String highlight = "";
   long date  = 0;
   long edate  = 0;
   long c_date  = 0;
   long su_date  = 0;
   int time  = 0;
   int c_time  = 0;
   int su_time  = 0;
   int month  = 0;
   int day = 0;
   int year = 0;
   int su_month  = 0;
   int su_day = 0;
   int su_year = 0;
   int c_month  = 0;
   int c_day = 0;
   int c_year = 0;
   int mm  = 0;
   int dd = 0;
   int yy = 0;
   int shr = 0;
   int smin = 0;
   int su_hr = 0;
   int su_min = 0;
   int c_hr = 0;
   int c_min = 0;
   int ehr = 0;
   int emin = 0;
   int signUp = 0;
   int gst = 0;
   int multi = 0;
   int gender = 0;
   int season = 0;

   boolean doHeader = true;
   boolean skipEvent = false;
   boolean sortBySeason = false;
   
   boolean showOld = (req.getParameter("showOld") != null) ? true : false;
   boolean sortDesc = (req.getParameter("desc") != null) ? true : false;
   
   String default_sortBy = (club.equals("tcclub") || club.equals("pattersonclub")) ? "gender" : "date";
   
   String sortBy = (req.getParameter("sortBy") != null) ? req.getParameter("sortBy") : default_sortBy; // default is to sort by date
   
   String tmp_gender = "";
   String tmp_mtype = "";
   
   
   //
   // If the user is not trying to sort by any rows, then by default lets try to sort by their gender
   //
   //if (req.getParameter("sortBy") == null) {
   if (default_sortBy.equals("gender")) {
       
       try {

          PreparedStatement pstmt = con.prepareStatement ("SELECT m_type, gender FROM member2b WHERE username = ?");

          pstmt.clearParameters();
          pstmt.setString(1, user);
          rs = pstmt.executeQuery();

          if (rs.next()) {

              tmp_gender = rs.getString("gender");
              tmp_mtype = rs.getString("m_type");

              if (tmp_gender.equalsIgnoreCase("f") || tmp_mtype.toLowerCase().endsWith("female")) {
                  sortDesc = true;
              }
          }

          pstmt.close();
       
       } catch (Exception ignore) { }
   
   }
   
   
   if (sess_activity_id == 0) {
   
       //
       //   Get multi option for this club
       //
       try {

          stmtm = con.createStatement();        // create a statement
          rs = stmtm.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");

          if (rs.next()) multi = rs.getInt(1);

          stmtm.close();
       }
       catch (Exception exc) {

          out.println(SystemUtils.HeadTitle("Member Events Page - Error"));
          out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
          out.println("<CENTER><BR>");
          out.println("<BR><BR><H3>Database Access Error</H3>");
          out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
          out.println("<BR>Error:" + exc.getMessage());
          out.println("<BR><BR>Please try again later.");
          out.println("<BR><BR>If problem persists, contact your golf shop (provide this information).");
          out.println("<br><br><a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
          out.println("</CENTER></BODY></HTML>");
          out.close();
          return;
       }

   }
   
   //
   //   Get current date and time (adjusted for time zone)
   //
   date = SystemUtils.getDate(con);      
   time = SystemUtils.getTime(con);    

   int cal_hourDay = time / 100;                    // get adjusted hour
   int cal_min = time - (cal_hourDay * 100);        // get minute value

   //
   // use the date to search table
   //
   try {
       
       if (club.equals("blackstone") || club.equals("bellehaven") || club.equals("elmcrestcc") || club.equals("awbreyglen")) {
           sortBySeason = true;
       } else {
           sortBySeason = false;
       }

       String sql = "" + 
         "SELECT name, date, year, month, day, color, act_hr, act_min, courseName, signUp, " +
            "c_month, c_day, c_year, c_hr, c_min, c_date, c_time, " +
            "su_month, su_day, su_year, su_hr, su_min, su_date, su_time, gender, season, " +
            "IF(season=1, c_date, date) AS date2 " +
         "FROM events2b " + 
         "WHERE IF(season=1, c_date, date) >= ? AND gstOnly = ? AND activity_id = ? AND inactive = 0 ";

       if (sortBy.equals("date")) {               // sort by event date, time
           sql += "ORDER BY " + (sortBySeason ? "season DESC, " : "") + "date2 " + ((sortDesc) ? "DESC" : "") + ", act_hr;";
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
         
       }
       
      PreparedStatement stmt = con.prepareStatement ( sql );
      
      stmt.clearParameters();
      stmt.setLong(1, date);
      stmt.setInt(2, gst);
      stmt.setInt(3, sess_activity_id);
      rs = stmt.executeQuery();

      //
      //   build the HTML page for the display
      //
      out.println(SystemUtils.HeadTitle("Member Events Page"));
      out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" style=\"font-family:Arial\">");
      SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" valign=\"top\">");       // table for main page

      out.println("<tr><td align=\"center\" valign=\"top\" width=\"75%\">");
      out.println("<font size=\"2\">");

      while ( rs.next() ) {
          
          if (doHeader) {

             out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" width=\"500\" cellpadding=\"5\" valign=\"top\">");
                out.println("<tr><td align=\"center\">");
                out.println("<font size=\"3\">");
                out.println("<b>Upcoming Special Events</b>");
                out.println("</font><font size=\"2\"><br><br>");
                out.println("To view the event information or to sign up for an event, click on the event name.<br>");
                out.println("If unable to select the event, then it is not available for signup.");
                out.println("</font></td>");
             out.println("</tr></table>");

             out.println("</td></tr><tr>");
             out.println("<td align=\"center\" valign=\"top\">");
             out.println("<font size=\"2\"><br>");
             out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"5\" valign=\"top\">");
                out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
                      out.println("<font color=\"#ffffff\" size=\"2\"><b>");
                      out.println("<a href='?sortBy=name" + ((sortBy.equals("name") && !sortDesc) ? "&desc" : "") + ((showOld) ? "&showOld" : "") + "' style='color:white'>Event Name</a>");
                      out.println("</b></font></td>");
                if (sess_activity_id == 0 && multi != 0) {
                   out.println("<td align=\"center\">");
                      out.println("<font color=\"#ffffff\" size=\"2\">");
                      out.println("<b>Course</b>");
                      out.println("</font></td>");
                }
                   out.println("<td align=\"center\">");
                      out.println("<font color=\"#ffffff\" size=\"2\"><b>");
                      out.println("<a href='?sortBy=gender" + ((sortBy.equals("gender") && !sortDesc) ? "&desc" : "") + ((showOld) ? "&showOld" : "") + "' style='color:white'>Gender</a>");
                      out.println("</b></font></td>");

                   out.println("<td align=\"center\">");
                      out.println("<font color=\"#ffffff\" size=\"2\"><b>");
                      out.println("<a href='?sortBy=date" + ((sortBy.equals("date") && !sortDesc) ? "&desc" : "") + ((showOld) ? "&showOld" : "") + "' style='color:white'>Date/Time</a>");
                      out.println("</b></font></td>");

                   if (!club.equals( "oldoaks" )) {
                      out.println("<td align=\"center\">");
                         out.println("<font color=\"#ffffff\" size=\"2\">");
                         out.println("<b>Sign Up Starts</b>");
                         out.println("</font></td>");
                   }
                   out.println("<td align=\"center\">");
                      out.println("<font color=\"#ffffff\" size=\"2\">");
                      out.println("<b>Sign Up Ends</b>");
                      out.println("</font></td></tr>");

                  doHeader = false;
            }
      
            name = rs.getString("name");
            edate = rs.getLong("date");
            yy = rs.getInt("year");
            mm = rs.getInt("month");
            dd = rs.getInt("day");
            ecolor = rs.getString("color");
            shr = rs.getInt("act_hr");
            smin = rs.getInt("act_min");
            course = rs.getString("courseName");
            signUp = rs.getInt("signUp");
            c_month = rs.getInt("c_month");
            c_day = rs.getInt("c_day");
            c_year = rs.getInt("c_year");
            c_hr = rs.getInt("c_hr");
            c_min = rs.getInt("c_min");
            c_date = rs.getLong("c_date");
            c_time = rs.getInt("c_time");
            su_month = rs.getInt("su_month");
            su_day = rs.getInt("su_day");
            su_year = rs.getInt("su_year");
            su_hr = rs.getInt("su_hr");
            su_min = rs.getInt("su_min");
            su_date = rs.getLong("su_date");
            su_time = rs.getInt("su_time");
            gender = rs.getInt("gender");
            season = rs.getInt("season");
            
            skipEvent = false;                // init
            
            //
            //   If Stonebridge Ranch CC - course selection based on mship type
            //
            if (club.equals( "stonebridgeranchcc" ) && !mship.equals("Dual")) {
            
               if (mship.equals("Dye") && !course.equals("Dye")) {
            
                  skipEvent = true;
               }
               if (mship.equals("Hills") && course.equals("Dye")) {
            
                  skipEvent = true;
               }
            }
            
            
            if (skipEvent == false) {          // if ok to list this event

               sampm = " AM";            // do start time
               if (shr == 12) {
                  sampm = " PM";
               }
               if (shr > 12) {
                  sampm = " PM";
                  shr = shr - 12;    // convert to conventional time
               }

               c_ampm = " AM";           // do sign-up end time (cut-off time)
               if (c_hr == 12) {
                  c_ampm = " PM";
               }
               if (c_hr > 12) {
                  c_ampm = " PM";
                  c_hr = c_hr - 12;    // convert to conventional time
               }

               su_ampm = " AM";           // do sign-up start time
               if (su_hr == 12) {
                  su_ampm = " PM";
               }
               if (su_hr > 12) {
                  su_ampm = " PM";
                  su_hr = su_hr - 12;    // convert to conventional time
               }


               highlight = "#F5F5DC";         // default background color

               if (signUp != 0 && date >= su_date && date <= c_date) {     // if within signup period, highlight the sign up info 

                  highlight = "#86B686";        // use highlight background color
               }


               //
               //  Build the HTML for each record found
               //
               out.println("<form action=\"/" +rev+ "/servlet/Member_events2\" method=\"post\" target=\"bot\">");

               out.println("<tr><td align=\"center\">");
                  out.println("<font size=\"2\">");
                  //
                  //   if signup is yes, then allow member to select it
                  //
                  //if (signUp != 0) {       // if members can sign up

                     out.println("<p>");
                     out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                     out.println("<input type=\"submit\" value=\"" + name + "\" style=\"background:" +ecolor+ "\">");
                     out.println("</p>");
/*
                  } else {

                     out.println("<p>" + name + "</p>");
                  }*/
                  out.println("</font></td>");

               if (sess_activity_id == 0 && multi != 0) {

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");

                     if (club.equals("congressional")) {
                         out.println(congressionalCustom.getFullCourseName(edate, dd, course));
                     } else {
                         out.println(course);
                     }

                     out.println("</font></td>");
               }

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println(Labels.gender_opts[gender]);
               out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  if (season == 0) {
                      out.println(mm + "/" + dd + "/" + yy + " at ");
                      out.println(shr + ":" + SystemUtils.ensureDoubleDigit(smin) + sampm);
                  } else {
                      out.println("Season Long");
                  }
               out.println("</font></td>");

               if (!club.equals( "oldoaks" )) {
                  out.println("<td bgcolor=\"" +highlight+ "\" align=\"center\">");
                     out.println("<font size=\"2\">");
                     if (signUp != 0) {       // if members can sign up
                        out.println(su_month + "/" + su_day + "/" + su_year + " at ");
                        out.println(su_hr + ":" + SystemUtils.ensureDoubleDigit(su_min) + su_ampm);
                     } else {
                        out.println("N/A");
                     }
                  out.println("</font></td>");
               }

               out.println("<td bgcolor=\"" +highlight+ "\" align=\"center\">");
                  out.println("<font size=\"2\">");
                  if (signUp != 0) {       // if members can sign up
                     out.println(c_month + "/" + c_day + "/" + c_year + " at");
                     out.println(" " + c_hr + ":" + SystemUtils.ensureDoubleDigit(c_min) + c_ampm);
                  } else {
                     out.println("N/A");
                  }
               out.println("</font></td>");
             out.println("</tr></form>");
         }

      }    // end of while

      if (doHeader) {                                   // no records found

         out.println("<br><br><br><br>");
         out.println("<p align=\"center\">There are no events scheduled at this time.</p>");
      }

      stmt.close();

      out.println("</font></td></tr>");
      out.println("</table>");                   // end of main page table

      out.println("<br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;\">");
      out.println("</form></font>");

      //
      //  End of HTML page
      //
      out.println("</center></font></body></html>");
      out.close();

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY bgcolor=\"#ccccaa\">");
      out.println("<CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your golf shop (provide this information).");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
   }

 }   // end of doGet


 //
 //******************************************************************************
 //
 //  doPost processing - is done by Member_events2 (so Member_jump will work)
 //
 //******************************************************************************

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


      doGet(req, resp);          // call doGet processing

 }   // end of doPost

}
