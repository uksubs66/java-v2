/***************************************************************************************
 *   parmDining:  This class will hold all the data needed for a dining reservation.
 *
 *
 *   called by:  several
 *
 *   created: 05/17/2011   Paul S.
 *
 *   last updated:
 *
 *
 *      1/13/2014  ccyork - default to today for new reservations
 *      4/24/2013  Minikahda - default member calendar to today on new reservations
 *      1/29/2013  Updated populate with req so that it verifies related res are in db before adding
 *      1/27/2013  Added isUserMasterReservee method
 *     12/29/2012  Fixed & renamed onlyMasterReserveeCanEdit to canNonMasterReserveeEdit (and add bunch of debug info for available times check)
 *     10/31/2012  Peninsula Club - default member calendar to today on new reservations
 *     10/30/2012  Force trim of member special requests parameter
 *      8/30/2012  Added buildUserIdentity method
 *
 *
 *
 ***************************************************************************************
 */

package com.foretees.common;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashSet;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class parmDining {


    public int id = 0;
    public int event_id = 0;
    public int organization_id = 0;
    public int location_id = 0;
    public int person_id = 0;
    public int parent_id = 0;
    
    public int covers = 0;
    public int reservations_count = 0;
    public int number_of_checks = 0;

    public String special_requests = "";
    public int occasion_id = 0;

    public boolean spouse_reservation = false;
    public boolean bump_count = false;

    public int taken_by_user_id = 0;
    public int changed_by_user_id = 0;

    public String charges = "";
    public String table_number = "";
    public String table_assignments = "";
    public String category = "";
    public String sdate = "";
    public String day_of_week = "";
    public String location_name = "";
    public String occasion = "";
    public String costs = "";
    public String seatings = "";
    public String time_format = "";

    public int date = 0;
    public int time = 0;

    public int dining_maximum_online_size = 0;      // max online reservation count for a location
    public int dining_maximum_party_size = 0;       // max size for party as defined by the location

    public boolean walk_in = false;
    public boolean has_guests = false;
    public boolean allow_day_of = false;

    public int reservation_number = 0;
    public int cancellation_number = 0;

    public String confirmation_email = "";
    public String confirmation_status = "";

    public int cancelled_by_user_id = 0;
    public String member_special_requests = "";
    public boolean email_user = false;
    public boolean member_created = false;

    public String state = "";

    public String [] names;             // names in the reservation
    public String [] reservee_category;
    public String [] price_category;
    public int [] related_id;           // holds the uid for the related reservation
    public int [] check_num;            // check number for corresponding name in the reservation
    public int [] dining_id;            // dining_id of the corresponding name in the reservation


    public String [] question_text;
    public int [] question_id;
    public int [] question_requires_answer;
    public int [] question_guest_only;
    public int [] question_for_whole_party;
    public int question_count = 0;
    public String [][] answers;

    public int names_found = -1;

    public String event_name = "";
    public String event_default_location_name = "";
    public int event_default_location_id = 0;
    public String [][] event_locations;
    public int event_location_count = 0;

    public int maximum_party_size = 0;


 public void populate(parmDining parmD, Connection con_d) {


    boolean close_con = false;
        
    if (con_d == null) {
        
        con_d = Connect.getDiningCon();
        close_con = true;
    }
    
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        if(con_d != null) {

            pstmt = con_d.prepareStatement ("" +
                    "SELECT *, to_char(date, 'YYYYMMDD')::int AS date_int, to_char(time, 'HH24MI')::int AS time_int " +
                    "FROM reservations " +
                    "WHERE id = ?"); // and not canceled?

            pstmt.setInt(1, parmD.id);

            rs = pstmt.executeQuery();

            if ( rs.next() ) {

                // populate the parm block with the reservation details
                parmD.event_id = rs.getInt("event_id");
                parmD.person_id = rs.getInt("person_id");
                parmD.covers = rs.getInt("covers");
                parmD.parent_id = rs.getInt("parent_id");
                parmD.location_id = rs.getInt("location_id");
                parmD.reservations_count = rs.getInt("reservations_count");
                parmD.special_requests = rs.getString("special_requests");
                parmD.occasion_id = rs.getInt("occasion_id");
                parmD.spouse_reservation = rs.getBoolean("spouse_reservation");
                parmD.charges = rs.getString("charges");
                parmD.date = rs.getInt("date_int");
                parmD.time = rs.getInt("time_int");
                parmD.reservation_number = rs.getInt("reservation_number");
                parmD.cancellation_number = rs.getInt("cancellation_number");
                parmD.cancelled_by_user_id = rs.getInt("cancelled_by_user_id");
                parmD.member_special_requests = rs.getString("member_special_requests");
                parmD.state = rs.getString("state");
                parmD.member_created = rs.getBoolean("member_created");

            }

        }

    } catch (Exception exc) {

        Utilities.logError("parmDining.populate(db): Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

        if (close_con) {
            try { con_d.close(); }
            catch (Exception ignore) {}
        }

    }

    // ensure never null
    if (parmD.member_special_requests == null || parmD.member_special_requests.equals("null") == true) {

        parmD.member_special_requests = "";
    }

    if (parmD.date > 0) {

        parmD.sdate = Utilities.getDateFromYYYYMMDD(parmD.date, 2);
        setDayName(parmD);
        loadLocationData(parmD, con_d);
    }

    // load the names stored in this and its related reservations
    loadReservationNames(parmD, con_d);

    // get the occasion name
    parmD.occasion = getOccasionName(parmD.occasion_id);

    // load event info
    if (parmD.event_id > 0) {

        parmD.loadEventInfo(parmD, con_d);

        // set default if nessesary
        if (parmD.location_id == 0) {

            parmD.location_id = parmD.event_default_location_id;
            parmD.location_name = parmD.event_default_location_name;
        }
    }

    // load questions for this meal time or event
    loadQuestions(parmD, con_d);

 }


 public void populate(parmDining parmD, HttpServletRequest req, Connection con_d) {


    // populate the parm block with the reservation details
    parmD.event_id = (req.getParameter("event_id") != null) ? Integer.parseInt(req.getParameter("event_id")) : 0;
    parmD.person_id = (req.getParameter("reservation[person_id]") != null) ? Integer.parseInt(req.getParameter("reservation[person_id]")) : 0;
    parmD.covers = (req.getParameter("reservation[covers]") != null) ? Integer.parseInt(req.getParameter("reservation[covers]")) : 1;
    parmD.parent_id = (req.getParameter("parent_id") != null) ? Integer.parseInt(req.getParameter("parent_id")) : 0;
    parmD.location_id = (req.getParameter("reservation[location_id]") != null) ? Integer.parseInt(req.getParameter("reservation[location_id]")) : 0;
    parmD.reservations_count = (req.getParameter("reservations_count") != null) ? Integer.parseInt(req.getParameter("reservations_count")) : 0;
    parmD.special_requests = (req.getParameter("special_requests") != null) ? req.getParameter("special_requests") : "";
    parmD.occasion_id = (req.getParameter("reservation[occasion_id]") != null) ? Integer.parseInt(req.getParameter("reservation[occasion_id]")) : 0;
    parmD.spouse_reservation = (req.getParameter("spouse_reservation") != null && req.getParameter("spouse_reservation").equals("true")) ? true : false;
    parmD.charges = (req.getParameter("charges") != null) ? req.getParameter("charges") : "";
    parmD.date = (req.getParameter("date") != null) ? Integer.parseInt(req.getParameter("date")) : 0;

    parmD.reservation_number = (req.getParameter("reservation_number") != null) ? Integer.parseInt(req.getParameter("reservation_number")) : 0;
    parmD.cancellation_number = (req.getParameter("cancellation_number") != null) ? Integer.parseInt(req.getParameter("cancellation_number")) : 0;
    parmD.cancelled_by_user_id = (req.getParameter("cancelled_by_user_id") != null) ? Integer.parseInt(req.getParameter("cancelled_by_user_id")) : 0;
    parmD.member_special_requests = (req.getParameter("reservation[member_special_requests]") != null) ? req.getParameter("reservation[member_special_requests]").trim().replace("\"","'") : "";
    parmD.state = (req.getParameter("state") != null) ? req.getParameter("state") : "";

    //Utilities.logError("reservation[location_id]="+parmD.location_id);

    //parmD.date = (req.getParameter("reservation[date]") != null) ? Integer.parseInt(req.getParameter("reservation[date]")) : 0;

    //parmD.time = (req.getParameter("reservation[reservation_time]") != null) ? Integer.parseInt(req.getParameter("reservation[reservation_time]")) : 0;

    if (req.getParameter("reservation[reservation_time]") != null) {

        String tmp = req.getParameter("reservation[reservation_time]").replace(":", "");
        try {
            parmD.time = Integer.parseInt(tmp);
        } catch (Exception ignore) {
            parmD.time = 0;
        }
    }

    int days_in_advance_for_calendar = 1; // default the reservation date to tomorrow

    // default peninsula and minikahda and ccyork to today
    if (parmD.organization_id == 18 || parmD.organization_id == 15 || parmD.organization_id == 33) days_in_advance_for_calendar = 0;

    if (parmD.date == 0) {
        // should we default to tomorrow or today? (as of now our early clubs are not using the system to take reservations on day of)
        // for now I'm setting this date to tomorrow - user can always select 'today' if their club has times avilable
        parmD.date = (int)Utilities.getDate(null, days_in_advance_for_calendar);

    }

    if (parmD.date > 0) {
        
        parmD.sdate = Utilities.getDateFromYYYYMMDD(parmD.date, 2);
        setDayName(parmD);
        loadLocationData(parmD, con_d);
    }

    // we increase the arrays by this #
    // default to one because we're addressing its index starting with 1
    int tmp = 1;

    // check to see if the user is adding a name
    if (req.getParameter("bump_covers") != null && req.getParameter("bump_covers").equals("1")) {

        // bump both covers and the array sizes
        parmD.covers++;
        tmp++;
    }


    // load questions for this meal time or event
    loadQuestions(parmD, con_d);


    // size the arrays
    parmD.check_num = new int [parmD.covers + tmp];
    parmD.dining_id = new int [parmD.covers + tmp];
    parmD.related_id = new int [parmD.covers + tmp];
    parmD.names = new String [parmD.covers + tmp];
    parmD.reservee_category = new String [parmD.covers + tmp];
    parmD.price_category = new String [parmD.covers + tmp];
    parmD.answers = new String [parmD.covers + tmp][parmD.question_count + 1];

    // initalize the arrays
    for (int i = 1; i <= parmD.covers; i++) {
        parmD.names[i] = "";
        parmD.dining_id[i] = 0;
        parmD.check_num[i] = 0;
        parmD.related_id[i] = 0;
        parmD.reservee_category[i] = "";
        parmD.price_category[i] = "";
        for (int i2 = 1; i2 <= parmD.question_count; i2++) {
            parmD.answers[i][i2] = "";
        }
    }

    String name = "";

    if (req.getParameter("name_1") != null) {

        // get all the names
        int i = 1;  // value in req param
        int i2 = 1; // array
        int a = 0;  // answer index
        //String answer = "";
        //String tmp1 = "";
        boolean add_res = false;
        int test_related_id = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        do {

            name = (req.getParameter("name_" + i) != null) ? req.getParameter("name_" + i).trim() : "";
            
            add_res = false;
            if(!name.equals("")){
                test_related_id = (req.getParameter("reservation_id_for_person_" + i) != null) ? Integer.parseInt(req.getParameter("reservation_id_for_person_" + i)) : 0;
                if(test_related_id > 0){
                    // Check to make sure this relation_id is valid

                    try {
                        pstmt = con_d.prepareStatement ("" +
                                "SELECT id, person_id, check_number, reservee_name, charges " +
                                "FROM reservations " +
                                "WHERE id = ? AND state <> 'cancelled' " +
                                "ORDER BY check_number, id");

                        pstmt.setInt(1, test_related_id);

                        rs = pstmt.executeQuery();
                        
                        if ( rs.next() ) {
                            // Found a valid record
                            add_res = true;
                        }
                        
                    } catch (Exception exc) {

                        Utilities.logError("parmDining.loadReservationNames: Err=" + exc.toString());

                    } finally {

                        try { rs.close(); }
                        catch (Exception ignore) {}

                        try { pstmt.close(); }
                        catch (Exception ignore) {}

                    }
                    
                    if(!add_res){
                        // We need to remove one.
                        //parmD.covers --;
                    }

                } else {
                    // New relation -- no need to check yet, since it shouldn't have been sent to the dining server.
                    add_res = true;
                }
            }

            if (add_res) {

                parmD.names[i2] = name;

                // if data ever contains more than the person_id from users member2b record then we'll need to isolate it
                try {
                    parmD.dining_id[i2] = (req.getParameter("data_" + i) != null) ? Integer.parseInt(req.getParameter("data_" + i)) : 0;
                } catch (Exception exc) {
                    Utilities.logError("parmDining: data_"+i+"="+req.getParameter("data_" + i));
                }/*
                try {
                    parmD.related_id[i2] = (req.getParameter("related_reservations[" + i + "][reservation_id]") != null) ? Integer.parseInt(req.getParameter("related_reservations[" + i + "][reservation_id]")) : 0;
                } catch (Exception exc) {
                    Utilities.logError("parmDining: related_reservations[" + i + "][reservation_id]="+req.getParameter("related_reservations[" + i + "][reservation_id]"));
                }*/
                parmD.related_id[i2] = (req.getParameter("reservation_id_for_person_" + i) != null) ? Integer.parseInt(req.getParameter("reservation_id_for_person_" + i)) : 0;
                parmD.check_num[i2] = (req.getParameter("check_num_for_person_" + i) != null) ? Integer.parseInt(req.getParameter("check_num_for_person_" + i)) : 0;
                parmD.reservee_category[i2] = (parmD.dining_id[i2] == 0) ? "guest" : "member";
                if (parmD.dining_id[i2] == 0) has_guests = true;
                parmD.price_category[i2] = (req.getParameter("related_reservations[" + i + "][price_category]") != null) ? req.getParameter("related_reservations[" + i + "][price_category]") : "";


/*
                // if there is at least one answer then dig deeper  // answers[1][question_id]
                //tmp1 = (i == 1) ? "answers[1][question_id]" : "related_reservations[" + i + "]answers[0]";

                if (parmD.question_count > 0) { // req.getParameter(tmp1) != null

                    // at least one answer present
                    do {

                        if (i == 1) {

                            parmD.answers[i2][a] = (req.getParameter("answers[" + a + "][answer_text]") != null) ? req.getParameter("answers[" + a + "][answer_text]").trim() : "";

                        } else {

                            parmD.answers[i2][a] = (req.getParameter("related_reservations[" + i + "]answers[" + a + "][answer_text]") != null) ? req.getParameter("related_reservations[" + i + "]answers[" + a + "][answer_text]").trim() : "";
                        
                        }

                        a++;

                    } while (req.getParameter("related_reservations[" + i + "]answers[" + a + "][question_id]") != null);

                    a = 0; // reset
                }
*/
                for (int q = 1; q <= parmD.question_count; q++) {

                    if (i == 1) {

                        // was parmD.answers[i2][q] which should always be...
                        parmD.answers[1][q] = (req.getParameter("answers[" + q + "][answer_text]") != null) ? req.getParameter("answers[" + q + "][answer_text]").trim() : "";

                    } else {

                        parmD.answers[i2][q] = (req.getParameter("related_reservations[" + i + "]answers[" + q + "][answer_text]") != null) ? req.getParameter("related_reservations[" + i + "]answers[" + q + "][answer_text]").trim() : "";

                    }

                }

                i2++;

            } // end if names is not empty

            i++;

        } while (req.getParameter("name_" + i) != null);

        i2--;
        parmD.names_found = i2;

    } // end checking for name1 parameter


    // get the occasion name
    parmD.occasion = getOccasionName(parmD.occasion_id);

    // count the checks
    ArrayList<Integer> al = new ArrayList<Integer>();
    //Collections.addAll(al, parmD.check_num);
    for (int cn : parmD.check_num) {
        if (cn!=0) al.add(cn);
    }

    HashSet<Integer> hs = new HashSet<Integer>();
    hs.addAll(al);
    al.clear();
    al.addAll(hs);

    parmD.number_of_checks = al.size();

    // load event info
    if (parmD.event_id > 0) {
    
        parmD.loadEventInfo(parmD, con_d);

        // set default if nessesary
        if (parmD.location_id == 0) {

            parmD.location_id = parmD.event_default_location_id;
            parmD.location_name = parmD.event_default_location_name;
        }
    }

 }


 public static int getMaxDays(int organization_id, Connection con_d) {


    // look up the days in advance for the club
    // as of now this is only used to determin the max days
    // in advance for a club when we don't want to narrow it to a
    // specific dining location

    int days = 0;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con_d.prepareStatement ("" +
                "SELECT MAX(dining_maximum_advance_days) AS max_days " +
                "FROM locations " +
                "WHERE " +
                    "organization_id = ? AND " +
                    "dining_members_can_make_reservations = true;");

        pstmt.setInt(1, organization_id);

        rs = pstmt.executeQuery();

        if ( rs.next() ) days = rs.getInt("max_days");

    } catch (Exception exc) {

        Utilities.logError("parmDining.getMaxDays: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

     return days;

 }

 public static DaysAdv daysInAdv(DaysAdv daysArray, int organization_id, String club, String user, Connection con, Connection con_d) {



    int days = getMaxDays(organization_id, con_d);

    daysArray.maxview = days;

    //
    //  init the array (1 entry per day, relative to today)
    //
    for (int index = 0; index < daysArray.MAXDAYS; index++) {

        if (days >= index) {

            daysArray.days[index] = 1;        // set ok in array

        } else {

            // default to no access
            daysArray.days[index] = 0;        // set to no access in array

            // perform any other nesseary checks here

            // if (club is ?) then check if this day is a weekend and allow reservations weeks further then weekdays

        }

    }

    return daysArray;

 }


 public void loadLocationData (parmDining parmD, Connection con_d) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con_d.prepareStatement ("" +
                "SELECT name, dining_maximum_online_size, dining_maximum_party_size " +
                "FROM locations " +
                "WHERE id = ?");

        pstmt.setInt(1, parmD.location_id);

        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            // populate the parm block with the reservation details
            parmD.location_name = rs.getString("name");
            parmD.dining_maximum_online_size = rs.getInt("dining_maximum_online_size");
            parmD.dining_maximum_party_size = rs.getInt("dining_maximum_party_size");

        }

    } catch (Exception exc) {

        Utilities.logError("parmDining.loadLocationData: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

 }


 private void setDayName(parmDining parmD) {


    int year = parmD.date / 10000;
    int month = (parmD.date - (year * 10000)) / 100;
    int day = (parmD.date - (year * 10000)) - (month * 100);


    Calendar cal = Calendar.getInstance();
    cal.set(year, month - 1, day);

    parmD.day_of_week = ProcessConstants.DAYS_OF_WEEK[cal.get(Calendar.DAY_OF_WEEK) - 1];
    
 }


 private void loadReservationNames (parmDining parmD, Connection con_d) {


    // if parmD.id contains a reservation_id then load it from the db
    // but if it's zero then we are here for a new (probably event) reservation
    // so manually size the arrays and set the required values
    if (parmD.id == 0) {

        //Utilities.logError("parmDining: loadReservationNames: defaulting to size of 1 but empty, id=" + parmD.id);

        // size the arrays to allow for the initial member (user) to be added - (we address this array starting with 1 not zero)
        parmD.check_num = new int [2];
        parmD.dining_id = new int [2];
        parmD.related_id = new int [2];
        parmD.names = new String [2];
        parmD.reservee_category = new String [2];
        parmD.price_category = new String [2];

        // initalize the arrays
        parmD.names[1] = "";
        parmD.dining_id[1] = 0;
        parmD.check_num[1] = 0;
        parmD.related_id[1] = 0;
        parmD.reservee_category[1] = "";
        parmD.price_category[1] = "";
        parmD.covers = 1;
        parmD.names_found = 0;

    } else {

        //Utilities.logError("parmDining: loadReservationNames: loading reservation_id=" + parmD.id);

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        int tmp_id = 0, master_res_id = 0, count = 0;

        try {

            pstmt = con_d.prepareStatement ("" +
                    "SELECT parent_id " +
                    "FROM reservations " +
                    "WHERE id = ?");

            pstmt.setInt(1, parmD.id);

            rs = pstmt.executeQuery();

            if ( rs.next() ) {

                tmp_id = rs.getInt(1);

                if (tmp_id == 0) {

                    // this is the master reservation
                    master_res_id = parmD.id;

                } else {

                    // this is a related reservation - the master is its parent_id
                    master_res_id = tmp_id;

                }

            }


            //
            // now load master reservation data
            String tmp_name = "", tmp_charges = "";
            int tmp_person_id = 0;
            int tmp_check_num = 0;

            pstmt = con_d.prepareStatement ("" +
                    "SELECT person_id, check_number, reservee_name, parent_id, charges " +
                    "FROM reservations " +
                    "WHERE id = ?");

            pstmt.setInt(1, master_res_id);

            rs = pstmt.executeQuery();

            if ( rs.next() ) {

                tmp_name = rs.getString("reservee_name");
                tmp_person_id = rs.getInt("person_id");
                tmp_check_num = rs.getInt("check_number");
                tmp_charges = rs.getString("charges");

                count = 1;

            }

            //Utilities.logError("parmDining: loaded " + tmp_name);

            //
            // now find any related reservations
            pstmt = con_d.prepareStatement ("" +
                    "SELECT id, person_id, check_number, reservee_name, charges " +
                    "FROM reservations " +
                    "WHERE parent_id = ? AND state <> 'cancelled' " +
                    "ORDER BY check_number, id");

            pstmt.setInt(1, master_res_id);

            rs = pstmt.executeQuery();
    /*
            if (rs.next()) {

               rs.last();
               count = rs.getRow();
               rs.beforeFirst();
            }

    */

            // ugly but having problem with scrollable cursor in postgres jdbc
            while ( rs.next() ) {

                count++;
            }

            //Utilities.logError("parmDining: found " + count);

            rs = pstmt.executeQuery();

            // size the arrays
            count++;
            parmD.check_num = new int [count];
            parmD.dining_id = new int [count];
            parmD.related_id = new int [count];
            parmD.names = new String [count];
            parmD.reservee_category = new String [count];
            parmD.price_category = new String [count];


            // put the tmp vars in we loaded from the master reservation
            parmD.names[1] = tmp_name;
            parmD.related_id[1] = parmD.id;
            parmD.dining_id[1] = tmp_person_id;
            parmD.check_num[1] = tmp_check_num;
            parmD.reservee_category[1] = "member";  // first name will always be a member(?)
            if (parmD.event_id != 0) {
                parmD.price_category[1] = parmDiningCosts.extractPriceCategory(tmp_charges);     // extract the price category from the charges field
            }
            tmp_id = 1; // reuse

            while ( rs.next() ) {

                tmp_id++;

                parmD.names[tmp_id] = rs.getString("reservee_name");
                parmD.dining_id[tmp_id] = rs.getInt("person_id");
                parmD.check_num[tmp_id] = rs.getInt("check_number");
                parmD.related_id[tmp_id] = rs.getInt("id");
                parmD.reservee_category[tmp_id] = (parmD.dining_id[tmp_id] == 0) ? "guest" : "member";
                if (parmD.dining_id[tmp_id] == 0) parmD.has_guests = true;
                if (parmD.event_id != 0) {
                    parmD.price_category[tmp_id] = parmDiningCosts.extractPriceCategory(rs.getString("charges"));
                }
            }

        } catch (Exception exc) {

            Utilities.logError("parmDining.loadReservationNames: Err=" + exc.toString());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

        parmD.covers = tmp_id;
        parmD.names_found = tmp_id;

    }

 }


 public static String getOccasionName(int id) {

    if (id == 1) {

        return "Birthday";

    } else if (id == 2) {

        return "Anniversary";

    } else if (id == 3) {

        return "Graduation";

    } else if (id == 4) {

        return "Promotion";

    } else if (id == 5) {

        return "Groom's Dinner";

    } else if (id == 6) {

        return "Engagement";

    } else if (id == 7) {

        return "Wedding Reception";

    } else if (id == 8) {

        return "Special Occasion";

    } else {

        return "";

    }
    
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

            pstmt = con_d.prepareStatement ("" +
                    "SELECT user_identity " +
                    "FROM people " +
                    "WHERE id = ?");

            pstmt.setInt(1, person_id);

            rs = pstmt.executeQuery();

            if ( rs.next() ) {

                result = rs.getString(1);

            }

        } catch (Exception exc) {

            Utilities.logError("parmDining.getUserIdentity: Err=" + exc.toString());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

            if (close_con) {
                try { con_d.close(); }
                catch (Exception ignore) {}
            }

        }

    }

    return result;

 }


 private void loadEventInfo (parmDining parmD, Connection con_d) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String err_tmp = "1";

    try {

        pstmt = con_d.prepareStatement ("" +
                        "SELECT e.name, e.members_can_make_reservations, " +
                            "to_char(e.start_time, 'HH24:MI') AS time1, " +
                            "to_char(e.start_time, 'HH24MI') AS stime, " +
                            "to_char(e.end_time, 'HH24MI') AS etime, " +
                            "to_char(e.date, 'YYYYMMDD')::int AS our_date, " +
                            "to_char(e.date, 'MM/DD/YYYY') AS date1, " +
                            "e.costs, e.seatings, loc.name AS location_name, " +
                            "e.start_time, e.date, e.maximum_party_size, e.location_id," +
                            "e.time_format " +
                        "FROM events e " +
                        "LEFT OUTER JOIN locations AS loc ON e.location_id = loc.id " +
                        "WHERE e.id = ?");

        pstmt.setInt(1, parmD.event_id);

        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            parmD.event_name = rs.getString("name");
            parmD.event_default_location_id = rs.getInt("location_id");
            parmD.event_default_location_name = rs.getString("location_name");
            parmD.costs = rs.getString("costs");

            parmD.maximum_party_size = rs.getInt("maximum_party_size");
            parmD.date = rs.getInt("our_date");
          //parmD.location_id = rs.getInt("location_id");
          //parmD.location_name = rs.getString("location_name");
            parmD.sdate = rs.getString("date1");

            parmD.costs = rs.getString("costs");
            parmD.seatings = rs.getString("seatings");
            parmD.time_format = rs.getString("time_format");



            //parmD.time = rs.getInt("stime");  ALL READY SPECIFIED WITH THE ACTUAL RESERVAITON

            /*
            // extract the cost for the event
            int pos1 = 0, pos2 = 0;
            pos1 = costs.indexOf("amount: ");
            pos2 = costs.indexOf(" currency");
            cost = costs.substring(pos1 + 7, pos2);
            cost = cost.trim();
            if (cost.endsWith(".0")) cost += "0";
            */
        }

        err_tmp = "2";
        
        pstmt = con_d.prepareStatement ("" +
                        "SELECT b.covers, b.maximum_online_size, b.location_id, loc.name AS location_name " +
                        "FROM bookings b " +
                        "LEFT OUTER JOIN locations AS loc ON b.location_id = loc.id " +
                        "WHERE b.event_id = ?");

        pstmt.setInt(1, parmD.event_id);
        
        parmD.event_location_count = 0;

        rs = pstmt.executeQuery();

        while ( rs.next() ) {
            parmD.event_location_count++;
        }

        if (parmD.event_location_count > 0) {

            parmD.event_locations = new String [parmD.event_location_count][5];

            int i = 0;
            rs = pstmt.executeQuery();

            while ( rs.next() ) {

                // load location data in to arrays
                parmD.event_locations[i][0] = rs.getString("location_name");
                parmD.event_locations[i][1] = rs.getString("covers");
                parmD.event_locations[i][2] = rs.getString("maximum_online_size");
                parmD.event_locations[i][3] = rs.getString("location_id");
                parmD.event_locations[i][4] = "0";

                i++;
            }

        }

    } catch (Exception exc) {

        Utilities.logError("parmDining.loadEventInfo: event_location_count=" + parmD.event_location_count + ", qry=" + err_tmp + ", Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

 }


 public static boolean isEventOpen(int organization_id, int event_id, Connection con, Connection con_d) {


    boolean result = false;

    PreparedStatement pstmt = null;
    ResultSet rs = null;


    String format = "";
    int min_adv_days = 0, max_adv_days = 0, event_date = 0;
    int today = (int)Utilities.getDate(con);
    boolean members_can_book = false;

    // first we load some basic event info to determine if the event is open yet (date checking)
    // and get the seating type so we know how to count covers

    try {

        pstmt = con_d.prepareStatement ("" +
                        "SELECT e.time_format, e.members_can_make_reservations, " +
                            "minimum_advance_days, maximum_advance_days, " +
                            "to_char(e.date, 'YYYYMMDD')::int AS our_date " +
                        "FROM events e " +
                        "WHERE e.id = ?");

        pstmt.setInt(1, event_id);

        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            format = rs.getString("time_format");
            min_adv_days = rs.getInt("minimum_advance_days");
            max_adv_days = rs.getInt("maximum_advance_days");
            members_can_book = rs.getBoolean("members_can_make_reservations");
            event_date = rs.getInt("our_date");

            // first check to see if we are within the signup window
            if ( (Utilities.getDate(event_date, (max_adv_days * -1)) <= today) || (Utilities.getDate(event_date, (min_adv_days * -1)) >= today) ) {

                result = true;
            }/* else {
                Utilities.logError("parmDining.isEventOpen: event_id " + event_id + " not open for signup.");
            }*/

        }

    } catch (Exception exc) {

        Utilities.logError("parmDining.isEventOpen: load basic data. event_id=" + event_id + ", Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }




    if (result) {

        result = false; // reset - set true againb once we find at least one reservation time remaining for the event
/*
        if (format.equals("single")) {


        } else if (format.equals("mulitple")) {



        } else {

            // ongoing

        }
*/

        //Utilities.logError("parmDining.isEventOpen: Doing location count. event_id=" + event_id);

        int location_count = 0;

        try {

            pstmt = con_d.prepareStatement ("" +
                            "SELECT b.covers, b.maximum_online_size, b.location_id, loc.name AS location_name " +
                            "FROM bookings b " +
                            "LEFT OUTER JOIN locations AS loc ON b.location_id = loc.id " +
                            "WHERE b.event_id = ?");

            pstmt.setInt(1, event_id);

            location_count = 0;

            rs = pstmt.executeQuery();

            while ( rs.next() ) { location_count++; }

//          int i = 0;

            if (location_count > 0) {

                rs = pstmt.executeQuery();

//              int[][] locations = new int [location_count][5];

                loc_check:
                while ( rs.next() ) {

                    String times = getLocationTimesFromDining(organization_id, rs.getInt("location_id"), event_date, event_id, "", "");

                    //Utilities.logError("parmDining.isEventOpen: Checking time for event " + event_id + " and location " + rs.getInt("location_id") + " times=" + times);

                    if (times.indexOf(":") > 0) {

                        // at least one time was found for this location
                        // we now can break out of this method and return a positive value
                        // indicating signups are allowed
                        result = true;
                       // Utilities.logError("parmDining.isEventOpen:event " + event_id + " and location " + rs.getInt("location_id") + " is open - breaking loop");
                        break;

                    } else {
                        //Utilities.logError("parmDining.isEventOpen:event " + event_id + " and location " + rs.getInt("location_id") + " is closed");

                    }
/*
                    // load location data in to arrays
                    locations[i][0] = rs.getInt("location_id");
                    locations[i][1] = rs.getInt("covers");
                    locations[i][2] = rs.getInt("maximum_online_size");

                    i++;
*/
                }

            } else {

                //Utilities.logError("parmDining.isEventOpen: NO LOCATIONS FOUND SO NOT CHECKING COUNTS. event_id=" + event_id);

            }

        } catch (Exception exc) {

            Utilities.logError("parmDining.isEventOpen: location count checks. event_id=" + event_id + ", Err=" + exc.toString());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

    } // end location count checks

    return result;

 }


 private boolean loadQuestions(parmDining parmD, Connection con_d) {


    boolean result = false;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int id = 0, count = 0, i = 0;
    
    String type = "";

    if (parmD.event_id == 0) {

        id = getMealPeriodId(parmD, con_d);
        type = "MealPeriod";

    } else {

        id = parmD.event_id;
        type = "Event";

    }

    try {

        pstmt = con_d.prepareStatement ("" +
                "SELECT * " +
                "FROM questions " +
                "WHERE owner_id = ? AND owner_type = ?");

        pstmt.setInt(1, id);
        pstmt.setString(2, type);

        rs = pstmt.executeQuery();

        // ugly but having problem with scrollable cursor in postgres jdbc
        while ( rs.next() ) {

            count++;
        }

        // size the question arrays
        count++;
        parmD.question_id = new int [count];
        parmD.question_text = new String [count];
        parmD.question_requires_answer = new int [count];
        parmD.question_guest_only = new int [count];
        parmD.question_for_whole_party = new int [count];

        rs = pstmt.executeQuery();

        // store them
        while ( rs.next() ) {

            i++;

            parmD.question_id[i] = rs.getInt("id");
            parmD.question_text[i] = rs.getString("question_text");
            parmD.question_requires_answer[i] = (rs.getBoolean("requires_answer") == true) ? 1 : 0;
            parmD.question_guest_only[i] = (rs.getBoolean("applies_to_guests_only") == true) ? 1 : 0;
            parmD.question_for_whole_party[i] = (rs.getBoolean("for_whole_party") == true) ? 1 : 0;
            
        }

        parmD.question_count = i;

    } catch (Exception exc) {

        Utilities.logError("parmDining.loadQuestions: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return result;

 }


 public static void loadAnswers(parmDining parmD, Connection con_d) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        // reset the answers arrays
        //parmD.answers = new String [parmD.covers + tmp][parmD.question_count + 1];

        for (int i = 1; i <= parmD.covers; i++) {
            for (int i2 = 1; i2 <= parmD.question_count; i2++) {
                parmD.answers[i][i2] = "";
            }
        }


        // load each answer for each person
        for (int i = 1; i <= parmD.names_found; i++) {

            for (int q = 1; q <= parmD.question_count; q++) {

                pstmt = con_d.prepareStatement ("" +
                        "SELECT * " +
                        "FROM answers " +
                        "WHERE reservation_id = ? AND question_id = ?");

                pstmt.setInt(1, (i == 1) ? parmD.id : parmD.related_id[i] );
                pstmt.setInt(2, parmD.question_id[q]);

                rs = pstmt.executeQuery();

                // store them
                while ( rs.next() ) {

                    parmD.answers[i][q] = rs.getString("answer_text");

                }

            } // end question loop

        } // end covers loop

    } catch (Exception exc) {

        Utilities.logError("parmDining.loadAnswers: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

 }
 
 
 public static int getMealPeriodId(parmDining parmD, Connection con_d) {


    int result = 0;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con_d.prepareStatement ("" +
                "SELECT id " +
                "FROM meal_periods " +
                "WHERE " +
                    "organization_id = ? AND " +
                    "location_id = ? AND " +
                    "available_" + parmD.day_of_week + " = true AND " +
                    "state = 'active' AND " +
                    "to_char(start_time, 'HH24MI')::int <= ? AND " +
                    "to_char(end_time, 'HH24MI')::int >= ? " +
                "LIMIT 1");

        pstmt.setInt(1, parmD.organization_id);
        pstmt.setInt(2, parmD.location_id);
        pstmt.setInt(3, parmD.time);
        pstmt.setInt(4, parmD.time);

        rs = pstmt.executeQuery();

        if ( rs.next() ) result = rs.getInt(1);

    } catch (Exception exc) {

        Utilities.logError("parmDining.getMealPeriodId: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return result;

 }


 public boolean foundQuestions(parmDining parmD, Connection con_d) {


    boolean result = false;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int id = 0;
    String type = "";



    try {

        pstmt = con_d.prepareStatement ("" +
                "SELECT * " +
                "FROM questions" +
                "WHERE owner_id = ? AND owner_type = ? " +
                "LIMIT 1");

        pstmt.setInt(1, id);
        pstmt.setString(1, type);

        rs = pstmt.executeQuery();

        if ( rs.next() ) result = true;

    } catch (Exception exc) {

        Utilities.logError("parmDining.foundQuestions: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return result;

 }


 public static boolean questionsToAsk(parmDining parmD) {


    boolean result = false;

    // check each question
    for (int i = 1; i <= parmD.question_count; i++) {

        // is question for party or each person
        if (parmD.question_for_whole_party[i] == 0) {

            // this question needs to be asked to each person
            for (int i2 = 1; i2 <= parmD.covers; i2++) {

                // if question is for guests only then only ask if this person is not a member
                if (parmD.question_guest_only[i] == 0 || (parmD.question_guest_only[i] == 1 && parmD.dining_id[i2] == 0)) {

                    result = true;
                    break;

                }
                
            }

        } else {

            // if this question for the dining party
            // but if it's for guests only then we only ask it if the dining party contains at least one guest
            if (parmD.question_guest_only[i] == 0 || (parmD.question_guest_only[i] == 1 && parmD.has_guests)) {

                result = true;
                break;

            }

        }

    }

    return result;

 }


 public static boolean areDiningReservationsPublic (int organization_id, Connection con_d) {


    boolean result = false; // default to not being able to see them
    boolean close_con = false;

    if (con_d == null) {

        con_d = Connect.getDiningCon();
        close_con = true;
    }

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con_d.prepareStatement ("" +
                "SELECT members_can_see_other_members_dining_reservations " +
                "FROM organizations " +
                "WHERE id = ?");

        pstmt.setInt(1, organization_id);

        rs = pstmt.executeQuery();

        if ( rs.next() ) result = rs.getBoolean(1);

    } catch (Exception exc) {

        Utilities.logError("parmDining.areDiningReservationsPublic: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

        if (close_con) {
            try { con_d.close(); }
            catch (Exception ignore) {}
        }

    }

    return result;

 }


 public static boolean areEventReservationsPublic (int organization_id, Connection con_d) {


    boolean result = false; // default to not being able to see them
    boolean close_con = false;

    if (con_d == null) {

        con_d = Connect.getDiningCon();
        close_con = true;
    }

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con_d.prepareStatement ("" +
                "SELECT members_can_see_other_members_event_reservations " +
                "FROM organizations " +
                "WHERE id = ?");

        pstmt.setInt(1, organization_id);

        rs = pstmt.executeQuery();

        if ( rs.next() ) result = rs.getBoolean(1);

    } catch (Exception exc) {

        Utilities.logError("parmDining.areEventReservationsPublic: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

        if (close_con) {
            try { con_d.close(); }
            catch (Exception ignore) {}
        }

    }

    return result;

 }

// canNonMasterReserveeEdit
 public static boolean canNonMasterReserveeEdit (int organization_id, Connection con_d) {


    boolean result = false; // default to only allowing the reservation maker (master reservee) to edit the reservation - true means anyone in that reservation can make changes to it
    boolean close_con = false;

    if (con_d == null) {

        con_d = Connect.getDiningCon();
        close_con = true;
    }

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con_d.prepareStatement ("" +
                "SELECT members_can_edit_other_reservees_on_same_check " +
                "FROM organizations " +
                "WHERE id = ?");

        pstmt.setInt(1, organization_id);

        rs = pstmt.executeQuery();

        if ( rs.next() ) result = rs.getBoolean(1);

    } catch (Exception exc) {

        Utilities.logError("parmDining.canNonMasterReserveeEdit: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

        if (close_con) {
            try { con_d.close(); }
            catch (Exception ignore) {}
        }

    }

    return result;

 }


 public static boolean isUserMasterReservee (int reservation_id, int person_id, Connection con_d) {


    boolean result = false;
    boolean close_con = false;

    if (con_d == null) {

        con_d = Connect.getDiningCon();
        close_con = true;
    }

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con_d.prepareStatement ("" +
                "SELECT id " +
                "FROM reservations " +
                "WHERE id = ? AND parent_id IS NULL");

        pstmt.setInt(1, reservation_id);

        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            result = true; // rs.getInt("taken_by_user_id") == Utilities.getUserId(person_id);
        }

    } catch (Exception exc) {

        Utilities.logError("parmDining.isUserMasterReservee: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

        if (close_con) {
            try { con_d.close(); }
            catch (Exception ignore) {}
        }

    }

    return result;

 }


 public static boolean anyLocationAllowDayOf (int organization_id, Connection con_d) {


    boolean result = false;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con_d.prepareStatement ("" +
                "SELECT id " +
                "FROM locations " +
                "WHERE " +
                    "organization_id = ? AND " +
                    "dining_minimum_advance_days = 0 AND " +
                    "dining_members_can_make_reservations = true AND " +
                    "deactivated = false");

        pstmt.setInt(1, organization_id);

        rs = pstmt.executeQuery();

        if ( rs.next() ) result = true;

    } catch (Exception exc) {

        Utilities.logError("parmDining.anyLocationAllowDayOf: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}
        
    }

    return result;

 }


 public static ArrayList<String[]> getDiningLocations (int organization_id, int days_in_advance, int location_id, Connection con_d) {


    //if(con_d == null) con_d = Connect.getDiningCon();

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    ArrayList<String[]> result = new ArrayList<String[]>();

    try {

        //if(con_d != null) {

            pstmt = con_d.prepareStatement ("" +
                    "SELECT id, name " +
                    "FROM locations " +
                    "WHERE " +
                        "organization_id = ? AND " +
                        "dining_maximum_advance_days >= ? AND " +
                        "dining_minimum_advance_days <= ? AND " +
                        "dining_members_can_make_reservations = true AND " +
                        "deactivated = false");

            pstmt.setInt(1, organization_id);
            pstmt.setInt(2, days_in_advance);
            pstmt.setInt(3, days_in_advance);

            rs = pstmt.executeQuery();

            while ( rs.next() ) {

                // add string array containing id and name of location
                result.add( new String [] {rs.getString(1), rs.getString(2)});
                
            }

            // make sure that this location is always returned (location_id is passed in when editing a reservation)
            if (location_id > 0 && result.size() == 0) {

                // go back and load at least this one location in to the array
                pstmt = con_d.prepareStatement ("" + 
                        "SELECT id, name " +
                        "FROM locations " +
                        "WHERE " +
                            "id = ? AND " +
                            "organization_id = ? AND " +
                            "deactivated = false");

                pstmt.setInt(1, location_id);
                pstmt.setInt(2, organization_id);

                rs = pstmt.executeQuery();

                if ( rs.next() ) {

                    // add string array containing id and name of location
                    result.add( new String [] {rs.getString(1), rs.getString(2)});

                }

            }

        //}

    } catch (Exception exc) {

        Utilities.logError("parmDining.getDiningLocations: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

        //try { con_d.close(); }
        //catch (Exception ignore) {}

    }

    return result;

 }

 public static String getLocationTimesFromDining (int organization_id, int location_id, int date, int event_id, String time) {

     return getLocationTimesFromDining (organization_id, location_id, date, event_id, time, "");
 }


 public static String getLocationTimesFromDining (int organization_id, int location_id, int date, int event_id, String time, String mode) {

    StringBuilder result = new StringBuilder();
    BufferedReader in = null;

    String tmp = "";
    int i = 0;

    try {

        // example
        // http://dining.foretees.com/portal/reservations/available_time_options?reservation_category=event&organization_id=1&event_id=29&location_id=1&reservation_date=2012-03-04

        // create a URL for the desired page
        tmp = "" +
                "http://10.0.0.82/portal/reservations/available_time_options" + mode + "?" +
                "reservation_category=" + ((event_id == 0) ? "dining" : "event") + "&" +
                ((event_id == 0) ? "" : "event_id=" + event_id + "&") +
                "organization_id=" + organization_id + "&" +
                "location_id=" + location_id + "&" +
                "reservation_date=" + Utilities.getDateFromYYYYMMDD(date, 1) + "&" +
                "reservation_time=" + time;

        //Utilities.logError("parmD.getLocationTimesFromDining() " + tmp);

        URL url = new URL(tmp);

        // perform simple http get call to url so we can read the response by the server
        in = new BufferedReader(new InputStreamReader(url.openStream()));

        // str is one line of text; 
        String str;

        while ((str = in.readLine()) != null) { // readLine() strips the newline character(s)
            i++;
            result.append(str);
        }

    } catch (Exception exc) {

        Utilities.logError("parmD.getLocationTimesFromDining() Error: " + exc.toString());

    } finally {

        try { in.close(); }
        catch (Exception ignore) {}

    }

    //Utilities.logError("parmD.getLocationTimesFromDining() i=" + i + ", result=<pre>" + result.toString() + "</pre>");

    return result.toString();

 }


 public static ArrayList<String[]> getLocationTimes (int organization_id, int location_id, String day_of_week, int date, Connection con_d) {


    //if(con_d == null) con_d = Connect.getDiningCon();

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    ArrayList<String[]> result = new ArrayList<String[]>();

    int time = 0;
    int hour = 0;
    int min = 0;
    int interval = 0;
    int end_time = 0;
    int smonth = 0;
    int emonth = 0;
    int sday = 0;
    int eday = 0;

    int year = date / 10000;
    int month = (date - (year * 10000)) / 100;
    int day = (date - (year * 10000)) - (month * 100);

    String value = "";
    String display = "";

    boolean add_times = false;

    Calendar cal = Calendar.getInstance();

    try {

        //if(con_d != null) {

            pstmt = con_d.prepareStatement ( "" +
                    "SELECT id, start_month, end_month, start_day, end_day, " +
                        "to_char(start_time, 'HH24MI')::int AS stime_int, " +
                        "to_char(end_time, 'HH24MI')::int AS etime_int, " +
                        "covers_per_period, period, time_increment " +
                    "FROM meal_periods " +
                    "WHERE " +
                        "organization_id = ? AND " +
                        "location_id = ? AND " +
                        "available_" + day_of_week + " = true AND " + /*
                        "start_month >= ? AND " +
                        "start_day >= ? AND " +
                        "end_month <= ? AND " +
                        "end_day <= ? AND " + */
                        "state = 'active' " +
                    "ORDER BY stime_int;" );

            pstmt.setInt(1, organization_id);
            pstmt.setInt(2, location_id);/*
            pstmt.setInt(3, month);
            pstmt.setInt(4, day);
            pstmt.setInt(5, month);
            pstmt.setInt(6, day);*/

            rs = pstmt.executeQuery();

            while ( rs.next() ) {

                // first determin if the dates are within range

                smonth = rs.getInt("start_month");
                emonth = rs.getInt("end_month");
                sday = rs.getInt("start_day");
                eday = rs.getInt("end_day");

                if (smonth + emonth + sday + eday == 0) {

                    add_times = true;

                } else {

                    add_times = (smonth <= month && emonth >= month);
                    //Utilities.logError(rs.getInt("id") + " add_times1 == " + add_times);

                    if (smonth <= emonth) {

                        //Utilities.logError(rs.getInt("id") + " no wrapping");

                        // no year wrapping
                        if (smonth < month && emonth > month) {

                            add_times = true;

                        } else if (smonth == month && sday <= day) {

                            add_times = true;

                        } else if (emonth == month && eday >= day) {

                            add_times = true;

                        }

                    } else {

                        //Utilities.logError(rs.getInt("id") + " year wrap");
                        // adjust for year wrapping

                        //skip = (smonth >= month && sday <= day && emonth <= month && eday >= day);
                        //add_times = (smonth >= month && emonth <= month);
                        //add_times = (month > smonth && month < emonth);

                        // no year wrapping
                        if (month > smonth && month < emonth) {

                            add_times = true;

                        } else if (smonth == month && sday <= day) {

                            add_times = true;

                        } else if (emonth == month && eday >= day) {

                            add_times = true;

                        }

                    }

                }

                //Utilities.logError(rs.getInt("id") + " add_times2 == " + add_times);

                if (add_times) {

                    time = rs.getInt("stime_int");              // begin with starting time
                    interval = rs.getInt("time_increment");     // our time interval
                    end_time = rs.getInt("etime_int");          // our time interval

                    //for (int time; time < rs.getInt("etime_int"); time += interval) {

                    //cal.set(Calendar.HOUR, 1);
                    //cal.set(Calendar.MINUTE, 0);

                    cal.set(Calendar.HOUR_OF_DAY, time / 100);
                    cal.set(Calendar.MINUTE, time - ((time / 100) * 100));

                    hour = cal.get(Calendar.HOUR_OF_DAY);
                    min = cal.get(Calendar.MINUTE);

          //hr = time / 100;
          //min = time - (hr * 100);

                    time = hour * 100 + min;

                    while ( time < end_time ) {

                        value = (time / 100) + ":" + Utilities.ensureDoubleDigit((time - ((time / 100) * 100)));
                        display = Utilities.getSimpleTime(time);

                        // add string array containing what we need for outputing the options for the select box
                        result.add( new String [] { value, display } );

                        cal.add(Calendar.MINUTE, interval);

                        time = cal.get(Calendar.HOUR_OF_DAY) * 100 + cal.get(Calendar.MINUTE);

                    }

                } else {

                    
                    //Utilities.logError("Did not add times for mead_period " + rs.getInt("id") + " at location_id=" + location_id);

                }


            }

        //}

    } catch (Exception exc) {

        Utilities.logError("parmDining.getLocationTimes: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

        //try { con_d.close(); }
        //catch (Exception ignore) {}

    }

    return result;

 }


 public static int countEventCovers(int organization_id, int event_id, int location_id, boolean member_made_only, Connection con_d) {

    int result = 0;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con_d.prepareStatement ("" +
                "SELECT COUNT(id) " +
                "FROM reservations " +
                "WHERE " +
                    "organization_id = ? AND " +
                    "event_id = ? AND " +
                    "location_id = ? AND " +
                    "state <> 'cancelled'" +
                    ((member_made_only) ? " AND member_created = true" : "" ));

        pstmt.setInt(1, organization_id);
        pstmt.setInt(2, event_id);
        pstmt.setInt(3, location_id);

        rs = pstmt.executeQuery();

        if ( rs.next() ) result = rs.getInt(1);

    } catch (Exception exc) {

        Utilities.logError("parmDining.countEventCovers: organization_id=" + organization_id + ", event_id=" + event_id + ", location_id=" + location_id + ", member_made_only=" + member_made_only + ", err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

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

    for (int i=pos; i < 10; i++) {

        user_identity = " " + user_identity;
    }

    return user_identity;

 }


/*
 private void countChecks(parmDining parmD) {

    int [] checks_found = new int [parmD.covers];
    int checks = 0;
    int tmp = 0;


    // initalize the tmp array
    for (int i = 1; i <= parmD.covers; i++) {
        checks_found[i] = 0;
    }

    if (parmD.covers > 1) {

        for (int i = 1; i <= parmD.covers; i++) {

            //tmp = parmD.check_num[i];

            for (int i2 = 1; i2 <= parmD.covers; i2++) {

                if (parmD.check_num[i] != checks_found[i2]) {

                    checks++;
                }
            }

        }

        parmD.number_of_checks = checks;

    } else {

        parmD.number_of_checks = 1;

    }

 }


 public static String get_location_name (int location_id, Connection con_d) {


    String result = "";
    boolean close_con = false;

    if (con_d == null) {

        con_d = Connect.getDiningCon();
        close_con = true;
    }

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        //if(con_d != null) {

            pstmt = con_d.prepareStatement ("" +
                    "SELECT name " +
                    "FROM locations " +
                    "WHERE id = ?");

            pstmt.setInt(1, location_id);

            rs = pstmt.executeQuery();

            if ( rs.next() ) {

                // populate the parm block with the reservation details
                result = rs.getString(1);

            }

        //}

    } catch (Exception exc) {

        Utilities.logError("parmDining.get_location_name: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

        if (close_con) {
            try { con_d.close(); }
            catch (Exception ignore) {}
        }

    }

    return result;

 }
*/

}