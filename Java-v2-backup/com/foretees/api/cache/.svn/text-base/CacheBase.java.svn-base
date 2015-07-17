/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.cache;

import java.util.concurrent.TimeUnit;
import java.sql.*;          // mysql

import com.foretees.common.Connect;
import com.foretees.common.ProcessConstants;


/**
 *
 * @author Owner
 */
public class CacheBase {
    
    private long exp = 0L; // Expiration date
    private long last_clear_count = clear_count; // last clear counter, when cleared by a system wide (every node) api clear trigger event
    
    private static long clear_count = 0L;
    private static long last_clear_trigger = 0L;
    
    public void setExpirationMinutes(int minutes){
        last_clear_count = clear_count;
        exp = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(minutes);
    }
    
    public void setExpirationSeconds(int seconds){
        last_clear_count = clear_count;
        exp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);
    }
    
    public void setExpirationMillis(long milliseconds){
        last_clear_count = clear_count;
        exp = System.currentTimeMillis() + milliseconds;
    }
    
    public void setExpired(){
        exp = 0L;
        last_clear_count = clear_count;
    }
    
    public boolean isExpired(){
        return last_clear_count != clear_count || System.currentTimeMillis() > exp;
    }
    
    public static void clearNodeCache(){
        clear_count ++; // Every next isExpired() check for every cached object will return false -- effectivly clearing the cache.
    }
    
    public static void checkClearTrigger(){
        Connection con_ft = Connect.getCon(ProcessConstants.REV);
        checkClearTrigger(con_ft);
        Connect.close(con_ft);
    }
    
    // this runs every 5 seconds on every node.  Keep as lightweight as possible.
    // Checks to see if we should mark all cached objects as expired
    // If a memcachd server was running on the DB server, it would be handly to use it for this kind of check.
    public static void checkClearTrigger(Connection con_ft){
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Long result = null;
        try {
            pstmt = con_ft.prepareStatement("SELECT last_trigger FROM v5.api_triggers WHERE name = 'clear_object_cache' ");
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = rs.getTimestamp("last_trigger").getTime();
            } 
        } catch(Exception e) {
            Connect.logError("CacheBase.checkClearTrigger: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt);
        }
        if(result != null && result > last_clear_trigger){
            last_clear_trigger = result;
            clearNodeCache();
            Connect.logError("CacheBase: Clearing cache");
        }
    }
    
    public static void triggerAllNodeCacheClear(){
        Connection con_ft = Connect.getCon(ProcessConstants.REV);
        triggerAllNodeCacheClear(con_ft);
        Connect.close(con_ft);
    }
    
    // Triggers every node to update their cache within 5 seconds.
    public static void triggerAllNodeCacheClear(Connection con_ft){
        PreparedStatement pstmt = null;
        Long result = null;
        Connect.logError("CacheBase: Requesting all nodes to clear cache.");
        try {
            pstmt = con_ft.prepareStatement("UPDATE v5.api_triggers SET last_trigger = NOW() WHERE name = 'clear_object_cache' ");
            pstmt.execute();
            last_clear_trigger = result;
            clearNodeCache();
            checkClearTrigger();
        } catch(Exception e) {
            Connect.logError("CacheBase.triggerAllNodeCacheClear: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(pstmt);
        }
    }
    
}
