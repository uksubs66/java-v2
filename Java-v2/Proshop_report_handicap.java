/***************************************************************************************
 *   Proshop_report_handicap:   This servlet will implement the hdcp peer reporting functionality
 *
 *
 *   Called by:     Menu tabs and self
 *
 *
 *   Created:       02/14/2007
 *
 *
 *   Revisions:
 *
 *        2/05/14   Add a 9-hole option to the reports that defaults to not include 9-hole no-post rounds.
 *        1/13/14   Pass parm to Send_email when sending emails to remind members to post scores, parm to inform Send_email that email originated
 *                  from here so the message can be altered accordingly.
 *       11/04/13   Allow course=ALL on Posted Scores Report and Add a link for proshop users to this report under their Report menu (in addition
 *                  to their access from Old Sheets).  Members and Proshop users will access the report via promptMemDate (doGet with todo-view).
 *       10/17/13   Add defaults to the Missed Postings by Member report
 *        9/20/13   Get member sub-type from session.
 *        9/20/13   Add 'Send Email' feature to Missed Postings Report and Tee Sheet Postings Report.
 *        8/22/13   Fix Member Type selection in doNonPosters when exporting to excel
 *        8/02/13   Display 'Handicap Index' instead of 'handicap' for all clubs.  This is the proper term.
 *        1/29/13   Do not reference a target=bot in forms for member users.
 *        1/17/13   Add a custom 'blend' option for mpccpb and loxahatchee to the viewSavedScores method (Handicap Peer Review Report)
 *        1/17/13   Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *        1/02/13   Allow for member access to these reports - for Handicap Chairs (member subtype).
 *       11/29/12   Add support for new score_postings table
 *       11/19/12   Added a gender filtering option to the Missed Postings by Member report.
 *       11/18/12   MySQL 5.5 enhancements - utilize the get_918_teepast_by_player function in lookupHistory
 *       10/31/12   Add gender filtering to the Handicap Summary Report (doAllMems) - also added hdncp num & index to report
 *        8/16/12   Fixed up a few sql statements to better match ghin numbers between the score_postings and member2b tables
 *        8/15/12   castlepines - Added skip 9 hole round custom for peer reivew (viewSavedScores) and tee sheet report (viewTeeSheetPostings)
 *        8/10/12   Added nopost fields to query in viewSavedScores (Handicap Peer Review Report)
 *        6/12/12   Fixed issue with opening an individual peer review report from the results of the missed postings report. Middle initials were not being included in the Full Name we use to find the username.
 *        3/23/12   Added email address option to the Missed Postings by Member report.
 *        1/25/12   Updated Posted Scores Report so it says "9 or 18" instead of "9 / 18".  The latter was showing up as "18-Sep" when exported to Excel, which was confusing.
 *        1/11/11   Add a new summary report (allMems option) that will list all active members and their rounds played vs scores posted info.
 *        8/04/10   In getNonPosters - display the actual club num/assoc num and not the id for them in the drop downs
 *        4/28/10   Made lookupHistory a public method so we can call it from the members peer review report
 *        4/20/10   Added hyperlinks to member names in the getNonPosters summary report that link to the individual member report
 *                  also updated viewSavedScores to include the unmatched score postings we found and a summary total of
 *                  rounds played vs. rounds posted during this period at the bottom of the table
 *        9/02/09   Added a copy set of Excel/Home/Back buttons to the top of two reports
 *        9/02/09   Added rptType field to Excel button form so the form printed to the excel document is the same format as originally displayed
 *        8/06/09   Changed SQL statement in viewSavedScores to return 40 scores and no 'AI' types
 *        7/30/09   Updated SQL statement in doNonPosters to match better on ghin numbers by casting to int
 *        5/29/09   Added new Missing Postings By Member Report
 *        3/25/09   Changed viewSavedScores to forget posted scores once displayed
 *       11/14/08   Change viewSavedScores so first loop doesn't grab Away scores
 *       09/02/08   Javascript compatability updates
 *       07/31/08   Added score type and 9/18 hole columns to the report
 *       07/18/08   Added limited access proshop users checks
 *       07/16/08   Removed text referring to List All Members button and minor browser fixes
 *       06/20/07   Fixed blank report problem if no posted scores found for member
 *       06/04/07   Added Excel export buttons
 *
 *
 *
 ***************************************************************************************
 */


import com.foretees.common.Common_Server;
import com.foretees.common.Common_skin;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.text.DateFormat;
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.client.action.ActionHelper;
//import java.text.SimpleDateFormat;

import com.foretees.common.Connect;

