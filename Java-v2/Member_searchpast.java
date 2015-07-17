/***************************************************************************************
 *   Member_searchpast:  This servlet will process a request from Member's
 *                       search page.
 *
 *
 *   called by:  Member_maintop
 *
 *
 *   parms passed by Member_maintop:
 *
 *               subtee=cal for a List of a member's past tee times for calendar year
 *               subtee=year for a List of a member's past tee times for past 12 months
 *               subtee=forever for a List of a member's past tee times since inception
 *
 *
 *
 *   created: 6/03/2002   Bob P.
 *
 *   last updated:
 *
 *        1/17/13   Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *       11/20/12   Fixed issue with member search not working for FlxRez activities and made it so the year is now included in the printed date for FlxRez.
 *       10/04/12   Update sql statement for mysql 5 compatability
 *        9/06/12   Updated outputTopNav calls to also pass the HttpServletRequest object.
 *        1/22/12   New skin updates.
 *        3/27/10   Winged Foot - add changes to guest report for 2010 (case 1096).
 *       10/29/09   Add checks and processing for activities.
 *        7/28/09   Winged Foot (wingedfoot) - Added additional guest quotas
 *        6/18/09   Change guestReport method to call out to Utilities.checkWingedFootGuestTypes() for all wingedfoot guest name checks.
 *        6/16/09   Changes to Winged Foot guest report custom(questReport) (case 1096).
 *        7/02/08   Add course name to output for multi course clubs
 *        4/22/08   Updated guestReport for Wigned Foot - fixed dates
 *        4/10/08   Updated guestReport for Wigned Foot
 *        9/19/07   Winged Foot - Added guestReport for showing guest rounds as counted for quota (Case #1250)
 *        9/25/06   Minor verbiage changes for TLT compliance
 *       10/06/04   Ver 5 - add sub-menu support.
 *        1/13/04   JAG  Modifications to match new color scheme
 *        7/18/03   Enhancements for Version 3 of the software.
 *        9/18/02   Enhancements for Version 2 of the software.
 *
 *
 *
 ***************************************************************************************
 */

import com.foretees.common.Common_skin;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// ForeTees imports
import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;
import com.foretees.common.getActivity;
import com.foretees.common.nameLists;
import com.foretees.common.htmlTags;
import com.foretees.common.Connect;

public class Member_searchpast extends HttpServlet {

    //
    //  Holidays for custom codes that may require them
    //
    //   Must change them in ProcessConstants...
    //     also, refer to SystemUtils !!!!!!!!!
    //
    private static long Hdate1 = ProcessConstants.memDay;     // Memorial Day
    private static long Hdate2 = ProcessConstants.july4;      // 4th of July - Monday
    private static long Hdate2b = ProcessConstants.july4b;    // 4th of July - other
    private static long Hdate3 = ProcessConstants.laborDay;   // Labor Day
    private static long Hdate7 = ProcessConstants.tgDay;      // Thanksgiving Day
    private static long Hdate8 = ProcessConstants.colDay;     // Columbus Day
     
    private static long Hdate4 = ProcessConstants.Hdate4;     // October 1st
    private static long Hdate5 = ProcessConstants.Hdate5;     // Junior Fridays Start (start on Thurs.)
    private static long Hdate6 = ProcessConstants.Hdate6;     // Junior Fridays End  (end on Sat.)

    String omit = "";

