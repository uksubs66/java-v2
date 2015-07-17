/***************************************************************************************
 *   parmLott:  This class will define a paramter block object to be used for lottery processing.
 *
 *
 *   called by:  Proshop_lott
 *               Member_lott
 *               SystemUtils
 *
 *   created: 8/20/2003   Bob P.
 *
 *   last updated:
 *
 *            2/18/14  Add update_recur for handling updates to recurring requests
 *            8/28/13  Add recurrence values for recurring lottery requests.
 *            7/31/12  Add courseReq for lottery processing in Common_lott.
 *            4/15/10  Added guest_id1-25, oldguest_id1-25, and gplayer for use with guest tracking
 *           12/11/06  Add fb_before and fb_after for processing in SystemUtils.
 *            6/07/05  Move tee time arrays into new parm block - parmLottC (one per course).
 *
 ***************************************************************************************
 */

import com.foretees.common.parmCore;

public class parmLott extends parmCore {

   public boolean error = false;
   public boolean isRecurr = false;
     
   public long date = 0;
   public long lottid = 0;

   public int in_use = 0;
   public int time = 0;
   public int stime = 0;
   public int etime = 0;
   public int fb = 0;
   public int mm = 0;
   public int dd = 0;
   public int yy = 0;
   public int emm = 0;     // end date values for recurrence option
   public int edd = 0;
   public int eyy = 0;
   public int eoweek = 0;  // 'every other week' option
   public int ind = 0;
   public int inval1 = 0;
   public int inval2 = 0;
   public int inval3 = 0;
   public int inval4 = 0;
   public int inval5 = 0;
   public int inval6 = 0;
   public int inval7 = 0;
   public int inval8 = 0;
   public int inval9 = 0;
   public int inval10 = 0;
   public int inval11 = 0;
   public int inval12 = 0;
   public int inval13 = 0;
   public int inval14 = 0;
   public int inval15 = 0;
   public int inval16 = 0;
   public int inval17 = 0;
   public int inval18 = 0;
   public int inval19 = 0;
   public int inval20 = 0;
   public int inval21 = 0;
   public int inval22 = 0;
   public int inval23 = 0;
   public int inval24 = 0;
   public int inval25 = 0;
   public int members = 0;
   public int memg1 = 0;
   public int memg2 = 0;
   public int memg3 = 0;
   public int memg4 = 0;
   public int memg5 = 0;
   public int players = 0;
   public int hide = 0;
   public int lstate = 0;
   public int mins_before = 0;
   public int mins_after = 0;
   public int slots = 0;
   public int beforei = 0;
   public int afteri = 0;
   public int ftime = 0;
   public int ltime = 0;
   public int groups = 0;
   public int grps = 0;
   public int i3 = 0;
   public int guests = 0;
   public int guestsg1 = 0;
   public int guestsg2 = 0;
   public int guestsg3 = 0;
   public int guestsg4 = 0;
   public int guestsg5 = 0;
   public int memNew = 0;
   public int memMod = 0;
   public int p91 = 0;
   public int p92 = 0;
   public int p93 = 0;
   public int p94 = 0;
   public int p95 = 0;
   public int p96 = 0;
   public int p97 = 0;
   public int p98 = 0;
   public int p99 = 0;
   public int p910 = 0;
   public int p911 = 0;
   public int p912 = 0;
   public int p913 = 0;
   public int p914 = 0;
   public int p915 = 0;
   public int p916 = 0;
   public int p917 = 0;
   public int p918 = 0;
   public int p919 = 0;
   public int p920 = 0;
   public int p921 = 0;
   public int p922 = 0;
   public int p923 = 0;
   public int p924 = 0;
   public int p925 = 0;
   public int guest_id1 = 0;
   public int guest_id2 = 0;
   public int guest_id3 = 0;
   public int guest_id4 = 0;
   public int guest_id5 = 0;
   public int guest_id6 = 0;
   public int guest_id7 = 0;
   public int guest_id8 = 0;
   public int guest_id9 = 0;
   public int guest_id10 = 0;
   public int guest_id11 = 0;
   public int guest_id12 = 0;
   public int guest_id13 = 0;
   public int guest_id14 = 0;
   public int guest_id15 = 0;
   public int guest_id16 = 0;
   public int guest_id17 = 0;
   public int guest_id18 = 0;
   public int guest_id19 = 0;
   public int guest_id20 = 0;
   public int guest_id21 = 0;
   public int guest_id22 = 0;
   public int guest_id23 = 0;
   public int guest_id24 = 0;
   public int guest_id25 = 0;
   public int oldguest_id1 = 0;
   public int oldguest_id2 = 0;
   public int oldguest_id3 = 0;
   public int oldguest_id4 = 0;
   public int oldguest_id5 = 0;
   public int oldguest_id6 = 0;
   public int oldguest_id7 = 0;
   public int oldguest_id8 = 0;
   public int oldguest_id9 = 0;
   public int oldguest_id10 = 0;
   public int oldguest_id11 = 0;
   public int oldguest_id12 = 0;
   public int oldguest_id13 = 0;
   public int oldguest_id14 = 0;
   public int oldguest_id15 = 0;
   public int oldguest_id16 = 0;
   public int oldguest_id17 = 0;
   public int oldguest_id18 = 0;
   public int oldguest_id19 = 0;
   public int oldguest_id20 = 0;
   public int oldguest_id21 = 0;
   public int oldguest_id22 = 0;
   public int oldguest_id23 = 0;
   public int oldguest_id24 = 0;
   public int oldguest_id25 = 0;
   public int checkothers = 0;
   public int skip = 0;
   public int grest_num = 0;
   public int recur_type = 0;
   public int update_recur = 0;

