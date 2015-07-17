/***************************************************************************************
 *   Proshop_content_videos: This servlet will display instructional videos for custom email content.
 *
 *
 *   Called by:       Proshop_content
 *
 *
 *   Created:         11/28/2012 by BP
 *
 *
 *   Last Updated:  
 *
 *
 *                  
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
//import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Connect;


public class Proshop_content_videos extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)

    static String DINING_USER = ProcessConstants.DINING_USER;               // Dining username for Admin user from Dining System

    
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;

    Connection con = Connect.getCon(req);                      // get DB connection

    if (con == null) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>");
        out.println("<a href=\"Proshop_announce\">Home</a>");
        out.println("</CENTER></BODY></HTML>");
        return;
    }

    String user = (String)session.getAttribute("user");
    String club = (String)session.getAttribute("club");

    if (!user.equals(DINING_USER)) {
         
       // Check Feature Access Rights for current proshop user
       if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_MANAGECONTENT", con, out)) {
           SystemUtils.restrictProshop("SYSCONFIG_MANAGECONTENT", out);
       }
    }

    int sess_activity_id = (Integer)session.getAttribute("activity_id");

    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);

    int id = 0;

    try { id = Integer.parseInt(req.getParameter("id")); }
    catch (Exception ignore) {}

    int organization_id = Utilities.getOrganizationId(con);


    String bgcolor = "";
    String location = "";

    out.println(SystemUtils.HeadTitle("ForeTees - Custom Email Content Videos"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    
    if (!user.equals(DINING_USER)) {
         
       SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    }

    
    //
    //   Add the instructional video(s)
    //
    out.println("<p align=center><br><strong>ForeTees Instructional Videos</strong></p>");
    out.println("<p align=center><br><strong>NOTE:</strong> We suggest that you view the videos in <strong>full screen mode</strong>. �Click the full screen icon <img src=\"/" +rev+ "/images/full_screen.png\" width=\"28\" height=\"32\"> located near the bottom right corner of the video player.");
    
    
    out.println("<p align=center><iframe src=\"http://player.vimeo.com/video/54498241\" width=\"500\" height=\"281\" frameborder=\"0\" webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>");
    out.println("<br><br></p>");
    

    out.println("<center>");
    out.println("<form><input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Return \" onClick='self.close()' alt=\"Close\">");
    out.println("</form></center>");

    out.println("</body>");
    out.println("</html>");
    
    out.close();

 } // end of doGet routine


 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(String msg, PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>" + msg + "<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }


} // end servlet public class
