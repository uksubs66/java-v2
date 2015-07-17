/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.records;

//import com.foretees.common.ArrayUtil;
import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Connect;
import com.foretees.common.sendEmail;
import com.foretees.common.parmEmail;
//import com.foretees.common.reqUtil;
import com.foretees.common.timeUtil;
import java.sql.*;          // mysql
import java.net.URLEncoder;
//import java.util.UUID;
//import javax.naming.*;
//import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;
import javax.mail.util.*;
import org.joda.time.DateTimeZone;
import org.apache.commons.lang.StringEscapeUtils;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author John Kielkopf
 */
public class Invoice {
    
    public Long id;
    public Long club_invoicing_id;
    public Long club_invoicing_rule_id;
    public Long terms_id;
    public String bill_to;
    public String notes;
    public String purchase_order;
    public String date;
    public String due_date;
    public String sales_people;
    public String rep_codes;
    //public String paid;
    public String sent_to_club;
    public String sent_email_to_club;
    
    public String voided;
    
    public Long club_id; // read only
    public String club_name; // read only
    public String club_db_name; // read only
    public String club_invoicing_name; // read only
    public String club_invoicing_rule_name; // read only
    public String club_invoicing_email; // ready only
    public String invoice_status; // ready only
    
    public Long updated; // read only

    public Float total; // read only
    public Float gross_total; // read only
    public Float tax_total; // read only
    public Float commission_total; // read only
    public String terms; // read only
    
    public String last_payment_date; // read only
    public Float payment_amount; // read only
    public Float amount_due; // read only
    public Integer days_past_due; // read only
    
    public Boolean show_to_pro; // read only
    
    public List<InvoiceDetail> details;
    
    public String last_error;
    
    private boolean block_save = false;
    
    
    // Club Invoice
    private final static String sql_past_due_stmt = " IF(((id.rate*id.quantity) + ((id.rate*id.quantity) * id.tax_rate) - IF(i.paid IS NULL,0,(id.rate*id.quantity) + ((id.rate*id.quantity) * id.tax_rate)) > 0), DATEDIFF(NOW(),i.due_date), 0) ";
    private final static String sql_select = ""
            + "SELECT i.*, c.fullname AS club_name, c.clubname AS club_db_name, c.id AS club_id, "
            + "    ci.name AS club_invoicing_name, it.name AS terms, "
            + "    cir.name AS club_invoicing_rule_name, "
            + "    ci.email AS club_invoicing_email, ci.show_to_pro, "
            + "    SUM(id.rate*id.quantity) AS item_total, "
            + "    SUM((id.rate*id.quantity) * id.tax_rate) AS tax_total, "
            + "    SUM((id.rate*id.quantity) + ((id.rate*id.quantity) * id.tax_rate)) AS gross_total, "
            + "    SUM((id.rate*id.quantity) * id.commission) AS commission_total, "
            + "    MAX(i.voided) AS voided, "
            + "    GROUP_CONCAT(DISTINCT sp.name ORDER BY sp.name SEPARATOR ', ') AS sales_people, "
            + "    GROUP_CONCAT(DISTINCT sp.rep_code ORDER BY sp.rep_code SEPARATOR ', ') AS rep_codes, "
            + "    MAX(i.paid) AS last_payment_date, "
            + "    SUM(IF(i.paid IS NOT NULL,(id.rate*id.quantity) + ((id.rate*id.quantity) * id.tax_rate),0)) AS payment_amount, "
            + "    SUM(IF(i.paid IS NULL,(id.rate*id.quantity) + ((id.rate*id.quantity) * id.tax_rate),0)) AS amount_due, "
            + "    MAX(" + sql_past_due_stmt + ") AS days_past_due "
            + "  FROM invoices AS i "
            + "    INNER JOIN club_invoicing ci "
            + "      ON ci.id = i.club_invoicing_id "
            + "    INNER JOIN clubs c "
            + "      ON c.id = ci.club_id "
            + "    LEFT JOIN club_invoicing_rules AS cir "
            + "      ON cir.id = i.club_invoicing_rule_id "
            + "    LEFT JOIN invoice_details AS id "
            + "      ON id.invoice_id = i.id "
            + "    LEFT JOIN users AS sp "
            + "      ON sp.id = id.sales_person_id "
            + "    LEFT JOIN invoice_terms AS it "
            + "      ON it.id = i.terms_id ";
    private final static String sql_aggregate = " GROUP BY i.id DESC ";
    private final static String sql_all = sql_select + sql_aggregate;
    private final static String sql_past_due = sql_all + " HAVING voided IS NULL AND days_past_due > 0 AND sent_to_club IS NOT NULL ";
    private final static String sql_unsent = sql_all + " HAVING voided IS NULL AND sent_to_club IS NULL ";
    private final static String sql_unsent_email = sql_all + 
            " HAVING voided IS NULL AND ci.email IS NOT NULL AND ci.email > '' "
            + "  AND sent_to_club IS NOT NULL AND sent_email_to_club IS NULL "
            + "  AND (last_failed_email IS NULL OR last_failed_email > (NOW() + INTERVAL 5 HOUR)) "; // wait five hour between failed email re-tries.
    private final static String sql_unpaid = sql_all + " HAVING voided IS NULL  AND amount_due > 0 AND sent_to_club IS NOT NULL ";
    private final static String sql_by_club_invoicing_id = sql_select + " WHERE i.club_invoicing_id = ? " + sql_aggregate;
    private final static String sql_by_club_id = sql_select + " WHERE ci.club_id = ? AND ci.disabled = 0 " + sql_aggregate;
    private final static String sql_sent_to_club = sql_select + " WHERE sent_to_club IS NOT NULL " + sql_aggregate;
    private final static String sql_sent_to_club_by_club_id = sql_by_club_id + " HAVING sent_to_club IS NOT NULL ";
    private final static String sql_unpaid_by_club_invoicing_id = sql_by_club_invoicing_id + " HAVING voided IS NULL AND amount_due > 0 AND sent_to_club IS NOT NULL ";
    private final static String sql_sent_to_club_by_club_invoicing_id = sql_by_club_invoicing_id + " HAVING sent_to_club IS NOT NULL ";
    private final static String sql_unpaid_by_club_id = sql_by_club_id + " HAVING voided IS NULL AND amount_due > 0 AND sent_to_club IS NOT NULL ";
    private final static String sql_past_due_by_club_invoicing_id = sql_by_club_invoicing_id + " HAVING voided IS NULL AND days_past_due > 0 AND sent_to_club IS NOT NULL ";
    private final static String sql_past_due_by_club_id = sql_by_club_id + " HAVING voided IS NULL AND days_past_due > 0 AND sent_to_club IS NOT NULL ";
    private final static String sql_unsent_by_club_invoicing_id = sql_by_club_invoicing_id + " HAVING voided IS NULL AND sent_to_club IS NULL ";
    private final static String sql_by_id = sql_select + "  WHERE i.id = ? " + sql_aggregate;