public class Proshop_report_handicap extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)

    DateFormat df_full = DateFormat.getDateInstance(DateFormat.MEDIUM);

 //*****************************************************
 // Process the a get method on this page as a post call
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {



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
    
    boolean invMem = false;

    // Check Feature Access Rights for current proshop user
    if (user.startsWith("proshop")) {     // from Proshop User ?
        
        if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
            SystemUtils.restrictProshop("REPORTS", out);
            return;
        }
    
    } else {
        
        invMem = checkMember(user, club, req, out, con);   // is member allowed to do this?
        
        if (invMem) return;                // exit if not
    }
        
    String templott = "";
    int lottery = 0;

    String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");
    
    if (user.startsWith("proshop")) {     // from Proshop User ?
        
        templott = (String)session.getAttribute("lottery");
        lottery = Integer.parseInt(templott);
    }

    if (req.getParameter("allMems") != null) {

       todo = "allMems";
    }



    // START DEFAULT PAGE OUTPUT
    if (!excel.equals("yes")) {
       
         if (user.startsWith("proshop")) {     // from Proshop User ?

            out.println("<!DOCTYPE html>");
            out.println("<html><head>");
            out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
            out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
            out.println("<script language=\"javascript\" src=\"/" +rev+ "/web%20utilities/foretees.js\"></script>");
            out.println("</head>");
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            if (!excel.equals("yes")) SystemUtils.getProshopSubMenu(req, out, lottery);

         } else {

            String clubName = Utilities.getClubName(con, true);        // get the full name of this club

            Common_skin.outputHeader(club, 0, "Handicap Reports", false, out, req);
            out.println("</head>");                
            Common_skin.outputBody(club, 0, out, req);
            if (!excel.equals("yes")) {
                  Common_skin.outputTopNav(req, club, 0, out, con);
                  Common_skin.outputBanner(club, 0, clubName, (String) session.getAttribute("zipcode"), out, req);    // no zip code for Dining
                  Common_skin.outputSubNav(club, 0, out, con, req);
                  Common_skin.outputPageStart(club, 0, out, req);
                  Common_skin.outputBreadCrumb(club, 0, out, "Handicap Reports", req);
                  Common_skin.outputLogo(club, 0, out, req);
            }
            out.println("<CENTER>");
         }
    }


    // MAKE SURE THIS CLUB HAS A HDCP SYSTEM ENABLED
    String hdcpSystem = "";
    try {

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT hdcpSystem FROM club5");
        if (rs.next()) hdcpSystem = rs.getString("hdcpSystem");

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
        return;
    }

    // MAKE SURE CLUB IS SETUP TO USE HDCP FEATURE
    if (hdcpSystem.equals("") || hdcpSystem.equalsIgnoreCase("other")) {

        out.println("<br><br><p align=center><b><i>Your club does not have a handicap system that allows online access.</i></b></p>");
        return;
    }

    // DONE VALIDATING THIS REQUEST - WE'RE READY TO GO


    // HANDLE POST SCORE REQUEST
    if (todo.equals("nonPosters")) { //  || req.getParameter("missingPosts") != null

        getNonPosters(req, session, con, out);

    } else if (todo.equals("view")) {

        //  From member's Handicap Menu or Proshop's Reports menu - Posted Scores By Day Report
           
        promptMemDate(req, club, session, con, out);     // prompt the member for a date and course      

    } else if (todo.equals("view2")) {

        viewTeeSheetPostings(req, session, club, con, out);

    } else if (todo.equals("allMems")) {               // if request to view Summary Report for All Members

        doAllMems(req, session, con, out);

    } else if (todo.equals("viewdates")) {             // if request to view single member for date range (from Summary Report)

        String username = req.getParameter("username");            // username to view
        viewSavedScores(req, username, session, club, con, out);

    } else {

        getViewReport(req, session, con, out);
        
    }

    if (user.startsWith("proshop")) {     // from Proshop User ?
        
        out.println("<br></body></html>");
        
    } else {

        out.println("</CENTER>");
        Common_skin.outputPageEnd(club, 0, out, req);
    }
    
    out.close();

 } // end of doGet routine



 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


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
    
    boolean invMem = false;

    String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");
    String name = (req.getParameter("name") == null) ? "" : req.getParameter("name");
 
    
    String templott = "";
    int lottery = 0;

    if (user.startsWith("proshop")) {     // from Proshop User ?
        
        templott = (String)session.getAttribute("lottery");
        lottery = Integer.parseInt(templott);

    } else {
        
        invMem = checkMember(user, club, req, out, con);   // is member allowed to do this?
        
        if (invMem) return;                // exit if not
    }
        

    if (req.getParameter("custom2") != null) {        // if custom date range entered (from allMems below)

       todo = "allMems2";
    }



    // START DEFAULT PAGE OUTPUT
    if (user.startsWith("proshop")) {     // from Proshop User ?

        out.println("<!DOCTYPE html>");
        out.println("<html><head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
        out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
        out.println("<script language=\"javascript\" src=\"/" +rev+ "/web%20utilities/foretees.js\"></script>");
        out.println("</head>");
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        if (!excel.equals("yes")) SystemUtils.getProshopSubMenu(req, out, lottery);
        
    } else {
        
        String clubName = Utilities.getClubName(con, true);        // get the full name of this club

        Common_skin.outputHeader(club, 0, "Handicap Reports", false, out, req);
        out.println("</head>");                
        Common_skin.outputBody(club, 0, out, req);
        if (!excel.equals("yes")) {
            Common_skin.outputTopNav(req, club, 0, out, con);
            Common_skin.outputBanner(club, 0, clubName, (String) session.getAttribute("zipcode"), out, req);    // no zip code for Dining
            Common_skin.outputSubNav(club, 0, out, con, req);
            Common_skin.outputPageStart(club, 0, out, req);
            Common_skin.outputBreadCrumb(club, 0, out, "Handicap Reports", req);
            Common_skin.outputLogo(club, 0, out, req);
        }
        out.println("<CENTER>");
    }

    // HANDLE REPORT REQUEST
    if (todo.equals("doNonPosters")) {

        doNonPosters(req, session, con, out);

    } else if (!name.equals("") && !todo.equals("")) {

        viewSavedScores(req, name, session, club, con, out);

    } else if (todo.equals("allMems2")) {

        doAllMems2(req, session, con, out);

    } else {

        getViewReport(req, session, con, out);

    }

    if (user.startsWith("proshop")) {     // from Proshop User ?
        
        out.println("<br></body></html>");
        
    } else {

        out.println("</CENTER>");
        Common_skin.outputPageEnd(club, 0, out, req);
    }
        
    out.close();

 } // end of doPost routine


 //
 // This is the Handicap Peer Review Report that is accessed from the Reports menu
 //
 public void viewSavedScores(HttpServletRequest req, String user, HttpSession session, String club, Connection con, PrintWriter out) {


    // custom 'blend' option
    boolean blend = (club.equals("mpccpb") || club.equals("loxahatchee"));
    
    boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);

    // Get the hdcp number from the username
    String hdcp_num = Common_handicaps.getHdcpNum(user, con);
    String fullName = "";
    String tee_name = "";
    String report_title = "Handicap Peer Review Report";

    int multi = 0;
    int last_date = 0;
    int sdate = 0;
    int edate = 0;
    int count = 20;        // number of scores/rounds to list
    int plimit = 40;       // limit of posted scores to gather

    String temps = "";
    String sql = "";

    boolean useDates = false;
    boolean skip_9hole = get9hole_flag(club);     // custom to skip ALL 9-hole rounds in report
    boolean skip_9_NoPosts = true;                // default to not show 9-hole rounds without a post

    String thisuser = (String)session.getAttribute("user");   // get username

    boolean isProshopUser = thisuser.startsWith("proshop");
    
    if (club.equals("seapines")) {
        plimit = 60;
    }
    
    String home = (isProshopUser) ? "Proshop_announce" : "Member_announce";
    
    if (req.getParameter("skip9NoPosts") != null) {         // if skip 9-hole no posts specified

        if (req.getParameter("skip9NoPosts").equals("no")) skip_9_NoPosts = false;
    }
    
    if (req.getParameter("sdate") != null) {         // if specific date range requested

       useDates = true;                             // use date range
       count = 80;                                  // list up to 80 rounds/posts
       plimit = 80;

       try {

          temps = req.getParameter("sdate");
          sdate = Integer.parseInt(temps);

          temps = req.getParameter("edate");
          edate = Integer.parseInt(temps);

       }
       catch (NumberFormatException e) {
       }
    }

    ArrayList<Integer> posting_dates = new ArrayList<Integer>(count);
    ArrayList<Integer> posting_scores = new ArrayList<Integer>(count);
    ArrayList<Integer> posting_used = new ArrayList<Integer>(count);
    ArrayList<String> posting_types = new ArrayList<String>(count);
    ArrayList<String> posting_tees = new ArrayList<String>(count);
    ArrayList<String> posting_course = new ArrayList<String>(count);

    double g_hancap = 0;
    String index = "NH"; // default to no handicap
    
    if (club.equals("governorsclub")) {
        report_title = "Rounds Played vs. Scores Posted Report";
    }

    try {

        // get some member data
        PreparedStatement pstmt = con.prepareStatement("" +
                "SELECT (SELECT multi FROM club5) AS multi, CONCAT(name_first, ' ', name_last) AS fullName, g_hancap FROM member2b WHERE username = ?;");
        pstmt.clearParameters();
        pstmt.setString(1, user);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {

            fullName = rs.getString("fullName");
            multi = rs.getInt("multi");
            g_hancap = rs.getDouble("g_hancap");

            if (g_hancap >= 0) {
                index = "+" + g_hancap;
            } else {
                index = "" + (g_hancap * -1);
            }

        }

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading member data for handicap peer report.", exc.getMessage(), out, false);
        return;
    }
    if (isProshopUser) {
        
        out.println("<p align=center><font size=5>" + report_title + "</font>");

        out.println("<br><br><font size=4><i>For " + fullName + " (" + formatHdcpNum(hdcp_num, club.equals("mpccpb") ? 2 : 1) + ")<br>Current Handicap Index is: " + index + "</i></font></p><br>");

        out.println("<table align=center>");

        out.println("<table width=400 align=center cellspacing=0 cellpadding=5 border=1 bgcolor=#F5F5DC>"); // style=\"border: 1px solid #336633\"  F5F5DC  86B686
        out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                        //"<td align=center colspan=2><font size=3 color=white><b>Member</b></font></td>" +
                        "<td align=center colspan=" + ((multi == 0) ? "2" : "3") + "><font size=3><b>Rounds Played</b></font></td>" +
                        "<td align=center colspan=4><font size=3><b>Scores Posted</b></font></td>" +
                    "</tr>");

        out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" + // #CCCCAA
                        "<td align=center>Date</td>" +
                        ((multi == 0) ? "" : "<td align=center>Course</td>") +
                        "<td align=center>9/18</td><td align=center>Date</td>" +
                        "<td align=center>Score</td><td align=center>Type</td>" +
                        "<td align=center>Tees</td>" +
                    "</tr>");
        
    } else {
        
        out.println("<h2 class=\"handicaps_ctr\">" + report_title + "</h2>");
        
        out.println("<br /><h3 class=\"handicaps_ctr\"><i>For " + fullName + " (" + formatHdcpNum(hdcp_num, club.equals("mpccpb") ? 2 : 1) + ")</i></h3><br />");
        out.println("<div class=\"handicaps_peer_review\">");

        out.println("<table class=\"rwdTable standard_list_table\">");
        out.println("<thead class=\"rwdThead\">"); 
        out.println("<tr class=\"rwdTr\"><th class=\"rwdTh hcaps_peer\" colspan=" + ((multi == 0) ? "2" : "3") + ">Rounds Played</th>");
        out.println("<th class=\"rwdTh hcaps_peer\" colspan=4>Scores Posted</th></tr>");

        out.println("<tr><th class=\"rwdTh hcaps_peer\">Date</th>" +
                    ((multi == 0) ? "" : "<th class=\"rwdTh hcaps_peer\">Course</th>") +
                    "<th class=\"rwdTh hcaps_peer\">9/18</th>");
        out.println("<th class=\"rwdTh hcaps_peer\">Date</th><th class=\"rwdTh hcaps_peer\">Score</th>");
        out.println("<th class=\"rwdTh hcaps_peer\">Type</th><th class=\"rwdTh hcaps_peer\">Tees</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody class=\"rwdTbody\">");
        
    }


    try {

        // get the last 40 or more scores posted
        sql = "" +
             "SELECT DATE_FORMAT(sp.date, '%Y%m%d') AS date, sp.score, sp.type, sp.courseName, t.tee_name " +
             "FROM score_postings sp " +
             "LEFT OUTER JOIN tees t ON t.tee_id = sp.tee_id " +
             "WHERE sp.hdcpNum = CONVERT(CAST(REPLACE(?, '-', '') AS UNSIGNED), CHAR) AND sp.type <> 'A' AND sp.type <> 'AI' ";

        if (useDates == true) {
           sql += "AND date BETWEEN ? AND ? ";
        }
        sql += "ORDER BY date DESC LIMIT " +plimit+ ";";

        PreparedStatement pstmt = con.prepareStatement(sql);

        pstmt.clearParameters();
        pstmt.setString(1, hdcp_num);

        if (useDates == true) {

           pstmt.setInt(2, sdate);
           pstmt.setInt(3, edate);
        }

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {

            posting_dates.add(rs.getInt("date"));
            posting_scores.add(rs.getInt("score"));
            posting_types.add(rs.getString("type"));
            posting_course.add(rs.getString("sp.courseName"));
            posting_used.add(0); // default it to not used

            if (rs.getString("tee_name") != null) {
                posting_tees.add(rs.getString("tee_name"));
            } else {
                posting_tees.add("N/A");
            }

        } // end while

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading posted scores for handicap peer report.", exc.getMessage(), out, false);
        return;
    }

    try {

        // get the last 20 rounds played
        sql = "" +
             "SELECT date, courseName, " +
                 "IF(" +
                     "(username1 = ? && p91 = 1) || " +
                     "(username2 = ? && p92 = 1) || " +
                     "(username3 = ? && p93 = 1) || " +
                     "(username4 = ? && p94 = 1) || " +
                     "(username5 = ? && p95 = 1), 1, 0) " +
                 "AS 9hole " +
             "FROM teepast2 " +
             "WHERE " +
             "(" +
                 "(username1 = ? && show1 = 1 && nopost1 = 0" + ((skip_9hole) ? " && p91 = 0" : "") + ") || " +
                 "(username2 = ? && show2 = 1 && nopost2 = 0" + ((skip_9hole) ? " && p92 = 0" : "") + ") || " +
                 "(username3 = ? && show3 = 1 && nopost3 = 0" + ((skip_9hole) ? " && p93 = 0" : "") + ") || " +
                 "(username4 = ? && show4 = 1 && nopost4 = 0" + ((skip_9hole) ? " && p94 = 0" : "") + ") || " +
                 "(username5 = ? && show5 = 1 && nopost5 = 0" + ((skip_9hole) ? " && p95 = 0" : "") + ")" +
             ") ";

        if (useDates == true) {
           sql += "AND date BETWEEN ? AND ? ";
        }

        sql += "ORDER BY date DESC, time ASC LIMIT " + count;

        PreparedStatement pstmt = con.prepareStatement(sql);

        pstmt.clearParameters();
        pstmt.setString(1, user);
        pstmt.setString(2, user);
        pstmt.setString(3, user);
        pstmt.setString(4, user);
        pstmt.setString(5, user);
        pstmt.setString(6, user);
        pstmt.setString(7, user);
        pstmt.setString(8, user);
        pstmt.setString(9, user);
        pstmt.setString(10, user);

        if (useDates == true) {

           pstmt.setInt(11, sdate);
           pstmt.setInt(12, edate);
        }
   
        ResultSet rs = pstmt.executeQuery();

        int post_date = 0;
        int play_date = 0;
        int score = 0;
        int mm = 0;
        int dd = 0;
        int yy = 0;
        int i = 0;
        int temp = 0;
        int holes = 0;
        int used = 0;
        String course = "";
        String post_sdate = "";
        String play_sdate = "";
        String type = "";
        String tees = "";
        String course_ghin = "";
        boolean found = false;

        while (rs.next()) {

            play_date = rs.getInt("date");
            course = rs.getString("courseName");
            holes = (rs.getInt("9hole") == 1) ? 9 : 18;
            found = false;

            // if displaying the blended report then
            // output any posted scores that are before the played date we are about to display
            if (blend) {

                for (i = 0; i < posting_dates.size(); i++) {

                    post_date = (Integer)posting_dates.get(i).intValue();
                    used = (Integer)posting_used.get(i).intValue();

                    if (post_date > play_date && used == 0) {

                        posting_used.set(i, 1);

                        score = (Integer)posting_scores.get(i).intValue();
                        type = (String)posting_types.get(i);
                        tees = (String)posting_tees.get(i);

                        post_sdate = Utilities.getDateFromYYYYMMDD(post_date, 2);

                        if (isProshopUser) {
                            
                            out.println("<tr align=\"center\">" +
                                    "<td bgcolor=#FFFF8F colspan=\"" + ((multi == 0) ? "2" : "3") + "\"><i>No Matching Round</i></td>" +
                                    "<td nowrap>" + post_sdate + "</td><td>" + score + "</td><td>" + type + "</td><td nowrap>" + tees + "</td></tr>");
                            
                        } else {
                            
                            out.println("<tr class=\"rwdTr\">" +
                                    "<td  bgcolor=\"#FFFF8F\" class=\"rwdTd hcaps_peer\" colspan=\"" + ((multi == 0) ? "2" : "3") + "\"><i>No Matching Round</i></td>" +
                                    "<td class=\"rwdTd hcaps_peer\">" + post_sdate + "</td>" +
                                    "<td class=\"rwdTd hcaps_peer\">" + score + "</td>" +
                                    "<td class=\"rwdTd hcaps_peer\">" + type + "</td>" + 
                                    "<td class=\"rwdTd hcaps_peer\">" + tees + "</td></tr>");
                            
                            
                        }
                    }

                }

            }

            // see if there is a posting for this round
            posted_score_loop:
            for (i = 0; i < posting_dates.size(); i++) {

                post_date = (Integer)posting_dates.get(i).intValue();
                score = (Integer)posting_scores.get(i).intValue();
                type = (String)posting_types.get(i);
                tees = (String)posting_tees.get(i);
                used = (Integer)posting_used.get(i).intValue();
                course_ghin = (String)posting_course.get(i);
        
                // For Governors Club, they want to skip rounds where we couldn't detect the tee, since they're likely from members' other home clubs
                if (club.equals("governorsclub") && tees.equals("N/A") && !type.equalsIgnoreCase("C") && !type.equalsIgnoreCase("CI") 
                        && !course_ghin.contains("Governors") && !course_ghin.contains("Primary Rotation")) {
                    continue posted_score_loop;
                }
                
                if (post_date == play_date && used == 0) { //  bgcolor=#F5F5DC   bgcolor=#86B686

                    posting_used.set(i, 1);

                    post_sdate = Utilities.getDateFromYYYYMMDD(post_date, 2);
                    play_sdate = Utilities.getDateFromYYYYMMDD(play_date, 2);

                    if (isProshopUser) {
                        
                        out.println("<tr align=\"center\" bgcolor=\"#86B686\">" +
                                "<td nowrap>" + play_sdate + "</td>" +
                                ((multi == 0) ? "" : "<td nowrap>" + course + "</td>") +
                                "<td>" + holes + "</td> <td nowrap>" + post_sdate + "</td><td>" + score + "</td><td>" + type + "</td><td>" + tees + "</td></tr>");
                    
                    } else {

                        out.println("<tr class=\"rwdTr\" bgcolor=\"#86B686\"><td class=\"rwdTd hcaps_peer\">" + play_sdate + "</td>");
                        out.println( ((multi == 0) ? "<!-- multi is 0 -->" : "<td class=\"rwdTd hcaps_peer\">" + course + "</td>") );
                        out.println("<td class=\"rwdTd hcaps_peer\">" + holes + "</td><td class=\"rwdTd hcaps_peer\">" + post_sdate + "</td>");
                        out.println("<td class=\"rwdTd hcaps_peer\">" + score + "</td><td class=\"rwdTd hcaps_peer\">" + type + "</td>");
                        out.println("<td class=\"rwdTd hcaps_peer\">" + tees + "</td></tr>");
                        
                    }
                    
                    found = true;
                    break;

                } // end if matching dates

            } // end for loop

            // if no posted score was found for this round
            if (!found) { // && i != 0) {

                play_sdate = Utilities.getDateFromYYYYMMDD(play_date, 2); 
                
                if (holes == 18 || skip_9_NoPosts == false) {    // skip if 9-hole round and user opted to skip 9-hole No Posts
                   
                  if (isProshopUser) {

                     out.println("<tr align=\"center\"><td nowrap>" + play_sdate + "</td>" +
                              ((multi == 0) ? "" : "<td nowrap>" + course + "</td>") +
                              "<td>" + holes + "</td> <td colspan=\"4\" bgcolor=\"#FFFF8F\"><i>No Matching Score</i></td></tr>");

                  } else {

                     out.println("<tr class=\"rwdTr\"><td class=\"rwdTd hcaps_peer\">" + play_sdate + "</td>" +
                              ((multi == 0) ? "" : "<td class=\"rwdTd hcaps_peer\">" + course + "</td>") + 
                              "<td class=\"rwdTd hcaps_peer\">" + holes + "</td>");
                     out.println("<td class=\"rwdTd hcaps_peer hcaps_empty\" colspan=\"4\" bgcolor=\"#FFFF8F\">No Matching Score</td></tr>");

                  }
                }
            }

            last_date = play_date; // remember the last date of play

        } // end while

        boolean didHeader = false;

        if (!blend) {        // Add unmatched postings to bottom if not using the blend custom

            unmatched_score_loop:
            for (i = 0; i < posting_dates.size(); i++) {

                post_date = (Integer)posting_dates.get(i).intValue();
                used = (Integer)posting_used.get(i).intValue();

                if (post_date >= last_date && used == 0) {

                    score = (Integer)posting_scores.get(i).intValue();
                    type = (String)posting_types.get(i);
                    tees = (String)posting_tees.get(i);
                    course_ghin = (String)posting_course.get(i);
                    post_sdate = Utilities.getDateFromYYYYMMDD(post_date, 2);
                        
                    // For Governors Club, they want to skip rounds where we couldn't detect the tee, since they're likely from members' other home clubs
                    if (club.equals("governorsclub") && tees.equals("N/A") && !type.equalsIgnoreCase("C") && !type.equalsIgnoreCase("CI") 
                            && !course_ghin.contains("Governors") && !course_ghin.contains("Primary Rotation")) {
                        continue unmatched_score_loop;
                    }

                    if (!didHeader) {

                        out.println("<tr class=\"rwdTr\" style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                                    "<td class=\"rwdTd\" colspan=\"" + ((multi == 0) ? "6" : "7") + "\" align=\"center\"><span style=\"color: white\">Unmatched Score Postings</span></td></tr>");
                        didHeader = true;
                    }

                    out.println("<tr class=\"rwdTr\" align=\"center\">" +
                            "<td class=\"rwdTd\" bgcolor=#FFFF8F colspan=\"" + ((multi == 0) ? "2" : "3") + "\">&nbsp;</td>" +
                            "<td class=\"rwdTd\" nowrap>" + post_sdate + "</td><td class=\"rwdTd\">" + score + "</td><td class=\"rwdTd\">" + type + "</td><td class=\"rwdTd\">" + tees + "</td></tr>");

                } // end unmatched postings loop

            } // end for loop

        } // end if custom

        int [] rounds = new int[3];
        
        Double total_rounds = 0.0;
        Double missed_postings = 0.0;
        
        int rounds_9 = 0;
        int rounds_18 = 0;
        int posted_scores = 0;
        int posted_pct = 0;
        int missed_pct = 0;
        int today = (int)SystemUtils.getDate(con);

        if (useDates == true) {
           rounds = lookupHistory(sdate, edate, user, con, out);
        } else {
           rounds = lookupHistory(last_date, today, user, con, out);
        }

        // Split out into individual ints to make logic easier to follow
        rounds_9 = rounds[0];
        rounds_18 = rounds[1];
        posted_scores = rounds[2];
        
        total_rounds = ((double) rounds_18 + (double) rounds_9 / 2);
        posted_pct = (int) ((posted_scores / total_rounds) * 100);    // Percent of rounds posted

        if (posted_pct > 100) {
            posted_pct = 100;
        } else if (posted_pct < 0) {
            posted_pct = 0;
        }

        missed_postings = total_rounds - (double) posted_scores;
        missed_pct = 100 - posted_pct;

        if (missed_postings < 0.0) {    // Zero the value out if it went negative
            missed_postings = 0.0;
        }
        
        if (isProshopUser) {
            
            out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                        "<td colspan=\"" + ((multi == 0) ? "6" : "7") + "\" align=\"center\">Rounds Played vs. Scores Posted<br><font size=2>(for last twenty rounds)</font></td></tr>");

            out.println("<tr bgcolor=\"#86B686\">" +
                    "<td align=\"center\" colspan=\"" + ((multi == 0) ? "2" : "3") + "\">"
                    + "9 hole: <span style=\"font-weight: bold\">" + rounds_9 + "</span><br>"
                    + "18 hole: <span style=\"font-weight: bold\">" + rounds_18 + "</span>"
                    + "<br>Combined: <span style=\"font-weight: bold\">" + (total_rounds % 1 == 0 ? total_rounds.intValue() : total_rounds.toString()) + "</span></td>" +
                    "<td align=\"center\" colspan=\"4\">Posted: <span style=\"font-weight: bold\">" + posted_scores + " (" + posted_pct + "%)</span>"
                    + "<br>Missed: <span style=\"font-weight: bold\">" + (missed_postings % 1 == 0 ? missed_postings.intValue() : missed_postings.toString()) + "</span> (" + missed_pct + "%)</td></tr>");
            
            out.println("</table>");
            out.println("<br>");
            
        } else {
            
            out.println("<tr class=\"rwdTr hcaps_peer_hdr\">" +
                        "<td class=\"rwdTd hcaps_peer_hdr_data\" colspan=\"" + ((multi == 0) ? "6" : "7") + "\">Rounds Played vs. Scores Posted<br />(for last twenty rounds)</td></tr>");

            out.println("<tr class=\"rwdTr\">"
                    + "<td class=\"rwdTd hcaps_peer hcaps_counts\" colspan=\"3\">"
                    + "9 hole: <span style=\"font-weight: bold\">" + rounds_9 + "</span> &nbsp; &nbsp; "
                    + "18 hole: <span style=\"font-weight: bold\">" + rounds_18 + "</span>"
                    + "<br>Combined: <span style=\"font-weight: bold\">" + (total_rounds % 1 == 0 ? total_rounds.intValue() : total_rounds.toString()) + "</span></td>");
            out.println("<td class=\"rwdTd hcaps_peer hcaps_counts\" colspan=\"" + ((multi == 0) ? "3" : "4") + "\">Posted: <span style=\"font-weight: bold\">" + posted_scores + " (" + posted_pct + "%)</span>"
                    + "<br>Missed: <span style=\"font-weight: bold\">" + (missed_postings % 1 == 0 ? missed_postings.intValue() : missed_postings.toString()) + " (" + missed_pct + "%)</span></td>");
            out.println("</tr>");
            
            out.println("</tbody>");
            out.println("</table>    <!-- standard_list_table -->");
            out.println("</div>    <!-- handicaps_peer_review -->");
        }
        
    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading data for handicap peer report.", exc.getMessage(), out, false);
        return;
    }

    if (req.getParameter("popup") != null || useDates == true) {

        out.println("<center><button onclick=\"window.close()\">Close Window</button></center>");

    } else {
        
        if (req.getParameter("excel") == null) {     // if normal request
           
            out.println("<p align=\"center\">");
            if (skip_9_NoPosts == false) {      // if we did NOT skip the 9-hole No Posts
               out.println("<strong>NOTE:</strong>&nbsp; This report includes all 9-hole rounds.");
            } else {
               out.println("<strong>NOTE:</strong>&nbsp; This report does NOT include 9-hole rounds that are missing a posted score,<br>"
                       + "as those scores may be held while waiting to be combined with another 9-hole score.");
            }
            out.println("</p><center><form method=\"post\" action=\"Proshop_report_handicap\"><br>");
            out.println("<input type=\"hidden\" name=\"todo\" value=\"view\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + user + "\">");
            if (skip_9_NoPosts == false) {      // if we did NOT skip the 9-hole No Posts
               out.println("<input type=\"hidden\" name=\"skip9NoPosts\" value=\"yes\">");
               out.println("<input type=\"submit\" value=\"Run Report w/o 9-Hole No Posts\" style=\"background:#8B8970\">");
            } else {
               out.println("<input type=\"hidden\" name=\"skip9NoPosts\" value=\"no\">");
               out.println("<input type=\"submit\" value=\"Run Report with 9-Hole No Posts\" style=\"background:#8B8970\">");
            }
            out.println("</form><br>");

            out.println("<form method=\"post\" action=\"Proshop_report_handicap\" target=\"_blank\">");
            out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"todo\" value=\"view\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + user + "\">");
            if (skip_9_NoPosts == false) {      // if we did NOT skip the 9-hole No Posts
               out.println("<input type=\"hidden\" name=\"skip9NoPosts\" value=\"no\">");
            } else {
               out.println("<input type=\"hidden\" name=\"skip9NoPosts\" value=\"yes\">");
            }
            out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"background:#8B8970\">");
            out.println("</form></center>");

            if(!rwd){
                out.println("<table style=\"margin-left: auto; margin-right: auto; text-align: center;\"><br><tr>");
                out.println("<td><form><input type=submit value=\"Back\" style=\"width:75px; background:#8B8970\"></form></td>");
                out.println("<td>&nbsp; &nbsp; &nbsp;</td>");
                out.println("<td><form action=" +home+ "><input type=submit value=\"Home\" style=\"width:75px; background:#8B8970\"></form></td>");
                out.println("</tr></table>");
            }
        } // end if not excel

    }

 }


 private void getViewReport(HttpServletRequest req, HttpSession session, Connection con, PrintWriter out) {

   ResultSet rs = null;

   String club = (String)session.getAttribute("club");

   String thisuser = (String)session.getAttribute("user");   // get username

   String name = "";
   String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");

   if (req.getParameter("name") != null) {        // if user specified a name to search for

      name = req.getParameter("name");            // name to search for

      //if (req.getParameter("allmems") != null) name = "ALL"; // is List All submit button sent, then change to ALL

      if (!name.equals( "" )) {

         name = SystemUtils.getUsernameFromFullName(name, con);
         viewSavedScores(req, name, session, club, con, out);
         return;
      }

   }

   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("function cursor() { document.forms['f'].name.focus(); }");
   out.println("function movename(name) {");
   out.println(" document.forms['f'].name.value = name;");
   out.println("}");
   out.println("// -->");
   out.println("</script>");

   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

    out.println("<p align=center><font size=5>Individual Peer Review Report</font></p>");

   out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\" width=\"600\">");
   out.println("<tr><td>");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<p>To review recent postings for a member, enter the name, ");
      out.println("or use the Member List to select the first letter of their last name.&nbsp; ");
      out.println("This will search for all names that start with the letter you select.&nbsp; ");
