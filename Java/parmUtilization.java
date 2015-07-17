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
 *
 ***************************************************************************************
 */

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
   int max = 300;                                 // allow for event times
     
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

}  // end of class
