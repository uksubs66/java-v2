
/******************************************************************************************************************   
 *   Member_mlottery:  This servlet will process the 'Check Lottery Requests' request from
 *                     the Member's lottery page.
 *
 *
 *   called by:  Member_lott
 *
 *   created: 10/26/2005   Bob P.
 *
 *   last updated:
 *
 *        1/23/14   Marbella CC (marbellacc) - Allow "Adult Female" members to access partial requests where player1's name starts with "LCOM_Working", so they can get in and add players to their ladies groups (case 2345).
 *        1/10/13   Chantilly National G & CC (chantillynationalgcc) - allow members to join partial requests (case 2205).
 *        3/06/12   Oakland Hills CC (MI) - allow members to join a partial request.
 *        1/16/12   Now passing lottery stime in json/new-skin mode.
 *       12/04/11   (dataw) - Moved lottery registration verification from Member_lottery to verifyCustom.checkLotteryRegistrationAccess
 *       12/04/11   Added map for connecting a user's login name to their lottery id in the JSON response.
 *       12/04/11   Refinement of JSON output for new skin
 *       12/02/11   Implemented JSON output for new skin
 *       12/01/11   Move player1-25 variables to string array player[25]; Begin modifications for new skin
 *       11/23/11   Mission Valley CC (missionvalleycc) - Removed custom since it was not what they were looking for (case 1991).
 *       11/21/11   Mission Valley CC (missionvalleycc) - Allow members to join partial requests and requests that are full but contain at least one x (case 1991).
 *       11/07/11   Dataw Island Club (dataw) - Added custom to prevent "Island Social", "Social", and "Sports Membership" mships from accessing lotteries (case 1922).
 *        9/28/11   Mira Vista CC (miravista) - Allow members to join partial lottery requests (case 2014).
 *        8/04/11   Sunset Ridge CC (sunsetridge) - Allow members to join partial lottery requests (case 2014).
 *        3/24/11   Silver Creek CC (silvercreekcountryclub) - allow members to join partial requests and requests that are full but contain at least one x (case 1957).
 *        1/21/11   Mission Viejo CC (missionviejo) - Do not allow members to join a lottery request that only has one open position (case 1927).
 *        9/10/10   Fix for BlackBerry phones on mobile site.
 *        4/28/10   Cherry Hills CC (cherryhills) - allow members to join partial requests.
 *        4/28/10   Beverly GC (beverlygc) - allow members to join partial requests.
 *        4/22/10   Brae Burn CC - allow members to join partial requests.
 *        4/20/10   Bonnie Briar CC - allow members to join partial requests.
 *        4/14/10   North Ridge CC - allow members to join partial requests (case 1817).
 *        3/31/10   White Manor - allow members to join partial requests (case 1812).
 *        1/19/10   Some clubs want members to access partial requests (see change directly below).  This 
 *                  custom backs out the previous change for some clubs (Mirasol & Mission Viejo case 1778).
 *       11/20/09   Do not allow member to access requests that he/she is not a part of.  Some members
 *                  have been abusing this and Ben feels its best to block all members.
 *        7/27/09   Add support for Mobile users.
 *       10/03/08   Check for replacement text for the word "Lottery" when email is for a lottery request.
 *        7/09/08   Ridge Club - do not allow members to view other lottery requests (case 1517).
 *
 ********************************************************************************************************************
 */
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import com.foretees.common.getClub;
import com.foretees.common.Utilities;
import com.foretees.common.verifyCustom;
import org.apache.commons.lang.*;
import com.google.gson.*; // for json

