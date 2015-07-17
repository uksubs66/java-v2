/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.records;

import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.ArrayUtil;
import com.foretees.common.Connect;
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
public class MembershipType {
    
    public Long id; // Not currently used/accurate (only an array index.  table has no ID field yet)
    public String name;
    public Integer activity_id;
    public Integer mtimes;
    public String rounds_per;
    public String period;
    public List<Integer> days;
    public List<Integer> advhrd;
    public List<Integer> advmind;
    public List<String> advamd;
    public String mpos;
    public String mposc;
    public String m9posc;
    public String mship_item;
    public String mship9_item;
    public Integer view_days;
    public String tflag;
    public Float sales_tax;
    public Boolean do9as18;
    public Boolean disabled;
    public Integer count; // read only
    public Long updated;
    
    public String last_error;
    
    // Club Membership Types
    private final static String sql_select = ""
            + "SELECT mst.*, count(IF(m.billable != 0 AND m.inact = 0,1,NULL)) as count "
            + "  FROM mship5 mst "
            + "    LEFT JOIN member2b m "
            + "       ON m.m_ship = mst.mship ";
    private final static String sql_aggregate = " GROUP BY mst.mship ";
    private final static String sql_all = sql_select + sql_aggregate;
    //private final static String sql_membership_type_by_id = sql_membership_type_select + "  WHERE mst.id = ? " + sql_aggregate;
    private final static String sql_by_activity_id = sql_select + "  WHERE mst.activity_id = ? " + sql_aggregate;
    private final static String sql_by_name = sql_select + "  WHERE mst.mship = ? " + sql_aggregate;
    private final static String sql_by_name_and_activity_id = sql_select + "  WHERE mst.mship = ? AND  mst.activity_id = ? " + sql_aggregate;
    

    public MembershipType(){}; // Empty parm
    
    public MembershipType(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public MembershipType(String name, Long club_id){
        Connection con_club = ApiCommon.getConnection(club_id);
        loadByName(name, con_club, false);
        Connect.close(con_club);
    }
    
    public final Long loadByName(String name, Connection con_club){
        return loadByName(name, con_club, false);
    }
    
    public final Long loadByName(String name, Connection con_club, boolean skip_loading){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con_club.prepareStatement(sql_by_name);
            pstmt.setLong(1, (long)0); // club invoicing id
            pstmt.setString(2, name.trim());

            rs = pstmt.executeQuery();
            if(rs.next()){
                result = loadFromResultSet(rs, skip_loading);
            } else {
                last_error = "Unable to find "+classNameLower()+": " + name;
            }  
        } catch(Exception e) {
            Connect.logError(className()+".loadByName: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
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
                //result = rs.getLong("id");
            } else {
                this.id = (long)rs.getRow();
                this.name = rs.getString("mship");
                this.activity_id = rs.getInt("activity_id");
                this.mtimes = rs.getInt("mtimes");
                this.period = rs.getString("period");
                this.days = Arrays.asList(ArrayUtil.getIntegerArrayFromResultSet(rs, "days%", 7));
                this.advhrd = Arrays.asList(ArrayUtil.getIntegerArrayFromResultSet(rs, "advhrd%", 7));
                this.advmind = Arrays.asList(ArrayUtil.getIntegerArrayFromResultSet(rs, "advmind%", 7));
                this.advamd = Arrays.asList(ArrayUtil.getStringArrayFromResultSet(rs, "advamd%", 7));
                this.mpos = rs.getString("mpos");
                this.mposc = rs.getString("mposc");
                this.m9posc = rs.getString("m9posc");
                this.mship_item = rs.getString("mshipItem");
                this.mship9_item = rs.getString("mship9Item");
                this.view_days = rs.getInt("viewdays");
                this.tflag = rs.getString("tflag");
                this.sales_tax = rs.getFloat("salestax");
                this.do9as18 = rs.getBoolean("9as18");
                this.count = rs.getInt("count");
                
                this.rounds_per =  mtimes + " per " + period; // Why did I put this here? 
                
                result = id;
            }
        } catch(Exception e) {
            last_error = String.format(ApiConstants.error_resultset, classNameLower(), e.toString());
            Connect.logError(className()+".loadFromResultSet: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            return null;
        }
        return result;
        
    }
    
    public static List<MembershipType> getList(Connection con_club) {
        return getList(null, con_club);
    }
    
    public static List<MembershipType> getList(long club_id) {
        return getListByActivityId(null, club_id);
    }
    
    public static List<MembershipType> getListByActivityId(Integer activity_id, long club_id) {
        Connection con_club = ApiCommon.getConnection(club_id);
        List<MembershipType> result = getList(activity_id, con_club);
        Connect.close(con_club);
        return result;
    }
    
    public static List<MembershipType> getListByActivityId(Integer activity_id, Connection con_club) {
        return getList(activity_id, con_club);
    }
    
    public static List<MembershipType> getList(Integer activity_id, Connection con_club){
        
        List<MembershipType> result = new ArrayList<MembershipType>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String error = null;
        
        MembershipType r = new MembershipType();
        
        try {
            int i = 1;
            if(activity_id != null){
                pstmt = con_club.prepareStatement(sql_by_activity_id);
                pstmt.setInt(i++, activity_id);
            } else {
                pstmt = con_club.prepareStatement(sql_all);
            }
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new MembershipType(rs);
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
