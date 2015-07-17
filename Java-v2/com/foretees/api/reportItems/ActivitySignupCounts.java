/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.reportItems;

import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.Connect;
import com.foretees.common.ProcessConstants;
import com.foretees.common.timeUtil;
import com.foretees.common.getActivity;
import java.sql.*;          // mysql
import java.util.*;

import org.joda.time.*;

/**
 *
 * @author Owner
 * 
 */
public class ActivitySignupCounts {
    
    public Integer activity_id;
    public String date;
    
    public Integer total;
    public Integer members;
    public Integer guests;
    
    public Integer activity_total;
    public Integer activity_members;
    public Integer activity_guests;
    
    public Integer event_total;
    public Integer event_members;
    public Integer event_guests;
    
    public String activity_text;
    public String event_text;
    
    public String last_error;
    
    
    // Registration Counts
    private final static String sql_golf_tee_curr_counts = ""
            + "SELECT t.date, "
            + "    SUM(if(t.player1 != '' AND t.player1 != 'X',1,0)+if(t.player2 != '' AND t.player2 != 'X',1,0) "
            + "      +if(t.player3 != '' AND t.player3 != 'X',1,0)+if(t.player4 != '' AND t.player4 != 'X',1,0) "
            + "      +if(t.player5 != '' AND t.player5 != 'X',1,0)) "
            + "         AS total_registrations, "
            + "    SUM((if(t.player1 != '' AND t.player1 != 'X',1,0)+if(t.player2 != '' AND t.player2 != 'X',1,0) "
            + "      +if(t.player3 != '' AND t.player3 != 'X',1,0)+if(t.player4 != '' AND t.player4 != 'X',1,0) "
            + "      +if(t.player5 != '' AND t.player5 != 'X',1,0)) * if(t.event != '',1,0)) "
            + "         AS total_event_registrations, "
            + "    SUM((if(t.player1 != '' AND t.player1 != 'X',1,0)+if(t.player2 != '' AND t.player2 != 'X',1,0) "
            + "      +if(t.player3 != '' AND t.player3 != 'X',1,0)+if(t.player4 != '' AND t.player4 != 'X',1,0) "
            + "      +if(t.player5 != '' AND t.player5 != 'X',1,0)) * if(t.event = '',1,0)) "
            + "         AS total_golf_registrations, "
            + "    SUM(if(t.username1 != '',1,0)+if(t.username2 != '',1,0)+if(t.username3 != '',1,0)+if(t.username4 != '',1,0) "
            + "      +if(t.username5 != '',1,0)) "
            + "         AS member_registrations, "
            + "    SUM((if(t.username1 != '',1,0)+if(t.username2 != '',1,0)+if(t.username3 != '',1,0)+if(t.username4 != '',1,0) "
            + "      +if(t.username5 != '',1,0)) * if(t.event != '',1,0)) "
            + "         AS member_event_registrations, "
            + "    SUM((if(t.username1 != '',1,0)+if(t.username2 != '',1,0)+if(t.username3 != '',1,0)+if(t.username4 != '',1,0) "
            + "      +if(t.username5 != '',1,0)) * if(t.event = '',1,0)) "
            + "         AS member_golf_registrations, "
            + "    SUM(if(t.userg1 != '',1,0)+if(t.userg2 != '',1,0)+if(t.userg3 != '',1,0)+if(t.userg4 != '',1,0) "
            + "      +if(t.userg5 != '',1,0)) "
            + "         AS aguest_registrations, "
            + "    SUM((if(t.userg1 != '',1,0)+if(t.userg2 != '',1,0)+if(t.userg3 != '',1,0)+if(t.userg4 != '',1,0) "
            + "      +if(t.userg5 != '',1,0)) * if(t.event != '',1,0)) "
            + "         AS aguest_event_registrations, "
            + "    SUM((if(t.userg1 != '',1,0)+if(t.userg2 != '',1,0)+if(t.userg3 != '',1,0)+if(t.userg4 != '',1,0) "
            + "      +if(t.userg5 != '',1,0)) * if(t.event = '',1,0)) "
            + "         AS aguest_golf_registrations "
            + "  FROM teecurr2 t "
            + "  WHERE "
            + "    t.date BETWEEN ? AND ? "
            + "  GROUP BY t.date ";
    