/*      out.println("You may also lookup all members by clicking the 'List All Members' button.&nbsp; " +
              "Check the 'Include Last 20 Postings' to see the members last 20 scores posted.<br>" +
              "<i>Note: Selecting all members will disregard the last 20 postings option.</i>");
*/
      out.println("</p></font>");
   out.println("</td></tr></table>");

   out.println("<font size=\"2\" face=\"Courier New\">");
   out.println("<p align=\"center\">(Click on <b>'Member List'</b> on right to view a list of members)</p>");
   out.println("<br></font>");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

   if (thisuser.startsWith("proshop")) { 
    
       out.println("<form action=\"Proshop_report_handicap\" method=\"post\" target=\"bot\" name=\"f\">");
       
   } else {
       
       out.println("<form action=\"Proshop_report_handicap\" method=\"post\" name=\"f\">");  // no frames for members - do not use target
   }
   //out.println("<input type=\"hidden\" name=\"todo\" value=\"view\">");
   out.println("<input type=\"hidden\" name=\"todo\" value=" + todo + ">");

   out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr>");
         out.println("<td valign=\"top\" align=\"center\">");

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\">");
               out.println("<tr><td width=\"250\" align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>Name: &nbsp;");
                     out.println("<input type=\"text\" name=\"name\" size=\"20\" maxlength=\"40\">");
                  out.println("<br><br>");
                  out.println("<input type=\"submit\" value=\"Search\" name=\"search\">");
                  out.println("</p>");

                  //out.println("<input type=checkbox name=inc20 value=yes> Include last 20 postings<br><br>");

                  out.println("</font>");
               out.println("</td></tr>");
            out.println("</table>");


      //   if (club.startsWith( "demo" )) {
            // add a button to list all members scored posted vs rounds played
            //
            out.println("<font size=\"2\"><br><br><br><br>Click here for a Summary Report of the<br>Rounds Played vs. Scores Posted for All Members:<br><br></font>");
            out.println("<input type=\"submit\" value=\"Rounds Played vs. Scores Posted\" name=\"allMems\" onclick=\"document.forms['f'].method='get'\" style=\"text-decoration:underline\">");
     //   }

