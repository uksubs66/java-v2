/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.records;

import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.ArrayUtil;
import com.foretees.common.Connect;
import com.foretees.common.ProcessConstants;
import com.foretees.common.reqUtil;
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
public class ClubSetting {
    
    public Long id;
    public String name;
    public Integer int_value;
    public Float float_value;
    public String text_value;
    //public Boolean disabled;
    
    public String last_error;
    
    public Long updated;
    
    
    // Club Invoice Terms
    private final static String sql_select = ""
            // Get club settings, with some default information
            + "(SELECT cs.id, cs.text_value, cs.float_value, cs.int_value, cs.description, "
            + "    COALESCE(cs.data_model, dcs.data_model) AS data_model, COALESCE(cs.allow_club_mod, dcs.allow_club_mod) AS allow_club_mod, "
            + "    dcs.text_value AS default_text_value, dcs.text_value AS default_float_value, dcs.text_value AS default_int_value, dcs.description AS default_description  "
            + "  FROM club_settings AS cs "
            + "    LEFT JOIN "+ProcessConstants.REV+".default_club_settings AS dcs "
            + "      ON dcs.name = cs.name) "
            // Get defaults incase there is no club settings
            + " UNION "
            + "(SELECT NULL as id, dcs.text_value, dcs.float_value, dcs.int_value, dcs.description, "
            + "    dcs.data_model, dcs.allow_club_mod, "
            + "    dcs.text_value AS default_text_value, dcs.text_value AS default_float_value, dcs.text_value AS default_int_value, dcs.description AS default_description  "
            + "  FROM club_settings AS cs "
            + "    LEFT JOIN "+ProcessConstants.REV+".default_club_settings AS dcs "
            + "      ON dcs.name = cs.name) ";
    private final static String sql_order = " ORDER BY name ";
    private final static String sql_all = sql_select + sql_order;
    private final static String sql_by_id = sql_select + "  WHERE id = ? " + sql_order;
    private final static String sql_by_name = sql_select + "  WHERE name = ? " + sql_order;
    

    public ClubSetting(){}; // Empty parm
    
