/***************************************************************************************
 *   Member_events2:  This servlet will display event information and sign-up info
 *                    for the event selected in Member_events.
 *
 *
 *
 *   called by:  Member_events
 *               Member_jump (on return from Member_evntSignUp)
 *
 *
 *   created: 2/13/2003   Bob P.
 *
 *   last updated:
 *
 *        4/14/10   CC of Virginia - do not display names in the event signup list for the "2010 Mens Member Guest" event (case 1799). 
 *       12/09/09   When looking for events only check those that are active.
 *       10/10/09   Do not use the course name when locating the event (Activities do not use a course).
 *        9/28/09   Added support for Activities
 *        3/19/09   Changed team/count calcuation to seperate registered vs. waitlist and changed related display text
 *        2/11/09   Custom for Hazeltine - do not allow members to signup for Invitational unless they
 *                  have a sub-type of Invite Priority (case 1585).
 *       12/10/08   Provide a more specific message about why user can't signup for event
 *        4/22/08   Do not show instructions on how to join a team if hidenames = yes.
 *        4/02/08   Removed C/W info in players listing for season long events
 *        3/27/08   Add gender and season information to event summary
 *        9/27/07   Display the new minimum sign-up size as part of the event details
 *        7/16/07   Allow members to view the event signup list up until the date of the event.
 *        4/25/07   Congressional - pass the date for the ourse Name Labeling.
 *        3/20/07   Custom for Congressional - abstract the course name depending on the day (Course Name Labeling)
 *        7/19/06   Desert Highlands - ignore the 'Hide Names' parm and always show names for events.
 *        1/30/06   Do not display the member names if option selected in config.
 *        6/16/05   If the entry in the event signup list is full, leave the button column empty (was "Full").
 *                  Some members thought this meant the event was full.
 *        5/13/05   Add processing for restrictions on events (member may not have access to event).
 *        4/15/05   Inverness - do not show the Itinerary if signup=no.
 *        3/09/05   Do not allow signup if singup=no or date too early.
 *        1/24/05   Ver 5 - change club2 to club5.
 *       11/20/04   Ver 5 - allow for return to Member_teelist (add index=).
 *       10/06/04   Ver 5 - allow for sub-menus.
 *        1/13/04   JAG Modifications to match new color scheme
 *        7/18/03   Enhancements for Version 3 of the software.
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
//import java.util.*;
import java.sql.*;
//import java.lang.Math;
  
// foretees imports
import com.foretees.common.parmClub;
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.congressionalCustom;
import com.foretees.common.Labels;
import com.foretees.common.verifyCustom;
import com.foretees.common.Utilities;


public class Member_events2 extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


//********************************************************************************
//
//  doGet - call doPost processing (gets control from Member_jump)
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
 //  doPost processing - gets control from Member_events when user selects an event.
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

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   ResultSet rs = null;
   ResultSet rs2 = null;

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   String user = (String)session.getAttribute("user");      // get this user's username
   String club = (String)session.getAttribute("club");      // get club name
   String caller = (String)session.getAttribute("caller");  // get caller (web site)
   String mtype = (String)session.getAttribute("mtype");    // member's mtype 
   String mship = (String)session.getAttribute("mship");    // member's mship type

   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"ccccaa\">");
      out.println("<CENTER>");
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

   String name = "";
   String format = "";
   String pairings = "";
   String memcost = "";
   String gstcost = "";
   String itin = "";
   String course = "";
   String c_ampm = "";
   String act_ampm = "";
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
   String fb = "";
   String index = "";

   int hndcpOpt = 0;
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
   int c_month = 0;
   int c_day = 0;
   int c_year = 0;
   int c_time = 0;
   int c_hr = 0;
   int c_min = 0;
   int count_reg = 0;
   int count_wait = 0;
   int teams_reg = 0;
   int teams_wait = 0;
   int i = 0;
   int t = 0;
   int full = 0;
   int id = 0;
   //int skip = 0;
   int in_use = 0;
   int wait = 0;
   int signup = 0;
   int sutime = 0;
   int currtime = 0;
   int hideNames = 0;
   int hideN = 0;
   int g1 = 0;
   int g2 = 0;
   int g3 = 0;
   int g4 = 0;
   int g5 = 0;
   int gender = 0;
   int season = 0;

   long today = 0;
   long date = 0;
   long c_date = 0;
   long sudate = 0;
   float hndcp = 0;
   float hndcp1 = 0;
   float hndcp2 = 0;
   float hndcp3 = 0;
   float hndcp4 = 0;
   float hndcp5 = 0;

   boolean disp_hndcp = true;
   boolean viewList = false; // default to not being able to see the event signups

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);

   String [] mshipA = new String [parm.MAX_Mems+1];            // Mem Types
   String [] mtypeA = new String [parm.MAX_Mships+1];          // Mship Types

   //
   //   Get the parms received
   //
   name = req.getParameter("name");

   if (req.getParameter("course") != null) {         // if course name provided

      course = req.getParameter("course");
   }
      
   if (req.getParameter("index") != null) {         // if from Member_teelist or Member_teelist_list

      index = req.getParameter("index");      
   }

   //
   //   Get current date and time (adjusted for time zone)
   //
   today = SystemUtils.getDate(con);

   currtime = SystemUtils.getTime(con);

   try {

      //
      // Get the Multiple Course Option, guest types, days in advance and time for advance from the club db
      //
      getClub.getParms(con, parm, sess_activity_id);        // get the club parms

      hndcpOpt = parm.hndcpMemEvent;
        
      if (!club.equals( "deserthighlands" )) {     // ignore setting if Desert Highlands (show names)
        
         hideNames = parm.hiden;
      }

      
      //
      //  Custom for CC of Virginia - hide the names on the 2010 Mens Member Guest event
      //
      if (club.equals( "virginiacc" ) && name.equals("2010 Mens Member Guest")) {   
        
         hideNames = 1;
      }

      
      //
      //  Check if club wants to display handicaps
      //
      if (hndcpOpt == 0) {

         disp_hndcp = false;      // if NO
      }

      //
      //  count the number of players already signed up and compare against the max allowed value
      //
      PreparedStatement pstmt = con.prepareStatement (
         "SELECT player1, player2, player3, player4, player5, wait " +
         "FROM evntsup2b " +
         "WHERE name = ? AND courseName = ? AND inactive = 0");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);
      pstmt.setString(2, course);
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
         "SELECT * FROM events2b WHERE name = ? AND courseName = ? ");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);
      stmt.setString(2, course);
      rs = stmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         date = rs.getLong("date");
         year = rs.getInt("year");
         month = rs.getInt("month");
         day = rs.getInt("day");
         type = rs.getInt("type");
         act_hr = rs.getInt("act_hr");
         act_min = rs.getInt("act_min");
         course = rs.getString("courseName");
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
         holes = rs.getInt("holes");
         sudate = rs.getLong("su_date");
         sutime = rs.getInt("su_time");
         fb = rs.getString("fb");
         gender = rs.getInt("gender");
         season = rs.getInt("season");
         for (i=1; i<parm.MAX_Mems+1; i++) {
            mtypeA[i] = rs.getString("mem" +i);
         }
         for (i=1; i<parm.MAX_Mships+1; i++) {
            mshipA[i] = rs.getString("mship" +i);
         }

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

         //
         // Define message to display later
         //
         String reason_msg = "You cannot register for this event at this time."; // default msg

         //
         //  Check if member can View the signup list for this event
         //
         if (signup == 0) { 
         
             reason_msg = "Online sign-up is not available for this event.<br>Please contact your golf shop for assistance.";
             
         } else if (today >= sudate && today <= date) { // if on or after singup date and before or on day of event

             // allow members to view signups
             viewList = true;

         }


         // reason_msg = "The sign-up cutoff date has already passed.  You can no longer sign-up for this event.";

         //
         //  Check if member can signup for this event
         //
         if (signup > 0) {

            if (today > c_date || (today == c_date && currtime > c_time)) {           // if after signup date

                reason_msg = "The sign-up cutoff date has already passed.<br>You can no longer sign-up for this event.";
                signup = 0;

            } else if (today < sudate || (today == sudate && currtime < sutime)) {    // if before signup dates

                reason_msg = "The sign-up has not yet started for this event.";
                signup = 0;
            
            }
              
            if (signup > 0) {            // if signup still ok, check mtype restrictions

               i = 1;
               loopr1:
               while (i < parm.MAX_Mems+1) { 

                  if (mtype.equals( mtypeA[i] )) {       // is this member restricted?
                    
                     signup = 0;                         // no signup
                     reason_msg = "Your member type restricts you from playing in this event.";
                     break loopr1;                       // exit loop
                  }
                  i++;
               }
            }

            if (signup > 0) {            // if signup still ok, check mship restrictions

               i = 1;
               loopr2:
               while (i < parm.MAX_Mships+1) {

                  if (mship.equals( mshipA[i] )) {       // is this member restricted?

                     signup = 0;                         // no signup
                     reason_msg = "Your membership type restricts you from playing in this event.";
                     break loopr2;                       // exit loop
                  }
                  i++;
               }
            }

            if (sess_activity_id == 0 && signup > 0 && club.equals( "hazeltine" )) {     // if Hazeltine & signup still ok, check event name and member sub-type

               if (name.equals( "Mens Invitational" )) {        // if Invitational, then only members that played last year can signup
                  
                  String restPlayer = verifyCustom.checkHazeltineInvite(user, "", "", "", "", con);    // check if user is Invite Priority sub-type

                  if (!restPlayer.equals( "" )) {         // if NOT Invite Priority - no signup
               
                     signup = 0;                      
                     reason_msg = "Sorry, you are unable to register for this event. Please contact the golf shop if you have questions.";
                  }
               }
            }           // end of IF hazeltine
            
         }

         //
         //  Override team size if proshop pairings (just in case)
         //
         if (!pairings.equalsIgnoreCase( "Member" )) {

            size = 1;       // set size to one for proshop pairings (size is # per team)
         }

         //
         //   build the HTML page for the display
         //
         out.println(SystemUtils.HeadTitle("Member Event Sign Up Page"));
         out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
         SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
         out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

         out.println("<table border=\"0\" valign=\"top\">");       // table for main page
         out.println("<tr><td align=\"center\" valign=\"top\">");
         out.println("<font size=\"2\">");

         out.println("<form action=\"/" +rev+ "/servlet/Member_evntSignUp\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");

         out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" width=\"530\" cellpadding=\"5\" cellspacing=\"3\" valign=\"top\">");
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
            out.println("<font color=\"ffffff\" size=\"3\">");
            out.println("<b>" + name + "</b>");
            out.println("</font></td></tr>");
            out.println("<tr><td align=\"left\">");
            out.println("<font size=\"2\">");
            out.println("<b>Date:</b>&nbsp;&nbsp; " + ((season == 0) ? month + "/" + day + "/" + year : "Season Long"));
            if (season == 0) {
                out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                out.println("<b>Time:</b>&nbsp;&nbsp; " + act_hr + ":" + Utilities.ensureDoubleDigit(act_min) + " " + act_ampm);
                if (sess_activity_id == 0) {
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
            if (sess_activity_id == 0) {
                if (!course.equals( "" )) {

                   if (club.equals("congressional")) {
                       out.println("<b>Course:</b>&nbsp;&nbsp; " + congressionalCustom.getFullCourseName(date, day, course));
                   } else {
                       out.println("<b>Course:</b>&nbsp;&nbsp; " + course);
                   }
                   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                }

                out.println("<b>Front/Back:</b>&nbsp;&nbsp; " + fb + "<br><br>");
            }
            out.println("<b>Format:</b>&nbsp;&nbsp; " + format);
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<b>Gender:</b>&nbsp;&nbsp; " + Labels.gender_opts[gender] + "<br><br>");
            out.println("<b>Teams Selected By:</b>&nbsp;&nbsp; " + pairings);
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<b># of Teams:</b>&nbsp;&nbsp; " + max);
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<b>Team Size:</b>&nbsp;&nbsp; " + size + "<br><br>");
            if (sess_activity_id == 0) {
                out.println("<b>Holes:</b>&nbsp;&nbsp; " + holes);
                out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            }
            out.println("<b>Min. Sign-Up Size:</b>&nbsp;&nbsp; " + minsize + "<br><br>");
            out.println("<nobr><b>Guests per Member:</b>&nbsp;&nbsp;" + guests);
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<b>Cost per Guest:</b>&nbsp;&nbsp;" + gstcost);
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<b>Cost per Member:</b>&nbsp;&nbsp;" + memcost + "</nobr><br><br>");
            out.println("<b>Must Sign Up By:</b>&nbsp;&nbsp; " + c_hr + ":" + Utilities.ensureDoubleDigit(c_min) + " " + c_ampm +  " on " + c_month + "/" + c_day + "/" + c_year);
            if (!club.equals( "inverness" ) || signup > 0) {   // do not display itin if Inverness and no Signup
               out.println("<br><br>");
               out.println("<b>Itinerary:</b>&nbsp;&nbsp; " + itin + "<br>");
            }
            out.println("</font></td></tr>");
            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\">");

            if (signup == 0) {         // if no signup at this time

               if (!club.equals( "foresthighlands" )) {      // skip if Forest Highlands
                  
                   out.println("<b>" + reason_msg + "</b>");
               }

            } else {

               if (teams_reg >= max) {         // if no room for more teams

                  if (pairings.equalsIgnoreCase( "Proshop" )) {

                     out.println("<input type=\"hidden\" name=\"new\" value=\"new\">");
                     out.println("<b>Warning:</b> this event already has " + count_reg + " members registered.<br><br>");
                     out.println("To be added to the waiting list, click here: ");
                     out.println("<input type=\"submit\" value=\"Sign Up\" >");
                     out.println("<br>");
                  } else {

                     out.println("<input type=\"hidden\" name=\"new\" value=\"new\">");
                     out.println("<b>Warning:</b> this event already has " + teams_reg + " teams registered.<br><br>");
                     if (hideNames == 0) {      // if NOT hiding names                
                        out.println("To join an existing team, click on the Select button in the table below.<br>");
                     }
                     out.println("To add a team to the <b>waiting list</b>, click here: ");
                     out.println("<input type=\"submit\" value=\"Sign Up\" >");
                     out.println("<br>");
                  }
               } else {

                  if (size == 1) {      // only member can sign up (no guests or other members)

                     out.println("<input type=\"hidden\" name=\"new\" value=\"new\">");
                     out.println("Because of the format selected for this event, you can only register yourself.<br>");
                     out.println("To sign up for this event click here: ");
                     out.println("<input type=\"submit\" value=\"Sign Up\" >");
                     out.println("<br>");

                  } else {              // allow team sign up

                     out.println("<input type=\"hidden\" name=\"new\" value=\"new\">");                   
                     if (hideNames == 0) {      // if NOT hiding names, then members can join a team
                        out.println("The format of this event allows you to join an existing team or register a new team.<br>");
                        out.println("To join an existing team, click on the Select button in the table below.");
                     }
                     out.println("<br>To register a new team, click here: ");
                     out.println("<input type=\"submit\" value=\"New Team\" >");
                     out.println("<br>");
                  }
               }
            }
            out.println("</font></td>");
         out.println("</tr></table></form>");             // end of header table

         if (signup > 0 || viewList == true) {         // if member can signup or view the list at this time

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

               /*
               if (pairings.equals( "Member" )) {

                  out.println("<br>There are currently " + teams_reg + " teams and " + count_reg + " players registered for this event.<br>");
               } else {
                  out.println("<br>There are currently " + count_reg + " players registered for this event.<br>");
               }
               */

               if (teams_reg > 5) {

                  out.println("<font size=\"2\">");
                  if (!index.equals( "" )) {
                     if (index.equals( "999" )) {
                        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_teelist\">");
                     } else {
                        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_teelist_list\">");
                     }
                  } else {
                     out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_events\">");
                  }
                  out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                  out.println("</form></font>");
               }
               out.println("<b>Players currently registered for this event:</b>");

               if (signup > 0) {         // if member can signup at this time

                  out.println("<form action=\"/" +rev+ "/servlet/Member_evntSignUp\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               }

               //
               //  Get all entries for this Event Sign Up Sheet
               //
               PreparedStatement pstmte = con.prepareStatement (
                  "SELECT * FROM evntsup2b " +
                  "WHERE name = ? AND courseName = ? AND inactive = 0 ORDER BY r_date, r_time");

               pstmte.clearParameters();        // clear the parms
               pstmte.setString(1, name);
               pstmte.setString(2, course);
               rs2 = pstmte.executeQuery();      // execute the prepared pstmt

               out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"5\" valign=\"top\">");
               out.println("<tr bgcolor=\"#336633\" style=\"color: white\">");

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<u><b>Select</b></u>");
                  out.println("</font></td>");

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<u><b>Player 1</b></u> ");
                  if (disp_hndcp == true) {
                     out.println("</font><font size=\"1\"><u>hndcp</u>");
                  }
                  out.println("</font></td>");
               
               if (sess_activity_id == 0 && season == 0) {
                  out.println("<td align=\"center\">");
                  out.println("<font size=\"1\">");
                  out.println("<u><b>C/W</b></u>");
                  out.println("</font></td>");
               }
                  
               if (size > 1) {

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<u><b>Player 2</b></u> ");
                  if (disp_hndcp == true) {
                     out.println("</font><font size=\"1\"><u>hndcp</u>");
                  }
                  out.println("</font></td>");

                  if (sess_activity_id == 0 && season == 0) {
                     out.println("<td align=\"center\">");
                     out.println("<font size=\"1\">");
                     out.println("<u><b>C/W</b></u>");
                     out.println("</font></td>");
                  }
               }

               if (size > 2) {

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<u><b>Player 3</b></u> ");
                  if (disp_hndcp == true) {
                     out.println("</font><font size=\"1\"><u>hndcp</u>");
                  }
                  out.println("</font></td>");

                  if (sess_activity_id == 0 && season == 0) {
                      out.println("<td align=\"center\">");
                      out.println("<font size=\"1\">");
                      out.println("<u><b>C/W</b></u>");
                      out.println("</font></td>");
                  }
               }

               if (size > 3) {

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<u><b>Player 4</b></u> ");
                  if (disp_hndcp == true) {
                     out.println("</font><font size=\"1\"><u>hndcp</u>");
                  }
                  out.println("</font></td>");

                  if (sess_activity_id == 0 && season == 0) {
                      out.println("<td align=\"center\">");
                      out.println("<font size=\"1\">");
                      out.println("<u><b>C/W</b></u>");
                      out.println("</font></td>");
                  }
               }

               if (size > 4) {

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<u><b>Player 5</b></u> ");
                  if (disp_hndcp == true) {
                     out.println("</font><font size=\"1\"><u>hndcp</u>");
                  }
                  out.println("</font></td>");

                  if (sess_activity_id == 0 && season == 0) {
                      out.println("<td align=\"center\">");
                      out.println("<font size=\"1\">");
                      out.println("<u><b>C/W</b></u>");
                      out.println("</font></td>");
                  }
               }

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
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
                  in_use = rs2.getInt("in_use");
                  hndcp1 = rs2.getFloat("hndcp1");
                  hndcp2 = rs2.getFloat("hndcp2");
                  hndcp3 = rs2.getFloat("hndcp3");
                  hndcp4 = rs2.getFloat("hndcp4");
                  hndcp5 = rs2.getFloat("hndcp5");
                  id = rs2.getInt("id");
                  wait = rs2.getInt("wait");               // wait list indicator

                  //
                  //   skip this entry if all players are null (someone cancelled)
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

                     //
                     //  Hide Names Feature - if club opts to hide the member names, then hide all names
                     //                       except for any group that this user is part of.
                     //
                     hideN = 0;           // default to 'do not hide names'

                     if (hideNames > 0) {

                        hideN = 1;        // hide names in this group

                        if (user.equalsIgnoreCase(user1) || user.equalsIgnoreCase(user2) || user.equalsIgnoreCase(user3) ||
                            user.equalsIgnoreCase(user4) || user.equalsIgnoreCase(user5)) {    // if user is in this group

                           hideN = 0;                // do not hide this group
                             
                        } else {
                          
                           g1 = 0;          // init guest indicators
                           g2 = 0;
                           g3 = 0;
                           g4 = 0;
                           g5 = 0;

                           //
                           //  Check if any player names are guest names
                           //
                           if (!player1.equals( "" ) && !player1.equalsIgnoreCase( "x" )) {

                              if (user1.equals( "" )) {       // if player but not member or x
                                
                                 g1 = 1;                      // must be guest
                              }
                           }
                           if (!player2.equals( "" ) && !player2.equalsIgnoreCase( "x" )) {

                              if (user2.equals( "" )) {       // if player but not member or x

                                 g2 = 1;                      // must be guest
                              }
                           }
                           if (!player3.equals( "" ) && !player3.equalsIgnoreCase( "x" )) {

                              if (user3.equals( "" )) {       // if player but not member or x

                                 g3 = 1;                      // must be guest
                              }
                           }
                           if (!player4.equals( "" ) && !player4.equalsIgnoreCase( "x" )) {

                              if (user4.equals( "" )) {       // if player but not member or x

                                 g4 = 1;                      // must be guest
                              }
                           }
                           if (!player5.equals( "" ) && !player5.equalsIgnoreCase( "x" )) {

                              if (user5.equals( "" )) {       // if player but not member or x

                                 g5 = 1;                      // must be guest
                              }
                           }
                        }
                     }

                     //
                     // determine if slot is full and this user is not on it
                     //
                     full = 0;

                     if (size == 1 && !player1.equals( "" )) {

                        if (!user1.equalsIgnoreCase( user )) {

                           full = 1;        // full and user not in it
                        }
                     }
                     if (size == 2 && !player1.equals( "" ) && !player2.equals( "")) {

                        if (!user1.equalsIgnoreCase( user ) && !user2.equalsIgnoreCase( user )) {

                           full = 1;        // full and user not in it
                        }
                     }
                     if (size == 3 && !player1.equals( "" ) && !player2.equals( "") && !player3.equals( "")) {

                        if (!user1.equalsIgnoreCase( user ) && !user2.equalsIgnoreCase( user ) && !user3.equalsIgnoreCase( user )) {

                           full = 1;        // full and user not in it
                        }
                     }
                     if (size == 4 && !player1.equals( "" ) && !player2.equals( "") &&
                         !player3.equals( "" ) && !player4.equals( "")) {

                        if (!user1.equalsIgnoreCase( user ) && !user2.equalsIgnoreCase( user ) && !user3.equalsIgnoreCase( user ) &&
                            !user4.equalsIgnoreCase( user )) {

                           full = 1;        // full and user not in it
                        }
                     }
                     if (size == 5 && !player1.equals( "" ) && !player2.equals( "") &&
                         !player3.equals( "" ) && !player4.equals( "") && !player5.equals( "")) {

                        if (!user1.equalsIgnoreCase( user ) && !user2.equalsIgnoreCase( user ) && !user3.equalsIgnoreCase( user ) &&
                            !user4.equalsIgnoreCase( user ) && !user5.equalsIgnoreCase( user )) {

                           full = 1;        // full and user not in it
                        }
                     }

                     //
                     //  list the players according to team size (use 'submit' to id the entry)
                     //
                     out.println("<tr><td align=\"center\">");
                     out.println("<font size=\"2\">");
                     if (in_use == 0) {
                        if (full == 0 && hideN == 0 && signup > 0) {
                           out.println("<input type=\"submit\" value=\"Select\" name=\"" + submit + "\" id=\"" + submit + "\">");
                        } else {
                           out.println("&nbsp;");
                        }
                     } else {
                        out.println("Busy");
                     }
                     out.println("</font></td>");

                     //
                     //  Add Player 1
                     //
                     out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");

                     if (!player1.equals("")) {

                        if (player1.equalsIgnoreCase("x")) {   // if 'x'

                           out.println("X");

                        } else {       // not 'x'

                           if (hideN == 0) {             // if ok to display names

                              if (disp_hndcp == false) {
                                 out.println(player1);
                              } else {
                                 if ((hndcp1 == 99) || (hndcp1 == -99)) {
                                    out.println(player1);
                                 } else {
                                    if (hndcp1 <= 0) {
                                       hndcp1 = 0 - hndcp1;                       // convert to non-negative
                                    }
                                    hndcp = Math.round(hndcp1);                   // round it off
                                    out.println(player1 + "  " + hndcp);
                                 }
                              }
                                
                           } else {                        // do not display member names
                             
                              if (g1 != 0) {               // if guest

                                 out.println("Guest");

                              } else {                     // must be a member

                                 out.println("Member");
                              }
                           }
                        }
                     } else {     // player is empty

                        out.println("&nbsp;");
                     }
                     out.println("</font></td>");
                     if (sess_activity_id == 0 && season == 0) {
                         
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

                              out.println("X");

                           } else {       // not 'x'

                              if (hideN == 0) {             // if ok to display names

                                 if (disp_hndcp == false) {
                                    out.println(player2);
                                 } else {
                                    if ((hndcp2 == 99) || (hndcp2 == -99)) {
                                       out.println(player2);
                                    } else {
                                       if (hndcp2 <= 0) {
                                          hndcp2 = 0 - hndcp2;                       // convert to non-negative
                                       }
                                       hndcp = Math.round(hndcp2);                   // round it off
                                       out.println(player2 + "  " + hndcp);
                                    }
                                 }
                                   
                              } else {                        // do not display member names

                                 if (g2 != 0) {               // if guest

                                    out.println("Guest");

                                 } else {                     // must be a member

                                    out.println("Member");
                                 }
                              }
                           }
                             
                        } else {     // player is empty

                           out.println("&nbsp;");
                        }
                        out.println("</font></td>");
                        if (sess_activity_id == 0 && season == 0) {
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

                              out.println("X");

                           } else {       // not 'x'

                              if (hideN == 0) {             // if ok to display names

                                 if (disp_hndcp == false) {
                                    out.println(player3);
                                 } else {
                                    if ((hndcp3 == 99) || (hndcp3 == -99)) {
                                       out.println(player3);
                                    } else {
                                       if (hndcp3 <= 0) {
                                          hndcp3 = 0 - hndcp3;                       // convert to non-negative
                                       }
                                       hndcp = Math.round(hndcp3);                   // round it off
                                       out.println(player3 + "  " + hndcp);
                                    }
                                 }
                                   
                              } else {                        // do not display member names

                                 if (g3 != 0) {               // if guest

                                    out.println("Guest");

                                 } else {                     // must be a member

                                    out.println("Member");
                                 }
                              }
                           }
                        } else {     // player is empty

                           out.println("&nbsp;");
                        }
                        out.println("</font></td>");
                        if (sess_activity_id == 0 && season == 0) {
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

                              out.println("X");

                           } else {       // not 'x'

                              if (hideN == 0) {             // if ok to display names

                                 if (disp_hndcp == false) {
                                    out.println(player4);
                                 } else {
                                    if ((hndcp4 == 99) || (hndcp4 == -99)) {
                                       out.println(player4);
                                    } else {
                                       if (hndcp4 <= 0) {
                                          hndcp4 = 0 - hndcp4;                       // convert to non-negative
                                       }
                                       hndcp = Math.round(hndcp4);                   // round it off
                                       out.println(player4 + "  " + hndcp);
                                    }
                                 }
                                   
                              } else {                        // do not display member names

                                 if (g4 != 0) {               // if guest

                                    out.println("Guest");

                                 } else {                     // must be a member

                                    out.println("Member");
                                 }
                              }
                           }
                        } else {     // player is empty

                           out.println("&nbsp;");
                        }
                        out.println("</font></td>");
                            if (sess_activity_id == 0 && season == 0) {
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

                              out.println("X");

                           } else {       // not 'x'

                              if (hideN == 0) {             // if ok to display names

                                 if (disp_hndcp == false) {
                                    out.println(player5);
                                 } else {
                                    if ((hndcp5 == 99) || (hndcp5 == -99)) {
                                       out.println(player5);
                                    } else {
                                       if (hndcp5 <= 0) {
                                          hndcp5 = 0 - hndcp5;                       // convert to non-negative
                                       }
                                       hndcp = Math.round(hndcp5);                   // round it off
                                       out.println(player5 + "  " + hndcp);
                                    }
                                 }
                                   
                              } else {                        // do not display member names

                                 if (g5 != 0) {               // if guest

                                    out.println("Guest");

                                 } else {                     // must be a member

                                    out.println("Member");
                                 }
                              }
                           }
                        } else {     // player is empty

                           out.println("&nbsp;");
                        }
                        out.println("</font></td>");
                        if (sess_activity_id == 0 && season == 0) {
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
                     //  add status (on wait list?)
                     //
                     out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     if (wait == 0) {
                        out.println("Registered");
                     } else {
                        out.println("Wait List");
                     }
                     out.println("</font></td>");

                     out.println("</tr>");              // end of row

                  }  // end of IF entry is empty (all players = null)

               }                   // end of while
               pstmte.close();

               out.println("</table></form>");              // end of player table
            }

         }    // end of IF signup     

         out.println("</font></td></tr>");
         out.println("</table>");                   // end of main page table

         out.println("<br><font size=\"2\">");
         if (!index.equals( "" )) {
            if (index.equals( "999" )) {
               out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_teelist\">");
            } else {
               out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_teelist_list\">");
            }
         } else {
            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_events\">");
         }
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</form></font>");

         //
         //  End of HTML page
         //
         out.println("</center></font></body></html>");
         out.close();
         
      } else {    // event name not found

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY bgcolor=\"ccccaa\">");
         out.println("<CENTER>");
         out.println("<BR><BR><H3>Procedure Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to locate the selected event (" +name+ ").");
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your golf shop (provide this information).");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         
      }   // end of IF event

      stmt.close();

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY bgcolor=\"ccccaa\">");
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

 }   // end of doPost

}
