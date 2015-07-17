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
/**
 *
 * @author John Kielkopf
 */
public class ClubInvoicingRuleDetail {
    
    public Long id;
    public Long club_id; // read only
    public Long club_invoicing_id; // read only
    public Long club_invoicing_rule_id;
    public Long invoice_item_id;
    public Long tax_group_id;
    public Long sales_person_id;
    public String invoicing_rule_type_data;
    public String description;
    public Float quantity;
    public Float rate;
    public Float commission;
    public Integer commission_on_first;
    public Integer reoccur_limit;
    public Boolean disabled;
    public Long next_date; // read only
    public Long interval_id; // read only
    public Long updated;
    public Integer run_count;
    
    public String name; // Read only -- used to simplify error responses
    
    public String last_error;
    
        
    // Club Invoicing Rule Detail
    private final static String sql_select = ""
            + "SELECT cird.*, ii.name, cir.club_invoicing_id, cir.next_date, cir.interval_id, ci.club_id, "
            + "    COUNT(DISTINCT IF(i.voided IS NULL AND i.id IS NOT NULL, id.id, NULL)) AS run_count "
            + "  FROM club_invoicing_rule_details AS cird "
            + "    LEFT JOIN invoice_items AS ii "
            + "      ON ii.id = cird.invoice_item_id "
            + "    LEFT JOIN club_invoicing_rules AS cir "
            + "      ON cir.id = cird.club_invoicing_rule_id "
            + "    LEFT JOIN club_invoicing as ci "
            + "      ON ci.id = cir.club_invoicing_id "
            + "    LEFT JOIN invoice_details AS id "
            + "      ON id.club_invoicing_rule_detail_id = cird.id "
            + "        AND id.invoice_item_id = cird.invoice_item_id "
            + "    LEFT JOIN invoices AS i "
            + "      ON i.id = id.invoice_id AND i.club_invoicing_id = cir.club_invoicing_id ";
    private final static String sql_order = " GROUP BY ii.name, cird.id ";
    private final static String sql_all = sql_select + sql_order;
    private final static String sql_by_id = sql_select + "  WHERE cird.id = ? " + sql_order;
    private final static String sql_by_rule_id = sql_select + "  WHERE cird.club_invoicing_rule_id = ? " + sql_order;
    private final static String sql_by_rule_and_item_id = sql_select + "  WHERE cird.club_invoicing_rule_id = ? AND cird.invoice_item_id = ? " + sql_order;
    private final static String sql_by_id_and_item_id = sql_select + "  WHERE cird.id = ? AND cird.invoice_item_id = ? " + sql_order;
    private final static String sql_by_binding_ids = sql_select + "  WHERE cird.club_invoicing_rule_id = ? AND cird.invoice_item_id = ? " + sql_order;
    

    public ClubInvoicingRuleDetail(){}; // Empty parm
    
    /*
    public ClubInvoicingRuleDetail(Long id, long club_invoicing_rule_id, long invoice_item_id,  long tax_group_id, 
            long sales_person_id, String invoicing_rule_type_data, 
            Float commission, int commission_on_first, boolean disabled){
        loadByValue(id, club_invoicing_rule_id, invoice_item_id, tax_group_id,
            sales_person_id, invoicing_rule_type_data, 
            commission, commission_on_first, disabled, null, null);
    }
    
    public ClubInvoicingRuleDetail(Long id, long club_invoicing_rule_id, long invoice_item_id,  long tax_group_id,
            long sales_person_id, String invoicing_rule_type_data, 
            Float commission, int commission_on_first, boolean disabled, Long updated, String name){
        loadByValue(id, club_invoicing_rule_id, invoice_item_id, tax_group_id,
            sales_person_id, invoicing_rule_type_data, 
            commission, commission_on_first, disabled, updated, name);
    }
     * 
     */
    