    public ClubSetting(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public ClubSetting(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public ClubSetting(long id){
        Connection con_ft = ApiCommon.getConnection();
        loadById(id, con_ft);
        Connect.close(con_ft);
    }
    
    public ClubSetting(String name, Connection con_ft){
        loadByName(name, con_ft);
    }
    
    public ClubSetting(String name){
        Connection con_ft = ApiCommon.getConnection();
        loadByName(name, con_ft);
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
    
    public final Long loadByName(String name, Connection con_ft){
        return loadByName(name, con_ft, false);
    }
    
    public final Long loadByName(String name, Connection con_ft, boolean skip_loading){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con_ft.prepareStatement(sql_by_name);
            pstmt.setString(1, name.trim());

            rs = pstmt.executeQuery();
            if(rs.next()){
                result = loadFromResultSet(rs, skip_loading);
            } else {
                last_error = String.format(ApiConstants.error_finding_by_name, classNameLower(), name);
            }  
        } catch(Exception e) {
            Connect.logError(className()+".loadByName: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = String.format(ApiConstants.error_loading_by_name, classNameLower(), e.toString());
        } finally {
            Connect.close(rs, pstmt);
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
                result = rs.getLong("id");
            } else {
                this.id = rs.getLong("id");
                this.name = rs.getString("name");
                this.int_value = rs.getInt("int_value");
                this.float_value = rs.getFloat("float_value");
                this.text_value = rs.getString("text_value");
                //this.disabled = rs.getBoolean("disabled");
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
        
        // Clean up setting name
        name = name.toLowerCase().trim()
                .replaceAll("^[^1-9a-zA-Z]+", "")
                .replaceAll("[^1-9a-zA-Z]+$", "")
                .replaceAll("[^1-9a-zA-Z]+", "_");
        
        if(name == null || name.trim().isEmpty()){
            last_error = ApiConstants.error_empty_name;
        } else if(id == null){
            // Inserting new record
            try {
                pstmt = con_ft.prepareStatement(""
                    + "INSERT INTO settings "
                    + "  (name, int_value, float_value, text_value) "
                    + "  VALUES"
                    + "  (?, ?, ?, ?)");

                int i = 1;
                pstmt.setString(i++, name.trim());
                ApiCommon.setOrNull(i++, pstmt, int_value);
                ApiCommon.setOrNull(i++, pstmt, float_value);
                ApiCommon.setOrNull(i++, pstmt, text_value);
                pstmt.executeUpdate();
                
                result = Connect.getLastInsertId(con_ft);
                
                id = result;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                if(loadByName(name, con_ft, true) != null){
                    last_error = String.format(ApiConstants.error_update_duplicate_name, classNameLower(), name);
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
                        + "UPDATE settings "
                        + " SET"
                        + "  name = ?,"
                        + "  int_value = ?,"
                        + "  float_value = ?, "
                        + "  text_value = ? "
                        + " WHERE id = ? ");
                
                int i = 1;
                pstmt.setString(i++, name.trim());
                ApiCommon.setOrNull(i++, pstmt, int_value);
                ApiCommon.setOrNull(i++, pstmt, float_value);
                ApiCommon.setOrNull(i++, pstmt, text_value);
                pstmt.setLong(i++, id);
                
                pstmt.executeUpdate();
                
                result = id;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                Long check_id = loadByName(name, con_ft, true);
                if(check_id != null && check_id.equals(id)){
                    last_error = String.format(ApiConstants.error_update_duplicate_name, classNameLower(), name);
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
                        + "DELETE FROM settings "
                        + " WHERE id = ? ");
                
                int i = 1;
                pstmt.setLong(i++, id);
                
                pstmt.executeUpdate();
                
                result = true;
                
            } catch(SQLException e) {
                if(ApiCommon.isValidationConstraint(e)){
                    last_error = String.format(ApiConstants.error_delete_bound, classNameLower(), e.toString());
                } else {
                    Connect.logError(className()+".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_delete, classNameLower(), e.toString());
                }
            } finally {
                Connect.close(pstmt);
            }
        }

        return result;
    }
    
    public static String getTextValue(String name, String default_value) {
        return getTextValue(name, default_value, false);
    }
    
    public static String getTextValue(String name, String default_value, Connection con_ft) {
        return getTextValue(name, default_value, false, con_ft);
    }
    
    public static String getTextValue(String name, String default_value, boolean log_default) {
        Connection con_ft = ApiCommon.getConnection();
        String result = getTextValue(name, default_value, log_default, con_ft);
        Connect.close(con_ft);
        return result;
    }
 
    public static String getTextValue(String name, String default_value, boolean log_default, Connection con_ft) {
        String result;
        ClubSetting setting = new ClubSetting(name, con_ft);
        if (setting.last_error != null || setting.text_value == null || setting.text_value.trim().isEmpty()) {
            result = default_value;
            if (log_default) {
                String error;
                if (setting.last_error != null) {
                    error = setting.last_error;
                } else {
                    error = "text_value is null or empty";
                }
                Connect.logError(setting.className() + ".getTextValue: Err=Unable to load setting: " + error);
            }
        } else {
            result = setting.text_value;
        }
        return result;
    }
    
    public static Float getFloatValue(String name, Float default_value) {
        return getFloatValue(name, default_value, false);
    }
    
    public static Float getFloatValue(String name, Float default_value, Connection con_ft) {
        return getFloatValue(name, default_value, false, con_ft);
    }
    
    public static Float getFloatValue(String name, Float default_value, boolean log_default) {
        Connection con_ft = ApiCommon.getConnection();
        Float result = getFloatValue(name, default_value, log_default, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static Float getFloatValue(String name, Float default_value, boolean log_default, Connection con_ft) {
        Float result;
        ClubSetting setting = new ClubSetting(name, con_ft);
        if (setting.last_error != null || setting.float_value == null) {
            result = default_value;
            if (log_default) {
                String error;
                if (setting.last_error != null) {
                    error = setting.last_error;
                } else {
                    error = "float_value is null";
                }
                Connect.logError(setting.className() + ".getFloatValue: Err=Unable to load setting: " + error);
            }
        } else {
            result = setting.float_value;
        }
        return result;
    }
    
    public static Integer getIntegerValue(String name, Integer default_value) {
        return getIntegerValue(name, default_value, false);
    }
    
    public static Integer getIntegerValue(String name, Integer default_value, Connection con_ft) {
        return getIntegerValue(name, default_value, false, con_ft);
    }
    
    public static Integer getIntegerValue(String name, Integer default_value, boolean log_default) {
        Connection con_ft = ApiCommon.getConnection();
        Integer result = getIntegerValue(name, default_value, log_default, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static Integer getIntegerValue(String name, Integer default_value, boolean log_default, Connection con_ft) {
        Integer result;
        ClubSetting setting = new ClubSetting(name, con_ft);
        if (setting.last_error != null || setting.int_value == null) {
            result = default_value;
            if (log_default) {
                String error;
                if (setting.last_error != null) {
                    error = setting.last_error;
                } else {
                    error = "int_value is null";
                }
                Connect.logError(setting.className() + ".getIntegerValue: Err=Unable to load setting: " + error);
            }
        } else {
            result = setting.int_value;
        }
        return result;
    }
    
    public static List<ClubSetting> getList(){
        return getList(null);
    }
    
    public static List<ClubSetting> getList(Long id){
        
        List<ClubSetting> result = new ArrayList<ClubSetting>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_ft = ApiCommon.getConnection();
        
        String error = null;
        
        ClubSetting r = new ClubSetting();
        
        try {
            int i = 1;
            if(id != null){
                pstmt = con_ft.prepareStatement(sql_by_id);
                pstmt.setLong(i++, id);
            } else {
                pstmt = con_ft.prepareStatement(sql_all);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new ClubSetting(rs);
                if(r.id != null && r.last_error == null){
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
