/***************************************************************************************
 *   Member_handicaps: This servlet will allow the members to post scores and view their hdcp.
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
 *        1/07/14   Fixed issue with GolfNet indexes not being divided by 10 prior to being used in course handicap calculations.
 *       10/17/13   Remove dashes for mpccpb and other minor changes in wording for all clubs
 *        8/02/13   Display 'USGA Handicap Index' instead of 'USGA handicap' for all clubs.  This is the proper term.
 *        6/03/13   Add formatiing to the handicap number when displayed in Peer Review report
 *        5/16/13   GolfNet fixes for posting scores
 *        4/19/13   Monterey Peninsula CC (mpccpb) - Added custom to only display Men's or Ladies' tees depending on the gender of the member (case 2255).
 *        4/02/13   Long Cove Club (longcove) - Display "USGA index" instead of "USGA handicap" on view score postings page.
 *        3/21/13   List of tees will now be sorted by a sort_by value first, then tee_id.
 *        1/17/13   Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *        1/03/13   Updates for GolfNet
 *       12/03/12   Updated SQL hdcpNum matching to use new normalized score_postings.hdcpNum
 *       10/02/12   Updated getScoreToPost so that only courses which have tees configured are selectable.
 *        9/06/12   Commented out Common_golfnet method calls until that class is ready for use.
 *        9/06/12   Updated outputTopNav calls to also pass the HttpServletRequest object.
 *        9/05/12   Fixed issue with how course handicaps were getting rounded. Since the handicaps are negative, -.5 values were getting rounded "up", where they needed to round "down" (e.g. -28.5 rounded to -28 instead of -29).
 *        8/22/12   Fixed problem with members looking up other members handicaps & recent postings
 *        8/16/12   Fixed up sql statement to better match ghin numbers between the score_postings and member2b tables (viewSavedScores)
 *        8/14/12   castlepines - Added skip 9 hole round custom for peer reivew report (viewSavedScores)
 *        8/06/12   Improved error handling for posting scores if tees are not defined.
 *        7/17/12   Updated queries to use the new nopost1-5 flags in teepast2
 *        7/11/12   Updates for new GHIN interface
 *        4/20/12   Race Brook CC (racebrook) - Allow members to post tournament scores (case 2146).
 *        3/08/12   Add class=handicaps_form to the form tags so forms center in Safari browsers.
 *        2/02/12   Add new skin changes for center alignment of buttons and calendar with safari browser.
 *        2/01/12   Add new skin changes to servlet.
 *        8/02/11   Blackhawk CC (blackhawk) - Display "USGA index" instead of "USGA handicap" on view score postings page.
 *       12/10/10   Access to handicap index and handicap breakdown by tee box reports for other members is now a configurable option in club setup (case 1765).
 *        9/24/10   Access to peer review reports for other members is now a configurable option in club setup
 *        7/22/10   Change the background color of these pages from white to the normal member color.
 *                  Also, add some instructions and notes, as well as some Return buttons.
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

// foretees imports
import com.foretees.common.nameLists;
import com.foretees.common.Utilities;


public class Member_handicaps extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
 
 //*****************************************************
 // Process the get calls and display the proper forms
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;

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

    String club = (String)session.getAttribute("club");               // get club name
    String user = (String)session.getAttribute("user");               // get user name
    String caller = (String)session.getAttribute("caller");
    
    boolean new_skin = ((String)session.getAttribute("new_skin")).equals("1");
    int sess_activity_id = (Integer)session.getAttribute("activity_id");
    
    Connection con = SystemUtils.getCon(session);                      // get DB connection
    String clubName = SystemUtils.getClubName(con);            // get the full name of this club

    if (con == null) {

        if (!new_skin) {
            out.println(SystemUtils.HeadTitle("DB Connection Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
            out.println("<BR><BR><H3>Database Connection Error</H3>");
            out.println("<BR><BR>Unable to connect to the Database.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact your club manager.");
            out.println("<BR><BR>");
            out.println("<a href=\"Member_announce\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
        }
        else {
            errorPageTop(out, req, session, con, "DB Connection Error", "Database Error");
            dbConnError(out);
            Common_skin.outputPageEnd(club, sess_activity_id, out, req);        
            
        } // else (if !new_skin)
        out.close();
        return;
    }

    String clubNum = "";
    String clubAssocNum = "";
    String hdcpSystem = "";
    String gn21_sourceClubId = "";
    int allowMemPost = 0;
    int in_season = 0;
    
    if (club.equals( "" )) {

        if (!new_skin) {
            invalidUser(out, new_skin, club, sess_activity_id, req);            // Error - reject
        }
        else {
            errorPageTop(out, req, session, con, "Access Error - Redirect", "Invalid User");
            invalidUser(out, new_skin, club, sess_activity_id, req);
        } // else (if !new_skin)
        
        return;
    }
    
    // DONE VALIDATING THIS REQUEST - WE'RE READY TO GO
    
    
    // GET REQUEST VARIABLES WE WOULD NEED TO ACT ON
    String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");
    String hdcp = (req.getParameter("hdcp") == null) ? "" : req.getParameter("hdcp");


    if (!new_skin) {
        out.println("<html><head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
        out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
        out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
        out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
        out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
        out.println("</head>");
        out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\">");

        SystemUtils.getMemberSubMenu(req, out, caller);
    } 
    else {
        Common_skin.outputHeader(club, sess_activity_id, "", false, out, req);
        out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web%20utilities/foretees.js\"></script>");
        //out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
        //out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
        out.println("</head>");

        Common_skin.outputBody(club, sess_activity_id, out, req);
        Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
        Common_skin.outputBanner(club, sess_activity_id, clubName, (String)session.getAttribute("zipcode"), out, req);
        Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
        Common_skin.outputPageStart(club, sess_activity_id, out, req);
        Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Handicaps", req);
        Common_skin.outputLogo(club, sess_activity_id, out, req);
    
    } // else (if !new_skin)
    
    // MAKE SURE THIS CLUB HAS A HDCP SYSTEM ENABLED AND MEMBER'S ARE ABLE TO ACCESS
    
    
    // GET CLUB INFO
    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("" +
                "SELECT " +
                    "hdcpSystem, allowMemPost, gn21_sourceClubId, " +
                    "IF(hdcpStartDate <= now() && hdcpEndDate >= now(),1,0) AS in_season " +
                "FROM club5;");

        if ( rs.next() ) {
            hdcpSystem = rs.getString("hdcpSystem");
            allowMemPost = rs.getInt("allowMemPost");
            in_season = rs.getInt("in_season");
            gn21_sourceClubId = rs.getString("gn21_sourceClubId");
        }
    
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
        return;

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}
    }
    
    
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

        if ( rs.next() ) {

            clubNum = rs.getString("club_num");
            clubAssocNum = rs.getString("assoc_num");

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
    
    
    // MAKE SURE CLUB IS SETUP TO USE HDCP FEATURE
    if (!new_skin) {
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
            if (!updateHdcpNum(user, hdcp, new_skin, con, out)) return; 
        }


        // MAKE SURE USER HAS A HDCP # IN THEIR MEMBER DATA
        if (!checkHdcpNum(user, todo, new_skin, con, out)) return;


        // MAKE SURE CLUB POLICY ALLOWS MEMBERS TO POST SCORES
        if (todo.equals("post") && allowMemPost == 0) {

                out.println("<br><br><p align=center><b><i>Your club policy does not to allow members to post scores.</i></b></p>");
                return;
        }


        // HANDLE POST SCORE REQUEST
        if (todo.equals("post")) getScoreToPost(req, club, new_skin, sess_activity_id, con, out);


        // HANDLE VIEW HDCP/SCORES REQUEST
        if (todo.equals("view")) getViewScores(user, new_skin, req, con, out);

        out.println("</body></html>");       
        
    } // if (!new_skin)
    else {
        
        if (hdcpSystem.equals("") || hdcpSystem.equalsIgnoreCase("other")) {

            if (club.equals("internationalcc")) {

                out.println("  <br /><p class=\"handicaps_ctr\"><a href=\"http://www.golfnetonline.net/clubentry4.asp?CourseNum=10980&StateId=3\" target=\"_VSGA\">");
                out.println("  Click here to access the Virginia State Golf Association web site.</a></p>");
            } else {

                out.println("  <br /><p class=\"handicaps_ctr\"><b><i>Your club does not have a handicap system that allows online access.</i></b></p>");
            }
            
            Common_skin.outputPageEnd(club, sess_activity_id, out, req);
            out.close();
            return;

        } else if (hdcpSystem.equalsIgnoreCase("GHIN") && (clubNum.equals("") || clubAssocNum.equals(""))) {

            out.println("<br /><p class=\"handicaps_ctr\"><b><i>Your club has not configured the online access to your handicap system.</i></b></p>");
            Common_skin.outputPageEnd(club, sess_activity_id, out, req);
            out.close();
            return;

        } else if (hdcpSystem.equalsIgnoreCase(("GN21")) && gn21_sourceClubId.equals("")) {

            out.println("<br /><p class=\"handicaps_ctr\"><b><i>Your club has not configured the online access to your handicap system.</i></b></p>");
            Common_skin.outputPageEnd(club, sess_activity_id, out, req);
            out.close();
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
            if (!updateHdcpNum(user, hdcp, new_skin, con, out)) return; 
        }


        // MAKE SURE USER HAS A HDCP # IN THEIR MEMBER DATA
        if (!checkHdcpNum(user, todo, new_skin, con, out)) return;

    
        // MAKE SURE CLUB POLICY ALLOWS MEMBERS TO POST SCORES
        if (todo.equals("post") && allowMemPost == 0 && Common_Server.SERVER_ID != 4) { //  && !user.equals("24561") - testing for lakewoodcc
        
            out.println("<br /><p class=\"handicaps_ctr\"><b><i>Your club policy does not to allow members to post scores.</i></b></p>");
            Common_skin.outputPageEnd(club, sess_activity_id, out, req);
            out.close();
            return;

        }
       
        // HANDLE POST SCORE REQUEST
        if (todo.equals("post")) getScoreToPost(req, club, new_skin, sess_activity_id, con, out);

        // HANDLE VIEW HDCP/SCORES REQUEST
        if (todo.equals("view")) getViewScores(user, new_skin, req, con, out);

        Common_skin.outputPageEnd(club, sess_activity_id, out, req);        
    } // else (if !new_skin)

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
    
    String club = (String)session.getAttribute("club");               // get club name
    String user = (String)session.getAttribute("user");               // get user name
    String caller = (String)session.getAttribute("caller");
    
    boolean new_skin = ((String)session.getAttribute("new_skin")).equals("1");
    int sess_activity_id = (Integer)session.getAttribute("activity_id");

    Connection con = SystemUtils.getCon(session);                      // get DB connection
    String clubName = SystemUtils.getClubName(con);            // get the full name of this club

    if (con == null) {

        if (!new_skin) {
            out.println(SystemUtils.HeadTitle("DB Connection Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
            out.println("<BR><BR><H3>Database Connection Error</H3>");
            out.println("<BR><BR>Unable to connect to the Database.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact your club manager.");
            out.println("<BR><BR>");
            out.println("<a href=\"Member_announce\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
        }
        else {
            errorPageTop(out, req, session, con, "DB Connection Error", "Database Error");
            dbConnError(out);
            Common_skin.outputPageEnd(club, sess_activity_id, out, req);        
            
        } // else (if !new_skin)
        
        out.close();
        return;
    }
    
    if (club.equals( "" )) {

        if (!new_skin) {
            invalidUser(out, new_skin, club, sess_activity_id, req);            // Error - reject
        }
        else {
            errorPageTop(out, req, session, con, "Access Error - Redirect", "Invalid User");
            invalidUser(out, new_skin, club, sess_activity_id, req);            // Error - reject
            Common_skin.outputPageEnd(club, sess_activity_id, out, req);        
            
        } // else (if !new_skin)
        return;
    }


    String hdcpSystem = Common_handicaps.getClubHdcpOption(club, con);

    // get members club/assoc info (ghin only)
    String clubNum = "";
    String clubAssocNum = "";

    if (hdcpSystem.equalsIgnoreCase("GHIN")) {

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

    } else if (hdcpSystem.equalsIgnoreCase("GN21")) {

        

    }

    // DONE VALIDATING THIS REQUEST - WE'RE READY TO GO
    
    
    // GET REQUEST VARIABLES WE WOULD NEED TO ACT ON
    String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");
    String username = (req.getParameter("user") == null) ? "" : req.getParameter("user");
    
    if (!new_skin) {
        out.println("<html><head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
        out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
        out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
        //out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
        //out.println("<script language=\"javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
        out.println("</head>");
        out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\">");

        SystemUtils.getMemberSubMenu(req, out, caller);
    }
    else {
        Common_skin.outputHeader(club, sess_activity_id, "", false, out, req);
        out.println("</head>");
        Common_skin.outputBody(club, sess_activity_id, out, req);
        Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
        Common_skin.outputBanner(club, sess_activity_id, clubName, (String)session.getAttribute("zipcode"), out, req);
        Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
        Common_skin.outputPageStart(club, sess_activity_id, out, req);
        if (todo.equals("peer")) {
            Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Handicaps Peer Review", req);
        }
        else {
            Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Handicaps", req);
        }
        Common_skin.outputLogo(club, sess_activity_id, out, req);
        
    } // else (if !new_skin)
    
    if (todo.equals("post")) doPostScore(user, club, clubNum, clubAssocNum, hdcpSystem, new_skin, req, con, out);
    
    if (todo.equals("view")) {

        if (username.equals("")) { // username is empty (was null)

            // perform look up on someone else
            if (req.getParameter("name") != null && !req.getParameter("name").equals("")) {

                username = SystemUtils.getUsernameFromFullName(req.getParameter("name"), con);
                doViewScores(username, club, clubNum, clubAssocNum, new_skin, req, con, out, false);

            } else {
                selectMember(req, club, new_skin, user, sess_activity_id, con, out);
            }
        } else {
            // perform lookup on self
            doViewScores(user, club, clubNum, clubAssocNum, new_skin, req, con, out, true);
        }
    }

    if (todo.equals("peer")) {
        if (username.equals("")) {
            getViewPeerScores(req, club, new_skin, user, sess_activity_id, con, out);            
        } else {
            viewSavedScores(req, club, username, new_skin, con, out);
        }
    }
    
    if (!new_skin) {
        out.println("</body></html>");
    }
    else {
        Common_skin.outputPageEnd(club, sess_activity_id, out, req);                
    }
    out.close();
    
 } // end of doPost routine
 
 
 // *********************************************************
 // Check for hncp # for user, if not found display form
 // to allow user to add it to their member data
 // Returns true if found, false if not found
 // *********************************************************
 
 private boolean checkHdcpNum(String user, String todo, boolean new_skin, Connection con, PrintWriter out) {

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
    
        if (!new_skin) {
            out.println("<br><p align=center><font size=4>Please Provide a Handicap Number</font></p>");
            out.println("<p align=center>If you are unaware of your assigned handicap number, please contact your golf professional for assistance.</p>");

            out.println("<center>");
            out.println("<form action=Member_handicaps method=GET>");
            out.println("<input type=hidden name=todo value=\"" + todo + "\">");
            out.println("Handicap Number: <input type=text name=hdcp value=\"\" size=16 maxlength=16>");
            out.println("<br><br><input type=submit value=\"  Save  \">");
            out.println("</form>");
            out.println("</center>");
        }
        else {
            out.println("  <h3 class=\"handicaps_ctr\">Please Provide a Handicap Number</h3>");
            out.println("  <p class=\"handicaps_ctr\">If you are unaware of your assigned handicap number, please contact your golf professional for assistance.</p>");

            out.println("  <form action=Member_handicaps method=GET>");
            out.println("    <input type=hidden name=todo value=\"" + todo + "\" />");
            out.println("    Handicap Number: <input type=text name=hdcp value=\"\" size=16 maxlength=16>");
            out.println("    <br /><br /><input class=\"standard_button\" type=submit value=\"  Save  \">");
            out.println("    </form>");
            
        } // else (if !new_skin)
        return false;
    }
    
    return true;
 }
 
 
 // *********************************************************
 // Save hdcp number in members data
 // Returns true if a record was updated, false if not
 // *********************************************************
 
 private boolean updateHdcpNum(String user, String hdcp, boolean new_skin, Connection con, PrintWriter out) {

    PreparedStatement pstmt = null;

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

    } finally {

        try { pstmt.close(); }
        catch (Exception ignore) {}
    }
    
    if (x == 0) {
    
        if (!new_skin) {
            out.println("<p>We were unable to save your handicap number.  Please try again.  If problem persists, please contact your golf shop.</p>");
        }
        else {
            out.println("  <p class=\"handicaps_ctr\">We were unable to save your handicap number.  Please try again.  If problem persists, please contact your golf shop.</p>");
        }
        return false;
    }
    
    return true;
 }
 
 
 // *********************************************************
 // Handles from posted from getScoreToPost method and
 // calls Common_ghin.postScore and displays result
 // *********************************************************
 
 private void doPostScore(String user, String club, String clubNum, String clubAssocNum, String hdcpSystem, boolean new_skin, HttpServletRequest req, Connection con, PrintWriter out) {
 
    long date = 0;
    
    int course_id = 0;
    int tee_id = 0;
    int holes = 0;
    int score = 0;
    int escScore = 0;
    int teecurr_id = 0;

    String courseName = "";

    String sdate = (req.getParameter("sdate") == null) ? "" : req.getParameter("sdate");
    String type = (req.getParameter("type") == null) ? "" : req.getParameter("type");
    String stid = (req.getParameter("tee_id") == null) ? "" : req.getParameter("tee_id");
    String sholes = (req.getParameter("holes") == null) ? "" : req.getParameter("holes");
    String sscore = (req.getParameter("score") == null) ? "" : req.getParameter("score");
    String sescScore = (req.getParameter("escScore") == null) ? "" : req.getParameter("escScore");
    String course = (req.getParameter("course") == null) ? "0" : req.getParameter("course");
       
    try {
        
        tee_id = Integer.parseInt(stid);
        holes = Integer.parseInt(sholes);
        date = Integer.parseInt(sdate);
        score = Integer.parseInt(sscore);
        course_id = Integer.parseInt(course);
        escScore = Integer.parseInt(sescScore);

    } catch (NumberFormatException exc) { 
    
        // check variables and report error
    
    
    }


    // IF GOLFNET THEN LETS GET THE ID FOR THIS TEE TIME
    if (hdcpSystem.equalsIgnoreCase("GN21")) {

        
        if (escScore == 0) {

            out.println("<h2 align=\"center\">Unable To Post Score</h2><br>");
            out.println("<p align=center><b>You must provide an ESC (Equitable Stroke Control) score for your round of golf.</b><br><br>Please contact your golf professional if your require assistance determining your ESC score.</p>");
            out.println("<p align=center><br><br><center><form><input type=button onclick=\"window.history.go(-1)\" value=\" Go Back \"></form></center></p>");
            return;
        }

        ResultSet rs = null;
        PreparedStatement pstmt = null;

        // get the courseName from the course_id passed in
        try {

            pstmt = con.prepareStatement("SELECT courseName FROM clubparm2 WHERE clubparm_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, course_id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                courseName = rs.getString("courseName");
            }

        } catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error getting courses.", exc.getMessage(), out, false);
            return;

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}
        }

        try {

            String table = (Utilities.getDate(con) == date) ? "teecurr2" : "teepast2";

            pstmt = con.prepareStatement("" +
                    "SELECT teecurr_id " +
                    "FROM " + table + " " +
                    "WHERE " +
                        "date = ? AND " +
                        "courseName = ? AND ( " +
                            "(username1 = ? AND show1 = 1 && nopost1 = 0) OR " +
                            "(username2 = ? AND show2 = 1 && nopost2 = 0) OR " +
                            "(username3 = ? AND show3 = 1 && nopost3 = 0) OR " +
                            "(username4 = ? AND show4 = 1 && nopost4 = 0) OR " +
                            "(username5 = ? AND show5 = 1 && nopost5 = 0) " +
                        ")");
/*
            out.println("<!-- SELECT teecurr_id " +
                    "FROM " + table + " " +
                    "WHERE " +
                        "date = \"" + date + "\" AND " +
                        "courseName = \"" + courseName + "\" AND ( " +
                            "(username1 = \"" + user + "\" AND show1 = 1 && nopost1 = 0) OR " +
                            "(username2 = \"" + user + "\" AND show2 = 1 && nopost2 = 0) OR " +
                            "(username3 = \"" + user + "\" AND show3 = 1 && nopost3 = 0) OR " +
                            "(username4 = \"" + user + "\" AND show4 = 1 && nopost4 = 0) OR " +
                            "(username5 = \"" + user + "\" AND show5 = 1 && nopost5 = 0) " +
                        ") -->");
*/
            pstmt.clearParameters();
            pstmt.setLong(1, date);
            pstmt.setString(2, courseName);
            pstmt.setString(3, user);
            pstmt.setString(4, user);
            pstmt.setString(5, user);
            pstmt.setString(6, user);
            pstmt.setString(7, user);
            rs = pstmt.executeQuery();

            if (rs.next()) teecurr_id = rs.getInt("teecurr_id");

        } catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error looking up member round.", exc.getMessage(), out, false);
            return;
        
        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

        if (teecurr_id == 0 && Common_Server.SERVER_ID != 4) {

            out.println("<h2 align=\"center\">Unable To Post Score</h2><br>");
            out.println("<p align=center><b>A round was not found in the system for you on the selected date and course.</b><br><br>Please select a date and course for which you played a round of golf.&nbsp; Other causes could be that the staff at your club may not have checked you in for the round of golf, or that your round was marked as a non-posting round.</p>");
            out.println("<p align=center><br><br><center><form><input class=\"standard_button\" type=\"button\" onclick=\"window.history.go(-1)\" value=\" Go Back \"></form></center></p>");
            return;
        }

    } // end golfnet processing


    //
    // MAKE THE CALL AND HANDLE THE RESPONSE DEPENDING ON THE HANDICAP SYSTEM BEING USED
    //

    String msg[] = new String[2];

    if (hdcpSystem.equalsIgnoreCase("GHIN")) {

        // post the score and get the result
        msg = Common_ghin.postScore(user, club, date, tee_id, score, type, holes, clubNum, clubAssocNum, out, con);

        // handle the ghin responses
        if (msg[0] == null || !msg[0].equals("1")) {

            out.println("<p class=\"handicaps_ctr\"><b>Your score was not posted.&nbsp; Please try again later.</b><br /><br />Message: \"" + msg[1] + "\"</p>");
            
        } else {

            out.println("<p class=\"handicaps_ctr\"><b>Your score was successfully posted.</b><br /><!--Message: " + msg[1] + "--></p>");
            
        }
        
    } else if (hdcpSystem.equalsIgnoreCase("GN21")) {

        // post the score and get the result
        msg = Common_golfnet.postScore(user, club, date, tee_id, score, escScore, type, holes, teecurr_id, out, con);

        // handle the possible golfnet responses
        if (msg[0] == null) { // this shouldn't really occur more for sanity

            out.println("<p class=\"handicaps_ctr\"><b>A fatal error occured while attempting to post your score. <i>Your score was not posted</i>.</b><br /></p>");

        } else if (msg[0].equals("1")) {

            out.println("<p class=\"handicaps_ctr\"><b>Your score was successfully posted.</b><br /><!-- Response: " + msg[1] + " --></p>");

        } else if (msg[0].equals("2")) {

            out.println("<p class=\"handicaps_ctr\"><b>Your score was not posted.</b><br /><br />Message: \"" + msg[1] + "\"</p>");

        } else {

            // probably equals a "0" which is a failure but lets capture all other responses
            out.println("<p class=\"handicaps_ctr\"><b>Your score was <i>not</i> posted.</b><br /><br />Reason given was: \"" + msg[1] + "\"</p>");

        }
    
    }

    outReturnBtn(out, "post");
        
 }
 
 
 // *********************************************************
 // Display form and gather information to submit score to
 // handicap system.  
 // *********************************************************
 
 private void getScoreToPost(HttpServletRequest req, String club, boolean new_skin, int sess_activity_id, Connection con, PrintWriter out) {

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
    
    Calendar end_cal_date = new GregorianCalendar();
    Calendar start_cal_date = new GregorianCalendar();
    int end_year = end_cal_date.get(Calendar.YEAR);
    int end_month = end_cal_date.get(Calendar.MONTH) + 1; // month is zero based
    int end_day = end_cal_date.get(Calendar.DAY_OF_MONTH);

    // reset date parts
    start_cal_date.add(Calendar.YEAR, -1); // subtract a year
    int start_year = start_cal_date.get(Calendar.YEAR);
    int start_month = start_cal_date.get(Calendar.MONTH) + 1; // month is zero based
    int start_day = end_cal_date.get(Calendar.DAY_OF_MONTH);

    String start_date = start_month+"/"+start_day+"/"+start_year;
    String end_date = end_month+"/"+end_day+"/"+end_year;

    String hdcpSystem = Common_handicaps.getClubHdcpOption(club, con);

    try {

        course_id = Integer.parseInt(scid);
        mm = Integer.parseInt(smm);
        yy = Integer.parseInt(syy);
        dd = Integer.parseInt(sdd);
    }
    catch (NumberFormatException e) { }
    
    
    if (!new_skin) {
        out.println("<CENTER><BR><BR><p align=center><font size=5>Post a Score</font></p>");

        out.println("<p align=center><BR><b>NOTE: Using the calendar below, select the <b>date that the round was played</b>.</p>");

        out.println("<table align=center border=0 height=165>\n<tr valign=top>\n<td>");   // was 190 !!!

        out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

        out.println("</td>\n</tr>\n</table>");

        out.println("<form action=Member_handicaps method=POST name=frmPostScore id=frmPostScore>");
        out.println("<input type=hidden name=todo value=\"post\">");
        out.println("<input type=hidden name=sdate value=\"" + sdate + "\">");
        out.println("<input type=hidden name=course_id value=\"\">");

        out.println("<table align=center>");

        out.println("<tr><td align=right><b>Date Played:&nbsp;</b></td><td><input type=text value=\"" + cal_box_0 + "\" name=cal_box_0 id=cal_box_0 size=10 onfocus=\"this.blur()\"></td></tr>"); // ((dd == 0) ? "" : yy + "-" + mm + "-" + dd)
    } // if (!new_skin)
    else {
        out.println("  <div class=\"main_instuctions\"><h2 class=\"handicaps_ctr\">Post a Score</h2></div>");

        //out.println("  <p class=\"handicaps_ctr\">NOTE: Using the calendar below, select the <b>date that the round was played</b>.</p>");

        //out.println("  <table class=handicaps_cal_orig>\n    <tr>\n    <td>");   // was 190 !!!

        //out.println("    <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

        //out.println("    </td></tr>");
        //out.println("  </table>");

        out.println("  <form action=Member_handicaps method=POST name=frmPostScore id=frmPostScore>");
        out.println("    <input type=hidden name=todo value=\"post\" />");
        out.println("    <input type=hidden name=sdate value=\"" + sdate + "\" />");
        out.println("    <input type=hidden name=course_id value=\"\" />");

        out.println("  <table class=\"handicaps_info\">");

        out.println("    <tr><td class=\"field_name\">Date Played:</td>"); // ((dd == 0) ? "" : yy + "-" + mm + "-" + dd)
        out.println("      <td><input type=text class=\"ft_date_picker\" data-ftstartdate=\""+start_date+"\" data-ftenddate=\""+end_date+"\" value=\"\" name=\"date_select\" size=10 /></td>"); 
        out.println("    </tr>");
        
    } // else (if !new_skin)
    
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

        if (!new_skin) {
            out.println("<tr><td align=right><b>Course Played:&nbsp;</b></td>");
            out.println("<td><select name=course size=1 onchange=\"selectTees(this.options[this.selectedIndex].value)\">");
        }
        else {
            out.println("    <tr><td class=\"field_name\">Course Played:</td>");
            out.println("      <td><select name=course size=1 onchange=\"selectTees(this.options[this.selectedIndex].value)\">");
            
        } // else (if !new_skin)
        try {

            Statement stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT clubparm_id, courseName FROM clubparm2 WHERE first_hr != 0");

            int i = 0;
            while (rs.next()) {
                
                if (Utilities.checkTees(rs.getInt("clubparm_id"), con)) {

                    out.print("<option value=\"" + rs.getInt(1) + "\"");
                    if (course_id == rs.getInt(1)) out.print(" selected");
                    out.println(">" + rs.getString(2) + "</option>");

                    if (i == 0 && course_id == 0) course_id = rs.getInt(1); // applies default to make sure course & tee match
                    i++;
                }
            }

        } catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error getting courses.", exc.getMessage(), out, false);
            return;
        }

        out.println("</select></td>");
        out.println("</tr>");
        
    } else {

        // get the course from clubparm2
        Statement stmt = null;
        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT clubparm_id, courseName FROM clubparm2 WHERE first_hr != 0");

            if (rs.next()) course_id = rs.getInt(1);

        } catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error getting courses.", exc.getMessage(), out, false);
            return;

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}
        }

    }

    //out.println("<!-- course_id=" + course_id + " -->");
        
    if (!new_skin){

        // DISPLAY TEES FOR SELECTION
        out.println("<tr><td align=right><b>Tees Played:&nbsp;</b></td>");
        out.println("<td><select name=tee_id size=1>");
    }
    else {
        // DISPLAY TEES FOR SELECTION
        out.println("<tr><td class=\"field_name\">Tees Played:</td>");
        out.println("<td><select name=tee_id size=1>");
        
    } // else (if !new_skin)

    PreparedStatement pstmt = null;
    boolean tees_found = false;
    try {

        pstmt = con.prepareStatement("SELECT tee_id, tee_name FROM tees WHERE course_id = ? ORDER BY sort_by, tee_id");
        pstmt.clearParameters();
        pstmt.setInt(1, course_id);
        rs = pstmt.executeQuery();

        while (rs.next()) {

            out.println("<option value=\"" + rs.getInt(1) + "\">" + rs.getString(2) + "</option>");
            tees_found = true;
        }

    } catch (Exception exc) {

        if (!new_skin) {
           SystemUtils.buildDatabaseErrMsg("Error getting tees.", exc.toString(), out, false);
        }
        else {
           out.println("</select></td></tr></table>");
           SystemUtils.buildDatabaseErrMsg("Error getting tees.", exc.toString(), out, false);
           Common_skin.outputPageEnd(club, sess_activity_id, out, req);
           out.close();
            
        } // else (if !new_skin)
        return;

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}
    }
    
    out.println("</select></td>");
    out.println("</tr><tr>");


    // ENSURE TEES ARE DEFINED FOR COURSE
    if (!tees_found) {

        out.println("<tr><td colspan=\"2\" align=\"center\"><b>You cannot post a score for this course because<br>your club has not defined any tees for it.</b></td></tr></table>");
        return;
    }


    if (!new_skin) {

        //
        // OLD SKIN
        //
        
        // HOLES PLAYED
        out.println("<td align=right><b>Round Length:&nbsp;</b></td>");
        out.println("<td><select name=holes size=1>");
        out.println("<option value=1>18 Holes");
        out.println("<option value=2>Front 9 Holes");
        out.println("<option value=3>Back 9 Holes");
        out.println("</select></td>");

        out.println("</tr><tr>");
        
        // PLAY TYPE (members can only post Home scores for now)
        if (club.equals("johnsisland") || club.equals("wellesley") || club.equals("merion") || club.equals("racebrook")) { // allow johnsisland and wellesley members to post tournament scores (Case #1615)

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


        out.println("<BR><form action=Member_announce><input type=submit value=\"Home\" style=\"width: 75px; background:#8B8970\"></form>");
        out.println("</CENTER>");
    } // if (!new_skin)
    else {

        //
        // NEW SKIN
        //
        
        
        // HOLES PLAYED
        out.println("      <td class=\"field_name\">Round Length:</td>");
        out.println("      <td><select name=holes size=1>");
        out.println("        <option value=1>18 Holes</option>");
        out.println("        <option value=2>Front 9 Holes</option>");
        out.println("        <option value=3>Back 9 Holes</option>");
        out.println("      </select></td>");

        out.println("    </tr><tr>");


        if (hdcpSystem.equalsIgnoreCase("GHIN")) {

            // PLAY TYPE (members can only post Home scores for now)
            if (club.equals("johnsisland") || club.equals("wellesley") || club.equals("merion") || club.equals("racebrook")) { // allow johnsisland and wellesley members to post tournament scores (Case #1615)

                out.println("      <td class=\"field_name\">Play Type:</td>");
                out.println("      <td><select name=type size=1>");
                out.println("        <option value='H'>Home</option>");
                //out.println("<option value='A'>Away");
                out.println("        <option value='T'>Tournament</option>");
                //out.println("<option value='I'>Internet");
                out.println("      </select></td>");

                out.println("    </tr><tr>");

            } else {

                out.println("      <input type=hidden name=type value=H>");

            }

            // SCORE TO POST
            out.println("      <td class=\"field_name\">Score:</td>");
            out.println("      <td><input type=text name=score size=5 maxlength=3 /></td>");

        } else if (hdcpSystem.equalsIgnoreCase("GN21")) {

            out.println("      <td class=\"field_name\">Play Type:</td>");
            out.println("      <td><select name=type size=1>");
            out.println("        <option value='H'>Home</option>");
            //out.println("<option value='A'>Away");
            out.println("        <option value='T'>Tournament</option>");
            //out.println("<option value='I'>Internet");
            out.println("      </select></td>");

            out.println("    </tr><tr>");

            // SCORE TO POST
            out.println("      <td class=\"field_name\">Gross Score:</td>");
            out.println("      <td><input type=text name=score size=5 maxlength=3 /></td>");
            
            out.println("    </tr><tr>");
            out.println("      <td class=\"field_name\">ESC Score:</td>");
            out.println("      <td><input type=text name=escScore size=5 maxlength=3 /></td>");
            

        }

        out.println("    </tr><tr>");

        out.println("      <td colspan=2 class=\"score\"><br><input class=\"standard_button\" type=button value=\" Post Score \" onclick=\"submitForm()\" name=btnSubmit></td>");

        out.println("    </tr>");
        out.println("  </table>    <!-- handicaps_info -->");

        out.println("  </form>");
        out.println("");

    } // else (if !new_skin)
    
    out.println("<script type=\"text/javascript\">");
    
    if (!new_skin) {

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
         out.println("g_cal_ending_month[0] = " + end_month + ";");
         out.println("g_cal_ending_day[0] = " + end_day + ";");
         out.println("g_cal_ending_year[0] = " + end_year + ";");

         // set starting month/year for calendar
         out.println("g_cal_month[0] = " + ((mm == 0) ? end_month : mm) + ";");
         out.println("g_cal_year[0] = " + ((yy == 0) ? end_year : yy) + ";");

         // specificing beginning of calendar (30 days back)
         out.println("g_cal_beginning_month[0] = " + start_month + ";");
         out.println("g_cal_beginning_year[0] = " + start_year + ";");
         out.println("g_cal_beginning_day[0] = " + start_day + ";");

         // refine our function that's called when user clicks day on calendar
         out.println("function sd(pCal, pMonth, pDay, pYear) {");
         out.println("  f = document.getElementById(\"cal_box_\"+pCal);");
         out.println("  f.value = pMonth + \"-\" + pDay + \"-\" + pYear;");
         out.println("  var d = (pYear * 10000) + (pMonth * 100) + (pDay * 1);");
         out.println("  document.forms[\"frmPostScore\"].sdate.value = d;");
         out.println("}");
    }

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
     if (new_skin) {
         out.println(" if ($('input[name=\"date_select\"]').val() == '') {");
         out.println("  alert('Please select a date you wish to post a score for.');");
         out.println("  return;");
         out.println(" } else {");
         out.println("  var date = ftStringDateToDate($('input[name=\"date_select\"]').val());");
         out.println("  $('input[name=\"sdate\"]').val(date.format('yyyymmdd'));");
         out.println(" }");
     } else {
         out.println(" if (f.sdate.value == '') {");
         out.println("  alert('Please select a date you wish to post a score for.');");
         out.println("  return;");
         out.println(" }");
     }
    //out.println(" b = document.getElementById(\"btnSubmit\");");
    out.println(" f.btnSubmit.value='Please Wait';");
    out.println(" f.btnSubmit.disabled=true;");
    out.println(" f.submit();");
    out.println("}");
    
    out.println("</script>");
    
    if(!new_skin){
        out.println("<script type=\"text/javascript\">\ndoCalendar('0');\n</script>");
    }
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


 // *********************************************************
 // Gather request information for displaying current hdcp
 // and option to get the last 20 scores posted.
 // *********************************************************
 
 private void getViewScores(String user, boolean new_skin, HttpServletRequest req, Connection con, PrintWriter out) {

/*
    String bdate = "20061001";
    String edate = "20061101";  
    //Common_ghin.getPostedScoresForClub("Test", "56", "103", bdate, edate, con, out);
    Common_ghin.getPostedScores(out);
*/
    Statement stmt = null;
    ResultSet rs = null;

    int peer_review = 0;
    int hdcp_memview = 0;

    try {
        stmt = con.createStatement();

        rs = stmt.executeQuery("SELECT peer_review, hdcp_memview FROM club5");

        if (rs.next()) {
            peer_review = rs.getInt("peer_review");
            hdcp_memview = rs.getInt("hdcp_memview");
        }

        stmt.close();
        
    } catch (Exception exc) {

    }

    if (!new_skin) {
        out.println("<br><p align=center><font size=5>Handicap Inquiry</font></p>");

        out.println("<br><p align=center><b>Get Your Current Handicap Data:</b></p>");

        out.println("<center>");
        out.println("<form method=POST action=Member_handicaps name=frmHdcpInquiry id=frmHdcpInquiry>");
        out.println("<input type=hidden name=todo value=view>");
        out.println("<input type=hidden name=user value=\"" + user + "\">");

        out.println("<input type=checkbox name=inc20 value=yes> Include last 20 postings");

        out.println("<br><br><input type=submit value=\" Retrieve \" onclick=\"submitForm()\" name=btnSubmit>");
        out.println("</form>");

        if (hdcp_memview == 1) {

            out.println("<br><HR width=\"20%\">");

            out.println("<br><p align=center><b>Get Current Handicap Data For Other Members:</b></p>");

            out.println("<form method=POST action=Member_handicaps name=frmHdcpInquiry id=frmHdcpInquiry>");
            out.println("<input type=hidden name=todo value=view>");
            out.println("<input type=submit value=\" Select Member \" onclick=\"submitForm()\" name=btnSubmit>");
            out.println("</form>");
        }

        out.println("<br><HR width=\"20%\">");

        /*
         * THIS BUTTON IS CONFIGURABLE IF A CLUB DOES NOT WANT TO
         * ALLOW MEMBERS TO DO PEER REVIEW REPORTS ON OTHER MEMBERS
         */
        if (peer_review == 1) {

            out.println("<br><p align=center><b>View 'Rounds Played vs Scores Posted' Report<BR>For You or Other Members:</b></p>");

            out.println("<p>");
            out.println("<form method=POST action=Member_handicaps>");
            out.println("<input type=hidden name=todo value=peer>");
            out.println("<input type=submit value=\"Peer Review Reports\" name=btnSubmit>");
            out.println("</form>");
            out.println("</p>");

        } else {

            out.println("<br><p align=center><b>View Your 'Rounds Played vs Scores Posted' Report</b></p>");

            out.println("<p>");
            out.println("<form method=POST action=Member_handicaps>");
            out.println("<input type=hidden name=todo value=peer>");
            out.println("<input type=hidden name=user value=\"" + user + "\">");
            out.println("<input type=submit value=\"View Report\" name=btnSubmit>");
            out.println("</form>");
            out.println("</p>");

        }

        out.println("<BR><BR><form action=Member_announce><input type=submit value=\"Home\" style=\"width: 75px; background:#8B8970\"></form>");

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
        
    } // if (!new_skin)
    else {
        out.println("  <h2 class=\"handicaps_ctr\">Handicap Inquiry</h2>");

        out.println("  <br /><br /><p class=\"handicaps_ctr\"><b>Get Your Current Handicap Data:</b></p>");

        out.println("  <p class=\"handicaps_ctr\">");
        out.println("    <form class=\"handicaps_form\" method=POST action=Member_handicaps name=frmHdcpInquiry id=frmHdcpInquiry1>");
        out.println("      <input type=hidden name=todo value=\"view\" />");
        out.println("      <input type=hidden name=user value=\"" + user + "\" />");

        out.println("      <input type=checkbox name=inc20 id=inc20 value=\"yes\" /><label for=inc20> Include last 20 postings</label><br />");

        out.println("      <br /><input class=\"standard_button\" type=submit value=\" Retrieve \" onclick=\"submitForm(1)\" name=btnSubmit />");
        out.println("    </form></p>");

        if (hdcp_memview == 1 || Common_Server.SERVER_ID == 4) {

            out.println("  <br /><p class=\"handicaps_ctr\"><hr class=\"handicaps2\" /></p>");

            out.println("  <br /><p class=\"handicaps_ctr\"><b>Get Current Handicap Data For Other Members:</b></p>");

            out.println("  <p class=\"handicaps_ctr\">");
            out.println("    <form class=\"handicaps_form\" method=POST action=Member_handicaps name=frmHdcpInquiry id=frmHdcpInquiry2>");
            out.println("      <input type=hidden name=todo value=\"view\" />");
            out.println("      <input type=hidden name=letter value=\"A\" />");         // default member list to display.
            out.println("      <input class=\"standard_button\" type=submit value=\" Select Member \" onclick=\"submitForm(2)\" name=btnSubmit />");
            out.println("    </form>");
            out.println("  </p>");
        }

        out.println("  <br /><hr class=\"handicaps2\" />");

        /*
         * THIS BUTTON IS CONFIGURABLE IF A CLUB DOES NOT WANT TO
         * ALLOW MEMBERS TO DO PEER REVIEW REPORTS ON OTHER MEMBERS
         */
        if (peer_review == 1 || Common_Server.SERVER_ID == 4) {

            out.println("  <br /><p class=\"handicaps_ctr\"><b>View 'Rounds Played vs Scores Posted' Report<br />For You or Other Members:</b></p>");

            out.println("  <p class=\"handicaps_ctr\">");
            out.println("    <form class=\"handicaps_form\" method=POST action=Member_handicaps>");
            out.println("      <input type=hidden name=todo value=peer />");
            out.println("      <input type=hidden name=letter value=\"A\" />");         // default member list to display.
            out.println("      <input class=\"standard_button\" type=submit value=\"Peer Review Reports\" name=btnSubmit />");
            out.println("    </form>");
            out.println("  </p>");

        } else {

            out.println("  <br /><p class=\"handicaps_ctr\"><b>View Your 'Rounds Played vs Scores Posted' Report</b></p>");

            out.println("  <p class=\"handicaps_ctr\">");
            out.println("    <form class=\"handicaps_form\" method=POST action=Member_handicaps>");
            out.println("      <input type=hidden name=todo value=peer />");
            out.println("      <input type=hidden name=user value=\"" + user + "\" />");
            out.println("      <input class=\"standard_button\" type=submit value=\"View Report\" name=btnSubmit />");
            out.println("    </form>");
            out.println("  </p>");
            out.println("");

        }
        
        out.println("<script type=\"text/javascript\">");
        out.println("  function submitForm(formNum) {" );
        out.println("   if (formNum == 1) {");
        out.println("     f = document.getElementById(\"frmHdcpInquiry1\");");
        out.println("   }");
        out.println("   else {");
        out.println("     f = document.getElementById(\"frmHdcpInquiry2\");");
        out.println("   }");
        out.println("   b = f.btnSubmit;");
        out.println("   b.value='Please Wait';");
        out.println("   b.disabled=true;");
        out.println("   f.submit();");
        out.println("  }");

        out.println("</script>");
        
    } // else (if !new_skin)

 }
 
 
 // *********************************************************
 // Handles request for displaying current hdcp and last 20 scores
 // *********************************************************
 
 private void doViewScores(String user, String club, String clubNum, String clubAssocNum, boolean new_skin, HttpServletRequest req, Connection con, PrintWriter out, boolean self) {


    String inc20 = (req.getParameter("inc20") == null) ? "" : req.getParameter("inc20");
    String hdcp[] = new String[6];
    String results[] = new String[22];

    String hdcpSystem = Common_handicaps.getClubHdcpOption(club, con);
    String gender = "";
    boolean isGHIN = hdcpSystem.equalsIgnoreCase( "GHIN" );
    boolean isGolfNet = hdcpSystem.equalsIgnoreCase( "GN21" );


    //
    // IF CLUB USES GHIN AND USER IS DOING A LOOKUP ON SOMEONE ELSE THEN WE NEED TO RE-LOAD THE CLUB# & ASSOC# SINCE IT COULD BE DIFFERENT
    // OR IF mpccpb THEN FORCE A LOOKUP TO LOAD THE MEMBERS GENDER FOR EXCLUDING TEES BASED ON GENDER
    if ((!self && isGHIN) || club.equals("mpccpb")) {

        try {

            PreparedStatement pstmt = con.prepareStatement("" +
                    "SELECT cn.club_num, ca.assoc_num, m.gender " +
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
                gender = rs.getString("m.gender");
            }

        } catch (Exception exc) {

            out.println("<br><p>We were unable to retrieve your handicap information at this time due to an error looking up the Club Number("+clubNum+") and Association Number ("+clubAssocNum+") for the other member. <!-- " + exc.toString() + " --><br></p>");
            return;
        }

    }


    if ( isGHIN ) {

        hdcp = Common_ghin.getCurrentHdcp(user, club, clubNum, clubAssocNum, out, con);

    } else if ( isGolfNet ) {

        hdcp = Common_golfnet.getCurrentHdcp(user, club, con);

    }



    /*
    hdcp[0] = "1";
    hdcp[1] = "";
    hdcp[2] = "14.7"; // hdcpIndex;
    hdcp[3] = "0"; //hdcpDiff;
    hdcp[4] = "20070201"; //hdcpDate;
    hdcp[5] = "1";  //status
    */
    
    if (!new_skin) {
        if (hdcp[0] == null || !hdcp[0].equals("1")) {

            out.println("<br><p>We were unable to retrieve your handicap information at this time, please try again later.<br><br>Error: " + hdcp[1] + "</p>");
            return;
        } else if (hdcp[5].equals("2")) {

            out.println("<br><p>Your GHIN number is not active at this time.</p>");
            return;
        }
    } // if (!new_skin)
    else {
        if (hdcp[0] == null || !hdcp[0].equals("1")) {

            out.println("  <br /><p class=\"handicaps_ctr\">");
            out.println("    We were unable to retrieve the handicap information at this time, please try again later.<br /><br />Error: " + hdcp[1] + "</p>");
            outReturnBtn(out, "view");
            return;
        } else if (hdcp[5].equals("2") && isGHIN) {

            out.println("  <br /><p class=\"handicaps_ctr\">That GHIN number is not active at this time.</p>");
            outReturnBtn(out, "view");
            return;
        }
        

    } // else (if !new_skin)
    
    String sdate = "";
    String parts[] = new String[3];

    // date format is 2012-07-01T00:00:00
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

    if (isGHIN) {

        g_hdcp = Common_ghin.parseHandicapValue(hdcp[2]);

    } else if (isGolfNet) {

        g_hdcp = Common_golfnet.parseHandicapValue(hdcp[2]);
        
        if (g_hdcp != -99) {
            g_hdcp /= 10;
            hdcpDisplay = String.valueOf(g_hdcp);
        }

    }
    
    if (!new_skin) {
      out.println("<CENTER><br><p align=center><font size=5>Handicap Inquiry Results</font></p>");
    
      out.println("<p align=center><b><i>As of " + sdate + ", your USGA " + (club.equals("blackhawk") ? "index" : "handicap") + " is " + hdcpDisplay + "</i></b></p>");
    }
    else {
      out.println("  <h2 class=\"handicaps_ctr\">Handicap Inquiry Results</h2>");
    
      out.println("  <div class=\"handicaps_scores\">");
      out.println("    <p class=\"handicaps_ctr\"><b><i>As of " + sdate + ", " + ((self) ? "your " : SystemUtils.getFullNameFromUsername(user, con) + "'s ") + Common_handicaps.getHdcpIndexName(club, hdcpSystem) + " Handicap Index is " + hdcpDisplay + "</i></b></p>");
      //out.println("    <p class=\"handicaps_ctr\"><b><i>As of " + sdate + ", your " + Common_handicaps.getHdcpIndexName(club, hdcpSystem) + " " + ((club.equals("blackhawk") || club.equals("longcove")) ? "index" : "handicap") + " is " + hdcpDisplay + "</i></b></p>");
        
    } // else (if !new_skin)
    
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

                if (club.equals("mpccpb")) {
                    
                    if ((gender.equalsIgnoreCase("M") && teeName.startsWith("Ladies")) || (gender.equalsIgnoreCase("F") && teeName.startsWith("Men"))) {
                        continue;
                    }
                }
                
                c_hdcp = (g_hdcp * slope) / 113;

                c_hdcp = Math.round(Math.abs(c_hdcp)) * -1;
                
                if (!new_skin) {
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
                } // if (!new_skin)
                else {
                  if (first) {
                      out.println("    <table class=\"standard_list_table\">"); 
                      out.println("    <thead>"); 
                      out.println("      <tr><th class=\"hcaps_scores\" colspan=" + ((!course.equals("")) ? "3" : "2") + ">Course Handicaps</th></tr>");
                      out.println("      <tr>");
                      if (!course.equals("")) out.println("      <th class=\"hcaps_scores\">Course</th>");
                      out.println("      <th class=\"hcaps_scores\">Tees</th><th class=\"hcaps_scores\">Handicap</th></tr>");
                      out.println("    </thead><tbody>");
                      first = false;
                  }

                  out.println("      <tr>");
                  if (!course.equals("")) out.println("        <td class=\"hcaps_scores\">" + ((!lastCourse.equals(course)) ? course : "") + "</td>");
                  out.println("        <td class=\"hcaps_scores\">" + teeName + "</td>");
                  out.print("        <td class=\"hcaps_scores\">");
                  if (c_hdcp > 0) {
                      // positive value - scratch golfer! show plus sign
                      out.print("+" + (int)c_hdcp);
                  } else {
                      // negative value - normal value suppress minus sign
                      out.print(Math.abs((int)c_hdcp));
                  }
                  out.println("</td></tr>");
                    
                } // else (if !new_skin)
                
                lastCourse = course;

            } // end while loop

        } catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error loading course and tee information.", exc.getMessage(), out, false);
        }
    
    } else {

        if (isGolfNet && hdcp[2].equals("999")) {
            out.println("<p align=center><b>Your handicap has not yet been determined as of " + hdcp[4] + ".</b></p>");
        } else {
            out.println("<p align=center><b>Your " + Common_handicaps.getHdcpIndexName(club, hdcpSystem) + " " + (club.equals("blackhawk") ? "index" : "handicap") + " inquiry was not successful.</b></p>");
            inc20 = "no"; // force recent scores off
        }

    } // end if g_hdcp not -99
     
    if (!new_skin) {
        out.println("</table><br><br>");
    } else {
        out.println("</tbody>");
        out.println("</table><br /><br />");        
    }
    
 
    // DISPLAY THE LAST 20 SCORE POSTINGS FOR THIS USER
    if (inc20.equals("yes")) {
        int s = 0;

        if (isGHIN) {

            results = Common_ghin.getRecentPostings(user, club, clubNum, clubAssocNum, out, con);

        } else if (isGolfNet) {

            results = Common_golfnet.getRecentPostings(user, club, out, con);

        }

        // handle a possible failure
        if (results[0] == null || !results[0].equals("1")) {

            out.println("  <br /><p class=\"handicaps_ctr\">");
            out.println("    We were unable to retrieve your most recent score postings at this time, please try again later.<br /><br />Error: " + results[1] + "</p>");
            outReturnBtn(out, "view");
            return;
        }

        //Utilities.logError("results.length=" + results.length);


        // data should be good, lets display the results
        if (!new_skin) {
            //out.println("<p>Your most recent score postings are:</p>");
            out.println("<table align=center bgcolor=\"#F5F5DC\" border=1 width=400>");
            out.println("<tr><td colspan=3 align=center bgcolor=\"#336633\"><font size=4 color=white><b>Recent Score Postings</b></font>");
            out.println("<br><br><font size=2 color=#F5F5DC>" +
                    "Legend: H = Home, A = Away, T = Tournament, I = Internet<br>" +
                    "AI = Away Internet, TI = Tournament Internet, C = Combined Nines<br>" +
                    "CI = Combined Internet, P = Penalty" +
                    "</font></td></tr>");
            out.println("<tr><td align=center><b>Date</b></td><td align=center><b>Score</b></td><td align=center><b>Type</b></td></tr>");
        } // if (!new_skin)
        else {
            out.println("    <div class=\"tee_sheet_legend main_instructions\">");
            out.println("    <h2>Legend:</h2>");
            out.println("    <p class=\"fb_legend\">");
            out.println("      <span class=\"code_legend_item\"><span>H</span> = <span>Home</span></span>");
            out.println("      <span class=\"code_legend_item\"><span>A</span> = <span>Away</span></span>");
            out.println("      <span class=\"code_legend_item\"><span>T</span> = <span>Tournament</span></span>");
            out.println("      <span class=\"code_legend_item\"><span>I</span> = <span>Internet</span></span>");
            out.println("    </p>");
            out.println("    <p class=\"fb_legend\">");
            out.println("      <span class=\"code_legend_item\"><span>AI</span> = <span>Away Internet</span></span>");
            out.println("      <span class=\"code_legend_item\"><span>TI</span> = <span>Tournament Internet</span></span>");
            out.println("      <span class=\"code_legend_item\"><span>C</span> = <span>Combined Nines</span></span>");
            out.println("    </p>");
            out.println("    <p class=\"fb_legend\">");
            out.println("      <span class=\"code_legend_item\"><span>CI</span> = <span>Combined Internet</span></span>");
            out.println("      <span class=\"code_legend_item\"><span>P</span> = <span>Penalty</span></span>");
            out.println("    </p>");
            out.println("    </div>    <!-- tee_sheet_legend -->");
            out.println("    <table class=\"standard_list_table\">");
            out.println("    <thead><tr><th class=\"hcaps_scores\" colspan=3>Recent Score Postings</th>");
            out.println("      </tr><tr><th class=\"hcaps_scores\">Date</th><th class=\"hcaps_scores\">Score</th><th class=\"hcaps_scores\">Type</th>");
            out.println("      </tr>");
            out.println("    </thead><tbody>");
                       
        } // else (if !new_skin)
        
        for (int i = 2; i < results.length; i++) {
            
          if (results[i] != null) {
              parts = results[i].split("\\|");
              s = 0;
              try {
                  s = Integer.parseInt(parts[0]);
              } catch (Exception ignore) { }
              
              if (!new_skin) {
                  out.print("<tr><td align=center>" + parts[2] + "</td>");
                  out.print("<td align=center>" + ( (s == 0) ? parts[0] : s ) + "</td>");
                  out.print("<td align=center>" + parts[1] + "</td></tr>");
              }
              else {
                  if (parts[2].indexOf("T") > 0) {
                      String date_part[] = new String[1];
                      date_part = parts[2].split("T");
                      out.print("<tr><td class=\"hcaps_scores\">" + date_part[0] + "</td>");
                  } else {
                      out.print("<tr><td class=\"hcaps_scores\">" + parts[2] + "</td>");
                  }
                  out.print("<td class=\"hcaps_scores\">" + ( (s == 0) ? parts[0] : s ) + "</td>");
                  out.println("<td class=\"hcaps_scores\">" + parts[1] + "</td></tr>");
                  
              } // else (if !new_skin)
          }
        }
        
        if (!new_skin) {
            out.println("</table>");
        }
        else {
            out.println("    </tbody>");
            out.println("    </table>    <!-- standard_list_table -->");            
        } // else(if !new_skin)
        
    }

    
    if (!new_skin) {

       out.println("<BR><BR><form action=Member_handicaps><input type=hidden name=todo value=view><input type=submit value=\"Return\" style=\"width: 75px; background:#8B8970\"></form>");
       out.println("</CENTER>");

    } else {

       out.println("<br /><p class=\"handicaps_ctr\"><center>");
       out.println("<form action=\"Member_handicaps\">");
       out.println("<input type=\"hidden\" name=\"todo\" value=\"view\" />");
       out.println("<input class=\"standard_button\" type=\"submit\" value=\"Return\" /></form>");
       out.println("</center></p>");

       out.println("</div> <!-- handicaps_scores -->");
       
    } // else (if !new_skin)
        
 }

 private void selectMember(HttpServletRequest req, String club, boolean new_skin, String user, int sess_activity_id, Connection con, PrintWriter out) {

   Statement stmt = null;
   ResultSet rs = null;

   int peer_review = 0;

   String name = "";
   String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");

   try {
       stmt = con.createStatement();

       rs = stmt.executeQuery("SELECT peer_review FROM club5");

       if (rs.next()) {
           peer_review = rs.getInt("peer_review");
       }

       stmt.close();

   } catch (Exception exc) {

   }

   if (!new_skin) {
       out.println("<script type=\"text/javascript\">");
       out.println("<!--");
       out.println("function cursor() { document.forms['f'].name.focus(); }");
       out.println("function movename(name) {");
       out.println(" document.forms['f'].name.value = name;");
       out.println("}");
       out.println("// -->");
       out.println("</script>");

       out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

       out.println("<br><br>");
       out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\" width=600>");
       out.println("<tr><td>");
       out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
       out.println("<p>To lookup the handicap of a member, enter the name, ");
          out.println("or use the Member List to select the first letter of their last name.&nbsp; ");
          out.println("This will search for all names that start with the letter you select.&nbsp; ");
          out.println("Check the 'Include 20 Most Recent Scores' to see the members last 20 scores posted.<br>" +
                  "<i>Note: Selecting all members will disregard the last 20 postings option.</i></p>");
          out.println("</font>");
       out.println("</td></tr></table>");

       out.println("<font size=\"2\" face=\"Courier New\">");
       out.println("<p align=\"center\">(Click on <b>'Member List'</b> on right to view a list of members)</p>");
       out.println("<br></font>");
       out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

       out.println("<form action=\"Member_handicaps\" method=\"post\" target=\"bot\" name=\"f\">");
       out.println("<input type=\"hidden\" name=\"todo\" value=\"view\">");

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

                      if (peer_review == 1) {
                          out.println("<input type=checkbox name=inc20 value=yes id=inc20><label for=inc20> Include 20 Most Recent Scores</label><br><br>"); // View 20 Most Recent Scores
                      }

                      out.println("</font>");
                   out.println("</td></tr>");
                out.println("</table>");

             out.println("</td>");
   } // if (!new_skin)
   else {
       out.println("");
       out.println("<script type=\"text/javascript\">");
       out.println("<!--");
       
       out.println("function erasename() {");
       out.println(" eval(\"document.member_hcaps_form.name.value = '';\")");
       out.println("}");
       
       out.println("function goSearch() {");
       out.println(" var f = document.member_hcaps_form;");
       out.println(" var len = f.name.value.length;");
       out.println(" if (f.name.value == '') {");
       out.println("  alert('Please select a name first.');");
       out.println(" } else {");
       out.println("  f.submit();");
       out.println(" }");
       out.println("}");

       out.println("function move_name(pName) {");
       out.println(" var f = document.member_hcaps_form;");
       out.println(" var names = new Array();");
       out.println(" array = pName.split(':'); ");
       out.println(" var name = array[0];");
       out.println(" var data = array[1];"); // person_id - if absent it's 'undefined' so test and set to zero too
       out.println(" if (isNaN(data)) data = 0;");
       out.println(" f.name.value = name;");
       out.println(" f.hcaps_data.value = data;");
       out.println("}");

       out.println("function subletter(x) {");
       out.println(" var f = document.member_hcaps_form;");
       out.println(" f.letter.value = x;");
       out.println(" $(\"#name_list_content\").load(\"data_loader?name_list&letter=\" + x);");
       out.println("}");

       out.println("// -->");
       out.println("</script>");
       
       out.println("  <div class=\"main_instructions\">");  
       out.println("    <b>Instructions: </b>");
       out.println("    To lookup the handicap of a member, enter the name, ");
       out.println("    or use the Member List to select the first letter of their last name.&nbsp; ");
       out.println("    This will search for all names that start with the letter you select.");
       out.println("    Check the 'Include 20 Most Recent Scores' to see the members last 20 scores posted.<br />" +
                   "<i>Note: Selecting all members will disregard the last 20 postings option.</i>");
       out.println("    <br /><br />(Click on <strong>'Member List'</strong> on right to view a list of members)");
       out.println("  </div>");
       
       //
       // letter2 is the same as letter, add for new skin to not affect current code.
       //
       String letter2 = "";
       if (req.getParameter("letter") != null) {  
           letter2 = req.getParameter("letter");      // get the letter
       }    
       
       out.println("  <form action=\"Member_handicaps\" method=\"post\" id=\"member_hcaps_form\" name=\"member_hcaps_form\">");
       out.println("    <input type=\"hidden\" name=\"todo\" value=\"view\" />");
       out.println("    <div class=\"res_left\">");
       out.println("      <div class=\"sub_main\">");
       
       out.println("      <br /><strong>Name:</strong><br />");
       out.println("      <br /><a href=\"javascript:void(0);\" class=\"tip_text\" onclick=\"erasename()\">Erase</a>&nbsp;&nbsp;");

       out.println("      <input type=\"text\" name=\"name\" class=\"res_name\" size=\"20\" maxlength=\"40\" />");
       out.println("      <input type=\"hidden\" name=\"hcaps_data\" value=\"\" />");
       out.println("      <input type=hidden name=letter value=\"" + letter2 + "\" />");
       out.println("      </div>"); // end sub_main
               
       out.println("      <p class=\"handicaps_ctr\">");
       out.println("        <br /><br />");
       out.println("        <input id=\"back\" name=\"back\" type=\"submit\" value=\"Search\" onclick=\"goSearch()\" />");
    
       if (peer_review == 1) {
           out.println("        <br />");
           out.println("        <input type=checkbox name=inc20 value=yes id=inc20 /><label for=inc20> Include 20 Most Recent Scores</label>"); // View 20 Most Recent Scores
       }
       out.println("        <br /><br /><br />");
       out.println("        <input id=\"back\" name=\"back\" type=\"button\" value=\"Return\" onclick=\"window.location.href='Member_handicaps?todo=view'\" />");
       out.println("      </p>");
       out.println("    </div>    <!-- res_left -->");
                 
   } // else (if !new_skin)

   if (req.getParameter("letter") != null) {     // if user clicked on a name letter

      String letter = req.getParameter("letter");      // get the letter
      letter = letter + "%";

      String first = "";
      String mid = "";
      String last = "";
      name = "";
      String wname = "";
      String dname = "";

      if (!new_skin) {
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
      } // if (!new_skin)
      else {
    
          //
          // output names with letter selected
          //
          out.println("    <div class=\"res_mid\" id=\"name_select\">");
          out.println("      <div class=\"name_list\" id=\"name_list_content\">");
          out.println(" ");
          nameLists.displayNameList(letter, club, sess_activity_id, 3, con, out, false);
          out.println("      </div>");
          out.println("    </div>    <!-- res_mid -->"); 
      
      } // else (if !new_skin)


   } else {

      if (!new_skin) {
          out.println("<td valign=\"top\" width=\"30\">");
          out.println("&nbsp;");
          out.println("</td>");   // end of empty column
      }
      else {
          out.println("    <div class=\"res_mid\" id=\"name_select\">");
          out.println("      <div class=\"name_list\" id=\"name_list_content\">");
      
          nameLists.displayPartnerList(user, sess_activity_id, 0, con, out);       
          out.println("      </div>");
          out.println("    </div>    <!-- res_mid -->"); 
          
      } // else (if !new_skin)      

   }  // end of if Letter

   if (!new_skin) {
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
   } // if (!new_skin)
   else {
       //
       // output letter box list
       //
       out.println("    <div class=\"res_right\">");
       out.println(" ");
       nameLists.getTable(out, user);
       out.println("    </div>    <!-- res_right -->");

       out.println("  </form>");  
       out.println("  <div class=\"handicaps_clr_both\"></div>");

   } // else (if !new_skin)
   
 }
 

 // *********************************************************
 // Allow members to select a fellow member for peer review
 // *********************************************************
 private void getViewPeerScores(HttpServletRequest req, String club, boolean new_skin, String user, int sess_activity_id, 
         Connection con, PrintWriter out) {
   
   ResultSet rs = null;

   String name = "";
   String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");
   
   if (req.getParameter("name") != null) {        // if user specified a name to search for

      name = req.getParameter("name");            // name to search for
      
      if (!name.equals( "" )) {
         
         name = SystemUtils.getUsernameFromFullName(name, con);
         viewSavedScores(req, club, name, new_skin, con, out);
         return;
      }
      
   }

   if (!new_skin) {
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

       out.println("<form action=\"Member_handicaps\" method=\"post\" target=\"bot\" name=\"f\">");
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

                      out.println("<input type=checkbox name=inc20 value=\"yes\" id=inc20><label for=inc20> Include last 20 postings</label><br><br>"); // View 20 Most Recent Scores

                      out.println("</font>");
                   out.println("</td></tr>");
                out.println("</table>");

             out.println("</td>");
   
   } else {
        
       // NEW SKIN
       
       out.println("");
       out.println("<script type=\"text/javascript\">");
       out.println("<!--");
       out.println("function erasename() {");
       out.println(" eval(\"document.member_hcaps_form.name.value = '';\")");
       out.println("}");
       
       out.println("function goSearch() {");
       out.println(" var f = document.member_hcaps_form;");
       out.println(" var len = f.name.value.length;");
       out.println(" if (f.name.value == '') {");
       out.println("  alert('Please select a name first.');");
       out.println(" } else {");
       out.println("  f.submit();");
       out.println(" }");
       out.println("}");

       out.println("function move_name(pName) {");
       out.println(" var f = document.member_hcaps_form;");
       out.println(" var names = new Array();");
       out.println(" array = pName.split(':'); ");
       out.println(" var name = array[0];");
       out.println(" var data = array[1];"); // person_id - if absent it's 'undefined' so test and set to zero too
       out.println(" if (isNaN(data)) data = 0;");
       out.println(" f.name.value = name;");
       out.println(" f.hcaps_data.value = data;");
       out.println("}");

       out.println("function subletter(x) {");
       out.println(" var f = document.member_hcaps_form;");
       out.println(" f.letter.value = x;");
       out.println(" $(\"#name_list_content\").load(\"data_loader?name_list&letter=\" + x);");
       out.println("}");

       out.println("// -->");
       out.println("</script>");
       
       out.println("  <div class=\"main_instructions\">");  
       out.println("    <b>Instructions: </b>");
       out.println("    To lookup recent score postings of a member, enter the name, ");
       out.println("    or use the Member List to select the first letter of their last name.&nbsp; ");
       out.println("    This will search for all names that start with the letter you select.");
       out.println("    <br /><br />(Click on <strong>'Member List'</strong> on right to view a list of members)");
       out.println("  </div>");
       
       //
       // letter2 is the same as letter, add for new skin to not affect current code.
       //
       String letter2 = "";
       if (req.getParameter("letter") != null) {  
           letter2 = req.getParameter("letter");      // get the letter
       }    
       
       out.println("  <form action=\"Member_handicaps\" method=\"post\" id=\"member_hcaps_form\" name=\"member_hcaps_form\">");
       out.println("    <input type=\"hidden\" name=\"todo\" value=" + todo + " />");
       out.println("    <div class=\"res_left\">");
       out.println("      <div class=\"sub_main\">");
       
       out.println("      <br /><strong>Name:</strong><br />");
       out.println("      <br /><a href=\"javascript:void(0);\" class=\"tip_text\" onclick=\"erasename()\">Erase</a>&nbsp;&nbsp;");

       out.println("      <input type=\"text\" name=\"name\" class=\"res_name\" size=\"20\" maxlength=\"40\" />");
       out.println("      <input type=\"hidden\" name=\"hcaps_data\" value=\"\" />");
       out.println("      <input type=hidden name=letter value=\"" + letter2 + "\" />");
       out.println("      </div>"); // end sub_main
               
       out.println("      <p class=\"handicaps_ctr\">");
       out.println("        <br /><br />");
       out.println("        <input id=\"back\" name=\"back\" type=\"submit\" value=\"Search\" onclick=\"goSearch()\" />");
       out.println("        <br /><br /><br />");
       out.println("        <input id=\"back\" name=\"back\" type=\"button\" value=\"Return\" onclick=\"window.location.href='Member_handicaps?todo=view'\" />");
       out.println("      </p>");
       out.println("    </div>    <!-- res_left -->");
       
   } // else (if !new_skin)

   if (req.getParameter("letter") != null) {     // if user clicked on a name letter

      String letter = req.getParameter("letter");      // get the letter
      letter = letter + "%";

      String first = "";
      String mid = "";
      String last = "";
      name = "";
      String dname = "";

      if (!new_skin) {
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
      } // if (!new_skin)
      else {
    
          //
          // output names with letter selected
          //
          out.println("    <div class=\"res_mid\" id=\"name_select\">");
          out.println("      <div class=\"name_list\" id=\"name_list_content\">");
          out.println(" ");
          nameLists.displayNameList(letter, club, sess_activity_id, 0, con, out, false);
          out.println("      </div>");
          out.println("    </div>    <!-- res_mid -->"); 
      
      } // else (if !new_skin)
      
           
   } else {
      
      if (!new_skin) {
          out.println("<td valign=\"top\" width=\"30\">");
          out.println("&nbsp;");  
          out.println("</td>");   // end of empty column
      } // if (!new_skin)
      else {
          out.println("    <div class=\"res_mid\" id=\"name_select\">");
          out.println("      <div class=\"name_list\" id=\"name_list_content\">");
      
          nameLists.displayPartnerList(user, sess_activity_id, 0, con, out);       
          out.println("      </div>");
          out.println("    </div>    <!-- res_mid -->"); 
          
      } // else (if !new_skin)
     
   }  // end of if Letter

   if (!new_skin) {
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
    
    out.println("<BR><BR><form action=Member_handicaps><input type=\"hidden\" name=\"todo\" value=\"view\"><input type=submit value=\"Return\" style=\"width: 75px; background:#8B8970\"></form>");

    out.println("</center></font>");
   } // if (!new_skin)
   else {
       //
       // output letter box list
       //
       out.println("    <div class=\"res_right\">");
       out.println(" ");
       nameLists.getTable(out, user);
       out.println("    </div>    <!-- res_right -->");

       out.println("  </form>");  
       out.println("  <div class=\"handicaps_clr_both\"></div>");

   } // else (if !new_skin)
    
 }
 
 
 private void viewSavedScores(HttpServletRequest req, String club, String user, boolean new_skin, Connection con, PrintWriter out) {

    HttpSession session = null;

    if (req != null) {
        session = req.getSession(false);
    }

    // create instance since it's static
    Proshop_report_handicap report = new Proshop_report_handicap();
    report.viewSavedScores(req, user, session, club, con, out);
      
 }
 
 private void viewSavedScores2(HttpServletRequest req, String club, String user, boolean new_skin, Connection con, PrintWriter out) {
    
    
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
    int peer_review = 0;


    boolean skip_9hole = false;
    if (club.equals("castlepines")) skip_9hole = true;

    try {

        // get the last 20 scores posted
        PreparedStatement pstmt = con.prepareStatement("" +
                "SELECT (SELECT multi FROM club5) AS multi, (SELECT peer_review FROM club5) AS peer_review, CONCAT(name_first, ' ', name_last) AS fullName FROM member2b WHERE username = ?;");
        pstmt.clearParameters();
        pstmt.setString(1, user);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            fullName = rs.getString("fullName");
            multi = rs.getInt("multi");
            peer_review = rs.getInt("peer_review");
        }
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading member data for handicap peer report.", exc.getMessage(), out, false);
        return;
    }
    
    if (!new_skin) {
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
    } // if (!new_skin)
    else {
        out.println("  <h2 class=\"handicaps_ctr\">Handicap Peer Review Report</h2>");
        
        out.println("  <br /><h3 class=\"handicaps_ctr\"><i>For " + fullName + " (" + formatHdcpNum(hdcp_num, club.equals("mpccpb") ? 2 : 1) + ")</i></h3><br />");
        out.println("  <div class=\"handicaps_peer_review\">");

        out.println("    <table class=\"standard_list_table\">");
        out.println("    <thead>"); 
        out.println("      <tr><th class=\"hcaps_peer\" colspan=" + ((multi == 0) ? "2" : "3") + ">Rounds Played</th>");
        out.println("        <th class=\"hcaps_peer\" colspan=4>Scores Posted</th></tr>");

        out.println("      <tr><th class=\"hcaps_peer\">Date</th>" +
                        ((multi == 0) ? "" : "<th class=\"hcaps_peer\">Course</th>") +
                        "<th class=\"hcaps_peer\">9/18</th>");
        out.println("        <th class=\"hcaps_peer\">Date</th><th class=\"hcaps_peer\">Score</th>");
        out.println("        <th class=\"hcaps_peer\">Type</th><th class=\"hcaps_peer\">Tees</th>");
        out.println("      </tr>");
        out.println("    </thead>");
        out.println("    <tbody>");
        
    } // else (if !new_skin)
    
    
    try {

        // get the last 20 scores posted
        PreparedStatement pstmt = con.prepareStatement("" +
                "SELECT DATE_FORMAT(sp.date, '%Y%m%d') AS date, sp.score, sp.type, t.tee_name, c.courseName " +
                "FROM score_postings sp " +
                "LEFT OUTER JOIN tees t ON t.tee_id = sp.tee_id " +
                "LEFT OUTER JOIN clubparm2 c ON c.clubparm_id = t.course_id " +
                "WHERE sp.hdcpNum = CONVERT(CAST(REPLACE(?, '-', '') AS UNSIGNED), CHAR) AND type <> 'A' AND type <> 'AI' " +
              //"WHERE CAST(sp.hdcpNum AS UNSIGNED) = ? AND type <> 'A' AND type <> 'AI' " +
                "ORDER BY date DESC LIMIT 40;");
        
        pstmt.clearParameters();
        pstmt.setString(1, hdcp_num);
        ResultSet rs = pstmt.executeQuery();
        
        String tmp = "";
        
        while (rs.next()) {
            posting_dates.add(rs.getInt("date"));
            posting_scores.add(rs.getInt("score"));
            posting_types.add(rs.getString("type"));
            posting_used.add(0); // default it to not used
            if (rs.getString("tee_name") != null) {
                tmp = rs.getString("courseName") + " " + rs.getString("tee_name");
                posting_tees.add( tmp.trim() );
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
                    "(username1 = ? && show1 = 1 && nopost1 = 0" + ((skip_9hole) ? " && p91 = 0" : "") + ") || " +
                    "(username2 = ? && show2 = 1 && nopost2 = 0" + ((skip_9hole) ? " && p92 = 0" : "") + ") || " +
                    "(username3 = ? && show3 = 1 && nopost3 = 0" + ((skip_9hole) ? " && p93 = 0" : "") + ") || " +
                    "(username4 = ? && show4 = 1 && nopost4 = 0" + ((skip_9hole) ? " && p94 = 0" : "") + ") || " +
                    "(username5 = ? && show5 = 1 && nopost5 = 0" + ((skip_9hole) ? " && p95 = 0" : "") + ")" +
                ") " +
                "ORDER BY date DESC, time ASC " +
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
                
                if (!new_skin) {
                    if (post_date == play_date && used == 0) { //  bgcolor=#F5F5DC   bgcolor=#86B686

                        posting_used.set(i, 1);
                        out.println("<tr align=center bgcolor=#86B686>" +
                                "<td nowrap>" + play_sdate + "</td>" +
                                ((multi == 0) ? "" : "<td nowrap>" + course + "</td>") +
                                "<td>" + holes + "</td> <td nowrap>" + post_sdate + "</td><td>" + score + "</td><td>" + type + "</td><td>" + tees + "</td></tr>");
                        found = true;
                        break;

                    } // end if matching dates
                } // if (!new_skin)
                else {
                    if (post_date == play_date && used == 0) {

                        posting_used.set(i, 1);
                        out.println("      <tr><td class=\"hcaps_peer\">" + play_sdate + "</td>");
                        out.println( ((multi == 0) ? "      <!-- multi is 0 -->" : "      <td class=\"hcaps_peer\">" + course + "</td>") );
                        out.println("        <td class=\"hcaps_peer\">" + holes + "</td><td class=\"hcaps_peer\">" + post_sdate + "</td>");
                        out.println("        <td class=\"hcaps_peer\">" + score + "</td><td class=\"hcaps_peer\">" + type + "</td>");
                        out.println("        <td class=\"hcaps_peer\">" + tees + "</td></tr>");
                        found = true;
                        break;

                    } // end if matching dates
                    
                } // else (if !new_skin)
            
                
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
                if (!new_skin) {
                    out.println("<tr align=center><td nowrap>" + play_sdate + "</td>" +
                            ((multi == 0) ? "" : "<td>" + course + "</td>") + 
                            "<td>" + holes + "</td> <td colspan=4 bgcolor=#FFFF8F>&nbsp;</td></tr>");
                }
                else {
                    out.println("      <tr><td class=\"hcaps_peer\">" + play_sdate + "</td>" +
                            ((multi == 0) ? "" : "<td class=\"hcaps_peer\">" + course + "</td>") + 
                            "<td class=\"hcaps_peer\">" + holes + "</td>");
                    out.println("        <td class=\"hcaps_peer hcaps_empty\" colspan=4>&nbsp;</td></tr>");
                    
                } // else (if !new_skin)
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

                    if (!new_skin) {
                      out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                                  "<td colspan=\"" + ((multi == 0) ? "6" : "7") + "\" align=\"center\">Unmatched Score Postings</td></tr>");
                    }
                    else {
                      out.println("      <tr class=\"hcaps_peer_hdr\">" +
                                  "<td class=\"hcaps_peer_hdr_data\" colspan=\"" + ((multi == 0) ? "6" : "7") + "\">Unmatched Score Postings</td></tr>");
                        
                    } // else (if !new_skin)
                    didHeader = true;
                }

                if (!new_skin) {
                  out.println("<tr align=\"center\">" +
                          "<td bgcolor=#FFFF8F colspan=\"" + ((multi == 0) ? "2" : "3") + "\">&nbsp;</td>" +
                          "<td nowrap>" + post_sdate + "</td><td>" + score + "</td><td>" + type + "</td><td>" + tees + "</td></tr>");
                }
                else {
                  out.println("      <tr><td class=\"hcaps_peer hcaps_unmatch\" colspan=\"" + ((multi == 0) ? "2" : "3") + "\">&nbsp;</td>" +
                          "<td class=\"hcaps_peer hcaps_unmatch\">" + post_sdate + "</td>");
                  out.println("        <td class=\"hcaps_peer hcaps_unmatch\">" + score + "</td><td class=\"hcaps_peer hcaps_unmatch\">" +
                          type + "</td><td class=\"hcaps_peer hcaps_unmatch\">" + tees + "</td></tr>");
                    
                } // else (if !new_skin)

            } // end unmatched postings loop

        } // end for loop

        int [] rounds = new int[3];
        int today = (int)SystemUtils.getDate(con);
        rounds = Proshop_report_handicap.lookupHistory(last_date, today, user, con, out);

        if (!new_skin) {
            out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                        "<td colspan=\"" + ((multi == 0) ? "6" : "7") + "\" align=\"center\">Rounds Played vs. Scores Posted<br><font size=2>(for last twenty rounds)</font></td></tr>");

            out.println("<tr bgcolor=\"#86B686\">" +
                    "<td align=\"center\" colspan=\"3\">9 hole: <b>" + rounds[0] + "</b> &nbsp; &nbsp; 18 hole: <b>" + rounds[1] + "</b></td>" +
                    "<td align=\"center\" colspan=\"" + ((multi == 0) ? "3" : "4") + "\">Posted: <b>" + rounds[2] + "</b></td></tr>");
            
        } 
        else {
            out.println("      <tr class=\"hcaps_peer_hdr\">" +
                        "<td class=\"hcaps_peer_hdr_data\" colspan=\"" + ((multi == 0) ? "6" : "7") + "\">Rounds Played vs. Scores Posted<br />(for last twenty rounds)</td></tr>");

            out.println("      <tr>" +
                    "<td class=\"hcaps_peer hcaps_counts\" colspan=\"3\">9 hole: <b>" + rounds[0] + "</b> &nbsp; &nbsp; 18 hole: <b>" + rounds[1] + "</b></td>");
            out.println("        <td class=\"hcaps_peer hcaps_counts\" colspan=\"" + ((multi == 0) ? "3" : "4") + "\">Posted: <b>" + rounds[2] + "</b></td>");
            out.println("      </tr>");
            
        } // else (if !new_skin)

    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading data for handicap peer report.", exc.getMessage(), out, false);
        return;
    }
    
    if (!new_skin) {
        out.println("</table>");

        out.println("<br><br>");

        if (req.getParameter("excel") == null) {     // if normal request
            out.println("<center><form method=\"post\" action=\"Member_handicaps\" target=\"_blank\">");
            out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"todo\" value=\"peer\">");
            out.println("<input type=\"hidden\" name=\"user\" value=\"" + user + "\">");
            out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></center>");

            out.println("<table align=center><tr>");
            //if (peer_review == 1) {
                out.println("<td><form method=\"" + ((peer_review == 1) ? "post" : "get") + "\"><input type=submit value=\"Back\" style=\"width: 75px; background:#8B8970\"><input type=\"hidden\" name=\"todo\" value=\"" + ((peer_review == 1) ? "peer" : "view") + "\"></form></td>");
                out.println("<td>&nbsp; &nbsp; &nbsp;</td>");
            //}
            out.println("<td><form action=Member_announce><input type=submit value=\"Home\" style=\"width: 75px; background:#8B8970\"></form></td>");
            out.println("</tr></table>");
        } // end if not excel
    } // if (!new_skin)
    else {
        out.println("    </tbody>");
        out.println("    </table>    <!-- standard_list_table -->");

        out.println("  </div>    <!-- handicaps_peer_review -->");
        out.println("  <br />");

        if (req.getParameter("excel") == null) {     // if normal request
            out.println("  <p class=\"handicaps_ctr\">");
            out.println("    <form method=\"post\" action=\"Member_handicaps\" target=\"_blank\">");
            out.println("      <input type=\"hidden\" name=\"excel\" value=\"yes\" />");
            out.println("      <input type=\"hidden\" name=\"todo\" value=\"peer\" />");
            out.println("      <input type=\"hidden\" name=\"user\" value=\"" + user + "\" />");
            out.println("      <input class=\"standard_button\" type=\"submit\" value=\"Create Excel Spreadsheet\" />");
            out.println("    </form></p>");

            //
            // Find first letter of last name.
            //
            int start = 0;
            String lastNameStr = "";
            String letter = "A";
            
            if (fullName.length() > 0) {
                start = fullName.indexOf(" ");         // find first space in fullname.
                
                if (start == -1)
                    start = 0;                         // space not found default to start of fullname.
                lastNameStr = fullName.substring(start);
                lastNameStr =  lastNameStr.trim();
                letter = lastNameStr.substring(0, 1);  // Get first letter.
            }
            
            if (letter.equals("") )
                letter = "A";             // no letter found default to A.
            
            out.println("  <p class=\"handicaps_ctr\">");
            out.println("    <form class=\"handicaps_form\" method=\"" + ((peer_review == 1) ? "post" : "get") + "\">");
            out.println("      <input class=\"standard_button\" type=submit value=\"Return\" />");
            out.println("      <input type=\"hidden\" name=\"todo\" value=\"" + ((peer_review == 1) ? "peer" : "view") + "\" />");
            out.println("      <input type=hidden name=letter value=\"" + letter + "\" />");
            out.println("    </form></p>");

        } // end if not excel
        
    } // else (if !new_skin)
    
 }    
  
 
 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out, boolean new_skin, String club, int sess_activity_id, HttpServletRequest req) {

    if (!new_skin) {
        out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
        out.println("<BODY><CENTER>");
        out.println("<BR><H2>Access Error</H2><BR>");
        out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
        out.println("<BR>This site requires the use of Cookies for security purposes.  We use them to verify");
        out.println("<BR>your session and prevent unauthorized access.  Please check your 'Privacy' settings,");
        out.println("<BR>under 'Tools', 'Internet Options' (for MS Internet Explorer).  This must be set to");
        out.println("<BR>'Medium High' or lower.  Thank you.");
        out.println("<BR><BR>");
        out.println("<a href=\"Logout\" target=\"_top\">Return</a>");
        out.println("</CENTER></BODY></HTML>");
    } // if (!new_skin)
    else {
        out.println("  <h2 class=\"handicaps_ctr\">Access Error</h2><br />");
        out.println("  <p  class=\"handicaps_ctr\">");
        out.println("    <br />Sorry, you must login before attempting to access these features.<br />");
        out.println("    <br />This site requires the use of Cookies for security purposes.  We use them to verify");
        out.println("    <br />your session and prevent unauthorized access.  Please check your 'Privacy' settings,");
        out.println("    <br />under 'Tools', 'Internet Options' (for MS Internet Explorer).  This must be set to");
        out.println("    <br />'Medium High' or lower.  Thank you.");
        out.println("    <br /><br />");
        out.println("    <a class=\"standard_button\" href=\"Logout\" target=\"_top\">Return</a>");
        out.println("  </p>");
        Common_skin.outputPageEnd(club, sess_activity_id, out, req);        
        
    } // else (if !new_skin)
    out.close();

 }
 
 // *********************************************************
 // Error Page top output.
 // *********************************************************

  private void errorPageTop(PrintWriter out, HttpServletRequest req, HttpSession session, Connection con, String titleStr, String breadCr) {

     String club = (String) session.getAttribute("club");               // get name of club
     int sess_activity_id = (Integer) session.getAttribute("activity_id");
//     boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");
     String clubName = SystemUtils.getClubName(con);            // get the full name of this club
//     String index = req.getParameter("index");               // get return indicator

     Common_skin.outputHeader(club, sess_activity_id, titleStr, true, out, req);
     Common_skin.outputBody(club, sess_activity_id, out, req);
     Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
     Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
     Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
     Common_skin.outputPageStart(club, sess_activity_id, out, req);
     Common_skin.outputBreadCrumb(club, sess_activity_id, out, breadCr, req);
     out.println("  <div class=\"handicaps_clr\">");
     out.println("    <p class=\"handicaps_ctr\">");
     out.println("      <img src=\"/" + rev + "/images/foretees.gif\" /><br />");
     out.println("      <hr class=\"handicaps\" />");
     out.println("    </p></div>");

 }

 private void dbConnError(PrintWriter out) {
   
   out.println("  <br /><h3  class=\"handicaps_ctr\">Database Connection Error</h3>");
   out.println("  <p  class=\"handicaps_ctr\">");
   out.println("    <br /><br />Unable to connect to the Database.");
   out.println("    <br />Please try again later.");
   out.println("    <br /><br />If problem persists, contact your club manager.");
   out.println("    <br /><br />");
   out.println("    <a class=\"standard_button\" href=\"Member_announce\">Return</a>");
   out.println("  </p>");

 }
 
 
 // *********************************************************
 // Output Return button on a page.
 // *********************************************************

 private void outReturnBtn (PrintWriter out, String page) {
     
    out.println("<br /><br /><p class=\"handicaps_ctr\"><center>");
    out.println("<form action=\"Member_handicaps\">");
    out.println("<input type=hidden name=todo value=\"" + page + "\" />");
    out.println("<input class=\"standard_button\" type=submit value=\"Return\" /></form></center>");
    out.println("</p>");
     
 }
 
} // end servlet public class
