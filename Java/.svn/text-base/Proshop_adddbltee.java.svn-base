/***************************************************************************************     
 *   Proshop_adddbltee:  This servlet will process the 'add double tee' request
 *                      from Proshop's adddbltee page.
 *
 *
 *   called by:  Proshop_dbltee to add a new double tee event
 *
 *   created: 3/04/2002   Bob P.
 *
 *   last updated:
 *
 *        5/19/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        9/02/08   Javascript compatability updates
 *        8/12/08   Update to limited access user restrictions
 *        7/24/08   Added limited access proshop users checks
 *        2/16/05   Ver 5 - start with today's date and use common function to set it.
 *        1/24/05   Ver 5 - change club2 to club5.
 *        8/19/04   Add 'Make Copy' option to allow pro to copy an existing dbltee.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        1/06/03   Enhancements for Version 2 of the software.
 *                  Add support for multiple courses.
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


public class Proshop_adddbltee extends HttpServlet {
 
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //****************************************************
 // Process the request from Proshop_dbltee
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

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H1>Database Connection Error</H1>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_TEESHEETS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_TEESHEETS", out);
   }

   //
   //  Array to hold the course names
   //
   ArrayList<String> course = new ArrayList<String>();

   String courseName = "";        // course names
   int multi = 0;               // multiple course support
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
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Build the HTML page to prompt user for the info
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Add Double Tee Main"));
       out.println("<script type=\"text/javascript\">");
       out.println("<!--");
       out.println("function cursor() { document.forms['f'].dbltee_name.focus(); }");
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
          out.println("<b>Double Tees</b><br>");
          out.println("Complete the following information for each Double Tee grouping to be added.<br>");
          out.println("Members will not be allowed to select tee times during the 'Cross-Over' period.<br>");
          out.println("Click on 'ADD' to add the Double Tee.");
       out.println("</font></td></tr></table><br>");

       out.println("<table border=\"1\" cols=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\">");
       out.println("<form action=\"/" +rev+ "/servlet/Proshop_adddbltee\" method=\"post\" target=\"bot\" name=\"f\">");
       out.println("<tr>");
          out.println("<td width=\"500\">");
             out.println("<font size=\"2\">");
             out.println("<p align=\"left\">&nbsp;&nbsp;Double Tee Name:&nbsp;&nbsp;");
                out.println("<input type=\"text\" name=\"dbltee_name\" size=\"30\" maxlength=\"30\">");
                out.println("</input>&nbsp;&nbsp;&nbsp;* Must be unique");
             out.println("</p>");
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
               
            //
            //  If multiple courses, then add a drop-down box for course names
            //
            if (multi != 0) {           // if multiple courses supported for this club

               out.println("<p align=\"Left\">&nbsp;&nbsp;Course:&nbsp;&nbsp;");
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
             out.println("<p align=\"left\">&nbsp;&nbsp;Start Time for Double Tees: &nbsp;&nbsp; hr &nbsp;");
               out.println("<select size=\"1\" name=\"start_hr1\">");
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
               out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"start_min1\">");
               out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"start_ampm1\">");
                 out.println("<option value=\"00\">AM</option>");
                 out.println("<option value=\"12\">PM</option>");
               out.println("</select>");
             out.println("</p>");
             out.println("<p align=\"left\">&nbsp;&nbsp;End Time for Double Tees: &nbsp;&nbsp;&nbsp; hr &nbsp;");
               out.println("<select size=\"1\" name=\"end_hr1\">");
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
               out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"end_min1\">");
               out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"end_ampm1\">");
                out.println("<option value=\"00\">AM</option>");
                 out.println("<option value=\"12\">PM</option>");
               out.println("</select>");
             out.println("</p>");

             out.println("<p align=\"left\">&nbsp;&nbsp;Start Time for Cross-Over: &nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;");
               out.println("<select size=\"1\" name=\"start_hr2\">");
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
               out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"start_min2\">");
               out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"start_ampm2\">");
                 out.println("<option value=\"00\">AM</option>");
                 out.println("<option value=\"12\">PM</option>");
               out.println("</select>");
             out.println("</p>");
             out.println("<p align=\"left\">&nbsp;&nbsp;End Time for Cross-Over: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;");
               out.println("<select size=\"1\" name=\"end_hr2\">");
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
               out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"end_min2\">");
               out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"end_ampm2\">");
                 out.println("<option value=\"00\">AM</option>");
                 out.println("<option value=\"12\">PM</option>");
               out.println("</select>");
             out.println("</p>");
             out.println("<p align=\"center\">");
               out.println("<input type=\"submit\" value=\"ADD\">");
               out.println("</input>&nbsp;&nbsp;(<b>Be Patient - This may take a couple of minutes.</b>)");
             out.println("</p>");
             out.println("</font>");
          out.println("</td>");
       out.println("</tr>");
       out.println("</form>");
       out.println("</table>");
       out.println("</font>");

       out.println("<font size=\"2\">");
       out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_dbltee\">");
       out.println("<input type=\"submit\" value=\"Done\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</input></form></font>");

    out.println("</center>");
    out.println("</body>");
    out.println("</html>");

 }  // end of doGet processing


 //****************************************************       
 // Process the form request from doGet processing
 //****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
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

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H1>Database Connection Error</H1>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_TEESHEETS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_TEESHEETS", out);
   }

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   boolean copy = false;

   if (req.getParameter("copy") != null) {

      copy = true;            // this is a copy request (from doCopy in _dbltee)
   }

   String s_month = "";             
   String s_day = "";                
   String s_year = "";               
      
   //
   // Get all the parameters entered
   //
   if (copy == false) {
      s_month = req.getParameter("month");
      s_day = req.getParameter("day");
      s_year = req.getParameter("year");
   } else {
      s_month = req.getParameter("smonth");
      s_day = req.getParameter("sday");
      s_year = req.getParameter("syear");
   }
   String name = req.getParameter("dbltee_name");            //  dbltee name
   String e_month = req.getParameter("emonth");             
   String e_day = req.getParameter("eday");                 
   String e_year = req.getParameter("eyear");               
   String start_hr1 = req.getParameter("start_hr1");         //  start hour (01 - 12)
   String start_min1 = req.getParameter("start_min1");       //  start min (00 - 59)
   String start_ampm1 = req.getParameter("start_ampm1");     //  AM/PM (00 or 12)
   String end_hr1 = req.getParameter("end_hr1");                
   String end_min1 = req.getParameter("end_min1");              
   String end_ampm1 = req.getParameter("end_ampm1");            
   String start_hr2 = req.getParameter("start_hr2");         //  start hour (01 - 12)
   String start_min2 = req.getParameter("start_min2");       //  start min (00 - 59)
   String start_ampm2 = req.getParameter("start_ampm2");     //  AM/PM (00 or 12)
   String end_hr2 = req.getParameter("end_hr2");
   String end_min2 = req.getParameter("end_min2");
   String end_ampm2 = req.getParameter("end_ampm2");
   String recurr = req.getParameter("recurr");              
   String course = req.getParameter("course");

   int smonth = 0;                // init integer date/time values
   int sday = 0;
   int syear = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int s_hr1 = 0;
   int s_min1 = 0;
   int s_ampm1 = 0;
   int e_hr1 = 0;
   int e_min1 = 0;
   int e_ampm1 = 0;
   int s_hr2 = 0;
   int s_min2 = 0;
   int s_ampm2 = 0;
   int e_hr2 = 0;
   int e_min2 = 0;
   int e_ampm2 = 0;
   int status = 0;
     
   short xx = 0;
   
   //
   // Convert the numeric string parameters to Int's
   //
   try {
      smonth = Integer.parseInt(s_month);
      sday = Integer.parseInt(s_day);
      syear = Integer.parseInt(s_year);
      emonth = Integer.parseInt(e_month);
      eday = Integer.parseInt(e_day);
      eyear = Integer.parseInt(e_year);
      s_hr1 = Integer.parseInt(start_hr1);
      s_min1 = Integer.parseInt(start_min1);
      s_ampm1 = Integer.parseInt(start_ampm1);
      e_hr1 = Integer.parseInt(end_hr1);
      e_min1 = Integer.parseInt(end_min1);
      e_ampm1 = Integer.parseInt(end_ampm1);
      s_hr2 = Integer.parseInt(start_hr2);
      s_min2 = Integer.parseInt(start_min2);
      s_ampm2 = Integer.parseInt(start_ampm2);
      e_hr2 = Integer.parseInt(end_hr2);
      e_min2 = Integer.parseInt(end_min2);
      e_ampm2 = Integer.parseInt(end_ampm2);
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
   //  Scan name for special characters and replace with HTML supported chars (i.e. '>' = &gt)
   //
//   name = SystemUtils.filter(name);

   //
   //  Verify length of name (special chars can force error)
   //
   if (name.length() > SystemUtils.DBLTL) {

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
      out.println("</input></form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Check if dbltee already exists in database
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
              "SELECT sdate FROM dbltee2 WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);       // put the parm in stmt
      rs = pstmt.executeQuery();      // execute the prepared stmt
        
      if (rs.next()) {

         dupMem(out);                 // dbltee exists - inform the user and return
         pstmt.close();
         return;
      }
      pstmt.close();
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

   if (s_hr1 != 12) {                // _hr specified as 01 - 12 (_ampm = 00 or 12)
                              
      s_hr1 = s_hr1 + s_ampm1;         // convert to military time (12 is always Noon, or PM)
   }

   if (e_hr1 != 12) {                // ditto

      e_hr1 = e_hr1 + e_ampm1;       
   }

   int stime1 = s_hr1 * 100;
   stime1 = stime1 + s_min1;
   int etime1 = e_hr1 * 100;
   etime1 = etime1 + e_min1;
 
   if (s_hr2 != 12) {                // _hr specified as 01 - 12 (_ampm = 00 or 12)

      s_hr2 = s_hr2 + s_ampm2;         // convert to military time (12 is always Noon, or PM)
   }

   if (e_hr2 != 12) {                // ditto

      e_hr2 = e_hr2 + e_ampm2;
   }

   int stime2 = s_hr2 * 100;
   stime2 = stime2 + s_min2;
   int etime2 = e_hr2 * 100;
   etime2 = etime2 + e_min2;

   //
   //  verify the date and time fields
   //
   if ((sdate > edate) || (stime1 > etime1) || (stime2 > etime2)) {

      invData(out);    // inform the user and return
      return;
   }

   //
   //  add dbltee data to the database
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
        "INSERT INTO dbltee2 (name, sdate, start_mm, start_dd, start_yy, start_hr1, start_min1, stime1, " +
        "edate, end_mm, end_dd, end_yy, end_hr1, end_min1, etime1, start_hr2, start_min2, stime2, " +
        "end_hr2, end_min2, etime2, recurr, courseName) VALUES " +
        "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);       // put the parm in pstmt1
      pstmt1.setLong(2, sdate);
      pstmt1.setInt(3, smonth);
      pstmt1.setInt(4, sday);
      pstmt1.setInt(5, syear);
      pstmt1.setInt(6, s_hr1);
      pstmt1.setInt(7, s_min1);
      pstmt1.setInt(8, stime1);
      pstmt1.setLong(9, edate);
      pstmt1.setInt(10, emonth);
      pstmt1.setInt(11, eday);
      pstmt1.setInt(12, eyear);
      pstmt1.setInt(13, e_hr1);
      pstmt1.setInt(14, e_min1);
      pstmt1.setInt(15, etime1);
      pstmt1.setInt(16, s_hr2);
      pstmt1.setInt(17, s_min2);
      pstmt1.setInt(18, stime2);
      pstmt1.setInt(19, e_hr2);
      pstmt1.setInt(20, e_min2);
      pstmt1.setInt(21, etime2);
      pstmt1.setString(22, recurr);
      pstmt1.setString(23, course);
      pstmt1.executeUpdate();          // execute the prepared stmt

      pstmt1.close();   // close the stmt

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR>Exception:   " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Get the club parameters from clubparm table - check if we should wait to build tee times
   //
   try {
     
      if (course.equals( "-ALL-" )) {
        
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName, xx FROM clubparm2");

         while (rs.next()) {

            course = rs.getString(1);
            xx = rs.getShort(2);

            if (xx == 2) {             // if we've been waiting for dbltee config

               xx = 1;                 // no longer have to wait

               PreparedStatement pstmt2 = con.prepareStatement ("UPDATE clubparm2 SET xx = ? WHERE courseName = ?");

               pstmt2.clearParameters();            // clear the parms
               pstmt2.setShort(1, xx);              // put the parm in stmt
               pstmt2.setString(2, course);        
               pstmt2.executeUpdate();              // execute the prepared stmt

               pstmt2.close();
            }
         }
         stmt.close();

      } else {             // individual course

         PreparedStatement pstmt1 = con.prepareStatement ("SELECT xx FROM clubparm2 WHERE courseName = ?");

         pstmt1.clearParameters();            // clear the parms
         pstmt1.setString(1, course);
         rs = pstmt1.executeQuery();              // execute the prepared stmt

         if (rs.next()) {

            xx = rs.getShort(1);

            if (xx == 2) {             // if we've been waiting for dbltee config

               xx = 1;                 // no longer have to wait

               PreparedStatement pstmt2 = con.prepareStatement ("UPDATE clubparm2 SET xx = ? WHERE courseName = ?");

               pstmt2.clearParameters();            // clear the parms
               pstmt2.setShort(1, xx);              // put the parm in stmt
               pstmt2.setString(2, course);
               pstmt2.executeUpdate();              // execute the prepared stmt

               pstmt2.close();
            }
         }
         pstmt1.close();
      }

   }
   catch (Exception ignore) {
   }

   //
   //  Call SystemUtils to build the new double tees if tee sheets exist
   //
   try {
     
      status = SystemUtils.buildDblTee(name, con);

   }
   catch (Exception exc) { 

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR>Exception:   " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Add Double Tee"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>Double Tee Has Been Added</H3>");
   out.println("<BR><BR>Thank you, the Double Tee has been added to the system database.");
      
   if (status == 0) {  // if no double tees built
      out.println("<BR><BR><b>NOTE:</b>  If this is your first time setting up double tees, finish the<br>");
      out.println("configuration of all parameters and restrictions.  Then select the 'Build Tee Sheets' option.");
   }
   if (status == 1) {  // if built ok
      out.println("<BR><BR>The tee sheets have been updated to reflect the changes.");
   }
   if (status == 2) {  // if built, but conflict encountered
      out.println("<BR><BR><b>NOTE:</b> The tee sheets have been updated, however there was a <br>");
      out.println("problem with one or more of the days affected.  There may have been a conflict<br>");
      out.println("with another Double Tee, or there may have already been players assigned to one<br>");
      out.println("or more of the tee times affected by this Double Tee.");
      out.println("<BR><BR>Please review your tee sheets to verify the Double Tee was built properly.");
   }

   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   if (copy == false) {
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_adddbltee\">");
   } else {
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_dbltee\">");
      out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");
   }
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");

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
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");

 }

 // *********************************************************
 // Member dbltee already exists
 // *********************************************************

 private void dupMem(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, the <b>Double Tee name</b> you specified already exists in the database.<BR>");
   out.println("<BR>Please use the edit feature to change an existing restriction record.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");

 }

}
