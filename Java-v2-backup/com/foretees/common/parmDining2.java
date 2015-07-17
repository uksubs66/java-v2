/***************************************************************************************
 *   parmDining2:  This class will hold all the data needed for a dining reservation.
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

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.*;
import java.util.ArrayList;

//import java.lang.reflect.Type;
public class parmDining2 {
    
    /* Stage definitions */
    public static final int PROMPT = 0;
    public static final int SLOT = 1;
    public static final int PROCESS = 2;
    public static final int DELETE = 3;
    public static final int REMOVE_SELF = 4;
    
    public int stage = PROMPT;

    public int id = 0;
    public int event_id = 0;
    public diningEvent event = new diningEvent();
    public int organization_id = 0;
    public int location_id = 0;

    public int covers = 0;
    public int reservations_count = 0;
    public String special_requests = "";
    public int occasion_id = 0;
    
    public boolean spouse_reservation = false;
    public boolean bump_count = false;
    public boolean has_guests = false;
    public boolean hide_check_numbers = false;
    
    public boolean has_answers = false;
    
    public int taken_by_user_id = 0;
    public int changed_by_user_id = 0;
    
    public String charges = "";
    public String table_number = "";
    public String table_assignments = "";
    public String category = "";
    public String location_name = "";
    public String owner = "";
    public int owner_person_id;

    public int date = 0; // selected date
    public int time = -1; /* 0 = midnight -1 = not set*/
    
    public int maximum_online_size = 0;      // max online reservation count for a location
    public int maximum_party_size = 0;       // max size for party as defined by the location
    
    public boolean walk_in = false;
    public boolean date_selected = false;
    
    public int reservation_number = 0;
    public int cancellation_number = 0; //??
    
    public String confirmation_email = "";
    public String confirmation_status = "";
    
    public int cancelled_by_user_id = 0;
    public String member_special_requests = "";
    public int auto_email_members_for_dining_reservations = 0;
    public int auto_email_members_for_event_reservations = 0;
    public boolean member_created = false;
    public boolean is_event = false;
    
    public boolean slot_data_error = false;
    
    public String reservation_load_error = null;
    
    public String state = "";

    public List<String> names;             // names in the reservation
    public List<String> reservee_category;  
    public List<String> price_category;
    public List<String> usernames;   // Foretees user id / name
    public List<Integer> related_id;           // holds the uid for the related reservation
    public List<Integer> check_num;            // check number for corresponding name in the reservation
    public List<Integer> dining_id;            // dining_id of the corresponding name in the reservation
    
    public Map<Integer,Question> questions = new LinkedHashMap<Integer,Question>();
    public Map<Integer,Question> group_questions = new LinkedHashMap<Integer,Question>();
    
    public List<Map<Integer,String>> answers;
    public Map<Integer,String> group_answers;
    
    public Map<String,Object> debug = new LinkedHashMap<String,Object>();
    
    //public List<Map<String, Object>>  meal_option_list = new ArrayList<Map<String, Object>>();
    
    private static final int OK = 0;
    private static final int RECORD_ERROR = 1;
    private static final int DATABASE_ERROR = 2;
    
    //private Map<String, Integer> USER_MAP = null;
    //private Map<String, Integer> NAME_MAP = null;
    //private Map<Integer, Integer> PERSON_ID_MAP = null;
    //private Map<Integer, Integer> RELATED_ID_MAP = null;
    
    // Populate with current user only
    public parmDining2(int orgId, int eventId, HttpServletRequest req, Connection con_d) {
        
        Connection con = Connect.getCon(req);
        
        organization_id = orgId;
        event_id = eventId;
        id = 0;
        owner = reqUtil.getSessionString(req, "user", reqUtil.getSessionString(req, "ext-user", "")); 
        owner_person_id = Utilities.getPersonId(owner, con);
        is_event = event_id > 0;
        
        List<String> l_names = new ArrayList<String>();
        List<String> l_usernames = new ArrayList<String>();
        List<String> l_reservee_category = new ArrayList<String>();
        List<String> l_price_category = new ArrayList<String>();
        List<Integer> l_related_id = new ArrayList<Integer>();
        List<Integer> l_dining_id = new ArrayList<Integer>();
        List<Integer> l_check_num = new ArrayList<Integer>();
        List<Map<Integer, String>> l_answers = new ArrayList<Map<Integer, String>>();

        l_names.add(Utilities.getSessionString(req, "name", ""));
        l_usernames.add(owner);
        //l_usernames.add(null); 
        l_reservee_category.add("member"); 
        l_price_category.add("");     // extract the price category from the charges field
        l_related_id.add(0);
        l_dining_id.add(owner_person_id);
        l_check_num.add(1);
        l_answers.add(new LinkedHashMap<Integer, String>());

        group_answers = new LinkedHashMap<Integer, String>();
        check_num = l_check_num;
        dining_id = l_dining_id;
        related_id = l_related_id;
        names = l_names;
        usernames = l_usernames;
        reservee_category = l_reservee_category;
        price_category = l_price_category;
        answers = l_answers;
        
        loadOrganizationValues(con_d);
    }

    // Populate from database
    public parmDining2(int orgId, int reservationId, Connection con_d, HttpServletRequest req) {

        id = diningUtil.getMasterId(orgId, reservationId, con_d); // id will always be the master id
        
        organization_id = orgId;

        boolean close_con = false;

        Connection con = Connect.getCon(req);
        
        if (con_d == null) {
            con_d = Connect.getDiningCon();
            close_con = true;
        }
        if (con_d == null) {
            reservation_load_error = "Unable to connect to dining system database.";
            return;
        }

        //PreparedStatement pstmt = null;
        //ResultSet rs = null;
        
        // load questions for this meal time or event
        loadQuestions(con_d);
 
        switch (loadReservationData(con_d, con)) {
            case DATABASE_ERROR:
                // Error connecting to the database or executing a query
                reservation_load_error = "Unable to query dining system database.";
                break;

            case RECORD_ERROR:
                // The record didn't load, but the query didn't fail.
                if(diningUtil.isCancelled(organization_id, id, con_d)){
                    // The reservation is cancelled
                    reservation_number = diningUtil.getReservationNumber(organization_id, id, con_d);
                    if(reservation_number > 0){
                        reservation_load_error = "Reservation #"+reservation_number+" appears to be cancelled.";
                    } else {
                        reservation_load_error = "Reservation appears to be invalid. (ID=" + id + ":" + reservationId + ")";
                    }
                } else{
                    // Not cancelled, but we couldn't load it. Probably no master record. Can we repair it?
                    id = diningUtil.repairReservation(organization_id, reservationId, con_d);
                    if(id == 0 || loadReservationData(con_d, con) != OK){
                        // Looks like the repair didn't work
                        reservation_load_error = "Reservation record does not appeard to exist, it is misconfigured, or is in an incomplete state. (ID=" + id + ":" + reservationId + ")";
                    }
                }
                
                break;
        }
        
        if(reservation_load_error!=null){
            return;
        }

        // ensure never null
        if (member_special_requests == null || member_special_requests.equals("null") == true) {

            member_special_requests = "";
        }

        loadQuestions(con_d);
        
        // load the names stored in this and its related reservations
        loadReservationNames(con_d, con);

        // get the occasion name
        //occasion = diningUtil.getOccasionName(occasion_id);

        is_event = event_id > 0;

        // load event info
        if (event_id > 0) {
            loadEventInfo(req, con_d);
        }
        if (location_id > 0) {
            loadLocationData(con_d);
        }
        if (close_con) {
                try {
                    con_d.close();
                } catch (Exception ignore) { }
        }

        loadOrganizationValues(con_d);
        
    }
    
    
    // Populate from request form
    public parmDining2(int orgId, HttpServletRequest req, Connection con_d) {

        fromRequest(orgId, req, con_d, null);
        loadOrganizationValues(con_d);
    }
    
    // Populate from request form
    public parmDining2(int orgId, HttpServletRequest req, Connection con_d, int o_event_id) {
        
        fromRequest(orgId, req, con_d, o_event_id);
        loadOrganizationValues(con_d);
    }

    // Populate from request form
    private void fromRequest(int orgId, HttpServletRequest req, Connection con_d, Integer o_event_id) {
        
        Connection con = Connect.getCon(req);
        
        String club = Utilities.getParameterString(req, "club", "");

        organization_id = orgId;
        
        boolean submit_slot = Utilities.getParameterString(req, "slot_submit_action", "").equals("update");
        boolean delete_slot = Utilities.getParameterString(req, "slot_submit_action", "").equals("delete");
        boolean remove_self = Utilities.getParameterString(req, "slot_submit_action", "").equals("remove_self");
        
        /*
        // Load any encrpted slot data
        String slot_hdata = Utilities.getParameterString(req, "slot_hdata", null);
        if(slot_hdata != null){
            slotId slot_id = new slotId(slot_hdata, Utilities.getParameterString(req, "slot_hkey", ""));
            if(slot_id.reservation_id == null){
                slot_data_error = true;
            } else {
                id = slot_id.reservation_id;
                event_id = slot_id.event_id;
                location_id = slot_id.location_id;
                date = slot_id.date;
                time = slot_id.time;
                //slot_id.maximum_players
            }
        } else if(submit_slot) {
            slot_data_error = true;
        } else {
         * 
         */
        
            event_id = Utilities.getParameterInteger(req, "event_id", 0);
            
            
            //person_id = Utilities.getParameterInteger(req, "reservation[person_id]", 0);
            location_id = Utilities.getParameterInteger(req, "reservation[location_id]", 0);
            date = Utilities.getIntDate(Utilities.getParameterString(req, "date", ""));
            time = Utilities.getIntTime(Utilities.getParameterString(req, "reservation[reservation_time]", ""));
            id = Utilities.getParameterInteger(req, "reservation_id", 0);
            
            
         /*    
        }
        */
        if(o_event_id != null){
            event_id = o_event_id;
        }
        // populate the parm block with the reservation details
            
        owner = Utilities.getParameterString(req, "owner", null);
        if(owner == null) {
            owner = reqUtil.getSessionString(req, "user", reqUtil.getSessionString(req, "ext-user", "")); 
        }
        owner_person_id = Utilities.getPersonId(owner, con);
        
        covers = Utilities.getParameterInteger(req, "covers", 0);
        occasion_id = Utilities.getParameterInteger(req, "reservation[occasion_id]", -1);
        charges = Utilities.getParameterString(req, "charges", "");
        member_special_requests = Utilities.getParameterString(req, "reservation[member_special_requests]", "").trim().replace("\"", "'");
        state = Utilities.getParameterString(req, "state", "");
        
        date_selected = req.getParameter("date_selected") != null;
        
        hide_check_numbers = (club.equals("misquamicut"));

        is_event = event_id > 0;
        
        boolean show_slot = req.getParameter("show_slot") != null;

        // load questions for this meal time or event
        loadQuestions(con_d);
        
        // Load names from submitted form, if any
        loadReservationNames(req, con_d, con);

        // load event info
        if (event_id > 0) {
            loadEventInfo(req, con_d);
        }
        if (location_id > 0) {
            loadLocationData(con_d);
        }
        
        if(location_id > 0 && time > 0 && date > 0 && covers > 0 && show_slot && !date_selected){
            stage = SLOT; 
        }
        if(submit_slot){
            stage = PROCESS;
        }
        if(delete_slot){
            stage = DELETE;
        }
        if(remove_self){
            stage = REMOVE_SELF;
        }
        
    }

    private int loadReservationData(Connection con_d, Connection con){
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        int result = OK;
        
         try {

            if (con_d != null) {

                pstmt = con_d.prepareStatement(""
                        + "SELECT *, to_char(date, 'YYYYMMDD')::int AS date_int, to_char(time, 'HH24MI')::int AS time_int "
                        + " FROM reservations "
                        + " WHERE id = ? "
                        + "  AND organization_id = ? "
                        + "  AND state <> 'cancelled'"
                        + "  AND parent_id IS NULL "); // and not canceled?

                pstmt.setInt(1, id);
                pstmt.setInt(2, organization_id);

                rs = pstmt.executeQuery();

                if (rs.next()) {

                    // populate the parm block with the reservation details
                    event_id = rs.getInt("event_id");
                    covers = rs.getInt("covers");
                    //parent_id = rs.getInt("parent_id");
                    location_id = rs.getInt("location_id");
                    reservations_count = rs.getInt("reservations_count");
                    special_requests = diningUtil.nullCheck(rs.getString("special_requests"),"");
                    occasion_id = rs.getInt("occasion_id");
                    spouse_reservation = rs.getBoolean("spouse_reservation");
                    charges = diningUtil.nullCheck(rs.getString("charges"),"");
                    date = rs.getInt("date_int");
                    time = rs.getInt("time_int");
                    reservation_number = rs.getInt("reservation_number");
                    cancellation_number = rs.getInt("cancellation_number");
                    cancelled_by_user_id = rs.getInt("cancelled_by_user_id");
                    member_special_requests = diningUtil.nullCheck(rs.getString("member_special_requests"),"");
                    state = rs.getString("state");
                    member_created = rs.getBoolean("member_created");
                    owner_person_id = rs.getInt("person_id");
                    owner = Utilities.getUserNameFromDiningId(owner_person_id, con);

                } else {
                    
                    result = RECORD_ERROR;
                    
                }

            }

        } catch (Exception exc) {
            
            Utilities.logError("parmDining2.loadReservationData: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
            result = DATABASE_ERROR;
            
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
    
    private void loadLocationData(Connection con_d) {

        diningLocation loc = diningUtil.getLocationData(con_d, location_id, event_id);

        if (loc != null) {
            location_name = loc.name;
            maximum_online_size = loc.max_online_size;
            maximum_party_size = loc.max_party_size;
        }
    }
    
    private void loadReservationNames(HttpServletRequest req, Connection con_d, Connection con) {

            String player, user, answer;
            int person_id;
            
            List<String> l_names = new ArrayList<String>();
            List<String> l_usernames = new ArrayList<String>();
            List<String> l_reservee_category = new ArrayList<String>();
            List<String> l_price_category = new ArrayList<String>();
            List<Integer> l_related_id = new ArrayList<Integer>();
            List<Integer> l_dining_id = new ArrayList<Integer>();
            List<Integer> l_check_num = new ArrayList<Integer>();
            List<Map<Integer, String>> l_answers = new ArrayList<Map<Integer, String>>();
            
            int i = 1;
            int check_number_offset = 0;
            Map<Integer, Integer> cn_offset_map = new HashMap<Integer, Integer>();
            boolean players_posted = false;
            int question_id;
            
            while(true){
                player = Utilities.getParameterString(req, "player"+i, null);
                if(player != null){
                    players_posted = true;
                    player = player.trim();
                    if(!player.isEmpty()){ // Only add non-blank players
                        user = Utilities.getParameterString(req, "user"+i, "");
                        person_id = Utilities.getPersonId(user, con);
                        
                        if(person_id == 0){
                            has_guests = true;
                        }
                        
                        l_names.add(player);
                        l_usernames.add(user);
                        l_reservee_category.add((person_id == 0) ? "guest" : "member");
                        l_price_category.add(Utilities.getParameterString(req, "meal_option"+i, ""));
                        l_dining_id.add(person_id);
                        l_related_id.add(0); // We'll figure this out later.  If it's still zero, then it's new
                        l_check_num.add(Utilities.getParameterInteger(req, "check_num"+i, 1));
                        Map<Integer, String> answer_map = new LinkedHashMap<Integer, String>();
                        for(Map.Entry<Integer,Question> question: questions.entrySet()){
                            question_id = question.getKey();
                            answer = Utilities.getParameterString(req, "answer"+i+"_"+question_id, null);
                            if(answer != null){
                                has_answers = true;
                                answer_map.put(question_id, answer);
                            }
                        }
                        l_answers.add(answer_map);
                        
                    } else {
                        // Empty player slot.  Change check number offset so we can correct check numbers later
                        check_number_offset --;
                    }
                    cn_offset_map.put(i, check_number_offset);
                } else {
                    break; // last player
                }
                i++;
                covers = l_names.size();
            }
            
            if(!players_posted){
                
                // No players posted.  Default to me
                user = reqUtil.getSessionString(req, "user", reqUtil.getSessionString(req, "ext-user", ""));
                player = reqUtil.getSessionString(req, "name", "");
                
                l_names.add(player);
                l_usernames.add(user);
                //l_usernames.add(null); 
                l_reservee_category.add("member"); 
                l_price_category.add("");     // extract the price category from the charges field
                l_related_id.add(id);
                l_dining_id.add(Utilities.getPersonId(user, con));
                l_check_num.add(Utilities.getParameterInteger(req, "check_num_"+(i+1), 0));
                Map<Integer, String> answer_map = new LinkedHashMap<Integer, String>();
                l_answers.add(answer_map);

            }
            
            // load group answers
            Map<Integer, String> answer_map = new LinkedHashMap<Integer, String>();
            
            for(Map.Entry<Integer,Question> question: group_questions.entrySet()){
                question_id = question.getKey();
                answer = Utilities.getParameterString(req, "group_answer"+question_id, null);
                if(answer != null){
                    has_answers = true;
                    answer_map.put(question_id, answer);
                }
            }
            group_answers = answer_map;

            // Convert lists to arrays in parmD
            check_num = l_check_num;
            dining_id = l_dining_id;
            related_id = l_related_id;
            names = l_names;
            usernames = l_usernames;
            reservee_category = l_reservee_category;
            price_category = l_price_category;
            answers = l_answers;

    }

    private void loadReservationNames(Connection con_d, Connection con) {

        List<String> l_names = new ArrayList<String>();
        List<String> l_usernames = new ArrayList<String>();
        List<String> l_reservee_category = new ArrayList<String>();
        List<String> l_price_category = new ArrayList<String>();
        List<Integer> l_related_id = new ArrayList<Integer>();
        List<Integer> l_dining_id = new ArrayList<Integer>();
        List<Integer> l_check_num = new ArrayList<Integer>();
        List<Map<Integer, String>> l_answers = new ArrayList<Map<Integer, String>>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String tmp;

        try {
            // load master reservation data

            pstmt = con_d.prepareStatement(""
                    + "SELECT person_id, check_number, reservee_name, parent_id, charges "
                    + " FROM reservations "
                    + " WHERE id = ? "
                    + "  AND state <> 'cancelled'"
                    + "  AND parent_id IS NULL ");

            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            
            if (rs.next()) {

                // Add first
                tmp = Utilities.getUserNameFromDiningId(rs.getInt("person_id"), con);
                if(tmp.isEmpty()){
                    l_names.add(diningUtil.nullCheck(rs.getString("reservee_name"),"")); // Guest
                } else {
                    l_names.add(diningUtil.getForteesName(tmp, con)); // Guest
                }
                l_usernames.add(tmp);
                
                //l_usernames.add(null); 
                l_reservee_category.add("member");
                if (event_id != 0) {
                    l_price_category.add(parmDiningCosts.extractPriceCategory(rs.getString("charges")));     // extract the price category from the charges field
                } else {
                    l_price_category.add("");
                }
                l_related_id.add(id); // if related_id == id, then we're the master
                l_dining_id.add(rs.getInt("person_id"));
                l_check_num.add(rs.getInt("check_number"));
                l_answers.add(diningUtil.getAnswers(id, questions, con_d));

            } else {
                
                
                
            }

            //Utilities.logError("parmDining: loaded " + tmp_name);

            // now find any related reservations
            pstmt = con_d.prepareStatement(""
                    + "SELECT id, person_id, check_number, reservee_name, charges "
                    + " FROM reservations "
                    + " WHERE parent_id = ? "
                    + "  AND state <> 'cancelled' "
                    + " ORDER BY check_number, id");

            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                int thisId = rs.getInt("person_id");
                // Add others
                tmp = diningUtil.nullCheck(Utilities.getUserNameFromDiningId(thisId, con),"");
                if(tmp.isEmpty()){
                    l_names.add(diningUtil.nullCheck(rs.getString("reservee_name"),"")); // Guest
                } else {
                    l_names.add(diningUtil.getForteesName(tmp, con)); // Member
                }
                l_usernames.add(tmp);
                l_reservee_category.add((thisId == 0) ? "guest" : "member");
                if(thisId == 0){
                    has_guests = true;
                }
                if (event_id != 0) {
                    l_price_category.add(parmDiningCosts.extractPriceCategory(rs.getString("charges")));     // extract the price category from the charges field
                } else {
                    l_price_category.add(null);
                }
                l_related_id.add(rs.getInt("id"));
                l_dining_id.add(thisId);
                l_check_num.add(rs.getInt("check_number"));
                l_answers.add(diningUtil.getAnswers(rs.getInt("id"), questions, con_d));

            }

        } catch (Exception exc) {
            
            reservation_load_error = "Unable to connect to dining system database.";

            Utilities.logError("parmDining2.loadReservationNames: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));

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

        // Convert lists to arrays in parmD
        check_num = l_check_num;
        dining_id = l_dining_id;
        related_id = l_related_id;
        names = l_names;
        usernames = l_usernames;
        reservee_category = l_reservee_category;
        price_category = l_price_category;
        answers = l_answers;
        group_answers = diningUtil.getAnswers(id, group_questions, con_d);

        covers = l_names.size();
        //names_found = l_names.size();

    }

    private void loadEventInfo(HttpServletRequest req, Connection con_d) {
        
        diningEvent e = new diningEvent(event_id, req, con_d);
        
        if(e.id > 0){
            event = e;
            //event_name = e.name;
            //costs = e.costs;
            date = e.date;
            //seatings = e.seatings;
            //time_format = e.time_format;
            if(location_id == 0){
                // Set default location
                location_id = e.location_id;
                location_name = e.location_name;
            }
        } else if (event_id > 0 && e.id == 0) {
            // Couldn't load event. What should we do?
            Utilities.logError("parmDining2.loadEventInfo Err=Unable to load event:"+event_id);
        }

    }

    private void loadQuestions(Connection con_d) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int oid = 0, count = 0, i = 0;

        String type = "";

        if (event_id == 0) {

            oid = getMealPeriodId(con_d);
            type = "MealPeriod";

        } else {

            oid = event_id;
            type = "Event";

        }

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT * "
                    + "FROM questions "
                    + "WHERE owner_id = ? AND owner_type = ?");

            pstmt.setInt(1, oid);
            pstmt.setString(2, type);

            rs = pstmt.executeQuery();
            
            Map<Integer,Question> questionMap = new LinkedHashMap<Integer,Question>();
            Map<Integer,Question> group_questionMap = new LinkedHashMap<Integer,Question>();

            // store them
            while (rs.next()) {
                
                if(rs.getBoolean("for_whole_party")){
                    group_questionMap.put(rs.getInt("id"),
                        new Question(
                                rs.getString("question_text"),
                                rs.getBoolean("requires_answer"),
                                rs.getBoolean("applies_to_guests_only"),
                                rs.getBoolean("for_whole_party")
                                ));
                } else {
                    questionMap.put(rs.getInt("id"),
                        new Question(
                                rs.getString("question_text"),
                                rs.getBoolean("requires_answer"),
                                rs.getBoolean("applies_to_guests_only"),
                                rs.getBoolean("for_whole_party")
                                ));
                }
                

            }
            
            questions = questionMap;
            group_questions = group_questionMap;

        } catch (Exception exc) {

            Utilities.logError("parmDining2.loadQuestions: Err=" + exc.toString());

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

    public int getMealPeriodId(Connection con_d) {

        int result = 0;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT id "
                    + "FROM meal_periods "
                    + "WHERE organization_id = ? "
                    + " AND location_id = ? "
                    + " AND available_"+timeUtil.getDayOfWeekLower(date)+" = true "
                    + " AND state = 'active' "
                    + " AND to_char(start_time, 'HH24MI')::int <= ? "
                    + " AND to_char(end_time, 'HH24MI')::int >= ? "
                    + "LIMIT 1");

            pstmt.setInt(1, organization_id);
            pstmt.setInt(2, location_id);
            pstmt.setInt(3, time);
            pstmt.setInt(4, time);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                result = rs.getInt("id");
            }

        } catch (Exception exc) {

            Utilities.logError("parmDining2.getMealPeriodId: Err=" + exc.toString());

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

    private void loadOrganizationValues(Connection con_d) {


        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT * FROM organizations WHERE id = ?");

            pstmt.setInt(1, organization_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                
                auto_email_members_for_dining_reservations = (rs.getBoolean("auto_email_members_for_dining_reservations")) ? 1 : 0;
                auto_email_members_for_event_reservations = (rs.getBoolean("auto_email_members_for_event_reservations")) ? 1 : 0;
            }

        } catch (Exception exc) {

            Utilities.logError("parmDining2.loadOrganizationValues: Err=" + exc.toString());

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
    
    public Integer getIndexByUser(String user){
        
        if(usernames != null){
            for(int i = 0; i<usernames.size();i++ ){
                if(usernames.get(i).equalsIgnoreCase(user)){
                    return i;
                }
            }
        }
        return null;
    }
    
    public Integer getIndexByPersonId(Integer lookup_id){
        
        if(dining_id != null){
            for(int i = 0; i<dining_id.size();i++ ){
                if(dining_id.get(i).equals(lookup_id)){
                    return i;
                }
            }
        }
        return null;
    }
    
    public Integer getIndexByRelatedId(Integer lookup_id){
        
        if(related_id != null){
            for(int i = 0; i<related_id.size();i++ ){
                if(related_id.get(i).equals(lookup_id)){
                    return i;
                }
            }
        }
        return null;
    }
    

}