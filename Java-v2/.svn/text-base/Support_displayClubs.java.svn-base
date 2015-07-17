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
 *        2/10/11   Add FlxRez and Dining indicators to the list.
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
   Connection con2 = null;                 // init DB objects
   Statement stmt = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
     
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
      out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'>");
      out.println("</FORM></CENTER></BODY></HTML>");
      return;
   }

   int i = 1;           // init the club counter
   int inact = 0;
   int golf = 0;
   int flx = 0;
   
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
   //**********************************************
   //  Check if call is for 'Status Change''
   //**********************************************
   //
   if (req.getParameter("Act") != null || req.getParameter("Inact") != null) {

      clubname = req.getParameter("clubname");           //  club name - short version for db name

      out.println(SystemUtils.HeadTitle("Club Change Request"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Club Removal or Status Change Request</H3>");
      
      out.println("<form method=\"post\" action=\"Support_displayClubs\">");
      out.println("<input type=\"hidden\" name=\"clubname\" value=\"" +clubname+ "\">");

      if (req.getParameter("Inact") != null) {        // if status change and status was Inactive
         out.println("<BR><BR>You have requested that the status for " +clubname+ " be changed to ACTIVE.");
         out.println("<input type=\"hidden\" name=\"confact\" value=\"yes\">");
      }

      if (req.getParameter("Act") != null) {        // if status change and status was Active
         out.println("<BR><BR>You have requested that the status for " +clubname+ " be changed to INACTIVE.");
         out.println("<input type=\"hidden\" name=\"confinact\" value=\"yes\">");
      }
      out.println("<BR><BR>ARE YOU SURE YOU WANT TO DO THIS?");
      out.println("<font size=\"2\">");
      
      out.println("<BR><BR><input type=\"submit\" value=\"YES - Continue\">");
      out.println("</FORM>");
      
      out.println("<BR><BR><A HREF=\"/" +rev+ "/servlet/Support_displayClubs\">NO - CANCEL</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;

   }       // end of if status change call

   
   //
   //**********************************************
   //  Check if call is for 'Confirm Status Change'
   //**********************************************
   //
   if (req.getParameter("confact") != null || req.getParameter("confinact") != null) {
      
      int actStatus = 0;
      
      if (req.getParameter("confinact") != null) {   // if request to mark Inactive

         actStatus = 1;   

      } else {

         actStatus = 0;          // request to mark Active
      }      

      clubname = req.getParameter("clubname");           //  club name - short version for db name

      try {

         pstmt = con.prepareStatement (
           "UPDATE clubs SET inactive = ? WHERE clubname = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, actStatus);
         pstmt.setString(2, clubname);
         pstmt.executeUpdate();         // execute the prepared stmt

         pstmt.close();   // close the stmt

         out.println("<script type=\"text/javascript\">");
         out.println("alert('Club Status Has Been Changed For " +clubname+ "');");
         out.println("</script>");

       /*
         out.println("<HTML><HEAD><TITLE>Club Status Change Complete</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Club Status Has Been Changed For " +clubname+ "</H3>");
         out.println("<BR><BR>Please continue.");
         out.println("<BR><BR><A HREF=\"/" +rev+ "/servlet/Support_displayClubs\">Return</A>.");
         out.println("</CENTER></BODY></HTML>");
         * 
         */

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR>Exception:   " + exc.getMessage());
         out.println("<BR><BR><A HREF=\"/" +rev+ "/servlet/Support_displayClubs\">Return</A>.");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

   }       // end of if delete call

   
   //
   // Call is to display the list - Get the 'clubs' table 
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

         out.println("<BR><A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
         
         out.println("<form method=\"post\" action=\"Support_displayClubs\">");
         out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
         out.println("<input type=\"hidden\" name=\"actOnly\" value=\"" +actOnly+ "\">");
         out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("<BR>");
         
         if (actOnly.equals("Yes")) {     // if we are to display only active clubs
            
           out.println("<p align=center><a href=\"Support_displayClubs\">");
           out.println("List All Clubs</a></p>");
           
         } else {
            
           out.println("<p align=center><a href=\"Support_displayClubs?actOnly=Yes\">");
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
      out.println("<td><p><b>Golf</b></p></td>");
      out.println("<td><p><b>FlxRez</b></p></td>");
      out.println("<td><p><b>Status</b></p></td>");
    //  if (excel == false) {    
    //     out.println("<td><p><b>Delete</b></p></td>");
    //  }
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
            
            // get the golf and flxrez settings
            
            con2 = dbConn.Connect(clubname);            // get a connection to this club's db

            if (con2 != null) {

                pstmt = con2.prepareStatement ("SELECT foretees_mode, genrez_mode FROM club5");
                pstmt.clearParameters();
                
                rs2 = pstmt.executeQuery();          // execute the prepared stmt

                if (rs2.next()) {

                  golf = rs2.getInt(1);
                  flx = rs2.getInt(2);
                }
                
                pstmt.close();

                try { con2.close(); } 
                catch (Exception ignore) {}
            }
               

            if (excel == false) {    
               out.println("<form method=\"post\" action=\"Support_displayClubs\">");
               out.println("<input type=\"hidden\" name=\"clubname\" value=\"" +clubname+ "\">");
            }
            out.println("<tr>");
            out.println("<td>" + i);
            out.println("</td><td>" + clubname);
            out.println("</td><td>" + rs.getString("fullname"));
            out.println("</td><td>" + rs.getLong("startdate"));
            if (golf > 0) {
               out.println("</td><td>Yes");
            } else {
               out.println("</td><td>&nbsp;");
            }
            if (flx > 0) {
               out.println("</td><td>Yes");
            } else {
               out.println("</td><td>&nbsp;");
            }
            out.println("</td>");
            out.println("<td>");
            if (excel == false) {    
               out.println("<input type=\"submit\" name=\"" +status+ "\" value=\"" +status+ "\">");
            } else {            
               out.println(status);
            }
            out.println("</td>");
            /*
            if (excel == false) {    
               out.println("<td>");
               out.println("<input type=\"submit\" name=\"delete\" value=\"Delete Club\">");
               out.println("</td>");
            }
             */
            out.println("</tr>");
            if (excel == false) {    
               out.println("</form>");
            }
            
            i++;           // bump counter
            
         }

      }

      out.println("</TABLE>");
      if (excel == false) {    
         out.println("<BR><A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
      }
      out.println("</CENTER></BODY></HTML>");

      stmt.close();

   }
   catch (Exception exc) {             // SQL Error

      out.println("<HTML><HEAD><TITLE>SQL Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H1>SQL Type Error</H1>");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
     
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
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
   out.println("</CENTER></BODY></HTML>");

 }

}
