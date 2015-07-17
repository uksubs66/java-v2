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
   //  Arrays to hold tee time info while processing the requests (max tee times = 100)
   //
   //     'new' initializes values to '0'
   //
     
   //
   //  The following are used to hold the requests in the order to be processed
   //
   public long [] idA = new long [100];          // request id array
   public int [] wghtA = new int [100];          // weight of this request
     

   //
   //   **** NOTE: the following 6 arrays are all associated and use the same index value  
   //
   //
   //  The following are used to hold the requests after they have been assigned times
   //
   public long [] id2A = new long [100];          // request id array
   public int [] wght2A = new int [100];          // weight of this request
   public int [] players2A = new int [100];       // # of players in the request

   //
   //  The following are used to hold the tee times that are available
   //
   public int [] timeA = new int [100];           // tee time
   public short [] fbA = new short [100];         // f/b for the tee time
   public boolean [] busyA = new boolean [100];   // tee time busy flag

   public int [] atimeA = new int [5];           // assigned tee time array per req (save area)

}  // end of class
