/***************************************************************************************
 *   Admin_maintop:  This servlet will display the Admins's navigation bar (Top of Page).
 *
 *
 *   called by:  admin_main.htm
 *
 *   created:  11/29/2003   JAG
 *
 *
 *   last updated:
 *
 *      3/04/10  Show 'mships' link for ForeTees admin account regardless of whether FlxRez is configured/turned on
 *     10/02/09  Add temporary link to Admin_mships that displays only for clubs with Activities configured
 *     10/27/08  Use logo.gif instead of logo.jpg for new TPC clubs
 *      9/14/08  Removed restrictions so Proshop Users link is available for all clubs
 *      9/10/08  Added Golf Club of Georgia (gcgeorgia) for Limited Access Proshop User testing
 *      9/03/08  Re-Added MIrasol CC and Portage for Limited Access Proshop User testing
 *      8/26/08  Reverted previous change
 *      8/26/08  Added Mirasol CC and Portage for Limited Access Proshop User testing
 *      7/29/08  Added seperate weather link for GC of Georgia (gcgeorgia)
 *      7/14/08  Added 'Proshop Users' tab/button to top menu bar (currently restricted to demo sites)
 *      2/01/07  Add new menu tab for List/Export Roster.
 *      3/03/05  Ver 5 - Change layout, add drop-down menu.
 *      1/24/05  Ver 5 - change club2 to club5.
 *      4/30/04 RDP  Add club logo and move ForeTees logo.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// Foretees imports
import com.foretees.common.getActivity;

public class Admin_maintop extends HttpServlet {

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyAdmin(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   String club = (String)session.getAttribute("club");   // get club name
   String user = (String)session.getAttribute("user");

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Logout\" target=\"_top\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

    // Define parms
   int hotel = 0;             // hotel support = no

   //
   // Get existing hotel parm if it exists
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT hotel FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         hotel = rs.getInt(1);
      }
      stmt.close();
   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitleAdmin("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Logout\" target=\"_top\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;

   }

   //
   //  Get this year
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int year = cal.get(Calendar.YEAR);            // get the year

   //
   //  Build the HTML page (main menu)
   //
   out.println("<html><head>");

   out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
   out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
   out.println("<title>ForeTees Admin Main Title Page</title>");

   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("  /*");
   out.println("   * Pre-load the specified image.  This loads the image as the page");
   out.println("   * loads, so that when it is used later (such as for a mouseover effect),");
   out.println("   * the image has already been loaded.");
   out.println("   */");
     out.println("function preloadImage(imageName) {");
       out.println("var image = new Image();");
       out.println("image.src = imageName;");
     out.println("}");

     out.println("// Preload images for rollover effects.");
     out.println("preloadImage(\"/" +rev+ "/images/AdminHome-over\");");
     out.println("preloadImage(\"/" +rev+ "/images/AdminMembers-over.png\");");
     if (hotel != 0) {
        out.println("preloadImage(\"/" +rev+ "/images/Hotel_Users-over.png\");");
     }
     out.println("preloadImage(\"/" +rev+ "/images/Admin_ProUsers-over.pmg\");");
     out.println("preloadImage(\"/" +rev+ "/images/AdminSettings1-over.png\");");
     out.println("preloadImage(\"/" +rev+ "/images/AdminLogout-over.png\");");
     out.println("preloadImage(\"/" +rev+ "/images/AdminHelp-over.png\");");
     out.println("preloadImage(\"/" +rev+ "/images/AdminListExport-over.png\");");
     out.println("// -->");
   out.println("</script>");
   out.println("</head>");

   out.println("<body leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" topmargin=\"0\">");
   out.println("<center>");
   out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");
     
   out.println("<tr><td valign=\"middle\" width=\"12%\" align=\"left\" rowspan=\"2\">");
   if (club.equals("tpcpotomac") || club.equals("tpcsouthwind") || club.equals("tpcsugarloaf") || club.equals("tpcwakefieldplantation") || 
       club.equals("tpcboston") || club.equals("tpcjasnapolana") || club.equals("tpcriverhighlands") || club.equals("tpcriversbend") ||
       club.equals("tpccraigranch") || club.equals("tpcsummerlin") || club.equals("tpcsanfranciscobay")) {
       out.println("<p>&nbsp;&nbsp;&nbsp;<img src=\"/" +club+ "/images/logo.gif\" border=0></p>");
   } else {
       out.println("<p>&nbsp;&nbsp;&nbsp;<img src=\"/" +club+ "/images/logo.jpg\" border=0></p>");
   }
     out.println("</td>");

     out.println("<td valign=\"middle\" width=\"12%\" align=\"left\" rowspan=\"2\">");
       if (club.equals("gcgeorgia")) {
           out.print("<a href=\"http://168.29.150.40/aemn/cgi-bin/AEMN.pl?site=GAAP&report=c\" target=\"_blank\">");
       } else {
           out.print("<a href=\"http://wwwa.accuweather.com/adcbin/public/golf_index.asp?partner=accuweather\" target=\"_blank\">");
       }
      out.println("<img src=\"/" +rev+ "/images/weather.gif\" border=0></a>");
     out.println("</td>");

     out.println("<td width=\"52%\" align=\"center\">");
        out.println("<p><font size=\"5\">System Administration</font>");

        //// TEMP - REMOVE LATER!!!! ////
        if (getActivity.isConfigured(con) || user.equals("admin4tea")) {
            out.println("&nbsp;&nbsp;<a href=\"/" + rev + "/servlet/Admin_mships\" target=\"bot\">mships</a>");
        }
        //// TEMP - REMOVE LATER!!!! ////

        out.println("</p>");
      out.println("<!-- Begin code to display images horizontally. -->");
      out.println("<a href=\"/" +rev+ "/servlet/Admin_announce\" target=\"bot\" onMouseOver=\"document.images['AdminHome'].src = '/" +rev+ "/images/AdminHome-over.png'\" onMouseOut=\"document.images['AdminHome'].src = '/" +rev+ "/images/AdminHome.png'\"><img name=\"AdminHome\" src=\"/" +rev+ "/images/AdminHome.png\" hspace=\"0\" border=\"0\" alt=\"Return to Announcement Page\"></a>");
      out.println("<a href=\"/" +rev+ "/servlet/Admin_members\" target=\"bot\" onMouseOver=\"document.images['AdminMembers'].src = '/" +rev+ "/images/AdminMembers-over.png'\" onMouseOut=\"document.images['AdminMembers'].src = '/" +rev+ "/images/AdminMembers.png'\"><img name=\"AdminMembers\" src=\"/" +rev+ "/images/AdminMembers.png\" hspace=\"0\" border=\"0\" alt=\"Maintain Member Database\"></a>");

      if (hotel != 0) {
         out.println("<a href=\"/" +rev+ "/servlet/Admin_hotelusers\" target=\"bot\" onMouseOver=\"document.images['Hotel_Users'].src = '/" +rev+ "/images/Hotel_Users-over.png'\" onMouseOut=\"document.images['Hotel_Users'].src = '/" +rev+ "/images/Hotel_Users.png'\"><img name=\"Hotel_Users\" src=\"/" +rev+ "/images/Hotel_Users.png\" hspace=\"0\" border=\"0\" alt=\"Create and Manage the Hotel User Accounts\"></a>");
      }
      out.println("<a href=\"/" +rev+ "/servlet/Admin_proshopusers\" target=\"bot\" onMouseOver=\"document.images['Proshop_Users'].src = '/" +rev+ "/images/Admin_ProUsers-over.png'\" onMouseOut=\"document.images['Proshop_Users'].src = '/" +rev+ "/images/Admin_ProUsers.png'\"><img name=\"Proshop_Users\" src=\"/" +rev+ "/images/Admin_ProUsers.png\" hspace=\"0\" border=\"0\" alt=\"Create and Manage the Proshop User Accounts\"></a>");
      out.println("<a href=\"/" +rev+ "/servlet/Common_report_members\" target=\"bot\" onMouseOver=\"document.images['AdminListExport'].src = '/" +rev+ "/images/AdminListExport-over.png'\" onMouseOut=\"document.images['AdminListExport'].src = '/" +rev+ "/images/AdminListExport.png'\"><img name=\"AdminListExport\" src=\"/" +rev+ "/images/AdminListExport.png\" hspace=\"0\" border=\"0\" alt=\"List or Export The Member Roster\"></a>");
      out.println("<a href=\"/" +rev+ "/admin_services.htm\" target=\"bot\" onMouseOver=\"document.images['AdminSettings1'].src = '/" +rev+ "/images/AdminSettings1-over.png'\" onMouseOut=\"document.images['AdminSettings1'].src = '/" +rev+ "/images/AdminSettings1.png'\"><img name=\"AdminSettings1\" src=\"/" +rev+ "/images/AdminSettings1.png\" hspace=\"0\" border=\"0\" alt=\"Change Your Password\"></a>");
      out.println("<a href=\"/" +rev+ "/servlet/Logout\" target=\"_top\" onMouseOver=\"document.images['AdminLogout'].src = '/" +rev+ "/images/AdminLogout-over.png'\" onMouseOut=\"document.images['AdminLogout'].src = '/" +rev+ "/images/AdminLogout.png'\"><img name=\"AdminLogout\" src=\"/" +rev+ "/images/AdminLogout.png\" hspace=\"0\" border=\"0\" alt=\"Exit ForeTees\"></a>");
      out.println("<a href=\"/" +rev+ "/admin_help.htm\" target=\"_blank\" onMouseOver=\"document.images['AdminHelp'].src = '/" +rev+ "/images/AdminHelp-over.png'\" onMouseOut=\"document.images['AdminHelp'].src = '/" +rev+ "/images/AdminHelp.png'\"><img name=\"AdminHelp\" src=\"/" +rev+ "/images/AdminHelp.png\" hspace=\"0\" border=\"0\" alt=\"Get Help on Using ForeTees\"></a>");

      out.println("<!-- End code to display images horizontally. -->");
      out.println("</td>");
        
     out.println("<td align=\"center\" width=\"24%\">");
       out.println("<p>");
       out.println("<a href=\"http://www.foretees.com\" target=\"_blank\">");
        out.println("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>");
       out.println("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
       out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
       out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC<br>" +year+ " All rights reserved.");
       out.println("</font></p>");
     out.println("</td>");
   out.println("</tr>");
   out.println("</table>");
   out.println("</center>");
   out.println("</body></html>");

 }  // end of doGet

}
