/***************************************************************************************     
 *   Proshop_editmNumrest:  This servlet will process the 'Edit Member Number Restriction' request from
 *                          the Proshop's Edit Member Number Restrictions page.
 *
 *
 *   called by:  Proshop_mNumrest (via doPost in HTML built by Proshop_mNumrest)
 *
 *   created: 2/04/2003   Bob P.
 *
 *   last updated:
 *
 *        8/12/08   Added limited access proshop users checks
 *        7/18/03   Enhancements for Version 3 of the software.
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

public class Proshop_editmNumrest extends HttpServlet {

                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //****************************************************************
 // Process the form request from Proshop_editmNumrest page.
 //****************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   //
   // Process request according to which 'submit' button was selected
   //
   //      Update - update the record - prompt user for conf
   //      Remove - delete the record - prompt user for conf
   //      Yes (value = update) - process the update confirmation
   //      Delete (value = remove) - process the delete confirmation
   //
   if (req.getParameter("Update") != null) {

      updateConf(req, out, session);

   } else {

      if (req.getParameter("Remove") != null) {

         removeConf(req, out);

      } else {

         if (req.getParameter("Delete") != null) {

            removemNumrest(req, out, session);

         } else {

            if (req.getParameter("Yes") != null) {

               updatemNumrest(req, out, session);

            }
         }
      }
   }
 }   // end of doPost


   //*************************************************
   //  updateConf - Request a confirmation from user
   //*************************************************

 private void updateConf(HttpServletRequest req, PrintWriter out, HttpSession sess) {


   ResultSet rs = null;
   Connection con = SystemUtils.getCon(sess);            // get DB connection

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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_RESTRICTIONS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_RESTRICTIONS", out);
   }
    
   //
   // Get the other parameters entered
   //
   String name = req.getParameter("rest_name");
   String oldName = req.getParameter("oldName");
   String smonth = req.getParameter("smonth");        
   String sday = req.getParameter("sday");      
   String syear = req.getParameter("syear");    
   String emonth = req.getParameter("emonth");
   String eday = req.getParameter("eday");
   String eyear = req.getParameter("eyear");
   String shr = req.getParameter("start_hr");      
   String smin = req.getParameter("start_min");    
   String sampm = req.getParameter("start_ampm");  
   String ehr = req.getParameter("end_hr");      
   String emin = req.getParameter("end_min");    
   String eampm = req.getParameter("end_ampm");     
   String recurr = req.getParameter("recurr");
   String mems = req.getParameter("mems");
   String course = req.getParameter("course");
   String oldCourse = req.getParameter("oldCourse");
   String fb = req.getParameter("fb");

   String ssampm = "AM";
   String seampm = "AM";

   int s_min = 0;
   int e_min = 0;
      
   if (sampm.equals ( "12" )) {      // sampm & eampm are either '00' or '12'
      ssampm = "PM";
   }

   if (eampm.equals ( "12" )) {
      seampm = "PM";
   }

   try {
      s_min = Integer.parseInt(smin);
      e_min = Integer.parseInt(emin);
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }


   //
   //  Validate minute values
   //
   if ((s_min < 0) || (s_min > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Start Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered:" + s_min);
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</input></form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if ((e_min < 0) || (e_min > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>End Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered:" + e_min);
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</input></form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  verify the required fields (name reqr'ed - rest are automatic)
   //
   if (name.equals( "" )) {

      invData(out);    // inform the user and return
      return;
   }

   //
   //  Validate new name if it has changed
   //
   boolean error = SystemUtils.scanQuote(name);           // check for single quote

   if (error == true) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Single quotes cannot be part of the Name.");
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</input></form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Scan name for special characters and replace with HTML supported chars (i.e. '>' = &gt)
   //
   name = SystemUtils.filter(name);
   oldName = SystemUtils.filter(oldName);

   //
   //  If name has changed check for dup
   //
   if (!oldName.equals( name )) {    // if name has changed

      try {

         PreparedStatement stmt = con.prepareStatement (
                 "SELECT sdate FROM mnumres2 WHERE name = ?");

         stmt.clearParameters();        // clear the parms
         stmt.setString(1, name);       // put the parm in stmt
         rs = stmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            dupMem(out);    // member restriction exists - inform the user and return
            stmt.close();
            return;
         }
         stmt.close();
      }
      catch (Exception ignored) {
      }
   }

   //
   //  Prompt user for confirmation
   //
   out.println(SystemUtils.HeadTitle("Update Member Number Restriction Confirmation"));
   out.println("<BODY><CENTER><BR>");
   out.println("<br>");
   out.println("<H3>Update Member Number Restriction Confirmation</H3><BR>");
   out.println("<BR>Please confirm the following parameters for the restriction:<br><b>" + name + "</b><br><br>");

   out.println("<table border=\"1\" cols=\"2\" bgcolor=\"#F5F5DC\">");

   if (!course.equals( "" )) {

      out.println("<tr><td align=\"right\">");
      out.println("Course Name:&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + course);
      out.println("</td></tr>");
   }

   out.println("<tr><td align=\"right\">");
   out.println("Tees:&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + fb);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Start Date:&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + smonth + "/" + sday + "/" + syear);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("End Date:&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + emonth + "/" + eday + "/" + eyear);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Start Time:&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");

   if (s_min < 10) {
      out.println("&nbsp;&nbsp;&nbsp;" + shr + ":0" + s_min + "  " + ssampm);
   } else {
      out.println("&nbsp;&nbsp;&nbsp;" + shr + ":" + s_min + "  " + ssampm);
   }
     
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("End Time:&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");

   if (e_min < 10) {
      out.println("&nbsp;&nbsp;&nbsp;" + ehr + ":0" + e_min + "  " + seampm);
   } else {
      out.println("&nbsp;&nbsp;&nbsp;" + ehr + ":" + e_min + "  " + seampm);
   }

   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Recurrence:&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + recurr);
   out.println("</td></tr>");

   out.println("<tr><td align=\"right\">");
   out.println("Max Number of Members Allowed per Member Number:&nbsp;&nbsp;");
   out.println("</td><td align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + mems);
   out.println("</td></tr></table>");

   out.println("<form action=\"/" +rev+ "/servlet/Proshop_editmNumrest\" method=\"post\" target=\"bot\">");
   out.println("<BR>ARE YOU SURE YOU WANT TO UPDATE THIS RECORD?");
   out.println("<input type=\"hidden\" name=\"rest_name\" value = \"" + name + "\">");
   out.println("<input type=\"hidden\" name=\"oldName\" value = \"" + oldName + "\">");
   out.println("<input type=\"hidden\" name=\"smonth\" value = " + smonth + ">");
   out.println("<input type=\"hidden\" name=\"sday\" value = " + sday + ">");
   out.println("<input type=\"hidden\" name=\"syear\" value = " + syear + ">");
   out.println("<input type=\"hidden\" name=\"emonth\" value = " + emonth + ">");
   out.println("<input type=\"hidden\" name=\"eday\" value = " + eday + ">");
   out.println("<input type=\"hidden\" name=\"eyear\" value = " + eyear + ">");
   out.println("<input type=\"hidden\" name=\"start_hr\" value = " + shr + ">");
   out.println("<input type=\"hidden\" name=\"start_min\" value = " + smin + ">");
   out.println("<input type=\"hidden\" name=\"start_ampm\" value = " + sampm + ">");
   out.println("<input type=\"hidden\" name=\"end_hr\" value = " + ehr + ">");
   out.println("<input type=\"hidden\" name=\"end_min\" value = " + emin + ">");
   out.println("<input type=\"hidden\" name=\"end_ampm\" value = " + eampm + ">");
   out.println("<input type=\"hidden\" name=\"recurr\" value = \"" + recurr + "\">");
   out.println("<input type=\"hidden\" name=\"mems\" value = \"" + mems + "\">");
   out.println("<input type=\"hidden\" name=\"fb\" value = \"" + fb + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value = \"" + course + "\">");
   out.println("<input type=\"hidden\" name=\"oldCourse\" value = \"" + oldCourse + "\">");
      
   out.println("<BR><BR>");
   out.println("<input type=\"submit\" value=\"Yes\" name=\"Yes\">");
   out.println("</form><font size=\"2\">");
      
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_mNumrest\">");
   out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");

 }  // wait for confirmation (Yes)


   //*************************************************
   //  removeConf - Request a confirmation for delete
   //*************************************************

 private void removeConf(HttpServletRequest req, PrintWriter out) {


   //
   // Get the name parameter from the hidden input field
   //
   String name = req.getParameter("rest_name");
   String course = req.getParameter("course");

   //
   //  Scan name for special characters and replace with HTML supported chars (i.e. '>' = &gt)
   //
   name = SystemUtils.filter(name);

   //
   //  Prompt user for confirmation
   //
   out.println(SystemUtils.HeadTitle("Delete Member Number Restriction Confirmation"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Remove Member Number Restriction Confirmation</H3><BR>");
   out.println("<BR>Please confirm that you wish to remove this restriction: <b>" + name + "</b><br>");

   out.println("<form action=\"/" +rev+ "/servlet/Proshop_editmNumrest\" method=\"post\" target=\"bot\">");
   out.println("<BR>ARE YOU SURE YOU WANT TO DELETE THIS RECORD?");
   out.println("<BR><BR>");

   out.println("<input type=\"hidden\" name=\"rest_name\" value =\"" + name + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value =\"" + course + "\">");
   out.println("<input type=\"submit\" value=\"Delete\" name=\"Delete\">");
   out.println("</form><font size=\"2\">");

   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_mNumrest\">");
   out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");

 }  // wait for confirmation (Yes)


   //*************************************************************************
   //  removemNumrest - Remove a restriction from restriction table - received confirmation
   //*************************************************************************

 private void removemNumrest(HttpServletRequest req, PrintWriter out, HttpSession sess) {


   Connection con = SystemUtils.getCon(sess);            // get DB connection

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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_RESTRICTIONS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_RESTRICTIONS", out);
   }
    
   //
   // Get the name parameter from the hidden input field
   //
   String name = req.getParameter("rest_name");
   String course = req.getParameter("course");

   //
   // Delete the Restriction from the restriction table
   //
   PreparedStatement stmt = null;
   ResultSet rs = null;
   int count = 0;

   try {
      //
      //  Check the name for special characters and translate if ncessary
      //
      stmt = con.prepareStatement (
               "SELECT sdate FROM mnumres2 WHERE name = ? AND courseName = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);       // put the parm in stmt
      stmt.setString(2, course);
      rs = stmt.executeQuery();      // execute the prepared stmt

      if (!rs.next()) {                     // if not found

         name = SystemUtils.filter(name);   // translate the name
      }
      stmt.close();              // close the stmt

      //
      //  Delete the rest
      //
      stmt = con.prepareStatement (
               "Delete FROM mnumres2 WHERE name = ? AND courseName = ?");

      stmt.clearParameters();               // clear the parms
      stmt.setString(1, name);              // put the parm in stmt
      stmt.setString(2, course);
      count = stmt.executeUpdate();         // execute the prepared stmt

      stmt.close();
   }
   catch (Exception exc) {

      dbError(out, exc);
      return;
   }

   //
   //  Member Number Restriction successfully deleted
   //
   out.println(SystemUtils.HeadTitle("Delete Member Number Restriction Confirmation"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Restriction Record Has Been Removed</H3><BR>");
   out.println("<BR><BR>Thank you, the restriction has been removed from the database.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");

   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_mNumrest\">");
   out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");

 }  // done with delete function


   //*******************************************************************
   //  updatemNumrest - Update a Restriction from restriction table - received a conf
   //*******************************************************************

 private void updatemNumrest(HttpServletRequest req, PrintWriter out, HttpSession sess) {


   Connection con = SystemUtils.getCon(sess);            // get DB connection

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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_RESTRICTIONS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_RESTRICTIONS", out);
   }
    
   //
   // Get the parameters entered
   //
   String name = req.getParameter("rest_name");
   String oldName = req.getParameter("oldName");
   String s_month = req.getParameter("smonth");             //  month (00 - 12)
   String s_day = req.getParameter("sday");                 //  day (01 - 31)
   String s_year = req.getParameter("syear");               //  year (2002 - 20xx)
   String e_month = req.getParameter("emonth");             
   String e_day = req.getParameter("eday");                 
   String e_year = req.getParameter("eyear");               
   String start_hr = req.getParameter("start_hr");         //  start hour (01 - 12)
   String start_min = req.getParameter("start_min");       //  start min (00 - 59)
   String start_ampm = req.getParameter("start_ampm");     //  AM/PM (00 or 12)
   String end_hr = req.getParameter("end_hr");             //   .
   String end_min = req.getParameter("end_min");           //   .
   String end_ampm = req.getParameter("end_ampm");         //   .
   String recurr = req.getParameter("recurr");             //  recurrence
   String smems = req.getParameter("mems");           
   String course = req.getParameter("course");             //  course name
   String fb = req.getParameter("fb");                     //  Front/Back indicator
   String oldCourse = req.getParameter("oldCourse");

   int smonth = 0;
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

   int mems = 0;

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
      s_hr = Integer.parseInt(start_hr);
      s_min = Integer.parseInt(start_min);
      s_ampm = Integer.parseInt(start_ampm);
      e_hr = Integer.parseInt(end_hr);
      e_min = Integer.parseInt(end_min);
      e_ampm = Integer.parseInt(end_ampm);
      mems = Integer.parseInt(smems);
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   //
   //  adjust some values for the table
   //
   long sdate = syear * 10000;      // create a date field from input values
   sdate = sdate + (smonth * 100);
   sdate = sdate + sday;             // date = yyyymmdd (for comparisons)

   long edate = eyear * 10000;       // create a date field from input values
   edate = edate + (emonth * 100);
   edate = edate + eday;             // date = yyyymmdd (for comparisons)

   if (s_hr != 12) {                // _hr specified as 01 - 12 (_ampm = 00 or 12)

      s_hr = s_hr + s_ampm;         // convert to military time (12 is always Noon or PM)
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
   //  Udate the record in the restriction table
   //
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   int count = 0;

   try {
      //
      //  Check the name for special characters and translate if ncessary
      //
      pstmt = con.prepareStatement (
               "SELECT sdate FROM mnumres2 WHERE name = ? AND courseName = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, oldName);       // put the parm in stmt
      pstmt.setString(2, oldCourse);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (!rs.next()) {                     // if not found

         oldName = SystemUtils.filter(oldName);   // translate the name
      }
      pstmt.close();              // close the stmt

      //
      //  Udate the record in the restriction table
      //
      pstmt = con.prepareStatement (
        "UPDATE mnumres2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ?, " +
        "start_hr = ?, start_min = ?, stime = ?, " +
        "edate = ?, end_mm = ?, end_dd = ?, end_yy = ?, " +
        "end_hr = ?, end_min = ?, etime = ?, recurr = ?, num_mems = ?," +
        "courseName = ?, fb = ?, name = ? WHERE name = ? AND courseName = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, sdate);
      pstmt.setInt(2, smonth);
      pstmt.setInt(3, sday);
      pstmt.setInt(4, syear);
      pstmt.setInt(5, s_hr);
      pstmt.setInt(6, s_min);
      pstmt.setInt(7, stime);
      pstmt.setLong(8, edate);
      pstmt.setInt(9, emonth);
      pstmt.setInt(10, eday);
      pstmt.setInt(11, eyear);
      pstmt.setInt(12, e_hr);
      pstmt.setInt(13, e_min);
      pstmt.setInt(14, etime);
      pstmt.setString(15, recurr);
      pstmt.setInt(16, mems);
      pstmt.setString(17, course);
      pstmt.setString(18, fb);
      pstmt.setString(19, name);
        
      pstmt.setString(20, oldName);
      pstmt.setString(21, oldCourse);

      count = pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();
   }
   catch (Exception exc) {

      dbError(out, exc);
      return;
   }

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Edit Member Number Restriction"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Restriction Has Been Updated</H3>");
   out.println("<BR><BR>Thank you, the restriction has been updated in the system database.");
   out.println("<BR><BR><BR>");
   out.println("<font size=\"2\">");

   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_mNumrest\">");
   out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");

 }


 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<br><br><font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");
 }


 // *********************************************************
 // Member Restriction already exists
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

 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>Exception:   " + e.getMessage());
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");
 }
}
