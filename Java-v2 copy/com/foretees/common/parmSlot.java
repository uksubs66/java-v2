/***************************************************************************************
 *   parmSlot:  This class will define a paramter block object to be used for tee time verification.
 *
 *
 *   called by:  Proshop_slot
 *               Proshop_slotm
 *               Proshop_lott
 *               Proshop_dsheet
 *               Proshop_oldsheets
 *               Member_slot
 *               Member_slotm
 *               Member_lott
 *
 *   created: 1/12/2004   Bob P.
 *
 *   last updated: 
 *
 *                  1/08/14  Added event_type int.
 *                  1/06/14  Added search_other_times for use with FlxRez alternate time searching.
 *                 10/10/13  Add make_private int for use in hiding tee time/data on tee sheets.
 *                  7/16/12  Add nopost ints for new handicap indicators.
 *                  2/17/11  Replaced disallow_joins variable with force_singles
 *                  7/20/10  Added proShortName (case 1555).
 *                  5/07/10  Added orig1-5 to track who originated each player in the time
 *                  4/15/10  Added hit4 boolean for use with guest tracking error reporting
 *                  2/12/09  Add guest_id1-5 and oldguest_id1-5 for holding guest tracking ids
 *                  1/05/09  Add report_ignore for consecutive activity times
 *                 12/30/09  Add slots for consecutive activity times
 *                 11/20/09  Add displayOpt for mobile interface.
 *                 10/16/09  Add layout_mode for activities.
 *                  8/28/09  Add showlott string
 *                  1/26/09  Add skipDining string
 *                  6/26/08  Add teecurr_id so we can more easily identify the tee time.
 *                  6/14/08  Add name & hideTimes for dsheet
 *                  4/23/08  Add tflag strings (case 1357).
 *                  4/19/08  Add numerous values for the wait list feature (noted under wait list comment)
 *                  2/27/08  Add blocker so we can verify member request in Member_slot.
 *                  6/27/07  Add event string
 *                  4/13/07  Add custom_int, custom_string and custom_dispx for custom fields in teecurr.
 *                 12/09/05  Add suppressEmails string.
 *
 *
 ***************************************************************************************
 */
   
package com.foretees.common;

import java.util.*;
import java.lang.reflect.*;
import org.apache.commons.lang.*;

public class parmSlot extends parmCore {

   public boolean error = false;
   public boolean hit = false;
   public boolean hit2 = false;
   public boolean hit3 = false;
   public boolean hit4 = false;
     
   public int MAX_Guests = Labels.MAX_GUESTS;         // IN Labels
   public int MAX_Mems = Labels.MAX_MEMS;
   public int MAX_Mships = Labels.MAX_MSHIPS;
   public int MAX_Tmodes = Labels.MAX_TMODES;

   public long date = 0;

   public int in_use = 0;
   public int time = 0;
   public int time2 = 0;            // time of existing tee time (1 player with 2 times in a day)
   public int stime = 0;
   public int etime = 0;
   public int fb = 0;
   public int mm = 0;
   public int dd = 0;
   public int yy = 0;
   public int inval1 = 0;
   public int inval2 = 0;
   public int inval3 = 0;
   public int inval4 = 0;
   public int inval5 = 0;
   public int members = 0;
   public int players = 0;
   public int hide = 0;
   public int make_private = 0;
   public int mins_before = 0;
   public int mins_after = 0;
   public int beforei = 0;
   public int afteri = 0;
   public int ftime = 0;
   public int ltime = 0;
   public int groups = 0;           // didn't see to be used - regardless using it now for when we populate this with data from parmSlotM to indicate which group it contains
   public int grps = 0;
   public int i3 = 0;
   public int guests = 0;
   public int memNew = 0;
   public int memMod = 0;
   public int proNew = 0;
   public int proMod = 0;
   public int grest_num = 0;
   public int ind = 0;              // numeric value of index
   public int p91 = 0;              // 9 hole indicators
   public int p92 = 0;
   public int p93 = 0;
   public int p94 = 0;
   public int p95 = 0;
   
