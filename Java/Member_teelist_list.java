/***************************************************************************************
 *   Member_teelist_list:  This servlet will search teecurr for this member and display
 *                         a list of current tee times.  Also, search evntsup for any
 *                         events and lreqs for any lottery requests.
 *
 *
 *   called by:  memu item & member_teemain2.htm
 *
 *   created: 1/10/2002   Bob P.
 *
 *   last updated:
 *
 *        5/18/10    Fix noAccessAfterLottery custom processing to check for lottery_color in teecurr to determine if lottery processed (case 1827).
 *        4/26/10    Brae Burn - do not allow members to access tee times after the lottery has been processed (case 1827).
 *        4/20/10    Bonnie Briar - do not allow members to access tee times after the lottery has been processed (case 1822).
 *        4/16/10    Wollaston GC - do not allow members to access tee times on Monday - all day (case 1819).
 *       12/09/09    When looking for events only check those that are active.
 *        9/22/09    Include scheduled activities - also we now hide things that are empty (no tee times, lotteries, etc)
 *        9/02/09    Do not include lottery tee times that have been pre-booked before lottery has been approved (case 1703).
 *        7/24/09    Add check for mobile user and route to Member_teelist_mobile if so.
 *        4/30/09    Added Status field to event signup display to show if the member is Registered or on the Wait List (case 1587).
 *       10/23/08    Added Wait List signups to list
 *       10/03/08    Check for replacement text for the word "Lottery" when email is for a lottery request.
 *        5/28/08    Do not allow member to access a tee time from calendar if cutoff date/time reached.
 *        3/26/08    Do not allow member to select a tee time that has already passed (case 1431).
 *        7/17/07    Include tee times that were originated by the user in the tee time list.
 *        1/05/07    Changes for TLT system - display notifications
 *       11/20/06    Do not use p5rest parm, only p5 for tee times and lottery requests.
 *        7/11/06    If tee time is during a shotgun event, do not allow link and show as shotgun time.
 *        5/25/05    Recreated from V4 Member_teelist to allow members to list their tee
 *                   times, etc. the old way.
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
import com.foretees.common.getActivity;
import com.foretees.common.verifyCustom;


public class Member_teelist_list extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmte = null;
   PreparedStatement pstmte2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rsev = null;

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }
   
   int mobile = 0;

   //
   //  See if Mobile user
   //
   try {      
      mobile = (Integer)session.getAttribute("mobile");        
   }
   catch (Exception ignore) {   
      mobile = 0;
   }
      
   //
   //   Check for Mobile user and route to proper servlet if so
   //
   if (mobile > 0) {        // if mobile user

      Member_teelist_mobile teelist_mobile = new Member_teelist_mobile();      // create an instance of Member_teelist_mobile so we can call it (static vs non-static)

      teelist_mobile.doGet(req, resp);             // call 'doGet' method in Memebr_teelist_mobile
      return;                                      // exit  
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
      out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   // See what activity mode we are in
   //
   int sess_activity_id = 0;

   try { sess_activity_id = (Integer)session.getAttribute("activity_id"); }
   catch (Exception ignore) { }

   //
   // See is activities are configured for this club
   //
   //boolean activities_enabled = getActivity.isConfigured(con);

   boolean found = false;
        
   //String omit = "";
   String ampm = "";
   String day = "";
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
   String caller = (String)session.getAttribute("caller");      // get caller

   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;
   
   boolean events = false;
   boolean allRest = false;
   boolean cutoff = false;
   boolean restrictAllTees = false;         // restrict all tee times


   //
   //  boolean for clubs that want to block member access to tee times after a lottery has been processed.
   //
   //  NOTE:  see same flag in Member_teelist and Member_sheet and Member_teelist_mobile !!!!!!!!!!!!!!!!!!
   //
   boolean noAccessAfterLottery = false;

   if (club.equals( "bonniebriar" ) || club.equals("braeburncc") ) {   // add other clubs here!!
       
       noAccessAfterLottery = true;      // no member access after lottery processed
   }
   
   
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);


   //
   //   Get options for this club
   //
   try {

      //
      // Get the days in advance and time for advance from the club db
      //
      getClub.getParms(con, parm, sess_activity_id);        // get the club parms

      multi = parm.multi;
      lottery = parm.lottery;
      zone = parm.adv_zone;
        

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT SUM(fives) FROM clubparm2");           // check all courses for 5-somes

      if ( rs.next() ) {

         if (rs.getInt(1) > 0) fivesomes = 1;

      }

   } catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Member My Tee Times - Error"));
      out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
      out.println("<CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your golf shop (provide this information).");
      out.println("<br><br><a href=\"/" +rev+ "/servlet/Member_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

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
   
   //String mysql_date = yy + "-" + SystemUtils.ensureDoubleDigit(mm) + "-" + SystemUtils.ensureDoubleDigit(dd);

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
   //  Wollaston GC - if today is Monday, then do not allow access to any tee times, no matter what day the tee sheet is for (case 1819). 
   //
   if (club.equals( "wollastongc" )) {

      restrictAllTees = verifyCustom.checkWollastonMon();         // restrict all day if today is Monday
   }
   

  //
  //   build the HTML page for the display
  //
  out.println(SystemUtils.HeadTitle(""));
  out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
  SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
  out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

  out.println("<script type=\"text/javascript\">");
  out.println("function editNotify(pId, pTime) {");
  out.println(" var f = document.forms['frmEditNotify'];");
  out.println(" f.notifyId.value = pId;");
  out.println(" f.stime.value = pTime;");
  out.println(" f.submit();");
  out.println("}");
  out.println("</script>");
  
  out.println("<script type=\"text/javascript\">");
  out.println("function gotoEvent(name, index) {");
  out.println(" top.document.location.href=\"/" + rev + "/servlet/Member_events2?name=\"+name+\"&index=\"+index;");
  out.println("}");
  out.println("</script>");
    
  out.println("<form method=post action=/" + rev + "/servlet/MemberTLT_slot name=frmEditNotify id=frmEditNotify target=_top>");
  out.println("<input type=hidden name=notifyId value=\"\">");
  out.println("<input type=hidden name=stime value=\"\">");
  out.println("<input type=hidden name=index value=\"995\">");
  out.println("</form>");
  
  out.println("<table border=\"0\" align=\"center\" valign=\"top\">");   // table for main page
  out.println("<tr><td align=\"center\"><BR>");

  
  
  //***********************************************************
  //  Check for Golf events if in that mode
  //***********************************************************  
  
  //
  // First display either notifications or tee times
  //
  if (parm.foretees_mode != 0 && sess_activity_id == 0) {    // if Golf defined and this user is under Golf

     if (IS_TLT) {


          try {

              pstmt = con.prepareStatement (
                   "SELECT n.notification_id, " +
                      "DATE_FORMAT(n.req_datetime, '%W, %b. %D') AS pretty_date, " +
                      "DATE_FORMAT(n.req_datetime, '%l:%i %p') AS pretty_time " +
                   "FROM notifications n, notifications_players np " +
                   "WHERE n.notification_id = np.notification_id " +
                      "AND np.username = ? " +
                      "AND DATE(n.req_datetime) >= DATE(now()) " +
                   "ORDER BY n.req_datetime");

              pstmt.clearParameters();        // clear the parms
              pstmt.setString(1, user);
              rs = pstmt.executeQuery();

              // if we found any then output the header row.
              rs.last();
              if (rs.getRow() > 0) {

                  found = true;

                  out.println("<font size=\"3\">");
                  out.println("<b>Your current notifications</b></font><br>");
                  out.println("<font size=\"2\"><br>");

              }

              rs.beforeFirst();

              while (rs.next()) {

                  out.println("<a href=\"javascript:editNotify(" + rs.getInt("notification_id") + ", '" + rs.getString("pretty_time") + "')\"><font color=darkGreen>" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</font></a><br>");
              }

          } catch (Exception exc) {

             out.println(SystemUtils.HeadTitle("Database Error"));
             out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
             out.println("<BR><BR><H3>Database Access Error</H3>");
             out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
             out.println("<BR>Error:" + exc.getMessage());
             out.println("<BR><BR>Please try again later.");
             out.println("<BR><BR>If problem persists, contact your club manager.");
             out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Member_announce\">Home</a>");
             out.println("</CENTER></BODY></HTML>");
             out.close();

          } finally {

               try { rs.close(); }
               catch (Exception ignore) {}

               try { pstmt.close(); }
               catch (Exception ignore) {}

          }

     } else {

         //
         // Not a notification club
         //

         try {
            //
            // search for this user's tee times
            //
            PreparedStatement pstmt1 = con.prepareStatement (
               "SELECT date, mm, dd, yy, day, hr, min, time, event, player1, player2, player3, player4, event_type, fb, " +
               "player5, lottery, courseName, rest5, rest5_color, lottery_color " +
               "FROM teecurr2 " +
               "WHERE username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? " +
               "OR username5 = ? OR userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ? OR orig_by = ? " +
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

            // if we found any then output the header row.
            rs.last();
            if (rs.getRow() > 0) {

               found = true;

               out.println("<font size=\"3\">");
               out.println("<b>Your current Tee Times</b></font>");
               out.println("<font size=\"2\"><br>");

               out.println("<b>To select a tee time</b>:  Just click on the box containing the time (2nd column).");
               out.println("<br></font><font size=\"1\">");
               out.println("F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;F = Front 9, &nbsp;&nbsp;B = Back 9, &nbsp;&nbsp;O = Other<br>");
               out.println("</font></td>");

               out.println("</tr><tr>");
               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"7\" valign=\"top\">");
                 out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                 out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
                       out.println("<font color=\"#ffffff\" size=\"2\">");
                       out.println("<u><b>Date</b></u>");
                       out.println("</font></td>");

                    out.println("<td align=\"center\">");
                       out.println("<font color=\"#ffffff\" size=\"2\">");
                       out.println("<u><b>Time</b></u>");
                       out.println("</font></td>");

                    if (multi != 0) {

                       out.println("<td align=\"center\">");
                       out.println("<font color=\"#ffffff\" size=\"2\">");
                       out.println("<u><b>Course Name</b></u>");
                       out.println("</font></td>");
                    }

                    out.println("<td align=\"center\">");
                       out.println("<font color=\"#ffffff\" size=\"2\">");
                       out.println("<u><b>F/B</b></u>");
                       out.println("</font></td>");

                    out.println("<td align=\"center\">");
                       out.println("<font color=\"#ffffff\" size=\"2\">");
                       out.println("<u><b>Player 1</b></u>");
                       out.println("</font></td>");

                    out.println("<td align=\"center\">");
                       out.println("<font color=\"#ffffff\" size=\"2\">");
                       out.println("<u><b>Player 2</b></u>");
                       out.println("</font></td>");

                    out.println("<td align=\"center\">");
                       out.println("<font color=\"#ffffff\" size=\"2\">");
                       out.println("<u><b>Player 3</b></u>");
                       out.println("</font></td>");

                    out.println("<td align=\"center\">");
                       out.println("<font color=\"#ffffff\" size=\"2\">");
                       out.println("<u><b>Player 4</b></u>");
                       out.println("</font></td>");

                    if (fivesomes != 0) {

                       out.println("<td align=\"center\">");
                       out.println("<font color=\"#ffffff\" size=\"2\">");
                       out.println("<u><b>Player 5</b></u>");
                       out.println("</font></td>");
                    }
                 out.println("</tr>");

            }

            rs.beforeFirst();


           //
           //  Get each additional record and display it
           //
           while (rs.next()) {

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
              allRest = getRests.checkRests(date, time, fb, course, day, con);

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
              out.println("<tr>");
              out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
              out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
              out.println("<input type=\"hidden\" name=\"day\" value=" + day + ">");
              out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
              out.println("<input type=\"hidden\" name=\"index\" value=\"995\">");  // indicate from here

              //
              //  check if 5-somes allowed on this course
              //
              PreparedStatement pstmt3 = con.prepareStatement (
                 "SELECT fives FROM clubparm2 WHERE courseName = ?");

              pstmt3.clearParameters();        // clear the parms
              pstmt3.setString(1, course);
              rs2 = pstmt3.executeQuery();      // execute the prepared pstmt3

              if (rs2.next()) {

                 fives = rs2.getInt(1);

                 if ((fives != 0 ) && (rest5.equals( "" ))) {   // if 5-somes and not restricted
                    out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
                 } else {
                    out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                 }
              }

              pstmt3.close();

              out.println("<td align=\"center\">");
              out.println("<font size=\"2\">");
              out.println( day + "&nbsp;" + mm + "/" + dd + "/" + yy );
              out.println("</font></td>");

              out.println("<td align=\"center\">");
              out.println("<font size=\"2\">");

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

                 out.println("Shotgun at " +stime2);

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

                    if (lstate < 5 || noAccessAfterLottery == true) {    // if lottery not approved OR access not allowed after approval

                       cutoff = true;        // do not allow access to this tee time (pre-booked lottery time)
                    }
                        
                 } else {

                     if (!lottery_color.equals("") && noAccessAfterLottery == true) {   // if it was a lottery time and access not allowed after processed

                        cutoff = true;                    // do not allow access to this tee time
                     }
                 }


                 if (restrictAllTees == true) {    // if all tee times are restricted

                    cutoff = true;        // do not allow access to this tee time
                 }
   

                 //
                 //  Display time button or just the time
                 //
                 if (allRest == false && cutoff == false && !(date == cdate && time <= ctime)) {     // if mem can edit tee time
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
              out.println("</font></td>");

              if (multi != 0) {

                 out.println("<td align=\"center\">");
                 out.println("<font size=\"2\">");
                 out.println(course);
                 out.println("</font></td>");
              }

              out.println("<td align=\"center\">");
              out.println("<font size=\"2\">");
              out.println(sfb);
              out.println("</font></td>");

              out.println("<td bgcolor=\"#FFFFFF\">");
              out.println("<font size=\"2\">");
              if (player1.equals( "" )) {

                 out.println("<p align=\"center\">&nbsp;</p>");         // don't put 'null' in table (omits border)
              } else {

                 out.println("<p align=\"center\">" + player1 + "</p>");
              }
              out.println("</font></td>");

              out.println("<td bgcolor=\"#FFFFFF\">");
              out.println("<font size=\"2\">");
              if (player2.equals( "" )) {

                 out.println("<p align=\"center\">&nbsp;</p>");
              } else {

                 out.println("<p align=\"center\">" + player2 + "</p>");
              }
              out.println("</font></td>");

              out.println("<td bgcolor=\"#FFFFFF\">");
              out.println("<font size=\"2\">");
              if (player3.equals( "" )) {

                 out.println("<p align=\"center\">&nbsp;</p>");
              } else {

                 out.println("<p align=\"center\">" + player3 + "</p>");
              }
              out.println("</font></td>");

              out.println("<td bgcolor=\"#FFFFFF\">");
              out.println("<font size=\"2\">");
              if (player4.equals( "" )) {

                 out.println("<p align=\"center\">&nbsp;</p>");
              } else {

                 out.println("<p align=\"center\">" + player4 + "</p>");
              }
              out.println("</font></td>");

              if (fivesomes != 0) {

                 if (rest5_color.equals( "" )) {

                    out.println("<td bgcolor=\"#FFFFFF\">");
                 } else {
                    out.println("<td bgcolor=\"" + rest5_color + "\">");
                 }
                 out.println("<font size=\"2\">");
                 if (player5.equals( "" )) {

                    out.println("<p align=\"center\">&nbsp;</p>");
                 } else {

                    out.println("<p align=\"center\">" + player5 + "</p>");
                 }
                 out.println("</font></td>");
              }
              out.println("</form></tr>");

           }    // end of while

           out.println("</font></table>");

           pstmt1.close();

         } catch (Exception exc) {

            out.println(SystemUtils.HeadTitle("Database Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
            out.println("<BR><BR><H3>Database Access Error</H3>");
            out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
            out.println("<BR>Error:" + exc.getMessage());
            out.println("<BR><BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact your club manager.");
            out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Member_announce\">Home</a>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
         }

      } // end if notification or tee times


       //
       //*****************************************************************
       // now check for any Golf events that member is signed up for
       //*****************************************************************
       //
       try {

         pstmte2 = con.prepareStatement (
            "SELECT name, courseName " +
            "FROM events2b WHERE date >= ? AND activity_id = 0 AND inactive = 0");       // look for Golf events

         pstmte2.clearParameters();       
         pstmte2.setLong(1, cdate);
         rs2 = pstmte2.executeQuery();     

         loop1:
         while (rs2.next()) {

            name = rs2.getString(1);
            course = rs2.getString(2);
          
            // check for signups
            pstmte = con.prepareStatement (
               "SELECT name " +
               "FROM evntsup2b WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? " +
               "OR username5 = ?) AND name = ? AND courseName = ? AND inactive = 0");

            pstmte.clearParameters();        // clear the parms
            pstmte.setString(1, user);
            pstmte.setString(2, user);
            pstmte.setString(3, user);
            pstmte.setString(4, user);
            pstmte.setString(5, user);
            pstmte.setString(6, name);
            pstmte.setString(7, course);
            rs = pstmte.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               events = true;
            }
            pstmte.close();

            if (events == true) {

               break loop1;
            }
         }        // end of WHILE evntsup
         pstmte2.close();

         //
         //  if any events found above, then display them here (search over again)
         //
         if (events == true) {

            found = true;

            out.println("<br><br><font size=\"3\">");
            out.println("<b>Events for which you are currently registered:</b></font>");
            out.println("<font size=\"2\"><br>");

            //
            //  Build html to display any events
            //
            out.println("<b>To select the event</b>:  Just click on the box containing the event name.<br>");
            out.println("If you cannot select the event, then it is currently past the sign-up date.<br>");
            out.println("</font></td>");
            out.println("</tr><tr>");
            out.println("<td>");
            out.println("<font size=\"2\">");
               out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"7\" align=\"center\" valign=\"top\">");
               out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<u><b>Event Name</b></u>");
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<u><b>Date & Time</b></u>");
               out.println("</font></td>");

               if (multi != 0) {

                  out.println("<td align=\"center\">");
                  out.println("<font color=\"#ffffff\" size=\"2\">");
                  out.println("<u><b>Course Name</b></u>");
                  out.println("</font></td>");
               }

               out.println("<td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<u><b>Player 1</b></u>");
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<u><b>Player 2</b></u>");
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<u><b>Player 3</b></u>");
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<u><b>Player 4</b></u>");
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<u><b>Player 5</b></u>");
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<u><b>Status</b></u>");
               out.println("</font></td>");

               out.println("</tr>");

            //
            // now list the Golf events that member is signed up for
            //
            pstmte2 = con.prepareStatement (
               "SELECT name, courseName, date, year, month, day, act_hr, act_min, signUp, c_date, c_time " +
               "FROM events2b WHERE date >= ? AND activity_id = 0 AND inactive = 0");       // look for Golf events

            pstmte2.clearParameters();        // clear the parms
            pstmte2.setLong(1, cdate);
            rs2 = pstmte2.executeQuery();      // execute the prepared stmt

            while (rs2.next()) {
               
               name = rs2.getString(1);
               course = rs2.getString(2);
               edate = rs2.getLong(3);
               yy = rs2.getInt(4);
               mm = rs2.getInt(5);
               dd = rs2.getInt(6);
               hr = rs2.getInt(7);
               min = rs2.getInt(8);
               signUp = rs2.getInt(9);
               c_date = rs2.getLong(10);
               c_time = rs2.getInt(11);
               
               
               //
               //  Now look for signups in this event
               //
               pstmte = con.prepareStatement (
                  "SELECT player1, player2, player3, player4, player5, wait " +
                  "FROM evntsup2b WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? " +
                  "OR username5 = ?) AND name = ? AND courseName = ? AND inactive = 0 ORDER BY c_date, c_time");

               pstmte.clearParameters();        // clear the parms
               pstmte.setString(1, user);
               pstmte.setString(2, user);
               pstmte.setString(3, user);
               pstmte.setString(4, user);
               pstmte.setString(5, user);
               pstmte.setString(6, name);
               pstmte.setString(7, course);
               rs = pstmte.executeQuery();      // execute the prepared stmt

               while (rs.next()) {

                  player1 = rs.getString(1);
                  player2 = rs.getString(2);
                  player3 = rs.getString(3);
                  player4 = rs.getString(4);
                  player5 = rs.getString(5);
                  wait = rs.getInt(6);

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

                     out.println("<form action=\"/" +rev+ "/servlet/Member_events2\" method=\"post\" target=\"bot\">");
                     out.println("<tr>");
                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");

                        if (signUp != 0 && c_date >= cdate) {     // if members can sign up for this event

                           if ((c_date > cdate) || (c_time > ctime)) {

                              out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                              out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                              out.println("<input type=\"hidden\" name=\"index\" value=\"995\">");  // indicate from here
                              out.println("<input type=\"submit\" value=\"" + name + "\">");

                           } else {

                              out.println( name );
                           }
                        } else {

                           out.println( name );
                        }
                        out.println("</font></td>");
                        out.println("</form>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        if (min < 10) {
                           out.println(mm + "/" + dd + "/" + yy + "<br>" + hr + ":0" + min + " " + ampm);
                        } else {
                           out.println(mm + "/" + dd + "/" + yy + "<br>" + hr + ":" + min + " " + ampm);
                        }
                        out.println("</font></td>");

                     if (multi != 0) {

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println( course );
                        out.println("</font></td>");
                     }

                        out.println("<td align=\"center\" bgcolor=\"#FFFFFF\">");
                        out.println("<font size=\"2\">");
                        out.println( player1 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"#FFFFFF\">");
                        out.println("<font size=\"2\">");
                        out.println( player2 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"#FFFFFF\">");
                        out.println("<font size=\"2\">");
                        out.println( player3 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"#FFFFFF\">");
                        out.println("<font size=\"2\">");
                        out.println( player4 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\" bgcolor=\"#FFFFFF\">");
                        out.println("<font size=\"2\">");
                        out.println( player5 );
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        if (wait == 0) {
                            out.println("Registered");
                        } else {
                            out.println("Wait List");
                        }
                        out.println("</font></td>");
                     out.println("</tr>");

                  }     // end of IF event date is current
                  
               }     // end of WHILE signups
               pstmte.close();
               
            }        // end of WHILE events

            pstmte2.close();

            out.println("</font></table>");        // done with events table
            
         }    // end of IF any events


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


            PreparedStatement pstmtl = con.prepareStatement (
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

            pstmtl.clearParameters();        // clear the parms
            pstmtl.setString(1, user);
            pstmtl.setString(2, user);
            pstmtl.setString(3, user);
            pstmtl.setString(4, user);
            pstmtl.setString(5, user);
            pstmtl.setString(6, user);
            pstmtl.setString(7, user);
            pstmtl.setString(8, user);
            pstmtl.setString(9, user);
            pstmtl.setString(10, user);
            pstmtl.setString(11, user);
            pstmtl.setString(12, user);
            pstmtl.setString(13, user);
            pstmtl.setString(14, user);
            pstmtl.setString(15, user);
            pstmtl.setString(16, user);
            pstmtl.setString(17, user);
            pstmtl.setString(18, user);
            pstmtl.setString(19, user);
            pstmtl.setString(20, user);
            pstmtl.setString(21, user);
            pstmtl.setString(22, user);
            pstmtl.setString(23, user);
            pstmtl.setString(24, user);
            pstmtl.setString(25, user);
            rs = pstmtl.executeQuery();      // execute the prepared stmt

            // if we found any then output the header row.
            rs.last();
            if (rs.getRow() > 0) {

                found = true;

                //
                //   build the table for the display
                //
                out.println("</td></tr>");                                 // terminate previous col/row
                out.println("<tr><td align=\"center\" valign=\"top\">");

                out.println("<font size=\"3\"><br><br>");
                if (club.equals( "oldoaks" )) {
                   out.println("<b>Current Tee Time Requests</b><br>");
                   out.println("</font><font size=\"2\">");
                   out.println("<b>To select a request</b>:  Just click on the box containing the time (if allowed).");
                } else if (!lotteryText.equals("")) {
                   out.println("<b>Current " +lotteryText+ "s</b><br>");
                   out.println("</font><font size=\"2\">");
                   out.println("<b>To select a " +lotteryText+ "</b>:  Just click on the box containing the time (if allowed).");
                } else {
                   out.println("<b>Current Lottery Requests</b><br>");
                   out.println("</font><font size=\"2\">");
                   out.println("<b>To select a lottery request</b>:  Just click on the box containing the time (if allowed).");
                }
                out.println("</font><font size=\"2\">");
                out.println("<br>F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;F = Front 9, &nbsp;&nbsp;B = Back 9, &nbsp;&nbsp;O = Other");
                out.println("</font><font size=\"2\">");

                out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"5\">");
                  out.println("<tr bgcolor=\"#336633\"><td align=center>");
                        out.println("<font color=\"#ffffff\" size=\"2\">");
                        out.println("<u><b>Date</b></u>");
                        out.println("</font></td>");

                     out.println("<td align=center>");
                        out.println("<font color=\"#ffffff\" size=\"2\">");
                        out.println("<u><b>Time</b></u>");
                        out.println("</font></td>");

                  if (multi != 0) {
                     out.println("<td align=center>");
                        out.println("<font color=\"#ffffff\" size=\"2\">");
                        out.println("<u><b>Course</b></u>");
                        out.println("</font></td>");
                  }

                     out.println("<td align=center>");
                        out.println("<font color=\"#ffffff\" size=\"2\">");
                        out.println("<u><b>F/B</b></u>");
                        out.println("</font></td>");

                     out.println("<td align=center>");
                        out.println("<font color=\"#ffffff\" size=\"2\">");
                        out.println("<u><b>Player 1</b></u>");
                        out.println("</font></td>");

                     out.println("<td align=center>");
                        out.println("<font color=\"#ffffff\" size=\"2\">");
                        out.println("<u><b>Player 2</b></u>");
                        out.println("</font></td>");

                     out.println("<td align=center>");
                        out.println("<font color=\"#ffffff\" size=\"2\">");
                        out.println("<u><b>Player 3</b></u>");
                        out.println("</font></td>");

                     out.println("<td align=center>");
                        out.println("<font color=\"#ffffff\" size=\"2\">");
                        out.println("<u><b>Player 4</b></u>");
                        out.println("</font></td>");

                  if (fivesomes != 0) {
                     out.println("<td align=center>");
                        out.println("<font color=\"#ffffff\" size=\"2\">");
                        out.println("<u><b>Player 5</b></u>");
                        out.println("</font></td>");
                  }

                     out.println("</tr>");

            }

            rs.beforeFirst();

            //
            //  Get each record and display it
            //
            count = 0;             // number of records found

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

               count++;

               ampm = " AM";
               if (hr == 12) {
                  ampm = " PM";
               }
               if (hr > 12) {
                  ampm = " PM";
                  hr = hr - 12;    // convert to conventional time
               }

               if (player1.equals( "" )) {

                  player1 = " ";       // make it a space for table display
               }
               if (player2.equals( "" )) {

                  player2 = " ";       // make it a space for table display
               }
               if (player3.equals( "" )) {

                  player3 = " ";       // make it a space for table display
               }
               if (player4.equals( "" )) {

                  player4 = " ";       // make it a space for table display
               }
               if (player5.equals( "" )) {

                  player5 = " ";       // make it a space for table display
               }
               if (player6.equals( "" )) {

                  player6 = " ";       // make it a space for table display
               }
               if (player7.equals( "" )) {

                  player7 = " ";       // make it a space for table display
               }
               if (player8.equals( "" )) {

                  player8 = " ";       // make it a space for table display
               }
               if (player9.equals( "" )) {

                  player9 = " ";       // make it a space for table display
               }
               if (player10.equals( "" )) {

                  player10 = " ";       // make it a space for table display
               }
               if (player11.equals( "" )) {

                  player11 = " ";       // make it a space for table display
               }
               if (player12.equals( "" )) {

                  player12 = " ";       // make it a space for table display
               }
               if (player13.equals( "" )) {

                  player13 = " ";       // make it a space for table display
               }
               if (player14.equals( "" )) {

                  player14 = " ";       // make it a space for table display
               }
               if (player15.equals( "" )) {

                  player15 = " ";       // make it a space for table display
               }
               if (player16.equals( "" )) {

                  player16 = " ";       // make it a space for table display
               }
               if (player17.equals( "" )) {

                  player17 = " ";       // make it a space for table display
               }
               if (player18.equals( "" )) {

                  player18 = " ";       // make it a space for table display
               }
               if (player19.equals( "" )) {

                  player19 = " ";       // make it a space for table display
               }
               if (player20.equals( "" )) {

                  player20 = " ";       // make it a space for table display
               }
               if (player21.equals( "" )) {

                  player21 = " ";       // make it a space for table display
               }
               if (player22.equals( "" )) {

                  player22 = " ";       // make it a space for table display
               }
               if (player23.equals( "" )) {

                  player23 = " ";       // make it a space for table display
               }
               if (player24.equals( "" )) {

                  player24 = " ";       // make it a space for table display
               }
               if (player25.equals( "" )) {

                  player25 = " ";       // make it a space for table display
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
               out.println("<tr>");

               if (lstate == 2) {       // if still ok to process lottery requests

                  out.println("<form action=\"/" +rev+ "/servlet/Member_lott\" method=\"post\" target=\"_top\">");
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
                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println( day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy );
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     if (min < 10) {
                        out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":0" + min + ampm + "\" >");
                     } else {
                        out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + min + ampm + "\">");
                     }
                  out.println("</font></td>");

               } else {

                  if (lstate == 5) {       // if lottery has already been processed

                     out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
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
                     out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println( day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy );
                        out.println("</font></td>");

                     out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        if (min < 10) {
                           out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":0" + min + ampm + "\">");
                        } else {
                           out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + min + ampm + "\">");
                        }
                     out.println("</font></td>");

                  } else {

   //                  out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"get\">");  // form with no submit
                     out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println( day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy );
                        out.println("</font></td>");

                     out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        if (min < 10) {
                           out.println(hr + ":0" + min + ampm);
                        } else {
                           out.println(hr + ":" + min + ampm);
                        }
                     out.println("</font></td>");
                  }
               }

               if (multi != 0) {
                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println(course);
                  out.println("</font></td>");
               }

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println(sfb);
                  out.println("</font></td>");

                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println( player1 );
                  out.println("</font></td>");

                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println( player2 );
                  out.println("</font></td>");

                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println( player3 );
                  out.println("</font></td>");

                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println( player4 );
                  out.println("</font></td>");

               if (fives != 0) {
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println( player5 );
                  out.println("</font></td>");

                  //
                  //  check if there are more than 5 players registered
                  //
                  if (!player6.equals( " " ) || !player7.equals( " " ) || !player8.equals( " " ) || !player9.equals( " " ) || !player10.equals( " " )) {

                     out.println("</tr><tr>");
                     out.println("<td align=\"center\">");      // date col
                     out.println("&nbsp;</td>");

                     out.println("<td align=\"center\">");      // time col
                     out.println("&nbsp;</td>");

                     if (multi != 0) {
                        out.println("<td align=\"center\">");   // course
                        out.println("&nbsp;</td>");
                     }

                     out.println("<td align=\"center\">");       // f/b
                     out.println("&nbsp;</td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player6 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player7 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player8 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player9 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player10 );
                     out.println("</font></td>");
                  }

                  if (!player11.equals( " " ) || !player12.equals( " " ) || !player13.equals( " " ) || !player14.equals( " " ) || !player15.equals( " " )) {

                     out.println("</tr><tr>");
                     out.println("<td align=\"center\">");      // date col
                     out.println("&nbsp;</td>");

                     out.println("<td align=\"center\">");      // time col
                     out.println("&nbsp;</td>");

                     if (multi != 0) {
                        out.println("<td align=\"center\">");   // course
                        out.println("&nbsp;</td>");
                     }

                     out.println("<td align=\"center\">");       // f/b
                     out.println("&nbsp;</td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player11 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player12 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player13 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player14 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player15 );
                     out.println("</font></td>");
                  }

                  if (!player16.equals( " " ) || !player17.equals( " " ) || !player18.equals( " " ) || !player19.equals( " " ) || !player20.equals( " " )) {

                     out.println("</tr><tr>");
                     out.println("<td align=\"center\">");      // date col
                     out.println("&nbsp;</td>");

                     out.println("<td align=\"center\">");      // time col
                     out.println("&nbsp;</td>");

                     if (multi != 0) {
                        out.println("<td align=\"center\">");   // course
                        out.println("&nbsp;</td>");
                     }

                     out.println("<td align=\"center\">");       // f/b
                     out.println("&nbsp;</td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player16 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player17 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player18 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player19 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player20 );
                     out.println("</font></td>");
                  }

                  if (!player21.equals( " " ) || !player22.equals( " " ) || !player23.equals( " " ) || !player24.equals( " " ) || !player25.equals( " " )) {

                     out.println("</tr><tr>");
                     out.println("<td align=\"center\">");      // date col
                     out.println("&nbsp;</td>");

                     out.println("<td align=\"center\">");      // time col
                     out.println("&nbsp;</td>");

                     if (multi != 0) {
                        out.println("<td align=\"center\">");   // course
                        out.println("&nbsp;</td>");
                     }

                     out.println("<td align=\"center\">");       // f/b
                     out.println("&nbsp;</td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player21 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player22 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player23 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player24 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player25 );
                     out.println("</font></td>");
                  }

               } else {   // no 5-somes on this course

                  if (fivesomes != 0) {        // if 5-somes listed - empty slot

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println("nbsp;");
                     out.println("</font></td>");
                  }
                  //
                  //  check if there are more than 4 players registered
                  //
                  if (!player5.equals( " " ) || !player6.equals( " " ) || !player7.equals( " " ) || !player8.equals( " " )) {

                     out.println("</tr><tr>");
                     out.println("<td align=\"center\">");      // date col
                     out.println("&nbsp;</td>");

                     out.println("<td align=\"center\">");      // time col
                     out.println("&nbsp;</td>");

                     if (multi != 0) {
                        out.println("<td align=\"center\">");   // course
                        out.println("&nbsp;</td>");
                     }

                     out.println("<td align=\"center\">");       // f/b
                     out.println("&nbsp;</td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player5 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player6 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player7 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player8 );
                     out.println("</font></td>");

                     if (fivesomes != 0) {        // if 5-somes listed - empty slot

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println("nbsp;");
                        out.println("</font></td>");
                     }
                  }

                  if (!player9.equals( " " ) || !player10.equals( " " ) || !player11.equals( " " ) || !player12.equals( " " )) {

                     out.println("</tr><tr>");
                     out.println("<td align=\"center\">");      // date col
                     out.println("&nbsp;</td>");

                     out.println("<td align=\"center\">");      // time col
                     out.println("&nbsp;</td>");

                     if (multi != 0) {
                        out.println("<td align=\"center\">");   // course
                        out.println("&nbsp;</td>");
                     }

                     out.println("<td align=\"center\">");       // f/b
                     out.println("&nbsp;</td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player9 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player10 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player11 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player12 );
                     out.println("</font></td>");

                     if (fivesomes != 0) {        // if 5-somes listed - empty slot

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println("nbsp;");
                        out.println("</font></td>");
                     }
                  }

                  if (!player13.equals( " " ) || !player14.equals( " " ) || !player15.equals( " " ) || !player16.equals( " " )) {

                     out.println("</tr><tr>");
                     out.println("<td align=\"center\">");      // date col
                     out.println("&nbsp;</td>");

                     out.println("<td align=\"center\">");      // time col
                     out.println("&nbsp;</td>");

                     if (multi != 0) {
                        out.println("<td align=\"center\">");   // course
                        out.println("&nbsp;</td>");
                     }

                     out.println("<td align=\"center\">");       // f/b
                     out.println("&nbsp;</td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player13 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player14 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player15 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player16 );
                     out.println("</font></td>");

                     if (fivesomes != 0) {        // if 5-somes listed - empty slot

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println("nbsp;");
                        out.println("</font></td>");
                     }
                  }

                  if (!player17.equals( " " ) || !player18.equals( " " ) || !player19.equals( " " ) || !player20.equals( " " )) {

                     out.println("</tr><tr>");
                     out.println("<td align=\"center\">");      // date col
                     out.println("&nbsp;</td>");

                     out.println("<td align=\"center\">");      // time col
                     out.println("&nbsp;</td>");

                     if (multi != 0) {
                        out.println("<td align=\"center\">");   // course
                        out.println("&nbsp;</td>");
                     }

                     out.println("<td align=\"center\">");       // f/b
                     out.println("&nbsp;</td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player17 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player18 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player19 );
                     out.println("</font></td>");

                     out.println("<td align=\"center\" bgcolor=\"white\">");
                     out.println("<font size=\"2\">");
                     out.println( player20 );
                     out.println("</font></td>");

                     if (fivesomes != 0) {        // if 5-somes listed - empty slot

                        out.println("<td align=\"center\" bgcolor=\"white\">");
                        out.println("<font size=\"2\">");
                        out.println("nbsp;");
                        out.println("</font></td>");
                     }
                  }
               }     // end of IF 5-somes

               out.println("</form></tr>");

            }    // end of while

            pstmtl.close();

            out.println("</font></table>");
   /*
            if (count == 0) {
               out.println("<font size=\"2\">");
               if (club.equals( "oldoaks" )) {
                  out.println("<p align=\"center\">You are currently not included in any Tee Time Requests at this time.</p>");
               } else if (!lotteryText.equals("")) {
                  out.println("<p align=\"center\">You are currently not included in any " +lotteryText+ "s at this time.</p>");
               } else {
                  out.println("<p align=\"center\">You are currently not included in any Lottery Requests at this time.</p>");
               }
               out.println("</font>");
            }
   */ 
          }




           boolean foundWait = false;
           date = SystemUtils.getDate(con);

           pstmt = con.prepareStatement(
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


           // if we found any then output the header row.
           rs.last();
           if (rs.getRow() > 0) {

               foundWait = true; // flag to indicate we need to close the header table
               found = true;

               //
               // Check for any wait list signups
               //
               out.println("<br><br>");
               out.println("<font size=3><b>Current Wait List Sign-ups</b></font><br>");
               out.println("");

               out.println("<b>To select the wait list</b>:  Just click on the box containing the date.<br>");
               out.println("");
               out.println("</font></td>");
               out.println("</tr><tr>");
               out.println("<td>");
               out.println("<font size=\"2\">");
               out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"7\" valign=\"top\" align=center>");
               out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<u><b>Date</b></u>");
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<u><b>Time</b></u>");
               out.println("</font></td>");

               if (multi != 0) {

                 out.println("<td align=\"center\">");
                 out.println("<font color=\"#ffffff\" size=\"2\">");
                 out.println("<u><b>Course Name</b></u>");
                 out.println("</font></td>");
               }

               out.println("<td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<u><b>Player 1</b></u>");
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<u><b>Player 2</b></u>");
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<u><b>Player 3</b></u>");
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<u><b>Player 4</b></u>");
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<u><b>Player 5</b></u>");
               out.println("</font></td>");

               out.println("</tr>");
           }

           rs.beforeFirst();

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

               out.println("<tr>");
               out.print("<td align=\"center\"><font size=\"2\">");      // date col
               out.print(rs.getString("date2"));
               //out.println("<button onclick=\"top.location.href='/" + rev + "/servlet/Member_waitlist?waitListId=" + rs.getInt("wait_list_id") + "&date=" + rs.getInt("dateymd") + "&index=0&day_name="+rs.getString("day_name")+"&course=" +rs.getString("course")+"&returnCourse=" +rs.getString("course")+"'\">" + rs.getString("date2") + "</button>");
               out.println("</font></td>");

               out.print("<form onsubmit='return false;'><td align=\"center\"><font size=\"2\">");      // time col
               out.print("<button onclick=\"top.location.href='/" + rev + "/servlet/Member_waitlist?waitListId=" + rs.getInt("wait_list_id") + "&date=" + rs.getInt("dateymd") + "&index=995&day="+rs.getString("day_name")+"&course=" +rs.getString("course")+"&returnCourse=" +rs.getString("course")+"'\">");
               out.print(SystemUtils.getSimpleTime(rs.getInt("ok_stime")) + " - " + SystemUtils.getSimpleTime(rs.getInt("ok_etime")));
               out.print("</button>");
               out.println("</font></td></form>");

               if (multi != 0) {
                   out.print("<td align=\"center\"><font size=\"2\">");   // course
                   out.print(rs.getString("course"));
                   out.println("</font></td>");
               }

               out.print("<td align=\"center\"><font size=\"2\">");      // player 1
               out.print(player1);
               out.println("</font></td>");

               out.print("<td align=\"center\"><font size=\"2\">");      // player 2
               out.print(player2);
               out.println("</font></td>");

               out.print("<td align=\"center\"><font size=\"2\">");      // player 3
               out.print(player3);
               out.println("</font></td>");

               out.print("<td align=\"center\"><font size=\"2\">");      // player 4
               out.print(player4);
               out.println("</font></td>");

               out.print("<td align=\"center\"><font size=\"2\">");      // player 5
               out.print(player5);
               out.println("</font></td>");

               out.println("</tr>");

           }

           pstmt.close();
   /*
           if (!found) {

               out.println("<tr><td colspan=" + ((multi != 0) ? "8" : "7") + " align=center>You are currently not signed up on any active wait lists.</td></tr>");
           }
   */
           if (foundWait) out.println("</table>");


      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error:" + exc.getMessage());
         out.println("<BR>Error:" + exc.toString());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your club manager.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Member_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
      }

   }    // end of IF Golf     

  
   //**********************************
   // END OF GOLF
   //**********************************
  
      
  
   //**********************************
   // Check Activities if in that mode
   //**********************************
  
        
   if (parm.genrez_mode != 0 && sess_activity_id > 0) {    // if activities defined and this user is under Activities

       //
       // Check for any activities
       //
       try {

           pstmt = con.prepareStatement (
                "SELECT a.sheet_id, a.activity_id, " +
                   "DATE_FORMAT(a.date_time, '%W, %b. %D') AS pretty_date, " +
                   "DATE_FORMAT(a.date_time, '%Y%m%d') AS dateymd, " +
                   "DATE_FORMAT(a.date_time, '%l:%i %p') AS pretty_time " +
                "FROM activity_sheets a, activity_sheets_players ap " +
                "WHERE a.sheet_id = ap.activity_sheet_id " +
                   "AND ap.username = ? " +
                   "AND DATE(a.date_time) >= DATE(now()) " +
                "ORDER BY a.date_time");

           pstmt.clearParameters();        // clear the parms
           pstmt.setString(1, user);
           rs = pstmt.executeQuery();

           // if we found any then output the header row.
           rs.last();
           if (rs.getRow() > 0) {

               found = true;

               out.println("<font size=\"3\">");
               out.println("<br><b>Your Scheduled Activities</b></font><br>");
               out.println("<font size=\"2\"><br>");
               out.println("<table border=\"0\">");

           }

           rs.beforeFirst();

           while (rs.next()) {

               out.println("<tr><td align=\"left\"><font size=2>");
               out.println(getActivity.getFullActivityName(rs.getInt("activity_id"), con));
               
               out.println("</font></td><td>&nbsp;</td><td align=\"left\">");
               out.print("<a href=\"javascript:void(0)\" onclick=\"top.location.href='/" + rev + "/servlet/Member_activity_slot?slot_id=" + rs.getInt("sheet_id") + "&date=" + rs.getInt("dateymd") + "&index=998'\"><font size=2 color=darkGreen>" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</font></a><br>");
               //out.println("&nbsp; &nbsp; <a href=\"javascript:editActSignup(" + rs.getInt("sheet_id") + ", '" + rs.getString("pretty_time") + "')\"><font color=darkGreen>" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</font></a><br>");
               out.println("</td></tr>");
               
           }
           out.println("</table>");
           
           
          //
          //*****************************************************************
          // now check for any Events for this Activity that member is signed up for
          //*****************************************************************
          //
          pstmte2 = con.prepareStatement (
            "SELECT name " +
            "FROM events2b WHERE date >= ? AND activity_id = ? AND inactive = 0");       // look for Activity events

          pstmte2.clearParameters();       
          pstmte2.setLong(1, cdate);
          pstmte2.setInt(2, sess_activity_id);
          rs2 = pstmte2.executeQuery();     

          loop1e:
          while (rs2.next()) {

            name = rs2.getString(1);
          
            // check for signups
            pstmte = con.prepareStatement (
               "SELECT name " +
               "FROM evntsup2b WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? " +
               "OR username5 = ?) AND name = ? AND inactive = 0");

            pstmte.clearParameters();        // clear the parms
            pstmte.setString(1, user);
            pstmte.setString(2, user);
            pstmte.setString(3, user);
            pstmte.setString(4, user);
            pstmte.setString(5, user);
            pstmte.setString(6, name);
            rs = pstmte.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               events = true;
            }
            pstmte.close();

            if (events == true) {

               break loop1e;
            }
         }        // end of WHILE evntsup
         pstmte2.close();

         //
         //  if any events found above, then display them here (search over again)
         //
         if (events == true) {

            found = true;
            
            out.println("<font size=\"3\">");
            out.println("<br><b>Your Scheduled Events</b></font><br>");
            out.println("<font size=\"2\"><br>");
            out.println("<table border=\"0\">");

            //
            // now list the events that member is signed up for
            //
            pstmte2 = con.prepareStatement (
               "SELECT name, date, year, month, day, act_hr, act_min, signUp, c_date, c_time " +
               "FROM events2b WHERE date >= ? AND activity_id = ? AND inactive = 0");       // look for Golf events

            pstmte2.clearParameters();        // clear the parms
            pstmte2.setLong(1, cdate);
            pstmte2.setInt(2, sess_activity_id);
            rs2 = pstmte2.executeQuery();     

            while (rs2.next()) {
               
               name = rs2.getString(1);
               edate = rs2.getLong(2);
               yy = rs2.getInt(3);
               mm = rs2.getInt(4);
               dd = rs2.getInt(5);
               hr = rs2.getInt(6);
               min = rs2.getInt(7);
               signUp = rs2.getInt(8);
               c_date = rs2.getLong(9);
               c_time = rs2.getInt(10);
               
               
               //
               //  Now look for signups in this event
               //
               pstmte = con.prepareStatement (
                  "SELECT player1, player2, player3, player4, player5, wait " +
                  "FROM evntsup2b WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? " +
                  "OR username5 = ?) AND name = ? AND inactive = 0 ORDER BY c_date, c_time");

               pstmte.clearParameters();        // clear the parms
               pstmte.setString(1, user);
               pstmte.setString(2, user);
               pstmte.setString(3, user);
               pstmte.setString(4, user);
               pstmte.setString(5, user);
               pstmte.setString(6, name);
               rs = pstmte.executeQuery();      // execute the prepared stmt

               while (rs.next()) {

                  player1 = rs.getString(1);
                  player2 = rs.getString(2);
                  player3 = rs.getString(3);
                  player4 = rs.getString(4);
                  player5 = rs.getString(5);
                  wait = rs.getInt(6);

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

                     if (signUp != 0 && c_date >= cdate) {     // if members can sign up for this event

                        if ((c_date > cdate) || (c_time > ctime)) {

                           signUp = 1;;

                        } else {

                           signUp = 0;;
                        }

                     } else {

                        signUp = 0;;
                     }
                        
                     String timeS = "";
                     
                     if (min < 10) {
                        timeS = mm + "/" + dd + "/" + yy + " at " + hr + ":0" + min + " " + ampm;
                     } else {
                        timeS = mm + "/" + dd + "/" + yy + " at " + hr + ":" + min + " " + ampm;
                     }
                     
                     
                     out.println("<tr><td align=\"left\"><font size=2>");
                     out.println(name);                         // display name of event

                     out.println("</font></td><td>&nbsp;</td><td align=\"left\">");
                     if (signUp == 1) {
                 //       out.print("<a href=\"javascript:void(0)\" onclick=\"gotoEvent(" +name+ ",995)\"><font size=2 color=darkGreen>" +name+ " on " + timeS + "</font></a><br>");
                        out.print("<a href=\"/" + rev + "/servlet/Member_events2?name="+name+"&index=995\"><font size=2 color=darkGreen>" +name+ " on " + timeS + "</font></a><br>");
                     } else {
                        out.print("<font size=2>" +name+ " at " + timeS + "</font><br>");
                     }
                     out.println("</td></tr>");

                  }     // end of IF event date is current
                  
               }     // end of WHILE signups
               pstmte.close();
               
            }        // end of WHILE events

            pstmte2.close();

            out.println("</font></table>");        // done with events table
            
         }    // end of IF any events


       } catch (Exception exc) {

          out.println(SystemUtils.HeadTitle("Database Error"));
          out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
          out.println("<BR><BR><H3>Database Access Error</H3>");
          out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
          out.println("<BR>Error:" + exc.getMessage());
          out.println("<BR><BR>Please try again later.");
          out.println("<BR><BR>If problem persists, contact your club manager.");
          out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Member_announce\">Home</a>");
          out.println("</CENTER></BODY></HTML>");
          out.close();

       } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

       }

   } // end if activities supported




        
   // Finish the page

   if (!found) {

       out.println("<p>You do not currently have any scheduled activities at this time.</p>");

   }



   out.println("</font></td>");
   out.println("</tr>");
   out.println("</table>");                   // end of table for main page

   out.println("<br><br>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;\">");
   out.println("</input></form></font>");

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");
   out.close();

 }   // end of doGet

}
