/***************************************************************************************
 *   Member_handicaps: This servlet will allow the members to post scores and view their hdcp
 * 			   
 *
 *
 *   Called by:     called by self and start w/ direct call main menu option
 *
 *
 *   Created:       02/05/2007 by Paul
 *
 *
 *   Last Updated:
 *
 *        7/15/10   Merion GC (merion) - Allow members to post tournament scores
 *        6/30/10   Allow Wellesley CC members to post tournament scores (Case #1856)
 *        4/28/10   Updated viewSavedScores (Peer Review Report) to include the changes done on the pro side.
 *        9/06/09   Changed SQL statement in viewSavedScores to return 40 scores and no 'A' or 'AI' types
 *        2/18/09   Allow Johns Island members to post tournament scores (Case #1615)
 *       12/10/08   Added order by clause to tees table query
 *        9/02/08   Javascript compatability updates
 *        9/02/08   Add link for International CC to the Virginia St. Golf Assoc. web site (Case 1507)
 *        8/27/08   Updated javascript for better cross-browser support
 *        5/13/08   Use round() not cast to display course handicaps
 *        5/05/08   Change call to Common_ghin.getHdcpNum to Common_handicaps
 *        4/08/08   Update Legend & suppress minus sign/add plus sign to handicap values
 *        1/03/08   Removed hdcp season check
 *       10/20/07   Added peer review reports
 *                  
 *                  
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Member_handicaps extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    
 //*****************************************************
 // Process the get calls and display the proper forms
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    
    //
    //  Prevent caching so sessions are not mangled
    //
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);                      // get DB connection

    if (con == null) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact your club manager.");
        out.println("<BR><BR>");
        out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }

    String club = (String)session.getAttribute("club");               // get club name
    String user = (String)session.getAttribute("user");               // get user name
    String caller = (String)session.getAttribute("caller");
    
    String clubNum = "";
    String clubAssocNum = "";
    String hdcpSystem = "";
    int allowMemPost = 0;
    int in_season = 0;
    
    if (club.equals( "" )) {

        invalidUser(out);            // Error - reject
        return;
    }
    
    // DONE VALIDATING THIS REQUEST - WE'RE READY TO GO
    
    
    // GET REQUEST VARIABLES WE WOULD NEED TO ACT ON
    String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");
    String hdcp = (req.getParameter("hdcp") == null) ? "" : req.getParameter("hdcp");


    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
    out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

    SystemUtils.getMemberSubMenu(req, out, caller);
    
    // MAKE SURE THIS CLUB HAS A HDCP SYSTEM ENABLED AND MEMBER'S ARE ABLE TO ACCESS
    
    
    // GET CLUB INFO
    try {

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("" +
                "SELECT " +
                    "hdcpSystem, allowMemPost, " +
                    "IF(hdcpStartDate <= now() && hdcpEndDate >= now(),1,0) AS in_season " +
                "FROM club5;");
        if (rs.next()) {
            hdcpSystem = rs.getString("hdcpSystem");
            allowMemPost = rs.getInt("allowMemPost");
            in_season = rs.getInt("in_season");
        }
    
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
        return;
    }
    
    
    // GET MEMBERS CLUB/ASSOC NUMBERS
    try {

        PreparedStatement pstmt = con.prepareStatement("" +
                "SELECT cn.club_num, ca.assoc_num " +
                "FROM member2b m, hdcp_club_num cn, hdcp_assoc_num ca " +
                "WHERE " +
                    "m.username = ? AND " +
                    "m.hdcp_club_num_id = cn.hdcp_club_num_id AND " +
                    "m.hdcp_assoc_num_id = ca.hdcp_assoc_num_id;");
        pstmt.clearParameters();
        pstmt.setString(1, user);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            clubNum = rs.getString("club_num");
            clubAssocNum = rs.getString("assoc_num");
        }
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
        return;
    }
    
    
    // MAKE SURE CLUB IS SETUP TO USE HDCP FEATURE
    if (hdcpSystem.equals("") || hdcpSystem.equalsIgnoreCase("other")) {
        
        if (club.equals("internationalcc")) {
            
            out.println("<br><br><p align=center><a href=\"http://www.golfnetonline.net/clubentry4.asp?CourseNum=10980&StateId=3\" target=\"_VSGA\">Click here to access the Virginia State Golf Association web site.</a></p>");
            return;
            
        } else {
        
            out.println("<br><br><p align=center><b><i>Your club does not have a handicap system that allows online access.</i></b></p>");
            return;
        }
    }
    if (clubNum.equals("") || clubAssocNum.equals("")) {
        
        out.println("<br><br><p align=center><b><i>Your club has not configured the online access to your handicap system.</i></b></p>");
        return;
    }
    /*
    if (in_season == 0) {
        
        out.println("<br><br><p align=center><b><i>Your club's handicap system is currently out of season and is unavailable at this time.</i></b></p>");
        return;
    }
    */
    
    // UPDATE THIS USERS HDCP # IF IT'S HERE
    if (!hdcp.equals("")) {
        if (!updateHdcpNum(user, hdcp, con, out)) return; 
    }
    
    
    // MAKE SURE USER HAS A HDCP # IN THEIR MEMBER DATA
    if (!checkHdcpNum(user, todo, con, out)) return;
    
    
    // MAKE SURE CLUB POLICY ALLOWS MEMBERS TO POST SCORES
    if (todo.equals("post") && allowMemPost == 0) {
        
        out.println("<br><br><p align=center><b><i>Your club policy does not to allow members to post scores.</i></b></p>");
        return;
    }
    
    
    // HANDLE POST SCORE REQUEST
    if (todo.equals("post")) getScoreToPost(req, club, con, out);
    
    
    // HANDLE VIEW HDCP/SCORES REQUEST
    if (todo.equals("view")) getViewScores(req, con, out);
    
    out.println("</body></html>");
    out.close();
    
 } // end of doGet routine
 
 
 //*****************************************************
 // Process the posted forms and call applicable methods
 //*****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    
    //
    //  Prevent caching so sessions are not mangled
    //
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    
    // set response content type
    try {
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
        } else {
            resp.setContentType("text/html");
        }
    }
    catch (Exception exc) {
    }
    
    HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);                      // get DB connection

    if (con == null) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact your club manager.");
        out.println("<BR><BR>");
        out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }

    String club = (String)session.getAttribute("club");               // get club name
    String user = (String)session.getAttribute("user");               // get user name
    String caller = (String)session.getAttribute("caller");
    
    if (club.equals( "" )) {

        invalidUser(out);            // Error - reject
        return;
    }
    
    // get members club/assoc info
    String clubNum = "";
    String clubAssocNum = "";
    
    try {

        PreparedStatement pstmt = con.prepareStatement("" +
                "SELECT cn.club_num, ca.assoc_num " +
                "FROM member2b m, hdcp_club_num cn, hdcp_assoc_num ca " +
                "WHERE " +
                    "m.username = ? AND " +
                    "m.hdcp_club_num_id = cn.hdcp_club_num_id AND " +
                    "m.hdcp_assoc_num_id = ca.hdcp_assoc_num_id;");
        pstmt.clearParameters();
        pstmt.setString(1, user);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            clubNum = rs.getString("club_num");
            clubAssocNum = rs.getString("assoc_num");
        }
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
        return;
    }
    
    // DONE VALIDATING THIS REQUEST - WE'RE READY TO GO
    
    
    // GET REQUEST VARIABLES WE WOULD NEED TO ACT ON
    String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");
    String username = (req.getParameter("user") == null) ? "" : req.getParameter("user");
    
    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
    //out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
    //out.println("<script language=\"javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

    SystemUtils.getMemberSubMenu(req, out, caller);
    
    if (todo.equals("post")) doPostScore(user, club, clubNum, clubAssocNum, req, con, out);
    if (todo.equals("view")) doViewScores(user, club, clubNum, clubAssocNum, req, con, out);    
    if (todo.equals("peer")) {
        if (username.equals("")) {
            getViewPeerScores(req, club, con, out);
        } else {
            viewSavedScores(req, username, con, out);
        }
    }
    
    out.println("</body></html>");
    out.close();
    
 } // end of doPost routine
 
 
 // *********************************************************
 // Check for hncp # for user, if not found display form
 // to allow user to add it to their member data
 // Returns true if found, false if not found
 // *********************************************************
 
 private boolean checkHdcpNum(String user, String todo, Connection con, PrintWriter out) {

    ResultSet rs = null;
    PreparedStatement pstmt = null;
    String hdcp_num = "";
    
    try {

        pstmt = con.prepareStatement("SELECT ghin FROM member2b WHERE username = ?");
        pstmt.clearParameters();
        pstmt.setString(1, user);
        rs = pstmt.executeQuery();
        if (rs.next()) hdcp_num = rs.getString(1);
    
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading up handicap number.", exc.getMessage(), out, false);
        return false;
    }
    
    if (hdcp_num.equals("")) {
    
        out.println("<br><p align=center><font size=4>Please Provide a Handicap Number</font></p>");
        out.println("<p align=center>If you are unaware of your assigned handicap number, please contact your golf professional for assistance.</p>");
        
        out.println("<center>");
        out.println("<form action=/" +rev+ "/servlet/Member_handicaps method=GET>");
        out.println("<input type=hidden name=todo value=\"" + todo + "\">");
        out.println("Handicap Number: <input type=text name=hdcp value=\"\" size=16 maxlength=16>");
        out.println("<br><br><input type=submit value=\"  Save  \">");
        out.println("</form>");
        out.println("</center>");
        return false;
    }
    
    return true;
 }
 
 
 // *********************************************************
 // Save hdcp number in members data
 // Returns true if a record was updated, false if not
 // *********************************************************
 
 private boolean updateHdcpNum(String user, String hdcp, Connection con, PrintWriter out) {

    ResultSet rs = null;
    PreparedStatement pstmt = null;
    String hdcp_num = "";
    int x = 0;
    
    try {

        pstmt = con.prepareStatement("UPDATE member2b SET ghin = ? WHERE username = ?");
        pstmt.clearParameters();
        pstmt.setString(1, hdcp);
        pstmt.setString(2, user);
        x = pstmt.executeUpdate();
    
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error saving handicap number.", exc.getMessage(), out, false);
        return false;
    }
    
    if (x == 0) {
    
        out.println("<p>We were unable to save your handicap number.  Please try again.  If problem persists, please contact your golf shop.</p>");
        return false;
    }
    
    return true;
 }
 
 
 // *********************************************************
 // Handles from posted from getScoreToPost method and
 // calls Common_ghin.postScore and displays result
 // *********************************************************
 
 private void doPostScore(String user, String club, String clubNum, String clubAssocNum, HttpServletRequest req, Connection con, PrintWriter out) {
 
    long date = 0;
    
    int course_id = 0;
    int tee_id = 0;
    int holes = 0;
    int score = 0;
    
    String sdate = (req.getParameter("sdate") == null) ? "" : req.getParameter("sdate");
    String type = (req.getParameter("type") == null) ? "" : req.getParameter("type");
    String stid = (req.getParameter("tee_id") == null) ? "" : req.getParameter("tee_id");
    String sholes = (req.getParameter("holes") == null) ? "" : req.getParameter("holes");
    String sscore = (req.getParameter("score") == null) ? "" : req.getParameter("score");
       
    try {
        
        tee_id = Integer.parseInt(stid);
        holes = Integer.parseInt(sholes);
        date = Integer.parseInt(sdate);
        score = Integer.parseInt(sscore);
        
    } catch (NumberFormatException exc) { }
    
    
    // FIRST LETS CHECK TO MAKE SURE THIS MEMBER HAS A ROUND IN TEEPAST2 FOR THIS DATE
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    boolean found = false;
    
    try {

        pstmt = con.prepareStatement("" +
                "SELECT * " +
                "FROM teepast2 " +
                "WHERE " +
                    "date = ? AND ( " +
                        "(username1 = ? AND show1 = 1) OR " +
                        "(username2 = ? AND show2 = 1) OR " +
                        "(username3 = ? AND show3 = 1) OR " +
                        "(username4 = ? AND show4 = 1) OR " +
                        "(username5 = ? AND show5 = 1) " +
                    ")");
        pstmt.clearParameters();
        pstmt.setLong(1, date);
        pstmt.setString(2, user);
        pstmt.setString(3, user);
        pstmt.setString(4, user);
        pstmt.setString(5, user);
        pstmt.setString(6, user);
        rs = pstmt.executeQuery();
        if (rs.next()) found = true;
    
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error looking up member round.", exc.getMessage(), out, false);
        return;
    }
/*
    if (found == false) {
        
        out.println("<p align=center><b><i>A round was not found in the system for you on the selected date.</i><br><br>Please select a date for which you played a round of golf.</b></p>");
        out.println("<p align=center><form><input type=button onclick=\"window.history.go(-1)\" value=\" Go Back \"></form></p>");
        return;
    }
*/
    
    String msg[] = new String[2];
    
    msg = Common_ghin.postScore(user, club, date, tee_id, score, type, holes, clubNum, clubAssocNum, out, con);
    
    //msg[0] = "1";
    //msg[1] = "SCORE ADDED";
    
    if (msg[0] == null || !msg[0].equals("1")) {
        
        out.println("<p align=center><b>Your score was not posted.&nbsp; Please try again later.</b><br>Message: " + msg[1] + "</p>");
    } else {
        
        out.println("<p align=center><b>Your score was successfully posted.</b><!--<br>Message: " + msg[1] + "--></p>");
    }
        
 }
 
 
 // *********************************************************
 // Display form and gather information to submit score to
 // handicap system.  
 // *********************************************************
 
 private void getScoreToPost(HttpServletRequest req, String club, Connection con, PrintWriter out) {

    ResultSet rs = null;
    int multi = 0;
    
    String courseName = "";
    int course_id = 0;
    
    // see if date parts are being passed in, if so default calendar to said mm & yy
    String smm = (req.getParameter("mm") == null) ? "0" : req.getParameter("mm");
    String syy = (req.getParameter("yy") == null) ? "0" : req.getParameter("yy");
    String sdd = (req.getParameter("dd") == null) ? "0" : req.getParameter("dd");
    String scid = (req.getParameter("course_id") == null) ? "0" : req.getParameter("course_id");
    String cal_box_0 = (req.getParameter("cal_box_0") == null) ? "" : req.getParameter("cal_box_0");
    String sdate = (req.getParameter("sdate") == null) ? "" : req.getParameter("sdate");
    
    int mm = 0;
    int yy = 0;
    int dd = 0;
    
    try {

        course_id = Integer.parseInt(scid);
        mm = Integer.parseInt(smm);
        yy = Integer.parseInt(syy);
        dd = Integer.parseInt(sdd);
    }
    catch (NumberFormatException e) { }
    
    
    out.println("<p align=center><font size=5>Post a Handicap Score</font></p>");
    
    out.println("<table align=center border=0 height=165>\n<tr valign=top>\n<td>");   // was 190 !!!

    out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

    out.println("</td>\n<tr>\n</table>");
    
    out.println("<form action=/" +rev+ "/servlet/Member_handicaps method=POST name=frmPostScore id=frmPostScore>");
    out.println("<input type=hidden name=todo value=\"post\">");
    out.println("<input type=hidden name=sdate value=\"" + sdate + "\">");
    out.println("<input type=hidden name=course_id value=\"\">");
        
    out.println("<table align=center>");
    
    out.println("<tr><td align=right><b>Date Played:&nbsp;</b></td><td><input type=text value=\"" + cal_box_0 + "\" name=cal_box_0 id=cal_box_0 size=10 onfocus=\"this.blur()\"></td></tr>"); // ((dd == 0) ? "" : yy + "-" + mm + "-" + dd)
    
    try {
        
        Statement stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");

        if (rs.next()) multi = rs.getInt(1);
        
    } 
    catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error getting club options.", exc.getMessage(), out, false);
        return;
    }
    
    // DISPLAY COURSE SELECTION BOX IF NECESSARY
    if (multi == 1) {
        
        out.println("<tr><td align=right><b>Course Played:&nbsp;</b></td>");
        out.println("<td><select name=course size=1 onchange=\"selectTees(this.options[this.selectedIndex].value)\">");
        try {

            Statement stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT clubparm_id, courseName FROM clubparm2 WHERE first_hr != 0");

            int i = 0;
            while (rs.next()) {
                out.print("<option value=\"" + rs.getInt(1) + "\"");
                if (course_id == rs.getInt(1)) out.print(" selected");
                out.println(">" + rs.getString(2) + "</option>");
                if (i == 0 && course_id == 0) course_id = rs.getInt(1); // applies default to make sure course & tee match
                i++;
            }
            
        }
        catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error getting courses.", exc.getMessage(), out, false);
            return;
        }

        out.println("</select></td>");

        out.println("</tr>");
        
    } else {

        // get the course from clubparm2
        try {

            Statement stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT clubparm_id, courseName FROM clubparm2 WHERE first_hr != 0");

            if (rs.next()) course_id = rs.getInt(1);

        }
        catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error getting courses.", exc.getMessage(), out, false);
            return;
        }

    }

    out.println("<!-- course_id=" + course_id + " -->");

    // DISPLAY TEES FOR SELECTION
    out.println("<tr><td align=right><b>Tees Played:&nbsp;</b></td>");
    out.println("<td><select name=tee_id size=1>");
    
    try {

        PreparedStatement pstmt = con.prepareStatement("SELECT tee_id, tee_name FROM tees WHERE course_id = ? ORDER BY tee_id");
        pstmt.clearParameters();
        pstmt.setInt(1, course_id);
        rs = pstmt.executeQuery();

        while (rs.next()) 
            out.println("<option value=\"" + rs.getInt(1) + "\">" + rs.getString(2) + "</option>");

    }
    catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error getting tees.", exc.getMessage(), out, false);
        return;
    }
    
    out.println("</select></td>");
    out.println("</tr><tr>");
    
    // HOLES PLAYED
    out.println("<td align=right><b>Round Length:&nbsp;</b></td>");
    out.println("<td><select name=holes size=1>");
    out.println("<option value=1>18 Holes");
    out.println("<option value=2>Front 9 Holes");
    out.println("<option value=3>Back 9 Holes");
    out.println("</select></td>");
    
    out.println("</tr><tr>");
    
    // PLAY TYPE (members can only post Home scores for now)
    if (club.equals("johnsisland") || club.equals("wellesley") || club.equals("merion")) { // allow johnsisland and wellesley members to post tournament scores (Case #1615)

        out.println("<td align=right><b>Play Type:&nbsp;</b></td>");
        out.println("<td><select name=type size=1>");
        out.println("<option value='H'>Home");
        //out.println("<option value='A'>Away");
        out.println("<option value='T'>Tournament");
        //out.println("<option value='I'>Internet");
        out.println("</select></td>");

        out.println("</tr><tr>");

    } else {

        out.println("<input type=hidden name=type value=H>");

    }
    
    
    // SCORE TO POST
    out.println("<td align=right><b>Score:&nbsp;</b></td>");
    out.println("<td><input type=text name=score size=5 maxlength=3></td>");
    
    out.println("</tr><tr>");
    
    out.println("<td colspan=2 align=center><br><input type=button value=\" Post Score \" onclick=\"submitForm()\" name=btnSubmit></td>");
    
    out.println("</tr></table>");
    
    out.println("</form>");
    
    Calendar cal_date = new GregorianCalendar();
    int cal_year = cal_date.get(Calendar.YEAR);
    int cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
    int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);
    
    out.println("<script type=\"text/javascript\">");

    out.println("var g_cal_bg_color = '#F5F5DC';");
    out.println("var g_cal_header_color = '#8B8970';");
    out.println("var g_cal_border_color = '#8B8970';");

    out.println("var g_cal_count = 1;"); // number of calendars on this page
    out.println("var g_cal_year = new Array(g_cal_count - 1);");
    out.println("var g_cal_month = new Array(g_cal_count - 1);");
    out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);");
    out.println("var g_cal_ending_month = new Array(g_cal_count - 1);");
    out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);");
    out.println("var g_cal_ending_day = new Array(g_cal_count - 1);");
    out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);");
    out.println("var g_cal_ending_year = new Array(g_cal_count - 1);");
    
    // this calendar needs to end today
    out.println("g_cal_ending_month[0] = " + cal_month + ";");
    out.println("g_cal_ending_day[0] = " + cal_day + ";");
    out.println("g_cal_ending_year[0] = " + cal_year + ";");
    
    // set starting month/year for calendar
    out.println("g_cal_month[0] = " + ( (mm==0) ? cal_month : mm ) + ";");
    out.println("g_cal_year[0] = " + ( (yy==0) ? cal_year : yy ) + ";");
    
    // reset date parts
    cal_date.add(Calendar.YEAR, -1); // subtract a year
    cal_year = cal_date.get(Calendar.YEAR);
    cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based

    // specificing beginning of calendar (30 days back)
    out.println("g_cal_beginning_month[0] = " + cal_month + ";");
    out.println("g_cal_beginning_year[0] = " + cal_year + ";");
    out.println("g_cal_beginning_day[0] = " + cal_day + ";");
    
    // refine our function that's called when user clicks day on calendar
    out.println("function sd(pCal, pMonth, pDay, pYear) {");
    out.println("  f = document.getElementById(\"cal_box_\"+pCal);");
    out.println("  f.value = pMonth + \"-\" + pDay + \"-\" + pYear;");
    out.println("  var d = (pYear * 10000) + (pMonth * 100) + (pDay * 1);");
    out.println("  document.forms[\"frmPostScore\"].sdate.value = d;");
    out.println("}");
    
    out.println("function selectTees(pCourseId) {");
    out.println(" f = document.forms[\"frmPostScore\"];");
    out.println(" f.course_id.value = pCourseId;");
    out.println(" f.method = \"GET\";");
    out.println(" f.submit();");
    out.println("}");
    
    out.println("function submitForm() {" );
    out.println(" f = document.forms[\"frmPostScore\"];");
    out.println(" if (f.score.value == '') {");
    out.println("  alert('Please provide a score to post.');");
    out.println("  return;");
    out.println(" }");
    out.println(" if (f.sdate.value == '') {");
    out.println("  alert('Please select a date you wish to post a score for.');");
    out.println("  return;");
    out.println(" }");
    //out.println(" b = document.getElementById(\"btnSubmit\");");
    out.println(" f.btnSubmit.value='Please Wait';");
    out.println(" f.btnSubmit.disabled=true;");
    out.println(" f.submit();");
    out.println("}");
    
    out.println("</script>");
    
    out.println("<script type=\"text/javascript\">\ndoCalendar('0');\n</script>");
    
 }
 
 
 // *********************************************************
 // Gather request information for displaying current hdcp
 // and option to get the last 20 scores posted.
 // *********************************************************
 
 private void getViewScores(HttpServletRequest req, Connection con, PrintWriter out) {

/*
    String bdate = "20061001";
    String edate = "20061101";  
    //Common_ghin.getPostedScoresForClub("Test", "56", "103", bdate, edate, con, out);
    Common_ghin.getPostedScores(out);
*/
    out.println("<br><p align=center><font size=5>Handicap Inquiry</font></p>");

    
    out.println("<center>");
    out.println("<form method=POST action=/" +rev+ "/servlet/Member_handicaps name=frmHdcpInquiry id=frmHdcpInquiry>");
    out.println("<input type=hidden name=todo value=view>");
    
    out.println("<input type=checkbox name=inc20 value=yes> Include last 20 postings");
    
    out.println("<br><br><input type=submit value=\" Retrieve \" onclick=\"submitForm()\" name=btnSubmit>");
    
    out.println("</form>");
    
    /*
     * WE CAN MAKE THIS BUTTON OPTIONAL OR CONFIGURABLE IF A CLUB DOES NOT 
     * WANT TO ALLOW MEMBERS TO DO PEER REVIEW REPORTS ON ANY/ALL MEMBERS
     */
    out.println("<br><p>");
    out.println("<form method=POST action=/" +rev+ "/servlet/Member_handicaps>");
    out.println("<input type=hidden name=todo value=peer>");
    out.println("<input type=submit value=\"Peer Review Reports\" name=btnSubmit>");
    out.println("</form>");
    out.println("</p>");
    
    out.println("</center>");
    
    out.println("<script type=\"text/javascript\">");
    out.println("function submitForm() {" );
    out.println(" f = document.getElementById(\"frmHdcpInquiry\");");
    out.println(" b = document.getElementById(\"btnSubmit\");");
    out.println(" b.value='Please Wait';");
    out.println(" b.disabled=true;");
    out.println(" f.submit();");
    out.println("}");
    
    out.println("</script>");

 }
 
 
 // *********************************************************
 // Handles request for displaying current hdcp and last 20 scores
 // *********************************************************
 
 private void doViewScores(String user, String club, String clubNum, String clubAssocNum, HttpServletRequest req, Connection con, PrintWriter out) {
     
    String inc20 = (req.getParameter("inc20") == null) ? "" : req.getParameter("inc20");
    String hdcp[] = new String[6];
    String results[] = new String[22];
    
    hdcp = Common_ghin.getCurrentHdcp(user, club, clubNum, clubAssocNum, out, con);
    
    /*
    hdcp[0] = "1";
    hdcp[1] = "";
    hdcp[2] = "14.7"; // hdcpIndex;
    hdcp[3] = "0"; //hdcpDiff;
    hdcp[4] = "20070201"; //hdcpDate;
    hdcp[5] = "1";  //status
    */
    
    if (hdcp[0] == null || !hdcp[0].equals("1")) {
        
        out.println("<br><p>We were unable to retrieve your handicap information at this time, please try again later.<br><br>Error: " + hdcp[1] + "</p>");
        return;
    } else if (hdcp[5].equals("2")) {
        
        out.println("<br><p>Your GHIN number is not active at this time.</p>");
        return;
    }
    
    int yy = 0;
    int mm = 0;
    int dd = 0;
    int date = 0;
    int temp = 0;
    
    try {

        date = Integer.parseInt(hdcp[4]);
    }
    catch (NumberFormatException e) { }

    //
    //  isolate yy, mm, dd
    //
    yy = date / 10000;                     
    temp = yy * 10000;
    mm = date - temp;
    temp = mm / 100;
    temp = temp * 100;
    dd = mm - temp;
    mm = mm / 100;
    
    String sdate = mm + "/" + dd + "/" + yy;
    String parts[] = new String[3];
    
    // COMPUTE COURSE HDCPS
    String course = "";
    String lastCourse = "";
    String teeName = "";
    double rating = 0;
    int slope = 0;
    double c_hdcp = 0;
    double g_hdcp = 0;
    boolean first = true;
    
    /*
    try {
        
        g_hdcp = Double.parseDouble(hdcp[2]);
    } catch (Exception exc) {
        out.println("Can't parse USGA handicap.  " + exc.toString());
        return;
    }
    */
    
    g_hdcp = Common_ghin.parseHandicapValue(hdcp[2]);
    
    out.println("<br><p align=center><font size=5>Handicap Inquiry Results</font></p>");
    
    out.println("<p align=center><b><i>As of " + sdate + ", your USGA handicap is " + hdcp[2] + "</i></b></p>");
    
    if (g_hdcp != -99) {

        try {

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("" +
                    "SELECT c.courseName, t.tee_name, t.tee_rating18, t.tee_slope18 " +
                    "FROM clubparm2 c, tees t " +
                    "WHERE c.clubparm_id = t.course_id " +
                    "ORDER BY courseName, t.tee_slope18 DESC");

            while (rs.next()) {
                course = rs.getString("courseName");
                teeName = rs.getString("tee_name");
                rating = rs.getDouble("tee_rating18");
                slope = rs.getInt("tee_slope18");

                c_hdcp = (g_hdcp * slope) / 113;

                c_hdcp = Math.round(c_hdcp);
                
                if (first) {
                    out.println("<table width=400 align=center cellspacing=0 cellpadding=5 bgcolor=#F5F5DC border=1>"); // style=\"border: 1px solid #336633\"
                    out.println("<tr bgcolor=#336633><td align=center colspan=" + ((!course.equals("")) ? "3" : "2") + "><font size=4 color=white><b>Your Course Handicaps</b></font></td></tr>");
                    out.println("<tr>");
                    if (!course.equals("")) out.println("<td align=center><b>Course</b></td>");
                    out.println("<td align=center><b>Tees</b></td><td align=center><b>Handicap</b></td></tr>");
                    first = false;
                }

                out.println("<tr>");
                if (!course.equals("")) out.println("<td>" + ((!lastCourse.equals(course)) ? course : "") + "</td>");
                out.println("<td>" + teeName + "</td>");
                out.println("<td align=center>");
                if (c_hdcp > 0) {
                    // positive value - scratch golfer! show plus sign
                    out.print("+" + (int)c_hdcp);
                } else {
                    // negative value - normal value suppress minus sign
                    out.print(Math.abs((int)c_hdcp));
                }
                out.println("</td></tr>");
                lastCourse = course;
            }

        } catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error loading course and tee information.", exc.getMessage(), out, false);
        }
    
    }
        
    out.println("</table><br><br>");
    
    
    // DISPLAY THE LAST 20 SCORE POSTINGS FOR THIS USER
    if (inc20.equals("yes")) {
        int s = 0;
        results = Common_ghin.getRecentPostings(user, club, clubNum, clubAssocNum, out, con);
        //out.println("<p>Your most recent score postings are:</p>");
        out.println("<table align=center bgcolor=\"#F5F5DC\" border=1 width=400>");
        out.println("<tr><td colspan=3 align=center bgcolor=\"#336633\"><font size=4 color=white><b>Recent Score Postings</b></font>");
        out.println("<br><br><font size=2 color=#F5F5DC>" +
                "Legend: H = Home, A = Away, T = Tournament, I = Internet<br>" +
                "AI = Away Internet, TI = Tournament Internet, C = Combined Nines<br>" +
                "CI = Combined Internet, P = Penalty" +
                "</font></td></tr>");
        out.println("<tr><td align=center><b>Date</b></td><td align=center><b>Score</b></td><td align=center><b>Type</b></td></tr>");
        
        for (int i = 2; i < results.length; i++) {
            
          if (results[i] != null) {
              parts = results[i].split("\\|");
              s = 0;
              try {
                  s = Integer.parseInt(parts[0]);
              } catch (Exception ignore) { }
              out.print("<tr><td align=center>" + parts[2] + "</td>");
              out.print("<td align=center>" + ( (s == 0) ? parts[0] : s ) + "</td>");
              out.print("<td align=center>" + parts[1] + "</td></tr>");
          }
        }
        
        out.println("</table>");
        
    }
    
    out.println("<br><br>");
    
 }
 

 // *********************************************************
 // Allow members to select a fellow member for peer review
 // *********************************************************
 private void getViewPeerScores(HttpServletRequest req, String club, Connection con, PrintWriter out) {
   
   Statement stmt = null;
   ResultSet rs = null;

   int thisYear = 0;
   int calYear = 0;
   int firstYear = 0;

   String name = "";
   String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");

   if (req.getParameter("name") != null) {        // if user specified a name to search for

      name = req.getParameter("name");            // name to search for
      
      if (!name.equals( "" )) {
         
         name = SystemUtils.getUsernameFromFullName(name, con);
         viewSavedScores(req, name, con, out);
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
      
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<br><p><font size=5>Peer Review Reports</font></p>");
   
   out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\" width=600>");
   out.println("<tr><td>");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<p>To lookup recent score postings of a member, enter the name, ");
      out.println("or use the Member List to select the first letter of their last name.&nbsp; ");
      out.println("This will search for all names that start with the letter you select.</p>");
      out.println("</font>");
   out.println("</td></tr></table>");

   out.println("<font size=\"2\" face=\"Courier New\">");
   out.println("<p align=\"center\">(Click on <b>'Member List'</b> on right to view a list of members)</p>");
   out.println("<br></font>");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<form action=\"/" +rev+ "/servlet/Member_handicaps\" method=\"post\" target=\"bot\" name=\"f\">");
   out.println("<input type=\"hidden\" name=\"todo\" value=" + todo + ">");

   out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr>");
         out.println("<td valign=\"top\" align=\"center\">");

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\">");
               out.println("<tr><td width=\"250\" align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>Name: &nbsp;");
                     out.println("<input type=\"text\" name=\"name\" size=\"20\" maxlength=\"40\">");
                     out.println("</input>");
                  out.println("<br><br>");
                  out.println("<input type=\"submit\" value=\"Search\" name=\"search\">");
                  out.println("</p>");
                  
                  //out.println("<input type=checkbox name=inc20 value=yes> Include last 20 postings<br><br>"); // View 20 Most Recent Scores
                  
                  out.println("</font>");
               out.println("</td></tr>");
            out.println("</table>");
              
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
            out.println("<select size=\"8\" name=\"bname\" onClick=\"movename(this.form.bname.value)\">");

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
    out.println("</center></font>");
    
 }
 
 
 
 private void viewSavedScores(HttpServletRequest req, String user, Connection con, PrintWriter out) {
    
    
    // Get the hdcp number from the username
    String hdcp_num = Common_handicaps.getHdcpNum(user, con);
    String fullName = "";
    String tee_name = "";

    ArrayList<Integer> posting_dates = new ArrayList<Integer>(20);
    ArrayList<Integer> posting_scores = new ArrayList<Integer>(20);
    ArrayList<Integer> posting_used = new ArrayList<Integer>(20);
    ArrayList<String> posting_types = new ArrayList<String>(20);
    ArrayList<String> posting_tees = new ArrayList<String>(20);
    
    int multi = 0;
    
    try {

        // get the last 20 scores posted
        PreparedStatement pstmt = con.prepareStatement("" +
                "SELECT (SELECT multi FROM club5) AS multi, CONCAT(name_first, ' ', name_last) AS fullName FROM member2b WHERE username = ?;");
        pstmt.clearParameters();
        pstmt.setString(1, user);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            fullName = rs.getString("fullName");
            multi = rs.getInt("multi");
        }
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading member data for handicap peer report.", exc.getMessage(), out, false);
        return;
    }
    
    out.println("<br><p align=center><font size=5>Handicap Peer Review Report</font>");
    
    out.println("<br><br><font size=4><i>For " + fullName + " (" + hdcp_num + ")</i></font></p><br>");
    
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
    
    
    try {

        // get the last 20 scores posted
        PreparedStatement pstmt = con.prepareStatement("" +
                "SELECT DATE_FORMAT(sp.date, '%Y%m%d') AS date, sp.score, sp.type, t.tee_name " +
                "FROM score_postings sp " +
                "LEFT OUTER JOIN tees t ON t.tee_id = sp.tee_id " +
                "WHERE hdcpNum = ? AND type <> 'A' AND type <> 'AI' " +
                "ORDER BY date DESC LIMIT 40;");
        
        pstmt.clearParameters();
        pstmt.setString(1, hdcp_num);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            posting_dates.add(rs.getInt("date"));
            posting_scores.add(rs.getInt("score"));
            posting_types.add(rs.getString("type"));
            posting_used.add(0); // default it to not used
            if (rs.getString("tee_name") != null) {
                posting_tees.add(rs.getString("tee_name"));
            } else {
                posting_tees.add("N/A");
            }
        }
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading posted scores for handicap peer report.", exc.getMessage(), out, false);
        return;
    }
    
    try {
        
        // get the last 20 rounds played
        PreparedStatement pstmt = con.prepareStatement("" +
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
                    "(username1 = ? && show1 = 1) || " +
                    "(username2 = ? && show2 = 1) || " +
                    "(username3 = ? && show3 = 1) || " +
                    "(username4 = ? && show4 = 1) || " +
                    "(username5 = ? && show5 = 1)" +
                ") " +
                "ORDER BY date DESC " +
                "LIMIT 20;");
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
        ResultSet rs = pstmt.executeQuery();
        
        int post_date = 0;
        int play_date = 0;
        int last_date = 0;
        int score = 0;
        //int value = 0;
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
        boolean found = false;
        
        while (rs.next()) {
            
            play_date = rs.getInt("date");
            course = rs.getString("courseName");
            holes = (rs.getInt("9hole") == 1) ? 9 : 18;
            found = false;
            
            // see if there is a posting for this round
            for (i = 0; i < posting_dates.size(); i++) {
                
                post_date = (Integer)posting_dates.get(i).intValue();
                score = (Integer)posting_scores.get(i).intValue();
                type = (String)posting_types.get(i);
                tees = (String)posting_tees.get(i);
                used = (Integer)posting_used.get(i).intValue();
                
                yy = post_date / 10000;                     
                temp = yy * 10000;
                mm = post_date - temp;
                temp = mm / 100;
                temp = temp * 100;
                dd = mm - temp;
                mm = mm / 100;
                post_sdate = mm + "/" + dd + "/" + yy;
                   
                yy = play_date / 10000;                     
                temp = yy * 10000;
                mm = play_date - temp;
                temp = mm / 100;
                temp = temp * 100;
                dd = mm - temp;
                mm = mm / 100;
                play_sdate = mm + "/" + dd + "/" + yy;
                
                if (post_date == play_date && used == 0) { //  bgcolor=#F5F5DC   bgcolor=#86B686

                    posting_used.set(i, 1);
                    out.println("<tr align=center bgcolor=#86B686>" +
                            "<td nowrap>" + play_sdate + "</td>" +
                            ((multi == 0) ? "" : "<td nowrap>" + course + "</td>") +
                            "<td>" + holes + "</td> <td nowrap>" + post_sdate + "</td><td>" + score + "</td><td>" + type + "</td><td>" + tees + "</td></tr>");
                    found = true;
                    break;
                    
                } // end if matching dates
            
            } // end for loop
            
            // if no posted score was found for this round
            if (!found) { // && i != 0) {
                yy = play_date / 10000;
                temp = yy * 10000;
                mm = play_date - temp;
                temp = mm / 100;
                temp = temp * 100;
                dd = mm - temp;
                mm = mm / 100;
                play_sdate = mm + "/" + dd + "/" + yy;
                out.println("<tr align=center><td nowrap>" + play_sdate + "</td>" +
                        ((multi == 0) ? "" : "<td>" + course + "</td>") + 
                        "<td>" + holes + "</td> <td colspan=4 bgcolor=#FFFF8F>&nbsp;</td></tr>");
            }

            last_date = play_date; // remember the last date of play
            
        } // end while

        boolean didHeader = false;

        for (i = 0; i < posting_dates.size(); i++) {

            post_date = (Integer)posting_dates.get(i).intValue();
            used = (Integer)posting_used.get(i).intValue();

            if (post_date >= last_date && used == 0) {

                score = (Integer)posting_scores.get(i).intValue();
                type = (String)posting_types.get(i);
                tees = (String)posting_tees.get(i);

                yy = post_date / 10000;
                temp = yy * 10000;
                mm = post_date - temp;
                temp = mm / 100;
                temp = temp * 100;
                dd = mm - temp;
                mm = mm / 100;
                post_sdate = mm + "/" + dd + "/" + yy;

                if (!didHeader) {

                    out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                                "<td colspan=\"" + ((multi == 0) ? "6" : "7") + "\" align=\"center\">Unmatched Score Postings</td></tr>");
                    didHeader = true;
                }

                out.println("<tr align=\"center\">" +
                        "<td bgcolor=#FFFF8F colspan=\"" + ((multi == 0) ? "2" : "3") + "\">&nbsp;</td>" +
                        "<td nowrap>" + post_sdate + "</td><td>" + score + "</td><td>" + type + "</td><td>" + tees + "</td></tr>");

            } // end unmatched postings loop

        } // end for loop

        int [] rounds = new int[3];
        int today = (int)SystemUtils.getDate(con);
        rounds = Proshop_report_handicap.lookupHistory(last_date, today, user, con, out);

        out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                    "<td colspan=\"" + ((multi == 0) ? "6" : "7") + "\" align=\"center\">Rounds Played vs. Scores Posted<br><font size=2>(for last twenty rounds)</font></td></tr>");

        out.println("<tr bgcolor=\"#86B686\">" +
                "<td align=\"center\" colspan=\"" + ((multi == 0) ? "2" : "3") + "\">9 hole: <b>" + rounds[0] + "</b> &nbsp; &nbsp; 18 hole: <b>" + rounds[1] + "</b></td>" +
                "<td align=\"center\" colspan=\"4\">Posted: <b>" + rounds[2] + "</b></td></tr>");

    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading data for handicap peer report.", exc.getMessage(), out, false);
        return;
    }
    
    out.println("</table>");
     
    out.println("<br><br>");
    
    if (req.getParameter("excel") == null) {     // if normal request
        out.println("<center><form method=\"post\" action=\"/" +rev+ "/servlet/Member_handicaps\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"todo\" value=\"peer\">");
        out.println("<input type=\"hidden\" name=\"user\" value=\"" + user + "\">");
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></center>");
        
        out.println("" +
            "<table align=center><tr>" +
            "<td><form method=\"post\"><input type=submit value=\"Back\" style=\"width: 75px; background:#8B8970\"><input type=\"hidden\" name=\"todo\" value=\"peer\"></form></td>" +
            "<td>&nbsp; &nbsp; &nbsp;</td>" +
            "<td><form action=/" + rev + "/servlet/Member_announce><input type=submit value=\"Home\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "</tr></table>");
    } // end if not excel
    
 }    
  
 
 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

    out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
    out.println("<BODY><CENTER>");
    out.println("<BR><H2>Access Error</H2><BR>");
    out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
    out.println("<BR>This site requires the use of Cookies for security purposes.  We use them to verify");
    out.println("<BR>your session and prevent unauthorized access.  Please check your 'Privacy' settings,");
    out.println("<BR>under 'Tools', 'Internet Options' (for MS Internet Explorer).  This must be set to");
    out.println("<BR>'Medium High' or lower.  Thank you.");
    out.println("<BR><BR>");
    out.println("<a href=\"/" +rev+ "/servlet/Logout\" target=\"_top\">Return</a>");
    out.println("</CENTER></BODY></HTML>");
    out.close();

 }
 
} // end servlet public class