public class Member_mlottery extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

    //*****************************************************
    //   doGet - receive control from Member_sheet
    //*****************************************************
    //
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        boolean json_mode = (req.getParameter("jsonMode")) != null;

        /******************************************************************************
         * 
         * NOTICE:  New skin, when non-mobile, exclusively uses JSON mode, 
         *          and passes all work to doPost
         * 
         *          doGet does nothing but pass req & resp to doPost in JSON mode.
         * 
         *          Most club customizations for lottery modals using the new skin
         *          can/should be accomplished using:
         *          /[clubname]/assets/scripts/club.js
         * 
         * ****************************************************************************
         */
        // If we are in json mode, allow doPost to handle all of the request
        if (json_mode) {
            doPost(req, resp); // call doPost
            return; //exit
        }

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        Statement stmt = null;
        Statement stmtc = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder

        if (session == null) {

            return;
        }

        Connection con = SystemUtils.getCon(session);            // get DB connection

        if (con == null) {

            out.println(SystemUtils.HeadTitle("DB Connection Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
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

        int mobile = 0;                      // Mobile user indicator

        String displayOpt = "";              // display option for Mobile devices


        //
        //   get name of club for this user
        //
        String club = (String) session.getAttribute("club");      // get club name
        String user = (String) session.getAttribute("user");
        String mship = (String) session.getAttribute("mship");
        String caller = (String) session.getAttribute("caller");

        //
        //  get Mobile user indicator
        //
        try {
            mobile = (Integer) session.getAttribute("mobile");
        } catch (Exception ignore) {
            mobile = 0;
        }

        // Check if the user has access to create 
        String accessMessage = verifyCustom.checkLotteryRegistrationAccess(session, req, con);

        if (accessMessage.length() > 0) {

            out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
            SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<CENTER><BR><BR><H3>Member Restricted</H3>");
            out.println("<BR>" + accessMessage + "<BR>");
            out.println("<BR><BR><a href=\"Member_announce\">Home</a>");
            out.println("</CENTER></BODY></HTML>");

            return;

        }
        /*
         *  This check has been moved to verifyCustom.checkLotteryRegistrationAccess
         *  See operation above.
         * 
        if (club.equals("dataw")) {
        
        
        PreparedStatement pstmt_temp = null;
        ResultSet rs_temp = null;
        
        String mship_temp = "";
        
        try {
        
        pstmt_temp = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
        pstmt_temp.clearParameters();
        pstmt_temp.setString(1, user);
        
        rs_temp = pstmt_temp.executeQuery();
        
        if (rs_temp.next()) {
        
        mship_temp = rs_temp.getString("m_ship");
        }
        
        } catch (Exception exc) {
        Utilities.logError("Member_mlottery.doGet - " + club + " - Error looking up member username - Err: " + exc.toString());
        } finally {
        
        try {
        rs_temp.close();
        } catch (Exception ignore) {
        }
        
        try {
        pstmt_temp.close();
        } catch (Exception ignore) {
        }
        }
        
        
        if (mship_temp.equalsIgnoreCase("Island Social") || mship_temp.equalsIgnoreCase("Social") || mship_temp.equalsIgnoreCase("Sports Membership")) {
        out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
        SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<CENTER><BR><BR><H3>Member Restricted</H3>");
        out.println("<BR>Sorry, you are restricted from submitting lottery requests due to membership type.<BR>");
        out.println("<BR><BR><a href=\"Member_announce\">Home</a>");
        out.println("</CENTER></BODY></HTML>");
        
        return;
        }
        
        }
         * 
         */


        //
        //  Get the parms passed from Member_sheet
        //
        String course = req.getParameter("course");      // get the course name passed
        String lname = req.getParameter("lname");        // get the lottery name passed
        String index = req.getParameter("index");        // get the day index value
        String lstate = req.getParameter("lstate");      // get the lottery state
        String slots = req.getParameter("slots");        // get the # of slots
        String date = req.getParameter("date");
        String day = req.getParameter("day");
        String p5 = req.getParameter("p5");

        String stime = "";
        String sfb = "";

        //
        //  Get the Display Option if specified (Mobile Devices)
        //
        if (req.getParameter("displayOpt") != null) {

            displayOpt = req.getParameter("displayOpt");
        }


        //
        //    The name of the submit button (time) has the front/back indicator appended to it ('time:fb')
        //    The value contains the time value.
        //
        Enumeration enum1 = req.getParameterNames();        // get the parm name passed

        while (enum1.hasMoreElements()) {

            String pname = (String) enum1.nextElement();

            if (pname.startsWith("time")) {

                stime = req.getParameter(pname);              //  get value: time of tee time requested (hhmm AM/PM:)

                StringTokenizer tok = new StringTokenizer(pname, ":");     // space is the default token, use ':'

                sfb = tok.nextToken();                        // skip past 'time:'
                sfb = tok.nextToken();                        // get the front/back indicator from name of submit button
            }
        }

        //
        //  Get the replacement text for the word "Lottery" if club requested one - added to club5 by Support Club Options
        //
        String lotteryText = getClub.getLotteryText(con);         // get replacement text for "Lottery" if provided  


        //
        //  Display a menu for user to either list the existing lottery requests or start a new one
        //
        if (mobile == 0) {

            out.println("<HTML>");
            out.println("<HEAD>");
            out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/web utilities/foretees2.css\" type=\"text/css\">");
            if (club.equals("oldoaks")) {
                out.println("<Title>Member Tee Time Registration Page</Title>");
            } else if (!lotteryText.equals("")) {
                out.println("<Title>Member " + lotteryText + " Page</Title>");
            } else {
                out.println("<Title>Member Lottery Registration Page</Title>");
            }
            out.println("</HEAD>");

            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
            SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
            out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

            out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
            out.println("<tr><td valign=\"top\" align=\"center\">");

            out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
            out.println("<tr>");
            out.println("<td width=\"580\" align=\"center\">");
            out.println("<font size=\"3\">");
            if (club.equals("oldoaks")) {
                out.println("<b>Tee Time Registration</b><br></font>");
            } else if (!lotteryText.equals("")) {
                out.println("<b>" + lotteryText + " Registration</b><br></font>");
            } else {
                out.println("<b>Lottery Registration</b><br></font>");
            }
            out.println("<font size=\"2\">");
            out.println("<br>To create a new request, select the 'Create New Request' button below.<br><br>");
            if (!club.equals("ridgeclub")) {
                out.println("To view all existing requests, select the 'View Other Requests' button below.");
            }
            out.println("</font></td></tr>");
            out.println("</table><br>");

            out.println("<table border=\"0\" cols=\"1\" cellpadding=\"3\">");
            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<form action=\"Member_lott\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stime + "\">");
            out.println("<input type=\"hidden\" name=\"sfb\" value=\"" + sfb + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
            out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
            out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lname + "\">");
            out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
            out.println("<input type=\"submit\" value=\"Create New Request\"></form>");
            out.println("</font></td></tr>");

            if (!club.equals("ridgeclub")) {

                out.println("<tr><td align=\"center\">");
                out.println("<font size=\"2\">");
                out.println("<form action=\"Member_mlottery\" method=\"post\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
                out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stime + "\">");
                out.println("<input type=\"hidden\" name=\"sfb\" value=\"" + sfb + "\">");
                out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
                out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
                out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lname + "\">");
                out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
                out.println("<input type=\"submit\" value=\"View Other Requests\"></form>");
                out.println("</font></td></tr>");
            }

            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<form action=\"Member_sheet\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"submit\" value=\"Return To Tee Sheet\"></form>");
            out.println("</font></td></tr>");
            out.println("</table>");

            out.println("</td>");
            out.println("</tr>");
            out.println("</table>");
            out.println("</font></center></body></html>");


        } else {

            //
            //  MOBILE user
            //
            out.println(SystemUtils.HeadTitleMobile("ForeTees Request List"));
            out.println(SystemUtils.BannerMobile());

            out.println("<div class=\"content\">");
            out.println("<div class=\"headertext\">");    // output the heading
            if (!lotteryText.equals("")) {
                out.println(lotteryText + " Request");
            } else {
                out.println("Tee Time Request");
            }
            out.println("</div>");

            out.println("<div class=\"smheadertext\">Select an Option</div>");

            out.println("<ul><li>");
            out.println("<form action=\"Member_lott\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stime + "\">");
            out.println("<input type=\"hidden\" name=\"sfb\" value=\"" + sfb + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
            out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
            out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lname + "\">");
            out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
            out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
            out.println("<input type=\"submit\" value=\"Create New Request\"></form>");
            out.println("</li>");

            if (!club.equals("ridgeclub")) {

                out.println("<li>");
                out.println("<form action=\"Member_mlottery\" method=\"post\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
                out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stime + "\">");
                out.println("<input type=\"hidden\" name=\"sfb\" value=\"" + sfb + "\">");
                out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
                out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
                out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lname + "\">");
                out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
                out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                out.println("<input type=\"submit\" value=\"View Other Requests\"></form>");
                out.println("</li>");
            }

            out.println("<li>");
            out.println("<form action=\"Member_sheet\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
            out.println("<input type=\"submit\" value=\"Return To Tee Sheet\"></form>");
            out.println("</li></ul>");

            out.println("</div>");
            out.println("</body></html>");

        }

        out.close();
        return;                 // exit and wait for reply

    }  // end of doGet

    //*****************************************************
    // Process the requests from doGet above
    //*****************************************************
    //
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
        resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        Statement stmt = null;
        Statement stmtc = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder

        if (session == null) {

            return;
        }

        Connection con = SystemUtils.getCon(session);                      // get DB connection

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
            out.close();
            return;
        }

        int mobile = 0;                      // Mobile user indicator

        //
        //   get name of club for this user
        //
        String club = (String) session.getAttribute("club");      // get club name
        String caller = (String) session.getAttribute("caller");
        String user = (String) session.getAttribute("user");
        String mtype = (String) session.getAttribute("mtype");        // get users mtype

        //
        //  get Mobile user indicator
        //
        try {
            mobile = (Integer) session.getAttribute("mobile");
        } catch (Exception ignore) {
            mobile = 0;
        }


        String index2 = "";
        String name = "";
        String lname = "";
        String num = "";
        String ampm = "";
        String sfb = "";
        String submit = "";
        String rest = "";
        String time_zone = "";
        String day_name = "";
        String course = "";
        String lcourse = "";
        String sdate = "";
        String lstate = "";
        String orig_by = "";
        String displayOpt = "";              // display option for Mobile devices

        String[] player = new String[25];
        String[] luser = new String[25];

        long date = 0;
        long lottid = 0;

        int mm = 0;
        int dd = 0;
        int yy = 0;
        int hr = 0;
        int hr24 = 0;
        int min = 0;
        int time = 0;
        int count = 0; // Count of requests
        int owner_count = 0; // Count of requests current user originated/owns
        int on_count = 0; // Count of requests current user is on
        int allow_count = 0; // Count of requests user is allowed to edit
        int fb = 0;
        int slots = 0;
        int advance_days = 0;
        int sdays = 0;
        int sdtime = 0;
        int edays = 0;
        int edtime = 0;
        int pdays = 0;
        int ptime = 0;
        int ind = 0;
        int month = 0;
        int day = 0;
        int year = 0;
        int day_num = 0;
        int fives = 0;
        int groups = 0;
        int players = 0;
        int maxPlayers = 0;
        int playersPerGroup = 0; // will be set later at runtime
        int maxGroups = 5; // currently only set here

        boolean allow = false;
        boolean partialReqs = false;
        boolean updateXs = false;
        boolean owner = false;
        boolean participant = false;

        Gson gson_obj = new Gson();

        Map request_map = new LinkedHashMap();
        request_map.put("requests", new LinkedHashMap());
        request_map.put("luser_to_lottid", new LinkedHashMap());

        //
        //  Custom - sets partialReqs to true if club wants to allow their members to join partial lottery requests.
        //           This used to be the default but too many members abused the privilege, so we changed the defualt
        //           to NOT allow members to join partial requests.
        //
        // if (club.equals("missionviejo") || club.equals("mirasolcc")) {    
        // cases 1778, 1812, 1817
        if (club.equals("missionviejo") || club.equals("whitemanor") || club.equals("northridge") || club.equals("bonniebriar")
                || club.equals("braeburncc") || club.equals("beverlygc") || club.equals("cherryhills") || club.equals("silvercreekcountryclub")
                || club.equals("sunsetridge") || club.equals("miravista") || club.equals("oaklandhills") || club.equals("chantillynationalgcc") 
                || club.startsWith("demo")) {

            partialReqs = true;     // allow mems to join
        }

        //
        //  Custom flag to allow members to update a full request if it contains any X's (requires partialReqs flag too!!)
        //
        if (club.startsWith("demo") || club.equals("silvercreekcountryclub")) {

            updateXs = true;
        }



        //
        //  request is from user to list all lottery requests
        //
        course = req.getParameter("course");      // get the course name passed
        lname = req.getParameter("lname");        // get the lottery name passed
        day_name = req.getParameter("day");
        sdate = req.getParameter("date");
        lstate = req.getParameter("lstate");

        num = req.getParameter("index");          // get the day index value

        boolean json_mode = (req.getParameter("jsonMode")) != null; // Will we output JSON or HTML

        if (json_mode) {
            // Check if the user has access to create registrations
            String accessMessage = verifyCustom.checkLotteryRegistrationAccess(session, req, con);

            if (accessMessage.length() > 0) {

                request_map.put("access_error", accessMessage);
                // Output JSON
                out.print(gson_obj.toJson(request_map));

                return;

            }
        }

        try {
            ind = Integer.parseInt(num);
            date = Long.parseLong(sdate);
        } catch (NumberFormatException e) {
            // ignore error
        }

        //
        //  Get the Display Option if specified (Mobile Devices)
        //
        if (req.getParameter("displayOpt") != null) {

            displayOpt = req.getParameter("displayOpt");
        }


        //
        //  Get the replacement text for the word "Lottery" if club requested one - added to club5 by Support Club Options
        //
        String lotteryText = getClub.getLotteryText(con);         // get replacement text for "Lottery" if provided  


        //
        //  Get today's date and then use the value passed to locate the requested date
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        if (ind > 0) {

            cal.add(Calendar.DATE, ind);                  // roll ahead 'index' days
        }

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

        month = month + 1;                            // month starts at zero

        try {
            //
            //  Determine if 5-somes are supported on this course
            //
            PreparedStatement pstmt = con.prepareStatement(
                    "SELECT fives FROM clubparm2 WHERE courseName = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, course);
            rs = pstmt.executeQuery();      // execute the prepared pstmt

            while (rs.next()) {

                fives = rs.getInt(1);      // 5-some support (0 = No)
            }
            pstmt.close();

            //
            //  Get all the lottery requests for the selected lottery, date and course
            //
            pstmt = con.prepareStatement(
                    "SELECT name, date, mm, dd, yy, day, hr, min, time, player1, player2, player3, player4, "
                    + "player5, player6, player7, player8, player9, player10, player11, player12, player13, player14, "
                    + "player15, player16, player17, player18, player19, player20, player21, player22, player23, "
                    + "player24, player25, user1, user2, user3, user4, "
                    + "user5, user6, user7, user8, user9, user10, user11, user12, user13, user14, "
                    + "user15, user16, user17, user18, user19, user20, user21, user22, user23, "
                    + "user24, user25, fb, courseName, id, groups, players, orig_by "
                    + "FROM lreqs3 "
                    + "WHERE name = ? AND date = ? "
                    + "ORDER BY date, time");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, lname);
            pstmt.setLong(2, date);

            if (json_mode) { // In json mode
                //
                //   build the json map object
                //
                request_map.put("fives", fives);
                request_map.put("todays_date", cal);
                request_map.put("partial_requests", partialReqs);
                request_map.put("lottery_text", lotteryText);
                request_map.put("course", course);
                request_map.put("index", ind);
                // Request count is added after the looping over the request recorset, below




            } else { // in Mobile or old-skin mode
                //
                //   build the HTML page for the display
                //
                if (mobile == 0) {           // if NOT Mobile user

                    out.println(SystemUtils.HeadTitle("Member Lottery Requests Page"));
                    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                    SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

                    out.println("<table border=\"0\" align=\"center\">");
                    out.println("<tr><td align=\"center\" valign=\"top\">");

                    out.println("<font size=\"3\">");
                    if (club.equals("oldoaks")) {
                        out.println("<b>Current Tee Time Requests</b><br><br>");
                    } else if (!lotteryText.equals("")) {
                        out.println("<b>Current " + lotteryText + "s</b><br><br>");
                    } else {
                        out.println("<b>Current Lottery Requests</b><br><br>");
                    }
                    out.println("<b>For " + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + " </b>");

                    out.println("</font><font size=\"2\">");
                    out.println("<form method=\"get\" action=\"Member_sheet\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + ind + "\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                    out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                    out.println("</form>");

                    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
                    out.println("<tr><td>");
                    out.println("<font size=\"2\">");
                    if (club.equals("oldoaks")) {
                        out.println("<b>To select a tee time request</b>:  Just click on the box containing the time (1st column).");
                    } else if (!lotteryText.equals("")) {
                        out.println("<b>To select a " + lotteryText + "</b>:  Just click on the box containing the time (1st column).");
                    } else {
                        out.println("<b>To select a lottery request</b>:  Just click on the box containing the time (1st column).");
                    }
                    out.println("<br><br><b>Note</b>: If the request is full and you are not already included, you will not be allowed to select it.");
                    out.println("</font></td></tr></table>");

                    out.println("</font><font size=\"1\">");
                    out.println("<br>F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;F = Front 9, &nbsp;&nbsp;B = Back 9");
                    out.println("</font>");

                    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
                    out.println("<tr bgcolor=\"#336633\"><td>");
                    out.println("<font color=\"#FFFFFF\" size=\"2\">");
                    out.println("<p align=\"center\"><u><b>Time</b></u></p>");
                    out.println("</font></td>");

                    if (!course.equals("") && course != null) {

                        out.println("<td>");
                        out.println("<font size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Course</b></u></p>");
                        out.println("</font></td>");
                    }

                    out.println("<td>");
                    out.println("<font size=\"2\">");
                    out.println("<p align=\"center\"><u><b>F/B</b></u></p>");
                    out.println("</font></td>");

                    out.println("<td>");
                    out.println("<font size=\"2\">");
                    out.println("<p align=\"center\"><u><b>Player 1</b></u></p>");
                    out.println("</font></td>");

                    out.println("<td>");
                    out.println("<font size=\"2\">");
                    out.println("<p align=\"center\"><u><b>Player 2</b></u></p>");
                    out.println("</font></td>");

                    out.println("<td>");
                    out.println("<font size=\"2\">");
                    out.println("<p align=\"center\"><u><b>Player 3</b></u></p>");
                    out.println("</font></td>");

                    out.println("<td>");
                    out.println("<font size=\"2\">");
                    out.println("<p align=\"center\"><u><b>Player 4</b></u></p>");
                    out.println("</font></td>");

                    if (fives != 0) {
                        out.println("<td>");
                        out.println("<font size=\"2\">");
                        out.println("<p align=\"center\"><u><b>Player 5</b></u></p>");
                        out.println("</font></td>");
                    }
                    out.println("</tr>");

                } else {

                    //
                    //  Output start of page for Mobile user
                    //
                    out.println(SystemUtils.HeadTitleMobile("ForeTees Tee Time Request List"));
                    out.println(SystemUtils.BannerMobile());

                    out.println("<div class=\"content\">");

                    if (!lotteryText.equals("")) {
                        out.println("<div class=\"headertext\">Current " + lotteryText + "s</div>");
                    } else {
                        out.println("<div class=\"headertext\">Current Tee Time Requests</div>");
                    }

                    out.println("<div class=\"smheadertext\">");
                    out.println("For " + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</div>");

                    out.println("<div class=\"smheadertext\">");
                    out.println("Click on time to access the request.");
                    out.println("</div>");

                    out.println("<table cellpadding=\"0\" cellspacing=\"1\" border=\"1\" id=\"ltimes\">");
                    out.println("<tr class=\"tableheader\">");

                    out.println("<td><strong>Time</td>");
                    if (!course.equals("") && course != null) {
                        out.println("<td><strong>Course</td>");
                    }
                    out.println("<td><strong>Players</td>");
                    out.println("</tr>");
                }

            }


            //
            //  Get each record and display it
            //
            count = 0;             // number of records found

            rs = pstmt.executeQuery();      // execute the prepared stmt again to start with first

            while (rs.next()) {

                count++;

                mm = rs.getInt(3);
                dd = rs.getInt(4);
                yy = rs.getInt(5);
                hr = rs.getInt(7);
                min = rs.getInt(8);
                time = rs.getInt(9);
                player[0] = rs.getString(10);
                player[1] = rs.getString(11);
                player[2] = rs.getString(12);
                player[3] = rs.getString(13);
                player[4] = rs.getString(14);
                player[5] = rs.getString(15);
                player[6] = rs.getString(16);
                player[7] = rs.getString(17);
                player[8] = rs.getString(18);
                player[9] = rs.getString(19);
                player[10] = rs.getString(20);
                player[11] = rs.getString(21);
                player[12] = rs.getString(22);
                player[13] = rs.getString(23);
                player[14] = rs.getString(24);
                player[15] = rs.getString(25);
                player[16] = rs.getString(26);
                player[17] = rs.getString(27);
                player[18] = rs.getString(28);
                player[19] = rs.getString(29);
                player[20] = rs.getString(30);
                player[21] = rs.getString(31);
                player[22] = rs.getString(32);
                player[23] = rs.getString(33);
                player[24] = rs.getString(34);
                luser[0] = rs.getString(35);
                luser[1] = rs.getString(36);
                luser[2] = rs.getString(37);
                luser[3] = rs.getString(38);
                luser[4] = rs.getString(39);
                luser[5] = rs.getString(40);
                luser[6] = rs.getString(41);
                luser[7] = rs.getString(42);
                luser[8] = rs.getString(43);
                luser[9] = rs.getString(44);
                luser[10] = rs.getString(45);
                luser[11] = rs.getString(46);
                luser[12] = rs.getString(47);
                luser[13] = rs.getString(48);
                luser[14] = rs.getString(49);
                luser[15] = rs.getString(50);
                luser[16] = rs.getString(51);
                luser[17] = rs.getString(52);
                luser[18] = rs.getString(53);
                luser[19] = rs.getString(54);
                luser[20] = rs.getString(55);
                luser[21] = rs.getString(56);
                luser[22] = rs.getString(57);
                luser[23] = rs.getString(58);
                luser[24] = rs.getString(59);
                fb = rs.getInt(60);
                lcourse = rs.getString(61);
                lottid = rs.getLong(62);
                groups = rs.getInt(63);
                players = rs.getInt(64);
                orig_by = rs.getString(65);

                hr24 = hr;
                ampm = " AM";
                if (hr == 12) {
                    ampm = " PM";
                }
                if (hr > 12) {
                    ampm = " PM";
                    hr = hr - 12;    // convert to conventional time
                }

                if (mobile == 0 && (json_mode == false)) {
                    for (int i = 0; i < player.length; i++) {
                        if (player[i].equals("")) {
                            player[i] = " ";       // make it a space for table display
                        }
                    }
                }

                //
                //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
                //
                sfb = "O";       // default Other

                if (fb == 1) {

                    sfb = "B";
                }

                if (fb == 0) {

                    sfb = "F";
                }
                
                // Only allow partial req access to Adult Females when the lott request's player1's name starts with "LCOM_Working"
                if (club.equals("marbellacc")) {
                    
                    if (mtype.equalsIgnoreCase("Adult Female") && player[0].startsWith("LCOM_Working")) {
                        partialReqs = true;
                    } else {
                        partialReqs = false;
                    }
                }

                //
                //  check if 5-somes restricted for this time
                //
                if (fives != 0) {

                    PreparedStatement pstmtr = con.prepareStatement(
                            "SELECT rest5 "
                            + "FROM teecurr2 WHERE date = ? AND time =? AND fb = ? AND courseName = ?");

                    pstmtr.clearParameters();        // clear the parms
                    pstmtr.setLong(1, date);
                    pstmtr.setInt(2, time);
                    pstmtr.setInt(3, fb);
                    pstmtr.setString(4, lcourse);
                    rs2 = pstmtr.executeQuery();      // execute the prepared stmt

                    if (rs2.next()) {

                        rest = rs2.getString(1);
                    }
                    pstmtr.close();
                }

                playersPerGroup = ((fives != 0 && rest.equals("")) ? 5 : 4);

                //
                //  get the slots value and determine the current state for this lottery
                //
                PreparedStatement pstmt7d = con.prepareStatement(
                        "SELECT sdays, sdtime, edays, edtime, pdays, ptime, slots "
                        + "FROM lottery3 WHERE name = ?");

                pstmt7d.clearParameters();          // clear the parms
                pstmt7d.setString(1, lname);

                rs2 = pstmt7d.executeQuery();      // find all matching lotteries, if any

                if (rs2.next()) {

                    sdays = rs2.getInt(1);         // days in advance to start taking requests
                    sdtime = rs2.getInt(2);
                    edays = rs2.getInt(3);         // ...stop taking reqs
                    edtime = rs2.getInt(4);
                    pdays = rs2.getInt(5);         // ....to process reqs
                    ptime = rs2.getInt(6);
                    slots = rs2.getInt(7);

                }                  // end of while
                pstmt7d.close();

                //
                //  Determine if user can select the request (must be room in request, or user already part of it)
                //
                allow = false;               // default to 'no'
                owner = false;
                participant = false;

                for (int i = 0; i < 25; i++) {

                    if (user.equalsIgnoreCase(luser[i])) {         // if user already part of request

                        participant = true;                       // User is participant of this request
                        on_count++;
                        break;
                    }
                }

                if (user.equalsIgnoreCase(orig_by)) {         // if user originated request

                    owner = true;                                // User is originator/owner of this request
                    owner_count++;

                }

                if (lstate.equals("2")) {        // if state is ok - should be to have gotten here

                    //
                    //  All clubs - check if user originated the request or is part of it
                    //

                    if (owner || participant) { // If user is owner/originator or participant in this registration

                        allow = true; // allow access

                    } else if (partialReqs == true) {    // if club allows members to join partial requests (case 1778 & 1812)

                        //
                        //  Custom to allow members to join partial requests (this is actually the original processing)
                        //

                        maxPlayers = groups * playersPerGroup;                    // max players allowed in request

                        if (players < maxPlayers && (!club.equals("missionviejo") || players < (maxPlayers - 1))) {    // if room in request for more players

                            allow = true;                              // allow user to access the request

                        } else if (updateXs == true) {     // if still not ok, but club allows members to replace X's - then see if any X's in req

                            for (int i = 0; i < player.length; i++) {

                                if (player[i].equalsIgnoreCase("x")) {

                                    allow = true; // allow access
                                    break;

                                }

                            }

                        }

                    } // end of IF club allows members to join partial requests

                }

                if (allow == true) {
                    allow_count++;
                }
                String stime = "";

                submit = "time:" + fb;       // create a name for the submit button

                if (min < 10) {
                    stime = "" + hr + ":0" + min + ampm;
                } else {
                    stime = "" + hr + ":" + min + ampm;
                }

                if (json_mode) {  // New skin / JSON mode
                    //
                    // Add request records to map object for use in json response
                    //
                    ((Map) request_map.get("requests")).put("lottid_" + lottid, new LinkedHashMap());
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("players", new LinkedHashMap());
                    for (int i = 0; i < player.length; i++) {
                        ((Map) ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).get("players")).put("player_" + i, player[i]);
                    }
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("lusers", new LinkedHashMap());
                    for (int i = 0; i < luser.length; i++) {
                        // Add a reverse lookup mapping user's login name to the lottery ID
                        if (!(((Map) request_map.get("luser_to_lottid")).containsKey(luser[i]))) {
                            ((Map) request_map.get("luser_to_lottid")).put(luser[i], lottid);
                        }
                        ((Map) ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).get("lusers")).put("luser_" + i, luser[i]);
                    }
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("lottid", lottid);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("orig_by", orig_by);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("lcourse", lcourse);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("owner", owner);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("allow", allow);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("participant", participant);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("fb", fb);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("player_count", players);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("group_count", groups);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("players_per_group", playersPerGroup);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("year", yy);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("month", mm);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("day", dd);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("hour", hr24);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("minute", min);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("time", time);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("lstate", lstate);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("sdays", sdays);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("sdtime", sdtime);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("edays", edays);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("edtime", edtime);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("pdays", pdays);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("ptime", ptime);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("slots", slots);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("stime", stime);
                    ((Map) ((Map) request_map.get("requests")).get("lottid_" + lottid)).put("displayOpt", displayOpt);


                } else {  // Mobile or old-skin (mobile == 0) mode

                    //
                    //  Build the HTML for each record found
                    //
                    if (mobile == 0) {
                        out.println("<tr>");
                    } else {
                        out.println("<tr class=\"tablerow\">");
                    }
                    if (allow == true) {
                        out.println("<form action=\"Member_lott\" method=\"post\" target=\"_top\">");
                        out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                        out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + lcourse + "\">");
                        out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lname + "\">");
                        out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
                        out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
                        out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + ind + "\">");

                        if (fives != 0 && rest.equals("")) {
                            out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
                        } else {
                            out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                        }
                    }
                    if (mobile == 0) {
                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                    } else {
                        if (allow == true) {
                            out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                        }
                        out.println("<td>");
                    }

                    if (allow == true) {

                        if (min < 10) {
                            out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":0" + min + ampm + "\">");
                        } else {
                            out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + min + ampm + "\">");
                        }

                    } else {

                        if (min < 10) {
                            out.println(hr + ":0" + min + ampm);
                        } else {
                            out.println(hr + ":" + min + ampm);
                        }
                    }

                    if (mobile == 0) {
                        out.println("</font>");
                    }

                    out.println("</td>");

                    if (mobile == 0) {                       // if NOT mobile user

                        for (int i = 0; i < (maxGroups * playersPerGroup); i += playersPerGroup) {

                            if (i == 0 || player[i] != " " || player[i + 1] != " " || player[i + 2] != " " || player[i + 3] != " " || (player[i + 4] != " " && (playersPerGroup == 5))) {

                                if (i >= playersPerGroup) {

                                    out.println("</tr><tr>");

                                    out.println("<td align=\"center\">");      // time col
                                    out.println("<font size=\"1\">");
                                    out.println("Grp " + ((i / playersPerGroup) + 1) + "</font></td>");

                                    if (!lcourse.equals("") && lcourse != null) {

                                        out.println("<td align=\"center\">");
                                        out.println("&nbsp;</td>");
                                    }

                                    out.println("<td align=\"center\">");       // f/b
                                    out.println("&nbsp;</td>");
                                } else {
                                    if (!lcourse.equals("") && lcourse != null) {

                                        out.println("<td align=\"center\">");
                                        out.println("<font size=\"2\">");
                                        out.println(lcourse);
                                        out.println("</font></td>");
                                    }
                                    out.println("<td align=\"center\">");
                                    out.println("<font size=\"2\">");
                                    out.println(sfb);
                                    out.println("</font></td>");
                                }

                                out.println("<td align=\"center\" bgcolor=\"white\">");
                                out.println("<font size=\"2\">");
                                out.println(player[i]);
                                out.println("</font></td>");

                                out.println("<td align=\"center\" bgcolor=\"white\">");
                                out.println("<font size=\"2\">");
                                out.println(player[i + 1]);
                                out.println("</font></td>");

                                out.println("<td align=\"center\" bgcolor=\"white\">");
                                out.println("<font size=\"2\">");
                                out.println(player[i + 2]);
                                out.println("</font></td>");

                                out.println("<td align=\"center\" bgcolor=\"white\">");
                                out.println("<font size=\"2\">");
                                out.println(player[i + 3]);
                                out.println("</font></td>");

                                if (playersPerGroup == 5) {
                                    out.println("<td align=\"center\" bgcolor=\"white\">");
                                    out.println("<font size=\"2\">");
                                    out.println(player[i + 4]);
                                    out.println("</font></td>");
                                }
                            }
                        }

                    } else {

                        //
                        //  Mobile user
                        //
                        out.println("<td>");

                        StringBuffer playerBfr = new StringBuffer();       // get buffer for players

                        // Place players in buffer
                        for (int i = 0; i < player.length; i++) {
                            //   Add Players
                            if (!player[i].equals("")) {
                                if (i > 0) {
                                    playerBfr.append("<BR>");
                                }
                                playerBfr.append(player[i]);
                            }
                        }

                        out.println(playerBfr.toString());     // add the players                         

                        out.println("</td>");
                    }

                    out.println("</form></tr>");
                }

            }    // end of while

            pstmt.close();

            if (json_mode) {

                request_map.put("request_count", count);
                request_map.put("owner_count", owner_count);
                request_map.put("on_count", on_count);
                request_map.put("allow_count", on_count);

                // Output JSON
                out.print(gson_obj.toJson(request_map));

            } else {

                out.println("</font></table>");
                if (count == 0) {

                    if (mobile == 0) {
                        out.println("<font size=\"2\">");
                        out.println("<p align=\"center\">No requests found for the date selected.</p>");
                        out.println("</font>");
                    } else {
                        out.println("<div class=\"smheadertext\">No requests found for this date.</div>");
                    }
                }

                out.println("</td></tr></table>");                // end of main page table

                if (mobile == 0) {

                    out.println("<table border=\"0\" align=\"center\">");
                    out.println("<tr><td>");
                    out.println("<font size=\"2\">");
                    out.println("<form method=\"get\" action=\"Member_sheet\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + ind + "\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                    out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                    out.println("</form></font>");
                    out.println("</td></tr></table>");

                    out.println("</center></font>");

                } else {

                    out.println("<ul><li>");
                    out.println("<form method=\"get\" action=\"Member_sheet\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + ind + "\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                    out.println("<input type=\"hidden\" name=\"displayOpt\" value=\"" + displayOpt + "\">");
                    out.println("<input type=\"submit\" value=\"Return to Tee Sheet\">");
                    out.println("</form></li></ul></div>");
                }

                //
                //  End of HTML page
                //
                out.println("</body></html>");

            }
            out.close();

        } catch (Exception exc) {

            if (mobile == 0) {
                out.println(SystemUtils.HeadTitle("Database Error"));
                out.println("<BODY><CENTER><BR>");
                out.println("<BR><BR><H3>Database Access Error</H3>");
                out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
                out.println("<BR>Error:" + exc.getMessage());
                out.println("<BR><BR>Please try again later.");
                out.println("<BR><BR>If problem persists, contact your golf shop staff.");
                out.println("<br><br><a href=\"Member_announce\">Return</a>");
                out.println("</CENTER></BODY></HTML>");
            } else {
                SystemUtils.displayMobileError("System Error.<BR><BR>If problem persists, contact your golf shop staff.", "", out);    // ouput error message
            }
            out.close();
        }     // end of search function
    }   // end of doPost
}
