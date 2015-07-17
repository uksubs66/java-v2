/***************************************************************************************
 *   getWaitList:  This class will populate a parameter block object to be used for the wait list.
 *
 *
 *   called by:  several
 *
 *   created: 04/19/2008   Paul S.
 *
 *   notes: the notice string doesn't get set when calling getParms.  It could be large so and
 *          since they are many times more instances where we need the parm block but don't
 *          need the notice I added a seperate method for retreiving it. (getNotice)
 *
 *
 *   last updated:
 *
 *          10/16/08  Changed auto_assign (was unused) to member_view_teesheet
 *
 *
 ***************************************************************************************
 */

package com.foretees.common;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;


public class getWaitList {
 
 
 public static void getParms(Connection con, parmWaitList parmWL)
         throws Exception {
    

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int i = 0;

    //
    //  get the club's parameters
    //
    try {

        pstmt = con.prepareStatement("" +
                "SELECT *, " +
                    "DATE_FORMAT(sdatetime, '%Y%m%d') AS sdate, " +
                    "DATE_FORMAT(edatetime, '%Y%m%d') AS edate, " +
                    "DATE_FORMAT(sdatetime, '%H%i') AS stime, " +
                    "DATE_FORMAT(edatetime, '%H%i') AS etime " +
                "FROM wait_list " +
                "WHERE wait_list_id = ?");
        
        pstmt.clearParameters();
        pstmt.setInt(1, parmWL.wait_list_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            parmWL.name = rs.getString("name");

            parmWL.course = rs.getString("course");
            parmWL.color = rs.getString("color");

            parmWL.start_date = rs.getInt("sdate");
            parmWL.start_time = rs.getInt("stime");
            parmWL.end_date = rs.getInt("edate");
            parmWL.end_time = rs.getInt("etime");
            
            parmWL.cutoff_days = rs.getInt("cutoff_days");
            parmWL.cutoff_time = rs.getInt("cutoff_time");

            parmWL.sunday = rs.getInt("sunday");
            parmWL.monday = rs.getInt("monday");
            parmWL.tuesday = rs.getInt("tuesday");
            parmWL.wednesday = rs.getInt("wednesday");
            parmWL.thursday = rs.getInt("thursday");
            parmWL.friday = rs.getInt("friday");
            parmWL.saturday = rs.getInt("saturday");

            parmWL.max_list_size = rs.getInt("max_list_size");
            parmWL.max_team_size = rs.getInt("max_team_size");

            parmWL.member_access = rs.getInt("member_access");
            parmWL.member_view = rs.getInt("member_view");

            parmWL.member_view_teesheet = rs.getInt("member_view_teesheet");

            parmWL.allow_guests = rs.getInt("allow_guests");
            parmWL.allow_x = rs.getInt("allow_x");

            parmWL.enabled = rs.getInt("enabled");
            
        }
        
        pstmt.close();

    } catch (Exception exc) { 
        
        throw new Exception("getWaitList.getParms: wait_list_id=" + parmWL.wait_list_id + ", Error=" + exc.getMessage());
    }
     
 } // end getParms
 
 
 public static String getNotice(int wait_list_id, Connection con)
         throws Exception {
    

    String notice = "";

    //
    //  get the notice for this wait list
    //
    try {

        PreparedStatement pstmt = con.prepareStatement("" +
                "SELECT notice " +
                "FROM wait_list " +
                "WHERE wait_list_id = ?");
        
        pstmt.clearParameters();
        pstmt.setInt(1, wait_list_id);
        
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {

            notice = rs.getString(1);
            
        }
        
        pstmt.close();

    } catch (Exception exc) { 
        
        throw new Exception("getWaitList.getNotice: wait_list_id=" + wait_list_id + ", Error=" + exc.getMessage());
    }
    
    return notice;
    
 } // end getNotice
 
 
  public static int getListCount(int wait_list_id, int date, int index, int time, boolean onlyUnConverted, Connection con)
         throws Exception {
    

    int count = 0;
    int tmp = 2;

    //
    //  get the # of signups for this wait list that need to be converted
    //
    try {

        PreparedStatement pstmt = con.prepareStatement("" +
                "SELECT COUNT(*) " +
                "FROM wait_list_signups " +
                "WHERE wait_list_id = ? " +
                ((date > 0) ? " AND date = ? " : "") +
                ((index == 0) ? "AND ok_etime > ? " : "") + 
                ((onlyUnConverted) ? "AND converted = 0" : ""));
        
        pstmt.clearParameters();
        pstmt.setInt(1, wait_list_id);
        if (date > 0) pstmt.setInt(tmp, date); tmp++;
        if (index == 0) pstmt.setInt(tmp, time);        
        
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {

            count = rs.getInt(1);
        }
        
        pstmt.close();

    } catch (Exception exc) { 
        
        throw new Exception("getWaitList.getListCount: wait_list_id=" + wait_list_id + ", Error=" + exc.getMessage());
    }
    
    return count;
    
 } // end getListCount
 
  
  //
  // Returns the uid of the wait list entry this player is on, 0 = not found
  //
  public static int onList(String user, int wait_list_id, int date, Connection con)
         throws Exception {
    

    int uid = 0;

    //
    //  get the notice for this wait list
    //
    try {

        PreparedStatement pstmt = con.prepareStatement("" +
                "SELECT wls.wait_list_signup_id " +
                "FROM wait_list_signups wls, wait_list_signups_players wlsp " +
                "WHERE " +
                    "wls.wait_list_signup_id = wlsp.wait_list_signup_id AND " +
                    "wls.wait_list_id = ? AND " +
                    "wlsp.username = ? AND " +
                    "date = ?");
                    //"DATE_FORMAT(wls.req_datetime, '%Y%m%d') = ?");
        
        pstmt.clearParameters();
        pstmt.setInt(1, wait_list_id);
        pstmt.setString(2, user);
        pstmt.setInt(3, date);
        
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {

            uid = rs.getInt(1);
            
        }
        
        pstmt.close();

    } catch (Exception exc) { 
        throw new Exception("getWaitList.onList: wait_list_id=" + wait_list_id + ", date=" + date + ", user=" + user + ", Error=" + exc.getMessage());
    }
    
    return uid;
    
 } // end onList
  
  
  public static int getListIdBySignupId(int signup_id, Connection con)
         throws Exception {
    

    int list_id = 0;

    //
    //  get the notice for this wait list
    //
    try {

        PreparedStatement pstmt = con.prepareStatement("" +
                "SELECT wait_list_id " +
                "FROM wait_list_signups " +
                "WHERE wait_list_signup_id = ?");
        
        pstmt.clearParameters();
        pstmt.setInt(1, signup_id);
        
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) list_id = rs.getInt(1);
        
        pstmt.close();

    } catch (Exception exc) { 
        
        throw new Exception("getWaitList.getListIdBySignupId: signup_id=" + signup_id + ", Error=" + exc.getMessage());
    }
    
