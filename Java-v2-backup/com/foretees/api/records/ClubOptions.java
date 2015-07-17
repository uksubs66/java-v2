/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.records;

import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.ArrayUtil;
import com.foretees.common.Connect;
import com.foretees.common.timeUtil;
import org.apache.commons.lang.*;
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
public class ClubOptions {
    
    public Boolean golf;
    public Boolean dining;
    public Boolean flxrez;
    public Boolean flxrez_staging;
    public Boolean dining_staging;

    public Long updated;
    public String last_error;
    
    
    public ClubOptions(){}; // Empty parm
    
    public ClubOptions(long club_id){
        Connection con_club = ApiCommon.getConnection(club_id);
        loadByConnection(con_club);
        Connect.close(con_club);
    };
    
    public ClubOptions(String club_name){
        Connection con_club = ApiCommon.getConnection(club_name);
        loadByConnection(con_club);
        Connect.close(con_club);
    };
    
    public ClubOptions(Connection con_club){
        loadByConnection(con_club);
    };
    
    public final Long loadByConnection(Connection con_club){
        Long result = null;
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        // Get member types
        try {
            // Get foretees_mode, dining_mode and # of activities from database
            pstmt = con_club.prepareStatement(ApiCommon.sql_club_parms);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                golf = rs.getInt("foretees_mode") > 0;
                dining = rs.getInt("organization_id") > 0;
                flxrez = rs.getInt("genrez_mode") > 0;
                flxrez_staging = rs.getInt("flxrez_staging") > 0;
                dining_staging = rs.getInt("dining_staging") > 0;
            }

        } catch(Exception e) {
            Connect.logError(className()+".loadById: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = String.format(ApiConstants.error_loading_by_id, classNameLower(), e.toString());
        } finally {
            Connect.close(rs, pstmt);
        }

        
        updated = timeUtil.getCurrentUnixTime();

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
    
}
