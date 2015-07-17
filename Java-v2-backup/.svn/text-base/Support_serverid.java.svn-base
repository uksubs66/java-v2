/***************************************************************************************
 *   Support_serverid:  This class will display the server's current id and allow
 *                      the user to change it.  This is necessary should the Master
 *                      server go down and we need to make another server the master.
 *
 *
 *   called by:  servlet/Support_main
 *
 *   created: 3/02/2006   Bob P.
 *
 *   last updated:
 *
 *                      6-12-12 Add server id 101-109
 *                      8-13-08 Add up to 16 nodes
 *
 *
 ***************************************************************************************
 */

import com.foretees.common.Common_Server;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Support_serverid extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //
 //  doGet - display the current id and post form for changing it
 //
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

   if (!SystemUtils.verifySupport(user)) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   int server_id = Common_Server.SERVER_ID;
 
   //
   //   Display the Server ID
   //
   out.println("<HTML><HEAD><TITLE>Support Display Server ID</TITLE></HEAD>");
   out.println("<BODY><CENTER><BR><BR><H3>Display or Change The Server ID</H3><BR>");
   out.println("<Table border=\"0\" align=\"center\" bgcolor=\"#F5F5DC\">");
   out.println("<tr>");

   out.println("<form method=\"post\" action=\"Support_serverid\">");
   out.println("<td align=\"center\">");

   out.println("<BR>BE CAREFUL - MAKE SURE THIS IS WHAT YOU WANT TO DO!!!");
   out.println("<BR><BR>Only set this server to a timer server if no other server is running those timers!");
   
   out.println("<BR><BR>The current server id is: <b>" +server_id+ "</b>");

   out.println("<BR><BR>Change the server id: &nbsp;&nbsp;&nbsp;");

        out.println("<select size=\"1\" name=\"newid\">");
          for (int i=1;i<17;i++) {
        
             out.println("<option " + ((i==server_id) ? "selected" : "") + ">"+i+"</option>");
          }
          for (int i=101;i<110;i++) {

             out.println("<option " + ((i==server_id) ? "selected" : "") + ">"+i+"</option>");
          }
        out.println("</select>");

         out.println("<br><br><input type=\"submit\" value=\"Change It\">");
   out.println("</td></form></tr>");
   out.println("</TABLE>");
   out.println("<BR><BR><A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 //
 //  doPost - change the current id !!!!
 //
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

   if (!SystemUtils.verifySupport(user)) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   //
   // Get the parameter entered
   //
   String new_id = req.getParameter("newid");
     
   String msg = "";

   int newid = 0;


   try {
       
      newid = Integer.parseInt(new_id);       // convert to int
     
      //
      //  Change the id
      //
      if (newid > 0 && newid < 17) {        // validate
        
         Common_Server.SERVER_ID = newid;        // change it !!!!!!!!!!!!
  
      } else {

         msg = "Invalid Server Id Received.  Must be in the range of 1 - 16.  Id = " +new_id;
         pError(msg, out);
         return;
      }

      //
      //   Display the Server ID
      //
      out.println("<HTML><HEAD><TITLE>Support Display Server ID</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>The Server ID Has Been Changed</H3><BR>");
        
      out.println("<BR>The new server id is: <b>" + Common_Server.SERVER_ID + "</b>");

      out.println("<BR><BR><A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
        
   }
   catch (NumberFormatException e) {
     
      msg = "Invalid Server Id Received.  Error converting to Int.  Id = " +new_id;
      pError(msg, out);
      return;
   }

 }


 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void pError(String msg, PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Procedure Error</H2><BR>");
   out.println("<BR><BR>" +msg+ "<BR>");
   out.println("<BR><BR><A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
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
