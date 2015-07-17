/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.Calendar;
import java.util.*;
//import java.util.Calendar;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.net.URL;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;

//import com.google.gson.*;
//import com.google.gson.reflect.*;

//import org.apache.commons.lang.*;

/**
 *
 * @author Owner
 */
public class diningEvent {
    
    public String name;
    public String location_name;
    //public String costs;
    //public String seatings;
    public String time_format;
    
    public String dress_code;
    public String musical_style;
    public String theme;
    public String extras;
    public String notes;
    public String online_message;
    
    public int id = 0;
    public int location_id;
    
    public int minimum_advance_days;
    public int maximum_advance_days;
    public int maximum_party_size;
    
    public int date;
    public int start_time;
    public int end_time;
    
    public int open_date;
    public int close_date;
    
    public List<diningMealOption> mealOptions = new ArrayList<diningMealOption>(); 
    public List<String> seatingTimes = new ArrayList<String>();
    
    public boolean allow_online_signup = false;
    
    public boolean non_event_cache = false;
    
    
    public diningEvent(){
        
    }

    public diningEvent(int event_id, HttpServletRequest req, Connection con_d){
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT e.id, e.name, e.members_can_make_reservations, "
                    + "  to_char(e.start_time, 'HH24MI') AS stime, "
                    + "  to_char(e.end_time, 'HH24MI') AS etime, "
                    + "  to_char(e.date, 'YYYYMMDD')::int AS our_date, "
                    + "  to_char(e.date, 'MM/DD/YYYY') AS date1, "
                    + "  e.costs, e.seatings, loc.name AS location_name, "
                    + "  e.start_time, e.date, e.maximum_party_size, e.location_id,"
                    + "  e.time_format, e.minimum_advance_days, e.maximum_advance_days,"
                    + "  ms.name as musical_style,"
                    + "  dc.name as dress_code, "
                    + "  e.theme, "
                    + "  e.extras, "
                    + "  e.notes, "
                    + "  e.online_message "
                    + " FROM events e "
                    + "  LEFT OUTER JOIN locations AS loc "
                    + "   ON e.location_id = loc.id "
                    + "  LEFT OUTER JOIN musical_styles ms "
                    + "   ON ms.id = e.musical_style_id "
                    + "  LEFT OUTER JOIN dress_codes dc "
                    + "   ON dc.id = e.dress_code_id "
                    + " WHERE e.id = ? ");

            pstmt.setInt(1, event_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                id = rs.getInt("id");
                name = rs.getString("name");
                //costs = rs.getString("costs");

                maximum_party_size = rs.getInt("maximum_party_size");
                minimum_advance_days = rs.getInt("minimum_advance_days");
                maximum_advance_days = rs.getInt("maximum_advance_days");
                
                date = rs.getInt("our_date");
                location_id = rs.getInt("location_id");
                location_name = rs.getString("location_name");
                
                allow_online_signup = rs.getBoolean("members_can_make_reservations");
                
                start_time = rs.getInt("stime");
                end_time = rs.getInt("etime");

                //costs = rs.getString("costs");
                //seatings = rs.getString("seatings");
                time_format = rs.getString("time_format");
                
                musical_style = rs.getString("musical_style");
                dress_code = rs.getString("dress_code");
                theme = rs.getString("theme");
                extras = rs.getString("extras");
                notes = rs.getString("notes");
                online_message = rs.getString("online_message");
                
                open_date = timeUtil.add(null, date, 0, Calendar.DATE, 0-maximum_advance_days)[timeUtil.DATE];
                close_date = timeUtil.add(null, date, 0, Calendar.DATE, 0-minimum_advance_days)[timeUtil.DATE];
                
                mealOptions = getMealOptions(rs.getString("costs"));
                seatingTimes = getSeatings(rs.getString("seatings"), req);

            }


        } catch (Exception exc) {

            //last_error = "parmDining.loadEventInfo: qry=" + err_tmp + ", Err=" + exc.toString();
            Utilities.logError("diningEvent: err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));

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
    
    public final diningMealOption getMealOption(String category){
        
        for(diningMealOption mealOption : mealOptions ){
            if(category != null && mealOption.category.equals(category)){
                return mealOption;
            }
        }
        
        return null;
        
    }
    
    public final String getMealOptionCost(String category){
        
        String result = "";
        diningMealOption mealOption = getMealOption(category);
        if(mealOption != null){
            result = mealOption.cost;
        }
        
        return result;
        
    }
    
    private List<diningMealOption> getMealOptions(String costs) {


        List<diningMealOption> result = new ArrayList<diningMealOption>();

        int pos1 = 0, pos2 = 0, marker = 0, last = 0, i = 0;
        String cost, category, type;

        last = costs.lastIndexOf("!ruby/object:Cost");

        try {

            while (marker < last) {

                pos1 = costs.indexOf("amount: ", marker) + 7;
                pos2 = costs.indexOf(" currency", marker);
                cost = costs.substring(pos1, pos2).trim();
                if (cost.endsWith(".0")) {
                    cost += "0";
                }

                pos1 = costs.indexOf("price_category: ", marker) + 15;
                pos2 = costs.indexOf("price_type_id:", marker);
                category = costs.substring(pos1, pos2).replace("\"", "").trim();

                pos1 = costs.indexOf("price_type_id: ", marker) + 14;
                pos2 = costs.indexOf("remote_item_id:", pos1);
                type = costs.substring(pos1, pos2).replace("\"", "").trim();

                marker = pos2;
                i++;
                
                result.add(new diningMealOption(cost, category, type));
            }

        } catch (Exception exc) {

            Utilities.logError("diningUtil.getMealOptions: err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));

        }

        return result;

    }
 
    public final List<String> getSeatings(String seatings, HttpServletRequest req) {


        /*
        
        ---
        - !ruby/object:Seating
        time: 2011-11-23 18:00:00 -05:00
        - !ruby/object:Seating
        time: 2011-11-23 20:00:00 -05:00
        
         */

        List<String> result = new ArrayList<String>();

        int pos1 = 0, marker = 0, last = 0, i = 0, time = 0;
        String tmp = "";
        last = seatings.lastIndexOf("!ruby/object:Seating");

        try {

            while (marker < last) {

                pos1 = seatings.indexOf("time: ", marker) + 5;
                tmp = seatings.substring(pos1, pos1 + 27).trim();

                //tmp1 = tmp.substring(11, 16);
                //listArray2.add(tmp1); // seating times array

                
                time = Integer.parseInt(tmp.substring(11, 16).replace(":", ""));
                // Adjust central to club time?
                //time = timeUtil.serverToClubDateTime(req, 20140102, time)[timeUtil.TIME];
                result.add(timeUtil.get12HourTime(time));

                //stime = Utilities.getSimpleTime(Utilities.adjustTime(con, time));

                //listArray1.add(tmp); // seating times array

                marker = pos1 + 26;
                i++;

            }

        } catch (Exception exc) {

            Utilities.logError("diningEvent.getSeatings: err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));

        }

        return result;

    } // end of parseCosts

    
}
