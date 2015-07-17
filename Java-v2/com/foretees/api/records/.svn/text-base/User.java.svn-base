/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.records;

import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.ArrayUtil;
import com.foretees.common.Connect;
import com.foretees.common.AESencrypt;
import com.foretees.common.reqUtil;
import com.foretees.common.LoginCredentials;
import java.sql.*;          // mysql
//import java.util.UUID;
//import javax.naming.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author John Kielkopf
 */
public class User {
    
    public Long id;
    public Long user_access_id;
    public Long club_id; // Partially implemented (to be used for club account abstration)
    public String club; // Partially implemented (To be used for club account abstration)
    public String name;
    public String email;
    public String rep_code;
    public String password; // Only set if you want to change it.  Never use value from DB, as it is a hash.
    public Float default_commission;
    public Boolean disabled;
    public Long updated;
    
    public Long member_id; // Used by club members ONLY
    public String username; // Used by club members ONLY
    public String mship; // Used by club members ONLY
    public String mtype; // Used by club members ONLY
    public String wc; // Used by club members ONLY
    public String caller; // Used by club members ONLY
    
    public String auth_token; // Set to the session's auth token in ApiAccess.getUser() if access was obtained via non-session auth token.
    
    public UserAccess access;
    
    public String last_error;
    
    
    // Users
    private final static String sql_select = ""
            + "SELECT "
            + "    u.id as user_id, "
            + "    u.user_access_id, "
            + "    u.name as user_name, "
            + "    u.email, "
            + "    u.rep_code, "
            + "    u.password, "
            + "    u.default_commission, "
            + "    u.disabled as user_disabled, "
            + "    u.updated as user_updated, "
            + "    ua.* "
            + "  FROM users u "
            + "    INNER JOIN user_access ua "
            + "      ON ua.id = u.user_access_id ";
    private final static String sql_order = " ORDER BY u.name, u.email ";
    
    private final static String sql_all = sql_select + sql_order;
    private final static String sql_by_id = sql_select + "  WHERE u.id = ? " + sql_order;
    private final static String sql_by_email = sql_select + "  WHERE u.email = ? " + sql_order;
    private final static String sql_by_email_and_password =  sql_select
            + "  WHERE u.email = ? AND u.password = PASSWORD(?) AND u.disabled = 0 AND ua.disabled = 0 " + sql_order;
    

    public User(){}; // Empty parm
    
    public User(Long id, Long user_access_id, String name, String email, String password, Float default_commission, 
            boolean disabled){
        this.id = id;
        this.user_access_id = user_access_id;
        this.name = name;
        this.email = email;
        this.password = password; // Only set if you want to change it.  Never use value from DB, as it is a hash.
        this.default_commission = default_commission;
        this.disabled = disabled;
    }
    
    public User(ResultSet rs){
        loadFromResultSet(rs);
    }
    /*
    public User(HttpServletRequest req){
        loadFromHttpServletRequest(req);
    }
    */
    public User(String club, String email, String password, Connection con_ft){
        loadByAccountName(club, email, password, con_ft);
    }
    
    public User(String club, String email, String password){
        Connection con_ft = ApiCommon.getConnection();
        loadByAccountName(club, email, password, con_ft);
        Connect.close(con_ft);
    }
    
    public User(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public User(long id){
        Connection con_ft = ApiCommon.getConnection();
        loadById(id, con_ft);
        Connect.close(con_ft);
    }
    public final Long loadById(long id, Connection con_ft){
        return loadById(id, con_ft, false);
    }
    
    public final Long loadById(long id, Connection con_ft, boolean skip_loading){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con_ft.prepareStatement(sql_by_id);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = loadFromResultSet(rs, skip_loading);
            } else {
                last_error = String.format(ApiConstants.error_finding_by_id, classNameLower(), id);
            }  
        } catch(Exception e) {
            Connect.logError(className()+".loadById: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = String.format(ApiConstants.error_loading_by_id, classNameLower(), e.toString());
        } finally {
            Connect.close(rs, pstmt);
        }
        
        return result;
        
    }
    public final Long loadByAccountName(String club, String email, String password, Connection con_ft){
        return loadByAccountName(club, email, password, con_ft, false);
    }
    
