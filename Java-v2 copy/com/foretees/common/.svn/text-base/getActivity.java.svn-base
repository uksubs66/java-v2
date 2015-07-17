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
 *    2/11/14   Updated getOrderedConsecList to trim the consec_csv_mem/pro string it gets back, to avoid errors when there are leading/trailing spaces attached.
 *    2/04/14   Winged Foot GC (wingedfoot) - Added custom to getOrderedConsecList to return a different set of time options based on the day of the week (case 2363).
 *    2/04/14   Updated getOrderedConsecList calls to pass a clubname and day name, to allow for customs to be done.
 *    1/06/14   Updated getParms() to populate the 'search_other_times' field in parmAct.
 *    9/25/13   Updated getParms() to add a second database call to the 'activities' table entry of the root activity id, for parameters that should be inherited from the root activity.
 *    9/20/13   Updated getParms() to also populate the 'max_originations' field in parmAct.
 *    4/22/13   Updated getActivityName to return Golf and Dining activity names as well (for api)
 *    3/14/13   Updated getAllActivitiesWithSheets() to only look at activities entries with enabled = 1.
 *    3/14/13   Added getChildlessActivityCount() method to return a count of the number of childless activities for a particular club.
 *   10/18/12   Added isChildlessActivity() method to provide an easy way to determine of a particular activity has any children (intended for checking for bare root activities).
 *   10/18/12   Updated getAllActivitiesWithSheets so that it will never pull back root activities (parent_id == 0).
 *    1/23/12   Update buildActivitySelect() to include Dining is applicable
 *   12/04/11   Added getColsPerRow() method to return the number of activity_id columns to display on the summary view time sheets.
 *    9/21/11   Added getMaxConsecTimes() method to return the maximum number of consecutive times allowed for a given activity_id.
 *    9/21/11   Added getOrderedConsecList() method to return a list of all allowed consec values for a given activity_id in numerical order, lowest to highest.
 *    5/17/11   Added isStagingMode() method for checking whether FlxRez is in staging mode for a given club.
 *    5/06/11   Added buildActivityTree() for building a nested list of sub-activity drop-down menus at the top of FlxRez timesheets (only for clubs with sub-activities).
 *    5/06/11   Added getChildrenForActivity() to return only children for the current activity (non-recursive).
 *    4/07/11   Added getActivityEmailName() to return the customized activity name to be used in place of the root activity name in emails (if specified).
 *    3/21/11   Fix for buildActivitySelect() not being able to default to Golf
 *    3/18/11   Added custom_action and include_all options to buildActivitySelect to allow for custom actions to be added to the select object, and
 *              the ability to include "ALL" as a selectable option, if specified.
 *    2/17/11   Added force_singles parameter to getParms processing so it will be populated in the parameter block.
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

        // Gather appropriate items from the activities entry specific to the reservation court
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
            parmAct.force_singles = rs.getInt("force_singles");
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
            parmAct.max_originations = rs.getInt("max_originations");
            parmAct.search_other_times = rs.getInt("search_other_times");
        }

    } catch (Exception exc) { 
        
        throw new Exception("getActivity.getParms: activity_id=" + parmAct.activity_id + ", Error=" + exc.getMessage());
    
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}
    }
    
    try {
        
        // Gather specific items from the activities entry of the root activity of the reservation court
        pstmt = con.prepareStatement("" +
                "SELECT * " +
                "FROM activities " +
                "WHERE activity_id = ?");
        
        pstmt.clearParameters();
        pstmt.setInt(1, getRootIdFromActivityId(parmAct.activity_id, con));
        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            parmAct.max_originations = rs.getInt("max_originations");
        }
        
    } catch (Exception exc) { 
        
        throw new Exception("getActivity.getParms: activity_id=" + parmAct.activity_id + ", Error2=" + exc.getMessage());
    
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

public static boolean isStagingMode(Connection con) {

    boolean result = false;

    Statement stmt = null;
    ResultSet rs = null;

    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT flxrez_staging FROM club5");

        if (rs.next()) {

            if (rs.getInt("flxrez_staging") == 1) {
                result = true;
            }
        }

    } catch (Exception exc) {

        String tmp = "";
        try {
            if (con.isValid(1) == false) {
                tmp = "getActivity.isStagingMode: isValid()=false! Err=";
            } else {
                tmp = "getActivity.isStagingMode: isValid()=true, club=" + getClub.getClubName(con);
            }
        } catch (Exception exc2) {
            tmp = "getActivity.isStagingMode: isValid() failed with " + exc2.toString();
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

/**
 * Checks to see if an activity_id has any children
 * @param activity_id activity_id to check for children for
 * @param con Connection to club database
 * @return boolean - true if no children exist, false otherwise
 */
public static boolean isChildlessActivity(int activity_id, Connection con) {
    
    boolean result = false;
    
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
        
        pstmt = con.prepareStatement("SELECT activity_id FROM activities WHERE parent_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);
        
        rs = pstmt.executeQuery();
        
        if (!rs.next()) {
            result = true;
        }
        
    } catch (Exception exc) {
        Utilities.logError("getActivity.isChildlessActivity - Error checking for children activities - ERR: " + exc.toString());
    } finally {
        
        try { rs.close(); }
        catch (Exception ignore) {}
        
        try { pstmt.close(); }
        catch (Exception ignore) {}
    }
    
    return result;
}

public static int getChildlessActivityCount(Connection con) {
    
    Statement stmt = null;
    ResultSet rs = null;
    
    int count = 0;
    
    try {
        
        stmt = con.createStatement();
        
        rs = stmt.executeQuery("SELECT activity_id FROM activities WHERE parent_id = 0 AND enabled = 1");
        
        while (rs.next()) {
            if (isChildlessActivity(rs.getInt(1), con)) {
                count++;
            }
        }
        
    } catch (Exception exc) {
        count = -1;
        Utilities.logError("getActivity.getChildlessActivityCount - Error looking up root activities - ERR: " + exc.toString());
    } finally {
        
        try { rs.close(); }
        catch (Exception ignore) {}
        
        try { stmt.close(); }
        catch (Exception ignore) {}
    }
    
    return (count);
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

    if (activity_id == 0) {

        result = "Golf";

    } else if (activity_id == ProcessConstants.DINING_ACTIVITY_ID) {

        result = "Dining";

    } else {

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

    }

    return result;

 }


 public static String getCommonName (int activity_id, Connection con) {

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

        Utilities.logError("getActivity.getCommonName: " +
                            "activity_id=" + activity_id + ", Error=" + exc.getMessage());
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return result;

 }


 public static String getActivityEmailName (int activity_id, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String result = "";

    try {

        pstmt = con.prepareStatement("" +
                "SELECT email_name " +
                "FROM activities " +
                "WHERE activity_id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);

        rs = pstmt.executeQuery();

        if (rs.next()) result = rs.getString(1);

    } catch (Exception exc) {

        Utilities.logError("getActivity.getActivityEmailName: " +
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

 /**
  * getChildrenForActivity - Returns an ArrayList of activity_ids for the children of the passed activity_id only. NO RECURSIVE CALLS!
  *
  * @param activity_id Activity id to look up children for.
  * @param con Connection to club database
  *
  * @return result - ArrayList of integer activity_ids
  */
 public static ArrayList<Integer> getChildrenForActivity(int activity_id, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    ArrayList<Integer> result = new ArrayList<Integer>();

    try {

        pstmt = con.prepareStatement("" +
                "SELECT activity_id " +
                "FROM activities " +
                "WHERE parent_id = ? " +
                "ORDER BY sort_by, activity_name");

        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);

        rs = pstmt.executeQuery();

        while (rs.next()) {
            result.add(rs.getInt("activity_id"));
        }

    } catch (Exception exc) {

        Utilities.logError("getActivity.getChildrenForActivity: " +
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
                "WHERE parent_id != 0 AND activity_id NOT IN (SELECT parent_id FROM activities GROUP BY parent_id) AND enabled = 1" +
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
  * @param custom_action Custom action to be applied to the select object (e.g. onChange action)
  * @param include_all Determines whether or not the "ALL" option is included at the end of the select options
  * @param con Connection to club database
  * @param out Output PrintWriter
  *
  * @return error - True if error encountered, false otherwise
  */
 public static boolean buildActivitySelect(int size, int selected_id, String custom_action, boolean include_all, Connection con, PrintWriter out) {

     Statement stmt = null;
     ResultSet rs = null;

     String selected = "";

     boolean error = false;
     boolean golfEnabled = false;
     boolean diningEnabled = false;

     try {

         // See if golf is enabled and set boolean for later use
         golfEnabled = isGolfEnabled(con);

         diningEnabled = (Utilities.getOrganizationId(con) != 0);

         // Grab all root activities from the database
         stmt = con.createStatement();
         rs = stmt.executeQuery("SELECT activity_id, activity_name FROM activities WHERE parent_id = 0 ORDER BY activity_name");

         if (rs.next() || golfEnabled) {

             rs.beforeFirst();

             // If at least one activity found, or golf is enabled
             out.println("<select name=\"activity_id\" size=\"" + size + "\" " + custom_action + ">");

             if (golfEnabled) {
                 out.println("<option " + (selected_id == 0 ? "selected " : "") + "value=\"0\">Golf</option>");
             }

             if (diningEnabled) {
                 out.println("<option " + (selected_id == ProcessConstants.DINING_ACTIVITY_ID ? "selected " : "") + "value=\"" + ProcessConstants.DINING_ACTIVITY_ID + "\">Dining</option>");
             }

             while (rs.next()) {

                 if (selected_id >= 0 && rs.getInt("activity_id") == selected_id) {
                     selected = "selected ";
                 } else {
                     selected = "";
                 }

                 out.println("<option " + selected + "value=\"" + rs.getInt("activity_id") + "\">" + rs.getString("activity_name") + "</option>");
             }

             if (include_all) {
                 out.println("<option " + (selected_id == 999 ? "selected " : "") + "value=\"999\">ALL</option>");
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


 public static void buildActivityTree(int activity_id, int selected_activity_id, PrintWriter out, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     int parent_id = 0;
     int child_count = 0;

     String result = "";
     
     ArrayList<Integer> child_ids = new ArrayList<Integer>();

     try {

         parent_id = getParentIdFromActivityId(activity_id, con);
         
         // If parent_id is 0, this is the root activity. Start printing the list. If not, call this method recursively using the parent_id.
         if (parent_id == activity_id) {
             
             out.print("<br><a href=\"javascript:void(" + activity_id + ")\" onclick=\"reloadPageActId(" + activity_id + ")\">" + getActivity.getActivityName(activity_id, con) + "</a>");

         } else {

             // Call recursively to print the earlier parts of the tree first
             buildActivityTree(parent_id, activity_id, out, con);
         }

         child_count = getSubActivityCount(activity_id, con);

         if (child_count > 0) {

             // Print the portion of the link for this activity_id (all children of its parent).
             child_ids = getChildrenForActivity(activity_id, con);

             if (child_ids.size() == 1) {

                 // Only a single child found.  Print a plain link instead of a select object
                 out.print(" > <a href=\"javascript:void(" + child_ids.get(0) + ")\" onclick=\"reloadPageActId(" + child_ids.get(0) + ")\">" + getActivity.getActivityName(child_ids.get(0), con) + "</a>");

             } else {

                 // Multiple children found.  Print select box with children as options.
                 out.print(" > ");
                 //out.println("<select name=activity_id onchange=\"reloadPageActId(this.options[this.selectedIndex].value)\">");
                 out.println("<select name=activity_id_select onchange=\"reloadPageActId(this.options[this.selectedIndex].value)\">");

                 if (selected_activity_id == 0) out.println("<option selected value=\"0\">CHOOSE...</option>");

                 for (int i=0; i < child_ids.size(); i++) {
                     out.println("<option value=\"" + child_ids.get(i) + "\"" + (child_ids.get(i) == selected_activity_id ? " selected" : "") + ">" + getActivity.getActivityName(child_ids.get(i), con) + "</option>");
                 }

                 out.println("</select>");
             }
         }

     } catch (Exception exc) {
         Utilities.logError("getActivity.buildActivityTree: Error=" + exc.getMessage());
     } finally {

         try { rs.close(); }
         catch (Exception exc) { }

         try { pstmt.close(); }
         catch (Exception exc) { }
     }
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

 /**
  * getMaxConsecTimes - Returns the maximum
  *
  * @param user
  * @param activity_id
  * @param con
  * @return
  */
 public static int getMaxConsecTimes(String user, int activity_id, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     String consec_csv = "";

     int consec = 0;

     boolean isProshop = ProcessConstants.isProshopUser(user);

     try {

         pstmt = con.prepareStatement("SELECT " + (isProshop ? "consec_pro_csv" : "consec_mem_csv") + " FROM activities WHERE activity_id = ?");
         pstmt.clearParameters();
         pstmt.setInt(1, activity_id);

         rs = pstmt.executeQuery();

         if (rs.next()) {
             consec_csv = rs.getString((isProshop ? "consec_pro_csv" : "consec_mem_csv"));

             if (!consec_csv.equals("")) {

                 String[] consec_split = consec_csv.split(",");
                 int temp = 0;

                 for (int i=0; i<consec_split.length; i++) {

                     try {

                         temp = Integer.parseInt(consec_split[i]);

                     } catch (Exception exc) {
                         temp = -1;
                     }

                     if (temp > consec) consec = temp;
                 }
             }
         }

     } catch (Exception exc) {

         consec = -1;

     } finally {

         try { rs.close(); }
         catch (Exception ignore) { }

         try { pstmt.close(); }
         catch (Exception ignore) { }
     }

     return consec;
 }

 public static ArrayList<Integer> getOrderedConsecList(String club, String user, String day_name, int activity_id, Connection con) {


     PreparedStatement pstmt = null;
     ResultSet rs = null;

     String consec_csv = "";

     ArrayList<Integer> consec = new ArrayList<Integer>();

     boolean isProshop = ProcessConstants.isProshopUser(user);

     if (club.equals("wingedfoot")) {
         
         if (day_name.equalsIgnoreCase("Saturday") || day_name.equalsIgnoreCase("Sunday")) {
             
             consec.add(2);
             consec.add(3);
             
         } else {
             
             consec.add(2);
             consec.add(1);
         }
         
     } else {
         
         try {

             pstmt = con.prepareStatement("SELECT " + (isProshop ? "consec_pro_csv" : "consec_mem_csv") + " FROM activities WHERE activity_id = ?");
             pstmt.clearParameters();
             pstmt.setInt(1, activity_id);

             rs = pstmt.executeQuery();

             if (rs.next()) {
                 consec_csv = rs.getString((isProshop ? "consec_pro_csv" : "consec_mem_csv")).trim();

                 if (!consec_csv.equals("")) {

                     String[] consec_split = consec_csv.split(",");
                     int temp = 0;

                     for (int i=0; i<consec_split.length; i++) {

                         try {
                             temp = Integer.parseInt(consec_split[i]);
                             consec.add(temp);
                         } catch (Exception ignore) { }
                     }
                 }
             }

             if (consec.size() > 1) Collections.sort(consec);

         } catch (Exception exc) {

             consec.clear();

         } finally {

             try { rs.close(); }
             catch (Exception ignore) { }

             try { pstmt.close(); }
             catch (Exception ignore) { }
         }
     }

     return consec;
 }
 
 
 /**
  * getColsPerRow - Returns the number of columns to print per row for the FlxRez summary view
  * 
  * @param activity_id Activity_id to check for
  * @param con Connection to the club's database
  * 
  * @return cols_per_row - Number of columns per row to display
  */
 public static int getColsPerRow(int activity_id, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     int cols_per_row = 10; // default is 10
     
     try {
         
         pstmt = con.prepareStatement("SELECT cols_per_row FROM activities WHERE activity_id = ?");
         pstmt.clearParameters();
         pstmt.setInt(1, activity_id);
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {
             if (rs.getInt("cols_per_row") != 0) cols_per_row = rs.getInt("cols_per_row");
         }
         
     } catch (Exception exc) {
         cols_per_row = 10;
         Utilities.logError("ERROR: getActivity.getColsPerRow() club=" + Utilities.getClubName(con, true) + ", activity_id=" + activity_id + ", err=" + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) { }
         
         try { pstmt.close(); }
         catch (Exception ignore) { }
     }
     
     return cols_per_row;
 }
 
}