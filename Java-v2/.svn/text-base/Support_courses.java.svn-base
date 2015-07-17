/***************************************************************************************
 * 
 *   Support_getProEmails: Allows configuration of Member and Proshop default courses
 *
 *   created: 8/22/2008   Brad K.
 *
 *   last updated:       ******* keep this accurate *******
 *
 *        3/19/14   Rewrote most of defaultCourseConfig to fix some issues, clean up code format, and remove the 20 course limit.
 *       10/27/08   Added -ALL- option for Proshop and Memeber default course
 *        8/22/08   Added defaultCourseConfig processing
 * 
 * 
 ***************************************************************************************/

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.Connect;
import com.foretees.common.Utilities;

public class Support_courses extends HttpServlet {
    
    
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
  
 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     
     doPost(req, resp);      // call doPost processing
 }
  
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {    
     
     resp.setContentType("text/html");
     PrintWriter out = resp.getWriter();
     
     
     HttpSession session = null;
     
     //
     // Make sure user didn't enter illegally
     //
     session = req.getSession(false);  // Get user's session object (no new one)
     
     if (session == null) {
         
         invalidUser(out);            // Intruder - reject
         return;
     }
     
     String user = (String)session.getAttribute("user");   // get username
     String club = (String)session.getAttribute("club");
     //String course = (String)session.getAttribute("course");
     
     if (!SystemUtils.verifySupport(user) && !user.startsWith( "sales" )) {
         
         invalidUser(out);            // Intruder - reject
         return;
     }
    
     Connection con = Connect.getCon(req);                      // get DB connection
     
     if (con == null) {
         
         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, please contact customer support.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
     }
     
     if (req.getParameter("default") != null) {
         defaultCourseConfig(req, user, club, con, out);
         return;
     }
 }
 
 
 //*********************************************************************************************
 // Allows for the configuration of the Member and Proshop default course settings for a club
 //*********************************************************************************************
 private void defaultCourseConfig(HttpServletRequest req, String user, String club, Connection con, PrintWriter out) {
     
     //
     //  array to hold course names
     //
     List <String> courseA = new ArrayList<String>();
     
     String defaultPro = "";
     String defaultMem = "";
     
     PreparedStatement pstmt = null;
     Statement stmt = null;
     ResultSet rs = null;
     
     // Print common header
     out.println(SystemUtils.HeadTitle("Default Course Configuration"));
     out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
     out.println("<hr width=\"40%\">");
     out.println("<br><H2>Default Course Configuration</H2>");
     
     if (req.getParameter("memCourse") != null && req.getParameter("proCourse") != null) {
         
         //
         // Update database with selected courses
         //
         defaultMem = req.getParameter("memCourse");
         defaultPro = req.getParameter("proCourse");
         
         try {
             pstmt = con.prepareStatement("UPDATE club5 SET default_course_mem = ?, default_course_pro = ?");
             
             pstmt.clearParameters();
             pstmt.setString(1, defaultMem);
             pstmt.setString(2, defaultPro);
             
             if (pstmt.executeUpdate() > 0) {
                 out.println("<p>Default courses updated successfully!");
             } else {
                 out.println("<p>Default courses were not updated.");
             }
             
         } catch (Exception e1) {
             
             out.println("<BR><H2>Database Error</H2><BR>");
             out.println("<BR><BR>Error trying to connect to database. Error = " + e1.getMessage());
             out.println("<BR><BR><BR>");
             if (user.startsWith( "sales" )) {
                 out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
             } else {
                 out.println("<BR><A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
             }
             out.println("</CENTER></BODY></HTML>");
             out.close();
         }
         
     } else {

         //  Display selection drop-down boxes
         try {
             
             //
             //  Get the names of all courses for this club
             //
             try {
                 stmt = con.createStatement();        // create a statement

                 rs = stmt.executeQuery("SELECT courseName " +
                         "FROM clubparm2 WHERE first_hr != 0");

                 while (rs.next()) {
                     courseA.add(rs.getString("courseName"));      // add course name to arraylist
                 }
                 
             } catch (Exception exc) {
                 Utilities.logError("Support_courses.defaultCourseConfig - " + club + " - Error getting list of course names from clubparm2 - ERR: " + exc.toString());
             } finally {
                 
                 try { rs.close(); }
                 catch (Exception ignore) {}
                 
                 try { stmt.close(); }
                 catch (Exception ignore) {}
             }

             try {
                 stmt = con.createStatement();

                 rs = stmt.executeQuery("SELECT default_course_mem, default_course_pro FROM club5");

                 if (rs.next()) {
                     defaultMem = rs.getString("default_course_mem");
                     defaultPro = rs.getString("default_course_pro");
                 }
                 
             } catch (Exception exc) {
                 Utilities.logError("Support_courses.defaultCourseConfig - " + club + " - Error getting pro/mem default course settings - ERR: " + exc.toString());
             } finally {
                 
                 try { rs.close(); }
                 catch (Exception ignore) {}
                 
                 try { stmt.close(); }
                 catch (Exception ignore) {}
             }

             //
             // Print select box for Default Member Course
             //
             out.println("<br>");
             out.println("<form action=\"Support_courses?default\" method=\"post\" name=\"defCourse\">");
             out.println("<p><b>Default Member Course: </b>");
             out.println("<select size=\"1\" name=\"memCourse\">");
             out.println("<option value=\"\"></option>");
             out.println("<option " + (defaultMem.equals("-ALL-") ? "selected " : "") + "value=\"-ALL-\">-ALL-</option>");

             for (String courseName : courseA) {
                 out.println("<option " + (defaultMem.equals(courseName) ? "selected " : "") + "value=\"" + courseName + "\">" + courseName + "</option>");
             }
             
             out.println("</select>");
             out.println("&nbsp;&nbsp;<br>");

             //
             // Print select box for Default Proshop Course
             //
             out.println("<p><b>Default Proshop Course: </b>");
             out.println("<select size=\"1\" name=\"proCourse\">");
             out.println("<option value=\"\"></option>");
             out.println("<option " + (defaultPro.equals("-ALL-") ? "selected " : "") + "value=\"-ALL-\">-ALL-</option>");

             for (String courseName : courseA) {
                 out.println("<option " + (defaultPro.equals(courseName) ? "selected " : "") + "value=\"" + courseName + "\">" + courseName + "</option>");
             }

             out.println("</select>");
             out.println("&nbsp;&nbsp;<br><br><br>");
             out.println("<input type=\"submit\" value=\"Submit\" name=\"submitConfig\">");
             out.println("</form>");

         } catch (Exception e1) {

             out.println("<BR><H2>Database Error</H2><BR>");
             out.println("<BR><BR>Error trying to connect to database. Error = " + e1.getMessage());
             out.println("<BR><BR><BR>");
             if (user.startsWith( "sales" )) {
                 out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
             } else {
                 out.println("<BR><A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
             }
             out.println("</CENTER></BODY></HTML>");
             out.close();
         }
     }
     
     out.println("<br><br>");
     if (user.startsWith("sales")) {
         out.println("<A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
     } else {
         out.println("<A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
     }
     out.println("</CENTER></BODY></HTML>");
     return;
 }
 
 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR> <FORM>");
   out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'>");
   out.println("</FORM></CENTER></BODY></HTML>");

 }
}
