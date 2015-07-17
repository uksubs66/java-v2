/***************************************************************************************
 *   Proshop_reports:  This servlet will process a request from Proshop's
 *                     reports page.
 *
 *
 *   called by:  proshop_reports.htm
 *               self (calls doPost)
 *
 *
 *   parms passed by proshop_reports.htm:
 *
 *               round=all  for Number of Rounds Played Report (this month, MTD, YTD)
 *               today=yes  for Number of Rounds Played Today Report
 *               custom=yes for Number of Rounds Played Report by custom date range
 *               noshow=yes for No-Show report by date & member
 *               subtee=cal for a List of a member's past tee times for calendar year
 *               subtee=year for a List of a member's past tee times for past 12 months
 *               subtee=forever for a List of a member's past tee times since inception
 *               teetimes=yes for a list of who is making tee times (proshop vs members)
 *
 *   parms passed by self:
 *
 *               gotee='subtee value' for Tee Times List
 *                                    (username)
 *               noshow2=yes  for No-Show report - call from here to generate report
 *                            (smonth, sday, syear, emonth, eday, eyear, username)
 *
 *               custom2=yes  for Number of Rounds report (custom date) - call from here to generate report
 *                            (smonth, sday, syear, emonth, eday, eyear)
 *
 *
 *   created: 2/21/2002   Bob P.
 *
 *   last updated:
 *
 *        2/24/14   Estero CC (esterocc) - Added custom to pull details for the entire day on the Number of Rounds Played - Today report, instead of only up through the current time (case 2374).
 *       11/21/13   Add 'All Guests' option to No-Show Report.
 *        6/13/13   Monterey Peninsula CC (mpccpb) - Added custom to pull details for the entire day on the Number of Rounds Played - Today report, instead of only up through the current time (case 2274).
 *       12/10/12   Move some forms inside TD instead of outside
 *       12/07/12   Enhanced the FlxRez getActivityTimes (formerly getPastActTimes) report - now looks more like golf and also handles the current reservations inquiry
 *       11/18/12   MySQL 5.5 compatability fixes (nulls w/ rollup)
 *       12/01/11   Fixed issue where Number of Rounds Report for today was counting mode of trans for pre-checkin rounds.
 *        6/27/11   No-Show report - allow user to select today's date and display no-show report for today if selected (Olympic Club needed this).
 *       12/06/10   Los Coyotes CC (loscoyotes) - Added displayMnums option to no-show report.  When set to true, mNums are displayed behind member names (case 1890).
 *       10/12/10   Do not include event tee times in tee time report unless requested (add events parm and link to switch).
 *        9/22/10   Added getPastActTimes method (activity ver of goTee) - Search members past reservations (Tools > Search)
 *        6/24/10   Updated js calls for name list to add iPad compatability (teeTime method)
 *        5/25/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        5/05/10   Update goCustom - move custom date range calendar build to Common_config so others can use it. 
 *        3/27/10   Winged Foot - add changes to guest report for 2010 (case 1096).
 *        3/08/10   Changed goTeeTimes to use the actual number of hotelNew times found instead of incrementing by 1
 *       12/14/09   Updated noshow methods to support Activites and cleaned up some code in those two methods
 *       12/10/09   Updated goTeetimes to support Activites and optimized teecurr/teepast queries
 *        9/15/09   Change Limited Access Proshop User calls for TS_VIEW to the "past"-specific version: TS_PAST_VIEW
 *        8/28/09   Winged Foot (wingedfoot) - Fixed report, was missing numerous membership types
 *        7/28/09   Winged Foot (wingedfoot) - Added additional guest quotas
 *        6/18/09   Change guestQuotaAll and guestQuota methods to call out to Utilities.checkWingedFootGuestTypes() for all wingedfoot guest name checks.
 *        6/16/09   Changes to Winged Foot guest report customs (guestQuotaAll, questQuota) (case 1096).
 *        5/15/09   Fixed revenue breakdown of guest rounds in goRounds and goCustom2
 *        3/30/09   Do not use the stats5 table for rounds data.  Instead get the info directly from teepast2.
 *       12/04/08   Custom date range boxes now default to current year and ending date is today
 *       10/03/08   Added capability for output to Excel for Today/Custom date range reports (case 1490).
 *        9/02/08   Javascript compatability updates
 *        8/13/08   Winged Foot - add guest quota report for All members (case 1300).
 *        7/18/08   Added limited access proshop users checks
 *        6/09/08   Add support for Custom Date Range for member past tee time reports (case 1487).
 *        6/04/08   Rounds Report - only display the Member No-Shows (do not include guest counts) so it will match the No Show Report.
 *        6/04/08   Changes to No Show Report (noShow2) - display course name, move name processing, simplify guest determinations.
 *        5/06/08   Increase length of text to search for from 20 to 42 characters - tweak invData text
 *        4/22/08   Updated guestReport for Wigned Foot - fixed dates - allow year selection
 *        4/17/08   Add Revenue Guest counts to Rounds Reports (case 1400).
 *        4/10/08   Updated guestReport for Wigned Foot
 *        1/05/08   Show 9 hole indicator on tmode like tee sheet for 'goTee' single player report
 *        9/19/07   Winged Foot - Added guestReport to for showing guest rounds as counted for quota (Case #1250)
 *        6/07/07   Fixed goNotifications method
 *        8/16/06   Correct SQL error in No Show report (pstmt2).
 *        9/05/05   goTee - add column for 'times checked in' to 'All Members' tee times report.
 *        8/18/05   Add notes to the tee time display and allow pro to display them.
 *        3/02/05   Ver 5 - modified some conditional statements for precheckin support, show1-5  (Paul S)
 *        1/25/05   Ver 5 - correct names specified for stats5 table.
 *        1/24/05   Ver 5 - change club2 to club5 and stats2 to stats5.
 *        9/16/04   Ver 5 - change getClub from SystemUtils to common.
 *        7/07/04   Add 'List All Members' option for Member Tee Time reports.
 *        3/01/04   Add 'Excel Spreadsheet' option for reports.
 *        2/06/04   Add new Modes of Transportation stats
 *        1/06/04   Add Hotel counts to Logins Stats
 *        7/18/03   Enhancements for Version 3 of the software.
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
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;
import com.foretees.common.getActivity;


public class Proshop_reports extends HttpServlet {

 String omit = "";

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

    private static long Hdate1 = ProcessConstants.memDay;     // Memorial Day
    private static long Hdate3 = ProcessConstants.laborDay;   // Labor Day


 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing

 }

 //*******************************************************
 // Process the initial request from proshop menu
 //  or the request from itself.
 //*******************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   //Statement stmt = null;
   //ResultSet rs = null;

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

   if (sess == null) {

      return;
   }

   int sess_activity_id = (Integer)sess.getAttribute("activity_id");

   Connection con = SystemUtils.getCon(sess);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
       SystemUtils.restrictProshop("REPORTS", out);
       return;
   }

   //String rep = "";

   //
   // Check which parameters were received - process accordingly
   //
   //   Initial calls from proshop_reports.htm
   //
   if (req.getParameter("gquota") != null) {

      guestQuota(req, out, con);           // custom report for Winged Foot
      return;
   }

   if (req.getParameter("gquotaall") != null) {

      guestQuotaAll(req, out, con);        // custom report for Winged Foot
      return;
   }

   if (req.getParameter("round") != null) {

      goRounds(req, resp, out, sess, con);                   // go process Number of Rounds Played request
      return;
   }

   if (req.getParameter("today") != null) {

      goToday(req, resp, out, con);                   // go process Number of Rounds Played request
      return;
   }

   if (req.getParameter("notifications") != null) {

      //goTeetimes(req, out, con);
      goNotifications(req, out, con);                   // go process Tee Time Report request
      return;
   }

   if (req.getParameter("teetimes") != null) {

      goTeetimes(req, out, con);                   // go process Tee Time Report request
      return;
   }

   if (req.getParameter("custom") != null) {

      goCustom(req, out, con);                   // go process Number of Rounds Played request (custom date)
      return;
   }

   if (req.getParameter("noshow") != null) {

      noShows(req, out, con);                   // go process No_shows request
      return;
   }

   if (req.getParameter("subtee") != null) {

      if (SystemUtils.verifyProAccess(req, "TOOLS_SEARCHTS", con, out) && SystemUtils.verifyProAccess(req, "TS_PAST_VIEW", con, out)) {
          teeTime(req, resp, out, con);                   // go process member tee time request
      } else {
          SystemUtils.restrictProshop("TOOLS_SEARCHTS", out);
      }

      return;
   }

   if (req.getParameter("noshow2") != null) {

      noShow2(req, out, con);                  // go process No-Show Report 2nd request
      return;
   }

   if (req.getParameter("custom2") != null) {

      Custom2(req, resp, out, con);                  // go process Number of Rounds Report, Custom Date - 2nd request
      return;
   }

   //
   //   if call was for Show Notes then get the notes and display a new page
   //
   if (req.getParameter("notes") != null) {

      if (sess_activity_id == 0) { 
          
          String sdate = req.getParameter("date");         //  date of the slot
          String stime = req.getParameter("time");         //  time of the slot
          String sfb = req.getParameter("fb");             //  front/back indicator
          String course = req.getParameter("course");

          long date = Long.parseLong(sdate);

          SystemUtils.displayOldNotes(stime, sfb, date, course, out, con);             // display the information
          return;
          
      } else {
          
          displaySlotDetails(Integer.parseInt(req.getParameter("slot_id")), out, con);
          return;
          
      }
   }

   out.println(SystemUtils.HeadTitle("Proshop Reports Page - Error"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>Procedure Error</H3>");
   out.println("<BR><BR>Sorry, you must first select a member in order to run this report.");
   out.println("<BR><BR>Please try again.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<br><br><a href=\"Proshop_announce\">Return</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

 }  // end of doPost



 //
 //  Winged Foot Guest Quota Report for ALL members
 //
 private void guestQuotaAll(HttpServletRequest req, PrintWriter out, Connection con) {

    Calendar cal = new GregorianCalendar();       // get todays date

    int year = cal.get(Calendar.YEAR);

    String name = "";
    String username = "";
    String mNum = "";
    String last_mNum = "";
    String mship = "";
    String userg1 = "";
    String userg2 = "";
    String userg3 = "";
    String userg4 = "";
    String userg5 = "";
    String player1 = "";
    String player2 = "";
    String player3 = "";
    String player4 = "";
    String player5 = "";

    long date = 0;
    long sdate = (year * 10000) + 501;          // yyyy0501     Golf Year bookends
    long edate = (year * 10000) + 1031;         // yyyy1031

    int i = 0;
    int countg = 0; // # of guests in year
    int counts = 0; // # of guests in season
    int total_checked = 0;
    int met_year = 0;
    int met_season = 0;

    boolean met_quota = false;

    ResultSet rs = null;
    ResultSet rs2 = null;
    ResultSet rs3 = null;

    //
    //  Build the HTML page to display report
    //
    out.println(SystemUtils.HeadTitle("Proshop Reports Page"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, 0);
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

    // display report header
    out.println("<font size=\"3\">");
    out.println("<h3>Guest Quota Report</h3>");

    out.println("<b>For All Members</b>");
    out.println("</font><font size=\"2\">");
    out.println("<br><br>The Following Members/Families Have Met Their Guest Quota for the Golf Year<br><br>");

    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"4\">");
    out.println("<tr align=center bgcolor=\"#336633\" style=\"color: white;font-weight: bold;text-decoration: underline\"><td>Member</td><td>Mem #</td><td>Yearly<br>Quota</td><td>Year<br>Used</td></tr>");


     try {

        // get all active members (one per memNum)
        PreparedStatement pstmt1 = con.prepareStatement (
                "SELECT memNum, m_ship, name_last, name_first, name_mi " +
                "FROM member2b " +
                "WHERE inact = 0 AND (m_type = ? OR m_type = ?) " +
                "GROUP BY memNum");

        pstmt1.clearParameters();
        pstmt1.setString(1, "Primary Male");
        pstmt1.setString(2, "Primary Female");
        rs = pstmt1.executeQuery();

        while (rs.next()) {

            mNum = rs.getString(1);
            mship = rs.getString(2);
            name = rs.getString(3) + ", " + rs.getString(4) + " " + rs.getString(5);

           if (!mNum.equals( last_mNum)) {      // if new mNum group

              last_mNum = mNum;                 // save new mNum

              total_checked++;                  // count number of mnums checked

              //
              //  Check counts based on mship type for this family
              //
              int maxs = 8;        // defaults
              int maxg = 12;

              if (mship.equals( "Regular Family" )) {

                  maxs = 20;
                  maxg = 24;

              } else if (mship.equals( "Regular" ) || mship.equals( "Regular Senior" ) ||
                      mship.equals( "Junior C Family" ) || mship.equals( "Junior D Family" )) {

                  maxs = 14;
                  maxg = 20;

              } else if (mship.equals( "Junior B" )) {

                  maxs = 10;
                  maxg = 15;

              } else if (mship.equals( "Junior C" ) || mship.equals( "Junior D" )) {

                  maxs = 12;
                  maxg = 18;

              }

              counts = 0;      // reset counts    (Season no longer tallied!!!!!!!! - 2010)
              countg = 0;


               //
               //  loop thru all members with matching memNum
               //
               PreparedStatement pstmt4 = con.prepareStatement (
                  "SELECT username FROM member2b WHERE memNum = ?");

               pstmt4.clearParameters();
               pstmt4.setString(1, mNum);
               rs2 = pstmt4.executeQuery();

               while (rs2.next()) {

                  username = rs2.getString(1);       // get the username

                  //
                  //   Check teecurr and teepast for guest times for this member for the Golf Year
                  //
                  PreparedStatement pstmt = con.prepareStatement (
                     "SELECT date, player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5 " +
                     "FROM teepast2 " +
                     "WHERE date <= ? AND date >= ? AND " +
                     "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

                  pstmt.clearParameters();
                  pstmt.setLong(1, edate);           // 10/31
                  pstmt.setLong(2, sdate);           // 5/01
                  pstmt.setString(3, username);
                  pstmt.setString(4, username);
                  pstmt.setString(5, username);
                  pstmt.setString(6, username);
                  pstmt.setString(7, username);
                  rs3 = pstmt.executeQuery();

                  while (rs3.next()) {

                     date = rs3.getLong(1);
                     player1 = rs3.getString(2);
                     player2 = rs3.getString(3);
                     player3 = rs3.getString(4);
                     player4 = rs3.getString(5);
                     player5 = rs3.getString(6);
                     userg1 = rs3.getString(7);
                     userg2 = rs3.getString(8);
                     userg3 = rs3.getString(9);
                     userg4 = rs3.getString(10);
                     userg5 = rs3.getString(11);

                     if (userg1.equals( username ) && Utilities.checkWingedFootGuestTypes(player1, mship)) {

                        countg++;     // bump # of guests
                     }
                     if (userg2.equals( username ) && Utilities.checkWingedFootGuestTypes(player2, mship)) {

                        countg++;     // bump # of guests
                     }
                     if (userg3.equals( username ) && Utilities.checkWingedFootGuestTypes(player3, mship)) {

                        countg++;     // bump # of guests
                     }
                     if (userg4.equals( username ) && Utilities.checkWingedFootGuestTypes(player4, mship)) {

                        countg++;     // bump # of guests
                     }
                     if (userg5.equals( username ) && Utilities.checkWingedFootGuestTypes(player5, mship)) {

                        countg++;     // bump # of guests
                     }
                  }      // end of WHILE

                  pstmt.close();


                  pstmt = con.prepareStatement (
                     "SELECT date, player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5 " +
                     "FROM teecurr2 " +
                     "WHERE date <= ? AND date >= ? AND " +
                     "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

                  pstmt.clearParameters();
                  pstmt.setLong(1, edate);                // 10/31
                  pstmt.setLong(2, sdate);                // 5/01
                  pstmt.setString(3, username);
                  pstmt.setString(4, username);
                  pstmt.setString(5, username);
                  pstmt.setString(6, username);
                  pstmt.setString(7, username);
                  rs3 = pstmt.executeQuery();

                  while (rs3.next()) {

                     date = rs3.getLong(1);
                     player1 = rs3.getString(2);
                     player2 = rs3.getString(3);
                     player3 = rs3.getString(4);
                     player4 = rs3.getString(5);
                     player5 = rs3.getString(6);
                     userg1 = rs3.getString(7);
                     userg2 = rs3.getString(8);
                     userg3 = rs3.getString(9);
                     userg4 = rs3.getString(10);
                     userg5 = rs3.getString(11);

                     if (userg1.equals( username ) && Utilities.checkWingedFootGuestTypes(player1, mship)) {

                        countg++;     // bump # of guests
                     }
                     if (userg2.equals( username ) && Utilities.checkWingedFootGuestTypes(player2, mship)) {

                        countg++;     // bump # of guests
                     }
                     if (userg3.equals( username ) && Utilities.checkWingedFootGuestTypes(player3, mship)) {

                        countg++;     // bump # of guests
                     }
                     if (userg4.equals( username ) && Utilities.checkWingedFootGuestTypes(player4, mship)) {

                        countg++;     // bump # of guests
                     }
                     if (userg5.equals( username ) && Utilities.checkWingedFootGuestTypes(player5, mship)) {

                        countg++;     // bump # of guests
                     }
                  }      // end of WHILE

                  pstmt.close();

               } // end loop of members w/ same mNum
               pstmt4.close();


               //
               //  Now check if this family has met either of their quotas
               //
               met_quota = false;      // init

               if (countg >= maxg) {          // if yearly quota met

                  met_quota = true;           // indicate quota met
                  met_year++;                 // bump yearly counter
               }

               //
               //   List this member if either quota met
               //
               if (met_quota == true) {

                  out.println("<tr align=center><td align=left>" +name+ "</td><td>" +mNum+ "</td><td>" +maxg+ "</td><td>" +countg+ "</td></tr>");
               }

           }    // end of IF new mNum

        }       // end of WHILE members

        pstmt1.close();

        out.println("</table>");   // end of table

        if (met_year/total_checked < 1) {
           out.println("<br><br>" +met_year+ " of " +total_checked+ " memberships (<1%) have met their yearly quota.</b></p>");
        } else {
           out.println("<br><br>" +met_year+ " of " +total_checked+ " memberships (" +(met_year/total_checked)+ "%) have met their yearly quota.</b></p>");
        }

        out.println("<br><p align=center>Yearly is defined as May 1st through October 31st.</p>");

        out.println("<center>");
        out.println("<form method=\"post\" action=\"Proshop_reports\">");
        out.println("<input type=hidden name=subtee value=cal>");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline\">");
        out.println("</form><br><br>");
        out.println("<form method=\"get\" action=\"Proshop_announce\">");
        out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;width:90px\">");
        out.println("</form>");
        out.println("</center>");

        //
        //  End of HTML page
        //
        out.println("</body></html>");
        out.close();

    }
    catch (Exception e) {

        SystemUtils.buildDatabaseErrMsg("Error building Winged Foot guest report for All members.", e.toString(), out, false);
    }
 }


 //
 // NOTE: THIS EXACT REPORT EXISTS IN Member_searchpast!!!!!!!
 //
 private void guestQuota(HttpServletRequest req, PrintWriter out, Connection con) {

    String name = "";
    String user = "";

    if (req.getParameter("name") != null) {             // if user specified a name to search for

       name = req.getParameter("name");                 // name to search for

       if (!name.equals( "" )) {

          user = SystemUtils.getUsernameFromFullName(name, con);
       }
    }


    //
    //  reject if no member
    //
    if (user.equals("")) {

         out.println(SystemUtils.HeadTitle("Proshop Reports Page - Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<CENTER><BR>");
         out.println("<BR><BR><H3>Procedure Error</H3>");
         out.println("<BR><BR>Sorry, you must first select a member in order to run this report.");
         out.println("<BR><BR>Please try again.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"Proshop_reports?subtee=cal\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
    }



    Calendar cal = new GregorianCalendar();

    int calYear = cal.get(Calendar.YEAR);               // default to current year

    if (req.getParameter("calYear") != null) {          // if year already selected

        String temp = req.getParameter("calYear");      // get the year
        calYear = Integer.parseInt(temp);
    }

    String username = "";
    String mNum = "";
    String mship = "";
    String userg1 = "";
    String userg2 = "";
    String userg3 = "";
    String userg4 = "";
    String userg5 = "";
    String player1 = "";
    String player2 = "";
    String player3 = "";
    String player4 = "";
    String player5 = "";
    String course = "";
    String fdate = "";

    long sdate = (calYear * 10000) + 501;               // yyyy0501     Golf Year bookends
    long edate = (calYear * 10000) + 1031;              // yyyy1031

    int i = 0;
    int countg = 0; // # of guests
    int counts = 0; // # of guests in season   (No Longer used - 2010)
    int date = 0;
    int time = 0;
    int members = 0;

    boolean found = false;

    ResultSet rs = null;
    ResultSet rs2 = null;

    try {

        // get this members member number
        PreparedStatement pstmt1 = con.prepareStatement (
                "SELECT memNum, m_ship FROM member2b WHERE username = ?");

        pstmt1.clearParameters();
        pstmt1.setString(1, user);
        rs = pstmt1.executeQuery();

        if (rs.next()) {

            mNum = rs.getString(1);
            mship = rs.getString(2);
        }

        //
        //  Check counts based on mship type for this family
        //
        int maxs = 8;        // defaults
        int maxg = 12;

        if (mship.equals( "Regular Family" )) {

            maxs = 20;
            maxg = 24;

        } else if (mship.equals( "Regular" ) || mship.equals( "Regular Senior" ) ||
                mship.equals( "Junior C Family" ) || mship.equals( "Junior D Family" )) {

            maxs = 14;
            maxg = 20;

        } else if (mship.equals( "Junior B" )) {

            maxs = 10;
            maxg = 15;

        } else if (mship.equals( "Junior C" ) || mship.equals( "Junior D" )) {

            maxs = 12;
            maxg = 18;
        }

        //
        //  Build the HTML page to display report
        //
        out.println(SystemUtils.HeadTitle("Proshop Reports Page"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        SystemUtils.getProshopSubMenu(req, out, 0);
        out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

        // display report header
        out.println("<h2>Guest Rounds Report</h2>");
        out.println("<p><b>For " + name + " ("+mNum+")</b></p>");

        out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"4\">");
        out.println("<tr align=center bgcolor=\"#336633\" style=\"color: white;font-weight: bold;text-decoration: underline\"><td>Date</td><td>Time</td><td>Course</td><td>Player 1</td><td>Player 2</td><td>Player 3</td><td>Player 4</td><td>Player 5</td></tr>");


                     //
                     //  loop thru all members with matching memNum
                     //
                     PreparedStatement pstmt4 = con.prepareStatement (
                        "SELECT username FROM member2b WHERE memNum = ?");

                     pstmt4.clearParameters();
                     pstmt4.setString(1, mNum);
                     rs2 = pstmt4.executeQuery();

                     while (rs2.next()) {

                        username = rs2.getString(1);       // get the username

                        //
                        //   Check teecurr and teepast for other guest times for this member for the season (In Season)
                        //
                        PreparedStatement pstmt = con.prepareStatement (
                           "SELECT player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, date, time, courseName, DATE_FORMAT(date, \"%b. %e, %y\") AS fdate " +
                           "FROM teepast2 " +
                           "WHERE date >= ? AND date <= ? AND " +
                           "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) " +
                            "ORDER BY date, time");

                        pstmt.clearParameters();
                        pstmt.setLong(1, sdate);           // 5/01
                        pstmt.setLong(2, edate);           // 10/31
                        pstmt.setString(3, username);
                        pstmt.setString(4, username);
                        pstmt.setString(5, username);
                        pstmt.setString(6, username);
                        pstmt.setString(7, username);
                        rs = pstmt.executeQuery();

                        while (rs.next()) {

                           found = false; // reset

                           player1 = rs.getString(1);
                           player2 = rs.getString(2);
                           player3 = rs.getString(3);
                           player4 = rs.getString(4);
                           player5 = rs.getString(5);
                           userg1 = rs.getString(6);
                           userg2 = rs.getString(7);
                           userg3 = rs.getString(8);
                           userg4 = rs.getString(9);
                           userg5 = rs.getString(10);
                           date = rs.getInt(11);
                           time = rs.getInt(12);
                           course = rs.getString(13);
                           fdate = rs.getString(14);

                           if (userg1.equals( username ) && Utilities.checkWingedFootGuestTypes(player1, mship)) {

                              countg++;     // bump # of guests
                              found = true;
                             // out.println("<!-- teepast2 - in season, player=" + player1 + " -->");
                           }
                           if (userg2.equals( username ) && Utilities.checkWingedFootGuestTypes(player2, mship)) {

                              countg++;     // bump # of guests
                              found = true;
                             // out.println("<!-- teepast2 - in season, player=" + player2 + " -->");
                           }
                           if (userg3.equals( username ) && Utilities.checkWingedFootGuestTypes(player3, mship)) {

                              countg++;     // bump # of guests
                              found = true;
                             // out.println("<!-- teepast2 - in season, player=" + player3 + " -->");
                           }
                           if (userg4.equals( username ) && Utilities.checkWingedFootGuestTypes(player4, mship)) {

                              countg++;     // bump # of guests
                              found = true;
                             // out.println("<!-- teepast2 - in season, player=" + player4 + " -->");
                           }
                           if (userg5.equals( username ) && Utilities.checkWingedFootGuestTypes(player5, mship)) {

                              countg++;     // bump # of guests
                              found = true;
                             // out.println("<!-- teepast2 - in season, player=" + player5 + " -->");
                           }

                           if (found) out.println("<tr><!-- teepast2 - in season, user=" + username + " --><td align=right>" + fdate + "</td><td align=center>" + SystemUtils.getSimpleTime(time) + "</td><td align=center>" + course + "</td>" +
                                        "<td>" + player1 + "</td><td>" + player2 + "</td><td>" + player3 + "&nbsp;</td><td>" + player4 + "&nbsp;</td><td>" + player5 + "&nbsp;</td></tr>");

                        }      // end of WHILE
                        rs.close();
                        pstmt.close();


                        pstmt = con.prepareStatement (
                           "SELECT player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, date, time, courseName, DATE_FORMAT(date, \"%b. %e, %y\") AS fdate " +
                           "FROM teecurr2 " +
                           "WHERE date >= ? AND date <= ? AND " +
                           "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) " +
                            "ORDER BY date, time");

                        pstmt.clearParameters();
                        pstmt.setLong(1, sdate);                // 5/01
                        pstmt.setLong(2, edate);                // 10/31
                        pstmt.setString(3, username);
                        pstmt.setString(4, username);
                        pstmt.setString(5, username);
                        pstmt.setString(6, username);
                        pstmt.setString(7, username);
                        rs = pstmt.executeQuery();

                        while (rs.next()) {

                           found = false; // reset

                           player1 = rs.getString(1);
                           player2 = rs.getString(2);
                           player3 = rs.getString(3);
                           player4 = rs.getString(4);
                           player5 = rs.getString(5);
                           userg1 = rs.getString(6);
                           userg2 = rs.getString(7);
                           userg3 = rs.getString(8);
                           userg4 = rs.getString(9);
                           userg5 = rs.getString(10);
                           date = rs.getInt(11);
                           time = rs.getInt(12);
                           course = rs.getString(13);
                           fdate = rs.getString(14);

                           if (userg1.equals( username ) && Utilities.checkWingedFootGuestTypes(player1, mship)) {

                              countg++;     // bump # of guests
                              found = true;
                             // out.println("<!-- teecurr2 - in season, player=" + player1 + " -->");
                           }
                           if (userg2.equals( username ) && Utilities.checkWingedFootGuestTypes(player2, mship)) {

                              countg++;     // bump # of guests
                              found = true;
                             // out.println("<!-- teecurr2 - in season, player=" + player2 + " -->");
                           }
                           if (userg3.equals( username ) && Utilities.checkWingedFootGuestTypes(player3, mship)) {

                              countg++;     // bump # of guests
                              found = true;
                             // out.println("<!-- teecurr2 - in season, player=" + player3 + " -->");
                           }
                           if (userg4.equals( username ) && Utilities.checkWingedFootGuestTypes(player4, mship)) {

                              countg++;     // bump # of guests
                              found = true;
                             // out.println("<!-- teecurr2 - in season, player=" + player4 + " -->");
                           }
                           if (userg5.equals( username ) && Utilities.checkWingedFootGuestTypes(player5, mship)) {

                              countg++;     // bump # of guests
                              found = true;
                             // out.println("<!-- teecurr2 - in season, player=" + player5 + " -->");
                           }

                           if (found) out.println("<tr><!-- teecurr2 - in season, user=" + username + " --><td align=right>" + fdate + "</td><td align=center>" + SystemUtils.getSimpleTime(time) + "</td><td align=center>" + course + "</td>" +
                                        "<td>" + player1 + "</td><td>" + player2 + "</td><td>" + player3 + "&nbsp;</td><td>" + player4 + "&nbsp;</td><td>" + player5 + "&nbsp;</td></tr>");

                        }      // end of WHILE

                        rs.close();
                        pstmt.close();

                        members++;

                     } // end loop of members w/ same mNum
                     pstmt4.close();


        out.println("</table>");

        out.println("<br><br>");

        out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"4\">");
        out.println("<tr align=center bgcolor=\"#336633\" style=\"color: white;font-weight: bold;text-decoration: underline\"><td></td><td>Allowed</td><td>Used</td><td>Remaining</td></tr>");
        out.println("<tr align=center><td align=right bgcolor=\"#336633\" style=\"color: white;font-weight: bold;text-decoration: underline\">Golf Year</td><td>" + maxg + "</td><td>" + (countg + counts) + "</td><td>" + (maxg - (countg + counts)) + "</td></tr>");
        out.println("</table>");

        out.println("<br><p align=center>Golf Year is defined as May 1st through October 31st.</p>");

        out.println("<center>");
        out.println("<form method=\"post\" action=\"Proshop_reports\">");
        out.println("<input type=hidden name=subtee value=cal>");
        out.println("<input type=\"submit\" value=\"Choose Another Member\" style=\"text-decoration:underline\">");
        out.println("</form><br><br>");
        out.println("<form method=\"get\" action=\"Proshop_announce\">");
        out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;width:90px\">");
        out.println("</form>");
        out.println("</center>");

        out.println("<!-- Hdate3=" + Hdate3 + " | Hdate1=" + Hdate1 + " -->");
        out.println("<!-- sdate=" + sdate + " | edate=" + edate + " -->");
        out.println("<!-- members found w/ this member numer =" + members + " -->");

        //
        //  End of HTML page
        //
        out.println("</body></html>");
        out.close();

    }
    catch (Exception e) {

        SystemUtils.buildDatabaseErrMsg("Error building Winged Foot guest report.", e.toString(), out, false);
    }

 }


 // *********************************************************
 //  Report Type = Number of Rounds Played (round=all)
 // *********************************************************

 private void goRounds(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession sess, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;
   //ResultSet rs2 = null;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // golf only report

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   //  get the club name from the session
   //
   //String club = (String)sess.getAttribute("club");      // get club name
   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //   Get multi option, member types, and guest types
   //
   try {

      getClub.getParms(con, parm);        // get the club parms
   }
   catch (Exception ignore) {
   }

   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int grev1 = 0;
   int grev2 = 0;
   int grev3 = 0;
   int grev4 = 0;
   int grev5 = 0;

   int memUnknown1 = 0;
   int memUnknown2 = 0;
   int memUnknown4 = 0;
   int memUnknown9 = 0;
   int memUnknown18 = 0;
   int mshipUnknown1 = 0;
   int mshipUnknown2 = 0;
   int mshipUnknown4 = 0;
   int mshipUnknown9 = 0;
   int mshipUnknown18 = 0;

   int [] tmodeR1 = new int [parm.MAX_Tmodes];       // use arrays for the 16 modes of trans
   int [] tmode9R1 = new int [parm.MAX_Tmodes];
   int [] tmode18R1 = new int [parm.MAX_Tmodes];

   int tmodeOldR91 = 0;
   int tmodeOldR181 = 0;

   int nshowRounds1 = 0;
   int nshow9Rounds1 = 0;
   int nshow18Rounds1 = 0;
   int mnshowRounds1 = 0;
   int mnshow9Rounds1 = 0;
   int mnshow18Rounds1 = 0;
   int gnshowRounds1 = 0;
   int gnshow9Rounds1 = 0;
   int gnshow18Rounds1 = 0;

   int otherRounds1 = 0;
   int other9Rounds1 = 0;
   int other18Rounds1 = 0;

   int totRounds1 = 0;

   int memRounds1 = 0;
   int [] memxRounds1 = new int [parm.MAX_Mems];       // use arrays for the mem types

   //String [] memTypes = new String [parm.MAX_Mems];

   int mem9Rounds1 = 0;
   int mem18Rounds1 = 0;

   int gstRounds1 = 0;
   int [] gstRnds1 = new int [parm.MAX_Guests];       // use array for the 36 guest types

   int gst9Rounds1 = 0;                    // total guest rounds
   int gst18Rounds1 = 0;
   int gst9RevRnds1 = 0;                   // total Revenue guest rounds
   int gst18RevRnds1 = 0;

   int mshipRounds1 = 0;
   int [] mshipxRounds1 = new int [parm.MAX_Mships];       // use arrays for the mship types

   int mship9Rounds1 = 0;
   int mship18Rounds1 = 0;

   int [] memxRounds9 = new int [parm.MAX_Mems];       // use arrays for the mem types
   int [] memxRounds18 = new int [parm.MAX_Mems];      // use arrays for the mem types

   int [] mshipxRounds9 = new int [parm.MAX_Mships];       // use arrays for the mship types
   int [] mshipxRounds18 = new int [parm.MAX_Mships];      // use arrays for the mship types

   int [] gst1Rnds9 = new int [parm.MAX_Guests];       // use array for the 36 guest types
   int [] gst1Rnds18 = new int [parm.MAX_Guests];

   int [] tmodeR2 = new int [parm.MAX_Tmodes];       // use arrays for the 16 modes of trans
   int [] tmode9R2 = new int [parm.MAX_Tmodes];
   int [] tmode18R2 = new int [parm.MAX_Tmodes];

   int tmodeOldR92 = 0;
   int tmodeOldR182 = 0;

   int nshowRounds2 = 0;
   int nshow9Rounds2 = 0;
   int nshow18Rounds2 = 0;
   int mnshowRounds2 = 0;
   int mnshow9Rounds2 = 0;
   int mnshow18Rounds2 = 0;
   int gnshowRounds2 = 0;
   int gnshow9Rounds2 = 0;
   int gnshow18Rounds2 = 0;

   int otherRounds2 = 0;
   int other9Rounds2 = 0;
   int other18Rounds2 = 0;

   int totRounds2 = 0;

   int memRounds2 = 0;
   int [] memxRounds2 = new int [parm.MAX_Mems];       // use arrays for the mem types

   int mem9Rounds2 = 0;
   int mem18Rounds2 = 0;

   int gstRounds2 = 0;
   int [] gstRnds2 = new int [parm.MAX_Guests];       // use array for the 36 guest types

   int gst9Rounds2 = 0;                    // total guest rounds
   int gst18Rounds2 = 0;
   int gst9RevRnds2 = 0;                   // total Revenue guest rounds
   int gst18RevRnds2 = 0;

   int mshipRounds2 = 0;
   int [] mshipxRounds2 = new int [parm.MAX_Mships];       // use arrays for the mship types

   int mship9Rounds2 = 0;
   int mship18Rounds2 = 0;

   int [] tmodeR4 = new int [parm.MAX_Tmodes];       // use arrays for the 16 modes of trans
   int [] tmode9R4 = new int [parm.MAX_Tmodes];
   int [] tmode18R4 = new int [parm.MAX_Tmodes];

   int tmodeOldR94 = 0;
   int tmodeOldR184 = 0;

   int nshowRounds4 = 0;
   int nshow9Rounds4 = 0;
   int nshow18Rounds4 = 0;
   int mnshowRounds4 = 0;
   int mnshow9Rounds4 = 0;
   int mnshow18Rounds4 = 0;
   int gnshowRounds4 = 0;
   int gnshow9Rounds4 = 0;
   int gnshow18Rounds4 = 0;

   int otherRounds4 = 0;
   int other9Rounds4 = 0;
   int other18Rounds4 = 0;

   int totRounds4 = 0;

   int memRounds4 = 0;
   int [] memxRounds4 = new int [parm.MAX_Mems];       // use arrays for the mem types

   int mem9Rounds4 = 0;
   int mem18Rounds4 = 0;

   int gstRounds4 = 0;
   int [] gstRnds4 = new int [parm.MAX_Guests];       // use array for the 36 guest types

   int gst9Rounds4 = 0;                    // total guest rounds
   int gst18Rounds4 = 0;
   int gst9RevRnds4 = 0;                   // total Revenue guest rounds
   int gst18RevRnds4 = 0;

   int mshipRounds4 = 0;
   int [] mshipxRounds4 = new int [parm.MAX_Mships];       // use arrays for the mship types

   int mship9Rounds4 = 0;
   int mship18Rounds4 = 0;

   long edate = 0;                             // today's date
   long mtddate = 0;                           // MTD start date
   long mtdend = 0;                            // MTD end date
   long ytddate = 0;                           // YTD start date
   long ytdend = 0;                            // YTD end date
   long lmsdate = 0;                           // Last Month start date
   long lmedate = 0;                           // Last Month end date
   int year = 0;
   int month = 0;
   int day = 0;

   int multi = 0;                 // multiple course support
   int index = 0;
   int i = 0;
   int i2 = 0;
   int count = 0;                 // number of courses

   //
   //  ints to hold stats from db table
   //
   int [] memxr9 = new int [parm.MAX_Mems];       // use arrays for the mem types
   int [] memxr18 = new int [parm.MAX_Mems];      // use arrays for the mem types

   int [] mshipxr9 = new int [parm.MAX_Mships];   // use arrays for the mship types
   int [] mshipxr18 = new int [parm.MAX_Mships];  // use arrays for the mship types

   int [] gstr9 = new int [parm.MAX_Guests];       // use array for the 36 guest types
   int [] gstr18 = new int [parm.MAX_Guests];

   int other9 = 0;
   int other18 = 0;
   int cart9 = 0;
   int cart18 = 0;
   int cady9 = 0;
   int cady18 = 0;
   int pc9 = 0;
   int pc18 = 0;
   int wa9 = 0;
   int wa18 = 0;
   int memnshow9 = 0;
   int memnshow18 = 0;
   int gstnshow9 = 0;
   int gstnshow18 = 0;
   int memunk9 = 0;
   int memunk18 = 0;
   int mshipunk9 = 0;
   int mshipunk18 = 0;

   int [] tmode9 = new int [parm.MAX_Tmodes];
   int [] tmode18 = new int [parm.MAX_Tmodes];

   int tmodeOldR9 = 0;
   int tmodeOldR18 = 0;

   //
   //  Array to hold the course names
   //
   ArrayList<String> course = new ArrayList<String>();      // unlimited courses
   
   String courseName = "";

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
   String mship1 = "";
   String mship2 = "";
   String mship3 = "";
   String mship4 = "";
   String mship5 = "";
   String mtype1 = "";
   String mtype2 = "";
   String mtype3 = "";
   String mtype4 = "";
   String mtype5 = "";
   String gtype1 = "";
   String gtype2 = "";
   String gtype3 = "";
   String gtype4 = "";
   String gtype5 = "";

   String error = "None";

   boolean found = false;
   boolean toExcel = false;

   //
   //  Get today's date and current time and calculate date & time values
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   int cal_am_pm = cal.get(Calendar.AM_PM);        // current time
   int cal_hour = cal.get(Calendar.HOUR);
   int cal_min = cal.get(Calendar.MINUTE);

   int curr_time = cal_hour;
   if (cal_am_pm == 1) {                        // if PM

      curr_time = curr_time + 12;               // convert to military time
   }

   curr_time = curr_time * 100;                 // create current time value for compare
   curr_time += cal_min;

   month++;                                     // month starts at zero

   edate = year * 10000;                        // create a edate field of yyyymmdd (for today)
   edate = edate + (month * 100);
   edate = edate + day;                         // date = yyyymmdd (for comparisons)

   mtddate = year * 10000;                      // create a MTD date
   mtddate = mtddate + (month * 100);
   mtddate = mtddate + 01;

   mtdend = edate;

   ytddate = year * 10000;                      // create a YTD date
   ytddate += 101;

   ytdend = year * 10000;                       // create a YTD end date
   ytdend += 1231;

   month--;                                     // last month

   if (month == 0) {

      month = 12;
      year = year - 1;
   }

   lmsdate = year * 10000;                      // create a Last Month Start date
   lmsdate = lmsdate + (month * 100);
   lmsdate = lmsdate + 01;

   lmedate = lmsdate + 30;                      // create a Last Month End date


   //
   //   Get multi option, member types, and guest types
   //
   multi = parm.multi;

   //
   //   Remove any guest types that are null - for tests below
   //
   for (i = 0; i < parm.MAX_Guests; i++) {

      if (parm.guest[i].equals( "" )) {

         parm.guest[i] = "$@#!^&*";      // make so it won't match player name
      }
   }

   //
   // Check for multiple courses
   //
   count = 1;                  // init to 1 course

   if (multi != 0) {           // if multiple courses supported for this club

      try {

         //
         //  Get the names of all courses for this club
         //
         course = Utilities.getCourseNames(con);     // get all the course names

         count = course.size();                      // number of courses

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Proshop Reports Page - Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error:" + exc.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }

   //
   //  Build the HTML page to display search results
   //
   //
   try{
      if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format

         toExcel = true;
         resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
      }
   }
   catch (Exception exc) {
   }

   if (!toExcel) {  // Don't print header if user requested Excel Spreadsheet Format
       out.println(SystemUtils.HeadTitle("Proshop Reports Page"));
   }
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   if (!toExcel) {  // Don't print submenu code if user requested Excel Spreadsheet Format
       SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   }
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   if (!toExcel) {     // if normal request
      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\">");

      out.println("<font size=\"3\">");
      out.println("<p><b>Course Statistics</b><br></font><font size=\"2\">");
      out.println("<b>Note:</b> Percentages are rounded down to whole number.<br>");
      out.println("Percentages for Member Types and Membership Types are of 'Rounds by Members'.<br>");
      out.println("Percentages for Guest Types are of 'Rounds by Guests'.  Others are of 'Total Rounds'.</p>");

      out.println("<form method=\"post\" action=\"Proshop_reports\" target=\"_blank\">");
      out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"round\" value=\"all\">");
      out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
      out.println("</font>");
   }

   courseName = "";            // init as not multi

   //
   // execute searches and display for each course
   //
   for (index=0; index < count; index++) {       // count = # of courses (1 if not multi)

      if (multi != 0) {                        // if multiple courses supported for this club

         courseName = course.get(index);      // get course name
      }

      
      //
      //  init count fields for each course
      //
      memUnknown1 = 0;
      memUnknown2 = 0;
      memUnknown4 = 0;
      memUnknown9 = 0;
      memUnknown18 = 0;
      mshipUnknown1 = 0;
      mshipUnknown2 = 0;
      mshipUnknown4 = 0;
      mshipUnknown9 = 0;
      mshipUnknown18 = 0;

      nshowRounds1 = 0;
      nshow9Rounds1 = 0;
      nshow18Rounds1 = 0;
      mnshowRounds1 = 0;
      mnshow9Rounds1 = 0;
      mnshow18Rounds1 = 0;
      gnshowRounds1 = 0;
      gnshow9Rounds1 = 0;
      gnshow18Rounds1 = 0;

      otherRounds1 = 0;
      other9Rounds1 = 0;
      other18Rounds1 = 0;

      totRounds1 = 0;
      memRounds1 = 0;
      mem9Rounds1 = 0;
      mem18Rounds1 = 0;

      gstRounds1 = 0;
      gst9Rounds1 = 0;
      gst18Rounds1 = 0;
      gst9RevRnds1 = 0;
      gst18RevRnds1 = 0;

      mship9Rounds1 = 0;
      mship18Rounds1 = 0;

      nshowRounds2 = 0;
      nshow9Rounds2 = 0;
      nshow18Rounds2 = 0;
      mnshowRounds2 = 0;
      mnshow9Rounds2 = 0;
      mnshow18Rounds2 = 0;
      gnshowRounds2 = 0;
      gnshow9Rounds2 = 0;
      gnshow18Rounds2 = 0;

      otherRounds2 = 0;
      other9Rounds2 = 0;
      other18Rounds2 = 0;

      totRounds2 = 0;
      memRounds2 = 0;
      mem9Rounds2 = 0;
      mem18Rounds2 = 0;

      gstRounds2 = 0;
      gst9Rounds2 = 0;
      gst18Rounds2 = 0;
      gst9RevRnds2 = 0;
      gst18RevRnds2 = 0;

      mship9Rounds2 = 0;
      mship18Rounds2 = 0;

      nshowRounds4 = 0;
      nshow9Rounds4 = 0;
      nshow18Rounds4 = 0;
      mnshowRounds4 = 0;
      mnshow9Rounds4 = 0;
      mnshow18Rounds4 = 0;
      gnshowRounds4 = 0;
      gnshow9Rounds4 = 0;
      gnshow18Rounds4 = 0;

      otherRounds4 = 0;
      other9Rounds4 = 0;
      other18Rounds4 = 0;

      totRounds4 = 0;
      memRounds4 = 0;
      mem9Rounds4 = 0;
      mem18Rounds4 = 0;

      gstRounds4 = 0;
      gst9Rounds4 = 0;
      gst18Rounds4 = 0;
      gst9RevRnds4 = 0;
      gst18RevRnds4 = 0;

      mship9Rounds4 = 0;
      mship18Rounds4 = 0;

      tmodeOldR9 = 0;
      tmodeOldR18 = 0;
      tmodeOldR91 = 0;
      tmodeOldR181 = 0;
      tmodeOldR92 = 0;
      tmodeOldR182 = 0;
      tmodeOldR94 = 0;
      tmodeOldR184 = 0;

      //
      //  Init the Modes of Trans arrays
      //
      for (i = 0; i < parm.MAX_Tmodes; i++) {
         tmodeR1[i] = 0;
         tmodeR2[i] = 0;
         tmodeR4[i] = 0;
         tmode9R1[i] = 0;
         tmode9R2[i] = 0;
         tmode9R4[i] = 0;
         tmode18R1[i] = 0;
         tmode18R2[i] = 0;
         tmode18R4[i] = 0;
      }

      //
      //  Init the Guest arrays
      //
      for (i = 0; i < parm.MAX_Guests; i++) {
         gstRnds4[i] = 0;
         gstRnds2[i] = 0;
         gst1Rnds9[i] = 0;
         gst1Rnds18[i] = 0;
         gstRnds1[i] = 0;
      }

      //
      //  Init the Mem Type arrays
      //
      for (i = 0; i < parm.MAX_Mems; i++) {
         memxRounds1[i] = 0;
         memxRounds2[i] = 0;
         memxRounds4[i] = 0;
         memxRounds9[i] = 0;
         memxRounds18[i] = 0;
      }

      //
      //  Init the Mship Type arrays
      //
      for (i = 0; i < parm.MAX_Mships; i++) {
         mshipxRounds1[i] = 0;
         mshipxRounds2[i] = 0;
         mshipxRounds4[i] = 0;
         mshipxRounds9[i] = 0;
         mshipxRounds18[i] = 0;
      }

      //
      // VARIBALES NOW INITIALIZED


      out.println("<!--");
      out.println(" ytddate=" + ytddate);
      out.println(" ytdend=" + ytdend);
      out.println(" mtddate=" + mtddate);
      out.println(" mtdend=" + mtdend);
      out.println(" lmsdate=" + lmsdate);
      out.println(" lmedate=" + lmedate);
      out.println("-->");

      int x = 0;

      //
      // use the dates provided to search the stats table
      //
      try {

         // we need to repopulate these values since they will only contain the ones
         // currently defined in the system and we need to include all of them used
         // within the time frame we are searching for.
/*
         for (i = 0; i < parm.MAX_Mems; i++) {

             parm.mem[i] = "";
         }

         for (i=0; i<parm.MAX_Mships; i++) {

             parm.mship[i] = "";
         }

         PreparedStatement pstmt = con.prepareStatement ("");


         pstmt.clearParameters();
         pstmt.setString(1, courseName);
         pstmt.setLong(2, ytddate);
         pstmt.setLong(3, ytdend);

         rs = pstmt.executeQuery();

         i = 0;

         while (rs.next()) {

             parm.mem[i] = rs.getString(1);

         }
*/      




         //
         //  Get the System Parameters for this Course
         //
         getParms.getCourse(con, parmc, courseName);

         //
         //  Statement for member type counts
         //
         PreparedStatement pstmt_mtype = con.prepareStatement (
            "SELECT m_type, SUM(p_9), SUM(p_18) FROM ( " +
            "SELECT IF(m_type='', IF(null_user=1,'Unknown','Removed from Member Database'), m_type) AS m_type, p_9, p_18 " +
            "FROM ( " +
                "SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, mtype1 AS m_type, IF(IFNULL(username1,'')='',1,0) AS null_user " +
                "FROM teepast2 " +
                "WHERE courseName = ? AND date >= ? AND date <= ? AND show1 = 1 AND (username1 <> '' AND username1 IS NOT NULL) " +
                "UNION ALL " +
                "SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, mtype2 AS m_type, IF(IFNULL(username2,'')='',1,0) AS null_user " +
                "FROM teepast2  " +
                "WHERE courseName = ? AND date >= ? AND date <= ? AND show2 = 1 AND (username2 <> '' AND username2 IS NOT NULL) " +
                "UNION ALL " +
                "SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, mtype3 AS m_type, IF(IFNULL(username3,'')='',1,0) AS null_user " +
                "FROM teepast2 " +
                "WHERE courseName = ? AND date >= ? AND date <= ? AND show3 = 1 AND (username3 <> '' AND username3 IS NOT NULL) " +
                "UNION ALL " +
                "SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, mtype4 AS m_type, IF(IFNULL(username4,'')='',1,0) AS null_user " +
                "FROM teepast2 " +
                "WHERE courseName = ? AND date >= ? AND date <= ? AND show4 = 1 AND (username4 <> '' AND username4 IS NOT NULL) " +
                "UNION ALL " +
                "SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, mtype5 AS m_type, IF(IFNULL(username5,'')='',1,0) AS null_user " +
                "FROM teepast2 " +
                "WHERE courseName = ? AND date >= ? AND date <= ? AND show5 = 1 AND (username5 <> '' AND username5 IS NOT NULL) " +
                "UNION ALL " +
                "SELECT (0) AS p_9, (0) AS p_18, m2b.m_type AS m_type, (1) AS null_user " +
                "FROM member2b m2b " +
                "WHERE m2b.m_type IS NOT NULL " +
                "GROUP BY m2b.m_type " +
            ") AS d_table ) AS g_table GROUP BY m_type WITH ROLLUP;");


         error = "Get YTD Member Type Counts";


         //
         //  Get YTD counts for Member Type
         //
         pstmt_mtype.clearParameters();
         pstmt_mtype.setString(1, courseName);
         pstmt_mtype.setLong(2, ytddate);
         pstmt_mtype.setLong(3, ytdend);

         pstmt_mtype.setString(4, courseName);
         pstmt_mtype.setLong(5, ytddate);
         pstmt_mtype.setLong(6, ytdend);

         pstmt_mtype.setString(7, courseName);
         pstmt_mtype.setLong(8, ytddate);
         pstmt_mtype.setLong(9, ytdend);

         pstmt_mtype.setString(10, courseName);
         pstmt_mtype.setLong(11, ytddate);
         pstmt_mtype.setLong(12, ytdend);

         pstmt_mtype.setString(13, courseName);
         pstmt_mtype.setLong(14, ytddate);
         pstmt_mtype.setLong(15, ytdend);

         rs = pstmt_mtype.executeQuery();

         i = 0;

         while (rs.next()) {

            if (rs.getString(1) == null) {

                // grand total
                mem9Rounds1 = rs.getInt(2);                 // Total Member Rounds - 9
                mem18Rounds1 = rs.getInt(3);                // Total Member Rounds - 18
                memRounds1 = mem9Rounds1 + mem18Rounds1;    // Total Member Rounds

            } else {

                found = false;
                loop1:
                for (x = 0; x < parm.MAX_Mems; x++) {

                    if (parm.mem[x].equals(rs.getString(1))) {

                        memxRounds1[x] = rs.getInt(2) + rs.getInt(3);
                        found = true;
                        break loop1;

                    } else if (parm.mem[x].equals("")) {

                        // found an empty one so we must be at the end of the defined types
                        break loop1;

                    }
                }

                // came across a member type that is no longer defined in the system
                if (!found && x < parm.MAX_Mems) {

                    // lets add it to our array for display
                    parm.mem[x] = rs.getString(1);
                    memxRounds1[x] = rs.getInt(2) + rs.getInt(3);

                }

            }

         } // end of while



         //
         // Member Types count for MTD
         //
         error = "Get MTD Member Type Counts";

         pstmt_mtype.clearParameters();
         pstmt_mtype.setString(1, courseName);
         pstmt_mtype.setLong(2, mtddate);
         pstmt_mtype.setLong(3, mtdend);

         pstmt_mtype.setString(4, courseName);
         pstmt_mtype.setLong(5, mtddate);
         pstmt_mtype.setLong(6, mtdend);

         pstmt_mtype.setString(7, courseName);
         pstmt_mtype.setLong(8, mtddate);
         pstmt_mtype.setLong(9, mtdend);

         pstmt_mtype.setString(10, courseName);
         pstmt_mtype.setLong(11, mtddate);
         pstmt_mtype.setLong(12, mtdend);

         pstmt_mtype.setString(13, courseName);
         pstmt_mtype.setLong(14, mtddate);
         pstmt_mtype.setLong(15, mtdend);

         rs = pstmt_mtype.executeQuery();

         i = 0;

         while (rs.next()) {

            if (rs.getString(1) == null) {

                // grand total
                mem9Rounds2 = rs.getInt(2);                 // Total Member Rounds - 9
                mem18Rounds2 = rs.getInt(3);                // Total Member Rounds - 18
                memRounds2 = mem9Rounds2 + mem18Rounds2;    // Total Member Rounds

            } else {

                found = false;
                loop1:
                for (x = 0; x < parm.MAX_Mems; x++) {

                    if (parm.mem[x].equals(rs.getString(1))) {

                        memxRounds2[x] = rs.getInt(2) + rs.getInt(3);
                        found = true;
                        break loop1;

                    } else if (parm.mem[x].equals("")) {

                        // found an empty one so we must be at the end of the defined types
                        break loop1;

                    }
                }

                // came across a member type that is no longer defined in the system
                if (!found && x < parm.MAX_Mems) {

                    // lets add it to our array for display
                    parm.mem[x] = rs.getString(1);
                    memxRounds2[x] = rs.getInt(2) + rs.getInt(3);

                }

            }

         }


         //
         // Member Types count for LM
         //
         error = "Get LM Member Type Counts";

         pstmt_mtype.clearParameters();
         pstmt_mtype.setString(1, courseName);
         pstmt_mtype.setLong(2, lmsdate);
         pstmt_mtype.setLong(3, lmedate);

         pstmt_mtype.setString(4, courseName);
         pstmt_mtype.setLong(5, lmsdate);
         pstmt_mtype.setLong(6, lmedate);

         pstmt_mtype.setString(7, courseName);
         pstmt_mtype.setLong(8, lmsdate);
         pstmt_mtype.setLong(9, lmedate);

         pstmt_mtype.setString(10, courseName);
         pstmt_mtype.setLong(11, lmsdate);
         pstmt_mtype.setLong(12, lmedate);

         pstmt_mtype.setString(13, courseName);
         pstmt_mtype.setLong(14, lmsdate);
         pstmt_mtype.setLong(15, lmedate);

         rs = pstmt_mtype.executeQuery();

         i = 0;

         while (rs.next()) {

            if (rs.getString(1) == null) {

                // grand total
                mem9Rounds4 = rs.getInt(2);                 // Total Member Rounds - 9
                mem18Rounds4 = rs.getInt(3);                // Total Member Rounds - 18
                memRounds4 = mem9Rounds4 + mem18Rounds4;    // Total Member Rounds

            } else {

                found = false;
                loop1:
                for (x = 0; x < parm.MAX_Mems; x++) {

                    if (parm.mem[x].equals(rs.getString(1))) {

                        memxRounds4[x] = rs.getInt(2) + rs.getInt(3);
                        found = true;
                        break loop1;

                    } else if (parm.mem[x].equals("")) {

                        // found an empty one so we must be at the end of the defined types
                        break loop1;

                    }
                }

                // came across a member type that is no longer defined in the system
                if (!found && x < parm.MAX_Mems) {

                    // lets add it to our array for display
                    parm.mem[x] = rs.getString(1);
                    memxRounds4[x] = rs.getInt(2) + rs.getInt(3);

                }

            }

         }

         pstmt_mtype.close();




         //
         //  Statement for membership counts
         //
         PreparedStatement pstmt_mship = con.prepareStatement (
               "SELECT IF(m_ship='', 'Removed',m_ship) AS d_m_ship, SUM(p_9), SUM(p_18) " +
               "FROM ( " +

                   "SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, mship1 AS m_ship " +
	               "FROM teepast2 " +
	               "WHERE courseName = ? AND date >= ? AND date <= ? AND show1 = 1 AND (username1 <> '' AND username1 IS NOT NULL) " +

	               "UNION ALL " +

	               "SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, mship2 AS m_ship " +
	               "FROM teepast2 " +
	               "WHERE courseName = ? AND date >= ? AND date <= ? AND show2 = 1 AND (username2 <> '' AND username2 IS NOT NULL) " +

	               "UNION ALL " +

	               "SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, mship3 AS m_ship " +
	               "FROM teepast2 " +
	               "WHERE courseName = ? AND date >= ? AND date <= ? AND show3 = 1 AND (username3 <> '' AND username3 IS NOT NULL) " +

	               "UNION ALL " +

	               "SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, mship4 AS m_ship " +
	               "FROM teepast2 " +
	               "WHERE courseName = ? AND date >= ? AND date <= ? AND show4 = 1 AND (username4 <> '' AND username4 IS NOT NULL) " +

	               "UNION ALL " +

	               "SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, mship5 AS m_ship " +
	               "FROM teepast2 " +
	               "WHERE courseName = ? AND date >= ? AND date <= ? AND show5 = 1 AND (username5 <> '' AND username5 IS NOT NULL) " +

               ") AS d_table " +
               "GROUP BY d_m_ship");
