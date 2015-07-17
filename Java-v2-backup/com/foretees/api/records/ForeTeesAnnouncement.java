/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.records;

import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
//import com.foretees.common.ArrayUtil;
import com.foretees.common.Connect;
//import com.foretees.common.AESencrypt;
//import com.foretees.common.reqUtil;
import com.foretees.common.ProcessConstants;
import com.foretees.common.timeUtil;
import java.sql.*;          // mysql
//import java.util.UUID;
//import javax.naming.*;
//import javax.servlet.http.*;
//import java.io.*;
import java.util.*;
//import org.apache.commons.io.IOUtils;
/**
 *
 * @author John Kielkopf
 */
public class ForeTeesAnnouncement {
    
    public Long id;
    public String title;
    public String file_name; //deprecated
    public String html;
    public String date_start;
    public String date_end;
    
    public String css; // read only
    
    public Boolean golf;
    public Boolean flxrez;
    public Boolean dining;
    public Boolean premier;
    public Boolean ftapp;
    
    public Boolean publish; // show on non-demo clubs
    
    public Boolean is_active; // read only (today is between announcement start and end dates)
    
    public String last_view; // read only
    public String first_view; // read only
    
    public Integer total_views; // read only
    public Integer distinct_user_views; // read only
    public Integer distinct_club_views; // read only
    
    public Boolean is_read; // read only
    
    public Long updated; // read only
    
    public String last_error; // read only
    
    // Used in setting view stats
    private String club_db_name; // read only
    private String username; // read only
    
    
    // SQL
    private final static String sql_select = ""
            + "SELECT a.*, "
            + "    IF(a.sdate <= CAST(DATE_FORMAT(NOW(),'%Y%m%d') AS UNSIGNED INTEGER) AND a.edate >= CAST(DATE_FORMAT(NOW(),'%Y%m%d') AS UNSIGNED INTEGER),1,0) AS is_active, "
            + "    MAX(av.date_time) AS last_view, "
            + "    MIN(av.date_time) AS first_view, "
            + "    COUNT(av.id) AS total_views, "
            + "    COUNT(DISTINCT av.user, av.club) AS distinct_user_views, "
            + "    COUNT(DISTINCT av.club) AS distinct_club_views "
            + "  FROM announcements AS a "
            + "    LEFT JOIN announce_views AS av " // If you need to join more, make sure this (announce_views AS av) is the last join!
            + "      ON av.announce_id = a.announce_id ";
    
    private final static String sql_aggregate = " GROUP BY a.sdate DESC, a.announce_id ";
    private final static String sql_user_views = " AND av.club = ? AND av.user = ? ";
    private final static String sql_date_range = " a.sdate <= ? AND a.edate >= ? ";
    private final static String sql_unread_filter = " HAVING total_views < 1 ";
    
    private final static String sql_all = sql_select + sql_aggregate;
    private final static String sql_by_id = sql_select + " WHERE a.announce_id = ? " + sql_aggregate;
    private final static String sql_by_user_and_id = sql_select + sql_user_views + " WHERE a.announce_id = ? " + sql_aggregate; // Limits counts to user
    private final static String sql_active_by_user = sql_select + sql_user_views + " WHERE " + sql_date_range + sql_aggregate;
    private final static String sql_active_unread_by_user = sql_select + sql_user_views + " WHERE " + sql_date_range + sql_aggregate + sql_unread_filter;
    private final static String sql_golf_active_by_user = sql_select + sql_user_views + " WHERE a.golf = 1 AND " + sql_date_range + sql_aggregate;
    private final static String sql_golf_active_unread_by_user = sql_select + sql_user_views + " WHERE a.golf = 1 AND " + sql_date_range + sql_aggregate + sql_unread_filter;
    private final static String sql_flxrez_active_by_user = sql_select + sql_user_views + " WHERE a.flxrez = 1 AND " + sql_date_range + sql_aggregate;
    private final static String sql_flxrez_active_unread_by_user = sql_select + sql_user_views + " WHERE a.flxrez = 1 AND " + sql_date_range + sql_aggregate + sql_unread_filter;
    private final static String sql_dining_active_by_user = sql_select + sql_user_views + " WHERE a.dining = 1 AND " + sql_date_range + sql_aggregate;
    private final static String sql_dining_active_unread_by_user = sql_select + sql_user_views + " WHERE a.dining = 1 AND " + sql_date_range + sql_aggregate + sql_unread_filter;
    private final static String sql_all_activities_active_by_user = sql_select + sql_user_views + " WHERE (a.dining = 1 OR a.flxrez = 1 OR a.golf = 1) AND " + sql_date_range + sql_aggregate;
    private final static String sql_all_activities_active_unread_by_user = sql_select + sql_user_views + " WHERE (a.dining = 1 OR a.flxrez = 1 OR a.golf = 1) AND " + sql_date_range + sql_aggregate + sql_unread_filter;
    private final static String sql_premier_active_by_user = sql_select + sql_user_views + " WHERE a.premier = 1 AND " + sql_date_range + sql_aggregate;
    private final static String sql_premier_active_unread_by_user = sql_select + sql_user_views + " WHERE a.premier = 1 AND " + sql_date_range + sql_aggregate + sql_unread_filter;
    private final static String sql_ftapp_active_by_user = sql_select + sql_user_views + " WHERE a.ftapp = 1 AND " + sql_date_range + sql_aggregate;
    private final static String sql_ftapp_active_unread_by_user = sql_select + sql_user_views + " WHERE a.ftapp = 1 AND " + sql_date_range + sql_aggregate + sql_unread_filter;
    
