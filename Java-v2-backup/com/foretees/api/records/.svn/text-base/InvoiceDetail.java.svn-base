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
import com.google.gson.*; // for json
import com.google.gson.reflect.*; // for json
/**
 *
 * @author John Kielkopf
 */
public class InvoiceDetail {
    
    public Long id;
    public Long invoice_id;
    public Long invoice_item_id;
    public Long club_invoicing_id; // read only
    public Long club_invoicing_rule_detail_id;
    public Long tax_group_id;
    public Long sales_person_id;
    public String sales_person_name; // Read only
    public Long invoicing_rule_type_id;
    public String invoicing_rule_type_data;
    public Float quantity;
    public Float rate;
    public Float tax_rate;
    public Float commission;
    public String commission_paid;
    public String invoice_date; // Read only
    public String paid; // read only
    public String invoice_item_name;
    public String tax_group_name;
    //public String sku;
    public String description;
    
    //public Boolean rate_changed;
    //public Boolean tax_rate_changed;
    //public Boolean commission_changed;
    //public Boolean name_changed;
    //public Boolean sku_changed;
    //public Boolean description_changed;
    
    public String changed_from_last; // Json of List<String>
    
    public Long updated;
    
    public String voided;
    
    public String last_error;
    
     
    // Club Invoice Detail
    private final static String sql_select = ""
            + "SELECT id.*, i.club_invoicing_id, i.voided, i.paid, sp.name AS sales_person_name, i.date AS invoice_date "
            + "  FROM invoice_details AS id "
            + "    LEFT JOIN invoices AS i "
            + "      ON i.id = id.invoice_id "
            + "    LEFT JOIN users AS sp "
            + "      ON sp.id = id.sales_person_id ";
    private final static String sql_order = " ORDER BY id.id ";
    private final static String sql_by_invoice_id = sql_select + "  WHERE id.invoice_id = ? " + sql_order;
    private final static String sql_by_id = sql_select + "  WHERE id.id = ? " + sql_order;
    private final static String sql_unpaid_commission = sql_select + "  WHERE i.voided IS NULL AND i.paid IS NOT NULL AND id.commission_paid IS NULL AND id.commission > 0 AND id.rate > 0 AND id.quantity > 0 AND id.sales_person_id IS NOT NULL ORDER BY sp.name, i.date DESC, id.id ";
    private final static String sql_unpaid_commission_by_sales_person_id = sql_select + "  WHERE i.voided IS NULL AND i.paid IS NOT NULL AND id.commission_paid IS NULL AND id.commission > 0 AND id.rate > 0 AND id.quantity > 0 AND id.sales_person_id = ? " + sql_order;
    private final static String sql_most_recent_by_item_id = sql_select + "  WHERE i.club_invoicing_id = ? AND id.invoice_item_id = ? ORDER BY i.due_date DESC LIMIT 1 ";
    private final static String sql_most_recent_by_rule_detail_id = sql_select + "  WHERE id.club_invoicing_rule_detail_id = ? ORDER BY i.due_date DESC LIMIT 1 ";
    

    public InvoiceDetail(){}; // Empty parm
    