   public int teecurr_id = 0;

   public int oldp91 = 0;
   public int oldp92 = 0;
   public int oldp93 = 0;
   public int oldp94 = 0;
   public int oldp95 = 0;
    
   public int rnds = 0;
   public int hrsbtwn = 0;
   
   public int nopost1 = 0;
   public int nopost2 = 0;
   public int nopost3 = 0;
   public int nopost4 = 0;
   public int nopost5 = 0;

   // parms used by FlxRez
   public int activity_id = 0;          // which activity
   public int root_activity_id = 0;     // same as local sess_activity_id var
   public int slot_id = 0;
   public int group_id = 0;
   public int force_singles = 0;       // flag to indicate the members do not anyone else joining their reservation
   public int layout_mode = 0;          // Activities - layout mode passed from sheet (save for return)
   public int slots = 0;                // Number of consecutive times requested
   public ArrayList<Integer> sheet_ids = new ArrayList<Integer>(); // holds the uid for each consecutive time slot found
   public String in_slots = "";         // holds an 'in' string (for sql) containing a csv list of the sheet_ids array
   public int report_ignore = 0;        // flag to indicate that this time slot should be ignored in reports and when counting existing times for this day
   public int search_other_times = 0;
   public int event_type = 0;

   //public int returnAct_id = 0;       // which activity to return to

   // parms used by wait list
   public int signup_id = 0;            // used to hold a wait list entry uid (and by activities to refer to the slot_id)
   public int wait_list_id = 0;         // which wait list are they signing up for
   public int converted = 0;
   public int course_id = 0;
   public int hideNotes = 0;
   public int ok_stime = 0;
   public int ok_etime = 0;
   public String orig_at = "";
   
   // parms used by Proshop_dsheet
   public int from_player = 0;
   public int to_player = 0;
   public int from_time = 0;
   public int to_time = 0;
   public int from_fb = 0;
   public int to_fb = 0;
   public String to_course = "";
   public String from_course = "";
   public String to_from = "";
   public String to_to = "";
   public String sendEmail = "";
   public String name = "";         // holds name of event or lottery
   public String hideTimes = "";    // hold flag to either show all tee times or only show event/lottery/waitlist tee times
     

   public short rfb = 0;
   public short show1 = 0;
   public short show2 = 0;
   public short show3 = 0;
   public short show4 = 0;
   public short show5 = 0;

   public short pos1 = 0;
   public short pos2 = 0;
   public short pos3 = 0;
   public short pos4 = 0;
   public short pos5 = 0;

   public float hndcp1 = 0;
   public float hndcp2 = 0;
   public float hndcp3 = 0;
   public float hndcp4 = 0;
   public float hndcp5 = 0;

   public String course = "";
   public String returnCourse = "";
   public String course2 = "";      // course name for error msg
   public String day = "";
   public String player = "";
   public String period = "";
   public String rest_name = "";
   public String hides = "";
   public String notes = "";
   public String index = "";
   public String p5 = "";
   public String p5rest = "";
   public String rest5 = "";
   public String jump = "";
   public String sfb = "";
   public String in_use_by = "";
   public String orig_by = "";
   public String conf = "";
   public String club = "";
   public String user = "";
   public String last_user = "";
   public String gplayer = "";      // guest player in error - Member_slot
   public String grest_per = "";    // guest restriction per option (Member or Tee Time)
   public String lottery = "";
   public String lottery_type = "";
   public String suppressEmails = "";
   public String skipDining = "";
   public String event = "";
   public String blocker = "";
   public String showlott = "";
   public String displayOpt = "";
   public String proShortName = "";      // name of proshop user that is making or changing the tee time

