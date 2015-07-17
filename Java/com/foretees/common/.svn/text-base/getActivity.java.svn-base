/***************************************************************************************
 *   getActivity:  This class will populate a parameter block object to be used for an activity
 *
 *
 *   called by:  several
 *
 *
 *   created: 12/29/2008   Paul S.
 *
 *
 *   last updated:
 *
 *   10/14/09   Change to buildInString() to avoid there being no comma between the default "0" and the next activity_id when more than one
 *   10/08/09   Added buildActivitySelect() method to print a standard selection box including all configured root activities
 *   10/02/09   Added isGolfEnabled() method to return true if golf is enabled for a club, false if not
 *   10/02/09   Added getActivityCount() method to return the number of currently configured activities (golf included) for a club
 *
 *
 *
 *
 ***************************************************************************************
 */

package com.foretees.common;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;


public class getActivity {
 
 
public static void getParms(Connection con, parmActivity parmAct)
         throws Exception {
    
    // if we came here not knowing the activity but we know the slot, then get the activity id from that
    if (parmAct.activity_id == 0 && parmAct.slot_id > 0) {
        parmAct.activity_id = getActivityIdFromSlotId(parmAct.slot_id, con);
    }

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con.prepareStatement("" +
                "SELECT * " +
                "FROM activities " +
                "WHERE activity_id = ?");
        
        pstmt.clearParameters();
        pstmt.setInt(1, parmAct.activity_id);
        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            parmAct.name = rs.getString("activity_name");
            parmAct.common_name = rs.getString("common_name");
            parmAct.max_players = rs.getInt("max_players");
            parmAct.allow_guests = rs.getInt("allow_guests");
            parmAct.allow_x = rs.getInt("allow_x");
            parmAct.max_players = rs.getInt("max_players");
            parmAct.disallow_joins = rs.getInt("disallow_joins");
            parmAct.allow_guests = rs.getInt("allow_guests");
            parmAct.allow_x = rs.getInt("allow_x");
            parmAct.xhrs = rs.getInt("xhrs");
            parmAct.contact = rs.getString("contact");
            parmAct.email = rs.getString("email");
            parmAct.emailOpt = rs.getInt("emailOpt");
            parmAct.unacompGuest = rs.getInt("unacompGuest");
            parmAct.forceg = rs.getInt("forceg");
            parmAct.rndsperday = rs.getInt("rndsperday");
            parmAct.minutesbtwn = rs.getInt("minutesbtwn");
            parmAct.first_time = rs.getInt("first_time");
            parmAct.last_time = rs.getInt("last_time");
            parmAct.interval = rs.getInt("interval");
            parmAct.alt_interval = rs.getInt("alt_interval");
            parmAct.use_hdcp_equiv = rs.getInt("use_hdcp_equiv");
            parmAct.hdcp_equiv_name = rs.getString("hdcp_equiv_name");
            parmAct.hndcpProSheet = rs.getInt("hndcpProSheet");
            parmAct.hndcpMemSheet = rs.getInt("hndcpMemSheet");
            parmAct.consec_mem = rs.getInt("consec_mem");
            parmAct.consec_pro = rs.getInt("consec_pro");

        }

    } catch (Exception exc) { 
        
        throw new Exception("getActivity.getParms: activity_id=" + parmAct.activity_id + ", Error=" + exc.getMessage());
    
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
     
 } // end getParms