    public final Long loadByAccountName(String club, String email, String password, Connection con_ft, boolean skip_loading){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        if (club == null || club.isEmpty()) {
            // Foretees global user account
            try {
                if (password != null) {
                    pstmt = con_ft.prepareStatement(sql_by_email_and_password);
                } else {
                    pstmt = con_ft.prepareStatement(sql_by_email);
                }

                pstmt.setString(1, email.trim());
                if (password != null) {
                    if (!password.trim().isEmpty()) {
                        pstmt.setString(2, password.trim());
                    } else {
                        // searching for empty password.  Make some random noise so we fail password match.
                        // This is to block matching on accounts that may have accidentally set their password to an empty string
                        pstmt.setString(2, UUID.randomUUID().toString());
                    }
                }
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    result = loadFromResultSet(rs, skip_loading);
                } else {
                    last_error = "Unable to find User: " + email;
                }
            } catch (Exception e) {
                Connect.logError(className() + ".loadByAccountName: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                last_error = "Error Loading User: " + e.toString();
            } finally {
                Connect.close(rs, pstmt);
            }
        } else {
            // Club account
            last_error = "Club account access currently unimplemented.";
        }
        
        return result;
        
    }
    
    private Long loadFromResultSet(ResultSet rs){
        return loadFromResultSet(rs, false);
    }
    
