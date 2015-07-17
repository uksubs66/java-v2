/***************************************************************************************
 *   parmSlotPage:  Parameters used by slotPage.
 *
 *
 *   called by:  Member_slotm, slorPage
 *
 *   created:    12/14/2011   John K.
 *
 *   last updated:
 * 
 *         2/27/14  BP - Added custom1_a array for a new custom event question/prompt.  Added for Interlachen but others can use.
 *         2/18/14  Add update_recur for handling updates to recurring requests
 *         1/06/14  BK - Added loaction_disp, alt_callback_form_html variables for use with FlxRez alternate time searching.
 *         9/05/13  BP - Added show_recur_checkbox and isRecurr for new feature to recur lottery requests (and possible tee times in future).
 *         8/22/13  BP - Added protect_notes for custom (case 2293).
 *         5/01/12  BP - Added event_id in for Member_evntSignUp.
 *        12/14/11  Created
 *
 *
 *
 ***************************************************************************************
 */
package com.foretees.common;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import org.apache.commons.lang.*;

public class parmSlotPage {

    public boolean show_member_tbd = false;
    public boolean show_gift_pack = false;
    public boolean show_transport = false;
    public boolean show_tbd = false;
    public boolean show_member_select = false;
    public boolean show_member_ghin = false;
    public boolean show_gender = false;
    public boolean show_ghin = false;
    //public boolean show_custom1 = false;
    public boolean lock_ghin = false;
    public boolean show_guest_types = false;
    public boolean show_fb = true;
    public boolean show_check_num = false;
    public boolean lock_fb = false;
    public boolean set_default_fb_value = false;
    public boolean edit_mode = false;
    public boolean allow_cancel = false;
    public boolean show_contact_to_cancel = false;
    public boolean verify_member_tmode = false; // If true, only c/w for course can be used
    public boolean use_default_member_tmode = true;
    public boolean show_ghin_in_list = false;
    public boolean filter_partner_list = false; // Use name_list_filter_map to filter partner list as well as letter/name list
    public boolean page_start_button_go_back = false;
    public boolean page_start_button_continue = false;
    public boolean page_start_button_cancel = false;
    public boolean page_start_button_accept = false;
    public boolean custom_caddie = false;
    public boolean season_long = false;
    public boolean show_name = false;
    public boolean ask_more = false;
    public boolean use_owner = false;
    public boolean lock_owner = false;
    public boolean show_force_singles_match = false;
    public boolean show_time_picker = false;
    public boolean protect_notes = false;       // custom flag used to only allow the originator to add/change/remove existing notes
    public boolean show_recur = false;          // for recurring lottery request (and maybe tee times some day)
    public boolean add_players;  // Adds a "Add player" option
    public boolean clientSideCancel = true; // use client side cancel prompt, or submit to server 
    
    public int default_fb_value = 0;
    public int player_count = 0;
    public int players_per_group = 0;
    public int visible_players_per_group = 5;
    public int time = 0;
    public int fb = 0;
    public int slots = 1;  // actual slots displayed on slot page
    public int activity_slots = 0; // Slots that flexrez will duplicate a single slot over  
    public int date = 0;
    public int yy = 0;
    public int mm = 0;
    public int dd = 0;
    public int activity_id = 0;
    public int history_jump = 1; // How many pages to jump back when existing
    public int lstate = 0; // lottery state
    public int checkothers = 0;
    public int allowx = 0;
    public int hide_notes = 0;
    public int mins_before = 0;
    public int mins_after = 0;
    public int layout_mode = 0;
    public int force_singles = 0;
    public int start_hr = 0;
    public int start_min = 0;
    public int end_hr = 0;
    public int end_min = 0;
    public int recur_type = 0;          // Recurrance type.  0 = None; 1 = weekly; 2 = every other week
    public int update_recur = 0;        // 0 = just this one req, 1 = we're updating all related requests
    
    public Integer occasion_id;
    public Integer max_players; // If "add_players", is active this is the maximum allowed
    
    public Integer reservation_id;
    public Integer location_id;
    public Integer event_id;
    
