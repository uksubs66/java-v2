/***************************************************************************************
 *   parmUtilization:  This class will define a paramter block object to be used for 
 *                     the Utilization Report (Proshop only).
 *
 *   called by:  Proshop_report_course_utilization
 *
 *
 *   created: 9/30/2006   Bob P.
 *
 *   last updated:
 *
 *    6/28/13   Added count_restricted boolean to be used for FlxRez when restricted time slots need to be counted as utilized.
 *    3/08/11   Added total_ fields for FlxRez to hold daily totals for later display, as well as ArrayList versions of
 *              FlxRez data Arrays for use with the "Each Time Slot" report option
 *   12/22/10   Added time array to hold FlxRez time slots
 *
 *
 ***************************************************************************************
 */

import java.util.*;

public class parmUtilization {

   boolean error = false;
     
   boolean tTimesAvail = false;                  // column display indicators
   boolean tTimesUsed = false;
   boolean tTimesUnused = false;
   boolean tTimesFull = false;
   boolean t3players = false;
   boolean t2players = false;
   boolean t1player = false;
   boolean eTimesUsed = false;
   boolean eTimesUnused = false;
   boolean slotsAvail = false;
   boolean slotsUsed = false;
   boolean slotsUnused = false;
   boolean memRounds = false;
   boolean gstRounds = false;

   long sdate = 0;
   long edate = 0;

   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int start_year;
   int start_month;
   int start_day;
   int end_year;
   int end_month;
   int end_day;
   int colCount = 0;         // # of columns req'd

   String club = "";
   String format = "";
   String data = "";
   String events = "";
   String interval = "";
   String course = "";
   String day = "";


   //
   //  arrays (allow enough to hold for all tee times during a specific day)
   //
   int max = 400;                                 // allow for event times

   int [] times = new int [max];                  // tee time values
   int [] timesA = new int [max];                 // tee times available counts
   int [] timesU = new int [max];                 // tee times utilized counts
   int [] slotsU = new int [max];                 // slots utilized counts
   int [] eventsU = new int [max];                // event times utilized
   int [] singles = new int [max];                // times with only one player
   int [] doubles = new int [max];                //          2 players
   int [] triples = new int [max];                //          3 players
   int [] fulls = new int [max];                  //          at least 4 players
   int [] members = new int [max];                // # of members
   int [] guests = new int [max];                 // # of guests
   int [] eTimesA = new int [max];                // # of empty tee times available
   int [] eEvents = new int [max];                // # of empty event times

   // added for activity report
   int activity_id = 0;
   int day_num = 0;
   String locations_csv = "";
   boolean t4players = false;
   boolean inc_events = false;
   boolean inc_gender = false;
   boolean count_restricted = false;

   int [] hour = new int[24];
   int [] slots_avail = new int[24];
   int [] slots_used = new int[24];
   int [] slots_unused = new int[24];
   int [] event_slots = new int[24];
   int [] event_slots_used = new int[24];
   int [] event_slots_unused = new int[24];
   int [] quads = new int [24];
   int [] males = new int [24];
   int [] females = new int [24];

   int total_guests = 0;
   int total_members = 0;
   int total_males = 0;
   int total_females = 0;
   int total_singles = 0;
   int total_doubles = 0;
   int total_triples = 0;
   int total_quads = 0;
   int total_slots_avail = 0;
   int total_slots_used = 0;
   int total_slots_unused = 0;
   int total_event_slots = 0;
   int total_event_slots_used = 0;
   int total_event_slots_unused = 0;
   
   ArrayList<Integer> timeSlotL = new ArrayList<Integer>();
   ArrayList<Integer> guestsL = new ArrayList<Integer>();
   ArrayList<Integer> membersL = new ArrayList<Integer>();
   ArrayList<Integer> malesL = new ArrayList<Integer>();
   ArrayList<Integer> femalesL = new ArrayList<Integer>();
   ArrayList<Integer> singlesL = new ArrayList<Integer>();
   ArrayList<Integer> doublesL = new ArrayList<Integer>();
   ArrayList<Integer> triplesL = new ArrayList<Integer>();
   ArrayList<Integer> quadsL = new ArrayList<Integer>();
   ArrayList<Integer> slots_availL = new ArrayList<Integer>();
   ArrayList<Integer> slots_usedL = new ArrayList<Integer>();
   ArrayList<Integer> slots_unusedL = new ArrayList<Integer>();
   ArrayList<Integer> event_slotsL = new ArrayList<Integer>();
   ArrayList<Integer> event_slots_usedL = new ArrayList<Integer>();
   ArrayList<Integer> event_slots_unusedL = new ArrayList<Integer>();

}  // end of class
