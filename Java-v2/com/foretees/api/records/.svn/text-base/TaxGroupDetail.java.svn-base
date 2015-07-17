/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.records;

import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.Connect;
import java.sql.*;          // mysql
//import java.util.UUID;
//import javax.naming.*;
//import javax.servlet.http.*;
//import java.io.*;
import java.util.*;
/**
 *
 * @author John Kielkopf
 */
public class TaxGroupDetail {
    
    public Long id;
    public Long tax_group_id;
    public Long tax_rate_id;
    public Long updated;
    
    public String name; // Read only -- used to simplify error responses
    
    public String last_error;
    
    
    // Tax Group Details
    private final static String sql_select = ""
            + "SELECT tgd.*, tr.name "
            + "  FROM tax_group_details tgd "
            + "    LEFT JOIN tax_rates tr "
            + "      ON tr.id = tgd.tax_rate_id ";
    private final static String sql_order = " ORDER BY tr.name ";
    private final static String sql_all = sql_select + sql_order;
    private final static String sql_by_id = sql_select + "  WHERE tgd.id = ? " + sql_order;
    private final static String sql_by_group_id = sql_select + "  WHERE tgd.tax_group_id = ? " + sql_order;
    private final static String sql_by_binding_ids = sql_select + "  WHERE tgd.tax_group_id = ? AND tgd.tax_rate_id = ? " + sql_order;
    

    public TaxGroupDetail(){}; // Empty parm
    
    public TaxGroupDetail(Long id, Long tax_group_id, Long tax_rate_id){
        loadByValue(id, tax_group_id, tax_rate_id, null);
    }
    
    public TaxGroupDetail(Long id, Long tax_group_id, Long tax_rate_id, Long updated){
        loadByValue(id, tax_group_id, tax_rate_id, updated);
    }
    
    public TaxGroupDetail(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public TaxGroupDetail(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public TaxGroupDetail(long id){
        Connection con_ft = ApiCommon.getConnection();
        loadById(id, con_ft);
        Connect.close(con_ft);
    }
    
    public final void loadByValue(Long id, Long tax_group_id, Long tax_rate_id, Long updated){
        this.id = id;
        this.tax_group_id = tax_group_id;
        this.tax_rate_id = tax_rate_id;
        this.updated = updated;
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
    
    public final Long loadByBindingIds(Long tax_group_id, Long tax_rate_id, Connection con_ft){
        return loadByBindingIds(tax_group_id, tax_rate_id, con_ft, false);
    }
    
    public final Long loadByBindingIds(Long tax_group_id, Long tax_rate_id, Connection con_ft, boolean skip_loading){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con_ft.prepareStatement(sql_by_binding_ids);

            pstmt.setLong(1, tax_group_id);
            pstmt.setLong(2, tax_rate_id);

            rs = pstmt.executeQuery();
            if(rs.next()){
                result = loadFromResultSet(rs, skip_loading);
            } else {
                last_error = "Unable to find "+classNameLower()+": with binding ids " + tax_group_id + "/" + tax_rate_id;
            }  
        } catch(Exception e) {
            Connect.logError(className()+".loadByBindingIds: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = "Error Loading "+classNameLower()+": " + e.toString();
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
                this.name = rs.getString("name"); // we'll get the name for error reporting
            } else {
                this.id = rs.getLong("id");
                this.tax_group_id = rs.getLong("tax_group_id");
                this.tax_rate_id = rs.getLong("tax_rate_id");
                this.updated = rs.getTimestamp("updated").getTime();
                this.name = rs.getString("name");
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
        
        if(tax_rate_id == null || tax_group_id == null){
            last_error = last_error = String.format(ApiConstants.error_update_detail_no_id, classNameLower(), "");
        } else if(id == null){
            // Inserting new record
            try {
                pstmt = con_ft.prepareStatement(""
                    + "INSERT INTO tax_group_details "
                    + "  (tax_group_id, tax_rate_id) "
                    + "  VALUES"
                    + "  (?, ?)");

                int i = 1;
                pstmt.setFloat(i++, tax_group_id);
                pstmt.setFloat(i++, tax_rate_id);
                pstmt.executeUpdate();
                
                result = Connect.getLastInsertId(con_ft);
                
                id = result;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                if(loadByBindingIds(tax_group_id, tax_rate_id, con_ft, true) != null){
                    last_error = String.format(ApiConstants.error_update_duplicate_name, classNameLower(), name.toString());
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
                    + "UPDATE tax_group_details "
                    + " SET"
                    + "  tax_group_id = ?, "
                    + "  tax_rate_id = ? "
                    + " WHERE id = ? ");
                
                int i = 1;
                pstmt.setFloat(i++, tax_group_id);
                pstmt.setFloat(i++, tax_rate_id);
                pstmt.setLong(i++, id);
                pstmt.executeUpdate();
                
                result = id;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                Long check_id = loadByBindingIds(tax_group_id, tax_rate_id, con_ft, true);
                if(check_id != null && check_id.equals(id)){
                    last_error = String.format(ApiConstants.error_update_duplicate_name, classNameLower(), name.toString());
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
                        + "DELETE FROM tax_group_details "
                        + " WHERE id = ? ");
                
                int i = 1;
                pstmt.setLong(i++, id);
                
                pstmt.executeUpdate();
                
                result = true;
                
            } catch(SQLException e) {
                if(ApiCommon.isValidationConstraint(e)){
                    last_error = String.format(ApiConstants.error_delete_bound, classNameLower(), e.toString());
                } else {
                    Connect.logError(className()+".delete: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_delete, classNameLower(), e.toString());
                }
            } finally {
                Connect.close(pstmt);
            }
        }

        return result;
    }
    
       
    public static List<TaxGroupDetail> getList(){
        return getList(null, null);
    }
    
    public static List<TaxGroupDetail> getList(Long id){
        return getList(id, null);
    }
    
    public static List<TaxGroupDetail> getListByGroupId(Long group_id){
        return getList(null, group_id);
    }
    
    private static List<TaxGroupDetail> getList(Long id, Long group_id){
        
        List<TaxGroupDetail> result = new ArrayList<TaxGroupDetail>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_ft = ApiCommon.getConnection();
        
        String error = null;
        
        TaxGroupDetail r = new TaxGroupDetail();
        
        try {
            if(id != null){
                pstmt = con_ft.prepareStatement(sql_by_id);
                pstmt.setLong(1, id);
            } else if(group_id != null){
                pstmt = con_ft.prepareStatement(sql_by_group_id);
                pstmt.setLong(1, group_id);
            } else {
                pstmt = con_ft.prepareStatement(sql_all);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new TaxGroupDetail(rs);
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
            Connect.logError(r.className() + error);
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
