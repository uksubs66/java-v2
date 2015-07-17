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
public class InvoiceItem {
    
    public Long id;
    public Long invoice_item_type_id;
    public Long invoicing_rule_type_id;
    public String name;
    public String sku;
    public Float rate;
    public Float minimum_qty;
    public Float maximum_qty;
    public String description;
    public Boolean count_before_min;
    public Boolean disabled;
    public Long updated;
    
    public Long default_tax_group_id; // read only
    public Float default_tax_rate; // read only
    
    public String invoice_item_type_name; // Read only -- used to simplify display
    public Long tax_group_id; // Read only -- used to simplify display
    
    public String last_error;
    
    
    // Invoice Item
    private final static String sql_select = ""
            + "SELECT ii.*, "
            + "    IFNULL(iitr.tax_group_id,1) AS default_tax_group_id, "
            + "    SUM(tr.rate) AS default_tax_rate, "
            + "    iit.name AS invoice_item_type_name "
            + "  FROM invoice_items AS ii "
            + "    LEFT JOIN invoice_item_types AS iit "
            + "      ON iit.id = ii.invoice_item_type_id"
            + "    LEFT JOIN invoice_item_type_tax_rates AS iitr "
            + "      ON iitr.invoice_item_type_id = iit.id "
            + "        AND iitr.club_invoicing_id = ? "
            + "    LEFT JOIN tax_groups AS tg "
            + "      ON tg.id = iitr.tax_group_id "
            + "        AND tg.disabled != 1 "
            + "    LEFT JOIN tax_group_details AS tgd "
            + "      ON tgd.tax_group_id = tg.id "
            + "    LEFT JOIN tax_rates AS tr "
            + "      ON tr.id = tgd.tax_rate_id "
            + "        AND tr.disabled != 1 ";
    private final static String sql_order = " GROUP BY iit.name, ii.name ";
    private final static String sql_all = sql_select + sql_order;
    private final static String sql_by_id = sql_select + "  WHERE ii.id = ? " + sql_order;
    private final static String sql_by_type_id = sql_select + "  WHERE ii.invoice_item_type_id = ? " + sql_order;
    private final static String sql_by_name = sql_select + "  WHERE ii.name = ? " + sql_order;
    private final static String sql_by_sku = sql_select + "  WHERE ii.sku = ? " + sql_order;
 

