/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import org.apache.commons.lang.*;

/**
 *
 * @author Owner
 */
public class memberUtil {

    public static memberSettings getSettings(HttpServletRequest req) {

        memberSettings result = null;
        try {
            result = (memberSettings) req.getAttribute("reqc_memberSettings");
        } catch (Exception e) {
            result = null;
        }
        if (result == null) {
            result = new memberSettings(req);
            req.setAttribute("reqc_memberSettings", result);
        }

        return result;

    }

    public static long getMemberIdFromUsername(String user, Connection con) {


        ResultSet rs = null;
        PreparedStatement pstmt = null;

        long member_id = 0;

        //
        //  Lookup the member id for a given member's username
        //
        try {

                pstmt = con.prepareStatement("SELECT id FROM member2b WHERE username = ?");

                pstmt.setString(1, user);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    member_id = rs.getLong("id");
                }

        } catch (Exception e) {

            Utilities.logError("memberUtil.getMemberIdFromUsername: Error looking up member_id for user: " + user + ", Error: " + e.getMessage());

        } finally {

            Connect.close(rs, pstmt);

        }

        return member_id;

    }
    
    

    public static boolean hasAppConfigured(long member_id, long club_id, Connection con) {


        boolean result = false;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        //
        //  Lookup the member id for a given member's username
        //
        try {

                pstmt = con.prepareStatement("SELECT id FROM v5.mobile_auth WHERE member_id = ? AND club_id = ? AND active = 1");

                pstmt.setLong(1, member_id);
                pstmt.setLong(2, club_id);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    
                    result = true;
                }

        } catch (Exception e) {

            Utilities.logError("memberUtil.hasAppConfigured: Error looking up app info for member_id: " + member_id + ", club_id=" + club_id + ", Error: " + e.getMessage());

        } finally {

            Connect.close(rs, pstmt);

        }

        return result;

    }
}
