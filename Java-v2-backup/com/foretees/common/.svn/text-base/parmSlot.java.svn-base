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
 *n
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
import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;

import com.google.gson.*; // for json

public class parmSlot extends parmCoreAlt {

   //public boolean user_has_lock = false;
   public boolean user_has_partial_lock = false;
    
   public boolean error = false;
   public boolean hit = false;
   public boolean hit2 = false;
   public boolean hit3 = false;
   public boolean hit4 = false;
   public boolean fives = true;
   
   public boolean member_tbd = true;
   public boolean member_select = true;
   public boolean guest_select = true;
   
   public boolean allow_cancel = true;
   public boolean allow_edit = false;
   
   public boolean new_request = false;
   
   public boolean max_orig = false;
   
   public boolean use_gift_pack = false;
   
   public int MAX_Guests = Labels.MAX_GUESTS;         // IN Labels
   public int MAX_Mems = Labels.MAX_MEMS;
   public int MAX_Mships = Labels.MAX_MSHIPS;
   public int MAX_Tmodes = Labels.MAX_TMODES;

   public long date = 0;
   
   public long lock_time_remaining = 0; 

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
   public int visible_players_restricted = 0; // This is the maximum visible, as set by any restictions/fives
   public int visible_players = 0; // This is the maximum visible, could be higher than restriction if there are more players than the restrictions allows
   public int max_visible_players = 0; // In a group of times, this is the maximum visible_players of all times in the group
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
   
   public long teecurr_id = 0;

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
   
   public int precheckin = 0;

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
   public String real_name = ""; // hold real ame of current user     
   public int lott_id = 0;

   public short rfb = 0;
   public short show1 = 0;
   public short show2 = 0;
   public short show3 = 0;
   public short show4 = 0;
   public short show5 = 0;
   
   public short[] oldShow = new short[5];

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
   
   public float[] oldHndcp = new float[5];

   public String course = "";
   public String returnCourse = "";
   public String course2 = "";      // course name for error msg
   public String day = "";
   public String player = "";
   public String period = "";
   public String rest_name = "";
   public String hides = "";
   public String notes = "";
   public String oldNotes = "";
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
   
   //public String user_mship = "";

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
   
   public String oldUser1 = "";
   public String oldUser2 = "";
   public String oldUser3 = "";
   public String oldUser4 = "";
   public String oldUser5 = "";

   public String userg1 = "";       //  member usernames associated with guests
   public String userg2 = "";
   public String userg3 = "";
   public String userg4 = "";
   public String userg5 = "";
   
   public String[] oldUserg = new String[5];

   public String orig1 = "";
   public String orig2 = "";
   public String orig3 = "";
   public String orig4 = "";
   public String orig5 = "";
   
   public String[] oldOrig = new String[5];

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
   
   public String[] oldCustom_disp = new String[5];
   
   public int custom_int = 0;
   public int oldCustom_int = 0;
  
   public int sendemail = 0;
   public int emailNew = 0;
   public int emailMod = 0;
   public int emailCan = 0;
   public boolean congressGstEmail = false;
   public boolean sendShadyCanyonNotesEmail = false;
   
   //
   // To be used by Member_slot / foreTeesSlot
   //
   public boolean lock_player[] = new boolean[5]; // Don't allow player data to change or move position
   //public boolean lock_position[] = new boolean[5]; // Don't allow player position to change

   //
   //  guest array for Member_lott
   //
   public String [] gstA = new String [5];     // guests (entire player name)
   public String [] g = new String [5];        // guest type of player position (if guest)
  
   //
   // Used for tee time groupings
   //
   public List<String> group_owner_names = new ArrayList<String>();
   public List<Long> group_owner_ids = new ArrayList<Long>();
   public List<Long> group_ids = new ArrayList<Long>();
   public List<teeCurrGroupDetail> group_details = new ArrayList<teeCurrGroupDetail>();
   public List<parmSlot> group_slots = new ArrayList<parmSlot>();
   public int group_index = 0;
   //public String group_owner = "";
   
   public parmClub club_parm;
   public parmCourse course_parm = new parmCourse(); 
   
   public String block_reason = null;
   
   private Map<String, reservationPlayer> playerLookupMap = null;
   private Map<String, reservationPlayer> userLookupMap = null;
   private Map<String, List<reservationPlayer>> cwLookupMap = null;
   private List<reservationPlayer> playerList = null;
   
   private Map<Integer, Integer> modified = null;
   private Map<Integer, Integer> positionChange = null;
   private Map<Integer, Integer> onlyPositionChange = null;
   private Set<Integer> newPlayers = null;
   private Map<Integer, Integer> cwmodified = null;
   private Map<Integer, Integer> p9modified = null;
   private Map<Integer, Integer> unmodified = null;
   private Map<Integer, Integer> nameunmodified = null;
   private Map<Integer, Integer> gmodified = null;
   private Map<Integer, Integer> onlygmodified = null;
   private Set<Integer> removed = null;
   private Set<Integer> empty = null;
   
   private Integer[] moved_to_group = new Integer[5]; // Used to track moving players from one time in a group to another
   
   //private Map<String, List<reservationPlayer>> userGuestLookupMap = null;
   
   public parmSlot(){
       // Do nothing
   }
   
    public parmSlot(long date, int time, int fb, String course, HttpServletRequest req) {

        loadSlot(date, time, fb, course, req);
   
   }
    
    public parmSlot(long teecurr_id, HttpServletRequest req) {

        loadSlot(teecurr_id, req);
   
   }
    
    public parmSlot(ResultSet rs, HttpServletRequest req) {

        loadSlot(rs, req);
   
   }
    
    public final void setOld(){
        //String[] emptyS = new String[5];
        //int[] emptyI = new int[5];
        //Arrays.fill(emptyS, "");
        //Arrays.fill(emptyI, 0);
        
        this.setOldPlayer(this.getPlayerArray(5));
        //this.setParameterByName("player%", emptyS);
        this.setOldUser(this.getUserArray(5));
        //this.setParameterByName("user%", emptyS);
        oldUserg = this.getUsergArray(5);
        //this.setParameterByName("userg%", emptyS);
        this.setOldCw(this.getCwArray(5));
        oldOrig = this.getOrigArray(5);
        oldShow = this.getShowArray(5);
        //this.setParameterByName("p%cw", emptyS);
        oldCustom_disp = this.getCustomDispArray(5);
        this.setOldP9(this.getP9Array(5));
        //this.setParameterByName("p9%", emptyI);
        this.setOldGuestId(this.getGuestIdArray(5));
        //this.setParameterByName("guest_id%", emptyI);
        this.oldNotes = this.notes;
        this.oldCustom_int = this.custom_int;
    }
  
    public final void loadSlot(long date, int time, int fb, String course, HttpServletRequest req) {

        Connection con = Connect.getCon(req);
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            
            pstmt = con.prepareStatement(
                            "SELECT t.*, c.fives, "
                            + "   GROUP_CONCAT(m.username ORDER BY g.id SEPARATOR '||') as group_owner_names, "
                            + "   GROUP_CONCAT(m.id ORDER BY g.id SEPARATOR ',') as group_owner_ids, "
                            + "   GROUP_CONCAT(g.id ORDER BY g.id SEPARATOR ',') as group_ids "
                            + " FROM teecurr2 t "
                            + "   LEFT OUTER JOIN teecurr2_group_details gd "
                            + "     ON gd.teecurr2_id = t.teecurr_id "
                            + "   LEFT OUTER JOIN teecurr2_groups g "
                            + "     ON g.id = gd.teecurr2_group_id "
                            + "   LEFT OUTER JOIN member2b m "
                            + "     ON m.id = g.member_id "
                            + "   LEFT OUTER JOIN clubparm2 c ON c.courseName = t.courseName "
                            + " WHERE t.date = ? "
                            + "  AND t.time = ? "
                            + "  AND t.fb = ? "
                            + "  AND t.courseName = ? "
                            + " GROUP BY t.teecurr_id ");

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, date);         // put the parm in pstmt
            pstmt.setInt(2, time);
            pstmt.setInt(3, fb);
            pstmt.setString(4, course);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            loadSlot(rs, req);

