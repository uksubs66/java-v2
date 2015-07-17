/***************************************************************************************
 *   Proshop_handicaps: This servlet will allow the proshop to manage their members handicaps.
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
 *        1/16/14   GolfNet code updates
 *        8/02/13   Display 'USGA Handicap Index' instead of 'USGA handicap' for all clubs.  This is the proper term.
 *        5/16/13   GolfNet fixes for posting scores
 *        4/22/13   Update the error message when a member is missing a club or association number to make it more
 *                  descriptive.  Also, inform the pro what to do to correct the problem.
 *        4/02/13   Long Cove Club (longcove) - Display "USGA index" instead of "USGA handicap" on view score postings page.
 *        3/21/13   List of tees will now be sorted by a sort_by value first, then tee_id.
 *        1/03/13   Updates for GolfNet
 *       11/29/12   Add support for new score_postings table
 *        7/11/12   Updates for new GHIN interface
 *        5/25/12   Fixed issue with how course handicaps were getting rounded. Since the handicaps are negative, -.5 values were getting rounded "up", where they needed to round "down" (e.g. -28.5 rounded to -28 instead of -29).
 *       10/06/11   Modified alphaTable.nameList() calls to pass an additional parameter used for displaying inact members when modifying past tee times.
 *        8/02/11   Blackhawk CC (blackhawk) - Display "USGA index" instead of "USGA handicap" on view score postings page.
 *        6/24/10   Modified alphaTable calls to pass the new enableAdvAssist parameter which is used for iPad compatability
 *       11/05/09   Display tees in order they were entered into the database (by tee_id) for Wellesley CC
 *       12/10/08   Added order by clause to tees table query
 *        9/02/08   Javascript compatability updates
 *        9/02/08   Add link for International CC to the Virginia St. Golf Assoc. web site (Case 1507)
 *        8/27/08   Updated javascript for better cross-browser support
 *        7/18/08   Added limited access proshop users checks
 *        6/09/08   Made date box value sticky
 *        5/14/08   Add label for 'Include Last 20' checkbox
 *        5/13/08   Use round() not cast to display course handicaps
 *        4/08/08   Update Legend & suppress minus sign/add plus sign to handicap values
 *        1/03/08   Removed hdcp season check
 *        6/11/07   Added additional checks for ensuring club & assoc numbers are assinged
 *        5/01/07   Updated SQL statement in doViewBulkScores
 *
 *                  
 *                  
 ***************************************************************************************
 */

import com.foretees.common.Common_Server;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.parmCourse;
import com.foretees.common.alphaTable;
import com.foretees.common.getParms;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;