    private final static String sql_golf_tee_past_counts = ""
            + "SELECT t.date, "
            + "    SUM(if(t.player1 != '' AND t.player1 != 'X',1,0)+if(t.player2 != '' AND t.player2 != 'X',1,0) "
            + "      +if(t.player3 != '' AND t.player3 != 'X',1,0)+if(t.player4 != '' AND t.player4 != 'X',1,0) "
            + "      +if(t.player5 != '' AND t.player5 != 'X',1,0)) "
            + "         AS total_registrations, "
            + "    SUM((if(t.player1 != '' AND t.player1 != 'X',1,0)+if(t.player2 != '' AND t.player2 != 'X',1,0) "
            + "      +if(t.player3 != '' AND t.player3 != 'X',1,0)+if(t.player4 != '' AND t.player4 != 'X',1,0) "
            + "      +if(t.player5 != '' AND t.player5 != 'X',1,0)) * if(t.event != '',1,0)) "
            + "         AS total_event_registrations, "
            + "    SUM((if(t.player1 != '' AND t.player1 != 'X',1,0)+if(t.player2 != '' AND t.player2 != 'X',1,0) "
            + "      +if(t.player3 != '' AND t.player3 != 'X',1,0)+if(t.player4 != '' AND t.player4 != 'X',1,0) "
            + "      +if(t.player5 != '' AND t.player5 != 'X',1,0)) * if(t.event = '',1,0)) "
            + "         AS total_golf_registrations, "
            + "    SUM(if(t.username1 != '',1,0)+if(t.username2 != '',1,0)+if(t.username3 != '',1,0)+if(t.username4 != '',1,0) "
            + "      +if(t.username5 != '',1,0)) "
            + "         AS member_registrations, "
            + "    SUM((if(t.username1 != '',1,0)+if(t.username2 != '',1,0)+if(t.username3 != '',1,0)+if(t.username4 != '',1,0) "
            + "      +if(t.username5 != '',1,0)) * if(t.event != '',1,0)) "
            + "         AS member_event_registrations, "
            + "    SUM((if(t.username1 != '',1,0)+if(t.username2 != '',1,0)+if(t.username3 != '',1,0)+if(t.username4 != '',1,0) "
            + "      +if(t.username5 != '',1,0)) * if(t.event = '',1,0)) "
            + "         AS member_golf_registrations, "
            + "    SUM(if(t.userg1 != '',1,0)+if(t.userg2 != '',1,0)+if(t.userg3 != '',1,0)+if(t.userg4 != '',1,0) "
            + "      +if(t.userg5 != '',1,0)) "
            + "         AS aguest_registrations, "
            + "    SUM((if(t.userg1 != '',1,0)+if(t.userg2 != '',1,0)+if(t.userg3 != '',1,0)+if(t.userg4 != '',1,0) "
            + "      +if(t.userg5 != '',1,0)) * if(t.event != '',1,0)) "
            + "         AS aguest_event_registrations, "
            + "    SUM((if(t.userg1 != '',1,0)+if(t.userg2 != '',1,0)+if(t.userg3 != '',1,0)+if(t.userg4 != '',1,0) "
            + "      +if(t.userg5 != '',1,0)) * if(t.event = '',1,0)) "
            + "         AS aguest_golf_registrations "
            + "  FROM teepast2 t "
            + "  WHERE "
            + "    t.date BETWEEN ? AND ? "
            + "  GROUP BY t.date ";
    
