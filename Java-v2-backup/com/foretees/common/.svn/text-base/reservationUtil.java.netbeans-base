/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;

//import java.io.*;
//import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import org.joda.time.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json

/**
 *
 * @author Owner
 */
public class reservationUtil {
    
    
    public static long getTeeCurrGroupId(long date, int time, int fb, String course, Connection con){
    
        long result = 0;
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con.prepareStatement(
                    "SELECT * "
                    + " FROM teecurr2 t  "
                    + "   INNER JOIN teecurr2_group_details gd ON gd.teecurr2_id = t.teecurr_id "
                    + " WHERE t.date = ? AND t.time = ? AND t.fb = ? AND t.courseName = ? ");

            pstmt.setLong(1, date);
            pstmt.setInt(2, time);
            pstmt.setInt(3, fb);
            pstmt.setString(4, course);
            rs = pstmt.executeQuery(); 

            if(rs.next()){
                result = rs.getLong("teecurr2_group_id");
            }

            pstmt.close();

        } catch (Exception e) {
            // Error
            Utilities.logError("reservationUtil.getTeeCurrGroupId: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));

        } finally {
            Connect.close(rs, pstmt);
        }
        
        return result;
        
    }
    
    public static long getTeeCurrGroupId(long teecurr_id, Connection con){
    
        long result = 0;
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con.prepareStatement(
                    "SELECT * FROM teecurr2_group_details gd WHERE gd.teecurr2_id = ? ");

            pstmt.setLong(1, teecurr_id); 
            rs = pstmt.executeQuery(); 

            if(rs.next()){
                result = rs.getLong("teecurr2_group_id");
            }

            pstmt.close();

        } catch (Exception e) {
            // Error
            Utilities.logError("reservationUtil.getTeeCurrGroupId: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));

        } finally {
            Connect.close(rs, pstmt);
        }
        
        return result;
        
    }
    
    public static List<teeCurrGroupDetail> getTeeCurrGroupDetails(long date, int time, int fb, String course, Connection con){

        return getTeeCurrGroupDetailsByGroupId(getTeeCurrGroupId(date, time, fb, course, con), con);
        
    }
    
    public static List<teeCurrGroupDetail> getTeeCurrGroupDetails(long teecurr_id, Connection con){

        return getTeeCurrGroupDetailsByGroupId(getTeeCurrGroupId(teecurr_id, con), con);
        
    }
    
    public static List<teeCurrGroupDetail> getTeeCurrGroupDetailsByGroupId(long teecurr_group_id, Connection con){
        
        List<teeCurrGroupDetail> result = new ArrayList<teeCurrGroupDetail>();
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con.prepareStatement(
                    "SELECT gd.*, g.member_id, m.username, t.coursename, t.date, t.time, t.fb "
                    + " FROM teecurr2_group_details gd"
                    + "   INNER JOIN teecurr2 t ON t.teecurr_id = gd.teecurr2_id "
                    + "   INNER JOIN teecurr2_groups g ON g.id = gd.teecurr2_group_id "
                    + "   INNER JOIN member2b m ON m.id = g.member_id "
                    + " WHERE gd.teecurr2_group_id = ? "
                    + " ORDER BY t.date, t.time, t.coursename, t.fb ");

            pstmt.setLong(1, teecurr_group_id); 
            rs = pstmt.executeQuery(); 

            while(rs.next()){
                teeCurrGroupDetail detail = new teeCurrGroupDetail(rs.getLong("id"), rs.getLong("teecurr2_id"), 
                        rs.getLong("teecurr2_group_id"), rs.getLong("member_id"), rs.getString("username"), 
                        rs.getLong("date"), rs.getInt("time"), rs.getInt("fb"), rs.getString("coursename"));
                result.add(detail);
            }

            pstmt.close();

        } catch (Exception e) {
            // Error
            Utilities.logError("reservationUtil.getTeeCurrGroupDetailsByGroupId: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));

        } finally {
            Connect.close(rs, pstmt);
        }
        
        return result;
        
    }

    public static boolean hideNames(HttpServletRequest req, String eventName, int activity_id, boolean defaultMode) {

        String club = Utilities.getSessionString(req, "club", "");
        String user = Utilities.getSessionString(req, "user", "");

        boolean result = false;
        boolean isOlyClubWGN = false;

        if (!club.equals("deserthighlands")
                && !club.equals("blackstone")
                && !club.equals("alisoviejo")) { // ignore preset, default use false as default 

            result = defaultMode;
        }


        //
        //  Custom for CC of Virginia - hide the names on the 2010 Mens Member Guest event
        //
        if (club.equals("virginiacc") && eventName.equals("2010 Mens Member Guest")) {

            result = true;
        }

        if (club.equals("olyclub")) {
            Connection con = Connect.getCon(req);
            ArrayList<Integer> temp_category_ids = new ArrayList<Integer>();
            temp_category_ids.add(1);
            
            if (Utilities.checkEventCategoryBindings(Utilities.getEventIdFromName(eventName,con), temp_category_ids, con).equals("")) {   //if event is not a WGN event set false
                result = false;
            } else {
                result = true;            
            }
        }

        if (club.equals("merion") && (eventName.equalsIgnoreCase("Fall 2 Day Member Guest")
                || eventName.equalsIgnoreCase("Fall 3 Day Member Guest")
                || eventName.equalsIgnoreCase("2 Day Member Guest")
                || eventName.equalsIgnoreCase("3 Day Member Guest")
                || eventName.equalsIgnoreCase("Stag Day June")
                || eventName.equalsIgnoreCase("Stag Day July")
                || eventName.equalsIgnoreCase("Stag Day May")
                || eventName.equalsIgnoreCase("Stag Day Oct")
                || eventName.equalsIgnoreCase("Stag Day Sep")
                || eventName.equalsIgnoreCase("Super Stag Day Aug"))) {

            result = true;
        }

        if (club.equals("beechmontcc") && eventName.equalsIgnoreCase("Member Guest Invitational")) {
            result = true;
        }

        // Don't hide member names for "BGC Ladies" events for Baltusrol GC
        if (club.equals("baltusrolgc") && eventName.startsWith("BGC")) {
            result = false;
        }
        
        if (club.equals("martiscamp") && activity_id != 0 && activity_id != ProcessConstants.DINING_ACTIVITY_ID) {
            result = true;
        }

        return result;
    }

    public static Map<String, String> eventDetail(String label, Integer value) {
        return eventDetail(label, value.toString(), null);
    }

    public static Map<String, String> eventDetail(String label, String value) {
        return eventDetail(label, value, null);
    }

    public static Map<String, String> eventDetail(String label, Integer value, String sclass) {
        return eventDetail(label, value.toString(), sclass);
    }

    public static Map<String, String> eventDetail(String label, String value, String sclass) {
        Map<String, String> result = new HashMap<String, String>();
        result.put("label", label);
        result.put("value", value);
        if (sclass != null && !sclass.isEmpty()) {
            result.put("class", sclass);
        }
        return result;
    }
    