public class Proshop_handicaps extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    
 //*****************************************************
 // Process the a get method on this page as a post call
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
    resp.setContentType("text/html");
    
    PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) return;
    Connection con = Connect.getCon(req);
    
    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Unable to connect to the database.", "", out, true);
        return;
    }
    
    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "TOOLS_HDCP", con, out)) {
        SystemUtils.restrictProshop("TOOLS_HDCP", out);
        return;
    }
    
    String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    
    // DONE VALIDATING THIS REQUEST - WE'RE READY TO GO
    
    
    // GET REQUEST VARIABLES WE WOULD NEED TO ACT ON
    String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");
    String club = (String)session.getAttribute("club");
    
    
    // START DEFAULT PAGE OUTPUT
    out.println(SystemUtils.DOCTYPE);
    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<script language=\"javascript\" src=\"/" +rev+ "/web%20utilities/foretees.js\"></script>");
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
    out.println("<script language=\"javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);
    
    
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
    
    /*
    if (in_season == 0) {
        
        out.println("<br><br><p align=center><b><i>Your club's handicap system is currently out of season and is unavailable at this time.</i></b></p>");
        return;
    }
    */
    
    // HANDLE POST SCORE REQUEST
    if (todo.equals("post")) getScoreToPost(req, club, session, con, out);
    if (todo.equals("view")) getViewScores(req, club, session, con, out);
    
    out.println("<br></body></html>");
    
 } // end of doGet routine
 
 
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

    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) return;
    Connection con = Connect.getCon(req);
    
    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Unable to connect to the database.", "", out, true);
        return;
    }
    
    String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    
    // DONE VALIDATING THIS REQUEST - WE'RE READY TO GO

    
    // GET REQUEST VARIABLES WE WOULD NEED TO ACT ON
    String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");
    String club = (String)session.getAttribute("club");               // get club name
    
    
    // START DEFAULT PAGE OUTPUT
    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<script language=\"javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
    out.println("<script language=\"javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);
    
    
    // HANDLE POST SCORE REQUEST
    if (todo.equals("post")) doPostScore(club, req, con, out);
    if (todo.equals("view")) getViewScores(req, club, session, con, out);
    
    out.println("<br></body></html>");
    
 } // end of doPost routine
 
 
 private void doPostScore(String club, HttpServletRequest req, Connection con, PrintWriter out) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    long date = 0;

    int course_id = 0;
    int tee_id = 0;
    int holes = 0;
    int score = 0;
    int esc_score = 0;

    String sdate = (req.getParameter("sdate") == null) ? "" : req.getParameter("sdate");
    String type = (req.getParameter("type") == null) ? "" : req.getParameter("type");
    String stid = (req.getParameter("tee_id") == null) ? "" : req.getParameter("tee_id");
    String sholes = (req.getParameter("holes") == null) ? "" : req.getParameter("holes");
    String sscore = (req.getParameter("score") == null) ? "" : req.getParameter("score");
    String sescscore = (req.getParameter("escScore") == null) ? "" : req.getParameter("escScore");
    String member = (req.getParameter("member") == null) ? "" : req.getParameter("member");
    
    String clubNum = "";
    String clubAssocNum = "";
    
    if (member.equals("")) {
        // no member name passed
    }
    
    try {
        
        tee_id = Integer.parseInt(stid);
        holes = Integer.parseInt(sholes);
        date = Integer.parseInt(sdate);
        //score = Integer.parseInt(sscore);
        
    } catch (NumberFormatException exc) {
        out.println("<br>NF ERROR: " + exc.toString());
        return;
    }
    
    String user = SystemUtils.getUsernameFromFullName(member, con);
    
    String hdcpSystem = Common_handicaps.getClubHdcpOption(club, con);
    boolean isGHIN = hdcpSystem.equalsIgnoreCase( "GHIN" );
    boolean isGolfNet = hdcpSystem.equalsIgnoreCase( "GN21" );
    
    if (isGHIN) {
        
        // GET MEMBERS CLUB/ASSOC NUMBERS
        try {

            pstmt = con.prepareStatement("" +
                    "SELECT cn.club_num, ca.assoc_num " +
                    "FROM member2b m, hdcp_club_num cn, hdcp_assoc_num ca " +
                    "WHERE " +
                        "m.username = ? AND " +
                        "m.hdcp_club_num_id = cn.hdcp_club_num_id AND " +
                        "m.hdcp_assoc_num_id = ca.hdcp_assoc_num_id;");
            pstmt.clearParameters();
            pstmt.setString(1, user);
            rs = pstmt.executeQuery();

            if (rs.next()) {

                clubNum = rs.getString("club_num");
                clubAssocNum = rs.getString("assoc_num");

            } else {

                Utilities.logError("GHIN - Missing Club/Assoc Binding: Club=" + club + ", User=" + user);

                out.println("<p align=center>This member is missing a Club Number or Association Number, or both.<br>"
                        + "Please correct and try again.<br><br>To add this information, login as an admin user and "
                        + "update this member's record.</p>");

                //out.println("<p align=center>This member is not bound to both a club and an association.  Please correct and try again.</p>");
                return;
            }

        } catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
            return;

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}
        }
    }