    private final static String sql_event_signup_counts = ""
            + "SELECT e.date, e.activity_id, "
            + "    SUM(if(es.player1 != '' AND es.player1 != 'X',1,0)+if(es.player2 != '' AND es.player2 != 'X',1,0) "
            + "      +if(es.player3 != '' AND es.player3 != 'X',1,0)+if(es.player4 != '' AND es.player4 != 'X',1,0) "
            + "      +if(es.player5 != '' AND es.player5 != 'X',1,0)) "
            + "        AS total_registrations, "
            + "    SUM(if(es.username1 != '',1,0)+if(es.username2 != '',1,0)+if(es.username3 != '',1,0)+if(es.username4 != '',1,0) "
            + "      +if(es.username5 != '',1,0)) "
            + "        AS member_registrations, "
            + "    SUM(if(es.userg1 != '',1,0)+if(es.userg2 != '',1,0)+if(es.userg3 != '',1,0)+if(es.userg4 != '',1,0) "
            + "      +if(es.userg5 != '',1,0)) "
            + "        AS aguest_registrations "
            + "  FROM events2b e "
            + "    INNER JOIN evntsup2b es "
            + "      ON es.name = e.name AND es.inactive = 0 "
            + "  WHERE e.activity_id = ? "
            + "    AND e.date BETWEEN ? AND ? "
            + "    AND e.inactive = 0 "
            + "  GROUP BY e.date ";
    
    private final static String sql_activity_signup_counts = ""
            + "SELECT DATE(a.date_time) as `date`, a.activity_id, "
            + "    SUM(IF(ap.player_name != '' AND ap.player_name != 'x',1,0)) AS total_registrations, "
            + "    SUM(IF(ap.player_name != '' AND ap.player_name != 'x' AND a.event_id != 0,1,0)) AS total_event_registrations, "
            + "    SUM(IF(ap.username != '',1,0)) AS member_registrations, "
            + "    SUM(IF(ap.username != '' AND a.event_id != 0,1,0)) AS member_event_registrations, "
            + "    SUM(IF(ap.userg != '',1,0)) AS aguest_registrations, "
            + "    SUM(IF(ap.userg != '' AND a.event_id != 0,1,0)) AS aguest_event_registrations "
            + "  FROM activity_sheets a "
            + "    INNER JOIN activity_sheets_players ap "
            + "      ON ap.activity_sheet_id = a.sheet_id "
            + "    LEFT JOIN activities ac1 "
            + "      ON ac1.activity_id = a.activity_id "
            + "    LEFT JOIN activities ac2 "
            + "      ON ac2.activity_id = ac1.parent_id "
            + "    LEFT JOIN activities ac3 "
            + "      ON ac3.activity_id = ac2.parent_id "
            + "    LEFT JOIN activities ac4 "
            + "      ON ac4.activity_id = ac3.parent_id "
            + "    LEFT JOIN activities ac5 "
            + "      ON ac5.activity_id = ac4.parent_id "
            + "  WHERE (a.activity_id = ? OR ac1.activity_id = ? OR ac2.activity_id = ? OR ac3.activity_id = ? OR ac4.activity_id = ? OR ac5.activity_id = ?) " // Ugly temporary work-around.  Clean up later
            + "    AND a.report_ignore = 0 AND a.date_time BETWEEN ? AND ? " // NOTE: use "YYYY-MM-DD 00:00:00" to "YYYY-MM-DD 23:59:59"
            + "  GROUP BY date ";
    
    private final static String sql_dining_signup_counts = ""  // NOTE: this is postgresql, not mysql
            + "SELECT MAX(to_char(r.date, 'YYYY-MM-DD')) AS date, "
            + "    COUNT(*) AS total_registrations, "
            + "    COUNT(NULLIF(COALESCE(r.person_id,0),0)) AS member_registrations, "
            + "    COUNT(e.id) AS total_event_registrations, "
            + "    COUNT(NULLIF(CAST(COALESCE(r.person_id,0) AS BIGINT) * CAST(COALESCE(e.id,0) AS BIGINT),0)) AS member_event_registrations "
            + "  FROM reservations r "
            + "    LEFT OUTER JOIN events e "
            + "      ON e.id = r.event_id "
            + "  WHERE r.organization_id = ? "
            + "    AND r.date BETWEEN CAST(? AS DATE) AND CAST(? AS DATE) "
            + "    AND r.state <> 'cancelled' "
            + "  GROUP BY r.date ";
    
    
    
    //public List<String> debug = new ArrayList<String>();
    
    public ActivitySignupCounts(){}
    
