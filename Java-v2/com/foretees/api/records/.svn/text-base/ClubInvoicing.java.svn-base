/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.records;

//import com.foretees.common.ArrayUtil;
import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.Connect;
//import com.foretees.common.reqUtil;
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
public class ClubInvoicing {
    
    public Long id;
    public Long club_id; // Doesn't change on update
    public String name;
    public String club_name; // Read only
    public String club_code; // Read only
    public String address;
    public String phone;
    public String email;
    public String notes;
    public String default_po;
    public Boolean show_to_pro; // Show invoices on proshop
    public Boolean disabled;
    public Long updated;

    public String last_error;
    
    
    // Club Invoicing
    private final static String sql_select = ""
            + "SELECT ci.*, c.clubname AS club_code, c.fullname AS club_name "
            + "  FROM club_invoicing AS ci "
            + "    LEFT JOIN clubs AS c "
            + "      ON c.id = ci.club_id ";
    private final static String sql_order = " ORDER BY c.clubname, ci.name ";
    private final static String sql_all = sql_select + sql_order;
    private final static String sql_by_id = sql_select + "  WHERE ci.id = ? " + sql_order;
    private final static String sql_by_club_id = sql_select + "  WHERE ci.club_id = ? " + sql_order;
    private final static String sql_by_name = sql_select + "  WHERE ci.club_id = ? AND ci.name = ? " + sql_order;
    private final static String sql_visible_to_proshop_by_club_id = sql_select + "  WHERE ci.club_id = ? AND ci.show_to_pro = 1 " + sql_order;

    

    public ClubInvoicing(){}; // Empty parm
    
    public ClubInvoicing(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public ClubInvoicing(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public ClubInvoicing(long id){
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
    
    public final Long loadByName(String name, Long club_id, Connection con_ft){
        return loadByName(name, club_id, con_ft, false);
    }
    
    public final Long loadByName(String name, Long club_id, Connection con_ft, boolean skip_loading){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con_ft.prepareStatement(sql_by_name);
            pstmt.setLong(1, club_id);
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
                this.club_id = rs.getLong("club_id");
                this.name = rs.getString("name");
                this.club_name = rs.getString("club_name");
                this.club_code = rs.getString("club_code");
                this.address = rs.getString("address");
                this.phone = rs.getString("phone");
                this.email = rs.getString("email");
                this.notes = rs.getString("notes");
                this.default_po = rs.getString("default_po");
                this.show_to_pro = rs.getBoolean("show_to_pro");
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
                        + "INSERT INTO club_invoicing "
                        + "  (club_id, name, address, phone, email, notes, "
                        + "    default_po, show_to_pro, disabled) "
                        + "  VALUES"
                        + "  (?, ?, ?, ?, ?, ?, "
                        + "     ?, ?, ?)");
                int i = 1;
                pstmt.setLong(i++, club_id);
                pstmt.setString(i++, name);
                pstmt.setString(i++, address);
                pstmt.setString(i++, phone);
                pstmt.setString(i++, email);
                pstmt.setString(i++, notes);
                pstmt.setString(i++, default_po);
                pstmt.setBoolean(i++, show_to_pro);
                pstmt.setBoolean(i++, disabled);
                pstmt.executeUpdate();
                
                result = Connect.getLastInsertId(con_ft);
                
                id = result;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                if(loadByName(name, club_id, con_ft, true) != null){
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
                        + "UPDATE club_invoicing "
                        + " SET"
                        + "  club_id = ?,"
                        + "  name = ?,"
                        + "  address = ?,"
                        + "  phone = ?,"
                        + "  email = ?,"
                        + "  notes = ?,"
                        + "  default_po = ?,"
                        + "  show_to_pro = ?,"
                        + "  disabled = ?"
                        + " WHERE id = ?"
                        + "  AND club_id = ?");
                int i = 1;
                pstmt.setLong(i++, club_id);
                pstmt.setString(i++, name);
                pstmt.setString(i++, address);
                pstmt.setString(i++, phone);
                pstmt.setString(i++, email);
                pstmt.setString(i++, notes);
                pstmt.setString(i++, default_po);
                pstmt.setBoolean(i++, show_to_pro);
                pstmt.setBoolean(i++, disabled);
                
                pstmt.setLong(i++, id);
                pstmt.setLong(i++, club_id);

                pstmt.executeUpdate();
                
                result = id;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                Long check_id = loadByName(name, club_id, con_ft, true);
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
                        + "DELETE FROM club_invoicing "
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
    
      
    public static List<ClubInvoicing> getList(){
        return getList(null, null, false);
    }
    
    public static List<ClubInvoicing> getList(Long id){
        return getList(id, null, false);
    }
    
    public static List<ClubInvoicing> getListsByClubId(Long club_id){
        return getList(null, club_id, false);
    }
    
    public static List<ClubInvoicing> getVisibleToProshopByClubId(Long club_id){
        return getList(null, club_id, true);
    }
    
    public static List<ClubInvoicing> getList(Long id, Long club_id){
        return getList(id, club_id, false);
    }
    
    private static List<ClubInvoicing> getList(Long id, Long club_id, boolean only_visible_to_proshop){
        
        List<ClubInvoicing> result = new ArrayList<ClubInvoicing>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Connection con_ft = ApiCommon.getConnection();
        
        String error = null;
        
        ClubInvoicing r = new ClubInvoicing();
        
        try {
            int i = 1;
            if(club_id != null){
                pstmt = con_ft.prepareStatement(only_visible_to_proshop?sql_visible_to_proshop_by_club_id:sql_by_club_id);
                pstmt.setLong(i++, club_id);
            } else if(id != null){
                pstmt = con_ft.prepareStatement(sql_by_id);
                pstmt.setLong(i++, id);
            } else {
                pstmt = con_ft.prepareStatement(sql_all);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new ClubInvoicing(rs);
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
