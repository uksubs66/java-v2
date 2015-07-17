/***************************************************************************************
 *   Member_teelist_mobile:  This servlet will search teecurr for this member and display
 *                         a list of current tee times.  Also, search evntsup for any
 *                         events and lreqs for any lottery requests.
 *
 *    For MOBILE users only!!!!!
 * 
 * 
 *   called by:  member_mobile_home.html
 *
 *   created: 7/24/2009   Bob P.
 *
 *   last updated:
 *
 *        5/29/13   Pass the club name to Utilities.checkRests for customs.
 *        7/05/11   Forest Highlands GC (foresthighlands) - Added custom so tee times entered from this page hide the 5th player position during the appropriate date range (case 1613).
 *        1/19/11   Members will now be appropriately blocked from accessing restricted times they are a part of.
 *        9/16/10   Edina CC 2010 (edina2010) - Do not allow members to access tee times via the tee list
 *        9/10/10   Do not display event and lottery times that have been drug to the tee sheet but not approved
 *        9/02/10   Changes to support passing encrypted tee time info to _slot page
 *        5/18/10   Fix noAccessAfterLottery custom processing to check for lottery_color in teecurr to determine if lottery processed (case 1827).
 *        4/20/10   Bonnie Briar - do not allow members to access tee times after the lottery has been processed (case 1822).
 *       12/09/09   When looking for events only check those that are active.
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
import com.foretees.common.getRests;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.Utilities;


public class Member_teelist_mobile extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   PreparedStatement pstmt1 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rsev = null;

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }
   
   int mobile = 0;

   //
   //  Only allow Mobile users
   //
   try {      
      mobile = (Integer)session.getAttribute("mobile");        
   }
   catch (Exception ignore) {   
      mobile = 0;
   }
   
   if (mobile == 0) {       // if NOT mobile user
      
      out.println(SystemUtils.HeadTitle("Access Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
      out.println("<BR><BR><H3>Access Error</H3>");
      out.println("<BR><BR>Invalid access - this process is for mobile users only.");
      out.println("<BR><BR>If you feel you received this message in error, please contact your golf shop staff.");
      out.println("<BR><BR>");
      out.println("<a href=\"Member_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }
      
   
   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {
      
      SystemUtils.displayMobileError("Unable to connect to the Database.<BR><BR>If problem persists, contact your golf shop staff.", "", out);    // ouput error message
      return;
   }

   //String omit = "";
   String ampm = "";
   String day = "";
   String day_short = "";
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
   String sfb = "";
   String submit = "";
   String name = "";
   String course = "";
   String rest5 = "";
   String rest5_color = "";
   String zone = "";
   String lname = "";
   String rest = "";
   String ename = "";
   String stime2 = "";
   String lotteryText = "";
   String lotteryName = "";
   String lottery_color = "";

   long date = 0;
   long edate = 0;
   long cdate = 0;
   long c_date = 0;
   long lottid = 0;

   int c_time  = 0;
   int mm  = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int ptime = 0;
   int ctime = 0;
   int count = 0;
   int multi = 0;
   int lottery = 0;
   int lstate = 0;
   int sdays = 0;
   int sdtime = 0;
   int edays = 0;
   int edtime = 0;
   int pdays = 0;
   //int pdtime = 0;
   int slots = 0;
   int advance_days = 0;
   int fives = 0;
   int fivesomes = 0;
   int signUp = 0;
   int fb = 0;
   int etype = 0;
   int wait = 0;

   String user = (String)session.getAttribute("user");      // get username
   String club = (String)session.getAttribute("club");      // get club name
   //String caller = (String)session.getAttribute("caller");      // get caller
   String mship = (String)session.getAttribute("mship");             // get member's mship type
   String mtype = (String)session.getAttribute("mtype");             // get member's mtype

   //int tmp_tlt = (Integer)session.getAttribute("tlt");
   //boolean IS_TLT = (tmp_tlt == 1) ? true : false;
   
   boolean events = false;
   boolean restricted = false;
   boolean cutoff = false;
   boolean restrictAllTees = false;


   //
   //  boolean for clubs that want to block member access to tee times after a lottery has been processed.
   //
   //  NOTE:  see same flag in Member_teelist and Member_sheet and Member_teelist_list !!!!!!!!!!!!!!!!!!
   //
   boolean noAccessAfterLottery = false;

   if (club.equals( "bonniebriar" ) ) {   // add other clubs here!!
       
       noAccessAfterLottery = true;      // no member access after lottery processed
   }

   if (club.equals("edina2010")) {

       restrictAllTees = true;
   }
   
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // no mobile support yet for FlxRez so hard code a zero as the root activity_id for now


   //
   //   Get options for this club
   //
   try {

      //
      // Get the days in advance and time for advance from the club db
      //
      getClub.getParms(con, parm);        // get the club parms

      multi = parm.multi;
      lottery = parm.lottery;
      zone = parm.adv_zone;
        

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT fives FROM clubparm2");           // check all courses for 5-somes

      while (rs.next()) {

         fives = rs.getInt(1);

         if (fives != 0) {

            fivesomes = 1;
         }
      }
      stmt.close();

   }
   catch (Exception exc) {

      SystemUtils.displayMobileError("Unable to connect to the Database.<BR><BR>If problem persists, contact your golf shop staff.", "", out);    // ouput error message
      return;
   }

   //
   //  get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);
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

   cdate = (yy * 10000) + (mm * 100) + dd;      // create a date field of yyyymmdd
   
   String mysql_date = yy + "-" + SystemUtils.ensureDoubleDigit(mm) + "-" + SystemUtils.ensureDoubleDigit(dd);

   //
   //  Get tomorrow's date for cutoff test
   //
   cal.add(Calendar.DATE,1);                     // get next day's date
   yy = cal.get(Calendar.YEAR);
   mm = cal.get(Calendar.MONTH) +1;
   dd = cal.get(Calendar.DAY_OF_MONTH);

   long tomorrowDate = (yy * 10000) + (mm * 100) + dd;     // create a date field of yyyymmdd
   
   yy = 0;
   mm = 0;
   dd = 0;

  //
  //   build the HTML page for the display
  //
  out.println(SystemUtils.HeadTitleMobile("ForeTees Member Tee List"));
  out.println(SystemUtils.BannerMobile());
      
  
  out.println("<div class=\"content\">");

  count = 0;
  
  try {
      //
      // search for this user's tee times
      //
      pstmt1 = con.prepareStatement (
         "SELECT COUNT(*) " +
         "FROM teecurr2 " +
         "WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? " +
         "OR username5 = ? OR userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ? OR orig_by = ?) " +
         "AND lottery_email = 0 " +
         "ORDER BY date, time");

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
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         count = rs.getInt(1);       // get number of tee times
      }
      
      pstmt1.close();
      
  }
  catch (Exception exc) {

      SystemUtils.displayMobileError("Database Error (1).<BR><BR>If problem persists, contact your golf shop staff.", "", out);    // ouput error message
      out.close();
      return;
  }
   

  out.println("<div class=\"headertext\">Your current Tee Times</div>");    // output the heading
      

  try {
     
      if (count > 0) {         // if any tee times      
      
         out.println("<div class=\"smheadertext\">");     
         out.println("Click on time to access Tee Time.");
         out.println("</div>");

         out.println("<table cellpadding=\"0\" cellspacing=\"1\" border=\"1\" id=\"ttimes\">");
         out.println("<tr class=\"tableheader\">");

         out.println("<td><strong>Date</td>");
         out.println("<td><strong>Time</td>");
         if (multi != 0) {
            out.println("<td><strong>Course</td>");
         }
         out.println("<td><strong>Players</td>");
         out.println("</tr>");

         //
         // search for this user's tee times again
         //
         pstmt1 = con.prepareStatement (
            "SELECT date, mm, dd, yy, day, hr, min, time, event, player1, player2, player3, player4, event_type, fb, " +
            "player5, lottery, courseName, rest5, rest5_color, lottery_color " +
            "FROM teecurr2 " +
            "WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? " +
            "OR username5 = ? OR userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ? OR orig_by = ?) " +
            "AND lottery_email = 0 " +
            "ORDER BY date, time");

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
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            //
            //  Get the first record and display it
            //
            date = rs.getLong(1);
            mm = rs.getInt(2);
            dd = rs.getInt(3);
            yy = rs.getInt(4);
            day = rs.getString(5);
            hr = rs.getInt(6);                                                   
            min = rs.getInt(7);
            time = rs.getInt(8);
            ename = rs.getString(9);
            player1 = rs.getString(10);
            player2 = rs.getString(11);
            player3 = rs.getString(12);
            player4 = rs.getString(13);
            etype = rs.getInt(14);                                     
            fb = rs.getInt(15);
            player5 = rs.getString(16);
            lotteryName = rs.getString(17);
            course = rs.getString(18);
            rest5 = rs.getString(19);
            rest5_color = rs.getString(20);
            lottery_color = rs.getString(21);

            //
            //  Check if a member restriction has been set up to block ALL mem types or mship types for this date & time
            //
            restricted = Utilities.checkRests(date, time, fb, course, day, mship, mtype, club, con);

            ampm = " AM";
            if (hr == 12) {
               ampm = " PM";
            }
            if (hr > 12) {
               ampm = " PM";
               hr = hr - 12;    // convert to conventional time
            }

            //
            //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
            //
            sfb = "O";       // default Other

            if (fb == 1) {

               sfb = "B";
            }

            if (fb == 0) {

               sfb = "F";
            }

            submit = "time:" + fb;       // create a name for the submit button

            //
            //  Build the HTML for each record found
            //
            out.println("<tr class=\"tablerow\">");
            out.println("<form action=\"Member_slot\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"ttdata\" value=\"" + Utilities.encryptTTdata(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "|" + fb + "|" + user) + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
            out.println("<input type=\"hidden\" name=\"day\" value=" + day + ">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=995>");  // indicate from here

            //
            //  check if 5-somes allowed on this course
            //
            PreparedStatement pstmt2 = con.prepareStatement (
               "SELECT fives FROM clubparm2 WHERE courseName = ?");

            pstmt2.clearParameters();        // clear the parms
            pstmt2.setString(1, course);
            rs2 = pstmt2.executeQuery();      // execute the prepared pstmt2

            if (rs2.next()) {

               fives = rs2.getInt(1);

               //
               // Forest Highlands - don't allow 5-somes or show 5th player column during specified date range
               //
               if (club.equals("foresthighlands")) {

                   long month_day = (mm * 100) + dd;     // get adjusted date

                   if (month_day > 424 && month_day < 1001) {
                       fives = 0;
                   }
               }

               if ((fives != 0 ) && (rest5.equals( "" ))) {   // if 5-somes and not restricted

                  out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
               } else {

                  out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
               }
            }

            pstmt2.close();
            
            //
            //  Get shorter value for day
            //
            if (day.equals( "Sunday" )) {
               day_short = "Sun";
            } else if (day.equals( "Monday" )) {
               day_short = "Mon";
            } else if (day.equals( "Tuesday" )) {
               day_short = "Tue";
            } else if (day.equals( "Wednesday" )) {
               day_short = "Wed";
            } else if (day.equals( "Thursday" )) {
               day_short = "Thur";
            } else if (day.equals( "Friday" )) {
               day_short = "Fri";
            } else if (day.equals( "Saturday" )) {
               day_short = "Sat";               
            }

            out.println("<td>");
            out.println( day_short + "<BR>" + mm + "/" + dd );
            out.println("</td><td>");
              
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

               out.println(stime2 + " Shotgun");

            } else {

               cutoff = false;

               //
               //  Check for Member Cutoff specified in Club Options
               //
               if (parm.cutoffdays < 99) {        // if option specified

                  if (parm.cutoffdays == 0 && date == cdate && ctime > parm.cutofftime) {  // if cutoff day of and we are doing today and current time is later than cutoff time             

                     cutoff = true;         // indicate no member access

                  } else {

                     if (parm.cutoffdays == 1 && (date == cdate || (date == tomorrowDate && ctime > parm.cutofftime))) {    // if cutoff day is the day before           

                        cutoff = true;         // indicate no member access
                     }
                  }
               }
               

               //
               //  Check for lottery time
               //
               if (!lotteryName.equals("")) {

                  //
                  //  Get the current state of this lottery on the day of this tee time
                  //
                  lstate = SystemUtils.getLotteryState(date, mm, dd, yy, lotteryName, course, con);

                  if (lstate < 5 || noAccessAfterLottery) {    // if lottery not approved OR access not allowed after approval

                     cutoff = true;        // do not allow access to this tee time (pre-booked lottery time)
                  }
                        
               } else {

                  if (!lottery_color.equals("") && noAccessAfterLottery) {   // if it was a lottery time and access not allowed after processed

                     cutoff = true;                    // do not allow access to this tee time
                  }
               }
                    
               if (restrictAllTees) {    // if all tee times are restricted

                   cutoff = true;        // do not allow access to this tee time
               }
                    
               //
               //  Display time button or just the time
               //
               if (!restricted && !cutoff && !(date == cdate && time <= ctime)) {     // if mem can edit tee time
                  if (min < 10) {
                     out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":0" + min + ampm + "\" >");
                  } else {
                     out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + min + ampm + "\" >");
                  }
               } else {
                  if (min < 10) {
                     out.println(hr + ":0" + min + ampm);
                  } else {
                     out.println(hr + ":" + min + ampm);
                  }
               }
            }
            out.println("</td>");

            if (multi != 0) {
               out.println("<td>" + course + "</td>");
            }

            out.println("<td>");
            
             StringBuffer playerBfr = new StringBuffer();       // get buffer for players

             //   Add Players
             if (!player1.equals("")) {

                playerBfr.append( player1 );
             }

             if (!player2.equals("")) {

                playerBfr.append( "<BR>" );             // add seperator
                playerBfr.append( player2 );
             }

             if (!player3.equals("")) {

                playerBfr.append( "<BR>" );             // add seperator
                playerBfr.append( player3 );
             }

             if (!player4.equals("")) {

                playerBfr.append( "<BR>" );             // add seperator
                playerBfr.append( player4 );
             }

             if (fivesomes != 0 && !player5.equals("")) {
                playerBfr.append( "<BR>" );             // add seperator
                playerBfr.append( player5 );                       
             }

             out.println( playerBfr.toString() );     // add the players                         
            
            out.println("</td>");
            out.println("</form></tr>");

         }    // end of while

         out.println("</table>");

         
      } else {                                  // no tee times

         out.println("<div class=\"smheadertext\">No tee times scheduled.<BR></div>");     

      }    // end of if

      pstmt1.close();

  }
  catch (Exception exc) {

      SystemUtils.displayMobileError("Database Error (2).<BR><BR>If problem persists, contact your golf shop staff.", "", out);    // ouput error message
      out.close();
      return;
  }
   
   
   //
   //*****************************************************************
   // now check for current events that member is signed up for
   //*****************************************************************
   //
   try {

      count = 0;
    
      //
      //  Build html to display any events
      //      
      out.println("<BR><BR><div class=\"headertext\">Your current Event Registrations</div>");    // output the heading
      
      
      PreparedStatement pstmte = con.prepareStatement (
         "SELECT name, courseName " +
         "FROM evntsup2b WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? " +
         "OR username5 = ?) AND inactive = 0");

      pstmte.clearParameters();        // clear the parms
      pstmte.setString(1, user);
      pstmte.setString(2, user);
      pstmte.setString(3, user);
      pstmte.setString(4, user);
      pstmte.setString(5, user);
      rs = pstmte.executeQuery();      // execute the prepared stmt

      loop1:
      while (rs.next()) {

         name = rs.getString(1);
         course = rs.getString(2);

         //
         //   Find the matching event
         //
         PreparedStatement pstmte2 = con.prepareStatement (
            "SELECT date " +
            "FROM events2b WHERE name = ? AND courseName = ?");

         pstmte2.clearParameters();        // clear the parms
         pstmte2.setString(1, name);
         pstmte2.setString(2, course);
         rs2 = pstmte2.executeQuery();      // execute the prepared stmt

         if (rs2.next()) {

            edate = rs2.getLong(1);

            if (edate >= cdate) {          // if event is today or later - we found one

               events = true;
            }
         }
         pstmte2.close();

         if (events == true) {

            break loop1;
         }
      }        // end of WHILE evntsup

      pstmte.close();

      //
      //  if any events found above, then display them here (search over again)
      //
      if (events == true) {

         //
         //  Build html to display any events
         //
         out.println("<div class=\"smheadertext\">");     
         out.println("Display Only - no access on Mobile.");
         out.println("</div>");

         out.println("<table cellpadding=\"0\" cellspacing=\"1\" border=\"1\" id=\"etimes\">");
         out.println("<tr class=\"tableheader\">");

         out.println("<td><strong>Event Name</td>");
         out.println("<td><strong>Date<BR>Time</td>");
       //  if (multi != 0) {
       //     out.println("<td><strong>Course</td>");
       //  }
         out.println("<td><strong>Players</td>");
         out.println("<td><strong>Status</td>");
         out.println("</tr>");

         
         //
         // now check for events that member is signed up for
         //
         pstmte = con.prepareStatement (
            "SELECT name, courseName, player1, player2, player3, player4, player5, wait " +
            "FROM evntsup2b WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? " +
            "OR username5 = ?) AND inactive = 0 ORDER BY c_date, c_time");

         pstmte.clearParameters();        // clear the parms
         pstmte.setString(1, user);
         pstmte.setString(2, user);
         pstmte.setString(3, user);
         pstmte.setString(4, user);
         pstmte.setString(5, user);
         rs = pstmte.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            name = rs.getString(1);
            course = rs.getString(2);
            player1 = rs.getString(3);
            player2 = rs.getString(4);
            player3 = rs.getString(5);
            player4 = rs.getString(6);
            player5 = rs.getString(7);
            wait = rs.getInt(8);


            //
            //   Find the matching event
            //
            PreparedStatement pstmte2 = con.prepareStatement (
               "SELECT date, year, month, day, act_hr, act_min, signUp, c_date, c_time " +
               "FROM events2b WHERE name = ? AND courseName = ?");

            pstmte2.clearParameters();        // clear the parms
            pstmte2.setString(1, name);
            pstmte2.setString(2, course);
            rs2 = pstmte2.executeQuery();      // execute the prepared stmt

            if (rs2.next()) {

               edate = rs2.getLong(1);
               yy = rs2.getInt(2);
               mm = rs2.getInt(3);
               dd = rs2.getInt(4);
               hr = rs2.getInt(5);
               min = rs2.getInt(6);
               signUp = rs2.getInt(7);
               c_date = rs2.getLong(8);
               c_time = rs2.getInt(9);

               if (edate >= cdate) {          // if event is today or later - we found one

                  //
                  //  Create time values
                  //
                  ampm = "AM";

                  if (hr == 0) {

                     hr = 12;                 // change to 12 AM (midnight)

                  } else {

                     if (hr == 12) {

                        ampm = "PM";         // change to Noon
                     }
                  }
                  if (hr > 12) {

                     hr = hr - 12;
                     ampm = "PM";             // change to 12 hr clock
                  }

                  if (player1.equals( "" )) {

                     player1 = " - ";
                  }
                  if (player2.equals( "" )) {

                     player2 = " - ";
                  }
                  if (player3.equals( "" )) {

                     player3 = " - ";
                  }
                  if (player4.equals( "" )) {

                     player4 = " - ";
                  }
                  if (player5.equals( "" )) {

                     player5 = " - ";
                  }

                  out.println("<tr class=\"tablerow\">");
                  
                     out.println("<td>" +name+ "</td>");

                     out.println("<td>");
                     if (min < 10) {
                        out.println(mm + "/" + dd + "<br>" + hr + ":0" + min);
                     } else {
                        out.println(mm + "/" + dd + "<br>" + hr + ":" + min);
                     }
                     out.println("</td>");

                   //  if (multi != 0) {
                   //     out.println("<td>" +course+ "</td>");
                   //  }

                     out.println("<td>");

                     StringBuffer playerBfr = new StringBuffer();       // get buffer for players

                      //   Add Players
                      if (!player1.equals("")) {

                         playerBfr.append( player1 );
                      }

                      if (!player2.equals("")) {

                         playerBfr.append( "<BR>" );             // add seperator
                         playerBfr.append( player2 );
                      }

                      if (!player3.equals("")) {

                         playerBfr.append( "<BR>" );             // add seperator
                         playerBfr.append( player3 );
                      }

                      if (!player4.equals("")) {

                         playerBfr.append( "<BR>" );             // add seperator
                         playerBfr.append( player4 );
                      }

                      if (!player5.equals("")) {
                         playerBfr.append( "<BR>" );             // add seperator
                         playerBfr.append( player5 );                       
                      }

                      out.println( playerBfr.toString() );     // add the players                         

                     out.println("</td>");
                        
                     out.println("<td>");
                     if (wait == 0) {
                         out.println("Reg'd");
                     } else {
                         out.println("Wait");
                     }
                     out.println("</td>");
                  out.println("</tr>");

               }     // end of IF event date is current
            }     // end of IF event matches
            pstmte2.close();
         }        // end of WHILE evntsup

         pstmte.close();

         out.println("</table>");        // doone with events table

      } else {                                  // no events

         out.println("<div class=\"smheadertext\">No scheduled events.<BR></div>");     

      }    // end of if

   }
   catch (Exception exc) {

      SystemUtils.displayMobileError("Database Error (3).<BR><BR>If problem persists, contact your golf shop staff.", "", out);    // ouput error message
      out.close();
      return;
   }

  
  
      
   try {   
      
      //
      // ****************************************************************
      // use the name to search for lottery requests (if supported)
      // ****************************************************************
      //
      if (lottery > 0) {

         //
         //  Get the replacement text for the word "Lottery" if club requested one - added to club5 by Support Club Options
         //
         lotteryText = getClub.getLotteryText(con);         // get replacement text for "Lottery" if provided  

         if (!lotteryText.equals("")) {
            
            out.println("<BR><BR><div class=\"headertext\">Your current " +lotteryText+ "s</div>");    // output the heading
         } else {
            out.println("<BR><BR><div class=\"headertext\">Your current Tee Time Requests</div>");    // output the heading
         }
         
      
         pstmt1 = con.prepareStatement (
            "SELECT COUNT(*) " +
            "FROM lreqs3 " +
            "WHERE user1 LIKE ? OR user2 LIKE ? OR user3 LIKE ? OR user4 LIKE ? OR user5 LIKE ? OR " +
            "user6 LIKE ? OR user7 LIKE ? OR user8 LIKE ? OR user9 LIKE ? OR user10 LIKE ? OR " +
            "user11 LIKE ? OR user12 LIKE ? OR user13 LIKE ? OR user14 LIKE ? OR user15 LIKE ? OR " +
            "user16 LIKE ? OR user17 LIKE ? OR user18 LIKE ? OR user19 LIKE ? OR user20 LIKE ? OR " +
            "user21 LIKE ? OR user22 LIKE ? OR user23 LIKE ? OR user24 LIKE ? OR user25 LIKE ? " +
            "ORDER BY date, time");

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
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            count = rs.getInt(1);
         }
            
         pstmt1.close();

         //
         //  Dislay lottery requests if any for this user
         //
         if (count > 0) { 

            //
            //   build the table for the display
            //
            out.println("<div class=\"smheadertext\">To select a request:  Just click on the box containing the time (if allowed).</div>");

            out.println("<table cellpadding=\"0\" cellspacing=\"1\" border=\"1\" id=\"ltimes\">");
            out.println("<tr class=\"tableheader\">");
            
            out.println("<td><strong>Date</td>");
            out.println("<td><strong>Time</td>");
            if (multi != 0) {
               out.println("<td><strong>Course</td>");
            }
            out.println("<td><strong>Players</td>");
            out.println("</tr>");

            //
            //  Get each record and display it
            //
            pstmt1 = con.prepareStatement (
               "SELECT name, date, mm, dd, yy, day, hr, min, time, player1, player2, player3, player4, " +
               "player5, player6, player7, player8, player9, player10, player11, player12, player13, player14, " +
               "player15, player16, player17, player18, player19, player20, player21, player22, player23, " +
               "player24, player25, fb, courseName, id " +
               "FROM lreqs3 " +
               "WHERE user1 LIKE ? OR user2 LIKE ? OR user3 LIKE ? OR user4 LIKE ? OR user5 LIKE ? OR " +
               "user6 LIKE ? OR user7 LIKE ? OR user8 LIKE ? OR user9 LIKE ? OR user10 LIKE ? OR " +
               "user11 LIKE ? OR user12 LIKE ? OR user13 LIKE ? OR user14 LIKE ? OR user15 LIKE ? OR " +
               "user16 LIKE ? OR user17 LIKE ? OR user18 LIKE ? OR user19 LIKE ? OR user20 LIKE ? OR " +
               "user21 LIKE ? OR user22 LIKE ? OR user23 LIKE ? OR user24 LIKE ? OR user25 LIKE ? " +
               "ORDER BY date, time");

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
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               lname = rs.getString(1);
               date = rs.getLong(2);
               mm = rs.getInt(3);
               dd = rs.getInt(4);
               yy = rs.getInt(5);
               day = rs.getString(6);
               hr = rs.getInt(7);
               min = rs.getInt(8);
               time = rs.getInt(9);
               player1 = rs.getString(10);
               player2 = rs.getString(11);
               player3 = rs.getString(12);
               player4 = rs.getString(13);
               player5 = rs.getString(14);
               player6 = rs.getString(15);
               player7 = rs.getString(16);
               player8 = rs.getString(17);
               player9 = rs.getString(18);
               player10 = rs.getString(19);
               player11 = rs.getString(20);
               player12 = rs.getString(21);
               player13 = rs.getString(22);
               player14 = rs.getString(23);
               player15 = rs.getString(24);
               player16 = rs.getString(25);
               player17 = rs.getString(26);
               player18 = rs.getString(27);
               player19 = rs.getString(28);
               player20 = rs.getString(29);
               player21 = rs.getString(30);
               player22 = rs.getString(31);
               player23 = rs.getString(32);
               player24 = rs.getString(33);
               player25 = rs.getString(34);
               fb = rs.getInt(35);
               course = rs.getString(36);
               lottid = rs.getLong(37);

               ampm = " AM";
               if (hr == 12) {
                  ampm = " PM";
               }
               if (hr > 12) {
                  ampm = " PM";
                  hr = hr - 12;    // convert to conventional time
               }

               //
               //  Get shorter value for day
               //
               if (day.equals( "Sunday" )) {
                  day_short = "Sun";
               } else if (day.equals( "Monday" )) {
                  day_short = "Mon";
               } else if (day.equals( "Tuesday" )) {
                  day_short = "Tue";
               } else if (day.equals( "Wednesday" )) {
                  day_short = "Wed";
               } else if (day.equals( "Thursday" )) {
                  day_short = "Thur";
               } else if (day.equals( "Friday" )) {
                  day_short = "Fri";
               } else if (day.equals( "Saturday" )) {
                  day_short = "Sat";               
               }

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
               if (fivesomes != 0) {                      // if 5-somes supported for any courses 

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

               //
               //  get the slots value and determine the current state for this lottery
               //
               PreparedStatement pstmt7d = con.prepareStatement (
                  "SELECT sdays, sdtime, edays, edtime, pdays, ptime, slots " +
                  "FROM lottery3 WHERE name = ?");

               pstmt7d.clearParameters();          // clear the parms
               pstmt7d.setString(1, lname);

               rs2 = pstmt7d.executeQuery();      // find all matching lotteries, if any

               if (rs2.next()) {

                  sdays = rs2.getInt(1);         // days in advance to start taking requests
                  sdtime = rs2.getInt(2);
                  edays = rs2.getInt(3);         // ...stop taking reqs
                  edtime = rs2.getInt(4);
                  pdays = rs2.getInt(5);         // ....to process reqs
                  ptime = rs2.getInt(6);
                  slots = rs2.getInt(7);

               }                  // end of while
               pstmt7d.close();

               //
               //    Determine which state we are in (before req's, during req's, before process, after process)
               //
               //  Get the current time
               //
               Calendar cal3 = new GregorianCalendar();    // get the current time of the day

               if (zone.equals( "Eastern" )) {         // Eastern Time = +1 hr

                  cal3.add(Calendar.HOUR_OF_DAY,1);         // roll ahead 1 hour (rest should adjust)
               }

               if (zone.equals( "Mountain" )) {        // Mountain Time = -1 hr

                  cal3.add(Calendar.HOUR_OF_DAY,-1);        // roll back 1 hour (rest should adjust)
               }

               if (zone.equals( "Pacific" )) {         // Pacific Time = -2 hrs

                  cal3.add(Calendar.HOUR_OF_DAY,-2);        // roll back 2 hours (rest should adjust)
               }

               int cal_hour = cal3.get(Calendar.HOUR_OF_DAY);  // 00 - 23 (military time - adjusted for time zone)
               cal_min = cal3.get(Calendar.MINUTE);
               int curr_time = cal_hour * 100;
               curr_time = curr_time + cal_min;                // create military time

               //
               //  determine the number of days in advance of the req'd tee time we currently are
               //
               int cal_yy = cal3.get(Calendar.YEAR);
               int cal_mm = cal3.get(Calendar.MONTH);
               int cal_dd = cal3.get(Calendar.DAY_OF_MONTH);

               cal_mm++;                            // month starts at zero
               advance_days = 0;

               while (cal_mm != mm || cal_dd != dd || cal_yy != yy) {

                  cal3.add(Calendar.DATE,1);                // roll ahead 1 day untill a match found

                  cal_yy = cal3.get(Calendar.YEAR);
                  cal_mm = cal3.get(Calendar.MONTH);
                  cal_dd = cal3.get(Calendar.DAY_OF_MONTH);

                  cal_mm++;                            // month starts at zero
                  advance_days++;
               }

               //
               //  now check the day and time values
               //
               if (advance_days > sdays) {       // if we haven't reached the start day yet

                  lstate = 1;                    // before time to take requests

               } else {

                  if (advance_days == sdays) {   // if this is the start day

                     if (curr_time >= sdtime) {   // have we reached the start time?

                        lstate = 2;              // after start time, before stop time to take requests

                     } else {

                        lstate = 1;              // before time to take requests
                     }
                  } else {                        // we are past the start day

                     lstate = 2;                 // after start time, before stop time to take requests
                  }

                  if (advance_days == edays) {   // if this is the stop day

                     if (curr_time >= edtime) {   // have we reached the stop time?

                        lstate = 3;              // after start time, before stop time to take requests
                     }
                  }

                  if (advance_days < edays) {   // if we are past the stop day

                     lstate = 3;                // after start time, before stop time to take requests
                  }
               }

               if (lstate == 3) {                // if we are now in state 3, check for state 4

                  if (advance_days == pdays) {   // if this is the process day

                     if (curr_time >= ptime) {    // have we reached the process time?

                        lstate = 4;              // after process time
                     }
                  }

                  if (advance_days < pdays) {   // if we are past the process day

                     lstate = 4;                // after process time
                  }
               }

               if (lstate == 4) {                // if we are now in state 4, check for state 5

                  PreparedStatement pstmt12 = con.prepareStatement (
                         "SELECT mm FROM lreqs3 " +
                         "WHERE name = ? AND date = ? AND courseName = ? AND state = 2");

                  pstmt12.clearParameters();        // clear the parms
                  pstmt12.setString(1, lname);
                  pstmt12.setLong(2, date);
                  pstmt12.setString(3, course);
                  rs2 = pstmt12.executeQuery();

                  if (!rs2.next()) {             // if none waiting approval

                     lstate = 5;                // state 5 - after process & approval time
                  }
                  pstmt12.close();
               }

               submit = "time:" + fb;       // create a name for the submit button

               //
               //  Build the HTML for each record found
               //
               out.println("<tr class=\"tablerow\">");

               if (lstate == 2) {       // if still ok to process lottery requests

                  out.println("<form action=\"Member_lott\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                  out.println("<input type=\"hidden\" name=\"day\" value=" + day + ">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                  out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lname + "\">");
                  out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
                  out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
                  out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");

                  if ((fives != 0 ) && (rest.equals( "" ))) {                // if 5-somes and not restricted
                     out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
                  } else {
                     out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                  }

                  out.println("<input type=\"hidden\" name=\"index\" value=995>");  // indicate from teelist
                  
                  out.println("<td>");
                  out.println( day_short + "<BR>" + mm + "/" + dd + "/" + yy );
                  out.println("</td>");

                  out.println("<td>");
                     if (min < 10) {
                        out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":0" + min + ampm + "\" >");
                     } else {
                        out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + min + ampm + "\">");
                     }
                  out.println("</td>");

               } else {

                  if (lstate == 5 && noAccessAfterLottery == false) {       // if lottery has already been processed and ok to access tee time

                     out.println("<form action=\"Member_slot\" method=\"post\" target=\"_top\">");
                     out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                     out.println("<input type=\"hidden\" name=\"day\" value=" + day + ">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                     out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lname + "\">");
                     out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
                     out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
                     out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");

                     if ((fives != 0 ) && (rest.equals( "" ))) {           // if 5-somes and not restricted
                        out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
                     } else {
                        out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                     }

                     out.println("<input type=\"hidden\" name=\"index\" value=995>");  // indicate from teelist
                     
                     out.println("<td>");
                        out.println( day + "<BR>" + mm + "/" + dd );
                        out.println("</td>");

                     out.println("<td>");
                        if (min < 10) {
                           out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":0" + min + ampm + "\">");
                        } else {
                           out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + min + ampm + "\">");
                        }
                     out.println("</td>");

                  } else {

   //                  out.println("<form action=\"Member_slot\" method=\"get\">");  // form with no submit
                     out.println("<td>");
                        out.println( day + "<BR>" + mm + "/" + dd + "/" + yy );
                        out.println("</td>");

                     out.println("<td>");
                        if (min < 10) {
                           out.println(hr + ":0" + min + ampm);
                        } else {
                           out.println(hr + ":" + min + ampm);
                        }
                     out.println("</td>");
                  }
               }

               if (multi != 0) {
                  out.println("<td>");
                  out.println(course);
                  out.println("</td>");
               }

               out.println("<td>");

               StringBuffer playerBfr = new StringBuffer();       // get buffer for players

                //   Add Players
                if (!player1.equals("")) {
                   playerBfr.append( player1 );
                }

                if (!player2.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player2 );
                }

                if (!player3.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player3 );
                }

                if (!player4.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player4 );
                }

                if (!player5.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player5 );                       
                }

                if (!player6.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player6 );                       
                }

                if (!player7.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player7 );                       
                }

                if (!player8.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player8 );                       
                }

                if (!player9.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player9 );                       
                }

                if (!player10.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player10 );                       
                }

                if (!player11.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player11 );
                }

                if (!player12.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player12 );
                }

                if (!player13.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player13 );
                }

                if (!player14.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player14 );
                }

                if (!player15.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player15 );                       
                }

                if (!player16.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player16 );                       
                }

                if (!player17.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player17 );                       
                }

                if (!player18.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player18 );                       
                }

                if (!player19.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player19 );                       
                }

                if (!player20.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player20 );                       
                }

                if (!player21.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player21 );
                }

                if (!player22.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player22 );
                }

                if (!player23.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player23 );
                }

                if (!player24.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player24 );
                }

                if (!player25.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player25 );                       
                }

                out.println( playerBfr.toString() );     // add the players                         

               out.println("</td>");
                        
               out.println("</form></tr>");

            }    // end of while

            pstmt1.close();

            out.println("</table>");
            

         } else {     // No lottery requests for this user
            
            if (!lotteryText.equals("")) {
               out.println("<div class=\"smheadertext\">You are currently not included in any " +lotteryText+ "s at this time.</div>");
            } else {
               out.println("<div class=\"smheadertext\">You are currently not included in any Tee Time Requests at this time.</div>");
            }
         }
         
       }         // end of IF lottery defined for this club

   }
   catch (Exception exc) {

      SystemUtils.displayMobileError("Database Error (4).<BR><BR>If problem persists, contact your golf shop staff.", "", out);    // ouput error message
      out.close();
      return;
   }

    
   try {
      
        //
        // Check for any wait list signups
        //
        date = SystemUtils.getDate(con);
        
        PreparedStatement pstmt = con.prepareStatement(
                "SELECT wl.wait_list_id, wl.course, wls.date, wls.ok_stime, wls.ok_etime, wls.wait_list_signup_id, " +
                "DATE_FORMAT(wls.date, '%m/%d/%Y') AS date2, DATE_FORMAT(wls.date, '%W') AS day_name, DATE_FORMAT(wls.date, '%Y%m%d') AS dateymd " + 
                "FROM wait_list_signups wls " +
                "LEFT OUTER JOIN wait_list wl ON wls.wait_list_id = wl.wait_list_id " +
                "LEFT OUTER JOIN wait_list_signups_players wlp ON wlp.wait_list_signup_id = wls.wait_list_signup_id " +
                "WHERE wls.date >= ? AND wlp.username = ? AND converted = 0 " +
                "ORDER BY wls.date");
        
        pstmt.clearParameters();
        pstmt.setLong(1, date);
        pstmt.setString(2, user);
        
        rs = pstmt.executeQuery();
        
        boolean found = false;
        
        if (rs.next()) {
            
            found = true;
        }
        pstmt.close();
        
        if (found == true) {      // if we found some
            
            out.println("<BR><BR><div class=\"headertext\">Current Wait List Sign-ups</div>");    // output the heading
      
            out.println("<div class=\"smheadertext\">");     
            out.println("Display Only - no access on Mobile.");
            out.println("</div>");

            out.println("<table cellpadding=\"0\" cellspacing=\"1\" border=\"1\" id=\"wtimes\">");
            out.println("<tr class=\"tableheader\">");

            out.println("<td><strong>Date</td>");
            out.println("<td><strong>Time</td>");
            if (multi != 0) {
               out.println("<td><strong>Course</td>");
            }
            out.println("<td><strong>Players</td>");
            out.println("</tr>");

           //
           // run the query again to get all entries
           //
           pstmt.clearParameters();
           pstmt.setLong(1, date);
           pstmt.setString(2, user);

           rs = pstmt.executeQuery();

           while (rs.next()) {

               player1 = "&nbsp;"; player2 = "&nbsp;"; player3 = "&nbsp;"; player4 = "&nbsp;"; player5 = "&nbsp;";

               PreparedStatement pstmtp = con.prepareStatement(
                   "SELECT player_name " + 
                   "FROM wait_list_signups_players " + 
                   "WHERE wait_list_signup_id = ? " + 
                   "ORDER BY pos");

               pstmtp.clearParameters();
               pstmtp.setInt(1, rs.getInt("wait_list_signup_id"));
               ResultSet rsp = pstmtp.executeQuery();

               if (rsp.next()) player1 = rsp.getString(1);
               if (rsp.next()) player2 = rsp.getString(1);
               if (rsp.next()) player3 = rsp.getString(1);
               if (rsp.next()) player4 = rsp.getString(1);
               if (rsp.next()) player5 = rsp.getString(1);

               pstmtp.close();

               out.println("<tr class=\"tablerow\">");
               out.print("<td>");      // date col
               out.print(rs.getString("date2"));
               out.println("</td>");

               out.print("<td>");      // time col
               out.print(SystemUtils.getSimpleTime(rs.getInt("ok_stime")) + " - " + SystemUtils.getSimpleTime(rs.getInt("ok_etime")));
               out.println("</td>");

               if (multi != 0) {
                   out.print("<td>");   // course
                   out.print(rs.getString("course"));
                   out.println("</td>");
               }

               out.println("<td>");

                StringBuffer playerBfr = new StringBuffer();       // get buffer for players

                //   Add Players
                if (!player1.equals("")) {
                   playerBfr.append( player1 );
                }

                if (!player2.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player2 );
                }

                if (!player3.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player3 );
                }

                if (!player4.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player4 );
                }

                if (!player5.equals("")) {
                   playerBfr.append( "<BR>" );             // add seperator
                   playerBfr.append( player5 );                       
                }

                out.println( playerBfr.toString() );     // add the players                         

               out.println("</td>");

               out.println("</tr>");

           }

           pstmt.close();

           out.println("</table>");
           
        }       // end of IF an wait lists
      
      // 
      //  Done with all lists
      //
      out.println("<BR></div>");           // end of main page

      out.println("<div class=\"content\"><ul>");      
      out.println("<li><a href=\"/" +rev+ "/mobile/member_mobile_home.html\">Return</a></li></ul>");
      out.println("</div></body></html>");   
      out.close();

   }
   catch (Exception exc) {

      SystemUtils.displayMobileError("Database Error (5).<BR><BR>If problem persists, contact your golf shop staff.", "", out);    // ouput error message
      out.close();
      return;
   }

 }   // end of doGet

}
