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
public class TaxGroup {
    
    public Long id;
    public String name;
    public Float group_rate;
    public Boolean disabled;
    public Long updated;
    
    public List<TaxRate> tax_rates = new ArrayList<TaxRate>();
    
    public String last_error;
    
    
    // Tax Group
    private final static String sql_select = ""
            + "SELECT tg.*, IFNULL(SUM(tr.rate),0) AS group_rate, "
            + "    GROUP_CONCAT(tr.id ORDER BY tr.name SEPARATOR ',') AS tax_rate_ids, "
            + "    GROUP_CONCAT(tgd.id ORDER BY tr.name SEPARATOR ',') AS tax_group_detail_ids, "
            + "    GROUP_CONCAT(CONCAT('\"',REPLACE(tr.name,'\"','\"\"'),'\"') ORDER BY tr.name SEPARATOR ',') AS tax_rate_names, "
            + "    GROUP_CONCAT(tr.rate ORDER BY tr.name SEPARATOR ',') AS tax_rate_rates, "
            + "    GROUP_CONCAT(tr.disabled ORDER BY tr.name SEPARATOR ',') AS tax_rate_disables, "
            + "    GROUP_CONCAT(UNIX_TIMESTAMP(tr.updated) ORDER BY tr.name SEPARATOR ',') AS tax_rate_updates "
            + "  FROM tax_groups tg "
            + "    LEFT JOIN tax_group_details tgd "
            + "      ON tgd.tax_group_id = tg.id "
            + "    LEFT JOIN tax_rates tr "
            + "      ON tr.id = tgd.tax_rate_id ";

    private final static String sql_aggregate = " GROUP BY tg.name, tg.id ";
    
    private final static String sql_all = sql_select + sql_aggregate;
    
    private final static String sql_by_id = sql_select + " WHERE tg.id = ? " + sql_aggregate;
    
    private final static String sql_by_name = sql_select + " WHERE tg.name = ? " + sql_aggregate;
 
    

    public TaxGroup(){}; // Empty parm
    
    public TaxGroup(Long id, String name, 
            boolean disabled){
        this.id = id;
        this.name = name;
        this.disabled = disabled;
    }
    
    public TaxGroup(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public TaxGroup(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public TaxGroup(long id){
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
                this.group_rate = rs.getFloat("group_rate");
                this.disabled = rs.getBoolean("disabled");
                this.updated = rs.getTimestamp("updated").getTime();
                
                result = id;
                
                tax_rates = new ArrayList<TaxRate>();
                
                if(rs.getString("tax_rate_ids") != null){
                    List<String> tax_rate_ids = ArrayUtil.parseCsvLine(new StringReader(rs.getString("tax_rate_ids")));
                    List<String> tax_group_detail_ids = ArrayUtil.parseCsvLine(new StringReader(rs.getString("tax_group_detail_ids")));
                    List<String> tax_rate_names = ArrayUtil.parseCsvLine(new StringReader(rs.getString("tax_rate_names")));
                    List<String> tax_rate_rates = ArrayUtil.parseCsvLine(new StringReader(rs.getString("tax_rate_rates")));
                    List<String> tax_rate_disables = ArrayUtil.parseCsvLine(new StringReader(rs.getString("tax_rate_disables")));
                    List<String> tax_rate_updates = ArrayUtil.parseCsvLine(new StringReader(rs.getString("tax_rate_updates")));

                    for(int i = 0; i < tax_rate_ids.size(); i++){
                        tax_rates.add(new TaxRate(
                                Long.parseLong(tax_rate_ids.get(i)),
                                id,
                                Long.parseLong(tax_group_detail_ids.get(i)),
                                tax_rate_names.get(i),
                                Float.parseFloat(tax_rate_rates.get(i)),
                                reqUtil.parseBoolean(tax_rate_disables.get(i), false),
                                Long.parseLong(tax_rate_updates.get(i))
                                ));
                    }
                }

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
                    + "INSERT INTO tax_groups "
                    + "  (name, disabled) "
                    + "  VALUES"
                    + "  (?, ?)");

                int i = 1;
                pstmt.setString(i++, name.trim());
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
                    + "UPDATE tax_groups "
                    + " SET"
                    + "  name = ?,"
                    + "  disabled = ? "
                    + " WHERE id = ? ");
                
                int i = 1;
                pstmt.setString(i++, name.trim());
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
                        + "DELETE FROM tax_groups "
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
    
    
       public static List<TaxGroup> getList(){
        return getList(null);
    }
    
    public static List<TaxGroup> getList(Long id){
        
        List<TaxGroup> result = new ArrayList<TaxGroup>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_ft = ApiCommon.getConnection();
        
        String error = null;
        
        TaxGroup r = new TaxGroup();
        
        try {
            if(id != null){
                pstmt = con_ft.prepareStatement(sql_by_id);
                pstmt.setLong(1, id);
            } else {
                pstmt = con_ft.prepareStatement(sql_all);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new TaxGroup(rs);
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
