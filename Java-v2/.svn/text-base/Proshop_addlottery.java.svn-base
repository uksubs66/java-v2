/***************************************************************************************     
 *   Proshop_addlottery:  This servlet will process the 'add lottery' request
 *                        from Proshop's lottery setup page.
 *
 *
 *   called by:  Proshop_lottery and self
 *
 *   created: 7/22/2003   Bob P.
 *
 *   last updated:
 *
 *       10/23/13   Updated minutes before and minutes afterward options to allow up to 10 hours to be selected.
 *       10/10/13   Add max days for recurrence options.
 *        9/05/13   Add options to allow proshop users and members to recur the lottery requests.
 *        1/08/12   Limit the X's allowed to 0-4 per tee time request (case 2104).
 *        2/23/12   Custom for Oakland Hills CC to add a 'max # of members' setting to limit how many members per request (case 2119).
 *        3/23/11   Add options for allowing X's in requests (cases 1202 & 1941).
 *        5/19/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        9/02/08   Javascript compatability updates
 *        8/11/08   Update to limited access user restrictions
 *        7/24/08   Added limited access proshop users checks
 *        4/22/08   Add mins-before and mins-after options (case #1459).
 *        2/16/08   Add help links for weighted lottery type definitions.
 *        6/22/06   Comment out the option to give preference to full groups (we don't do this).
 *        6/06/05   Add new weighted lottery type - "Weighted by Proximity".
 *        2/16/05   Ver 5 - start with today's date and use common function to set it.
 *        1/24/05   Ver 5 - change club2 to club5.
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.dateOpts;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;

public class Proshop_addlottery extends HttpServlet {
 
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //****************************************************
 // Process the request from Proshop_lottery for a new lottery
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;
   String omit = "";

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_LOTTERY", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_LOTTERY", out);
   }

   //
   //   get name of club for this user
   //
   String club = (String)session.getAttribute("club");      // get club name
   
   
   //
   //  Get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);        // get the year
   int thisMonth = cal.get(Calendar.MONTH) +1;
   int thisDay = cal.get(Calendar.DAY_OF_MONTH);

   //
   //  Array to hold the course names
   //
   ArrayList<String> course = new ArrayList<String>();

   String courseName = "";        // course names
   int multi = 0;                 // multiple course support
   int index= 0;


   //
   // Get the 'Multiple Course' option from the club db
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi " +
                             "FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);

      }
      stmt.close();

      if (multi != 0) {           // if multiple courses supported for this club

         course = Utilities.getCourseNames(con);     // get all the course names
      }

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Output HTML page to prompt for parms
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Add Lottery Main"));
   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("function cursor() { document.forms['f'].lottery_name.focus(); }");
   out.println("// -->");
   out.println("</script>");
   out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   SystemUtils.getProshopSubMenu(req, out, 1);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<b>Lotteries</b><br>");
      out.println("Complete the following information for each lottery to be added.<br>");
      out.println("Click on 'ADD' to add the lottery.");
   out.println("</font></td></tr></table><br>");

   //out.println("<table border=\"1\" cols=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\" cellspacing=\"6\">");
   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"6\" cellspacing=\"6\">");
      out.println("<form action=\"Proshop_addlottery\" method=\"post\" target=\"bot\" name=\"f\">");
      out.println("<tr>");
         out.println("<td width=\"600\">");      // was 580
            out.println("<font size=\"2\">");
            out.println("<p align=\"left\">&nbsp;&nbsp;Lottery Name:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"lottery_name\" size=\"30\" maxlength=\"30\">");
               out.println("&nbsp;&nbsp;&nbsp;* Must be unique");
            out.println("</p>");

         if (multi != 0) {

            out.println("<p align=\"left\">&nbsp;&nbsp;Select a Course:&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"course\">");
            out.println("<option selected value=\"-ALL-\">ALL</option>");

            for (index=0; index<course.size(); index++) {

               courseName = course.get(index);                    // get course name from array
               out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
            }
            out.println("</select>");
            out.println("</p>");

         } else {

            out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
         }
         out.println("<p align=\"left\">&nbsp;&nbsp;Tees:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"fb\">");
             out.println("<option value=\"Both\">Both</option>");
             out.println("<option value=\"Front\">Front</option>");
             out.println("<option value=\"Back\">Back</option>");
           out.println("</select></p>");

            out.println("<p align=\"left\">&nbsp;&nbsp;Start Date:&nbsp;&nbsp;");
            out.println("Month:&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"month\">");

              dateOpts.opMonth(out, thisMonth);         // output the month options

            out.println("</select>");

            out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"day\">");

              dateOpts.opDay(out, thisDay);         // output the day options

            out.println("</select>");

            out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"year\">");

              dateOpts.opYear(out, thisYear);         // output the year options

            out.println("</select>");
            out.println("</p>");
              
            out.println("<p align=\"left\">&nbsp;&nbsp;End Date:&nbsp;&nbsp;&nbsp;");
            out.println("Month:&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"emonth\">");

              dateOpts.opMonth(out, thisMonth);         // output the month options

            out.println("</select>");

            out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"eday\">");

              dateOpts.opDay(out, thisDay);         // output the day options

            out.println("</select>");

            out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"eyear\">");

              dateOpts.opYear(out, thisYear);         // output the year options

            out.println("</select>");
            out.println("</p>");
              
            out.println("<p align=\"left\">&nbsp;&nbsp;Start Time: &nbsp;&nbsp; hr &nbsp;");
            out.println("<select size=\"1\" name=\"start_hr\">");
            out.println("<option value=\"01\">01</option>");
            out.println("<option value=\"02\">02</option>");
            out.println("<option value=\"03\">03</option>");
            out.println("<option value=\"04\">04</option>");
            out.println("<option value=\"05\">05</option>");
            out.println("<option value=\"06\">06</option>");
            out.println("<option value=\"07\">07</option>");
            out.println("<option value=\"08\">08</option>");
            out.println("<option value=\"09\">09</option>");
            out.println("<option value=\"10\">10</option>");
            out.println("<option value=\"11\">11</option>");
            out.println("<option value=\"12\">12</option>");
            out.println("</select>");
            out.println("&nbsp; min &nbsp;");
            out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"start_min\">");
            out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"start_ampm\">");
            out.println("<option value=\"00\">AM</option>");
            out.println("<option value=\"12\">PM</option>");
            out.println("</select>");
            out.println("</p>");
            out.println("<p align=\"left\">&nbsp;&nbsp;End Time: &nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;");
            out.println("<select size=\"1\" name=\"end_hr\">");
            out.println("<option value=\"01\">01</option>");
            out.println("<option value=\"02\">02</option>");
            out.println("<option value=\"03\">03</option>");
            out.println("<option value=\"04\">04</option>");
            out.println("<option value=\"05\">05</option>");
            out.println("<option value=\"06\">06</option>");
            out.println("<option value=\"07\">07</option>");
            out.println("<option value=\"08\">08</option>");
            out.println("<option value=\"09\">09</option>");
            out.println("<option value=\"10\">10</option>");
            out.println("<option value=\"11\">11</option>");
            out.println("<option value=\"12\">12</option>");
            out.println("</select>");
            out.println("&nbsp; min &nbsp;");
            out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"end_min\">");
            out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"end_ampm\">");
            out.println("<option value=\"00\">AM</option>");
            out.println("<option value=\"12\">PM</option>");
            out.println("</select>");
            out.println("</p>");
            out.println("<p align=\"Left\">&nbsp;&nbsp;Recurrence:&nbsp;&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"recurr\">");
            out.println("<option value=\"Every Day\">Every Day</option>");
            out.println("<option value=\"Every Sunday\">Every Sunday</option>");
            out.println("<option value=\"Every Monday\">Every Monday</option>");
            out.println("<option value=\"Every Tuesday\">Every Tuesday</option>");
            out.println("<option value=\"Every Wednesday\">Every Wednesday</option>");
            out.println("<option value=\"Every Thursday\">Every Thursday</option>");
            out.println("<option value=\"Every Friday\">Every Friday</option>");
            out.println("<option value=\"Every Saturday\">Every Saturday</option>");
            out.println("<option value=\"All Weekdays\">All Weekdays</option>");
            out.println("<option value=\"All Weekends\">All Weekends</option>");
            out.println("</select>");
            out.println("</p>");

            out.println("<p align=\"left\">&nbsp;&nbsp;Color to make the Lottery times on the tee sheet:&nbsp;&nbsp;");

            Common_Config.displayColors(out);       // output the color options

            out.println("<br>");
            out.println("&nbsp;&nbsp;Click here to see the available colors:&nbsp;");
            out.println("<a href=\"/" +rev+ "/proshop_color.htm\" target=\"_blank\">View Colors</a>");
            out.println("</p>");

            out.println("<br>");
             out.println("Days in advance to start taking requests:&nbsp;&nbsp;&nbsp;");
             out.println("<select size=\"1\" name=\"sdays\">");
               out.println("<option value=\"2\">2</option>");
               out.println("<option value=\"3\">3</option>");
               out.println("<option value=\"4\">4</option>");
               out.println("<option value=\"5\">5</option>");
               out.println("<option value=\"6\">6</option>");
               out.println("<option value=\"7\">7</option>");
               out.println("<option value=\"8\">8</option>");
               out.println("<option value=\"9\">9</option>");
               out.println("<option value=\"10\">10</option>");
               out.println("<option value=\"11\">11</option>");
               out.println("<option value=\"12\">12</option>");
               out.println("<option value=\"13\">13</option>");
               out.println("<option value=\"14\">14</option>");
               out.println("<option value=\"15\">15</option>");
               out.println("<option value=\"16\">16</option>");
               out.println("<option value=\"17\">17</option>");
               out.println("<option value=\"18\">18</option>");
               out.println("<option value=\"19\">19</option>");
               out.println("<option value=\"20\">20</option>");
               out.println("<option value=\"21\">21</option>");
               out.println("<option value=\"22\">22</option>");
               out.println("<option value=\"23\">23</option>");
               out.println("<option value=\"24\">24</option>");
               out.println("<option value=\"25\">25</option>");
               out.println("<option value=\"26\">26</option>");
               out.println("<option value=\"27\">27</option>");
               out.println("<option value=\"28\">28</option>");
               out.println("<option value=\"29\">29</option>");
               out.println("<option value=\"30\">30</option>");
               if (club.equals("desertmountain")) {
                   out.println("<option value=\"365\">365</option>");
               }
             out.println("</select>");
             out.println("<br><br>");
          out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at:");
          out.println("&nbsp;&nbsp;&nbsp; hr &nbsp;");
            out.println("<select size=\"1\" name=\"sd_hr\">");
                 out.println("<option value=\"01\">1</option>");
                 out.println("<option value=\"02\">2</option>");
                 out.println("<option value=\"03\">3</option>");
                 out.println("<option value=\"04\">4</option>");
                 out.println("<option value=\"05\">5</option>");
                 out.println("<option value=\"06\">6</option>");
                 out.println("<option value=\"07\">7</option>");
                 out.println("<option value=\"08\">8</option>");
                 out.println("<option value=\"09\">9</option>");
                 out.println("<option value=\"10\">10</option>");
                 out.println("<option value=\"11\">11</option>");
                 out.println("<option value=\"12\">12</option>");
            out.println("</select>");
            out.println("&nbsp;&nbsp;&nbsp; min &nbsp;");
               out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"sd_min\">");
               out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"sdampm\">");
                 out.println("<option value=\"00\">AM</option>");
                 out.println("<option value=\"12\">PM</option>");
            out.println("</select>");
          out.println("<br><br>");

             out.println("Days in advance to stop taking requests (must be less than above):&nbsp;&nbsp;&nbsp;");
             out.println("<select size=\"1\" name=\"edays\">");
               out.println("<option value=\"1\">1</option>");
               out.println("<option value=\"2\">2</option>");
               out.println("<option value=\"3\">3</option>");
               out.println("<option value=\"4\">4</option>");
               out.println("<option value=\"5\">5</option>");
               out.println("<option value=\"6\">6</option>");
               out.println("<option value=\"7\">7</option>");
               out.println("<option value=\"8\">8</option>");
               out.println("<option value=\"9\">9</option>");
               out.println("<option value=\"10\">10</option>");
               out.println("<option value=\"11\">11</option>");
               out.println("<option value=\"12\">12</option>");
               out.println("<option value=\"13\">13</option>");
               out.println("<option value=\"14\">14</option>");
               out.println("<option value=\"15\">15</option>");
               out.println("<option value=\"16\">16</option>");
               out.println("<option value=\"17\">17</option>");
               out.println("<option value=\"18\">18</option>");
               out.println("<option value=\"19\">19</option>");
               out.println("<option value=\"20\">20</option>");
               out.println("<option value=\"21\">21</option>");
               out.println("<option value=\"22\">22</option>");
               out.println("<option value=\"23\">23</option>");
               out.println("<option value=\"24\">24</option>");
               out.println("<option value=\"25\">25</option>");
               out.println("<option value=\"26\">26</option>");
               out.println("<option value=\"27\">27</option>");
               out.println("<option value=\"28\">28</option>");
               out.println("<option value=\"29\">29</option>");
               out.println("<option value=\"30\">30</option>");
             out.println("</select>");
          out.println("<br><br>");
          out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at:");
          out.println("&nbsp;&nbsp;&nbsp; hr &nbsp;");
            out.println("<select size=\"1\" name=\"ed_hr\">");
                 out.println("<option value=\"01\">1</option>");
                 out.println("<option value=\"02\">2</option>");
                 out.println("<option value=\"03\">3</option>");
                 out.println("<option value=\"04\">4</option>");
                 out.println("<option value=\"05\">5</option>");
                 out.println("<option value=\"06\">6</option>");
                 out.println("<option value=\"07\">7</option>");
                 out.println("<option value=\"08\">8</option>");
                 out.println("<option value=\"09\">9</option>");
                 out.println("<option value=\"10\">10</option>");
                 out.println("<option value=\"11\">11</option>");
                 out.println("<option value=\"12\">12</option>");
                 out.println("<option value=\"0\">12</option>");
            out.println("</select>");
            out.println("&nbsp;&nbsp;&nbsp; min &nbsp;");
               out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"ed_min\">");
               out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"edampm\">");
                 out.println("<option value=\"00\">AM</option>");
                 out.println("<option value=\"12\">PM</option>");
            out.println("</select>");
          out.println("<br><br>");

             out.println("Days in advance to process the requests (must be less than or equal to above):&nbsp;&nbsp;&nbsp;");
             out.println("<select size=\"1\" name=\"pdays\">");
               out.println("<option value=\"1\">1</option>");
               out.println("<option value=\"2\">2</option>");
               out.println("<option value=\"3\">3</option>");
               out.println("<option value=\"4\">4</option>");
               out.println("<option value=\"5\">5</option>");
               out.println("<option value=\"6\">6</option>");
               out.println("<option value=\"7\">7</option>");
               out.println("<option value=\"8\">8</option>");
               out.println("<option value=\"9\">9</option>");
               out.println("<option value=\"10\">10</option>");
               out.println("<option value=\"11\">11</option>");
               out.println("<option value=\"12\">12</option>");
               out.println("<option value=\"13\">13</option>");
               out.println("<option value=\"14\">14</option>");
               out.println("<option value=\"15\">15</option>");
               out.println("<option value=\"16\">16</option>");
               out.println("<option value=\"17\">17</option>");
               out.println("<option value=\"18\">18</option>");
               out.println("<option value=\"19\">19</option>");
               out.println("<option value=\"20\">20</option>");
               out.println("<option value=\"21\">21</option>");
               out.println("<option value=\"22\">22</option>");
               out.println("<option value=\"23\">23</option>");
               out.println("<option value=\"24\">24</option>");
               out.println("<option value=\"25\">25</option>");
               out.println("<option value=\"26\">26</option>");
               out.println("<option value=\"27\">27</option>");
               out.println("<option value=\"28\">28</option>");
               out.println("<option value=\"29\">29</option>");
               out.println("<option value=\"30\">30</option>");
             out.println("</select>");
          out.println("<br><br>");
          out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at:");
          out.println("&nbsp;&nbsp;&nbsp; hr &nbsp;");
            out.println("<select size=\"1\" name=\"p_hr\">");
                 out.println("<option value=\"01\">1</option>");
                 out.println("<option value=\"02\">2</option>");
                 out.println("<option value=\"03\">3</option>");
                 out.println("<option value=\"04\">4</option>");
                 out.println("<option value=\"05\">5</option>");
                 out.println("<option value=\"06\">6</option>");
                 out.println("<option value=\"07\">7</option>");
                 out.println("<option value=\"08\">8</option>");
                 out.println("<option value=\"09\">9</option>");
                 out.println("<option value=\"10\">10</option>");
                 out.println("<option value=\"11\">11</option>");
                 out.println("<option value=\"12\">12</option>");
                 out.println("<option value=\"0\">12</option>");
            out.println("</select>");
            out.println("&nbsp;&nbsp;&nbsp; min &nbsp;");
               out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"p_min\">");
               out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"pampm\">");
                 out.println("<option value=\"00\">AM</option>");
                 out.println("<option value=\"12\">PM</option>");
            out.println("</select>");
          out.println("<br><br>");

          out.println("Number of consecutive tee times a member can request:&nbsp;&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"slots\">");
                 out.println("<option value=\"01\">1</option>");
                 out.println("<option value=\"02\">2</option>");
                 out.println("<option value=\"03\">3</option>");
                 out.println("<option value=\"04\">4</option>");
                 out.println("<option value=\"05\">5</option>");
            out.println("</select>");
          out.println("<br><br>");

          out.println("Minimum number of <b>players</b> per request (including guests):&nbsp;&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"players\">");
                 out.println("<option value=\"0\">0</option>");
                 out.println("<option value=\"1\">1</option>");
                 out.println("<option value=\"2\">2</option>");
                 out.println("<option value=\"3\">3</option>");
                 out.println("<option value=\"4\">4</option>");
                 out.println("<option value=\"5\">5</option>");
            out.println("</select>");
          out.println("<br><br>");

          out.println("Minimum number of <b>members</b> per request (not counting guests):&nbsp;&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"members\">");
                 out.println("<option value=\"0\">0</option>");
                 out.println("<option value=\"1\">1</option>");
                 out.println("<option value=\"2\">2</option>");
                 out.println("<option value=\"3\">3</option>");
                 out.println("<option value=\"4\">4</option>");
                 out.println("<option value=\"5\">5</option>");
            out.println("</select>");
          out.println("<br><br>");

          if (club.equals("oaklandhills")) {       // custom for Oakland Hills CC
              
              out.println("<b>Maximum</b> number of members per request:&nbsp;&nbsp;&nbsp;");
              out.println("<select size=\"1\" name=\"maxmems\">");
                 out.println("<option value=\"0\">0</option>");
                 out.println("<option value=\"1\">1</option>");
                 out.println("<option value=\"2\">2</option>");
                 out.println("<option value=\"3\">3</option>");
                 out.println("<option selected value=\"4\">4</option>");      // default value
                 // out.println("<option value=\"5\">5</option>");
              out.println("</select>");
              out.println("<br><br>");
          }

/*
          out.println("Give preference to full groups?&nbsp;&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"pref\">");
                 out.println("<option value=\"Yes\">Yes</option>");
                 out.println("<option value=\"No\">No</option>");
            out.println("</select>");
          out.println("<br><br>");
*/

          out.println("Do you want to allow members to specify acceptable time<br>range values when they request a starting time (minutes before and after)?&nbsp;&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"allowmins\">");
                 out.println("<option value=\"Yes\">Yes</option>");
                 out.println("<option value=\"No\">No</option>");
            out.println("</select>");
          out.println("<br><br>");

          int mins_before = 120;       // set default - allow easy change

          out.println("Default value for Minutes Before for member starting time requests:&nbsp;&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"minsbefore\">");
               if (mins_before == 0) {
                  out.println("<option selected value=\"0\">None</option>");
               } else {
                  out.println("<option value=\"0\">None</option>");
               }
               if (mins_before == 10) {
                  out.println("<option selected value=\"10\">10 mins</option>");
               } else {
                  out.println("<option value=\"10\">10 mins</option>");
               }
               if (mins_before == 20) {
                  out.println("<option selected value=\"20\">20 mins</option>");
               } else {
                  out.println("<option value=\"20\">20 mins</option>");
               }
               if (mins_before == 30) {
                  out.println("<option selected value=\"30\">30 mins</option>");
               } else {
                  out.println("<option value=\"30\">30 mins</option>");
               }
               if (mins_before == 40) {
                  out.println("<option selected value=\"40\">40 mins</option>");
               } else {
                  out.println("<option value=\"40\">40 mins</option>");
               }
               if (mins_before == 50) {
                  out.println("<option selected value=\"50\">50 mins</option>");
               } else {
                  out.println("<option value=\"50\">50 mins</option>");
               }
               if (mins_before == 60) {
                  out.println("<option selected value=\"60\">1 hr</option>");
               } else {
                  out.println("<option value=\"60\">1 hr</option>");
               }
               if (mins_before == 75) {
                  out.println("<option selected value=\"75\">1 hr 15 mins</option>");
               } else {
                  out.println("<option value=\"75\">1 hr 15 mins</option>");
               }
               if (mins_before == 90) {
                  out.println("<option selected value=\"90\">1 hr 30 mins</option>");
               } else {
                  out.println("<option value=\"90\">1 hr 30 mins</option>");
               }
               if (mins_before == 120) {
                  out.println("<option selected value=\"120\">2 hrs</option>");
               } else {
                  out.println("<option value=\"120\">2 hrs</option>");
               }
               if (mins_before == 150) {
                  out.println("<option selected value=\"150\">2 hrs 30 mins</option>");
               } else {
                  out.println("<option value=\"150\">2 hrs 30 mins</option>");
               }
               if (mins_before == 180) {
                  out.println("<option selected value=\"180\">3 hrs</option>");
               } else {
                  out.println("<option value=\"180\">3 hrs</option>");
               }
               if (mins_before == 210) {
                  out.println("<option selected value=\"210\">3 hrs 30 mins</option>");
               } else {
                  out.println("<option value=\"210\">3 hrs 30 mins</option>");
               }
               if (mins_before == 240) {
                  out.println("<option selected value=\"240\">4 hrs</option>");
               } else {
                  out.println("<option value=\"240\">4 hrs</option>");
               }
               if (mins_before == 300) {
                  out.println("<option selected value=\"300\">5 hrs</option>");
               } else {
                  out.println("<option value=\"300\">5 hrs</option>");
               }
               if (mins_before == 360) {
                  out.println("<option selected value=\"360\">6 hrs</option>");
               } else {
                  out.println("<option value=\"360\">6 hrs</option>");
               }
               if (mins_before == 420) {
                  out.println("<option selected value=\"420\">7 hrs</option>");
               } else {
                  out.println("<option value=\"420\">7 hrs</option>");
               }
               if (mins_before == 480) {
                  out.println("<option selected value=\"480\">8 hrs</option>");
               } else {
                  out.println("<option value=\"480\">8 hrs</option>");
               }
               if (mins_before == 540) {
                  out.println("<option selected value=\"540\">9 hrs</option>");
               } else {
                  out.println("<option value=\"540\">9 hrs</option>");
               }
               if (mins_before == 600) {
                  out.println("<option selected value=\"600\">10 hrs</option>");
               } else {
                  out.println("<option value=\"600\">10 hrs</option>");
               }
               out.println("</select>");
          out.println("<br><br>");
          
          
          int mins_after = 120;       // set default - allow easy change

          out.println("Default value for Minutes After for member starting time requests:&nbsp;&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"minsafter\">");
               if (mins_after == 0) {
                  out.println("<option selected value=\"0\">None</option>");
               } else {
                  out.println("<option value=\"0\">None</option>");
               }
               if (mins_after == 10) {
                  out.println("<option selected value=\"10\">10 mins</option>");
               } else {
                  out.println("<option value=\"10\">10 mins</option>");
               }
               if (mins_after == 20) {
                  out.println("<option selected value=\"20\">20 mins</option>");
               } else {
                  out.println("<option value=\"20\">20 mins</option>");
               }
               if (mins_after == 30) {
                  out.println("<option selected value=\"30\">30 mins</option>");
               } else {
                  out.println("<option value=\"30\">30 mins</option>");
               }
               if (mins_after == 40) {
                  out.println("<option selected value=\"40\">40 mins</option>");
               } else {
                  out.println("<option value=\"40\">40 mins</option>");
               }
               if (mins_after == 50) {
                  out.println("<option selected value=\"50\">50 mins</option>");
               } else {
                  out.println("<option value=\"50\">50 mins</option>");
               }
               if (mins_after == 60) {
                  out.println("<option selected value=\"60\">1 hr</option>");
               } else {
                  out.println("<option value=\"60\">1 hr</option>");
               }
               if (mins_after == 75) {
                  out.println("<option selected value=\"75\">1 hr 15 mins</option>");
               } else {
                  out.println("<option value=\"75\">1 hr 15 mins</option>");
               }
               if (mins_after == 90) {
                  out.println("<option selected value=\"90\">1 hr 30 mins</option>");
               } else {
                  out.println("<option value=\"90\">1 hr 30 mins</option>");
               }
               if (mins_after == 120) {
                  out.println("<option selected value=\"120\">2 hrs</option>");
               } else {
                  out.println("<option value=\"120\">2 hrs</option>");
               }
               if (mins_after == 150) {
                  out.println("<option selected value=\"150\">2 hrs 30 mins</option>");
               } else {
                  out.println("<option value=\"150\">2 hrs 30 mins</option>");
               }
               if (mins_after == 180) {
                  out.println("<option selected value=\"180\">3 hrs</option>");
               } else {
                  out.println("<option value=\"180\">3 hrs</option>");
               }
               if (mins_after == 210) {
                  out.println("<option selected value=\"210\">3 hrs 30 mins</option>");
               } else {
                  out.println("<option value=\"210\">3 hrs 30 mins</option>");
               }
               if (mins_after == 240) {
                  out.println("<option selected value=\"240\">4 hrs</option>");
               } else {
                  out.println("<option value=\"240\">4 hrs</option>");
               }
               if (mins_after == 300) {
                  out.println("<option selected value=\"300\">5 hrs</option>");
               } else {
                  out.println("<option value=\"300\">5 hrs</option>");
               }
               if (mins_after == 360) {
                  out.println("<option selected value=\"360\">6 hrs</option>");
               } else {
                  out.println("<option value=\"360\">6 hrs</option>");
               }
               if (mins_before == 420) {
                  out.println("<option selected value=\"420\">7 hrs</option>");
               } else {
                  out.println("<option value=\"420\">7 hrs</option>");
               }
               if (mins_before == 480) {
                  out.println("<option selected value=\"480\">8 hrs</option>");
               } else {
                  out.println("<option value=\"480\">8 hrs</option>");
               }
               if (mins_before == 540) {
                  out.println("<option selected value=\"540\">9 hrs</option>");
               } else {
                  out.println("<option value=\"540\">9 hrs</option>");
               }
               if (mins_before == 600) {
                  out.println("<option selected value=\"600\">10 hrs</option>");
               } else {
                  out.println("<option value=\"600\">10 hrs</option>");
               }
               out.println("</select>");
          out.println("<br><br>");
          
          
          out.println("Do you want to allow members to use an X (TBD) to <BR>hold positions in the lottery requests?&nbsp;&nbsp;(0 = No)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"allowx\">");
                out.println("<option value=\"0\">0</option>");
                out.println("<option value=\"1\">1</option>");
                out.println("<option value=\"2\">2</option>");
                out.println("<option value=\"3\">3</option>");
                out.println("<option value=\"4\">4</option>");
            out.println("</select>&nbsp;&nbsp;# of X's per tee time requested.");
          out.println("<br><br>");


          out.println("Do you want to allow <strong>proshop users to recur</strong> tee time requests for this lottery?&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"recurrpro\">");
                 out.println("<option value=\"No\">No</option>");
                 out.println("<option value=\"Yes\">Yes</option>");
            out.println("</select>");
          out.println("<br><br>");

          
          out.println("Do you want to allow <strong>members to recur</strong> tee time requests for this lottery?&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"recurrmem\">");
                 out.println("<option value=\"No\">No</option>");
                 out.println("<option value=\"Yes\">Yes</option>");
            out.println("</select>");
          out.println("<br>");
          
          out.println("If yes, specify the max number of days that members can recur a request<BR>&nbsp;&nbsp;&nbsp; (0 = end date of this lottery, max = 365):&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" value=\"0\" name=\"recur_days\">");
          out.println("<br><br>");
          
          
          out.println("Do you want to review & approve the tee times prior to posting?&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"approve\">");
                 out.println("<option value=\"Yes\">Yes</option>");
                 out.println("<option value=\"No\">No</option>");
            out.println("</select>");
          out.println("<br><br>");

          
          out.println("Lottery Type:&nbsp;&nbsp;&nbsp;");
          out.println("<select size=\"1\" name=\"type\">");
            out.println("<option value=\"Random\">Random</option>");
            out.println("<option value=\"Proshop\">Proshop</option>");
            out.println("<option value=\"WeightedBR\">Weighted By Rounds</option>");
            out.println("<option value=\"WeightedBP\">Weighted By Proximity</option>");
         out.println("</select>");
         out.println("<br><br>");

       out.println("</font></td></tr>");
       out.println("<tr>");
       out.println("<td width=\"600\">");     // was 580
       out.println("<font size=\"2\">");
          out.println("<b>You MUST complete the following if you selected a Weighted Lottery Type:</b>");
          out.println("<br>(Not necessary for Random or Proshop types)<br><br>");

          out.println("Number of days to accumulate members' lottery points:&nbsp;&nbsp;&nbsp;");
            out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"adays\">");
            out.println("&nbsp;(enter 00 - 99)");
          out.println("<br><br>");

             out.println("Lottery selection based on:&nbsp;&nbsp;&nbsp;");
             out.println("<select size=\"1\" name=\"selection\">");
               out.println("<option value=\"1\">Total Points of Group</option>");
               out.println("<option value=\"2\">Average Points of Group</option>");
               out.println("<option value=\"3\">Points of Highest Player in Group</option>");
               out.println("<option value=\"4\">Points of Lowest Player in Group</option>");
            out.println("</select>");
          out.println("<br><br>");

             out.println("Each guest in a request will count as:&nbsp;&nbsp;&nbsp;");
             out.println("<select size=\"1\" name=\"guest\">");
               out.println("<option value=\"0\">None - do not count</option>");
               out.println("<option value=\"1\">Same as the highest member</option>");
               out.println("<option value=\"2\">Same as the lowest member</option>");
            out.println("</select>");
          out.println("<br><br>");
          
             out.println("If allowed, each X in a request will count as:&nbsp;&nbsp;&nbsp;");
             out.println("<select size=\"1\" name=\"xvalue\">");
               out.println("<option value=\"0\">None - do not count</option>");
               out.println("<option value=\"1\">Same as the highest member</option>");
               out.println("<option value=\"2\">Same as the lowest member</option>");
            out.println("</select>");
          out.println("<br><br>");
          
          out.println("Click here for more information:&nbsp;&nbsp;");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_lottery-ByRounds.htm', 'newwindow', 'Height=460, width=650, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("Weighted By Rounds</a>");
          out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_lottery-ByProximity.htm', 'newwindow', 'Height=570, width=650, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("Weighted By Proximity</a>");
          out.println("<br><br>");

       out.println("</font></td></tr>");
       out.println("<tr>");
       out.println("<td width=\"600\">");    // was 580
       out.println("<font size=\"2\">");
          out.println("<b>You MUST complete the following ONLY if you selected 'Weighted By Rounds' Lottery Type:</b>");
          out.println("<br>(Not necessary for any other types)<br><br>");

          out.println("Number of lottery points to assess for a weekday round:&nbsp;&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"wdpts\">");
                 out.println("<option value=\"00\">0</option>");
                 out.println("<option value=\"01\">1</option>");
                 out.println("<option value=\"02\">2</option>");
                 out.println("<option value=\"03\">3</option>");
                 out.println("<option value=\"04\">4</option>");
                 out.println("<option value=\"05\">5</option>");
            out.println("</select>");
          out.println("<br><br>");

          out.println("Number of lottery points to assess for a weekend round:&nbsp;&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"wepts\">");
                 out.println("<option value=\"00\">0</option>");
                 out.println("<option value=\"01\">1</option>");
                 out.println("<option value=\"02\">2</option>");
                 out.println("<option value=\"03\">3</option>");
                 out.println("<option value=\"04\">4</option>");
                 out.println("<option value=\"05\">5</option>");
            out.println("</select>");
          out.println("<br><br>");

          out.println("Number of lottery points to assess for an event round:&nbsp;&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"evpts\">");
                 out.println("<option value=\"00\">0</option>");
                 out.println("<option value=\"01\">1</option>");
                 out.println("<option value=\"02\">2</option>");
                 out.println("<option value=\"03\">3</option>");
                 out.println("<option value=\"04\">4</option>");
                 out.println("<option value=\"05\">5</option>");
            out.println("</select>");
          out.println("<br><br>");

          out.println("Number of lottery points to assess for each guest round:&nbsp;&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"gpts\">");
                 out.println("<option value=\"00\">0</option>");
                 out.println("<option value=\"01\">1</option>");
                 out.println("<option value=\"02\">2</option>");
                 out.println("<option value=\"03\">3</option>");
                 out.println("<option value=\"04\">4</option>");
                 out.println("<option value=\"05\">5</option>");
            out.println("</select>");
          out.println("<br><br>");

          out.println("Number of lottery points to assess for each no-show by a member:&nbsp;&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"nopts\">");
                 out.println("<option value=\"00\">0</option>");
                 out.println("<option value=\"01\">1</option>");
                 out.println("<option value=\"02\">2</option>");
                 out.println("<option value=\"03\">3</option>");
                 out.println("<option value=\"04\">4</option>");
                 out.println("<option value=\"05\">5</option>");
            out.println("</select>");
          out.println("<br><br>");

            out.println("<p align=\"center\">");
            out.println("<input type=\"submit\" value=\"ADD\">");
            out.println("</p></font>");
      out.println("</td></tr></form>");
   out.println("</table>");
   out.println("</font>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_lottery\">");
   out.println("<input type=\"submit\" value=\"Done\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center>");
   out.println("</body>");
   out.println("</html>");

 }    // end of doGet processing


 //****************************************************
 // Process the form request from proshop_addlottery page
 //****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   ResultSet rs = null;
   String omit = "";

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_LOTTERY", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_LOTTERY", out);
   }

   //
   //   get name of club for this user
   //
   String club = (String)session.getAttribute("club");      // get club name
   
   
   //
   // Get all the parameters entered
   //
   String name = req.getParameter("lottery_name");         //  lottery name
   String s_month = req.getParameter("month");             //  month (00 - 12)
   String s_day = req.getParameter("day");                 //  day (01 - 31)
   String s_year = req.getParameter("year");               //  year (2004 - 20xx)
   String start_hr = req.getParameter("start_hr");         //  start hour (01 - 12)
   String start_min = req.getParameter("start_min");       //  start min (00 - 59)
   String start_ampm = req.getParameter("start_ampm");     //  AM/PM (00 or 12)
   String e_month = req.getParameter("emonth");             
   String e_day = req.getParameter("eday");                 
   String e_year = req.getParameter("eyear");               
   String end_hr = req.getParameter("end_hr");                
   String end_min = req.getParameter("end_min");              
   String end_ampm = req.getParameter("end_ampm");            
   String recurr = req.getParameter("recurr");              
   String color = req.getParameter("color");               //  color for the event display
   String courseName = req.getParameter("course");
   String fb = req.getParameter("fb");
   String ssdays = req.getParameter("sdays");
   String ssd_hr = req.getParameter("sd_hr");
   String ssd_min = req.getParameter("sd_min");
   String ssdampm = req.getParameter("sdampm");
   String sedays = req.getParameter("edays");
   String sed_hr = req.getParameter("ed_hr");
   String sed_min = req.getParameter("ed_min");
   String sedampm = req.getParameter("edampm");
   String spdays = req.getParameter("pdays");
   String sp_hr = req.getParameter("p_hr");
   String sp_min = req.getParameter("p_min");
   String spampm = req.getParameter("pampm");
   String type = req.getParameter("type");
   String sadays = req.getParameter("adays");
   String swdpts = req.getParameter("wdpts");
   String swepts = req.getParameter("wepts");
   String sevpts = req.getParameter("evpts");
   String sgpts = req.getParameter("gpts");
   String snopts = req.getParameter("nopts");
   String sselection = req.getParameter("selection");
   String sguest = req.getParameter("guest");
   String sslots = req.getParameter("slots");
   String splayers = req.getParameter("players");
   String smembers = req.getParameter("members");
   String approve = req.getParameter("approve");
   String allowmins = req.getParameter("allowmins");
   String minsbefore = req.getParameter("minsbefore");
   String minsafter = req.getParameter("minsafter");
     
   String pref = "No";

   if (req.getParameter("pref") != null) {     

      pref = req.getParameter("pref");
   }

     
   String maxmems = "0";

   if (req.getParameter("maxmems") != null) {     

      maxmems = req.getParameter("maxmems");      // custom parm for Oakland Hills CC
   }

   String temp = "";
   int allow_x = 0;
    
   if (req.getParameter("allowx") != null) {     

      temp = req.getParameter("allowx");
   
      try {
           allow_x = Integer.parseInt(temp);
      } catch (NumberFormatException ignore) {}
   }

   temp = "";
   int x_value = 0;

   if (req.getParameter("xvalue") != null) {     

      temp = req.getParameter("xvalue");
   
      try {
           x_value = Integer.parseInt(temp);
      } catch (NumberFormatException ignore) {}
   }

   int recurrpro = 0;

   if (req.getParameter("recurrpro") != null) {     

      if (req.getParameter("recurrpro").equalsIgnoreCase("yes")) recurrpro = 1;
   }

   int recurrmem = 0;

   if (req.getParameter("recurrmem") != null) {     

      if (req.getParameter("recurrmem").equalsIgnoreCase("yes")) recurrmem = 1;
   }
   
   int recur_days = 0;
   
   if (req.getParameter("recur_days") != null) {     

      recur_days = Integer.parseInt(req.getParameter("recur_days"));
   }

   
   int smonth = 0;                // init integer date/time values
   int sday = 0;
   int syear = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int s_hr = 0;
   int s_min = 0;
   int s_ampm = 0;
   int e_hr = 0;
   int e_min = 0;
   int e_ampm = 0;
   int sdays = 0;
   int sd_hr = 0;
   int sd_min = 0;
   int sdampm = 0;
   int edays = 0;
   int ed_hr = 0;
   int ed_min = 0;
   int edampm = 0;
   int pdays = 0;
   int p_hr = 0;
   int p_min = 0;
   int pampm = 0;
   int adays = 0;
   int wdpts = 0;
   int wepts = 0;
   int evpts = 0;
   int gpts = 0;
   int nopts = 0;
   int selection = 0;
   int guest = 0;
   int slots = 0;
   int members = 0;
   int players = 0;
   int allow_mins = 0;
   int mins_before = 0;
   int mins_after = 0;
   int maxmem = 0;
   int custom_int = 0;

   //
   // Convert the numeric string parameters to Int's
   //
   try {
      smonth = Integer.parseInt(s_month);
      sday = Integer.parseInt(s_day);
      syear = Integer.parseInt(s_year);
      s_hr = Integer.parseInt(start_hr);
      s_min = Integer.parseInt(start_min);
      s_ampm = Integer.parseInt(start_ampm);
      emonth = Integer.parseInt(e_month);
      eday = Integer.parseInt(e_day);
      eyear = Integer.parseInt(e_year);
      e_hr = Integer.parseInt(end_hr);
      e_min = Integer.parseInt(end_min);
      e_ampm = Integer.parseInt(end_ampm);
      sdays = Integer.parseInt(ssdays);
      sd_hr = Integer.parseInt(ssd_hr);
      sd_min = Integer.parseInt(ssd_min);
      sdampm = Integer.parseInt(ssdampm);
      edays = Integer.parseInt(sedays);
      ed_hr = Integer.parseInt(sed_hr);
      ed_min = Integer.parseInt(sed_min);
      edampm = Integer.parseInt(sedampm);
      pdays = Integer.parseInt(spdays);
      p_hr = Integer.parseInt(sp_hr);
      p_min = Integer.parseInt(sp_min);
      pampm = Integer.parseInt(spampm);
      adays = Integer.parseInt(sadays);
      wdpts = Integer.parseInt(swdpts);
      wepts = Integer.parseInt(swepts);
      evpts = Integer.parseInt(sevpts);
      gpts = Integer.parseInt(sgpts);
      nopts = Integer.parseInt(snopts);
      selection = Integer.parseInt(sselection);
      guest = Integer.parseInt(sguest);
      slots = Integer.parseInt(sslots);
      members = Integer.parseInt(smembers);
      players = Integer.parseInt(splayers);
      mins_before = Integer.parseInt(minsbefore);
      mins_after = Integer.parseInt(minsafter);
      maxmem = Integer.parseInt(maxmems);
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }
   
   
   if (allowmins.equals("No")) {
      
      allow_mins = 1;          // 1 means to NOT allow members to change mins before and mins after !!!
   }

   //
   //  verify the required fields (name reqr'ed - rest are automatic)
   //
   if (name.equals( omit )) {
                                                                                
      invData(out);    // inform the user and return
      return;
   }

   //
   //  Check for quotes in name
   //
   boolean error = SystemUtils.scanQuote(name);           // check for single quote

   if (error == true) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Apostrophes (single quotes) cannot be part of the Name.");
      out.println("<BR>Please remove the apostrophe and try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Scan name for special characters and replace with HTML supported chars (i.e. '>' = &gt)
   //
   name = SystemUtils.filter(name);

   //
   //  Verify length of name (special chars can force error)
   //
   if (name.length() > SystemUtils.LOTTL) {

      out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
      out.println("<BODY><CENTER><BR>");
      out.println("<br><br>");
      out.println("<H3>Input Error</H3>");
      out.println("<BR>Sorry, name field is too long.<BR>");
      out.println("<BR>Special characters (i.e. #, *, &, etc.) generate additional characters which");
      out.println("<BR>can cause the maximum length to be exceeded.<BR>");
      out.println("<BR>Please reduce the length of the name field and try again.<BR>");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Check if lottery already exists in database
   //
   try {

      PreparedStatement stmt = con.prepareStatement (
              "SELECT sdate FROM lottery3 WHERE name = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);       // put the parm in stmt
      rs = stmt.executeQuery();      // execute the prepared stmt
        
      if (rs.next()) {

         dupMem(out);    // lottery exists - inform the user and return
         stmt.close();
         return;
      }
      stmt.close();
   }
   catch (Exception ignored) {
   }

   //
   //  adjust some values for the table
   //
   long sdate = syear * 10000;       // create a date field from input values
   sdate = sdate + (smonth * 100);
   sdate = sdate + sday;             // date = yyyymmdd (for comparisons)

   long edate = eyear * 10000;       // ditto
   edate = edate + (emonth * 100);
   edate = edate + eday;             

   if (s_hr != 12) {                // _hr specified as 01 - 12 (_ampm = 00 or 12)
                              
      s_hr = s_hr + s_ampm;         // convert to military time (12 is always Noon, or PM)
   }

   if (s_hr == 12 && s_ampm == 0) {   // if midnight - 12 AM

      s_hr = 0;                      // use 00 for hr
   }

   if (e_hr != 12) {                // ditto for end time

      e_hr = e_hr + e_ampm;       
   }

   if (e_hr == 12 && e_ampm == 0) {   // if midnight - 12 AM

      e_hr = 0;                      // use 00 for hr
   }

   if (sd_hr != 12) {                // ditto for time to start taking requests 

      sd_hr = sd_hr + sdampm;
   }

   if (sd_hr == 12 && sdampm == 0) {  

      sd_hr = 0;                      
   }

   if (ed_hr != 12) {                // ditto for time to stop taking requests

      ed_hr = ed_hr + edampm;
   }

   if (ed_hr == 12 && edampm == 0) {

      ed_hr = 0;
   }

   if (p_hr != 12) {                // ditto for time to process requests

      p_hr = p_hr + pampm;
   }

   if (p_hr == 12 && pampm == 0) {

      p_hr = 0;
   }

   int stime = (s_hr * 100) + s_min;
   int etime = (e_hr * 100) + e_min;
   int sdtime = (sd_hr * 100) + sd_min;
   int edtime = (ed_hr * 100) + ed_min;
   int ptime = (p_hr * 100) + p_min;

   //
   //  verify the date and time fields
   //
   if ((sdate > edate) || (stime > etime)) {

      invData(out);    // inform the user and return
      return;
   }

   if ((edays >= sdays) || (pdays > edays)) {

      invData2(out);    // inform the user and return
      return;
   }

   if ((pdays == edays) && (ptime <= edtime)) {

      invData3(out);    // inform the user and return
      return;
   }

   //
   //  Validate # of members per request & # of players per request
   //
   if (members > players) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Number of Members per Request cannot be greater than the Number of Players per Request.");
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   
   //
   //  Verify the recur_days value - do not allow negative and do not allow too many days
   //
   if (recur_days < 0) recur_days = 0;
   
   if (recur_days > 365) recur_days = 365;
   
   
   //
   //  Custom for Oakland Hills CC
   //
   if (club.equals("oaklandhills") && maxmem > 0) {
       
       custom_int = maxmem;
   }
   
   

   //
   //  add lottery data to the database
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
        "INSERT INTO lottery3 (name, sdate, start_mm, start_dd, start_yy, start_hr, start_min, stime, " +
        "edate, end_mm, end_dd, end_yy, end_hr, end_min, etime, recurr, " +
        "color, courseName, fb, sdays, sdtime, sd_hr, sd_min, edays, edtime, ed_hr, ed_min, " +
        "pdays, ptime, p_hr, p_min, type, adays, wdpts, wepts, evpts, gpts, nopts, selection,  " +
        "guest, slots, pref, approve, members, players, minsbefore, minsafter, allowmins, allowx, xvalue, " +
        "custom_int, recurrpro, recurrmem, recur_days) " +
        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);       // put the parm in pstmt
      pstmt.setLong(2, sdate);
      pstmt.setInt(3, smonth);
      pstmt.setInt(4, sday);
      pstmt.setInt(5, syear);
      pstmt.setInt(6, s_hr);
      pstmt.setInt(7, s_min);
      pstmt.setInt(8, stime);
      pstmt.setLong(9, edate);
      pstmt.setInt(10, emonth);
      pstmt.setInt(11, eday);
      pstmt.setInt(12, eyear);
      pstmt.setInt(13, e_hr);
      pstmt.setInt(14, e_min);
      pstmt.setInt(15, etime);
      pstmt.setString(16, recurr);
      pstmt.setString(17, color);
      pstmt.setString(18, courseName);
      pstmt.setString(19, fb);
      pstmt.setInt(20, sdays);
      pstmt.setInt(21, sdtime);
      pstmt.setInt(22, sd_hr);
      pstmt.setInt(23, sd_min);
      pstmt.setInt(24, edays);
      pstmt.setInt(25, edtime);
      pstmt.setInt(26, ed_hr);
      pstmt.setInt(27, ed_min);
      pstmt.setInt(28, pdays);
      pstmt.setInt(29, ptime);
      pstmt.setInt(30, p_hr);
      pstmt.setInt(31, p_min);
      pstmt.setString(32, type);
      pstmt.setInt(33, adays);
      pstmt.setInt(34, wdpts);
      pstmt.setInt(35, wepts);
      pstmt.setInt(36, evpts);
      pstmt.setInt(37, gpts);
      pstmt.setInt(38, nopts);
      pstmt.setInt(39, selection);
      pstmt.setInt(40, guest);
      pstmt.setInt(41, slots);
      pstmt.setString(42, pref);
      pstmt.setString(43, approve);
      pstmt.setInt(44, members);
      pstmt.setInt(45, players);
      pstmt.setInt(46, mins_before);
      pstmt.setInt(47, mins_after);
      pstmt.setInt(48, allow_mins);
      pstmt.setInt(49, allow_x);
      pstmt.setInt(50, x_value);
      pstmt.setInt(51, custom_int);
      pstmt.setInt(52, recurrpro);
      pstmt.setInt(53, recurrmem);
      pstmt.setInt(54, recur_days);
      pstmt.executeUpdate();          // execute the prepared stmt

      pstmt.close();   // close the stmt

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR>Exception:   " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;

   }

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Add Lottery"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, 1);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>Lottery Has Been Added</H3>");
   out.println("<BR><BR>Thank you, the lottery has been added to the system database.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_addlottery\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

   try {
      resp.flushBuffer();      // force the repsonse to complete
   }
   catch (Exception ignore) {
   }

   //
   //  Now, call utility to scan the lottery table and update tee slots in teecurr accordingly
   //
   SystemUtils.do1Lottery(con, name);

 }   // end of doPost   


 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");

 }


 // *********************************************************
 //  Advance days entered incorrectly
 // *********************************************************

 private void invData2(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>The 'days in advance to stop taking request' MUST BE LESS THAN");
   out.println("<BR>the 'days in advance to start taking requests'.");
   out.println("<BR>The 'days in advance to process the requests' MUST BE LESS THAN or EQUAL TO");
   out.println("<BR>the 'days in advance to stop taking requests'.");
   out.println("<BR><BR>");
   out.println("<BR>Please correct this and try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }

 private void invData3(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>The 'days in advance to process the requests' is EQUAL TO");
   out.println("<BR>the 'days in advance to stop taking requests'. This is ok.");
   out.println("<BR>However, when that's the case, the 'time of day to process the requests' MUST BE");
   out.println("<BR>GREATER THAN the 'time of day to stop taking requests'.");
   out.println("<BR><BR>");
   out.println("<BR>Please correct this and try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }

 // *********************************************************
 // lottery already exists
 // *********************************************************

 private void dupMem(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, the <b>lottery name</b> you specified already exists in the database.<BR>");
   out.println("<BR>Please use the edit feature if you wish to change an existing lottery record.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");

 }

}
