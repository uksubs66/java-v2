
/***************************************************************************************
 *   Common_guestdb:  This servlet will process common functions for the guest tracking system
 *
 *
 *   created: 1/04/2010   Brad K.
 *
 *   last updated:       ******* keep this accurate *******
 *
 *    5/30/13   Updated page to utilize the new skin on the member side, and made a couple slight adjustments to the page that apply to both proshop/member side.
 *    5/29/13   Fixed issue that caused Hotel users to not be able to add a new tracked guest via the modal window popup.
 *    1/25/13   All guest management pages will now return the screen to the last member you clicked after editing a guest, their hosts, or returning from the merge page.    
 *   11/27/12   Tweak iframe resize code
 *    3/08/12   Updated instructions box to only display info on merging and red text for Proshop users, and added a message about the "Remove" option for members.
 *    1/16/12   Moved makeFieldMap to com.foretees.formUtils
 *   12/17/11   Added json_mode and Map object generation to modal code path, for new skin member slot pages
 *    9/27/11   Added a note to the guest info form to inform users that adding a guest email will all the guest to receive tee time notifications.
 *    4/22/11   Added a .trim() for first name when adding a new guest since it was missed in the original code and causing issues.
 *    4/13/11   Fixed an issue preventing users from closing out of the modal window after adding a new guest with an apostrophe in their name.
 *    9/23/10   Changes to accomodate for 'TBA' guest names coming through on the mobile side.
 *    9/21/10   Changes to accommodate hotel side 
 *    9/13/10   Guest information fields will now be merged as well when missing from target_id record but populated in merged_id record.
 *    9/10/10   Adjusted layout of merge guest page
 *    9/09/10   Fixes and tweaks to 9/8 changes
 *    9/08/10   Added manual merge capabilty to proshop side guest management page
 *    6/17/10   If 'TBA' option is on, and user is adding a guest via the modal window, do not allow first/last name to be '?' or 'TBA'
 *    6/16/10   Changes to handle 'TBA' option
 *    4/19/10   Removed isConfigured() method.  Use Utilities.isGuestTrackingConfigured() now.
 *    4/15/10   Numerous assorted changes
 *    1/15/10   Numerous methods added (isConfigured, isGuestTypeConfigured, buildUIDString, checkGuestReqInfo, displayGuestForm, printGuestInfo, updateGuest)
 *    1/04/10   Initial creation
 *
 ***************************************************************************************
 */
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json

// foretees imports
import com.foretees.common.ProcessConstants;
import com.foretees.common.alphaTable;
import com.foretees.common.Utilities;
import com.foretees.common.getClub;
import com.foretees.common.formUtil;

public class Common_guestdb extends HttpServlet {

    static String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
        resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server
        resp.setContentType("text/html");

        PrintWriter out = resp.getWriter();

        Connection con = null;                 // init DB objects

        boolean json_mode = ((req.getParameter("json_mode") != null) ? true : false);
        Gson gson_obj = new Gson();

        //Statement stmt = null;
        //PreparedStatement pstmt = null;
        //PreparedStatement pstmt2 = null;

        //ResultSet rs = null;
        //ResultSet rs2 = null;

        //ArrayList<String> activity_names = new ArrayList<String>();
        //ArrayList<Integer> activity_ids = new ArrayList<Integer>();

        HttpSession session = null;

        //
        // This servlet can be called by both Proshop and Member users - find out which
        //
        session = req.getSession(false);  // Get user's session object (no new one)

        if (session == null) {

            out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
            out.println("<BODY><CENTER>");
            out.println("<BR><H2>Access Error</H2><BR>");
            out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
            out.println("<BR>This site requires the use of Cookies for security purposes.");
            out.println("<BR>We use them to verify your session and prevent unauthorized access.");
            out.println("<BR><BR>Please check your 'Privacy' settings, under 'Tools', 'Internet Options'");
            out.println("<BR>(for MS Internet Explorer).  This must be set to 'Medium High' or lower.");
            out.println("<BR><BR>");
            out.println("<BR>If you have changed or verified the setting above and still receive this message,");
            out.println("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
            out.println("<BR>Provide your name and the name of your club.  Thank you.");
            out.println("<BR><BR>");
            out.println("<a href=\"Logout\" target=\"_top\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            return;
        }

        // get user id so we know if proshop or member
        String user = (String) session.getAttribute("user");     // get username ('proshop' or member's username)
        //String club = (String)session.getAttribute("club");

        con = SystemUtils.getCon(session);            // get DB connection

        if (con == null) {

            out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Database Connection Error</H3>");
            out.println("<BR><BR>Unable to connect to the Database.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR><a href=\"Admin_announce\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            return;
        }

        boolean isProshop = false;
        boolean isHotel = false;

        if (ProcessConstants.isProshopUser(user)) {
            isProshop = true;
        } else if (Utilities.isHotelUser(user, con)) {
            isProshop = true;
            isHotel = true;
        }


        int sess_activity_id = (Integer) session.getAttribute("activity_id");;                    // ID # of current activity

        String result_msg = "";
        String caller = "";

        if (req.getParameter("caller") != null) {
            caller = req.getParameter("caller");
        }
/*
 *  CAN NOT OUTPUT SUB MENU CODE HERE!!!!
 *
        if (!json_mode) {
            if (!caller.equals("modal") && !isHotel) {
                if (isProshop) {
                    int lottery = Integer.parseInt((String) session.getAttribute("lottery"));        // get lottery support indicator
                    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
                } else {
                    String sess_caller = (String) session.getAttribute("caller");
                    SystemUtils.getMemberSubMenu(req, out, sess_caller);        // required to allow submenus on this page
                }
            }
        }
*/
        if (req.getParameter("removeGuest") != null) {
        }

        if (req.getParameter("submitGuestInfo") != null) {

            if (json_mode) {
                out.print(gson_obj.toJson(updateGuest(req, isProshop, sess_activity_id, user, out, con, true))); // output json string
                return;
            } else {
                result_msg = updateGuest(req, isProshop, sess_activity_id, user, out, con);

                if (caller.equals("sheet") || caller.equals("modal") || caller.equals("mobile")) {

                    out.println(SystemUtils.HeadTitle("Guest Management"));
                    out.println("<body style=\"margin: 0; background: #F5F5DC;\">");

                    if (isProshop) {
                        if (!isHotel) {
                            int lottery = Integer.parseInt((String) session.getAttribute("lottery"));        // get lottery support indicator
                            SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
                        }
                    } else {
                        String sess_caller = (String) session.getAttribute("caller");
                        SystemUtils.getMemberSubMenu(req, out, sess_caller);        // required to allow submenus on this page
                    }

                    out.println("<br><h2 align=center>Guest Management</h2><br><br>");
                    out.println("<center>");

                    if (caller.equals("sheet")) {
                        out.println(result_msg);
                        out.println("<br><br><button onclick=\"window.close()\">Close</button>");
                        return;
                    } else if (caller.equals("modal")) {

                        out.println("<style>");
                        out.println(".btnNorm {");
                        out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
                        out.println("  background: #99CC66;");
                        out.println("  width: 80px;");
                        out.println("}");
                        out.println("</style>");

                        printModalScripts(out);

                        String target_guest = "";
                        StringTokenizer tempTok = new StringTokenizer(result_msg, "|");

                        if (tempTok.countTokens() == 2) {
                            target_guest = tempTok.nextToken();
                            result_msg = tempTok.nextToken();

                            out.println(result_msg);
                            out.println("<br><br><button onclick=\"passguest('" + target_guest.replace("'", "\\'") + "');\">Continue</button>");

                        } else {
                            out.println(result_msg);
                        }

                        return;

                    } else if (caller.equals("mobile")) {
                    } else {
                        out.println(result_msg);
                    }

                    out.println("</center></body>");
                }
            }
        }

        if (req.getParameter("mergeGuest") != null) {

            int target_id = (req.getParameter("target_id") != null ? Integer.parseInt(req.getParameter("target_id")) : 0);
            int merged_id = (req.getParameter("merged_id") != null ? Integer.parseInt(req.getParameter("merged_id")) : 0);
            String target_unique_id = (req.getParameter("target_unique_id") != null ? req.getParameter("target_unique_id") : "");

            if (target_id != 0 && merged_id != 0 && (!target_unique_id.equals("") || caller.equals("manual"))) {

                if (!mergeGuestID(target_unique_id, target_id, merged_id, con)) {     // If no error encountered

                    if (caller.equals("sheet")) {
                        out.println("Guests merged successfully! (Sheet must be reloaded for changes to be visible)");
                        out.println("<br><br><button onclick=\"window.close()\">Close</button>");
                        return;
                    } else if (caller.equals("manual")) {
                        result_msg = "Guests merged successfully! To merge additional quests, select another from the list to the right.";
                    }

                } else {

                    if (caller.equals("sheet")) {
                        out.println("An error was encountered while merging the guests. Please try again, and contact ForeTees Support if the problem persists.");
                        out.println("<br><br><button onclick=\"window.close()\">Close</button>");
                        return;
                    } else if (caller.equals("manual")) {
                        result_msg = "An error was encountered while merging the guests.  Please try again, and contact ForeTees Support if the problem persists.";
                    }
                }
            }
        }
        /*
        if (req.getParameter("removeGuest") != null && isProshop) {
        removeGuest(req, con);
        }
         */

        if (req.getParameter("modal") != null || caller.equals("modal")) {
            if (json_mode) {
                out.print(gson_obj.toJson(loadModal(req, session, sess_activity_id, user, out, con, true))); // output json string
            } else {
                loadModal(req, session, sess_activity_id, user, out, con);
            }
            return;
        }

        if (req.getParameter("guestForm") != null || caller.equals("sheet")) {
            displayGuestForm(req, isProshop, sess_activity_id, out, con);
            return;
        }

        if (req.getParameter("mergeForm") != null) {
            displayMergeForm(req, session, sess_activity_id, isProshop, user, result_msg, out, con);
            return;
        }

        if (req.getParameter("mergeInfo") != null) {
            displayMergeInfo(req, sess_activity_id, isProshop, out, con);
            return;
        }

        if (req.getParameter("hostForm") != null) {
            displayHostForm(req, session, isProshop, user, out, con);
            return;
        }

        if (req.getParameter("hostList") != null) {
            displayHostList(req, isProshop, out, con);
            return;
        }

        // Default action is to display the Guest Management page
        displayGuestConfig(req, session, sess_activity_id, isProshop, user, result_msg, out, con);

    }

    /**
     * isGuestTypeConfigured - Determines if the passed guest type is currently set to be used with the guest database system
     *
     * @param gtype Name of guest type to check
     * @param activity_id Activity id the guest type is associated with
     * @param con Connection to club database
     *
     * @return result - True if guest type is set to be used with the guest database system, false if not
     */
    public static boolean isGuestTypeConfigured(String gtype, int activity_id, Connection con) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean result = false;

        int use_guestdb = 0;

        // First make sure the guest database system is active for this activity id
        if (Utilities.isGuestTrackingConfigured(activity_id, con)) {

            try {

                // Get the use_guestdb value from the guest5 db table to see if this guest type uses the guest database system
                pstmt = con.prepareStatement("SELECT use_guestdb FROM guest5 WHERE guest = ? AND activity_id = ?");
                pstmt.clearParameters();
                pstmt.setString(1, gtype);
                pstmt.setInt(2, activity_id);

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    use_guestdb = rs.getInt("use_guestdb");

                    if (use_guestdb == 1) {
                        result = true;
                    }
                }

                pstmt.close();

            } catch (Exception exc) {
                result = false;
            }
        }

        return result;
    }

    /**
     * buildUIDString - Builds a String in the following format:  "uid_type: unique_id"
     *
     * @param guest_id ID of guestdb entry for this guest
     * @param con Connection to club database
     *
     * @return guest_uid - Completed guest unique id string
     */
    public static String buildUIDString(int guest_id, Connection con) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String guest_uid = "";
        String unique_id = "";
        String uid_type = "";

        try {
            pstmt = con.prepareStatement("SELECT unique_id, uid_type FROM guestdb_data WHERE guest_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, guest_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                if (rs.getString("unique_id") == null) {
                    unique_id = "";
                } else {
                    unique_id = rs.getString("unique_id");
                }

                uid_type = rs.getString("uid_type");

                if (unique_id.equals("")) {
                    guest_uid = "No UID Found";
                } else {
                    guest_uid = (uid_type.equals("") ? "UID" : uid_type) + ": " + unique_id;
                }
            }

