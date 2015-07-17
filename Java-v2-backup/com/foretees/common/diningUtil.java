/***************************************************************************************
 *   diningUtil:  This class will hold all the data needed for a dining reservation.
 *
 *
 *   called by:  several
 *
 *   created: 05/17/2011   Paul S.
 *
 *   last updated:
 *
 *
 ***************************************************************************************
 */
package com.foretees.common;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.*;
import java.util.Calendar;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import javax.mail.*;
import javax.mail.internet.*;
import javax.xml.parsers.*;

import com.google.gson.*; // for json

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gson.*;
import com.google.gson.reflect.*;

import org.apache.commons.lang.*;
import java.lang.reflect.Type;

public class diningUtil {

    public static final int VIEW = 0; // No availible times (still clickable)
    public static final int OPEN = 1; // Available times
    public static final int MINIMUM = 0;
    public static final int MAXIMUM = 1;
    public static final int START = 0;
    public static final int END = 1;
    public static final int DEFAULT = 2;
    public static final int OFFSET = 0;
    public static final int MEAL_PERIOD_OFFSET = 1;
    public static final int CLOSE_TIME = 2;
    public static final int FML = 0; // First M Last
    public static final int LFM = 1; // Last, First M
    private static final int DEFAULT_MINUTES_BEFORE = 60;
    private static final int DEFAULT_RESERVATION_BLOCK = 90; // How many minutes + or - will we allow a reservation
    
    private static final String req_person_id_cache = "req_person_id"; // This caching should be moved to reqUtil.

    //private static final String DINING_SELF_SERVICE  = "http://dining.foretees.com/self_service/";
    //private static final String DINING_PORTAL  = "http://dining.foretees.com/portal/reservations/";
    
    //private static final String DINING_SELF_SERVICE  = "http://10.0.0.35/self_service/";
    //private static final String DINING_PORTAL  = "http://10.0.0.35/portal/reservations/";
    
    // dining03
    private static final String DINING_SELF_SERVICE  = "http://10.0.0.82/self_service/";
    private static final String DINING_PORTAL  = "http://10.0.0.82/portal/reservations/";

    //private static final int DEFAULT_CLOSE_TIME = 2200; // default close reservation at 10:00PM
    /*
     * Static maps
     */
    private static final Map<String, Integer> customDaysInAdvance = customDaysInAdvance();

    private static Map<String, Integer> customDaysInAdvance() {
        Map<String, Integer> result = new HashMap<String, Integer>();

        /* Default is 1 */
        /* This should be moved to a club database parameter */
        result.put("bluehillscc", 0);    // Club name, days in advanced to start to allow new "a la carte" registrations
        result.put("ccyork", 0);
        result.put("chartwellgcc", 0);
        result.put("denvercc", 0);
        result.put("esterocc", 0);
        result.put("meadowbrook", 0);
        result.put("minikahda", 0); 
        result.put("peninsula", 0);
        result.put("whippoorwillclub", 0);

        return result;
    }

    public static int getDefaultDaysInAdvance(HttpServletRequest req) {

        /* This should be moved to a club database parameter */
        String club = Utilities.getSessionString(req, "club", "");
        Integer result = customDaysInAdvance.get(club);
        return result != null ? result : 1; // Defaullt to 1 day in advance if no custom is set

    }
    public static final Map<String, Object> occasionMap = buildOccasionMap();

    private static Map<String, Object> buildOccasionMap() {
        // Create special occasions list
        // We reverse what would normally be key with value
        // for use in javascript form builder.
        // This complicates lookups, so we should eventually update the
        // javascript form builder, and the maps we pass to it, to be a little more
        // elegant, though this would require changes to just about every slot servlet
        // Probably the best, most flexible, approach would be to have an array of maps rather than a map of objects
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("No Special Occasion", "0");
        result.put("Birthday", "1");
        result.put("Anniversary", "2");
        result.put("Graduation", "3");
        result.put("Promotion", "4");
        result.put("Groom's Dinner", "5");
        result.put("Engagement", "6");
        result.put("Other", "7");
        result.put("Special Occasion", "8");

        return result;
    }
    
    private static final Map<String, String[]> clubNotificationMap = buildClubEmailNotifictionMap();
    
    private static Map<String, String[]> buildClubEmailNotifictionMap() {
        
        Map<String, String[]> result = new LinkedHashMap<String, String[]>();
        result.put("mirabel", new String[]{"reservations@mirabel.com"});
        result.put("denvercc", new String[]{"diningreservations@denvercc.net"});
        result.put("peninsula", new String[]{"james.cunningham@thepeninsulaclub.com"});
        result.put("hyperion", new String[]{"emeyer@hyperionfc.com"});
        result.put("ccyork", new String[]{"reservations@ccyork.org"}); 
        result.put("belairbayclub", new String[]{"online_reservations@belairbayclub.com"});
        result.put("stcloudcc", new String[]{"ryand@stcloudcountryclub.com"});
        result.put("deserthighlands", new String[]{"recept@deserthighlandsscottsdale.com"}); 
        result.put("glenoaks", new String[]{"receptionist@glenoakscc.com", "jessica.harmon@glenoakscc.com"}); 
        result.put("lacumbrecc", new String[]{"info@lacumbrecc.org"});
        result.put("bluehillscc", new String[]{"nhirt@bluehillscc.com"});
        result.put("martiscamp", new String[]{"concierge@martiscamp.com"});
        result.put("brooklawn", new String[]{"Liz@brooklawncc.com", "andrew@brooklawncc.com", "brian@brooklawncc.com"});
        result.put("lakewood", new String[]{"reception@lakewoodcountryclub.net"});
        result.put("lascampanas", new String[]{"lcconcierge@clublc.com"});
        //result.put("demotom", "psindelar@foretees.com");

        return result;
    }
    
    public static String[] getClubNotificationAddress(Connection con_d, HttpServletRequest req){
        
        String club = Utilities.getSessionString(req, "club", "");
        
        return clubNotificationMap.get(club.toLowerCase());

    }

    public static int getDefaultCovers(int maxCovers, int location_id, int event_id, Connection con_d, HttpServletRequest req) {

        //String club = Utilities.getSessionString(req, "club", "");

        int result = 1;

        if (event_id > 0) {
            result = 1; // maxCovers;
        }

        return result;

    }

    public static int dayStatus(int organization_id, int date, HttpServletRequest req, Connection con_d) {

        boolean hasTimes = true;
        /*
         * Code here to check if day has availible times
         */

        if (hasTimes) {
            return OPEN;
        } else {
            return VIEW;
        }

    }

    public static List<Integer> daysList(int organization_id, int event_id, int startDate, int endDate, HttpServletRequest req, Connection con_d) {


        List<Integer> daysList = new ArrayList<Integer>();

        int days = timeUtil.daysBetween(startDate, endDate);

        //
        //  build list (1 entry per day, relative to today)
        //
        //int dayVal = 0;
        for (int i = 0; i < days; i++) {

            daysList.add(dayStatus(organization_id, timeUtil.add(req, startDate, 0, Calendar.DATE, i)[timeUtil.DATE], req, con_d));

        }

        return daysList;

    }

    public static diningLocation getLocationData(Connection con_d, int location_id, int event_id) {


        PreparedStatement pstmt = null;
        ResultSet rs = null;
        diningLocation result = null;


        try {

            if (event_id > 0) {

                pstmt = con_d.prepareStatement(""
                        + "SELECT id, name, (SELECT e.maximum_party_size FROM events AS e WHERE e.id = ?) AS dining_maximum_online_size, (SELECT e.maximum_party_size FROM events AS e WHERE e.id = ?) AS dining_maximum_party_size "
                        + "FROM locations "
                        + "WHERE id = ?");

                pstmt.setInt(1, event_id);
                pstmt.setInt(2, event_id);
                pstmt.setInt(3, location_id);

            } else {

                pstmt = con_d.prepareStatement(""
                        + "SELECT id, name, dining_maximum_online_size, dining_maximum_party_size "
                        + "FROM locations "
                        + "WHERE id = ?");

                pstmt.setInt(1, location_id);

            }

            rs = pstmt.executeQuery();

            if (rs.next()) {

                result = new diningLocation(
                        rs.getInt("id"),
                        rs.getString("name"),
                        0,
                        rs.getInt("dining_maximum_party_size"),
                        rs.getInt("dining_maximum_online_size"));

            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.getLocationData: location_id=" + location_id + ", event_id=" + event_id + ", Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return result;

    }

    public static String getOccasionName(int id) {

        String sid = Integer.toString(id);
        for (Map.Entry<String, Object> entry : occasionMap.entrySet()) {
            if (((String) entry.getValue()).equals(sid)) {
                return entry.getKey();
            }
        }
        return "";
    }

    public static String getUserIdentity(int person_id, Connection con_d) {


        String result = "";

        if (person_id > 0) {

            boolean close_con = false;

            if (con_d == null) {

                con_d = Connect.getDiningCon();
                close_con = true;
            }

            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {

                pstmt = con_d.prepareStatement(""
                        + "SELECT user_identity "
                        + "FROM people "
                        + "WHERE id = ?");

                pstmt.setInt(1, person_id);

                rs = pstmt.executeQuery();

                if (rs.next()) {

                    result = rs.getString(1);

                }

            } catch (Exception exc) {

                Utilities.logError("diningUtil.getUserIdentity: Err=" + exc.toString());

            } finally {

                try {
                    rs.close();
                } catch (Exception ignore) {
                }

                try {
                    pstmt.close();
                } catch (Exception ignore) {
                }

                if (close_con) {
                    try {
                        con_d.close();
                    } catch (Exception ignore) {
                    }
                }

            }

        }

        return result;

    }

    public static boolean areDiningReservationsPublic(int organization_id, Connection con_d) {


        boolean result = false; // default to not being able to see them
        boolean close_con = false;

        if (con_d == null) {

            con_d = Connect.getDiningCon();
            close_con = true;
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT members_can_see_other_members_dining_reservations "
                    + "FROM organizations "
                    + "WHERE id = ?");

            pstmt.setInt(1, organization_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                result = rs.getBoolean(1);
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.areDiningReservationsPublic: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

            if (close_con) {
                try {
                    con_d.close();
                } catch (Exception ignore) {
                }
            }

        }

        return result;

    }

    public static boolean areEventReservationsPublic(int organization_id, Connection con_d) {


        boolean result = false; // default to not being able to see them
        boolean close_con = false;

        if (con_d == null) {

            con_d = Connect.getDiningCon();
            close_con = true;
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT members_can_see_other_members_event_reservations "
                    + "FROM organizations "
                    + "WHERE id = ?");

            pstmt.setInt(1, organization_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                result = rs.getBoolean(1);
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.areEventReservationsPublic: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

            if (close_con) {
                try {
                    con_d.close();
                } catch (Exception ignore) {
                }
            }

        }

        return result;

    }

    public static parmDining2 getConflictingReservation(int organization_id, int reservation_id, int location_id, int person_id, diningEvent event, int date, int time, Connection con_d, HttpServletRequest req) {

        parmDining2 result = null;

        if (person_id == 0) {
            // Don't check guests
            return null;
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        long start_utime, end_utime;
        int event_id = 0;

        if (event != null && event.id > 0) {
            event_id = event.id;
        }
        start_utime = timeUtil.addClubUnixTimeMinutes(req, date, time, 0 - (DEFAULT_RESERVATION_BLOCK-1)); // n Minutes before
        end_utime = timeUtil.addClubUnixTimeMinutes(req, date, time, (DEFAULT_RESERVATION_BLOCK-1)); // n Minutes after
        
        //long test = timeUtil.getClubUnixTime(req, date, time);
        //int[] test2 = timeUtil.getClubDateTime(req, test);

        int[] startDateTime = timeUtil.getClubDateTime(req, start_utime);
        int[] endDateTime = timeUtil.getClubDateTime(req, end_utime);

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT r.id AS id, r.parent_id AS parent_id "
                    + " FROM reservations r "
                    + "  LEFT OUTER JOIN events e "
                    + "   ON e.id = r.event_id "
                    + " WHERE r.organization_id = ? "
                    + "  AND r.person_id = ? "
                    + "  AND ( "
                    + "   ( " // a la carte reservation check
                    + "    r.date BETWEEN CAST(? AS DATE) AND CAST(? AS DATE) "
                    + "    AND "
                    + "    r.time BETWEEN CAST(? AS TIME) AND CAST(? AS TIME) "
                    + "   ) "
                    + "   OR "
                    + "   ( " // event reservation check
                    + "    e.id IS NOT NULL "
                    + "    AND e.id = ? "
                    + "    AND ? > 0 "
                    + "   )"
                    + "  ) "
                    + "  AND r.state <> 'cancelled' ");
            
            //Utilities.logError("diningUtil.getConflictingReservation: Testing Date:" + timeUtil.getDbDate(startDateTime[timeUtil.DATE]) + " - "+ timeUtil.getDbDate(endDateTime[timeUtil.DATE]));
            //Utilities.logError("diningUtil.getConflictingReservation: Testing Time:" + timeUtil.get24HourTime(startDateTime[timeUtil.TIME]) + " - "+ timeUtil.get24HourTime(endDateTime[timeUtil.TIME]) + " : "+ time + " - "+ test2[timeUtil.TIME]+" :: "+test+" :: "+ timeUtil.getClubTimeZone(req).getID());
            
            pstmt.setInt(1, organization_id);
            pstmt.setInt(2, person_id);
            // a la carte reservation check
            pstmt.setString(3, timeUtil.getDbDate(startDateTime[timeUtil.DATE]));
            pstmt.setString(4, timeUtil.getDbDate(endDateTime[timeUtil.DATE]));
            pstmt.setString(5, timeUtil.get24HourTime(startDateTime[timeUtil.TIME]));
            pstmt.setString(6, timeUtil.get24HourTime(endDateTime[timeUtil.TIME]));
            // event reservation check
            pstmt.setInt(7, event_id);
            pstmt.setInt(8, event_id);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                // Look like we have a conflicting reservation for this user
                // Load the reservation and return it
                int conflict_id = rs.getInt("id");
                int parent_conflict_id = rs.getInt("parent_id");
                if (conflict_id > 0 && ((conflict_id != reservation_id && parent_conflict_id != reservation_id) || reservation_id == 0)) {
                    //Connection con = Connect.getCon(req);
                    result = new parmDining2(organization_id, conflict_id, con_d, req);
                }
            }


        } catch (Exception exc) {
            Utilities.logError("diningUtil.getConflictingReservation: Err=" + exc.toString());
        } finally {
            try {
                rs.close();
            } catch (Exception ignore) {
            }
            try {
                pstmt.close();
            } catch (Exception ignore) {
            }
        }


        return result;
    }