/*
            // add a button to list all members tee times
            //
            out.println("<br><br>");
            out.println("<input type=\"submit\" value=\"Missing Postings By Member\" name=\"missingPosts\" onclick=\"document.forms['f'].method='get'\">");
*/
         out.println("</td>");

   if (req.getParameter("letter") != null) {     // if user clicked on a name letter

      String letter = req.getParameter("letter");      // get the letter
      letter = letter + "%";

      String first = "";
      String mid = "";
      String last = "";
      name = "";
      String wname = "";
      String dname = "";

         out.println("<td valign=\"top\" align=\"center\">");
         out.println("<table border=\"1\" width=\"140\" bgcolor=\"#F5F5DC\">");      // name list
         out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Name List</b>");
               out.println("</font></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("Click on name to add");
            out.println("</font></td></tr>");

         try {

            PreparedStatement stmt2 = con.prepareStatement (
                    "SELECT name_last, name_first, name_mi " +
                    "FROM member2b " +
                    "WHERE ghin <> '' AND name_last LIKE ? " +
                    "ORDER BY name_last, name_first, name_mi");

            stmt2.clearParameters();               // clear the parms
            stmt2.setString(1, letter);            // put the parm in stmt
            rs = stmt2.executeQuery();             // execute the prepared stmt

            out.println("<tr><td align=\"left\"><font size=\"2\">");
            out.println("<select size=\"20\" name=\"bname\" onClick=\"movename(this.form.bname.value)\">");

            while(rs.next()) {

               last = rs.getString(1);
               first = rs.getString(2);
               mid = rs.getString(3);

               if (mid.equals("")) {

                  name = first + " " + last;
                  dname = last + ", " + first;
               } else {

                  name = first + " " + mid + " " + last;
                  dname = last + ", " + first + " " + mid;
               }

               out.println("<option value=\"" + name + "\">" + dname + "</option>");
            }

            out.println("</select>");
            out.println("</font></td></tr>");

            stmt2.close();
         }
         catch (Exception ignore) {

         }
         out.println("</table>");

         out.println("</td>");   // end of name list column

   } else {
      out.println("<td valign=\"top\" width=\"30\">");
      out.println("&nbsp;");
      out.println("</td>");   // end of empty column

   }  // end of if Letter

         out.println("<td valign=\"top\" align=\"center\">");
            out.println("<table border=\"2\" align=\"center\" bgcolor=\"#F5F5DC\">");
               out.println("<tr>");
                  out.println("<td colspan=\"6\" align=\"center\" bgcolor=\"#336633\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<b>Member List</b>");
                     out.println("</font>");
                  out.println("</td>");
               out.println("</tr>");
               out.println("<tr>");
                  out.println("<td colspan=\"6\" align=\"center\">");
                     out.println("<font size=\"2\">Name begins with:");
                     out.println("</font>");
                  out.println("</td>");
               out.println("</tr>");
               out.println("<tr>");
                  out.println("<td align=\"center\"><font size=\"1\">");
                     out.println("<input type=\"submit\" value=\"A\" name=\"letter\"></font></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"B\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"C\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"D\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"E\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"F\" name=\"letter\"></td>");
               out.println("</tr>");

               out.println("<tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"G\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"H\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"I\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"J\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"K\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"L\" name=\"letter\"></td>");
               out.println("</tr>");

               out.println("<tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"M\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"N\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"O\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"P\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"Q\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"R\" name=\"letter\"></td>");
               out.println("</tr>");

               out.println("<tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"S\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"T\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"U\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"V\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"W\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"X\" name=\"letter\"></td>");
               out.println("</tr>");

               out.println("<tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"Y\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"Z\" name=\"letter\"></td>");
                  out.println("<td align=\"center\"></td>");
                  out.println("<td align=\"center\"></td>");
                  out.println("<td align=\"center\"></td>");
                  out.println("<td align=\"center\"></td>");
               out.println("</tr>");
            out.println("</table>");
         out.println("</td>");
      out.println("</tr>");
      out.println("</table>");
      out.println("</select></font>");
      out.println("</td></tr></table>");
      out.println("</form>");
/*
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
*/
    out.println("</font>");

 }


 //
 // This is the Posted Scores report that is accessed from old tee sheets OR from the member or pro menu (Posted Scores By Day) - via promptMemDate
 //
 private void viewTeeSheetPostings(HttpServletRequest req, HttpSession session, String club, Connection con, PrintWriter out) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String sdate = "";
    String course = "";
    String email = "";
    String username = "";
    String userList = "";
    String fullName = "";
    String lastFullName = "";

    int tmp_holes = 0;
    int index = 0;
    long date = 0;
    int multi = 0;
    int count = 0;

    boolean skip_9hole = get9hole_flag(club);     // custom to skip ALL 9-hole rounds in report
    boolean skip_9_NoPosts = true;                // default to not show 9-hole rounds without a post
    boolean excel = false;
    boolean sendEmails = false;

    course = req.getParameter("course");
    
    if (req.getParameter("skip9NoPosts") != null) {         // if skip 9-hole no posts specified

        if (req.getParameter("skip9NoPosts").equals("no")) skip_9_NoPosts = false;
    }
    
    if (req.getParameter("date") != null) {
       
       sdate = req.getParameter("date");

    } else if (req.getParameter("calDate") != null) {        // if user selected the date (from promptMemDate below)

      sdate = req.getParameter("calDate");

      //
      //  Convert the index value from string (mm/dd/yyyy) to ints (month, day, year)
      //
      StringTokenizer tok = new StringTokenizer( sdate, "/" );     // space is the default token - use '/'

      String num = tok.nextToken();                    // get the mm value
      int month = Integer.parseInt(num);
      num = tok.nextToken();                    // get the dd value
      int day = Integer.parseInt(num);
      num = tok.nextToken();                    // get the yyyy value
      int year = Integer.parseInt(num);

      date = (year * 10000) + (month * 100) + day;         // create a date field of yyyymmdd
      
      sdate = String.valueOf((year * 10000) + (month * 100) + day);         // create a date field of yyyymmdd
    }      
    
    
    if (req.getParameter("excel") != null) {     // if request for excel output
       
       excel = true;
    }

    if (req.getParameter("sendEmailAll") != null) {     // if user clicked on "Send Email to All Members" below
       
       sendEmails = true;
       excel = false;
    }


    try {

        if (date == 0) {
           
           date = Integer.parseInt(sdate);
        }

        index = SystemUtils.getIndexFromToday(date, con);

        pstmt = con.prepareStatement("SELECT multi, DATE_FORMAT(?, '%m-%d-%Y') AS sdate FROM club5;");
        pstmt.clearParameters();
        pstmt.setLong(1, date);
        rs = pstmt.executeQuery();

        if ( rs.next() ) {
            sdate = rs.getString("sdate");
            multi = rs.getInt("multi");
        }

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading member data for handicap peer report.", exc.getMessage(), out, false);
        return;

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    String user = (String)session.getAttribute("user");   // get username

    String home = "Proshop_announce";

    if (!user.startsWith("proshop")) home = "Member_announce"; 


    out.println("<br><p align=center><font size=5>Handicap Posting Review Report</font>");

    if (course.equals("-ALL-")) {
       
       out.println("<br><br><font size=4>For " + sdate + " on ALL Courses</font></p><br>");

    } else {
       
       out.println("<br><br><font size=4>For " + sdate + ((multi == 0) ? "" : " on " + course) + "</font></p><br>");
    }

    if (excel == false && sendEmails == false) {     // if normal request

         out.println("<p align=\"center\">");
         if (skip_9_NoPosts == false) {      // if we did NOT skip the 9-hole No Posts
            out.println("<strong>NOTE:</strong>&nbsp; This report includes all 9-hole rounds.");
         } else {
            out.println("<strong>NOTE:</strong>&nbsp; This report does NOT include 9-hole rounds that are missing a posted score,<br>"
                     + "as those scores may be held while waiting to be combined with another 9-hole score.");
         }
         out.println("</p><center><form method=\"get\" action=\"Proshop_report_handicap\">");
         out.println("<input type=\"hidden\" name=\"todo\" value=\"view2\">");
         out.println("<input type=\"hidden\" name=\"name\" value=\"" + user + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
         if (skip_9_NoPosts == false) {      // if we did NOT skip the 9-hole No Posts
            out.println("<input type=\"hidden\" name=\"skip9NoPosts\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"Run Report w/o 9-Hole No Posts\" style=\"background:#8B8970\">");
         } else {
            out.println("<input type=\"hidden\" name=\"skip9NoPosts\" value=\"no\">");
            out.println("<input type=\"submit\" value=\"Run Report with 9-Hole No Posts\" style=\"background:#8B8970\">");
         }
         out.println("</form><br>");

        out.println("<form method=\"get\" action=\"Proshop_report_handicap\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"todo\" value=\"view2\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
        out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
        if (skip_9_NoPosts == false) {      // if we did NOT skip the 9-hole No Posts
            out.println("<input type=\"hidden\" name=\"skip9NoPosts\" value=\"no\">");
        } else {
            out.println("<input type=\"hidden\" name=\"skip9NoPosts\" value=\"yes\">");
        }
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></center>");

        if (!user.startsWith("proshop")) out.println("<BR>"); 

        if (!user.startsWith("proshop") || SystemUtils.verifyProAccess(req, "TOOLS_EMAIL", con, out)) {
           
            out.println("<center><form method=\"get\" action=\"Proshop_report_handicap\">");
            out.println("<input type=\"hidden\" name=\"todo\" value=\"view2\">");
            out.println("<input type=\"hidden\" name=\"sendEmailAll\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"skip9NoPosts\" value=\"" + (skip_9_NoPosts ? "yes" : "no") + "\">");
            out.println("<input type=\"submit\" value=\"Send Email to All Members In This Report That Did Not Post\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></center>");

            if (!user.startsWith("proshop")) out.println("<BR>"); 
        }
        
        out.println("" +
            "<table align=center><tr>");
        
        if (user.startsWith("proshop") && req.getParameter("fromMenu") == null) {
               out.println("<td><form action=Proshop_oldsheets method=post>" +
                  "<input type=\"hidden\" name=\"date\" value=\"" + date + "\">" +
                  "<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
        } else {
            out.println("<td><form action=Proshop_report_handicap method=get>" +
               "<input type=\"hidden\" name=\"todo\" value=\"view\">");           
        }
        out.println("<input type=submit value=\"Back\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "<td>&nbsp; &nbsp; &nbsp;</td>" +
            "<td><form action=" +home+ "><input type=submit value=\"Home\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "</tr></table>");
        
        if (!user.startsWith("proshop")) out.println("<BR>"); 

    } // end if not excel
    
    if (sendEmails == false) {      // if doing report

       //out.println("<table align=center width=400>");
       out.println("<table align=center cellspacing=0 cellpadding=7 border=1 bgcolor=#F5F5DC>"); // style=\"border: 1px solid #336633\"  F5F5DC  86B686
       out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                     "<td align=center><font size=3><b>Member</b></font></td>" +
                     "<td align=center><font size=3><b>Time</b></font></td>" +
                     ((!course.equals("-ALL-")) ? "" : "<td align=center><font size=3><b>Course</b></font></td>") +
                     "<td align=center nowrap><font size=3><b>9 or 18</b></font></td>" +
                     "<td align=center><font size=3><b>Tees</b></font></td>" +
                     "<td align=center><font size=3><b>Type</b></font></td>" +
                     "<td align=center><font size=3><b>Posted Score</b></font></td>");
       if (excel == false) out.println("<td align=center><font size=3><b>Send Email</b></font></td>");
       out.println("</tr>");

    }

    try {

        // all posted scores for players on given day from old tee sheets
        pstmt = con.prepareStatement("" +
                "SELECT tp.time, tp.courseName, tp.p91, tp.p92, tp.p93, tp.p94, tp.p95, sp.score, sp.type, t.tee_name, " +
                    "CONCAT(m.name_last, ', ', m.name_first) AS fullName, m.email, m.username, " +
                    "IF(m.username=tp.username1, 1, 0) AS pos1, " +
                    "IF(m.username=tp.username2, 1, 0) AS pos2, " +
                    "IF(m.username=tp.username3, 1, 0) AS pos3, " +
                    "IF(m.username=tp.username4, 1, 0) AS pos4, " +
                    "IF(m.username=tp.username5, 1, 0) AS pos5 " +
                "FROM teepast2 tp, member2b m " +
              //"LEFT OUTER JOIN score_postings sp ON sp.hdcpNum = REPLACE(m.ghin, '-', '') AND sp.date = ? " +
                "LEFT OUTER JOIN score_postings sp ON sp.hdcpNum = CONVERT(CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED), CHAR) AND sp.date = ? " +
                "LEFT OUTER JOIN tees t ON t.tee_id = sp.tee_id " +
                "WHERE " +
                    "tp.date = ? AND " +
                    ((!course.equals("") && !course.equals("-ALL-")) ? "tp.courseName = ? AND " : "") +
                    "m.ghin <> '' AND " +
                    "( " +
                        "(tp.show1 = 1 AND tp.nopost1 = 0 AND tp.username1 = m.username) OR " +
                        "(tp.show2 = 1 AND tp.nopost2 = 0 AND tp.username2 = m.username) OR " +
                        "(tp.show3 = 1 AND tp.nopost3 = 0 AND tp.username3 = m.username) OR " +
                        "(tp.show4 = 1 AND tp.nopost4 = 0 AND tp.username4 = m.username) OR " +
                        "(tp.show5 = 1 AND tp.nopost5 = 0 AND tp.username5 = m.username) " +
                    ") " +
                "ORDER BY fullName, time;");

        pstmt.clearParameters();
        pstmt.setLong(1, date);
        pstmt.setLong(2, date);
        if (!course.equals("") && !course.equals("-ALL-")) pstmt.setString(3, course);
        rs = pstmt.executeQuery();

        while ( rs.next() ) {
           
            username = rs.getString("username");
            email = rs.getString("email");
            fullName = rs.getString("fullName");

            tmp_holes = 18;

            if (rs.getInt("p91") + rs.getInt("pos1") == 2 ||
               rs.getInt("p92") + rs.getInt("pos2") == 2 ||
               rs.getInt("p93") + rs.getInt("pos3") == 2 ||
               rs.getInt("p94") + rs.getInt("pos4") == 2 ||
               rs.getInt("p95") + rs.getInt("pos5") == 2) {

               tmp_holes = 9;
            }

            if (rs.getInt("score") != 0 || tmp_holes == 18 || skip_9_NoPosts == false) {    // skip if 9-hole round and user opted to skip 9-hole No Posts
                   
               if (sendEmails == false) {      // if doing report

                  if (skip_9hole && tmp_holes == 9) {

                     // for now lets just skip displaying these rows

                  } else {

                     out.println("<tr align=center bgcolor=" + ((rs.getInt("score") != 0) ? "#86B686" : "#FFFF8F") + ">" +
                                 "<td nowrap>" + rs.getString("fullName") + "</td>" +
                                 "<td nowrap>" + SystemUtils.getSimpleTime(rs.getInt("time") / 100, rs.getInt("time") % 100) + "</td>" +
                                 ((!course.equals("-ALL-")) ? "" : "<td nowrap>" + rs.getString("courseName") + "</td>") +
                                 "<td>" + tmp_holes + "</td>" +
                                 "<td nowrap>" + ((rs.getString("tee_name") == null) ? "&nbsp;" : rs.getString("tee_name")) + "</td>" +
                                 "<td>" + ((rs.getString("type") == null) ? "&nbsp;" : rs.getString("type")) + "</td>" +
                                 "<td>" + ((rs.getInt("score") == 0) ? "&nbsp;" : rs.getInt("score")) + "</td>");

                     if (excel == false) {

                        if (rs.getInt("score") == 0 && !email.equals("")) {  // if no score posted and user has an email

                           if (!fullName.equals(lastFullName)) {    // if new member 

                              out.println("<td nowrap align=center><a href=\"Send_email?" + ActionHelper.NEXT_ACTION + "=" + ActionHelper.ADD_TO_LIST + "&" + ActionHelper.SEARCH_TYPE + "=" + ActionHelper.SEARCH_MEMBERS + "&" + ActionHelper.SELECTED_ITEMS_STRING + "=" + username + "&email_orig_caller=report_handicap\" target=\"bot\" title=\"Send Email To This Member\" alt=\"Send Email\">");
                              out.println("Email</a></td>");

                           } else {

                              out.println("<td> &nbsp;</td>");
                           }

                           lastFullName = fullName;

                        } else {

                           out.println("<td>&nbsp;</td>");
                        }
                     }
                     out.println("</tr>");
                  }
               
               } else {

                  //  Sending emails to all non-posted users

                  if (rs.getInt("score") == 0 && !email.equals("")) {  // if no score posted and user has an email

                     if (!fullName.equals(lastFullName)) {    // if new member 

                        count++;
                        userList += ";" + username;       // add username to list for Send_email      
                     }

                     lastFullName = fullName;
                  }
               }
            }        // end of IF 18-hole or skip 9-hole rounds is false
        }   // end of WHILE

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading posted scores for handicap peer report.", exc.getMessage(), out, false);
        return;

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }


    out.println("</table>");

    if (excel == false && sendEmails == false) {     // if normal request

        out.println("<br><br>");

        out.println("<center><form method=\"get\" action=\"Proshop_report_handicap\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"todo\" value=\"view2\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
        out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></center>");

        if (!user.startsWith("proshop") || SystemUtils.verifyProAccess(req, "TOOLS_EMAIL", con, out)) {
           
            out.println("<center><form method=\"get\" action=\"Proshop_report_handicap\">");
            out.println("<input type=\"hidden\" name=\"todo\" value=\"view2\">");
            out.println("<input type=\"hidden\" name=\"sendEmailAll\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"submit\" value=\"Send Email to All Members In This Report That Did Not Post\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></center>");
        }
        
        out.println("" +
            "<table align=center><tr>");
        if (user.startsWith("proshop") && req.getParameter("fromMenu") == null) {
            out.println("<td><form action=Proshop_oldsheets method=post>" +
               "<input type=\"hidden\" name=\"date\" value=\"" + date + "\">" +
               "<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
        } else {
            out.println("<td><form action=Proshop_report_handicap method=get>" +
               "<input type=\"hidden\" name=\"todo\" value=\"view\">");           
        }
        out.println("<input type=submit value=\"Back\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "<td>&nbsp; &nbsp; &nbsp;</td>" +
            "<td><form action=" +home+ "><input type=submit value=\"Home\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "</tr></table>");
        
    } // end if not excel

    if (sendEmails == true) {

      if (count > 0) {

         out.println("<center><font size=\"3\"><br><p align=center>You have requested to send an email to all " +count+ " members in this report (that have email addresses).</p>");
         out.println("<br><p align=center>Would you like to continue?</p>");
         out.println("<br><p align=center>");

         out.println("<form action=Send_email method=get>"
                  + "<input type=hidden name=" + ActionHelper.NEXT_ACTION + " value=" + ActionHelper.ADD_TO_LIST + ">"
                  + "<input type=hidden name=" + ActionHelper.SEARCH_TYPE + " value=" + ActionHelper.SEARCH_MEMBERS + ">"
                  + "<input type=hidden name=" + ActionHelper.SELECTED_ITEMS_STRING + " value=" + userList + ">"
                  + "<input type=hidden name=\"email_orig_caller\" value=\"report_handicap\">"
                  + "<input type=submit value=\"Yes\" style=\"width: 80px; background:#F5F5DC\">");
         out.println("</form></p>");  

         out.println("<br><p align=center>");
         out.println("<form action=Proshop_report_handicap method=get>"
                     + "<input type=hidden name=todo value=view2>"
                     + "<input type=\"hidden\" name=\"course\" value=\"" + course + "\">" 
                     + "<input type=\"hidden\" name=\"date\" value=\"" + date + "\">"
                     + "<input type=submit value=\"No, Go Back\" style=\"width: 90px; background:#8B8970\">");
         out.println("</form></p></font></center>");    

      } else {

         out.println("<center><font size=\"3\"><br><p align=center>Sorry, there were no members found in this report with a non-post and a valid email address.</p>");
         out.println("<br><p align=center>");
         out.println("<form action=Proshop_report_handicap method=get>"
                     + "<input type=hidden name=todo value=view2>"
                     + "<input type=\"hidden\" name=\"course\" value=\"" + course + "\">" 
                     + "<input type=\"hidden\" name=\"date\" value=\"" + date + "\">"
                     + "<input type=submit value=\"Return\" style=\"width: 80px; background:#8B8970\">");
         out.println("</form></p></font></center>");    
      }
    }
        
 }


private void getNonPosters(HttpServletRequest req, HttpSession session, Connection con, PrintWriter out) {

    Statement stmt = null;
    ResultSet rs = null;

    String user = (String)session.getAttribute("user");   // get username
    String club = (String)session.getAttribute("club");   // get club name

    
    // define standard defaults
    
    String defaultPeriod = "30";
    int defaultGrace = 3;
    String defaultRptType = "detail";
    String defaultGender = "MF";
    String defaultSort= "last";
    int defaultDsplyMType = 0;
    int defaultIncEmail = 0;
    
    
    // define club specific defaults
    if (club.equals("mpccpb")) {

        defaultPeriod = "90";
        defaultGrace = 1;
        defaultRptType = "summary";
        defaultGender = "M";
        defaultSort= "count";
        defaultDsplyMType = 0;
        defaultIncEmail = 0;
        
    }
    
    
    // summary report can be sorted by name or by # of missing postings
    // detail report can be sorted by name or by oldest missing posting
    // (summary can't sort by oldest round and detail can't sort by count)
    
    out.println("<script type=\"text/javascript\">");
    out.println("function updateSorts(selectedIndex) {");
    out.println(" var optSort = document.forms['frmHdcpRpt'].sort;");
    out.println(" if (optSort) {");
    out.println("  for (i=optSort.options.length-1;i>=0;i--) {");
    out.println("   optSort.remove(i);");
    out.println("   //if(optSort.options[i].selected) optSort.remove(i);");
    out.println("  }");
    out.println("  if (selectedIndex == 0) { ");
    out.println("   // detailed");
    out.println("   optn = document.createElement(\"OPTION\");");
    out.println("   optn.text = 'By Last Name';");
    out.println("   optn.value = 'last';");
    out.println("   optSort.options.add(optn);");
    out.println("   optn = document.createElement(\"OPTION\");");
    out.println("   optn.text = 'By Oldest Round';");
    out.println("   optn.value = 'oldest';");
    out.println("   optSort.options.add(optn);");
    out.println("  } else { ");
    out.println("   // summary");
    out.println("   optn = document.createElement(\"OPTION\");");
    out.println("   optn.text = 'By Last Name';");
    out.println("   optn.value = 'last';");
    out.println("   optSort.options.add(optn);");
    out.println("   optn = document.createElement(\"OPTION\");");
    out.println("   optn.text = 'By Total Missing';");
    out.println("   optn.value = 'count';");
    out.println("   optSort.options.add(optn);");
    out.println("  }");
    out.println(" }");
    out.println("}");
    out.println("</script>");


    out.println("<p align=center><font size=5>Missed Postings by Member</font></p>");

    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

    out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\" width=\"600\">");
    out.println("<tr><td>");
    out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
    out.println("<p>Select the days you wish to look back as well as a grace period.&nbsp; <!--If you wish to look back at all ");
      out.println("play history then specify a zero.&nbsp; -->If you wish to allow a grace period you can specify the number ");
      out.println("of days.&nbsp; <!--Entering a zero for grace period means rounds played yesterday will be inluded.&nbsp; -->");
      out.println("Slecting '1' for grace period means rounds played yesteday will be excluded from the report.");
      out.println("</p></font>");
    out.println("</td></tr></table>");

    out.println("<br><br>");

    //out.println("<br><p align=center><font size=3><b>Configure Report</b></font></p>");

    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" align=\"center\" width=\"400\">");

    out.println("<form method=post action=\"Proshop_report_handicap\" name=\"frmHdcpRpt\">");
    out.println("<input type=hidden name=todo value='doNonPosters'>");

    out.println("<input type=hidden name=clubid value='0'>");
    out.println("<input type=hidden name=associd value='0'>");

    out.println("<tr><td colspan=2 align=center><font size=3><b>Configure Report</b></font></td></tr>");
    
    out.println("<tr>" +
                 "<td><font size=2>Days To Look Back</td>" +
                 "<td>&nbsp;&nbsp;<input type=text name=period value=\"" + defaultPeriod + "\" size=3 maxlength=3></td>" +
                "</tr>");

    out.print("<tr><td><font size=2>Grace Period</td>");
    out.print("<td>&nbsp;&nbsp;");
     out.print("<select name=grace size=1>");
     for (int i=1;i<=7;i++) {
       Common_Config.buildOption(i, i, defaultGrace, out);
     }
     out.print("</select>");
    out.println("</td></tr>");


    out.println("<tr><td><font size=2>Report Type</font></td>");
    out.println("<td>&nbsp;&nbsp;<select name=rptType size=1 onchange=\"updateSorts(this.selectedIndex)\">");
       Common_Config.buildOption("detail", "Detail", defaultRptType, out);
       Common_Config.buildOption("summary", "Summary", defaultRptType, out);
    out.println("</select>");
    out.println("</td></tr>");
/*
    out.println("<tr><td><font size=2>Match Dates</font></td>");
    out.println("<td>&nbsp;&nbsp;" +
            "<select name=matchDates size=1>" +
             "<option value=yes>Yes" +
             "<option value=no>No" +
            "</select>" +
            "</td></tr>");
*/
    out.println("<tr><td><font size=2>Gender Filtering</td>");
    out.println("<td>&nbsp;&nbsp;<select name=gender size=1>");
       Common_Config.buildOption("MF", "None", defaultGender, out);
       Common_Config.buildOption("M", "Men Only", defaultGender, out);
       Common_Config.buildOption("F", "Ladies Only", defaultGender, out);
    out.println("</select>");
    out.println("</td></tr>");
    
    out.println("<tr><td><font size=2>Sorting Method</td>");
    out.println("<td>&nbsp;&nbsp;" +
            "<select name=sort size=1>");
       Common_Config.buildOption("last", "By Last Name", defaultSort, out);
       if (!defaultRptType.equals("summary")) Common_Config.buildOption("oldest", "By Oldest Round", defaultSort, out);    // not valid for summary
       if (defaultRptType.equals("summary")) Common_Config.buildOption("count", "By Total Missing", defaultSort, out);     // not valid for detail
    out.println("</select>");
    out.println("</td></tr>");

    out.println("<tr><td nowrap><font size=2>Include Member Type</td>");
    out.println("<td>&nbsp;&nbsp;<select name=dsplyMType size=1>");
       Common_Config.buildOption(0, "No", defaultDsplyMType, out);
       Common_Config.buildOption(1, "Yes", defaultDsplyMType, out);
    out.println("</select>");
    out.println("</td></tr>");

    out.println("<tr><td nowrap><font size=2>Include Email Address</td>");
    out.println("<td>&nbsp;&nbsp;<select name=incEmail size=1>");
       Common_Config.buildOption(0, "No", defaultIncEmail, out);
       Common_Config.buildOption(1, "Yes", defaultIncEmail, out);
    out.println("</select>");
    out.println("</td></tr>");

    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT * FROM hdcp_club_num");
        rs.last();

        if (rs.getRow() > 1) {

            out.print("<tr><td><font size=2>Club Number</font></td><td>&nbsp;&nbsp;");
            out.print("<select name=club_num_id size=1>");

            rs.beforeFirst();

            while (rs.next()) {

                out.print("<option value=" + rs.getInt("hdcp_club_num_id") + ">" + rs.getString("club_name") + " (" + rs.getString("club_num") + ")</option>"); // + " (" + rs.getString("club_name")
            }

            out.print("</select>");
            out.println("</td></tr>");

        }

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

    }


    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT * FROM hdcp_assoc_num");
        rs.last();

        if (rs.getRow() > 1) {

            out.print("<tr><td><font size=2>Club Association</font></td><td>&nbsp;&nbsp;");
            out.print("<select name=club_num_id size=1>");

            rs.beforeFirst();

            while (rs.next()) {

                out.print("<option value=" + rs.getInt("hdcp_assoc_num_id") + ">" + rs.getString("assoc_name") + " (" + rs.getString("assoc_num") + ")</option>"); // + " (" + rs.getString("club_name")
            }

            out.print("</select>");
            out.println("</td></tr>");
        }

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

    }


    out.println("<tr><td colspan=2 align=center><br>");
    if (user.startsWith("proshop")) {
        out.println("<input type=button value=\" Home \" onclick=\"location.href='Proshop_announce'\">");
    } else {
        out.println("<input type=button value=\" Home \" onclick=\"location.href='Member_announce'\">");
    }
    out.println("&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<input type=submit value=\" Get Report \"><br>&nbsp;</td></tr>");

    out.println("</form>");

    out.println("</table><br><br>");

}


private void doNonPosters(HttpServletRequest req, HttpSession session, Connection con, PrintWriter out) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String fullName = "";
    String tee_name = "";
    String sdate = "";
    String tmp_holes = "";
    String email = "";
    String lastGhin = "";
    String username = "";
    String userList = "";
    String p9Filter1 = "";
    String p9Filter2 = "";
    String p9Filter3 = "";
    String p9Filter4 = "";
    String p9Filter5 = "";

    String user = (String)session.getAttribute("user");   // get username
    String club = (String)session.getAttribute("club");   // get club name

    String home = "Proshop_announce";

    if (!user.startsWith("proshop")) home = "Member_announce"; 

    String period = (req.getParameter("period") != null) ? req.getParameter("period")  : "30";
    String sgrace = (req.getParameter("grace") != null) ? req.getParameter("grace")  : "0";
    String clubid = (req.getParameter("clubid") != null) ? req.getParameter("clubid")  : "0";
    String associd = (req.getParameter("associd") != null) ? req.getParameter("associd")  : "0";
    String rptType = (req.getParameter("rptType") != null) ? req.getParameter("rptType")  : "";
    String incEmail = (req.getParameter("incEmail") != null) ? req.getParameter("incEmail")  : "0";
    String sort = (req.getParameter("sort") != null) ? req.getParameter("sort")  : "";
    String gender = (req.getParameter("gender") != null) ? req.getParameter("gender")  : "";
    String orderby = "fullName, tp.date, time";

    int dsplyMType = (req.getParameter("dsplyMType") != null && req.getParameter("dsplyMType").equals("1")) ? 1 : 0;
    //int match_dates = (req.getParameter("matchDates") != null && req.getParameter("matchDates").equals("yes")) ? 1 : 0;
    int multi = SystemUtils.getMulti(con);
    int club_id = 0;
    int assoc_id = 0;
    int days = 0;
    int grace = 0;
    int count = 0;
    
    boolean excel = false;
    boolean sendEmails = false;
    boolean skip_9_NoPosts = true;                // default to not show 9-hole rounds without a post
    
    if (req.getParameter("excel") != null) {     // if excel output requested
       
       excel = true;
    }

    if (req.getParameter("sendEmailAll") != null) {     // if user clicked on "Send Email to All Members" below
       
       sendEmails = true;
       excel = false;
    }

    if (req.getParameter("skip9NoPosts") != null) {         // if skip 9-hole no posts specified

        if (req.getParameter("skip9NoPosts").equals("no")) skip_9_NoPosts = false;
    }
    

    try {

        days = Integer.parseInt(period);
        club_id = Integer.parseInt(clubid);
        assoc_id = Integer.parseInt(associd);
        grace = Integer.parseInt(sgrace);

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error converting strings and loading multi.", exc.getMessage(), out, false);
        return;
    }

    // enforce sane defaults
    if (!sort.equals("count") && !sort.equals("oldest")) sort = "last";

    if (sort.equals("oldest")) {

        orderby = "tp.date, time, fullname";

    }

    if (!rptType.equals("detail")) {

        rptType = "summary";

        if (sort.equals("last")) {
            orderby = "fullname, c DESC";
        } else {
            orderby = "c DESC, fullname";
        }
    }

    if (skip_9_NoPosts == true) {    // skip if 9-hole round and user opted to skip 9-hole No Posts
       
       p9Filter1 = " AND tp.p91 = 0";    // filter out all 9-hole rounds
       p9Filter2 = " AND tp.p92 = 0";  
       p9Filter3 = " AND tp.p93 = 0";  
       p9Filter4 = " AND tp.p94 = 0";  
       p9Filter5 = " AND tp.p95 = 0";  
       
    } else {
       
       p9Filter1 = "";                  // include all 9-hole no-post rounds
       p9Filter2 = "";             
       p9Filter3 = "";             
       p9Filter4 = "";             
       p9Filter5 = "";             
    }
                   
    out.println("<br><p align=center><font size=5>Missed Postings by Member</font>"); // Members With Missing Posted Scores

    out.println("<br><br><font size=3>for the last " + days + " days</font>");
    
    if (sendEmails == false) {     // if normal request
    
      out.println("<br><br><font size=2>" + buildDisplayDateTime() + "</font>");
      if (grace > 0) out.println("<br><font size=2>with a grace period of " + grace + " days");
    }
    out.println("</p>");

    //  Print Excel/Back/Home buttons
    if (excel == false && sendEmails == false) {     // if normal request

        out.println("<p align=\"center\">");
        if (skip_9_NoPosts == false) {      // if we did NOT skip the 9-hole No Posts
            out.println("<strong>NOTE:</strong>&nbsp; This report includes all 9-hole rounds.");
        } else {
            out.println("<strong>NOTE:</strong>&nbsp; This report does NOT include 9-hole rounds that are missing a posted score,<br>"
                     + "as those scores may be held while waiting to be combined with another 9-hole score.");
        }
        out.println("</p><center>");
        if (rptType.equals("detail")) {      // only allow this option on detailed reports
            out.println("<form method=\"post\" action=\"Proshop_report_handicap\">");
            out.println("<input type=\"hidden\" name=\"todo\" value=\"doNonPosters\">");
            out.println("<input type=\"hidden\" name=\"grace\" value=\"" + grace + "\">");
            out.println("<input type=\"hidden\" name=\"period\" value=\"" + days + "\">");
            out.println("<input type=\"hidden\" name=\"clubid\" value=\"" + clubid + "\">");
            out.println("<input type=\"hidden\" name=\"associd\" value=\"" + associd + "\">");
            out.println("<input type=\"hidden\" name=\"rptType\" value=\"" + rptType + "\">");
            out.println("<input type=\"hidden\" name=\"incEmail\" value=\"" + incEmail + "\">");
            out.println("<input type=\"hidden\" name=\"gender\" value=\"" + gender + "\">");
            out.println("<input type=\"hidden\" name=\"dsplyMType\" value=\"" + dsplyMType + "\">");
            out.println("<input type=\"hidden\" name=\"sort\" value=\"" + sort + "\">");
            if (skip_9_NoPosts == false) {      // if we did NOT skip the 9-hole No Posts
                  out.println("<input type=\"hidden\" name=\"skip9NoPosts\" value=\"yes\">");
                  out.println("<input type=\"submit\" value=\"Run Report w/o 9-Hole No Posts\" style=\"background:#8B8970\">");
            } else {
                  out.println("<input type=\"hidden\" name=\"skip9NoPosts\" value=\"no\">");
                  out.println("<input type=\"submit\" value=\"Run Report with 9-Hole No Posts\" style=\"background:#8B8970\">");
            }
            out.println("</form><br>");
        }

        out.println("<form method=\"post\" action=\"Proshop_report_handicap\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"todo\" value=\"doNonPosters\">");
        out.println("<input type=\"hidden\" name=\"grace\" value=\"" + grace + "\">");
        out.println("<input type=\"hidden\" name=\"period\" value=\"" + days + "\">");
        out.println("<input type=\"hidden\" name=\"clubid\" value=\"" + clubid + "\">");
        out.println("<input type=\"hidden\" name=\"associd\" value=\"" + associd + "\">");
        out.println("<input type=\"hidden\" name=\"rptType\" value=\"" + rptType + "\">");
        out.println("<input type=\"hidden\" name=\"incEmail\" value=\"" + incEmail + "\">");
        out.println("<input type=\"hidden\" name=\"gender\" value=\"" + gender + "\">");
        out.println("<input type=\"hidden\" name=\"dsplyMType\" value=\"" + dsplyMType + "\">");
        out.println("<input type=\"hidden\" name=\"sort\" value=\"" + sort + "\">");
        if (skip_9_NoPosts == false) {      // if we did NOT skip the 9-hole No Posts
            out.println("<input type=\"hidden\" name=\"skip9NoPosts\" value=\"no\">");
        } else {
            out.println("<input type=\"hidden\" name=\"skip9NoPosts\" value=\"yes\">");
        }
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></center>");

        if (!user.startsWith("proshop") || SystemUtils.verifyProAccess(req, "TOOLS_EMAIL", con, out)) {
           
            out.println("<center><form method=\"post\" action=\"Proshop_report_handicap\">");
            out.println("<input type=\"hidden\" name=\"todo\" value=\"doNonPosters\">");
            out.println("<input type=\"hidden\" name=\"sendEmailAll\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"grace\" value=\"" + grace + "\">");
            out.println("<input type=\"hidden\" name=\"period\" value=\"" + days + "\">");
            out.println("<input type=\"hidden\" name=\"clubid\" value=\"" + clubid + "\">");
            out.println("<input type=\"hidden\" name=\"associd\" value=\"" + associd + "\">");
            out.println("<input type=\"hidden\" name=\"rptType\" value=\"" + rptType + "\">");
            if (skip_9_NoPosts == false) {      // if we did NOT skip the 9-hole No Posts
               out.println("<input type=\"hidden\" name=\"skip9NoPosts\" value=\"no\">");
            } else {
               out.println("<input type=\"hidden\" name=\"skip9NoPosts\" value=\"yes\">");
            }
            out.println("<input type=\"submit\" value=\"Send Email to All Members In This Report\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></center>");
        }

        out.println("" +
            "<table align=center><tr>" +
            "<td><form action=Proshop_report_handicap method=get><input type=hidden name=todo value=nonPosters><input type=submit value=\"Back\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "<td>&nbsp; &nbsp; &nbsp;</td>" +
            "<td><form action=" +home+ "><input type=submit value=\"Home\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "</tr></table><BR>");
    } // end if not excel

    if (sendEmails == false) {     // if not sending emails

         // detail  report columns - member, (member type), date, course
         // summary report columns - member, (member type), count, oldest date

         //out.println("<table align=center>");
         out.println("<table width=400 align=center cellspacing=0 cellpadding=5 border=1 bgcolor=#F5F5DC>"); // style=\"border: 1px solid #336633\"  F5F5DC  86B686
         out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                        "<td nowrap align=center><font size=3><b>&nbsp;Member's Name&nbsp;</b></font></td>" +
                        ((dsplyMType == 1) ? "<td align=center nowrap><font size=3><b>&nbsp;Member Type&nbsp;</b></font></td>" : "") +
                        "<td align=center nowrap><font size=3><b>&nbsp;Handicap #&nbsp;</b></font></td>");
         if (rptType.equals("summary")) {
            out.println("<td align=center><font size=3><b>Count</b></font></td>");
            out.println("<td align=center nowrap><font size=3><b>&nbsp;Oldest Date&nbsp;</b></font></td>");
         } else {
            out.println("<td align=center><font size=3><b>Date</b></font></td>");
            if (multi == 1) out.println("<td align=center><font size=3><b>Course</b></font></td>");
            out.println("<td align=center><font size=3><b>9/18</b></font></td>");
         }
         if (incEmail.equals("1")) out.println("<td align=center><font size=3><b>Email</b></font></td>");

         if ((!user.startsWith("proshop") || SystemUtils.verifyProAccess(req, "TOOLS_EMAIL", con, out)) && excel == false) {    // add send email column?

            out.println("<td align=center><font size=3><b>Send Email</b></font></td>");
         }

         out.println("</tr>");      // end of header row
    }

    try {

        // find all members with old tee times without a posted score

        String sql = "" +
                "SELECT " + ((rptType.equals("summary")) ? "COUNT(*) AS c, DATE_FORMAT(MIN(tp.date), '%m-%d-%Y') AS sdate, CONCAT(m.name_first, ' ', IF(m.name_mi != '', CONCAT(m.name_mi, ' '), ''), m.name_last) AS searchName," : "DATE_FORMAT(tp.date, '%m-%d-%Y') AS sdate,") + " " +
                    "tp.time, tp.courseName, tp.username1, tp.username2, tp.username3, tp.username4, tp.username5, tp.p91, tp.p92, tp.p93, tp.p94, tp.p95, " +
                    "m.ghin, m.m_type, CONCAT(m.name_last, ', ', m.name_first) AS fullName, m.email, m.username " +
                "FROM teepast2 tp INNER JOIN member2b m ON " +
                    "( " +
                        "(tp.show1 = 1 AND tp.nopost1 = 0 " +p9Filter1+ " AND tp.username1 = m.username) OR " +
                        "(tp.show2 = 1 AND tp.nopost2 = 0 " +p9Filter2+ " AND tp.username2 = m.username) OR " +
                        "(tp.show3 = 1 AND tp.nopost3 = 0 " +p9Filter3+ " AND tp.username3 = m.username) OR " +
                        "(tp.show4 = 1 AND tp.nopost4 = 0 " +p9Filter4+ " AND tp.username4 = m.username) OR " +
                        "(tp.show5 = 1 AND tp.nopost5 = 0 " +p9Filter5+ " AND tp.username5 = m.username) " +
                    ") " +
                "LEFT OUTER JOIN score_postings sp ON sp.hdcpNum = CONVERT(CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED), CHAR) AND sp.date = tp.date " +
                "WHERE 1 = ? AND " +
                    "tp.date > DATE_FORMAT(DATE_ADD(now(), INTERVAL -" +days+ " DAY), '%Y%m%d') AND " +
                    ((grace > 0) ? "tp.date < DATE_FORMAT(DATE_ADD(now(), INTERVAL -" +grace+ " DAY), '%Y%m%d') AND " : "") +
                    "m.ghin <> '' AND ISNULL(sp.score) " +
                    ((club_id != 0) ? "AND m.hdcp_club_num_id = ? " : "") +
                    ((assoc_id != 0) ? "AND m.hdcp_assoc_num_id = ? " : "") +
                    ((gender.equals("M")) ? "AND m.gender = 'M' " : "") +
                    ((gender.equals("F")) ? "AND m.gender = 'F' " : "") +
                    ((rptType.equals("summary")) ? "GROUP BY m.username " : "") +
                "ORDER BY " + orderby + ";";

        if (Common_Server.SERVER_ID == 4) out.println("<!-- " + sql + " -->");

        pstmt = con.prepareStatement(sql);

        int parmIdx = 1;
        pstmt.clearParameters();
        pstmt.setInt(parmIdx, 1);
        if (club_id != 0) {
            parmIdx++;
            pstmt.setInt(parmIdx, club_id);
        }
        if (assoc_id != 0) {
            parmIdx++;
            pstmt.setInt(parmIdx, assoc_id);
        }
        rs = pstmt.executeQuery();

        while ( rs.next() ) {
           
            username = rs.getString("username");

            if (sendEmails == false) {     // if not sending emails

               out.println("<tr>" +
                           "<td nowrap>" + ((rptType.equals("summary")) ? "<a href=\"Proshop_report_handicap?search=Search" +
                           "&popup=yes&name=" + rs.getString("searchName") + "\" style=\"color:black\" target=_hdcpPopup>" : "") + rs.getString("fullName") +
                           ((rptType.equals("summary")) ? "</a>" : "") + "</td>" +
                           ((dsplyMType == 1) ? "<td nowrap align=center>" + rs.getString("m_type") + "</td>" : "") +
                           "<td nowrap align=center>" + formatHdcpNum(rs.getString("ghin"), club.equals("mpccpb") ? 2 : 1) + "</td>");

               if (rptType.equals("summary")) {

                  out.print("<td align=center>" + rs.getInt("c") + "</td>");
                  out.print("<td align=center>" + rs.getString("sdate") + "</td>");

               } else {
                  
                  int tmp_918 = 18;
                  
                  if (username.equals(rs.getString("username1"))) {
                     if (rs.getInt("p91") == 1) tmp_918 = 9;
                  } else if (username.equals(rs.getString("username2"))) {
                     if (rs.getInt("p92") == 1) tmp_918 = 9;
                  } else if (username.equals(rs.getString("username3"))) {
                     if (rs.getInt("p93") == 1) tmp_918 = 9;
                  } else if (username.equals(rs.getString("username4"))) {
                     if (rs.getInt("p94") == 1) tmp_918 = 9;
                  } else if (username.equals(rs.getString("username5"))) {
                     if (rs.getInt("p95") == 1) tmp_918 = 9;
                  }

                  out.print("<td nowrap align=center>" +rs.getString("sdate") + "</td>");
                  if (multi == 1) out.println("<td nowrap align=center>" + rs.getString("courseName") + "</td>");
                  out.print("<td nowrap align=center>" +tmp_918+ "</td>");

               }

               if (incEmail.equals("1")) out.println("<td>" + rs.getString("email") + "</td>");

               if ((!user.startsWith("proshop") || SystemUtils.verifyProAccess(req, "TOOLS_EMAIL", con, out)) && excel == false) {    // add send email column?

                  if (!lastGhin.equals(rs.getString("ghin")) && !rs.getString("email").equals("")) {    // new member with email?

                     out.println("<td nowrap align=center><a href=\"Send_email?" + ActionHelper.NEXT_ACTION + "=" + ActionHelper.ADD_TO_LIST + "&" + ActionHelper.SEARCH_TYPE + "=" + ActionHelper.SEARCH_MEMBERS + "&" + ActionHelper.SELECTED_ITEMS_STRING + "=" + username + "&email_orig_caller=report_handicap\" target=\"bot\" title=\"Send Email To This Member\" alt=\"Send Email\">");
                     out.println("Email</a></td>");

                  } else {

                     out.println("<td>&nbsp;</td>");
                  }
   
                  lastGhin = rs.getString("ghin");       // save this member's GHIN number
               }

               out.println("</tr>");
               
            } else {
               
               //  user wants to send emails to members in this report - just gather the usernames
               
               if (!lastGhin.equals(rs.getString("ghin")) && !rs.getString("email").equals("")) {    // new member with email?

                  userList += ";" + username;       // add username to list for Send_email 
                  count++;                          // count number of unique members
               }

               lastGhin = rs.getString("ghin");     // save this member's GHIN number
            }
        }

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading posted scores for non posters report.", exc.getMessage(), out, false);

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }


    out.println("</table>");

    if (excel == false && sendEmails == false) {     // if normal request

        out.println("<br><br>");

        out.println("<center><form method=\"post\" action=\"Proshop_report_handicap\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"todo\" value=\"doNonPosters\">");
        out.println("<input type=\"hidden\" name=\"grace\" value=\"" + grace + "\">");
        out.println("<input type=\"hidden\" name=\"period\" value=\"" + days + "\">");
        out.println("<input type=\"hidden\" name=\"clubid\" value=\"" + clubid + "\">");
        out.println("<input type=\"hidden\" name=\"associd\" value=\"" + associd + "\">");
        out.println("<input type=\"hidden\" name=\"rptType\" value=\"" + rptType + "\">");
        out.println("<input type=\"hidden\" name=\"incEmail\" value=\"" + incEmail + "\">");
        out.println("<input type=\"hidden\" name=\"gender\" value=\"" + gender + "\">");
        out.println("<input type=\"hidden\" name=\"dsplyMType\" value=\"" + dsplyMType + "\">");
        out.println("<input type=\"hidden\" name=\"sort\" value=\"" + sort + "\">");
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></center>");

        if (!user.startsWith("proshop") || SystemUtils.verifyProAccess(req, "TOOLS_EMAIL", con, out)) {
           
            out.println("<center><form method=\"post\" action=\"Proshop_report_handicap\">");
            out.println("<input type=\"hidden\" name=\"todo\" value=\"doNonPosters\">");
            out.println("<input type=\"hidden\" name=\"sendEmailAll\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"grace\" value=\"" + grace + "\">");
            out.println("<input type=\"hidden\" name=\"period\" value=\"" + days + "\">");
            out.println("<input type=\"hidden\" name=\"clubid\" value=\"" + clubid + "\">");
            out.println("<input type=\"hidden\" name=\"associd\" value=\"" + associd + "\">");
            out.println("<input type=\"hidden\" name=\"rptType\" value=\"" + rptType + "\">");
            out.println("<input type=\"submit\" value=\"Send Email to All Members In This Report\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></center>");
        }
        
        out.println("" +
            "<table align=center><tr>" +
            "<td><form action=Proshop_report_handicap method=get><input type=hidden name=todo value=nonPosters><input type=submit value=\"Back\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "<td>&nbsp; &nbsp; &nbsp;</td>" +
            "<td><form action=" +home+ "><input type=submit value=\"Home\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "</tr></table>");

    } // end if not excel
    
    
    if (sendEmails == true) {     // if request to send emails to all members in the report
       
       if (count > 0) {

          out.println("<center><font size=\"3\"><br><p align=center>You have requested to send an email to all " +count+ " members in this report (that have email addresses).</p>");
          out.println("<br><p align=center>Would you like to continue?</p>");
          out.println("<br><p align=center>");

          out.println("<form action=Send_email method=get>"
                  + "<input type=hidden name=" + ActionHelper.NEXT_ACTION + " value=" + ActionHelper.ADD_TO_LIST + ">"
                  + "<input type=hidden name=" + ActionHelper.SEARCH_TYPE + " value=" + ActionHelper.SEARCH_MEMBERS + ">"
                  + "<input type=hidden name=" + ActionHelper.SELECTED_ITEMS_STRING + " value=" + userList + ">"
                  + "<input type=hidden name=\"email_orig_caller\" value=\"report_handicap\">"
                  + "<input type=submit value=\"Yes\" style=\"width: 80px; background:#F5F5DC\">");
          out.println("</form></p>");     

          out.println("<br><p align=center>");
          out.println("<form action=Proshop_report_handicap method=get><input type=hidden name=todo value=nonPosters><input type=submit value=\"No, Go Back\" style=\"width: 90px; background:#8B8970\">");
          out.println("</form></p></font></center>");    
         
       } else {
          
          out.println("<center><font size=\"3\"><br><p align=center>Sorry, there were no members found in this report with a valid email address.</p>");
          out.println("<br><p align=center>");
          out.println("<form action=Proshop_report_handicap method=get><input type=hidden name=todo value=nonPosters><input type=submit value=\"Return\" style=\"width: 80px; background:#8B8970\">");
          out.println("</form></p></font></center>");    
        }
    }
}



/*
 *   Display a summary report for all members - rounds played vs scores posted
 */
private void doAllMems(HttpServletRequest req, HttpSession session, Connection con, PrintWriter out) {

   String thisuser = (String)session.getAttribute("user");   // get username

   //  First, prompt the user for a date range or time period for the report

   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"3\">");
   out.println("<b>Rounds Played vs Scores Posted Summary Report</b><br>");
   out.println("</font><font color=\"#FFFFFF\" size=\"2\">");
   out.println("<br>Select the date range below.<br>");
   out.println("<b>Note:</b>  Only rounds before today will be included in the counts.<br><br>");
   out.println("Click on <b>Continue</b> to generate the report.");
   out.println("</font></td></tr></table><br>");

   //
   // Build the custom date range calendars and form
   //
   //Common_Config.buildReportCals("Proshop_report_handicap", out);

    Calendar cal = new GregorianCalendar();     // get today's date
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH) + 1;
    int day = cal.get(Calendar.DAY_OF_MONTH);


    //
    //   Build and output the custom date range calendars - with form
    //
    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
    
    if (thisuser.startsWith("proshop")) {
        
          out.println("<form action=\"Proshop_report_handicap\" method=\"post\" target=\"bot\">");
          
    } else {
        
          out.println("<form action=\"Proshop_report_handicap\" method=\"post\">");
    }
      
      out.println("<input type=\"hidden\" name=\"custom2\" value=\"yes\">");

      out.println("<tr><td>");
        out.println("<font size=\"2\">");
        out.println("<div id=\"awmobject1\">");        // allow menus to show over this box
        out.println("Start Date:&nbsp;&nbsp;&nbsp;");
        out.println("Month:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"smonth\">");
             out.println("<option selected value=\"01\">JAN</option>");
             out.println("<option value=\"02\">FEB</option>");
             out.println("<option value=\"03\">MAR</option>");
             out.println("<option value=\"04\">APR</option>");
             out.println("<option value=\"05\">MAY</option>");
             out.println("<option value=\"06\">JUN</option>");
             out.println("<option value=\"07\">JUL</option>");
             out.println("<option value=\"08\">AUG</option>");
             out.println("<option value=\"09\">SEP</option>");
             out.println("<option value=\"10\">OCT</option>");
             out.println("<option value=\"11\">NOV</option>");
             out.println("<option value=\"12\">DEC</option>");
        out.println("</select>");

        out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"sday\">");

        for (int i=1; i<=31; i++) {

            out.println("<option value=\"" + i + "\">" + i + "</option>");

        }

        out.println("</select>");

        out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"syear\">");

        for (int i=2003; i<=year; i++) {

            Common_Config.buildOption(i, i, year, out);

        }
        out.println("</select></div><br><br>");

        out.println("<div id=\"awmobject2\">");        // allow menus to show over this box
        out.println("End Date:&nbsp;&nbsp;&nbsp;&nbsp;");
        out.println("Month:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"emonth\">");

            Common_Config.buildOption(1,  "JAN", month, out);
            Common_Config.buildOption(2,  "FEB", month, out);
            Common_Config.buildOption(3,  "MAR", month, out);
            Common_Config.buildOption(4,  "APR", month, out);
            Common_Config.buildOption(5,  "MAY", month, out);
            Common_Config.buildOption(6,  "JUN", month, out);
            Common_Config.buildOption(7,  "JUL", month, out);
            Common_Config.buildOption(8,  "AUG", month, out);
            Common_Config.buildOption(9,  "SEP", month, out);
            Common_Config.buildOption(10, "OCT", month, out);
            Common_Config.buildOption(11, "NOV", month, out);
            Common_Config.buildOption(12, "DEC", month, out);

        out.println("</select>");

        out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"eday\">");

        for (int i=1; i<=31; i++) {

            Common_Config.buildOption(i, i, day, out);

        }
        out.println("</select>");

        out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"eyear\">");

        for (int i=2003; i<=year; i++) {

           Common_Config.buildOption(i, i, year, out);

        }
        out.println("</select></div><br><br>");

        out.println("<div id=\"awmobject3\">");
        out.println("Gender Filtering:&nbsp;&nbsp;&nbsp;&nbsp;");
        out.println("<select name=\"gender\" size=\"1\">");
         out.println("<option value=\"0\">Do Not Filter");
         out.println("<option value=\"1\">Men Only");
         out.println("<option value=\"2\">Women Only");
        out.println("</select></div><br><br>");

    out.println("<p align=\"center\"><input type=\"submit\" value=\"Continue\"></p>");
    out.println("</td></tr></table>");

   out.println("");
   out.println("");
   out.println("");
   out.println("");

   out.println("</font></td></tr></form></table>");         // end of main page table

   out.println("<center><font size=\"2\"><br><b>Note:</b> Be patient, this may take a few minutes to run.</font>");

   out.println("<br><br><form action=Proshop_report_handicap method=get><input type=submit value=\"Back\" style=\"width: 75px; background:#8B8970\"></form></center>");

   out.println("</font>");

}   // end of doAllMems



