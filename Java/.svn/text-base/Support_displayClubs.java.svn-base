/***************************************************************************************     
 *   Support_displayClubs:  This servlet will process the display request from Support's display page.
 *                          It will display the 'clubs' database table.
 *
 *
 *   called by:  support_display.htm
 *
 *   created: 6/13/2001   Bob P.
 *
 *   last updated:
 *
 *       10/23/09   Add list option to only show active clubs that are not demo sites.
 *        4/10/08   Add support for 'inactive' status.
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


public class Support_displayClubs extends HttpServlet {
                           
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
 
      doPost(req, resp);   // go process in doPost
 } 
 
 
 
 // Process the form request from support_display.htm.....
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Connection con = null;                 // init DB objects
   Statement stmt = null;
   ResultSet rs = null;
     
   String support = "support";             // valid username
   String clubname = "";

   HttpSession session = null; 
   
   boolean excel = false;

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equals( support )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   //
   // Load the JDBC Driver and connect to DB
   //
   String club = "" +rev+ "";          // get db name for 'clubs' table

   try {
      con = dbConn.Connect(club);

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <FORM>");
      out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
      out.println("</FORM></CENTER></BODY></HTML>");
      return;
   }

   int i = 1;           // init the club counter
   int inact = 0;
   String status = "";
   String actOnly = "No";

   
   if (req.getParameter("actOnly") != null) {     // if only active clubs

      actOnly = req.getParameter("actOnly");
   }
   
   
   try{
      if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format

         resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
         
         excel = true;
      }
   }
   catch (Exception exc) {
   }

   //
   // Get the 'clubs' table 
   //
   try {
       
      stmt = con.createStatement();        // create a statement

      if (actOnly.equals("Yes")) {     // if we are to display only active clubs

         rs = stmt.executeQuery("SELECT * FROM clubs WHERE inactive=0 ORDER BY clubname");
         
      } else {
            
         rs = stmt.executeQuery("SELECT * FROM clubs ORDER BY clubname");
      }

      out.println(SystemUtils.HeadTitle("Display Clubs Table"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><H2>Clubs Table</H2><BR>");

      if (excel == false) {    

         out.println("<FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></FORM>"); 
         
         out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Support_displayClubs\">");
         out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
         out.println("<input type=\"hidden\" name=\"actOnly\" value=\"" +actOnly+ "\">");
         out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("<BR>");
         
         if (actOnly.equals("Yes")) {     // if we are to display only active clubs
            
           out.println("<p align=center><a href=\"/" +rev+ "/servlet/Support_displayClubs\">");
           out.println("List All Clubs</a></p>");
           
         } else {
            
           out.println("<p align=center><a href=\"/" +rev+ "/servlet/Support_displayClubs?actOnly=Yes\">");
           out.println("List Only Active Clients</a></p>");
         }    
         
         out.println("<BR>");
         out.println("NOTE: Click on the Status to change it.<BR><BR>");
      }
      
      out.println("<Table border=\"1\" align=\"center\" bgcolor=\"#FFFFCC\">");
      out.println("<tr bgcolor=\"#CC9966\">");
      out.println("<td><p><b>Count</b></p></td>");
      out.println("<td><p><b>Name</b></p></td>");
      out.println("<td><p><b>Full Name</b></p></td>");
      out.println("<td><p><b>Start Date</b></p></td>");
      out.println("<td><p><b>Status</b></p></td>");
      if (excel == false) {    
         out.println("<td><p><b>Delete</b></p></td>");
      }
      out.println("</tr>");

      while(rs.next()) {

         clubname = rs.getString("clubname");
         inact = rs.getInt("inactive");
         
         status = "Act";
         
         if (inact == 1) {
            status = "Inact";
         }
         
         boolean skip = false;
         
         if (actOnly.equals("Yes") && 
             (clubname.startsWith("demo") || clubname.startsWith("mfirst") || clubname.startsWith("testJonas") || 
              clubname.startsWith("notify") || clubname.startsWith("test") || clubname.equals("admiralscove2") ) ) {   
            
            skip = true;      // skip it
         }
         
         if (skip == false) {

            if (excel == false) {    
               out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Support_clubs\">");
               out.println("<input type=\"hidden\" name=\"clubname\" value=\"" +clubname+ "\">");
            }
            out.println("<tr>");
            out.println("<td>" + i);
            out.println("</td><td>" + clubname);
            out.println("</td><td>" + rs.getString("fullname"));
            out.println("</td><td>" + rs.getLong("startdate"));
            out.println("</td>");
            out.println("<td>");
            if (excel == false) {    
               out.println("<input type=\"submit\" name=\"" +status+ "\" value=\"" +status+ "\">");
            } else {            
               out.println(status);
            }
            out.println("</td>");
            if (excel == false) {    
               out.println("<td>");
               out.println("<input type=\"submit\" name=\"delete\" value=\"Delete Club\">");
               out.println("</td>");
            }
            out.println("</tr>");
            if (excel == false) {    
               out.println("</form>");
            }
            
            i++;           // bump counter
            
         }

      }

      out.println("</TABLE>");
      if (excel == false) {    
         out.println("<BR><BR><FORM><INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></FORM>");
      }
      out.println("</CENTER></BODY></HTML>");

      stmt.close();

   }
   catch (Exception exc) {             // SQL Error

      out.println("<HTML><HEAD><TITLE>SQL Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H1>SQL Type Error</H1>");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <FORM>");
      out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
      out.println("</FORM></CENTER></BODY></HTML>");
     
   }
   
   //
   // Done - return.......
   //
   if (con != null) {
      try {
         con.close();       // Close the db connection........
      }
      catch (SQLException ignored) {
      }
   }
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
   out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
   out.println("</FORM></CENTER></BODY></HTML>");

 }

}