    public InvoiceDetail(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public InvoiceDetail(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public InvoiceDetail(long id){
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
        int i = 1;
        try {
            pstmt = con_ft.prepareStatement(sql_by_id);
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
                this.invoice_id = rs.getLong("invoice_id");
                this.invoice_item_id = rs.getLong("invoice_item_id");
                this.club_invoicing_id = rs.getLong("club_invoicing_id");
                this.club_invoicing_rule_detail_id = rs.getLong("club_invoicing_rule_detail_id");
                this.tax_group_id = rs.getLong("tax_group_id");
                this.sales_person_id = rs.getLong("sales_person_id");
                this.sales_person_name = rs.getString("sales_person_name");
                this.invoicing_rule_type_id = rs.getLong("invoicing_rule_type_id");
                this.invoicing_rule_type_data = rs.getString("invoicing_rule_type_data");
                this.quantity = rs.getFloat("quantity");
                this.rate = rs.getFloat("rate");
                this.tax_rate = rs.getFloat("tax_rate");
                this.commission = rs.getFloat("commission");
                this.commission_paid = rs.getString("commission_paid");
                this.invoice_date = rs.getString("invoice_date");
                this.paid = rs.getString("paid");
                this.invoice_item_name = rs.getString("invoice_item_name");
                this.tax_group_name = rs.getString("tax_group_name");
                //this.sku = rs.getString("sku");
                this.description = rs.getString("description");
                this.changed_from_last = rs.getString("changed_from_last");
                this.voided = rs.getString("voided");
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
        
        if(sales_person_id != null && sales_person_id == 0){
            sales_person_id = null;
        }
        if(club_invoicing_rule_detail_id != null && club_invoicing_rule_detail_id == 0){
            club_invoicing_rule_detail_id = null;
        }
        
        if(invoice_item_id == null){
            last_error = "You must select an invoice item for all details";
        } else if(invoicing_rule_type_id == null){
            last_error = "You must select an invoicing rule type for all details";
        } else if(tax_group_id == null){
            last_error = "You must select a tax group for all details";
        } else if(tax_rate == null){
            last_error = "You must select a tax rate for all details";
        } else if(id == null){
            // Inserting new record
            if(commission == null){
                commission = (float)0;
            }
            try {
                pstmt = con_ft.prepareStatement(""
                        + "INSERT INTO invoice_details "
                        + "  (invoice_id, invoice_item_id, tax_group_id, sales_person_id, invoicing_rule_type_id, club_invoicing_rule_detail_id,  "
                        + "     invoicing_rule_type_data, quantity, rate, tax_rate, commission, "
                        + "     invoice_item_name, tax_group_name, description, changed_from_last) "
                        + "  VALUES"
                        + "  (?, ?, ?, ?, ?, ?, "
                        + "   ?, ?, ?, ?, ?, "
                        + "   ?, ?, ?, ?)");

                int i = 1;
                pstmt.setLong(i++, invoice_id);
                pstmt.setLong(i++, invoice_item_id);
                pstmt.setLong(i++, tax_group_id);
                ApiCommon.setOrNull(i++, pstmt, sales_person_id);
                pstmt.setLong(i++, invoicing_rule_type_id);
                ApiCommon.setOrNull(i++, pstmt, club_invoicing_rule_detail_id);
                pstmt.setString(i++, invoicing_rule_type_data);
                pstmt.setFloat(i++, quantity);
                pstmt.setFloat(i++, rate);
                pstmt.setFloat(i++, tax_rate);
                pstmt.setFloat(i++, commission);
                pstmt.setString(i++, invoice_item_name);
                pstmt.setString(i++, tax_group_name);
                //pstmt.setString(i++, sku);
                pstmt.setString(i++, description);
                ApiCommon.setOrNull(i++, pstmt, changed_from_last);
                pstmt.executeUpdate();
                
                result = Connect.getLastInsertId(con_ft);
                
                id = result;
                
                loadById(id, con_ft); // Refresh results
                
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
                        + "UPDATE invoice_details "
                        + " SET"
                        + "  invoice_id = ?,"
                        + "  invoice_item_id = ?, "
                        + "  tax_group_id = ?,"
                        + "  sales_person_id = ?, "
                        + "  invoicing_rule_type_id = ?, "
                        + "  club_invoicing_rule_detail_id = ?, "
                        + "  invoicing_rule_type_data = ?, "
                        + "  quantity = ?, "
                        + "  rate = ?, "
                        + "  tax_rate = ?, "
                        + "  commission = ?, "
                        + "  invoice_item_name = ?, "
                        + "  tax_group_name = ?, "
                        //+ "  sku = ?, "
                        + "  description = ?, "
                        + "  changed_from_last = ? "
                        + " WHERE id = ? ");

                int i = 1;
                pstmt.setLong(i++, invoice_id);
                pstmt.setLong(i++, invoice_item_id);
                pstmt.setLong(i++, tax_group_id);
                ApiCommon.setOrNull(i++, pstmt, sales_person_id);
                pstmt.setLong(i++, invoicing_rule_type_id);
                ApiCommon.setOrNull(i++, pstmt, club_invoicing_rule_detail_id);
                pstmt.setString(i++, invoicing_rule_type_data);
                pstmt.setFloat(i++, quantity);
                pstmt.setFloat(i++, rate);
                pstmt.setFloat(i++, tax_rate);
                pstmt.setFloat(i++, commission);
                pstmt.setString(i++, invoice_item_name);
                pstmt.setString(i++, tax_group_name);
                //pstmt.setString(i++, sku);
                pstmt.setString(i++, description);
                ApiCommon.setOrNull(i++, pstmt, changed_from_last);
                pstmt.setLong(i++, id);
                
                pstmt.executeUpdate();
                
                result = id;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                    Connect.logError(className()+".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_update, classNameLower(), e.toString());
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
                        + "DELETE FROM invoice_details "
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
    
    public static String setCommissionPaid(List<InvoiceDetail> records){
        Connection con_ft = ApiCommon.getConnection();
        String result = setCommissionPaid(records, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static String setCommissionPaid(List<InvoiceDetail> records, Connection con_ft){
        Savepoint save_point = Connect.startTransaction(con_ft);
        for (InvoiceDetail record : records) {
            record.setCommissionPaid(con_ft);
            if (record.last_error != null) {
                // We encountered an error.  Rollback any changes and restore previous state.
                Connect.cancelTransaction(con_ft, save_point);
                return record.last_error;
            }
        }
        // All good.  Commit the transaction.
        Connect.commitTransaction(con_ft, save_point);
        return null;
    }
    
    public final boolean setCommissionPaid(){
        Connection con_ft = ApiCommon.getConnection();
        boolean result = setCommissionPaid(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final boolean setCommissionPaid(Connection con_ft){
        boolean result = false;
        
        PreparedStatement pstmt = null;
        
        if(id == null){
            // Can't pay for an invoice with a null id
            last_error = "Unable to set commission payment date -- no invoice detail record given.";
            
        } else {
            
            InvoiceDetail test = new InvoiceDetail(id);
            if (test.last_error != null) {
                last_error = String.format(ApiConstants.error_update, classNameLower(), test.last_error);
            } else if (test.voided != null) {
                last_error = String.format(ApiConstants.error_update, classNameLower(), "Cannot mark commission for details of voided Invoice #" + invoice_id + " as paid.");
            } else if (test.paid == null) {
                last_error = String.format(ApiConstants.error_update, classNameLower(), "Cannot mark commission for details of unpaid Invoice #" + invoice_id + " as paid.");
            } else {
                // Update existing record

                try {
                    pstmt = con_ft.prepareStatement(""
                            + "UPDATE invoice_details "
                            + " SET commission_paid = ? "
                            + " WHERE id = ? ");

                    int i = 1;
                    pstmt.setString(i++, commission_paid);
                    pstmt.setLong(i++, id);
                    pstmt.executeUpdate();

                    result = true;

                } catch (SQLException e) {
                    Connect.logError(className() + ".setCommissionPaid: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_update, classNameLower(), e.toString());
                } finally {
                    Connect.close(pstmt);
                }
            }
            
        }

        return result;
    }
    
    public final void setChangedFromLast(List<String> changes){
        Gson gson = new Gson();
        if(changes == null || changes.isEmpty()){
            changed_from_last = null;
        } else {
            changed_from_last = gson.toJson(changes);
        }
    }
    
    public final List<String> getChangedFromLast(){
        Gson gson = new Gson();
        List<String> result = new ArrayList<String>();
        if(this.changed_from_last == null){
            return result;
        }
        try {
            result = gson.fromJson(this.changed_from_last, new TypeToken<ArrayList<String>>(){}.getType());
        } catch (Exception e) {
            return result = new ArrayList<String>();
        }
        return result;
    }
    
    public final InvoicingRuleTypeData getRuleTypeData(){
        
        Gson gson = new Gson();
        
        InvoicingRuleTypeData data = null;
        
        try {
            data = gson.fromJson(this.invoicing_rule_type_data, InvoicingRuleTypeData.class);
        } catch (Exception e) {
            return null;
        }
        
        return data;
        
    }
    
    public static InvoiceDetail lastDetailByItemId(long invoice_item_id, long club_invoicing_id){
        Connection con_ft = ApiCommon.getConnection();
        InvoiceDetail result = lastDetailByItemId(invoice_item_id, club_invoicing_id, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static InvoiceDetail lastDetailByItemId(long invoice_item_id, long club_invoicing_id, Connection con_ft){
        
        InvoiceDetail result = new InvoiceDetail();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int i = 1;
        try {
            pstmt = con_ft.prepareStatement(sql_most_recent_by_item_id);
            pstmt.setLong(i++, club_invoicing_id);
            pstmt.setLong(i++, invoice_item_id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result.loadFromResultSet(rs, false);
            } 
        } catch(Exception e) {
            Connect.logError(result.className()+".lastDetailByItemId: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            result.last_error = String.format(ApiConstants.error_loading_by_id, result.classNameLower(), e.toString());
        } finally {
            Connect.close(rs, pstmt);
        }
        
        return result;
        
    }
    
    public static InvoiceDetail lastDetailByRuleDetailId(long club_invoicing_rule_detail_id){
        Connection con_ft = ApiCommon.getConnection();
        InvoiceDetail result = lastDetailByRuleDetailId(club_invoicing_rule_detail_id, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static InvoiceDetail lastDetailByRuleDetailId(long club_invoicing_rule_detail_id, Connection con_ft){
        
        InvoiceDetail result = new InvoiceDetail();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int i = 1;
        try {
            pstmt = con_ft.prepareStatement(sql_most_recent_by_rule_detail_id);
            pstmt.setLong(i++, club_invoicing_rule_detail_id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result.loadFromResultSet(rs, false);
            } else {
                result = null;
            }
        } catch(Exception e) {
            Connect.logError(result.className()+".lastDetailByRuleDetailId: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            result.last_error = String.format(ApiConstants.error_loading_by_id, result.classNameLower(), e.toString());
        } finally {
            Connect.close(rs, pstmt);
        }
        
        return result;
        
    }
    
    public static List<InvoiceDetail> getList(long invoice_id){
        Connection con_ft = ApiCommon.getConnection();
        List<InvoiceDetail> result = getList(invoice_id, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static List<InvoiceDetail> getList(long invoice_id, Connection con_ft){
        
        List<InvoiceDetail> result = new ArrayList<InvoiceDetail>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String error = null;
        
        InvoiceDetail r = new InvoiceDetail();
        
        try {
            int i = 1;
            pstmt = con_ft.prepareStatement(sql_by_invoice_id);
            pstmt.setLong(i++, invoice_id);
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new InvoiceDetail(rs);
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
            Connect.close(rs, pstmt);
        }
 
        if(error != null){
            Connect.logError(r.className() + ".getList: Err=" + error);
            return null;
        } else {
            return result;
        }

    }
    
     public static List<InvoiceDetail> getUnpaidCommission(){
        Connection con_ft = ApiCommon.getConnection();
        List<InvoiceDetail> result = null;
        result = getUnpaidCommission(null);
        Connect.close(con_ft);
        return result;
        
    }
    
    public static List<InvoiceDetail> getUnpaidCommission(Long sales_person_id){
        Connection con_ft = ApiCommon.getConnection();
        List<InvoiceDetail> result = null;
        result = getUnpaidCommission(sales_person_id, con_ft);
        Connect.close(con_ft);
        return result;
        
    }
    
    public static List<InvoiceDetail> getUnpaidCommission(Long sales_person_id, Connection con_ft){
        
        List<InvoiceDetail> result = new ArrayList<InvoiceDetail>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String error = null;
        
        InvoiceDetail r = new InvoiceDetail();
        
        try {
            int i = 1;
            if(sales_person_id != null){
                pstmt = con_ft.prepareStatement(sql_unpaid_commission_by_sales_person_id);
                pstmt.setLong(i++, sales_person_id);
            } else {
                pstmt = con_ft.prepareStatement(sql_unpaid_commission);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new InvoiceDetail(rs);
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
            Connect.logError(r.className() + ".getUnpaidCommission: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt);
        }
 
        if(error != null){
            Connect.logError(r.className() + ".getUnpaidCommission: Err=" + error);
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
