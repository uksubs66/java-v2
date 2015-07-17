/***************************************************************************************     
 *   Proshop_memNotice:  This servlet will process the 'Member Notices' request from
 *                       the Proshop's Config page.
 *
 *
 *   called by:  proshop menu (to doGet) and Proshop_memNotice (to doPost from HTML built here)
 *
 *   created: 2/22/2007   Bob P.
 *
 *   last updated:
 *
 *           5/24/10  Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *          04/21/10  Added a check while loading courses to not attempt to read the name of a 21st course.
 *          03/16/09  Added note to clarify how to add line breaks to member notice text
 *          09/05/08  Added Tee Sheet/Background Color option for member notice configuration
 *          08/07/08  Modified limited access proshop user restrictions
 *          07/24/08  Added limited access proshop users checks
 *          08/20/07  Added proside field to config for making notices appear on proside
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


public class Proshop_memNotice extends HttpServlet {

    
   String zero = "00";
   String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

  
 //************************************************************************
 //
 // Process the initial request from Proshop menus
 //
 //  doGet:  Get control from menus to list all current Member Notices,
 //          or to copy an existing mem notice (from list).
 //
 //************************************************************************
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

   Connection con = SystemUtils.getCon(session);                      // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_MEMBERNOTICES", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_MEMBERNOTICES", out);
   }
     
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  Check if call is to copy an existing notice
   //
   if (req.getParameter("copy") != null) {

      doCopy(req, out, con, lottery);        // process the copy request
      return;
   }

   //
   // Define some parms to use in the html
   //
   int id = 0;             // record id
   int stime = 0;          // start time
   int etime = 0;          // end time
   int s_hour = 0;         // start time hr
   int s_min = 0;          // start time min
   int e_hour = 0;         // end time hr
   int e_min = 0;          // end time min
   //int guests = 0;         // # of guests
   int multi = 0;
   int teetime = 0;
   int event = 0;
   int proside = 0;
   int teesheet = 0;
   //int overrideColors = 0;
     
   long sdate = 0;
   long edate = 0;
   long s_year = 0;         // start year
   long s_month = 0;        // start month
   long s_day = 0;          // start day
   long e_year = 0;         // end year
   long e_month = 0;        // end month
   long e_day = 0;          // end day
       
   String name = "";       // name of notice
   String courseName = ""; // name of course
   String fb = "";         // Front/back Indicator
   String s_ampm = "";
   String e_ampm = "";
   //String color = "";      // color for notice displays
   //String per = "";
   String bgColor = "";

   boolean b = false;

   //
   //  First, see if multi courses for this club
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi " +
                             "FROM club5");
      if (rs.next()) {
         multi = rs.getInt(1);
      }
      stmt.close();
   }
   catch (Exception ignore) {
   }

   //
   //  Build the HTML page to display the existing notices
   //
   out.println(SystemUtils.HeadTitle("Proshop Member Notices Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

      out.println("<table cellpadding=\"5\" border=\"0\" bgcolor=\"#336633\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>Member Notices</b><br>");
      out.println("</font>");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<br>To change or remove a member notice, click on the Select button within the member notice.");

      out.println("<br>");
      out.println("</font></td></tr></table><br><br>");

      out.println("<table border=\"2\" cellpadding=\"5\"><tr bgcolor=\"#8B8970\">");
      if (multi != 0) {           // if multiple courses supported for this club
         out.println("<td colspan=\"12\" align=\"center\">");
      } else {
         out.println("<td colspan=\"11\" align=\"center\">");
      }
      out.println("<font size=\"2\">");
      out.println("<p align=\"center\"><b>Active Member Notices</b></p>");
      out.println("</font></td></tr>");
      out.println("<tr bgcolor=\"#8B8970\"><td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Name of Member Notice</b></p>");
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
      out.println("<font size=\"2\"><p><b>For Tee Times</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>For Events</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Tee Sheet</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Proshop Side</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p>&nbsp;</p>");      // empty for select button
      out.println("</font></td></tr>");

   //
   //  Get and display the existing notices (one table row per notice)
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT * FROM mem_notice ORDER BY name");

      while (rs.next()) {

         b = true;                     // indicate notices exist

         id = rs.getInt("mem_notice_id");
         name = rs.getString("name");
         sdate = rs.getLong("sdate");
         edate = rs.getLong("edate");
         stime = rs.getInt("stime");
         etime = rs.getInt("etime");
         teetime = rs.getInt("teetime");
         event = rs.getInt("event");
         courseName = rs.getString("courseName");
         fb = rs.getString("fb");
         proside = rs.getInt("proside");
         teesheet = rs.getInt("teesheet");
         bgColor = rs.getString("bgColor");
         
         

         //
         //  some values must be converted for display
         //
         s_hour = stime / 100;             // get hour
         s_min = stime - (s_hour * 100);   // get minute
           
         s_ampm = " AM";                   // default to AM
         if (s_hour > 12) {
            s_ampm = " PM";
            s_hour = s_hour - 12;                // convert to 12 hr clock value
         }

         if (s_hour == 12) {
            s_ampm = " PM";
         }

         e_hour = etime / 100;             // get hour
         e_min = etime - (e_hour * 100);   // get minute

         e_ampm = " AM";                   // default to AM
         if (e_hour > 12) {
            e_ampm = " PM";
            e_hour = e_hour - 12;                  // convert to 12 hr clock value
         }

         if (e_hour == 12) {
            e_ampm = " PM";
         }

         s_year = sdate / 10000;                                // get year
         s_month = (sdate - (s_year * 10000)) / 100;            // get month 
         s_day = sdate - ((s_year * 10000) + (s_month * 100));  // get day

         e_year = edate / 10000;                                // get year
         e_month = (edate - (e_year * 10000)) / 100;            // get month
         e_day = edate - ((e_year * 10000) + (e_month * 100));  // get day


         if (!bgColor.equals("")) {
             out.println("<tr bgcolor=\"" + bgColor + "\">");
         } else {
             out.println("<tr bgcolor=\"#F5F5DC\">");
         }
         
         out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_memNotice\" target=\"bot\">");
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
         if (teetime > 0) {
            out.println("<font size=\"2\"><p>Y</p>");
         } else {
            out.println("<font size=\"2\"><p>N</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (event > 0) {
            out.println("<font size=\"2\"><p>Y</p>");
         } else {
            out.println("<font size=\"2\"><p>N</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (teesheet > 0) {
            out.println("<font size=\"2\"><p>Y</p>");
         } else {
            out.println("<font size=\"2\"><p>N</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (proside > 0) {
            out.println("<font size=\"2\"><p>Y</p>");
         } else {
            out.println("<font size=\"2\"><p>N</p>");
         }
         out.println("</font></td>");

         out.println("<input type=\"hidden\" name=\"notice_id\" value=\"" + id + "\">");
         out.println("<td align=\"center\">");
         out.println("<p>");
         out.println("<input type=\"submit\" value=\"Select\">");
         out.println("</td></form></tr>");

      }  // end of while loop

      stmt.close();

      if (!b) {
        
         out.println("<p>No Member Notices Currently Exist</p>");
      }
   }
   catch (Exception e2) {

      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR><BR>Exception: " + e2);
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");

   }  // end of try

   //
   //  End of HTML page
   //
   out.println("</table></font>");                   // end of memNotice table
   out.println("</td></tr></table>");                // end of main page table

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
  
   out.println("</center></font></body></html>");

 }  // end of doGet



 //
 //****************************************************************
 //
 //  doPost:  Get control from above to edit an existing mem notice,
 //           or from menus to add or copy a mem notice.
 //
 //****************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;
     
   //String [] month_table = { "inv", "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
   //                          "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_MEMBERNOTICES", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_MEMBERNOTICES", out);
   }

   boolean copy = false;

   long sdate = 0;
   long edate = 0;
   long s_year = 0;
   long s_month = 0;
   long s_day = 0;
   long e_year = 0;
   long e_month = 0;
   long e_day = 0;
     
   int stime = 0;
   int etime = 0;
   int shr = 0;
   int smin = 0;
   int ehr = 0;
   int emin = 0;
   int multi = 0;        // multiple course support option
   int index = 0;
   int id = 0;
   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int teetime = 0;
   int event = 0;
   int i = 0;
   //int i2 = 0;
   int proside = 0;
   int teesheet = 0;
   //int overrideColors = 0;        // Removed for now, may implement at a later date

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   String name = "";                // name of notice
   //String oldName = "";             // original name
   String notice_id = "";
   //String temp = "";
   String fb = "";
   String message = "";
   String courseName = "";
   String bgColor = "";

   //
   //  Arrays to hold the course names and guest types
   //
   ArrayList<String> course = new ArrayList<String>();

   
   //
   // Get the record id, if present call is for copy or edit (if not, then its an 'add')
   //
   if (req.getParameter("notice_id") != null) {
     
      notice_id = req.getParameter("notice_id");
   }

   if (!notice_id.equals( "" )) {        // if id provided (not an 'add')

      try {
         id = Integer.parseInt(notice_id);
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }
   }

   if (req.getParameter("copy") != null) {

      copy = true;            // this is a copy request (from doCopy below)
   }

   if (req.getParameter("step2") != null) {    // if call is from this doPost processing

      if (id > 0) {       // if record id provided
        
         doEdit(id, req, out, con);         // go process the edit/delete request
           
      } else {
        
         doAdd(copy, req, out, con);        // go process the add/copy request
      }
      return;          // done
   }

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // golf only feature

   try {

      getClub.getParms(con, parm);        //  get the club parameters (multi, guest types, hotel)
   }
   catch (Exception exc) {
   }

   try {

      //
      // Get the notice from the notice table
      //
      if (id > 0) {        // if edit or copy

         PreparedStatement pstmt = con.prepareStatement (
                  "SELECT * FROM mem_notice WHERE mem_notice_id = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, id);       // put the parm in stmt
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            //
            //  Found the notice record - get it
            //
            name = rs.getString("name");
            sdate = rs.getLong("sdate");
            stime = rs.getInt("stime");
            edate = rs.getLong("edate");
            etime = rs.getInt("etime");
            mon = rs.getInt("mon");
            tue = rs.getInt("tue");
            wed = rs.getInt("wed");
            thu = rs.getInt("thu");
            fri = rs.getInt("fri");
            sat = rs.getInt("sat");
            sun = rs.getInt("sun");
            teetime = rs.getInt("teetime");
            event = rs.getInt("event");
            courseName = rs.getString("courseName");
            fb = rs.getString("fb");
            message = rs.getString("message");
            proside = rs.getInt("proside");
            teesheet = rs.getInt("teesheet");
            //overrideColors = rs.getInt("overrideColors");        // Removed for now, may implement at a later date
            bgColor = rs.getString("bgColor");
              
            //
            //  some values must be converted for display
            //
            shr = stime / 100;             // get hour
            smin = stime - (shr * 100);   // get minute

            ehr = etime / 100;             // get hour
            emin = etime - (ehr * 100);   // get minute

            s_year = sdate / 10000;                                // get year
            s_month = (sdate - (s_year * 10000)) / 100;            // get month
            s_day = sdate - ((s_year * 10000) + (s_month * 100));  // get day

            e_year = edate / 10000;                                // get year
            e_month = (edate - (e_year * 10000)) / 100;            // get month
            e_day = edate - ((e_year * 10000) + (e_month * 100));  // get day

         }
         pstmt.close();              // close the stmt
           
      }

      multi = parm.multi;

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
   //int sampm = 0;
   //int eampm = 0;

   if (shr > 12) {
      
      shr = shr - 12;        // convert to 12 hour value
      ssampm = "PM";         // indicate PM
      //sampm = 12;
   }

   if (shr == 12) {
      ssampm = "PM";         // indicate PM
   }

   if (ehr > 12) {

      ehr = ehr - 12;        // convert to 12 hour value
      seampm = "PM";         // indicate PM
      //eampm = 12;
   }

   if (ehr == 12) {
      seampm = "PM";         // indicate PM
   }

   //i = (int)s_month;
     
   //String alphaSmonth = month_table[i];  // get name for start month
     
   //i = (int)e_month;

   //String alphaEmonth = month_table[i];  // get name for end month

   i = 0;
     
   //
   // Database record found - output an edit page
   //
   out.println(SystemUtils.HeadTitle("Proshop Edit Member Notice"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
              
         out.println("<table cellpadding=\"5\" border=\"1\" bgcolor=\"#336633\" align=\"center\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
           
         if (copy == false && id > 0) {
            out.println("<b>Edit Member Notice</b><br>");
            out.println("<br>Change the desired information for the member notice below.");
            out.println("<br>Click on <b>Update</b> to submit the changes.");
            out.println("<br>Click on <b>Remove</b> to delete the notice.");
         } else {
            if (copy == false) {
               out.println("<b>Add Member Notice</b><br>");
               out.println("<br>Set the desired information for the notice below.<br>");
               out.println("Click on <b>Add</b> to create the new notice.");
            } else {
               out.println("<b>Copy Member Notice</b><br>");
               out.println("<br>Change the desired information for the notice below.<br>");
               out.println("Click on <b>Add</b> to create the new notice.");
               out.println("<br><br><b>NOTE:</b> You must change the name of the notice.");
            }
         }
         out.println("</font></td></tr></table><br>");

         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_memNotice\" method=\"post\" target=\"bot\">");
            if (copy == true) {
               out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");
            } else {
               if (id > 0) {    // if edit
                  out.println("<input type=\"hidden\" name=\"notice_id\" value=\"" + id + "\">");  // notice to edit
               }
            }
            out.println("<input type=\"hidden\" name=\"step2\" value=\"yes\">");  // notice to edit
            out.println("<tr><td width=\"500\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"left\">&nbsp;&nbsp;Member Notice name:&nbsp;&nbsp;&nbsp;");
               out.println("<input type=\"text\" name=\"notice_name\" value=\"" +name+ "\" size=\"30\" maxlength=\"30\">");
               if (copy == false) {
                  out.println("</input><br>&nbsp;&nbsp;&nbsp;* Must be unique");
               } else {
                  out.println("</input><br>&nbsp;&nbsp;&nbsp;* Must be changed!!");
               }
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

               out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
            }
              
            Common_Config.displayTees(fb, out);                           // display the Tees option (f/b)

            Common_Config.displayStartDate(s_month, s_day, s_year, out);  // display the Start Date prompt

            Common_Config.displayEndDate(e_month, e_day, e_year, out);    // display the End Date prompt

            Common_Config.displayStartTime(shr, smin, ssampm, out);       // display the Start Time prompt

            Common_Config.displayEndTime(ehr, emin, seampm, out);         // display the End Time prompt

            Common_Config.displayRecurr(mon, tue, wed, thu, fri, sat, sun, out);  // display Recurr prompt

            out.println("&nbsp;&nbsp;Display For (select all that apply):<br>");

              out.println("<br>");
              out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
              if (teetime > 0) {        //    already checked
                 out.println("<input type=\"checkbox\" checked name=\"teetime\" value=\"1\">&nbsp;&nbsp;Tee Times - display notice when making/changing tee times");
              } else {
                 out.println("<input type=\"checkbox\" name=\"teetime\" value=\"1\">&nbsp;&nbsp;Tee Times - display notice when making/changing tee times");
              }
              out.println("<br>");
              out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
              if (event > 0) {        //    already checked
                 out.println("<input type=\"checkbox\" checked name=\"event\" value=\"1\">&nbsp;&nbsp;Events - display notice when making/changing event registrations");
              } else {
                 out.println("<input type=\"checkbox\" name=\"event\" value=\"1\">&nbsp;&nbsp;Events - display notice when making/changing event registrations");
              }
              out.println("<br>");
              out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
              if (teesheet > 0) {        //    already checked
                 out.println("<input type=\"checkbox\" checked name=\"teesheet\" value=\"1\">&nbsp;&nbsp;Tee Sheet - display notice above tee sheet in banner");
              } else {
                 out.println("<input type=\"checkbox\" name=\"teesheet\" value=\"1\">&nbsp;&nbsp;Tee Sheet - display notice above tee sheet in banner");
              }
              /* // Removed for now, may implement at a later date
              out.println("<br>");
              out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
              if (overrideColors > 0) {        //    already checked
                 out.println("<input type=\"checkbox\" checked name=\"overrideColors\" value=\"1\">&nbsp;&nbsp;Override Event & Restrictions colors on tee sheet (Tee Sheet only)");
              } else {
                 out.println("<input type=\"checkbox\" name=\"overrideColors\" value=\"1\">&nbsp;&nbsp;Override Event & Restrictions colors on tee sheet (Tee Sheet only)");
              }
              */
              out.println("<br>");
              out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
              if (proside > 0) {        //    already checked
                 out.println("<input type=\"checkbox\" checked name=\"proside\" value=\"1\">&nbsp;&nbsp;Proshop Side - display notice for proshop users too");
              } else {
                 out.println("<input type=\"checkbox\" name=\"proside\" value=\"1\">&nbsp;&nbsp;Proshop Side - display notice for proshop users too");
              }
              
              out.println("<br><br>");
              out.println("&nbsp;&nbsp;Background Color of banner and tee times (for Tee Sheet option only):");
            
              out.println("<br><br>");
              out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
              Common_Config.displayColorsAll(bgColor, out);       // output the color options
            
              out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
              out.println("<a href=\"/" +rev+ "/proshop_color.htm\" target=\"_blank\">View Colors</a>");
              out.println("<br><br>");
              
              out.println("&nbsp;&nbsp;Message to Display: <br>&nbsp;&nbsp;(Text will automatically wrap.  To force a line-break, please insert &lt;br&gt; into your message)");
              out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;");
              out.println("<textarea name=\"message\" cols=\"40\" rows=\"8\">" + message + "</textarea>");

         out.println("</p>");
         out.println("<input type=\"hidden\" name=\"oldName\" value=\"" + name + "\"></input>");

         out.println("<p align=\"center\">");
         if (copy == false && id > 0) {
            out.println("<input type=\"submit\" name=\"Update\" value=\"Update\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<input type=\"submit\" name=\"Remove\" value=\"Remove\">");
         } else {
            out.println("<input type=\"submit\" name=\"Add\" value=\"Add\">");
         }
         out.println("</p>");
                 
      out.println("</font></td></tr></form></table>");
      out.println("</td></tr></table>");                       // end of main page table

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_memNotice\">");
   out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");

   out.println("</center></font></body></html>");

 }   // end of doPost   


 // ***************************************************************************
 //  Process the copy request from doGet - display a selection list of existing notices
 // ***************************************************************************

 private void doCopy(HttpServletRequest req, PrintWriter out, Connection con, int lottery) {

   Statement stmt = null;
   ResultSet rs = null;

   //
   // Define some parms to use in the html
   //
   String name = "";         // name of notice

   int id = 0;
     
   boolean b = false;

   //
   //  Build the HTML page to display the existing notices
   //
   out.println(SystemUtils.HeadTitle("Proshop Member Notices Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

   out.println("<table cellpadding=\"5\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"3\">");
   out.println("<b>Copy a Member Notice</b><br>");
   out.println("</font>");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<b>Instructions:</b>&nbsp;&nbsp;Use this feature to create a new Member Notice by copying an existing notice.<br>");
   out.println("Select the notice you wish to copy from the list below.");
   out.println("</font></td></tr></table>");
   out.println("<br><br>");

   //
   //  Get and display the existing notices
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT name FROM mem_notice");

      if (rs.next()) {

         b = true;                     // indicate notices exist
      }
      stmt.close();

      if (b == true) {

         out.println("<font size=\"2\">");
         out.println("<p>Select the Member Notice you wish to copy.</p>");

         out.println("<form action=\"/" +rev+ "/servlet/Proshop_memNotice\" method=\"post\" target=\"bot\">");
         out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");     // tell dopost its a copy
         out.println("<select size=\"1\" name=\"notice_id\">");

         //
         //  Do again to actually get the names
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT mem_notice_id, name FROM mem_notice ORDER BY name");

         while (rs.next()) {

            id = rs.getInt("mem_notice_id");
            name = rs.getString("name");

            out.println("<option value=\"" +id+ "\">" +name+ "</option>");  // show name but send id

         }  // end of while loop

         stmt.close();

         out.println("</select><br><br>");

         out.println("<input type=\"submit\" name=\"Continue\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");

      } else {            // no rest's exist

         out.println("<p><font size=\"2\">No Member Notices Currently Exist</p>");
      }
   }
   catch (Exception exc) {

      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");

   }  // end of try

   //
   //  End of HTML page
   //
   out.println("</td></tr></table>");                // end of main page table
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_memNotice\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</center></font></body></html>");
   out.close();
 }


 // ***************************************************************************
 //  Process the add/copy request from doPost 
 // ***************************************************************************

 private void doAdd(boolean copy, HttpServletRequest req, PrintWriter out, Connection con) {

   PreparedStatement stmt = null;
   ResultSet rs = null;


   String name = "";
   String course = "";
   String fb = "";
   String message = "";
   String ssampm = "AM";
   String seampm = "AM";
   String bgColor = "";

   String temp = "";
   String msg = "";

   int count = 0;
   int teetime = 0;
   int event = 0;
   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int smonth = 0;
   int sday = 0;
   int syear = 0;
   int shr = 0;
   int smin = 0;
   int sampm = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int ehr = 0;
   int emin = 0;
   int eampm = 0;
   int proside = 0;
   int teesheet = 0;
   int overrideColors = 0;


   //
   //  User wishes to edit the record - get parms passed
   //
   name = req.getParameter("notice_name");
   course = req.getParameter("course");             //  course name
   fb = req.getParameter("fb");                     //  Front/Back indicator
   message = req.getParameter("message");
   proside = (req.getParameter("proside") != null && req.getParameter("proside").equals("1")) ? 1 : 0;
   teesheet = (req.getParameter("teesheet") != null && req.getParameter("teesheet").equals("1")) ? 1 : 0;
   //overrideColors = (req.getParameter("overrideColors") != null && req.getParameter("overrideColors").equals("1")) ? 1 : 0;   // Removed for now, may implement at a later date
   temp = req.getParameter("smonth");
   bgColor = req.getParameter("color");

   try {
      smonth = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("sday");

   try {
      sday = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("syear");

   try {
      syear = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("emonth");

   try {
      emonth = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("eday");

   try {
      eday = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("eyear");

   try {
      eyear = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("start_hr");

   try {
      shr = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("start_min");

   try {
      smin = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("end_hr");

   try {
      ehr = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("end_min");

   try {
      emin = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }


   temp = req.getParameter("start_ampm");

   if (temp.equals ( "12" )) {      // sampm & eampm are either '00' or '12'
      ssampm = "PM";
   }

   try {
      sampm = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   temp = req.getParameter("end_ampm");

   if (temp.equals ( "12" )) {
      seampm = "PM";
   }

   try {
      eampm = Integer.parseInt(temp);        // get int value
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   if (req.getParameter("teetime") != null) {        // tee times ?

      teetime = 1;
   }
   if (req.getParameter("event") != null) {        //  events ?

      event = 1;
   }
   if (req.getParameter("mon") != null) {

      mon = 1;           // recurr check box selected
   }
   if (req.getParameter("tue") != null) {

      tue = 1;           // recurr check box selected
   }
   if (req.getParameter("wed") != null) {

      wed = 1;           // recurr check box selected
   }
   if (req.getParameter("thu") != null) {

      thu = 1;           // recurr check box selected
   }
   if (req.getParameter("fri") != null) {

      fri = 1;           // recurr check box selected
   }
   if (req.getParameter("sat") != null) {

      sat = 1;           // recurr check box selected
   }
   if (req.getParameter("sun") != null) {

      sun = 1;           // recurr check box selected
   }

   //
   //  adjust some values for the table
   //
   long sdate = syear * 10000;       // create a date field from input values
   sdate = sdate + (smonth * 100);
   sdate = sdate + sday;             // date = yyyymmdd (for comparisons)

   long edate = eyear * 10000;       // create a date field from input values
   edate = edate + (emonth * 100);
   edate = edate + eday;             // date = yyyymmdd (for comparisons)

   if (shr < 12) {                  // _hr specified as 01 - 12 (_ampm = 00 or 12)

      shr = shr + sampm;             // convert to military time (12 is always Noon or PM)
   }

   if (ehr < 12) {                 // ditto

      ehr = ehr + eampm;
   }

   int stime = shr * 100;
   stime = stime + smin;
   int etime = ehr * 100;
   etime = etime + emin;

   //
   //  verify the date and time fields
   //
   if (sdate > edate) {

      msg = "Sorry, the Start Date and End Date values are incorrect.";       // error message
      invData(msg, out);    // inform the user and return
      return;
   }
   if (stime > etime) {

      msg = "Sorry, the Start Time and End Time values are incorrect.";       // error message
      invData(msg, out);    // inform the user and return
      return;
   }

   //
   //  Validate minute values
   //
   if ((smin < 0) || (smin > 59)) {

      msg = "Start Minute parameter must be in the range of 00 - 59. " +
            " You entered:" + smin;
      invData(msg, out);    // inform the user and return
      return;
   }

   if ((emin < 0) || (emin > 59)) {

      msg = "End Minute parameter must be in the range of 00 - 59. " +
            " You entered:" + emin;
      invData(msg, out);    // inform the user and return
      return;
   }

   //
   //  Validate new name if it has changed
   //
   boolean error = SystemUtils.scanQuote(name);           // check for single quote

   if (error == true) {

      msg = "Apostrophes (single quotes) cannot be part of the Name.";
      invData(msg, out);    // inform the user and return
      return;
   }

   if (mon == 0 && tue == 0 && wed == 0 && thu == 0 && fri == 0 && sat == 0 && sun == 0) {     // if no recurr

      msg = "You must select at least one Recurrence.";
      invData(msg, out);
      return;
   }

   if (teetime == 0 && event == 0 && teesheet == 0) {     // at least one must be selected

      msg = "You must select either Tee Times, Events, or Tee Sheet, or any combination of the three.";
      invData(msg, out);
      return;
   }

   if (message.equals( "" )) {     // if no message text

      msg = "You must specify a message.";
      invData(msg, out);
      return;
   }


   //
   //  add notice data to the database
   //

   // make sure bgColor isn't "Default"
   if (bgColor.equalsIgnoreCase("Default")) {
       bgColor = "";
   }   
   
   try {

      PreparedStatement pstmt = con.prepareStatement (
        "INSERT INTO mem_notice (name, sdate, stime, " +
        "edate, etime, mon, tue, wed, thu, fri, sat, sun, teetime, event, " +
        "courseName, fb, message, proside, teesheet, bgColor) VALUES " +
        "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);       // put the parm in pstmt
      pstmt.setLong(2, sdate);
      pstmt.setInt(3, stime);
      pstmt.setLong(4, edate);
      pstmt.setInt(5, etime);
      pstmt.setInt(6, mon);
      pstmt.setInt(7, tue);
      pstmt.setInt(8, wed);
      pstmt.setInt(9, thu);
      pstmt.setInt(10, fri);
      pstmt.setInt(11, sat);
      pstmt.setInt(12, sun);
      pstmt.setInt(13, teetime);
      pstmt.setInt(14, event);
      pstmt.setString(15, course);
      pstmt.setString(16, fb);
      pstmt.setString(17, message);
      pstmt.setInt(18, proside);
      pstmt.setInt(19, teesheet);
      //pstmt.setInt(20, overrideColors);        // Removed for now, may implement at a later date
      pstmt.setString(20, bgColor);
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
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Add Member Notice"));
   out.println("<BODY>");
//   SystemUtils.getProshopSubMenu(req, out, lottery);
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>Member Notice Has Been Added</H3>");
   out.println("<BR><BR>Thank you, the Member Notice has been added to the system database.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   if (copy == false) {
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_memNotice\">");
   } else {
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_memNotice\">");
      out.println("<input type=\"hidden\" name=\"copy\" value=\"yes\">");
   }
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");

   out.close();
 }


 // ***************************************************************************
 //  Process the edit/delete request from doPost
 // ***************************************************************************

 private void doEdit(int id, HttpServletRequest req, PrintWriter out, Connection con) {

   PreparedStatement stmt = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;


   String name = "";
   String oldName = "";
   String course = "";
   String fb = "";
   String message = "";
   String ssampm = "AM";
   String seampm = "AM";
   String bgColor = "";

   String temp = "";
   String msg = "";
     
   int count = 0;
   int teetime = 0;
   int event = 0;
   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int smonth = 0;
   int sday = 0;
   int syear = 0;
   int shr = 0;
   int smin = 0;
   int sampm = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int ehr = 0;
   int emin = 0;
   int eampm = 0;
   int proside = 0;
   int teesheet = 0;
   //int overrideColors = 0;        // Removed for now, may implement at a later date
     

   //
   //  Verify that we received an id
   //
   if (id < 1) {
     
      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H2>System Sequence Error</H2>");
      out.println("<BR><BR>Sorry, we are unable to identify the record you wish to process.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;  
   }

   if (req.getParameter("Remove") != null) {       // user wish to delete the record??

      if (req.getParameter("oldName") != null) {       // is this the first call??

         //
         //  First call for delete - request a confirmation
         //
         name = req.getParameter("oldName");           // get the name of the notice

         out.println(SystemUtils.HeadTitle("Delete Notice Confirmation"));
         out.println("<BODY><CENTER><BR>");
         out.println("<p>&nbsp;</p>");
         out.println("<BR><H3>Remove Member Notice Confirmation</H3><BR>");
         out.println("<BR>Please confirm that you wish to remove this Member Notice: <b>" + name + "</b><br>");

         out.println("<form action=\"/" +rev+ "/servlet/Proshop_memNotice\" method=\"post\" target=\"bot\">");
         out.println("<BR>ARE YOU SURE YOU WANT TO DELETE THIS RECORD?");

         out.println("<input type=\"hidden\" name=\"notice_id\" value =\"" + id + "\">");
         out.println("<input type=\"hidden\" name=\"step2\" value =\"yes\">");
         out.println("<br><br><input type=\"submit\" value=\"Yes - Delete\" name=\"Remove\">");
         out.println("</form><font size=\"2\">");

         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_memNotice\">");
         out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");

      } else {         // delete confirmed - delete it

         //
         //  User wishes to delete the member notice
         //
         try {

            stmt = con.prepareStatement (
                     "Delete FROM mem_notice WHERE mem_notice_id = ?");

            stmt.clearParameters();               // clear the parms
            stmt.setInt(1, id);
            count = stmt.executeUpdate();

            stmt.close();

         }
         catch (Exception exc) {

            dbError(out, exc);

         }  // end of try


         out.println(SystemUtils.HeadTitle("Delete Notice Confirmation"));
         out.println("<BODY><CENTER><BR>");
         out.println("<p>&nbsp;</p>");

         if (count > 0) {       // if notice deleted

            out.println("<BR><H3>Member Notice Has Been Removed</H3><BR>");
            out.println("<BR><BR>Thank you, the member notice has been removed from the database.");

         } else {

            out.println("<BR><H3>Member Notice Removal Failed</H3><BR>");
            out.println("<BR><BR>Sorry, we were not able to remove the member notice at this time.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact customer support.");
         }

         out.println("<BR><BR>");
         out.println("<font size=\"2\">");

         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_memNotice\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
      }

   } else {
     
      //
      //  User wishes to edit the record - get parms passed
      //
      name = req.getParameter("notice_name");
      course = req.getParameter("course");             //  course name
      fb = req.getParameter("fb");                     //  Front/Back indicator
      message = req.getParameter("message");
      proside = (req.getParameter("proside") != null && req.getParameter("proside").equals("1")) ? 1 : 0;
      teesheet = (req.getParameter("teesheet") != null && req.getParameter("teesheet").equals("1")) ? 1 : 0;
      //overrideColors = (req.getParameter("overrideColors") != null && req.getParameter("overrideColors").equals("1")) ? 1 : 0;   // Removed for now, may implement at a later date
      temp = req.getParameter("smonth");
      bgColor = req.getParameter("color");

      try {
         smonth = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("sday");

      try {
         sday = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("syear");

      try {
         syear = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("emonth");

      try {
         emonth = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("eday");

      try {
         eday = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("eyear");

      try {
         eyear = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("start_hr");

      try {
         shr = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("start_min");

      try {
         smin = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("end_hr");

      try {
         ehr = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("end_min");

      try {
         emin = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }


      temp = req.getParameter("start_ampm");

      if (temp.equals ( "12" )) {      // sampm & eampm are either '00' or '12'
         ssampm = "PM";
      }

      try {
         sampm = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      temp = req.getParameter("end_ampm");

      if (temp.equals ( "12" )) {
         seampm = "PM";
      }

      try {
         eampm = Integer.parseInt(temp);        // get int value
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      if (req.getParameter("teetime") != null) {        // tee times ?

         teetime = 1;          
      }
      if (req.getParameter("event") != null) {        //  events ?

         event = 1;
      }
      if (req.getParameter("mon") != null) {

         mon = 1;           // recurr check box selected
      }
      if (req.getParameter("tue") != null) {

         tue = 1;           // recurr check box selected
      }
      if (req.getParameter("wed") != null) {

         wed = 1;           // recurr check box selected
      }
      if (req.getParameter("thu") != null) {

         thu = 1;           // recurr check box selected
      }
      if (req.getParameter("fri") != null) {

         fri = 1;           // recurr check box selected
      }
      if (req.getParameter("sat") != null) {

         sat = 1;           // recurr check box selected
      }
      if (req.getParameter("sun") != null) {

         sun = 1;           // recurr check box selected
      }

      //
      //  adjust some values for the table
      //
      long sdate = syear * 10000;       // create a date field from input values
      sdate = sdate + (smonth * 100);
      sdate = sdate + sday;             // date = yyyymmdd (for comparisons)

      long edate = eyear * 10000;       // create a date field from input values
      edate = edate + (emonth * 100);
      edate = edate + eday;             // date = yyyymmdd (for comparisons)

      if (shr < 12) {                  // _hr specified as 01 - 12 (_ampm = 00 or 12)

         shr = shr + sampm;             // convert to military time (12 is always Noon or PM)
      }

      if (ehr < 12) {                 // ditto

         ehr = ehr + eampm;
      }

      int stime = shr * 100;
      stime = stime + smin;
      int etime = ehr * 100;
      etime = etime + emin;

      //
      //  verify the date and time fields
      //
      if (sdate > edate) {

         msg = "Sorry, the Start Date and End Date values are incorrect.";       // error message
         invData(msg, out);    // inform the user and return
         return;
      }
      if (stime > etime) {

         msg = "Sorry, the Start Time and End Time values are incorrect.";       // error message
         invData(msg, out);    // inform the user and return
         return;
      }

      //
      //  Validate minute values
      //
      if ((smin < 0) || (smin > 59)) {

         msg = "Start Minute parameter must be in the range of 00 - 59. " +
               " You entered:" + smin;
         invData(msg, out);    // inform the user and return
         return;
      }

      if ((emin < 0) || (emin > 59)) {

         msg = "End Minute parameter must be in the range of 00 - 59. " +
               " You entered:" + emin;
         invData(msg, out);    // inform the user and return
         return;
      }

      //
      //  Validate new name if it has changed
      //
      boolean error = SystemUtils.scanQuote(name);           // check for single quote

      if (error == true) {

         msg = "Apostrophes (single quotes) cannot be part of the Name.";
         invData(msg, out);    // inform the user and return
         return;
      }

      if (mon == 0 && tue == 0 && wed == 0 && thu == 0 && fri == 0 && sat == 0 && sun == 0) {     // if no recurr

         msg = "You must select at least one Recurrence.";
         invData(msg, out);
         return;
      }

      if (teetime == 0 && event == 0 && teesheet == 0) {     // at least one must be selected

         msg = "You must select either Tee Times, Events, or Tee Sheet, or any combination of the three.";
         invData(msg, out);
         return;
      }

      if (message.equals( "" )) {     // if no message text

         msg = "You must specify a message.";
         invData(msg, out);
         return;
      }


      //
      //  Determine if the user has been prompted for confirmation yet
      //
      if (req.getParameter("oldName") != null) {       // is this the first call??

         oldName = req.getParameter("oldName");           // get the name of the notice

         //
         //  If name has changed check for dup
         //
         if (!oldName.equals( name )) {    // if name has changed

            try {

               stmt = con.prepareStatement (
                       "SELECT mon FROM mem_notice WHERE name = ?");

               stmt.clearParameters();        // clear the parms
               stmt.setString(1, name);       // put the parm in stmt
               rs = stmt.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  dupMem(out);    // member notice exists - inform the user and return
                  stmt.close();
                  return;
               }
               stmt.close();
            }
            catch (Exception ignored) {
            }
         }

         //
         //  First call for this - prompt user for confirmation
         //
         out.println(SystemUtils.HeadTitle("Update Notice Confirmation"));
         out.println("<BODY><CENTER><BR>");
         out.println("<br>");
         out.println("<H3>Update Member Notice Confirmation</H3><BR>");
         out.println("<BR>Please confirm the following updated parameters for the Notice:<br><b>" + name + "</b><br><br>");

         out.println("<table border=\"1\" cols=\"2\" bgcolor=\"#F5F5DC\">");

         if (!course.equals( "" )) {

            out.println("<tr><td width=\"200\" align=\"right\">");
            out.println("Course Name:&nbsp;&nbsp;");
            out.println("</td><td width=\"200\" align=\"left\">");
            out.println("&nbsp;&nbsp;&nbsp;" + course);
            out.println("</td></tr>");
         }

         out.println("<tr><td width=\"200\" align=\"right\">");
         out.println("Tees:&nbsp;&nbsp;");
         out.println("</td><td width=\"200\" align=\"left\">");
         out.println("&nbsp;&nbsp;&nbsp;" + fb);
         out.println("</td></tr>");

         out.println("<tr><td width=\"200\" align=\"right\">");
         out.println("Start Date:&nbsp;&nbsp;");
         out.println("</td><td width=\"200\" align=\"left\">");
         out.println("&nbsp;&nbsp;&nbsp;" + smonth + "/" + sday + "/" + syear);
         out.println("</td></tr>");

         out.println("<tr><td width=\"200\" align=\"right\">");
         out.println("End Date:&nbsp;&nbsp;");
         out.println("</td><td width=\"200\" align=\"left\">");
         out.println("&nbsp;&nbsp;&nbsp;" + emonth + "/" + eday + "/" + eyear);
         out.println("</td></tr>");

         out.println("<tr><td width=\"200\" align=\"right\">");
         out.println("Start Time:&nbsp;&nbsp;");
         out.println("</td><td width=\"200\" align=\"left\">");

         if (smin < 10) {
            out.println("&nbsp;&nbsp;&nbsp;" + shr + ":0" + smin + "  " + ssampm);
         } else {
            out.println("&nbsp;&nbsp;&nbsp;" + shr + ":" + smin + "  " + ssampm);
         }

         out.println("</td></tr>");

         out.println("<tr><td width=\"200\" align=\"right\">");
         out.println("End Time:&nbsp;&nbsp;");
         out.println("</td><td width=\"200\" align=\"left\">");

         if (emin < 10) {
            out.println("&nbsp;&nbsp;&nbsp;" + ehr + ":0" + emin + "  " + seampm);
         } else {
            out.println("&nbsp;&nbsp;&nbsp;" + ehr + ":" + emin + "  " + seampm);
         }

         out.println("</td></tr>");

         out.println("<tr><td width=\"200\" align=\"right\">");
         out.println("Recurrence:&nbsp;&nbsp;");
         out.println("</td><td width=\"200\" align=\"left\">");
         if (mon > 0) {
            out.println("&nbsp;&nbsp;&nbsp;Monday");
         }
         if (tue > 0) {
            out.println("&nbsp;&nbsp;&nbsp;Tuesday");
         }
         if (wed > 0) {
            out.println("&nbsp;&nbsp;&nbsp;Wednesday");
         }
         if (thu > 0) {
            out.println("&nbsp;&nbsp;&nbsp;Thursday");
         }
         if (fri > 0) {
            out.println("&nbsp;&nbsp;&nbsp;Friday");
         }
         if (sat > 0) {
            out.println("&nbsp;&nbsp;&nbsp;Saturday");
         }
         if (sun > 0) {
            out.println("&nbsp;&nbsp;&nbsp;Sunday");
         }
         out.println("</td></tr>");

         out.println("<tr><td width=\"200\" align=\"right\">");
         out.println("Display Message For:&nbsp;&nbsp;");
         out.println("</td><td width=\"200\" align=\"left\">");
         if (teetime > 0) {
            out.println("&nbsp;&nbsp;&nbsp;Tee Times");
         }
         if (event > 0) {
            out.println("&nbsp;&nbsp;&nbsp;Events");
         }
         if (teesheet > 0) {
            out.println("&nbsp;&nbsp;&nbsp;Tee Sheet");
         }
         if (proside > 0) {
            out.println("&nbsp;&nbsp;&nbsp;<nobr>Proshop Side</nobr>");
         }
         out.println("</td></tr>");
         
         /*     // Removed for now, may implement at a later date
         if (teesheet > 0) {    // message only applicable if tee sheet option selected
             out.println("<tr><td width=\"200\" align=\"right\">");
             out.println("<nobr>&nbsp;&nbsp;Override Event & Restriction Colors:&nbsp;&nbsp;</nobr>");
             out.println("</td><td width=\"200\" align=\"left\">");
             if (overrideColors > 0) {
                out.println("&nbsp;&nbsp;&nbsp;Yes");
             } else {
                out.println("&nbsp;&nbsp;&nbsp;No");
             }
             out.println("</td></tr>");
         }
         */
         
         out.println("<tr><td width=\"200\" align=\"right\">");
         out.println("Background Color:&nbsp;&nbsp;");
         out.println("</td>");
         if (!bgColor.equals("") && !bgColor.equals("Default")) {     // if color not default
             out.println("<td width=\"200\" bgcolor=\"" + bgColor + "\" align=\"left\">&nbsp;&nbsp;&nbsp;" + bgColor);
         } else {
             out.println("<td width=\"200\" align=\"left\">&nbsp;&nbsp;&nbsp;Default (None)");
         }
         out.println("</td></tr>");

         out.println("<tr><td width=\"200\" align=\"right\">");
         out.println("Message:&nbsp;&nbsp;");
         out.println("</td><td width=\"200\" align=\"left\">");
         out.println("&nbsp;&nbsp;&nbsp;" + message);
         out.println("</td></tr>");

         out.println("</table>");

         out.println("<form action=\"/" +rev+ "/servlet/Proshop_memNotice\" method=\"post\" target=\"bot\">");
         out.println("<BR>ARE YOU SURE YOU WANT TO UPDATE THIS RECORD?");
         out.println("<input type=\"hidden\" name=\"notice_id\" value = \"" + id + "\">");
         out.println("<input type=\"hidden\" name=\"notice_name\" value = \"" + name + "\">");
         out.println("<input type=\"hidden\" name=\"smonth\" value = \"" + smonth + "\">");
         out.println("<input type=\"hidden\" name=\"sday\" value = \"" + sday + "\">");
         out.println("<input type=\"hidden\" name=\"syear\" value = \"" + syear + "\">");
         out.println("<input type=\"hidden\" name=\"emonth\" value = \"" + emonth + "\">");
         out.println("<input type=\"hidden\" name=\"eday\" value = \"" + eday + "\">");
         out.println("<input type=\"hidden\" name=\"eyear\" value = \"" + eyear + "\">");
         out.println("<input type=\"hidden\" name=\"start_hr\" value = \"" + shr + "\">");
         out.println("<input type=\"hidden\" name=\"start_min\" value = \"" + smin + "\">");
         out.println("<input type=\"hidden\" name=\"start_ampm\" value = \"" + sampm + "\">");
         out.println("<input type=\"hidden\" name=\"end_hr\" value = \"" + ehr + "\">");
         out.println("<input type=\"hidden\" name=\"end_min\" value = \"" + emin + "\">");
         out.println("<input type=\"hidden\" name=\"end_ampm\" value = \"" + eampm + "\">");
         if (mon > 0) {
            out.println("<input type=\"hidden\" name=\"mon\" value = \"" + mon + "\">");
         }
         if (tue > 0) {
            out.println("<input type=\"hidden\" name=\"tue\" value = \"" + tue + "\">");
         }
         if (wed > 0) {
            out.println("<input type=\"hidden\" name=\"wed\" value = \"" + wed + "\">");
         }
         if (thu > 0) {
            out.println("<input type=\"hidden\" name=\"thu\" value = \"" + thu + "\">");
         }
         if (fri > 0) {
            out.println("<input type=\"hidden\" name=\"fri\" value = \"" + fri + "\">");
         }
         if (sat > 0) {
            out.println("<input type=\"hidden\" name=\"sat\" value = \"" + sat + "\">");
         }
         if (sun > 0) {
            out.println("<input type=\"hidden\" name=\"sun\" value = \"" + sun + "\">");
         }
         if (teetime > 0) {
            out.println("<input type=\"hidden\" name=\"teetime\" value = \"" + teetime + "\">");
         }
         if (event > 0) {
            out.println("<input type=\"hidden\" name=\"event\" value = \"" + event + "\">");
         }
         if (teesheet > 0) {
            out.println("<input type=\"hidden\" name=\"teesheet\" value = \"" + teesheet + "\">");
            
            /*        // Removed for now, may implement at a later date
            if (overrideColors > 0) {
                out.println("<input type=\"hidden\" name=\"overrideColors\" value = \"" + overrideColors + "\">");
            }
            */
         }
         out.println("<input type=\"hidden\" name=\"proside\" value = \"" + proside + "\">");
         
         if (bgColor.equals("") || bgColor.equals("Default")) {
             out.println("<input type=\"hidden\" name=\"color\" value=\"\">");
         } else {
             out.println("<input type=\"hidden\" name=\"color\" value=\"" + bgColor + "\">");
         }
         out.println("<input type=\"hidden\" name=\"course\" value = \"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"fb\" value = \"" + fb + "\">");
         out.println("<input type=\"hidden\" name=\"message\" value = \"" + message + "\">");
         out.println("<input type=\"hidden\" name=\"step2\" value = \"yes\">");

         out.println("<br><input type=\"submit\" value=\"Yes - Continue\" name=\"Update\">");
         out.println("</form><font size=\"2\">");

         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_memNotice\">");
         out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
           
  
      } else {
        
         //
         //  Confirmation received - update the record
         //
         
         // make sure bgColor isn't "Default"
         if (bgColor.equalsIgnoreCase("Default")) {
             bgColor = "";
         }
           
         try {

            //
            //  Update the record in the member notice table
            //
            pstmt = con.prepareStatement (
              "UPDATE mem_notice SET name = ?, sdate = ?, stime = ?, " +
              "edate = ?, etime = ?, " +
              "mon = ?, tue = ?, wed = ?, thu = ?, fri = ?, sat = ?, sun = ?, " +
              "teetime = ?, event = ?, " +
              "courseName = ?, fb = ?, message = ?, proside = ?, teesheet = ?, " +
              "bgColor = ? WHERE mem_notice_id = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, name);
            pstmt.setLong(2, sdate);
            pstmt.setInt(3, stime);
            pstmt.setLong(4, edate);
            pstmt.setInt(5, etime);
            pstmt.setInt(6, mon);
            pstmt.setInt(7, tue);
            pstmt.setInt(8, wed);
            pstmt.setInt(9, thu);
            pstmt.setInt(10, fri);
            pstmt.setInt(11, sat);
            pstmt.setInt(12, sun);
            pstmt.setInt(13, teetime);
            pstmt.setInt(14, event);
            pstmt.setString(15, course);
            pstmt.setString(16, fb);
            pstmt.setString(17, message);
            pstmt.setInt(18, proside);
            pstmt.setInt(19, teesheet);
            //pstmt.setInt(20, overrideColors);        // Removed for now, may implement at a later date
            pstmt.setString(20, bgColor);

            pstmt.setInt(21, id);

            pstmt.executeUpdate();     // execute the prepared stmt

            pstmt.close();

         }
         catch (Exception exc) {

            dbError(out, exc);

         }  // end of try

         //
         // Database updated - inform user
         //
         out.println(SystemUtils.HeadTitle("Proshop Update Member Notice"));
         out.println("<BODY>");
      //   SystemUtils.getProshopSubMenu(req, out, lottery);
         out.println("<CENTER><BR>");
         out.println("<BR><BR><H3>Member Notice Has Been Updated</H3>");
         out.println("<BR><BR>Thank you, the Member Notice has been updated.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_memNotice\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
      }
  
   }   // all done
     
   out.close();
   return;
 }


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
   out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");

 }

 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(String msg, PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>" + msg + "<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");
 }

 // *********************************************************
 // Notice already exists
 // *********************************************************

 private void dupMem(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, the <b>name</b> you specified already exists in the database.<BR>");
   out.println("<BR>Please change the name to a unique value.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");
 }

}