   public short rfb = 0;
   public short fb_after = 0;
   public short fb_before = 0;

   public String ltype = "";             // lottery type
   public String club = "";
   public String course = "";
   public String returnCourse = "";
   public String courseReq = "";
   public String approve = "";
   public String day = "";
   public String player = "";
   public String gplayer = "";
   public String mship = "";
   public String mtype = "";
   public String period = "";
   public String rest_name = "";
   public String hides = "";
   public String notes = "";
   public String index = "";
   public String index2 = "";
   public String lottName = "";
   public String p5 = "";
   public String p5rest = "";
   public String jump = "";
   public String sfb = "";
   public String in_use_by = "";
   public String orig_by = "";
   public String grest_per = "";
   public String error_hdr = "";
   public String error_msg = "";
   public String recur_start = "";
   public String recur_end = "";

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

   public String oldplayer1 = "";
   public String oldplayer2 = "";
   public String oldplayer3 = "";
   public String oldplayer4 = "";
   public String oldplayer5 = "";
   public String oldplayer6 = "";
   public String oldplayer7 = "";
   public String oldplayer8 = "";
   public String oldplayer9 = "";
   public String oldplayer10 = "";
   public String oldplayer11 = "";
   public String oldplayer12 = "";
   public String oldplayer13 = "";
   public String oldplayer14 = "";
   public String oldplayer15 = "";
   public String oldplayer16 = "";
   public String oldplayer17 = "";
   public String oldplayer18 = "";
   public String oldplayer19 = "";
   public String oldplayer20 = "";
   public String oldplayer21 = "";
   public String oldplayer22 = "";
   public String oldplayer23 = "";
   public String oldplayer24 = "";
   public String oldplayer25 = "";

   public String user1 = "";
   public String user2 = "";
   public String user3 = "";
   public String user4 = "";
   public String user5 = "";
   public String user6 = "";
   public String user7 = "";
   public String user8 = "";
   public String user9 = "";
   public String user10 = "";
   public String user11 = "";
   public String user12 = "";
   public String user13 = "";
   public String user14 = "";
   public String user15 = "";
   public String user16 = "";
   public String user17 = "";
   public String user18 = "";
   public String user19 = "";
   public String user20 = "";
   public String user21 = "";
   public String user22 = "";
   public String user23 = "";
   public String user24 = "";
   public String user25 = "";

