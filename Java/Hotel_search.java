/***************************************************************************************
 *   Hotel_search:  This servlet will process the 'search for Hotel guests' request from
 *                      Hotel's searchmain page.  It will use the name provided to search
 *                      teecurr for any matches (current tee times).
 *
 *                      Caller only provides the last name, or a portion of it.
 *
 *
 *   called by:  hotel_search.htm (doPost)
 *               hotel_mainleft.htm (doGet)
 *
 *   created: 11/19/2003   Bob P.
 *
 *   last updated:
 *
 *       12/02/09   Allow for Saudi Arabia time - adjustTime.
 *        1/24/05   Ver 5 - change club2 to club5.
 *     01/07/2004   JAG  Modified to match new color scheme
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Hotel_search extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //
 //*****************************************************************
 // Process call from hotel_mainleft.htm (or My reservations tab)
 //*****************************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/hotel_mainleft.htm\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   String omit = "";
   String ampm = "";
   String day = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String sfb = "";
   String submit = "";
   String name = "";
   String course = "";
   String rest5 = "";
   String rest5_color = "";
   String zone = "";
   String lname = "";
   String rest = "";

   long date = 0;
   long edate = 0;
   long cdate = 0;
   long c_date = 0;

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
   int sdays = 0;
   int sdtime = 0;
   int edays = 0;
   int edtime = 0;
   int pdays = 0;
   int pdtime = 0;
   int slots = 0;
   int advance_days = 0;
   int fives = 0;
   int fivesomes = 0;
   int signUp = 0;
   int fb = 0;

   String user = (String)session.getAttribute("user");   // get username

   boolean events = false;

   //
   //   Get options for this club
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi, adv_zone FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);
         zone = rs.getString(2);
      }
      stmt.close();

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

      out.println(SystemUtils.HeadTitle("Hotel My Tee Times - Error"));
      out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
      out.println("<CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your golf shop (provide this information).");
      out.println("<br><br><a href=\"/" +rev+ "/hotel_mainleft.htm\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   yy = cal.get(Calendar.YEAR);
   mm = cal.get(Calendar.MONTH);
   dd = cal.get(Calendar.DAY_OF_MONTH);
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

         yy = cal.get(Calendar.YEAR);
         mm = cal.get(Calendar.MONTH);
         dd = cal.get(Calendar.DAY_OF_MONTH);

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         cal.add(Calendar.DATE,-1);                     // get yesterday's date

         yy = cal.get(Calendar.YEAR);
         mm = cal.get(Calendar.MONTH);
         dd = cal.get(Calendar.DAY_OF_MONTH);
      }
   }

   cal_hourDay = ctime / 100;                      // get adjusted hour
   cal_min = ctime - (cal_hourDay * 100);          // get minute value

   mm = mm + 1;                                    // month starts at zero

   cdate = (yy * 10000) + (mm * 100) + dd;      // create a date field of yyyymmdd

   yy = 0;
   mm = 0;
   dd = 0;

   try {
      //
      // search for this user's tee times
      //
      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT date, mm, dd, yy, day, hr, min, time, player1, player2, player3, player4, fb, " +
         "player5, courseName, rest5, rest5_color " +
         "FROM teecurr2 WHERE orig_by = ? " +
         "ORDER BY date, time");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, user);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      //
      //   build the HTML page for the display
      //
      out.println(SystemUtils.HeadTitle("Hotel Tees Page"));
      out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" align=\"center\" valign=\"top\">");   // table for main page

      out.println("<tr><td align=\"center\">");

      out.println("<font size=\"3\">");
      out.println("<b>Your Guests' Current Tee Times</b></font>");
      out.println("<font size=\"2\"><br>");

      if (rs.next()) {

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
                     out.println("<font color=\"ffffff\" size=\"2\">");
                     out.println("<u><b>Date</b></u>");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font color=\"ffffff\" size=\"2\">");
                     out.println("<u><b>Time</b></u>");
                     out.println("</font></td>");

                  if (multi != 0) {

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"ffffff\" size=\"2\">");
                     out.println("<u><b>Course Name</b></u>");
                     out.println("</font></td>");
                  }

                  out.println("<td align=\"center\">");
                     out.println("<font color=\"ffffff\" size=\"2\">");
                     out.println("<u><b>F/B</b></u>");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font color=\"ffffff\" size=\"2\">");
                     out.println("<u><b>Player 1</b></u>");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font color=\"ffffff\" size=\"2\">");
                     out.println("<u><b>Player 2</b></u>");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font color=\"ffffff\" size=\"2\">");
                     out.println("<u><b>Player 3</b></u>");
                     out.println("</font></td>");

                  out.println("<td align=\"center\">");
                     out.println("<font color=\"ffffff\" size=\"2\">");
                     out.println("<u><b>Player 4</b></u>");
                     out.println("</font></td>");

                  if (fivesomes != 0) {

                     out.println("<td align=\"center\">");
                     out.println("<font color=\"ffffff\" size=\"2\">");
                     out.println("<u><b>Player 5</b></u>");
                     out.println("</font></td>");
                  }
                  out.println("</tr>");

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
            player1 = rs.getString(9);
            player2 = rs.getString(10);
            player3 = rs.getString(11);
            player4 = rs.getString(12);
            fb = rs.getInt(13);
            player5 = rs.getString(14);
            course = rs.getString(15);
            rest5 = rs.getString(16);
            rest5_color = rs.getString(17);

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
            out.println("<form action=\"/" +rev+ "/servlet/Hotel_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
            out.println("<input type=\"hidden\" name=\"day\" value=" + day + ">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=999>");  // indicate from here

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
            }

            pstmt2.close();

            if ((fives != 0 ) && (rest5.equals( "" ))) {   // if 5-somes and not restricted

               out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
            } else {

               out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
            }

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println( day + "&nbsp;" + mm + "/" + dd + "/" + yy );
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (min < 10) {
               out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":0" + min + ampm + "\">");
            } else {
               out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + min + ampm + "\">");
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
            player1 = rs.getString(9);
            player2 = rs.getString(10);
            player3 = rs.getString(11);
            player4 = rs.getString(12);
            fb = rs.getInt(13);
            player5 = rs.getString(14);
            course = rs.getString(15);
            rest5 = rs.getString(16);
            rest5_color = rs.getString(17);

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
            out.println("<form action=\"/" +rev+ "/servlet/Hotel_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
            out.println("<input type=\"hidden\" name=\"day\" value=" + day + ">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"999\">");  // indicate from here

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
            }

            pstmt3.close();

            if ((fives != 0 ) && (rest5.equals( "" ))) {   // if 5-somes and not restricted

               out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
            } else {

               out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
            }

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println( day + "&nbsp;" + mm + "/" + dd + "/" + yy );
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (min < 10) {
               out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":0" + min + ampm + "\">");
            } else {
               out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + min + ampm + "\" >");
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

      } else {                                  // no tee times

         out.println("<br><p align=\"center\">Sorry, your guests have no tee time reservations scheduled at this time.</p>");

      }    // end of if

      pstmt1.close();

      out.println("</font></td>");
      out.println("</tr>");
      out.println("</table>");                   // end of table for main page

      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_mainleft.htm\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;\">");
      out.println("</input></form></font>");

      //
      //  End of HTML page
      //
      out.println("</center></font></body></html>");

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR><a href=\"/" +rev+ "/hotel_mainleft.htm\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
   }
 }     // end of doGet


 //
 //****************************************************
 // Process the form request from hotel_search.htm page
 //****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/hotel_mainleft.htm\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   String omit = "";
   String time_zone = "";
   String ampm = "";
   String day = "";
   String lname = "";
   String submit = "";
   String sfb = "";
   String course = "";
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

   String rest = "";

   long date = 0;
   long lottid = 0;

   int mm  = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int count = 0;
   int fb = 0;
   int multi = 0;
   int lottery = 0;
   int fives = 0;
   int fivesomes = 0;
   int slots = 0;
   int lstate = 0;
   int advance_days = 0;
   int sdays = 0;
   int sdtime = 0;
   int edays = 0;
   int edtime = 0;
   int pdays = 0;
   int ptime = 0;

   boolean available = false;

   //
   // Process request according to which 'submit' button was selected
   //
   //      'search' - a search request (only valid option for hotel
   //
   //  Called to search for a name - get the name and search for it
   //
   String name = req.getParameter("name");        //  name or portion of name

   int length = name.length();                    // get length of name requested

   //
   //  verify the required fields
   //
   if ((name.equals( omit )) || (length > 20)) {

      invData(out);    // inform the user and return
      return;
   }

   //
   // see if 5-somes are supported on any course at this club
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT fives FROM clubparm2");

      while (rs.next()) {

         fives = rs.getInt(1);

         if (fives != 0) {

            fivesomes = 1;
         }
      }

      stmt.close();

      //
      //  get the multiple course parm for this club
      //
      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi, lottery, adv_zone FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);
         lottery = rs.getInt(2);
         time_zone = rs.getString(3);

      }

      stmt.close();


      //
      //   Add a % to the name provided so search will match anything close
      //
      StringBuffer buf = new StringBuffer("%");
      buf.append( name );
      buf.append("%");
      String sname = buf.toString();

      //
      // use the name to search table
      //

      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT date, mm, dd, yy, day, hr, min, time, player1, player2, player3, player4, " +
         "username1, username2, username3, username4, " +
         "fb, player5, username5, courseName, rest5 " +
         "FROM teecurr2 " +
         "WHERE player1 LIKE ? OR player2 LIKE ? OR player3 LIKE ? OR player4 LIKE ? OR player5 LIKE ? " +
         "ORDER BY date, time");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, sname);
      pstmt1.setString(2, sname);
      pstmt1.setString(3, sname);
      pstmt1.setString(4, sname);
      pstmt1.setString(5, sname);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      //
      //   build the HTML page for the display
      //
      out.println(SystemUtils.HeadTitle("Hotel Search Page"));
      out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

      out.println("<font size=\"3\">");
      out.println("<p><b>Search Results for</b> " + name + "</p>");
      out.println("<br><b>Tee Times</b>.");
      out.println("</font><font size=\"2\">");
      out.println("<br><br><b>To join an open tee time</b>:  Just click on the box containing the time (2nd column).");
      out.println("</font><font size=\"1\">");
      out.println("<br>F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;F = Front 9, &nbsp;&nbsp;B = Back 9, &nbsp;&nbsp;O = Other<br><br>");
      out.println("</font><font size=\"2\">");

         out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"5\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<tr bgcolor=\"#336633\"><td>");
                  out.println("<font color=\"ffffff\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Date</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"ffffff\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Time</b></u></p>");
                  out.println("</font></td>");

               if (multi != 0) {

                  out.println("<td align=\"center\">");
                  out.println("<font color=\"ffffff\" size=\"2\">");
                  out.println("<u><b>Course Name</b></u>");
                  out.println("</font></td>");
               }

               out.println("<td>");
                  out.println("<font color=\"ffffff\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>F/B</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"ffffff\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Player 1</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"ffffff\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Player 2</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"ffffff\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Player 3</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font color=\"ffffff\" size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Player 4</b></u></p>");
                  out.println("</font></td>");

               if (fivesomes != 0) {

                  out.println("<td align=\"center\">");
                  out.println("<font color=\"ffffff\" size=\"2\">");
                  out.println("<u><b>Player 5</b></u>");
                  out.println("</font></td>");
               }
               out.println("</tr>");

      //
      //  Get each record and display it
      //
      count = 0;             // number of records found

      while (rs.next()) {

         count++;

         date = rs.getLong(1);
         mm = rs.getInt(2);
         dd = rs.getInt(3);
         yy = rs.getInt(4);
         day = rs.getString(5);
         hr = rs.getInt(6);
         min = rs.getInt(7);
         time = rs.getInt(8);
         player1 = rs.getString(9);
         player2 = rs.getString(10);
         player3 = rs.getString(11);
         player4 = rs.getString(12);
         username1 = rs.getString(13);
         username2 = rs.getString(14);
         username3 = rs.getString(15);
         username4 = rs.getString(16);
         fb = rs.getInt(17);
         player5 = rs.getString(18);
         username5 = rs.getString(19);
         course = rs.getString(20);
         rest = rs.getString(21);

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
         }

         pstmt3.close();

         ampm = " AM";
         if (hr == 12) {
            ampm = " PM";
         }
         if (hr > 12) {
            ampm = " PM";
            hr = hr - 12;    // convert to conventional time
         }

         available = false;

         if (player1.equals( "" )) {

            player1 = "&nbsp;";       // make it a space for table display

         } else {

            if (!username1.equals( "")) {    // if member

               player1 = "Member";           // hide the name
            }
         }

         if (player2.equals( "" )) {

            player2 = "&nbsp;";       // make it a space for table display

         } else {

            if (!username2.equals( "")) {    // if member

               player2 = "Member";           // hide the name
            }
         }

         if (player3.equals( "" )) {

            player3 = "&nbsp;";       // make it a space for table display

         } else {

            if (!username3.equals( "")) {    // if member

               player3 = "Member";           // hide the name
            }
         }

         if (player4.equals( "" )) {

            player4 = "&nbsp;";       // make it a space for table display

         } else {

            if (!username4.equals( "")) {    // if member

               player4 = "Member";           // hide the name
            }
         }

         if (fivesomes != 0) {

            if (player5.equals( "" )) {

               if (rest.equals( "" )) {          // if 5-somes are not restricted

                  player5 = "&nbsp;";       // make it a space for table display
               } else {
                  player5 = "N/A";          // player 5 N/A
               }
            } else {

               if (!username5.equals( "")) {    // if member

                  player5 = "Member";           // hide the name
               }
            }
         }

         if (username1.equals( "" ) && username2.equals( "" ) && username3.equals( "" ) && username4.equals( "" ) && username5.equals( "" )) {

            available = true;         // tee slot is available for hotel access if no members
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
         out.println("<form action=\"/" +rev+ "/servlet/Hotel_slot\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
         out.println("<input type=\"hidden\" name=\"day\" value=" + day + ">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"888\">");  // from here

         if (fives != 0 && rest.equals( "" )) {

            out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
         } else {

            out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
         }

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println( day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy );
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");

         if (available) {                      // if there is an empty slot available

            if (min < 10) {
               out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":0" + min + ampm + "\">");
            } else {
               out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + min + ampm + "\">");
            }
         } else {

            if (min < 10) {
               out.println(hr + ":0" + min + ampm);
            } else {
               out.println(hr + ":" + min + ampm);
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

         if (fivesomes != 0) {

            out.println("<td align=\"center\" bgcolor=\"white\">");
            out.println("<font size=\"2\">");
            out.println( player5 );
            out.println("</font></td>");
         }
            out.println("</form></tr>");

      }    // end of while

      pstmt1.close();

      out.println("</font></table>");

      if (count == 0) {

         out.println("<p align=\"center\">No records found for " + name + ".</p>");

      }

      out.println("</td></tr></table></td>");                // end of main page table & column
      out.println("</font>");
      out.println("<font size=\"2\">");

      out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_search.htm\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
      out.println("</input></form>");               // return to searchmain.htm

      out.println("</font>");

      //
      //  End of HTML page
      //
      out.println("</center></font></body></html>");

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<br><br><a href=\"/" +rev+ "/hotel_search.htm\">Return</a>");
      out.println("</CENTER></BODY></HTML>");

   }     // end of search function

 }   // end of doPost


 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><BR>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>You must enter the player's name, or some portion of it.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }

 // *********************************************************
 // Hotel Guest does not exist
 // *********************************************************

 private void noMem(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>No Tee Times Found</H3><BR>");
   out.println("<BR><BR>The guest you specified has no tee times scheduled for the next 30 days.<BR>");
   out.println("<BR>Please check your data and try again if you wish.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }

}