    public ActivitySignupCounts(int activity_id, String date, 
            Integer activity_total, Integer activity_members, Integer activity_guests,
            Integer events_total, Integer events_members, Integer events_guests, 
            String activity_text, String events_text ){
    
        this.activity_id = activity_id;
        this.date = date;
        this.activity_total = activity_total;
        this.activity_members = activity_members;
        this.activity_guests = activity_guests;
        this.event_total = events_total;
        this.event_members = events_members;
        this.event_guests = events_guests;
        this.activity_text = activity_text;
        this.event_text = events_text;
    
    }
    
    public ActivitySignupCounts(Connection con_club, int activity_id, String date){
        
        loadActivitySignupCounts(con_club, activity_id, date);

    }
    
    public ActivitySignupCounts(Long club_id, int activity_id, String date){
        
        Connection con_club = ApiCommon.getConnection(club_id);
        loadActivitySignupCounts(con_club, activity_id, date);
        Connect.close(con_club);
        
    }
    
    public final void loadActivitySignupCounts(Connection con_club, int activity_id, String date){
        
        this.activity_id = activity_id;
        this.date = date;
        
        int idate = timeUtil.getIntDateFromString(date);
        String sdate = timeUtil.getDbDate(idate); // Normalize the string date that was passed
        String edate = sdate + " 23:59:59";
        zeroCounts();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
     
        try {

            switch (activity_id) {

                case 0: // Golf
                    
                    activity_text = "Tee Times";
                    event_text = "Events";

                    pstmt = con_club.prepareStatement(sql_golf_tee_curr_counts);
                    pstmt.setInt(1, idate); // from
                    pstmt.setInt(2, idate); // to
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        addActivityCounts(rs);
                    }
                    pstmt = con_club.prepareStatement(sql_golf_tee_past_counts);
                    pstmt.setInt(1, idate); // from
                    pstmt.setInt(2, idate); // to
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        addActivityCounts(rs);
                    }

                    break;

                case ProcessConstants.DINING_ACTIVITY_ID: // Dining
                    
                    activity_text = "A la carte";
                    event_text = "Events";

                    Long organization_id = ApiCommon.getClubOrganizationId(con_club);
                    
                    Connection con_dining = Connect.getDiningCon();
                    
                    try {
                        pstmt = con_dining.prepareStatement(sql_dining_signup_counts);
                        pstmt.setLong(1, organization_id);
                        pstmt.setString(2, sdate); // from
                        pstmt.setString(3, edate); // to
                        rs = pstmt.executeQuery();
                        if (rs.next()) {
                            addActivityCounts(rs);
                        }

                    } catch (Exception e) {
                        Connect.logError(className() + ".loadActivitySignupCounts: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e) + " organization_id="+(organization_id==null?"null":organization_id)+" sdate="+(sdate==null?"null":sdate)+" edate="+(edate==null?"null":edate));
                        last_error = String.format(ApiConstants.error_loading_by_id, classNameLower(), e.toString());
                    } finally {
                        Connect.close(rs, pstmt, con_dining);
                    }

                    break;

                default: // Flxrez
                    
                    activity_text = getActivity.getActivityName (activity_id, con_club);
                    event_text = "Events";
                    
                    int i = 1;
                    pstmt = con_club.prepareStatement(sql_activity_signup_counts);
                    pstmt.setInt(i++, activity_id); // Ugly hack.  Fix later.
                    pstmt.setInt(i++, activity_id); // ''
                    pstmt.setInt(i++, activity_id); // ''
                    pstmt.setInt(i++, activity_id); // ''
                    pstmt.setInt(i++, activity_id); // ''
                    pstmt.setInt(i++, activity_id); // ''
                    pstmt.setString(i++, sdate); // from
                    pstmt.setString(i++, edate); // to
                    rs = pstmt.executeQuery();
                    if (rs.next()) {
                        addActivityCounts(rs);
                    }

                    break;

            }
            if (activity_id != ProcessConstants.DINING_ACTIVITY_ID) {
                
                // Get flxrez or golf event signups that havn't been converted
                pstmt = con_club.prepareStatement(sql_event_signup_counts);
                pstmt.setInt(1, activity_id);
                pstmt.setInt(2, idate); // from
                pstmt.setInt(3, idate); // to
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    addEventSignupCounts(rs);
                }

            }
        } catch (Exception e) {
            Connect.logError(className() + ".loadActivitySignupCounts: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = String.format(ApiConstants.error_loading_by_id, classNameLower(), e.toString());
        } finally {
            Connect.close(rs, pstmt);
        }


    }

    public final void addActivityCounts(ResultSet rs) {

        int c_total, c_member, c_guest, a_total, a_member, a_guest, e_total, e_member, e_guest;
        try {

            c_total = rs.getInt("total_registrations");
            c_member = rs.getInt("member_registrations");
            c_guest = c_total - c_member;

            e_total = rs.getInt("total_event_registrations");
            e_member = rs.getInt("member_event_registrations");
            e_guest = e_total - e_member;

            a_total = c_total - e_total;
            a_member = c_member - e_member;
            a_guest = a_total - a_member;

            this.total += c_total;
            this.members += c_member;
            this.guests += c_guest;
            this.activity_total += a_total;
            this.activity_members += a_member;
            this.activity_guests += a_guest;
            this.event_total += e_total;
            this.event_members += e_member;
            this.event_guests += e_guest;


        } catch (Exception e) {
            Connect.logError(className() + ".addActivityCounts: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = String.format(ApiConstants.error_resultset, classNameLower(), e.toString());
        } finally {
        }

    }
    
    public final void addEventSignupCounts(ResultSet rs){
        
        int e_total, e_member, e_guest;
        try {
            
            e_total = rs.getInt("total_registrations");
            e_member = rs.getInt("member_registrations");
            e_guest = e_total - e_member;

            this.total += e_total;
            this.members += e_member;
            this.guests += e_guest;
            this.event_total += e_total;
            this.event_members += e_member;
            this.event_guests += e_guest;
            
            
        } catch(Exception e) {
            Connect.logError(className()+".addEventSignupCounts: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = String.format(ApiConstants.error_resultset, classNameLower(), e.toString());
        } finally {
        }

    }
    
    public final void zeroCounts(){
        
        this.total = 0;
        this.members = 0;
        this.guests = 0;
        this.activity_total = 0;
        this.activity_members = 0;
        this.activity_guests = 0;
        this.event_total = 0;
        this.event_members = 0;
        this.event_guests = 0;
    }
    
    
    public static List<ActivitySignupCounts> getListByDate(Long club_id, Integer activity_id, String start_date, String end_date){
        Connection con_club = ApiCommon.getConnection(club_id);
        List<ActivitySignupCounts> result = getListByDate(con_club, activity_id, start_date, end_date);
        Connect.close(con_club);
        return result;
    }
    
    
    public static List<ActivitySignupCounts> getListByDate(Connection con_club, Integer activity_id, String start_date, String end_date){
        
        List<ActivitySignupCounts> result = new ArrayList<ActivitySignupCounts>();
        
        String club_tz_string = timeUtil.getClubTimeZoneId(con_club); 
        
        DateTimeZone club_tz = timeUtil.getClubTimeZone(club_tz_string);

        Long ts_start_date = timeUtil.getUnixTimeFromDb(club_tz, timeUtil.formatTzDate(club_tz, timeUtil.getUnixTimeFromDb(club_tz, start_date), "yyyy-MM-dd"));
        Long ts_end_date = timeUtil.getUnixTimeFromDb(club_tz, timeUtil.formatTzDate(club_tz, timeUtil.getUnixTimeFromDb(club_tz, end_date), "yyyy-MM-dd '23:59:59'"));

        int days_between = timeUtil.daysBetween(club_tz, ts_start_date, ts_end_date);
        
        // Temporary hack.  This loop needs to be replaced with a proper sql query that grabs a resultset with all grouped dates.
        for(int i = 0; i <= days_between; i++){
            Long date = timeUtil.addUnixTimeDays(ts_start_date, i);
            int idate = timeUtil.getDate(club_tz, date);
            result.add(new ActivitySignupCounts(con_club, activity_id, timeUtil.getDbDate(idate)));
        }
        
        return result;

    }
    
    public final String className(){
        return this.getClass().getSimpleName();
    }
    
    public final String classNameProper(){
        return ApiCommon.formatClassName(className());
    }
    
    public final String classNameLower(){
        return ApiCommon.formatClassNameLower(className());
    }
    
}
