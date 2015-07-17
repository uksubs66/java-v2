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
 *              9/06/12 Commented out Common_golfnet method calls until that class is ready for use.
 *              5/05/08 added getHdcpNum method
 *              5/02/08 Initial creation and addition of printScoreTypeLegend method
 *
 *                  
 *                  
 ***************************************************************************************
 */


import java.io.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.*;


import com.foretees.common.Utilities;


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

        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT hdcpSystem from club5");

            if ( rs.next() ){

                if (rs.getString("hdcpSystem").equalsIgnoreCase("ghin")){
                    return Common_ghin.getHdcpNum(user, con);
                }
                else if (rs.getString("hdcpSystem").equalsIgnoreCase("scga")){
                    return Common_scga.getHdcpNum(user, con);
                }
                else if (rs.getString("hdcpSystem").equalsIgnoreCase("gn21")){
                    return Common_golfnet.getHdcpNum(user, con);
                }

            }

        } catch (Exception ignore) { 
        
        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }
        
        return "";
    
    } // end getHdcpNum


 public static String getHdcpIndexName(String club, String hdcpSystem) {

    String result = "";

    if (hdcpSystem.equalsIgnoreCase("ghin")){
        
        return "USGA";
    
    } else if (hdcpSystem.equalsIgnoreCase("scga")) {
        
        return "SCGA";
    
    } else if (hdcpSystem.equalsIgnoreCase("gn21")) {
    
        return "GolfNet";
    
    } else {
        
        return "";
        
    }
    
 }


 public static String getClubHdcpOption(String club, Connection con) {


     Statement stmt = null;
     ResultSet rs = null;

     String hdcpSystem = "";

     try {

         stmt = con.createStatement();
         rs = stmt.executeQuery("SELECT hdcpSystem FROM club5 WHERE clubName <> ''");

         if (rs.next()) hdcpSystem = rs.getString(1);

     } catch (Exception exc) {

         Utilities.logError("Common_handicaps.getClubHdcpOption: Error looking up hdcpSystem. Err=" + exc.getMessage() + ", strace=" + Utilities.getStackTraceAsString(exc));

     } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

    }

     return hdcpSystem;

 }   // end of getClubHdcpOption

}
