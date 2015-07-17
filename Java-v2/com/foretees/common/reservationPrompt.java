/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;

import java.util.*;


/**
 *
 * @author Owner
 */
public class reservationPrompt {
    
    public Integer event_id;
    
    public String prompt_title = "[data.event_name] Registration";
    public String list_title = "Current [data.event_name] Registrations";
    
    public String signup_id_field = "id";
    public String event_id_field = "event_id";
    
    public Integer select_count = 0; // number of signups we can select (not sure if this is usefull for the client-side code?)
    public Integer my_signup_id; // user's signup id
    
    public String event_name;
    public String base_url;
    public String slot_url;
    
    public String status;
    public String block_reason;
    public String list_status;
    
    public String players_column = "Players";
    public String player_column = "Player";
    public String select_column = "Select";
    
    public String signup_button = "Register";
    public String edit_signup_button = "Your Reservation";
    public String list_button = "Current Registrations";
    
    public List<String> instructions; // instructions for prompt and list
    public List<String> list_instructions; // instructions for list view
    public List<String> prompt_instructions; // instructions for prompt view
    
    public List<Map<String, String>> detail_list = new ArrayList<Map<String, String>>();
    
    //public List<Map<String, Object>> signup_list;
    public List<reservationSignup> signup_list = new ArrayList<reservationSignup>();
    
    public Boolean signup;
    public Boolean use_signup_button;
    public Boolean use_signup_list;
    public Boolean in_event = false; // is user in this event
    public Boolean hide_names; // are names hidden?
    
    public Boolean use_location;
    public Boolean use_type;
    public Boolean status_per_player;
    
}
