/***************************************************************************************
 *   Support_query_all:  This servlet will run a custom query on all active clubs.
 *
 *
 *     ******* Use this job to run custom querries **********
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

import com.foretees.common.Connect;
import com.foretees.common.Utilities;
import com.foretees.common.verifySlot;

public class Support_query_all extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


    doPost(req, resp);
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

    Connection con1 = null;                  // init DB objects
    Connection con2 = null;
    Statement stmt1 = null;
    Statement stmt2 = null;
    ResultSet rs1 = null;
    ResultSet rs2 = null;

    out.println("<HTML><HEAD><TITLE>Database Query</TITLE></HEAD>");
    out.println("<BODY><H3>List Each Club With a Custom Style Sheet</H3>");
    out.println("<table border=1><tr><td><b>Site</b></td><td><b>Club Name</b></td><td><b>Style Sheet</b></td></tr>");
    out.flush();

    String club = "";
    String fullname = "";
    String styles = "";

    try {

        con1 = Connect.getCon(rev);    // connect to v5
        
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
    try {

        stmt1 = con1.createStatement();
        rs1 = stmt1.executeQuery("SELECT clubname, fullname FROM v5.clubs WHERE inactive=0 ORDER BY clubname");

        while (rs1.next()) {

            club = rs1.getString(1);                // get a club name
            fullname = rs1.getString(2);            // get club's full name
            
            if (!club.startsWith("demo")) {         // if NOT a demo site

               con2 = Connect.getCon(club);            // get a connection to this club's db
               stmt2 = con2.createStatement();         // create a statement

               try {

                     rs2 = stmt2.executeQuery("SELECT custom_styles FROM club5");

                     if (rs2.next()) {

                        styles = rs2.getString("custom_styles");       // get the custom styles name, if any

                        if (!styles.equals("")) {

                              out.println("<tr><td>" +club+ "</td><td>" +fullname+ "</td><td>" +styles+ "</td></tr>");
                        }
                     }

               } catch (Exception exc) {
                  out.println(club + " failed!!!!!!!!!!!!!!!!!!!! " + exc.toString());
               }

               stmt2.close();
               con2.close();
            }
            
        }         // end if WHILE clubs

        out.println("</table>");
        
    } catch (Exception exc) {

        // Error connecting to db....
        out.println("<BR><BR>Unable to connect to the DB.");
        out.println("<BR>Exception: "+ exc.getMessage());
        out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
        out.println("</BODY></HTML>");
        return;
    }
                    

    try {
        stmt1.close();
        con1.close();
    } catch (Exception ignore) {}
    
    out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
    out.println("</BODY></HTML>");
    out.close();  

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
