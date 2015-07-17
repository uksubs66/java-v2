/*******************************************************************************
 * 
 *   Proshop_activity_sendemail - prompts user for the items they would like to  
 *                                send emails from.  For example, which courts on
 *                                the selected day do they want to send emails
 *                                to the members currently signed up.
 * 
 * 
 *   Called By:  Proshop_gensheets (Control Panel - Send Email)
 * 
 * 
 *   Last updated:
 * 
 *      12/08/09  Removed parent_id references
 * 
 * 
 ******************************************************************************/



import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.Utilities;
import com.foretees.common.getActivity;
import com.foretees.client.action.ActionHelper;
import com.foretees.common.Connect;

public class Proshop_activity_sendemail extends HttpServlet {
    
 String rev = SystemUtils.REVLEVEL;
    
 String [] dayShort_table = { "inv", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

 
    
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;
    
    String club = (String)session.getAttribute("club");               // get club name
    String user = (String)session.getAttribute("user");               // get user name
    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    int sess_activity_id = (Integer)session.getAttribute("activity_id");
    boolean pushNotification = req.getParameter("push") != null;
    Connection con = Connect.getCon(req);
    
    //int parent_id = 0;
    int group_id = 0;
    int activity_id = 0;
    int month = 0;
    int day = 0;
    int year = 0;
    int date = 0;
    int i = 0;
    
    boolean setall = false;
    
    String activity_name = "";
    //String num = "";
    
    
    //get the time sheet date
    String dateStr = req.getParameter("date");

    if (dateStr == null || dateStr.equals("")) {

      date = 0;

    } else {

       date = Integer.parseInt(dateStr);
    }
/*
    String sid = req.getParameter("parent_id");
    try {
        parent_id = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
*/    
    String sid = req.getParameter("group_id");
    try {
        group_id = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    if (req.getParameter("setall") != null) {    
       
       setall = true;     // set all sheets as checked
    }

    
    //if ( parent_id == 0 ) parent_id = sess_activity_id;
    
    
    if (date > 0) {

        year = date / 10000;
        int temp = year * 10000;
        month = date - temp;
        temp = month / 100;
        temp = temp * 100;
        day = month - temp;
        month = month / 100;
    }


    out.println(SystemUtils.HeadTitle2("Proshop Send Email Page"));

    out.println("<style>");
    out.println("body { text-align: center; }");
    out.println("</style>");

    out.println("</head><body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page

    //
    // Display Root Activity Name
    //
    out.println("<p align=center><BR><BR><b><font size=6 color=#336633>" + getActivity.getActivityName(sess_activity_id, con) + "</font></b></p>");

    
    if (group_id != 0 && group_id != sess_activity_id) {

        out.println("<p align=center><b><font size=5 color=#336633>" + getActivity.getActivityName(group_id, con) + "</font></b></p>");

    }

    out.println("<center>");

    out.println("<p align=center><b>Send Emails For " + month + "/" + day + "/" + year + "</b><BR></p>");

    out.println("<table align=\"center\" border=\"1\" cellspacing=\"2\" cellpadding=\"3\" bgcolor=\"#F5F5DC\">");
    out.println("<td align=center>Please select the Sheets you wish to send the emails for.<br>" +
                "The email will be sent to all members on these sheets as long as they have a valid email address.</td>");
    out.println("</tr></table><BR><BR>");
    
    if (setall == false) {

       // add a Check All button
       out.println("<a href=\"Proshop_activity_sendemail?" + ((pushNotification)? "push=1&" : "") + "group_id=" +group_id+ "&setall=yes&date=" + date + "\" target=\"bot\" title=\"Click Here to Check All Boxes Below\" alt=\"Check All\">");
       out.println("Check All</a><br><br>");
       
    } else {

       // add a Clear All button
       out.println("<a href=\"Proshop_activity_sendemail?group_id=" +group_id+ "&date=" + date + "\" target=\"bot\" title=\"Click Here to Clear All Boxes Below\" alt=\"Clear All\">");
       out.println("Clear All</a><br><br>");
    }

    
    //
    // DISPLAY A SELECTION LIST OF THE TIME SHEETS
    //
    out.println("<form action=\"Send_email\" method=\"get\">");
    if (pushNotification) {
        out.println("<input type=\"hidden\" name=\"push\" value=\"1\">");
    }
    out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
    out.println("<input type=\"hidden\" name=\"nextAction\" value=\"addToList\">");
    out.println("<input type=\"hidden\" name=\"" + ActionHelper.SEARCH_TYPE + "\" value=\"" + ActionHelper.SEARCH_TIMESHEET + "\">");   // = emailTimeSheet
    
    out.println("<table align=center border=0>");

    try {

        pstmt = con.prepareStatement("" +
                    "SELECT activity_id, activity_name " +
                    "FROM activities " +
                    "WHERE parent_id = ? AND activity_id NOT IN (SELECT parent_id FROM activities) " +
                    "ORDER BY activity_name");

        pstmt.clearParameters();
        pstmt.setInt(1, group_id); // parent_id
        rs = pstmt.executeQuery();

        while ( rs.next() ) {

             activity_id = rs.getInt(1);
             activity_name = rs.getString(2);
             i++; 
            
             out.println("<tr valign=top><td>");
             out.println( activity_name );
             out.println("</td><td>");
             out.println("&nbsp;&nbsp;<input type=\"checkbox\" name=\"activity" +i+ "\" value=\"" +activity_id+ "\"" + ((setall) ? " checked" : "") + ">");
             out.println("</td></tr>");
         }

         out.println("<input type=\"hidden\" name=\"count\" value=\"" +i+ "\">");       // number of items listed (number of activity_ parms to check)

    } catch (Exception exc) {

        out.println("<p>ERROR:" + exc.toString() + "</p>");

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    // end the main table
    out.println("</table>");

    //  add submit button
    out.println("<BR><input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline;\">");

    out.println("</form>");
        
        
    
    // debug
    out.println("<!-- group_id=" + group_id + " -->");

    out.println("<br></body></html>");

    out.close();
 }

}