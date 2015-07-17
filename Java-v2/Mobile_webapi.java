/***************************************************************************************
 * 
 * Exposes an API for our mobile app
 * 
 * 
 * 
 * Terms:
 * 
 *      Authorization:  The one time process of authorizing a device for use with ForeTees
 *                      this process returns a set of credentials for the device to use
 * 
 *      Authentication: The process of validating a set of credentials passed to us by
 *                      a remote device
 * 
 * 
 * .
 ***************************************************************************************/


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.Charset;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.*;
import com.google.gson.reflect.*;

import com.foretees.common.ProcessConstants;
import com.foretees.common.Connect;
import com.foretees.common.Utilities;
import com.foretees.common.memberUtil;
import com.foretees.common.BasicSHA256;
import com.foretees.common.mobileAPI;
import com.foretees.common.reqUtil;
import com.foretees.common.timeUtil;

import java.security.SecureRandom;
import java.util.List;


public class Mobile_webapi extends HttpServlet {
 

 /** 
  * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
  * @param req servlet request
  * @param resp servlet response
  * @throws ServletException if a servlet-specific error occurs
  * @throws IOException if an I/O error occurs
  */
 protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
 throws ServletException, IOException {


    final long benchmark_start = System.currentTimeMillis();
     
    String result = "";
    String type = req.getParameter("type") == null ? "" : req.getParameter("type");                 // action
    
    //
    // All requests that are allowed without being authenticated go first
    //
    if (type.equals("authorizeDevice") && req.getMethod().equals("POST")) {
        
        if (req.getParameter("step") != null) {
            result = encodeMap(requestDeviceAuthorization(req, benchmark_start));
        } else if (req.getParameter("c2") != null) {
            result = encodeMap(requestDeviceAuthorizationOld(req, benchmark_start));
        }
    
    } else if (type.equals("authenticateDevice") && req.getMethod().equals("GET")) {
        
        result = encodeMap(authenticateDevice(req, benchmark_start));
        
    }
    
    // if we haven't done anything yet and our device is authenticated then continue
    if ( result.isEmpty() && mobileAPI.authenticateDevice(req) ) {
        
        long member_id = (req.getParameter("mid") == null) ? 0 : Integer.parseInt(req.getParameter("mid"));
        int club_id = (req.getParameter("cid") == null) ? 0 : Integer.parseInt(req.getParameter("cid"));
        String club = Utilities.getClubName(club_id, null);
        Connection con = Connect.getCon(req, club);
        
        if (type.equals("userData") && req.getMethod().equals("GET")) {

            result = encodeMap(mobileAPI.getUserData(member_id, club_id, con));

        } else if (type.equals("activityList") && req.getMethod().equals("GET")) {
            
            result = encodeMap(getActivityList(req, club, con, benchmark_start));
            
        } else if (type.equals("eventList") && req.getMethod().equals("GET")) {
            
            result = encodeMap(getEventList(req, club, con, benchmark_start));
            
        } else if (type.equalsIgnoreCase("premierJump") && req.getMethod().equals("GET")) {
            
            result = getRedirectURL(req, club, member_id, con);
            
        }
        
        // close our connection
        try { con.close(); }
        catch (Exception ignore) { }
        
    } else if ( result.isEmpty() && mobileAPI.authenticateCaller(req) ) {
        
        // here we can add calls that come from the admin side of the mobile app (not a device)
        int club_id = (req.getParameter("cid") == null) ? 0 : Integer.parseInt(req.getParameter("cid"));
        int activity_id = (req.getParameter("actid") == null) ? 0 : Integer.parseInt(req.getParameter("actid"));
        int start_date = (req.getParameter("sdate") == null) ? 0 : Integer.parseInt(req.getParameter("sdate"));
        String club = Utilities.getClubName(club_id, null);
        Connection con = Connect.getCon(req, club);
        
        if (type.equals("activityList") && req.getMethod().equals("GET")) {
            
            result = encodeMap(mobileAPI.getActivityList(club, con));
            
        } else if (type.equals("eventList") && req.getMethod().equals("GET")) {
            
            result = encodeList(mobileAPI.getEventList(activity_id, start_date, 0, club, con));
            
        } else if (type.equals("destinationList") && req.getMethod().equals("GET")) {
            
            result = encodeMap(mobileAPI.getDestinationList(activity_id, club, con));
            
        } else if (type.equals("clubName") && req.getMethod().equals("GET")) {
            
            result = encodeMap(mobileAPI.getClubName(activity_id, club_id, club, con));
            
        } else if (type.equals("baseUrl") && req.getMethod().equals("GET")) {
            
            result = encodeMap(mobileAPI.getBaseUrl(activity_id, club, Utilities.setBit(0, ProcessConstants.APPMODE_MOBILE_APP1, true)));
            
        }
        
        // close our connection
        try { con.close(); }
        catch (Exception ignore) { }
        
    }
    
    if (type.equals("premierJump")) {
        
        // send redirect to client
        resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        resp.setHeader("Location", result);
        
    } else {
        
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json;charset=UTF-8"); // http://www.ietf.org/rfc/rfc4627.txt
        out.print(result);
        out.close();
    
    }
    
 }
 
 
 /**
  * This method returns a URL to send as a redirect back a mobile device to get the user into their Premier site
  * @param req request object
  * @param club club name
  * @param member_id member id
  * @param con database connection object
  * @return Map A map containing the response
  */
 private String getRedirectURL(HttpServletRequest req, String club, long member_id, Connection con) {
     
    
    // https://clubwebsitedomainhere/fw/api/fw_profile_logon.asp?a=[Member#]&l=[Encoded Landing Page URL]
    // http://foretees2.flexdemo.com/fw/api/fw_profile_logon.asp?a=5572&l=http://foretees2.flexdemo.com/members-only/members-home/club-calendar-15.html
    // http://foretees2.flexdemo.com/fw/api/fw_profile_logon.asp?userid=5572&l=http://foretees2.flexdemo.com/members-only/members-home/club-calendar-15.html
    // http://dev.foretees.com/v5/servlet/Mobile_webapi?type=premierJump&l=http://foretees2.flexdemo.com/members-only/members-home/club-calendar-15.html
     
     //http://dev.foretees.com/v5/servlet/Mobile_webapi?landing=/Members/Updates/Current-News-175.html&type=premierJump&dc=foretees&mid=594&cid=1


    String result = "", premierSite = "";
    int flexid = 0;
    String premierPage = req.getParameter("landing"); 
     
    ResultSet rs = null;
    PreparedStatement pstmt = null;

    // get the website url
    
    try {

        // lookup the member's flexid
        pstmt = con.prepareStatement("SELECT c.website_url, m.flexid FROM club5 c, member2b m WHERE m.id = ? AND m.inact = 0");
        pstmt.clearParameters();
        pstmt.setLong(1, member_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            
            premierSite = rs.getString("website_url");
            flexid = rs.getInt("flexid");
            
            if (premierSite.isEmpty()) throw new Exception("Missing website URL in club setup.");
            
            // handle protocol and trailing slash!
            result = "http://" + premierSite + "/fw/api/fw_profile_logon.asp?userid=" + flexid + "&cachebuster=" + timeUtil.getCurrentUnixTime() + "&l=" + URLEncoder.encode(premierPage,"UTF-8");
        
        }
              

    } catch (Exception exc) {
        
            Utilities.logError("Mobile_webapi.getRedirectURL - club=" + club + ", member_id=" + member_id + ", err=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
     
    //debug
    Utilities.logDebug("PTS", "Mobile_webapi.getRedirectURL - result=" + result);
    
    return result;
    
 }
 
 
 /**
  * This method processes a request for a list of events
  * @param req request object
  * @param club club name
  * @param con database connection object
  * @param benchmark_start timestamp to track processing time
  * @return Map A map containing the response
  */
 private Map<String, Object> getEventList(HttpServletRequest req, String club, Connection con, long benchmark_start) {
    

    int activity_id = (req.getParameter("actid") == null) ? 0 : Integer.parseInt(req.getParameter("actid"));
    int start_date = (req.getParameter("sdate") == null) ? 0 : Integer.parseInt(req.getParameter("sdate"));
        
    Map<String, Object> container_map = new HashMap<String, Object>();
    Map<String, Object> response_map = new HashMap<String, Object>();

    container_map.put("foreTeesMobileApiResp", response_map);
    
    response_map.put("action", req.getParameter("type"));
    response_map.put("eventList", mobileAPI.getEventList(activity_id, start_date, 0, club, con));
    response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");
    response_map.put("serverId", ProcessConstants.SERVER_ID);
    
    return container_map;

 }
 
 
 /**
  * This method processes a request for a list of all configured activities
  * @param req request object
  * @param club club name
  * @param con database connection object
  * @param benchmark_start timestamp to track processing time
  * @return Map A map containing the response
  */
 private Map<String, Object> getActivityList(HttpServletRequest req, String club, Connection con, long benchmark_start) {
    

    Map<String, Object> container_map = new HashMap<String, Object>();
    Map<String, Object> response_map = new HashMap<String, Object>();

    container_map.put("foreTeesMobileApiResp", response_map);
    
    response_map.put("action", req.getParameter("type"));
    response_map.put("activityList", mobileAPI.getActivityList(club, con));
    response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");
    response_map.put("serverId", ProcessConstants.SERVER_ID);
    
    return container_map;

 }
 
 
 /**
  * This method processes a request from a remote device and determines if the credentials are valid
  * @param req request object
  * @param benchmark_start timestamp to track processing time
  * @return Map A map containing the response
  */
 private Map<String, Object> authenticateDevice(HttpServletRequest req, long benchmark_start) {
    

    Map<String, Object> container_map = new HashMap<String, Object>();
    Map<String, Object> response_map = new HashMap<String, Object>();

    container_map.put("foreTeesMobileApiResp", response_map);
    
    response_map.put("action", req.getParameter("type"));
    
    Connection con = Connect.getCon(ProcessConstants.REV);

    String deviceCode = (req.getParameter("dc") == null) ? "" : req.getParameter("dc");
    long member_id = (req.getParameter("mid") == null) ? 0 : Integer.parseInt(req.getParameter("mid"));
    int club_id = (req.getParameter("cid") == null) ? 0 : Integer.parseInt(req.getParameter("cid"));
    
    // only attempt to authenticate if required parameters are here
    if ( mobileAPI.authenticateDeviceKey(deviceCode, member_id, club_id, con) ) {
        
        // SUCCESS
        response_map.put("success", "1");
        
    } else {
        
        // FAIL
        response_map.put("success", "0");
        
    }
    
    response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");
    response_map.put("serverId", ProcessConstants.SERVER_ID);
    
    // close our connection
    try { con.close(); }
    catch (Exception ignore) { }
    
    return container_map;

 }
 
 
 /**
  * This method handles the one-time device authorization and returns the authentication data
  * needed by the remote device so it can store it locally and use it when accessing ForeTees
  * @param req request object
  * @param benchmark_start timestamp to track processing time
  * @return Map A map containing the response
  */
 private Map<String, Object> requestDeviceAuthorization(HttpServletRequest req, long benchmark_start) {
    

    Map<String, Object> container_map = new HashMap<String, Object>();
    Map<String, Object> response_map = new HashMap<String, Object>();

    container_map.put("foreTeesMobileApiResp", response_map);
    
    response_map.put("action", req.getParameter("type"));
    
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    Connection con = Connect.getCon(ProcessConstants.REV);

    int step = reqUtil.getParameterInteger(req, "step", 0);
    int member_id = reqUtil.getParameterInteger(req, "mid", 0);
    int club_id = reqUtil.getParameterInteger(req, "cid", 0);
    String deviceCode = (req.getParameter("dc") == null) ? "" : req.getParameter("dc");
    String c1 = (req.getParameter("c1") == null) ? "" : req.getParameter("c1").trim();
    String c2 = (req.getParameter("c2") == null) ? "" : req.getParameter("c2").trim().replace("-","").toUpperCase();
    String deviceAllias = (req.getParameter("da") == null) ? "" : req.getParameter("da").trim();
    String deviceUuid = (req.getParameter("du") == null) ? "" : req.getParameter("du").trim();
    String permDeviceAuthSalt = BasicSHA256.getSalt(16);
    String permDeviceAuthCode = null;
    boolean step2 = false;
    
    
    //Utilities.logError("Incoming requestDeviceAuthorization: c1=" + c1 + ", c2=" + c2 + ", da=" + deviceAllias + ", du=" + deviceUuid);
                
    // only process if required bits are here
    if (!c1.isEmpty() && !c2.isEmpty() && !deviceAllias.isEmpty() && step == 1) {

        club_id = 0;
        member_id = 0;
    
        try {

            // find an unused auth_lookup_code that's less than an hour old
            pstmt = con.prepareStatement("SELECT * FROM v5.mobile_auth WHERE auth_username = ? AND auth_password = ? AND active = 0"); // AND TIMESTAMPDIFF(HOUR, date_created, NOW()) < 1");
            pstmt.clearParameters();
            pstmt.setString(1, c1);
            pstmt.setString(2, c2);
            rs = pstmt.executeQuery();
            
            // if we found one then continue
            if ( rs.next() ) {

                // build a uid for this device to store and send back to us for authentication
                String decodedString = permDeviceAuthSalt + "|" + deviceAllias + "|" + rs.getString("member_id") + "|" + rs.getString("club_id") + "|" + deviceUuid;
                permDeviceAuthCode = BasicSHA256.SHA256(decodedString);

                // add the permenant record (clear the auth_lookup_code, set active to 1, add new values
              //pstmt = con.prepareStatement("UPDATE v5.mobile_auth SET device_name = ?, device_uid = ?, device_auth = ?, device_auth_salt = ?, auth_username = NULL, auth_password = NULL, date_created = now(), active = 1 WHERE id = ?");
                pstmt = con.prepareStatement("UPDATE v5.mobile_auth SET device_name = ?, device_uid = ?, device_auth = ?, device_auth_salt = ?, date_created = now() WHERE id = ?");
                pstmt.clearParameters();
                pstmt.setString(1, deviceAllias);
                pstmt.setString(2, deviceUuid);
                pstmt.setString(3, permDeviceAuthCode);
                pstmt.setString(4, permDeviceAuthSalt);
                pstmt.setInt(5, rs.getInt("id"));
                pstmt.executeUpdate();
                
                club_id = rs.getInt("club_id");
                member_id = rs.getInt("member_id");
                
            } else {
        
                response_map.put("error", "Invalid username or password.");
                Utilities.logDebug("PTS", "Mobile_webapi.requestDeviceAuthorization: FAIL c1=" + c1 + ", c2=" + c2 + ", da=" + deviceAllias);

            }

        } catch (Exception exc) {
            
            response_map.put("error", exc.toString());
            Utilities.logError("Mobile_webapi.requestDeviceAuthorization: FATAL c1=" + c1 + ", c2=" + c2 + ", da=" + deviceAllias + ", permDeviceAuthCode=" + permDeviceAuthCode);
        
        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }
        
    } else if (!deviceCode.isEmpty() && member_id > 0 && club_id > 0 && step == 2) {

        try {
            
            // find an partially authenticated device
            pstmt = con.prepareStatement("SELECT * FROM v5.mobile_auth WHERE member_id = ? AND club_id = ? AND device_auth = ? AND active = 0"); // AND TIMESTAMPDIFF(HOUR, date_created, NOW()) < 1");
            pstmt.clearParameters();
            pstmt.setInt(1, member_id);
            pstmt.setInt(2, club_id);
            pstmt.setString(3, deviceCode);
            rs = pstmt.executeQuery();
            
            // if we found one then continue
            if ( rs.next() ) {
                
                pstmt = con.prepareStatement("UPDATE v5.mobile_auth SET auth_username = NULL, auth_password = NULL, active = 1 WHERE id = ?");
                pstmt.clearParameters();
                pstmt.setInt(1, rs.getInt("id"));
                pstmt.executeUpdate();
                
                step2 = true;
                
            } else {
        
                response_map.put("error", "Unknown device key.");
                Utilities.logError("Mobile_webapi.requestDeviceAuthorization: FAIL2 c1=" + c1 + ", c2=" + c2 + ", da=" + deviceAllias);

            }
            
        } catch (Exception exc) {
            
            response_map.put("error", "Mobile_webapi.requestDeviceAuthorization: Step2 - " + exc.toString());
        
        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }
        
    } else {
        
       response_map.put("error", "Missing required data.");
       
    }
    
    if (
        (step == 1 && (permDeviceAuthCode == null || permDeviceAuthCode.equals("") || club_id == 0 || member_id == 0)) || 
        (step == 2 && step2 == false)
       ){
        
        // fail
        response_map.put("success", "0");
        
    } else if (step == 1) {
        
        // success
        String club = Utilities.getClubName(club_id, null);
        con = Connect.getCon(club);
        
        response_map.put("success", "1");
        response_map.put("deviceAuthKey", permDeviceAuthCode);
        response_map.put("memberId", member_id);
        response_map.put("clubId", club_id);
        response_map.put("clubName", club);
        
        // get the user data and add it to the map
        Map<String, Object> user_data_map = new HashMap<String, Object>();
        user_data_map = mobileAPI.getUserData(member_id, club_id, con);
        response_map.put("userData", user_data_map);
        
    } else if (step == 2) {
        
        response_map.put("success", "2");
        
    }
    
    // close our connection
    try { con.close(); }
    catch (Exception ignore) { }
    
    response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");
    response_map.put("serverId", ProcessConstants.SERVER_ID);
    response_map.put("action", req.getParameter("type"));
    response_map.put("step", step);
    
    return container_map;

 }
 
 
 /**
  * This method handles the one-time device authorization and returns the authentication data
  * needed by the remote device so it can store it locally and use it when accessing ForeTees
  * @param req request object
  * @param benchmark_start timestamp to track processing time
  * @return Map A map containing the response
  */
 private Map<String, Object> requestDeviceAuthorizationOld(HttpServletRequest req, long benchmark_start) {
    

    Map<String, Object> container_map = new HashMap<String, Object>();
    Map<String, Object> response_map = new HashMap<String, Object>();

    container_map.put("foreTeesMobileApiResp", response_map);
    
    response_map.put("action", req.getParameter("type"));
    
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    Connection con = Connect.getCon(ProcessConstants.REV);

    String c1 = (req.getParameter("c1") == null) ? "" : req.getParameter("c1").trim();
    String c2 = (req.getParameter("c2") == null) ? "" : req.getParameter("c2").trim().replace("-","").toUpperCase();
    String deviceAllias = (req.getParameter("da") == null) ? "" : req.getParameter("da").trim();
    String deviceUuid = (req.getParameter("du") == null) ? "" : req.getParameter("du").trim();
    String permDeviceAuthSalt = BasicSHA256.getSalt(16);
    String permDeviceAuthCode = null;
    
    int club_id = 0;
    int member_id = 0;
    
    //Utilities.logError("Incoming requestDeviceAuthorization: c1=" + c1 + ", c2=" + c2 + ", da=" + deviceAllias + ", du=" + deviceUuid);
                
    // only process if required bits are here
    if (!c1.isEmpty() && !c2.isEmpty() && !deviceAllias.isEmpty()) {
    
        try {

            // find an unused auth_lookup_code that's less than an hour old
            pstmt = con.prepareStatement("SELECT * FROM v5.mobile_auth WHERE auth_username = ? AND auth_password = ? AND active = 0"); // AND TIMESTAMPDIFF(HOUR, date_created, NOW()) < 1");
            pstmt.clearParameters();
            pstmt.setString(1, c1);
            pstmt.setString(2, c2);
            rs = pstmt.executeQuery();
            
            // if we found one then continue
            if ( rs.next() ) {

                // build a uid for this device to store and send back to us for authentication
                String decodedString = permDeviceAuthSalt + "|" + deviceAllias + "|" + rs.getString("member_id") + "|" + rs.getString("club_id") + "|" + deviceUuid;
                permDeviceAuthCode = BasicSHA256.SHA256(decodedString);

                // add the permenant record (clear the auth_lookup_code, set active to 1, add new values
                pstmt = con.prepareStatement("UPDATE v5.mobile_auth SET device_name = ?, device_uid = ?, device_auth = ?, device_auth_salt = ?, auth_username = NULL, auth_password = NULL, date_created = now(), active = 1 WHERE id = ?");
                pstmt.clearParameters();
                pstmt.setString(1, deviceAllias);
                pstmt.setString(2, deviceUuid);
                pstmt.setString(3, permDeviceAuthCode);
                pstmt.setString(4, permDeviceAuthSalt);
                pstmt.setInt(5, rs.getInt("id"));
                pstmt.executeUpdate();
                
                club_id = rs.getInt("club_id");
                member_id = rs.getInt("member_id");
                
            } else {
        
                response_map.put("error", "Invalid username or password.");
                Utilities.logError("Mobile_webapi.requestDeviceAuthorizationOld: FAIL c1=" + c1 + ", c2=" + c2 + ", da=" + deviceAllias);

            }

        } catch (Exception exc) {
            
            response_map.put("error", exc.toString());
        
        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }
        
    } else {
        
       response_map.put("error", "Missing required data.");
       
    }
    
    if (permDeviceAuthCode == null || permDeviceAuthCode.equals("") || club_id == 0 || member_id == 0) {
        
        // fail
        response_map.put("success", "0");
        
    } else {
        
        // success
        String club = Utilities.getClubName(club_id, null);
        con = Connect.getCon(club);
        
        response_map.put("success", "1");
        response_map.put("deviceAuthKey", permDeviceAuthCode);
        response_map.put("memberId", member_id);
        response_map.put("clubId", club_id);
        response_map.put("clubName", club);
        
        // get the user data and add it to the map
        Map<String, Object> user_data_map = new HashMap<String, Object>();
        user_data_map = mobileAPI.getUserData(member_id, club_id, con);
        response_map.put("userData", user_data_map);
        
    }
    
    // close our connection
    try { con.close(); }
    catch (Exception ignore) { }
    
    response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");
    response_map.put("serverId", ProcessConstants.SERVER_ID);
    response_map.put("action", req.getParameter("type"));
    
    return container_map;

 }
 
 
 private static String encodeMap2(Map<String, Object> map){

     
    Gson gson_obj = new Gson();
    return gson_obj.toJson(map);

 }
 
 
 private static String encodeMap(Map<String, Object> map){

     
    GsonBuilder builder = new GsonBuilder();
    builder.disableHtmlEscaping();
    Gson gson_obj = builder.create();
    return gson_obj.toJson(map);

 }
 
 
 private static String encodeList(List<Map<String,Object>> list){

     
    Gson gson_obj = new Gson();
    return gson_obj.toJson(list);
        
 }
 
 /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
}