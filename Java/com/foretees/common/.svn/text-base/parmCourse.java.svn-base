/***************************************************************************************
 *   parmCourse:  This class will define a parameter block object to be used for Course Parms.
 *
 *
 *   called by:  several
 *
 *   created: 2/06/2004   Bob P.
 *
 *   last updated:
 *
 *      6/16/08   Added tOpt array to hold Pro-Only indicators for tmodes
 * 	
 *
 *
 ***************************************************************************************
 */

package com.foretees.common;


public class parmCourse {

   public int tmode_limit = Labels.MAX_TMODES;       // max of 16 tmodes

   public int first_hr = 0;
   public int first_min = 0;
   public int last_hr = 0;
   public int last_min = 0;
   public int betwn = 0;
   public int xx = 0;
   public int alt = 0;
   public int fives = 0;
   public int tmode_count = 0;

   public String courseName = "";
   
   public boolean hasProOnlyTmodes = false;

   //
   //  Arrays to hold the course transportation mode parms
   //
   public String [] tmode = new String [tmode_limit];       // max of 16 transportation modes per course
   public String [] tmodea = new String [tmode_limit];      // acronyms for trans modes
   public String [] tpos = new String [tmode_limit];        // POS Charge Codes for trans modes (18 holes)
   public String [] t9pos = new String [tmode_limit];       // POS Charge Codes for trans modes (9 holes)
   public int [] tOpt = new int  [tmode_limit];             // Pro-Only indicators for tmodes

}  // end of class
