/***************************************************************************************
 *   Support_find_clubster:  This servlet will find all clubs that contain Clubster logins.
 *
 *
 ***************************************************************************************
 */

import com.foretees.common.Connect;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;


public class Support_find_clubster extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    String support = "support";             // valid username

    HttpSession session = null;
    session = req.getSession(false);  // Get user's session object (no new one)

    if (session == null) {

        invalidUser(out);            // Intruder - reject
        return;
    }

   String userName = (String)session.getAttribute("user");   // get username
   
   if (!userName.equals( support )) {

      invalidUser(out);            // Intruder - reject
      return;
   }
   
   out.println("<HTML><HEAD><TITLE>Database Upgrade</TITLE></HEAD>");
   out.println("<BODY><CENTER>");
   out.println("<BR><BR><H3>This job will check all clubs' session logs for caller=clubster.</H3>");
   out.println("<BR><BR>Click 'Continue' to start the job.");
   out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A><BR><BR>");
   
   out.println("<form method=post><input type=submit value=\"Continue\" onclick=\"return confirm('Are you sure?')\">");
   out.println(" <input type=hidden value=\"update\" name=\"todo\"></form>");
   /*
   out.println("<form method=post><input type=submit value=\"  Test  \">");
   out.println(" <input type=hidden value=\"test\" name=\"todo\"></form>");
   * 
   */
   
   out.println("</CENTER></BODY></HTML>");
   
   out.close();
   
 }
 

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();


    String support = "support";             // valid username

    HttpSession session = null;
    session = req.getSession(false);  // Get user's session object (no new one)
    if (session == null) {

        invalidUser(out);            // Intruder - reject
        return;
    }

   String userName = (String)session.getAttribute("user");   // get username

   if (!userName.equals( support )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String action = "";
   if (req.getParameter("todo") != null) action = req.getParameter("todo");
   
   if (action.equals("update")) {
       
       doUpdate(out);
       return;
   }
   
   out.println("<p>Nothing to do.</p>todo="+action);
   
 }


 private void doUpdate(PrintWriter out) {
     

    Connection con1 = null;                  // init DB objects
    Connection con2 = null;
    PreparedStatement pstmt = null;
    Statement stmt1 = null;
    Statement stmt1a = null;
    Statement stmt2 = null;
    Statement stmt3 = null;
    ResultSet rs1 = null;
    ResultSet rs2 = null;
    ResultSet rs3 = null;

    out.println("<HTML><HEAD><TITLE>Database Query</TITLE></HEAD>");
    out.println("<BODY><H3>Starting Job to Check for Clubster Users</H3>");
    out.flush();

    String club = "";
    String name = "";

    try {

        con1 = dbConn.Connect(rev);
    } catch (Exception exc) {

        // Error connecting to db....
        out.println("<BR><BR>Unable to connect to the DB.");
        out.println("<BR>Exception: "+ exc.getMessage());
        out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
        out.println("</BODY></HTML>");
        return;
    }
    
    //
    // Get the club names from the 'clubs' table
    //
    //  Process each club in the table
    //
    int x1 = 0;
    int x2 = 0;
    int i = 0;
  
    boolean skip = true;
    
    try {
       
        stmt1 = con1.createStatement();
        rs1 = stmt1.executeQuery("SELECT clubname, fullname FROM clubs ORDER BY clubname");

        while (rs1.next()) {

            x1++; 
            club = rs1.getString(1);                // get a club name
            name = rs1.getString(2);                // get full club name

            con2 = dbConn.Connect(club);            // get a connection to this club's db
       
            stmt2 = con2.createStatement();
            
            rs2 = stmt2.executeQuery("SELECT date FROM sessionlog WHERE msg LIKE '%clubster'");   

            if (rs2.next()) {
            
                out.println("<br><br>");
                out.print("Club found: " + club + ", " +name);
            }
            
            stmt2.close();
            con2.close();
        }
            
        stmt1.close();
        con1.close();
      
   }
   catch (Exception e) {

      // Error connecting to db....

      out.println("<BR><BR><H3>Fatal Error!</H3>");
      out.println("Error performing update to club '" + club + "'.");
      out.println("<BR>Exception: "+ e.getMessage());
      out.println("<BR>Message: "+ e.toString());
      out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
      out.println("</BODY></HTML>");
      out.close();
      Connect.close(stmt2, con2);
      Connect.close(stmt1, con1);
      return;
   }
    
   Connect.close(stmt2, con2);
   Connect.close(stmt1, con1);

   out.print("<BR><BR>Done, " +x1+ "clubs checked.");
   out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   //out.flush();
   //out.close();
    
 }
 
  
  
 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/v5/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR>Please <A HREF=\"Logout\">login</A>");
   out.println("</CENTER></BODY></HTML>");

 }

}
