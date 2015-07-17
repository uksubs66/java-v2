/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api;

import java.util.*;

import com.foretees.common.timeUtil;
import org.joda.time.DateTimeZone;

/**
 *
 * @author Owner
 */
public class ApiGetCommand {
    
    public String command;
    public String request_id; // If null, command is used.  If set, response key is set to this.
    public boolean as_object = false; // If true, list results will be converted from an array list to an object with the ID of each record as the key.  
    public boolean exclude_disabled = false; // Not in use yet.  Intended to exclude disabled records in requests.
    public boolean force_refresh = false; // Used by some commands for various things
    public Long id;         // ID of wanted  record
    public Long detail_id;    // Use depends on context.  Usually a binding record
    public Long parent_id;  // Use depends on context.  Usually a binding record
    public Long club_id;
    public Long member_id;
    public Integer activity_id;
    public String date_start;
    public String date_end;
    public String data; // Misc data.  May be JSON string.  Decoding depends on specific commands/parameters
    
    public Integer req_month;         //  These are temporary. ** DO NOT USE! **
    public Integer req_year;          //  They are used for compatibility with Common_web_api, 
    public Boolean club_cal;          //  But will be removed when that legacy code interface is replaced -- probably before
    public Boolean events_only;       //  ""
    public Boolean reservations_only; //  ""
    public List<String> string_list;  //  ""
    
    
    public String last_error;
    
    public ApiGetCommand(){
        
    }
    
    public ApiGetCommand(String command, boolean as_object, boolean exclude_disabled, Long id, Long detail_id, Long club_id, Long parent_id, Long member_id, Integer activity_id,
            String date_start, String date_end, Integer req_month, Integer req_year, 
            Boolean club_cal, Boolean events_only, List<String> string_list,
            String data){
        this.command = command;
        this.as_object = as_object;
        this.exclude_disabled = exclude_disabled;
        this.id = id;
        this.detail_id = detail_id;
        this.club_id = club_id;
        this.parent_id = parent_id;
        
        this.member_id = member_id;
        this.activity_id = activity_id;
        this.date_start = date_start;
        this.date_end = date_end;
        this.data = data;  
        
        this.req_month = req_month;
        this.req_year = req_year; 
        this.club_cal = club_cal;  
        this.string_list = string_list;
        this.events_only = events_only;
        
    }
    
    public final boolean validateStartDate(){
        return validateDate(date_start);
    }
    
    public final boolean validateEndDate(){
        return validateDate(date_end);
    }
    
    public final boolean validateDates(){
        if(validateDate(date_start)){
            return validateDate(date_end);
        } else {
            return false;
        }
    }
    
    public final boolean setValidOrNullDates(){
        date_start = validOrNullDate(date_start);
        date_end = validOrNullDate(date_end);
        return last_error == null;
    }
    
    public final boolean validateDate(String testDate){
        boolean result = timeUtil.isValidDate(testDate);
        if(!result){
            if(testDate == null){
                last_error = "Inavlid date: null";
            } else {
                last_error = "Inavlid date: "+testDate;
            }
        }
        return result;
    }
    
    public final String validOrNullDate(String testDate){
        if(testDate == null || testDate.isEmpty()){
            return null;
        }
        if(!timeUtil.isValidDate(testDate)){
            last_error = "Inavlid date: null";
            return null;
        }
        return testDate;
    }
    
    public final Long getUnixStartDate(){
        Long result = null;
        try{
            result = timeUtil.getUnixTimeFromDb(date_start);
        }catch(Exception e){
            if(date_start == null){
                last_error = "Inavlid start date: null";
            } else {
                last_error = "Inavlid start date: "+date_start;
            }
        }
        return result;
    }
    
}