    public Invoice(){}; // Empty parm
    
    public Invoice(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public Invoice(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public Invoice(long id){
        Connection con_ft = ApiCommon.getConnection();
        loadById(id, con_ft);
        Connect.close(con_ft);
    }
    
    public Invoice(long id, Connection con_ft, boolean load_details){
        loadById(id, con_ft);
        if(load_details && last_error == null){
            loadDetails(con_ft);
        }
    }
    
    public Invoice(long id, boolean load_details){
        Connection con_ft = ApiCommon.getConnection();
        loadById(id, con_ft);
        if(load_details && last_error == null){
            loadDetails(con_ft);
        }
        Connect.close(con_ft);
    }
    
    public Invoice(long id, boolean load_details, boolean noSensitiveData){
        Connection con_ft = ApiCommon.getConnection();
        loadById(id, con_ft);
        if(load_details && last_error == null){
            loadDetails(con_ft);
        }
        if(noSensitiveData){
            clearSensitiveData();
        }
        Connect.close(con_ft);
    }
    
    public final void clearSensitiveData(){
        // Remove information we may not want the club to see
        if(details != null){
            for (InvoiceDetail detail : this.details) {
                detail.commission = null;
                detail.sales_person_id = null;
                detail.commission_paid = null;
                detail.sales_person_name = null;
                detail.invoicing_rule_type_data = null;
                detail.invoicing_rule_type_id = null;
                detail.changed_from_last = null;
            }
        }
        this.commission_total = null;
        this.sales_people = null;
        this.block_save = true; // Prevent this invoice object from being saved.
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
    
    public final void loadDetails(){
        Connection con_ft = ApiCommon.getConnection();
        loadDetails(con_ft);
        Connect.close(con_ft);
    }
    
    public final void loadDetails(Connection con_ft){
        
        details = InvoiceDetail.getList(id, con_ft);
        
        if(details == null){
            last_error = String.format("Error loading %1s details.", classNameLower());
        }

    }
    
    public Long loadFromResultSet(ResultSet rs){
        return loadFromResultSet(rs, false);
    }
    
    private Long loadFromResultSet(ResultSet rs, boolean skip_loading){
        
        Long result = null;
        try {
            if(skip_loading){
                result = rs.getLong("id");
            } else {
                
                this.id = rs.getLong("id");
                this.club_invoicing_id = rs.getLong("club_invoicing_id");
                this.club_invoicing_rule_id = rs.getLong("club_invoicing_rule_id");
                this.club_id = rs.getLong("club_id");
                this.terms_id = rs.getLong("terms_id");
                this.bill_to = rs.getString("bill_to");
                this.notes = rs.getString("notes");
                this.purchase_order = rs.getString("purchase_order");
                this.due_date = rs.getString("due_date");
                this.sent_to_club = rs.getString("sent_to_club");
                this.sent_email_to_club = rs.getString("sent_email_to_club");
                this.last_payment_date = rs.getString("paid");
                this.sales_people = rs.getString("sales_people");
                this.rep_codes = rs.getString("rep_codes");
                this.voided = rs.getString("voided");
                this.date = rs.getString("date");
                this.updated = rs.getTimestamp("updated").getTime();
                
                this.total = rs.getFloat("item_total");
                this.gross_total = rs.getFloat("gross_total");
                this.tax_total = rs.getFloat("tax_total");
                this.commission_total = rs.getFloat("commission_total");
                
                this.last_payment_date = rs.getString("last_payment_date");
                this.payment_amount = rs.getFloat("payment_amount");
                this.amount_due = rs.getFloat("amount_due");
                this.days_past_due = rs.getInt("days_past_due");
                
                this.club_invoicing_name = rs.getString("club_invoicing_name");
                this.club_invoicing_rule_name = rs.getString("club_invoicing_rule_name");
                this.club_name = rs.getString("club_name");
                this.club_db_name = rs.getString("club_db_name");
                this.club_invoicing_email = rs.getString("club_invoicing_email");
                
                this.show_to_pro = rs.getBoolean("show_to_pro");
                
                this.terms = rs.getString("terms");
                
                if(days_past_due == null || days_past_due < 0){
                    days_past_due = 0;
                }
                
                if(voided != null){
                    invoice_status = "VOIDED";
                } else if(days_past_due > 0){
                    invoice_status = days_past_due + " DAY"+(days_past_due>1?"S":"")+" PAST DUE";
                } else if(amount_due > 0){
                    invoice_status = "UNPAID";
                } else {
                    invoice_status = "PAID";
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
        return save(false);
    }
    
    public final Long save(boolean save_details){
        Long result = null;
        Connection con_ft = ApiCommon.getConnection();
        result = save(con_ft, save_details);
        Connect.close(con_ft);
        return result;
    }
    
    public final Long save(Connection con_ft){
        return save(con_ft, false);
    }
    
    public final Long save(Connection con_ft, boolean save_details){
        Long result = null;
        
        Savepoint save_point = Connect.startTransaction(con_ft);
        
        boolean new_record = id == null;
        
        PreparedStatement pstmt = null;
        if(block_save){
            last_error = "This invoice is not able to be saved.";
        } else if(new_record){
            // Inserting new record
            try {
                pstmt = con_ft.prepareStatement(""
                        + "INSERT INTO invoices "
                        + "  (club_invoicing_id, club_invoicing_rule_id, terms_id, bill_to, notes, purchase_order, "
                        + "    due_date, sent_to_club, paid, voided, date) "
                        + "  VALUES"
                        + "  (?, ?, ?, ?, ?, ?, "
                        + "   ?, ?, ?, ?, ?)");
                int i = 1;
                pstmt.setLong(i++, club_invoicing_id);
                pstmt.setLong(i++, club_invoicing_rule_id);
                pstmt.setLong(i++, terms_id);
                pstmt.setString(i++, bill_to);
                pstmt.setString(i++, notes);
                pstmt.setString(i++, purchase_order);
                pstmt.setString(i++, due_date);
                pstmt.setString(i++, sent_to_club);
                pstmt.setString(i++, last_payment_date);
                pstmt.setString(i++, voided);
                pstmt.setString(i++, date);
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
            Invoice test = new Invoice(id, con_ft);
            if(test.last_error == null){
                if(test.voided != null && save_details){
                    last_error = "Voided invoices cannot be modified.";
                //} else if(test.sent_to_club != null && save_details){
                //    last_error = "Sent invoices cannot be modified.";
                } else if(club_invoicing_rule_id == null || (test.club_invoicing_rule_id != null && club_invoicing_rule_id != test.club_invoicing_rule_id)){
                    last_error = "Invoicing rules cannot be changed.";
                }
            } else {
                last_error = "Unable to update Invoice #"+id+"; "+ test.last_error;
            }
            if (last_error == null) {
                try {
                    pstmt = con_ft.prepareStatement(""
                            + "UPDATE invoices "
                            + " SET"
                            + "  club_invoicing_id = ?,"
                            + "  club_invoicing_rule_id = ?,"
                            + "  terms_id = ?,"
                            + "  bill_to = ?,"
                            + "  notes = ?,"
                            + "  purchase_order = ?,"
                            + "  due_date = ?,"
                            + (save_details ? "" : "  sent_to_club = ?,") // these don't get set when saving details
                            + (save_details ? "" : "  voided = ?,") // these don't get set when saving details
                            + (save_details ? "" : "  paid = ?,") // these don't get set when saving details
                            + "  date = ?"
                            + " WHERE id = ?"
                            + "  AND club_invoicing_id = ?");
                    int i = 1;
                    pstmt.setLong(i++, club_invoicing_id);
                    pstmt.setLong(i++, club_invoicing_rule_id);
                    pstmt.setLong(i++, terms_id);
                    pstmt.setString(i++, bill_to);
                    pstmt.setString(i++, notes);
                    pstmt.setString(i++, purchase_order);
                    pstmt.setString(i++, due_date);
                    if (!save_details) {
                        pstmt.setString(i++, sent_to_club);
                        pstmt.setString(i++, voided);
                        pstmt.setString(i++, last_payment_date);
                    }
                    pstmt.setString(i++, date);

                    pstmt.setLong(i++, id);
                    pstmt.setLong(i++, club_invoicing_id);

                    pstmt.executeUpdate();

                    result = id;

                    loadById(id, con_ft); // Refresh results

                } catch (Exception e) {
                    Connect.logError(className() + ".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_update, classNameLower(), e.toString());
                } finally {
                    Connect.close(pstmt);
                }
            }

        }
        
        
        // Save details
        if(last_error == null && save_details){
            if(details == null || details.isEmpty()){
                // Require at least one detail.
                last_error = "You must supply at least one "+classNameLower()+" detail.";
            } else {
                // Try to save each detail
                List<Long> detail_ids = new ArrayList<Long>();
                for(InvoiceDetail detail: details){
                    detail.club_invoicing_id = club_invoicing_id;
                    detail.invoice_id = id;
                    Long detail_id = detail.save(con_ft);
                    if(detail.last_error != null){
                        last_error = detail.last_error;
                        break;
                    } else if (detail_id == null){
                        last_error = String.format("Unknown error saving %1s details", classNameLower());
                        break;
                    }
                    detail_ids.add(detail_id);
                }
                if (last_error == null) {
                    // Delete any details that we didn't save. 
                    // (details not in the list must have been deleted.)
                    try {
                        pstmt = con_ft.prepareStatement(""
                                + "DELETE FROM invoice_details "
                                + " WHERE invoice_id = ? "
                                + "  AND id NOT IN ("+ StringUtils.join(detail_ids, ',') +") ");

                        int i = 1;
                        pstmt.setLong(i++, id);

                        pstmt.executeUpdate();
                        
                    } catch (SQLException e) {
                        if (ApiCommon.isValidationConstraint(e)) {
                            last_error = String.format(ApiConstants.error_delete_bound, "Invoice Detail", e.toString());
                        } else {
                            Connect.logError(className() + ".save (delete details): Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                            last_error = String.format(ApiConstants.error_delete, "Invoice Detail", e.toString());
                        }
                    } finally {
                        Connect.close(pstmt);
                    }
                }
            }
        }
        
        if(last_error != null){
            // We encountered an error.  Rollback any changes.
            Connect.cancelTransaction(con_ft, save_point);
            if(new_record){
                // Clear any ID we may have created
                id = null;
            }
        } else {
            // Looks good.  
            Connect.commitTransaction(con_ft, save_point);
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
            Invoice testInv = new Invoice(id);
            if (testInv.last_error != null) {
                last_error = String.format(ApiConstants.error_delete, classNameLower(), testInv.last_error);
            } else if (testInv.sent_to_club != null) {
                last_error = String.format(ApiConstants.error_delete, classNameLower(), "Invoice #"+id+" already sent to club.");
            } else {
                // Update existing record
                try {
                    pstmt = con_ft.prepareStatement(""
                            + "DELETE FROM invoices "
                            + " WHERE id = ? ");

                    int i = 1;
                    pstmt.setLong(i++, id);

                    pstmt.executeUpdate();

                    result = true;

                } catch (SQLException e) {
                    if (ApiCommon.isValidationConstraint(e)) {
                        last_error = String.format(ApiConstants.error_delete_bound, classNameLower(), e.toString());
                    } else {
                        Connect.logError(className() + ".delete: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                        last_error = String.format(ApiConstants.error_delete, classNameLower(), e.toString());
                    }
                } finally {
                    Connect.close(pstmt);
                }
            }
        }

        return result;
    }
    
    public static String setVoided(List<Invoice> records){
        Connection con_ft = ApiCommon.getConnection();
        String result = setVoided(records, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static String setVoided(List<Invoice> records, Connection con_ft){
        Savepoint save_point = Connect.startTransaction(con_ft);
        for (Invoice record : records) {
            record.setVoided(con_ft);
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
    
    public final boolean setVoided(){
        Connection con_ft = ApiCommon.getConnection();
        boolean result = setVoided(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final boolean setVoided(Connection con_ft){
        boolean result = false;
        
        PreparedStatement pstmt = null;
        
        if(id == null){
            // Can't void a record with a null id
            last_error = String.format(ApiConstants.error_voiding_null_id, classNameLower(), className());
            
        } else {
            
            Invoice testInv = new Invoice(id);
            if (testInv.last_error != null) {
                last_error = String.format(ApiConstants.error_update, classNameLower(), testInv.last_error);
            } else if (testInv.last_payment_date != null && voided != null) {
                last_error = String.format(ApiConstants.error_update, classNameLower(), "Cannot mark paid Invoice #" + id + " as void.");
            } else if (testInv.voided != null && voided != null){
                result = true; // already voided.
            } else {

                // Update existing record
                try {
                    pstmt = con_ft.prepareStatement(""
                            + "UPDATE invoices "
                            + " SET voided = ? "
                            + " WHERE id = ? ");

                    int i = 1;
                    ApiCommon.setOrNull(i++, pstmt, voided);
                    pstmt.setLong(i++, id);

                    pstmt.executeUpdate();

                    result = true;

                } catch (SQLException e) {
                    Connect.logError(className() + ".setVoided: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_voiding, classNameLower(), e.toString());
                } finally {
                    Connect.close(pstmt);
                }
            }
        }

        return result;
    }
    
    public static String sendToClub(List<Invoice> records){
        Connection con_ft = ApiCommon.getConnection();
        String result = sendToClub(records, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static String sendToClub(List<Invoice> records, Connection con_ft){
        Savepoint save_point = Connect.startTransaction(con_ft);
        for (Invoice record : records) {
            record.sendToClub(con_ft);
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
    
    public final boolean sendToClub(){
        Connection con_ft = ApiCommon.getConnection();
        boolean result = sendToClub(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final boolean sendToClub(Connection con_ft){
        boolean result = false;
        
        PreparedStatement pstmt = null;
        
        if(id == null){
            // Can't send  a record with a null id
            last_error = "Unable to send record to invoice to club -- no invoice record given.";
            
        } else {
            Invoice test = new Invoice(id,con_ft);
            if(test.last_error != null){
                last_error = test.last_error;
                return false;
            } else if(test.voided != null){
                // Can't send a voided invoice
                last_error = "Unable to send a voided invoice #"+id+" to club.";
            } else if(test.sent_to_club != null){
                // Already sent to club
                return true;
            } else {
                // Update existing record
                sent_to_club = timeUtil.getDbDateTime();
                if(sent_to_club == null){
                    last_error = String.format(ApiConstants.error_sending_to_club, classNameLower(), "DB date/time error.");
                } else {
                    try {
                        pstmt = con_ft.prepareStatement(""
                                + "UPDATE invoices "
                                + " SET sent_to_club = ? "
                                + " WHERE id = ? ");

                        int i = 1;

                        ApiCommon.setOrNull(i++, pstmt, sent_to_club);
                        pstmt.setLong(i++, id);
                        pstmt.executeUpdate();

                        result = true;

                    } catch (SQLException e) {
                        Connect.logError(className() + ".sendToClub: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                        last_error = String.format(ApiConstants.error_sending_to_club, classNameLower(), e.toString());
                    } finally {
                        Connect.close(pstmt);
                    }
                }
            }
        }

        return result;
    }
    
    public static String sendEmailToClub(List<Invoice> records){
        Connection con_ft = ApiCommon.getConnection();
        String result = sendEmailToClub(records, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static String sendEmailToClub(List<Invoice> records, Connection con_ft){
        List<String> errors = new ArrayList<String>();
        String subject_template = Invoice.getInvoiceSubjectTemplate(con_ft);
        String message_template = Invoice.getInvoiceEmailTemplate(con_ft);
        for (Invoice record : records) {
            record.sendEmailToClub(subject_template, message_template, con_ft);
            if (record.last_error != null) {
                // We encountered an error.
                // Just keep going... (for now)
                errors.add(record.last_error);
            }
        }
        // All good.  Commit the transaction.
        if(errors.isEmpty()){
            return null;
        } else {
            return StringUtils.join(errors, "; ");
        }
    }
    
    
    public static String getInvoiceEmailTemplate(){
        Connection con_ft = ApiCommon.getConnection();
        String result = getInvoiceEmailTemplate(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static String getInvoiceEmailTemplate(Connection con_ft){
        // Get email message template
        return ForeTeesSetting.getTextValue("invoicing_email_template", ApiConstants.invoicing_email_template, true, con_ft);
    }
    
    public static String getInvoiceSubjectTemplate(){
        Connection con_ft = ApiCommon.getConnection();
        String result = getInvoiceEmailTemplate(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static String getInvoiceSubjectTemplate(Connection con_ft){
        // Get message subject
        return ForeTeesSetting.getTextValue("invoicing_email_subject", ApiConstants.invoicing_email_subject, true, con_ft)
                .replaceAll("[\n\r]", " ").replaceAll("[\n\r]+", " ");
    }
    
    
    public final boolean sendEmailToClub(){
        return sendEmailToClub(Invoice.getInvoiceSubjectTemplate(), Invoice.getInvoiceEmailTemplate());
    }
    
    public final boolean sendEmailToClub(Connection con_ft){
        return sendEmailToClub(Invoice.getInvoiceSubjectTemplate(con_ft), Invoice.getInvoiceEmailTemplate(con_ft), con_ft);
    }
    
    public final boolean sendEmailToClub(String subject_template, String message_template){
        Connection con_ft = ApiCommon.getConnection();
        boolean result = sendEmailToClub(subject_template, message_template, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final boolean sendEmailToClub(String subject_template, String message_template, Connection con_ft){
        boolean result = false;
        
        PreparedStatement pstmt = null;
        
        //DateTimeZone ft_tz = timeUtil.getServerTimeZone();
        
        if(id == null){
            // Can't send  a record with a null id
            last_error = "Unable to send invoice email to club -- no invoice record given.";
            
        } else if(voided != null){
            // Can't send a voided invoice
            last_error = "Unable to send a voided invoice #"+id+" to club.";
        } else {
            Invoice invoice = new Invoice(id);
            sent_email_to_club = timeUtil.getDbDateTime();
            if(invoice.last_error != null){
                last_error = invoice.last_error;
                return false;
            } else if (sent_email_to_club == null) {
                last_error = String.format(ApiConstants.error_sending_to_club, classNameLower(), "DB date/time error.");
            } else {
                try {
                    // Send an email:
                    
                    // Build path for API call to generate PDF from Invoice HTML.
                    StringBuilder url = new StringBuilder();
                    url.append(ProcessConstants.THIS_SERVER_BASE_URL);
                    url.append(ProcessConstants.SERVLET_PATH);
                    url.append("API?s_sdbg=on&"); // wkhtmltopdf doesn't like the compiled scripts for some reason.  Use s_sdbg=on to force uncompiled scripts until I figure it out.
                    url.append(ApiConstants.parameter_command);
                    url.append("=invoiceGetHtml&id=");
                    url.append(id);
                    url.append("&");
                    url.append(ApiConstants.parameter_auth_token);
                    url.append("=");
                    // Authenticate as the system account (Be very carfull when using the system account token.  It has full acccess.  Do not give it out!)
                    url.append(URLEncoder.encode(ApiConstants.system_account_auth_token, "UTF-8"));
                    String file_name = "foretees_invoice_" + id + "_" + timeUtil.getDateForFileName() + ".pdf";
                    // Download an HTML invoice and convert it to PDF 
                    // (Much of the Invoice HTML is created using client-side javascript.
                    //  ExportFile's loadPdfByUrl uses wkhtmltopdf to render the page as a client would, 
                    //  and then converts the render to a PDF.)
                    ExportFile pdf = new ExportFile().loadPdfByUrl(url.toString(), file_name);
                    if(pdf.last_error != null){
                        last_error = pdf.last_error;
                        return false;
                    }
                    
                    // Create mime attachment
                    MimeBodyPart bodyPart = new MimeBodyPart();
                    DataSource ds = new ByteArrayDataSource(pdf.byte_data, pdf.media_type);
                    bodyPart.setDataHandler(new DataHandler(ds));
                    bodyPart.setDisposition("attachment; filename=\"" + pdf.file_name + "\"");
                    bodyPart.setFileName(pdf.file_name);
                    List<BodyPart> attachments = new ArrayList<BodyPart>(Arrays.asList(bodyPart));
                    
                    // Configure the message
                    String date_format = "MM/dd/yyyy";
                    String invoice_date = timeUtil.formatTzDate(invoice.date, date_format);
                    String invoice_due_date = timeUtil.formatTzDate(invoice.due_date, date_format);
                    
                    // Fill in any macros in the message and subject
                    String message, message_pre = message_template
                            .replace("%CLUB%", invoice.club_name)
                            .replace("%RULE%", invoice.club_invoicing_rule_name)
                            .replace("%NAME%", invoice.club_invoicing_name)
                            .replace("%INVOICE_DATE%", invoice_date)
                            .replace("%INVOICE_DUE_DATE%", invoice_due_date)
                            .replace("%INVOICE_NUMBER%", invoice.id.toString())
                            .replace("%INVOICE_AMOUNT%", "$"+String.format(Locale.US,"%,.2f",invoice.total))
                            .replace("%INVOICE_STATUS%", invoice.invoice_status);
                    if(invoice.notes != null && !invoice.notes.trim().isEmpty()){
                        message = message_pre.replace("%NOTES%", "\n"+invoice.notes.trim()+"\n");
                    } else {
                        message = message_pre.replace("%NOTES%", "");
                    }
                    
                    String subject = subject_template
                            .replace("%CLUB%", invoice.club_name)
                            .replace("%RULE%", invoice.club_invoicing_rule_name)
                            .replace("%NAME%", invoice.club_invoicing_name)
                            .replace("%INVOICE_DATE%", invoice_date)
                            .replace("%INVOICE_DUE_DATE%", invoice_due_date)
                            .replace("%INVOICE_NUMBER%", invoice.id.toString())
                            .replace("%INVOICE_AMOUNT%", "$"+String.format(Locale.US,"%,.2f",invoice.total))
                            .replace("%INVOICE_STATUS%", invoice.invoice_status);
                    
                    // Get from address
                    String email_from = ForeTeesSetting.getTextValue("invoicing_email_from", ApiConstants.invoicing_email_from, true, con_ft);
                    
                    // Get pro cc address
                    //ArrayList<String> eaddrProCopy = ApiCommon.parseEmailProRecipientString(ApiConstants.invoicing_email_from);
                    
                    // Get recipients
                    ArrayList<ArrayList<String>> eaddrTo = ApiCommon.parseEmailRecipientString(invoice.club_invoicing_email);
                    String replyTo = email_from;

                    parmEmail emailParm = new parmEmail();
                    emailParm.type = "Invoice";
                    emailParm.subject = subject; // Has to be set here as well as in the method call??
                    emailParm.txtBody = message; // ""
                    emailParm.from = email_from;
                    emailParm.replyTo = replyTo; // ""
                    emailParm.activity_name = "ForeTees";
                    emailParm.activity_id = 0;
                    emailParm.club = invoice.club_db_name;
                    emailParm.user = "proshop"; // Is this the best thing to use?
                    emailParm.message = ""; // ?? txtBody?? message?? help!

                    StringBuffer vCalMsg = new StringBuffer();  // no vCal.  Can this just be null??
                    ArrayList<String> eaddrProCopy = new ArrayList<String>(); // no Pro cc.  Can this just be null??

                    //
                    //  Send the email
                    //
                    result = sendEmail.doSending(eaddrTo, eaddrProCopy, replyTo, subject, message, vCalMsg, 
                            emailParm, invoice.club_db_name, attachments);
                    
                    if(!result){
                        sent_email_to_club = null;
                        last_error = "Email did not send for invoice # "+id+" for unknown reason.";
                    } else {
      
                        // Mark as sent in DB
                        pstmt = con_ft.prepareStatement(""
                                + "UPDATE invoices "
                                + " SET sent_email_to_club = ?, "
                                + "     last_failed_email = NULL, "
                                + "     failed_email_count = 0 "
                                + " WHERE id = ? ");

                        int i = 1;

                        ApiCommon.setOrNull(i++, pstmt, sent_email_to_club);
                        pstmt.setLong(i++, id);
                        pstmt.executeUpdate();

                    result = true;
                    }

                } catch (Exception e) {
                    Connect.logError(className() + ".sendEmailToClub: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_sending_to_club, classNameLower(), e.toString());
                } finally {
                    Connect.close(pstmt);
                }
            }
        }
        
        if(last_error != null){
            try{
                // Mark as failed
                pstmt = con_ft.prepareStatement(""
                        + "UPDATE invoices "
                        + " SET sent_email_to_club = NULL, "
                        + "     last_failed_email = NOW(), "
                        + "     failed_email_count = failed_email_count + 1 "
                        + " WHERE id = ? ");

                int i = 1;
                pstmt.setLong(i++, id);
                pstmt.executeUpdate();
                
            } catch (Exception e){
                Connect.logError(className() + ".sendEmailToClub - set failed: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            }
            Connect.logError(className() + ".sendEmailToClub - Failed sending invoice PDF: Err=" + last_error);
        }

        return result;
    }
    
    public static String setPaid(List<Invoice> records){
        Connection con_ft = ApiCommon.getConnection();
        String result = setPaid(records, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static String setPaid(List<Invoice> records, Connection con_ft){
        Savepoint save_point = Connect.startTransaction(con_ft);
        for (Invoice record : records) {
            record.setPaid(con_ft);
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
    
    public final boolean setPaid(){
        Connection con_ft = ApiCommon.getConnection();
        boolean result = setPaid(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final boolean setPaid(Connection con_ft){
        boolean result = false;
        
        PreparedStatement pstmt = null;
        
        if(id == null){
            // Can't pay for an invoice with a null id
            last_error = "Unable to set payment date -- no invoice record given.";
            
        } else {
            
            Invoice testInv = new Invoice(id);
            if (testInv.last_error != null) {
                last_error = String.format(ApiConstants.error_update, classNameLower(), testInv.last_error);
            } else if (testInv.voided != null && last_payment_date != null) {
                last_error = String.format(ApiConstants.error_update, classNameLower(), "Cannot mark voided Invoice #" + id + " as paid.");
            } else if (testInv.last_payment_date != null && last_payment_date != null ){
                result = true; // already paid.
            } else {
                // Update existing record

                try {
                    pstmt = con_ft.prepareStatement(""
                            + "UPDATE invoices "
                            + " SET paid = ? "
                            + " WHERE id = ? ");

                    int i = 1;

                    ApiCommon.setOrNull(i++, pstmt, last_payment_date);
                    pstmt.setLong(i++, id);
                    pstmt.executeUpdate();

                    result = true;

                } catch (SQLException e) {
                    Connect.logError(className() + ".setPaid: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_sending_to_club, classNameLower(), e.toString());
                } finally {
                    Connect.close(pstmt);
                }
            }
            
        }

        return result;
    }
    
    public final String getHtmlView() {
        
        StringBuilder result = new StringBuilder();
        
        result.append("<div class=\"ftInvContainer\">");
        
        result.append("<div class\"ftInvLogo\"></div>");
        result.append("<div class\"ftInvFtAddress\"></div>");
        
        result.append("<div class\"ftInvBillto ftInvBlock\"><div class=\"ftInvHead\">Bill To:</div><div class=\"ftInvContent\">");
        result.append(bill_to.replaceAll("(\r\n|\n)", "<br />"));
        result.append("</div></div>");
        
        result.append("<div class\"ftInvBillto ftInvBlock\"><div class=\"ftInvHead\">Bill To:</div><div class=\"ftInvContent\">");
        result.append(bill_to.replaceAll("(\r\n|\n)", "<br />"));
        result.append("</div></div>");
        
        return result.toString();
        
    }
    
    public static List<Invoice> getList(){
        return getList(null, null);
    }
    
    public static List<Invoice> getList(Long club_invoicing_id){
        return getList(club_invoicing_id, null);
    }
    
    public static List<Invoice> getListByClubId(Long club_id){
        return getList(null, club_id);
    }
    
    public static List<Invoice> getList(Long club_invoicing_id, Long club_id){
        
        List<Invoice> result = new ArrayList<Invoice>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_ft = ApiCommon.getConnection();
        
        String error = null;
        
        Invoice r = new Invoice();
        
        try {
            int i = 1;
            if(club_invoicing_id != null){
                pstmt = con_ft.prepareStatement(sql_by_club_invoicing_id);
                pstmt.setLong(i++, club_invoicing_id);
            } else if(club_id != null){
                pstmt = con_ft.prepareStatement(sql_by_club_id);
                pstmt.setLong(i++, club_id);
            } else {
                pstmt = con_ft.prepareStatement(sql_all);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new Invoice(rs);
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
    
    
    
    public static List<Invoice> getSentToClub(){
        return getList(null, null);
    }
    
    public static List<Invoice> getSentToClub(Long club_invoicing_id){
        return getSentToClub(club_invoicing_id, null);
    }
    
    public static List<Invoice> getSentToClubByClubId(Long club_id){
        return getSentToClub(null, club_id);
    }
    
    public static List<Invoice> getSentVisibleToProByClubId(Long club_id){
        return getSentToClub(null, club_id, true);
    }
    
    public static List<Invoice> getSentToClub(Long club_invoicing_id, Long club_id){
        return getSentToClub(club_invoicing_id, club_id, false);
    }
    
    private static List<Invoice> getSentToClub(Long club_invoicing_id, Long club_id, boolean only_visible_to_proshop){
        
        List<Invoice> result = new ArrayList<Invoice>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_ft = ApiCommon.getConnection();
        
        String error = null;
        
        Invoice r = new Invoice();
        
        try {
            int i = 1;
            if(club_invoicing_id != null){
                pstmt = con_ft.prepareStatement(sql_sent_to_club_by_club_invoicing_id);
                pstmt.setLong(i++, club_invoicing_id);
            } else if(club_id != null){
                pstmt = con_ft.prepareStatement(sql_sent_to_club_by_club_id+(only_visible_to_proshop?" AND show_to_pro = 1 ":""));
                pstmt.setLong(i++, club_id);
            } else {
                pstmt = con_ft.prepareStatement(sql_sent_to_club);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new Invoice(rs);
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
    
    public static List<Invoice> getUnpaidInvoices(){
        return getUnpaidInvoices(null);
    }
    
    public static List<Invoice> getUnpaidInvoices(Long club_invoicing_id){
        return getUnpaidInvoices(club_invoicing_id, null);
    }
    
    public static List<Invoice> getUnpaidInvoicesByClubId(Long club_id){
        return getUnpaidInvoices(null, club_id);
    }
    
    public static List<Invoice> getUnpaidVisibleToProByClubId(Long club_id){
        return getUnpaidInvoices(null, club_id, true);
    }
    
    public static List<Invoice> getUnpaidInvoices(Long club_invoicing_id, Long club_id){
        return getUnpaidInvoices(null, club_id, false);
    }
    
    private static List<Invoice> getUnpaidInvoices(Long club_invoicing_id, Long club_id, boolean only_visible_to_proshop){
        
        List<Invoice> result = new ArrayList<Invoice>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_ft = ApiCommon.getConnection();
        
        String error = null;
        
        Invoice r = new Invoice();
        
        try {
            int i = 1;
            if (club_invoicing_id != null) {
                pstmt = con_ft.prepareStatement(sql_unpaid_by_club_invoicing_id);
                //Connect.logError(r.className() + ".getUnpaidInvoices: Err=" + "\nSQL:\n"+ApiConstants.sql_invoice_unpaid_by_club_invoicing_id);
                pstmt.setLong(i++, club_invoicing_id);
            } else if (club_id != null) {
                pstmt = con_ft.prepareStatement(sql_unpaid_by_club_id+(only_visible_to_proshop?" AND show_to_pro = 1 ":""));
                //Connect.logError(r.className() + ".getUnpaidInvoices: Err=" + "\nSQL:\n"+ApiConstants.sql_invoice_unpaid_by_club_invoicing_id);
                pstmt.setLong(i++, club_id);
            } else {
                pstmt = con_ft.prepareStatement(sql_unpaid);
                //Connect.logError(r.className() + ".getUnpaidInvoices: Err=" + "\nSQL:\n"+ApiConstants.sql_invoice_unpaid);
            }
            
            
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new Invoice(rs);
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
            Connect.logError(r.className() + ".getUnpaidInvoices: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e) );
        } finally {
            Connect.close(rs, pstmt, con_ft);
        }
 
        if(error != null){
            Connect.logError(r.className() + ".getUnpaidInvoices: Err=" + error);
            return null;
        } else {
            return result;
        }

    }
    
    public static List<Invoice> getPastDueInvoices(){
        return getPastDueInvoices(null);
    }
    
    public static List<Invoice> getPastDueInvoices(Long club_invoicing_id){
        return getPastDueInvoices(club_invoicing_id, null);
    }
    
    public static List<Invoice> getPastDueInvoicesByClubId(Long club_id){
        return getPastDueInvoices(null, club_id);
    }
    
    public static List<Invoice> getPastDueInvoices(Long club_invoicing_id, Long club_id){
        
        List<Invoice> result = new ArrayList<Invoice>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_ft = ApiCommon.getConnection();
        
        String error = null;
        
        Invoice r = new Invoice();
        
        try {
            int i = 1;
            if (club_invoicing_id != null) {
                pstmt = con_ft.prepareStatement(sql_past_due_by_club_invoicing_id);
                //Connect.logError(r.className() + ".getUnpaidInvoices: Err=" + "\nSQL:\n"+ApiConstants.sql_invoice_past_due_by_club_invoicing_id);
                pstmt.setLong(i++, club_invoicing_id);
            } else if (club_id != null) {
                pstmt = con_ft.prepareStatement(sql_past_due_by_club_id);
                //Connect.logError(r.className() + ".getUnpaidInvoices: Err=" + "\nSQL:\n"+ApiConstants.sql_invoice_past_due_by_club_invoicing_id);
                pstmt.setLong(i++, club_id);
            } else {
                pstmt = con_ft.prepareStatement(sql_past_due);
                //Connect.logError(r.className() + ".getUnpaidInvoices: Err=" + "\nSQL:\n"+ApiConstants.sql_invoice_past_due);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new Invoice(rs);
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
            Connect.logError(r.className() + ".getPastDueInvoices: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt, con_ft);
        }
 
        if(error != null){
            Connect.logError(r.className() + ".getPastDueInvoices: Err=" + error);
            return null;
        } else {
            return result;
        }

    }
    
    public static List<Invoice> getUnsentInvoices(){
        return getUnsentInvoices(null);
    }
    
    public static List<Invoice> getUnsentInvoices(Long club_invoicing_id){
        
        List<Invoice> result = new ArrayList<Invoice>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_ft = ApiCommon.getConnection();
        
        String error = null;
        
        Invoice r = new Invoice();
        
        try {
            int i = 1;
            if (club_invoicing_id != null) {
                pstmt = con_ft.prepareStatement(sql_unsent_by_club_invoicing_id);
                pstmt.setLong(i++, club_invoicing_id);
            } else {
                pstmt = con_ft.prepareStatement(sql_unsent);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new Invoice(rs);
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
            Connect.logError(r.className() + ".getUnsentInvoices: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt, con_ft);
        }
 
        if(error != null){
            Connect.logError(r.className() + ".getUnsentInvoices: Err=" + error);
            return null;
        } else {
            return result;
        }

    }
    
    public final List<Invoice> getUnsentEmailInvoices(){
        Connection con_ft = ApiCommon.getConnection();
        List<Invoice> result = getUnsentEmailInvoices(null, false, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static List<Invoice> getUnsentEmailInvoices(Connection con_ft){
        return getUnsentEmailInvoices(null, false, con_ft);
    }
    
    public static List<Invoice> getUnsentEmailInvoices(boolean for_update, Connection con_ft){
        return getUnsentEmailInvoices(null, false, con_ft);
    }
    
    public static List<Invoice> getUnsentEmailInvoices(Integer limit, boolean for_update, Connection con_ft){
        
        List<Invoice> result = new ArrayList<Invoice>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String error = null;
        
        Invoice r = new Invoice();
        
        try {
            int i = 1;
            StringBuilder sql = new StringBuilder();
            sql.append(sql_unsent_email);
            if(limit != null){
                sql.append(" LIMIT ");
                sql.append(limit);
            }
            if(for_update){
                sql.append(" FOR UPDATE ");
            }
            pstmt = con_ft.prepareStatement(sql.toString());
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new Invoice(rs);
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
            Connect.logError(r.className() + ".getUnsentEmailInvoices: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt);
        }
 
        if(error != null){
            Connect.logError(r.className() + ".getUnsentEmailInvoices: Err=" + error);
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