            pstmt.close();

        } catch (Exception e) {
            // Error
            Utilities.logError("parmSlot.loadSlot: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));

        } finally {
            Connect.close(rs, pstmt);
        }

    }

    public final void loadSlot(long teecurr_id, HttpServletRequest req) {

        Connection con = Connect.getCon(req);
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            
            pstmt = con.prepareStatement(
                            "SELECT t.*, c.fives, "
                            + "   GROUP_CONCAT(m.username ORDER BY g.id SEPARATOR '||') AS group_owner_names, "
                            + "   GROUP_CONCAT(m.id ORDER BY g.id SEPARATOR ',') AS group_owner_ids, "
                            + "   GROUP_CONCAT(g.id ORDER BY g.id SEPARATOR ',') AS group_ids "
                            + " FROM teecurr2 t "
                            + "   LEFT OUTER JOIN teecurr2_group_details gd "
                            + "     ON gd.teecurr2_id = t.teecurr_id "
                            + "   LEFT OUTER JOIN teecurr2_groups g "
                            + "     ON g.id = gd.teecurr2_group_id "
                            + "   LEFT OUTER JOIN member2b m "
                            + "     ON m.id = g.member_id "
                            + "   LEFT OUTER JOIN clubparm2 c ON c.courseName = t.courseName "
                            + " WHERE teecurr_id = ? "
                            + " GROUP BY t.teecurr_id ");

            pstmt.clearParameters();        // clear the parms
            pstmt.setLong(1, teecurr_id);         // put the parm in pstmt
            rs = pstmt.executeQuery();      // execute the prepared stmt

            loadSlot(rs, req);

            pstmt.close();

        } catch (Exception e) {
            // Error
            Utilities.logError("parmSlot.loadSlot: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));

        } finally {
            Connect.close(rs, pstmt);
        }


    }

    public final void loadSlot(ResultSet rs, HttpServletRequest req) {

        try {

            if (rs.next()) {
                loadSlotFromResultSetCurrentPosition(rs, req);
            }

        } catch (Exception e) {
            // Error
            Utilities.logError("parmSlot.loadSlot: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));

        }

    }
    
    public final void loadSlotFromResultSetCurrentPosition(ResultSet rs, HttpServletRequest req) {

        Connection con = Connect.getCon(req);
        
        try {

            this.date = rs.getLong("date");
            this.time = rs.getInt("time");
            this.fb = rs.getInt("fb");
            this.course = rs.getString("courseName");
            this.teecurr_id = rs.getLong("teecurr_id");
            lottery = rs.getString("lottery");
            last_user = rs.getString("in_use_by");
            hide = rs.getInt("hideNotes");
            blocker = rs.getString("blocker");
            rest5 = rs.getString("rest5");
            orig_by = rs.getString("orig_by");
            conf = rs.getString("conf");
            custom_int = rs.getInt("custom_int");
            make_private = rs.getInt("make_private");
            event = rs.getString("event");
            event_type = rs.getInt("event_type");
            memNew = rs.getInt("memNew");
            memMod = rs.getInt("memMod");
            proNew = rs.getInt("proNew");
            proMod = rs.getInt("proMod");
            in_use = rs.getInt("in_use");
            notes =  rs.getString("notes");
            
            this.setPlayer(ArrayUtil.getStringArrayFromResultSet(rs, "player%", 5));
            this.setUser(ArrayUtil.getStringArrayFromResultSet(rs, "username%", 5));
            this.setCw(ArrayUtil.getStringArrayFromResultSet(rs, "p%cw", 5));
            this.setHndcp(ArrayUtil.getFloatArrayFromResultSet(rs, "hndcp%", 5));
            this.setShow(ArrayUtil.getShortArrayFromResultSet(rs, "show%", 5));
            this.setMnum(ArrayUtil.getStringArrayFromResultSet(rs, "mNum%", 5));
            this.setUserg(ArrayUtil.getStringArrayFromResultSet(rs, "userg%", 5));
            this.setGuestId(ArrayUtil.getIntegerArrayFromResultSet(rs, "guest_id%", 5));
            this.setP9(ArrayUtil.getIntegerArrayFromResultSet(rs, "p9%", 5));
            this.setPos(ArrayUtil.getShortArrayFromResultSet(rs, "pos%", 5));
            this.setCustomDisp(ArrayUtil.getStringArrayFromResultSet(rs, "custom_disp%", 5));
            this.setTflag(ArrayUtil.getStringArrayFromResultSet(rs, "tflag%", 5));
            this.setOrig(ArrayUtil.getStringArrayFromResultSet(rs, "orig%", 5));
            this.setNoPost(ArrayUtil.getIntegerArrayFromResultSet(rs, "nopost%", 5));
            
            group_ids = ArrayUtil.getLongListFromDelemetedField(rs.getString("group_ids"), ",");
            group_owner_ids = ArrayUtil.getLongListFromDelemetedField(rs.getString("group_owner_ids"), ",");
            group_owner_names = ArrayUtil.getStringListFromDelemetedField(rs.getString("group_owner_names"), "\\|\\|");
            // Force all group owner names to lower case for comparison, since we can't trust the case of
            // user names have been stored properly (this is one of the reasons for transitioning to member ids 
            // for record binding, but much more needs to be converted to member ids before it can be used in all cases.)
            for(int i = 0; i < group_owner_names.size(); i++){
                group_owner_names.set(i, group_owner_names.get(i).toLowerCase());
            }
            
            int[] dateA = timeUtil.parseIntDate((int) date);
            yy = dateA[timeUtil.YEAR];
            dd = dateA[timeUtil.DAY];
            mm = dateA[timeUtil.MONTH];    

            day = timeUtil.getDayOfWeek((int) date);
            
            group_details = reservationUtil.getTeeCurrGroupDetails(teecurr_id, con);
            
            club = reqUtil.getSessionString(req, "club", "");
            user = reqUtil.getSessionString(req, "user", reqUtil.getSessionString(req, "ext-user", ""));
            real_name = reqUtil.getSessionString(req, "name", "");
            mship = reqUtil.getSessionString(req, "mship", "");
            
            fives = (rs.getString("rest5").isEmpty() && rs.getInt("fives") == 1);
            p5 = (fives?"Yes":"No");
            
            ind = timeUtil.daysBetween(timeUtil.getClubDate(con), (int) date); // Get "index"
            index = Integer.toString(ind);
            
            new_request = !hasPlayers();
            
            user_has_partial_lock = (user.equalsIgnoreCase(last_user) && in_use > 0);
            //user_has_lock = (user_has_partial_lock && verifySlot.checkInSession(date, time, fb, course, req));
            lock_time_remaining = verifySlot.getInUseTimeRemaining(date, time, fb, course, req);

        } catch (Exception e) {
            // Error
            Utilities.logError("parmSlot.loadSlotFromResultSetCurrentPosition: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));

        }

    }
    
    public boolean userHasLock(HttpServletRequest req){
        return (user_has_partial_lock && verifySlot.checkInSession(date, time, fb, course, req));
    }
    
    public void setAccessOnGroup(HttpServletRequest req){
        
        Connection con = Connect.getCon(req);
        for(parmSlot parm : group_slots){
            
            // Get club parm (there are club parm customs for course, so needs to be per tee time)
            parm.club_parm = new parmClub(0, con);
            try {
                parm.club_parm.club = parm.club;                   // set club name
                parm.club_parm.course = parm.course;               // and course name
                getClub.getParms(con, parm.club_parm);        // get the club parms
                //
                //  if Forest Highlands, do not allow any X's (only pros can use them)
                //
                if (club.equals("foresthighlands") && !user.startsWith("proshop")) {
                    parm.club_parm.x = 0;
                }
                parm.member_tbd = (parm.club_parm.x != 0);
                parm.rnds = parm.club_parm.rnds;
                parm.hrsbtwn = parm.club_parm.hrsbtwn;
            } catch (Exception exc) {             // SQL Error - ignore guest and x

            }
            //  retrieve course parameters
            try {
                getParms.getTmodes(con, course_parm, course);
            } catch (Exception e) {
            }

            parm.setPlayerCounts(req);
            parm.setAccess(req);
            
        }
        
        
    }
    
    private void setPlayerCounts(HttpServletRequest req) {

        max_visible_players = 0;
        if (group_slots == null || group_slots.size() == 0) {
            List <parmSlot> groupList = new ArrayList<parmSlot>();
            groupList.add(this);
            group_slots = groupList;
            
        }
        for (parmSlot sParm : group_slots) {

            // count players
            sParm.players = 0; // Player count
            //int lp = 0; // Last position with player (0 = no players)
            int i = 0;
            for (String playerName : Arrays.asList(sParm.getPlayerArray(5))) {
                i++;
                if (!playerName.isEmpty()
                        && !playerName.equalsIgnoreCase("x") // NOTE: These filters may not be needed or wanted
                        //&& !playerName.equalsIgnoreCase("Join Me")
                        && !playerName.equalsIgnoreCase("Need A Player")
                        ) {
                    //sParm.players++;
                    //lp = i;
                    sParm.players = i;
                }
            }
            sParm.visible_players_restricted = reservationUtil.getMaxPlayersForTeeTime(req, (int) date, time, course, fb, sParm.fives);
            // If a player is in a position higher than any retriction allows, set it.
            sParm.visible_players = Math.max(sParm.visible_players_restricted, sParm.players);
            if (sParm.visible_players > max_visible_players) {
                max_visible_players = sParm.visible_players;
            }
            
            // Set lock on non visible players
            //for(i = (visible_players-1); i < 5; i++){
            //    sParm.lock_player[i] = true;
            //}

        }
        for (parmSlot sParm : group_slots) {
            sParm.max_visible_players = max_visible_players;
        }

    }
    
    // Get a set of originator/users by name
    public Set<String> getAccessibleUsers(String user){
        String[] userA = this.getUserArray(5);
        String[] origA = this.getOrigArray(5);
        String[] usergA = this.getUsergArray(5);
        return getAccessibleUsers(user, userA, origA, 0);
    }
    
    private Set<String> getAccessibleUsers(String user, String[] userA, String[] origA, int depth){
        Set<String> origs = new HashSet<String>();
        
        if(!user.isEmpty()){
            origs.add(user.toLowerCase());
        }
        if(depth < 6 && !user.isEmpty()){
            for(int i = 0; i < userA.length; i++){
                if(user.equalsIgnoreCase(userA[i]) || user.equalsIgnoreCase(origA[i])){
                    origs.addAll(getAccessibleUsers(userA[i], userA, origA, depth+1));
                    origs.addAll(getAccessibleUsers(origA[i], userA, origA, depth+1));
                }
            }
        }
        
        return origs;
    }
    
    // Check if every position with a player is one of our descendants
    public boolean isDescendantOrOrigOfAll(String user){
        for(int i = 0; i < 5; i++){
            if(isPlayer(i) && !isDescendantOrOrigOf(i, user)){
                return false;
            }
        }
        return true;
    }
    
    // Find out of the user is the user, orig_of, or originated the parent of the player index requested.
    public boolean isDescendantOrOrigOf(int index, String user){
        return isDescendantOrOrigOf(index, user, 0);
    }
    private boolean isDescendantOrOrigOf(int index, String user, int depth){
   
        if(depth > 25){ // If we've gone over 25 deep, we're certainly stuck in a regressive loop.  Get out of it.
            return false;
        }
        if(user == null || user.isEmpty()){
            return false; // Can't search for a null or empty orig by
        }
        String u = getUser(index);
        String o = getOrig(index);
        String ug = getUserg(index);
        if(ug.equalsIgnoreCase(user) || u.equalsIgnoreCase(user) || o.equalsIgnoreCase(user)){
            return true; // This position is originated by, a guest of, or is the user we're searching for
        } else if (o.isEmpty()) {
            return false; // Empty user.  
        } else if(u.equalsIgnoreCase(o) && !u.isEmpty()) {
            return false; // user originated self, and is not user we're searching for.  Not descendant
        } else {
            Integer pi = indexOfUser(o);
            if(pi != null && !pi.equals(index) && isDescendantOrOrigOf(pi, user, depth+1)) {
                return  true;// This position is a descendant of one of our descendants.
            } else if(!ug.isEmpty()) {
                // Search for this guest's responsible user
                // Is guests's user in the time?
                pi = indexOfUser(ug);
                if(pi != null && !pi.equals(index) && isDescendantOrOrigOf(pi, user, depth+1)){
                    return true; // The user responsible for this guest is a descendant of one of our descendants.
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
      
    }
    
    private void setAccess(HttpServletRequest req){
        
        Connection con = Connect.getCon(req);
        //
        //  Check access based off original slot -- MUST BE CALLED BEFORE ANY MODIFICATION OF SLOT
        //
        
        String[] playerA = this.getPlayerArray(5);
        String[] userA = this.getUserArray(5);
        String[] usergA = this.getUsergArray(5);
        String[] origA = this.getOrigArray(5);
        String[] custom_dispA = this.getCustomDispArray(5);
        boolean[] force_lock = new boolean[5];
        Set<String> accessibleUsers = getAccessibleUsers(user, userA, origA, 0);
        
        int thisTimeAdjusted = timeUtil.getClubTime(req);
        
        int slotAccessMethod = Utilities.getSlotAccessMethod(req);
        boolean restrictToCurrentPlayers = false;
        boolean restrictByOrig = false;
        boolean alwayslAllowSlotOrig = false;
        boolean lockOpenPositions = false;
        boolean blockCancel = false;
        boolean restrictByDescendants = false;
        boolean restrictByAncestorsAndDescendants = false;
        boolean isOrigBy = orig_by.equalsIgnoreCase(user);
        boolean isGroupOwner = this.group_owner_names.contains(user.toLowerCase());
        boolean partOfOrig = accessibleUsers.contains(orig_by.toLowerCase()); // Current user is part of the "origination group"
        boolean partOfReservation = this.hasUser(user);
        
        boolean clubUsesGroupEdit = Utilities.usesGroupedSlotEdit(req);
        boolean timeIsPartOfGroup = clubUsesGroupEdit && this.group_ids.size() > 0; // If the group is just being created, it's possible there is only 1 time in the gorup.
        boolean timeInMultipleGroups = clubUsesGroupEdit && this.group_ids.size() > 1;  // This time is part of multiple "groups" (Proshop could make this happen when draaing and dropping times)
        
        
        if (timeIsPartOfGroup) {
            alwayslAllowSlotOrig = isGroupOwner;
            restrictToCurrentPlayers = Utilities.getBit(slotAccessMethod, ProcessConstants.SAM_GROUP_RESTRICTION);
            Utilities.getBit(slotAccessMethod, ProcessConstants.SAM_GROUP_RESTRICTION);
            lockOpenPositions = Utilities.getBit(slotAccessMethod, ProcessConstants.SAM_GROUP_NO_ADD) && !isGroupOwner;
            restrictByOrig = Utilities.getBit(slotAccessMethod, ProcessConstants.SAM_GROUP_RESTRICT_ORIG);
            blockCancel = Utilities.getBit(slotAccessMethod, ProcessConstants.SAM_GROUP_NO_CANCEL) && (!isGroupOwner || timeInMultipleGroups);
            restrictByDescendants = Utilities.getBit(slotAccessMethod, ProcessConstants.SAM_GROUP_DESCENDANTS);
            restrictByAncestorsAndDescendants = Utilities.getBit(slotAccessMethod, ProcessConstants.SAM_GROUP_ANCESTORS);
        } else {
            if (Utilities.getRestrictByOrig(req) || club.equals("tpcboston") || club.equals("alisoviejo") || club.equals("bluebellcc") || club.equals("hiwan") || club.equals("trophyclubcc")
                    || club.equals("pmarshgc") || club.equals("governorsclub") || club.equals("belfair") || club.equals("wildcatruncc") || club.equals("yellowstonecc")
                    || club.equals("dovecanyonclub") || club.equals("indianridgecc") || club.contains("deserthighlands") || club.contains("claremontcc")
                    || club.equals("bayclubmatt") || club.equals("olyclub") || club.equals("desertmountain") || club.equals("brookridgegf") || club.equals("ballenisles")
                    || club.equals("marbellacc") || club.equals("shadycanyongolfclub") || club.equals("plantationgcc") || club.equals("bayhill") || club.equals("elgincc")
                    || club.equals("castlepines")) {

                restrictByOrig = true;
            }
            lockOpenPositions = Utilities.getBit(slotAccessMethod, ProcessConstants.SAM_PLAYER_NO_ADD);
            alwayslAllowSlotOrig = Utilities.getBit(slotAccessMethod, ProcessConstants.SAM_ALLOW_ORIG) && isOrigBy;
            restrictByDescendants = Utilities.getBit(slotAccessMethod, ProcessConstants.SAM_PLAYER_DESCENDANTS);
            restrictByAncestorsAndDescendants = Utilities.getBit(slotAccessMethod, ProcessConstants.SAM_PLAYER_ANCESTORS);
        }

        
        if(restrictToCurrentPlayers && !partOfReservation && !hasUser(user) && !hasOrig(user) && !isOrigBy && !isGroupOwner){
            // Time is part of a group, only allow those already in group to access
            allow_edit = false;
            if(timeIsPartOfGroup){
                block_reason = "<p>Sorry, this time is part of a group.  Access is restricted to players that are currently part of the reservation.</p><p>Please contact the Golf Shop with any question.</p>";
            } else {
                block_reason = "<p>Sorry, this time is restricted to players currently in the reservation.</p><p>Please contact the Golf Shop with any question.</p>";
            }
            
            return; // no reason to stick around.
        }
        
        /*
        if(lockOpenPositions && !hasPlayer(name) && !hasUser(name) && !isOrigBy && !isGroupOwner){
            // If we're not already part of the time, and open positions are locked, don't allow access.
            allow_edit = false;
            block_reason = "<p>Sorry, You must already be part of this time in order to access it.</p><p>Please contact the Golf Shop with any question.</p>";
            return; // no reason to stick around.
        }
         * 
         */
        
        if(lockOpenPositions){
            for (int i2 = 0; i2 < lock_player.length; i2++) {
                if(playerA[i2].isEmpty()){
                    lock_player[i2] = true; // Block editing
                    force_lock[i2] = true; // Do not unlock later, for any reason.
                }
            }
        }
        
        //
        //  Custom to allow members in a request to only remove themselves, any members they added to the time, or any of their guests.
        //    *****add club name at top of doPost where restrictByOrig is set to opt in to this custom.*****
        //
        if ((restrictByOrig || restrictByDescendants || restrictByAncestorsAndDescendants) && !new_request && !alwayslAllowSlotOrig) {

            //allow_cancel = true;

            for(int i2 = 0; i2 < playerA.length; i2++){
                if( 
                        !(
                            (usergA[i2].equalsIgnoreCase(user) && userA[i2].isEmpty()) 
                            || origA[i2].equalsIgnoreCase(user)
                            || userA[i2].equalsIgnoreCase(user)
                            || playerA[i2].equalsIgnoreCase(real_name)
                            || playerA[i2].isEmpty()
//                            || playerA[i2].equalsIgnoreCase("x")
                            || isOpenPlayer(i2)
                        )
                        //(!origA[i2].isEmpty() && !playerA[i2].isEmpty() && !playerA[i2].equalsIgnoreCase(name) ) 
                        //||  
                        //(!origA[i2].isEmpty() && !playerA[i2].isEmpty() && !playerA[i2].equalsIgnoreCase("x")
                        //&& !origA[i2].equalsIgnoreCase(user) && !usergA[i2].equalsIgnoreCase(user))
                        ){
                    if(
                            !(timeIsPartOfGroup && isGroupOwner) // don't block access if we're the group owner
                            &&
                            !(restrictByDescendants && isDescendantOrOrigOf(i2, user)) // don't block access if we allow edit of descendants, and this is a descendant
                            &&
                            !(restrictByAncestorsAndDescendants && ((accessibleUsers.contains(origA[i2].toLowerCase())  ||  accessibleUsers.contains(usergA[i2].toLowerCase()))  // don't block access if we allow edit of descendants or ancestors, and this is a descendant or ancestors
                               || accessibleUsers.contains(userA[i2].toLowerCase())))
                            ){
                        lock_player[i2] = true;
                        if(timeIsPartOfGroup){
                            force_lock[i2] = true; // Do not unlock later, for any reason.
                        }
                    }
                }
                //if(!playerA[i2].isEmpty() && !origA[i2].equalsIgnoreCase(user) && !userA[i2].equalsIgnoreCase(user)){
                //    allow_cancel = false;
                //}
            }

        }
        
        if(blockCancel){
            allow_cancel = false;
        }
        
        
        for(int i2 = 0;(i2 < visible_players) && allow_edit == false; i2++){
            if(isOpenPlayer(i2)){
                allow_edit = true;
                break;
            }
        }

        if(allow_edit || orig_by.equalsIgnoreCase(user) 
                || hasEmpty(visible_players) 
                || hasUser(user)
                || hasOrig(user)){
            allow_edit = true;
        } //else if(!hasEmpty(visible_players)) {
         //   block_reason = "<p>Sorry, this reservation is full.</p><p>Please contact the Golf Shop with any questions.</p>";
        //}
        
        

            //
            //  Los Coyotes
            //
            if (club.equals("loscoyotes") && new_request == false) {

                for(int i2 = 0; i2 < playerA.length; i2++){
                    if(!playerA[i2].equalsIgnoreCase(real_name)){
                        lock_player[i2] = true;
                    }
                }
            }                              // end of IF Los Coyotes

            if ((club.startsWith("gaa") && !club.endsWith("class")) || club.equals("pgmunl")) {

                
                
                
                /*
                String[] tempPlayerA = tempParms.getStringArrayByName("player%", 5);
                
                for(int i2 = 0; i2 < tempPlayerA.length; i2++){
                    if(!tempPlayerA[i2].equalsIgnoreCase(playerA[i2]) && (i2 > 0 || new_request == false)){
                        lock_player[i2] = true;
                    } else {
                        playerA[i2] = name;
                    }
                }

                if (slotParms.isOnlyUser(user)) {
                    allowCancel = true;         // OK to cancel this tee time if only one member remaining
                } else {
                    allowCancel = false;        // NOT ok to cancel this tee time
                }
                 * 
                 */
                if (isOnlyUser(user) && !new_request && !blockCancel) {
                    allow_cancel = true;         // OK to cancel this tee time if only one member remaining
                } else {
                    allow_cancel = false;        // NOT ok to cancel this tee time
                }
            }

            if(checkHawksLandingBlockCancel(req)){
                allow_cancel = false;
            }

            //
            //  Brae Burn CC - only originator can remove players and cancel the tee time.
            //
            if (club.equals("braeburncc") && !new_request && !user.equalsIgnoreCase(orig_by)) {      // if not a new request and not origintor
                Arrays.fill(lock_player, true); // do not allow user to change/erase this one
                allow_cancel = false;        // NOT ok to cancel this tee time
            }


            //
            //  Custom - only Originator can remove players other than themselves or their guests
            //
            if ((club.equals("brookhavenclub") || club.equals("gleneaglesclub") || club.equals("pinehurstcountryclub") || club.equals("oregongolfclub") || club.equals("clcountryclub"))
                    && !new_request && !user.equalsIgnoreCase(orig_by)) {      // if not origintor (if not owner of tee time)
                
                for(int i2 = 0; i2 < playerA.length; i2++){ 
                    // if player is NOT this member or this member's guest
                    if(!playerA[i2].equalsIgnoreCase(real_name) && !usergA[i2].equalsIgnoreCase(user)){
                        lock_player[i2] = true;
                    }
                }

                if (isOnlyUser(user) && !blockCancel) {
                    allow_cancel = true;         // OK to cancel this tee time if only one member remaining
                } else {
                    allow_cancel = false;        // NOT ok to cancel this tee time
                }
            }

            


            //
            //  Custom for Timarron - only Originator can remove players other than themselves.
            //                        All members in the request can only remove themselves (and their guests).
            //
            if (club.equals("timarroncc") && !new_request) {

                for(int i2 = 0; i2 < playerA.length; i2++){
                    if( 
                            (custom_dispA[i2].isEmpty() && !playerA[i2].isEmpty() && !playerA[i2].equalsIgnoreCase(real_name) ) 
                            ||  
                            (!custom_dispA[i2].isEmpty() && 
                              (!playerA[i2].equalsIgnoreCase(real_name) || !playerA[i2].equalsIgnoreCase("x"))
                            && !custom_dispA[i2].equalsIgnoreCase(user)
                            && !origA[i2].equalsIgnoreCase(user) && !usergA[i2].equalsIgnoreCase(user))
                            ){
                        lock_player[i2] = true;
                    }
                }

                if (isOnlyUser(user) && !blockCancel) {
                    allow_cancel = true;         // OK to cancel this tee time if only one member remaining
                } else {
                    allow_cancel = false;        // NOT ok to cancel this tee time
                }

            }      // end of timarron custom


            //
            //  St Clair CC - Terrace course is always 9 holes
            //
            if ((club.equals("stclaircc") && course.equals("Terrace"))
                    || (club.equals("huntingdonvalleycc") && course.equals("Centennial Nine"))
                    || (club.equals("philcricket") && course.equals("St Martins"))
                    || (club.equals("spurwingcc") && course.equals("Challenge Course"))) {
                
                this.fillParameterByName("p9%", 1, 5);
            }

                //
                //  Do not allow user to cancel the tee time if not already in it
                //
                if (!user.equalsIgnoreCase(orig_by) && !hasUser(user)
                        && (!restrictByOrig || !hasOrig(user))) {
                    allow_cancel = false;
                }

                if (verifyCustom.checkOakGuests(date, time, fb, course, req)) {      // if Oakmont guests
                    allow_cancel = false;                                   // DO NOT allow
                }

                if (club.equals("inverness") && hasPlayerStartingWith("Hotel")) {

                    allow_cancel = false;                  // If Inverness and Hotel Guests, DO NOT allow
                }

                if (club.equals("loscoyotes") || club.equals("pganj")) {    // Don't allow members to cancel tee times.

                    allow_cancel = false;
                }

                if (club.startsWith("tpc") && index.equals("0")) {      // if any TPC Club and today

                    allow_cancel = false;                                   // DO NOT allow on the day of
                }

                /*
                if (club.equals( "turnerhill" ) && index.equals( "0" )) {      // if any Turner Hill and today
                
                allowCancel = false;                                   // DO NOT allow on the day of
                }
                 */

                if (club.equals("lagcc") && index.equals("0") && ((day.equals("Tuesday") && time >= 800 && time <= 930) || (day.equals("Thursday") && time >= 730 && time <= 930))) {  // If Los Altos, Thursday, day of, and is one of the tee times between 7:30am and 9:30am

                    allow_cancel = false;       // DO NOT allow cancellations
                }
                              
                if (club.equals("philcricketrecip") && (ind < 2 || (ind == 2 && thisTimeAdjusted >= 1200))) {
                    
                    allow_cancel = false;
                }
                
                if (club.equalsIgnoreCase("carolinacc") && index.equals("0") && (thisTimeAdjusted >= (time - 400))) {  // don't allow members to cancel tee times an hour before
                    
                    allow_cancel = false;
                }
                
                if(new_request && club_parm.max_originations > 0 && !orig_by.equalsIgnoreCase(user) && !hasUser(user)
                        && verifySlot.checkMaxOrigBy(user, date, club_parm.max_originations, con)){
                    
                }
                
                if (club.equals("merion")) {

                    //
                    //  Get adjusted date/time 48 hours out from now
                    //
                    /*
                    Calendar cal2 = new GregorianCalendar();                       // get todays date
                    cal2.add(Calendar.HOUR_OF_DAY, 48);                            // roll ahead 48 hours
                    int okDate = cal2.get(Calendar.YEAR) * 10000;                  // create a date field of yyyymmdd
                    okDate = okDate + ((cal2.get(Calendar.MONTH) + 1) * 100);
                    okDate = okDate + cal2.get(Calendar.DAY_OF_MONTH);             // date = yyyymmdd (for comparisons)

                    int okTime = (cal2.get(Calendar.HOUR_OF_DAY) * 100) + cal2.get(Calendar.MINUTE);
                    okTime = SystemUtils.adjustTime(con, okTime);                  // adjust the time

                    if ((okDate > slotParms.date) || (okDate == slotParms.date && okTime > slotParms.time)) {

                        disallowCancel = true;
                    }
                     * 
                     */
                    int[] okDateTime = timeUtil.getClubDateTime(req, timeUtil.addClubUnixTime(req, Calendar.HOUR_OF_DAY, 48));
                
                    if ((okDateTime[timeUtil.DATE] > date) || (okDateTime[timeUtil.DATE] == date && okDateTime[timeUtil.TIME] > time)) {
                        allow_cancel = false;
                    }

                }
                
                // If Naperville CC and time was booked as an advance guest time, do not allow members to cancel within the normal booking window (prevent them from gaming the system)
                if (club.equals("napervillecc") && custom_int > 7 && ind <= 7) {
                    allow_cancel = false;
                }

            /*
            // Looks like this should be moved back to Member_slot
            if (club.equals("pganj") && new_request) {
                this.fillParameterByName("player%", "Guest", 1, 5); // fill positions 2 to 5 (start is zero based, end is not)
                this.fillParameterByName("p%cw", "CRT", 1, 5); // fill positions 2 to 5 (start is zero based, end is not)
                
            }
                 * 
                 */
            
            
            // If one of the Golf Academy of America sites (non-classroom), do not display the member/partner/guest/x selection lists. Players can only add themselves.
            if ((club.startsWith("gaa") && !club.endsWith("class")) || club.equals("pgmunl")) {

                this.member_select = false;
                this.guest_select = false;
                this.member_tbd = false;
                
            }
            
            //
            //  Check if Guest Type table should be displayed
            //
            if (club.equals("newcanaan") && mship.equals("Special")) {

                this.guest_select = false;
            }
            
            
            //
            // If Greenwich and specific date/time then don't allow members to use X
            //
            if (club.equals("greenwich")) {
                if ((day.equals("Saturday") || day.equals("Sunday")
                        || date == ProcessConstants.memDay || date == ProcessConstants.july4b || date == ProcessConstants.laborDay || date == ProcessConstants.colDayObsrvd)
                        && (time > 1131 && time < 1229)) {

                    this.member_tbd = false;
                }
            }

            if (club.equals("foresthighlands")) {
                this.member_tbd = false;
            }
            
            //
            // Custom for Greenwich - don't allow Guests in twosomes if more than 48 hrs from tee time (Case #1217)
            //
            if (club.equals("greenwich") && visible_players == 2) {

                //
                //  Get adjusted date/time 48 hours out from now
                //
                
                
                /*
                Calendar cal2 = new GregorianCalendar();                       // get todays date
                cal2.add(Calendar.HOUR_OF_DAY, 48);                            // roll ahead 48 hours
                int okDate = cal2.get(Calendar.YEAR) * 10000;                  // create a date field of yyyymmdd
                okDate = okDate + ((cal2.get(Calendar.MONTH) + 1) * 100);
                okDate = okDate + cal2.get(Calendar.DAY_OF_MONTH);             // date = yyyymmdd (for comparisons)

                int okTime = (cal2.get(Calendar.HOUR_OF_DAY) * 100) + cal2.get(Calendar.MINUTE);
                okTime = SystemUtils.adjustTime(con, okTime);                  // adjust the time
*/
                int[] okDateTime = timeUtil.getClubDateTime(req, timeUtil.addClubUnixTime(req, Calendar.HOUR_OF_DAY, 48));
                
                if ((okDateTime[timeUtil.DATE] > date) || (okDateTime[timeUtil.DATE] == date && okDateTime[timeUtil.TIME] > time)) {
                    this.guest_select = false;
                }
            } // end greenwich custom

        if (club.equals("philcricket") && !course.equals("Militia Hill")) {
            this.member_tbd = false;
        }
        
        //
        //  Interlachen - use custom_disp fileds for the Gift Pack option
        //
        // if (club.equals("interlachen") || club.equals("oaklandhills")) {
        if (club.equals("oaklandhills")) {
            use_gift_pack = true;

        }
  

        // Unlock open positions
        // NOTE: This seems to undo some locks from above.  Could this be an issue?
        //
        for (int i2 = 0; i2 < lock_player.length; i2++) {
            if ((!(
                    !playerA[i2].isEmpty() 
                    || club.equals("pgmunl") 
                    || (club.startsWith("gaa") && !club.endsWith("class")))
                    ) && lock_player[i2] && !force_lock[i2]) {
            lock_player[i2] = false; // Allow editing if player is empty, and custom does not match club
            }
        }
        
        // If there are locked players in this tee time,
        // don't allow cancel
        if(ArrayUtil.contains(lock_player,true)){
            allow_cancel = false;
        }
        
        // Force blockers for visible player mis-matching in multiple groups
        // Thus MUST be the last check
        for(int i = 0; i < 5; i++){
            if(i >= visible_players){
                lock_player[i] = true;
            }
        }
        // *** DO NOT MODIFY lock_player FROM THIS POINT ON! ***
        
        
    }
    
    public Map<String, reservationPlayer> getPlayerLookupMaps(){
        
        Map<String, reservationPlayer> fullMap = new HashMap<String, reservationPlayer>();
        for(parmSlot groupSlot : group_slots){
            fullMap.putAll(groupSlot.getPlayerLookupMap());
        }
        if(fullMap.isEmpty()){
            return playerLookupMap;
        }
        return fullMap;
        
    }
    
    public Map<String, reservationPlayer> getPlayerLookupMap(){
        
        buildLookupMaps(false);
        return playerLookupMap;

    }
    
    public Map<String, reservationPlayer> getUserLookupMaps(){
        
        Map<String, reservationPlayer> fullMap = new HashMap<String, reservationPlayer>();
        for(parmSlot groupSlot : group_slots){
            fullMap.putAll(groupSlot.getUserLookupMap());
        }
        if(fullMap.isEmpty()){
            return userLookupMap;
        }
        return fullMap;
        
    }
    
    public Map<String, reservationPlayer> getUserLookupMap(){
        
        buildLookupMaps(false);
        return userLookupMap;

    }
    
    public Map<String, List<reservationPlayer>> getCwLookupMaps(){
        
        Map<String, List<reservationPlayer>> fullMap = new HashMap<String, List<reservationPlayer>>();
        for(parmSlot groupSlot : group_slots){
            Map<String, List<reservationPlayer>> thisMap = groupSlot.getCwLookupMap();
            for(String key : thisMap.keySet()){
                List<reservationPlayer> cwtl = fullMap.get(key);
                if(cwtl == null){
                    fullMap.put(key, thisMap.get(key));
                } else {
                    cwtl.addAll(thisMap.get(key));
                }
            }
        }
        if(fullMap.isEmpty()){
            return cwLookupMap;
        }
        return fullMap;
        
    }
    
    public Map<String, List<reservationPlayer>> getCwLookupMap(){
        
        buildLookupMaps(false);
        return cwLookupMap;

    }
    
    public List<reservationPlayer> getPlayerList(){
        
        buildLookupMaps(false);
        return playerList;

    }
    
    public void buildLookupMaps(boolean force){
        if(playerLookupMap == null || userLookupMap == null || cwLookupMap == null ||  force || playerList == null){
            playerLookupMap = new HashMap<String, reservationPlayer>();
            userLookupMap = new HashMap<String, reservationPlayer>();
            cwLookupMap = new HashMap<String, List<reservationPlayer>>();
            playerList = new ArrayList<reservationPlayer>();
            String[] pl = this.getPlayerArray(5);
            String[] ul = this.getUserArray(5);
            String[] cwl = this.getCwArray(5);
            String[] ol = this.getOrigArray(5);
            int[] gidl = this.getGuestIdArray(5);
            for(int i = 0; i < pl.length; i++){
                String p = pl[i];
                String u = ul[i];
                String cw = cwl[i];
                reservationPlayer rp = new reservationPlayer(u, p, null, null, cwl[i], gidl[i], group_index, i, ol[i]);
                playerList.add(rp);
                playerLookupMap.put(p.toLowerCase(), rp);
                if(!u.isEmpty()){
                    userLookupMap.put(u.toLowerCase(), rp);
                }
                if(!cw.isEmpty()){
                    String cwlow = cw.toLowerCase();
                    List<reservationPlayer> cwtl = cwLookupMap.get(cwlow);
                    if(cwtl == null){
                        cwtl = new ArrayList<reservationPlayer>();
                        cwLookupMap.put(cwlow, cwtl);
                    }
                    cwtl.add(rp);
                }
            }
        }
    }
    
    public Map<Integer, Integer> getModified(){
        checkModifications(false);
        return modified;
    }
    
    public Map<Integer, Integer> getModifiedCw(){
        checkModifications(false);
        return cwmodified;
    }
    
    public Map<Integer, Integer> getModifiedP9(){
        checkModifications(false);
        return p9modified;
    }
    
    public Map<Integer, Integer> getModifiedGuestUsers(){
        checkModifications(false);
        return gmodified;
    }
    public Map<Integer, Integer> getModifiedOnlyGuestUsers(){
        checkModifications(false);
        return onlygmodified;
    }

    public Map<Integer, Integer> getModifiedPositions(){
        checkModifications(false);
        return positionChange;
    }
    
    public Map<Integer, Integer> getUnModified(){
        checkModifications(false);
        return unmodified;
    }
    
    public Map<Integer, Integer> getUnModifiedNames(){
        checkModifications(false);
        return nameunmodified;
    }
    
    public Set<Integer> getEmpty(){
        checkModifications(false);
        return empty;
    }
    
    public Set<Integer> getRemoved(){
        checkModifications(false);
        return removed;
    }
    
    public Set<Integer> getNew(){
        checkModifications(false);
        return newPlayers;
    }
    
    public Map<Integer, Integer> getOnlyPositionChanged(){
        checkModifications(false);
        return onlyPositionChange;
    }
    
    public Integer[] findMovedFromGroup(int index) {

        // Search removed players in the other tee times in our group to see if it matched the requested player
        
        String searchPlayer = this.getPlayer(index).trim();
        String searchUser = this.getUser(index);
        String searchCw = this.getCw(index);
        int searchP9 = this.getP9(index);
        int searchGuest_id = this.getGuestId(index);

        Integer lastMatchGroup = null;
        Integer lastMatchIndex = null;
        int lastMatchScore = -1;
        
        int score;
        
        for(parmSlot parm : this.group_slots){
            if(parm.group_index != this.group_index){ // Skip our own tee time
                for(Integer i : parm.getRemoved()){ // We'll only search ones that have been removed from the tee time.
                    if(parm.moved_to_group[i] == null 
                            && searchPlayer.equalsIgnoreCase(parm.getOldPlayer(i).trim()) 
                            && searchUser.equals(parm.getOldUser(i))){
                        // Could be the same user?
                        if(!searchUser.isEmpty() || (searchGuest_id == parm.getOldGuestId(i) && searchGuest_id > 0)){
                            // Found a user or guest DB match.  Take it.
                            parm.moved_to_group[i] = this.group_index;
                            return new Integer[]{parm.group_index, i};
                        } else {    
                            // We're looking for a guest -- search harder.  Guests can have the same name.
                            score = 0;
                            if(searchCw.equals(parm.getOldCw(i))){
                                score += 2;
                            }
                            if(searchP9 == parm.getOldP9(i)){
                                score += 2;
                            }
                            if(parm.oldOrig[i].equalsIgnoreCase(this.user)){
                                // If we match the current user's orig -- increase the score a little (maybe this should increase the score even more?).
                                score ++;
                            }
                            if(score > lastMatchScore){
                                lastMatchGroup = parm.group_index;
                                lastMatchIndex = i;
                            }
                        }
                    }
                }
            }
        }
        if(lastMatchScore >= 0){
            // Take our best result
            this.group_slots.get(lastMatchGroup).moved_to_group[lastMatchIndex] = this.group_index;
            return new Integer[]{lastMatchGroup, lastMatchIndex};
        } else {
            // No result
            return null;
        }
        
        
    }
    /*
     * Scan, and record in lists/maps, modifications to slot
     * 
     * Compares old to new (oldPayerX to playerX, etc.), detects moved players, etc.
     * 
     */
    public void checkModifications(boolean force) {

        if (modified == null || onlyPositionChange == null || positionChange == null || newPlayers == null 
                || cwmodified == null || p9modified == null
                || unmodified == null || onlygmodified == null || gmodified == null || removed == null || force) {


            modified = new HashMap<Integer, Integer>();
            positionChange = new HashMap<Integer, Integer>();
            onlyPositionChange = new HashMap<Integer, Integer>();
            newPlayers = new HashSet<Integer>();
            cwmodified = new HashMap<Integer, Integer>();
            p9modified = new HashMap<Integer, Integer>();
            unmodified = new HashMap<Integer, Integer>();
            nameunmodified = new HashMap<Integer, Integer>();
            gmodified = new HashMap<Integer, Integer>();
            onlygmodified = new HashMap<Integer, Integer>();
            removed = new HashSet<Integer>();
            empty = new HashSet<Integer>();

            Set<Integer> processed = new HashSet<Integer>();

            String[] playerL = this.getPlayerArray(5);
            String[] userL = this.getUserArray(5);
            String[] usergL = this.getUsergArray(5);
            String[] gL = this.getGArray(5);
            String[] cwL = this.getCwArray(5);
            
            int[] p9L = this.getP9Array(5);
            int[] guest_idL = this.getGuestIdArray(5);

            String[]  oldPlayerL = this.getOldPlayerArray(5);
            //String[]  oldUserL = this.getOldUserArray(5);
            String[]  oldCwL = this.getOldCwArray(5);
            
            int[] oldP9L = this.getOldP9Array(5);
            int[] oldGuest_idL = this.getOldGuestIdArray(5);
            
            String playerTest;
            
            /*
            Gson gson = new Gson();
            
            Connect.logError("Debug parmSlot - playerL: " + gson.toJson(Arrays.asList(playerL)));
            Connect.logError("Debug parmSlot - oldPlayerL: " + gson.toJson(Arrays.asList(oldPlayerL)));
            Connect.logError("Debug parmSlot - usergL: " + gson.toJson(Arrays.asList(usergL)));
            Connect.logError("Debug parmSlot - oldUsergL: " + gson.toJson(Arrays.asList(oldUserg)));
            Connect.logError("Debug parmSlot - userL: " + gson.toJson(Arrays.asList(userL)));
            */
            
            // The following MUST be done in order! 
            //   (There is probably a more elegant way to do this. For now, this seems to catch most edge cases.  
            //    Guests being able to have the same name can complicate deferentiating a modified guest from a moved guest.)

            // First get all member/guest db (not non-db guests) players that may have been modified, 
            //  but the position has not (name is the same, and has username or guest id)
            for (int i = 0; i < playerL.length; i++) {
                playerTest = playerL[i].trim();
                if (playerTest.isEmpty()) {
                    empty.add(i);
                } else {
                    if (!processed.contains(i)
                            && !playerTest.isEmpty()
                            && playerTest.equalsIgnoreCase(oldPlayerL[i].trim())
                            && (guest_idL[i] > 0 || !userL[i].trim().isEmpty() || !usergL[i].trim().isEmpty())) {
                        if (cwL[i].equalsIgnoreCase(oldCwL[i])
                                && p9L[i] == oldP9L[i]
                                && guest_idL[i] == oldGuest_idL[i]) {
                            // Unmodified
                            unmodified.put(i, i);
                        } else if ((guest_idL[i] == oldGuest_idL[i] && guest_idL[i] > 0)) {
                            // Modified in some way
                            modified.put(i, i);
                            if (!cwL[i].equalsIgnoreCase(oldCwL[i])) {
                                // cw modified
                                cwmodified.put(i, i);
                            }
                            if (p9L[i] != oldP9L[i]) {
                                // p9 modified
                                p9modified.put(i, i);
                            }
                        }
                        processed.add(i);
                        nameunmodified.put(i, i);
                        if (oldUserg[i] != null && gstA[i] != null && !usergL[i].equalsIgnoreCase(oldUserg[i]) && (!gL[i].isEmpty() || !gstA[i].isEmpty())) {
                            // This appears to be a guest, and the owner of this guest appears to have changed
                            onlygmodified.put(i, i);
                            gmodified.put(i, i);
                        }
                    } else {
                        /*
                        Connect.logError("Debug parmSlot - no match["+i+"]: " + playerTest);
                        if(!playerTest.equalsIgnoreCase(oldPlayerL[i].trim())){
                            Connect.logError("Debug parmSlot - no match["+i+"]: Name Mismatch");
                        }
                        if(!playerTest.equalsIgnoreCase(oldPlayerL[i].trim())){
                            Connect.logError("Debug parmSlot - no match["+i+"]: Name Mismatch");
                        } 
                         */
                    }
                }
            }
            
            // Then get all players that have not been modified, but position may have changed
            for (int i = 0; i < playerL.length; i++) {
                playerTest = playerL[i].trim();
                for (int i2 = 0; i2 < oldPlayerL.length; i2++) {
                    if (!processed.contains(i2)
                            && !playerTest.isEmpty()
                            && playerTest.equalsIgnoreCase(oldPlayerL[i2].trim())
                            //&& userL[i].equalsIgnoreCase(oldUserL[i2])
                            && cwL[i].equalsIgnoreCase(oldCwL[i2])
                            && p9L[i] == oldP9L[i2]
                            && guest_idL[i] == oldGuest_idL[i2]
                            && !(oldUserg[i2] != null && gstA[i] != null && !usergL[i].equalsIgnoreCase(oldUserg[i2]) && (!gL[i].isEmpty() || !gstA[i].isEmpty()))) {
                        // Unmodified
                        processed.add(i2);
                        unmodified.put(i, i2);
                        nameunmodified.put(i, i2);
                        if (i != i2) {
                            positionChange.put(i, i2); // new position, old position. 
                            onlyPositionChange.put(i, i2); // new position, old position. 
                        }
                    }
                }
            }

            // Then get all players that have not been modified, but position or userg may have changed
            for (int i = 0; i < playerL.length; i++) {
                playerTest = playerL[i].trim();
                for (int i2 = 0; i2 < oldPlayerL.length; i2++) {
                    if (!processed.contains(i2)
                            && !playerTest.isEmpty()
                            && playerTest.equalsIgnoreCase(oldPlayerL[i2].trim())
                            //&& userL[i].equalsIgnoreCase(oldUserL[i2])
                            && cwL[i].equalsIgnoreCase(oldCwL[i2])
                            && p9L[i] == oldP9L[i2]
                            && guest_idL[i] == oldGuest_idL[i2]) {
                        // Unmodified
                        processed.add(i2);
                        unmodified.put(i, i2);
                        nameunmodified.put(i, i2);
                        if (i != i2) {
                            positionChange.put(i, i2); // new position, old position. 
                            onlyPositionChange.put(i, i2); // new position, old position. 
                        }
                        if (oldUserg[i2] != null && gstA[i] != null && !usergL[i].equalsIgnoreCase(oldUserg[i2]) && (!gL[i].isEmpty() || !gstA[i].isEmpty())) {
                            // This appears to be a guest, and the owner of this guest appears to have changed
                            onlygmodified.put(i, i2);
                            gmodified.put(i, i2);
                        }
                    }
                }
            }
            
            // Next check all players that have had ony CW modified
            for (int i = 0; i < playerL.length; i++) {
                if (!nameunmodified.containsKey(i)) {
                    playerTest = playerL[i].trim();
                    for (int i2 = 0; i2 < oldPlayerL.length; i2++) {
                        
                        if (!processed.contains(i2)
                                && !playerTest.isEmpty()
                                && playerTest.equalsIgnoreCase(oldPlayerL[i2].trim())
                                //&& userL[i].equalsIgnoreCase(oldUserL[i2])
                                && !cwL[i].equalsIgnoreCase(oldCwL[i2])
                                && p9L[i] == oldP9L[i2]
                                && guest_idL[i] == oldGuest_idL[i2]) {
                            // cw changed
                            processed.add(i2);
                            cwmodified.put(i, i2);
                            modified.put(i, i2);
                            nameunmodified.put(i, i2);
                            if (i != i2) {
                                positionChange.put(i, i2); // new position, old position. 
                            }
                            if (oldUserg[i2] != null && gstA[i] != null && !usergL[i].equalsIgnoreCase(oldUserg[i2]) && (!gL[i].isEmpty() || !gstA[i].isEmpty())) {
                                // This appears to be a guest, and the owner of this guest appears to have changed
                                gmodified.put(i, i2);
                            }
                        }
                    }
                }
            }
            
            // Next check all players that have had only p9 modified
            for (int i = 0; i < playerL.length; i++) {
                if (!nameunmodified.containsKey(i) && !modified.containsKey(i)) {
                    playerTest = playerL[i].trim();
                    for (int i2 = 0; i2 < oldPlayerL.length; i2++) {
                        if (!processed.contains(i2)
                                && !playerTest.isEmpty()
                                && playerTest.equalsIgnoreCase(oldPlayerL[i2].trim())
                                //&& userL[i].equalsIgnoreCase(oldUserL[i2])
                                && cwL[i].equalsIgnoreCase(oldCwL[i2])
                                && p9L[i] != oldP9L[i2]
                                && guest_idL[i] == oldGuest_idL[i2]) {
                            // p9 changed
                            processed.add(i2);
                            p9modified.put(i, i2);
                            modified.put(i, i2);
                            nameunmodified.put(i, i2);
                            if (i != i2) {
                                positionChange.put(i, i2); // new position, old position. 
                            }
                            if (oldUserg[i2] != null && gstA[i] != null && !usergL[i].equalsIgnoreCase(oldUserg[i2]) && (!gL[i].isEmpty() || !gstA[i].isEmpty())) {
                                // This appears to be a guest, and the owner of this guest appears to have changed
                                gmodified.put(i, i2);
                            }
                        }
                    }
                }
            }
            
            // Next check all players that have had p9 and CW modified
            for (int i = 0; i < playerL.length; i++) {
                if (!nameunmodified.containsKey(i) && !modified.containsKey(i)) {
                    playerTest = playerL[i].trim();
                    for (int i2 = 0; i2 < oldPlayerL.length; i2++) {
                        if (!processed.contains(i2)
                                && !playerTest.isEmpty()
                                && playerTest.equalsIgnoreCase(oldPlayerL[i2].trim())
                                //&& userL[i].equalsIgnoreCase(oldUserL[i2])
                                && !cwL[i].equalsIgnoreCase(oldCwL[i2])
                                && p9L[i] != oldP9L[i2]
                                && guest_idL[i] == oldGuest_idL[i2]) {
                            // cw and p9 changed
                            processed.add(i2);
                            p9modified.put(i, i2);
                            cwmodified.put(i, i2);
                            modified.put(i, i2);
                            nameunmodified.put(i, i2);
                            if (i != i2) {
                                positionChange.put(i, i2); // new position, old position. 
                            }
                            if (oldUserg[i2] != null && gstA[i] != null && !usergL[i].equalsIgnoreCase(oldUserg[i2]) && (!gL[i].isEmpty() || !gstA[i].isEmpty())) {
                                // This appears to be a guest, and the owner of this guest appears to have changed
                                gmodified.put(i, i2);
                            }
                        }
                    }
                }
            }
            
            // Next check all players that have had only thier guest owner (userg) modified
            for (int i = 0; i < playerL.length; i++) {
                if (!nameunmodified.containsKey(i) && !modified.containsKey(i)) {
                    playerTest = playerL[i].trim();
                    for (int i2 = 0; i2 < oldPlayerL.length; i2++) {
                        if (!processed.contains(i2)
                                && !playerTest.isEmpty()
                                && playerTest.equalsIgnoreCase(oldPlayerL[i2].trim())
                                //&& userL[i].equalsIgnoreCase(oldUserL[i2])
                                && cwL[i].equalsIgnoreCase(oldCwL[i2])
                                && p9L[i] == oldP9L[i2]
                                && (oldUserg[i2] != null && gstA[i] != null && !usergL[i].equalsIgnoreCase(oldUserg[i2]) && (!gL[i].isEmpty() || !gstA[i].isEmpty()))
                                ) {
                            // This appears to be a guest, and the owner of this guest appears to have changed
                            onlygmodified.put(i, i2);
                            if (i != i2) {
                                positionChange.put(i, i2); // new position, old position. 
                            }
                        }
                    }
                }
            }

            // Anything remaining should be new players
            for (int i = 0; i < playerL.length; i++) {
                if (!nameunmodified.containsKey(i) && !modified.containsKey(i) && !playerL[i].trim().isEmpty()) {
                    playerTest = playerL[i].trim();
                    // New player
                    newPlayers.add(i);
                }
            }

            // Now look for removed players in unprocessed old players
            for (int i2 = 0; i2 < oldPlayerL.length; i2++) {
                if (!processed.contains(i2)) {
                    playerTest = oldPlayerL[i2].trim();
                    if (!playerTest.isEmpty() 
                            // && !playerTest.equalsIgnoreCase("x") // Commented out "X" exclusion, since it's possible that
                                                                    // an "X" position could be locked.
                                                                    // We'll record the removal of "X", since a user created
                                                                    // it, and we may want to check restrictions on it somwhere.
                            && !playerTest.startsWith("Join Me") // Treat "Join Me" and "Needs Player" 
                            && !playerTest.startsWith("Needs Player") // like empty positions
                            ) {
                        // Removed player
                        processed.add(i2);
                        removed.add(i2);
                    }
                }
            }
            
        }


    }
    
    
    public int countRealPlayers() {
        // This will NOT count "X" as a player
        String[] A = this.getPlayerArray(5);
        int count = 0;
        for (int i = 0; i < A.length; i++) {
            if (!A[i].isEmpty() && !A[i].equalsIgnoreCase("x")) {
                count++;
            }
        }
        return count;
    }
    
    public int countRealOldPlayers() {
        // This will NOT count "X" as a player
        String[] A = this.getOldPlayerArray(5);
        int count = 0;
        for (int i = 0; i < A.length; i++) {
            if (!A[i].isEmpty() && !A[i].equalsIgnoreCase("x")) {
                count++;
            }
        }
        return count;
    }
    
    public int countPlayers() {
        // This will count "X" as a player
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (!getPlayer(i).isEmpty()) {
                count++;
            }
        }
        return count;
    }
    
    public int countOldPlayers() {
        // This will count "X" as a player
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (!getOldPlayer(i).isEmpty()) {
                count++;
            }
        }
        return count;
    }
    
    public int countOrig(String user) {
        int count = 0;
        if (orig1.equalsIgnoreCase(user)) {
            count++;
        }
        if (orig2.equalsIgnoreCase(user)) {
            count++;
        }   
        if (orig3.equalsIgnoreCase(user)) {
            count++;
        }
        if (orig4.equalsIgnoreCase(user)) {
            count++;
        }
        if (orig5.equalsIgnoreCase(user)) {
            count++;
        }
        return count;
    }
    public boolean isOpenPlayer(int i) {
        // Is position "Open"
        String test = getPlayer(i);
        if (
                test.isEmpty()
                || club.equals("mpccpb") && fives && test.startsWith("Need A Player")
                || club.startsWith("demo") && test.startsWith("Join Me")
                ) {
            return true;
        }
        return false;
    }
    
    public boolean isPlayer(int i) {
        // This will NOT count "X" as a player
        String test = getPlayer(i);
        if (!test.isEmpty() && !test.equalsIgnoreCase("x")) {
            return true;
        }
        return false;
    }
    
    public boolean hasRealPlayers() {
        // This will NOT count "X" as players
        String[] A = this.getPlayerArray(5);
        for (int i = 0; i < A.length; i++) {
            if (!A[i].isEmpty() && !A[i].equalsIgnoreCase("x")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPlayers() {
        // This will count "X", "Join Me", etc., as players
        for (int i = 0; i < 5; i++) {
            if (!getPlayer(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasOldPlayers() {

        for (int i = 0; i < 5; i++) {
            if (!getOldPlayer(i).isEmpty()) {
                return true;
            }
        }
        return false;

    }
    
    public boolean hasPlayer(String name) {

        for (int i = 0; i < 5; i++) {
            if (getPlayer(i).equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;

    }
    
    public boolean hasOldPlayer(String name) {

        for (int i = 0; i < 5; i++) {
            if (getOldPlayer(i).equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;

    }
    
    public boolean hasOldUser(String user) {
        
        for (int i = 0; i < 5; i++) {
            if (getOldUser(i).equalsIgnoreCase(user)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasCw(String cw) {
        
        for (int i = 0; i < 5; i++) {
            if (!getCw(i).isEmpty() && getCw(i).equalsIgnoreCase(cw)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasMship(String mship) {
        
        for (int i = 0; i < 5; i++) {
            if (!getMship(i).isEmpty() && getMship(i).equalsIgnoreCase(mship)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasMship(List<String> mships) {
        
        // Returns true if at least one of the mships in the list are present in this tee time
        for (int i = 0; i < 5; i++) {
            if (!getMship(i).isEmpty() && mships.contains(getMship(i))) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isMembersOnly() {

        // Returns true if all players in time are members
        for (int i = 0; i < 5; i++) {
            if (!getPlayer(i).isEmpty() && (getPlayer(i).equalsIgnoreCase("x") || getUser(i).equals(""))) {
                return false;
            }
        }
        return true;

    }
    
    public Integer indexOfPlayer(String name) {

        for (int i = 0; i < 5; i++) {
            if (getPlayer(i).equalsIgnoreCase(name)) {
                return i;
            }
        }
        return null;
    }
    
    public boolean hasPlayerStartingWith(String name) {

        for (int i = 0; i < 5; i++) {
            if (getPlayer(i).startsWith(name)) {
                return true;
            }
        }
        return false;

    }
    
    public boolean hasEmpty(int size) {

        for (int i = 0; i < 5; i++) {
            if (getPlayer(i).isEmpty()) {
                return true;
            }
        }
        return false;

    }
    
    public Integer firstEmpty(int size) {

        for (int i = 0; i < 5; i++) {
            if (getPlayer(i).isEmpty() && !lock_player[i]) {
                return i;
            }
        }
        return null;

    }
    
    public boolean hasUser(String user) {

        for (int i = 0; i < 5; i++) {
            if (getUser(i).equalsIgnoreCase(user)) {
                return true;
            }
        }
        return false;

    }
    
    public Integer indexOfUser(String user) {

        for (int i = 0; i < 5; i++) {
            if (getUser(i).equalsIgnoreCase(user)) {
                return i;
            }
        }
        return null;
    }
    
    public boolean hasOrig(String user) {

        for (int i = 0; i < 5; i++) {
            if (getOrig(i).equalsIgnoreCase(user)) {
                return true;
            }
        }
        return false;

    }
    
    public boolean isOnlyUser(String user) {

        if(!user1.equalsIgnoreCase(user)){
            return false;
        }

        for (int i = 1; i < 5; i++) {
            if (!getUser(i).isEmpty()) {
                return false;
            }
        }
        return true;

    }
   
   /*
    * Save this slot to teecurr2
    * return ID of modified record, or 0 if failed.
    * 
    */
    public long saveSlot(HttpServletRequest req) {
        return saveSlot(req, true);
    }
    public long saveSlot(HttpServletRequest req, boolean incrementModCount) {

        //
        //  Update the tee slot in teecurr
        //
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isMod = false;
        
        Connection con = Connect.getCon(req);
        
        String current_user = reqUtil.getSessionString(req, "user", "");

        try {

            long result = 0;
            String[] origPlayers = {};

            // Get original teecurr record
            pstmt = con.prepareStatement(""
                    + "SELECT t.*, c.fives "
                    + "  FROM teecurr2 t "
                    + "     LEFT OUTER JOIN clubparm2 c ON c.courseName = t.courseName "
                    + " WHERE t.date = ? "
                    + "  AND t.time = ? "
                    + "  AND t.fb = ? "
                    + "  AND t.courseName = ? ");

            pstmt.clearParameters();
            pstmt.setLong(1, date);
            pstmt.setInt(2, time);
            pstmt.setInt(3, fb);
            pstmt.setString(4, course);

            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {
                result = rs.getLong("teecurr_id");  // get original id
                origPlayers = ArrayUtil.getStringArrayFromResultSet(rs, "player%", 5);
            }
            
            // Check if this is new or modified
            for(int i = 0; i < origPlayers.length; i++){
                if(!origPlayers[i].isEmpty()){
                    isMod = true;
                }
            }

            //
            //  Determine if Proshop or Member
            //
            int memModInc = 0;
            int proModInc = 0;
            int memNewInc = 0;
            int proNewInc = 0;
            
            if(incrementModCount){
                if (current_user.startsWith("proshop")) {
                    proNewInc = (isMod?0:1);
                    proModInc = (isMod?1:0);
                } else {
                    memNewInc = (isMod?0:1);
                    memModInc = (isMod?1:0);
                }
            }

            //boolean fives = this.p5.equals("Yes");

            rs.close();
            pstmt.close();

            pstmt = con.prepareStatement(
                    "UPDATE teecurr2 "
                    + "SET "
                    + "  last_mod_date = now(), "
                    
                    + "  player1 = ?, player2 = ?, player3 = ?, player4 = ?, player5 = ?, "
                    + "  username1 = ?, username2 = ?, username3 = ?, username4 = ?, username5 = ?, "
                    + "  p1cw = ?, p2cw = ?, p3cw = ?, p4cw = ?, p5cw = ?, "
                    + "  hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, hndcp4 = ?, hndcp5 = ?, "
                    + "  show1 = ?, show2 = ?, show3 = ?, show4 = ?, show5 = ?, "
                    + "  mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, "
                    + "  userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, "
                    + "  guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ?, "
                    + "  p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, "
                    + "  pos1 = ?, pos2 = ?, pos3 = ?, pos4 = ?, pos5 = ?, "
                    + "  custom_disp1 = ?, custom_disp2 = ?, custom_disp3 = ?, custom_disp4 = ?, custom_disp5 = ?, "
                    + "  tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, tflag5 = ?, "
                    + "  orig1 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ?, "
                    
                    + "  notes = ?, hideNotes = ?, "
                    + "  memNew = IFNULL(memNew,0) + ?, "
                    + "  memMod = IFNULL(memMod,0) + ?, "
                    + "  proNew = IFNULL(proNew,0) + ?, "
                    + "  proMod = IFNULL(proMod,0) + ?, "
                    + "  orig_by = ?, "
                    + "  custom_string = ?, "
                    + "  custom_int = ?, "
                    
                    + "  in_use = 0 "
                    
                    + "WHERE date = ? "
                    + "   AND time = ? "
                    + "   AND fb = ? "
                    + "   AND courseName = ?");

            pstmt.clearParameters();
            int i = 1;
            
            i = ArrayUtil.setArrayToPreparedStatment(pstmt, this.getPlayerArray(5), i);
            i = ArrayUtil.setArrayToPreparedStatment(pstmt, this.getUserArray(5), i);
            i = ArrayUtil.setArrayToPreparedStatment(pstmt, this.getCwArray(5), i);
            i = ArrayUtil.setArrayToPreparedStatment(pstmt, this.getHndcpArray(5), i);
            i = ArrayUtil.setArrayToPreparedStatment(pstmt, this.getShowArray(5), i);
            i = ArrayUtil.setArrayToPreparedStatment(pstmt, this.getMnumArray(5), i);
            i = ArrayUtil.setArrayToPreparedStatment(pstmt, this.getUsergArray(5), i);
            i = ArrayUtil.setArrayToPreparedStatment(pstmt, this.getGuestIdArray(5), i);
            i = ArrayUtil.setArrayToPreparedStatment(pstmt, this.getP9Array(5), i);
            i = ArrayUtil.setArrayToPreparedStatment(pstmt, this.getPosArray(5), i);
            i = ArrayUtil.setArrayToPreparedStatment(pstmt, this.getCustomDispArray(5), i);
            i = ArrayUtil.setArrayToPreparedStatment(pstmt, this.getTflagArray(5), i);
            i = ArrayUtil.setArrayToPreparedStatment(pstmt, this.getOrigArray(5), i);
 
            pstmt.setString(i++, notes);
            pstmt.setInt(i++, hide);
            pstmt.setInt(i++, memNewInc);
            pstmt.setInt(i++, memModInc);
            pstmt.setInt(i++, proNewInc);
            pstmt.setInt(i++, proModInc);
            pstmt.setString(i++, orig_by);
            pstmt.setString(i++, custom_string);
            pstmt.setInt(i++, custom_int);
            
            pstmt.setLong(i++, date);
            pstmt.setInt(i++, time);
            pstmt.setInt(i++, fb);
            pstmt.setString(i++, course);
            
            pstmt.executeUpdate();      // execute the prepared stmt

            pstmt.close();

            return result; // Should return the ID of the modified teecurr record

        } catch (Exception e) {
            // Error
            Utilities.logError("parmSlot.saveSlot: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            Connect.close(rs, pstmt);
            return 0;
        }

    }
    
    public final boolean checkHawksLandingBlockCancel(HttpServletRequest req){

        if (club.equals("hawkslandinggolfclub") && !new_request && index.equals("0")) {   // if Hawks Landing, today and not a new req

            int cur_time = timeUtil.getClubTime(req);                 // get the current adjusted time

            if (cur_time > (time - 300)) {                         // if within 3 hours of the tee time
                return true;                               // do not allow member to cancel it
            }
        }
        
        return false;
    }
    
    
    /*
     * Abstractions for legacy non-array parameters specific to parmSlot
     * 
     */
    
    public String getFname(int index){
        return getStringByName("fname%", index);
    }
    
    public String[] getFnameArray(int length){
        return getStringArrayByName("fname%", length);
    }
    
    public void setFname(String value, int index){
        setParameterByName("fname%",value,  index);
    }
    
    public void setFname(String[] array){
        setParameterByName("fname%", array);
    }
    
    public void fillFname(String value, int length) {
        fillParameterByName("fname%", value, length);
    }
    
    public String getLname(int index){
        return getStringByName("lname%", index);
    }
    
    public String[] getLnameArray(int length){
        return getStringArrayByName("lname%", length);
    }
    
    public void setLname(String value, int index){
        setParameterByName("lname%",value,  index);
    }
    
    public void setLname(String[] array){
        setParameterByName("lname%", array);
    }
    
    public void fillLname(String value, int length) {
        fillParameterByName("lname%", value, length);
    }
    
    public String getMi(int index){
        return getStringByName("mi%", index);
    }
    
    public String[] getMiArray(int length){
        return getStringArrayByName("mi%", length);
    }
    
    public void setMi(String value, int index){
        setParameterByName("mi%",value,  index);
    }
    
    public void setMi(String[] array){
        setParameterByName("mi%", array);
    }
    
    public void fillMi(String value, int length) {
        fillParameterByName("mi%", value, length);
    }
    
 
   
}  // end of class
