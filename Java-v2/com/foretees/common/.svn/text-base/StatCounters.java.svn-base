/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;

import com.foretees.api.ApiCommon;
//import com.foretees.common.ArrayUtil;
//import com.foretees.common.Connect;
//import com.foretees.common.timeUtil;
//import org.apache.commons.lang.*;
import java.sql.*;          // mysql
//import java.util.UUID;
//import javax.naming.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
//import java.lang.*;
//import org.joda.time.DateTimeZone;

/**
 *
 * @author Owner
 */
public class StatCounters {
 
    //
    //   Login and Tee Sheet Disply Statistics - Login, Member_sheet, Proshop_sheet (dsiplayed in Support)
    //
    public static String startDate = "";                 // date and time of first login
    public static int[] loginCountsMem = new int[24];  // one per hour of day
    public static int[] loginCountsPro = new int[24];
    public static int[] sheetCountsMem = new int[24];
    public static int[] sheetCountsPro = new int[24];
    
    // *********************************************************
    // Record the detils of this login for record keeping
    // *********************************************************
    public static void recordLogin(String user, String pass, String club, String ip, int success) {

        Connection con = null;
        PreparedStatement pstmt = null;

        try {

            con = Connect.getCon(ProcessConstants.REV);
            pstmt = con.prepareStatement(""
                    + "INSERT INTO logins (club, username, password, ip, node, success, datetime) "
                    + "VALUES (?, ?, ?, ?, ?, ?, now());");
            pstmt.clearParameters();
            pstmt.setString(1, club);
            pstmt.setString(2, user);
            pstmt.setString(3, pass);
            pstmt.setString(4, ip);
            pstmt.setInt(5, Common_Server.SERVER_ID);
            pstmt.setInt(6, success);

            pstmt.executeUpdate();

        } catch (Exception e) {

            Utilities.logError("Error in Login.recordLogin for club " + club + ", user " + user + ", Exception = " + e.getMessage());
            //Utilities.logError("Login.recordLogin Err:" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));

        } finally {

            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ignored) {
                }
            }

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ignored) {
                }
            }
        }

        pstmt = null;
        con = null;
        
    }
    
    
    public static void countMobileApp(String user, String club, HttpServletRequest req, Connection con) {


        PreparedStatement stmt = null;

        try {

            stmt = con.prepareStatement(
                    "UPDATE member2b SET mobile_app_count = mobile_app_count + 1 WHERE username = ?");

            stmt.clearParameters();
            stmt.setString(1, user);
            stmt.executeUpdate();

        } catch (Exception exc) {

            Utilities.logError("StatCounters.countMobileApp - error setting mobile app login count in member2b.  User=" + user + ", club=" + club + ", Err=" + exc.toString());

        } finally {

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignored) {
                }
            }
        }

    }

    //
    //  Login count method - bump counter for the specified member
    //
    public static void countLogins(String user, String club, HttpServletRequest req, Connection con) {


        PreparedStatement stmt = null;

        try {

            stmt = con.prepareStatement(
                    "UPDATE member2b SET count = count + 1 WHERE username = ?");

            stmt.clearParameters();
            stmt.setString(1, user);          // username 
            stmt.executeUpdate();

        } catch (Exception exc) {

            Utilities.logError("Common_webapi.countLogins - error setting login count in member2b.  User=" + user + ", club=" + club + ", Err=" + exc.toString());

        } finally {

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignored) {
                }
            }
        }

    }       // end of countLogins

    
}
