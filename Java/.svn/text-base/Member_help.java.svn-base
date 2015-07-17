/***************************************************************************************
 *   Member_help:  This servlet will process the 'Help' request from Member_maintop.
 *
 *
 *   called by:  Member_maintop
 *
 *   created: 4/25/2006   Bob P.
 *
 *   last updated:      ******* keep this accurate *******
 *
 *      10/28/09  Allow for Activity instructions.
 *       2/29/08  Add check for member instructions pdf file.  If there add link for it.
 *      10/03/07  Troon CC - Custom message w/ golfshop phone number (Case #1280)
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Member_help extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 // Process the initial call from Member_maintop
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                 // init DB objects
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   //
   //  get the club name
   //
   String user = (String)session.getAttribute("user");   // get username
   String club = (String)session.getAttribute("club");      // get club name
   String caller = (String)session.getAttribute("caller");   // get caller's name
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
     

   boolean failed = false;

   File f;
   FileReader fr;
   BufferedReader br;
   String tmp = "";
   String path = "";
   String fname = club;
   
   
   //
   //  Determine the name of the file - use club name for golf, club name plus activity id for others
   //
   if (sess_activity_id > 0) {
      
      fname = club + sess_activity_id;
   }


   //
   //  Output the help page
   //
   out.println(SystemUtils.HeadTitle("Member Help Page"));
   out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
   out.println("<font size=\"2\" face=\"Arial, Times New Roman, Helvetica\">");
   out.println("<BR><BR><H3>ForeTees Assistance</H3>");
   
   //
   // check for member instrcutions pdf file
   //
   try {
   
      // path = req.getRealPath("");                       // deprecated - use getServletContext
      path = getServletContext().getRealPath("");          //   get the path to our servlets
      tmp = "/instructions/" + fname + ".pdf";             //   /rev/instructions/club.pdf (is it there?)
      f = new File(path + tmp);
      fr = new FileReader(f);
      
   } catch (Exception e2) {
      
      failed = true;              // file not found
   }
   
   if (failed == false) {         // if pdf exists
       
      out.println("<BR><BR><a href=\"/" +rev+ "/instructions/" +fname+ ".pdf\">Click Here For Member Instructions</a>");
   }
       
   
   if (club.equals( "trooncc" )) {
       
      out.println("<BR><BR>Please contact the golfshop at 480-585-0540, or send an email to <a href=\"mailto:golfshop@trooncc.com\">golfshop@trooncc.com</a>.");
      
   } else {
       
      out.println("<BR><BR>Please contact your club staff for assistance with ForeTees.");
   }
   
   if (club.equals( "foresthighlands" )) {

      out.println("<BR><BR>Canyon Golf Shop:  928.525.9000 or 888.470.4607");
      out.println("<BR><BR>Meadow Golf Shop:  928.525.5250 or 866.470.4608");
   }

   out.println("<BR><BR>Thank you!");
   out.println("<BR><BR><input type=\"button\" value=\"OK\" onClick='self.close();'>");
   out.println("</font></CENTER></BODY></HTML>");
   out.close();

 }

}
