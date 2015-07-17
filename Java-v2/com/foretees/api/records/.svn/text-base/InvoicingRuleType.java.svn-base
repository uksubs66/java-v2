/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.records;

import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.ArrayUtil;
import com.foretees.common.Connect;
import com.foretees.common.reqUtil;
import java.sql.*;          // mysql
//import java.util.UUID;
//import javax.naming.*;
//import javax.servlet.http.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author John Kielkopf
 */
public class InvoicingRuleType {
    
    public Long id;
    public String name;
    public Boolean count_members;
    public Boolean count_adults;
    public Boolean count_courts;
    public Boolean count_dining;
    public Boolean disabled;
    public Long updated;
    
    public String last_error;
    
    
    // Club Invoicing Rule Type
    private final static String sql_select = ""
            + "SELECT * "
            + "  FROM invoicing_rule_types ";
    private final static String sql_order = " ORDER BY id ";
    private final static String sql_all = sql_select + sql_order;
    private final static String sql_by_id = sql_select + "  WHERE id = ? " + sql_order;
    private final static String sql_by_name = sql_select + "  WHERE name = ? " + sql_order;


    public InvoicingRuleType(){}; // Empty parm
    
    public InvoicingRuleType(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public InvoicingRuleType(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public InvoicingRuleType(long id){
        Connection con_ft = ApiCommon.getConnection();
        loadById(id, con_ft);
        Connect.close(con_ft);
    }
    
    public final boolean compare(InvoicingRuleType compare){
        boolean result = true;
        result = count_members != compare.count_members?false:result;
        result = count_adults != compare.count_adults?false:result;
        result = count_courts != compare.count_courts?false:result;
        result = count_dining != compare.count_dining?false:result;
        return result;
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
                this.count_members = rs.getBoolean("count_members");
                this.count_adults = rs.getBoolean("count_adults");
                this.count_courts = rs.getBoolean("count_courts");
                this.count_dining = rs.getBoolean("count_dining");
                this.disabled = rs.getBoolean("disabled");
                this.updated = rs.getTimestamp("updated").getTime();
                
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
        
        if(name == null || name.trim().isEmpty()){
            last_error = ApiConstants.error_empty_name;
        } else if(id == null){
            // Inserting new record
            try {
                pstmt = con_ft.prepareStatement(""
                        + "INSERT INTO invoicing_rule_types "
                        + "  (count_members, count_adults, count_courts, count_dining, disabled) "
                        + "  VALUES"
                        + "  (?, ?, ?, ?, ?)");
                int i = 1;
                pstmt.setBoolean(i++, count_members);
                pstmt.setBoolean(i++, count_adults);
                pstmt.setBoolean(i++, count_courts);
                pstmt.setBoolean(i++, count_dining);
                pstmt.setBoolean(i++, disabled);
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
                        + "UPDATE invoicing_rule_types "
                        + " SET"
                        + "  count_members = ?,"
                        + "  count_adults = ?,"
                        + "  count_courts = ?, "
                        + "  count_dining = ?, "
                        + "  disabled = ?, "
                        + " WHERE id = ?");
                int i = 1;
                pstmt.setBoolean(i++, count_members);
                pstmt.setBoolean(i++, count_adults);
                pstmt.setBoolean(i++, count_courts);
                pstmt.setBoolean(i++, count_dining);
                pstmt.setBoolean(i++, disabled);
                
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
                        + "DELETE FROM invoicing_rule_types "
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
    
    public final int getCount(Long club_id, List<MemberType> memberTypes, List<MembershipType> membershipTypes){
        int result = 0;
        
        if(count_members){
            
        }
        
        return result;
    }
    
    
    public static List<InvoicingRuleType> getList(){
        return getList(null);
    }
    
    public static List<InvoicingRuleType> getList(Long id){
        
        List<InvoicingRuleType> result = new ArrayList<InvoicingRuleType>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_ft = ApiCommon.getConnection();
        
        String error = null;
        
        InvoicingRuleType r = new InvoicingRuleType();
        
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
                r = new InvoicingRuleType(rs);
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
