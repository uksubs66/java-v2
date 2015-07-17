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
public class InvoiceItemType {
    
    public Long id;
    public String name;
    public Boolean disabled;
    public Long updated;
    
    public Long tax_group_id; // Read only -- used to simplify display
    
    public List<InvoiceItem> invoice_items = new ArrayList<InvoiceItem>();
    
    public String last_error;
    
    
    // Invoice Item Type
    private final static String sql_select = ""
            + "SELECT iit.*, iitr.tax_group_id, "
            + "    GROUP_CONCAT(ii.id ORDER BY ii.name SEPARATOR ',') AS invoice_item_ids, "
            + "    GROUP_CONCAT(ii.invoicing_rule_type_id ORDER BY ii.name SEPARATOR ',') AS invoice_item_rule_type_ids, "
            + "    GROUP_CONCAT(CONCAT('\"',REPLACE(ii.name,'\"','\"\"'),'\"') ORDER BY ii.name SEPARATOR ',') as invoice_item_names, "
            + "    GROUP_CONCAT(CONCAT('\"',REPLACE(ii.description,'\"','\"\"'),'\"') ORDER BY ii.name SEPARATOR ',') as invoice_item_descriptions, "
            + "    GROUP_CONCAT(ii.rate ORDER BY ii.name SEPARATOR ',') AS invoice_item_rates, "
            + "    GROUP_CONCAT(ii.minimum_qty ORDER BY ii.name SEPARATOR ',') AS invoice_item_minimum_qtys, "
            + "    GROUP_CONCAT(ii.maximum_qty ORDER BY ii.name SEPARATOR ',') AS invoice_item_maximum_qtys, "
            + "    GROUP_CONCAT(ii.disabled ORDER BY ii.name SEPARATOR ',') AS invoice_item_disables, "
            + "    GROUP_CONCAT(UNIX_TIMESTAMP(ii.updated) ORDER BY ii.name SEPARATOR ',') AS invoice_item_updates "
            + "  FROM invoice_item_types iit "
            + "    LEFT JOIN invoice_items ii "
            + "      ON ii.invoice_item_type_id = iit.id "
            + "    LEFT JOIN invoice_item_type_tax_rates iitr "
            + "      ON iitr.invoice_item_type_id = iit.id "
            + "        AND iitr.club_invoicing_id = ? ";
    private final static String sql_aggregate = " GROUP BY iit.name, iit.id ";
    private final static String sql_all = sql_select + sql_aggregate;
    private final static String sql_by_id = sql_select + " WHERE iit.id = ? " + sql_aggregate;
    private final static String sql_by_name = sql_select + " WHERE iit.name = ? " + sql_aggregate;


    public InvoiceItemType(){}; // Empty parm
    
    public InvoiceItemType(Long id, String name, 
            boolean disabled){
        this.id = id;
        this.name = name;
        this.disabled = disabled;
    }
    
    public InvoiceItemType(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public InvoiceItemType(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public InvoiceItemType(long id){
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
            pstmt.setLong(1, (long)0); // club invoicing id
            pstmt.setLong(2, id);
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
                this.disabled = rs.getBoolean("disabled");
                this.updated = rs.getTimestamp("updated").getTime();
                
                result = id;
                
                //invoice_items = new ArrayList<InvoiceItem>();
                /*
                if(rs.getString("invoice_item_ids") != null){
                    List<String> invoice_item_ids = ArrayUtil.parseCsvLine(new StringReader(rs.getString("invoice_item_ids")));
                    List<String> invoice_item_rule_type_ids = ArrayUtil.parseCsvLine(new StringReader(rs.getString("invoice_item_rule_type_ids")));
                    List<String> invoice_item_names = ArrayUtil.parseCsvLine(new StringReader(rs.getString("invoice_item_names")));
                    List<String> invoice_item_descriptions = ArrayUtil.parseCsvLine(new StringReader(rs.getString("invoice_item_descriptions")));
                    List<String> invoice_item_rates = ArrayUtil.parseCsvLine(new StringReader(rs.getString("invoice_item_rates")));
                    List<String> invoice_item_minimum_qtys = ArrayUtil.parseCsvLine(new StringReader(rs.getString("invoice_item_minimum_qtys")));
                    List<String> invoice_item_maximum_qtys = ArrayUtil.parseCsvLine(new StringReader(rs.getString("invoice_item_maximum_qtys")));
                    List<String> invoice_item_disables = ArrayUtil.parseCsvLine(new StringReader(rs.getString("invoice_item_disables")));
                    List<String> invoice_item_updates = ArrayUtil.parseCsvLine(new StringReader(rs.getString("invoice_item_updates")));

                    for(int i = 0; i < invoice_item_ids.size(); i++){
                        invoice_items.add(new InvoiceItem(
                                Long.parseLong(invoice_item_ids.get(i)),
                                id,
                                Long.parseLong(invoice_item_rule_type_ids.get(i)),
                                invoice_item_names.get(i),
                                Float.parseFloat(invoice_item_rates.get(i)),
                                Float.parseFloat(invoice_item_minimum_qtys.get(i)),
                                Float.parseFloat(invoice_item_maximum_qtys.get(i)),
                                invoice_item_descriptions.get(i),
                                name,
                                reqUtil.parseBoolean(invoice_item_disables.get(i), false),
                                Long.parseLong(invoice_item_updates.get(i))
                                ));
                    }
                }
                 * 
                 */

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
                    + "INSERT INTO invoice_item_types "
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
                    + "UPDATE invoice_item_types "
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
                        + "DELETE FROM invoice_item_types "
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
    
    public static List<InvoiceItemType> getList(){
        return getList(null, null);
    }
    
    public static List<InvoiceItemType> getList(Long id){
        return getList(id, null);
    }
    
    public static List<InvoiceItemType> getListByClubInvoicingId(Long club_invoicing_id){
        return getList(null, club_invoicing_id);
    }
    
    public static List<InvoiceItemType> getList(Long id, Long club_invoicing_id){
        
        List<InvoiceItemType> result = new ArrayList<InvoiceItemType>();
        
        if(club_invoicing_id == null){
            club_invoicing_id = (long)0;
        }
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_ft = ApiCommon.getConnection();
        
        String error = null;
        
        InvoiceItemType r = new InvoiceItemType();
        
        try {
            int i = 1;
            if(id != null){
                pstmt = con_ft.prepareStatement(sql_by_id);
                pstmt.setLong(i++, club_invoicing_id);
                pstmt.setLong(i++, id);
            } else {
                pstmt = con_ft.prepareStatement(sql_all);
                pstmt.setLong(i++, club_invoicing_id);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new InvoiceItemType(rs);
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