/*
    // NEXT LETS CHECK TO MAKE SURE THIS MEMBER HAS A ROUND IN TEEPAST2 FOR THIS DATE
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    boolean found = false;
    
    try {

        pstmt = con.prepareStatement("" +
                "SELECT * " +
                "FROM teepast2 " +
                "WHERE " +
                    "date = ? AND ( " +
                        "(username1 = ? AND show1 = 1 && nopost1 = 0) OR " +
                        "(username2 = ? AND show2 = 1 && nopost2 = 0) OR " +
                        "(username3 = ? AND show3 = 1 && nopost3 = 0) OR " +
                        "(username4 = ? AND show4 = 1 && nopost4 = 0) OR " +
                        "(username5 = ? AND show5 = 1 && nopost5 = 0) " +
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

    if (found == false) {
        
        out.println("<p align=center><b><i>A round was not found in the system for this member on the selected date.</i><br><br>Please select a date for which they played a round of golf.</b></p>");
        out.println("<p align=center><form><input type=button onclick=\"window.history.go(-1)\" value=\" Go Back \"></form></p>");
        return;
    }
*/    
    
    String msg[] = new String[2];

    if (isGHIN) {

        try {
            score = Integer.parseInt(sscore);
        } catch (NumberFormatException exc) {
            out.println("<br>Score is not a valid number. " + exc.toString());
            return;
        }
        
        msg = Common_ghin.postScore(user, club, date, tee_id, score, type, holes, clubNum, clubAssocNum, out, con);
    
    } else if (isGolfNet) {

        try {
            esc_score = Integer.parseInt(sescscore);
        } catch (NumberFormatException exc) {
            out.println("<br>Score is not a valid number. " + exc.toString());
            return;
        }
        
        msg = Common_golfnet.postScore(user, club, date, tee_id, score, esc_score, type, holes, 0, out, con);
        
    }

    if (msg[0] == null || !msg[0].equals("1")) {
        
        out.println("<p align=center><b>The score was not posted.&nbsp; Please try again later.</b><br><br>Reason given was: <i>" + msg[1] + "</i></p>");
    } else {
        
        out.println("<p align=center><b>The score was successfully posted.</b></p>"); // <br><br>Message: " + msg[1] + "
    }


    out.println("<br>");
    //out.println("<span style=\"width:200px; margin:auto\">");
    out.println("<center>");
    out.println("<button onclick=\"window.location.href='Proshop_handicaps?todo=post'\">Post Another Score</button>");
    out.println("<br><br>");
    out.println("<button onclick=\"window.location.href='Proshop_announce'\">Home</button>");
    out.println("</center>");
    //out.println("</span>");

    out.println("");

 }
 
 
 private void getScoreToPost(HttpServletRequest req, String club, HttpSession session, Connection con, PrintWriter out) {

    ResultSet rs = null;
    int multi = 0;
    
    String courseName = "";
    int course_id = 0;
    
    String hdcpSystem = Common_handicaps.getClubHdcpOption(club, con);
    
   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();
   try {
    getParms.getCourse(con, parmc, "");
   } catch (Exception ignore) {}
   
   String user = (String)session.getAttribute("user");
   String mshipOpt = (String)session.getAttribute("mshipOpt");
   String mtypeOpt = (String)session.getAttribute("mtypeOpt");

   if (mshipOpt.equals( "" ) || mshipOpt == null) {

      mshipOpt = "ALL";
   }
   if (mtypeOpt.equals( "" ) || mtypeOpt == null) {

      mtypeOpt = "ALL";
   }
   
    // see if date parts are being passed in, if so default calendar to said mm & yy
    String smm = (req.getParameter("mm") == null) ? "0" : req.getParameter("mm");
    String syy = (req.getParameter("yy") == null) ? "0" : req.getParameter("yy");
    String sdd = (req.getParameter("dd") == null) ? "0" : req.getParameter("dd");
    String scid = (req.getParameter("course_id") == null) ? "0" : req.getParameter("course_id");
    String cal_box_0 = (req.getParameter("cal_box_0") == null) ? "" : req.getParameter("cal_box_0");
    String sdate = (req.getParameter("sdate") == null) ? "" : req.getParameter("sdate");
    String type = (req.getParameter("type") == null) ? "" : req.getParameter("type");
    String sholes = (req.getParameter("holes") == null) ? "" : req.getParameter("holes");
    String sscore = (req.getParameter("score") == null) ? "" : req.getParameter("score");
    String sescScore = (req.getParameter("escScore") == null) ? "" : req.getParameter("escScore");
    String member = (req.getParameter("member") == null) ? "" : req.getParameter("member");
    
    int mm = 0;
    int yy = 0;
    int dd = 0;
    int escScore = 0;
    
    try {

        course_id = Integer.parseInt(scid);
        mm = Integer.parseInt(smm);
        yy = Integer.parseInt(syy);
        dd = Integer.parseInt(sdd);
        escScore = Integer.parseInt(sescScore); // must be last since this fails for non-golfnet
    }
    catch (NumberFormatException e) { }
    
    
    out.println("<p align=center><font size=5>Post a Score</font></p>");
    
    //out.println("<table align=center border=1 style=\"border: 1px solid #336633\"><tr><td>");
    out.println("<table border=0 align=center><tr><td>"); // bgcolor=#CCCCAA
    
    out.println("<table align=center border=0 height=165>\n<tr valign=top>\n<td align=center><!--<b>Select Date of Play</b><br>-->");   // was 190 !!!

    out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

    out.println("</td>\n<tr>\n</table>");
    
    out.println("<table align=center width=400>");
    
    out.println("<form action=Proshop_handicaps method=POST name=frmPostScore id=frmPostScore>");
    out.println("<input type=hidden name=todo value=\"post\">");
    out.println("<input type=hidden name=sdate value=\"" + sdate + "\">");
    out.println("<input type=hidden name=course_id value=\"\">");    
    
    out.println("<tr><td align=right><b>Member:&nbsp;</td><td><input type=text name=member value=\"" + member + "\" onfocus=\"this.blur()\" ondblclick=\"alert('Choose a member using the lists on the right.');\"></td></tr>");
    
    out.println("<tr><td align=right><b>Date Played:&nbsp;</b></td><td><input type=text value=\"" + cal_box_0 + "\" name=cal_box_0 id=cal_box_0 size=10 onfocus=\"this.blur()\" ondblclick=\"alert('Choose a date using the calendar above.');\"></td></tr>"); // ((dd == 0) ? "" : yy + "-" + mm + "-" + dd)
    
    try {
        
        Statement stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");

        if (rs.next()) multi = rs.getInt(1);
        
    } 
    catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error getting club options.", exc.getMessage(), out, false);
        return;
    }
    
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

    out.println("<tr><td align=right><b>Tees Played:&nbsp;</b></td>");
    out.println("<td><select name=tee_id size=1>");
    
    try {

        PreparedStatement pstmt = con.prepareStatement("SELECT tee_id, tee_name FROM tees WHERE course_id = ? ORDER BY sort_by, tee_id");
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
    
    out.println("<td align=right><b>Holes Played:&nbsp;</b></td>");
    out.println("<td><select name=holes size=1>");
    out.println("<option value=1" + ((sholes.equals("1")) ? " selected" : "") + ">18 Holes");
    out.println("<option value=2" + ((sholes.equals("2")) ? " selected" : "") + ">Front 9");
    out.println("<option value=3" + ((sholes.equals("3")) ? " selected" : "") + ">Back 9");
    out.println("</select></td>");
    
    out.println("</tr><tr>");
    
    out.println("<td align=right><b>Play Type:&nbsp;</b></td>");
    out.println("<td><select name=type size=1>");
    out.println("<option value='H'" + ((type.equals("H")) ? "selected" : "") + ">Home");
    //out.println("<option value='A'" + ((type.equals("A")) ? "selected" : "") + ">Away");
    out.println("<option value='T'" + ((type.equals("T")) ? "selected" : "") + ">Tournament");
    //out.println("<option value='I'" + ((type.equals("I")) ? "selected" : "") + ">Internet");
    out.println("</select></td>");
    
    out.println("</tr><tr>");
        
    if (hdcpSystem.equalsIgnoreCase("GN21")) {
        out.println("<td align=right><b>ESC Score:&nbsp;</b></td>");
        out.println("<td><input type=text name=escScore size=5 maxlength=3 value=\"" + sescScore + "\"></td>");
        out.println("</tr><tr><td colspan=2>");
        out.println("\"Equitable Stroke Control\" (ESC) is the downward adjustment of individual hole scores for handicap purposes in order to make handicaps more representative of a player's potential ability. ESC sets a maximum number that a player can post on any hole depending on the player's Course Handicap.  If you're not sure what your maximum number is, please contact your professional staff.");
        out.println("</td>");
    } else {
        out.println("<td align=right><b>Score:&nbsp;</b></td>");
        out.println("<td><input type=text name=score size=5 maxlength=3 value=\"" + sscore + "\"></td>");
        
    }
    
    out.println("</tr><tr>");
    
    out.println("<td colspan=2 align=center><br><input type=button value=\" Post Score \" onclick=\"submitForm()\" name=btnSubmit></td>");
    
    out.println("</tr></form></table>");
    
    out.println("</td><td nowrap><img src=/images/shim.gif width=20 height=1></td><td>");
    
    // if user clicked on a name letter or mtype
    if (req.getParameter("letter") != null || req.getParameter("return") != null || req.getParameter("mtypeopt") != null) { 

        if (req.getParameter("mtypeopt") != null) {

            mtypeOpt = req.getParameter("mtypeopt");
            session.setAttribute("mtypeOpt", mtypeOpt);   //  Save the member class options in the session for next time
        }
        if (req.getParameter("mshipopt") != null) {
            mshipOpt = req.getParameter("mshipopt");
            session.setAttribute("mshipOpt", mshipOpt);
        }

    }

    String letter = "%";         // default is 'List All'
    if (req.getParameter("letter") != null) {

        letter = req.getParameter("letter");

        if (letter.equals( "List All" )) {
            letter = "%";
        } else {
            letter = letter + "%";
        }
    }
    out.println("<form action=\"Proshop_handicaps\" method=\"get\" name=\"playerform\" id=\"playerform\">");
    out.println("<input type=hidden name=todo value=\"post\">");
    
    out.println("<input type=hidden name=cal_box_0 value=\"" + cal_box_0 + "\">");
    
    //
    //   Output the List of Names
    //

    //out.println("VARS: club=" + club + ", letter=" + letter + ", mshipOpt=" + mshipOpt + ", mtypeOpt=" + mtypeOpt);

    boolean enableAdvAssist = Utilities.enableAdvAssist(req);

    alphaTable.nameList(club, letter, mshipOpt, mtypeOpt, true, parmc, enableAdvAssist, false, out, con);


    out.println("</td>");                                      // end of this column
    out.println("<td valign=\"top\">");

    //
    //   Output the Alphabit Table for Members' Last Names
    //
    alphaTable.getTable(out, user);


    //
    //   Output the Mship and Mtype Options
    //
    alphaTable.typeOptions(club, mshipOpt, mtypeOpt, out, con);

    out.println("");
    out.println("</td></tr>");
    out.println("</form></table>");
    //out.println("</td></tr></table>");
    
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
    //out.println(" f = document.getElementById(\"cal_box_\"+pCal);");
    //out.println(" f.value = pMonth + \"-\" + pDay + \"-\" + pYear;");
    //out.println(" var d = (pYear * 10000) + (pMonth * 100) + (pDay * 1);");
    //out.println(" document.getElementById(\"sdate\").value = d;");
    out.println(" f = document.forms['frmPostScore'];");
    out.println(" f.cal_box_0.value = pMonth + \"-\" + pDay + \"-\" + pYear;");
    out.println(" var d = (pYear * 10000) + (pMonth * 100) + (pDay * 1);");
    out.println(" f.sdate.value = d;");
    out.println(" f = document.forms['playerform'];");
    out.println(" f.cal_box_0.value = pMonth + \"-\" + pDay + \"-\" + pYear;");
    
    out.println("}");
    
    out.println("function selectTees(pCourseId) {");
    out.println(" f = document.forms['frmPostScore'];");
    out.println(" f.course_id.value = pCourseId;");
    out.println(" f.method = \"GET\";");
    out.println(" f.submit();");
    out.println("}");

    out.println("function submitForm() {" );
    out.println(" f = document.forms['frmPostScore'];");
        
    if (hdcpSystem.equalsIgnoreCase("GN21")) {
        
        out.println(" if (f.escScore.value == '') {");
        out.println("  alert('Please provide an ESC score to post.');");
        out.println("  return;");
        out.println(" }");
        
    } else {
     
        out.println(" if (f.score != 'undefined' && f.score.value == '') {");
        out.println("  alert('Please provide a score to post.');");
        out.println("  return;");
        out.println(" }");
    
    }
    
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
    
    out.println("<script type=\"text/javascript\">");
    out.println("function subletter(x) {");
    out.println(" document.playerform.letter.value = x;");
    out.println(" playerform.submit();");
    out.println("}");
    out.println("function movename(namewc) {");
    out.println(" array = namewc.split(':');"); // split string in to 2 (name, wc)
    out.println(" var name = array[0];");
    out.println(" f = document.forms['frmPostScore'];");
    //out.println(" var member = document.frmPostScore.member.value;");
    //out.println(" document.frmPostScore.member.value = name;");
    out.println(" var member = f.member.value;");
    out.println(" f.member.value = name;");
    out.println("}");
   out.println("</script>");
   
 }
 
 
 private void getViewScores(HttpServletRequest req, String club, HttpSession session, Connection con, PrintWriter out) {

   PreparedStatement pstmt = null;
   ResultSet rs = null;

    String hdcpSystem = Common_handicaps.getClubHdcpOption(club, con);
    boolean isGHIN = hdcpSystem.equalsIgnoreCase( "GHIN" );
    boolean isGolfNet = hdcpSystem.equalsIgnoreCase( "GN21" );

   String name = "";
   String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");

   if (req.getParameter("name") != null) {        // if user specified a name to search for

      name = req.getParameter("name");            // name to search for

      //if (req.getParameter("allmems") != null) name = "ALL"; // is List All submit button sent, then change to ALL
      
      if (req.getParameter("allmems") != null) {
         
         doViewBulkScores(req, con, out);                // go process bulk hdcp inquiry
         return;
      }
      
      if (!name.equals( "" )) {
         
         name = SystemUtils.getUsernameFromFullName(name, con);

         String clubNum = "";
         String clubAssocNum = "";

         if (isGHIN) {

             // GET MEMBERS CLUB/ASSOC NUMBERS
             try {

                pstmt = con.prepareStatement("" +
                        "SELECT cn.club_num, ca.assoc_num " +
                        "FROM member2b m, hdcp_club_num cn, hdcp_assoc_num ca " +
                        "WHERE " +
                            "m.username = ? AND " +
                            "m.hdcp_club_num_id = cn.hdcp_club_num_id AND " +
                            "m.hdcp_assoc_num_id = ca.hdcp_assoc_num_id;");
                pstmt.clearParameters();
                pstmt.setString(1, name);

                rs = pstmt.executeQuery();

                if ( rs.next() ) {

                    clubNum = rs.getString("club_num");
                    clubAssocNum = rs.getString("assoc_num");

                } else {

                    Utilities.logError("GHIN - Missing Club/Assoc Binding: Club=" + club + ", User=" + name);

                    out.println("<br><p align=center>ERROR!</p>");
                    out.println("<p align=center>This member is missing a Club Number or Association Number, or both.<br>"
                        + "Please correct and try again.<br><br>To add this information, login as an admin user and "
                        + "update this member's record.</p>");
                    return;
                }

             } catch (Exception exc) {

                SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
                return;

             } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}
             }

         }

         doViewScores(req, name, club, clubNum, clubAssocNum, hdcpSystem, con, out);              // go process individual hdcp inquiry
         return;
         
      } // end if name not empty
      
   } // end if name was passed to search for

   
   // Display page/form for selecting a member
   
   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("function cursor() { document.forms['f'].name.focus(); }");
   out.println("function movename(name) {");
   out.println(" document.forms['f'].name.value = name;");
   out.println("}");
   out.println("// -->");
   out.println("</script>"); 
      
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\" width=600>");
   out.println("<tr><td>");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<p>To lookup the handicap of a member, enter the name, ");
      out.println("or use the Member List to select the first letter of their last name.&nbsp; ");
      out.println("This will search for all names that start with the letter you select.&nbsp; ");
      out.println("You may also lookup all members by clicking the 'List All Members' button.&nbsp; " +
              "<!--Check the 'Include 20 Most Recent Scores' to see the members last 20 scores posted.<br>" +
              "<i>Note: Selecting all members will disregard the last 20 postings option.</i>--></p>");
      out.println("</font>");
   out.println("</td></tr></table>");

   out.println("<font size=\"2\" face=\"Courier New\">");
   out.println("<p align=\"center\">(Click on <b>'Member List'</b> on right to view a list of members)</p>");
   out.println("<br></font>");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<form action=\"Proshop_handicaps\" method=\"post\" target=\"bot\" name=\"f\">");
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
                  
                  out.println("<input type=checkbox name=inc20 value=yes id=inc20><label for=inc20> Include 20 Most Recent Scores</label><br><br>"); // View 20 Most Recent Scores
                  
                  out.println("</font>");
               out.println("</td></tr>");
            out.println("</table>");

            // add a button to list all members tee times
            //
            out.println("<br><br>");
            out.println("<input type=\"submit\" value=\"List All Members\" name=\"allmems\">");
              
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

            pstmt = con.prepareStatement (
                    "SELECT name_last, name_first, name_mi " +
                    "FROM member2b " +
                    "WHERE ghin <> '' AND name_last LIKE ? " +
                    "ORDER BY name_last, name_first, name_mi");

            pstmt.clearParameters();
            pstmt.setString(1, letter);
            rs = pstmt.executeQuery();

            out.println("<tr><td align=\"left\"><font size=\"2\">");
            out.println("<select size=\"20\" name=\"bname\" onClick=\"movename(this.form.bname.value)\">");

            while ( rs.next() ) {

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
            
         } catch (Exception ignore) {

         } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}
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
    out.println("</center></font>");
    
 }
 
 
 private void doViewScores(HttpServletRequest req, String user, String club, String clubNum, String clubAssocNum, String hdcpSystem, Connection con, PrintWriter out) {
     
    String inc20 = (req.getParameter("inc20") == null) ? "" : req.getParameter("inc20");
    String hdcp[] = new String[6];
    String results[] = new String[22];
    String fullName = SystemUtils.getFullNameFromUsername(user, con);
    
    //hdcp = Common_ghin.getCurrentHdcp(user, club, clubNum, clubAssocNum, out, con);

    boolean isGHIN = hdcpSystem.equalsIgnoreCase( "GHIN" );
    boolean isGolfNet = hdcpSystem.equalsIgnoreCase( "GN21" );


    if ( isGHIN ) {

        hdcp = Common_ghin.getCurrentHdcp(user, club, clubNum, clubAssocNum, out, con);

    } else if ( isGolfNet ) {

        hdcp = Common_golfnet.getCurrentHdcp(user, club, con);

    }

    // output common page header
    out.println("<p align=center><font size=5>Handicap Inquiry Results<br><font size=4>" +
            ((inc20.equals("yes")) ? "<br>With Most Recent Scores" : "") +
            "<br>For</font><br>" + fullName + "</font></p>");
    
    // check for general errors
    if (hdcp[0] == null || !hdcp[0].equals("1")) {
        
        out.println("<p align=center>We were unable to retrieve your handicap information at this time, please try again later.<br><br>Error: " + hdcp[1] + "</p>");
        buildBackButton(out);
        return;
        
    } else if (hdcp[5].equals("2") && isGHIN) {
        
        out.println("<p align=center>Member has a GHIN number that is currently not active.</p>");
        buildBackButton(out);
        return;
    }
    
    
    String sdate = "";
    String parts[] = new String[3];
    
    // date format is now 2012-07-01T00:00:00
    parts = hdcp[4].split("T");
    sdate = parts[0];

    parts = new String[3];


    // COMPUTE COURSE HDCPS
    String course = "";
    String lastCourse = "";
    String teeName = "";
    double rating = 0;
    int slope = 0;
    double c_hdcp = 0;
    double g_hdcp = 0;
    boolean first = true;
            
    String hdcpDisplay = hdcp[3];
    
    //g_hdcp = Common_ghin.parseHandicapValue(hdcp[2]);

    if (isGHIN) {

        g_hdcp = Common_ghin.parseHandicapValue(hdcp[2]);

    } else if (isGolfNet) {

        g_hdcp = Common_golfnet.parseHandicapValue(hdcp[2]);
        if (g_hdcp != -99) hdcpDisplay = String.valueOf(g_hdcp / 10);
        g_hdcp = g_hdcp / 10;

    }

    //debug
    if (Common_Server.SERVER_ID == 4) out.println("<!-- hdcp[2]=" + hdcp[2] + ", g_hdcp=" + g_hdcp + ", hdcpDisplay=" + hdcpDisplay + " -->");


  //out.println("<p align=center><b><i>USGA " + (club.equals("blackhawk") ? "index" : "handicap") + " is " + hdcpDisplay + " as of " + sdate + "</i></b></p>");
    
    if (g_hdcp != -99) {

        out.println("<p align=center><b><i>As of " + sdate + ", " + fullName + "'s " + Common_handicaps.getHdcpIndexName(club, hdcpSystem) + " Handicap Index is " + hdcpDisplay + "</i></b></p>");
        //out.println("<p align=center><b><i>As of " + sdate + ", " + fullName + "'s " + Common_handicaps.getHdcpIndexName(club, hdcpSystem) + " " + ((club.equals("blackhawk") || club.equals("longcove")) ? "index" : "handicap") + " is " + hdcpDisplay + "</i></b></p>");

        Statement stmt = null;
        ResultSet rs = null;

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("" +
                    "SELECT c.courseName, t.tee_name, t.tee_rating18, t.tee_slope18 " +
                    "FROM clubparm2 c, tees t " +
                    "WHERE c.clubparm_id = t.course_id " +
                    "ORDER BY courseName, t.sort_by, " + (club.equals("wellesley") ? "t.tee_id" : "t.tee_slope18 DESC"));

            while ( rs.next() ) {

                course = rs.getString("courseName");
                teeName = rs.getString("tee_name");
                rating = rs.getDouble("tee_rating18");
                slope = rs.getInt("tee_slope18");

                c_hdcp = (g_hdcp * slope) / 113;
                              
                c_hdcp = Math.round(Math.abs(c_hdcp)) * -1;
                
                // out the header row
                if (first) {
                    out.println("<table width=400 align=center cellspacing=0 cellpadding=5 bgcolor=#F5F5DC border=1>"); // style=\"border: 1px solid #336633\"
                    out.println("<tr bgcolor=#336633><td align=center colspan=" + ((!course.equals("")) ? "3" : "2") + "><font size=4 color=white><b>Course Handicaps</b></font></td></tr>");
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

            } // end while

        } catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error loading course and tee information.", exc.getMessage(), out, false);
            
        } finally {

           try { rs.close(); }
           catch (Exception ignore) {}

           try { stmt.close(); }
           catch (Exception ignore) {}
        }
      
    } else {

        if (isGolfNet && hdcp[2].equals("999")) {
            out.println("<p align=center><b>" + fullName + "'s handicap has not yet been determined as of " + hdcp[4] + ".</b></p>");
        } else {
            out.println("<p align=center><b>" + fullName + "'s " + Common_handicaps.getHdcpIndexName(club, hdcpSystem) + " Handicap Index inquiry was not successful.</b></p>");
            inc20 = "no"; // force recent scores off
        }
        
        if (Common_Server.SERVER_ID == 4) {
            out.println("<!--");
            out.println("hdcp[0]=" + hdcp[0]);
            out.println("hdcp[1]=" + hdcp[1]);
            out.println("hdcp[2]=" + hdcp[2]);
            out.println("hdcp[3]=" + hdcp[3]);
            out.println("hdcp[4]=" + hdcp[4]);
            out.println("hdcp[5]=" + hdcp[5]);
            out.println("-->");
        }
    }
    
    out.println("</table><br><br>");
    
    
    // DISPLAY THE LAST 20 SCORE POSTINGS FOR THIS USER
    // DISABLED FOR NOW
    if (inc20.equals("yes")) {
        
        int s = 0;
        
        //results = Common_ghin.getRecentPostings(user, club, clubNum, clubAssocNum, out, con);

        if (isGHIN) {

            results = Common_ghin.getRecentPostings(user, club, clubNum, clubAssocNum, out, con);

        } else if (isGolfNet) {

            results = Common_golfnet.getRecentPostings(user, club, out, con);

        }

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

              if (parts[2].indexOf("T") > 0) {
                  String date_part[] = new String[1];
                  date_part = parts[2].split("T");
                  out.print("<tr><td align=center>" + date_part[0] + "</td>");
              } else {
                  out.print("<tr><td align=center>" + parts[2] + "</td>");
              }

              out.print("<td align=center>" + ( (s == 0) ? parts[0] : s ) + "</td>");
              out.print("<td align=center>" + parts[1] + "</td></tr>");
          }
        }
        
        out.println("</table>");
        
    } // end if include last 20 score postings
    
    buildBackButton(out);
    /*
    // provide back button
    out.println("<br>");
    out.println("<center>");
    out.println("<form method=\"get\" action=\"Proshop_handicaps\">");
    out.println("<input type=hidden name=todo value=view>");
    out.println("<input type=\"submit\" value=\" Back \" style=\"background:#8B8970\">");
    out.println("</center>");
    */
 }
 
 
 private void doViewBulkScores(HttpServletRequest req, Connection con, PrintWriter out) {
 
    String name = "";
    String type = "";
    String date = "";
    double hdcp = 0;
     
    out.println("<br><br><table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\" width=650>");
    out.println("<tr><td><font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
    out.println("<p>The dates and handicap indexes shown below are from the most recent data we've received from " +
            "your handicap provider.&nbsp; This is not a real-time lookup.&nbsp; A date of 'N/A' indicates we " +
            "have not downloaded any posted scores for that member.&nbsp; An index of '-99' indicates we " +
            "have not downloaded any handicap data for that member.</p>");
    out.println("</font></td></tr></table>");
    
    out.println("<br>");
    
    //out.println("<table align=center>");
    out.println("<table width=500 align=center cellspacing=0 cellpadding=5 bgcolor=#F5F5DC border=1>"); // style=\"border: 1px solid #336633\"
    out.println("<tr bgcolor=#336633><td align=center colspan=4><font size=4 color=white><b>Member Handicap Listing</b></font></td></tr>");
    out.println("<tr>");
    out.println("<td align=center><b>Member</b></td>");
    out.println("<td align=center><b>Date</b></td><td align=center><b>USGA Index</b></td></tr>");
    
    try {
        // g_hancap   REPLACE(m.ghin, '-', '')
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("" +
                "SELECT name, g_hancap, playDate, hdcpNum " +
                "FROM ( " +
                    "SELECT CONCAT(m.name_last, ', ', m.name_mi, ' ', m.name_first) AS name, " +
                        "m.g_hancap, sp.hdcpNum, DATE_FORMAT(sp.date, '%c/%e/%Y') AS playDate " +
                    "FROM member2b m " +
                    "LEFT OUTER JOIN score_postings sp ON sp.hdcpNum = CONVERT(CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED), CHAR) " +
                    "ORDER BY m.name_last, m.name_mi, m.name_first, sp.date DESC " +
                ") AS temp " + 
                "GROUP BY hdcpNum " + 
                "ORDER BY name;");
        
        while (rs.next()) {

            name = rs.getString("name");
            date = rs.getString("playDate");
            hdcp = rs.getDouble("g_hancap");
            
            out.println("" +
                    "<tr>" +
                        "<td nowrap>" + name + "</td>" +
                        "<td align=center>" + ((date == null) ? "N/A" : date) + "</td>" +
                        "<td align=center>" + hdcp + "</td>" +
                    "</tr>");
        }
    
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading course and tee information.", exc.getMessage(), out, false);
    }
    
    out.println("</table>");
    
    buildBackButton(out);
    /*
    out.println("<br>");
    out.println("<center>");
    out.println("<form method=\"get\" action=\"Proshop_handicaps\">");
    out.println("<input type=hidden name=todo value=view>");
    out.println("<input type=\"submit\" value=\" Back \" style=\"background:#8B8970\">");
    out.println("</center>");
    */
 }
 
 
 private void buildBackButton(PrintWriter out) {    
     
    // provide back button
    out.println("<br>");
    out.println("<center>");
    out.println("<form method=\"get\" action=\"Proshop_handicaps\">");
    out.println("<input type=hidden name=todo value=view>");
    out.println("<input type=\"submit\" value=\" Back \" style=\"background:#8B8970\">");
    out.println("</center>");
    
 }
 
} // end servlet public class
