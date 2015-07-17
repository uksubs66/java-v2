/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.records;

import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.ArrayUtil;
import com.foretees.common.Connect;
import com.foretees.common.AESencrypt;
import com.foretees.common.timeUtil;
import com.foretees.common.ProcessConstants;
import java.sql.*;          // mysql
import java.util.*;
//import javax.naming.*;
//import javax.servlet.http.*;
//import java.io.*;
//import java.util.*;
import org.apache.commons.lang.StringUtils;
/**
 *
 * @author John Kielkopf
 */
public class ClubMember {
    
    public Long id;
    public Long dining_id;
    //public Long club_id; // Currently unimplemented (to be used for club account abstration)
    public String club;

    public String username;
    public String password; // Only used to set password.  Will never contain stored password.
    public String name_last;
    public String name_first;
    public String name_mi;
    public String full_name; // read only
    public String m_ship;
    public String m_type;
    public String email;
    public Integer count;
    public Double c_hancap;
    public Double g_hancap;
    public String wc;
    public String message;
    public Integer emailOpt;
    public Integer emailOpt2;
    public Integer clubEmailOpt1;
    public Integer clubEmailOpt2;
    public Integer memEmailOpt1;
    public Integer memEmailOpt2;
    public String memNum;
    public String ghin;
    public String locker;
    public String bag;
    public String birthdate; // String YYYY-MM-DD; Stored in DB as INT in YYYYMMDD.
    public String posid;
    public String msub_type;
    public String email2;
    public String phone1;
    public String phone2;
    public String name_pre;
    public String name_suf;
    public String custom_string;
    public String custom_string2;
    public String webid;
    public Boolean email_bounced;
    public Boolean email2_bounced;
    public Long hdcp_club_num_id;
    public Long hdcp_assoc_num_id;
    public Long default_tee_id;
    public Boolean default_holes;
    public Boolean displayHdcp;
	//`inact` TINYINT(4) NOT NULL DEFAULT '0', - see 'disabled'
    public Boolean billable;
    public Long last_sync_date;
    public String gender;
    public Boolean pri_indicator;
    public String tflag;
    public Boolean iCal1;
    public Boolean iCal2;
    public Integer default_activity_id;
    public Double ntrp_rating;
    public String usta_num;
    public String mobile_user;
    public String mobile_pass;
    public Integer mobile_count;
    public Integer mobile_iphone;
    public Integer old_mobile_count;

    public Boolean tee_sheet_jump;
    public Boolean read_login_msg;
    public Integer sort_by;
    public String flexid;
    public Boolean display_partner_hndcp;
    public Boolean allow_mp;
    //public Integer vip;

    public Boolean disabled; // stored as 'inact' in DB
    
    //public Long updated;

    public String last_error;
    
    // Load types
    public static int load_none = 0; // do not load any data
    public static int load_full = 1; // load all data
    public static int load_public = 2; // load data that is appropriate for public display
    
    // SQL
    private final static String sql_select = ""
            + "SELECT m.*, FLOOR((m.birth - (FLOOR(m.birth/10000))*10000)/100) AS birth_month, DATABASE() as club "
            + "  FROM member2b m ";
    private final static String sql_order = " ORDER BY m.name_last, m.name_first, m.name_mi ";
    private final static String sql_all = sql_select + sql_order;
    private final static String sql_active = sql_select + " WHERE m.inact = 0 " + sql_order;
    private final static String sql_by_id = sql_select + " WHERE m.id = ? " + sql_order;
    private final static String sql_by_id_in = sql_select + " WHERE m.id IN (%%LIST%%) " + sql_order;
    private final static String sql_by_dining_id = sql_select + " WHERE m.dining_id = ? " + sql_order;
    private final static String sql_by_username = sql_select + " WHERE m.username = ? " + sql_order;
    private final static String sql_by_birthdates = sql_select + " WHERE m.inact = 0 AND (m.birth - (FLOOR(m.birth/10000))*10000) BETWEEN ? AND ? "
            + " ORDER BY (YEAR(CURDATE()) + IF(MONTH(CURDATE())>birth_month,1,0)), birth_month, (m.birth - (FLOOR(m.birth/100))*100),  m.name_last, m.name_first, m.name_mi ";
    private final static String sql_by_accountcredentials = sql_select + " WHERE m.username = ? AND password = ? AND m.inact = 0 " + sql_order;
    
    
    
    
    // Reservation selection (not complete queries. used by other queries)
    private static final String sql_golf_reservation_member_binding = ""
            + "    FROM member2b m "
            + "      INNER JOIN teecurr2 t "
            + "        ON t.username%% = m.username "
            + "        AND t.date BETWEEN ? AND ? ";
    
    private static final String sql_golf_reservation_orig_binding = ""
            + "    FROM member2b m "
            + "      INNER JOIN teecurr2 t "
            + "        ON t.orig_by = m.username "
            + "        AND t.date BETWEEN ? AND ? ";
    
    private static final String sql_event_signup_member_binding = ""
            + "    FROM member2b m "
            + "      INNER JOIN events2b e "
            + "        ON e.inactive = 0 "
            + "          AND e.date BETWEEN ? AND ? "
            + "      INNER JOIN evntsup2b es "
            + "        ON es.name = e.name AND es.inactive = 0 "
            + "          AND es.username%% = m.username ";
    
