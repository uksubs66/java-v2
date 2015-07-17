/***************************************************************************************
 *   dataLoader:  This servlet will provide API processing for the data_loader servlet.
 *
 *       called by:  data_loader
 *
 *
 *   created:  6/29/2015   Bob P.
 *
 *
 ***************************************************************************************
 */
package com.foretees.common;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class dataLoader {


    //************************************************************************
    //
    //  extAPI - receives control from data_loader and dispatches based on todo value
    //
    //************************************************************************
    public static void extAPI(String todo, String club, HttpServletResponse resp, PrintWriter out, HttpServletRequest req, Connection con) {
        
        
        if (todo.equals("memberExport")) {
            
            memberExport(club, resp, out, req, con);      // go process member export request
            
            
        // add other todo checks here..........
            
        } else {
            
            // unknown request
            
            Utilities.logError("dataLoader.extAPI - Unknown Request: Club = " +club+ ", todo = " + todo);
            out.print("Error: Improper request.");
        }
                        
    }    // end of extAPI


    //************************************************************************
    //
    //  memberExport - export member information (original dev for CMI - Club Member Index for Interlachen)
    //
    //************************************************************************
    private static void memberExport(String club, HttpServletResponse resp, PrintWriter out, HttpServletRequest req, Connection con) {

        
        Statement stmt = null;
        ResultSet rs = null;
        
        //
        //  First, verify club name and key as a security measure (all new clubs must be added here!)
        //
        if (!club.equals("interlachen") || !req.getParameter("key").equals("asdf")) {
            
            Utilities.logError("dataLoader.memberExport - Unknown Club or Key: Club = " +club+ ", key = " +req.getParameter("key")+ ", todo = memberExport");
            out.print("Error: Improper request. Invalid club or key.");
            return;
        }
        
        resp.setContentType("text/csv");                                                             // text-csv file
        resp.setHeader("Content-Disposition", "attachment;filename=\""+club+"-login-counts.csv\""); 
                
        StringBuffer sb = new StringBuffer("");    // Use a stringbuffer to hold the records
        
        //
        //  Add header row
        //
        sb.append("\"ForeTees Id\",");
        sb.append("\"Member #\",");
        sb.append("\"Membership\",");
        sb.append("\"Member Type\",");
        sb.append("\"Total Logins\",");
        sb.append("\"Mobile Logins\",");
        sb.append("\"App Logins\"\n");
        
        try {

            //
            //  Get member info and output as table rows
            //
            if (con != null) {
                
                stmt = con.createStatement();    
                
                String sql = "" +
                    "SELECT id, username, count, m_ship, m_type, mobile_count, mobile_app_count " +
                    "FROM member2b " +
                    "WHERE inact=0 AND billable = 1";
                
                rs = stmt.executeQuery(sql);
                
                while (rs.next()) {

                    sb.append("\"" + rs.getInt("id") + "\",");
                    sb.append("\"" + rs.getString("username") + "\",");
                    sb.append("\"" + rs.getString("m_ship") + "\",");
                    sb.append("\"" + rs.getString("m_type") + "\",");
                    sb.append("\"" + rs.getInt("count") + "\",");
                    sb.append("\"" + rs.getInt("mobile_count") + "\",");
                    sb.append("\"" + rs.getInt("mobile_app_count") + "\"\n");
                }
                    
                stmt.close();
            }
            
            out.print(sb.toString());        // output the file
            
        } catch (Exception e) {

            Utilities.logError("Error in dataLoader.memberExport for club: " + club + ". Exception= " + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            out.print("Error: Database error gathering member data.");

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}
        }

    }  // end of memberExport
    

    
}  // end of dataLoader class