/*
 *   Display a summary report for all members - rounds played vs scores posted
 */
private void doAllMems2(HttpServletRequest req, HttpSession session, Connection con, PrintWriter out) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String user = "";
    String fullname = "";
    String color1 = "#F5F5DC";
    String color2 = "#86B686";
    String last_color = "";
    String sdates = "";
    String edates = "";
    String temp = "";

    int sdate = 0;           // for custom date range
    int edate = 0;
    int mm  = 0;
    int dd = 0;
    int yy = 0;



    int [] rounds = new int[3];
  //  int days = 180;                                         // Number of days to go back (past 'x' days)
  //  int today = (int)Utilities.getDate(con,0);
  //  int last_date = (int)Utilities.getDate(con, -(days));

    boolean excel = false;

    if (req.getParameter("excel") != null) {

       excel = true;     // if excel request

       try {

          temp = req.getParameter("sdate");
          sdate = Integer.parseInt(temp);

          temp = req.getParameter("edate");
          edate = Integer.parseInt(temp);

       }
       catch (NumberFormatException e) {
       }

       yy = sdate / 10000;
       mm = (sdate - (yy * 10000)) / 100;
       dd = sdate - ((yy * 10000) + (mm * 100));

       sdates = mm + "/" + dd + "/" + yy;

       yy = edate / 10000;
       mm = (edate - (yy * 10000)) / 100;
       dd = edate - ((yy * 10000) + (mm * 100));

       edates = mm + "/" + dd + "/" + yy;

    } else {

       //     Get the dates selected

       try {

          temp = req.getParameter("smonth");
          mm = Integer.parseInt(temp);

          temp = req.getParameter("sday");
          dd = Integer.parseInt(temp);

          temp = req.getParameter("syear");
          yy = Integer.parseInt(temp);
       }
       catch (NumberFormatException e) {
       }

       sdate = (yy * 10000) + (mm * 100) + dd;      // create a date field of yyyymmdd

       sdates = mm + "/" + dd + "/" + yy;


       try {

          temp = req.getParameter("emonth");
          mm = Integer.parseInt(temp);

          temp = req.getParameter("eday");
          dd = Integer.parseInt(temp);

          temp = req.getParameter("eyear");
          yy = Integer.parseInt(temp);
       }
       catch (NumberFormatException e) {
       }

       edate = (yy * 10000) + (mm * 100) + dd;      // create a date field of yyyymmdd

       edates = mm + "/" + dd + "/" + yy;
    }

    String thisuser = (String)session.getAttribute("user");     // get username
    String club = (String)session.getAttribute("club");         // get club name

    String home = "Proshop_announce";

    if (!thisuser.startsWith("proshop")) home = "Member_announce"; 


    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

    if (edate < sdate) {

       out.println("<form method=\"get\" action=\"Proshop_report_handicap\">");
       out.println("<input type=\"hidden\" name=\"todo\" value=\"allMems\">");
       out.println("<p align=center><BR><font size=3>Input Error - the Start Date is later than the End Date.<BR><BR></font>");
       out.println("<input type=submit value=\"Back\" style=\"width: 75px; background:#8B8970\"></form></p></font>");
       return;
    }

    int gender_option = 0;

    if (req.getParameter("gender") != null) {

       try { gender_option = Integer.parseInt(req.getParameter("gender")); }
       catch (NumberFormatException ignore) { }

    }



    out.println("<p align=center><BR><font size=5>Rounds Played vs Scores Posted Summary Report</font>");
    out.println("<br><br><font size=4><i>Rounds Played vs Scores Posted for " +sdates+ " to " +edates+ "</i></font></p><font size=2>");

    if (excel == false) {     // if normal request - add buttons on top so user does not have to scroll

        out.println("<BR><table align=center><tr>" +
                    "<td><form method=\"post\" action=\"Proshop_report_handicap\" target=\"_blank\">" +
                    "<input type=\"hidden\" name=\"excel\" value=\"yes\">" +
                    "<input type=\"hidden\" name=\"todo\" value=\"allMems2\">" +
                    "<input type=\"hidden\" name=\"gender\" value=\"" + gender_option + "\">" +
                    "<input type=\"hidden\" name=\"sdate\" value=\"" +sdate+ "\">" +
                    "<input type=\"hidden\" name=\"edate\" value=\"" +edate+ "\">" +
                    "<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">" +
                    "</form></td><td>&nbsp;&nbsp;&nbsp;</td>");

        out.println("" +
          "<td><form method=\"get\" action=\"Proshop_report_handicap\">" +
          "<input type=\"hidden\" name=\"todo\" value=\"allMems\">" +
          "<input type=submit value=\"Back\" style=\"width: 75px; background:#8B8970\"></form></td>" +
          "<td>&nbsp; &nbsp; &nbsp;</td>" +
          "<td><form action=" +home+ "><input type=submit value=\"Home\" style=\"width: 75px; background:#8B8970\"></form></td>" +
          "</tr></table>");
    }

    out.println("<p align=center><b>Combined Rounds</b> = total number of 18-hole rounds played (# of 18-hole rounds + 1/2 of 9-hole rounds).");
    out.println("<BR>(This should approximately match the number of scores posted.)</p>");

    out.println("<p align=center>Click on the Member's name to get a list of rounds and postings for this date range.</p>");

    out.println("<table align=center>");
    out.println("<table align=center cellspacing=0 cellpadding=5 border=1 bgcolor=#F5F5DC>"); // width=600
    out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                    "<td align=center rowspan=2><font size=3><b>Member</b></font></td>" +
                    "<td align=center rowspan=2 nowrap><font size=3><b>Hndcp #</b></font></td>" +
                    "<td align=center rowspan=2><font size=3><b>Index</b></font></td>" +
                    "<td align=center colspan=3><font size=3><b>Rounds Played</b></font></td>" +
                    "<td align=center rowspan=2><font size=3><b>Scores<br>Posted</b></font></td>" +
                    "<td align=center rowspan=2><font size=3><b>Posted %</b></font></td>" +
                    "<td align=center rowspan=2><font size=3><b>Missed<br>Postings</b></font></td>" +
                    "<td align=center rowspan=2><font size=3><b>Missed %</b></font></td></tr>" +
                "<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                    "<td align=center nowrap><font size=3><b>9-Hole</b></font></td>" +
                    "<td align=center nowrap><font size=3><b>18-Hole</b></font></td>" +
                    "<td align=center><font size=3><b>Combined</b></font></td>" +
                "</tr>");

    //
    //  Get each active member in the roster and gather their rounds played and scores posted (only show those with rounds played)
    //
    try {

        double g_hancap = 0;
        String index = "";

        // find all members with old tee times without a posted score
        /*
        String sql = "" +
                "SELECT username, CONCAT(name_last, ', ', name_first) AS fullName, ghin, g_hancap " +
                "FROM member2b " +
                "WHERE inact=0 AND billable=1 AND ghin != '' " +
                ((gender_option == 0) ? "" : "AND gender = '" + ((gender_option == 1) ? "M" : "F") + "' ") +
                "ORDER BY name_last, name_first ";
         */
        /*
        String sql = "" +
                "SELECT username, CONCAT(name_last, ', ', name_first) AS fullName, ghin, g_hancap, get_918_teepast_by_player(member2b.username,?,?) as p918, " +
                "(" +
                     "SELECT COUNT(*) " +
                        "FROM score_postings sp WHERE sp.date BETWEEN ? AND ? AND sp.type <> 'A' AND sp.type <> 'AI' " +
                        "AND CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(member2b.ghin, '-', '') AS UNSIGNED) " +
                    ") AS posted_scores " +
                "FROM member2b " +
                "WHERE inact=0 AND billable=1 AND ghin != '' " +
                ((gender_option == 0) ? "" : "AND gender = '" + ((gender_option == 1) ? "M" : "F") + "' ") +
                "ORDER BY name_last, name_first ";
        */

        /*
         // Once hdcp is fully normalized, use this query
        String sql = "" +
                "SELECT username, CONCAT(name_last, ', ', name_first) AS fullName, ghin, g_hancap, get_918_teepast_by_player(member2b.username,?,?) as p918, " +
                "(" +
                     "SELECT COUNT(*) " +
                        "FROM score_postings sp WHERE sp.date BETWEEN ? AND ? AND sp.type <> 'A' AND sp.type <> 'AI' " +
                        "AND sp.hdcpNum = member2b.ghin " +
                    ") AS posted_scores " +
                "FROM member2b " +
                "WHERE inact=0 AND billable=1 AND ghin != '' " +
                ((gender_option == 0) ? "" : "AND gender = '" + ((gender_option == 1) ? "M" : "F") + "' ") +
                "ORDER BY name_last, name_first ";
        */

        String sql = ""
                + "SELECT username, CONCAT(name_last, ', ', name_first) AS fullName, ghin, g_hancap, get_918_teepast_by_player(member2b.username,?,?) as p918, "
                + "("
                    + "SELECT COUNT(*) "
                    + "FROM score_postings sp WHERE sp.date BETWEEN ? AND ? AND sp.type <> 'A' AND sp.type <> 'AI' "
                    + "AND sp.hdcpNum = CONVERT(CAST(REPLACE(member2b.ghin, '-', '') AS UNSIGNED), CHAR) " 
                    + (club.equals("governorsclub") ? "AND (tee_id != 0 OR type IN ('C','CI') OR sp.courseName LIKE '%Governors%' OR sp.courseName LIKE '%Primary Rotation%') " : "")
                + ") AS posted_scores "
                + "FROM member2b "
                + "WHERE inact=0 AND billable=1 AND ghin != '' "
                + ((gender_option == 0) ? "" : "AND gender = '" + ((gender_option == 1) ? "M" : "F") + "' ")
                + "ORDER BY name_last, name_first ";

        pstmt = con.prepareStatement(sql);
        pstmt.clearParameters();

        pstmt.setInt(1, sdate);
        pstmt.setInt(2, edate);
        pstmt.setInt(3, sdate);
        pstmt.setInt(4, edate);
   
        rs = pstmt.executeQuery();            // check each member

        while ( rs.next() ) {

           user = rs.getString("username");
           fullname = rs.getString("fullName");
           g_hancap = rs.getDouble("g_hancap");

           if (g_hancap >= 0) {
               index = "+" + g_hancap;
           } else {
               index = "" + (g_hancap * -1);
           }

           //
           //  Get the rounds played and scores posted date for this member
           //
           //rounds = lookupHistory(sdate, edate, user, con, out);   // returns:  rounds[0] = 9-hole rounds, rounds[1] = 18-hole rounds, rounds[2] = scores posted

           String[] p918 = rs.getString("p918").split(";");
           Double total_rounds = 0.0;
           Double missed_postings = 0.0;
           int rounds_9 = 0;
           int rounds_18 = 0;
           int posted_scores = 0;
           int posted_pct = 0;
           int missed_pct = 0;

            rounds_9 = Integer.parseInt(p918[0]);
            rounds_18 = Integer.parseInt(p918[1]);
            posted_scores = rs.getInt("posted_scores");
            
            total_rounds = ((double)rounds_18 + (double)rounds_9/2);
            posted_pct = (int)((posted_scores / total_rounds) * 100);    // Percent of rounds posted
            
            if (posted_pct > 100) {
                posted_pct = 100;
            } else if (posted_pct < 0) {
                posted_pct = 0;
            }
            
            missed_postings = total_rounds - (double)posted_scores;
            missed_pct = 100 - posted_pct;
            
            if (missed_postings < 0.0) {    // Zero the value out if it went negative
                missed_postings = 0.0;
            }
            

           if (rounds_9 > 0 || rounds_18 > 0) {        // if any rounds played

              if (last_color.equals(color1)) {

                 last_color = color2;       // alternate background colors

              } else {

                 last_color = color1;
              }

               out.println("<tr bgcolor=" + last_color + " align=\"center\">"
                       + "<td align=\"left\"><font size=2>"
                       + "<a href=\"javascript:void(0)\" onClick=\"window.open ('Proshop_report_handicap?todo=viewdates&username=" + user + "&sdate=" + sdate + "&edate=" + edate + "', 'newwindow', 'Height=600, width=600, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no')\">"
                       + fullname + "</a></font></td>"
                       + "<td align=\"center\"><font size=2>" + formatHdcpNum(rs.getString("ghin"), club.equals("mpccpb") ? 2 : 1) + "</font></td>" + // Common_handicaps.formatHdcpNum()
                       "<td align=\"center\"><font size=2>" + index + "</font></td>"
                       + "<td align=\"center\"><font size=2>" + rounds_9 + "</font></td>"
                       + "<td align=\"center\"><font size=2>" + rounds_18 + "</font></td>"
                       + "<td align=\"center\"><font size=2>" + (total_rounds % 1 == 0 ? total_rounds.intValue() : total_rounds.toString()) + "</font></td>"
                       + "<td align=\"center\"><font size=2>" + posted_scores + "</font></td>"
                       + "<td align=\"center\"><font size=2>" + posted_pct + "%</font></td>"
                       + "<td align=\"center\"><font size=2>" + (missed_postings % 1 == 0 ? missed_postings.intValue() : missed_postings.toString()) + "</font></td>" 
                       + "<td align=\"center\"><font size=2>" + missed_pct + "%</font></td></tr>");
           }

        }   // end of WHILE members

        pstmt.close();

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Proshop_report_handicap.doAllMems - Error gathering members.", exc.getMessage(), out, false);

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    out.println("</table>");

    out.println("</font>");


    //  End - display the return/exit buttons

    if (excel == false) {     // if normal request

        out.println("<BR><BR><table align=center><tr>" +
                    "<td><form method=\"post\" action=\"Proshop_report_handicap\" target=\"_blank\">" +
                    "<input type=\"hidden\" name=\"excel\" value=\"yes\">" +
                    "<input type=\"hidden\" name=\"todo\" value=\"allMems2\">" +
                    "<input type=\"hidden\" name=\"gender\" value=\"" + gender_option + "\">" +
                    "<input type=\"hidden\" name=\"sdate\" value=\"" +sdate+ "\">" +
                    "<input type=\"hidden\" name=\"edate\" value=\"" +edate+ "\">" +
                    "<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">" +
                    "</form></td><td>&nbsp;&nbsp;&nbsp;</td>");

        out.println("" +
          "<td><form method=\"get\" action=\"Proshop_report_handicap\">" +
          "<input type=\"hidden\" name=\"todo\" value=\"allMems\">" +
          "<input type=submit value=\"Back\" style=\"width: 75px; background:#8B8970\"></form></td>" +
          "<td>&nbsp; &nbsp; &nbsp;</td>" +
          "<td><form action=" +home+ "><input type=submit value=\"Home\" style=\"width: 75px; background:#8B8970\"></form></td>" +
          "</tr></table>");
    }

    // done - return to doGet

}   // end of doAllMems2