    public String slot_hdata;  // Contains hash of json object containing slotId object
    public String slot_hkey;   // Public key used to decrypt slot_data

    public long time_remaining = -1;
    public long lottid = 0;
    public long id = 0;
    public long signup_id = 0;
    public long group_id = 0;
    
    //public String day_name = "";
    public String id_hash;
    public String in_slots = "";
    public String ghin_text = "Hdcp #";
    public String mship = "";
    public String mtype = "";
    public String jump = "";
    public String index = "";
    public String course = "";
    public String owner = "";
    public String return_course = "";
    public String day = "";
    public String stime = "";
    public String activity_stime = "";
    public String course_disp = "";
    public String location_disp = "";
    public String sdate = "";
    public String notice_message = "";
    public String transport_legend = "";
    public String p5 = "";
    public String notes = "";
    public String user = "";
    public String club = "";
    public String club_name = "";
    public String name = "";
    public String lname = ""; // lottery name
    public String orig_by = "";
    public String slot_url = ""; // Base url to submit slot requests to
    public String gift_pack_text = "Gift Pack";
    public String slot_help_url = "../member_help_slot_instruct.htm";
    public String slot_type = "Tee Time";
    public String signup_type = ""; // If empty, will be set to slot_type
    public String member_tbd_text = "Member";
    public String page_title = "Member Tee Time Request Page";
    public String bread_crumb = "Tee Time Registration";
    public String zip_code = "";
    public String page_start_title = "";
    public String page_start_header = "";
    public String callback_form_html = "";
    public String alt_callback_form_html = "";
    public String notes_prompt = "[options.labelNotesToPro]";
    public String start_ampm = "";
    public String end_ampm = "";
    public String default_member_wc = "";
    public String default_member_wc_override = "";
    public String pcw = ""; // User's default PCW
    //public String custom1_title = "";
    public String reservation_number; // Human readable reservation id
    
    public String post_mode;
    
    public String recur_start = ""; // Date recurrance starts, in US "civilian" date format mm/dd/yyyy (only used for display)
    public String recur_end = ""; // Date recurrance ends, in US "civilian" date format mm/dd/yyyy (used for calculating end date of recur)
    
    public int[] guest_id_a = new int[25];
    public int[] p9_a = new int[25];
    public int[] time_a = new int[5];
    public int[] gift_pack_a = new int[25]; // gift pack custom
    public int[] check_num_a = new int[0];
    
    public boolean[] lock_player_a = new boolean[25];
    public boolean[] lock_player_fb_a = new boolean[25];
    
    public String[] player_a = new String[25];
    public String[] user_a = new String[25];
    public String[] pcw_a = new String[25];
    public String[] orig_a = new String[25];
    public String[] custom_a = new String[25];
    public String[] custom_disp_a = new String[25];
    public String[] gender_a = new String[25];
    public String[] ghin_a = new String[25];
    public String[] homeclub_a = new String[25];
    public String[] phone_a = new String[25];
    public String[] address_a = new String[25];
    public String[] email_a = new String[25];
    public String[] shirt_a = new String[25];
    public String[] shoe_a = new String[25];
    public String[] otherA1_a = new String[25];
    public String[] otherA2_a = new String[25];
    public String[] otherA3_a = new String[25];
    public String[] custom1_a = new String[25];
    public String[] meal_option_a;
    
    public List<String> group_titles;
    public List<String> id_list;
    
    public List<Map<Integer, String>> answers;

    public List<String> allowed_tmodes_list = new ArrayList<String>();
    public List<String> tmodes_list = new ArrayList<String>();
    public List<String> slot_header_notes = new ArrayList<String>();
    public List<String> slot_footer_notes = new ArrayList<String>();
    public List<String> guest_type_footer_notes = new ArrayList<String>();
    public List<String> page_start_notifications = new ArrayList<String>(); 
    public List<String> page_start_instructions = new ArrayList<String>();
    public List<String> custom1_options = new ArrayList<String>();        // available for list of options to use for custom event question/prompt (refer to Interlachen custom for Tees to Play)
    public List<String> page_start_messages = new ArrayList<String>(); // used for club messages
    public List<String> page_start_htmlblocks = new ArrayList<String>(); // Displayed before form
    public List<String> page_start_footers = new ArrayList<String>();
    