    private static final String sql_event_signup_by_activity_member_binding = ""
            + "    FROM member2b m "
            + "      INNER JOIN events2b e "
            + "        ON e.inactive = 0 "
            + "          AND e.date BETWEEN ? AND ? "
            + "          AND e.activity_id = ? "
            + "      INNER JOIN evntsup2b es "
            + "        ON es.name = e.name AND es.inactive = 0 "
            + "          AND es.username%% = m.username ";
    
    private static final String sql_full_golf_reservations_username = ""
            + "  SELECT t.* "
            + sql_golf_reservation_member_binding;
    
    private static final String sql_full_event_signups_username = ""
            + "  SELECT es.*, e.date as event_date, e.time as event_time "
            + sql_event_signup_member_binding;
    
    private static final String sql_full_event_signups_username_by_activity = ""
            + "  SELECT es.*, e.date as event_date, e.time as event_time "
            + sql_event_signup_by_activity_member_binding;
    
    private static final String sql_golf_reservations_username = ""
            + "  SELECT m.id as member_id, t.teecurr_id "
            + sql_golf_reservation_member_binding;
    
    private static final String sql_event_signups_username = ""
            + "  SELECT m.id as member_id, es.id as evntsup_id "
            + sql_event_signup_member_binding;
    
    private static final String sql_event_signups_username_by_activity = ""
            + "  SELECT m.id as member_id, es.id as evntsup_id "
            + sql_event_signup_by_activity_member_binding;
    
    // Reservations By Member Selection
    private final static String sql_tecurrs_by_member_id = "" // Needs 6 triplets of to-from dates and member id.
            + "SELECT * FROM ("
            + ApiCommon.buildUserSelectSql(" (",sql_full_golf_reservations_username," WHERE m.member_id = ?) ", " UNION ", 5) 
            + " UNION DISTINCT "
            + "  (SELECT t.* "
            + sql_golf_reservation_orig_binding
            + "    WHERE m.member_id = ?) "
            + ") AS mtcurrs  ";
    
    private final static String sql_event_signups_by_member_id = "" // Needs 5 triplets of to-from dates and member id.
            + "SELECT * FROM ("
            + ApiCommon.buildUserSelectSql(" (",sql_full_event_signups_username," WHERE m.member_id = ?) ", " UNION ", 5) 
            + ") AS mevntsups  ";
    
    private final static String sql_event_signups_by_activity_by_member_id = "" // Needs 5 triples of to-from dates and activity_id
            + "SELECT * FROM ("
            + ApiCommon.buildUserSelectSql(" (",sql_full_event_signups_username_by_activity," WHERE mmember_id = ?) ", " UNION ", 5) 
            + ") AS mevntsups  ";
    
    private final static String sql_dining_by_person_id = ""  // NOTE:: this is postgresql, not mysql
            + "SELECT r2.*, "
            + "    p.prefix, p.first_name, p.middle_name, p.last_name, p.suffix, e.name AS event_name, "
            + "    COALESCE(r2.parent_id,0) AS p_sort " // Float primary reservation to the top
            + "  FROM reservations AS r "
            + "    INNER JOIN reservations AS r2 "
            + "      ON r2.reservation_number = r.reservation_number "
            + "      AND r2.state <> 'cancelled' "
            + "    LEFT JOIN events AS e "
            + "      ON e.id = r.event_id "
            + "    LEFT JOIN people AS p "
            + "      ON p.person_id = r2.person_id "
            + "  WHERE r.organization_id = ? "
            + "    AND r.date BETWEEN CAST(? AS DATE) AND CAST(? AS DATE) "
            + "    AND r.state <> 'cancelled' "
            + "    AND r.person_id = ? "
            + "  ORDER BY r2.date, r2.time, r2.reservation_number, p_sort, r2.check_number ";
    