    return list_id;
    
 } // end getListIdBySignupId
  
  
 public static String getColor(int wait_list_id, Connection con)
         throws Exception {
    

    String color = "";

    //
    //  get the color for this wait list
    //
    try {

        PreparedStatement pstmt = con.prepareStatement("" +
                "SELECT color " +
                "FROM wait_list " +
                "WHERE wait_list_id = ?");
        
        pstmt.clearParameters();
        pstmt.setInt(1, wait_list_id);
        
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {

            color = rs.getString(1);
            
        }
        
        pstmt.close();

    } catch (Exception exc) { 
        
        throw new Exception("getWaitList.getColor: wait_list_id=" + wait_list_id + ", Error=" + exc.getMessage());
    }
    
    return color;
    
 } // end getColor
 
 
  //
  // Find out if there are any signups covering a specific time
  //
  public static boolean checkForSignups(int wait_list_id, int date, int time, Connection con)
         throws Exception {
    

    boolean result = false;

    try {
        
        PreparedStatement pstmt = con.prepareStatement(
                "SELECT COUNT(*) " +
                "FROM wait_list_signups " +
                "WHERE wait_list_id = ? AND date = ? AND " +
                "ok_stime <= ? AND ok_etime >= ? AND converted = 0");
        
        pstmt.clearParameters();
        pstmt.setInt(1, wait_list_id);
        pstmt.setInt(2, date);
        pstmt.setInt(3, time);
        pstmt.setInt(4, time);
        
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) result = (rs.getInt(1) > 0); // count = rs.getInt(1);
        
        pstmt.close();
        
    } catch (Exception exc) { 
        
        throw new Exception("getWaitList.checkForSignups: wait_list_id=" + wait_list_id + ", date=" + date + ", time=" + time + ", Error=" + exc.getMessage());
    }
    
    return result;
    
 } // end checkForSignups
 
}