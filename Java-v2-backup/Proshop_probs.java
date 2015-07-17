/***************************************************************************************
 *   Proshop_probs:  This servlet will provide the user with info to contact ForeTees.
 *
 *
 *   called by:  menu
 *
 *   created: 12/08/2004   Bob P.
 *
 *   last updated:
 * 
 *                 5/21/2012  Correct the sales email link.
 *                 2/10/2006  Change support phone number and email.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

public class Proshop_probs extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 // Process the call from the menu
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //   output the html page
   //
   out.println(SystemUtils.HeadTitle("Proshop Probs"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onLoad=cursor()>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr>");
      out.println("<td align=\"center\" valign=\"top\" width=\"60%\">");
         out.println("<font size=\"2\">");
         out.println("<table align=\"center\" cellpadding=\"7\" border=\"1\" bgcolor=\"#336633\">");
            out.println("<tr><td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("Use the contact information below to report any problems you encounter or to make suggestions or comments.<br>");
               out.println("We will make our best effort to respond as quickly as possible.  We welcome all comments & suggestions.<br>");
               out.println("<br><br><b>Note:</b>  Please report any error messages you receive.  Include the error message if possible.");
            out.println("</font></td></tr></table>");
      out.println("<font size=\"3\">");
      out.println("<p align=\"center\"><font size=\"4\"><b>Contact ForeTees:</b></font><br><br>");
                      out.println("Support: <a href=\"mailto:prosupport@foretees.com\">prosupport@foretees.com</a><br><br>");
                      out.println("Or Call:  <b>651.765.6006</b><br><br>");
                      out.println("Sales: <a href=\"mailto:sales@foretees.com\">sales@foretees.com</a></p>");
      out.println("</font>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
      out.println("</td>");
   out.println("</tr>");
   out.println("</table>");
   out.println("</font>");
   out.println("</body>");
   out.println("</html>");
   out.close();
 }
}
