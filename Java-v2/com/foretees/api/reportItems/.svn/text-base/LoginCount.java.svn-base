/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.reportItems;

import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.ArrayUtil;
import com.foretees.common.Connect;
import com.foretees.common.reqUtil;
import com.foretees.common.timeUtil;
import java.sql.*;          // mysql
//import java.util.UUID;
//import javax.naming.*;
//import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import org.joda.time.*;
/**
 *
 * @author John Kielkopf
 */
public class LoginCount {

    public String club;
    public String date;
    public Integer successful_logins;
    public Integer unsuccessful_logins;
    public String last_error;
    
     
    // Login Counts
    private final static String sql_login_counts = ""
            + "SELECT l.club, "
            + "    DATE_FORMAT(CONVERT_TZ(l.datetime, ?, ?), ?) AS group_date, "
            + "    COUNT(IF(l.success=1,1,NULL)) AS successful_logins, "
            + "    COUNT(IF(l.success=1,NULL,1)) AS unsuccessful_logins "
            + " FROM logins l "
            + " ";
    private final static String sql_login_counts_filter_club_and_date  = " WHERE l.datetime BETWEEN ? AND ? AND l.club = ? ";
    private final static String sql_login_counts_by_date_and_club = sql_login_counts 
            + sql_login_counts_filter_club_and_date
            + " GROUP BY l.club, group_date ";
    private final static String sql_login_counts_average_wrapper_open = ""
            + "SELECT club, group_date, "
            + "  AVG(successful_logins) AS successful_logins, "
            + "  AVG(unsuccessful_logins) AS unsuccessful_logins "
            + "  FROM (";
    private final static String sql_login_counts_average_wrapper_close = ") AS fc ";
    private final static String sql_login_average_by_date_and_club = ""
            + sql_login_counts_average_wrapper_open + sql_login_counts_by_date_and_club + sql_login_counts_average_wrapper_close
            + " GROUP BY club, DATE_FORMAT(group_date, ?) ";

    public LoginCount(){}; // Empty parm
    
    public LoginCount(String club, String date, Integer successful_logins, Integer unsuccessful_logins){
        this.club = club;
        this.date = date;
        this.successful_logins = successful_logins;
        this.unsuccessful_logins = unsuccessful_logins;
    }
    
    public LoginCount(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    private Boolean loadFromResultSet(ResultSet rs){
        
        Boolean result = null;
        try {
                this.club = rs.getString("club");
                this.date = rs.getString("group_date");
                this.successful_logins = rs.getInt("successful_logins");
                this.unsuccessful_logins = rs.getInt("unsuccessful_logins");
                result = true;
        } catch(Exception e) {
            last_error = String.format(ApiConstants.error_resultset, classNameLower(), e.toString());
            Connect.logError(className()+".loadFromResultSet: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            return null;
        }
        return result;
        
    }
    
    
    public static List<LoginCount> getList(String club_code, String start_date, String end_date){
        
        List<LoginCount> result = new ArrayList<LoginCount>();
        
        //String club_code = ApiCommon.getClubCode(club_id);
        Connection con_club = ApiCommon.getConnection(club_code);
        Connection con_ft = ApiCommon.getConnection();
        
        String club_tz_string = timeUtil.getClubTimeZoneId(con_club); 
        String db_tz_string = timeUtil.getClubTimeZoneId("");
        
        DateTimeZone club_tz = timeUtil.getClubTimeZone(club_tz_string);
        DateTimeZone db_tz = timeUtil.getClubTimeZone(db_tz_string);

        Long ts_start_date = timeUtil.getUnixTimeFromDb(club_tz, timeUtil.formatTzDate(club_tz, timeUtil.getUnixTimeFromDb(club_tz, start_date), "yyyy-MM-dd"));
        Long ts_end_date = timeUtil.getUnixTimeFromDb(club_tz, timeUtil.formatTzDate(club_tz, timeUtil.getUnixTimeFromDb(club_tz, end_date), "yyyy-MM-dd '23:59:59'"));
        
        // Convert club relative start and end dates to db relative start and end dates
        String db_start_date = timeUtil.formatTzDate(db_tz, ts_start_date, "yyyy-MM-dd HH:mm:ss");
        String db_end_date = timeUtil.formatTzDate(db_tz, ts_end_date, "yyyy-MM-dd HH:mm:ss");

        int days_between = timeUtil.daysBetween(club_tz, ts_start_date, ts_end_date);
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String error = null;
        
        LoginCount r = new LoginCount();
        
        try {
            pstmt = con_ft.prepareStatement(sql_login_counts_by_date_and_club);
            int i = 1;
            //pstmt.setString(i++, db_tz_string);
            //pstmt.setString(i++, club_tz_string);
            pstmt.setString(i++, "+0:00");
            pstmt.setString(i++, "+0:00");
            pstmt.setString(i++, "%Y-%m-%d");
            pstmt.setString(i++, db_start_date);
            pstmt.setString(i++, db_end_date);
            pstmt.setString(i++, club_code);
            
            rs = pstmt.executeQuery();
            String t_compare;
            i = 0;
            while(rs.next()){
                t_compare = timeUtil.formatTzDate(club_tz, timeUtil.addUnixTimeDays(ts_start_date, i), "yyyy-MM-dd");
                r = new LoginCount(rs);
                if(r.last_error == null){
                    while(!t_compare.equals(r.date) && i < days_between){
                        // Add empty count for days missing from result
                        result.add(new LoginCount(club_code, t_compare, 0, 0));
                        i ++;
                        t_compare = timeUtil.formatTzDate(club_tz, timeUtil.addUnixTimeDays(ts_start_date, i), "yyyy-MM-dd");
                    }
                    result.add(r);
                    
                } else {
                    if (r.last_error != null) {
                        error = r.last_error;
                    } else {
                        error = String.format(ApiConstants.error_unknown_condition, r.classNameLower());
                    }
                }
                i ++;
            }
            for(i = result.size(); i < days_between; i++){
                // Add empty count for days missing from result
                result.add(new LoginCount(club_code, timeUtil.formatTzDate(club_tz, timeUtil.addUnixTimeDays(ts_start_date, i), "yyyy-MM-dd"), 0, 0));
            }
        } catch(Exception e) {
            error = String.format(ApiConstants.error_db_select, r.classNameLower(), e.toString());
            Connect.logError(r.className() + ".getList: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt, con_ft);
            Connect.close(con_club);
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
