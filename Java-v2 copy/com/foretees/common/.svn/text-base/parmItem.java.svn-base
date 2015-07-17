/***************************************************************************************
 *   parmItem:  This class will define an event, lottery, wait list or other occurance
 *              that needs to be handled when building a tee sheet
 *
 *
 *   called by:  several
 *
 *   created: 06/18/2008   Paul
 *
 *   last updated:
 *
 *          10/22/09   Add act_time for shotgun events
 *          10/16/08   Changed unused db/parm field auto_assign to member_view_teesheet
 *
 *  notes:
 *          
 *
 ***************************************************************************************
 */

package com.foretees.common;

import java.sql.*;
import java.util.*;


public class parmItem {
    
    public final static int MAX         = 10;
    public final static int EVENT       = 1;
    public final static int LOTTERY     = 2;
    public final static int WAITLIST    = 3;
    
    public int count = 0;     // number of items of this type found
    
    public int [] itemType = new int [MAX]; // the type of item this parm is holding
    
    public String [] name = new String [MAX];
    public String [] color = new String [MAX];
    public String [] courseName = new String [MAX];
    
    public int [] stime = new int [MAX];
    public int [] etime = new int [MAX];
    public int [] fb = new int [MAX];
    public int [] id = new int [MAX];               // uid for said item in the db table
    public int [] signups = new int [MAX];          // number of signups for said item (if applicable)
    public int [] unc_signups = new int [MAX];      // number of unconverted signups for said item (if applicable)
    
    public boolean [] access = new boolean [MAX];   // can member access this item from tee sheet page
    
    // lottery specific variables
    public int [] lstate = new int [MAX];
    
    // event specific variables
    public boolean [] season_long = new boolean [MAX];
    public int [] event_type = new int [MAX];
    public int [] act_time = new int [MAX];        // actual start time (for shotguns)
    public int [] stime2 = new int [MAX];
    public int [] etime2 = new int [MAX];
    public int [] fb2 = new int [MAX];
    
    // wait list specific variables
    public int [] member_view_teesheet = new int [MAX];
}