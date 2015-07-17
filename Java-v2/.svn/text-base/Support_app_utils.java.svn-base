/***************************************************************************************
 *   Support_app_utils:  This class will process app functions for sales and support users.
 *
 *
 *   called by:  support_main2.htm and sales_main.htm
 *
 *   created: 5/01/2015   Bob P.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.Connect;


public class Support_app_utils extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
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
   
   String status = "";
   String status_color = "";

   if (!user.equals( "support" ) && !user.startsWith( "sales" )) {

      invalidUser(out);            // Intruder - reject
      return;
   }
   
   //
   //  Get the current app status for this club
   //
   status = getAppStatus(club);
   
   if (status == null) {
       status = "-None-";
       status_color = "red";
   } else if (status.equalsIgnoreCase("Staging")) {
       status_color = "darkorange";
   } else if (status.equalsIgnoreCase("Live")) {
       status_color = "green";
   }
   
   out.println("<HTML><HEAD><TITLE>Support App Utilities</TITLE></HEAD>");
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR><BR>");
   out.println("<H2>ClubCentral App Utilities</H2>");
     
   out.println("Use this utility to chenge the App status for the current club.");
   out.println("<BR><BR>");

   out.println("Club: <b>" +club+ "</b>");
   out.println("<BR><BR>");
   out.println("The current status of this club is <span style=\"font-weight:bold; color: " + status_color + ";\">" +status+ "</span>.");
   out.println("<BR><BR><BR>");

      out.println("<form method=\"post\" action=\"Support_app_utils\">");
      out.println("<input type=\"hidden\" name=\"toggle\" value = \"yes\">");
      out.println("<input type=\"hidden\" name=\"club\" value = \"" +club+ "\">");
      
      out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"status\">");
      out.println("<option value=\"\"" + (status.equalsIgnoreCase("-None-") ? " selected" : "") + ">-None-</option>");
      out.println("<option value=\"Staging\"" + (status.equalsIgnoreCase("Staging") ? " selected" : "") + ">Staging</option>");
      out.println("<option value=\"Live\"" + (status.equalsIgnoreCase("Live") ? " selected" : "") + ">Live</option>");
      out.println("</select>");
      
      out.println("<BR><BR><input type=\"submit\" value=\"Change Club Status\" style=\"text-decoration:underline;\">");
      out.println("</form>");

     
   out.println("<BR><BR>");
   if (user.startsWith( "sales" )) {
      out.println("<A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
   } else {
      out.println("<A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
   }
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 //  doPost - perform the enable or disable 
 // *********************************************************
   
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                 // init DB objects
   PreparedStatement pstmt = null;


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

   if (!user.equals( "support" ) && !user.startsWith( "sales" )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   
   try {
      con = dbConn.Connect(rev);

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
         out.println("<BR><BR>");
         if (user.startsWith( "sales" )) {
            out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
         } else {
            out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
         }
      out.println("</CENTER></BODY></HTML>");
      return;
   }



   String toggle = "";
   String status = "";

   if (req.getParameter("toggle") != null) {

      toggle = req.getParameter("toggle");
   }
   
   if (req.getParameter("status") != null) {

      status = req.getParameter("status");
   }
   
   if (toggle.equals("yes")) {        // if request to toggle the app status

        try {

            pstmt = con.prepareStatement (
                "UPDATE clubs SET msac = ? " +
                "WHERE clubname = ?");

            pstmt.clearParameters();     
            pstmt.setString(1, status);
            pstmt.setString(2, club);
            pstmt.executeUpdate();     

        }
        catch (Exception e1) {

            out.println(SystemUtils.HeadTitle("Database Error"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H2>Database Error</H2><BR>");
            out.println("<BR><BR>Error trying to change app status. Error = " + e1.getMessage());
            out.println("<BR><BR><BR>");
            if (user.startsWith( "sales" )) {
                out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
            } else {
                out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
            }
            out.println("</CENTER></BODY></HTML>");
        } finally {
            try {
                pstmt.close();
                con.close();
            } catch (Exception e2) {
            }
        }
        
   } else {
       
        out.println(SystemUtils.HeadTitle("Procedure Error"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><H2>Invalid Request</H2><BR>");
        out.println("<BR><BR>Unable to determine request.");
        out.println("<BR><BR><BR>");
        if (user.startsWith( "sales" )) {
            out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
        } else {
            out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
        }
        out.println("</CENTER></BODY></HTML>");      
        return;
   }

    out.println(SystemUtils.HeadTitle("Done"));
    out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
    out.println("<hr width=\"40%\">");
    out.println("<BR><H2>Status Changed</H2><BR>");
    out.println("<BR><BR>Your request to change the status has been completed.");
    out.println("<BR><BR><BR>");
    if (user.startsWith( "sales" )) {
        out.println("<BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
    } else {
        out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
    }
    out.println("</CENTER></BODY></HTML>");      
        
 }
 
 
 
 // *********************************************************
 //   Get the current app status
 // *********************************************************

 private String getAppStatus(String club) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    Connection con = null;
    String status = "";
     
    try {
        con = dbConn.Connect(rev);     // connect to v5

    }
    catch (Exception exc) {

        status = "Unable to connect to v5 db";
        return(status);
    }

    
   try {
     
      pstmt = con.prepareStatement (
         "SELECT msac " +
         "FROM clubs " +
         "WHERE clubname = ?");

      pstmt.clearParameters();     
      pstmt.setString(1, club);
      
      rs = pstmt.executeQuery();     

      if (rs.next()) {

         status = rs.getString("msac");
         
      } else {
          
          status = "Not Found";
      }

    }
    catch (Exception exc) {

        status = "Unable to find club status";
        return(status);
    }

    return(status);
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