    private final static String sql_flxrez_by_activity_by_member_id = ""
            + "SELECT a.*, ap2.*, m.id as memebr_id,  m.name_last, m.name_first, m.name_mi, name_pre, name_suf, "
            + "    e.event_name, "
            + "    CONCAT_WS(', ', ac5.activity_name, ac4.activity_name, ac3.activity_name, ac2.activity_name, ac1.activity_name, a.activity_name) AS full_activity_name, "
            + "    CONCAT_WS(',', ac5.activity_id, ac4.activity_id, ac3.activity_id, ac2.activity_id, ac1.activity_id, a.activity_id) AS activity_path, "
            + "    COALESCE(ac5.activity_id, ac4.activity_id, ac3.activity_id, ac2.activity_id, ac1.activity_id, a.activity_id) AS root_activity_id "
            + "  FROM member2b AS m "
            + "    INNER JOIN activity_sheets_players ap "
            + "      ON ap.username = m.username "
            + "    INNER JOIN activity_sheets a "
            + "      ON a.sheet_id = ap.activity_sheet_id "
            + "    INNER JOIN activity_sheet_players AS ap2 "
            + "      ON ap2.activity_sheet_id = a.sheet_id "
            + "    LEFT JOIN events2b AS e "
            + "      ON e.event_id = a.event_id "
            + "    LEFT JOIN activities AS ac1 "
            + "      ON ac1.activity_id = a.activity_id "
            + "    LEFT JOIN activities AS ac2 "
            + "      ON ac2.activity_id = ac1.parent_id "
            + "    LEFT JOIN activities AS ac3 "
            + "      ON ac3.activity_id = ac2.parent_id "
            + "    LEFT JOIN activities AS ac4 "
            + "      ON ac4.activity_id = ac3.parent_id "
            + "    LEFT JOIN activities AS ac5 "
            + "      ON ac5.activity_id = ac4.parent_id "
            + "  WHERE m.member_id = ? "
            + "    AND (a.activity_id = ? OR ac1.activity_id = ? OR ac2.activity_id = ? OR ac3.activity_id = ? OR ac4.activity_id = ? OR ac5.activity_id = ?) " // Ugly temporary work-around.  Clean up later with a "ROOT ID" in each activity
            + "    AND a.report_ignore = 0 AND a.date_time BETWEEN ? AND ? " // NOTE: use "YYYY-MM-DD 00:00:00" to "YYYY-MM-DD 23:59:59"
            + "  ORDER BY a.date_time, a.sheet_id, ap2.pos ";
    
    private final static String sql_flxrez_by_member_id = ""
            + "SELECT a.*, ap2.*, m.id as memebr_id,  m.name_last, m.name_first, m.name_mi, name_pre, name_suf, "
            + "    e.event_name, "
            + "    CONCAT_WS(', ', ac5.activity_name, ac4.activity_name, ac3.activity_name, ac2.activity_name, ac1.activity_name, a.activity_name) AS full_activity_name, "
            + "    CONCAT_WS(',', ac5.activity_id, ac4.activity_id, ac3.activity_id, ac2.activity_id, ac1.activity_id, a.activity_id) AS activity_path, "
            + "    COALESCE(ac5.activity_id, ac4.activity_id, ac3.activity_id, ac2.activity_id, ac1.activity_id, a.activity_id) AS root_activity_id "
            + "  FROM member2b AS m "
            + "    INNER JOIN activity_sheets_players ap "
            + "      ON ap.username = m.username "
            + "    INNER JOIN activity_sheets a "
            + "      ON a.sheet_id = ap.activity_sheet_id "
            + "    INNER JOIN activity_sheet_players AS ap2 "
            + "      ON ap2.activity_sheet_id = a.sheet_id "
            + "    LEFT JOIN events2b AS e "
            + "      ON e.event_id = a.event_id "
            + "    LEFT JOIN activities AS ac1 "
            + "      ON ac1.activity_id = a.activity_id "
            + "    LEFT JOIN activities AS ac2 "
            + "      ON ac2.activity_id = ac1.parent_id "
            + "    LEFT JOIN activities AS ac3 "
            + "      ON ac3.activity_id = ac2.parent_id "
            + "    LEFT JOIN activities AS ac4 "
            + "      ON ac4.activity_id = ac3.parent_id "
            + "    LEFT JOIN activities AS ac5 "
            + "      ON ac5.activity_id = ac4.parent_id "
            + "  WHERE m.member_id = ? AND a.report_ignore = 0 AND a.date_time BETWEEN ? AND ? " // NOTE: use "YYYY-MM-DD 00:00:00" to "YYYY-MM-DD 23:59:59"
            + "  ORDER BY a.date_time, a.sheet_id, ap2.pos ";

    
    // VIP Selection 
    private final static String sql_vip_binding = ""
            + "    INNER JOIN partner AS p " // VIPs are partners with Activity ID of 9000
            + "      ON p.partner_id = m.username " // Bind to partner (vip) based on partner's username.  It would be great to get these converted over to member_id
            + "        AND p.activity_id = " + ProcessConstants.MANAGERS_PORTAL
            + "        AND p.user_id = ? "; // username of VIP list owner.  Would be great to get these converted over to member_id.
    
    private final static String sql_vips = "" 
            + "SELECT m.* "
            + "  FROM member2b AS m "
            + sql_vip_binding;
    
    private final static String sql_vips_with_golf_reservations = "" // Needs 6 triples of to-from dates and username of VIP list owner
            + "SELECT DISTINCT member_id FROM ("
            + ApiCommon.buildUserSelectSql(" (",sql_golf_reservations_username, sql_vip_binding+") ", " UNION ", 5) 
            + " UNION "
            + "  (SELECT m.id as member_id, teecurr_id "
            + sql_golf_reservation_orig_binding
            + sql_vip_binding
            + "  ) "
            + ") AS vips  ";
    
    private final static String sql_vips_with_event_signups = "" // Needs 5 triples of to-from dates and username of VIP list owner
            + "SELECT DISTINCT member_id FROM ("
            + ApiCommon.buildUserSelectSql(" (",sql_event_signups_username, sql_vip_binding+") ", " UNION ", 5) 
            + ") AS vips  ";
    
