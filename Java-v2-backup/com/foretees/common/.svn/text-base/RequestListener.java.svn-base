/*
 * 
 * RequestListener:  Global request start and stop hooks for all servlets
 * 
 */

/*
 * NOTE:  This requires the following to be added to web.xml
 * 
  <listener>
    <listener-class>com.foretees.common.RequestListener</listener-class>
  </listener>
 * 
 */
package com.foretees.common;

import java.sql.*;          // mysql
import javax.sql.*;         // postgres
import javax.servlet.*;
import javax.servlet.http.*;

public class RequestListener implements ServletRequestListener {
    
    public static long hitCount = 0;
    public static long closeCount = 0;
 
    public void requestDestroyed(ServletRequestEvent event) {
        // If we have an open db connection, close it
        ServletRequest sreq = event.getServletRequest();
        HttpServletRequest req = (HttpServletRequest) sreq;  // Cast ServletRequest as HttpServletRequest for compatibility
        Connection con = Connect.getCon(req, false); // Get existing connection
        if(con != null){
            closeCount ++; // Count how many times we've closed a connection -- used for debug
            Connect.closeCon(req); // Close connection
        }
    }
 
    public void requestInitialized(ServletRequestEvent event) {
        // Do nothing on start
        hitCount ++; // Count how many hits -- used for debug
    }
 
}