/*
                    SELECT IF(m_ship='', 'Removed',m_ship) AS m_ship, SUM(p_9), SUM(p_18)
                    FROM (

                    SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, mship1 AS m_ship
                    FROM teepast2
                    WHERE courseName = "" AND date >= 20090101 AND date <= 20090422 AND show1 = 1 AND (username1 <> '' AND username1 IS NOT NULL)

                    UNION ALL

                    SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, mship2 AS m_ship
                    FROM teepast2
                    WHERE courseName = "" AND date >= 20090101 AND date <= 20090422 AND show2 = 1 AND (username2 <> '' AND username2 IS NOT NULL)

                    UNION ALL

                    SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, mship3 AS m_ship
                    FROM teepast2
                    WHERE courseName = "" AND date >= 20090101 AND date <= 20090422 AND show3 = 1 AND (username3 <> '' AND username3 IS NOT NULL)

                    UNION ALL

                    SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, mship4 AS m_ship
                    FROM teepast2
                    WHERE courseName = "" AND date >= 20090101 AND date <= 20090422 AND show4 = 1 AND (username4 <> '' AND username4 IS NOT NULL)

                    UNION ALL

                    SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, mship5 AS m_ship
                    FROM teepast2
                    WHERE courseName = "" AND date >= 20090101 AND date <= 20090422 AND show5 = 1 AND (username5 <> '' AND username5 IS NOT NULL)

                    ) AS d_table GROUP BY m_ship;
*/

         error = "Get YTD Membership Counts";

         //
         //  Get YTD counts for Membership Type
         //
         pstmt_mship.clearParameters();
         pstmt_mship.setString(1, courseName);
         pstmt_mship.setLong(2, ytddate);
         pstmt_mship.setLong(3, ytdend);

         pstmt_mship.setString(4, courseName);
         pstmt_mship.setLong(5, ytddate);
         pstmt_mship.setLong(6, ytdend);

         pstmt_mship.setString(7, courseName);
         pstmt_mship.setLong(8, ytddate);
         pstmt_mship.setLong(9, ytdend);

         pstmt_mship.setString(10, courseName);
         pstmt_mship.setLong(11, ytddate);
         pstmt_mship.setLong(12, ytdend);

         pstmt_mship.setString(13, courseName);
         pstmt_mship.setLong(14, ytddate);
         pstmt_mship.setLong(15, ytdend);

         rs = pstmt_mship.executeQuery();

         i = 0;

         while (rs.next()) {

             // sub total
             //mshipxRounds1[i] = rs.getInt(2) + rs.getInt(3);
             //i++;

             found = false;
             loop1:
             for (x = 0; x < parm.MAX_Mships; x++) {

                 if (parm.mship[x].equals(rs.getString(1))) {

                     mshipxRounds1[x] = rs.getInt(2) + rs.getInt(3);
                     found = true;
                     break loop1;

                 } else if (parm.mship[x].equals("")) {

                     // found an empty one so we must be at the end of the defined types
                     break loop1;

                 }
             }

             // came across a member type that is no longer defined in the system
             if (!found && x < parm.MAX_Mships) {

                 // lets add it to our array for display
                 parm.mship[x] = rs.getString(1);
                 mshipxRounds1[x] = rs.getInt(2) + rs.getInt(3);

             }

         }


         //
         // Membership count for MTD
         //
         error = "Get MTD Membersip Counts";

         pstmt_mship.clearParameters();
         pstmt_mship.setString(1, courseName);
         pstmt_mship.setLong(2, mtddate);
         pstmt_mship.setLong(3, mtdend);

         pstmt_mship.setString(4, courseName);
         pstmt_mship.setLong(5, mtddate);
         pstmt_mship.setLong(6, mtdend);

         pstmt_mship.setString(7, courseName);
         pstmt_mship.setLong(8, mtddate);
         pstmt_mship.setLong(9, mtdend);

         pstmt_mship.setString(10, courseName);
         pstmt_mship.setLong(11, mtddate);
         pstmt_mship.setLong(12, mtdend);

         pstmt_mship.setString(13, courseName);
         pstmt_mship.setLong(14, mtddate);
         pstmt_mship.setLong(15, mtdend);

         rs = pstmt_mship.executeQuery();

         i = 0;

         while (rs.next()) {

            found = false;
            loop1:
            for (x = 0; x < parm.MAX_Mships; x++) {

                if (parm.mship[x].equals(rs.getString(1))) {

                    mshipxRounds2[x] = rs.getInt(2) + rs.getInt(3);
                    found = true;
                    break loop1;

                } else if (parm.mship[x].equals("")) {

                    // found an empty one so we must be at the end of the defined types
                    break loop1;

                }
            }

            // came across a member type that is no longer defined in the system
            if (!found && x < parm.MAX_Mships) {

                // lets add it to our array for display
                parm.mship[x] = rs.getString(1);
                mshipxRounds2[x] = rs.getInt(2) + rs.getInt(3);

            }

         }


         //
         // Membership count for LM
         //
         error = "Get LM Membership Counts";

         pstmt_mship.clearParameters();
         pstmt_mship.setString(1, courseName);
         pstmt_mship.setLong(2, lmsdate);
         pstmt_mship.setLong(3, lmedate);

         pstmt_mship.setString(4, courseName);
         pstmt_mship.setLong(5, lmsdate);
         pstmt_mship.setLong(6, lmedate);

         pstmt_mship.setString(7, courseName);
         pstmt_mship.setLong(8, lmsdate);
         pstmt_mship.setLong(9, lmedate);

         pstmt_mship.setString(10, courseName);
         pstmt_mship.setLong(11, lmsdate);
         pstmt_mship.setLong(12, lmedate);

         pstmt_mship.setString(13, courseName);
         pstmt_mship.setLong(14, lmsdate);
         pstmt_mship.setLong(15, lmedate);

         rs = pstmt_mship.executeQuery();

         i = 0;

         while (rs.next()) {


             found = false;
             loop1:
             for (x = 0; x < parm.MAX_Mships; x++) {

                 if (parm.mship[x].equals(rs.getString(1))) {

                     mshipxRounds4[x] = rs.getInt(2) + rs.getInt(3);
                     found = true;
                     break loop1;

                 } else if (parm.mship[x].equals("")) {

                     // found an empty one so we must be at the end of the defined types
                     break loop1;

                 }
             }

             // came across a member type that is no longer defined in the system
             if (!found && x < parm.MAX_Mships) {

                 // lets add it to our array for display
                 parm.mship[x] = rs.getString(1);
                 mshipxRounds4[x] = rs.getInt(2) + rs.getInt(3);

             }

         } // end of while

         pstmt_mship.close();










         //
         //  Statement for guest type counts
         //
         PreparedStatement pstmt_gtype = con.prepareStatement (
               "SELECT guest_type, SUM(p_9), SUM(p_18), SUM(grev18) AS revenue18, SUM(grev9) AS revenue9 FROM ( " +
               "SELECT IF(guest_type='','Unknown',guest_type) AS guest_type, p_9, p_18, grev18, grev9 FROM ( " +

               "SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, gtype1 AS guest_type, IF(p91=0 && grev1=1,1,0) AS grev18, IF(p91=1 && grev1=1,1,0) AS grev9 " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show1 = 1 AND ((username1 = '' OR username1 IS NULL) AND (player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL)) " +

               "UNION ALL " +

               "SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, gtype2 AS guest_type, IF(p92=0 && grev2=1,1,0) AS grev18, IF(p92=1 && grev2=1,1,0) AS grev9 " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show2 = 1 AND ((username2 = '' OR username2 IS NULL) AND (player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL)) " +

               "UNION ALL " +

               "SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, gtype3 AS guest_type, IF(p93=0 && grev3=1,1,0) AS grev18, IF(p93=1 && grev3=1,1,0) AS grev9 " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show3 = 1 AND ((username3 = '' OR username3 IS NULL) AND (player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL)) " +

               "UNION ALL " +

               "SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, gtype4 AS guest_type, IF(p94=0 && grev4=1,1,0) AS grev18, IF(p94=1 && grev4=1,1,0) AS grev9 " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show4 = 1 AND ((username4 = '' OR username4 IS NULL) AND (player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL)) " +

               "UNION ALL " +

               "SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, gtype5 AS guest_type, IF(p95=0 && grev5=1,1,0) AS grev18, IF(p95=1 && grev5=1,1,0) AS grev9 " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show5 = 1 AND ((username5 = '' OR username5 IS NULL) AND (player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL)) " +

               ") AS d_table ) AS g_table GROUP BY guest_type WITH ROLLUP;");

         error = "Get YTD Guest Type Counts";

          /*
               SELECT IF(guest_type='','Unknown',guest_type) AS guest_type, SUM(p_9), SUM(p_18), SUM(grev18)  AS revenue18, SUM(grev9) AS revenue9
               FROM (

               SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, gtype1 AS guest_type, IF(p91=0 && grev1=1,1,0) AS grev18, IF(p91=1 && grev1=1,1,0) AS grev9
               FROM teepast2
               WHERE courseName = "" AND date >= 20090101 AND date <= 20090514 AND show1 = 1
                    AND ((username1 = '' OR username1 IS NULL) AND (player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL))

               UNION ALL

               SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, gtype2 AS guest_type, IF(p92=0 && grev2=1,1,0) AS grev18, IF(p92=1 && grev2=1,1,0) AS grev9
               FROM teepast2
               WHERE courseName = "" AND date >= 20090101 AND date <= 20090514 AND show2 = 1
                    AND ((username2 = '' OR username2 IS NULL) AND (player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL))

               UNION ALL

               SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, gtype3 AS guest_type, IF(p93=0 && grev3=1,1,0) AS grev18, IF(p93=1 && grev3=1,1,0) AS grev9
               FROM teepast2
               WHERE courseName = "" AND date >= 20090101 AND date <= 20090514 AND show3 = 1
                    AND ((username3 = '' OR username3 IS NULL) AND (player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL))

               UNION ALL

               SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, gtype4 AS guest_type, IF(p94=0 && grev4=1,1,0) AS grev18, IF(p94=1 && grev4=1,1,0) AS grev9
               FROM teepast2
               WHERE courseName = "" AND date >= 20090101 AND date <= 20090514 AND show4 = 1
                    AND ((username4 = '' OR username4 IS NULL) AND (player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL))

               UNION ALL

               SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, gtype5 AS guest_type, IF(p95=0 && grev5=1,1,0) AS grev18, IF(p95=1 && grev5=1,1,0) AS grev9
               FROM teepast2
               WHERE courseName = "" AND date >= 20090101 AND date <= 20090514 AND show5 = 1
                    AND ((username5 = '' OR username5 IS NULL) AND (player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL))

               ) AS d_table
               GROUP BY guest_type WITH ROLLUP;

       */
         //
         //  Get YTD counts for Guest Type
         //
         pstmt_gtype.clearParameters();
         pstmt_gtype.setString(1, courseName);
         pstmt_gtype.setLong(2, ytddate);
         pstmt_gtype.setLong(3, ytdend);

         pstmt_gtype.setString(4, courseName);
         pstmt_gtype.setLong(5, ytddate);
         pstmt_gtype.setLong(6, ytdend);

         pstmt_gtype.setString(7, courseName);
         pstmt_gtype.setLong(8, ytddate);
         pstmt_gtype.setLong(9, ytdend);

         pstmt_gtype.setString(10, courseName);
         pstmt_gtype.setLong(11, ytddate);
         pstmt_gtype.setLong(12, ytdend);

         pstmt_gtype.setString(13, courseName);
         pstmt_gtype.setLong(14, ytddate);
         pstmt_gtype.setLong(15, ytdend);

         rs = pstmt_gtype.executeQuery();

         i = 0;

         while (rs.next()) {

            if (rs.getString(1) == null) {

                // grand total
                gst9Rounds1 = rs.getInt(2);                 // Total Guest Rounds - 9
                gst18Rounds1 = rs.getInt(3);                // Total Guest Rounds - 18
                gstRounds1 = gst9Rounds1 + gst18Rounds1;    // Total Guest Rounds

                gst18RevRnds1 = rs.getInt(4);               // Total Revenue Guest Rounds - 18
                gst9RevRnds1 = rs.getInt(5);                // Total Revenue Guest Rounds - 9

            } else {

                found = false;
                loop1:
                for (x = 0; x < parm.MAX_Guests; x++) {

                    if (parm.guest[x].equals(rs.getString(1))) {

                        gstRnds1[x] = rs.getInt(2) + rs.getInt(3);
                        found = true;
                        break loop1;

                    } else if (parm.guest[x].equals("$@#!^&*")) {

                        // found an empty one so we must be at the end of the defined types
                        break loop1;

                    }
                }

                // came across a member type that is no longer defined in the system
                if (!found && x < parm.MAX_Guests) {

                    // lets add it to our array for display
                    parm.guest[x] = rs.getString(1);
                    gstRnds1[x] = rs.getInt(2) + rs.getInt(3);

                }

            }

         }


         //
         //  Get MTD counts for Guest Type
         //
         pstmt_gtype.clearParameters();
         pstmt_gtype.setString(1, courseName);
         pstmt_gtype.setLong(2, mtddate);
         pstmt_gtype.setLong(3, mtdend);

         pstmt_gtype.setString(4, courseName);
         pstmt_gtype.setLong(5, mtddate);
         pstmt_gtype.setLong(6, mtdend);

         pstmt_gtype.setString(7, courseName);
         pstmt_gtype.setLong(8, mtddate);
         pstmt_gtype.setLong(9, mtdend);

         pstmt_gtype.setString(10, courseName);
         pstmt_gtype.setLong(11, mtddate);
         pstmt_gtype.setLong(12, mtdend);

         pstmt_gtype.setString(13, courseName);
         pstmt_gtype.setLong(14, mtddate);
         pstmt_gtype.setLong(15, mtdend);

         rs = pstmt_gtype.executeQuery();

         i = 0;

         while (rs.next()) {

            if (rs.getString(1) == null) {

                // grand total
                gst9Rounds2 = rs.getInt(2);                 // Total Guest Rounds - 9
                gst18Rounds2 = rs.getInt(3);                // Total Guest Rounds - 18
                gstRounds2 = gst9Rounds2 + gst18Rounds2;    // Total Guest Rounds

                gst18RevRnds2 = rs.getInt(4);               // Total Revenue Guest Rounds - 18
                gst9RevRnds2 = rs.getInt(5);                // Total Revenue Guest Rounds - 9

            } else {

                found = false;
                loop1:
                for (x = 0; x < parm.MAX_Guests; x++) {

                    if (parm.guest[x].equals(rs.getString(1))) {

                        gstRnds2[x] = rs.getInt(2) + rs.getInt(3);
                        found = true;
                        break loop1;

                    } else if (parm.guest[x].equals("$@#!^&*")) {

                        // found an empty one so we must be at the end of the defined types
                        break loop1;

                    }
                }

                // came across a member type that is no longer defined in the system
                if (!found && x < parm.MAX_Guests) {

                    // lets add it to our array for display
                    parm.guest[x] = rs.getString(1);
                    gstRnds2[x] = rs.getInt(2) + rs.getInt(3);

                }

            }

         }


         //
         //  Get LM counts for Guest Type
         //
         pstmt_gtype.clearParameters();
         pstmt_gtype.setString(1, courseName);
         pstmt_gtype.setLong(2, lmsdate);
         pstmt_gtype.setLong(3, lmedate);

         pstmt_gtype.setString(4, courseName);
         pstmt_gtype.setLong(5, lmsdate);
         pstmt_gtype.setLong(6, lmedate);

         pstmt_gtype.setString(7, courseName);
         pstmt_gtype.setLong(8, lmsdate);
         pstmt_gtype.setLong(9, lmedate);

         pstmt_gtype.setString(10, courseName);
         pstmt_gtype.setLong(11, lmsdate);
         pstmt_gtype.setLong(12, lmedate);

         pstmt_gtype.setString(13, courseName);
         pstmt_gtype.setLong(14, lmsdate);
         pstmt_gtype.setLong(15, lmedate);

         rs = pstmt_gtype.executeQuery();

         i = 0;

         while (rs.next()) {

            if (rs.getString(1) == null) {

                // grand total
                gst9Rounds4 = rs.getInt(2);                 // Total Guest Rounds - 9
                gst18Rounds4 = rs.getInt(3);                // Total Guest Rounds - 18
                gstRounds4 = gst9Rounds4 + gst18Rounds4;    // Total Guest Rounds

                gst18RevRnds4 = rs.getInt(4);               // Total Revenue Guest Rounds - 18
                gst9RevRnds4 = rs.getInt(5);                // Total Revenue Guest Rounds - 9

            } else {

                found = false;
                loop1:
                for (x = 0; x < parm.MAX_Guests; x++) {

                    if (parm.guest[x].equals(rs.getString(1))) {

                        gstRnds4[x] = rs.getInt(2) + rs.getInt(3);
                        found = true;
                        break loop1;

                    } else if (parm.guest[x].equals("$@#!^&*")) {

                        // found an empty one so we must be at the end of the defined types
                        break loop1;

                    }
                }

                // came across a member type that is no longer defined in the system
                if (!found && x < parm.MAX_Guests) {

                    // lets add it to our array for display
                    parm.guest[x] = rs.getString(1);
                    gstRnds4[x] = rs.getInt(2) + rs.getInt(3);

                }

            }

         }

         pstmt_gtype.close();





         //
         // Total Rounds Played 9/18
         //
         PreparedStatement pstmt_totals = con.prepareStatement (
         "SELECT SUM(p_9), SUM(p_18) " +
         "FROM ( " +

         "SELECT p91*(show1=1) AS p_9, IF(p91=0,1,0)*(show1=1) AS p_18 " +
         "FROM teepast2 " +
         "WHERE courseName = ? AND date >= ? AND date <= ? AND (player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL) " +

         "UNION ALL " +

         "SELECT p92*(show2=1) AS p_9, IF(p92=0,1,0)*(show2=1) AS p_18 " +
         "FROM teepast2 " +
         "WHERE courseName = ? AND date >= ? AND date <= ? AND (player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL) " +

         "UNION ALL " +

         "SELECT p93*(show3=1) AS p_9, IF(p93=0,1,0)*(show3=1) AS p_18 " +
         "FROM teepast2 " +
         "WHERE courseName = ? AND date >= ? AND date <= ? AND (player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL) " +

         "UNION ALL " +

         "SELECT p94*(show4=1) AS p_9, IF(p94=0,1,0)*(show4=1) AS p_18 " +
         "FROM teepast2 " +
         "WHERE courseName = ? AND date >= ? AND date <= ? AND (player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL) " +

         "UNION ALL " +

         "SELECT p95*(show5=1) AS p_9, IF(p95=0,1,0)*(show5=1) AS p_18 " +
         "FROM teepast2 " +
         "WHERE courseName = ? AND date >= ? AND date <= ? AND (player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL) ) AS d_table");

         //
         //  Get YTD total rounds played
         //
         pstmt_totals.clearParameters();
         pstmt_totals.setString(1, courseName);
         pstmt_totals.setLong(2, ytddate);
         pstmt_totals.setLong(3, ytdend);

         pstmt_totals.setString(4, courseName);
         pstmt_totals.setLong(5, ytddate);
         pstmt_totals.setLong(6, ytdend);

         pstmt_totals.setString(7, courseName);
         pstmt_totals.setLong(8, ytddate);
         pstmt_totals.setLong(9, ytdend);

         pstmt_totals.setString(10, courseName);
         pstmt_totals.setLong(11, ytddate);
         pstmt_totals.setLong(12, ytdend);

         pstmt_totals.setString(13, courseName);
         pstmt_totals.setLong(14, ytddate);
         pstmt_totals.setLong(15, ytdend);

         rs = pstmt_totals.executeQuery();

         i = 0;

         if (rs.next()) {

             // totals
             totRounds1 = rs.getInt(1) + rs.getInt(2);

         }



         //
         //  Get MTD total rounds played
         //
         pstmt_totals.clearParameters();
         pstmt_totals.setString(1, courseName);
         pstmt_totals.setLong(2, mtddate);
         pstmt_totals.setLong(3, mtdend);

         pstmt_totals.setString(4, courseName);
         pstmt_totals.setLong(5, mtddate);
         pstmt_totals.setLong(6, mtdend);

         pstmt_totals.setString(7, courseName);
         pstmt_totals.setLong(8, mtddate);
         pstmt_totals.setLong(9, mtdend);

         pstmt_totals.setString(10, courseName);
         pstmt_totals.setLong(11, mtddate);
         pstmt_totals.setLong(12, mtdend);

         pstmt_totals.setString(13, courseName);
         pstmt_totals.setLong(14, mtddate);
         pstmt_totals.setLong(15, mtdend);

         rs = pstmt_totals.executeQuery();

         i = 0;

         if (rs.next()) {

             // totals
             totRounds2 = rs.getInt(1) + rs.getInt(2);

         }



         //
         //  Get LM total rounds played
         //
         pstmt_totals.clearParameters();
         pstmt_totals.setString(1, courseName);
         pstmt_totals.setLong(2, lmsdate);
         pstmt_totals.setLong(3, lmedate);

         pstmt_totals.setString(4, courseName);
         pstmt_totals.setLong(5, lmsdate);
         pstmt_totals.setLong(6, lmedate);

         pstmt_totals.setString(7, courseName);
         pstmt_totals.setLong(8, lmsdate);
         pstmt_totals.setLong(9, lmedate);

         pstmt_totals.setString(10, courseName);
         pstmt_totals.setLong(11, lmsdate);
         pstmt_totals.setLong(12, lmedate);

         pstmt_totals.setString(13, courseName);
         pstmt_totals.setLong(14, lmsdate);
         pstmt_totals.setLong(15, lmedate);

         rs = pstmt_totals.executeQuery();

         i = 0;

         if (rs.next()) {

             // totals
             totRounds4 = rs.getInt(1) + rs.getInt(2);

         }

         pstmt_totals.close();



         //
         // Total No-Shows 9/18
         //
         PreparedStatement pstmt_noshows = con.prepareStatement ("" +
             "SELECT SUM(p_9ns), SUM(p_18ns) " +

             "FROM ( " +

             "SELECT p91*(show1<>1) AS p_9ns, IF(p91=0,1,0)*(show1<>1) AS p_18ns " +
             "FROM teepast2 " +
             "WHERE courseName = ? AND date >= ? AND date <= ? AND username1 <> '' AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL " +

             "UNION ALL " +

             "SELECT p92*(show2<>1) AS p_9ns, IF(p92=0,1,0)*(show2<>1) AS p_18ns " +
             "FROM teepast2 " +
             "WHERE courseName = ? AND date >= ? AND date <= ? AND username2 <> '' AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL " +

             "UNION ALL " +

             "SELECT p93*(show3<>1) AS p_9ns, IF(p93=0,1,0)*(show3<>1) AS p_18ns " +
             "FROM teepast2 " +
             "WHERE courseName = ? AND date >= ? AND date <= ? AND username3 <> '' AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL " +

             "UNION ALL " +

             "SELECT p94*(show4<>1) AS p_9ns, IF(p94=0,1,0)*(show4<>1) AS p_18ns " +
             "FROM teepast2 " +
             "WHERE courseName = ? AND date >= ? AND date <= ? AND username4 <> '' AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL " +

             "UNION ALL " +

             "SELECT p95*(show5<>1) AS p_9ns, IF(p95=0,1,0)*(show5<>1) AS p_18ns " +
             "FROM teepast2 " +
             "WHERE courseName = ? AND date >= ? AND date <= ? AND username5 <> '' AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL " +

             ") AS d_table");


         /*

         *
         * Guests AND Members
         *

         SELECT SUM(p_9ns), SUM(p_18ns)
         FROM (

         SELECT p91*(show1<>1) AS p_9ns, IF(p91=0,1,0)*(show1<>1) AS p_18ns
         FROM teepast2
         WHERE courseName = "" AND date >= 20090101 AND date <= 20090427 AND username1 <> ''

         UNION ALL

         SELECT p92*(show2<>1) AS p_9ns, IF(p92=0,1,0)*(show2<>1) AS p_18ns
         FROM teepast2
         WHERE courseName = "" AND date >= 20090101 AND date <= 20090427 AND username2 <> ''

         UNION ALL

         SELECT p93*(show3<>1) AS p_9ns, IF(p93=0,1,0)*(show3<>1) AS p_18ns
         FROM teepast2
         WHERE courseName = "" AND date >= 20090101 AND date <= 20090427 AND username3 <> ''

         UNION ALL

         SELECT p94*(show4<>1) AS p_9ns, IF(p94=0,1,0)*(show4<>1) AS p_18ns
         FROM teepast2
         WHERE courseName = "" AND date >= 20090101 AND date <= 20090427 AND username4 <> ''

         UNION ALL

         SELECT p95*(show5<>1) AS p_9ns, IF(p95=0,1,0)*(show5<>1) AS p_18ns
         FROM teepast2
         WHERE courseName = "" AND date >= 20090101 AND date <= 20090427 AND username5 <> ''
         ) AS d_table


         *
         * Guests Only
         *

         SELECT SUM(p_9ns), SUM(p_18ns)

         FROM (

         SELECT p91*(show1<>1) AS p_9ns, IF(p91=0,1,0)*(show1<>1) AS p_18ns
         FROM teepast2
         WHERE courseName = "" AND date >= 20090101 AND date <= 20090427 AND username1 <> '' AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL

         UNION ALL

         SELECT p92*(show2<>1) AS p_9ns, IF(p92=0,1,0)*(show2<>1) AS p_18ns
         FROM teepast2
         WHERE courseName = "" AND date >= 20090101 AND date <= 20090427 AND username2 <> '' AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL


         UNION ALL

         SELECT p93*(show3<>1) AS p_9ns, IF(p93=0,1,0)*(show3<>1) AS p_18ns
         FROM teepast2
         WHERE courseName = "" AND date >= 20090101 AND date <= 20090427 AND username3 <> '' AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL


         UNION ALL

         SELECT p94*(show4<>1) AS p_9ns, IF(p94=0,1,0)*(show4<>1) AS p_18ns
         FROM teepast2
         WHERE courseName = "" AND date >= 20090101 AND date <= 20090427 AND username4 <> '' AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL


         UNION ALL

         SELECT p95*(show5<>1) AS p_9ns, IF(p95=0,1,0)*(show5<>1) AS p_18ns
         FROM teepast2
         WHERE courseName = "" AND date >= 20090101 AND date <= 20090427 AND username5 <> '' AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL

         ) AS d_table

         */

         //
         //  Get YTD total of member no-shows
         //
         pstmt_noshows.clearParameters();
         pstmt_noshows.setString(1, courseName);
         pstmt_noshows.setLong(2, ytddate);
         pstmt_noshows.setLong(3, ytdend);

         pstmt_noshows.setString(4, courseName);
         pstmt_noshows.setLong(5, ytddate);
         pstmt_noshows.setLong(6, ytdend);

         pstmt_noshows.setString(7, courseName);
         pstmt_noshows.setLong(8, ytddate);
         pstmt_noshows.setLong(9, ytdend);

         pstmt_noshows.setString(10, courseName);
         pstmt_noshows.setLong(11, ytddate);
         pstmt_noshows.setLong(12, ytdend);

         pstmt_noshows.setString(13, courseName);
         pstmt_noshows.setLong(14, ytddate);
         pstmt_noshows.setLong(15, ytdend);

         rs = pstmt_noshows.executeQuery();

         i = 0;

         if (rs.next()) {

             // totals
             mnshowRounds1 = rs.getInt(1) + rs.getInt(2);

         }



         //
         //  Get MTD total of member no-shows
         //
         pstmt_noshows.clearParameters();
         pstmt_noshows.setString(1, courseName);
         pstmt_noshows.setLong(2, mtddate);
         pstmt_noshows.setLong(3, mtdend);

         pstmt_noshows.setString(4, courseName);
         pstmt_noshows.setLong(5, mtddate);
         pstmt_noshows.setLong(6, mtdend);

         pstmt_noshows.setString(7, courseName);
         pstmt_noshows.setLong(8, mtddate);
         pstmt_noshows.setLong(9, mtdend);

         pstmt_noshows.setString(10, courseName);
         pstmt_noshows.setLong(11, mtddate);
         pstmt_noshows.setLong(12, mtdend);

         pstmt_noshows.setString(13, courseName);
         pstmt_noshows.setLong(14, mtddate);
         pstmt_noshows.setLong(15, mtdend);

         rs = pstmt_noshows.executeQuery();

         i = 0;

         if (rs.next()) {

             // totals
             mnshowRounds2 = rs.getInt(1) + rs.getInt(2);

         }



         //
         //  Get LM total of member no-shows
         //
         pstmt_noshows.clearParameters();
         pstmt_noshows.setString(1, courseName);
         pstmt_noshows.setLong(2, lmsdate);
         pstmt_noshows.setLong(3, lmedate);

         pstmt_noshows.setString(4, courseName);
         pstmt_noshows.setLong(5, lmsdate);
         pstmt_noshows.setLong(6, lmedate);

         pstmt_noshows.setString(7, courseName);
         pstmt_noshows.setLong(8, lmsdate);
         pstmt_noshows.setLong(9, lmedate);

         pstmt_noshows.setString(10, courseName);
         pstmt_noshows.setLong(11, lmsdate);
         pstmt_noshows.setLong(12, lmedate);

         pstmt_noshows.setString(13, courseName);
         pstmt_noshows.setLong(14, lmsdate);
         pstmt_noshows.setLong(15, lmedate);

         rs = pstmt_noshows.executeQuery();

         i = 0;

         if (rs.next()) {

             // totals
             mnshowRounds4 = rs.getInt(1) + rs.getInt(2);

         }

         pstmt_noshows.close();




         //
         // Do transportation queries
         //

         PreparedStatement pstmt_tmodes = con.prepareStatement ("" +
           "SELECT IF(tmode='','Unknown', tmode) AS tmode, SUM(p_9), SUM(p_18) " +
               "FROM ( " +

               "SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, p1cw AS tmode " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show1 = 1 " +
                    "AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL " +

               "UNION ALL " +

               "SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, p2cw AS tmode " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show2 = 1  " +
                    "AND player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL " +

               "UNION ALL " +

               "SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, p3cw AS tmode " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show3 = 1  " +
                    "AND player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL " +

               "UNION ALL " +

               "SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, p4cw AS tmode " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show4 = 1  " +
                    "AND player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL " +

               "UNION ALL " +

               "SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, p5cw AS tmode " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show5 = 1  " +
                    "AND player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL " +

           ") AS d_table GROUP BY tmode;");


           /*
           SELECT IF(tmode='','Unknown', tmode) AS tmode, SUM(p_9), SUM(p_18)
               FROM (

               SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, p1cw AS tmode
               FROM teepast2
               WHERE courseName = "" AND date >= 20090101 AND date <= 20090504 AND show1 = 1
                    AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL

               UNION ALL

               SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, p2cw AS tmode
               FROM teepast2
               WHERE courseName = "" AND date >= 20090101 AND date <= 20090504 AND show2 = 1
                    AND player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL

               UNION ALL

               SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, p3cw AS tmode
               FROM teepast2
               WHERE courseName = "" AND date >= 20090101 AND date <= 20090504 AND show3 = 1
                    AND player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL

               UNION ALL

               SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, p4cw AS tmode
               FROM teepast2
               WHERE courseName = "" AND date >= 20090101 AND date <= 20090504 AND show4 = 1
                    AND player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL

               UNION ALL

               SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, p5cw AS tmode
               FROM teepast2
               WHERE courseName = "" AND date >= 20090101 AND date <= 20090504 AND show5 = 1
                    AND player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL

           ) AS d_table
           GROUP BY tmode;
           */


         //
         //  Get YTD counts for tmodes
         //
         pstmt_tmodes.clearParameters();
         pstmt_tmodes.setString(1, courseName);
         pstmt_tmodes.setLong(2, ytddate);
         pstmt_tmodes.setLong(3, ytdend);

         pstmt_tmodes.setString(4, courseName);
         pstmt_tmodes.setLong(5, ytddate);
         pstmt_tmodes.setLong(6, ytdend);

         pstmt_tmodes.setString(7, courseName);
         pstmt_tmodes.setLong(8, ytddate);
         pstmt_tmodes.setLong(9, ytdend);

         pstmt_tmodes.setString(10, courseName);
         pstmt_tmodes.setLong(11, ytddate);
         pstmt_tmodes.setLong(12, ytdend);

         pstmt_tmodes.setString(13, courseName);
         pstmt_tmodes.setLong(14, ytddate);
         pstmt_tmodes.setLong(15, ytdend);

         rs = pstmt_tmodes.executeQuery();

         i = 0;

         while (rs.next()) {

            found = false;
            loop1:
            for (x = 0; x < parm.MAX_Tmodes; x++) {

                if (parmc.tmodea[x].equals(rs.getString(1))) {

                    tmodeR1[x] = rs.getInt(2) + rs.getInt(3);
                    found = true;
                    break loop1;

                } else if (parmc.tmodea[x].equals("")) {

                    // found an empty one so we must be at the end of the defined types
                    break loop1;

                }
            }

            // came across a tmode that is no longer defined in the system
            if (!found && x < parm.MAX_Tmodes) {

                // lets add it to our array for display
                parmc.tmodea[x] = rs.getString(1);
                parmc.tmode[x] = "Legacy Type " + rs.getString(1);
                tmodeR1[x] = rs.getInt(2) + rs.getInt(3);

            }

         }


         //
         //  Get MTD counts for tmodes
         //
         pstmt_tmodes.clearParameters();
         pstmt_tmodes.setString(1, courseName);
         pstmt_tmodes.setLong(2, mtddate);
         pstmt_tmodes.setLong(3, mtdend);

         pstmt_tmodes.setString(4, courseName);
         pstmt_tmodes.setLong(5, mtddate);
         pstmt_tmodes.setLong(6, mtdend);

         pstmt_tmodes.setString(7, courseName);
         pstmt_tmodes.setLong(8, mtddate);
         pstmt_tmodes.setLong(9, mtdend);

         pstmt_tmodes.setString(10, courseName);
         pstmt_tmodes.setLong(11, mtddate);
         pstmt_tmodes.setLong(12, mtdend);

         pstmt_tmodes.setString(13, courseName);
         pstmt_tmodes.setLong(14, mtddate);
         pstmt_tmodes.setLong(15, mtdend);

         rs = pstmt_tmodes.executeQuery();

         i = 0;

         while (rs.next()) {

            found = false;
            loop1:
            for (x = 0; x < parm.MAX_Tmodes; x++) {

                if (parmc.tmodea[x].equals(rs.getString(1))) {

                    tmodeR2[x] = rs.getInt(2) + rs.getInt(3);
                    found = true;
                    break loop1;

                } else if (parmc.tmodea[x].equals("")) {

                    // found an empty one so we must be at the end of the defined types
                    break loop1;

                }
            }

            // came across a tmode that is no longer defined in the system
            if (!found && x < parm.MAX_Tmodes) {

                // lets add it to our array for display
                parmc.tmodea[x] = rs.getString(1);
                parmc.tmode[x] = "Legacy Type " + rs.getString(1);
                tmodeR2[x] = rs.getInt(2) + rs.getInt(3);

            }

         }



         //
         //  Get LM counts for tmodes
         //
         pstmt_tmodes.clearParameters();
         pstmt_tmodes.setString(1, courseName);
         pstmt_tmodes.setLong(2, lmsdate);
         pstmt_tmodes.setLong(3, lmedate);

         pstmt_tmodes.setString(4, courseName);
         pstmt_tmodes.setLong(5, lmsdate);
         pstmt_tmodes.setLong(6, lmedate);

         pstmt_tmodes.setString(7, courseName);
         pstmt_tmodes.setLong(8, lmsdate);
         pstmt_tmodes.setLong(9, lmedate);

         pstmt_tmodes.setString(10, courseName);
         pstmt_tmodes.setLong(11, lmsdate);
         pstmt_tmodes.setLong(12, lmedate);

         pstmt_tmodes.setString(13, courseName);
         pstmt_tmodes.setLong(14, lmsdate);
         pstmt_tmodes.setLong(15, lmedate);

         rs = pstmt_tmodes.executeQuery();

         i = 0;

         while (rs.next()) {

            found = false;
            loop1:
            for (x = 0; x < parm.MAX_Tmodes; x++) {

                if (parmc.tmodea[x].equals(rs.getString(1))) {

                    tmodeR4[x] = rs.getInt(2) + rs.getInt(3);
                    found = true;
                    break loop1;

                } else if (parmc.tmodea[x].equals("")) {

                    // found an empty one so we must be at the end of the defined types
                    break loop1;

                }
            }

            // came across a tmode that is no longer defined in the system
            if (!found && x < parm.MAX_Tmodes) {

                // lets add it to our array for display
                parmc.tmodea[x] = rs.getString(1);
                parmc.tmode[x] = "Legacy Type " + rs.getString(1);
                tmodeR4[x] = rs.getInt(2) + rs.getInt(3);

            }

         }

         pstmt_tmodes.close();

      }
      catch (Exception exc) {

         out.println("<BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Exception:" + exc.getMessage());
         out.println("<BR><BR>Error:" + error);
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }




      //
      //  Output the results !!!!!!!!!!!!!
      //

      if (toExcel) {     // if user requested Excel Spreadsheet Format
         out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\" cols=\"4\">");
      } else {
         out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" cols=\"4\">");
      }
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");


      String bgrndcolor = "#336633";      // default
      String fontcolor = "#FFFFFF";      // default

      if (toExcel) {     // if user requested Excel Spreadsheet Format

         bgrndcolor = "#FFFFFF";      // white for excel
         fontcolor = "#000000";      // black for excel
      }
      //
      // add course name header if multi
      //
      if (!courseName.equals( "" )) {

         out.println("<tr bgcolor=\"" +bgrndcolor+ "\"><td colspan=\"4\">");
         out.println("<font size=\"3\" color=\"" +fontcolor+ "\">");
         out.println("<p align=\"center\"><b>" + courseName + "</b></p>");
         out.println("</font></td></tr>");
      }

      //
      //  Header row
      //
      out.println("<tr bgcolor=\"" +bgrndcolor+ "\">");
         out.println("<td>");
            out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
            out.println("<p align=\"left\"><b>Stat</b></p>");
            out.println("</font></td>");

         out.println("<td>");
            out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
            out.println("<p align=\"center\"><b>Last Month</b><br>(" + month + "/" + year + ")</p>");
            out.println("</font></td>");

         out.println("<td>");
            out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
            out.println("<p align=\"center\"><b>Month To Date</b><br>(excludes today)</p>");
            out.println("</font></td>");

         out.println("<td>");
            out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
            out.println("<p align=\"center\"><b>Year To Date</b><br>(excludes today)</p>");
            out.println("</font></td>");

         //
         //  Build the HTML for each stat gathered above
         //
         out.println("</tr><tr>");                       // Grand totals
         out.println("<td align=\"left\">");
            out.println("<font size=\"2\"><br>");
            out.println("<b>Total Rounds Played:</b>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br><b>");
            out.println(totRounds4);
            out.println("</b></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br><b>");
            out.println(totRounds2);
            out.println("</b></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br><b>");
            out.println(totRounds1);
            out.println("</b></font></td>");


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("</tr><tr>");                     // Total Rounds for Members
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<p align=\"left\"><b>Rounds by Members:</b></p>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
         if (memRounds4 < 1 || totRounds4 < 1) {
            out.println(memRounds4);
         } else {
            out.println(memRounds4 + " (" + (memRounds4 * 100)/totRounds4 + "%)");
         }
            out.println("</b></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
         if (memRounds2 < 1 || totRounds2 < 1) {
            out.println(memRounds2);
         } else {
            out.println(memRounds2 + " (" + (memRounds2 * 100)/totRounds2 + "%)");
         }
            out.println("</b></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
         if (memRounds1 < 1 || totRounds1 < 1) {
            out.println(memRounds1);
         } else {
            out.println(memRounds1 + " (" + (memRounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</b></font></td>");

         found = false;                              // init flag

         for (i=0; i<parm.MAX_Mems; i++) {           // do all member types

            if (!parm.mem[i].equals( "" )) {

               out.println("</tr><tr>");                     // Rounds for Member Type
               out.println("<td align=\"right\">");
                  out.println("<font size=\"2\">");
                  if (found == false) {
                     out.println("<u>by Member Type</u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"); // &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                     //out.println("<br><br>");
                     out.println("</td></tr><tr><td align=\"right\"><font size=\"2\">");
                  }
                  out.println(parm.mem[i] + ":");
                  //out.println("<br>");
                  out.println("</font></td>");

               found = true;             // indicate a member type has been found (only display heading once)

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (memxRounds4[i] < 1 || memRounds4 < 1) {
                  out.println(memxRounds4[i]);
               } else {
                  out.println(memxRounds4[i] + " (" + (memxRounds4[i] * 100)/memRounds4 + "%)");
               }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (memxRounds2[i] < 1 || memRounds2 < 1) {
                  out.println(memxRounds2[i]);
               } else {
                  out.println(memxRounds2[i] + " (" + (memxRounds2[i] * 100)/memRounds2 + "%)");
               }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (memxRounds1[i] < 1 || memRounds1 < 1) {
                  out.println(memxRounds1[i]);
               } else {
                  out.println(memxRounds1[i] + " (" + (memxRounds1[i] * 100)/memRounds1 + "%)");
               }
                  out.println("</font></td>");
            }
         }


         //
         //  check for rounds with no member type (member has been deleted from db since round was played)
         //
         if (memUnknown1 != 0 || memUnknown2 != 0 || memUnknown4 != 0) {

            out.println("</tr><tr>");                     // Rounds for Unknown Member Type
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"right\">Unknown (member no longer in database):</p>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (memUnknown4 < 1 || memRounds4 < 1) {
               out.println(memUnknown4);
            } else {
               out.println(memUnknown4 + " (" + (memUnknown4 * 100)/memRounds4 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (memUnknown2 < 1 || memRounds2 < 1) {
               out.println(memUnknown2);
            } else {
               out.println(memUnknown2 + " (" + (memUnknown2 * 100)/memRounds2 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (memUnknown1 < 1 || memRounds1 < 1) {
               out.println(memUnknown1);
            } else {
               out.println(memUnknown1 + " (" + (memUnknown1 * 100)/memRounds1 + "%)");
            }
               out.println("</font></td>");
         }


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         found = false;                                // init flag

         for (i=0; i<parm.MAX_Mships; i++) {           // do all membership types

            if (!parm.mship[i].equals( "" )) {

               out.println("</tr><tr>");                     // Rounds for Membership Type 1
               out.println("<td align=\"right\">");
                  out.println("<font size=\"2\">");
                  if (found == false) {
                     out.println("<u>by Membership Type</u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"); // &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                     //out.println("<br>");
                     out.println("</td></tr><tr><td align=\"right\"><font size=\"2\">");
                  }
                  out.println(parm.mship[i] + ":");
                  out.println("<br>");
                  out.println("</font></td>");

               found = true;         // indicate mship found - no heading needed

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (mshipxRounds4[i] < 1 || memRounds4 < 1) {
                  out.println(mshipxRounds4[i]);
               } else {
                  out.println(mshipxRounds4[i] + " (" + (mshipxRounds4[i] * 100)/memRounds4 + "%)");
               }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (mshipxRounds2[i] < 1 || memRounds2 < 1) {
                  out.println(mshipxRounds2[i]);
               } else {
                  out.println(mshipxRounds2[i] + " (" + (mshipxRounds2[i] * 100)/memRounds2 + "%)");
               }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (mshipxRounds1[i] < 1 || memRounds1 < 1) {
                  out.println(mshipxRounds1[i]);
               } else {
                  out.println(mshipxRounds1[i] + " (" + (mshipxRounds1[i] * 100)/memRounds1 + "%)");
               }
                  out.println("</font></td>");
            }
         }


         //
         //  check for rounds with no member type (member has been deleted from db since round was played)
         //
         if (mshipUnknown1 != 0 || mshipUnknown2 != 0 || mshipUnknown4 != 0) {

            out.println("</tr><tr>");                     // Rounds for Unknown Membership Type
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"right\">Unknown (member no longer in database):</p>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (mshipUnknown4 < 1 || memRounds4 < 1) {
               out.println(mshipUnknown4);
            } else {
               out.println(mshipUnknown4 + " (" + (mshipUnknown4 * 100)/memRounds4 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (mshipUnknown2 < 1 || memRounds2 < 1) {
               out.println(mshipUnknown2);
            } else {
               out.println(mshipUnknown2 + " (" + (mshipUnknown2 * 100)/memRounds2 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (mshipUnknown1 < 1 || memRounds1 < 1) {
               out.println(mshipUnknown1);
            } else {
               out.println(mshipUnknown1 + " (" + (mshipUnknown1 * 100)/memRounds1 + "%)");
            }
               out.println("</font></td>");
         }


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");


         out.println("</tr><tr>");                     // 9 Hole Rounds for Members
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("Member 9 Hole Rounds:");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (mem9Rounds4 < 1 || totRounds4 < 1) {
            out.println(mem9Rounds4);
         } else {
            out.println(mem9Rounds4 + " (" + (mem9Rounds4 * 100)/totRounds4 + "%)");
         }
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (mem9Rounds2 < 1 || totRounds2 < 1) {
            out.println(mem9Rounds2);
         } else {
            out.println(mem9Rounds2 + " (" + (mem9Rounds2 * 100)/totRounds2 + "%)");
         }
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (mem9Rounds1 < 1 || totRounds1 < 1) {
            out.println(mem9Rounds1);
         } else {
            out.println(mem9Rounds1 + " (" + (mem9Rounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</font></td>");

         out.println("</tr><tr>");                     // 18 Hole Rounds for Members
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("Member 18 Hole Rounds:");
            out.println("</font><br></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (mem18Rounds4 < 1 || totRounds4 < 1) {
            out.println(mem18Rounds4);
         } else {
            out.println(mem18Rounds4 + " (" + (mem18Rounds4 * 100)/totRounds4 + "%)");
         }
            out.println("</font><br></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (mem18Rounds2 < 1 || totRounds2 < 1) {
            out.println(mem18Rounds2);
         } else {
            out.println(mem18Rounds2 + " (" + (mem18Rounds2 * 100)/totRounds2 + "%)");
         }
         out.println("</font><br></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (mem18Rounds1 < 1 || totRounds1 < 1) {
            out.println(mem18Rounds1);
         } else {
            out.println(mem18Rounds1 + " (" + (mem18Rounds1 * 100)/totRounds1 + "%)");
         }
         out.println("</font><br></td>");


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");


         out.println("</tr><tr>");                      // Total Rounds by Guests
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<p align=\"left\"><b>Rounds by Guests:</b></p>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
         if (gstRounds4 < 1 || totRounds4 < 1) {
            out.println(gstRounds4);
         } else {
            out.println(gstRounds4 + " (" + (gstRounds4 * 100)/totRounds4 + "%)");
         }
            out.println("</b></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
         if (gstRounds2 < 1 || totRounds2 < 1) {
            out.println(gstRounds2);
         } else {
            out.println(gstRounds2 + " (" + (gstRounds2 * 100)/totRounds2 + "%)");
         }
            out.println("</b></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
         if (gstRounds1 < 1 || totRounds1 < 1) {
            out.println(gstRounds1);
         } else {
            out.println(gstRounds1 + " (" + (gstRounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</b></font></td>");

         for (i = 0; i < parm.MAX_Guests; i++) {          // chack all 36 guest types

            if (!parm.guest[i].equals( "" ) && !parm.guest[i].equals( "$@#!^&*" )) {

               out.println("</tr><tr>");                     // Rounds for Guest Type 1
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\">" + parm.guest[i] + ":</p>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (gstRnds4[i] < 1 || gstRounds4 < 1) {
                  out.println(gstRnds4[i]);
               } else {
                  out.println(gstRnds4[i] + " (" + (gstRnds4[i] * 100)/gstRounds4 + "%)");
               }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (gstRnds2[i] < 1 || gstRounds2 < 1) {
                  out.println(gstRnds2[i]);
               } else {
                  out.println(gstRnds2[i] + " (" + (gstRnds2[i] * 100)/gstRounds2 + "%)");
               }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (gstRnds1[i] < 1 || gstRounds1 < 1) {
                  out.println(gstRnds1[i]);
               } else {
                  out.println(gstRnds1[i] + " (" + (gstRnds1[i] * 100)/gstRounds1 + "%)");
               }
                  out.println("</font></td>");
            }
         }

         out.println("</tr><tr>");                     // 9 Hole Rounds for Guests
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\"><br>");
            out.println("Guest 9 Hole Rounds:");
            out.println("<br>(Revenue)");
            out.println("<br>(Non-Revenue)");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br>");
            if (gst9Rounds4 < 1 || totRounds4 < 1) {
               out.println(gst9Rounds4);
            } else {
               out.println(gst9Rounds4 + " (" + (gst9Rounds4 * 100)/totRounds4 + "%)");
            }
            out.println("<br>");
            if (gst9RevRnds4 < 1 || gst9Rounds4 < 1) {
               out.println("&nbsp;");
            } else {
               out.println(gst9RevRnds4 + " (" + (gst9RevRnds4 * 100)/gst9Rounds4 + "%)");   // % of 9 hole gst rounds
            }
            int gst9NonRevRnds4 = gst9Rounds4 - gst9RevRnds4;               // get # of non-rev gst rounds and display
            out.println("<br>");
            if (gst9NonRevRnds4 < 1 || gst9Rounds4 < 1) {
               out.println("&nbsp;");
            } else {
               out.println(gst9NonRevRnds4 + " (" + (gst9NonRevRnds4 * 100)/gst9Rounds4 + "%)");   // % of 9 hole gst rounds
            }
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br>");
            if (gst9Rounds2 < 1 || totRounds2 < 1) {
               out.println(gst9Rounds2);
            } else {
               out.println(gst9Rounds2 + " (" + (gst9Rounds2 * 100)/totRounds2 + "%)");
            }
            out.println("<br>");
            if (gst9RevRnds2 < 1 || gst9Rounds2 < 1) {
               out.println("&nbsp;");
            } else {
               out.println(gst9RevRnds2 + " (" + (gst9RevRnds2 * 100)/gst9Rounds2 + "%)");   // % of 9 hole rev gst rounds
            }
            int gst9NonRevRnds2 = gst9Rounds2 - gst9RevRnds2;               // get # of non-rev gst rounds and display
            out.println("<br>");
            if (gst9NonRevRnds2 < 1 || gst9Rounds2 < 1) {
               out.println("&nbsp;");
            } else {
               out.println(gst9NonRevRnds2 + " (" + (gst9NonRevRnds2 * 100)/gst9Rounds2 + "%)");   // % of 9 hole non-rev gst rounds
            }
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br>");
            if (gst9Rounds1 < 1 || totRounds1 < 1) {
               out.println(gst9Rounds1);
            } else {
               out.println(gst9Rounds1 + " (" + (gst9Rounds1 * 100)/totRounds1 + "%)");
            }
            out.println("<br>");
            if (gst9RevRnds1 < 1 || gst9Rounds1 < 1) {
               out.println("&nbsp;");
            } else {
               out.println(gst9RevRnds1 + " (" + (gst9RevRnds1 * 100)/gst9Rounds1 + "%)");   // % of 9 hole gst rounds
            }
            int gst9NonRevRnds1 = gst9Rounds1 - gst9RevRnds1;               // get # of non-rev gst rounds and display
            out.println("<br>");
            if (gst9NonRevRnds1 < 1 || gst9Rounds1 < 1) {
               out.println("&nbsp;");
            } else {
               out.println(gst9NonRevRnds1 + " (" + (gst9NonRevRnds1 * 100)/gst9Rounds1 + "%)");   // % of 9 hole gst rounds
            }
            out.println("</font></td>");

         out.println("</tr><tr>");                     // 18 Hole Rounds for Guests
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("Guest 18 Hole Rounds:");
            out.println("<br>(Revenue)");
            out.println("<br>(Non-Revenue)");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (gst18Rounds4 < 1 || totRounds4 < 1) {
               out.println(gst18Rounds4);
            } else {
               out.println(gst18Rounds4 + " (" + (gst18Rounds4 * 100)/totRounds4 + "%)");
            }
            out.println("<br>");
            if (gst18RevRnds4 < 1 || gst18Rounds4 < 1) {
               out.println("&nbsp;");
            } else {
               out.println(gst18RevRnds4 + " (" + (gst18RevRnds4 * 100)/gst18Rounds4 + "%)");   // % of 18 hole gst rounds
            }
            int gst18NonRevRnds4 = gst18Rounds4 - gst18RevRnds4;               // get # of non-rev gst rounds and display
            out.println("<br>");
            if (gst18NonRevRnds4 < 1 || gst18Rounds4 < 1) {
               out.println("&nbsp;");
            } else {
               out.println(gst18NonRevRnds4 + " (" + (gst18NonRevRnds4 * 100)/gst18Rounds4 + "%)");   // % of 18 hole gst rounds
            }
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (gst18Rounds2 < 1 || totRounds2 < 1) {
               out.println(gst18Rounds2);
            } else {
               out.println(gst18Rounds2 + " (" + (gst18Rounds2 * 100)/totRounds2 + "%)");
            }
            out.println("<br>");
            if (gst18RevRnds2 < 1 || gst18Rounds2 < 1) {
               out.println("&nbsp;");
            } else {
               out.println(gst18RevRnds2 + " (" + (gst18RevRnds2 * 100)/gst18Rounds2 + "%)");   // % of 18 hole gst rounds
            }
            int gst18NonRevRnds2 = gst18Rounds2 - gst18RevRnds2;               // get # of non-rev gst rounds and display
            out.println("<br>");
            if (gst18NonRevRnds2 < 1 || gst18Rounds2 < 1) {
               out.println("&nbsp;");
            } else {
               out.println(gst18NonRevRnds2 + " (" + (gst18NonRevRnds2 * 100)/gst18Rounds2 + "%)");   // % of 18 hole gst rounds
            }
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (gst18Rounds1 < 1 || totRounds1 < 1) {
               out.println(gst18Rounds1);
            } else {
               out.println(gst18Rounds1 + " (" + (gst18Rounds1 * 100)/totRounds1 + "%)");
            }
            out.println("<br>");
            if (gst18RevRnds1 < 1 || gst18Rounds1 < 1) {
               out.println("&nbsp;");
            } else {
               out.println(gst18RevRnds1 + " (" + (gst18RevRnds1 * 100)/gst18Rounds1 + "%)");   // % of 18 hole gst rounds
            }
            int gst18NonRevRnds1 = gst18Rounds1 - gst18RevRnds1;               // get # of non-rev gst rounds and display
            out.println("<br>");
            if (gst18NonRevRnds1 < 1 || gst18Rounds1 < 1) {
               out.println("&nbsp;");
            } else {
               out.println(gst18NonRevRnds1 + " (" + (gst18NonRevRnds1 * 100)/gst18Rounds1 + "%)");   // % of 18 hole gst rounds
            }
            out.println("</font></td>");


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");


         //
         //  Only display 'Others' if there were some found (non-members, non-guests)
         //
         if (otherRounds1 > 0 || otherRounds2 > 0 || otherRounds4 > 0) {

            out.println("</tr><tr>");                      // Total Rounds by Others
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"left\"><b>Rounds by Others:</b></p>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\"><b>");
            if (otherRounds4 < 1 || totRounds4 < 1) {
               out.println(otherRounds4);
            } else {
               out.println(otherRounds4 + " (" + (otherRounds4 * 100)/totRounds4 + "%)");
            }
               out.println("</b></font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\"><b>");
            if (otherRounds2 < 1 || totRounds2 < 1) {
               out.println(otherRounds2);
            } else {
               out.println(otherRounds2 + " (" + (otherRounds2 * 100)/totRounds2 + "%)");
            }
               out.println("</b></font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\"><b>");
            if (otherRounds1 < 1 || totRounds1 < 1) {
               out.println(otherRounds1);
            } else {
               out.println(otherRounds1 + " (" + (otherRounds1 * 100)/totRounds1 + "%)");
            }
               out.println("</b></font></td>");

            out.println("</tr><tr>");                     // 9 Hole Rounds for Others
            out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("Other 9 Hole Rounds:");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (other9Rounds4 < 1 || totRounds4 < 1) {
               out.println(other9Rounds4);
            } else {
               out.println(other9Rounds4 + " (" + (other9Rounds4 * 100)/totRounds4 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (other9Rounds2 < 1 || totRounds2 < 1) {
               out.println(other9Rounds2);
            } else {
               out.println(other9Rounds2 + " (" + (other9Rounds2 * 100)/totRounds2 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (other9Rounds1 < 1 || totRounds1 < 1) {
               out.println(other9Rounds1);
            } else {
               out.println(other9Rounds1 + " (" + (other9Rounds1 * 100)/totRounds1 + "%)");
            }
               out.println("</font></td>");

            out.println("</tr><tr>");                     // 18 Hole Rounds for Others
            out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("Other 18 Hole Rounds:");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (other18Rounds4 < 1 || totRounds4 < 1) {
               out.println(other18Rounds4);
            } else {
               out.println(other18Rounds4 + " (" + (other18Rounds4 * 100)/totRounds4 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (other18Rounds2 < 1 || totRounds2 < 1) {
               out.println(other18Rounds2);
            } else {
               out.println(other18Rounds2 + " (" + (other18Rounds2 * 100)/totRounds2 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (other18Rounds1 < 1 || totRounds1 < 1) {
               out.println(other18Rounds1);
            } else {
               out.println(other18Rounds1 + " (" + (other18Rounds1 * 100)/totRounds1 + "%)");
            }
               out.println("</font></td>");


            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">&nbsp;");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">&nbsp;");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">&nbsp;");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">&nbsp;");
               out.println("</font></td>");
         }
         out.println("</tr>");

         //
         //  Check all the Transportation Modes - now 16 configurable modes (V4)
         //
         for (i=0; i<parm.MAX_Tmodes; i++) {

            if (tmodeR1[i] > 0 || tmodeR2[i] > 0 || tmodeR4[i] > 0) {

               out.println("<tr>");
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"left\"><b>" +parmc.tmode[i]+ " Rounds:</b></p>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (tmodeR4[i] < 1 || totRounds4 < 1) {
                  out.println(tmodeR4[i]);
               } else {
                  out.println(tmodeR4[i] + " (" + (tmodeR4[i] * 100)/totRounds4 + "%)");
               }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (tmodeR2[i] < 1 || totRounds2 < 1) {
                  out.println(tmodeR2[i]);
               } else {
                  out.println(tmodeR2[i] + " (" + (tmodeR2[i] * 100)/totRounds2 + "%)");
               }
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (tmodeR1[i] < 1 || totRounds1 < 1) {
                  out.println(tmodeR1[i]);
               } else {
                  out.println(tmodeR1[i] + " (" + (tmodeR1[i] * 100)/totRounds1 + "%)");
               }
                  out.println("</font></td>");
               out.println("</tr>");
            }
         }

         if (tmodeOldR91 > 0 || tmodeOldR92 > 0 || tmodeOldR94 > 0 ||
             tmodeOldR181 > 0 || tmodeOldR182 > 0 || tmodeOldR184 > 0) {

            out.println("<tr>");
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"left\"><b>Rounds From Modes No Longer Used:</b></p>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if ((tmodeOldR94 + tmodeOldR184) < 1 || totRounds4 < 1) {
               out.println(tmodeOldR94 + tmodeOldR184);
            } else {
               out.println((tmodeOldR94 + tmodeOldR184) + " (" + ((tmodeOldR94 + tmodeOldR184) * 100)/totRounds4 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if ((tmodeOldR92 + tmodeOldR182) < 1 || totRounds2 < 1) {
               out.println(tmodeOldR92 + tmodeOldR182);
            } else {
               out.println((tmodeOldR92 + tmodeOldR182) + " (" + ((tmodeOldR92 + tmodeOldR182) * 100)/totRounds2 + "%)");
            }
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if ((tmodeOldR91 + tmodeOldR181) < 1 || totRounds1 < 1) {
               out.println(tmodeOldR91 + tmodeOldR181);
            } else {
               out.println((tmodeOldR91 + tmodeOldR181) + " (" + ((tmodeOldR91 + tmodeOldR181) * 100)/totRounds1 + "%)");
            }
               out.println("</font></td>");
            out.println("</tr>");
         }

         out.println("<tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("</tr><tr>");
         out.println("<td align=\"left\">");
            out.println("<font size=\"2\">");
            out.println("<b>Member No-Shows:</b>");
            out.println("<br></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(mnshowRounds4);
            out.println("<br></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(mnshowRounds2);
            out.println("<br></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(mnshowRounds1);
            out.println("<br></font></td>");

      out.println("</font></tr></table><br>");

   }       // end of while Courses - do all courses

   if (!toExcel) {     // if normal request

      out.println("</td></tr></table>");                // end of main page table & column

      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
   }

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");
   out.close();

 }  // end of goRounds


 // *********************************************************
 //  Report Type = Number of Rounds Played Today (today=yes)
 // *********************************************************

 private void goToday(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // golf only report

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   //  get the session and the club name
   //
   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

   String club = (String)sess.getAttribute("club");      // get club name
   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //   Get multi option, member types, and guest types
   //
   try {

      getClub.getParms(con, parm);        // get the club parms
   }
   catch (Exception ignore) {
   }

   boolean guest = false;

   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;

   int memRounds3 = 0;         // Today's counts
   int [] memxRounds3 = new int [parm.MAX_Mems];       // use arrays for the mem types

   int mem9Rounds3 = 0;
   int mem18Rounds3 = 0;
   int otherRounds3 = 0;
   int other9Rounds3 = 0;
   int other18Rounds3 = 0;

   int gstRounds3 = 0;

   int [] gstRnds3 = new int [parm.MAX_Guests];       // use array for the 36 guest types

   int gst9Rounds3 = 0;
   int gst18Rounds3 = 0;
   int gst9RevRnds3 = 0;
   int gst18RevRnds3 = 0;

   int [] mshipxRounds3 = new int [parm.MAX_Mships];       // use arrays for the mship types

   int totRounds3 = 0;
   int nshowRounds3 = 0;
   int memUnknown3 = 0;
   int mshipUnknown3 = 0;

   int [] tmodeR3 = new int [parm.MAX_Tmodes];       // use arrays for the 16 modes of trans
   int [] tmode9R3 = new int [parm.MAX_Tmodes];
   int [] tmode18R3 = new int [parm.MAX_Tmodes];

   long edate = 0;                             // today's date
   int year = 0;
   int month = 0;
   int day = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;

   int multi = 0;                 // multiple course support
   int index = 0;
   int i = 0;
   int count = 0;                 // number of courses

   //
   //  Array to hold the course names
   //
   ArrayList<String> course = new ArrayList<String>();      // unlimited courses

   String courseName = "";        // course names

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

   String error = "None";

   boolean found = false;
   boolean toExcel = false;
   boolean fullDay = false;    // Determines whether to get info for entire day instead of cut off by the current time

   //
   //  Get today's date and current time and calculate date & time values
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   int cal_hour = cal.get(Calendar.HOUR_OF_DAY);       // 24 hr clock (0 - 23)
   int cal_min = cal.get(Calendar.MINUTE);

   int curr_time = (cal_hour * 100) + cal_min;    // get time in hhmm format

   curr_time = SystemUtils.adjustTime(con, curr_time);   // adjust the time

   if (curr_time < 0) {          // if negative, then we went back or ahead one day

      curr_time = 0 - curr_time;        // convert back to positive value

      if (curr_time < 1200) {           // if AM, then we rolled ahead 1 day

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

   month = month + 1;                           // month starts at zero

   edate = year * 10000;                        // create a edate field of yyyymmdd (for today)
   edate = edate + (month * 100);
   edate = edate + day;                         // date = yyyymmdd (for comparisons)

   multi = parm.multi;

   //
   //   Remove any guest types that are null - for tests below
   //
   //
   //   Remove any guest types that are null - for tests below
   //
   for (i = 0; i < parm.MAX_Guests; i++) {

      if (parm.guest[i].equals( "" )) {

         parm.guest[i] = "$@#!^&*";      // make so it won't match player name
      }
   }

   //
   // Check for multiple courses
   //
   count = 1;                  // init to 1 course

   if (multi != 0) {           // if multiple courses supported for this club

      try {

         //
         //  Get the names of all courses for this club
         //
         course = Utilities.getCourseNames(con);     // get all the course names

         count = course.size();                      // number of courses

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Proshop Reports Page - Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error:" + exc.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }

   //
   //  Build the HTML page to display search results
   //
   try{
      if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format

         toExcel = true;
         resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
      }
   }
   catch (Exception exc) {
   }

   if (!toExcel) {  // Don't print header if user requested Excel Spreadsheet Format
       out.println(SystemUtils.HeadTitle("Proshop Reports Page"));
   }
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   if (!toExcel) {  // Don't print submenu code if user requested Excel Spreadsheet Format
       SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   }
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   if (!toExcel) {     // if normal request
       out.println("<table border=\"0\" align=\"center\">");
       out.println("<tr><td align=\"center\">");

       out.println("<font size=\"3\">");
       out.println("<p><b>Course Statistics for Today</b><br></font><font size=\"2\">");
       out.println("<b>Note:</b> Percentages are rounded down to whole number.<br>");
       out.println("Percentages for Member Types and Membership Types are of 'Rounds by Members'.<br>");
       out.println("Percentages for Guest Types are of 'Rounds by Guests'.  Others are of 'Total Rounds'.</p>");

       out.println("<form method=\"post\" action=\"Proshop_reports\" target=\"_blank\">");
       out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
       out.println("<input type=\"hidden\" name=\"today\" value=\"yes\">");
       out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</form>");
       out.println("</font>");
   }

   courseName = "";            // init as not multi
   
   // Get report results for the entire day, instead of only up to the current time.
   if (club.equals("mpccpb") || club.equals("esterocc")) {
       fullDay = true;
   }

   //
   // execute searches and display for each course
   //
   for (index=0; index < count; index++) {       // count = # of courses (1 if not multi)

      if (multi != 0) {                        // if multiple courses supported for this club

         courseName = course.get(index);      // get course name
      }

      //
      //  init count fields for each course
      //
      for (i = 0; i < parm.MAX_Mems; i++) {
         memxRounds3[i] = 0;
      }

      memRounds3 = 0;         // Today's counts
      mem9Rounds3 = 0;
      mem18Rounds3 = 0;
      otherRounds3 = 0;
      other9Rounds3 = 0;
      other18Rounds3 = 0;

      for (i = 0; i < parm.MAX_Guests; i++) {
         gstRnds3[i] = 0;
      }

      gstRounds3 = 0;
      gst9Rounds3 = 0;
      gst18Rounds3 = 0;
      gst9RevRnds3 = 0;
      gst18RevRnds3 = 0;

      for (i = 0; i < parm.MAX_Mships; i++) {
         mshipxRounds3[i] = 0;
      }

      totRounds3 = 0;
      nshowRounds3 = 0;
      memUnknown3 = 0;
      mshipUnknown3 = 0;

      //
      //  Init the Modes of Trans arrays
      //
      for (i = 0; i < parm.MAX_Tmodes; i++) {
         tmodeR3[i] = 0;
         tmode9R3[i] = 0;
         tmode18R3[i] = 0;
      }

      //
      // use the dates provided to search the tee times tables
      //
      try {

         //
         //  Get the System Parameters for this Course
         //
         getParms.getCourse(con, parmc, courseName);

         //
         //  Statement for Today's counts (from teecurr)
         //
         PreparedStatement pstmt2 = con.prepareStatement (
            "SELECT player1, player2, player3, player4, username1, username2, username3, username4, " +
            "p1cw, p2cw, p3cw, p4cw, show1, show2, show3, show4, " +
            "player5, username5, p5cw, show5, p91, p92, p93, p94, p95 " +
            "FROM teecurr2 WHERE date = ?" + (!fullDay ? " AND time <= ?" : "") + " AND courseName = ?");

         error = "Get Today's counts";

         //
         //  Get Today's counts - use teecurr for today
         //
         pstmt2.clearParameters();        // clear the parms
         pstmt2.setLong(1, edate);
         if (fullDay) {
             pstmt2.setString(2, courseName);
         } else {
             pstmt2.setInt(2, curr_time);
             pstmt2.setString(3, courseName);
         }
         rs = pstmt2.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            player1 = rs.getString(1);
            player2 = rs.getString(2);
            player3 = rs.getString(3);
            player4 = rs.getString(4);
            username1 = rs.getString(5);
            username2 = rs.getString(6);
            username3 = rs.getString(7);
            username4 = rs.getString(8);
            p1cw = rs.getString(9);
            p2cw = rs.getString(10);
            p3cw = rs.getString(11);
            p4cw = rs.getString(12);
            show1 = rs.getInt(13);
            show2 = rs.getInt(14);
            show3 = rs.getInt(15);
            show4 = rs.getInt(16);
            player5 = rs.getString(17);
            username5 = rs.getString(18);
            p5cw = rs.getString(19);
            show5 = rs.getInt(20);
            p91 = rs.getInt(21);
            p92 = rs.getInt(22);
            p93 = rs.getInt(23);
            p94 = rs.getInt(24);
            p95 = rs.getInt(25);

            if ((!player1.equals( "" )) && (!player1.equalsIgnoreCase( "x"))) {

               guest = false;
               i = 0;

               ploop1:
               while (i < parm.MAX_Guests) {

                  if (player1.startsWith( parm.guest[i] )) {

                     guest = true;
                     break ploop1;
                  }
                  i++;
               }
               if (guest == true) {

                  if (show1 == 1) {           // if guest and not a no-show

                     gstRnds3[i]++;           // update counts for matching guest type
                     gstRounds3++;
                     totRounds3++;

                    if (p91 == 1) {                          // 9 holes

                        gst9Rounds3++;

                        if (parm.gRev[i] == 1) {          // if Revenue guest

                           gst9RevRnds3++;
                        }

                     } else {

                        gst18Rounds3++;                       // 18 holes

                        if (parm.gRev[i] == 1) {          // if Revenue guest

                           gst18RevRnds3++;
                        }
                     }

                  }
               } else {

                  if (show1 == 1) {           // if member and not a no-show

                     totRounds3++;

                     if (username1.equals( "" )) {

                        otherRounds3++;                   // not guest, not member

                        if (p91 == 1) {                          // 9 holes

                           other9Rounds3++;

                        } else {

                           other18Rounds3++;                       // 18 holes
                        }

                     } else {                            // member

                        memRounds3++;

                        if (p91 == 1) {                          // 9 holes

                           mem9Rounds3++;

                        } else {

                           mem18Rounds3++;                       // 18 holes
                        }
                     }

                  } else {                     // no-show

                     nshowRounds3++;           // bump no-shows
                  }
               }

               //
               // check all modes of trans
               //
               i = 0;
               loop1a:
               while (i < parm.MAX_Tmodes) {
                  if ((p1cw.equals( parmc.tmodea[i] )) && (show1 == 1)) {   // if matches mode of trans

                     tmodeR3[i]++;
                     break loop1a;
                  }
                  i++;
               }
            }
            if ((!player2.equals( "" )) && (!player2.equalsIgnoreCase( "x"))) {

               guest = false;
               i = 0;

               ploop2:
               while (i < parm.MAX_Guests) {

                  if (player2.startsWith( parm.guest[i] )) {

                     guest = true;
                     break ploop2;
                  }
                  i++;
               }
               if (guest == true) {

                  if (show2 == 1) {           // if guest and not a no-show

                     gstRnds3[i]++;           // update counts for matching guest type
                     gstRounds3++;
                     totRounds3++;

                     if (p92 == 1) {                          // 9 holes

                        gst9Rounds3++;

                        if (parm.gRev[i] == 1) {          // if Revenue guest

                           gst9RevRnds3++;
                        }

                     } else {

                        gst18Rounds3++;                       // 18 holes

                        if (parm.gRev[i] == 1) {          // if Revenue guest

                           gst18RevRnds3++;
                        }
                     }
                  }
               } else {

                  if (show2 == 1) {           // if member and not a no-show

                     totRounds3++;

                     if (username2.equals( "" )) {

                        otherRounds3++;                   // not guest, not member

                        if (p92 == 1) {                          // 9 holes

                           other9Rounds3++;

                        } else {

                           other18Rounds3++;                       // 18 holes
                        }

                     } else {                            // member

                        memRounds3++;

                        if (p92 == 1) {                          // 9 holes

                           mem9Rounds3++;

                        } else {

                           mem18Rounds3++;                       // 18 holes
                        }
                     }

                  } else {                     // no-show

                     nshowRounds3++;           // bump no-shows
                  }
               }

               //
               // check all modes of trans
               //
               i = 0;
               loop2a:
               while (i < parm.MAX_Tmodes) {
                  if ((p2cw.equals( parmc.tmodea[i] )) && (show2 == 1)) {   // if matches mode of trans

                     tmodeR3[i]++;
                     break loop2a;
                  }
                  i++;
               }
            }
            if ((!player3.equals( "" )) && (!player3.equalsIgnoreCase( "x"))) {

               guest = false;
               i = 0;

               ploop3:
               while (i < parm.MAX_Guests) {

                  if (player3.startsWith( parm.guest[i] )) {

                     guest = true;
                     break ploop3;
                  }
                  i++;
               }
               if (guest == true) {

                  if (show3 == 1) {           // if guest and not a no-show

                     gstRnds3[i]++;           // update counts for matching guest type
                     gstRounds3++;
                     totRounds3++;

                    if (p93 == 1) {                          // 9 holes

                        gst9Rounds3++;

                        if (parm.gRev[i] == 1) {          // if Revenue guest

                           gst9RevRnds3++;
                        }

                     } else {

                        gst18Rounds3++;                       // 18 holes

                        if (parm.gRev[i] == 1) {          // if Revenue guest

                           gst18RevRnds3++;
                        }
                     }
                  }
               } else {

                  if (show3 == 1) {           // if member and not a no-show

                     totRounds3++;

                     if (username3.equals( "" )) {

                        otherRounds3++;                   // not guest, not member

                        if (p93 == 1) {                          // 9 holes

                           other9Rounds3++;

                        } else {

                           other18Rounds3++;                       // 18 holes
                        }

                     } else {                            // member

                        memRounds3++;

                        if (p93 == 1) {                          // 9 holes

                           mem9Rounds3++;

                        } else {

                           mem18Rounds3++;                       // 18 holes
                        }
                     }

                  } else {                     // no-show

                     nshowRounds3++;           // bump no-shows
                  }
               }

               //
               // check all modes of trans
               //
               i = 0;
               loop3a:
               while (i < parm.MAX_Tmodes) {
                  if ((p3cw.equals( parmc.tmodea[i] )) && (show3 == 1)) {   // if matches mode of trans

                     tmodeR3[i]++;
                     break loop3a;
                  }
                  i++;
               }
            }
            if ((!player4.equals( "" )) && (!player4.equalsIgnoreCase( "x" ))) {

               guest = false;
               i = 0;

               ploop4:
               while (i < parm.MAX_Guests) {

                  if (player4.startsWith( parm.guest[i] )) {

                     guest = true;
                     break ploop4;
                  }
                  i++;
               }
               if (guest == true) {

                  if (show4 == 1) {           // if guest and not a no-show

                     gstRnds3[i]++;           // update counts for matching guest type
                     gstRounds3++;
                     totRounds3++;

                    if (p94 == 1) {                          // 9 holes

                        gst9Rounds3++;

                        if (parm.gRev[i] == 1) {          // if Revenue guest

                           gst9RevRnds3++;
                        }

                     } else {

                        gst18Rounds3++;                       // 18 holes

                        if (parm.gRev[i] == 1) {          // if Revenue guest

                           gst18RevRnds3++;
                        }
                     }
                  }
               } else {

                  if (show4 == 1) {           // if member and not a no-show

                     totRounds3++;

                     if (username4.equals( "" )) {

                        otherRounds3++;                   // not guest, not member

                        if (p94 == 1) {                          // 9 holes

                           other9Rounds3++;

                        } else {

                           other18Rounds3++;                       // 18 holes
                        }

                     } else {                            // member

                        memRounds3++;

                        if (p94 == 1) {                          // 9 holes

                           mem9Rounds3++;

                        } else {

                           mem18Rounds3++;                       // 18 holes
                        }
                     }

                  } else {                     // no-show

                     nshowRounds3++;           // bump no-shows
                  }
               }

               //
               // check all modes of trans
               //
               i = 0;
               loop4a:
               while (i < parm.MAX_Tmodes) {
                  if ((p4cw.equals( parmc.tmodea[i] )) && (show4 == 1)) {   // if matches mode of trans

                     tmodeR3[i]++;
                     break loop4a;
                  }
                  i++;
               }
            }
            if ((!player5.equals( "" )) && (!player5.equalsIgnoreCase( "x" ))) {

               guest = false;
               i = 0;

               ploop5:
               while (i < parm.MAX_Guests) {

                  if (player5.startsWith( parm.guest[i] )) {

                     guest = true;
                     break ploop5;
                  }
                  i++;
               }
               if (guest == true) {

                  if (show5 == 1) {           // if guest and not a no-show

                     gstRnds3[i]++;           // update counts for matching guest type
                     gstRounds3++;
                     totRounds3++;

                    if (p95 == 1) {                          // 9 holes

                        gst9Rounds3++;

                        if (parm.gRev[i] == 1) {          // if Revenue guest

                           gst9RevRnds3++;
                        }

                     } else {

                        gst18Rounds3++;                       // 18 holes

                        if (parm.gRev[i] == 1) {          // if Revenue guest

                           gst18RevRnds3++;
                        }
                     }
                  }
               } else {

                  if (show5 == 1) {           // if member and not a no-show

                     totRounds3++;

                     if (username5.equals( "" )) {

                        otherRounds3++;                   // not guest, not member

                        if (p95 == 1) {                          // 9 holes

                           other9Rounds3++;

                        } else {

                           other18Rounds3++;                       // 18 holes
                        }

                     } else {                            // member

                        memRounds3++;

                        if (p95 == 1) {                          // 9 holes

                           mem9Rounds3++;

                        } else {

                           mem18Rounds3++;                       // 18 holes
                        }
                     }

                  } else {                     // no-show

                     nshowRounds3++;           // bump no-shows
                  }
               }

               //
               // check all modes of trans
               //
               i = 0;
               loop5a:
               while (i < parm.MAX_Tmodes) {
                  if ((p5cw.equals( parmc.tmodea[i] )) && (show5 == 1)) {   // if matches mode of trans

                     tmodeR3[i]++;
                     break loop5a;
                  }
                  i++;
               }
            }

            error = "Count rounds per Member Type - 3";

            //
            // Count rounds per Member Type
            //
            user1 = "";        // init username fields
            user2 = "";
            user3 = "";
            user4 = "";
            user5 = "";
            if ((!username1.equals( "" )) && (show1 == 1)) {

               user1 = username1;
            }
            if ((!username2.equals( "" )) && (show2 == 1)) {

               user2 = username2;
            }
            if ((!username3.equals( "" )) && (show3 == 1)) {

               user3 = username3;
            }
            if ((!username4.equals( "" )) && (show4 == 1)) {

               user4 = username4;
            }
            if ((!username5.equals( "" )) && (show5 == 1)) {

               user5 = username5;
            }

            if (!user1.equals( "" ) || !user2.equals( "" ) || !user3.equals( "" ) ||
                !user4.equals( "" ) || !user5.equals( "" )) {

               for (i=0; i<parm.MAX_Mems; i++) {         // check all mem types

                  if (!parm.mem[i].equals( "" )) {

                     //
                     //  Statement for Member Types
                     //
                     PreparedStatement pstmt4 = con.prepareStatement (
                        "SELECT password FROM member2b WHERE " +
                        "(username = ? OR username = ? OR username = ? OR username = ? OR username = ?) AND m_type = ?");

                     pstmt4.clearParameters();
                     pstmt4.setString(1, user1);
                     pstmt4.setString(2, user2);
                     pstmt4.setString(3, user3);
                     pstmt4.setString(4, user4);
                     pstmt4.setString(5, user5);
                     pstmt4.setString(6, parm.mem[i]);
                     rs2 = pstmt4.executeQuery();

                     while (rs2.next()) {

                        memxRounds3[i]++;
                     }
                     pstmt4.close();
                  }
               }

               error =  "Count rounds per Membership Type - 3";

               //
               // Count rounds per Membership Type
               //
               for (i=0; i<parm.MAX_Mships; i++) {         // check all mem types

                  if (!parm.mship[i].equals( "" )) {

                     //
                     //  Statement for Membership Types
                     //
                     PreparedStatement pstmt5 = con.prepareStatement (
                        "SELECT password FROM member2b WHERE " +
                        "(username = ? OR username = ? OR username = ? OR username = ? OR username = ?) AND m_ship = ?");

                     pstmt5.clearParameters();
                     pstmt5.setString(1, user1);
                     pstmt5.setString(2, user2);
                     pstmt5.setString(3, user3);
                     pstmt5.setString(4, user4);
                     pstmt5.setString(5, user5);
                     pstmt5.setString(6, parm.mship[i]);
                     rs2 = pstmt5.executeQuery();

                     while (rs2.next()) {

                        mshipxRounds3[i]++;
                     }
                     pstmt5.close();
                  }
               }
            }
         }         // end of while for Today

         pstmt2.close();

      }
      catch (Exception exc) {

         out.println("<BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Exception:" + exc.getMessage());
         out.println("<BR><BR>Error:" + error);
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      if (toExcel) {     // if user requested Excel Spreadsheet Format
            out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\">");
      } else {
            out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
      }

      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

      String bgrndcolor = "#336633";      // default
      String fontcolor = "#FFFFFF";      // default

      if (toExcel) {     // if user requested Excel Spreadsheet Format

         bgrndcolor = "#FFFFFF";      // white for excel
         fontcolor = "#000000";      // black for excel
      }

               //
               // add course name header if multi
               //
               if (!courseName.equals( "" )) {

                  out.println("<tr bgcolor=\"" + bgrndcolor + "\"><td colspan=\"2\">");
                  out.println("<font color=\"" + fontcolor + "\" size=\"3\">");
                  out.println("<p align=\"center\"><b>" + courseName + "</b></p>");
                  out.println("</font></td></tr>");
               }

               //
               //  Header row
               //
               out.println("<tr bgcolor=\"" + bgrndcolor + "\"><td>");
                     out.println("<font color=\"" + fontcolor + "\" size=\"2\">");
                     out.println("<p align=\"left\"><b>Stat</b></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font color=\"" + fontcolor + "\" size=\"2\">");
                     out.println("<p align=\"center\"><b>Today</b> (thus far)</p>");
                     out.println("</font></td>");

               //
               //  Build the HTML for each stat gathered above
               //
               out.println("</tr><tr>");                       // Grand totals
               out.println("<td align=\"left\">");
                  out.println("<font size=\"2\"><br>");
                  out.println("<b>Total Rounds Played:</b>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\"><br><b>");
                  out.println(totRounds3);
                  out.println("</b></font></td>");

               out.println("</tr><tr>");                          // blank row for divider
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("</tr><tr>");                     // Total Rounds for Members
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"left\"><b>Rounds by Members:</b></p>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\"><b>");
               if (memRounds3 < 1 || totRounds3 < 1) {
                  out.println(memRounds3);
               } else {
                  out.println(memRounds3 + " (" + (memRounds3 * 100)/totRounds3 + "%)");
               }
                  out.println("</b></font></td>");


               //
               // Rounds per Member Type
               //
               found = false;
               int memtemp = 0;
               for (i=0; i<parm.MAX_Mems; i++) {         // check all mem types

                  if (!parm.mem[i].equals( "" )) {

                     out.println("</tr><tr>");                     // Rounds for Member Type
                     out.println("<td align=\"right\">");
                     out.println("<font size=\"2\">");
                     if (found == false) {
                        out.println("<u>by Member Type</u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                        out.println("<br>");
                     }
                     out.println(parm.mem[i] + ":");
                     out.println("</font></td>");

                     found = true;

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"2\"><br>");
                     if (memxRounds3[i] < 1 || memRounds3 < 1) {
                        out.println(memxRounds3[i]);
                     } else {
                        out.println(memxRounds3[i] + " (" + (memxRounds3[i] * 100)/memRounds3 + "%)");
                     }
                     out.println("</font></td>");

                     memtemp = memtemp + memxRounds3[i];    // keep total
                  }
               }


               //
               //  check for rounds with no member type (member has been deleted from db since round was played)
               //
               memUnknown3 = memRounds3 - memtemp;

               if (memUnknown3 != 0) {

                  out.println("</tr><tr>");                     // Rounds for Unknown Member Type
                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"right\">Unknown (member no longer in database):</p>");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                  if (memUnknown3 < 1 || memRounds3 < 1) {
                     out.println(memUnknown3);
                  } else {
                     out.println(memUnknown3 + " (" + (memUnknown3 * 100)/memRounds3 + "%)");
                  }
                     out.println("</font></td>");
               }


               out.println("</tr><tr>");                          // blank row for divider
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");


               //
               // Rounds per Membership Type
               //
               found = false;
               int mshiptemp = 0;
               for (i=0; i<parm.MAX_Mships; i++) {         // check all mship types

                  if (!parm.mship[i].equals( "" )) {

                     out.println("</tr><tr>");                     // Rounds for Membership Type
                     out.println("<td align=\"right\">");
                     out.println("<font size=\"2\">");
                     if (found == false) {
                        out.println("<u>by Membership Type</u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                        out.println("<br>");
                     }
                     out.println(parm.mship[i] + ":");
                     out.println("</font></td>");

                     found = true;

                     out.println("<td align=\"center\">");
                     out.println("<font size=\"2\"><br>");
                     if (mshipxRounds3[i] < 1 || memRounds3 < 1) {
                        out.println(mshipxRounds3[i]);
                     } else {
                        out.println(mshipxRounds3[i] + " (" + (mshipxRounds3[i] * 100)/memRounds3 + "%)");
                     }
                     out.println("</font></td>");

                     mshiptemp = mshiptemp + mshipxRounds3[i];   // keep total
                  }
               }


               //
               //  check for rounds with no member type (member has been deleted from db since round was played)
               //
               mshipUnknown3 = memRounds3 - mshiptemp;

               if (mshipUnknown3 != 0) {

                  out.println("</tr><tr>");                     // Rounds for Unknown Membership Type
                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"right\">Unknown (member no longer in database):</p>");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                  if (mshipUnknown3 < 1 || memRounds3 < 1) {
                     out.println(mshipUnknown3);
                  } else {
                     out.println(mshipUnknown3 + " (" + (mshipUnknown3 * 100)/memRounds3 + "%)");
                  }
                     out.println("</font></td>");
               }


               out.println("</tr><tr>");                          // blank row for divider
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");


               out.println("</tr><tr>");                     // 9 Hole Rounds for Members
               out.println("<td align=\"right\">");
                  out.println("<font size=\"2\">");
                  out.println("Member 9 Hole Rounds:");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (mem9Rounds3 < 1 || totRounds3 < 1) {
                  out.println(mem9Rounds3);
               } else {
                  out.println(mem9Rounds3 + " (" + (mem9Rounds3 * 100)/totRounds3 + "%)");
               }
                  out.println("</font></td>");

               out.println("</tr><tr>");                     // 18 Hole Rounds for Members
               out.println("<td align=\"right\">");
                  out.println("<font size=\"2\">");
                  out.println("Member 18 Hole Rounds:");
                  out.println("</font><br></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (mem18Rounds3 < 1 || totRounds3 < 1) {
                  out.println(mem18Rounds3);
               } else {
                  out.println(mem18Rounds3 + " (" + (mem18Rounds3 * 100)/totRounds3 + "%)");
               }
                  out.println("</font><br></td>");

               out.println("</tr><tr>");                          // blank row for divider
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("</tr><tr>");                      // Total Rounds by Guests
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"left\"><b>Rounds by Guests:</b></p>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\"><b>");
               if (gstRounds3 < 1 || totRounds3 < 1) {
                  out.println(gstRounds3);
               } else {
                  out.println(gstRounds3 + " (" + (gstRounds3 * 100)/totRounds3 + "%)");
               }
                  out.println("</b></font></td>");

               for (i = 0; i < parm.MAX_Guests; i++) {          // chack all 36 guest types

                  if (!parm.guest[i].equals( "" ) && !parm.guest[i].equals( "$@#!^&*" )) {

                     out.println("</tr><tr>");                     // Rounds for Guest Type
                     out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println("<p align=\"right\">" + parm.guest[i] + ":</p>");
                        out.println("</font></td>");

                     out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                     if (gstRnds3[i] < 1 || gstRounds3 < 1) {
                        out.println(gstRnds3[i]);
                     } else {
                        out.println(gstRnds3[i] + " (" + (gstRnds3[i] * 100)/gstRounds3 + "%)");
                     }
                        out.println("</font></td>");
                  }
               }

               out.println("</tr><tr>");                     // 9 Hole Rounds for Guests
               out.println("<td align=\"right\">");
                  out.println("<font size=\"2\"><br>");
                  out.println("Guest 9 Hole Rounds:");
                  out.println("<br>(Revenue)");
                  out.println("<br>(Non-Revenue)");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\"><br>");
                  if (gst9Rounds3 < 1 || totRounds3 < 1) {
                     out.println(gst9Rounds3);
                  } else {
                     out.println(gst9Rounds3 + " (" + (gst9Rounds3 * 100)/totRounds3 + "%)");
                  }
                  out.println("<br>");
                  if (gst9Rounds3 < 1 || gst9RevRnds3 < 1) {
                     out.println("&nbsp;");
                  } else {
                     out.println(gst9RevRnds3 + " (" + (gst9RevRnds3 * 100)/gst9Rounds3 + "%)");
                  }
                  gst9RevRnds3 = gst9Rounds3 - gst9RevRnds3;         // get Non-Rev rounds
                  out.println("<br>");
                  if (gst9Rounds3 < 1 || gst9RevRnds3 < 1) {
                     out.println("&nbsp;");
                  } else {
                     out.println(gst9RevRnds3 + " (" + (gst9RevRnds3 * 100)/gst9Rounds3 + "%)");
                  }
                  out.println("</font></td>");

               out.println("</tr><tr>");                     // 18 Hole Rounds for Guests
               out.println("<td align=\"right\">");
                  out.println("<font size=\"2\">");
                  out.println("Guest 18 Hole Rounds:");
                  out.println("<br>(Revenue)");
                  out.println("<br>(Non-Revenue)");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  if (gst18Rounds3 < 1 || totRounds3 < 1) {
                     out.println(gst18Rounds3);
                  } else {
                     out.println(gst18Rounds3 + " (" + (gst18Rounds3 * 100)/totRounds3 + "%)");
                  }
                  out.println("<br>");
                  if (gst18Rounds3 < 1 || gst18RevRnds3 < 1) {
                     out.println("&nbsp;");
                  } else {
                     out.println(gst18RevRnds3 + " (" + (gst18RevRnds3 * 100)/gst18Rounds3 + "%)");
                  }
                  gst18RevRnds3 = gst18Rounds3 - gst18RevRnds3;         // get Non-Rev rounds
                  out.println("<br>");
                  if (gst18Rounds3 < 1 || gst18RevRnds3 < 1) {
                     out.println("&nbsp;");
                  } else {
                     out.println(gst18RevRnds3 + " (" + (gst18RevRnds3 * 100)/gst18Rounds3 + "%)");
                  }
                  out.println("</font></td>");

               out.println("</tr><tr>");                          // blank row for divider
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               //
               //  Only display 'Others' if there were some found (non-members, non-guests)
               //
               if (otherRounds3 > 0) {

                  out.println("</tr><tr>");                      // Total Rounds by Others
                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"left\"><b>Rounds by Others:</b></p>");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\"><b>");
                  if (otherRounds3 < 1 || totRounds3 < 1) {
                     out.println(otherRounds3);
                  } else {
                     out.println(otherRounds3 + " (" + (otherRounds3 * 100)/totRounds3 + "%)");
                  }
                     out.println("</b></font></td>");

                  out.println("</tr><tr>");                     // 9 Hole Rounds for Others
                  out.println("<td align=\"right\">");
                     out.println("<font size=\"2\">");
                     out.println("Other 9 Hole Rounds:");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                  if (other9Rounds3 < 1 || totRounds3 < 1) {
                     out.println(other9Rounds3);
                  } else {
                     out.println(other9Rounds3 + " (" + (other9Rounds3 * 100)/totRounds3 + "%)");
                  }
                     out.println("</font></td>");

                  out.println("</tr><tr>");                     // 18 Hole Rounds for Others
                  out.println("<td align=\"right\">");
                     out.println("<font size=\"2\">");
                     out.println("Other 18 Hole Rounds:");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">");
                  if (other18Rounds3 < 1 || totRounds3 < 1) {
                     out.println(other18Rounds3);
                  } else {
                     out.println(other18Rounds3 + " (" + (other18Rounds3 * 100)/totRounds3 + "%)");
                  }
                     out.println("</font></td>");

                  out.println("</tr><tr>");                          // blank row for divider
                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">&nbsp;");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font size=\"2\">&nbsp;");
                     out.println("</font></td>");
               }
               out.println("</tr>");


               //
               //  Check all the Transportation Modes - now 16 configurable modes (V4)
               //
               for (i=0; i<parm.MAX_Tmodes; i++) {

                  if (tmodeR3[i] > 0) {

                     out.println("<tr>");
                     out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println("<p align=\"left\"><b>" +parmc.tmode[i]+ " Rounds:</b></p>");
                        out.println("</font></td>");

                     out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                     if (tmodeR3[i] < 1 || totRounds3 < 1) {
                        out.println(tmodeR3[i]);
                     } else {
                        out.println(tmodeR3[i] + " (" + (tmodeR3[i] * 100)/totRounds3 + "%)");
                     }
                        out.println("</font></td>");
                     out.println("</tr>");                          // blank row for divider
                  }
               }

               out.println("<tr>");                          // blank row for divider
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">&nbsp;");
                  out.println("</font></td>");

               out.println("</tr><tr>");
               out.println("<td align=\"left\">");
                  out.println("<font size=\"2\">");
                  out.println("<b>Member No-Shows:</b>");
                  out.println("<br></font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println(nshowRounds3);
                  out.println("<br></font></td>");

            out.println("</font></tr></table><br>");

   }       // end of while Courses - do all courses

   if (!toExcel) {     // if normal request
       out.println("</td></tr></table>");                // end of main page table & column

       out.println("<form method=\"get\" action=\"Proshop_announce\">");
       out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</form></font>");
   }

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");
   out.close();

 }  // end of goRounds


 // *********************************************************
 //  Report Type = Tee Times (proshop vs members)
 // *********************************************************

 private void goTeetimes(HttpServletRequest req, PrintWriter out, Connection con) {

   PreparedStatement pstmt = null;
   ResultSet rs = null;

   //
   //  get the session and the club name
   //
   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   int sess_activity_id = (Integer)sess.getAttribute("activity_id");

   int tmp_tlt = (Integer)sess.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;
   
   boolean includeEvents = false;

   if (req.getParameter("events") != null) includeEvents = true;          // if we are to include event tee times

   String sql = "";
   
   long proNewlmn = 0;         // pro values
   long proModlmn = 0;
   long proNewthmn = 0;
   long proModthmn = 0;
   long proNewthyr = 0;
   long proModthyr = 0;
   long proNewlyr = 0;

   long memNewlmn = 0;         // member values
   long memModlmn = 0;
   long memNewthmn = 0;
   long memModthmn = 0;
   long memNewthyr = 0;
   long memModthyr = 0;
   long memNewlyr = 0;
   long memModlyr = 0;

   long hotelNewlmn = 0;         // hotel values
   long hotelModlmn = 0;
   long hotelNewthmn = 0;
   long hotelModthmn = 0;
   long hotelNewthyr = 0;
   long hotelModthyr = 0;
   long hotelNewlyr = 0;
   long hotelModlyr = 0;

   long totProlmn = 0;         // sub-totals
   long totProthmn = 0;
   long totProthyr = 0;
   long totProlyr = 0;
   long totMemlmn = 0;
   long totMemthmn = 0;
   long totMemthyr = 0;
   long totMemlyr = 0;
   long totHotellmn = 0;
   long totHotelthmn = 0;
   long totHotelthyr = 0;
   long totHotellyr = 0;

   long totLastmn = 0;         // grand totals
   long totThismn = 0;
   long totThisyr = 0;
   long totLastyr = 0;
/*
   int proNew = 0;
   int proMod = 0;
   int memNew = 0;
   int memMod = 0;
   int hotelNew = 0;
   int hotelMod = 0;
  */
   int year = 0;
   int lastYear = 0;
   int month = 0;
   int lastMonth = 0;
   int lastMonthYr = 0;
   int hotel = 0;

   //
   //  Get today's date and calculate date & time values
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);

   lastYear = (year - 1);

   lastMonth = month;      // month starts at zero
   lastMonthYr = year;

   if (month == 0) {       // if current month is Jan

      lastMonth = 12;      // last month is Dec
      lastMonthYr--;       // adjust year
   }

   month = month + 1;      // adjust current month value

   //
   // use the dates provided to search the tee times tables
   //
   try {

      //
      //  First, see if Hotels are supported
      //
      pstmt = con.prepareStatement ("SELECT hotel FROM club5");

      pstmt.clearParameters();        // clear the parms
      rs = pstmt.executeQuery();      // execute the prepared stmt for teepast2

      if (rs.next()) {

         hotel = rs.getInt(1);
      }
      pstmt.close();

      //
      //  Get Last Month counts
      //
      if (sess_activity_id == 0) {            // if Golf
         
         if (includeEvents == true) {

            sql = "SELECT " +
                     "SUM(proNew) AS proNew, SUM(proMod) AS proMod, " +
                     "SUM(memNew) AS memNew, SUM(memMod) AS memMod, " +
                     "SUM(hotelNew) AS hotelNew, SUM(hotelMod) AS hotelMod " +
                  "FROM teepast2 " +
                  "WHERE mm = ? AND yy = ?";
            
         } else {      // do NOT include event times (default)
            
            sql = "SELECT " +
                     "SUM(proNew) AS proNew, SUM(proMod) AS proMod, " +
                     "SUM(memNew) AS memNew, SUM(memMod) AS memMod, " +
                     "SUM(hotelNew) AS hotelNew, SUM(hotelMod) AS hotelMod " +
                  "FROM teepast2 " +
                  "WHERE mm = ? AND yy = ? AND event = ''";
         }

      } else {        // an activity

         sql = "SELECT " +
             "SUM(proNew) AS proNew, SUM(proMod) AS proMod, " +
             "SUM(memNew) AS memNew, SUM(memMod) AS memMod, " +
             "'0' AS hotelNew, '0' AS hotelMod " +
          "FROM activity_sheets " +
          "WHERE " +
             "DATE_FORMAT(date_time, '%m') = ? AND " +
             "DATE_FORMAT(date_time, '%Y') = ? AND " +
             "activity_id IN (" + getActivity.buildInString(sess_activity_id, 1, con) + ")";
      }

      pstmt = con.prepareStatement ( sql );
      pstmt.clearParameters();
      pstmt.setInt(1, lastMonth);
      pstmt.setInt(2, lastMonthYr);
      rs = pstmt.executeQuery();

      if (rs.next()) {

         proNewlmn += rs.getInt("proNew");       // gather totals for Last Month
         proModlmn += rs.getInt("proMod");
         memNewlmn += rs.getInt("memNew");
         memModlmn += rs.getInt("memMod");
         hotelNewlmn += rs.getInt("hotelNew");
         //hotelNewlmn += ((rs.getInt("hotelNew") > 1) ? 1 : rs.getInt("hotelNew"));
         hotelModlmn += rs.getInt("hotelMod");

      }

      pstmt.close();

      //
      //  Get This Month counts
      //
      if (sess_activity_id == 0) {            // if Golf
         
         if (includeEvents == true) {      // include event times

            sql = "SELECT  SUM(f1) AS pronew, SUM(f2) AS promod, " +
                          "SUM(f3) AS memNew, SUM(f4) as memMod, " +
                          "SUM(f5) AS hotelNew, SUM(f6) AS hotelMod " +
                  "FROM ( " +
                          "SELECT  SUM(proNew) AS f1, SUM(proMod) AS f2, " +
                                  "SUM(memNew) AS f3, SUM(memMod) AS f4, " +
                                  "SUM(hotelNew) AS f5, SUM(hotelMod) AS f6 " +
                          "FROM teecurr2 " +
                          "WHERE mm = ? AND yy = ? " +

                          "UNION ALL " +

                          "SELECT  SUM(proNew), SUM(proMod), SUM(memNew), " +
                                  "SUM(memMod), SUM(hotelNew), SUM(hotelMod) " +
                          "FROM teepast2 " +
                          "WHERE mm = ? AND yy = ?" +
                  ") AS t1000";

         } else {     // do NOT include events
            
            sql = "SELECT  SUM(f1) AS pronew, SUM(f2) AS promod, " +
                          "SUM(f3) AS memNew, SUM(f4) as memMod, " +
                          "SUM(f5) AS hotelNew, SUM(f6) AS hotelMod " +
                  "FROM ( " +
                          "SELECT  SUM(proNew) AS f1, SUM(proMod) AS f2, " +
                                  "SUM(memNew) AS f3, SUM(memMod) AS f4, " +
                                  "SUM(hotelNew) AS f5, SUM(hotelMod) AS f6 " +
                          "FROM teecurr2 " +
                          "WHERE mm = ? AND yy = ? AND event = '' " +

                          "UNION ALL " +

                          "SELECT  SUM(proNew), SUM(proMod), SUM(memNew), " +
                                  "SUM(memMod), SUM(hotelNew), SUM(hotelMod) " +
                          "FROM teepast2 " +
                          "WHERE mm = ? AND yy = ? AND event = ''" +
                  ") AS t1000";
         }

      } else {       // an activity

         sql = "SELECT " +
                "SUM(proNew) AS proNew, SUM(proMod) AS proMod, " +
                "SUM(memNew) AS memNew, SUM(memMod) AS memMod, " +
                "'0' AS hotelNew, '0' AS hotelMod " +
             "FROM activity_sheets " +
             "WHERE " +
                "DATE_FORMAT(date_time, '%m') = ? AND " +
                "DATE_FORMAT(date_time, '%Y') = ? AND " +
                "activity_id IN (" + getActivity.buildInString(sess_activity_id, 1, con) + ")";
      }

      pstmt = con.prepareStatement ( sql );

      pstmt.clearParameters();
      pstmt.setInt(1, month);
      pstmt.setInt(2, year);
      if (sess_activity_id == 0) {
          pstmt.setInt(3, month);
          pstmt.setInt(4, year);
      }
      rs = pstmt.executeQuery();

      if (rs.next()) {


         proNewthmn += rs.getInt("proNew");       // gather totals for Last Month
         proModthmn += rs.getInt("proMod");
         memNewthmn += rs.getInt("memNew");
         memModthmn += rs.getInt("memMod");
         hotelNewthmn += rs.getInt("hotelNew");
         //hotelNewthmn += ((rs.getInt("hotelNew") > 1) ? 1 : rs.getInt("hotelNew"));
         hotelModthmn += rs.getInt("hotelMod");

      }

      pstmt.close();


      //
      //  Get This Year counts
      //
      if (sess_activity_id == 0) {            // if Golf
         
         if (includeEvents == true) {      // include event times

            sql = "SELECT  SUM(f1) AS pronew, SUM(f2) AS promod, " +
                          "SUM(f3) AS memNew, SUM(f4) as memMod, " +
                          "SUM(f5) AS hotelNew, SUM(f6) AS hotelMod " +
                  "FROM ( " +
                          "SELECT  SUM(proNew) AS f1, SUM(proMod) AS f2, " +
                                  "SUM(memNew) AS f3, SUM(memMod) AS f4, " +
                                  "SUM(hotelNew) AS f5, SUM(hotelMod) AS f6 " +
                          "FROM teecurr2 " +
                          "WHERE yy = ? " +

                          "UNION ALL " +

                          "SELECT  SUM(proNew), SUM(proMod), SUM(memNew), " +
                                  "SUM(memMod), SUM(hotelNew), SUM(hotelMod) " +
                          "FROM teepast2 " +
                          "WHERE yy = ?" +
                  ") AS t1000";

         } else {         // do NOT include event times
            
            sql = "SELECT  SUM(f1) AS pronew, SUM(f2) AS promod, " +
                          "SUM(f3) AS memNew, SUM(f4) as memMod, " +
                          "SUM(f5) AS hotelNew, SUM(f6) AS hotelMod " +
                  "FROM ( " +
                          "SELECT  SUM(proNew) AS f1, SUM(proMod) AS f2, " +
                                  "SUM(memNew) AS f3, SUM(memMod) AS f4, " +
                                  "SUM(hotelNew) AS f5, SUM(hotelMod) AS f6 " +
                          "FROM teecurr2 " +
                          "WHERE yy = ? AND event = '' " +

                          "UNION ALL " +

                          "SELECT  SUM(proNew), SUM(proMod), SUM(memNew), " +
                                  "SUM(memMod), SUM(hotelNew), SUM(hotelMod) " +
                          "FROM teepast2 " +
                          "WHERE yy = ? AND event = ''" +
                  ") AS t1000";
         }
         
      } else {

         sql = "SELECT " +
                   "SUM(proNew) AS proNew, SUM(proMod) AS proMod, " +
                   "SUM(memNew) AS memNew, SUM(memMod) AS memMod, " +
                   "'0' AS hotelNew, '0' AS hotelMod " +
                "FROM activity_sheets " +
                "WHERE " +
                   "DATE_FORMAT(date_time, '%Y') = ? AND " +
                   "activity_id IN (" + getActivity.buildInString(sess_activity_id, 1, con) + ")";
      }

      out.println("<!-- " + sql + " -->");

      pstmt = con.prepareStatement ( sql );
      pstmt.clearParameters();
      pstmt.setInt(1, year);
      if (sess_activity_id == 0) {
          pstmt.setInt(2, year);
      }
      rs = pstmt.executeQuery();

      if (rs.next()) {

         proNewthyr += rs.getInt("proNew");       // gather totals for Last Month
         proModthyr += rs.getInt("proMod");
         memNewthyr += rs.getInt("memNew");
         memModthyr += rs.getInt("memMod");
         hotelNewthyr += rs.getInt("hotelNew");
         //hotelNewthyr += ((rs.getInt("hotelNew") > 1) ? 1 : rs.getInt("hotelNew"));
         hotelModthyr += rs.getInt("hotelMod");

      }

      pstmt.close();


      //
      //  Get Last Year counts
      //
      if (sess_activity_id == 0) {            // if Golf
         
         if (includeEvents == true) {      // include event times

            sql = "SELECT " +
                      "SUM(proNew) AS proNew, SUM(proMod) AS proMod, " +
                      "SUM(memNew) AS memNew, SUM(memMod) AS memMod, " +
                      "SUM(hotelNew) AS hotelNew, SUM(hotelMod) AS hotelMod " +
                   "FROM teepast2 WHERE yy = ?";

         } else {    // do NOT include event times
            
            sql = "SELECT " +
                      "SUM(proNew) AS proNew, SUM(proMod) AS proMod, " +
                      "SUM(memNew) AS memNew, SUM(memMod) AS memMod, " +
                      "SUM(hotelNew) AS hotelNew, SUM(hotelMod) AS hotelMod " +
                   "FROM teepast2 WHERE yy = ? AND event = ''";
         }
         
      } else {

         sql = "SELECT " +
                "SUM(proNew) AS proNew, SUM(proMod) AS proMod, " +
                "SUM(memNew) AS memNew, SUM(memMod) AS memMod, " +
                "'0' AS hotelNew, '0' AS hotelMod " +
             "FROM activity_sheets " +
             "WHERE " +
                "DATE_FORMAT(date_time, '%Y') = ? AND " +
                "activity_id IN (" + getActivity.buildInString(sess_activity_id, 1, con) + ")";
      }

      out.println("<!-- " + sql + " -->");

      pstmt = con.prepareStatement ( sql );

      pstmt.clearParameters();
      pstmt.setInt(1, lastYear);
      rs = pstmt.executeQuery();

      if (rs.next()) {

         proNewlyr += rs.getInt("proNew");       // gather totals for Last Year
         //proModlyr += rs.getInt("proMod");     // not tracked??
         memNewlyr += rs.getInt("memNew");
         memModlyr += rs.getInt("memMod");
         hotelNewlyr += rs.getInt("hotelNew");
         //hotelNewlyr += ((rs.getInt("hotelNew") > 1) ? 1 : rs.getInt("hotelNew"));
         hotelModlyr += rs.getInt("hotelMod");

      }
      pstmt.close();


      totProlmn = proNewlmn;                     // Last month Pro sub-totals
      totProthmn = proNewthmn;                   // This month Pro sub-totals
      totProthyr = proNewthyr;                   // This year Pro sub-totals
      totProlyr = proNewlyr;                     // Last year Pro sub-totals

      totMemlmn = memNewlmn;                     // Last month Mem sub-totals
      totMemthmn = memNewthmn;                   // This month Mem sub-totals
      totMemthyr = memNewthyr;                   // This year Mem sub-totals
      totMemlyr = memNewlyr;                     // Last year Mem sub-totals

      totHotellmn = hotelNewlmn;                 // Last month hotel sub-totals
      totHotelthmn = hotelNewthmn;               // This month hotel sub-totals
      totHotelthyr = hotelNewthyr;               // This year hotel sub-totals
      totHotellyr = hotelNewlyr;                 // Last year hotel sub-totals

      if (hotel > 0) {             // if hotel supported

         totLastmn = totProlmn + totMemlmn + totHotellmn;          // Last month grand total
         totThismn = totProthmn + totMemthmn + totHotelthmn;       // This month grand total
         totThisyr = totProthyr + totMemthyr + totHotelthyr;       // This year grand total
         totLastyr = totProlyr + totMemlyr + totHotellyr;          // Last year grand total

      } else {                  // no hotels

         totLastmn = totProlmn + totMemlmn;                    // Last month grand total
         totThismn = totProthmn + totMemthmn;                  // This month grand total
         totThisyr = totProthyr + totMemthyr;                  // This year grand total
         totLastyr = totProlyr + totMemlyr;                    // Last year grand total
      }

   } catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Proshop Reports Page - Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<br><br><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { pstmt.close(); }
      catch (Exception ignore) {}

   }

   //
   //  Build the HTML page to display search results
   //
   out.println(SystemUtils.HeadTitle("Proshop Reports Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

      out.println("<font size=\"3\">");
      if (sess_activity_id == 0) {
          out.println("<p><b>" + ((IS_TLT) ? "Notification" : "Tee Time") + " Statistics</b></p>");
      } else {
          out.println("<p><b>" + getActivity.getActivityName(sess_activity_id, con) + " Reservation Statistics</b></p>");
      }
      out.println("</font>");

         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<tr bgcolor=\"#336633\"><td colspan=\"5\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\">This report identifies how many times the " + ((sess_activity_id == 0) ? "Golf Shop" : "Staff") + " made");
                  if (sess_activity_id == 0) {
                      if (hotel > 0) {
                         out.println("<br>" + ((IS_TLT) ? "notifications" : "tee times") + ", Hotel users made " + ((IS_TLT) ? "notifications" : "tee times") + " and Members made " + ((IS_TLT) ? "notifications" : "tee times") + ".");
                      } else {
                         out.println("<br>" + ((IS_TLT) ? "notifications" : "tee times") + " versus how many times Members made " + ((IS_TLT) ? "notifications" : "tee times") + ".");
                      }
                      out.println("<br><br>Percentages represent new " + ((IS_TLT) ? "notifications" : "tee times") + " only and");
                  } else {
                      if (hotel > 0) {
                         out.println("<br>reservations, Hotel users made reservations and Members made reservations.");
                      } else {
                         out.println("<br>reservations versus how many times Members made reservations.");
                      }
                      out.println("<br><br>Percentages represent new reservations only and");
                  }
                  out.println("<br>are rounded to the nearest whole number.</p>");
                  out.println("</font></td></tr>");

            out.println("<tr bgcolor=\"#336633\"><td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"left\">&nbsp;</p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><b>Last Month</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><b>This Month<br>(To Date)</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><b>Last Year</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><b>This Year<br>(To Date)</b></p>");
                  out.println("</font></td>");

            out.println("</tr><tr>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\"><b>" + ((sess_activity_id == 0) ? "Golf Shop New Tee Times" : "Pro Shop New Reservations") + ":</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (proNewlmn < 1 || totLastmn < 1) {
                     out.println("<p align=\"center\">" + proNewlmn + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + proNewlmn + " (" + (proNewlmn * 100)/totLastmn + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (proNewthmn < 1 || totThismn < 1) {
                     out.println("<p align=\"center\">" + proNewthmn + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + proNewthmn + " (" + (proNewthmn * 100)/totThismn + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (proNewlyr < 1 || totLastyr < 1) {
                     out.println("<p align=\"center\">" + proNewlyr + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + proNewlyr + " (" + (proNewlyr * 100)/totLastyr + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (proNewthyr < 1 || totThisyr < 1) {
                     out.println("<p align=\"center\">" + proNewthyr + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + proNewthyr + " (" + (proNewthyr * 100)/totThisyr + "%)</p>");
                  }
                  out.println("</font></td>");

            out.println("</tr><tr>");

            if (hotel > 0) {

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\"><b>Hotel User New " + ((sess_activity_id == 0) ? "Tee Times" : "Reservations") + ":</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (hotelNewlmn < 1 || totLastmn < 1) {
                     out.println("<p align=\"center\">" + hotelNewlmn + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + hotelNewlmn + " (" + (hotelNewlmn * 100)/totLastmn + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (hotelNewthmn < 1 || totThismn < 1) {
                     out.println("<p align=\"center\">" + hotelNewthmn + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + hotelNewthmn + " (" + (hotelNewthmn * 100)/totThismn + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (hotelNewlyr < 1 || totLastyr < 1) {
                     out.println("<p align=\"center\">" + hotelNewlyr + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + hotelNewlyr + " (" + (hotelNewlyr * 100)/totLastyr + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (hotelNewthyr < 1 || totThisyr < 1) {
                     out.println("<p align=\"center\">" + hotelNewthyr + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + hotelNewthyr + " (" + (hotelNewthyr * 100)/totThisyr + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("</tr><tr>");

            } // end if hotel supported

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\"><b>Member New " + ((sess_activity_id == 0) ? "Tee Times" : "Reservations") + ":</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (memNewlmn < 1 || totLastmn < 1) {
                     out.println("<p align=\"center\">" + memNewlmn + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + memNewlmn + " (" + (((memNewlmn * 100)/totLastmn) +1) + "%)</p>");   // round member counts up 1% (to make 100% total)
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (memNewthmn < 1 || totThismn < 1) {
                     out.println("<p align=\"center\">" + memNewthmn + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + memNewthmn + " (" + (((memNewthmn * 100)/totThismn) +1) + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (memNewlyr < 1 || totLastyr < 1) {
                     out.println("<p align=\"center\">" + memNewlyr + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + memNewlyr + " (" + (((memNewlyr * 100)/totLastyr) +1) + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (memNewthyr < 1 || totThisyr < 1) {
                     out.println("<p align=\"center\">" + memNewthyr + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + memNewthyr + " (" + (((memNewthyr * 100)/totThisyr) +1) + "%)</p>");
                  }
                  out.println("</font></td>");

            out.println("</tr><tr>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\"><br><b>Total New " + ((sess_activity_id == 0) ? "Tee Times" : "Reservations") + ":</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br><b>" + totLastmn + "</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br><b>" + totThismn + "</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br><b>" + totLastyr + "</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br><b>" + totThisyr + "</b></p>");
                  out.println("</font></td>");

            out.println("</tr><tr>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\"><br><b>Member Modified " + ((sess_activity_id == 0) ? "Tee Times" : "Reservations") + ":</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>" + memModlmn + "</p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>" + memModthmn + "</p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>" + memModlyr + "</p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>" + memModthyr + "</p>");
                  out.println("</font></td>");

            out.println("</tr>");
         out.println("</font></table>");
      out.println("</td></tr></table>");                // end of main page table & column

      if (sess_activity_id == 0) {            // if Golf
         
         if (includeEvents == true) {      // include event times
            
            out.println("<p align=\"center\">The above counts include tee times scheduled as part of an event.<BR>");
            out.println("<form method=\"post\" action=\"Proshop_reports\">");
            out.println("<input type=\"hidden\" name=\"teetimes\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"Do Not Include Event Times\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");            
            out.println("</p>");
            
         } else { 

            out.println("<p align=\"center\">The above counts do not include tee times scheduled as part of an event.<BR>");
            out.println("<form method=\"post\" action=\"Proshop_reports\">");
            out.println("<input type=\"hidden\" name=\"events\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"teetimes\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"Include Event Times\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");            
            out.println("</p>");      
         }
      }
            
      out.println("<BR><form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

      //
      //  End of HTML page
      //
      out.println("</center></font></body></html>");
      out.close();

 }  // end of goTeetimes


 // *********************************************************
 //  Report Type = Custom Date Range (custom=yes) - from proshop_reports.htm
 // *********************************************************

 private void goCustom(HttpServletRequest req, PrintWriter out, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   String fname = "";
   String lname = "";
   String mname = "";
   String user = "";

   //
   //  get the session and the club name
   //
   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   /*
   Calendar cal = new GregorianCalendar();
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH) + 1;
   int day = cal.get(Calendar.DAY_OF_MONTH);
    */

   //
   //  Prompt user for date range
   //
   out.println(SystemUtils.HeadTitle("Proshop Custom Date Report"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td>");  

      out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<b>Number of Rounds Played Report</b><br>");
      out.println("<br>Select the date range below.<br>");
      out.println("<b>Note:</b>  Only rounds before today will be included in the counts.<br><br>");
      out.println("Click on <b>Continue</b> to generate the report.");
      out.println("</font></td></tr></table><br>");
      
      //
      // Build the custom date range calendars and form
      //
      Common_Config.buildReportCals("Proshop_reports", out);
      
      
      
      /*
      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
         out.println("<form action=\"Proshop_reports\" method=\"post\" target=\"bot\">");
         out.println("<input type=\"hidden\" name=\"custom2\" value=\"yes\">");

            out.println("<tr><td>");
               out.println("<font size=\"2\">");
               out.println("<div id=\"awmobject1\">");        // allow menus to show over this box
               out.println("Start Date:&nbsp;&nbsp;&nbsp;");
                 out.println("Month:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"smonth\">");
                      out.println("<option selected value=\"01\">JAN</option>");
                      out.println("<option value=\"02\">FEB</option>");
                      out.println("<option value=\"03\">MAR</option>");
                      out.println("<option value=\"04\">APR</option>");
                      out.println("<option value=\"05\">MAY</option>");
                      out.println("<option value=\"06\">JUN</option>");
                      out.println("<option value=\"07\">JUL</option>");
                      out.println("<option value=\"08\">AUG</option>");
                      out.println("<option value=\"09\">SEP</option>");
                      out.println("<option value=\"10\">OCT</option>");
                      out.println("<option value=\"11\">NOV</option>");
                      out.println("<option value=\"12\">DEC</option>");
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"sday\">");

                 for (int i=1; i<=31; i++) {

                     out.println("<option value=\"" + i + "\">" + i + "</option>");

                 }

                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"syear\">");

                 for (int i=2003; i<=year; i++) {

                     Common_Config.buildOption(i, i, year, out);

                 }

                 out.println("</select></div><br><br>");
                 out.println("<div id=\"awmobject2\">");        // allow menus to show over this box
               out.println("End Date:&nbsp;&nbsp;&nbsp;&nbsp;");
                 out.println("Month:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"emonth\">");

                     Common_Config.buildOption(1,  "JAN", month, out);
                     Common_Config.buildOption(2,  "FEB", month, out);
                     Common_Config.buildOption(3,  "MAR", month, out);
                     Common_Config.buildOption(4,  "APR", month, out);
                     Common_Config.buildOption(5,  "MAY", month, out);
                     Common_Config.buildOption(6,  "JUN", month, out);
                     Common_Config.buildOption(7,  "JUL", month, out);
                     Common_Config.buildOption(8,  "AUG", month, out);
                     Common_Config.buildOption(9,  "SEP", month, out);
                     Common_Config.buildOption(10, "OCT", month, out);
                     Common_Config.buildOption(11, "NOV", month, out);
                     Common_Config.buildOption(12, "DEC", month, out);

                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"eday\">");

                 for (int i=1; i<=31; i++) {

                     Common_Config.buildOption(i, i, day, out);

                 }

                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"eyear\">");

                 for (int i=2003; i<=year; i++) {

                    Common_Config.buildOption(i, i, year, out);

                 }

                 out.println("</select></div><br><br>");

      out.println("<p align=\"center\"><input type=\"submit\" value=\"Continue\"></p>");
      out.println("</td></tr></table>");
       */
      
      
      
      
      out.println("</font></td></tr></form></table>");         // end of main page table

      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
   out.println("</center></font></body></html>");
   out.close();

 }  // end of Custom


 // *****************************************************************************************
 //  Report Type = Number of Rounds, Custom Date (custom2=yes) - from self (goCustom above)
 // *****************************************************************************************

 private void Custom2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;


   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // golf only report

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   //  get the session and the club name
   //
   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

   String club = (String)sess.getAttribute("club");      // get club name
   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  get the club parameters
   //
   try {
      getClub.getParms(con, parm);        // get the club parms
   }
   catch (Exception e) {
   }

   long sdate = 0;
   long edate = 0;
   int mm  = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int count = 0;

   int memUnknown = 0;
   int memUnknown9 = 0;
   int memUnknown18 = 0;
   int mshipUnknown = 0;
   int mshipUnknown9 = 0;
   int mshipUnknown18 = 0;

   int [] tmodeR1 = new int [parm.MAX_Tmodes];       // use arrays for the 16 modes of trans
   int [] tmode9R1 = new int [parm.MAX_Tmodes];
   int [] tmode18R1 = new int [parm.MAX_Tmodes];

   int tmodeOldR91 = 0;
   int tmodeOldR181 = 0;

   int nshowRounds1 = 0;
   int nshow9Rounds1 = 0;
   int nshow18Rounds1 = 0;
   int mnshowRounds1 = 0;
   int mnshow9Rounds1 = 0;
   int mnshow18Rounds1 = 0;
   int gnshowRounds1 = 0;
   int gnshow9Rounds1 = 0;
   int gnshow18Rounds1 = 0;

   int otherRounds1 = 0;
   int other9Rounds1 = 0;
   int other18Rounds1 = 0;

   int totRounds1 = 0;
   int memRounds1 = 0;

   int [] memxRounds1 = new int [parm.MAX_Mems];       // use array for the Member types
   int mem9Rounds1 = 0;
   int mem18Rounds1 = 0;

   int [] memxRounds9 = new int [parm.MAX_Mems];
   int [] memxRounds18 = new int [parm.MAX_Mems];

   int [] mshipxRounds1 = new int [parm.MAX_Mships];       // use array for the Membership types
   int mship9Rounds1 = 0;
   int mship18Rounds1 = 0;

   int [] mshipxRounds9 = new int [parm.MAX_Mships];
   int [] mshipxRounds18 = new int [parm.MAX_Mships];

   int [] gstRnds1 = new int [parm.MAX_Guests];       // use array for the 36 guest types

   int gstRounds1 = 0;
   int gst9Rounds1 = 0;
   int gst18Rounds1 = 0;
   int gst9RevRnds1 = 0;
   int gst18RevRnds1 = 0;

   int [] gst1Rnds9 = new int [parm.MAX_Guests];       // use array for the 36 guest types
   int [] gst1Rnds18 = new int [parm.MAX_Guests];

   int multi = 0;                 // multiple course support
   int index = 0;
   int i = 0;
   int i2 = 0;
   int count2 = 0;                 // number of courses

   //
   //  ints to hold stats from db table
   //
   int [] memxr9 = new int [parm.MAX_Mems];
   int [] memxr18 = new int [parm.MAX_Mems];

   int [] mshipxr9 = new int [parm.MAX_Mships];
   int [] mshipxr18 = new int [parm.MAX_Mships];

   int [] gstr9 = new int [parm.MAX_Guests];       // use array for the 36 guest types
   int [] gstr18 = new int [parm.MAX_Guests];

   int other9 = 0;
   int other18 = 0;
   int cart9 = 0;
   int cart18 = 0;
   int cady9 = 0;
   int cady18 = 0;
   int pc9 = 0;
   int pc18 = 0;
   int wa9 = 0;
   int wa18 = 0;
   int memnshow9 = 0;
   int memnshow18 = 0;
   int gstnshow9 = 0;
   int gstnshow18 = 0;
   int memunk9 = 0;
   int memunk18 = 0;
   int mshipunk9 = 0;
   int mshipunk18 = 0;

   int tmodeOldR9 = 0;
   int tmodeOldR18 = 0;

   int [] tmode9 = new int [parm.MAX_Tmodes];
   int [] tmode18 = new int [parm.MAX_Tmodes];

   //
   //  Array to hold the course names
   //
   ArrayList<String> course = new ArrayList<String>();      // unlimited courses

   String courseName = "";        // course names

   String error = "None";

   boolean found = false;
   boolean toExcel = false;

   //
   // Process request according to the dates
   //
   // Get the parameters entered
   //
   String smonth = req.getParameter("smonth");
   String sday = req.getParameter("sday");
   String syear = req.getParameter("syear");

   String emonth = req.getParameter("emonth");
   String eday = req.getParameter("eday");
   String eyear = req.getParameter("eyear");

   // Make copies of the unmodified start/end dates to pass to the excel feature
   String excelsmonth = smonth;
   String excelsday = sday;
   String excelsyear = syear;

   String excelemonth = emonth;
   String exceleday = eday;
   String exceleyear = eyear;

   //
   //  Convert the string values to int's
   //
   try {
      mm = Integer.parseInt(smonth);
      dd = Integer.parseInt(sday);
      yy = Integer.parseInt(syear);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   sdate = yy * 10000;                            // create a date field of yyyymmdd
   sdate = sdate + (mm * 100);
   sdate = sdate + dd;

   try {
      mm = Integer.parseInt(emonth);
      dd = Integer.parseInt(eday);
      yy = Integer.parseInt(eyear);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   edate = yy * 10000;                            // create a date field of yyyymmdd
   edate = edate + (mm * 100);
   edate = edate + dd;

   //
   //   Get multi option, member types, and guest types
   //
   multi = parm.multi;

   //
   //   Remove any guest types that are null - for tests below
   //
   for (i = 0; i < parm.MAX_Guests; i++) {

      if (parm.guest[i].equals( "" )) {

         parm.guest[i] = "$@#!^&*";      // make so it won't match player name
      }
   }

   count2 = 1;                  // init to 1 course

   //
   //   Check for multiple courses
   //
   if (multi != 0) {           // if multiple courses supported for this club

      try {

         //
         //  Get the names of all courses for this club
         //
         course = Utilities.getCourseNames(con);     // get all the course names

         count2 = course.size();                      // number of courses

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Proshop Reports Page - Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error:" + exc.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }

   //
   //  Build the HTML page to display search results
   //
   try{
      if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format

         toExcel = true;
         resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
      }
   }
   catch (Exception exc) {
   }

   if (!toExcel) {
       out.println(SystemUtils.HeadTitle("Proshop Reports Page"));
   }
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   if (!toExcel) {
       SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   }
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   if (!toExcel) {     // if normal request
       out.println("<table border=\"0\" align=\"center\">");
       out.println("<tr><td align=\"center\">");

       out.println("<font size=\"3\">");
       out.println("<p><b>Course Statistics</b><br></font><font size=\"2\">");
       out.println("<b>Note:</b> Today's counts are not included. Percentages are rounded down to whole number.<br>");
       out.println("Percentages for Member Types and Membership Types are of 'Rounds by Members'.<br>");
       out.println("Percentages for Guest Types are of 'Rounds by Guests'.  Others are of 'Total Rounds'.</p>");

       out.println("<form method=\"post\" action=\"Proshop_reports\" target=\"_blank\">");
       out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
       out.println("<input type=\"hidden\" name=\"custom2\" value=\"yes\">");
       out.println("<input type=\"hidden\" name=\"smonth\" value=\"" + excelsmonth + "\">");
       out.println("<input type=\"hidden\" name=\"sday\" value=\"" + excelsday + "\">");
       out.println("<input type=\"hidden\" name=\"syear\" value=\"" + excelsyear + "\">");
       out.println("<input type=\"hidden\" name=\"emonth\" value=\"" + excelemonth + "\">");
       out.println("<input type=\"hidden\" name=\"eday\" value=\"" + exceleday + "\">");
       out.println("<input type=\"hidden\" name=\"eyear\" value=\"" + exceleyear + "\">");
       out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</form>");
       out.println("</font>");
   }

   courseName = "";            // init as not multi

   //
   // execute searches and display for each course
   //
   for (index=0; index < count2; index++) {       // count = # of courses (1 if not multi)

      if (multi != 0) {                        // if multiple courses supported for this club

         courseName = course.get(index);      // get course name
      }

      //
      //  init count fields for each course
      //
      for (i = 0; i < parm.MAX_Mems; i++) {
         memxRounds1[i] = 0;
         memxRounds9[i] = 0;
         memxRounds18[i] = 0;
      }

      for (i = 0; i < parm.MAX_Mships; i++) {
         mshipxRounds1[i] = 0;
         mshipxRounds9[i] = 0;
         mshipxRounds18[i] = 0;
      }

      for (i = 0; i < parm.MAX_Guests; i++) {
         gstRnds1[i] = 0;
         gst1Rnds9[i] = 0;
         gst1Rnds18[i] = 0;
      }

      memRounds1 = 0;
      mem9Rounds1 = 0;
      mem18Rounds1 = 0;
      otherRounds1 = 0;
      other9Rounds1 = 0;
      other18Rounds1 = 0;

      gstRounds1 = 0;
      gst9Rounds1 = 0;
      gst18Rounds1 = 0;
      gst9RevRnds1 = 0;
      gst18RevRnds1 = 0;

      mship9Rounds1 = 0;
      mship18Rounds1 = 0;
      totRounds1 = 0;
      nshowRounds1 = 0;
      nshow9Rounds1 = 0;
      nshow18Rounds1 = 0;
      mnshowRounds1 = 0;
      mnshow9Rounds1 = 0;
      mnshow18Rounds1 = 0;
      gnshowRounds1 = 0;
      gnshow9Rounds1 = 0;
      gnshow18Rounds1 = 0;
      memUnknown = 0;
      memUnknown9 = 0;
      memUnknown18 = 0;
      mshipUnknown = 0;
      mshipUnknown9 = 0;
      mshipUnknown18 = 0;

      //
      //  Init the Modes of Trans arrays
      //
      for (i = 0; i < parm.MAX_Tmodes; i++) {
         tmodeR1[i] = 0;
         tmode9R1[i] = 0;
         tmode18R1[i] = 0;
      }

      tmodeOldR9 = 0;
      tmodeOldR18 = 0;
      tmodeOldR91 = 0;
      tmodeOldR181 = 0;

      error = " Stats Table Access Error";

      //
      // use the dates provided to search the stats table
      //
      try {

         //
         //  Get the System Parameters for this Course
         //
         getParms.getCourse(con, parmc, courseName);


         //
         //  Paul - set Custom Date Range counts here as follows (zero values are simply used to prevent errors):
         //
         memUnknown = 0;             // unknown member type - total
         mshipUnknown = 0;           // unknown mship type - total

         mnshowRounds1 = 0;           // Member No-shows - total

         otherRounds1 = 0;           // NOT Guest & NOT Member (no username) - total
         other9Rounds1 = 0;           // NOT Guest & NOT Member (no username) - 9
         other18Rounds1 = 0;           // NOT Guest & NOT Member (no username) - 18

         totRounds1 = 0;            // Grand total of all rounds for this period

         memRounds1 = 0;            // Total Member Rounds
         mem9Rounds1 = 0;           // Total Member Rounds - 9
         mem18Rounds1 = 0;          // Total Member Rounds - 18

         gstRounds1 = 0;            // Total Guest rounds for this period
         gst9Rounds1 = 0;           // Total Guest Rounds - 9
         gst18Rounds1 = 0;          // Total guest Rounds - 18
         gst9RevRnds1 = 0;          // Total Revenue Guest Rounds - 9
         gst18RevRnds1 = 0;         // Total Revenue Guest Rounds - 18

         tmodeOldR91 = 0;           // Total unknown tmodes (old) - 9
         tmodeOldR181 = 0;          // Total unknown tmodes (old) - 18

         //
         //  Init the Modes of Trans arrays
         //
         for (i = 0; i < parm.MAX_Tmodes; i++) {        // arrays for each tmode type counts
            tmodeR1[i] = 0;
         }

         //
         //  Init the Guest arrays
         //
         for (i = 0; i < parm.MAX_Guests; i++) {         // arrays for each guest type counts
            gstRnds1[i] = 0;
         }

         //
         //  Init the Mem Type arrays
         //
         for (i = 0; i < parm.MAX_Mems; i++) {          // arrays for each member type counts
            memxRounds1[i] = 0;
         }

         //
         //  Init the Mship Type arrays
         //
         for (i = 0; i < parm.MAX_Mships; i++) {        // arrays for each mship type counts
            mshipxRounds1[i] = 0;
         }



         int x = 0;

         //
         //  Statement for member type counts
         //
         PreparedStatement pstmt_mtype = con.prepareStatement (
            "SELECT m_type, SUM(p_9), SUM(p_18) FROM (" +
            "SELECT IF(m_type='', IF(null_user=1,'Unknown','Removed from Member Database'), m_type) AS m_type, p_9, p_18 " +
            "FROM ( " +
                "SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, mtype1 AS m_type, IF(IFNULL(username1,'')='',1,0) AS null_user " +
                "FROM teepast2 " +
                "WHERE courseName = ? AND date >= ? AND date <= ? AND show1 = 1 AND (username1 <> '' AND username1 IS NOT NULL) " +
                "UNION ALL " +
                "SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, mtype2 AS m_type, IF(IFNULL(username2,'')='',1,0) AS null_user " +
                "FROM teepast2  " +
                "WHERE courseName = ? AND date >= ? AND date <= ? AND show2 = 1 AND (username2 <> '' AND username2 IS NOT NULL) " +
                "UNION ALL " +
                "SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, mtype3 AS m_type, IF(IFNULL(username3,'')='',1,0) AS null_user " +
                "FROM teepast2 " +
                "WHERE courseName = ? AND date >= ? AND date <= ? AND show3 = 1 AND (username3 <> '' AND username3 IS NOT NULL) " +
                "UNION ALL " +
                "SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, mtype4 AS m_type, IF(IFNULL(username4,'')='',1,0) AS null_user " +
                "FROM teepast2 " +
                "WHERE courseName = ? AND date >= ? AND date <= ? AND show4 = 1 AND (username4 <> '' AND username4 IS NOT NULL) " +
                "UNION ALL " +
                "SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, mtype5 AS m_type, IF(IFNULL(username5,'')='',1,0) AS null_user " +
                "FROM teepast2 " +
                "WHERE courseName = ? AND date >= ? AND date <= ? AND show5 = 1 AND (username5 <> '' AND username5 IS NOT NULL) " +
                "UNION ALL " +
                "SELECT (0) AS p_9, (0) AS p_18, m2b.m_type AS m_type, (1) AS null_user " +
                "FROM member2b m2b " +
                "WHERE m2b.m_type IS NOT NULL " +
                "GROUP BY m2b.m_type " +
            ") AS d_table ) AS g_table GROUP BY m_type WITH ROLLUP;");


         error = "Get Member Type Counts";


         //
         //  Get counts for Member Type
         //
         pstmt_mtype.clearParameters();
         pstmt_mtype.setString(1, courseName);
         pstmt_mtype.setLong(2, sdate);
         pstmt_mtype.setLong(3, edate);

         pstmt_mtype.setString(4, courseName);
         pstmt_mtype.setLong(5, sdate);
         pstmt_mtype.setLong(6, edate);

         pstmt_mtype.setString(7, courseName);
         pstmt_mtype.setLong(8, sdate);
         pstmt_mtype.setLong(9, edate);

         pstmt_mtype.setString(10, courseName);
         pstmt_mtype.setLong(11, sdate);
         pstmt_mtype.setLong(12, edate);

         pstmt_mtype.setString(13, courseName);
         pstmt_mtype.setLong(14, sdate);
         pstmt_mtype.setLong(15, edate);

         rs = pstmt_mtype.executeQuery();

         i = 0;

         while (rs.next()) {

            if (rs.getString(1) == null) {

                // grand total
                mem9Rounds1 = rs.getInt(2);                 // Total Member Rounds - 9
                mem18Rounds1 = rs.getInt(3);                // Total Member Rounds - 18
                memRounds1 = mem9Rounds1 + mem18Rounds1;    // Total Member Rounds

            } else {

                found = false;
                loop1:
                for (x = 0; x < parm.MAX_Mems; x++) {

                    if (parm.mem[x].equals(rs.getString(1))) {

                        memxRounds1[x] = rs.getInt(2) + rs.getInt(3);
                        found = true;
                        break loop1;

                    } else if (parm.mem[x].equals("")) {

                        // found an empty one so we must be at the end of the defined types
                        break loop1;

                    }
                }

                // came across a member type that is no longer defined in the system
                if (!found && x < parm.MAX_Mems) {

                    // lets add it to our array for display
                    parm.mem[x] = rs.getString(1);
                    memxRounds1[x] = rs.getInt(2) + rs.getInt(3);

                }

            }

         } // end of while

         pstmt_mtype.close();


         //
         //  Statement for membership counts
         //
         PreparedStatement pstmt_mship = con.prepareStatement (
               "SELECT IF(m_ship='', 'Removed',m_ship) AS m_ship, SUM(p_9), SUM(p_18) " +
               "FROM ( " +

                   "SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, mship1 AS m_ship " +
	               "FROM teepast2 " +
	               "WHERE courseName = ? AND date >= ? AND date <= ? AND show1 = 1 AND (username1 <> '' AND username1 IS NOT NULL) " +

	               "UNION ALL " +

	               "SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, mship2 AS m_ship " +
	               "FROM teepast2 " +
	               "WHERE courseName = ? AND date >= ? AND date <= ? AND show2 = 1 AND (username2 <> '' AND username2 IS NOT NULL) " +

	               "UNION ALL " +

	               "SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, mship3 AS m_ship " +
	               "FROM teepast2 " +
	               "WHERE courseName = ? AND date >= ? AND date <= ? AND show3 = 1 AND (username3 <> '' AND username3 IS NOT NULL) " +

	               "UNION ALL " +

	               "SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, mship4 AS m_ship " +
	               "FROM teepast2 " +
	               "WHERE courseName = ? AND date >= ? AND date <= ? AND show4 = 1 AND (username4 <> '' AND username4 IS NOT NULL) " +

	               "UNION ALL " +

	               "SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, mship5 AS m_ship " +
	               "FROM teepast2 " +
	               "WHERE courseName = ? AND date >= ? AND date <= ? AND show5 = 1 AND (username5 <> '' AND username5 IS NOT NULL) " +

               ") AS d_table GROUP BY m_ship");

         error = "Get Membership Counts";

         //
         //  Get counts for Membership Type
         //
         pstmt_mship.clearParameters();
         pstmt_mship.setString(1, courseName);
         pstmt_mship.setLong(2, sdate);
         pstmt_mship.setLong(3, edate);

         pstmt_mship.setString(4, courseName);
         pstmt_mship.setLong(5, sdate);
         pstmt_mship.setLong(6, edate);

         pstmt_mship.setString(7, courseName);
         pstmt_mship.setLong(8, sdate);
         pstmt_mship.setLong(9, edate);

         pstmt_mship.setString(10, courseName);
         pstmt_mship.setLong(11, sdate);
         pstmt_mship.setLong(12, edate);

         pstmt_mship.setString(13, courseName);
         pstmt_mship.setLong(14, sdate);
         pstmt_mship.setLong(15, edate);

         rs = pstmt_mship.executeQuery();

         i = 0;

         while (rs.next()) {

             found = false;
             loop1:
             for (x = 0; x < parm.MAX_Mships; x++) {

                 if (parm.mship[x].equals(rs.getString(1))) {

                     mshipxRounds1[x] = rs.getInt(2) + rs.getInt(3);
                     found = true;
                     break loop1;

                 } else if (parm.mship[x].equals("")) {

                     // found an empty one so we must be at the end of the defined types
                     break loop1;

                 }
             }

             // came across a member type that is no longer defined in the system
             if (!found && x < parm.MAX_Mships) {

                 // lets add it to our array for display
                 parm.mship[x] = rs.getString(1);
                 mshipxRounds1[x] = rs.getInt(2) + rs.getInt(3);

             }

         }

         pstmt_mship.close();


         //
         //  Statement for guest type counts
         //
         PreparedStatement pstmt_gtype = con.prepareStatement (
               "SELECT guest_type, SUM(p_9), SUM(p_18), SUM(grev18) AS revenue18, SUM(grev9) AS revenue9 FROM ( " +
               "SELECT IF(guest_type='','Unknown',guest_type) AS guest_type, p_9, p_18, grev18, grev9 " +
               "FROM ( " +

               "SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, gtype1 AS guest_type, IF(p91=0 && grev1=1,1,0) AS grev18, IF(p91=1 && grev1=1,1,0) AS grev9 " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show1 = 1 AND ((username1 = '' OR username1 IS NULL) AND (player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL)) " +

               "UNION ALL " +

               "SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, gtype2 AS guest_type, IF(p92=0 && grev2=1,1,0) AS grev18, IF(p92=1 && grev2=1,1,0) AS grev9 " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show2 = 1 AND ((username2 = '' OR username2 IS NULL) AND (player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL)) " +

               "UNION ALL " +

               "SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, gtype3 AS guest_type, IF(p93=0 && grev3=1,1,0) AS grev18, IF(p93=1 && grev3=1,1,0) AS grev9 " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show3 = 1 AND ((username3 = '' OR username3 IS NULL) AND (player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL)) " +

               "UNION ALL " +

               "SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, gtype4 AS guest_type, IF(p94=0 && grev4=1,1,0) AS grev18, IF(p94=1 && grev4=1,1,0) AS grev9 " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show4 = 1 AND ((username4 = '' OR username4 IS NULL) AND (player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL)) " +

               "UNION ALL " +

               "SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, gtype5 AS guest_type, IF(p95=0 && grev5=1,1,0) AS grev18, IF(p95=1 && grev5=1,1,0) AS grev9 " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show5 = 1 AND ((username5 = '' OR username5 IS NULL) AND (player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL)) " +

               ") AS d_table ) AS g_table GROUP BY guest_type WITH ROLLUP;");

         error = "Get YTD Guest Type Counts";


         //
         //  Get counts for Guest Type
         //
         pstmt_gtype.clearParameters();
         pstmt_gtype.setString(1, courseName);
         pstmt_gtype.setLong(2, sdate);
         pstmt_gtype.setLong(3, edate);

         pstmt_gtype.setString(4, courseName);
         pstmt_gtype.setLong(5, sdate);
         pstmt_gtype.setLong(6, edate);

         pstmt_gtype.setString(7, courseName);
         pstmt_gtype.setLong(8, sdate);
         pstmt_gtype.setLong(9, edate);

         pstmt_gtype.setString(10, courseName);
         pstmt_gtype.setLong(11, sdate);
         pstmt_gtype.setLong(12, edate);

         pstmt_gtype.setString(13, courseName);
         pstmt_gtype.setLong(14, sdate);
         pstmt_gtype.setLong(15, edate);

         rs = pstmt_gtype.executeQuery();

         i = 0;

         while (rs.next()) {

            if (rs.getString(1) == null) {

                // grand total
                gst9Rounds1 = rs.getInt(2);                 // Total Guest Rounds - 9
                gst18Rounds1 = rs.getInt(3);                // Total Guest Rounds - 18
                gstRounds1 = gst9Rounds1 + gst18Rounds1;    // Total Guest Rounds

                gst18RevRnds1 = rs.getInt(4);               // Total Revenue Guest Rounds - 18
                gst9RevRnds1 = rs.getInt(5);                // Total Revenue Guest Rounds - 9

            } else {

                found = false;
                loop1:
                for (x = 0; x < parm.MAX_Guests; x++) {

                    if (parm.guest[x].equals(rs.getString(1))) {

                        gstRnds1[x] = rs.getInt(2) + rs.getInt(3);
                        found = true;
                        break loop1;

                    } else if (parm.guest[x].equals("$@#!^&*")) {

                        // found an empty one so we must be at the end of the defined types
                        break loop1;

                    }
                }

                // came across a member type that is no longer defined in the system
                if (!found && x < parm.MAX_Guests) {

                    // lets add it to our array for display
                    parm.guest[x] = rs.getString(1);
                    gstRnds1[x] = rs.getInt(2) + rs.getInt(3);

                }

            }

         }

         pstmt_gtype.close();


         //
         // Total Rounds Played 9/18
         //
         PreparedStatement pstmt_totals = con.prepareStatement (
         "SELECT SUM(p_9), SUM(p_18) " +
         "FROM ( " +

         "SELECT p91*(show1=1) AS p_9, IF(p91=0,1,0)*(show1=1) AS p_18 " +
         "FROM teepast2 " +
         "WHERE courseName = ? AND date >= ? AND date <= ? AND (player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL) " +

         "UNION ALL " +

         "SELECT p92*(show2=1) AS p_9, IF(p92=0,1,0)*(show2=1) AS p_18 " +
         "FROM teepast2 " +
         "WHERE courseName = ? AND date >= ? AND date <= ? AND (player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL) " +

         "UNION ALL " +

         "SELECT p93*(show3=1) AS p_9, IF(p93=0,1,0)*(show3=1) AS p_18 " +
         "FROM teepast2 " +
         "WHERE courseName = ? AND date >= ? AND date <= ? AND (player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL) " +

         "UNION ALL " +

         "SELECT p94*(show4=1) AS p_9, IF(p94=0,1,0)*(show4=1) AS p_18 " +
         "FROM teepast2 " +
         "WHERE courseName = ? AND date >= ? AND date <= ? AND (player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL) " +

         "UNION ALL " +

         "SELECT p95*(show5=1) AS p_9, IF(p95=0,1,0)*(show5=1) AS p_18 " +
         "FROM teepast2 " +
         "WHERE courseName = ? AND date >= ? AND date <= ? AND (player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL) ) AS d_table");

         //
         //  Get total rounds played
         //
         pstmt_totals.clearParameters();
         pstmt_totals.setString(1, courseName);
         pstmt_totals.setLong(2, sdate);
         pstmt_totals.setLong(3, edate);

         pstmt_totals.setString(4, courseName);
         pstmt_totals.setLong(5, sdate);
         pstmt_totals.setLong(6, edate);

         pstmt_totals.setString(7, courseName);
         pstmt_totals.setLong(8, sdate);
         pstmt_totals.setLong(9, edate);

         pstmt_totals.setString(10, courseName);
         pstmt_totals.setLong(11, sdate);
         pstmt_totals.setLong(12, edate);

         pstmt_totals.setString(13, courseName);
         pstmt_totals.setLong(14, sdate);
         pstmt_totals.setLong(15, edate);

         rs = pstmt_totals.executeQuery();

         i = 0;

         if (rs.next()) {

             // totals
             totRounds1 = rs.getInt(1) + rs.getInt(2);

         }

         pstmt_totals.close();
         
         
         //
         // Total No-Shows 9/18
         //
         PreparedStatement pstmt_noshows = con.prepareStatement ("" +
             "SELECT SUM(p_9ns), SUM(p_18ns) " +

             "FROM ( " +

             "SELECT p91*(show1<>1) AS p_9ns, IF(p91=0,1,0)*(show1<>1) AS p_18ns " +
             "FROM teepast2 " +
             "WHERE courseName = ? AND date >= ? AND date <= ? AND username1 <> '' AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL " +

             "UNION ALL " +

             "SELECT p92*(show2<>1) AS p_9ns, IF(p92=0,1,0)*(show2<>1) AS p_18ns " +
             "FROM teepast2 " +
             "WHERE courseName = ? AND date >= ? AND date <= ? AND username2 <> '' AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL " +

             "UNION ALL " +

             "SELECT p93*(show3<>1) AS p_9ns, IF(p93=0,1,0)*(show3<>1) AS p_18ns " +
             "FROM teepast2 " +
             "WHERE courseName = ? AND date >= ? AND date <= ? AND username3 <> '' AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL " +

             "UNION ALL " +

             "SELECT p94*(show4<>1) AS p_9ns, IF(p94=0,1,0)*(show4<>1) AS p_18ns " +
             "FROM teepast2 " +
             "WHERE courseName = ? AND date >= ? AND date <= ? AND username4 <> '' AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL " +

             "UNION ALL " +

             "SELECT p95*(show5<>1) AS p_9ns, IF(p95=0,1,0)*(show5<>1) AS p_18ns " +
             "FROM teepast2 " +
             "WHERE courseName = ? AND date >= ? AND date <= ? AND username5 <> '' AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL " +

             ") AS d_table");

         //
         //  Get total of member no-shows
         //
         pstmt_noshows.clearParameters();
         pstmt_noshows.setString(1, courseName);
         pstmt_noshows.setLong(2, sdate);
         pstmt_noshows.setLong(3, edate);

         pstmt_noshows.setString(4, courseName);
         pstmt_noshows.setLong(5, sdate);
         pstmt_noshows.setLong(6, edate);

         pstmt_noshows.setString(7, courseName);
         pstmt_noshows.setLong(8, sdate);
         pstmt_noshows.setLong(9, edate);

         pstmt_noshows.setString(10, courseName);
         pstmt_noshows.setLong(11, sdate);
         pstmt_noshows.setLong(12, edate);

         pstmt_noshows.setString(13, courseName);
         pstmt_noshows.setLong(14, sdate);
         pstmt_noshows.setLong(15, edate);

         rs = pstmt_noshows.executeQuery();

         i = 0;

         if (rs.next()) {

             // totals
             mnshowRounds1 = rs.getInt(1) + rs.getInt(2);

         }

         pstmt_noshows.close();



         //
         // Do transportation queries
         //
         PreparedStatement pstmt_tmodes = con.prepareStatement ("" +
           "SELECT IF(tmode='','Unknown', tmode) AS tmode, SUM(p_9), SUM(p_18) " +
               "FROM ( " +

               "SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, p1cw AS tmode " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show1 = 1 " +
                    "AND player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL " +

               "UNION ALL " +

               "SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, p2cw AS tmode " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show2 = 1  " +
                    "AND player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL " +

               "UNION ALL " +

               "SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, p3cw AS tmode " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show3 = 1  " +
                    "AND player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL " +

               "UNION ALL " +

               "SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, p4cw AS tmode " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show4 = 1  " +
                    "AND player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL " +

               "UNION ALL " +

               "SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, p5cw AS tmode " +
               "FROM teepast2 " +
               "WHERE courseName = ? AND date >= ? AND date <= ? AND show5 = 1  " +
                    "AND player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL " +

           ") AS d_table GROUP BY tmode;");


         //
         //  Get YTD counts for tmodes
         //
         pstmt_tmodes.clearParameters();
         pstmt_tmodes.setString(1, courseName);
         pstmt_tmodes.setLong(2, sdate);
         pstmt_tmodes.setLong(3, edate);

         pstmt_tmodes.setString(4, courseName);
         pstmt_tmodes.setLong(5, sdate);
         pstmt_tmodes.setLong(6, edate);

         pstmt_tmodes.setString(7, courseName);
         pstmt_tmodes.setLong(8, sdate);
         pstmt_tmodes.setLong(9, edate);

         pstmt_tmodes.setString(10, courseName);
         pstmt_tmodes.setLong(11, sdate);
         pstmt_tmodes.setLong(12, edate);

         pstmt_tmodes.setString(13, courseName);
         pstmt_tmodes.setLong(14, sdate);
         pstmt_tmodes.setLong(15, edate);

         rs = pstmt_tmodes.executeQuery();

         i = 0;

         while (rs.next()) {

            found = false;
            loop1:
            for (x = 0; x < parm.MAX_Tmodes; x++) {

                if (parmc.tmodea[x].equals(rs.getString(1))) {

                    tmodeR1[x] = rs.getInt(2) + rs.getInt(3);
                    found = true;
                    break loop1;

                } else if (parmc.tmodea[x].equals("")) {

                    // found an empty one so we must be at the end of the defined types
                    break loop1;

                }
            }

            // came across a tmode that is no longer defined in the system
            if (!found && x < parm.MAX_Tmodes) {

                // lets add it to our array for display
                parmc.tmodea[x] = rs.getString(1);
                parmc.tmode[x] = "Legacy Type " + rs.getString(1);
                tmodeR1[x] = rs.getInt(2) + rs.getInt(3);

            }

         }

         pstmt_tmodes.close();

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Exception:" + exc.getMessage());
         out.println("<BR><BR>Error:" + error);
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      //
      //  Build a table for each course
      //

      if (toExcel) {     // if user requested Excel Spreadsheet Format
            out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\">");
      } else {
            out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
      }

      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");


      String bgrndcolor = "#336633";      // default
      String fontcolor = "#FFFFFF";      // default

      if (toExcel) {     // if user requested Excel Spreadsheet Format

         bgrndcolor = "#FFFFFF";      // white for excel
         fontcolor = "#000000";      // black for excel
      }

         //
         // add course name header if multi
         //
         if (!courseName.equals( "" )) {

            out.println("<tr bgcolor=\"" + bgrndcolor + "\"><td colspan=\"2\">");
            out.println("<font color=\"" + fontcolor + "\" size=\"3\">");
            out.println("<p align=\"center\"><b>" + courseName + "</b></p>");
            out.println("</font></td></tr>");
         }

         out.println("<tr bgcolor=\"" + bgrndcolor + "\"><td>");
               out.println("<font color=\"" + fontcolor + "\" size=\"2\">");
               out.println("<p align=\"left\"><b>Stat</b></p>");
               out.println("</font></td>");

            out.println("<td>");
               out.println("<font color=\"" + fontcolor + "\" size=\"2\">");
               out.println("<p align=\"center\"><b>From " + smonth + "/" + sday + "/" + syear + " to");
               out.println(" " + emonth + "/" + eday + "/" + eyear + "</b></p>");
               out.println("</font></td>");

         //
         //  Build the HTML for each stat gathered above
         //
         out.println("</tr><tr>");                       // Grand totals
         out.println("<td align=\"left\">");
            out.println("<font size=\"2\"><br>");
            out.println("<b>Total Rounds Played:</b>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br><b>");
            out.println(totRounds1);
            out.println("</b></font></td>");


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");


         out.println("</tr><tr>");                     // Total Rounds for members
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<p align=\"left\"><b>Rounds by Members:</b></p>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
         if (memRounds1 < 1 || totRounds1 < 1) {
            out.println(memRounds1);
         } else {
            out.println(memRounds1 + " (" + (memRounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</b></font></td>");

         found = false;
         for (i=0; i<parm.MAX_Mems; i++) {      // do all mem types

            if (!parm.mem[i].equals( "" )) {

               out.println("</tr><tr>");                     // Rounds for Member Types
               out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               if (found == false) {
                  out.println("<u>by Member Type</u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  out.println("<br>");
               }
               out.println(parm.mem[i] + ":");
               out.println("</font></td>");

               found = true;

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\"><br>");
               if (memxRounds1[i] < 1 || memRounds1 < 1) {
                  out.println(memxRounds1[i]);
               } else {
                  out.println(memxRounds1[i] + " (" + (memxRounds1[i] * 100)/memRounds1 + "%)");
               }
               out.println("</font></td>");
            }
         }


         //
         //  check for rounds with no member type (member has been deleted from db since round was played)
         //
         if (memUnknown != 0) {

            out.println("</tr><tr>");                     // Rounds for Unknown Member Type
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"right\">Unknown (member no longer in database):</p>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(memUnknown + " (" + (memUnknown * 100)/memRounds1 + "%)");
               out.println("</font></td>");
         }

         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         found = false;
         for (i=0; i<parm.MAX_Mships; i++) {        // do all mship types

            if (!parm.mship[i].equals( "" )) {

               out.println("</tr><tr>");                     // Rounds for Membership Types
               out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               if (found == false) {
                  out.println("<u>by Membership Type</u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                  out.println("<br>");
               }
               out.println(parm.mship[i] + ":");
               out.println("</font></td>");

               found = true;

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\"><br>");
               if (mshipxRounds1[i] < 1 || memRounds1 < 1) {
                  out.println(mshipxRounds1[i]);
               } else {
                  out.println(mshipxRounds1[i] + " (" + (mshipxRounds1[i] * 100)/memRounds1 + "%)");
               }
               out.println("</font></td>");
            }
         }


         //
         //  check for rounds with no membership type (member has been deleted from db since round was played)
         //
         if (mshipUnknown != 0) {

            out.println("</tr><tr>");                     // Rounds for Unknown Membership
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"right\">Unknown (member no longer in database):</p>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(mshipUnknown + " (" + (mshipUnknown * 100)/memRounds1 + "%)");
               out.println("</font></td>");
         }


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");


         out.println("</tr><tr>");                     // 9 Hole Rounds for Members
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("Member 9 Hole Rounds:");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (mem9Rounds1 < 1 || totRounds1 < 1) {
            out.println(mem9Rounds1);
         } else {
            out.println(mem9Rounds1 + " (" + (mem9Rounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</font></td>");

         out.println("</tr><tr>");                     // 18 Hole Rounds for Members
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("Member 18 Hole Rounds:");
            out.println("</font><br></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
         if (mem18Rounds1 < 1 || totRounds1 < 1) {
            out.println(mem18Rounds1);
         } else {
            out.println(mem18Rounds1 + " (" + (mem18Rounds1 * 100)/totRounds1 + "%)");
         }
         out.println("</font><br></td>");


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");


         out.println("</tr><tr>");                      // Total Rounds by Guests
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<p align=\"left\"><b>Rounds by Guests:</b></p>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
         if (gstRounds1 < 1 || totRounds1 < 1) {
            out.println(gstRounds1);
         } else {
            out.println(gstRounds1 + " (" + (gstRounds1 * 100)/totRounds1 + "%)");
         }
            out.println("</b></font></td>");

         for (i = 0; i < parm.MAX_Guests; i++) {          // chack all 36 guest types

            if (!parm.guest[i].equals( "" ) && !parm.guest[i].equals( "$@#!^&*" )) {

               out.println("</tr><tr>");                     // Rounds for all 36 Guest Types
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\">" + parm.guest[i] + ":</p>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (gstRnds1[i] < 1 || gstRounds1 < 1) {
                  out.println(gstRnds1[i]);
               } else {
                  out.println(gstRnds1[i] + " (" + (gstRnds1[i] * 100)/gstRounds1 + "%)");
               }
                  out.println("</font></td>");
            }
         }

         out.println("</tr><tr>");                     // 9 Hole Rounds for Guests
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\"><br>");
            out.println("Guest 9 Hole Rounds:");
            out.println("<br>(Revenue)");
            out.println("<br>(Non-Revenue)");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br>");
            if (gst9Rounds1 < 1 || totRounds1 < 1) {
               out.println(gst9Rounds1);
            } else {
               out.println(gst9Rounds1 + " (" + (gst9Rounds1 * 100)/totRounds1 + "%)");
            }
            out.println("<br>");
            if (gst9Rounds1 < 1 || gst9RevRnds1 < 1) {
               out.println("&nbsp;");
            } else {
               out.println(gst9RevRnds1 + " (" + (gst9RevRnds1 * 100)/gst9Rounds1 + "%)");     // revenue
            }
            //gst9RevRnds1 = gst9Rounds1 - gst9RevRnds1;           // get # of Non-Rev
            int gst9NonRevRnds1 = gst9Rounds1 - gst9RevRnds1;
            out.println("<br>");
            if (gst9Rounds1 < 1 || gst9NonRevRnds1 < 1) {
               out.println("&nbsp;");
            } else {
               out.println(gst9NonRevRnds1 + " (" + (gst9NonRevRnds1 * 100)/gst9Rounds1 + "%)");     // non-revenue
            }
            out.println("</font></td>");

         out.println("</tr><tr>");                     // 18 Hole Rounds for Guests
         out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("Guest 18 Hole Rounds:");
            out.println("<br>(Revenue)");
            out.println("<br>(Non-Revenue)");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (gst18Rounds1 < 1 || totRounds1 < 1) {
               out.println(gst18Rounds1);
            } else {
               out.println(gst18Rounds1 + " (" + (gst18Rounds1 * 100)/totRounds1 + "%)");
            }
            out.println("<br>");
            if (gst18Rounds1 < 1 || gst18RevRnds1 < 1) {
               out.println("&nbsp;");
            } else {
               out.println(gst18RevRnds1 + " (" + (gst18RevRnds1 * 100)/gst18Rounds1 + "%)");     // revenue
            }
            //gst18RevRnds1 = gst18Rounds1 - gst18RevRnds1;           // get # of Non-Rev
            int gst18NonRevRnds1 = gst18Rounds1 - gst18RevRnds1;
            out.println("<br>");
            if (gst18Rounds1 < 1 || gst18NonRevRnds1 < 1) {
               out.println("&nbsp;");
            } else {
               out.println(gst18NonRevRnds1 + " (" + (gst18NonRevRnds1 * 100)/gst18Rounds1 + "%)");     // non-revenue
            }
            out.println("</font></td>");


         out.println("</tr><tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         //
         //  Only display 'Others' if there were some found (non-members, non-guests)
         //
         if (otherRounds1 > 0) {

            out.println("</tr><tr>");                      // Total Rounds by Others
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"left\"><b>Rounds by Others:</b></p>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\"><b>");
            if (otherRounds1 < 1 || totRounds1 < 1) {
               out.println(otherRounds1);
            } else {
               out.println(otherRounds1 + " (" + (otherRounds1 * 100)/totRounds1 + "%)");
            }
               out.println("</b></font></td>");

            out.println("</tr><tr>");                     // 9 Hole Rounds for Others
            out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("Other 9 Hole Rounds:");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\"><br>");
            if (other9Rounds1 < 1 || totRounds1 < 1) {
               out.println(other9Rounds1);
            } else {
               out.println(other9Rounds1 + " (" + (other9Rounds1 * 100)/totRounds1 + "%)");
            }
               out.println("</font></td>");

            out.println("</tr><tr>");                     // 18 Hole Rounds for Others
            out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("Other 18 Hole Rounds:");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if (other18Rounds1 < 1 || totRounds1 < 1) {
               out.println(other18Rounds1);
            } else {
               out.println(other18Rounds1 + " (" + (other18Rounds1 * 100)/totRounds1 + "%)");
            }
               out.println("</font></td>");

            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">&nbsp;");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">&nbsp;");
               out.println("</font></td>");
         }
         out.println("</tr>");


         //
         //  Check all the Transportation Modes - now 16 configurable modes (V4)
         //
         for (i=0; i<parm.MAX_Tmodes; i++) {

            if (tmodeR1[i] > 0) {

               out.println("<tr>");
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"left\"><b>" +parmc.tmode[i]+ " Rounds:</b></p>");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
               if (tmodeR1[i] < 1 || totRounds1 < 1) {
                  out.println(tmodeR1[i]);
               } else {
                  out.println(tmodeR1[i] + " (" + (tmodeR1[i] * 100)/totRounds1 + "%)");
               }
                  out.println("</font></td>");
               out.println("</tr>");
            }
         }

         if ((tmodeOldR91 + tmodeOldR181) > 0) {

            out.println("<tr>");
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"left\"><b>Rounds From Modes No Longer Used:</b></p>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
            if ((tmodeOldR91 + tmodeOldR181) < 1 || totRounds1 < 1) {
               out.println(tmodeOldR91 + tmodeOldR181);
            } else {
               out.println((tmodeOldR91 + tmodeOldR181) + " (" + ((tmodeOldR91 + tmodeOldR181) * 100)/totRounds1 + "%)");
            }
               out.println("</font></td>");
            out.println("</tr>");
         }

         out.println("<tr>");                          // blank row for divider
         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");

         out.println("</tr><tr>");
         out.println("<td align=\"left\">");
            out.println("<font size=\"2\">");
            out.println("<b>Member No-Shows:</b>");
            out.println("<br></font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(mnshowRounds1);
            out.println("<br></font></td>");

      out.println("</font></tr></table><br>");

   }       // end of while Courses - do all courses


   if (!toExcel) {     // if normal request
       out.println("</td></tr></table>");                // end of main page table & column

       out.println("<form method=\"get\" action=\"Proshop_announce\">");
       out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</form></font>");
   }

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");
   out.close();

 }  // end of Custom2


 // *********************************************************
 //  Report Type = No-Shows (noshow=yes)
 // *********************************************************

 private void noShows(HttpServletRequest req, PrintWriter out, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   boolean found = false;

   //
   //  get the session and the club name
   //
   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder
   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   int sess_activity_id = (Integer)sess.getAttribute("activity_id");

    // get today's date
    Calendar cal_date = new GregorianCalendar();
    long year = cal_date.get(Calendar.YEAR);
    long month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
    long day = cal_date.get(Calendar.DAY_OF_MONTH);

    
   //
   //  Prompt user for dates and members
   //
   out.println(SystemUtils.HeadTitle("Proshop No-Show Report Select Dates"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td>");

      out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<b>No-Show Report</b><br>");
      out.println("<br>Select the date range and member name(s) below.<br>");
      out.println("Click on <b>Go</b> to generate the report.");
      out.println("<BR><BR><b>NOTE:</b> To see today's no-shows, specify today's date only.");
      out.println("<BR>Any other date range will only include past times.");
      out.println("</font></td></tr></table><br>");

      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
         out.println("<form action=\"Proshop_reports\" method=\"post\" target=\"bot\">");
         out.println("<input type=\"hidden\" name=\"noshow2\" value=\"yes\">");

            out.println("<tr><td>");
               out.println("<font size=\"2\">");
               out.println("<div id=\"awmobject1\">");        // allow menus to show over this box
               Common_Config.displayStartDate(month, day, year, true, false, out);     // start with today
               out.println("</div><br>");

               out.println("<div id=\"awmobject2\">");        // allow menus to show over this box
               Common_Config.displayEndDate(month, day, year, true, false, out);     // start with today
               out.println("</div><br><br>");

               out.println("&nbsp; Member Name:&nbsp;&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"username\">");
               out.println("<option selected value=\"All Members\">All Members</option>");
               if (sess_activity_id ==0) {
                  out.println("<option value=\"All Guests\">All Guests</option>");
               }

   //
   // Get a list of members
   //
   try {

      stmt = con.createStatement();
      rs = stmt.executeQuery("SELECT username, name_last, name_first, name_mi " +
                             "FROM member2b " +
                             "ORDER BY name_last, name_first, name_mi");

      while ( rs.next() ) {

         found = true;
         out.println("<option value=" + rs.getString("username") + ">" + rs.getString("name_last") + ", " + rs.getString("name_first") + " " + rs.getString("name_mi") + "</option>");

      }

   } catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>Error: " + exc.toString());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { stmt.close(); }
      catch (Exception ignore) {}

   }

   if ( !found ) {          // if no members in db yet

      out.println("</select><br><br>");
      out.println("There are no members in the database at this time.<br><br>");
      out.println("</font>");
      out.println("</td></tr></table>");

   } else {

      out.println("</select><br>");
      out.println("<p align=\"center\"><input type=\"submit\" value=\"Go\"></p>");
      out.println("</td></tr></table>");

   }
   out.println("</font></td></tr></form></table>");         // end of main page table

   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center></font></body></html>");

   out.close();

 }  // end of noShows


 // *********************************************************
 //  Report Type = No-Show (noshow2=yes) - from self (noShows above)
 //  Process request according to the dates and member name
 //  selected (a name or 'All Members')
 // *********************************************************

 private void noShow2(HttpServletRequest req, PrintWriter out, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   PreparedStatement pstmt =  null;
   PreparedStatement pstmt2 = null;

   String user = "";
   String name = "";
   String fname = "";
   String mname = "";
   String lname = "";

   String ampm = "";
   String day = "";
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
   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";
   String sfb = "";
   String courseName = "";
   
   ArrayList<String> course = new ArrayList<String>();      // unlimited courses

   long sdate = 0;
   long edate = 0;

   int mm  = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int count = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   int fb = 0;
   int multi = 0;               // multiple course support
   int index = 0;
   int courses = 0;             // number of courses
   int fives = 0;               // 5-somes

   boolean go = false;
   boolean displayMnums = false;
   
   HttpSession sess = SystemUtils.verifyPro(req, out);          // check for intruder

   String templott = (String)sess.getAttribute("lottery");      // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)sess.getAttribute("activity_id");

   String club = getClub.getClubName(con);

   if (club.equals("loscoyotes")) {
       displayMnums = true;
   }

   // get today's date
   Calendar cal_date = new GregorianCalendar();
   long today = (cal_date.get(Calendar.YEAR) * 10000) + ((cal_date.get(Calendar.MONTH) + 1) * 100) + cal_date.get(Calendar.DAY_OF_MONTH);
    
   //
   // Get the parameters entered
   //
   user = req.getParameter("username");

   String smonth = req.getParameter("smonth");
   String sday = req.getParameter("sday");
   String syear = req.getParameter("syear");

   String emonth = req.getParameter("emonth");
   String eday = req.getParameter("eday");
   String eyear = req.getParameter("eyear");

   //
   //  Convert the string values to int's
   //
   try {
      mm = Integer.parseInt(smonth);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   try {
      dd = Integer.parseInt(sday);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   try {
      yy = Integer.parseInt(syear);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   sdate = yy * 10000;                            // create a date field of yyyymmdd
   sdate = sdate + (mm * 100);
   sdate = sdate + dd;

   try {
      mm = Integer.parseInt(emonth);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   try {
      dd = Integer.parseInt(eday);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   try {
      yy = Integer.parseInt(eyear);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   edate = yy * 10000;                            // create a date field of yyyymmdd
   edate = edate + (mm * 100);
   edate = edate + dd;

   //
   //   Get club parms
   //
   try {

      if (sess_activity_id == 0) {

          //
          //  parm block to hold the club parameters
          //
          parmClub parm = new parmClub(0, con); // golf only report
          getClub.getParms(con, parm);

          multi = parm.multi;

      } else {

          multi = 0;

      }

      courses = 1;                  // init to 1 course

      //
      //   Check for multiple courses
      //
      if (multi != 0) {           // if multiple courses supported for this club

         //
         //  Get the names of all courses for this club
         //
         course = Utilities.getCourseNames(con);     // get all the course names

         courses = course.size();                      // number of courses
         
      }

      pstmt = con.prepareStatement (
            "SELECT name_last, name_first, name_mi " +
            "FROM member2b " +
            "WHERE username = ?");

      if (user.equals( "All Members" ) || user.equals( "All Guests" )) {

         name = user;

      } else {

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, user);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            lname = rs.getString(1);
            fname = rs.getString(2);
            mname = rs.getString(3);

            if (mname.equals( "" )) {

               name = fname + " " + lname;

            } else {

               name = fname + " " + mname + " " + lname;
            }
         }
         pstmt.close();
      }

   } catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Proshop Reports Page - Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error1:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<br><br><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
      
   } finally {
       
      try { rs.close(); }
      catch (Exception ignore) {}

      try { stmt.close(); }
      catch (Exception ignore) {}

      try { pstmt.close(); }
      catch (Exception ignore) {}
       
   }

   //
   //  Build the HTML page to display search results
   //
   out.println(SystemUtils.HeadTitle("Proshop No-Show Report"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");          // main page table
   out.println("<tr><td align=\"center\" valign=\"top\">");

   out.println("<font size=\"3\">");
   out.println("<p><b>No-Show Report for " + name + "</b></p>");
   out.println("</font><font size=\"2\">");
   if (sess_activity_id == 0) {
       out.println("<font size=\"2\"><br>F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;F = Front 9, &nbsp;&nbsp;B = Back 9, &nbsp;&nbsp;O = Other<br><br>");
   }
   courseName = "";            // init as not multi

   //
   // execute searches and display for each course
   //
   for (index=0; index < courses; index++) {       // count = # of courses (1 if not multi)

      if (multi != 0) {                        // if multiple courses supported for this club

         courseName = course.get(index);      // get course name
      }

      try {

         String sql = "";


      if (sess_activity_id == 0) {
         
         if (sdate == today && edate == today) {    // if user wants no-show report for today only

            if (user.equals( "All Members" )) {

                sql = 
                   "SELECT mm, dd, yy, day, hr, min, player1, player2, player3, player4, " +
                       "username1, username2, username3, username4, show1, show2, show3, show4, fb, " +
                       "player5, username5, show5 " +
                   "FROM teecurr2 " +
                   "WHERE ((show1 <> 1 OR show2 <> 1 OR show3 <> 1 OR show4 <> 1 OR show5 <> 1) AND (date >= ? AND date <= ?) AND courseName = ?) " +
                   "ORDER BY time";

            } else if (user.equals( "All Guests" )) {

                sql =
                   "SELECT mm, dd, yy, day, hr, min, player1, player2, player3, player4, " +
                       "username1, username2, username3, username4, show1, show2, show3, show4, fb, " +
                       "player5, username5, show5 " +
                   "FROM teecurr2 " +
                   "WHERE (((player1 <> '' AND username1 = '' AND show1 <> 1) OR (player2 <> '' AND username2 = '' AND show2 <> 1) OR " +
                          "(player3 <> '' AND username3 = '' AND show3 <> 1) OR (player4 <> '' AND username4 = '' AND show4 <> 1) OR " +
                          "(player5 <> '' AND username5 = '' AND show5 <> 1)) AND (date >= ? AND date <= ?) AND courseName = ?) " +
                   "ORDER BY time";

            } else {

                sql =
                   "SELECT mm, dd, yy, day, hr, min, player1, player2, player3, player4, " +
                       "username1, username2, username3, username4, show1, show2, show3, show4, fb, " +
                       "player5, username5, show5 " +
                   "FROM teecurr2 " +
                   "WHERE ((username1 LIKE ? OR username2 LIKE ? OR username3 LIKE ? OR username4 LIKE ? OR username5 LIKE ?) " +
                       "AND (show1 <> 1 OR show2 <> 1 OR show3 <> 1 OR show4 <> 1 OR show5 <> 1) AND (date >= ? AND date <= ?) AND courseName = ?) " +
                   "ORDER BY time";

            }

         } else {     // not today
                    
            if (user.equals( "All Members" )) {

                sql = 
                   "SELECT mm, dd, yy, day, hr, min, player1, player2, player3, player4, " +
                       "username1, username2, username3, username4, show1, show2, show3, show4, fb, " +
                       "player5, username5, show5 " +
                   "FROM teepast2 " +
                   "WHERE ((show1 <> 1 OR show2 <> 1 OR show3 <> 1 OR show4 <> 1 OR show5 <> 1) AND (date >= ? AND date <= ?) AND courseName = ?) " +
                   "ORDER BY date, time";

            } else if (user.equals( "All Guests" )) {

                sql =
                   "SELECT mm, dd, yy, day, hr, min, player1, player2, player3, player4, " +
                       "username1, username2, username3, username4, show1, show2, show3, show4, fb, " +
                       "player5, username5, show5 " +
                   "FROM teepast2 " +
                   "WHERE (((player1 <> '' AND username1 = '' AND show1 <> 1) OR (player2 <> '' AND username2 = '' AND show2 <> 1) OR " +
                          "(player3 <> '' AND username3 = '' AND show3 <> 1) OR (player4 <> '' AND username4 = '' AND show4 <> 1) OR " +
                          "(player5 <> '' AND username5 = '' AND show5 <> 1)) AND (date >= ? AND date <= ?) AND courseName = ?) " +
                   "ORDER BY date, time";

            } else {

                sql =
                   "SELECT mm, dd, yy, day, hr, min, player1, player2, player3, player4, " +
                       "username1, username2, username3, username4, show1, show2, show3, show4, fb, " +
                       "player5, username5, show5 " +
                   "FROM teepast2 " +
                   "WHERE ((username1 LIKE ? OR username2 LIKE ? OR username3 LIKE ? OR username4 LIKE ? OR username5 LIKE ?) " +
                       "AND (show1 <> 1 OR show2 <> 1 OR show3 <> 1 OR show4 <> 1 OR show5 <> 1) AND (date >= ? AND date <= ?) AND courseName = ?) " +
                   "ORDER BY date, time";

            }
         }     // end of if today            
            
      } else {

         sql =
             "SELECT date_time, activity_id, player_name, " +
                "DATE_FORMAT(date_time, '%W') AS day, " +
                "DATE_FORMAT(date_time, '%m') AS mm, " +
                "DATE_FORMAT(date_time, '%d') AS dd, " +
                "DATE_FORMAT(date_time, '%Y') AS yy, " +
                "DATE_FORMAT(date_time, '%H') AS hr, " +
                "DATE_FORMAT(date_time, '%i') AS min " +
             "FROM activity_sheets t1 " +
             "LEFT OUTER JOIN activity_sheets_players t2 ON t1.sheet_id = t2.activity_sheet_id " +
             "WHERE " +
                "DATE_FORMAT(date_time, '%Y%m%d') >= ? AND " +
                "DATE_FORMAT(date_time, '%Y%m%d') <= ? AND " +
                "activity_id IN (" + getActivity.buildInString(sess_activity_id, 1, con) + ") AND " +
                "player_name <> '' AND `show` <> 1 AND " +
                "username " + ((user.equals( "All Members" )) ? " <> '' " : " = ? ") + 
             "ORDER BY date_time, activity_id;";

      }
         if (sess_activity_id == 0) {

             // Golf - first lets see if this course supports 5-somes
             pstmt = con.prepareStatement (
                "SELECT fives " +
                "FROM clubparm2 " +
                "WHERE first_hr != 0 AND courseName = ?");

             pstmt.clearParameters();
             pstmt.setString(1, courseName);
             rs = pstmt.executeQuery();

             if (rs.next()) {

                fives = rs.getInt(1);          // 5-somes
             }
             pstmt.close();


             pstmt = con.prepareStatement ( sql );
         
             if (user.equals( "All Members" ) || user.equals( "All Guests" )) {

                pstmt.clearParameters();
                pstmt.setLong(1, sdate);
                pstmt.setLong(2, edate);
                pstmt.setString(3, courseName);

             } else {

                pstmt.clearParameters();
                pstmt.setString(1, user);
                pstmt.setString(2, user);
                pstmt.setString(3, user);
                pstmt.setString(4, user);
                pstmt.setString(5, user);
                pstmt.setLong(6, sdate);
                pstmt.setLong(7, edate);
                pstmt.setString(8, courseName);

             }

         } else {

             // Activity

             pstmt = con.prepareStatement ( sql );
             pstmt.clearParameters();
             pstmt.setLong(1, sdate);
             pstmt.setLong(2, edate);
             if (!user.equals( "All Members" )) {
                pstmt.setString(3, user);

             }

         } // end if golf or activity


         // execute the prepared statement
         rs = pstmt.executeQuery();

         
         //
         //   build the HTML page for the display
         //
         if (!courseName.equals( "" )) {

            out.println("</font><font size=\"3\">");
            out.println("<br>Course:  " +courseName+ "<br><br>");
            out.println("</font><font size=\"2\">");
         }

         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
         out.println("<tr bgcolor=\"#336633\"><td>");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<p align=\"center\"><u><b>Date</b></u></p>");
            out.println("</font></td>");

         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<p align=\"center\"><u><b>Time</b></u></p>");
            out.println("</font></td>");

         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<p align=\"center\">&nbsp;<u><b>");
            out.println((sess_activity_id == 0) ? "F/B" : "Activity");
            out.println("</b></u>&nbsp;</p></font></td>");

         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<p align=\"center\"><u><b>Player 1</b></u></p>");
            out.println("</font></td>");
     if (sess_activity_id == 0) {
         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<p align=\"center\"><u><b>Player 2</b></u></p>");
            out.println("</font></td>");

         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<p align=\"center\"><u><b>Player 3</b></u></p>");
            out.println("</font></td>");

         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<p align=\"center\"><u><b>Player 4</b></u></p>");
            out.println("</font></td>");

         if (fives != 0) {

            out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<p align=\"center\"><u><b>Player 5</b></u></p>");
            out.println("</font></td>");
         }
     }
         out.println("</tr>");

         //
         //  Get each record and display it
         //
         count = 0;             // number of records found

         int gcount = 0;        // guest no-shows (not used/displayed right now)

         int activity_id = 0;

         while ( rs.next() ) {

            if (sess_activity_id == 0) {

                mm = rs.getInt(1);
                dd = rs.getInt(2);
                yy = rs.getInt(3);
                day = rs.getString(4);
                hr = rs.getInt(5);
                min = rs.getInt(6);
                player1 = rs.getString(7);
                player2 = rs.getString(8);
                player3 = rs.getString(9);
                player4 = rs.getString(10);
                username1 = rs.getString(11);
                username2 = rs.getString(12);
                username3 = rs.getString(13);
                username4 = rs.getString(14);
                show1 = rs.getInt(15);
                show2 = rs.getInt(16);
                show3 = rs.getInt(17);
                show4 = rs.getInt(18);
                fb = rs.getInt(19);
                player5 = rs.getString(20);
                username5 = rs.getString(21);
                show5 = rs.getInt(22);

            } else {

                activity_id = rs.getInt("activity_id");
                player1 = rs.getString("player_name");
                day = rs.getString("day");
                mm = rs.getInt("mm");
                dd = rs.getInt("dd");
                yy = rs.getInt("yy");
                hr = rs.getInt("hr");
                min = rs.getInt("min");
                go = true;  // if there's a record then it's a noshow
            }

            // Reset mNum values
            mNum1 = "";
            mNum2 = "";
            mNum3 = "";
            mNum4 = "";
            mNum5 = "";

        if (sess_activity_id == 0) {

            go = false;                  // reset

            if (user.equals( "All Members" ) || user.equals( "All Guests" )) {

               if (!player1.equals( "" ) &&
                   !player1.equalsIgnoreCase( "x" ) && show1 != 1) {

                  if (!username1.equals("")) {   // if a member

                     go = true;              // indicate at least one player/member is a no-show

                  } else {

                     if (user.equals( "All Guests" )) {

                        gcount++;
                        go = true;
                     }
                  }
               }

               if (!player2.equals( "" ) &&
                   !player2.equalsIgnoreCase( "x" ) && show2 != 1) {

                  if (!username2.equals("")) {   // if a member

                     go = true;              // indicate at least one player/member is a no-show

                  } else {

                     if (user.equals( "All Guests" )) {

                        gcount++;
                        go = true;
                     }
                  }
               }

               if (!player3.equals( "" ) &&
                   !player3.equalsIgnoreCase( "x" ) && show3 != 1) {

                  if (!username3.equals("")) {   // if a member

                     go = true;              // indicate at least one player/member is a no-show

                  } else {

                     if (user.equals( "All Guests" )) {

                        gcount++;
                        go = true;
                     }
                  }
               }

               if (!player4.equals( "" ) &&
                   !player4.equalsIgnoreCase( "x" ) && show4 != 1) {

                  if (!username4.equals("")) {   // if a member

                     go = true;              // indicate at least one player/member is a no-show

                  } else {

                     if (user.equals( "All Guests" )) {

                        gcount++;
                        go = true;
                     }
                  }
               }

               if (!player5.equals( "" ) &&
                   !player5.equalsIgnoreCase( "x" ) && show5 != 1) {

                  if (!username5.equals("")) {   // if a member

                     go = true;              // indicate at least one player/member is a no-show

                  } else {

                     if (user.equals( "All Guests" )) {

                        gcount++;
                        go = true;
                     }
                  }
               }

            } else {

               // just doing one member - check to see if specified member is part of time and did not show
               if ((username1.equals(user) && show1 != 1) || (username2.equals(user) && show2 != 1) ||
                   (username3.equals(user) && show3 != 1) || (username4.equals(user) && show4 != 1) ||
                   (username5.equals(user) && show5 != 1)) {

                  go = true;
               }
            }

         } // end if Golf

            if (go == true) {         // if member no-show found

               count++;

               day = day.substring(0, 3);   // trim to just the first 3 chars

               ampm = " AM";
               if (hr == 12) {
                  ampm = " PM";
               } else if (hr > 12) {
                  ampm = " PM";
                  hr = hr - 12;    // convert to conventional time
               }

               //
               //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
               //
               sfb = "O";       // default Other

               if (fb == 1) {

                  sfb = "B";

               } else if (fb == 0) {

                  sfb = "F";
               }

               if (displayMnums) {

                   String query = "SELECT memNum FROM member2b WHERE username = ?";

                   if (!username1.equals("")) {

                       try {
                           pstmt2 = con.prepareStatement(query);
                           pstmt2.clearParameters();
                           pstmt2.setString(1, username1);

                           rs2 = pstmt2.executeQuery();

                           if (rs2.next()) {
                               mNum1 = " " + rs2.getString("memNum");
                           } else {
                               mNum1 = "";
                           }

                           pstmt2.close();

                       } catch (Exception exc) {
                           mNum1 = "";
                       }
                   }

                   if (!username2.equals("")) {

                       try {
                           pstmt2 = con.prepareStatement(query);
                           pstmt2.clearParameters();
                           pstmt2.setString(1, username2);

                           rs2 = pstmt2.executeQuery();

                           if (rs2.next()) {
                               mNum2 = " " + rs2.getString("memNum");
                           } else {
                               mNum2 = "";
                           }

                           pstmt2.close();

                       } catch (Exception exc) {
                           mNum2 = "";
                       }
                   }

                   if (!username3.equals("")) {

                       try {
                           pstmt2 = con.prepareStatement(query);
                           pstmt2.clearParameters();
                           pstmt2.setString(1, username3);

                           rs2 = pstmt2.executeQuery();

                           if (rs2.next()) {
                               mNum3 = " " + rs2.getString("memNum");
                           } else {
                               mNum3 = "";
                           }

                           pstmt2.close();

                       } catch (Exception exc) {
                           mNum3 = "";
                       }
                   }

                   if (!username4.equals("")) {

                       try {
                           pstmt2 = con.prepareStatement(query);
                           pstmt2.clearParameters();
                           pstmt2.setString(1, username4);

                           rs2 = pstmt2.executeQuery();

                           if (rs2.next()) {
                               mNum4 = " " + rs2.getString("memNum");
                           } else {
                               mNum4 = "";
                           }

                           pstmt2.close();

                       } catch (Exception exc) {
                           mNum4 = "";
                       }
                   }

                   if (!username5.equals("")) {

                       try {
                           pstmt2 = con.prepareStatement(query);
                           pstmt2.clearParameters();
                           pstmt2.setString(1, username5);

                           rs2 = pstmt2.executeQuery();

                           if (rs2.next()) {
                               mNum5 = " " + rs2.getString("memNum");
                           } else {
                               mNum5 = "";
                           }

                           pstmt2.close();
                           
                       } catch (Exception exc) {
                           mNum5 = "";
                       }
                   }
               }

               //
               //  Build the HTML for each record found
               //
               out.println("<tr>");
               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println( day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy );
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println( hr + ":" + Utilities.ensureDoubleDigit(min) + ampm );
               out.println("</font></td>");

                   out.println("<td align=\"center\">");
                   out.println("<font size=\"2\">");
               if (sess_activity_id == 0) {
                   out.println(sfb);
               } else {
                   out.println(getActivity.getActivityName(activity_id, con));
               }
                   out.println("</font></td>");

               out.println("<td bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               if ((!player1.equals( "" )) && (!player1.equalsIgnoreCase( "x" ))) {
                  if (show1 == 1) {                                   // if player is a no-show
                     out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">");
                  } else {
                     out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">");
                  }
               }
               out.println("&nbsp;" + player1 + mNum1);
               out.println("</font></td>");

            if (sess_activity_id == 0) {
               out.println("<td bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               if ((!player2.equals( "" )) && (!player2.equalsIgnoreCase( "x" ))) {
                  if (show2 == 1) {                                   // if player is a no-show
                     out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">");
                  } else {
                     out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">");
                  }
               }
               out.println("&nbsp;" + player2 + mNum2);
               out.println("</font></td>");

               out.println("<td bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               if ((!player3.equals( "" )) && (!player3.equalsIgnoreCase( "x" ))) {
                  if (show3 == 1) {                                   // if player is a no-show
                     out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">");
                  } else {
                     out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">");
                  }
               }
               out.println("&nbsp;" + player3 + mNum3);
               out.println("</font></td>");

               out.println("<td bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               if ((!player4.equals( "" )) && (!player4.equalsIgnoreCase( "x" ))) {
                  if (show4 == 1) {                                   // if player is a no-show
                     out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">");
                  } else {
                     out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">");
                  }
               }
               out.println("&nbsp;" + player4 + mNum4);
               out.println("</font></td>");

               if (fives != 0) {

                  out.println("<td bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  if ((!player5.equals( "" )) && (!player5.equalsIgnoreCase( "x" ))) {
                     if (show5 == 1) {                                   // if player is a no-show
                        out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">");
                     } else {
                        out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">");
                     }
                  }
                  out.println("&nbsp;" + player5 + mNum5);
                  out.println("</font></td>");
               }
            } // end if Golf
               out.println("</tr>");

            }    // end if no-show found (display portion)

         }    // end of while rs

         pstmt.close();

         out.println("</font></table>");

         if (count == 0) {

            out.println("<p align=\"center\">No records found for " + name + ".</p>");

           // out.println("<p align=\"center\">User= " +user+ ", sdate= " +sdate+ ", edate= " +edate+ ", Guests= " +gcount+ ".</p>");  // for testing
         }

         if (count != 0) {

            if (name.equals( "All Members" )) {

               out.println("<br><font size=\"2\">");
               out.println("<p align=\"center\">There were " + count + " " + ((sess_activity_id == 0) ? "tee times" : "reservations") + " with at least one No-Show during this period.</p>");
               out.println("</font>");

            } else if (name.equals( "All Guests" )) {

               out.println("<br><font size=\"2\">");
               out.println("<p align=\"center\">There were " + gcount + " Guest No-Shows during this period.</p>");
               out.println("</font>");

            } else {

               out.println("<br><font size=\"2\">");
               out.println("<p align=\"center\">" + name + " had " + count + " No-Shows during this period.</p>");
               out.println("</font>");
            }
         }

      } catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error2:" + exc.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;

      } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { pstmt.close(); }
         catch (Exception ignore) {}
      }

   }     // end of while courses

   out.println("</td></tr></table>");                // end of main page table
   out.println("</font>");
   out.println("<br><font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");
   out.close();


 }   // end of noShow2


 // *********************************************************
 //  Report Type = Search For Member Tee Times
 // *********************************************************

 private void teeTime(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   //
   //  get the session and the club name
   //
   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

   String club = (String)sess.getAttribute("club");            // get club name
   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)sess.getAttribute("activity_id");

   int thisYear = 0;
   int calYear = 0;
   int firstYear = 0;

   String name = "";
   String sdates = "";
   String edates = "";

   if (req.getParameter("name") != null) {          // if user specified a name to search for (2nd call here)

      name = req.getParameter("name");              // name to search for

      if (!name.equals( "" )) {

          if (sess_activity_id == 0) {

              goTee(req, out, con, name);           // go process tee time search request

          } else {

              getActivityTimes(req, resp, out, con, name);      // go process activity time search request

          }

          return;

      }

   } // end if name passed in

   if (req.getParameter("allmems") != null) {       // if user requested a list of all members (2nd call here)

      if (sess_activity_id == 0) {

          goTee(req, out, con, name);               // go process tee time search request

      } else {

          getActivityTimes(req, resp, out, con, "");            // go process activity time search request

      }

      return;
   }

   String subtee = req.getParameter("subtee");      //  tee time report subtype

   boolean enableAdvAssist = Utilities.enableAdvAssist(req);
   
   if (subtee.equalsIgnoreCase( "cal" )) {          // if calendar year requested

      //
      //  Get current year
      //
      Calendar cal = new GregorianCalendar();       // get todays date
      thisYear = cal.get(Calendar.YEAR);

      if (req.getParameter("calYear") != null) {    // if year already selected

         String temp = req.getParameter("calYear"); // get the year

         calYear = Integer.parseInt(temp);

      } else {

         calYear = thisYear;
      }

      //
      //  Get the oldest tee time available to determine how many years we can go back
      //
      try {

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT MIN(yy) FROM teepast2");

         if (rs.next()) {

            firstYear = rs.getInt(1);

         } else {

            firstYear = calYear;
         }
         stmt.close();

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H2>Database Access Error</H2>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><br>Exception: " + exc.getMessage());
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }

   //
   //  Build the HTML page to prompt Proshop for member name
   //
   out.println(SystemUtils.HeadTitle2("Proshop Reports"));
   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("function cursor() { document.forms['f'].name.focus(); }");
   out.println("function movename(name) {");
   out.println(" document.forms['f'].name.value = name;");            // put name selected into the search form
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");                               // End of script
   out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

    if (sess_activity_id == 0) {

        // GOLF INSTRUCTIONS
        out.println("<p>To locate the past tee times for an individual or group, enter the name,<br>");
        out.println("or any portion of the name, as it may exist on the tee sheets.<br>");
        out.println("This will search for all names that contain the value you enter.<br>");
        out.println("You may also search for Guests or a Member Number if you wish.");

        if (club.equals("wingedfoot")) {
            out.println("<br>You can view a report detailing current quota usage for Guest Retrictions by clicking the 'Guest Quota Report' button.");
        }

    } else {

        // FLXREZ INSTRUCTIONS
        out.println("<p>To locate the past reservations for an individual or group, enter the name,<br>");
        out.println("or any portion of the name, as it may exist on the time sheets.<br>");
        out.println("This will search for all names that contain the value you enter.<br>");
        out.println("You may also search for Guests if you wish.");

    }

    out.println("</p></font>");
    out.println("</td></tr></table>");

    out.println("<form action=\"Proshop_reports\" method=\"post\" target=\"bot\" name=\"f\">");
    out.println("<input type=\"hidden\" name=\"subtee\" value=" + subtee + ">");


   //
   //  If "Custom Date Range" option, display the date range calendars (added for case 1487)
   //
   if (subtee.equalsIgnoreCase( "custom" )) {      // if Custom Date Range - display calendars

       sdates = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0") : "";
       edates = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1") : "";

       // our oldest date variables (how far back calendars go)
       int oldest_mm = 0;
       int oldest_dd = 0;
       int oldest_yy = 0;

       // lookup oldest date in teepast2
       try {
           stmt = con.createStatement();
           rs = stmt.executeQuery("SELECT mm,dd,yy FROM teepast2 ORDER BY date ASC LIMIT 1");

           if (rs.next()) {
               oldest_mm = rs.getInt(1);
               oldest_dd = rs.getInt(2);
               oldest_yy = rs.getInt(3);
           }
           stmt.close();

       } catch (Exception e) {
           displayDatabaseErrMsg("Error looking up oldest teetime.", e.getMessage(), out);
           return;
       }

       // set calendar vars
       Calendar cal_date = new GregorianCalendar();
       int cal_year = cal_date.get(Calendar.YEAR);
       int cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
       int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);

       // include files for dynamic calendars
       out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
       out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");

       //out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

       out.println("<br><br>");

       // output instructions
       out.println("Select the date range below.<br><br>");

       // output table that hold calendars and their related text boxes
       out.println("<table align=center border=0>\n<tr valign=top>\n<td align=center>");
        out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
        out.println(" <input type=text name=cal_box_0 id=cal_box_0 value=\"" +sdates+ "\">");
        out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td align=center>");
        out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
        out.println(" <input type=text name=cal_box_1 id=cal_box_1 value=\"" +edates+ "\">");
       out.println("</td>\n</tr></table>\n");


       // start calendar javascript setup code
       out.println("<script type=\"text/javascript\">");

        out.println("var g_cal_bg_color = '#F5F5DC';");
        out.println("var g_cal_header_color = '#8B8970';");
        out.println("var g_cal_border_color = '#8B8970';");

        out.println("var g_cal_count = 2;"); // number of calendars on this page
        out.println("var g_cal_year = new Array(g_cal_count - 1);");
        out.println("var g_cal_month = new Array(g_cal_count - 1);");
        out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);");
        out.println("var g_cal_ending_month = new Array(g_cal_count - 1);");
        out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);");
        out.println("var g_cal_ending_day = new Array(g_cal_count - 1);");
        out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);");
        out.println("var g_cal_ending_year = new Array(g_cal_count - 1);");


        // set calendar date parts in js
        out.println("g_cal_month[0] = " + cal_month + ";");
        out.println("g_cal_year[0] = " + cal_year + ";");
        out.println("g_cal_beginning_month[0] = " + oldest_mm + ";");
        out.println("g_cal_beginning_year[0] = " + oldest_yy + ";");
        out.println("g_cal_beginning_day[0] = " + oldest_dd + ";");
        out.println("g_cal_ending_month[0] = " + cal_month + ";");
        out.println("g_cal_ending_day[0] = " + cal_day + ";");
        out.println("g_cal_ending_year[0] = " + cal_year + ";");

        out.println("g_cal_month[1] = " + cal_month + ";");
        out.println("g_cal_year[1] = " + cal_year + ";");
        out.println("g_cal_beginning_month[1] = " + oldest_mm + ";");
        out.println("g_cal_beginning_year[1] = " + oldest_yy + ";");
        out.println("g_cal_beginning_day[1] = " + oldest_dd + ";");
        out.println("g_cal_ending_month[1] = " + cal_month + ";");
        out.println("g_cal_ending_day[1] = " + cal_day + ";");
        out.println("g_cal_ending_year[1] = " + cal_year + ";");

        out.println("function sd(pCal, pMonth, pDay, pYear) {");
        out.println(" var f = document.getElementById(\"cal_box_\"+pCal);");
        out.println(" f.value = pYear + \"-\" + pMonth + \"-\" + pDay;");
        out.println("}");

       out.println("</script>");

       out.println("<script type=\"text/javascript\">\n doCalendar('0');\n doCalendar('1');\n</script>");

   }   // end of IF custom date range


   out.println("<font size=\"2\" face=\"Courier New\">");
   out.println("<p align=\"center\">(Click on <b>'Member List'</b> on right to view a list of members)</p>");
   out.println("<br></font>");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr>");
         out.println("<td valign=\"top\" align=\"center\">");

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\">");
               out.println("<tr><td width=\"320\" align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>");
                  if (sess_activity_id == 0) {
                     out.println("Search Type:&nbsp;&nbsp;&nbsp;");
                     out.println("<input type=radio name=name_type value=\"name\" checked>&nbsp;&nbsp;Member Name or Guest&nbsp;&nbsp;&nbsp;<br>");
                     out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=radio name=name_type value=\"mnum\">&nbsp;&nbsp;Member Number");
                     out.println("<br><br>Name or Member Number: &nbsp;");
                  } else {
                     out.println("<br><br>Member Name or Guest: &nbsp;");
                  }
                  out.println("<input type=\"text\" name=\"name\" size=\"20\" maxlength=\"40\">");
                  out.println("");
                  out.println("<br><br>");
                  out.println("<input type=\"submit\" value=\"Search\" name=\"search\">");
                  out.println("</p>");
                  out.println("</font>");
               out.println("</td></tr>");
            out.println("</table>");

            // add a button to list all members tee times
            //
            out.println("<br><br>");
            out.println("<input type=\"submit\" value=\"List All Members\" name=\"allmems\">");

            //
            // Add link to custom report for Winged Foot - Case# 1250
            //
            if (club.equals("wingedfoot") && sess_activity_id == 0) {

                out.println("<p><input type=\"submit\" value=\"Guest Quota Report For Selected Member \" name=\"gquota\" style=\"text-decoration:underline; background:#F5F5DC\"></p>");
                out.println("<p><input type=\"submit\" value=\"Guest Quota Report For All Members (Takes Several Minutes!)\" name=\"gquotaall\" style=\"text-decoration:underline; background:#F5F5DC\"></p>");
            }

            if (subtee.equalsIgnoreCase( "cal" ) && firstYear < thisYear) {  // if calendar year

               //
               //   Present a drop-down list of years to choose from
               //
               out.println("<br><br>");
               out.println("<b>Year:</b>&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"calYear\">");

               while (firstYear <= thisYear) {

                  if (firstYear == calYear) {
                     out.println("<option selected value=\"" + firstYear + "\">" + firstYear + "</option>");
                  } else {
                     out.println("<option value=\"" + firstYear + "\">" + firstYear + "</option>");
                  }
                  firstYear++;
               }
               out.println("</select>");
            }
         out.println("</td>");

   if (req.getParameter("letter") != null) {     // if user clicked on a name letter

      String letter = req.getParameter("letter");      // get the letter
      letter = letter + "%";

      String first = "";
      String mid = "";
      String last = "";
      name = "";
      String wname = "";
      String dname = "";

         out.println("<td valign=\"top\" align=\"center\">");
         out.println("<table border=\"1\" width=\"140\" bgcolor=\"#F5F5DC\">");      // name list
         out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Name List</b>");
               out.println("</font></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("Click on name to add");
            out.println("</font></td></tr>");

         try {

            PreparedStatement stmt2 = con.prepareStatement (
                     "SELECT name_last, name_first, name_mi FROM member2b " +
                     "WHERE name_last LIKE ? ORDER BY name_last, name_first, name_mi");

            stmt2.clearParameters();               // clear the parms
            stmt2.setString(1, letter);            // put the parm in stmt
            rs = stmt2.executeQuery();             // execute the prepared stmt

            out.println("<tr><td align=\"left\"><font size=\"2\">");
            out.println("<select size=\"8\" name=\"bname\" " + ((enableAdvAssist) ? "onclick" : "onchange") + "=\"movename(this.form.bname.value)\">");

            while(rs.next()) {

               last = rs.getString(1);
               first = rs.getString(2);
               mid = rs.getString(3);

               if (mid.equals("")) {

                  name = first + " " + last;
                  dname = last + ", " + first;
               } else {

                  name = first + " " + mid + " " + last;
                  dname = last + ", " + first + " " + mid;
               }

               out.println("<option value=\"" + name + "\">" + dname + "</option>");
            }

            out.println("</select>");
            out.println("</font></td></tr>");

            stmt2.close();
         }
         catch (Exception ignore) {

         }
         out.println("</table>");

         out.println("</td>");   // end of name list column

   } else {

      out.println("<td valign=\"top\" width=\"30\">");
      out.println("&nbsp;");
      out.println("</td>");   // end of empty column

   }  // end of if Letter

         out.println("<td valign=\"top\" align=\"center\">");
            out.println("<table border=\"2\" align=\"center\" bgcolor=\"#F5F5DC\">");
               out.println("<tr>");
                  out.println("<td colspan=\"6\" align=\"center\" bgcolor=\"#336633\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<b>Member List</b>");
                     out.println("</font>");
                  out.println("</td>");
               out.println("</tr>");
               out.println("<tr>");
                  out.println("<td colspan=\"6\" align=\"center\">");
                     out.println("<font size=\"2\">Name begins with:");
                     out.println("</font>");
                  out.println("</td>");
               out.println("</tr>");
               out.println("<tr>");
                  out.println("<td align=\"center\"><font size=\"1\">");
                     out.println("<input type=\"submit\" value=\"A\" name=\"letter\"></font></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"B\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"C\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"D\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"E\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"F\" name=\"letter\"></td>");
               out.println("</tr>");

               out.println("<tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"G\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"H\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"I\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"J\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"K\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"L\" name=\"letter\"></td>");
               out.println("</tr>");

               out.println("<tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"M\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"N\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"O\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"P\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"Q\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"R\" name=\"letter\"></td>");
               out.println("</tr>");

               out.println("<tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"S\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"T\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"U\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"V\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"W\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"X\" name=\"letter\"></td>");
               out.println("</tr>");

               out.println("<tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"Y\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"Z\" name=\"letter\"></td>");
                  out.println("<td align=\"center\"></td>");
                  out.println("<td align=\"center\"></td>");
                  out.println("<td align=\"center\"></td>");
                  out.println("<td align=\"center\"></td>");
               out.println("</tr>");
            out.println("</table>");
         out.println("</td>");
      out.println("</tr>");
      out.println("</table>");
      out.println("</select></font>");
      out.println("</td></tr></table>");
      out.println("</form>");

      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

    out.println("</center></font></body></html>");
    out.close();
   //
   //  exit and wait for return with selected name or a letter
   //
 }


 // *********************************************************
 //  Report Type = Past Tee Times (2nd request - from above)
 // *********************************************************

 private void goTee(HttpServletRequest req, PrintWriter out, Connection con, String name) {


   PreparedStatement pstmt = null;
   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   //
   //  get the session and the club name
   //
   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

   String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   String sday = "";
   String ampm = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";
   String course = "";
   String memName = "";
   String username = "";
   String lname = "";
   String fname = "";
   String mname = "";
   String notes = "";
   String name_type = "";

   long date = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   int fb = 0;
   int count = 0;
   int count2 = 0;
   int multi = 0;
   int fives = 0;
   int fiveSomes = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;

   long sdate = 20020101;       // default date = 01/01/2002 (forever)
   long edate = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int length = 0;                    // length of name requested
   int calYear = 0;
   int start_year = 0;
   int start_month = 0;
   int start_day = 0;
   int end_year = 0;
   int end_month = 0;
   int end_day = 0;


   String subtee = req.getParameter("subtee");         //  tee time report subtype (cal, year, forever)

   if (req.getParameter("name_type") != null) {

      name_type = req.getParameter("name_type");         //  name entered is a member name or a member number
   }
   
   
         
   if (subtee.equalsIgnoreCase( "custom" )) {                //  if for Custom Date Range

       String start_date = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0") : "";
       String end_date = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1") : "";

       // make sure the dates here are valid, if not redisplay the calendars
       try {

           int dash1 = start_date.indexOf("-");
           int dash2 = start_date.indexOf("-", dash1 + 1);
           start_year = Integer.parseInt(start_date.substring(0, 4));
           start_month = Integer.parseInt(start_date.substring(dash1 + 1, dash2));
           start_day = Integer.parseInt(start_date.substring(dash2 + 1));

           dash1 = end_date.indexOf("-");
           dash2 = end_date.indexOf("-", dash1 + 1);
           end_year = Integer.parseInt(end_date.substring(0, 4));
           end_month = Integer.parseInt(end_date.substring(dash1 + 1, dash2));
           end_day = Integer.parseInt(end_date.substring(dash2 + 1));

       } catch (Exception e) {
           // invalid dates here, bailout and call form again
           invDate(out);
           return;
       }

       // build our date variables for use in query
       sdate = start_year * 10000;                    // create a date field of yyyymmdd
       sdate = sdate + (start_month * 100);
       sdate = sdate + start_day;

       edate = end_year * 10000;                      // create a date field of yyyymmdd
       edate = edate + end_month * 100;
       edate = edate + end_day;

       if (sdate > edate) {
           // start date is after the end date, jump out and call form again
           invDate(out);
           return;
       }

   } else {

      //
      //  Get today's date and use it for the end date
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      year = cal.get(Calendar.YEAR);
      month = cal.get(Calendar.MONTH);
      day = cal.get(Calendar.DAY_OF_MONTH);

      month = month + 1;                           // month starts at zero

      edate = year * 10000;                   // create a edate field of yyyymmdd
      edate = edate + (month * 100);
      edate = edate + day;                         // date = yyyymmdd (for comparisons)

      if (subtee.equals( "cal" )) {                //  if for calendar year

         if (req.getParameter("calYear") != null) {        // if year already selected

            String temp = req.getParameter("calYear");     // get the year

            calYear = Integer.parseInt(temp);

         } else {

            calYear = year;
         }

         sdate = (calYear * 10000) + 0101;         // sdate = 01/01/yyyy
         edate = (calYear * 10000) + 1231;         // edate = 12/31/yyyy

      } else {

         if (subtee.equals( "year" )) {            //  if for past 12 months

            sdate = year - 1;
            sdate = sdate * 10000;
            sdate = sdate + (month * 100);
            sdate = sdate + day;                   // sdate = yyyymmdd (yyyy is 1 yr ago)
         }
      }        // else use default (forever)
   }

   try {

      //
      //  Check if call is to list all members
      //
      if (req.getParameter("allmems") != null) {

         //
         //  Build the HTML page to display search results
         //
         out.println(SystemUtils.HeadTitle("Proshop Reports Page"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
         out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr><td align=\"center\">");

         out.println("<font size=\"2\">");
         out.println("<p>Tee Times for <b>All Members</b>");

         if (subtee.equals( "cal" )) {                //  if for calendar year

            out.println("<br>For the year <b>" +calYear+ "</b>");
         }
         if (subtee.equalsIgnoreCase( "custom" )) {                //  if for custom date range

            out.println("<br>Date Range: <b>" +start_month+ "/" +start_day+ "/" +start_year+ " to " +end_month+ "/" +end_day+ "/" +end_year+ "</b>");
         }
         out.println("</p></font>");

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
               out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<tr bgcolor=\"#336633\"><td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>Name</b></u></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b># of Tee Times</b></u></p>");
                     out.println("</font>");
                  out.println("</td>");

                  out.println("<td>");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<p align=\"center\"><u><b>Times Checked In</b></u></p>");
                     out.println("</font>");
                  out.println("</td>");
               out.println("</tr>");

         //
         //  Get each member and count the number of tee times
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT username, name_last, name_first, name_mi FROM member2b ORDER BY name_last, name_first");

         while (rs.next()) {

            username = rs.getString(1);
            lname = rs.getString(2);
            fname = rs.getString(3);
            mname = rs.getString(4);

            // Get the member's full name.......

            StringBuffer mem_name = new StringBuffer(lname);  // get last name

            mem_name.append(", " + fname);                     // first name

            if (!mname.equals( "" )) {
               mem_name.append(" " +mname);                        // mi
            }
            memName = mem_name.toString();                    // convert to one string

            //
            //  Count the number of tee times for this member
            //
            pstmt = con.prepareStatement (
               "SELECT username1, username2, username3, username4, " +
               "show1, show2, show3, show4, username5, show5 " +
               "FROM teepast2 " +
               "WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) " +
               "AND (date >= ? AND date <= ?)");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, username);
            pstmt.setString(2, username);
            pstmt.setString(3, username);
            pstmt.setString(4, username);
            pstmt.setString(5, username);
            pstmt.setLong(6, sdate);
            pstmt.setLong(7, edate);
            rs2 = pstmt.executeQuery();      // execute the prepared stmt

            count = 0;
            count2 = 0;

            while (rs2.next()) {

               user1 = rs2.getString(1);
               user2 = rs2.getString(2);
               user3 = rs2.getString(3);
               user4 = rs2.getString(4);
               show1 = rs2.getInt(5);
               show2 = rs2.getInt(6);
               show3 = rs2.getInt(7);
               show4 = rs2.getInt(8);
               user5 = rs2.getString(9);
               show5 = rs2.getInt(10);

               count++;                                          // bump number of tee times for this member

               if (user1.equalsIgnoreCase( username ) && show1 == 1) {     // if member checked in

                  count2++;                                      // bump checked in count
               }
               if (user2.equalsIgnoreCase( username ) && show2 == 1) {

                  count2++;
               }
               if (user3.equalsIgnoreCase( username ) && show3 == 1) {

                  count2++;
               }
               if (user4.equalsIgnoreCase( username ) && show4 == 1) {

                  count2++;
               }
               if (user5.equalsIgnoreCase( username ) && show5 == 1) {

                  count2++;
               }

            }

            pstmt.close();

            //
            //  Build the HTML for each record found
            //
            out.println("<tr>");
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println( memName );
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println( count );
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println( count2 );
               out.println("</font></td>");
            out.println("</tr>");

         }    // end of while more members

         stmt.close();

         out.println("</font></table>");
         out.println("</td></tr></table>");                // end of main page table & column

      } else {  // call is for one member or a member number

         //
         //  verify the required fields
         //
         length = name.length();                    // get length of name requested

         if ((name.equals( "" )) || (length > 43)) {

            invData(out);    // inform the user and return
            return;
         }

         //
         //   Add a % to the name provided so search will match anything close
         //
         String sname = name;
         
         if (name_type.equals("name")) {      // if name specified (not mNum)
            
            StringBuffer buf = new StringBuffer("%");
            buf.append( name );
            buf.append("%");
            sname = buf.toString();
         }

         //
         //   See if multiple courses or 5-somes are supported
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT multi " +
                                "FROM club5 WHERE clubName != ''");

         if (rs.next()) {

            multi = rs.getInt(1);
         }
         stmt.close();

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT fives FROM clubparm2 WHERE first_hr != 0");

         while (rs.next()) {

            fiveSomes = rs.getInt(1);

            if (fiveSomes != 0) {

               fives = 1;      // 5-somes supported on at least one course
            }
         }
         stmt.close();

         //
         // use the name and dates provided to search table
         //
         String whereClause = "player1 LIKE ? OR player2 LIKE ? OR player3 LIKE ? OR player4 LIKE ? OR player5 LIKE ?"; 
         
         if (name_type.equals("mnum")) {      // if mNum entered for search
            
            whereClause = "mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?"; 
         }
         
         pstmt = con.prepareStatement (
            "SELECT date, mm, dd, yy, day, hr, min, time, player1, player2, player3, player4, " +
            "p1cw, p2cw, p3cw, p4cw, show1, show2, show3, show4, fb, " +
            "player5, p5cw, show5, notes, courseName, p91, p92, p93, p94, p95, " +
            "mNum1, mNum2, mNum3, mNum4, mNum5 " +
            "FROM teepast2 " +
            "WHERE (" +whereClause+ ") " +
            "AND (date >= ? AND date <= ?) " +
            "ORDER BY date, time");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, sname);
         pstmt.setString(2, sname);
         pstmt.setString(3, sname);
         pstmt.setString(4, sname);
         pstmt.setString(5, sname);
         pstmt.setLong(6, sdate);
         pstmt.setLong(7, edate);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         //
         //  Build the HTML page to display search results
         //
         out.println(SystemUtils.HeadTitle("Proshop Reports Page"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
         out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr><td align=\"center\">");

         out.println("<font size=\"3\">");
         out.println("<p>Tee Times Located for <b>" + name + "</b>");
         if (subtee.equalsIgnoreCase( "custom" )) {                //  if for custom date range

            out.println("<br>Date Range: <b>" +start_month+ "/" +start_day+ "/" +start_year+ " to " +end_month+ "/" +end_day+ "/" +end_year+ "</b>");
         }
         out.println("</p></font>");

         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<tr bgcolor=\"#336633\"><td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Date</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Time</b></u></p>");
                  out.println("</font></td>");

               if (multi != 0) {

                  out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Course</b></u></p>");
                  out.println("</font></td>");
               }

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>F/B</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Player 1</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>C/W</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Player 2</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>C/W</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Player 3</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>C/W</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Player 4</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>C/W</b></u></p>");
                  out.println("</font></td>");

            if (fives !=0) {
               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Player 5</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>C/W</b></u></p>");
                  out.println("</font></td>");
            }

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>N</b></u></p>");
                  out.println("</font></td>");

               out.println("</tr>");

         count = 0;
         //
         //  Get each record and display it
         //
         while (rs.next()) {

            date = rs.getLong(1);
            mm = rs.getInt(2);
            dd = rs.getInt(3);
            yy = rs.getInt(4);
            sday = rs.getString(5);
            hr = rs.getInt(6);
            min = rs.getInt(7);
            time = rs.getInt(8);
            player1 = rs.getString(9);
            player2 = rs.getString(10);
            player3 = rs.getString(11);
            player4 = rs.getString(12);
            p1cw = rs.getString(13);
            p2cw = rs.getString(14);
            p3cw = rs.getString(15);
            p4cw = rs.getString(16);
            show1 = rs.getInt(17);
            show2 = rs.getInt(18);
            show3 = rs.getInt(19);
            show4 = rs.getInt(20);
            fb = rs.getInt(21);
            player5 = rs.getString(22);
            p5cw = rs.getString(23);
            show5 = rs.getInt(24);
            notes = rs.getString(25);
            course = rs.getString(26);
            p91 = rs.getInt(27);
            p92 = rs.getInt(28);
            p93 = rs.getInt(29);
            p94 = rs.getInt(30);
            p95 = rs.getInt(31);
            mNum1 = rs.getString("mNum1");
            mNum2 = rs.getString("mNum2");
            mNum3 = rs.getString("mNum3");
            mNum4 = rs.getString("mNum4");
            mNum5 = rs.getString("mNum5");

            if (p91 == 1) p1cw += "9";
            if (p92 == 1) p2cw += "9";
            if (p93 == 1) p3cw += "9";
            if (p94 == 1) p4cw += "9";
            if (p95 == 1) p5cw += "9";

            ampm = " AM";
            if (hr == 12) {
               ampm = " PM";
            }
            if (hr > 12) {
               ampm = " PM";
               hr = hr - 12;    // convert to conventional time
            }

            if (player1.equals( "" ) || player1.equalsIgnoreCase( "X" )) {

               player1 = "-";       // change it for table display
               p1cw = "-";
            }
            if (player2.equals( "" ) || player2.equalsIgnoreCase( "X" )) {

               player2 = "-";       // change it for table display
               p2cw = "-";
            }
            if (player3.equals( "" ) || player3.equalsIgnoreCase( "X" )) {

               player3 = "-";       // change it for table display
               p3cw = "-";
            }
            if (player4.equals( "" ) || player4.equalsIgnoreCase( "X" )) {

               player4 = "-";       // change it for table display
               p4cw = "-";
            }
            if (player5.equals( "" ) || player5.equalsIgnoreCase( "X" )) {

               player5 = "-";       // change it for table display
               p5cw = "-";
            }

            if (course.equals( "" )) {

               course = " ";       // make it a spece for table display
            }

            if (sday.equalsIgnoreCase( "sunday" )) {

               sday = "Sun";
            }
            if (sday.equalsIgnoreCase( "monday" )) {

               sday = "Mon";
            }
            if (sday.equalsIgnoreCase( "tuesday" )) {

               sday = "Tue";
            }
            if (sday.equalsIgnoreCase( "wednesday" )) {

               sday = "Wed";
            }
            if (sday.equalsIgnoreCase( "thursday" )) {

               sday = "Thu";
            }
            if (sday.equalsIgnoreCase( "friday" )) {

               sday = "Fri";
            }
            if (sday.equalsIgnoreCase( "saturday" )) {

               sday = "Sat";
            }

            //
            //  Build the HTML for each record found
            //
            out.println("<tr>");
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println( sday + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy );
               out.println("</font></td>");

            out.println("<td align=\"center\" nowrap>");
               out.println("<font size=\"2\">");
            if (min < 10) {
               out.println(hr + ":0" + min + ampm);
            } else {
               out.println(hr + ":" + min + ampm);
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
            if (fb == 0) {
               out.println("F");
            } else {
               out.println("B");
            }
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (!player1.equals( "-")) {
               if (show1 == 1) {                                   // if player has not checked in yet
                  out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">&nbsp;&nbsp;");
               } else {
                  out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">&nbsp;&nbsp;");
               }
            }
            if (player1.equals( name ) || mNum1.equals( name )) {
               out.println("<b>" + player1 + "</b>");
            } else {
               out.println( player1 );
            }
            out.println("</font></td>");

            out.println("<td align=\"center\" bgcolor=\"#FFFFFF\">");
            out.println("<font size=\"2\">");

            if (player1.equals( name )) {
               out.println("<b>" + p1cw + "</b>");
            } else {
               out.println( p1cw );
            }

            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (!player2.equals( "-")) {
               if (show2 == 1) {                                   // if player has not checked in yet
                  out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">&nbsp;&nbsp;");
               } else {
                  out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">&nbsp;&nbsp;");
               }
            }
            if (player2.equals( name ) || mNum2.equals( name )) {
               out.println("<b>" + player2 + "</b>");
            } else {
               out.println( player2 );
            }
            out.println("</font></td>");

            out.println("<td align=\"center\" bgcolor=\"#FFFFFF\">");
            out.println("<font size=\"2\">");
            if (player2.equals( name )) {
               out.println("<b>" + p2cw + "</b>");
            } else {
               out.println( p2cw );
            }
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (!player3.equals( "-")) {
               if (show3 == 1) {                                   // if player has not checked in yet
                  out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">&nbsp;&nbsp;");
               } else {
                  out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">&nbsp;&nbsp;");
               }
            }
            if (player3.equals( name ) || mNum3.equals( name )) {
               out.println("<b>" + player3 + "</b>");
            } else {
               out.println( player3 );
            }
            out.println("</font></td>");

            out.println("<td align=\"center\" bgcolor=\"#FFFFFF\">");
            out.println("<font size=\"2\">");
            if (player3.equals( name )) {
               out.println("<b>" + p3cw + "</b>");
            } else {
               out.println( p3cw );
            }
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (!player4.equals( "-")) {
               if (show4 == 1) {                                   // if player has not checked in yet
                  out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">&nbsp;&nbsp;");
               } else {
                  out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">&nbsp;&nbsp;");
               }
            }
            if (player4.equals( name ) || mNum4.equals( name )) {
               out.println("<b>" + player4 + "</b>");
            } else {
               out.println( player4 );
            }
            out.println("</font></td>");

            out.println("<td align=\"center\" bgcolor=\"#FFFFFF\">");
            out.println("<font size=\"2\">");
            if (player4.equals( name )) {
               out.println("<b>" + p4cw + "</b>");
            } else {
               out.println( p4cw );
            }
            out.println("</font></td>");

            if (fives != 0) {

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               if (!player5.equals( "-")) {
                  if (show5 == 1) {                                   // if player has not checked in yet
                     out.println("<img src=\"/" +rev+ "/images/xbox.gif\" border=\"1\">&nbsp;&nbsp;");
                  } else {
                     out.println("<img src=\"/" +rev+ "/images/mtbox.gif\" border=\"1\">&nbsp;&nbsp;");
                  }
               }
               if (player5.equals( name ) || mNum5.equals( name )) {
                  out.println("<b>" + player5 + "</b>");
               } else {
                  out.println( player5 );
               }
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"#FFFFFF\">");
               out.println("<font size=\"2\">");
               if (player5.equals( name )) {
                  out.println("<b>" + p5cw + "</b>");
               } else {
                  out.println( p5cw );
               }
               out.println("</font></td>");
            }

            //
            //  Last column for 'Notes' box
            //
            if (!notes.equals("")) {

               out.println("<form method=\"post\" action=\"Proshop_reports\" target=\"_blank\">");
               out.println("<td bgcolor=\"white\" align=\"center\">");
               out.println("<font size=\"2\">");

               out.println("<input type=\"hidden\" name=\"notes\" value=\"yes\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"image\" src=\"/" +rev+ "/images/notes.jpg\" border=\"0\" name=\"showNotes\" title=\"Click here to view notes.\">");

               out.println("</font></td></form>");         // end of the col

            } else {
               out.println("<td bgcolor=\"white\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;");
               out.println("</font></td>");         // end of the col
            }

            out.println("</tr>");

            count += 1;

         }    // end of while

         pstmt.close();

            out.println("</font></table>");
         out.println("</td></tr></table>");                // end of main page table & column
         out.println("<font size=\"2\"><BR>");
         out.println("<p><b>" + name + "</b> had a total of <b>" + count + "</b> tee times during the specified period.</p>");
         out.println("</td>");
         out.println("</font>");
      }

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
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<br><br><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
   }

   out.close();

 }


 // *********************************************************
 //  Report Type = Notifications (proshop vs members)
 // *********************************************************

 private void goNotifications(HttpServletRequest req, PrintWriter out, Connection con) {

   ResultSet rs = null;
   Statement stmt = null;

   //
   //  get the session and the club name
   //
   HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

   int tmp_tlt = (Integer)sess.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;

   int pro_ytd = 0;
   int mem_ytd = 0;
   int pro_lm = 0;
   int mem_lm = 0;
   int pro_tm = 0;
   int mem_tm = 0;

   int pro_ytd_diff = 0;
   int mem_ytd_diff = 0;
   int pro_lm_diff = 0;
   int mem_lm_diff = 0;
   int pro_tm_diff = 0;
   int mem_tm_diff = 0;

   //
   // use the dates provided to search the tee times tables
   //
   try {

         // get YTD counts
         stmt = con.createStatement();        // create a statement
         rs = stmt.executeQuery("SELECT * FROM " +
            "(SELECT COUNT(*) AS pro_ytd, ROUND(AVG(DATEDIFF(req_datetime, created_datetime))) AS diff_pro " +
             "FROM notifications WHERE " +
                "created_by LIKE \"proshop%\" AND DATE_FORMAT(created_datetime, \"%Y\") = DATE_FORMAT(now(), \"%Y\") " +
            ") AS t1, " +
            "(SELECT COUNT(*) AS mem_ytd, ROUND(AVG(DATEDIFF(req_datetime, created_datetime))) AS diff_mem " +
                "FROM notifications WHERE " +
                "created_by NOT LIKE \"proshop%\" AND DATE_FORMAT(created_datetime, \"%Y\") = DATE_FORMAT(now(), \"%Y\") " +
            ") AS t2;");

         if ( rs.next() ) {
            pro_ytd = rs.getInt("pro_ytd");
            mem_ytd = rs.getInt("mem_ytd");
            pro_ytd_diff = rs.getInt("diff_pro");
            mem_ytd_diff = rs.getInt("diff_mem");
         }
         stmt.close();

         // get LM counts
         stmt = con.createStatement();        // create a statement
         rs = stmt.executeQuery("SELECT * FROM " +
            "(SELECT COUNT(*) AS pro_lm, ROUND(AVG(DATEDIFF(req_datetime, created_datetime))) AS diff_pro " +
             "FROM notifications WHERE " +
                "created_by LIKE \"proshop%\" AND " +
                "DATE_FORMAT(created_datetime, \"%Y\") = DATE_FORMAT(DATE_ADD(now(), INTERVAL -1 MONTH), \"%Y\") AND " +
                "DATE_FORMAT(created_datetime, \"%m\") = DATE_FORMAT(DATE_ADD(now(), INTERVAL -1 MONTH), \"%m\") " +
            ") AS t1, " +
            "(SELECT COUNT(*) AS mem_lm, ROUND(AVG(DATEDIFF(req_datetime, created_datetime))) AS diff_mem " +
             "FROM notifications WHERE " +
                "created_by NOT LIKE \"proshop%\" AND " +
                "DATE_FORMAT(created_datetime, \"%Y\") = DATE_FORMAT(DATE_ADD(now(), INTERVAL -1 MONTH), \"%Y\") AND " +
                "DATE_FORMAT(created_datetime, \"%m\") = DATE_FORMAT(DATE_ADD(now(), INTERVAL -1 MONTH), \"%m\") " +
            ") AS t2;");

         if ( rs.next() ) {
            pro_lm = rs.getInt("pro_lm");
            mem_lm = rs.getInt("mem_lm");
            pro_lm_diff = rs.getInt("diff_pro");
            mem_lm_diff = rs.getInt("diff_mem");
         }
         stmt.close();

         // get TM counts
         stmt = con.createStatement();        // create a statement
         rs = stmt.executeQuery("SELECT * FROM " +
            "(SELECT COUNT(*) AS pro_tm, ROUND(AVG(DATEDIFF(req_datetime, created_datetime))) AS diff_pro " +
             "FROM notifications WHERE " +
                "created_by LIKE \"proshop%\" AND " +
                "DATE_FORMAT(created_datetime, \"%Y\") = DATE_FORMAT(now(), \"%Y\") AND " +
                "DATE_FORMAT(created_datetime, \"%m\") = DATE_FORMAT(now(), \"%m\") " +
            ") AS t1, " +
            "(SELECT COUNT(*) AS mem_tm, ROUND(AVG(DATEDIFF(req_datetime, created_datetime))) AS diff_mem " +
             "FROM notifications WHERE " +
                "created_by NOT LIKE \"proshop%\" AND " +
                "DATE_FORMAT(created_datetime, \"%Y\") = DATE_FORMAT(now(), \"%Y\") AND " +
                "DATE_FORMAT(created_datetime, \"%m\") = DATE_FORMAT(now(), \"%m\") " +
            ") AS t2;");

         if ( rs.next() ) {
            pro_tm = rs.getInt("pro_tm");
            mem_tm = rs.getInt("mem_tm");
            pro_tm_diff = rs.getInt("diff_pro");
            mem_tm_diff = rs.getInt("diff_mem");
         }
         stmt.close();
   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Proshop Reports Page - Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<br><br><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Build the HTML page to display search results
   //
   out.println(SystemUtils.HeadTitle("Proshop Reports Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, 0);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

      out.println("<font size=\"4\">");
      out.println("<br><p><b>Notification Statistics</b></p>");
      out.println("</font>");

         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<tr bgcolor=\"#336633\"><td colspan=\"4\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\">This table identifies how many times the Golf Shop made");
                  out.println("<br>notifications versus how many times Members submitted notifications.");

                  out.println("<br><br>Percentages represent new notifications only and");
                  out.println("<br>are rounded down to the nearest whole number.</p>");
                  out.println("</font></td></tr>");

            out.println("<tr bgcolor=\"#336633\"><td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"left\">&nbsp;</p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><b>Last Month</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><b>This Month</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><b>This Year</b></p>");
                  out.println("</font></td>");

            out.println("</tr><tr>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\"><b>Golf Shop Made Notifications:</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (pro_lm < 1 || (pro_lm + mem_lm) < 1) {
                     out.println("<p align=\"center\">" + pro_lm + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + pro_lm + " (" + (pro_lm * 100) / (pro_lm + mem_lm) + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (pro_tm < 1 || (pro_tm + mem_tm) < 1) {
                     out.println("<p align=\"center\">" + pro_tm + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + pro_tm + " (" + (pro_tm * 100) / (pro_tm + mem_tm) + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (pro_ytd < 1 || (pro_ytd + mem_ytd) < 1) {
                     out.println("<p align=\"center\">" + pro_ytd + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + pro_ytd + " (" + (pro_ytd * 100) / (pro_ytd + mem_ytd) + "%)</p>");
                  }
                  out.println("</font></td>");

            out.println("</tr><tr>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\"><b>Member Made Notifications:</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (mem_lm < 1 || (pro_lm + mem_lm) < 1) {
                     out.println("<p align=\"center\">" + mem_lm + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + mem_lm + " (" + (mem_lm * 100) / (pro_lm + mem_lm) + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (mem_tm < 1 || (pro_tm + mem_tm) < 1) {
                     out.println("<p align=\"center\">" + mem_tm + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + mem_tm + " (" + (mem_tm * 100) / (pro_tm + mem_tm) + "%)</p>");
                  }
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  if (mem_ytd < 1 || (pro_ytd + mem_ytd) < 1) {
                     out.println("<p align=\"center\">" + mem_ytd + "</p>");
                  } else {
                     out.println("<p align=\"right\">" + mem_ytd + " (" + (mem_ytd * 100) / (pro_ytd + mem_ytd) + "%)</p>");
                  }
                  out.println("</font></td>");

            out.println("</tr><tr>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\"><br><b>Total Notifications:</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br><b>" + (pro_lm + mem_lm) + "</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br><b>" + (pro_tm + mem_tm) + "</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br><b>" + (pro_ytd + mem_ytd) + "</b></p>");
                  out.println("</font></td>");

/*
            out.println("</tr><tr>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\"><br><b>Member Modified Tee Times:</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>" + memModlmn + "</p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>" + memModthmn + "</p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>" + memModthyr + "</p>");
                  out.println("</font></td>");
*/

            out.println("</tr>");
         out.println("</font></table>");

         out.println("<br><br>");

         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<tr bgcolor=\"#336633\"><td colspan=\"4\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\">This table shows the average number of days between ");
                  out.println("<br>a notification being submitted and the day of play the notification is for.");
                  out.println("</font></td></tr>");

            out.println("<tr bgcolor=\"#336633\"><td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"left\">&nbsp;</p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><b>Last Month</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><b>This Month</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<p align=\"center\"><b>This Year</b></p>");
                  out.println("</font></td>");

            out.println("</tr><tr>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\"><b>Golf Shop Days in Advance:</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\">" + pro_lm_diff + "</p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\">" + pro_tm_diff + "</p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\">" + pro_ytd_diff + "</p>");
                  out.println("</font></td>");

            out.println("</tr><tr>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"right\"><b>Member Days in Advance:</b></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\">" + mem_lm_diff + "</p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\">" + mem_tm_diff + "</p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\">" + mem_ytd_diff + "</p>");
                  out.println("</font></td>");

            out.println("</tr>");
         out.println("</font></table>");


         out.println("</td></tr></table>");                // end of main page table & column

      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

      //
      //  End of HTML page
      //
      out.println("</center></font></body></html>");
      out.close();

 }  // end of goNotifications


 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR><BR>You can search for name or guest type up to 43 characters in length.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 // *********************************************************
 // Member does not exists
 // *********************************************************

 private void noMem(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, the member you specified does not exist in the database.<BR>");
   out.println("<BR>Please check your data and try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 // *********************************************************
 // Member does not exists
 // *********************************************************

 private void invDate(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, the date range is either invalid or missing.<BR>");
   out.println("<BR>Please check your data and try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

 }

 //**************************************************
 // Common Method for Displaying Database Errors
 //**************************************************
 //
 private void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {
    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H1>Database Access Error</H1>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Fatal Error: " + pMessage);
    out.println("<BR><br>Exception: " + pException);
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
    out.close();
 }

 /*
 private String[] addToArray(String[] labelArray, int[] countArray, int max, String label, int count1, int count2) {

    boolean found = false;
    int x = 0;

    loop1:
    for (x = 0; x < max; x++) {

        if (labelArray.equals(label)) {

            countArray[x] = count1 + count2;
            found = true;
            break loop1;

        } else if (labelArray.equals("")) {

            // found an empty one so we must be at the end of the defined types
            break loop1;

        }
    }

    // came across a member type that is no longer defined in the system
    if (!found && x < max) {

        // lets add it to our array for display
        labelArray = label;
        countArray[x] = count1 + count2;

    }

 }
 */

 
 //
 // this method was called getPastActTimes but now handles current not just old times
 //
 private void getActivityTimes(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, Connection con, String name) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;


    int sdate = 20090101;       // default date = 01/01/2009 (forever)
    int edate = 0;
    int year = 0;
    int month = 0;
    int day = 0;
    int calYear = 0;
    int start_year = 0;
    int start_month = 0;
    int start_day = 0;
    int end_year = 0;
    int end_month = 0;
    int end_day = 0;


    String subtee = req.getParameter("subtee");         //  tee time report subtype (cal, year, forever)


    if (subtee.equalsIgnoreCase( "custom" )) {                //  if for Custom Date Range

        String start_date = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0") : "";
        String end_date = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1") : "";

        // make sure the dates here are valid, if not redisplay the calendars
        try {

            int dash1 = start_date.indexOf("-");
            int dash2 = start_date.indexOf("-", dash1 + 1);
            start_year = Integer.parseInt(start_date.substring(0, 4));
            start_month = Integer.parseInt(start_date.substring(dash1 + 1, dash2));
            start_day = Integer.parseInt(start_date.substring(dash2 + 1));

            dash1 = end_date.indexOf("-");
            dash2 = end_date.indexOf("-", dash1 + 1);
            end_year = Integer.parseInt(end_date.substring(0, 4));
            end_month = Integer.parseInt(end_date.substring(dash1 + 1, dash2));
            end_day = Integer.parseInt(end_date.substring(dash2 + 1));

        } catch (Exception e) {
            // invalid dates here, bailout and call form again
            invDate(out);
            return;
        }

        // build our date variables for use in query
        sdate = start_year * 10000;                    // create a date field of yyyymmdd
        sdate = sdate + (start_month * 100);
        sdate = sdate + start_day;

        edate = end_year * 10000;                      // create a date field of yyyymmdd
        edate = edate + end_month * 100;
        edate = edate + end_day;

        if (sdate > edate) {
            // start date is after the end date, jump out and call form again
            invDate(out);
            return;
        }

    } else {

        //
        //  Get today's date
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        month = month + 1;                           // month starts at zero

        if (subtee.equals( "current" )) {

            sdate = year * 10000;
            sdate = sdate + (month * 100);
            sdate = sdate + day;

            edate = (year + 1) * 10000;     // add a year
            edate = edate + (month * 100);
            edate = edate + day;

        } else {

            edate = year * 10000;                   // create a edate field of yyyymmdd
            edate = edate + (month * 100);
            edate = edate + day;                         // date = yyyymmdd (for comparisons)

            if (subtee.equals( "cal" )) {                //  if for calendar year

                if (req.getParameter("calYear") != null) {        // if year already selected

                    String temp = req.getParameter("calYear");     // get the year

                    calYear = Integer.parseInt(temp);

                } else {

                    calYear = year;
                }

                sdate = (calYear * 10000) + 101;         // sdate = 01/01/yyyy
                edate = (calYear * 10000) + 1231;        // edate = 12/31/yyyy

            } else {

                if (subtee.equals( "year" )) {            //  if for past 12 months

                    sdate = year - 1;
                    sdate = sdate * 10000;
                    sdate = sdate + (month * 100);
                    sdate = sdate + day;                   // sdate = yyyymmdd (yyyy is 1 yr ago)
                }
            }        // else use default (forever)

        }

    }

    HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder

    String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);

    boolean toExcel = false;
    try{
        if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format

            toExcel = true;
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
        }
    } catch (Exception ignore) { }


    if (!toExcel) out.println(SystemUtils.HeadTitle2(""));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    if (!toExcel) SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page


    // search activities for guest times

    String location = "";
    String event = "";
    //String played_with = "";
    int count = 0;

    try {

        boolean found = false;

        boolean allmems = (req.getParameter("allmems") != null);

        String user = "";

        if (!allmems) user = SystemUtils.getUsernameFromFullName(name, con);

        if (!allmems && user.equals("")) {

           out.println("<font size=\"3\">");
           out.println("<p align=\"center\">Sorry we were unable to find the username for " + name + ".</p>");
           out.println("</font>");

        } else {

            //
            // use the name to search the activities table
            //
/*
            pstmt = con.prepareStatement (
                "SELECT a.sheet_id, a.activity_id, a.event_id, a.notes, ap.show, ap.player_name, " +
                   "DATE_FORMAT(a.date_time, '%m/%d/%Y') AS pretty_date, " +
                   "DATE_FORMAT(a.date_time, '%l:%i %p') AS pretty_time " +
                "FROM activity_sheets a, activity_sheets_players ap " +
                "WHERE a.sheet_id = ap.activity_sheet_id AND " +
                   "(DATE_FORMAT(a.date_time, '%Y%m%d') >= ? AND DATE_FORMAT(a.date_time, '%Y%m%d') <= ?) AND " +
                   ((!allmems) ? "ap.username = ? AND " : "") +
                   "report_ignore = 0 " +
                "ORDER BY " + ((!allmems) ? "a.date_time" : "ap.player_name, a.date_time"));
*/

            pstmt = con.prepareStatement (
                "SELECT a.sheet_id, a.activity_id, a.event_id, a.notes, ap.show, ap.player_name, ap.username, " +
                   "DATE_FORMAT(a.date_time, '%m/%d/%Y') AS pretty_date, " +
                   "DATE_FORMAT(a.date_time, '%l:%i %p') AS pretty_time " +
                "FROM activity_sheets_players ap " +
                "LEFT OUTER JOIN activity_sheets a ON a.sheet_id = ap.activity_sheet_id " +
                "WHERE ap.activity_sheet_id IN (" +
                    "SELECT a.sheet_id " +
                    "FROM activity_sheets a " +
                    "LEFT OUTER JOIN activity_sheets_players ap ON a.sheet_id = ap.activity_sheet_id " +
                    "WHERE " +
                        "DATE_FORMAT(a.date_time, '%Y%m%d') BETWEEN ? AND ? AND " +
                        ((!allmems) ? "ap.username = ? AND " : "") +
                        "report_ignore = 0) " +
                "ORDER BY " + ((!allmems) ? " a.date_time, a.sheet_id, ap.pos" : "ap.player_name, a.date_time"));

            pstmt.clearParameters();
            pstmt.setInt(1, sdate);
            pstmt.setInt(2, edate);
            if (!allmems) pstmt.setString(3, user);

            // debug
            if (!toExcel && Common_Server.SERVER_ID == 4) out.println("<!-- user=" + user + ", sdate=" + sdate + ", edate=" + edate + " -->");

            rs = pstmt.executeQuery();

            // if we found any then output the header row.
            rs.last();
            if (rs.getRow() > 0) {

                found = true;

                if (!toExcel) {

                    // output the non-excel page header
                    out.println("<font size=\"3\"><br>");
                    out.println("<p align=\"center\"><b>" + (subtee.equals("current") ? "Upcoming " : "") + "Activity History for " + ((!allmems) ? name : " All Members") + "</b></font>");

                    out.println("<br><br>Date Range: <b>");
                    if (subtee.equals("forever")) {
                        out.println("Since Inception");
                    } else {
                        out.println(Utilities.getDateFromYYYYMMDD(sdate, 0) + " thru " + Utilities.getDateFromYYYYMMDD(edate, 0));
                    }
                    out.println("</b></p>");

                    // out excel button
                    out.println("<center>");
                    out.println("<form method=\"post\" action=\"Proshop_reports\" target=\"_blank\">");
                    out.println("<input type=hidden name=subtee value=\"" + subtee + "\">");
                    if (allmems) {
                        out.println("<input type=hidden name=allmems value=\"\">");
                    } else {
                        out.println("<input type=hidden name=name value=\"" + name + "\">");
                    }
                    if (subtee.equals("custom")) {
                        out.println("<input type=hidden name=cal_box_0 value=\"" + Utilities.getDateFromYYYYMMDD(sdate, 1) + "\">");
                        out.println("<input type=hidden name=cal_box_1 value=\"" + Utilities.getDateFromYYYYMMDD(edate, 1) + "\">");
                    }
                    out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
                    out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
                    out.println("</form>");
                    out.println("</center>");
                }

                //  table heading
                out.println("<br><table " + ((toExcel) ? "" : "bgcolor=\"#F5F5DC\"") + " align=center border=1 cellpadding=6>");
                if (toExcel) out.println("" +
                        "<caption>" +
                            "<b>Activity History for " + ((!allmems) ? name : " All Members") + "</b><br>" +
                            "Date Range: <b>" + (subtee.equals("forever") ? "Since Inception" : Utilities.getDateFromYYYYMMDD(sdate, 0) + " thru " + Utilities.getDateFromYYYYMMDD(edate, 0)) +
                        "</caption>");

                out.println("<thead>");
                out.println("<tr " + ((toExcel) ? "" : "bgcolor=\"#336633\"") + ">");
                out.println(((allmems) ? "<td><font color=white><b>Player Name</b></font></td>" : "") +
                            "<td><font color=white><b>Date</b></font></td>" +
                            "<td><font color=white><b>Time</b></font></td>" +
                            "<td><font color=white><b>Location</b></font></td>" +
                            ((allmems) ? "" : "<td><font color=white><b>Player Names</b></font></td>") +
                            "<td><font color=white><b>Event</b></font></td>" +
                            "<td><font color=white><b>Checked In</b></font></td>" +
                            ((toExcel) ? "" : "<td><font color=white><b>&nbsp;</b></font></td>"));
                out.println("</tr>");
                out.println("</thead>");
                out.println("<tbody>");

            }

            rs.beforeFirst();

            int last_id = 0;
            boolean name_match = false, backup = false;
            
            while (rs.next()) {
                
                if (allmems) {

                    event = (rs.getInt("event_id") == 0) ? "&nbsp;" : Utilities.getEventName(rs.getInt("event_id"), con);
                    location = getActivity.getFullActivityName(rs.getInt("activity_id"), con);

                    //  output each row as each own line
                    out.println("<tr>");
                    if (allmems) out.println("<td nowrap>" + rs.getString("player_name") + "</td>");
                    out.println("<td>" + rs.getString("pretty_date") + "</td>");
                    out.println("<td>" + rs.getString("pretty_time") + "</td>");
                    out.println("<td>" + location + "</td>");
                    out.println("<td>" + event + "</td>" +
                                "<td>" + ((rs.getInt("show") == 1) ? "Yes" : "No") + "</td>");

                    if (!toExcel) {
                        out.println("<td bgcolor=\"" + ((rs.getString("notes").equals("")) ? "#F5F5DC" : "yellow") + "\" align=\"center\">");
                        out.println("<form method=\"post\" action=\"Proshop_reports\" target=\"_notes\">");
                        out.println("<font size=\"2\">");

                        out.println("<input type=\"hidden\" name=\"notes\" value=\"yes\">");
                        out.println("<input type=\"hidden\" name=\"slot_id\" value=\"" + rs.getInt("sheet_id") + "\">");
                        out.println("<input type=\"submit\" name=\"showNotes\" value=\"Details\">");

                        out.println("</font></form></td>");
                    }
                    out.println("</tr>");
                    
                } else {

                    last_id = rs.getInt("sheet_id");
                    
                    out.println("<tr>");
                    out.println("<td>" + rs.getString("pretty_date") + "</td>");
                    out.println("<td align=\"right\">" + rs.getString("pretty_time") + "</td>");
                    out.println("<td>" + getActivity.getFullActivityName(rs.getInt("activity_id"), con) + "</td>");

                    name_match = user.equals(rs.getString("username"));
                    out.print("<td nowrap>" + (name_match ? "<b>" : "") + rs.getString("player_name") + (name_match ? "</b>" : ""));

                    backup = false;
                    
                    // test for additional players
                    while (rs.next()) {

                        // if we advance the rs at all we'll need to back it up to finish the row
                        backup = true;

                        if (last_id == rs.getInt("sheet_id")) {
                            
                            // more players from the same time slot
                            //out.print(", " + rs.getString("player_name"));
                            name_match = user.equals(rs.getString("username"));
                            out.print(", " + (name_match ? "<b>" : "") + rs.getString("player_name") + (name_match ? "</b>" : ""));

                        } else {

                            break;
                        }

                    }
                    
                    if (backup) rs.previous();

                    out.print("</td>");

                    out.println("<td>" + ((rs.getInt("event_id") == 0) ? "&nbsp;" : Utilities.getEventName(rs.getInt("event_id"), con)) + "</td>" +
                                "<td>" + ((rs.getInt("show") == 1) ? "Yes" : "No") + "</td>");

                    if (!toExcel) {
                        out.println("<td bgcolor=\"" + ((rs.getString("notes").equals("")) ? "#F5F5DC" : "yellow") + "\" align=\"center\">");
                        out.println("<form method=\"post\" action=\"Proshop_reports\" target=\"_notes\">");
                        out.println("<font size=\"2\">");

                        out.println("<input type=\"hidden\" name=\"notes\" value=\"yes\">");
                        out.println("<input type=\"hidden\" name=\"slot_id\" value=\"" + rs.getInt("sheet_id") + "\">");
                        out.println("<input type=\"submit\" name=\"showNotes\" value=\"Details\">");

                        out.println("</font></form></td>");
                    }
                    out.println("</tr>");
                    
                }

                count++;

            } // end while rs

            if (found) {

                // output the results footer
                if (toExcel) {

                    out.println("</tbody>");
                    out.println("<tfoot>");
                    out.println("<tr><td align=center colspan=" + (toExcel ? "6" : "7") + ">");
                    if (!allmems) {
                        out.println("<b>" + name + "</b> had a total of <b>" + count + "</b> reservations during the specified period.");
                    } else {
                        out.println("Total of <b>" + count + "</b> members played during the specified period.");
                    }
                    out.println("</td></tr>");
                    out.println("</tfoot>");
                    out.println("</table>");

                } else {

                    out.println("</table>");
                    out.println("<font size=\"2\"><br>");
                    if (!allmems) {
                        out.println("<p align=\"center\"><b>" + name + "</b> had a total of <b>" + count + "</b> reservations during the specified period.</p></font>");
                    } else {
                        out.println("<p align=\"center\">Total of <b>" + count + "</b> members reservations during the specified period.</p></font>");
                    }
                }

            } else {

                // no results found
                out.println("<font size=\"3\"><br>");
                out.println("<p align=\"center\"><b>No Activity History Found For " + ((!allmems) ? name : "Any Members") + ".</b><br>");
                out.println("<br>Date Range: <b>" + Utilities.getDateFromYYYYMMDD(sdate, 0) + " thru " + Utilities.getDateFromYYYYMMDD(edate, 0) + "</b></p>");
                out.println("</font>");

            }

            pstmt.close();
            
        } // end if doing report or not

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg(exc.toString(), "", out, false);
        return;
    }

    if (!toExcel) {
        out.println("<center><BR><form method=get action=\"Proshop_reports\">");
        out.println("<input type=hidden name=subtee value=\"" + subtee + "\">");
        if (subtee.equals("custom")) {
            out.println("<input type=hidden name=cal_box_0 value=\"" + Utilities.getDateFromYYYYMMDD(sdate, 1) + "\">");
            out.println("<input type=hidden name=cal_box_1 value=\"" + Utilities.getDateFromYYYYMMDD(edate, 1) + "\">");
        }
        out.println("<input type=submit value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
    }
    out.println("</form></center>");

 }

 
 private void displaySlotDetails(int slot_id, PrintWriter out, Connection con) {

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    //ArrayList<Integer> sheet_ids = new ArrayList<Integer>();
    String notes = "", times = "";
    
    out.println("<html>");
    out.println("<head>");
    out.println("<!-- slot_id=" + slot_id + " -->");
    out.println("<title>Reservation Details</title>");
    out.println("<style>");
    out.println("body { text-align: center; }");
    out.println("</style>");
    out.println("</head><body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("<div style=\"height:50px\"></div>");
    out.println("<h3>Reservation Details</h3>");

    try {

        pstmt = con.prepareStatement("" +
                "SELECT ap.*, a.activity_id, a.notes, a.event_id, a.related_ids, " +
                   "DATE_FORMAT(a.date_time, '%W, %b. %D, %Y') AS full_date, " +
                   "DATE_FORMAT(a.date_time, '%l:%i %p') AS pretty_time " +
                "FROM activity_sheets_players ap " +
                "LEFT OUTER JOIN activity_sheets a ON a.sheet_id = ap.activity_sheet_id " +
                "WHERE activity_sheet_id = ? " +
                "ORDER BY pos");
        
        pstmt.clearParameters();
        pstmt.setInt(1, slot_id);
        rs = pstmt.executeQuery();

        // if we found any then output the header row.
        rs.last();
        if (rs.getRow() > 0) {

            if (rs.getString("related_ids").equals("")) {

                times = rs.getString("pretty_time");

            } else {

                // find any related times
                StringTokenizer tok = new StringTokenizer( rs.getString("related_ids"), "," );
                while ( tok.hasMoreTokens() ) {

                    //sheet_ids.add(Integer.parseInt(tok.nextToken()));

                    pstmt2 = con.prepareStatement (
                       "SELECT DATE_FORMAT(date_time, '%k%i') AS time " +
                       "FROM activity_sheets " +
                       "WHERE sheet_id = ?");

                    pstmt2.clearParameters();
                    pstmt2.setInt(1, Integer.parseInt(tok.nextToken()));
                    rs2 = pstmt2.executeQuery();

                    if (rs2.next()) {

                        times += SystemUtils.getSimpleTime(rs2.getInt(1)) + ", ";

                    }
                } // end while

                times = times.substring(0, times.length() - 2); // trim off comma & space
            }

            notes = rs.getString("notes");
            out.println("<div>" + rs.getString("full_date") + " at " + times + "</div>");
            if (rs.getInt("event_id") > 0) out.println("<div>Event Name: " + Utilities.getEventName(rs.getInt("event_id"), con) + "</div>");
            out.println("<div>" + getActivity.getFullActivityName(rs.getInt("activity_id"), con) + "</div>");
        }

        rs.beforeFirst();

        out.println("<br>");

        while (rs.next()) {

            out.println("&nbsp;<span>" + rs.getString("player_name") + "&nbsp;" +
                        "<img src=\"/" + rev + "/images/" + ((rs.getInt("show") == 1) ? "xbox.gif" : "mtbox.gif") + "\" border=\"1\">" +
                        "</span>&nbsp;");


        } // end player loop
            
        out.println("<table border=0 " + ((notes.equals("")) ? "" : "width=512 ") + "align=center>" +
                    "<tr><td><br><b>Notes:</b> " +
                    ((notes.equals("")) ? "<font color=silver>Empty</font>" : notes) + "</td>" +
                    "</tr></table>");

    } catch (Exception exc) {

        out.println("<p>ERROR LOADING DETAILS:" + exc.toString() + "</p>");

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
     
    out.println("<br><br><div><button onclick=\"window.close()\">Close Window</button></div>");
    
 }
 
}