    public static Map<String, Object> linkData(Object data, String type, int activity_id) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("type", type);
        String data_field = "event_id";
        String modal = "eventPrompt";
        if(type.equalsIgnoreCase("dining_reservation_list")){
            data_field = "date";
            modal = "eventList";
        }
        result.put(data_field, data);
        result.put("modal", modal);
        return result;
    }
    
    public static String linkJson(Object data, String type, int activity_id) {;
        Gson gson_obj = new Gson();
        return gson_obj.toJson(linkData(data, type, activity_id));
    }
    
    public static String linkJsonEsc(Object data, String type, int activity_id) {;
        return StringEscapeUtils.escapeHtml(linkJson(data, type, activity_id));
    }
    
    public static Map<String, Object> linkData(int event_id, int activity_id) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("type", activity_id == ProcessConstants.DINING_ACTIVITY_ID?"dining_event":"event");
        result.put("event_id", event_id);
        return result;
    }
    
    public static String linkJson(int event_id, int activity_id) {;
        Gson gson_obj = new Gson();
        return gson_obj.toJson(linkData(event_id, activity_id));
    }
    
    public static String linkJsonEsc(int event_id, int activity_id) {;
        return StringEscapeUtils.escapeHtml(linkJson(event_id, activity_id));
    }
    
    public static String getPlayerHtml(List<reservationPlayer> players, boolean show_status, HttpServletRequest req) {
        
        boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
        String user = reqUtil.getSessionString(req, "user", reqUtil.getSessionString(req, "ext-user", ""));
        
        htmlTags tags = new htmlTags(rwd);

        StringBuilder playerHtml = new StringBuilder();

        for(reservationPlayer player : players){
            StringBuilder playerName = new StringBuilder();
            playerName.append(tags.getTag("span", player.name));
            if(player.cw != null && !player.cw.isEmpty()){
                playerName.append(tags.getTag("span", player.cw));
            }
            if (show_status && player.status != null && !player.status.isEmpty()){
                playerName.append(tags.getTag("span", player.status));
            }
            playerHtml.append(tags.getTag("div", playerName.toString(), user.equalsIgnoreCase(player.user)?"me":""));
        }

        return playerHtml.toString();
    }

    

    public static reservationPrompt getEventPrompt(HttpServletRequest req, int event_id, String user) {

        String club = Utilities.getSessionString(req, "club", "");
        String mtype = Utilities.getSessionString(req, "mtype", "");
        String mship = Utilities.getSessionString(req, "mship", "");

        String name;
        String format;
        String pairings;
        String memcost;
        String gstcost;
        String itin;
        String course;

        String[] player = new String[5];
        String[] euser = new String[5];
        String[] cw = new String[5];
        String[] othera1 = new String[5];// Used for a custom that relies on answers to Other Question #1
        String fb = "";
        String course_display = "";
        String team_player_status = "";

        int activity_id;
        int hndcpOpt = 0;

        int type = 0;
        int holes = 0;

        int size = 0;
        int minsize = 0;
        int max = 0;
        int guests = 0;

        int c_time = 0;
        int act_time = 0;

        int count_reg = 0;
        int count_wait = 0;
        int teams_reg = 0;
        int teams_wait = 0;
        int i = 0;
        int full = 0;
        int id = 0;

        int in_use = 0;
        int wait = 0;
        int signup = 0;
        int sutime = 0;

        int hideNames = 0;
        int hideN = 0;
        int[] guest = new int[5];
        int gender = 0;
        int season = 0;

        int ask_otherA1 = 0;
        int row_count = 0;
        int select_count = 0;
        int moved = 0;
        int custom_event_count_1 = 0;
        int custom_event_count_2 = 0;
        int custom_event_category_id_1 = 0;
        int custom_event_category_id_2 = 0;
        int custom_event_category_id_3 = 0;
        int custom_selected_event_id = 0;
        int event_activity_id = 0;

        int date = 0;
        int c_date = 0;
        int sudate = 0;

        float[] hndcpa = new float[5];

        boolean disp_hndcp = true;
        boolean viewList = false; // default to not being able to see the event signups

        boolean show_itinerary = true;
        boolean use_signup_button = true;
        boolean use_signup_list = true;
        boolean all_players_empty = true;
        boolean in_this_group = false;
        boolean has_hidden_mem = false;

        PreparedStatement stmt = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        reservationPrompt event = new reservationPrompt();

        List<String> instructions = new ArrayList<String>();
        List<String> prompt_instructions = new ArrayList<String>();
        List<String> list_instructions = new ArrayList<String>();
        Map<String, String> hidden_mems = Utilities.getHiddenMems(req);

        Connection con = Connect.getCon(req);

        //
        //   Get current date and time (adjusted for time zone)
        //
        //today = SystemUtils.getDate(con);
        int today = timeUtil.getClubDate(req);

        int currtime = timeUtil.getClubTime(req);
        
        String user_mNum = Utilities.getmNum(user, con);

        try {

            //
            //   get the event requested
            //
            stmt = con.prepareStatement(""
                    + "SELECT e.*,"
                    + "  SUM("
                    + "   (IF(LENGTH(es.player1)>0,1,0)"
                    + "   +IF(LENGTH(es.player2)>0,1,0)"
                    + "   +IF(LENGTH(es.player3)>0,1,0)"
                    + "   +IF(LENGTH(es.player4)>0,1,0)"
                    + "   +IF(LENGTH(es.player5)>0,1,0)) * IF(es.wait=0,1,0)) AS reg_count, "
                    + "  SUM("
                    + "   (IF(LENGTH(es.player1)>0,1,0)"
                    + "   +IF(LENGTH(es.player2)>0,1,0)"
                    + "   +IF(LENGTH(es.player3)>0,1,0)"
                    + "   +IF(LENGTH(es.player4)>0,1,0)"
                    + "   +IF(LENGTH(es.player5)>0,1,0)) * IF(es.wait>0,1,0)) AS wait_count, "
                    + "  COUNT("
                    + "   IF(((IF(LENGTH(es.player1)>0,1,0)"
                    + "   +IF(LENGTH(es.player2)>0,1,0)"
                    + "   +IF(LENGTH(es.player3)>0,1,0)"
                    + "   +IF(LENGTH(es.player4)>0,1,0)"
                    + "   +IF(LENGTH(es.player5)>0,1,0)) * IF(es.wait=0,1,0))>0,1,NULL)) AS reg_teams, "
                    + "  COUNT("
                    + "   IF(((IF(LENGTH(es.player1)>0,1,0)"
                    + "   +IF(LENGTH(es.player2)>0,1,0)"
                    + "   +IF(LENGTH(es.player3)>0,1,0)"
                    + "   +IF(LENGTH(es.player4)>0,1,0)"
                    + "   +IF(LENGTH(es.player5)>0,1,0)) * IF(es.wait>0,1,0))>0,1,NULL)) AS wait_teams "
                    + " FROM events2b e "
                    + "  LEFT OUTER JOIN evntsup2b AS es "
                    + "   ON es.inactive = 0 "
                    + "    AND es.name = e.name "
                    + "    AND es.courseName = e.courseName "
                    + " WHERE e.event_id = ? "
                    + " GROUP BY e.event_id ");

            stmt.clearParameters();        // clear the parms
            stmt.setInt(1, event_id);
            rs = stmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

                //
                // Get the Multiple Course Option, guest types, days in advance and time for advance from the club db
                //

                event_activity_id = rs.getInt("activity_id");
                activity_id = event_activity_id;
                String activity_name;

                if (event_activity_id == 0) {
                    activity_name = "Golf";
                } else {
                    activity_name = getActivity.getActivityName(event_activity_id, con);
                }

                //
                //  parm block to hold the club parameters
                //
                parmClub parm = new parmClub(activity_id, con);

                String[] mshipA = new String[parm.MAX_Mems + 1];            // Mem Types
                String[] mtypeA = new String[parm.MAX_Mships + 1];          // Mship Types

                getClub.getParms(con, parm, activity_id);        // get the club parms

                date = rs.getInt("date");
                type = rs.getInt("type");
                act_time = timeUtil.buildIntTime(rs.getInt("act_hr"), rs.getInt("act_min"));
                course = rs.getString("courseName");
                signup = rs.getInt("signUp");
                format = rs.getString("format");
                pairings = rs.getString("pairings");
                size = rs.getInt("size");
                minsize = rs.getInt("minsize");
                max = rs.getInt("max");
                guests = rs.getInt("guests");
                memcost = rs.getString("memcost");
                gstcost = rs.getString("gstcost");

                c_date = rs.getInt("c_date");
                c_time = rs.getInt("c_time");
                itin = rs.getString("itin");
                holes = rs.getInt("holes");
                sudate = rs.getInt("su_date");
                sutime = rs.getInt("su_time");
                fb = rs.getString("fb");
                gender = rs.getInt("gender");
                season = rs.getInt("season");
                name = rs.getString("name");
                ask_otherA1 = rs.getInt("ask_otherA1");
                for (i = 1; i < parm.MAX_Mems + 1; i++) {
                    mtypeA[i] = rs.getString("mem" + i);
                }
                for (i = 1; i < parm.MAX_Mships + 1; i++) {
                    mshipA[i] = rs.getString("mship" + i);
                }

                count_reg = rs.getInt("reg_count");
                count_wait = rs.getInt("wait_count");
                teams_reg = rs.getInt("reg_teams");
                teams_wait = rs.getInt("wait_teams");

                hndcpOpt = parm.hndcpMemEvent;
                hideNames = hideNames(req, name, activity_id, parm.hiden == 1) ? 1 : 0; // Hide names?

                //
                //  Check if club wants to display handicaps
                //
                if (hndcpOpt == 0) {
                    disp_hndcp = false;      // if NO
                }
                
                


                //
                // Define message to display later
                //
                String reason_msg = "Sorry, you cannot register for this event at this time."; // default msg

                //
                //  Check if member can View the signup list for this event
                //
                if (signup == 0) {

                    if (club.equals("rollinghillsgc")) {
                        reason_msg = "Sorry, online sign-up is not available for this event.<br>Please contact tournaments@arabiangolf.com for assistance.";

                    } else if (club.equalsIgnoreCase("Sonnenalp")) {
                        reason_msg = "";

                    } else {
                        reason_msg = "Sorry, online sign-up is not available for this event.<br>Please contact your " + activity_name + " shop for assistance.";
                    }

                } else if (today >= sudate && today <= date) { // if on or after singup date and before or on day of event

                    // allow members to view signups
                    viewList = true;

                }


                // reason_msg = "The sign-up cutoff date has already passed.  You can no longer sign-up for this event.";

                //
                //  Check if member can signup for this event
                //
                if (signup > 0) {

                    if (today > c_date || (today == c_date && currtime > c_time)) {           // if after signup date

                        // reason_msg = "Sorry, the sign-up cutoff date has already passed.<br>You can no longer sign-up for this event.";
                        if (club.equals("rollinghillsgc")) {
                            reason_msg = "Sorry, the online registration deadline for this event has passed.<BR>Please contact tournaments@arabiangolf.com to check availability.";
                        } else {
                            reason_msg = "Sorry, the online registration deadline for this event has passed.<BR>Please contact the " + activity_name + " shop to check availability.";
                        }
                        signup = 0;

                    } else if (today < sudate || (today == sudate && currtime < sutime)) {    // if before signup dates

                        if (club.equals("rollinghillsgc")) {
                            reason_msg = "Sorry, online registration is not yet available for this event.<BR>Please contact tournaments@arabiangolf.com if you have questions.";
                        } else {
                            reason_msg = "Sorry, online registration is not yet available for this event.<BR>Please contact the " + activity_name + " shop if you have questions.";
                        }
                        signup = 0;

                    }

                    if (signup > 0) {            // if signup still ok, check mtype restrictions

                        i = 1;
                        loopr1:
                        while (i < parm.MAX_Mems + 1) {

                            if (mtype.equals(mtypeA[i])) {       // is this member restricted?

                                signup = 0;                         // no signup
                                reason_msg = "Sorry, your member type restricts you from playing in this event.";
                                break loopr1;                       // exit loop
                            }
                            i++;
                        }
                    }

                    if (signup > 0) {            // if signup still ok, check mship restrictions

                        i = 1;
                        loopr2:
                        while (i < parm.MAX_Mships + 1) {

                            if (mship.equals(mshipA[i])) {       // is this member restricted?

                                signup = 0;                         // no signup
                                reason_msg = "Sorry, your membership type restricts you from playing in this event.";
                                break loopr2;                       // exit loop
                            }
                            i++;
                        }
                    }

                    if (activity_id == 0 && signup > 0 && club.equals("hazeltine")) {     // if Hazeltine & signup still ok, check event name and member sub-type

                        if (name.equals("Mens Invitational")) {        // if Invitational, then only members that played last year can signup

                            String restPlayer = verifyCustom.checkHazeltineInvite(user, "", "", "", "", con);    // check if user is Invite Priority sub-type

                            if (!restPlayer.equals("")) {         // if NOT Invite Priority - no signup

                                signup = 0;
                                reason_msg = "Sorry, you are unable to register for this event.<BR>Please contact the " + activity_name + " shop if you have questions.";
                            }
                        }
                    }           // end of IF hazeltine

                }

                if (club.equals("foresthighlands")) {      // no reason_msg, if Forest Highlands
                    reason_msg = "";
                }

                //
                //  Override team size if proshop pairings (just in case)
                //
                if (!pairings.equalsIgnoreCase("Member")) {

                    size = 1;       // set size to one for proshop pairings (size is # per team)
                }

                // Override course name, if needed
                course_display = course;
                //if (!course.equals("")) {
                //    if (club.equals("congressional")) {
                //        course_display = congressionalCustom.getFullCourseName(date, day, course);
                //    }
                //}

                // Should we show itinerary?
                if (club.equals("inverness") && signup == 0) { // do not display itin if Inverness and no Signup
                    show_itinerary = false;
                }

                // Create rows
                //List<Map<String, Object>> signupList = new ArrayList<Map<String, Object>>();
                int my_signup_id = 0;
                boolean in_event = false;

                if (signup != 0 || viewList) {
                    //
                    //  Get all entries for this Event Sign Up Sheet
                    //
                    PreparedStatement pstmte = con.prepareStatement(""
                            + "SELECT es.*, "
                            + "   CONCAT_WS(', ', m1.name_last, CONCAT_WS(' ',m1.name_first, IF(m1.name_mi='',NULL,m1.name_mi))) AS alphaN1, "
                            + "   CONCAT_WS(', ', m2.name_last, CONCAT_WS(' ',m2.name_first, IF(m2.name_mi='',NULL,m2.name_mi))) AS alphaN2, "
                            + "   CONCAT_WS(', ', m3.name_last, CONCAT_WS(' ',m3.name_first, IF(m3.name_mi='',NULL,m3.name_mi))) AS alphaN3, "
                            + "   CONCAT_WS(', ', m4.name_last, CONCAT_WS(' ',m4.name_first, IF(m4.name_mi='',NULL,m4.name_mi))) AS alphaN4, "
                            + "   CONCAT_WS(', ', m5.name_last, CONCAT_WS(' ',m5.name_first, IF(m5.name_mi='',NULL,m5.name_mi))) AS alphaN5 "
                            + " FROM evntsup2b es "
                            + "  LEFT OUTER JOIN member2b m1 "
                            + "   ON m1.username = es.username1 "
                            + "  LEFT OUTER JOIN member2b m2 "
                            + "   ON m2.username = es.username2 "
                            + "  LEFT OUTER JOIN member2b m3 "
                            + "   ON m3.username = es.username3 "
                            + "  LEFT OUTER JOIN member2b m4 "
                            + "   ON m4.username = es.username4 "
                            + "  LEFT OUTER JOIN member2b m5 "
                            + "   ON m5.username = es.username5 "
                            + " WHERE es.name = ? "
                            + "  AND es.courseName = ? "
                            + "  AND es.inactive = 0 "
                            + " ORDER BY wait, r_date, r_time ");

                    pstmte.clearParameters();        // clear the parms
                    pstmte.setString(1, name);
                    pstmte.setString(2, course);
                    rs2 = pstmte.executeQuery();      // execute the prepared pstmt

                    row_count = 0;
                    select_count = 0;

                    while (rs2.next()) {

                        String[] alphaNames = new String[]{rs2.getString("alphaN1"), rs2.getString("alphaN2"), rs2.getString("alphaN3"), rs2.getString("alphaN4"), rs2.getString("alphaN5")};
                        player[0] = rs2.getString("player1");
                        player[1] = rs2.getString("player2");
                        player[2] = rs2.getString("player3");
                        player[3] = rs2.getString("player4");
                        player[4] = rs2.getString("player5");
                        euser[0] = rs2.getString("username1");
                        euser[1] = rs2.getString("username2");
                        euser[2] = rs2.getString("username3");
                        euser[3] = rs2.getString("username4");
                        euser[4] = rs2.getString("username5");
                        cw[0] = rs2.getString("p1cw");
                        cw[1] = rs2.getString("p2cw");
                        cw[2] = rs2.getString("p3cw");
                        cw[3] = rs2.getString("p4cw");
                        cw[4] = rs2.getString("p5cw");
                        in_use = rs2.getInt("in_use");
                        hndcpa[0] = rs2.getFloat("hndcp1");
                        hndcpa[1] = rs2.getFloat("hndcp2");
                        hndcpa[2] = rs2.getFloat("hndcp3");
                        hndcpa[3] = rs2.getFloat("hndcp4");
                        hndcpa[4] = rs2.getFloat("hndcp5");
                        id = rs2.getInt("id");
                        wait = rs2.getInt("wait");                 // wait list indicator
                        moved = rs2.getInt("moved");               // moved to tee sheet indicator

                        //if (ask_otherA1 == 1) {
                        othera1[0] = rs2.getString("other1a1");
                        othera1[1] = rs2.getString("other2a1");
                        othera1[2] = rs2.getString("other3a1");
                        othera1[3] = rs2.getString("other4a1");
                        othera1[4] = rs2.getString("other5a1");
                        //}
                        //
                        //  set up some fields needed for the table
                        //
                        all_players_empty = true;
                        in_this_group = false;
                        has_hidden_mem = false;
                        hideN = 0;                   // default to 'do not hide names'
                        full = 0;

                        // Check if current user is in this group, and init guest indicators
                        for (int i2 = 0; i2 < euser.length; i2++) {

                            guest[i2] = 0;                                 // By default, players are not guests

                            if (user.equalsIgnoreCase(euser[i2])) {
                                in_this_group = true;                       // Current user is in this group
                                if (moved == 0) {
                                    my_signup_id = id; // Set the signup id for this user
                                }
                                in_event = true;
                            }
                            
                            if (!in_this_group && hidden_mems.containsKey(euser[i2]) && (!hidden_mems.get(euser[i2]).equalsIgnoreCase(user_mNum) || user_mNum.equals(""))) {
                                has_hidden_mem = true;
                            }
                        }

                        // Skip all other signups if the current member isn't a part of them
                        if (club.equals("walpolecc") && name.equalsIgnoreCase("Mens October Member Guest") && !in_this_group) {
                            continue;
                        }

                        //
                        //  Custom to check if any dependents of this member are in the event signup - if so, allow member to access it
                        //
                        if (in_this_group == false && ((club.equals("denvercc") || club.startsWith("demo")) && !mtype.equalsIgnoreCase("Dependent"))
                                || (club.equals("edina") && (mtype.startsWith("Adult") || mtype.startsWith("Pre-Leg")))) {  // Custom to check for Dependents registered

                            //  Get each dependent and check if they are registered

                            boolean foundDep = false;
                            String mNumD = "";
                            PreparedStatement pstmtDenver = null;
                            ResultSet rsDenver = null;

                            String mtypeString = "m_type = 'Dependent'";      // for Denver

                            if (club.equals("edina")) {

                                mtypeString = "(m_type = 'Qualified Junior' OR m_type = 'Jr Male' OR m_type = 'Jr Female')";  // for Edina
                            }

                            try {

                                pstmtDenver = con.prepareStatement(
                                        "SELECT memNum "
                                        + "FROM member2b WHERE username = ?");      // get this member's mNum

                                pstmtDenver.clearParameters();
                                pstmtDenver.setString(1, user);
                                rsDenver = pstmtDenver.executeQuery();

                                if (rsDenver.next()) {

                                    mNumD = rsDenver.getString("memNum");
                                }
                                pstmtDenver.close();

                                if (!mNumD.equals("")) {        // if member number found for this member

                                    //  Locate any Dependents for this member and check if they are part of this event registration

                                    pstmtDenver = con.prepareStatement(
                                            "SELECT username "
                                            + "FROM member2b WHERE memNum = ? AND " + mtypeString);

                                    pstmtDenver.clearParameters();
                                    pstmtDenver.setString(1, mNumD);
                                    rsDenver = pstmtDenver.executeQuery();

                                    loopDenver:
                                    while (rsDenver.next()) {

                                        String userD = rsDenver.getString("username");   // get the dependent's username

                                        // Check if current user is in this group, and init guest indicators
                                        for (int i2 = 0; i2 < euser.length; i2++) {

                                            if (userD.equalsIgnoreCase(euser[i2])) {

                                                in_this_group = true;  // Current user is in this group
                                                foundDep = true;       // dependent found
                                                break;
                                            }
                                        }

                                        if (foundDep) {
                                            break loopDenver;    // stop looking if already found
                                        }
                                    }   // end of WHILE dependents

                                    pstmtDenver.close();

                                }       // end of IF mNumD

                            } catch (Exception e9) {

                                Utilities.logError("reservationUtil.getEventPrompt: Error processing events (custom) for " + club + ", User: " + user + ", Error: " + e9.getMessage());

                            } finally {

                                try {
                                    if (rsDenver != null) {
                                        rsDenver.close();
                                    }
                                } catch (SQLException ignored) {
                                }

                                try {
                                    if (pstmtDenver != null) {
                                        pstmtDenver.close();
                                    }
                                } catch (SQLException ignored) {
                                }
                            }       // end of try - catch - finally
                        }           // end of custom


                        // Check players/guests
                        for (int i2 = 0; i2 < player.length; i2++) {
                            if (!(player[i2].equals(""))) {
                                all_players_empty = false;
                                //
                                // Hide Names Feature - if club opts to hide the member names, then hide all names
                                // except for any group that this user is part of.
                                //
                                if ((hideNames > 0 || has_hidden_mem) && (!in_this_group)) {
                                    hideN = 1;
                                    if ((!player[i2].equalsIgnoreCase("x")) && (euser[i2].equals(""))) {
                                        guest[i2] = 1;  // this player is a guest
                                    }
                                }
                                //
                                // determine if slot is full and this user is not on it
                                //
                                if (((i2 + 1) == size) && (!in_this_group)) {
                                    full = 1;
                                }
                            } else {
                                cw[i2] = "";
                            }
                        }

                        //
                        //  Denver CC - if Juniors or Fitness activities, do not allow others to join a partial group
                        //
                        if (club.equals("denvercc") && full == 0 && in_this_group == false && (activity_id == 2 || activity_id == 3)) {

                            full = 1;         // act like team is full
                        }



                        //
                        //   skip this entry if all players are null (someone cancelled) or Rolling Hills - SA and wait listed
                        //
                        if ((!all_players_empty) && (!club.equals("rollinghillsgc") || wait == 0)) {

                            reservationSignup eSignup = new reservationSignup();
                            event.signup_list.add(eSignup);

                            if (in_use == 0) {
                                if (moved == 0) {
                                    if (full == 0 && hideN == 0 && signup > 0 && (!club.equals("merion") || use_signup_button || in_this_group)) {
                                        eSignup.select_button = "Select";
                                        eSignup.id =  id;
                                        select_count++; // record how many groups we can select
                                    } else if (full > 0) {
                                        eSignup.select_button = "Full";
                                    } else {
                                        eSignup.select_button = "Closed"; // Unavailable?
                                    }

                                } else {
                                    eSignup.select_button = "Moved";
                                }
                            } else {
                                eSignup.select_button = "Busy";
                            }


                            //
                            //  add status
                            //
                            if (wait == 0) {
                                if (club.equals("olyclub") || (club.equals("denvercc") && activity_id == 2)) {
                                    eSignup.status = "Received";
                                } else {
                                    eSignup.status = "Registered";
                                }
                            } else {
                                eSignup.status = "Wait List";
                            }

                            //
                            //  Add Players
                            //

                            //List<eventPlayer> playerList = new ArrayList<eventPlayer>();
                            //signupMap.put("players", playerList);
                            //eventPlayer ePlayer = new eventPlayer();
                            for (int i2 = 0; i2 < size; i2++) {
                                // Add player
                                String alphaName = null, playerStatus = null;

                                String playerName = player[i2];
                                if (player[i2].equalsIgnoreCase("x")) {   // if 'x'
                                    playerName = "X";
                                    alphaName = playerName;
                                } else if (!player[i2].isEmpty()) {
                                    if (hideN == 0) {             // if ok to display names
                                        if (disp_hndcp) {
                                            if ((hndcpa[i2] != 99) && (hndcpa[i2] != -99)) {
                                                if (hndcpa[i2] <= 0) {
                                                    hndcpa[i2] = 0 - hndcpa[i2]; // convert to non-negative
                                                }
                                                playerStatus = Float.toString(hndcpa[i2]);
                                            }
                                        }
                                        if (!euser[i2].isEmpty()) {
                                            // Member
                                            alphaName = alphaNames[i2];
                                        } else {
                                            // Guest
                                            alphaName = player[i2];
                                        }
                                    } else {                        // do not display member names
                                        if (euser[i2].isEmpty()) {       // if guest
                                            playerName = "Guest";
                                        } else {                    // must be a member
                                            playerName = "Member";
                                        }
                                        alphaName = playerName;
                                    }

                                }
                                // Add ramseycountryclub custom "Arrival Time"
                                if (club.equals("ramseycountryclub") && activity_id == 1 && ask_otherA1 == 1 && !othera1[i2].isEmpty()) {
                                    playerStatus = "Arrival:" + othera1[i2];
                                }
                                if (!playerName.isEmpty()) {
                                    if(playerStatus != null && !playerStatus.isEmpty()){
                                        eSignup.show_player_status = true;
                                    }
                                    if(euser[i2].equalsIgnoreCase(user)){
                                        eSignup.in_signup = true;
                                    }
                                    eSignup.players.add(new reservationPlayer(
                                            euser[i2],
                                            playerName,
                                            alphaName,
                                            playerStatus,
                                            (activity_id == 0 && season == 0 ? cw[i2] : null)));
                                }
                            }
                            row_count++;
                        }
                    }

                    pstmte.close();

                }

                use_signup_button = true;

                if (club.equals("merion")) {

//                    ****Custom for 2015 Season****
//
//                    *Stag Day Events*
//
//                    Stag Day May
//                    Stag Day June
//                    Stag Day July
//                    Super Stag Day Aug
//                    Stag Day Oct
//
//                    On March 1st, all members will be able to sign up for one Stag Day event, of the 5 scheduled (1 of 5).
//                    On April 1st, all members may sign up for 1 additional Stag Day (2 of 5).
//                    On May 1st, all members may sign up for 1 additional Stag Day (3 of 5).
//                    On June 1st, all members will be able to register for any available Stag Day (5 of 5).

                    
                    ArrayList<Integer> category_ids_1 = new ArrayList<Integer>();
                    ArrayList<Integer> category_ids_2 = new ArrayList<Integer>();
                    ArrayList<Integer> category_ids_3 = new ArrayList<Integer>();
                    
                    custom_event_category_id_1 = 1;
                    custom_event_category_id_2 = 3;
                    custom_event_category_id_3 = 4;

                    category_ids_1.add(custom_event_category_id_1);
                    category_ids_2.add(custom_event_category_id_2);
                    category_ids_3.add(custom_event_category_id_3);
                    
                    // See if the event is part of the first event category for "Stag Day" events
                    if (Utilities.checkEventCategoryBindings(custom_selected_event_id, category_ids_1, con).equals("")) {

                        custom_event_count_1 = verifyCustom.checkEventCategoryCounts(user, club, custom_event_category_id_1, con);
                        
                        if ((today < 20150401 && custom_event_count_1 >= 1)
                                || (today >= 20150401 && today < 20150501 && custom_event_count_1 >= 2)
                                || (today >= 20150501 && today < 20150601 && custom_event_count_1 >= 3)) {

                            if (today < 20150401) {
                                instructions.add("You are already signed up for the maximum allowed Mens Stag Day events at this time (1).");
                                instructions.add("You will be able to sign up for an additional Mens Stag Day event on April 1st.");
                            } else if (today >= 20150401 && today < 20150501) {
                                instructions.add("You are already signed up for the maximum allowed Mens Stag Day events at this time (2).");
                                instructions.add("You will be able to sign up for an additional Mens Stag Day event on May 1st.");
                            } else if (today >= 20150501 && today < 20150601) {
                                instructions.add("You are already signed up for the maximum allowed Mens Stag Day events at this time (3).");
                                instructions.add("You will be able to sign up for all remaining Mens Stag Day events on June 1st.");
                            }

                            use_signup_button = false;
                        }
                    }

//                    ****Custom for 2015 Season****
//                    
//                    *Member-Guest Events*
//
//                    2 Day Member Guest (June Member Guest)
//                    3 Day Member Guest (June Member Guest)
//                    Fall 3 Day Member Guest (Sept. Member Guest)
//                    Fall 2 Day Member Guest (Sept. Member Guest)
//
//                    On March 1st, all members will be able to sign up for the June Member-Guest events.
//                    On June 10th, registration for our September Member-Guest will open for any members who did not play in the June Member-Guest.
//                    On June 21st, registration for our September Member-Guest will be open to all members.
                    
                    
                    // See if the event is part of the event category for "Member/Guest" events
                    if (Utilities.checkEventCategoryBindings(custom_selected_event_id, category_ids_3, con).equals("")) {

                        custom_event_count_2 = verifyCustom.checkEventCategoryCounts(user, club, custom_event_category_id_2, con);
                        
                        if (today >= 20150610 && today < 20150621 && custom_event_count_2 >= 1) {
                            
                            instructions.add("Sign up for the September Mens Member Guest events is currently only open to members who did not play in the June Member Guest events.");
                            instructions.add("You will be able to sign up for the September Mens Member Guest events on June 21st.");
                            
                            use_signup_button = false;
                        }
                    }
                }
                //hide current registration button for quakerridgegc for all events through May 29, 2015
                if (club.equalsIgnoreCase("quakerridgegc") && date <= 20150529) {  
                    
                    use_signup_list = false;
                }
                
                // Configure instructions and buttons
                // After old-skin has been abandond, this configuration should be moved into javascript,
                // allowing customs to be accomplished with club.js 

                if (teams_reg >= max) {         // if no room for more teams

                    // If Rolling Hills - SA, do not display sign up button if max teams has been reached.  Members must call Proshop to be placed on the wait list.
                    if (club.equals("rollinghillsgc")) {
                        instructions.add("This event is currently <b>full</b>.");
                        instructions.add("To be put on the waiting list for this event, send an email to tournaments@arabiangolf.com stating your Name, Member ID, and the event you wish to place on the waiting list for.");
                        use_signup_button = false;

                    } else if (signup > 0) {
                        
                        if (pairings.equalsIgnoreCase("Proshop")) {

                            instructions.add("<b>Warning:</b> this event already has " + count_reg + " members registered.");
                            instructions.add("To be added to the waiting list, select \"[data.signup_button]\"");

                        } else {

                            instructions.add("<b>Warning:</b> this event already has " + teams_reg + " teams registered.");
                            instructions.add("To add a team to the <b>waiting list</b>, select \"[data.signup_button]\"");
                            if (hideNames == 0 && (viewList) && (select_count > 0)) {  // if NOT hiding names, and there are teams to select
                                instructions.add("To join an existing team, click [data.list_button]");
                            }
                        }
                    }

                } else {

                    if (size == 1) {      // only member can sign up (no guests or other members
                        instructions.add("Because of the format selected for this event, you can only register yourself.");
                        instructions.add("To sign up for this event, select \"[data.signup_button]\"");
                    } else {              // allow team sign up 

                        if (activity_id == 0) {
                            if (use_signup_button) {
                                if (hideNames == 0 && viewList && select_count > 0 && size > minsize) {      // if NOT hiding names, then members can join a team (only when team size is larger than minsize)
                                    instructions.add("The format of this event allows you to join an existing team or register a new team.");
                                    prompt_instructions.add("To join an existing team, select \"[data.list_button]\"");
                                    //signup_button_type = "new_team_button";
                                }
                                instructions.add("To register a new team, select \"[data.signup_button]\"");
                            }
                            if (viewList == true && select_count > 0 && size > minsize) {          // if showing the event list
                                list_instructions.add("If \"Moved\" is listed in the Select column, that team has been moved to the tee sheet.");
                            }
                        } else {
                            if (hideNames == 0 && viewList && select_count > 0 && size > minsize) {      // if NOT hiding names, then members can join a team
                                instructions.add("The format of this event allows you to join an existing registration or register a new one.");
                                prompt_instructions.add("To join an existing registration, select \"[data.list_button]\"");
                            }
                            instructions.add("To create a new registration, select \"[data.signup_button]\"");
                        }
                    }
                }
                
                if (club.equals("rollinghillsgc")) {                  
                    
                    if (Utilities.getHdcpIndex(user, req) == -99) {
                        instructions.add("Due to club policy, members must have a current USGA handicap to sign up for events. For more information please contact the Handicap Director.");
                        use_signup_button = false;
                        signup = 0;
                    }
                }

                //
                // Create current players/teams status text
                //
                team_player_status = "";

                String players_term = "players";
                String teams_term = "teams";

                if (activity_id != 0) {
                    players_term = "participants";
                    teams_term = "sign ups";
                }

                if (count_reg == 0) {      // if no one signed up
                    team_player_status = "There are currently no " + players_term + " registered for this event";
                } else {
                    if (pairings.equals("Member")) {
                        team_player_status = "There are currently " + teams_reg + " " + teams_term + " (" + count_reg + " " + players_term + ") registered for this event";
                        if (teams_wait > 0) {
                            team_player_status += " and " + teams_wait + " " + teams_term + " (" + count_wait + " " + players_term + ") on the wait list";
                        }
                    } else {
                        team_player_status = "There are currently " + count_reg + " " + players_term + " registered for this event";
                        if (count_wait > 0) {
                            team_player_status += " and " + count_wait + " " + players_term + " on the wait list";
                        }
                    }
                }

                //
                // Create dashboard/itinerary
                //

                if (season == 0) {
                    event.detail_list.add(reservationUtil.eventDetail("Time", timeUtil.get12HourTime(act_time)));
                    if (activity_id == 0) {
                        event.detail_list.add(reservationUtil.eventDetail("Type", ((type != 0) ? "Shotgun" : "Tee Time")));
                    }
                }

                event.detail_list.add(reservationUtil.eventDetail("Date", ((season == 0) ? timeUtil.getStringDateMDYYYY((int) date) : "Season Long")));

                if (activity_id == 0) {
                    if (!course_display.equals("")) {
                        event.detail_list.add(reservationUtil.eventDetail("Course", course_display));
                    }
                    event.detail_list.add(reservationUtil.eventDetail("Front/Back", fb));
                }
                event.detail_list.add(reservationUtil.eventDetail("Format", format, "double_width"));
                event.detail_list.add(reservationUtil.eventDetail("Gender", Labels.gender_opts[gender]));

                if (activity_id == 0) {
                    event.detail_list.add(reservationUtil.eventDetail("Teams Selected By", pairings, "double_width"));
                }

                if (activity_id == 0) {
                    event.detail_list.add(reservationUtil.eventDetail("Max # of Teams", max));
                } else {
                    event.detail_list.add(reservationUtil.eventDetail("Max # of Sign Ups", max));
                }

                if (activity_id == 0) {
                    event.detail_list.add(reservationUtil.eventDetail("Team Size", size));
                    event.detail_list.add(reservationUtil.eventDetail("Holes", holes));
                    event.detail_list.add(reservationUtil.eventDetail("Min. Sign-Up Size", minsize));
                }

                event.detail_list.add(reservationUtil.eventDetail("Guests per Member", guests));
                event.detail_list.add(reservationUtil.eventDetail("Cost per Guest", gstcost));
                event.detail_list.add(reservationUtil.eventDetail("Cost per Member", memcost));
                event.detail_list.add(reservationUtil.eventDetail("Must Sign Up By", timeUtil.getShortVerboseDate(req, (int) c_date, c_time), "double_width"));

                if (show_itinerary && (itin.length() > 0)) {
                    event.detail_list.add(reservationUtil.eventDetail("Itinerary", itin, "box_with_title"));
                }

                //
                // Output results
                //
                event.event_name = name;
                event.event_id = event_id;
                event.base_url = Utilities.getBaseUrl(req, event_activity_id, club);
                event.slot_url = "Member_evntSignUp";

                event.instructions = instructions;
                event.list_instructions = list_instructions;
                event.prompt_instructions = prompt_instructions;
                event.status = team_player_status;
                event.use_signup_button = use_signup_button;
                event.use_signup_list = use_signup_list;
                //event_map.put("signup_button_type", signup_button_type);
                event.signup = signup > 0; // Can we sign up
                //event_map.put("view_list", ((viewList || signup > 0) && count_reg != 0));
                
                if (in_event) {
                    event.signup_button = "New Registration";
                } else {
                    event.signup_button = "Register";
                }
                event.list_button = "Current Registrations";
                event.edit_signup_button = "Your Registration";
                event.select_count = select_count;
                event.block_reason = reason_msg;
                event.in_event = in_event;
                event.my_signup_id = my_signup_id;
                event.hide_names = hideNames > 0;

                return event;

            } else {    // event name not found

                event.block_reason = "Sorry, requested event is not found.";

            }   // end of IF event

            stmt.close();
            return event;

        } catch (Exception exc) {

            Utilities.logError("reservationUtil.getEventPrompt: err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));

        }

        return null;
    }

    public static reservationPrompt getDiningEventPrompt(HttpServletRequest req, int event_id, String user) {

        String club = Utilities.getSessionString(req, "club", "");

        //List<String> instructions = new ArrayList<String>();
        //List<String> prompt_instructions = new ArrayList<String>();
        //List<String> list_instructions = new ArrayList<String>();
        
        Connection con = Connect.getCon(req);
        Connection con_d = Connect.getDiningCon();
        
        int organization_id = Utilities.getOrganizationId(con);
        
        // Get empty event prompt
        reservationPrompt event = new reservationPrompt();
        
        // Get parmd for this event
        parmDining2 parmD = new parmDining2(organization_id, req, con_d, event_id); 
        
        if(parmD.event.id == 0){
            event.block_reason = "That event does not appear to be valid, or there is an issue temporarily blocking access to the event.";
            Connect.close(con_d);
            return event;
        }
        
        // Get event block status, if any.
        List<diningMealPeriod> mealPeriod = diningUtil.getMealPeriods(parmD.location_id, parmD.event.id, parmD.date, con_d);
        String editStatus = diningUtil.reservationTimeStatus(organization_id, parmD.location_id, parmD.date, parmD.time, parmD, mealPeriod, req, con_d);
        
        event.event_id = parmD.event.id;
        event.signup_list = diningUtil.getEventSignups(event_id, organization_id, user, req, con_d);
        event.base_url = Utilities.getBaseUrl(req, ProcessConstants.DINING_ACTIVITY_ID, club);
        event.slot_url = "Dining_slot";
        event.signup_id_field = "reservation_id";
        event.event_name = parmD.event.name;
        event.player_column = "Reservee";
        event.players_column = "Reservees";
        event.select_column = "Time";
        event.use_signup_list = true;
        
        // Iterate the signups to count guests/members, etc., and get current user's reservation_id
        int guests = 0;
        int members = 0;
        int signups = event.signup_list.size();
        event.my_signup_id = 0;
        for(reservationSignup signup : event.signup_list){
            guests += signup.guests;
            members += signup.members;
            if(signup.id != null && signup.id > 0){
                event.use_signup_button = false; // If we're already signed up, don't show the signup button
                event.my_signup_id = signup.id;
                event.select_count ++; // if we have a signup id, we can select/edit the signup
            }
            if(signup.in_signup){
                event.in_event = true;
            }
        }
        if (editStatus != null) {
            event.signup = false;
            event.block_reason = editStatus;
        } else {
            event.signup = parmD.event.allow_online_signup; // Alow us to signup 
            event.use_signup_button = event.signup; // And allow use of the signup button
            if(!event.signup){
                if(event.my_signup_id > 0){
                    event.block_reason = "This event does not allow online registrations.  Please call the club for any changes to your registration.";
                    //event.use_signup_button = true;
                } else {
                    if (club.equals("tontoverde")) {
                        event.block_reason = "This event does not allow online registrations.";
                    } else {
                        event.block_reason = "This event does not allow online registrations.  Please call the club to register for this event.";
                    }
                }
            }
            // make sure we're in the signup window
            if (timeUtil.getClubDate(con) > parmD.event.close_date) {
                event.use_signup_button = false;
                event.signup = false;
                event.block_reason = "The registration period for this event has passed.";
            }
            if (timeUtil.getClubDate(con) < parmD.event.open_date) {
                event.use_signup_button = false;
                event.signup = false;
                event.block_reason = "This event is not yet open for registrations.";
            }
        }
        int total = guests + members;
        
        if(!diningUtil.canViewOthersEventSignups(organization_id, con_d)){
            event.signup_list.clear(); // empty list if we cant show it
        }
        
        // do not show the current reservation count for lacumbrecc
        if (club.equals("lacumbrecc") || club.equals("kiawahislandclub")) {
            
            event.status = "";
            
        } else {
        
            if(signups == 0){
                event.status = "There are currently no reservations for this event.";
            } else {
                event.status = "There "+(signups>1?"are":"is")+" currently "+signups+" reservation"+(signups>1?"s":"")+", having "+total+" total participant"+(total>1?"s":"")+" for this event.";
            }
        }
        
        event.detail_list.add(reservationUtil.eventDetail("When", 
                timeUtil.getVerboseDate(req, parmD.event.date) + " from " + timeUtil.get12HourTime(parmD.event.start_time) + " to " + timeUtil.get12HourTime(parmD.event.end_time), 
                "box_with_title"));
        
        List<diningLocation> locations = diningUtil.getDiningLocations(organization_id, parmD.event.maximum_advance_days, parmD.location_id, event_id, con_d);
        
        StringBuilder locationString = new StringBuilder();
        int i = 0;
        for(diningLocation location : locations){
            locationString.append(i>0?", ":"");
            locationString.append(location.name);
            i++;
        }
        event.detail_list.add(reservationUtil.eventDetail("Where", 
                locationString.toString(), 
                "box_with_title"));
        
        if(parmD.event.seatingTimes.size() > 1){
            event.detail_list.add(reservationUtil.eventDetail("Seating Times", 
                StringUtils.join(parmD.event.seatingTimes,", "),
                "box_with_title"));
        }
        
        StringBuilder options = new StringBuilder();
        for (diningMealOption mealOption : parmD.event.mealOptions) {
            options.append("<li><span>");
            options.append(mealOption.category);
            options.append(":</span> <span>$");
            options.append(mealOption.cost);
            options.append("</span></li>");
        }
        if(options.length() > 0){
            event.detail_list.add(reservationUtil.eventDetail("Options", 
                    "<ul class=\"standard_list\">"+options.toString()+"</ul>", 
                    "box_with_title"));
        }
        
        event.detail_list.add(reservationUtil.eventDetail("Registration", 
                "Begins " + timeUtil.getStringDateMDYYYY(parmD.event.open_date) + " and ends " + timeUtil.getStringDateMDYYYY(parmD.event.close_date), 
                "box_with_title"));
        
        if(parmD.event.dress_code != null && !parmD.event.dress_code.isEmpty()){
            event.detail_list.add(reservationUtil.eventDetail("Dress Code", 
                parmD.event.dress_code, 
                    "box_with_title"));
        }
        
        if(parmD.event.musical_style != null && !parmD.event.musical_style.isEmpty()){
            event.detail_list.add(reservationUtil.eventDetail("Musical Style", 
                parmD.event.musical_style, 
                    "box_with_title"));
        }
        
        if(parmD.event.theme != null && !parmD.event.theme.isEmpty()){
            event.detail_list.add(reservationUtil.eventDetail("Theme", 
                parmD.event.theme, 
                    "box_with_title"));
        }
        
        if(parmD.event.online_message != null && !parmD.event.online_message.isEmpty()){
            event.detail_list.add(reservationUtil.eventDetail("Itinerary", 
                    parmD.event.online_message, 
                    "box_with_title"));
        }

        if(event.in_event){
            // I'm in the event
            event.use_signup_button = false;
        }
        
        Connect.close(con_d);

        return event;

    }
    
    
    public static reservationPrompt getDiningSignupsByDate(HttpServletRequest req, int date, String user) {

        String club = Utilities.getSessionString(req, "club", "");
        boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
        
        htmlTags tags = new htmlTags(rwd);

        //List<String> instructions = new ArrayList<String>();
        //List<String> prompt_instructions = new ArrayList<String>();
        //List<String> list_instructions = new ArrayList<String>();
        
        Connection con = Connect.getCon(req);
        Connection con_d = Connect.getDiningCon();
        
        int organization_id = Utilities.getOrganizationId(con);
        
        // Get empty event prompt
        reservationPrompt event = new reservationPrompt();
        
        
        // Get event block status, if any.
        event.signup = true; // Alow us to signup 
        event.use_signup_button = false; // And allow use of the signup button
        //event.event_id = 0;
        event.signup_list = diningUtil.getSignupsByDate(date, organization_id, user, req, con_d);
        event.base_url = Utilities.getBaseUrl(req, ProcessConstants.DINING_ACTIVITY_ID, club);
        event.slot_url = "Dining_slot";
        event.signup_id_field = "reservation_id";
        event.list_title =  tags.getDatePicker("Reservations for", // Keep this short, or you'll push the date selector off mobile devices
                timeUtil.getStringDateMMDDYYYY(date), 
                timeUtil.getStringDateMMDDYYYY(timeUtil.getClubDate(req)), 
                "", "reservation_date_picker") ;
        if(event.signup_list.isEmpty()){
            event.list_status = "No reservation yet for " + timeUtil.getStringDateMDYYYY(date);
        }
        event.player_column = "Reservee";
        event.players_column = "Reservees";
        event.select_column = "Time";
        event.use_type = true;
        
        int guests = 0;
        int members = 0;
        int signups = event.signup_list.size();
        event.my_signup_id = 0;
        for(reservationSignup signup : event.signup_list){
            guests += signup.guests;
            members += signup.members;
            if(signup.id != null && signup.id > 0){
                event.my_signup_id = signup.id;
                event.select_count ++; // if we have a signup id, we can select/edit the signup
            }
        }
        event.status_per_player = true;
        event.use_location = true;
        
        Connect.close(con_d);
        
        return event;

    }
    
    
    public static List<reservationSignup> getLessonSignupsByUser(HttpServletRequest req, String user) {
        return getLessonSignupsByUser(req, user, null, null);
    }
    
    public static List<reservationSignup> getLessonSignupsByUser(HttpServletRequest req, String user, Integer date) {
        return getLessonSignupsByUser(req, user, date, null);
    }

    public static List<reservationSignup> getLessonSignupsByUser(HttpServletRequest req, String user, Integer date, Integer activity_id) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //String current_user = reqUtil.getSessionString(req, "user", "").toLowerCase();

        long now = timeUtil.getCurrentUnixTime();

        if (date == null) {
            date = timeUtil.getClubDateTime(req, now)[timeUtil.DATE];
        }
        String activityFilter;
        if(activity_id != null){
            activityFilter = "WHERE activity_id = ? ";
        } else {
            activityFilter = "";
        }
        
        //long searchtime = timeUtil.getClubUnixTime(req, date);

        Connection con = Connect.getCon(req);
        
        List<reservationSignup> result = new ArrayList<reservationSignup>();

        try {

            pstmt = con.prepareStatement(""
                    + "( "
                    + " SELECT lb.date AS `date`, lb.time AS `time`, lb.memname AS player, lb.memid AS `user`, "
                    + "   lt.length AS `length`, lb.ltype AS lesson_name, lt.cost AS cost, lt.activity_id AS activity_id, "
                    + "   lp.id AS pro_id, lb.recid AS lesson_id, "
                    + "   CONCAT_WS(' ', lp.fname, IF(lp.mi = '',NULL,lp.mi), lp.lname) AS pro_name, "
                    + "   'Individual' AS `type` "
                    + "  FROM lessonbook5 AS lb "
                    + "   LEFT OUTER JOIN lessonpro5 AS lp "
                    + "    ON lp.id = lb.proid "
                    + "   LEFT OUTER JOIN lessontype5 AS lt "
                    + "    ON lt.proid = lb.proid "
                    + "    AND lt.ltname = lb.ltype "
                    + "  WHERE lb.memid = ? "
                    + "   AND lb.date >= ? "
                    + "   AND lb.ltype != '' "
                    + ") "
                    + "UNION ALL "
                    + "( "
                    + " SELECT lgs.date AS `date`, lg.stime AS `time`, lgs.memname AS player, lgs.memid AS `user`, "
                    + "   (lg.etime - lg.stime) AS `length`, lg.lname AS lesson_name, lg.cost AS cost, lg.activity_id AS activity_id, "
                    + "   lp.id AS pro_id, lg.lesson_id AS lesson_id, "
                    + "   CONCAT_WS(' ', lp.fname, IF(lp.mi = '',NULL,lp.mi), lp.lname) AS pro_name, "
                    + "   'Group' AS `type` "
                    + "  FROM lgrpsignup5 AS lgs "
                    + "   LEFT OUTER JOIN lessonpro5 AS lp "
                    + "    ON lp.id = lgs.proid "
                    + "   LEFT OUTER JOIN lessongrp5 AS lg "
                    + "    ON lg.lesson_id = lgs.lesson_id "
                    + "    AND lg.proid = lgs.proid " // just incase -- shouldn't be needed
                    + "  WHERE lgs.memid = ? "
                    + "   AND lgs.date >= ? "
                    + ") "
                    + activityFilter
                    + "ORDER BY `date`, `time`, activity_id ");

            pstmt.setString(1, user);
            pstmt.setInt(2, date);
            pstmt.setString(3, user);
            pstmt.setInt(4, date);
            if(activity_id != null){
                pstmt.setInt(5, activity_id);
            }
            rs = pstmt.executeQuery();

            long restime;

            while (rs.next()) {

                reservationSignup signup = new reservationSignup();
                
                signup.date = rs.getInt("date");
                signup.time = rs.getInt("time");
                
                signup.activity_id = rs.getInt("activity_id");

                restime = timeUtil.getClubUnixTime(req, signup.date, signup.time);
                
                signup.players.add(new reservationPlayer(rs.getString("user"), rs.getString("player"), rs.getString("player"), null));

                signup.in_signup = true;
                
                signup.length = rs.getInt("length");
                
                signup.cost = rs.getString("cost");
                
                signup.id = rs.getInt("lesson_id");
                
                if(rs.getString("type").equals("Group")){
                    signup.slot_type = "group_lesson";
                    signup.url = "Member_lesson?" + httpConnect.getUri(new String[]{
                                "proid", rs.getString("pro_id"),
                                "date", Integer.toString(signup.date),
                                "lgname", rs.getString("lesson_name"),
                                "lesson_id", rs.getString("lesson_id"),
                                "activity_id", Integer.toString(signup.activity_id),
                                "groupLesson","yes",
                                "index", "999"
                            }, true);
                } else {
                    
                    signup.slot_type = "individual_lesson";
                    signup.url = "Member_lesson?" + httpConnect.getUri(new String[]{
                                "proid", rs.getString("pro_id"),
                                "date", Integer.toString(signup.date),
                                "time", Integer.toString(signup.time),
                                "ltype", rs.getString("lesson_name"),
                                "lesson_id", rs.getString("lesson_id"),
                                "activity_id", Integer.toString(signup.activity_id),
                                "index", "999",
                                "reqtime", "yes"
                            }, true);
                }

                if (restime > now && signup.in_signup) {
                    //signup.id = 1; // No signup ids for lessons yet
                } else {
                    signup.id = null;
                    signup.url = null;
                }

                signup.location = rs.getString("lesson_name");
                signup.pro_name = rs.getString("pro_name");

                signup.type = rs.getString("type")+" "+getActivity.getRootNameFromActivityId(rs.getInt("activity_id"), req)+" Lesson";

                signup.select_button = timeUtil.formatDate(req, restime, "M/d/yyyy, h:mm aaa");

                signup.in_signup = true;

                signup.members = 1;
                signup.guests = 0;

                signup.show_player_status = false;
                signup.status = "";

                result.add(signup);
            }
        } catch (Exception exc) {

            Utilities.logError("reservationUtil.getLessonSignupsByUser: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));

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
    
    
    public static List<reservationSignup> getNotificationReservationsByUser(HttpServletRequest req, String user) {
        return getNotificationReservationsByUser(req, user, null, null);
    }
    
    public static List<reservationSignup> getNotificationReservationsByUser(HttpServletRequest req, String user, Integer date) {
        return getNotificationReservationsByUser(req, user, date, null);
    }
      
    public static List<reservationSignup> getNotificationReservationsByUser(HttpServletRequest req, String user, Integer date, Integer time) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String current_user = reqUtil.getSessionString(req, "user", reqUtil.getSessionString(req, "ext-user", "")).toLowerCase();

        long now = timeUtil.getCurrentUnixTime();

        if (time == null) {
            time = 0;
        }
        if (date == null) {
            date = timeUtil.getClubDateTime(req, now)[timeUtil.DATE];
        }
        long searchtime = timeUtil.getClubUnixTime(req, date, time);

        Connection con = Connect.getCon(req);
        
        List<reservationSignup> result = new ArrayList<reservationSignup>();

        try {

            pstmt = con.prepareStatement(""
                    + "SELECT n.*, cp.courseName, "
                    + "  COUNT(IF(np.username != '' AND np.username IS NOT NULL,1,null) as member_count, "
                    + "  GROUP_CONCAT(np.player_name ORDER BY np.pos SEPARATOR '\\t') AS players, " // Get players, tab delimited
                    + "  GROUP_CONCAT(LOWER(np.username) ORDER BY np.pos SEPARATOR '\\t') AS users, "
                    + "  GROUP_CONCAT(np.cw ORDER BY np.pos SEPARATOR '\\t') AS cws, "
                    + "  GROUP_CONCAT(np.guest_id ORDER BY np.pos SEPARATOR ',') AS guest_ids, "
                    + "  GROUP_CONCAT(IFNULL(CONCAT_WS(', ', m1.name_last, CONCAT_WS(' ',m.name_first, IF(m.name_mi='',NULL,m.name_mi))),'') ORDER BY np.pos SEPARATOR '\\t') AS alpha_names "
                    + " FROM notifications n "
                    + "  INNER JOIN notifications_players AS npu " // Use inner join to filter notifications that are specified user's
                    + "   ON npu.notification_id = n.notification_id "
                    + "    AND npu.username = ? "
                    + "  LEFT OUTER JOIN notifications_players AS np " // Get player names
                    + "   ON np.notification_id = n.notification_id "
                    + "  LEFT OUTER JOIN member2b AS m "
                    + "   ON m.username = np.username "
                    + "  LEFT OUTER JOIN clubparm2 AS cp " // Get course name
                    + "   ON cp.clubparm_id = n.course_id " // Is this correct? Or do we use an index?
                    + " WHERE DATE(n.req_datetime) >= ? "
                    + "  AND n.converted = 0 "
                    + " GROUP BY n.notification_id "
                    + " ORDER BY n.req_datetime ");

            pstmt.setString(1, user);
            pstmt.setString(2, timeUtil.formatDate(req, searchtime, "yyyy-MM-dd kk:mm"));
            rs = pstmt.executeQuery();

            String[] players, cws, users, alpha_names;
            Integer[] guest_ids;
            long restime;
            int[] resDateTime;

            while (rs.next()) {

                boolean can_access = false;
                Integer signup_id = 0;

                reservationSignup signup = new reservationSignup();

                signup_id = rs.getInt("notification_id");
                restime = timeUtil.getClubUnixTimeFromDb(req, rs.getString("req_datetime"));
                resDateTime = timeUtil.getClubDateTime(req, restime);

                players = rs.getString("players").split("\t", -1);
                alpha_names = rs.getString("alpha_names").split("\t", -1);
                users = rs.getString("users").split("\t", -1);
                cws = rs.getString("cws").split("\t", -1);
                guest_ids = ArrayUtil.stringToInteger(rs.getString("guest_ids").split(",", -1), 0);

                signup.in_signup = Arrays.asList(users).contains(current_user);

                if (restime > now && signup.in_signup) {
                    signup.url = "MemberTLT_slot?" + httpConnect.getUri(new String[]{
                                "notifyId", signup_id.toString(),
                                "stime", timeUtil.get12HourTime(resDateTime[timeUtil.TIME]),
                                "sdate", Integer.toString(resDateTime[timeUtil.DATE])
                            }, true);
                    signup.id = signup_id;
                }

                signup.location = rs.getString("courseName");

                signup.type = "Notification";

                signup.time = resDateTime[timeUtil.TIME];
                signup.date = resDateTime[timeUtil.DATE];

                signup.select_button = timeUtil.formatDate(req, restime, "M/d/yyyy, h:mm aaa");

                signup.in_signup = true;

                signup.members = rs.getInt("member_count");
                signup.guests = players.length - signup.members;
                
                signup.players = buildReservationPlayers(users, players, alpha_names, cws, guest_ids);

                if (can_access) {
                    signup.id = signup_id;
                }
                signup.show_player_status = false;
                signup.status = "Waiting";

                signup.slot_type = "notification";

                result.add(signup);
            }
        } catch (Exception exc) {

            Utilities.logError("reservationUtil.getNotificationReservationsByUser: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));

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
    
    public static List<reservationPlayer> buildReservationPlayers(String[] users, String[] names, String[] alpha_names, String[] cws, Integer[] guest_ids){
        
        List<reservationPlayer> result = new ArrayList<reservationPlayer>();
        for(int i = 0; i < users.length; i++){
            result.add(new reservationPlayer(users[i], names[i], alpha_names[i], null, cws[i], guest_ids[i]));
        }
        return result;
        
    }
    
    
    public static int getMaxPlayersForTeeTime(HttpServletRequest req, int date, int time, String course, int fb, boolean fivesomes) {
        
        String club = reqUtil.getSessionString(req, "club", "");
        /* You can get user, etc. out of the request object here, just like club */
        //Connection con = Connect.getCon(req);
        DateTime jdt = timeUtil.getClubJodaDateTime(req, date, time);
        int week_of_year = jdt.weekOfWeekyear().get();
        String day_name = timeUtil.getDayOfWeek(date);
        
        Connection con = Connect.getCon(req);
        long cur_date = Utilities.getDate(con);
        long cur_yy = cur_date / 10000;
        long cur_mm = (cur_date - (cur_yy * 10000)) / 100;
        long cur_dd = (cur_date - (cur_yy * 10000)) - (cur_mm * 100);       // get day
        long cur_shortDate = (cur_mm * 100) + cur_dd;

        int result = fivesomes?5:4;

        boolean oneSomeOnly = false;
        boolean twoSomeOnly = false;
        boolean threeSomeOnly = false;

        //***********************************************
        //  2-some processing - customs
        //***********************************************
        //

        //
        //  if Woodway CC, check if tee time is for 2-somes only
        //
        if (club.equals("woodway")) {

            twoSomeOnly = verifySlot.checkWoodway(date, time, fb);     // check if special time
        }

        //  if Huntingdon Valley CC, check if tee time is for 2-somes only
        if (club.equals("huntingdonvalleycc")) {

            twoSomeOnly = verifySlot.checkHuntingdonValley(date, time, course, fb);     // check if special time
        }

        //
        //  if Hudson National, check if tee time is for 2-somes only
        //
        if (club.equals("hudsonnatl")) {

            twoSomeOnly = verifySlot.checkHudson(date, time, day_name);     // check if special time
        }

        //
        //  if The Stanwich Club, check if tee time is for 2-somes only
        //
        if (club.equals("stanwichclub")) {

            twoSomeOnly = verifySlot.checkStanwich(date, time, day_name);     // check if special time
        }

        //
        //  New Canaan, check if tee time is for 2-somes only
        //
        if (club.equals("newcanaan")) {

            twoSomeOnly = verifySlot.checkNewCanaan(date, time, day_name);     // check if special time
        }

        //
        //  Apawamis, check if tee time is for 2-somes only
        //
        if (club.equals("apawamis")) {

            twoSomeOnly = verifySlot.checkApawamis(date, time, day_name);     // check if special time
        }

        //
        //  Wee Burn, check if tee time is for 2-somes only
        //
        if (club.equals("weeburn")) {

            twoSomeOnly = verifySlot.checkWeeburn(date, time, day_name);     // check if special time
        }

        //
        //  Claremont, check if tee time is for 2-somes only  (Case #1281)
        //
        if (club.equals("claremontcc")) {

            if (day_name.equals("Sunday") && time >= 1030 && time <= 1115) {
                twoSomeOnly = true;
            }
        }

        //
        //  Misquamicut, check if tee time is for 2-somes only (case #1996)
        //
        if (club.equals("misquamicut")) {

            twoSomeOnly = verifyCustom.checkMisquamicut2someTimes(date, time, day_name);
        }

        //
        //  Bald Peak, check if tee time is for 2-somes only  (Case #1428)
        //
//        if (club.equals("baldpeak")) {
//
//            if (day_name.equals("Friday") && time >= 730 && time <= 754) {
//                twoSomeOnly = true;
//            }
//        }

        //
        //  if Mayfield Sand Ridge, check if tee time is for 2-somes only on the Sand Ridge Course
        //
        if (club.equals("mayfieldsr") && course.equalsIgnoreCase("sand ridge")) {

            twoSomeOnly = verifyCustom.checkMayfieldSR(date, time, day_name);     // check if special time
        }

        //
        //   longue vue Club - check for 2-some time
        //
        if (club.equals("longuevueclub")) {

            twoSomeOnly = verifyCustom.check2LongueVue(date, time, day_name);     // check if special time
        }

        //
        //  if Dorset Field Club, check if tee time is for 2-somes only times  Case# 1440
        //
        if (club.equals("dorsetfc")) {

            twoSomeOnly = verifyCustom.checkDorsetFC(date, time, day_name);     // check if special time
        }

        //
        //  Desert Forest - check to see if tee time is for 2-somes only
        //
        if (club.equals("desertforestgolfclub")) {

            twoSomeOnly = verifyCustom.checkDesertForest(date, time, fb, day_name);       // check if special time
        }

        //
        //  Cherry Valley CC - check to see if tee time is for 2-somes only
        //
        if (club.equals("cherryvalleycc")) {

            twoSomeOnly = verifyCustom.checkCherryValley2someTimes(date, time, day_name);
        }

        if (club.equals("echolakecc")) {

            twoSomeOnly = verifyCustom.checkEchoLake2someTimes(date, time, day_name);
        }

        if (club.equals("baltusrolgc")) {

//                **** NOTE - Only the 2-some only portion pertains to the code here, however the entire custom setup is listed in these comments for clarity ****
//
//            ****Dates and time changes for 2015****
//            Member sub-type 18 Holer
//            Twosomes Only times:
//            Lower Course:  April 21 - Oct. 6 every other Tuesday 7:30am-10:00am.
//            Upper Course:  April 28 - Sept. 29 every other Tuesday 7:30am-10:00am.  There are also 2 Thursdays:  May 28 and Oct. 15 both on Upper Course (same times).
//
//            4some Times:
//            Lower Course:  April 28 - Sept. 29th every other Tuesday 8am-9am
//            Upper Course:  April 21 - Oct. 6 every other Tuesday 8am-9am
//
//            Member Sub-Type 9 Holer:
//            2some Only Times:
//            Upper Course:  8am-10am every other Wednesday April 22 - July 15.  Other Wednesday dates include Sept. 9, 16, 23 and Oct. 7.
//            Lower Course:  8am-10am every other Wednesday April 29 - July 22.  Every Wednesday July 29th - Aug. 19.  Additional Wednesdays are Sept. 2, 30, and Oct. 14.

            // These times are all 2-some only the first week, then after 5/6 on Lower and 4/29 on Upper, the tee times from 8:00am - 9:00am allow 4-somes
            if ((day_name.equals("Tuesday") && (time >= 730 && time <= 1000
                    && (((week_of_year % 2 == 1 || date == 20150623) && course.equals("Lower") && date >= 20150421 && date <= 20151006)
                     || (week_of_year % 2 == 0 && course.equals("Upper") && date >= 20150428 && date <= 20150929 && date != 20150623))))
             || (day_name.equals("Wednesday") && time >= 800 && time <= 1000  
                    && ((course.equals("Upper") && ((date >= 20150422 && date <= 20150715 && week_of_year % 2 == 1) 
                      || date == 20150909 || date == 20150916 || date == 20150923 || date == 20151007)) 
                     || (course.equals("Lower") && ((date >= 20150429 && date <= 20150722 && week_of_year % 2 == 0) || (date >= 20150729 && date <= 20150819) 
                      || date == 20150902 || date == 20150930 || date == 20151014))))) {

                twoSomeOnly = true;
            }
        }

        if (club.equals("piedmont")) {
            if (verifySlot.checkPiedmont(date, time, day_name) == 3) {
                twoSomeOnly = true;             // Only allow 2-somes for this tee time
            }
        }

        //
        //  if Westchester CC, check if tee time is for 2-somes only on the South course
        //
        if (club.equals("westchester") && course.equalsIgnoreCase("south")) {

            twoSomeOnly = verifySlot.checkWestchester(date, time, day_name);     // check if special time
        }

        //
        //  if The CC, check if tee time is for 2-somes only on the Main Course
        //
        if (club.equals("tcclub") && course.startsWith("Main Cours")) {

            twoSomeOnly = verifyCustom.checkTheCC(date, time, day_name);     // check if special time
        }

        //
        //  if New Canaan, check if tee time is for 2-somes only
        //
        if (club.equals("newcanaan")) {

            twoSomeOnly = verifyCustom.checkNewCan(date, time, day_name);     // check if special time
        }

        //
        //  if Greenwich CC, check if tee time is for 2-somes only
        //
        if (club.equals("greenwich")) {

            twoSomeOnly = verifyCustom.checkGreenwich(date, time);     // check if special time
        }

        // If Indian Hills CC and the 'Hitting Room', always set to two some times
        if (club.equals("indianhillscc") && course.equalsIgnoreCase("Hitting Room")) {

            twoSomeOnly = true;
        }


        //
        //  if The Congressional, check if tee time is for 2-somes only on the Gold Course
        //
        if (club.equals("congressional")) {

            if (course.equals("Gold Course") && day_name.equals("Tuesday")) {

                twoSomeOnly = congressionalCustom.check2Somes(date, time);     // check if special time
            }

        }
        
        if (club.equalsIgnoreCase("mg-cc")) {
            
            twoSomeOnly = verifySlot.checkmg_cc(date, time, day_name);     // check if special time         
        }
        
        if (club.equalsIgnoreCase("foxchapelgolfclub")) {
            
            twoSomeOnly = verifySlot.checkfoxchapel(date, time, day_name);     // check if special time         
        }
        
        if (club.equalsIgnoreCase("pelhamcc")) {
            
            twoSomeOnly = verifySlot.checkPelhamcc(date, time, day_name);
        }

        //***********************************************
        //  3-some processing - customs
        //***********************************************
        //
        

        if (club.equals("chartwellgcc") && day_name.equals("Thursday") && date > 20090331 && date < 20091102
                && time > 759 && time < 1001) {

            threeSomeOnly = true;
        }


        //
        //  Meadow Club  (case 1761)
        //
   /*
        if (club.equals( "meadowclub" )) {
        
        threeSomeOnly = verifyCustom.checkMeadowClub(date, time, day_name);     // check if 3-some time
        }
         */


        //
        //  Ramsey  (case 1816)
        //
        if (club.equals("ramseycountryclub")) {

            threeSomeOnly = verifyCustom.checkRamsey3someTime(date, time, day_name);     // check if 3-some time
        }

        // Sonnenalp (case 1978) - check for 3-some times except on july 1st
        if (club.equals("sonnenalp") && date != 20140701 ) {

            threeSomeOnly = verifyCustom.checkSonnenalp3someTimes(date, time, day_name);
        }

        // Royal Montreal GC - Check for 3-some times
        if (club.equals("rmgc")) {

            threeSomeOnly = !verifyCustom.checkRMGC4Ball(course, date, day_name);
        }

        if (club.equals("minnetonkacc")) {

            threeSomeOnly = verifyCustom.checkMinnetonka3someTimes(date, time, day_name, fb);
        }
        
        // Riviera CC - On Tuesdays between 6/25/13 and 9/24/13 (except some specific dates), tee times between 8:02am and 10:58am need to be 3-some only.
        if (club.equals("rivieracc")) {

            if (date >= 20130625 && date <= 20130924 && date != 20130709 && date != 20130820 && (day_name.equals("Tuesday") || (day_name.equals("Thursday") && (date == 20130711 || date == 20130822))) 
                    && time >= 802 && time <= 1058) {
                threeSomeOnly = true;
            }
        }
        
        if (club.equals("foxchapelgolfclub")) {

            threeSomeOnly = verifySlot.checkfoxchapel3some(date, time, day_name);
        }

        /*
        //
        //  Rivercrest Golf Club & Preserve (case 1492)
        //
        if (club.equals( "rivercrestgc" )) {
        
        threeSomeOnly = verifyCustom.checkRivercrest(date, time, day_name);     // check if 3-some time
        }
         */

        if (oneSomeOnly) {
            result = 1;  
        } if (twoSomeOnly){
            result = 2;
        } else if (threeSomeOnly) {
            result = 3;
        }
        return result;
    }
    
    
    
    public static enum ButtonFormat {
        
        EVENT_SIGNUP("Event"),
        DATE_TIME("Date/Time")
        ;
        
        private ButtonFormat(String columnTitle){
            this.columnTitle = columnTitle;

        }
        public String columnTitle;

    }
    
    public static enum ListType {
        
        DINING_SIGNUP (ButtonFormat.DATE_TIME, "Reservees", "sP sR", "Location", "Type", true, false, false, false),
        NOTIFY_LIST (ButtonFormat.DATE_TIME, "Players", "sP", "Course", null, false, false, false, false)
        ;

        private ListType(
                ButtonFormat buttonFormat,
                String playerColumn,
                String playerColumnClass,
                String locationColumn,
                String typeColumn,
                boolean showFb,
                boolean showDateTime,
                boolean showDate,
                boolean showTime) {
            this.buttonFormat = buttonFormat;
            this.playerColumn = playerColumn;
            this.playerColumnClass = playerColumnClass;
            this.locationColumn = locationColumn;
            this.typeColumn = typeColumn;
            this.showFb = showFb;
            this.showDateTime = showDateTime;
            this.showDate = showDate;
            this.showTime = showTime;
        }
     
        public ButtonFormat buttonFormat;
        public String playerColumn;
        public String playerColumnClass;
        public String locationColumn;
        public String typeColumn;
        public boolean showFb;
        public boolean showDateTime;
        public boolean showDate;
        public boolean showTime;

    }
    
    // Not yet complete
    
    public static String getSignupListHtml(HttpServletRequest req, List<reservationSignup> signups, String tableClass, String title, String subTitle, ListType listType){
        
        boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);

        String club = Utilities.getSessionString(req, "club", "");
        
        htmlTags tags = new htmlTags(rwd);

        StringBuilder result = new StringBuilder();
        
        StringBuilder buttonText;
        
        parmClub parm;
        
        boolean showLocation = true;
        boolean init = false;
        
        
        for(reservationSignup signup : signups){

            result.append(tags.openTr(listType.showFb?"":"ft-noFb"));
            
            buttonText = new StringBuilder();
            
            if(!init){
                // Set some stuff off our first record
                init = true;
                parm = getClub.getParms(req, signup.activity_id); // cached in the request, so it's not as expensive as you think
                                                                  // may need to reload parm on every loop if we need parms from different activities
                showLocation = listType.locationColumn != null && !(listType.locationColumn.equals("Course") && parm.multi == 0);
            }

            switch (listType.buttonFormat){
                case EVENT_SIGNUP:
                    buttonText.append(signup.type);
                    break;
                    
                case DATE_TIME:
                    buttonText.append(timeUtil.formatDate(req, signup.date, signup.time, "M/d/yyyy, h:mm aaa"));
                    break;
            }
            
                    
            if(signup.id != null && signup.id > 0){
                result.append(tags.getTd(
                        tags.getFtLink(
                        buttonText.toString(), 
                        Utilities.getBaseUrl(req, signup.activity_id, club)+"Dining_slot?reservation_id="+signup.id, 
                        "standard_button"), "sT"));
            } else {
                result.append(tags.getTd(tags.getTag("div", buttonText.toString(), "time_slot"), "sT"));
            }
            
            if(showLocation){
                result.append(tags.getTd(signup.location, "sN"));
            }
            
            
            if(signup.event_id != null && signup.event_id > 0){
                result.append(tags.getTd(tags.getJsonLink(signup.type, reservationUtil.linkJsonEsc(signup.event_id, ProcessConstants.DINING_ACTIVITY_ID), "standard_link event_button"), "sP sTp"));
            } else {
                result.append(tags.getTd(signup.type, "sP sTp"));
            }
            result.append(tags.getTd(reservationUtil.getPlayerHtml(signup.players, false, req), "sP sR"));
            result.append(tags.closeTr());

        }
        
        if(result.length() > 0){
            StringBuilder table = new StringBuilder();
            
            table.append(tags.openTable("standard_list_table rwdCompactible " + tableClass));
            
            if(subTitle != null & !subTitle.isEmpty()){
                table.append(tags.getCaption(tags.getTag("h2", title )+subTitle));
            } else {
                table.append(tags.getCaption(tags.getTag("h2", title )));
            }
            
            table.append(tags.openThead());
            table.append(tags.openTr());
            
            table.append(tags.getTh(listType.buttonFormat.columnTitle));
            
            if(listType.locationColumn != null){
                table.append(tags.getTh(listType.locationColumn));
            }
            
            if(listType.typeColumn != null){
                table.append(tags.getTh(listType.typeColumn));
            }
            
            table.append(tags.getTh(listType.playerColumn));
            
            table.append(tags.closeTr());
            table.append(tags.closeThead());
            table.append(tags.openTbody());

            table.append(result);

            table.append(tags.closeTbody());
            table.append(tags.closeTable());
            
            result = table;
        }
        
        return result.toString();
        
    }
}