    String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*****************************************************
 // Process the call from Member_maintop (via menu)
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing
 }


 //*******************************************************
 // Process the initial request from Member_searchmenu.htm page
 //*******************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   ResultSet rs = null;
   ResultSet rs2 = null;

   HttpSession sess = SystemUtils.verifyMem(req, out);       // check for intruder

   if (sess == null) {

      return;
   }

   Connection con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your proshop.");
      out.println("<BR><BR>");
      out.println("<a href=\"Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   String subtee = req.getParameter("subtee");         //  tee time report subtype
   
   String sday = "";
   String ampm = "";
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
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String course = "";
   String row_color = "";

   long date = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int count = 0;
   int custom_int = 0;

   long sdate = 20020101;       // default date = 01/01/2002 (forever)
   int year = 0;
   int month = 0;
   int day = 0;
   int multi = 0;
   int fives = 0;
   int fivesomes = 0;


   String user = (String)sess.getAttribute("user");      // get member's username
   String name = (String)sess.getAttribute("name");      // get member's name
   String caller = (String)sess.getAttribute("caller"); 
   String club = (String)sess.getAttribute("club"); 
   boolean new_skin = ((String) sess.getAttribute("new_skin")).equals("1");
   boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
   
   htmlTags tags = new htmlTags(rwd);

   //
   // See what activity mode we are in
   //
   int sess_activity_id = 0;

   try { sess_activity_id = (Integer)sess.getAttribute("activity_id"); }
   catch (Exception ignore) { }

   String clubName = Utilities.getClubName(con, true);        // get the full name of this club
   String club_group_name = Utilities.getClubGroupName(club);
         
   
   if (subtee.equals("gquota")) {
       
       guestReport(req, resp, user, caller, club, new_skin, sess_activity_id, out, con);
       return;
   }
   
   //
   //  Get today's date and use it for the end date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);

   month = month + 1;                           // month starts at zero

   long edate = year * 10000;                   // create a edate field of yyyymmdd
   edate = edate + (month * 100);
   edate = edate + day;                         // date = yyyymmdd (for comparisons)

   if (subtee.equals( "cal" )) {                //  if for calendar year

      sdate = year * 10000;
      sdate = sdate + 0101;                     // sdate = 01/01/yyyy

   } else {

      if (subtee.equals( "year" )) {            //  if for past 12 months

         sdate = year - 1;
         sdate = sdate * 10000;
         sdate = sdate + (month * 100);
         sdate = sdate + day;                   // sdate = yyyymmdd (yyyy is 1 yr ago)
      }
   }        // else use default (forever)

   
   
   //
   //  Process according to the activity
   //
   if (sess_activity_id == 0) {    // if Golf  

      
      //****************************************************************************************
      //   GOLF
      //****************************************************************************************
     
      // get multi for this club
       try {

           Statement stmt = con.createStatement();
           rs = stmt.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");

           if (rs.next()) multi = rs.getInt(1);

           //  Check for 5-somes
           rs = stmt.executeQuery("SELECT fives FROM clubparm2");

           while (rs.next()) {

                fives = rs.getInt(1);

                if (fives != 0) {

                   fivesomes = 1;          // set 5-somes supported for this club
                }
           }

           stmt.close();

       } catch (Exception ignore) { }
       
       if (club.equals("springhillgc")) {
           fivesomes = 0;
       }


      //
      // use the username and dates provided to search table
      //
      try {

         PreparedStatement stmt = con.prepareStatement (
            "SELECT date, mm, dd, yy, day, hr, min, time, player1, player2, player3, player4, " +
            "p1cw, p2cw, p3cw, p4cw, player5, p5cw, courseName, " +
            "username1, username2, username3, username4, username5, custom_int " + 
            "FROM teepast2 " +
            "WHERE (username1 LIKE ? OR username2 LIKE ? OR username3 LIKE ? OR username4 LIKE ? OR username5 LIKE ?) " +
            "AND (date >= ? AND date <= ?) " +
            "ORDER BY date, time");

         stmt.clearParameters();        // clear the parms
         stmt.setString(1, user);
         stmt.setString(2, user);
         stmt.setString(3, user);
         stmt.setString(4, user);
         stmt.setString(5, user);
         stmt.setLong(6, sdate);
         stmt.setLong(7, edate);
         rs = stmt.executeQuery();      // execute the prepared stmt

      //
      //  Build the HTML page to display search results
      //
      if (new_skin) {
          
          //
          //  Build the top of the page
          //
          Common_skin.outputHeader(club, sess_activity_id, "Member Search", true, out, req);
          Common_skin.outputBody(club, sess_activity_id, out, req);
          Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
          Common_skin.outputBanner(club, sess_activity_id, clubName, (String) sess.getAttribute("zipcode"), out, req);  
          Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
          Common_skin.outputPageStart(club, sess_activity_id, out, req);
          Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Search Past Times", req);
          Common_skin.outputLogo(club, sess_activity_id, out, req);

          if(!rwd){
            out.println("<div class=\"preContentFix\"></div>"); // clear the float
          }
          
          out.println("<H2>Past Reservations for <span class=\"ft-textGroup\">" + Utilities.filterDisplayName(name, club, club_group_name) + "</span></H2>");
          
          if(!rwd){
            out.println("<br>");
          }
          
          out.println("<"+tags.table+" class=\"rwdTable standard_list_table rwdCompactible\">");
          if(rwd){
              out.println("  <"+tags.thead+" class=\"rwdThead\"><"+tags.tr+" class=\"rwdTr\">");
              out.println("  <"+tags.th+" class=\"rwdTh\">Date / Time</"+tags.th+">");
              if (multi != 0) out.println("  <"+tags.th+" class=\"rwdTh\">Course</"+tags.th+">");
              out.println("  <"+tags.th+" class=\"rwdTh\">Players</"+tags.th+">");
              out.println(" </"+tags.tr+"></"+tags.thead+">");
          } else {
              out.println("  <thead><tr>");
              out.println("  <th>Date</th>");
              if (multi != 0) out.println("  <th>Course</th>");
              out.println("  <th>Time</th>");
              out.println("  <th>Player 1</th>");
              out.println("  <th>C/W</th>");
              out.println("  <th>Player 2</th>");
              out.println("  <th>C/W</th>");
              out.println("  <th>Player 3</th>");
              out.println("  <th>C/W</th>");
              out.println("  <th>Player 4</th>");
              out.println("  <th>C/W</th>");
              if (fivesomes != 0) out.println("  <th>Player 5</th><th>C/W</th>");
              out.println(" </tr></thead>");
          }
          out.println(" <"+tags.tbody+" class=\"rwdTbody\">");
                         
      } else {
             
          out.println(SystemUtils.HeadTitle("Member Search Page"));
          out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
          SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
          out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

          out.println("<table border=\"0\" align=\"center\">");
          out.println("<tr><td align=\"center\">");

             out.println("<font size=\"3\">");
             out.println("<p>Rounds Played for " + name + "</p>");
             out.println("</font>");

             if (club.equals("wingedfoot")) out.println("<p align=\"center\"><form><input type=hidden name=subtee value=gquota><input type=submit value=\" Guest Quota Report \"></form></p>");

             out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"4\">");
                out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<tr bgcolor=\"#336633\"><td>");
                   out.println("<font color=\"ffffff\" size=\"2\">");
                   out.println("<p align=\"center\"><u><b>Date</b></u></p>");
                   out.println("</font></td>");

                if (multi == 1) {
                out.println("<td>");
                   out.println("<font color=\"ffffff\" size=\"2\">");
                   out.println("<p align=\"center\"><u><b>Course</b></u></p>");
                   out.println("</font></td>");
                }

                out.println("<td>");
                   out.println("<font color=\"ffffff\" size=\"2\">");
                   out.println("<p align=\"center\"><u><b>Time</b></u></p>");
                   out.println("</font></td>");

                out.println("<td nowrap>");
                   out.println("<font color=\"ffffff\" size=\"2\">");
                   out.println("<p align=\"center\"><u><b>Player 1</b></u></p>");
                   out.println("</font></td>");

                out.println("<td>");
                   out.println("<font color=\"ffffff\" size=\"2\">");
                   out.println("<p align=\"center\"><u><b>C/W</b></u></p>");
                   out.println("</font></td>");

                out.println("<td nowrap>");
                   out.println("<font color=\"ffffff\" size=\"2\">");
                   out.println("<p align=\"center\"><u><b>Player 2</b></u></p>");
                   out.println("</font></td>");

                out.println("<td>");
                   out.println("<font color=\"ffffff\" size=\"2\">");
                   out.println("<p align=\"center\"><u><b>C/W</b></u></p>");
                   out.println("</font></td>");

                out.println("<td nowrap>");
                   out.println("<font color=\"ffffff\" size=\"2\">");
                   out.println("<p align=\"center\"><u><b>Player 3</b></u></p>");
                   out.println("</font></td>");

                out.println("<td>");
                   out.println("<font color=\"ffffff\" size=\"2\">");
                   out.println("<p align=\"center\"><u><b>C/W</b></u></p>");
                   out.println("</font></td>");

                out.println("<td nowrap>");
                   out.println("<font color=\"ffffff\" size=\"2\">");
                   out.println("<p align=\"center\"><u><b>Player 4</b></u></p>");
                   out.println("</font></td>");

                out.println("<td>");
                   out.println("<font color=\"ffffff\" size=\"2\">");
                   out.println("<p align=\"center\"><u><b>C/W</b></u></p>");
                   out.println("</font></td>");

                out.println("<td nowrap>");
                   out.println("<font color=\"ffffff\" size=\"2\">");
                   out.println("<p align=\"center\"><u><b>Player 5</b></u></p>");
                   out.println("</font></td>");

                out.println("<td>");
                   out.println("<font color=\"ffffff\" size=\"2\">");
                   out.println("<p align=\"center\"><u><b>C/W</b></u></p>");
                   out.println("</font></td></tr>");
                   
      }    // end of new_skin

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
            player5 = rs.getString(17);
            course = rs.getString(19);
            user1 = rs.getString("username1");
            user2 = rs.getString("username2");
            user3 = rs.getString("username3");
            user4 = rs.getString("username4");
            user5 = rs.getString("username5");
            custom_int = rs.getInt("custom_int");

            ampm = " AM";
            if (hr == 12) {
               ampm = " PM";
            }
            if (hr > 12) {
               ampm = " PM";
               hr = hr - 12;    // convert to conventional time
            }
            
            if(!rwd){
                if (player1.equals( "" )) {

                   player1 = "&nbsp;";       // make it a space for table display
                }
                if (player2.equals( "" )) {

                   player2 = "&nbsp;";       // make it a space for table display
                }
                if (player3.equals( "" )) {

                   player3 = "&nbsp;";       // make it a space for table display
                }
                if (player4.equals( "" )) {

                   player4 = "&nbsp;";       // make it a space for table display
                }
                if (player5.equals( "" )) {

                   player5 = "&nbsp;";       // make it a space for table display
                }
                if (p1cw.equals( "" )) {

                   p1cw = "&nbsp;";       // make it a space for table display
                }
                if (p2cw.equals( "" )) {

                   p2cw = "&nbsp;";       // make it a space for table display
                }
                if (p3cw.equals( "" )) {

                   p3cw = "&nbsp;";       // make it a space for table display
                }
                if (p4cw.equals( "" )) {

                   p4cw = "&nbsp;";       // make it a space for table display
                }
                if (p5cw.equals( "" )) {

                   p5cw = "&nbsp;";       // make it a space for table display
                }

                if (sday.equalsIgnoreCase( "sunday" )) {

                   sday = "Sun";
                }
                if (sday.equalsIgnoreCase( "monday" )) {

                   sday = "Mon";
                }
                if (sday.equalsIgnoreCase( "tuesday" )) {

                   sday = "Tues";
                }
                if (sday.equalsIgnoreCase( "wednesday" )) {

                   sday = "Wed";
                }
                if (sday.equalsIgnoreCase( "thursday" )) {

                   sday = "Thurs";
                }
                if (sday.equalsIgnoreCase( "friday" )) {

                   sday = "Fri";
                }
                if (sday.equalsIgnoreCase( "saturday" )) {

                   sday = "Sat";
                }
            }

            //
            //  Build the HTML for each record found
            //
            
            if (club.equals("kiawahislandclub") && custom_int > 0) {
                row_color = "orange";
            } else {
                row_color = "#FFFFFF";
            }
            
            if(rwd){
                String[] players = {player1,player2,player3,player4,player5};
                String[] pcw = {p1cw,p2cw,p3cw,p4cw,p5cw};
                String[] users = {user1,user2,user3,user4,user5};
                if (club.equals("kiawahislandclub")) {
                    out.print("<"+tags.tr+" class=\"rwdTr\" style=\"background-color:" + row_color + ";\">");
                } else {
                    out.print("<"+tags.tr+" class=\"rwdTr\">");
                }
                out.print("<"+tags.td+" class=\"rwdTd sT\"><div class=\"time_slot\">"+ mm + "/" + dd + "/" + yy+" - "+hr + (min<10?":0":":") + min + ampm+"</div></"+tags.td+">");
                if (multi == 1) {
                    out.print("<"+tags.td+" class=\"rwdTd sN\">"+course+"</"+tags.td+">");
                }
                out.print("<"+tags.td+" class=\"rwdTd sP\">");
                for(int i = 0; i < players.length; i++){
                    if(!players[i].equals("")){
                        out.print("<div");
                        if(users[i].equals( user )){
                            out.print(" class=\"me\"");
                        }
                        out.print(">");
                        out.print("<span>"+players[i]+"</span>");
                        out.print("<span>"+pcw[i]+"</span>");
                        out.print("</div>");
                    }
                }
                out.print("</"+tags.td+">");
                out.println("</"+tags.tr+">");
            }else{
                if (club.equals("kiawahislandclub")) {
                    out.println("<tr style=\"background-color:" + row_color + ";\">");
                } else {
                    out.println("<tr>");
                }
                out.println("<td align=\"center\">");
                   out.println("<font size=\"2\">");
                   out.println( sday + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy );
                   out.println("</font></td>");

                if (multi == 1) {
                out.println("<td align=\"center\">");
                   out.println("<font size=\"2\">");
                   out.println( course );
                   out.println("</font></td>");
                }

                out.println("<td align=\"center\" nowrap>");
                   out.println("<font size=\"2\">");
                if (min < 10) {
                   out.println(hr + ":0" + min + ampm);
                } else {
                   out.println(hr + ":" + min + ampm);
                }
                out.println("</font></td>");

                out.println("<td align=\"center\" " + (player1.equals(name) ? "style=\"font-weight:bold;\"" : "") + ">");
                out.println("<font size=\"2\">");
                out.println(Utilities.filterDisplayName(player1, club, club_group_name));
                out.println("</font></td>");

                out.println("<td align=\"center\">");
                out.println("<font size=\"2\">");
                out.println( p1cw );
                out.println("</font></td>");

                out.println("<td align=\"center\"" + (player2.equals(name) ? "style=\"font-weight:bold;\"" : "") + ">");
                out.println("<font size=\"2\">");
                out.println(Utilities.filterDisplayName(player2, club, club_group_name));
                out.println("</font></td>");

                out.println("<td align=\"center\">");
                out.println("<font size=\"2\">");
                out.println( p2cw );
                out.println("</font></td>");

                out.println("<td align=\"center\"" + (player3.equals(name) ? "style=\"font-weight:bold;\"" : "") + ">");
                out.println("<font size=\"2\">");
                out.println(Utilities.filterDisplayName(player3, club, club_group_name));
                out.println("</font></td>");

                out.println("<td align=\"center\">");
                out.println("<font size=\"2\">");
                out.println( p3cw );
                out.println("</font></td>");

                out.println("<td align=\"center\"" + (player4.equals(name) ? "style=\"font-weight:bold;\"" : "") + ">");
                out.println("<font size=\"2\">");
                out.println(Utilities.filterDisplayName(player4, club, club_group_name));
                out.println("</font></td>");
                
                out.println("<td align=\"center\">");
                out.println("<font size=\"2\">");
                out.println( p4cw );
                out.println("</font></td>");
                
                if (fivesomes != 0) {

                    out.println("<td align=\"center\"" + (player5.equals(name) ? "style=\"font-weight:bold;\"" : "") + ">");
                    out.println("<font size=\"2\">");
                    out.println(Utilities.filterDisplayName(player5, club, club_group_name));
                    out.println("</font></td>");

                    out.println("<td align=\"center\">");
                    out.println("<font size=\"2\">");
                    out.println( p5cw );
                    out.println("</font></td>");
                }
                out.println("</tr>");
            }
            count++;

         }    // end of while

         stmt.close();
         
         if (new_skin) {
             
             out.println(" </"+tags.tbody+"></"+tags.table+">");
             out.println("</td></tr></table><BR>");                // end of main page table & column
             
         } else {

             out.println("</font></table>");
             out.println("</td></tr></table>");                // end of main page table & column
         }

          out.println("<p><BR><b>" + Utilities.filterDisplayName(name, club, club_group_name) + "</b> played a total of <b>" + count + "</b> rounds during the specified period.</p>");         
         
      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error:" + exc.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your proshop.");
      }

      
   } else {        // Not Golf
         
         
      //****************************************************************************************
      //   ACTIVITIES
      //****************************************************************************************
     
      Common_skin.outputHeader(club, sess_activity_id, "Member Search", true, out, req);
      Common_skin.outputBody(club, sess_activity_id, out, req);
      Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
      Common_skin.outputBanner(club, sess_activity_id, clubName, (String) sess.getAttribute("zipcode"), out, req);  
      Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
      Common_skin.outputPageStart(club, sess_activity_id, out, req);
      Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Search Past Times", req);
      Common_skin.outputLogo(club, sess_activity_id, out, req);

      if(!rwd){
        out.println("<div class=\"preContentFix\"></div>"); // clear the float
      }

      out.println("<H2>Past Reservations for <span class=\"ft-textGroup\">" + Utilities.filterDisplayName(name, club, club_group_name) + "</span></H2>");

      if(!rwd){
        out.println("<br>");
      }

      try {
         
         PreparedStatement pstmt2 = null;

         //
         // non-Golf Activity - find any old reservations for user making the request
         //
         PreparedStatement pstmt1 = con.prepareStatement (
             "SELECT *, " +
                "DATE_FORMAT(ash.date_time, '%W, %b. %D, %Y') AS pretty_date, " +
                "DATE_FORMAT(ash.date_time, '%Y%m%d') AS dateymd, " +
                "DATE_FORMAT(ash.date_time, '%l:%i %p') AS pretty_time, " +
                "DATE_FORMAT(ash.date_time, '%m/%d/%Y') AS rwd_date " +
             "FROM activity_sheets_players asp " +
                "LEFT OUTER JOIN activity_sheets ash ON ash.sheet_id = asp.activity_sheet_id " +
                "LEFT OUTER JOIN activities a ON a.activity_id = ash.activity_id " +
             "WHERE asp.username = ? AND " +
                "DATE_FORMAT(ash.date_time, '%Y%m%d') >= ? AND " +
                "DATE_FORMAT(ash.date_time, '%Y%m%d') <= ? " +
             "ORDER BY ash.date_time");

         pstmt1.clearParameters();       
         pstmt1.setString(1, user);
         pstmt1.setLong(2, sdate);
         pstmt1.setLong(3, edate);

         rs = pstmt1.executeQuery();    

         // if we found any then output the header row.
         rs.last();
         if (rs.getRow() > 0) {
             
             if (new_skin) {
                 
                  out.println("<"+tags.table+" class=\"rwdTable standard_list_table rwdCompactible\">");
                  out.println("  <"+tags.thead+" class=\"rwdThead\"><"+tags.tr+" class=\"rwdTr\">");
                  out.println("  <"+tags.th+" class=\"rwdTh\">Date/Time</"+tags.th+">");
                  out.println("  <"+tags.th+" class=\"rwdTh\">Location</"+tags.th+">");
                  out.println("  <"+tags.th+" class=\"rwdTh\">Players</"+tags.th+">");
                  out.println(" </"+tags.tr+"></"+tags.thead+">");
                  out.println(" <"+tags.tbody+" class=\"rwdTbody\">");
                 
             } else {

                out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"5\">");
                out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<tr bgcolor=\"#336633\"><td>");
                out.println("<font size=\"2\">");
                out.println("<p align=\"center\"><u><b>Date/Time</b></u></p>");
                out.println("</font></td>");

                out.println("<td>");
                out.println("<font size=\"2\">");
                out.println("<p align=\"center\"><u><b>Location</b></u></p>");
                out.println("</font></td>");

                out.println("<td align=\"center\">");
                out.println("<font size=\"2\">");
                out.println("<u><b>Players</b></u>");
                out.println("</font></td>");
                out.println("</tr>");
             }
         }

         rs.beforeFirst();       // back up

         //
         //  Get each record and display it
         //
         count = 0;             // number of records found

         while (rs.next()) {

            StringBuilder playerList = new StringBuilder();
            count++;
            int pcount = 0;

            //
            //   Get the players 
            //
            pstmt2 = con.prepareStatement("" +
                   "SELECT *, (" +
                       "SELECT COUNT(*) AS players " +
                       "FROM activity_sheets_players " +
                       "WHERE activity_sheet_id = ?) AS part_of " +
                   "FROM activity_sheets_players " +
                   "WHERE activity_sheet_id = ? " +
                   "ORDER BY pos");
            pstmt2.clearParameters();
            pstmt2.setInt(1, rs.getInt("sheet_id"));
            pstmt2.setInt(2, rs.getInt("sheet_id"));
            rs2 = pstmt2.executeQuery();

            String player = null;
            
            while ( rs2.next() ) {

               pcount++;          // count number of players
               
               if(rwd){
                   player = rs2.getString("player_name");
                   if(!player.equals("")){
                        playerList.append("<div");
                        if(player.equals( name )){
                            playerList.append(" class=\"me\"");
                        }
                        playerList.append(">");
                        playerList.append("<span>");
                        playerList.append(player);
                        playerList.append("</span>");
                        playerList.append("</div>");
                    }
               } else {
                playerList.append(rs2.getString("player_name") + "<BR>");     // add player to playerlist
               }
            }

            //
            //  Build the HTML for each record found
            //
            out.print("<"+tags.tr+" class=\"rwdTr\">");
            if(rwd){
                out.println("<"+tags.td+" class=\"rwdTd sT\"><div class=\"time_slot\">");
                out.print(rs.getString("rwd_date")+ " - " + rs.getString("pretty_time") ); 
                out.print("</div></"+tags.td+">");
                out.print("<"+tags.td+" class=\"rwdTd sN\">");
                out.print(getActivity.getFullActivityName(rs.getInt("activity_id"), con));
                out.print("</"+tags.td+">");
                out.println("<"+tags.td+" class=\"rwdTd sP\">");
                out.println( playerList );
                out.println("</"+tags.td+">");
            }else{
                out.println("<td align=\"center\">");
                out.print("<font size=2>" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</font>"); 
                out.println("</td>");

                out.println("<td align=\"center\" bgcolor=\"white\">");
                out.println("<font size=\"2\">");
                out.println(getActivity.getFullActivityName(rs.getInt("activity_id"), con));
                out.println("</font></td>");

                out.println("<td align=\"center\" bgcolor=\"white\">");
                out.println("<font size=\"2\">");
                out.println( playerList );
                out.println("</font></td>");
            }
            out.println("</"+tags.tr+">");          

            pstmt2.close();

         }    // end of while

         pstmt1.close();

         if (new_skin) {
             
             out.print(" </"+tags.tbody+"></"+tags.table+">");
             if(!rwd){
                 out.println("<BR>");
             }
             
         } else {

             out.println("</font></table>");
         }

         if (count == 0) {

            out.println("<br><p align=\"center\">No records found for " + name + ".</p>");
         }               
                 
         out.println("</td></tr></table>");      // end of main page table & column
               
      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><BR>");
         out.println("<BR><BR><H3>System Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error:" + exc.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your system administrator.");
      }
      
   }   // end of IF golf or Activities
      
   if (new_skin) {
       if(!rwd){
           out.println("<br><br><div style=\"text-align:center;\">");
           out.println("<input id=\"back\" name=\"back\" type=\"button\" value=\"Home\" onclick=\"window.location.href='Member_announce'\" /></div>");
           out.println("</div>"); // end reservations // This
           out.println("</center></div></div>"); // and this seem wrong for new skin.
       }

       Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page       

   } else {
             
       out.println("<br><font size=\"2\">");
       out.println("<form method=\"get\" action=\"Member_announce\">");
       out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;width:100px\">");
       out.println("</form></font>");

       out.println("</center>&nbsp;<br></font></body></html>");
   }
   out.close();

 }  // end of doPost


 //
 // NOTE: THIS EXACT REPORT EXISTS IN Proshop_reports!!!!!!!
 //
 private void guestReport(HttpServletRequest req, HttpServletResponse resp, String user, String caller, String club, boolean new_skin, int sess_activity_id, PrintWriter out, Connection con) {
   
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
    String ndate = "";
    
   String clubName = Utilities.getClubName(con, true);        // get the full name of this club
         
    
    Calendar cal = new GregorianCalendar();
    int year = cal.get(Calendar.YEAR);
    
    long sdate = (year * 10000) + 501;                  // yyyy0501     Golf Year bookends
    //long sdate = (2000 * 10000) + 501;                  // yyyy0501     Golf Year bookends
    long edate = (year * 10000) + 1031;                 // yyyy1031

    int i = 0;
    int countg = 0; // # of guests
    int counts = 0; // # of guests in season
    int date = 0;
    int time = 0;
    int members = 0;
    
    boolean found = false;
    
    ResultSet rs = null;
    ResultSet rs2 = null;
    
    boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
    
    htmlTags tags = new htmlTags(rwd);
    
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

        if (mship.equals("Regular Family") || mship.equals("Senior Family")) {

            maxs = 20;
            maxg = 24;

        } else if (mship.equals("Regular") || mship.equals("Regular Senior")
                || mship.equals("Junior C Family") || mship.equals("Junior Family D")) {

            maxs = 14;
            maxg = 20;

        } else if (mship.equals("Junior B")) {

            maxs = 10;
            maxg = 15;

        } else if (mship.equals("Junior C") || mship.equals("Junior D")) {

            maxs = 12;
            maxg = 18;

        } else if (mship.equals("Family Playing Privileges")) {

            maxg = 9;
        }

        //
        //  Build the HTML page to display search results
        //
        if (new_skin) {
            
            Common_skin.outputHeader(club, sess_activity_id, "Guest Rounds", true, out, req);
            Common_skin.outputBody(club, sess_activity_id, out, req);
            Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
            Common_skin.outputBanner(club, sess_activity_id, clubName, "", out, req);  
            Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
            Common_skin.outputPageStart(club, sess_activity_id, out, req);
            Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Guest Rounds Report", req);
            Common_skin.outputLogo(club, sess_activity_id, out, req);

            if(!rwd){
                out.println("<div class=\"preContentFix\"></div>"); // clear the float
            }
            out.println("<h2>Guest Rounds Report</h2>");
          
              out.println("<"+tags.table+" class=\"rwdTable standard_list_table rwdCompactible\">");
              out.println("  <"+tags.thead+" class=\"rwdThead\"><"+tags.tr+" class=\"rwdTr\">");
              if(rwd){
                  out.println("  <"+tags.th+" class=\"rwdTh\">Date/Time</"+tags.th+">");
              } else {
                  out.println("  <"+tags.th+" class=\"rwdTh\">Date</"+tags.th+">");
                  out.println("  <"+tags.th+" class=\"rwdTh\">Time</"+tags.th+">");
              }
              out.println("  <"+tags.th+" class=\"rwdTh\">Course</"+tags.th+">");
              if(rwd){
                  out.println("  <"+tags.th+" class=\"rwdTh\">Players</"+tags.th+">");
              } else {
                  out.println("  <th>Player 1</th>");
                  out.println("  <th>Player 2</th>");
                  out.println("  <th>Player 3</th>");
                  out.println("  <th>Player 4</th>");
                  out.println("  <th>Player 5</th>");
              }
              out.println(" </"+tags.tr+"></"+tags.thead+">");
              out.println(" <"+tags.tbody+" class=\"rwdTbody\">");
                             
        } else {
            
            out.println(SystemUtils.HeadTitle("Member Search Page"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
            SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
            out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

            // display report header
            out.println("<h2>Guest Rounds Report</h2>");
            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"4\">");
            out.println("<tr align=center bgcolor=\"#336633\" style=\"color: white;font-weight: bold;text-decoration: underline\"><td>Date</td><td>Time</td><td>Course</td><td>Player 1</td><td>Player 2</td><td>Player 3</td><td>Player 4</td><td>Player 5</td></tr>");
        
        }   // end of new_skin
        
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
               "SELECT player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, "
                    + "date, time, courseName, DATE_FORMAT(date, \"%b. %e, %y\") AS fdate, DATE_FORMAT(date, \"%m/%d/%Y\") AS ndate " +
               "FROM teepast2 " +
               "WHERE date >= ? AND date <= ? AND " +
               "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

            pstmt.clearParameters();
            pstmt.setLong(1, sdate);           // Labor Day
            pstmt.setLong(2, edate);           // Memorial Day
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
               ndate = rs.getString(15);

               if (userg1.equals( username ) && Utilities.checkWingedFootGuestTypes(player1, mship)) {

                  counts++;     // bump # of guests
                  found = true;
               }
               if (userg2.equals( username ) && Utilities.checkWingedFootGuestTypes(player2, mship)) {

                  counts++;     // bump # of guests
                  found = true;
               }
               if (userg3.equals( username ) && Utilities.checkWingedFootGuestTypes(player3, mship)) {

                  counts++;     // bump # of guests
                  found = true;
               }
               if (userg4.equals( username ) && Utilities.checkWingedFootGuestTypes(player4, mship)) {

                  counts++;     // bump # of guests
                  found = true;
               }
               if (userg5.equals( username ) && Utilities.checkWingedFootGuestTypes(player5, mship)) {

                  counts++;     // bump # of guests
                  found = true;
               }

               if (found) {
                   if(rwd){
                       out.println("<"+tags.tr+" class=\"rwdTr\">");
                        String[] players = {player1,player2,player3,player4,player5};
                        out.print("<"+tags.td+" class=\"rwdTd sT\"><div class=\"time_slot\">" + ndate + " " + SystemUtils.getSimpleTime(time) + "</div></"+tags.td+">");
                        out.print("<"+tags.td+" class=\"rwdTd sN\">"+course+"</"+tags.td+">");
                        out.print("<"+tags.td+" class=\"rwdTd sP\">");
                        for(int i2 = 0; i2 < players.length; i2++){
                            if(!players[i2].equals("")){
                                out.print("<div");
                                /*
                                if(players[i2].equals( name )){
                                    out.print(" class=\"me\"");
                                }
                                 */
                                out.print(">");
                                out.print("<span>"+players[i2]+"</span>");
                                out.print("</div>");
                            }
                        }
                        out.print("</"+tags.td+">");
                        out.println("</"+tags.tr+">");
                   } else {
                       out.println("<tr><td align=right>" + fdate + "</td><td align=center>" + SystemUtils.getSimpleTime(time) + "</td><td align=center>" + course + "</td>" +
                            "<td>" + player1 + "</td><td>" + player2 + "</td><td>" + player3 + "&nbsp;</td><td>" + player4 + "&nbsp;</td><td>" + player5 + "&nbsp;</td></tr>");
                   }
               }

            }      // end of WHILE
            rs.close();
            pstmt.close();

            //out.println("<tr><td colspan=8 align=center><b>Scheduled In Season Tee Times</b></td></tr>");

            pstmt = con.prepareStatement (
               "SELECT player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, date, time, courseName, "
                    + "DATE_FORMAT(date, \"%b. %e, %y\") AS fdate, DATE_FORMAT(date, \"%m/%d/%Y\") AS ndate " +
               "FROM teecurr2 " +
               "WHERE date >= ? AND date <= ? AND " +
               "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

            pstmt.clearParameters();
            pstmt.setLong(1, sdate);              
            pstmt.setLong(2, edate);               
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
               fdate = rs.getString(15);

               if (userg1.equals( username ) && Utilities.checkWingedFootGuestTypes(player1, mship)) {

                  counts++;     // bump # of guests
                  found = true;
               }
               if (userg2.equals( username ) && Utilities.checkWingedFootGuestTypes(player2, mship)) {

                  counts++;     // bump # of guests
                  found = true;
               }
               if (userg3.equals( username ) && Utilities.checkWingedFootGuestTypes(player3, mship)) {

                  counts++;     // bump # of guests
                  found = true;
               }
               if (userg4.equals( username ) && Utilities.checkWingedFootGuestTypes(player4, mship)) {

                  counts++;     // bump # of guests
                  found = true;
               }
               if (userg5.equals( username ) && Utilities.checkWingedFootGuestTypes(player5, mship)) {

                  counts++;     // bump # of guests
                  found = true;
               }

               if (found) {
                   if(rwd){
                       out.println("<"+tags.tr+" class=\"rwdTr\">");
                        String[] players = {player1,player2,player3,player4,player5};
                        out.print("<"+tags.td+" class=\"rwdTd sT\"><div class=\"time_slot\">" + ndate + " " + SystemUtils.getSimpleTime(time) + "</div></"+tags.td+">");
                        out.print("<"+tags.td+" class=\"rwdTd sN\">"+course+"</"+tags.td+">");
                        out.print("<"+tags.td+" class=\"rwdTd sP\">");
                        for(int i2 = 0; i2 < players.length; i2++){
                            if(!players[i2].equals("")){
                                out.print("<div");
                                /*
                                if(players[i2].equals( name )){
                                    out.print(" class=\"me\"");
                                }
                                 */
                                out.print(">");
                                out.print("<span>"+players[i2]+"</span>");
                                out.print("</div>");
                            }
                        }
                        out.print("</"+tags.td+">");
                        out.println("</"+tags.tr+">");
                   } else {
                       out.println("<tr><td align=right>" + fdate + "</td><td align=center>" + SystemUtils.getSimpleTime(time) + "</td><td align=center>" + course + "</td>" +
                            "<td>" + player1 + "</td><td>" + player2 + "</td><td>" + player3 + "&nbsp;</td><td>" + player4 + "&nbsp;</td><td>" + player5 + "&nbsp;</td></tr>");
                   }
               }

            }      // end of WHILE

            rs.close();
            pstmt.close();

         } // end loop of members w/ same mNum
         pstmt4.close();


        if (new_skin) out.println("</"+tags.tbody+">");
        
        out.println("</"+tags.table+">");
        
        out.println("<br><br>");
        
        if (new_skin) {
            
              out.println("<"+tags.table+" class=\"rwdTable standard_list_table\">");
              out.println("  <"+tags.thead+" class=\"rwdThead\"><"+tags.tr+" class=\"rwdTr\">");
              out.println("  <"+tags.th+" class=\"rwdTh\"> </"+tags.th+">");
              out.println("  <"+tags.th+" class=\"rwdTh\">Allowed</"+tags.th+">");
              out.println("  <"+tags.th+" class=\"rwdTh\">Used</"+tags.th+">");
              out.println("  <"+tags.th+" class=\"rwdTh\">Remaining</"+tags.th+">");
              out.println(" </"+tags.tr+"></"+tags.thead+">");
              out.println(" <"+tags.tbody+" class=\"rwdTbody\">");                             
              out.println("<"+tags.tr+" class=\"rwdTr\"><"+tags.td+" class=\"rwdTd\">Golf Year</"+tags.td+"><"+tags.td+" class=\"rwdTd\">" + maxg + "</"+tags.td+"><"+tags.td+" class=\"rwdTd\">" + counts + "</"+tags.td+"><"+tags.td+" class=\"rwdTd\">" + (maxg - counts) + "</"+tags.td+"></"+tags.tr+">");
              out.println("</"+tags.tbody+">");
              out.println("</"+tags.table+">");
              out.println("<br><p align=center>The Golf Year for Guest Quotas is defined as May 1st - October 31st.</p>");
            
        } else {
        
            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"4\">");
            out.println("<tr align=center bgcolor=\"#336633\" style=\"color: white;font-weight: bold;text-decoration: underline\"><td></td><td>Allowed</td><td>Used</td><td>Remaining</td></tr>");
            out.println("<tr align=center><td align=right bgcolor=\"#336633\" style=\"color: white;font-weight: bold;text-decoration: underline\">Golf Year</td><td>" + maxg + "</td><td>" + counts + "</td><td>" + (maxg - counts) + "</td></tr>");
            out.println("</table>");

            out.println("<br><p align=center>The Golf Year for Guest Quotas is defined as May 1st - October 31st.</p>");

            out.println("<center>");
            out.println("<form method=\"get\" action=\"Member_announce\">");
            out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;width:90px\">");
            out.println("</form></center>");
        }

        out.println("<!-- Hdate3=" + Hdate3 + " | Hdate1=" + Hdate1 + " -->");
        out.println("<!-- sdate=" + sdate + " | edate=" + edate + " -->");
        out.println("<!-- members found w/ this member numer =" + members + " -->");
        
        //
        //  End of HTML page
        //
        if (new_skin) {
            if(rwd){
                // Nothing?
                // Looks like the desktop markup, below, has too many closing divs.
            } else {
                out.println("<br><br><div style=\"text-align:center;\">");
               out.println("<input id=\"back\" name=\"back\" type=\"button\" value=\"Home\" onclick=\"window.location.href='Member_announce'\" />");
               out.println("</div></div>"); // end reservations
               out.println("</center></div></div>");
            }
           
           Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page       
            
        } else {
        
            out.println("</body></html>");
        }
        out.close();

    }
    catch (Exception e) {

        SystemUtils.buildDatabaseErrMsg("Error building Winged Foot guest report.", e.toString(), out, false);
    }    

    
    
    //
    //  The following is the old report - prior to changes on 3/26/2010
    //
    /*
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
        //  Build the HTML page to display search results
        //
        out.println(SystemUtils.HeadTitle("Member Search Page"));
        out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
        SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
        out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
        
        // display report header
        out.println("<h2>Guest Rounds Report</h2>");
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
                           "WHERE date < ? AND date > ? AND " +
                           "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

                        pstmt.clearParameters();
                        pstmt.setLong(1, Hdate3);           // Labor Day
                        pstmt.setLong(2, Hdate1);           // Memorial Day
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

                           if (userg1.equals( username ) && Utilities.checkWingedFootGuestTypes(player1)) {

                              counts++;     // bump # of guests
                              found = true;
                           }
                           if (userg2.equals( username ) && Utilities.checkWingedFootGuestTypes(player2)) {

                              counts++;     // bump # of guests
                              found = true;
                           }
                           if (userg3.equals( username ) && Utilities.checkWingedFootGuestTypes(player3)) {

                              counts++;     // bump # of guests
                              found = true;
                           }
                           if (userg4.equals( username ) && Utilities.checkWingedFootGuestTypes(player4)) {

                              counts++;     // bump # of guests
                              found = true;
                           }
                           if (userg5.equals( username ) && Utilities.checkWingedFootGuestTypes(player5)) {

                              counts++;     // bump # of guests
                              found = true;
                           }
                           
                           if (found) out.println("<tr><td align=right>" + fdate + "</td><td align=center>" + SystemUtils.getSimpleTime(time) + "</td><td align=center>" + course + "</td>" +
                                        "<td>" + player1 + "</td><td>" + player2 + "</td><td>" + player3 + "&nbsp;</td><td>" + player4 + "&nbsp;</td><td>" + player5 + "&nbsp;</td></tr>");
                           
                        }      // end of WHILE
                        rs.close();
                        pstmt.close();

                        //out.println("<tr><td colspan=8 align=center><b>Scheduled In Season Tee Times</b></td></tr>");
                        
                        pstmt = con.prepareStatement (
                           "SELECT player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, date, time, courseName, DATE_FORMAT(date, \"%b. %e, %y\") AS fdate " +
                           "FROM teecurr2 " +
                           "WHERE date < ? AND date > ? AND " +
                           "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

                        pstmt.clearParameters();
                        pstmt.setLong(1, Hdate3);                // Labor Day
                        pstmt.setLong(2, Hdate1);                // Memorial Day
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

                           if (userg1.equals( username ) && Utilities.checkWingedFootGuestTypes(player1)) {

                              counts++;     // bump # of guests
                              found = true;
                           }
                           if (userg2.equals( username ) && Utilities.checkWingedFootGuestTypes(player2)) {

                              counts++;     // bump # of guests
                              found = true;
                           }
                           if (userg3.equals( username ) && Utilities.checkWingedFootGuestTypes(player3)) {

                              counts++;     // bump # of guests
                              found = true;
                           }
                           if (userg4.equals( username ) && Utilities.checkWingedFootGuestTypes(player4)) {

                              counts++;     // bump # of guests
                              found = true;
                           }
                           if (userg5.equals( username ) && Utilities.checkWingedFootGuestTypes(player5)) {

                              counts++;     // bump # of guests
                              found = true;
                           }
                           
                           if (found) out.println("<tr><td align=right>" + fdate + "</td><td align=center>" + SystemUtils.getSimpleTime(time) + "</td><td align=center>" + course + "</td>" +
                                        "<td>" + player1 + "</td><td>" + player2 + "</td><td>" + player3 + "&nbsp;</td><td>" + player4 + "&nbsp;</td><td>" + player5 + "&nbsp;</td></tr>");
                           
                        }      // end of WHILE

                        rs.close();
                        pstmt.close();

                        //out.println("<tr><td colspan=8 align=center><b>Scheduled Tee Times</b></td></tr>");
                        
                        //
                        //   Check teecurr and teepast for other guest times for this member for the Golf Year
                        //
                        pstmt = con.prepareStatement (
                           "SELECT player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, date, time, courseName, DATE_FORMAT(date, \"%b. %e, %y\") AS fdate " +
                           "FROM teepast2 " +
                           "WHERE date < ? AND date > ? AND !(date < ? AND date > ?) AND " +
                           "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

                        pstmt.clearParameters();
                        pstmt.setLong(1, edate);           // 10/15
                        pstmt.setLong(2, sdate);           // 4/14
                        pstmt.setLong(3, Hdate3);          // Labor Day
                        pstmt.setLong(4, Hdate1);          // Memorial Day
                        pstmt.setString(5, username);
                        pstmt.setString(6, username);
                        pstmt.setString(7, username);
                        pstmt.setString(8, username);
                        pstmt.setString(9, username);
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

                           if (userg1.equals( username ) && Utilities.checkWingedFootGuestTypes(player1)) {

                              countg++;     // bump # of guests
                              found = true;
                           }
                           if (userg2.equals( username ) && Utilities.checkWingedFootGuestTypes(player2)) {

                              countg++;     // bump # of guests
                              found = true;
                           }
                           if (userg3.equals( username ) && Utilities.checkWingedFootGuestTypes(player3)) {

                              countg++;     // bump # of guests
                              found = true;
                           }
                           if (userg4.equals( username ) && Utilities.checkWingedFootGuestTypes(player4)) {

                              countg++;     // bump # of guests
                              found = true;
                           }
                           if (userg5.equals( username ) && Utilities.checkWingedFootGuestTypes(player5)) {

                              countg++;     // bump # of guests
                              found = true;
                           }
                           
                           if (found) out.println("<tr><td align=right>" + fdate + "</td><td align=center>" + SystemUtils.getSimpleTime(time) + "</td><td align=center>" + course + "</td>" +
                                        "<td>" + player1 + "</td><td>" + player2 + "</td><td>" + player3 + "&nbsp;</td><td>" + player4 + "&nbsp;</td><td>" + player5 + "&nbsp;</td></tr>");
                           
                        }      // end of WHILE

                        pstmt.close();

                        pstmt = con.prepareStatement (
                           "SELECT player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, date, time, courseName, DATE_FORMAT(date, \"%b. %e, %y\") AS fdate " +
                           "FROM teecurr2 " +
                           "WHERE date < ? AND date > ? AND !(date < ? AND date > ?) AND " +
                           "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

                        pstmt.clearParameters();
                        pstmt.setLong(1, edate);
                        pstmt.setLong(2, sdate);
                        pstmt.setLong(3, Hdate3);          // Labor Day
                        pstmt.setLong(4, Hdate1);          // Memorial Day
                        pstmt.setString(5, username);
                        pstmt.setString(6, username);
                        pstmt.setString(7, username);
                        pstmt.setString(8, username);
                        pstmt.setString(9, username);
                        rs = pstmt.executeQuery();

                        while (rs.next()) {

                           found = false;
                            
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

                           if (userg1.equals( username ) && Utilities.checkWingedFootGuestTypes(player1)) {

                              countg++;     // bump # of guests
                              found = true;
                           }
                           if (userg2.equals( username ) && Utilities.checkWingedFootGuestTypes(player2)) {

                              countg++;     // bump # of guests
                              found = true;
                           }
                           if (userg3.equals( username ) && Utilities.checkWingedFootGuestTypes(player3)) {

                              countg++;     // bump # of guests
                              found = true;
                           }
                           if (userg4.equals( username ) && Utilities.checkWingedFootGuestTypes(player4)) {

                              countg++;     // bump # of guests
                              found = true;
                           }
                           if (userg5.equals( username ) && Utilities.checkWingedFootGuestTypes(player5)) {

                              countg++;     // bump # of guests
                              found = true;
                           }
                           
                           if (found) out.println("<tr><td align=right>" + fdate + "</td><td align=center>" + SystemUtils.getSimpleTime(time) + "</td><td align=center>" + course + "</td>" +
                                        "<td>" + player1 + "</td><td>" + player2 + "</td><td>" + player3 + "&nbsp;</td><td>" + player4 + "&nbsp;</td><td>" + player5 + "&nbsp;</td></tr>");
                           
                        }      // end of WHILE

                        pstmt.close();

                     } // end loop of members w/ same mNum
                     pstmt4.close();


                     

        out.println("</table>");
        
        out.println("<br><br>");
        
        out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"4\">");
        out.println("<tr align=center bgcolor=\"#336633\" style=\"color: white;font-weight: bold;text-decoration: underline\"><td></td><td>Allowed</td><td>Used</td><td>Remaining</td></tr>");
        out.println("<tr align=center><td align=right bgcolor=\"#336633\" style=\"color: white;font-weight: bold;text-decoration: underline\">In Season</td><td>" + maxs + "</td><td>" + counts + "</td><td>" + (maxs - counts) + "</td></tr>");
        out.println("<tr align=center><td align=right bgcolor=\"#336633\" style=\"color: white;font-weight: bold;text-decoration: underline\">Overall</td><td>" + maxg + "</td><td>" + (countg + counts) + "</td><td>" + (maxg - (countg + counts)) + "</td></tr>");
        out.println("</table>");
        
       
       // out.println("<p align=center><b>" +
       //         "Max Guests Allowed Overall: " + maxg + "<br>" +
       //         "Max Guests Allowed in Season: " + maxs + "<br>" +
       //         "<br><br>" +
       //         "Guests Rounds Found Overall: " + countg + "<br>" +
       //         "Guests Rounds Found in Season: " + counts + "" +
       //         "</b></p>");
       
        
        out.println("<br><p align=center>Season is defined as Memorial Day thru Labor Day.<br>" +
                "Overall is defined as Opening Day Through Novemeber 1.</p>");
                  
        out.println("<center>");
        out.println("<form method=\"get\" action=\"Member_announce\">");
        out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;width:90px\">");
        out.println("</form></center>");

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
     */ 
    
 }      // end of Winged Foot Guest Quota Report
 
 
 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>You must enter the username or First & Last names.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 // *********************************************************
 // Member does not exists
 // *********************************************************

 private void noMem(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, the member you specified does not exist in the database.<BR>");
   out.println("<BR>Please check your data and try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

 }

 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact your proshop.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

 }

}
