/***************************************************************************************
 *   Proshop_waitlist:  This servlet will allow members to submit a wait list request
 *                     as well as process the request.
 *
 *
 *   called by:  Proshop_sheet (doPost)
 *               
 *
 *
 *   created: 4/19/2008   Paul S
 *
 *   last updated:       ******* keep this accurate ******
 *
 *        10/20/08  Add Member Access to Tee Sheet option
 *         8/11/08  Update to limited access user restrictions
 *         8/01/08  Added limited access proshop users checks
 *        10/16/08  Changed auto_assign (was unused) to member_view_teesheet
 *
 *
 ***************************************************************************************
 */


import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


// foretees imports
import com.foretees.common.parmWaitList;
import com.foretees.common.getWaitList;



public class Proshop_waitlist extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*****************************************************
 // Process the request from Member_sheet
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    PreparedStatement pstmt3 = null;
    Statement stmt = null;
    ResultSet rs = null;

    HttpSession session = SystemUtils.verifyPro(req, out);      // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);               // get DB connection

    if (con == null) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, please contact customer support.");
        out.println("<BR><BR>");
        out.println("<font size=\"2\">");
        out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");
        return;
    }
    
    // check what access rights the current proshop user has
    boolean viewAccess = SystemUtils.verifyProAccess(req, "WAITLIST_VIEW", con, out);
    boolean updateAccess = SystemUtils.verifyProAccess(req, "WAITLIST_UPDATE", con, out);
    boolean manageAccess = SystemUtils.verifyProAccess(req, "WAITLIST_MANAGE", con, out);
    boolean sysConfigAccess = SystemUtils.verifyProAccess(req, "SYSCONFIG_WAITLIST", con, out);
    
    int multi = 0;              // multiple course support indicator

    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);

    out.println(SystemUtils.HeadTitle(""));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    
    
    //
    // Make sure the club is setup and there is at least one course configured
    //
    try {
      
        boolean tmp_test = false;
        int tmp_count = 0;

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT multi, (SELECT COUNT(*) FROM clubparm2 AS c) FROM club5 WHERE clubName != ''");

        if (rs.next()) {

            multi = rs.getInt(1);
            tmp_count = rs.getInt(2);

        } else {

            tmp_test = true;

        }

        if (tmp_count == 0) tmp_test = true;

        stmt.close();

        if (tmp_test) {

            // not setup yet - inform user to start with club setup 

            out.println(SystemUtils.HeadTitle("Sequence Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Setup Sequence Error</H3>");
            out.println("<BR><BR>Sorry, we are unable to process your request at this time.");
            out.println("<BR>The club setup has not been completed.");
            out.println("<BR>Please return to Configuration and select Club Setup.");
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
            out.println("</CENTER></BODY></HTML>");
            return;
        }

    } catch (Exception exc) {

        out.println(SystemUtils.HeadTitle("Database Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Database Access Error</H3>");
        out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
        out.println("<BR>Please try again later.");
        out.println("<BR><br>Exception: " + exc.getMessage());
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
        out.println("</CENTER></BODY></HTML>");
        return;

    }
    
    //
    // See if we are here to either make a NEW wait list or EDIT an existing one
    //
    if (req.getParameter("new") != null || (req.getParameter("edit") != null && req.getParameter("waitListId") != null)) {
        
        if (!sysConfigAccess) {                                  // Make sure user has appropriate access
            SystemUtils.restrictProshop("SYSCONFIG_WAITLIST", out);      // User doesn't have access, reject them
            return;
        } else {
            /*
            String club = (String)session.getAttribute("club");
            
            if (Common_Server.SERVER_ID == 4 || club.equals("ironwood") || club.startsWith("demo")) {
            */
                waitListConfig(req, out, con);
                return;
            /*
            } else {
                
                out.println("<center><h3>The Wait List feature is not yet active for all clubs.  This " +
                        "feature is currently being beta tested.  If you are interested in testing this " +
                        "feature at your club, please contact our pro-support staff at " +
                        "<a href=\"mailto:prosupport@foretees.com\">prosupport@foretees.com</a>.<br><br>Thank you.</h3></center>");
                out.close();
                return;
                
            }
            */
        }
    }
    
    //
    // See if we are here to delete an existing wait list
    //
    if (req.getParameter("delete") != null && req.getParameter("waitListId") != null) {
        
        if (!sysConfigAccess) {                                  // Make sure user has appropriate access
            SystemUtils.restrictProshop("SYSCONFIG_WAITLIST", out);      // User doesn't have access, reject them
            return;
        } else {
            
            int wait_list_id = 0;

            // delete the list
            try {

                wait_list_id = Integer.parseInt(req.getParameter("waitListId"));

                // delete the wait list
                PreparedStatement pstmt = con.prepareStatement ( "DELETE FROM wait_list WHERE wait_list_id = ?" );
                pstmt.clearParameters();
                pstmt.setInt(1, wait_list_id);
                pstmt.executeUpdate();

                // delete signups
                pstmt = con.prepareStatement ( "DELETE FROM wait_list_signups WHERE wait_list_id = ?" );
                pstmt.clearParameters();
                pstmt.setInt(1, wait_list_id);
                pstmt.executeUpdate();

                // delete players from the signups
                stmt = con.createStatement();
                stmt.executeUpdate( "DELETE FROM wait_list_signups_players WHERE wait_list_signup_id NOT IN (SELECT wait_list_signup_id FROM wait_list_signups)" );
                stmt.close();

            } catch (Exception e) {

                String club = (String)session.getAttribute("club");
                out.println("<center><h3>Unable to delete list due to internal error.  Error was " + e.toString() + "</h3></center>");
                SystemUtils.logError("Proshop_waitlist: Error deleting wait list id# " + wait_list_id + " for club " + club + ". Err=" + e.toString());
            }

            out.println("<h3 align=center>The wait list was been deleted.</h3>");

            // return to listing
            buildList(multi, out, con);
            return;
        }
    }
    
    //
    // See if we are here to SELECT a wait list to edit
    //
    if (req.getParameter("edit") != null && req.getParameter("waitListId") == null) {
        
        if (!sysConfigAccess) {                                  // Make sure user has appropriate access
            SystemUtils.restrictProshop("SYSCONFIG_WAITLIST", out);      // User doesn't have access, reject them
            return;
        } else {
            buildList(multi, out, con);
            return;
        }
    }
        
    //
    // See if we are here to VIEW a wait list
    //
    if (req.getParameter("view") != null && req.getParameter("waitListId") != null) {
        
        if (!viewAccess) {                                  // Make sure user has appropriate access
            SystemUtils.restrictProshop("WAITLIST_VIEW", out);      // User doesn't have access, reject them
            return;
        } else {
            viewSignups(req, multi, out, con);
            return;
        }
    }
    
    String jump = "0";                                          // jump index - default to zero (for _sheet)

    if (req.getParameter("jump") != null) {                     // if jump index provided

        jump = req.getParameter("jump");
    }
    
    String sindex = req.getParameter("index");                  //  index value of day (needed by Proshop_waitlist_slot when returning)
    String id = req.getParameter("waitListId");                 //  uid of the wait list we are working with

    String returnCourse = "";
    
    if (req.getParameter("returnCourse") != null) {             // if returnCourse provided

        returnCourse = req.getParameter("returnCourse");
    }


    String sdate = req.getParameter("date");                    //  date of the request (yyyymmdd)
    String day_name = req.getParameter("day");                  //  name of the day
    String p5 = req.getParameter("p5");                         //  5-somes supported
    String sshr = "";
    String ssmin = "";
    String sampm = "";
    String sehr = "";
    String semin = "";
    String eampm = "";

    int shr = 0;
    int smin = 0;
    int ehr = 0;
    int emin = 0;
    int index = 0;
    int wait_list_id = 0;
    int count = 0;
    
    int mm = 0;
    int dd = 0;
    int yy = 0;
    int date = 0;

    int time = SystemUtils.getTime(con);
    
    //
    //  Convert the values from string to int
    //
    try {
        
        wait_list_id = Integer.parseInt(id);
        index = Integer.parseInt(sindex);
        date = Integer.parseInt(sdate);
        shr = Integer.parseInt(sshr);
        smin = Integer.parseInt(ssmin);
        ehr = Integer.parseInt(sehr);
        emin = Integer.parseInt(semin);
        
    } catch (NumberFormatException e) { }

    // get our date parts
    yy = date / 10000;
    mm = date - (yy * 10000);
    dd = mm - (mm / 100) * 100;
    mm = mm / 100;
      
    
    //
    //  parm block to hold the wait list parameters
    //
    parmWaitList parmWL = new parmWaitList();                   // allocate a parm block
    
    parmWL.wait_list_id = wait_list_id;
    
    try {
        
        getWaitList.getParms(con, parmWL);                      // get the wait list config
        
        count = getWaitList.getListCount(wait_list_id, date, index, time, true, con);
        
    } catch (Exception exp) {
        out.println(exp.getMessage());
        return;
    }
    
    out.println("<!-- wait_list_id=" + wait_list_id + ", date=" + date + ", count=" + count + " -->");
    
/*    
    //
    //********************************************************************
    //   Build a page to display Wait List summary and provide links to manage and add signups
    //********************************************************************
    //
    out.println("<html>");
    out.println("<head>");
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
    out.println("<title>Wait List Registration Page</title>");
    out.println("</head>");

    out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

    out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
    out.println("<tr><td valign=\"top\" align=\"center\">");

    out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#336633\" align=\"center\" valign=\"top\">");
    out.println("<tr><td align=\"left\" width=\"300\">&nbsp;");
    out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
    out.println("</td>");

    out.println("<td align=\"center\">");
    out.println("<font color=\"ffffff\" size=\"5\">Wait List Registration</font>");
    out.println("</font></td>");

    out.println("<td align=\"center\" width=\"300\">");
    out.println("<font size=\"1\" color=\"#ffffff\">Copyright&nbsp;</font>");
    out.println("<font size=\"2\" color=\"#ffffff\">&#169;&nbsp;</font>");
    out.println("<font size=\"1\" color=\"#ffffff\">ForeTees, LLC <br> 2008 All rights reserved.");
    out.println("</font></td>");
    out.println("</tr></table>");

    out.println("<br>");

    out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
    out.println("<tr>");
    out.println("<td width=\"620\" align=\"center\">");
    out.println("<font size=\"3\">");
    out.println("<b>Wait List Registration</b><br></font>");
    out.println("<font size=\"2\">");
    
    out.println("The golf shop is running a wait list " + ((index == 0) ? "today": "on this day") + ". ");
    out.println("The wait list you've selected is running from " + SystemUtils.getSimpleTime(parmWL.start_time) + " till " + SystemUtils.getSimpleTime(parmWL.end_time) + ". ");
    
    out.println("Review the information below and click on 'Continue With Request' to contnue.");
    out.println("<br>OR click on 'Cancel Request' to delete the request. To return without changes click on 'Go Back'.");

    //out.println("<br><br><b>NOTE:</b> Only the person that originates the request will be allowed to cancel it or change these values.");

    out.println("</font></td></tr>");
    out.println("</table>");
*/
    
    out.println("<center><h3>" + ((!parmWL.name.equals("")) ? parmWL.name : "Wait List Information") + "</h3></center>");
    
    out.println("<br>");

    out.println("<table border=0 align=center>");
    
    out.println("<tr><td><font size=\"2\">");
    out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b></td>");
    out.println("<td>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</td><td>");
    if (!parmWL.course.equals( "" )) {
        out.println("<font size=\"2\">Course:&nbsp;&nbsp;<b>" + parmWL.course + "</b></font>");
    }
    out.println("</td></tr>");
    
    out.println("<tr><td><font size=\"2\">Time:&nbsp;&nbsp;<b>" + SystemUtils.getSimpleTime(parmWL.start_time) + " to " + SystemUtils.getSimpleTime(parmWL.end_time) + "</b></font></td>");
    
    out.println("<td></td>");
    
    out.println("<td><font size=\"2\">Signups:&nbsp;&nbsp;<b>" + count + "</b></font></td>");
    
    out.println("</table>");
/*    
    out.println("<br>");
    
    out.println("<table border=\"0\" align=\"center\">"); // table to contain 2 tables below

    out.println("<tr>");

    
    out.println("<td align=\"center\" valign=\"top\">");

    out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" align=\"center\" width=\"500\" cellpadding=\"5\" cellspacing=\"5\">");  // table for request details
    out.println("<tr bgcolor=\"#336633\"><td align=\"center\" colspan=\"2\">");
    out.println("<font color=\"ffffff\" size=\"3\">");
    out.println("<b>" + ((!parmWL.name.equals("")) ? parmWL.name : "Wait List Information") + "</b>");
    out.println("</font></td></tr>");
    
    out.println("<tr>");
    
    out.println("<td align=center><font size=\"2\">");
*/
    
    String tmp_style = "style=\"width: 200px\"";
    
    out.println("<br><br>");
    
    out.println("<center>");
    
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_waitlist_slot\" method=\"POST\" target=\"_top\">");
    out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + wait_list_id + "\">");
    out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
    out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
    out.println("<input type=\"hidden\" name=\"index\" value=\"" + sindex + "\">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + parmWL.course + "\">");
    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
    out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
    out.println("<input type=submit value=\"Create New Sign-up\" name=\"continue\" " + tmp_style + " " + ((updateAccess) ? "" : "disabled") + ">");
    out.println("</form>");
    
    
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"GET\" target=\"_top\">");
    out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
    out.println("<input type=\"hidden\" name=\"name\" value=\"" + parmWL.name + "\">");
    out.println("<input type=\"hidden\" name=\"hide\" value=\"1\">");
    out.println("<input type=\"hidden\" name=\"index\" value=\"" + sindex + "\">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + parmWL.course + "\">");
    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
    out.println("<input type=submit value=\"Manage Wait List\" name=\"manage\" " + tmp_style + " " + ((count > 0 && manageAccess) ? "" : "disabled") + ">");
    out.println("</form>");
    
    
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_waitlist\" method=\"GET\">");
    out.println("<input type=\"hidden\" name=\"view\" value=\"current\">");
    out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + wait_list_id + "\">");
    out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
    out.println("<input type=\"hidden\" name=\"name\" value=\"" + parmWL.name + "\">");
    //out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
    out.println("<input type=\"hidden\" name=\"index\" value=\"" + sindex + "\">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + parmWL.course + "\">");
    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
    out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");;
    out.println("<input type=\"hidden\" name=\"start_time\" value=\"" + parmWL.start_time + "\">");
    out.println("<input type=\"hidden\" name=\"end_time\" value=\"" + parmWL.end_time + "\">");
    out.println("<input type=\"hidden\" name=\"day_name\" value=\"" + day_name + "\">");
    out.println("<input type=submit value=\"View Sign-ups\" name=\"view\" " + tmp_style + " " + ((count > 0 && viewAccess) ? "" : "disabled") + ">");
    out.println("</form>");
      
    
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"POST\" target=\"_top\">");
    out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
    out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + ((!returnCourse.equals( "" )) ? returnCourse : parmWL.course) + "\">");
    out.println("<input type=\"submit\" value=\"Return to Tee Sheet\" name=\"cancel\" " + tmp_style + ">");
    out.println("</form>");
      
    out.println("</center>");
    
 } // end doGet

 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
 
     
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyPro(req, out);      // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);               // get DB connection

    if (con == null) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, please contact customer support.");
        out.println("<BR><BR>");
        out.println("<font size=\"2\">");
        out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");
        return;
    }
    
    String sql = "";
    String sdatetime = "";
    String edatetime = "";
    
    String id = (req.getParameter("wait_list_id") == null) ? "0" : req.getParameter("wait_list_id");
    String name = (req.getParameter("name") == null) ? "" : req.getParameter("name").trim();
    String course = (req.getParameter("course") == null) ? "" : req.getParameter("course");
    String color = (req.getParameter("color") == null) ? "" : req.getParameter("color");
    
    int sun = (req.getParameter("sun") == null) ? 0 : 1;
    int mon = (req.getParameter("mon") == null) ? 0 : 1;
    int tue = (req.getParameter("tue") == null) ? 0 : 1;
    int wed = (req.getParameter("wed") == null) ? 0 : 1;
    int thu = (req.getParameter("thu") == null) ? 0 : 1;
    int fri = (req.getParameter("fri") == null) ? 0 : 1;
    int sat = (req.getParameter("sat") == null) ? 0 : 1;
    
    String tmp_smonth = (req.getParameter("smonth") == null) ? "0" : req.getParameter("smonth");
    String tmp_sday = (req.getParameter("sday") == null) ? "0" : req.getParameter("sday");
    String tmp_syear = (req.getParameter("syear") == null) ? "0" : req.getParameter("syear");
    String tmp_emonth = (req.getParameter("emonth") == null) ? "0" : req.getParameter("emonth");
    String tmp_eday = (req.getParameter("eday") == null) ? "0" : req.getParameter("eday");
    String tmp_eyear = (req.getParameter("eyear") == null) ? "0" : req.getParameter("eyear");
    
    String tmp_start_hr = (req.getParameter("start_hr") == null) ? "0" : req.getParameter("start_hr");
    String tmp_start_min = (req.getParameter("start_min") == null) ? "0" : req.getParameter("start_min").trim();
    String tmp_end_hr = (req.getParameter("end_hr") == null) ? "0" : req.getParameter("end_hr");
    String tmp_end_min = (req.getParameter("end_min") == null) ? "0" : req.getParameter("end_min").trim();
    String tmp_cutoff_hr = (req.getParameter("cutoff_hr") == null) ? "0" : req.getParameter("cutoff_hr");
    String tmp_cutoff_min = (req.getParameter("cutoff_min") == null) ? "0" : req.getParameter("cutoff_min").trim();
    String tmp_cutoff_days = (req.getParameter("cutoff_days") == null) ? "0" : req.getParameter("cutoff_days");
    
    String start_ampm = (req.getParameter("start_ampm") == null) ? "00" : req.getParameter("start_ampm");
    String end_ampm = (req.getParameter("end_ampm") == null) ? "00" : req.getParameter("end_ampm");
    String cutoff_ampm = (req.getParameter("cutoff_ampm") == null) ? "AM" : req.getParameter("cutoff_ampm");
    
    String smax_list_size = (req.getParameter("max_list_size") == null) ? "0" : req.getParameter("max_list_size");
    String smax_team_size = (req.getParameter("max_team_size") == null) ? "0" : req.getParameter("max_team_size");
    String smember_access = (req.getParameter("member_access") == null) ? "0" : req.getParameter("member_access");
    String smember_view = (req.getParameter("member_view") == null) ? "0" : req.getParameter("member_view");
    String smember_view_teesheet = (req.getParameter("member_view_teesheet") == null) ? "0" : req.getParameter("member_view_teesheet");
    String sallow_guests = (req.getParameter("allow_guests") == null) ? "0" : req.getParameter("allow_guests");
    String sallow_x = (req.getParameter("allow_x") == null) ? "0" : req.getParameter("allow_x");
    String senabled = (req.getParameter("enabled") == null) ? "0" : req.getParameter("enabled");
    String notice = (req.getParameter("notice") == null) ? "" : req.getParameter("notice").trim();
    
    int wait_list_id = 0;
    int start_date = 0;
    int start_time = 0;
    int end_date = 0;
    int end_time = 0;
    int max_list_size = 0;
    int max_team_size = 0;
    int member_access = 0;
    int member_view = 0;
    int member_view_teesheet = 0;
    int allow_guests = 0;
    int allow_x = 0;
    int enabled = 0;
    
    int smonth = 0;
    int sday = 0;
    int syear = 0;
    int emonth = 0;
    int eday = 0;
    int eyear = 0;
    int start_hr = 0;
    int start_min = 0;
    int end_hr = 0;
    int end_min = 0;
    int cutoff_hr = 0;
    int cutoff_min = 0;
    int cutoff_time = 0;
    int cutoff_days = 0;
    
    
    try {
        
        wait_list_id = Integer.parseInt(id);
        smonth = Integer.parseInt(tmp_smonth);
        sday = Integer.parseInt(tmp_sday);
        syear = Integer.parseInt(tmp_syear);
        emonth = Integer.parseInt(tmp_emonth);
        eday = Integer.parseInt(tmp_eday);
        eyear = Integer.parseInt(tmp_eyear);
        start_hr = Integer.parseInt(tmp_start_hr);
        start_min = Integer.parseInt(tmp_start_min);
        end_hr = Integer.parseInt(tmp_end_hr);
        end_min = Integer.parseInt(tmp_end_min);
        cutoff_hr = Integer.parseInt(tmp_cutoff_hr);
        cutoff_min = Integer.parseInt(tmp_cutoff_min);
        cutoff_days = Integer.parseInt(tmp_cutoff_days);
        
        max_list_size = Integer.parseInt(smax_list_size);
        max_team_size = Integer.parseInt(smax_team_size);
        member_access = Integer.parseInt(smember_access);
        member_view = Integer.parseInt(smember_view);
        member_view_teesheet = Integer.parseInt(smember_view_teesheet);
        allow_guests = Integer.parseInt(sallow_guests);
        allow_x = Integer.parseInt(sallow_x);
        enabled = Integer.parseInt(senabled);
        
    }
    catch (NumberFormatException e) { }
    
    
    start_date = syear * 10000;                 // create a date field from input values
    start_date = start_date + (smonth * 100);
    start_date = start_date + sday;             // date = yyyymmdd (for comparisons)
    
    end_date = eyear * 10000;                   // create a date field from input values
    end_date = end_date + (emonth * 100);
    end_date = end_date + eday;                 // date = yyyymmdd (for comparisons)
        
    if (cutoff_ampm.equals("PM") && cutoff_hr != 12) cutoff_hr = cutoff_hr + 12;
    if (cutoff_ampm.equals("AM") && cutoff_hr == 12) cutoff_hr = 0;
    cutoff_time = cutoff_hr * 100;
    cutoff_time = cutoff_time + cutoff_min;
    
    if (start_ampm.equals("12") && start_hr != 12) start_hr = start_hr + 12;
    if (start_ampm.equals("00") && start_hr == 12) start_hr = 0;
    start_time = start_hr * 100;
    start_time = start_time + start_min;
    
    if (end_ampm.equals("12") && end_hr != 12) end_hr = end_hr + 12;
    if (end_ampm.equals("00") && end_hr == 12) end_hr = 0;
    end_time = end_hr * 100;
    end_time = end_time + end_min;
                
    sdatetime = "" + syear + "-" + smonth + "-" + sday + " " + start_hr + ":" + start_min + ":00";
    edatetime = "" + eyear + "-" + emonth + "-" + eday + " " +  end_hr  + ":" +  end_min  + ":00";
    
    //
    //  parm block to hold the wait list parameters
    //
    parmWaitList parmWL = new parmWaitList();                   // allocate a parm block
    
    parmWL.wait_list_id = wait_list_id;
    parmWL.name = name;
    parmWL.course = course;
    parmWL.color = color;
    parmWL.start_date = start_date;
    parmWL.start_time = start_time;
    parmWL.end_date = end_date;
    parmWL.end_time = end_time;
    parmWL.cutoff_days = cutoff_days;
    parmWL.cutoff_time = cutoff_time;
    parmWL.sunday = sun;
    parmWL.monday = mon;
    parmWL.tuesday = tue;
    parmWL.wednesday = wed;
    parmWL.thursday = thu;
    parmWL.friday = fri;
    parmWL.saturday = sat;
    parmWL.max_list_size = max_list_size;
    parmWL.max_team_size = max_team_size;
    parmWL.member_access = member_access;
    parmWL.member_view = member_view;
    parmWL.member_view_teesheet = member_view_teesheet;
    parmWL.allow_guests = allow_guests;
    parmWL.allow_x = allow_x;
    parmWL.enabled = enabled;
    
    
    //
    // START VERIFICATION
    
    
    //
    // Make sure there is a name 
    //
    if (parmWL.name.equals("")) {
        
        buildForm(req, parmWL, "Must specify a name for the wait list.", out, con);
        return;
    }
    
    //
    // Make sure the name is unique
    //
    try {
        
        if (wait_list_id == 0) { // or if editing and name is different
            // see if this name is being used an any custom sheets
            sql = "SELECT * FROM wait_list WHERE name = ?";
        } else {
            // see if this name is being used on a DIFFERENT custom sheet
            sql = "SELECT * FROM wait_list WHERE name = ? AND wait_list_id <> ?";
        }
        
        PreparedStatement pstmt = con.prepareStatement ( sql );
        pstmt.clearParameters();
        pstmt.setString(1, parmWL.name);
        if (wait_list_id != 0) pstmt.setInt(2, wait_list_id);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            
            // if any rows returned, the name passed here is already in use
            buildForm(req, parmWL, "Name is already in use.", out, con);
            return;
        }
        
    } catch(Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Failed to validate name.", exc.toString(), out, false);
        return;
    }
    
    //
    // Make sure the ending date is after the starting date
    //
    if (start_date > end_date) {
        
        buildForm(req, parmWL, "Starting date must be before the ending date.", out, con);
        return;
    }
    
    //
    // Make sure the ending date is after the starting date
    //
    if (start_time >= end_time) {
        
        buildForm(req, parmWL, "Starting time must be before the ending time.", out, con);
        return;
    }
    
    //
    // Make sure there is at least one day selected for recurrence
    //
    if (mon + tue + wed + thu + fri + sat + sun == 0) {
        
        buildForm(req, parmWL, "Must select at least one day for recurrence.", out, con);
        return;
    }
    
    
    
    
    if (wait_list_id == 0) {
        
        // insert
        sql = "INSERT INTO wait_list " +
               "(name, sdatetime, edatetime, cutoff_days, cutoff_time, sunday, monday, tuesday, wednesday, thursday, friday, saturday, " +
                "course, member_view_teesheet, member_access, member_view, max_team_size, max_list_size, allow_guests, allow_x, enabled, notice, color) " +
               "VALUES " +
               "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
    } else {
        
        // update
        sql = "UPDATE wait_list " +
              "SET name = ?, sdatetime = ?, edatetime = ?, cutoff_days = ?, cutoff_time = ?, " +
                "sunday = ?, monday = ?, tuesday = ?, wednesday = ?, thursday = ?, friday = ?, saturday = ?, " +
                "course = ?, member_view_teesheet = ?, member_access = ?, member_view = ?, max_team_size = ?, max_list_size = ?, " +
                "allow_guests = ?, allow_x = ?, enabled = ?, notice = ?, color = ? " +
              "WHERE wait_list_id = ?";
        
    }
    
    out.println("<!-- " + sql + " -->");
    out.println("<!-- sdatetime=" + sdatetime + " -->");
    out.println("<!-- edatetime=" + edatetime + " -->");
    out.println("<!-- cutoff_time=" + cutoff_time + " -->");
    out.println("<!-- start_time=" + start_time + " " + start_ampm + " -->");
    out.println("<!-- end_time=" + end_time + " " + end_ampm + " -->");
    
    try {
    
        PreparedStatement pstmt = con.prepareStatement ( sql );
        pstmt.clearParameters();
        pstmt.setString(1, parmWL.name);
        pstmt.setString(2, sdatetime);
        pstmt.setString(3, edatetime);
        pstmt.setInt(4, parmWL.cutoff_days);
        pstmt.setInt(5, parmWL.cutoff_time);
        pstmt.setInt(6, parmWL.sunday);
        pstmt.setInt(7, parmWL.monday);
        pstmt.setInt(8, parmWL.tuesday);
        pstmt.setInt(9, parmWL.wednesday);
        pstmt.setInt(10, parmWL.thursday);
        pstmt.setInt(11, parmWL.friday);
        pstmt.setInt(12, parmWL.saturday);
        pstmt.setString(13, parmWL.course);
        pstmt.setInt(14, parmWL.member_view_teesheet);
        pstmt.setInt(15, parmWL.member_access);
        pstmt.setInt(16, parmWL.member_view);
        pstmt.setInt(17, parmWL.max_team_size);
        pstmt.setInt(18, parmWL.max_list_size);
        pstmt.setInt(19, parmWL.allow_guests);
        pstmt.setInt(20, parmWL.allow_x);
        pstmt.setInt(21, parmWL.enabled);
        pstmt.setString(22, parmWL.notice);
        pstmt.setString(23, parmWL.color);
        if (wait_list_id != 0) pstmt.setInt(24, parmWL.wait_list_id);
        pstmt.executeUpdate();
        pstmt.close();
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Failed to save data.", exc.toString(), out, false);
        return;
    }
    
    //
    // WAIT LIST CONFIG SAVED
    
    out.println("<center>");
    out.println("<br>");
    out.println("<h3>Wait List " + ((parmWL.wait_list_id == 0) ? "Created" : "Updated") + " Successfully</h3>");
    
    out.println("<br>");
    
    out.println("<form action=\"/" + rev + "/servlet/Proshop_waitlist\" method=get>");
    
    // for now lets return back to the wait list SELECT screen
    out.println("<input type=hidden name=edit>");
        
    if (parmWL.wait_list_id == 0) {
        // we just made a new wait list - lets return back to the wait list SELECT screen
        //out.println("<input type=hidden name=edit>");
    } else {
        // we just updated an existing wait list - lets return to the 
        //out.println("<input type=hidden name=waitListId value=\"" + parmWL.wait_list_id + "\">");
    }
    out.println("<input type=submit value=\"Return\">");
    out.println("</form>");
    out.println("</center>");
    
    
