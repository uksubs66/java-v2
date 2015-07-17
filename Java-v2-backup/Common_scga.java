/***************************************************************************************
 *   Common_scga:   This servlet contains all the common process for the scga scorelink
 *
 *
 *   Called by:     Common_handicaps
 *                  Support_importScores     // Tentatively
 *
 *
 *   Created:       05/02/2008 - Brad K.
 *
 *
 *   Revisions: 
 *
 *                  5/05/08 Added getHdcpNum method
 *                  5/02/08 Initial creation and addition of getScoreType() and getScgaScoreTypes methods
 *
 *                  
 *                  
 ***************************************************************************************
 */


import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import org.xml.sax.SAXException;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;


public class Common_scga extends HttpServlet {

    static String rev = SystemUtils.REVLEVEL;
    
    
    // ********************************************************************
    // Returns an ArrayList containing abbreviations and descriptions for SCGA score types
    // ********************************************************************
    protected static ArrayList<String> getScoreTypes (){

        ArrayList<String> scoreTypes = new ArrayList<String>();


        scoreTypes.add("A - Away");
        scoreTypes.add("H - Home");
        scoreTypes.add("T - Tournament");
        scoreTypes.add("P - Penalty");
        scoreTypes.add("IC - Internet Combined");
        scoreTypes.add("IA - Internet Away");
        scoreTypes.add("IH - Internet Home");
        scoreTypes.add("IT - Internet Tournament");
        
        return scoreTypes;
    } // end getScoreTypes
    
    
    
    // ********************************************************************
    // return score type based on SCGA type definitions:
    // 1 -- A -- Away
    // 2 -- H -- Home
    // 3 -- T -- Tour
    // 4 -- P -- Penalty
    // 10 - IC - Internet Combined
    // 11 - IA - Internet Away
    // 12 - IH - Internet Home
    // 13 - IT - Internet Tour
    // ********************************************************************
    protected static String getScoreType(int type){
        switch (type){
            case 1:
                return "A";
            case 2:
                return "H";
            case 3:
                return "T";
            case 4:
                return "P";
            case 10:
                return "IC";
            case 11:
                return "IA";
            case 12:
                return "IH";
            case 13:
                return "IT";
            default:
                return "";
        }
    } // end getScoreType
    
    // ********************************************************************
    // Returns the appropriately formatted
    // ********************************************************************
    protected static String getHdcpNum(String user, Connection con) {
  
        // lookup ghin # for this user
        try {

            PreparedStatement pstmt = con.prepareStatement("SELECT ghin FROM member2b WHERE username = ?");
            pstmt.clearParameters();
            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) 
                return rs.getString("ghin");

        } catch (Exception ignore) { }

        return "";
    
    } // end getHdcpNum
}