/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api;

import java.sql.*;
import org.apache.commons.lang.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// DO not import com.foretees.common.* or com.foretees.common.Utilities.  
// Be selective what is included here.  Anything included cannot use methods in this class.
// Do not import com.fortees.common.records.[anything], etc.

import com.foretees.common.ProcessConstants;
import com.foretees.common.Connect;
import com.foretees.api.cache.StringCache;
import com.foretees.api.cache.LongCache;

/**
 *
 * @author Owner
 */
public class ApiCommon {
    
    public static String sql_club_parms = "SELECT * FROM club5 WHERE clubName <> ''";
    
    
    // Cache club db name / id lookups (they should rarely, if ever, change.)
    private static int id_name_cache_time = 60; // Cache name/id lookups for 60 seconds (this could probably be much longer)
    
    private static Map<Long, StringCache> club_dbname_cache = new ConcurrentHashMap<Long, StringCache>();
    private static Map<String, LongCache> club_id_cache = new ConcurrentHashMap<String, LongCache>();
    private static Map<String, LongCache> org_id_cache = new ConcurrentHashMap<String, LongCache>();
    
    // Get foretees system DB connection
    public static Connection getConnection(){
        return Connect.getCon(ProcessConstants.REV);
    }
    
    // Get club DB connection by club database name
    public static Connection getConnection(String club_code){
        return Connect.getCon(club_code);
    }
    
