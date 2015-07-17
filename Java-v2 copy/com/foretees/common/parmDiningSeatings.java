/*****************************************************************************************
 *   parmDiningSeating:  This class parses the dining event seating string in to useable parts
 *
 *
 *   called by:  several
 *
 *   created: 06/16/2011   Paul S.
 *
 *   last updated:
 *
 *
 *
 *
 *****************************************************************************************
 */


package com.foretees.common;

import java.util.ArrayList;
import java.sql.Connection;


/**
 *
 * @author sindep
 */
public class parmDiningSeatings {

    
    public String seatings = "";   // holds the data from the "costs" field in the dining event table
    public String err_string = "";
    public String err_message = "";
    
    public String [] seating_timeA;
    public String [] seating_time_valueA;
    
    public int seatings_found = 0;

    Connection con = null;
    

 public boolean parseSeatings() {
     

/*

 ---
- !ruby/object:Seating
  time: 2011-11-23 18:00:00 -05:00
- !ruby/object:Seating
  time: 2011-11-23 20:00:00 -05:00

*/

    boolean result = false;
    
    int pos1 = 0, pos2 = 0, marker = 0, last = 0, i = 0, time = 0;
    String tmp = "", tmp1 = "";

    last = seatings.lastIndexOf("!ruby/object:Seating");

    // use array lists for temporary storage
    ArrayList<String> listArray1 = new ArrayList<String> ();
    ArrayList<String> listArray2 = new ArrayList<String> ();

    try {

        while (marker < last) {

            pos1 = seatings.indexOf("time: ", marker) + 5;
            tmp = seatings.substring(pos1, pos1 + 27).trim();
            
            tmp1 = tmp.substring(11, 16);
            listArray2.add(tmp1); // seating times array
            
            time = Integer.parseInt(tmp.substring(11, 16).replace(":", ""));
            
            tmp = Utilities.getSimpleTime(Utilities.adjustTime(con, time));
            
            listArray1.add(tmp); // seating times array

            marker = pos1 + 26;
            i++;

        }
        
            seating_timeA = new String[listArray1.size()];
            seating_time_valueA = new String[listArray2.size()];

            listArray1.toArray(seating_timeA);
            listArray2.toArray(seating_time_valueA);

            seatings_found = i;

        
        result = true;

    } catch (Exception exc) {

        Utilities.logError("parmDiningSeatings: FATAL ERROR: " + exc.toString() + ", MSG: " + exc.getMessage());
        
        err_string = exc.toString();
        err_message = exc.getMessage();
        
    }    

    return result;
     
 } // end of parseCosts

} // end of class
