/*
 * Object and utilities for club user verifications
 * 
 * Still much more in SystemUtils that needs to be moved here so non servlet objects can access validation methods.
 * 
 * 
 */
package com.foretees.common;

import com.foretees.api.ApiCommon;
import java.sql.*;          // mysql
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
//import java.lang.*;
//import org.joda.time.DateTimeZone;
import org.apache.commons.codec.binary.Base64;

import java.lang.reflect.*;

import com.google.gson.*;
import com.google.gson.reflect.*; // for json

/**
 *
 * @author Owner
 */
public class VerifyUser {
    
    //
    // Parameters for VerifyUser object
    //
    public Map<String, Object> ssoMap;
    public HttpSession session;
    public Integer activity_id;
    public String club;
    public String user;
    public Long member_id;
    public String ext_user;
    public String htmlResponse; // Long, full HTML document error message.
    public String error; // Short error message. Suitable for API error response.

    
    //
    // Session length for member logins
    //
    public static int MEMBER_TIMEOUT = 15 * 60;
    
    //
    // Messages
    //
    private static final String error_session_timeout = "Access Error:  Sorry, you have not authenticated or your session has timed out.";
    
      // Verify session and return map coontaining session and/or error
    public static VerifyUser verifyMem(HttpServletRequest req) {
        return verifyMem(req, false);
    }