   public String olduser1 = "";
   public String olduser2 = "";
   public String olduser3 = "";
   public String olduser4 = "";
   public String olduser5 = "";
   public String olduser6 = "";
   public String olduser7 = "";
   public String olduser8 = "";
   public String olduser9 = "";
   public String olduser10 = "";
   public String olduser11 = "";
   public String olduser12 = "";
   public String olduser13 = "";
   public String olduser14 = "";
   public String olduser15 = "";
   public String olduser16 = "";
   public String olduser17 = "";
   public String olduser18 = "";
   public String olduser19 = "";
   public String olduser20 = "";
   public String olduser21 = "";
   public String olduser22 = "";
   public String olduser23 = "";
   public String olduser24 = "";
   public String olduser25 = "";

   public String pcw1 = "";
   public String pcw2 = "";
   public String pcw3 = "";
   public String pcw4 = "";
   public String pcw5 = "";
   public String pcw6 = "";
   public String pcw7 = "";
   public String pcw8 = "";
   public String pcw9 = "";
   public String pcw10 = "";
   public String pcw11 = "";
   public String pcw12 = "";
   public String pcw13 = "";
   public String pcw14 = "";
   public String pcw15 = "";
   public String pcw16 = "";
   public String pcw17 = "";
   public String pcw18 = "";
   public String pcw19 = "";
   public String pcw20 = "";
   public String pcw21 = "";
   public String pcw22 = "";
   public String pcw23 = "";
   public String pcw24 = "";
   public String pcw25 = "";

   public String oldpcw1 = "";
   public String oldpcw2 = "";
   public String oldpcw3 = "";
   public String oldpcw4 = "";
   public String oldpcw5 = "";
   public String oldpcw6 = "";
   public String oldpcw7 = "";
   public String oldpcw8 = "";
   public String oldpcw9 = "";
   public String oldpcw10 = "";
   public String oldpcw11 = "";
   public String oldpcw12 = "";
   public String oldpcw13 = "";
   public String oldpcw14 = "";
   public String oldpcw15 = "";
   public String oldpcw16 = "";
   public String oldpcw17 = "";
   public String oldpcw18 = "";
   public String oldpcw19 = "";
   public String oldpcw20 = "";
   public String oldpcw21 = "";
   public String oldpcw22 = "";
   public String oldpcw23 = "";
   public String oldpcw24 = "";
   public String oldpcw25 = "";

   public String mNum1 = "";
   public String mNum2 = "";
   public String mNum3 = "";
   public String mNum4 = "";
   public String mNum5 = "";
   public String mNum6 = "";
   public String mNum7 = "";
   public String mNum8 = "";
   public String mNum9 = "";
   public String mNum10 = "";
   public String mNum11 = "";
   public String mNum12 = "";
   public String mNum13 = "";
   public String mNum14 = "";
   public String mNum15 = "";
   public String mNum16 = "";
   public String mNum17 = "";
   public String mNum18 = "";
   public String mNum19 = "";
   public String mNum20 = "";
   public String mNum21 = "";
   public String mNum22 = "";
   public String mNum23 = "";
   public String mNum24 = "";
   public String mNum25 = "";

   public String pNum1 = "";
   public String pNum2 = "";
   public String pNum3 = "";
   public String pNum4 = "";
   public String pNum5 = "";
   public String pNum6 = "";
   public String pNum7 = "";
   public String pNum8 = "";
   public String pNum9 = "";
   public String pNum10 = "";
   public String pNum11 = "";
   public String pNum12 = "";
   public String pNum13 = "";
   public String pNum14 = "";
   public String pNum15 = "";
   public String pNum16 = "";
   public String pNum17 = "";
   public String pNum18 = "";
   public String pNum19 = "";
   public String pNum20 = "";
   public String pNum21 = "";
   public String pNum22 = "";
   public String pNum23 = "";
   public String pNum24 = "";
   public String pNum25 = "";

