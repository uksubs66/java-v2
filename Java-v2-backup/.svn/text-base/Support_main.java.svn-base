/***************************************************************************************     
 *   Support_main:  This servlet will provide the main menu for support and allow user to switch clubs.
 *
 *
 *   called by:  support_main.htm
 *
 *   created:  1/12/2013   BP
 *
 *   last updated:
 *
 *       1/13/14 Added support for the 'supportpro' limited user on the support side.
 *      12/04/13 Added link to Utilities Menu, which currently houses the AGC NGC member push functions.
 *       1/16/13 Changed club drop-down list to have the clubname first, and sort by clubname. Copied in text & link color scheme from Support_main.html.
 *       1/16/13 BP Removed (commented out) some unused menu items.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.getClub;
import com.foretees.common.mTypeArrays;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;


public class Support_main extends HttpServlet {
 
       
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 String support = "support";              // valid username (dev)
 String supportpro = "supportpro";        // valid username (prosupport)
 String sales = "sales";
 String supportwill = "supportwill";



 //*********************************************************************************
 // Process the request from menu 
 //*********************************************************************************

 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Connection con = null;                  // init DB objects
     
   HttpSession session = null; 
   String itype = "";                      // invoice type

   
   // Make sure user didn't enter illegally.........

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


   // Load the JDBC Driver and connect to DB.........

   String club = (String)session.getAttribute("club");   // get club name
   
   
   //
   //  See if user requested that we switch the club to process
   //
   if (req.getParameter("clubswitch") != null && req.getParameter("clubswitch").equals("1") && req.getParameter("club") != null) {

       //
       //  Request is to switch clubs - switch the db (TPC or Demo sites)
       //
       String newClub = req.getParameter("club");

       session.setAttribute("club", newClub);
       
       out.println("<HTML><HEAD><Title>Switching Sites</Title>");
       out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Support_main?home=yes\">");
       out.println("</HEAD>");
       out.println("<BODY><CENTER><BR>");
       out.println("<BR><H2>Switching Sites</H2><BR>");
       out.println("<a href=\"/" +rev+ "/servlet/Support_main?home=yes\">Continue</a><br>");
       out.println("</CENTER></BODY></HTML>");
       out.close();
       return;
   }
   
      
   //  Process Home page
   
    out.println("<HTML><HEAD><TITLE>Support Main Menu</TITLE></HEAD>");
    out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#FFFFFF\" link=\"#000000\" vlink=\"#000000\" alink=\"#FF0000\">");
    out.println("<span style=\"color:black;\">");
    out.println("<CENTER><H3>Support Main Menu</H3>");
    out.println("Current Club = " + club + "");

    out.println("<p>");    //  add drop down list of all clubs - for switch

    out.println("<form action=\"/" +rev+ "/servlet/Support_main\" method=\"get\" name=\"cform\">");
    out.println("<input type=\"hidden\" name=\"clubswitch\" value=\"1\">");

        out.println("To switch sites, select the desired club name below.<br>");
        out.println("<br><b>Club:</b>&nbsp;&nbsp;</span>");

        out.println("<select size=\"1\" name=\"club\" onChange=\"document.cform.submit()\">");

        String clubname = "";
        String fullname = "";
        int inactive = 0;

        Connection con2 = null;

        try {
            con2 = dbConn.Connect(rev);           // get connection to the Vx db

            //
            //  Get the club names for each club
            //
            //
            //  Get the club names for each TPC club
            //
            PreparedStatement pstmt = con2.prepareStatement("SELECT clubname, fullname, inactive FROM clubs ORDER BY clubname");

            pstmt.clearParameters();
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                clubname = rs.getString("clubname");             // get the club's site name             
                fullname = rs.getString("fullname");             // get the club's full name
                inactive = rs.getInt("inactive");                // get inactive flag

                out.println("<option " + (clubname.equals(club) ? "selected " : "") + "value=\"" + clubname + "\">" + clubname + " - " + fullname + (inactive > 0 ? " (INACT)" : "") + "</option>"); 
            }  
            pstmt.close();

        }
        catch (Exception e) {

        } finally {

            try { con2.close(); }
            catch (SQLException ignored) {}
        }        

    out.println("</select><br /></p></form>");

    
    out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" width=\"450\">");
    out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
    
    if (user.equals(support) || user.equalsIgnoreCase(supportwill)) {
        
        out.println("<tr bgcolor=\"#336633\">");
            out.println("<td>");
                    out.println("<p align=\"center\">");
                    out.println("<b>Tasks to Add a New Club</b>");
                    out.println("</p>");
            out.println("</td>");
        out.println("</tr>");
        out.println("<tr>");
            out.println("<td>");
                    out.println("<p align=\"center\">");
                    out.println("<a href=\"/v5/support_addclubs.htm\" target=\"_top\">Add a Club to Clubs Database</a>");
                    out.println("</p>");
            out.println("</td>");
        out.println("</tr>");
        out.println("<tr>");
            out.println("<td>");
                    out.println("<p align=\"center\">");
                    out.println("<a href=\"/v5/support_init.htm\" target=\"_top\">Init System for this Club (Support_init)</a>");
                    out.println("</p>");
            out.println("</td>");
        out.println("</tr>");
        out.println("<tr>");
            out.println("<td>");
                    out.println("<p align=\"center\">");
                    out.println("<a href=\"/v5/servlet/Support_cluboptions\" target=\"_top\">Set/Change/View Club Options </a>");
                    out.println("</p>");
            out.println("</td>");
        out.println("</tr>");
        out.println("<tr>");
            out.println("<td>");
                    out.println("<p align=\"center\">");
                    out.println("<a href=\"/v5/support_port.htm\" target=\"_top\">Import Roster File (Support_port)</a>");
                    out.println("</p>");
            out.println("</td>");
        out.println("</tr>");
        out.println("<tr>");
            out.println("<td>");
                    out.println("<p align=\"center\">");
                    out.println("<a href=\"/v5/servlet/Support_port_dining\" target=\"_top\">Port FT Roster to Dining (Support_port_dining)</a>");
                    out.println("</p>");
            out.println("</td>");
        out.println("</tr>");
        out.println("</font>");
        out.println("</table>");

        if (!user.equalsIgnoreCase(supportwill)) {
            out.println("<br>");
            out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" width=\"450\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<tr bgcolor=\"#336633\">");
                out.println("<td>");
                        out.println("<p align=\"center\">");
                        out.println("<b>Server Maintenance Tasks</b>");
                        out.println("</p>");
                out.println("</td>");
            out.println("</tr>");
            out.println("<tr>");
                out.println("<td>");
                        out.println("<p align=\"center\">");
                        out.println("<a href=\"/v5/servlet/Support_serverid\" target=\"_top\">View/Change This Server ID</a>");
                        out.println("</p>");
                out.println("</td>");
            out.println("</tr>");
            out.println("</font>");
            out.println("</table>");
        }

        out.println("<br>");
        out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" width=\"450\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<tr bgcolor=\"#336633\">");
        out.println("    <td>");
        out.println("            <p align=\"center\">");
        out.println("            <b>Miscellaneous Tasks</b>");
        out.println("            </p>");
        out.println("    </td>");
        out.println("</tr>");
        /*
        out.println("<tr>");
        out.println("    <td>");
        out.println("            <p align=\"center\">");
        out.println("            <a href=\"/v5/servlet/Support_cluboptions?switchClubs\" target=\"_top\">Switch Clubs</a>");
        out.println("            </p>");
        out.println("    </td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("    <td>");
        out.println("            <p align=\"center\">");
        out.println("            <a href=\"/v5/support_updatedb.htm\" target=\"_top\">Enter Custom Database Update Command</a>");
        out.println("            </p>");
        out.println("    </td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("    <td>");
        out.println("            <p align=\"center\">");
        out.println("            <a href=\"/v5/servlet/Support_setCaddyAvail\" target=\"_top\">Set All Caddies Available For This Club (demo/notify only)</a>");
        out.println("            </p>");
        out.println("    </td>");
        out.println("</tr>");
        * 
        */
        if (!user.equalsIgnoreCase("supportwill")) {
            out.println("<tr>");
            out.println("    <td>");
            out.println("            <p align=\"center\">");
            out.println("            <a href=\"/v5/servlet/Support_displayClubs\" target=\"_top\">Display Clubs Table</a>");
            out.println("            </p>");
            out.println("    </td>");
            out.println("</tr>");
        }
        out.println("<tr>");
        out.println("    <td>");
        out.println("            <p align=\"center\">");
        out.println("            <a href=\"/v5/servlet/Support_cleanup\" target=\"_top\">Cleanup Tee Sheets (Delete Dup Tee Times)</a>");
        out.println("            </p>");
        out.println("    </td>");
        out.println("</tr>");
        
        if (!user.equalsIgnoreCase("supportwill")) {
            out.println("<tr>");
            out.println("    <td>");
            out.println("            <p align=\"center\">");
            out.println("            <a href=\"/v5/servlet/Support_billing\" target=\"_top\">Get Billing Info for Club</a>");
            out.println("            </p>");
            out.println("    </td>");
            out.println("</tr>");
        }
        out.println("<tr>");
        out.println("    <td>");
        out.println("            <p align=\"center\">");
        out.println("            <a href=\"/v5/servlet/Support_courses?default\" target=\"_top\">Default Course Configuration</a>");
        out.println("            </p>");
        out.println("    </td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("    <td>");
        out.println("            <p align=\"center\">");
        out.println("            <a href=\"/v5/servlet/Support_utilities\" target=\"_top\">Utilities Menu</a>");
        out.println("            </p>");
        out.println("    </td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("    <td>");
        out.println("            <p align=\"center\">");
        out.println("            <a href=\"/v5/support_main2.htm\" target=\"_top\">Support Functions (Menu #2)</a>");
        out.println("            </p>");
        out.println("    </td>");
        out.println("</tr>");
        
    } else if (user.equals(supportpro)) {
        
        out.println("<tr bgcolor=\"#336633\">");
        out.println("    <td>");
        out.println("        <p align=\"center\">");
        out.println("        <b>Support Functions</b>");
        out.println("        </p>");
        out.println("    </td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("    <td>");
        out.println("        <p align=\"center\">");
        out.println("        <a href=\"/v5/servlet/Support_errorlog?sess=yes\" target=\"_top\">Display Session Log</a>");
        out.println("        </p>");
        out.println("    </td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("    <td>");
        out.println("        <p align=\"center\">");
        out.println("        <a href=\"/v5/servlet/Support_sync?support=yes\" target=\"_top\">Run Roster Sync For This Club</a>");
        out.println("        </p>");
        out.println("    </td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("    <td>");
        out.println("        <p align=\"center\">");
        out.println("        <a href=\"/v5/servlet/Support_sync?rtype=rosters\" target=\"_top\">View Roster Sync Roster Files</a>");
        out.println("        </p>");
        out.println("    </td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("    <td>");
        out.println("        <p align=\"center\">");
        out.println("        <a href=\"/v5/servlet/Support_sync?rtype=errorlogs\" target=\"_top\">View Roster Sync Error Logs</a>");
        out.println("        </p>");
        out.println("    </td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("    <td>");
        out.println("        <p align=\"center\">");
        out.println("        <a href=\"/v5/servlet/Support_sync?rtype=activity_ids\" target=\"_top\">View Activity Ids</a>");
        out.println("        </p>");
        out.println("    </td>");
        out.println("</tr>");
    }
    
    out.println("</font>");
    out.println("</table>");
    
        
    
    out.println("<br />");
    out.println("<p class=\"general_button\"><a href=\"/" +rev+ "/servlet/Logout\">Logout</a></p>");
    out.println("</body>");
    out.println("</html>");
    out.close();
   
   
 }        // end of doGet
   

 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<body class=\"serifFont\">");
   out.println("<div id=\"wrapper\">");
   out.println("<img src=\"/" +rev+ "/images/foretees.gif\" /><br />");
   out.println("<hr class=\"menu\">");
   out.println("<br /><h2>Access Error</h2><br />");
   out.println("<br /><br />Sorry, you must login before attempting to access these features.<br />");
   out.println("<br /><br />Please <a href=\"Logout\">login</a>");
   out.println("</div>    <!-- wrapper  -->");
   out.println("</body></html>");

 }
 
 
}
