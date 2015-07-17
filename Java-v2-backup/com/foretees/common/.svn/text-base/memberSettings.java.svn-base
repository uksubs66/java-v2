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
//import org.apache.commons.lang.StringEscapeUtils.*

/**
 *
 * @author Owner
 */
public class memberSettings {
    
    public boolean compactTeeSheet = false;
    // Add more settings here!
    
    public memberSettings(HttpServletRequest req) {

        String user = reqUtil.getSessionString(req, "user", "");
        loadSettings(req, user);
        
    }
    
    public memberSettings(HttpServletRequest req, String user) {

        loadSettings(req, user);
        
    }
    
    private void loadSettings(HttpServletRequest req, String user){
        
        Connection con = Connect.getCon(req);
        
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {

            //
            //  Get the tee sheet option and member sub-type for this user
            //
            pstmt = con.prepareStatement(
                    "SELECT * FROM member2b WHERE username = ?");
            
            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, user);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {
   
                compactTeeSheet = rs.getInt("tee_sheet_jump") > 0;
                // Add more settings here!
            }
            
            pstmt.close();
        } catch (Exception exc) {
        } finally {
            Connect.close(rs, pstmt);
        }
        
    }
}