    public boolean page_start_force = false;
    // callback_form_list is an alternate for callback_form_map.
    // Use in place of callback_form_map, not with callback_form_map.
    // Use callback_form_list for all callback forms going forward.
    // order of form elements is unpredictable on some browsers with callback_form_map 
    public List<Object> callback_form_list = new ArrayList<Object>(); 

    public parmCourse course_parms = new parmCourse();
    
    //public Object club_parm = new Object();
    public Map<String, String> guest_type_cw_map = new LinkedHashMap<String, String>();  // Map of default transport type to be passed to javascript
    public Map<String, Map<String, Object>> guest_types_map = new LinkedHashMap<String, Map<String, Object>>();
    public Map<String, Map<String, Object>> guest_types_map_full = new LinkedHashMap<String, Map<String, Object>>(); // All guest types, regardless
    public Map<String, String[]> name_list_filter_map = new LinkedHashMap<String, String[]>();
    public Map<String, Object> callback_map = new LinkedHashMap<String, Object>();
    public Map<String, Object> callback_form_map = new LinkedHashMap<String, Object>();
    public Map<String, Map> callback_button_map = new LinkedHashMap<String, Map>();
    public Map<String, String> slot_submit_map = new LinkedHashMap<String, String>();
    public Map<String, Object> options = new LinkedHashMap<String, Object>();
    
    //public Map<String, Map<String,Object>> slotParamTable = getParamTable(); // not ready to implement yet

    public Map<String, Object> debug = new HashMap<String, Object>();
    
    public List<diningMealOption>  meal_options = new ArrayList<diningMealOption>();

    public slotPostBack process_postback;
      
