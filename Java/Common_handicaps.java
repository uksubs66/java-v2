/***************************************************************************************
 *   Common_ghin:   This servlet contains all the common/general methods for handling handicaps
 *
 *
 *   Called by:     
 *
 *
 *   Created:       05/02/2008 Brad K.
 *
 *
 *   Revisions: 
 *
 *                  5/5/08 added getHdcpNum method
 *                  5/2/08 Initial creation and addition of printScoreTypeLegend method
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
import java.util.*;
import java.lang.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import org.xml.sax.SAXException;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;


public class Common_handicaps extends HttpServlet {

    static String rev = SystemUtils.REVLEVEL;
    
    
    // ********************************************************************
    // print out a score type legend for handicap system.  System to print for
    // is designated by the value of the 'hdcpSystem' parameter:
    // 1 - GHIN
    // 2 - SCGA
    // Style for printing designated by the value of the 'mode' parameter:
    // 0 = default/standard printing, no special formatting (single line)
    // ********************************************************************
    protected static void printScoreTypeLegend(PrintWriter out, int hdcpSystem, int mode){
        
        ArrayList<String> scoreTypes = null;
        String outStr = "";
        
        switch(hdcpSystem){
            case 1:                             // GHIN system handling
                switch (mode){
                    case 1:
                        break;
                    default:
                        outStr = "<CENTER>Legend: ";
                        scoreTypes = Common_ghin.getScoreTypes();
                        for (int i=0; i<scoreTypes.size(); i++){
                            if (i < scoreTypes.size())
                                outStr += "<nobr>" + scoreTypes.get(i) + ",</nobr> ";
                            else
                                outStr += "<nobr>" + scoreTypes.get(i) + "</nobr>";
                        }
                        outStr += "</CENTER>";
                        out.println(outStr);                        
                        break;
                }
                break;
            case 2:                             // SCGA system handling
                switch (mode){
                    case 1:
                        break;
                    default:
                        outStr = "Legend: ";
                        scoreTypes = Common_scga.getScoreTypes();
                        for (int i=0; i<scoreTypes.size(); i++){
                            if (i < scoreTypes.size())
                                outStr += "<nobr>" + scoreTypes.get(i) + ",</nobr> ";
                            else
                                outStr += "<nobr>" + scoreTypes.get(i) + "</nobr>";
                        }
                        out.println(outStr);                        
                        break;
                }
                break;
            default:
                return;
        }
    } // end printScoreTypeLegend
    
    
    // ********************************************************************
    // Return properly formatted Hdcp number based on the Hdcp system in use
    // ********************************************************************
    public static String getHdcpNum(String user, Connection con) {
  
        // lookup ghin # for this user
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT hdcpSystem from club5");
            if (rs.next()){
                if (rs.getString("hdcpSystem").equalsIgnoreCase("ghin")){
                    return Common_ghin.getHdcpNum(user, con);
                }
                else if (rs.getString("hdcpSystem").equalsIgnoreCase("scga")){
                    return Common_scga.getHdcpNum(user, con);
                }
            }
        } catch (Exception ignore) { }
        
    return "";
    
    } // end getHdcpNum
    
}
