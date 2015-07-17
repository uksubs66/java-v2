/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api;

import com.foretees.common.*;
import com.foretees.api.records.*;
import java.sql.*;          // mysql
//import javax.naming.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;


/**
 *
 * @author Owner
 */
public class ApiAccess {
    
    private static int idx_club = 0;
    private static int idx_username = 1;
    private static int idx_password = 2;
    
    private static String req_user_record = "global_user_record";
    
    // We must do this in ApiAccess and not User, since this uses SingleUseAccessKey, and SingleUseAccessKey uses User.
    public static User getUser(HttpServletRequest req){
        return getUser(req, false);
    }
    public static User getUser(HttpServletRequest req, boolean force_session_use){
        
        if (req != null) {
            // See if User was cached in the request
            Object reqUser = null;
            reqUser = req.getAttribute(req_user_record);
            if (reqUser != null && reqUser instanceof User) {
                return (User) reqUser;
            }
        }
        
        // Get authentication parameters that we may use.  First we'll check if they were passed in the request header (preferred), or in the query string (it's best if only Single Use Tokens/Keys are passed in the query string)
        String requestAuthToken = reqUtil.getHeaderString(req, ApiConstants.header_auth_token, reqUtil.getParameterString(req, ApiConstants.parameter_auth_token, null));
        String sua_key = reqUtil.getHeaderString(req, ApiConstants.header_single_use_key, reqUtil.getParameterString(req, ApiConstants.parameter_single_use_key, null));
        String sessionAuthToken = getAuthenticationToken(req);
        String authToken = (requestAuthToken!=null?requestAuthToken:sessionAuthToken);
        String mobileWebApiDeviceCode = reqUtil.getHeaderString(req, ApiConstants.header_device_code, reqUtil.getParameterString(req, ApiConstants.parameter_device_code, null));
        Long mobileWebApiMemberId = reqUtil.getHeaderLong(req, ApiConstants.header_member_id, reqUtil.getParameterLong(req, ApiConstants.parameter_member_id, null));
        Integer mobileWebApiClubId = reqUtil.getHeaderInteger(req, ApiConstants.header_club_id, reqUtil.getParameterInteger(req, ApiConstants.parameter_club_id, null));
        
        String error = null;
        
        User result = null;
        
        SingleUseAccessKey.purge(); // Purge any expired single use keys
        
        if (!reqUtil.getRequestBoolean(req, ProcessConstants.RQA_BLOCKUSER, false)) { // Since this may call verifymem below, and verifymem could call methods in Common_skin that call this, we check RQA_BLOCKUSER so we don't get stuck in a loop.
            if (!force_session_use && mobileWebApiDeviceCode != null && mobileWebApiMemberId != null && mobileWebApiClubId != null) {
                // Mobile web API authentication.
                Connection club_con = ApiCommon.getConnection((long) mobileWebApiClubId);
                try {
                    if (mobileAPI.authenticateDeviceKey(mobileWebApiDeviceCode, mobileWebApiMemberId, mobileWebApiClubId, club_con)) {
                        // Valid mobileWebAPI authentication
                        result = User.getMemberUser(mobileWebApiMemberId, (long) mobileWebApiClubId);
                    } else {
                        error = "Unable to Authenticate Device";
                    }
                } catch (Exception e) {
                    Connect.logError("Access.getUser Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                }
                Connect.close(club_con);
            } else if (!force_session_use && sua_key != null) {
                // Single Use Token Authentication
                SingleUseAccessKey singleUserAccessKey = new SingleUseAccessKey(sua_key);
                //apiResp.debug.put("singleUserAccessKey", singleUserAccessKey);
                if (singleUserAccessKey.last_error == null) {
                    result = singleUserAccessKey.user;
                } else {
                    error = singleUserAccessKey.last_error;
                }
                singleUserAccessKey.delete();
            } else {
                // Other Authentication Method
                if (!force_session_use && authToken != null) {
                    // Check if this is using a foretees global account authentication token
                    List<String> tokenParts = decodeAuthenticationToken(authToken);
                    if (tokenParts != null) {
                        result = new User(tokenParts.get(idx_club), tokenParts.get(idx_username), tokenParts.get(idx_password));
                    } else {
                        result = new User();
                        result.last_error = "Invalid Authentication Token";
                    }
                } else if (req != null) {
                    // No valid auth token sent in header/query string
                    // See if there is a fortees global user_id in the session
                    Long user_id = reqUtil.getSessionLong(req, ApiConstants.sess_user_id, null);
                    if (user_id != null) {
                        // Get foretees global user from session.
                        result = new User(user_id);
                    } else {
                        // No foretees global user_id in session.  Try for a club proshop user or member
                        if (VerifyUser.verifyPro(req).session != null) {
                            // Looks like we're a proshop user
                            result = User.getProshopUser(req);
                        } else {
                            // Not a club proshop user.  Try for a member.
                            VerifyUser verify_user = VerifyUser.verifyMem(req);
                            if (verify_user.session != null) {
                                // Looks like we're a member
                                result = User.getMemberUser(req);
                            } else if (verify_user.error != null) {
                                // Nothing to get from session.  Give up.
                                result = new User();
                                result.last_error = verify_user.error;
                            }
                        }
                    }
                }
            }
        }
        
        // Validate the user be loaded, if any.
        if (result != null && result.last_error != null) {
            //Connect.logError("ApiAccess.getUser Err=" + result.last_error);
        }
        if (result == null) {
            // Wasn't able to load a user
            result = new User();
            result.last_error = "Access Denied; Invalid or expired session.";
        }
        if (result.access == null) {
            // No access levels loded.  Load default access (none).
            result.access = new UserAccess();
            result.access.name = "No Access";
            result.access.disabled = false;
        }
        if (error != null) {
            result.last_error = error;
        } else if (result.last_error == null && result.access.last_error != null) {
            result.last_error = result.access.last_error;
        }
        if (result.disabled == null || result.disabled || result.access.disabled == null || result.access.disabled) {
            // If this account is disabled, disable all access levels.
            result.disabled = true;
            result.access = new UserAccess();
            result.access.name = "Disabled";
            result.access.disabled = true;
        }
        if (req != null && result.last_error == null && result.access.last_error == null && !result.disabled && !result.access.disabled) {
            // Cache in request
            req.setAttribute(req_user_record, result);
            if (requestAuthToken == null && result.id != null && sessionAuthToken != null) {
                // Authentication wasn't via auth token.  Return our session auth token for later API use
                result.auth_token = sessionAuthToken;
            }
        } else if(result.last_error != null) {
            StringBuilder log_error = new StringBuilder("ApiAccess.getUser Err=");
            log_error.append(result.last_error);
            if(req == null){
                log_error.append("; req=null");
            } else {
                log_error.append("; ip=");
                log_error.append(reqUtil.getRemoteAddrs(req));
            }
            Connect.logError("ApiAccess.getUser Err=" + log_error.toString());
        }

        return result;
        
    }
    
    public static String getAuthenticationToken(HttpServletRequest req){
        return reqUtil.getSessionString(req, ApiConstants.session_auth_token_key, null);
    }
    
    public static void setAuthenticationToken(HttpServletRequest req, String club, String username, String password) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.setAttribute(ApiConstants.session_auth_token_key, getAuthenticationToken(club, username, password)); // Set our authentication token for API access
        } else {
            Connect.logError("ApiAccess.setAuthenticationToken Err=Null Sesssion");
        }

    }
    