    public final void setParmCourse(HttpServletRequest req){
        
        Connection con = Connect.getCon(req);
        
        parmCourse parmc = new parmCourse();
        
        String day_name = timeUtil.getDayOfWeek(date);   
        int i = 0;
        long shortDate = date - ((date / 10000) * 10000);
        
            //
            //  Get the walk/cart options available
            //
            try {

                getParms.getTmodes(con, parmc, course);
            } catch (Exception e1) {

                //msg = "Get wc options. ";

                //dbErrorHtml(out, e1, msg); 
                return;
            }
            

            /*
            //
            //  If Jonathan's Landing then remove certain options  (Case# 1330)
            //
            if (club.equals("jonathanslanding")) {
            
            for (i = 0; i < parmc.tmode_limit; i++) {
            
            if (parmc.tmodea[i].equalsIgnoreCase( "TF" ) || parmc.tmodea[i].equalsIgnoreCase( "ACF" ) || parmc.tmodea[i].equalsIgnoreCase( "WLK" )) {
            
            parmc.tmodea[i] = "";      // remove it
            }
            }
            }
             */

            //
            //  if Piedmont Driving Club, remove 2 trans modes that are for events only
            //                  Also, check for 2-some time, and caddie only times.
            //
            int piedmontStatus = 0;

            if (club.equals("piedmont")) {

                piedmontStatus = verifySlot.checkPiedmont(date, time, day_name);     // check if special time

                Calendar cal2 = new GregorianCalendar();

                cal2.set((int) yy, (int) (mm - 1), (int) dd);

                boolean isDST = false;

                if (cal2.get(Calendar.DST_OFFSET) != 0) {
                    isDST = true;
                }

                if (time < 1210 || (isDST && time > 1430) || (!isDST && time > 1330)) {

                    for (i = 0; i < parmc.tmode_limit; i++) {

                        if (parmc.tmodea[i].equalsIgnoreCase("CFC")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }

                for (i = 0; i < parmc.tmode_limit; i++) {

                    //
                    //  If "Walking No Caddie" and time requires a Caddie, remove this option
                    //
                    if (parmc.tmodea[i].equalsIgnoreCase("wnc") && piedmontStatus > 0) {

                        parmc.tmodea[i] = "";      // remove it
                    }

                    //
                    //  If "Cart With ForeCaddie" or "Walk With Caddie, remove this option (used for events only)
                    //
                    if (parmc.tmodea[i].equalsIgnoreCase("wwc")) {

                        parmc.tmodea[i] = "";      // remove it
                    }
                }
            }

            //
            //  if The Stanwich Club and before 1:00 on any day, remove trans mode of 'Carry' (CRY)
            //
            if (club.equals("stanwichclub")) {

                if (time < 1300) {

                    for (i = 0; i < parmc.tmode_limit; i++) {

                        if (parmc.tmodea[i].equalsIgnoreCase("cry")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }
            }

            //
            //  if Lake Forest CC and a weekend and before 10:01, remove trans mode of 'Walk' (WLK)
            //
            if (club.equals("lakeforestcc")) {

                if ((day_name.equals("Saturday") || day_name.equals("Sunday")) && time < 1001) {

                    for (i = 0; i < parmc.tmode_limit; i++) {

                        if (parmc.tmodea[i].equalsIgnoreCase("WLK") || parmc.tmodea[i].equalsIgnoreCase("P/C")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }
            }

            //
            //  if Olympic Club, remove trans mode of 'Caddie' (CAD)
            //
            if (club.equals("olyclub")) {

                for (i = 0; i < parmc.tmode_limit; i++) {

                    if (parmc.tmodea[i].equalsIgnoreCase("CAD")) {

                        parmc.tmodea[i] = "";      // remove it
                    }
                }
            }

            //
            //  if Imperial Golf Club and before 11:30 on any day between 1/1 - 5/31, remove trans mode of Walk & Pull Cart  (Case #1287)
            //
            if (club.equals("imperialgc")) {

                // long mmdd = (mm*100) + dd;

                // if (mmdd >= 101 && mmdd <= 531 && time < 1130) {

                if (date <= 20100531 && time < 1130) {        // this custom goes away after 5/31/2010

                    for (i = 0; i < parmc.tmode_limit; i++) {

                        if (parmc.tmodea[i].equalsIgnoreCase("WLK") || parmc.tmodea[i].equalsIgnoreCase("PC")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }
            }

            /*
            //
            //  if Wilmington, remove trans mode of 'Carry Your Own' (CYO) on specified times
            //
            if (club.equals( "wilmington" )) {
            
            boolean wilstrip = false;
            
            if ((day_name.equals( "Tuesday" ) || day_name.equals( "Wednesday" ) || day_name.equals( "Thursday" ) || day_name.equals( "Friday" )) &&
            date != Hdate2b) {       // if Tues - Fri and NOT 7/04
            
            if (time < 1300 || time > 1900) {
            
            wilstrip = true;
            }
            
            } else {
            
            if (day_name.equals( "Saturday" ) || day_name.equals( "Sunday" ) || date == Hdate1 || date == Hdate3) {   // if a w/e, Mem Day or Labor Day
            
            if (time < 1100 || time > 1900) {
            
            wilstrip = true;
            }
            }
            }
            
            if (wilstrip == true) {
            
            for (i = 0; i < parmc.tmode_limit; i++) {
            
            if (parmc.tmodea[i].equalsIgnoreCase( "cyo" )) {
            
            parmc.tmodea[i] = "";      // remove it
            }
            }
            }
            }
             */


            /*   ***removed at request of club***
            //
            //  If Mediterra remove the trans mode of 'Walking' (Case 1263)
            //
            if (club.equals( "mediterra" )) {
            
            for (i = 0; i < parmc.tmode_limit; i++) {
            
            if (parmc.tmodea[i].equalsIgnoreCase( "w" )) {
            
            parmc.tmodea[i] = "";      // remove it
            }
            }
            }
             */

            //
            //  If Pinnacle Peak remove the trans mode of 'NC' (Case 1288)
            //
            if (club.equals("pinnaclepeak")) {

                for (i = 0; i < parmc.tmode_limit; i++) {

                    if (parmc.tmodea[i].equalsIgnoreCase("NC")) {

                        parmc.tmodea[i] = "";      // remove it
                    }
                }
            }

            //
            //  If Sonnenalp remove the trans mode of 'ICP' and 'FCP' (Case 1452)
            //
            if (club.equals("sonnenalp")) {

                for (i = 0; i < parmc.tmode_limit; i++) {

                    if (parmc.tmodea[i].equalsIgnoreCase("ICP") || parmc.tmodea[i].equalsIgnoreCase("FCP")) {

                        parmc.tmodea[i] = "";      // remove it
                    }
                }
            }

            //
            //  If Berkeley Hall remove the trans mode of 'REC' and 'CMP' (Case 1341)
            //
            if (club.equals("berkeleyhall")) {

                for (i = 0; i < parmc.tmode_limit; i++) {

                    if (parmc.tmodea[i].equalsIgnoreCase("REC") || parmc.tmodea[i].equalsIgnoreCase("CMP")) {

                        parmc.tmodea[i] = "";      // remove it
                    }
                }
            }

            //
            //  If Silver Lake CC, remove the trans mode 'WLK' if any day but Tues/Thurs and before 3pm
            //
            if (club.equals("silverlakecc")) {

                if (!day_name.equals("Tuesday") && !day_name.equals("Thursday") && time < 1500) {

                    for (i = 0; i < parmc.tmode_limit; i++) {

                        if (parmc.tmodea[i].equalsIgnoreCase("WLK")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }
            }
            //
            //  If CapeCod National CC thurs-sunday before 1:10pm only Cart or Caddie for MOD.
            //
            if (club.equals("capecodnational")) {
                if (day_name.equals("Thursday") || day_name.equals("Friday") || day_name.equals("Saturday") || day_name.equals("Sunday")) {
                    if (shortDate >= 620 && shortDate <= 901 && time < 1310) {
                        for (i = 0; i < parmc.tmode_limit; i++) {
                            if (parmc.tmodea[i].equalsIgnoreCase("W") || parmc.tmodea[i].equalsIgnoreCase("PC") || parmc.tmodea[i].equalsIgnoreCase("W9C") || parmc.tmodea[i].equalsIgnoreCase("P9C")) {

                                parmc.tmodea[i] = "";      // remove it
                            }
                        }
                    }
                }
            }

            //
            //  If Tavistock CC and between 4/1 and 10/31, remove the trans mode 'CRY' and 'TRL if any day but Tues/Thurs and before 1pm
            //
            if (club.equals("tavistockcc")) {

                if (shortDate >= 401 && shortDate <= 1031 && time < 1259) {

                    for (i = 0; i < parmc.tmode_limit; i++) {

                        if (parmc.tmodea[i].equalsIgnoreCase("CRY") || parmc.tmodea[i].equalsIgnoreCase("TRL")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }
            }

            //
            //  If Wayzata CC, remove all mode of trans aside from 'CAD' and 'CRT' if between 6/1 and 8/31 on Wed/Fri/Sat/Sun before 3PM
            //
            if (club.equals("wayzata")) {

                if (shortDate >= 601 && shortDate <= 831 && time < 1200
                        && (day_name.equals("Wednesday") || day_name.equals("Friday") || day_name.equals("Saturday") || day_name.equals("Sunday"))) {

                    for (i = 0; i < parmc.tmode_limit; i++) {

                        if (!parmc.tmodea[i].equalsIgnoreCase("CAD") && !parmc.tmodea[i].equalsIgnoreCase("CRT")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }
            }
            
            //  If Wisconsin Club (wisconsinclub), remove all mode of trans aside from 'GC', 'CAD', 'C/C', 'FC' on Sat between 7-9am from 5/1-9/30
            if (club.equals("wisconsinclub")) {
                
                if (shortDate >= 501 && shortDate <= 930 && time >= 700 && time < 900 && day_name.equals("Saturday")) {
                    
                    for (i = 0; i < parmc.tmode_limit; i++) {
                        
                        if (!parmc.tmodea[i].equalsIgnoreCase("GC") && !parmc.tmodea[i].equalsIgnoreCase("CAD") && !parmc.tmodea[i].equalsIgnoreCase("C/C") && !parmc.tmodea[i].equalsIgnoreCase("FC")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }
            }

            //
            //  If Minikahda remove all mode of trans aside from 'CRT', 'CAD', 'C/C' on weekends and holidays and some Fridays
            //
            if (club.equals("minikahda")) {
                if (date >= ProcessConstants.memDay && date <= ProcessConstants.laborDay && (day_name.equals("Tuesday") || day_name.equals("Wednesday") || day_name.equals("Thursday")) && time >= 1200 && time <1400) {
                    for (i = 0; i < parmc.tmode_limit; i++) {
                        
                        if (parmc.tmodea[i].equalsIgnoreCase("PC") || parmc.tmodea[i].equalsIgnoreCase("WLK") || parmc.tmodea[i].equalsIgnoreCase("W/R")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }
                
                if ((((shortDate >= 501 && shortDate <= 930 && (day_name.equals("Saturday") || day_name.equals("Sunday"))) 
                        || (date == ProcessConstants.memDay || date == ProcessConstants.july4b || date == ProcessConstants.laborDay)) && time < 1200) 
                        || (shortDate >= 524 && shortDate <= 830 && day_name.equals("Friday") && time >= 1000 && time < 1400)) {
                    
                    for (i = 0; i < parmc.tmode_limit; i++) {
                        
                        if (!parmc.tmodea[i].equalsIgnoreCase("CRT") && !parmc.tmodea[i].equalsIgnoreCase("CAD") && !parmc.tmodea[i].equalsIgnoreCase("C/C")) {

                            parmc.tmodea[i] = "";      // remove it
                        }
                    }
                }
            }
            
            // If Lakewood Ranch, don't display 'WLK' tmode in tee times prior to 1:00pm.
            if (club.equals("lakewoodranch")) {
                
                if (time < 1300) {
                    
                    for (i = 0; i < parmc.tmode_limit; i++) {
                        
                        if (parmc.tmodea[i].equalsIgnoreCase("WLK")) {

                            parmc.tmodea[i] = "";      // remove it
                            break;
                        }
                    }
                }
            }


            //
            //  Make sure the user's c/w option is still supported (pro may have changed config)
            //
            if (!pcw.equals("") && !club.equals("sonnenalp")) {    // must skip this for Sonnenalp so ICP and FCP are allowed for some members

                i = 0;
                loopi1:
                while (i < parmc.tmode_limit) {

                    if (parmc.tmodea[i].equals(pcw)) {

                        break loopi1;
                    }
                    i++;
                }
                if (i > parmc.tmode_limit - 1) {       // if we went all the way without a match

                    pcw = "";        // force user to specify one
                }
            }
            i = 0;

            //
            //  If selected course only has one mode of trans, then make that the default
            //
            if (!club.equals("sonnenalp")) {    // skip this for Sonnenalp so ICP and FCP are allowed for some members

                if (!parmc.tmodea[0].equals("") && parmc.tmodea[1].equals("")) {     // if only one tmode specified

                    pcw = parmc.tmodea[0];         // use that as default
                }
            }

            //
            //   If Fort Collins and the Greeley course, then use GC as the default mode of trans
            //
            if (club.equals("fortcollins") && course.startsWith("Greeley")) {

                if (pcw.equals("")) {

                    pcw = "GC";
                }
            }

            //
            //   If Merion and the East course, then use CAD as the default mode of trans during specific times
            //

            if (club.equals("merion") && course.equals("East")) {

                if (((day_name.equals("Tuesday") || day_name.equals("Wednesday") || day_name.equals("Thursday")) && time < 1437)
                        || (day_name.equals("Friday") && time < 1537)
                        || ((day_name.equals("Saturday") || day_name.equals("Sunday")) && time < 1401)) {

                    pcw = "CAD";
                    custom_caddie = true; // new skin
                }
            }

            if (club.equals("cherryhills") || club.equals("tualatincc")) {

                pcw = "";        // no mode of trans - they must specify
            }
            

            if (club.equals("morriscgc")) {
                
                if (time < 1300) {
                    
                    for (i = 0; i < parmc.tmode_limit; i++) {
                        
                        if (parmc.tmodea[i].equalsIgnoreCase("CRY")) {

                            parmc.tmodea[i] = "";      // remove it
                            //break;
                        }
                    }
                }
            }
            
            //For mon,tues,sat,sun 6am-2:59pm hide "WLK" from members
            //For wed,thur,fri 9:31am-2:59pm hid "WLK" from members
            if (club.equalsIgnoreCase("waialae")) {
                if (((day_name.equalsIgnoreCase("Monday") || day_name.equalsIgnoreCase("Tuesday") || day_name.equalsIgnoreCase("Saturday") || day_name.equalsIgnoreCase("Sunday")) && (time > 559 && time < 1500))
                        || ((day_name.equalsIgnoreCase("Wednesday") || day_name.equalsIgnoreCase("Thursday") || day_name.equalsIgnoreCase("Friday")) && (time > 930 && time < 1500))) {
                    
                    for (i = 0; i < parmc.tmode_limit; i++) {

                        if (parmc.tmodea[i].equalsIgnoreCase("WLK")) {

                            parmc.tmodea[i] = "";      // remove it
                            //break;
                        }
                    }
                }

            }
            
            if (club.equals("kahkwa")) {
                
                if (time > 1300) {
                    
                    for (i = 0; i < parmc.tmode_limit; i++) {
                        
                        if (parmc.tmodea[i].equalsIgnoreCase("W")) {

                            parmc.tmodea[i] = "";      // remove it
                            //break;
                        }
                    }
                }
            }
            
            if (club.equals("torresdale")) {
                
                if (time < 1400 && shortDate >= 401 && shortDate <= 1031) {
                    
                    for (i = 0; i < parmc.tmode_limit; i++) {
                        
                        if (parmc.tmodea[i].equalsIgnoreCase("WLK")) {

                            parmc.tmodea[i] = "";      // remove it
                            //break;
                        }
                    }
                }
            }
            
                        
            allowed_tmodes_list = Arrays.asList(parmc.tmodea);  // Tmode that will be allowed when set as defaults

            course_parms = parmc;

        
    }
    
    private Map<String,Object> getParamTableItem(Object defaultValue, boolean resetOnClear){
        
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("defaultValue", defaultValue);
        result.put("resetOnClear", resetOnClear);
        return result;
    }
    
    private Map<String, Map<String,Object>> getParamTable(){
        
         Map<String, Map<String,Object>> result = new HashMap<String, Map<String,Object>>();

         result.put("player_a", getParamTableItem("", true));
         result.put("user_a", getParamTableItem("", true));
         result.put("orig_a", getParamTableItem("", true));
         result.put("pcw_a", getParamTableItem("", true));
         result.put("custom_a", getParamTableItem("", true));
         result.put("custom_disp_a", getParamTableItem("", true));
         result.put("homeclub_a", getParamTableItem("", true));
         result.put("ghin_a", getParamTableItem("", true));
         result.put("gender_a", getParamTableItem("", true));
         result.put("email_a", getParamTableItem("", true));
         result.put("phone_a", getParamTableItem("", true));
         result.put("shoe_a", getParamTableItem("", true));
         result.put("shirt_a", getParamTableItem("", true));
         result.put("otherA1_a", getParamTableItem("", true));
         result.put("otherA2_a", getParamTableItem("", true));
         result.put("otherA3_a", getParamTableItem("", true));
         result.put("meal_option_a", getParamTableItem("", true));
         
         result.put("gift_pack_a", getParamTableItem(0, true));
         result.put("guest_id_a", getParamTableItem(0, true));
         result.put("check_num_a", getParamTableItem(1, true));
         result.put("p9_a", getParamTableItem(default_fb_value, true)); // This needs to grab slotParms.default_fb_value, but not on init.
         
         result.put("lock_player_a", getParamTableItem(0, false));
         result.put("lock_player_a_fb", getParamTableItem(0, false));

         return result;
        
    }
    
}  // end of class
