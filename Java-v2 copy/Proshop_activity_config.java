/***************************************************************************************
 *   Proshop_activity_config: This servlet will ...
 *
 *
 *   Called by:     called by self and start w/ direct call main menu option
 *
 *
 *   Created:       3/10/2009 by Paul
 *
 *
 *   Last Updated:  
 *
 *
 *         9/25/13  Added configuration option for max_originations.
 *        10/16/12  Clubs may now only select up to 5 max players.
 *         9/12/11  Added hooks to tie this page in with the SYSCONFIG_CLUBCONFIG Proshop Limited Access option.
 *         6/03/11  Updated verbiage for disallow_joins option to not be misleading.
 *         2/22/11  Added link to open the staff list management pane, and removed the email/emailOpt fields from the setup since they have been replaced with the staff list.
 *         2/17/11  Added new option for force_singles field to handle what the original disallow_joins
 *         2/10/11  Fixed issue with Unaccompanied Guest option not saving properly.
 *         2/10/11  Commented out '?' help links since they are not currently hooked up to anything.
 *         7/12/10  Removed confuration for consec_mem and consec_pro for the time being due to new format for these values.
 *         1/18/09  Added config options for consecutive times
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
import com.foretees.common.Utilities;
import com.foretees.common.getActivity;


public class Proshop_activity_config extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    
 //*****************************************************
 // Process the a get method on this page as a post call
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

    HttpSession session = SystemUtils.verifyPro(req, out);              // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);            // get DB connection

    if (con == null) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact your club manager.");
        out.println("<BR><BR>");
        out.println("<a href=\"javascript:history.back(1)\">Return</a>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
   
    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_CLUBCONFIG", con, out)) {
        SystemUtils.restrictProshop("SYSCONFIG_CLUBCONFIG", out);
    }

    String activity_name = "";
    String club = (String)session.getAttribute("club");                 // get club name
    String templott = (String)session.getAttribute("lottery");          // get lottery support indicator
    int lottery = Integer.parseInt(templott);

    int sess_activity_id = (Integer)session.getAttribute("activity_id");

    int parent_id = 0;
    int group_id = 0;
    int activity_id = 0;
    
    String sid = req.getParameter("parent_id");
    try {
        parent_id = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    sid = req.getParameter("group_id");
    try {
        group_id = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    sid = req.getParameter("activity_id");
    try {
        activity_id = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    // debug
    out.println("<!-- parent_id=" + parent_id + ", group_id=" + group_id + ", activity_id=" + activity_id + " -->");

    if (parent_id == 0 && activity_id == 0) parent_id = sess_activity_id;
    
    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<title> \"ForeTees Proshop Announcement Page\"</title>");
  //out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\"></link>");
    out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
    out.println("<style>");
    out.println(".linkA {color:#336633; font-size:12pt; font-weight:bold}");
    out.println("</style>");
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

    SystemUtils.getProshopSubMenu(req, out, lottery);

    
    if (req.getParameter("new") != null) {
        
        out.println("<p align=center><b><font size=6 color=#336633>Configure New " + getActivity.getFullActivityName(parent_id, con) + " Activity</font></b></p>");
        buildForm(0, parent_id, con, out);
        out.println("</body></html>");
        return;

    } else {

        out.println("<p align=center><b><font size=6 color=#336633>Configure Activities</font></b></p>");

    }
    
    Statement stmt = null;
    ResultSet rs = null;

    if (activity_id > 0) {

        out.println("<p align=center><b><font size=4 color=#336633>" + getActivity.getFullActivityName(activity_id, con) + "</font></b></p>");

        out.println("<table align=center>");

        boolean found = false;

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("" +
                        "SELECT * " +
                        "FROM activities " +
                        "WHERE parent_id = '" + activity_id + "'"); //AND activity_id IN (SELECT parent_id FROM activities)

            while (rs.next()) {

                out.println("<tr><td align=center><a href=\"Proshop_activity_config?parent_id=" + activity_id + "&activity_id=" + rs.getInt("activity_id") + "\" class=linkA>" + rs.getString("activity_name") + "</a></td></tr>");

                found = true;
            }

        } catch (Exception exc) {

            Utilities.logError("getActivity.getSubActivityCount: Error=" + exc.getMessage());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

        if (found) out.println("<tr><td><br><a href=\"Proshop_activity_config?parent_id=" + activity_id + "&new\" class=linkA>Create New " + activity_name + " Activity w/ Time Sheet</a></td></tr>");

        out.println("</table>");

        // display form for specific activity
        buildForm(activity_id, parent_id, con, out);

    }/* else if (parent_id == 0) {
    
        out.println("<table align=center>");

        try {

            stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT * FROM activities WHERE parent_id = 0 ORDER BY activity_name");

            while (rs.next()) {

                out.println("<tr><td>asdf <a href=\"Proshop_activity_config?parent_id=" + rs.getInt("activity_id") + "\" class=linkA>" + rs.getString("activity_name") + "</a></td></tr>");

            }
            
            stmt.close();

        } catch (Exception exc) {

            out.println("<p>ERROR:" + exc.toString() + "</p>");

        }
        
        out.println("</table>");
        
        out.println("<br><br>");
        out.println("<center><a href=\"Proshop_activity_config?newroot\">New Activity</a></center>");
    
    }*/ else {
        
    
        // display the name of the selected activity
        try {

            stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT * FROM activities WHERE activity_id = " + sess_activity_id); // parent_id

            if ( rs.next() ) {
                
                activity_name = rs.getString("activity_name");
                out.println("<p align=center><b><font size=5 color=#336633>" + rs.getString("activity_name") + "</font></b></p>");
            }
            
            stmt.close();

        } catch (Exception exc) {

            out.println("<p>ERROR:" + exc.toString() + "</p>");

        }
        
        out.println("<table align=center>");
        
        // display any existing child activities
        try {

            stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT * FROM activities WHERE parent_id = " + sess_activity_id + " ORDER BY activity_name"); //parent_id

            while ( rs.next() ) {

                out.println("<tr><td align=center><a href=\"Proshop_activity_config?activity_id=" + rs.getInt("activity_id") + "&edit\" class=linkA>" + rs.getString("activity_name") + "</a></td></tr>");

            }
            
            stmt.close();

        } catch (Exception exc) {

            out.println("<p>ERROR:" + exc.toString() + "</p>");

        }
        
        // add link to create new child activity
        out.println("<tr><td><br><a href=\"Proshop_activity_config?parent_id=" + parent_id + "&new\" class=linkA>Create New " + activity_name + " Sub Activity</a></td></tr>");

        out.println("</table>");
        
        out.println("<br><br>");
        
        // display form with parent activity information
        buildForm(parent_id, parent_id, con, out);
        
    }
    
    out.println("</body></html>");

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

    HttpSession session = SystemUtils.verifyPro(req, out);              // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);            // get DB connection

    if (con == null) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact your club manager.");
        out.println("<BR><BR>");
        out.println("<a href=\"javascript:history.back(1)\">Return</a>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }
   
    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_CLUBCONFIG", con, out)) {
        SystemUtils.restrictProshop("SYSCONFIG_CLUBCONFIG", out);
    } 
    
    int allow_x = 0;
    int xhrs = 0;
    int forceg = 0;
    int unacompGuest = 0;
    int hndcpProSheet = 0;
    int hndcpMemSheet = 0;
    int rndsperday = 0;
    int max_originations = 0;
    int minutesbtwn = 0;
    int activity_id = 0;
    int parent_id = 0;
    int max_players = 0;
    int allow_guests = 0;

    int first_time = 0;
    int last_time = 0;
    int interval = 0;
    int alt_interval = 0;
    int disallow_joins = 0;
    int force_singles = 0;
    int use_hdcp_equiv = 0;
    int first_hr = 0;
    int first_min = 0;
    int last_hr = 0;
    int last_min = 0;
    int enabled = 0;
    int consec_mem = 0;
    int consec_pro = 0;

    double hdcp_joining_range = 0;

    // scrub all the form values
    
    String activity_name = (req.getParameter("activity_name") != null) ? req.getParameter("activity_name") : "";
    String common_name = (req.getParameter("common_name") != null) ? req.getParameter("common_name") : "";
    String contact = (req.getParameter("contact") != null) ? req.getParameter("contact") : "";
    String hdcp_equiv_name = (req.getParameter("hdcp_equiv_name") != null) ? req.getParameter("hdcp_equiv_name") : "";
    String first_ampm = (req.getParameter("first_ampm") != null) ? req.getParameter("first_ampm") : "AM";
    String last_ampm = (req.getParameter("last_ampm") != null) ? req.getParameter("last_ampm") : "PM";

    String sid = req.getParameter("allow_x");
    try {
        allow_x = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("xhrs");
    try {
        xhrs = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("forceg");
    try {
        forceg = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("unacompGuest");
    try {
        unacompGuest = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("hndcpProSheet");
    try {
        hndcpProSheet = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("hndcpMemSheet");
    try {
        hndcpMemSheet = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("rndsperday");
    try {
        rndsperday = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("max_originations");
    try {
        max_originations = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("minutesbtwn");
    try {
        minutesbtwn = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("activity_id");
    try {
        activity_id = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    sid = req.getParameter("parent_id");
    try {
        parent_id = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    sid = req.getParameter("max_players");
    try {
        max_players = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("allow_guests");
    try {
        allow_guests = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("disallow_joins");
    try {
        disallow_joins = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    sid = req.getParameter("force_singles");
    try {
        force_singles = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("interval");
    try {
        interval = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("alt_interval");
    try {
        alt_interval = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("use_hdcp_equiv");
    try {
        use_hdcp_equiv = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    sid = req.getParameter("hdcp_joining_range");
    try {
        hdcp_joining_range = Double.parseDouble(sid);
    } catch (Exception ignore) {}

    sid = req.getParameter("enabled");
    try {
        enabled = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("first_hr");
    try {
        first_hr = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("first_min");
    try {
        first_min = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("last_hr");
    try {
        last_hr = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    sid = req.getParameter("last_min");
    try {
        last_min = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    sid = req.getParameter("consec_mem");
    try {
        consec_mem = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    sid = req.getParameter("consec_pro");
    try {
        consec_pro = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    if (first_ampm.equals("PM")) first_hr += 12;

    if (last_ampm.equals("PM")) last_hr += 12;

    first_time = (first_hr * 100) + first_min;
    last_time = (last_hr * 100) + last_min;

    PreparedStatement pstmt = null;
    
    try {

        if (activity_id == 0) {

            pstmt = con.prepareStatement("" +
                    "INSERT INTO activities " +
                        "(activity_name, contact, allow_x, xhrs, forceg, unacompGuest, hndcpProSheet, hndcpMemSheet, rndsperday, max_originations, minutesbtwn, " +
                         "max_players, allow_guests, common_name, first_time, last_time, `interval`, alt_interval, disallow_joins, force_singles, use_hdcp_equiv, " +
                         "hdcp_equiv_name, hdcp_joining_range, enabled, parent_id, consec_mem, consec_pro) " +
                    "VALUES " +
                        "(?,?,?,?,?,?,?,?,?,?," +
                         "?,?,?,?,?,?,?,?,?,?," +
                         "?,?,?,?,?,?,?)");
            
        } else {

            pstmt = con.prepareStatement("" +
                    "UPDATE activities " +
                    "SET " +
                        "activity_name = ?, contact = ?, allow_x = ?, xhrs = ?, forceg = ?, unacompGuest = ?, " +
                        "hndcpProSheet = ?, hndcpMemSheet = ?, rndsperday = ?, max_originations = ?, minutesbtwn = ?, max_players = ?, " +
                        "allow_guests = ?, common_name = ?, first_time = ?, last_time = ?, `interval` = ?, " +
                        "alt_interval = ?, disallow_joins = ?, force_singles = ?, use_hdcp_equiv = ?, hdcp_equiv_name = ?, " +
                        "hdcp_joining_range = ?, enabled = ?, consec_mem = ?, consec_pro = ? " +
                    "WHERE activity_id = ?");
            
        }
        
        pstmt.clearParameters();
        pstmt.setString(1, activity_name);
        pstmt.setString(2, contact);
        pstmt.setInt(3, allow_x);
        pstmt.setInt(4, xhrs);
        pstmt.setInt(5, forceg);
        pstmt.setInt(6, unacompGuest);
        pstmt.setInt(7, hndcpProSheet);
        pstmt.setInt(8, hndcpMemSheet);
        pstmt.setInt(9, rndsperday);
        pstmt.setInt(10, max_originations);
        pstmt.setInt(11, minutesbtwn);
        pstmt.setInt(12, max_players);
        pstmt.setInt(13, allow_guests);

        pstmt.setString(14, common_name);
        pstmt.setInt(15, first_time);
        pstmt.setInt(16, last_time);
        pstmt.setInt(17, interval);
        pstmt.setInt(18, alt_interval);
        pstmt.setInt(19, disallow_joins);
        pstmt.setInt(20, force_singles);
        pstmt.setInt(21, use_hdcp_equiv);
        pstmt.setString(22, hdcp_equiv_name);
        pstmt.setDouble(23, hdcp_joining_range);
        pstmt.setInt(24, enabled);
        pstmt.setInt(25, consec_mem);
        pstmt.setInt(26, consec_pro);
        
        // if new then add parent_id else if updating then use activity_id
        pstmt.setInt(27, (activity_id != 0) ? activity_id : parent_id);
        
        pstmt.executeUpdate();
        
    } catch (Exception exc) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY><CENTER><BR>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to update the configuration for this activity.");
        out.println("<BR>Error: " + exc.toString());
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>");
        out.println("<a href=\"javascript:history.back(-1);\" target=\"bot\">Back</a>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        
    } finally {

        if (pstmt != null) {

            try {
                pstmt.close();
            } catch (SQLException sqlEx) {}

            pstmt = null;
        }
        
    }

    //out.println("Saved activity id " + activity_id + ". contact="+contact);

    doGet(req, resp);

 } // end of doPost routine
 
 
 private void buildForm(int activity_id, int parent_id, Connection con, PrintWriter out) {

    String activity_name = "";
    String common_name = "";
    String contact = "";
    String email = "";
    String hdcp_equiv_name = "";

    int use_hdcp_equiv = 0;
    int disallow_joins = 0;
    int force_singles = 0;
    int first_time = 0;
    int last_time = 0;
    int interval = 0;
    int alt_interval = 0;
    int emailOpt = 0;
    int allow_x = 0;
    int xhrs = 0;
    int allow_guests = 0;
    int max_players = 0;
    int forceg = 0;
    int unacompGuest = 0;
    int hndcpProSheet = 0;
    int hndcpMemSheet = 0;
    int rndsperday = 0;
    int minutesbtwn = 0;
    int max_originations = 0;
    int i = 0;
    //int parent_id = 0;
    int enabled = 0;
    int consec_mem = 0;
    int consec_pro = 0;

    double hdcp_joining_range = 0;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con.prepareStatement("SELECT * FROM activities WHERE activity_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, (activity_id > 0) ? activity_id : parent_id); // if activty_id is zero - then load the parent as defaults for new activity
        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            activity_name = rs.getString("activity_name");
            common_name = rs.getString("common_name");
            contact = rs.getString("contact");
            email = rs.getString("email");
            emailOpt = rs.getInt("emailOpt");
            allow_x = rs.getInt("allow_x");
            max_players = rs.getInt("max_players");
            allow_guests = rs.getInt("allow_guests");
            xhrs = rs.getInt("xhrs");
            forceg = rs.getInt("forceg");
            unacompGuest = rs.getInt("unacompGuest");
            hndcpProSheet = rs.getInt("hndcpProSheet");
            hndcpMemSheet = rs.getInt("hndcpMemSheet");
            rndsperday = rs.getInt("rndsperday");
            max_originations = rs.getInt("max_originations");
            minutesbtwn = rs.getInt("minutesbtwn");
            disallow_joins = rs.getInt("disallow_joins");
            force_singles = rs.getInt("force_singles");
            use_hdcp_equiv = rs.getInt("use_hdcp_equiv");
            hdcp_equiv_name = rs.getString("hdcp_equiv_name");
            hdcp_joining_range = rs.getDouble("hdcp_joining_range");
            first_time = rs.getInt("first_time");
            last_time = rs.getInt("last_time");
            interval = rs.getInt("interval");
            alt_interval = rs.getInt("alt_interval");
            consec_mem = rs.getInt("consec_mem");
            consec_pro = rs.getInt("consec_pro");
            enabled = rs.getInt("enabled");

        }

    } catch (Exception exc) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY><CENTER><BR>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to load the configuration data for this activity.");
        out.println("<BR>Error: " + exc.toString());
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>");
        out.println("<a href=\"Proshop_announce\" target=\"bot\">Return</a>");
        out.println("</CENTER></BODY></HTML>");
        out.close();

    } finally {

        if (rs != null) {

            try {
                rs.close();
            } catch (SQLException sqlEx) {}

            rs = null;
        }

        if (pstmt != null) {

            try {
                pstmt.close();
            } catch (SQLException sqlEx) {}

            pstmt = null;
        }

    }

    // if we loaded up the parent activity to get the defaults, then clear the activity_name value
    if (activity_id == 0) activity_name = "";

    // if not setup yet, lets use some defaults
    if (first_time == 0 && last_time == 0 && interval == 0) {

        // put default here
        first_time = 800;
        last_time = 1800;
        interval = 30;
    }

    out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" align=\"center\">");

    out.println("<form action=\"Proshop_activity_config\" method=\"post\" target=\"bot\" name=\"f\">");
    out.println("<input type=hidden name=activity_id value='" + activity_id + "'>");
    out.println("<input type=hidden name=parent_id value='" + parent_id + "'>");
    out.println("<input type=hidden name=save value=yes>");

    out.println("<tr><td colspan=3 align=center><b>General Configuration</b></td></tr>");

    // Activity Name
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_activity_name.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("Name of club <b>Activity</b>:");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("<input type=\"text\" name=\"activity_name\" value=\"" + activity_name + "\" size=\"25\" maxlength=\"40\">");
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");

    // Common Name for this Activity
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_activity_commonname.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("<b>Common Name</b> for this Activity:");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("<input type=\"text\" name=\"common_name\" value=\"" + common_name + "\" size=\"25\" maxlength=\"40\">");
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");
    
    // Name of Contact for this Activity
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_contact.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("Name of <b>Contact</b> person for this Activity:");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("<input type=\"text\" name=\"contact\" value=\"" + contact + "\" size=\"25\" maxlength=\"40\">");
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");

    // Email address for Contact person
    /*
    out.println("<tr>");

      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_email.htm', 'newwindow', 'Height=370, width=600, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("<b>Email address</b> for Activity Contact:");
         out.println("<br>Do you want to receive daily time sheets via email for back-up?");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("<input type=\"text\" name=\"email\" value=\"" + email + "\" size=\"25\" maxlength=\"40\">");
         out.println("<select size=\"1\" name=\"emailOpt\">");
         
         Common_Config.buildOption(0, "No" , emailOpt, out);
         Common_Config.buildOption(1, "Yes" , emailOpt, out);
         
         out.println("</select>&nbsp; &nbsp;(We recommend 'Yes')");
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");
*/
    out.println("<tr>");
    /*
       out.println("<td align=\"center\">");
       out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_email.htm', 'newwindow', 'Height=370, width=600, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
       out.println("?</a>");
       out.println("</td>");
    */
       out.println("<td align=\"right\" style=\"padding-right: 15px\">");
          out.println("<font size=\"2\">");
              out.println("Identify and configure <b>Club Contacts</b> with email and other options here:");
          out.println("</font>");

       out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");

             out.println("<span id=cfgStaff><a href=\"javascript: void(0)\" onclick=\"window.open ('Proshop_club_staff', 'staffCfg', 'height=775, width=900, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no')\">Add, change or view Staff List and Options</a></span>");

          out.println("</font>");
       out.println("</td>");
    out.println("</tr>");
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_disallow_joins.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */
      
      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("Restrict <b>members from joining other members'</b> reservations?");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("<select size=\"1\" name=\"disallow_joins\">");
         
         Common_Config.buildOption(0, "No" , disallow_joins, out);
         Common_Config.buildOption(1, "Yes" , disallow_joins, out);
         
         out.println("</select>");
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");


    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_force_singles.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("Allow members to <b>specify at time of booking if others are allowed to join</b> their reservation?");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("<select size=\"1\" name=\"force_singles\">");

         Common_Config.buildOption(0, "No" , force_singles, out);
         Common_Config.buildOption(1, "Yes" , force_singles, out);

         out.println("</select>&nbsp;&nbsp;&nbsp;'Force Singles Match?' checkbox");
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");

    // Unaccompanied Guests
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_unaccompanied.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */
      
      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("Do you allow <b>Unaccompanied Guests</b>?");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("<select size=\"1\" name=\"unacompGuest\">");
         
         Common_Config.buildOption(0, "No" , unacompGuest, out);
         Common_Config.buildOption(1, "Yes" , unacompGuest, out);
         
         out.println("</select>");
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");

    // Force Guest Names
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_force.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("Would you like to force members to specify <b>Guest Names</b> in their reservations?");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("<select size=\"1\" name=\"forceg\">");
         
         Common_Config.buildOption(0, "No" , forceg, out);
         Common_Config.buildOption(1, "Yes" , forceg, out);
         
         out.println("</select>");
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");

    // Use hdcp equivalent
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_activity_name.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("Use <b>Handicap Equivalent</b>?<br>If 'Yes' then provide the name to use. (e.g. Rating, Level)");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("<select size=\"1\" name=\"use_hdcp_equiv\">");
         Common_Config.buildOption(0, "No" , use_hdcp_equiv, out);
         Common_Config.buildOption(1, "Yes" , use_hdcp_equiv, out);

         out.println("</select>&nbsp; &nbsp;Name:&nbsp;");
         out.println("<input type=\"text\" name=\"hdcp_equiv_name\" value=\"" + hdcp_equiv_name + "\" size=\"17\" maxlength=\"40\">");
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");

/***** DISABLING THIS FOR NOW *****

    // Use hdcp joining range
    out.println("<tr>");
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_activity_name.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("Use <b>Handicap Range</b> for Game Finder?<br>If 'Yes' then provide the range to use. (.5 means +/- .5)");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("<select size=\"1\" name=\"hdcp_joining_range\">");
         Common_Config.buildOption(0, "No" , hdcp_joining_range, out);
         Common_Config.buildOption(1, "Yes" , hdcp_joining_range, out);

         out.println("</select>&nbsp; &nbsp;Name:&nbsp;");
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");
*/

    // Display Hndcp on Pro Time Sheets
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_hndcp.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("If applicable, do you want <b>Members' Handicaps</b> displayed on:");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("<select size=\"1\" name=\"hndcpPS\">");
         
         Common_Config.buildOption(0, "No" , hndcpProSheet, out);
         Common_Config.buildOption(1, "Yes", hndcpProSheet, out);
         
         out.println("</select>");
         out.println("&nbsp;&nbsp;Proshop's Time Sheets?<br>");
         out.println("<select size=\"1\" name=\"hndcpMS\">");
         
         Common_Config.buildOption(0, "No" , hndcpMemSheet, out);
         Common_Config.buildOption(1, "Yes", hndcpMemSheet, out);
         
         out.println("</select>");
         out.println("&nbsp;&nbsp;Member's Time Sheets?");
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");

    // Allow X's and hours in advance to remove them
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_x.htm', 'newwindow', 'Height=240, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("Do you wish to allow members to reserve player positions using an <b>'X'</b>?");
         out.println("<br>If yes, how many X's can members specify per reservation?  (zero = none):");
         out.println("<br>Also if yes, how many hours in advance should we delete all X's?");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("X's&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"allow_x\">");
         
         for (i = 0; i <= 4; i++) {
             
             Common_Config.buildOption(i, i, allow_x, out);
             
         }
         
         out.println("</select>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;hours&nbsp;&nbsp;");
         out.println("<input type=\"text\" name=\"xhrs\" value=\"" + xhrs + "\" size=\"3\" maxlength=\"3\">&nbsp;&nbsp;(1 - 240)");
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");

    // Number of times a member can play this activity in one day - and minutes between each time
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_rounds.htm', 'newwindow', 'Height=240, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("How many times do you allow members to play per day (be part of a reservation, zero = no limit)?");
         out.println("<br>If more than 1, how many minutes do you require between reservations (zero = no limit)?");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("Times&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"rndsperday\">");
         
         for (i = 0; i <= 10; i++) {
             
             Common_Config.buildOption(i, i, rndsperday, out);
             
         }
         
         out.println("</select>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;minutes&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"minutesbtwn\">");
         
         for (i = 0; i <= 240; i=i+5) {
             
             Common_Config.buildOption(i, i, minutesbtwn, out);
             
         }
         
         out.println("</select>");
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");

    // Max originations per day
    out.println("<tr>");
      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("How many reservations can a member <span style=\"font-weight:bold;\">originate</span> each day?");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("<select size=\"1\" name=\"max_originations\">");
         
         Common_Config.buildOption(0, "Disabled" , max_originations, out);
         Common_Config.buildOption(1, "1" , max_originations, out);
         Common_Config.buildOption(2, "2" , max_originations, out);
         Common_Config.buildOption(3, "3" , max_originations, out);
         Common_Config.buildOption(4, "4" , max_originations, out);
         Common_Config.buildOption(5, "5" , max_originations, out);
         
         out.println("</select>");
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");

    // Consecutive Times (Member)
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_consecutive.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("How many <b>consecutive times can members</b> reserve at one time?");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
      out.println("Please contact ForeTees Support to make adjustments to this value.");
      out.println("<input type=\"hidden\" name=\"consec_mem\" value=\"0\">");
      /*
         out.println("<select size=\"1\" name=\"consec_mem\">");

         for (i = 0; i <= 12; i++)
             Common_Config.buildOption(i, i, consec_mem, out);

         out.println("</select>");
         out.println("</font>");
      */
      out.println("</td>");
    out.println("</tr>");

    // Consecutive Times (Member)
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_consecutive.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("How manu <b>consecutive times can proshop users</b> reserve at one time?");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
      out.println("Please contact ForeTees Support to make adjustments to this value.");
      out.println("<input type=\"hidden\" name=\"consec_pro\" value=\"0\">");
      /*
         out.println("<select size=\"1\" name=\"consec_pro\">");

         for (i = 0; i <= 12; i++)
             Common_Config.buildOption(i, i, consec_pro, out);

         out.println("</select>");
         out.println("</font>");
      */
      out.println("</td>");
    out.println("</tr>");


    // Divider
    out.println("<tr><td colspan=3 align=center><b>Time Sheet Configuration</b></td></tr>");


    // Max players
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_maxplayers.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("Specify the <b>Maximum Players</b> allowed per time slot:");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("<select size=\"1\" name=\"max_players\">");

         for (i = 1; i <= 5; i++)
             Common_Config.buildOption(i, i, max_players, out);

         out.println("</select>");
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");

    // First time
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_contact.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("<b>Earliest time</b> to appear on time sheets:");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         //out.println("<input type=\"text\" name=\"first_time\" value=\"" + first_time + "\" size=\"10\" maxlength=\"10\">");
         Common_Config.displayHrMinToD(first_time, "", "first_hr", "first_min", "first_ampm", out);
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");

    // Last time
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_contact.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("<b>Latest time</b> to appear on time sheets:");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         //out.println("<input type=\"text\" name=\"last_time\" value=\"" + last_time + "\" size=\"10\" maxlength=\"10\">");
         Common_Config.displayHrMinToD(last_time, "", "last_hr", "last_min", "last_ampm", out);
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");

    // Intervals
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_contact.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("<b>Interval</b> in minutes between each time:");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("<input type=\"text\" name=\"interval\" value=\"" + interval + "\" size=\"3\" maxlength=\"3\">");
         out.println("&nbsp; &nbsp;Alternating Interval:&nbsp;&nbsp; ");
         out.println("<input type=\"text\" name=\"alt_interval\" value=\"" + alt_interval + "\" size=\"3\" maxlength=\"3\">");
         out.println("&nbsp;(optional)</font>");
      out.println("</td>");
    out.println("</tr>");

    // Enabled
    out.println("<tr>");
    /*
      out.println("<td align=\"center\">");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_force.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");
    */

      out.println("<td align=\"right\" style=\"padding-right: 15px\">");
         out.println("<font size=\"2\">");
         out.println("Is this Activity <b>enabled</b>?<br>If 'No', then time sheets will NOT be built each night!");
         out.println("</font>");

      out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
         out.println("<select size=\"1\" name=\"enabled\">");

         Common_Config.buildOption(0, "No" , enabled, out);
         Common_Config.buildOption(1, "Yes" , enabled, out);

         out.println("</select>");
         out.println("</font>");
      out.println("</td>");
    out.println("</tr>");

    out.println("</table>");
    
    out.println("<br><center><input type=submit value=\" Save \"></center>");

    out.println("</form>");

    out.println("<br><center><button onclick=\"location.href='Proshop_announce'\"> Home </button></center><br>");

 }
 
} // end servlet public class
