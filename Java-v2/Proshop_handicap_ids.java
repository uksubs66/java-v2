/***************************************************************************************     
 *   Proshop_handicap_ids:  This servlet will provide a quick way to update the members' handicap
 *                          numbers and club/course ids.
 *
 *
 *   called by:  proshop menu and self
 *
 *   created: 1/07/2013   Bob P.
 *
 *   last updated:
 *
 *     1/18/13  Added a Refresh Name List link to update the info in the name list.
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

public class Proshop_handicap_ids extends HttpServlet {

                               
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
    if (!SystemUtils.verifyProAccess(req, "TOOLS_HDCP", con, out)) {
        SystemUtils.restrictProshop("TOOLS_HDCP", out);
        return;
    }
       
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
   
   
    // MAKE SURE THIS CLUB HAS A HDCP SYSTEM ENABLED
    int in_season = 0;
    String hdcpSystem = "";
    try {
        
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("" +
                "SELECT " +
                    "hdcpSystem, " +
                    "IF(hdcpStartDate <= now() && hdcpEndDate >= now(),1,0) AS in_season " +
                "FROM club5;");
        if (rs.next()) {
            hdcpSystem = rs.getString("hdcpSystem");
            in_season = rs.getInt("in_season");
        }
    
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading up club information in Proshop_handicap_ids.", exc.getMessage(), out, false);
        return;
    }
    
    // MAKE SURE CLUB IS SETUP TO USE HDCP FEATURE
    if (hdcpSystem.equals("") || hdcpSystem.equalsIgnoreCase("other")) {
        
        out.println("<br><br><p align=center><b><i>Your club does not have a handicap system that allows online access.</i></b></p>");
        return;
    }
    
   
   String user_id = (req.getParameter("user_id") == null) ? "" : req.getParameter("user_id");  // get username if passed
   
   if (req.getParameter("updatemem") != null) {
    
       updateMem(user_id, req, out, con);    //  user submitted an update - process the update
       return;
   }
   
   if (req.getParameter("memdata") != null) {        // if iframe loaded - display the handicap data
       
       memDataTable(user_id, out, con);    
       return;
   }
   
   
   //
   //   output the html page
   //
   out.println(SystemUtils.HeadTitle2("Proshop Handicap Ids"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<br><h2 align=center>Member Handicap Data Management</h2>");
    
   out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<p>Use this tool to update the handicap values for one or more members.</p>");
      out.println("</font>");
   out.println("</td></tr></table>");
   
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><BR>");
   out.println("Select the member to update, then add or update the handicap information below.</font><BR><BR>");
   

   //  Output the member list form
   
   listMems(user_id, out, con);
   

   // insert an iframe to hold the member data table so it can run independently
   out.println("<iframe name=\"memdataiframe\" id=\"memdataiframe\" src=\"Proshop_handicap_ids?memdata\" width=\"90%\" scrolling=no frameborder=no onload='javascript:resizeIframe(this);'></iframe>");

    
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
    out.println("<tr bgcolor=\"#336633\"><td align=center><b><font color=white>&nbsp;Update Member Handicap Data</font></b></td></tr>");
    out.println("<tr><td align=\"center\">");
    
   out.println("<font size=\"1\" face=\"Arial, Helvetica, Sans-serif\"><BR>");
   
   out.println("<input type=text name=DYN_search onkeyup=\"DYN_triggerChange()\" onkeypress=\"DYN_moveOnEnterKey(event); return DYN_disableEnterKey(event)\" onclick=\"this.select()\" value=\"Type Last Name\">"); // return DYN_disableEnterKey(event)
   
   out.println("&nbsp;&nbsp;(Name Search)<BR>Name (Member Number) - Current Handicap Number</font><BR>");
    
    out.println("<input type=hidden name=user_id value=''>");   // username of member selected
    out.println("<select size=15 name=\"bname\" onclick=\"selectMem(this.options[this.selectedIndex].value)\" onkeypress=\"DYN_moveOnEnterKey(event); return false\" style=\"cursor:hand\" style=\"width:220px\">");

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    String sql = "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, memNum, username, ghin "
                + "FROM member2b "
                + "WHERE inact = 0 AND billable = 1 "
                + "ORDER BY last_only, memNum, name_first, name_mi";
    
    String last = "";
    String first = "";
    String mid = "";
    String mnum = ""; 
    String username = "";
    String dname = "";
    String hndcpNum = "";

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
            hndcpNum = rs.getString("ghin");

            if (mid.equals("")) {
                dname = last + ", " + first + " (" +mnum+ ") - " +hndcpNum;
            } else {
                dname = last + ", " + first + " " + mid + " (" +mnum+ ") - " +hndcpNum;
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
    out.println("<a href=\"Proshop_handicap_ids\">Refresh Name List</a>");      
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
 
 
 private void memDataTable(String user_id, PrintWriter out, Connection con) {
    
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    String sql = "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, ghin, hdcp_club_num_id, hdcp_assoc_num_id "
                + "FROM member2b "
                + "WHERE username = ?";
    
    String sql2 = "SELECT hdcp_club_num_id, club_num "
                + "FROM hdcp_club_num";
    
    String sql3 = "SELECT hdcp_assoc_num_id, assoc_num "
                + "FROM hdcp_assoc_num";
    
    String last = "";
    String first = "";
    String mid = "";
    String dname = "";
    String hndcpNum = "";
    String clubNum = "";
    String assocNum = "";
    int clubNumId = 0;
    int assocNumId = 0;
    int clubNumP = 0;
    int assocNumP = 0;

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
                hndcpNum = rs.getString("ghin");
                clubNumId = rs.getInt("hdcp_club_num_id");
                assocNumId = rs.getInt("hdcp_assoc_num_id");
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
      //out.println("<form action=\"Proshop_handicap_ids\" method=\"POST\" name=\"memdataform\" onsubmit=\"false\">");
      out.println("<form action=\"Proshop_handicap_ids\" method=\"POST\" name=\"memdataform\" id=\"memdataform\">");
      out.println("<input type=\"hidden\" name=\"user_id\" value=\"\">");
      out.println("<input type=\"hidden\" name=\"memdata\" value=\"yes\">");
      out.println("</form>");
    
    
    out.println("<table border=0 align=center bgcolor=#F5F5DC width=\"650\">");
    out.println("<form method=post name=frmHndcpData>");        
    out.println("<input type=hidden name=user_id value=\"" +user_id+ "\">");   // username of member selected
    out.println("<tr><td align=center><HR>");
    out.println("<strong>" +dname+ "</strong><BR><BR>");
   
    out.println("Handicap Number:&nbsp;&nbsp;<input type=text name=handicap_number value=\"" +hndcpNum+ "\" size=16 maxlength=16>&nbsp;&nbsp;&nbsp;&nbsp;<BR><BR>");
    
    out.println("Club Number:&nbsp;&nbsp;<select name=club_number size=1>");
    
    try {

        pstmt = con.prepareStatement(sql2);
        pstmt.clearParameters();
        rs = pstmt.executeQuery();

        while (rs.next()) {

            clubNumP = rs.getInt("hdcp_club_num_id");
            clubNum = rs.getString("club_num");
            
            out.print("<option value=\"" +clubNumP+ "\"");
            if (clubNumP == clubNumId) out.print(" selected");  
            out.println(">" +clubNum+ "</option>");            
        }
        pstmt.close();

    } catch (Exception exc) {

        // handle error

    } finally {

        try {rs.close();} catch (Exception ignore){}
        try {pstmt.close();} catch (Exception ignore){}
    }
    
    out.println("</select>");    
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;Association Number:&nbsp;&nbsp;<select name=assoc_number size=1>");
    
    try {

        pstmt = con.prepareStatement(sql3);
        pstmt.clearParameters();
        rs = pstmt.executeQuery();

        while (rs.next()) {

            assocNumP = rs.getInt("hdcp_assoc_num_id");
            assocNum = rs.getString("assoc_num");
            
            out.print("<option value=\"" +assocNumP+ "\"");
            if (assocNumP == assocNumId) out.print(" selected");
            out.println(">" +assocNum+ "</option>");
        }
        pstmt.close();

    } catch (Exception exc) {

        // handle error

    } finally {

        try {rs.close();} catch (Exception ignore){}
        try {pstmt.close();} catch (Exception ignore){}
    }
    
    out.println("</select>");    
    out.println("<BR><BR><input type=submit name=updatemem value='Update' style=\"text-decoration:underline;width:90px\">");
    out.println("<BR></form></td></tr></table>");
    
 }      // end of memDataTable
 
 
 
 private void updateMem(String user_id, HttpServletRequest req, PrintWriter out, Connection con) {
     

   String temp = "";
   int clubNum = 0;
   int assocNum = 0;
   
   String handicap = (req.getParameter("handicap_number") == null) ? "" : req.getParameter("handicap_number");  // get the handicap number
   
   temp = (req.getParameter("club_number") == null) ? "" : req.getParameter("club_number");  // get club number
   
   if (!temp.equals("")) {
   
       clubNum = Integer.parseInt(temp);
   }

   temp = (req.getParameter("assoc_number") == null) ? "" : req.getParameter("assoc_number");  // get association number
   
   if (!temp.equals("")) {
   
       assocNum = Integer.parseInt(temp);
   }
   
   //
   //  Use the username and parms passed to update the member record
   //
   if (user_id != null && !user_id.equals("")) {      // if username provided
       
        PreparedStatement pstmt = null;

        String sql = "UPDATE member2b "
                    + "SET ghin = ?, hdcp_club_num_id = ?, hdcp_assoc_num_id = ? "
                    + "WHERE username = ?";
    
        try {

            pstmt = con.prepareStatement(sql);
            pstmt.clearParameters();
            
            pstmt.setString(1, handicap);
            pstmt.setInt(2, clubNum);
            pstmt.setInt(3, assocNum);
            pstmt.setString(4, user_id);
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
   
   memDataTable(user_id, out, con);  // rebuild the frame (member data table)  
     
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