    public static int getValidMasterId(int organization_id, int reservation_id, Connection con_d) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int result = 0;

        try {

            int master_id = getMasterId(organization_id, reservation_id, con_d);

            pstmt = con_d.prepareStatement(""
                    + "SELECT id "
                    + " FROM reservations "
                    + " WHERE organization_id = ?"
                    + "  AND id = ? "
                    + "  AND parent_id IS NULL "
                    + "  AND state <> 'cancelled' ");

            pstmt.setInt(1, organization_id);
            pstmt.setInt(2, master_id);
            rs = pstmt.executeQuery();

            if (rs.next() && master_id > 0) {
                // We don't appear to need repair
                result = rs.getInt("id");

            }

        } catch (Exception exc) {
            Utilities.logError("diningUtil.getValidMasterId: Err=" + exc.toString());
        } finally {
            try {
                rs.close();
            } catch (Exception ignore) {
            }
            try {
                pstmt.close();
            } catch (Exception ignore) {
            }
        }

        return result;

    }
    
    public static int getValidMasterIdFromReservationNumber(int organization_id, int reservation_number, Connection con_d) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int result = 0;

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT id "
                    + " FROM reservations "
                    + " WHERE organization_id = ?"
                    + "  AND reservation_number = ? "
                    + "  AND parent_id IS NULL "
                    + "  AND state <> 'cancelled' ");

            pstmt.setInt(1, organization_id);
            pstmt.setInt(2, reservation_number);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // We don't appear to need repair
                result = rs.getInt("id");

            }

        } catch (Exception exc) {
            Utilities.logError("diningUtil.getValidMasterIdFromReservationNumber: Err=" + exc.toString());
        } finally {
            try {
                rs.close();
            } catch (Exception ignore) {
            }
            try {
                pstmt.close();
            } catch (Exception ignore) {
            }
        }

        return result;

    }

    public static void cleanUpReservation(int organization_id, int reservation_id, Connection con_d) {
        // Fix Cover counts, number of checks, etc.

        if (reservation_id == 0) {
            return; // New reservations don't need this
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean autoCommitMode = false;
        try {
            // Save our current auto commit status
            autoCommitMode = con_d.getAutoCommit();
        } catch (Exception exc) {
            Utilities.logError("diningUtil.cleanUpReservation: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
        }

        try {

            con_d.setAutoCommit(false); // enable transactions if they're not already

            // Check that the reservation id is valid
            int master_id = getValidMasterId(organization_id, reservation_id, con_d);
            // Get reservation number for this group
            int reservation_number = getReservationNumber(organization_id, master_id, con_d);
            if (master_id > 0 && reservation_number > 0) {

                // Get cover count
                pstmt = con_d.prepareStatement(""
                        + "SELECT count(*) as cover_count "
                        + " FROM reservations "
                        + " WHERE organization_id = ? "
                        + "  AND reservation_number = ? "
                        + "  AND state <> 'cancelled' ");

                pstmt.setInt(1, organization_id);
                pstmt.setInt(2, reservation_number);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    // We found a cover count
                    int cover_count = rs.getInt("cover_count");

                    // Now get the total number of distinct check numbers



                    // Update the new master reservation with information from the old master 
                    pstmt = con_d.prepareStatement(""
                            + "UPDATE reservations "
                            + " SET "
                            + "   member_special_requests = ? "
                            // Anything else??
                            + " WHERE organization_id = ? "
                            + "  AND id = ? ");

                    //pstmt.setString(1, rs.getString("member_special_requests"));
                    //pstmt.setInt(2, organization_id);
                    //pstmt.setInt(3, new_master_id);
                    pstmt.executeUpdate();


                    con_d.commit(); // commit our changes

                } else {
                    // No old master found.  Do nothing 
                    con_d.rollback();
                }
            } else {
                // New master doesn't appear to be a valid master.  Do nothing.
                con_d.rollback();
            }

        } catch (Exception exc) {
            Utilities.logError("diningUtil.cleanUpReservation: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
            try {
                con_d.rollback(); // Undo any issues
            } catch (Exception e) {
                Utilities.logError("diningUtil.cleanUpReservation: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            }
        } finally {
            try {
                rs.close();
            } catch (Exception ignore) {
            }
            try {
                pstmt.close();
            } catch (Exception ignore) {
            }
        }

        try {
            // Revert auto commit to what it was before
            con_d.setAutoCommit(autoCommitMode);
        } catch (Exception e) {
            Utilities.logError("diningUtil.cleanUpReservation: Err=" + e.toString());
        }

    }

    public static void migrateDataToNewMaster(int organization_id, int old_master_id, int new_master_id, Connection con_d) {
        // Because the dining system's master reservation re-delegation doesn't move some important
        // data to the new master, we'll have to do it here

        if (old_master_id == 0 || new_master_id == 0) {
            return; // New reservations don't need this workaround
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean autoCommitMode = false;
        try {
            // Save our current auto commit status
            autoCommitMode = con_d.getAutoCommit();
        } catch (Exception exc) {
            Utilities.logError("diningUtil.migrateDataToNewMaster: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
        }

        try {

            con_d.setAutoCommit(false); // enable transactions if they're not already

            // Check that the org and reservation id are valid
            int master_id = getValidMasterId(organization_id, new_master_id, con_d);
            if (master_id > 0) {
                // Get old, now cancelled, master reservation
                pstmt = con_d.prepareStatement(""
                        + "SELECT * "
                        + " FROM reservations "
                        + " WHERE organization_id = ? "
                        + "  AND id = ? "
                        + "  AND state = 'cancelled' "
                        + "  AND parent_id IS NULL ");

                pstmt.setInt(1, organization_id);
                pstmt.setInt(2, old_master_id);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    // We found the old master reservation

                    // Update the new master reservation with information from the old master 
                    pstmt = con_d.prepareStatement(""
                            + "UPDATE reservations "
                            + " SET "
                            + "   member_special_requests = ? "
                            // Anything else??
                            + " WHERE organization_id = ? "
                            + "  AND id = ? ");

                    pstmt.setString(1, rs.getString("member_special_requests"));
                    pstmt.setInt(2, organization_id);
                    pstmt.setInt(3, new_master_id);
                    pstmt.executeUpdate();

                    // Remove any potentialy conflicting answers from the new master reservation
                    pstmt = con_d.prepareStatement(""
                            + "DELETE FROM answers a "
                            + " WHERE a.reservation_id = ? "
                            + "  AND a.question_id IN "
                            + "  ("
                            + "   SELECT a2.question_id "
                            + "    FROM answers a2 "
                            + "     INNER JOIN questions q "
                            + "      ON q.id = a2.question_id "
                            + "       AND q.for_whole_party = 'true' "
                            + "    WHERE a2.reservation_id = ? "
                            + "  ) ");

                    pstmt.setInt(1, new_master_id);
                    pstmt.setInt(2, old_master_id);
                    pstmt.executeUpdate();

                    // Copy group/whole party answers from the old master to the new master
                    pstmt = con_d.prepareStatement(""
                            + "INSERT INTO answers "
                            + " (question_id, reservation_id, answer_text, question_text, lock_version) "
                            + " SELECT a.question_id, ?, a.answer_text, q.question_text, a.lock_version "
                            + "  FROM answers a "
                            + "   INNER JOIN questions q "
                            + "    ON q.id = a.question_id "
                            + "     AND q.for_whole_party = 'true' "
                            + "  WHERE a.reservation_id = ? ");

                    pstmt.setInt(1, new_master_id);
                    pstmt.setInt(2, old_master_id);
                    pstmt.executeUpdate();

                    con_d.commit(); // commit our changes

                } else {
                    // No old master found.  Do nothing 
                    con_d.rollback();
                }
            } else {
                // New master doesn't appear to be a valid master.  Do nothing.
                con_d.rollback();
            }

        } catch (Exception exc) {
            Utilities.logError("diningUtil.migrateDataToNewMaster: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
            try {
                con_d.rollback(); // Undo any issues
            } catch (Exception e) {
                Utilities.logError("diningUtil.migrateDataToNewMaster: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            }
        } finally {
            try {
                rs.close();
            } catch (Exception ignore) {
            }
            try {
                pstmt.close();
            } catch (Exception ignore) {
            }
        }

        try {
            // Revert auto commit to what it was before
            con_d.setAutoCommit(autoCommitMode);
        } catch (Exception e) {
            Utilities.logError("diningUtil.migrateDataToNewMaster: Err=" + e.toString());
        }

    }
    
    public static void cleanQuestionRecords(int organization_id, int reservation_id, List<Integer> question_ids, Connection con_d) {
        
        // Work around bug in dining system where updating master reservation with new answers (usually as a result of changing location)
        // will not save the new answer.  Seems to be because the update codepath on the dining system does not create an answer record if it does not exist.

        if (reservation_id == 0) {
            return; // New reservations don't need this workaround
        }
        
        String questionFilter = ""; // remove all questions fro this reservation
        if(!question_ids.isEmpty()){
            questionFilter = " AND question_id NOT IN ("+StringUtils.join(question_ids,",") +") "; // remove unasked questions
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean autoCommitMode = false;
        try {
            // Save our current auto commit status
            autoCommitMode = con_d.getAutoCommit();
        } catch (Exception exc) {
            Utilities.logError("diningUtil.cleanQuestionRecords: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
        }

        try {

            con_d.setAutoCommit(false); // enable transactions if they're not already

            // Check that the org and reservation id are valid
            int master_id = getValidMasterId(organization_id, reservation_id, con_d);
            if (master_id > 0) {

                // Remove answers that have no questions
                pstmt = con_d.prepareStatement(""
                        + "DELETE FROM answers "
                        + " WHERE reservation_id = ? "
                        + questionFilter);

                pstmt.setInt(1, reservation_id);
                pstmt.executeUpdate();

                con_d.commit(); // commit our changes
            } else {
                //con_d.rollback(); // Undo any issues
            }

        } catch (Exception exc) {
            Utilities.logError("diningUtil.cleanQuestionRecords: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
            try {
                con_d.rollback(); // Undo any issues
            } catch (Exception e) {
                Utilities.logError("diningUtil.cleanQuestionRecords: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            }
        } finally {
            try {
                rs.close();
            } catch (Exception ignore) {
            }
            try {
                pstmt.close();
            } catch (Exception ignore) {
            }
        }

        try {
            // Revert auto commit to what it was before
            con_d.setAutoCommit(autoCommitMode);
        } catch (Exception e) {
            Utilities.logError("diningUtil.cleanQuestionRecords: Err=" + e.toString());
        }

    }

    public static void repairQuestionRecord(int organization_id, int reservation_id, int question_id, Connection con_d) {
        // Work around bug in dining system where updating master reservation with new answers (usually as a result of changing location)
        // will not save the new answer.  Seems to be because the update codepath on the dining system does not create an answer record if it does not exist.

        if (reservation_id == 0) {
            return; // New reservations don't need this workaround
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean autoCommitMode = false;
        try {
            // Save our current auto commit status
            autoCommitMode = con_d.getAutoCommit();
        } catch (Exception exc) {
            Utilities.logError("diningUtil.repairQuestionRecord: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
        }

        try {

            con_d.setAutoCommit(false); // enable transactions if they're not already

            // Check that the org and reservation id are valid
            int master_id = getValidMasterId(organization_id, reservation_id, con_d);
            if (master_id > 0) {
                // Check if the answer record exists
                pstmt = con_d.prepareStatement(""
                        + "SELECT id "
                        + " FROM answers "
                        + " WHERE question_id = ? "
                        + "  AND reservation_id = ? ");

                pstmt.setInt(1, question_id);
                pstmt.setInt(2, reservation_id);
                rs = pstmt.executeQuery();

                if (!rs.next()) {
                    // No record exists.  We'll need to create one
                    pstmt = con_d.prepareStatement(""
                            + "INSERT INTO answers "
                            + " (question_id, reservation_id, answer_text, question_text, lock_version) "
                            + " VALUES (?, ?, '', '', 0) ");

                    pstmt.setInt(1, question_id);
                    pstmt.setInt(2, reservation_id);
                    pstmt.executeUpdate();

                    con_d.commit(); // commit our changes
                } else {
                    // Exists.  Nothing needed
                    con_d.rollback();
                }
            } else {
                // Invalid reservation.  Do nothing.
                con_d.rollback();
            }

        } catch (Exception exc) {
            Utilities.logError("diningUtil.repairQuestionRecord: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
            try {
                con_d.rollback(); // Undo any issues
            } catch (Exception e) {
                Utilities.logError("diningUtil.repairQuestionRecord: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            }
        } finally {
            try {
                rs.close();
            } catch (Exception ignore) {
            }
            try {
                pstmt.close();
            } catch (Exception ignore) {
            }
        }

        try {
            // Revert auto commit to what it was before
            con_d.setAutoCommit(autoCommitMode);
        } catch (Exception e) {
            Utilities.logError("diningUtil.repairQuestionRecord: Err=" + e.toString());
        }

    }

    public static int getReservationNumber(int organization_id, int reservation_id, Connection con_d) {

        int result = 0;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            // Get the reservation number
            pstmt = con_d.prepareStatement(""
                    + "SELECT reservation_number "
                    + " FROM reservations "
                    + " WHERE organization_id = ?"
                    + "  AND id = ? "
                    + "  AND reservation_number IS NOT NULL ");

            pstmt.setInt(1, organization_id);
            pstmt.setInt(2, reservation_id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                result = rs.getInt("reservation_number");

            }

        } catch (Exception exc) {
            Utilities.logError("diningUtil.getReservationNumber: Err=" + exc.toString());
        } finally {
            try {
                rs.close();
            } catch (Exception ignore) {
            }
            try {
                pstmt.close();
            } catch (Exception ignore) {
            }
        }

        return result;

    }

    public static boolean isCancelled(int organization_id, int reservation_id, Connection con_d) {

        boolean result = true;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int reservation_number = getReservationNumber(organization_id, reservation_id, con_d);

        if (reservation_number > 0) {
            try {

                // Check if there's a valid master
                //int master_id = getMasterId(organization_id, reservation_id, con_d);
                pstmt = con_d.prepareStatement(""
                        + "SELECT count(*) "
                        + " FROM reservations "
                        + " WHERE organization_id = ?"
                        + "  AND reservation_number = ? "
                        + "  AND state <> 'cancelled'");

                pstmt.setInt(1, organization_id);
                pstmt.setInt(2, reservation_number);
                rs = pstmt.executeQuery();

                if (rs.next()) {

                    // We have uncancelled reservations
                    result = false;

                }

            } catch (Exception exc) {
                Utilities.logError("diningUtil.isCancelled: Err=" + exc.toString());
            } finally {
                try {
                    rs.close();
                } catch (Exception ignore) {
                }
                try {
                    pstmt.close();
                } catch (Exception ignore) {
                }
            }
        }

        return result;

    }

    public static int repairReservation(int organization_id, int reservation_id, Connection con_d) {

        // If we got here, it's likely because there is no valid master reservation record found for a given reservation id.
        // A valid master reservation record requires a NULL parent_id and must not be cancelled.

        // We'll try to repair the reservation by selecting the reservation_number from any record (even cancelled) 
        // with the provided ID, regardless of its parent_id status, then select a minimum uncancelled ID 
        // having the same reservation_number.  Once we get that ID, we need to update it's record 
        // as the master, and then any associated records that need to point to it, leaving the state of 
        // each record (cancelled, etc.) untouched, but updating the parent_id to the new id. 

        // Is there anything else we need to do?

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int result = 0;

        boolean autoCommitMode = false;
        try {
            // Save our current auto commit status
            autoCommitMode = con_d.getAutoCommit();
        } catch (Exception exc) {
            Utilities.logError("diningUtil.repairReservation: Err=" + exc.toString());
        }

        try {

            con_d.setAutoCommit(false); // enable transactions if they're not already

            // Check if there is a valid master
            int master_id = getValidMasterId(organization_id, reservation_id, con_d);
            if (master_id > 0) {
                // We don't appear to need repair
                result = master_id;
            } else {

                // No valid master.  See if we can fix that

                // Get the reservation number
                int reservation_number = getReservationNumber(organization_id, reservation_id, con_d);

                if (reservation_number > 0) {

                    // Get the minimum un-canceled ID having that reservation number
                    pstmt = con_d.prepareStatement(""
                            + "SELECT min(id) AS id "
                            + " FROM reservations "
                            + " WHERE organization_id = ? "
                            + "  AND reservation_number = ? "
                            + "  AND state <> 'cancelled' "
                            + "  AND id IS NOT NULL ");

                    pstmt.setInt(1, organization_id);
                    pstmt.setInt(2, reservation_number);
                    rs = pstmt.executeQuery();

                    if (rs.next()) {

                        int new_master_id = rs.getInt("id");

                        if (new_master_id > 0) {

                            // Try converting this to a master
                            pstmt = con_d.prepareStatement(""
                                    + "UPDATE reservations "
                                    + " SET parent_id = NULL " // Anything else we have to set?
                                    + " WHERE organization_id = ? "
                                    + "  AND id = ? ");

                            pstmt.setInt(1, organization_id);
                            pstmt.setInt(2, new_master_id);
                            pstmt.executeUpdate();

                            // Update children to this new master
                            pstmt = con_d.prepareStatement(""
                                    + "UPDATE reservations "
                                    + " SET parent_id = ?"
                                    + " WHERE organization_id = ? "
                                    + "  AND reservation_number = ? "
                                    + "  AND id <> ? ");

                            pstmt.setInt(1, new_master_id);
                            pstmt.setInt(2, organization_id);
                            pstmt.setInt(3, reservation_number);
                            pstmt.setInt(4, new_master_id);
                            pstmt.executeUpdate();

                            con_d.commit(); // commit our changes

                            // Log that we had to do this
                            Utilities.logError("diningUtil.repairReservation *NOTICE* Repaired dining reservation_number:" + reservation_number + " for organization_id:" + organization_id + " setting reservation_id:" + new_master_id + " as new master.");

                            result = new_master_id;
                        } else {
                            // Reservation appears to be beyond repair.
                            con_d.rollback();

                            Utilities.logError("diningUtil.repairReservation *NOTICE* Unable to repaired dining reservation_number:" + reservation_number + " for organization_id:" + organization_id + ", reservation_id:" + reservation_id + ".");
                        }

                    } else {
                        // Reservation appears to be beyond repair.
                        con_d.rollback();

                        Utilities.logError("diningUtil.repairReservation *NOTICE* Unable to repaired dining reservation_number:" + reservation_number + " for organization_id:" + organization_id + ", reservation_id:" + reservation_id + ".");
                    }

                } else {
                    // No reservation number?
                    // Reservation appears to be beyond repair.
                    con_d.rollback();

                    Utilities.logError("diningUtil.repairReservation *NOTICE* Unable to repaired dining reservation_number:" + reservation_number + " for organization_id:" + organization_id + ", reservation_id:" + reservation_id + ".");
                }
            }

        } catch (Exception exc) {
            Utilities.logError("diningUtil.repairReservation: Err=" + exc.toString());
            try {
                con_d.rollback(); // Undo any issues
            } catch (Exception e) {
                Utilities.logError("diningUtil.repairReservation: Err=" + e.toString());
            }
        } finally {
            try {
                rs.close();
            } catch (Exception ignore) {
            }
            try {
                pstmt.close();
            } catch (Exception ignore) {
            }
        }

        try {
            // Revert auto commit to what it was before
            con_d.setAutoCommit(autoCommitMode);
        } catch (Exception e) {
            Utilities.logError("diningUtil.repairReservation: Err=" + e.toString());
        }

        return result;
    }

// canNonMasterReserveeEdit
    public static boolean canNonMasterReserveeEdit(int organization_id, Connection con_d) {


        boolean result = false; // default to only allowing the reservation maker (master reservee) to edit the reservation - true means anyone in that reservation can make changes to it
        boolean close_con = false;

        if (con_d == null) {

            con_d = Connect.getDiningCon();
            close_con = true;
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT members_can_edit_other_reservees_on_same_check "
                    + "FROM organizations "
                    + "WHERE id = ?");

            pstmt.setInt(1, organization_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                result = rs.getBoolean(1);
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.canNonMasterReserveeEdit: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

            if (close_con) {
                try {
                    con_d.close();
                } catch (Exception ignore) {
                }
            }

        }

        return result;

    }
    
    public static boolean canViewOthersEventSignups(int organization_id, Connection con_d) {


        boolean result = false; // default to only allowing the reservation maker (master reservee) to edit the reservation - true means anyone in that reservation can make changes to it
        boolean close_con = false;

        if (con_d == null) {

            con_d = Connect.getDiningCon();
            close_con = true;
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT members_can_see_other_members_event_reservations "
                    + "FROM organizations "
                    + "WHERE id = ?");

            pstmt.setInt(1, organization_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                result = rs.getBoolean(1);
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.canViewOthersEventSignups: Err=" + exc.toString());

        } finally {

            Connect.close(rs,pstmt);
            if (close_con) {
                Connect.close(con_d);
            }

        }

        return result;

    }
    
    public static int getOrganizationId(HttpServletRequest req) {

        Integer organization_id = reqUtil.getRequestInteger(req, "reqCacheDining_organizationId", null);
        if(organization_id != null){
            return organization_id;
        } else {
        
            Connection con = Connect.getCon(req);
            Statement stmt = null;
            ResultSet rs = null;

            organization_id = 0;

            try {

                stmt = con.createStatement();

                rs = stmt.executeQuery("SELECT organization_id FROM club5");

                if (rs.next()) {
                    organization_id = rs.getInt("organization_id");
                    req.setAttribute("reqCacheDining_organizationId", organization_id);
                }

            } catch (Exception exc) {

                organization_id = 0;
                //logError("diningUtil.getOrganizationId: Error looking up organization_id. Err=" + exc.getMessage() + ", strace=" + Utilities.getStackTraceAsString(exc));

            } finally {

                Connect.close(rs,stmt);

            }
            
            return organization_id;
            
        }

        
    }

    
    public static boolean canViewOthersSignups(HttpServletRequest req) {
        
        Boolean result = reqUtil.getRequestBoolean(req, "reqCacheDining_canViewOthersSignups", null);
        if (result != null) {
            return result;
        } else {
            result = canViewOthersSignups(getOrganizationId(req), null);
            req.setAttribute("reqCacheDining_canViewOthersSignups", result);
            return result;
        }
    }
    
    public static boolean canViewOthersSignups(int organization_id, Connection con_d) {


        boolean result = false; // default to only allowing the reservation maker (master reservee) to edit the reservation - true means anyone in that reservation can make changes to it
        boolean close_con = false;

        if (con_d == null) {

            con_d = Connect.getDiningCon();
            close_con = true;
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT members_can_see_other_members_dining_reservations "
                    + "FROM organizations "
                    + "WHERE id = ?");

            pstmt.setInt(1, organization_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                result = rs.getBoolean(1);
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.canViewOthersSignups: Err=" + exc.toString());

        } finally {

            Connect.close(rs,pstmt);
            if (close_con) {
                Connect.close(con_d);
            }

        }

        return result;

    }

    public static boolean isUserReservee(String user, parmDining2 parmD) {

        Map<String, Integer> user_lookup = buildUserMap(parmD.usernames, parmD.dining_id);

        return getMember(user, user_lookup) != null;

    }

    public static boolean isUserReservee(int reservation_id, int person_id, Connection con_d) {


        boolean result = false;
        boolean close_con = false;

        if (con_d == null) {

            con_d = Connect.getDiningCon();
            close_con = true;
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT id "
                    + " FROM reservations "
                    + " WHERE "
                    + "  (person_id = ? AND id = ? AND parent_id IS NULL)"
                    + "   OR (person_id = ? AND parent_id = ?)"
                    + "  AND state <> 'cancelled' ");

            pstmt.setInt(1, person_id);
            pstmt.setInt(2, reservation_id);
            pstmt.setInt(3, person_id);
            pstmt.setInt(4, reservation_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                result = true; // rs.getInt("taken_by_user_id") == Utilities.getUserId(person_id);
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.isUserMasterReservee: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

            if (close_con) {
                try {
                    con_d.close();
                } catch (Exception ignore) {
                }
            }

        }

        return result;

    }

    public static boolean isUserMasterReservee(int reservation_id, int person_id, Connection con_d) {


        boolean result = false;
        boolean close_con = false;

        if (con_d == null) {

            con_d = Connect.getDiningCon();
            close_con = true;
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT id "
                    + "FROM reservations "
                    + "WHERE id = ? AND parent_id IS NULL"
                    + "  AND state <> 'cancelled' ");

            pstmt.setInt(1, reservation_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                result = true; // rs.getInt("taken_by_user_id") == Utilities.getUserId(person_id);
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.isUserMasterReservee: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

            if (close_con) {
                try {
                    con_d.close();
                } catch (Exception ignore) {
                }
            }

        }

        return result;

    }

    public static int getMaxDays(int organization_id, diningEvent event, Connection con_d) {
        return getMinMaxDays(organization_id, event, con_d)[MAXIMUM];
    }

    public static int getMinDays(int organization_id, diningEvent event, Connection con_d) {
        return getMinMaxDays(organization_id, event, con_d)[MINIMUM];
    }

    public static int[] getMinMaxDays(int organization_id, diningEvent event, Connection con_d) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        int[] result = new int[]{0, 0};

        try {

            if (event.id > 0 || event.non_event_cache) {

                // This is an event, or we already used the event object as a cache for our non-event min/max values
                result = new int[]{event.minimum_advance_days, event.maximum_advance_days};

            } else {
                pstmt = con_d.prepareStatement(""
                        + "SELECT min(dining_minimum_advance_days) as minimum_advance_days, "
                        + " max(dining_maximum_advance_days) as maximum_advance_days "
                        + "FROM locations "
                        + "WHERE "
                        + " organization_id = ? AND "
                        + " dining_members_can_make_reservations = true AND "
                        + " deactivated = false");

                pstmt.setInt(1, organization_id);

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    // Cache our non-event min/max values in the event object for later use
                    event.minimum_advance_days = rs.getInt("minimum_advance_days");
                    event.maximum_advance_days = rs.getInt("maximum_advance_days");
                    event.non_event_cache = true;
                    result = new int[]{event.minimum_advance_days, event.maximum_advance_days};
                }
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.getMinMax: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return result;

    }

    public static boolean anyLocationAllowDayOf(int organization_id, Connection con_d) {


        boolean result = false;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT id "
                    + "FROM locations "
                    + "WHERE "
                    + "organization_id = ? AND "
                    + "dining_minimum_advance_days = 0 AND "
                    + "dining_members_can_make_reservations = true AND "
                    + "deactivated = false");

            pstmt.setInt(1, organization_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                result = true;
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.anyLocationAllowDayOf: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return result;

    }

    public static ArrayList<diningLocation> getDiningLocations(int organization_id, int days_in_advance, int location_id, int event_id, Connection con_d) {

        if (event_id < 1) {
            // If not an event, return non-event location list
            return getDiningLocations(organization_id, days_in_advance, location_id, con_d);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList<diningLocation> result = new ArrayList<diningLocation>();

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT b.covers, b.location_id, l.name AS location_name, "
                    + "  e.maximum_party_size, b.maximum_online_size "
                    + " FROM events AS e "
                    + "  LEFT OUTER JOIN bookings AS b "
                    + "   ON b.event_id = e.id "
                    + "  LEFT OUTER JOIN locations AS l "
                    + "   ON l.id = b.location_id "
                    + " WHERE e.id = ?");

            pstmt.setInt(1, event_id);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                // add string array containing id and name of location
                result.add(new diningLocation(
                        rs.getInt("location_id"),
                        rs.getString("location_name"),
                        rs.getInt("covers"),
                        rs.getInt("maximum_party_size"),
                        rs.getInt("maximum_online_size")));

            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.getDiningLocations: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return result;

    }

    public static ArrayList<diningLocation> getDiningLocations(int organization_id, int days_in_advance, int location_id, Connection con_d) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList<diningLocation> result = new ArrayList<diningLocation>();

        try {
            pstmt = con_d.prepareStatement(""
                    + "SELECT id, name, dining_maximum_party_size, dining_maximum_online_size "
                    + "FROM locations "
                    + "WHERE "
                    + "organization_id = ? AND "
                    + "dining_maximum_advance_days >= ? AND "
                    + "dining_minimum_advance_days <= ? AND "
                    + "dining_members_can_make_reservations = true AND "
                    + "deactivated = false");

            pstmt.setInt(1, organization_id);
            pstmt.setInt(2, days_in_advance);
            pstmt.setInt(3, days_in_advance);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                // add string array containing id and name of location
                result.add(new diningLocation(
                        rs.getInt("id"),
                        rs.getString("name"),
                        0,
                        rs.getInt("dining_maximum_party_size"),
                        rs.getInt("dining_maximum_online_size")));

            }

            // make sure that this location is always returned (location_id is passed in when editing a reservation)
            if (location_id > 0 && result.isEmpty()) {

                // go back and load at least this one location in to the array
                pstmt = con_d.prepareStatement(""
                        + "SELECT id, name, dining_maximum_party_size, dining_maximum_online_size "
                        + "FROM locations "
                        + "WHERE "
                        + "id = ? AND "
                        + "organization_id = ? AND "
                        + "deactivated = false");

                pstmt.setInt(1, location_id);
                pstmt.setInt(2, organization_id);

                rs = pstmt.executeQuery();

                if (rs.next()) {

                    // add string array containing id and name of location
                    result.add(new diningLocation(
                            rs.getInt("id"),
                            rs.getString("name"),
                            0,
                            rs.getInt("dining_maximum_party_size"),
                            rs.getInt("dining_maximum_online_size")));

                }

            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.getDiningLocations: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return result;

    }

    public static List<String> cancelReservation(int organization_id, int event_id, int user_id, int reservation_id, String reason, Connection con_d, HttpServletRequest req) {

        List<String[]> params = new ArrayList<String[]>();
        params.add(httpConnect.keyVal("foretees_login", "XV3"));
        params.add(httpConnect.keyVal("user_id", user_id));
        if (event_id > 0) {
            params.add(httpConnect.keyVal("event_id", event_id));
        }
        params.add(httpConnect.keyVal("organization_id", organization_id));
        params.add(httpConnect.keyVal("cancel_id", reservation_id));
        params.add(httpConnect.keyVal("_method", "DELETE"));
        params.add(httpConnect.keyVal("reservation_cancellation_reason_id", 5)); // other
        params.add(httpConnect.keyVal("reservation_cancellation_comments", reason));

        return postDiningService(eventOrDining(event_id), params, reservation_id);

    }

    public static List<String> removeReservee(int organization_id, int event_id, int user_id, int cancel_id, String reason, Connection con_d, HttpServletRequest req) {

        List<String[]> params = new ArrayList<String[]>();
        params.add(httpConnect.keyVal("foretees_login", "XV3"));

        if (event_id > 0) {
            params.add(httpConnect.keyVal("event_id", event_id));
        }
        params.add(httpConnect.keyVal("organization_id", organization_id));
        //params.add(httpConnect.keyVal("reservation[organization_id]", organization_id)); 
        params.add(httpConnect.keyVal("user_id", user_id));
        params.add(httpConnect.keyVal("cancel_id", cancel_id));
        //params.add(httpConnect.keyVal("reservation_id", ));
        params.add(httpConnect.keyVal("new_master_id", "this"));
        params.add(httpConnect.keyVal("_method", "DELETE"));
        params.add(httpConnect.keyVal("reservation_cancellation_reason_id", 5)); // other
        params.add(httpConnect.keyVal("reservation_cancellation_comments", reason));

        return postDiningService(eventOrDining(event_id), params, cancel_id);

    }

    public static List<String> redelegateReservation(int organization_id, int event_id, int user_id, int reservation_id, int new_master_id, String reason, Connection con_d, HttpServletRequest req) {

        List<String[]> params = new ArrayList<String[]>();
        List<String> result = new ArrayList<String>();
        params.add(httpConnect.keyVal("foretees_login", "XV3"));

        if (event_id > 0) {
            //params.add(httpConnect.keyVal("event_id", parmD.event_id));
            params.add(httpConnect.keyVal("reservation[event_id]", event_id));
        }
        params.add(httpConnect.keyVal("organization_id", organization_id));
        //params.add(httpConnect.keyVal("reservation[organization_id]", organization_id)); 
        params.add(httpConnect.keyVal("user_id", user_id));
        params.add(httpConnect.keyVal("reservation_id", reservation_id));
        params.add(httpConnect.keyVal("new_master_id", new_master_id));
        params.add(httpConnect.keyVal("_method", "DELETE"));
        params.add(httpConnect.keyVal("reservation_cancellation_reason_id", 5)); // other
        params.add(httpConnect.keyVal("reservation_cancellation_comments", reason));

        result = postDiningService(eventOrDining(event_id), params, reservation_id);
        if (result.isEmpty()) {
            // Looks like it went OK
            // Now move some data that the dining system seems to forget.
            migrateDataToNewMaster(organization_id, reservation_id, new_master_id, con_d);
        }
        return result;

    }

    public static String eventOrDining(int event_id) {
        return event_id > 0 ? "event" : "dining";
    }

    public static List<String> saveReservation(parmDining2 parmD, int owner_index, Connection con_d, HttpServletRequest req) {

        List<String[]> params = new ArrayList<String[]>();
        List<String> result = new ArrayList<String>();
        parmDiningCosts parmCosts = new parmDiningCosts();

        String user = reqUtil.getSessionString(req, "user", reqUtil.getSessionString(req, "ext-user", ""));
        String name = reqUtil.getSessionString(req, "name", "");
        Connection con = Connect.getCon(req);
        int my_person_id = Utilities.getPersonId(user, con);
        int my_user_id = Utilities.getUserId(my_person_id);

        if (my_person_id == 0 || my_user_id == 0) {
            // Something must be wrong with the dining server
            result.add("Unable to access your dining account.  Please try again, or contact support.");
            return result;
        }

        String redelegate_to = null;
        Integer remove_id_when_done = null;
        String redelegation_reason = "Uknown";
        String removal_reason = "Uknown";

        parmDining2 oldParmD;

        if (parmD.id > 0) {
            oldParmD = new parmDining2(parmD.organization_id, parmD.id, con_d, req);
        } else {
            oldParmD = new parmDining2(parmD.organization_id, parmD.event_id, req, con_d);
        }
        Map<String, Integer> old_user_map = buildUserMap(oldParmD.usernames, oldParmD.dining_id);
        Map<String, Integer> user_map = buildUserMap(parmD.usernames, parmD.dining_id);

        // Make sure owner information is set.
        parmD.owner = parmD.usernames.get(owner_index);
        parmD.owner_person_id = parmD.dining_id.get(owner_index);

        boolean in_reservation = getMember(user, user_map) != null;
        boolean in_old_reservation = getMember(user, old_user_map) != null;

        // Check owner/master status
        if (!oldParmD.owner.equalsIgnoreCase(parmD.usernames.get(owner_index)) || parmD.id == 0) {
            // We're changing owners (or it's a new reservation)
            Integer owner_test = getMember(oldParmD.owner, user_map);
            if (owner_test != null) {
                // previous owner is still in the new reservation
                // let's just leave them as the owner
                owner_index = owner_test;

            } else {
                // This owner is not in the current reservation
                // Was it me?
                if (oldParmD.owner.equalsIgnoreCase(user)) {
                    // It's me.  I must have removed myself from the reservation, or created a new
                    // reservation that doesn't include me.
                    // We'll need to temporarily add me back to this reservation, so we can save changes, then
                    // redelegate.
                    Integer my_old_index = getMember(user, old_user_map);
                    if (my_old_index != null) {
                        // I'm in the old reservation.  Copy me back to the new one temporarily
                        parmD.usernames.add(oldParmD.usernames.get(my_old_index));
                        parmD.names.add(oldParmD.names.get(my_old_index));
                        parmD.dining_id.add(oldParmD.dining_id.get(my_old_index));
                        parmD.related_id.add(oldParmD.related_id.get(my_old_index));

                        // Copy some stuff from another member of the reservation, 
                        // since data from the previous reservation may no longer 
                        // be applicable and could trigger an error
                        int dupe_index = user_map.entrySet().iterator().next().getValue();
                        Map<Integer, String> answers_copy = new LinkedHashMap<Integer, String>();
                        answers_copy.putAll(parmD.answers.get(dupe_index));
                        parmD.answers.add(answers_copy);
                        parmD.check_num.add(parmD.check_num.get(dupe_index));
                        parmD.price_category.add(parmD.price_category.get(dupe_index));
                        parmD.reservee_category.add(parmD.reservee_category.get(dupe_index));

                        // Set the owner_index to my re-added enty
                        owner_index = parmD.names.size() - 1;
                        // Set the member that we'll redelegate to when all is done
                        redelegate_to = parmD.usernames.get(dupe_index);

                        if (parmD.id > 0) {
                            redelegation_reason = name + " removed themself from the reservation, automatically delegating to " + parmD.names.get(dupe_index);
                        } else {
                            redelegation_reason = name + " created a reservation that they will not be part of, automatically delegating to " + parmD.names.get(dupe_index);
                        }


                    } else {
                        // Something is wrong.  I'm listed as the master of the old reservation
                        // but I'm not actually in it.  Shouldn't ever get here -- Bomb out.
                        result.add("You are no longer part of this reservation.  You cannot edit a reservation you are not part of.");
                        return result;
                    }
                } else {
                    // Let's add the old owner back to this reservation change request, and then redelegate after
                    // changes have been saved (man, would it be nice to not worry about this)
                    Integer old_owner_index = getMember(oldParmD.owner, old_user_map);
                    if (old_owner_index != null) {
                        parmD.usernames.add(oldParmD.usernames.get(old_owner_index));
                        parmD.names.add(oldParmD.names.get(old_owner_index));
                        parmD.dining_id.add(oldParmD.dining_id.get(old_owner_index));
                        parmD.related_id.add(oldParmD.related_id.get(old_owner_index));

                        // Copy some stuff from another member of the reservation, 
                        // since data from the previous reservation may no longer 
                        // be applicable and could trigger an error
                        int dupe_index = user_map.entrySet().iterator().next().getValue();
                        Map<Integer, String> answers_copy = new LinkedHashMap<Integer, String>();
                        answers_copy.putAll(parmD.answers.get(dupe_index));
                        parmD.answers.add(answers_copy);
                        parmD.check_num.add(parmD.check_num.get(dupe_index));
                        parmD.price_category.add(parmD.price_category.get(dupe_index));
                        parmD.reservee_category.add(parmD.reservee_category.get(dupe_index));

                        // Set the owner_index to the re-added entry
                        owner_index = parmD.names.size() - 1;

                        // Set the member that we'll redelegate to when all is done
                        // Is the new owner part of this reservation?
                        Integer new_owner_index = getMember(parmD.owner, user_map);
                        if (new_owner_index == null) {
                            // New owner is invalid.  Let's just grab the first available member
                            new_owner_index = dupe_index;
                        }
                        redelegate_to = parmD.usernames.get(new_owner_index);

                        if (parmD.id > 0) {
                            redelegation_reason = name + " removed the previous master, " + oldParmD.names.get(old_owner_index) + ", from the reservation, automatically delegating to " + parmD.names.get(new_owner_index);
                        } else {
                            // Should be able to get here?
                            result.add("Reservation in unknown state.  Please try again, or contact support.");
                            return result;
                            //removal_reason = name+ " created a reservation that they will not be part of, automatically redelegating to " + parmD.names.get(dupe_index);
                        }

                    } else {
                        // Something is wrong. 
                        // Shouldn't ever get here -- Bomb out.
                        result.add("Reservation members in unknown state.  Please try again, or contact support.");
                        return result;
                    }

                }
            }
        }

        // Make sure owner information is re-set after any changes.
        parmD.owner = parmD.usernames.get(owner_index);
        parmD.owner_person_id = parmD.dining_id.get(owner_index);

        // Old owner is not me. Am I trying to edit one I'm not part of?
        if (parmD.id > 0 && !in_reservation && !in_old_reservation) {
            // Trying to edit a reservation that we're not part of (shouldn't be able to get here)
            result.add("You cannot edit a reservation you are not part of.");
            return result;
        }

        // Am I removing myself from this reservation?
        if (parmD.id > 0 && !parmD.owner.equalsIgnoreCase(user) && !in_reservation && in_old_reservation) {
            // If I'm not a master, but I'm removing myself from this reservation,
            // My record needs to be copied back in temporarily, then moved after any other changes
            Integer my_old_index = getMember(user, old_user_map);
            parmD.usernames.add(oldParmD.usernames.get(my_old_index));
            parmD.names.add(oldParmD.names.get(my_old_index));
            parmD.dining_id.add(oldParmD.dining_id.get(my_old_index));
            parmD.related_id.add(oldParmD.related_id.get(my_old_index));

            // Copy some stuff from another member of the reservation, 
            // since data from the previous reservation may no longer 
            // be applicable and could trigger an error
            int dupe_index = user_map.entrySet().iterator().next().getValue();
            Map<Integer, String> answers_copy = new LinkedHashMap<Integer, String>();
            answers_copy.putAll(parmD.answers.get(dupe_index));
            parmD.answers.add(answers_copy);
            parmD.check_num.add(parmD.check_num.get(dupe_index));
            parmD.price_category.add(parmD.price_category.get(dupe_index));
            parmD.reservee_category.add(parmD.reservee_category.get(dupe_index));

            remove_id_when_done = oldParmD.related_id.get(my_old_index);
            removal_reason = name + " removed themself from the reservation.";
        }

        // Create a sanitized check number lookup map
        Map<Integer, Integer> check_map = new LinkedHashMap<Integer, Integer>();
        for (Integer cn : parmD.check_num) {
            int i = check_map.size() + 1;
            if (check_map.get(cn) == null) { // While the haspmap will deduplicate, c
                // we want to only any new to keep our new index correct
                check_map.put(cn, i);
            }

        }
        int number_of_checks = check_map.size();

        // if updating existing then add reservation id
        params.add(httpConnect.keyVal("foretees_login", "XV3"));

        params.add(httpConnect.keyVal("organization_id", parmD.organization_id));
        params.add(httpConnect.keyVal("user_id", my_user_id));

        if (parmD.id != 0) {
            params.add(httpConnect.keyVal("reservation_id", parmD.id));
            params.add(httpConnect.keyVal("_method", "PUT"));
        }

        if (parmD.names.size() == 1) {
            parmD.hide_check_numbers = true; // no check numbers if there is only 1
        }

        if (parmD.event_id > 0) {
            params.add(httpConnect.keyVal("reservation[event_id]", parmD.event_id));
        }

        params.add(httpConnect.keyVal("reservation[organization_id]", parmD.organization_id));
        params.add(httpConnect.keyVal("reservation[person_id]", parmD.dining_id.get(owner_index)));
        params.add(httpConnect.keyVal("reservation[category]", eventOrDining(parmD.event_id)));
        params.add(httpConnect.keyVal("reservation[date]", timeUtil.getStringDateMMDDYYYY(parmD.date)));
        params.add(httpConnect.keyVal("reservation[reservation_time]", timeUtil.get24HourTime(parmD.time)));
        params.add(httpConnect.keyVal("reservation[member_special_requests]", parmD.member_special_requests));
        params.add(httpConnect.keyVal("reservation[occasion_id]", parmD.occasion_id));
        params.add(httpConnect.keyVal("reservation[location_id]", parmD.location_id));
        params.add(httpConnect.keyVal("reservation[email_user]", (parmD.event_id > 0) ? parmD.auto_email_members_for_event_reservations : parmD.auto_email_members_for_dining_reservations));
        params.add(httpConnect.keyVal("reservation[member_created]", "true"));
        params.add(httpConnect.keyVal("reservation[number_of_checks]", number_of_checks));
        params.add(httpConnect.keyVal("has_related_reservations", parmD.names.size() > 1 ? "yes" : "no"));
        params.add(httpConnect.keyVal("reservation[covers]", 1)); // Why "1" instead of the actual number of covers?

        int index_offset = 0;
        int index, question_id, answer_index;
        String ri, an_id, an_i, answer;
        // Add reservees
        for (int i = 0; i < parmD.names.size(); i++) {
            // Deal with the possibility that the owner may not have been submitted in the first position
            answer_index = 0;
            if (i == 0 && owner_index != 0) {
                // First reservee must always be the owner.  If it's not currently,
                // we need to make sure we add the owner first, and then offset until we reach the owner
                index = owner_index;
                index_offset = -1;
            } else if (i != 0 && i > owner_index) {
                // Passed owner not in non-zero position.  Clear the offset. 
                index = i;
            } else {
                index = i + index_offset;
            }

            // Set up parameter prefixes, based on if we're the owner or not
            if (i == 0) {
                ri = "reservation";
                an_id = "id";
                an_i = "answers";
            } else {
                ri = "related_reservations[" + i + "]";
                an_id = "reservation_id";
                an_i = "related_reservations[" + i + "][answers]";
            }

            
            // Add reservee params
            if (parmD.event_id > 0 && i == 0) {
                // Only for owner's event reservation record
                params.add(httpConnect.keyVal("charges[" + i + "][covers]", 1));
                diningMealOption mealOption = parmD.event.getMealOption(parmD.price_category.get(index));
                if(mealOption != null){
                    params.add(httpConnect.keyVal("charges[" + i + "][price_category]", parmD.price_category.get(index)));
                    params.add(httpConnect.keyVal("charges[" + i + "][price_type_id]", mealOption.type));
                    params.add(httpConnect.keyVal("charges[" + i + "][price]", mealOption.cost));
                }
                
            } else if (parmD.event_id > 0) {
                // Only for related's event reservation record
                params.add(httpConnect.keyVal(ri + "[charges]", 1));
                params.add(httpConnect.keyVal(ri + "[price_category]", parmD.price_category.get(index)));
            } else {
                // Only for a la carte?
            }

            if (i > 0) {
                // Only for related
                params.add(httpConnect.keyVal("full_name_" + i, parmD.names.get(index)));
                if (parmD.related_id.get(index) > 0) {
                    params.add(httpConnect.keyVal(ri + "[id]", parmD.related_id.get(index)));
                }
                params.add(httpConnect.keyVal(ri + "[user_identity]", getUserIdentity(parmD.dining_id.get(index), con_d)));
                params.add(httpConnect.keyVal(ri + "[covers]", 1));
            }
            params.add(httpConnect.keyVal(ri + "[check_number]", parmD.hide_check_numbers ? 1 : check_map.get(parmD.check_num.get(index))));
            params.add(httpConnect.keyVal(ri + "[reservee_category]", parmD.reservee_category.get(index)));

            // Output individual answers for this reservee
            List<Integer> question_ids = new ArrayList<Integer>();
            for (Map.Entry<Integer, Question> entry : parmD.questions.entrySet()) {
                question_id = entry.getKey();
                Question question = entry.getValue();
                Map<Integer, String> answer_map = parmD.answers.get(index);
                if (answer_map == null) {
                    answer_map = new HashMap<Integer, String>();
                }
                answer = answer_map.get(question_id);
                if (answer == null) {
                    answer = "";
                }
                params.add(httpConnect.keyVal(an_i + "[" + answer_index + "][question_id]", question_id));
                if (parmD.id > 0) {
                    params.add(httpConnect.keyVal(an_i + "[" + answer_index + "][" + an_id + "]", parmD.related_id.get(index)));
                }
                params.add(httpConnect.keyVal(an_i + "[" + answer_index + "][question_text]", question.text));
                params.add(httpConnect.keyVal(an_i + "[" + answer_index + "][answer_text]", answer));
                answer_index++;
                // Workaround dining system bug
                repairQuestionRecord(parmD.organization_id, parmD.related_id.get(index), question_id, con_d);
                question_ids.add(question_id);
            }
            if (i == 0) {
                // Only for owner
                // Output group answers for this reservation (group answers bind to the owner)
                for (Map.Entry<Integer, Question> entry : parmD.group_questions.entrySet()) {
                    question_id = entry.getKey();
                    Question question = entry.getValue();
                    answer = parmD.group_answers.get(question_id);
                    if (answer == null) {
                        answer = "";
                    }
                    params.add(httpConnect.keyVal(an_i + "[" + answer_index + "][question_id]", question_id));
                    if (parmD.id > 0) {
                        params.add(httpConnect.keyVal(an_i + "[" + answer_index + "][" + an_id + "]", parmD.related_id.get(index)));
                    }
                    params.add(httpConnect.keyVal(an_i + "[" + answer_index + "][question_text]", question.text));
                    params.add(httpConnect.keyVal(an_i + "[" + answer_index + "][answer_text]", answer));
                    answer_index++;
                    // Workaround dining system bug
                    repairQuestionRecord(parmD.organization_id, parmD.related_id.get(index), question_id, con_d);
                    question_ids.add(question_id);
                }
                
            }
            // Clean up any answers to questions that shouldn't exist
            cleanQuestionRecords(parmD.organization_id, parmD.related_id.get(index), question_ids, con_d);

        }
        if (parmD.names.size() > 1) {
            params.add(httpConnect.keyVal("next_related_reservation", 10)); // ?? why 10 ??
            params.add(httpConnect.keyVal("done_adding_related_reservations", "true"));
        }

        result.addAll(postDiningService(eventOrDining(parmD.event_id), params, parmD.id));

        int id = parmD.id;

        if (result.isEmpty() && redelegate_to != null) {
            // We sucessfully added this reservation, but we need to redelegate it to another user.
            int resnum = parmD.reservation_number;
            if (id == 0) {
                // This was a new reservation, so first get the id of the new reservation.
                id = getReservationId(parmD, parmD.dining_id.get(owner_index), con_d);
            }
            boolean error = false;
            String error_message = "";
            if (id == 0) {
                error_message = "Unable to find reservation id for delegation after creation.";
                error = true;
            } else {
                parmDining2 newParmD = new parmDining2(parmD.organization_id, id, con_d, req);
                resnum = newParmD.reservation_number;
                parmD.reservation_number = resnum;
                user_map = buildUserMap(newParmD.usernames, newParmD.dining_id);
                Integer old_master_index = popMember(newParmD.owner, user_map);
                if (old_master_index == null) {
                    error_message = "Unable to find proper member for delegation in reservation #" + id;
                    error = true;
                } else if (user_map.isEmpty()) {
                    error_message = "Not enough members for delegation in reservation #" + id;
                    error = true;
                } else {
                    // Redelegate reservation to different user
                    Integer new_master_index = getMember(redelegate_to, user_map);
                    if (new_master_index == null) {
                        new_master_index = user_map.entrySet().iterator().next().getValue(); // Next member in the list will be the new owner
                    }
                    id = newParmD.related_id.get(new_master_index);

                    result.addAll(redelegateReservation(newParmD.organization_id, newParmD.event_id, my_user_id, newParmD.id, id, redelegation_reason, con_d, req));

                    if (!result.isEmpty()) {
                        error_message = "Error delegating reservation #" + resnum;
                        error = true;
                    }
                }
            }
            if (error) {
                Utilities.logError("diningUtil.saveReservation: Redelegation Error: " + error_message);
                result.add("Reservation was created or modified successfully, but there was an error when removing previous reservation master from the reservation.");
            }
        } else if (result.isEmpty() && id == 0){
            // New reservation.  Populate the reservation number for this reservation in parmD. (Could be used later)
            id = getReservationId(parmD, parmD.dining_id.get(owner_index), con_d);
            if (id == 0) {
                result.add("Unable to find reservation number for newly created reservation");
            } else {
                parmDining2 newParmD = new parmDining2(parmD.organization_id, id, con_d, req);
                parmD.reservation_number = newParmD.reservation_number;
            }
           
        }

        if (result.isEmpty() && remove_id_when_done != null) {
            // We sucessfully added this reservation, but we need to remove ourself from the reservation.  
            result.addAll(removeReservee(parmD.organization_id, parmD.event_id, my_user_id, remove_id_when_done, removal_reason, con_d, req));
            if (!result.isEmpty()) {
                Utilities.logError("diningUtil.saveReservation: Error removing self from reservation.");
            }
        }

        return result;

    }

    public static List<Map<String, String>> getLocationTimesList(int organization_id, int location_id, int date, int event_id) {

        String json = getDiningPortal(
                "available_time_options",
                new String[]{
                    "reservation_category", ((event_id == 0) ? "dining" : "event"),
                    ((event_id != 0) ? "event_id" : null), Integer.toString(event_id),
                    "organization_id", Integer.toString(organization_id),
                    "location_id", Integer.toString(location_id),
                    "reservation_date", Utilities.getDateFromYYYYMMDD(date, 1)
                });

        Gson gson = new Gson();

        // Define the structure of our response (A list of String key/String value Maps.)
        Type dataType = new TypeToken<List<Map<String, String>>>() {
        }.getType();
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        try {
            // Try parsing json response
            resultList = gson.fromJson(json, dataType);

        } catch (JsonParseException e) {
            // Inavlid json
            resultList = new ArrayList<Map<String, String>>();
            /* Probably should return an error.  Error in the map? like errormap.put("error","json parse error"); */
            Utilities.logError("diningUtil.getLocationTimesList(), result=" + json + "");
        }
        /*
        if(resultList == null || resultList.size() < 1){
        resultList = new ArrayList<Map<String,String>>();
        Map<String,String> error = new HashMap<String,String>();
        error.put("text", json);
        error.put("time", "debug-time");
        error.put("value", "debug");
        resultList.add(error);
        }
         * 
         */

        if (resultList == null) {
            // Error processing or empty result
            /* Probably should return an error.  Error in the map? like errormap.put("error","invalid rresponse error"); */
            resultList = new ArrayList<Map<String, String>>();
        }

        return resultList;

    }

    public static List<String> postDiningService(String dining_or_event, List<String[]> params, int id) {

        StringBuilder url = new StringBuilder();

        url.append(DINING_SELF_SERVICE);
        url.append(dining_or_event);
        url.append("_reservations");
        if (id > 0) {
            url.append("/");
            url.append(id);
        }
        url.append(".xml");

        return parseDiningXmlError(httpConnect.post(url.toString(), params));

    }

    private static List<String> parseDiningXmlError(String xml) {

        List<String> result = new ArrayList<String>();

        if (xml == null) {
            result.add("Error: Unknown error code from dining system.");
            Utilities.logError("diningUtil.parseDiningErrorXml: null response == html error code");
        } else if (!xml.trim().isEmpty()) {
            try {
                ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.parse(stream);

                NodeList errors = doc.getElementsByTagName("error");

                for (int i = 0; i < errors.getLength(); i++) {
                    Element error = (Element) errors.item(i);
                    result.add(error.getTextContent());
                }

            } catch (Exception e) {
                result.add("Error: Unknown response from dining system.");
                Utilities.logError("diningUtil.parseDiningErrorXml: response=" + xml + ", err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            }
        }

        return result;
    }

    public static String getDiningPortal(String command, String[] params) {

        StringBuilder url = new StringBuilder();

        url.append(DINING_PORTAL);
        url.append(command);
        url.append(".json");

        return httpConnect.get(url.toString(), params);

    }

    public static int countEventCovers(int organization_id, int event_id, int location_id, boolean member_made_only, Connection con_d) {

        int result = 0;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT COUNT(id) "
                    + " FROM reservations "
                    + " WHERE "
                    + "   organization_id = ? AND "
                    + "   event_id = ? AND "
                    + "   location_id = ? AND "
                    + "   state <> 'cancelled'"
                    + ((member_made_only) ? " AND member_created = true" : ""));

            pstmt.setInt(1, organization_id);
            pstmt.setInt(2, event_id);
            pstmt.setInt(3, location_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                result = rs.getInt(1);
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.countEventCovers: organization_id=" + organization_id + ", event_id=" + event_id + ", location_id=" + location_id + ", member_made_only=" + member_made_only + ", err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return result;

    }

    public static String buildUserIdentity(String username, String prefix, String last_name, String first_name, String middle_name, String suffix) {


        /*
         * from the person model in dining
        
        def self.build_username_with_full_name(username = "", prefix = "", last_name = "", first_name = "", middle_name = "", suffix = "")
        "#{username.rjust(10)}: #{last_name}, #{first_name}#{middle_name.blank? ? '' : ' ' << middle_name}#{suffix.blank? ? '' : ', ' + suffix}#{prefix.blank? ? '' : ', ' + prefix}"
        end
        
         */

        String user_identity = username + ":" + " " + last_name + ", " + first_name
                + (middle_name.equals("") ? "" : " " + middle_name)
                + (suffix.equals("") ? "" : ", " + suffix)
                + (prefix.equals("") ? "" : ", " + prefix);

        // ensure the username portion before the colon is 10 characters
        int pos = user_identity.indexOf(":");

        for (int i = pos; i < 10; i++) {

            user_identity = " " + user_identity;
        }

        return user_identity;

    }

    public static Integer[] getClubOffset(int date, int location_id, int event_id, HttpServletRequest req) {

        Connection con = Connect.getCon(req); // Get foretees DB connection

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int[] date_a = timeUtil.parseIntDate(date);

        int offset = 0;
        int meal_period_offset = 0;
        int close_time = -1;

        // Figure out the lowest starting reservation time
        try {

            // Search for any custom close time rules
            pstmt = con.prepareStatement(""
                    + "SELECT id, name, meal_period, offset, close_time "
                    + " FROM dining_close_times  "
                    + " WHERE " + timeUtil.getDayOfWeekLower(date) + " > 0 "
                    + "   AND " + (event_id > 0 ? "event" : "alacarte") + " > 0 "
                    + "   AND (location_id is null OR location_id = ?)"
                    + "   AND (start_year is null OR start_year >= ?) "
                    + "   AND (start_month is null OR start_month >= ?) "
                    + "   AND (start_day is null OR start_day >= ?) "
                    + "   AND (end_year is null OR end_year <= ?) "
                    + "   AND (end_month is null OR end_month <= ?) "
                    + "   AND (end_day is null OR end_day <= ?) "
                    + " ORDER BY priority "
                    + "   ");

            pstmt.setInt(1, location_id);
            pstmt.setInt(2, date_a[timeUtil.YEAR]);
            pstmt.setInt(3, date_a[timeUtil.MONTH]);
            pstmt.setInt(4, date_a[timeUtil.DAY]);
            pstmt.setInt(5, date_a[timeUtil.YEAR]);
            pstmt.setInt(6, date_a[timeUtil.MONTH]);
            pstmt.setInt(7, date_a[timeUtil.DAY]);

            rs = pstmt.executeQuery();



            while (rs.next()) {

                boolean for_meal_period = rs.getBoolean("meal_period");
                int this_offset = rs.getInt("offset");
                int this_close_time = rs.getInt("close_time");

                if ((for_meal_period && event_id < 1) && this_offset > 0) {
                    meal_period_offset = this_offset;
                } else if (this_offset > 0) {
                    offset = for_meal_period ? 0 : this_offset;
                }
                if (this_close_time > -1) {
                    close_time = this_close_time;
                }

            }


        } catch (Exception exc) {

            Utilities.logError("diningUtil.getClubOffset: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return new Integer[]{offset, meal_period_offset, close_time};

    }

    public static long[] getReservationMinMax(int organization_id, int location_id, int date, int time, diningEvent event, List<diningMealPeriod> mealPeriods, HttpServletRequest req, Connection con_d) {

        //String club = Utilities.getSessionString(req, "club", "");

        long now = timeUtil.getCurrentUnixTime();
        int[] today = timeUtil.getClubDateTime(req, now);

        int[] minMaxDays = getMinMaxDays(organization_id, event, con_d); // Days in Advance

        long min_utime = timeUtil.addClubUnixTime(req, date, 0, Calendar.DATE, 0 - minMaxDays[MAXIMUM]);

        long max_utime = 0;
        long test_utime = 0;

        Integer[] offset_a = getClubOffset(date, location_id, event.id, req);

        if (offset_a[OFFSET] > 0) {
            // We have a reservation time offset
            test_utime = timeUtil.addClubUnixTimeMinutes(req, date, time, 0 - offset_a[OFFSET]);
            if (test_utime < max_utime || max_utime == 0) {
                max_utime = test_utime;
            }
        }
        if (offset_a[MEAL_PERIOD_OFFSET] > 0 && event.id == 0) {
            // We have a meal period time offset
            diningMealPeriod mealPeriod = getMealPeriodByTime(mealPeriods, time);
            if (mealPeriod != null) {
                // Use meal period start time as minimum start time
                test_utime = timeUtil.addClubUnixTimeMinutes(req, date, mealPeriod.start_time, 0 - offset_a[MEAL_PERIOD_OFFSET]);
                if (test_utime < max_utime || max_utime == 0) {
                    max_utime = test_utime;
                }
            }

        }
        if (today[timeUtil.DATE] == date && offset_a[CLOSE_TIME] > -1) {
            test_utime = timeUtil.addClubUnixTimeMinutes(req, date, offset_a[CLOSE_TIME], 0 - offset_a[OFFSET]);
            if (test_utime < max_utime || max_utime == 0) {
                max_utime = test_utime;
            }
        }

        if (max_utime == 0) {
            // No custom close time/offset, fallback to default minutes before, or days in advanced
            max_utime = timeUtil.addClubUnixTimeMinutes(req, date, time, 0 - DEFAULT_MINUTES_BEFORE);
        }

        return new long[]{min_utime, max_utime};

    }

    public static long[] getStartEndDefault(int organization_id, int location_id, parmDining2 parmD, List<diningMealPeriod> mealPeriods, HttpServletRequest req, Connection con_d) {

        Gson gson = new Gson();
        //String club = Utilities.getSessionString(req, "club", "");
        if (parmD.event.id > 0) {
            return new long[]{
                        timeUtil.getClubUnixTime(req, parmD.event.date, parmD.event.start_time),
                        timeUtil.getClubUnixTime(req, parmD.event.date, parmD.event.end_time),
                        timeUtil.getClubUnixTime(req, parmD.date, parmD.time)
                    };
        }

        long now = timeUtil.getCurrentUnixTime();
        int[] today = timeUtil.getClubDateTime(req, now);
        int[] minMaxDays = getMinMaxDays(organization_id, parmD.event, con_d); // Days in Advance
        //long useReservationUtime = timeUtil.getClubUnixTime(req, parmD.date, parmD.time);
        //int[] reservationDateTime = timeUtil.getClubDateTime(req, useReservationUtime);

        int date = today[timeUtil.DATE];
        int time = today[timeUtil.TIME];
        
        boolean skip_to_next_day = true;
        
        int offset = 0;

        // Set maximum utime that reservation can be made
        long max_utime = timeUtil.addClubUnixTime(req, date, 2359, Calendar.DATE, minMaxDays[MAXIMUM]);

        // Set minimum utime that reservation can be made
        long min_utime = 0;
        long test_utime = 0;

        if (location_id > 0) {
            //Utilities.logDebug("JGK","Setting start end by location: location_id:"+location_id+" date:"+date+" time:"+time);
            
            // We're checking for a specific location -- check close times
            Integer[] offset_a = getClubOffset(date, location_id, parmD.event.id, req);
            //Utilities.logDebug("JK0",gson.toJson(Arrays.asList(offset_a)));

            if (offset_a[OFFSET] > 0) {
                // We have a reservation time offset
                //Utilities.logDebug("JGK","Setting by offset (no MP?)");
                test_utime = timeUtil.addClubUnixTimeMinutes(req, date, time, offset_a[OFFSET]);
                if (test_utime > min_utime) {
                    min_utime = test_utime;
                }
            }
            if (offset_a[MEAL_PERIOD_OFFSET] > 0) {
                // We have a meal period time offset
                //Utilities.logDebug("JGK","Setting by meal period offset:"+offset_a[MEAL_PERIOD_OFFSET]);
                //Utilities.logDebug("JK3", "DATE:"+date+";TIME:"+time+";OFFSET:"+offset_a[MEAL_PERIOD_OFFSET].toString());
                diningMealPeriod mealPeriod = getNextAvailableMealPeriod(req, mealPeriods, date, time, offset_a[MEAL_PERIOD_OFFSET]);
                //Utilities.logDebug("JK4", gson.toJson(mealPeriods));
                if (mealPeriod == null) {
                    // We're past the last meal period, putting us in the next day.
                    //Utilities.logDebug("JGK","No availible meal periods!  Skip to tomorrow.");
                    
                    test_utime = timeUtil.addClubUnixTime(req, date, 0, Calendar.DATE, 1);
                    mealPeriod = getLastMealPeriod(mealPeriods);
                    //Utilities.logDebug("JK5", Long.toString(test_utime));
                    if (mealPeriod != null) {
                        //Utilities.logDebug("JK6", gson.toJson(mealPeriod));
                        long test_time2 = timeUtil.addClubUnixTimeMinutes(req, date, mealPeriod.end_time, 1);
                        //Utilities.logDebug("JK7", Long.toString(test_utime));
                        if (test_time2 > min_utime) {
                            min_utime = test_time2;
                        }
                    }
                    //Utilities.logDebug("JK8", Long.toString(test_utime));
                } else {
                    
                    // Use meal period start time as minimum start time
                    //test_utime = timeUtil.getClubUnixTime(req, date, mealPeriod.start_time);
                    test_utime = timeUtil.addClubUnixTimeMinutes(req, date, mealPeriod.start_time, -1);
                    //Utilities.logDebug("JGK","Check "+mealPeriod.name+" offset: "+timeUtil.getVerboseDateTime(req, test_utime));
                    if (test_utime > min_utime) {
                        //Utilities.logDebug("JGK","Using startdate/time of: "+timeUtil.getVerboseDateTime(req, test_utime));
                        min_utime = test_utime;
                    }
                }
                
            }
            if (offset_a[CLOSE_TIME] > -1) {
                // We have a close time
                //Utilities.logDebug("JGK","Setting by close time:"+offset_a[CLOSE_TIME]);
                test_utime = timeUtil.getClubUnixTime(req, date, offset_a[CLOSE_TIME]);
                if (timeUtil.getCurrentUnixTime() >= test_utime) {
                    // It's past the close time.  Set minimum to close time to next day.
                    date = timeUtil.add(req, date, 0, 1, Calendar.DATE)[timeUtil.DATE];
                    skip_to_next_day = true;
                    min_utime = 0;
                    //timeUtil.addClubUnixTime(req, date, date);
                    //min_utime = test_utime;
                }
            }
            
        }
        if (min_utime == 0) {
            //Utilities.logDebug("JGK","Using default minimum");
            // No custom close time/offset, fallback to default minutes before, or days in advanced
            if (minMaxDays[MINIMUM] == 0) {
                // Reservation is today, and we're allowed to edit/reserve on the day of -- use minutes before
                min_utime = timeUtil.addClubUnixTimeMinutes(req, date, time, DEFAULT_MINUTES_BEFORE);
            } else {
                // Set max using minimum days in advance
                min_utime = timeUtil.addClubUnixTime(req, date, 0, Calendar.DATE, minMaxDays[MINIMUM]);
            }
        }
        
        //Utilities.logDebug("JGK","Allow from: "+timeUtil.getVerboseDateTime(req, min_utime));
        //Utilities.logDebug("JGK","Allow to: "+timeUtil.getVerboseDateTime(req, max_utime));

        int default_days_in_advance = getDefaultDaysInAdvance(req);

        long default_utime;

        if (default_days_in_advance == 0 && minMaxDays[MINIMUM] == 0) {
            // We default to today
            default_utime = now;
        } else {
            // Set default
            default_utime = timeUtil.addClubUnixTime(req, date, 0, Calendar.DATE, default_days_in_advance);
        }
        if (default_utime < min_utime) {
            default_utime = min_utime;
        }

        return new long[]{
                    min_utime,
                    max_utime,
                    default_utime};

    }

    public static String reservationTimeStatus(int organization_id, int location_id, int date, int time, parmDining2 parmD, List<diningMealPeriod> mealPeriods, HttpServletRequest req, Connection con_d) {

        //String club = Utilities.getSessionString(req, "club", "");

        long now = timeUtil.getCurrentUnixTime();
        int[] today = timeUtil.getClubDateTime(req, now);

        
        int cdate = date;
        int ctime = time;

        if (parmD.event.id > 0) {
            cdate = parmD.event.date;
            ctime = parmD.event.start_time;
        } else {
            //minMaxUtime = getStartEndDefault(organization_id, location_id, parmD, mealPeriods, req, con_d);
        }

        long[] minMaxUtime = getReservationMinMax(organization_id, location_id, cdate, ctime, parmD.event, mealPeriods, req, con_d);
        long reservation_utime = timeUtil.getClubUnixTime(req, cdate, ctime);

        parmD.debug.put("minMaxUtime", Arrays.asList(minMaxUtime));
        parmD.debug.put("min", Arrays.asList(timeUtil.getClubDateTime(req, minMaxUtime[MINIMUM])));
        parmD.debug.put("max", Arrays.asList(timeUtil.getClubDateTime(req, minMaxUtime[MAXIMUM])));
        parmD.debug.put("reservation_utime", reservation_utime);
        parmD.debug.put("reservation_date_time", Arrays.asList(timeUtil.getClubDateTime(req, reservation_utime)));
        parmD.debug.put("now_utime", now);
        parmD.debug.put("now_date_time", Arrays.asList(timeUtil.getClubDateTime(req, now)));

        // Check if the event/reservation is in the past
        
        if (now >= reservation_utime) {
            return "Sorry, this reservation can no longer be edited.  Please call the club if you have any questions.";
        }

        // Check if it's too early to edit/book a reservation
        if (now <= minMaxUtime[MINIMUM]) {
            if (parmD.event.id > 0 && parmD.id == 0) {
                return "Sorry, this event is not available for new registrations until after " + timeUtil.getVerboseDateTime(req, minMaxUtime[MINIMUM]);
            } else {
                return "Sorry, unable to edit this registration until after " + timeUtil.getVerboseDateTime(req, minMaxUtime[MINIMUM]);
            }
        }

        if (now >= minMaxUtime[MAXIMUM]) {
            if (parmD.event.id > 0 && parmD.id == 0) {
                return "Sorry, reservations for this event closed on " + timeUtil.getVerboseDateTime(req, minMaxUtime[MAXIMUM]);
            } else {
                return "Sorry, this reservation was locked on " + timeUtil.getVerboseDateTime(req, minMaxUtime[MAXIMUM]);
            }
        } else {
            return null; // Null response == reservation is good to go
        }
    }

    /*
    public static String fillNoEditMessage(String message, int date, int time){
    
    Map<String,String> repMap = new HashMap<String,String>();
    
    repMap.put("DAY_OF_WEEK", timeUtil.getDayOfWeek(date));
    repMap.put("TIME", Utilities.getSimpleTime(time));
    repMap.put("DATE", Utilities.getDateFromYYYYMMDD(date, 3));
    
    for (Map.Entry<String, String> rep: repMap.entrySet()){
    message = message.replace("%"+rep.getKey()+"%", rep.getValue());
    }
    
    return message;
    
    }
     */
    public static diningMealPeriod getMealPeriodByIndex(List<diningMealPeriod> mealPeriods, int index) {

        if (index + 1 > mealPeriods.size() || index < 0) {
            return null;
        } else {
            return mealPeriods.get(index);
        }

    }

    public static diningMealPeriod getMealPeriodByTime(List<diningMealPeriod> mealPeriods, int time) {

        for (diningMealPeriod mp : mealPeriods) {
            if (mp.start_time >= time && mp.end_time <= time) {
                return mp;
            }
        }

        return null;

    }

    public static int getMealPeriodIndex(int time, List<diningMealPeriod> mealPeriods) {

        int i = 0;
        for (diningMealPeriod mp : mealPeriods) {
            if (mp.start_time >= time && mp.end_time <= time) {
                return i;
            }
            i++;
        }

        return -1;

    }
/*
    public static int getClosestMealPeriodIndex(List<diningMealPeriod> mealPeriods, int time) {

        int i = -1;
        for (diningMealPeriod mp : mealPeriods) {
            if (mp.end_time <= time) {
                i++;
            } else {
                break;
            }
        }

        return -1;

    }
*/
    public static int getClosestMealPeriodIndex(List<diningMealPeriod> mealPeriods, int time, int start_index) {

        int found = -1;
        for (int i = start_index; i < mealPeriods.size(); i++) {
            diningMealPeriod mp = mealPeriods.get(i);
            if (mp.end_time <= time) {
                found = i;
            } else {
                break;
            }
        }

        return found;

    }

    //  Returns the next available meal period index
    public static int getNextMealPeriodIndex(List<diningMealPeriod> mealPeriods, int index) {

        if (index < 0 || index + 2 > mealPeriods.size()) {
            return -1;
        }
        return index + 1;

    }

    //  Returns the last available meal period
    public static diningMealPeriod getLastMealPeriod(List<diningMealPeriod> mealPeriods) {

        int i = mealPeriods.size();
        if (i < 1) {
            return null;
        } else {
            return getMealPeriodByIndex(mealPeriods, i - 1);
        }

    }

    public static int getMasterId(int organization_id, int reservation_id, Connection con_d) {

        int result = reservation_id;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            String sql = ""
                    + "SELECT parent_id AS id "
                    + " FROM reservations "
                    + " WHERE organization_id = ? "
                    + "  AND parent_id IS NOT NULL "
                    + "  AND id = ? ";
            // NOTE: we're skipping the cancelled check here, so we can at leaste get the parent ID
            // of the canceled record
            //+ "  AND state <> 'cancelled' ";


            pstmt = con_d.prepareStatement(sql);

            pstmt.setInt(1, organization_id);
            pstmt.setInt(2, reservation_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                result = rs.getInt("id");
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.getMasterId: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return result;
    }

    public static int getReservationId(parmDining2 parmD, int person_id, Connection con_d) {

        int result = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            String sql = ""
                    + "SELECT max(id) as id "
                    + " FROM reservations "
                    + " WHERE organization_id = ? "
                    + "  AND location_id = ? "
                    + "  AND person_id = ?"
                    + "  AND date = CAST(? AS DATE) "
                    + "  AND time = CAST(? AS TIME) "
                    + "  AND parent_id IS NULL "
                    + "  AND state <> 'cancelled' ";
            if (parmD.event_id > 0) {
                sql += " AND event_id = ? ";
            } else {
                sql += " AND event_id IS NULL ";
            }

            pstmt = con_d.prepareStatement(sql);

            pstmt.setInt(1, parmD.organization_id);
            pstmt.setInt(2, parmD.location_id);
            pstmt.setInt(3, person_id);
            pstmt.setString(4, timeUtil.getDbDate(parmD.date));
            pstmt.setString(5, timeUtil.get24HourTime(parmD.time));
            if (parmD.event_id > 0) {
                pstmt.setInt(6, parmD.event_id);
            }

            rs = pstmt.executeQuery();

            if (rs.next()) {
                result = rs.getInt("id");
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.getReservationId: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return result;
    }

    //  Returns the next availible meal period, by time and offset.  
    //  If the time+offset falls in a meal period, it will return the next meal period
    //  If it falls before, it will return the one it falls before
    public static diningMealPeriod getNextAvailableMealPeriod(HttpServletRequest req, List<diningMealPeriod> mealPeriods, int date, int time, int offset) {

        int[] date_time_with_offset =  timeUtil.addMinutes(req, date, time, offset);// time + offset;
        int time_with_offset =  date_time_with_offset[timeUtil.TIME];// time + offset;
        if (date_time_with_offset[timeUtil.DATE] > date) {
            // Pushes us to tomorrow
            //Utilities.logDebug("JGK", "Offset past end of day");
            return null;
        }

        //int mpi = getClosestMealPeriodIndex(mealPeriods, time_with_offset);
        //diningMealPeriod mp = getMealPeriodByIndex(mealPeriods, mpi);
        //if (mp != null) {
        diningMealPeriod found_mp = null;
        for(diningMealPeriod mp: mealPeriods){
            //Utilities.logDebug("JGK", "Comparing: time:"+time+" + offset:"+offset + " against: " + mp.start_time);
            if (time_with_offset < mp.start_time) {
                if(found_mp == null || mp.start_time < found_mp.start_time){
                    found_mp = mp;
                    //Utilities.logDebug("JGK", "possible start_time:"+ mp.start_time);
                }
            }
        }
        return found_mp;

    }

    public static Map<Integer, String> getAnswers(int lookup_id, Map<Integer, Question> questions, Connection con_d) {


        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<Integer, String> result = new HashMap<Integer, String>();

        try {

            // reset the answers arrays
            //answers = new String [covers + tmp][question_count + 1];

            // load each answer for each person
            pstmt = con_d.prepareStatement(""
                    + "SELECT * "
                    + "FROM answers "
                    + "WHERE reservation_id = ?");

            pstmt.setInt(1, lookup_id);

            rs = pstmt.executeQuery();

            // store them
            while (rs.next()) {
                if (questions.get(rs.getInt("question_id")) != null) {
                    result.put(rs.getInt("question_id"), rs.getString("answer_text"));
                }
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.getAnswers: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return result;

    }

    public static List<diningMealPeriod> getMealPeriods(int location_id, int event_id, int date, Connection con_d) {

        List<diningMealPeriod> result = new ArrayList<diningMealPeriod>();

        if (event_id > 0) {
            return result; // no meal periods for events
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int[] date_a = timeUtil.parseIntDate(date);

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT id, name, "
                    + "  to_char(start_time, 'HH24MI')::int AS start_time_int, "
                    + "  to_char(end_time, 'HH24MI')::int AS end_time_int  "
                    + " FROM meal_periods  "
                    + " WHERE location_id = ? "
                    + "   AND available_" + timeUtil.getDayOfWeekLower(date) + " = 'true' "
                    + "   AND (start_month is null OR start_month >= ?) "
                    + "   AND (start_day is null OR start_day >= ?) "
                    + "   AND (end_month is null OR end_month <= ?) "
                    + "   AND (end_day is null OR end_day <= ?) "
                    + "   ");

            pstmt.setInt(1, location_id);
            pstmt.setInt(2, date_a[timeUtil.MONTH]);
            pstmt.setInt(3, date_a[timeUtil.DAY]);
            pstmt.setInt(4, date_a[timeUtil.MONTH]);
            pstmt.setInt(5, date_a[timeUtil.DAY]);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                result.add(new diningMealPeriod(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("start_time_int"),
                        rs.getInt("end_time_int")));

            }


        } catch (Exception exc) {

            //last_error = "parmDining.loadEventInfo: qry=" + err_tmp + ", Err=" + exc.toString();
            Utilities.logError("diningUtil.getMealPeriods: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return result;

    }

    public static List<parmEvent> getEvents(int activity_id, String username, HttpServletRequest req, Connection con_d) {


        List<parmEvent> eventList = new ArrayList<parmEvent>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //String orderBy = "";
        
        Connection con = Connect.getCon(req);

        int organizationId = Utilities.getOrganizationId(con);

        if (activity_id == ProcessConstants.DINING_ACTIVITY_ID && con_d != null) {

            try {

                pstmt = con_d.prepareStatement(""
                        + "SELECT id "
                        + "FROM events "
                        + "WHERE organization_id = ? AND cancelled = false AND "
                        + "date >= CAST(? AS DATE) "
                        + "ORDER BY date, start_time");

                pstmt.setInt(1, organizationId);
                pstmt.setString(2, timeUtil.getDbDate(req));

                rs = pstmt.executeQuery();

                while (rs.next()) {

                    eventList.add(load1Event(rs.getInt(1), organizationId, username, req, con_d));

                }

            } catch (Exception ignore) {
            } finally {

                try {
                    rs.close();
                } catch (Exception ignore) {
                }

                try {
                    pstmt.close();
                } catch (Exception ignore) {
                }

            }

        } // end if dining

        return eventList;

    }
    
    public static List<reservationSignup> getSignupsByUser(int person_id, HttpServletRequest req) {
        return getSignupsByUser(null, null, person_id, req, null);
    }
    
    public static List<reservationSignup> getSignupsByUser(int person_id, HttpServletRequest req, Connection con_d) {
        return getSignupsByUser(null, null, person_id, req, con_d);
    }
    
    public static List<reservationSignup> getSignupsByUser(int organization_id, String user, HttpServletRequest req, Connection con_d) {
        return getSignupsByUser(organization_id, user, null, req, con_d);
    }
    
    public static List<reservationSignup> getSignupsByUser(int organization_id, int person_id, HttpServletRequest req, Connection con_d) {
        return getSignupsByUser(organization_id, null, person_id, req, con_d);
    }
    
    
    private static List<reservationSignup> getSignupsByUser(Integer organization_id, String user, Integer person_id, HttpServletRequest req, Connection con_d) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean close_con = false;
        if(con_d == null){
            con_d = Connect.getDiningCon();
            close_con = true;
        }
        
        Connection con = Connect.getCon(req);
        
        if(organization_id == null){
            organization_id = Utilities.getOrganizationId(con);
        }
        
        if(person_id == null){
            person_id = Utilities.getPersonId(user, con);
        }
  
        
        List<reservationSignup> result = new ArrayList<reservationSignup>();

        try {
            
            // Get list of reservations for a given person
            pstmt = con_d.prepareStatement(""
                    + "SELECT r.reservation_number "
                    + " FROM reservations r "
                    + " WHERE r.date >= CAST(? AS DATE) "
                    + "  AND r.organization_id = ? "
                    + "  AND r.person_id = ? " // May not be the master record, but should only be one for a given reservation
                    + "  AND r.state <> 'cancelled' "
                    + " ORDER BY r.date, r.time ");

            pstmt.clearParameters();
            pstmt.setString(1, timeUtil.getDbDate(timeUtil.getClubDate(req)));
            pstmt.setInt(2, organization_id);
            pstmt.setInt(3, person_id);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                result.addAll(getSignupsByReservation(organization_id, rs.getInt("reservation_number"), req, con_d));
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.getSignupsByUser: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }
        
        if(close_con){
            try {
                con_d.close();
            } catch (Exception ignore) {
            }
        }

        return result;
    }
    
    public static List<reservationSignup> getSignupsByDate(int date, int organization_id, String user, HttpServletRequest req, Connection con_d) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List<reservationSignup> result = new ArrayList<reservationSignup>();

        try {
            
            // Get list of reservations for a given date
            pstmt = con_d.prepareStatement(""
                    + "SELECT r.reservation_number "
                    + " FROM reservations r "
                    + " WHERE r.date = CAST(? AS DATE) "
                    + "  AND r.organization_id = ? "
                    + "  AND r.parent_id IS NULL " // Only get master record for reservation
                    + "  AND r.state <> 'cancelled' "
                    + " ORDER BY r.date, r.time ");

            pstmt.clearParameters();
            pstmt.setString(1, timeUtil.getDbDate(date));
            pstmt.setInt(2, organization_id);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                result.addAll(getSignupsByReservation(organization_id, rs.getInt("reservation_number"), req, con_d));
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.getSignupsByDate: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return result;
    }
    
    public static List<reservationSignup> getEventSignups(int event_id, int organization_id, String user, HttpServletRequest req, Connection con_d) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        List<reservationSignup> result = new ArrayList<reservationSignup>();

        try {
            
            // Get list of reservations for a given event
            pstmt = con_d.prepareStatement(""
                    + "SELECT r.reservation_number "
                    + " FROM reservations r "
                    + " WHERE r.event_id = ? "
                    + "  AND r.organization_id = ? "
                    + "  AND r.parent_id IS NULL " // Only get master record for reservation
                    + "  AND r.state <> 'cancelled' "
                    + " ORDER BY r.date, r.time ");

            pstmt.clearParameters();
            pstmt.setInt(1, event_id);
            pstmt.setInt(2, organization_id);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                result.addAll(getSignupsByReservation(organization_id, rs.getInt("reservation_number"), req, con_d));
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.getEventSignups: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return result;
    }
    
       
    private static List<reservationSignup> getSignupsByReservation(int organization_id, int reservation_number, HttpServletRequest req, Connection con_d) {

        PreparedStatement pstmt2 = null;
        ResultSet rs2 = null;
        
        Connection con = Connect.getCon(req);
        
        // This caching of person_id should e moved to reqUtil
        Integer person_id = reqUtil.getRequestInteger(req, req_person_id_cache, null);
        if(person_id == null){
            person_id = Utilities.getPersonId(reqUtil.getSessionString(req, "user", reqUtil.getSessionString(req, "ext-user", "")), con);
            req.setAttribute(req_person_id_cache, person_id);
        }
        
        List<reservationSignup> result = new ArrayList<reservationSignup>();

        try {

            // Get list of reservees for this reservation number
            pstmt2 = con_d.prepareStatement(""
                    + "SELECT r.*, "
                    + "  to_char(r.time, 'HH24MI')::int AS int_time, "
                    + "  to_char(r.date, 'YYYYMMDD')::int AS int_date, "
                    + "  u.username, " // Foretees username
                    + "  e.name AS event_name, "
                    + "  l.name AS location_name, "
                    + "  p.last_name, p.first_name, p.middle_name "
                    + " FROM reservations r "
                    + "  LEFT OUTER JOIN people p "
                    + "   ON p.id = r.person_id "
                    + "   AND p.organization_id = r.organization_id "
                    + "  LEFT OUTER JOIN events e "
                    + "   ON e.id = r.event_id "
                    + "  LEFT OUTER JOIN locations l "
                    + "   ON l.id = r.location_id "
                    + "  LEFT OUTER JOIN users u "
                    + "   ON u.person_id = r.person_id " // To get foretees username
                    + " WHERE r.reservation_number = ? "
                    + "  AND r.organization_id = ? "
                    + "  AND r.state <> 'cancelled' "
                    + " ORDER BY r.parent_id DESC, r.check_number, r.id "); // Float master to the top

            pstmt2.clearParameters();
            pstmt2.setInt(1, reservation_number);
            pstmt2.setInt(2, organization_id);
            rs2 = pstmt2.executeQuery();

            boolean can_access = false;
            boolean init = false;
            Set<String> statuses = new HashSet<String>();

            Integer signup_id = null;
            
            reservationSignup signup = new reservationSignup();

            while (rs2.next()) {
                if(!init){
                    // set some things off the first record
                    init = true;
                    signup.location = rs2.getString("location_name");
                    signup.type = rs2.getString("event_name");
                    if(signup.type == null){
                        signup.type = "Dining";
                    }
                    signup.time = rs2.getInt("int_time");
                    signup.date = rs2.getInt("int_date");
                    signup.select_button = timeUtil.get12HourTime(signup.time);
                    signup_id = rs2.getInt("id");
                    signup.event_id = rs2.getInt("event_id");
                }
                statuses.add(StringUtils.capitalize(rs2.getString("state")));
                int record_person_id = rs2.getInt("person_id");
                if(person_id == record_person_id){
                    signup.in_signup = true;
                    can_access = true;
                }
                if(record_person_id > 0){
                    signup.members ++;
                } else {
                    signup.guests ++;
                }
                String[] names = foreTeesNames(rs2.getString("first_name"), rs2.getString("middle_name"), rs2.getString("last_name"), rs2.getString("reservee_name"));
                signup.players.add(new reservationPlayer(rs2.getString("username"), names[FML], names[LFM], StringUtils.capitalize(rs2.getString("state"))));
            }

            if(can_access){
                signup.id = signup_id;
            }
            signup.show_player_status = statuses.size() > 1;
            signup.status = StringUtils.join(statuses,", ");
            if(signup.event_id != null && signup.event_id > 0){
                signup.slot_type = "dining_event";
            } else {
                signup.slot_type = "dining_reservation";
            }

            result.add(signup); // We could just return the signup, but the list, even for this single signup, makes things easier

        } catch (Exception exc) {

            Utilities.logError("diningUtil.getSignupsByReservation: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));

        } finally {

            try {
                rs2.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt2.close();
            } catch (Exception ignore) {
            }

        }

        return result;
    }
    
    public static String[] foreTeesNames(String first, String mi, String last, String guest_name){
        // Format name parts from dining DB to formats we're used to in foretees. {"First M Last","Last, First M"}
        if(last == null || first == null || last.isEmpty()){
            return new String[]{guest_name,guest_name};
        }
        StringBuilder name = new StringBuilder();
        StringBuilder alphaName = new StringBuilder();
        name.append(first);
        alphaName.append(last);
        alphaName.append(", ");
        alphaName.append(first);
        if(mi != null && !mi.isEmpty()){
          name.append(" ");
          alphaName.append(" ");
          name.append(mi); 
          alphaName.append(mi);
        }
        name.append(" ");
        name.append(last); 
        return new String[]{name.toString(),alphaName.toString()};
    }

    public static parmEvent load1Event(int event_id, int organization_id, String user, HttpServletRequest req, Connection con_d) {

        parmEvent parm = new parmEvent();

        PreparedStatement pstmt = null, pstmt2 = null;
        ResultSet rs = null, rs2 = null;

        String err_tmp = "1";
        
        Connection con = Connect.getCon(req);

        boolean is_signed_up = false;
        boolean can_sign_up = false;
        boolean in_signup_period = false;

        //int signup_begin_day = 0, signup_end_day = 0;
        int signup_begin_date = 0, signup_end_date = 0;
        int person_id = Utilities.getPersonId(user, con);
        int today = timeUtil.getClubDate(req);

        String state = "";//, cost = "", price_category = "", price_type_id = "";

        /*
         *  default behavior is to show all upcoming events - but if the club
         *  wants to hide events that members cannot signup for online
         *  then set hide_offline_events to true for that club. the no_online_text
         *  string can be changed to say 'Please Call' or something
         */
        //boolean hide_offline_events = false;    // customize this value if clubs want to display events members cannot sign up for online

        //String no_online_text = "N/A";

        try {

            int reservation_id = 0;

            pstmt = con_d.prepareStatement(""
                    + "SELECT e.id, e.name, e.members_can_make_reservations, "
                    + "e.minimum_advance_days, e.maximum_advance_days, "
                    + "to_char(e.start_time, 'HH24:MI') AS time1, "
                    + "to_char(e.start_time, 'HH24MI') AS stime, "
                    + "to_char(e.end_time, 'HH24MI') AS etime, "
                    + "to_char(e.date, 'YYYYMMDD')::int AS our_date, "
                    + "to_char(e.date, 'MM/DD/YYYY') AS date1, "
                    + "e.costs, e.seatings, e.time_format, e.online_message, loc.name AS location_name, "
                    + "e.start_time, e.date, e.maximum_party_size "
                    + "FROM events e "
                    + "LEFT OUTER JOIN locations AS loc ON e.location_id = loc.id "
                    + "WHERE e.id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, event_id);
            rs = pstmt.executeQuery();

            while (rs.next()) {

                // extract the event info

                can_sign_up = rs.getBoolean("members_can_make_reservations");

                //
                // hide events that members cannot sign up for online
                //if (can_sign_up || (!can_sign_up && !hide_offline_events)) {

                signup_begin_date = timeUtil.add(req, rs.getInt("our_date"), 0, Calendar.DATE, 0 - rs.getInt("maximum_advance_days"))[timeUtil.DATE];
                signup_end_date = timeUtil.add(req, rs.getInt("our_date"), 0, Calendar.DATE, 0 - rs.getInt("minimum_advance_days"))[timeUtil.DATE];

                in_signup_period = (signup_begin_date <= today && signup_end_date >= today);

                parmDiningCosts parmCosts = new parmDiningCosts();
                parmCosts.costs = rs.getString("costs");
                parmCosts.parseCosts();

                parmDiningSeatings parmSeatings = new parmDiningSeatings();
                parmSeatings.seatings = rs.getString("seatings");
                parmSeatings.parseSeatings();

                // lookup this member in the reservations table to see if they are already signed up for this dining event
                pstmt2 = con_d.prepareStatement(""
                        + "SELECT id, state "
                        + " FROM reservations "
                        + " WHERE category = 'event' "
                        + "  AND event_id = ? "
                        + "  AND state <> 'cancelled' "
                        + "  AND person_id = ? ");

                pstmt2.setInt(1, event_id);
                pstmt2.setInt(2, person_id);

                rs2 = pstmt2.executeQuery();

                if (rs2.next()) {

                    is_signed_up = true;
                    state = rs2.getString("state");
                    reservation_id = rs2.getInt("id");

                } else {

                    is_signed_up = false;
                    state = "";
                }

                //
                // populate the event parm

                parm.id = event_id;
                parm.name = rs.getString("name");
                parm.date = rs.getInt("our_date");
                //parm.dateString = getDateFromYYYYMMDD(rs.getInt("our_date"), 2);
                //parm.startTime24hString = rs.getString("time1");
                parm.startTime = rs.getInt("stime");
                //parm.startTimeString = getSimpleTime(rs.getInt("stime"));
                parm.endTime = rs.getInt("etime");
                //parm.endTimeString = getSimpleTime(rs.getInt("etime"));
                //parm.timeFormat = rs.getString("time_format");
                parm.locationName = rs.getString("location_name");
                parm.isSignedUp = is_signed_up;
                parm.canSignUp = can_sign_up;
                parm.inSignUpPeriod = in_signup_period;
                parm.state = Utilities.titleCase(state);
                parm.maximumPartySize = rs.getInt("maximum_party_size");
                parm.reservationId = reservation_id; // if person is signed up this is their reservation id
                parm.locationName = rs.getString("location_name");
                parm.onlineMessage = rs.getString("online_message");
                parm.isEventOpen = parmDining.isEventOpen(organization_id, rs.getInt("id"), con, con_d);
                parm.registrationStart = signup_begin_date;
                parm.registrationEnd = signup_end_date;
                //parm.registrationStartString = Utilities.getDateFromYYYYMMDD(signup_begin_date, 2);
                //parm.registrationEndString = Utilities.getDateFromYYYYMMDD(signup_end_date, 2);

                for (int i = 0; i < parmCosts.costs_found; i++) {

                    parm.eventCostCategory.add(parmCosts.price_categoryA[i]);
                    parm.eventCostPrice.add(parmCosts.costA[i]);
                }

                for (int i = 0; i < parmSeatings.seatings_found; i++) {

                    parm.eventSeatingTimes.add(parmSeatings.seating_timeA[i]);
                }


                //} // end supress event

            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.load1Event: id=" + event_id + ", qry=" + err_tmp + ", Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return parm;
    }
    
    public static void sendClubNotification(String notificationReason, int organization_id, int reservation_number, parmDining2 oldParmD, HttpServletRequest req, Connection con_d){
        
        String[] notificationAddress = getClubNotificationAddress(con_d, req);
        
        if(notificationAddress == null){
            return; // club does not use reservation change notifications
        }
        
        // Send notification email to club letting them know a resevation has been changed/created
        
        // Get reservation
        int reservation_id = getValidMasterIdFromReservationNumber(organization_id, reservation_number, con_d);
        parmDining2 parmD = new parmDining2(organization_id, reservation_id, con_d, req);
        
        StringBuilder message = new StringBuilder();
        
        appendItemToMessage(message,"Action",notificationReason);
        
        appendItemToMessage(message,"Reservation Number",Integer.toString(reservation_number));

        if(parmD.reservation_load_error != null || parmD.id == 0){
            // Reservation was probably cancelled,
            // use old parm  
            parmD = oldParmD;
        }
        
        //Set the subject
        String subject = timeUtil.getStringDateMDYYYY(parmD.date) + " @ " + timeUtil.get12HourTime(parmD.time) + " - " + parmD.location_name + " for " + parmD.names.get(0);
        if (parmD.names.size() > 1) subject += " + " + (parmD.names.size()- 1);
        
        // Show the reservation information
        appendItemToMessage(message,"Date",timeUtil.getDayOfWeek(parmD.date) + " " + timeUtil.getStringDateMDYYYY(parmD.date));
        appendItemToMessage(message,"Time",timeUtil.get12HourTime(parmD.time));
            
        if(parmD.event_id > 0){
            appendItemToMessage(message,"Event",parmD.event.name);
        }
        appendItemToMessage(message,"Location",parmD.location_name);

        for(int i = 0; i < parmD.names.size(); i++){
            StringBuilder person = new StringBuilder();
            person.append(parmD.names.get(i));
            if(parmD.event_id > 0){
                person.append(", ");
                person.append(parmD.price_category.get(i));
            }
            person.append(", Check #");
            person.append(parmD.check_num.get(i));
            appendItemToMessage(message,"Person #"+(i+1),person.toString());
        }
        
        appendItemToMessage(message,"Occasion",getOccasionName(parmD.occasion_id));
        
        appendItemToMessage(message,"Special Requests",parmD.special_requests);
        
        
        Properties properties = new Properties();
        properties.put("mail.smtp.host", ProcessConstants.HOST);                     // set outbound host address
        properties.put("mail.smtp.port", ProcessConstants.PORT);                     // set outbound port
        properties.put("mail.smtp.auth", "true");                               // set 'use authentication'

        Session mailSess = Session.getInstance(properties, Utilities.getAuthenticator());   // get session properties

        MimeMessage mail = new MimeMessage(mailSess);

        try {

            mail.setFrom(new InternetAddress(ProcessConstants.EFROM));                  // set from addr
            mail.setSubject(subject);                                              // set subject line
            mail.setSentDate(new java.util.Date());                                // set date/time sent
            for(int i = 0; i < notificationAddress.length; i++){
                 mail.addRecipient(Message.RecipientType.TO, new InternetAddress(notificationAddress[i]));  // add recipient
            }
           
            mail.setText(message.toString());                                                     // put msg in email text area

            Transport.send(mail);                                                  // send it!!

        } catch (Exception exc) {

            Utilities.logError("diningUtil.sendClubNotification: Error sending custom email verification. " + exc.getMessage() + ", " + exc.toString());

        }
        
    }
    
    private static void appendItemToMessage(StringBuilder message, String label, String item) {
        
        message.append(label);
        message.append(": ");
        message.append(item);
        message.append("\r\n");
        
    }

    public static Map<String, Integer> buildInvalidUserMap(List<String> user_a, List<Integer> person_id_a) {

        Map<String, Integer> result = new LinkedHashMap<String, Integer>(); // Linked hash map for order

        for (int i = 0; i < user_a.size(); i++) {
            String user = user_a.get(i).trim().toLowerCase();
            if (!user.isEmpty() && person_id_a.get(i) == 0) {
                result.put(user, i);
            }
        }

        return result;

    }

    public static Integer indexByRelatedId(parmDining2 parmD, Integer related_id) {

        for (int i = 0; i < parmD.related_id.size(); i++) {
            Integer id = parmD.related_id.get(i);
            if (id != null && related_id != null && related_id.equals(id) && id > 0) {
                return i;
            }
        }

        return null;

    }

    public static int indexByPersonId(parmDining2 parmD, Integer related_id) {

        for (int i = 0; i < parmD.dining_id.size(); i++) {
            Integer id = parmD.dining_id.get(i);
            if (id != null && related_id != null && related_id.equals(id) && id > 0) {
                return i;
            }
        }

        return -1;

    }

    public static Map<String, Integer> buildUserMap(List<String> user_a, List<Integer> person_id_a) {

        Map<String, Integer> result = new LinkedHashMap<String, Integer>(); // Linked hash map for order

        for (int i = 0; i < user_a.size(); i++) {
            String user = user_a.get(i).trim().toLowerCase();
            if (!user.isEmpty() && person_id_a.get(i) > 0) {
                result.put(user, i);
            }
        }

        return result;

    }

    public static Map<String, List<Integer>> buildGuestMap(List<String> user_a, List<String> player_a) {

        Map<String, List<Integer>> result = new LinkedHashMap<String, List<Integer>>(); // Linked hash map for order

        for (int i = 0; i < user_a.size(); i++) {
            String user = user_a.get(i).trim().toLowerCase();
            String player = player_a.get(i).trim().toLowerCase();
            List<Integer> list;
            if (user.isEmpty()) {
                // Check if we've already added a guest with this name
                list = result.get(player);
                if (list == null) {
                    // Guest name not in map, add a new list
                    list = new ArrayList<Integer>();
                    result.put(player, list);
                }
                // Add guest to list
                list.add(i);
            }
        }

        return result;

    }

    public static Integer popMember(String member, Map<String, Integer> memberMap) {

        return memberMap.remove(member.trim().toLowerCase());

    }

    public static Integer getMember(String member, Map<String, Integer> memberMap) {

        return memberMap.get(member.trim().toLowerCase());

    }

    public static String getForteesName(String username, Connection con) {

        String ret = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con.prepareStatement(""
                    + "SELECT CONCAT_WS(' ',name_first, if(name_mi = '',null,name_mi), name_last) AS fullName "
                    + "FROM member2b "
                    + "WHERE username = ?;");
            pstmt.clearParameters();
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                ret = rs.getString("fullName");
            }

        } catch (Exception ignore) {
        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return ret;

    }

    public static Integer popGuest(String guest_name, Map<String, List<Integer>> guestMap) {

        Integer result = null;

        List<Integer> list;
        list = guestMap.get(guest_name.trim().toLowerCase());
        if (list != null && !list.isEmpty()) {
            result = list.get(0);
            list.remove(0);
            if (list.isEmpty()) {
                guestMap.remove(guest_name.trim().toLowerCase());
            }
        }

        return result;

    }

    public static String nullCheck(String val, String alt) {
        if (val == null) {
            return alt;
        } else {
            return val;
        }
    }

    public static Integer nullCheck(Integer val, Integer alt) {
        if (val == null) {
            return alt;
        } else {
            return val;
        }
    }
}