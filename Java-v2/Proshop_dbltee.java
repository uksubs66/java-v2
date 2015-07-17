/***************************************************************************************     
 *   Proshop_dbltee:  This servlet will process the 'Double Tees' request from
 *                    the Proshop's Configuration page.
 *
 *
 *   called by:  proshop menu (to doGet) and Proshop_dbltee (to doPost from HTML built here)
 *
 *   created: 3/04/2002   Bob P.
 *
 *   last updated:
 *
 *        5/19/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        8/15/08   Add more years to the start and end dates until we come up with a better method.
 *        8/12/08   Update to limited access user restrictions
 *        7/24/08   Added limited access proshop users checks
 *        1/24/05   Ver 5 - change club2 to club5.
 *        8/19/04   Add 'Make Copy' option to allow pro to copy an existing dbltee.
 *        1/15/04   Add 'old' parm values so editdbltee will know if changes made.
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
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.Utilities;

import com.foretees.common.Connect;

public class Proshop_dbltee extends HttpServlet {


   String zero = "00";

  
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //**************************************************
 // Process the initial request from proshop menu
 //**************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {
     
      return;
   }

   Connection con = Connect.getCon(req);                      // get DB connection

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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_TEESHEETS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_TEESHEETS", out);
   }
     
   //
   // Define some parms to use in the html
   //
   String name = "";       // name of dbltee
   int s_hour1 = 0;        // start time hr1
   int s_min1 = 0;         // start time min1
   int e_hour1 = 0;        // end time hr1
   int e_min1 = 0;         // end time min1
   int s_hour2 = 0;        // start time hr2
   int s_min2 = 0;         // start time min2
   int e_hour2 = 0;        // end time hr2
   int e_min2 = 0;         // end time min2
   int s_year = 0;         // start year
   int s_month = 0;        // start month
   int s_day = 0;          // start day
   int e_year = 0;         // end year
   int e_month = 0;        // end month
   int e_day = 0;          // end day
   int multi = 0;             // day

   String recurr = "";     // recurrence
   String course = "";     // course name
       
   String s_ampm1 = "";
   String e_ampm1 = "";
   String s_ampm2 = "";
   String e_ampm2 = "";

   boolean b = false;

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  Check if call is to copy an existing dbltee
   //
   if (req.getParameter("copy") != null) {

      doCopy(lottery, req, out, con);        // process the copy request
      return;
   }

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // golf only

   //
   // Get the Multiple Course Option
   //
   try {
      getClub.getParms(con, parm);        // get the club parms
      multi = parm.multi;
   }
   catch (Exception ignore) {
   }

   //
   //  Build the HTML page to display the existing dbltees
   //
   out.println(SystemUtils.HeadTitle("Proshop Member Double Tees Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
        
      out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>Double Tees</b>");
      out.println("</font>");
      out.println("<font color=\"#FFFFFF\" size=\"2\"><br><br>");
      out.println(" To change or remove a Double Tee, click on the Select button within the Double Tee.");
      out.println("<br>");
      out.println("</font></td></tr>");
      out.println("<tr bgcolor=\"#F5F5DC\"><td>");
      out.println("<font size=\"3\"><br>");
      out.println("<b>** Warning:</b></font><font size=\"2\">&nbsp;&nbsp;");
      out.println("This will take several minutes to complete. ");
      out.println("Once you start the process <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ");
      out.println("DO NOT try to disrupt it - let it complete.");
      out.println("<br></font></td></tr></table>");
      out.println("<br><br>");

      out.println("<table border=\"2\" cellpadding=\"5\"><tr bgcolor=\"#8B8970\">");
      if (multi != 0) {           // if multiple courses supported for this club
         out.println("<td colspan=\"10\" align=\"center\">");
      } else {
         out.println("<td colspan=\"9\" align=\"center\">");
      }
      out.println("<font size=\"2\">");
      out.println("<p align=\"center\"><b>Active Double Tee Settings</b></p>");
      out.println("</font></td></tr>");
      out.println("<tr bgcolor=\"#8B8970\"><td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Double Tee Name</b></p>");
      out.println("</font></td>");
      if (multi != 0) {
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Course</b></p>");
         out.println("</font></td>");
      }
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Start Date</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>End Date</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Double Tee<br>Start Time</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Double Tee<br>End Time</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Cross-Over<br>Start Time</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Cross-Over<br>End Time</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Recurrence</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p>&nbsp;</p>");      // empty for select button
      out.println("</font></td></tr>");

   //
   //  Get and display the existing Double Tees (one table row per Double Tee)
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT * FROM dbltee2 ORDER BY name");

      while (rs.next()) {

         b = true;                     // indicate Double Tees exist

         name = rs.getString("name");
         s_month = rs.getInt("start_mm");
         s_day = rs.getInt("start_dd");
         s_year = rs.getInt("start_yy");
         s_hour1 = rs.getInt("start_hr1");
         s_min1 = rs.getInt("start_min1");
         e_month = rs.getInt("end_mm");
         e_day = rs.getInt("end_dd");
         e_year = rs.getInt("end_yy");
         e_hour1 = rs.getInt("end_hr1");
         e_min1 = rs.getInt("end_min1");
         s_hour2 = rs.getInt("start_hr2");
         s_min2 = rs.getInt("start_min2");
         e_hour2 = rs.getInt("end_hr2");
         e_min2 = rs.getInt("end_min2");
         recurr = rs.getString("recurr");
         course = rs.getString("courseName");

         //
         //  some values must be converted for display
         //
         s_ampm1 = " AM";         // default to AM
         if (s_hour1 > 12) {
            s_ampm1 = " PM";
            s_hour1 = s_hour1 - 12;                // convert to 12 hr clock value
         }

         if (s_hour1 == 12) {
            s_ampm1 = " PM";
         }

         e_ampm1 = " AM";         // default to AM
         if (e_hour1 > 12) {
            e_ampm1 = " PM";
            e_hour1 = e_hour1 - 12;                  // convert to 12 hr clock value
         }

         if (e_hour1 == 12) {
            e_ampm1 = " PM";
         }

         s_ampm2 = " AM";         // default to AM
         if (s_hour2 > 12) {
            s_ampm2 = " PM";
            s_hour2 = s_hour2 - 12;                // convert to 12 hr clock value
         }

         if (s_hour2 == 12) {
            s_ampm2 = " PM";
         }

         e_ampm2 = " AM";         // default to AM
         if (e_hour2 > 12) {
            e_ampm2 = " PM";
            e_hour2 = e_hour2 - 12;                  // convert to 12 hr clock value
         }

         if (e_hour2 == 12) {
            e_ampm2 = " PM";
         }

         out.println("<tr bgcolor=\"#F5F5DC\">");
         out.println("<form method=\"post\" action=\"Proshop_dbltee\" target=\"bot\">");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +name+ "</p>");
         out.println("</font></td>");
         if (multi != 0) {
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><p>" +course+ "</b></p>");
            out.println("</font></td>");
         }
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +s_month+ "/" + s_day + "/" + s_year + "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +e_month+ "/" + e_day + "/" + e_year + "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (s_min1 < 10) {
            out.println("<font size=\"2\"><p>" + s_hour1 + ":0" + s_min1 + "  " + s_ampm1 + "</p>");
         } else {
            out.println("<font size=\"2\"><p>" + s_hour1 + ":" + s_min1 + "  " + s_ampm1 + "</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (e_min1 < 10) {
            out.println("<font size=\"2\"><p>" + e_hour1 + ":0" + e_min1 + "  " + e_ampm1 + "</b></p>");
         } else {
            out.println("<font size=\"2\"><p>" + e_hour1 + ":" + e_min1 + "  " + e_ampm1 + "</b></p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (s_min2 < 10) {
            out.println("<font size=\"2\"><p>" + s_hour2 + ":0" + s_min2 + "  " + s_ampm2 + "</p>");
         } else {
            out.println("<font size=\"2\"><p>" + s_hour2 + ":" + s_min2 + "  " + s_ampm2 + "</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (e_min2 < 10) {
            out.println("<font size=\"2\"><p>" + e_hour2 + ":0" + e_min2 + "  " + e_ampm2 + "</b></p>");
         } else {
            out.println("<font size=\"2\"><p>" + e_hour2 + ":" + e_min2 + "  " + e_ampm2 + "</b></p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +recurr+ "</p>");
         out.println("</font></td>");

         out.println("<input type=\"hidden\" maxlength=\"30\" name=\"name\" value=\"" + name + "\">");    // must pass whole name!!!!
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<td align=\"center\">");
         out.println("<p>");
         out.println("<input type=\"submit\" value=\"Select\">");
         out.println("</td></form></tr>");

      }  // end of while loop

      stmt.close();

      if (!b) {
        
         out.println("</font><font size=\"2\"><p>No Double Tee Entries Currently Exist</p>");
      }
   }
   catch (Exception exc) {

      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");

   }  // end of try

   //
   //  End of HTML page
   //
   out.println("</table></font>");                   // end of dbltee table
   out.println("</td></tr></table>");                // end of main page table
        
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   out.println("</center></font></body></html>");



 }  // end of doGet

 //
 //****************************************************************
 // Process the form request from Proshop_dbltee page displayed above.
 //
 // Use the name provided to locate the Double Tee record and then display
 // the record to the user and prompt for edit or delete action. 
 //
 //****************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   ResultSet rs = null;
   Statement stmt = null;
   Statement stmtc = null;
     
   String [] month_table = { "inv", "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
                             "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_TEESHEETS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_TEESHEETS", out);
   }
                                                                                                                 
   //
   // Get the name and course parameters from the hidden input field
   //
   String name = req.getParameter("name");  
   String courseName = "";
   String courseName1 = "";
     
   if (req.getParameter("course") != null) {

      courseName1 = req.getParameter("course");
   }

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   boolean copy = false;

   if (req.getParameter("copy") != null) {

      copy = true;            // this is a copy request (from doCopy below)
   }

   //
   //  Array to hold the course names
   //
   ArrayList<String> course = new ArrayList<String>();

   String oldCourse = courseName1;       // save original course name
   String recur = "";
   String oldrecur = "";

   long s_date = 0;
   long e_date = 0;
     
   int s_year = 0;
   int s_month = 0;
   int s_day = 0;
   int e_year = 0;
   int e_month = 0;
   int e_day = 0;
   int shr1 = 0;
   int smin1 = 0;
   int ehr1 = 0;
   int emin1 = 0;
   int shr2 = 0;
   int smin2 = 0;
   int ehr2 = 0;
   int emin2 = 0;
   int olds_year = 0;
   int olds_month = 0;
   int olds_day = 0;
   int olde_year = 0;
   int olde_month = 0;
   int olde_day = 0;
   int oldshr1 = 0;
   int oldsmin1 = 0;
   int oldehr1 = 0;
   int oldemin1 = 0;
   int oldshr2 = 0;
   int oldsmin2 = 0;
   int oldehr2 = 0;
   int oldemin2 = 0;
   int multi = 0;
   int i = 0;

   try {
      //
      // Get the Multiple Course Option from the club db
      //
      stmtc = con.createStatement();        // create a statement

      rs = stmtc.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);
      }
      stmtc.close();

      if (multi != 0) {           // if multiple courses supported for this club

         course = Utilities.getCourseNames(con);     // get all the course names
      }

      //
      // Get the Double Tee from the dbltee table
      //
      PreparedStatement pstmt1 = con.prepareStatement (
               "SELECT * FROM dbltee2 WHERE name = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);       // put the parm in pstmt1
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         s_date = rs.getLong("sdate");
         s_month = rs.getInt("start_mm");
         s_day = rs.getInt("start_dd");
         s_year = rs.getInt("start_yy");
         shr1 = rs.getInt("start_hr1");
         smin1 = rs.getInt("start_min1");
         e_date = rs.getLong("edate");
         e_month = rs.getInt("end_mm");
         e_day = rs.getInt("end_dd");
         e_year = rs.getInt("end_yy");
         ehr1 = rs.getInt("end_hr1");
         emin1 = rs.getInt("end_min1");
         shr2 = rs.getInt("start_hr2");
         smin2 = rs.getInt("start_min2");
         ehr2 = rs.getInt("end_hr2");
         emin2 = rs.getInt("end_min2");
         recur = rs.getString("recurr");
         courseName1 = rs.getString("courseName");

      } else {                             // not found - try filtering the name

         name = SystemUtils.filter(name);

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, name);       // put the parm in pstmt1
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            s_date = rs.getLong("sdate");
            s_month = rs.getInt("start_mm");
            s_day = rs.getInt("start_dd");
            s_year = rs.getInt("start_yy");
            shr1 = rs.getInt("start_hr1");
            smin1 = rs.getInt("start_min1");
            e_date = rs.getLong("edate");
            e_month = rs.getInt("end_mm");
            e_day = rs.getInt("end_dd");
            e_year = rs.getInt("end_yy");
            ehr1 = rs.getInt("end_hr1");
            emin1 = rs.getInt("end_min1");
            shr2 = rs.getInt("start_hr2");
            smin2 = rs.getInt("start_min2");
            ehr2 = rs.getInt("end_hr2");
            emin2 = rs.getInt("end_min2");
            recur = rs.getString("recurr");
            courseName1 = rs.getString("courseName");
         }
      }
      pstmt1.close();              // close the stmt

   }                                         
   catch (Exception exc) {

      dbError(out);
      return;
   }
     
   //
   //  save current values to pass on
   //
   oldrecur = recur;
   olds_month = s_month;
   olds_day = s_day;
   olds_year = s_year;
   oldshr1 = shr1;
   oldsmin1 = smin1;
   olde_month = e_month;
   olde_day = e_day;
   olde_year = e_year;
   oldehr1 = ehr1;
   oldemin1 = emin1;
   oldshr2 = shr2;
   oldsmin2 = smin2;
   oldehr2 = ehr2;
   oldemin2 = emin2;


   String ssampm1 = "AM";     // AM or PM for display (start hour)
   String seampm1 = "AM";     // AM or PM for display (end hour)
   String ssampm2 = "AM";     
   String seampm2 = "AM";     
   int sampm1 = 0;
   int eampm1 = 0;
   int sampm2 = 0;
   int eampm2 = 0;
   
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);        // get the year
   int thisMonth = cal.get(Calendar.MONTH) + 1;
   int thisDay = cal.get(Calendar.DAY_OF_MONTH);

   if (shr1 > 12) {
      
      shr1 = shr1 - 12;        // convert to 12 hour value
      ssampm1 = "PM";         // indicate PM
      sampm1 = 12;
   }

   if (shr1 == 12) {
      ssampm1 = "PM";         // indicate PM
   }

   if (ehr1 > 12) {

      ehr1 = ehr1 - 12;        // convert to 12 hour value
      seampm1 = "PM";         // indicate PM
      eampm1 = 12;
   }

   if (ehr1 == 12) {
      seampm1 = "PM";         // indicate PM
   }

   if (shr2 > 12) {

      shr2 = shr2 - 12;        // convert to 12 hour value
      ssampm2 = "PM";         // indicate PM
      sampm2 = 12;
   }

   if (shr2 == 12) {
      ssampm2 = "PM";         // indicate PM
   }

   if (ehr2 > 12) {

      ehr2 = ehr2 - 12;        // convert to 12 hour value
      seampm2 = "PM";         // indicate PM
      eampm2 = 12;
   }

   if (ehr2 == 12) {
      seampm2 = "PM";         // indicate PM
   }

   String alphaSmonth = month_table[s_month];  // get name for start month
   String alphaEmonth = month_table[e_month];  // get name for end month

   //
   // Database record found - output an edit page
   //
   out.println(SystemUtils.HeadTitle("Proshop Edit Double Tee"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td>");

      out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
        
      if (copy == false) {
         out.println("<b>Edit Double Tee</b><br>");
         out.println("<br>Change the desired information for the Double Tee below.<br>");
         out.println("Click on <b>Update</b> to submit the changes.");
         out.println("<br>Click on <b>Remove</b> to delete the Double Tee.");
      } else {
         out.println("<b>Copy Double Tee</b><br>");
         out.println("<br>Change the desired information for the double tee below.<br>");
         out.println("Click on <b>Add</b> to create the new double tee.");
         out.println("<br><br><b>NOTE:</b> You must change the name of the double tee.");
      }
      out.println("</font></td></tr></table><br>");

      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\">");
         if (copy == true) {
            out.println("<form action=\"Proshop_adddbltee\" method=\"post\" target=\"bot\">");
            out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");
         } else {
            out.println("<form action=\"Proshop_editdbltee\" method=\"post\" target=\"bot\">");
         }
            out.println("<tr><td>");
               out.println("<font size=\"2\">");
               if (copy == false) {
                  out.println("<p align=\"left\">&nbsp;&nbsp;Double Tee name:&nbsp;&nbsp;&nbsp;<b>" + name + "</b>");
               } else {
                  out.println("<p align=\"left\">&nbsp;&nbsp;Double Tee name:&nbsp;&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"dbltee_name\" value=\"" +name+ "\" size=\"30\" maxlength=\"30\">");
                  out.println("<br>&nbsp;&nbsp;&nbsp;* Must be changed!!");
               }
               out.println("<br><br>");
               out.println("&nbsp;&nbsp;Start Date:&nbsp;&nbsp;&nbsp;");
                 out.println("Month:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"smonth\">");
                   if (s_month == 1) {
                      out.println("<option selected value=\"01\">JAN</option>");
                   } else {
                      out.println("<option value=\"01\">JAN</option>");
                   }
                   if (s_month == 2) {
                      out.println("<option selected value=\"02\">FEB</option>");
                   } else {
                      out.println("<option value=\"02\">FEB</option>");
                   }
                   if (s_month == 3) {
                      out.println("<option selected value=\"03\">MAR</option>");
                   } else {
                      out.println("<option value=\"03\">MAR</option>");
                   }
                   if (s_month == 4) {
                      out.println("<option selected value=\"04\">APR</option>");
                   } else {
                      out.println("<option value=\"04\">APR</option>");
                   }
                   if (s_month == 5) {
                      out.println("<option selected value=\"05\">MAY</option>");
                   } else {
                      out.println("<option value=\"05\">MAY</option>");
                   }
                   if (s_month == 6) {
                      out.println("<option selected value=\"06\">JUN</option>");
                   } else {
                      out.println("<option value=\"06\">JUN</option>");
                   }
                   if (s_month == 7) {
                      out.println("<option selected value=\"07\">JUL</option>");
                   } else {
                      out.println("<option value=\"07\">JUL</option>");
                   }
                   if (s_month == 8) {
                      out.println("<option selected value=\"08\">AUG</option>");
                   } else {
                      out.println("<option value=\"08\">AUG</option>");
                   }
                   if (s_month == 9) {
                      out.println("<option selected value=\"09\">SEP</option>");
                   } else {
                      out.println("<option value=\"09\">SEP</option>");
                   }
                   if (s_month == 10) {
                      out.println("<option selected value=\"10\">OCT</option>");
                   } else {
                      out.println("<option value=\"10\">OCT</option>");
                   }
                   if (s_month == 11) {
                      out.println("<option selected value=\"11\">NOV</option>");
                   } else {
                      out.println("<option value=\"11\">NOV</option>");
                   }
                   if (s_month == 12) {
                      out.println("<option selected value=\"12\">DEC</option>");
                   } else {
                      out.println("<option value=\"12\">DEC</option>");
                   }
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"sday\">");
                   if (s_day == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (s_day == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (s_day == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (s_day == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (s_day == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                   if (s_day == 6) {
                      out.println("<option selected value=\"06\">6</option>");
                   } else {
                      out.println("<option value=\"06\">6</option>");
                   }
                   if (s_day == 7) {
                      out.println("<option selected value=\"07\">7</option>");
                   } else {
                      out.println("<option value=\"07\">7</option>");
                   }
                   if (s_day == 8) {
                      out.println("<option selected value=\"08\">8</option>");
                   } else {
                      out.println("<option value=\"08\">8</option>");
                   }
                   if (s_day == 9) {
                      out.println("<option selected value=\"09\">9</option>");
                   } else {
                      out.println("<option value=\"09\">9</option>");
                   }
                   if (s_day == 10) {
                      out.println("<option selected value=\"10\">10</option>");
                   } else {
                      out.println("<option value=\"10\">10</option>");
                   }
                   if (s_day == 11) {
                      out.println("<option selected value=\"11\">11</option>");
                   } else {
                      out.println("<option value=\"11\">11</option>");
                   }
                   if (s_day == 12) {
                      out.println("<option selected value=\"12\">12</option>");
                   } else {
                      out.println("<option value=\"12\">12</option>");
                   }
                   if (s_day == 13) {
                      out.println("<option selected value=\"13\">13</option>");
                   } else {
                      out.println("<option value=\"13\">13</option>");
                   }
                   if (s_day == 14) {
                      out.println("<option selected value=\"14\">14</option>");
                   } else {
                      out.println("<option value=\"14\">14</option>");
                   }
                   if (s_day == 15) {
                      out.println("<option selected value=\"15\">15</option>");
                   } else {
                      out.println("<option value=\"15\">15</option>");
                   }
                   if (s_day == 16) {
                      out.println("<option selected value=\"16\">16</option>");
                   } else {
                      out.println("<option value=\"16\">16</option>");
                   }
                   if (s_day == 17) {
                      out.println("<option selected value=\"17\">17</option>");
                   } else {
                      out.println("<option value=\"17\">17</option>");
                   }
                   if (s_day == 18) {
                      out.println("<option selected value=\"18\">18</option>");
                   } else {
                      out.println("<option value=\"18\">18</option>");
                   }
                   if (s_day == 19) {
                      out.println("<option selected value=\"19\">19</option>");
                   } else {
                      out.println("<option value=\"19\">19</option>");
                   }
                   if (s_day == 20) {
                      out.println("<option selected value=\"20\">20</option>");
                   } else {
                      out.println("<option value=\"20\">20</option>");
                   }
                   if (s_day == 21) {
                      out.println("<option selected value=\"21\">21</option>");
                   } else {
                      out.println("<option value=\"21\">21</option>");
                   }
                   if (s_day == 22) {
                      out.println("<option selected value=\"22\">22</option>");
                   } else {
                      out.println("<option value=\"22\">22</option>");
                   }
                   if (s_day == 23) {
                      out.println("<option selected value=\"23\">23</option>");
                   } else {
                      out.println("<option value=\"23\">23</option>");
                   }
                   if (s_day == 24) {
                      out.println("<option selected value=\"24\">24</option>");
                   } else {
                      out.println("<option value=\"24\">24</option>");
                   }
                   if (s_day == 25) {
                      out.println("<option selected value=\"25\">25</option>");
                   } else {
                      out.println("<option value=\"25\">25</option>");
                   }
                   if (s_day == 26) {
                      out.println("<option selected value=\"26\">26</option>");
                   } else {
                      out.println("<option value=\"26\">26</option>");
                   }
                   if (s_day == 27) {
                      out.println("<option selected value=\"27\">27</option>");
                   } else {
                      out.println("<option value=\"27\">27</option>");
                   }
                   if (s_day == 28) {
                      out.println("<option selected value=\"28\">28</option>");
                   } else {
                      out.println("<option value=\"28\">28</option>");
                   }
                   if (s_day == 29) {
                      out.println("<option selected value=\"29\">29</option>");
                   } else {
                      out.println("<option value=\"29\">29</option>");
                   }
                   if (s_day == 30) {
                      out.println("<option selected value=\"30\">30</option>");
                   } else {
                      out.println("<option value=\"30\">30</option>");
                   }
                   if (s_day == 31) {
                      out.println("<option selected value=\"31\">31</option>");
                   } else {
                      out.println("<option value=\"31\">31</option>");
                   }
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"syear\">");
                 
                 if (s_year < thisYear) {
                     Common_Config.buildOption(s_year, s_year, s_year, out);
                 }
                 for (i = thisYear; i <= (thisYear + 5); i++) {
                     Common_Config.buildOption(i, i, s_year, out);
                 }
                 out.println("</select><br><br>");
                 
               out.println("&nbsp;&nbsp;End Date:&nbsp;&nbsp;&nbsp;&nbsp;");
                 out.println("Month:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"emonth\">");
                   if (e_month == 1) {
                      out.println("<option selected value=\"01\">JAN</option>");
                   } else {
                      out.println("<option value=\"01\">JAN</option>");
                   }
                   if (e_month == 2) {
                      out.println("<option selected value=\"02\">FEB</option>");
                   } else {
                      out.println("<option value=\"02\">FEB</option>");
                   }
                   if (e_month == 3) {
                      out.println("<option selected value=\"03\">MAR</option>");
                   } else {
                      out.println("<option value=\"03\">MAR</option>");
                   }
                   if (e_month == 4) {
                      out.println("<option selected value=\"04\">APR</option>");
                   } else {
                      out.println("<option value=\"04\">APR</option>");
                   }
                   if (e_month == 5) {
                      out.println("<option selected value=\"05\">MAY</option>");
                   } else {
                      out.println("<option value=\"05\">MAY</option>");
                   }
                   if (e_month == 6) {
                      out.println("<option selected value=\"06\">JUN</option>");
                   } else {
                      out.println("<option value=\"06\">JUN</option>");
                   }
                   if (e_month == 7) {
                      out.println("<option selected value=\"07\">JUL</option>");
                   } else {
                      out.println("<option value=\"07\">JUL</option>");
                   }
                   if (e_month == 8) {
                      out.println("<option selected value=\"08\">AUG</option>");
                   } else {
                      out.println("<option value=\"08\">AUG</option>");
                   }
                   if (e_month == 9) {
                      out.println("<option selected value=\"09\">SEP</option>");
                   } else {
                      out.println("<option value=\"09\">SEP</option>");
                   }
                   if (e_month == 10) {
                      out.println("<option selected value=\"10\">OCT</option>");
                   } else {
                      out.println("<option value=\"10\">OCT</option>");
                   }
                   if (e_month == 11) {
                      out.println("<option selected value=\"11\">NOV</option>");
                   } else {
                      out.println("<option value=\"11\">NOV</option>");
                   }
                   if (e_month == 12) {
                      out.println("<option selected value=\"12\">DEC</option>");
                   } else {
                      out.println("<option value=\"12\">DEC</option>");
                   }
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"eday\">");
                   if (e_day == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (e_day == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (e_day == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (e_day == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (e_day == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                   if (e_day == 6) {
                      out.println("<option selected value=\"06\">6</option>");
                   } else {
                      out.println("<option value=\"06\">6</option>");
                   }
                   if (e_day == 7) {
                      out.println("<option selected value=\"07\">7</option>");
                   } else {
                      out.println("<option value=\"07\">7</option>");
                   }
                   if (e_day == 8) {
                      out.println("<option selected value=\"08\">8</option>");
                   } else {
                      out.println("<option value=\"08\">8</option>");
                   }
                   if (e_day == 9) {
                      out.println("<option selected value=\"09\">9</option>");
                   } else {
                      out.println("<option value=\"09\">9</option>");
                   }
                   if (e_day == 10) {
                      out.println("<option selected value=\"10\">10</option>");
                   } else {
                      out.println("<option value=\"10\">10</option>");
                   }
                   if (e_day == 11) {
                      out.println("<option selected value=\"11\">11</option>");
                   } else {
                      out.println("<option value=\"11\">11</option>");
                   }
                   if (e_day == 12) {
                      out.println("<option selected value=\"12\">12</option>");
                   } else {
                      out.println("<option value=\"12\">12</option>");
                   }
                   if (e_day == 13) {
                      out.println("<option selected value=\"13\">13</option>");
                   } else {
                      out.println("<option value=\"13\">13</option>");
                   }
                   if (e_day == 14) {
                      out.println("<option selected value=\"14\">14</option>");
                   } else {
                      out.println("<option value=\"14\">14</option>");
                   }
                   if (e_day == 15) {
                      out.println("<option selected value=\"15\">15</option>");
                   } else {
                      out.println("<option value=\"15\">15</option>");
                   }
                   if (e_day == 16) {
                      out.println("<option selected value=\"16\">16</option>");
                   } else {
                      out.println("<option value=\"16\">16</option>");
                   }
                   if (e_day == 17) {
                      out.println("<option selected value=\"17\">17</option>");
                   } else {
                      out.println("<option value=\"17\">17</option>");
                   }
                   if (e_day == 18) {
                      out.println("<option selected value=\"18\">18</option>");
                   } else {
                      out.println("<option value=\"18\">18</option>");
                   }
                   if (e_day == 19) {
                      out.println("<option selected value=\"19\">19</option>");
                   } else {
                      out.println("<option value=\"19\">19</option>");
                   }
                   if (e_day == 20) {
                      out.println("<option selected value=\"20\">20</option>");
                   } else {
                      out.println("<option value=\"20\">20</option>");
                   }
                   if (e_day == 21) {
                      out.println("<option selected value=\"21\">21</option>");
                   } else {
                      out.println("<option value=\"21\">21</option>");
                   }
                   if (e_day == 22) {
                      out.println("<option selected value=\"22\">22</option>");
                   } else {
                      out.println("<option value=\"22\">22</option>");
                   }
                   if (e_day == 23) {
                      out.println("<option selected value=\"23\">23</option>");
                   } else {
                      out.println("<option value=\"23\">23</option>");
                   }
                   if (e_day == 24) {
                      out.println("<option selected value=\"24\">24</option>");
                   } else {
                      out.println("<option value=\"24\">24</option>");
                   }
                   if (e_day == 25) {
                      out.println("<option selected value=\"25\">25</option>");
                   } else {
                      out.println("<option value=\"25\">25</option>");
                   }
                   if (e_day == 26) {
                      out.println("<option selected value=\"26\">26</option>");
                   } else {
                      out.println("<option value=\"26\">26</option>");
                   }
                   if (e_day == 27) {
                      out.println("<option selected value=\"27\">27</option>");
                   } else {
                      out.println("<option value=\"27\">27</option>");
                   }
                   if (e_day == 28) {
                      out.println("<option selected value=\"28\">28</option>");
                   } else {
                      out.println("<option value=\"28\">28</option>");
                   }
                   if (e_day == 29) {
                      out.println("<option selected value=\"29\">29</option>");
                   } else {
                      out.println("<option value=\"29\">29</option>");
                   }
                   if (e_day == 30) {
                      out.println("<option selected value=\"30\">30</option>");
                   } else {
                      out.println("<option value=\"30\">30</option>");
                   }
                   if (e_day == 31) {
                      out.println("<option selected value=\"31\">31</option>");
                   } else {
                      out.println("<option value=\"31\">31</option>");
                   }
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"eyear\">");
                 
                 if (e_year < thisYear) {
                     Common_Config.buildOption(e_year, e_year, e_year, out);
                 }
                 for (i = thisYear; i <= (thisYear + 5); i++) {
                     Common_Config.buildOption(i, i, e_year, out);
                 }
                 out.println("</select><br><br>");
                    
               //
               //  If multiple courses, then add a drop-down box for course names
               //
               if (multi != 0) {           // if multiple courses supported for this club

                  out.println("&nbsp;&nbsp;Course:&nbsp;&nbsp;");
                  out.println("<select size=\"1\" name=\"course\">");

                  if (courseName1.equals( "-ALL-" )) {             // if existing name is ALL

                     out.println("<option selected value=\"-ALL-\">ALL</option>");
                  } else {
                     out.println("<option value=\"-ALL-\">ALL</option>");
                  }

                  for (i=0; i<course.size(); i++) {

                     courseName = course.get(i);                    // get course name from array

                    if (courseName.equals( courseName1 )) {             // if same as existing name

                       out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
                    } else {
                       out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
                    }             
                  }
                  out.println("</select>");
                  out.println("<br><br>");

               } else {
                  out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
               }

                 out.println("&nbsp;&nbsp;Recurrence:&nbsp;&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"recurr\">");
                if (recur.equalsIgnoreCase( "every day" )) {
                   out.println("<option selected value=\"Every Day\">Every Day</option>");
                } else {
                   out.println("<option value=\"Every Day\">Every Day</option>");
                }
                if (recur.equalsIgnoreCase( "every sunday" )) {
                   out.println("<option selected value=\"Every Sunday\">Every Sunday</option>");
                } else {
                   out.println("<option value=\"Every Sunday\">Every Sunday</option>");
                }
                if (recur.equalsIgnoreCase( "every monday" )) {
                   out.println("<option selected value=\"Every Monday\">Every Monday</option>");
                } else {
                   out.println("<option value=\"Every Monday\">Every Monday</option>");
                }
                if (recur.equalsIgnoreCase( "every tuesday" )) {
                   out.println("<option selected value=\"Every Tuesday\">Every Tuesday</option>");
                } else {
                   out.println("<option value=\"Every Tuesday\">Every Tuesday</option>");
                }
                if (recur.equalsIgnoreCase( "every wednesday" )) {
                   out.println("<option selected value=\"Every Wednesday\">Every Wednesday</option>");
                } else {
                   out.println("<option value=\"Every Wednesday\">Every Wednesday</option>");
                }
                if (recur.equalsIgnoreCase( "every thursday" )) {
                   out.println("<option selected value=\"Every Thursday\">Every Thursday</option>");
                } else {
                   out.println("<option value=\"Every Thursday\">Every Thursday</option>");
                }
                if (recur.equalsIgnoreCase( "every friday" )) {
                   out.println("<option selected value=\"Every Friday\">Every Friday</option>");
                } else {
                   out.println("<option value=\"Every Friday\">Every Friday</option>");
                }
                if (recur.equalsIgnoreCase( "every saturday" )) {
                   out.println("<option selected value=\"Every Saturday\">Every Saturday</option>");
                } else {
                   out.println("<option value=\"Every Saturday\">Every Saturday</option>");
                }
                if (recur.equalsIgnoreCase( "all weekdays" )) {
                   out.println("<option selected value=\"All Weekdays\">All Weekdays</option>");
                } else {
                   out.println("<option value=\"All Weekdays\">All Weekdays</option>");
                }
                if (recur.equalsIgnoreCase( "all weekends" )) {
                   out.println("<option selected value=\"All Weekends\">All Weekends</option>");
                } else {
                   out.println("<option value=\"All Weekends\">All Weekends</option>");
                }
                 out.println("</select><br><br>");
               out.println("&nbsp;&nbsp;Start Time for Double Tees:");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"start_hr1\">");
                   if (shr1 == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (shr1 == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (shr1 == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (shr1 == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (shr1 == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                   if (shr1 == 6) {
                      out.println("<option selected value=\"06\">6</option>");
                   } else {
                      out.println("<option value=\"06\">6</option>");
                   }
                   if (shr1 == 7) {
                      out.println("<option selected value=\"07\">7</option>");
                   } else {
                      out.println("<option value=\"07\">7</option>");
                   }
                   if (shr1 == 8) {
                      out.println("<option selected value=\"08\">8</option>");
                   } else {
                      out.println("<option value=\"08\">8</option>");
                   }
                   if (shr1 == 9) {
                      out.println("<option selected value=\"09\">9</option>");
                   } else {
                      out.println("<option value=\"09\">9</option>");
                   }
                   if (shr1 == 10) {
                      out.println("<option selected value=\"10\">10</option>");
                   } else {
                      out.println("<option value=\"10\">10</option>");
                   }
                   if (shr1 == 11) {
                      out.println("<option selected value=\"11\">11</option>");
                   } else {
                      out.println("<option value=\"11\">11</option>");
                   }
                   if (shr1 == 12) {
                      out.println("<option selected value=\"12\">12</option>");
                   } else {
                      out.println("<option value=\"12\">12</option>");
                   }
                 out.println("</select>");
                 out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
                    if (smin1 < 10) {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + smin1 + " name=\"start_min1\">");
                    } else {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + smin1 + " name=\"start_min1\">");
                    }
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"start_ampm1\">");
                   if (ssampm1.equals( "AM" )) {
                      out.println("<option selected value=\"00\">AM</option>");
                   } else {
                      out.println("<option value=\"00\">AM</option>");
                   }
                   if (ssampm1.equals( "PM" )) {
                      out.println("<option selected value=\"12\">PM</option>");
                   } else {
                      out.println("<option value=\"12\">PM</option>");
                   }
                 out.println("</select><br><br>");
                 out.println("&nbsp;&nbsp;End Time:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                 out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                 out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                 out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"end_hr1\">");
                   if (ehr1 == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (ehr1 == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (ehr1 == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (ehr1 == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (ehr1 == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                   if (ehr1 == 6) {
                      out.println("<option selected value=\"06\">6</option>");
                   } else {
                      out.println("<option value=\"06\">6</option>");
                   }
                   if (ehr1 == 7) {
                      out.println("<option selected value=\"07\">7</option>");
                   } else {
                      out.println("<option value=\"07\">7</option>");
                   }
                   if (ehr1 == 8) {
                      out.println("<option selected value=\"08\">8</option>");
                   } else {
                      out.println("<option value=\"08\">8</option>");
                   }
                   if (ehr1 == 9) {
                      out.println("<option selected value=\"09\">9</option>");
                   } else {
                      out.println("<option value=\"09\">9</option>");
                   }
                   if (ehr1 == 10) {
                      out.println("<option selected value=\"10\">10</option>");
                   } else {
                      out.println("<option value=\"10\">10</option>");
                   }
                   if (ehr1 == 11) {
                      out.println("<option selected value=\"11\">11</option>");
                   } else {
                      out.println("<option value=\"11\">11</option>");
                   }
                   if (ehr1 == 12) {
                      out.println("<option selected value=\"12\">12</option>");
                   } else {
                      out.println("<option value=\"12\">12</option>");
                   }
                 out.println("</select>");
                 out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
                    if (emin1 < 10) {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + emin1 + " name=\"end_min1\">");
                    } else {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + emin1 + " name=\"end_min1\">");
                    }
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"end_ampm1\">");
                   if (seampm1.equals( "AM" )) {
                      out.println("<option selected value=\"00\">AM</option>");
                   } else {
                      out.println("<option value=\"00\">AM</option>");
                   }
                   if (seampm1.equals( "PM" )) {
                      out.println("<option selected value=\"12\">PM</option>");
                   } else {
                      out.println("<option value=\"12\">PM</option>");
                   }
                 out.println("</select><br><br>");

               out.println("&nbsp;&nbsp;Start Time for Cross-Over:");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"start_hr2\">");
                   if (shr2 == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (shr2 == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (shr2 == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (shr2 == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (shr2 == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                   if (shr2 == 6) {
                      out.println("<option selected value=\"06\">6</option>");
                   } else {
                      out.println("<option value=\"06\">6</option>");
                   }
                   if (shr2 == 7) {
                      out.println("<option selected value=\"07\">7</option>");
                   } else {
                      out.println("<option value=\"07\">7</option>");
                   }
                   if (shr2 == 8) {
                      out.println("<option selected value=\"08\">8</option>");
                   } else {
                      out.println("<option value=\"08\">8</option>");
                   }
                   if (shr2 == 9) {
                      out.println("<option selected value=\"09\">9</option>");
                   } else {
                      out.println("<option value=\"09\">9</option>");
                   }
                   if (shr2 == 10) {
                      out.println("<option selected value=\"10\">10</option>");
                   } else {
                      out.println("<option value=\"10\">10</option>");
                   }
                   if (shr2 == 11) {
                      out.println("<option selected value=\"11\">11</option>");
                   } else {
                      out.println("<option value=\"11\">11</option>");
                   }
                   if (shr2 == 12) {
                      out.println("<option selected value=\"12\">12</option>");
                   } else {
                      out.println("<option value=\"12\">12</option>");
                   }
                 out.println("</select>");
                 out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
                    if (smin2 < 10) {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + smin2 + " name=\"start_min2\">");
                    } else {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + smin2 + " name=\"start_min2\">");
                    }
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"start_ampm2\">");
                   if (ssampm2.equals( "AM" )) {
                      out.println("<option selected value=\"00\">AM</option>");
                   } else {
                      out.println("<option value=\"00\">AM</option>");
                   }
                   if (ssampm2.equals( "PM" )) {
                      out.println("<option selected value=\"12\">PM</option>");
                   } else {
                      out.println("<option value=\"12\">PM</option>");
                   }
                 out.println("</select><br><br>");
                 out.println("&nbsp;&nbsp;End Time:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                 out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                 out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                 out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"end_hr2\">");
                   if (ehr2 == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (ehr2 == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (ehr2 == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (ehr2 == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (ehr2 == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                   if (ehr2 == 6) {
                      out.println("<option selected value=\"06\">6</option>");
                   } else {
                      out.println("<option value=\"06\">6</option>");
                   }
                   if (ehr2 == 7) {
                      out.println("<option selected value=\"07\">7</option>");
                   } else {
                      out.println("<option value=\"07\">7</option>");
                   }
                   if (ehr2 == 8) {
                      out.println("<option selected value=\"08\">8</option>");
                   } else {
                      out.println("<option value=\"08\">8</option>");
                   }
                   if (ehr2 == 9) {
                      out.println("<option selected value=\"09\">9</option>");
                   } else {
                      out.println("<option value=\"09\">9</option>");
                   }
                   if (ehr2 == 10) {
                      out.println("<option selected value=\"10\">10</option>");
                   } else {
                      out.println("<option value=\"10\">10</option>");
                   }
                   if (ehr2 == 11) {
                      out.println("<option selected value=\"11\">11</option>");
                   } else {
                      out.println("<option value=\"11\">11</option>");
                   }
                   if (ehr2 == 12) {
                      out.println("<option selected value=\"12\">12</option>");
                   } else {
                      out.println("<option value=\"12\">12</option>");
                   }
                 out.println("</select>");
                 out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
                    if (emin2 < 10) {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + emin2 + " name=\"end_min2\">");
                    } else {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + emin2 + " name=\"end_min2\">");
                    }
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"end_ampm2\">");
                   if (seampm2.equals( "AM" )) {
                      out.println("<option selected value=\"00\">AM</option>");
                   } else {
                      out.println("<option value=\"00\">AM</option>");
                   }
                   if (seampm2.equals( "PM" )) {
                      out.println("<option selected value=\"12\">PM</option>");
                   } else {
                      out.println("<option value=\"12\">PM</option>");
                   }
                 out.println("</select></p>");

               out.println("<input type=\"hidden\" maxlength=\"30\" name=\"dbl_name\" value=\"" + name + "\">");
               out.println("<input type=\"hidden\" name=\"oldCourse\" value=\"" + oldCourse + "\">");
               out.println("<input type=\"hidden\" name=\"oldrecurr\" value=\"" + oldrecur + "\">");
               out.println("<input type=\"hidden\" name=\"oldsmonth\" value=\"" + olds_month + "\">");
               out.println("<input type=\"hidden\" name=\"oldsday\" value=\"" + olds_day + "\">");
               out.println("<input type=\"hidden\" name=\"oldsyear\" value=\"" + olds_year + "\">");
               out.println("<input type=\"hidden\" name=\"oldemonth\" value=\"" + olde_month + "\">");
               out.println("<input type=\"hidden\" name=\"oldeday\" value=\"" + olde_day + "\">");
               out.println("<input type=\"hidden\" name=\"oldeyear\" value=\"" + olde_year + "\">");
               out.println("<input type=\"hidden\" name=\"oldstart_hr1\" value=\"" + oldshr1 + "\">");
               out.println("<input type=\"hidden\" name=\"oldstart_min1\" value=\"" + oldsmin1 + "\">");
               out.println("<input type=\"hidden\" name=\"oldend_hr1\" value=\"" + oldehr1 + "\">");
               out.println("<input type=\"hidden\" name=\"oldend_min1\" value=\"" + oldemin1 + "\">");
               out.println("<input type=\"hidden\" name=\"oldstart_hr2\" value=\"" + oldshr2 + "\">");
               out.println("<input type=\"hidden\" name=\"oldstart_min2\" value=\"" + oldsmin2 + "\">");
               out.println("<input type=\"hidden\" name=\"oldend_hr2\" value=\"" + oldehr2 + "\">");
               out.println("<input type=\"hidden\" name=\"oldend_min2\" value=\"" + oldemin2 + "\">");

               out.println("<p align=\"center\"><b>NOTE: Be Patient - This may take a couple of minutes.</b><br>");
         if (copy == false) {
            out.println("<input type=\"submit\" name=\"Update\" value=\"Update\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<input type=\"submit\" name=\"Delete\" value=\"Delete\" onclick=\"return confirm('Are you sure you want to delete this restriction?\\n\\nThis will remove all empty Back 9 and Crossover tee times previously created for this Double Tee. Any occupied tee times will not be removed.')\">");
         } else {
            out.println("<input type=\"submit\" name=\"Add\" value=\"Add\">");
         }
           
      out.println("</p></font></td></tr></form></table>");
      out.println("</td></tr></table>");                       // end of main page table
        
   out.println("<br><font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_dbltee\">");
   out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   out.println("</center></font></body></html>");

 }   // end of doPost   


 // ***************************************************************************
 //  Process the copy request - display a selection list of existing dbltee's
 // ***************************************************************************

 private void doCopy(int lottery, HttpServletRequest req, PrintWriter out, Connection con) {

   Statement stmt = null;
   ResultSet rs = null;

   //
   // Define some parms to use in the html
   //
   String name = "";       // name of dbltee

   boolean b = false;

   //
   //  Build the HTML page to display the existing dbltees
   //
   out.println(SystemUtils.HeadTitle("Proshop Copy Double Tee Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

   out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"3\">");
   out.println("<b>Copy a Double Tee</b><br>");
   out.println("</font>");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<b>Instructions:</b>&nbsp;&nbsp;Use this feature to create a new double tee by copying an existing double tee.<br>");
   out.println("Select the double tee you wish to copy from the list below.");
   out.println("</font></td></tr></table>");
   out.println("<br><br>");

   //
   //  Get and display the existing dbltee
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT name FROM dbltee2");

      if (rs.next()) {

         b = true;                     // indicate dbltees exist
      }
      stmt.close();

      if (b == true) {

         out.println("<font size=\"2\">");
         out.println("<p>Select the Double Tee you wish to copy.</p>");

         out.println("<form action=\"Proshop_dbltee\" method=\"post\" target=\"bot\">");
         out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");     // tell dbltee its a copy
         out.println("<select size=\"1\" name=\"name\">");

         //
         //  Do again to actually get the names
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT name FROM dbltee2 ORDER BY name");

         while (rs.next()) {

            name = rs.getString("name");

            out.println("<option value=\"" +name+ "\">" +name+ "</option>");

         }  // end of while loop

         stmt.close();

         out.println("</select><br><br>");

         out.println("<input type=\"submit\" name=\"Continue\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");

      } else {            // no dbltee's exist

         out.println("<p><font size=\"2\">No Double Tees Currently Exist</p>");
      }
   }
   catch (Exception exc) {

      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");

   }  // end of try

   //
   //  End of HTML page
   //
   out.println("</td></tr></table>");                // end of main page table
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_dbltee\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center></font></body></html>");
   out.close();
 }


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");

 }

}
