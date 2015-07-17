/***************************************************************************************
 *   Proshop_content: This servlet will handle the management of custom email content
 *                    used in the various ForeTees products.
 *
 *
 *   Called by:       called by self and start w/ direct call main menu option
 *
 *
 *   Created:         10/20/2010 by Paul
 *
 *
 *   Last Updated:  
 *
 *          3/01/13  Upgrade TinyMCE and tweak settings
 *          2/22/13  Add courseName selection list for golf content.
 *          9/21/12  Add Palatino Linotype font to the tinyMCE editor
 *          3/27/12  Add new option for Unaccompanied Guests Only for email content in reservations (case 2124).
 *          1/04/12  Hide options for including content in outgoing dining emails
 *          8/30/11  Allow for Dining proshop user (Admin) to access these pages in non-frame mode.
 *          8/12/11  Fixed activity_id not getting set correctly when adding new content (all were being set to 0 = golf). Updated a "Tee Time" reference to be activity-friendly.
 *          7/08/11  Add dining support
 *          2/07/11  Add a help link to a pdf file for the Time Mode option.
 *                     Also added a link to an online editor user guide.
 *
 *
 *
 *   Notes:
 *
 *      Content Locations - hard coded static locations that we define
 *      Content Containers - defines the size, border, alignment of the content
 *      Content - the actual content to be displayed
 *
 *
 *
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
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.ProcessConstants;



public class Proshop_content extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)

    String yes = "Y";
    String no = "-";
    
    static String DINING_USER = ProcessConstants.DINING_USER;               // Dining username for Admin user from Dining System

    
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);                      // get DB connection

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

    // see if we are here to display a preview or edit existing content
    if (req.getParameter("force_preview") != null) {

        int location_id = Integer.parseInt(req.getParameter("location_id"));
        doForecedPreview(req, id, location_id, user, club, sess_activity_id, out, con);
        out.close();
        return;

    } else if (req.getParameter("preview") != null) {

        doPreview(req, lottery, user, club, sess_activity_id, out, con);
        out.close();
        return;

    } else if (req.getParameter("edit") != null) {

        doEdit(id, sess_activity_id, lottery, req, out, con);
        out.close();
        return;
        
    } else if (req.getParameter("new") != null) {

        doEdit(0, sess_activity_id, lottery, req, out, con);
        out.close();
        return;

    } else if (req.getParameter("delete") != null) {

        // delete the content
        try {

            pstmt = con.prepareStatement("DELETE FROM email_content WHERE id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (Exception exc) {

            out.println("<p>Error deleting content! Error: " + exc.getMessage() + "</p>");

        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }
    }

    String bgcolor = "";
    String location = "";

    //int multi = Utilities.getMulti(con);

    out.println(SystemUtils.HeadTitle("ForeTees - Custom Email Content"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    
    if (!user.equals(DINING_USER)) {
         
       SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    }


/*
    ArrayList<Integer> defaultIDs = new ArrayList<Integer>();

    // DISPLAY ALL THE EXISTING CONTENT FOR THIS CLUB/ACTIVITY
    try {

        pstmt = con.prepareStatement("" +
                "SELECT *, " +
                    "DATE_FORMAT(start_datetime, '%b. %D, %Y') AS pretty_start_date, DATE_FORMAT(end_datetime, '%b. %D, %Y') AS pretty_end_date, " +
                    "DATE_FORMAT(start_datetime, '%l:%i %p') AS pretty_start_time, DATE_FORMAT(end_datetime, '%l:%i %p') AS pretty_end_time " +
                "FROM email_content " +
                "WHERE activity_id = ? " +
                "ORDER BY enabled");

        pstmt.clearParameters();
        pstmt.setInt(1, sess_activity_id);
        rs = pstmt.executeQuery();

        while (rs.next()) {


        }

    } catch (Exception exc) {

        out.println("error=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }


    out.println("");
    out.println("");
    out.println("");
    out.println("");
    out.println("");
    out.println("");
    out.println("");
*/




    out.println("<table border=\"0\" align=\"center\">");
    out.println("<tr><td align=\"center\">");

    out.println("<table cellpadding=\"5\" border=\"0\" bgcolor=\"#336633\">");
    out.println("<tr><td align=\"center\">");
    out.println("<font color=\"#FFFFFF\" size=\"3\">");
    out.println("<b>Custom Email Content</b><br>");
    out.println("</font>");
    out.println("<font color=\"#FFFFFF\" size=\"2\">");
    out.println("<br>To change or remove custom email content, click on the 'Edit' button within the desired content row.<br>" +
                "If you wish to see how it will look in an email, click the 'Preview' button.");

    out.println("<br>");
    out.println("</font></td></tr></table><br><br>");
    
    //
    //   Add a link to an instructional video
    //
    out.println("<p align=center><a href=\"Proshop_content_videos\" target=\"_blank\">Click here to view a short Instructional Video on how to add content to your confirmation emails.</a><br><br></p>");
       
    //out.println("<p align=center><iframe src=\"http://player.vimeo.com/video/54498241\" width=\"500\" height=\"281\" frameborder=\"0\" webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>");
    //out.println("<br><br></p>");
    

    // output the create new & preview buttons
    out.println("<table border=\"0\"><tr>");
        if (!user.equals(DINING_USER)) {
           out.println("<form method=\"get\" action=\"Proshop_content\" target=\"bot\">");
        } else {
           out.println("<form method=\"get\" action=\"Proshop_content\">");
        }
        out.println("<input type=hidden name=id value=\"0\">");
        out.println("<td><input type=submit name=new value=\"  Create New Content  \"></td>");
        out.println("</form>");

        if (!user.equals(DINING_USER)) {
           out.println("<form method=\"get\" action=\"Proshop_content\" target=\"bot\">");
        } else {
           out.println("<form method=\"get\" action=\"Proshop_content\">");
        }
        out.println("<td>&nbsp; &nbsp;</td>");
        out.println("<td><input type=submit name=preview value=\"  Preview  \"></td>");
        out.println("</form>");
    out.println("</tr></table><br>");

    out.println("<table border=\"2\" cellpadding=\"5\"><tr bgcolor=\"#8B8970\">");
    out.println("<td colspan=\"" + ((organization_id == 0) ? 13 : 14) + "\" align=\"center\">");
    out.println("<font size=\"3\">");
    out.println("<p align=\"center\"><b>Custom Email Content</b></p>");
    out.println("</font></td></tr>");
    out.println("<tr bgcolor=\"#8B8970\"><td align=\"center\">");
    out.println("<font size=\"2\"><p><b>Name of Content</b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><p><b>Location</b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><p><b>Date Range</b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><p><b>Time Range</b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><p><b>Time Mode</b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><p><b>" + ((sess_activity_id == 0) ? "Tee Times" : "Reservations") + "</b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><p><b>Events</b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><p><b>Lesson</b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><p><b>Lottery</b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><p><b>Wait List</b></p>");
    out.println("</font></td>");
    // COMMENT OUT UNTIL WE IMPLEMENT CUSTOM CONTENT IN OUTGOING DINING EMAILS
