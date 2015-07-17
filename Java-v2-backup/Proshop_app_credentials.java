/***************************************************************************************     
 *   Proshop_app_credentials:  This servlet will provide a quick way for proshop users
 *                             to assist members in getting the app setup.
 *
 *
 *   called by:  proshop menu and self
 *
 *   created: 5/14/2015   Bob P.
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.client.SystemLingo;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;
import com.foretees.common.alphaTable;
import com.foretees.common.Connect;
import com.foretees.common.mobileAPI;

public class Proshop_app_credentials extends HttpServlet {

                               
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 //****************************************************
 // Process the call from the menu 
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     
     doPost(req, resp);
 }

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }    
   
   Connection con = Connect.getCon(req);                     // get DB connection
   
   if (con == null) {
       
       out.println(SystemUtils.HeadTitle("DB Connection Error"));
       out.println("<BODY><CENTER><BR>");
       out.println("<BR><BR><H3>Database Connection Error</H3>");
       out.println("<BR><BR>Unable to connect to the Database.");
       out.println("<BR>Please try again later.");
       out.println("<BR><BR>If problem persists, contact customer support.");
       out.println("<BR><BR>");
       out.println("<a href=\"javascript:history.back(1)\">Return</a>");
       out.println("</CENTER></BODY></HTML>");
       out.close();
       return;
   }
   
    // Check Feature Access Rights for current proshop user
   /*
    if (!SystemUtils.verifyProAccess(req, "TOOLS_HDCP", con, out)) {
        SystemUtils.restrictProshop("TOOLS_HDCP", out);
        return;
    }
    *       // allow all proshop users for now - possibly add a new setting later??
    */
       
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   int sess_activity_id = (Integer)session.getAttribute("activity_id");   // get Activity indicator (golf=0)

   String club = (String)session.getAttribute("club");        // get club name
   
   //
   //  See if we are in the timeless tees mode
   //
   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;
   
   // setup our custom sytem text veriables
   SystemLingo sysLingo = new SystemLingo();
   sysLingo.setLingo(IS_TLT);
   
   
   // MAKE SURE THIS CLUB HAS A THE APP ENABLED
   boolean clubCentral = mobileAPI.isMobileAppEnabledForClub(club, 1, con);
   boolean clubCentralStaging = false;
   if (clubCentral) clubCentralStaging = mobileAPI.isMobileAppStagingForClub(club, 1, con);  // ClubCentral app in staging mode for this club?
   
    
    // MAKE SURE CLUB IS SETUP FOR THE APP
    if (!clubCentral) {                // if club does not use the app 

        out.println("<br><br><p align=center><b><i>The ForeTees ClubCentral App is not currently available for your club.</i><br><br>"
                  + "Please contact <a href=\"mailto:sales@foretees.com\">sales@foretees.com</a> if you would like information on the App.</b></p>");
        return;
    }

    
   
   String user_id = (req.getParameter("user_id") == null) ? "" : req.getParameter("user_id");  // get username if passed
   
   if (req.getParameter("updatemem") != null) {
    
       updateMem(club, user_id, req, out, con);    //  user submitted an update - process the update
       return;
   }
   
   if (req.getParameter("memdata") != null) {        // if iframe loaded - display member data
       
       memDataTable(club, user_id, false, req, out, con);    
       return;
   }
   
   
   //
   //   output the html page
   //
   out.println(SystemUtils.HeadTitle2("Proshop App Credentials"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<br><h2 align=center>Member App Management</h2>");
    
   out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<p>Use this tool to assist your members in getting logged into the ClubCentral App.</p>");
      out.println("</font>");
   out.println("</td></tr></table>");
   
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><BR>");
   
   if (clubCentralStaging) {           // club in Staging mode for app

       out.println("<b>NOTE:</b> &nbsp;The App is currently in Staging Mode. &nbsp;Only use this for members or staff that you would like to assist in testing the App.</font><BR><BR>");
   }
   
   out.println("Select the member you wish to setup for the App, then select the correct option below.</font><BR><BR>");
   

   //  Output the member list form
   
   listMems(user_id, out, con);
   

   // insert an iframe to hold the member data table so it can run independently
   out.println("<iframe name=\"memdataiframe\" id=\"memdataiframe\" src=\"Proshop_app_credentials?memdata\" width=\"90%\" scrolling=no frameborder=no onload='javascript:resizeIframe(this);'></iframe>");

    
   out.println("</font>");
   out.println("<font size=\"2\"><BR>");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   out.println("</center>");
   out.println("</body>");
   out.println("</html>");
   out.close();

 }

 
 private void listMems(String user_id, PrintWriter out, Connection con) {
    
    // include the dynamic search box scripts
    //out.println("<script type=\"text/javascript\" src=\"/" + rev + "/dyn-search.js\"></script>");
     Utilities.proSlotScripts(out);

    
    out.println("<table border=0 align=center bgcolor=#F5F5DC width=\"650\">");
    out.println("<form method=\"POST\" name=playerform onsubmit=\"false\">");        
    out.println("<tr bgcolor=\"#336633\"><td align=center><b><font color=white>&nbsp;Setup Member for ClubCentral App</font></b></td></tr>");
    out.println("<tr><td align=\"center\">");
    
   out.println("<font size=\"1\" face=\"Arial, Helvetica, Sans-serif\"><BR>");
   
   out.println("<input type=text name=DYN_search onkeyup=\"DYN_triggerChange()\" onkeypress=\"DYN_moveOnEnterKey(event); return DYN_disableEnterKey(event)\" onclick=\"this.select()\" value=\"Type Last Name\">"); // return DYN_disableEnterKey(event)
   
   out.println("&nbsp;&nbsp;(Name Search)<BR>Name (Member Number)</font><BR>");
    
    out.println("<input type=hidden name=user_id value=''>");   // username of member selected
    out.println("<select size=15 name=\"bname\" onclick=\"selectMem(this.options[this.selectedIndex].value)\" onkeypress=\"DYN_moveOnEnterKey(event); return false\" style=\"cursor:hand\" style=\"width:220px\">");

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    String sql = "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, memNum, username "
                + "FROM member2b "
                + "WHERE inact = 0 AND billable = 1 "
                + "ORDER BY last_only, memNum, name_first, name_mi";
    
    String last = "";
    String first = "";
    String mid = "";
    String mnum = ""; 
    String username = "";
    String dname = "";

    try {
        
        pstmt = con.prepareStatement(sql);
        pstmt.clearParameters();
        rs = pstmt.executeQuery();
        
        while (rs.next()) {
            
            last = rs.getString("name_last");          // full last name (with suffix, if appended)
            first = rs.getString("name_first");
            mid = rs.getString("name_mi");
            mnum = rs.getString("memNum");
            username = rs.getString("username");

            if (mid.equals("")) {
                dname = last + ", " + first + " (" +mnum+ ")";
            } else {
                dname = last + ", " + first + " " + mid + " (" +mnum+ ")";
            }

            
            out.print("<option value=\"" + username + "\"");
            
            if (user_id.equals(username)) out.print(" selected");
            
            out.println(">" + dname + "</option>");
        }
        pstmt.close();

    } catch (Exception exc) {
        
        // handle error
    
    } finally {
    
        try {rs.close();} catch (Exception ignore){}
        try {pstmt.close();} catch (Exception ignore){}
    }
    
    out.println("</select>");
    out.println("</td></tr></form>"); 
    
    out.println("<tr><td align=\"center\">");
    out.println("<a href=\"Proshop_app_credentials\">Refresh Name List</a>");      
    out.println("</td></tr>"); 
    out.println("</table>");  
    
    out.println("<script type=\"text/javascript\">");
    out.println("function selectMem(user) {");
    out.println(" var fr = document.getElementById('memdataiframe');");          // get the iframe
    out.println(" frDoc = fr.contentDocument || fr.contentWindow.document;");    // get the iframe doc
    out.println(" f = frDoc.getElementById('memdataform');");                    // get the form within the iframe
    out.println(" f.user_id.value = user;");                                     // set the username of member selected
    out.println(" f.submit();");                                                 // kick the frame content
    out.println("}");
    out.println("function resizeIframe(obj){");
    out.println("obj.style.height = obj.contentWindow.document.body.scrollHeight + 'px';");
    out.println("obj.style.width = obj.contentWindow.document.body.scrollWidth + 'px';");
    out.println("}");
    out.println("</script>");
             
 }    // end of listMems
 
 
 private void memDataTable(String club, String user_id, boolean do_generate, HttpServletRequest req, PrintWriter out, Connection con) {
    
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
   boolean clubCentralStaging = mobileAPI.isMobileAppStagingForClub(club, 1, con);    // ClubCentral app in staging mode for this club?
   
    
    String sql = "SELECT id, name_last, name_first, name_mi, msub_type "
                + "FROM member2b "
                + "WHERE username = ?";
    
    
    String last = "";
    String first = "";
    String mid = "";
    String dname = "";
    String msub = "";
    int mem_id = 0;

    if (!user_id.equals("")) {
        
        try {

            pstmt = con.prepareStatement(sql);
            pstmt.clearParameters();
            pstmt.setString(1, user_id);
            rs = pstmt.executeQuery();

            while (rs.next()) {

                last = rs.getString("name_last");          // full last name (with suffix, if appended)
                first = rs.getString("name_first");
                mid = rs.getString("name_mi");
                msub = rs.getString("msub_type");
                mem_id = rs.getInt("id");  
            }
            pstmt.close();

        } catch (Exception exc) {

            // handle error

        } finally {

            try {rs.close();} catch (Exception ignore){}
            try {pstmt.close();} catch (Exception ignore){}
        }

        //  create a name field for display purposes
        if (mid.equals("")) {
            dname = last + ", " + first;
        } else {
            dname = last + ", " + first + " " + mid;
        }
        
    } else {
        
        dname = "<i>Select Name Above</i>";
    }
    
    
    // Set up form to trigger this method by clicking the name-list on the main config page
    //out.println("<form action=\"Proshop_app_credentials\" method=\"POST\" name=\"memdataform\" onsubmit=\"false\">");
    out.println("<form action=\"Proshop_app_credentials\" method=\"POST\" name=\"memdataform\" id=\"memdataform\">");
    out.println("<input type=\"hidden\" name=\"user_id\" value=\"\">");
    out.println("<input type=\"hidden\" name=\"memdata\" value=\"yes\">");
    out.println("</form>");


    out.println("<table border=0 align=center bgcolor=#F5F5DC width=\"650\">");
    out.println("<form method=post name=frmHndcpData>");        
    out.println("<input type=hidden name=user_id value=\"" +user_id+ "\">");   // username of member selected
    out.println("<tr><td align=center><HR>");
    out.println("<strong>" +dname+ "</strong><BR><BR>");

    
    if (!user_id.equals("")) {
        
        if (clubCentralStaging && do_generate == false) {

            out.println("Current Status of Member:&nbsp;&nbsp;");

            if (msub.equals("App Tester")) {

                out.println("Currently setup to Test the App<BR><BR>");
                
                do_generate = true;            

            } else {

                out.println("Not setup to Test the App<BR><BR>");
                out.println("<input type=submit name=updatemem value='Allow Member to Test the App' style=\"text-decoration:underline;width:180px\"><BR><BR>");
            }

        } else {
            
            do_generate = true;
        }
            
        if (do_generate) {
            
            int club_id = Utilities.getClubId(club, con);
            
            String authUser = "";
            String authPass = "";

            if (req.getParameter("generateCode") != null) {

                String[] mobileAppCredentails = mobileAPI.generateDeviceCode(user_id, club, con);
                authUser = mobileAppCredentails[0];
                authPass = mobileAppCredentails[1];
                Utilities.logError("mobileAppCredentails: created for " + user_id + " at " + club + " - user:" + authUser + ", pass:" + authPass);

            } else {

                try {

                    // find an unused auth_lookup_code that's less than an hour old
                    pstmt = con.prepareStatement("SELECT * FROM v5.mobile_auth WHERE member_id = ? AND club_id = ? AND active = 0 AND auth_username IS NOT NULL AND auth_password IS NOT NULL"); 
                    pstmt.clearParameters();
                    pstmt.setInt(1, mem_id);
                    pstmt.setInt(2, club_id);
                    rs = pstmt.executeQuery();

                    // if we found a record then there is an unused device authorization still available for use so display it
                    if ( rs.next() ) {

                        authUser = rs.getString("auth_username");
                        authPass = rs.getString("auth_password");

                    }

                } catch (Exception ignore) { // any error is a fail

                    out.println("<P style=\"padding-left:20px;\">ERROR: " + ignore.toString() + "</P>");

                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt.close(); }
                    catch (Exception ignore) {}
                }
            }
            
            if (!authPass.isEmpty()) {

                out.println("</td></tr><tr><td align=left>");
                
                out.print("<p style=\"padding-left:200px;\">"
                        + "App Username: &nbsp;&nbsp;&nbsp;<strong>" + authUser + "</strong><br>"
                        + "App Password: &nbsp;&nbsp;&nbsp;<strong>" + authPass.replaceAll("(.{4})(?!$)", "$1-")
                        + "</strong></p>");

                out.println("</td></tr><tr><td align=center>");
            }

            out.println("<input type=\"hidden\" name=\"generateCode\" value=\"yes\">");
            out.print("<BR><input type=\"submit\" value=\"Generate " + ((authPass.isEmpty()) ? "" : "New ") + "Password\" name=\"updatemem\">");
            out.println("<BR><BR>");
        }
    }
    
    out.println("<BR></form></td></tr></table>");
    
 }      // end of memDataTable
 
 
 
 private void updateMem(String club, String user_id, HttpServletRequest req, PrintWriter out, Connection con) {
     

   //
   //  Use the username and parms passed to update the member record
   //
   boolean do_generate = false;
   
   if (user_id != null && !user_id.equals("")) {      // if username provided
       
        String call_type = req.getParameter("updatemem");  // get name of update button

        if (call_type.startsWith("Allow Member")) {       // if request to allow tester

            PreparedStatement pstmt = null;

            String sql = "UPDATE member2b "
                        + "SET msub_type = 'App Tester' "
                        + "WHERE username = ?";

            try {

                pstmt = con.prepareStatement(sql);
                pstmt.clearParameters();
                pstmt.setString(1, user_id);
                pstmt.executeUpdate();

                pstmt.close();
                
                do_generate = true;       // generate credentials next

            } catch (Exception exc) {

                // handle error
                out.println("<script type=\"text/javascript\">");
                out.println("alert('There was a problem with the update.\\n\\nSelect OK to go back and try again.');");
                out.println("</script>");

            } finally {

                try {pstmt.close();} catch (Exception ignore){}
            }

            out.println("<script type=\"text/javascript\">");
            out.println("alert('Member record updated.\\n\\nSelect OK to return.');");
            out.println("</script>");   
       

        } else {        // else call is to generare credentials for this user

            // nothing to do for Generate Code - will be done in memDataTable

            do_generate = true;       // generate credentials next (in case staging)

            out.println("<script type=\"text/javascript\">");
            out.println("alert('Member credentials created.\\n\\nSelect OK to return.');");
            out.println("</script>");   
        }

    
   } else {
       
       out.println("<script type=\"text/javascript\">");
       out.println("alert('There was a problem with the update.\\n\\nSelect OK to go back and try again.');");
       out.println("</script>");
   }
   
   memDataTable(club, user_id, do_generate, req, out, con);  // rebuild the frame (member data table)  
     
 }    // end of updateMem
    
     
 
 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>You must enter the last name, or some portion of it.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }

}