    // Get club DB connection by club ID
    public static Connection getConnection(Long club_id){
        Connection result = null;
        Connection con_ft = ApiCommon.getConnection();
        result = getConnection(club_id, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static Connection getConnection(Long club_id, Connection con_ft){
        
        Connection result = null;
        String clubCode = getClubCode(club_id, con_ft);
        if(clubCode != null){
            result = getConnection(clubCode);
        }
        return result;
    }
    
    public static Long getClubId(String club_code){
        
        LongCache cache = LongCache.get(club_code, club_id_cache);
        if(!cache.isExpired()){
            return cache.val;
        }
        Connection con_ft = getConnection();
        Long result = getClubId(club_code, con_ft);
        Connect.close(con_ft);
        return result;
        
    }
    
    // Expensive (not cacheable).  Avoid use if possible.
    public static Long getClubId(Connection con_club){
        
        return getClubId(getClubCode(con_club));
        
    }
    
    public static Long getClubId(String club_code, Connection con_ft_or_club){
        
        LongCache cache = LongCache.get(club_code, club_id_cache);
        if(!cache.isExpired()){
            return cache.val;
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Long result = null;
        try {
            pstmt = con_ft_or_club.prepareStatement("SELECT c.id FROM v5.clubs c WHERE c.clubname = ? ");
            pstmt.setString(1, club_code);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = rs.getLong("id");
            } else {
                Connect.logError("Common.getClubId: Err=No Club ID found for "+(club_code == null?"NULL":club_code) + "\n" + Connect.stackTraceToString(Thread.currentThread().getStackTrace()));
            }
        } catch(Exception e) {
            Connect.logError("Common.getClubId: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt);
        }
        if(result != null){
            cache.val = result;
            cache.setExpirationSeconds(id_name_cache_time); // Then set exp time
        }
        return result;
    }
    
    public static String getClubCode(Long club_id){
        StringCache cache = StringCache.get(club_id, club_dbname_cache);
        if(!cache.isExpired()){
            return cache.val;
        }
        Connection con_ft = ApiCommon.getConnection();
        String result = getClubCode(club_id, con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static String getClubCode(Long club_id, Connection con_ft_or_club){
        StringCache cache = StringCache.get(club_id, club_dbname_cache);
        if(!cache.isExpired()){
            return cache.val;
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String result = null;
        try {
            pstmt = con_ft_or_club.prepareStatement("SELECT c.clubname FROM v5.clubs c WHERE c.id = ? ");
            pstmt.setLong(1, club_id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = rs.getString("clubname");
            } else {
                Connect.logError("Common.getClubCode: Err=No Club DB Name found for "+(club_id == null?"NULL":club_id) + "\n" + Connect.stackTraceToString(Thread.currentThread().getStackTrace()));
            }
        } catch(Exception e) {
            Connect.logError("Common.getClubCode: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt);
        }
        if(result != null){
            cache.val = result;
            cache.setExpirationSeconds(id_name_cache_time);
        }
        return result;
    }
    
    
    // Expensive (not cacheable), but not as expensive as a query of v5.clubs or club5, etc.
    public static String getClubCode(Connection con_club){
        String result = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con_club.prepareStatement("SELECT DATABASE() AS clubname");
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = rs.getString("clubname");
            } 
        } catch(Exception e) {
            Connect.logError("Common.getClubCode: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt);
        }
        return result;
    }
    
    public static Long getClubOrganizationId(Long club_id){
        
        String club_code = getClubCode(club_id);
        if(club_code == null){
            return null;
        } else {
            return getClubOrganizationId(club_code);
        }
        
    }
    
    public static Long getClubOrganizationId(String club_code){
        
        LongCache cache = LongCache.get(club_code, org_id_cache);
        if(!cache.isExpired()){
            return cache.val;
        }
        Connection con_club = getConnection(club_code);
        Long result = getClubOrganizationId(club_code, con_club);
        Connect.close(con_club);
        return result;
        
    }
    
    public static Long getClubOrganizationId(Connection con_club){
        return getClubOrganizationId(null, con_club);
    }
    public static Long getClubOrganizationId(String club_code, Connection con_club){
        Long result = null;
        if(club_code == null){
            club_code = getClubCode(con_club);
        }
        LongCache cache = LongCache.get(club_code, org_id_cache);
        if(!cache.isExpired()){
            return cache.val;
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con_club.prepareStatement(sql_club_parms);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = rs.getLong("organization_id");
            } 
        } catch(Exception e) {
            Connect.logError("Common.getClubOrganizationId: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt);
        }
        if(result != null){
            cache.val = result;
            cache.setExpirationSeconds(id_name_cache_time);
        }
        return result;
    }
    
    
    public static boolean isValidationConstraint(SQLException e){
        return e.getSQLState().startsWith("23");
    }
    
    public static String formatClassName(String className){
        return StringUtils.join(className.split("(?=\\p{Lu})")," ").trim();
    }
    
    public static String formatClassNameLower(String className){
        return formatClassName(className).toLowerCase();
    }
    
    public static void setOrNull(int index, PreparedStatement pstmt, Long value) throws SQLException {
        if(value == null){
            pstmt.setNull(index, Types.BIGINT);
        } else {
            pstmt.setLong(index, value);
        }
    }
    
    public static void setOrNull(int index, PreparedStatement pstmt, Integer value) throws SQLException {
        if(value == null){
            pstmt.setNull(index, Types.INTEGER);
        } else {
            pstmt.setInt(index, value);
        }
    }
    
    public static void setOrNull(int index, PreparedStatement pstmt, Float value) throws SQLException {
        if(value == null){
            pstmt.setNull(index, Types.FLOAT);
        } else {
            pstmt.setFloat(index, value);
        }
    }
    
    public static void setOrNull(int index, PreparedStatement pstmt, String value) throws SQLException {
        if(value == null){
            pstmt.setNull(index, Types.VARCHAR);
        } else {
            pstmt.setString(index, value);
        }
    }
    
    public static void setOrNull(int index, PreparedStatement pstmt, Boolean value) throws SQLException {
        if(value == null){
            pstmt.setNull(index, Types.TINYINT);
        } else {
            pstmt.setBoolean(index, value);
        }
    }
    
    public static Float parseFloatOrNull(String string){
        Float result = null;
        try{
            result = Float.parseFloat(string);
        }catch(Exception e){
            
        }
        return result;
    }

    public static Long parseLongOrNull(String string){
        Long result = null;
        try{
            result = Long.parseLong(string);
        }catch(Exception e){
            
        }
        return result;
    }
    
    public static String simple(Float number) {

        float d;
        if(number == null){
            d = (float)0;
        } else {
            d = number;
        }
        if (d == (long) d) {
            return String.format("%d", (long) d);
        } else {
            return String.format("%s", d);
        }

    }
    
    
    /*
     * Convert a list of email addresses, like test1@domain.com, test2@domain.com; test3@domain.com to an ArrayList compat. with sendEmail.doSending's pro copy list
     */
    public static ArrayList<String> parseEmailProRecipientString(String recipient_list){
        ArrayList<String> rcptList = new ArrayList<String>(Arrays.asList(recipient_list.split(" *[;,] *")));
        return rcptList;
    }
    
    /*
     * Convert a list of email addresses, like test1@domain.com, test2@domain.com; test3@domain.com to an ArrayList compat. with sendEmail.doSending's rcpt list
     */
    public static ArrayList<ArrayList<String>> parseEmailRecipientString(String recipient_list){
         ArrayList<String> rcpt = new ArrayList<String>(Arrays.asList(recipient_list.split(" *[;,] *")));
        // Convert list of addresses to name/email pair (currently we don't support named email addresses)
        ArrayList<ArrayList<String>> rcptList = new ArrayList<ArrayList<String>>();
        for(String address : rcpt){
            rcptList.add(new ArrayList<String>(Arrays.asList(address,"")));
        }
        return rcptList;
    }
    
    public static String buildUserSelectSql(String prefix, String query, String suffix, String glue, int count){
        
        StringBuilder result = new StringBuilder();
        for(int i = 1; i <= count; i++){
            result.append(prefix);
            result.append(query.replace("%%", Integer.toString(i)));
            result.append(suffix);
            if(i < count){
                result.append(glue);
            }
        }
        
        return result.toString();
    }
    
    

    // ***************************************************************
    //  getClubPOS
    //
    //      Get the club's POS Type.
    //
    // ***************************************************************
    public static String getClubPOS(Connection con_club) {

        Statement stmt = null;
        ResultSet rs = null;

        String posType = "";

        try {

            stmt = con_club.createStatement();        // create a statement
            rs = stmt.executeQuery("SELECT posType FROM club5");          // get pos type

            if (rs.next()) {
                posType = rs.getString("posType");
            }

        } catch (Exception ignore) {
        } finally {
            Connect.close(rs, stmt);
        }

        return (posType);
    }

}