   public String mship1 = "";
   public String mship2 = "";
   public String mship3 = "";
   public String mship4 = "";
   public String mship5 = "";
   public String mship6 = "";
   public String mship7 = "";
   public String mship8 = "";
   public String mship9 = "";
   public String mship10 = "";
   public String mship11 = "";
   public String mship12 = "";
   public String mship13 = "";
   public String mship14 = "";
   public String mship15 = "";
   public String mship16 = "";
   public String mship17 = "";
   public String mship18 = "";
   public String mship19 = "";
   public String mship20 = "";
   public String mship21 = "";
   public String mship22 = "";
   public String mship23 = "";
   public String mship24 = "";
   public String mship25 = "";

   public String mtype1 = "";
   public String mtype2 = "";
   public String mtype3 = "";
   public String mtype4 = "";
   public String mtype5 = "";
   public String mtype6 = "";
   public String mtype7 = "";
   public String mtype8 = "";
   public String mtype9 = "";
   public String mtype10 = "";
   public String mtype11 = "";
   public String mtype12 = "";
   public String mtype13 = "";
   public String mtype14 = "";
   public String mtype15 = "";
   public String mtype16 = "";
   public String mtype17 = "";
   public String mtype18 = "";
   public String mtype19 = "";
   public String mtype20 = "";
   public String mtype21 = "";
   public String mtype22 = "";
   public String mtype23 = "";
   public String mtype24 = "";
   public String mtype25 = "";

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
   public String fname6 = "";
   public String lname6 = "";
   public String mi6 = "";
   public String fname7 = "";
   public String lname7 = "";
   public String mi7 = "";
   public String fname8 = "";
   public String lname8 = "";
   public String mi8 = "";
   public String fname9 = "";
   public String lname9 = "";
   public String mi9 = "";
   public String fname10 = "";
   public String lname10 = "";
   public String mi10 = "";
   public String fname11 = "";
   public String lname11 = "";
   public String mi11 = "";
   public String fname12 = "";
   public String lname12 = "";
   public String mi12 = "";
   public String fname13 = "";
   public String lname13 = "";
   public String mi13 = "";
   public String fname14 = "";
   public String lname14 = "";
   public String mi14 = "";
   public String fname15 = "";
   public String lname15 = "";
   public String mi15 = "";
   public String fname16 = "";
   public String lname16 = "";
   public String mi16 = "";
   public String fname17 = "";
   public String lname17 = "";
   public String mi17 = "";
   public String fname18 = "";
   public String lname18 = "";
   public String mi18 = "";
   public String fname19 = "";
   public String lname19 = "";
   public String mi19 = "";
   public String fname20 = "";
   public String lname20 = "";
   public String mi20 = "";
   public String fname21 = "";
   public String lname21 = "";
   public String mi21 = "";
   public String fname22 = "";
   public String lname22 = "";
   public String mi22 = "";
   public String fname23 = "";
   public String lname23 = "";
   public String mi23 = "";
   public String fname24 = "";
   public String lname24 = "";
   public String mi24 = "";
   public String fname25 = "";
   public String lname25 = "";
   public String mi25 = "";

   //
   //  guest array for Member_lott
   //
   public String [] gstA = new String [25];     // guests (entire player name)
   public String [] g = new String [25];        // guest type of player position (if guest)
   public String [] userg = new String [25];     // and user guest names
   
   public String getOldPlayer(int index) {
       return getStringByName("oldplayer%", index);
   }
    
   public boolean hasOldPlayer(String name) {

       for (int i = 0; i < 5; i++) {
           if (getOldPlayer(i).equalsIgnoreCase(name)) {
               return true;
           }
       }
       return false;
   }

}  // end of class
