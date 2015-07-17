/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.records;

import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.Connect;
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
public class Club {
    
    public Long id;
    public String name;
    public String code;
    
    public Long start_date;
    
    public Integer club_invoicing_count = 0;
    public Boolean club_invoicing_enabled = false;
    
    public Integer unsent_invoices = 0;
    public Integer unpaid_invoices = 0;
    public Integer max_days_past_due = 0;
    
    public String next_billing_date;
    
    public Boolean non_billable;
    
    public Boolean disabled;
    
    public Long updated;
    
    // Extended information from club5 table
    public String club_name;
    public String contact;
    public String email;
    //
    
    public String last_error;
    
    // SQL
    private final static String sql_select = ""
            + "SELECT c.*, COUNT(ci.id) club_invoicing_count, MIN(cir.next_date) AS next_billing_date,"
            + "     MAX(IF(((id.rate*id.quantity) + ((id.rate*id.quantity) * id.tax_rate) - IF(i.paid IS NULL,0,(id.rate*id.quantity) + ((id.rate*id.quantity) * id.tax_rate)) > 0),  IF(i.sent_to_club IS NULL, 0, DATEDIFF(NOW(),i.due_date)), 0)) AS max_days_past_due, "
            + "     COUNT(DISTINCT IF(((id.rate*id.quantity) + ((id.rate*id.quantity) * id.tax_rate) - IF(i.paid IS NULL,0,(id.rate*id.quantity) + ((id.rate*id.quantity) * id.tax_rate)) > 0), IF(i.sent_to_club IS NULL, NULL, i.id), NULL)) AS unpaid_invoices, "
            + "     COUNT(DISTINCT IF(i.sent_to_club IS NULL,i.id,NULL)) AS unsent_invoices "
            + "  FROM clubs AS c "
            + "    LEFT JOIN club_invoicing AS ci "
            + "      ON ci.club_id = c.id "
            + "        AND ci.disabled = 0 "
            + "    LEFT JOIN invoices AS i "
            + "      ON i.club_invoicing_id = ci.id "
            + "        AND i.voided IS NULL "
            + "        AND (i.paid IS NULL OR i.sent_to_club IS NULL) "
            + "    LEFT JOIN invoice_terms AS it "
            + "      ON it.id = i.terms_id "
            + "    LEFT JOIN invoice_details AS id "
            + "      ON id.invoice_id = i.id "
            + "    LEFT JOIN club_invoicing_rules AS cir "
            + "      ON cir.club_invoicing_id = ci.id "
            + "        AND cir.disabled = 0 ";
    private final static String sql_aggregate = " GROUP BY c.inactive, (IF(ci.id IS NULL,1,0)), ci.disabled, c.fullname, c.id ";
    private final static String sql_all = sql_select + sql_aggregate;
    private final static String sql_by_id = sql_select + "  WHERE c.id = ? " + sql_aggregate;
    private final static String sql_by_club_invoicing_id = sql_select + "  WHERE ci.id = ? " + sql_aggregate;
    private final static String sql_by_code = sql_select + "  WHERE c.clubname = ? " + sql_aggregate;
    

    public Club(){}; // Empty parm
    
    public Club(Long id, String name, String code, boolean disabled){
        loadByValue(id, name, code, disabled);
    }
    
    public Club(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public Club(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public Club(long id){
        Connection con_ft = ApiCommon.getConnection();
        loadById(id, con_ft);
        Connect.close(con_ft);
    }
    
    public Club(String club_code){
        Connection con_ft = ApiCommon.getConnection();
        loadByCode(club_code, con_ft);
        Connect.close(con_ft);
    }
    
    public final void loadByValue(Long id, String name, String code, boolean disabled){
        this.id = id;
        this.name = name;
        this.code = code;
        this.disabled = disabled;
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
    
    public final Long loadByCode(String code, Connection con_ft){
        return loadByCode(code, con_ft, false);
    }
    
    public final Long loadByCode(String code, Connection con_ft, boolean skip_loading){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con_ft.prepareStatement(sql_by_code);
            pstmt.setString(1, code.trim());

            rs = pstmt.executeQuery();
            if(rs.next()){
                result = loadFromResultSet(rs, skip_loading);
            } else {
                last_error = "Unable to find "+classNameLower()+": " + code;
            }  
        } catch(Exception e) {
            Connect.logError(className()+".loadByCode: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
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
                this.name = rs.getString("fullname");
                this.code = rs.getString("clubname");
                this.next_billing_date = rs.getString("next_billing_date");
                this.club_invoicing_count = rs.getInt("club_invoicing_count");
                this.disabled = rs.getBoolean("inactive");
                this.non_billable = rs.getBoolean("non_billable");
                this.unsent_invoices = rs.getInt("unsent_invoices");
                this.unpaid_invoices = rs.getInt("unpaid_invoices");
                this.max_days_past_due = rs.getInt("max_days_past_due");
                this.start_date = timeUtil.getClubUnixTimeFromCon(null, rs.getInt("startdate"), 0);
                this.club_invoicing_enabled = club_invoicing_count > 0;
                //this.updated = rs.getTimestamp("updated").getTime();
                result = id;
            }
        } catch(Exception e) {
            last_error = String.format(ApiConstants.error_resultset, classNameLower(), e.toString());
            Connect.logError(className()+".loadFromResultSet: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            return null;
        }
        return result;
        
    }
    
    public final boolean loadExtended(){
        boolean result = false;
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_club = this.clubConnection();
        
        try {
            pstmt = con_club.prepareStatement(ApiCommon.sql_club_parms);

            rs = pstmt.executeQuery();
            if(rs.next()){
                club_name = rs.getString("clubName");
                contact = rs.getString("contact");
                email = rs.getString("email");
                result = true;
            } else {
                last_error = "Unable to load extended club data.";
            }  
        } catch(Exception e) {
            Connect.logError(className()+".loadByCode: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = "Error Loading "+classNameLower()+": " + e.toString();
        } finally {
            Connect.close(rs, pstmt, con_club);
        }
        
        return result;
    }
    
       
    public static List<Club> getList(){
        return getList(null, null);
    }
    
    public static List<Club> getList(Long id){
        return getList(id, null);
    }
    
    public static List<Club> getListByClubInvoicingId(Long club_invoicing_id){
        return getList(null, club_invoicing_id);
    }
    
    public static List<Club> getList(Long id, Long club_invoicing_id){
        
        List<Club> result = new ArrayList<Club>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_ft = ApiCommon.getConnection();
        
        String error = null;
        
        Club r = new Club();
        
        try {
            if(club_invoicing_id != null){
                pstmt = con_ft.prepareStatement(sql_by_club_invoicing_id);
                pstmt.setLong(1, club_invoicing_id);
            } else if(id != null){
                pstmt = con_ft.prepareStatement(sql_by_id);
                pstmt.setLong(1, id);
            } else {
                pstmt = con_ft.prepareStatement(sql_all);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new Club(rs);
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
    
    public final Connection clubConnection(){
        return ApiCommon.getConnection(code);
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
