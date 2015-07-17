/***************************************************************************************
 *   Support_runlottery:  This servlet will force a lottery to be processed on the dev server.
 *
 *
 *       Prompt for lottery info and then process it.
 * 
 *    ******* RUN ON DEV SERVER **************
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


public class Support_runlottery extends HttpServlet {


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
   out.println("<BODY><CENTER><H3>WARNING: THIS WILL PROCESS REQUESTS FOR THE LOTTERY ENTERED HERE!</H3>");
   out.println("<BR><BR>Identify the Lottery Here.");
   
   out.println("<form action=\"Support_runlottery\" method=post>");
      out.println("Name of Lottery:&nbsp;&nbsp;<input type=\"text\" name=\"lname\" value=\"\" size=\"30\" maxlength=\"60\">");
      out.println("Date (yyyymmdd):&nbsp;&nbsp;<input type=\"text\" name=\"ldate\" value=\"\" size=\"8\" maxlength=\"8\">");
   out.println("<input type=submit value=\"Run Lottery\" onclick=\"return confirm('Are you sure?')\">");
   out.println(" </form>");
   
   out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A><BR><BR>");
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
   String club = (String)session.getAttribute("club");   // get username

   if (!userName.equals( support )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String lname = "";
   String ldate = "";
   
   if (req.getParameter("lname") != null) lname = req.getParameter("lname");
   
   if (req.getParameter("ldate") != null) ldate = req.getParameter("ldate");
   
   if (!lname.equals("") && !ldate.equals("")) {
       
       doUpdate(lname, ldate, club, out);
       return;
   }
   
   out.println("<p>Nothing to do.</p>");
   
 }


 private void doUpdate(String lname, String ldate, String club, PrintWriter out) {
     

    Connection con = null;                  // init DB objects

    out.println("<HTML><HEAD><TITLE>Database Update</TITLE></HEAD>");
    out.println("<BODY><H3>Running Lottery - " +lname+ " ........</H3>");
    out.flush();


    try {

        con = dbConn.Connect(club);
        
    } catch (Exception exc) {

        // Error connecting to db....
        out.println("<BR><BR>Unable to connect to the DB.");
        out.println("<BR>Exception: "+ exc.getMessage());
        out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
        out.println("</BODY></HTML>");
        return;
    }
    
    long date = Long.parseLong(ldate);

    //
    //  Call SystemUtils to run the lottery
    //
    SystemUtils.testLott(lname, date, club, con);      // process this lottery
    
    
   out.println("<BR><BR>Lottery Finished!");
   out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   
   out.close();
   
   try {
    
        con.close();

    } catch (Exception e) {
    }
    
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