            pstmt.close();

        } catch (Exception exc) {
            guest_uid = "Error Finding UID";
        }

        return guest_uid;
    }

    /**
     * checkGuestReqInfo - Returns whether or not the supplied guest_id record contains values for all required information fields under the given activity_id
     *
     * @param guest_id ID of guest to be checked
     * @param activity_id Activity id to check required field settings under
     * @param con Connection to club database
     *
     * @return missingInfo - True if any required fields do not contain values, false if no problems found
     */
    public static boolean checkGuestReqInfo(int guest_id, int activity_id, boolean isProshop, Connection con, PrintWriter out) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean missingInfo = false;

        String uid = "N";
        String name = "R";
        String email = "N";
        String phone = "N";
        String address = "N";
        String gender = "N";
        String hdcp_num = "N";
        String hdcp_index = "N";
        String home_club = "N";

        try {

            // First, determine which fields are required.
            pstmt = con.prepareStatement("SELECT * FROM guestdb WHERE activity_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, activity_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                uid = rs.getString("uid");
                name = "R";
                email = rs.getString("email");
                phone = rs.getString("phone");
                address = rs.getString("address");
                gender = rs.getString("gender");
                hdcp_num = rs.getString("hdcp_num");
                hdcp_index = rs.getString("hdcp_index");
                home_club = rs.getString("home_club");
            }

            pstmt.close();

            // If a required field was found, proceed to check all required fields to see if any are unpopulated.  If found, short circuit process and return false
            if (uid.equals("R") || name.equals("R") || email.equals("R") || phone.equals("R") || address.equals("R")
                    || gender.equals("R") || hdcp_num.equals("R") || hdcp_index.equals("R") || home_club.equals("R")) {

                pstmt = con.prepareStatement("SELECT * FROM guestdb_data WHERE guest_id = ?");
                pstmt.clearParameters();
                pstmt.setInt(1, guest_id);

                rs = pstmt.executeQuery();

                if (rs.next()) {

                    // Check all required values to see if any are unpopulated
                    if ((isProshop && uid.equals("R") && (rs.getString("unique_id") == null || rs.getString("unique_id").equals("")))
                            || (name.equals("R") && (rs.getString("name_first").equals("") || rs.getString("name_last").equals("")))
                            || (email.equals("R") && rs.getString("email1").equals("") && rs.getString("email2").equals(""))
                            || (phone.equals("R") && rs.getString("phone1").equals("") & rs.getString("phone2").equals(""))
                            || (address.equals("R") && (rs.getString("address1").equals("") || rs.getString("city").equals("") || rs.getString("state").equals("") || rs.getString("zip").equals("")))
                            || (gender.equals("R") && rs.getString("gender").equals(""))
                            || (hdcp_num.equals("R") && rs.getString("hdcp_num").equals(""))
                            || (hdcp_index.equals("R") && rs.getDouble("hdcp_index") == -99)
                            || (home_club.equals("R") && rs.getString("home_club").equals(""))) {

                        // At least one required field is missing info, set return value
                        missingInfo = true;
                    }
                }

                pstmt.close();
            }

        } catch (Exception exc) {
            out.println("Error: " + exc.getMessage());
            missingInfo = true;
        }

        return missingInfo;
    }

    /**
     * displayGuestConfig - Prints the main Guest Management page that contains a list of all guests currently in the system.
     *
     * @param req Request object
     * @param session User's login session
     * @param activity_id Current activity id
     * @param isProshop True if proshp user, false otherwise
     * @param user Username of current user
     * @param result Result message from any processing that ran prior to this method (e.g. guest update/merge processing)
     * @param out Output stream
     * @param con Connection to club database
     */
    public static void displayGuestConfig(HttpServletRequest req, HttpSession session, int activity_id, boolean isProshop, String user, String result, PrintWriter out, Connection con) {

        // Declare Vars
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sescaller = (String) session.getAttribute("caller");
        String club = (String)session.getAttribute("club");      // get club name
        String templott = "";        // get lottery support indicator
        
        String clubName = Utilities.getClubName(con, true);        // get the full name of this club

        boolean isHotel = Utilities.isHotelUser(user, con);

        int lottery = 0;

        if (isProshop && !isHotel) {
            templott = (String) session.getAttribute("lottery");
            lottery = Integer.parseInt(templott);
        }

        String name_last = "";
        String name_first = "";
        String name_mi = "";
        String unique_id = "";
        String uid_type = "";
        String email1 = "";
        String phone1 = "";
        String gender = "";
        String home_club = "";
        String result_msg = "";
        String textStyle = "";
        String letter = "";
        String letterSort = "";

        int host_id = 0;
        int guest_id = 0;
        int guest_count = 0;
        int inact = 0;
        int count = 0;

        boolean guestFound = false;

        // If a member selected to remove themselves as a host for a guest, do so
        if (!isProshop && req.getParameter("removeHost") != null) {

            host_id = Integer.parseInt(req.getParameter("removeHost"));

            if (host_id > 0) {
                try {
                    pstmt = con.prepareStatement("DELETE FROM guestdb_hosts WHERE host_id = ?");
                    pstmt.clearParameters();
                    pstmt.setInt(1, host_id);

                    count = pstmt.executeUpdate();

                    pstmt.close();

                    if (count > 0) {
                        result_msg = "Host removed successfully.";
                    }

                } catch (Exception exc) {
                    result_msg = "Error while removing host.";
                }
            }

            // Reset host_id
            host_id = 0;
        }

        if (!isProshop) {
            
            //  Build the top of the page
            Common_skin.outputHeader(club, activity_id, "Manage Guests", true, out, req);
            Common_skin.outputBody(club, activity_id, out, req);
            Common_skin.outputTopNav(req, club, activity_id, out, con);
            Common_skin.outputBanner(club, activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
            Common_skin.outputSubNav(club, activity_id, out, con, req);
            Common_skin.outputPageStart(club, activity_id, out, req);
            Common_skin.outputBreadCrumb(club, activity_id, out, "Manage Guests", req);
            Common_skin.outputLogo(club, activity_id, out, req);

            out.println("<div class=\"preContentFix\"></div>"); // clear the float

        } else {
            out.println(SystemUtils.HeadTitle("Guest Management"));
        }

        out.println("<!-- ******** BEGIN LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->"
                + "<script type=\"text/javascript\">var lwmwLinkedBy=\"LiknoWebModalWindows [1]\",lwmwName=\"foretees-modal\",lwmwBN=\"128\";awmAltUrl=\"\";</script>"
                + "<script charset=\"UTF-8\" src=\"/" + rev + "/web%20utilities/foretees-modal.js\" type=\"text/javascript\"></script>"
                + "<!-- ******** END LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->");

        out.println("<script type=\"text/javascript\">");
        out.println("<!--");
        out.println("function resizeIFrame(divHeight, iframeName) {");
        out.println("document.getElementById(iframeName).height = divHeight;");
        out.println("}");
        out.println("// -->");
        out.println("</script>");

        out.println("<style>");

        out.println("a.rwDtaAct {");
        out.println("  color: #336633;");
        out.println("}");

        out.println("table.lst {");
        out.println("  background: #CCCCAA;");
        out.println("  border-width: 2px;");
        out.println("  border-style: solid;");
        out.println("  border-color: #8B8970;");
        out.println("  padding: 0px;");
        out.println("  width: 100%;");
        out.println("}");

        out.println("th.colHdr {");
        out.println("  white-space: nowrap;");
        out.println("  text-align: center;");
        out.println("  font-size: .8em;");
        out.println("  vertical-align: top;");
        out.println("  padding-left: 5px;");
        out.println("  padding-right: 5px;");
        out.println("  border-style: solid;");
        out.println("  border-width: 2px;");
        out.println("  border-color: #ccccaa;");
        out.println("  background: #8B8970;");
        out.println("}");

        out.println("td.mainTd {");
        out.println("  text-align: left;");
        out.println("  font-size: .8em;");
        out.println("  padding-left: 5px;");
        out.println("  padding-right: 5px;");
        out.println("}");

        out.println("td.rwDta {");
        out.println("  text-align: left;");
        out.println("  font-size: .8em;");
        out.println("  vertical-align: top;");
        out.println("  padding: 0px;");
        out.println("  border-style: solid;");
        out.println("  border-width: 1px;");
        out.println("  border-color: #e5e5d9;");
        out.println("}");

        out.println("tr.rwDtaOdd {");
        out.println("  border-width: 0px;");
        out.println("  border-style: solid;");
        out.println("  padding: 0px;");
        out.println("  background: #F5F5DC;");
        out.println("}");

        out.println("tr.rwDtaEve {");
        out.println("  border-width: 0px;");
        out.println("  border-style: solid;");
        out.println("  padding: 0px;");
        out.println("  background: #CDCDB4;");
        out.println("}");

        out.println(".btnNorm {");
        out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
        out.println("  background: #99CC66;");
        out.println("  width: 100px;");
        out.println("}");

        out.println(".btnLtr {");
        out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
        out.println("  background: #99CC66;");
        out.println("  width: 20px;");
        out.println("}");

        out.println("</style>");

        out.println("<body>");
        
        // THIS WAS COMMENTED OUT - REENABLING IT 11/27/12
        if (isProshop) {
        SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
        } else {
        SystemUtils.getMemberSubMenu(req, out, sescaller);        // required to allow submenus on this page
        }
        
        // If guest database system is not configured and active, display rejection message
        if (!Utilities.isGuestTrackingConfigured(activity_id, con)) {

            out.println("<br><h2 align=\"center\">Guest Tracking Inactive</h2>");
            out.println("<br><br><center>The guest tracking system is not currently active.</center>");

            if (isProshop) {
                out.println("<br><br><center>This feature can be activated from the Club Setup > Club Options page.</center>");
                out.println("<br><br><center><button onclick=\"location.href='Proshop_announce'\">Home</button></center>");
            } else {
                out.println("<br><br><center><button onclick=\"location.href='Member_announce'\">Home</button></center>");
            }

        } else {

            if (isProshop) {
                
                out.println("<br><h2 align=center>Guest Management</h2>");

                out.println("<table width=500 align=center border=\"1\" bgcolor=\"#336633\">");
                out.println("<tr><td align=\"left\"><font color=white size=2><CENTER><b>Instructions:</b></CENTER>");
                out.println("<br>-To <b>Add a new guest</b>: click the 'Add New Guest' button.<br>"
                        + "-To <b>Edit a guest</b>: select 'Edit' by the guest's name in the list below.<br>"
                        + "-To <b>Add additional hosts for a guest</b>: select 'Hosts' by the guest's name in the list below.<br>"
                        + "-To <b>Merge duplicate guest records</b>: select 'Merge' by the name of the guest you would like to merge other records into.<br>"
                        + "-Names <b>printed in <span style=\"color:red;\">red</span></b> indicate that required information is missing for that guest.");
                out.println("</td></tr></table><br>");

            } else {
                
                out.println("<div class=\"main_instructions\"><strong>Instructions</strong>: ");
                out.println(""
                        + "To <b>Add a new guest</b>: click the \"Add New Guest\" button. "
                        + "To <b>Edit a guest</b>: select \"Edit\" by the guest's name in the list below. "
                        + "To <b>Add additional hosts for a guest</b>: select \"Hosts\" by the guest's name in the list below. "
                        + "To <b>Remove yourself as a host of a guest</b>: select 'Remove' by the guest's name below.");

                out.println("</div>");
            }
            
            /*
            if (isProshop) {
            out.println("<br>-Use the letters below to View All, or display only last names of a certain letter.");
            }
             */

            /*  Don't use alphabet filtering box for now.  Will re-enable later if requested
            if (isProshop) {
            
            // Print letter selection table
            out.print("<form action=\"Common_guestdb\" method=\"POST\">");
            out.print("<table align=\"center\" border=\"2\" bgcolor=\"#F5F5DC\">");
            out.print("<tr bgcolor=\"#336633\">");
            out.print("<td colspan=\"6\" align=\"center\">");
            out.print("<font color=\"#ffffff\" size=\"2\"><b>Member List</b>");
            out.print("</font></td></tr><tr>");
            out.print("<td colspan=\"6\" align=\"center\"><font size=\"2\">Last name begins with:</font>");
            out.print("</td></tr>");
            
            // A-F
            out.println("<tr>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"A\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"B\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"C\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"D\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"E\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"F\"></td>");
            out.println("</tr>");
            
            // G-L
            out.println("<tr>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"G\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"H\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"I\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"J\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"K\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"L\"></td>");
            out.println("</tr>");
            
            // M-R
            out.println("<tr>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"M\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"N\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"O\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"P\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"Q\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"R\"></td>");
            out.println("</tr>");
            
            // S-X
            out.println("<tr>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"S\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"T\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"U\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"V\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"W\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"X\"></td>");
            out.println("</tr>");
            
            // Y-Z
            out.println("<tr>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"Y\"></td>");
            out.println("<td align=\"center\"><input type=\"submit\" class=\"btnLtr\" name=\"letter\" value=\"Z\"></td>");
            out.println("</tr>");
            
            out.print("<tr><td align=\"center\" colspan=\"6\">");
            out.print("<font size=\"2\"><input type=\"submit\" class=\"btnNorm\" value=\"View All\" name=\"letter\">");
            out.print("</tr></td>");
            
            out.print("</table></form>");
            }
             */

            out.println("<div style=\"margin-left:auto; margin-right:auto; width:100%;\">");
            out.println("<table style=\"margin-left:auto; margin-right:auto; width:70%;\">");

            if (!result_msg.equals("")) {
                out.println("<tr><td align=\"center\"><h3>" + result_msg + "</h3><br></td></tr>");
            }
            out.println("<form action=\"Common_guestdb\" method=\"POST\" name=\"guestBtnForm\">");
            out.println("<tr><td align=\"center\">");
            out.println("<input type=\"hidden\" name=\"caller\" value=\"mgmt\">");
            if (isHotel) {
                out.println("<input type=\"button\" class=\"btnNorm\" name=\"btnHome\" value=\"Home\" title=\"Return to main page\" onclick=\"location.href='Hotel_select'\">&nbsp;&nbsp;");
            } else if (isProshop) {
                out.println("<input type=\"button\" class=\"btnNorm\" name=\"btnHome\" value=\"Home\" title=\"Return to announcement page\" onclick=\"location.href='Proshop_announce'\">&nbsp;&nbsp;");
            } else {
                out.println("<input type=\"button\" class=\"btnNorm\" name=\"btnHome\" value=\"Home\" title=\"Return to announcement page\" onclick=\"location.href='Member_announce'\">&nbsp;&nbsp;");
            }

            out.println("<input type=\"submit\" class=\"btnNorm\" name=\"guestForm\" value=\"Add New Guest\" title=\"Add a new guest to the tracking system\">&nbsp;&nbsp;");

            if (!isProshop) {
                //  If user is a member, display a link to the Partner List page
                out.println("<input type=\"button\" class=\"btnNorm\" value=\"Partner List\" onclick=\"location.href='Member_partner'\">");
            }

            out.println("</td></tr>");
            out.println("</form>");

            if (!result.equals("")) {
                out.println("<tr><td align=\"center\"><br>");
                out.println(result);
                out.println("</td></tr>");
            }

            out.println("<tr><td align=\"center\"><br>");

            // Print Guest List Table
            out.println("<table class=\"lst\">");

            // Header row
            out.println("<tr>");
            out.println("<th class=\"colHdr\">Last Name</th>");
            out.println("<th class=\"colHdr\">First Name</th>");
            out.println("<th class=\"colHdr\">MI</th>");
            out.println("<th class=\"colHdr\">Actions</th>");
            if (isProshop) {
                out.println("<th class=\"colHdr\">UID Type</th>");
                out.println("<th class=\"colHdr\">Unique ID</th>");
            }
            out.println("<th class=\"colHdr\">Email</th>");
            out.println("<th class=\"colHdr\">Phone</th>");
            out.println("<th class=\"colHdr\">Gender</th>");
            out.println("<th class=\"colHdr\">Home Club</th>");
            out.println("<th class=\"colHdr\">Status</th>");
            out.println("</tr>");

            // Grab member records from database.  If Proshop, grab all records, if Member, only grab records they have bindings to
            try {
                // Statement is going to differ depending on if the user is a proshop user or member
                if (isProshop) {

                    // First see if a letter filter was passed
                    if (req.getParameter("letter") != null && !req.getParameter("letter").equals("") && !req.getParameter("letter").equals("View All")) {
                        letter = req.getParameter("letter");
                        letterSort = "WHERE name_last like '" + letter + "%' ";
                    }

                    pstmt = con.prepareStatement(
                            "SELECT d.guest_id, d.name_last, d.name_first, d.name_mi, d.uid_type, d.unique_id, d.email1, d.phone1, d.gender, d.home_club, d.inact "
                            + "FROM guestdb_data d "
                            + letterSort
                            + "ORDER BY d.name_last, d.name_first, d.email1");
                } else {    // Members
                    pstmt = con.prepareStatement(
                            "SELECT d.guest_id, d.name_last, d.name_first, d.name_mi, d.email1, d.phone1, d.gender, d.home_club, d.inact, h.host_id "
                            + "FROM guestdb_data d "
                            + "LEFT OUTER JOIN guestdb_hosts h ON d.guest_id = h.guest_id "
                            + "WHERE d.inact = 0 AND h.username = ? "
                            + "ORDER BY d.name_last, d.name_first, d.email1");

                    pstmt.clearParameters();
                    pstmt.setString(1, user);
                }

                rs = pstmt.executeQuery();

                while (rs.next()) {

                    // Mark that we've found at least one guest
                    guestFound = true;

                    // Grab all info for current guest
                    guest_id = rs.getInt("d.guest_id");
                    name_last = rs.getString("d.name_last");
                    name_first = rs.getString("d.name_first");
                    name_mi = rs.getString("d.name_mi");
                    if (isProshop) {     // If proshop, grab the unique id and unique id type as well
                        uid_type = rs.getString("d.uid_type");
                        unique_id = (rs.getString("d.unique_id") != null ? rs.getString("d.unique_id") : "");
                    }
                    email1 = rs.getString("d.email1");
                    phone1 = rs.getString("d.phone1");
                    gender = rs.getString("d.gender");
                    home_club = rs.getString("d.home_club");
                    inact = rs.getInt("d.inact");
                    if (!isProshop) {       // if not proshop, grab the host_id value as well
                        host_id = rs.getInt("h.host_id");
                    }

                    if (checkGuestReqInfo(guest_id, activity_id, isProshop, con, out)) {
                        textStyle = " style=\"color:red;\"";
                    } else {
                        textStyle = "";
                    }

                    guest_count++;

                    // Print table row for this member
                    if (guest_count % 2 == 0) {
                        out.println("<tr class=\"rwDtaEve\">");
                    } else {
                        out.println("<tr class=\"rwDtaOdd\">");
                    }
                    out.println("<td class=\"mainTd\"" + textStyle + "><a name=\"" + guest_id + "\">" + (name_last.equals("") ? "&nbsp;" : name_last) + "</a></td>");
                    out.println("<td class=\"mainTd\"" + textStyle + ">" + (name_first.equals("") ? "&nbsp;" : name_first) + "</td>");
                    out.println("<td class=\"mainTd\"" + textStyle + ">" + (name_mi.equals("") ? "&nbsp;" : name_mi) + "</td>");
                    out.println("<td class=\"mainTd\" style=\"text-align: center;\">");
                    out.println("<a class=\"rwDtaAct\" href=\"Common_guestdb?guestForm&caller=mgmt&guest_id=" + guest_id + "\" title=\"Edit this guest's information\">Edit</a> | ");
                    out.println("<a class=\"rwDtaAct\" href=\"Common_guestdb?hostForm&guest_id=" + guest_id + "\" title=\"Select which members will be able to see this guest during the booking process\">Hosts</a> | ");

                    if (isProshop) {
                        out.println("<a class=\"rwDtaAct\" href=\"Common_guestdb?mergeForm&guest_id=" + guest_id + "\" title=\"Merge other guest records into this one.\">Merge</a>");
                    }

                    // If a member, also display a link to remove the host binding between the user and this guest.
                    if (!isProshop && host_id > 0) {
                        out.println("<a class=\"rwDtaAct\" href=\"Common_guestdb?removeHost=" + host_id + "\" title=\"Remove yourself as a host for this guest\" onclick=\"return confirm('Warning: If you remove yourself as a host for this guest, you will no longer be able to select them when booking a reservation!');\">Remove</a>");
                    } /*else if (isProshop) {
                    out.println("<a class=\"rwDtaAct\" href=\"Common_guestdb?removeGuest&guest_id=" + guest_id + "\" title=\"Remove this guest from the system.\" onclick=\"confirm('Warning: This guest will be permanently removed, and any currently booked rounds with this guest will no longer be associated with them!')\">Remove</a>");
                    }*/

                    out.println("</td>");
                    if (isProshop) {
                        out.println("<td class=\"mainTd\">" + (uid_type.equals("") ? "&nbsp;" : uid_type) + "</td>");
                        out.println("<td class=\"mainTd\">" + (unique_id.equals("") ? "&nbsp;" : unique_id) + "</td>");
                    }
                    out.println("<td class=\"mainTd\">" + (email1.equals("") ? "&nbsp;" : email1) + "</td>");
                    out.println("<td class=\"mainTd\">" + (phone1.equals("") ? "&nbsp;" : phone1) + "</td>");
                    out.println("<td class=\"mainTd\">" + (gender.equals("") ? "&nbsp;" : gender) + "</td>");
                    out.println("<td class=\"mainTd\">" + (home_club.equals("") ? "&nbsp;" : home_club) + "</td>");
                    out.println("<td class=\"mainTd\">" + (inact == 0 ? "A" : "I") + "</td>");
                    out.println("</tr>");
                }

                pstmt.close();

            } catch (Exception exc) {
                out.println("Error encountered: " + exc.getMessage());
            }

            if (!guestFound) {
                out.println("<tr><td align=\"center\" colspan=\"11\">No guests found.</td></tr>");
            }

            out.println("</form>");

            out.println("</table>");    // end guest list table

            out.println("</td></tr>");

            out.println("<form action=\"Common_guestdb\" method=\"POST\" name=\"guestBtnForm\">");
            out.println("<tr><td align=\"center\"><br>");
            out.println("<input type=\"hidden\" name=\"caller\" value=\"mgmt\">");

            if (isHotel) {
                out.println("<input type=\"button\" class=\"btnNorm\" name=\"btnHome\" value=\"Home\" title=\"Return to main page\" onclick=\"location.href='Hotel_select'\">&nbsp;&nbsp;");
            } else if (isProshop) {
                out.println("<input type=\"button\" class=\"btnNorm\" name=\"btnHome\" value=\"Home\" title=\"Return to announcement page\" onclick=\"location.href='Proshop_announce'\">&nbsp;&nbsp;");
            } else {
                out.println("<input type=\"button\" class=\"btnNorm\" name=\"btnHome\" value=\"Home\" title=\"Return to announcement page\" onclick=\"location.href='Member_announce'\">&nbsp;&nbsp;");
            }

            out.println("<input type=\"submit\" class=\"btnNorm\" name=\"guestForm\" value=\"Add New Guest\" title=\"Add a new guest to the tracking system\">&nbsp;&nbsp;");

            if (!isProshop) {
                //  If user is a member, display a link to the Partner List page
                out.println("<input type=\"button\" class=\"btnNorm\" value=\"Partner List\" onclick=\"location.href='Member_partner'\">");
            }

            out.println("</table></div></body></html>");
        }
    }

    private void displayMergeForm(HttpServletRequest req, HttpSession session, int activity_id, boolean isProshop, String user, String result_msg, PrintWriter out, Connection con) {


        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean error = false;
        boolean existingGuest = false;

        String unique_id = "";
        String uid_type = "";
        String name_pre = "";
        String name_first = "";
        String name_mi = "";
        String name_last = "";
        String name_suf = "";
        String email1 = "";
        String email2 = "";
        String phone1 = "";
        String phone2 = "";
        String address1 = "";
        String address2 = "";
        String city = "";
        String state = "";
        String zip = "";
        String gender = "";
        String hdcp_num = "";
        String home_club = "";

        String guest_name = "";

        String use_uid = "";
        String use_name = "";
        String use_email = "";
        String use_phone = "";
        String use_address = "";
        String use_gender = "";
        String use_hdcp_num = "";
        String use_hdcp_index = "";
        String use_home_club = "";

        double hdcp_index = -99;

        int guest_id = 0;
        int email_bounced1 = 0;
        int email_bounced2 = 0;
        int emailOpt = 0;
        int inact = 0;

        int dup_guest_id = 0;

        int lottery = 0;

        String templott = "";
        String sescaller = "";

        boolean isHotel = Utilities.isHotelUser(user, con);

        if (isProshop && !isHotel) {
            templott = (String) session.getAttribute("lottery");
            lottery = Integer.parseInt(templott);
        }

        sescaller = (String) session.getAttribute("caller");

        if (req.getParameter("guest_id") != null) {
            guest_id = Integer.parseInt(req.getParameter("guest_id"));
        }

        if (guest_id != 0) {

            // Look up and display the originally selected guest
            try {
                pstmt = con.prepareStatement(
                        "SELECT *, CONCAT(name_first, ' ', IF(name_mi != '', CONCAT(name_mi, ' '), ''), name_last) as guest_name "
                        + "FROM guestdb_data "
                        + "WHERE guest_id = ?");
                pstmt.clearParameters();
                pstmt.setInt(1, guest_id);

                rs = pstmt.executeQuery();

                if (rs.next()) {

                    if (rs.getString("unique_id") != null) {
                        unique_id = rs.getString("unique_id");
                    }
                    guest_name = rs.getString("guest_name");
                    uid_type = rs.getString("uid_type");
                    name_pre = rs.getString("name_pre");
                    name_first = rs.getString("name_first");
                    name_mi = rs.getString("name_mi");
                    name_last = rs.getString("name_last");
                    name_suf = rs.getString("name_suf");
                    email1 = rs.getString("email1");
                    email2 = rs.getString("email2");
                    email_bounced1 = rs.getInt("email_bounced1");
                    email_bounced2 = rs.getInt("email_bounced2");
                    emailOpt = rs.getInt("emailOpt");
                    phone1 = rs.getString("phone1");
                    phone2 = rs.getString("phone2");
                    address1 = rs.getString("address1");
                    address2 = rs.getString("address2");
                    city = rs.getString("city");
                    state = rs.getString("state");
                    zip = rs.getString("zip");
                    gender = rs.getString("gender");
                    hdcp_num = rs.getString("hdcp_num");
                    hdcp_index = rs.getDouble("hdcp_index");
                    home_club = rs.getString("home_club");
                    inact = rs.getInt("inact");
                }

                pstmt.close();
            } catch (Exception exc) {
                out.println("<!-- Error encountered gathering guest (2) information from DB: " + exc.getMessage() + " -->");
                error = true;
            }
        }

        // Gather ask/req values for all information fields
        try {

            pstmt = con.prepareStatement("SELECT * FROM guestdb WHERE activity_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, activity_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                use_uid = rs.getString("uid");
                use_name = rs.getString("name");
                use_email = rs.getString("email");
                use_phone = rs.getString("phone");
                use_address = rs.getString("address");
                use_gender = rs.getString("gender");
                use_hdcp_num = rs.getString("hdcp_num");
                use_hdcp_index = rs.getString("hdcp_index");
                use_home_club = rs.getString("home_club");
            }

            pstmt.close();

        } catch (Exception exc) {
            out.println("<!-- Error encountered gathering guest tracking settings: " + exc.getMessage() + " -->");
            error = true;
        }

        out.println(SystemUtils.HeadTitle("Merge Guest Record"));

        out.println("<script type=\"text/javascript\">");
        out.println("<!--");
        out.println("function resizeIFrame(divHeight, iframeName) {");
        out.println("document.getElementById(iframeName).height = divHeight;");
        out.println("}");
        out.println("function passguest(nameinfo) {");
        out.println("  var f = document.forms['mergeGuestForm'];");
        out.println("  var fr = document.getElementById('mergeinfoiframe');");
        out.println("  array = nameinfo.split(':');"); // split string (guest_name, guest_id)
        out.println("  var dup_guest_id = array[1];");
        out.println("  f.merged_id.value = dup_guest_id;");
        out.println("  fr.src = 'Common_guestdb?mergeInfo&dup_guest_id=' + dup_guest_id;");
        out.println("  f.mergeBtn.disabled = false;");
        out.println("}");
        out.println("function submitAndUpdateBtn() {");
        out.println("  var f = document.forms['mergeGuestForm'];");
        out.println("  var answer = confirm('Warning: Merging two guests is an irreversible process!  "
                + "Please be absolutely sure the selected guests are correct before performing the merge.  "
                + "Are you sure you wish to proceed?');");
        out.println("  if (answer) {");
        out.println("    f.mergeBtn.value='Please Wait...';");
        out.println("    f.submit();");
        out.println("  }");
        out.println("}");
        out.println("// -->");
        out.println("</script>");

        out.println("<style>");
        out.println(".btnNorm {");
        out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
        out.println("  background: #99CC66;");
        out.println("  width: 80px;");
        out.println("}");
        out.println(".btnWide {");
        out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
        out.println("  background: #99CC66;");
        out.println("  width: 120px;");
        out.println("}");
        out.println("</style>");

        out.println("<body bgcolor=\"#F5F5DC\">");

        if (!isHotel) {
            if (isProshop) {
                SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
            } else {
                SystemUtils.getMemberSubMenu(req, out, sescaller);        // required to allow submenus on this page
            }
        }

        out.println("<br><h2 align=center>Merge Guests</h2>");

        out.println("<table width=\"550\" align=\"center\" border=\"1\" bgcolor=\"#336633\">");
        out.println("<tr><td align=\"center\"><font color=white size=2>");
        out.println("<b>Instructions:</b><br><br>");
        out.println("This page allows duplicate guest records to be merged together.  The guest record on the left <b>will be kept</b>, "
                + "and the selected member on the right will have its play history, hosts, and information <b>merged into the left record</b>.  "
                + "Guest information will only be merged if that data is missing in the left record.  Some information fields cannot be merged. Once the "
                + "merge is completed, the record on the right will be <b>removed from the system</b>.<br><br>"
                + "<b>**WARNING**</b><br>This process cannot be reversed!!<br>Please double-check the selected guest records before confirming the merge!");
        out.println("</font></td></tr>");
        out.println("</table><br><br>");

        out.println("<table align=\"center\" bgcolor=\"#F5F5DC\">");

        out.println("<tr><td align=\"center\" colspan=\"3\">" + result_msg + "</td></tr>");

        out.println("<tr><td align=\"center\" valign=\"top\"><br><br>");

        // Print left half
        out.println("<table><tr><td align=\"left\" valign=\"top\" width=\"300\">");

        // Print existing record (left side)
        out.println("<table border=\"0\" bgcolor=\"#F5F5DC\">");

        out.println("<tr><td alignt=\"center\"><b>This record will be kept</b><br><br></td></tr>");

        // Unique ID
        if (isProshop && use_uid.equals("R")) {
            out.println("<tr><td align=\"left\">Unique ID:&nbsp; " + unique_id + (use_uid.equals("R") ? (unique_id.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
            //out.println("<tr><td align=\"left\">Unique ID Type: " + uid_type + "</td></tr>");
        }

        // Name
        if (!use_name.equals("N")) {

            String name_display = (!name_pre.equals("") ? name_pre + " " : "") + name_first + " " + (!name_mi.equals("") ? name_mi + " " : "") + name_last
                    + (!name_suf.equals("") ? " " + name_suf : "") + (name_first.equals("") || name_last.equals("") ? "<font color=red> * </font>" : " *");

            out.println("<tr><td align=\"left\">Name:<b>&nbsp; " + name_display + "</b></td></tr>");

            /*
            out.println("<tr><td align=\"left\">Prefix: " + name_pre + "</td></tr>");
            out.println("<tr><td align=\"left\">First Name: " + name_first + (use_name.equals("R") ? (name_first.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
            out.println("<tr><td align=\"left\">Middle Initial: " + name_mi + "</td></tr>");
            out.println("<tr><td align=\"left\">Last Name: " + name_last + (use_name.equals("R") ? (name_last.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
            out.println("<tr><td align=\"left\">Suffix: " + name_suf + "</td></tr>");
             */
        }

        // Email
        if (!use_email.equals("N")) {
            out.println("<tr><td align=\"left\">Email 1:<b>&nbsp; " + email1 + (use_email.equals("R") ? (email1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
            out.println("<tr><td align=\"left\">Email 2:<b>&nbsp; " + email2 + "</b></td></tr>");
        }

        // Phone
        if (!use_phone.equals("N")) {
            out.println("<tr><td align=\"left\">Phone 1:<b>&nbsp; " + phone1 + (use_phone.equals("R") ? (phone1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
            out.println("<tr><td align=\"left\">Phone 2:<b>&nbsp; " + phone2 + "</b></td></tr>");
        }

        // Address
        if (!use_address.equals("N")) {
            out.println("<tr><td align=\"left\">Address 1:<b>&nbsp; " + address1 + (use_address.equals("R") ? (address1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
            out.println("<tr><td align=\"left\">Address 2:<b>&nbsp; " + address2 + "</b></td></tr>");
            out.println("<tr><td align=\"left\">City:<b>&nbsp; " + city + (use_address.equals("R") ? (city.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
            out.println("<tr><td align=\"left\">State:<b>&nbsp; " + state + (use_address.equals("R") ? (state.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
            out.println("<tr><td align=\"left\">Zip:<b>&nbsp; " + zip + (use_address.equals("R") ? (zip.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
        }

        // Gender
        if (!use_gender.equals("N")) {
            out.println("<tr><td align=\"left\">Gender:<b>&nbsp; " + gender + (use_gender.equals("R") ? (gender.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
        }

        // Handicap Number
        if (!use_hdcp_num.equals("N")) {
            out.println("<tr><td align=\"left\">Handicap Number:<b>&nbsp; " + hdcp_num + (use_hdcp_num.equals("R") ? (hdcp_num.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
        }

        // Handicap Index
        if (!use_hdcp_index.equals("N")) {
            out.println("<tr><td align=\"left\">Handicap Index:<b>&nbsp; " + (hdcp_index == -99 ? "" : hdcp_index) + (use_hdcp_index.equals("R") ? (hdcp_index == -99 ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
        }

        // Home Club
        if (!use_home_club.equals("N")) {
            out.println("<tr><td align=\"left\">Home Club:<b>&nbsp; " + home_club + (use_home_club.equals("R") ? (home_club.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
        }

        // Inactive Button
        if (isProshop && guest_id > 0) {
            out.println("<tr><td align=\"left\">Inactive:<b>&nbsp; " + (inact == 1 ? "Yes" : "No") + "</b></td></tr>");
        }
        out.println("</table>");

        out.println("</td>");

        // Print merged guest iframe
        out.println("<td align=\"center\" valign=\"top\" width=\"300\">");
        out.println("<iframe id=\"mergeinfoiframe\" src=\"Common_guestdb?mergeInfo&guest_id=" + guest_id + "\" width=\"300px\" scrolling=no frameborder=no></iframe>");
        out.println("</td>");

        // Close left half table
        out.println("</td></tr></table>");

        // Print namelist selection box
        out.println("<td align=\"center\" valign=\"top\"><br>");
        alphaTable.guestdbList(user, 2, activity_id, guest_id, out, con);
        out.println("</td>");

        out.println("</form>");

        out.println("</tr>");

        // Print form and submit button to merge to this guest
        out.println("<form action=\"Common_guestdb\" method=\"POST\" name=\"mergeGuestForm\">");
        out.println("<input type=\"hidden\" name=\"caller\" value=\"manual\">");
        out.println("<input type=\"hidden\" name=\"guest_id\" value=\"" + guest_id + "\">");
        out.println("<input type=\"hidden\" name=\"target_id\" value=\"" + guest_id + "\">");
        out.println("<input type=\"hidden\" name=\"merged_id\" value=\"" + dup_guest_id + "\">");
        out.println("<input type=\"hidden\" name=\"mergeForm\">");
        out.println("<input type=\"hidden\" name=\"mergeGuest\">");
        out.println("<tr><td align=\"center\" colspan=\"2\">");
        out.println("<input class=\"btnWide\" type=\"button\" name=\"mergeBtn\" value=\"Merge Guests\" disabled onclick=\"submitAndUpdateBtn()\">");
        out.println("</td></tr>");
        out.println("</form>");

        out.println("<tr><td align=\"center\" colspan=\"2\"><br><button class=\"btnNorm\" onclick=\"location.href='Common_guestdb" + (guest_id != 0 ? "#" + guest_id : "") + "'\">Return</button></td></tr>");
        out.println("</table>");
        out.println("</body></html>");
    }

    private void displayMergeInfo(HttpServletRequest req, int activity_id, boolean isProshop, PrintWriter out, Connection con) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean error = false;
        boolean existingGuest = false;

        String unique_id = "";
        String uid_type = "";
        String name_pre = "";
        String name_first = "";
        String name_mi = "";
        String name_last = "";
        String name_suf = "";
        String email1 = "";
        String email2 = "";
        String phone1 = "";
        String phone2 = "";
        String address1 = "";
        String address2 = "";
        String city = "";
        String state = "";
        String zip = "";
        String gender = "";
        String hdcp_num = "";
        String home_club = "";

        String dup_unique_id = "";
        String dup_uid_type = "";
        String dup_name_pre = "";
        String dup_name_first = "";
        String dup_name_mi = "";
        String dup_name_last = "";
        String dup_name_suf = "";
        String dup_email1 = "";
        String dup_email2 = "";
        String dup_phone1 = "";
        String dup_phone2 = "";
        String dup_address1 = "";
        String dup_address2 = "";
        String dup_city = "";
        String dup_state = "";
        String dup_zip = "";
        String dup_gender = "";
        String dup_hdcp_num = "";
        String dup_home_club = "";

        String caller = "";
        String guest_name = "";
        String dup_guest_name = "";

        String use_uid = "";
        String use_name = "";
        String use_email = "";
        String use_phone = "";
        String use_address = "";
        String use_gender = "";
        String use_hdcp_num = "";
        String use_hdcp_index = "";
        String use_home_club = "";

        double hdcp_index = -99;
        double dup_hdcp_index = -99;

        int guest_id = 0;
        int email_bounced1 = 0;
        int email_bounced2 = 0;
        int emailOpt = 0;
        int inact = 0;

        int dup_guest_id = 0;
        int dup_email_bounced1 = 0;
        int dup_email_bounced2 = 0;
        int dup_emailOpt = 0;
        int dup_inact = 0;

        String templott = "";        // get lottery support indicator

        if (req.getParameter("dup_guest_id") != null) {
            dup_guest_id = Integer.parseInt(req.getParameter("dup_guest_id"));
        }

        if (dup_guest_id != 0) {

            // Look up and display the originally selected guest
            try {
                pstmt = con.prepareStatement(
                        "SELECT *, CONCAT(name_first, ' ', IF(name_mi != '', CONCAT(name_mi, ' '), ''), name_last) as guest_name "
                        + "FROM guestdb_data "
                        + "WHERE guest_id = ?");
                pstmt.clearParameters();
                pstmt.setInt(1, dup_guest_id);

                rs = pstmt.executeQuery();

                if (rs.next()) {

                    if (rs.getString("unique_id") != null) {
                        dup_unique_id = rs.getString("unique_id");
                    }
                    dup_guest_name = rs.getString("guest_name");
                    dup_uid_type = rs.getString("uid_type");
                    dup_name_pre = rs.getString("name_pre");
                    dup_name_first = rs.getString("name_first");
                    dup_name_mi = rs.getString("name_mi");
                    dup_name_last = rs.getString("name_last");
                    dup_name_suf = rs.getString("name_suf");
                    dup_email1 = rs.getString("email1");
                    dup_email2 = rs.getString("email2");
                    dup_email_bounced1 = rs.getInt("email_bounced1");
                    dup_email_bounced2 = rs.getInt("email_bounced2");
                    dup_emailOpt = rs.getInt("emailOpt");
                    dup_phone1 = rs.getString("phone1");
                    dup_phone2 = rs.getString("phone2");
                    dup_address1 = rs.getString("address1");
                    dup_address2 = rs.getString("address2");
                    dup_city = rs.getString("city");
                    dup_state = rs.getString("state");
                    dup_zip = rs.getString("zip");
                    dup_gender = rs.getString("gender");
                    dup_hdcp_num = rs.getString("hdcp_num");
                    dup_hdcp_index = rs.getDouble("hdcp_index");
                    dup_home_club = rs.getString("home_club");
                    dup_inact = rs.getInt("inact");
                }

                pstmt.close();
            } catch (Exception exc) {
                out.println("<!-- Error encountered gathering guest (2) information from DB: " + exc.getMessage() + " -->");
                error = true;
            }
        }

        // Gather ask/req values for all information fields
        try {

            pstmt = con.prepareStatement("SELECT * FROM guestdb WHERE activity_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, activity_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                use_uid = rs.getString("uid");
                use_name = rs.getString("name");
                use_email = rs.getString("email");
                use_phone = rs.getString("phone");
                use_address = rs.getString("address");
                use_gender = rs.getString("gender");
                use_hdcp_num = rs.getString("hdcp_num");
                use_hdcp_index = rs.getString("hdcp_index");
                use_home_club = rs.getString("home_club");
            }

            pstmt.close();

        } catch (Exception exc) {
            out.println("<!-- Error encountered gathering guest tracking settings: " + exc.getMessage() + " -->");
            error = true;
        }

        out.println("<style type=\"text/css\">");
        out.println("html body {");
        //out.println("  margin-left:0;");
        //out.println("  margin-right:0;");
        out.println("  margin-top:0;");
        out.println("  margin-bottom:0;");
        out.println("}");
        out.println("</style>");

        out.println("<body onload=\"parent.window.resizeIFrame(document.getElementById('mergeinfoiframediv').offsetHeight + 50, 'mergeinfoiframe');\" bgcolor=\"#F5F5DC\" text=\"#000000\">");
        out.println("<div id=\"mergeinfoiframediv\">");

        // Data collected, now print out the two options
        out.println("<table style=\"border:0px; padding-left:10px; padding-right:10px; background-color:#F5F5DC; vertical-align:top;\">");

        if (dup_guest_id != 0) {

            out.println("<tr><td alignt=\"center\"><b>This record will be merged</b><br><br></td></tr>");

            // Unique ID
            if (isProshop && use_uid.equals("R")) {
                out.println("<tr><td align=\"left\">Unique ID:<b>&nbsp; " + dup_unique_id + (use_uid.equals("R") ? (dup_unique_id.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
                //out.println("<tr><td align=\"left\">Unique ID Type: " + dup_uid_type + "</td></tr>");
            }

            // Name
            if (!use_name.equals("N")) {

                String dup_name_display = (!dup_name_pre.equals("") ? dup_name_pre + " " : "") + dup_name_first + " " + (!dup_name_mi.equals("") ? dup_name_mi + " " : "")
                        + dup_name_last + (dup_name_suf.equals("") ? " " + dup_name_suf : "") + (dup_name_first.equals("") || dup_name_last.equals("") ? "<font color=red> * </font>" : " *");

                out.println("<tr><td align=\"left\">Name:<b>&nbsp; " + dup_name_display + "</b></td></tr>");

                /*
                out.println("<tr><td align=\"left\">Prefix: " + dup_name_pre + "</td></tr>");
                out.println("<tr><td align=\"left\">First Name: " + dup_name_first + (use_name.equals("R") ? (dup_name_first.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                out.println("<tr><td align=\"left\">Middle Initial: " + dup_name_mi + "</td></tr>");
                out.println("<tr><td align=\"left\">Last Name: " + dup_name_last + (use_name.equals("R") ? (dup_name_last.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                out.println("<tr><td align=\"left\">Suffix: " + dup_name_suf + "</td></tr>");
                 */
            }

            // Email
            if (!use_email.equals("N")) {
                out.println("<tr><td align=\"left\">Email 1:<b>&nbsp; " + dup_email1 + (use_email.equals("R") ? (dup_email1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
                out.println("<tr><td align=\"left\">Email 2:<b>&nbsp; " + dup_email2 + "</b></td></tr>");
            }

            // Phone
            if (!use_phone.equals("N")) {
                out.println("<tr><td align=\"left\">Phone 1:<b>&nbsp; " + dup_phone1 + (use_phone.equals("R") ? (dup_phone1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
                out.println("<tr><td align=\"left\">Phone 2:<b>&nbsp; " + dup_phone2 + "</b></td></tr>");
            }

            // Address
            if (!use_address.equals("N")) {
                out.println("<tr><td align=\"left\">Address 1:<b>&nbsp; " + dup_address1 + (use_address.equals("R") ? (dup_address1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
                out.println("<tr><td align=\"left\">Address 2:<b>&nbsp; " + dup_address2 + "</b></td></tr>");
                out.println("<tr><td align=\"left\">City:<b>&nbsp; " + dup_city + (use_address.equals("R") ? (dup_city.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
                out.println("<tr><td align=\"left\">State:<b>&nbsp; " + dup_state + (use_address.equals("R") ? (dup_state.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
                out.println("<tr><td align=\"left\">Zip:<b>&nbsp; " + dup_zip + (use_address.equals("R") ? (dup_zip.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
            }

            // Gender
            if (!use_gender.equals("N")) {
                out.println("<tr><td align=\"left\">Gender:<b>&nbsp; " + dup_gender + (use_gender.equals("R") ? (dup_gender.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
            }

            // Handicap Number
            if (!use_hdcp_num.equals("N")) {
                out.println("<tr><td align=\"left\">Handicap Number:<b>&nbsp; " + dup_hdcp_num + (use_hdcp_num.equals("R") ? (dup_hdcp_num.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
            }

            // Handicap Index
            if (!use_hdcp_index.equals("N")) {
                out.println("<tr><td align=\"left\">Handicap Index:<b>&nbsp; " + (dup_hdcp_index == -99 ? "" : hdcp_index) + (use_hdcp_index.equals("R") ? (dup_hdcp_index == -99 ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
            }

            // Home Club
            if (!use_home_club.equals("N")) {
                out.println("<tr><td align=\"left\">Home Club:<b>&nbsp; " + dup_home_club + (use_home_club.equals("R") ? (dup_home_club.equals("") ? "<font color=red> *</font>" : " *") : "") + "</b></td></tr>");
            }

            // Inactive Button
            if (isProshop && dup_guest_id > 0) {
                out.println("<tr><td align=\"left\">Inactive:<b>&nbsp; " + (dup_inact == 1 ? "Yes" : "No") + "</b></td></tr>");
            }
        }

        out.println("</table>");

    }

    private void displayHostForm(HttpServletRequest req, HttpSession session, boolean isProshop, String user, PrintWriter out, Connection con) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sescaller = (String) session.getAttribute("caller");
        String templott = "";        // get lottery support indicator

        int lottery = 0;

        boolean isHotel = Utilities.isHotelUser(user, con);

        if (isProshop && !isHotel) {
            templott = (String) session.getAttribute("lottery");
            lottery = Integer.parseInt(templott);
        }

        // Declare variables
        String display_name = "";

        int guest_id = 0;

        // Get parameters from request object
        guest_id = Integer.parseInt(req.getParameter("guest_id"));

        // Get the guests name from the database
        try {
            pstmt = con.prepareStatement(
                    "SELECT CONCAT(name_first, ' ', IF(name_mi != '', CONCAT(name_mi, ' '), ''), name_last) as display_name "
                    + "FROM guestdb_data "
                    + "WHERE guest_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, guest_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                display_name = rs.getString("display_name");
            }

            pstmt.close();

        } catch (Exception exc) {
            display_name = "selected guest";
        }

        out.println(SystemUtils.HeadTitle("Guest Host Management"));

        out.println("<script type=\"text/javascript\">");
        out.println("<!--");
        out.println("function resizeIFrame(divHeight, iframeName) {");
        out.println("document.getElementById(iframeName).height = divHeight;");
        out.println("}");
        out.println("function movename(nameinfo) {");
        out.println(" var f = document.forms['playerform'];");
        out.println(" var fr = document.getElementById('hostlistiframe');");
        out.println(" array = nameinfo.split(':');"); // split string (partner_name, partner_id)
        out.println(" var username = array[1];");
        out.println(" fr.src = 'Common_guestdb?hostList&guest_id=" + guest_id + "&addHost=' + username;");
        out.println("}");
        out.println("// -->");
        out.println("</script>");

        out.println("<style>");
        out.println(".btnNorm {");
        out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
        out.println("  background: #99CC66;");
        out.println("  width: 80px;");
        out.println("}");
        out.println("</style>");

        out.println("<body bgcolor=\"#F5F5DC\">");

        if (!isHotel) {
            if (isProshop) {
                SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
            } else {
                SystemUtils.getMemberSubMenu(req, out, sescaller);        // required to allow submenus on this page
            }
        }

        out.println("<br><h2 align=center>Guest Host Management</h2>");

        out.println("<table width=\"500\" align=\"center\" border=\"1\" bgcolor=\"#336633\">");
        out.println("<tr><td align=\"left\"><font color=white size=2>");
        out.println("<CENTER><b>Instructions:</b><br><br>");
        out.println("This page displays all members that currently have access to this guest.  If a member is not "
                + "<br>in the list below, they will not be able to select this guest when booking a reservation.</CENTER><br><br>");
        out.println("-To <b>Add a host</b>: click their name in the name list to the left.<br>");
        if (isProshop) {
            out.println("-To <b>Remove a host</b>: click the trash can icon next to the name of the host to remove.");
        }
        out.println("</font></td></tr>");
        out.println("</table><br><br>");

        out.println("<table align=\"center\" bgcolor=\"#F5F5DC\">");

        // Display the guest name
        out.println("<tr><td align=\"center\" colspan=\"2\"><h3>Hosts for " + display_name + ":</h3></td></tr>");

        // Print host list iframe
        out.println("<tr><td align=\"center\" valign=\"top\">");
        out.println("<iframe id=\"hostlistiframe\" src=\"Common_guestdb?hostList&guest_id=" + guest_id + "\" width=\"500px\" scrolling=no frameborder=no></iframe>");
        out.println("</td>");


        // Set up form for use with adding a new host by clicking the memeber name-list
        out.println("<form action=\"Common_guestdb\" method=\"POST\" name=\"playerform\" onsubmit=\"false\">");
        out.println("<input type=\"hidden\" name=\"addHost\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"hostList\" value=\"\">");      // Signifies this submission came from clicking the name list
        out.println("<input type=\"hidden\" name=\"guest_id\" value=\"" + guest_id + "\">");
        out.println("<input type=\"hidden\" name=\"username\" value=\"\">");

        // Print namelist selection box
        out.println("<td align=\"center\" valign=\"top\"><br>");
        alphaTable.nameList_simple("", 20, false, out, con);
        out.println("</td>");

        out.println("</form>");

        out.println("</tr>");
        out.println("<tr><td align=\"center\" colspan=\"2\"><br><button class=\"btnNorm\" onclick=\"location.href='Common_guestdb" + (guest_id != 0 ? "#" + guest_id : "") + "'\">Return</button></td></tr>");
        out.println("</table>");
        out.println("</body></html>");
    }

    private void displayHostList(HttpServletRequest req, boolean isProshop, PrintWriter out, Connection con) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String username = "";
        String display_name = "";
        String result_msg = "";

        int guest_id = 0;
        int host_id = 0;

        boolean hostFound = false;

        // Get parameters from request object
        guest_id = Integer.parseInt(req.getParameter("guest_id"));
        // First see if we need to add/remove a host
        if (req.getParameter("addHost") != null) {

            String user = req.getParameter("addHost");

            result_msg = addHost(guest_id, user, con);
        }

        if (req.getParameter("removeHost") != null) {
            result_msg = removeHost(req, con);
        }

        out.println("<body onload=\"parent.window.resizeIFrame(document.getElementById('hostlistiframediv').offsetHeight + 50, 'hostlistiframe');\" bgcolor=\"#F5F5DC\" text=\"#000000\">");
        out.println("<div id=\"hostlistiframediv\">");

        out.println("<style>");

        out.println(".hostTable {");
        out.println("  padding: 0;");
        out.println("  margin: 0;");
        out.println("  background: #F5F5DC;");
        out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
        out.println("  border: 0px solid #dfeda3;");
        out.println("  align: center;");
        out.println("}");

        out.println("</style>");

        out.println("<script type=\"text/javascript\">");
        out.println("<!--");
        out.println("function removeHost(guest_id, host_id, username) {");
        out.println(" f = document.forms['removeHostForm'];");
        out.println(" f.guest_id.value = guest_id;");
        out.println(" f.host_id.value = host_id");
        out.println(" f.username.value = username;");
        out.println(" f.submit();");
        out.println("}");
        out.println("// -->");
        out.println("</script>");

        // Set up form for use with the remove partner buttons
        out.println("<form action=\"Common_guestdb\" method=\"POST\" name=\"removeHostForm\">");
        out.println("<input type=\"hidden\" name=\"guest_id\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"host_id\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"username\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"removeHost\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"hostList\" value=\"\">");
        out.println("</form>");

        if (!result_msg.equals("")) {
            out.println("<h3 align=\"center\">" + result_msg + "</h3>");
        } else {
            out.println("<h3>&nbsp;</h3>");
        }


        out.println("<CENTER><table class=\"hostTable\">");

        // Gather all the current hosts associated with this guest and list them
        try {
            pstmt = con.prepareStatement(
                    "SELECT h.host_id, m.username, CONCAT(m.name_first, ' ', IF(m.name_mi != '', CONCAT(m.name_mi, ' '), ''), m.name_last) as display_name "
                    + "FROM guestdb_hosts h "
                    + "LEFT OUTER JOIN member2b m ON h.username = m.username "
                    + "WHERE h.guest_id = ? "
                    + "ORDER BY m.name_last, m.name_first");

            pstmt.clearParameters();
            pstmt.setInt(1, guest_id);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                hostFound = true;  // Set this so we know at least one host was found;

                host_id = rs.getInt("h.host_id");
                username = rs.getString("m.username");
                display_name = rs.getString("display_name");

                out.println("<tr>");
                if (isProshop) {
                    out.println("<td align=\"right\"><a alt=\"Remove host\" title=\"Remove host\" onclick=\"removeHost('" + guest_id + "', '" + host_id + "', '" + username + "');\"><img width=\"13\" height=\"13\" border=\"0\" src=\"/v5/images/dts_trash.gif\"></a></td>");
                }
                out.println("<td align=\"left\">" + display_name + "</td>");
                out.println("</tr>");
            }

            pstmt.close();

        } catch (Exception exc) {
            out.println("err: " + exc.getMessage());
        }

        if (!hostFound) {
            out.println("<tr><td align=\"center\" colspan=\"2\">No hosts found!</td></tr>");
        }

        out.println("</table></CENTER>");
        out.println("</div>");
        out.println("</body></html>");
    }

    public static String addHost(int guest_id, String username, Connection con) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String result_msg = "";
        String display_name = "";

        int count = 0;

        if (guest_id > 0 && !username.equals("")) {

            try {

                // Get player name from database
                pstmt = con.prepareStatement(
                        "SELECT CONCAT(name_first, ' ', IF(name_mi != '', CONCAT(name_mi, ' '), ''), name_last) as display_name "
                        + "FROM member2b WHERE username = ?");
                pstmt.clearParameters();
                pstmt.setString(1, username);

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    display_name = rs.getString("display_name");
                }

                pstmt.close();

                pstmt = con.prepareStatement("INSERT INTO guestdb_hosts (guest_id, username) VALUES (?,?)");
                pstmt.clearParameters();
                pstmt.setInt(1, guest_id);
                pstmt.setString(2, username);

                count = pstmt.executeUpdate();

                pstmt.close();

                if (count > 0) {
                    result_msg = display_name + " added successfully!";
                }

            } catch (Exception exc) {
                result_msg = display_name + " is already present in the host list.  No changes made.";
            }
        }

        return result_msg;
    }

    private String removeHost(HttpServletRequest req, Connection con) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String result_msg = "";
        String display_name = "";
        String username = "";

        int host_id = 0;
        int count = 0;

        host_id = Integer.parseInt(req.getParameter("host_id"));
        username = req.getParameter("username");

        if (host_id > 0) {

            try {

                pstmt = con.prepareStatement(
                        "SELECT CONCAT(name_first, ' ', IF(name_mi != '', CONCAT(name_mi, ' '), ''), name_last) as display_name "
                        + "FROM member2b WHERE username = ?");
                pstmt.clearParameters();
                pstmt.setString(1, username);

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    display_name = rs.getString("display_name");
                }

                pstmt.close();

                pstmt = con.prepareStatement("DELETE FROM guestdb_hosts WHERE host_id = ?");
                pstmt.clearParameters();
                pstmt.setInt(1, host_id);

                count = pstmt.executeUpdate();

                pstmt.close();

                if (count > 0) {
                    result_msg = display_name + " removed successfully!";
                }

            } catch (Exception exc) {
                result_msg = "Error encountered while removing host: " + exc.getMessage();
            }
        }

        return result_msg;
    }

    public static void displayGuestForm(HttpServletRequest req, boolean isProshop, int activity_id, PrintWriter out, Connection con) {

        String result_msg = "";
        String caller = "";
        String submitText = "Apply Changes";

        int guest_id = 0;
        
        if (req.getParameter("caller") != null) {
            caller = req.getParameter("caller");
        }
        if (req.getParameter("guestForm") != null && req.getParameter("guestForm").equals("Add New Guest")) {
            submitText = "Add Guest";
        }
        if (req.getParameter("guest_id") != null) {
            guest_id = Integer.parseInt(req.getParameter("guest_id"));
        }

        out.println(SystemUtils.HeadTitle("Guest Management"));

        out.println("<style>");

        out.println(".btnNorm {");
        out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
        out.println("  background: #99CC66;");
        out.println("  width: 100px;");
        out.println("}");

        out.println("</style>");

        out.println("<body bgcolor=\"#F5F5DC\">");

        // If guest database system is not configured and active, display rejection message
        if (!Utilities.isGuestTrackingConfigured(activity_id, con)) {

            out.println("<br><h2 align=\"center\">Guest Tracking Inactive</h2>");
            out.println("<br><br><center>The guest tracking system is not currently active.</center>");

            if (isProshop) {
                out.println("<br><br><center>This feature can be activated from the System Config > Club Setup > Club Options page.</center>");
            }

            out.println("<br><br><center><button onclick=\"location.href='Member_announce'\">Home</button></center>");

        } else {

            out.println("<br><h2 align=center>Guest Management</h2>");

            if (!caller.equals("sheet") && !caller.equals("modal")) {
                out.println("<table width=450 align=center border=\"1\" bgcolor=\"#336633\">");
                out.println("<tr><td><font color=white size=2><center><b>Instructions:</b></center><br>");
                out.println("Listed below are various information fields for guests stored in the guest tracking system.  "
                        + "Please fill in any available information as best as possible, and click " + submitText + "."
                        + "<br><br>Required fields are denoted with a '<b>*</b>'.");
                out.println("</font></td></tr>");
                out.println("</table><br><br>");
            }

            out.println("<table align=\"center\" bgcolor=\"#F5F5DC\">");

            // If an error message is present, display it before continuing.
            if (!result_msg.equals("")) {
                out.println("<tr><td align=\"center\" colspan=\"2\">" + result_msg + "</td></tr>");
            }

            // Print information fields
            out.println("<tr><td align=\"center\">");

            // Start table and form used within printGuestInfoFields method
            out.println("<table align=\"center\" border=\"0\" bgcolor=\"#F5F5DC\">");
            out.println("<form action=\"Common_guestdb" + (guest_id != 0 ? "#" + guest_id : "") + "\" method=\"POST\" name=\"guestInfo\">");
            printGuestInfoFields(req, isProshop, false, activity_id, out, con);

            // Print submit/cancel button(s) for guest info field form
            out.println("<tr><td align=\"center\" colspan=\"2\">");
            out.println("<br><input type=\"submit\" class=\"btnNorm\" name=\"submitGuestInfo\" value=\"" + submitText + "\" width=\"120px\">");

            if (caller.equals("sheet")) {   // came from tee sheet, meaning form is currently in a window that should be closed
                out.println("&nbsp;&nbsp;<input type=\"button\" class=\"btnNorm\" value=\"Cancel\" onclick=\"window.close();\">");
            } else {      // came from guest management page
                out.println("<input type=\"button\" class=\"btnNorm\" name=\"btnCancel\" value=\"Cancel\" onclick=\"location.href='Common_guestdb" + (guest_id != 0 ? "#" + guest_id : "") + "'\">");
            }

            out.println("</td></tr>");
            out.println("</form>");
            out.println("</table>");

            out.println("</td></tr>");
        }

        out.println("</body></html>");
    }


    /**
     * printGuestInfoFields - Prints guest info form for adding/editing guest database entries.  Called standalone or from within displayGuestForm
     *
     * @param req Request object
     * @param isProshop True if user is a Proshop user, false if Member
     * @param reqOnly True if only required fields should be printed
     * @param activity_id Activity id to use when looking up optional/required guest information fields
     * @param out Output stream
     * @param con Connection to club database
     */
    public static void printGuestInfoFields(HttpServletRequest req, boolean isProshop, boolean reqOnly, int activity_id, PrintWriter out, Connection con) {
        Map<String, Object> temp_map = generateGuestInfoFields(req, isProshop, reqOnly, activity_id, out, con, false);
    }

    /**
     * generateGuestInfoFields - Prints/returns guest info form for adding/editing guest database entries.  Called standalone or from within displayGuestForm
     *
     * @param req Request object
     * @param isProshop True if user is a Proshop user, false if Member
     * @param reqOnly True if only required fields should be printed
     * @param activity_id Activity id to use when looking up optional/required guest information fields
     * @param out Output stream
     * @param con Connection to club database
     * @param genrateMap flag to trigger generation of Map objected instead of direct form output
     */
    public static Map<String, Object> generateGuestInfoFields(HttpServletRequest req, boolean isProshop, boolean reqOnly, int activity_id, PrintWriter out, Connection con, boolean generateMap) {

        // Declare variables
        Map<String, Map<String, Object>> field_map = new LinkedHashMap<String, Map<String, Object>>();
        Map<String, Object> field_group_map = new LinkedHashMap<String, Object>();

        List<String> message_list = new ArrayList<String>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String unique_id = "";
        String uid_type = "";
        String name_pre = "";
        String name_first = "";
        String name_mi = "";
        String name_last = "";
        String name_suf = "";
        String email1 = "";
        String email2 = "";
        String phone1 = "";
        String phone2 = "";
        String address1 = "";
        String address2 = "";
        String city = "";
        String state = "";
        String zip = "";
        String gender = "";
        String hdcp_num = "";
        String home_club = "";
        String caller = "";
        String use_uid = "";
        String use_name = "";
        String use_email = "";
        String use_phone = "";
        String use_address = "";
        String use_gender = "";
        String use_hdcp_num = "";
        String use_hdcp_index = "";
        String use_home_club = "";

        int guest_id = 0;
        int email_bounced1 = 0;
        int email_bounced2 = 0;
        int emailOpt = 0;
        int inact = 0;
        int sess_activity_id = 0;

        double hdcp_index = -99;

        if (req.getParameter("caller") != null) {
            caller = req.getParameter("caller");
        }

        // If guest_id has been passed, gather info for that guest from guestdb_data
        if (req.getParameter("guest_id") != null && !req.getParameter("guest_id").equals("0") && req.getParameter("return") == null) {

            guest_id = Integer.parseInt(req.getParameter("guest_id"));

            try {
                pstmt = con.prepareStatement("SELECT * FROM guestdb_data WHERE guest_id = ?");
                pstmt.clearParameters();
                pstmt.setInt(1, guest_id);

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    if (rs.getString("unique_id") != null) {
                        unique_id = rs.getString("unique_id");
                    }
                    uid_type = rs.getString("uid_type");
                    name_pre = rs.getString("name_pre");
                    name_first = rs.getString("name_first");
                    name_mi = rs.getString("name_mi");
                    name_last = rs.getString("name_last");
                    name_suf = rs.getString("name_suf");
                    email1 = rs.getString("email1");
                    email2 = rs.getString("email2");
                    email_bounced1 = rs.getInt("email_bounced1");
                    email_bounced2 = rs.getInt("email_bounced2");
                    emailOpt = rs.getInt("emailOpt");
                    phone1 = rs.getString("phone1");
                    phone2 = rs.getString("phone2");
                    address1 = rs.getString("address1");
                    address2 = rs.getString("address2");
                    city = rs.getString("city");
                    state = rs.getString("state");
                    zip = rs.getString("zip");
                    gender = rs.getString("gender");
                    hdcp_num = rs.getString("hdcp_num");
                    hdcp_index = rs.getDouble("hdcp_index");
                    home_club = rs.getString("home_club");
                    inact = rs.getInt("inact");
                }

                pstmt.close();

            } catch (Exception exc) {
                out.println("<!-- Error encountered gathering guest information from DB: " + exc.getMessage() + " -->");
            }

        } else if (req.getParameter("return") != null) {        // If no guest_id present, but the return field is present, then try to grab parameters from the request object

            if (req.getParameter("unique_id") != null) {
                unique_id = req.getParameter("unique_id");
            }
            if (req.getParameter("uid_type") != null) {
                uid_type = req.getParameter("uid_type");
            }
            if (req.getParameter("name_pre") != null) {
                name_pre = req.getParameter("name_pre");
            }
            if (req.getParameter("name_first") != null) {
                name_first = req.getParameter("name_first");
            }
            if (req.getParameter("name_mi") != null) {
                name_mi = req.getParameter("name_mi");
            }
            if (req.getParameter("name_last") != null) {
                name_last = req.getParameter("name_last");
            }
            if (req.getParameter("name_suf") != null) {
                name_suf = req.getParameter("name_suf");
            }
            if (req.getParameter("email1") != null) {
                email1 = req.getParameter("email1");
            }
            if (req.getParameter("email2") != null) {
                email2 = req.getParameter("email2");
            }
            if (req.getParameter("email_bounced1") != null) {
                email_bounced1 = Integer.parseInt(req.getParameter("email_bounced1"));
            }
            if (req.getParameter("email_bounced2") != null) {
                email_bounced2 = Integer.parseInt(req.getParameter("email_bounced2"));
            }
            if (req.getParameter("emailOpt") != null) {
                emailOpt = Integer.parseInt(req.getParameter("emailOpt"));
            }
            if (req.getParameter("phone1") != null) {
                phone1 = req.getParameter("phone1");
            }
            if (req.getParameter("phone2") != null) {
                phone2 = req.getParameter("phone2");
            }
            if (req.getParameter("address1") != null) {
                address1 = req.getParameter("address1");
            }
            if (req.getParameter("address2") != null) {
                address2 = req.getParameter("address2");
            }
            if (req.getParameter("city") != null) {
                city = req.getParameter("city");
            }
            if (req.getParameter("state") != null) {
                state = req.getParameter("state");
            }
            if (req.getParameter("zip") != null) {
                zip = req.getParameter("zip");
            }
            if (req.getParameter("gender") != null) {
                gender = req.getParameter("gender");
            }
            if (req.getParameter("hdcp_num") != null) {
                hdcp_num = req.getParameter("hdcp_num");
            }
            if (req.getParameter("home_club") != null) {
                home_club = req.getParameter("home_club");
            }
            if (req.getParameter("inact") != null) {
                inact = Integer.parseInt(req.getParameter("inact"));
            }

            try {
                if (req.getParameter("hdcp_index") != null && !req.getParameter("hdcp_index").equals("")) {
                    hdcp_index = Double.parseDouble(req.getParameter("hdcp_index"));
                }
            } catch (Exception ignore) {
            }
        }

        // Gather ask/req values for all information fields
        try {

            pstmt = con.prepareStatement("SELECT * FROM guestdb WHERE activity_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, activity_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                use_uid = rs.getString("uid");
                use_name = rs.getString("name");
                use_email = rs.getString("email");
                use_phone = rs.getString("phone");
                use_address = rs.getString("address");
                use_gender = rs.getString("gender");
                use_hdcp_num = rs.getString("hdcp_num");
                use_hdcp_index = rs.getString("hdcp_index");
                use_home_club = rs.getString("home_club");
            }

            pstmt.close();

        } catch (Exception exc) {
            out.println("<!-- Error encountered gathering guest information from DB: " + exc.getMessage() + " -->");
        }

        if (!generateMap) {
            // Hidden variable so we know we're returning to the page 
            out.println("<input type=\"hidden\" name=\"return\" value=\"1\">");
            out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
        } else {
            field_map.put("return", formUtil.makeFieldMap("", "hidden", "", 0, 0, "1", false));
            field_map.put("caller", formUtil.makeFieldMap("", "hidden", "", 0, 0, caller, false));
        }

        // Guest ID (if present)
        if (guest_id > 0) {
            if (generateMap) {
                field_map.put("guest_id", formUtil.makeFieldMap("", "hidden", "", 0, 0, "" + guest_id, false));
            } else {
                out.println("<input type=\"hidden\" name=\"guest_id\" value=\"" + guest_id + "\">");
            }
        }

        if (!use_email.equals("N") && (!reqOnly || use_email.equals("R"))) {
            if (generateMap) {
                message_list.add("Note: If an email address is entered, this guest will receive " + (sess_activity_id == 0 ? "tee time" : "reservation") + " notifications.");
            } else {
                out.println("<tr><td colspan=\"2\" style=\"text-align:center; color:red; font-size:medium;\">Note: If an email address is entered, this guest will receive " + (sess_activity_id == 0 ? "tee time" : "reservation") + " notifications.<br><br></td></tr>");
            }
        }
        
        if (caller.equals("sheet")) {
            if (generateMap) {
                message_list.add("Warning: Changes made on this page will be applied to <span style=\"font-weight: bold;\">all" + (sess_activity_id == 0 ? "tee times" : "reservations") + "</span> "
                        + "this tracked guest (<span style=\"font-weight: bold;\">" + name_first + " " + (!name_mi.equals("") ? name_mi + " " : "") + name_last + "</span>) is used in. "
                        + "To replace a temporary guest name for this " + (sess_activity_id == 0 ? "tee time" : "reservation") + " only, please enter the " + (sess_activity_id == 0 ? "tee time" : "reservation") + ", "
                        + "erase this guest, and replace it with a new guest with the desired name.");
            } else {
                out.println("<tr><td colspan=\"2\" style=\"text-align:center; color:red; font-size:medium;\">Warning: Changes made on this page will be applied to "
                        + "<span style=\"font-weight: bold;\">all " + (sess_activity_id == 0 ? "tee times" : "reservations") + "</span> this tracked guest "
                        + "(<span style=\"font-weight: bold;\">" + name_first + " " + (!name_mi.equals("") ? name_mi + " " : "") + name_last + "</span>) is a part of! "
                        + "To replace a temporary guest name for this " + (sess_activity_id == 0 ? "tee time" : "reservation") + " only, please enter the " 
                        + (sess_activity_id == 0 ? "tee time" : "reservation") + ", erase this guest, and replace it with a new guest with the desired name.<br><br></td></tr>");
            }
        }

        // Unique ID
        if (isProshop && use_uid.equals("R")) {
            if (generateMap) {
                field_map.put("unique_id", formUtil.makeFieldMap("Unique ID", "text", "large_text", 24, 24, unique_id, use_uid.equals("R")));
                field_map.put("uid_type", formUtil.makeFieldMap("Unique ID Type", "text", "large_text", 24, 24, uid_type, false));
            } else {
                out.println("<tr><td align=\"right\">Unique ID:</td><td align=\"left\"><input type=\"text\" name=\"unique_id\" value=\"" + unique_id + "\"size=\"24\" maxlength=\"24\">" + (use_uid.equals("R") ? (unique_id.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                out.println("<tr><td align=\"right\">Unique ID Type:</td><td align=\"left\"><input type=\"text\" name=\"uid_type\" value=\"" + uid_type + "\" size=\"24\" maxlength=\"24\"></td></tr>");
            }
        }
        // Name
        if (!use_name.equals("N") && (!reqOnly || use_name.equals("R"))) {
            if (generateMap) {
                field_map.put("name_first", formUtil.makeFieldMap("First Name", "text", "normal_text", 20, 20, name_first, use_name.equals("R")));
                field_map.put("name_mi", formUtil.makeFieldMap("Middle Initial", "text", "normal_text", 1, 1, name_mi, false));
                field_map.put("name_last", formUtil.makeFieldMap("Last Name", "text", "normal_text", 20, 20, name_last, use_name.equals("R")));
                field_map.put("name_pre", formUtil.makeFieldMap("", "hidden", "", 0, 0, name_pre, false));
                field_map.put("name_suf", formUtil.makeFieldMap("", "hidden", "", 0, 0, name_suf, false));
            } else {
                //out.println("<tr><td align=\"right\">Prefix:</td><td align=\"left\"><input type=\"text\" name=\"name_pre\" value=\"" + name_pre + "\"size=\"4\" maxlength=\"4\"></td></tr>");
                out.println("<tr><td align=\"right\">First Name:</td><td align=\"left\"><input type=\"text\" name=\"name_first\" value=\"" + name_first + "\" size=\"20\" maxlength=\"20\">" + (name_first.equals("") ? "<font color=red> *</font>" : " *") + "</td></tr>");
                out.println("<tr><td align=\"right\">Middle Initial:</td><td align=\"left\"><input type=\"text\" name=\"name_mi\" value=\"" + name_mi + "\"size=\"1\" maxlength=\"1\"></td></tr>");
                out.println("<tr><td align=\"right\">Last Name:</td><td align=\"left\"><input type=\"text\" name=\"name_last\" value=\"" + name_last + "\" size=\"20\" maxlength=\"20\">" + (name_last.equals("") ? "<font color=red> *</font>" : " *") + "</td></tr>");
                //out.println("<tr><td align=\"right\">Suffix:</td><td align=\"left\"><input type=\"text\" name=\"name_suf\" value=\"" + name_suf + "\"size=\"4\" maxlength=\"4\"></td></tr>");
                out.println("<input type=\"hidden\" name=\"name_pre\" value=\"\">");
                out.println("<input type=\"hidden\" name=\"name_suf\" value=\"\">");
            }
        }

        // Email
        if (!use_email.equals("N") && (!reqOnly || use_email.equals("R"))) {
            if (generateMap) {
                field_map.put("email1", formUtil.makeFieldMap("Email 1", "text", "large_text" + (email_bounced1 == 1 ? " entry_error" : ""), 24, 60, email1, use_email.equals("R")));
                field_map.put("email2", formUtil.makeFieldMap("Email 2", "text", "large_text" + (email_bounced2 == 1 ? " entry_error" : ""), 24, 60, email2, false));
            } else {
                //out.println("<tr><td colspan=\"2\" style=\"text-align:center; color:red; font-size:small;\">Note: If an email address is entered, this guest will receive " + (sess_activity_id == 0 ? "tee time" : "activity") + " snotifications.</td></tr>");
                out.println("<tr><td align=\"right\"" + (email_bounced1 == 1 ? " bgcolor=\"yellow\"" : "") + ">Email 1:</td><td align=\"left\"><input type=\"text\" name=\"email1\" value=\"" + email1 + "\" size=\"24\" maxlength=\"60\">" + (use_email.equals("R") ? (email1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                out.println("<tr><td align=\"right\"" + (email_bounced2 == 1 ? " bgcolor=\"yellow\"" : "") + ">Email 2:</td><td align=\"left\"><input type=\"text\" name=\"email2\" value=\"" + email2 + "\" size=\"24\" maxlength=\"60\"></td></tr>");
            }
        }

        // Phone
        if (!use_phone.equals("N") && (!reqOnly || use_phone.equals("R"))) {
            if (generateMap) {
                field_map.put("phone1", formUtil.makeFieldMap("Phone 1", "text", "large_text", 24, 24, phone1, use_phone.equals("R")));
                field_map.put("phone2", formUtil.makeFieldMap("Phone 2", "text", "large_text", 24, 24, phone2, false));
            } else {
                out.println("<tr><td align=\"right\">Phone 1:</td><td align=\"left\"><input type=\"text\" name=\"phone1\" value=\"" + phone1 + "\" size=\"24\" maxlength=\"24\">" + (use_email.equals("R") ? (phone1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                out.println("<tr><td align=\"right\">Phone 2:</td><td align=\"left\"><input type=\"text\" name=\"phone2\" value=\"" + phone2 + "\" size=\"24\" maxlength=\"24\"></td></tr>");
            }
        }

        // Address
        if (!use_address.equals("N") && (!reqOnly || use_address.equals("R"))) {
            if (generateMap) {
                field_map.put("address1", formUtil.makeFieldMap("Address 1", "text", "address_text", 30, 64, address1, use_address.equals("R")));
                field_map.put("address2", formUtil.makeFieldMap("Address 2", "text", "address_text", 30, 64, address2, false));
                field_map.put("city", formUtil.makeFieldMap("City", "text", "normal_text", 20, 30, city, use_address.equals("R")));
                field_map.put("state", formUtil.makeFieldMap("State", "text", "normal_text", 20, 24, state, use_address.equals("R")));
                field_map.put("zip", formUtil.makeFieldMap("Zip", "text", "zip_text", 6, 10, zip, use_address.equals("R")));
            } else {
                out.println("<tr><td align=\"right\">Address 1:</td><td align=\"left\"><input type=\"text\" name=\"address1\" value=\"" + address1 + "\" size=\"30\" maxlength=\"64\">" + (use_address.equals("R") ? (address1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                out.println("<tr><td align=\"right\">Address 2:</td><td align=\"left\"><input type=\"text\" name=\"address2\" value=\"" + address2 + "\" size=\"30\" maxlength=\"64\"></td></tr>");
                out.println("<tr><td align=\"right\">City:</td><td align=\"left\"><input type=\"text\" name=\"city\" value=\"" + city + "\" size=\"20\" maxlength=\"30\">" + (use_address.equals("R") ? (city.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                out.println("<tr><td align=\"right\">State:</td><td align=\"left\"><input type=\"text\" name=\"state\" value=\"" + state + "\" size=\"20\" maxlength=\"24\">" + (use_address.equals("R") ? (state.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                out.println("<tr><td align=\"right\">Zip:</td><td align=\"left\"><input type=\"text\" name=\"zip\" value=\"" + zip + "\" size=\"6\" maxlength=\"10\">" + (use_address.equals("R") ? (zip.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
            }
        }

        // Gender
        if (!use_gender.equals("N") && (!reqOnly || use_gender.equals("R"))) {
            if (generateMap) {
                field_map.put("gender", formUtil.makeFieldMap("Gender", "select", "", 0, 1, gender, use_gender.equals("R"), new String[]{"M", "F"}));
            } else {
                out.println("<tr><td align=\"right\">");
                out.println("Gender:</td><td align=\"left\"><select name=\"gender\" value=\"" + gender + "\" size=\"1\" width=\"2\">");
                out.println("<option value=\"\"" + (!gender.equals("M") && !gender.equals("F") ? " selected" : "") + ">&nbsp;</option>");
                out.println("<option value=\"M\"" + (gender.equals("M") ? " selected" : "") + ">M</option>");
                out.println("<option value=\"F\"" + (gender.equals("F") ? " selected" : "") + ">F</option>");
                out.println("</select>" + (use_gender.equals("R") ? (gender.equals("") ? "<font color=red> *</font>" : " *") : "") + "");
            }
        }

        // Handicap Number
        if (!use_hdcp_num.equals("N") && (!reqOnly || use_hdcp_num.equals("R"))) {
            if (generateMap) {
                field_map.put("hdcp_num", formUtil.makeFieldMap("Handicap Number", "text", "text_medium", 16, 10, hdcp_num, use_hdcp_num.equals("R")));
            } else {
                out.println("<tr><td align=\"right\">Handicap Number:</td><td align=\"left\"><input type=\"text\" name=\"hdcp_num\" value=\"" + hdcp_num + "\" size=\"10\" maxlength=\"16\">" + (use_hdcp_num.equals("R") ? (hdcp_num.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
            }
        }

        // Handicap Index
        if (!use_hdcp_index.equals("N") && (!reqOnly || use_hdcp_index.equals("R"))) {
            if (generateMap) {
                field_map.put("hdcp_index", formUtil.makeFieldMap("Handicap Index", "text", "text_small", 5, 0, ((hdcp_index == -99) ? "" : "" + hdcp_index), use_hdcp_index.equals("R")));
            } else {
                out.println("<tr><td align=\"right\">Handicap Index:</td><td align=\"left\"><input type=\"text\" name=\"hdcp_index\" value=\"" + (hdcp_index == -99 ? "" : hdcp_index) + "\" size=\"5\">" + (use_hdcp_index.equals("R") ? (hdcp_index == -99 ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
            }
        }

        // Home Club
        if (!use_home_club.equals("N") && (!reqOnly || use_home_club.equals("R"))) {
            if (generateMap) {
                field_map.put("home_club", formUtil.makeFieldMap("Home Club", "text", "text_snormal", 24, 30, home_club, use_home_club.equals("R")));
            } else {
                out.println("<tr><td align=\"right\">Home Club:</td><td align=\"left\"><input type=\"text\" name=\"home_club\" value=\"" + home_club + "\" size=\"24\" maxlength=\"30\">" + (use_home_club.equals("R") ? (home_club.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
            }
        }

        // Inactive Button
        if (isProshop && guest_id > 0) {
            if (generateMap) {
                field_map.put("inact", formUtil.makeFieldMap("Inactive", "checkbox", "", 0, 0, "" + inact, false));
            } else {
                out.println("<tr><td align=\"right\">Inactive:</td><td align=\"left\"><input type=\"checkbox\" name=\"inact\" value=\"1\"" + (inact == 1 ? " checked" : "") + "></td></tr>");
            }
        }

        if (generateMap) {
            field_group_map.put("fields", field_map);
            field_group_map.put("field_notes", message_list);
        }

        return field_group_map;
    }

    /**
     * updateGuest - Adds or updates a guest record after a submission from the guest information form.
     *
     * @param req Request object
     * @param isProshop True if user is a Proshop user, false if user is a member
     * @param activity_id Activity id the user is currently using ForeTees under.
     * @param user Current user's username
     * @param out Output stream
     * @param con Connection to club database
     * 
     * @return result_msg - Contains a result message based on the result of the insert/update
     */
    private static String updateGuest(HttpServletRequest req, boolean isProshop, int activity_id, String user, PrintWriter out, Connection con) {
        Map<String, Object> result_map = updateGuest(req, isProshop, activity_id, user, out, con, false);
        return (String) (result_map.get("result_msg"));
    }

    /**
     * updateGuest - Adds or updates a guest record after a submission from the guest information form.
     *
     * @param req Request object
     * @param isProshop True if user is a Proshop user, false if user is a member
     * @param activity_id Activity id the user is currently using ForeTees under.
     * @param user Current user's username
     * @param out Output stream
     * @param con Connection to club database
     * @param boolean generateMap flags returning response in map object
     * 
     * @return result_map - Contains a result message based on the result of the insert/update
     */
    private static Map<String, Object> updateGuest(HttpServletRequest req, boolean isProshop, int activity_id, String user, PrintWriter out, Connection con, boolean generateMap) {

        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;

        // Declare variables
        Map<String, Object> result_map = new LinkedHashMap<String, Object>();

        List<String> message_list = new ArrayList<String>();

        Map<String, Object> guest_map = new LinkedHashMap<String, Object>();


        String unique_id = "";
        String uid_type = "";
        String name_pre = "";
        String name_first = "";
        String name_mi = "";
        String name_last = "";
        String name_suf = "";
        String email1 = "";
        String email2 = "";
        String phone1 = "";
        String phone2 = "";
        String address1 = "";
        String address2 = "";
        String city = "";
        String state = "";
        String zip = "";
        String gender = "";
        String hdcp_num = "";
        String home_club = "";
        String caller = "";
        String result_msg = "";
        String guest_name = "";
        String guest_name_old = "";
        String use_uid = "";
        String use_name = "";
        String use_email = "";
        String use_phone = "";
        String use_address = "";
        String use_gender = "";
        String use_hdcp_num = "";
        String use_hdcp_index = "";
        String use_home_club = "";

        int guest_id = 0;
        int email_bounced1 = 0;
        int email_bounced2 = 0;
        int emailOpt = 0;
        int offset = 0;
        int count = 0;
        int count2 = 0;
        int inact = 0;
        int teecurr_id = 0;
        int player_num = 0;

        double hdcp_index = -99;

        boolean isHotel = Utilities.isHotelUser(user, con);
        boolean overrideAccess = false;

        boolean showOverrideButton = false; // used by generateMap

        boolean success = false; // used by generateMap

        // If proshop user, check proshop user feature access for appropriate access rights
        if (isHotel) {
            overrideAccess = true;
        } else if (isProshop) {
            overrideAccess = SystemUtils.verifyProAccess(req, "REST_OVERRIDE", con, out);
        }

        boolean existingUser = false;
        boolean allowOverride = true;
        boolean nameChanged = false;
        boolean useDupName = false;
        boolean skip = false;
        boolean allow_tba = Utilities.isGuestTrackingTbaAllowed(activity_id, isProshop, con);

        // Gather passed data from request object
        if (req.getParameter("skip") != null) {
            skip = true;
        }
        if (req.getParameter("caller") != null) {
            caller = req.getParameter("caller");
        }
        if (req.getParameter("unique_id") != null) {
            unique_id = req.getParameter("unique_id").trim();
        }
        if (req.getParameter("uid_type") != null) {
            uid_type = req.getParameter("uid_type").trim();
        }
        if (req.getParameter("name_pre") != null) {
            name_pre = req.getParameter("name_pre").trim();
        }
        if (req.getParameter("name_first") != null) {
            name_first = req.getParameter("name_first").trim();
        }
        if (req.getParameter("name_mi") != null) {
            name_mi = req.getParameter("name_mi").trim();
        }
        if (req.getParameter("name_last") != null) {
            name_last = req.getParameter("name_last").trim();
        }
        if (req.getParameter("name_suf") != null) {
            name_suf = req.getParameter("name_suf").trim();
        }
        if (req.getParameter("email1") != null) {
            email1 = req.getParameter("email1").trim();
        }
        if (req.getParameter("email2") != null) {
            email2 = req.getParameter("email2").trim();
        }
        if (req.getParameter("email_bounced1") != null) {
            email_bounced1 = Integer.parseInt(req.getParameter("email_bounced1"));
        }
        if (req.getParameter("email_bounced2") != null) {
            email_bounced2 = Integer.parseInt(req.getParameter("email_bounced2"));
        }
        if (req.getParameter("emailOpt") != null) {
            emailOpt = Integer.parseInt(req.getParameter("emailOpt"));
        }
        if (req.getParameter("phone1") != null) {
            phone1 = req.getParameter("phone1").trim();
        }
        if (req.getParameter("phone2") != null) {
            phone2 = req.getParameter("phone2").trim();
        }
        if (req.getParameter("address1") != null) {
            address1 = req.getParameter("address1").trim();
        }
        if (req.getParameter("address2") != null) {
            address2 = req.getParameter("address2").trim();
        }
        if (req.getParameter("city") != null) {
            city = req.getParameter("city").trim();
        }
        if (req.getParameter("state") != null) {
            state = req.getParameter("state").trim();
        }
        if (req.getParameter("zip") != null) {
            zip = req.getParameter("zip").trim();
        }
        if (req.getParameter("gender") != null) {
            gender = req.getParameter("gender").trim();
        }
        if (req.getParameter("hdcp_num") != null) {
            hdcp_num = req.getParameter("hdcp_num").trim();
        }
        if (req.getParameter("home_club") != null) {
            home_club = req.getParameter("home_club").trim();
        }
        if (req.getParameter("inact") != null) {
            inact = Integer.parseInt(req.getParameter("inact"));
        }
        if (req.getParameter("teecurr_id") != null) {
            teecurr_id = Integer.parseInt(req.getParameter("teecurr_id"));
        }
        if (req.getParameter("player_num") != null) {
            player_num = Integer.parseInt(req.getParameter("player_num"));
        }
        try {
            if (req.getParameter("hdcp_index") != null && !req.getParameter("hdcp_index").equals("")) {
                hdcp_index = Double.parseDouble(req.getParameter("hdcp_index"));
            }
        } catch (Exception ignore) {
        }

        // Gather ask/req values for all information fields
        try {

            pstmt = con.prepareStatement("SELECT * FROM guestdb WHERE activity_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, activity_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                use_uid = rs.getString("uid");
                use_name = rs.getString("name");
                use_email = rs.getString("email");
                use_phone = rs.getString("phone");
                use_address = rs.getString("address");
                use_gender = rs.getString("gender");
                use_hdcp_num = rs.getString("hdcp_num");
                use_hdcp_index = rs.getString("hdcp_index");
                use_home_club = rs.getString("home_club");
            }

            pstmt.close();

        } catch (Exception exc) {
            result_msg = "Error encountered gathering guest information from DB: " + exc.getMessage();
        }

        if (req.getParameter("guest_id") != null && Integer.parseInt(req.getParameter("guest_id")) > 0) {

            existingUser = true;
            guest_id = Integer.parseInt(req.getParameter("guest_id"));
            
            // if name has changed, ask if user wants to create a new tracked guest instead of
            if (getGuestName(guest_id, con).equalsIgnoreCase(name_first + " " + (!name_mi.equals("") ? name_mi + " " : "") + name_last)) {
                
            }
        }

        // Verify that all required fields have been filled in
        if (!skip
                && ((use_uid.equals("R") && isProshop && unique_id.equals(""))
                || //(use_name.equals("R") && (name_first.equals("") || name_last.equals(""))) ||
                (name_first.equals("") || name_last.equals(""))
                || (caller.equals("modal") && allow_tba && (name_first.equalsIgnoreCase("TBA") || name_last.equalsIgnoreCase("TBA") || name_first.equals("?") || name_last.equals("?")))
                || (use_email.equals("R") && email1.equals("") && email2.equals(""))
                || (use_phone.equals("R") && phone1.equals("") && phone2.equals(""))
                || (use_address.equals("R") && (address1.equals("") || city.equals("") || state.equals("") || zip.equals("")))
                || (use_gender.equals("R") && gender.equals(""))
                || (use_hdcp_num.equals("R") && hdcp_num.equals(""))
                || (use_hdcp_index.equals("R") && hdcp_index == -99)
                || (use_home_club.equals("R") && home_club.equals("")))) {

            if (name_first.equals("") || name_last.equals("")) {
                allowOverride = false;
            }

            if (!generateMap) {
                // Required field is missing.  If member, print return message.  If proshop, allow override or return
                out.println(SystemUtils.HeadTitle("Required Information Missing"));

                out.println("<style>");

                out.println(".btnNorm {");
                out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
                out.println("  background: #99CC66;");
                out.println("  width: 120px;");
                out.println("}");

                out.println("</style>");


                if (!caller.equals("modal")) {
                    out.println("<BODY><CENTER><img src=\"/" + rev + "/images/foretees.gif\">");
                    out.println("<hr width=\"40%\">");
                } else {
                    out.println("<BODY><CENTER>");
                }
            }

            if (generateMap) {
                message_list.add("<h3>Required Information Missing!</h3>");
                message_list.add("Some of the required information was not entered or contained invalid values.");
            } else {
                out.println("<BR><BR><H3>Required Information Missing!</H3>");
                out.println("<BR>Some of the required information was not entered or contained invalid values.");
            }

            if (isProshop && (name_first.equals("") || name_last.equals(""))) {
                if (generateMap) {
                    message_list.add("*First and last name must be included!*");
                } else {
                    out.println("<br><br>*First and last name must be included!*");
                }
            }

            if (caller.equals("modal") && allow_tba && (name_first.equalsIgnoreCase("TBA") || name_last.equalsIgnoreCase("TBA") || name_first.equals("?") || name_last.equals("?"))) {
                if (generateMap) {
                    message_list.add("*Invalid name entered*");
                    message_list.add("Please select 'TBA' from the list of existing guests if you do not know the name of your guest at this time.");
                } else {
                    out.println("<br><br>*Invalid name entered*"
                            + "<br><br>Please return and select 'TBA' from the list of existing guests if you do not know the name of your guest at this time.");
                }
            }
            if (!generateMap) {
                out.println("<form action=\"Common_guestdb\" method=\"POST\" name=\"returnForm\">");

                if (guest_id > 0) {
                    out.println("<input type=\"hidden\" name=\"guest_id\" value=\"" + guest_id + "\">");
                }

                out.println("<input type=\"hidden\" name=\"return\" value=\"1\">");
                if (!caller.equals("")) {
                    out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
                }
                if (!unique_id.equals("")) {
                    out.println("<input type=\"hidden\" name=\"unique_id\" value=\"" + unique_id + "\">");
                }
                if (!uid_type.equals("")) {
                    out.println("<input type=\"hidden\" name=\"uid_type\" value=\"" + uid_type + "\">");
                }
                if (!name_pre.equals("")) {
                    out.println("<input type=\"hidden\" name=\"name_pre\" value=\"" + name_pre + "\">");
                }
                if (!name_first.equals("")) {
                    out.println("<input type=\"hidden\" name=\"name_first\" value=\"" + name_first + "\">");
                }
                if (!name_mi.equals("")) {
                    out.println("<input type=\"hidden\" name=\"name_mi\" value=\"" + name_mi + "\">");
                }
                if (!name_last.equals("")) {
                    out.println("<input type=\"hidden\" name=\"name_last\" value=\"" + name_last + "\">");
                }
                if (!name_suf.equals("")) {
                    out.println("<input type=\"hidden\" name=\"name_suf\" value=\"" + name_suf + "\">");
                }
                if (!email1.equals("")) {
                    out.println("<input type=\"hidden\" name=\"email1\" value=\"" + email1 + "\">");
                }
                if (!email2.equals("")) {
                    out.println("<input type=\"hidden\" name=\"email2\" value=\"" + email2 + "\">");
                }
                if (email_bounced1 != 0) {
                    out.println("<input type=\"hidden\" name=\"email_bounced1\" value=\"" + email_bounced1 + "\">");
                }
                if (email_bounced2 != 0) {
                    out.println("<input type=\"hidden\" name=\"email_bounced2\" value=\"" + email_bounced2 + "\">");
                }
                if (emailOpt != 0) {
                    out.println("<input type=\"hidden\" name=\"emailOpt\" value=\"" + emailOpt + "\">");
                }
                if (!phone1.equals("")) {
                    out.println("<input type=\"hidden\" name=\"phone1\" value=\"" + phone1 + "\">");
                }
                if (!phone2.equals("")) {
                    out.println("<input type=\"hidden\" name=\"phone2\" value=\"" + phone2 + "\">");
                }
                if (!address1.equals("")) {
                    out.println("<input type=\"hidden\" name=\"address1\" value=\"" + address1 + "\">");
                }
                if (!address2.equals("")) {
                    out.println("<input type=\"hidden\" name=\"address2\" value=\"" + address2 + "\">");
                }
                if (!city.equals("")) {
                    out.println("<input type=\"hidden\" name=\"city\" value=\"" + city + "\">");
                }
                if (!state.equals("")) {
                    out.println("<input type=\"hidden\" name=\"state\" value=\"" + state + "\">");
                }
                if (!zip.equals("")) {
                    out.println("<input type=\"hidden\" name=\"zip\" value=\"" + zip + "\">");
                }
                if (!gender.equals("")) {
                    out.println("<input type=\"hidden\" name=\"gender\" value=\"" + gender + "\">");
                }
                if (!hdcp_num.equals("")) {
                    out.println("<input type=\"hidden\" name=\"hdcp_num\" value=\"" + hdcp_num + "\">");
                }
                if (hdcp_index != -99) {
                    out.println("<input type=\"hidden\" name=\"hdcp_index\" value=\"" + hdcp_index + "\">");
                }
                if (!home_club.equals("")) {
                    out.println("<input type=\"hidden\" name=\"home_club\" value=\"" + home_club + "\">");
                }
                if (inact == 1) {
                    out.println("<input type=\"hidden\" name=\"inact\" value=\"" + inact + "\">");
                }
            }
            if (!isProshop || !allowOverride) {

                if (isProshop) {
                    if (generateMap) {
                        message_list.add("Please enter at least a first and last name.");
                    } else {
                        out.println("<br><br>Please return and enter at least a first and last name.");
                    }
                } else {
                    if (generateMap) {
                        message_list.add("Please enter all required fields (denoted with a *)");
                    } else {
                        out.println("<br><br>Please return and enter all required fields (denoted with a *)");
                    }
                }
                if (!generateMap) {
                    out.println("<br><br><input class=\"btnNorm\" type=\"submit\" name=\"guestForm\" value=\"Return\">");
                    out.println("</form>");
                }

            } else {
                if (generateMap) {
                    message_list.add("Would you like to override this and proceed?");
                    showOverrideButton = true;
                } else {
                    out.println("<br><br>Would you like to override this and proceed?");
                    out.println("</form><br>");

                    out.println("<form action=\"Common_guestdb\" method=\"POST\" name=\"overrideForm\">");

                    if (guest_id > 0) {
                        out.println("<input type=\"hidden\" name=\"guest_id\" value=\"" + guest_id + "\">");
                    }

                    out.println("<input type=\"hidden\" name=\"skip\" value=\"yes\">");
                    if (!caller.equals("")) {
                        out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
                    }
                    if (!unique_id.equals("")) {
                        out.println("<input type=\"hidden\" name=\"unique_id\" value=\"" + unique_id + "\">");
                    }
                    if (!uid_type.equals("")) {
                        out.println("<input type=\"hidden\" name=\"uid_type\" value=\"" + uid_type + "\">");
                    }
                    if (!name_pre.equals("")) {
                        out.println("<input type=\"hidden\" name=\"name_pre\" value=\"" + name_pre + "\">");
                    }
                    if (!name_first.equals("")) {
                        out.println("<input type=\"hidden\" name=\"name_first\" value=\"" + name_first + "\">");
                    }
                    if (!name_mi.equals("")) {
                        out.println("<input type=\"hidden\" name=\"name_mi\" value=\"" + name_mi + "\">");
                    }
                    if (!name_last.equals("")) {
                        out.println("<input type=\"hidden\" name=\"name_last\" value=\"" + name_last + "\">");
                    }
                    if (!name_suf.equals("")) {
                        out.println("<input type=\"hidden\" name=\"name_suf\" value=\"" + name_suf + "\">");
                    }
                    if (!email1.equals("")) {
                        out.println("<input type=\"hidden\" name=\"email1\" value=\"" + email1 + "\">");
                    }
                    if (!email2.equals("")) {
                        out.println("<input type=\"hidden\" name=\"email2\" value=\"" + email2 + "\">");
                    }
                    if (email_bounced1 != 0) {
                        out.println("<input type=\"hidden\" name=\"email_bounced1\" value=\"" + email_bounced1 + "\">");
                    }
                    if (email_bounced2 != 0) {
                        out.println("<input type=\"hidden\" name=\"email_bounced2\" value=\"" + email_bounced2 + "\">");
                    }
                    if (emailOpt != 0) {
                        out.println("<input type=\"hidden\" name=\"emailOpt\" value=\"" + emailOpt + "\">");
                    }
                    if (!phone1.equals("")) {
                        out.println("<input type=\"hidden\" name=\"phone1\" value=\"" + phone1 + "\">");
                    }
                    if (!phone2.equals("")) {
                        out.println("<input type=\"hidden\" name=\"phone2\" value=\"" + phone2 + "\">");
                    }
                    if (!address1.equals("")) {
                        out.println("<input type=\"hidden\" name=\"address1\" value=\"" + address1 + "\">");
                    }
                    if (!address2.equals("")) {
                        out.println("<input type=\"hidden\" name=\"address2\" value=\"" + address2 + "\">");
                    }
                    if (!city.equals("")) {
                        out.println("<input type=\"hidden\" name=\"city\" value=\"" + city + "\">");
                    }
                    if (!state.equals("")) {
                        out.println("<input type=\"hidden\" name=\"state\" value=\"" + state + "\">");
                    }
                    if (!zip.equals("")) {
                        out.println("<input type=\"hidden\" name=\"zip\" value=\"" + zip + "\">");
                    }
                    if (!gender.equals("")) {
                        out.println("<input type=\"hidden\" name=\"gender\" value=\"" + gender + "\">");
                    }
                    if (!hdcp_num.equals("")) {
                        out.println("<input type=\"hidden\" name=\"hdcp_num\" value=\"" + hdcp_num + "\">");
                    }
                    if (hdcp_index != -99) {
                        out.println("<input type=\"hidden\" name=\"hdcp_index\" value=\"" + hdcp_index + "\">");
                    }
                    if (!home_club.equals("")) {
                        out.println("<input type=\"hidden\" name=\"home_club\" value=\"" + home_club + "\">");
                    }
                    if (inact == 1) {
                        out.println("<input type=\"hidden\" name=\"inact\" value=\"" + inact + "\">");
                    }

                    out.println("<input class=\"btnNorm\" type=\"button\" name=\"guestForm\" value=\"No - Return\" onclick=\"document.forms['returnForm'].submit();\">");
                    out.println("<input class=\"btnNorm\" type=\"submit\" name=\"submitGuestInfo\" value=\"Yes - Continue\">");
                    out.println("</form>");
                }
            }
            if (!generateMap) {
                out.println("</body></html>");
                out.close();
            }

        } else {

            // See if we're updating an existing record or creating a new one
            if (existingUser) {

                // First see if the name has changed
                try {

                    // Build new guest name value
                    guest_name = name_first + " " + (!name_mi.equals("") ? name_mi + " " : "") + name_last;

                    // Get old guest name value from database
                    pstmt = con.prepareStatement(
                            "SELECT CONCAT(name_first, ' ', IF(name_mi != '', CONCAT(name_mi, ' '), ''), name_last) as guest_name "
                            + "FROM guestdb_data "
                            + "WHERE guest_id = ?");
                    pstmt.clearParameters();
                    pstmt.setInt(1, guest_id);

                    rs = pstmt.executeQuery();

                    if (rs.next()) {
                        guest_name_old = rs.getString("guest_name");
                    }

                    pstmt.close();

                    if (!guest_name.equals(guest_name_old)) {
                        nameChanged = true;
                    }

                } catch (Exception exc) {
                    result_msg = "Error occurred while updating guest record: " + exc.getMessage();
                }

                try {

                    // Update the existing record with the new values
                    pstmt = con.prepareStatement(
                            "UPDATE guestdb_data SET "
                            + "name_pre = ?, name_first = ?, name_mi = ?, name_last = ?, name_suf = ?, "
                            + "email1 = ?, email2 = ?, email_bounced1 = ?, email_bounced2 = ?, emailOpt = ?, "
                            + "phone1 = ?, phone2 = ?, address1 = ?, address2 = ?, city = ?, "
                            + "state = ?, zip = ?, gender = ?, hdcp_num = ?, hdcp_index = ?, "
                            + "home_club = ?, inact = ? "
                            + "WHERE guest_id = ?");
                    pstmt.clearParameters();
                    pstmt.setString(1, name_pre);
                    pstmt.setString(2, name_first);
                    pstmt.setString(3, name_mi);
                    pstmt.setString(4, name_last);
                    pstmt.setString(5, name_suf);
                    pstmt.setString(6, email1);
                    pstmt.setString(7, email2);
                    pstmt.setInt(8, email_bounced1);
                    pstmt.setInt(9, email_bounced2);
                    pstmt.setInt(10, emailOpt);
                    pstmt.setString(11, phone1);
                    pstmt.setString(12, phone2);
                    pstmt.setString(13, address1);
                    pstmt.setString(14, address2);
                    pstmt.setString(15, city);
                    pstmt.setString(16, state);
                    pstmt.setString(17, zip);
                    pstmt.setString(18, gender);
                    pstmt.setString(19, hdcp_num);
                    pstmt.setDouble(20, hdcp_index);
                    pstmt.setString(21, home_club);
                    pstmt.setInt(22, inact);
                    pstmt.setInt(23, guest_id);

                    count = pstmt.executeUpdate();

                    pstmt.close();

                } catch (Exception exc) {
                    result_msg = "Error occurred while updating guest record: " + exc.getMessage();
                }

                // Apply any name changes made, if the update fails, set the result_msg
                if (nameChanged && updateGuestNames(guest_id, guest_name, guest_name_old, con)) {
                    result_msg = "Error occurred while updating guest names";
                }

                // If no error encountered so far, attempt to update the unique id
                if (result_msg.equals("")) {

                    if (isProshop && use_uid.equals("R")) {

                        try {
                            pstmt = con.prepareStatement("UPDATE guestdb_data SET unique_id = ?, uid_type = ? WHERE guest_id = ?");
                            pstmt.clearParameters();
                            if (unique_id.equals("")) {
                                pstmt.setNull(1, Types.VARCHAR);
                            } else {
                                pstmt.setString(1, unique_id);
                            }
                            pstmt.setString(2, uid_type);
                            pstmt.setInt(3, guest_id);

                            count2 = pstmt.executeUpdate();

                            pstmt.close();

                        } catch (Exception exc) {

                            // See if we need to prompt to merge two guests or not.
                            if (exc.getMessage().startsWith("Duplicate entry")) {
                                displayDuplicateGuests(req, user, activity_id, out, con);
                            } else {
                                result_msg = "Error occurred while updating guest record: " + exc.getMessage();
                            }
                        }
                    }
                }

                if (result_msg.equals("")) {
                    result_msg = "Guest updated successfully!";
                    success = true;
                }

            } else {        // No guest_id, create new guest entry

                // Create a new record for this guest
                try {

                    pstmt = con.prepareStatement("INSERT INTO guestdb_data "
                            + "(name_pre, name_first, name_mi, name_last, name_suf, email1, email2, email_bounced1, email_bounced2, emailOpt, "
                            + "phone1, phone2, address1, address2, city, state, zip, gender, hdcp_num, hdcp_index, "
                            + "home_club, inact" + (isProshop && use_uid.equals("R") ? ", uid_type, unique_id" : "") + ") "
                            + "VALUES "
                            + "(?,?,?,?,?,?,?,0,0,1,"
                            + " ?,?,?,?,?,?,?,?,?,?,"
                            + " ?,?" + (isProshop && use_uid.equals("R") ? ",?" + (unique_id.equals("") ? ",NULL" : ",?") : "") + ")");
                    pstmt.clearParameters();
                    pstmt.setString(1, name_pre);
                    pstmt.setString(2, name_first);
                    pstmt.setString(3, name_mi);
                    pstmt.setString(4, name_last);
                    pstmt.setString(5, name_suf);
                    pstmt.setString(6, email1);
                    pstmt.setString(7, email2);
                    pstmt.setString(8, phone1);
                    pstmt.setString(9, phone2);
                    pstmt.setString(10, address1);
                    pstmt.setString(11, address2);
                    pstmt.setString(12, city);
                    pstmt.setString(13, state);
                    pstmt.setString(14, zip);
                    pstmt.setString(15, gender);
                    pstmt.setString(16, hdcp_num);
                    pstmt.setDouble(17, hdcp_index);
                    pstmt.setString(18, home_club);
                    pstmt.setInt(19, inact);
                    if (isProshop && use_uid.equals("R")) {

                        pstmt.setString(20, uid_type);

                        if (!unique_id.equals("")) {
                            pstmt.setString(21, unique_id);
                        }
                    }

                    count = pstmt.executeUpdate();

                    pstmt.close();

                    // If guest was successfully added, get the guest_id assigned to it.
                    if (count > 0) {

                        try {
                            stmt = con.createStatement();

                            rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");

                            if (rs.next()) {

                                guest_id = rs.getInt(1);

                                // If guest was added by a member, add the member as a host for this guest
                                if (!isProshop) {
                                    pstmt = con.prepareStatement("INSERT INTO guestdb_hosts (guest_id, username) VALUES (?,?)");
                                    pstmt.clearParameters();
                                    pstmt.setInt(1, guest_id);
                                    pstmt.setString(2, user);

                                    count2 = pstmt.executeUpdate();

                                    pstmt.close();
                                }
                            }

                            stmt.close();

                        } catch (Exception exc) {
                            result_msg = "Error adding host: " + exc.getMessage();
                        }
                    }

                } catch (Exception exc) {

                    // See if we need to prompt to merge two guests or not.
                    if (exc.getMessage().startsWith("Duplicate entry")) {
                        displayDuplicateGuests(req, user, activity_id, out, con);
                    } else {
                        result_msg = "Error occurred while adding new guest record: " + exc.getMessage();
                    }
                }

                if (count > 0) {
                    result_msg = "Guest added successfully!";
                    success = true;
                    if (generateMap) {
                        guest_map.put("guest_name", name_first + " " + (!name_mi.equals("") ? name_mi + " " : "") + name_last);
                        guest_map.put("display_name", name_last + ", " + name_first + (!name_mi.equals("") ? " " + name_mi : ""));
                        guest_map.put("guest_id", guest_id);
                        guest_map.put("email1", email1);
                        guest_map.put("email2", email2);
                        guest_map.put("address1", address1);
                        guest_map.put("address2", address2);
                        guest_map.put("city", city);
                        guest_map.put("state", state);
                        guest_map.put("zip", zip);
                        guest_map.put("homeclub", home_club);
                        guest_map.put("ghin", hdcp_num);
                        guest_map.put("gender", gender);
                    } else if (caller.equals("modal")) {
                        result_msg = name_first + " " + (!name_mi.equals("") ? name_mi + " " : "") + name_last + ":" + guest_id + "|" + result_msg;

                    }
                }
            }
        }
        result_map.put("result_msg", result_msg);
        result_map.put("guest_data", guest_map);
        result_map.put("successful", success);
        result_map.put("message_list", message_list);
        return result_map;
    }

    public static void displayDuplicateGuests(HttpServletRequest req, String user, int activity_id, PrintWriter out, Connection con) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean error = false;
        boolean existingGuest = false;
        boolean isProshop = false;


        String unique_id = "";
        String uid_type = "";
        String name_pre = "";
        String name_first = "";
        String name_mi = "";
        String name_last = "";
        String name_suf = "";
        String email1 = "";
        String email2 = "";
        String phone1 = "";
        String phone2 = "";
        String address1 = "";
        String address2 = "";
        String city = "";
        String state = "";
        String zip = "";
        String gender = "";
        String hdcp_num = "";
        String home_club = "";

        String dup_unique_id = "";
        String dup_uid_type = "";
        String dup_name_pre = "";
        String dup_name_first = "";
        String dup_name_mi = "";
        String dup_name_last = "";
        String dup_name_suf = "";
        String dup_email1 = "";
        String dup_email2 = "";
        String dup_phone1 = "";
        String dup_phone2 = "";
        String dup_address1 = "";
        String dup_address2 = "";
        String dup_city = "";
        String dup_state = "";
        String dup_zip = "";
        String dup_gender = "";
        String dup_hdcp_num = "";
        String dup_home_club = "";

        String caller = "";
        String guest_name = "";
        String dup_guest_name = "";

        String use_uid = "";
        String use_name = "";
        String use_email = "";
        String use_phone = "";
        String use_address = "";
        String use_gender = "";
        String use_hdcp_num = "";
        String use_hdcp_index = "";
        String use_home_club = "";

        double hdcp_index = -99;
        double dup_hdcp_index = -99;

        int guest_id = 0;
        int email_bounced1 = 0;
        int email_bounced2 = 0;
        int emailOpt = 0;
        int inact = 0;

        int dup_guest_id = 0;
        int dup_email_bounced1 = 0;
        int dup_email_bounced2 = 0;
        int dup_emailOpt = 0;
        int dup_inact = 0;

        if (ProcessConstants.isProshopUser(user) || Utilities.isHotelUser(user, con)) {
            isProshop = true;
        }

        if (req.getParameter("unique_id") != null) {

            dup_unique_id = req.getParameter("unique_id");

            // See if the current user is a new addition or an edit to an existing guest
            if (req.getParameter("guest_id") != null && Integer.parseInt(req.getParameter("guest_id")) > 0) {

                existingGuest = true;
                dup_guest_id = Integer.parseInt(req.getParameter("guest_id"));
            }

            // Get the info for the new guest from the request object
            if (req.getParameter("caller") != null) {
                caller = req.getParameter("caller");
            }
            if (req.getParameter("uid_type") != null) {
                dup_uid_type = req.getParameter("uid_type");
            }
            if (req.getParameter("name_pre") != null) {
                dup_name_pre = req.getParameter("name_pre");
            }
            if (req.getParameter("name_first") != null) {
                dup_name_first = req.getParameter("name_first");
            }
            if (req.getParameter("name_mi") != null) {
                dup_name_mi = req.getParameter("name_mi");
            }
            if (req.getParameter("name_last") != null) {
                dup_name_last = req.getParameter("name_last");
            }
            if (req.getParameter("name_suf") != null) {
                dup_name_suf = req.getParameter("name_suf");
            }
            if (req.getParameter("email1") != null) {
                dup_email1 = req.getParameter("email1");
            }
            if (req.getParameter("email2") != null) {
                dup_email2 = req.getParameter("email2");
            }
            if (req.getParameter("email_bounced1") != null) {
                dup_email_bounced1 = Integer.parseInt(req.getParameter("email_bounced1"));
            }
            if (req.getParameter("email_bounced2") != null) {
                dup_email_bounced2 = Integer.parseInt(req.getParameter("email_bounced2"));
            }
            if (req.getParameter("emailOpt") != null) {
                dup_emailOpt = Integer.parseInt(req.getParameter("emailOpt"));
            }
            if (req.getParameter("phone1") != null) {
                dup_phone1 = req.getParameter("phone1");
            }
            if (req.getParameter("phone2") != null) {
                dup_phone2 = req.getParameter("phone2");
            }
            if (req.getParameter("address1") != null) {
                dup_address1 = req.getParameter("address1");
            }
            if (req.getParameter("address2") != null) {
                dup_address2 = req.getParameter("address2");
            }
            if (req.getParameter("city") != null) {
                dup_city = req.getParameter("city");
            }
            if (req.getParameter("state") != null) {
                dup_state = req.getParameter("state");
            }
            if (req.getParameter("zip") != null) {
                dup_zip = req.getParameter("zip");
            }
            if (req.getParameter("gender") != null) {
                dup_gender = req.getParameter("gender");
            }
            if (req.getParameter("hdcp_num") != null) {
                dup_hdcp_num = req.getParameter("hdcp_num");
            }
            if (req.getParameter("hdcp_index") != null && !req.getParameter("hdcp_index").equals("")) {
                dup_hdcp_index = Double.parseDouble(req.getParameter("hdcp_index"));
            }
            if (req.getParameter("home_club") != null) {
                dup_home_club = req.getParameter("home_club");
            }
            if (req.getParameter("inact") != null) {
                dup_inact = Integer.parseInt(req.getParameter("inact"));
            }


            // Look up what guest already exists with this unique id
            try {

                pstmt = con.prepareStatement(
                        "SELECT *, CONCAT(name_first, ' ', IF(name_mi != '', CONCAT(name_mi, ' '), ''), name_last) as guest_name "
                        + "FROM guestdb_data "
                        + "WHERE unique_id = ?" + (existingGuest ? " AND guest_id != ?" : ""));
                pstmt.clearParameters();
                pstmt.setString(1, dup_unique_id);
                if (existingGuest) {
                    pstmt.setInt(2, dup_guest_id);
                }

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    if (rs.getString("unique_id") != null) {
                        unique_id = rs.getString("unique_id");
                    }
                    guest_name = rs.getString("guest_name");
                    guest_id = rs.getInt("guest_id");
                    uid_type = rs.getString("uid_type");
                    name_pre = rs.getString("name_pre");
                    name_first = rs.getString("name_first");
                    name_mi = rs.getString("name_mi");
                    name_last = rs.getString("name_last");
                    name_suf = rs.getString("name_suf");
                    email1 = rs.getString("email1");
                    email2 = rs.getString("email2");
                    email_bounced1 = rs.getInt("email_bounced1");
                    email_bounced2 = rs.getInt("email_bounced2");
                    emailOpt = rs.getInt("emailOpt");
                    phone1 = rs.getString("phone1");
                    phone2 = rs.getString("phone2");
                    address1 = rs.getString("address1");
                    address2 = rs.getString("address2");
                    city = rs.getString("city");
                    state = rs.getString("state");
                    zip = rs.getString("zip");
                    gender = rs.getString("gender");
                    hdcp_num = rs.getString("hdcp_num");
                    hdcp_index = rs.getDouble("hdcp_index");
                    home_club = rs.getString("home_club");
                    inact = rs.getInt("inact");
                }

                pstmt.close();
            } catch (Exception exc) {
                out.println("<!-- Error encountered gathering guest information from DB: " + exc.getMessage() + " -->");
                error = true;
            }

            // Gather ask/req values for all information fields
            try {

                pstmt = con.prepareStatement("SELECT * FROM guestdb WHERE activity_id = ?");
                pstmt.clearParameters();
                pstmt.setInt(1, activity_id);

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    use_uid = rs.getString("uid");
                    use_name = rs.getString("name");
                    use_email = rs.getString("email");
                    use_phone = rs.getString("phone");
                    use_address = rs.getString("address");
                    use_gender = rs.getString("gender");
                    use_hdcp_num = rs.getString("hdcp_num");
                    use_hdcp_index = rs.getString("hdcp_index");
                    use_home_club = rs.getString("home_club");
                }

                pstmt.close();

            } catch (Exception exc) {
                out.println("<!-- Error encountered gathering guest tracking settings: " + exc.getMessage() + " -->");
                error = true;
            }

            out.println(SystemUtils.HeadTitle("Duplicate Guest Found"));

            out.println("<style>");

            out.println(".btnNorm {");
            out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
            out.println("  background: #99CC66;");
            out.println("  width: 165px;");
            out.println("}");

            out.println("</style>");

            if (caller.equals("modal")) {

                out.println("<!-- ******** BEGIN LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->"
                        + "<script type=\"text/javascript\">var lwmwLinkedBy=\"LiknoWebModalWindows [1]\",lwmwName=\"foretees-modal\",lwmwBN=\"128\";awmAltUrl=\"\";</script>"
                        + "<script charset=\"UTF-8\" src=\"/" + rev + "/web%20utilities/foretees-modal.js\" type=\"text/javascript\"></script>"
                        + "<!-- ******** END LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->");

                printModalScripts(out);

                out.println("<body bgcolor=\"#F5F5DC\" text=\"#000000\" onload=\"parent.window.resizeIFrame(document.getElementById('modaliframediv').offsetHeight + 50, 'modaliframe');\">");
                out.println("<div id=\"modaliframediv\">");
            } else {
                out.println("<body bgcolor=\"#F5F5DC\">");
            }

            out.println("<br><h2 align=center>Duplicate Guest Found!</h2>");


            // If the current guest already exists, then display their info and the info for the duplicate guest and allow user to select one of the two to merge into.
            if (existingGuest) {
                // Data collected, now print out the two options
                out.println("<CENTER>");
                out.println("<table style=\"border:0px; padding-left:10px; padding-right:10px; background-color:#F5F5DC;\">");

                // Print message/instructions
                out.println("<tr align=\"center\"><td align=\"center\" colspan=\"2\" width=\"650\">");
                out.println("Another guest is already using this Unique ID!  If both of these records represent the same guest, "
                        + "the records can be merged by selecting one of them below.  Which ever record is <b>selected</b> will "
                        + "be kept, and the other record will be merged into it.  All past/current reservations and hosts for both "
                        + "guests will now be associated with the selected record, removing the duplicate record.");
                out.println("<br><br>If both these records do <b>not</b> represent the same person, please use the 'Return "
                        + "to Guest Info Form' button to return and alter the Unique ID before re-submitting, as they must be "
                        + "unique!<br><br>");
                out.println("(<b>Note:</b> Required fields denoted with a *)");
                out.println("</td></tr>");

                out.println("<tr><td align=\"right\">");

                // Print existing record (left side)
                out.println("<table border=\"0\" bgcolor=\"#F5F5DC\">");

                // Unique ID
                if (isProshop && use_uid.equals("R")) {
                    out.println("<tr><td align=\"left\">Unique ID: " + unique_id + (use_uid.equals("R") ? (unique_id.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    //out.println("<tr><td align=\"left\">Unique ID Type: " + uid_type + "</td></tr>");
                }

                // Name
                if (!use_name.equals("N")) {

                    String name_display = (!name_pre.equals("") ? name_pre + " " : "") + name_first + " " + (!name_mi.equals("") ? name_mi + " " : "") + name_last + (!name_suf.equals("") ? " " + name_suf : "");

                    out.println("<tr><td align=\"left\">Name: " + name_display + "</td></tr>");

                    /*
                    out.println("<tr><td align=\"left\">Prefix: " + name_pre + "</td></tr>");
                    out.println("<tr><td align=\"left\">First Name: " + name_first + (use_name.equals("R") ? (name_first.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Middle Initial: " + name_mi + "</td></tr>");
                    out.println("<tr><td align=\"left\">Last Name: " + name_last + (use_name.equals("R") ? (name_last.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Suffix: " + name_suf + "</td></tr>");
                     */
                }

                // Email
                if (!use_email.equals("N")) {
                    out.println("<tr><td align=\"left\">Email 1: " + email1 + (use_email.equals("R") ? (email1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Email 2: " + email2 + "</td></tr>");
                }

                // Phone
                if (!use_phone.equals("N")) {
                    out.println("<tr><td align=\"left\">Phone 1: " + phone1 + (use_phone.equals("R") ? (phone1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Phone 2: " + phone2 + "</td></tr>");
                }

                // Address
                if (!use_address.equals("N")) {
                    out.println("<tr><td align=\"left\">Address 1: " + address1 + (use_address.equals("R") ? (address1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Address 2: " + address2 + "</td></tr>");
                    out.println("<tr><td align=\"left\">City: " + city + (use_address.equals("R") ? (city.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">State: " + state + (use_address.equals("R") ? (state.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Zip: " + zip + (use_address.equals("R") ? (zip.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                }

                // Gender
                if (!use_gender.equals("N")) {
                    out.println("<tr><td align=\"left\">Gender: " + gender + (use_gender.equals("R") ? (gender.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                }

                // Handicap Number
                if (!use_hdcp_num.equals("N")) {
                    out.println("<tr><td align=\"left\">Handicap Number: " + hdcp_num + (use_hdcp_num.equals("R") ? (hdcp_num.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                }

                // Handicap Index
                if (!use_hdcp_index.equals("N")) {
                    out.println("<tr><td align=\"left\">Handicap Index: " + (hdcp_index == -99 ? "" : hdcp_index) + (use_hdcp_index.equals("R") ? (hdcp_index == -99 ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                }

                // Home Club
                if (!use_home_club.equals("N")) {
                    out.println("<tr><td align=\"left\">Home Club: " + home_club + (use_home_club.equals("R") ? (home_club.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                }

                // Inactive Button
                if (isProshop && guest_id > 0) {
                    out.println("<tr><td align=\"left\">Inactive: " + (inact == 1 ? "Yes" : "No") + "</td></tr>");
                }

                // Print form and submit button to merge to this guest
                out.println("<form action=\"Common_guestdb\" method=\"POST\" name=\"mergeToExistingForm\">");
                out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
                out.println("<input type=\"hidden\" name=\"target_unique_id\" value=\"" + dup_unique_id + "\">");
                out.println("<input type=\"hidden\" name=\"target_id\" value=\"" + guest_id + "\">");
                out.println("<input type=\"hidden\" name=\"merged_id\" value=\"" + dup_guest_id + "\">");
                out.println("<tr><td align=\"center\" colspan=\"2\">");
                out.println("<input class=\"btnNorm\" type=\"submit\" name=\"mergeGuest\" value=\"Merge into this record\">");
                out.println("</td></tr>");
                out.println("</form>");

                out.println("</table>");    // End left side table

                out.println("</td><td align=\"left\">");



                // Print new/edited record (right side)
                out.println("<table border=\"0\" bgcolor=\"#F5F5DC\">");    // Start right side table

                // Unique ID
                if (isProshop && use_uid.equals("R")) {
                    out.println("<tr><td align=\"left\">Unique ID: " + dup_unique_id + (use_uid.equals("R") ? (dup_unique_id.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    //out.println("<tr><td align=\"left\">Unique ID Type: " + dup_uid_type + "</td></tr>");
                }

                // Name
                if (!use_name.equals("N")) {

                    String dup_name_display = (!dup_name_pre.equals("") ? dup_name_pre + " " : "") + dup_name_first + " " + (!dup_name_mi.equals("") ? dup_name_mi + " " : "")
                            + dup_name_last + (dup_name_suf.equals("") ? " " + dup_name_suf : "") + (dup_name_first.equals("") || dup_name_last.equals("") ? "<font color=red> * </font>" : " *");

                    out.println("<tr><td align=\"left\">Name: " + dup_name_display + "</td></tr>");

                    /*
                    out.println("<tr><td align=\"left\">Prefix: " + dup_name_pre + "</td></tr>");
                    out.println("<tr><td align=\"left\">First Name: " + dup_name_first + (use_name.equals("R") ? (dup_name_first.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Middle Initial: " + dup_name_mi + "</td></tr>");
                    out.println("<tr><td align=\"left\">Last Name: " + dup_name_last + (use_name.equals("R") ? (dup_name_last.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Suffix: " + dup_name_suf + "</td></tr>");
                     */
                }

                // Email
                if (!use_email.equals("N")) {
                    out.println("<tr><td align=\"left\">Email 1: " + dup_email1 + (use_email.equals("R") ? (dup_email1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Email 2: " + dup_email2 + "</td></tr>");
                }

                // Phone
                if (!use_phone.equals("N")) {
                    out.println("<tr><td align=\"left\">Phone 1: " + dup_phone1 + (use_phone.equals("R") ? (dup_phone1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Phone 2: " + dup_phone2 + "</td></tr>");
                }

                // Address
                if (!use_address.equals("N")) {
                    out.println("<tr><td align=\"left\">Address 1: " + dup_address1 + (use_address.equals("R") ? (dup_address1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Address 2: " + dup_address2 + "</td></tr>");
                    out.println("<tr><td align=\"left\">City: " + dup_city + (use_address.equals("R") ? (dup_city.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">State: " + dup_state + (use_address.equals("R") ? (dup_state.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Zip: " + dup_zip + (use_address.equals("R") ? (dup_zip.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                }

                // Gender
                if (!use_gender.equals("N")) {
                    out.println("<tr><td align=\"left\">Gender: " + dup_gender + (use_gender.equals("R") ? (dup_gender.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                }

                // Handicap Number
                if (!use_hdcp_num.equals("N")) {
                    out.println("<tr><td align=\"left\">Handicap Number: " + dup_hdcp_num + (use_hdcp_num.equals("R") ? (dup_hdcp_num.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                }

                // Handicap Index
                if (!use_hdcp_index.equals("N")) {
                    out.println("<tr><td align=\"left\">Handicap Index: " + (dup_hdcp_index == -99 ? "" : hdcp_index) + (use_hdcp_index.equals("R") ? (dup_hdcp_index == -99 ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                }

                // Home Club
                if (!use_home_club.equals("N")) {
                    out.println("<tr><td align=\"left\">Home Club: " + dup_home_club + (use_home_club.equals("R") ? (dup_home_club.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                }

                // Inactive Button
                if (isProshop && guest_id > 0) {
                    out.println("<tr><td align=\"left\">Inactive: " + (dup_inact == 1 ? "Yes" : "No") + "</td></tr>");
                }

                // Print form and submit button to merge to this guest
                out.println("<form action=\"Common_guestdb\" method=\"POST\" name=\"mergeToEditedForm\">");
                out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
                out.println("<input type=\"hidden\" name=\"target_unique_id\" value=\"" + dup_unique_id + "\">");
                out.println("<input type=\"hidden\" name=\"target_id\" value=\"" + dup_guest_id + "\">");
                out.println("<input type=\"hidden\" name=\"merged_id\" value=\"" + guest_id + "\">");
                out.println("<tr><td align=\"center\" colspan=\"2\">");
                out.println("<input class=\"btnNorm\" type=\"submit\" name=\"mergeGuest\" value=\"Merge into this record\">");
                out.println("</td></tr>");
                out.println("</form>");

                out.println("</table>");    // End right side table

                out.println("</td></tr>");
                out.println("<tr><td align=\"center\" colspan=\"2\"><br>");

                // Print return button to bring user back to guest info form
                out.println("<form action=\"Common_guestdb\" method=\"POST\" name=\"returnForm\">");

                out.println("<input type=\"hidden\" name=\"return\" value=\"1\">");
                out.println("<input type=\"hidden\" name=\"guest_id\" value=\"" + dup_guest_id + "\">");
                if (!caller.equals("")) {
                    out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
                }
                if (!dup_unique_id.equals("")) {
                    out.println("<input type=\"hidden\" name=\"unique_id\" value=\"" + dup_unique_id + "\">");
                }
                if (!dup_uid_type.equals("")) {
                    out.println("<input type=\"hidden\" name=\"uid_type\" value=\"" + dup_uid_type + "\">");
                }
                if (!dup_name_pre.equals("")) {
                    out.println("<input type=\"hidden\" name=\"name_pre\" value=\"" + dup_name_pre + "\">");
                }
                if (!dup_name_first.equals("")) {
                    out.println("<input type=\"hidden\" name=\"name_first\" value=\"" + dup_name_first + "\">");
                }
                if (!dup_name_mi.equals("")) {
                    out.println("<input type=\"hidden\" name=\"name_mi\" value=\"" + dup_name_mi + "\">");
                }
                if (!dup_name_last.equals("")) {
                    out.println("<input type=\"hidden\" name=\"name_last\" value=\"" + dup_name_last + "\">");
                }
                if (!dup_name_suf.equals("")) {
                    out.println("<input type=\"hidden\" name=\"name_suf\" value=\"" + dup_name_suf + "\">");
                }
                if (!dup_email1.equals("")) {
                    out.println("<input type=\"hidden\" name=\"email1\" value=\"" + dup_email1 + "\">");
                }
                if (!dup_email2.equals("")) {
                    out.println("<input type=\"hidden\" name=\"email2\" value=\"" + dup_email2 + "\">");
                }
                if (dup_email_bounced1 != 0) {
                    out.println("<input type=\"hidden\" name=\"email_bounced1\" value=\"" + dup_email_bounced1 + "\">");
                }
                if (dup_email_bounced2 != 0) {
                    out.println("<input type=\"hidden\" name=\"email_bounced2\" value=\"" + dup_email_bounced2 + "\">");
                }
                if (dup_emailOpt != 0) {
                    out.println("<input type=\"hidden\" name=\"emailOpt\" value=\"" + dup_emailOpt + "\">");
                }
                if (!dup_phone1.equals("")) {
                    out.println("<input type=\"hidden\" name=\"phone1\" value=\"" + dup_phone1 + "\">");
                }
                if (!dup_phone2.equals("")) {
                    out.println("<input type=\"hidden\" name=\"phone2\" value=\"" + dup_phone2 + "\">");
                }
                if (!dup_address1.equals("")) {
                    out.println("<input type=\"hidden\" name=\"address1\" value=\"" + dup_address1 + "\">");
                }
                if (!dup_address2.equals("")) {
                    out.println("<input type=\"hidden\" name=\"address2\" value=\"" + dup_address2 + "\">");
                }
                if (!dup_city.equals("")) {
                    out.println("<input type=\"hidden\" name=\"city\" value=\"" + dup_city + "\">");
                }
                if (!dup_state.equals("")) {
                    out.println("<input type=\"hidden\" name=\"state\" value=\"" + dup_state + "\">");
                }
                if (!dup_zip.equals("")) {
                    out.println("<input type=\"hidden\" name=\"zip\" value=\"" + dup_zip + "\">");
                }
                if (!dup_gender.equals("")) {
                    out.println("<input type=\"hidden\" name=\"gender\" value=\"" + dup_gender + "\">");
                }
                if (!dup_hdcp_num.equals("")) {
                    out.println("<input type=\"hidden\" name=\"hdcp_num\" value=\"" + dup_hdcp_num + "\">");
                }
                if (dup_hdcp_index != -99) {
                    out.println("<input type=\"hidden\" name=\"hdcp_index\" value=\"" + dup_hdcp_index + "\">");
                }
                if (!dup_home_club.equals("")) {
                    out.println("<input type=\"hidden\" name=\"home_club\" value=\"" + dup_home_club + "\">");
                }
                if (dup_inact == 1) {
                    out.println("<input type=\"hidden\" name=\"inact\" value=\"" + dup_inact + "\">");
                }

                if (caller.equals("modal")) {
                    out.println("<input class=\"btnNorm\" type=\"submit\" name=\"modal\" value=\"Return to Guest Info Form\">");
                } else {
                    out.println("<input class=\"btnNorm\" type=\"submit\" name=\"guestForm\" value=\"Return to Guest Info Form\">");
                }

                out.println("</form>");

                out.println("</td></tr>");
                out.println("</table>");   // End layout table
                out.println("</CENTER>");


            } else {    // Clashing guest is a brand new entry and not a pre-existing guest.  


                // Data collected, now print out the two options
                out.println("<table border=\"0\" cellpadding=\"5\" bgcolor=\"#F5F5DC\" align=\"center\">");

                // Print message/instructions
                out.println("<tr align=\"center\"><td align=\"center\" width=\"650\">");
                out.println("Another guest is already using this Unique ID.  If the existing guest matches the guest you were "
                        + "<br>attempting to add, click the 'Use Existing Record' button to use the existing record instead.  "
                        + "<br>If the existing guest does not match the guest you were trying to add, please use the 'Return to "
                        + "<br>Guest Info Form' button to return to the previous page and enter a different Unique ID");
                if (caller.equals("modal")) {
                    out.println("<br><br>(<b>Note:</b> If the existing record is used, the member they are assigned to in this "
                            + "<br>reservation will be added as a host for this guest)");
                }
                out.println("</td></tr>");

                out.println("<tr><td align=\"center\">");

                // Print existing record (left side)
                out.println("<table border=\"0\" bgcolor=\"#F5F5DC\">");

                // Unique ID
                if (isProshop && use_uid.equals("R")) {
                    out.println("<tr><td align=\"left\">Unique ID: " + unique_id + (use_uid.equals("R") ? (unique_id.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    //out.println("<tr><td align=\"left\">Unique ID Type: " + uid_type + "</td></tr>");
                }

                // Name
                if (!use_name.equals("N")) {

                    String name_display = (!name_pre.equals("") ? name_pre + " " : "") + name_first + " " + (!name_mi.equals("") ? name_mi + " " : "") + name_last + (!name_suf.equals("") ? " " + name_suf : "");

                    out.println("<tr><td align=\"left\">Name: " + name_display + "</td></tr>");

                    /*
                    out.println("<tr><td align=\"left\">Prefix: " + name_pre + "</td></tr>");
                    out.println("<tr><td align=\"left\">First Name: " + name_first + (use_name.equals("R") ? (name_first.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Middle Initial: " + name_mi + "</td></tr>");
                    out.println("<tr><td align=\"left\">Last Name: " + name_last + (use_name.equals("R") ? (name_last.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Suffix: " + name_suf + "</td></tr>");
                     */
                }

                // Email
                if (!use_email.equals("N")) {
                    out.println("<tr><td align=\"left\">Email 1: " + email1 + (use_email.equals("R") ? (email1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Email 2: " + email2 + "</td></tr>");
                }

                // Phone
                if (!use_phone.equals("N")) {
                    out.println("<tr><td align=\"left\">Phone 1: " + phone1 + (use_phone.equals("R") ? (phone1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Phone 2: " + phone2 + "</td></tr>");
                }

                // Address
                if (!use_address.equals("N")) {
                    out.println("<tr><td align=\"left\">Address 1: " + address1 + (use_address.equals("R") ? (address1.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Address 2: " + address2 + "</td></tr>");
                    out.println("<tr><td align=\"left\">City: " + city + (use_address.equals("R") ? (city.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">State: " + state + (use_address.equals("R") ? (state.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                    out.println("<tr><td align=\"left\">Zip: " + zip + (use_address.equals("R") ? (zip.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                }

                // Gender
                if (!use_gender.equals("N")) {
                    out.println("<tr><td align=\"left\">Gender: " + gender + (use_gender.equals("R") ? (gender.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                }

                // Handicap Number
                if (!use_hdcp_num.equals("N")) {
                    out.println("<tr><td align=\"left\">Handicap Number: " + hdcp_num + (use_hdcp_num.equals("R") ? (hdcp_num.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                }

                // Handicap Index
                if (!use_hdcp_index.equals("N")) {
                    out.println("<tr><td align=\"left\">Handicap Number: " + (hdcp_index == -99 ? "" : hdcp_index) + (use_hdcp_index.equals("R") ? (hdcp_index == -99 ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                }

                // Home Club
                if (!use_home_club.equals("N")) {
                    out.println("<tr><td align=\"left\">Home Club: " + home_club + (use_home_club.equals("R") ? (home_club.equals("") ? "<font color=red> *</font>" : " *") : "") + "</td></tr>");
                }

                // Inactive Button
                if (isProshop && guest_id > 0) {
                    out.println("<tr><td align=\"left\">Inactive: " + (inact == 1 ? "Yes" : "No") + "</td></tr>");
                }

                out.println("</table>");    // End left side table

                out.println("</td></tr>");

                // Print return button to bring user back to guest info form
                out.println("<form action=\"Common_guestdb\" method=\"POST\" name=\"returnForm\">");

                out.println("<tr><td align=\"center\">");

                out.println("<input type=\"hidden\" name=\"return\" value=\"1\">");
                out.println("<input type=\"hidden\" name=\"guest_id\" value=\"" + dup_guest_id + "\">");
                if (!caller.equals("")) {
                    out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
                }
                if (!dup_unique_id.equals("")) {
                    out.println("<input type=\"hidden\" name=\"unique_id\" value=\"" + dup_unique_id + "\">");
                }
                if (!dup_uid_type.equals("")) {
                    out.println("<input type=\"hidden\" name=\"uid_type\" value=\"" + dup_uid_type + "\">");
                }
                if (!dup_name_pre.equals("")) {
                    out.println("<input type=\"hidden\" name=\"name_pre\" value=\"" + dup_name_pre + "\">");
                }
                if (!dup_name_first.equals("")) {
                    out.println("<input type=\"hidden\" name=\"name_first\" value=\"" + dup_name_first + "\">");
                }
                if (!dup_name_mi.equals("")) {
                    out.println("<input type=\"hidden\" name=\"name_mi\" value=\"" + dup_name_mi + "\">");
                }
                if (!dup_name_last.equals("")) {
                    out.println("<input type=\"hidden\" name=\"name_last\" value=\"" + dup_name_last + "\">");
                }
                if (!dup_name_suf.equals("")) {
                    out.println("<input type=\"hidden\" name=\"name_suf\" value=\"" + dup_name_suf + "\">");
                }
                if (!dup_email1.equals("")) {
                    out.println("<input type=\"hidden\" name=\"email1\" value=\"" + dup_email1 + "\">");
                }
                if (!dup_email2.equals("")) {
                    out.println("<input type=\"hidden\" name=\"email2\" value=\"" + dup_email2 + "\">");
                }
                if (dup_email_bounced1 != 0) {
                    out.println("<input type=\"hidden\" name=\"email_bounced1\" value=\"" + dup_email_bounced1 + "\">");
                }
                if (dup_email_bounced2 != 0) {
                    out.println("<input type=\"hidden\" name=\"email_bounced2\" value=\"" + dup_email_bounced2 + "\">");
                }
                if (dup_emailOpt != 0) {
                    out.println("<input type=\"hidden\" name=\"emailOpt\" value=\"" + dup_emailOpt + "\">");
                }
                if (!dup_phone1.equals("")) {
                    out.println("<input type=\"hidden\" name=\"phone1\" value=\"" + dup_phone1 + "\">");
                }
                if (!dup_phone2.equals("")) {
                    out.println("<input type=\"hidden\" name=\"phone2\" value=\"" + dup_phone2 + "\">");
                }
                if (!dup_address1.equals("")) {
                    out.println("<input type=\"hidden\" name=\"address1\" value=\"" + dup_address1 + "\">");
                }
                if (!dup_address2.equals("")) {
                    out.println("<input type=\"hidden\" name=\"address2\" value=\"" + dup_address2 + "\">");
                }
                if (!dup_city.equals("")) {
                    out.println("<input type=\"hidden\" name=\"city\" value=\"" + dup_city + "\">");
                }
                if (!dup_state.equals("")) {
                    out.println("<input type=\"hidden\" name=\"state\" value=\"" + dup_state + "\">");
                }
                if (!dup_zip.equals("")) {
                    out.println("<input type=\"hidden\" name=\"zip\" value=\"" + dup_zip + "\">");
                }
                if (!dup_gender.equals("")) {
                    out.println("<input type=\"hidden\" name=\"gender\" value=\"" + dup_gender + "\">");
                }
                if (!dup_hdcp_num.equals("")) {
                    out.println("<input type=\"hidden\" name=\"hdcp_num\" value=\"" + dup_hdcp_num + "\">");
                }
                if (dup_hdcp_index != -99) {
                    out.println("<input type=\"hidden\" name=\"hdcp_index\" value=\"" + dup_hdcp_index + "\">");
                }
                if (!dup_home_club.equals("")) {
                    out.println("<input type=\"hidden\" name=\"home_club\" value=\"" + dup_home_club + "\">");
                }
                if (dup_inact == 1) {
                    out.println("<input type=\"hidden\" name=\"inact\" value=\"" + dup_inact + "\">");
                }

                if (caller.equals("modal")) {
                    out.println("<input class=\"btnNorm\" type=\"button\" name=\"useGuestBtn\" value=\"Use Existing Record\" onclick=\"passguest('" + guest_name + ":" + guest_id + "');\">&nbsp;&nbsp;");
                    out.println("<input class=\"btnNorm\" type=\"submit\" name=\"modal\" value=\"Return to Guest Info Form\">");
                } else {
                    out.println("<input class=\"btnNorm\" type=\"button\" name=\"useGuestBtn\" value=\"Use Existing Record\" onclick=\"location.href='Common_guestdb'\">&nbsp;&nbsp;");
                    out.println("<input class=\"btnNorm\" type=\"submit\" name=\"guestForm\" value=\"Return to Guest Info Form\">");
                }

                out.println("</td></tr>");
                out.println("</form>");
                out.println("</table>");
            }

            if (caller.equals("modal")) {
                out.println("</div>");
            }
            out.println("</body>");

        } else {
            error = true;   // No unique_id found
        }

        out.close();
    }

    /**
     * mergeGuestID - Performs all necessary database updates associated with merging two existing guest_ids into one.
     *
     * @param target_id Guest_id that another is being merged into
     * @param merged_id Guest_id to be merged into the target_id and then removed from the database
     * @param con Connection to club database
     *
     * @return error - True if error encountered, false if not.
     */
    public static boolean mergeGuestID(String target_unique_id, int target_id, int merged_id, Connection con) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean error = false;

        String target_name = "";
        String merged_name = "";

        String email1 = "";
        String phone1 = "";
        String address1 = "";
        String address2 = "";
        String city = "";
        String state = "";
        String zip = "";
        String gender = "";
        String hdcp_num = "";
        String home_club = "";

        double hdcp_index = 0.0;

        // See if the merged_id record has any information that the target_id record does not.  Port over to target_id record if so.
        try {

            // Gather up fields for target_id's record
            pstmt = con.prepareStatement("SELECT * FROM guestdb_data WHERE guest_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, target_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                email1 = rs.getString("email1");
                phone1 = rs.getString("phone1");
                address1 = rs.getString("address1");
                address2 = rs.getString("address2");
                city = rs.getString("city");
                state = rs.getString("state");
                zip = rs.getString("zip");
                gender = rs.getString("gender");
                hdcp_num = rs.getString("hdcp_num");
                hdcp_index = rs.getDouble("hdcp_index");
                home_club = rs.getString("home_club");
            }

            pstmt.close();

            // Query up merged_id record data for comparison
            pstmt = con.prepareStatement("SELECT * FROM guestdb_data WHERE guest_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, merged_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                // If target_id record is missing a value that is contained in the merged_id record, copy it over to the target_id record
                if (email1.equals("") && !rs.getString("email1").equals("")) {
                    email1 = rs.getString("email1");
                }
                if (email1.equals("") && !rs.getString("email2").equals("")) {
                    email1 = rs.getString("email2");
                }
                if (phone1.equals("") && !rs.getString("phone1").equals("")) {
                    phone1 = rs.getString("phone1");
                }
                if (phone1.equals("") && !rs.getString("phone2").equals("")) {
                    phone1 = rs.getString("phone2");
                }
                if (address1.equals("") && !rs.getString("address1").equals("")) {
                    address1 = rs.getString("address1");
                }
                if (address2.equals("") && !rs.getString("address2").equals("")) {
                    address2 = rs.getString("address2");
                }
                if (city.equals("") && !rs.getString("city").equals("")) {
                    city = rs.getString("city");
                }
                if (state.equals("") && !rs.getString("state").equals("")) {
                    state = rs.getString("state");
                }
                if (zip.equals("") && !rs.getString("zip").equals("")) {
                    zip = rs.getString("zip");
                }
                if (gender.equals("") && !rs.getString("gender").equals("")) {
                    gender = rs.getString("gender");
                }
                if (hdcp_num.equals("") && !rs.getString("hdcp_num").equals("")) {
                    hdcp_num = rs.getString("hdcp_num");
                }
                if (hdcp_index == 0.0 && rs.getDouble("hdcp_index") != 0.0) {
                    hdcp_index = rs.getDouble("hdcp_index");
                }
                if (home_club.equals("") && !rs.getString("home_club").equals("")) {
                    home_club = rs.getString("home_club");
                }
            }

            pstmt.close();

            // Update target_id record with merged data
            pstmt = con.prepareStatement("UPDATE guestdb_data SET "
                    + "email1 = ?, phone1 = ?, address1 = ?, address2 = ?, city = ?, "
                    + "state = ?, zip = ?, gender = ?, hdcp_num = ?, hdcp_index = ?, "
                    + "home_club = ? "
                    + "WHERE guest_id = ?");
            pstmt.clearParameters();
            pstmt.setString(1, email1);
            pstmt.setString(2, phone1);
            pstmt.setString(3, address1);
            pstmt.setString(4, address2);
            pstmt.setString(5, city);
            pstmt.setString(6, state);
            pstmt.setString(7, zip);
            pstmt.setString(8, gender);
            pstmt.setString(9, hdcp_num);
            pstmt.setDouble(10, hdcp_index);
            pstmt.setString(11, home_club);

            pstmt.setInt(12, target_id);

            pstmt.executeUpdate();

            pstmt.close();

        } catch (Exception exc) {
            // Continue merge processing
        }

        // Get the player names for both IDs to see if any changes occurred.
        try {

            // Build common query string
            String query = "SELECT CONCAT(name_first, ' ', IF(name_mi != '', CONCAT(name_mi, ' '), ''), name_last) as guest_name "
                    + "FROM guestdb_data "
                    + "WHERE guest_id = ?";

            // Get guest name for target_id
            pstmt = con.prepareStatement(query);
            pstmt.clearParameters();
            pstmt.setInt(1, target_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                target_name = rs.getString("guest_name");
            }

            pstmt.close();

            // Get guest name for merged_id
            pstmt = con.prepareStatement(query);
            pstmt.clearParameters();
            pstmt.setInt(1, merged_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                merged_name = rs.getString("guest_name");
            }

            pstmt.close();

        } catch (Exception exc) {
            error = true;
        }

        // If the name has changed, then callupdateGuestNames() to apply the change
        if (!error && !target_name.equals("") && !merged_name.equals("") && !target_name.equals(merged_name)) {
            error = updateGuestNames(merged_id, target_name, merged_name, con);
        }

        // Update teecurr2
        if (!error) {
            error = convertGuestId("teecurr2", "guest_id1", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("teecurr2", "guest_id2", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("teecurr2", "guest_id3", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("teecurr2", "guest_id4", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("teecurr2", "guest_id5", target_id, merged_id, con);
        }

        // Update teepast2
        if (!error) {
            error = convertGuestId("teepast2", "guest_id1", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("teepast2", "guest_id2", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("teepast2", "guest_id3", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("teepast2", "guest_id4", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("teepast2", "guest_id5", target_id, merged_id, con);
        }

        // Update evntsup2b
        if (!error) {
            error = convertGuestId("evntsup2b", "guest_id1", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("evntsup2b", "guest_id2", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("evntsup2b", "guest_id3", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("evntsup2b", "guest_id4", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("evntsup2b", "guest_id5", target_id, merged_id, con);
        }

        // Update lreqs3
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id1", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id2", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id3", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id4", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id5", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id6", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id7", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id8", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id9", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id10", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id11", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id12", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id13", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id14", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id15", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id16", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id17", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id18", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id19", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id20", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id21", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id22", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id23", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id24", target_id, merged_id, con);
        }
        if (!error) {
            error = convertGuestId("lreqs3", "guest_id25", target_id, merged_id, con);
        }

        // Update activity_sheets_players
        if (!error) {
            error = convertGuestId("activity_sheets_players", "guest_id", target_id, merged_id, con);
        }

        // Update lreqs3
        if (!error) {
            error = convertGuestId("wait_list_signups_players", "guest_id", target_id, merged_id, con);
        }

        // Delete any duplicate host entries between the two guest_ids in guestdb_hosts
        if (!error) {
            error = removeDuplicateHosts(target_id, merged_id, con);
        }

        // Update guestdb_hosts
        if (!error) {
            error = convertGuestId("guestdb_hosts", "guest_id", target_id, merged_id, con);
        }

        // Remove guestdb_data entry for merged_id
        if (!error) {
            error = removeGuestId(merged_id, con);
        }

        // Finally, update unique_id to the new value if one was provided
        if (!error && !target_unique_id.equals("")) {
            error = convertUniqueId("guestdb_data", "unique_id", "guest_id", target_unique_id, target_id, con);
        }

        return error;
    }

    /**
     * updateGuestNames - Applies a name change, where needed, to all possible locations where this guest's name is stored.
     *
     * @param guest_id Id for guest which name change is to be applied for
     * @param name_new Name after name change (first mi last)
     * @param name_old Name prior to name change (first mi last)
     * @param con Connection to club database
     *
     * @return error - True if error encountered, false if not
     */
    public static boolean updateGuestNames(int guest_id, String name_new, String name_old, Connection con) {

        boolean error = false;

        // Update teecurr2
        if (!error) {
            error = convertGuestName("teecurr2", "teecurr_id", "guest_id1", "player1", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("teecurr2", "teecurr_id", "guest_id2", "player2", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("teecurr2", "teecurr_id", "guest_id3", "player3", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("teecurr2", "teecurr_id", "guest_id4", "player4", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("teecurr2", "teecurr_id", "guest_id5", "player5", name_new, name_old, guest_id, con);
        }

        // Update teepast2
        if (!error) {
            error = convertGuestName("teepast2", "teepast_id", "guest_id1", "player1", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("teepast2", "teepast_id", "guest_id2", "player2", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("teepast2", "teepast_id", "guest_id3", "player3", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("teepast2", "teepast_id", "guest_id4", "player4", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("teepast2", "teepast_id", "guest_id5", "player5", name_new, name_old, guest_id, con);
        }

        // Update evntsup2b
        if (!error) {
            error = convertGuestName("evntsup2b", "id", "guest_id1", "player1", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("evntsup2b", "id", "guest_id2", "player2", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("evntsup2b", "id", "guest_id3", "player3", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("evntsup2b", "id", "guest_id4", "player4", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("evntsup2b", "id", "guest_id5", "player5", name_new, name_old, guest_id, con);
        }

        // Update lreqs3
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id1", "player1", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id2", "player2", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id3", "player3", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id4", "player4", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id5", "player5", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id6", "player6", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id7", "player7", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id8", "player8", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id9", "player9", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id10", "player10", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id11", "player11", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id12", "player12", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id13", "player13", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id14", "player14", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id15", "player15", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id16", "player16", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id17", "player17", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id18", "player18", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id19", "player19", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id20", "player20", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id21", "player21", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id22", "player22", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id23", "player23", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id24", "player24", name_new, name_old, guest_id, con);
        }
        if (!error) {
            error = convertGuestName("lreqs3", "id", "guest_id25", "player25", name_new, name_old, guest_id, con);
        }

        // Update activity_sheets_players
        if (!error) {
            error = convertGuestName("activity_sheets_players", "activity_sheets_player_id", "guest_id", "player_name", name_new, name_old, guest_id, con);
        }

        // Update lreqs3
        if (!error) {
            error = convertGuestName("wait_list_signups_players", "wait_list_signup_player_id", "guest_id", "player_name", name_new, name_old, guest_id, con);
        }

        return error;
    }

    /**
     * convertGuestId - Converts a single database field from containing the merged_id to the target_id
     *
     * @param table Name of database table to be updated
     * @param guestid_field Name of database field in 'table' to be updated
     * @param target_id guest_id that the merged_id is in the process of being merged into.
     * @param merged_id guest_id that is being merged into the target_id
     * @param con Connection to club database
     *
     * @return error - True if error encountered, false if not.
     */
    private static boolean convertGuestId(String table, String guestid_field, int target_id, int merged_id, Connection con) {

        PreparedStatement pstmt = null;

        String query = "";

        int count = 0;

        boolean error = false;

        // Formulate query string
        query = "UPDATE " + table + " SET " + guestid_field + " = ? WHERE " + guestid_field + " = ?";

        // Apply update
        try {
            pstmt = con.prepareStatement(query);
            pstmt.clearParameters();
            pstmt.setInt(1, target_id);
            pstmt.setInt(2, merged_id);

            count = pstmt.executeUpdate();

            pstmt.close();

        } catch (Exception exc) {
            error = true;
        }

        return error;
    }

    /**
     * convertGuestId - Converts a single database field from containing the merged_id to the target_id
     *
     * @param table Name of database table to be updated
     * @param uid_field Name of database field holding the unique_id
     * @param guestid_field Name of the database field holding the guest_id
     * @param target_unique_id unique_id that the target_id should have after the merge
     * @param target_id guest_id that the merged guest is being merged into
     * @param con Connection to club database
     *
     * @return error - True if error encountered, false if not.
     */
    private static boolean convertUniqueId(String table, String uid_field, String guestid_field, String target_unique_id, int target_id, Connection con) {

        PreparedStatement pstmt = null;

        String query = "";

        int count = 0;

        boolean error = false;

        // Formulate query string
        query = "UPDATE " + table + " SET " + uid_field + " = ? WHERE " + guestid_field + " = ?";

        // Apply update
        try {
            pstmt = con.prepareStatement(query);
            pstmt.clearParameters();
            pstmt.setString(1, target_unique_id);
            pstmt.setInt(2, target_id);

            count = pstmt.executeUpdate();

            pstmt.close();

        } catch (Exception exc) {
            error = true;
        }

        return error;
    }

    /**
     * convertGuestName - Attempts to convert the guest name in a given database entry specified by parameters
     *
     * @param table Name of database table to be updated
     * @param uniqueid_field Name of unique identifier field for entries in the current database table
     * @param guestid_field Name of the guest_id field in question for this database table
     * @param name_field Name of the player name field in question for this database table
     * @param name_new Name to be applied in place of name_old
     * @param name_old Name to be overwritten with name_new
     * @param guest_id Guest_ID for this guest, used to identify them in reservations
     * @param con Connection to club database
     *
     * @return error - True if error encountered, false if not
     */
    private static boolean convertGuestName(String table, String uniqueid_field, String guestid_field, String name_field, String name_new, String name_old, int guest_id, Connection con) {

        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;

        String player_name = "";
        String player_new = "";

        int count = 0;
        int uniqueid = 0;

        boolean error = false;

        // Gather all database entries that need updating
        try {

            pstmt = con.prepareStatement("SELECT " + uniqueid_field + ", " + name_field + " FROM " + table + " WHERE " + guestid_field + " = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, guest_id);
            rs = pstmt.executeQuery();

            // Loop through all entries and
            while (rs.next()) {

                // Get uniqueid and player_name for this entry
                uniqueid = rs.getInt(uniqueid_field);
                player_name = rs.getString(name_field);

                // If the player name contains the old name, replace it with the new name and update the teecurr entry
                if (player_name.contains(name_old)) {

                    player_new = player_name.replace(name_old, name_new);

                    // Update database entry with new name and guest_id
                    pstmt2 = con.prepareStatement(
                            "UPDATE " + table + " "
                            + "SET " + name_field + " = ? "
                            + "WHERE " + uniqueid_field + " = ?");
                    pstmt2.clearParameters();
                    pstmt2.setString(1, player_new);
                    pstmt2.setInt(2, uniqueid);

                    count = pstmt2.executeUpdate();

                    pstmt2.close();
                }
            }

            pstmt.close();

        } catch (Exception exc) {
            error = true;
        }

        return error;
    }

    /**
     * removeDuplicateHosts - Searches for any members that are hosts of both the target and merged guest_id.  If found, the merged_id copy is deleted.
     *
     * @param target_id guest_id that the merged_id is in the process of being merged into.
     * @param merged_id guest_id that is being merged into the target_id
     * @param con Connection to club database
     *
     * @return error - True if error encountered, false if not.
     */
    private static boolean removeDuplicateHosts(int target_id, int merged_id, Connection con) {

        PreparedStatement pstmt = null;

        ResultSet rs = null;

        String hostList = "";

        int count = 0;

        boolean error = false;

        try {

            pstmt = con.prepareStatement("SELECT username FROM guestdb_hosts WHERE guest_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, target_id);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                if (hostList.equals("")) {
                    hostList = "'" + rs.getString("username") + "'";
                } else {
                    hostList += ", '" + rs.getString("username") + "'";
                }
            }

            pstmt.close();

            if (!hostList.equals("")) {
                pstmt = con.prepareStatement("DELETE FROM guestdb_hosts WHERE guest_id = ? AND username IN (" + hostList + ")");
                pstmt.clearParameters();
                pstmt.setInt(1, merged_id);

                count = pstmt.executeUpdate();

                pstmt.close();
            }

        } catch (Exception exc) {
            error = true;
        }

        return error;
    }

    private static boolean removeGuest(int guest_id, Connection con) {

        boolean error = false;

        return error;
    }

    /**
     * removeGuestId - Removes guestdb_data entry for the passed guest_id.
     *
     * @param guest_id guest_id that is to be removed
     * @param con Connection to club database
     *
     * @return error - True if error encountered, false if not.
     */
    private static boolean removeGuestId(int guest_id, Connection con) {

        PreparedStatement pstmt = null;

        int count = 0;

        boolean error = false;

        try {
            // Remove the entry for this guest from guestdb_data
            pstmt = con.prepareStatement("DELETE FROM guestdb_data WHERE guest_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, guest_id);

            count = pstmt.executeUpdate();

            pstmt.close();

            // If no record was removed, flag as error
            if (count == 0) {
                error = true;
            }

        } catch (Exception exc) {
            error = true;
        }

        return error;
    }

    private static void loadModal(HttpServletRequest req, HttpSession session, int activity_id, String user, PrintWriter out, Connection con) {
        Map<String, Object> temp_map = loadModal(req, session, activity_id, user, out, con, false);
    }

    private static Map<String, Object> loadModal(HttpServletRequest req, HttpSession session, int activity_id, String user, PrintWriter out, Connection con, boolean generateMap) {

        Map<String, Object> result_map = new LinkedHashMap<String, Object>();

        List<String> message_list = new ArrayList<String>();

        boolean isProshop = false;

        if (ProcessConstants.isProshopUser(user) || Utilities.isHotelUser(user, con)) {
            isProshop = true;
        }

        if (!generateMap) {

            out.println(SystemUtils.HeadTitle("Guest Selection"));

            out.println("<style>");

            out.println(".btnNorm {");
            out.println("  font: normal 11px \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;");
            out.println("  background: #99CC66;");
            out.println("  width: 100px;");
            out.println("}");

            out.println("</style>");

            out.println("<!-- ******** BEGIN LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->"
                    + "<script type=\"text/javascript\">var lwmwLinkedBy=\"LiknoWebModalWindows [1]\",lwmwName=\"foretees-modal\",lwmwBN=\"128\";awmAltUrl=\"\";</script>"
                    + "<script charset=\"UTF-8\" src=\"/" + rev + "/web%20utilities/foretees-modal.js\" type=\"text/javascript\"></script>"
                    + "<!-- ******** END LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->");

            printModalScripts(out);

            //out.println("<body bgcolor=\"#F5F5DC\" text=\"#000000\" onload=\"parent.window.resizeIFrame(document.getElementById('modaliframediv').offsetHeight + 50, 'modaliframe');\">");
            out.println("<body bgcolor=\"#F5F5DC\" text=\"#000000\" onload=\"parent.window.resizeIFrame(document.getElementById('modaliframediv').offsetHeight + 50, 'modaliframe');\">");
/*
            if (isProshop) {
                int lottery = Integer.parseInt((String) session.getAttribute("lottery"));        // get lottery support indicator
                SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
            } else {
                String sess_caller = (String) session.getAttribute("caller");
                SystemUtils.getMemberSubMenu(req, out, sess_caller);        // required to allow submenus on this page
            }*/
            out.println("<div id=\"modaliframediv\">");

            // Start the layout table
            out.println("<table border=\"0\" bgcolor=\"#F5F5Dc\" width=\"100%\">");

            // Header row for instructions
            out.println("<tr><td align=\"center\" colspan=\"2\">");
        }
        if (generateMap) {
            message_list.add("Please select an existing guest from the list to the right, or add a new guest using the form below.");
        } else {
            out.println("Please select an existing guest from the list to the right, or add a new guest using the form below.");
        }
        if (Utilities.isGuestTrackingTbaAllowed(activity_id, isProshop, con)) {
            if (generateMap) {
                message_list.add("If you do not know the name of your guest at this time, please select 'TBA' from the list.");
                result_map.put("use_tba", true);
            } else {
                out.println("<br>If you do not know the name of your guest at this time, please select 'TBA' from the list.");
            }
        } else if (generateMap) {
            result_map.put("use_tba", false);
        }
        if (!generateMap) {
            out.println("<br><br>");
            out.println("</td></tr>");

            // Print information fields
            out.println("<tr>");

            // Left hand side - Guest add/edit form
            out.println("<td valign=\"top\" align=\"left\" width=\"80%\">");

            // Start table and form used within printGuestInfoFields method
            out.println("<table align=\"center\" border=\"0\"bgcolor=\"#F5F5DC\">");
            out.println("<form action=\"Common_guestdb\" method=\"POST\" name=\"guestInfo\">");
            out.println("<input type=\"hidden\" name=\"caller\" value=\"modal\">");
        }
        if (generateMap) {

            result_map.put("guest_info_fields", generateGuestInfoFields(req, isProshop, false, activity_id, out, con, true));

        } else {

            printGuestInfoFields(req, isProshop, false, activity_id, out, con);

            // Print submit/cancel button(s) for guest info field form
            out.println("<tr><td align=\"center\" colspan=\"2\">");
            out.println("<br><input type=\"submit\" class=\"btnNorm\" name=\"submitGuestInfo\" value=\"Add Guest\" width=\"120px\">");

            out.println("<input type=\"button\" class=\"btnNorm\" name=\"btnCancel\" value=\"Cancel\" onclick=\"passguest(''); if(top.foretees_modal) { top.foretees_modal.close(); } else { parent.foretees_modal.close(); }\">");

            // Close table/form from printGuestInfoFields
            out.println("</td></tr>");
            out.println("</form>");
            out.println("</table>");

            out.println("</td>");

            out.println("<td valign=\"top\" align=\"right\" width=\"20%\">");
        }

        if (generateMap) {

            result_map.put("guest_db_list", alphaTable.guestdbList(user, 0, activity_id, 0, out, con, true));

        } else {

            alphaTable.guestdbList(user, 0, activity_id, 0, out, con);

            out.println("</td>");

            out.println("</tr>");
            out.println("</table>");

            out.println("</div>");
            /*
            if (getClub.getClubName(con).equals("demobrad")) {
            
            out.println("<style>");
            out.println("#elGUESTPopup {");
            out.println("  position: absolute;");
            out.println("  top: 0px;");
            out.println("  left: -500px;");
            out.println("  width: 290px;");
            out.println("  height: 130px;");
            out.println("  border: 1px black solid;");
            out.println("  background-color: #E6E6E6;");
            out.println("  visibility: hidden;");
            out.println("}");
            out.println("</style>");
            
            out.println("<div id=elGUESTPopup defaultValue=\"\" fb=\"\" nh=\"\" jump=\"\" tid=\"\">");
            out.println("<table width='100%' height='100%' border=0 cellpadding=0 cellspacing=2>");
            out.println("<tr><td align=center><a href=\"javascript: cancelGUESTPopup()\" class=smtext>cancel</a>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <a href=\"javascript: saveTOPopupLite()\" class=smtext>save</a>&nbsp;</td></tr>");
            out.println("</table>");
            out.println("</div>");
            }
             * 
             */

            out.println("</body></html>");
        }

        if (generateMap) {
            result_map.put("message_list", message_list);
        }

        return result_map;
    }

    private static void printModalScripts(PrintWriter out) {

        out.println("<script type=\"text/javascript\">");
        out.println("<!--");

        out.println("function closemodal() {");
        out.println("  if (top.foretees_modal) {");
        out.println("    top.foretees_modal.close();");
        out.println("  } else {");
        out.println("    parent.foretees_modal.close();");
        out.println("  }");
        out.println("}");

        out.println("function passguest(guest_info) {");

        out.println("  var f = parent.document.playerform;");
        out.println("  var f2 = parent.window;");

        out.println("  if (guest_info != '') {");
        out.println("    array = guest_info.split(':');"); // Split passed info into guest_name and guest_id
        out.println("    var guest_name = array[0];");
        out.println("    var guest_id = array[1];");

        out.println("    var i=1;");
        out.println("    var dupguest = new Boolean(false);");

        out.println("    if (guest_name != 'TBA' || guest_id > '0') {");   // Ignore duplicate check for entries that have a guest_name of 'TBA' and guest_id of '0'
        out.println("      while (dupguest == false && eval('f.guest_id'+i+' != undefined')) {");
        out.println("        if (eval('f.guest_id'+i+'.value == guest_id') && guest_name != 'TBA') {");
        out.println("          dupguest = true;");
        out.println("        }");
        out.println("        i = i+1;");
        out.println("      }");
        out.println("    } else if (guest_id == '-1') {");
        out.println("      alert('Please add a new guest using the form to the left.')");
        out.println("    }");

        out.println("    if (dupguest == false) {");
        out.println("      f2.guestid_slot.value = guest_id;");  // Apply the values to existing form elements
        out.println("      f2.player_slot.value += guest_name;");
        out.println("      closemodal();");   // close modal window
        out.println("    } else { ");
        out.println("      alert(guest_name + ' is already a part of this reservation.');");
        out.println("    }");
        out.println("  } else {");
        out.println("    f2.guestid_slot.value = '0';");
        out.println("    f2.player_slot.value = '';");
        out.println("    closemodal();");
        out.println("  }");
        out.println("}");
        out.println("// -->");
        out.println("</script>");
    }

    public static String getGuestName(int guest_id, Connection con) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String guest_name = "";

        if (guest_id == -99) {      // Mobile side passing -99 because no guest name can be passed along with the id

            guest_name = "TBA";

        } else {
            try {
                pstmt = con.prepareStatement(
                        "SELECT CONCAT(name_first, ' ', IF(name_mi != '', CONCAT(name_mi, ' '), ''), name_last) as guest_name "
                        + "FROM guestdb_data "
                        + "WHERE guest_id = ?");
                pstmt.clearParameters();
                pstmt.setInt(1, guest_id);

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    guest_name = rs.getString("guest_name");
                }

                pstmt.close();

            } catch (Exception exc) {
                guest_name = exc.getMessage() + " " + guest_id;
            } // return blank name if error encountered
        }

        return guest_name;
    }
}
