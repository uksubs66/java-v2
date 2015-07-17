/***************************************************************************************     
 *   Proshop_editdbltee:  This servlet will process the 'Edit Double Tee' request from
 *                        the Proshop's Edit Double Tee page.
 *
 *
 *   called by:  Proshop_dbltee (to doPost from HTML built by Proshop_dbltee)
 *
 *   created: 12/28/2001   Bob P.
 *
 *   last updated:
 *
 *        8/12/08   Added limited access proshop users checks
 *        1/15/04   Add processing to remove and rebuild double tees.
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


public class Proshop_editdbltee extends HttpServlet {

                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //****************************************************************
 // Process the form request from Proshop_dbltee page.
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

      updateConf(req, out);

   } else {

      if (req.getParameter("Remove") != null) {

         removeConf(req, out);

      } else {

         if (req.getParameter("Delete") != null) {

            removedbltee(req, out, session);

         } else {

            if (req.getParameter("Yes") != null) {

               updatedbltee(req, out, session);

            }
         }
      }
   }
 }   // end of doPost


   //*************************************************
   //  updateConf - Request a confirmation from user
   //*************************************************

 private void updateConf(HttpServletRequest req, PrintWriter out) {


   //
   // Get the parameters entered
   //
   String name = req.getParameter("dbl_name");
   String smonth = req.getParameter("smonth");        
   String sday = req.getParameter("sday");      
   String syear = req.getParameter("syear");    
   String emonth = req.getParameter("emonth");
   String eday = req.getParameter("eday");
   String eyear = req.getParameter("eyear");
   String shr1 = req.getParameter("start_hr1");      
   String smin1 = req.getParameter("start_min1");    
   String sampm1 = req.getParameter("start_ampm1");  
   String ehr1 = req.getParameter("end_hr1");      
   String emin1 = req.getParameter("end_min1");    
   String eampm1 = req.getParameter("end_ampm1");     
   String shr2 = req.getParameter("start_hr2");
   String smin2 = req.getParameter("start_min2");
   String sampm2 = req.getParameter("start_ampm2");
   String ehr2 = req.getParameter("end_hr2");
   String emin2 = req.getParameter("end_min2");
   String eampm2 = req.getParameter("end_ampm2");
   String recurr = req.getParameter("recurr");
   String course = req.getParameter("course");
   String oldCourse = req.getParameter("oldCourse");

   String ssampm1 = "AM";
   String seampm1 = "AM";
   String ssampm2 = "AM";
   String seampm2 = "AM";

   int s_min1 = 0;
   int e_min1 = 0;
   int s_min2 = 0;
   int e_min2 = 0;

   //
   //  Scan name for special characters and replace with HTML supported chars (i.e. '>' = &gt)
   //
   name = SystemUtils.filter(name);


   if (sampm1.equals ( "12" )) {      // sampm & eampm are either '00' or '12'
      ssampm1 = "PM";
   }

   if (eampm1.equals ( "12" )) {
      seampm1 = "PM";
   }

   if (sampm2.equals ( "12" )) {      // sampm & eampm are either '00' or '12'
      ssampm2 = "PM";
   }

   if (eampm2.equals ( "12" )) {
      seampm2 = "PM";
   }

   try {
      s_min1 = Integer.parseInt(smin1);
      e_min1 = Integer.parseInt(emin1);
      s_min2 = Integer.parseInt(smin2);
      e_min2 = Integer.parseInt(emin2);
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   //
   //  Validate minute values
   //
   if ((s_min1 < 0) || (s_min1 > 59) || (s_min2 < 0) || (s_min2 > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Start Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered: " + s_min1 + " and " + s_min2);
      out.println("<BR>Please try again.");
      out.println("<BR><BR><a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if ((e_min1 < 0) || (e_min1 > 59) || (e_min2 < 0) || (e_min2 > 59)) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>End Minute parameter must be in the range of 00 - 59.");
      out.println("<BR>You entered: " + e_min1 + " and " + e_min2);
      out.println("<BR>Please try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</input></form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Prompt user for confirmation
   //
   out.println(SystemUtils.HeadTitle("Update Double Tee Confirmation"));
   out.println("<BODY><CENTER><BR>");
   out.println("<br>");
   out.println("<H3>Update Double Tee Confirmation</H3><BR>");
   out.println("<BR>Please confirm the following updated parameters for the Double Tee:<br><b>" + name + "</b><br><br>");

   out.println("<table border=\"1\" cols=\"2\" bgcolor=\"#F5F5DC\">");
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

   if (!course.equals( "" )) {
      out.println("<tr><td width=\"200\" align=\"right\">");
      out.println("Course Name:&nbsp;&nbsp;");
      out.println("</td><td width=\"200\" align=\"left\">");
      out.println("&nbsp;&nbsp;&nbsp;" + course);
      out.println("</td></tr>");
   }

   out.println("<tr><td width=\"200\" align=\"right\">");
   out.println("Recurrence:&nbsp;&nbsp;");
   out.println("</td><td width=\"200\" align=\"left\">");
   out.println("&nbsp;&nbsp;&nbsp;" + recurr);
   out.println("</td></tr>");

   out.println("<tr><td width=\"200\" align=\"right\">");
   out.println("Start Time for Double Tees:&nbsp;&nbsp;");
   out.println("</td><td width=\"200\" align=\"left\">");

   if (s_min1 < 10) {
      out.println("&nbsp;&nbsp;&nbsp;" + shr1 + ":0" + s_min1 + "  " + ssampm1);
   } else {
      out.println("&nbsp;&nbsp;&nbsp;" + shr1 + ":" + s_min1 + "  " + ssampm1);
   }
     
   out.println("</td></tr>");

   out.println("<tr><td width=\"200\" align=\"right\">");
   out.println("End Time for Double Tees:&nbsp;&nbsp;");
   out.println("</td><td width=\"200\" align=\"left\">");

   if (e_min1 < 10) {
      out.println("&nbsp;&nbsp;&nbsp;" + ehr1 + ":0" + e_min1 + "  " + seampm1);
   } else {
      out.println("&nbsp;&nbsp;&nbsp;" + ehr1 + ":" + e_min1 + "  " + seampm1);
   }
   out.println("</td></tr>");

   out.println("<tr><td width=\"200\" align=\"right\">");
   out.println("Start Time for Cross-Over:&nbsp;&nbsp;");
   out.println("</td><td width=\"200\" align=\"left\">");

   if (s_min2 < 10) {
      out.println("&nbsp;&nbsp;&nbsp;" + shr2 + ":0" + s_min2 + "  " + ssampm2);
   } else {
      out.println("&nbsp;&nbsp;&nbsp;" + shr2 + ":" + s_min2 + "  " + ssampm2);
   }

   out.println("</td></tr>");

   out.println("<tr><td width=\"200\" align=\"right\">");
   out.println("End Time for Cross-Over:&nbsp;&nbsp;");
   out.println("</td><td width=\"200\" align=\"left\">");

   if (e_min2 < 10) {
      out.println("&nbsp;&nbsp;&nbsp;" + ehr2 + ":0" + e_min2 + "  " + seampm2);
   } else {
      out.println("&nbsp;&nbsp;&nbsp;" + ehr2 + ":" + e_min2 + "  " + seampm2);
   }
   out.println("</td></tr></table>");

   out.println("<form action=\"/" +rev+ "/servlet/Proshop_editdbltee\" method=\"post\" target=\"bot\">");
   out.println("<BR>ARE YOU SURE YOU WANT TO UPDATE THIS RECORD?");
   out.println("<BR><BR><b>NOTE: This may take a couple of minutes. DO NOT cancel or exit once it has started.</b>");
   out.println("<input type=\"hidden\" name=\"dbl_name\" value = \"" + name + "\">");
   out.println("<input type=\"hidden\" name=\"smonth\" value = " + smonth + ">");
   out.println("<input type=\"hidden\" name=\"sday\" value = " + sday + ">");
   out.println("<input type=\"hidden\" name=\"syear\" value = " + syear + ">");
   out.println("<input type=\"hidden\" name=\"emonth\" value = " + emonth + ">");
   out.println("<input type=\"hidden\" name=\"eday\" value = " + eday + ">");
   out.println("<input type=\"hidden\" name=\"eyear\" value = " + eyear + ">");
   out.println("<input type=\"hidden\" name=\"start_hr1\" value = " + shr1 + ">");
   out.println("<input type=\"hidden\" name=\"start_min1\" value = " + smin1 + ">");
   out.println("<input type=\"hidden\" name=\"start_ampm1\" value = " + sampm1 + ">");
   out.println("<input type=\"hidden\" name=\"end_hr1\" value = " + ehr1 + ">");
   out.println("<input type=\"hidden\" name=\"end_min1\" value = " + emin1 + ">");
   out.println("<input type=\"hidden\" name=\"end_ampm1\" value = " + eampm1 + ">");
   out.println("<input type=\"hidden\" name=\"start_hr2\" value = " + shr2 + ">");
   out.println("<input type=\"hidden\" name=\"start_min2\" value = " + smin2 + ">");
   out.println("<input type=\"hidden\" name=\"start_ampm2\" value = " + sampm2 + ">");
   out.println("<input type=\"hidden\" name=\"end_hr2\" value = " + ehr2 + ">");
   out.println("<input type=\"hidden\" name=\"end_min2\" value = " + emin2 + ">");
   out.println("<input type=\"hidden\" name=\"end_ampm2\" value = " + eampm2 + ">");
   out.println("<input type=\"hidden\" name=\"recurr\" value = \"" + recurr + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value = \"" + course + "\">");
   out.println("<input type=\"hidden\" name=\"oldCourse\" value = \"" + oldCourse + "\">");
   out.println("<br><input type=\"submit\" value=\"Yes\" name=\"Yes\">");
   out.println("</form><font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_dbltee\">");
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
   String name = req.getParameter("dbl_name");
   String course = req.getParameter("course");

   //
   //  Scan name for special characters and replace with HTML supported chars (i.e. '>' = &gt)
   //
   name = SystemUtils.filter(name);

   //
   //  Prompt user for confirmation
   //
   out.println(SystemUtils.HeadTitle("Delete Double Tee Confirmation"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Remove Double Tee Confirmation</H3><BR>");
   out.println("<BR>Please confirm that you wish to remove this Double Tee: <b>" + name + "</b><br>");
   out.println("<BR>NOTE: This will remove all empty 'Back 9' and 'Crossover' Tee Times previously created for<br>");
   out.println("this Double Tee. Any occupied tee times will not be removed.<br>");

   out.println("<form action=\"/" +rev+ "/servlet/Proshop_editdbltee\" method=\"post\" target=\"bot\">");
   out.println("<BR>ARE YOU SURE YOU WANT TO DELETE THIS RECORD?");
   out.println("<BR><BR><b>NOTE: Be Patient - This may take a couple of minutes.</b>");
   out.println("<input type=\"hidden\" name=\"dbl_name\" value =\"" + name + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value =\"" + course + "\">");
   out.println("<br><br><input type=\"submit\" value=\"Delete\" name=\"Delete\">");
   out.println("</form><font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_dbltee\">");
   out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");

 }  // wait for confirmation (Yes)


   //*************************************************************************
   //  removedbltee - Remove a dbltee from dbltee table - received confirmation
   //*************************************************************************

 private void removedbltee(HttpServletRequest req, PrintWriter out, HttpSession sess) {


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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_TEESHEETS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_TEESHEETS", out);
   }
     
   String omit = "";
   int status = 0;
   
   //
   // Get the name parameter from the hidden input field
   //
   String name = req.getParameter("dbl_name");
     
   String course = "";
   String recurr = "";

   long sdate = 0;
   long edate = 0;

   int stime1 = 0;
   int etime1 = 0;
   int stime2 = 0;
   int etime2 = 0;

   //
   // Delete the dbltee from the dbltee table
   //
   PreparedStatement stmt = null;
   ResultSet rs = null;

   try {
      //
      //  Check the name for special characters and translate if ncessary
      //
      stmt = con.prepareStatement (
               "SELECT sdate FROM dbltee2 WHERE name = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);       // put the parm in stmt
      rs = stmt.executeQuery();      // execute the prepared stmt

      if (!rs.next()) {                     // if not found

         name = SystemUtils.filter(name);   // translate the name
      }
      stmt.close();              // close the stmt
     
      // 
      //  Get the double tee's current settings
      //
      stmt = con.prepareStatement (
               "SELECT * FROM dbltee2 WHERE name = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);       // put the parm in stmt
      rs = stmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         sdate = rs.getLong("sdate");
         stime1 = rs.getInt("stime1");
         edate = rs.getLong("edate");
         etime1 = rs.getInt("etime1");
         stime2 = rs.getInt("stime2");
         etime2 = rs.getInt("etime2");
         recurr = rs.getString("recurr");
         course = rs.getString("courseName");
      }
      stmt.close();

      //
      //  Call SystemUtils to remove the double tees if tee sheets exist
      //
      status = SystemUtils.removeDblTee(course, recurr, sdate, edate, stime1, etime1, stime2, etime2, con);


      stmt = con.prepareStatement (
               "Delete FROM dbltee2 WHERE name = ?");

      stmt.clearParameters();               // clear the parms
      stmt.setString(1, name);              // put the parm in stmt
      stmt.executeUpdate();                 // execute the prepared stmt

      stmt.close();
   }
   catch (Exception exc) {

      dbError(out, exc);
      return;
   }

   //
   //  Member dbltee successfully deleted
   //
   out.println(SystemUtils.HeadTitle("Delete Double Tee Confirmation"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Double Tee Record Has Been Removed</H3><BR>");
   out.println("<BR><BR>Thank you, the Double Tee has been removed from the database.");
   out.println("<br><br><font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_dbltee\">");
   out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");

 }  // done with delete function


   //*******************************************************************
   //  updatedbltee - Update a dbltee from dbltee table - received a conf
   //*******************************************************************

 private void updatedbltee(HttpServletRequest req, PrintWriter out, HttpSession sess) {


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
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_TEESHEETS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_TEESHEETS", out);
   }
     
   //
   // Get the parameters entered
   //
   String name = req.getParameter("dbl_name");
   String s_month = req.getParameter("smonth");             //  month (00 - 12)
   String s_day = req.getParameter("sday");                 //  day (01 - 31)
   String s_year = req.getParameter("syear");               //  year (2002 - 20xx)
   String e_month = req.getParameter("emonth");             
   String e_day = req.getParameter("eday");                 
   String e_year = req.getParameter("eyear");               
   String start_hr1 = req.getParameter("start_hr1");         //  start hour (01 - 12)
   String start_min1 = req.getParameter("start_min1");       //  start min (00 - 59)
   String start_ampm1 = req.getParameter("start_ampm1");     //  AM/PM (00 or 12)
   String end_hr1 = req.getParameter("end_hr1");             //   .
   String end_min1 = req.getParameter("end_min1");           //   .
   String end_ampm1 = req.getParameter("end_ampm1");         //   .
   String start_hr2 = req.getParameter("start_hr2");         //  start hour (01 - 12)
   String start_min2 = req.getParameter("start_min2");       //  start min (00 - 59)
   String start_ampm2 = req.getParameter("start_ampm2");     //  AM/PM (00 or 12)
   String end_hr2 = req.getParameter("end_hr2");             //   .
   String end_min2 = req.getParameter("end_min2");           //   .
   String end_ampm2 = req.getParameter("end_ampm2");         //   .
   String recurr = req.getParameter("recurr");               //  recurrence
   String course = req.getParameter("course");
   String oldCourse = req.getParameter("oldCourse");

   String omit = "";
   String oldrecurr = "";
   
   int smonth = 0;
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
     
   int status1 = 0;
   int status2 = 0;
   int changed = 0;

   int oldstime1 = 0;
   int oldstime2 = 0;
   int oldetime1 = 0;
   int oldetime2 = 0;

   long oldsdate = 0;
   long oldedate = 0;

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
   //  adjust some values for the table
   //
   long sdate = (syear * 10000) + (smonth * 100) + sday;

   long edate = (eyear * 10000) + (emonth * 100) + eday;

   if (s_hr1 != 12) {              // _hr specified as 01 - 12 (_ampm = 00 or 12)

      s_hr1 = s_hr1 + s_ampm1;       // convert to military time (12 is always Noon or PM)
   }

   if (e_hr1 != 12) {              // ditto

      e_hr1 = e_hr1 + e_ampm1;
   }

   int stime1 = (s_hr1 * 100) + s_min1;
 
   int etime1 = (e_hr1 * 100) + e_min1;
 
   if (s_hr2 != 12) {              // _hr specified as 01 - 12 (_ampm = 00 or 12)

      s_hr2 = s_hr2 + s_ampm2;       // convert to military time (12 is always Noon or PM)
   }

   if (e_hr2 != 12) {              // ditto

      e_hr2 = e_hr2 + e_ampm2;
   }

   int stime2 = (s_hr2 * 100) + s_min2;
   
   int etime2 = (e_hr2 * 100) + e_min2;

   //
   //  verify the date and time fields
   //
   if ((sdate > edate) || (stime1 > etime1) || (stime2 > etime2)) {

      invData(out);    // inform the user and return
      return;
   }

   //
   //  Udate the record in the dbltee table
   //
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   int count = 0;
     
   try {
      //
      //  Check the name for special characters and translate if ncessary
      //
      pstmt = con.prepareStatement (
               "SELECT sdate FROM dbltee2 WHERE name = ? AND courseName = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);       // put the parm in stmt
      pstmt.setString(2, oldCourse);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (!rs.next()) {                     // if not found

         name = SystemUtils.filter(name);   // translate the name
      }
      pstmt.close();              // close the stmt

      //
      //  Get the double tee's current settings
      //
      pstmt = con.prepareStatement (
               "SELECT * FROM dbltee2 WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);       // put the parm in stmt
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         oldsdate = rs.getLong("sdate");
         oldstime1 = rs.getInt("stime1");
         oldedate = rs.getLong("edate");
         oldetime1 = rs.getInt("etime1");
         oldstime2 = rs.getInt("stime2");
         oldetime2 = rs.getInt("etime2");
         oldrecurr = rs.getString("recurr");
      }
      pstmt.close();

      //
      //  Check if double was changed where tee sheets need to be updated
      //
      if (!course.equals( "oldCourse" ) || !recurr.equals( "oldrecurr" )) {

         changed = 1;
      }
      if (sdate != oldsdate) {

         changed = 1;
      }
      if (edate != oldedate) {

         changed = 1;
      }
      if (stime1 != oldstime1) {

         changed = 1;
      }
      if (etime1 != oldetime1) {

         changed = 1;
      }
      if (stime2 != oldstime2) {

         changed = 1;
      }
      if (etime2 != oldetime2) {

         changed = 1;
      }

      //
      //  Update the dbltee
      //
      pstmt = con.prepareStatement (
        "UPDATE dbltee2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ?, " +
        "start_hr1 = ?, start_min1 = ?, stime1 = ?, " +
        "edate = ?, end_mm = ?, end_dd = ?, end_yy = ?, " +
        "end_hr1 = ?, end_min1 = ?, etime1 = ?, start_hr2 = ?, start_min2 = ?, stime2 = ?, " +
        "end_hr2 = ?, end_min2 = ?, etime2 = ?, recurr = ?, courseName = ? WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, sdate);
      pstmt.setInt(2, smonth);
      pstmt.setInt(3, sday);
      pstmt.setInt(4, syear);
      pstmt.setInt(5, s_hr1);
      pstmt.setInt(6, s_min1);
      pstmt.setInt(7, stime1);
      pstmt.setLong(8, edate);
      pstmt.setInt(9, emonth);
      pstmt.setInt(10, eday);
      pstmt.setInt(11, eyear);
      pstmt.setInt(12, e_hr1);
      pstmt.setInt(13, e_min1);
      pstmt.setInt(14, etime1);
      pstmt.setInt(15, s_hr2);
      pstmt.setInt(16, s_min2);
      pstmt.setInt(17, stime2);
      pstmt.setInt(18, e_hr2);
      pstmt.setInt(19, e_min2);
      pstmt.setInt(20, etime2);
      pstmt.setString(21, recurr);
      pstmt.setString(22, course);
      pstmt.setString(23, name);

      count = pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();
        
      if (changed == 1) {     // if dbltee changed
        
         //
         //  Call SystemUtils to remove the double tees if tee sheets exist
         //
         status1 = SystemUtils.removeDblTee(oldCourse, oldrecurr, oldsdate, oldedate, oldstime1, oldetime1, oldstime2, oldetime2, con);

         //
         //  Call SystemUtils to build the new double tees if tee sheets exist
         //
         status2 = SystemUtils.buildDblTee(name, con);
      }

   }
   catch (Exception exc) {

      dbError(out, exc);
      return;
   }

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Edit Double Tee"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Double Tee Record Has Been Updated</H3>");
   out.println("<BR><BR>Thank you, the Double Tee has been updated in the system database.");
   out.println("<BR><BR>");
   out.println("<br><font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_dbltee\">");
   out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");

 }


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception exc) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR><BR>Error in Proshop_editdbltee: " + exc.getMessage());
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");

 }


 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<br><font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");

 }

}
