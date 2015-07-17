/***************************************************************************************
 *   parmRest:  This class will define a parameter block object to be used for Restriction Parms.
 *
 *
 *   called by:  Member_sheet
 *
 *   created: 8/02/2004   Bob P.
 *
 *   last updated:
 *
 *      03/28/10  Added club and msubtype so we can add custom processing.
 *      03/26/10  Removed act_id and added locations (csv) string
 *      10/10/09  Added rest_id for easy access to other restriction info.
 *      10/07/09  Added activity_id for GenRez
 *      11/12/08  Added array to hold restriction names and restr
 *      11/06/08  Added array to hold restriction suspensions
 *      04/16/07  Increased MAX and arrays from 40 to 60
 *
 *
 ***************************************************************************************
 */

package com.foretees.common;


public class parmRest {

   //
   //  parms to identify the user and tee sheet for each request
   //
   public String user = "";
   public String mship = "";
   public String mtype = "";
   public String club = "";
   public String msubtype = "";
   public String course = "";
   public String day = "";
   public long date = 0;
   public int MAX = 60;                 // max number of entries
   public int activity_id = 0;          // hold the root activity id

   //
   //  Arrays to hold the Restrictions for the member displaying the tee sheet (allow up to 40)
   //   
   public String [] restName = new String [MAX];
   public String [] courseName = new String [MAX];
   public String [] color = new String [MAX];
   public String [] fb = new String [MAX];
   public String [] locations = new String [MAX];      // location csv
   public int [] applies = new int [MAX];
   public int [] stime = new int [MAX];
   public int [] etime = new int [MAX];
// public int [] act_id = new int [MAX];               // activity ids (now using locations not act_id)
   public int [] rest_id = new int [MAX];              // restriction ids
   public int [][][] susp = new int [MAX][MAX][2];

}  // end of class
