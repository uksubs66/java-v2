/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.records;

import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.Connect;
import java.sql.*;          // mysql
//import javax.naming.*;
//import javax.servlet.http.*;
//import java.io.*;
import java.util.*;
/**
 *
 * @author John Kielkopf
 */
public class UserAccess {
    
    public Long id;
    public String name;
    public Integer hierarchy_level = -1;
    public Boolean use_commission = false;
    public Boolean accounting_app = false;
    public Boolean bind_to_club = false;
    public Boolean view_invoices = false;
    public Boolean manage_clubs = false;
    public Boolean manage_invoices = false;
    public Boolean manage_users = false;
    public Boolean manage_settings = false;
    public Boolean manage_announcements = false;
    public Boolean export_relay = false; // Allow javascript to post data to API, and return document from posted data.  A simple download relay.
    
    public Boolean single_use_key = false; // Allow user to request a single use access key
    
    public Boolean disabled = false;
    public Long updated;
    
    /* club only access levels ( no rows in v5.user_access -- only used by club memebrs / proshop / admin, etc. ) */
    public Boolean manager_portal = false; // Used by club memebrs/admins ONLY
    public Boolean view_member_reservations = false; // Used by club memebrs/admins ONLY
    public Boolean view_foretees_announcements = false;
    
    
    public String last_error;
    
    
    // User Access
    private final static String sql_select = ""
            + "SELECT *, "
            + "    (0) AS user_disabled, (0) AS user_id " // Lets us use the same method for loading user_access from user result set or result set from this query.
            + "  FROM user_access ";
    private final static String sql_order = " ORDER BY name ";
    private final static String sql_all = sql_select + sql_order;
    private final static String sql_by_id = sql_select + "  WHERE id = ? " + sql_order;
    
    
    public UserAccess(){}; // Empty parm
    
    public UserAccess(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public UserAccess(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public UserAccess(long id){
        Connection con_ft = ApiCommon.getConnection();
        loadById(id, con_ft);
        Connect.close(con_ft);
    }
    
    public final boolean loadById(long id, Connection con_ft){
        
        boolean result = false;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con_ft.prepareStatement(sql_by_id);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = loadFromResultSet(rs);
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
    
    
    private boolean loadFromResultSet(ResultSet rs){
        
        try {
            this.id = rs.getLong("id");
            this.name = rs.getString("name");
            this.disabled = rs.getBoolean("disabled");
            int user_id = rs.getInt("user_id");
            if(user_id == 0 || (!disabled && !rs.getBoolean("user_disabled"))){ // Dont load access levels if user record is disabled
                this.hierarchy_level = rs.getInt("hierarchy_level");
                this.use_commission = rs.getBoolean("use_commission");
                this.accounting_app = rs.getBoolean("accounting_app");
                this.bind_to_club = rs.getBoolean("bind_to_club");
                this.view_invoices = rs.getBoolean("view_invoices");
                this.manage_clubs = rs.getBoolean("manage_clubs");
                this.manage_invoices = rs.getBoolean("manage_invoices");
                this.export_relay = rs.getBoolean("export_relay");
                this.manage_users = rs.getBoolean("manage_users");
                this.manage_settings = rs.getBoolean("manage_settings");
                this.manage_announcements = rs.getBoolean("manage_announcements");
            }
            this.updated = rs.getTimestamp("updated").getTime();
        } catch(Exception e) {
            last_error = String.format(ApiConstants.error_resultset, classNameLower(), e.toString());
            Connect.logError(className()+".loadFromResultSet: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            return false;
        }
        return true;
        
    }
    
    public final long save(){
        Long result = null;
        Connection con_ft = ApiCommon.getConnection();
        result = save(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final Long save(Connection con_ft){
        Long result = null;
        
        PreparedStatement pstmt = null;
        
        if(id == null){
            // Inserting new record
            try {
                pstmt = con_ft.prepareStatement(""
                        + "INSERT INTO users "
                        + "  (name, hierarchy_level, "
                        + "    accounting_app, bind_to_club, view_invoices,"
                        + "    use_commission, manage_clubs, manage_invoices, manage_users, "
                        + "    manage_settings, manage_announcements, disabled) "
                        + "  VALUES"
                        + "   (?, ?, "
                        + "    ?, ?, ?,"
                        + "    ?, ?, ?, ?, "
                        + "    ?, ?, ?)");
                int i = 1;
                pstmt.setString(i++, name);
                pstmt.setInt(i++, hierarchy_level);
                pstmt.setBoolean(i++, use_commission);
                pstmt.setBoolean(i++, accounting_app);
                pstmt.setBoolean(i++, bind_to_club);
                pstmt.setBoolean(i++, view_invoices);
                pstmt.setBoolean(i++, manage_clubs);
                pstmt.setBoolean(i++, manage_invoices);
                pstmt.setBoolean(i++, manage_users);
                pstmt.setBoolean(i++, manage_settings);
                pstmt.setBoolean(i++, manage_announcements);
                pstmt.setBoolean(i++, disabled);
                pstmt.executeUpdate();
                
                result = Connect.getLastInsertId(con_ft);
                
            } catch(Exception e) {
                Connect.logError(className()+".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                last_error = String.format(ApiConstants.error_insert, classNameLower(), e.toString());
            } finally {
                Connect.close(pstmt);
            }
        } else {
            // Update existing record
            try {
                pstmt = con_ft.prepareStatement(""
                        + "UPDATE users "
                        + " SET"
                        + "  name = ?,"
                        + "  hierarchy_level = ?, "
                        + "  use_commission = ?, "
                        + "  accounting_app = ?, "
                        + "  bind_to_club = ?, "
                        + "  view_invoices = ?, "
                        + "  manage_clubs = ?, "
                        + "  manage_invoices = ?, "
                        + "  manage_users = ?, "
                        + "  manage_settings = ?, "
                        + "  manage_announcements = ?, "
                        + "  disabled = ?"
                        + " WHERE id = ?");
                int i = 1;
                pstmt.setString(i++, name);
                pstmt.setInt(i++, hierarchy_level);
                pstmt.setBoolean(i++, use_commission);
                pstmt.setBoolean(i++, accounting_app);
                pstmt.setBoolean(i++, bind_to_club);
                pstmt.setBoolean(i++, view_invoices);
                pstmt.setBoolean(i++, manage_clubs);
                pstmt.setBoolean(i++, manage_invoices);
                pstmt.setBoolean(i++, manage_users);
                pstmt.setBoolean(i++, manage_settings);
                pstmt.setBoolean(i++, manage_announcements);
                pstmt.setBoolean(i++, disabled);
                
                pstmt.setLong(i++, id);
                
                pstmt.executeUpdate();
                
                result = id;
                
            } catch(Exception e) {
                Connect.logError(className()+".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                last_error = String.format(ApiConstants.error_update, classNameLower(), e.toString());
            } finally {
                Connect.close(pstmt);
            }
        }

        return result;
    }
    
    
    public static List<UserAccess> getList(){
        return getList(null);
    }
    
    public static List<UserAccess> getList(Long id){
        
        List<UserAccess> result = new ArrayList<UserAccess>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_ft = ApiCommon.getConnection();
        
        String error = null;
        
        UserAccess r = new UserAccess();
        
        try {
            if(id != null){
                pstmt = con_ft.prepareStatement(sql_by_id);
                pstmt.setLong(1, id);
            } else {
                pstmt = con_ft.prepareStatement(sql_all);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new UserAccess(rs);
                if(r.last_error == null){
                    result.add(r);
                } else {
                    if (r.last_error != null) {
                        error = r.last_error;
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
