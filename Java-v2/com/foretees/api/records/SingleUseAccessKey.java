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
import java.sql.*;          // mysql
import java.util.UUID;
//import javax.naming.*;
import com.google.gson.*; // for json
//import com.google.gson.reflect.*; // for json
/**
 *
 * @author John Kielkopf
 */
public class SingleUseAccessKey {
    
    public Long id;
    public String access_key;
    public User user;

    public Long updated;
    
    public String last_error;
    
    
    // SQL
    private final static String sql_select = ""
            + "SELECT * "
            + "  FROM single_use_access_keys sk ";
    private final static String sql_order = " ORDER BY sk.updated ";
    
    private final static String sql_all = sql_select + sql_order;
    private final static String sql_by_id = sql_select + "  WHERE sk.id = ? " + sql_order;
    private final static String sql_by_access_key = sql_select + "  WHERE sk.access_key = ? " + sql_order;
    

    public SingleUseAccessKey(){}; // Empty parm
    
    public SingleUseAccessKey(User user){
        this.user = user;
        this.access_key = UUID.randomUUID().toString();
        this.save();
    };
    
    public SingleUseAccessKey(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public SingleUseAccessKey(long id){
        Connection con_ft = ApiCommon.getConnection();
        loadById(id, con_ft);
        Connect.close(con_ft);
    }
    
    public SingleUseAccessKey(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public SingleUseAccessKey(String access_key){
        Connection con_ft = ApiCommon.getConnection();
        loadByAccessKey(access_key, con_ft);
        Connect.close(con_ft);
    }
    
    public SingleUseAccessKey(String access_key, Connection con_ft){
        loadByAccessKey(access_key, con_ft);
    }
    
    public final Long loadById(long id, Connection con_ft){
        
        Long result = null;

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
    
    public final Long loadByAccessKey(String access_key, Connection con_ft){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con_ft.prepareStatement(sql_by_access_key);
            pstmt.setString(1, access_key);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = loadFromResultSet(rs);
                if(user != null){
                    // Re-load user data to make sure we have the latest credentials.
                    if(user.id != null){
                        // This is a fortees global user
                        user = new User(user.id, con_ft);
                    } else if(user.club_id != null && user.member_id != null){
                        // This is a foretees club member
                        user = User.getMemberUser(user.club_id, user.member_id);
                    } else if(user.club_id != null){
                        // Probably a club proshop or other useer that doesn't really have any type of record?
                        // For now we'll just return the user record as stored in the SUAK record.  These types of users really need some kind of database record.
                    } else {
                        // ?? shouldn't be able to get here ??
                        //  Probably should log an error if we get here -- but we shouldn't ever get here.
                    }
                    if(user.last_error != null){
                        last_error = user.last_error;
                    }
                }
            } else {
                last_error = "Invalid Access Key: " + access_key;
            }
        } catch (Exception e) {
            Connect.logError(className() + ".loadByAccessKey: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = "Error Loading Access Key: " + e.toString();
        } finally {
            Connect.close(rs, pstmt);
        }

        
        return result;
        
    }
    
    private Long loadFromResultSet(ResultSet rs){
        return loadFromResultSet(rs, false);
    }
    
    private Long loadFromResultSet(ResultSet rs, boolean skip_loading){
        
        Gson gson = new Gson();
        
        Long result = null;
        try {
                this.id = rs.getLong("id");
                this.user = gson.fromJson(rs.getString("user_json"), User.class);
                this.access_key = rs.getString("access_key");
                this.updated = rs.getTimestamp("updated").getTime();
                
                if(this.user == null){
                    last_error = "Unable to load access key.  Invalid user record.";
                } else if(this.user.access == null){
                    last_error = "Unable to load access key.  Invalid user access record.";
                } else if (this.user.last_error != null){
                    last_error = this.user.last_error;
                } else if (this.user.access.last_error != null){
                    last_error = this.user.access.last_error;
                } else {
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
        
        Gson gson = new Gson();
        
        if(id == null){
            // Inserting new record
            try {
                    pstmt = con_ft.prepareStatement(""
                        + "INSERT INTO single_use_access_keys "
                        + "  (access_key, user_json) "
                        + "  VALUES"
                        + "  (?, ?)");
                
                int i = 1;
                pstmt.setString(i++, access_key);
                pstmt.setString(i++, gson.toJson(user));
                pstmt.executeUpdate();
                
                result = Connect.getLastInsertId(con_ft);
                
                id = result;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                SingleUseAccessKey test = new SingleUseAccessKey(access_key, con_ft);
                if(test.last_error == null){
                    last_error = String.format(ApiConstants.error_update_duplicate_name, classNameLower(), access_key);
                } else {
                    Connect.logError(className()+".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_insert, classNameLower(), e.toString());
                }
            } finally {
                Connect.close(pstmt);
            }
        } else {
            // Update existing record
            try {
                    pstmt = con_ft.prepareStatement(""
                        + "UPDATE single_use_access_keys "
                        + " SET"
                        + "  access_key = ?,"
                        + "  user_json = ?"
                        + " WHERE id = ? ");
                
                int i = 1;
                pstmt.setString(i++, access_key);
                pstmt.setString(i++, gson.toJson(user));
                pstmt.setLong(i++, id);
                
                pstmt.executeUpdate();
                
                result = id;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                SingleUseAccessKey test = new SingleUseAccessKey(access_key, con_ft);
                if(test.last_error == null && test.id != null && !test.id.equals(id)){
                    last_error = String.format(ApiConstants.error_update_duplicate_name, classNameLower(), access_key);
                } else {
                    Connect.logError(className()+".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_update, classNameLower(), e.toString());
                }
            } finally {
                Connect.close(pstmt);
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
            try {
                    pstmt = con_ft.prepareStatement(""
                        + "DELETE FROM single_use_access_keys "
                        + " WHERE id = ? ");
                
                int i = 1;
                pstmt.setLong(i++, id);
                
                pstmt.executeUpdate();
                
                result = true;
                
            } catch(Exception e) {
                Connect.logError(className()+".delete: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                last_error = String.format(ApiConstants.error_delete, classNameLower(), e.toString());
            } finally {
                Connect.close(pstmt);
            }
        }

        return result;
    }
    
    // Purge records older than 10 minutes
    public static boolean purge(){
        Connection con_ft = ApiCommon.getConnection();
        boolean result = purge(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static boolean purge(Connection con_ft){
        boolean result = false;
        
        PreparedStatement pstmt = null;
        // Update existing record
        try {
                pstmt = con_ft.prepareStatement(""
                    + "DELETE FROM single_use_access_keys "
                    + " WHERE updated < (NOW() - INTERVAL 10 MINUTE) ");

            int i = 1;

            pstmt.executeUpdate();

            result = true;

        } catch(Exception e) {
            Connect.logError(new SingleUseAccessKey().className()+".delete: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(pstmt);
        }

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
