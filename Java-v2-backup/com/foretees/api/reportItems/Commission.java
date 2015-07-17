/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.reportItems;

import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.Connect;
import com.foretees.common.ProcessConstants;
import com.foretees.common.timeUtil;
import com.foretees.common.getActivity;
import java.sql.*;          // mysql
import java.util.*;

import org.joda.time.*;

/**
 *
 * @author Owner
 * 
 */
public class Commission {
    
    public Long id; // Just an index -- nothing more.
    public Long sales_person_id;
    public String sales_person_name;
    public String start_date;
    public String end_date;
    
    public Integer detail_count; // count of how many invoice details are included in this commission
    
    public float commission_total;
    public float sales_total;
    
    public String last_error;
    
    private final static String sql_select = ""
            + "SELECT sp.id AS sales_person_id, sp.name AS sales_person_name, "
            + "    COUNT(DISTINCT id.id) AS detail_count, "
            + "    SUM(id.quantity * id.rate) AS sales_total,"
            + "    SUM((id.quantity * id.rate) * id.commission) AS commission_total, "
            + "    DATE_FORMAT(MIN(id.commission_paid),'%Y-%m-%d') AS start_date, "
            + "    DATE_FORMAT(MAX(id.commission_paid),'%Y-%m-%d') AS end_date "
            + "  FROM invoice_details AS id "
            + "    INNER JOIN invoices AS i "
            + "      ON i.id = id.invoice_id "
            + "    INNER JOIN users AS sp "
            + "      ON sp.id = id.sales_person_id "
            + "  WHERE i.voided IS NULL "
            + "    AND i.paid IS NOT NULL "
            + "    AND id.commission_paid IS NOT NULL "
            + "    AND id.commission IS NOT NULL "
            + "    AND id.commission > 0 "
            + "    AND id.rate > 0 "
            + "    AND id.quantity > 0 ";
    private final static String sql_by_sales_person_id_grouped_by_sales_person_and_date = sql_select + " AND sales_person_id = ? GROUP BY sp.name, DATE_FORMAT(id.commission_paid,'%Y-%m-%d') DESC ";
    private final static String sql_all_grouped_by_sales_person_and_date_range = sql_select + " GROUP BY sp.name, DATE_FORMAT(id.commission_paid,'%Y-%m-%d') DESC ";
    private final static String sql_by_sales_person_id_and_date_range = sql_select + " AND sales_person_id = ? AND id.commission_paid BETWEEN ? AND ? GROUP BY sp.name, DATE_FORMAT(id.commission_paid,'%Y-%m-%d') DESC ";
    private final static String sql_by_date_range_grouped_by_sales_person_and_date = sql_select + " AND id.commission_paid BETWEEN ? AND ? GROUP BY sp.name, DATE_FORMAT(id.commission_paid,'%Y-%m-%d') DESC ";
    private final static String sql_by_sales_person_grouped_by_date_range = sql_select + " AND sales_person_id = ? GROUP BY DATE_FORMAT(id.commission_paid,'%Y-%m-%d') DESC, sp.name ";
    
    
    //public List<String> debug = new ArrayList<String>();
    
    public Commission(){}
    
    public Commission(ResultSet rs){
        loadFromResultSet(rs);
    }
    

    public final void loadFromResultSet(ResultSet rs) {

        try {

            this.sales_person_id = rs.getLong("sales_person_id");
            this.sales_person_name = rs.getString("sales_person_name");
            this.start_date = rs.getString("start_date");
            this.end_date = rs.getString("end_date");
            this.detail_count = rs.getInt("detail_count");
            this.commission_total = rs.getFloat("commission_total");
            this.sales_total = rs.getFloat("sales_total");

        } catch (Exception e) {
            Connect.logError(className() + ".loadFromResultSet: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = String.format(ApiConstants.error_resultset, classNameLower(), e.toString());
        } finally {
        }

    }

    
    public static List<Commission> getListByDate(Long report_sales_person_id, String report_start_date, String report_end_date){
        Connection con_ft = ApiCommon.getConnection();
        List<Commission> result = getListByDate(con_ft, report_sales_person_id, report_start_date, report_end_date);
        Connect.close(con_ft);
        return result;
    }
    
    
    public static List<Commission> getListByDate(Connection con_ft, Long report_sales_person_id, String report_start_date, String report_end_date){
        
        List<Commission> result = new ArrayList<Commission>();
        
        DateTimeZone ft_tz = timeUtil.getServerTimeZone();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String filtered_start_date = null;
        String filtered_end_date = null;
        String error = null;
        
        Commission r = new Commission();
        
        if(report_start_date == null && report_end_date != null){
            report_start_date = report_end_date;
        } else if(report_end_date == null && report_start_date != null){
            report_end_date = report_start_date;
        }
        
        try {
            
            if(report_start_date != null && report_end_date != null){
                filtered_start_date = timeUtil.getMySqlSOD(report_start_date);
                filtered_end_date = timeUtil.getMySqlEOD(report_end_date);
            }
            
            int i = 1;
            if(report_sales_person_id != null){
                if(filtered_start_date != null){
                    pstmt = con_ft.prepareStatement(sql_by_sales_person_id_and_date_range);
                    pstmt.setLong(i++, report_sales_person_id);
                    pstmt.setString(i++, filtered_start_date);
                    pstmt.setString(i++, filtered_end_date);
                } else {
                    pstmt = con_ft.prepareStatement(sql_by_sales_person_id_grouped_by_sales_person_and_date);
                    pstmt.setLong(i++, report_sales_person_id);
                }
            } else {
                if(filtered_start_date != null){
                    pstmt = con_ft.prepareStatement(sql_by_date_range_grouped_by_sales_person_and_date);
                    pstmt.setString(i++, filtered_start_date);
                    pstmt.setString(i++, filtered_end_date);
                } else {
                    pstmt = con_ft.prepareStatement(sql_all_grouped_by_sales_person_and_date_range);
                }
            }
            
            rs = pstmt.executeQuery();
            
            i = 0;
            
            while(rs.next()){
                r = new Commission(rs);
                if(r.sales_person_id != null && r.last_error == null){
                    r.id = (long)i;
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
            Connect.logError(r.className() + ".getListByDate: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt);
        }
 
        if(error != null){
            Connect.logError(r.className() + ".getListByDate: Err=" + error);
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