    public InvoiceItem(){}; // Empty parm
    /*
    public InvoiceItem(Long id, Long invoice_item_type_id, Long club_invoicing_rule_type_id, String name, Float rate, Float minimum_qty, Float maximum_qty, 
            String description, boolean disabled){
        loadByValue(id, invoice_item_type_id, club_invoicing_rule_type_id, name, rate, minimum_qty, maximum_qty, description, null, disabled, null);
    }
    
    public InvoiceItem(Long id, Long invoice_item_type_id, Long club_invoicing_rule_type_id, String name, Float rate, Float minimum_qty, Float maximum_qty, 
            String description, String invoice_item_type_name, boolean disabled, Long updated){
        loadByValue(id, invoice_item_type_id, club_invoicing_rule_type_id, name, rate, minimum_qty, maximum_qty, description, invoice_item_type_name, disabled, updated);
    }
    */
    public InvoiceItem(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public InvoiceItem(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public InvoiceItem(long id, long club_invoicing_id){
        loadById(id, club_invoicing_id);
    }
    
    public InvoiceItem(long id, long club_invoicing_id, Connection con_ft){
        loadById(id, club_invoicing_id, con_ft);
    }
    
    public InvoiceItem(long id){
        Connection con_ft = ApiCommon.getConnection();
        loadById(id, con_ft);
        Connect.close(con_ft);
    }
    /*
    public final void loadByValue(Long id, Long invoice_item_type_id, Long club_invoicing_rule_type_id, String name, Float rate, Float minimum_qty, Float maximum_qty, 
            String description, String invoice_item_type_name, boolean disabled, Long updated){
        this.id = id;
        this.invoice_item_type_id = invoice_item_type_id;
        this.invoicing_rule_type_id = club_invoicing_rule_type_id;
        this.name = name;
        this.rate = rate;
        this.minimum_qty = minimum_qty;
        this.maximum_qty = maximum_qty;
        this.description = description;
        this.disabled = disabled;
        this.updated = updated;
        this.invoice_item_type_name = invoice_item_type_name;
    }
    */
    public final Long loadById(long id, Connection con_ft){
        return loadById(id, null, con_ft, false);
    }
    
    public final Long loadById(long id, Connection con_ft, boolean skip_loading){
        return loadById(id, null, con_ft, false);
    }
    
    public final Long loadById(long id, long club_invoicing_id){
        Long result = null;
        Connection con_ft = ApiCommon.getConnection();
        result = loadById(id, club_invoicing_id, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final Long loadById(long id, Long club_invoicing_id, Connection con_ft){
        return loadById(id, club_invoicing_id, con_ft, false);
    }
    
    public final Long loadById(long id, Long club_invoicing_id, Connection con_ft, boolean skip_loading){
        
        Long result = null;
        
        if(club_invoicing_id == null){
            club_invoicing_id = (long)0; // if not getting club specific data, then 0 will stop club data
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int i = 1;
        try {
            pstmt = con_ft.prepareStatement(sql_by_id);
            pstmt.setLong(i++, club_invoicing_id);
            pstmt.setLong(i++, id);
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
            pstmt.setLong(1, (long)0); // club invoicing id
            pstmt.setString(2, name.trim());

            rs = pstmt.executeQuery();
            if(rs.next()){
                result = loadFromResultSet(rs, skip_loading);
            } else {
                last_error = "Unable to find "+classNameLower()+": " + name;
            }  
        } catch(Exception e) {
            Connect.logError(className()+".loadByName: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
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
            } else {
                this.id = rs.getLong("id");
                this.invoice_item_type_id = rs.getLong("invoice_item_type_id");
                this.invoicing_rule_type_id = rs.getLong("invoicing_rule_type_id");
                this.name = rs.getString("name");
                this.rate = rs.getFloat("rate");
                this.minimum_qty = rs.getFloat("minimum_qty");
                this.maximum_qty = rs.getFloat("maximum_qty");
                this.description = rs.getString("description");
                this.invoice_item_type_name = rs.getString("invoice_item_type_name");
                this.count_before_min = rs.getBoolean("count_before_min");
                this.disabled = rs.getBoolean("disabled");
                this.updated = rs.getTimestamp("updated").getTime();
                
                this.default_tax_group_id = rs.getLong("default_tax_group_id");
                this.default_tax_rate = rs.getFloat("default_tax_rate");
                
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
                        + "INSERT INTO invoice_items "
                        + "  (invoice_item_type_id, invoicing_rule_type_id, name, rate, "
                        + "minimum_qty, maximum_qty, description, count_before_min, disabled) "
                        + "  VALUES"
                        + "  (?, ?, ?, ?, "
                        + "?, ?, ?, ?, ?)");

                int i = 1;
                pstmt.setLong(i++, invoice_item_type_id);
                pstmt.setLong(i++, invoicing_rule_type_id);
                pstmt.setString(i++, name.trim());
                pstmt.setFloat(i++, rate);
                pstmt.setFloat(i++, minimum_qty);
                pstmt.setFloat(i++, maximum_qty);
                pstmt.setString(i++, description.trim());
                pstmt.setBoolean(i++, count_before_min);
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
                        + "UPDATE invoice_items "
                        + " SET"
                        + "  invoice_item_type_id = ?,"
                        + "  invoicing_rule_type_id = ?, "
                        + "  name = ?,"
                        + "  rate = ?, "
                        + "  minimum_qty = ?, "
                        + "  maximum_qty = ?, "
                        + "  description = ?, "
                        + "  count_before_min = ?, "
                        + "  disabled = ? "
                        + " WHERE id = ? ");

                int i = 1;
                pstmt.setLong(i++, invoice_item_type_id);
                pstmt.setLong(i++, invoicing_rule_type_id);
                pstmt.setString(i++, name.trim());
                pstmt.setFloat(i++, rate);
                pstmt.setFloat(i++, minimum_qty);
                pstmt.setFloat(i++, maximum_qty);
                pstmt.setString(i++, description.trim());
                pstmt.setBoolean(i++, count_before_min);
                pstmt.setBoolean(i++, disabled);
                pstmt.setLong(i++, id);
                
                pstmt.executeUpdate();
                
                result = id;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                Long check_id = loadByName(name, con_ft, true);
                if(check_id != null && !check_id.equals(id)){
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
                        + "DELETE FROM invoice_items "
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
    
    
    public static List<InvoiceItem> getList(){
        return getList(null, null, null);
    }
    
    public static List<InvoiceItem> getList(Long id){
        return getList(id, null, null);
    }
    
    public static List<InvoiceItem> getList(Long id, Long club_invoicing_id){
        return getList(id, null, club_invoicing_id);
    }
    
    public static List<InvoiceItem> getListByTypeId(Long type_id){
        return getList(null, type_id, null);
    }
    
    public static List<InvoiceItem> getListByTypeId(Long type_id, Long club_invoicing_id){
        return getList(null, type_id, club_invoicing_id);
    }
    
    public static List<InvoiceItem> getListByClubInvoicingId(Long club_invoicing_id){
        return getList(null, null, club_invoicing_id);
    }

    private static List<InvoiceItem> getList(Long id, Long type_id, Long club_invoicing_id){
        
        List<InvoiceItem> result = new ArrayList<InvoiceItem>();
        
        if(club_invoicing_id == null){
            club_invoicing_id = (long)0; // if not getting club specific data, then 0 will stop club data
        }
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_ft = ApiCommon.getConnection();
        
        String error = null;
        
        InvoiceItem r = new InvoiceItem();
        
        try {
            int i = 1;
            if(id != null){
                pstmt = con_ft.prepareStatement(sql_by_id);
                pstmt.setLong(i++, club_invoicing_id);
                pstmt.setLong(i++, id);
            } else if(type_id != null){
                pstmt = con_ft.prepareStatement(sql_by_type_id);
                pstmt.setLong(i++, club_invoicing_id);
                pstmt.setLong(i++, type_id);
            } else {
                pstmt = con_ft.prepareStatement(sql_all);
                pstmt.setLong(i++, club_invoicing_id);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new InvoiceItem(rs);
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
