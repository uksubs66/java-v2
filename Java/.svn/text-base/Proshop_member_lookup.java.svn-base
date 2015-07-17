/***************************************************************************************
 *   Proshop_member_lookup: This servlet will ask for retrieve a member number from the user
 *                          and return all the matching results for the user to see
 *
 *
 *   Called by:     Called by Proshop_sheet (Control Panel option)
 *                  expects "mem_num" to be passed in the querystring
 *                  mem_num = member number to search for
 *
 *
 *   Created:       03/21/2006
 *
 *
 *   Revisions:     
 *                  
 *                  02/05/2007  Added ORDER BY clause to query and commented out scorelink
 *                  09/21/2006  Added column for posting scores via scorelink  
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


public class Proshop_member_lookup extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    
 //*****************************************************
 // Process the a get method on this page as a post call
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    doPost(req, resp);                                              // call doPost processing

 } // end of doGet routine
 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setContentType("text/html");
    
    PrintWriter out = resp.getWriter();

    ResultSet rs = null;

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);                      // get DB connection

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
      return;
    }
   
    //int mem_num = 0;
    String mem_name = "";
    String username = "";
    String mtype = "";
    String mship = "";
    String memNum = "";
    String smem_num = req.getParameter("mem_num");
    String bgcolor = "";
    
    out.println("<form method=post action=/"+rev+"/servlet/Proshop_post_scores name=frmPostScore id=frmPostScore>");
    //out.println("<form method=post action=/"+ rev + "/servlet/Proshop_post_scores>");
    out.println("<input type=hidden name=username value=\"\">");
    out.println("<input type=hidden name=score value=\"\">");
    out.println("</form>");
    
    out.println("<script>");
    out.println("function postScore(pUsername) {");
    out.println(" var f = document.forms['frmPostScore'];");
    out.println(" f.username.value=pUsername;");
    out.println(" f.submit();");
    out.println("}");
    out.println("</script>");
    
    try {
    
        PreparedStatement pstmtc = con.prepareStatement ("SELECT name_first, name_last, username, m_type, m_ship, memNum FROM member2b WHERE memNum LIKE ? ORDER BY name_last, name_first");

        pstmtc.clearParameters();        // clear the parms
        pstmtc.setString(1, smem_num+"%");
        
        rs = pstmtc.executeQuery();      // execute the prepared stmt
        int i = 0;
        boolean x = false;
        boolean h = false;
        
        out.println("<br><table align=center border=1 cellpadding=5 bgcolor=\"#F5F5DC\">");
        
        while (rs.next()) {
           if (h == false) {
              out.println("<tr bgcolor=\"#336633\" style=\"color:white\"><td colspan=2></td><td nowrap><b>Member Name</b></td><td nowrap><b>Member Number</b></td><td nowrap><b>User Name</b></td><td nowrap><b>Member Type</b></td><td><b>Membership</b></td></tr>");
              h = true;
           }
           i++;
           x = (x==false);
           bgcolor = (x==true) ? "#DDDDBE" : "#B3B392";
           
           mem_name = rs.getString(2) + ", " + rs.getString(1);
           username = rs.getString(3);
           mtype = rs.getString(4);
           mship = rs.getString(5);
           memNum = rs.getString(6);
           
           out.println("<tr bgcolor=\"" + bgcolor + "\">");
           out.print("<td bgcolor=white><b>" + i + "</b>.</td>");
           out.print("<td nowrap><!--<img src=\"\" width=16 height=16 onclick=\"postScore('" + username + "')\">--></td>");
           out.print("<td nowrap>" + mem_name + "</td>");
           out.print("<td>" + memNum + "</td>");
           out.print("<td>" + username + "</td>");
           out.print("<td nowrap>" + mtype + "</td>");
           out.print("<td nowrap>" + mship + "</td>");
           out.println("</tr>");
        }
        pstmtc.close();

        out.println("</table>");
           
        if (i==0) {
            out.println("<center><br><p><i><b><font color=red>No member found with member number:</font> "+ smem_num +"</b></i></p>");
            out.println("<br><form><input type=button value=\"  Close  \" onclick=\"window.close();\"></form><br>");
            out.println("<font size=-1>(this window will auto close in 3 seconds)</font></center>");
            out.println("<script>setTimeout('window.close()', 3000)</script>");
        } else {
            out.println("<center><p><i><b><font size=2>&nbsp;Found "+ i +" members with matching member numbers.</font></b></i></p>");
            out.println("<br><form><input type=button value=\"  Close  \" onclick=\"window.close();\"></form></center>");
        }
    
        out.println("<br>");
        
    }
    catch (Exception exp) {
        
        SystemUtils.buildDatabaseErrMsg("Error looking up member by their number.", exp.getMessage(), out, false);
    }
    
 } // end of doPost routine
 
private void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {
    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H1>Database Access Error</H1>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Fatal Error: " + pMessage);
    out.println("<BR><br>Exception: " + pException);
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
}

} // end servlet public class