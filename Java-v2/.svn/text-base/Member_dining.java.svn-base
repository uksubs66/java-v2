/***************************************************************************************     
 *   Proshop_dining:  This servlet will do member verification and call the Dining Request
 *                    Form builder method from Proshop_dining
 *
 *
 *   called by:  proshop menu (doGet) and self.
 *
 *   created: 01/21/09   Brad K.
 *
 *
 *   last updated:
 *
 *       03/10/09   Added ability to invoke from email links
 *       01/21/09   Created class.
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

// foretees imports
import com.foretees.common.Connect;

public class Member_dining extends HttpServlet {
                         
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 //**********************************************************
 //
 // Process the dining request form display
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

     doPost(req, resp);

 }


 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = null;

   if (req.getParameter("ext-dReq") != null) {

       session = req.getSession(false);

       // if the user sits too long on the exernal welcome page their special session may of expired
       if (session == null || (String)session.getAttribute("ext-user") == null) {

            out.println("<HTML>");
            out.println("<HEAD>");
            out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
            out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
            out.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
            out.println("<TITLE>Access Error</TITLE></HEAD>");
            out.println("<BODY><CENTER>");
            out.println("<H2>Access Error - Please Read</H2>");
            out.println("Sorry, your session either timed out, you didn't login, or your computer does not allow the use of Cookies.");
            out.println("<BR><BR>This site requires the use of Cookies for security purposes.");
            out.println("<BR>We use them to verify your session and prevent unauthorized access.");
            out.println("<BR><BR>Please check your 'Privacy' settings, under 'Tools', 'Internet Options'");
            out.println("<BR>(for MS Internet Explorer).  This must be set to 'Medium High' or lower.");
            out.println("<BR><BR>If you have a firewall, please check its settings as well.");
            out.println("<BR><BR><HR width=\"500\">");
            out.println("<b>NOTE:</b> You must be logged in to access the ForeTees system. You cannot bookmark a page");
            out.println("<BR>within ForeTees and then return to it later without logging in.  If you access ForeTees");
            out.println("<BR>from your club web site, then you must login to the web site.  If you access ForeTees");
            out.println("<BR>directly, then you must do so through the ForeTees Login page.");
            out.println("<BR><HR width=\"500\"><BR>");
            out.println("If you have tried all of the above and still receive this message,");
            out.println("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
            out.println("<BR><b>Provide your name or member number, the name of your club and a detailed description of your problem.</b>");
            out.println("<BR>Thank you.");
            out.println("<BR><BR>");
            out.println("<a href=\"Logout\" target=\"_top\">Return</a>");
            out.println("<BR><BR>");
            out.println("<CENTER>Server: " + Common_Server.SERVER_ID + "</CENTER>");
            out.println("</CENTER></BODY></HTML>");
            out.close();

            return;
       }

   } else {

       session = SystemUtils.verifyMem(req, out);             // check for intruder
   }

   if (session == null) return;

   Connection con = Connect.getCon(req);                      // get DB connection

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

   if (req.getParameter("dReq") != null) {
       Proshop_dining.buildDiningForm(req, out, session, con);
       return;
   }

   if (req.getParameter("reqSubmit") != null) {
       Proshop_dining.processDiningForm(req, out, session, con);
       return;
   }

 }


 public void doPostOld(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder
   
   if (session == null) {
     
      return;
   }

   Connection con = Connect.getCon(req);                      // get DB connection

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
              
   if (req.getParameter("dReq") != null) {
       Proshop_dining.buildDiningForm(req, out, session, con);
       return;
   }
   
   if (req.getParameter("reqSubmit") != null) {
       Proshop_dining.processDiningForm(req, out, session, con);
       return;
   }
   
 }
}