   public String player1 = "";
   public String player2 = "";
   public String player3 = "";
   public String player4 = "";
   public String player5 = "";
   public String player6 = "";
   public String player7 = "";
   public String player8 = "";
   public String player9 = "";
   public String player10 = "";
   public String player11 = "";
   public String player12 = "";
   public String player13 = "";
   public String player14 = "";
   public String player15 = "";
   public String player16 = "";
   public String player17 = "";
   public String player18 = "";
   public String player19 = "";
   public String player20 = "";
   public String player21 = "";
   public String player22 = "";
   public String player23 = "";
   public String player24 = "";
   public String player25 = "";

   public String oldPlayer1 = "";
   public String oldPlayer2 = "";
   public String oldPlayer3 = "";
   public String oldPlayer4 = "";
   public String oldPlayer5 = "";

   public String user1 = "";
   public String user2 = "";
   public String user3 = "";
   public String user4 = "";
   public String user5 = "";

   public String userg1 = "";       //  member usernames associated with guests
   public String userg2 = "";
   public String userg3 = "";
   public String userg4 = "";
   public String userg5 = "";

   public String orig1 = "";
   public String orig2 = "";
   public String orig3 = "";
   public String orig4 = "";
   public String orig5 = "";

   public int guest_id1 = 0;
   public int guest_id2 = 0;
   public int guest_id3 = 0;
   public int guest_id4 = 0;
   public int guest_id5 = 0;

   public int oldguest_id1 = 0;
   public int oldguest_id2 = 0;
   public int oldguest_id3 = 0;
   public int oldguest_id4 = 0;
   public int oldguest_id5 = 0;

   public String mem1 = "";         // member names used for Unaccompanied Guests (tied to userg above)
   public String mem2 = "";
   public String mem3 = "";
   public String mem4 = "";
   public String mem5 = "";

   public String oldUser1 = "";
   public String oldUser2 = "";
   public String oldUser3 = "";
   public String oldUser4 = "";
   public String oldUser5 = "";

   public String p1cw = "";
   public String p2cw = "";
   public String p3cw = "";
   public String p4cw = "";
   public String p5cw = "";

   public String oldp1cw = "";
   public String oldp2cw = "";
   public String oldp3cw = "";
   public String oldp4cw = "";
   public String oldp5cw = "";

   public String mNum1 = "";
   public String mNum2 = "";
   public String mNum3 = "";
   public String mNum4 = "";
   public String mNum5 = "";

   public String pnum1 = "";
   public String pnum2 = "";
   public String pnum3 = "";
   public String pnum4 = "";
   public String pnum5 = "";

   public String mship = "";
   public String mship1 = "";
   public String mship2 = "";
   public String mship3 = "";
   public String mship4 = "";
   public String mship5 = "";

   public String mtype = "";      // member types
   public String mtype1 = "";
   public String mtype2 = "";
   public String mtype3 = "";
   public String mtype4 = "";
   public String mtype5 = "";

   public String mstype1 = "";      // member sub_types
   public String mstype2 = "";
   public String mstype3 = "";
   public String mstype4 = "";
   public String mstype5 = "";

   public String fname1 = "";
   public String lname1 = "";
   public String mi1 = "";
   public String fname2 = "";
   public String lname2 = "";
   public String mi2 = "";
   public String fname3 = "";
   public String lname3 = "";
   public String mi3 = "";
   public String fname4 = "";
   public String lname4 = "";
   public String mi4 = "";
   public String fname5 = "";
   public String lname5 = "";
   public String mi5 = "";

   public String g1 = "";
   public String g2 = "";
   public String g3 = "";
   public String g4 = "";
   public String g5 = "";

   public String tflag1 = "";
   public String tflag2 = "";
   public String tflag3 = "";
   public String tflag4 = "";
   public String tflag5 = "";

   //
   //  Custom fields for making custom processing easier
   //
   public String custom_string = "";
   public String custom_disp1 = "";
   public String custom_disp2 = "";
   public String custom_disp3 = "";
   public String custom_disp4 = "";
   public String custom_disp5 = "";
   public int custom_int = 0;

   //
   //  guest array for Member_lott
   //
   public String [] gstA = new String [5];     // guests (entire player name)
   public String [] g = new String [5];        // guest type of player position (if guest)
   
}  // end of class
