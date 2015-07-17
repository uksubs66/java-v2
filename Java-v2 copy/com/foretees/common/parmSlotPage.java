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
import org.apache.commons.lang.*;
import com.foretees.common.parmClub;

public class parmSlotPage {

    public boolean show_member_tbd = false;
    public boolean show_gift_pack = false;
    public boolean show_transport = false;
    public boolean show_tbd = false;
    public boolean show_member_select = false;
    public boolean show_member_ghin = false;
    public boolean show_gender = false;
    public boolean show_ghin = false;
    public boolean lock_ghin = false;
    public boolean show_guest_types = false;
    public boolean show_fb = true;
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
    public boolean show_force_singles_match = false;
    public boolean show_time_picker = false;
    public boolean protect_notes = false;       // custom flag used to only allow the originator to add/change/remove existing notes
    public boolean show_recur = false;          // for recurring lottery request (and maybe tee times some day)
    
    
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
    public int event_id = 0;
    public int custom_int = 0;
    
    public int recur_type = 0;          // Recurrance type.  0 = None; 1 = weekly; 2 = every other week
    public int update_recur = 0;        // 0 = just this one req, 1 = we're updating all related requests
    
    public long time_remaining = -1;
    public long lottid = 0;
    public long id = 0;
    public long signup_id = 0;
    public long group_id = 0;
    
    //public String day_name = "";
    public String in_slots = "";
    public String ghin_text = "Hdcp #";
    public String mship = "";
    public String mtype = "";
    public String jump = "";
    public String index = "";
    public String course = "";
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
    
    public String recur_start = ""; // Date recurrance starts, in US "civilian" date format mm/dd/yyyy (only used for display)
    public String recur_end = ""; // Date recurrance ends, in US "civilian" date format mm/dd/yyyy (used for calculating end date of recur)
    
    public int[] guest_id_a = new int[25];
    public int[] p9_a = new int[25];
    public int[] time_a = new int[5];
    public int[] gift_pack_a = new int[25]; // gift pack custom
    
    public boolean[] lock_player_a = new boolean[25];
    
    public String[] player_a = new String[25];
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
    
    public List<String> allowed_tmodes_list = new ArrayList<String>();
    public List<String> tmodes_list = new ArrayList<String>();
    public List<String> slot_header_notes = new ArrayList<String>();
    public List<String> slot_footer_notes = new ArrayList<String>();
    public List<String> guest_type_footer_notes = new ArrayList<String>();
    public List<String> page_start_notifications = new ArrayList<String>();
    public List<String> page_start_footers = new ArrayList<String>();
    public List<String> page_start_instructions = new ArrayList<String>();
    
    public parmCourse course_parms = new parmCourse();
    
    //public Object club_parm = new Object();
    public Map<String, String> guest_type_cw_map = new LinkedHashMap<String, String>();  // Map of default transport type to be passed to javascript
    public Map<String, Map<String, Object>> guest_types_map = new LinkedHashMap<String, Map<String, Object>>();
    public Map<String, String[]> name_list_filter_map = new LinkedHashMap<String, String[]>();
    public Map<String, Object> callback_map = new LinkedHashMap<String, Object>();
    public Map<String, Object> callback_form_map = new LinkedHashMap<String, Object>();
    public Map<String, Map> callback_button_map = new LinkedHashMap<String, Map>();
    public Map<String, String> slot_submit_map = new LinkedHashMap<String, String>();
}  // end of class