    private final static String sql_vips_with_event_signups_by_activity = "" // Needs 5 quads of to-from dates and activity_id and username of VIP list owner
            + "SELECT DISTINCT member_id FROM ("
            + ApiCommon.buildUserSelectSql(" (",sql_event_signups_username_by_activity, sql_vip_binding+") ", " UNION ", 5) 
            + ") AS vips  ";
    
    private final static String sql_vips_with_dining_reservations = ""  // NOTE:: this is postgresql, not mysql
            + "SELECT MAX(r.person_id) as person_id "
            + "  FROM reservations r "
            + "    LEFT OUTER JOIN events e "
            + "      ON e.id = r.event_id "
            + "  WHERE r.organization_id = ? "
            + "    AND r.date BETWEEN CAST(? AS DATE) AND CAST(? AS DATE) "
            + "    AND r.state <> 'cancelled' "
            + "    AND r.person_id IN (%%LIST%%) " // Replace %%LIST%% with list of VIP member IDs.  Yes, it's ugly.  Unless we connect posgresql to mysql using a FEDERATED table, the only other way would be to select all dining reservations and filter out the non VIPs, or query each VIP person ID individually.
            + "  GROUP BY r.person_id ";
    
    private final static String sql_vip_with_flxrez_reservations_by_activity = ""
            + "SELECT m.id AS member_id "
            + "  FROM member2b AS m "
            + sql_vip_binding
            + "    INNER JOIN activity_sheets_players ap "
            + "      ON ap.username = m.username "
            + "    INNER JOIN activity_sheets a "
            + "      ON a.sheet_id = ap.activity_sheet_id "
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
            + "  WHERE "
            + "    (a.activity_id = ? OR ac1.activity_id = ? OR ac2.activity_id = ? OR ac3.activity_id = ? OR ac4.activity_id = ? OR ac5.activity_id = ?) " // Ugly temporary work-around.  Clean up later with a "ROOT ID" in each activity
            + "    AND a.report_ignore = 0 AND a.date_time BETWEEN ? AND ? " // NOTE: use "YYYY-MM-DD 00:00:00" to "YYYY-MM-DD 23:59:59"
            + "  GROUP BY m.id ";
    
    private final static String sql_vip_with_flxrez_reservations = ""
            + "SELECT m.id AS member_id "
            + "  FROM member2b AS m "
            + sql_vip_binding
            + "    INNER JOIN activity_sheets_players ap "
            + "      ON ap.username = m.username "
            + "    INNER JOIN activity_sheets a "
            + "      ON a.sheet_id = ap.activity_sheet_id "
            + "  WHERE a.report_ignore = 0 AND a.date_time BETWEEN ? AND ? " // NOTE: use "YYYY-MM-DD 00:00:00" to "YYYY-MM-DD 23:59:59"
            + "  GROUP BY m.id ";

    public ClubMember(){}; // Empty parm
    
    public ClubMember(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public ClubMember(ResultSet rs, int load_type){
        loadFromResultSet(rs, load_type);
    }
    
    public ClubMember(String club, String username, int load_type){
        Connection con_club = ApiCommon.getConnection(club);
        loadByAccountName(username, null, con_club, load_type);
        Connect.close(con_club);
    }
    
    public ClubMember(String username, Connection con_club, int load_type){
        loadByAccountName(username, null, con_club, load_type);
    }
    
    public ClubMember(String club, String username){
        Connection con_club = ApiCommon.getConnection(club);
        loadByAccountName(username, null, con_club, ClubMember.load_full);
        Connect.close(con_club);
    }
    
    public ClubMember(String username, Connection con_club){
        loadByAccountName(username, null, con_club, ClubMember.load_full);
    }
    
    public ClubMember(long member_id, Connection con_club, int load_type){
        loadById(member_id, con_club, load_type);
    }
    
    public ClubMember(long member_id, Connection con_club){
        loadById(member_id, con_club, ClubMember.load_full);
    }
    
    public ClubMember(long member_id, long club_id){
        Connection con_club = ApiCommon.getConnection(club_id);
        loadById(member_id, con_club, ClubMember.load_full);
        Connect.close(con_club);
    }
    
    public ClubMember(long member_id, long club_id, int load_type){
        Connection con_club = ApiCommon.getConnection(club_id);
        loadById(member_id, con_club, load_type);
        Connect.close(con_club);
    }
    
    public final Long loadById(long id, Connection con_club, int load_type){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con_club.prepareStatement(sql_by_id);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = loadFromResultSet(rs, load_type);
            } else {
                last_error = String.format(ApiConstants.error_finding_by_id, classNameLower(), id);
            }  
        } catch(Exception e) {
            Connect.logError(className()+".loadById: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = String.format(ApiConstants.error_loading_by_id, classNameLower(), e.toString());
        } finally {
            Connect.close(rs, pstmt);
        }
        
        return result;
        
    }
    
