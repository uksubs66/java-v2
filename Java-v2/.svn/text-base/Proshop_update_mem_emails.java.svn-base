/***************************************************************************************     
 *   Proshop_update_mem_emails:  This servlet will provide a quick way to update the members'
 *                               email addresses, bounce flags, and options.
 *
 *
 *   called by:  proshop menu and self
 *
 *   created: 6/12/2014   Bob P.
 *
 *   last updated:
 *
 * 
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

public class Proshop_update_mem_emails extends HttpServlet {

                               
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
    if (!SystemUtils.verifyProAccess(req, "TOOLS_EMAIL", con, out)) {
        SystemUtils.restrictProshop("TOOLS_EMAIL", out);
        return;
    }

   String club = (String)session.getAttribute("club");
   String user = (String)session.getAttribute("user");               // get username
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   int sess_activity_id = (Integer)session.getAttribute("activity_id");   // get Activity indicator (golf=0)
    
   //
   //  See if we are in the timeless tees mode
   //
   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;
   
   // setup our custom sytem text veriables
   SystemLingo sysLingo = new SystemLingo();
   sysLingo.setLingo(IS_TLT);
   
   
   String user_id = (req.getParameter("user_id") == null) ? "" : req.getParameter("user_id");  // get username if passed
   
   if (req.getParameter("updatemem") != null) {
    
       updateMem(user_id, club, req, out, con);    //  user submitted an update - process the update
       return;
   }
   
   if (req.getParameter("memdata") != null) {        // if iframe loaded - display the handicap data
       
       memDataTable(user_id, club, out, con);    
       return;
   }
   
   
   //
   //   output the html page
   //
   out.println(SystemUtils.HeadTitle2("Proshop Update Member Emails"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<br><h2 align=center>Member Email Management</h2>");
    
   out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<p>Use this tool to update the email information for one or more members.</p>");
   out.println("</font>");
   out.println("</td></tr></table>");
   
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><BR>");
   out.println("Select the member to update, then add or update the email information below.</font><BR><BR>");
   

   //  Output the member list form
   
   listMems(user_id, out, con);
   

   // insert an iframe to hold the member data table so it can run independently
   
   out.println("<iframe name=\"memdataiframe\" id=\"memdataiframe\" src=\"Proshop_update_mem_emails?memdata\" width=\"90%\" height=\"360\" scrolling=no frameborder=no></iframe>");

    
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
    Utilities.proSlotScripts(out);

    out.println("<table border=0 align=center bgcolor=#F5F5DC width=\"650\">");
    out.println("<form method=\"POST\" name=playerform onsubmit=\"false\">");        
    out.println("<tr bgcolor=\"#336633\"><td align=center><b><font color=white>&nbsp;Update Member Email Data</font></b></td></tr>");
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
    out.println("<a href=\"Proshop_update_mem_emails\">Refresh Name List</a>");      
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
    out.println("</script>");
             
 }    // end of listMems
 
 
 private void memDataTable(String user_id, String club, PrintWriter out, Connection con) {
    
    Statement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    String last = "";
    String first = "";
    String mid = "";
    String dname = "";
    String email = "";
    String email2 = "";
    String caller = "";
    int emailOpt = 0;
    int emailOpt2 = 0;
    int clubEmailOpt1 = 0;
    int clubEmailOpt2 = 0;
    int memEmailOpt1 = 0;
    int memEmailOpt2 = 0;
    int email_bounced = 0;
    int email2_bounced = 0;
    int iCal1 = 0;
    int iCal2 = 0;
    int rsync = 0;
    
    
    //
    //  Get Roster Sync indicator and Caller for this club
    //
    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT rsync, seamless_caller FROM club5");
        
        if (rs.next()) {
            
            rsync = rs.getInt(1);
            caller = rs.getString("seamless_caller");
        }
     
    } catch (Exception ignore) {
       
    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }

        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }

    
    
    // if Roster Sync - no email address changes allowed (except for clubs explicity listed here)
    boolean static_emails = (rsync == 1 &&
        !caller.equals("FLEXSCAPE4865") && !club.equals("mesaverdecc") &&
        !club.equals("charlottecc") && !club.equals("dorsetfc") &&
        !club.equals("bellevuecc") && !club.equals("greenacrescountryclub") && 
        !club.equals("hillwoodcc"));
    
    if (caller.equals("FLEXWEBFT")) static_emails = true;
         

    if (!user_id.equals("")) {
        
        String sql = "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, email, email2, emailOpt, emailOpt2, "
                    + "clubEmailOpt1, clubEmailOpt2, memEmailOpt1, memEmailOpt2, email_bounced, email2_bounced, iCal1, iCal2 "
                    + "FROM member2b "
                    + "WHERE username = ?";
    
        try {

            pstmt = con.prepareStatement(sql);
            pstmt.clearParameters();
            pstmt.setString(1, user_id);
            rs = pstmt.executeQuery();

            while (rs.next()) {

                last = rs.getString("name_last");          // full last name (with suffix, if appended)
                first = rs.getString("name_first");
                mid = rs.getString("name_mi");
                email = rs.getString("email");
                email2 = rs.getString("email2");
                emailOpt = rs.getInt("emailOpt");
                emailOpt2 = rs.getInt("emailOpt2");
                clubEmailOpt1 = rs.getInt("clubEmailOpt1");
                clubEmailOpt2 = rs.getInt("clubEmailOpt2");
                memEmailOpt1 = rs.getInt("memEmailOpt1");
                memEmailOpt2 = rs.getInt("memEmailOpt2");
                email_bounced = rs.getInt("email_bounced");
                email2_bounced = rs.getInt("email2_bounced");
                iCal1 = rs.getInt("iCal1");
                iCal2 = rs.getInt("iCal2");
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
    out.println("<form action=\"Proshop_update_mem_emails\" method=\"POST\" name=\"memdataform\" id=\"memdataform\">");
    out.println("<input type=\"hidden\" name=\"user_id\" value=\"\">");
    out.println("<input type=\"hidden\" name=\"memdata\" value=\"yes\">");
    out.println("</form>");
    
    
    out.println("<table border=0 align=center bgcolor=#F5F5DC width=\"650\">");
    out.println("<form method=post name=frmHndcpData>");        
    out.println("<input type=hidden name=user_id value=\"" +user_id+ "\">");   // username of member selected
    out.println("<tr><td align=center><HR>");
    out.println("<strong>" +dname+ "</strong><BR><BR>");
    
    if (static_emails) {      // if club uses roster sync
        
        out.println("<strong>NOTE:</strong> &nbsp;Your club uses Roster Sync - email addresses must be updated at the<br>source (Accounting System or Website). &nbsp;"
                   + "However, you can update the email preferences here.<BR><BR>");
    }
    
    out.println("<table border=0 align=center bgcolor=#F5F5DC><tr>");
    out.println("<td align=left>&nbsp;</td>");
    out.println("<td align=left><strong>Primary Email</strong></td>");
    out.println("<td align=left><strong>Secondary Email</strong></td>");
    
    out.println("</tr><tr>");
    out.println("<td align=left>Email Address:</td>");
    out.println("<td align=left><input type=\"text\" name=\"email\" maxlength=\"50\" value=\"" + email +"\"" + (static_emails ? " readonly" : "") + " autocomplete=\"off\"></td>");
    out.println("<td align=left><input type=\"text\" name=\"email2\" maxlength=\"50\" value=\"" + email2 +"\"" + (static_emails ? " readonly" : "") + " autocomplete=\"off\"></td>");    
    //out.println("<td align=left><input type=\"text\" name=\"email\" maxlength=\"50\" value=\"" + email +"\"" + ((email_bounced!=0) ? " style=\"background-color: red\"" : "") + "" + (static_emails ? " readonly" : "") + " autocomplete=\"off\"></td>");
    //out.println("<td align=left><input type=\"text\" name=\"email2\" maxlength=\"50\" value=\"" + email2 +"\"" + ((email2_bounced!=0) ? " style=\"background-color: red\"" : "") + "" + (static_emails ? " readonly" : "") + " autocomplete=\"off\"></td>");    
    
    out.println("</tr><tr>");
    out.println("<td align=left>Receive Reservation Notifications:</td>");
    out.println("<td align=center><input type=\"checkbox\" " + ((emailOpt == 1) ? "checked " : "") + "name=\"emailOpt\" value=\"1\"></td>");
    out.println("<td align=center><input type=\"checkbox\" " + ((emailOpt2 == 1) ? "checked " : "") + "name=\"emailOpt2\" value=\"1\"></td>");
    
    out.println("</tr><tr>");
    out.println("<td align=left>Receive Club Communications:</td>");
    out.println("<td align=center><input type=\"checkbox\" " + ((clubEmailOpt1 == 1) ? "checked " : "") + "name=\"clubEmailOpt1\" value=\"1\"></td>");
    out.println("<td align=center><input type=\"checkbox\" " + ((clubEmailOpt2 == 1) ? "checked " : "") + "name=\"clubEmailOpt2\" value=\"1\"></td>");
    
    out.println("</tr><tr>");
    out.println("<td align=left>Receive Emails From Other Members:</td>");
    out.println("<td align=center><input type=\"checkbox\" " + ((memEmailOpt1 == 1) ? "checked " : "") + "name=\"memEmailOpt1\" value=\"1\"></td>");
    out.println("<td align=center><input type=\"checkbox\" " + ((memEmailOpt2 == 1) ? "checked " : "") + "name=\"memEmailOpt2\" value=\"1\"></td>");
    
    out.println("</tr><tr>");
    out.println("<td align=left>Include iCal Attachments (calendar):</td>");
    out.println("<td align=center><input type=\"checkbox\" " + ((iCal1 == 1) ? "checked " : "") + "name=\"iCal1\" value=\"1\"></td>");
    out.println("<td align=center><input type=\"checkbox\" " + ((iCal2 == 1) ? "checked " : "") + "name=\"iCal2\" value=\"1\"></td>");
    
    out.println("</tr><tr>");
    out.println("<td align=left>Emails Have Bounced:</td>");
    out.println("<td align=center><input type=\"checkbox\" " + ((email_bounced == 1) ? "checked " : "") + "name=\"bounced1\" value=\"1\"></td>");
    out.println("<td align=center><input type=\"checkbox\" " + ((email2_bounced == 1) ? "checked " : "") + "name=\"bounced2\" value=\"1\"></td>");
    out.println("</tr></table>");
    
    out.println("<BR><input type=submit name=updatemem value='Update' style=\"text-decoration:underline;width:90px\">");
    out.println("</form><BR><BR><strong>WARNING:</strong> &nbsp;Personal preferences should not be changed without the member's permission.");
    out.println("<BR><BR></td></tr></table>");
    
 }      // end of memDataTable
 
 
 
 private void updateMem(String user_id, String club, HttpServletRequest req, PrintWriter out, Connection con) {
     

   String temp = "";
   String email = "";
   String email2 = "";
   int emailOpt = 0;
   int emailOpt2 = 0;
   int clubEmailOpt1 = 0;
   int clubEmailOpt2 = 0;
   int memEmailOpt1 = 0;
   int memEmailOpt2 = 0;
   int email_bounced = 0;
   int email2_bounced = 0;
   int iCal1 = 0;
   int iCal2 = 0;
   
   email = (req.getParameter("email") == null) ? "" : req.getParameter("email");   
   email2 = (req.getParameter("email2") == null) ? "" : req.getParameter("email2");   
   
   if (req.getParameter("iCal1") != null) iCal1 = Integer.parseInt(req.getParameter("iCal1"));
   
   if (req.getParameter("iCal2") != null) iCal2 = Integer.parseInt(req.getParameter("iCal2"));
   
   if (req.getParameter("emailOpt") != null) emailOpt = Integer.parseInt(req.getParameter("emailOpt"));
   
   if (req.getParameter("emailOpt2") != null) emailOpt2 = Integer.parseInt(req.getParameter("emailOpt2"));
   
   if (req.getParameter("clubEmailOpt1") != null) clubEmailOpt1 = Integer.parseInt(req.getParameter("clubEmailOpt1"));
   
   if (req.getParameter("clubEmailOpt2") != null) clubEmailOpt2 = Integer.parseInt(req.getParameter("clubEmailOpt2"));
   
   if (req.getParameter("memEmailOpt1") != null) memEmailOpt1 = Integer.parseInt(req.getParameter("memEmailOpt1"));
   
   if (req.getParameter("memEmailOpt2") != null) memEmailOpt2 = Integer.parseInt(req.getParameter("memEmailOpt2"));
      
   if (req.getParameter("bounced1") != null) email_bounced = Integer.parseInt(req.getParameter("bounced1"));
   
   if (req.getParameter("bounced2") != null) email2_bounced = Integer.parseInt(req.getParameter("bounced2"));
   
   
   //
   //  Use the username and parms passed to update the member record
   //
   if (user_id != null && !user_id.equals("")) {      // if username provided
       
        PreparedStatement pstmt = null;

        String sql = "UPDATE member2b "
                    + "SET email = ?, email2 = ?, emailOpt = ?, emailOpt2 = ?, clubEmailOpt1 = ?, clubEmailOpt2 = ?, "
                    + "memEmailOpt1 = ?, memEmailOpt2 = ?, email_bounced = ?, email2_bounced = ?, iCal1 = ?, iCal2 = ? "
                    + "WHERE username = ?";
    
        try {

            pstmt = con.prepareStatement(sql);
            pstmt.clearParameters();
            
            pstmt.setString(1, email);
            pstmt.setString(2, email2);
            pstmt.setInt(3, emailOpt);
            pstmt.setInt(4, emailOpt2);
            pstmt.setInt(5, clubEmailOpt1);
            pstmt.setInt(6, clubEmailOpt2);
            pstmt.setInt(7, memEmailOpt1);
            pstmt.setInt(8, memEmailOpt2);
            pstmt.setInt(9, email_bounced);
            pstmt.setInt(10, email2_bounced);
            pstmt.setInt(11, iCal1);
            pstmt.setInt(12, iCal2);
            pstmt.setString(13, user_id);
            pstmt.executeUpdate();

            pstmt.close();

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
       
       
   } else {
       
       out.println("<script type=\"text/javascript\">");
       out.println("alert('There was a problem with the update.\\n\\nSelect OK to go back and try again.');");
       out.println("</script>");
   }
   
   memDataTable(user_id, club, out, con);  // rebuild the frame (member data table)  
     
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
