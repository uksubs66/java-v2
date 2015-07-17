/***************************************************************************************
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 ***************************************************************************************/


package com.foretees.common;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.nio.charset.Charset;

import javax.servlet.*;
import javax.servlet.http.*;

import com.google.gson.*;
import com.google.gson.reflect.*; // for json

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class mobileAPI {



public static Map<String, Object> getClubName(int activity_id, int club_id, String club, Connection con) {
    
    Map<String, Object> response_map = new LinkedHashMap<String, Object>();
    
    response_map.put("clubId", club_id);
    response_map.put("clubName", club);
    
    return response_map;
    
}


public static Map<String, Object> getDestinationList(int activity_id, String club, Connection con) {
    
    
    Map<String, Object> response_map = new LinkedHashMap<String, Object>();
    
    // Common pages regardless of activity
    response_map.put("Announcement Page", "Member_announce");
    response_map.put("Settings", "Member_services");
    response_map.put("Send Email", "Member_email");
    response_map.put("My Activites / Calendar", "Member_teelist");
    response_map.put("My Activites / List", "Member_teelist_list");
    
    if (activity_id == 0) {
        
        // Golf specific pages
        response_map.put("Tee Sheets", "Member_select");
        response_map.put("Today's Tee Sheet", "Member_sheet?index=0");
        response_map.put("View Handicaps and Scores", "Member_handicaps?todo=view");
        response_map.put("Post A Score", "Member_handicaps?todo=post");
        response_map.put("Partners", "Member_partner");
        if (Utilities.isGuestTrackingConfigured(activity_id, con)) {
            response_map.put("Manage Guests", "Common_guestdb");
        }
        
    } else if (activity_id == ProcessConstants.DINING_ACTIVITY_ID) {
        
        // Dining specific pages
        response_map.put("Make a Dining Reservation", "Dining_slot?action=new");
        response_map.put("Dining Events", "Dining_home?view_events");
        
    } else {
        
        // FlxRez specific pages
        response_map.put("Time Sheets", "Member_gensheets");
        
    }
    
    if (activity_id != ProcessConstants.DINING_ACTIVITY_ID) {
        
        // Pages for both Golf and FlxRez but not Dining
        response_map.put("Events", "Member_events");
        response_map.put("Lessons", "Member_lesson");
        response_map.put("Group Lessons", "Member_lesson?group=yes");
        response_map.put("Lesson Pro Bio's", "Member_lesson?bio=yes");
    }
    
    return response_map;
     
 }


public static Map<String, Object> getActivityList(String club, Connection con) {
    
    
    Map<String, Object> response_map = new LinkedHashMap<String, Object>();
    
    response_map = getActivity.getAllActivitities(club, true, con);
    
    return response_map;
     
 }
 
 

 private static List<Map<String,Object>> getEventListForDining(int organization_id, int start_date, int end_date, String club) {
     
    
    List<Map<String,Object>> container_list = new ArrayList<Map<String,Object>>();
    
    Gson gson_obj = new Gson();
    ResultSet rs = null;
    PreparedStatement pstmt = null;

    Connection con_d = null;

    try {

        con_d = Connect.getDiningCon();

        if (con_d != null) {

            // get all dining event reservations
            pstmt = con_d.prepareStatement(""
                    + "SELECT name, date, id "
                    + "FROM events "
                    + "WHERE organization_id = ? AND "
                    + "to_char(date, 'YYYYMMDD')::int >= ? AND "
                  //+ "to_char(date, 'YYYYMMDD')::int <= ? AND "
                    + "cancelled = false");

            pstmt.setInt(1, organization_id);
            pstmt.setLong(2, start_date);
          //pstmt.setLong(3, end_date);

            rs = pstmt.executeQuery();

            while (rs.next()) {


                Map<String, Object> response_map = new LinkedHashMap<String, Object>();

                response_map.put("eventName", rs.getString("name"));
                response_map.put("eventId", rs.getInt("id"));
                response_map.put("eventDate", rs.getDate("date"));
                //response_map.put("eventDate", Utilities.getDateFromYYYYMMDD(rs.getInt("date2"), 2));

                container_list.add(response_map);
                
            }
            
        }

    } catch (Exception exc) {
        
            Utilities.logError("mobileAPI.getEventListForDining() err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

        try { con_d.close(); }
        catch (Exception ignored) {}

    }
    
    return container_list;
     
 }
 
 
 public static List<Map<String,Object>> getEventList(int activity_id, int start_date, int end_date, String club, Connection con) {
     
    
    if (start_date == 0) start_date = (int)Utilities.getDate(con);
    
     // handle dining events
    if (activity_id == ProcessConstants.DINING_ACTIVITY_ID) {
        
        return getEventListForDining(Utilities.getOrganizationId(con), start_date, end_date, club);
    }
     
    
    List<Map<String,Object>> container_list = new ArrayList<Map<String,Object>>();
    
    Gson gson_obj = new Gson();
    ResultSet rs = null;
    PreparedStatement pstmt = null;

    if (start_date == 0) start_date = (int)Utilities.getDate(con);
    
    try {

        // find events
        pstmt = con.prepareStatement(""
                + "SELECT *, IF(season=1, c_date, date) AS date2 "
                + "FROM events2b "
                + "WHERE inactive = 0 AND IF(season=1, c_date, date) >= ? "
                + ((activity_id == ProcessConstants.ALL_ACTIVITIES) ? "" : " AND activity_id = ? ")
                + "ORDER BY date2, act_hr");
        pstmt.clearParameters();
        pstmt.setInt(1, start_date);
        pstmt.setInt(2, activity_id);
        rs = pstmt.executeQuery();

        while (rs.next()) {

            Map<String, Object> response_map = new LinkedHashMap<String, Object>();
            
            response_map.put("eventName", rs.getString("name"));
            response_map.put("eventId", rs.getInt("event_id"));
            response_map.put("eventDate", Utilities.getDateFromYYYYMMDD(rs.getInt("date2"), 2));
            
            container_list.add(response_map);
        
        }

    } catch (Exception exc) {
        
            Utilities.logError("mobileAPI.getEventList() err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
    
    return container_list;
     
 }
 
 
 public static Map<String, Object> getUserData(long member_id, int club_id, Connection con) {
     

    Map<String, Object> response_map = new HashMap<String, Object>();
    
    ResultSet rs = null;
    PreparedStatement pstmt = null;

    try {

        // retreive the member data
        pstmt = con.prepareStatement("SELECT c.clubName, m.* FROM club5 c, member2b m WHERE m.id = ? AND m.inact = 0");
        pstmt.clearParameters();
        pstmt.setLong(1, member_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            response_map.put("nameFirst", rs.getString("name_first")); // .replace("'", "&#39;")
            response_map.put("nameLast", rs.getString("name_last"));
        
            response_map.put("clubName", rs.getString("clubName"));
          //response_map.put("clubAddress", rs.getString("clubAddress"));
            
            response_map.put("memberId", member_id);
            response_map.put("clubId", club_id);
        
        } else {
            
            response_map.put("error", "Member not found.");
            
        }
              

    } catch (Exception exc) {
        
            response_map.put("error", exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
    
    return response_map;
     
 }
 
 
 public static String[] generateDeviceCode(String user, String club, Connection con) {

    
    ResultSet rs = null;
    PreparedStatement pstmt = null;

    boolean close_con = false;

    if (con == null) {

        con = Connect.getCon(ProcessConstants.REV);
        close_con = true;
    }
    
    String mauser = "";
    String authPassword = buildAuthCode(8);
    int club_id = Utilities.getClubId(club, con);
    long member_id = memberUtil.getMemberIdFromUsername(user, con);
    
    try {

        // saninty to ensure only one auth code can exist at a time for a given member
        pstmt = con.prepareStatement("DELETE FROM v5.mobile_auth WHERE member_id = ? AND club_id = ? AND active = 0 AND device_auth IS NULL"); // make sure not to delete devices that are only deactivated
        pstmt.clearParameters();
        pstmt.setLong(1, member_id);
        pstmt.setInt(2, club_id);
        pstmt.executeUpdate();

        // find the app username for this member
        pstmt = con.prepareStatement("SELECT memNum FROM member2b WHERE id = ?");
        pstmt.clearParameters();
        pstmt.setLong(1, member_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            
            mauser = rs.getString(1);
        }
        
        if (mauser.isEmpty()) mauser = user;

        pstmt = con.prepareStatement("INSERT INTO v5.mobile_auth (member_id, club_id, auth_username, auth_password, date_created, active) VALUES (?, ?, ?, ?, now(), 0)");
        pstmt.clearParameters();
        pstmt.setLong(1, member_id);
        pstmt.setInt(2, club_id);
        pstmt.setString(3, mauser);
        pstmt.setString(4, authPassword);
        pstmt.executeUpdate();
      
    } catch (Exception exc) {
        // TODO: add error handling so that we re-try if the insert fails
        authPassword = exc.getMessage();
        
    } finally {
        
        try { pstmt.close(); }
        catch (Exception ignore) {}

        if (close_con) {
            try { con.close(); }
            catch (Exception ignore) { }
        }
    }
    
    return new String[]{mauser, authPassword};
    
 }
 
 
 public static boolean authenticateDeviceKey(String deviceCode, long member_id, int club_id, Connection con) {
     

    boolean result = false;
    ResultSet rs = null;
    PreparedStatement pstmt = null;

    try {

        // find the deivce key
        // TODO: bind to the club5 table and make sure the club is not inactive
        pstmt = con.prepareStatement("SELECT * FROM v5.mobile_auth WHERE member_id = ? AND club_id = ? AND device_auth = ? AND active = 1");
        pstmt.clearParameters();
        pstmt.setLong(1, member_id);
        pstmt.setInt(2, club_id);
        pstmt.setString(3, deviceCode);
        rs = pstmt.executeQuery();

        // if we found a record then the device is authorized
        if (rs.next()) {

            // we could decode the deviceCode to make sure it decodes properly and the user/club values match but
            // if the record exists in the database then it should always decode and match shouldn't it??

            //String permDeviceAuthCode = rs.getString("device_auth");
            //String permDeviceAuthSalt = rs.getString("device_auth_salt");

            result = true; // skip further testing for now

            pstmt = con.prepareStatement("UPDATE v5.mobile_auth SET count = count + 1 WHERE id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, rs.getInt("id"));
            pstmt.executeUpdate();
                
        }

    } catch (Exception exc) { // any error is a fail
        
        Utilities.logError("mobileAPI.authenticateDeviceKey() err=" + exc.toString());
        
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
                
    return result;
     
 }
 
 
 /**
  * This method processes a request from mobile app admin web site and determines if the credentials are valid
  * @param req request object
  * @param benchmark_start timestamp to track processing time
  * @return boolean true if successful
  */
 public static boolean authenticateCaller(HttpServletRequest req) {
     
     
    boolean result = false;

    ResultSet rs = null;
    PreparedStatement pstmt = null;
    Connection con = null;
    String callerCode = "";
    int club_id = 0;

    try {
        

        callerCode = (req.getParameter("cc") == null) ? "" : req.getParameter("cc");
        club_id = (req.getParameter("cid") == null) ? 0 : Integer.parseInt(req.getParameter("cid"));

        if (!callerCode.equals("") && club_id > 0) {

            con = Connect.getCon(ProcessConstants.REV);

            // find the mobile site auth code
            //pstmt = con.prepareStatement("SELECT * FROM clubs WHERE id = ? AND msac = ? AND inactive = 0");
            pstmt = con.prepareStatement("SELECT * FROM clubs WHERE id = ? AND msac <> '' AND inactive = 0");
            pstmt.clearParameters();
            pstmt.setInt(1, club_id);
            //pstmt.setString(2, callerCode);
            rs = pstmt.executeQuery();

            // if we found a record then the caller is authorized
            if (rs.next()) {

                result = true; // skip further testing for now

            }
        }

    } catch (Exception exc) { // any error is a fail but let's log it
        
        Utilities.logError("mobileAPI.authenticateCaller() err=" + exc.toString() + ", cc=" + callerCode + ", club_id=" + club_id);
        
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}
        
        try { con.close(); }
        catch (Exception ignore) { }
    
    }

    return result;
    
 }
 
 
 /**
  * This method checks a request to see if it's coming from our first mobile app
  * @param req request object
  * @return boolean true if it is
  */
 public static boolean isRequestComingFromApp(HttpServletRequest req) {
     

    return isRequestComingFromApp(req, ProcessConstants.APPMODE_MOBILE_APP1);
    
 }
 
 
 /**
  * This method checks a request to see if it's coming from a specific mobile app version
  * @param req request object
  * @param version the version number to check for
  * @return boolean true if it is
  */
 public static boolean isRequestComingFromApp(HttpServletRequest req, int version) {
     

    return Utilities.getBitFromRequest(req, ProcessConstants.RQA_APPMODE, version);
    
 }
 
 
 public static boolean isMobileAppEnabledForClub(String club, int version, Connection con) {
     

    boolean result = false;
    ResultSet rs = null;
    PreparedStatement pstmt = null;

    try {

        // find the deivce key
        // TODO: bind to the club5 table and make sure the club is not inactive
        pstmt = con.prepareStatement("SELECT * FROM v5.clubs WHERE clubname = ? AND msac <> '' AND inactive = 0");
        pstmt.clearParameters();
        pstmt.setString(1, club);
        rs = pstmt.executeQuery();

        // if we found a record then the club is authorized
        if (rs.next()) {

            result = true; // skip further testing for now
        }

    } catch (Exception exc) { // any error is a fail
        
        Utilities.logError("mobileAPI.isMobileAppEnabledForClub() err=" + exc.toString());
        
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
                
    return result;
     
 }
 
 
 public static boolean isMobileAppStagingForClub(String club, int version, Connection con) {
     

    boolean result = false;
    ResultSet rs = null;
    PreparedStatement pstmt = null;

    try {

        // find the deivce key
        // TODO: bind to the club5 table and make sure the club is not inactive
        pstmt = con.prepareStatement("SELECT * FROM v5.clubs WHERE clubname = ? AND msac = 'Staging' AND inactive = 0");
        pstmt.clearParameters();
        pstmt.setString(1, club);
        rs = pstmt.executeQuery();

        // if we found a record then the club is authorized and in staging mode
        if (rs.next()) {

            result = true; // skip further testing for now
        }

    } catch (Exception exc) { // any error is a fail
        
        Utilities.logError("mobileAPI.isMobileAppStagingForClub() err=" + exc.toString());
        
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
                
    return result;
     
 }
 
 
 public static boolean isUserConfiguredForApp(String club, int member_id, Connection con) {
     
    return isUserConfiguredForApp(Utilities.getClubId(club, con), member_id, con);
     
 }
 
 
 public static boolean isUserConfiguredForApp(int club_id, int member_id, Connection con) {
     

    boolean result = false;
    ResultSet rs = null;
    PreparedStatement pstmt = null;

    try {

        // find the deivce key
        // TODO: bind to the club5 table and make sure the club is not inactive
        pstmt = con.prepareStatement("SELECT * FROM v5.mobile_auth WHERE member_id = ? AND club_id = ? AND device_auth <> '' AND active = 1");
        pstmt.clearParameters();
        pstmt.setLong(1, member_id);
        pstmt.setInt(2, club_id);
        rs = pstmt.executeQuery();

        // if we found a record then the user has a device configured
        if (rs.next()) {

            result = true; // skip further testing for now
        }

    } catch (Exception exc) { // any error is a fail
        
        Utilities.logError("mobileAPI.isUserConfiguredForApp() err=" + exc.toString());
        
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
                
    return result;
     
 }
 
 
 /**
  * This method processes a request from a remote device and determines if the credentials are valid
  * @param req request object
  * @return boolean true if successful
  */
 public static boolean authenticateDevice(HttpServletRequest req) {

     
    boolean result = false;

    Connection con = Connect.getCon(ProcessConstants.REV);

    try {

        String deviceCode = (req.getParameter("dc") == null) ? "" : req.getParameter("dc");
        long member_id = (req.getParameter("mid") == null) ? 0 : Integer.parseInt(req.getParameter("mid"));
        int club_id = (req.getParameter("cid") == null) ? 0 : Integer.parseInt(req.getParameter("cid"));

        if (!deviceCode.equals("") && member_id > 0 && club_id > 0) {

            // backdoor for testing
            if (ProcessConstants.SERVER_ID == 4 && deviceCode.equals("foretees") && member_id == 594 && club_id == 1) {
                result = true;
            } else {
                result = authenticateDeviceKey(deviceCode, member_id, club_id, con);
            }
        }

    } catch (Exception exc) { 
    
        Utilities.logError("mobileAPI.authenticateDeviceKey() err=" + exc.toString());
    }

    // close our connection
    try { con.close(); }
    catch (Exception ignore) { }

    return result;
    
 }


//
// Returns a randomly generated string to be used as a salt
// The string returned will only use ascii chars 48-57 & 65-90 (0-9, A-Z)
// Updated to exclude zero and the letters I, L and O
//
public static String buildAuthCode(int length) {

    SecureRandom r = new SecureRandom();
    StringBuffer salt_key = new StringBuffer();
    
    int randomChar = 0;
    
    do {

        randomChar = 48 + r.nextInt(42);
        
        do {
            
            randomChar = 48 + r.nextInt(42);
            
        } while ( (randomChar > 57 && randomChar < 65) || (randomChar > 90 && randomChar < 65) || randomChar == 48 || randomChar == 73 || randomChar == 76 || randomChar == 79 );
        
        salt_key.append((char)(randomChar));

    } while (salt_key.length() < length);

    return salt_key.toString();

 }


 public static String asHex(byte buf[]) {
    StringBuffer strbuf = new StringBuffer(buf.length * 2);

    for(int i=0; i< buf.length; i++) {
            if(((int) buf[i] & 0xff) < 0x10)
                    strbuf.append("0");
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
    }
    return strbuf.toString();
 }
 

 public static Map<String, Object> getBaseUrl(int activity_id, String club, Integer app_mode) {

     
    Map<String, Object> response_map = new HashMap<String, Object>();
    
    String activity = null;

    if(app_mode == null){
        app_mode = Utilities.setBit(0, ProcessConstants.APPMODE_MOBILE_APP1, true);
    }

    if (activity_id == ProcessConstants.DINING_ACTIVITY_ID) {

        activity = "dining";

    } else if (activity_id == 0){

        activity = "golf";

    } else {

        activity = "flxrez" + activity_id;

    }

    response_map.put("baseUrl", club + ProcessConstants.UNDERSCORE + activity + "_m" + app_mode);
    
    return response_map;

 }
    
 
 public static boolean deactivateDevice(int mobile_auth_id, long member_id, int club_id, Connection con) {


    boolean result = false;
     
    PreparedStatement pstmt = null;
    //Connection con = Connect.getCon(ProcessConstants.REV);
     
    try {

        // mark the specific device as inactive (we can detect inactive devices with active = 0 and device_auth is not null
        pstmt = con.prepareStatement("UPDATE v5.mobile_auth SET active = 0 WHERE id = ? AND member_id = ? AND club_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, mobile_auth_id);    // the specific device
        pstmt.setLong(2, member_id);        // sanity
        pstmt.setInt(3, club_id);           // sanity
        
        if (pstmt.executeUpdate() > 0) result = true;
        
    } catch (Exception ignore) { // any error is a fail

        Utilities.logError("mobileAPI.deactivateDevice(): Error updaing v5.mobile_auth to deactivate a member's device. memid=" + member_id + ", clubid=" + club_id + ", mobile_auth_id=" + mobile_auth_id);

    } finally {

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
          
    return result;
    
 }   
 
 /*
 private Map<String, Object> authenticateDevice(HttpServletRequest req, long benchmark_start) {
    

    Map<String, Object> container_map = new HashMap<String, Object>();
    Map<String, Object> response_map = new HashMap<String, Object>();

    container_map.put("foreTeesMobileApiResp", response_map);
    
    response_map.put("action", req.getParameter("type"));
    
    if (authenticateDeviceCode(req)) {
        
        // success
        response_map.put("success", "1");
        
    } else {
        
        // fail
        response_map.put("success", "0");
        
    }
    
    response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");
    response_map.put("serverId", ProcessConstants.SERVER_ID);
    
    return container_map;

 }
 */
 /*
 private Map<String, Object> requestDeviceAuthorization_old(HttpServletRequest req, long benchmark_start) {
    

    Map<String, Object> container_map = new HashMap<String, Object>();
    Map<String, Object> response_map = new HashMap<String, Object>();

    container_map.put("foreTeesMobileApiResp", response_map);
    
    response_map.put("action", req.getParameter("type"));

    String deviceKey = validateDeviceCode(req);
    
    if (deviceKey == null || deviceKey.equals("")) {
        
        // fail
        response_map.put("success", "0");
        
    } else {
        
        // success
        response_map.put("success", "1");
        response_map.put("deviceAuthKey", deviceKey);
        // other items to include?
        
    }
    
    response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");
    response_map.put("serverId", ProcessConstants.SERVER_ID);
    
    return container_map;

 }
 */
 
 
 /*
 private Map<String, Object> requestDeviceAuthorization(HttpServletRequest req, long benchmark_start) {
    

    Map<String, Object> container_map = new HashMap<String, Object>();
    Map<String, Object> response_map = new HashMap<String, Object>();

    container_map.put("foreTeesMobileApiResp", response_map);
    
    response_map.put("action", req.getParameter("type"));

    //String deviceKey = validateDeviceCode(req);
    
    
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    Connection con = Connect.getCon(ProcessConstants.REV);

    String authCode = (req.getParameter("ac") == null) ? "" : req.getParameter("ac").trim();
    String deviceAllias = (req.getParameter("da") == null) ? "" : req.getParameter("da").trim();
    String deviceUuid = (req.getParameter("du") == null) ? "" : req.getParameter("du").trim();
    String permDeviceAuthSalt = BasicSHA256.getSalt(16);
    String permDeviceAuthCode = null;
    
    int club_id = 0;
    int member_id = 0;
    
    // only process if required bits are here
    if (!authCode.equals("") && !deviceAllias.equals("")) {
    
        try {

            // find an unused auth_lookup_code that's less than an hour old
            pstmt = con.prepareStatement("SELECT * FROM v5.mobile_auth WHERE auth_lookup_code = ? AND active = 0 AND date_created >= DATE_ADD(now(), INTERVAL 59 MINUTE)");
            pstmt.clearParameters();
            pstmt.setString(1, authCode);
            rs = pstmt.executeQuery();
            
            // if we found one then continue
            if (rs.next()) {

                // build a uid for this device to store and send back to us for authentication
                String decodedString = permDeviceAuthSalt + "|" + deviceAllias + "|" + rs.getString("member_id") + "|" + rs.getString("club_id") + "|" + deviceUuid;
                permDeviceAuthCode = BasicSHA256.SHA256(decodedString);

                // add the permenant record (clear the auth_lookup_code, set active to 1, add new values
                pstmt = con.prepareStatement("UPDATE v5.mobile_auth SET device_name = ?, device_uid = ?, device_auth = ?, device_auth_salt = ?, auth_lookup_code = '', date_created = now(), active = 1 WHERE id = ?");
                pstmt.clearParameters();
                pstmt.setString(1, deviceAllias);
                pstmt.setString(2, deviceUuid);
                pstmt.setString(3, permDeviceAuthCode);
                pstmt.setString(4, permDeviceAuthSalt);
                pstmt.setInt(5, rs.getInt("id"));
                pstmt.executeUpdate();
                
                club_id = rs.getInt("club_id");
                member_id = rs.getInt("member_id");
                
            }

        } catch (Exception exc) {
            
        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }
    }

    // close our connection
    try { con.close(); }
    catch (Exception ignore) { }
    
    if (permDeviceAuthCode == null || permDeviceAuthCode.equals("") || club_id == 0 || member_id == 0) {
        
        // fail
        response_map.put("success", "0");
        
    } else {
        
        // success
        response_map.put("success", "1");
        response_map.put("deviceAuthKey", permDeviceAuthCode);
        response_map.put("memberId", member_id);
        response_map.put("clubId", club_id);
        
    }
    
    response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");
    response_map.put("serverId", ProcessConstants.SERVER_ID);
    
    return container_map;

 }
 */
 
 /*
 public static Map<String, Object> requestDeviceAuthCode(long benchmark_start) {
    

    Map<String, Object> container_map = new HashMap<String, Object>();
    Map<String, Object> response_map = new HashMap<String, Object>();

    container_map.put("foreTeesMobileApiResp", response_map);

    String authCode = generateDeviceCode("6700", "demov4", null);
    StringBuilder sb = new StringBuilder(authCode)
                                .insert(4,"-")
                                .insert(9,"-")
                                .insert(14,"-");
    
    response_map.put("authCode", authCode);
    response_map.put("prettyAuthCode", sb.toString());
    response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");
    response_map.put("serverId", ProcessConstants.SERVER_ID);
    
    return container_map;

 }
 */
 
 
 /*
 // API CALL - NO SESSION
 private String validateDeviceCode(HttpServletRequest req) {
     

    ResultSet rs = null;
    PreparedStatement pstmt = null;
    Connection con = Connect.getCon(ProcessConstants.REV);

    String authCode = (req.getParameter("ac") == null) ? "" : req.getParameter("ac").trim();
    String deviceAllias = (req.getParameter("da") == null) ? "" : req.getParameter("da").trim();
    String deviceUuid = (req.getParameter("du") == null) ? "" : req.getParameter("du").trim();
    String permDeviceAuthSalt = BasicSHA256.getSalt(16);
    String permDeviceAuthCode = null;
    
    // only process if required bits are here
    if (!authCode.equals("") && !deviceAllias.equals("")) {
    
        try {

            // find an unused auth_lookup_code that's less than an hour old
            pstmt = con.prepareStatement("SELECT * FROM v5.mobile_auth WHERE auth_lookup_code = ? AND active = 0 AND date_created >= DATE_ADD(now(), INTERVAL 59 MINUTE)");
            pstmt.clearParameters();
            pstmt.setString(1, authCode);
            rs = pstmt.executeQuery();
            
            // if we found one then continue
            if (rs.next()) {

                // build a uid for this device to store and send back to us for authentication
                String decodedString = permDeviceAuthSalt + "|" + deviceAllias + "|" + rs.getString("member_id") + "|" + rs.getString("club_id") + "|" + deviceUuid;
                permDeviceAuthCode = BasicSHA256.SHA256(decodedString);

                // add the permenant record (clear the auth_lookup_code, set active to 1, add new values
                pstmt = con.prepareStatement("UPDATE v5.mobile_auth SET device_name = ?, device_uid = ?, device_auth = ?, device_auth_salt = ?, auth_lookup_code = '', date_created = now(), active = 1 WHERE id = ?");
                pstmt.clearParameters();
                pstmt.setString(1, deviceAllias);
                pstmt.setString(2, deviceUuid);
                pstmt.setString(3, permDeviceAuthCode);
                pstmt.setString(4, permDeviceAuthSalt);
                pstmt.setInt(5, rs.getInt("id"));
                pstmt.executeUpdate();
                
            }

        } catch (Exception exc) {
            
            permDeviceAuthCode = exc.getMessage();
            
        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }
    }

    // close our connection
    try { con.close(); }
    catch (Exception ignore) { }
                
    return permDeviceAuthCode;
     
 }
*/
 
}