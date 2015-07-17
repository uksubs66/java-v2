/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.records;

import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
//import com.foretees.common.ArrayUtil;
import com.foretees.common.Connect;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
//import com.foretees.common.reqUtil;
import com.foretees.common.timeUtil;
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
public class ClubInvoicingRule {
    
    public Long id;
    public Long club_id; // read only
    public Long club_invoicing_id;
    public Long interval_id;
    public Long terms_id;
    public String name;
    public Long next_date;
    public Long last_run; // read only
    public Integer run_count; // read only
    public Boolean auto_send;
    public Boolean disabled;
    public Long updated;
    
    public List<ClubInvoicingRuleDetail> rule_details = new ArrayList<ClubInvoicingRuleDetail>();
    
    public String last_error;
    public String continue_prompt; // need a more elegant way of doing this. this will change!!
    
    
    // Club Invoicing Rule 
    private final static String sql_select = ""
            + "SELECT ci.club_id, cir.*, MAX(i.date) AS last_run, COUNT(DISTINCT i.id) AS run_count "
            //+ "    GROUP_CONCAT(cird.id ORDER BY ii.name SEPARATOR ',') AS club_invoicing_rule_detail_ids, "
            //+ "    GROUP_CONCAT(cird.invoice_item_id ORDER BY ii.name SEPARATOR ',') AS club_invoicing_rule_detail_invoice_item_ids, "
            //+ "    GROUP_CONCAT(cird.tax_group_id ORDER BY ii.name SEPARATOR ',') AS club_invoicing_rule_detail_tax_group_ids, "
            //+ "    GROUP_CONCAT(IFNULL(cird.sales_person_id,'null') ORDER BY ii.name SEPARATOR ',') AS club_invoicing_rule_detail_sales_person_ids, "
            //+ "    GROUP_CONCAT(CONCAT('\"',REPLACE(ii.name,'\"','\"\"'),'\"') ORDER BY ii.name SEPARATOR ',') as club_invoicing_rule_names, "
            //+ "    GROUP_CONCAT(CONCAT('\"',REPLACE(cird.invoicing_rule_type_data,'\"','\"\"'),'\"') ORDER BY ii.name SEPARATOR ',') as club_invoicing_rule_detail_type_data, "
            //+ "    GROUP_CONCAT(cird.commission ORDER BY ii.name SEPARATOR ',') AS club_invoicing_rule_detail_commissions, "
            //+ "    GROUP_CONCAT(cird.commission_on_first ORDER BY ii.name SEPARATOR ',') AS club_invoicing_rule_detail_commission_on_firsts, "
            //+ "    GROUP_CONCAT(cird.disabled ORDER BY ii.name SEPARATOR ',') AS club_invoicing_rule_detail_disables, "
            //+ "    GROUP_CONCAT(UNIX_TIMESTAMP(cird.updated) ORDER BY ii.name SEPARATOR ',') AS club_invoicing_rule_detail_updates "
            + "  FROM club_invoicing_rules AS cir "
            + "    LEFT JOIN club_invoicing AS ci "
            + "      ON ci.id = cir.club_invoicing_id "
            + "    LEFT JOIN club_invoicing_rule_details AS cird "
            + "      ON cird.club_invoicing_rule_id = cir.id "
            //+ "    LEFT JOIN invoice_items ii "
            //+ "      ON ii.id = cird.invoice_item_id "
            + "    LEFT JOIN invoice_details AS id "
            + "      ON id.club_invoicing_rule_detail_id = cird.id "
            + "    LEFT JOIN invoices AS i "
            + "      ON i.id = id.invoice_id ";
    private final static String sql_aggregate = " GROUP BY cir.name ";
    private final static String sql_all = sql_select + sql_aggregate;
    private final static String sql_by_id = sql_select + " WHERE cir.id = ? " + sql_aggregate;
    private final static String sql_ready_to_run = sql_select + " WHERE cir.disabled <> 1 AND cir.next_date <= NOW() " + sql_aggregate;
    private final static String sql_by_club_invoicing_id = sql_select + " WHERE cir.club_invoicing_id = ? " + sql_aggregate;
    private final static String sql_by_name = sql_select + " WHERE cir.club_invoicing_id = ? AND cir.name = ? " + sql_aggregate;


