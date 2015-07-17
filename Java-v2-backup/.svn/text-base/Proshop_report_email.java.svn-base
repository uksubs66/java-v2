/***************************************************************************************
 *   Proshop_report_email:  This servlet will provide some basic reporting capabilities
 *                          for our Email Tool.
 * 
 * 
 *   Called by:       called by self and start w/ direct call main menu option.
 *
 *
 *   Created:         1/03/2014 by Paul
 *
 *
 *   Last Updated:  
 * 
 *          1/03/14  Code cleanup - initial release
 * 
 * 
 * 
 ****************************************************************************************/


import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;


import com.foretees.common.Connect;


public class Proshop_report_email extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)

    DateFormat df_full = DateFormat.getDateInstance(DateFormat.MEDIUM);

 //*****************************************************
 // Process the a get method on this page as a post call
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


    ResultSet rs = null;
    PreparedStatement pstmt = null;
    
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

    PrintWriter out = resp.getWriter();

    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";


    HttpSession session = null;
    
    String user = "";
    
    //
    //  allow for both proshop and member access
    //
    session = req.getSession(false);  // Get user's session object (no new one)

    if (session != null) {

        user = (String)session.getAttribute("user");   // get username
    }
    
    if (user.startsWith("proshop")) {     // from Proshop User ?
        
        session = SystemUtils.verifyPro(req, out);
    
    } else {
        
        session = SystemUtils.verifyMem(req, out);   // check for member
    }
    
    
    if (session == null) return;
    
    Connection con = Connect.getCon(req);

    if (con == null) {

        SystemUtils.buildDatabaseErrMsg("Unable to connect to the database.", "", out, true);
        return;
    }
    
    String club = (String)session.getAttribute("club");
    
        // set response content type
    try{
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
            resp.setHeader("Content-Disposition", "attachment;filename=\""+club+".xls\"");
        } else {
            resp.setContentType("text/html");
        }
    }
    catch (Exception exc) {
    }
    
    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    
    out.println(SystemUtils.HeadTitle2("Sent Email Report"));
    //out.println(Common_skin.getScripts(club, 0, session, req, true));
            
    out.println("</head>");
    
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
    
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    
    // page title
    out.println("<br><p align=center><font size=5>Sent Email History Log</font><br>");
    if (req.getParameter("log_id") == null) {
        
        out.println("<font size=3>(includes the last 60 days)</font>");
        
        // Display a message for 60 days to let people know the list is not retroactive
        Calendar cal = new GregorianCalendar();       // get todays date

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        long thisDate = (year * 10000) + (month * 100) + day;         // get adjusted date for today
        
        if (thisDate < 20140412) {
            out.println("<br><br><font size=3><span style=\"font-weight:bold\">Notice: </span>Emails sent prior to 2/11/2014 will not appear in this log.</font>");
        }
        
    } else {
        out.println("<font size=4>Email Details</font>");
    }
    out.println("</p>");
    
    int id = 0;
    
    if (req.getParameter("log_id") != null) {
        
        try {
            id = Integer.parseInt(req.getParameter("log_id"));
        } catch (NullPointerException ignore) {}
        
    }
    
    if ( id > 0 ) {
        
        String name = "";
        
        // display the selected email log data
        try {

            pstmt = con.prepareStatement("SELECT *, DATE_FORMAT(sent_date, '%m-%d-%Y at %r') AS pretty_date FROM email_audit_log WHERE id = ?");
            
            pstmt.clearParameters();
            
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            // back button
            out.println("<br><center><form action=Proshop_report_email><input type=submit value=\"Back\" style=\"width:75px; background:#8B8970\"></form></center>");
        
            // output table header
            out.println("<table width=640 align=center>");
         
            if ( rs.next() ) {

                out.print("<tr><td>");
                out.print("<br><b>Subject:</b> " + rs.getString("subject"));
                out.print("<br><b>Date Sent:</b> " + rs.getString("pretty_date"));
                out.print("<br><b>Reply-To:</b> " + rs.getString("from_address"));
                out.print("<br><b>Recipients:</b> " + rs.getInt("recipients"));
                out.print("<br><b>Attachments:</b> " + rs.getInt("attachments"));
                out.print("<br><b>Message Body: <a href=\"#\" onclick=\"$('#message_body').toggle();\" style=\"color:#000;font-size:10pt\">show/hide message</a></b><br>");
                
                out.println("<div id=\"message_body\" class=\"message_container\" style=\"display:none;border:2px solid black;padding:10px\">");
                
                out.println(rs.getString("message"));
                
                out.println("</div>");
                
                out.println("</tr>");
                
            }
            
            out.println("</table><br><br>");
            
            // query and display the recipients
            pstmt = con.prepareStatement("" + 
                    "SELECT d.*, CONCAT(m.name_last, ', ', m.name_first, IF(m.name_mi != '', CONCAT(' ', m.name_mi, '.'), '')) AS mem_name " +
                    "FROM email_audit_log_details d " +
                    "LEFT OUTER JOIN member2b m ON m.username = d.username " + 
                    "WHERE d.audit_log_id = ? " + 
                    "ORDER BY m.name_last, m.name_first, m.name_mi");
            
            pstmt.clearParameters();
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            // output table header
            out.println("<table width=600 align=center cellspacing=0 cellpadding=5 border=1 bgcolor=#F5F5DC>");
            out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                           "<td nowrap align=center><font size=3><b>&nbsp;Member Name&nbsp;</b></font></td>" +
                           "<td nowrap align=center><font size=3><b>&nbsp;Username&nbsp;</b></font></td>" +
                           "<td nowrap align=center><font size=3><b>&nbsp;Email Address&nbsp;</b></font></td>" +
                        "</tr>");
         
            while ( rs.next() ) {
                
                out.print("<tr>");
                out.print("<td>" + ((rs.getString("mem_name") == null) ? "" : rs.getString("mem_name")) + "</td>");
                out.print("<td>" + rs.getString("username") + "</td>");
                out.print("<td>" + rs.getString("email") + "</td>");
                out.println("</tr>");
                
            }
            
            // close table
            out.println("</table>");
            
        } catch (Exception exc) {

            out.println("<p>Fatal error loading up logged email data!</p>Error = " + exc.toString());
            return;

        } finally {

            try { rs.close(); }
            catch (SQLException ignored) {}

            try { pstmt.close(); }
            catch (SQLException ignored) {}

        }
        
        out.println("<br><center><form action=Proshop_report_email><input type=submit value=\"Back\" style=\"width:75px; background:#8B8970\"></form></center>");
        
    } else {
        
        
        // display a listing of logged emails
        try {

            pstmt = con.prepareStatement("SELECT *, DATE_FORMAT(sent_date, '%m-%d-%Y at %r') AS pretty_date FROM email_audit_log ORDER BY sent_date DESC");
            
            // id, email_type, sent_date, attachment_count, from_address, subject
            
            pstmt.clearParameters();
            
            // TODO: add filters
            //pstmt.setInt(1, email_type);
            //pstmt.setInt(2, attachement_count);

            rs = pstmt.executeQuery();

            // output table header
            out.println("<table width=600 align=center cellspacing=0 cellpadding=5 border=1 bgcolor=#F5F5DC>"); // style=\"border: 1px solid #336633\"  F5F5DC  86B686
            out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                           "<td nowrap align=center><font size=3><b>&nbsp;Subject&nbsp;</b></font></td>" +
                           "<td nowrap align=center><font size=3><b>&nbsp;Reply-To&nbsp;</b></font></td>" +
                           "<td nowrap align=center><font size=3><b>&nbsp;Recipients&nbsp;</b></font></td>" +
                           "<td nowrap align=center><font size=3><b>&nbsp;Attachments&nbsp;</b></font></td>" +
                           "<td nowrap align=center><font size=3><b>&nbsp;Date Sent&nbsp;</b></font></td>" +
                        "</tr>");
         
            while ( rs.next() ) {

                out.print("<tr>");
                out.print("<td nowrap><a href=\"Proshop_report_email?log_id=" + rs.getInt("id") + "\" target=\"bot\" style=\"color: black\">" + rs.getString("subject") + "</a></td>");
              //out.print("<td>" + rs.getString("email_type") + "</td>");
                out.print("<td nowrap>" + rs.getString("from_address") + "</td>");
                out.print("<td align=center>" + rs.getInt("recipients") + "</td>");
                out.print("<td align=center>" + rs.getInt("attachments") + "</td>");
                out.print("<td nowrap>" + rs.getString("pretty_date") + "</td>");
              //out.print("<td>" + rs.getInt("id") + "</td>");
                out.println("</tr>");
                
            }
            
            // close table
            out.println("</table>");
    
        } catch (Exception exc) {

            out.println("<p>Fatal error loading up logged email data!</p>Error = " + exc.toString());
            return;

        } finally {

            try { rs.close(); }
            catch (SQLException ignored) {}

            try { pstmt.close(); }
            catch (SQLException ignored) {}

        }
        
        // button back to home
        out.println("<br><center><form action=Proshop_announce><input type=submit value=\"Home\" style=\"width:75px; background:#8B8970\"></form></center>");
            
    }
    
    out.println("</body>");
    out.println("</html>");
    
    out.close();
    
 } // end of doGet routine
 
}