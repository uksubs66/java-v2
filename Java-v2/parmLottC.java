/***************************************************************************************
 *   parmLottC:  This class will define a paramter block object to be used for lottery processing.
 *               One per Course.
 *
 *
 *   called by:                  
 *               SystemUtils
 *
 *   created: 6/07/2005   Bob P.
 *
 *   last updated:
 *
 *      7/22/13  Add the assigned f/b array so we can use both front and back (case 2286).
 *      7/31/12  Add constructor for new lottery processing where all tee times are in
 *               one parm block (for all courses) rather than one parm block per course.
 *     11/10/09  Added busyA array to mark tee times busy if any players in them.
 *
 ***************************************************************************************
 */

public class parmLottC {


   //
   //  Name of course that this parm block is allocated for
   //
   public String course = "";
   
   //
   //   Length of the Request and Tee Time Arrays below
   //
   public int req_count = 0;
   public int tee_count = 0;
   

   //
   //  Arrays to hold tee time info while processing the requests (max tee times = 100)
   //
   //     'new' initializes values to '0'
   //
     
   //
   //  The following are used to hold the requests in the order to be processed
   //
   public long [] idA = new long [1];          // request id array
   public int [] wghtA = new int [1];          // weight of this request
     

   //
   //   **** NOTE: the following 6 arrays are all associated and use the same index value  
   //
   //
   //  The following are used to hold the requests after they have been assigned times
   //
   public long [] id2A = new long [1];          // request id array
   public int [] wght2A = new int [1];          // weight of this request
   public int [] players2A = new int [1];       // # of players in the request

   //
   //  The following are used to hold the tee times that are available
   //
   public int [] timeA = new int [1];           // tee time
   public short [] fbA = new short [1];         // f/b for the tee time
   public String [] courseA = new String [1];   // course name for tee time
   public boolean [] busyA = new boolean [1];   // tee time busy flag

   public int [] atimeA = new int [1];           // assigned tee time array per req (save area)
   public short [] afbA = new short [1];             // assigned f/b array per req (save area)
   
   
   //
   //  Provide a default constructor for the old lottery processing that assumed a size of 100 for the arrays
   //
   public parmLottC () {
       
       course = "";                     // name of this course

       idA = new long [100];            // request id array
       wghtA = new int [100];           // weight of this request

       id2A = new long [100];           // request id array
       wght2A = new int [100];          // weight of this request
       players2A = new int [100];       // # of players in the request

       timeA = new int [100];           // tee time
       fbA = new short [100];           // f/b for the tee time
       busyA = new boolean [100];       // tee time busy flag

       atimeA = new int [5];            // assigned tee time array per req (save area)
   }
   
   
   //
   //  Provide a flexible constructor for the new lottery processing that calculates the size needed and uses one parm for all courses and requests
   //
   public parmLottC (int req_size, int tee_size) {
       
       if (tee_size < req_size) tee_size = req_size;     // must be at least as many entries for tee times as there are requests
       
       tee_size++;                           // must add one entry for 'end of entries' marker (0)
       
       course = "";                          // name of the course specified in Lottery (or -ALL-)

       idA = new long [req_size];            // request id array
       wghtA = new int [req_size];           // weight of this request

       id2A = new long [tee_size];           // request id array (one entry for each tee time so they match by index)
       wght2A = new int [tee_size];          // weight of this request
       players2A = new int [tee_size];       // # of players in the request

       timeA = new int [tee_size];           // tee time
       fbA = new short [tee_size];           // f/b for the tee time
       courseA = new String [tee_size];      // course for the tee time (required for new processing - make time the priority)
       busyA = new boolean [tee_size];       // tee time busy flag

       atimeA = new int [5];                 // assigned tee time array per req (save area)
       afbA = new short [5];                   // assigned f/b array per req (save area)
       
       req_count = req_size;                 // save array lengths
       tee_count = tee_size;
       
       courseA[(tee_size-1)] = "";           // remove null value of last tee marker entery
   }
   
}  // end of class