    private Long loadFromResultSet(ResultSet rs, boolean skip_loading){
        
        Long result = null;
        try {
            if(skip_loading){
                result = rs.getLong("user_id");
            } else {
                this.id = rs.getLong("user_id");
                this.club_id = null;
                this.user_access_id = rs.getLong("user_access_id");
                this.club = null;
                this.name = rs.getString("user_name");
                this.rep_code = rs.getString("rep_code");
                this.email = rs.getString("email");
                this.password = null;
                this.default_commission = rs.getFloat("default_commission");
                this.disabled = rs.getBoolean("user_disabled");
                this.updated = rs.getTimestamp("user_updated").getTime();
                
                this.access = new UserAccess(rs);
                
                this.access.single_use_key = this.id != null;
                
                result = id;
            }
        } catch(Exception e) {
            last_error = String.format(ApiConstants.error_resultset, classNameLower(), e.toString());
            Connect.logError(className()+".loadFromResultSet: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            return null;
        }
        return result;
        
    }
    
    public final Long save(){
        Long result = null;
        Connection con_ft = ApiCommon.getConnection();
        result = save(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final Long save(Connection con_ft){
        Long result = null;
        
        PreparedStatement pstmt = null;
        
        if (rep_code == null || rep_code.trim().isEmpty()) {
            rep_code = null;
        }
        if (rep_code == null || rep_code.trim().length() < 2) {
            if (rep_code != null) {
                rep_code = rep_code.trim().toUpperCase();
            }
        } else {
            rep_code = rep_code.trim().toUpperCase().substring(0, 2);
        }
        
        if(email == null || email.trim().isEmpty()){
            last_error = ApiConstants.error_empty_email;
        } else if(name == null || name.trim().isEmpty()){
            last_error = ApiConstants.error_empty_name;
        } else if(id == null){
            // Inserting new record
            try {
                if (password != null) {
                    pstmt = con_ft.prepareStatement(""
                        + "INSERT INTO users "
                        + "  (user_access_id, name, email, rep_code, default_commission, "
                        + "    disabled, password) "
                        + "  VALUES"
                        + "  (?, ?, ?, ?, ?, "
                        + "   ?, PASSWORD(?))");
                } else {
                    pstmt = con_ft.prepareStatement(""
                        + "INSERT INTO users "
                        + "  (user_access_id, name, email, rep_code, default_commission, "
                        + "    disabled) "
                        + "  VALUES"
                        + "  (?, ?, ?, ?, ?, "
                        + "   ?)");
                }
                
                int i = 1;
                pstmt.setLong(i++, user_access_id);
                pstmt.setString(i++, name.trim());
                pstmt.setString(i++, email.trim());
                pstmt.setString(i++, rep_code);
                pstmt.setFloat(i++, default_commission);
                pstmt.setBoolean(i++, disabled);
                if(password != null && !password.trim().isEmpty()){
                    pstmt.setString(i++, password.trim());
                }
                pstmt.executeUpdate();
                
                result = Connect.getLastInsertId(con_ft);
                
                id = result;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                if(loadByAccountName(club, email, null, con_ft, true) != null){
                    last_error = String.format(ApiConstants.error_update_duplicate_email, classNameLower(), email);
                } else {
                    Connect.logError(className()+".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_insert, classNameLower(), e.toString());
                }
            } finally {
                Connect.close(pstmt);
            }
        } else {
            // Update existing record
            if (id == ApiConstants.system_account_user_id) {
                last_error = "SYSTEM account can not be modified.";
            } else {
                try {
                    if (password != null) {
                        pstmt = con_ft.prepareStatement(""
                                + "UPDATE users "
                                + " SET"
                                + "  user_access_id = ?,"
                                + "  name = ?,"
                                + "  email = ?,"
                                + "  rep_code = ?,"
                                + "  default_commission = ?, "
                                + "  disabled = ?, "
                                + "  password = PASSWORD(?) "
                                + " WHERE id = ? ");
                    } else {
                        pstmt = con_ft.prepareStatement(""
                                + "UPDATE users "
                                + " SET"
                                + "  user_access_id = ?,"
                                + "  name = ?,"
                                + "  email = ?,"
                                + "  rep_code = ?,"
                                + "  default_commission = ?, "
                                + "  disabled = ? "
                                + " WHERE id = ? ");
                    }

                    int i = 1;
                    pstmt.setLong(i++, user_access_id);
                    pstmt.setString(i++, name.trim());
                    pstmt.setString(i++, email.trim());
                    pstmt.setString(i++, rep_code);
                    pstmt.setFloat(i++, default_commission);
                    pstmt.setBoolean(i++, disabled);
                    if (password != null && !password.trim().isEmpty()) {
                        pstmt.setString(i++, password.trim());
                    }
                    pstmt.setLong(i++, id);

                    pstmt.executeUpdate();

                    result = id;

                    loadById(id, con_ft); // Refresh results

                } catch (Exception e) {
                    Long check_id = loadByAccountName(club, email, null, con_ft, true);
                    if (check_id != null && !check_id.equals(id)) {
                        last_error = String.format(ApiConstants.error_update_duplicate_email, classNameLower(), email);
                    } else {
                        Connect.logError(className() + ".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                        last_error = String.format(ApiConstants.error_update, classNameLower(), e.toString());
                    }
                } finally {
                    Connect.close(pstmt);
                }
            }
        }

        return result;
    }

    public final boolean delete(){
        Connection con_ft = ApiCommon.getConnection();
        boolean result = delete(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final boolean delete(Connection con_ft){
        boolean result = false;
        
        PreparedStatement pstmt = null;
        
        if(id == null){
            // Can't delete a record with a null id
            last_error = String.format(ApiConstants.error_delete_null_id, classNameLower(), className());

        } else {
            // Update existing record
            if (id == ApiConstants.system_account_user_id) {
                last_error = "SYSTEM account can not be modified.";
            } else {
                try {
                    pstmt = con_ft.prepareStatement(""
                            + "DELETE FROM users "
                            + " WHERE id = ? ");

                    int i = 1;
                    pstmt.setLong(i++, id);

                    pstmt.executeUpdate();

                    result = true;

                } catch (Exception e) {
                    Connect.logError(className() + ".delete: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_delete, classNameLower(), e.toString());
                } finally {
                    Connect.close(pstmt);
                }
            }
        }

        return result;
    }
    
    
    
    public static List<User> getlist(){
        return getList(null);
    }
    
    public static List<User> getList(Long id){
        
        List<User> result = new ArrayList<User>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_ft = ApiCommon.getConnection();
        
        String error = null;
        
        User r = new User();
        
        try {
            if(id != null){
                pstmt = con_ft.prepareStatement(sql_by_id);
                pstmt.setLong(1, id);
            } else {
                pstmt = con_ft.prepareStatement(sql_all);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new User(rs);
                if(r.id != null && r.last_error == null && r.access != null && r.access.last_error == null){
                    result.add(r);
                } else {
                    if (r.last_error != null) {
                        error = r.last_error;
                    } else if (r.access == null) {
                        error = String.format(ApiConstants.error_null_record, "user access");
                    } else if (r.access.last_error != null) {
                        error = r.access.last_error;
                    } else {
                        error = String.format(ApiConstants.error_unknown_condition, r.classNameLower());
                    }
                }
            }  
        } catch(Exception e) {
            error = String.format(ApiConstants.error_db_select, r.classNameLower(), e.toString());
            Connect.logError(r.className() + ".getList: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt, con_ft);
        }
 
        if(error != null){
            Connect.logError(r.className() + ".getList: Err=" + error);
            return null;
        } else {
            return result;
        }

    }
    
    public static User getMemberUser(long member_id, long club_id){
        Connection con_club = ApiCommon.getConnection(club_id);
        User result = getMemberUser(member_id, club_id, con_club);
        Connect.close(con_club);
        return result;
    }
    
    // Expensive.  Don't use
    //public static User getMemberUser(long member_id, Connection con_club){
    //    Long club_id = ApiCommon.getClubId(con_club);
    //    return getMemberUser(member_id, club_id, con_club);
    //}
    
    // Load member user record and access levels.
    public static User getMemberUser(long member_id, long club_id, Connection con_club){
        User result = new User();
        ClubMember member = new ClubMember(member_id, con_club);
        if(member.last_error != null){
            result.last_error = member.last_error;
            return result;
        }
        result.disabled = member.disabled;
        result.member_id = member.id;
        result.club = member.club;
        result.club_id = club_id;
        result.username = member.username;
        result.mship = member.m_ship;
        result.mtype = member.m_type;
        result.wc = member.wc;
        
        result.caller = "";
        
        result.access = new UserAccess();
        result.access.manager_portal = member.allow_mp;
        result.access.view_member_reservations = result.access.manager_portal;
        result.access.view_foretees_announcements = result.access.manager_portal;
        result.access.bind_to_club = true;
        result.access.single_use_key = !result.username.isEmpty();
        result.disabled = false;
        if(result.access.manager_portal){
            result.access.name = "Manager";
        } else {
            result.access.name = "Member";
        }
        return result;
    }
    
    // Load member user record and access levels.
    public static User getMemberUser(HttpServletRequest req){
        User result;
        String username = reqUtil.getSessionString(req, "user", reqUtil.getSessionString(req, "ext-user", null));
        String club = reqUtil.getSessionString(req, "club", null);
        Long member_id = reqUtil.getSessionLong(req, "member_id", null);
        Long club_id = ApiCommon.getClubId(club);
        if(member_id == null || username == null || club == null || club_id == null){
            result = new User();
            List errlist = new ArrayList();
            if(member_id == null){
                errlist.add("Member ID Null");
            }
            if(username == null){
                errlist.add("Username Null");
            }
            if(club == null){
                errlist.add("Club Null");
            }
            if(club_id == null){
                errlist.add("Club ID Null");
            }
            result.last_error = "No valid member session: " + org.apache.commons.lang.StringUtils.join(errlist, "; ");
            return result;
        } else {
            result = User.getMemberUser(member_id, club_id);
        }
        result.caller = reqUtil.getSessionString(req, "caller", "");
        
        return result;
    }
    
    // Load proshop user record and access levels. (Only works for viewing invoices currently)
    public static User getProshopUser(HttpServletRequest req){
        User result = new User();
        if(!reqUtil.getSessionString(req, "user", "").toLowerCase().startsWith(LoginCredentials.proshop)){
            result.last_error = "No valid proshop session";
            return result;
        }
        
        result.club = reqUtil.getSessionString(req, "club", "");
        result.club_id = ApiCommon.getClubId(result.club);
        result.username = reqUtil.getSessionString(req, "user", "");
        result.name = reqUtil.getSessionString(req, "name", "Proshop User");
        result.caller = reqUtil.getSessionString(req, "caller", "");
        
        result.access = new UserAccess();
        result.access.view_invoices = true;
        result.access.view_member_reservations = true;
        result.access.view_foretees_announcements = true;
        result.access.manager_portal = true; // probably should be false?
        result.access.bind_to_club = true;
        result.access.single_use_key = true;
        result.access.name = "Proshop";
        result.disabled = false;
        
        return result;
    }
    
    
    
    public final String className(){
        return this.getClass().getSimpleName();
    }
    
    public final String classNameProper(){
        return ApiCommon.formatClassName(className());
    }
    
    public final String classNameLower(){
        return ApiCommon.formatClassNameLower(className());
    }
    
    
}
