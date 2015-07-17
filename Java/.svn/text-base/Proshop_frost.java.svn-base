/***************************************************************************************
 *   Proshop_frost:  This servlet will process the 'Frost Delay' request from
 *                   the Proshop's Tee Sheet page (Control Panel).
 *
 *
 *   called by:  Proshop_sheet (doGet)
 *               Proshop_frost (doPost)
 *
 *
 *   parms passed (on doGet):  
 *                  'course'      = name of the course for this sheet
 *
 *      secondary calls (from self to doPost):
 *                   (refer to dopost method)
 *
 *
 *   created: 12/01/2004   Bob P. for The Lakes CC
 *
 *   last updated:
 *
 *        3/02/10   Allow Frost Deley to work on any day not just today (now excepts index)
 *        9/02/08   Javascript compatability updates
 *        7/18/08   Added limited access proshop users checks
 *        4/25/08   PTS  Sanity check the incoming minutes value
 *        1/16/07   RDP  Set auto_blocked when changing the times of tee times so that SystemUtils
 *                      will not block the times that are now included in a blocker.
 *        1/05/05   RDP  Ver 5 - made this a feature for all clubs.
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.zip.*;
import java.sql.*;
import java.lang.Math;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;


public class Proshop_frost extends HttpServlet {


   String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

   static String host = SystemUtils.HOST;

   static String efrom = SystemUtils.EFROM;

   static String header = SystemUtils.HEADER;

   static String trailer = SystemUtils.TRAILER;


   static String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   
 //****************************************************************************
 // Process the request from Proshop_sheet 
 //
 //   This doGet method will prompt the user for frost delay information.
 //
 //   parms:
 //           course - name of course
 //
 //****************************************************************************
 //

 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   Statement stmt = null;
   ResultSet rs = null;

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();                                         // normal output stream

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      out.println(SystemUtils.HeadTitle("Access Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>System Access Error</H3>");
      out.println("<BR><BR>You have entered this site incorrectly or have lost your session cookie.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   Connection con = SystemUtils.getCon(session);                     // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "TS_CTRL_FROST", con, out)) {
       SystemUtils.restrictProshop("TS_CTRL_FROST", out);
       return;
   }
           
   String club = (String)session.getAttribute("club");      // get club name
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator

   int lottery = Integer.parseInt(templott);

   //
   //  Get the golf course name requested and the name of the day passed
   //
   String course = req.getParameter("course");
   if (course == null) course = "";


   //
   // Get the index of the day for the frost delay
   //
   int index = 0;
   String sindex = req.getParameter("index");
   
   try { index = Integer.parseInt(sindex); 
   } catch (Exception ignore) { }


   //
   //  Get the date - frost delays can only be done for today.
   //
   Calendar cal = new GregorianCalendar();
   cal.add(Calendar.DATE, index);                       // roll ahead

   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH) + 1;
   int day = cal.get(Calendar.DAY_OF_MONTH);

   int date = (year * 10000) + (month * 100) + day;



   //
   //  Build the HTML page
   //
   out.println("<html>");
   out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
   out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
   out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
   out.println("<title>Proshop Frost Delay</title>");
   out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("function cursor() { document.forms['f'].minutes.focus(); }");
   out.println("// -->");
   out.println("</script>");

   out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");

   SystemUtils.getProshopSubMenu(req, out, lottery);

   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<br>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\"><b>Frost Delay</b></font><br><br>");
      out.println("Complete the following information and click Continue to adjust the specified days tee sheet.<br>");
      out.println("This will shift all tee times for the day ahead or back the specified number of minutes.<br>");
      out.println("<br><b>Warning:</b> There is a slight possibility that one or more tee times could");
      out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("end up in the wrong time slot (if a member updates a tee time at a precise moment).");
   out.println("</font></td></tr></table><br>");

   out.println("<table border=\"1\" cols=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\" align=\"center\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_frost\" method=\"post\" target=\"bot\" name=\"f\">");

      out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");

      out.println("<tr>");
         out.println("<td width=\"600\">");
         out.println("<center><h3>Date:&nbsp;&nbsp;" + day_table[cal.get(Calendar.DAY_OF_WEEK)] + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</h3></center>");
         out.println("<font size=\"2\">");
         out.println("<p align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;Number of Minutes to Adjust Tee Times (length of frost delay):&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"minutes\" size=\"3\" maxlength=\"3\">");
            out.println("&nbsp;&nbsp;(specify 1 - 240)");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;Shift tee times Ahead (frost delay) or Back (to undo all or part of a frost delay)?:&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"direction\">");
           out.println("<option value=\"Ahead\">Ahead</option>");
           out.println("<option value=\"Back\">Back</option>");
         out.println("</select>");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;&nbsp;&nbsp;Would you like email notifications sent to the members?:&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"emailOpt\">");
           out.println("<option value=\"Yes\">Yes</option>");
           out.println("<option value=\"No\">No</option>");
         out.println("</select><br>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;(Note: emails are only sent to members if their tee time is at least an hour from now)");
         out.println("</p>");

         out.println("<p align=\"center\">");
           out.println("<input type=\"submit\" value=\"Continue\">");
         out.println("</p>");
      out.println("</form>");

      out.println("<form action=\"/" +rev+ "/servlet/Proshop_sheet\" method=\"post\" target=\"bot\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<p align=\"center\">");
           out.println("<input type=\"submit\" value=\"Cancel - Return\">");
         out.println("</p>");
      out.println("</form>");
           
         out.println("</font>");
         out.println("</td>");
      out.println("</tr>");
   out.println("</table>");
   out.println("</font>");
   //
   //  End of HTML page
   //
   out.println("</center></body></html>");
   out.close();

 }   // end of doGet


 //************************************************************************************
 //
 //   **** doPost **** 
 //
 //   This method receives control from the form in doGet above.
 //
 //
 //
 //************************************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   ResultSet rs = null;
   PreparedStatement pstmt1 = null;
   PreparedStatement pstmt2 = null;

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);                    // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Get this session's username (to be saved in teecurr)
   //
   String user = (String)session.getAttribute("user");
   String club = (String)session.getAttribute("club");
     
   String msg = "";
     
   int time = 0;
   int time2 = 0;
   int hour = 0;
   int minute = 0;
   int fb = 0;
   int count = 0;
   int total = 0;
   int min = 0;
   int date = 0;
     
   boolean error = false;


   //
   //  Get the parms passed
   //
   String course = req.getParameter("course");       
   String minutes = req.getParameter("minutes");           // # of minutes to adjust
   String emailOpt = req.getParameter("emailOpt");
   String direction = req.getParameter("direction");
   String sdate = req.getParameter("date");
   String index = req.getParameter("index");
     
   try { min = Integer.parseInt(minutes);
   } catch (Exception ignore) { }

   try { date = Integer.parseInt(sdate);
   } catch (Exception ignore) { }
   
   
   if (min < 1 || min > 240) {        // validate parm

      out.println(SystemUtils.HeadTitle("Invalid Parm Value Received"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Invalid Minutes Value Received</H3>");
      out.println("<BR><BR>The Number of Minutes must be between 1 and 240.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

/*
   //
   //  Get today's date - frost delays can only be done for today.
   //
   Calendar cal = new GregorianCalendar();             // get current date 
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);

   month++;                            // month starts at zero

   long date = (year * 10000) + (month * 100) + day;    // create a date field of yyyymmdd
*/

   try {
     
      //
      //  Set all tee times for this date as busy (set proshop as the user to prevent members from completing any changes)
      //
      msg = "Proshop_frost error setting tee times busy. ";
     
      pstmt1 = con.prepareStatement (
         "UPDATE teecurr2 SET in_use = 1, in_use_by = ? WHERE date = ? AND courseName = ?");

      pstmt1.clearParameters();          // clear the parms
      pstmt1.setString(1, user);         // put the parm in pstmt1
      pstmt1.setLong(2, date);
      pstmt1.setString(3, course);
      pstmt1.executeUpdate();            // execute the prepared stmt

      pstmt1.close();
  

      if (direction.equals( "Ahead" )) {         // if shifting ahead for frost delay
        
         //
         //  Remove the empty tee times at the end of the day - using the # of minutes
         //
         error = removeTimes(out, min, date, course, con);

         if (error == true) {

            return;
         }
      }

      //
      //  Get each tee time for this day, starting with the latest, and change the time
      //
      msg = "Proshop_frost error getting all times for day. ";

      //
      //  Statements for back or ahead
      //
      String state1 = "SELECT time, fb FROM teecurr2 WHERE date = ? AND courseName = ? ORDER BY time DESC";
      String state2 = "SELECT time, fb FROM teecurr2 WHERE date = ? AND courseName = ? ORDER BY time";

      if (direction.equals( "Ahead" )) {         // if shifting ahead for frost delay

         pstmt1 = con.prepareStatement (state1);        // list in descending order (start with last tee time)

      } else {
         
         pstmt1 = con.prepareStatement (state2);       // list in opposite order (ascending)
      }

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, date);         // put the parm in pstmt1
      pstmt1.setString(2, course);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         time = rs.getInt( "time" );
         fb = rs.getInt( "fb" );

         //
         //  Adjust the time according to the minutes value
         //
         time2 = adjustTime(min, time, direction);

         hour = time2 / 100;
         minute = time2 - (hour * 100);

         //
         //  Update the tee time using the new time value (set autoblocked so SystemUtils does not block these times - if blocker defined)
         //
         msg = "Proshop_frost error changing the tee times. ";

         pstmt2 = con.prepareStatement (
            "UPDATE teecurr2 SET hr = ?, min = ?, time = ?, auto_blocked = 1 " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt2.clearParameters();          // clear the parms
         pstmt2.setInt(1, hour);
         pstmt2.setInt(2, minute);
         pstmt2.setInt(3, time2);
         pstmt2.setLong(4, date);
         pstmt2.setInt(5, time);
         pstmt2.setInt(6, fb);
         pstmt2.setString(7, course);
         count = pstmt2.executeUpdate();            // execute the prepared stmt

         total += count;                       // track number of updated records
           
         pstmt2.close();
      }
      pstmt1.close();


      //
      //  Done - set all tee times available
      //
      msg = "Proshop_frost error setting tee times available. ";

      pstmt1 = con.prepareStatement (
         "UPDATE teecurr2 SET in_use = 0 WHERE date = ? AND courseName = ?");

      pstmt1.clearParameters();          // clear the parms
      pstmt1.setLong(1, date);
      pstmt1.setString(2, course);
      pstmt1.executeUpdate();            // execute the prepared stmt

      pstmt1.close();

   }
   catch (Exception e1) {

      dbError(out, e1, 0, course, msg);
      return;
   }

   //
   //  Tee Times adjusted
   //
   out.println(SystemUtils.HeadTitle("Frost Delay Completed"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>The Frost Delay Has Been Completed</H3><BR>");
   out.println("<BR><BR>Thank you, " +total+ " tee times have been adjusted.");
   out.println("<br><br>");
   if (direction.equals( "Back" )) {         // if shifting back to undo frost delay
      out.println("Tee times have NOT been added to the end of the day.  Use the Edit Tee Sheet <br>");
      out.println("feature to insert new times if necessary.<br><br>");
   }
   out.println("<form action=\"/" +rev+ "/servlet/Proshop_sheet\" method=\"post\" target=\"bot\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
   out.println("<input type=\"submit\" value=\"Return\">");
   out.println("</form>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

   //
   //  Send email notifications to all members on the tee sheet if email selected
   //
   if (emailOpt.equalsIgnoreCase( "yes" )) {
     
      sendEmails(date, course, con);
   }

 }  // end of doPost


 // *********************************************************
 //  Remove latest times
 // *********************************************************

 private boolean removeTimes(PrintWriter out, int min, long date, String course, Connection con) {


   ResultSet rs = null;
   PreparedStatement pstmt1 = null;

   boolean error = false;
     
   String msg = "Proshop_frost error removing times from end of day. ";

   int time = 0;
  
   try {
     
      //
      //  Get the latest time for this date
      //
      pstmt1 = con.prepareStatement (
         "SELECT MAX(time) " +
         "FROM teecurr2 WHERE date = ? AND courseName = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, date);         // put the parm in pstmt1
      pstmt1.setString(2, course);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         time = rs.getInt(1);
      }
      pstmt1.close();

      //
      //  Adjust the time according to the minutes value
      //
      time = adjustTimeBack(min, time);

      //
      //  Remove all times starting with the adjusted time (only empty times)
      //
      pstmt1 = con.prepareStatement (
         "DELETE FROM teecurr2 " +
         "WHERE date = ? AND time > ? AND courseName = ? AND " +
         "player1 = '' AND player2 = '' AND player3 = '' AND player4 = '' AND player5 = ''");

      pstmt1.clearParameters();          // clear the parms
      pstmt1.setLong(1, date);
      pstmt1.setInt(2, time);
      pstmt1.setString(3, course);
      pstmt1.executeUpdate();            // execute the prepared stmt

      pstmt1.close();

   }
   catch (Exception e1) {

      dbError(out, e1, 0, course, msg);
      error = true;
   }

   return(error);
 }
   

 // *********************************************************
 //  Adjust the time forward or back 'x' minutes
 // *********************************************************

 private int adjustTime(int mins, int time, String direction) {


   int time2 = 0;
   int hr = 0;
   int min = 0;

   if (direction.equals( "Ahead" )) {
     
      //
      //  Add 'mins' minutes to the time value in 'time'
      //
      hr = time / 100;             // get hour value
      min = time - (hr * 100);     // get minutes

      min += mins;                 // add the number of minutes to the time value (minutes)

      while (min > 59) {

         hr++;               // add one hour
         min -= 60;          // take it off the minutes
      }

      time2 = (hr * 100) + min;    // create new time value
        
    } else {

      //
      //  Subtract 'mins' minutes from the time value in 'time'
      //
      time2 = adjustTimeBack(mins, time);
   }

   return(time2);
 }


 // *********************************************************
 //  Adjust the time backward 'x' minutes
 // *********************************************************

 private int adjustTimeBack(int mins, int time) {


   int time2 = 0;
   int hr = 0;
   int min = 0;

   //
   //  Subtract 'mins' minutes from the time value in 'time'
   //
   hr = time / 100;             // get hour value
   min = time - (hr * 100);     // get minutes

   while (mins > 0) {

      if (mins > 59) {
        
         hr--;                // go back one hour
         mins -= 60;
           
      } else {
        
         if (min == mins) {
           
            min = 0;
              
         } else {
           
            if (min > mins) {
              
               min -= mins;
                 
            } else {         // min < mins
              
               mins -= min;               
               hr--;
               min = 60 - mins;     
            }
         }
         mins = 0;             // done
      } 
   }

   time2 = (hr * 100) + min;    // create new time value

   return(time2);
 }


 // *********************************************************
 //  Send Email Notifications to all members
 // *********************************************************

 private void sendEmails(long date, String course, Connection con) {


   ResultSet rs = null;
   PreparedStatement pstmt1 = null;

   int hr = 0;
   int min = 0;
   int time = 0;

   String msg = "Proshop_frost error sending emails. ";


   try {

      //
      //  allocate a parm block to hold the email parms
      //
      parmEmail parme = new parmEmail();          // allocate an Email parm block

      //
      //  Set the values in the email parm block
      //
      parme.type = "frost";         // type = Frost Delay
      parme.emailMod = 1;           // sub-type = modification
      parme.date = date;
      parme.user = "proshop";
      parme.course = course;
        
  
      //
      //  Get the latest time for this date
      //
      pstmt1 = con.prepareStatement (
         "SELECT mm, dd, yy, day, hr, min, time, player1, player2, player3, player4, " +
         "username1, username2, username3, username4, p1cw, p2cw, p3cw, p4cw, " +
         "fb, player5, username5, p5cw, p91, p92, p93, p94, p95 " +
         "FROM teecurr2 WHERE date = ? AND courseName = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, date);         // put the parm in pstmt1
      pstmt1.setString(2, course);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         parme.mm = rs.getInt(1);
         parme.dd = rs.getInt(2);
         parme.yy = rs.getInt(3);
         parme.day = rs.getString(4);
         hr = rs.getInt(5);
         min = rs.getInt(6);
         parme.time = rs.getInt(7);
         parme.player1 = rs.getString(8);
         parme.player2 = rs.getString(9);
         parme.player3 = rs.getString(10);
         parme.player4 = rs.getString(11);
         parme.user1 = rs.getString(12);
         parme.user2 = rs.getString(13);
         parme.user3 = rs.getString(14);
         parme.user4 = rs.getString(15);
         parme.pcw1 = rs.getString(16);
         parme.pcw2 = rs.getString(17);
         parme.pcw3 = rs.getString(18);
         parme.pcw4 = rs.getString(19);
         parme.fb = rs.getInt(20);
         parme.player5 = rs.getString(21);
         parme.user5 = rs.getString(22);
         parme.pcw5 = rs.getString(23);
         parme.p91 = rs.getInt(24);
         parme.p92 = rs.getInt(25);
         parme.p93 = rs.getInt(26);
         parme.p94 = rs.getInt(27);
         parme.p95 = rs.getInt(28);

         //
         //  Send the email
         //
         sendEmail.sendIt(parme, con);      // in common

      }
      pstmt1.close();

   }
   catch (Exception e1) {
   }

 }


 // *********************************************************
 //  Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e1, int index, String course, String eMsg) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>" + eMsg + " Exc= " + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_sheet?index=" +index+ "&course=" +course+ "\">");
      out.println("Return to Tee Sheet</a></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }

}
