/***************************************************************************************
 *   Member_waitlist:  This servlet will allow members to submit a wait list request
 *                     as well as process the request.
 *
 *
 *   called by:  Member_sheet (doPost)
 *               
 *
 *
 *   created: 4/19/2008   Paul S
 *
 *
 *   last updated:       ******* keep this accurate ******
 *
 *        10/23/08  Add hooks for incoming requests from Member_teelist & teelist_list
 *
 */


import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;


// foretees imports
import com.foretees.common.parmWaitList;
import com.foretees.common.getWaitList;



public class Member_waitlist extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
 
     doPost(req, resp);     
 }
 
 
 //*****************************************************
 // Process the request from Member_sheet
 //*****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    PreparedStatement pstmt3 = null;
    Statement stmt = null;
    ResultSet rs = null;

    HttpSession session = SystemUtils.verifyMem(req, out);      // check for intruder

    if (session == null) {

        return;
    }

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
        out.close();
        return;
    }
    
    //
    // See if we are here to VIEW a wait list
    //
    if (req.getParameter("view") != null && req.getParameter("waitListId") != null) {
        
        viewSignups(req, out, con);
        return;
    
    }
    
    String jump = "0";                                          // jump index - default to zero (for _sheet)

    if (req.getParameter("jump") != null) {                     // if jump index provided

        jump = req.getParameter("jump");
    }
    
    //
    //  Get this session's username
    //
    String club = (String)session.getAttribute("club");
    String user = (String)session.getAttribute("user");
    String name = (String)session.getAttribute("name");         // get users full name

    String sindex = req.getParameter("index");                   //  index value of day (needed by Member_sheet when returning)
    String course = req.getParameter("course");                 //  Name of Course
    String id = req.getParameter("waitListId");               //  uid of the wait list we are working with

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
    }
    catch (NumberFormatException e) { }

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
        
        // if members can see the wait list then get the count
        if ( parmWL.member_view == 1 ) 
            count = getWaitList.getListCount(wait_list_id, date, index, time, true, con);
        
    } catch (Exception exp) {
        out.println(exp.getMessage());
    }
    
    out.println("<!-- wait_list_id=" + wait_list_id + ", date=" + date + ", count=" + count + " -->");
    
    //
    //********************************************************************
    //   Build a page to display Wait List details to member
    //********************************************************************
    //
    out.println("<html>");
    out.println("<head>");
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
    out.println("<title>Member Wait List Registration Page</title>");
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
    out.println("<font color=\"ffffff\" size=\"5\">Member Wait List Registration</font>");
    out.println("</font></td>");

    out.println("<td align=\"center\" width=\"300\">");
    out.println("<font size=\"1\" color=\"#ffffff\">Copyright&nbsp;</font>");
    out.println("<font size=\"2\" color=\"#ffffff\">&#169;&nbsp;</font>");
    out.println("<font size=\"1\" color=\"#ffffff\">ForeTees, LLC <br> 2009 All rights reserved.");
    out.println("</font><font size=\"3\">");
    out.println("<br><br><a href=\"/" +rev+ "/member_help.htm\" target=\"_blank\"><b>Help</b></a>");
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
    out.println("The wait list you've selected is running from <nobr>" + SystemUtils.getSimpleTime(parmWL.start_time) + "</nobr> till <nobr>" + SystemUtils.getSimpleTime(parmWL.end_time) + ".</nobr> ");
    
    out.println("Review the information below and click on 'Continue With Request' to contnue.");
    out.println("<br>OR click on 'Cancel Request' to delete the request. To return without changes click on 'Go Back'.");

    //out.println("<br><br><b>NOTE:</b> Only the person that originates the request will be allowed to cancel it or change these values.");

    out.println("</font></td></tr>");
    out.println("</table>");
    
    out.println("<br><br>");

    out.println("<table border=0>");
    
    out.println("<tr><td><font size=\"2\">");
    out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b></td>");
    out.println("<td>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</td><td>");
    if (!course.equals( "" )) {
        out.println("<font size=\"2\">Course:&nbsp;&nbsp;<b>" + course + "</b></font>");
    }
    out.println("</td></tr>");
    
    out.println("<tr><td><font size=\"2\">Wait List:&nbsp;&nbsp;<b>" + SystemUtils.getSimpleTime(parmWL.start_time) + " to " + SystemUtils.getSimpleTime(parmWL.end_time) + "</b></font></td>");
    
    out.println("<td></td>");
    
    out.println("<td><font size=\"2\">Signups:<b>");
    out.print(( (parmWL.member_view == 1) ? count : "N/A") );
    out.println("</b></font></td>");
    
    out.println("</table>");
    
    out.println("<br>");
    
    out.println("<table border=\"0\" align=\"center\">"); // table to contain 2 tables below

    out.println("<tr>");

    
    out.println("<td align=\"center\" valign=\"top\">");

    out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" align=\"center\" width=\"500\" cellpadding=\"5\" cellspacing=\"5\">");  // table for request details
    out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
    out.println("<font color=\"ffffff\" size=\"3\">");
    out.println("<b>" + ((!parmWL.name.equals("")) ? parmWL.name : "Wait List Information") + "</b>");
    out.println("</font></td></tr>");
    
    out.println("<tr>");
    
    out.println("<form action=\"/" +rev+ "/servlet/Member_waitlist_slot\" method=\"post\">");
    out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + wait_list_id + "\">");
    out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
    out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
    out.println("<input type=\"hidden\" name=\"index\" value=\"" + sindex + "\">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
    out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
        
    out.println("<td><font size=\"2\"><br>");
    
    // see if they are already on the wait list
    int onlist = 0;
    
    try {

        onlist = getWaitList.onList(user, wait_list_id, date, con);
        
    } catch (Exception exp) {
        
        out.println(exp.toString());
    }
    
    out.println("<input type=\"hidden\" name=\"signupId\" value=\"" + onlist + "\">");
    
    if (onlist == 0) {
        
        // not on the list
        
        //out.println("The golf shop is running a wait list " + ((index == 0) ? "today": "on this day") + ". ");
        //out.println("The wait list you've selected is running from " + SystemUtils.getSimpleTime(parmWL.start_time) + " till " + SystemUtils.getSimpleTime(parmWL.end_time) + ". ");

        try {

            //out.println("<pre>");
            out.print(getWaitList.getNotice(wait_list_id, con));
            //out.println("</pre>");

        } catch (Exception exp) { }

        if (parmWL.member_access == 1) {
            out.println("<br><p align=center><input type=submit value=\"Continue With Sign-up\" name=\"continue\"></p>");
        } else {
            out.println("<p align=center><b>Contact the golf shop to get on the wait list.</b></p>");
        }
        
    } else {
        
        // already on this list
        
        out.println("<p align=center><b><i>You are already signed up for this wait list.</b></i></p>");
        
        if (parmWL.member_access == 1) {
            out.println("<br><p align=center><input type=submit value=\"Modify Your Sign-up\" name=\"continue\"></p>");
        } else {
            out.println("<p align=center><b>Contact the golf shop to make changes or cancel your entry.</b></p>");
        }
    }
    
    if ( parmWL.member_view == 1 && count > 0 ) {
        
        out.println("<p align=center><input type=button value=\"View Wait List\" name=\"view\" onclick=\"document.forms['frmView'].submit();\"></p>");
    }
    
    out.println("<br></font></td>");
    
    out.println("</table>");
    out.println("</form>");
    
    out.println("<br>");    
    
    if (index == 999) {
    
        //out.println("<form action=\"/" +rev+ "/servlet/Member_teelist\" method=\"GET\">");
        out.println("<form action=\"/" +rev+ "/member_teemain.htm\" method=\"GET\">");
        
    } else if (index == 995) {
        
        //out.println("<form action=\"/" +rev+ "/servlet/Member_teelist_list\" method=\"GET\">");
        out.println("<form action=\"/" +rev+ "/member_teemain2.htm\" method=\"GET\">");
        
    } else {
        
        out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"POST\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + ((!returnCourse.equals( "" )) ? returnCourse : course) + "\">");
    
    }
    out.println("<font size=2>Return w/o Changes:</font><br>");
    out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");
    
    
    out.println("<form action=\"/" +rev+ "/servlet/Member_waitlist\" method=\"POST\" name=frmView>");
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
    //out.println("<input type=submit value=\"View Sign-ups\" name=\"view\">");
    out.println("</form>");
    
 } // end doPost


 private void viewSignups(HttpServletRequest req, PrintWriter out, Connection con) {
     
    
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
    
    out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"POST\" target=\"_top\">");
    out.println("<input type=\"hidden\" name=\"jump\" value=\"0\">");
    out.println("<input type=\"hidden\" name=\"index\" value=" + sindex + ">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + ((!returnCourse.equals( "" )) ? returnCourse : course) + "\">");
    
    out.println("<td><input type=\"submit\" value=\"Tee Sheet\"></td></form>");
    
    out.println("<td>&nbsp;&nbsp;</td>");
    
    out.println("<form action=\"/" +rev+ "/servlet/Member_waitlist\" method=\"POST\">");
    out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + wait_list_id + "\">");
    out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
    out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
    out.println("<input type=\"hidden\" name=\"index\" value=\"" + sindex + "\">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
    out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
    out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
    
    out.println("<td><input type=\"submit\" value=\"Return\"></td></form>");
    
    out.println("</tr></table></form>");
    
    out.println("<br>");
    
 } // end viewSignups

}
 
 