    public final Long loadByDiningId(int id, Connection con_club, int load_type){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con_club.prepareStatement(sql_by_dining_id);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = loadFromResultSet(rs, load_type);
            } else {
                last_error = String.format(ApiConstants.error_finding_by_id, classNameLower(), id);
            }  
        } catch(Exception e) {
            Connect.logError(className()+".loadByDiningId: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = String.format(ApiConstants.error_loading_by_id, classNameLower(), e.toString());
        } finally {
            Connect.close(rs, pstmt);
        }
        
        return result;
        
    }
    
    public final Long loadByAccountName(String username, String password, Connection con_club){
        return loadByAccountName(username, password, con_club, ClubMember.load_full);
    }
    
    public final Long loadByAccountName(String username, String password, Connection con_club, int load_type){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
  
        try {
            if (password != null) {
                pstmt = con_club.prepareStatement(sql_by_accountcredentials);
                pstmt.setString(1, username.trim());
                pstmt.setString(1, password.trim());
            } else {
                pstmt = con_club.prepareStatement(sql_by_username);
                pstmt.setString(1, username.trim());
            }

            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = loadFromResultSet(rs, load_type);
            } else {
                last_error = "Unable to find User: " + email;
            }
        } catch (Exception e) {
            Connect.logError(className() + ".loadByAccountName: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = "Error Loading User: " + e.toString();
        } finally {
            Connect.close(rs, pstmt);
        }

        return result;
        
    }
    
    private Long loadFromResultSet(ResultSet rs){
        return loadFromResultSet(rs, ClubMember.load_full);
    }
    
    private Long loadFromResultSet(ResultSet rs, int load_type){
        
        Long result = null;
        try {
            if(load_type == ClubMember.load_none){
                result = rs.getLong("id");
            } else {
                
                // data that is allowed to display publicly
                id = rs.getLong("id");
                dining_id = rs.getLong("dining_id");
                name_last = rs.getString("name_last");
                name_first = rs.getString("name_first");
                name_mi = rs.getString("name_mi");
                club = rs.getString("club");
                
                m_ship = rs.getString("m_ship");
                m_type = rs.getString("m_type");
                email = rs.getString("email");              
                c_hancap = rs.getDouble("c_hancap");
                g_hancap = rs.getDouble("g_hancap");
                wc = rs.getString("wc");
                ghin = rs.getString("ghin");
                name_pre = rs.getString("name_pre");
                name_suf = rs.getString("name_suf");
                //vip = rs.getInt("vip");
                gender = rs.getString("gender");
                birthdate = timeUtil.getDbDate(rs.getInt("birth")); // Stored in DB as INT in YYYYMMDD.
                
                StringBuilder fn = new StringBuilder();
                
                if(name_pre != null && !name_pre.isEmpty()){
                    fn.append(name_pre);
                    fn.append(" ");
                }
                fn.append(name_first);
                fn.append(" ");
                if(name_mi != null && !name_mi.isEmpty()){
                    fn.append(name_mi);
                    fn.append(" ");
                }
                fn.append(name_last);
                if(name_suf != null && !name_suf.isEmpty()){
                    fn.append(" ");
                    fn.append(name_suf);
                }
                full_name = fn.toString();

                if (load_type == ClubMember.load_full) {
                    // Data that should only be used internally/by admins
                    username = rs.getString("username");
                    count = rs.getInt("count");
                    message = rs.getString("message");
                    emailOpt = rs.getInt("emailOpt");
                    emailOpt2 = rs.getInt("emailOpt2");
                    clubEmailOpt1 = rs.getInt("clubEmailOpt1");
                    clubEmailOpt2 = rs.getInt("clubEmailOpt2");
                    memEmailOpt1 = rs.getInt("memEmailOpt1");
                    memEmailOpt2 = rs.getInt("memEmailOpt2");
                    memNum = rs.getString("memNum");
                    locker = rs.getString("locker");
                    bag = rs.getString("bag");
                    posid = rs.getString("posid");
                    msub_type = rs.getString("msub_type");
                    email2 = rs.getString("email2");
                    phone1 = rs.getString("phone1");
                    phone2 = rs.getString("phone2");
                    custom_string = rs.getString("custom_string");
                    custom_string2 = rs.getString("custom_string2");
                    webid = rs.getString("webid");
                    email_bounced = rs.getBoolean("email_bounced");
                    email2_bounced = rs.getBoolean("email2_bounced");
                    hdcp_club_num_id = rs.getLong("hdcp_club_num_id");
                    hdcp_assoc_num_id = rs.getLong("hdcp_assoc_num_id");
                    default_tee_id = rs.getLong("default_tee_id");
                    default_holes = rs.getBoolean("default_holes");
                    displayHdcp = rs.getBoolean("displayHdcp");
                    billable = rs.getBoolean("billable");
                    last_sync_date = rs.getLong("last_sync_date");
                    pri_indicator = rs.getBoolean("pri_indicator");
                    tflag = rs.getString("tflag");
                    iCal1 = rs.getBoolean("iCal1");
                    iCal2 = rs.getBoolean("iCal2");
                    default_activity_id = rs.getInt("default_activity_id");
                    ntrp_rating = rs.getDouble("ntrp_rating");
                    usta_num = rs.getString("usta_num");
                    mobile_user = rs.getString("mobile_user");
                    mobile_count = rs.getInt("mobile_count");
                    mobile_iphone = rs.getInt("mobile_iphone");
                    old_mobile_count = rs.getInt("old_mobile_count");
                    tee_sheet_jump = rs.getBoolean("tee_sheet_jump");
                    read_login_msg = rs.getBoolean("read_login_msg");
                    sort_by = rs.getInt("sort_by");
                    flexid = rs.getString("flexid");
                    display_partner_hndcp = rs.getBoolean("display_partner_hndcp");
                    allow_mp = rs.getBoolean("allow_mp");
                    disabled = rs.getBoolean("inact"); // stored as 'inact' in DB 
                }

                result = id;
            }
        } catch(Exception e) {
            last_error = String.format(ApiConstants.error_resultset, classNameLower(), e.toString());
            Connect.logError(className()+".loadFromResultSet: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            return null;
        }
        return result;
        
    }
    /*
    public final Long save(){
        Long result = null;
        Connection con_ft = ApiCommon.getConnection();
        result = save(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final Long save(Connection con_ft){
        Long result = null;
        
        PreparedStatement pstmt = null;
        
        if(email == null || email.trim().isEmpty()){
            last_error = ApiConstants.error_empty_email;
        } else if(name == null || name.trim().isEmpty()){
            last_error = ApiConstants.error_empty_name;
        } else if(id == null){
            // Inserting new record
            try {
                if (password != null) {
                    pstmt = con_ft.prepareStatement(""
                        + "INSERT INTO users "
                        + "  (user_access_id, name, email, default_commission,"
                        + "    disabled, password) "
                        + "  VALUES"
                        + "  (?, ?, ?, ?,"
                        + "   ?, PASSWORD(?))");
                } else {
                    pstmt = con_ft.prepareStatement(""
                        + "INSERT INTO users "
                        + "  (user_access_id, name, email, default_commission,"
                        + "    disabled) "
                        + "  VALUES"
                        + "  (?, ?, ?, ?,"
                        + "   ?)");
                }
                
                int i = 1;
                pstmt.setLong(i++, user_access_id);
                pstmt.setString(i++, name.trim());
                pstmt.setString(i++, email.trim());
                pstmt.setFloat(i++, default_commission);
                pstmt.setBoolean(i++, disabled);
                if(password != null && !password.trim().isEmpty()){
                    pstmt.setString(i++, password.trim());
                }
                pstmt.executeUpdate();
                
                result = Connect.getLastInsertId(con_ft);
                
                id = result;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                if(loadByAccountName(club, email, null, con_ft, true) != null){
                    last_error = String.format(ApiConstants.error_update_duplicate_email, classNameLower(), email);
                } else {
                    Connect.logError(className()+".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_insert, classNameLower(), e.toString());
                }
            } finally {
                Connect.close(pstmt);
            }
        } else {
            // Update existing record
            try {
                if (password != null) {
                    pstmt = con_ft.prepareStatement(""
                        + "UPDATE users "
                        + " SET"
                        + "  user_access_id = ?,"
                        + "  name = ?,"
                        + "  email = ?,"
                        + "  default_commission = ?, "
                        + "  disabled = ?, "
                        + "  password = PASSWORD(?) "
                        + " WHERE id = ? ");
                } else {
                    pstmt = con_ft.prepareStatement(""
                        + "UPDATE users "
                        + " SET"
                        + "  user_access_id = ?,"
                        + "  name = ?,"
                        + "  email = ?,"
                        + "  default_commission = ?, "
                        + "  disabled = ? "
                        + " WHERE id = ? ");
                }
                
                int i = 1;
                pstmt.setLong(i++, user_access_id);
                pstmt.setString(i++, name.trim());
                pstmt.setString(i++, email.trim());
                pstmt.setFloat(i++, default_commission);
                pstmt.setBoolean(i++, disabled);
                if(password != null && !password.trim().isEmpty()){
                    pstmt.setString(i++, password.trim());
                }
                pstmt.setLong(i++, id);
                
                pstmt.executeUpdate();
                
                result = id;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                Long check_id = loadByAccountName(club, email, null, con_ft, true);
                if(check_id != null && check_id.equals(id)){
                    last_error = String.format(ApiConstants.error_update_duplicate_email, classNameLower(), email);
                } else {
                    Connect.logError(className()+".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_update, classNameLower(), e.toString());
                }
            } finally {
                Connect.close(pstmt);
            }
        }

        return result;
    }

    public final boolean delete(){
        Connection con_ft = ApiCommon.getConnection();
        boolean result = delete(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final boolean delete(Connection con_ft){
        boolean result = false;
        
        PreparedStatement pstmt = null;
        
        if(id == null){
            // Can't delete a record with a null id
            last_error = String.format(ApiConstants.error_delete_null_id, classNameLower(), className());
            
        } else {
            // Update existing record
            try {
                    pstmt = con_ft.prepareStatement(""
                        + "DELETE FROM users "
                        + " WHERE id = ? ");
                
                int i = 1;
                pstmt.setLong(i++, id);
                
                pstmt.executeUpdate();
                
                result = true;
                
            } catch(Exception e) {
                Connect.logError(className()+".delete: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                last_error = String.format(ApiConstants.error_delete, classNameLower(), e.toString());
            } finally {
                Connect.close(pstmt);
            }
        }

        return result;
    }
    */
    
    
    public static List<ClubMember> getListByBirthDate(long club_id, String start_date, String end_date, int loadType){
        Connection con_club = ApiCommon.getConnection(club_id);
        List<ClubMember> result = getListByBirthDate(con_club, start_date, end_date, loadType);
        Connect.close(con_club);
        return result;
    }
    
    public static List<ClubMember> getListByBirthDate(String club_code, String start_date, String end_date, int loadType){
        Connection con_club = ApiCommon.getConnection(club_code);
        List<ClubMember> result = getListByBirthDate(con_club, start_date, end_date, loadType);
        Connect.close(con_club);
        return result;
    }
    
    public static List<ClubMember> getListByBirthDate(Connection con_club, String start_date, String end_date, int loadType){
        
        List<ClubMember> result = new ArrayList<ClubMember>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String error = null;
        
        ClubMember r = new ClubMember();
        
        try {
            pstmt = con_club.prepareStatement(sql_by_birthdates);
            int i = 1;
            int sdate = timeUtil.getIntDateFromString(start_date);
            int edate = timeUtil.getIntDateFromString(end_date);
            pstmt.setInt(i++, sdate - (int)(Math.floor(sdate/10000) * 10000)); // Convert date from YYYYMMDD to MMDD
            pstmt.setInt(i++, edate - (int)(Math.floor(edate/10000) * 10000));
            
            rs = pstmt.executeQuery();
            while(rs.next()){
                r = new ClubMember(rs, loadType);
                if(r.id != null && r.last_error == null){
                    result.add(r);
                } else {
                    if (r.last_error != null) {
                        error = r.last_error;
                    } else {
                        error = String.format(ApiConstants.error_unknown_condition, r.classNameLower());
                    }
                }
            }  
        } catch(Exception e) {
            error = String.format(ApiConstants.error_db_select, r.classNameLower(), e.toString());
            Connect.logError(r.className() + ".getListByBirthDate: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(rs, pstmt);
        }
 
        if(error != null){
            Connect.logError(r.className() + ".getListByBirthDate: Err=" + error);
            return null;
        } else {
            return result;
        }

    }
    
      
    public static List<ClubMember> getVipsWithReservations(long club_id, long member_id, int activity_id, String start_date, String end_date, int loadType){
        Connection con_club = ApiCommon.getConnection(club_id);
        List<ClubMember> result = getVipsWithReservations(con_club, member_id, activity_id, start_date, end_date, loadType);
        Connect.close(con_club);
        return result;
    }
    
    public static List<ClubMember> getVipsWithReservations(String club_code, long member_id, int activity_id, String start_date, String end_date, int loadType){
        Connection con_club = ApiCommon.getConnection(club_code);
        List<ClubMember> result = getVipsWithReservations(con_club, member_id, activity_id, start_date, end_date, loadType);
        Connect.close(con_club);
        return result;
    }
    
    public static List<ClubMember> getVipsWithReservations(Connection con_club, long member_id, int activity_id, String start_date, String end_date, int loadType){
        
        int i;
        
        List<ClubMember> result = new ArrayList<ClubMember>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String error = null;
        
        ClubMember r = new ClubMember();
        
        ClubMember vip_list_owner = new ClubMember(member_id, con_club);
        if(vip_list_owner.last_error != null){
            error = vip_list_owner.last_error;
        }
        
        if(error == null){
            
            Set<Long> vipsWithReservations = new HashSet<Long>();

            Map<Integer, Long> person_id_to_vip_id = new HashMap<Integer, Long>();

            int sdate = timeUtil.getIntDateFromString(start_date);
            int edate = timeUtil.getIntDateFromString(end_date);

            String ssdate = timeUtil.getDbDate(sdate);
            String sedate = timeUtil.getDbDate(edate) + " 23:59:59";

            boolean do_golf = false, do_dining = false, do_all_flxrez = false, do_flxrez = false;

            switch(activity_id){

                case ProcessConstants.ALL_ACTIVITIES:
                    do_golf = true; do_dining = true; do_all_flxrez = true;
                    break;

                case ProcessConstants.DINING_ACTIVITY_ID:
                    do_dining = true;
                    break;

                case 0: // Golf
                    do_golf = true;
                    break;

                default:
                    do_flxrez = true;
                    break;

            }

            if(do_golf){

                try {

                    // Load VIPs with golf reservations
                    pstmt = con_club.prepareStatement(sql_vips_with_golf_reservations);
                    i = 1;
                    for(int t = 0; t < 6; t++){
                        pstmt.setInt(i++,sdate);
                        pstmt.setInt(i++,edate);
                        pstmt.setString(i++,vip_list_owner.username);
                    }
                    rs = pstmt.executeQuery();
                    while (rs.next()) {
                        vipsWithReservations.add(rs.getLong("member_id"));
                    }

                    // Load VIPs with golf event signups
                    if(!do_all_flxrez){ // If all, we'll just do this below
                        pstmt = con_club.prepareStatement(sql_vips_with_event_signups_by_activity);
                        i = 1;
                        for(int t = 0; t < 5; t++){
                            pstmt.setInt(i++,sdate);
                            pstmt.setInt(i++,edate);
                            pstmt.setInt(i++,0); // Only golf activities
                            pstmt.setString(i++,vip_list_owner.username);
                        }
                        rs = pstmt.executeQuery();
                        while (rs.next()) {
                            vipsWithReservations.add(rs.getLong("member_id"));
                        }
                    }

                } catch (Exception e) {
                    error = String.format(ApiConstants.error_db_select, r.classNameLower(), e.toString());
                    Connect.logError(r.className() + ".getVipsWithReservations: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                } finally {
                    Connect.close(rs, pstmt);
                }

            }

            if(do_flxrez || do_all_flxrez){

                try {

                    // Load VIPs with flxrez reservations
                    i = 1;
                    if (do_all_flxrez) {
                        pstmt = con_club.prepareStatement(sql_vip_with_flxrez_reservations);
                        pstmt.setString(i++,vip_list_owner.username);
                    } else {
                        pstmt = con_club.prepareStatement(sql_vip_with_flxrez_reservations_by_activity);
                        pstmt.setString(i++,vip_list_owner.username);
                        for (int t = 0; t < 6; t++) {
                            pstmt.setInt(i++, activity_id);
                        }
                    }
                    pstmt.setString(i++, ssdate); // from
                    pstmt.setString(i++, sedate); // to

                    rs = pstmt.executeQuery();
                    while (rs.next()) {
                        vipsWithReservations.add(rs.getLong("member_id"));
                    }

                    i = 1;
                    if (do_all_flxrez) {
                        // Load VIPs with golf or any flxrez signups
                        pstmt = con_club.prepareStatement(sql_vips_with_event_signups);
                        for (int t = 0; t < 5; t++) {
                            pstmt.setInt(i++, sdate);
                            pstmt.setInt(i++, edate);
                            pstmt.setString(i++,vip_list_owner.username);
                        }
                    } else {
                        // Load VIPs with a this flxrez signups
                        pstmt = con_club.prepareStatement(sql_vips_with_event_signups_by_activity);
                        for (int t = 0; t < 5; t++) {
                            pstmt.setInt(i++, sdate);
                            pstmt.setInt(i++, edate);
                            pstmt.setInt(i++, activity_id); // Only golf activities
                            pstmt.setString(i++,vip_list_owner.username);
                        }
                    }

                    rs = pstmt.executeQuery();
                    while (rs.next()) {
                        vipsWithReservations.add(rs.getLong("member_id"));
                    }


                } catch (Exception e) {
                    error = String.format(ApiConstants.error_db_select, r.classNameLower(), e.toString());
                    Connect.logError(r.className() + ".getVipsWithReservations: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                } finally {
                    Connect.close(rs, pstmt);
                }

            }

            if(do_dining){
                // We'll need our personID to memberID map
                long organization_id = ApiCommon.getClubOrganizationId(con_club);
                Connection con_dining = Connect.getDiningCon();
                try {

                    pstmt = con_club.prepareStatement(sql_vips);
                    pstmt.setString(1,vip_list_owner.username);
                    rs = pstmt.executeQuery();
                    while (rs.next()) {
                        person_id_to_vip_id.put(rs.getInt("dining_id"), rs.getLong("id"));
                    }

                    if (!person_id_to_vip_id.isEmpty()) {
                        List<Integer> person_ids = new ArrayList<Integer>(person_id_to_vip_id.keySet());
                        String person_id_list = StringUtils.join(person_ids, ',');

                        pstmt = con_dining.prepareStatement(sql_vips_with_dining_reservations.replace("%%LIST%%", person_id_list));
                        pstmt.setLong(1, organization_id);
                        pstmt.setString(2, ssdate); // from
                        pstmt.setString(3, sedate); // to
                        rs = pstmt.executeQuery();
                        while (rs.next()) {
                            Long vip_id = person_id_to_vip_id.get(rs.getInt("person_id"));
                            if(vip_id != null){
                                vipsWithReservations.add(vip_id);
                            }
                        }
                    }

                } catch (Exception e) {
                    error = String.format(ApiConstants.error_db_select, r.classNameLower(), e.toString());
                    Connect.logError(r.className() + ".getVipsWithReservations: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                } finally {
                    Connect.close(rs, pstmt, con_dining);
                }

            }

            if (!vipsWithReservations.isEmpty()) {

                String VIP_list = StringUtils.join(vipsWithReservations, ',');

                try {
                    pstmt = con_club.prepareStatement(sql_by_id_in.replace("%%LIST%%", VIP_list));
                    rs = pstmt.executeQuery();
                    while (rs.next()) {
                        r = new ClubMember(rs, loadType);
                        if (r.id != null && r.last_error == null) {
                            result.add(r);
                        } else {
                            if (r.last_error != null) {
                                error = r.last_error;
                            } else {
                                error = String.format(ApiConstants.error_unknown_condition, r.classNameLower());
                            }
                        }
                    }
                } catch (Exception e) {
                    error = String.format(ApiConstants.error_db_select, r.classNameLower(), e.toString());
                    Connect.logError(r.className() + ".getVipsWithReservations: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                } finally {
                    Connect.close(rs, pstmt);
                }
            }
        }
        
        if(error != null){
            Connect.logError(r.className() + ".getVipsWithReservations: Err=" + error);
            return null;
        } else {
            return result;
        }

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