//
//   Members and Pros arrive here via doGet (todo=view) after selecting the Tee Sheet Score Posting Report from their Handicap menu.
//   They will select a date and course and then be routed to viewTeeSheetPostings for the report.
//
private void promptMemDate(HttpServletRequest req, String club, HttpSession session, Connection con, PrintWriter out) {

    Statement stmt = null;
    ResultSet rs = null;

    String user = (String)session.getAttribute("user");   // get username
    
    int multi = Utilities.getMulti(con);
    
   //
   //  Array to hold the course names
   //
   int cMax = 0;
   int index = 0;
   
   String courseName = "";
   String courseText = "";
   ArrayList<String> course = new ArrayList<String>();

   
   boolean isProshopUser = user.startsWith("proshop");
    
   String home = (isProshopUser) ? "Proshop_announce" : "Member_announce";

   //
   //  Get the course names if more than one
   //
   try {

      if (multi != 0) {           // if multiple courses supported for this club

         course = Utilities.getCourseNames(con);     // get all the course names
      }

   }
   catch (Exception exc) {
   }


    // include files for dynamic calendars
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/cal-styles.css\">");
    out.println("<script language=\"javascript\" src=\"/" +rev+ "/cal-scripts-old.js\"></script>");

      Calendar cal_date = new GregorianCalendar();         // Calendar.getInstance();
      cal_date.add(Calendar.DATE, -1);                     // yesterday is the latest old sheet
      int cal_year = cal_date.get(Calendar.YEAR);
      int cal_month = cal_date.get(Calendar.MONTH) + 1;
      int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);
      
      cal_date.add(Calendar.DATE, -364);                 // allow user to back up 1 year

      int old_yy = cal_date.get(Calendar.YEAR);
      int old_mm = cal_date.get(Calendar.MONTH) + 1;
      int old_dd = cal_date.get(Calendar.DAY_OF_MONTH);
      
      String calDate = cal_month + "/" + cal_day + "/" + cal_year;            // create calDate 
   
    out.println("<div class=\"main_instructions\">");
    if (isProshopUser) out.println("<center>");
    out.println("<br><p align=center><H2>Handicap Scores Posted By Day Report</H2></p>");
    
    if (multi > 0) {       // if multiple courses
       
       courseText = "course and ";
    }
    
    out.println("<br><p>Select the " +courseText+ "date desired to see who played and who posted that day.</p><br>");

    out.println("</div>");
    out.println("<div class=\"sub_instructions\">");
    
    // this is the form that gets submitted when the user selects a day from the calendar
    out.println("<form action=\"Proshop_report_handicap\" method=\"get\" name=\"frmLoadDay\">");
    out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");  // value set by script - will override current date if changed
    out.println("<input type=\"hidden\" name=\"old_mm\" value=\"" +old_mm+ "\">");   // oldest month (for js)
    out.println("<input type=\"hidden\" name=\"old_dd\" value=\"" +old_dd+ "\">");   // oldest day
    out.println("<input type=\"hidden\" name=\"old_yy\" value=\"" +old_yy+ "\">");   // oldest year
    out.println("<input type=\"hidden\" name=\"todo\" value=\"view2\">");
          
    if (multi > 0) {

       if (isProshopUser == false) out.println("<div class=\"select\">");

       index = 0;
       cMax = course.size();     // number of courses

       out.println("<p align=center><b>Course:</b>&nbsp;&nbsp;");
       out.println("<select size=\"1\" name=\"course\">");
       
       if (cMax > 1) {
          
           out.println("<option value=\"-ALL-\">-ALL-</option>");
       }

       courseName = course.get(0);            // get first course name from array

       while (!courseName.equals( "" ) && index < cMax) {

           out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
           
           index++;
           if (index < cMax) {
              courseName = course.get(index);      // get course name from array
           }
        }
        out.println("</select></p>");
        if (isProshopUser == false) out.println("</div>");

    } else {
       out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
    }
      
    out.println("<input type=\"hidden\" name=\"fromMenu\" value=\"yes\">");
    
    //  add calendar for date selection (date =)  - copied from Proshop_oldsheets
    
      out.println("<table align=center border=0 height=165>\n<tr valign=top>\n<td>");   

      out.println(" <div id=calendar0 style=\"width: 180px\"></div>");

      out.println("</td></tr>\n</table>");

      out.println("<script type=\"text/javascript\">");
      out.println("var iEndingDay = " + cal_day + ";");
      out.println("var iEndingYear = " + cal_year + ";");
      out.println("var iEndingMonth = " + cal_month + ";");
      out.println("var iStartingMonth = " + old_mm + ";");
      out.println("var iStartingDay = " + old_dd + ";");
      out.println("var iStartingYear = " + old_yy + ";");
      out.println("doCalendar('" + cal_month + "', '" + cal_year + "');");
      out.println("</script>");
          
    //out.println("<input type=submit value=\" Get Report \">");
    out.println("</form>");
    out.println("<br><br>");
    out.println("<p align=center><input type=button value=\" Home \" onclick=\"location.href=" +home+ "\"></p>");
    out.println("</div>");

}    // end of promptMemDate



 //**************************************************
 // Common Method for Displaying Date/Time of Report
 //**************************************************
 //
 private String buildDisplayDateTime() {

    GregorianCalendar cal = new GregorianCalendar();
    DateFormat df_full = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
    return "<i>This report was generated on " + df_full.format(cal.getTime()) + "</i>";

 }




 //**************************************************
 // Check member's subtype
 //**************************************************
 //
 private boolean checkMember(String user, String club, HttpServletRequest req, PrintWriter out, Connection con) {

    boolean bad = false;
    
    String subtype = Utilities.getSessionString(req, "msubtype", "");    // get member sub-type from the session
      
    if (subtype.equals("")) {           // if not found
       
       subtype = Utilities.getSubtypeFromUsername(user, con);  // get member's subtype from member2b
    }
    
    if (!subtype.equals("Handicap Chair") && !club.equals("governorsclub")) {      // if not the Handicap Chair
        
        bad = true;
        
        Common_skin.outputError(club, "", 0, "Access Error", "Invalid access - not allowed.", "Member_announce", out, req);
    }
    
    return(bad);
 }



 private String formatHdcpNum(String hdcpNum, int style) {

     String tmp = "";

     if (hdcpNum != null) {

         // regardless of which style, lets default to removing any dashes
         tmp = hdcpNum.replace("-", "");

         if (style == 1 || style == 2) {

             // first ensure ghin # is 7 chars long - add leading zeros to fix
             while (tmp.length() < 7) {

                 tmp = "0" + tmp;
             }

             // add the dash back in if style is 1
             if (style == 1) tmp = tmp.substring(0,3) + "-" + tmp.substring(3);

         } // add additional formats as needed below in else if clauses

     }

     return tmp;

 }


 public static int[] lookupHistory(int sdate, int edate, String username, Connection con, PrintWriter out) {

    int [] result = new int [3];

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String club = Utilities.getClubDbName(con);
    
    try {
        /*
        String sql = "" +
                "SELECT " +
                    "SUM(IF(" +
                        "(username1 = ? && p91 = 1) || " +
                        "(username2 = ? && p92 = 1) || " +
                        "(username3 = ? && p93 = 1) || " +
                        "(username4 = ? && p94 = 1) || " +
                        "(username5 = ? && p95 = 1), 1, 0) ) " +
                    "AS played9, " +
                    "SUM(IF(" +
                        "(username1 = ? && p91 = 0) || " +
                        "(username2 = ? && p92 = 0) || " +
                        "(username3 = ? && p93 = 0) || " +
                        "(username4 = ? && p94 = 0) || " +
                        "(username5 = ? && p95 = 0), 1, 0) ) " +
                    "AS player18,  " +
                    "(" +
                        "SELECT COUNT(*) AS posted_scores " +
                        "FROM member2b m " +
                        "LEFT OUTER JOIN score_postings sp ON CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED) " +
                        "WHERE m.username = ? AND sp.date BETWEEN ? AND ? AND sp.type <> 'A' AND sp.type <> 'AI' " +
                      //"WHERE m.username = ? AND sp.date >= ? AND sp.date <= ? AND sp.type <> 'A' AND sp.type <> 'AI' " +
                      //"ORDER BY date DESC" +
                    ") AS posted_scores " +
                "FROM teepast2 tp " +
                "WHERE " +
                    "tp.date BETWEEN ? AND ? AND " +
                  //"tp.date >= ? AND tp.date <= ? AND " +
                    "( " +
                        "(username1 = ? && show1 = 1) || " +
                        "(username2 = ? && show2 = 1) || " +
                        "(username3 = ? && show3 = 1) || " +
                        "(username4 = ? && show4 = 1) || " +
                        "(username5 = ? && show5 = 1) " +
                    ") " +
                "ORDER BY date DESC;";
         *
         */

        String sql = "SELECT get_918_teepast_by_player(?,?,?) as p918, "
                + "("
                + "SELECT COUNT(*) AS posted_scores "
                + "FROM member2b m "
                + "LEFT OUTER JOIN score_postings sp ON sp.hdcpNum = CONVERT(CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED), CHAR) "
                + "WHERE m.username = ? AND sp.date BETWEEN ? AND ? AND sp.type <> 'A' AND sp.type <> 'AI' "
                + (club.equals("governorsclub") ? "AND (tee_id != 0 OR type IN ('C','CI') OR sp.courseName LIKE '%Governors%' OR sp.courseName LIKE '%Primary Rotation%') " : "")
                + //"ORDER BY date DESC" +
                ") AS posted_scores ";
        // out.println("<!-- " + sql + " -->");
        // out.println("<!-- USING: user=" + username + ",sdate=" + sdate + ",edate=" + edate + " -->");

        pstmt = con.prepareStatement(sql);
       
        pstmt.clearParameters();
        /*
        pstmt.setString(1, username);
        pstmt.setString(2, username);
        pstmt.setString(3, username);
        pstmt.setString(4, username);
        pstmt.setString(5, username);
        pstmt.setString(6, username);
        pstmt.setString(7, username);
        pstmt.setString(8, username);
        pstmt.setString(9, username);
        pstmt.setString(10, username);
        pstmt.setString(11, username);
        pstmt.setInt(12, sdate);
        pstmt.setInt(13, edate);
        pstmt.setInt(14, sdate);
        pstmt.setInt(15, edate);
        pstmt.setString(16, username);
        pstmt.setString(17, username);
        pstmt.setString(18, username);
        pstmt.setString(19, username);
        pstmt.setString(20, username);
         *
         */
        pstmt.setString(1, username);
        pstmt.setInt(2, sdate);
        pstmt.setInt(3, edate);
        pstmt.setString(4, username);
        pstmt.setInt(5, sdate);
        pstmt.setInt(6, edate);
        rs = pstmt.executeQuery();
       
        if ( rs.next() ) {

            /*
            result[0] = rs.getInt(1);
            result[1] = rs.getInt(2);
            result[2] = rs.getInt(3);
             *
             */
            String[] p918 = rs.getString("p918").split(";");

                result[0] = Integer.parseInt( p918[0] );
                result[1] = Integer.parseInt( p918[1] );
                result[2] = rs.getInt("posted_scores");

        }

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading rounds/posted score counts for " + username, exc.getMessage(), out, false);

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return result;

 }

 private boolean get9hole_flag(String club) {

    boolean skip_9hole = false;

    if (club.equals("castlepines")) skip_9hole = true;

    return skip_9hole;

 }

} // end Proshop_report_handicap




