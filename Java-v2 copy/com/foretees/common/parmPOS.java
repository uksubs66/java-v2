/***************************************************************************************
 *   parmPOS:  This class will define a paramter block object to be used for POS Parms.
 *
 *
 *   called by:  several
 *
 *   created: 12/08/2003   Bob P.
 *
 *   last updated:
 *
 *       8/30/12  Add combineCharges and chargesA for ClubSoft POS.
 *       2/06/11  Changes for supporting the new IBS interface.
 *       9/28/10  Add salestax fields for CSG.
 *       6/16/09  Add fields for Club Prophet Systems so we can combine charges for members.
 *       2/20/09  Add returnCode strings for Club Prophet Systems.
 *       7/17/08  Add pos_paynow and fields for making history entry.
 *       9/30/06  Add salestax for IBS.
 *
 ***************************************************************************************
 */

package com.foretees.common;

import java.util.ArrayList;
import org.tempuri.*;       // CPS
import org.ibsservices.*;   // IBS



public class parmPOS {

   public boolean error = false;
   public boolean posSent1 = false;        // charges sent for this player
   public boolean posSent2 = false;
   public boolean posSent3 = false;
   public boolean posSent4 = false;
   public boolean posSent5 = false;
   public boolean combineCharges = false;        // combine alike charges for this club
     
   public int MAX_Guests = Labels.MAX_GUESTS;         // IN Labels
   public int MAX_Mems = Labels.MAX_MEMS;
   public int MAX_Mships = Labels.MAX_MSHIPS;
   public int MAX_Tmodes = Labels.MAX_TMODES;

   public String posType = "";                 // System type (Jonus, Pro-ShopKeeper, etc.)
   public String sdate = "";
   public String poslist = "";
   public String player = "";
   public String player1 = "";
   public String player2 = "";
   public String player3 = "";
   public String player4 = "";
   public String player5 = "";
   public String user = "";
   public String pcw = "";
   public String courseid = "";
   public String course = "";
   public String club = "";
   public String day = "";                   // name of day
   public String posid = "";
   public String fname = "";
   public String lname = "";
   public String posid1 = "";
   public String posid2 = "";
   public String posid3 = "";
   public String posid4 = "";
   public String posid5 = "";
   public String fname1 = "";
   public String fname2 = "";
   public String fname3 = "";
   public String fname4 = "";
   public String fname5 = "";
   public String lname1 = "";
   public String lname2 = "";
   public String lname3 = "";
   public String lname4 = "";
   public String lname5 = "";
   public String mship0 = "";
   public String mship1 = "";
   public String mship2 = "";
   public String mship3 = "";
   public String mship4 = "";
   public String mship5 = "";
     
   //
   //  Event Charge Codes
   //
   public String mempos = "";
   public String gstpos = "";
     

   public int count = 0;
   public int p9 = 0;
   public int time = 0;
   public int pos_paynow = 0;

   public long date = 0;

   public double salestax = 0;

   //
   //  Jonas - unique fields
   //
   public String stime = "";
   public String memNum = "";      // posid from member2b
   public String item = "";        // item name

   
   //
   //  Club Prophet Systems - unique fields
   //
   public String returnCode1 = "";
   public String returnCode2 = "";
   public String returnCode3 = "";
   public String returnCode4 = "";
   public String returnCode5 = "";
   
   
   //
   //   Fields for history table entry (pos_hist table)
   //
   public int hist_fb = 0;
   public String hist_posid = "";
   public String hist_player = "";
   public String hist_item_num = "";
   public String hist_item_name = "";
   public String hist_price = "";
   

   //
   //  arrays
   //
   public String [] gtype = new String [Labels.MAX_GUESTS];    // guest types
   public String [] gpos = new String [Labels.MAX_GUESTS];     // guest type codes (18 holes)
   public String [] g9pos = new String [Labels.MAX_GUESTS];    // guest type codes (9 holes)

   public String [] gstI = new String [Labels.MAX_GUESTS];     // Item Group # for guest type
   public String [] gst9I = new String [Labels.MAX_GUESTS];    // Item Group # for guest type (9 holes)
   public double [] salestaxg = new double [Labels.MAX_GUESTS];  // sales tax rate

   public String [] tmode = new String [Labels.MAX_TMODES];    // Modes of trans description
   public String [] tmodea = new String [Labels.MAX_TMODES];   // Modes of trans acronyms
   public String [] tpos = new String [Labels.MAX_TMODES];     // Modes of trans codes (18 holes)
   public String [] t9pos = new String [Labels.MAX_TMODES];    // modes of trans codes (9 holes)
   public String [] tposc = new String [Labels.MAX_TMODES];    // Modes of trans cost (18 holes)
   public String [] t9posc = new String [Labels.MAX_TMODES];   // modes of trans cost (9 holes)
   public double [] salestaxt = new double [Labels.MAX_TMODES];  // sales tax rate

   public String [] mship = new String [Labels.MAX_MSHIPS];     // mship types
   public String [] mpos = new String [Labels.MAX_MSHIPS];      // mship type class codes (Pro-ShopKeeper)
   public String [] mposc = new String [Labels.MAX_MSHIPS];     // mship type charge codes (Jonas)
   public String [] m9posc = new String [Labels.MAX_MSHIPS];    // mship type charge codes (Jonas)

   public String [] mshipI = new String [Labels.MAX_MSHIPS];    // Item Group # for mship type
   public String [] mship9I = new String [Labels.MAX_MSHIPS];   // Item Group # for mship type (9 holes)
   public double [] salestaxm = new double [Labels.MAX_MSHIPS];   // sales tax rate

   //
   // CPSV3 - These two object contain the charge codes and the qty of each for Club Prophet Systems POS
   //
   public org.tempuri.ArrayOfString codeA = new org.tempuri.ArrayOfString();
   public org.tempuri.ArrayOfInt qtyA = new org.tempuri.ArrayOfInt();

   //
   //  Array list used to combine charges (initially used for ClubSoft POS)
   //
   public ArrayList<String> chargesA = new ArrayList<String>();

   //
   // IBS specific objects [posid][charge string]
   //
   public ArrayList<ArrayList<String>> charges  = new ArrayList<ArrayList<String>>();

   //public ArrayList <ArrayList <ArrayList <ArrayList <ArrayList <String>>>>> charges  = new ArrayList <ArrayList <ArrayList <ArrayList <ArrayList <String>>>>>();

}  // end of class
