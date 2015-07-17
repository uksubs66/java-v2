/***************************************************************************************
 *   Proshop_activity_history: This servlet will display the history & notes for an Activity time
 *
 *
 *   Called by:     Proshop_gensheets
 *                  Proshop_oldgensheets?
 *
 *
 *   Created:       10/22/2009 by Paul
 *
 *
 *   Revisions:     
 *
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

import com.foretees.common.getActivity;
import com.foretees.common.Connect;

public class Proshop_activity_history extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    
 //*****************************************************
 // Process the a get method on this page as a post call
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    if (req.getParameter("history") == null || (!req.getParameter("history").equals("yes"))) {
        
        // not called correctly - exit
        //return;
    }
    
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setContentType("text/html");
    
    PrintWriter out = resp.getWriter();

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;

    Connection con = Connect.getCon(req);                      // get DB connection

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

    int slot_id = 0;
    String tmp = req.getParameter("slot_id");
    
    try { slot_id = Integer.parseInt(tmp); }
    catch (Exception ignore) {}
    
    // see if call is for the notes and not history
    if (req.getParameter("notes") != null) {
        
        displayNotes(slot_id, con, out);
        out.close();
        return;
    }
    
    boolean h = false; // used to control header output
    boolean x = false; // used for toggling rows

    int i = 0; // number of entries found

    String user = "";
    String bgcolor = "";; // used for row shading
    
    out.println(SystemUtils.HeadTitle("Reservation History"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("");

    try {

        pstmt = con.prepareStatement (
            "SELECT *, " +
            "DATE_FORMAT(date_time, '%c/%d/%Y') AS pretty_date, " +
            "DATE_FORMAT(date_time, '%l:%i %p') AS pretty_time " +
            "FROM activity_sheets " +
            "WHERE sheet_id = ?");

        pstmt.clearParameters();        // clear the parms
        pstmt.setInt(1, slot_id);
        rs = pstmt.executeQuery();      // execute the prepared stmt

        if (rs.next()) {

            out.println("<center><p><font size=5>Reservation History</font></p>");
            out.println("<b>Date:</b>&nbsp;&nbsp;" + rs.getString("pretty_date"));
            out.println("&nbsp;&nbsp;&nbsp;<b>Time:</b>&nbsp;&nbsp;" + rs.getString("pretty_time"));
            out.println("&nbsp;&nbsp;&nbsp;<b>Location:</b>&nbsp;&nbsp;" + getActivity.getFullActivityName(rs.getInt("activity_id"), con));
            out.println("</center><br>");

        }

        pstmt.close();

    } catch (Exception exp) {

        displayDatabaseErrMsg("Error looking up time slot information.", exp.getMessage(), out);

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    out.println("<center><form>");
    out.println("<input type=image src=\"/" +rev+ "/images/print.gif\" width=\"80\" height=\"18\" border=\"0\" onclick=\"window.print(); return false;\" alt=\"Click here to print this history.\">");
    out.println("&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;");
    out.println("<input type=button value=\"  Close  \" onclick=\"window.close();\">");
    out.println("</form></center>");
    
    try {
    
        pstmt = con.prepareStatement (
            "SELECT *, " +
            "DATE_FORMAT(date_time, '%c/%d/%Y') AS pretty_date, " +
            "DATE_FORMAT(date_time, '%r') AS pretty_time " +
            "FROM activity_sheet_history " +
            "WHERE sheet_id = ? " +
            "ORDER BY date_time DESC");

        pstmt.clearParameters();        // clear the parms
        pstmt.setInt(1, slot_id);
        rs = pstmt.executeQuery();      // execute the prepared stmt
        
        out.println("<table align=center border=1 cellspacing=5 cellpadding=3>");
        
        while (rs.next()) {
            
           if (h == false) {
              out.println("<tr bgcolor=\"#336633\" style=\"color: white\"><td nowrap><b>Date and Time</b></td><td><b>Name</b></td><td nowrap><b>Players</b></td></tr>");
              h = true;
           }
           
           i++;
           x = (x==false);
           bgcolor = (x==true) ? "#F9F9F9" : "#F2F2F2";

           user = rs.getString("username");
           
           if (!user.toLowerCase().startsWith("proshop")) {
               user = SystemUtils.getFullNameFromUsername(user, con);
           }

           out.println("<tr bgcolor=\"" + bgcolor + "\">");
            out.print("<td nowrap>" + rs.getString("pretty_date") + " at "  + rs.getString("pretty_time") + "</td>"); // date & time stamp
            out.print("<td nowrap>" + user + "</td>"); // member name or proshop username
            out.print("<td nowrap>" + rs.getString("players") + "</td>");  // players
           out.println("</tr>");
           
        }
        
        pstmt.close();

        out.println("</table>");
           
        if (i==0) {
            out.println("<center><br><p><i><b><font color=red>No history found for this time slot.</font></b></i></p>");
            //out.println("<br><form><input type=button value=\"  Close  \" onclick=\"window.close();\"></form><br>");
            out.println("<font size=-1>(this window will auto close in 3 seconds)</font></center>");
            out.println("<script>setTimeout('window.close()', 3000)</script>");
        } else {
            out.println("<center><p><i><b>&nbsp;Found "+ i +" historical " + ((i>1) ? "entries" : "entry") + " for this reservation.</b></i></p></center>");
            //out.println("<br><form><input type=button value=\"  Close  \" onclick=\"window.close();\"></form>");
        }
        
        out.println("<br>");
        
    } catch (Exception exp) {

        displayDatabaseErrMsg("Error looking up reservation history.", exp.getMessage(), out);
    
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
    
    out.println("</body></html>");
    out.close();

 } // end of doGet routine
 
 
 public void displayNotes(int slot_id, Connection con, PrintWriter out) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    out.println(SystemUtils.HeadTitle("Reservation Notes"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("");

    try {

        pstmt = con.prepareStatement (
            "SELECT *, " +
            "DATE_FORMAT(date_time, '%c/%d/%Y') AS pretty_date, " +
            "DATE_FORMAT(date_time, '%l:%i %p') AS pretty_time " +
            "FROM activity_sheets " +
            "WHERE sheet_id = ?");

        pstmt.clearParameters();        // clear the parms
        pstmt.setInt(1, slot_id);
        rs = pstmt.executeQuery();      // execute the prepared stmt

        if (rs.next()) {

            out.println("<center><p><font size=5>Reservation Notes</font></p>");
            out.println("<b>Date:</b>&nbsp;&nbsp;" + rs.getString("pretty_date"));
            out.println("&nbsp;&nbsp;&nbsp;<b>Time:</b>&nbsp;&nbsp;" + rs.getString("pretty_time"));
            out.println("&nbsp;&nbsp;&nbsp;<b>Location:</b>&nbsp;&nbsp;" + getActivity.getFullActivityName(rs.getInt("activity_id"), con));
            
            if (!rs.getString("notes").trim().equals( "" )) {
                out.println("<br><br>");
                out.println("<b>Notes:</b> &nbsp;&nbsp;" + rs.getString("notes"));
            }

            out.println("</center><br>");

        }

        pstmt.close();

    } catch (Exception exp) {

        displayDatabaseErrMsg("Error looking up notes for reservation.", exp.getMessage(), out);

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

 } // end of displayNotes routine
 
 
private void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {
    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H1>Database Access Error</H1>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Fatal Error: " + pMessage);
    out.println("<BR><br>Exception: " + pException);
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
}

} // end servlet public class