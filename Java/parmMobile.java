/***************************************************************************************
 *   parmMobile:  This class will define a paramter block object to be used for mobile processing.
 *
 *
 *   called by:  Member_lott
 *               Member_slot
 *
 *   created: 9/15/2009   Bob P.
 *
 *   last updated:
 *
 *   5/07/10   Added orig array
 *   4/18/10   Added guest_id array
 *
 ***************************************************************************************
 */

public class parmMobile {

   public long date = 0;
   public long lottid = 0;

   public int max = 25;              // max number of players to allow 
   
   public int mobile = 0;            // mobile device type (level of javascript support) 
   public int in_use = 0;
   public int time = 0;
   public int time2 = 0;
   public int time3 = 0;
   public int time4 = 0;
   public int time5 = 0;
   public int fb = 0;
   public int mm = 0;
   public int dd = 0;
   public int yy = 0;
   public int ind = 0;
   public int players = 0;
   public int hide = 0;
   public int lstate = 0;
   public int mins_before = 0;
   public int mins_after = 0;
   public int checkothers = 0;
   public int slots = 0;

   public String user = "";              // username of member making request
   public String type = "";              // request type (lottery or tee time)
   public String ltype = "";             // lottery type
   public String club = "";
   public String course = "";
   public String returnCourse = "";
   public String day = "";
   public String notes = "";
   public String index = "";
   public String lottName = "";
   public String p5 = "";
   public String p5rest = "";
   public String jump = "";
   public String sfb = "";
   public String stime = "";
   public String sdate = "";
   public String displayOpt = "";
   
   public boolean twoSomeOnly = false;
   public boolean threeSomeOnly = false;
   public boolean allowCancel = false;
   public boolean newreq = false;

   public String [] playerA = new String [max];     // players
   public String [] pcwA = new String [max];        // modes of trans
   public String [] origA = new String [max];       // originator of each player slot
   public int [] p9A = new int [max];               // 9-hole indicators
   public int [] gpA = new int [max];               // gift pack indicators
   public int [] guest_idA = new int [max];         // guest ids
   public boolean [] blockPA = new boolean [max];   // block player position indicators

   
}  // end of class