    /* If you change these, you'll need to re-build any classes that use them */
    public final static int MODE_GOLF = 0;
    public final static int MODE_FLXREZ = 1;
    public final static int MODE_DINING = 2;
    public final static int MODE_ALL_ACTIVITIES = 99; // All Activities (Everything above)
    public final static int MODE_PREMIER = 100;
    public final static int MODE_FTAPP = 200;
    public final static int MODE_ALL = 9999; // EVERYTHING
    

    public ForeTeesAnnouncement(){}; // Empty parm
    
    public ForeTeesAnnouncement(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public ForeTeesAnnouncement(ResultSet rs, boolean skip_content){
        loadFromResultSet(rs, skip_content);
    }
    
    public ForeTeesAnnouncement(long id){
        Connection con_ft = ApiCommon.getConnection();
        loadById(id, con_ft);
        Connect.close(con_ft);
    }
    
    public ForeTeesAnnouncement(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public ForeTeesAnnouncement(long id, String club_db_name, String username){
        Connection con_ft = ApiCommon.getConnection();
        loadByUsername(id, club_db_name, username, con_ft);
        Connect.close(con_ft);
    }
    
    public ForeTeesAnnouncement(long id, String club_db_name, String username, Connection con_ft){
        loadByUsername(id, club_db_name, username, con_ft);
    }
    
    public final Long loadById(long id, Connection con_ft){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con_ft.prepareStatement(sql_by_id);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = loadFromResultSet(rs);
                this.css = loadCss(con_ft);
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
    
    public final Long loadByUsername(long id, String club_db_name, String username, Connection con_ft){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con_ft.prepareStatement(sql_by_user_and_id);
            int i = 1;
            pstmt.setString(i++, club_db_name);
            pstmt.setString(i++, username);
            pstmt.setLong(i++, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = loadFromResultSet(rs);
                this.club_db_name = club_db_name; // setting this allows us to easily mark this as read later
                this.username = username; // setting this allows us to easily mark this as read later
                this.css = loadCss(con_ft);
            } else {
                last_error = String.format(ApiConstants.error_finding_by_id, classNameLower(), id);
            }
        } catch (Exception e) {
            Connect.logError(className() + ".loadByUsername: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = String.format(ApiConstants.error_loading_by_id, classNameLower(), e.toString());
        } finally {
            Connect.close(rs, pstmt);
        }

        
        return result;
        
    }
    
    public static String loadCss(){
        Connection con_ft = ApiCommon.getConnection();
        String result = loadCss(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    private static String loadCss(Connection con_ft){
        return ForeTeesSetting.getTextValue("announcement_css", "", con_ft);
    }
    
    private Long loadFromResultSet(ResultSet rs){
        return loadFromResultSet(rs, false);
    }
    
    private Long loadFromResultSet(ResultSet rs, boolean skip_content){
        
        Long result = null;
        try {
                this.id = rs.getLong("announce_id");
                
                this.title = rs.getString("title");
                this.file_name = rs.getString("fileName");
                
                this.date_start = timeUtil.getDbDate(rs.getInt("sdate"));
                this.date_end = timeUtil.getDbDate(rs.getInt("edate"));
                this.last_view = rs.getString("last_view");
                this.first_view = rs.getString("first_view");
                
                this.golf = rs.getBoolean("golf");
                this.flxrez = rs.getBoolean("flxrez");
                this.dining = rs.getBoolean("dining");
                this.premier = rs.getBoolean("premier");
                this.ftapp = rs.getBoolean("ftapp");
                
                this.publish = rs.getBoolean("publish");
                
                this.is_active = rs.getBoolean("is_active");
                
                this.total_views = rs.getInt("total_views");
                this.distinct_user_views = rs.getInt("distinct_user_views");
                this.distinct_club_views = rs.getInt("distinct_club_views");
                
                this.updated = rs.getTimestamp("updated").getTime();
                
                if(!skip_content){
                    // Don't return this when just returning a list.
                    this.html = rs.getString("html");
                }
                
                this.is_read = this.total_views > 0;
                
                result = id;
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
        
        if(title == null || title.trim().isEmpty()){
            last_error = "You must specify a title";
        } else if(html == null || html.trim().isEmpty()){
            last_error = "You must have HTML content.";
        } else if(date_start == null || date_start.trim().isEmpty()){
            last_error = "You must specify a start date.";
        } else if(date_end == null || date_end.trim().isEmpty()){
            last_error = "You must specify an end date.";
        }
        if (last_error == null) {
            if (id == null) {
                // Inserting new record
                try {
                    pstmt = con_ft.prepareStatement(""
                            + "INSERT INTO announcements "
                            + "  (title, fileName, html, sdate, edate, "
                            + "    golf, flxrez, dining, premier, ftapp, publish) "
                            + "  VALUES"
                            + "  (?, ?, ?, ?, ?, "
                            + "   ?, ?, ?, ?, ?, ?)");

                    int i = 1;
                    pstmt.setString(i++, title.trim());
                    pstmt.setString(i++, file_name);
                    pstmt.setString(i++, html);
                    pstmt.setInt(i++, timeUtil.getIntDateFromString(date_start));
                    pstmt.setInt(i++, timeUtil.getIntDateFromString(date_end));
                    pstmt.setBoolean(i++, golf);
                    pstmt.setBoolean(i++, flxrez);
                    pstmt.setBoolean(i++, dining);
                    pstmt.setBoolean(i++, premier);
                    pstmt.setBoolean(i++, ftapp);
                    pstmt.setBoolean(i++, publish);
                    pstmt.executeUpdate();

                    result = Connect.getLastInsertId(con_ft);

                    id = result;

                    loadById(id, con_ft); // Refresh results

                } catch (Exception e) {
                    //ForeTeesAnnouncement test = new ForeTeesAnnouncement(title, con_ft);
                    //if(test.last_error == null){
                    //    last_error = String.format(ApiConstants.error_update_duplicate_name, classNameLower(), title);
                    //} else {
                    Connect.logError(className() + ".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_insert, classNameLower(), e.toString());
                    //}
                } finally {
                    Connect.close(pstmt);
                }
            } else {
                // Update existing record
                try {
                    pstmt = con_ft.prepareStatement(""
                            + "UPDATE announcements "
                            + " SET"
                            + "  title = ?,"
                            + "  fileName = ?,"
                            + "  html = ?,"
                            + "  sdate = ?, "
                            + "  edate = ?, "
                            + "  golf = ?, "
                            + "  flxrez = ?, "
                            + "  dining = ?, "
                            + "  premier = ?, "
                            + "  ftapp = ?, "
                            + "  publish = ? "
                            + " WHERE announce_id = ? ");

                    int i = 1;
                    pstmt.setString(i++, title);
                    pstmt.setString(i++, file_name);
                    pstmt.setString(i++, html);
                    pstmt.setInt(i++, timeUtil.getIntDateFromString(date_start));
                    pstmt.setInt(i++, timeUtil.getIntDateFromString(date_end));
                    pstmt.setBoolean(i++, golf);
                    pstmt.setBoolean(i++, flxrez);
                    pstmt.setBoolean(i++, dining);
                    pstmt.setBoolean(i++, premier);
                    pstmt.setBoolean(i++, ftapp);
                    pstmt.setBoolean(i++, publish);
                    pstmt.setLong(i++, id);

                    pstmt.executeUpdate();

                    result = id;

                    loadById(id, con_ft); // Refresh results

                } catch (Exception e) {
                    //ForeTeesAnnouncement test = new ForeTeesAnnouncement(title, con_ft);
                    //if(test.last_error == null && test.id != null && !test.id.equals(id)){
                    //    last_error = String.format(ApiConstants.error_update_duplicate_name, classNameLower(), title);
                    //} else {
                    Connect.logError(className() + ".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_update, classNameLower(), e.toString());
                    //}
                } finally {
                    Connect.close(pstmt);
                }
            }
        }

        return result;
    }
    
    public final Long markRead(){
        return markRead(club_db_name, username);
    }
    
    public final Long markRead(String club_db_name, String username){
        Long result = null;
        Connection con_ft = ApiCommon.getConnection();
        result = markRead(club_db_name, username, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final Long markRead(String club_db_name, String username, Connection con_ft){
        Long result = null;
        
        PreparedStatement pstmt = null;
        
        if(club_db_name == null){
            last_error = "Cannot mark read;  Club Name cannot be NULL.";
        } else if(username == null){
            last_error = "Cannot mark read;  User Name cannot be NULL.";
        } else if(id == null){
            last_error = "Cannot mark read;  Announcement ID cannot be NULL.";
        } else {
            // Inserting view log record
            try {
                    pstmt = con_ft.prepareStatement(""
                        + "INSERT INTO announce_views "
                        + "  (club, user, announce_id, date_time) "
                        + "  VALUES"
                        + "  (?, ?, ?, NOW())");
                
                int i = 1;
                pstmt.setString(i++, club_db_name);
                pstmt.setString(i++, username);
                pstmt.setLong(i++, id);
                pstmt.executeUpdate();
                
                result = Connect.getLastInsertId(con_ft);
                
            } catch(Exception e) {
                Connect.logError(className() + ".markRead: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                last_error = String.format(ApiConstants.error_insert, classNameLower(), e.toString());
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
                        + "DELETE FROM announcements "
                        + " WHERE announce_id = ? ");
                
                int i = 1;
                pstmt.setLong(i++, id);
                
                pstmt.executeUpdate();
                
                result = true;
                
            } catch(Exception e) {
                Connect.logError(className()+".delete: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                last_error = String.format(ApiConstants.error_delete, classNameLower(), e.toString());
            } finally {
                Connect.close(pstmt);
            }
        }

        return result;
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
    
    public static List<ForeTeesAnnouncement> getList(){
        return getListById(null);
    }
    
    public static List<ForeTeesAnnouncement> getListById(Long id){
        Connection con_ft = ApiCommon.getConnection();
        List<ForeTeesAnnouncement> result = getList(id, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static List<ForeTeesAnnouncement> getList(Connection con_ft){
        return getList(null, con_ft);
    }
    
    public static List<ForeTeesAnnouncement> getList(Long id, Connection con_ft){
        
        List<ForeTeesAnnouncement> result = new ArrayList<ForeTeesAnnouncement>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String error = null;
        
        ForeTeesAnnouncement r = new ForeTeesAnnouncement();
        
        try {
            if(id == null){
                pstmt = con_ft.prepareStatement(sql_all);
            } else {
                pstmt = con_ft.prepareStatement(sql_by_id);
                pstmt.setLong(1, id);
            }
            
            String css = loadCss(con_ft);
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new ForeTeesAnnouncement(rs); // THis is used by administration.  We don't want to skip content for this list
                if(r.id != null && r.last_error == null){
                    r.css = css;
                    result.add(r);
                } else if (r.last_error != null) {
                    error = r.last_error;
                } else {
                    error = String.format(ApiConstants.error_unknown_condition, r.classNameLower());
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
    
    public static List<ForeTeesAnnouncement> getActiveByUsername(String club_db_name, String username){
        return getActiveByUsername(MODE_ALL, club_db_name, username);
    }
    
    public static List<ForeTeesAnnouncement> getActiveByUsername(int mode, String club_db_name, String username){
        Connection con_ft = ApiCommon.getConnection();
        List<ForeTeesAnnouncement> result = getActiveByUsername(mode, club_db_name, username, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static List<ForeTeesAnnouncement> getActiveByUsername(int mode, String club_db_name, String username, Connection con_ft){
        
        List<ForeTeesAnnouncement> result = new ArrayList<ForeTeesAnnouncement>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String error = null;
        
        ForeTeesAnnouncement r = new ForeTeesAnnouncement();
        
        try {
            
            // NOTE:  Please do not add club customs here -- except for this one demo club check.
            //        Try to implement the custom as a feature using club settings.
            boolean filter_published = !club_db_name.toLowerCase().startsWith("demo");
            
            switch (mode) {
                case MODE_ALL:
                    pstmt = con_ft.prepareStatement(sql_active_by_user);
                    break;
                case MODE_GOLF:
                    pstmt = con_ft.prepareStatement(sql_golf_active_by_user);
                    break;
                case MODE_FLXREZ:
                    pstmt = con_ft.prepareStatement(sql_flxrez_active_by_user);
                    break;
                case MODE_DINING:
                    pstmt = con_ft.prepareStatement(sql_dining_active_by_user);
                    break;
                case MODE_PREMIER:
                    pstmt = con_ft.prepareStatement(sql_premier_active_by_user);
                    break;
                case MODE_FTAPP:
                    pstmt = con_ft.prepareStatement(sql_ftapp_active_by_user);
                    break;
                case MODE_ALL_ACTIVITIES:
                    pstmt = con_ft.prepareStatement(sql_all_activities_active_by_user);
                    break;
                default:
                    return result;// empty result if proper mode not passed
            }
            
            int i = 1;
            int today = timeUtil.getDate();
            pstmt.setString(i++, club_db_name);
            pstmt.setString(i++, username);
            pstmt.setInt(i++, today); // Anything starting today
            pstmt.setInt(i++, today); // And nothing that has ended after today
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new ForeTeesAnnouncement(rs, true);
                if(r.id != null && r.last_error == null){
                    if(!filter_published || r.publish){
                        r.username = username;
                        r.club_db_name = club_db_name;
                        result.add(r);
                    }
                } else if (r.last_error != null) {
                    error = r.last_error;
                } else {
                    error = String.format(ApiConstants.error_unknown_condition, r.classNameLower());
                }
            }  
        } catch(Exception e) {
            error = String.format(ApiConstants.error_db_select, r.classNameLower(), e.toString());
            Connect.logError(r.className() + ".getActiveByUsername: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt);
        }
 
        if(error != null){
            Connect.logError(r.className() + ".getActiveByUsername: Err=" + error);
            return null;
        } else {
            return result;
        }

    }
    
    public static List<ForeTeesAnnouncement> getVisableByUsername(String club_db_name, String username){
        return getVisableByUsername(MODE_ALL, club_db_name, username);
    }
    
    public static List<ForeTeesAnnouncement> getVisableByUsername(int mode, String club_db_name, String username){
        Connection con_ft = ApiCommon.getConnection();
        List<ForeTeesAnnouncement> result = getVisableByUsername(mode, club_db_name, username, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static List<ForeTeesAnnouncement> getVisableByUsername(int mode, String club_db_name, String username, Connection con_ft){
        
        List<ForeTeesAnnouncement> result = new ArrayList<ForeTeesAnnouncement>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String error = null;
        
        ForeTeesAnnouncement r = new ForeTeesAnnouncement();
        
        try {
            
            // NOTE:  Please do not add club customs here -- except for this one demo club check.
            //        Try to implement the custom as a feature using club settings.
            boolean filter_published = !club_db_name.toLowerCase().startsWith("demo");
            
            switch (mode) {
                case MODE_ALL:
                    pstmt = con_ft.prepareStatement(sql_active_by_user);
                    break;
                case MODE_GOLF:
                    pstmt = con_ft.prepareStatement(sql_golf_active_by_user);
                    break;
                case MODE_FLXREZ:
                    pstmt = con_ft.prepareStatement(sql_flxrez_active_by_user);
                    break;
                case MODE_DINING:
                    pstmt = con_ft.prepareStatement(sql_dining_active_by_user);
                    break;
                case MODE_PREMIER:
                    pstmt = con_ft.prepareStatement(sql_premier_active_by_user);
                    break;
                case MODE_FTAPP:
                    pstmt = con_ft.prepareStatement(sql_ftapp_active_by_user);
                    break;
                case MODE_ALL_ACTIVITIES:
                    pstmt = con_ft.prepareStatement(sql_all_activities_active_by_user);
                    break;
                default:
                    return result;// empty result if proper mode not passed
            }
            
            int i = 1;
            pstmt.setString(i++, club_db_name);
            pstmt.setString(i++, username);
            pstmt.setInt(i++, timeUtil.getDate()); // Anything starting today
            pstmt.setInt(i++, 0); // And everything after, even if it has "ended".
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new ForeTeesAnnouncement(rs, true);
                if(r.id != null && r.last_error == null){
                    if(!filter_published || r.publish){
                        r.username = username;
                        r.club_db_name = club_db_name;
                        result.add(r);
                    }
                } else if (r.last_error != null) {
                    error = r.last_error;
                } else {
                    error = String.format(ApiConstants.error_unknown_condition, r.classNameLower());
                }
            }  
        } catch(Exception e) {
            error = String.format(ApiConstants.error_db_select, r.classNameLower(), e.toString());
            Connect.logError(r.className() + ".getVisableByUsername: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt);
        }
 
        if(error != null){
            Connect.logError(r.className() + ".getVisableByUsername: Err=" + error);
            return null;
        } else {
            return result;
        }

    }
    
    public static List<ForeTeesAnnouncement> getUnreadByUsername(String club_db_name, String username){
        return getUnreadByUsername(MODE_ALL, club_db_name, username);
    }
    
    public static List<ForeTeesAnnouncement> getUnreadByUsername(int mode, String club_db_name, String username){
        Connection con_ft = ApiCommon.getConnection();
        List<ForeTeesAnnouncement> result = getUnreadByUsername(mode, club_db_name, username, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static List<ForeTeesAnnouncement> getUnreadByUsername(int mode, String club_db_name, String username, Connection con_ft){
        
        List<ForeTeesAnnouncement> result = new ArrayList<ForeTeesAnnouncement>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String error = null;
        
        ForeTeesAnnouncement r = new ForeTeesAnnouncement();
        
        try {
            // NOTE:  Please do not add club customs here -- except for this one demo club check.
            //        Try to implement the custom as a feature using club settings.
            boolean filter_published = !club_db_name.toLowerCase().startsWith("demo");
            
            switch (mode) {
                case MODE_ALL:
                    pstmt = con_ft.prepareStatement(sql_active_unread_by_user);
                    break;
                case MODE_GOLF:
                    pstmt = con_ft.prepareStatement(sql_golf_active_unread_by_user);
                    break;
                case MODE_FLXREZ:
                    pstmt = con_ft.prepareStatement(sql_flxrez_active_unread_by_user);
                    break;
                case MODE_DINING:
                    pstmt = con_ft.prepareStatement(sql_dining_active_unread_by_user);
                    break;
                case MODE_PREMIER:
                    pstmt = con_ft.prepareStatement(sql_premier_active_unread_by_user);
                    break;
                case MODE_FTAPP:
                    pstmt = con_ft.prepareStatement(sql_ftapp_active_unread_by_user);
                    break;
                case MODE_ALL_ACTIVITIES:
                    pstmt = con_ft.prepareStatement(sql_all_activities_active_unread_by_user);
                    break;
                default:
                    return result;// empty result if proper mode not passed
            }
            int i = 1;
            pstmt.setString(i++, club_db_name);
            pstmt.setString(i++, username);
            pstmt.setInt(i++, timeUtil.getDate()); // Anything starting today
            pstmt.setInt(i++, 0); // And everything after, even if it has "ended".
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new ForeTeesAnnouncement(rs, true);
                if(r.id != null && r.last_error == null){
                    if(!filter_published || r.publish){
                        r.username = username;
                        r.club_db_name = club_db_name;
                        result.add(r);
                    }
                } else if (r.last_error != null) {
                    error = r.last_error;
                } else {
                    error = String.format(ApiConstants.error_unknown_condition, r.classNameLower());
                }
            }  
        } catch(Exception e) {
            error = String.format(ApiConstants.error_db_select, r.classNameLower(), e.toString());
            Connect.logError(r.className() + ".getUnreadByUsername: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt);
        }
 
        if(error != null){
            Connect.logError(r.className() + ".getUnreadByUsername: Err=" + error);
            return null;
        } else {
            return result;
        }

    }
    
    public static int getModeByActivityId(int activity_id){
        if(activity_id == ProcessConstants.GOLF_ACTIVITY_ID){
            return MODE_GOLF;
        } else if (activity_id == ProcessConstants.DINING_ACTIVITY_ID) {
            return MODE_DINING;
        } else if (activity_id > 0 & activity_id < 900){
            return MODE_FLXREZ;
        } else {
            return MODE_ALL_ACTIVITIES;
        }
    }
    
    
}