/*
    if (organization_id != 0) {
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><p><b>Dining</b></p>");
    out.println("</font></td>");
    }
*/
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><p><b>Only w/ Guests</b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><p><b>Email Tool</b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><p><b>Preview</b></p>");
    out.println("</font></td>");
    
    out.println("</tr>");


    // DISPLAY ALL THE EXISTING CONTENT FOR THIS CLUB/ACTIVITY
    try {

        pstmt = con.prepareStatement("" +
                "SELECT *, " +
                    "DATE_FORMAT(start_datetime, '%b. %D, %Y') AS pretty_start_date, DATE_FORMAT(end_datetime, '%b. %D, %Y') AS pretty_end_date, " +
                    "DATE_FORMAT(start_datetime, '%l:%i %p') AS pretty_start_time, DATE_FORMAT(end_datetime, '%l:%i %p') AS pretty_end_time " +
                "FROM email_content " +
                "WHERE activity_id = ? " +
                "ORDER BY enabled DESC");

        pstmt.clearParameters();
        pstmt.setInt(1, sess_activity_id);
        rs = pstmt.executeQuery();

        boolean inactive = false;
        boolean found = false;

        while (rs.next()) {

            if (!inactive && rs.getInt("enabled") == 0) {

                // we are now displaying inactive ones so output a seperator
                inactive = true;

                if (!found) {

                    // if we didn't find any active content before finding this inactive one then display message
                    out.println("<tr bgcolor=\"#F5F5DC\">");
                    out.println("<td colspan=13 align=\"center\"><font size=\"3\"><b>No Active Content Found</b></font></td>");
                    out.println("</tr>");

                }

                out.println("<tr bgcolor=\"#8B8970\">");
                out.println("<td colspan=13 align=\"center\"><font size=\"3\"><b>Inactive Content</b></font></td>");
                out.println("</tr>");

            }

            found = true; // indicate that we found at least one piece of content

            if (rs.getInt("location_id") == 1) {

                // CONTENT_AREA_1
                location = "Right Side";

            } else if (rs.getInt("location_id") == 2) {

                // LOWER_SPAN
                location = "Lower Span";

            }

            out.println("<tr bgcolor=\"" + ((!bgcolor.equals("")) ? bgcolor : "#F5F5DC") + "\">");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><p>" +rs.getString("name")+ "</p>");
            out.println("</font></td>");
         
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><p>" +location+ "</b></p>");
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><p>" +rs.getString("pretty_start_date")+ "<br>thru<br>" +rs.getString("pretty_end_date")+ "</p>");
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><p>" +rs.getString("pretty_start_time")+ "<br>to<br>" +rs.getString("pretty_end_time")+ "</p>");
            out.println("</font></td>");

            out.println("<td align=\"center\">");
          //  out.println("<font size=\"2\"><p>" +((rs.getInt("time_mode") == 0) ? "Time Booked" : "Day Made")+ "</p>");
            out.println("<font size=\"2\"><p>" +((rs.getInt("time_mode") == 0) ? "Date/Time of Event" : "Date/Time of Action")+ "</p>");
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><p>" +((rs.getInt("reservation") == 0) ? no : yes)+ "</p>");
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><p>" +((rs.getInt("event_signup") == 0) ? no : yes)+ "</p>");
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><p>" +((rs.getInt("lesson_signup") == 0) ? no : yes)+ "</p>");
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><p>" +((rs.getInt("lottery_signup") == 0) ? no : yes)+ "</p>");
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><p>" +((rs.getInt("wait_list_signup") == 0) ? no : yes)+ "</p>");
            out.println("</font></td>");
            // COMMENT OUT UNTIL WE IMPLEMENT CUSTOM CONTENT IN OUTGOING DINING EMAILS
/*
            if (organization_id != 0) {
                out.println("<td align=\"center\">");
                out.println("<font size=\"2\"><p>" +((rs.getInt("dining") == 0) ? no : yes)+ "</p>");
                out.println("</font></td>");
            }
*/
            out.println("<td align=\"center\"><font size=\"2\"><p>");
        //    out.println("<font size=\"2\"><p>" +((rs.getInt("only_if_guests") == 0) ? no : yes)+ "</p>");
            if ((rs.getInt("only_if_guests") > 0) || (rs.getInt("only_if_unaccomp") > 0)) {
                out.println("Y");
            } else {
                out.println("-");
            }
            out.println("</p></font></td>");

            out.println("<td align=\"center\">");
            if (rs.getInt("email_tool_pro") + rs.getInt("email_tool_mem") == 0) {
                out.println("<font size=\"2\"><p>" + no + "</p>");
            } else {
                out.println("<font size=\"2\"><p>" +((rs.getInt("email_tool_pro") == 0) ? "" : "Pro<br>")+ "" +
                                                "" +((rs.getInt("email_tool_mem") == 0) ? "" : "Mem")+ "</p>");
            }
            out.println("</font></td>");

            out.println("<td align=\"center\" nowrap><br>");
            out.println("<form method=\"get\" action=\"Proshop_content\" target=\"_preview\">");
            out.println("<input type=hidden name=id value=\"" + rs.getInt("id") + "\">");
            out.println("<input type=hidden name=location_id value=\"" + rs.getInt("location_id") + "\">");
            out.println("<input type=submit name=force_preview value=\" Preview \">");
            out.println("</form>");
            //out.println("&nbsp; &nbsp;");
             if (!user.equals(DINING_USER)) {
                 out.println("<form method=\"get\" action=\"Proshop_content\" target=\"bot\">");
             } else {
                 out.println("<form method=\"get\" action=\"Proshop_content\">");
             }
            out.println("<input type=hidden name=id value=\"" + rs.getInt("id") + "\">");
            out.println("<input type=submit name=edit value=\"  Edit  \">");
            out.println("</form>");
            out.println("</td>");
            
            out.println("</tr>");

        }

    } catch (Exception exc) {

        out.println("error=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
    
    
    out.println("</table></font>");                   // end of memNotice table
    out.println("</td></tr></table>");                // end of main page table

    out.println("<center>");
    if (!user.equals(DINING_USER)) {
       out.println("<form method=\"get\" action=\"Proshop_announce\">");
       out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
    } else {
        out.println("<form><input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Return \" onClick='self.close()' alt=\"Close\">");
    }
    out.println("</form></center>");

    out.println("</body>");
    out.println("</html>");
    
    out.close();

 } // end of doGet routine



 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    PreparedStatement pstmt = null;

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);                      // get DB connection

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

    int id = Integer.parseInt(req.getParameter("id"));

    String msg = "";
    String start_datetime = "";
    String end_datetime = "";
    String name = req.getParameter("name");
    String courseName = "";

    int reservation = (req.getParameter("reservation") != null && req.getParameter("reservation").equals("1")) ? 1 : 0;
    int event_signup = (req.getParameter("event_signup") != null && req.getParameter("event_signup").equals("1")) ? 1 : 0;
    int lesson_signup = (req.getParameter("lesson_signup") != null && req.getParameter("lesson_signup").equals("1")) ? 1 : 0;
    int lottery_signup = (req.getParameter("lottery_signup") != null && req.getParameter("lottery_signup").equals("1")) ? 1 : 0;
    int wait_list_signup = (req.getParameter("wait_list_signup") != null && req.getParameter("wait_list_signup").equals("1")) ? 1 : 0;
    int only_if_guests = (req.getParameter("only_if_guests") != null && req.getParameter("only_if_guests").equals("1")) ? 1 : 0;
    int only_if_unaccomp = (req.getParameter("only_if_unaccomp") != null && req.getParameter("only_if_unaccomp").equals("1")) ? 1 : 0;
    int no_guests = (req.getParameter("no_guests") != null && req.getParameter("no_guests").equals("1")) ? 1 : 0;
    int email_tool_pro = (req.getParameter("email_tool_pro") != null && req.getParameter("email_tool_pro").equals("1")) ? 1 : 0;
    int email_tool_mem = (req.getParameter("email_tool_mem") != null && req.getParameter("email_tool_mem").equals("1")) ? 1 : 0;
    int dining = (req.getParameter("dining") != null && req.getParameter("dining").equals("1")) ? 1 : 0;
    int enabled = (req.getParameter("enabled") != null && req.getParameter("enabled").equals("1")) ? 1 : 0;
    int time_mode = (req.getParameter("time_mode") != null && req.getParameter("time_mode").equals("1")) ? 1 : 0;

    int location_id = Integer.parseInt(req.getParameter("location_id"));


    //
    // Get the content, trim it and make sure there is something to save
    //
    String content = "";

    if (req.getParameter("textfield") != null) {

        content = req.getParameter("textfield");          // from 'name=textfield' in ae.jsp

    } else if (req.getParameter("content") != null) {

        content = req.getParameter("content");            // from tinyMCE Editor (doGet above)
    }

    content = content.trim();

    if (content.equals( "" )) {         // if nothing there

        out.println("<HTML><HEAD><Title>Proshop Save Content</Title>");
        out.println("</HEAD>");
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Error Update Content Failed</H3>");
        out.println("<BR><BR>");
        out.println("The content is missing and the record cannot be saved.  If you wish to delete this item, please disable it or use the delete button.");
        out.println("<BR>Please try again. If problem continues please contact Customer Support.");
        if (!user.equals(DINING_USER)) {
           out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
        } else {
           out.println("<BR><BR><form><input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Return \" onClick='self.close()' alt=\"Close\"></form>");
        }
        out.println("</CENTER></BODY></HTML>");
        return;
    }

    if (req.getParameter("course") != null) {

        courseName = req.getParameter("course");          // course name, if provided
    }

    //
    //  adjust some values for the table
    //
    int smonth = 0;
    int sday = 0;
    int syear = 0;
    int shr = 0;
    int smin = 0;
    int sampm = 0;
    int emonth = 0;
    int eday = 0;
    int eyear = 0;
    int ehr = 0;
    int emin = 0;
    int eampm = 0;

    String temp = req.getParameter("start_ampm");

    try {
        sampm = Integer.parseInt(temp);        // get int value
    }
    catch (NumberFormatException e) {
        // ignore error - let verify catch it
    }

    temp = req.getParameter("end_ampm");

    try {
        eampm = Integer.parseInt(temp);        // get int value
    }
    catch (NumberFormatException e) {
        // ignore error - let verify catch it
    }

    temp = req.getParameter("smonth");

    try {
     smonth = Integer.parseInt(temp);        // get int value
    }
    catch (NumberFormatException e) {
     // ignore error - let verify catch it
    }

    temp = req.getParameter("sday");

    try {
     sday = Integer.parseInt(temp);        // get int value
    }
    catch (NumberFormatException e) {
     // ignore error - let verify catch it
    }

    temp = req.getParameter("syear");

    try {
     syear = Integer.parseInt(temp);        // get int value
    }
    catch (NumberFormatException e) {
     // ignore error - let verify catch it
    }

    temp = req.getParameter("emonth");

    try {
     emonth = Integer.parseInt(temp);        // get int value
    }
    catch (NumberFormatException e) {
     // ignore error - let verify catch it
    }

    temp = req.getParameter("eday");

    try {
     eday = Integer.parseInt(temp);        // get int value
    }
    catch (NumberFormatException e) {
     // ignore error - let verify catch it
    }

    temp = req.getParameter("eyear");

    try {
     eyear = Integer.parseInt(temp);        // get int value
    }
    catch (NumberFormatException e) {
     // ignore error - let verify catch it
    }

    temp = req.getParameter("start_hr");

    try {
     shr = Integer.parseInt(temp);        // get int value
    }
    catch (NumberFormatException e) {
     // ignore error - let verify catch it
    }

    temp = req.getParameter("start_min");

    try {
     smin = Integer.parseInt(temp);        // get int value
    }
    catch (NumberFormatException e) {
     // ignore error - let verify catch it
    }

    temp = req.getParameter("end_hr");

    try {
     ehr = Integer.parseInt(temp);        // get int value
    }
    catch (NumberFormatException e) {
     // ignore error - let verify catch it
    }

    temp = req.getParameter("end_min");

    try {
        emin = Integer.parseInt(temp);        // get int value
    }
    catch (NumberFormatException e) {
        // ignore error - let verify catch it
    }

    long sdate = syear * 10000;       // create a date field from input values
    sdate = sdate + (smonth * 100);
    sdate = sdate + sday;             // date = yyyymmdd (for comparisons)

    long edate = eyear * 10000;       // create a date field from input values
    edate = edate + (emonth * 100);
    edate = edate + eday;             // date = yyyymmdd (for comparisons)

    if (shr == 12 && sampm == 0) shr = 0;
    if (shr < 12 && shr != 0) {                  // _hr specified as 01 - 12 (_ampm = 00 or 12)

        shr = shr + sampm;             // convert to military time (12 is always Noon or PM)
    }

    if (ehr == 12 && eampm == 0) ehr = 0;
    if (ehr < 12 && ehr != 0) {                 // ditto

        ehr = ehr + eampm;
    }

    int stime = shr * 100;
    stime = stime + smin;
    int etime = ehr * 100;
    etime = etime + emin;

    //
    //  verify the date and time fields
    //
    if (sdate > edate) {

        msg = "Sorry, the Start Date and End Date values are incorrect. sdate="+sdate+" edate="+edate;       // error message
        invData(msg, out);    // inform the user and return
        return;
    }
    if (stime > etime) {

        msg = "Sorry, the Start Time and End Time values are incorrect.";       // error message
        invData(msg, out);    // inform the user and return
        return;
    }

    //
    //  Validate minute values
    //
    if ((smin < 0) || (smin > 59)) {

        msg = "Start Minute parameter must be in the range of 00 - 59. " +
              "You entered:" + smin;
        invData(msg, out);    // inform the user and return
        return;
    }

    if ((emin < 0) || (emin > 59)) {

        msg = "End Minute parameter must be in the range of 00 - 59. " +
              "You entered:" + emin;
        invData(msg, out);    // inform the user and return
        return;
    }

    // gets date & time in to mysql format 0000-00-00 00:00:00
    start_datetime = syear + "-" + smonth + "-" + sday + " " + shr + ":" + smin + ":00";
    end_datetime = eyear + "-" + emonth + "-" + eday + " " + ehr + ":" + emin + ":00";

    //out.println("<br>start_datetime="+start_datetime);
    //out.println("<br>end_datetime="+end_datetime);

    //
    //  Validate new name if it has changed
    //
    boolean error = SystemUtils.scanQuote(name);           // check for single quote

    if (error == true) {

        msg = "Apostrophes (single quotes) cannot be part of the Name.";
        invData(msg, out);    // inform the user and return
        return;
    }


    if (reservation + event_signup + lesson_signup + lottery_signup + wait_list_signup + email_tool_pro + email_tool_mem == 0) {     // at least one must be selected

        msg = "You must select either Tee Times, Events, Lesson, Lottery, Wait List, or one of the Email Tool emails in order for this content to be displayed.";
        invData(msg, out);
        return;
    }


    String sql = "";

    if (id == 0) {

        sql = "" +
            "INSERT INTO email_content (" +
                "name, activity_id, location_id, enabled, start_datetime, end_datetime, time_mode, " +
                "reservation, event_signup, lesson_signup, lottery_signup, wait_list_signup, " +
                "email_tool_pro, email_tool_mem, only_if_guests, content, dining, only_if_unaccomp, no_guests, courseName)" +
            "VALUES (" +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    } else {

        sql = "" +
            "UPDATE email_content " +
            "SET " +
                "name = ?, activity_id = ?, location_id = ?, enabled = ?, " +
                "start_datetime = ?, end_datetime = ?, time_mode = ?, " +
                "reservation = ?, event_signup = ?, lesson_signup = ?, lottery_signup = ?, " +
                "wait_list_signup = ?, email_tool_pro = ?, email_tool_mem = ?, " +
                "only_if_guests = ?, content = ?, dining = ?, only_if_unaccomp = ?, no_guests = ?, courseName = ? " +
            "WHERE id = ?";
    }

    // save the content
    try {

        pstmt = con.prepareStatement(sql);

        pstmt.clearParameters();
        pstmt.setString(1, name);
        pstmt.setInt(2, sess_activity_id);
        pstmt.setInt(3, location_id);
        pstmt.setInt(4, enabled);
        pstmt.setString(5, start_datetime);
        pstmt.setString(6, end_datetime);
        pstmt.setInt(7, time_mode);
        pstmt.setInt(8, reservation);
        pstmt.setInt(9, event_signup);
        pstmt.setInt(10, lesson_signup);
        pstmt.setInt(11, lottery_signup);
        pstmt.setInt(12, wait_list_signup);
        pstmt.setInt(13, email_tool_pro);
        pstmt.setInt(14, email_tool_mem);
        pstmt.setInt(15, only_if_guests);
        pstmt.setString(16, content);
        pstmt.setInt(17, dining);
        pstmt.setInt(18, only_if_unaccomp);
        pstmt.setInt(19, no_guests);
        pstmt.setString(20, courseName);

        if (id !=0) pstmt.setInt(21, id);
        pstmt.executeUpdate();

    } catch (Exception exc) {

        out.println("<p>Error saving content! Error: " + exc.getMessage() + "<br>sql="+sql+"</p>");

    } finally {

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    doGet(req, resp);

 } // end doPost


 private String getContent(int id, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String result = "";

    try {

        pstmt = con.prepareStatement("" +
                "SELECT content " +
                "FROM email_content " +
                "WHERE id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            result = rs.getString("content");

        }

    } catch (Exception exc) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return result;
 }


 private void doForecedPreview(HttpServletRequest req, int id, int location_id, String user, String club, int activity_id, PrintWriter out, Connection con) {

    out.println("<html>");
    out.println("<body>");
    out.println("<!--  __BEGIN CONTENT__  -->");

    out.println(getContent(id, con));

    out.println("<!--  __END CONTENT__  -->");

    out.println("<p><hr></p>");


    if (req.getParameter("emailPreview") == null) {

        long sdate = (int)Utilities.getDate(con);
        long s_year = 0;
        long s_month = 0;
        long s_day = 0;
        int hr = 9;
        int min = 0;
        
        s_year = sdate / 10000;                                // get year
        s_month = (sdate - (s_year * 10000)) / 100;            // get month
        s_day = sdate - ((s_year * 10000) + (s_month * 100));  // get day

        String ampm = "AM";

        if (hr > 12) {

            hr = hr - 12;
            ampm = "PM";
        }

        if (hr == 12) {
            ampm = "PM";
        }

        out.println("<form method=\"get\" action=\"Proshop_content\">");
        out.println("<input type=hidden name=id value=\"" + id + "\">");
        out.println("<input type=hidden name=location_id value=\"" + location_id + "\">");
        out.println("<input type=hidden name=emailPreview value=\"1\">");
        out.println("Email Address:&nbsp; <input type=input name=email_address value=\"\"> (only include if you want a copy sent to your email)");
        out.println("<br><br>");
        out.println("<input type=submit name=force_preview value=\" Preview Email \">");
        out.println("&nbsp; &nbsp;");
        out.println("<input type=button onclick=\"javascript:window.close()\" name=edit value=\"  Close  \">");
        out.println("</form>");

    } else {

        out.println("<!--  __BEGIN BUILDING EMAIL__  -->");

        String email = req.getParameter("email_address");
        //if (email.equals("")) email = "paul@mnwebhost.net";

        parmEmail emailParm = new parmEmail();

        emailParm.force_content_id = id;
        emailParm.force_content_location_id = location_id;
        emailParm.out = out;
        emailParm.preview = true;
        emailParm.preview_address = email;
        emailParm.activity_id = activity_id;
        emailParm.emailNew = 1;
        emailParm.club = club;
        emailParm.course = "Members Course";
        emailParm.date = (Utilities.getDate(con));
        emailParm.time = 830;
        emailParm.emailNew = 1;
        emailParm.user = user;
        emailParm.day = "Monday";
        emailParm.player1 = "Jim Adams";
        emailParm.user1 = "6700";
        emailParm.player2 = "Brad Koch";
        emailParm.user2 = "1233";
        emailParm.player3 = "Tom McManus";
        emailParm.user3 = "1234";
        emailParm.player4 = "Bob Parise";
        emailParm.user4 = "1235";
        emailParm.type = "tee";
        emailParm.mm = 4;
        emailParm.dd = 9;
        emailParm.yy = 2011;

        sendEmail.sendIt(emailParm, con);

        out.println("<!--  __END BUILDING EMAIL__  -->");

        out.println("<p>&nbsp;</p><p align=center><button type=button onclick=\"javascript:window.close()\">  Close  </button></p>");

    }

    out.println("</body>");
    out.println("</html>");

 }


 private void doPreview(HttpServletRequest req, int lottery, String user, String club, int activity_id, PrintWriter out, Connection con) {


    out.println(SystemUtils.HeadTitle("ForeTees - Custom Email Content"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    
    if (!user.equals(DINING_USER)) {
       SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    }

    out.println("<table border=\"0\" align=\"center\">");
    out.println("<tr><td align=\"center\">");

    out.println("<table cellpadding=\"5\" border=\"0\" bgcolor=\"#336633\">");
    out.println("<tr><td align=\"center\">");
    out.println("<font color=\"#FFFFFF\" size=\"3\">");
    out.println("<b>Preview Custom Email Content</b><br>");
    out.println("</font>");
    out.println("<font color=\"#FFFFFF\" size=\"2\">");
    out.println("<br>To see exactly how certain emails will look on a given date and time, use the form below to <br>" +
                "specify the parameters needed to test the email type you want then click the 'Preview' button.<br>" +
                "If you wish to receive a copy of the email as well simply enter your email address in the appropriate box.");

    out.println("<br>");
    out.println("</font></td></tr></table><br><br>");

    if (req.getParameter("emailPreview") == null) {

        long sdate = (int)Utilities.getDate(con);
        long s_year = 0;
        long s_month = 0;
        long s_day = 0;
        int hr = 9;
        int min = 0;

        s_year = sdate / 10000;                                // get year
        s_month = (sdate - (s_year * 10000)) / 100;            // get month
        s_day = sdate - ((s_year * 10000) + (s_month * 100));  // get day

        String ampm = "AM";

        if (hr > 12) {

            hr = hr - 12;
            ampm = "PM";
        }

        if (hr == 12) {
            ampm = "PM";
        }

        out.println("<form method=\"get\" action=\"Proshop_content\">");
        //out.println("<input type=hidden name=id value=\"" + id + "\">");
        //out.println("<input type=hidden name=location_id value=\"" + location_id + "\">");
        out.println("<input type=hidden name=emailPreview value=\"1\">");
        out.println("Email Address:&nbsp; <input type=input name=email_address value=\"\"> (only include if you want a copy sent to your email)");
        out.println("<br><br>");
        Common_Config.displayStartDate("Date: ", s_month, s_day, s_year, false, out);
        Common_Config.displayHrMinToD(hr, min, ampm, "Time: ", "hr", "min", "ampm", out);
        out.println("<br><br>");
        out.println("Time Mode:&nbsp; <select size=1 name=time_mode>");
        out.println("<option value=0>Date/Time of Event");
        out.println("<option value=1>Date/Time of Action");
        out.println("</select>");
        out.println("<br><br>");
        out.println("Type of Email:&nbsp; <select size=1 name=type>");
        out.println("<option value=tee>" + ((activity_id == 0) ? "Tee Time" : "Activity Reservation"));
        out.println("<option value=event>Event Signup");
        out.println("<option value=lesson>Lesson Signup");
        out.println("<option value=lottery>Lottery");
        out.println("<option value=waitlist>Wait List (non-event)");
        out.println("<option value=EmailToolPro>Email Tool (Pro)");
        out.println("<option value=EmailToolMem>Email Tool (Mem)");
        out.println("</select>");
        out.println("<br><br>");
        out.println("Guests:&nbsp; <select size=1 name=guests>");
        out.println("<option value=0>No");
        out.println("<option value=1>Yes");
        out.println("</select>");
        out.println("<br><br>");
        out.println("<input type=submit name=preview value=\" Preview Email \">");
        out.println("&nbsp; &nbsp;");
        out.println("<input type=button onclick=\"javascript:window.history.go(-1)\" value=\"  Back  \">");
        out.println("</form>");

    } else {

        out.println("<center><button onclick=\"javascript:window.history.go(-1)\">Preview Another</button></center>");

        out.println("<br><br>");

        out.println("<!--  __BEGIN BUILDING EMAIL__  -->");

        long date = 0;
        int time = 0;
        int hr = 0;
        int min = 0;
        int day = 0;
        int month = 0;
        int year = 0;
        int guests = 0;

        String ampm = req.getParameter("ampm");
        String email = req.getParameter("email_address");
        if (email.equals("")) email = "paul@mnwebhost.net";

        String type = req.getParameter("type");

        String temp = req.getParameter("smonth");
        try {
            month = Integer.parseInt(temp);        // get int value
        } catch (NumberFormatException e) {}

        temp = req.getParameter("sday");
        try {
            day = Integer.parseInt(temp);        // get int value
        } catch (NumberFormatException e) {}

        temp = req.getParameter("syear");
        try {
            year = Integer.parseInt(temp);        // get int value
        } catch (NumberFormatException e) {}

        temp = req.getParameter("hr");
        try {
            hr = Integer.parseInt(temp);        // get int value
        } catch (NumberFormatException e) {}

        temp = req.getParameter("min");
        try {
            min = Integer.parseInt(temp);        // get int value
        } catch (NumberFormatException e) {}

        temp = req.getParameter("guests");
        try {
            guests = Integer.parseInt(temp);        // get int value
        } catch (NumberFormatException e) {}

        if (hr == 12 && ampm.equals("AM")) hr = 0;
        if (hr < 12 && hr != 0 && ampm.equals("PM")) {

            hr = hr + 12;             // convert to military time (12 is always Noon or PM)
        }

        time = (hr * 100) + min;
        date = (year * 10000) + (month * 100) + day;


        parmEmail emailParm = new parmEmail();

        //emailParm.force_content_id = id;
        //emailParm.force_content_location_id = location_id;
        emailParm.out = out;
        emailParm.preview = true;
        emailParm.preview_address = email;
        emailParm.activity_id = activity_id;
        emailParm.emailNew = 1;
        emailParm.club = club;
        emailParm.course = "Members Course";
        emailParm.date = date;
        emailParm.time = time;
        emailParm.emailNew = 1;
        emailParm.user = user;
        emailParm.day = "Monday";
        emailParm.player1 = "Jim Adams";
        emailParm.user1 = "6700";
        if (guests == 0) {
            emailParm.player2 = "Brad Koch";
            emailParm.user2 = "1233";
        } else {
            emailParm.player2 = "Guest Paul Alan";
            emailParm.user2 = "";
            emailParm.userg2 = "6700";
        }
        emailParm.player3 = "Tom McManus";
        emailParm.user3 = "1234";
        emailParm.player4 = "Bob Parise";
        emailParm.user4 = "1235";
        emailParm.type = type;
        emailParm.mm = month;
        emailParm.dd = day;
        emailParm.yy = year;
        emailParm.guests = guests;

        // see if the user wants us to fake the day the email is generated
        if (req.getParameter("time_mode").equals("1")) {
            
            emailParm.force_now_date = (int)date;
            emailParm.force_now_time = time;
            
        }

        sendEmail.sendIt(emailParm, con);

        out.println("<!--  __END BUILDING EMAIL__  -->");

    }

    out.println("</body>");
    out.println("</html>");

 }


 private void doEdit(int id, int sess_activity_id, int lottery, HttpServletRequest req, PrintWriter out, Connection con) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    long sdate = (int)Utilities.getDate(con);
    long edate = sdate;
    long s_year = 0;
    long s_month = 0;
    long s_day = 0;
    long e_year = 0;
    long e_month = 0;
    long e_day = 0;

    int stime = 0;
    int etime = 2359;
    int shr = 0;
    int smin = 0;
    int ehr = 0;
    int emin = 0;
    int multi = 0;

    int enabled = 1;
    int location_id = 0;
    int time_mode = 0;
    int reservation = 0;
    int event_signup = 0;
    int lesson_signup = 0;
    int lottery_signup = 0;
    int wait_list_signup = 0;
    int only_if_guests = 0;
    int only_if_unaccomp = 0;
    int no_guests = 0;
    int email_tool_pro = 0;
    int email_tool_mem = 0;
    int dining = 0;

    String name = "";
    String content = "";
    String courseName = "";

    if (sess_activity_id == 0) {         // if golf user
        
        multi = Utilities.getMulti(con);      // multi courses ?
    }

    try {

        pstmt = con.prepareStatement("" +
                "SELECT *, " +
                    "DATE_FORMAT(start_datetime, '%Y%m%d') AS sdate, DATE_FORMAT(end_datetime, '%Y%m%d') AS edate, " +
                    "DATE_FORMAT(start_datetime, '%k%i') AS stime, DATE_FORMAT(end_datetime, '%k%i') AS etime " +
                "FROM email_content " +
                "WHERE id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            sdate = rs.getLong("sdate");
            stime = rs.getInt("stime");
            edate = rs.getLong("edate");
            etime = rs.getInt("etime");
            name = rs.getString("name");
            content = rs.getString("content");
            enabled = rs.getInt("enabled");
            location_id = rs.getInt("location_id");
            time_mode = rs.getInt("time_mode");
            reservation = rs.getInt("reservation");
            event_signup = rs.getInt("event_signup");
            lesson_signup = rs.getInt("lesson_signup");
            lottery_signup = rs.getInt("lottery_signup");
            wait_list_signup = rs.getInt("wait_list_signup");
            only_if_guests = rs.getInt("only_if_guests");
            email_tool_pro = rs.getInt("email_tool_pro");
            email_tool_mem = rs.getInt("email_tool_mem");
            dining = rs.getInt("dining");
            only_if_unaccomp = rs.getInt("only_if_unaccomp");
            no_guests = rs.getInt("no_guests");
            courseName = rs.getString("courseName");
        }

    } catch (Exception exc) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    //
    //  converted values for common display
    //
    shr = stime / 100;             // get hour
    smin = stime - (shr * 100);   // get minute

    ehr = etime / 100;             // get hour
    emin = etime - (ehr * 100);   // get minute

    s_year = sdate / 10000;                                // get year
    s_month = (sdate - (s_year * 10000)) / 100;            // get month
    s_day = sdate - ((s_year * 10000) + (s_month * 100));  // get day

    e_year = edate / 10000;                                // get year
    e_month = (edate - (e_year * 10000)) / 100;            // get month
    e_day = edate - ((e_year * 10000) + (e_month * 100));  // get day

    String ssampm = "AM";     // AM or PM for display (start hour)
    String seampm = "AM";     // AM or PM for display (end hour)

    if (shr > 12) {

        shr = shr - 12;
        ssampm = "PM";
    }

    if (shr == 12) {
        ssampm = "PM";
    }

    if (ehr > 12) {

        ehr = ehr - 12;
        seampm = "PM";
    }

    if (ehr == 12) {
        seampm = "PM";
    }

    out.println(SystemUtils.HeadTitleEditor("ForeTees - Custom Email Content"));

  //out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/tiny_mce/tiny_mce.js\"></script>");
    //out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/assets/jquery/tiny_mce/tiny_mce.js\"></script>");

    out.println("<script type=\"text/javascript\">");
    out.println("tinyMCE.init({");

    // General options
    out.println("relative_urls : false,");      // convert all URLs to absolute URLs
    out.println("remove_script_host : false,"); // don't strip the protocol and host part of the URLs
    out.println("document_base_url : \"http://www1.foretees.com/\",");
    out.println("valid_children : \"+body[style]\",");
    
    out.println("mode : \"textareas\",");
    out.println("theme : \"advanced\",");
    out.println("plugins : \"safari,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,imagemanager\",");

    // Theme options
    //out.println("theme_advanced_buttons1 : \"save,|,cut,copy,paste,pastetext,pasteword,|,search,replace,|,undo,redo,|,tablecontrols,|,removeformat,visualaid,|,charmap,insertdate,inserttime,emotions,hr,advhr,|,print,|,ltr,rtl,|,fullscreen,|,insertlayer,moveforward,movebackward,absolute,|,iespell,spellchecker\",");
    //out.println("theme_advanced_buttons2 : \"formatselect,fontselect,fontsizeselect,styleprops,|,bold,italic,underline,strikethrough,|,forecolor,backcolor,|,justifyleft,justifycenter,justifyright,justifyfull,|,bullist,numlist,|,outdent,indent,blockquote,|,sub,sup,|,link,unlink,anchor,image,insertimage,|,cleanup,code,preview\",");

    out.println("theme_advanced_buttons1 : \"save,|,cut,copy,paste,pastetext,pasteword,|,undo,redo,|,tablecontrols,|,hr,advhr,|,link,unlink,anchor,image,insertimage,|,code\",");
    out.println("theme_advanced_buttons2 : \"formatselect,fontselect,fontsizeselect,styleprops,|,bold,italic,underline,strikethrough,|,forecolor,backcolor,|,justifyleft,justifycenter,justifyright,justifyfull,|,bullist,numlist,|,outdent,indent,blockquote\",");
    out.println("theme_advanced_buttons3 : \"\",");
    out.println("theme_advanced_buttons4 : \"\",");
    
    out.println("theme_advanced_fonts : \"Andale Mono=andale mono,times;Arial=arial,helvetica,sans-serif;Arial Black=arial black,avant garde;Book Antiqua=book antiqua,palatino;Comic Sans MS=comic sans ms,sans-serif;Courier New=courier new,courier;Georgia=georgia,palatino;Helvetica=helvetica;Impact=impact,chicago;Palatino Linotype=palatino linotype,palatino,book antiqua;Symbol=symbol;Tahoma=tahoma,arial,helvetica,sans-serif;Terminal=terminal,monaco;Times New Roman=times new roman,times;Trebuchet MS=trebuchet ms,geneva;Verdana=verdana,geneva;Webdings=webdings;Wingdings=wingdings,zapf dingbats\",");

    out.println("theme_advanced_toolbar_location : \"top\",");
    out.println("theme_advanced_toolbar_align : \"left\",");
    out.println("theme_advanced_resizing : true,");
    // out.println("theme_advanced_statusbar_location : \"bottom\",");      // we don't need to show the file location info

    // Example content CSS (should be your site CSS)
    // out.println("content_css : \"css/example.css\",");

    // Drop lists for link/image/media/template dialogs
    out.println("template_external_list_url : \"js/template_list.js\",");
    out.println("external_link_list_url : \"js/link_list.js\",");
    out.println("external_image_list_url : \"js/image_list.js\",");
    out.println("media_external_list_url : \"js/media_list.js\",");

    // Replace values for the template plugin
    out.println("template_replace_values : {");
    out.println("username : \"Some User\",");
    out.println("staffid : \"991234\"");


    out.println("}");

    out.println("});");

    out.println("</script>");

    out.println("<style type=\"text/css\"> body {text-align:center} </style>");      // so body will align on center

    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page

    out.println("<form method=post action=\"Proshop_content\">");
    out.println("<input type=hidden name=id value=\"" + id + "\">");
    //out.println("<input type=hidden name=enabled value=\"" + enabled + "\">");



    out.println("<table width=\"640\" cellpadding=\"5\" border=\"1\" bgcolor=\"#336633\" align=\"center\">");
    out.println("<tr><td align=\"center\">");
    out.println("<font color=\"#FFFFFF\" size=\"2\">");

    if (id == 0) {

        out.println("<font size=\"3\"><b>Create New Custom Content</b></font><br>");
        out.println("<br>Complete the form below to create a custom message to be included in outgoing emails.&nbsp; Make sure to select all the types of emails this content should be included in.");
        out.println("<br>Click on <b>Create Content</b> to submit the changes.");

    } else {

        out.println("<font size=\"3\"><b>Edit Custom Content</b></font><br>");
        out.println("<br>Change the desired information for the custom content below.");
        out.println("<br>Click on <b>Save Changes</b> to submit the changes.");
        out.println("<br>To disable this content from being used, uncheck the <b>Enabled</b> box and click <b>Save Changes</b>.");

    }

    /*
    out.println("<br><br><b>Time Booked</b> means that the date & time you specify below must match the date & time the reservation was booked for, whereas <b>Day Made</b> means that the date & time you specify " +
            "below must match the date & time the reservation was made.<br><br>Example:  Supposed you create Content to be displayed on the 4th of July.  If you choose Time Booked then " +
            "the content would be included in emails for tee times book for that day.  So a member making a reservation on June 22nd for the 4th of July would see that content in their confirmation email. " +
            "However if you choose Day Made then any member making reservations on the 4th of July, regardless of when the reservation is actually for, would see that content in their confirmation email.");
     */

    out.println("</font></td></tr></table><br>");

    out.println("<table width=\"740\" border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\">");

    out.println("<tr><td>"); // 640
    out.println("<font size=\"2\">");
    out.println("<p align=\"left\">&nbsp;&nbsp;Name for Content:&nbsp;&nbsp;&nbsp;");
    out.println("<input type=\"text\" name=\"name\" value=\"" + name + "\" size=\"30\" maxlength=\"30\">");

    out.println("<br>&nbsp;&nbsp;&nbsp;* Must be unique");
    out.println("<br><br>");

    out.println("&nbsp;&nbsp;Enabled:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\"" + ((enabled != 0) ? " checked" : "") + " name=\"enabled\" value=\"1\">");

    out.println("<br><br>");
    out.println("&nbsp;&nbsp;Location:");
    out.println("<select size=1 name=location_id>");
    if (location_id == 0) out.println("<option value=0>CHOOSE...");
    Common_Config.buildOption(1, "Right Side", location_id, out);
    Common_Config.buildOption(2, "Lower Span", location_id, out);
    out.println("</select>");

    // include date mode options
    out.println("<br><br>");
    out.println("&nbsp;&nbsp;Time Mode:");
    out.println("<input type=radio name=time_mode value=\"0\"" + ((time_mode == 0) ? " checked" : "") + ">&nbsp;&nbsp;Date/Time of Event");
    out.println("<input type=radio name=time_mode value=\"1\"" + ((time_mode == 1) ? " checked" : "") + ">&nbsp;&nbsp;Date/Time of Action &nbsp; &nbsp; ");
       out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_custom_timemode.pdf', 'newwindow', 'Height=460, width=650, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no')\">");
       out.println("What do these do?</a>");
    out.println("<br><br>");


    Common_Config.displayStartDate(s_month, s_day, s_year, out);  // display the Start Date prompt

    Common_Config.displayEndDate(e_month, e_day, e_year, out);    // display the End Date prompt

    Common_Config.displayStartTime(shr, smin, ssampm, out);       // display the Start Time prompt

    Common_Config.displayEndTime(ehr, emin, seampm, out);         // display the End Time prompt
    
    if (sess_activity_id == 0 && multi > 0) {                    // if golf user and multi courses
        
        if (courseName.equals("")) {
            
            courseName = "-ALL-";           // default to ALL if no course selected yet
        }
        
        Common_Config.displayCourseSelection(courseName, true, con, out);         // display the Course prompt
    }

    out.println("&nbsp;&nbsp;Display For (select all that apply):<br>");

    out.println("<br>");
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    out.println("<input type=\"checkbox\"" + ((reservation != 0) ? " checked" : "") + " name=\"reservation\" value=\"1\">&nbsp;&nbsp;" + (sess_activity_id == 0 ? "Tee Times - include when sending tee time confirmations" : "Reservations - include when sending out reservation confirmations"));

    out.println("<br>");
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    out.println("<input type=\"checkbox\"" + ((event_signup != 0) ? " checked" : "") + " name=\"event_signup\" value=\"1\">&nbsp;&nbsp;Events - include when sending event registration confirmations");

    out.println("<br>");
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    out.println("<input type=\"checkbox\"" + ((lesson_signup != 0) ? " checked" : "") + " name=\"lesson_signup\" value=\"1\">&nbsp;&nbsp;Lessons - include when sending lesson signup confirmations");

    if (sess_activity_id == 0) {
        out.println("<br>");
        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        out.println("<input type=\"checkbox\"" + ((lottery_signup != 0) ? " checked" : "") + " name=\"lottery_signup\" value=\"1\">&nbsp;&nbsp;Lottery - include when sending lottery registrations and subsequent tee time confirmations");

        out.println("<br>");
        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        out.println("<input type=\"checkbox\"" + ((wait_list_signup != 0) ? " checked" : "") + " name=\"wait_list_signup\" value=\"1\">&nbsp;&nbsp;Wait Lists - include when sending wait list confirmations");
    } else {
        out.println("<input type=\"hidden\" name=\"lottery_signup\" value=\"0\">");
        out.println("<input type=\"hidden\" name=\"wait_list_signup\" value=\"0\">");
    }
    out.println("<br>");
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    out.println("<input type=\"checkbox\"" + ((email_tool_pro != 0) ? " checked" : "") + " name=\"email_tool_pro\" value=\"1\">&nbsp;&nbsp;Email Tool (pro) - include when sending emails to the membership");

    out.println("<br>");
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    out.println("<input type=\"checkbox\"" + ((email_tool_mem != 0) ? " checked" : "") + " name=\"email_tool_mem\" value=\"1\">&nbsp;&nbsp;Email Tool (mem) - include when members send emails to one another");

    // COMMENT OUT UNTIL WE IMPLEMENT CUSTOM CONTENT IN OUTGOING DINING EMAILS
/*
    out.println("<br>");
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    out.println("<input type=\"checkbox\"" + ((dining != 0) ? " checked" : "") + " name=\"dining\" value=\"1\">&nbsp;&nbsp;Dining - include when sending dining reservation confirmations");
*/
    out.println("<br><br>");
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                "NOTE: &nbsp;If you are using the Guest Tracking feature and gather the email addresses of your guests, we can include this <br>" +
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                "content in the reservation notifications sent to your guests. &nbsp;Please use the following options to determine <br>" +
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                "when to include this content for your guests. &nbsp;By default, this content will be included in reservation <br>" +
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                "notifications sent to members and guests regardless of others in the reservation (if Tee Times, Events,<br>" +
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                "Lottery, or Wait Lists selected above).");
    out.println("<br><br>");
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    out.println("&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Only With Guests - select one or both of the options below to include this <strong>only</strong> if the reservation includes guests:");
    out.println("<br>");
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\"" + ((only_if_guests != 0) ? " checked" : "") + " name=\"only_if_guests\" value=\"1\">&nbsp;&nbsp;Include only if reservation contains both members and guests (do not include if members only or if guests only)");
    out.println("<br>");
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\"" + ((only_if_unaccomp != 0) ? " checked" : "") + " name=\"only_if_unaccomp\" value=\"1\">&nbsp;&nbsp;Include only if the reservation contains guests with no members (unaccompanied)");
    out.println("<br>");
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    out.println("- OR -<br>");
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\"" + ((no_guests != 0) ? " checked" : "") + " name=\"no_guests\" value=\"1\">&nbsp;&nbsp;DO NOT include in notifications sent to guests");

    out.println("<br><br>");
    out.println("<br>&nbsp;&nbsp;Content:");

    out.println("<center><textarea name=\"content\" style=\"width:480px;height:240px\">");

    out.println(content);

    out.println("</textarea></center>");

    out.println("</font></td></tr>");
    out.println("<tr><td>");
    
    out.println("<BR><p align=center><font size=\"3\">Click here to <a href=\"Proshop_event_list\" target=\"_blank\">Get a Link URL for an Event</a></font></p>");  
    
    out.println("<p align=center><font size=\"3\">Click here for <a href=\"/" +rev+ "/web utilities/tiny_mce/TinyMCE-Content-User-Guide.pdf\" target=\"_blank\">Help with the Editor</a></font></p><BR>");  
    
    out.println("</font></td></tr></table>");
    
    out.println("</td></tr></table>");                       // end of main page table

    // save button
    out.println("<table width=\"300\" border=\"0\" align=\"center\"><tr><td align=\"left\">");
    out.println("<input type=submit value=\"" + ((id == 0) ? " Create Content " : " Save Changes ") + "\" style=\"background-color: #8B8970\">");
    out.println("</td></form>");

    // cancel button
    out.println("<form method=get action=\"Proshop_content\">");
    out.println("<td align=\"center\">");
    out.println("<input type=submit value=\" Cancel - Return w/o Changes \" style=\"background-color: #8B8970\">");
    out.println("</td></form>");

    // delete button
    if (id != 0) {

        out.println("<form method=get action=\"Proshop_content\"><input type=hidden name=id value=\"" + id + "\">");
        out.println("<td align=\"right\">");
        out.println("<input type=submit name=delete value=\" Delete Content \" style=\"background-color: #8B8970\" " +
                "onclick=\"return confirm('Are you sure you want to permenatly delete this content?/n/nThis action can not be undone!');\">");
        out.println("</td></form>");

    }

    out.println("</tr></table>");

    out.println("</body>");
    out.println("</html>");

 }


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