/*
    int multi = 0;
    
    //
    // Make sure the club is setup and there is at least one course configured
    //
    try {

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");

        if (rs.next()) multi = rs.getInt(1);

        stmt.close();

    } catch (Exception exc) {

        out.println(SystemUtils.HeadTitle("Database Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Database Access Error</H3>");
        out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
        out.println("<BR>Please try again later.");
        out.println("<BR><br>Exception: " + exc.getMessage());
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
        out.println("</CENTER></BODY></HTML>");
        return;

    }
    
    buildList(multi, out, con);
*/
 }
 
 
 private void waitListConfig(HttpServletRequest req, PrintWriter out, Connection con) {


    String id = req.getParameter("waitListId");                 //  uid of the wait list we are working with

    int wait_list_id = 0;
    
    //
    //  Convert the values from string to int
    //
    try {
        
        wait_list_id = Integer.parseInt(id);
    }
    catch (NumberFormatException e) { }
   
    
    //
    //  parm block to hold the wait list parameters
    //
    parmWaitList parmWL = new parmWaitList();                   // allocate a parm block
        
    
    if (wait_list_id > 0) {
        
        parmWL.wait_list_id = wait_list_id;

        try {

            getWaitList.getParms(con, parmWL);                      // get the wait list config

        } catch (Exception exp) {
            out.println(exp.getMessage());
        }
        
    } else {
        
        // new wait list - set any defaults we want here
        Calendar cal = new GregorianCalendar();        // get current date and time
        parmWL.start_date = (cal.get(Calendar.YEAR) * 10000) + ((cal.get(Calendar.MONTH)+1) * 100) + cal.get(Calendar.DAY_OF_MONTH);
        
        
    }
    
    buildForm(req, parmWL, "", out, con);
    
 }

 
 private void buildForm(HttpServletRequest req, parmWaitList parmWL, String msg, PrintWriter out, Connection con) {
    
/*
    //
    //  Get this session's user name
    //
    String user = (String)session.getAttribute("user");
    String club = (String)session.getAttribute("club");

    String notes = req.getParameter("notes");                // Member Notes
    String hides = req.getParameter("hide");                 // Hide Notes Indicator
    String jump = req.getParameter("jump");                  // jump index for _sheet
*/
    
    long mm = 0;
    long dd = 0;
    long yy = 0;
    long temp = 0;
    
    int hr = 0;
    int min = 0;
    int i = 0;
    int multi = SystemUtils.getMulti(con);
    int cutoff_hr = parmWL.cutoff_time / 100;
    int cutoff_min = parmWL.cutoff_time - cutoff_hr * 100;
    
    String ampm = "";
    String cutoff_ampm = "AM";
    
    if (cutoff_hr >= 12) {
        cutoff_hr = cutoff_hr - 12;
        cutoff_ampm = "PM";
    }
    
    out.println("<script type=\"text/javascript\">");
    out.println("<!-- ");
    out.println("function openHelp() {");
    out.println(" w = window.open ('/v5/proshop_help_waitlist_member_access.htm','WLpopup','width=650,height=455,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
    out.println(" w.creator = self;");
    out.println("}");
    out.println("// -->");
    out.println("</script>");
    
    out.println("<br>");
    
    out.println("<table width=550 cellpadding=15 bgcolor=#F5F5DC border=1 align=center><tr><td align=center>");
    if (parmWL.wait_list_id == 0) {
        
        out.println("<font size=3><b>New Wait List Setup</b></font>");
        /*out.println("<p>" +
                    "Please be careful when making custom tee sheets.  The changes it will make to your tee sheets are " +
                    "not easily undone.  If you haven't used this feature before, please contact support so that we can " +
                    "assist you.</p>" +
                    "<p>Please be aware that we can not apply custom tee sheets to days that already have " +
                    "tee times reserved on them.</p>");*/
    } else {
        
        out.println("<font size=3><b>Edit Existing Wait List</b></font>");
        /*out.println("<p>" +
                    "Please be aware that while we allow you to edit your existing custom tee sheets<br>" +
                    "changes that have already been made to your tee sheets can not be undone!<br>" +
                    "Please contact our Support staff for additional assitance.</p>");*/
    }
    out.println("</td></tr></table>");
    
    if (!msg.equals("")) {
        out.println("<p align=center style=\"color:red;font-weight:bold\"><i>Error: " + msg + "</i></p>");
    } else {
        out.println("<br>");
    }
    
    out.println("<table width=550 cellpadding=15 bgcolor=#F5F5DC border=1 align=center><tr><td>");
    
    
    out.println("<form action=\"/" + rev + "/servlet/Proshop_waitlist\" method=post>");
    out.println("<input type=hidden name=wait_list_id value=\"" + parmWL.wait_list_id + "\">");
    
    out.println("&nbsp;&nbsp;Name:&nbsp;&nbsp;<input type=\"text\" name=\"name\" value=\"" + parmWL.name + "\" size=24 maxlength=32> *&nbsp; <font size=1>(must be unique)</font><br><br>");
    
    if (multi == 0) {
        out.println("<input type=hidden name=course value=\"\">");
    } else {
        Common_Config.displayCourseSelection(parmWL.course, true, con, out);
    }
    
    //
    //  isolate yy, mm, dd
    //
    yy = parmWL.start_date / 10000;
    temp = yy * 10000;
    mm = parmWL.start_date - temp;
    temp = mm / 100;
    temp = temp * 100;
    dd = mm - temp;
    mm = mm / 100;
    
    Common_Config.displayStartDate(mm, dd, yy, out);
    
    //
    //  isolate yy, mm, dd
    //
    yy = parmWL.end_date / 10000;
    temp = yy * 10000;
    mm = parmWL.end_date - temp;
    temp = mm / 100;
    temp = temp * 100;
    dd = mm - temp;
    mm = mm / 100;
    
    Common_Config.displayEndDate(mm, dd, yy, out);

    ampm = "AM";
    hr = parmWL.start_time / 100;
    min = parmWL.start_time - (hr * 100);
    if (hr >= 12) {
        if (hr > 12) hr = hr - 12;
        ampm = "PM";
    } else if (hr == 0) hr = 12;
    
    Common_Config.displayStartTime(hr, min, ampm, out);

    ampm = "AM";
    hr = parmWL.end_time / 100;
    min = parmWL.end_time - (hr * 100);
    if (hr >= 12) {
        if (hr > 12) hr = hr - 12;
        ampm = "PM";
    } else if (hr == 0) hr = 12;
    
    Common_Config.displayEndTime(hr, min, ampm, out);
    
    Common_Config.displayRecurr(parmWL.monday, parmWL.tuesday, parmWL.wednesday, parmWL.thursday, parmWL.friday, parmWL.saturday, parmWL.sunday, out);
    
    out.println("&nbsp;&nbsp;Member Access to Tee Sheet: &nbsp;&nbsp; ");
    out.println("<select size=\"1\" name=\"member_view_teesheet\">");
    Common_Config.buildOption(0, "Suppress All", parmWL.member_view_teesheet, out);
    Common_Config.buildOption(1, "Display Only", parmWL.member_view_teesheet, out);
    Common_Config.buildOption(2, "Conditional Access", parmWL.member_view_teesheet, out);
    out.println("</select>&nbsp; &nbsp; &nbsp;<font size=2><a href=\"javascript:void(0)\" onclick=\"openHelp()\">Help</a></font>");
    out.println("<br><br>");
    
    out.println("&nbsp;&nbsp;Cutoff Days: &nbsp;&nbsp; ");
    out.println("<select size=\"1\" name=\"cutoff_days\">");
    Common_Config.buildOption(0, "Day Of", parmWL.cutoff_days, out);
    for (i=1;i<=31;i++) {
        Common_Config.buildOption(i, i, parmWL.cutoff_days, out);
    }
    out.println("</select>&nbsp; &nbsp;<font size=2>(when to cutoff member access to tee sheet)</font>");
    out.println("<br><br>");
    
    Common_Config.displayHrMinToD(cutoff_hr, cutoff_min, cutoff_ampm, "&nbsp;&nbsp;Cutoff Time:", "cutoff_hr", "cutoff_min", "cutoff_ampm", out);
    out.println("<br><br>");
    
    out.println("&nbsp;&nbsp;Color:&nbsp;&nbsp;");
    Common_Config.displayColorsAll(parmWL.color, out);
    out.println("<br><br>");
    
    out.println("&nbsp;&nbsp;Max Players per Sign-up: &nbsp;&nbsp; ");
    out.println("<select size=\"1\" name=\"max_team_size\">");
    for (i=1;i<=5;i++) {
        Common_Config.buildOption(i, i, parmWL.max_team_size, out);
    }
    out.println("</select>");
    out.println("<br><br>");
    
    out.println("&nbsp;&nbsp;Number of Guests Allowed: &nbsp;&nbsp; ");
    out.println("<select size=\"1\" name=\"allow_guests\">");
    Common_Config.buildOption(0, "None", parmWL.allow_guests, out);
    for (i=1;i<=5;i++) {
        Common_Config.buildOption(i, i, parmWL.allow_guests, out);
    }
    out.println("</select>");
    out.println("<br><br>");
    
    out.println("&nbsp;&nbsp;Number of X's Allowed: &nbsp;&nbsp; ");
    out.println("<select size=\"1\" name=\"allow_x\">");
    Common_Config.buildOption(0, "None", parmWL.allow_x, out);
    for (i=1;i<=4;i++) {
        Common_Config.buildOption(i, i, parmWL.allow_x, out);
    }
    out.println("</select>");
    out.println("<br><br>");
    
    out.println("&nbsp;&nbsp;Max List Size: &nbsp;&nbsp; ");
    out.println("<select size=\"1\" name=\"max_list_size\">");
    Common_Config.buildOption(0, "No Limit", parmWL.max_list_size, out);
    for (i=1;i<=99;i++) {
        Common_Config.buildOption(i, i, parmWL.max_list_size, out);
    }
    out.println("</select>&nbsp; &nbsp;<font size=2>(Maximum number of sign-ups allowed)</font>");
    out.println("<br><br>");
    
    //out.println("<input type=hidden name=auto_assign value=\"0\">"); // future use
    //out.println("<input type=\"checkbox\" name=\"member_view_teesheet\" " + ((parmWL.member_view_teesheet == 1) ? "checked" : "") + " value=\"1\">&nbsp;&nbsp;Auto Assign");
    
    out.println("<input type=\"checkbox\" name=\"member_access\" " + ((parmWL.member_access == 1) ? "checked" : "") + " value=\"1\">&nbsp;&nbsp;" +
            "Allow Member Sign-ups &nbsp;&nbsp;" +
            "<font size=2>(If not checked, members can't sign themselves up)</font>");
    out.println("<br><br>");
    out.println("<input type=\"checkbox\" name=\"member_view\" " + ((parmWL.member_view == 1) ? "checked" : "") + " value=\"1\">&nbsp;&nbsp;" +
            "Allow Members to View &nbsp;&nbsp;" +
            "<font size=2>(If not checked, members can't see who is on the wait list)</font>");
    out.println("<br><br>");
    out.println("<input type=\"checkbox\" name=\"enabled\" " + ((parmWL.enabled == 1) ? "checked" : "") + " value=\"1\">&nbsp;&nbsp;" +
            "Enabled &nbsp;&nbsp;" +
            "<font size=2>(The wait list is only active and accessible if enabled)</font>");
    out.println("<br><br><br>");
    
    out.println("<table align=center border=0><tr><td align=center>");
    
    out.println("<input type=submit value=\"" + ((parmWL.wait_list_id == 0) ? "Create" : "Update") + "\"></td>");
    out.println("</form>");
    
    out.println("<td>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</td>");
    
    // allow the delete button if it's an exising wait list that has passed or has no signups
    if (parmWL.wait_list_id != 0) {
        
        String delmsg = "";
        int count = 0;
        
        try {

            count = getWaitList.getListCount(parmWL.wait_list_id, 0, 1, 0, true, con); // get count of ALL unconverted signups for ALL dates for this list 

        } catch (Exception exp) {
            out.println(exp.getMessage());
        }
        
        if (parmWL.end_date < SystemUtils.getDate(con) || count == 0) {
            
            delmsg = "Are you sure you want to PERMENATLY DELETE this wait list?\\n\\nThis action CAN NOT be undone!";
            
        } else if (count > 0) {
            
            delmsg = "The list you are trying to DELETE still has " + count + " sign-ups.\\n\\nAre you sure you want to PERMENATLY DELETE this wait list?\\n\\nThis action CAN NOT be undone!";
            
        } else {
            
            delmsg = "Are you sure you want to PERMENATLY DELETE this wait list?\\n\\nThis action CAN NOT be undone!";
            
        }
        
        out.println("<form action=\"/" + rev + "/servlet/Proshop_waitlist\" method=get>");
        out.println("<td align=center>");
        out.println("<input type=hidden name=delete>");
        out.println("<input type=hidden name=waitListId value=\"" + parmWL.wait_list_id + "\">");
        out.println("<input type=submit value=\"Delete\" onclick=\"return confirm('" + delmsg + "')\"></td>");
        out.println("</form>");

        out.println("<td>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</td>");
        
    }
    
    out.println("<form action=\"/" + rev + "/servlet/Proshop_announce\" method=get>");
    out.println("<td align=center>");
    out.println("<input type=hidden name=menu>");
    out.println("<input type=submit value=\"Cancel\"></td>");
    out.println("</form>");

    out.println("</tr>");
     
 } 
 
 
 private void buildList(int multi, PrintWriter out, Connection con) {
     
        int wait_list_id = 0;
        String tmp = "";
        int tmp_id = 0;
        String tmp_name = "";
        String tmp_course = "";
        String tmp_sdate = "";
        String tmp_edate = "";
        String tmp_stime = "";
        String tmp_etime = "";
        String tmp_recurrence = "Every ";
        
        out.println("<br>");
        out.println("<h3 align=center>Current Configured Wait Lists</h3>");
        out.println("<br><center>");
        
        out.println("<table align=center border=1 bgcolor=\"#F5F5DC\" width=800>");
        
        out.println("<tr bgcolor=\"#8B8970\" align=center style=\"color: black; font-weight: bold\">" +
                    "<td height=35>Name</td>" +
                    ((multi == 0) ? "" : "<td>Course</td>") +
                    "<td>&nbsp;Start Date&nbsp;</td><td>&nbsp;End Date&nbsp;</td>" +
                    "<td>&nbsp;Start Time&nbsp;</td><td>&nbsp;End Time&nbsp;</td>" +
                    "<td>Recurrence</td><td>&nbsp;</td></tr>");

        try {

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("" +
                "SELECT *, " +
                    "DATE_FORMAT(sdatetime, '%c/%e/%Y') AS sdate, " +
                    "DATE_FORMAT(edatetime, '%c/%e/%Y') AS edate, " +
                    "DATE_FORMAT(sdatetime, '%l:%i %p') AS stime, " +
                    "DATE_FORMAT(edatetime, '%l:%i %p') AS etime " +
                "FROM wait_list " +
                "ORDER BY name ASC");

            while (rs.next()) {
                
                tmp_recurrence = "";
                tmp_id = rs.getInt("wait_list_id");
                tmp_name = rs.getString("name");
                tmp_course = rs.getString("course");
                tmp_sdate = rs.getString("sdate");
                tmp_edate = rs.getString("edate");
                tmp_stime = rs.getString("stime");
                tmp_etime = rs.getString("etime");
                if (rs.getInt("sunday") == 1) tmp_recurrence += "Sun, ";
                if (rs.getInt("monday") == 1) tmp_recurrence += "Mon, ";
                if (rs.getInt("tuesday") == 1) tmp_recurrence += "Tue, ";
                if (rs.getInt("wednesday") == 1) tmp_recurrence += "Wed, ";
                if (rs.getInt("thursday") == 1) tmp_recurrence += "Thu, ";
                if (rs.getInt("friday") == 1) tmp_recurrence += "Fri, ";
                if (rs.getInt("saturday") == 1) tmp_recurrence += "Sat, ";
                tmp_recurrence = tmp_recurrence.substring(0, tmp_recurrence.length() - 2);
                                
                out.println("<tr align=center height=50><form>" +
                            "<td>" + tmp_name + "</td>" +
                            ((multi == 0) ? "" : "<td>" + tmp_course + "</td>") +
                            "<td>" + tmp_sdate + "</td>" +
                            "<td>" + tmp_edate + "</td>" +
                            "<td>" + tmp_stime + "</td>" +
                            "<td>" + tmp_etime + "</td>" +
                            "<td>" + tmp_recurrence + "</td>" +
                            "<td><input type=hidden name=edit><input type=hidden name=waitListId value=\"" + tmp_id + "\">" + 
                            "<input type=submit value=\" Select \"></td></form></tr>");
            }
            
            stmt.close();
        
        } catch (Exception exc) {
            SystemUtils.buildDatabaseErrMsg("Error loading existing wait list names.", exc.toString(), out, false);
        }


        out.println("</table>");
                
        out.println("<br><form action=\"/" + rev + "/servlet/Proshop_announce\">");
        out.println("<input type=hidden name=menu>");
        out.println("<input type=submit value=\"Return\">");
        out.println("</form>");
        out.println("</center>");
     
 }
 
 
 private void viewSignups(HttpServletRequest req, int multi, PrintWriter out, Connection con) {
     
    
    int wait_list_id = 0;
    int wait_list_signup_id = 0;
    int sum_players = 0; 
    int date = 0;
    int pos = 1;
    int time = SystemUtils.getTime(con);
    int today_date = (int)SystemUtils.getDate(con);
    int start_time = 0;
    int end_time = 0;
    int count = 0;
    int index = 0;
    
    String sindex = req.getParameter("index");                  //  index value of day (needed by Proshop_waitlist_slot when returning)
    String id = req.getParameter("waitListId");                 //  uid of the wait list we are working with
    String course = (req.getParameter("course") == null) ? "" : req.getParameter("course");
    String returnCourse = (req.getParameter("returnCourse") == null) ? "" : req.getParameter("returnCourse");
    String sdate = (req.getParameter("sdate") == null) ? "" : req.getParameter("sdate");
    String name = (req.getParameter("name") == null) ? "" : req.getParameter("name");
    String day_name = (req.getParameter("day_name") == null) ? "" : req.getParameter("day_name");
    String sstart_time = (req.getParameter("start_time") == null) ? "" : req.getParameter("start_time");
    String send_time = (req.getParameter("end_time") == null) ? "" : req.getParameter("end_time");
    //String count = (req.getParameter("count") == null) ? "" : req.getParameter("count");
    String jump = req.getParameter("jump");
    
    String fullName = "";
    String cw = "";
    String notes = "";
    String nineHole = "";
    
    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    
    boolean tmp_found = false;
    boolean tmp_found2 = false;
    boolean master = (req.getParameter("view") != null && req.getParameter("view").equals("master"));
    boolean show_notes = (req.getParameter("show_notes") != null && req.getParameter("show_notes").equals("yes"));
    boolean alt_row = false;
    boolean tmp_converted = false;
    
    try {
        
        date = Integer.parseInt(sdate);
        index = Integer.parseInt(sindex);
        wait_list_id = Integer.parseInt(id);
        start_time = Integer.parseInt(sstart_time);
        end_time = Integer.parseInt(send_time);
    }
    catch (NumberFormatException e) { }
    
    try {
        
        count = getWaitList.getListCount(wait_list_id, date, index, time, !master, con);
        
    } catch (Exception exp) {
        out.println(exp.getMessage());
    }
    
    //
    //  isolate yy, mm, dd
    //
    int yy = date / 10000;
    int temp = yy * 10000;
    int mm = date - temp;
    temp = mm / 100;
    temp = temp * 100;
    int dd = mm - temp;
    mm = mm / 100;
    
    String report_date = SystemUtils.getLongDateTime(today_date, time, " at ", con);
    
    out.println("<br>");
    out.println("<h3 align=center>" + ((master) ? "Master Wait List Sign-up Sheet" : "Current Wait List Sign-ups") + "</h3>");
    
    out.println("<p align=center><font size=3><b><i>\"" + name + "\"</i></b></font></p>");
    
    out.println("<table border=0 align=center>");
    
    out.println("<tr><td><font size=\"2\">");
    out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b></td>");
    out.println("<td>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</td><td>");
    if (!course.equals( "" )) {
        out.println("<font size=\"2\">Course:&nbsp;&nbsp;<b>" + course + "</b></font>");
    }
    out.println("</td></tr>");
    
    out.println("<tr><td><font size=\"2\">Time:&nbsp;&nbsp;<b>" + SystemUtils.getSimpleTime(start_time) + " to " + SystemUtils.getSimpleTime(end_time) + "</b></font></td>");
    
    out.println("<td></td>");
    
    out.println("<td><font size=\"2\">Signups:&nbsp;&nbsp;<b>" + count + "</b></font></td>");
    
    out.println("</table>");
    
    out.println("<p align=center><font size=2><b><i>List Generated on " + report_date + "</i></b></font></p>");
    
    out.println("<table align=center border=1 bgcolor=\"#F5F5DC\">");
    
    if (master) {

        out.println("<tr bgcolor=\"#8B8970\" align=center style=\"color: black; font-weight: bold\">" +
                        "<td height=35>&nbsp;Pos&nbsp;</td>" +
                        "<td>Sign-up Time</td>" + 
                        "<td>Members</td>" +
                        "<td>Desired Time</td>" +
                        "<td>&nbsp;Players&nbsp;</td>" +
                        "<td>&nbsp;On Sheet&nbsp;</td>" +
                        "<td>Converted At</td>" +
                        "<td>&nbsp;Converted By&nbsp;</td>" +
                        ((show_notes) ? "<td>&nbsp;Notes&nbsp;</td>" : "") +
                    "</tr>");
    } else {

        out.println("<tr bgcolor=\"#8B8970\" align=center style=\"color: black; font-weight: bold\">" +
                        "<td height=35>&nbsp;Pos&nbsp;</td>" +
                        "<td>Members</td>" +
                        "<td>Desired Time</td>" +
                        "<td>&nbsp;Players&nbsp;</td>" +
                        ((show_notes) ? "<td>&nbsp;Notes&nbsp;</td>" : "") +
                    "</tr>"); // +
                    //"<td>&nbsp;On Sheet&nbsp;</td>" +
                    //"</tr>");
                    //((multi == 0) ? "" : "<td>Course</td>") +
    }
    out.println("<!-- wait_list_id=" + wait_list_id + ", date=" + date + ", time=" + time + " -->");
    
    try {
        
        pstmt = con.prepareStatement ("" +
            "SELECT *, " +
                "DATE_FORMAT(created_datetime, '%c/%e/%y %r') AS created_time, " +
                "DATE_FORMAT(converted_at, '%c/%e/%y %r') AS converted_time " + // %l:%i %p
            "FROM wait_list_signups " +
            "WHERE wait_list_id = ? AND date = ? " +
                ((master) ? "" : "AND converted = 0 ") + 
                ((!master && sindex.equals("0")) ? "AND ok_etime > ? " : "") + 
            "ORDER BY created_datetime");
        
        pstmt.clearParameters();
        pstmt.setInt(1, wait_list_id);
        pstmt.setInt(2, date);
        if (!master && sindex.equals("0")) pstmt.setInt(3, time);
        
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            
            wait_list_signup_id = rs.getInt("wait_list_signup_id");
            
            out.print("<tr align=center" + ((alt_row) ? " style=\"background-color:white\"" : "") + "><td>" + pos + "</td>");
            
            if (master) out.println("<td>&nbsp;" + rs.getString("created_time") + "&nbsp;</td>");
            
            //if (multi == 1) out.println("<td>" + rs.getString("course") + "</td>");
            
            //
            //  Display players in this signup
            //
            pstmt2 = con.prepareStatement ("" +
                  "SELECT * " +
                  "FROM wait_list_signups_players " +
                  "WHERE wait_list_signup_id = ? " +
                  "ORDER BY pos");
            
            pstmt2.clearParameters();
            pstmt2.setInt(1, wait_list_signup_id);
            
            ResultSet rs2 = pstmt2.executeQuery();

            out.print("<td align=left>");

            tmp_found2 = false;

            while (rs2.next()) {

                fullName = rs2.getString("player_name");
                cw = rs2.getString("cw");
                if (rs2.getInt("9hole") == 1) cw = cw + "9";

                if (tmp_found2) out.print(",&nbsp; "); else out.print("&nbsp;");
                out.print(fullName + " <font style=\"font-size:9px\">(" + cw + ")</font>");
                tmp_found2 = true;
                sum_players++;
                nineHole = "";   // reset
            }
            
            pstmt2.close();
            
            out.print("</td>");
            
            out.println("<td>&nbsp;" + SystemUtils.getSimpleTime(rs.getInt("ok_stime")) + " - " + SystemUtils.getSimpleTime(rs.getInt("ok_etime")) + "&nbsp;</td>");
            
            out.println("<td>" + sum_players + "</td>");
            
            if (master) {
                
                tmp_converted = rs.getInt("converted") == 1;
                out.println("<td>" + ((tmp_converted) ? "Yes" : "No") + "</td>");
                out.println("<td>" + ((tmp_converted) ? rs.getString("converted_time") : "&nbsp;") + "</td>");
                out.println("<td>" + ((tmp_converted) ? rs.getString("converted_by") : "&nbsp;") + "</td>");
                
            }
            
            if (show_notes) {
                
                notes = rs.getString("notes").trim();
                if (notes.equals("")) notes = "&nbsp;";
                out.println("<td>" + notes + "</td>");
            }
            
            out.print("</tr>");
            
            pos++;
            sum_players = 0;
            alt_row = alt_row == false;
        }

        pstmt.close();

    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading wait list signups.", exc.toString(), out, false);
    }


    out.println("</table><br>");
    
    out.println("<table align=center><tr>");
    
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_waitlist\" method=\"GET\">");
    out.println("<input type=\"hidden\" name=\"view\" value=\"" + ((master) ? "current" : "master") + "\">");
    out.println("<input type=\"hidden\" name=\"show_notes\" value=\"" + ((show_notes) ? "yes" : "no") + "\">");
    out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + wait_list_id + "\">");
    out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
    out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
    out.println("<input type=\"hidden\" name=\"index\" value=\"" + sindex + "\">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
    out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
    out.println("<input type=\"hidden\" name=\"start_time\" value=\"" + start_time + "\">");
    out.println("<input type=\"hidden\" name=\"end_time\" value=\"" + end_time + "\">");
    out.println("<input type=\"hidden\" name=\"day_name\" value=\"" + day_name + "\">");
    
    out.println("<td><input type=\"submit\" value=\"" + ((master) ? "Current List" : "Master List") + "\"></td></form>");
    
    out.println("<td>&nbsp;&nbsp;</td>");
    
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_waitlist\" method=\"GET\">");
    out.println("<input type=\"hidden\" name=\"view\" value=\"" + ((master) ? "master" : "current") + "\">");
    out.println("<input type=\"hidden\" name=\"show_notes\" value=\"" + ((show_notes) ? "no" : "yes") + "\">");
    out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + wait_list_id + "\">");
    out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
    out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
    out.println("<input type=\"hidden\" name=\"index\" value=\"" + sindex + "\">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
    out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
    out.println("<input type=\"hidden\" name=\"start_time\" value=\"" + start_time + "\">");
    out.println("<input type=\"hidden\" name=\"end_time\" value=\"" + end_time + "\">");
    out.println("<input type=\"hidden\" name=\"day_name\" value=\"" + day_name + "\">");
    
    out.println("<td><input type=\"submit\" value=\"" + ((show_notes) ? "Hide Notes" : "Show Notes") + "\"></td></form>");
    
    out.println("<td>&nbsp;&nbsp;</td>");
    
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"POST\" target=\"_top\">");
    out.println("<input type=\"hidden\" name=\"jump\" value=\"0\">");
    out.println("<input type=\"hidden\" name=\"index\" value=" + sindex + ">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + ((!returnCourse.equals( "" )) ? returnCourse : course) + "\">");
    
    out.println("<td><input type=\"submit\" value=\"Tee Sheet\"></td></form>");
    
    out.println("<td>&nbsp;&nbsp;</td>");
    
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_waitlist\" method=\"GET\">");
    out.println("<input type=\"hidden\" name=\"view\" value=\"" + ((master) ? "master" : "current") + "\">");
    out.println("<input type=\"hidden\" name=\"show_notes\" value=\"" + ((show_notes) ? "yes" : "no") + "\">");
    out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + wait_list_id + "\">");
    out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
    out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
    out.println("<input type=\"hidden\" name=\"index\" value=\"" + sindex + "\">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
    out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
    out.println("<input type=\"hidden\" name=\"start_time\" value=\"" + start_time + "\">");
    out.println("<input type=\"hidden\" name=\"end_time\" value=\"" + end_time + "\">");
    out.println("<input type=\"hidden\" name=\"day_name\" value=\"" + day_name + "\">");
    
    out.println("<td><button onclick=\"javscript:window.print();return false\">Print</button></td>");
    
    out.println("<td>&nbsp;&nbsp;</td>");
    
    out.println("<td><input type=submit value=\"Refresh\" name=refresh></td></form>");
    
    out.println("<td>&nbsp;&nbsp;</td>");
    
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_waitlist\" method=\"GET\">");
    out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + wait_list_id + "\">");
    out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
    out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
    out.println("<input type=\"hidden\" name=\"index\" value=\"" + sindex + "\">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
    out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
    
    out.println("<td><input type=\"submit\" value=\"Return\"></td></form>");
    
    //out.println("<td><button onclick=\"javscript:history.go(-1);return false\">Return</button></td>");
    
    out.println("</tr></table></form>");
    
    out.println("<br>");
 }
 
}
 
 