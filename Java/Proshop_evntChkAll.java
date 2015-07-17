/***************************************************************************************
 *   Proshop_evntChkAll:  This servlet will create tee time entries in teecurr for all
 *                        players registered for the named event.
 *
 *
 *   called by:  Proshop_events2
 *               self - after confirmation
 *
 *
 *   created: 2/18/2003   Bob P.
 *
 *   last updated:
 *
 *       12/17/09   Added support for moving activity event sign-ups to the time sheets
 *       12/08/09   Do not delete events or event signups - mark them inactive instead so we can easily restore them.
 *        7/22/08   When inserting guest times on tee sheet only fill 4 player positions in
 *                  case club only allows 4-somes (St. Clair CC asked for this).
 *       12/12/07   Fixed potential bug with add9/18 methods - do not process individual 
 *                  signups if already moved via drag-n-drop feature
 *        9/13/06   If course = -ALL- then use the 1st course in list for tee times.
 *       12/01/05   RDP - Add a 'Move To Tee Sheet w/o Check In' option.
 *        9/16/04   V5 - change getClub from SystemUtils to common.
 *        2/16/04   Version 4.  Add POS processing.
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
import com.foretees.common.parmTee;
import com.foretees.common.parmClub;
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.parmPOS;
import com.foretees.common.getClub;
import com.foretees.common.Utilities;



public class Proshop_evntChkAll extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

//********************************************************************************
//
//  doGet - call doPost processing
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
 //  doPost processing - gets control from Proshop_events2
 //
 //******************************************************************************

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmtx = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   //String user = (String)session.getAttribute("user");      // get this user's username

   int sess_activity_id = (Integer)session.getAttribute("activity_id");


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
      out.close();
      return;
   }

   String name = "";
   String course = "";
   String player = "";
   String color = "";
   String sfb = "";
   String mempos = "";
   String gstpos = "";
   String locations = "";

   int month  = 0;
   int day = 0;
   int year = 0;
   int holes = 0;
   int act_hr = 0;
   int act_min = 0;
   int etype = 0;
   int etime = 0;
   int etime2 = 0;
   int error = 0;
   int fb = 0;
   int activity_id = 0;
   int event_id = 0;

   long date = 0;
   long edate = 0;

   boolean noCheck = false;

   //
   //   Get the parms received
   //
   name = req.getParameter("name");
   course = req.getParameter("course");

   if (req.getParameter("nocheck") != null) {       // if Move w/o Checkin

      noCheck = true;
   }

   //
   //  parm block to hold the tee time parameters
   //
   parmTee parm = new parmTee();          // allocate a parm block

   //
   //  parm block to hold the POS parameters
   //
   parmPOS parmp = new parmPOS();          // allocate a parm block for POS parms

   //
   //  parm block to hold the club parameters
   //
   parmClub parmc = new parmClub(sess_activity_id, con);


   //
   //     Get the Guest parms specified by proshop
   //
   try {

      getClub.getParms(con, parmc, sess_activity_id);        // get the club parms

   } catch (Exception ignore) {}

   //
   //  First, make sure this hasn't already been done or that its not too early
   //
   //   Get current date
   //
   try {

      Calendar cal = new GregorianCalendar();       // get current date and time
      year = cal.get(Calendar.YEAR);
      month = cal.get(Calendar.MONTH);
      day = cal.get(Calendar.DAY_OF_MONTH);

      month++;                                      // month starts at zero

      date = year * 10000;                          // convert to yyyymmdd value
      month = month * 100;
      date = date + month + day;

      //
      //   get the event requested
      //
      PreparedStatement stmt = con.prepareStatement (
         "SELECT event_id, activity_id, locations, date, year, month, day, color, type, act_hr, act_min, holes, fb, mempos, gstpos " +
         "FROM events2b " +
         "WHERE name = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);
      rs = stmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         event_id = rs.getInt("event_id");
         activity_id = rs.getInt("activity_id");
         locations = rs.getString("locations");
         edate = rs.getLong("date");
         year = rs.getInt("year");
         month = rs.getInt("month");
         day = rs.getInt("day");
         color = rs.getString("color");
         etype = rs.getInt("type");
         act_hr = rs.getInt("act_hr");
         act_min = rs.getInt("act_min");
         holes = rs.getInt("holes");
         sfb = rs.getString("fb");
         mempos = rs.getString("mempos");
         gstpos = rs.getString("gstpos");

      } else {          // event not found

         out.println(SystemUtils.HeadTitle("Proshop Event Check All Page"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
         out.println("<table border=\"0\" valign=\"top\">");       // table for main page
         out.println("<tr><td align=\"center\" valign=\"top\">");
         out.println("<font size=\"2\">");
         out.println("<BR><BR><H3>Event Check All Error</H3>");
         out.println("<BR><BR>An error has occurred trying to process your request for");
         out.println("<BR>event: " +name+ ".");
         out.println("<BR><BR>Please notify ForeTees support and provide this message.");
         out.println("<BR><BR>");
         out.println("</font></td></tr>");
         out.println("</table>");                   // end of main page table
         out.println("<br><font size=\"2\">");
         out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
         out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</center></font></body></html>");
         out.close();
         return;
      }   // end of IF event

      stmt.close();

      etime = (act_hr * 100) + act_min;       // create time value (of event)

      etime2 = etime;
        
      //
      //  add 5 minutes to the time to create a time range
      //
      if (act_min < 55) {

         etime2 += 5;

      } else {

         if (act_min == 55) {

            etime2 += 100;

         } else {

            if (act_min == 56) {

               etime2 += 101;

            } else {

               if (act_min == 57) {

                  etime2 += 102;

               } else {

                  if (act_min == 58) {

                     etime2 += 103;

                  } else {

                     etime2 += 104;
                  }
               }
            }
         }
      }


      //
      //  check the event date against today's date (must be today or later)
      //
      if (edate > date && noCheck == false) {       // if too early to check in

         out.println(SystemUtils.HeadTitle("Proshop Event Check All Page"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

         out.println("<table border=\"0\" valign=\"top\">");       // table for main page
         out.println("<tr><td align=\"center\" valign=\"top\">");
         out.println("<font size=\"2\">");
         out.println("<BR><BR><H3>Event Check All Error</H3>");

         if (req.getParameter("gstOnly") == null) {

            out.println("<BR><BR>You are requesting that all players be checked in for an event");
         } else {
            out.println("<BR><BR>You are requesting to check in guests for an event");
         }
         out.println("<BR>that has yet to occur.  You must wait until at least the day of the event.");
         out.println("<BR><BR>");
         out.println("</font></td></tr>");
         out.println("</table>");                   // end of main page table
         out.println("<br><font size=\"2\">");
         out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
         out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</center></font></body></html>");
         out.close();
         return;
      }

      //
      //  prepare F/B indicator
      //
      fb = 0;                       // init to Front

      if (sfb.equals( "Back" )) {

         fb = 1;     // back
      }

      //
      //  if this is a Guest Only request, then go process it
      //
      if (req.getParameter("gstOnly") != null) {

         gstOnly(req, out, con, name, course);   // go process it
         return;
      }

      //
      //  Now see if there are any entries in teecurr or teepast for this date and time
      //
      if (activity_id == 0) {

          if (date <= edate) {    // if day of event or earlier

             PreparedStatement pstmt = con.prepareStatement (
                "SELECT player1 " +
                "FROM teecurr2 WHERE date = ? AND time >= ? AND time <= ? AND fb = ? and courseName = ?");

             pstmt.clearParameters();        // clear the parms
             pstmt.setLong(1, edate);         // put the parm in pstmt
             pstmt.setLong(2, etime);
             pstmt.setLong(3, etime2);
             pstmt.setInt(4, fb);
             pstmt.setString(5, course);
             rs = pstmt.executeQuery();      // execute the prepared stmt

             while (rs.next()) {

                player = rs.getString(1);

                if (!player.equals( "" )) {

                   error = 1;          // indicate error occurred
                }
             }

             pstmt.close();

          } else {           // prior to today - use teepast

             PreparedStatement pstmt = con.prepareStatement (
                "SELECT player1 " +
                "FROM teepast2 WHERE date = ? AND time >= ? AND time <= ? AND fb = ? and courseName = ?");

             pstmt.clearParameters();        // clear the parms
             pstmt.setLong(1, edate);         // put the parm in pstmt
             pstmt.setLong(2, etime);
             pstmt.setLong(3, etime2);
             pstmt.setInt(4, fb);
             pstmt.setString(5, course);
             rs = pstmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

                player = rs.getString(1);

                if (!player.equals( "" )) {

                   error = 1;          // indicate error occurred
                }
             }

             pstmt.close();
          }

      } else {

         // this event is for an activity - check the starting time in the selected locations to see if any players exist
         PreparedStatement pstmt = con.prepareStatement (
            "SELECT COUNT(t1.player_name) " +
            "FROM activity_sheets_players t1, activity_sheets t2 " +
            "WHERE " +
                "t1.activity_sheet_id = t2.sheet_id AND " +
                "t1.player_name <> '' AND " +
                "DATE_FORMAT(t2.date_time, '%Y%m%d') = ? AND " +
                "DATE_FORMAT(t2.date_time, '%k%i') >= ? AND " +
                "DATE_FORMAT(t2.date_time, '%k%i') <= ? AND " +
                "t2.activity_id IN (" + locations + ")");

         pstmt.clearParameters();
         pstmt.setLong(1, edate);
         pstmt.setLong(2, etime);
         pstmt.setLong(3, etime2);
         rs = pstmt.executeQuery();

         if (rs.next()) error = (rs.getInt(1) > 0) ? 1 : 0;

         pstmt.close();

      } // end if golf or activity

   } catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact support (provide this information).");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   if (error != 0) {    // if error found above

      out.println(SystemUtils.HeadTitle("Proshop Event Check All Page"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\" valign=\"top\">");
      out.println("<font size=\"2\">");
      out.println("<BR><BR><H3>Event Move Players Error</H3>");
      out.println("<BR><BR>At least one tee time already exists on the date of this event and");
      out.println("<BR>at the time that the event started.  Its likely that you have already");
      out.println("<BR>moved some players/guests for this event.");
      out.println("<BR><BR>Check the tee sheet for the date of this event.");
      out.println("<BR>If the tee time at " + act_hr + ":" + Utilities.ensureDoubleDigit(act_min) + " is not an event related tee time, and");
      out.println("<BR>you have determined that this event has not already been processed, then");
      out.println("<BR>move the conflicting tee time (change the time) and try this again.");
      out.println("<BR><BR>");
      out.println("</font></td></tr>");
      out.println("</table>");                   // end of main page table
      out.println("<br><font size=\"2\">");
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</center></font></body></html>");
      out.close();
      return;
   }

   //
   //  Ok so far -
   //  If first time here, display an explanation and make sure user wants to do this.
   //
   if (req.getParameter("continue") == null) {

      //
      //   build the HTML page for the display
      //
      out.println(SystemUtils.HeadTitle("Proshop Event Check All Page"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" valign=\"top\" width=\"500\">");       // table for main page
      out.println("<tr><td align=\"center\" valign=\"top\">");
      out.println("<font size=\"2\">");
      out.println("<BR><BR><H3>Event Move Players Confirmation</H3>");
      out.println("<BR><BR>You have requested that all players currently signed up for");
      if (noCheck == false) {       // if checking players in
         out.println("<BR>this event be moved and checked in.  This will create a " + ((activity_id == 0) ? "tee time" : "reservation") + " entry");
      } else {
         out.println("<BR>this event be moved to the tee sheet.  This will create a " + ((activity_id == 0) ? "tee time" : "reservation") + " entry");
      }
      out.println("<BR>for each team with a date and start time of this event.");
      if (noCheck == false) {       // if checking players in
         out.println("<BR><BR>The purpose of this is so that all rounds played during this");
         out.println("<BR>event are counted and included in the reports.");
      }
      out.println("<BR>Players on the wait list will not be included.");
        
      if (parmc.posType.equals( "Pro-ShopKeeper" ) && noCheck == false) {
         out.println("<BR><BR><b>NOTE:</b> POS charges, if any, will be accumulated for the members in this event.");
         if (holes > 18) {
            out.println("<BR>The charges will be based on <b>one 18-hole round</b>.");
         }
         out.println("<BR>You will be prompted again after the players are checked in, but before the");
         out.println("<BR>POS charges are processed.  You will be allowed to block the POS charges.");
         out.println("<br><BR><b>This must be done from the browser window within the Pro-ShopKeeper program.</b>");
      }

      if (course.equals( "-ALL-" )) {
         out.println("<BR><BR><b>NOTE:</b> The event was configured to use ALL courses.");
         out.println("<BR>The players will all be moved to the first course located.");
         out.println("<BR>You can then use the Edit Tee Sheet feature to move them as desired.");
      }
      out.println("<BR><BR>Are you sure you want to continue?");
      out.println("<BR><BR>");
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_evntChkAll\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      if (noCheck == true) {       // if NOT checking players in
         out.println("<input type=\"hidden\" name=\"nocheck\" value=\"yes\">");
      }
      out.println("<input type=\"hidden\" name=\"continue\" value=\"continue\">");
      out.println("<input type=\"submit\" value=\"YES\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
      out.println("</font></td></tr>");
      out.println("</table>");                   // end of main page table

      out.println("<br><font size=\"2\">");
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"submit\" value=\"No - Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</center></font></body></html>");
      out.close();
      return;        // wait for response
   }

   if (activity_id == 0) {

       //
       //  User wants to continue, date and teecurr are ok - add tee times to teecurr or teepast
       //
       //   set the tee time values in parmTee
       //
       parm.event = name;
       parm.courseName = course;
       parm.date = date;
       parm.edate = edate;
       parm.event_color = color;
       parm.fb = fb;
       parm.event_type = etype;
       parm.time = etime;           // start with event's actual start time
       
       //
       //  Change course name if ALL
       //
       if (course.equals( "-ALL-" )) {          // if event is for all courses, change to first in list

          try {
             stmtx = con.createStatement();        // create a statement

             rs = stmtx.executeQuery("SELECT courseName " +
                                     "FROM clubparm2 WHERE first_hr != 0");

             if (rs.next()) {

                parm.courseName = rs.getString(1);
             }
             stmtx.close();

          }
          catch (Exception e) {
          }

       }

       //
       //  Add 1 minute to the time (prevent duplicate tee times)
       //
       addTime(parm, con);


       if (noCheck == true) {       // if NOT checking players in
          parm.show1 = 0;
          parm.show2 = 0;
          parm.show3 = 0;
          parm.show4 = 0;
          parm.show5 = 0;
       } else {
          parm.show1 = 1;
          parm.show2 = 1;
          parm.show3 = 1;
          parm.show4 = 1;
          parm.show5 = 1;
       }

       //
       //  Get the POS System Parameters for this Club & Course
       //
       try {
           
          getClub.getPOS(con, parmp, parm.courseName);

       } catch (Exception ignore) { }

       
       //
       // Create date and time string
       //
       String sdate = month + "/" + day + "/" + year + " " + act_hr + ":" + Utilities.ensureDoubleDigit(act_min);  
       

       parmp.count = 0;
       parmp.sdate = sdate;
       parmp.poslist = "";
       parmp.mempos = mempos;
       parmp.gstpos = gstpos;

       boolean doPOS = false;
       boolean pskeeper = false;

       if (parmp.posType.equals( "Pro-ShopKeeper" )) {

          pskeeper = true;         // gather charges
       }

       //
       //   Create the tee times for this event
       //
       error = 0;      // init error flag

       if (holes < 9) {

          holes = 18;         // just in case !!
       }

       if (holes == 9) {

          doPOS = pskeeper;
          error = add9(con, parm, parmp, parmc, doPOS);   // add 9 hole tee time entries

       } else if (holes == 18) {

          doPOS = pskeeper;
          error = add18(con, parm, parmp, parmc, doPOS);   // add 18 hole tee time entries

       } else if (holes == 27) {

          doPOS = pskeeper;
          error = add18(con, parm, parmp, parmc, doPOS);   // add 18 hole tee time entries

          doPOS = false;

          error = add9(con, parm, parmp, parmc, doPOS);   // add 9 hole tee time entries

       } else if (holes == 36) {

          doPOS = pskeeper;
          error = add18(con, parm, parmp, parmc, doPOS);   // add 18 hole tee time entries

          doPOS = false;

          error = add18(con, parm, parmp, parmc, doPOS);   // add 18 hole tee time entries

       }

       if (holes == 45) {

          doPOS = pskeeper;
          error = add18(con, parm, parmp, parmc, doPOS);   // add 18 hole tee time entries

          doPOS = false;

          error = add18(con, parm, parmp, parmc, doPOS);   // add 18 hole tee time entries

          error = add9(con, parm, parmp, parmc, doPOS);   // add 9 hole tee time entries

       } else if (holes > 45) {

          doPOS = pskeeper;
          error = add18(con, parm, parmp, parmc, doPOS);   // add 18 hole tee time entries

          doPOS = false;

          error = add18(con, parm, parmp, parmc, doPOS);   // add 18 hole tee time entries

          error = add18(con, parm, parmp, parmc, doPOS);   // add 18 hole tee time entries

       }

       if (error != 0) {    // if error found above

          out.println(SystemUtils.HeadTitle("Proshop Event Check All Page"));
          out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
          out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

          out.println("<table border=\"0\" valign=\"top\">");       // table for main page
          out.println("<tr><td align=\"center\" valign=\"top\">");
          out.println("<font size=\"2\">");
          out.println("<BR><BR><H3>Event Move Players Error</H3>");
          out.println("<BR><BR>An error occurred while building the tee time entries.");
          out.println("<BR>As a result your reports may not be accurate.");
          out.println("<BR><BR>Check the tee sheet for the day of the event to see if the tee time entries exist.");
          out.println("<BR><BR>");
          out.println("</font></td></tr>");
          out.println("</table>");                   // end of main page table
          out.println("<br><font size=\"2\">");
          out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
          out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
          out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
          out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
          out.println("</form></font>");
          out.println("</center></font></body></html>");
          out.close();
          return;
       }

       //
       //  Check All complete - inform user and return to events
       //
       if (parmp.count == 0 || noCheck == true) {        // if no POS charges found or Not checked in

          out.println(SystemUtils.HeadTitle("Proshop Event Check All Page"));
          out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
          out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
          out.println("<table border=\"0\" valign=\"top\">");       // table for main page
          out.println("<tr><td align=\"center\" valign=\"top\">");
          out.println("<font size=\"2\">");
          out.println("<BR><BR><H3>Event Move Players Complete</H3>");
          if (noCheck == false) {       // if checking players in
             out.println("<BR><BR>Rounds for all players registered for the event have been recorded.");
             out.println("<BR>These rounds will now be included in the reports.");
          } else {
             out.println("<BR><BR>All players registered for the event have been moved to the Tee Sheet.");
          }
          out.println("<BR><BR>It is now safe to delete the Event if desired.");
          out.println("<BR><BR>");
          out.println("</font></td></tr>");
          out.println("</table>");                   // end of main page table
          out.println("<br><font size=\"2\">");
          out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
          out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
          out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
          out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
          out.println("</form></font>");
          out.println("</center></font></body></html>");
          out.close();

       } else {

          String counts = String.valueOf( parmp.count );     // create string value from count

          parmp.poslist = counts + "," + parmp.poslist;   // create full charge string

          parmp.poslist = "ChangeStatus(" + parmp.poslist + ")";  // wrap pos list with command

          out.println("<HTML>");
          out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
          out.println("<Title>Proshop Process POS Page</Title>");
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

          out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
          out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
          out.println("<table border=\"0\" valign=\"top\">");       // table for main page
          out.println("<tr><td align=\"center\" valign=\"top\">");
          out.println("<font size=\"2\">");
          out.println("<BR><BR><H3>Event Check All Complete</H3>");
          out.println("<BR><BR>Rounds for all players registered for the event have been recorded.");
          out.println("<BR>These rounds will now be included in the reports.");
          out.println("<BR><BR>It is now safe to delete the Event if desired.");
          out.println("<BR><BR>");
          out.println("<b>NOTE:</b> Member charges for this event are about to be transferred to the POS system.");
          out.println("<BR><BR>Would you like to proceed with these charges?");
          out.println("<BR>This must be done from the browser window within the Pro-ShopKeeper program.");
          out.println("<BR><BR>");
          out.println("</font></td></tr>");
          out.println("</table>");                   // end of main page table
          out.println("<br><font size=\"2\">");
          out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
          out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
          out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
          out.println("<input type=\"submit\" value=\"No - Return Without Making Charges\" style=\"text-decoration:underline; background:#8B8970\">");
          out.println("</form><br>");
          out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
          out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
          out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
          out.println("<input type=\"submit\" value=\"Yes - Continue\" ONCLICK=\"sendstatus('" +parmp.poslist+ "')\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
          out.println("</form></font>");
          out.println("</center></font></body></html>");
          out.close();
       }

   } else {

       // activity event
       //error = moveSignupsToTimeSheet(name, locations, event_id, con);
       error = moveSignupsToTimeSheet(event_id, name, locations, noCheck, con);


       out.println(SystemUtils.HeadTitle("Proshop Event Check All Page"));
       out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

       if (error == 0) {

           // no error
           
           out.println("<table border=\"0\" align=\"center\">");
           out.println("<tr><td align=\"center\"><font size=\"2\">");
           out.println("<BR><BR><H3>Event Check All Complete</H3>");
           out.println("<BR><BR>Rounds for all players registered for the event have been recorded.");
           out.println("<BR>These rounds will now be included in the reports.");
           out.println("<BR><BR>It is now safe to delete the Event if desired.");
           out.println("<BR><BR>");
           out.println("</font></td></tr>");

           out.println("<tr><td align=\"center\"><font size=\"2\">");
           out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
           out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
           out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
           out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
           out.println("</form>");
           out.println("</font></td></tr>");
           out.println("</table>");

       } else {

           // error occured
           
           out.println("<table border=\"0\" align=\"center\">");
           out.println("<tr><td align=\"center\"><font size=\"2\">");
           out.println("<BR><BR><H3>Event Move Players Error</H3>");
           out.println("<BR><BR>An error occurred while moving the sign-ups to your time sheets.");
           out.println("<BR>As a result your reports may not be accurate.");
           out.println("<BR><BR>Check the time sheet for the day of the event to see if the reservations where moved successfully.");
           out.println("<BR><BR>");
           out.println("</font></td></tr>");
           out.println("</table>");       
           out.println("<br><font size=\"2\">");
           out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
           out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
           out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
           out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
           out.println("</form></font>");

       }

       out.println("</center></font></body></html>");
       out.close();

   }

 }   // end of doPost

 
 //
 // 
 //
 private int moveSignupsToTimeSheet(int event_id, String name, String locations, boolean noCheck, Connection con) {
 

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    int error = 0;
    int count1 = 0;
    int pos = 0;
    int id = 0;
    int show = (noCheck) ? 0 : 1;

    String player1 = "";
    String player2 = "";
    String player3 = "";
    String player4 = "";
    String player5 = "";
    String username1 = "";
    String username2 = "";
    String username3 = "";
    String username4 = "";
    String username5 = "";
    String userg1 = "";
    String userg2 = "";
    String userg3 = "";
    String userg4 = "";
    String userg5 = "";

    boolean found_slot = false;
    boolean error2 = false;
    
    try {

        //
        //  Get all entries ready to be moved for this event
        //
        pstmt = con.prepareStatement (
                    "SELECT * FROM evntsup2b " +
                    "WHERE name = ? AND wait = 0 AND moved = 0 AND inactive = 0 " +
                    "ORDER BY r_date, r_time");
/*
SELECT t1.sheet_id, date_time, activity_id
FROM activity_sheets t1
LEFT OUTER JOIN activity_sheets_players t2 ON t1.sheet_id = t2.activity_sheet_id
WHERE t1.event_id = 231 AND t1.sheet_id NOT IN (
    SELECT sheet_id
    FROM activity_sheets t1
    RIGHT JOIN activity_sheets_players t2 ON t1.sheet_id = t2.activity_sheet_id
    WHERE event_id = 231)
ORDER BY date_time, activity_id
 */
        pstmt.clearParameters();
        pstmt.setString(1, name);
        rs = pstmt.executeQuery();

        while (rs.next()) {

            id = rs.getInt("id");
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            username1 = rs.getString("username1");
            username2 = rs.getString("username2");
            username3 = rs.getString("username3");
            username4 = rs.getString("username4");
            username5 = rs.getString("username5");
            userg1 = rs.getString("userg1");
            userg2 = rs.getString("userg2");
            userg3 = rs.getString("userg3");
            userg4 = rs.getString("userg4");
            userg5 = rs.getString("userg5");

            //
            //  check for an empty entry
            //
            if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) ||
                !player4.equals( "" ) || !player5.equals( "" )) {

                count1++;           // count # of entries with players found
                pos = 0;            // reset
                found_slot = false;

                //
                // Find all the time slots covered by this event and order them by date_time & activity_id
                //
                pstmt2 = con.prepareStatement (
                            "SELECT sheet_id, " +
                                "(SELECT COUNT(*) FROM activity_sheets_players WHERE sheet_id = activity_sheet_id) AS players " +
                            "FROM activity_sheets " +
                            "WHERE event_id = ? " +
                            "ORDER BY date_time, activity_id ASC");

/*

SELECT sheet_id, activity_id, date_time
FROM activity_sheets
RIGHT OUTER JOIN activity_sheets_players ON sheet_id NOT IN (activity_sheet_id)
WHERE event_id = 221
ORDER BY date_time ASC;


SELECT sheet_id, activity_id, date_time, (SELECT COUNT(*) FROM activity_sheets_players WHERE sheet_id = activity_sheet_id) AS players 
FROM activity_sheets
WHERE event_id = 221
ORDER BY date_time, activity_id ASC;


*/
                pstmt2.clearParameters();
                pstmt2.setInt(1, event_id);
                rs2 = pstmt2.executeQuery();

                while ( rs2.next() && !found_slot ) {
                    

                    //
                    // Only populate this time slot if there are no existing players bound to it
                    // If this time slot has players in it skip it and look at the next available time slot
                    //

                    if (rs2.getInt("players") == 0) {
                        
                        found_slot = true;  // set to true so we skip out of rs2 loop
                        error2 = false;

                        if (!player1.equals( "" )) {
                            pos++;
                            error2 = addActivityPlayer(rs2.getInt("sheet_id"), player1, username1, userg1, pos, show, con);
                        }

                        if (!player2.equals( "" ) && !error2) {
                            pos++;
                            error2 = addActivityPlayer(rs2.getInt("sheet_id"), player2, username2, userg2, pos, show, con);
                        }

                        if (!player3.equals( "" ) && !error2) {
                            pos++;
                            error2 = addActivityPlayer(rs2.getInt("sheet_id"), player3, username3, userg3, pos, show, con);
                        }

                        if (!player4.equals( "" ) && !error2) {
                            pos++;
                            error2 = addActivityPlayer(rs2.getInt("sheet_id"), player4, username4, userg4, pos, show, con);
                        }

                        if (!player5.equals( "" ) && !error2) {
                            pos++;
                            error2 = addActivityPlayer(rs2.getInt("sheet_id"), player5, username5, userg5, pos, show, con);
                        }

                        if (error2) {

                            //
                            // We encountered an error moving an individual player (should already be logged from addActivityPlayer method
                            // Perform any clean-up work here - first remove any players from this time slot
                            //

                            PreparedStatement pstmt3 = null;

                            try {

                                pstmt3 = con.prepareStatement ("DELETE FROM activity_sheets_players WHERE activity_sheet_id = ?");
                                pstmt3.clearParameters();
                                pstmt3.setInt(1, rs2.getInt("sheet_id"));
                                pstmt3.executeUpdate();

                            } catch (Exception exc) {

                                Utilities.logError("Error in Proshop_evntChkAll.moveSignupsToTimeSheet: error cleaning up for sheet_id=" + rs2.getInt("sheet_id") + ", Error=" + exc.getMessage());

                            } finally {

                                try { pstmt3.close(); }
                                catch (Exception ignore) {}

                            }

                        } else {

                            //
                            // We successfully moved each player - now lets mark this signup as moved
                            //
                            
                            PreparedStatement pstmt3 = null;

                            try {

                                pstmt3 = con.prepareStatement ("UPDATE evntsup2b SET moved = 1 WHERE id = ?");
                                pstmt3.clearParameters();
                                pstmt3.setInt(1, id);
                                pstmt3.executeUpdate();

                            } catch (Exception exc) {

                                Utilities.logError("Error in Proshop_evntChkAll.moveSignupsToTimeSheet: error marking signup as moved id=" + id + ", Error=" + exc.getMessage());

                            } finally {

                                try { pstmt3.close(); }
                                catch (Exception ignore) {}

                            }

                        } // end if error2

                    } // end if time slot is empty

                } // end while loop of available time slots for this event

            }  // end of IF players in this sign-up

        }  // end of WHILE loop for all sign-ups

    } catch (Exception exc) {

      Utilities.logError("Error in Proshop_evntChkAll.moveSignupsToTimeSheet: event_id=" + event_id + ", Error=" + exc.getMessage());

      error = 1;     // inform caller

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

    return(error);
     
 }
 

 private boolean addActivityPlayer(int slot_id, String player_name, String username, String userg, int pos, int show, Connection con) {

    boolean error = false;

    PreparedStatement pstmt = null;

    try {

        if ( !player_name.equals("") ) {

            pstmt = con.prepareStatement (
                "INSERT INTO activity_sheets_players " +
                    "(activity_sheet_id, username, player_name, userg, pos, `show`) VALUES (?, ?, ?, ?, ?, ?)");

            pstmt.clearParameters();
            pstmt.setInt(1, slot_id);
            pstmt.setString(2, username);
            pstmt.setString(3, player_name);
            pstmt.setString(4, userg);
            pstmt.setInt(5, pos);
            pstmt.setInt(6, show);
            pstmt.executeUpdate();
            pstmt.close();

        }


    } catch (Exception exc) {

        Utilities.logError("Error in Proshop_evntChkAll.addActivityPlayer: slot_id=" + slot_id + ", player_name=" + player_name + ", username=" + username + ", userg=" + userg + ", pos=" + pos + ", Error=" + exc.toString());
        error = true;

    } finally {

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return (error);

 }


 // *********************************************************
 //  Process 9 hole tee time request
 // *********************************************************

 private int add9(Connection con, parmTee parm, parmPOS parmp, parmClub parmc, boolean doPOS) {


   ResultSet rs2 = null;

   int error = 0;
   int wait = 0;
   int moved = 0;

   long year = 0;
   long month = 0;
   long day = 0;


   //
   //  gather some data for the entry
   //
   year = parm.edate / 10000;
   month = (parm.edate - (year * 10000))/100;
   day = parm.edate - ((year * 10000) + (month * 100));

   parm.yy = (int) year;
   parm.mm = (int) month;
   parm.dd = (int) day;

   parm.hr = parm.time / 100;
   parm.min = parm.time - (parm.hr * 100);

   PreparedStatement pstmte = null;

   try {

      //
      //  Get all entries for this Event Sign Up Sheet
      //
      pstmte = con.prepareStatement (
         "SELECT * FROM evntsup2b " +
         "WHERE name = ? AND inactive = 0");

      pstmte.clearParameters();        // clear the parms
      pstmte.setString(1, parm.event);
      rs2 = pstmte.executeQuery();      // execute the prepared pstmt

      while (rs2.next()) {

         parm.player1 = rs2.getString("player1");
         parm.player2 = rs2.getString("player2");
         parm.player3 = rs2.getString("player3");
         parm.player4 = rs2.getString("player4");
         parm.player5 = rs2.getString("player5");
         parm.username1 = rs2.getString("username1");
         parm.username2 = rs2.getString("username2");
         parm.username3 = rs2.getString("username3");
         parm.username4 = rs2.getString("username4");
         parm.username5 = rs2.getString("username5");
         parm.p1cw = rs2.getString("p1cw");
         parm.p2cw = rs2.getString("p2cw");
         parm.p3cw = rs2.getString("p3cw");
         parm.p4cw = rs2.getString("p4cw");
         parm.p5cw = rs2.getString("p5cw");
         parm.hndcp1 = rs2.getFloat("hndcp1");
         parm.hndcp2 = rs2.getFloat("hndcp2");
         parm.hndcp3 = rs2.getFloat("hndcp3");
         parm.hndcp4 = rs2.getFloat("hndcp4");
         parm.hndcp5 = rs2.getFloat("hndcp5");
         wait = rs2.getInt("wait");
         moved = rs2.getInt("moved");
         parm.userg1 = rs2.getString("userg1");
         parm.userg2 = rs2.getString("userg2");
         parm.userg3 = rs2.getString("userg3");
         parm.userg4 = rs2.getString("userg4");
         parm.userg5 = rs2.getString("userg5");

         if (wait == 0 && moved == 0) {     // process entry if not on wait list and not already moved

            //
            //  check for an empty entry
            //
            if (!parm.player1.equals( "" ) || !parm.player2.equals( "" ) || !parm.player3.equals( "" ) ||
                !parm.player4.equals( "" ) || !parm.player5.equals( "" )) {

               //
               //  set the rounds to 9 holes
               //
               parm.p91 = 1;
               parm.p92 = 1;
               parm.p93 = 1;
               parm.p94 = 1;
               parm.p95 = 1;

               //
               //  insert a tee time for this team
               //
               SystemUtils.insertTee2(parm, con);     // insert new tee time
  
               //
               //  Accumulate the POS charges, if requested by caller
               //
               if (doPOS == true) {
                 
                  buildCharge(parmp, parm, parmc, con);
               }

               //
               //  Add 1 minute to the time (prevent duplicate tee times
               //
               addTime(parm, con);

            }  // end of IF players

         }  // end of IF on wait list

      }  // end of WHILE

   } catch (Exception exc) {
       
      SystemUtils.logError("Error in Proshop_evntChkAll add9: Error = " + exc.getMessage());

      error = 1;

   } finally {

      try { rs2.close(); }
      catch (Exception ignore) {}

      try { pstmte.close(); }
      catch (Exception ignore) {}

    }

   return error;
 }


 // *********************************************************
 //  Process 18 hole tee time request
 // *********************************************************

 private int add18(Connection con, parmTee parm, parmPOS parmp, parmClub parmc, boolean doPOS) {


   ResultSet rs2 = null;

   int error = 0;
   int wait = 0;
   int moved = 0;
   int count1 = 0;
   int count2 = 0;

   long year = 0;
   long month = 0;
   long day = 0;


   //
   //  gather some data for the entry
   //
   year = parm.edate / 10000;
   month = (parm.edate - (year * 10000))/100;
   day = parm.edate - ((year * 10000) + (month * 100));

   parm.yy = (int) year;
   parm.mm = (int) month;
   parm.dd = (int) day;

   parm.hr = parm.time / 100;
   parm.min = parm.time - (parm.hr * 100);

   PreparedStatement pstmte = null;

   try {

      //
      //  Get all entries for this Event Sign Up Sheet
      //
      pstmte = con.prepareStatement (
         "SELECT * FROM evntsup2b " +
         "WHERE name = ? AND inactive = 0");

      pstmte.clearParameters();        // clear the parms
      pstmte.setString(1, parm.event);
      rs2 = pstmte.executeQuery();      // execute the prepared pstmt

      while (rs2.next()) {

         parm.player1 = rs2.getString("player1");
         parm.player2 = rs2.getString("player2");
         parm.player3 = rs2.getString("player3");
         parm.player4 = rs2.getString("player4");
         parm.player5 = rs2.getString("player5");
         parm.username1 = rs2.getString("username1");
         parm.username2 = rs2.getString("username2");
         parm.username3 = rs2.getString("username3");
         parm.username4 = rs2.getString("username4");
         parm.username5 = rs2.getString("username5");
         parm.p1cw = rs2.getString("p1cw");
         parm.p2cw = rs2.getString("p2cw");
         parm.p3cw = rs2.getString("p3cw");
         parm.p4cw = rs2.getString("p4cw");
         parm.p5cw = rs2.getString("p5cw");
         parm.hndcp1 = rs2.getFloat("hndcp1");
         parm.hndcp2 = rs2.getFloat("hndcp2");
         parm.hndcp3 = rs2.getFloat("hndcp3");
         parm.hndcp4 = rs2.getFloat("hndcp4");
         parm.hndcp5 = rs2.getFloat("hndcp5");
         wait = rs2.getInt("wait");
         moved = rs2.getInt("moved");
         parm.userg1 = rs2.getString("userg1");
         parm.userg2 = rs2.getString("userg2");
         parm.userg3 = rs2.getString("userg3");
         parm.userg4 = rs2.getString("userg4");
         parm.userg5 = rs2.getString("userg5");

         count1++;            // count # of entries found

         if (wait == 0 && moved == 0) {     // process entry if not on wait list and not already moved

            //
            //  check for an empty entry
            //
            if (!parm.player1.equals( "" ) || !parm.player2.equals( "" ) || !parm.player3.equals( "" ) ||
                !parm.player4.equals( "" ) || !parm.player5.equals( "" )) {

               //
               //  set the rounds to 18 holes
               //
               parm.p91 = 0;
               parm.p92 = 0;
               parm.p93 = 0;
               parm.p94 = 0;
               parm.p95 = 0;

               //
               //  insert a tee time for this team
               //
               SystemUtils.insertTee2(parm, con);     // insert new tee time

               count2++;            // count # of entries moved

               //
               //  Accumulate the POS charges, if requested by caller
               //
               if (doPOS == true) {

                  buildCharge(parmp, parm, parmc, con);
               }

               //
               //  Add 1 minute to the time (prevent duplicate tee times)
               //
               addTime(parm, con);

            }  // end of IF players

         }  // end of IF on wait list

      }  // end of WHILE

   } catch (Exception exc) {

       SystemUtils.logError("Error in Proshop_evntChkAll add18: Error = " + exc.getMessage());

       error = 1;     // inform caller

   } finally {

       try { rs2.close(); }
       catch (Exception ignore) {}

       try { pstmte.close(); }
       catch (Exception ignore) {}

   }

   return(error);
 }


 // ****************************************************************
 //  Process Guest Only request
 //
 //    First time here - display page to prompt for guest info.
 //    Next time - add the requested guests to teecurr or teepast
 //
 // ****************************************************************

 private void gstOnly(HttpServletRequest req, PrintWriter out, Connection con, String name, String course) {


   Statement stmtx = null;

   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   int holes = 0;
   int act_hr = 0;
   int act_min = 0;
   int fb = 0;
   int etime = 0;
   int error = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int gNum = 0;
   int i = 0;

   long date = 0;
   long edate = 0;
   
   String color = "";
   String sfb = "";
   String courseName = "";

   //
   //  parm block to hold the tee time parameters
   //
   parmTee parm = new parmTee();          // allocate a parm block

   //
   //  parm block to hold the club parameters
   //
   parmClub parmc = new parmClub(sess_activity_id, con);

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmt = new parmCourse();          // allocate a parm block


   //
   //  if this is not a Submit request, then display the prompt page
   //
   if (req.getParameter("go") == null) {

      try {
         //
         //     Get the Guest parms specified by proshop
         //
         getClub.getParms(con, parmc, sess_activity_id);        // get the club parms

         getParms.getTmodes(con, parmt, course);        // get the trans mode parms

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
         out.close();
      }

      //
      //   build the HTML page for the display
      //
      out.println(SystemUtils.HeadTitle("Proshop Event Register Guests"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"500\" cellpadding=\"5\" cellspacing=\"3\" valign=\"top\">");
      out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>" + name + "</b>");
      out.println("</font></td></tr>");

      out.println("<form action=\"/" +rev+ "/servlet/Proshop_evntChkAll\" method=\"post\" target=\"bot\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"gstOnly\" value=\"gstOnly\">");
      out.println("<input type=\"hidden\" name=\"go\" value=\"go\">");

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"3\">");
      out.println("<br>Register Guests for Rounds Played");
      out.println("</font><font size=\"2\">");
      out.println("<br><br>To register the guests, indicate the number and type of guests along");
      out.println("<br>with the mode of transportation.  Then click on 'Register Guests' below.");
      out.println("<br>The number of holes specified for this event will be used to record the rounds.<br>");
      out.println("<br><b>Note:</b> You can perform this task multiple times for the same event.");
      out.println("<br>Therefore, if you would like to enter more than one guest type, ");
      out.println("<br>return here for each additional guest type to process.");
      out.println("<br><br><b>Caution:</b> Be careful not to submit the same guests more than once.");
      out.println("<br><br>");

         out.println("Number of Guests: &nbsp;");
         out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" value=\"00\" name=\"guestNum\">");
         out.println("&nbsp;(enter 1 - 999)<br><br>");

         out.println("Guest Type:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"guestType\">");
             for (i = 0; i < parmc.MAX_Guests; i++) {

                if (!parmc.guest[i].equals( "" )) {

                   out.println("<option value=\"" + parmc.guest[i] + "\">" + parmc.guest[i] + "</option>");
                }
             }
           out.println("</select>");
      out.println("<br><br>");
         out.println("Transportation:&nbsp;&nbsp;<select size=\"1\" name=\"gcw\">");
           for (i=0; i<parmt.tmode_limit; i++) {        // get all c/w options
              if (!parmt.tmodea[i].equals( "" )) {
                 out.println("<option value=\"" +parmt.tmodea[i]+ "\">" +parmt.tmode[i]+ "</option>");
              }
           }
        out.println("</select>");
          
      if (course.equals( "-ALL-" )) {          // if event is for all courses, prompt for course name
  
         out.println("<br><br>");
         out.println("Assign Rounds to Course:&nbsp;&nbsp;<select size=\"1\" name=\"course\">");

         try {
            stmtx = con.createStatement();        // create a statement

            rs = stmtx.executeQuery("SELECT courseName " +
                                    "FROM clubparm2 WHERE first_hr != 0");

            while (rs.next()) {

               courseName = rs.getString(1);

               out.println("<option value=\"" +courseName+ "\">" +courseName+ "</option>");
            }
            stmtx.close();
              
         }
         catch (Exception e) {
         }

        out.println("</select>");
  
      } else {
        
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      }
  
      out.println("<br><br>");
      out.println("<input type=\"submit\" value=\"Register Guests\" name=\"check\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("<br>");
      out.println("</font></td></tr>");

      out.println("</table></form>");             // end of table

      out.println("<font size=\"2\">");
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</center></font></body></html>");
      out.close();
      return;

   }             // end of IF NOT 'go'

   //
   //**************************************************
   //  User has submitted a request to add guests
   //**************************************************
   //
   String sgNum = req.getParameter("guestNum");       // get the parms passed (already have name & course)
   String gType = req.getParameter("guestType");
   String gcw = req.getParameter("gcw");

   try {
      gNum = Integer.parseInt(sgNum);           // convert to int
   }
   catch (NumberFormatException e) {
   }

   //
   // verify number of guests to process
   //
   if (gNum < 1 || gNum > 999) {

      out.println(SystemUtils.HeadTitle("Event Input Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<p>&nbsp;</p>");
      out.println("<BR><H3>Input Error</H3><BR>");
      out.println("<BR><BR>Number of Guests is Invalid<BR>");
      out.println("<BR>The number of guests must be in the range of 1 to 999.");
      out.println("<BR>You specified " + sgNum + ".<BR>");
      out.println("<BR>Please try again.<BR>");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</input></form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Get current date and event info
   //
   Calendar cal = new GregorianCalendar();          // get current date and time
   yy = cal.get(Calendar.YEAR);
   mm = cal.get(Calendar.MONTH) + 1;
   dd = cal.get(Calendar.DAY_OF_MONTH);

   date = (yy * 10000) + (mm * 100) + dd;           // convert to yyyymmdd value

   try {

      //
      //   get the event requested
      //
      PreparedStatement stmt = con.prepareStatement (
         "SELECT date, color, act_hr, act_min, holes, fb " +
         "FROM events2b " +
         "WHERE name = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);
      rs = stmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         edate = rs.getLong("date");
         color = rs.getString("color");
         act_hr = rs.getInt("act_hr");
         act_min = rs.getInt("act_min");
         holes = rs.getInt("holes");
         sfb = rs.getString("fb");

      }   // end of IF event

      stmt.close();

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
      out.close();
      return;
   }

   etime = (act_hr * 100) + act_min;       // create time value (of event)

   //
   //  prepare F/B indicator
   //
   fb = 0;                       // init to Front

   if (sfb.equals( "Back" )) {

      fb = 1;     // back
   }

   //
   //   set the tee time values in parmTee
   //
   parm.event = name;
   parm.courseName = course;
   parm.date = date;
   parm.edate = edate;
   parm.event_color = color;
   parm.fb = fb;
   parm.p1cw = gcw;
   parm.p2cw = gcw;
   parm.p3cw = gcw;
   parm.p4cw = gcw;
   parm.p5cw = gcw;
   parm.time = etime;     // get starting time of event

   //
   //  Add 1 minute to the time (prevent duplicate tee times)
   //
   addTime(parm, con);


   //
   //  Create the tee times for the specified number of guests
   //
   error = 0;      // init error flag

   if (holes == 9) {

      parm.p91 = 1;        // 9 holes
      parm.p92 = 1;
      parm.p93 = 1;
      parm.p94 = 1;
      parm.p95 = 1;

      error = addGrnd(con, parm, gType, gNum);

   }

   if (holes == 18) {

      parm.p91 = 0;        // 18 holes
      parm.p92 = 0;     
      parm.p93 = 0;
      parm.p94 = 0;
      parm.p95 = 0;

      error = addGrnd(con, parm, gType, gNum);

   }

   if (holes == 27) {

      parm.p91 = 0;        // 18 holes
      parm.p92 = 0;
      parm.p93 = 0;
      parm.p94 = 0;
      parm.p95 = 0;

      error = addGrnd(con, parm, gType, gNum);

      parm.p91 = 1;        // 9 holes
      parm.p92 = 1;
      parm.p93 = 1;
      parm.p94 = 1;
      parm.p95 = 1;

      error = addGrnd(con, parm, gType, gNum);


   }

   if (holes == 36) {   
                              
      parm.p91 = 0;        // 18 holes
      parm.p92 = 0;
      parm.p93 = 0;
      parm.p94 = 0;
      parm.p95 = 0;

      error = addGrnd(con, parm, gType, gNum);

      error = addGrnd(con, parm, gType, gNum);

   }

   if (holes == 45) {

      parm.p91 = 0;        // 18 holes
      parm.p92 = 0;
      parm.p93 = 0;
      parm.p94 = 0;
      parm.p95 = 0;

      error = addGrnd(con, parm, gType, gNum);

      error = addGrnd(con, parm, gType, gNum);

      parm.p91 = 1;        // 9 holes
      parm.p92 = 1;
      parm.p93 = 1;
      parm.p94 = 1;
      parm.p95 = 1;

      error = addGrnd(con, parm, gType, gNum);

   }

   if (holes > 45) {

      parm.p91 = 0;        // 18 holes
      parm.p92 = 0;
      parm.p93 = 0;
      parm.p94 = 0;
      parm.p95 = 0;

      error = addGrnd(con, parm, gType, gNum);

      error = addGrnd(con, parm, gType, gNum);

      error = addGrnd(con, parm, gType, gNum);

   }

   if (error != 0) {    // if error found above

      out.println(SystemUtils.HeadTitle("Proshop Event Guest Reqister"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\" valign=\"top\">");
      out.println("<font size=\"2\">");
      out.println("<BR><BR><H3>Guest Event Check In Error</H3>");
      out.println("<BR><BR>An error occurred while building the tee time entries.");
      out.println("<BR>As a result your reports may not be accurate.");
      out.println("<BR><BR>Check the tee sheet for the day of the event to see if the tee time entries exist.");
      out.println("<BR><BR>");
      out.println("</font></td></tr>");
      out.println("</table>");                   // end of main page table
      out.println("<br><font size=\"2\">");
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</center></font></body></html>");
      out.close();
      return;
   }

   //
   //  Check All complete - inform user and return to events
   //
   out.println(SystemUtils.HeadTitle("Proshop Guest Event Registration"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" valign=\"top\">");       // table for main page
   out.println("<tr><td align=\"center\" valign=\"top\">");
   out.println("<font size=\"2\">");
   out.println("<BR><BR><H3>Guest Event Check All Complete</H3>");
   out.println("<BR><BR>Rounds for the guests requested have been recorded.");
   out.println("<BR>These rounds will now be included in the reports.");
   out.println("<BR><BR>When finished, it will be safe to delete the Event if desired.");
   out.println("<BR><BR>");
   out.println("</font></td></tr>");
   out.println("</table>");                   // end of main page table
   out.println("<br><font size=\"2\">");
   out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_events2\">");
   out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center></font></body></html>");
   out.close();

 }


 // *********************************************************
 //  Add Guest rounds to teecurr or teepast
 // *********************************************************

 private int addGrnd(Connection con, parmTee parm, String gType, int gNum) {


   int error = 0;

   long temp = 0;
   long year = 0;
   long month = 0;
   long day = 0;

   //
   //  gather some data for the entry
   //
   year = parm.edate / 10000;
   temp = year * 10000;
   month = parm.edate - temp;
   temp = month / 100;
   temp = temp * 100;
   day = month - temp;
   month = month / 100;

   parm.yy = (int) year;
   parm.mm = (int) month;
   parm.dd = (int) day;

   parm.hr = parm.time / 100;
   parm.min = parm.time - (parm.hr * 100);

   parm.player1 = gType;        // set player names and trans modes
   parm.player2 = gType;
   parm.player3 = gType;
   parm.player4 = gType;
   parm.player5 = "";
//   parm.player5 = gType;
   parm.username1 = "";         // init the rest
   parm.username2 = "";       
   parm.username3 = "";
   parm.username4 = "";
   parm.username5 = "";
   parm.hndcp1 = 0;
   parm.hndcp2 = 0;
   parm.hndcp3 = 0;
   parm.hndcp4 = 0;
   parm.hndcp5 = 0;
   parm.userg1 = "";
   parm.userg2 = "";
   parm.userg3 = "";
   parm.userg4 = "";
   parm.userg5 = "";
   parm.show1 = 1;
   parm.show2 = 1;
   parm.show3 = 1;
   parm.show4 = 1;
   parm.show5 = 0;
//   parm.show5 = 1;


   try {

      while (gNum > 0) {        // do 4 at a time (was 5 prior to 7/22/08)

     //    if (gNum < 5) {

     //       parm.player5 = "";
     //    }
         if (gNum < 4) {

            parm.player4 = "";
         }
         if (gNum < 3) {

            parm.player3 = "";
         }
         if (gNum < 2) {

            parm.player2 = "";
         }
         if (gNum < 5) {       // was 6

            gNum = 0;          // done

         } else {

            gNum = gNum - 4;      // decrement number of guests (was 5)
         }

         //
         //  insert a tee time for this team
         //
         SystemUtils.insertTee2(parm, con);     // insert new tee time

         //
         //  Add 1 minute to the time (prevent duplicate tee times)
         //
         addTime(parm, con);

      }     // end of WHILE gNum

   } catch (Exception ignore) {

      error = 1;     // inform caller
   }

   return error;
 }

 // ********************************************************************
 //  Process the POS charges for an individual member
 //
 //    Check for mode of trans, guest, and event charges
 // ********************************************************************

 public void buildCharge(parmPOS parmp, parmTee parmt, parmClub parm, Connection con) {


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

   int i = 0;
   int guest = 0;

   //
   //  Get the player data from the event group passed
   //
   player1 = parmt.player1;
   player2 = parmt.player2;
   player3 = parmt.player3;
   player4 = parmt.player4;
   player5 = parmt.player5;
   user1 = parmt.username1;
   user2 = parmt.username2;
   user3 = parmt.username3;
   user4 = parmt.username4;
   user5 = parmt.username5;
   p1cw = parmt.p1cw;
   p2cw = parmt.p2cw;
   p3cw = parmt.p3cw;
   p4cw = parmt.p4cw;
   p5cw = parmt.p5cw;
   userg1 = parmt.userg1;
   userg2 = parmt.userg2;
   userg3 = parmt.userg3;
   userg4 = parmt.userg4;
   userg5 = parmt.userg5;

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
      parmp.p9 = parmt.p91;

      if (guest == 0) {        // if member

         if (!user1.equals( "" )) {      // skip if no user name found or already processed

            parmp.player = "";   // indicate member
            parmp.user = user1;

            build1Charge(parmp, con);
         }

      } else {          // else guest

         if (!userg1.equals( "" )) {      // skip if no member associated with this guest

            parmp.player = player1;   // indicate guest - pass the guest type
            parmp.user = userg1;

            build1Charge(parmp, con);
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
      parmp.p9 = parmt.p92;

      if (guest == 0) {        // if member

         if (!user2.equals( "" )) {      // skip if no user name found

            parmp.player = "";   // indicate member
            parmp.user = user2;

            build1Charge(parmp, con);
         }

      } else {          // else guest

         if (!userg2.equals( "" )) {      // skip if no member associated with this guest

            parmp.player = player2;   // indicate guest - pass the guest type
            parmp.user = userg2;

            build1Charge(parmp, con);
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
      parmp.p9 = parmt.p93;

      if (guest == 0) {        // if member

         if (!user3.equals( "" )) {      // skip if no user name found

            parmp.player = "";   // indicate member
            parmp.user = user3;

            build1Charge(parmp, con);
         }

      } else {          // else guest

         if (!userg3.equals( "" )) {      // skip if no member associated with this guest

            parmp.player = player3;   // indicate guest - pass the guest type
            parmp.user = userg3;

            build1Charge(parmp, con);
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
      parmp.p9 = parmt.p94;

      if (guest == 0) {        // if member

         if (!user4.equals( "" )) {      // skip if no user name found

            parmp.player = "";   // indicate member
            parmp.user = user4;

            build1Charge(parmp, con);
         }

      } else {          // else guest

         if (!userg4.equals( "" )) {      // skip if no member associated with this guest

            parmp.player = player4;   // indicate guest - pass the guest type
            parmp.user = userg4;

            build1Charge(parmp, con);
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
      parmp.p9 = parmt.p95;

      if (guest == 0) {        // if member

         if (!user5.equals( "" )) {      // skip if no user name found

            parmp.player = "";   // indicate member
            parmp.user = user5;

            build1Charge(parmp, con);
         }

      } else {          // else guest

         if (!userg5.equals( "" )) {      // skip if no member associated with this guest

            parmp.player = player5;   // indicate guest - pass the guest type
            parmp.user = userg5;

            build1Charge(parmp, con);
         }
      }   // end of IF member or guest
   }      // end of IF player not X and not null

 }   // end of buildCharge


 // ********************************************************************
 //  Process the POS charges for an individual member
 //
 //    Check for mode of trans, guest, and event charges
 // ********************************************************************

 public void build1Charge(parmPOS parmp, Connection con) {


   ResultSet rs = null;

   String fname = "";
   String lname = "";
   String mship = "";
   String posid = "";
   String tpos = "";
   String mpos = "";
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
      //  get the mship charge code, if any
      //
      i = 0;
      loop2:
      while (i < parmp.MAX_Mships) {

         if (parmp.mship[i].equalsIgnoreCase( mship )) {     // if matching mode mship type

            mpos = parmp.mpos[i];               // get mship charge code
            break loop2;
         }
         i++;
      }

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

         parmp.poslist = parmp.poslist + posid + ",";           // player id
         parmp.poslist = parmp.poslist + parmp.sdate + ",";     // date and time of tee time
         parmp.poslist = parmp.poslist + p9c + ",";             // 9 or 18 holes
         parmp.poslist = parmp.poslist + lname + ",";          // last name
         parmp.poslist = parmp.poslist + fname + ",,,,";       // first name - skip zip, phone, email
         parmp.poslist = parmp.poslist + mpos + ",";           // mship code
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

               gpos = parmp.gstpos;              // get guest fee for this event
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
            parmp.poslist = parmp.poslist + mpos + ",";           // mship code
            parmp.poslist = parmp.poslist + gpos;                 // charge code for guest
         }

      } else {     // member

         //
         //  Must charge member for Event, if charge code specified in event setup
         //
         if (!parmp.mempos.equals( "" )) {          // if pos charge specified

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
            parmp.poslist = parmp.poslist + mpos + ",";           // mship code
            parmp.poslist = parmp.poslist + parmp.mempos;         // charge code for guest
         }
      }     // end of guest processing

   } catch (Exception ignore) { }

   return;

 }                   // end of build1Charge


 // ********************************************************************
 //   Add one minute to tee time 'time' value
 // ********************************************************************

 public void addTime(parmTee parm, Connection con) {


   boolean stop = false;
     
   PreparedStatement pstmt = null;
   ResultSet rs = null;
  
   while (stop == false) {
     
      parm.hr = parm.time / 100;
      parm.min = parm.time - (parm.hr * 100);

      if (parm.min > 58) {      // if 59 (or error)

         parm.min = 0;
         parm.hr++;

      } else {

         parm.min++;
      }

      parm.time = (parm.hr * 100) + parm.min;

      //
      //  Now see if there are any entries in teecurr for this date and time (teepast is ok to insert new times w/o checking)
      //
      try {    

         pstmt = con.prepareStatement (
            "SELECT player1 " +
            "FROM teecurr2 " +
            "WHERE date = ? AND time = ? AND fb = ? and courseName = ?");

         pstmt.clearParameters();      
         pstmt.setLong(1, parm.date);       
         pstmt.setLong(2, parm.time);
         pstmt.setInt(3, parm.fb);
         pstmt.setString(4, parm.courseName);

         rs = pstmt.executeQuery();     

         if (!rs.next()) {

            stop = true;                  // tee time not found - ok to use this time
         }

      } catch (Exception ignore) {
      
      } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

      }
      
   }                                 // end of while

 }   // end of addTime

}
