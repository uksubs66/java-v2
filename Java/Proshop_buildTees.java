/***************************************************************************************     
 *   Proshop_buildTees:  This servlet will process the 'build tees' request
 *                      from Proshop's config menu page.  This is used by the Proshop
 *                      to indicate that ocnfig is done and we should now build the tees.
 *
 *
 *   called by:  proshop menu
 *
 *   created: 3/14/2002   Bob P.
 *
 *   last updated:
 *
 *        1/24/05   Ver 5 - change club2 to club5.
 *        1/14/04   Ver 4 - change tee sheets from 30 days to 365 days.
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


public class Proshop_buildTees extends HttpServlet {
 
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //****************************************************
 // Process the form request from proshop menu
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

   //
   //  Check if this is the 2nd call from doGet processing (single course setup)
   //
   if (req.getParameter("course") != null) {
     
      doPost(req, resp);      // call doPost processing
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

   //
   //  First we must determine if there are multiple courses and if so, which course to use.
   //
   String course = "";         // course name
   int multi = 0;              // multiple course support indicator

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   // Get existing club parms if they exist
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);     // get the multi-course indicator

      } else {

        // Parms do not exist yet - inform user to start with club setup

        out.println(SystemUtils.HeadTitle("Sequence Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Setup Sequence Error</H3>");
        out.println("<BR><BR>Sorry, we are unable to process your request at this time.");
        out.println("<BR>The club setup has not been completed.");
        out.println("<BR>Please return to Configuration and select Club Setup.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
        out.println("</CENTER></BODY></HTML>");
        return;

      }
      stmt.close();
   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR>Proshop_buildTees doGet Processing #1.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;

   }

   //
   //  If there is only 1 course, and the parms are built, then jump straight to doPost for that course
   //
   if (multi == 0) {

      //
      // Get parms from clubparm2 database table
      //
      try {

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT first_hr FROM clubparm2 WHERE first_hr != 0");

         if (!rs.next()) {

            // Parms do not exist yet - inform user to start with course setup

            out.println(SystemUtils.HeadTitle("Sequence Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Setup Sequence Error</H3>");
            out.println("<BR><BR>Sorry, we are unable to process your request at this time.");
            out.println("<BR>The course setup has not been completed.");
            out.println("<BR>Please return to Configuration and select Course Setup.");
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }
         stmt.close();
      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><br>Exception: " + exc.getMessage());
         out.println("<BR>Proshop_buildTees doGet Processing #2.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      //
      //   Single Course - call self again with course parm specified.
      //
      out.println("<HTML>");
      out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
      out.println("<Title>Proshop Course Page</Title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_buildTees?course=" + course + "\">");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER>");
      out.println("<hr width=\"40%\">");
      out.println("<table border=\"0\" cols=\"1\" bgcolor=\"#336633\" cellpadding=\"8\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_parms\" method=\"get\">");
      out.println("<tr><td><font color=\"#FFFFFF\" size=\"2\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"submit\" value=\"Continue\">");
      out.println("</font></td></tr></form></table>");
      out.println("</center></font></body></html>");
      return;

   } else {

      //
      //  Multiple Courses specified in club table
      //
      //    Get course names from clubparm2 database table and request user to select the course
      //
      try {

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName FROM clubparm2 WHERE courseName != ''");

         if (!rs.next()) {     // if there are none yet - inform user to complete setup

            out.println(SystemUtils.HeadTitle("Sequence Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Setup Sequence Error</H3>");
            out.println("<BR><BR>Sorry, we are unable to process your request at this time.");
            out.println("<BR>The course setup has not been completed.");
            out.println("<BR>Please return to Configuration and select Course Setup.");
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }

         rs = stmt.executeQuery("SELECT courseName FROM clubparm2 WHERE courseName != ''");

         //
         //  Build the HTML page to solicit the desired course to update
         //

         out.println(SystemUtils.HeadTitle("Proshop - Course Selection Page"));

         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

         out.println("<center>");

         out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\"><tr><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<b>Course Selection</b><br>");
         out.println("To build tee sheets, select a course from the drop-down list below.");
         out.println("<br><br>Click on 'Continue' to build tee sheets for the selected course, or 'Return' to exit without changes.");
         out.println("<br><br><b>Note:</b>  Please be patient as this may take a couple of minutes.");
         out.println("</font>");
         out.println("</td></tr></table>");

         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_buildTees\" method=\"post\" target=\"bot\">");

         out.println("<br><table border=\"2\" cols=\"2\" bgcolor=\"#F5F5DC\">");
         out.println("<tr>");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<td width=\"200\" align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("<br>Select a course: &nbsp;&nbsp;");
               out.println("<br><br>");
               out.println("<p align=\"center\"><br><input type=\"submit\" value=\"Continue\"></p>");
               out.println("</font>");

            out.println("</td><td width=\"200\" align=\"left\"><font size=\"2\">");
               out.println("&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"course\">");

         while (rs.next()) {

            course = rs.getString(1);     // get the course name

               out.println("<option value=\"" + course + "\">" + course + "</option>");

         }
         stmt.close();
      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><br>Exception: " + exc.getMessage());
         out.println("<BR>Proshop_buildTees doGet Processing #3.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

         out.println("</select>");
         out.println("</font>");
         out.println("</td>");
         out.println("</tr>");
         out.println("</table>");
         out.println("</form>");

      out.println("<br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</input></form></font><br>");
      out.println("</center></font></body></html>");
   }

 }   // end of doGet


 //****************************************************
 // Process the request from above, or the confirmation
 //****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;
   String omit = "";

   boolean b = false;

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   String club = (String)session.getAttribute("club");             // get club name
   Connection con = SystemUtils.getCon(session);                   // get DB connection

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

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   int count = 0;      
   short xx = 0;

   //
   //  Get the golf course name requested
   //
   String course = req.getParameter("course");

   //
   //   if call was for a Confirmation then build the tee sheet for the selected course
   //
   if (req.getParameter("conf") != null) {

      //
      //   User has requested that we build the tee sheets now.  Set them as built and call systemutils to build them
      //
      try {
        
         PreparedStatement pstmt1 = con.prepareStatement (
            "UPDATE clubparm2 SET xx = 1 WHERE courseName = ?");

         pstmt1.clearParameters();            // clear the parms
         pstmt1.setString(1, course);

         count = pstmt1.executeUpdate();      // execute the prepared stmt

         pstmt1.close();

      }
      catch (Exception ignore) {

      }

      //
      //  Now go build the tee sheets for the next 365 days
      //
      try {

         b = SystemUtils.scanTee(con, club);         // go build tee sheets

      }
      catch (Exception e) {
         //
         //  Error building tee sheets - inform user
         //

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we were unable to build the tee sheets for the first 365 days.");
         out.println("<BR><br>Exception: " + e.getMessage());
         out.println("<BR>Proshop_buildTees doPost Processing #1.");
         out.println("<BR><BR>Please inform customer support (provide the above message).");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;

      }  // end of try

      //
      // Database updated - inform user
      //
      out.println(SystemUtils.HeadTitle("Proshop Build Tees"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<BR><BR><H3>Tee Sheets Have Been Built</H3>");
      out.println("<BR><BR>Thank you, Tee Sheets for the next 365 days have been built and are ready for use.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
             
      if (!course.equals( "" )) {

         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_buildTees\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");

      } else {

         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
      }
      out.println("</CENTER></BODY></HTML>");
      return;

   } else {
     
      //
      //  Request is first - use the course specified and request a confirmation
      //
      // Check if tee sheets already exist
      //
      try {

         PreparedStatement pstmt2 = con.prepareStatement (
                                   "SELECT date FROM teecurr2 WHERE date != 0 AND courseName = ?");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.setString(1, course);

         rs = pstmt2.executeQuery();      // execute the prepared stmt
              
         if (rs.next()) {

            //
            // Sheets already exist - inform user
            //
            out.println(SystemUtils.HeadTitle("Procedure Error"));
            out.println("<BODY>");
            SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
            out.println("<CENTER>");
            out.println("<BR><BR><H3>Procedure Error</H3>");
            if (!course.equals( "" )) {
               out.println("<BR><BR>Sorry, the tee sheets have already been built for " + course + ".");
            } else {
               out.println("<BR><BR>Sorry, the tee sheets have already been built.");
            }
            out.println("<BR>There should be no need to perform this function again.");
            out.println("<BR>Please contact customer support if the tee sheets have not been built.");
            out.println("<BR><BR><BR>");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_announce\" method=\"get\" target=\"bot\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            pstmt2.close();
            return;
         }
         pstmt2.close();

      }
      catch (Exception ignore) {   // no sheets - good
      }

      //
      // Check the 'wait for config' setting in the club parms
      //
      try {

         if (!course.equals( "" )) {

            PreparedStatement pstmt3 = con.prepareStatement (
                                      "SELECT xx FROM clubparm2 WHERE courseName = ?");

            pstmt3.clearParameters();        // clear the parms
            pstmt3.setString(1, course);

            rs = pstmt3.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               xx = rs.getShort("xx");            // get current 'build tees' value (retain it)

               //
               // Parms already exist - check the value of xx
               //
               if (xx != 0) {

                  out.println(SystemUtils.HeadTitle("Procedure Error"));
                  out.println("<BODY>");
                  SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
                  out.println("<CENTER>");
                  out.println("<BR><BR><H3>Procedure Error</H3>");
                  if (!course.equals( "" )) {
                     out.println("<BR><BR>Sorry, the tee sheets have already been built for " + course + ".");
                  } else {
                     out.println("<BR><BR>Sorry, the tee sheets have already been built.");
                  }
                  out.println("<BR>There should be no need to perform this function again.");
                  out.println("<BR>Please contact customer support if the tee sheets have not been built.");
                  out.println("<BR><BR><BR>");
                  out.println("<form action=\"/" +rev+ "/servlet/Proshop_announce\" method=\"get\" target=\"bot\">");
                  out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form>");
                  out.println("</CENTER></BODY></HTML>");

               } else {

                  //
                  // Prompt user to make sure they really want to proceed
                  //
                  out.println(SystemUtils.HeadTitle("Build Tees Confirmation"));
                  out.println("<BODY>");
                  SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
                  out.println("<CENTER>");
                  out.println("<BR>");
                  out.println("<br>");
                  out.println("<H3>Build Tees Confirmation</H3><BR>");
                  out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

                  if (!course.equals( "" )) {
                     out.println("<br>You have requested that Tee Sheets for the next 365 days be built for " + course + ".");
                  } else {
                     out.println("<br>You have requested that Tee Sheets for the next 365 days be built.");
                  }
                  out.println("<br>It is important that you first complete the configuration of the ");
                  out.println("<br>Club Parameters and Double Tees.  Once the Tee Sheets are built");
                  out.println("<br>certain parameter changes will not take affect for 365 days (as new");
                  out.println("<br>sheets are built). After these initial 365 sheets are built, new sheets");
                  out.println("<br>will be built nightly and will always utilize the latest parameters.<br>");
                  out.println("<BR>ARE YOU SURE YOU WANT TO BUILD THE TEE SHEETS NOW?");
                  out.println("<br>(This may take a minute or so to complete)");

                  out.println("<form action=\"/" +rev+ "/servlet/Proshop_buildTees\" method=\"post\" target=\"bot\">");
                  out.println("<input type=\"hidden\" name=\"conf\" value=\"yes\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                  out.println("<br><input type=\"submit\" value=\"Yes\" name=\"Yes\">");
                  out.println("</form>");

                  out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
                  out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");
                  out.println("</CENTER></BODY></HTML>");
               }

            } else {
               //
               // Parms do not exist - cannot build tees yet
               //
               out.println(SystemUtils.HeadTitle("Procedure Error"));
               out.println("<BODY>");
               SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
               out.println("<CENTER>");
               out.println("<BR><BR><H3>Procedure Error</H3>");
               out.println("<BR><BR>Sorry, we are unable to build the tee sheets at this time.");
               out.println("<BR>You must first complete the system configuration.");
               out.println("<BR>Return to 'System Config' menu and start with the top menu item and work your way down.");
               out.println("<BR>Please try again once you have completed the configuration.");
               out.println("<BR><BR><BR>");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_announce\" method=\"get\" target=\"bot\">");
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form>");
               out.println("</CENTER></BODY></HTML>");
            }

            pstmt3.close();

         } else {

            stmt = con.createStatement();        // create a statement

            rs = stmt.executeQuery("SELECT xx FROM clubparm2");

            if (rs.next()) {

               xx = rs.getShort("xx");            // get current 'build tees' value (retain it)

               //
               // Parms already exist - check the value of xx
               //
               if (xx != 0) {

                  out.println(SystemUtils.HeadTitle("Procedure Error"));
                  out.println("<BODY>");
                  SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
                  out.println("<CENTER>");
                  out.println("<BR><BR><H3>Procedure Error</H3>");
                  if (!course.equals( "" )) {
                     out.println("<BR><BR>Sorry, the tee sheets have already been built for " + course + ".");
                  } else {
                     out.println("<BR><BR>Sorry, the tee sheets have already been built.");
                  }
                  out.println("<BR>There should be no need to perform this function again.");
                  out.println("<BR>Please contact customer support if the tee sheets have not been built.");
                  out.println("<BR><BR><BR>");
                  out.println("<form action=\"/" +rev+ "/servlet/Proshop_announce\" method=\"get\" target=\"bot\">");
                  out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form>");
                  out.println("</CENTER></BODY></HTML>");

               } else {

                  //
                  // Prompt user to make sure they really want to proceed
                  //
                  out.println(SystemUtils.HeadTitle("Build Tees Confirmation"));
                  out.println("<BODY>");
                  SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
                  out.println("<CENTER>");
                  out.println("<BR><BR>");
                  out.println("<H3>Build Tees Confirmation</H3><BR>");
                  out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

                  if (!course.equals( "" )) {
                     out.println("<br>You have requested that Tee Sheets for the next 365 days be built for " + course + ".");
                  } else {
                     out.println("<br>You have requested that Tee Sheets for the next 365 days be built.");
                  }
                  out.println("<br>It is important that you first complete the configuration of the ");
                  out.println("<br>Club Parameters and Double Tees.  Once the Tee Sheets are built");
                  out.println("<br>certain parameter changes will not take affect for 365 days (as new");
                  out.println("<br>sheets are built). After these initial 365 sheets are built, new sheets");
                  out.println("<br>will be built nightly and will always utilize the latest parameters.<br>");
                  out.println("<BR>ARE YOU SURE YOU WANT TO BUILD THE TEE SHEETS NOW?");
                  out.println("<br>(This may take a minute or so to complete)");

                  out.println("<form action=\"/" +rev+ "/servlet/Proshop_buildTees\" method=\"post\" target=\"bot\">");
                  out.println("<input type=\"hidden\" name=\"conf\" value=\"yes\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                  out.println("<br><input type=\"submit\" value=\"Yes\" name=\"Yes\">");
                  out.println("</form>");

                  out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
                  out.println("<input type=\"submit\" value=\"No - Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");
                  out.println("</CENTER></BODY></HTML>");
               }

            } else {
               //
               // Parms do not exist - cannot build tees yet
               //
               out.println(SystemUtils.HeadTitle("Procedure Error"));
               out.println("<BODY>");
               SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
               out.println("<CENTER>");
               out.println("<BR><BR><H3>Procedure Error</H3>");
               out.println("<BR><BR>Sorry, we are unable to build the tee sheets at this time.");
               out.println("<BR>You must first complete the system configuration.");
               out.println("<BR>Return to 'System Config' menu and start with the top menu item and work your way down.");
               out.println("<BR>Please try again once you have completed the configuration.");
               out.println("<BR><BR><BR>");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_announce\" method=\"get\" target=\"bot\">");
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form>");
               out.println("</CENTER></BODY></HTML>");
            }

            stmt.close();
         }
      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><br>Exception: " + exc.getMessage());
         out.println("<BR>Proshop_buildTees doPost Processing #2  Course = " + course + ".");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
      }
   }
 }   // end of doPost   

}