/*




SELECT tp.time, tp.date, m.ghin, CONCAT(m.name_last, ', ', m.name_first) AS fullName
FROM teepast2 tp, member2b m
LEFT OUTER JOIN score_postings sp ON sp.hdcpNum = REPLACE(m.ghin, '-', '') AND sp.date > 20090500
LEFT OUTER JOIN tees t ON t.tee_id = sp.tee_id
WHERE
    tp.date > 20090500 AND
    tp.courseName = 'Members Course' AND
    m.ghin <> '' AND
    (
        (tp.show1 = 1 AND tp.p91 = 0 AND tp.username1 = m.username) OR
        (tp.show2 = 1 AND tp.p92 = 0 AND tp.username2 = m.username) OR
        (tp.show3 = 1 AND tp.p93 = 0 AND tp.username3 = m.username) OR
        (tp.show4 = 1 AND tp.p94 = 0 AND tp.username4 = m.username) OR
        (tp.show5 = 1 AND tp.p95 = 0 AND tp.username5 = m.username)
    )
ORDER BY fullName, date, time;




 SELECT tp.time, tp.p91, tp.p92, tp.p93, tp.p94, tp.p95, sp.score, sp.type, t.tee_name,
    CONCAT(m.name_last, ', ', m.name_first) AS fullName,
    IF(m.username=tp.username1, 1, 0) AS pos1,
    IF(m.username=tp.username2, 1, 0) AS pos2,
    IF(m.username=tp.username3, 1, 0) AS pos3,
    IF(m.username=tp.username4, 1, 0) AS pos4,
    IF(m.username=tp.username5, 1, 0) AS pos5
FROM teepast2 tp, member2b m
LEFT OUTER JOIN score_postings sp ON sp.hdcpNum = REPLACE(m.ghin, '-', '') AND sp.date = 20090315
LEFT OUTER JOIN tees t ON t.tee_id = sp.tee_id
WHERE
    tp.date = 20090315 AND
    tp.courseName = "Lake Valley" AND
    m.ghin <> '' AND
    (
        (tp.show1 = 1 AND tp.username1 = m.username) OR
        (tp.show2 = 1 AND tp.username2 = m.username) OR
        (tp.show3 = 1 AND tp.username3 = m.username) OR
        (tp.show4 = 1 AND tp.username4 = m.username) OR
        (tp.show5 = 1 AND tp.username5 = m.username)
    )
ORDER BY fullName, time

 */