    public static String getAuthenticationToken(String club, String username, String password){
        if(club == null){
            club = "";
        }
        String authToken = null;
        User ua = new User(club, username, password);
        if(ua != null && ua.last_error == null){
            String tokenCsv = ArrayUtil.encodeCsvLine(new String[]{club, username, password});
            //Connect.logError("Access.getAuthenticationToken Debug=" + tokenCsv);
            try {
                authToken = AESencrypt.encryptBase64(tokenCsv, ApiConstants.auth_token_gen_key, ApiConstants.auth_token_gen_iv);
            }catch (Exception e){
                Connect.logError("ApiAccess.getAuthenticationToken Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            }
        } else {
            if(ua == null){
                Connect.logError("ApiAccess.getAuthenticationToken Err=Null User Object");
            } else {
                Connect.logError("ApiAccess.getAuthenticationToken Err=" + ua.last_error);
            }
            
        }
        return authToken;
    }
    
    public static List<String> decodeAuthenticationToken(String authToken){
        List<String> result = null;
        String csv = null;
        try {
            //Connect.logError("Access.getAuthenticationToken Debug=" + authToken);
            csv = AESencrypt.decryptBase64(authToken, ApiConstants.auth_token_gen_key, ApiConstants.auth_token_gen_iv);
            result = ArrayUtil.parseCsvLine(new StringReader(csv));
            if(result.size() != 3){
                Connect.logError("ApiAccess.decodeAuthenticationToken Err=Invalid Authentication Data");
                result = null;
            } else {
                 //Connect.logError("ApiAccess.getAuthenticationToken Debug= COMPLETE:" + csv);
            }
        }catch (Exception e){
            Connect.logError("ApiAccess.decodeAuthenticationToken Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        }
        return result;
    }
    
}