    public ClubInvoicingRuleDetail(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public ClubInvoicingRuleDetail(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public ClubInvoicingRuleDetail(long club_invoicing_rule_id, long invoice_item_id){
        Connection con_ft = ApiCommon.getConnection();
        loadByInvoicingRuleAndInvoiceItemId(club_invoicing_rule_id, invoice_item_id, con_ft, false);
        Connect.close(con_ft);
    }
    
    public ClubInvoicingRuleDetail(long club_invoicing_rule_id, long invoice_item_id, Connection con_ft){
        loadByInvoicingRuleAndInvoiceItemId(club_invoicing_rule_id, invoice_item_id, con_ft, false);
    }
    
    public ClubInvoicingRuleDetail(long id){
        Connection con_ft = ApiCommon.getConnection();
        loadById(id, con_ft);
        Connect.close(con_ft);
    }
    /*
    public final void loadByValue(Long id, long club_invoicing_rule_id, long invoice_item_id,  long tax_group_id,
            long sales_person_id, String invoicing_rule_type_data, 
            Float commission, int commission_on_first, boolean disabled, Long updated, String name){
        this.id = id;
        this.club_invoicing_rule_id = club_invoicing_rule_id;
        this.invoice_item_id = invoice_item_id;
        this.sales_person_id = sales_person_id;
        this.invoicing_rule_type_data = invoicing_rule_type_data;
        this.commission_on_first = commission_on_first;
        this.commission = commission;
        this.disabled = disabled;
        this.updated = updated;
        
        this.name = name;
    }*/
    
    
    public final ClubInvoicingRuleDetail loadByIdAndInvoiceItemId(long club_invoicing_rule_detail_id, long invoice_item_id){
        Connection con_ft = ApiCommon.getConnection();
        loadByIdAndInvoiceItemId(club_invoicing_rule_detail_id, invoice_item_id, con_ft);
        Connect.close(con_ft);
        return this;
    }
    
    
    public final ClubInvoicingRuleDetail loadByIdAndInvoiceItemId(long id, long invoice_item_id, Connection con_ft){

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con_ft.prepareStatement(sql_by_id_and_item_id);
            pstmt.setLong(1, id);
            pstmt.setLong(2, invoice_item_id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                // Just get the first one if there are multiple
                loadFromResultSet(rs);
            } else {
                last_error = String.format(ApiConstants.error_finding_by_id, classNameLower(), id);
            }  
        } catch(Exception e) {
            Connect.logError(className()+".loadByIdAndInvoiceItemId: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = String.format(ApiConstants.error_loading_by_id, classNameLower(), e.toString());
        } finally {
            Connect.close(rs, pstmt);
        }
        
        return this;
        
    }
    
    public final Long loadByInvoicingRuleAndInvoiceItemId(long club_invoicing_rule_id, long invoice_item_id, Connection con_ft, boolean skip_loading){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con_ft.prepareStatement(sql_by_rule_and_item_id);
            pstmt.setLong(1, club_invoicing_rule_id);
            pstmt.setLong(2, invoice_item_id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                // Just get the first one if there are multiple
                result = loadFromResultSet(rs, skip_loading);
            } else {
                last_error = String.format(ApiConstants.error_finding_by_id, classNameLower(), club_invoicing_rule_id);
            }  
        } catch(Exception e) {
            Connect.logError(className()+".loadByInvoicingRuleAndInvoiceItemId: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = String.format(ApiConstants.error_loading_by_id, classNameLower(), e.toString());
        } finally {
            Connect.close(rs, pstmt);
        }
        
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
    
    public final Long loadByBindingIds(Long club_invoicing_rule_id, Long tax_rate_id, Connection con_ft){
        return loadByBindingIds(club_invoicing_rule_id, tax_rate_id, con_ft, false);
    }
    
    public final Long loadByBindingIds(Long club_invoicing_rule_id, Long invoice_item_id, Connection con_ft, boolean skip_loading){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con_ft.prepareStatement(sql_by_binding_ids);

            pstmt.setLong(1, club_invoicing_rule_id);
            pstmt.setLong(2, invoice_item_id);

            rs = pstmt.executeQuery();
            if(rs.next()){
                result = loadFromResultSet(rs, skip_loading);
            } else {
                last_error = "Unable to find "+classNameLower()+": with binding ids " + club_invoicing_rule_id + "/" + invoice_item_id;
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
                this.club_id = rs.getLong("club_id");
                this.club_invoicing_id = rs.getLong("club_invoicing_id");
                this.club_invoicing_rule_id = rs.getLong("club_invoicing_rule_id");
                this.invoice_item_id = rs.getLong("invoice_item_id");
                this.tax_group_id = rs.getLong("tax_group_id");
                this.sales_person_id = rs.getLong("sales_person_id");
                this.invoicing_rule_type_data = rs.getString("invoicing_rule_type_data");
                this.description = rs.getString("description");
                this.commission_on_first = rs.getInt("commission_on_first");
                this.commission = rs.getFloat("commission");
                this.quantity = rs.getFloat("quantity");
                this.rate = rs.getFloat("rate");
                this.reoccur_limit = rs.getInt("reoccur_limit");
                this.disabled = rs.getBoolean("disabled");
                this.next_date = rs.getTimestamp("next_date").getTime();
                this.interval_id = rs.getLong("interval_id");
                this.updated = rs.getTimestamp("updated").getTime();
                this.name = rs.getString("name");
                this.run_count = rs.getInt("run_count");
                
                if(sales_person_id == null || sales_person_id < 1){
                    sales_person_id = null;
                    commission_on_first = null;
                }
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
        
        reoccur_limit = (reoccur_limit == null || reoccur_limit < 1)?null:reoccur_limit;
        rate = (rate == null || rate.equals((float)0))?null:rate;
        quantity = (quantity == null || quantity.equals((float)0))?null:quantity;
        description = (description == null || description.trim().isEmpty())?null:description;
        
        if(invoice_item_id == null || club_invoicing_rule_id == null){
            last_error = last_error = String.format(ApiConstants.error_update_detail_no_id, classNameLower(), "");
        } else if(tax_group_id == null){
            last_error = last_error = String.format("Unable to update %1s record, no %1s selected", classNameLower(), "tax group");
        } else if(id == null){
            // Inserting new record
            try {
                pstmt = con_ft.prepareStatement(""
                        + "INSERT INTO club_invoicing_rule_details "
                        + "  (club_invoicing_rule_id, invoice_item_id, tax_group_id, "
                        + "    sales_person_id, invoicing_rule_type_data, "
                        + "    commission, commission_on_first, reoccur_limit, rate, quantity, description, disabled) "
                        + "  VALUES"
                        + "  (?, ?, ?, "
                        + "   ?, ?, "
                        + "   ?, ?, ?, ?, ?, ?, ?)");
                int i = 1;
                pstmt.setLong(i++, club_invoicing_rule_id);
                pstmt.setLong(i++, invoice_item_id);
                pstmt.setLong(i++, tax_group_id);
                if(sales_person_id == null){
                    pstmt.setNull(i++, Types.INTEGER);
                } else {
                    pstmt.setLong(i++, sales_person_id);
                }
                pstmt.setString(i++, invoicing_rule_type_data);
                ApiCommon.setOrNull(i++, pstmt, commission);
                ApiCommon.setOrNull(i++, pstmt, commission_on_first);
                ApiCommon.setOrNull(i++, pstmt, reoccur_limit);
                ApiCommon.setOrNull(i++, pstmt, rate);
                ApiCommon.setOrNull(i++, pstmt, quantity);
                ApiCommon.setOrNull(i++, pstmt, description);
                pstmt.setBoolean(i++, disabled);
                pstmt.executeUpdate();
                
                result = Connect.getLastInsertId(con_ft);
                
                id = result;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                if(loadByBindingIds(club_invoicing_rule_id, invoice_item_id, con_ft, true) != null){
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
                        + "UPDATE club_invoicing_rule_details "
                        + " SET"
                        + "  invoice_item_id = ?,"
                        + "  tax_group_id = ?,"
                        + "  sales_person_id = ?,"
                        + "  invoicing_rule_type_data = ?,"
                        + "  commission = ?, "
                        + "  commission_on_first = ?, "
                        + "  reoccur_limit = ?, "
                        + "  rate = ?, "
                        + "  quantity = ?, "
                        + "  description = ?, "
                        + "  disabled = ? "
                        + " WHERE id = ?"
                        + "  AND club_invoicing_rule_id = ?");
                int i = 1;
                pstmt.setLong(i++, invoice_item_id);
                pstmt.setLong(i++, tax_group_id);
                if(sales_person_id == null){
                    pstmt.setNull(i++, Types.INTEGER);
                } else {
                    pstmt.setLong(i++, sales_person_id);
                }
                pstmt.setString(i++, invoicing_rule_type_data);
                ApiCommon.setOrNull(i++, pstmt, commission);
                ApiCommon.setOrNull(i++, pstmt, commission_on_first);
                ApiCommon.setOrNull(i++, pstmt, reoccur_limit);
                ApiCommon.setOrNull(i++, pstmt, rate);
                ApiCommon.setOrNull(i++, pstmt, quantity);
                ApiCommon.setOrNull(i++, pstmt, description);
                pstmt.setBoolean(i++, disabled);
                
                pstmt.setLong(i++, id);
                pstmt.setLong(i++, club_invoicing_rule_id);
                
                pstmt.executeUpdate();
                
                result = id;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                Long check_id = loadByBindingIds(club_invoicing_rule_id, invoice_item_id, con_ft, true);
                if(check_id != null && !check_id.equals(id)){
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
                        + "DELETE FROM club_invoicing_rule_details "
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
    
    public final InvoicingRuleTypeData getInvoicingRuleTypeData(){
        Connection con_ft = ApiCommon.getConnection();
        InvoicingRuleTypeData result = getInvoicingRuleTypeData(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final InvoicingRuleTypeData getInvoicingRuleTypeData(Connection con_ft){
        Connection con_club = ApiCommon.getConnection(club_id);
        InvoicingRuleTypeData result = getInvoicingRuleTypeData(con_club, con_ft);
        Connect.close(con_club);
        return result;
    }
    
    // Load rule type data for this club invoicing rule
    public final InvoicingRuleTypeData getInvoicingRuleTypeData(Connection con_club, Connection con_ft){
        
        InvoicingRuleTypeData rule_type_data = new InvoicingRuleTypeData(); 
        Gson gson = new Gson();
        
        InvoiceItem invoice_item = new InvoiceItem(invoice_item_id, club_invoicing_id, con_ft);
        if(invoice_item.last_error != null){
            rule_type_data.last_error = invoice_item.last_error;
            return rule_type_data;
        }
        InvoicingRuleType invoicing_rule_type = new InvoicingRuleType(invoice_item.invoicing_rule_type_id, con_ft);
        if(invoicing_rule_type.last_error != null){
            rule_type_data.last_error = invoicing_rule_type.last_error;
            return rule_type_data;
        }
        DbInterval interval = new DbInterval(interval_id, con_ft);
        if(interval.last_error != null){
            rule_type_data.last_error = interval.last_error;
            return rule_type_data;
        }
        try {
            // See if there is existing data (rule type data is stored as JSON)
            rule_type_data = gson.fromJson(invoicing_rule_type_data, InvoicingRuleTypeData.class);
        } catch (Exception e) {
            rule_type_data = null;
        }
        
        if(rule_type_data == null || rule_type_data.club_id == null || rule_type_data.last_error != null){
            // New rule type data, or old data was corrupted
            rule_type_data = new InvoicingRuleTypeData();
            rule_type_data.club_id = this.club_id;
            rule_type_data.billing_start = this.next_date;
            rule_type_data.invoicing_rule_type = invoicing_rule_type;
            rule_type_data.invoice_item = invoice_item;
            if(!rule_type_data.loadMemberTypes(con_club)){
                return rule_type_data;
            }
            if(!rule_type_data.loadMemberShipTypes(con_club)){
                return rule_type_data;
            }
        } else {
            // Existing rule type data
            if(rule_type_data.invoice_item == null 
                    || rule_type_data.invoice_item.id == null
                    || !rule_type_data.invoice_item.id.equals(invoice_item.id)
                    || rule_type_data.invoice_item.invoicing_rule_type_id == null
                    || !rule_type_data.invoice_item.invoicing_rule_type_id.equals(invoice_item.invoicing_rule_type_id)){
                // Item or Rule type has changed since last run
                rule_type_data.updated = null;
            }
            // Make sure the rule type matches the item type
            rule_type_data.club_id = this.club_id;
            rule_type_data.billing_start = this.next_date;
            rule_type_data.invoicing_rule_type = invoicing_rule_type;
            rule_type_data.invoice_item = invoice_item;
            
        }
        
        rule_type_data.billing_end = interval.getLastDay(this.next_date);
        
        if(rule_type_data.updated == null || rule_type_data.billable_count_details == null){
            // No counts ever loaded.  Load them.
            rule_type_data.loadBillableCounts(con_club);
        }
        
        return rule_type_data;
    }
    
    public final void setInvoicingRuleTypeData(InvoicingRuleTypeData data){
        Gson gson = new Gson();
        if(data == null){
            invoicing_rule_type_data = null;
        } else {
            invoicing_rule_type_data = gson.toJson(data);
        }
    }
    
    public static List<ClubInvoicingRuleDetail> getList(){
        return getList(null, null);
    }
    
    public static List<ClubInvoicingRuleDetail> getList(Long id){
        return getList(id, null);
    }
    
    public static List<ClubInvoicingRuleDetail> getListByRuleId(Long club_invoicing_rule_id){
        return getList(null, club_invoicing_rule_id);
    }
    
    public static List<ClubInvoicingRuleDetail> getList(Long id, Long club_invoicing_rule_id){
        Connection con_ft = ApiCommon.getConnection();
        List<ClubInvoicingRuleDetail> result = null;
        result = getList(id, club_invoicing_rule_id, con_ft);
        Connect.close(con_ft);
        return result;
        
    }
    
    public static List<ClubInvoicingRuleDetail> getList(Long id, Long club_invoicing_rule_id, Connection con_ft){
        
        List<ClubInvoicingRuleDetail> result = new ArrayList<ClubInvoicingRuleDetail>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String error = null;
        
        ClubInvoicingRuleDetail r = new ClubInvoicingRuleDetail();
        
        try {
            int i = 1;
            if(id != null){
                pstmt = con_ft.prepareStatement(sql_by_id);
                pstmt.setLong(i++, id);
            } else if(club_invoicing_rule_id != null){
                pstmt = con_ft.prepareStatement(sql_by_rule_id);
                pstmt.setLong(i++, club_invoicing_rule_id);
            } else {
                pstmt = con_ft.prepareStatement(sql_all);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new ClubInvoicingRuleDetail(rs);
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
    
   
    // Generate invoice detail based on this rule -- does not save rule detail!!
    public final InvoiceDetail run(){
        InvoiceDetail result = null;
        Connection con_ft = ApiCommon.getConnection();
        result = run(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final InvoiceDetail run(Connection con_ft){
        
        if(this.disabled || (this.reoccur_limit != null && this.reoccur_limit > 0 && this.run_count >= this.reoccur_limit )){
            return null;
        }
        
        Gson gson = new Gson();
        
        InvoiceDetail invoice_detail = new InvoiceDetail();
        
        Connection con_club = ApiCommon.getConnection(club_id);
        
        InvoicingRuleTypeData rule_type_data = this.getInvoicingRuleTypeData(con_club, con_ft);
        if(rule_type_data.last_error != null){
            last_error = rule_type_data.last_error;
            Connect.close(con_club);
            return null;
        }
        rule_type_data.loadBillableCounts(con_club);
        if(rule_type_data.last_error != null){
            last_error = rule_type_data.last_error;
            Connect.close(con_club);
            return null;
        }
        
        Connect.close(con_club);
        
        TaxGroup tax_group = new TaxGroup(this.tax_group_id, con_ft);
        if(tax_group.last_error != null){
            last_error = tax_group.last_error;
            return null;
        }

        // Save snapshot of auto generated invoicing rule type data
        invoice_detail.invoicing_rule_type_data = gson.toJson(rule_type_data);
        
        // See if this rule overrides and data
        boolean override = false;
        if(this.quantity != null && !this.quantity.equals((float)0)) {
            override = true;
            rule_type_data.quantity = this.quantity;
        }
        if(this.rate != null && !this.rate.equals((float)0)){
            override = true;
            rule_type_data.invoice_item.rate = this.rate;
        }
        if((this.description == null || this.description.isEmpty()) && override){
            // We need to override the description.
            InvoiceItem item = new InvoiceItem(this.invoice_item_id, con_ft);
            if(item.description != null){
                rule_type_data.invoice_item.description = item.description;
            }
        } else if(this.description != null && !this.description.isEmpty()) {
            rule_type_data.invoice_item.description = this.description;
        }
        if(override){
            rule_type_data.updateDescription();
        }
        
        // Fill invoice detail with data
        invoice_detail.invoice_item_id = this.invoice_item_id;
        invoice_detail.invoice_item_name = rule_type_data.invoice_item.name;
        invoice_detail.description = rule_type_data.invoice_item.description;
        invoice_detail.quantity = rule_type_data.quantity;
        invoice_detail.rate = rule_type_data.invoice_item.rate;
        invoice_detail.tax_group_id = this.tax_group_id;
        invoice_detail.tax_group_name = tax_group.name;
        invoice_detail.tax_rate = tax_group.group_rate;
        invoice_detail.club_invoicing_rule_detail_id = this.id;
        
        invoice_detail.invoicing_rule_type_id = rule_type_data.invoice_item.invoicing_rule_type_id;
        invoice_detail.sales_person_id = this.sales_person_id;
        
        if(this.commission_on_first != null && this.run_count < this.commission_on_first){
            // Havn't yet applied commission to enough of these.  Do so.
            invoice_detail.commission = this.commission;
        } else {
            invoice_detail.commission = (float)0;
        }
        /*
        if (rule_type_data.invoicing_rule_type.count_adults || rule_type_data.invoicing_rule_type.count_members) {
            // Per-member/adult golf count check.  Check if quantities are different.
            InvoiceDetail last_sold = InvoiceDetail.lastDetailByRuleDetailId(this.id, con_ft);
            if (last_sold != null && last_sold.last_error != null) {
                // Error.  Report and return
                last_error = last_sold.last_error;
                return null;
            } else if (last_sold != null) {
                Connect.logDebug("JGK", "Checking against last sold items");
                // This item was billed before.  Compare it.
                // Get the rule type data that was used before.
                InvoicingRuleTypeData last_data = last_sold.getRuleTypeData();
                if (last_data != null) {
                    Connect.logDebug("JGK", "Checking last rule data type");
                    // There is rule type data for the last sale.  Check it.
                    if(last_data.billing_start == null){
                        // Looks like there wasn't a start/end date stored.
                        // Just use what we had before
                        last_data.billing_start = next_date;
                        DbInterval interval = new DbInterval(interval_id, con_ft);
                        if(interval.last_error != null){
                            last_error = interval.last_error;
                            return null;
                        }
                        last_data.billing_end = interval.getLastDay(this.next_date);
                    }
                    List<String> changes = rule_type_data.difference(club_id, last_data);
                    if(!changes.isEmpty()){
                        invoice_detail.setChangedFromLast(changes);
                    }
                }
            }
        }
         * */
        
        // Simplified:  Just check if the quantity has changed:
        InvoiceDetail last_sold = InvoiceDetail.lastDetailByRuleDetailId(this.id, con_ft);
        if (last_sold != null && last_sold.last_error != null) {
            // Error.  Report and return
            last_error = last_sold.last_error;
            return null;
        } else if (last_sold != null) {
            Connect.logDebug("JGK", "Checking against last sold items");
            // This item was billed before using this rule detail.  Compare it.
            if(!last_sold.quantity.equals(invoice_detail.quantity)){
                Connect.logDebug("JGK", "Quantity has changed since last");
                invoice_detail.setChangedFromLast(new ArrayList<String>(Arrays.asList("Quantity changed from "+last_sold.quantity+" to "+invoice_detail.quantity)));
            }
        } else {
            Connect.logDebug("JGK", "No last sold items");
        }
        
        return invoice_detail;
        
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