private String buildContent(int id, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String result = "";
    String content = "";
    String width = "";
    String height = "";

    int min_height_px = 0;
    int min_width_px = 0;
    int height_pct = 0;
    int width_pct = 0;

    try {

        pstmt = con.prepareStatement("" +
                "SELECT c.content, cl.* " +
                "FROM email_content c, content_locations cl" +
                "WHERE c.id = ? AND c.location_id = cl.location_id");

        pstmt.clearParameters();
        pstmt.setInt(1, id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            content = rs.getString("content");
            min_height_px = rs.getInt("min_height_px");
            min_width_px = rs.getInt("min_width_px");
            height_pct = rs.getInt("height_pct");
            width_pct = rs.getInt("width_pct");

        }

    } catch (Exception exc) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    /*
     *
     * Capabilities:
     * 1. Build standard container with no sizing information
     * 2. Build container with fixed width and/or height
     * 3. Build container with a forced minimum width and/or height with optional percentage width/height
     *
     *
     if no minimums set don't implement
     *
     *
     */

    // first determin the width config

    if (width_pct == 0 && min_width_px == 0) {
        
        result = "<div ";
        
    } else if (width_pct != 0 && min_width_px == 0) {

        result = "<table width=\"" + width_pct + "%\" ";
        if (height_pct != 0) {
            result += "height=\"" + height_pct + "%\" ";
        } else if (min_width_px != 0) {
            result += "height=\"" + min_width_px + "px\" ";
        }
        //width = "width:"+width_pct+"px";

    } else if (width_pct != 0 && min_width_px != 0) {

        result = "<table ";
        width = "width:"+width_pct+"px";

    }

    result += content + "</div>";


    return result;

 }

} // end servlet public class

/*
 * Define the size and whatever else of the various content locations
 * then we can use that for the preview
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */