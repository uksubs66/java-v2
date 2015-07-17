/***************************************************************************************
 *   parmWaitList:  This class will define a paramter block object to be used for the wait list.
 *
 *
 *   called by:  several
 *
 *
 *   created: 04/19/2008   Paul S.
 *
 *
 *   last updated:
 *                  
 *          10/16/08  Changed auto_assign (was unused) to member_view_teesheet
 *
 *
 ***************************************************************************************
 */

package com.foretees.common;


public class parmWaitList {


    public String name = "";
    public String course = "";
    public String color = "";
    public String notice = "";
    
    public int wait_list_id = 0;
    
    public int start_date = 0;
    public int start_time = 0;
    public int end_date = 0;
    public int end_time = 0;
    
    public int cutoff_days = 0;
    public int cutoff_time = 0;
    
    public int sunday = 0;
    public int monday = 0;
    public int tuesday = 0;
    public int wednesday = 0;
    public int thursday = 0;
    public int friday = 0;
    public int saturday = 0;
    
    public int max_list_size = 0;
    public int max_team_size = 0;
    
    public int member_access = 0;           // can members put themselves on the wait list
    public int member_view = 0;             // view wait list signups
    
    public int member_view_teesheet = 0;    // view tee times covered by wait list (0=no, 1=see, 2=access)
    
    public int allow_guests = 0;
    public int allow_x = 0;
    
    public int enabled = 0;
    
}