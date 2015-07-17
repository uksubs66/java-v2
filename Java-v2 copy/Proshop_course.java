/***************************************************************************************     
 *   Proshop_course:  This servlet will process the 'course setup' request from Proshop's
 *                    System Config page.
 *
 *
 *   called by:  proshop menu (doGet)
 *
 *   created: 12/05/2002   Bob P.
 *
 *
 *   last updated:
 *
 *        7/18/03   Enhancements for Version 3 of the software.
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

public class Proshop_course extends HttpServlet {
                         
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 //**********************************************************
 //
 // Process the initial request from Proshop menu
 //
 //   parms passed:  none
 //
 //**********************************************************
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
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
     
    // Define parms
   String course = "";         // course names
   int multi = 0;              // multiple course support indicator

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   // Get existing club parms if they exist
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi FROM club WHERE clubName != ''");

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
        out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
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
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;

   }

   //
   //  If there is only 1 course, then jump straight to Proshop_parms for that course
   //
   if (multi == 0) {

      //
      // Get course name from clubparm2 database table
      //
      try {

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName FROM clubparm2 WHERE courseName != ''");

         if (rs.next()) {

            course = rs.getString("courseName");     // get the course name

         } else {

            course = "";        // indicate new course parms
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
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      out.println("<HTML>");
      out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
      out.println("<meta http-equiv=\"Refresh\" content=\"0; url=Proshop_parms?course=" + course + "\">");
      out.println("<Title>Proshop Course Page</Title>");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER>");
      out.println("<hr width=\"40%\">");
      out.println("<table border=\"0\" cols=\"1\" bgcolor=\"#336633\" cellpadding=\"8\">");
      out.println("<form action=\"Proshop_parms\" method=\"get\">");
      out.println("<tr><td><font color=\"#FFFFFF\" size=\"2\">");
      out.println("<input type=\"hidden\" maxlength=\"30\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"submit\" value=\"Continue\">");
      out.println("</font></td></tr></form></table>");
      out.println("</center></font></body></html>");
   
   } else {

      //
      // Get course names from clubparm2 database table
      //
      try {

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName FROM clubparm2 WHERE courseName != ''");

         if (!rs.next()) {     // if there are none yet - go to setup parms for 1st course

            out.println("<HTML>");
            out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
            out.println("<Title>Proshop Course Page</Title>");
            out.println("<meta http-equiv=\"Refresh\" content=\"0; url=Proshop_parms?course=''\">");
            out.println("</HEAD>");
            out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
            SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<CENTER>");
            out.println("<hr width=\"40%\">");
            out.println("<table border=\"0\" cols=\"1\" bgcolor=\"#336633\" cellpadding=\"8\">");
            out.println("<form action=\"Proshop_parms\" method=\"get\">");
            out.println("<tr><td><font color=\"#FFFFFF\" size=\"2\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
            out.println("<input type=\"submit\" value=\"Continue\">");
            out.println("</font></td></tr></form></table>");
            out.println("</center></font></body></html>");
            return;
         }

         rs = stmt.executeQuery("SELECT courseName FROM clubparm2 WHERE courseName != ''");

         //
         //  Build the HTML page to solicit the desired course to update
         //

         out.println(SystemUtils.HeadTitle("Proshop - Course Setup Page"));

         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

         out.println("<center>");

         out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\"><tr><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<b>Course Setup</b><br>");
         out.println("Select a course from the drop-down list below or select 'Add New Course' to add a new course.");
         out.println("<br><br>Click on 'Continue' to update the course parameters or 'Return' to exit without changes.");
         out.println("</font>");
         out.println("</td></tr></table>");

         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<form action=\"Proshop_parms\" method=\"get\" target=\"bot\">");

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

            course = rs.getString("courseName");     // get the course name

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
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
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
      out.println("<form action=\"Proshop_parms\" method=\"get\" target=\"bot\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
      out.println("<input type=\"submit\" value=\"Add New Course\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>&nbsp;&nbsp;&nbsp;&nbsp;");

      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font><br>");
      out.println("</center></font></body></html>");
   }

 }  // end of doGet

}