    public ClubInvoicingRule(){}; // Empty parm
    
    /*
    public ClubInvoicingRule(Long id, long club_invoicing_id, long interval_id, String name, 
            Long next_date, boolean auto_send, boolean disabled){
        this.id = id;
        this.club_invoicing_id = club_invoicing_id;
        this.interval_id = interval_id;
        this.name = name;
        this.next_date = next_date;
        this.auto_send = auto_send;
        this.disabled = disabled;
    }
     * 
     */
    
    public ClubInvoicingRule(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public ClubInvoicingRule(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public ClubInvoicingRule(long id){
        Connection con_ft = ApiCommon.getConnection();
        loadById(id, con_ft);
        Connect.close(con_ft);
    }
    
    public final Long loadById(long id, Connection con_ft){
        return loadById(id, con_ft, false);
    }
    
    public final Long loadById(long id, Connection con_ft, boolean for_update){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con_ft.prepareStatement(sql_by_id + (for_update?" FOR UPDATE ":""));
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
    
    public final Long loadByName(String name, Long club_invoicing_id, Connection con_ft){
        return loadByName(name, club_invoicing_id, con_ft, false);
    }
    
    public final Long loadByName(String name, Long club_invoicing_id, Connection con_ft, boolean skip_loading){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con_ft.prepareStatement(sql_by_name);

            pstmt.setLong(1, club_invoicing_id);
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
                this.club_invoicing_id = rs.getLong("club_invoicing_id");
                this.club_id = rs.getLong("club_id");
                this.interval_id = rs.getLong("interval_id");
                this.terms_id = rs.getLong("terms_id");
                this.name = rs.getString("name");
                this.next_date = rs.getTimestamp("next_date").getTime();
                String slast_run = rs.getString("last_run");
                if(slast_run != null){
                    last_run = timeUtil.getServerUnixTimeFromDb(slast_run);
                }
                this.run_count = rs.getInt("run_count");
                this.auto_send = rs.getBoolean("auto_send");
                this.disabled = rs.getBoolean("disabled");
                this.updated = rs.getTimestamp("updated").getTime();
                
                result = id;
                
                //rule_details = new ArrayList<ClubInvoicingRuleDetail>();
                /*
                if(rs.getString("club_invoicing_rule_detail_ids") != null){
                    List<String> club_invoicing_rule_detail_ids = ArrayUtil.parseCsvLine(new StringReader(rs.getString("club_invoicing_rule_detail_ids")));
                    List<String> club_invoicing_rule_detail_invoice_item_ids = ArrayUtil.parseCsvLine(new StringReader(rs.getString("club_invoicing_rule_detail_invoice_item_ids")));
                    List<String> club_invoicing_rule_detail_tax_group_ids = ArrayUtil.parseCsvLine(new StringReader(rs.getString("club_invoicing_rule_detail_tax_group_ids")));
                    List<String> club_invoicing_rule_detail_sales_person_ids = ArrayUtil.parseCsvLine(new StringReader(rs.getString("club_invoicing_rule_detail_sales_person_ids")));
                    
                    List<String> club_invoicing_rule_detail_rule_data = ArrayUtil.parseCsvLine(new StringReader(rs.getString("club_invoicing_rule_detail_rule_data")));
                    List<String> club_invoicing_rule_detail_commissions = ArrayUtil.parseCsvLine(new StringReader(rs.getString("club_invoicing_rule_detail_commissions")));
                    List<String> club_invoicing_rule_detail_commission_on_firsts = ArrayUtil.parseCsvLine(new StringReader(rs.getString("club_invoicing_rule_detail_commission_on_firsts")));
                    List<String> club_invoicing_rule_detail_disables = ArrayUtil.parseCsvLine(new StringReader(rs.getString("club_invoicing_rule_detail_disables")));
                    List<String> club_invoicing_rule_detail_updates = ArrayUtil.parseCsvLine(new StringReader(rs.getString("club_invoicing_rule_detail_updates")));
                    
                    List<String> club_invoicing_rule_names = ArrayUtil.parseCsvLine(new StringReader(rs.getString("club_invoicing_rule_names")));
                    
                    for(int i = 0; i < club_invoicing_rule_detail_ids.size(); i++){
                        rule_details.add(new ClubInvoicingRuleDetail(
                                Long.parseLong(club_invoicing_rule_detail_ids.get(i)),
                                id,
                                Long.parseLong(club_invoicing_rule_detail_invoice_item_ids.get(i)),
                                Long.parseLong(club_invoicing_rule_detail_tax_group_ids.get(i)),
                                ApiCommon.parseLongOrNull(club_invoicing_rule_detail_sales_person_ids.get(i)),
                                club_invoicing_rule_detail_rule_data.get(i),
                                Float.parseFloat(club_invoicing_rule_detail_commissions.get(i)),
                                Integer.parseInt(club_invoicing_rule_detail_commission_on_firsts.get(i)),
                                reqUtil.parseBoolean(club_invoicing_rule_detail_disables.get(i), false),
                                Long.parseLong(club_invoicing_rule_detail_updates.get(i)),
                                club_invoicing_rule_names.get(i)
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
    
    private void checkDates(Connection con_ft){
        if(next_date == null){
            last_error = "Next run date cannot be null.";
            return;
        }
        String date_format = "yyyy-MM-dd";
        ClubInvoicingRule test = new ClubInvoicingRule();
        if(id != null){
            test.loadById(id, con_ft, true);
        }
        if (test.last_error != null) {
            last_error = test.last_error;
        //} else if (!updated.equals(test.updated) && !skip_update_check){
            // Updated dates do not match
            //last_error = String.format(ApiConstants.error_update_changed, classNameLower(), name);
            //continue_prompt = String.format(ApiConstants.error_update_changed_prompt, classNameLower(), name);
        } else if (test.last_run != null && timeUtil.getServerUnixTimeFromDb(timeUtil.formatTzDate(next_date, date_format)) < timeUtil.getServerUnixTimeFromDb(timeUtil.formatTzDate(test.last_run, date_format))){
            // Setting rule to run before it was last run
            last_error = timeUtil.formatTzDate(next_date, date_format) 
                    + " is before the last time this rule was run, on " + timeUtil.formatTzDate(test.last_run, date_format) 
                    + "\n\nThis will cause invoices to be generated for periods that may already have been invoiced. ";

            if(timeUtil.getServerUnixTimeFromDb(timeUtil.formatTzDate(next_date, date_format)) < timeUtil.getServerUnixTimeFromDb(timeUtil.formatTzDate(timeUtil.getCurrentUnixTime(), date_format))){
                last_error += "\n\nIf this date is far in the past the rule may generate multiple invoices, depending on your interval setting.\n\n"
                    + "For exmaple; if this rule is set to run monthly, and you set the rule to run 4 months in the past, the rule will run once every hour until it has generated 4 invoices -- one for each month in the past. ";
            }

            continue_prompt = "Are you sure you want this rule to run on "+timeUtil.formatTzDate(next_date, date_format) +"?";
        } else if (test.next_date != null && timeUtil.getServerUnixTimeFromDb(timeUtil.formatTzDate(next_date, date_format)) < timeUtil.getServerUnixTimeFromDb(timeUtil.formatTzDate(test.next_date, date_format))){
            // Setting rule to run before it was last run
            last_error = timeUtil.formatTzDate(next_date, date_format) 
                    + " is before the currently scheduled next run of " + timeUtil.formatTzDate(test.next_date, date_format) + ". ";
                    if(test.run_count > 0){
                        last_error += "\n\nThis may cause invoices to be generated for periods that have already have been invoiced. ";
                    }
                    if(test.last_run != null){
                        last_error += "(This rule was last run on "+timeUtil.formatTzDate(test.last_run, date_format)+") ";
                    } else {
                        last_error += "(This rule has not yet been run.) ";
                    }
            if(timeUtil.getServerUnixTimeFromDb(timeUtil.formatTzDate(next_date, date_format)) < timeUtil.getServerUnixTimeFromDb(timeUtil.formatTzDate(timeUtil.getCurrentUnixTime(), date_format))){
                last_error += "\n\nIf this date is far in the past the rule may generate multiple invoices, depending on your interval setting.\n\n"
                    + "For example; if this rule is set to run monthly, and you set the rule to run 4 months in the past, the rule will run once every hour until it has generated 4 invoices -- one for each month in the past. ";
            }
            continue_prompt = "Are you sure you want this rule to run on "+timeUtil.formatTzDate(next_date, date_format) +"?";
        } else if (((id==null && !disabled) || id!=null ) && timeUtil.getServerUnixTimeFromDb(timeUtil.formatTzDate(next_date, "yyyy-MM-dd")) < timeUtil.getServerUnixTimeFromDb(timeUtil.formatTzDate(timeUtil.getCurrentUnixTime(), date_format))){
            // Setting rule to a time before today
            last_error = "Setting this rule to run on " 
                    + timeUtil.formatTzDate(next_date, date_format) 
                    + " will force it to run today.\n\n"
                    + "If this date is far in the past the rule may generate multiple invoices, depending on your interval setting.\n\n"
                    + "For example; if this rule is set to run monthly, and you set the rule to run 4 months in the past, the rule will run once every hour until it has generated 4 invoices -- one for each month in the past. ";
            continue_prompt = "Are you sure you want this rule to run today?";
        } else if (((id==null && !disabled) || id!=null ) && timeUtil.getServerUnixTimeFromDb(timeUtil.formatTzDate(next_date, date_format)) == timeUtil.getServerUnixTimeFromDb(timeUtil.formatTzDate(timeUtil.getCurrentUnixTime(), date_format))){
            // Setting rule to today
            last_error = "Setting this rule to run on " 
                    + timeUtil.formatTzDate(next_date, date_format) 
                    + " will force it to run today. ";
            continue_prompt = "Are you sure you want this rule to run today?";
        }
        
    }
    
    public final Long save(){
        return save(false);
    }
    
    public final Long save(boolean skip_update_check){
        Long result = null;
        Connection con_ft = ApiCommon.getConnection();
        result = save(con_ft, skip_update_check);
        Connect.close(con_ft);
        return result;
    }
    
    public final Long save(Connection con_ft){
        return save(con_ft, false);
    }
    
    public final Long save(Connection con_ft, boolean skip_update_check){
        Long result = null;

        if (name == null || name.trim().isEmpty()) {
            last_error = ApiConstants.error_empty_name;
        }
        PreparedStatement pstmt = null;
        Savepoint savepoint = Connect.startTransaction(con_ft);
        if (last_error == null && !skip_update_check) {
            this.checkDates(con_ft);
        }
        if (last_error == null && id == null) {
            // Inserting new record
            try {
                pstmt = con_ft.prepareStatement(""
                        + "INSERT INTO club_invoicing_rules "
                        + "  (club_invoicing_id, interval_id, terms_id, name, next_date, auto_send, disabled) "
                        + "  VALUES"
                        + "  (?, ?, ?, ?, ?, ?, ?)");
                int i = 1;
                pstmt.setLong(i++, club_invoicing_id);
                pstmt.setLong(i++, interval_id);
                pstmt.setLong(i++, terms_id);
                pstmt.setString(i++, name);
                pstmt.setTimestamp(i++, new Timestamp(next_date));
                pstmt.setBoolean(i++, auto_send);
                pstmt.setBoolean(i++, disabled);
                pstmt.executeUpdate();

                result = Connect.getLastInsertId(con_ft);

                id = result;

                loadById(id, con_ft); // Refresh results

            } catch (Exception e) {
                if (loadByName(name, club_invoicing_id, con_ft, true) != null) {
                    last_error = String.format(ApiConstants.error_update_duplicate_name, classNameLower(), name);
                } else {
                    Connect.logError(className() + ".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_insert, classNameLower(), e.toString());
                }
            } finally {
                Connect.close(pstmt);
            }
        } else if (last_error == null) {
            // Update existing record
            try {
                pstmt = con_ft.prepareStatement(""
                        + "UPDATE club_invoicing_rules "
                        + " SET"
                        + "  interval_id = ?,"
                        + "  terms_id = ?,"
                        + "  name = ?,"
                        + "  next_date = ?, "
                        + "  auto_send = ?, "
                        + "  disabled = ? "
                        + " WHERE id = ?"
                        + "  AND club_invoicing_id = ?");
                int i = 1;
                pstmt.setLong(i++, interval_id);
                pstmt.setLong(i++, terms_id);
                pstmt.setString(i++, name);
                pstmt.setTimestamp(i++, new Timestamp(next_date));
                pstmt.setBoolean(i++, auto_send);
                pstmt.setBoolean(i++, disabled);

                pstmt.setLong(i++, id);
                pstmt.setLong(i++, club_invoicing_id);

                pstmt.executeUpdate();

                result = id;

                loadById(id, con_ft); // Refresh results
            } catch (Exception e) {
                Long check_id = loadByName(name, club_invoicing_id, con_ft, true);
                if (check_id != null && !check_id.equals(id)) {
                    last_error = String.format(ApiConstants.error_update_duplicate_name, classNameLower(), name);
                } else {
                    Connect.logError(className() + ".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_update, classNameLower(), e.toString());
                }
            } finally {
                Connect.close(pstmt);
            }
        }
        if (last_error != null) {
            Connect.rollbackTo(con_ft, savepoint);
        } else {
            Connect.commitTransaction(con_ft, savepoint);
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
                        + "DELETE FROM club_invoicing_rules "
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
    
    public final List<ClubInvoicingRuleDetail> loadDetails(){
        Connection con_ft = ApiCommon.getConnection();
        List<ClubInvoicingRuleDetail> result = loadDetails(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final List<ClubInvoicingRuleDetail> loadDetails(Connection con_ft){
        
        rule_details = ClubInvoicingRuleDetail.getList(null, this.id, con_ft);
        if(rule_details == null){
            last_error = "Unknown error loading rule details for rule.";
        }
        return rule_details;
        
    }
    
    // Generate invoice based on list of rules
    
    public static List<Invoice> runReady(){
        List<Invoice> result = null;
        Connection con_ft = ApiCommon.getConnection();
        Savepoint save_point = Connect.startTransaction(con_ft);
        List<ClubInvoicingRule> rules = ClubInvoicingRule.getReadyToRun(con_ft, true);
        result = run(rules, con_ft);
        Connect.commitTransaction(con_ft, save_point);
        Connect.close(con_ft);
        
        // Check over what we did
        List<Invoice> visibile_to_club = new ArrayList<Invoice>();
        List<Invoice> waiting_for_approval = new ArrayList<Invoice>();
        StringBuilder message = new StringBuilder();
        if(!result.isEmpty()){
            // Some of the rules we ran produced results. See what they did, and sort the results for our stats message
            for(Invoice invoice : result){
                if(invoice.sent_to_club != null){
                    // Invoice was generated and marked as "sent to club" (visible in proshop)
                    visibile_to_club.add(invoice);
                } else {
                    // Invoice was generated, but was not marked as "sent to club".  
                    // Someone needs to look it over and send it manually.
                    waiting_for_approval.add(invoice);
                }
            }
            
            // Generate stats message for rules
            message.append("Processed ");
            message.append(rules.size());
            message.append(" rule(s).\n\n");
            
            message.append("Generated ");
            message.append(result.size());
            message.append(" invoice(s).\n\n");
            
            if(!visibile_to_club.isEmpty()){
                message.append("Marked ");
                message.append(visibile_to_club.size());
                message.append(" invoice(s) as visible to club proshop users.\nWill attempt to send these invoices to clubs via email in a few moments.\n\n");
            }
            
            if(!waiting_for_approval.isEmpty()){
                message.append("*** WAITING FOR APPROVAL *** of ");
                message.append(waiting_for_approval.size());
                message.append(" generated invoice(s).\nPlease review \"unsent\" invoices in ForeTees Accounting ASAP.\n\n");
            }
        }
        if(message.length() > 0){
            // Send rule stats email
            emailStatsToForeTees("New Invoices Generated - "+timeUtil.getDbDateTime(), message.toString());
        }
        
        // This should robably be spawned in a different thread
        sendUnsentInvoiceEmails();
        
        return result;
    }
    
    public static List<Invoice> run(List<ClubInvoicingRule> rules){
        return run(rules, timeUtil.getCurrentUnixTime());
    }
    
    public static List<Invoice> run(List<ClubInvoicingRule> rules, long date_time){
        List<Invoice> result = null;
        Connection con_ft = ApiCommon.getConnection();
        result = run(rules, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static List<Invoice> run(List<ClubInvoicingRule> rules, Connection con_ft) {
        return run(rules, timeUtil.getCurrentUnixTime(), con_ft);
    }
    
    public static List<Invoice> run(List<ClubInvoicingRule> rules, long date_time, Connection con_ft) {
        
        List<Invoice> result = new ArrayList<Invoice>();
        
        // Process rules
        Savepoint save_point = Connect.startTransaction(con_ft);
        for (ClubInvoicingRule rule : rules) {
            Invoice invoice = rule.run(date_time, con_ft);
            if (rule.last_error != null) {
                // Error.  Cancel and rollback any changes.
                Connect.cancelTransaction(con_ft, save_point);
                emailStatsToForeTees("Error running Club Invoicing Rules - "+timeUtil.getDbDateTime(), "Encountered error: "+ rule.last_error + "\nwhile running rule ID:"+rule.id);
                Connect.logError(new ClubInvoicingRule().className() + ".run: Err="+ rule.last_error);
                return null;
            } else if (invoice != null) {
                // Invoice was created
                result.add(invoice);
            }
        }
        Connect.commitTransaction(con_ft, save_point);
        
        return result;
        
    }
    
    public static void sendUnsentInvoiceEmails(){
        
        // Send emails for invoices that have been marked as "sent to club" (visible in the proshop)
        //      and have email enabled, but have not yet been marked as "sent email to club".
        
        // Run in a new thread to keep this potentially long task from blocking.
        
        Thread thread = new Thread(){
            @Override
            public void run(){
                Connection con_ft = ApiCommon.getConnection();
                String error = null;
                List<Invoice> sent_email = new ArrayList<Invoice>();
                List<Invoice> error_sending_email = new ArrayList<Invoice>();
                boolean loop = true;
                String subject_template = Invoice.getInvoiceSubjectTemplate(con_ft);
                String message_template = Invoice.getInvoiceEmailTemplate(con_ft);
                while (loop && !Thread.currentThread().isInterrupted()) {
                    Savepoint save_point = Connect.startTransaction(con_ft);
                    // Load 5 at a time since we're locking these for update and emails can take time to send
                    List<Invoice> un_emailed_invoices = Invoice.getUnsentEmailInvoices(5, true, con_ft);
                    if (un_emailed_invoices == null) {
                        //Encountered issue.
                        error = "*** Unkown error encountered while trying to send invoice emails to clubs. ***\nCannot continue sending email.\nWill try again later, but please report this to your system administrator!\n\n";
                        loop = false;
                    } else if (un_emailed_invoices.isEmpty()) {
                        //No more emails to send.
                        loop = false;
                    } else {
                        for (Invoice invoice : un_emailed_invoices) {
                            invoice.sendEmailToClub(subject_template, message_template, con_ft);
                            if (invoice.last_error != null) {
                                // We encountered an error sending this invoice email. Sort it for later, but keep sending emails.
                                error_sending_email.add(invoice);
                            } else {
                                // Invoice email appears to have sent.
                                sent_email.add(invoice);
                            }
                            if(Thread.currentThread().isInterrupted()){
                                // Our thread has been requested to stop
                                break;
                            }
                        }
                    }
                    Connect.commitTransaction(con_ft, save_point);
                }
                Connect.close(con_ft);
                
                if(Thread.currentThread().isInterrupted()){
                    Connect.logError("ClubInvoicingRule.sendUnsentInvoiceEmails:  Thread Interrupted. Exiting early.");
                }

                // Generate email invoice stats message
                StringBuilder message = new StringBuilder();
                if(error != null){
                    message.append(error);
                }

                if (!error_sending_email.isEmpty()) {
                    error = ""; // Flag this as an error.
                    message.append("ERROR SENDING: ");
                    message.append(error_sending_email.size());
                    message.append(" invoice emails(s).\nPlease review \"un emailed\" invoices in ForeTees Accounting, and report this to your system administrator!\n\n");
                }

                if(!sent_email.isEmpty()){
                    message.append("Sent ");
                    message.append(sent_email.size());
                    message.append(" invoice emails(s).\n\n");
                }

                if(message.length() > 0){
                    // Send rule stats email
                    String subject = (error==null?"Invoice Emails Sent - ":"Errors Encountered Sending Invoice Emails - ") + timeUtil.getDbDateTime();
                    emailStatsToForeTees(subject, message.toString());
                }
            }
        };
        thread.start();     
        
        
    }
    
    public static void emailStatsToForeTees(String subject, String message){
        
        try{
            
            String email_from = ForeTeesSetting.getTextValue("invoicing_email_from", ApiConstants.invoicing_email_from);
            ArrayList<ArrayList<String>> eaddrTo = ApiCommon.parseEmailRecipientString(ForeTeesSetting.getTextValue("invoicing_email_stats", ApiConstants.invoicing_email_stats));
            String club_db_name = "demov4"; // looks like we need to use a club name?
            parmEmail emailParm = new parmEmail();
            emailParm.type = "Invoice";
            emailParm.subject = subject;
            emailParm.txtBody = message;
            emailParm.from = email_from;
            emailParm.replyTo = email_from;
            emailParm.activity_name = "ForeTees";
            emailParm.activity_id = 0;
            emailParm.club = club_db_name;
            emailParm.user = "proshop"; // Is this the best thing to use?
            emailParm.message = "--"; // ?? txtBody?? message??

            StringBuffer vCalMsg = new StringBuffer();  // no vCal.  Can this just be null??
            ArrayList<String> eaddrProCopy = new ArrayList<String>(); // no Pro cc.  Can this just be null??

            //
            //  Send the email
            //
            boolean result = sendEmail.doSending(eaddrTo, eaddrProCopy, email_from, subject, message, vCalMsg, emailParm, club_db_name);
            
        }catch(Exception e){
            
            Connect.logError(new ClubInvoicingRule().className() + ".emailStatsToForeTees: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            
        }
        
    }
    
    // Generate invoice based on this rule
    public final Invoice run(){
        Invoice result = null;
        Connection con_ft = ApiCommon.getConnection();
        result = run(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final Invoice run(Connection con_ft){
        return run(timeUtil.getCurrentUnixTime(), con_ft);
    }
    
    public final Invoice run(long date_time, Connection con_ft){
        
        
        Savepoint save_point = Connect.startTransaction(con_ft);
        
        // Load rule fresh from DB to make sure we're using current data.
        ClubInvoicingRule rule = new ClubInvoicingRule(this.id, con_ft);
        if(rule.last_error != null){
            last_error = rule.last_error;
            Connect.cancelTransaction(con_ft, save_point);
            return null;
        }
        
        rule.loadDetails(con_ft);
        if(rule.last_error != null){
            last_error = rule.last_error;
            Connect.cancelTransaction(con_ft, save_point);
            return null;
        }
        
        if(rule.disabled || rule.rule_details == null || rule.rule_details.isEmpty() || rule.next_date == null || rule.next_date > date_time){
            // This rule isn't ready to run.  Skip it.
            Connect.cancelTransaction(con_ft, save_point);
            return null;
        }
        
        ClubInvoicing club = new ClubInvoicing(this.club_invoicing_id, con_ft);
        if(club.last_error != null){
            last_error = club.last_error;
            Connect.cancelTransaction(con_ft, save_point);
            return null;
        }
        
        
        
        InvoiceTerms terms = new InvoiceTerms(rule.terms_id, con_ft);
        if(terms.last_error != null){
            last_error = terms.last_error;
            Connect.cancelTransaction(con_ft, save_point);
            return null;
        }
        
        DbInterval interval = new DbInterval(rule.interval_id, con_ft);
        if(interval.last_error != null){
            last_error = interval.last_error;
            Connect.cancelTransaction(con_ft, save_point);
            return null;
        }
        
        Invoice invoice = new Invoice();
        invoice.club_invoicing_id = rule.club_invoicing_id;
        invoice.club_invoicing_rule_id = rule.id;
        invoice.details = new ArrayList<InvoiceDetail>();
        invoice.terms_id = rule.terms_id;
        invoice.bill_to = club.address;
        if(club.default_po != null){
            invoice.purchase_order = club.default_po;
        }
        // Set due date of invoice to run date of rule + term days 
        invoice.due_date = timeUtil.getDbDate(timeUtil.getClubDate(null, timeUtil.addUnixTimeDays(rule.next_date, terms.days)));
        invoice.date = timeUtil.getDbDate(timeUtil.getClubDate(null, rule.next_date));
        
        boolean allow_send = (this.auto_send!=null?this.auto_send:false);
        
        for (ClubInvoicingRuleDetail rule_detail : rule.rule_details) {
            InvoiceDetail invoice_detail = rule_detail.run(con_ft);
            if (rule_detail.last_error != null) {
                last_error = rule_detail.last_error;
                Connect.cancelTransaction(con_ft, save_point);
                return null;
            }
            if(invoice_detail != null){
                invoice.details.add(invoice_detail);
                if(!invoice_detail.getChangedFromLast().isEmpty()){
                    allow_send = false;
                }
            }
        }
        
        if(allow_send){
            invoice.sent_to_club = timeUtil.getDbDateTime(); // Allow club to view invoice on proshop side, and allow invoice email to be sent
        }        
        
        if(invoice.details.isEmpty()){
            // No invoice details created.  Skip this rule.
            Connect.cancelTransaction(con_ft, save_point);
            return null;
        }
        
        invoice.save(con_ft, true);
        if(invoice.last_error != null){
            last_error = invoice.last_error;
            Connect.cancelTransaction(con_ft, save_point);
            return null;
        }
        
        invoice.loadById(invoice.id, con_ft);
        if(invoice.last_error != null){
            last_error = invoice.last_error;
            Connect.cancelTransaction(con_ft, save_point);
            return null;
        }
        
        // Invoice worked.  lets advance this rule to the next interval.
        rule.next_date = interval.calculate(rule.next_date);
        if(interval.last_error != null){
            last_error = interval.last_error;
            Connect.cancelTransaction(con_ft, save_point);
            return null;
        }
        // save changes to the rule
        rule.save(con_ft, true);
        if(rule.last_error != null){
            last_error = rule.last_error;
            Connect.cancelTransaction(con_ft, save_point);
            return null;
        }
        
        this.next_date = rule.next_date;
        
        Connect.commitTransaction(con_ft, save_point);        
        return invoice;
        
    }
    
    public static List<ClubInvoicingRule> getList(){
        return getList(null, null);
    }
    
    public static List<ClubInvoicingRule> getList(Long id){
        return getList(id, null);
    }
    
    public static List<ClubInvoicingRule> getListByClubInvoicingId(Long club_invoicing_id){
        return getList(null, club_invoicing_id);
    }
    
    public static List<ClubInvoicingRule> getList(Long id, Long club_invoicing_id){
        
        List<ClubInvoicingRule> result = new ArrayList<ClubInvoicingRule>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_ft = ApiCommon.getConnection();
        
        String error = null;
        
        ClubInvoicingRule r = new ClubInvoicingRule();
        
        try {
            int i = 1;
            if(id != null){
                pstmt = con_ft.prepareStatement(sql_by_id);
                pstmt.setLong(i++, id);
            } else if(club_invoicing_id != null){
                pstmt = con_ft.prepareStatement(sql_by_club_invoicing_id);
                pstmt.setLong(i++, club_invoicing_id);
            } else {
                pstmt = con_ft.prepareStatement(sql_all);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new ClubInvoicingRule(rs);
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
    
    public final List<ClubInvoicingRule> getReadyToRun(){
        List<ClubInvoicingRule> result = null;
        Connection con_ft = ApiCommon.getConnection();
        result = getReadyToRun(con_ft, false);
        Connect.close(con_ft);
        return result;
    }
    
    public final List<ClubInvoicingRule> getReadyToRun(Connection con_ft){
        return getReadyToRun(con_ft, false);
    }
    
    public static List<ClubInvoicingRule> getReadyToRun(Connection con_ft, boolean for_update){
        
        List<ClubInvoicingRule> result = new ArrayList<ClubInvoicingRule>();
        
        //for_update = false; // Set this false when trying to debug uncaught exceptions.
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String error = null;
        
        ClubInvoicingRule r = new ClubInvoicingRule();
        
        try {
            pstmt = con_ft.prepareStatement(sql_ready_to_run + (for_update?" FOR UPDATE ":""));
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new ClubInvoicingRule(rs);
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
            Connect.logError(r.className() + ".getReadyToRun: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt);
        }
 
        if(error != null){
            Connect.logError(r.className() + ".getReadyToRun: Err=" + error);
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