    // Verify session and return map coontaining session and/or error
    public static VerifyUser verifyMem(HttpServletRequest req, boolean allow_ext) {

        String id = "foretees";                   // session id

        String agent = req.getHeader("User-Agent");  // Get user's browser info for error msg

        String errorMsg = "Verify Member: User Verification Error (cookies). Agent: " + agent + ", ";
        
        String error = null; // Short error response for API

        HttpSession session = null;

        StringBuilder resp = new StringBuilder();
        VerifyUser verify_user = new VerifyUser();

        // Check if this is a SSO request
        if (req.getParameter("sso_uid") != null || (req.getParameter("dc") != null && req.getParameter("mid") != null && req.getParameter("cid") != null)) {
            final long benchmark_start = System.currentTimeMillis();
            Map<String, Object> userMap = loadUser(req, "SSO", "", "");
            if (!(Boolean) userMap.get("is_error")) {
                // Looks like a valid SSO request -- establish the session using SSO
                //Connection con = (Connection) userMap.get("db_connection");
                Connection con = Connect.getCon(req);

                String mship = (String) userMap.get("mship");
                String mtype = (String) userMap.get("mtype");
                String msubtype = (String) userMap.get("msubtype");
                String wc = (String) userMap.get("wc");
                String club = (String) userMap.get("club");
                String clubid = (String) userMap.get("clubid");
                String name = (String) userMap.get("name");
                String username = (String) userMap.get("username");
                String user = (String) userMap.get("user");
                String remote_ip = (String) userMap.get("remote_ip");
                long member_id = (Long) userMap.get("member_id");
                String caller = ProcessConstants.FT_PREMIER_CALLER;
                if (req.getParameter("sso_caller") != null) {
                    caller = req.getParameter("sso_caller");
                }
                //out.print("<!-- " + user+"/"+clubid + "-->");
                // get the activity id
                int activity_id = 0;
                String tmp = req.getParameter("s_a") == null ? "" : req.getParameter("s_a");
                try {
                    activity_id = Integer.parseInt(tmp);
                } catch (Exception ignore) {
                }
                try {
                    verify_user.ssoMap = performSSO(req, null, activity_id, user, username, member_id, club, caller, mship, mtype, msubtype, name, wc, remote_ip, benchmark_start, con);
                } catch (Exception ignore) {
                }

            }
        }
        session = req.getSession(false);  // Get user's session object (no new one)

        String sess_id = "";
        String user = "";
        String ext_user = "";
        String name = "";
        String club = "";
        String caller = "";
        long member_id = (long) 0;

        reqUtil.setAppModeFromUri(req, 0);

        boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
        boolean seemless = Utilities.getBitFromRequest(req, ProcessConstants.RQA_APPMODE, ProcessConstants.APPMODE_SEEMLESS);

        if (session == null) {
            //logError(errorMsg + " No Session Found.");
        } else {

            // trap an error if one occurs and fail (caller was null on an expired session - not sure how/why this happens)
            try {

                sess_id = (String) session.getAttribute("sess_id");   // get session id
                user = (String) session.getAttribute("user");         // get username
                ext_user = (String) session.getAttribute("ext-user");         // get ext username
                name = (String) session.getAttribute("name");         // get user's full name
                club = (String) session.getAttribute("club");         // get club's name
                caller = (String) session.getAttribute("caller");     // get caller (none, or AMO or MFirst)
                member_id = reqUtil.getSessionLong(req, "member_id", (long) 0);

            } catch (Exception ignore) {

                session = null;

            }

            if (session == null
                    || ((user == null || sess_id == null) && !allow_ext)
                    || (ext_user == null && (user == null || sess_id == null) && allow_ext)) {

                Connect.logError(errorMsg + " Invalid (null) session info: Allow-ext = " + allow_ext + ", Session id = " + sess_id + ", Ext-User = " + ext_user + ", User = " + user + ", Name = " + name + ", Club = " + club + ", Caller = " + caller);

                session = null;

            } else if (((!allow_ext && (user.isEmpty() || !sess_id.equals(id)))
                    || (allow_ext && ext_user.isEmpty() && (user.isEmpty() || !sess_id.equals(id))))) {

                Connect.logError(errorMsg + " Invalid session info: Session id = " + sess_id + ", Ext-User = " + ext_user + ", User = " + user + ", Name = " + name + ", Club = " + club + ", Caller = " + caller);

                session = null;
            }
        }

        if (req.getParameter("s_c") != null) {
            String club_test = req.getParameter("s_c");
            if (club == null || club.length() < 1 || club_test.equals(club)) {
                club = club_test;
            } else if (session != null) {
                // club mismatch -- error out
                session.invalidate();
                session = null;
                club = club_test;
            }

        }
        Integer set_activity_id = Utilities.getParameterInteger(req, "s_a", null);
        if (set_activity_id == null) {
            set_activity_id = 0;
        } 
        if (session != null) {
            session.setAttribute("activity_id", set_activity_id);
        }
        req.setAttribute(ProcessConstants.RQA_ACTIVITY_ID, set_activity_id);
        switch (set_activity_id) {
            case ProcessConstants.GOLF_ACTIVITY_ID: // Golf
                req.setAttribute(ProcessConstants.RQA_ACTIVITY_MODE, "golf");
                break;
            case ProcessConstants.DINING_ACTIVITY_ID: // Dining
                req.setAttribute(ProcessConstants.RQA_ACTIVITY_MODE, "dining");
                break;
            case ProcessConstants.MANAGERS_PORTAL: // MP
                req.setAttribute(ProcessConstants.RQA_ACTIVITY_MODE, "mp");
                break;
            default: // flxrez
                req.setAttribute(ProcessConstants.RQA_ACTIVITY_MODE, "flxrez");
                break;
        }
        if (Utilities.getBitFromRequest(req, ProcessConstants.RQA_APPMODE, ProcessConstants.APPMODE_BLOCK_RWD_SWITCH)) {
            req.setAttribute(ProcessConstants.RQA_ALLOW_RWD_SWITCH, false);
        } else if (Utilities.clubAllowRwdSwitch(req, club)) {
            req.setAttribute(ProcessConstants.RQA_ALLOW_RWD_SWITCH, true);
        }

        // if we're in appmode for mobile app1 then force some options
        if (Utilities.getBitFromRequest(req, ProcessConstants.RQA_APPMODE, ProcessConstants.APPMODE_MOBILE_APP1)) {

            req.setAttribute(ProcessConstants.RQA_RWD, true);
            req.setAttribute(ProcessConstants.RQA_ALLOW_RWD_SWITCH, false);
            Utilities.setBitInRequest(req, ProcessConstants.RQA_APPMODE, ProcessConstants.APPMODE_SEEMLESS);
            //Utilities.setBitInRequest(req, ProcessConstants.RQA_APPMODE,ProcessConstants.APPMODE_HIDE_TOP_NAV);

        }

        String userChk = "";
        if (user != null) {
            userChk = user.toLowerCase();
        }
        if (session != null
                && (!userChk.startsWith("proshop") || reqUtil.getSessionBoolean(session, "view_as_member", false))
                && !userChk.startsWith("sales")
                && !userChk.startsWith("admin")
                && !userChk.startsWith("support")) {

            verify_user.session = session;

            // Set script debug mode
            if (req.getParameter("s_sdbg") != null) {
                String scriptDebug = req.getParameter("s_sdbg");
                if (scriptDebug.equals("on")) {
                    session.setAttribute("script_debug", true);
                } else {
                    session.setAttribute("script_debug", false);
                }
            }

            verify_user.activity_id = set_activity_id;
            verify_user.club = club;
            verify_user.user = user;
            verify_user.member_id = member_id;
            verify_user.ext_user = ext_user;
            
            // Stamp our last activity
            java.util.Date serverTime = new java.util.Date();
            Long timeStamp = serverTime.getTime();
            session.setAttribute("last_activity_time", timeStamp);

        } else {
            // No session.
            
            Connection con = null;
            String clubName = "";
            if (!club.equals("")) {
                caller = Utilities.getClubCaller(club);
                if (Utilities.getBitFromRequest(req, ProcessConstants.RQA_APPMODE, ProcessConstants.APPMODE_HIDE_SUB_NAV)) {
                    // If we don't have sub-nav, we're probably in premier mode
                    //  (we need a better way for this.  Probably should reserve a mode bit specifically for premier mode)
                    caller = ProcessConstants.FT_PREMIER_CALLER;
                }

                // Get club db connection
                try {
                    con = Connect.getCon(club);
                    clubName = Utilities.getClubName(con, true);
                } catch (Exception exc) {
                }
            }
            
            req.setAttribute(ProcessConstants.RQA_BLOCKUSER, true);

            if (!club.equals("") && !caller.equals(ProcessConstants.FT_PREMIER_CALLER)) { // club was found in url and club does not use seamless
                

                if (rwd) {
                    error = error_session_timeout;
                    req.setAttribute(ProcessConstants.RQA_ACCESS_ERROR, true); // Flag that we're in Access Error mode
                    resp.append(Common_skin.getHeader(club, set_activity_id, "Access Error", true, req, con));
                    resp.append(Common_skin.getBody(club, set_activity_id, req));

                    resp.append("<div id=\"wrapper_login\">");
                    resp.append("<div id=\"title\">");
                    resp.append(clubName);
                    resp.append("</div>");
                    resp.append("<div id=\"main_login\">");

                    resp.append("<CENTER>");
                    resp.append("<H2>Access Error - Please Read</H2>");
                    resp.append("<p>Sorry, your session either timed out, you didn't login, or your computer does not allow the use of Cookies.</p>");
                    resp.append("<p>This site requires the use of Cookies for security purposes.");
                    resp.append("<BR>We use them to verify your session and prevent unauthorized access.</p>");
                    resp.append("<div class=\"sub_instructions\">");
                    resp.append("<p><b>NOTE:</b> You must be logged in to access the ForeTees system. You cannot bookmark a page ");
                    resp.append("within ForeTees and then return to it later without logging in.  If you access ForeTees ");
                    resp.append("from your club web site, then you must login to the web site.  If you access ForeTees ");
                    resp.append("directly, then you must do so through the ForeTees Login page.");
                    resp.append("</div>");
                    resp.append("<div class=\"main_instructions\"><p>If you have tried all of the above and still receive this message, ");
                    resp.append("please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.</p>");
                    resp.append("<p><b>Provide your name or member number, the name of your club and a detailed description of your problem.</b></p>");
                    resp.append("<p>Thank you.</p>");
                    resp.append("</div>");
                    if (!seemless) {
                        resp.append("<p><a class=\"standard_button\" href=\"/");
                        resp.append(club);
                        resp.append("\" target=\"_top\">Return to Login Page</a></p>");
                    } else {
                        resp.append("<p><a class=\"standard_button\" href=\"#\" onclick=\"window.close();return false;\">Close</a></p>");
                    }
                    resp.append("");
                    resp.append("<CENTER>Server: ");
                    resp.append(Common_Server.SERVER_ID);
                    resp.append("</CENTER>");
                    resp.append("</CENTER>");

                    resp.append("</div>");
                    resp.append("</div>");

                    resp.append(Common_skin.getPageEnd(club, set_activity_id, req));
                } else {
                    error = error_session_timeout;
                    resp.append("<!doctype html>");
                    resp.append("<html>");
                    resp.append("<head lang=\"en-US\">");
                    //resp.append("<meta http-equiv=\"Refresh\" content=\"5; url=" + club + "\">");
                    resp.append("<title>Access Error</title>");
                    resp.append("</head>");
                    resp.append("<BODY><CENTER>");
                    resp.append("<H2>Access Error - Please Read</H2>");
                    resp.append("Sorry, your session either timed out, you didn't login, or your computer does not allow the use of Cookies.");
                    resp.append("<BR><BR>This site requires the use of Cookies for security purposes.");
                    resp.append("<BR>We use them to verify your session and prevent unauthorized access.");
                    resp.append("<BR><BR><HR width=\"500\">");
                    resp.append("<b>NOTE:</b> You must be logged in to access the ForeTees system. You cannot bookmark a page");
                    resp.append("<BR>within ForeTees and then return to it later without logging in.  If you access ForeTees");
                    resp.append("<BR>from your club web site, then you must login to the web site.  If you access ForeTees");
                    resp.append("<BR>directly, then you must do so through the ForeTees Login page.");
                    resp.append("<BR><HR width=\"500\"><BR>");
                    resp.append("If you have tried all of the above and still receive this message,");
                    resp.append("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
                    resp.append("<BR><b>Provide your name or member number, the name of your club and a detailed description of your problem.</b>");
                    resp.append("<BR>Thank you.");
                    resp.append("<BR><BR>");
                    if (!seemless) {
                        resp.append("<h3><a href=\"/");
                        resp.append(club);
                        resp.append("\" target=\"_top\">Return to Login Page</a></h3>");
                    } else {
                        resp.append("<h3><a class=\"standard_button\" href=\"#\" onclick=\"window.close();return false;\">Close</a><h3>");
                    }
                    resp.append("<BR><BR>");
                    resp.append("<CENTER>Server: ");
                    resp.append(Common_Server.SERVER_ID);
                    resp.append("</CENTER>");
                    resp.append("</CENTER></BODY></HTML>");
                }

            } else if (caller.equals(ProcessConstants.FT_PREMIER_CALLER)) {

                error = error_session_timeout;
                resp.append("<!DOCTYPE html>");
                resp.append("<html>");
                resp.append("<head>");
                resp.append("<meta name=\"application-name\" content=\"ForeTees\">");
                resp.append("<meta name=\"ft-server-id\" content=\"");
                resp.append(ProcessConstants.SERVER_ID);
                resp.append("\">");
                resp.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");
                resp.append("</head>");
                resp.append("<body>");
                resp.append("<br><center>");
                resp.append("<h2>Sorry, your session has either expired or was not established correctly.<br><br>Please use the main menu to restart the reservations session.</h2>");
                resp.append("<h3>If you are seeing this message immediately or continually, you may need to adjust your browser's cookie settings.<br>Please <a href=\"/v5/premier_cookies.htm\">click here for further information</a>.</h3>");

                if (club.equals("parkmeadowscc")) {
                    //resp.append("<h3>You can also access ForeTees using the previous method by <a href=\"http://www.parkmeadowscc.com/Members/GOLF/Online-Tee-Times-154.html\" target=\"_top\">clicking here</a>.</h3>");
                    resp.append("<h3>If you are uncomfortable changing your browser settings to always allow, <a href=\"http://www.parkmeadowscc.com/Members/GOLF/Online-Tee-Times-154.html\" target=\"_top\">please click here</a> to access ForeTees.</h3>");
                }

                resp.append("</center>");
                resp.append("</body></html>");


            } else {
                error = error_session_timeout;
                // Club isn't known
                resp.append("<HTML>");
                resp.append("<HEAD>");
                resp.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
                resp.append("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
                resp.append("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
                resp.append("<TITLE>Access Error</TITLE></HEAD>");
                resp.append("<BODY><CENTER>");
                resp.append("<H2>Access Error - Please Read</H2>");
                resp.append("<!-- CLUB: '");
                resp.append(club);
                resp.append("' CALLER: '");
                resp.append(caller);
                resp.append("' -->");
                resp.append("Sorry, your session either timed out, you didn't login, or your computer does not allow the use of Cookies.");
                resp.append("<BR><BR>This site requires the use of Cookies for security purposes.");
                resp.append("<BR>We use them to verify your session and prevent unauthorized access.");
                resp.append("<BR><BR>Please check your 'Privacy' settings, under 'Tools', 'Internet Options'");
                resp.append("<BR>(for MS Internet Explorer).  This must be set to 'Medium High' or lower.");
                resp.append("<BR><BR>If you have a firewall, please check its settings as well.");
                resp.append("<BR><BR><HR width=\"500\">");
                resp.append("<b>NOTE:</b> You must be logged in to access the ForeTees system. You cannot bookmark a page");
                resp.append("<BR>within ForeTees and then return to it later without logging in.  If you access ForeTees");
                resp.append("<BR>from your club web site, then you must login to the web site.  If you access ForeTees");
                resp.append("<BR>directly, then you must do so through the ForeTees Login page.");
                resp.append("<BR><HR width=\"500\"><BR>");
                resp.append("If you have tried all of the above and still receive this message,");
                resp.append("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
                resp.append("<BR><b>Provide your name or member number, the name of your club and a detailed description of your problem.</b>");
                resp.append("<BR>Thank you.");
                resp.append("<BR><BR>");
                resp.append("<a href=\"Logout\" target=\"_top\">Return</a>");
                resp.append("<BR><BR>");
                resp.append("<CENTER>Server: ");
                resp.append(Common_Server.SERVER_ID);
                resp.append("</CENTER>");
                resp.append("</CENTER></BODY></HTML>");


            }
            Connect.close(con);

        }
        
        verify_user.htmlResponse = resp.toString();
        verify_user.error = error;
        
        return verify_user;
    }
    
    
    
    // *********************************************************
    // verifyPro - Check for illegal access by user
    // *********************************************************
    public static VerifyUser verifyPro(HttpServletRequest req) {


        VerifyUser verify_user = new VerifyUser();

        StringBuilder htmlResponse = new StringBuilder();
        String error = null;

        HttpSession session = null;

        String proshop = LoginCredentials.proshop;
        String user = "";

        String agent = req.getHeader("User-Agent");  // Get user's browser info for error msg

        String errorMsg = "Proshop User Rejected: User Verification Error (cookies). Agent: " + agent + ", ";

        //
        // Make sure user didn't enter illegally
        //
        session = req.getSession(false);  // Get user's session object (no new one)

        if (session != null) {

            user = reqUtil.getSessionString(req, "user", "");   // get username

            if (!user.startsWith(proshop)) {

                session = null;
            }
        }

        if (session == null) {
            
            req.setAttribute(ProcessConstants.RQA_BLOCKUSER, true);

            //htmlResponse.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
            error = error_session_timeout;
            htmlResponse.append("<HTML>");
            htmlResponse.append("<HEAD>");
            htmlResponse.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
            htmlResponse.append("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
            htmlResponse.append("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
            htmlResponse.append("<TITLE>Access Error - Redirect</TITLE></HEAD>");
            htmlResponse.append("<BODY><CENTER>");
            htmlResponse.append("<BR><H2>Access Error</H2><BR>");
            htmlResponse.append("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
            htmlResponse.append("<BR>Your session has most likely timed out.");
            htmlResponse.append("<BR><BR>");
            htmlResponse.append("<BR>If you feel you have received this message in error,");
            htmlResponse.append("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
            htmlResponse.append("<BR>Provide your name and the name of your club.  Thank you.");
            htmlResponse.append("<BR><BR>");
            htmlResponse.append("<a href=\"Logout\" target=\"_top\">Return</a>");
            htmlResponse.append("</CENTER></BODY></HTML>");
            //
            //  save error message in /v2/error.txt
            //
            errorMsg = errorMsg + " User=" + user;    // build error msg - display user if present
            //logError(errorMsg);                           // log it
        }

        verify_user.session = session;
        verify_user.user = user;
        verify_user.error = error;
        verify_user.htmlResponse = htmlResponse.toString();

        return verify_user;
    }
    
    
    public static Map<String, Object> loadUser(HttpServletRequest req, String type, String clubid, String user) {

        Map<String, Object> map_result = new LinkedHashMap<String, Object>();
        Map<String, Object> error_result = new LinkedHashMap<String, Object>();
        String mship = "", mtype = "", msubtype = "", wc = "", club = "", name = "", username = "", uid = "", base64_iv = "";
        long member_id = 0;
        String remote_ip = req.getHeader("x-forwarded-for");  // get remote IP for access control
        boolean user_allowed = false;
        boolean ip_allowed = true; // default to true since we're not really using this 
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection con = null;

        // detect sso from app and change type accordingly
        if (req.getParameter("dc") != null && req.getParameter("mid") != null && req.getParameter("cid") != null) {
            type = "SSO-APP";
        }

        if (type.equalsIgnoreCase("SSO")) {

            // authentication from Premier integrated sites

            if (req.getParameter("sso_uid") == null) {
                uid = req.getParameter("uid") == null ? "" : req.getParameter("uid");                    // encrypted club/user values
                base64_iv = req.getParameter("iv") == null ? "" : req.getParameter("iv");                // initialization vector
            } else {
                uid = req.getParameter("sso_uid") == null ? "" : req.getParameter("sso_uid");                    // encrypted club/user values
                base64_iv = req.getParameter("sso_iv") == null ? "" : req.getParameter("sso_iv");                // initialization vector
            }
            Long keyTime = (System.currentTimeMillis() / 1000L) / 3600;                                     // get hours since Epoch

            List<String> uid_list = new ArrayList();

            // try to decrypt the current hour
            uid_list = decryptUID(req, clubid, user, keyTime, base64_iv, uid);

            // if it failed try forward an hour
            if (uid_list == null || uid_list.size() != 2) {

                uid_list = decryptUID(req, clubid, user, keyTime + 1, base64_iv, uid);
            }

            // if it failed again try back an hour
            if (uid_list == null || uid_list.size() != 2) {

                uid_list = decryptUID(req, clubid, user, keyTime - 1, base64_iv, uid);
            }

            // if failed after trying rolling forward and back an hour then abort
            if (uid_list == null || uid_list.size() != 2) {

                //error_result = errorMessageObj("INVALID_UID","The UID provided was not valid. unadjusted key=" + AESencrypt.encryptionKey + keyTime.toString() + ", pass_uid=" + uid + ", passed_base64_iv=" + base64_iv);
                error_result = errorMessageObj("INVALID_UID", "The UID provided was not valid.");

            } else {

                // if still here these should be available
                user = uid_list.get(0).toString();
                clubid = uid_list.get(1).toString();
            }

        } else if (type.equalsIgnoreCase("SSO-APP")) {

            // authentication from ForeTees mobile app
            try {

                con = Connect.getCon(ProcessConstants.REV);

                if (mobileAPI.authenticateDeviceKey(req.getParameter("dc"), Long.parseLong(req.getParameter("mid")), Integer.parseInt(req.getParameter("cid")), con)) {

                    // valid key
                    user = req.getParameter("mid");
                    clubid = req.getParameter("cid");

                } else {

                    // authentication failure
                    error_result = errorMessageObj("INVALID_SSO", "Device Authentication Failure.");

                }

            } catch (Exception exc) {

                error_result = errorMessageObj("INVALID_SSO", "The data provided was not valid. Error: " + exc.getMessage());

            } finally {
            }

        }


        // MAKE SURE THE CLUB SPECIFIED EXISTS AND ACCESS IS ALLOWED

        if (error_result.size() < 1) {

            if (remote_ip == null || remote_ip.equals("")) {
                remote_ip = req.getRemoteAddr();
            }

            // verify user credentials (user access & club is configured for access)

            try {

                if (Integer.valueOf(clubid) > 0) {

                    con = Connect.getCon(ProcessConstants.REV);

                    club = Utilities.getClubName(Integer.valueOf(clubid), con); // if club is inactive this will be null

                    if (club != null && !club.equals("")) {

                        Connect.close(con);

                        con = Connect.getCon(req, club);

                        // Store the connection object for later use by caller
                        map_result.put("db_connection", con);

                        /*
                        pstmt = con.prepareStatement (
                        "SELECT seamless_caller " +
                        "FROM club5 " +
                        "WHERE 1 = ? AND clubName <> '';");
                        
                        pstmt.clearParameters();
                        pstmt.setInt(1, 1);
                        rs = pstmt.executeQuery();
                        
                        if (rs.next()) {
                        
                        if (!rs.getString("seamless_caller").equalsIgnoreCase("PDG4735") && !rs.getString("seamless_caller").equalsIgnoreCase("FLEXSCAPE4865") && !rs.getString("seamless_caller").equalsIgnoreCase(ProcessConstants.FT_PREMIER_CALLER)) {
                        
                        //if (ProcessConstants.SERVER_ID != 4) result = errorMessage("BAD_CLUB_ID","The club requested is not configured for API access. " + clubid);
                        
                        }
                        }
                        
                        // only do IP verification on production servers and non-sso reqs
                        if (ProcessConstants.SERVER_ID != 4 && !type.equalsIgnoreCase("SSO")) {
                        
                        // now look up the allowable IP
                        pstmt = con.prepareStatement (
                        "SELECT id " +
                        "FROM v5.flexweb_ip_addresses " +
                        "WHERE ip = ?");
                        
                        pstmt.clearParameters();
                        pstmt.setString(1, remote_ip);
                        rs = pstmt.executeQuery();
                        
                        if (rs.next()) ip_allowed = true;
                        
                        } else {
                        
                        // sso or on dev server so allow it regardless
                        ip_allowed = true;
                        }
                         */

                    } else {

                        Connect.close(con);
                        error_result = errorMessageObj("BAD_CLUB_ID", "The club requested was not found. " + clubid);

                    }

                } else {

                    Connect.close(con);
                    //Utilities.logError("Common_webapi.loadUser() Error finding club. club_id=" + clubid + ", club=" + club + ", remote_ip=" + remote_ip);

                    error_result = errorMessageObj("UNEXPECTED_ERROR", "Error loading initial data.  Invalid.");

                }

            } catch (Exception exc) {

                Utilities.logError("Common_webapi.loadUser() Error loading initial data. club_id=" + clubid + ", club=" + club + ", remote_ip=" + remote_ip + ", err=" + exc.getMessage());

                error_result = errorMessageObj("UNEXPECTED_ERROR", "Error loading initial data. " + exc.getMessage());

            } finally {

                Connect.close(rs, pstmt);

            }

        } // end if no error yet


        // MAKE SURE USER EXISTS AND IS NOT INACTIVE OR MARKED AS NON-BILLABLE

        if (error_result.size() < 1) {

            try {

                String stmtString = ""
                        + "SELECT username, name_last, name_first, name_mi, m_ship, m_type, msub_type, email, count, wc, message, "
                        + "memNum, email2, email_bounced, email2_bounced, iCal1, iCal2, default_activity_id, mobile_count, mobile_iphone, id "
                        + "FROM member2b "
                        + "WHERE inact = 0 AND "
                        + ((type.equalsIgnoreCase("SSO-APP")) ? "id" : "flexid") + " = ?;";

                pstmt = con.prepareStatement(stmtString);

                pstmt.clearParameters();
                pstmt.setString(1, user);
                rs = pstmt.executeQuery();

                if (rs.next()) {


                    // Get the member's full name.......
                    StringBuilder mem_name = new StringBuilder(rs.getString("name_first"));   // get first name

                    String mi = rs.getString("name_mi");                                    // middle initial
                    if (!mi.equals("")) {
                        mem_name.append(" ");
                        mem_name.append(mi);
                    }
                    mem_name.append(" ");                       // last name
                    mem_name.append(rs.getString("name_last"));                       // last name

                    name = mem_name.toString();             // convert to one string

                    // Get the member's membership type
                    mship = rs.getString("m_ship");         // Get mship type
                    mtype = rs.getString("m_type");         // Get member type
                    msubtype = rs.getString("msub_type");
                    wc = rs.getString("wc");                // Get default tmode
                    username = rs.getString("username");
                    member_id = rs.getLong("id");

                    user_allowed = true;
                }

            } catch (Exception exc) {

                Utilities.logError("Common_webapi.loadUser() Error validating user. type=" + type + ", user=" + user + ", club=" + club + ", remote_ip=" + remote_ip + ", err=" + exc.getMessage());

                error_result = errorMessageObj("UNEXPECTED_ERROR", "Error validating user. " + exc.getMessage());

            } finally {

                Connect.close(rs, pstmt);

            }


            if (!user_allowed) {

                error_result = errorMessageObj("MEMBER_NOT_FOUND", "No active member record found for requested user. user=" + user + ", club=" + club);

            }

        } // end if no error yet


        map_result.put("mship", mship);
        map_result.put("mtype", mtype);
        map_result.put("msubtype", msubtype);
        map_result.put("wc", wc);
        map_result.put("club", club);
        map_result.put("clubid", clubid);
        map_result.put("name", name);
        map_result.put("user", user);
        map_result.put("username", username);
        map_result.put("member_id", member_id);
        map_result.put("remote_ip", remote_ip);
        map_result.put("user_allowed", user_allowed);
        map_result.put("ip_allowed", ip_allowed);
        map_result.put("is_error", (error_result.size() > 0));

        if (error_result.size() > 0) {
            map_result.put("error", error_result);
        }

        Connect.close(rs, pstmt);
        return map_result;

    }

    
    // Moved from Common_webapi
    public static Map<String,Object> performSSO(HttpServletRequest req, HttpServletResponse resp, int activity_id, final String user, final String username, final long member_id, final String club, String caller, final String mship, final String mtype, final String msubtype, final String name, final String wc, final String remote_ip, final long benchmark_start, Connection con)
            throws ServletException, IOException {

        /*
         * NOTE:  resp object could be NULL!!
         * 
         * Once here we already know the caller, club and user are all valid and active
         * Test for an existing session and if not found, create one
        */

        int result = 0;
        String resp_message = "";
        String uuid = null;
        
        

        if (req.getParameter("logout") != null) {

            // we're here to detroy the users session

            HttpSession session = req.getSession(false);

            if (session == null) {

                // there was no session to release
                result = 0;
                resp_message = "No existing session found for user " + user;

            } else {

                // clear the users session variables
                session.removeAttribute("user");
                session.removeAttribute("member_id");
                session.removeAttribute("club");
                session.removeAttribute("connect");

                // end the users session
                session.invalidate();

                result = 1;
                resp_message = "The existing session was found and detroyed for user " + user;
            }

        } else {


            PreparedStatement pstmt = null;
            ResultSet rs = null;

            int organization_id = Utilities.getOrganizationId(con);
            int mobile = (req.getParameter("mobile") != null) ? 1 : 0;

            String zipcode = "";

            //
            //  Get club's POS Type for _slot processing
            //
            String posType = ApiCommon.getClubPOS(con);

            //
            //  Get TLT indicator
            //
            int tlt = (Utilities.isNotificiationClub(con)) ? 1 : 0;

            try {

                pstmt = con.prepareStatement (
                  "SELECT zipcode " +
                  "FROM club5");

                pstmt.clearParameters();
                rs = pstmt.executeQuery();

                if (rs.next()) {

                    zipcode = rs.getString("zipcode");
                }

            } catch (Exception exc) {

                //invalidRemote(new_skin, default_activity_id, "Unable to Connect to Club Database for options. Error: " + exc.getMessage(), req, out, con);
                //return;
            } finally {
                
                Connect.close(rs, pstmt);
                
            }


            HttpSession session = req.getSession(false); // do not create a new session if one doesn't already exist
            
            boolean createSession = true;

            if (session != null) {

                // user has an existing session
                String session_user = (String)session.getAttribute("user");       // get user
                String session_club = (String)session.getAttribute("club");       // get club name

                // simple sanity validation
                if (!session_user.equals(username) || !session_club.equals(club)) {

                    // clear the users session variables
                    session.removeAttribute("user");
                    session.removeAttribute("member_id");
                    session.removeAttribute("club");
                    session.removeAttribute("connect");

                    // The current session is not for the requested user.
                    // end the current session (the session will be recreated with different user below)
                    session.invalidate();
                    

                    //result = -1;
                    //resp_message = "A session mismatch has occured. Details have been logged.";

                    //Utilities.logError("*** API SSO BUG - username=" + username + ", session_user=" + session_user + ", club=" + club + ", session_club=" + session_club + ", ");
                    
                } else {

                    result = 1;
                    resp_message = "An existing session was found and touched for user " + user;
                    createSession = false;
                    
                }

            } // end if session was found
            
            
   
            if (createSession){

                //
                //   Check to see if this is probably a mobile device - if so, log that too
                //
                boolean isMobileClient = uaUtil.isMobile(req);
                
                //
                //   Check to see if this is a user on the mobile app
                //
              //boolean isMobileAppUser = Utilities.getBitFromRequest(req, ProcessConstants.RQA_APPMODE, ProcessConstants.APPMODE_MOBILE_APP1);
                boolean isMobileAppUser = Utilities.getBit(reqUtil.getRequestInteger(req, ProcessConstants.RQA_APPMODE, 0), ProcessConstants.APPMODE_MOBILE_APP1);
                
                //
                //  Trace good logins - display parms passed for verification purposes
                //
                Connect.sessionLog("SSO Login Successful: user_name from website=" +user+ ", Primary=No, mNum=No, IP=" + remote_ip + ", mobile=" + isMobileClient + ", mobileApp= " + isMobileAppUser + " ", username, "", club, caller, con);            // log it

                StatCounters.recordLogin(username, "", club, remote_ip, 1);
                
                //
                //  Get the hour of day (24 hr clock)
                //
                Calendar cal = new GregorianCalendar();   

                StatCounters.loginCountsMem[cal.get(Calendar.HOUR_OF_DAY)]++;       // count the logins for this hour today

                if (isMobileAppUser) {
                    
                    StatCounters.countMobileApp(username, club, req, con);      // bump mobile app counter and track mobile device 
                    
                } else if (req.getParameter("dc") != null && req.getParameter("mid") != null && req.getParameter("cid") != null) {
                    String uri = req.getRequestURI();
                    if (req.getQueryString() != null) uri += "?" + req.getQueryString();
                    //Utilities.logError("isMobileAppUser is still false - uri=" + uri);
                    StatCounters.countMobileApp(username, club, req, con);
                    caller = "FTMOBILEAPP";
                    
                }

                StatCounters.countLogins(username, club, req, con);          // bump login counter for this member
                
                
                //
                // No valid existing session - create one now
                //
                session = req.getSession(true);

                session.setAttribute("sess_id", LoginCredentials.id);  // set session id for validation ("foretees")
                session.setAttribute("user", username);       // save username
                session.setAttribute("name", name);           // save members full name
                session.setAttribute("member_id", member_id);
                session.setAttribute("club", club);           // save club name
                session.setAttribute("caller", caller);       // save caller's name
                session.setAttribute("mship", mship);         // save member's mship type
                session.setAttribute("mtype", mtype);         // save member's mtype
                session.setAttribute("msubtype", msubtype);         // save member's mtype
                session.setAttribute("wc", wc);               // save member's w/c pref (for _slot)
                session.setAttribute("posType", posType);     // save club's POS Type
                session.setAttribute("zipcode", zipcode);     // save club's zipcode
                session.setAttribute("tlt", tlt);             // timeless tees indicator
                //session.setAttribute("mobile", mobile);       // set mobile indicator (0 = NOT, 1 = Mobile)
                session.setAttribute("activity_id", activity_id);  // activity indicator
                session.setAttribute("organization_id", organization_id);  // organization_id (set if using ForeTeesDining system)
                //session.setAttribute("new_skin", "1");        // new skin flag
                //session.setAttribute("premier_referrer", req.getHeader("referer"));       // referer

                //
                // set inactivity timer for this session
                //
                session.setMaxInactiveInterval( MEMBER_TIMEOUT * 2 ); // double the length for Premier users

                result = 2;
                resp_message = "A new session was created for user " + user;
            }
            
            session.setAttribute("premier_referrer", req.getHeader("referer"));
            session.setAttribute("caller", caller);  
            session.setAttribute("mobile", mobile); 
            session.setAttribute("new_skin", "1");        // new skin flag
            //session.setAttribute("activity_id", activity_id); 
            
            uuid = (String) session.getAttribute("session_uuid");
            if(uuid == null){
                uuid = UUID.randomUUID().toString();
                session.setAttribute("session_uuid", uuid);
            }
            

            //
            //  Count the number of users logged in
            //
            //countLogin("mem", con);

            // new stats logging routine
            //recordLoginStat(2);

        }

        //Gson gson_obj = new Gson();

        Map<String, Object> container_map = new HashMap<String, Object>();
        Map<String, Object> response_map = new HashMap<String, Object>();
        
        /*if (req.getParameter("callback") != null) {

            container_map.put(req.getParameter("callback"), response_map);

            response_map.put("valid", result);
            response_map.put("message", resp_message);
            response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");
            response_map.put("serverId", ProcessConstants.SERVER_ID);

        } else {*/

            container_map.put("foreTeesSSOResp", response_map);

            response_map.put("valid", result);
            response_map.put("message", resp_message);
            response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");
            response_map.put("serverId", ProcessConstants.SERVER_ID);
            response_map.put("sessionUuid", uuid);
            
        //}
        
        //return gson_obj.toJson(container_map);
           return container_map;

    }
   
    public static List<String> decryptUID(Long keyTime, String base64_iv, String uid) {

        return decryptUID(null, null, null, keyTime, base64_iv, uid);
    }

    public static List<String> decryptUID(HttpServletRequest req, String clubid, String user, Long keyTime, String base64_iv, String uid) {


        try {

            String key = AESencrypt.encryptionKey + keyTime.toString();

            String clear = "", decrypt_err = "", type_err = "";

            //BASE64Decoder base64decoder = new BASE64Decoder();

            byte[] iv = Base64.decodeBase64(base64_iv);
            //String hex_iv = asHex(iv);

            byte[] credentials = Base64.decodeBase64(uid);

            //String hex = asHex(credentials);

            try {

                clear = AESencrypt.decrypt(credentials, key, iv);

            } catch (Exception exc) {

                String remote_ip = "";
                if (req != null) {
                    remote_ip = req.getHeader("x-forwarded-for");
                    if (remote_ip == null || remote_ip.equals("")) {
                        remote_ip = req.getRemoteAddr();
                    }
                }
                Utilities.logDebug("PTS", "Common_webapi.decryptUID error1. err=" + exc.toString() + ", remote_ip=" + remote_ip + ", clubid=" + clubid + ", user=" + user + ", key=" + key + ", uid=" + uid + ", base64_iv=" + base64_iv);
            }

            Gson gson = new Gson();
            List<String> uid_list = new ArrayList();

            try {

                Type data_type = new TypeToken<List<String>>() {
                }.getType();
                uid_list = gson.fromJson(clear, data_type);

            } catch (Exception exc) {

                Utilities.logError("Common_webapi.decryptUID error2. err=" + exc.toString() + ", clear=" + clear);

            }


            if (uid_list == null || uid_list.size() != 2) {

                return null;

            } else {

                return uid_list;
            }

        } catch (Exception exc) {

            Utilities.logError("Common_webapi.decryptUID unexpected error! err=" + exc.toString());

        }

        return null;

    }
    
    public static Map<String, Object> errorMessageObj(String errorCode, String errorMessage) {

        Map<String, Object> response_map = new HashMap<String, Object>();

        response_map.put("errorCode", errorCode);
        response_map.put("errorMessage", errorMessage);
        response_map.put("errorTime", System.currentTimeMillis());  // the difference, measured in milliseconds, between the current time and midnight, January 1, 1970 UTC.
        response_map.put("serverId", ProcessConstants.SERVER_ID);

        return response_map;

    }


    
}
