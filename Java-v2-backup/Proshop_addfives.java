/***************************************************************************************     
 *   Proshop_addfives:  This servlet will process the 'add 5-some restriction' request
 *                      from Proshop's addfives page.
 *
 *
 *   called by:  Proshop_fives and self
 *
 *   created: 1/12/2003   Bob P.
 *
 *   last updated:
 *
 *        5/19/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *       10/02/08   Added code for copying existing five-some restrictions
 *        9/02/08   Javascript compatability updates
 *        8/12/08   Update to limited access user restrictions
 *        7/24/08   Added limited access proshop users checks
 *        2/16/05   Ver 5 - start with today's date and use common function to set it.
 *        1/24/05   Ver 5 - change club2 to club5.
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
import com.foretees.common.dateOpts;
import com.foretees.common.Utilities;

import com.foretees.common.Connect;


public class Proshop_addfives extends HttpServlet {
 
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //****************************************************
 // Process the request from Proshop_fives for a new 5-some restriction
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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_RESTRICTIONS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_RESTRICTIONS", out);
   }

   //
   //  Array to hold the course names
   //
   ArrayList<String> course = new ArrayList<String>();

   String courseName = "";        // course names
   int multi = 0;                 // multiple course support
   int index= 0;

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  Get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);        // get the year
   int thisMonth = cal.get(Calendar.MONTH) +1;
   int thisDay = cal.get(Calendar.DAY_OF_MONTH);

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
   out.println(SystemUtils.HeadTitle2("Proshop - Add 5-Some Rest Main"));
   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("function cursor() { document.forms['f'].restrict_name.focus(); }");
   out.println("// -->");
   out.println("</script>");
   out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<b>5-Some Restrictions</b><br>");
      out.println("Complete the following information for each Restriction to be added.<br>");
      out.println("Click on 'ADD' to add the Restriction.");
   out.println("</font></td></tr></table><br>");

   out.println("<table border=\"1\" cols=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\">");
      out.println("<form action=\"Proshop_addfives\" method=\"post\" target=\"bot\" name=\"f\">");
      out.println("<tr>");
         out.println("<td width=\"500\">");
            out.println("<font size=\"2\">");
            out.println("<p align=\"left\">&nbsp;&nbsp;Restriction Name:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"restrict_name\" size=\"30\" maxlength=\"30\">");
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

            out.println("<p align=\"left\">&nbsp;&nbsp;Color to make the Player 5 slot on the tee sheet:&nbsp;&nbsp;");

            Common_Config.displayColors(out);       // output the color options

            out.println("<br>");
            out.println("&nbsp;&nbsp;Click here to see the available colors:&nbsp;");
            out.println("<a href=\"/" +rev+ "/proshop_color.htm\" target=\"_blank\">View Colors</a>");
            out.println("</p>");

            out.println("<p align=\"center\">");
            out.println("<input type=\"submit\" value=\"ADD\">");
            out.println("</p></font>");
      out.println("</td></tr></form>");
   out.println("</table>");
   out.println("</font>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_fives\">");
   out.println("<input type=\"submit\" value=\"Done\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center>");
   out.println("</body>");
   out.println("</html>");

 }    // end of doGet processing


 //****************************************************
 // Process the form request from proshop_addfives page
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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_RESTRICTIONS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_RESTRICTIONS", out);
   }

   boolean copy = false;

   if (req.getParameter("copy") != null) {

      copy = true;            // this is a copy request (from doCopy in _mrest)
   }
   
   String name = "";        //  restriction name
   String s_month = "";             //  month (00 - 12)
   String s_day = "";                 //  day (01 - 31)
   String s_year = "";
       
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   // Get all the parameters entered
   //
   if (!copy) {
       name = req.getParameter("restrict_name");        //  restriction name
       s_month = req.getParameter("month");             //  month (00 - 12)
       s_day = req.getParameter("day");                 //  day (01 - 31)
       s_year = req.getParameter("year");               //  year (2004 - 20xx)
   } else {
       name = req.getParameter("rest_name");             //  restriction name
       s_month = req.getParameter("smonth");             //  month (00 - 12)
       s_day = req.getParameter("sday");                 //  day (01 - 31)
       s_year = req.getParameter("syear");               //  year (2003 - 20xx)
   }
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
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
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
   if (name.length() > SystemUtils.FIVEL) {

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
   //  Check if restriction already exists in database
   //
   try {

      PreparedStatement stmt = con.prepareStatement (
              "SELECT sdate FROM fives2 WHERE name = ? AND courseName = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);       // put the parm in stmt
      stmt.setString(2, courseName);
      rs = stmt.executeQuery();      // execute the prepared stmt
        
      if (rs.next()) {

         dupMem(out);    // 5-some restriction exists - inform the user and return
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

   if (e_hr != 12) {                // ditto

      e_hr = e_hr + e_ampm;       
   }

   int stime = s_hr * 100;
   stime = stime + s_min;
   int etime = e_hr * 100;
   etime = etime + e_min;

   //
   //  verify the date and time fields
   //
   if ((sdate > edate) || (stime > etime)) {

      invData(out);    // inform the user and return
      return;
   }

   //
   //  add restriction data to the database
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
        "INSERT INTO fives2 (name, sdate, start_mm, start_dd, start_yy, start_hr, start_min, stime, " +
        "edate, end_mm, end_dd, end_yy, end_hr, end_min, etime, recurr, " +
        "color, courseName, fb) VALUES " +
        "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

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
   out.println(SystemUtils.HeadTitle("Proshop Add Restriction"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>5-Some Restriction Has Been Added</H3>");
   out.println("<BR><BR>Thank you, the restriction has been added to the system database.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   if (!copy) {
       out.println("<form method=\"get\" action=\"Proshop_addfives\">");
   } else {
       out.println("<form method=\"get\" action=\"Proshop_fives\">");
       out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
       out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");
   }
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
   //  Now, call utility to scan the restriction table and update tee slots in teecurr accordingly
   //
   SystemUtils.do1Five(con, name);


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
 // 5-some Restriction already exists
 // *********************************************************

 private void dupMem(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, the <b>restriction name</b> you specified already exists in the database.<BR>");
   out.println("<BR>Please use the edit feature to change an existing restriction record.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");

 }

}