//
// Return true/false depending on if the activity system is enabled/configured for the club
//
public static boolean isConfigured(Connection con) {

    boolean result = false;

    Statement stmt = null;
    ResultSet rs = null;

    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("" +
                "SELECT clubName, genrez_mode, activity_id " +
                "FROM club5 c, activities a " +
                "WHERE " +
                    "c.clubName <> '' AND c.genrez_mode != 0 AND " +
                    "a.activity_id <> 0 AND a.parent_id = 0 " +
                "LIMIT 1;");

        if ( rs.next() ) result = true;

    } catch (Exception exc) {
        
        String tmp = "";
        try {
            if (con.isValid(1) == false) {
                tmp = "getActivity.isConfigured: isValid()=false! Err=";
            } else {
                tmp = "getActivity.isConfigured: isValid()=true, club=" + getClub.getClubName(con);
            }
        } catch (Exception exc2) {
            tmp = "getActivity.isConfigured: isValid() failed with " + exc2.toString();
        }
        
        Utilities.logError(tmp + exc.getMessage());
    
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

    }

    return result;

}


 public static int getActivityIdFromSlotId (int slot_id, Connection con)
         throws Exception {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int result = 0;

    try {

        pstmt = con.prepareStatement("" +
                "SELECT activity_id " +
                "FROM activity_sheets " +
                "WHERE sheet_id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, slot_id);

        rs = pstmt.executeQuery();

        if (rs.next()) result = rs.getInt(1);

        pstmt.close();

    } catch (Exception exc) {

        throw new Exception("getActivity.getActivityIdFromSlotId: " +
                            "slot_id=" + slot_id + ", Error=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return result;

 }


 public static int getParentIdFromActivityId (int activity_id, Connection con)
         throws Exception {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int result = 0;

    try {

        pstmt = con.prepareStatement("" +
                "SELECT parent_id " +
                "FROM activities " +
                "WHERE activity_id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);

        rs = pstmt.executeQuery();

        if (rs.next()) result = rs.getInt(1);

        pstmt.close();

    } catch (Exception exc) {

        throw new Exception("getActivity.getParentIdFromActivityId: " +
                            "activity_id=" + activity_id + ", Error=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return (result == 0) ? activity_id : result;

 }

 
 public static ArrayList<Integer> getAllParentsForActivity (int activity_id, Connection con)
         throws Exception {

     
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    ArrayList<Integer> result = new ArrayList<Integer>();
    result.add( activity_id );

    try {

        pstmt = con.prepareStatement("" +
                "SELECT parent_id " +
                "FROM activities " +
                "WHERE activity_id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);

        rs = pstmt.executeQuery();

        while ( rs.next() ) {
            
            if ( rs.getInt(1) != 0 ) {
                result.add( rs.getInt(1) ); // add this activity to the list
                result = addParentActivities(result, rs.getInt(1), con); // go and get the activity for this parent
            }
        }

    } catch (Exception exc) {

        throw new Exception("getActivity.getAllParentsForActivity: " +
                            "activity_id=" + activity_id + ", Error=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return result;

 }


 private static ArrayList<Integer> addParentActivities (ArrayList<Integer> array, int activity_id, Connection con)
         throws Exception {

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {

        pstmt = con.prepareStatement("" +
                "SELECT parent_id " +
                "FROM activities " +
                "WHERE activity_id = ? " +
                "ORDER BY activity_name");

        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);

        rs = pstmt.executeQuery();

        while ( rs.next() ) {

            if ( rs.getInt(1) != 0 ) {
                array.add( rs.getInt(1) ); // add this activity to the list
                array = addParentActivities(array, rs.getInt(1), con); // call ourselves rescursivly to find all parent activities
            }
        }

    } catch (Exception exc) {

        throw new Exception("getActivity.addParentActivities: " +
                            "activity_id=" + activity_id + ", Error=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return array;

 }
 
 
 public static int getRootIdFromActivityId (int activity_id, Connection con)
         throws Exception {

    if ( activity_id == 0 ) return 0;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int root_id = 0;
    int parent_id = -1;
    int loop = 0;
    int orig = activity_id;

    loop1:
    while (true) {

        loop++;
        try {

            pstmt = con.prepareStatement("" +
                    "SELECT parent_id " +
                    "FROM activities " +
                    "WHERE activity_id = ?");

            pstmt.setInt(1, activity_id);
            
            rs = pstmt.executeQuery();

            if (rs.next()) {
                parent_id = rs.getInt(1);
            } else {
                parent_id = 0;
            }

        } catch (Exception exc) {

            throw new Exception("getActivity.getRootIdFromActivityId: " +
                    "activity_id=" + activity_id + ", orig " + orig + ", loop " + loop + ", Error=" + exc.getMessage());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

        if (parent_id == 0) {

            root_id = activity_id;
            break loop1;

        } else {

            activity_id = parent_id;

        }

    } // while loop

    return root_id;

 }


 public static String getActivityName (int activity_id, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String result = "";

    try {

        pstmt = con.prepareStatement("" +
                "SELECT activity_name " +
                "FROM activities " +
                "WHERE activity_id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);

        rs = pstmt.executeQuery();

        if (rs.next()) result = rs.getString(1);

    } catch (Exception exc) {

        Utilities.logError("getActivity.getActivityName: " +
                            "activity_id=" + activity_id + ", Error=" + exc.getMessage());
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return result;

 }


 public static String getActivityCommonName (int activity_id, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String result = "";

    try {

        pstmt = con.prepareStatement("" +
                "SELECT common_name " +
                "FROM activities " +
                "WHERE activity_id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);

        rs = pstmt.executeQuery();

        if (rs.next()) result = rs.getString(1);

    } catch (Exception exc) {

        Utilities.logError("getActivity.getActivityCommonName: " +
                            "activity_id=" + activity_id + ", Error=" + exc.getMessage());
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return result;

 }


 //
 // TODO:  Need to add sanity checking. addChildActivities too!
 //
 public static ArrayList<Integer> getAllChildrenForActivity (int activity_id, Connection con)
         throws Exception {

    /*  (this query only goes 3 deep from (including) root)
        SELECT * FROM activities
        WHERE activity_id = 1 OR parent_id = 1 OR
        parent_id IN (SELECT activity_id FROM activities WHERE parent_id = 1)
    */
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    ArrayList<Integer> result = new ArrayList<Integer>();
    result.add( activity_id );

    try {

        pstmt = con.prepareStatement("" +
                "SELECT activity_id " +
                "FROM activities " +
                "WHERE parent_id = ? " +
                "ORDER BY activity_name");

        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);

        rs = pstmt.executeQuery();

        while ( rs.next() ) {

            result.add( rs.getInt(1) ); // add this activity to the list
            result = addChildActivities(result, rs.getInt(1), con); // go and add all the child activities for this activity
        }

        pstmt.close();

    } catch (Exception exc) {

        throw new Exception("getActivity.getAllChildrenForActivity: " +
                            "activity_id=" + activity_id + ", Error=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return result;

 }


 private static ArrayList<Integer> addChildActivities (ArrayList<Integer> array, int activity_id, Connection con)
         throws Exception {

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {

        pstmt = con.prepareStatement("" +
                "SELECT activity_id " +
                "FROM activities " +
                "WHERE parent_id = ? " +
                "ORDER BY activity_name");

        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);

        rs = pstmt.executeQuery();

        while ( rs.next() ) {

            array.add( rs.getInt(1) ); // add this activity to the list
            array = addChildActivities(array, rs.getInt(1), con); // call ourselves rescursivly to find all child activities
        }

    } catch (Exception exc) {

        throw new Exception("getActivity.addChildActivities: " +
                            "activity_id=" + activity_id + ", Error=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return array;

 }

 
 public static String buildInString(int activity_id, int type, Connection con) {

    String in = "0";

    if (activity_id != 0) {
        
        ArrayList<Integer> result = new ArrayList<Integer>();

        if (type == 0) {
            
            try { result = getAllParentsForActivity(activity_id, con); }
            catch (Exception exc) { Utilities.logError("Error in getActivity.buildInString for activity id#" + activity_id + " ERR=" + exc.toString()); }
        
        } else {
            
            try { result = getAllChildrenForActivity(activity_id, con); }
            catch (Exception exc) { Utilities.logError("Error in getActivity.buildInString for activity id#" + activity_id + " ERR=" + exc.toString()); }
        }
        
        for (int i = 0; i < result.size(); i++) {

            in += result.get(i) + ",";
        }

        in = in.substring(0, in.length() - 1);

        // now getAllChiildren/Parents calls are going to include the activity that was passed
        //in += "" + activity_id;
    }

    
    return in;
     
 }
 
 
 public static ArrayList<Integer> getAllActivitiesWithSheets(String exclude, Connection con) {

    Statement stmt = null;
    ResultSet rs = null;
    
    ArrayList<Integer> array = new ArrayList<Integer>();

    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery( "" +
                "SELECT activity_id " +
                "FROM activities " +
                "WHERE activity_id NOT IN (SELECT parent_id FROM activities GROUP BY parent_id)" +
                ((exclude.equals("")) ? "" : " AND activity_id IN (" + exclude + ")") );

        while ( rs.next() ) {

            array.add( rs.getInt(1) ); // add this activity to the list
        
        }

    } catch (Exception exc) {

        Utilities.logError("getActivity.getAllActivitiesWithSheets: Error=" + exc.getMessage());
    
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

    }
    
    return array;
    
 }


 public static String getFullActivityName(int activity_id, Connection con) {

    String result = "";

    ArrayList<Integer> array = new ArrayList<Integer>();

    try { array = getAllParentsForActivity(activity_id, con); }
    catch (Exception exc) { Utilities.logError("Error in getActivity.getFullActivityName for activity id#" + activity_id + " ERR=" + exc.toString()); }

    for (int i = 0; i < array.size(); i++) {

        result = getActivityName(array.get(i), con) + " > " + result;

    }

    if (result.endsWith(" > ")) result = result.substring(0, result.length() - 3);

    return result;

 }

 /**
  * getActivityCount - returns the number of activities (golf included) for this club
  * 
  * @param con Connection to club database
  *
  * @return count - # of configured activities
  */
 public static int getActivityCount(Connection con) {


     Statement stmt = null;
     ResultSet rs = null;

     int count = 0;
     int foretees_mode = 0;
     int activity_count = 0;

     // First check if they have YesRez active.  If not, we know they only have golf and can return 1
     if (!isConfigured(con)) {
         
         count = 1;
         
     } else {
         
         try {

             // Get foretees_mode and # of activities from database
             stmt = con.createStatement();
             rs = stmt.executeQuery(
                     "SELECT foretees_mode, (SELECT count(*) FROM activities WHERE parent_id = 0) as activity_count " +
                     "FROM club5");

             if (rs.next()) {
                 foretees_mode = rs.getInt("foretees_mode");
                 activity_count = rs.getInt("activity_count");
                 count = foretees_mode + activity_count;
             }

             stmt.close();
             
         } catch (Exception exc) {
             count = -1;
         } finally {

             try { rs.close(); }
             catch (Exception ignore) {}

             try { stmt.close(); }
             catch (Exception ignore) {}

         }
     }

     return count;
 }

 /**
  * isGolfEnabled - Returns whether or not golf is enabled for this club
  *
  * @param con Connection to club database
  *
  * @return enabled - True if golf is enabled, false if not
  */
 public static boolean isGolfEnabled(Connection con) {

     Statement stmt = null;
     ResultSet rs = null;

     boolean enabled = false;

     try {
         
         stmt = con.createStatement();
         rs = stmt.executeQuery("SELECT foretees_mode FROM club5");

         if (rs.next()) {
             if (rs.getInt("foretees_mode") == 1) enabled = true;
         }
         
     } catch (Exception ignore) {
         
         // do nothing - defaults to false
         
     } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { stmt.close(); }
         catch (Exception ignore) {}

     }

     return enabled;
 }


 /**
  * buildActivitySelection - prints out a simple selection box containing all configured activities
  * selection name = "activity_id"
  * value of options = id of activity
  *
  * @param size Desired size of select box
  * @param selected Optional - id of activity to be set as "selected." Pass -1 to ignore
  * @param con Connection to club database
  * @param out Output PrintWriter
  *
  * @return error - True if error encountered, false otherwise
  */
 public static boolean buildActivitySelect(int size, int selected_id, Connection con, PrintWriter out) {

     Statement stmt = null;
     ResultSet rs = null;

     String selected = "";

     boolean error = false;
     boolean golfEnabled = false;

     try {

         // See if golf is enabled and set boolean for later use
         golfEnabled = isGolfEnabled(con);

         // Grab all root activities from the database
         stmt = con.createStatement();
         rs = stmt.executeQuery("SELECT activity_id, activity_name FROM activities WHERE parent_id = 0 ORDER BY activity_name");

         if (rs.next() || golfEnabled) {

             rs.beforeFirst();

             // If at least one activity found, or golf is enabled
             out.println("<select name=\"activity_id\" size=\"" + size + "\">");

             if (golfEnabled) {
                 out.println("<option value=\"0\">Golf</option>");
             }

             while (rs.next()) {

                 if (selected_id >= 0 && rs.getInt("activity_id") == selected_id) {
                     selected = "selected ";
                 } else {
                     selected = "";
                 }

                 out.println("<option " + selected + "value=\"" + rs.getInt("activity_id") + "\">" + rs.getString("activity_name") + "</option>");
             }

             out.println("</select>");

         }

     } catch (Exception exc) {
         
         out.println("<!-- Error building activity select: " + exc.getMessage() + " -->");
         error = true;
         
     } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

     }
     
     return error;
 }

 
 public static int getSubActivityCount(int parent_id, Connection con) {
     
     Statement stmt = null;
     ResultSet rs = null;

     int count = 0;

     try {
         
         stmt = con.createStatement();
         rs = stmt.executeQuery("" +
                 "SELECT COUNT(activity_id) " +
                 "FROM activities " +
                 "WHERE parent_id = '" + parent_id + "' AND activity_id IN (SELECT parent_id FROM activities)");

         if (rs.next()) count = rs.getInt(1);
         
     } catch (Exception exc) {
         
         Utilities.logError("getActivity.getSubActivityCount: Error=" + exc.getMessage());
         
     } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { stmt.close(); }
         catch (Exception ignore) {}

     }

     return count;
     
 }
 
}