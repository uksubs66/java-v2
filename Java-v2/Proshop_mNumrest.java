/***************************************************************************************     
 *   Proshop_mNumrest:  This servlet will process the 'Member Number Restriction' request from
 *                      the Proshop's Config page.
 *
 *
 *   called by:  proshop menu (to doGet) and Proshop_mNumrest (to doPost from HTML built here)
 *
 *   created: 2/04/2003   Bob P.
 *
 *   last updated:
 *
 *        5/24/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        8/15/08   Add more years to the start and end dates until we come up with a better method.
 *        8/12/08   Update to limited access user restrictions
 *        7/24/08  Added limited access proshop users checks
 *        6/09/06   Added javascript confirmation to Delete button
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

import com.foretees.common.Utilities;
import com.foretees.common.Connect;


public class Proshop_mNumrest extends HttpServlet {

    
   String zero = "00";
  
   String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //**************************************************
 // Process the initial request from Proshop_main
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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_RESTRICTIONS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_RESTRICTIONS", out);
   }
     
    // Define some parms to use in the html
    //
   String name = "";       // name of restriction
   int s_hour = 0;         // start time hr
   int s_min = 0;          // start time min
   int e_hour = 0;         // end time hr
   int e_min = 0;          // end time min
   int s_year = 0;         // start year
   int s_month = 0;        // start month
   int s_day = 0;          // start day
   int e_year = 0;         // end year
   int e_month = 0;        // end month
   int e_day = 0;          // end day
   int mems = 0;           // # of members for restriction
   int multi = 0;             // day
       
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   String courseName = ""; // name of course
   String fb = "";         // Front/back Indicator
   String s_ampm = "";
   String e_ampm = "";
   String recurr = "";

   boolean b = false;

   //
   //  First, see if lotteries are supported for this club
   //
   try {

      stmt = con.createStatement();        // create a statement
      rs = stmt.executeQuery("SELECT multi FROM club5");

      if (rs.next()) {

         multi = rs.getInt(1);
      }
      stmt.close();
   }
   catch (Exception ignore) {
   }

   //
   //  Build the HTML page to display the existing restrictions
   //
   out.println(SystemUtils.HeadTitle("Proshop Member Number Restrictions Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

      out.println("<table cellpadding=\"5\" border=\"0\" bgcolor=\"#336633\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>Member Number Restrictions</b><br>");
      out.println("</font>");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<br>Use this to restrict how many members with the same Member Number can play within a specified time period.");
      out.println("<br><br>");
      out.println("To change or remove a restriction, click on the Select button within the restriction.");

      out.println("<br>");
      out.println("</font></td></tr></table><br>");
      out.println("<br><br>");

      out.println("<table border=\"2\" cellpadding=\"5\"><tr bgcolor=\"#8B8970\">");
      if (multi != 0) {           // if multiple courses supported for this club
         out.println("<td colspan=\"10\" align=\"center\">");
      } else {
         out.println("<td colspan=\"9\" align=\"center\">");
      }
      out.println("<font size=\"2\">");
      out.println("<p align=\"center\"><b>Active Member Number Restrictions</b></p>");
      out.println("</font></td></tr>");
      out.println("<tr bgcolor=\"#8B8970\"><td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Restriction Name</b></p>");
      out.println("</font></td>");
      if (multi != 0) {
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Course</b></p>");
         out.println("</font></td>");
      }
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Tees</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Start Date</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>End Date</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Start Time</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>End Time</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Recurrence</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b># of Members</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p>&nbsp;</p>");      // empty for select button
      out.println("</font></td></tr>");

   //
   //  Get and display the existing restrictions (one table row per restriction)
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT * FROM mnumres2 ORDER BY name");

      while (rs.next()) {

         b = true;                     // indicate restrictions exist

         name = rs.getString("name");
         s_month = rs.getInt("start_mm");
         s_day = rs.getInt("start_dd");
         s_year = rs.getInt("start_yy");
         s_hour = rs.getInt("start_hr");
         s_min = rs.getInt("start_min");
         e_month = rs.getInt("end_mm");
         e_day = rs.getInt("end_dd");
         e_year = rs.getInt("end_yy");
         e_hour = rs.getInt("end_hr");
         e_min = rs.getInt("end_min");
         recurr = rs.getString("recurr");
         mems = rs.getInt("num_mems");
         courseName = rs.getString("courseName");
         fb = rs.getString("fb");

         //
         //  some values must be converted for display
         //
         s_ampm = " AM";         // default to AM
         if (s_hour > 12) {
            s_ampm = " PM";
            s_hour = s_hour - 12;                // convert to 12 hr clock value
         }

         if (s_hour == 12) {
            s_ampm = " PM";
         }

         e_ampm = " AM";         // default to AM
         if (e_hour > 12) {
            e_ampm = " PM";
            e_hour = e_hour - 12;                  // convert to 12 hr clock value
         }

         if (e_hour == 12) {
            e_ampm = " PM";
         }

         out.println("<tr bgcolor=\"#F5F5DC\">");
         out.println("<form method=\"post\" action=\"Proshop_mNumrest\" target=\"bot\">");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +name+ "</p>");
         out.println("</font></td>");
         if (multi != 0) {
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><p>" +courseName+ "</b></p>");
            out.println("</font></td>");
         }
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +fb+ "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +s_month+ "/" + s_day + "/" + s_year + "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +e_month+ "/" + e_day + "/" + e_year + "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (s_min < 10) {
            out.println("<font size=\"2\"><p>" + s_hour + ":0" + s_min + "  " + s_ampm + "</p>");
         } else {
            out.println("<font size=\"2\"><p>" + s_hour + ":" + s_min + "  " + s_ampm + "</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (e_min < 10) {
            out.println("<font size=\"2\"><p>" + e_hour + ":0" + e_min + "  " + e_ampm + "</b></p>");
         } else {
            out.println("<font size=\"2\"><p>" + e_hour + ":" + e_min + "  " + e_ampm + "</b></p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +recurr+ "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +mems+ "</p>");
         out.println("</font></td>");

         out.println("<input type=\"hidden\" maxlength=\"30\" name=\"name\" value=\"" + name + "\">");    // must pass whole name!!!!
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
         out.println("<td align=\"center\">");
         out.println("<p>");
         out.println("<input type=\"submit\" value=\"Select\">");
         out.println("</td></form></tr>");

      }  // end of while loop

      stmt.close();

      if (!b) {
        
         out.println("<p>No Member Number Restrictions Currently Exist</p>");
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
   out.println("</table></font>");                   // end of mNumrest table
   out.println("</td></tr></table>");                // end of main page table

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
  
   out.println("</center></font></body></html>");



 }  // end of doGet

 //
 //****************************************************************
 // Process the form request from Proshop_mNumrest page displayed above.
 //
 // Use the name provided to locate the restriction record and then display
 // the record to the user and prompt for edit or delete action. 
 //
 //****************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;
     
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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_RESTRICTIONS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_RESTRICTIONS", out);
   }
   
   //
   // Get the name parameter from the hidden input field
   //
   String name = req.getParameter("name");  
   String courseName = req.getParameter("course");
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  Scan name for special characters and replace with HTML supported chars (i.e. '>' = &gt)
   //
   //  *** 3/31/04 Do not do this as the name is received from the db via a hidden parm and therefore is not changed.
   //  *** do it below if query fails!!!
   //
//   name = SystemUtils.filter(name);
   
   String oldCourse = courseName;    // save course name in case it changes
   String oldName = name;                // save original event name


   long s_date = 0;
   long e_date = 0;
   int s_year = 0;
   int s_month = 0;
   int s_day = 0;
   int e_year = 0;
   int e_month = 0;
   int e_day = 0;
   int shr = 0;
   int smin = 0;
   int ehr = 0;
   int emin = 0;
   int mems = 0;
   int multi = 0;        // multiple course support option
   int index = 0;

   String recur = "";
   String fb = "";
   String courseName2 = "";
      
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);        // get the year
   int thisMonth = cal.get(Calendar.MONTH) + 1;
   int thisDay = cal.get(Calendar.DAY_OF_MONTH);

   //
   //  Array to hold the course names
   //
   ArrayList<String> course = new ArrayList<String>();


   //
   // Get the restriction from the restriction table
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
               "SELECT * FROM mnumres2 WHERE name = ? AND courseName = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);       // put the parm in stmt
      pstmt.setString(2, courseName);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         //
         //  Found the restriction record - get it
         //
         s_date = rs.getLong("sdate");
         s_month = rs.getInt("start_mm");
         s_day = rs.getInt("start_dd");
         s_year = rs.getInt("start_yy");
         shr = rs.getInt("start_hr");
         smin = rs.getInt("start_min");
         e_date = rs.getLong("edate");
         e_month = rs.getInt("end_mm");
         e_day = rs.getInt("end_dd");
         e_year = rs.getInt("end_yy");
         ehr = rs.getInt("end_hr");
         emin = rs.getInt("end_min");
         recur = rs.getString("recurr");
         mems = rs.getInt("num_mems");
         fb = rs.getString("fb");

      } else {      // not found - try filtering the name

         name = SystemUtils.filter(name);
         oldName = name;                 // save original name

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, name);       // put the parm in stmt
         pstmt.setString(2, courseName);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            //
            //  Found the restriction record - get it
            //
            s_date = rs.getLong("sdate");
            s_month = rs.getInt("start_mm");
            s_day = rs.getInt("start_dd");
            s_year = rs.getInt("start_yy");
            shr = rs.getInt("start_hr");
            smin = rs.getInt("start_min");
            e_date = rs.getLong("edate");
            e_month = rs.getInt("end_mm");
            e_day = rs.getInt("end_dd");
            e_year = rs.getInt("end_yy");
            ehr = rs.getInt("end_hr");
            emin = rs.getInt("end_min");
            recur = rs.getString("recurr");
            mems = rs.getInt("num_mems");
            fb = rs.getString("fb");
         }
      }
      pstmt.close();              // close the stmt

      //
      //  check if multi-couse is yes
      //
      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi " +
                             "FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);
      }

      stmt.close();

      if (multi != 0) {
         
         course = Utilities.getCourseNames(con);     // get all the course names
      }
   }                                         
   catch (Exception exc) {

      dbError(out, exc);
      return;
   }

   String ssampm = "AM";     // AM or PM for display (start hour)
   String seampm = "AM";     // AM or PM for display (end hour)
   int sampm = 0;
   int eampm = 0;

   if (shr > 12) {
      
      shr = shr - 12;        // convert to 12 hour value
      ssampm = "PM";         // indicate PM
      sampm = 12;
   }

   if (shr == 12) {
      ssampm = "PM";         // indicate PM
   }

   if (ehr > 12) {

      ehr = ehr - 12;        // convert to 12 hour value
      seampm = "PM";         // indicate PM
      eampm = 12;
   }

   if (ehr == 12) {
      seampm = "PM";         // indicate PM
   }

   String alphaSmonth = month_table[s_month];  // get name for start month
   String alphaEmonth = month_table[e_month];  // get name for end month

   //
   // Database record found - output an edit page
   //
   out.println(SystemUtils.HeadTitle("Proshop Edit Member Number Restriction"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
              
         out.println("<table cellpadding=\"5\" border=\"1\" bgcolor=\"#336633\" align=\"center\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>Edit Member Number Restriction</b><br>");
         out.println("<br>Change the desired information for the restriction below.");
         out.println("<br>Click on <b>Update</b> to submit the changes.");
         out.println("<br>Click on <b>Remove</b> to delete the restriction.");
         out.println("</font></td></tr></table><br>");

         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\">");
            out.println("<form action=\"Proshop_editmNumrest\" method=\"post\" target=\"bot\">");
               out.println("<tr><td width=\"500\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"left\">&nbsp;&nbsp;Restriction name:&nbsp;&nbsp;&nbsp;");
                  out.println("<input type=\"text\" name=\"rest_name\" value=\"" +name+ "\" size=\"30\" maxlength=\"30\">");
                  out.println("<br>&nbsp;&nbsp;&nbsp;* Must be unique");
                  out.println("<br><br>");

            if (multi != 0) {

               out.println("&nbsp;&nbsp;Select a Course:&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"course\">");

               if (courseName.equals( "-ALL-" )) {             // if same as existing name

                  out.println("<option selected value=\"-ALL-\">ALL</option>");
               } else {
                  out.println("<option value=\"-ALL-\">ALL</option>");
               }
               
               for (index=0; index < course.size(); index++) {

                  if (course.get(index).equals( courseName )) {             // if same as existing name

                     out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
                  } else {
                     out.println("<option value=\"" + course.get(index) + "\">" + course.get(index) + "</option>");
                  }
               }
               out.println("</select>");
               out.println("<br><br>");

            } else {

               out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
            }
              out.println("&nbsp;&nbsp;Tees:&nbsp;&nbsp;");
                out.println("<select size=\"1\" name=\"fb\">");
                if (fb.equals( "Both" )) {
                   out.println("<option selected value=\"Both\">Both</option>");
                } else {
                   out.println("<option value=\"Both\">Both</option>");
                }
                if (fb.equals( "Front" )) {
                   out.println("<option selected value=\"Front\">Front</option>");
                } else {
                   out.println("<option value=\"Front\">Front</option>");
                }
                if (fb.equals( "Back" )) {
                   out.println("<option selected value=\"Back\">Back</option>");
                } else {
                   out.println("<option value=\"Back\">Back</option>");
                }
              out.println("</select><br><br>");

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
                    for (int j = thisYear; j <= (thisYear + 5); j++) {
                        Common_Config.buildOption(j, j, s_year, out);
                    }
                    
                    out.println("</select><br><br>");
                  out.println("&nbsp;&nbsp;End Date:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
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
                    for (int j = thisYear; j <= (thisYear + 5); j++) {
                        Common_Config.buildOption(j, j, e_year, out);
                    }
                    
                    out.println("</select><br><br>");
                  out.println("&nbsp;&nbsp;Start Time:");
                  out.println("&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"start_hr\">");
                   if (shr == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (shr == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (shr == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (shr == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (shr == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                   if (shr == 6) {
                      out.println("<option selected value=\"06\">6</option>");
                   } else {
                      out.println("<option value=\"06\">6</option>");
                   }
                   if (shr == 7) {
                      out.println("<option selected value=\"07\">7</option>");
                   } else {
                      out.println("<option value=\"07\">7</option>");
                   }
                   if (shr == 8) {
                      out.println("<option selected value=\"08\">8</option>");
                   } else {
                      out.println("<option value=\"08\">8</option>");
                   }
                   if (shr == 9) {
                      out.println("<option selected value=\"09\">9</option>");
                   } else {
                      out.println("<option value=\"09\">9</option>");
                   }
                   if (shr == 10) {
                      out.println("<option selected value=\"10\">10</option>");
                   } else {
                      out.println("<option value=\"10\">10</option>");
                   }
                   if (shr == 11) {
                      out.println("<option selected value=\"11\">11</option>");
                   } else {
                      out.println("<option value=\"11\">11</option>");
                   }
                   if (shr == 12) {
                      out.println("<option selected value=\"12\">12</option>");
                   } else {
                      out.println("<option value=\"12\">12</option>");
                   }
                    out.println("</select>");
                    out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
                    if (smin < 10) {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + smin + " name=\"start_min\">");
                    } else {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + smin + " name=\"start_min\">");
                    }
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"start_ampm\">");
                   if (ssampm.equals( "AM" )) {
                      out.println("<option selected value=\"00\">AM</option>");
                   } else {
                      out.println("<option value=\"00\">AM</option>");
                   }
                   if (ssampm.equals( "PM" )) {
                      out.println("<option selected value=\"12\">PM</option>");
                   } else {
                      out.println("<option value=\"12\">PM</option>");
                   }
                    out.println("</select><br><br>");
                  out.println("&nbsp;&nbsp;End Time:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"end_hr\">");
                   if (ehr == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (ehr == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (ehr == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (ehr == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (ehr == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                   if (ehr == 6) {
                      out.println("<option selected value=\"06\">6</option>");
                   } else {
                      out.println("<option value=\"06\">6</option>");
                   }
                   if (ehr == 7) {
                      out.println("<option selected value=\"07\">7</option>");
                   } else {
                      out.println("<option value=\"07\">7</option>");
                   }
                   if (ehr == 8) {
                      out.println("<option selected value=\"08\">8</option>");
                   } else {
                      out.println("<option value=\"08\">8</option>");
                   }
                   if (ehr == 9) {
                      out.println("<option selected value=\"09\">9</option>");
                   } else {
                      out.println("<option value=\"09\">9</option>");
                   }
                   if (ehr == 10) {
                      out.println("<option selected value=\"10\">10</option>");
                   } else {
                      out.println("<option value=\"10\">10</option>");
                   }
                   if (ehr == 11) {
                      out.println("<option selected value=\"11\">11</option>");
                   } else {
                      out.println("<option value=\"11\">11</option>");
                   }
                   if (ehr == 12) {
                      out.println("<option selected value=\"12\">12</option>");
                   } else {
                      out.println("<option value=\"12\">12</option>");
                   }
                    out.println("</select>");
                    out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
                    if (emin < 10) {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + emin + " name=\"end_min\">");
                    } else {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + emin + " name=\"end_min\">");
                    }
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"end_ampm\">");
                   if (seampm.equals( "AM" )) {
                      out.println("<option selected value=\"00\">AM</option>");
                   } else {
                      out.println("<option value=\"00\">AM</option>");
                   }
                   if (seampm.equals( "PM" )) {
                      out.println("<option selected value=\"12\">PM</option>");
                   } else {
                      out.println("<option value=\"12\">PM</option>");
                   }
                    out.println("</select><br><br>");
                    out.println("&nbsp;&nbsp;Recurrence:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
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


               out.println("<br><br>");
                    out.println("&nbsp;&nbsp;Max Number of Members Allowed per Member Number:&nbsp;&nbsp;&nbsp;");
                    out.println("<select size=\"1\" name=\"mems\">");
                   if (mems == 1) {
                      out.println("<option selected value=\"1\">1</option>");
                   } else {
                      out.println("<option value=\"1\">1</option>");
                   }
                   if (mems == 2) {
                      out.println("<option selected value=\"2\">2</option>");
                   } else {
                      out.println("<option value=\"2\">2</option>");
                   }
                   if (mems == 3) {
                      out.println("<option selected value=\"3\">3</option>");
                   } else {
                      out.println("<option value=\"3\">3</option>");
                   }
                   if (mems == 4) {
                      out.println("<option selected value=\"4\">4</option>");
                   } else {
                      out.println("<option value=\"4\">4</option>");
                   }
                   if (mems == 5) {
                      out.println("<option selected value=\"5\">5</option>");
                   } else {
                      out.println("<option value=\"5\">5</option>");
                   }
                   if (mems == 6) {
                      out.println("<option selected value=\"6\">6</option>");
                   } else {
                      out.println("<option value=\"6\">6</option>");
                   }
                   if (mems == 7) {
                      out.println("<option selected value=\"7\">7</option>");
                   } else {
                      out.println("<option value=\"7\">7</option>");
                   }
                   if (mems == 8) {
                      out.println("<option selected value=\"8\">8</option>");
                   } else {
                      out.println("<option value=\"8\">8</option>");
                   }
                   if (mems == 9) {
                      out.println("<option selected value=\"9\">9</option>");
                   } else {
                      out.println("<option value=\"9\">9</option>");
                   }
                   if (mems == 10) {
                      out.println("<option selected value=\"10\">10</option>");
                   } else {
                      out.println("<option value=\"10\">10</option>");
                   }
                   if (mems == 11) {
                      out.println("<option selected value=\"11\">11</option>");
                   } else {
                      out.println("<option value=\"11\">11</option>");
                   }
                   if (mems == 12) {
                      out.println("<option selected value=\"12\">12</option>");
                   } else {
                      out.println("<option value=\"12\">12</option>");
                   }
                   if (mems == 13) {
                      out.println("<option selected value=\"13\">13</option>");
                   } else {
                      out.println("<option value=\"13\">13</option>");
                   }
                   if (mems == 14) {
                      out.println("<option selected value=\"14\">14</option>");
                   } else {
                      out.println("<option value=\"14\">14</option>");
                   }
                   if (mems == 15) {
                      out.println("<option selected value=\"15\">15</option>");
                   } else {
                      out.println("<option value=\"15\">15</option>");
                   }
                   if (mems == 16) {
                      out.println("<option selected value=\"16\">16</option>");
                   } else {
                      out.println("<option value=\"16\">16</option>");
                   }
                   if (mems == 17) {
                      out.println("<option selected value=\"17\">17</option>");
                   } else {
                      out.println("<option value=\"17\">17</option>");
                   }
                   if (mems == 18) {
                      out.println("<option selected value=\"18\">18</option>");
                   } else {
                      out.println("<option value=\"18\">18</option>");
                   }
                   if (mems == 19) {
                      out.println("<option selected value=\"19\">19</option>");
                   } else {
                      out.println("<option value=\"19\">19</option>");
                   }
                   if (mems == 20) {
                      out.println("<option selected value=\"20\">20</option>");
                   } else {
                      out.println("<option value=\"20\">20</option>");
                   }
                    out.println("</select></p><br>");

                  out.println("<input type=\"hidden\" name=\"oldName\" value=\"" + oldName + "\">");
                  out.println("<input type=\"hidden\" name=\"oldCourse\" value=\"" + oldCourse + "\">");

                  out.println("<p align=\"center\">");
            out.println("<input type=\"submit\" name=\"Update\" value=\"Update\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<input type=\"submit\" name=\"Delete\" value=\"Delete\" onclick=\"return confirm('Are you sure you want to delete this restriction?')\">");
                 
      out.println("</p></font></td></tr></form></table>");
      out.println("</td></tr></table>");                       // end of main page table

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_mNumrest\">");
   out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   out.println("</center></font></body></html>");

 }   // end of doPost   


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception exc) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Exception: " + exc);
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");

 }

}
