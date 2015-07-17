/***************************************************************************************
 *   Support_update_democlubs_all: 
 *
 *
 *       Updates the demo club table for ALL clubs
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;


public class Support_update_democlub_all extends HttpServlet {


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
   out.println("<BODY><CENTER><H3>WARNING: PERMENT DATABASE CHANGES PENDING!</H3>");
   out.println("<BR><BR>Click 'Run' to start the job.");
   out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A><BR><BR>");
   
   out.println("<form method=post><input type=submit value=\"Update\" onclick=\"return confirm('Are you sure?')\">");
   out.println(" <input type=hidden value=\"update\" name=\"todo\"></form>");
   
   out.println("<form method=post><input type=submit value=\"  Test  \">");
   out.println(" <input type=hidden value=\"test\" name=\"todo\"></form>");
   
   out.println("</CENTER></BODY></HTML>");
   
   out.close();
   
 }
 

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();
    //PrintWriter out = new PrintWriter(resp.getOutputStream());


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
   
   if (action.equals("test")) {
       
       doTest(out);
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

    out.println("<HTML><HEAD><TITLE>Database Update</TITLE></HEAD>");
    out.println("<BODY><H3>Starting DB Update...</H3>");
    out.flush();

    String club = "";

    try {

        con1 = dbConn.Connect(rev);
        
    } catch (Exception exc) {

        // Error connecting to db....
        out.println("<BR><BR>Unable to connect to the DB.");
        out.println("<BR>Exception: "+ exc.getMessage());
        out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
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
       rs1 = stmt1.executeQuery("SELECT clubname, (SELECT COUNT(*) FROM v5.clubs) AS total FROM v5.clubs ORDER BY clubname");

       while (rs1.next()) {

          x1++; 

          club = rs1.getString(1);                // get a club name
          x2 = rs1.getInt(2);                     // get count of clubs

          con2 = dbConn.Connect(club);            // get a connection to this club's db
          stmt2 = con2.createStatement();         // create a statement
            
          out.println("<br><br>");
          out.print("[" + x1 + "/" + x2 + "] Starting " + club);
          out.flush();

          stmt2.executeUpdate("UPDATE demo_clubs SET icn=null WHERE inact = 1");
           
          stmt2.close(); 
          con2.close();
         
       } // loop all clubs
      
       //out.println("<br>" + i + " clubs have more than one course.");
            
       stmt1.close();
       con1.close();
      
    }
    catch (Exception e) {

       // Error connecting to db....

       out.println("<BR><BR><H3>Fatal Error!</H3>");
       out.println("Error performing update to club '" + club + "'.");
       out.println("<BR>Exception: "+ e.getMessage());
       out.println("<BR>Message: "+ e.toString());
       out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
       out.println("</BODY></HTML>");
       out.close();
       return;
    }

   out.println("<BR><BR>Upgrade Finished!  The upgrade is complete for all clubs.");
   out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   //out.flush();
   //out.close();
    
 }
 
 
 
 private void doTest(PrintWriter out) {

    Connection con1 = null;                  // init DB objects
    Connection con2 = null;
    Statement stmt1 = null;
    Statement stmt2 = null;
    PreparedStatement pstmt = null;
    ResultSet rs1 = null;
    ResultSet rs2 = null;
    ResultSet rs3 = null;

    int i = 0;
    int t = 0;
    int c = 0;
    boolean found = false;
    String club = "";
    
    out.println("<HTML><HEAD><TITLE>Database Test</TITLE></HEAD>");
    out.println("<BODY><H3>Starting DB Test...</H3>");
    out.flush();


    int tmp_total = 0;
 
    try {

        con1 = dbConn.Connect(rev);
    } catch (Exception exc) {

        // Error connecting to db....
        out.println("<BR><BR>Unable to connect to the DB.");
        out.println("<BR>Exception: "+ exc.getMessage());
        out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
        out.println("</BODY></HTML>");
        return;
    }

    try {

        stmt1 = con1.createStatement();
        rs1 = stmt1.executeQuery("SELECT clubname FROM clubs ORDER BY clubname");

        while (rs1.next()) {

            club = rs1.getString(1);                // get a club name
            con2 = dbConn.Connect(club);             // get a connection to this club's db
            stmt2 = con2.createStatement();           // create a statement
            i = 0;
            found = false;
            int x = 0;
            int prob = 0;

            String [] gtypes = new String[99];
            String response = "";
            String problem = "";

            //out.println("<br><br>Starting " + club);

            
            
            // FIND CLUBS with inactive demo clubs
            rs2 = stmt2.executeQuery("SELECT name FROM demo_clubs WHERE inact=1");
            
            if ( rs2.next() ) {

                out.print("<br><br><b><font size=+1><u>" + club + "</u></font></b>");
                out.println("&nbsp;&nbsp;&nbsp;" + rs2.getString("name"));                
            }
            
            stmt2.close(); 
            con2.close();
            
            out.flush();     
        
        } // loop all clubs

        // out.println("<p><i>Found " + tmp_total); // + " total in " + t + " clubs.</i></p>");

        stmt2.close(); 
        con2.close();
        stmt1.close();
        con1.close();

    } catch (Exception e) {

        // Error connecting to db....
        out.println("<BR><BR><H3>Fatal Error!</H3>");
        out.println("Error performing update to club '" + club + "'.");
        out.println("<BR>Exception: "+ e.getMessage());
        out.println("<BR>Message: "+ e.toString());
        out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
        out.println("</BODY></HTML>");
        out.close();
        return;
    }
    
   out.println("<BR><BR>Test Finished!  The test is complete for all clubs.");
   out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   
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
   out.println("<BR><BR>Please <A HREF=\"/v5/servlet/Logout\">login</A>");
   out.println("</CENTER></BODY></HTML>");

 }

 /*
 private static Authenticator getAuthenticator(final String user, final String pass) {

    Authenticator auth = new Authenticator() {

       public PasswordAuthentication getPasswordAuthentication() {

         return new PasswordAuthentication(user, pass); // credentials
         //return new PasswordAuthentication("support@foretees.com", "fikd18"); // credentials
       }
    };

    return auth;
 }
 */
}
