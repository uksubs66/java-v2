/***************************************************************************************
 *   Common_Lott:  This servlet will process common Lottery functions.
 *
 *
 *
 *   created: 7/12/2006   Bob P.
 *
 *   last updated:       ******* keep this accurate *******
 *
 *       2/20/14   Changes for updaing recurred lottery requests, added rechainRecurReq method
 *      11/26/13   Pecan Plantation - start the custom below on 12/06/13.
 *      11/21/13   Pecan Plantation - When processing a member's weight, check past tee times for a no-show.  If so,
 *                                    use a weight of zero for that time.
 *      11/19/13   Ballenisles - update getWeightP to count any guests in the group as having a weight of zero.
 *      11/02/13   Update addRecurrRequests to chain the lottery requests together using recur_id in lreqs3.
 *      10/04/13   Correct how we are checking the course name in checkAfter so that we find an available time first, then look for a matching course
 *                 for any subsequent groups in the request.  Also, in checkRests do not check for minimum number of players or members when processing
 *                 the lottery.  If the request does not meet the minimum numbers, then the pro must have allowed it.
 *       9/21/13   Include all tee times, even if blocked or event defined, in buildParmc and buildArrays so
 *                 assign1Time will always find the time requested.  These times are flagged as busy so
 *                 they will not be assigned.
 *       9/21/13   Add debug info to error log in assign1Time to help debug exceptions.
 *       9/19/13   Add debuglog entries at start and end of lottery processing to track these.
 *       9/09/13   Set useBothFB option for Plantation G&CC (plantationgcc) - like Estero.
 *       8/30/13   Add addRecurrRequests so Proshop_lott and Member_lott can add recurring lottery requests.
 *       7/22/13   Add useBothFB option for Estero CC to use both F/Bs when assigning lottery requests (case 2286).
 *       6/03/13   Change in assign1Time to correct the i3 index so we check for restrictions using the correct course and time.  We were falsely hitting a 
 *                 restriction because we were checking the entry following the good one.
 *       5/31/13   Add debug message for Gallery Golf when request fails because of a restriction - in assign1Time.
 *       5/10/13   buildArrays & buildParmC - add custom for Gallery (gallerygolf) to include the blocked tee times in case they blocked the times after
 *                                            the members submitted their requests (tee time must be found in order to process).
 *       2/26/13   moveReqs method will now generate history entires for converted lottery requests, and will now be passed a username value.
 *      12/13/12   moveReqs method will now populate the orig1-5 fields when all lottery requests is auto_converted.
 *       8/01/12   Westchester - Lottery Weighted By Rounds processing - reverse the weighting so it gives priority to those members that play the most.
 *       7/31/12   Move lottery methods from SystemUtils to here and add support for time preference when assigning requests (check time on all courses).
 *       4/12/11   Allow for X's in lottery requests.
 *       4/12/11   Fixed issue with list of restricted guest types not getting reset when more than one applicable guest restrictions found for a given member.
 *       6/30/10   Fixed typo in checkGuests to prevent miscount of guests in group 3
 *       9/02/09   Changed mship gathering processing to grab mships from mship5 instead of club5
 *      12/04/08   Added restriction suspension checking for member and guest restrictions
 *      10/10/06   When checking guest restrictions, only check the guest types that are restricted.
 *       9/18/06   Add checkGuestQuota from Member_lott & Proshop_lott since they are identical.
 *
 *
 ***************************************************************************************
 */

//import java.io.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.internet.*;
import javax.mail.*;


// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.parmSlot;
import com.foretees.common.verifySlot;
import com.foretees.common.alphaTable;
import com.foretees.common.Utilities;


public class Common_Lott {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)



 //************************************************************************
 // processLott - Process a lottery by processing all the lottery requests
 //               queued for a specific lottery and day.
 //
 //
 //   called by:  SystemUtils.checkTime 
 //
 //       parms:  name   = name of the lottery
 //               date   = actual date of the lottery event
 //               con    = db connection
 //
 //************************************************************************

 public static void processLott(String name, long date, String club, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs3 = null;


   boolean ok = false;
   boolean fail = false;

   String color = "";
   String course = "";
   String fb = "";
   String p5 = "";
   String type = "";
   String pref = "";
   String approve = "";
   String day = "";
   String in_use_by = "";

   String errorMsg = "";

   int adays = 0;
   int wdpts = 0;
   int wepts = 0;
   int evpts = 0;
   int gpts = 0;
   int nopts = 0;
   int selection = 0;
   int guest = 0;
   int slots = 0;
   int players = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int time2 = 0;
   int stime = 0;
   int etime = 0;
   int ttime = 0;
   int before = 0;
   int after = 0;
   int proNew = 0;
   int proMod = 0;
   int memNew = 0;
   int memMod = 0;
   int in_use = 0;
   int groups = 0;
   int grps = 0;
   int state = 0;
   int index = 0;
   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int i4 = 0;
   int weight = 0;
   int error = 0;
   int count = 0;
   int courseCount = 0;
   int full = 0;
   int checkothers = 0;

   short tfb = 0;
   short tfb2 = 0;

   long id = 0;
   long tid = 0;
   
   //
   //  Log the start of this lottery processing event (to determine how long it takes and if we are doing this on time)
   //
   Utilities.logDebug("BP", "Lottery processing STARTED for " +club+ " and lottery " +name+ ".");                                   
   

   //
   //  Put the parms in a parm block for use in subr's
   //
   parmLott parm = new parmLott();          // allocate a parm block

   parm.date = date;                     // these are static through this function
   parm.lottName = name;
   parm.club = club;                     // save club name
   parm.course = "";                     // empty for now

   parmLottC parmc = null;               // assign a parm block name for course parm blocks

   //
   //  First, make sure there are still some lottery requests for this lottery
   //   (may have been all cancelled, or already processed).
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
             "SELECT mm FROM lreqs3 WHERE name = ? AND date = ? AND state = 0");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);
      pstmt1.setLong(2, date);
      rs = pstmt1.executeQuery();

      if (rs.next()) {

         ok = true;     // ok to continue
      }
      pstmt1.close();
   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = "Error in Common_Lott processLott (get actlott3): ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
      ok = false;                                               // not ok to continue
   }

   if (ok == true) {          // if requests still exist

      errorMsg = "Error in Common_Lott processLott (get lottery info): ";
      ok = false;             // default to not ok

      try {
         //
         //  Get the lottery info for the requested lottery
         //
         PreparedStatement pstmt2 = con.prepareStatement (
            "SELECT stime, etime, color, courseName, fb, type, adays, wdpts, wepts, evpts, gpts, " +
            "nopts, selection, guest, slots, pref, approve " +
            "FROM lottery3 " +
            "WHERE name = ?");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.setString(1, name);
         rs = pstmt2.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            stime = rs.getInt(1);
            etime = rs.getInt(2);
            color = rs.getString(3);
            parm.course = rs.getString(4);       // course from Lottery
            fb = rs.getString(5);
            type = rs.getString(6);
            adays = rs.getInt(7);
            wdpts = rs.getInt(8);
            wepts = rs.getInt(9);
            evpts = rs.getInt(10);
            gpts = rs.getInt(11);
            nopts = rs.getInt(12);
            selection = rs.getInt(13);
            guest = rs.getInt(14);
            slots = rs.getInt(15);
            pref = rs.getString(16);
            approve = rs.getString(17);

            parm.approve = approve;        // save

            ok = true;                     // ok to proceed
         }
         pstmt2.close();

      }
      catch (Exception e2) {
         //
         //  save error message in /v_x/error.txt
         //
         errorMsg = errorMsg + "Exception1= " +e2.getMessage();                  // build error msg

         logError(errorMsg);                                       // log it
         ok = false;
      }
   }

   //
   //  Determine number of courses for lottery
   //
   try {

      courseCount = 1;                              // init to 1 course

      if (parm.course.equals( "-ALL-" )) {          // if lottery for ALL courses

         courseCount = 0;                           // start at zero

         //
         //  Get the number of courses for this club
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName " +
                                 "FROM clubparm2 WHERE first_hr != 0");

         while (rs.next()) {

            courseCount++;
         }
         stmt.close();
      }

   }
   catch (Exception e1) {

      errorMsg = "Error in Common_Lott processLott (count courses): ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
      ok = false;                                               // not ok to continue
   }
   

   //
   //  Arrays to hold course information
   //
   // parmLottC [] parmcA = new parmLottC [courseCount];      // course parm blocks    
   String [] courseA = new String [courseCount];           // course names

   try {

      //
      //  Build arrays for courses and their parm blocks
      //
      if (parm.course.equals( "-ALL-" )) {          // if lottery for ALL courses

         //
         //  Get the names of all courses for this club
         //
         i = 0;

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName " +
                                 "FROM clubparm2 WHERE first_hr != 0");

         while (rs.next()) {

            course = rs.getString(1);

            courseA[i] = course;               // add course name to array
            //parmc = new parmLottC();           // get a new course parm block
            //parmcA[i] = parmc;                 // save it
            i++;
         }
         stmt.close();

      } else {

         courseA[0] = parm.course;             // set course name
         //parmc = new parmLottC();              // get a new course parm block
         //parmcA[0] = parmc;                    // save it
      }

   }
   catch (Exception e1) {

      errorMsg = "Error in Common_Lott processLott (get course names): ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
      ok = false;                                               // not ok to continue
   }
  
   
   
   if (ok == true) {       // if lottery still exist (if still ok to proceed)
   
       //
       //  New Method to give time a priority over the course - build one parmLottC to hold all information
       //
       parm.sfb = fb;            // save parms
       parm.stime = stime;
       parm.etime = etime;
       parm.ltype = type;        // lottery type

       parmc = buildParmC(con, parm, parmc);      // get a new course parm block customized for this lottery (array sizes)

       if (parmc == null) {

           errorMsg = "Error in Common_Lott processLott (buildParmC failed). ";
           logError(errorMsg);                                       // log it
           ok = false;           // error

       } else {

           parmc.course = parm.course;           // set course that was configured in Lottery (empty, course name, or -ALL-)
       }
   }

      
   //
   //  If still ok to proceed, then process according to the lottery type (Proshop, Random or Weighted)
   //
   if (ok == true) {       // if lottery still exist (if still ok to proceed)

      //
      //  New as of 11/18/2010 - log the number of requests for this lottery along with the number of available tee times (for reporting)
      //
      logLottStats(parm, con);


      if (type.equals( "Proshop" )) {       // if proshop to process the requests manually

         errorMsg = "Error in Common_Lott processLott (type=Proshop): ";

         //**************************************************************************************
         //  Type = Proshop (Proshop will manually assign the times via _mlottery)
         //
         //  Set the type and change the state of each request to tell Proshop_mlottery
         //  they are ready for processing. State 1 = 'Processed, Not Assigned'
         //                                 State 2 = 'Processed and Assigned, Not Approved'
         //**************************************************************************************
         //
         try {

            PreparedStatement pstmt4 = con.prepareStatement (
                "UPDATE lreqs3 SET type = ?, state = 2 WHERE name = ? AND date = ?");

            pstmt4.clearParameters();        // clear the parms
            pstmt4.setString(1, type);
            pstmt4.setString(2, name);
            pstmt4.setLong(3, date);
            pstmt4.executeUpdate();

            pstmt4.close();

         }
         catch (Exception e2) {
            //
            //  save error message in /v_x/error.txt
            //
            errorMsg = errorMsg + "Exception= " +e2.getMessage();                  // build error msg

            logError(errorMsg);                                       // log it
         }

      } else {        // Weighted or Random

         //**************************************************************************************
         //  Type = Weighted or Random
         //
         //  Setup the tee time arrays for processing below
         //**************************************************************************************
         //
         try {

             /*
            //
            //  Now setup the arrays for each course
            //
            for (i=0; i < courseCount; i++) {        // do all courses

               errorMsg = "Error in Common_Lott processLott - buildArrays (type=Weighted or Random): ";

               parmc = parmcA[i];                    // get parm block for course

               parmc.course = courseA[i];            // get course name

               buildArrays(con, parm, parmc);        // build arrays

               errorMsg = "Error in Common_Lott processLott - order requests (type=Weighted or Random): ";

               //
               //  Now put the requests in the arrays in the order to process
               //
               if (type.equals( "Random" )) {                   // Random Lottery ?

                  orderReqsRan(con, parm, parmc);               // put requests in order

               } else {

                  if (type.equals( "WeightedBR" )) {             // Weighted By Rounds

                     orderReqsWBR(con, parm, parmc);             // put requests in order

                  } else {                                       // Weighted By Proximity

                     orderReqsWBP(con, parm, parmc);             // put requests in order
                  }
               }

            }          // end of DO ALL courses loop
              */
             
            errorMsg = "Error in Common_Lott processLott - buildArrays (type=Weighted or Random): ";

            buildArrays(con, parm, parmc);        // build arrays
             
            errorMsg = "Error in Common_Lott processLott - order requests (type=Weighted or Random): ";

            //
            //  Now put the requests in the arrays in the order to process
            //
            if (type.equals( "Random" )) {                   // Random Lottery ?

               orderReqsRan(con, parm, parmc);               // put requests in order

            } else {

               if (type.equals( "WeightedBR" )) {             // Weighted By Rounds

                  orderReqsWBR(con, parm, parmc);             // put requests in order

               } else {                                       // Weighted By Proximity

                  orderReqsWBP(con, parm, parmc);             // put requests in order
               }
            }


            i = 0;     // reset

            errorMsg = "Error in Common_Lott processLott (type=Random or Weighted, set state): ";

            //
            //  set the state to 'Processed, Not Assigned (1)' and init the assigned time for all requests
            //
            PreparedStatement pstmt7 = con.prepareStatement (
                "UPDATE lreqs3 SET type = ?, state = 1, atime1 = 0, atime2 = 0, atime3 = 0, atime4 = 0, atime5 = 0 " +
                "WHERE name = ? AND date = ?");

            pstmt7.clearParameters();        // clear the parms
            pstmt7.setString(1, type);
            pstmt7.setString(2, name);
            pstmt7.setLong(3, date);

            pstmt7.executeUpdate();

            pstmt7.close();

            errorMsg = "Error in Common_Lott processLott (type=Random or Weighted, assign times): ";

            //**************************************************************************************
            //  Type = Random or Weighted
            //
            //  Assign Times - process the requests for each course in the order listed
            //
            //**************************************************************************************
            //
            /*
            for (index=0; index < courseCount; index++) {     // do all courses

               parmc = parmcA[index];                         // get parm block for course

               assignTime(con, parm, parmc);                  // assign a tee time for each request
            }
             */
            assignTime(con, parm, parmc);                  // assign a tee time for each request


            //*********************************************************************************
            //
            //  The requests for all courses (if more than one) have been processed and possibly assigned.
            //
            //  Now check for any requests that didn't get assigned and try other courses if possible.
            //
            //*********************************************************************************
            //
            /*
            if (courseCount > 1) {                      // if more than one course specified for this lottery

               assignTime2(con, parm, parmcA, courseCount);  // try to assign a tee time for each remaining request
            }
             */


            errorMsg = "Error in Common_Lott processLott (type=Random or Weighted, move requests): ";

            //
            //*********************************************************************************
            //  now check if proshop wants to pre-approve the assignments before putting them in teecurr
            //*********************************************************************************
            //
            if (approve.equals( "No" )) {

               //
               //  Move the requests that have been assigned into teecurr
               //
               for (i=0; i < courseCount; i++) {        // do all courses

                  course = courseA[i];                  // get course name

                  moveReqs(name, date, course, "lottautoprocess", true, con);    // move the requests for this course
               }
            }

         }
         catch (Exception e2) {
            //
            //  save error message in /v_x/error.txt
            //
            errorMsg = errorMsg + "Exception2= " +e2.getMessage();                  // build error msg

            logError(errorMsg);                                       // log it
         }

      }         // end of IF type = proshop, random or weighted

   }            // end of IF ok
   //
   //  remove the lottery from the active queue - done processing
   //
   try {

      PreparedStatement pstmt10 = con.prepareStatement (
               "DELETE FROM actlott3 WHERE name = ? AND date = ?");

      pstmt10.clearParameters();               // clear the parms
      pstmt10.setString(1, name);
      pstmt10.setLong(2, date);
      pstmt10.executeUpdate();

      pstmt10.close();
   }
   catch (Exception e3) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = "Error in Common_Lott processLott (delete actlott3): ";
      errorMsg = errorMsg + e3.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

   //
   //  Log the start of this lottery processing event (to determine how long it takes and if we are doing this on time)
   //
   Utilities.logDebug("BP", "Lottery processing ENDED for " +club+ " and lottery " +name+ ".");                                   
   

 }  // end of processLott

 
 
 // **********************************************************************************
 //  log some lottery stats - number of requests and number of available tee times
 // **********************************************************************************

 public static void logLottStats(parmLott parm, Connection con) {


   PreparedStatement pstmtd2 = null;

   int reqs = 0;
   int teetimes = 0;

   String errorMsg = "Error in Common_Lott logLottStats (getting request count): ";


   try {

      //
      // calculate the number of requests (total tee times requested)
      //
      pstmtd2 = con.prepareStatement ("" +
              "SELECT SUM(groups) FROM lreqs3 WHERE name = ? AND date = ?;");

      pstmtd2.clearParameters();
      pstmtd2.setString(1, parm.lottName);
      pstmtd2.setLong(2, parm.date);

      ResultSet rs = pstmtd2.executeQuery();

      if ( rs.next() ) {

          reqs = rs.getInt(1);  // get total number of tee times requested for this lottery and date
      }

      pstmtd2.close();

      errorMsg = "Error in Common_Lott logLottStats (getting tee time count): ";

      //
      //  Now determine the total number of tee times available for this lottery
      //
      if (parm.sfb.equals( "Both" )) {

         if (parm.course.equals( "" ) || parm.course.equals( "-ALL-" )) {

            pstmtd2 = con.prepareStatement (
               "SELECT COUNT(*) " +
               "FROM teecurr2 WHERE date = ? AND time >= ? AND time <= ? AND fb < 2 AND " +
               "event = '' AND blocker = ''");

         } else {

            pstmtd2 = con.prepareStatement (
               "SELECT COUNT(*) " +
               "FROM teecurr2 WHERE date = ? AND time >= ? AND time <= ? AND fb < 2 AND " +
               "event = '' AND blocker = '' AND courseName = ?");
         }

         pstmtd2.clearParameters();        // clear the parms
         pstmtd2.setLong(1, parm.date);
         pstmtd2.setInt(2, parm.stime);
         pstmtd2.setInt(3, parm.etime);

         if (!parm.course.equals( "" ) && !parm.course.equals( "-ALL-" )) {

            pstmtd2.setString(4, parm.course);
         }

         rs = pstmtd2.executeQuery();

         if (rs.next()) {

            teetimes = rs.getInt(1);        // get the number of available tee times
         }
         pstmtd2.close();

      } else {        // single tees

         int tfb = 0;         // default = Front

         if (parm.sfb.equals( "Back" )) {

            tfb = 1;         // back tee
         }

         if (parm.course.equals( "" ) || parm.course.equals( "-ALL-" )) {

            pstmtd2 = con.prepareStatement (
               "SELECT COUNT(*) " +
               "FROM teecurr2 WHERE date = ? AND fb = ? AND time >= ? AND time <= ? AND " +
               "event = '' AND blocker = ''");

         } else {

            pstmtd2 = con.prepareStatement (
               "SELECT COUNT(*) " +
               "FROM teecurr2 WHERE date = ? AND fb = ? AND time >= ? AND time <= ? AND " +
               "event = '' AND blocker = '' AND courseName = ?");
         }

         pstmtd2.clearParameters();        // clear the parms
         pstmtd2.setLong(1, parm.date);
         pstmtd2.setInt(2, tfb);
         pstmtd2.setInt(3, parm.stime);
         pstmtd2.setInt(4, parm.etime);

         if (!parm.course.equals( "" ) && !parm.course.equals( "-ALL-" )) {

            pstmtd2.setString(5, parm.course);
         }

         rs = pstmtd2.executeQuery();

         if (rs.next()) {

            teetimes = rs.getInt(1);        // get the number of available tee times
         }
         pstmtd2.close();
      }


      errorMsg = "Error in Common_Lott logLottStats (logging stats): ";

      //
      //  log this info
      //
      pstmtd2 = con.prepareStatement (
              "INSERT INTO lott_stats (lott_name, date, courseName, requests, teetimes) " +
              "VALUES (?, ?, ?, ?, ?)");

      pstmtd2.clearParameters();
      pstmtd2.setString(1, parm.lottName);
      pstmtd2.setLong(2, parm.date);
      pstmtd2.setString(3, parm.course);
      pstmtd2.setInt(4, reqs);
      pstmtd2.setInt(5, teetimes);

      pstmtd2.executeUpdate();

      pstmtd2.close();

   }
   catch (Exception e1) {

      errorMsg = errorMsg + e1.getMessage();
      logError(errorMsg);
   }

 }   // end of logLottStats



 //************************************************************************
 //  buildParmC - build a parm block to store requests and tee times for use in processLott
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = the lottery parameter block
 //               con    = db connection
 //               parmc  = lottery parm block for this course
 //************************************************************************

 public static parmLottC buildParmC(Connection con, parmLott parm, parmLottC parmc) {

   PreparedStatement pstmt5 = null;
   ResultSet rs = null;

   int req_count = 0;
   int tee_count = 0;
   int tfb = 0;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";

   String errorMsg = "Error in Common_Lott buildParmC: ";
   String sql = "";
   String blockerFilter = "AND event = '' AND blocker = ''";  // filter out blocked tee times and event times (old way) 
   
   //if (parm.club.equals("gallerygolf")) {  // do this for all clubs as of 9/21/13
      
      blockerFilter = "";     // DO NOT filter out blocked tee times or event times (they will be flagged as busy in buildArrays)
   //}


   //
   //   Locate all available tee times to be used in this lottery on the course(s) specified in the lottery.
   //
   //   NOTE:  include any tee times that already have players so that the assign process will still find
   //          a matching tee time.  This is for the case where a pro pre-books a tee time, but other members
   //          have already submitted a lottery request for that time.
   //
   try {
      if (parm.sfb.equals( "Both" )) {
          
         if (!parm.course.equals("") && !parm.course.equals("-ALL-")) {
                 
             sql = "SELECT COUNT(*) " +
                    "FROM teecurr2 WHERE date = ? AND time >= ? AND time <= ? AND fb < 2 AND courseName = ? " +blockerFilter+ " " +
                    "ORDER BY time, fb";        
             
         } else {
             
             sql = "SELECT COUNT(*) " +
                    "FROM teecurr2 WHERE date = ? AND time >= ? AND time <= ? AND fb < 2 " +blockerFilter+ " " +
                    "ORDER BY time, fb";        
         }
         
      } else {

         tfb = 0;         // default = Front

         if (parm.sfb.equals( "Back" )) {

            tfb = 1;         // back tee
         }

         if (!parm.course.equals("") && !parm.course.equals("-ALL-")) {
                 
             sql = "SELECT COUNT(*) " +
                    "FROM teecurr2 WHERE date = ? AND time >= ? AND time <= ? AND fb = ? AND courseName = ? " +blockerFilter+ " " +
                    "ORDER BY time, fb";        
             
         } else {
             
             sql = "SELECT COUNT(*) " +
                    "FROM teecurr2 WHERE date = ? AND time >= ? AND time <= ? AND fb = ? " +blockerFilter+ " " +
                    "ORDER BY time, fb";        
         }
      }

     pstmt5 = con.prepareStatement (sql);

     pstmt5.clearParameters();        // clear the parms
     pstmt5.setLong(1, parm.date);
     pstmt5.setInt(2, parm.stime);
     pstmt5.setInt(3, parm.etime);

     if (parm.sfb.equals( "Both" )) {
          
         if (!parm.course.equals("") && !parm.course.equals("-ALL-")) {

             pstmt5.setString(4, parm.course);
         }
         
     } else {
   
         pstmt5.setInt(4, tfb);
         
         if (!parm.course.equals("") && !parm.course.equals("-ALL-")) {

             pstmt5.setString(5, parm.course);
         }
     }

     rs = pstmt5.executeQuery();      // execute the prepared stmt

     if (rs.next()) {

        tee_count = rs.getInt(1);        // get number of tee times
     }

     
     //
     //  Now locate the number of requests that are queued for this lottery
     //
     sql = "SELECT COUNT(*) " +
           "FROM lreqs3 " +
           "WHERE name = ? AND date = ?";
          // "WHERE name = ? AND date = ? AND courseName = ?";
      
     pstmt5 = con.prepareStatement (sql);

     pstmt5.clearParameters();        // clear the parms
     pstmt5.setString(1, parm.lottName);
     pstmt5.setLong(2, parm.date);
     //pstmt5.setString(3, parm.course);

     rs = pstmt5.executeQuery();      // execute the prepared stmt

     if (rs.next()) {

        req_count = rs.getInt(1);        // get number of requests
     }

     pstmt5.close();
     
   }
   catch (Exception e3) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e3.getMessage();                                 // build error msg
      logError(errorMsg);                                                    // log it
      
      parmc = null;
   }
   

   //
   //   Build the parm block using the counts from above
   //
   if (tee_count > 0 && req_count > 0) {
       
       try {
           
          parmc = new parmLottC(req_count, tee_count); 
          
       } catch (Exception e4) {
           
          errorMsg = errorMsg + "parmLottC failed.  Error = " + e4.getMessage();   // build error msg
          logError(errorMsg);                                                    // log it
       }
   }
   
   return(parmc);
 }                    // end of buildParmC



 //************************************************************************
 //  buildArrays - build tee time arrays for use in processLott
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = the lottery parameter block
 //               con    = db connection
 //               parmc  = lottery parm block for this course
 //************************************************************************

 public static void buildArrays(Connection con, parmLott parm, parmLottC parmc) {

     
   PreparedStatement pstmt5 = null;
   ResultSet rs = null;

   int tfb = 0;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String blocker = "";
   String event = "";

   String errorMsg = "Error in Common_Lott buildArrays: ";
   String sql = "";


   //
   //   Locate all available tee times to be used in this lottery on the course(s) specified in the lottery.
   //
   //   NOTE:  include any tee times that already have players so that the assign process will still find
   //          a matching tee time.  This is for the case where a pro pre-books a tee time, but other members
   //          have already submitted a lottery request for that time.
   //
   //   ALWAYS get times with events and blockers but flag them as busy!!!  This allows assign1Time to find a matching
   //          tee time.  It will check the next time if busy.
   //
   try {
      if (parm.sfb.equals( "Both" )) {
          
         if (!parm.course.equals("") && !parm.course.equals("-ALL-")) {
                 
             sql = "SELECT time, fb, player1, player2, player3, player4, player5, courseName, blocker, event " +
                    "FROM teecurr2 WHERE date = ? AND time >= ? AND time <= ? AND fb < 2 AND courseName = ? " +
                    "ORDER BY time, fb";        
             
         } else {
             
             sql = "SELECT time, fb, player1, player2, player3, player4, player5, courseName, blocker, event " +
                    "FROM teecurr2 WHERE date = ? AND time >= ? AND time <= ? AND fb < 2 " +
                    "ORDER BY time, courseName, fb";        
         }
         
      } else {

         tfb = 0;         // default = Front

         if (parm.sfb.equals( "Back" )) {

            tfb = 1;         // back tee
         }

         if (!parm.course.equals("") && !parm.course.equals("-ALL-")) {
                 
             sql = "SELECT time, fb, player1, player2, player3, player4, player5, courseName, blocker, event " +
                    "FROM teecurr2 WHERE date = ? AND time >= ? AND time <= ? AND fb = ? AND courseName = ? " +
                    "ORDER BY time, fb";        
             
         } else {
             
             sql = "SELECT time, fb, player1, player2, player3, player4, player5, courseName, blocker, event " +
                    "FROM teecurr2 WHERE date = ? AND time >= ? AND time <= ? AND fb = ? " +
                    "ORDER BY time, courseName, fb";        
         }
      }

     pstmt5 = con.prepareStatement (sql);

     pstmt5.clearParameters();        // clear the parms
     pstmt5.setLong(1, parm.date);
     pstmt5.setInt(2, parm.stime);
     pstmt5.setInt(3, parm.etime);

     if (parm.sfb.equals( "Both" )) {
          
         if (!parm.course.equals("") && !parm.course.equals("-ALL-")) {

             pstmt5.setString(4, parm.course);
         }
         
     } else {
   
         pstmt5.setInt(4, tfb);
         
         if (!parm.course.equals("") && !parm.course.equals("-ALL-")) {

             pstmt5.setString(5, parm.course);
         }
     }

     rs = pstmt5.executeQuery();      // execute the prepared stmt

     int i = 0;

     while (rs.next()) {

        blocker = rs.getString("blocker");        // get blocker for this tee time, if any
        event = rs.getString("event");            // get event for this tee time, if any
        
        //if (blocker.equals("") || parm.club.equals("gallerygolf")) {      //  skip this one if blocker and NOT the Gallery
        
            parmc.timeA[i] = rs.getInt("time");     
            parmc.fbA[i] = rs.getShort("fb");      
            player1 = rs.getString("player1");          
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            parmc.courseA[i] = rs.getString("courseName");

            parmc.busyA[i] = false;

            if (!player1.equals("") || !player2.equals("") || !player3.equals("") || !player4.equals("") || 
                !player5.equals("") || !blocker.equals("") || !event.equals("")) {  // if busy or blocked or an event

               parmc.busyA[i] = true;     // flag this time as busy so it is not assigned
            }

            i++;
        //}
     }

     pstmt5.close();
     
   }
   catch (Exception e3) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e3.getMessage();                                 // build error msg
      logError(errorMsg);                                                    // log it
   }
     
      // old method  -  refer to Common_Lott_Orig
     
 }  // end of buildArrays



 //************************************************************************
 //  orderReqsRan - put lottery reqs in arrays in order for use in processLott
 //
 //    For Random lottery types
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = the lottery parameter block
 //               con    = db connection
 //               parmc  = lottery parm block that holds the requests and the tee time info
 //************************************************************************

 public static void orderReqsRan(Connection con, parmLott parm, parmLottC parmc) {

   ResultSet rs = null;

   int i = 0;

   long id = 0;


   String errorMsg = "Error in Common_Lott orderReqsRan (type=Random, order Reqs): ";

   try {

      String course = parmc.course;           // get course name

      //
      //  Get the lottery requests for the lottery name and date that were passed
      //
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT id " +
         "FROM lreqs3 " +
         "WHERE name = ? AND date = ? " +
         "ORDER BY RAND()");                                  // Random order

         // was:  "WHERE name = ? AND date = ? AND courseName = ? " +
      
      pstmt3.clearParameters();        // clear the parms
      pstmt3.setString(1, parm.lottName);
      pstmt3.setLong(2, parm.date);
      //pstmt3.setString(3, course);
      rs = pstmt3.executeQuery();

      while (rs.next()) {       // get all lottery requests

         id = rs.getLong(1);

         parmc.idA[i] = id;                // set id in array

         i++;
      }

      pstmt3.close();

   }
   catch (Exception e3) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e3.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }
 }  // end of orderReqsRan


 //************************************************************************
 //  orderReqsWBR - put lottery reqs in arrays in order for use in processLott
 //
 //    For 'Weighted By Rounds' lottery types
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = the lottery parameter block
 //               con    = db connection
 //               parmc  = lottery parm block that holds the requests and the tee time info
 //************************************************************************

 public static void orderReqsWBR(Connection con, parmLott parm, parmLottC parmc) {

   ResultSet rs = null;


   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int i4 = 0;
   int weight = 0;
   int req_count = parmc.req_count;

   long id = 0;

   long [] idA = new long [req_count];          // request id array
   int [] wghtA = new int [req_count];          // weight of this request
   int [] playersA = new int [req_count];       // # of players in this request


   String errorMsg = "Error in Common_Lott orderReqsWBR (type=Weighted By Rounds, order Reqs): ";
   String club = "";

   try {
       
       club = con.getCatalog();           // get db (club) name
       
   }
   catch (Exception e) {       
   }


   try {

      //String course = parmc.course;           // get course name

      //
      //  Get the lottery requests for the lottery name and date that were passed
      //
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT id, players " +
         "FROM lreqs3 " +
         "WHERE name = ? AND date = ?");

         // was: "WHERE name = ? AND date = ? AND courseName = ?");

      pstmt3.clearParameters();        // clear the parms
      pstmt3.setString(1, parm.lottName);
      pstmt3.setLong(2, parm.date);
      //pstmt3.setString(3, course);
      rs = pstmt3.executeQuery();

      while (rs.next()) {               // get all lottery requests

         id = rs.getLong(1);
         playersA[i] = rs.getInt(2);        // set # of players in array

         idA[i] = id;                       // set id in array

         //
         //  get the weight value for this entry
         //
         weight = getWeight(con, id, parm.lottName);

         wghtA[i] = weight;                // set weight in array
         
         i++;
      }

      pstmt3.close();
      
      i = 0;

      //
      //  Now move the values from the temp arrays above into the parm block (order by weight)
      //
      while (i < req_count) {

         id = idA[i];             // get an id
         weight = wghtA[i];       // get its weight

         if (id > 0 && playersA[i] > 1) {     // if id exists and more than one player

            i3 = i;               // save for move

            i2 = i + 1;           // start at next spot

            while (i2 < req_count) {                   // compare against the rest

               if (idA[i2] > 0 && playersA[i2] > 1) {    // if id still there & more than one player

                   if (club.equals("westchester")) {
                       
                       if (weight < wghtA[i2]) {      // if this weight is higher - switch to this one

                            id = idA[i2];               // get the id
                            weight = wghtA[i2];         // get its weight

                            i3 = i2;
                       }

                   } else {

                       if (weight > wghtA[i2]) {      // if this weight is lower - switch to this one

                            id = idA[i2];               // get the id
                            weight = wghtA[i2];         // get its weight

                            i3 = i2;
                       }
                   }
                    
               }
               i2++;
            }

            //
            //  i3 points to the lowest weight, i4 is the save index
            //
            parmc.idA[i4] = id;            // set the id
            parmc.wghtA[i4] = weight;      // set its weight

            idA[i3] = 0;                   // remove the id from temp array
            i4++;                          // bump save index

         } else {                          // id already set in real array

            i++;                           // do next id (do not bump i until its id is zero)
         }
      }

      //
      //  Now move any single players from the temp arrays above into the parm block (order by weight)
      //
      i = 0;                      // start at beginning

      while (i < req_count) {

         id = idA[i];             // get an id
         weight = wghtA[i];       // get its weight

         if (id > 0) {     // if id exists

            i3 = i;               // save for move

            i2 = i + 1;           // start at next spot

            while (i2 < req_count) {                   // compare against the rest

               if (idA[i2] > 0) {                // if id still there

                   if (club.equals("westchester")) {
                       
                        if (weight < wghtA[i2]) {      // if this weight is higher - switch to this one

                            id = idA[i2];               // get the id
                            weight = wghtA[i2];         // get its weight

                            i3 = i2;
                        }

                   } else {
                       
                        if (weight > wghtA[i2]) {      // if this weight is lower - switch to this one

                            id = idA[i2];               // get the id
                            weight = wghtA[i2];         // get its weight

                            i3 = i2;
                        }
                   }

               }
               i2++;
            }

            //
            //  i3 points to the lowest weight, i4 is the save index
            //
            parmc.idA[i4] = id;            // set the id
            parmc.wghtA[i4] = weight;      // set its weight

            idA[i3] = 0;                   // remove the id from temp array
            i4++;                          // bump save index

         } else {                          // id already set in real array

            i++;                           // do next id (do not bump i until its id is zero)
         }
      }

   }
   catch (Exception e3) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e3.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }
 }  // end of orderReqsWBR


 //************************************************************************
 //  orderReqsWBP - put lottery reqs in arrays in order for use in processLott
 //
 //    For 'Weighted By Proximity' lottery types
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = the lottery parameter block
 //               con    = db connection
 //               parmc  = lottery parm block that holds the requests and the tee time info
 //************************************************************************

 public static void orderReqsWBP(Connection con, parmLott parm, parmLottC parmc) {

   ResultSet rs = null;


   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int i4 = 0;
   int weight = 0;
   int req_count = parmc.req_count;

   long id = 0;

   long [] idA = new long [req_count];          // request id array
   int [] wghtA = new int [req_count];          // weight of this request
   int [] playersA = new int [req_count];       // # of players in this request



   String errorMsg = "Error in Common_Lott orderReqsWBP (type=Weighted By Proximity, order Reqs): ";

   try {

      String course = parmc.course;           // get course name

      //
      //  Get the lottery requests for the lottery name and date that were passed
      //
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT id, players " +
         "FROM lreqs3 " +
         "WHERE name = ? AND date = ?");

         // was: "WHERE name = ? AND date = ? AND courseName = ?");

      pstmt3.clearParameters();        // clear the parms
      pstmt3.setString(1, parm.lottName);
      pstmt3.setLong(2, parm.date);
      rs = pstmt3.executeQuery();

      while (rs.next() && i < req_count) {              // get all lottery requests up to 100

         id = rs.getLong(1);
         playersA[i] = rs.getInt(2);        // set # of players in array

         idA[i] = id;                       // set id in array

         //
         //  get the weight value for this entry
         //
         weight = getWeightP(con, id, parm.lottName);

         wghtA[i] = weight;                // set weight in array

         i++;
      }

      pstmt3.close();

      i = 0;

      //
      //  Now move the values from the temp arrays above into the parm block (order by weight)
      //
      //    The weight values for this type are 'the average # of minutes previous requests were assigned within requested time'
      //
      while (i < req_count) {

         id = idA[i];             // get an id
         weight = wghtA[i];       // get its weight

         if (id > 0 && playersA[i] > 1) {     // if id exists and more than one player

            i3 = i;               // save for move

            i2 = i + 1;           // start at next spot

            while (i2 < req_count) {                   // compare against the rest

               if (idA[i2] > 0 && playersA[i2] > 1) {    // if id still there & more than one player

                  if (weight < wghtA[i2]) {      // if this weight is higher - switch to this one (larger value gets priority)

                     id = idA[i2];               // get the id
                     weight = wghtA[i2];         // get its weight

                     i3 = i2;
                  }
               }
               i2++;
            }

            //
            //  i3 points to the lowest weight, i4 is the save index
            //
            parmc.idA[i4] = id;            // set the id
            parmc.wghtA[i4] = weight;      // set its weight

            idA[i3] = 0;                   // remove the id from temp array
            i4++;                          // bump save index

         } else {                          // id already set in real array

            i++;                           // do next id (do not bump i until its id is zero)
         }
      }

      //
      //  Now move any single players from the temp arrays above into the parm block (order by weight)
      //
      i = 0;                      // start at beginning

      while (i < req_count) {

         id = idA[i];             // get an id
         weight = wghtA[i];       // get its weight

         if (id > 0) {     // if id exists

            i3 = i;               // save for move

            i2 = i + 1;           // start at next spot

            while (i2 < req_count) {                   // compare against the rest

               if (idA[i2] > 0) {                // if id still there

                  if (weight < wghtA[i2]) {      // if this weight is higher - switch to this one (larger value gets priority)

                     id = idA[i2];               // get the id
                     weight = wghtA[i2];         // get its weight

                     i3 = i2;
                  }
               }
               i2++;
            }

            //
            //  i3 points to the lowest weight, i4 is the save index
            //
            parmc.idA[i4] = id;            // set the id
            parmc.wghtA[i4] = weight;      // set its weight

            idA[i3] = 0;                   // remove the id from temp array
            i4++;                          // bump save index

         } else {                          // id already set in real array

            i++;                           // do next id (do not bump i until its id is zero)
         }
      }

   }
   catch (Exception e3) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e3.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }
 }  // end of orderReqsWBP



 //************************************************************************
 //  assignTime - assign a Tee Time to each of the lottery requests 
 //
 //    For Random and Weighted lottery types
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = the lottery parameter block
 //               con    = db connection
 //               parmc  = lottery parm block that holds the requests and the tee time info
 //************************************************************************

 public static void assignTime(Connection con, parmLott parm, parmLottC parmc) {


   int i = 0;
   int i2 = 0;
   int i4 = 0;
   int weight = 0;
   int ttime = 0;
   int req_count = parmc.req_count;
   int tee_count = parmc.tee_count;
   short tfb = 0;
   long id = 0;
   String course = "";

   int fail = 0;   // init the assign indicator

   String errorMsg = "Error in Common_Lott assignTime: ";
   
   
   // ******************* TEMPORARY DEBUG - DUMP OUT THE PARM BLOCKS
   /*
   errorMsg = "Common_Lott.assignTime info: parmc req_count=" +req_count+ ", tee_count=" +tee_count+ ", Reqs=";
   
   for (i=0; i < req_count; i++) {   
       errorMsg += "(" +parmc.idA[i]+ "," +parmc.wghtA[i]+ ")";
   }
   errorMsg += ", Tee Times=";
   for (i=0; i < tee_count; i++) {   
       errorMsg += "(" +parmc.timeA[i]+ "," +parmc.fbA[i]+ "," +parmc.courseA[i]+ ")";
   }
   Utilities.logDebug("BP", errorMsg);                                   
   * 
   */
   //****************************************************************

   
   
   //
   //  Process each request for this lottery
   //
   for (i=0; i < req_count; i++) {      

      id = parmc.idA[i];               // get an id
      weight = parmc.wghtA[i];         // get its weight

      if (id > 0) {                     // if exists

         fail = assign1Time(con, parm, parmc, id, weight);          // try to assign a time

         //
         //  If req failed, then we must update the weight (Weighted type only) and save the fail code
         //
         if (fail > 0) {     // always set failCode - weight won't matter if not weighted lottery

            try {

               PreparedStatement pstmtf = con.prepareStatement (
                   "UPDATE lreqs3 SET weight = ?, fail_code = ? WHERE id = ?");

               pstmtf.clearParameters();        // clear the parms
               pstmtf.setInt(1, weight);
               pstmtf.setInt(2, fail);
               pstmtf.setLong(3, id);
               pstmtf.executeUpdate();

               pstmtf.close();

            }
            catch (Exception e3) {
               //
               //  save error message in /v_x/error.txt
               //
               errorMsg = errorMsg + e3.getMessage();                                 // build error msg
               logError(errorMsg);                                       // log it
            }
         }

      }
   
        // ******************* TEMPORARY DEBUG - DUMP OUT THE Tee Time Assignments
        /*
        errorMsg = "assignTime Tee Times = ";

        for (int i3=0; i3 < tee_count; i3++) {   
            errorMsg += "(" +parmc.timeA[i3]+ "," +parmc.courseA[i3]+ "," +parmc.id2A[i3]+ ")";
        }

        Utilities.logDebug("BP", errorMsg);            
        */
        //****************************************************************
      
   }
   

   //*****************************************************************************
   //  Lottery has been processed - set the assigned times in the requests 
   //*****************************************************************************
   //
   i = 0;
                                         
   loop5:
   while (i < tee_count) {           // one entry per tee time

      ttime = parmc.timeA[i];        // get tee time from array

      if (ttime == 0) {              // if reached end of tee times (last entry is zero)

         break loop5;                // exit loop
      }

      course = parmc.courseA[i];     // get the course for this time slot
      tfb = parmc.fbA[i];            // get the f/b for this time slot
      id = parmc.id2A[i];            // get the request id assigned to this time slot
      weight = parmc.wght2A[i];      // get the weight for this time slot

      if (id != 0) {                 // if assigned

         parmc.id2A[i] = 999999;     // mark it done
         i2 = i + 1;                 // get next slot
         i4 = 1;
         parmc.atimeA[0] = ttime;    // init time values - set 1st one
         parmc.atimeA[1] = 0;
         parmc.atimeA[2] = 0;
         parmc.atimeA[3] = 0;
         parmc.atimeA[4] = 0;
         parmc.afbA[0] = tfb;      
         parmc.afbA[1] = 0;  
         parmc.afbA[2] = 0;  
         parmc.afbA[3] = 0;  
         parmc.afbA[4] = 0;  

         while (i2 < tee_count && i4 < 5) {           // look for matching id's (more than 1 tee time)

            if (parmc.id2A[i2] == id) {               // if match

               parmc.atimeA[i4] = parmc.timeA[i2];    // save time value
               parmc.afbA[i4] = parmc.fbA[i2];        // save f/b value
               parmc.id2A[i2] = 999999;               // mark it done
               i4++;
            }
            i2++;                                   // next
         }
         //
         //  set the assigned time and f/b in the req for later processing
         //
         //  State = 2 (Processed & Assigned)
         //
         errorMsg = "Error in Common_Lott assignTime (type=Random/Weighted, set assigned): ";

         try {

            PreparedStatement pstmt9 = con.prepareStatement (
                "UPDATE lreqs3 SET courseName = ?, type = ?, state = 2, atime1 = ?, atime2 = ?, atime3 = ?, atime4 = ?, " +
                "atime5 = ?, afb = ?, weight = ?, afb2 = ?, afb3 = ?, afb4 = ?, afb5 = ?, fail_code = 0 " +
                "WHERE id = ?");

            pstmt9.clearParameters();       
            pstmt9.setString(1, course);           // set course in case it changed 
            pstmt9.setString(2, parm.ltype);       // weighted or random
            pstmt9.setInt(3, parmc.atimeA[0]);
            pstmt9.setInt(4, parmc.atimeA[1]);
            pstmt9.setInt(5, parmc.atimeA[2]);
            pstmt9.setInt(6, parmc.atimeA[3]);
            pstmt9.setInt(7, parmc.atimeA[4]);
            pstmt9.setShort(8, parmc.afbA[0]);
            pstmt9.setInt(9, weight);
            pstmt9.setShort(10, parmc.afbA[1]);
            pstmt9.setShort(11, parmc.afbA[2]);
            pstmt9.setShort(12, parmc.afbA[3]);
            pstmt9.setShort(13, parmc.afbA[4]);
            pstmt9.setLong(14, id);

            pstmt9.executeUpdate();

            pstmt9.close();

         }
         catch (Exception e3) {
            //
            //  save error message in /v_x/error.txt
            //
            errorMsg = errorMsg + e3.getMessage();                                 // build error msg
            logError(errorMsg);                                       // log it
         }
      }
      i++;          // next time slot

   }                // end of loop5 WHILE

 }  // end of assignTime


 //************************************************************************
 //  assign1Time - assign a Tee Time to the lottery request passed
 //
 //    For Random and Weighted lottery types
 //
 //   called by:  assignTime
 //
 //       parms:  parm   = the lottery parameter block
 //               con    = db connection
 //               parmc  = lottery parm block that holds the requests and the tee time info
 //               id     = lottery request id
 //               weight = lottery request's weight (if any)
 //************************************************************************

 public static int assign1Time(Connection con, parmLott parm, parmLottC parmc, long id, int weight) {

   ResultSet rs = null;


   int time = 0;
   int time2 = 0;
   int ttime = 0;
   int before = 0;
   int after = 0;
   int groups = 0;
   int players = 0;
   int full = 0;
   int stat = 0;
   int astat = 0;
   int bstat = 0;
   int toggle = 0;
   int index = 0;
   int grps = 0;
   int error = 0;
   int count = 0;
   int i = 0;
   int i2 = 0;
   int i3 = 0;

   int failCode = 0;                    // status to return - init to success
   int req_count = parmc.req_count;     // # of requests
   int tee_count = parmc.tee_count;     // # of tee times available for the lottery requests

   short tfb = 0;
//   short save_fb = 0;

   boolean fail = true;               // init to failed status
   boolean restricted = false;
   boolean found = false;
   boolean useBothFB = false;         // custom option to use both F & B tee times
   
   if ((parm.club.equals("esterocc") || parm.club.equals("plantationgcc")) && parm.sfb.equals( "Both" )) useBothFB = true;   // do this for Estero if lottery defined for both tees
   

   String p5 = "";
   String course = "";
   String req_course = "";
   String current_course = "";
   String save_course = "";            

   String errorMsg = "Error in Common_Lott assign1Time for club " +parm.club+ ", Lreq Id " +id+ ": ";


   try {

      //
      //  get the request info for id passed
      //
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT time, minsbefore, minsafter, " +
         "fb, groups, p5, players, checkothers, courseReq " +
         "FROM lreqs3 " +
         "WHERE id = ?");

      pstmt3.clearParameters();
      pstmt3.setLong(1, id);
      rs = pstmt3.executeQuery();

      if (rs.next()) {

         time = rs.getInt("time");                       
         before = rs.getInt("minsbefore");
         after = rs.getInt("minsafter");
         parm.rfb = rs.getShort("fb");
         groups = rs.getInt("groups");
         p5 = rs.getString("p5");
         players = rs.getInt("players");
         parm.checkothers = rs.getInt("checkothers");      // check other courses option
         req_course = rs.getString("courseReq");           // get course that was requested
         
         parm.courseReq = req_course;                      // save for later

         //
         //  determine earliest and latest times to accept
         //
         parm.ftime = time;   // init
         parm.ltime = time;

         if (before > 0) {

            parm.ftime = getFirstTime(time, before);    // get earliest time for this request
         }

         if (after > 0) {

            parm.ltime = getLastTime(time, after);     // get latest time for this request
         }


         //
         // determine how many players constitute a full group
         //
         if (p5.equals( "Yes" )) {       // determine # of players for this group
            full = 5;
         } else {
            full = 4;
         }

         //
         //  scan the tee time array for a matching time
         //
         i = 0;
         time2 = time;                // preserve original time requested
         stat = 0;                    // init status = ok
         astat = 0;                   // init after times status = ok
         bstat = 0;                   // init before times status = ok
         toggle = 0;                  // init before/after toggle
         parm.fb_after = parm.rfb;    // init f/b for after times
         parm.fb_before = parm.rfb;   // init f/b for before times
         // fail = false;             // changed to init fail to true and set to false only if assigned
         failCode = 1;                // default failure reason = time not available

         //
         //  Process this request
         //
         loop1:
         while (i < tee_count) {          // loop through arrays for a matching tee time (what was requested)

            ttime = parmc.timeA[i];       // get tee time from array
            tfb = parmc.fbA[i];           // get associated f/b
            
            if (ttime > 0) {
               course = parmc.courseA[i];    // get course name for this tee time (in last entry ttime is zero and course is null)
            } else {
                course = "";
            }

            if (ttime == time2 && tfb == parm.rfb && course.equals(req_course)) {   // if matching time/fb/course found       

               grps = groups;         // # of groups requested - temp for work below
               i3 = i;                // temp index for work below
               parm.beforei = i;      // init before and after indexes
               parm.afteri = i;
               i2 = 999;              // init index save
               restricted = false;    // init restricted flag
               current_course = course; 

               //
               //  Time found that matches the requested time - check if available times for all groups
               //
               while (grps > 0) {

                  if (astat == 0 || bstat == 0) {   // if before or after still open to check

                     if (parmc.id2A[i3] == 0 && parmc.busyA[i3] == false && parmc.timeA[i3] > 0 && restricted == false) {  // if spot open (check associated entry in Id Array)

                        error = 0;                     // init

                        if (i2 == 999) {

                           i2 = i3;                                // save first open spot
                           current_course = parmc.courseA[i3];      // get course for this tee time - make sure others match
                        }

                        i3++;                 // next spot
                        grps--;               // next grp

                        if (grps > 0) {          // if still more groups
                            
                            found = false;
                            
                            loop1n:
                            while (i3 < tee_count) {        // search for next matching time (matches fb and course)

                               tfb = parmc.fbA[i3];          // get associated f/b of next slot
                               course = parmc.courseA[i3];   // get course

                               if ((tfb == parm.rfb || useBothFB == true) && course.equals(current_course)) {  // if matching f/b & course
                                   
                                   found = true;
                                   break loop1n;             // done - exit while loop
                               }

                               i3++;                      // next spot
                            }
                            
                            if (found == false) {         // if not found

                                 error = 1;              // error - not enough tee times for this group
                                 i2 = 999;
                            }
                            
                        } else {
                           
                           i3--;      // Done - back up i3 to point at good entry so we can check for restrictions (added 6/03/13 to address problem at Gallery with a restriction on one course)
                        }             // end of IF grps

                     } else {

                        error = 1;                        // indicate error if not available
                        i2 = 999;                         // reset first available index

                        if (restricted == true) {
                           failCode = 2;                  // reason = member restricted
                        }

                     }

                     if (error > 0) {                     // if error - try another time slot

                        if (astat == 0 || bstat == 0) {   // if before or after still open to check

                           if (toggle == 0 && astat ==0) {     // check after time ?

                              toggle = 1;                    // check before time next

                              //if (astat == 0) {           // if after still open to check

                                 parm.groups = groups;        // save current parms

                                 astat = checkAfter(parm, parmc, useBothFB);   // go check for an after time

                                 if (astat == 1 && bstat == 0) {     // if 'after' failed and 'before' ok to check

                                    bstat = checkBefore(parm, parmc, useBothFB);    // go check for a before time
                                 }

                                 i3 = parm.i3;
                                 grps = parm.grps;
                              //}                    // end of IF astat = 0

                           } else {                // check 'after' times

                              toggle = 0;                 // check after time next

                              if (bstat == 0) {           // if before still open to check

                                 parm.groups = groups;        // save current parms

                                 bstat = checkBefore(parm, parmc, useBothFB);    // go check for a before time

                                 if (bstat == 1) {                   // if 'before' failed

                                    if (astat == 0) {                   // if 'after' ok to check

                                       astat = checkAfter(parm, parmc, useBothFB);    // go check for an after time

                                    } else {

                                       stat = 1;                        // done checking - failed for this request
                                       parm.grps = 0;                   // done with this one
                                    }
                                 }

                                 i3 = parm.i3;
                                 grps = parm.grps;
                              }                    // end of IF astat = 0

                           }            // end of IF toggle (before or after times check)

                           //
                           //  Before and/or After times checked.  If we found some, then make sure the players
                           //  are allowed to play during this time(s).
                           //
                           if (stat == 0 && (astat == 0 || bstat == 0)) {   // if we haven't failed yet

                              parm.lottid = id;                        // set id

                              getParmValues(parm, con);         // go set parm values needed for restriction processing

                              parm.time = parmc.timeA[i3];      // override time and fb
                              parm.fb = parm.rfb;
                              
                              if (useBothFB == true) parm.fb = tfb;    // use the tee time fb if it could be different than requested fb
                              
                              parm.ind = 1;                     // index not pertinent for this - use 1 to get through restrictions
                              save_course = parm.course;        // save the lottery course
                              parm.course = parmc.courseA[i3];  // use the tee time's course for restriction processing

                              restricted = checkRests(0, parm, con, true);  // check restrictions (returns true if a member is restricted)
                                                                                  // if true, forces a new search (above)
                              parm.course = save_course;              // restore the lottery course

                              if (restricted == true) {

                                 grps = groups;          // start over if restricted (the restricted=true will force the logic above to keep trying)
       
                                 // Utilities.logDebug("BP", "Lottery Req Failed - Member Restricted. Req Id = " +id+ ". Error = " +parm.error_hdr+ ". Msg = " +parm.error_msg+ ".");                                   
                              }
                           }

                        } else {        // before and after times already checked

                           stat = 1;    // done checking - failed for this request
                           grps = 0;    // terminate this loop
                        }               // end of IF stat or bstat

                     } else {

                        //
                        //  Matching time is available - must check for restrictions as we have not gone through restriction check above
                        //
                        parm.lottid = id;                        // set id

                        getParmValues(parm, con);           // go set parm values needed for restriction processing

                        save_course = parm.course;          // save the lottery course
                        parm.time = parmc.timeA[i3];        // override time, course and fb
                        parm.course = parmc.courseA[i3];    // get the tee time's course for restriction processing  
                        parm.fb = parm.rfb;
                              
                        if (useBothFB == true) parm.fb = tfb;    // use the tee time fb if it could be different than requested fb
                              
                        parm.ind = 1;                       // index not pertinent for this - use 1 to get through restrictions

                        restricted = checkRests(0, parm, con, true);  // check restrictions (returns true if a member is restricted)
                                                                            // if true, forces a new search (above)
                        parm.course = save_course;             // restore the lottery course

                        if (restricted == true) {

                           grps = groups;          // start over if restricted (the restricted=true will force the logic above to keep trying)
       
                           // Utilities.logDebug("BP", "Lottery Req Failed - Member Restricted. Req Id = " +id+ ". Error = " +parm.error_hdr+ ". Msg = " +parm.error_msg+ ".");                                   
                        }

                     }                  // end of IF spot open (if ok for this group)

                  } else {              // before and after times already checked

                     stat = 1;          // done checking - failed for this request
                     grps = 0;          // terminate this loop
                  }                     // end of IF before or after still ok

               }      // end of WHILE grps


               if (stat == 0) {      // if spot(s) open

                  //**************************************************************************
                  //  Found spot(s) for the lottery request - save the req id in the array
                  //**************************************************************************
                  //
                  grps = groups;                       // restore # of groups requested

                  tfb = parmc.fbA[i2];                 // get f/b and course of the first selected tee time    
                  course = parmc.courseA[i2];     

                  while (grps > 0 && i2 < tee_count) {       // make sure we don't go past end of array     

                     if ((tfb == parmc.fbA[i2] || useBothFB == true) && course.equals(parmc.courseA[i2])) { // if matching tee time found  

                        if (players > full) {           // determine # of players for this group
                           count = full;                // 4 or 5
                           players = players - full;    // new count
                        } else {
                           count = players;
                           players = 0;
                        }

                                                        // ids are put in this array so they have the same index as the tee time!
                        parmc.id2A[i2] = id;            // put this id in array to reserve this spot
                        parmc.players2A[i2] = count;    // set # of players in this tee time
                        parmc.wght2A[i2] = weight;      // set weight for this tee time
                        weight = 9999;                  // weight for susequent grps (only 1st has weight)
                        grps--;
                     }
                     i2++;
                  }            // end of WHILE grps

                  fail = false;               // success!!!
                  break loop1;                // done with this request - exit while loop

               } else {                       // request failed - try other tees?

                  fail = true;              // this req failed
                  break loop1;              // done with this request - exit while loop
               }
               
            }             // end of IF time/fb matches

            if (i == 999) {        // start over?
               i = 0;
            } else {
               i++;                   // check next entry in Time Array
            }
         }           // end of loop1 WHILE - still entries in array

      }     // end of IF lottery request found

      pstmt3.close();

   }
   catch (Exception e3) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + " i=" +i+ ", i2=" +i2+ ", i3=" +i3+ ", Error= " + e3.getMessage() + ", " + e3.toString();                    // build error msg
      logError(errorMsg);                                       // log it
      fail = true;                                              // this req failed
      failCode = 99;                                            // indicate System Problem
   }

   if (fail == false) failCode = 0;              // make sure the failure reason code is zero if assign was successful

   if (fail == true && failCode == 0) failCode = 9;   // failed but no reason code - use 'Other'
   
   if (failCode == 2 && (parm.club.equals("mesaverdecc") || parm.club.equals("gallerygolf"))) {      // if failed because of restrictions and Mesa verde - log it for debug
       
       Utilities.logDebug("BP", "Lottery Req Failed - Member Restricted (" +parm.club+ "). Req Id = " +id+ ". Error = " +parm.error_hdr+ ". Msg = " +parm.error_msg+ ".");                                   
   }

   return(failCode);

 }  // end of assign1Time


 //************************************************************************
 //  assignTime2 - try to assign a Tee Time to the remaining lottery requests for the course passed
 //                (try other courses)
 //
 //    For Random and Weighted lottery types
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = the lottery parameter block
 //               con    = db connection
 //               parmcA = lottery parm block array - one per course
 //               count  = # of courses
 //************************************************************************
 
 /*   NO LONGER USED

 public static void assignTime2(Connection con, parmLott parm, parmLottC [] parmcA, int count) {


   ResultSet rs = null;
   ResultSet rs2 = null;

   int index = 0;
   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int i4 = 0;
   int tid = 0;
   int weight = 0;
   int ttime = 0;
   short tfb = 0;

   long id = 0;

   int fail = 0;

   parmLottC parmc = null;                  // parm block for course to check
   parmLottC parmc2 = null;                 // parm block for other courses

   String errorMsg = "Error in Common_Lott assignTime2: ";


   for (i=0; i < count; i++) {              // do all courses

      parmc = parmcA[i];                    // get parm block for course to check

      //
      //  Get each unassigned request and try to assign a time from another course (if requested)
      //
      try {

         PreparedStatement pstmt8 = con.prepareStatement (
            "SELECT id, weight " +
            "FROM lreqs3 " +
            "WHERE name = ? AND date = ? AND courseName = ? AND state = 1 AND checkothers = 1");

         pstmt8.clearParameters();
         pstmt8.setString(1, parm.lottName);
         pstmt8.setLong(2, parm.date);
         pstmt8.setString(3, parmc.course);          // just for this course
         rs = pstmt8.executeQuery();

         while (rs.next()) {                         // get all of them

            id = rs.getLong(1);
            weight = rs.getInt(2);

            if (i == 0) {                  // if this is first course (i = course index)

               i3 = 1;                     // start with next course

            } else {

               i3 = 0;                     // start with first course
            }

            loopCourse1:
            while (i3 < count) {           // check each course if necessary

               parmc2 = parmcA[i3];        // get course to try

               parm.course = parmc2.course;    // use this course (for checking restrictions)

               fail = assign1Time(con, parm, parmc2, id, weight);   // try to assign a time to the new course

               if (fail == 0) {              // if assigned

                  //
                  //  Assigned - save the assigned time(s) - update the request record
                  //
                  loopFindId1:
                  for (index=0; index<100; index++) {        // find the assigned times

                     if (id == parmc2.id2A[index]) {         // if assigned to this id

                        ttime = parmc2.timeA[index];        // get tee time from array
                        tfb = parmc2.fbA[index];            // get the f/b for this time slot
                        weight = parmc2.wght2A[index];      // get the weight for this time slot

                        parmc2.id2A[index] = 999999;        // mark it done
                        i2 = index + 1;                    // get next slot
                        i3 = i2 + 11;                      // furthest we should have to search for matching id
                        i4 = 1;
                        parmc2.atimeA[0] = ttime;           // init time values - set 1st one
                        parmc2.atimeA[1] = 0;
                        parmc2.atimeA[2] = 0;
                        parmc2.atimeA[3] = 0;
                        parmc2.atimeA[4] = 0;

                        while (i2 < i3 && i2 < 100 && i4 < 5) {    // look for matching id's (more than 1 tee time)

                           if (parmc2.id2A[i2] == id) {               // if match

                              parmc2.atimeA[i4] = parmc2.timeA[i2];    // save time value
                              parmc2.id2A[i2] = 999999;               // mark it done
                              i4++;
                           }
                           i2++;                                   // next
                        }
                        //
                        //  set the assigned time and f/b in the req for later processing
                        //
                        //  State = 2 (Processed & Assigned)
                        //
                        errorMsg = "Error in Common_Lott assignTime2 (type=Random/Weighted, set assigned): ";

                        try {

                           PreparedStatement pstmt9 = con.prepareStatement (
                               "UPDATE lreqs3 SET courseName = ?, type = ?, state = 2, atime1 = ?, atime2 = ?, atime3 = ?, " +
                               "atime4 = ?, atime5 = ?, afb = ?, weight = ?, afb2 = ?, afb3 = ?, afb4 = ?, afb5 = ?, fail_code = 0 " +
                               "WHERE id = ?");

                           pstmt9.clearParameters();
                           pstmt9.setString(1, parmc2.course);       // new course name
                           pstmt9.setString(2, parm.ltype);          // weighted or random
                           pstmt9.setInt(3, parmc2.atimeA[0]);
                           pstmt9.setInt(4, parmc2.atimeA[1]);
                           pstmt9.setInt(5, parmc2.atimeA[2]);
                           pstmt9.setInt(6, parmc2.atimeA[3]);
                           pstmt9.setInt(7, parmc2.atimeA[4]);
                           pstmt9.setShort(8, tfb);
                           pstmt9.setInt(9, weight);
                           pstmt9.setShort(10, tfb);
                           pstmt9.setShort(11, tfb);
                           pstmt9.setShort(12, tfb);
                           pstmt9.setShort(13, tfb);
                           pstmt9.setLong(14, id);

                           pstmt9.executeUpdate();

                           pstmt9.close();

                        }
                        catch (Exception e3) {
                           //
                           //  save error message in /v_x/error.txt
                           //
                           errorMsg = errorMsg + e3.getMessage();                                 // build error msg
                           logError(errorMsg);                                       // log it
                        }

                        break loopFindId1;       // done - exit loop
                     }
                  }                              // end of FOR loop

                  break loopCourse1;             // assigned - quit trying
               }

               i3++;                          // next course to try

               if (i3 == i) {                 // if same course

                  i3++;                       // check next one
               }
            }         // end of loopCourse1
         }            // end of WHILE

         pstmt8.close();

      }
      catch (Exception e2) {
         //
         //  save error message in /v_x/error.txt
         //
         errorMsg = errorMsg + "Exception2= " +e2.getMessage();                  // build error msg

         logError(errorMsg);                                       // log it
      }

   }

 }  // end of assignTime2
  */


 //************************************************************************
 // checkBefore - Look for an available time before the requested time
 // checkAfter - Look for an available time after the requested time
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = lottery parm block
 //               parmc  = lottery parm block that holds the requests and the tee time info
 //
 //
 //*  checkBefore
 //*
 //*  This method will locate the next 'before' time slot that MIGHT fit the group in the selected lottery
 //*  request. It does not check if that/those time slots are avaialable.  The assign1Time method
 //*  will perform those checks. That way it can check the next 'after' time if they are not 
 //*  available.  The assign1Time method alternates between before times and after times.
 //*
 //****************************************************************************************************
 //

 public static int checkBefore(parmLott parm, parmLottC parmc, boolean useBothFB) {


    int bstat = 0;
    int i3 = 0;
    int grps = 0;
    int ttime = 0;
    
    if (!parmc.course.equals("-ALL-")) {          // if NOT multi course lottery

        i3 = parm.beforei;                  // get latest before index (last one checked)
        grps = parm.groups;                 // # of groups to back up

        if (i3 >= grps) {                   // if room to back up

           if (parm.sfb.equals( "Both" )) {   // if both tees used for this lottery

              //
              //  Use both tees - switch tees if both are defined (double tees for this day)
              //
              if (parmc.fbA[i3-1] != parm.fb_before) {     // if different f/b found than last checked

                 parm.fb_before = parmc.fbA[i3-1];        // then switch
              }
           }

           loop1:
           while (i3 > 0 && grps > 0) {     // while room to back up

              i3--;                         // back up one tee time slot
              ttime = parmc.timeA[i3];      // get tee time from array

              if (ttime >= parm.ftime) {    // if tee time is acceptable

                 if ((parmc.fbA[i3] == parm.fb_before || useBothFB == true) && parmc.busyA[i3] == false) {   // if matching f/b found & not busy

                    grps--;                 // match found, continue to back up
                 }

              } else {

                 bstat = 1;                // can't go back any further
                 grps = parm.groups;       // reset # of groups
                 i3++;                     // went too far - go back so check fails
                 break loop1;              // exit while loop
              }
           }                               // end of loop1 WHILE

           parm.beforei--;                 // save new index (go back one at a time)

           if (grps == 0) {                // did we get all the way back ?

              grps = parm.groups;          // yes, reset # of groups and go back to check if available

              parm.rfb = parm.fb_before;    // set new f/b if it changed

           } else {                        // no

              bstat = 1;                   // can't go back any more
           }                               // end of IF i3 > groups

        } else {

           bstat = 1;           // can't go back any more
        }                       // end of IF i3 > groups
        
        
    } else {        // Lottery is for ALL courses
        
        String new_course = "";
        int new_fb = 0;
        int index = 0;

        i3 = parm.beforei;                  // get latest before index (last one checked)
        grps = parm.groups;                 // # of groups to back up

        if (i3 >= grps) {                   // if room to back up

           //
           //     First loop will locate a course
           //
           loop2:
           while (i3 > 0 && grps > 0) {     // while room to back up and more groups to match    
               
               if (parm.sfb.equals( "Both" )) {      // if both tees used for this lottery

                     parm.fb_before = parmc.fbA[i3-1];        // then use whatever the next one is
               }

               //
               //  Determine the fb value and course of the tee time before the one last checked - so we are sure to find matching tee times for the whole group!
               //
               new_fb = parm.fb_before;             // use last fb or new fb determined above
               
               new_course = parmc.courseA[i3-1];    // get the course from the next entry backwards (use these for each group)
                  
               if (parm.checkothers == 1 || new_course.equals(parm.courseReq)) {   // if member wants to check other courses or this is the same course
                   
                    if (parmc.timeA[i3-1] < parm.ftime) {    // if tee time is NOT acceptable

                        bstat = 1;                // can't go back any further
                        grps = parm.groups;       // reset # of groups
                        break loop2;              // exit while loop
                    }

                    index = i3;                          // start with this entry and work back for the others 
                    i3--;      

                    loop3:
                    while (index > 0 && grps > 0) {     // while room to back up

                        index--;                         // back up one tee time slot
                        ttime = parmc.timeA[index];      // get tee time from array

                        if (ttime >= parm.ftime) {    // if tee time is acceptable

                            if (parmc.fbA[index] == new_fb && parmc.courseA[index].equals(new_course) && parmc.busyA[index] == false) {   // if matching f/b & course found & not busy

                                grps--;                 // match found, continue to back up
                            }

                        } else {

                            bstat = 1;                // can't go back any further
                            grps = parm.groups;       // reset # of groups
                            index++;                  // went too far - go back so check fails
                            break loop3;              // exit while loop
                        }
                    }           // end of loop3 WHILE
                    
                    if (grps == 0) {       // if we got all we need
                        
                        i3 = index;        // mark our spot (last good one)
                    }

               } else {              // else find a matching course
                   
                   i3--;             // back up one more and check for matching course                  
               }                     // end of course locator
           
           }                     // end of loop2 while
           

           parm.beforei--;                 // save new index (go back one at a time)

           if (grps == 0) {                // did we get all the way back ?

              grps = parm.groups;          // yes, reset # of groups and go back to check if available

              parm.rfb = parm.fb_before;    // set new f/b if it changed

           } else {                        // no

              bstat = 1;                   // can't go back any more
           }                               // end of IF i3 > groups
               

        } else {              // not enough tee times left to check

           bstat = 1;           // can't go back any more
        }                       // end of IF i3 > groups
    
    }          // end if IF ALL courses    

    parm.i3 = i3;           // set new values for return
    parm.grps = grps;

    return(bstat);

 }  // end of checkBefore


 //****************************************************************************************************
 //*  checkAfter
 //*
 //*  This method will locate the next time slot that MIGHT fit the group in the selected lottery
 //*  request. It does not check if that/those time slots are avaialable.  The assign1Time method
 //*  will perform those checks. That way it can check the next 'before' time if they are not 
 //*  available.  The assign1Time method alternates between before times and after times.
 //*
 //****************************************************************************************************
 //
 public static int checkAfter(parmLott parm, parmLottC parmc, boolean useBothFB) {

    int astat = 0;
    int i3 = 0;
    int grps = 0;
    int ttime = 0;
    int i4 = 999;                      // init start location
    int req_count = parmc.req_count;
    int tee_count = parmc.tee_count;

    parm.afteri++;                     // bump to next after spot
    i3 = parm.afteri;
    grps = parm.groups;                // restore # of groups


    if (!parmc.course.equals("-ALL-")) {          // if NOT multi course lottery

        if ((i3 + grps) < tee_count) {              // if room to go ahead

           if (parm.sfb.equals( "Both" )) {   // if both tees used for this lottery

              //
              //  Use both tees - switch tees if both are defined (double tees for this day)
              //
              if (parmc.fbA[i3] != parm.fb_after) {     // if different f/b found than last checked

                 parm.fb_after = parmc.fbA[i3];        // then switch
              }
           }

           loop3:
           while (i3 < tee_count && grps > 0) {   // while room to search ahead

              ttime = parmc.timeA[i3];       // get tee time from array

              if (ttime > 0 && ttime <= parm.ltime) {    // if tee time is acceptable

                 if ((parmc.fbA[i3] == parm.fb_after || useBothFB == true) && parmc.busyA[i3] == false) {     // if matching f/b found

                    grps--;            // match found, continue to go ahead

                    if (i4 == 999) {   // first find?

                       i4 = i3;        // yes, save start index
                    }
                 }

              } else {

                 astat = 1;             // can't go ahead any further
                 grps = parm.groups;    // yes, reset # of groups and check others
                 break loop3;           // exit while loop
              }

              i3++;                    // go ahead one tee time slot
           }                          // end of loop3 WHILE

           if (grps == 0) {     // did we get all the way ahead ?

              grps = parm.groups;    // yes, reset # of groups and go check again

              parm.rfb = parm.fb_after;    // set new f/b if it changed

              if (i4 != 999) {

                 i3 = i4;       // set new start index
              }

           } else {          // no, we didn't get the times we need

              //
              //  We reached the end w/o finding enough tee times - if double tees, then try other f/b (start over)
              //  Do this in case the double tees ended and there are normal times following that are avaiable.
              //
              if (parm.sfb.equals( "Both" )) {     // if both tees used for this lottery

                 i4 = 999;                          // start over
                 i3 = parm.afteri;                  // restore starting point
                 grps = parm.groups;                // restore # of groups
                 astat = 0;                         // reset error code

                 if (parm.fb_after == 0) {          // if we just checked for front tees

                    parm.fb_after = 1;              // try back tees

                 } else {

                    parm.fb_after = 0;              // try front tees
                 }

                 loop3b:
                 while (i3 < tee_count && grps > 0) {   // while room to search ahead

                    ttime = parmc.timeA[i3];       // get tee time from array

                    if (ttime > 0 && ttime <= parm.ltime) {    // if tee time is acceptable

                       if ((parmc.fbA[i3] == parm.fb_after || useBothFB == true) && parmc.busyA[i3] == false) {     // if matching f/b found

                          grps--;            // match found, continue to go ahead

                          if (i4 == 999) {   // first find?

                             i4 = i3;        // yes, save start index
                          }
                       }

                    } else {

                       astat = 1;             // can't go ahead any further
                       grps = parm.groups;    // yes, reset # of groups and check others
                       break loop3b;           // exit while loop
                    }

                    i3++;                    // go ahead one tee time slot
                 }                          // end of loop3b WHILE

                 if (grps == 0) {          // did we get all the way ahead ?

                    grps = parm.groups;    // yes, reset # of groups and go check again

                    parm.rfb = parm.fb_after;    // set new f/b if it changed

                    if (i4 != 999) {

                       i3 = i4;       // set new start index
                    }

                 } else {          // no, we didn't get the times we need

                    astat = 1;      // can't go ahead any more
                 }

              } else {           // not double tees - done

                 astat = 1;      // can't go ahead any more
              }
           }                 // end of IF i3 > groups

        } else {

           astat = 1;           // can't go ahead any more
        }                       // end of IF i3 < tee_count

        
    } else {       // course = -ALL-
        
        String new_course = "";
        int new_fb = 0;

        if ((i3 + grps) < tee_count) {              // if room to go ahead

           if (parm.sfb.equals( "Both" )) {   // if both tees used for this lottery

              //
              //  Use both tees - switch tees if both are defined (double tees for this day)
              //
              if (parmc.fbA[i3] != parm.fb_after) {     // if different f/b found than last checked

                 parm.fb_after = parmc.fbA[i3];        // then switch
              }
           }

           //
           //  Determine the fb value and course of the tee time before the one last checked - so we are sure to find matching tee times for the whole group!
           //
           new_fb = parm.fb_after;              // use last fb or new fb determined above
           new_course = parmc.courseA[i3];      // get the course to be checked 

           if (parm.checkothers == 0 && !new_course.equals(parm.courseReq)) {   // if member doesn't want to check other courses and different course
               
              i3++;           
           
              loop2c:
              while (i3 < tee_count) {          // roll ahead and check for a matching course
                  
                 new_fb = parmc.fbA[i3];              // get next fb
                 new_course = parmc.courseA[i3];      // get the next course

                 if (new_course.equals(parm.courseReq)) {     // if course matches what was requested    
                     
                     break loop2c;        // exit loop and continue with this tee time
                 }              
                  
                 i3++;            // try next one
              }                
           }
                   
           loop3:
           while (i3 < tee_count && grps > 0) {   // while room to search ahead

              ttime = parmc.timeA[i3];       // get tee time from array

              if (ttime > 0 && ttime <= parm.ltime) {    // if tee time is acceptable

                 if (parmc.busyA[i3] == false && (parmc.fbA[i3] == new_fb || useBothFB == true) && 
                     (parmc.courseA[i3].equals(new_course) || (parm.checkothers > 0 && grps == parm.groups))) {   // if matching f/b & course found OR new course and first time thru here

                    if (parm.checkothers > 0 && grps == parm.groups) {       // if first time here save the course so the following groups will match
                       
                       new_course = parmc.courseA[i3];
                    }

                    grps--;                  // match found, continue to go ahead
                    
                    if (i4 == 999) {   // first find?

                       i4 = i3;        // yes, save start index
                    }
                 }

              } else {

                 astat = 1;             // can't go ahead any further
                 grps = parm.groups;    // yes, reset # of groups and check others
                 break loop3;           // exit while loop
              }

              i3++;                    // go ahead one tee time slot
           }                          // end of loop3 WHILE

           if (grps == 0) {     // did we get all the way ahead ?

              grps = parm.groups;    // yes, reset # of groups and go check again

              parm.rfb = parm.fb_after;    // set new f/b if it changed

              if (i4 != 999) {

                 i3 = i4;       // set new start index
              }

           } else {          // no, we didn't get the times we need

              //
              //  We reached the end w/o finding enough tee times - if double tees, then try other f/b (start over)
              //  Do this in case the double tees ended and there are normal times following that are avaiable.
              //
               /*
              if (parm.sfb.equals( "Both" )) {     // if both tees used for this lottery

                 i4 = 999;                          // start over
                 i3 = parm.afteri;                  // restore starting point
                 grps = parm.groups;                // restore # of groups
                 astat = 0;                         // reset error code

                 if (parm.fb_after == 0) {          // if we just checked for front tees

                    parm.fb_after = 1;              // try back tees

                 } else {

                    parm.fb_after = 0;              // try front tees
                 }

                 loop3b:
                 while (i3 < req_count && grps > 0) {   // while room to search ahead

                    ttime = parmc.timeA[i3];       // get tee time from array

                    if (ttime > 0 && ttime <= parm.ltime) {    // if tee time is acceptable

                       if (parmc.fbA[i3] == parm.fb_after && parmc.busyA[i3] == false) {     // if matching f/b found

                          grps--;            // match found, continue to go ahead

                          if (i4 == 999) {   // first find?

                             i4 = i3;        // yes, save start index
                          }
                       }

                    } else {

                       astat = 1;             // can't go ahead any further
                       grps = parm.groups;    // yes, reset # of groups and check others
                       break loop3b;           // exit while loop
                    }

                    i3++;                    // go ahead one tee time slot
                 }                          // end of loop3b WHILE

                 if (grps == 0) {          // did we get all the way ahead ?

                    grps = parm.groups;    // yes, reset # of groups and go check again

                    parm.rfb = parm.fb_after;    // set new f/b if it changed

                    if (i4 != 999) {

                       i3 = i4;       // set new start index
                    }

                 } else {          // no, we didn't get the times we need

                    astat = 1;      // can't go ahead any more
                 }

              } else {           // not double tees - done

                 astat = 1;      // can't go ahead any more
              }
              */
               
              astat = 1;      // not sure that we need the above check for other fb - just error out 
               
           }                 // end of IF i3 > groups

        } else {

           astat = 1;           // can't go ahead any more
        }                       // end of IF i3 < tee_count
        
    }           // end of IF course = -ALL-
        
    parm.i3 = i3;           // set new values for return
    parm.grps = grps;

    return(astat);

 }  // end of checkAfter


 
 //************************************************************************
 // getWeight - Determine the weight of all the players in the lottery
 //               request that was passed.
 //
 //
 //   called by:  processLott above
 //
 //       parms:  con   = db connection
 //               id    = id of the lottery request
 //               name  = name of lottery
 //
 //************************************************************************

 private static int getWeight(Connection con, long id, String name) {


   ResultSet rs = null;

   int i = 0;
   int weight = 0;     // total weight for req
   int high = 0;       // highest
   int low = 9999;     // lowest
   int avg = 0;        // average
   int w = 0;
   int players = 0;    // # of players in req

   int adays = 0;      // days to accumulate points
   int wdpts = 0;      // points for weekday rounds
   int wepts = 0;      // points for weekend rounds
   int evpts = 0;      // points for event rounds
   int gpts = 0;       // points for each guest round
   int nopts = 0;      // points for each no-show
   int select = 0;     // Lottery selection based on; total pts, avg pts, highest, lowest
   int guest = 0;      // points for each guest in request
   int days = 0;
   int allowx = 0;     // are X's allowed in this lottery
   int xvalue = 0;     // if yes, how to treat them

   long sdate = 0;
   long edate = 0;

   String user = "";

   String [] userA = new String [25];       // members in the request
   String [] usergA = new String [25];      // guests (owning members) in the request
   String [] playerA = new String [25];     // players in the request

   int [] weightA = new int [25];           // weights of each player

   //
   //  get lottery data (pts, etc.) for the lottery requested
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT adays, wdpts, wepts, evpts, gpts, nopts, selection, guest, allowx, xvalue " +
         "FROM lottery3 " +
         "WHERE name = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);
      rs = pstmt1.executeQuery();

      if (rs.next()) {          // get lottery

         adays = rs.getInt(1);
         wdpts = rs.getInt(2);
         wepts = rs.getInt(3);
         evpts = rs.getInt(4);
         gpts = rs.getInt(5);
         nopts = rs.getInt(6);
         select = rs.getInt(7);
         guest = rs.getInt(8);
         allowx = rs.getInt(9);
         xvalue = rs.getInt(10);
      }
      pstmt1.close();

      //
      //  Determine the start and end dates for searches
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);

      month = month + 1;                            // month starts at zero

      edate = year * 10000;
      edate = edate + (month * 100);
      edate = edate + day;                          // end date = yyyymmdd

      //
      // roll cal back to find the start date
      //
      days = 0 - adays;                             // create negative number

      cal.add(Calendar.DATE,days);                  // roll back 'adays' days

      year = cal.get(Calendar.YEAR);
      month = cal.get(Calendar.MONTH);
      day = cal.get(Calendar.DAY_OF_MONTH);

      month = month + 1;                            // month starts at zero

      sdate = year * 10000;
      sdate = sdate + (month * 100);
      sdate = sdate + day;                          // date = yyyymmdd

      //
      //  Get the players from the request
      //
      PreparedStatement pstmt8 = con.prepareStatement (
         "SELECT player1, player2, player3, player4, player5, player6, player7, player8, player9, player10, player11, player12, player13, " +
         "player14, player15, player16, player17, player18, player19, player20, player21, player22, player23, player24, player25, " +
         "user1, user2, user3, user4, user5, user6, user7, user8, user9, user10, user11, user12, user13, " +
         "user14, user15, user16, user17, user18, user19, user20, user21, user22, user23, user24, user25, players, " +
         "userg1, userg2, userg3, userg4, userg5, userg6, userg7, userg8, userg9, userg10, userg11, userg12, userg13, " +
         "userg14, userg15, userg16, userg17, userg18, userg19, userg20, userg21, userg22, userg23, userg24, userg25 " +
         "FROM lreqs3 " +
         "WHERE id = ?");

      pstmt8.clearParameters();        // clear the parms
      pstmt8.setLong(1, id);
      rs = pstmt8.executeQuery();

      if (rs.next()) {                 // get the request info

         playerA[0] = rs.getString("player1");
         playerA[1] = rs.getString("player2");
         playerA[2] = rs.getString("player3");
         playerA[3] = rs.getString("player4");
         playerA[4] = rs.getString("player5");
         playerA[5] = rs.getString("player6");
         playerA[6] = rs.getString("player7");
         playerA[7] = rs.getString("player8");
         playerA[8] = rs.getString("player9");
         playerA[9] = rs.getString("player10");
         playerA[10] = rs.getString("player11");
         playerA[11] = rs.getString("player12");
         playerA[12] = rs.getString("player13");
         playerA[13] = rs.getString("player14");
         playerA[14] = rs.getString("player15");
         playerA[15] = rs.getString("player16");
         playerA[16] = rs.getString("player17");
         playerA[17] = rs.getString("player18");
         playerA[18] = rs.getString("player19");
         playerA[19] = rs.getString("player20");
         playerA[20] = rs.getString("player21");
         playerA[21] = rs.getString("player22");
         playerA[22] = rs.getString("player23");
         playerA[23] = rs.getString("player24");
         playerA[24] = rs.getString("player25");
         userA[0] = rs.getString("user1");
         userA[1] = rs.getString("user2");
         userA[2] = rs.getString("user3");
         userA[3] = rs.getString("user4");
         userA[4] = rs.getString("user5");
         userA[5] = rs.getString("user6");
         userA[6] = rs.getString("user7");
         userA[7] = rs.getString("user8");
         userA[8] = rs.getString("user9");
         userA[9] = rs.getString("user10");
         userA[10] = rs.getString("user11");
         userA[11] = rs.getString("user12");
         userA[12] = rs.getString("user13");
         userA[13] = rs.getString("user14");
         userA[14] = rs.getString("user15");
         userA[15] = rs.getString("user16");
         userA[16] = rs.getString("user17");
         userA[17] = rs.getString("user18");
         userA[18] = rs.getString("user19");
         userA[19] = rs.getString("user20");
         userA[20] = rs.getString("user21");
         userA[21] = rs.getString("user22");
         userA[22] = rs.getString("user23");
         userA[23] = rs.getString("user24");
         userA[24] = rs.getString("user25");
         players = rs.getInt("players");
         usergA[0] = rs.getString("userg1");
         usergA[1] = rs.getString("userg2");
         usergA[2] = rs.getString("userg3");
         usergA[3] = rs.getString("userg4");
         usergA[4] = rs.getString("userg5");
         usergA[5] = rs.getString("userg6");
         usergA[6] = rs.getString("userg7");
         usergA[7] = rs.getString("userg8");
         usergA[8] = rs.getString("userg9");
         usergA[9] = rs.getString("userg10");
         usergA[10] = rs.getString("userg11");
         usergA[11] = rs.getString("userg12");
         usergA[12] = rs.getString("userg13");
         usergA[13] = rs.getString("userg14");
         usergA[14] = rs.getString("userg15");
         usergA[15] = rs.getString("userg16");
         usergA[16] = rs.getString("userg17");
         usergA[17] = rs.getString("userg18");
         usergA[18] = rs.getString("userg19");
         usergA[19] = rs.getString("userg20");
         usergA[20] = rs.getString("userg21");
         usergA[21] = rs.getString("userg22");
         usergA[22] = rs.getString("userg23");
         usergA[23] = rs.getString("userg24");
         usergA[24] = rs.getString("userg25");
      }
      pstmt8.close();

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in Common_Lott getWeight: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }

   if (players > 0) {                         // if ok to continue

      for (i=0; i<25; i++) {

         user = userA[i];                      // get user

         if (!user.equals( "" )) {

            w = get1Weight(con, user, wdpts, wepts, evpts, gpts, nopts, sdate, edate);  // get this user's weight

            weightA[i] = w;                    // save this player's weight

            //
            //  Accumulate total weight, highest weight, lowest weight, avg weight
            //
            weight = weight + w;              // accumulate total weight for request

            if (w > high) {

               high = w;          // new high
            }
            if (w < low) {

               low = w;           // new low
            }
         }
      }

      //
      //  calculate guests' weight
      //
      for (i=0; i<25; i++) {

         if (!usergA[i].equals( "" )) {      // each non-null represents one guest

            if (guest == 0) {           // if guest don't count

               players--;               // remove guest from # of players (so avg is correct)
            }
            if (guest == 1) {           // if guest counts same as high member

               weight = weight + high;
            }
            if (guest == 2) {           // if guest counts same as low member

               weight = weight + low;
            }
         }
      }

      //
      //  calculate weight of any X's if allowed
      //
      if (allowx > 0) {          // if X's allowed in this lottery

         for (i=0; i<25; i++) {

            if (playerA[i].equalsIgnoreCase( "x" )) {      // if player position contains an X

               if (xvalue == 0) {           // if X's don't count

                  players--;               // remove X from # of players (so avg is correct)
               }
               if (xvalue == 1) {           // if X counts same as high member

                  weight = weight + high;
               }
               if (xvalue == 2) {           // if X counts same as low member

                  weight = weight + low;
               }
            }
         }
      }

      //
      //  calculate average weight per player
      //
      avg = weight/players;

      if (avg == 0) {

         if (weight > 0) {     // if there was a weight

            avg = 1;           // round up
         }
      }

      //
      //  determine which weight to return based on lottery options
      //
      if (select == 2) {     // if Average Points of group

         weight = avg;       // return average weight
      }
      if (select == 3) {     // if Highest Points of group members

         weight = high;       // return highest weight
      }
      if (select == 4) {     // if Lowest Points of group members

         weight = low;       // return lowest weight
      }
      // else - return total weight (select = 1)
   }

   //
   //  Save the weights in the lottery request
   //
   saveWeights(weightA, id, con);


   return(weight);

 }  // end of getWeight


 //************************************************************************
 // get1Weight - Determine the lottery weight of the player requested.
 //
 //
 //   called by:  getWeight above
 //
 //       parms:  con   = db connection
 //               user  = username of the player to check
 //               wdpts = weekday points
 //               wepts = weekend points
 //               evpts = event points
 //               gpts  = guest points
 //               nopts = no-show points
 //               sdate = date to start looking
 //               edate = date to stop looking
 //
 //************************************************************************

 public static int get1Weight(Connection con, String user, int wdpts, int wepts, int evpts,
                               int gpts, int nopts, long sdate, long edate) {


   ResultSet rs = null;

   String day = "";
   String event = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";

   int weight = 0;

   //
   //  calculate the weight for this user based on the parms passed and his/her past tee times
   //
   try {

      PreparedStatement pstmt2 = con.prepareStatement (
                "SELECT day, event " +
                "FROM teepast2 WHERE date >= ? AND date <= ? AND " +
                "((username1 = ? && show1 = 1) || (username2 = ? && show2 = 1) || (username3 = ? && show3 = 1) || " +
                "(username4 = ? && show4 = 1) || (username5 = ? && show5 = 1))");

      //
      //  find tee slots for the specified date period
      //
      pstmt2.clearParameters();        // clear the parms
      pstmt2.setLong(1, sdate);
      pstmt2.setLong(2, edate);
      pstmt2.setString(3, user);
      pstmt2.setString(4, user);
      pstmt2.setString(5, user);
      pstmt2.setString(6, user);
      pstmt2.setString(7, user);
      rs = pstmt2.executeQuery();

      while (rs.next()) {

         day = rs.getString(1);
         event = rs.getString(2);

         if (evpts > 0 && !event.equals( "" )) {

            weight = weight + evpts;          // add event points

         } else {

            if (!day.equalsIgnoreCase( "Saturday" ) && !day.equalsIgnoreCase( "Sunday" )) {   // if not a w/e

               weight = weight + wdpts;       // or weekday points

            } else {

               weight = weight + wepts;       // or weekend points
            }
         }
      }
      pstmt2.close();

      if (gpts > 0) {             // if points for guest rounds

         pstmt2 = con.prepareStatement (
                   "SELECT userg1, userg2, userg3, userg4, userg5 " +
                   "FROM teepast2 WHERE date >= ? AND date <= ? AND " +
                   "(userg1 = ? || userg2 = ? || userg3 = ? || userg4 = ? || userg5 = ?)");

         //
         //  find tee times where this member had a guest
         //
         pstmt2.clearParameters();        // clear the parms
         pstmt2.setLong(1, sdate);
         pstmt2.setLong(2, edate);
         pstmt2.setString(3, user);
         pstmt2.setString(4, user);
         pstmt2.setString(5, user);
         pstmt2.setString(6, user);
         pstmt2.setString(7, user);
         rs = pstmt2.executeQuery();

         while (rs.next()) {

            userg1 = rs.getString(1);
            userg2 = rs.getString(2);
            userg3 = rs.getString(3);
            userg4 = rs.getString(4);
            userg5 = rs.getString(5);

            if (user.equalsIgnoreCase( userg1 )) {

               weight = weight + gpts;          // add guest points for each guest
            }
            if (user.equalsIgnoreCase( userg2 )) {

               weight = weight + gpts;
            }
            if (user.equalsIgnoreCase( userg3 )) {

               weight = weight + gpts;
            }
            if (user.equalsIgnoreCase( userg4 )) {

               weight = weight + gpts;
            }
            if (user.equalsIgnoreCase( userg5 )) {

               weight = weight + gpts;
            }
         }
         pstmt2.close();
      }

      if (nopts > 0) {             // if points for no-shows

         pstmt2 = con.prepareStatement (
                   "SELECT mm " +
                   "FROM teepast2 WHERE date >= ? AND date <= ? AND " +
                   "((username1 = ? && show1 <> 1) || (username2 = ? && show2 <> 1) || (username3 = ? && show3 <> 1) || " +
                   "(username4 = ? && show4 <> 1) || (username5 = ? && show5 <> 1))");

         //
         //  find tee times where this member had a guest
         //
         pstmt2.clearParameters();        // clear the parms
         pstmt2.setLong(1, sdate);
         pstmt2.setLong(2, edate);
         pstmt2.setString(3, user);
         pstmt2.setString(4, user);
         pstmt2.setString(5, user);
         pstmt2.setString(6, user);
         pstmt2.setString(7, user);
         rs = pstmt2.executeQuery();

         while (rs.next()) {

            weight = weight + nopts;
         }
         pstmt2.close();
      }
   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in Common_Lott get1Weight: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }

   return(weight);

 }  // end of get1Weight


 //************************************************************************
 // getWeightP - Determine the weight of all the players in the lottery
 //              request that was passed.
 //
 //     For 'Weighted By Proximity' type Lottery
 //
 //   called by:  processLott above
 //
 //       parms:  con   = db connection
 //               id    = id of the lottery request
 //               name  = name of lottery
 //
 //************************************************************************

 private static int getWeightP(Connection con, long id, String name) {


   ResultSet rs = null;

   int i = 0;
   int weight = 0;     // total weight for req
   int high = 0;       // highest
   int low = 9999;     // lowest
   int avg = 0;        // average
   int w = 0;
   int players = 0;    // # of players in req

   int adays = 0;      // days to accumulate points
   int select = 0;     // Lottery selection based on; total pts, avg pts, highest, lowest
   int guest = 0;      // points for each guest in request
   int days = 0;
   int year = 0;
   int month = 0;
   int day = 0;


   long sdate = 0;
   long edate = 0;

   String user = "";
   String club = getClub.getClubName(con);


   String [] userA = new String [25];       // players in the request
   String [] usergA = new String [25];      // guests (owning players) in the request
   String [] playerA = new String [25];

   int [] weightA = new int [25];           // weights of each player

   //
   //  get lottery data (pts, etc.) for the lottery requested
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT adays, selection, guest " +
         "FROM lottery3 " +
         "WHERE name = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);
      rs = pstmt1.executeQuery();

      if (rs.next()) {          // get lottery

         adays = rs.getInt(1);
         select = rs.getInt(2);
         guest = rs.getInt(3);
      }
      pstmt1.close();

      //
      //  Get the players from the request
      //
      PreparedStatement pstmt8 = con.prepareStatement (
         "SELECT user1, user2, user3, user4, user5, user6, user7, user8, user9, user10, user11, user12, user13, " +
         "user14, user15, user16, user17, user18, user19, user20, user21, user22, user23, user24, user25, players, " +
         "userg1, userg2, userg3, userg4, userg5, userg6, userg7, userg8, userg9, userg10, userg11, userg12, userg13, " +
         "userg14, userg15, userg16, userg17, userg18, userg19, userg20, userg21, userg22, userg23, userg24, userg25," +
         "mm, dd, yy " +
         "FROM lreqs3 " +
         "WHERE id = ?");

      pstmt8.clearParameters();        // clear the parms
      pstmt8.setLong(1, id);
      rs = pstmt8.executeQuery();

      if (rs.next()) {                 // get the request info

         userA[0] = rs.getString(1);
         userA[1] = rs.getString(2);
         userA[2] = rs.getString(3);
         userA[3] = rs.getString(4);
         userA[4] = rs.getString(5);
         userA[5] = rs.getString(6);
         userA[6] = rs.getString(7);
         userA[7] = rs.getString(8);
         userA[8] = rs.getString(9);
         userA[9] = rs.getString(10);
         userA[10] = rs.getString(11);
         userA[11] = rs.getString(12);
         userA[12] = rs.getString(13);
         userA[13] = rs.getString(14);
         userA[14] = rs.getString(15);
         userA[15] = rs.getString(16);
         userA[16] = rs.getString(17);
         userA[17] = rs.getString(18);
         userA[18] = rs.getString(19);
         userA[19] = rs.getString(20);
         userA[20] = rs.getString(21);
         userA[21] = rs.getString(22);
         userA[22] = rs.getString(23);
         userA[23] = rs.getString(24);
         userA[24] = rs.getString(25);
         players = rs.getInt(26);
         usergA[0] = rs.getString(27);
         usergA[1] = rs.getString(28);
         usergA[2] = rs.getString(29);
         usergA[3] = rs.getString(30);
         usergA[4] = rs.getString(31);
         usergA[5] = rs.getString(32);
         usergA[6] = rs.getString(33);
         usergA[7] = rs.getString(34);
         usergA[8] = rs.getString(35);
         usergA[9] = rs.getString(36);
         usergA[10] = rs.getString(37);
         usergA[11] = rs.getString(38);
         usergA[12] = rs.getString(39);
         usergA[13] = rs.getString(40);
         usergA[14] = rs.getString(41);
         usergA[15] = rs.getString(42);
         usergA[16] = rs.getString(43);
         usergA[17] = rs.getString(44);
         usergA[18] = rs.getString(45);
         usergA[19] = rs.getString(46);
         usergA[20] = rs.getString(47);
         usergA[21] = rs.getString(48);
         usergA[22] = rs.getString(49);
         usergA[23] = rs.getString(50);
         usergA[24] = rs.getString(51);
         month = rs.getInt(52);
         day = rs.getInt(53);
         year = rs.getInt(54);
      }
      pstmt8.close();

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in Common_Lott getWeightP: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }


   // If Dataw, we need to look up player names for all players in the request
   if (club.equals("dataw")) {

       PreparedStatement pstmt = null;

       try {
           pstmt = con.prepareStatement(
                   "SELECT " +
                   "player1, player2, player3, player4, player5, player6, player7, player8, player9, player10, " +
                   "player11, player12, player13, player14, player15, player16, player17, player18, player19, player20, " +
                   "player21, player22, player23, player24, player25 " +
                   "FROM lreqs3 " +
                   "WHERE id = ?");

           pstmt.clearParameters();
           pstmt.setLong(1, id);

           rs = pstmt.executeQuery();

           if (rs.next()) {

               for (int j=0; j<25; j++) {
                   playerA[j] = rs.getString("player" + (j+1));
               }
           }

           pstmt.close();

       } catch (Exception exc) {

           String errorMsg = "Error in Common_Lott getWeight: ";
           errorMsg = errorMsg + exc.getMessage();                                 // build error msg
           logError(errorMsg);                                       // log it
       }

   } else {     // For other clubs, initialize the playerA array to empty strings to ensure no null errors occur

       for (int j=0; j<25; j++) {
           playerA[i] = "";
       }
   }

   //
   //  Determine the start and end dates for searches
   //
   //  End date will be the date that this lottery request is for (date of the tee sheet) so it includes
   //     any other requests processed between today and this requested date.
   //
   edate = (year * 10000) + (month * 100) + day;        // end date = yyyymmdd

   //
   // roll cal back to find the start date
   //
   days = 0 - adays;                             // create negative number

   Calendar cal = new GregorianCalendar(year, month-1, day);    // get requested date
   cal.add(Calendar.DATE,days);                                 // roll back 'adays' days

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH) +1;
   day = cal.get(Calendar.DAY_OF_MONTH);

   sdate = (year * 10000) + (month * 100) + day;        // start date = yyyymmdd


   if (players > 0) {                         // if ok to continue

      for (i=0; i<25; i++) {

         user = userA[i];                      // get user

         if (!user.equals( "" )) {

            w = get1WeightP(con, user, sdate, edate, name);     // get this user's weight

            weightA[i] = w;                     // save each member's weight

            //
            //  Accumulate total weight, highest weight, lowest weight, avg weight
            //
            weight = weight + w;              // accumulate total weight for request

            if (w > high) {

               high = w;          // new high
            }
            if (w < low) {

               low = w;           // new low
            }
         }
      }

      //
      //  calculate guests' weight
      //
      for (i=0; i<25; i++) {

         if (!usergA[i].equals( "" )) {      // each non-null represents one guest

            if (guest == 0 || (club.equals("dataw") && playerA[i].startsWith("Blank"))) {    // if guest are not to be counted

               if (!club.equals("ballenisles")) {    // Ballenisles wants to count the guests, but don't add any weight to group weight (count guest as zero)

                  players--;               // remove guest from # of players (so avg is correct)
               }
            }
            if (guest == 1) {           // if guest counts same as high member

               weight = weight + high;
            }
            if (guest == 2) {           // if guest counts same as low member

               weight = weight + low;
            }
         }
      }

      //
      //  calculate average weight per player
      //
      avg = weight/players;

      if (avg == 0) {

         if (weight > 0) {     // if there was a weight

            avg = 1;           // round up
         }
      }

      //
      //  determine which weight to return based on lottery options
      //
      if (select == 2) {     // if Average Points of group

         weight = avg;       // return average weight
      }
      if (select == 3) {     // if Highest Points of group members

         weight = high;       // return highest weight
      }
      if (select == 4) {     // if Lowest Points of group members

         weight = low;       // return lowest weight
      }
      // else - return total weight (select = 1)
   }

   //
   //  Save the weights in the lottery request
   //
   saveWeights(weightA, id, con);


   return(weight);

 }  // end of getWeightP


 //************************************************************************
 // get1WeightP - Determine the lottery weight of the player requested.
 //
 //
 //   called by:  getWeight above
 //
 //       parms:  con   = db connection
 //               user  = username of the player to check
 //               sdate = date to start looking
 //               edate = date to stop looking
 //
 //************************************************************************

 public static int get1WeightP(Connection con, String user, long sdate, long edate, String name) {


   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   String club = "";
   String query = "";
   String course = "";

   int count = 0;
   int mins = 0;
   int total = 0;
   int weight = 0;
   int time = 0;
   
   long date = 0;
   
   long today = Utilities.getDate(con);      // get today's date (yyyymmdd)
   
   boolean thisLottOnly = false;
   boolean checkNoShows = false;
   boolean noShow = false;


   //
   //  Get the club name for customs
   //
   club = getClub.getClubName(con);            // in common - get the club name (name of db)
   
   //
   //  See if this club wants to exclude other lottery requests when calculating weight (cases 1860 & 1894)
   //
   if (club.equals("leawoodsouthcc") || club.equals("pecanplantation") || club.equals("mesaverdecc")) { 
       
       thisLottOnly = true;      // only include requests for this lottery
   }


   //
   //  See if this club wants to assign a weight of zero for any no-shows
   //
   if (club.equals("pecanplantation") && today > 20131205) { 
       
       checkNoShows = true;      // check for no-shows and use a weight of zero
   }


   //
   //  calculate the weight for this user based on the parms passed and his/her past lottery results
   //
   try {

      if (thisLottOnly == true) {       // if only using this lotterry

         query = "SELECT * " +
                   "FROM lassigns5 WHERE username = ? AND date >= ? AND date <= ? AND lname = ?";

      } else {

         query = "SELECT * " +
                   "FROM lassigns5 WHERE username = ? AND date >= ? AND date <= ?";
      }

      pstmt2 = con.prepareStatement (query);

      pstmt2.clearParameters();
      pstmt2.setString(1, user);
      pstmt2.setLong(2, sdate);
      pstmt2.setLong(3, edate);

      if (thisLottOnly == true) {     // if only using this lotterry

         pstmt2.setString(4, name);
      }

      rs = pstmt2.executeQuery();

      while (rs.next()) {

         mins = rs.getInt("mins");   // minutes from requested time
         
         if (checkNoShows == true && mins > 0) {   // check for no-show?
            
            //
            //  Club wants to check for no-shows.  If this member was a no-show on this date, then use a weight of zero
            //
            date = rs.getLong("date");                //  date of this assignment 
            time = rs.getInt("time_assign");          //  time assigned
            course = rs.getString("course_assign");   //  course assigned (the fb is not tracked)
                       
            noShow = checkNoShow(date, time, course, user, con);   // check for no-show
            
            if (noShow == true) mins = 0;
         }

         total = total + mins;       // keep running total

         count++;                    // count number of req's that were filled

      }
      pstmt2.close();

      //
      //  Determine weight
      //
      if (count == 0) {              // if no weight yet

         weight = 200;               // assign a default weight that should give good result

         if (club.equals("tamarack") || club.equals("bishopsbay")) {

            weight = 0;        // they want newbies to start with low priority
         }

      } else {

         if (total > 0) {              // make sure weight is non-zero (0 indicates user got all requested times)

            weight = total / count;     // calculate weight (average minutes)
         }
      }

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in Common_Lott get1WeightP: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it

      weight = 20;               // default weight
   }

   return(weight);

 }  // end of get1WeightP


 
 //************************************************************************
 //  getFirstTime - determine the earliest time member will accept
 //                 (for assigning a tee time - lottery)
 //
 //   called by:  processLott - above
 //               Proshop_mlottery
 //
 //       parms:  time   = requested time from lottery request
 //               before = number of minutes before req'd time member will accept
 //
 //   return:  time to accept
 //
 //************************************************************************

 public static int getFirstTime(int time, int before) {


   int hr = 0;
   int min = 0;

   hr = time / 100;
   min = time - (hr * 100);

   while (before > 0) {

      if (before <= min) {

         min = min - before;
         before = 0;

      } else {     // before > min

         before = before - min;   // before will still be > 0

         if (hr > 0) {

            min = 60;
            hr--;

         } else {      // hr is 0 or less - exit

            before = 0;
            hr = 0;
         }
      }
   }
   if (hr == 0) {

      time = 0100;         // this is early enough
   } else {

      time = (hr * 100) + min;
   }
   return(time);

 }  // end of getFirstTime


 //************************************************************************
 //  getLastTime - determine the latest time member will accept
 //                 (for assigning a tee time - lottery)
 //
 //   called by:  processLott - above
 //               Proshop_mlottery
 //
 //       parms:  time   = requested time from lottery request
 //               after = number of minutes after req'd time member will accept
 //
 //   return:  time to accept
 //
 //************************************************************************

 public static int getLastTime(int time, int after) {


   int hr = 0;
   int min = 0;

   hr = time / 100;
   min = time - (hr * 100);

   while (after > 0) {

      if (after >= 60) {

         if (hr < 23) {

            hr++;
            after = after - 60;

         } else {      // hr is too high - exit

            after = 0;
            hr = 23;
         }

      } else {     // after < 60

         min = min + after;
         after = 0;               // done

         if (min > 59) {

            min = min - 60;
            hr++;

            if (hr > 23) {

               hr = 23;
            }
         }
      }
   }

   time = (hr * 100) + min;

   return(time);

 }  // end of getLastTime


 
 
 //************************************************************************
 //
 //  saveWeights - save the members' weights in the lottery request
 //
 //************************************************************************

 public static void saveWeights(int [] weightA, long id, Connection con) {


   try {

      PreparedStatement pstmt2 = con.prepareStatement (
          "UPDATE lreqs3 " +
          "SET weight1 = ?, weight2 = ?, weight3 = ?, weight4 = ?, weight5 = ?, weight6 = ?, weight7 = ?, weight8 = ?, " +
          "weight9 = ?, weight10 = ?, weight11 = ?, weight12 = ?, weight13 = ?, weight14 = ?, weight15 = ?, weight16 = ?, " +
          "weight17 = ?, weight18 = ?, weight19 = ?, weight20 = ?, weight21 = ?, weight22 = ?, weight23 = ?, " +
          "weight24 = ?, weight25 = ? " +
          "WHERE id = ?");

      pstmt2.clearParameters();        // clear the parms
      pstmt2.setInt(1, weightA[0]);
      pstmt2.setInt(2, weightA[1]);
      pstmt2.setInt(3, weightA[2]);
      pstmt2.setInt(4, weightA[3]);
      pstmt2.setInt(5, weightA[4]);
      pstmt2.setInt(6, weightA[5]);
      pstmt2.setInt(7, weightA[6]);
      pstmt2.setInt(8, weightA[7]);
      pstmt2.setInt(9, weightA[8]);
      pstmt2.setInt(10, weightA[9]);
      pstmt2.setInt(11, weightA[10]);
      pstmt2.setInt(12, weightA[11]);
      pstmt2.setInt(13, weightA[12]);
      pstmt2.setInt(14, weightA[13]);
      pstmt2.setInt(15, weightA[14]);
      pstmt2.setInt(16, weightA[15]);
      pstmt2.setInt(17, weightA[16]);
      pstmt2.setInt(18, weightA[17]);
      pstmt2.setInt(19, weightA[18]);
      pstmt2.setInt(20, weightA[19]);
      pstmt2.setInt(21, weightA[20]);
      pstmt2.setInt(22, weightA[21]);
      pstmt2.setInt(23, weightA[22]);
      pstmt2.setInt(24, weightA[23]);
      pstmt2.setInt(25, weightA[24]);
      pstmt2.setLong(26, id);

      pstmt2.executeUpdate();

      pstmt2.close();

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in Common_Lott saveWeights: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }

 }  // end of saveWeights


 
 
//    END OF MOVED METHODS FROM SYSTEMUTILS !!!!!!!!!!!! 
 
 
 

 // *********************************************************
 //
 //  Process member restrictions for lottery request.
 //
 //
 //     Called by:  Member_lott (calls individual methods below)
 //                 Proshop_lott (calls individual methods below)
 //                 Proshop_mlottery
 //                 Common_Lott
 //
 //
 // *********************************************************

 public static boolean checkRests(int skip, parmLott parm, Connection con) {
    
    boolean error = false;

    error = checkRests(skip, parm, con, false);  
    
    return(error);
 }


 public static boolean checkRests(int skip, parmLott parm, Connection con, boolean processCheck) {



   //  The boolean 'processCheck' indicates if we are processing the lottery.  If so, then we don't need to check all restrictions
   //  because they were already checked when the request was made.  If the request got through the first round of checks, then 
   //  it is either ok or the pro allowed.  Therefore, we don't want to prevent the request from being assigned a time.
    
    
   //
   //  Get some parms from the parm block for easier reference
   //
   String club = parm.club;
   String course = parm.course;
     
   String player = "";

   int time = parm.time;
   int fb = parm.fb;
     
   long date = parm.date;
   long lottid = parm.lottid;

   //int count = 0;
   //int i = 0;
   int hit = 0;

   boolean check = false;
   boolean error = false;


   //
   //  Check if proshop user requested that we skip the mship test (member exceeded max and proshop
   //  wants to override the violation).
   //
   //  If this skip, or any of the following skips are set, then we've already been through these tests.
   //
   if (skip < 2) {

      //
      //************************************************************************
      //  Normal request -
      //  Check any membership types for max rounds per week, month or year
      //************************************************************************
      //
      if (!parm.mship1.equals( "" ) ||
          !parm.mship2.equals( "" ) ||
          !parm.mship3.equals( "" ) ||
          !parm.mship4.equals( "" ) ||
          !parm.mship5.equals( "" ) ||
          !parm.mship6.equals( "" ) ||
          !parm.mship7.equals( "" ) ||
          !parm.mship8.equals( "" ) ||
          !parm.mship9.equals( "" ) ||
          !parm.mship10.equals( "" ) ||
          !parm.mship11.equals( "" ) ||
          !parm.mship12.equals( "" ) ||
          !parm.mship13.equals( "" ) ||
          !parm.mship14.equals( "" ) ||
          !parm.mship15.equals( "" ) ||
          !parm.mship16.equals( "" ) ||
          !parm.mship17.equals( "" ) ||
          !parm.mship18.equals( "" ) ||
          !parm.mship19.equals( "" ) ||
          !parm.mship20.equals( "" ) ||
          !parm.mship21.equals( "" ) ||
          !parm.mship22.equals( "" ) ||
          !parm.mship23.equals( "" ) ||
          !parm.mship24.equals( "" ) ||
          !parm.mship25.equals( "" )) {                // if at least one name exists then check number of rounds

         //
         // *******************************************************************************
         //  Check Membership Restrictions for Max Rounds
         // *******************************************************************************
         //
         check = checkMemship(con, parm, parm.day);      // go check

         if (check == true) {      // a member exceed the max allowed tee times per week/month/year

            parm.error_hdr = "Member Exceeded Limit";
            parm.error_msg = "Warning:  " + parm.player + " is a " + parm.mship + " member and has exceeded the " + 
                             "maximum number of tee times allowed for this " + parm.period + ".";
            parm.skip = 2;
            return(check);
         }
      }      // end of mship if
   }         // end of skip2 if


   if (skip < 3) {

      //
      //************************************************************************
      //  Check for max # of guests exceeded (per member or per tee time)
      //************************************************************************
      //
      if (parm.guests > 0) {      // if any guests were included

         error = checkGuests(parm, con, parm.guests);

         if (error == true) {

            parm.error_hdr = "Number of Guests Exceeded Limit";
            parm.error_msg = "Sorry, the maximum number of guests allowed for the time you are requesting " +
                             "is " +parm.grest_num+ " per " +parm.period+ ".<br><br>Restriction Name = " +parm.rest_name+ ".";
            parm.skip = 3;
            return(error);           // exit if error encountered and reported
         }
      }      // end of if guests

   }  // end of skip3 if


   if (skip < 4) {

      //
      // *******************************************************************************
      //  Check Member Restrictions
      // *******************************************************************************
      //
      check = checkMemRes(con, parm, parm.day);      // go check

      if (check == true) {          // if we hit on a restriction

         parm.error_hdr = "Member Restricted";
         parm.error_msg = "Sorry, <b>" + parm.player + "</b> is restricted from playing during this time.<br><br>" +
                          "This time slot has the following restriction: <b>" + parm.rest_name + "</b>.";
         parm.skip = 4;
         return(check);
      }

      if (club.equals( "cherryhills" )) {

         //
         // *******************************************************************************
         //  Cherry Hills - custom member type and membership restrictions
         // *******************************************************************************
         //
         check = checkCherryRes(parm);          // go check

         if (check == true) {          // if we hit on a restriction

            parm.error_hdr = "Player Not Allowed";
            parm.error_msg = "Sorry, one or more players are not allowed to be part of a tee time for this day and time.<br><br>";

            if (parm.day.equals( "Monday" ) || parm.day.equals( "Wednesday" ) || parm.day.equals( "Friday" )) {
               parm.error_msg += "A Member must be included when making the request more than 1 day in advance.";
            } else {
               if (parm.day.equals( "Tuesday" )) {
                  if (time > 1100) {
                     parm.error_msg += "A Member must be included when making the request more than 1 day in advance.";
                  } else {
                     parm.error_msg += "Only Spouses may make a request more than 1 day in advance for a tee time before 11 AM on Tuesdays.";
                  }
               } else {
                  if (parm.day.equals( "Thursday" )) {
                     if (time > 1000) {
                        parm.error_msg += "A Member must be included when making the request more than 1 day in advance.";
                     } else {
                        parm.error_msg += "Only Spouses may make a request more than 1 day in advance for a tee time before 10 AM on Thursdays.";
                     }
                  } else {
                     if (parm.day.equals( "Sunday" )) {
                        if (time > 1000) {
                           parm.error_msg += "A Member must be included when making the request more than 1 day in advance.";
                        } else {
                           parm.error_msg += "Only Members may be included in a tee time before 10 AM on Sundays.";
                        }
                     } else {       // Saturday or Holiday
                        if (time > 1100) {
                           parm.error_msg += "A Member must be included when making the request more than 1 day in advance.";
                        } else {
                           parm.error_msg += "Player not allowed to make a tee time more than 24 hours in advance on Saturdays and Holidays before 11 AM.";
                        }
                     }
                  }
               }
            }
           
            parm.skip = 4;
            return(check);
         }
      }

   }  // end of skip4 if (5-some check removed - skip 5)


   if (skip < 6) {

      //
      // *******************************************************************************
      //  Check Member Number restrictions
      //
      // *******************************************************************************
      //
      check = checkMemNum(con, parm, parm.day);      // go check

      if (check == true) {          // if we hit on a restriction

         parm.error_hdr = "Member Restricted by Member Number";
         parm.error_msg = "Sorry, ";

         if (!parm.pNum1.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum1 + "</b> ";
         }
         if (!parm.pNum2.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum2 + "</b> ";
         }
         if (!parm.pNum3.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum3 + "</b> ";
         }
         if (!parm.pNum4.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum4 + "</b> ";
         }
         if (!parm.pNum5.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum5 + "</b> ";
         }
         if (!parm.pNum6.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum6 + "</b> ";
         }
         if (!parm.pNum7.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum7 + "</b> ";
         }
         if (!parm.pNum8.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum8 + "</b> ";
         }
         if (!parm.pNum9.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum9 + "</b> ";
         }
         if (!parm.pNum10.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum10 + "</b> ";
         }
         if (!parm.pNum11.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum11 + "</b> ";
         }
         if (!parm.pNum12.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum12 + "</b> ";
         }
         if (!parm.pNum13.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum13 + "</b> ";
         }                                                 
         if (!parm.pNum14.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum14 + "</b> ";
         }
         if (!parm.pNum15.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum15 + "</b> ";
         }
         if (!parm.pNum16.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum16 + "</b> ";
         }
         if (!parm.pNum17.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum17 + "</b> ";
         }
         if (!parm.pNum18.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum18 + "</b> ";
         }
         if (!parm.pNum19.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum19 + "</b> ";
         }
         if (!parm.pNum20.equals( "" )) {
            parm.error_msg +="<b>" + parm.pNum20 + "</b> ";
         }
         if (!parm.pNum21.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum21 + "</b> ";
         }
         if (!parm.pNum22.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum22 + "</b> ";
         }
         if (!parm.pNum23.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum23 + "</b> ";
         }
         if (!parm.pNum24.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum24 + "</b> ";
         }
         if (!parm.pNum25.equals( "" )) {
            parm.error_msg += "<b>" + parm.pNum25 + "</b> ";
         }
         parm.error_msg += "is/are restricted from playing during this time because the " +
                           "number of members with the same member number has exceeded the maximum allowed.<br><br>" +
                           "This time slot has the following restriction:  <b>" + parm.rest_name + "</b>.";
         parm.skip = 6;
         return(check);
      }
   }         // end of IF skip6


   if (skip < 7) {

      //
      //***********************************************************************************************
      //
      //    Now check if any of the players are already scheduled today (only 1 res per day) 
      //
      //***********************************************************************************************
      //
      hit = 0;

      if (!parm.player1.equals( "" ) && !parm.player1.equalsIgnoreCase( "x" ) && parm.g[0].equals( "" )) {

         player = parm.player1;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player2.equals( "" ) && !parm.player2.equalsIgnoreCase( "x" ) && parm.g[1].equals( "" ) && hit == 0) {

         player = parm.player2;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player3.equals( "" ) && !parm.player3.equalsIgnoreCase( "x" ) && parm.g[2].equals( "" ) && hit == 0) {

         player = parm.player3;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player4.equals( "" ) && !parm.player4.equalsIgnoreCase( "x" ) && parm.g[3].equals( "" ) && hit == 0) {

         player = parm.player4;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player5.equals( "" ) && !parm.player5.equalsIgnoreCase( "x" ) && parm.g[4].equals( "" ) && hit == 0) {

         player = parm.player5;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player6.equals( "" ) && !parm.player6.equalsIgnoreCase( "x" ) && parm.g[5].equals( "" ) && hit == 0) {

         player = parm.player6;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player7.equals( "" ) && !parm.player7.equalsIgnoreCase( "x" ) && parm.g[6].equals( "" ) && hit == 0) {

         player = parm.player7;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player8.equals( "" ) && !parm.player8.equalsIgnoreCase( "x" ) && parm.g[7].equals( "" ) && hit == 0) {

         player = parm.player8;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player9.equals( "" ) && !parm.player9.equalsIgnoreCase( "x" ) && parm.g[8].equals( "" ) && hit == 0) {

         player = parm.player9;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player10.equals( "" ) && !parm.player10.equalsIgnoreCase( "x" ) && parm.g[9].equals( "" ) && hit == 0) {

         player = parm.player10;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player11.equals( "" ) && !parm.player11.equalsIgnoreCase( "x" ) && parm.g[10].equals( "" ) && hit == 0) {

         player = parm.player11;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player12.equals( "" ) && !parm.player12.equalsIgnoreCase( "x" ) && parm.g[11].equals( "" ) && hit == 0) {

         player = parm.player12;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player13.equals( "" ) && !parm.player13.equalsIgnoreCase( "x" ) && parm.g[12].equals( "" ) && hit == 0) {

         player = parm.player13;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player14.equals( "" ) && !parm.player14.equalsIgnoreCase( "x" ) && parm.g[13].equals( "" ) && hit == 0) {

         player = parm.player14;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player15.equals( "" ) && !parm.player15.equalsIgnoreCase( "x" ) && parm.g[14].equals( "" ) && hit == 0) {

         player = parm.player15;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player16.equals( "" ) && !parm.player16.equalsIgnoreCase( "x" ) && parm.g[15].equals( "" ) && hit == 0) {

         player = parm.player16;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player17.equals( "" ) && !parm.player17.equalsIgnoreCase( "x" ) && parm.g[16].equals( "" ) && hit == 0) {

         player = parm.player17;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player18.equals( "" ) && !parm.player18.equalsIgnoreCase( "x" ) && parm.g[17].equals( "" ) && hit == 0) {

         player = parm.player18;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player19.equals( "" ) && !parm.player19.equalsIgnoreCase( "x" ) && parm.g[18].equals( "" ) && hit == 0) {

         player = parm.player19;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player20.equals( "" ) && !parm.player20.equalsIgnoreCase( "x" ) && parm.g[19].equals( "" ) && hit == 0) {

         player = parm.player20;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player21.equals( "" ) && !parm.player21.equalsIgnoreCase( "x" ) && parm.g[20].equals( "" ) && hit == 0) {

         player = parm.player21;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player22.equals( "" ) && !parm.player22.equalsIgnoreCase( "x" ) && parm.g[21].equals( "" ) && hit == 0) {

         player = parm.player22;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player23.equals( "" ) && !parm.player23.equalsIgnoreCase( "x" ) && parm.g[22].equals( "" ) && hit == 0) {

         player = parm.player23;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player24.equals( "" ) && !parm.player24.equalsIgnoreCase( "x" ) && parm.g[23].equals( "" ) && hit == 0) {

         player = parm.player24;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player25.equals( "" ) && !parm.player25.equalsIgnoreCase( "x" ) && parm.g[24].equals( "" ) && hit == 0) {

         player = parm.player25;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (hit > 0) {          // if we hit on a duplicate res

         parm.error_hdr = "Member Already Playing";
         parm.error_msg = "Sorry, <b>" + player + "</b> is already scheduled to play on this date.<br><br>";

         if (hit == 1) {
            parm.error_msg += "The player is already scheduled the maximum number of times allowed per day. ";
         } else {
            if (hit == 2) {
               parm.error_msg += "The player has another tee time that is too close to the time requested. ";
            } else {
               parm.error_msg += "The player has another request that is too close to the time of this request. ";
            }
         }
         parm.skip = 7;
         check = true;
         return(check);
      }
   }         // end of IF skip7

   //
   //  Check for minimum number of members & players in request
   //
   if (skip < 8 && processCheck == false) {

      check = chkminPlayer(con, parm, parm.lottName);

      if (check == true) {          // if we hit on an error

         return(check);
      }
   }         // end of IF skip8

   //
   //  If we fall through to here, then everything is ok - return ok status
   //
   check = false;
   return(check);

 }       // end of checkRests



 // *******************************************************************************
 //  Check Guests per Member or per Tee Time (skip3)
 // *******************************************************************************
 //
 public static boolean checkGuests(parmLott parm, Connection con, int guests) {


   ResultSet rs = null;

   boolean error = false;
   boolean check = false;

   int i = 0;
   int i2 = 0;
   int fb = parm.fb;
   int grest_num = 0;
   int guestsg1 = 0;
   int guestsg2 = 0;
   int guestsg3 = 0;
   int guestsg4 = 0;
   int guestsg5 = 0;
   int grest_id = 0;

   String gname = "";
   String sfb = "";
   String grest_recurr = "";
   String rcourse = "";
   String rest_fb = "";
   String per = "";             // restriction is per 'Member' or per 'Tee Time'
   String errMsg = "DB Error in Common_Lott.checkGuests.";
     
   //String [] rguest = new String [36];    // array to hold the Guest Restriction guest names
   ArrayList<String> rguest = new ArrayList<String>();

   if (fb == 0) {                   // is Tee time for Front 9?

      sfb = "Front";
   }

   if (fb == 1) {                   // is it Back 9?

      sfb = "Back";
   }

   try {

      //
      //  Process each guest restriction for this date and time
      //
      PreparedStatement pstmt5 = con.prepareStatement (
         "SELECT * " +
         "FROM guestres2 WHERE sdate <= ? AND edate >= ? AND " +
         "stime <= ? AND etime >= ? AND activity_id = 0");

      pstmt5.clearParameters();        // clear the parms
      pstmt5.setLong(1, parm.date);
      pstmt5.setLong(2, parm.date);
      pstmt5.setInt(3, parm.time);
      pstmt5.setInt(4, parm.time);
      rs = pstmt5.executeQuery();      // execute the prepared stmt

      loop1:
      while (rs.next()) {

         gname = rs.getString("name");
         grest_recurr = rs.getString("recurr");
         grest_id = rs.getInt("id");
         grest_num = rs.getInt("num_guests");
         rcourse = rs.getString("courseName");
         rest_fb = rs.getString("fb");
         per = rs.getString("per");

         rguest.clear();

         // now look up the guest types for this restriction
         PreparedStatement pstmt2 = con.prepareStatement (
                 "SELECT guest_type FROM guestres2_gtypes WHERE guestres_id = ?");

         pstmt2.clearParameters();
         pstmt2.setInt(1, rs.getInt("id"));

         ResultSet rs2 = pstmt2.executeQuery();

         while ( rs2.next() ) {

            rguest.add(rs2.getString("guest_type"));

         }

         pstmt2.close();

         check = false;       // init 'check guests' flag

         //
         //  Check if course matches that specified in restriction
         //
         if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( parm.course ))) {

            
            //
            // Make sure restriction isn't suspended
            //
            if (!verifySlot.checkRestSuspend(-99, grest_id, 0, (int)parm.date, parm.time, parm.day, parm.course, con)) {

                //
                //  We must check the recurrence for this day (Monday, etc.) and guest types
                //
                //     parm.g[x] = guest types specified in player name fields
                //     rguest[x] = guest types from restriction gotten above
                //
                if (grest_recurr.equalsIgnoreCase( "every " + parm.day )) {

                   if ((rest_fb.equals( "Both" )) || (rest_fb.equals( sfb ))) {

                      i = 0;
                      while (i < 25) {                        // check all possible players

                         if (!parm.g[i].equals( "" )) {      // if player is a guest

                            i2 = 0;
                            gloop1:
                            while (i2 < rguest.size()) {     // check all guest types for this restriction

                               if ( rguest.get(i2).equals( parm.g[i] )) {

                                  check = true;                  // indicate check num of guests
                                  break gloop1;
                               }
                               i2++;
                            }
                         }
                         i++;
                      }
                   }
                }

                if (grest_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                   if ((rest_fb.equals( "Both" )) || (rest_fb.equals( sfb ))) {

                      i = 0;
                      while (i < 25) {                        // check all possible players

                         if (!parm.g[i].equals( "" )) {      // if player is a guest

                            i2 = 0;
                            gloop2:
                            while (i2 < rguest.size()) {     // check all guest types for this restriction

                               if ( rguest.get(i2).equals( parm.g[i] )) {

                                  check = true;                  // indicate check num of guests
                                  break gloop2;
                               }
                               i2++;
                            }
                         }
                         i++;
                      }
                   }
                }

                if ((grest_recurr.equalsIgnoreCase( "all weekdays" )) &&
                    (!parm.day.equalsIgnoreCase( "saturday" )) &&
                    (!parm.day.equalsIgnoreCase( "sunday" ))) {

                   if ((rest_fb.equals( "Both" )) || (rest_fb.equals( sfb ))) {

                      i = 0;
                      while (i < 25) {                        // check all possible players

                         if (!parm.g[i].equals( "" )) {      // if player is a guest

                            i2 = 0;
                            gloop3:
                            while (i2 < rguest.size()) {     // check all guest types for this restriction

                               if ( rguest.get(i2).equals( parm.g[i] )) {

                                  check = true;                  // indicate check num of guests
                                  break gloop3;
                               }
                               i2++;
                            }
                         }
                         i++;
                      }
                   }
                }

                //
                //  if Weekends and its Saturday
                //
                if ((grest_recurr.equalsIgnoreCase( "all weekends" )) &&
                    (parm.day.equalsIgnoreCase( "saturday" ))) {

                   if ((rest_fb.equals( "Both" )) || (rest_fb.equals( sfb ))) {

                      i = 0;
                      while (i < 25) {                        // check all possible players

                         if (!parm.g[i].equals( "" )) {      // if player is a guest

                            i2 = 0;
                            gloop4:
                            while (i2 < rguest.size()) {     // check all guest types for this restriction

                               if ( rguest.get(i2).equals( parm.g[i] )) {

                                  check = true;                  // indicate check num of guests
                                  break gloop4;
                               }
                               i2++;
                            }
                         }
                         i++;
                      }
                   }
                }

                //
                //  if Weekends and its Sunday
                //
                if ((grest_recurr.equalsIgnoreCase( "all weekends" )) &&
                    (parm.day.equalsIgnoreCase( "sunday" ))) {

                   if ((rest_fb.equals( "Both" )) || (rest_fb.equals( sfb ))) {

                      i = 0;
                      while (i < 25) {                        // check all possible players

                         if (!parm.g[i].equals( "" )) {      // if player is a guest

                            i2 = 0;
                            gloop5:
                            while (i2 < rguest.size()) {     // check all guest types for this restriction

                               if ( rguest.get(i2).equals( parm.g[i] )) {

                                  check = true;                  // indicate check num of guests
                                  break gloop5;
                               }
                               i2++;
                            }
                         }
                         i++;
                      }
                   }
                }
            }
         }      // end of IF course matches

         if (check == true) {   // if restriction exists for this day and time

            //
            //  Restriction found and at least one restricted guest type exists in the request
            //
            //       to get here guests is > 0
            //       grest_num is 0 - 4 (max allowed)
            //       parm.memg1-5 is the number of members in each group/slot
            //       parm.g[x] = guest types specified in player name fields
            //
            float xguests = 0;               // number of guests per member requested
            error = false;                   // init error flag
              
            guestsg1 = 0;                     // init guest counts for each group (# of restricted guests)
            guestsg2 = 0;
            guestsg3 = 0;
            guestsg4 = 0;
            guestsg5 = 0;
              
            int grpSize = 4;                  // default to 4-somes
            int iGst = 0;
            int iGrp = 0;
            int iReq = 0;

            if (parm.p5.equals( "Yes" )) {   // if 5-somes

               grpSize = 5;            
            }

            //
            //  First count the number of restricted guests in each group of the request
            //
            for (iGrp=0; iGrp<grpSize; iGrp++) {           // check 1st group

               if (!parm.g[iReq].equals( "" )) {           // if player is a guest

                  rloop1:
                  for (iGst=0; iGst < rguest.size(); iGst++) {             // check restricted guest types

                     if (parm.g[iReq].equals( rguest.get(iGst) )) {    // if guest type matches restricted guest

                        guestsg1++;                           // increment # of restricted guests found
                        break rloop1;
                     }
                  }
               }

               iReq++;                                  // next player
            }
                 
            for (iGrp=0; iGrp<grpSize; iGrp++) {           // check 2nd group

               if (!parm.g[iReq].equals( "" )) {           // if player is a guest

                  rloop2:
                  for (iGst=0; iGst < rguest.size(); iGst++) {             // check restricted guest types

                     if (parm.g[iReq].equals( rguest.get(iGst) )) {    // if guest type matches restricted guest

                        guestsg2++;                           // increment # of restricted guests found
                        break rloop2;
                     }
                  }
               }

               iReq++;                                  // next player
            }

            for (iGrp=0; iGrp<grpSize; iGrp++) {           // check 3rd group

               if (!parm.g[iReq].equals( "" )) {           // if player is a guest

                  rloop3:
                  for (iGst=0; iGst < rguest.size(); iGst++) {             // check restricted guest types

                     if (parm.g[iReq].equals( rguest.get(iGst) )) {    // if guest type matches restricted guest

                        guestsg3++;                           // increment # of restricted guests found
                        break rloop3;
                     }
                  }
               }

               iReq++;                                  // next player
            }

            for (iGrp=0; iGrp<grpSize; iGrp++) {           // check 4th group

               if (!parm.g[iReq].equals( "" )) {           // if player is a guest

                  rloop4:
                  for (iGst=0; iGst < rguest.size(); iGst++) {             // check restricted guest types

                     if (parm.g[iReq].equals( rguest.get(iGst) )) {    // if guest type matches restricted guest

                        guestsg4++;                           // increment # of restricted guests found
                        break rloop4;
                     }
                  }
               }

               iReq++;                                  // next player
            }

            for (iGrp=0; iGrp<grpSize; iGrp++) {           // check 5th group

               if (!parm.g[iReq].equals( "" )) {           // if player is a guest

                  rloop5:
                  for (iGst=0; iGst < rguest.size(); iGst++) {             // check restricted guest types

                     if (parm.g[iReq].equals( rguest.get(iGst) )) {    // if guest type matches restricted guest

                        guestsg5++;                           // increment # of restricted guests found
                        break rloop5;
                     }
                  }
               }

               iReq++;                                  // next player
            }

  
            //
            //  Now check if the restriction was violated
            //
            if (per.equals( "Member" )) {       // if restriction is per member

               if ((parm.memg1 == 0 && guestsg1 > 0) || (parm.memg2 == 0 && guestsg2 > 0) ||
                   (parm.memg3 == 0 && guestsg3 > 0) || (parm.memg4 == 0 && guestsg4 > 0) ||
                   (parm.memg5 == 0 && guestsg5 > 0)) {

                  error = true;      // guest(s) in a group with no member
                  break loop1;

               } else {

                  if (parm.memg1 > 0 && guestsg1 > 0) {    // if member and guest in group

                     xguests = guestsg1 / parm.memg1;      // # of guests per member (in slot)

                     if (xguests > grest_num) {         // too many guests per member?

                        error = true;
                        break loop1;
                     }
                  }
                  if (parm.memg2 > 0 && guestsg2 > 0) {

                     xguests = guestsg2 / parm.memg2;      // # of guests per member (in slot)

                     if (xguests > grest_num) {         // too many guests per member?

                        error = true;
                        break loop1;
                     }
                  }
                  if (parm.memg3 > 0 && guestsg3 > 0) {

                     xguests = guestsg3 / parm.memg3;      // # of guests per member (in slot)

                     if (xguests > grest_num) {         // too many guests per member?

                        error = true;
                        break loop1;
                     }
                  }
                  if (parm.memg4 > 0 && guestsg4 > 0) {

                     xguests = guestsg4 / parm.memg4;      // # of guests per member (in slot)

                     if (xguests > grest_num) {         // too many guests per member?

                        error = true;
                        break loop1;
                     }
                  }
                  if (parm.memg5 > 0 && guestsg5 > 0) {

                     xguests = guestsg5 / parm.memg5;      // # of guests per member (in slot)

                     if (xguests > grest_num) {         // too many guests per member?

                        error = true;
                        break loop1;
                     }
                  }
               }
            } else {         // restriction is per tee time

               if (guestsg1 > grest_num || guestsg2 > grest_num || guestsg3 > grest_num ||
                   guestsg4 > grest_num || guestsg5 > grest_num) {

                  error = true;
                  break loop1;
               }
            }
         }
           
      }   // end of loop1 WHILE loop

      pstmt5.close();
        
      parm.grest_num = grest_num;          // save in case of error
      parm.period = per;
      parm.rest_name = gname;

   }
   catch (Exception e5) {

      dbError(errMsg, e5);
   }

   return(error);
 }


 // *******************************************************************************
 //  Check membership restrictions - max rounds per week, month or year
 // *******************************************************************************
 //
 public static boolean checkMemship(Connection con, parmLott parm, String day) {


   ResultSet rs = null;

   boolean check = false;

   //String rest_name = "";
   //String rest_recurr = "";
   //String rest_course = "";
   //String rest_fb = "";
   //String sfb = "";
   String mship = "";
   String player = "";
   String period = "";
   String mperiod = "";
   //String course = parm.course;
   String errMsg = "";

   //int rest_stime = 0;
   //int rest_etime = 0;
   //int mems = 0;
   int mtimes = 0;
   int ind = 0;
   int i = 0;
   //int time = parm.time;
   int year = 0;
   int month = 0;
   int dayNum = 0;
   int count = 0;
   int mm = parm.mm;
   int yy = parm.yy;
   int dd = parm.dd;

   long date = parm.date;
   long dateEnd = 0;
   long dateStart = 0;

   //
   //  parm block to hold the club parameters
   //
   parmClub parmc = new parmClub(0, con); // since lotteries are not supported in FlxRez let's hard code the root activity_id to zero

   int [] mtimesA = new int [parmc.MAX_Mships+1];          // array to hold the membership time values
   String [] mshipA = new String [parmc.MAX_Mships+1];     // array to hold the membership names
   String [] periodA = new String [parmc.MAX_Mships+1];    // array to hold the membership periods

    //
    //  verfify the input parms
    //
    if (con == null) {

       errMsg = "Error in Common_Lott.checkMemship - Connection is null.";
       pgError(errMsg);
    }
    if (parm == null) {

       errMsg = "Error in Common_Lott.checkMemship - parmLott is null.";
       pgError(errMsg);
    }
    if (day == null) {

       errMsg = "Error in Common_Lott.checkMemship - Day is null.";
       pgError(errMsg);
    }

    //
    //  Get this date's calendar and then determine start and end of week.
    //
    int calmm = mm - 1;                            // adjust month value for cal

    Calendar cal = new GregorianCalendar();       // get todays date

    //
    //  set cal to tee time's date
    //
    cal.set(Calendar.YEAR,yy);               // set year in cal
    cal.set(Calendar.MONTH,calmm);                // set month in cal
    cal.set(Calendar.DAY_OF_MONTH,dd);       // set day in cal

    ind = cal.get(Calendar.DAY_OF_WEEK);          // day of week (01 - 07)
    ind = 7 - ind;                                // number of days to end of week

    //
    // roll cal ahead to find Saturday's date (end of week)
    //
    if (ind != 0) {                               // if not today

       cal.add(Calendar.DATE,ind);                // roll ahead (ind) days
    }

    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    dayNum = cal.get(Calendar.DAY_OF_MONTH);

    month = month + 1;                            // month starts at zero

    dateEnd = year * 10000;                       // create a date field of yyyymmdd
    dateEnd = dateEnd + (month * 100);
    dateEnd = dateEnd + dayNum;                      // date = yyyymmdd (for comparisons)

    //
    // roll cal back 6 days to find Sunday's date (start of week)
    //
    cal.add(Calendar.DATE,-6);                    // roll back 6 days

    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    dayNum = cal.get(Calendar.DAY_OF_MONTH);

    month = month + 1;                            // month starts at zero

    dateStart = year * 10000;                     // create a date field of yyyymmdd
    dateStart = dateStart + (month * 100);
    dateStart = dateStart + dayNum;                  // date = yyyymmdd (for comparisons)

    //
    //  Init the mships
    //
    for (i=0; i<parmc.MAX_Mships+1; i++) {
       mshipA[i] = "";
       mtimesA[i] = 0;
       periodA[i] = "";
    }
     
  
    //
    //  Get membership types, number of rounds and time periods (week, month, year)
    //
    try {

       errMsg = "Error in Common_Lott.checkMemship - getting mships.";

       Statement stmt = con.createStatement();

       rs = stmt.executeQuery("SELECT mship, mtimes, period FROM mship5 WHERE activity_id = 0 LIMIT " + parmc.MAX_Mships);

       i = 1;

       while (rs.next()) {

           mshipA[i] = rs.getString("mship");
           mtimesA[i] = rs.getInt("mtimes");
           periodA[i] = rs.getString("period");

           i++;
       }
 
       errMsg = "Error in Common_Lott.checkMemship - checking each player.";

       //
       //   Check each player's mship
       //
       if (!parm.mship1.equals( "" )) {          // check if player 1 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop1:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship1.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop1;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user1, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship1;
                player = parm.player1;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 1 if

       if (!parm.mship2.equals( "" )) {          // check if player 2 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop2:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship2.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop2;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user2, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship2;
                player = parm.player2;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 2 if

       if (!parm.mship3.equals( "" )) {          // check if player 3 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop3:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship3.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop3;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user3, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship3;
                player = parm.player3;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 3 if

       if (!parm.mship4.equals( "" )) {          // check if player 4 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop4:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship4.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop4;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user4, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship4;
                player = parm.player4;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 4 if

       if (!parm.mship5.equals( "" )) {          // check if player 5 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop5:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship5.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop5;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user5, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship5;
                player = parm.player5;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 5 if

       if (!parm.mship6.equals( "" )) {          // check if player 6 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop6:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship6.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop6;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user6, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship6;
                player = parm.player6;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 6 if

       if (!parm.mship7.equals( "" )) {          // check if player 7 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop7:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship7.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop7;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user7, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship7;
                player = parm.player7;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 7 if

       if (!parm.mship8.equals( "" )) {          // check if player 8 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop8:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship8.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop8;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user8, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship8;
                player = parm.player8;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 8 if

       if (!parm.mship9.equals( "" )) {          // check if player 9 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop9:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship9.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop9;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user9, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship9;
                player = parm.player9;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 9 if

       if (!parm.mship10.equals( "" )) {          // check if player 10 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop10:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship10.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop10;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user10, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship10;
                player = parm.player10;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 10 if

       if (!parm.mship11.equals( "" )) {          // check if player 11 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop11:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship11.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop11;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user11, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship11;
                player = parm.player11;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 11 if

       if (!parm.mship12.equals( "" )) {          // check if player 12 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop12:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship12.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop12;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user12, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship12;
                player = parm.player12;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 12 if

       if (!parm.mship13.equals( "" )) {          // check if player 13 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop13:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship13.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop13;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user13, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship13;
                player = parm.player13;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 13 if

       if (!parm.mship14.equals( "" )) {          // check if player 14 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop14:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship14.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop14;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user14, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship14;
                player = parm.player14;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 14 if

       if (!parm.mship15.equals( "" )) {          // check if player 15 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop15:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship15.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop15;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user15, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship15;
                player = parm.player15;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 15 if

       if (!parm.mship16.equals( "" )) {          // check if player 16 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop16:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship16.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop16;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user16, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship16;
                player = parm.player16;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 16 if

       if (!parm.mship17.equals( "" )) {          // check if player 17 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop17:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship17.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop17;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user17, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship17;
                player = parm.player17;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 17 if

       if (!parm.mship18.equals( "" )) {          // check if player 18 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop18:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship18.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop18;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user18, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship18;
                player = parm.player18;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 18 if

       if (!parm.mship19.equals( "" )) {          // check if player 19 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop19:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship19.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop19;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user19, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship19;
                player = parm.player19;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 19 if

       if (!parm.mship20.equals( "" )) {          // check if player 20 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop20:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship20.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop20;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user20, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship20;
                player = parm.player20;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 20 if

       if (!parm.mship21.equals( "" )) {          // check if player 21 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop21:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship21.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop21;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user21, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship21;
                player = parm.player21;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 21 if

       if (!parm.mship22.equals( "" )) {          // check if player 22 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop22:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship22.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop22;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user22, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship22;
                player = parm.player22;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 22 if

       if (!parm.mship23.equals( "" )) {          // check if player 23 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop23:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship23.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop23;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user23, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship23;
                player = parm.player23;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 23 if

       if (!parm.mship24.equals( "" )) {          // check if player 24 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop24:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship24.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop24;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user24, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship24;
                player = parm.player24;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 24 if

       if (!parm.mship25.equals( "" )) {          // check if player 25 name specified

          ind = 1;             // init fields
          mtimes = 0;
          mperiod = "";

          loop25:
          while (ind < parmc.MAX_Mships+1) {

             if (parm.mship25.equals( mshipA[ind] )) {

                mtimes = mtimesA[ind];            // match found - get number of rounds
                mperiod = periodA[ind];           //               and period (week, month, year)
                break loop25;
             }
             ind++;
          }

          if (mtimes != 0) {             // if match found for this player and there is a limit

             count = checkRounds(con, mperiod, parm.user25, date, dateStart, dateEnd, mm, yy);

             //
             //  Compare # of tee times in this period with max allowed for membership type
             //
             if (count >= mtimes)  {

                check = true;                // reject this member
                mship = parm.mship25;
                player = parm.player25;
                period = mperiod;
             }
          }          // end of IF match found for player
       }          // end of player 25 if

   }
   catch (Exception e7) {

     dbError(errMsg, e7);
   }

   //
   //  save parms if error
   //
   parm.player = player;
   parm.mship= mship;
   parm.period = period;
     
   return(check);

 }         // end of checkMemRes



 // *******************************************************************************
 //  Check member restrictions
 //
 //     First, find all restrictions within date & time constraints on this course.
 //     Then, find the ones for this day.
 //     Then, find any for this member type or membership type (all 25 possible players).
 //
 // *******************************************************************************
 //
 public static boolean checkMemRes(Connection con, parmLott parm, String day) {


   ResultSet rs = null;

   boolean check = false;

   String rest_name = "";
   String rest_recurr = "";
   //String rest_course = "";
   String rest_fb = "";
   String sfb = "";
   String player = "";
   String course = parm.course;
   String errMsg = "";

   //int rest_stime = 0;
   //int rest_etime = 0;
   //int mems = 0;
   int ind = 0;
   int time = parm.time;
   int mrest_id = 0;

   long date = parm.date;

   String [] mtypeA = new String [8];     // array to hold the member type names
   String [] mshipA = new String [8];     // array to hold the membership names

   //
   //  verfify the input parms
   //
   if (con == null) {

      errMsg = "Error in Common_Lott.checkMemship - Connection is null.";
      pgError(errMsg);
   }
   if (parm == null) {

      errMsg = "Error in Common_Lott.checkMemship - parmLott is null.";
      pgError(errMsg);
   }
   if (day == null) {

      errMsg = "Error in Common_Lott.checkMemship - Day is null.";
      pgError(errMsg);
   }

   errMsg = "Error in Common_Lott.checkMemRes - getting restrictions.";

   try {

      check = false;                     // init 'hit' flag

      if (parm.fb == 0) {                   // is Tee time for Front 9?

         sfb = "Front";
      }

      if (parm.fb == 1) {                   // is it Back 9?

         sfb = "Back";
      }

      PreparedStatement pstmt7 = con.prepareStatement (
         "SELECT * FROM restriction2 WHERE sdate <= ? AND edate >= ? AND " +
         "stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-') AND activity_id = 0");


      pstmt7.clearParameters();          // clear the parms
      pstmt7.setLong(1, date);
      pstmt7.setLong(2, date);
      pstmt7.setInt(3, time);
      pstmt7.setInt(4, time);
      pstmt7.setString(5, course);

      rs = pstmt7.executeQuery();      // find all matching restrictions, if any

      loop2:
      while (rs.next()) {              // check all matching restrictions for this day, mship, mtype & F/B

         rest_name = rs.getString("name");
         rest_recurr = rs.getString("recurr");
         mrest_id = rs.getInt("id");
         mtypeA[0] = rs.getString("mem1");
         mtypeA[1] = rs.getString("mem2");
         mtypeA[2] = rs.getString("mem3");
         mtypeA[3] = rs.getString("mem4");
         mtypeA[4] = rs.getString("mem5");
         mtypeA[5] = rs.getString("mem6");
         mtypeA[6] = rs.getString("mem7");
         mtypeA[7] = rs.getString("mem8");
         mshipA[0] = rs.getString("mship1");
         mshipA[1] = rs.getString("mship2");
         mshipA[2] = rs.getString("mship3");
         mshipA[3] = rs.getString("mship4");
         mshipA[4] = rs.getString("mship5");
         mshipA[5] = rs.getString("mship6");
         mshipA[6] = rs.getString("mship7");
         mshipA[7] = rs.getString("mship8");
         rest_fb = rs.getString("fb");

         //
         //  We must check the recurrence for this day (Monday, etc.)
         //
         if ((rest_recurr.equals( "Every " + day )) ||               // if this day
             (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
             ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
               (!day.equalsIgnoreCase( "saturday" )) &&
               (!day.equalsIgnoreCase( "sunday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
              (day.equalsIgnoreCase( "saturday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
              (day.equalsIgnoreCase( "sunday" )))) {

            //
            //  Now check if F/B matches
            //
            if ((rest_fb.equals( "Both" )) || (rest_fb.equals( sfb ))) {

               check = false;
               
               //
               // Make sure restriction isn't suspended
               //
               if (!verifySlot.checkRestSuspend(mrest_id, -99, 0, (int)parm.date, parm.time, parm.day, parm.course, con)) {

                   //
                   //  Found a restriction that matches date, time, day & F/B - check mtype & mship of each member player
                   //
                   if (!parm.mship1.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player1;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship1.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype1.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 1 restrictions if

                   if (!parm.mship2.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player2;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship2.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype2.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 2 restrictions if

                   if (!parm.mship3.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player3;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship3.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype3.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 3 restrictions if

                   if (!parm.mship4.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player4;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship4.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype4.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 4 restrictions if

                   if (!parm.mship5.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player5;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship5.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype5.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 5 restrictions if

                   if (!parm.mship6.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player6;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship6.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype6.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 6 restrictions if

                   if (!parm.mship7.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player7;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship7.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype7.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 7 restrictions if

                   if (!parm.mship8.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player8;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship8.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype8.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 8 restrictions if

                   if (!parm.mship9.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player9;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship9.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype9.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 9 restrictions if

                   if (!parm.mship10.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player10;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship10.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype10.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 10 restrictions if

                   if (!parm.mship11.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player11;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship11.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype11.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 11 restrictions if

                   if (!parm.mship12.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player12;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship12.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype12.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 12 restrictions if

                   if (!parm.mship13.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player13;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship13.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype13.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 13 restrictions if

                   if (!parm.mship14.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player14;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship14.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype14.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 14 restrictions if

                   if (!parm.mship15.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player15;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship15.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype15.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 15 restrictions if

                   if (!parm.mship16.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player16;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship16.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype16.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 16 restrictions if

                   if (!parm.mship17.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player17;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship17.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype17.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 17 restrictions if

                   if (!parm.mship18.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player18;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship18.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype18.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 18 restrictions if

                   if (!parm.mship19.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player19;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship19.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype19.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 19 restrictions if

                   if (!parm.mship20.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player20;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship20.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype20.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 20 restrictions if

                   if (!parm.mship21.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player21;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship21.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype21.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 21 restrictions if

                   if (!parm.mship22.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player22;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship22.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype22.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 22 restrictions if

                   if (!parm.mship23.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player23;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship23.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype23.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 23 restrictions if

                   if (!parm.mship24.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player24;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship24.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype24.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 24 restrictions if

                   if (!parm.mship25.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      player = parm.player25;                  // save current player name

                      while (ind < 8) {

                         if ((parm.mship25.equalsIgnoreCase( mshipA[ind] )) || (parm.mtype25.equalsIgnoreCase( mtypeA[ind] ))) {

                            check = true;        // match found
                            break loop2;
                         }
                         ind++;
                      }
                   }  // end of member 25 restrictions if
               }
            }     // end of IF F/B matches
         }     // end of 'day' if
      }       // end of while (no more restrictions)

      pstmt7.close();

   }
   catch (Exception e7) {

      dbError(errMsg, e7);
   }                

   //
   //  save parms if error
   //
   parm.player = player;
   parm.rest_name = rest_name;

   return(check);

 }         // end of checkMemRes


 // *******************************************************************************
 //  Check Member Number restrictions
 //
 //     First, find all restrictions within date & time constraints
 //     Then, find the ones for this day
 //     Then, check all players' member numbers against all others in the time period
 //
 // *******************************************************************************
 //
 public static boolean checkMemNum(Connection con, parmLott parm, String day) {


   ResultSet rs = null;

   boolean check = false;

   String rest_name = "";
   String rest_recurr = "";
   String rest_course = "";
   String rest_fb = "";
   String sfb = "";
   String course = parm.course;
   String errMsg = "DB Error in Common_Lott.checkMemNum.";
     
   int rest_stime = 0;
   int rest_etime = 0;
   int mems = 0;
   int ind = 0;
   int time = parm.time;
         
   long date = parm.date;


   try {

      PreparedStatement pstmt7b = con.prepareStatement (
         "SELECT name, stime, etime, recurr, courseName, fb, num_mems " +
         "FROM mnumres2 WHERE sdate <= ? AND edate >= ? AND " +
         "stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-')");


      pstmt7b.clearParameters();          // clear the parms
      pstmt7b.setLong(1, date);
      pstmt7b.setLong(2, date);
      pstmt7b.setInt(3, time);
      pstmt7b.setInt(4, time);
      pstmt7b.setString(5, course);

      rs = pstmt7b.executeQuery();      // find all matching restrictions, if any

      check = false;                    // init 'hit' flag
      ind = 0;                          // init matching member count

      if (parm.fb == 0) {                    // is Tee time for Front 9?

         sfb = "Front";
      }

      if (parm.fb == 1) {                    // is it Back 9?

         sfb = "Back";
      }

      loop3:
      while (rs.next()) {              // check all matching restrictions for this day & F/B

         rest_name = rs.getString("name");
         rest_stime = rs.getInt("stime");
         rest_etime = rs.getInt("etime");
         rest_recurr = rs.getString("recurr");
         rest_course = rs.getString("courseName");
         rest_fb = rs.getString("fb");
         mems = rs.getInt("num_mems");

         //
         //  We must check the recurrence for this day (Monday, etc.)
         //
         if ((rest_recurr.equals( "Every " + day )) ||               // if this day
             (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
             ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
               (!day.equalsIgnoreCase( "saturday" )) &&
               (!day.equalsIgnoreCase( "sunday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
              (day.equalsIgnoreCase( "saturday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
              (day.equalsIgnoreCase( "sunday" )))) {

            //
            //  Now check if F/B matches this tee time
            //
            if (rest_fb.equals( "Both" ) || rest_fb.equals( sfb )) {

               //
               //  Found a restriction that matches date, time, day, course & F/B - check each member player
               //
               //   Check Player 1
               //
               if (!parm.mNum1.equals( "" )) {           // if this player is a member and member number exists

                  ind = checkmNum(parm.mNum1, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum1 = parm.player1;  // save this player name for error msg

                  if (parm.mNum1.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum1.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }

               }  // end of member 1 restrictions if

               //
               //   Check Player 2
               //
               if ((check == false) && (!parm.mNum2.equals( "" ))) {   // if this player is a member

                  ind = checkmNum(parm.mNum2, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum2 = parm.player2;  // save this player name for error msg

                  if (parm.mNum2.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum2.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }

               }  // end of member 2 restrictions if

               //
               //   Check Player 3
               //
               if ((check == false) && (!parm.mNum3.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum3, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum3 = parm.player3;  // save this player name for error msg

                  if (parm.mNum3.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum3.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }

               }  // end of member 3 restrictions if

               //
               //   Check Player 4
               //
               if ((check == false) && (!parm.mNum4.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum4, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum4 = parm.player4;  // save this player name for error msg

                  if (parm.mNum4.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum4.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 4 restrictions if

               //
               //   Check Player 5
               //
               if ((check == false) && (!parm.mNum5.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum5, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum5 = parm.player5;  // save this player name for error msg

                  if (parm.mNum5.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum5.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 5 restrictions if

               //
               //   Check Player 6
               //
               if ((check == false) && (!parm.mNum6.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum6, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum6 = parm.player6;  // save this player name for error msg

                  if (parm.mNum6.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum6.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 6 restrictions if

               //
               //   Check player 7
               //
               if ((check == false) && (!parm.mNum7.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum7, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum7 = parm.player7;  // save this player name for error msg

                  if (parm.mNum7.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum7.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 7 restrictions if

               //
               //   Check Player 8
               //
               if ((check == false) && (!parm.mNum8.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum8, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum8 = parm.player8;  // save this player name for error msg

                  if (parm.mNum8.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum8.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 8 restrictions if

               //
               //   Check Player 9
               //
               if ((check == false) && (!parm.mNum9.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum9, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum9 = parm.player9;  // save this player name for error msg

                  if (parm.mNum9.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum9.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 9 restrictions if

               //
               //   Check Player 10
               //
               if ((check == false) && (!parm.mNum10.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum10, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum10 = parm.player10;  // save this player name for error msg

                  if (parm.mNum10.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum10.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 10 restrictions if

               //
               //   Check Player 11
               //
               if ((check == false) && (!parm.mNum11.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum11, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum11 = parm.player11;  // save this player name for error msg

                  if (parm.mNum11.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum11.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 11 restrictions if

               //
               //   Check Player 12
               //
               if ((check == false) && (!parm.mNum12.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum12, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum12 = parm.player12;  // save this player name for error msg

                  if (parm.mNum12.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum12.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 12 restrictions if

               //
               //   Check Player 13
               //
               if ((check == false) && (!parm.mNum13.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum13, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum13 = parm.player13;  // save this player name for error msg

                  if (parm.mNum13.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum13.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 13 restrictions if

               //
               //   Check Player 14
               //
               if ((check == false) && (!parm.mNum14.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum14, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum14 = parm.player14;  // save this player name for error msg

                  if (parm.mNum14.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum14.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 14 restrictions if

               //
               //   Check Player 15
               //
               if ((check == false) && (!parm.mNum15.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum15, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum15 = parm.player15;  // save this player name for error msg

                  if (parm.mNum15.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum15.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 15 restrictions if

               //
               //   Check Player 16
               //
               if ((check == false) && (!parm.mNum16.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum16, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum16 = parm.player16;  // save this player name for error msg

                  if (parm.mNum16.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum16.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 16 restrictions if

               //
               //   Check player 17
               //
               if ((check == false) && (!parm.mNum17.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum17, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum17 = parm.player17;  // save this player name for error msg

                  if (parm.mNum17.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum17.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 17 restrictions if

               //
               //   Check Player 18
               //
               if ((check == false) && (!parm.mNum18.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum18, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum18 = parm.player18;  // save this player name for error msg

                  if (parm.mNum18.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum18.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 18 restrictions if

               //
               //   Check Player 19
               //
               if ((check == false) && (!parm.mNum19.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum19, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum19 = parm.player19;  // save this player name for error msg

                  if (parm.mNum19.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum19.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 19 restrictions if

               //
               //   Check Player 20
               //
               if ((check == false) && (!parm.mNum20.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum20, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum20 = parm.player20;  // save this player name for error msg

                  if (parm.mNum20.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum20.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 20 restrictions if

               //
               //   Check Player 21
               //
               if ((check == false) && (!parm.mNum21.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum21, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum21 = parm.player21;  // save this player name for error msg

                  if (parm.mNum21.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum21.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 21 restrictions if

               //
               //   Check Player 22
               //
               if ((check == false) && (!parm.mNum22.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum22, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum22 = parm.player22;  // save this player name for error msg

                  if (parm.mNum22.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum22.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 22 restrictions if

               //
               //   Check Player 23
               //
               if ((check == false) && (!parm.mNum23.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum23, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum23 = parm.player23;  // save this player name for error msg

                  if (parm.mNum23.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  if (parm.mNum23.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 23 restrictions if

               //
               //   Check Player 24
               //
               if ((check == false) && (!parm.mNum24.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum24, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum24 = parm.player24;  // save this player name for error msg

                  if (parm.mNum24.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;    // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum24.equals( parm.mNum25 )) {

                     ind++;
                     parm.pNum25 = parm.player25;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }       // end of member 24 restrictions if

               //
               //   Check Player 25
               //
               if ((check == false) && (!parm.mNum25.equals( "" ))) {           // if this player is a member

                  ind = checkmNum(parm.mNum25, date, rest_etime, rest_stime, time, course, rest_fb, rest_course, con);

                  //
                  //  Now check if any other members in this tee time match
                  //
                  parm.pNum25 = parm.player25;  // save this player name for error msg

                  if (parm.mNum25.equals( parm.mNum1 )) {

                     ind++;
                     parm.pNum1 = parm.player1;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum2 )) {

                     ind++;
                     parm.pNum2 = parm.player2;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum3 )) {

                     ind++;
                     parm.pNum3 = parm.player3;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum4 )) {

                     ind++;
                     parm.pNum4 = parm.player4;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum5 )) {

                     ind++;
                     parm.pNum5 = parm.player5;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum6 )) {

                     ind++;
                     parm.pNum6 = parm.player6;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum7 )) {

                     ind++;
                     parm.pNum7 = parm.player7;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum8 )) {

                     ind++;
                     parm.pNum8 = parm.player8;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum9 )) {

                     ind++;
                     parm.pNum9 = parm.player9;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum10 )) {

                     ind++;
                     parm.pNum10 = parm.player10;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum11 )) {

                     ind++;
                     parm.pNum11 = parm.player11;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum12 )) {

                     ind++;
                     parm.pNum12 = parm.player12;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum13 )) {

                     ind++;
                     parm.pNum13 = parm.player13;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum14 )) {

                     ind++;
                     parm.pNum14 = parm.player14;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum15 )) {

                     ind++;
                     parm.pNum15 = parm.player15;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum16 )) {

                     ind++;
                     parm.pNum16 = parm.player16;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum17 )) {

                     ind++;
                     parm.pNum17 = parm.player17;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum18 )) {

                     ind++;
                     parm.pNum18 = parm.player18;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum19 )) {

                     ind++;
                     parm.pNum19 = parm.player19;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum20 )) {

                     ind++;
                     parm.pNum20 = parm.player20;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum21 )) {

                     ind++;
                     parm.pNum21 = parm.player21;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum22 )) {

                     ind++;
                     parm.pNum22 = parm.player22;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum23 )) {

                     ind++;
                     parm.pNum23 = parm.player23;  // match found for player - save for error msg
                  }
                  if (parm.mNum25.equals( parm.mNum24 )) {

                     ind++;
                     parm.pNum24 = parm.player24;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     check = true;      // max # exceeded - reject
                  }
               }  // end of member 25 restrictions if

               if (check == true ) {          // if restriction hit

                  break loop3;
               }
            }     // end of IF F/B matches
         }     // end of 'day' if
      }       // end of while (no more restrictions)

      pstmt7b.close();

   }
   catch (Exception e7) {

      dbError(errMsg, e7);
   }
     
   //
   //  save parms if error
   //
   parm.rest_name = rest_name;
         
   return(check);
 }          // end of member restriction tests


 // *******************************************************************************
 //  Check custom Cherry Hills member restrictions
 // *******************************************************************************
 //
 public static boolean checkCherryRes(parmLott parm) {


   boolean error = false;
   boolean go = false;

   //
   //  Allocate a new parm block for each tee time and call common method to process each.
   //
   parmSlot parm1 = new parmSlot();          // allocate a parm block for a single tee time

   //
   //  Setup the new single parm block
   //
   parm1.date = parm.date;
   parm1.time = parm.time;
   parm1.mm = parm.mm;
   parm1.yy = parm.yy;
   parm1.dd = parm.dd;
   parm1.course = parm.course;
   parm1.p5 = parm.p5;
   parm1.day = parm.day;
   parm1.oldPlayer1 = "";       // always empty from here
   parm1.oldPlayer2 = "";
   parm1.oldPlayer3 = "";
   parm1.oldPlayer4 = "";
   parm1.oldPlayer5 = "";
   parm1.fb = parm.fb;
   parm1.ind = parm.ind;      // index value
   parm1.sfb = parm.sfb;


   //
   //  Do all players, one group at a time
   //
   go = false;                             // init to 'No Go'

   if (parm.p5.equals( "Yes" )) {

      if (!parm.player1.equals( "" ) || !parm.player2.equals( "" ) || !parm.player3.equals( "" ) ||
          !parm.player4.equals( "" ) || !parm.player5.equals( "" )) {

         go = true;                // go process this group

         //
         //  set parms for first group
         //
         parm1.player1 = parm.player1;
         parm1.player2 = parm.player2;
         parm1.player3 = parm.player3;
         parm1.player4 = parm.player4;
         parm1.player5 = parm.player5;
         parm1.user1 = parm.user1;
         parm1.user2 = parm.user2;
         parm1.user3 = parm.user3;
         parm1.user4 = parm.user4;
         parm1.user5 = parm.user5;
         parm1.mship1 = parm.mship1;
         parm1.mship2 = parm.mship2;
         parm1.mship3 = parm.mship3;
         parm1.mship4 = parm.mship4;
         parm1.mship5 = parm.mship5;
         parm1.mtype1 = parm.mtype1;
         parm1.mtype2 = parm.mtype2;
         parm1.mtype3 = parm.mtype3;
         parm1.mtype4 = parm.mtype4;
         parm1.mtype5 = parm.mtype5;
         parm1.mNum1 = parm.mNum1;
         parm1.mNum2 = parm.mNum2;
         parm1.mNum3 = parm.mNum3;
         parm1.mNum4 = parm.mNum4;
         parm1.mNum5 = parm.mNum5;
      }

   } else {                       // 4-somes only

      if (!parm.player1.equals( "" ) || !parm.player2.equals( "" ) || !parm.player3.equals( "" ) ||
          !parm.player4.equals( "" )) {

         go = true;                // go process this group

         //
         //  set parms for first group
         //
         parm1.player1 = parm.player1;
         parm1.player2 = parm.player2;
         parm1.player3 = parm.player3;
         parm1.player4 = parm.player4;
         parm1.user1 = parm.user1;
         parm1.user2 = parm.user2;
         parm1.user3 = parm.user3;
         parm1.user4 = parm.user4;
         parm1.mship1 = parm.mship1;
         parm1.mship2 = parm.mship2;
         parm1.mship3 = parm.mship3;
         parm1.mship4 = parm.mship4;
         parm1.mtype1 = parm.mtype1;
         parm1.mtype2 = parm.mtype2;
         parm1.mtype3 = parm.mtype3;
         parm1.mtype4 = parm.mtype4;
         parm1.mNum1 = parm.mNum1;
         parm1.mNum2 = parm.mNum2;
         parm1.mNum3 = parm.mNum3;
         parm1.mNum4 = parm.mNum4;
         parm1.player5 = "";
         parm1.user5 = "";
         parm1.mship5 = "";
         parm1.mtype5 = "";
         parm1.mNum5 = "";
      }
   }

   if (go == true) {          // if players found

      error = verifySlot.checkCherryHills(parm1);    // process custom restrictions
   }

   if (error == false) {           // if we can keep going

      //
      //  Do 2nd group
      //
      go = false;                             // init to 'No Go'

      if (parm.p5.equals( "Yes" )) {

         if (!parm.player6.equals( "" ) || !parm.player7.equals( "" ) || !parm.player8.equals( "" ) ||
             !parm.player9.equals( "" ) || !parm.player10.equals( "" )) {

            go = true;                // go process this group

            //
            //  set parms for this group
            //
            parm1.player1 = parm.player6;
            parm1.player2 = parm.player7;
            parm1.player3 = parm.player8;
            parm1.player4 = parm.player9;
            parm1.player5 = parm.player10;
            parm1.user1 = parm.user6;
            parm1.user2 = parm.user7;
            parm1.user3 = parm.user8;
            parm1.user4 = parm.user9;
            parm1.user5 = parm.user10;
            parm1.mship1 = parm.mship6;
            parm1.mship2 = parm.mship7;
            parm1.mship3 = parm.mship8;
            parm1.mship4 = parm.mship9;
            parm1.mship5 = parm.mship10;
            parm1.mtype1 = parm.mtype6;
            parm1.mtype2 = parm.mtype7;
            parm1.mtype3 = parm.mtype8;
            parm1.mtype4 = parm.mtype9;
            parm1.mtype5 = parm.mtype10;
            parm1.mNum1 = parm.mNum6;
            parm1.mNum2 = parm.mNum7;
            parm1.mNum3 = parm.mNum8;
            parm1.mNum4 = parm.mNum9;
            parm1.mNum5 = parm.mNum10;
         }

      } else {                       // 4-somes only

         if (!parm.player5.equals( "" ) || !parm.player6.equals( "" ) || !parm.player7.equals( "" ) ||
             !parm.player8.equals( "" )) {

            go = true;                // go process this group

            //
            //  set parms for this group
            //
            parm1.player1 = parm.player5;
            parm1.player2 = parm.player6;
            parm1.player3 = parm.player7;
            parm1.player4 = parm.player8;
            parm1.user1 = parm.user5;
            parm1.user2 = parm.user6;
            parm1.user3 = parm.user7;
            parm1.user4 = parm.user8;
            parm1.mship1 = parm.mship5;
            parm1.mship2 = parm.mship6;
            parm1.mship3 = parm.mship7;
            parm1.mship4 = parm.mship8;
            parm1.mtype1 = parm.mtype5;
            parm1.mtype2 = parm.mtype6;
            parm1.mtype3 = parm.mtype7;
            parm1.mtype4 = parm.mtype8;
            parm1.mNum1 = parm.mNum5;
            parm1.mNum2 = parm.mNum6;
            parm1.mNum3 = parm.mNum7;
            parm1.mNum4 = parm.mNum8;
            parm1.player5 = "";
            parm1.user5 = "";
            parm1.mship5 = "";
            parm1.mtype5 = "";
            parm1.mNum5 = "";
         }
      }

      if (go == true) {          // if mships found

         error = verifySlot.checkCherryHills(parm1);    // process custom restrictions
      }

      if (error == false) {           // if we can keep going

         //
         //  Do 3rd group
         //
         go = false;                             // init to 'No Go'

         if (parm.p5.equals( "Yes" )) {

            if (!parm.player11.equals( "" ) || !parm.player12.equals( "" ) || !parm.player13.equals( "" ) ||
                !parm.player14.equals( "" ) || !parm.player15.equals( "" )) {

               go = true;                // go process this group

               //
               //  set parms for this group
               //
               parm1.player1 = parm.player11;
               parm1.player2 = parm.player12;
               parm1.player3 = parm.player13;
               parm1.player4 = parm.player14;
               parm1.player5 = parm.player15;
               parm1.user1 = parm.user11;
               parm1.user2 = parm.user12;
               parm1.user3 = parm.user13;
               parm1.user4 = parm.user14;
               parm1.user5 = parm.user15;
               parm1.mship1 = parm.mship11;
               parm1.mship2 = parm.mship12;
               parm1.mship3 = parm.mship13;
               parm1.mship4 = parm.mship14;
               parm1.mship5 = parm.mship15;
               parm1.mtype1 = parm.mtype11;
               parm1.mtype2 = parm.mtype12;
               parm1.mtype3 = parm.mtype13;
               parm1.mtype4 = parm.mtype14;
               parm1.mtype5 = parm.mtype15;
               parm1.mNum1 = parm.mNum11;
               parm1.mNum2 = parm.mNum12;
               parm1.mNum3 = parm.mNum13;
               parm1.mNum4 = parm.mNum14;
               parm1.mNum5 = parm.mNum15;
            }

         } else {                       // 4-somes only

            if (!parm.player9.equals( "" ) || !parm.player10.equals( "" ) || !parm.player11.equals( "" ) ||
                !parm.player12.equals( "" )) {

               go = true;                // go process this group

               //
               //  set parms for this group
               //
               parm1.player1 = parm.player9;
               parm1.player2 = parm.player10;
               parm1.player3 = parm.player11;
               parm1.player4 = parm.player12;
               parm1.user1 = parm.user9;
               parm1.user2 = parm.user10;
               parm1.user3 = parm.user11;
               parm1.user4 = parm.user12;
               parm1.mship1 = parm.mship9;
               parm1.mship2 = parm.mship10;
               parm1.mship3 = parm.mship11;
               parm1.mship4 = parm.mship12;
               parm1.mtype1 = parm.mtype9;
               parm1.mtype2 = parm.mtype10;
               parm1.mtype3 = parm.mtype11;
               parm1.mtype4 = parm.mtype12;
               parm1.mNum1 = parm.mNum9;
               parm1.mNum2 = parm.mNum10;
               parm1.mNum3 = parm.mNum11;
               parm1.mNum4 = parm.mNum12;
               parm1.player5 = "";
               parm1.user5 = "";
               parm1.mship5 = "";
               parm1.mtype5 = "";
               parm1.mNum5 = "";
            }
         }

         if (go == true) {          // if mships found

            error = verifySlot.checkCherryHills(parm1);    // process custom restrictions
         }

         if (error == false) {           // if we can keep going

            //
            //  Do 4th group
            //
            go = false;                             // init to 'No Go'

            if (parm.p5.equals( "Yes" )) {

               if (!parm.player16.equals( "" ) || !parm.player17.equals( "" ) || !parm.player18.equals( "" ) ||
                   !parm.player19.equals( "" ) || !parm.player20.equals( "" )) {

                  go = true;                // go process this group

                  //
                  //  set parms for this group
                  //
                  parm1.player1 = parm.player16;
                  parm1.player2 = parm.player17;
                  parm1.player3 = parm.player18;
                  parm1.player4 = parm.player19;
                  parm1.player5 = parm.player20;
                  parm1.user1 = parm.user16;
                  parm1.user2 = parm.user17;
                  parm1.user3 = parm.user18;
                  parm1.user4 = parm.user19;
                  parm1.user5 = parm.user20;
                  parm1.mship1 = parm.mship16;
                  parm1.mship2 = parm.mship17;
                  parm1.mship3 = parm.mship18;
                  parm1.mship4 = parm.mship19;
                  parm1.mship5 = parm.mship20;
                  parm1.mtype1 = parm.mtype16;
                  parm1.mtype2 = parm.mtype17;
                  parm1.mtype3 = parm.mtype18;
                  parm1.mtype4 = parm.mtype19;
                  parm1.mtype5 = parm.mtype20;
                  parm1.mNum1 = parm.mNum16;
                  parm1.mNum2 = parm.mNum17;
                  parm1.mNum3 = parm.mNum18;
                  parm1.mNum4 = parm.mNum19;
                  parm1.mNum5 = parm.mNum20;
               }

            } else {                       // 4-somes only

               if (!parm.player13.equals( "" ) || !parm.player14.equals( "" ) || !parm.player15.equals( "" ) ||
                   !parm.player16.equals( "" )) {

                  go = true;                // go process this group

                  //
                  //  set parms for this group
                  //
                  parm1.player1 = parm.player13;
                  parm1.player2 = parm.player14;
                  parm1.player3 = parm.player15;
                  parm1.player4 = parm.player16;
                  parm1.user1 = parm.user13;
                  parm1.user2 = parm.user14;
                  parm1.user3 = parm.user15;
                  parm1.user4 = parm.user16;
                  parm1.mship1 = parm.mship13;
                  parm1.mship2 = parm.mship14;
                  parm1.mship3 = parm.mship15;
                  parm1.mship4 = parm.mship16;
                  parm1.mtype1 = parm.mtype13;
                  parm1.mtype2 = parm.mtype14;
                  parm1.mtype3 = parm.mtype15;
                  parm1.mtype4 = parm.mtype16;
                  parm1.mNum1 = parm.mNum13;
                  parm1.mNum2 = parm.mNum14;
                  parm1.mNum3 = parm.mNum15;
                  parm1.mNum4 = parm.mNum16;
                  parm1.player5 = "";
                  parm1.user5 = "";
                  parm1.mship5 = "";
                  parm1.mtype5 = "";
                  parm1.mNum5 = "";
               }
            }

            if (go == true) {          // if mships found

               error = verifySlot.checkCherryHills(parm1);    // process custom restrictions

            }

            if (error == false) {           // if we can keep going

               //
               //  Do 5th group
               //
               go = false;                             // init to 'No Go'

               if (parm.p5.equals( "Yes" )) {

                  if (!parm.player21.equals( "" ) || !parm.player22.equals( "" ) || !parm.player23.equals( "" ) ||
                      !parm.player24.equals( "" ) || !parm.player25.equals( "" )) {

                     go = true;                // go process this group

                     //
                     //  set parms for this group
                     //
                     parm1.player1 = parm.player21;
                     parm1.player2 = parm.player22;
                     parm1.player3 = parm.player23;
                     parm1.player4 = parm.player24;
                     parm1.player5 = parm.player25;
                     parm1.user1 = parm.user21;
                     parm1.user2 = parm.user22;
                     parm1.user3 = parm.user23;
                     parm1.user4 = parm.user24;
                     parm1.user5 = parm.user25;
                     parm1.mship1 = parm.mship21;
                     parm1.mship2 = parm.mship22;
                     parm1.mship3 = parm.mship23;
                     parm1.mship4 = parm.mship24;
                     parm1.mship5 = parm.mship25;
                     parm1.mtype1 = parm.mtype21;
                     parm1.mtype2 = parm.mtype22;
                     parm1.mtype3 = parm.mtype23;
                     parm1.mtype4 = parm.mtype24;
                     parm1.mtype5 = parm.mtype25;
                     parm1.mNum1 = parm.mNum21;
                     parm1.mNum2 = parm.mNum22;
                     parm1.mNum3 = parm.mNum23;
                     parm1.mNum4 = parm.mNum24;
                     parm1.mNum5 = parm.mNum25;
                  }

               } else {                       // 4-somes only

                  if (!parm.player17.equals( "" ) || !parm.player18.equals( "" ) || !parm.player19.equals( "" ) ||
                      !parm.player20.equals( "" )) {

                     go = true;                // go process this group

                     //
                     //  set parms for this group
                     //
                     parm1.player1 = parm.player17;
                     parm1.player2 = parm.player18;
                     parm1.player3 = parm.player19;
                     parm1.player4 = parm.player20;
                     parm1.user1 = parm.user17;
                     parm1.user2 = parm.user18;
                     parm1.user3 = parm.user19;
                     parm1.user4 = parm.user20;
                     parm1.mship1 = parm.mship17;
                     parm1.mship2 = parm.mship18;
                     parm1.mship3 = parm.mship19;
                     parm1.mship4 = parm.mship20;
                     parm1.mtype1 = parm.mtype17;
                     parm1.mtype2 = parm.mtype18;
                     parm1.mtype3 = parm.mtype19;
                     parm1.mtype4 = parm.mtype20;
                     parm1.mNum1 = parm.mNum17;
                     parm1.mNum2 = parm.mNum18;
                     parm1.mNum3 = parm.mNum19;
                     parm1.mNum4 = parm.mNum20;
                     parm1.player5 = "";
                     parm1.user5 = "";
                     parm1.mship5 = "";
                     parm1.mtype5 = "";
                     parm1.mNum5 = "";
                  }
               }

               if (go == true) {          // if mships found

                  error = verifySlot.checkCherryHills(parm1);    // process custom restrictions

               }
            }
         }
      }
   }

   return(error);

 }         // end of checkCherryRes


 // *********************************************************
 // Check each member for # of rounds played in a period
 // *********************************************************

 public static int checkRounds(Connection con, String mperiod, String user, long date, long dateStart, long dateEnd, int mm, int yy) {


   ResultSet rs = null;

   int count = 0;

   try {
      //
      // statements for week
      //
      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT dd FROM teecurr2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND date >= ? AND date <= ?");

      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT dd FROM teepast2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND date >= ? AND date <= ?");
      //
      // statements for month
      //
      PreparedStatement pstmt2m = con.prepareStatement (
         "SELECT dd FROM teecurr2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND mm = ? AND yy = ?");

      PreparedStatement pstmt3m = con.prepareStatement (
         "SELECT dd FROM teepast2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND mm = ? AND yy = ?");
      //
      // statements for year
      //
      PreparedStatement pstmt2y = con.prepareStatement (
         "SELECT dd FROM teecurr2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND yy = ?");

      PreparedStatement pstmt3y = con.prepareStatement (
         "SELECT dd FROM teepast2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR " +
                    "username5 = ?) AND date != ? AND yy = ?");

      if (mperiod.equals( "Week" )) {       // if WEEK

         pstmt2.clearParameters();        // get count from teecurr
         pstmt2.setString(1, user);
         pstmt2.setString(2, user);
         pstmt2.setString(3, user);
         pstmt2.setString(4, user);
         pstmt2.setString(5, user);
         pstmt2.setLong(6, date);
         pstmt2.setLong(7, dateStart);
         pstmt2.setLong(8, dateEnd);
         rs = pstmt2.executeQuery();

         count = 0;

         while (rs.next()) {

            count++;                      // count number or tee times in this week
         }

         pstmt3.clearParameters();        // get count from teepast
         pstmt3.setString(1, user);
         pstmt3.setString(2, user);
         pstmt3.setString(3, user);
         pstmt3.setString(4, user);
         pstmt3.setString(5, user);
         pstmt3.setLong(6, date);
         pstmt3.setLong(7, dateStart);
         pstmt3.setLong(8, dateEnd);
         rs = pstmt3.executeQuery();

         while (rs.next()) {

            count++;                      // count number or tee times in this week
         }
      }       // end of IF mperiod = week

      if (mperiod.equals( "Month" )) {      // if MONTH

         pstmt2m.clearParameters();        // get count from teecurr
         pstmt2m.setString(1, user);
         pstmt2m.setString(2, user);
         pstmt2m.setString(3, user);
         pstmt2m.setString(4, user);
         pstmt2m.setString(5, user);
         pstmt2m.setLong(6, date);
         pstmt2m.setInt(7, mm);
         pstmt2m.setInt(8, yy);
         rs = pstmt2m.executeQuery();

         count = 0;

         while (rs.next()) {

            count++;                      // count number or tee times in this month
         }

         pstmt3m.clearParameters();        // get count from teepast
         pstmt3m.setString(1, user);
         pstmt3m.setString(2, user);
         pstmt3m.setString(3, user);
         pstmt3m.setString(4, user);
         pstmt3m.setString(5, user);
         pstmt3m.setLong(6, date);
         pstmt3m.setInt(7, mm);
         pstmt3m.setInt(8, yy);
         rs = pstmt3m.executeQuery();

         while (rs.next()) {

            count++;                         // count number or tee times in this month
         }
      }       // end of IF mperiod = Month

      if (mperiod.equals( "Year" )) {            // if Year

         pstmt2y.clearParameters();             // get count from teecurr
         pstmt2y.setString(1, user);
         pstmt2y.setString(2, user);
         pstmt2y.setString(3, user);
         pstmt2y.setString(4, user);
         pstmt2y.setString(5, user);
         pstmt2y.setLong(6, date);
         pstmt2y.setInt(7, mm);
         pstmt2y.setInt(8, yy);
         rs = pstmt2y.executeQuery();

         count = 0;

         while (rs.next()) {

            count++;                      // count number or tee times in this year
         }

         pstmt3y.clearParameters();        // get count from teepast
         pstmt3y.setString(1, user);
         pstmt3y.setString(2, user);
         pstmt3y.setString(3, user);
         pstmt3y.setString(4, user);
         pstmt3y.setString(5, user);
         pstmt3y.setLong(6, date);
         pstmt3y.setInt(7, mm);
         pstmt3y.setInt(8, yy);
         rs = pstmt3y.executeQuery();

         while (rs.next()) {

            count++;                      // count number or tee times in this year
         }
      }       // end of IF mperiod = Year

      pstmt2.close();
      pstmt3.close();
      pstmt2m.close();
      pstmt3m.close();
      pstmt2y.close();
      pstmt3y.close();

   }
   catch (Exception ignore) {
   }
   return count;
 }       // end of checkRounds


 // *********************************************************
 // Check for minimum number of players and members
 // *********************************************************

 public static boolean chkminPlayer(Connection con, parmLott parm, String lottName) {

   ResultSet rs = null;

   boolean hit = false;
   int minMembers = 0;
   int minPlayers = 0;
     

   try {
      //
      PreparedStatement pstmtl3 = con.prepareStatement (
               "SELECT members, players FROM lottery3 WHERE name = ?");

      pstmtl3.clearParameters();        // clear the parms
      pstmtl3.setString(1, lottName);       // put the parm in stmt
      rs = pstmtl3.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         minMembers = rs.getInt(1);    // minimum # of members per request
         minPlayers = rs.getInt(2);    // minimum # of Players per request
      }
      pstmtl3.close();              // close the stmt

      if (minMembers > 0) {

         if (parm.p5.equals( "No" )) {

            if (minMembers > 4) {

               minMembers = 4;         // reduce for 4-somes
            }
         }
         //
         //  Reject request if not enough members in request
         //
         if (parm.members < minMembers) {
           
            hit = true;

            parm.error_hdr = "Not Enough Members in Request";
            parm.error_msg = "Warning: Your request does not contain the minimum number of members required.<br><br>" +
                             "Your request contains " + parm.members + " members but you need " + minMembers + ".";
         
            return hit;
         }
      }
      if (minPlayers > 0) {

         if (parm.p5.equals( "No" )) {

            if (minPlayers > 4) {

               minPlayers = 4;         // reduce for 4-somes
            }
         }
         //
         //  Reject request if not enough Players in request
         //
         if (parm.players < minPlayers) {

            hit = true;

            parm.error_hdr = "Not Enough Players in Request";
            parm.error_msg = "Warning: Your request does not contain the minimum number of Players required.<br><br>" +
                             "Your request contains " + parm.players + " Players but you need " + minPlayers + ".";
            
            return hit;
         }
      }
   }
      catch (Exception ignore) {
   }
   return hit;
 }


 // *********************************************************
 // Check if player already scheduled
 // *********************************************************

 public static int chkPlayer(Connection con, String player, long date, int time, int fb, String course, long id) {


   ResultSet rs = null;

   int hit = 0;
   int time2 = 0;
   int fb2 = 0;
   int count = 0;
     
   long id2 = 0;

   String course2 = "";

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // since lotteries are not supported in FlxRez let's hard code the root activity_id to zero

   //
   //   Get the guest names specified for this club
   //
   try {
      getClub.getParms(con, parm);        // get the club parms

   }
   catch (Exception ignore) {
   }

   int max = parm.rnds;           // max allowed rounds per day for members (club option)
   int hrsbtwn = parm.hrsbtwn;    // minumum hours between tee times (club option when rnds > 1)


   try {

      PreparedStatement pstmt21 = con.prepareStatement (
         "SELECT time, fb, courseName FROM teecurr2 " +
         "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ?) AND date = ?");

      pstmt21.clearParameters();        // clear the parms and check player 1
      pstmt21.setString(1, player);
      pstmt21.setString(2, player);
      pstmt21.setString(3, player);
      pstmt21.setString(4, player);
      pstmt21.setString(5, player);
      pstmt21.setLong(6, date);
      rs = pstmt21.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         time2 = rs.getInt("time");
         fb2 = rs.getInt("fb");
         course2 = rs.getString("courseName");

         if ((time2 != time) || (fb2 != fb) || (!course2.equals( course ))) {      // if not this tee time

            count++;         // add to tee time counter for member

            //
            //  check if requested tee time is too close to this one
            //
            if (max > 1 && hrsbtwn > 0) {

               if (time2 < time) {            // if this tee time is before the time requested

                  if (time < (time2 + (hrsbtwn * 100))) {     // if this tee time is within range

                     hit = 2;                       // tee times not far enough apart
                  }

               } else {                                 // this time is after the requested time

                  if (time2 < (time + (hrsbtwn * 100))) {     // if this tee time is within range

                     hit = 2;                       // tee times not far enough apart
                  }
               }
            }
         }
      }
      pstmt21.close();
        
      //
      //  check if player already on a lottery request
      //
      PreparedStatement pstmt22 = con.prepareStatement (
         "SELECT time, id FROM lreqs3 " +
         "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ? OR " +
                "player6 = ? OR player7 = ? OR player8 = ? OR player9 = ? OR player10 = ? OR " +
                "player11 = ? OR player12 = ? OR player13 = ? OR player14 = ? OR player15 = ? OR " +
                "player16 = ? OR player17 = ? OR player18 = ? OR player19 = ? OR player20 = ? OR " +
                "player21 = ? OR player22 = ? OR player23 = ? OR player24 = ? OR player25 = ?) AND date = ?");

      pstmt22.clearParameters();        // clear the parms and check player 1
      pstmt22.setString(1, player);
      pstmt22.setString(2, player);
      pstmt22.setString(3, player);
      pstmt22.setString(4, player);
      pstmt22.setString(5, player);
      pstmt22.setString(6, player);
      pstmt22.setString(7, player);
      pstmt22.setString(8, player);
      pstmt22.setString(9, player);
      pstmt22.setString(10, player);
      pstmt22.setString(11, player);
      pstmt22.setString(12, player);
      pstmt22.setString(13, player);
      pstmt22.setString(14, player);
      pstmt22.setString(15, player);
      pstmt22.setString(16, player);
      pstmt22.setString(17, player);
      pstmt22.setString(18, player);
      pstmt22.setString(19, player);
      pstmt22.setString(20, player);
      pstmt22.setString(21, player);
      pstmt22.setString(22, player);
      pstmt22.setString(23, player);
      pstmt22.setString(24, player);
      pstmt22.setString(25, player);
      pstmt22.setLong(26, date);
      rs = pstmt22.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         time2 = rs.getInt("time");
         id2 = rs.getLong("id");

         if (id2 != id) {              // if not this req

            count++;         // add to tee time counter for member

            //
            //  check if requested tee time is too close to this one
            //
            if (max > 1 && hrsbtwn > 0) {

               if (time2 < time) {            // if this tee time is before the time requested

                  if (time < (time2 + (hrsbtwn * 100))) {     // if this tee time is within range

                     hit = 3;                       // tee times not far enough apart
                  }

               } else {                                 // this time is after the requested time

                  if (time2 < (time + (hrsbtwn * 100))) {     // if this tee time is within range

                     hit = 3;                       // tee times not far enough apart
                  }
               }
            }
         }
      }
      pstmt22.close();
        
      //
      //  See if we exceeded max allowed for day - if so, set indicator
      //
      if (count >= max) {

         hit = 1;                       // player already scheduled on this date (max times allowed)
      }

   }
   catch (Exception ignore) {
   }

   return hit;
 }


 // *********************************************************
 // Check Member Number Restrictions
 // *********************************************************

 public static int checkmNum(String mNum, long date, int rest_etime, int rest_stime, int time, String course, String rest_fb, String rest_course, Connection con) {


   ResultSet rs7 = null;

   int ind = 0;
   int time2 = 0;
   int t_fb = 0;

   String course2 = "";
   String sfb2 = "";
   String rmNum1 = "";
   String rmNum2 = "";
   String rmNum3 = "";
   String rmNum4 = "";
   String rmNum5 = "";

   try {

      PreparedStatement pstmt7c = con.prepareStatement (
         "SELECT time, fb, courseName, mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 " +
         "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND date = ? " +
         "AND time <= ? AND time >= ?");

      pstmt7c.clearParameters();        // clear the parms and check player 1
      pstmt7c.setString(1, mNum);
      pstmt7c.setString(2, mNum);
      pstmt7c.setString(3, mNum);
      pstmt7c.setString(4, mNum);
      pstmt7c.setString(5, mNum);
      pstmt7c.setLong(6, date);
      pstmt7c.setInt(7, rest_etime);
      pstmt7c.setInt(8, rest_stime);
      rs7 = pstmt7c.executeQuery();      // execute the prepared stmt

      while (rs7.next()) {

         time2 = rs7.getInt("time");
         t_fb = rs7.getInt("fb");
         course2 = rs7.getString("courseName");
         rmNum1 = rs7.getString("mNum1");
         rmNum2 = rs7.getString("mNum2");
         rmNum3 = rs7.getString("mNum3");
         rmNum4 = rs7.getString("mNum4");
         rmNum5 = rs7.getString("mNum5");

         //
         //  matching member number found in teecurr - check if course and f/b match
         //
         if (t_fb == 0) {                   // is Tee time for Front 9?

            sfb2 = "Front";
         }

         if (t_fb == 1) {                   // is it Back 9?

            sfb2 = "Back";
         }

         //
         //  First make sure this is not this tee time before changes,
         //  Then check if it matches the criteria for the restriction.
         //
         if ((time2 != time) || (!course2.equals( course ))) {  // either time or course is diff

            if ((rest_fb.equals( "Both" ) || rest_fb.equals( sfb2 )) &&
                (rest_course.equals( "-ALL-" ) || rest_course.equals( course2 ))) {

               if (mNum.equals( rmNum1 )) {
                  ind++;
               }
               if (mNum.equals( rmNum2 )) {
                  ind++;
               }
               if (mNum.equals( rmNum3 )) {
                  ind++;
               }
               if (mNum.equals( rmNum4 )) {
                  ind++;
               }
               if (mNum.equals( rmNum5 )) {
                  ind++;
               }
            }
         }

      } // end of while members

      pstmt7c.close();

   }
   catch (Exception ignore) {
   }

   return (ind);
 }


 // *******************************************************************************
 //  Check for guests
 // *******************************************************************************
 //
 public static int processGuests(Connection con, parmLott parm) {


   Statement stmtx2 = null;
   ResultSet rs = null;

   int guestErr = 0;
   int i = 0;
   int i2 = 0;

   String gplayer = "";
   String club = parm.club;

   String [] playerA = new String [25];       // array to hold the player values
   String [] oldplayerA = new String [25];    // array to hold the old player values

   boolean invalid = false;


   //
   //  save the player values in the arrays
   //
   playerA[0] = parm.player1;
   playerA[1] = parm.player2;
   playerA[2] = parm.player3;
   playerA[3] = parm.player4;
   playerA[4] = parm.player5;
   playerA[5] = parm.player6;
   playerA[6] = parm.player7;
   playerA[7] = parm.player8;
   playerA[8] = parm.player9;
   playerA[9] = parm.player10;
   playerA[10] = parm.player11;
   playerA[11] = parm.player12;
   playerA[12] = parm.player13;
   playerA[13] = parm.player14;
   playerA[14] = parm.player15;
   playerA[15] = parm.player16;
   playerA[16] = parm.player17;
   playerA[17] = parm.player18;
   playerA[18] = parm.player19;
   playerA[19] = parm.player20;
   playerA[20] = parm.player21;
   playerA[21] = parm.player22;
   playerA[22] = parm.player23;
   playerA[23] = parm.player24;
   playerA[24] = parm.player25;
   oldplayerA[0] = parm.oldplayer1;
   oldplayerA[1] = parm.oldplayer2;
   oldplayerA[2] = parm.oldplayer3;
   oldplayerA[3] = parm.oldplayer4;
   oldplayerA[4] = parm.oldplayer5;
   oldplayerA[5] = parm.oldplayer6;
   oldplayerA[6] = parm.oldplayer7;
   oldplayerA[7] = parm.oldplayer8;
   oldplayerA[8] = parm.oldplayer9;
   oldplayerA[9] = parm.oldplayer10;
   oldplayerA[10] = parm.oldplayer11;
   oldplayerA[11] = parm.oldplayer12;
   oldplayerA[12] = parm.oldplayer13;
   oldplayerA[13] = parm.oldplayer14;
   oldplayerA[14] = parm.oldplayer15;
   oldplayerA[15] = parm.oldplayer16;
   oldplayerA[16] = parm.oldplayer17;
   oldplayerA[17] = parm.oldplayer18;
   oldplayerA[18] = parm.oldplayer19;
   oldplayerA[19] = parm.oldplayer20;
   oldplayerA[20] = parm.oldplayer21;
   oldplayerA[21] = parm.oldplayer22;
   oldplayerA[22] = parm.oldplayer23;
   oldplayerA[23] = parm.oldplayer24;
   oldplayerA[24] = parm.oldplayer25;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm2 = new parmClub(0, con); // since lotteries are not supported in FlxRez let's hard code the root activity_id to zero


   //
   //   Get the guest names specified for this club
   //
   try {
      getClub.getParms(con, parm2);        // get the club parms

   }
   catch (Exception ignore) {
   }
/*
   //
   //   Remove any guest types that are null - for tests below
   //
   for (i = 0; i < parm2.MAX_Guests; i++) {

      if (parm2.guest[i].equals( "" )) {

         parm2.guest[i] = "$@#!^&*";      // make so it won't match player name
      }
   }         // end of while loop
*/
   //
   //  Check if any player names are guest names
   //
   i = 0;
   while (i < 25) {

      parm.gstA[i] = "";    // init guest array and indicators
      i++;
   }

   //
   //  Process each player
   //
   loop1:
   for (i2=0; i2<25; i2++) {

      parm.g[i2] = "";
      gplayer = "";
      if (!playerA[i2].equals( "" ) && !playerA[i2].equalsIgnoreCase( "x" )) {

         loop2:
         for (i=0; i<parm2.MAX_Guests; i++) {

            if (playerA[i2].startsWith( parm2.guest[i] )) {

               parm.g[i2] = parm2.guest[i];       // indicate player is a guest name and save name
               parm.gstA[i2] = playerA[i2];       // save guest value
               parm.guests++;                    // increment number of guests this request
               parm.guestsg1++;                  // increment number of guests this slot

/*
               if (parm2.gOpt[i] > 0) {                             // if Proshop-only guest type

                  if (!playerA[i2].equals( oldplayerA[i2] )) {      // if new or changed player name

                     gplayer = playerA[i2];                         // indicate error (ok if it was already entered by pro)
                     guestErr = 1;
                     break loop1;                                   // exit both loops
                  }
               }

               //
               //  Check for guest name if requested for this club
               //
               if (parm2.forceg > 0) {

                  invalid = verifySlot.checkGstName(playerA[i2], parm2.guest[i], club);      // go check for a name

                  if (invalid == true) {                                    // if name not specified

                     if (!playerA[i2].equals( oldplayerA[i2] )) {      // if new or changed player name

                        gplayer = playerA[i2];                         // indicate error (ok if it was already entered by pro)
                        guestErr = 2;
                        break loop1;                                   // exit both loops
                     }
                  }
               }
*/

               break loop2;
            }
         }         // end of while loop
      }
   }

   parm.player = gplayer;            // save player name if error

   return(guestErr);
 }


 //************************************************************************
 //
 //  getParmValues - get parameter values for a lottery request so that we
 //                  can process restrictions.
 //
 //
 //   called by:  assign1Time (lottery processing)
 //
 //************************************************************************

 public static void getParmValues(parmLott parm, Connection con) {


   ResultSet rs = null;
   PreparedStatement pstmt = null;

   int i = 0;

   String [] userA = new String [26];
   String [] mshipA = new String [26];
   String [] mtypeA = new String [26];
   String [] mNumA = new String [26];

    //
    //  Init the arrays
    //
    for (i=0; i<26; i++) {
       userA[i] = "";
       mshipA[i] = "";
       mtypeA[i] = "";
       mNumA[i] = "";
    }


   //
   //   Get the values from the lottery request
   //
   try {

      pstmt = con.prepareStatement (
         "SELECT * " +
         "FROM lreqs3 WHERE id = ?");

      pstmt.clearParameters();
      pstmt.setLong(1, parm.lottid);
      rs = pstmt.executeQuery();

      if (rs.next()) {

         parm.lottName = rs.getString("name");
         parm.date = rs.getLong("date");
         parm.mm = rs.getInt("mm");
         parm.dd = rs.getInt("dd");
         parm.yy = rs.getInt("yy");
         parm.day = rs.getString("day");
         parm.player1 = rs.getString("player1");
         parm.player2 = rs.getString("player2");
         parm.player3 = rs.getString("player3");
         parm.player4 = rs.getString("player4");
         parm.player5 = rs.getString("player5");
         parm.player6 = rs.getString("player6");
         parm.player7 = rs.getString("player7");
         parm.player8 = rs.getString("player8");
         parm.player9 = rs.getString("player9");
         parm.player10 = rs.getString("player10");
         parm.player11 = rs.getString("player11");
         parm.player12 = rs.getString("player12");
         parm.player13 = rs.getString("player13");
         parm.player14 = rs.getString("player14");
         parm.player15 = rs.getString("player15");
         parm.player16 = rs.getString("player16");
         parm.player17 = rs.getString("player17");
         parm.player18 = rs.getString("player18");
         parm.player19 = rs.getString("player19");
         parm.player20 = rs.getString("player20");
         parm.player21 = rs.getString("player21");
         parm.player22 = rs.getString("player22");
         parm.player23 = rs.getString("player23");
         parm.player24 = rs.getString("player24");
         parm.player25 = rs.getString("player25");
         for (i=1; i<26; i++) {
            userA[i] = rs.getString("user" +i);
         }
         parm.pcw1 = rs.getString("p1cw");
         parm.pcw2 = rs.getString("p2cw");
         parm.pcw3 = rs.getString("p3cw");
         parm.pcw4 = rs.getString("p4cw");
         parm.pcw5 = rs.getString("p5cw");
         parm.pcw6 = rs.getString("p6cw");
         parm.pcw7 = rs.getString("p7cw");
         parm.pcw8 = rs.getString("p8cw");
         parm.pcw9 = rs.getString("p9cw");
         parm.pcw10 = rs.getString("p10cw");
         parm.pcw11 = rs.getString("p11cw");
         parm.pcw12 = rs.getString("p12cw");
         parm.pcw13 = rs.getString("p13cw");
         parm.pcw14 = rs.getString("p14cw");
         parm.pcw15 = rs.getString("p15cw");
         parm.pcw16 = rs.getString("p16cw");
         parm.pcw17 = rs.getString("p17cw");
         parm.pcw18 = rs.getString("p18cw");
         parm.pcw19 = rs.getString("p19cw");
         parm.pcw20 = rs.getString("p20cw");
         parm.pcw21 = rs.getString("p21cw");
         parm.pcw22 = rs.getString("p22cw");
         parm.pcw23 = rs.getString("p23cw");
         parm.pcw24 = rs.getString("p24cw");
         parm.pcw25 = rs.getString("p25cw");
         parm.p5 = rs.getString("p5");
         parm.players = rs.getInt("players");
         for (i=0; i<25; i++) {
            parm.userg[i] = rs.getString("userg" +(i+1));
         }
      }

      pstmt.close();

      //
      //  Set user values
      //
      parm.user1 = userA[1];
      parm.user2 = userA[2];
      parm.user3 = userA[3];
      parm.user4 = userA[4];
      parm.user5 = userA[5];
      parm.user6 = userA[6];
      parm.user7 = userA[7];
      parm.user8 = userA[8];
      parm.user9 = userA[9];
      parm.user10 = userA[10];
      parm.user11 = userA[11];
      parm.user12 = userA[12];
      parm.user13 = userA[13];
      parm.user14 = userA[14];
      parm.user15 = userA[15];
      parm.user16 = userA[16];
      parm.user17 = userA[17];
      parm.user18 = userA[18];
      parm.user19 = userA[19];
      parm.user20 = userA[20];
      parm.user21 = userA[21];
      parm.user22 = userA[22];
      parm.user23 = userA[23];
      parm.user24 = userA[24];
      parm.user25 = userA[25];

      parm.memg1 = 0;            // init
      parm.memg2 = 0;
      parm.memg3 = 0;
      parm.memg4 = 0;
      parm.memg5 = 0;
      parm.members = 0;

      //
      //  Get member info (mship, mNum, etc.)
      //
      for (i=1; i<26; i++) {

         if (!userA[i].equals( "" )) {      // if user

            pstmt = con.prepareStatement (
            "SELECT m_ship, m_type, memNum FROM member2b WHERE username = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, userA[i]);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               mshipA[i] = rs.getString(1);
               mtypeA[i] = rs.getString(2);
               mNumA[i] = rs.getString(3);

               parm.members++;            // increment number of members this res.

               if (parm.p5.equals( "Yes" )) {   // if 5-somes

                  if (i < 6) {

                     parm.memg1++;          // bump # of members in group 1

                  } else {

                     if (i < 11) {

                        parm.memg2++;          // bump # of members in group 2

                     } else {

                        if (i < 16) {

                           parm.memg3++;          // bump # of members in group 3

                        } else {

                           if (i < 21) {

                              parm.memg4++;          // bump # of members in group 4

                           } else {

                              parm.memg5++;          // bump # of members in group 5
                           }
                        }
                     }
                  }

               } else {           // 4-somes only

                  if (i < 5) {

                     parm.memg1++;          // bump # of members in group 1

                  } else {

                     if (i < 9) {

                        parm.memg2++;          // bump # of members in group 2

                     } else {

                        if (i < 13) {

                           parm.memg3++;          // bump # of members in group 3

                        } else {

                           if (i < 17) {

                              parm.memg4++;          // bump # of members in group 4

                           } else {

                              parm.memg5++;          // bump # of members in group 5
                           }
                        }
                     }
                  }

               }
            }

            pstmt.close();
         }
      }

      //
      //  Set member values
      //
      parm.mship1 = mshipA[1];
      parm.mship2 = mshipA[2];
      parm.mship3 = mshipA[3];
      parm.mship4 = mshipA[4];
      parm.mship5 = mshipA[5];
      parm.mship6 = mshipA[6];
      parm.mship7 = mshipA[7];
      parm.mship8 = mshipA[8];
      parm.mship9 = mshipA[9];
      parm.mship10 = mshipA[10];
      parm.mship11 = mshipA[11];
      parm.mship12 = mshipA[12];
      parm.mship13 = mshipA[13];
      parm.mship14 = mshipA[14];
      parm.mship15 = mshipA[15];
      parm.mship16 = mshipA[16];
      parm.mship17 = mshipA[17];
      parm.mship18 = mshipA[18];
      parm.mship19 = mshipA[19];
      parm.mship20 = mshipA[20];
      parm.mship21 = mshipA[21];
      parm.mship22 = mshipA[22];
      parm.mship23 = mshipA[23];
      parm.mship24 = mshipA[24];
      parm.mship25 = mshipA[25];

      parm.mtype1 = mtypeA[1];
      parm.mtype2 = mtypeA[2];
      parm.mtype3 = mtypeA[3];
      parm.mtype4 = mtypeA[4];
      parm.mtype5 = mtypeA[5];
      parm.mtype6 = mtypeA[6];
      parm.mtype7 = mtypeA[7];
      parm.mtype8 = mtypeA[8];
      parm.mtype9 = mtypeA[9];
      parm.mtype10 = mtypeA[10];
      parm.mtype11 = mtypeA[11];
      parm.mtype12 = mtypeA[12];
      parm.mtype13 = mtypeA[13];
      parm.mtype14 = mtypeA[14];
      parm.mtype15 = mtypeA[15];
      parm.mtype16 = mtypeA[16];
      parm.mtype17 = mtypeA[17];
      parm.mtype18 = mtypeA[18];
      parm.mtype19 = mtypeA[19];
      parm.mtype20 = mtypeA[20];
      parm.mtype21 = mtypeA[21];
      parm.mtype22 = mtypeA[22];
      parm.mtype23 = mtypeA[23];
      parm.mtype24 = mtypeA[24];
      parm.mtype25 = mtypeA[25];

      parm.mNum1 = mNumA[1];
      parm.mNum2 = mNumA[2];
      parm.mNum3 = mNumA[3];
      parm.mNum4 = mNumA[4];
      parm.mNum5 = mNumA[5];
      parm.mNum6 = mNumA[6];
      parm.mNum7 = mNumA[7];
      parm.mNum8 = mNumA[8];
      parm.mNum9 = mNumA[9];
      parm.mNum10 = mNumA[10];
      parm.mNum11 = mNumA[11];
      parm.mNum12 = mNumA[12];
      parm.mNum13 = mNumA[13];
      parm.mNum14 = mNumA[14];
      parm.mNum15 = mNumA[15];
      parm.mNum16 = mNumA[16];
      parm.mNum17 = mNumA[17];
      parm.mNum18 = mNumA[18];
      parm.mNum19 = mNumA[19];
      parm.mNum20 = mNumA[20];
      parm.mNum21 = mNumA[21];
      parm.mNum22 = mNumA[22];
      parm.mNum23 = mNumA[23];
      parm.mNum24 = mNumA[24];
      parm.mNum25 = mNumA[25];

      //
      //  Process any guests that were specified - set guest parms (ignore error)
      //
      int guestErr = processGuests(con, parm);

   }
   catch (Exception e1) {
      String errorMsg = "Error in Common_Lott getParmValues: ";
      dbError(errorMsg, e1);                                          // log it
   }

 }  // end of getParmValues


/**
 //************************************************************************
 //
 //  checkGuestQuota - checks for maximum number of guests exceeded per member
 //                    or per membership during a specified period.
 //
 //
 //   called by:   Member_lott
 //                Proshop_lott
 //
 //************************************************************************
 **/

 public static boolean checkGuestQuota(parmLott slotParms, Connection con) {


   ResultSet rs = null;
   ResultSet rs2 = null;

   boolean error = false;
   boolean check = false;

   int i = 0;
   int i2 = 0;
   //int guests = 0;
   int grest_id = 0;
   int stime = 0;
   int etime = 0;

   long sdate = 0;
   long edate = 0;

   String rcourse = "";
   String rest_fb = "";
   String per = "";                        // per = 'Member' or 'Tee Time'

   String errorMsg = "Error in Member_lott.checkGuestQuota: ";

   int [] guestsA = new int [25];         // array to hold the Guest Counts for each player position

   String [] userg = new String [25];     // array to hold the Associated Member's username for each Guest

   //String [] rguest = new String [36];    // array to hold the Guest Restriction's guest types
   ArrayList<String> rguest = new ArrayList<String>();


   if (slotParms.fb == 0) {                   // is Tee time for Front 9?

      slotParms.sfb = "Front";
   }

   if (slotParms.fb == 1) {                   // is it Back 9?

      slotParms.sfb = "Back";
   }

   try {

      PreparedStatement pstmt5 = con.prepareStatement (
         "SELECT * " +
         "FROM guestqta4 WHERE sdate <= ? AND edate >= ? AND " +
         "stime <= ? AND etime >= ? AND activity_id = 0");

      pstmt5.clearParameters();        // clear the parms
      pstmt5.setLong(1, slotParms.date);
      pstmt5.setLong(2, slotParms.date);
      pstmt5.setInt(3, slotParms.time);
      pstmt5.setInt(4, slotParms.time);
      rs = pstmt5.executeQuery();      // execute the prepared stmt

      loop1:
      while (rs.next()) {

         sdate = rs.getLong("sdate");
         edate = rs.getLong("edate");
         stime = rs.getInt("stime");
         etime = rs.getInt("etime");
         slotParms.grest_num = rs.getInt("num_guests");
         rcourse = rs.getString("courseName");
         rest_fb = rs.getString("fb");
         per = rs.getString("per");
         grest_id = rs.getInt("id");
         
         // now look up the guest types for this restriction
         PreparedStatement pstmt2 = con.prepareStatement (
                 "SELECT guest_type FROM guestqta4_gtypes WHERE guestqta_id = ?");

         pstmt2.clearParameters();
         pstmt2.setInt(1, grest_id);

         rs2 = pstmt2.executeQuery();

         while ( rs2.next() ) {

            rguest.add(rs2.getString("guest_type"));

         }

         pstmt2.close();
         
/*         
         rguest[0] = rs.getString("guest1");
         rguest[1] = rs.getString("guest2");
         rguest[2] = rs.getString("guest3");
         rguest[3] = rs.getString("guest4");
         rguest[4] = rs.getString("guest5");
         rguest[5] = rs.getString("guest6");
         rguest[6] = rs.getString("guest7");
         rguest[7] = rs.getString("guest8");
         rguest[8] = rs.getString("guest9");
         rguest[9] = rs.getString("guest10");
         rguest[10] = rs.getString("guest11");
         rguest[11] = rs.getString("guest12");
         rguest[12] = rs.getString("guest13");
         rguest[13] = rs.getString("guest14");
         rguest[14] = rs.getString("guest15");
         rguest[15] = rs.getString("guest16");
         rguest[16] = rs.getString("guest17");
         rguest[17] = rs.getString("guest18");
         rguest[18] = rs.getString("guest19");
         rguest[19] = rs.getString("guest20");
         rguest[20] = rs.getString("guest21");
         rguest[21] = rs.getString("guest22");
         rguest[22] = rs.getString("guest23");
         rguest[23] = rs.getString("guest24");
         rguest[24] = rs.getString("guest25");
         rguest[25] = rs.getString("guest26");
         rguest[26] = rs.getString("guest27");
         rguest[27] = rs.getString("guest28");
         rguest[28] = rs.getString("guest29");
         rguest[29] = rs.getString("guest30");
         rguest[30] = rs.getString("guest31");
         rguest[31] = rs.getString("guest32");
         rguest[32] = rs.getString("guest33");
         rguest[33] = rs.getString("guest34");
         rguest[34] = rs.getString("guest35");
         rguest[35] = rs.getString("guest36");
*/

         check = false;       // init 'check guests' flag

         for (i = 0; i < 25; i++) {
            userg[i] = "";         // init usernames
         }
/*
         for (i = 0; i < 36; i++) {
            if (rguest[i].equals( "" )) {
               rguest[i] = "@#$%&*";         // init guest usernames to prevent false match below
            }
         }
*/
         //
         //  Check if course and f/b match that specified in restriction
         //
         if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( slotParms.course ))) {

            if ((rest_fb.equals( "Both" )) || (rest_fb.equals( slotParms.sfb ))) {  

               //  compare guest types in tee time against those specified in restriction
               i = 0;
               ploop1:
               while (i < rguest.size()) {

                  //
                  //     slotParms.g[x] = guest types specified in player name fields
                  //     rguest[x] = guest types from restriction gotten above
                  //
                  for (i2 = 0; i2 < 25; i2++) {

                     if (!slotParms.g[i2].equals( "" )) {
                        if (slotParms.g[i2].equals( rguest.get(i) )) {
                           check = true;                          // indicate check num of guests
                           userg[i2] = slotParms.userg[i2];       // save member associated with this guest
                        }
                     }
                  }
                  i++;

               } // end while loops of guest types

            } // end if f/b matches

         } // end of IF course matches

         if (check == true) {   // if restriction exists for this day and time and there are guests in tee time

            //
            //  Determine the member assigned to the guest and calculate their quota count
            //
            for (i = 0; i < 25; i++) {
               guestsA[i] = 0;          // init # of guests for each member
            }

            //
            //  Check each member for duplicates and count these guests first
            //
            for (i = 0; i < 25; i++) {

               i2 = i + 1;              // point to next user

               if (!userg[i].equals( "" )) {

                  guestsA[i]++;               // count the guest

                  while (i2 < 25) {           // loop for dups

                     if (userg[i].equals( userg[i2] )) {

                        guestsA[i]++;            // count the guest
                        userg[i2] = "";          // remove dup
                     }
                     i2++;
                  }

                  // go count the number of guests for this member
                  guestsA[i] += countGuests(con, userg[i], slotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

               }
            }

            //
            //  Process according to the 'per' value; member or member number
            //
            if (per.startsWith( "Membership" )) {

               for (i = 0; i < 25; i++) {

                  if (!userg[i].equals( "" )) {

                     // go count the number of guests for this member number
                     guestsA[i] += checkMnums(con, userg[i], slotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

                  }
               }
            }

            // if num of guests in quota count (guests_) > num allowed (grest_num) per member
            //
            //       to get here guests_ is = # of guests accumulated for member
            //       grest_num is 0 - 999 (restriction quota)
            //       per is 'Member' or 'Membership Number'
            //
            slotParms.grest_per = per;    // save this rest's per value

            if (guestsA[0] > slotParms.grest_num) {

               slotParms.player = slotParms.player1;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[1] > slotParms.grest_num) {

               slotParms.player = slotParms.player2;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[2] > slotParms.grest_num) {

               slotParms.player = slotParms.player3;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[3] > slotParms.grest_num) {

               slotParms.player = slotParms.player4;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[4] > slotParms.grest_num) {

               slotParms.player = slotParms.player5;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[5] > slotParms.grest_num) {

               slotParms.player = slotParms.player6;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[6] > slotParms.grest_num) {

               slotParms.player = slotParms.player7;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[7] > slotParms.grest_num) {

               slotParms.player = slotParms.player8;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[8] > slotParms.grest_num) {

               slotParms.player = slotParms.player9;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[9] > slotParms.grest_num) {

               slotParms.player = slotParms.player10;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[10] > slotParms.grest_num) {

               slotParms.player = slotParms.player11;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[11] > slotParms.grest_num) {

               slotParms.player = slotParms.player12;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[12] > slotParms.grest_num) {

               slotParms.player = slotParms.player13;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[13] > slotParms.grest_num) {

               slotParms.player = slotParms.player14;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[14] > slotParms.grest_num) {

               slotParms.player = slotParms.player15;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[15] > slotParms.grest_num) {

               slotParms.player = slotParms.player16;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[16] > slotParms.grest_num) {

               slotParms.player = slotParms.player17;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[17] > slotParms.grest_num) {

               slotParms.player = slotParms.player18;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[18] > slotParms.grest_num) {

               slotParms.player = slotParms.player19;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[19] > slotParms.grest_num) {

               slotParms.player = slotParms.player20;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[20] > slotParms.grest_num) {

               slotParms.player = slotParms.player21;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[21] > slotParms.grest_num) {

               slotParms.player = slotParms.player22;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[22] > slotParms.grest_num) {

               slotParms.player = slotParms.player23;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[23] > slotParms.grest_num) {

               slotParms.player = slotParms.player24;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }
            if (guestsA[24] > slotParms.grest_num) {

               slotParms.player = slotParms.player25;   // save member name
               error = true;                 // set error flag
               break loop1;                  // done checking - exit while loop
            }

         }    // end of IF true
      }       // end of loop1 while loop

      pstmt5.close();

   }
   catch (Exception e) {
   }

   return(error);

 }     // end of checkGuestQuota 


/**
 //************************************************************************
 //
 //  Find any other members with the same Member Number and count their guests.
 //
 //    Called by:  checkGuestQuota above
 //
 //************************************************************************
 **/

 private static int checkMnums(Connection con, String user, parmLott slotParms, long sdate, long edate, int stime, int etime,
                               String rfb, String rcourse, ArrayList<String> rguest)
                           throws Exception {

   ResultSet rs = null;

   int guests = 0;
   //int guests2 = 0;
   //int i = 0;

   String mNum = "";
   String tuser = "";


   //****************************************************************************
   //  per = Membership Number  -  check all members with the same Member Number
   //****************************************************************************

   try {

      //  get this user's mNum
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT memNum FROM member2b WHERE username = ?");

      pstmt3.clearParameters();        // clear the parms
      pstmt3.setString(1, user);
      rs = pstmt3.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         mNum = rs.getString(1);       // get this user's member number
      }
      pstmt3.close();

      if (!mNum.equals( "" )) {     // if there is one specified

         //  get all users with matching mNum and put in userm array
         PreparedStatement pstmt4 = con.prepareStatement (
            "SELECT username FROM member2b WHERE memNum = ?");

         pstmt4.clearParameters();        // clear the parms
         pstmt4.setString(1, mNum);
         rs = pstmt4.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            tuser = rs.getString(1);       // get the username
            if (!tuser.equals( "" ) && !tuser.equalsIgnoreCase( user )) {   // if exists and not this user

               // go count the number of guests for this member
               guests += countGuests(con, tuser, slotParms, sdate, edate, stime, etime, rfb, rcourse, rguest);

            }
         }
         pstmt4.close();
      }

   }
   catch (Exception e) {

      throw new Exception("Error Checking Mnums for Guest Rest - Member_lott: " + e.getMessage());
   }

   return(guests);
 }                   // end of checkMnums


/**
 //************************************************************************
 //
 //  Count the number of guests that a member has scheduled in the specified time.
 //
 //    Called by:  checkGuestQuota and checkMnums above
 //
 //    Check teecurr and tee past for all specified guest types that are
 //    associated with this member or member number.
 //
 //************************************************************************
 **/

 private static int countGuests(Connection con, String user, parmLott slotParms, long sdate, long edate, int stime, int etime,
                               String rfb, String rcourse, ArrayList<String> rguest)
                           throws Exception {

   ResultSet rs = null;

   int guests = 0;
   int time = 0;
   int fb = 0;
   int rest_fb = 0;
   int i = 0;

   long date = 0;

   //String sfb = "";
   String course = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";

   //  convert restriction's f/b
   if (rfb.equalsIgnoreCase( "Front" )) {
      rest_fb = 0;
   } else {
      rest_fb = 1;
   }


   //
   //  Count all guests with matching guest types that are associated with this member (teecurr)
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT date, time, courseName, player1, player2, player3, player4, fb, player5, " +
         "userg1, userg2, userg3, userg4, userg5 " +
         "FROM teecurr2 " +
         "WHERE (userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) " +
         "AND date <= ? AND date >= ? AND time <= ? AND time >= ?");

      pstmt1.clearParameters();        // clear the parms and check player 1
      pstmt1.setString(1, user);
      pstmt1.setString(2, user);
      pstmt1.setString(3, user);
      pstmt1.setString(4, user);
      pstmt1.setString(5, user);
      pstmt1.setLong(6, edate);
      pstmt1.setLong(7, sdate);
      pstmt1.setInt(8, etime);
      pstmt1.setInt(9, stime);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         date = rs.getLong("date");
         time = rs.getInt("time");
         course = rs.getString("courseName");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         fb = rs.getInt("fb");
         player5 = rs.getString("player5");
         userg1 = rs.getString("userg1");
         userg2 = rs.getString("userg2");
         userg3 = rs.getString("userg3");
         userg4 = rs.getString("userg4");
         userg5 = rs.getString("userg5");

         //
         //  matching tee time found in teecurr - check if course, date and time match
         //  Make sure this is not this tee time before changes,
         //
         if ((date != slotParms.date) || (time != slotParms.time) || (!course.equals( slotParms.course ))) {

            //
            //  Check if course and f/b match that specified in restriction
            //
            if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( course ))) {

               if ((rfb.equals( "Both" )) || (rest_fb == fb )) {  // if f/b matches

                  // check if any players from the tee time are a restricted guest
                  if (user.equalsIgnoreCase( userg1 )) {
                     i = 0;
                     loop1:
                     while (i < 36) {
                        if (player1.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                           guests++;             // bump count of guests for quota check
                           break loop1;          // exit loop
                        }
                        i++;
                     }
                  }
                  if (user.equalsIgnoreCase( userg2 )) {
                     i = 0;
                     loop2:
                     while (i < 36) {
                        if (player2.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                           guests++;             // bump count of guests for quota check
                           break loop2;          // exit loop
                        }
                        i++;
                     }
                  }
                  if (user.equalsIgnoreCase( userg3 )) {
                     i = 0;
                     loop3:
                     while (i < 36) {
                        if (player3.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                           guests++;             // bump count of guests for quota check
                           break loop3;          // exit loop
                        }
                        i++;
                     }
                  }
                  if (user.equalsIgnoreCase( userg4 )) {
                     i = 0;
                     loop4:
                     while (i < 36) {
                        if (player4.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                           guests++;             // bump count of guests for quota check
                           break loop4;          // exit loop
                        }
                        i++;
                     }
                  }
                  if (user.equalsIgnoreCase( userg5 )) {
                     i = 0;
                     loop5:
                     while (i < 36) {
                        if (player5.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                           guests++;             // bump count of guests for quota check
                           break loop5;          // exit loop
                        }
                        i++;
                     }
                  }
               }
            }
         }   // end of IF tee time not 'this tee time'
      }   // end of WHILE

      pstmt1.close();

      //
      //  Count all guests with matching guest types that are associated with this member (teepast)
      //
      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT courseName, player1, player2, player3, player4, fb, player5, " +
         "userg1, userg2, userg3, userg4, userg5 " +
         "FROM teepast2 " +
         "WHERE (userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) " +
         "AND date <= ? AND date >= ? AND time <= ? AND time >= ?");

      pstmt2.clearParameters();        // clear the parms and check player 1
      pstmt2.setString(1, user);
      pstmt2.setString(2, user);
      pstmt2.setString(3, user);
      pstmt2.setString(4, user);
      pstmt2.setString(5, user);
      pstmt2.setLong(6, edate);
      pstmt2.setLong(7, sdate);
      pstmt2.setInt(8, etime);
      pstmt2.setInt(9, stime);
      rs = pstmt2.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         course = rs.getString("courseName");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         fb = rs.getInt("fb");
         player5 = rs.getString("player5");
         userg1 = rs.getString("userg1");
         userg2 = rs.getString("userg2");
         userg3 = rs.getString("userg3");
         userg4 = rs.getString("userg4");
         userg5 = rs.getString("userg5");

         //
         //  Check if course and f/b match that specified in restriction
         //
         if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( course ))) {

            if ((rfb.equals( "Both" )) || (rest_fb == fb )) {  // if f/b matches

               //
               //  matching tee time found in teecurr
               //  check if any players from the tee time are a restricted guest
               //
               if (user.equalsIgnoreCase( userg1 )) {
                  i = 0;
                  loop11:
                  while (i < 36) {
                     if (player1.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                        guests++;             // bump count of guests for quota check
                        break loop11;          // exit loop
                     }
                     i++;
                  }
               }
               if (user.equalsIgnoreCase( userg2 )) {
                  i = 0;
                  loop12:
                  while (i < 36) {
                     if (player2.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                        guests++;             // bump count of guests for quota check
                        break loop12;          // exit loop
                     }
                     i++;
                  }
               }
               if (user.equalsIgnoreCase( userg3 )) {
                  i = 0;
                  loop13:
                  while (i < 36) {
                     if (player3.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                        guests++;             // bump count of guests for quota check
                        break loop13;          // exit loop
                     }
                     i++;
                  }
               }
               if (user.equalsIgnoreCase( userg4 )) {
                  i = 0;
                  loop14:
                  while (i < 36) {
                     if (player4.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                        guests++;             // bump count of guests for quota check
                        break loop14;          // exit loop
                     }
                     i++;
                  }
               }
               if (user.equalsIgnoreCase( userg5 )) {
                  i = 0;
                  loop15:
                  while (i < 36) {
                     if (player5.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                        guests++;             // bump count of guests for quota check
                        break loop15;          // exit loop
                     }
                     i++;
                  }
               }
            }
         }
      }   // end of WHILE

      pstmt2.close();

   }
   catch (Exception e) {

      throw new Exception("Error Counting Guests for Guest Rest - Member_lott: " + e.getMessage());
   }

   return(guests);
 }                      // end of countGuests


 

 //************************************************************************
 // moveReqs - Move lottery requests from lreqs to teecurr.
 //
 //   called by:  processLott and Proshop_dsheet (via SystemUtils.moveReqs)
 //
 //       parms:  name   = name of the lottery
 //               date   = date of the lottery requests
 //               course = name of course
 //               con    = db connection
 //************************************************************************

 public static void moveReqs(String name, long date, String course, String user, boolean sendEmailsNow, Connection con) {

   int tmp_emailFlag = (sendEmailsNow) ? 0 : 1;

   Statement estmt = null;
   Statement stmtN = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   PreparedStatement pstmtd = null;
   PreparedStatement pstmtd2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String player6 = "";
   String player7 = "";
   String player8 = "";
   String player9 = "";
   String player10 = "";
   String player11 = "";
   String player12 = "";
   String player13 = "";
   String player14 = "";
   String player15 = "";
   String player16 = "";
   String player17 = "";
   String player18 = "";
   String player19 = "";
   String player20 = "";
   String player21 = "";
   String player22 = "";
   String player23 = "";
   String player24 = "";
   String player25 = "";

   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String p6cw = "";
   String p7cw = "";
   String p8cw = "";
   String p9cw = "";
   String p10cw = "";
   String p11cw = "";
   String p12cw = "";
   String p13cw = "";
   String p14cw = "";
   String p15cw = "";
   String p16cw = "";
   String p17cw = "";
   String p18cw = "";
   String p19cw = "";
   String p20cw = "";
   String p21cw = "";
   String p22cw = "";
   String p23cw = "";
   String p24cw = "";
   String p25cw = "";

   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String user6 = "";
   String user7 = "";
   String user8 = "";
   String user9 = "";
   String user10 = "";
   String user11 = "";
   String user12 = "";
   String user13 = "";
   String user14 = "";
   String user15 = "";
   String user16 = "";
   String user17 = "";
   String user18 = "";
   String user19 = "";
   String user20 = "";
   String user21 = "";
   String user22 = "";
   String user23 = "";
   String user24 = "";
   String user25 = "";

   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String userg6 = "";
   String userg7 = "";
   String userg8 = "";
   String userg9 = "";
   String userg10 = "";
   String userg11 = "";
   String userg12 = "";
   String userg13 = "";
   String userg14 = "";
   String userg15 = "";
   String userg16 = "";
   String userg17 = "";
   String userg18 = "";
   String userg19 = "";
   String userg20 = "";
   String userg21 = "";
   String userg22 = "";
   String userg23 = "";
   String userg24 = "";
   String userg25 = "";

   String orig1 = "";
   String orig2 = "";
   String orig3 = "";
   String orig4 = "";
   String orig5 = "";
   String orig6 = "";
   String orig7 = "";
   String orig8 = "";
   String orig9 = "";
   String orig10 = "";
   String orig11 = "";
   String orig12 = "";
   String orig13 = "";
   String orig14 = "";
   String orig15 = "";
   String orig16 = "";
   String orig17 = "";
   String orig18 = "";
   String orig19 = "";
   String orig20 = "";
   String orig21 = "";
   String orig22 = "";
   String orig23 = "";
   String orig24 = "";
   String orig25 = "";

   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";

   String color = "";
   String p5 = "";
   String type = "";
   String pref = "";
   String approve = "";
   String day = "";
   String notes = "";
   String in_use_by = "";
   String orig_by = "";
   String parm = "";
   String hndcps = "";
   String courseReq = "";
   String lottery = "";
   String orig = "";

   String player5T = "";
   String user5T = "";
   String p5cwT = "";

   String errorMsg = "";

   String [] userA = new String [25];            // array to hold usernames

   long id = 0;

   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int p96 = 0;
   int p97 = 0;
   int p98 = 0;
   int p99 = 0;
   int p910 = 0;
   int p911 = 0;
   int p912 = 0;
   int p913 = 0;
   int p914 = 0;
   int p915 = 0;
   int p916 = 0;
   int p917 = 0;
   int p918 = 0;
   int p919 = 0;
   int p920 = 0;
   int p921 = 0;
   int p922 = 0;
   int p923 = 0;
   int p924 = 0;
   int p925 = 0;

   int i = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int fb = 0;
   int afb = 0;
   int afb2 = 0;
   int afb3 = 0;
   int afb4 = 0;
   int afb5 = 0;
   int count = 0;
   int groups = 0;
   int time = 0;
   int rtime = 0;
   int atime1 = 0;
   int atime2 = 0;
   int atime3 = 0;
   int atime4 = 0;
   int atime5 = 0;
   int players = 0;
   int hide = 0;
   int proNew = 0;
   int proMod = 0;
   int memNew = 0;
   int memMod = 0;
   int proxMins = 0;
   int lottery_email = 0;

   int teecurr_id = 0;

   int weight = 0;
   int weight1 = 0;
   int weight2 = 0;
   int weight3 = 0;
   int weight4 = 0;
   int weight5 = 0;
   int weight6 = 0;
   int weight7 = 0;
   int weight8 = 0;
   int weight9 = 0;
   int weight10 = 0;
   int weight11 = 0;
   int weight12 = 0;
   int weight13 = 0;
   int weight14 = 0;
   int weight15 = 0;
   int weight16 = 0;
   int weight17 = 0;
   int weight18 = 0;
   int weight19 = 0;
   int weight20 = 0;
   int weight21 = 0;
   int weight22 = 0;
   int weight23 = 0;
   int weight24 = 0;
   int weight25 = 0;

   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;

   float hndcp1 = 99;
   float hndcp2 = 99;
   float hndcp3 = 99;
   float hndcp4 = 99;
   float hndcp5 = 99;

   boolean ok = true;


   try {
      //
      //  Get the lottery type for the requested lottery
      //
      pstmt = con.prepareStatement (
         "SELECT type " +
         "FROM lottery3 " +
         "WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         type = rs.getString(1);
      }
      pstmt.close();

   }
   catch (Exception e2) {

      errorMsg = "Error in Common_Lott moveReqs (get lottery type): ";
      errorMsg = errorMsg + "Exception: " +e2.getMessage();                  // build error msg

      logError(errorMsg);                                       // log it
   }

   try {

      errorMsg = "Error in Common_Lott moveReqs (get lottery requests): ";

      //
      //  Get the Lottery Requests for the lottery passed
      //
      pstmt = con.prepareStatement (
         "SELECT * " +
         "FROM lreqs3 " +
         "WHERE name = ? AND date = ? AND courseName = ? AND state = 2");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);
      pstmt.setLong(2, date);
      pstmt.setString(3, course);

      rs = pstmt.executeQuery();      // execute the prepared stmt again to start with first

      while (rs.next()) {

         mm = rs.getInt("mm");
         dd = rs.getInt("dd");
         yy = rs.getInt("yy");
         day = rs.getString("day");
         rtime = rs.getInt("time");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         player5 = rs.getString("player5");
         player6 = rs.getString("player6");
         player7 = rs.getString("player7");
         player8 = rs.getString("player8");
         player9 = rs.getString("player9");
         player10 = rs.getString("player10");
         player11 = rs.getString("player11");
         player12 = rs.getString("player12");
         player13 = rs.getString("player13");
         player14 = rs.getString("player14");
         player15 = rs.getString("player15");
         player16 = rs.getString("player16");
         player17 = rs.getString("player17");
         player18 = rs.getString("player18");
         player19 = rs.getString("player19");
         player20 = rs.getString("player20");
         player21 = rs.getString("player21");
         player22 = rs.getString("player22");
         player23 = rs.getString("player23");
         player24 = rs.getString("player24");
         player25 = rs.getString("player25");
         user1 = rs.getString("user1");
         user2 = rs.getString("user2");
         user3 = rs.getString("user3");
         user4 = rs.getString("user4");
         user5 = rs.getString("user5");
         user6 = rs.getString("user6");
         user7 = rs.getString("user7");
         user8 = rs.getString("user8");
         user9 = rs.getString("user9");
         user10 = rs.getString("user10");
         user11 = rs.getString("user11");
         user12 = rs.getString("user12");
         user13 = rs.getString("user13");
         user14 = rs.getString("user14");
         user15 = rs.getString("user15");
         user16 = rs.getString("user16");
         user17 = rs.getString("user17");
         user18 = rs.getString("user18");
         user19 = rs.getString("user19");
         user20 = rs.getString("user20");
         user21 = rs.getString("user21");
         user22 = rs.getString("user22");
         user23 = rs.getString("user23");
         user24 = rs.getString("user24");
         user25 = rs.getString("user25");
         p1cw = rs.getString("p1cw");
         p2cw = rs.getString("p2cw");
         p3cw = rs.getString("p3cw");
         p4cw = rs.getString("p4cw");
         p5cw = rs.getString("p5cw");
         p6cw = rs.getString("p6cw");
         p7cw = rs.getString("p7cw");
         p8cw = rs.getString("p8cw");
         p9cw = rs.getString("p9cw");
         p10cw = rs.getString("p10cw");
         p11cw = rs.getString("p11cw");
         p12cw = rs.getString("p12cw");
         p13cw = rs.getString("p13cw");
         p14cw = rs.getString("p14cw");
         p15cw = rs.getString("p15cw");
         p16cw = rs.getString("p16cw");
         p17cw = rs.getString("p17cw");
         p18cw = rs.getString("p18cw");
         p19cw = rs.getString("p19cw");
         p20cw = rs.getString("p20cw");
         p21cw = rs.getString("p21cw");
         p22cw = rs.getString("p22cw");
         p23cw = rs.getString("p23cw");
         p24cw = rs.getString("p24cw");
         p25cw = rs.getString("p25cw");
         notes = rs.getString("notes");
         hide = rs.getInt("hideNotes");
         fb = rs.getInt("fb");
         proNew = rs.getInt("proNew");
         proMod = rs.getInt("proMod");
         memNew = rs.getInt("memNew");
         memMod = rs.getInt("memMod");
         id = rs.getInt("id");
         groups = rs.getInt("groups");
         atime1 = rs.getInt("atime1");
         atime2 = rs.getInt("atime2");
         atime3 = rs.getInt("atime3");
         atime4 = rs.getInt("atime4");
         atime5 = rs.getInt("atime5");
         afb = rs.getInt("afb");
         p5 = rs.getString("p5");
         players = rs.getInt("players");
         userg1 = rs.getString("userg1");
         userg2 = rs.getString("userg2");
         userg3 = rs.getString("userg3");
         userg4 = rs.getString("userg4");
         userg5 = rs.getString("userg5");
         userg6 = rs.getString("userg6");
         userg7 = rs.getString("userg7");
         userg8 = rs.getString("userg8");
         userg9 = rs.getString("userg9");
         userg10 = rs.getString("userg10");
         userg11 = rs.getString("userg11");
         userg12 = rs.getString("userg12");
         userg13 = rs.getString("userg13");
         userg14 = rs.getString("userg14");
         userg15 = rs.getString("userg15");
         userg16 = rs.getString("userg16");
         userg17 = rs.getString("userg17");
         userg18 = rs.getString("userg18");
         userg19 = rs.getString("userg19");
         userg20 = rs.getString("userg20");
         userg21 = rs.getString("userg21");
         userg22 = rs.getString("userg22");
         userg23 = rs.getString("userg23");
         userg24 = rs.getString("userg24");
         userg25 = rs.getString("userg25");
         orig_by = rs.getString("orig_by");
         p91 = rs.getInt("p91");
         p92 = rs.getInt("p92");
         p93 = rs.getInt("p93");
         p94 = rs.getInt("p94");
         p95 = rs.getInt("p95");
         p96 = rs.getInt("p96");
         p97 = rs.getInt("p97");
         p98 = rs.getInt("p98");
         p99 = rs.getInt("p99");
         p910 = rs.getInt("p910");
         p911 = rs.getInt("p911");
         p912 = rs.getInt("p912");
         p913 = rs.getInt("p913");
         p914 = rs.getInt("p914");
         p915 = rs.getInt("p915");
         p916 = rs.getInt("p916");
         p917 = rs.getInt("p917");
         p918 = rs.getInt("p918");
         p919 = rs.getInt("p919");
         p920 = rs.getInt("p920");
         p921 = rs.getInt("p921");
         p922 = rs.getInt("p922");
         p923 = rs.getInt("p923");
         p924 = rs.getInt("p924");
         p925 = rs.getInt("p925");
         afb2 = rs.getInt("afb2");
         afb3 = rs.getInt("afb3");
         afb4 = rs.getInt("afb4");
         afb5 = rs.getInt("afb5");
         weight = rs.getInt("weight");              // get the group weight and individual weights
         weight1 = rs.getInt("weight1");
         weight2 = rs.getInt("weight2");
         weight3 = rs.getInt("weight3");
         weight4 = rs.getInt("weight4");
         weight5 = rs.getInt("weight5");
         weight6 = rs.getInt("weight6");
         weight7 = rs.getInt("weight7");
         weight8 = rs.getInt("weight8");
         weight9 = rs.getInt("weight9");
         weight10 = rs.getInt("weight10");
         weight11 = rs.getInt("weight11");
         weight12 = rs.getInt("weight12");
         weight13 = rs.getInt("weight13");
         weight14 = rs.getInt("weight14");
         weight15 = rs.getInt("weight15");
         weight16 = rs.getInt("weight16");
         weight17 = rs.getInt("weight17");
         weight18 = rs.getInt("weight18");
         weight19 = rs.getInt("weight19");
         weight20 = rs.getInt("weight20");
         weight21 = rs.getInt("weight21");
         weight22 = rs.getInt("weight22");
         weight23 = rs.getInt("weight23");
         weight24 = rs.getInt("weight24");
         weight25 = rs.getInt("weight25");
         courseReq = rs.getString("courseReq");
         
         // Set all orig values to the main orig_by value for the lottery request. If starts with "proshop" or blank, user user1 instead.
         if (orig_by.equals("") || orig_by.startsWith("proshop")) {
             if (!user1.equals("")) {
                 orig = user1;
             } else {
                 orig = "";
             }
         } else {
             orig = orig_by;
         }
         
         // Reset the orig values
         orig1 = "";
         orig2 = "";
         orig3 = "";
         orig4 = "";
         orig5 = "";
         orig6 = "";
         orig7 = "";
         orig8 = "";
         orig9 = "";
         orig10 = "";
         orig11 = "";
         orig12 = "";
         orig13 = "";
         orig14 = "";
         orig15 = "";
         orig16 = "";
         orig17 = "";
         orig18 = "";
         orig19 = "";
         orig20 = "";
         orig21 = "";
         orig22 = "";
         orig23 = "";
         orig24 = "";
         orig25 = "";
   
         if (!player1.equals("")) orig1 = orig;
         if (!player2.equals("")) orig2 = orig;
         if (!player3.equals("")) orig3 = orig;
         if (!player4.equals("")) orig4 = orig;
         if (!player5.equals("")) orig5 = orig;
         if (!player6.equals("")) orig6 = orig;
         if (!player7.equals("")) orig7 = orig;
         if (!player8.equals("")) orig8 = orig;
         if (!player9.equals("")) orig9 = orig;
         if (!player10.equals("")) orig10 = orig;
         if (!player11.equals("")) orig11 = orig;
         if (!player12.equals("")) orig12 = orig;
         if (!player13.equals("")) orig13 = orig;
         if (!player14.equals("")) orig14 = orig;
         if (!player15.equals("")) orig15 = orig;
         if (!player16.equals("")) orig16 = orig;
         if (!player17.equals("")) orig17 = orig;
         if (!player18.equals("")) orig18 = orig;
         if (!player19.equals("")) orig19 = orig;
         if (!player20.equals("")) orig20 = orig;
         if (!player21.equals("")) orig21 = orig;
         if (!player22.equals("")) orig22 = orig;
         if (!player23.equals("")) orig23 = orig;
         if (!player24.equals("")) orig24 = orig;
         if (!player25.equals("")) orig25 = orig;


         if (atime1 != 0) {          // only process if its assigned

            ok = checkInUse(con, id);     // check if assigned tee times are currently in use

            if (ok) ok = checkBlockers(con, id);

            if (ok == true) {             // if ok to proceed (no tee times are in use)

               //
               //  Save the usernames
               //
               userA[0] = user1;
               userA[1] = user2;
               userA[2] = user3;
               userA[3] = user4;
               userA[4] = user5;
               userA[5] = user6;
               userA[6] = user7;
               userA[7] = user8;
               userA[8] = user9;
               userA[9] = user10;
               userA[10] = user11;
               userA[11] = user12;
               userA[12] = user13;
               userA[13] = user14;
               userA[14] = user15;
               userA[15] = user16;
               userA[16] = user17;
               userA[17] = user18;
               userA[18] = user19;
               userA[19] = user20;
               userA[20] = user21;
               userA[21] = user22;
               userA[22] = user23;
               userA[23] = user24;
               userA[24] = user25;

               //
               //  create 1 tee time for each group requested (groups = )
               //
               time = atime1;    // time for this tee time
               hndcp1 = 99;      // init
               hndcp2 = 99;
               hndcp3 = 99;
               hndcp4 = 99;
               hndcp5 = 99;
               mNum1 = "";
               mNum2 = "";
               mNum3 = "";
               mNum4 = "";
               mNum5 = "";

               //
               //  Save area for tee time and email processing - by groups
               //
               String g1user1 = user1;
               String g1user2 = user2;
               String g1user3 = user3;
               String g1user4 = user4;
               String g1user5 = "";
               String g1player1 = player1;
               String g1player2 = player2;
               String g1player3 = player3;
               String g1player4 = player4;
               String g1player5 = "";
               String g1p1cw = p1cw;
               String g1p2cw = p2cw;
               String g1p3cw = p3cw;
               String g1p4cw = p4cw;
               String g1p5cw = "";
               String g1userg1 = userg1;
               String g1userg2 = userg2;
               String g1userg3 = userg3;
               String g1userg4 = userg4;
               String g1userg5 = "";
               String g1orig1 = orig1;
               String g1orig2 = orig2;
               String g1orig3 = orig3;
               String g1orig4 = orig4;
               String g1orig5 = "";
               int g1p91 = p91;
               int g1p92 = p92;
               int g1p93 = p93;
               int g1p94 = p94;
               int g1p95 = 0;

               String g2user1 = "";
               String g2user2 = "";
               String g2user3 = "";
               String g2user4 = "";
               String g2user5 = "";
               String g2player1 = "";
               String g2player2 = "";
               String g2player3 = "";
               String g2player4 = "";
               String g2player5 = "";
               String g2p1cw = "";
               String g2p2cw = "";
               String g2p3cw = "";
               String g2p4cw = "";
               String g2p5cw = "";
               String g2userg1 = "";
               String g2userg2 = "";
               String g2userg3 = "";
               String g2userg4 = "";
               String g2userg5 = "";
               String g2orig1 = "";
               String g2orig2 = "";
               String g2orig3 = "";
               String g2orig4 = "";
               String g2orig5 = "";
               int g2p91 = 0;
               int g2p92 = 0;
               int g2p93 = 0;
               int g2p94 = 0;
               int g2p95 = 0;
               int g2weight1 = 0;
               int g2weight2 = 0;
               int g2weight3 = 0;
               int g2weight4 = 0;
               int g2weight5 = 0;

               String g3user1 = "";
               String g3user2 = "";
               String g3user3 = "";
               String g3user4 = "";
               String g3user5 = "";
               String g3player1 = "";
               String g3player2 = "";
               String g3player3 = "";
               String g3player4 = "";
               String g3player5 = "";
               String g3p1cw = "";
               String g3p2cw = "";
               String g3p3cw = "";
               String g3p4cw = "";
               String g3p5cw = "";
               String g3userg1 = "";
               String g3userg2 = "";
               String g3userg3 = "";
               String g3userg4 = "";
               String g3userg5 = "";
               String g3orig1 = "";
               String g3orig2 = "";
               String g3orig3 = "";
               String g3orig4 = "";
               String g3orig5 = "";
               int g3p91 = 0;
               int g3p92 = 0;
               int g3p93 = 0;
               int g3p94 = 0;
               int g3p95 = 0;
               int g3weight1 = 0;
               int g3weight2 = 0;
               int g3weight3 = 0;
               int g3weight4 = 0;
               int g3weight5 = 0;

               String g4user1 = "";
               String g4user2 = "";
               String g4user3 = "";
               String g4user4 = "";
               String g4user5 = "";
               String g4player1 = "";
               String g4player2 = "";
               String g4player3 = "";
               String g4player4 = "";
               String g4player5 = "";
               String g4p1cw = "";
               String g4p2cw = "";
               String g4p3cw = "";
               String g4p4cw = "";
               String g4p5cw = "";
               String g4userg1 = "";
               String g4userg2 = "";
               String g4userg3 = "";
               String g4userg4 = "";
               String g4userg5 = "";
               String g4orig1 = "";
               String g4orig2 = "";
               String g4orig3 = "";
               String g4orig4 = "";
               String g4orig5 = "";
               int g4p91 = 0;
               int g4p92 = 0;
               int g4p93 = 0;
               int g4p94 = 0;
               int g4p95 = 0;
               int g4weight1 = 0;
               int g4weight2 = 0;
               int g4weight3 = 0;
               int g4weight4 = 0;
               int g4weight5 = 0;

               String g5user1 = "";
               String g5user2 = "";
               String g5user3 = "";
               String g5user4 = "";
               String g5user5 = "";
               String g5player1 = "";
               String g5player2 = "";
               String g5player3 = "";
               String g5player4 = "";
               String g5player5 = "";
               String g5p1cw = "";
               String g5p2cw = "";
               String g5p3cw = "";
               String g5p4cw = "";
               String g5p5cw = "";
               String g5userg1 = "";
               String g5userg2 = "";
               String g5userg3 = "";
               String g5userg4 = "";
               String g5userg5 = "";
               String g5orig1 = "";
               String g5orig2 = "";
               String g5orig3 = "";
               String g5orig4 = "";
               String g5orig5 = "";
               int g5p91 = 0;
               int g5p92 = 0;
               int g5p93 = 0;
               int g5p94 = 0;
               int g5p95 = 0;
               int g5weight1 = 0;
               int g5weight2 = 0;
               int g5weight3 = 0;
               int g5weight4 = 0;
               int g5weight5 = 0;

               errorMsg = "Error in Common_Lott moveReqs (get mem# and hndcp): ";

               //
               // calculate mins difference between requested time and assigned time
               //
               int mins = SystemUtils.calcProxTime(rtime, time);

               //
               //  Get the tee time intervals for the requested course so we can determine all the requested times
               //
               int interval = SystemUtils.getCourseInterval(courseReq, con);


               //
               //  Get Member# and Handicap for each member
               //
               if (!user1.equals( "" )) {        // if player is a member

                  parm = SystemUtils.getUser(con, user1);     // get mNum and hndcp for member

                  StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                  mNum1 = tok.nextToken();         // member Number
                  hndcps = tok.nextToken();       // handicap

                  hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int

                  if (sendEmailsNow == true) SystemUtils.logAssign(user1, name, date, mins, rtime, courseReq, time, course, weight1, weight, id, con);  // log the assigned time in lassigns5 for this member
               }
               if (!user2.equals( "" )) {        // if player is a member

                  parm = SystemUtils.getUser(con, user2);     // get mNum and hndcp for member

                  StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                  mNum2 = tok.nextToken();         // member Number
                  hndcps = tok.nextToken();       // handicap

                  hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int

                  if (sendEmailsNow == true) SystemUtils.logAssign(user2, name, date, mins, rtime, courseReq, time, course, weight2, weight, id, con);  // log the assigned time in lassigns5 for this member
               }
               if (!user3.equals( "" )) {        // if player is a member

                  parm = SystemUtils.getUser(con, user3);     // get mNum and hndcp for member

                  StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                  mNum3 = tok.nextToken();         // member Number
                  hndcps = tok.nextToken();       // handicap

                  hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int

                  if (sendEmailsNow == true) SystemUtils.logAssign(user3, name, date, mins, rtime, courseReq, time, course, weight3, weight, id, con);  // log the assigned time in lassigns5 for this member
               }
               if (!user4.equals( "" )) {        // if player is a member

                  parm = SystemUtils.getUser(con, user4);     // get mNum and hndcp for member

                  StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                  mNum4 = tok.nextToken();         // member Number
                  hndcps = tok.nextToken();       // handicap

                  hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int

                  if (sendEmailsNow == true) SystemUtils.logAssign(user4, name, date, mins, rtime, courseReq, time, course, weight4, weight, id, con);  // log the assigned time in lassigns5 for this member
               }
               if (p5.equals( "Yes" )) {

                  if (!user5.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, user5);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum5 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(user5, name, date, mins, rtime, courseReq, time, course, weight5, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  g1player5 = player5;
                  g1user5 = user5;
                  g1p5cw = p5cw;
                  g1userg5 = userg5;
                  g1orig5 = orig5;
                  g1p95 = p95;
               }

               if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                  mNum1 = "";                  // convert back to null
               }
               if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                  mNum2 = "";                  // convert back to null
               }
               if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                  mNum3 = "";                  // convert back to null
               }
               if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                  mNum4 = "";                  // convert back to null
               }
               if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                  mNum5 = "";                  // convert back to null
               }

               try {

                   lottery_email = tmp_emailFlag;

                   pstmt2 = con.prepareStatement("SELECT lottery FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");
                   pstmt2.clearParameters();

                   pstmt2.setLong(1, date);
                   pstmt2.setInt(2, time);
                   pstmt2.setInt(3, afb);
                   pstmt2.setString(4, course);

                   rs3 = pstmt2.executeQuery();

                   if (rs3.next()) {
                       if (rs3.getString("lottery").equals("")) {
                           lottery_email = 0;
                       }
                   } else {
                       lottery_email = tmp_emailFlag;
                   }

                   pstmt2.close();

               } catch (Exception exc) {
                   lottery_email = tmp_emailFlag;
               }

               errorMsg = "Error in Common_Lott moveReqs (put group 1 in tee sheet): ";
               //
               //  Update the tee slot in teecurr
               //
               //  Clear the lottery name so this tee time is displayed in _sheet even though there
               //  may be some requests still outstanding (state = 4).
               //
               PreparedStatement pstmt6 = con.prepareStatement (
                  "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                  "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                  "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                  "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                  "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " + // lottery = '',
                  "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                  "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                  "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, orig1 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ?, " + 
                  "lottery_email = ?, last_mod_date = now() " +
                  "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               pstmt6.clearParameters();        // clear the parms
               pstmt6.setString(1, g1player1);
               pstmt6.setString(2, g1player2);
               pstmt6.setString(3, g1player3);
               pstmt6.setString(4, g1player4);
               pstmt6.setString(5, g1user1);
               pstmt6.setString(6, g1user2);
               pstmt6.setString(7, g1user3);
               pstmt6.setString(8, g1user4);
               pstmt6.setString(9, g1p1cw);
               pstmt6.setString(10, g1p2cw);
               pstmt6.setString(11, g1p3cw);
               pstmt6.setString(12, g1p4cw);
               pstmt6.setFloat(13, hndcp1);
               pstmt6.setFloat(14, hndcp2);
               pstmt6.setFloat(15, hndcp3);
               pstmt6.setFloat(16, hndcp4);
               pstmt6.setString(17, g1player5);
               pstmt6.setString(18, g1user5);
               pstmt6.setString(19, g1p5cw);
               pstmt6.setFloat(20, hndcp5);
               pstmt6.setString(21, notes);
               pstmt6.setInt(22, hide);
               pstmt6.setInt(23, proNew);
               pstmt6.setInt(24, proMod);
               pstmt6.setInt(25, memNew);
               pstmt6.setInt(26, memMod);
               pstmt6.setString(27, mNum1);
               pstmt6.setString(28, mNum2);
               pstmt6.setString(29, mNum3);
               pstmt6.setString(30, mNum4);
               pstmt6.setString(31, mNum5);
               pstmt6.setString(32, g1userg1);
               pstmt6.setString(33, g1userg2);
               pstmt6.setString(34, g1userg3);
               pstmt6.setString(35, g1userg4);
               pstmt6.setString(36, g1userg5);
               pstmt6.setString(37, orig_by);
               pstmt6.setInt(38, g1p91);
               pstmt6.setInt(39, g1p92);
               pstmt6.setInt(40, g1p93);
               pstmt6.setInt(41, g1p94);
               pstmt6.setInt(42, g1p95);
               pstmt6.setString(43, g1orig1);
               pstmt6.setString(44, g1orig2);
               pstmt6.setString(45, g1orig3);
               pstmt6.setString(46, g1orig4);
               pstmt6.setString(47, g1orig5);
               pstmt6.setInt(48, lottery_email);

               pstmt6.setLong(49, date);
               pstmt6.setInt(50, time);
               pstmt6.setInt(51, afb);
               pstmt6.setString(52, course);

               count = pstmt6.executeUpdate();      // execute the prepared stmt

               pstmt6.close();

               if (count > 0) {
                   
                   // Create a tee time history entry
                   String fullName = "Lottery Request Conversion (Convert-All)";
                   int hist_type = 3;
                   
                   SystemUtils.updateHist(date, day, time, afb, course, g1player1, g1player2, g1player3, g1player4, g1player5, user, fullName, hist_type, con);
               }

               //
               //  Get this tee time's id and save in the lottery request for this group
               //
               teecurr_id = SystemUtils.getTeeCurrId(date, time, afb, course, con);

               pstmt6 = con.prepareStatement("UPDATE lreqs3 SET teecurr_id1 = ? WHERE id = ?");
               pstmt6.clearParameters();
               pstmt6.setInt(1, teecurr_id);
               pstmt6.setLong(2, id);
               pstmt6.executeUpdate();
               pstmt6.close();



               //
               //  Do next group, if there is one
               //
               if (groups > 1) {

                  time = atime2;    // time for this tee time
                  hndcp1 = 99;      // init
                  hndcp2 = 99;
                  hndcp3 = 99;
                  hndcp4 = 99;
                  hndcp5 = 99;
                  mNum1 = "";
                  mNum2 = "";
                  mNum3 = "";
                  mNum4 = "";
                  mNum5 = "";

                  if (p5.equals( "Yes" )) {

                     g2player1 = player6;
                     g2player2 = player7;
                     g2player3 = player8;
                     g2player4 = player9;
                     g2player5 = player10;
                     g2user1 = user6;
                     g2user2 = user7;
                     g2user3 = user8;
                     g2user4 = user9;
                     g2user5 = user10;
                     g2p1cw = p6cw;
                     g2p2cw = p7cw;
                     g2p3cw = p8cw;
                     g2p4cw = p9cw;
                     g2p5cw = p10cw;
                     g2userg1 = userg6;
                     g2userg2 = userg7;
                     g2userg3 = userg8;
                     g2userg4 = userg9;
                     g2userg5 = userg10;
                     g2orig1 = orig6;
                     g2orig2 = orig7;
                     g2orig3 = orig8;
                     g2orig4 = orig9;
                     g2orig5 = orig10;
                     g2p91 = p96;
                     g2p92 = p97;
                     g2p93 = p98;
                     g2p94 = p99;
                     g2p95 = p910;
                     g2weight1 = weight6;
                     g2weight2 = weight7;
                     g2weight3 = weight8;
                     g2weight4 = weight9;
                     g2weight5 = weight10;

                  } else {

                     g2player1 = player5;
                     g2player2 = player6;
                     g2player3 = player7;
                     g2player4 = player8;
                     g2user1 = user5;
                     g2user2 = user6;
                     g2user3 = user7;
                     g2user4 = user8;
                     g2p1cw = p5cw;
                     g2p2cw = p6cw;
                     g2p3cw = p7cw;
                     g2p4cw = p8cw;
                     g2userg1 = userg5;
                     g2userg2 = userg6;
                     g2userg3 = userg7;
                     g2userg4 = userg8;
                     g2orig1 = orig5;
                     g2orig2 = orig6;
                     g2orig3 = orig7;
                     g2orig4 = orig8;
                     g2p91 = p95;
                     g2p92 = p96;
                     g2p93 = p97;
                     g2p94 = p98;
                     g2weight1 = weight5;
                     g2weight2 = weight6;
                     g2weight3 = weight7;
                     g2weight4 = weight8;
                  }

                  //
                  //  Calculate the requested time for this group
                  //
                  rtime = SystemUtils.calcRtime(rtime, 2, interval);


                  if (!g2user1.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g2user1);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum1 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g2user1, name, date, mins, rtime, courseReq, time, course, g2weight1, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  if (!g2user2.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g2user2);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum2 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g2user2, name, date, mins, rtime, courseReq, time, course, g2weight2, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  if (!g2user3.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g2user3);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum3 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g2user3, name, date, mins, rtime, courseReq, time, course, g2weight3, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  if (!g2user4.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g2user4);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum4 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g2user4, name, date, mins, rtime, courseReq, time, course, g2weight4, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  if (!g2user5.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g2user5);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum5 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g2user5, name, date, mins, rtime, courseReq, time, course, g2weight5, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }

                  if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                     mNum1 = "";                  // convert back to null
                  }
                  if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                     mNum2 = "";                  // convert back to null
                  }
                  if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                     mNum3 = "";                  // convert back to null
                  }
                  if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                     mNum4 = "";                  // convert back to null
                  }
                  if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                     mNum5 = "";                  // convert back to null
                  }

                  try {

                      lottery_email = tmp_emailFlag;

                      pstmt2 = con.prepareStatement("SELECT lottery FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");
                      pstmt2.clearParameters();

                      pstmt2.setLong(1, date);
                      pstmt2.setInt(2, time);
                      pstmt2.setInt(3, afb2);
                      pstmt2.setString(4, course);

                      rs3 = pstmt2.executeQuery();

                      if (rs3.next()) {
                          if (rs3.getString("lottery").equals("")) {
                              lottery_email = 0;
                          }
                      } else {
                          lottery_email = tmp_emailFlag;
                      }

                      pstmt2.close();

                  } catch (Exception exc) {
                      lottery_email = tmp_emailFlag;
                  }

                  errorMsg = "Error in Common_Lott moveReqs (put group 2 in tee sheet): ";
                  //
                  //  Update the tee slot in teecurr
                  //
                  //  Clear the lottery name so this tee time is displayed in _sheet even though there
                  //  may be some requests still outstanding (state = 4).
                  //
                  pstmt6 = con.prepareStatement (
                     "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                     "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                     "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                     "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " + // lottery = '',
                     "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, orig1 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ?, " + 
                     "lottery_email = ?, last_mod_date = now() " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt6.clearParameters();        // clear the parms
                  pstmt6.setString(1, g2player1);
                  pstmt6.setString(2, g2player2);
                  pstmt6.setString(3, g2player3);
                  pstmt6.setString(4, g2player4);
                  pstmt6.setString(5, g2user1);
                  pstmt6.setString(6, g2user2);
                  pstmt6.setString(7, g2user3);
                  pstmt6.setString(8, g2user4);
                  pstmt6.setString(9, g2p1cw);
                  pstmt6.setString(10, g2p2cw);
                  pstmt6.setString(11, g2p3cw);
                  pstmt6.setString(12, g2p4cw);
                  pstmt6.setFloat(13, hndcp1);
                  pstmt6.setFloat(14, hndcp2);
                  pstmt6.setFloat(15, hndcp3);
                  pstmt6.setFloat(16, hndcp4);
                  pstmt6.setString(17, g2player5);
                  pstmt6.setString(18, g2user5);
                  pstmt6.setString(19, g2p5cw);
                  pstmt6.setFloat(20, hndcp5);
                  pstmt6.setString(21, notes);
                  pstmt6.setInt(22, hide);
                  pstmt6.setInt(23, proNew);
                  pstmt6.setInt(24, proMod);
                  pstmt6.setInt(25, memNew);
                  pstmt6.setInt(26, memMod);
                  pstmt6.setString(27, mNum1);
                  pstmt6.setString(28, mNum2);
                  pstmt6.setString(29, mNum3);
                  pstmt6.setString(30, mNum4);
                  pstmt6.setString(31, mNum5);
                  pstmt6.setString(32, g2userg1);
                  pstmt6.setString(33, g2userg2);
                  pstmt6.setString(34, g2userg3);
                  pstmt6.setString(35, g2userg4);
                  pstmt6.setString(36, g2userg5);
                  pstmt6.setString(37, orig_by);
                  pstmt6.setInt(38, g2p91);
                  pstmt6.setInt(39, g2p92);
                  pstmt6.setInt(40, g2p93);
                  pstmt6.setInt(41, g2p94);
                  pstmt6.setInt(42, g2p95);
                  pstmt6.setString(43, g2orig1);
                  pstmt6.setString(44, g2orig2);
                  pstmt6.setString(45, g2orig3);
                  pstmt6.setString(46, g2orig4);
                  pstmt6.setString(47, g2orig5);
                  pstmt6.setInt(48, lottery_email);

                  pstmt6.setLong(49, date);
                  pstmt6.setInt(50, time);
                  pstmt6.setInt(51, afb2);
                  pstmt6.setString(52, course);

                  count = pstmt6.executeUpdate();      // execute the prepared stmt

                  pstmt6.close();

                  if (count > 0) {
                      
                      // Create a tee time history entry
                      String fullName = "Lottery Request Conversion (Convert-All)";
                      int hist_type = 3;
                      
                      SystemUtils.updateHist(date, day, time, afb2, course, g2player1, g2player2, g2player3, g2player4, g2player5, user, fullName, hist_type, con);
                  }

                  //
                  //  Get this tee time's id and save in the lottery request for this group
                  //
                  teecurr_id = SystemUtils.getTeeCurrId(date, time, afb2, course, con);

                  pstmt6 = con.prepareStatement("UPDATE lreqs3 SET teecurr_id2 = ? WHERE id = ?");
                  pstmt6.clearParameters();
                  pstmt6.setInt(1, teecurr_id);
                  pstmt6.setLong(2, id);
                  pstmt6.executeUpdate();
                  pstmt6.close();

               }    // end of IF groups

               //
               //  Do next group, if there is one
               //
               if (groups > 2) {

                  time = atime3;    // time for this tee time
                  hndcp1 = 99;      // init
                  hndcp2 = 99;
                  hndcp3 = 99;
                  hndcp4 = 99;
                  hndcp5 = 99;
                  mNum1 = "";
                  mNum2 = "";
                  mNum3 = "";
                  mNum4 = "";
                  mNum5 = "";

                  if (p5.equals( "Yes" )) {

                     g3player1 = player11;
                     g3player2 = player12;
                     g3player3 = player13;
                     g3player4 = player14;
                     g3player5 = player15;
                     g3user1 = user11;
                     g3user2 = user12;
                     g3user3 = user13;
                     g3user4 = user14;
                     g3user5 = user15;
                     g3p1cw = p11cw;
                     g3p2cw = p12cw;
                     g3p3cw = p13cw;
                     g3p4cw = p14cw;
                     g3p5cw = p15cw;
                     g3userg1 = userg11;
                     g3userg2 = userg12;
                     g3userg3 = userg13;
                     g3userg4 = userg14;
                     g3userg5 = userg15;
                     g3orig1 = orig11;
                     g3orig2 = orig12;
                     g3orig3 = orig13;
                     g3orig4 = orig14;
                     g3orig5 = orig15;
                     g3p91 = p911;
                     g3p92 = p912;
                     g3p93 = p913;
                     g3p94 = p914;
                     g3p95 = p915;
                     g3weight1 = weight11;
                     g3weight2 = weight12;
                     g3weight3 = weight13;
                     g3weight4 = weight14;
                     g3weight5 = weight15;

                  } else {

                     g3player1 = player9;
                     g3player2 = player10;
                     g3player3 = player11;
                     g3player4 = player12;
                     g3user1 = user9;
                     g3user2 = user10;
                     g3user3 = user11;
                     g3user4 = user12;
                     g3p1cw = p9cw;
                     g3p2cw = p10cw;
                     g3p3cw = p11cw;
                     g3p4cw = p12cw;
                     g3userg1 = userg9;
                     g3userg2 = userg10;
                     g3userg3 = userg11;
                     g3userg4 = userg12;
                     g3orig1 = orig9;
                     g3orig2 = orig10;
                     g3orig3 = orig11;
                     g3orig4 = orig12;
                     g3p91 = p99;
                     g3p92 = p910;
                     g3p93 = p911;
                     g3p94 = p912;
                     g3weight1 = weight9;
                     g3weight2 = weight10;
                     g3weight3 = weight11;
                     g3weight4 = weight12;
                  }

                  //
                  //  Calculate the requested time for this group
                  //
                  rtime = SystemUtils.calcRtime(rtime, 2, interval);


                  if (!g3user1.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g3user1);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum1 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g3user1, name, date, mins, rtime, courseReq, time, course, g3weight1, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  if (!g3user2.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g3user2);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum2 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g3user2, name, date, mins, rtime, courseReq, time, course, g3weight2, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  if (!g3user3.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g3user3);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum3 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g3user3, name, date, mins, rtime, courseReq, time, course, g3weight3, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  if (!g3user4.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g3user4);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum4 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g3user4, name, date, mins, rtime, courseReq, time, course, g3weight4, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  if (p5.equals( "Yes" )) {

                     if (!g3user5.equals( "" )) {        // if player is a member

                        parm = SystemUtils.getUser(con, g3user5);     // get mNum and hndcp for member

                        StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                        mNum5 = tok.nextToken();         // member Number
                        hndcps = tok.nextToken();       // handicap

                        hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int

                        if (sendEmailsNow == true) SystemUtils.logAssign(g3user5, name, date, mins, rtime, courseReq, time, course, g3weight5, weight, id, con);  // log the assigned time in lassigns5 for this member
                     }
                  }

                  if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                     mNum1 = "";                  // convert back to null
                  }
                  if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                     mNum2 = "";                  // convert back to null
                  }
                  if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                     mNum3 = "";                  // convert back to null
                  }
                  if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                     mNum4 = "";                  // convert back to null
                  }
                  if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                     mNum5 = "";                  // convert back to null
                  }

                  try {

                      lottery_email = tmp_emailFlag;

                      pstmt2 = con.prepareStatement("SELECT lottery FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");
                      pstmt2.clearParameters();

                      pstmt2.setLong(1, date);
                      pstmt2.setInt(2, time);
                      pstmt2.setInt(3, afb3);
                      pstmt2.setString(4, course);

                      rs3 = pstmt2.executeQuery();

                      if (rs3.next()) {
                          if (rs3.getString("lottery").equals("")) {
                              lottery_email = 0;
                          }
                      } else {
                          lottery_email = tmp_emailFlag;
                      }

                      pstmt2.close();

                  } catch (Exception exc) {
                      lottery_email = tmp_emailFlag;
                  }

                  errorMsg = "Error in Common_Lott moveReqs (put group 3 in tee sheet): ";
                  //
                  //  Update the tee slot in teecurr
                  //
                  //  Clear the lottery name so this tee time is displayed in _sheet even though there
                  //  may be some requests still outstanding (state = 4).
                  //
                  pstmt6 = con.prepareStatement (
                     "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                     "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                     "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                     "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " + // lottery = '',
                     "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, orig1 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ?, " +
                     "lottery_email = ?, last_mod_date = now() " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt6.clearParameters();        // clear the parms
                  pstmt6.setString(1, g3player1);
                  pstmt6.setString(2, g3player2);
                  pstmt6.setString(3, g3player3);
                  pstmt6.setString(4, g3player4);
                  pstmt6.setString(5, g3user1);
                  pstmt6.setString(6, g3user2);
                  pstmt6.setString(7, g3user3);
                  pstmt6.setString(8, g3user4);
                  pstmt6.setString(9, g3p1cw);
                  pstmt6.setString(10, g3p2cw);
                  pstmt6.setString(11, g3p3cw);
                  pstmt6.setString(12, g3p4cw);
                  pstmt6.setFloat(13, hndcp1);
                  pstmt6.setFloat(14, hndcp2);
                  pstmt6.setFloat(15, hndcp3);
                  pstmt6.setFloat(16, hndcp4);
                  pstmt6.setString(17, g3player5);
                  pstmt6.setString(18, g3user5);
                  pstmt6.setString(19, g3p5cw);
                  pstmt6.setFloat(20, hndcp5);
                  pstmt6.setString(21, notes);
                  pstmt6.setInt(22, hide);
                  pstmt6.setInt(23, proNew);
                  pstmt6.setInt(24, proMod);
                  pstmt6.setInt(25, memNew);
                  pstmt6.setInt(26, memMod);
                  pstmt6.setString(27, mNum1);
                  pstmt6.setString(28, mNum2);
                  pstmt6.setString(29, mNum3);
                  pstmt6.setString(30, mNum4);
                  pstmt6.setString(31, mNum5);
                  pstmt6.setString(32, g3userg1);
                  pstmt6.setString(33, g3userg2);
                  pstmt6.setString(34, g3userg3);
                  pstmt6.setString(35, g3userg4);
                  pstmt6.setString(36, g3userg5);
                  pstmt6.setString(37, orig_by);
                  pstmt6.setInt(38, g3p91);
                  pstmt6.setInt(39, g3p92);
                  pstmt6.setInt(40, g3p93);
                  pstmt6.setInt(41, g3p94);
                  pstmt6.setInt(42, g3p95);
                  pstmt6.setString(43, g3orig1);
                  pstmt6.setString(44, g3orig2);
                  pstmt6.setString(45, g3orig3);
                  pstmt6.setString(46, g3orig4);
                  pstmt6.setString(47, g3orig5);
                  pstmt6.setInt(48, lottery_email);

                  pstmt6.setLong(49, date);
                  pstmt6.setInt(50, time);
                  pstmt6.setInt(51, afb3);
                  pstmt6.setString(52, course);

                  count = pstmt6.executeUpdate();      // execute the prepared stmt

                  pstmt6.close();

                  if (count > 0) {
                      
                      // Create a tee time history entry
                      String fullName = "Lottery Request Conversion (Convert-All)";
                      int hist_type = 3;
                      
                      SystemUtils.updateHist(date, day, time, afb3, course, g3player1, g3player2, g3player3, g3player4, g3player5, user, fullName, hist_type, con);
                  }

                  //
                  //  Get this tee time's id and save in the lottery request for this group
                  //
                  teecurr_id = SystemUtils.getTeeCurrId(date, time, afb3, course, con);

                  pstmt6 = con.prepareStatement("UPDATE lreqs3 SET teecurr_id3 = ? WHERE id = ?");
                  pstmt6.clearParameters();
                  pstmt6.setInt(1, teecurr_id);
                  pstmt6.setLong(2, id);
                  pstmt6.executeUpdate();
                  pstmt6.close();

               }    // end of IF groups

               //
               //  Do next group, if there is one
               //
               if (groups > 3) {

                  time = atime4;    // time for this tee time
                  hndcp1 = 99;      // init
                  hndcp2 = 99;
                  hndcp3 = 99;
                  hndcp4 = 99;
                  hndcp5 = 99;
                  mNum1 = "";
                  mNum2 = "";
                  mNum3 = "";
                  mNum4 = "";
                  mNum5 = "";

                  if (p5.equals( "Yes" )) {

                     g4player1 = player16;
                     g4player2 = player17;
                     g4player3 = player18;
                     g4player4 = player19;
                     g4player5 = player20;
                     g4user1 = user16;
                     g4user2 = user17;
                     g4user3 = user18;
                     g4user4 = user19;
                     g4user5 = user20;
                     g4p1cw = p16cw;
                     g4p2cw = p17cw;
                     g4p3cw = p18cw;
                     g4p4cw = p19cw;
                     g4p5cw = p20cw;
                     g4userg1 = userg16;
                     g4userg2 = userg17;
                     g4userg3 = userg18;
                     g4userg4 = userg19;
                     g4userg5 = userg20;
                     g4orig1 = orig16;
                     g4orig2 = orig17;
                     g4orig3 = orig18;
                     g4orig4 = orig19;
                     g4orig5 = orig20;
                     g4p91 = p916;
                     g4p92 = p917;
                     g4p93 = p918;
                     g4p94 = p919;
                     g4p95 = p920;
                     g4weight1 = weight16;
                     g4weight2 = weight17;
                     g4weight3 = weight18;
                     g4weight4 = weight19;
                     g4weight5 = weight20;

                  } else {

                     g4player1 = player13;
                     g4player2 = player14;
                     g4player3 = player15;
                     g4player4 = player16;
                     g4user1 = user13;
                     g4user2 = user14;
                     g4user3 = user15;
                     g4user4 = user16;
                     g4p1cw = p13cw;
                     g4p2cw = p14cw;
                     g4p3cw = p15cw;
                     g4p4cw = p16cw;
                     g4userg1 = userg13;
                     g4userg2 = userg14;
                     g4userg3 = userg15;
                     g4userg4 = userg16;
                     g4orig1 = orig13;
                     g4orig2 = orig14;
                     g4orig3 = orig15;
                     g4orig4 = orig16;
                     g4p91 = p913;
                     g4p92 = p914;
                     g4p93 = p915;
                     g4p94 = p916;
                     g4weight1 = weight13;
                     g4weight2 = weight14;
                     g4weight3 = weight15;
                     g4weight4 = weight16;
                  }

                  //
                  //  Calculate the requested time for this group
                  //
                  rtime = SystemUtils.calcRtime(rtime, 2, interval);


                  if (!g4user1.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g4user1);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum1 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g4user1, name, date, mins, rtime, courseReq, time, course, g4weight1, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  if (!g4user2.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g4user2);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum2 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g4user2, name, date, mins, rtime, courseReq, time, course, g4weight2, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  if (!g4user3.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g4user3);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum3 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g4user3, name, date, mins, rtime, courseReq, time, course, g4weight3, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  if (!g4user4.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g4user4);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum4 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g4user4, name, date, mins, rtime, courseReq, time, course, g4weight4, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  if (p5.equals( "Yes" )) {

                     if (!g4user5.equals( "" )) {        // if player is a member

                        parm = SystemUtils.getUser(con, g4user5);     // get mNum and hndcp for member

                        StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                        mNum5 = tok.nextToken();         // member Number
                        hndcps = tok.nextToken();       // handicap

                        hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int

                        if (sendEmailsNow == true) SystemUtils.logAssign(g4user5, name, date, mins, rtime, courseReq, time, course, g4weight5, weight, id, con);  // log the assigned time in lassigns5 for this member
                     }
                  }

                  if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                     mNum1 = "";                  // convert back to null
                  }
                  if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                     mNum2 = "";                  // convert back to null
                  }
                  if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                     mNum3 = "";                  // convert back to null
                  }
                  if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                     mNum4 = "";                  // convert back to null
                  }
                  if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                     mNum5 = "";                  // convert back to null
                  }

                  try {

                      lottery_email = tmp_emailFlag;

                      pstmt2 = con.prepareStatement("SELECT lottery FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");
                      pstmt2.clearParameters();

                      pstmt2.setLong(1, date);
                      pstmt2.setInt(2, time);
                      pstmt2.setInt(3, afb4);
                      pstmt2.setString(4, course);

                      rs3 = pstmt2.executeQuery();

                      if (rs3.next()) {
                          if (rs3.getString("lottery").equals("")) {
                              lottery_email = 0;
                          }
                      } else {
                          lottery_email = tmp_emailFlag;
                      }

                      pstmt2.close();

                  } catch (Exception exc) {
                      lottery_email = tmp_emailFlag;
                  }

                  errorMsg = "Error in Common_Lott moveReqs (put group 4 in tee sheet): ";
                  //
                  //  Update the tee slot in teecurr
                  //
                  //  Clear the lottery name so this tee time is displayed in _sheet even though there
                  //  may be some requests still outstanding (state = 4).
                  //
                  pstmt6 = con.prepareStatement (
                     "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                     "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                     "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                     "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " + // lottery = '',
                     "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, orig1 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ?, " +
                     "lottery_email = ?, last_mod_date = now() " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt6.clearParameters();        // clear the parms
                  pstmt6.setString(1, g4player1);
                  pstmt6.setString(2, g4player2);
                  pstmt6.setString(3, g4player3);
                  pstmt6.setString(4, g4player4);
                  pstmt6.setString(5, g4user1);
                  pstmt6.setString(6, g4user2);
                  pstmt6.setString(7, g4user3);
                  pstmt6.setString(8, g4user4);
                  pstmt6.setString(9, g4p1cw);
                  pstmt6.setString(10, g4p2cw);
                  pstmt6.setString(11, g4p3cw);
                  pstmt6.setString(12, g4p4cw);
                  pstmt6.setFloat(13, hndcp1);
                  pstmt6.setFloat(14, hndcp2);
                  pstmt6.setFloat(15, hndcp3);
                  pstmt6.setFloat(16, hndcp4);
                  pstmt6.setString(17, g4player5);
                  pstmt6.setString(18, g4user5);
                  pstmt6.setString(19, g4p5cw);
                  pstmt6.setFloat(20, hndcp5);
                  pstmt6.setString(21, notes);
                  pstmt6.setInt(22, hide);
                  pstmt6.setInt(23, proNew);
                  pstmt6.setInt(24, proMod);
                  pstmt6.setInt(25, memNew);
                  pstmt6.setInt(26, memMod);
                  pstmt6.setString(27, mNum1);
                  pstmt6.setString(28, mNum2);
                  pstmt6.setString(29, mNum3);
                  pstmt6.setString(30, mNum4);
                  pstmt6.setString(31, mNum5);
                  pstmt6.setString(32, g4userg1);
                  pstmt6.setString(33, g4userg2);
                  pstmt6.setString(34, g4userg3);
                  pstmt6.setString(35, g4userg4);
                  pstmt6.setString(36, g4userg5);
                  pstmt6.setString(37, orig_by);
                  pstmt6.setInt(38, g4p91);
                  pstmt6.setInt(39, g4p92);
                  pstmt6.setInt(40, g4p93);
                  pstmt6.setInt(41, g4p94);
                  pstmt6.setInt(42, g4p95);
                  pstmt6.setString(43, g4orig1);
                  pstmt6.setString(44, g4orig2);
                  pstmt6.setString(45, g4orig3);
                  pstmt6.setString(46, g4orig4);
                  pstmt6.setString(47, g4orig5);
                  pstmt6.setInt(48, lottery_email);

                  pstmt6.setLong(49, date);
                  pstmt6.setInt(50, time);
                  pstmt6.setInt(51, afb4);
                  pstmt6.setString(52, course);

                  count = pstmt6.executeUpdate();      // execute the prepared stmt

                  pstmt6.close();

                  if (count > 0) {
                      
                      // Create a tee time history entry
                      String fullName = "Lottery Request Conversion (Convert-All)";
                      int hist_type = 3;
                      
                      SystemUtils.updateHist(date, day, time, afb4, course, g4player1, g4player2, g4player3, g4player4, g4player5, user, fullName, hist_type, con);
                  }

                  //
                  //  Get this tee time's id and save in the lottery request for this group
                  //
                  teecurr_id = SystemUtils.getTeeCurrId(date, time, afb4, course, con);

                  pstmt6 = con.prepareStatement("UPDATE lreqs3 SET teecurr_id4 = ? WHERE id = ?");
                  pstmt6.clearParameters();
                  pstmt6.setInt(1, teecurr_id);
                  pstmt6.setLong(2, id);
                  pstmt6.executeUpdate();
                  pstmt6.close();

               }    // end of IF groups

               //
               //  Do next group, if there is one
               //
               if (groups > 4) {

                  time = atime5;    // time for this tee time
                  hndcp1 = 99;      // init
                  hndcp2 = 99;
                  hndcp3 = 99;
                  hndcp4 = 99;
                  hndcp5 = 99;
                  mNum1 = "";
                  mNum2 = "";
                  mNum3 = "";
                  mNum4 = "";
                  mNum5 = "";

                  if (p5.equals( "Yes" )) {

                     g5player1 = player21;
                     g5player2 = player22;
                     g5player3 = player23;
                     g5player4 = player24;
                     g5player5 = player25;
                     g5user1 = user21;
                     g5user2 = user22;
                     g5user3 = user23;
                     g5user4 = user24;
                     g5user5 = user25;
                     g5p1cw = p21cw;
                     g5p2cw = p22cw;
                     g5p3cw = p23cw;
                     g5p4cw = p24cw;
                     g5p5cw = p25cw;
                     g5userg1 = userg21;
                     g5userg2 = userg22;
                     g5userg3 = userg23;
                     g5userg4 = userg24;
                     g5userg5 = userg25;
                     g5orig1 = orig21;
                     g5orig2 = orig22;
                     g5orig3 = orig23;
                     g5orig4 = orig24;
                     g5orig5 = orig25;
                     g5p91 = p921;
                     g5p92 = p922;
                     g5p93 = p923;
                     g5p94 = p924;
                     g5p95 = p925;
                     g5weight1 = weight21;
                     g5weight2 = weight22;
                     g5weight3 = weight23;
                     g5weight4 = weight24;
                     g5weight5 = weight25;

                  } else {

                     g5player1 = player17;
                     g5player2 = player18;
                     g5player3 = player19;
                     g5player4 = player20;
                     g5user1 = user17;
                     g5user2 = user18;
                     g5user3 = user19;
                     g5user4 = user20;
                     g5p1cw = p17cw;
                     g5p2cw = p18cw;
                     g5p3cw = p19cw;
                     g5p4cw = p20cw;
                     g5userg1 = userg17;
                     g5userg2 = userg18;
                     g5userg3 = userg19;
                     g5userg4 = userg20;
                     g5orig1 = orig17;
                     g5orig2 = orig18;
                     g5orig3 = orig19;
                     g5orig4 = orig20;
                     g5p91 = p917;
                     g5p92 = p918;
                     g5p93 = p919;
                     g5p94 = p920;
                     g5weight1 = weight17;
                     g5weight2 = weight18;
                     g5weight3 = weight19;
                     g5weight4 = weight20;
                  }

                  //
                  //  Calculate the requested time for this group
                  //
                  rtime = SystemUtils.calcRtime(rtime, 2, interval);


                  if (!g5user1.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g5user1);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum1 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g5user1, name, date, mins, rtime, courseReq, time, course, g5weight1, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  if (!g5user2.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g5user2);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum2 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g5user2, name, date, mins, rtime, courseReq, time, course, g5weight2, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  if (!g5user3.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g5user3);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum3 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g5user3, name, date, mins, rtime, courseReq, time, course, g5weight3, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  if (!g5user4.equals( "" )) {        // if player is a member

                     parm = SystemUtils.getUser(con, g5user4);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum4 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int

                     if (sendEmailsNow == true) SystemUtils.logAssign(g5user4, name, date, mins, rtime, courseReq, time, course, g5weight4, weight, id, con);  // log the assigned time in lassigns5 for this member
                  }
                  if (p5.equals( "Yes" )) {

                     if (!g5user5.equals( "" )) {        // if player is a member

                        parm = SystemUtils.getUser(con, g5user5);     // get mNum and hndcp for member

                        StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                        mNum5 = tok.nextToken();         // member Number
                        hndcps = tok.nextToken();       // handicap

                        hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int

                        if (sendEmailsNow == true) SystemUtils.logAssign(g5user5, name, date, mins, rtime, courseReq, time, course, g5weight5, weight, id, con);  // log the assigned time in lassigns5 for this member
                     }
                  }

                  if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                     mNum1 = "";                  // convert back to null
                  }
                  if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                     mNum2 = "";                  // convert back to null
                  }
                  if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                     mNum3 = "";                  // convert back to null
                  }
                  if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                     mNum4 = "";                  // convert back to null
                  }
                  if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                     mNum5 = "";                  // convert back to null
                  }

                  try {

                      lottery_email = tmp_emailFlag;

                      pstmt2 = con.prepareStatement("SELECT lottery FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");
                      pstmt2.clearParameters();

                      pstmt2.setLong(1, date);
                      pstmt2.setInt(2, time);
                      pstmt2.setInt(3, afb5);
                      pstmt2.setString(4, course);

                      rs3 = pstmt2.executeQuery();

                      if (rs3.next()) {
                          if (rs3.getString("lottery").equals("")) {
                              lottery_email = 0;
                          }
                      } else {
                          lottery_email = tmp_emailFlag;
                      }

                      pstmt2.close();

                  } catch (Exception exc) {
                      lottery_email = tmp_emailFlag;
                  }

                  errorMsg = "Error in Common_Lott moveReqs (put group 5 in tee sheet): ";
                  //
                  //  Update the tee slot in teecurr
                  //
                  //  Clear the lottery name so this tee time is displayed in _sheet even though there
                  //  may be some requests still outstanding (state = 4).
                  //
                  pstmt6 = con.prepareStatement (
                     "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                     "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                     "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                     "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " + // lottery = '',
                     "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, orig1 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ?, " + 
                     "lottery_email = ?, last_mod_date = now() " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt6.clearParameters();        // clear the parms
                  pstmt6.setString(1, g5player1);
                  pstmt6.setString(2, g5player2);
                  pstmt6.setString(3, g5player3);
                  pstmt6.setString(4, g5player4);
                  pstmt6.setString(5, g5user1);
                  pstmt6.setString(6, g5user2);
                  pstmt6.setString(7, g5user3);
                  pstmt6.setString(8, g5user4);
                  pstmt6.setString(9, g5p1cw);
                  pstmt6.setString(10, g5p2cw);
                  pstmt6.setString(11, g5p3cw);
                  pstmt6.setString(12, g5p4cw);
                  pstmt6.setFloat(13, hndcp1);
                  pstmt6.setFloat(14, hndcp2);
                  pstmt6.setFloat(15, hndcp3);
                  pstmt6.setFloat(16, hndcp4);
                  pstmt6.setString(17, g5player5);
                  pstmt6.setString(18, g5user5);
                  pstmt6.setString(19, g5p5cw);
                  pstmt6.setFloat(20, hndcp5);
                  pstmt6.setString(21, notes);
                  pstmt6.setInt(22, hide);
                  pstmt6.setInt(23, proNew);
                  pstmt6.setInt(24, proMod);
                  pstmt6.setInt(25, memNew);
                  pstmt6.setInt(26, memMod);
                  pstmt6.setString(27, mNum1);
                  pstmt6.setString(28, mNum2);
                  pstmt6.setString(29, mNum3);
                  pstmt6.setString(30, mNum4);
                  pstmt6.setString(31, mNum5);
                  pstmt6.setString(32, g5userg1);
                  pstmt6.setString(33, g5userg2);
                  pstmt6.setString(34, g5userg3);
                  pstmt6.setString(35, g5userg4);
                  pstmt6.setString(36, g5userg5);
                  pstmt6.setString(37, orig_by);
                  pstmt6.setInt(38, g5p91);
                  pstmt6.setInt(39, g5p92);
                  pstmt6.setInt(40, g5p93);
                  pstmt6.setInt(41, g5p94);
                  pstmt6.setInt(42, g5p95);
                  pstmt6.setString(43, g5orig1);
                  pstmt6.setString(44, g5orig2);
                  pstmt6.setString(45, g5orig3);
                  pstmt6.setString(46, g5orig4);
                  pstmt6.setString(47, g5orig5);
                  pstmt6.setInt(48, lottery_email);

                  pstmt6.setLong(49, date);
                  pstmt6.setInt(50, time);
                  pstmt6.setInt(51, afb5);
                  pstmt6.setString(52, course);

                  count = pstmt6.executeUpdate();      // execute the prepared stmt

                  pstmt6.close();

                  if (count > 0) {
                      
                      // Create a tee time history entry
                      String fullName = "Lottery Request Conversion (Convert-All)";
                      int hist_type = 3;
                      
                      SystemUtils.updateHist(date, day, time, afb5, course, g5player1, g5player2, g5player3, g5player4, g5player5, user, fullName, hist_type, con);
                  }

                  //
                  //  Get this tee time's id and save in the lottery request for this group
                  //
                  teecurr_id = SystemUtils.getTeeCurrId(date, time, afb5, course, con);

                  pstmt6 = con.prepareStatement("UPDATE lreqs3 SET teecurr_id5 = ? WHERE id = ?");
                  pstmt6.clearParameters();
                  pstmt6.setInt(1, teecurr_id);
                  pstmt6.setLong(2, id);
                  pstmt6.executeUpdate();
                  pstmt6.close();

               }    // end of IF groups

               if (sendEmailsNow == true) {            // if we should send emails now (FALSE if from Proshop_dsheet)


                   //
                   // LETS MAKE THIS USE THE NEW sendEmail
                   //




                  //*****************************************************************************
                  //  Send an email to all in this request
                  //*****************************************************************************
                  //
                  errorMsg = "Error in Common_Lott moveReqs (send email): ";

                  String clubName = "";

                  try {

                     estmt = con.createStatement();        // create a statement

                     rs2 = estmt.executeQuery("SELECT clubName " +
                                             "FROM club5 WHERE clubName != ''");

                     if (rs2.next()) {

                        clubName = rs2.getString(1);
                     }
                     estmt.close();
                  }
                  catch (Exception ignore) {
                  }

                  //
                  //  Get today's date and time for email processing
                  //
                  Calendar ecal = new GregorianCalendar();               // get todays date
                  int eyear = ecal.get(Calendar.YEAR);
                  int emonth = ecal.get(Calendar.MONTH);
                  int eday = ecal.get(Calendar.DAY_OF_MONTH);
                  int e_hourDay = ecal.get(Calendar.HOUR_OF_DAY);
                  int e_min = ecal.get(Calendar.MINUTE);

                  int e_time = 0;
                  long e_date = 0;

                  //
                  //  Build the 'time' string for display
                  //
                  //    Adjust the time based on the club's time zone (we are Central)
                  //
                  e_time = (e_hourDay * 100) + e_min;

                  e_time = SystemUtils.adjustTime(con, e_time);       // adjust for time zone

                  if (e_time < 0) {          // if negative, then we went back or ahead one day

                     e_time = 0 - e_time;        // convert back to positive value

                     if (e_time < 100) {           // if hour is zero, then we rolled ahead 1 day

                        //
                        // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                        //
                        ecal.add(Calendar.DATE,1);                     // get next day's date

                        eyear = ecal.get(Calendar.YEAR);
                        emonth = ecal.get(Calendar.MONTH);
                        eday = ecal.get(Calendar.DAY_OF_MONTH);

                     } else {                        // we rolled back 1 day

                        //
                        // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                        //
                        ecal.add(Calendar.DATE,-1);                     // get yesterday's date

                        eyear = ecal.get(Calendar.YEAR);
                        emonth = ecal.get(Calendar.MONTH);
                        eday = ecal.get(Calendar.DAY_OF_MONTH);
                     }
                  }

                  int e_hour = e_time / 100;                // get adjusted hour
                  e_min = e_time - (e_hour * 100);          // get minute value
                  int e_am_pm = 0;                         // preset to AM

                  if (e_hour > 11) {

                     e_am_pm = 1;                // PM
                     e_hour = e_hour - 12;       // set to 12 hr clock
                  }
                  if (e_hour == 0) {

                     e_hour = 12;
                  }

                  String email_time = "";

                  emonth = emonth + 1;                            // month starts at zero
                  e_date = (eyear * 10000) + (emonth * 100) + eday;

                  //
                  //  get date/time string for email message
                  //
                  if (e_am_pm == 0) {
                     if (e_min < 10) {
                        email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":0" + e_min + " AM";
                     } else {
                        email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":" + e_min + " AM";
                     }
                  } else {
                     if (e_min < 10) {
                        email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":0" + e_min + " PM";
                     } else {
                        email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":" + e_min + " PM";
                     }
                  }

                  //
                  //***********************************************
                  //  Send email notification if necessary
                  //***********************************************
                  //
                  String to = "";                          // to address
                  String f_b = "";
                  String eampm = "";
                  String etime = "";
                  String enewMsg = "";
                  int emailOpt = 0;                        // user's email option parm
                  int ehr = 0;
                  int emin = 0;
                  int send = 0;

                  PreparedStatement pstmte1 = null;

                  //
                  //  set the front/back value
                  //
                  f_b = "Front";

                  if (afb == 1) {

                     f_b = "Back";
                  }

                  String enew1 = "";
                  String enew2 = "";
                  String subject = "";

                  if (clubName.startsWith( "Old Oaks" )) {

                     enew1 = "The following Tee Time has been ASSIGNED.\n\n";
                     enew2 = "The following Tee Times have been ASSIGNED.\n\n";
                     subject = "ForeTees Tee Time Assignment Notification";

                  } else {

                     if (clubName.startsWith( "Westchester" )) {

                        enew1 = "The following Draw Tee Time has been ASSIGNED.\n\n";
                        enew2 = "The following Draw Tee Times have been ASSIGNED.\n\n";
                        subject = "Your Tee Time for Weekend Draw";

                     } else {

                        enew1 = "The following Lottery Tee Time has been ASSIGNED.\n\n";
                        enew2 = "The following Lottery Tee Times have been ASSIGNED.\n\n";
                        subject = "ForeTees Lottery Assignment Notification";
                     }
                  }

                  if (!clubName.equals( "" )) {

                     subject = subject + " - " + clubName;
                  }

                  Properties properties = new Properties();
                  properties.put("mail.smtp.host", SystemUtils.host);                      // set outbound host address
                  properties.put("mail.smtp.port", SystemUtils.port);                      // set outbound port
                  properties.put("mail.smtp.auth", "true");                    // set 'use authentication'
                  properties.put("mail.smtp.sendpartial", "true");       // a message has some valid and some invalid addresses, send the message anyway

                  Session mailSess = Session.getInstance(properties, SystemUtils.getAuthenticator());   // get session properties

                  MimeMessage message = new MimeMessage(mailSess);

                  try {

                     message.setFrom(new InternetAddress(SystemUtils.efrom));                               // set from addr

                     message.setSubject( subject );                                            // set subject line
                     message.setSentDate(new java.util.Date());                                // set date/time sent
                  }
                  catch (Exception ignore) {
                  }

                  //
                  //  Set the recipient addresses
                  //
                  if (!g1user1.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g1user1);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  if (!g1user2.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g1user2);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  if (!g1user3.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g1user3);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  if (!g1user4.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g1user4);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  if (!g1user5.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g1user5);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  if (!g2user1.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g2user1);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  if (!g2user2.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g2user2);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  if (!g2user3.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g2user3);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  if (!g2user4.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g2user4);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  if (!g2user5.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g2user5);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  if (!g3user1.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g3user1);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  if (!g3user2.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g3user2);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  if (!g3user3.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g3user3);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  if (!g3user4.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g3user4);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  if (!g3user5.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g3user5);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  if (!g4user1.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g4user1);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  if (!g4user2.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g4user2);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }
                  if (!g4user3.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g4user3);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }
                  if (!g4user4.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g4user4);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }
                  if (!g4user5.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g4user5);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }
                  if (!g5user1.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g5user1);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }
                  if (!g5user2.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g5user2);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }
                  if (!g5user3.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g5user3);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }
                  if (!g5user4.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g5user4);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }
                  if (!g5user5.equals( "" )) {       // if new user exist and not same as old usernames

                     try {
                        pstmte1 = con.prepareStatement (
                                 "SELECT email, emailOpt FROM member2b WHERE username = ?");

                        pstmte1.clearParameters();        // clear the parms
                        pstmte1.setString(1, g5user5);
                        rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           to = rs2.getString(1);        // user's email address
                           emailOpt = rs2.getInt(2);        // email option

                           if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                              message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                              send = 1;
                           }
                        }
                        pstmte1.close();              // close the stmt
                     }
                     catch (Exception ignore) {
                     }
                  }

                  //
                  //  send email if anyone to send it to
                  //
                  if (send != 0) {        // if any email addresses specified for members
                     //
                     //  Create the message content
                     //
                     if (groups > 1) {
                        if (afb == afb2 && afb == afb3 && afb == afb4 && afb == afb5) {    // if all on the same tee
                           enewMsg = SystemUtils.header + enew2 + day + " " + mm + "/" + dd + "/" + yy + " " +
                                         "on the " + f_b + " tee ";
                        } else {
                           enewMsg = SystemUtils.header + enew2 + day + " " + mm + "/" + dd + "/" + yy + " " +
                                         "on both tees ";
                        }
                     } else {
                        if (afb == afb2 && afb == afb3 && afb == afb4 && afb == afb5) {    // if all on the same tee
                           enewMsg = SystemUtils.header + enew1 + day + " " + mm + "/" + dd + "/" + yy + " " +
                                         "on the " + f_b + " tee ";
                        } else {
                           enewMsg = SystemUtils.header + enew1 + day + " " + mm + "/" + dd + "/" + yy + " " +
                                         "on both tees ";
                        }
                     }
                     if (!course.equals( "" )) {

                        enewMsg = enewMsg + "of Course: " + course;
                     }

                     //
                     //  convert time to hour and minutes for email msg
                     //
                     time = atime1;              // time for this tee time
                     ehr = time / 100;
                     emin = time - (ehr * 100);
                     eampm = " AM";
                     if (ehr > 12) {

                        eampm = " PM";
                        ehr = ehr - 12;       // convert from military time
                     }
                     if (ehr == 12) {

                        eampm = " PM";
                     }
                     if (ehr == 0) {

                        ehr = 12;
                        eampm = " AM";
                     }

                     if (emin < 10) {

                        etime = ehr + ":0" + emin + eampm;

                     } else {

                        etime = ehr + ":" + emin + eampm;
                     }

                     enewMsg = enewMsg + "\n at " + etime + "\n";

                     if (!g1player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + g1player1 + "  " + g1p1cw;
                     }
                     if (!g1player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + g1player2 + "  " + g1p2cw;
                     }
                     if (!g1player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + g1player3 + "  " + g1p3cw;
                     }
                     if (!g1player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + g1player4 + "  " + g1p4cw;
                     }
                     if (!g1player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + g1player5 + "  " + g1p5cw;
                     }

                     if (groups > 1) {

                        time = atime2;              // time for this tee time
                        ehr = time / 100;
                        emin = time - (ehr * 100);
                        eampm = " AM";
                        if (ehr > 12) {

                           eampm = " PM";
                           ehr = ehr - 12;       // convert from military time
                        }
                        if (ehr == 12) {

                           eampm = " PM";
                        }
                        if (ehr == 0) {

                           ehr = 12;
                           eampm = " AM";
                        }

                        if (emin < 10) {

                           etime = ehr + ":0" + emin + eampm;

                        } else {

                           etime = ehr + ":" + emin + eampm;
                        }

                        enewMsg = enewMsg + "\n\n at " + etime + "\n";

                        if (!g2player1.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 1: " + g2player1 + "  " + g2p1cw;
                        }
                        if (!g2player2.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 2: " + g2player2 + "  " + g2p2cw;
                        }
                        if (!g2player3.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 3: " + g2player3 + "  " + g2p3cw;
                        }
                        if (!g2player4.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 4: " + g2player4 + "  " + g2p4cw;
                        }
                        if (!g2player5.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 5: " + g2player5 + "  " + g2p5cw;
                        }
                     }

                     if (groups > 2) {

                        time = atime3;              // time for this tee time
                        ehr = time / 100;
                        emin = time - (ehr * 100);
                        eampm = " AM";
                        if (ehr > 12) {

                           eampm = " PM";
                           ehr = ehr - 12;       // convert from military time
                        }
                        if (ehr == 12) {

                           eampm = " PM";
                        }
                        if (ehr == 0) {

                           ehr = 12;
                           eampm = " AM";
                        }

                        if (emin < 10) {

                           etime = ehr + ":0" + emin + eampm;

                        } else {

                           etime = ehr + ":" + emin + eampm;
                        }

                        enewMsg = enewMsg + "\n\n at " + etime + "\n";

                        if (!g3player1.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 1: " + g3player1 + "  " + g3p1cw;
                        }
                        if (!g3player2.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 2: " + g3player2 + "  " + g3p2cw;
                        }
                        if (!g3player3.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 3: " + g3player3 + "  " + g3p3cw;
                        }
                        if (!g3player4.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 4: " + g3player4 + "  " + g3p4cw;
                        }
                        if (!g3player5.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 5: " + g3player5 + "  " + g3p5cw;
                        }
                     }

                     if (groups > 3) {

                        time = atime4;              // time for this tee time
                        ehr = time / 100;
                        emin = time - (ehr * 100);
                        eampm = " AM";
                        if (ehr > 12) {

                           eampm = " PM";
                           ehr = ehr - 12;       // convert from military time
                        }
                        if (ehr == 12) {

                           eampm = " PM";
                        }
                        if (ehr == 0) {

                           ehr = 12;
                           eampm = " AM";
                        }

                        if (emin < 10) {

                           etime = ehr + ":0" + emin + eampm;

                        } else {

                           etime = ehr + ":" + emin + eampm;
                        }

                        enewMsg = enewMsg + "\n\n at " + etime + "\n";

                        if (!g4player1.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 1: " + g4player1 + "  " + g4p1cw;
                        }
                        if (!g4player2.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 2: " + g4player2 + "  " + g4p2cw;
                        }
                        if (!g4player3.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 3: " + g4player3 + "  " + g4p3cw;
                        }
                        if (!g4player4.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 4: " + g4player4 + "  " + g4p4cw;
                        }
                        if (!g4player5.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 5: " + g4player5 + "  " + g4p5cw;
                        }
                     }

                     if (groups > 4) {

                        time = atime5;              // time for this tee time
                        ehr = time / 100;
                        emin = time - (ehr * 100);
                        eampm = " AM";
                        if (ehr > 12) {

                           eampm = " PM";
                           ehr = ehr - 12;       // convert from military time
                        }
                        if (ehr == 12) {

                           eampm = " PM";
                        }
                        if (ehr == 0) {

                           ehr = 12;
                           eampm = " AM";
                        }

                        if (emin < 10) {

                           etime = ehr + ":0" + emin + eampm;

                        } else {

                           etime = ehr + ":" + emin + eampm;
                        }

                        enewMsg = enewMsg + "\n\n at " + etime + "\n";

                        if (!g5player1.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 1: " + g5player1 + "  " + g5p1cw;
                        }
                        if (!g5player2.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 2: " + g5player2 + "  " + g5p2cw;
                        }
                        if (!g5player3.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 3: " + g5player3 + "  " + g5p3cw;
                        }
                        if (!g5player4.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 4: " + g5player4 + "  " + g5p4cw;
                        }
                        if (!g5player5.equals( "" )) {

                           enewMsg = enewMsg + "\nPlayer 5: " + g5player5 + "  " + g5p5cw;
                        }
                     }

                     enewMsg = enewMsg + SystemUtils.trailer;

                     try {
                        message.setText( enewMsg );  // put msg in email text area

                        Transport.send(message);     // send it!!
                     }
                     catch (Exception ignore) {
                     }
                  }     // end of IF send

                  //
                  // delete the request after players have been moved
                  //
                  pstmtd = con.prepareStatement (
                           "DELETE FROM lreqs3 WHERE id = ?");

                  pstmtd.clearParameters();               // clear the parms
                  pstmtd.setLong(1, id);
                  pstmtd.executeUpdate();

                  pstmtd.close();


               } else {

                  //
                  //  We were called from Proshop_dsheet when a user selected the "Convert All" Control Panel option.
                  //  We must set the state to 4 (converted) so that emails can be sent and the reqs deleted later.
                  //
                  PreparedStatement pstmt7s = con.prepareStatement (
                      "UPDATE lreqs3 SET state = 4 " +
                      "WHERE id = ?");

                  pstmt7s.clearParameters();        // clear the parms
                  pstmt7s.setLong(1, id);

                  pstmt7s.executeUpdate();

                  pstmt7s.close();


               } // end if sendEmailsNow

            }  // end of IF ok (tee times in use?)


         } else {     // req is NOT assigned

            //
            //  Change the state to 5 (processed & approved) so _sheet will show the others
            //
            PreparedStatement pstmt7s = con.prepareStatement (
                "UPDATE lreqs3 SET state = 5 " +
                "WHERE id = ?");

            pstmt7s.clearParameters();        // clear the parms
            pstmt7s.setLong(1, id);

            pstmt7s.executeUpdate();

            pstmt7s.close();

         }     // end of IF req is assigned

      }    // end of WHILE lreqs - process next request

      pstmt.close();

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
      return;
   }

 }  // end of moveReqs 
 
 
 
 //************************************************************************
 //  checkInUse - check tee times to see if they are in use
 //
 //   called by:  moveRegs above
 //
 //       parms:  con = db connection
 //               id  = id of the lottery request
 //
 //************************************************************************

 public static boolean checkInUse(Connection con, long id) {


   PreparedStatement pstmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String errorMsg = "";
   String name = "";
   String course = "";
   String p5 = "";

   int groups = 0;
   int fb1 = 0;
   int fb2 = 0;
   int fb3 = 0;
   int fb4 = 0;
   int fb5 = 0;
   int atime1 = 0;
   int atime2 = 0;
   int atime3 = 0;
   int atime4 = 0;
   int atime5 = 0;
   int in_use = 0;

   long date = 0;

   boolean ok = true;


   try {

      errorMsg = "Error in SystemUtils checkInUse: ";

      //
      //  Get the Lottery Request for the lottery id passed
      //
      PreparedStatement pstmt = con.prepareStatement (
         "SELECT name, date, courseName, groups, atime1, atime2, atime3, " +
         "atime4, atime5, afb, p5, afb2, afb3, afb4, afb5 " +
         "FROM lreqs3 " +
         "WHERE id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, id);

      rs = pstmt.executeQuery();      // execute the prepared stmt again to start with first

      if (rs.next()) {

         name = rs.getString(1);
         date = rs.getLong(2);
         course = rs.getString(3);
         groups = rs.getInt(4);
         atime1 = rs.getInt(5);
         atime2 = rs.getInt(6);
         atime3 = rs.getInt(7);
         atime4 = rs.getInt(8);
         atime5 = rs.getInt(9);
         fb1 = rs.getInt(10);
         p5 = rs.getString(11);
         fb2 = rs.getInt(12);
         fb3 = rs.getInt(13);
         fb4 = rs.getInt(14);
         fb5 = rs.getInt(15);

         if (atime1 != 0) {          // only process if its assigned

            //
            //  Check the in-use flag for this tee time
            //
            pstmt2 = con.prepareStatement (
               "SELECT in_use " +
               "FROM teecurr2 " +
               "WHERE date = ? AND time = ? AND fb = ? AND courseName = ? AND player1 = ''");

            pstmt2.clearParameters();        // clear the parms
            pstmt2.setLong(1, date);
            pstmt2.setInt(2, atime1);
            pstmt2.setInt(3, fb1);
            pstmt2.setString(4, course);

            rs2 = pstmt2.executeQuery();

            if (rs2.next()) {

               in_use = rs2.getInt(1);
            } else { in_use = 1; }
            pstmt2.close();

            if (in_use == 0) {

               //
               //  Do next group, if there is one
               //
               if (groups > 1) {

                  //
                  //  Check the in-use flag for this tee time
                  //
                  pstmt2 = con.prepareStatement (
                     "SELECT in_use " +
                     "FROM teecurr2 " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ? AND player1 = ''");

                  pstmt2.clearParameters();        // clear the parms
                  pstmt2.setLong(1, date);
                  pstmt2.setInt(2, atime2);
                  pstmt2.setInt(3, fb2);
                  pstmt2.setString(4, course);

                  rs2 = pstmt2.executeQuery();

                  if (rs2.next()) {

                     in_use = rs2.getInt(1);
                  } else { in_use = 1; }
                  pstmt2.close();

                  if (in_use == 0) {

                     //
                     //  Do next group, if there is one
                     //
                     if (groups > 2) {

                        //
                        //  Check the in-use flag for this tee time
                        //
                        pstmt2 = con.prepareStatement (
                           "SELECT in_use " +
                           "FROM teecurr2 " +
                           "WHERE date = ? AND time = ? AND fb = ? AND courseName = ? AND player1 = ''");

                        pstmt2.clearParameters();        // clear the parms
                        pstmt2.setLong(1, date);
                        pstmt2.setInt(2, atime3);
                        pstmt2.setInt(3, fb3);
                        pstmt2.setString(4, course);

                        rs2 = pstmt2.executeQuery();

                        if (rs2.next()) {

                           in_use = rs2.getInt(1);
                        } else { in_use = 1; }
                        pstmt2.close();

                        if (in_use == 0) {

                           //
                           //  Do next group, if there is one
                           //
                           if (groups > 3) {

                              //
                              //  Check the in-use flag for this tee time
                              //
                              pstmt2 = con.prepareStatement (
                                 "SELECT in_use " +
                                 "FROM teecurr2 " +
                                 "WHERE date = ? AND time = ? AND fb = ? AND courseName = ? AND player1 = ''");

                              pstmt2.clearParameters();        // clear the parms
                              pstmt2.setLong(1, date);
                              pstmt2.setInt(2, atime4);
                              pstmt2.setInt(3, fb4);
                              pstmt2.setString(4, course);

                              rs2 = pstmt2.executeQuery();

                              if (rs2.next()) {

                                 in_use = rs2.getInt(1);
                              } else { in_use = 1; }
                              pstmt2.close();

                              if (in_use == 0) {

                                 //
                                 //  Do next group, if there is one
                                 //
                                 if (groups > 4) {

                                    //
                                    //  Check the in-use flag for this tee time
                                    //
                                    pstmt2 = con.prepareStatement (
                                       "SELECT in_use " +
                                       "FROM teecurr2 " +
                                       "WHERE date = ? AND time = ? AND fb = ? AND courseName = ? AND player1 = ''");

                                    pstmt2.clearParameters();        // clear the parms
                                    pstmt2.setLong(1, date);
                                    pstmt2.setInt(2, atime5);
                                    pstmt2.setInt(3, fb5);
                                    pstmt2.setString(4, course);

                                    rs2 = pstmt2.executeQuery();

                                    if (rs2.next()) {

                                       in_use = rs2.getInt(1);
                                    } else { in_use = 1; }
                                    pstmt2.close();

                                    if (in_use > 0) {

                                       ok = false;           // do not change these tee times
                                    }
                                 }                      // end of IF groups > 4

                              } else {

                                 ok = false;           // do not change these tee times
                              }
                           }                      // end of IF groups > 3

                        } else {

                           ok = false;           // do not change these tee times
                        }
                     }                      // end of IF groups > 2

                  } else {

                     ok = false;           // do not change these tee times
                  }
               }                           // end of IF groups > 1

            } else {

               ok = false;           // do not change these tee times
            }
         }                           // end of IF atime1 > 0
      }            // end of IF lottery req

      pstmt.close();
   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }
   
   return(ok);
   
 }  // end of checkInUse

 
 //************************************************************************
 //  checkBlockers - check tee times to see if any of them are blocked
 //
 //   called by:  moveRegs above
 //
 //       parms:  con = db connection
 //               id  = id of the lottery request
 //
 //************************************************************************

 public static boolean checkBlockers(Connection con, long id) {


   PreparedStatement pstmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String errorMsg = "";
   String name = "";
   String course = "";
   String p5 = "";

   int groups = 0;
   int fb1 = 0;
   int fb2 = 0;
   int fb3 = 0;
   int fb4 = 0;
   int fb5 = 0;
   int atime1 = 0;
   int atime2 = 0;
   int atime3 = 0;
   int atime4 = 0;
   int atime5 = 0;
   int in_use = 0;

   long date = 0;

   boolean ok = true;

   try {

      errorMsg = "Error in SystemUtils checkBlockers: ";

      //
      //  Get the Lottery Request for the lottery id passed
      //
      PreparedStatement pstmt = con.prepareStatement (
         "SELECT name, date, courseName, groups, atime1, atime2, atime3, " +
         "atime4, atime5, afb, p5, afb2, afb3, afb4, afb5 " +
         "FROM lreqs3 " +
         "WHERE id = ?");

      pstmt.clearParameters();
      pstmt.setLong(1, id);

      rs = pstmt.executeQuery();

      if (rs.next()) {

         name = rs.getString(1);
         date = rs.getLong(2);
         course = rs.getString(3);
         groups = rs.getInt(4);
         atime1 = rs.getInt(5);
         atime2 = rs.getInt(6);
         atime3 = rs.getInt(7);
         atime4 = rs.getInt(8);
         atime5 = rs.getInt(9);
         fb1 = rs.getInt(10);
         p5 = rs.getString(11);
         fb2 = rs.getInt(12);
         fb3 = rs.getInt(13);
         fb4 = rs.getInt(14);
         fb5 = rs.getInt(15);

         if (atime1 != 0) {

            //
            //  Check the blocker indicator for this tee time
            //
            pstmt2 = con.prepareStatement (
               "SELECT teecurr_id " +
               "FROM teecurr2 " +
               "WHERE date = ? AND time = ? AND fb = ? AND courseName = ? AND blocker <> ''");

            pstmt2.clearParameters();
            pstmt2.setLong(1, date);
            pstmt2.setInt(2, atime1);
            pstmt2.setInt(3, fb1);
            pstmt2.setString(4, course);

            rs2 = pstmt2.executeQuery();

            if (rs2.next()) ok = false;

            pstmt2.close();

            if (ok && groups > 1) {

                  pstmt2 = con.prepareStatement (
                     "SELECT teecurr_id " +
                     "FROM teecurr2 " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ? AND blocker <> ''");

                  pstmt2.clearParameters();
                  pstmt2.setLong(1, date);
                  pstmt2.setInt(2, atime2);
                  pstmt2.setInt(3, fb2);
                  pstmt2.setString(4, course);

                  rs2 = pstmt2.executeQuery();

                  if (rs2.next()) ok = false;

                  pstmt2.close();
            }

            if (ok && groups > 2) {

                pstmt2 = con.prepareStatement (
                   "SELECT teecurr_id " +
                   "FROM teecurr2 " +
                   "WHERE date = ? AND time = ? AND fb = ? AND courseName = ? AND blocker <> ''");

                pstmt2.clearParameters();
                pstmt2.setLong(1, date);
                pstmt2.setInt(2, atime3);
                pstmt2.setInt(3, fb3);
                pstmt2.setString(4, course);

                rs2 = pstmt2.executeQuery();

                if (rs2.next()) ok = false;

                pstmt2.close();
            }

            if (ok && groups > 3) {

                pstmt2 = con.prepareStatement (
                    "SELECT teecurr_id " +
                    "FROM teecurr2 " +
                    "WHERE date = ? AND time = ? AND fb = ? AND courseName = ? AND blocker <> ''");

                pstmt2.clearParameters();
                pstmt2.setLong(1, date);
                pstmt2.setInt(2, atime4);
                pstmt2.setInt(3, fb4);
                pstmt2.setString(4, course);

                rs2 = pstmt2.executeQuery();

                if (rs2.next()) ok = false;

                pstmt2.close();
            }

            if (ok && groups > 4) {

                pstmt2 = con.prepareStatement (
                   "SELECT teecurr_id " +
                   "FROM teecurr2 " +
                   "WHERE date = ? AND time = ? AND fb = ? AND courseName = ? AND blocker <> ''");

                pstmt2.clearParameters();
                pstmt2.setLong(1, date);
                pstmt2.setInt(2, atime5);
                pstmt2.setInt(3, fb5);
                pstmt2.setString(4, course);

                rs2 = pstmt2.executeQuery();

                if (rs2.next()) ok = false;

                pstmt2.close();

            }

         } // end of IF atime1 != 0

      } // end of IF lottery req

      pstmt.close();
   }
   catch (Exception e1) {

      errorMsg = errorMsg + e1.getMessage();
      logError(errorMsg);
   }

   return(ok);

 }  // end of checkBlockers

 
 
 
 //************************************************************************
 //  updateRecurReqs - update recurring lottery requests after one was updated
 //
 //   called by:  Proshop_lott
 //               Member_lott
 //
 //************************************************************************

 public static ArrayList updateRecurReqs(long lottid, Connection con) {

  
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   String dates = "";

   long recur_id = 0;
   long next_lottid = 0;
   long date = 0;
 
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int temp = 0;
   
   boolean error = false;
   
   parmLott parm = new parmLott();          // allocate a parm block

   ArrayList<String> recurr_dates = new ArrayList<String>();
   
    
   try {    
    
      pstmt = con.prepareStatement (
         "SELECT date, recur_id " +
         "FROM lreqs3 WHERE id = ?");    // get the date and recur_id for the request that was just updated

      pstmt.clearParameters();      
      pstmt.setLong(1, lottid);      
      rs = pstmt.executeQuery();    

      if (rs.next()) {

         date = rs.getLong("date");  
         recur_id = rs.getLong("recur_id");   // get this request's recur_id
      }
    
      if (recur_id > 0) {       // if this request was part of a recurring request
         
         //
         //  Get all the parm values from the updated request
         //
         parm.lottid = lottid;       // set the id 
         
         setParmValues(parm, con);   // copy the request content to parm
   
         //  update all requests that are part of this chain and after this date
         
         pstmt = con.prepareStatement (
            "SELECT id, date " +
            "FROM lreqs3 WHERE date > ? AND recur_id = ? ORDER BY date");  

         pstmt.clearParameters();      
         pstmt.setLong(1, date);      
         pstmt.setLong(2, recur_id);      
         rs = pstmt.executeQuery();    

         while (rs.next()) {

            next_lottid = rs.getLong("id");    //  recurring request
            date = rs.getLong("date");  
            
            //  update this request
            
            error = update1RecurReq(next_lottid, parm, con);
            
            //
            //   Save this date in the list of updated requests
            //
            if (error == false) {
               
               yy = (int)date / 10000;                // break down the new date
               temp = yy * 10000;
               mm = (int)date - temp;
               temp = mm / 100;
               temp = temp * 100;
               dd = mm - temp;
               mm = mm / 100;

               dates = mm + "/" + dd + "/" + yy;            // save date of the updated request

               recurr_dates.add(dates);                                    
            }
         }
      }
      
      pstmt.close();
      
   }
   catch (Exception e1) {
      dbError("Error in Common_Lott.updateRecurReqs.  Error=", e1);
   }
     
   return(recurr_dates);

 }  // end of updateRecurReqs

 
 
 
 //************************************************************************
 //  update1RecurReq - update recurring lottery requests after one was updated
 //
 //   called by:  updateRecurReqs above
 //************************************************************************

 private static boolean update1RecurReq(long lottid, parmLott parm, Connection con) {

  
   PreparedStatement pstmt6 = null;
   
   boolean error = false;
   
   int count = 0;
   int hr = parm.time/100;
   int min = parm.time - (hr * 100); 

   try {   
   
      pstmt6 = con.prepareStatement (
            "UPDATE lreqs3 SET hr = ?, min = ?, time = ?, minsbefore = ?, minsafter = ?, " +
            "player1 = ?, player2 = ?, player3 = ?, player4 = ?, player5 = ?, " +
            "player6 = ?, player7 = ?, player8 = ?, player9 = ?, player10 = ?, " +
            "player11 = ?, player12 = ?, player13 = ?, player14 = ?, player15 = ?, " +
            "player16 = ?, player17 = ?, player18 = ?, player19 = ?, player20 = ?, " +
            "player21 = ?, player22 = ?, player23 = ?, player24 = ?, player25 = ?, " +
            "user1 = ?, user2 = ?, user3 = ?, user4 = ?, user5 = ?, " +
            "user6 = ?, user7 = ?, user8 = ?, user9 = ?, user10 = ?, " +
            "user11 = ?, user12 = ?, user13 = ?, user14 = ?, user15 = ?, " +
            "user16 = ?, user17 = ?, user18 = ?, user19 = ?, user20 = ?, " +
            "user21 = ?, user22 = ?, user23 = ?, user24 = ?, user25 = ?, " +
            "p1cw = ?, p2cw = ?, p3cw = ?, p4cw = ?, p5cw = ?, " +
            "p6cw = ?, p7cw = ?, p8cw = ?, p9cw = ?, p10cw = ?, " +
            "p11cw = ?, p12cw = ?, p13cw = ?, p14cw = ?, p15cw = ?, " +
            "p16cw = ?, p17cw = ?, p18cw = ?, p19cw = ?, p20cw = ?, " +
            "p21cw = ?, p22cw = ?, p23cw = ?, p24cw = ?, p25cw = ?, " +
            "notes = ?, hideNotes = ?, fb = ?, courseName = ?, in_use = 0, groups = ?, p5 = ?, " +
            "players = ?, userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, userg6 = ?, " +
            "userg7 = ?, userg8 = ?, userg9 = ?, userg10 = ?, userg11 = ?, userg12 = ?, userg13 = ?, " +
            "userg14 = ?, userg15 = ?, userg16 = ?, userg17 = ?, userg18 = ?, userg19 = ?, userg20 = ?, " +
            "userg21 = ?, userg22 = ?, userg23 = ?, userg24 = ?, userg25 = ?, " +
            "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, p96 = ?, " +
            "p97 = ?, p98 = ?, p99 = ?, p910 = ?, p911 = ?, p912 = ?, p913 = ?, " +
            "p914 = ?, p915 = ?, p916 = ?, p917 = ?, p918 = ?, p919 = ?, p920 = ?, " +
            "p921 = ?, p922 = ?, p923 = ?, p924 = ?, p925 = ?, checkothers = ?, " +
            "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ?, " +
            "guest_id6 = ?, guest_id7 = ?, guest_id8 = ?, guest_id9 = ?, guest_id10 = ?, " +
            "guest_id11 = ?, guest_id12 = ?, guest_id13 = ?, guest_id14 = ?, guest_id15 = ?, " +
            "guest_id16 = ?, guest_id17 = ?, guest_id18 = ?, guest_id19 = ?, guest_id20 = ?, " +
            "guest_id21 = ?, guest_id22 = ?, guest_id23 = ?, guest_id24 = ?, guest_id25 = ? " +
            "WHERE id = ? AND in_use = 0");                                                         // make sure its not in use

      pstmt6.clearParameters();     
      pstmt6.setInt(1, hr);
      pstmt6.setInt(2, min);
      pstmt6.setInt(3, parm.time);
      pstmt6.setInt(4, parm.mins_before);
      pstmt6.setInt(5, parm.mins_after);
      pstmt6.setString(6, parm.player1);
      pstmt6.setString(7, parm.player2);
      pstmt6.setString(8, parm.player3);
      pstmt6.setString(9, parm.player4);
      pstmt6.setString(10, parm.player5);
      pstmt6.setString(11, parm.player6);
      pstmt6.setString(12, parm.player7);
      pstmt6.setString(13, parm.player8);
      pstmt6.setString(14, parm.player9);
      pstmt6.setString(15, parm.player10);
      pstmt6.setString(16, parm.player11);
      pstmt6.setString(17, parm.player12);
      pstmt6.setString(18, parm.player13);
      pstmt6.setString(19, parm.player14);
      pstmt6.setString(20, parm.player15);
      pstmt6.setString(21, parm.player16);
      pstmt6.setString(22, parm.player17);
      pstmt6.setString(23, parm.player18);
      pstmt6.setString(24, parm.player19);
      pstmt6.setString(25, parm.player20);
      pstmt6.setString(26, parm.player21);
      pstmt6.setString(27, parm.player22);
      pstmt6.setString(28, parm.player23);
      pstmt6.setString(29, parm.player24);
      pstmt6.setString(30, parm.player25);
      pstmt6.setString(31, parm.user1);
      pstmt6.setString(32, parm.user2);
      pstmt6.setString(33, parm.user3);
      pstmt6.setString(34, parm.user4);
      pstmt6.setString(35, parm.user5);
      pstmt6.setString(36, parm.user6);
      pstmt6.setString(37, parm.user7);
      pstmt6.setString(38, parm.user8);
      pstmt6.setString(39, parm.user9);
      pstmt6.setString(40, parm.user10);
      pstmt6.setString(41, parm.user11);
      pstmt6.setString(42, parm.user12);
      pstmt6.setString(43, parm.user13);
      pstmt6.setString(44, parm.user14);
      pstmt6.setString(45, parm.user15);
      pstmt6.setString(46, parm.user16);
      pstmt6.setString(47, parm.user17);
      pstmt6.setString(48, parm.user18);
      pstmt6.setString(49, parm.user19);
      pstmt6.setString(50, parm.user20);
      pstmt6.setString(51, parm.user21);
      pstmt6.setString(52, parm.user22);
      pstmt6.setString(53, parm.user23);
      pstmt6.setString(54, parm.user24);
      pstmt6.setString(55, parm.user25);
      pstmt6.setString(56, parm.pcw1);
      pstmt6.setString(57, parm.pcw2);
      pstmt6.setString(58, parm.pcw3);
      pstmt6.setString(59, parm.pcw4);
      pstmt6.setString(60, parm.pcw5);
      pstmt6.setString(61, parm.pcw6);
      pstmt6.setString(62, parm.pcw7);
      pstmt6.setString(63, parm.pcw8);
      pstmt6.setString(64, parm.pcw9);
      pstmt6.setString(65, parm.pcw10);
      pstmt6.setString(66, parm.pcw11);
      pstmt6.setString(67, parm.pcw12);
      pstmt6.setString(68, parm.pcw13);
      pstmt6.setString(69, parm.pcw14);
      pstmt6.setString(70, parm.pcw15);
      pstmt6.setString(71, parm.pcw16);
      pstmt6.setString(72, parm.pcw17);
      pstmt6.setString(73, parm.pcw18);
      pstmt6.setString(74, parm.pcw19);
      pstmt6.setString(75, parm.pcw20);
      pstmt6.setString(76, parm.pcw21);
      pstmt6.setString(77, parm.pcw22);
      pstmt6.setString(78, parm.pcw23);
      pstmt6.setString(79, parm.pcw24);
      pstmt6.setString(80, parm.pcw25);
      pstmt6.setString(81, parm.notes);
      pstmt6.setInt(82, parm.hide);
      pstmt6.setInt(83, parm.fb);
      pstmt6.setString(84, parm.course);
      pstmt6.setInt(85, parm.slots);
      pstmt6.setString(86, parm.p5);
      pstmt6.setInt(87, parm.players);
      pstmt6.setString(88, parm.userg[0]);
      pstmt6.setString(89, parm.userg[1]);
      pstmt6.setString(90, parm.userg[2]);
      pstmt6.setString(91, parm.userg[3]);
      pstmt6.setString(92, parm.userg[4]);
      pstmt6.setString(93, parm.userg[5]);
      pstmt6.setString(94, parm.userg[6]);
      pstmt6.setString(95, parm.userg[7]);
      pstmt6.setString(96, parm.userg[8]);
      pstmt6.setString(97, parm.userg[9]);
      pstmt6.setString(98, parm.userg[10]);
      pstmt6.setString(99, parm.userg[11]);
      pstmt6.setString(100, parm.userg[12]);
      pstmt6.setString(101, parm.userg[13]);
      pstmt6.setString(102, parm.userg[14]);
      pstmt6.setString(103, parm.userg[15]);
      pstmt6.setString(104, parm.userg[16]);
      pstmt6.setString(105, parm.userg[17]);
      pstmt6.setString(106, parm.userg[18]);
      pstmt6.setString(107, parm.userg[19]);
      pstmt6.setString(108, parm.userg[20]);
      pstmt6.setString(109, parm.userg[21]);
      pstmt6.setString(110, parm.userg[22]);
      pstmt6.setString(111, parm.userg[23]);
      pstmt6.setString(112, parm.userg[24]);
      pstmt6.setInt(113, parm.p91);   
      pstmt6.setInt(114, parm.p92);
      pstmt6.setInt(115, parm.p93);
      pstmt6.setInt(116, parm.p94);   
      pstmt6.setInt(117, parm.p95);
      pstmt6.setInt(118, parm.p96);
      pstmt6.setInt(119, parm.p97);
      pstmt6.setInt(120, parm.p98);
      pstmt6.setInt(121, parm.p99);
      pstmt6.setInt(122, parm.p910);
      pstmt6.setInt(123, parm.p911);
      pstmt6.setInt(124, parm.p912);
      pstmt6.setInt(125, parm.p913);
      pstmt6.setInt(126, parm.p914);
      pstmt6.setInt(127, parm.p915);
      pstmt6.setInt(128, parm.p916);
      pstmt6.setInt(129, parm.p917);
      pstmt6.setInt(130, parm.p918);
      pstmt6.setInt(131, parm.p919);
      pstmt6.setInt(132, parm.p920);
      pstmt6.setInt(133, parm.p921);
      pstmt6.setInt(134, parm.p922);
      pstmt6.setInt(135, parm.p923);
      pstmt6.setInt(136, parm.p924);
      pstmt6.setInt(137, parm.p925);
      pstmt6.setInt(138, parm.checkothers);
      pstmt6.setInt(139, parm.guest_id1);
      pstmt6.setInt(140, parm.guest_id2);
      pstmt6.setInt(141, parm.guest_id3);
      pstmt6.setInt(142, parm.guest_id4);
      pstmt6.setInt(143, parm.guest_id5);
      pstmt6.setInt(144, parm.guest_id6);
      pstmt6.setInt(145, parm.guest_id7);
      pstmt6.setInt(146, parm.guest_id8);
      pstmt6.setInt(147, parm.guest_id9);
      pstmt6.setInt(148, parm.guest_id10);
      pstmt6.setInt(149, parm.guest_id11);
      pstmt6.setInt(150, parm.guest_id12);
      pstmt6.setInt(151, parm.guest_id13);
      pstmt6.setInt(152, parm.guest_id14);
      pstmt6.setInt(153, parm.guest_id15);
      pstmt6.setInt(154, parm.guest_id16);
      pstmt6.setInt(155, parm.guest_id17);
      pstmt6.setInt(156, parm.guest_id18);
      pstmt6.setInt(157, parm.guest_id19);
      pstmt6.setInt(158, parm.guest_id20);
      pstmt6.setInt(159, parm.guest_id21);
      pstmt6.setInt(160, parm.guest_id22);
      pstmt6.setInt(161, parm.guest_id23);
      pstmt6.setInt(162, parm.guest_id24);
      pstmt6.setInt(163, parm.guest_id25);

      pstmt6.setLong(164, lottid);
      
      count = pstmt6.executeUpdate();   

      pstmt6.close();
            
   }
   catch (Exception e1) {
      dbError("Error in Common_Lott.update1RecurReq.  Error=", e1);
      error = true;
   }
   
   if (count == 0) error = true;     // check if update failed (in use or other reason)
   
   return(error);
     
 }  // end of update1RecurReq

 
 
 
 //************************************************************************
 //  addRecurrRequests - add recurring lottery requests
 //
 //
 //   called by:  Proshop_lott
 //               Member_lott
 //
 //************************************************************************

 public static ArrayList addRecurrRequests(parmLott parm, Connection con) {

   ArrayList<String> recurr_dates = new ArrayList<String>();
   
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int days = 7;
   int temp = 0;
   int proNew = 0;
   int memNew = 0;
   
   long lottid = 0;
   long recur_id = 0;      // use id of first request to link all together
   long newdate = 0;
   long edate = (parm.eyy * 10000) + (parm.emm * 100) + parm.edd;   // set end date as indicated by user

   String dates = "";
   
   //
   //  Get all the parm values from the original request (request to be recurred)
   //
   setParmValues(parm, con);     //  use parm.lottid to get the values
   
   mm = parm.mm;                 // get date values of original request to form a base date
   dd = parm.dd;
   yy = parm.yy;
   
   hr = parm.time / 100;               // get hr and min for new request
   min = parm.time - (hr * 100);
   
   if (parm.orig_by.startsWith("proshop")) {        // if proshop user made the request
      
      proNew = 1;
      
   } else {
      
      memNew = 1;
   }
   
   //
   //  Use the date of the first request and roll ahead 1 or 2 weeks at a time until we exceed the end date
   //
   if (parm.eoweek > 0) days = 14;              // use 2 weeks if 'every other' was selected
   
   newdate = getNextDate(days, mm, dd, yy);     // jump to the next date

   while (newdate <= edate) {
      
      yy = (int)newdate / 10000;                // break down the new date
      temp = yy * 10000;
      mm = (int)newdate - temp;
      temp = mm / 100;
      temp = temp * 100;
      dd = mm - temp;
      mm = mm / 100;
      
      if (recur_id == 0) {              // if this is the first recur
         
         setRecurId(parm.lottid, con);  // set the id in the original req now that we know we have others
         
         recur_id = parm.lottid;        // use the id of the original request to tie the others together
      }
      
      lottid = SystemUtils.getLottId(con);      // allocate a new entry
      
      //
      //  Copy the lottery request to this date
      //
      try {
         PreparedStatement pstmt3 = con.prepareStatement (
            "INSERT INTO lreqs3 (name, date, mm, dd, yy, day, hr, min, time, minsbefore, minsafter, " +
            "player1, player2, player3, player4, player5, player6, player7, player8, player9, player10, " +
            "player11, player12, player13, player14, player15, player16, player17, player18, player19, player20, " +
            "player21, player22, player23, player24, player25, " +
            "user1, user2, user3, user4, user5, user6, user7, user8, user9, user10, " +
            "user11, user12, user13, user14, user15, user16, user17, user18, user19, user20, " +
            "user21, user22, user23, user24, user25, " +
            "p1cw, p2cw, p3cw, p4cw, p5cw, p6cw, p7cw, p8cw, p9cw, p10cw, " +
            "p11cw, p12cw, p13cw, p14cw, p15cw, p16cw, p17cw, p18cw, p19cw, p20cw, " +
            "p21cw, p22cw, p23cw, p24cw, p25cw, notes, hideNotes, fb, courseName, proNew, " +
            "proMod, memNew, memMod, id, in_use, in_use_by, groups, type, state, atime1, " +
            "atime2, atime3, atime4, atime5, afb, p5, players, userg1, userg2, userg3, userg4, userg5, " +
            "userg6, userg7, userg8, userg9, userg10, userg11, userg12, userg13, userg14, userg15, " +
            "userg16, userg17, userg18, userg19, userg20, userg21, userg22, userg23, userg24, userg25, " +
            "weight, orig_by, p91, p92, p93, p94, p95, " +
            "p96, p97, p98, p99, p910, p911, p912, p913, p914, p915, " +
            "p916, p917, p918, p919, p920, p921, p922, p923, p924, p925, checkothers, courseReq, " +
            "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5, " +
            "guest_id6, guest_id7, guest_id8, guest_id9, guest_id10, " +
            "guest_id11, guest_id12, guest_id13, guest_id14, guest_id15, " +
            "guest_id16, guest_id17, guest_id18, guest_id19, guest_id20, " +
            "guest_id21, guest_id22, guest_id23, guest_id24, guest_id25, " +
            "recur_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, " +                        
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +         
            "0, ?, 0, ?, 0, '', ?, '', 0, 0, " +
            "0, 0, 0, 0, 0, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "0, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?" +
            ")");

         pstmt3.clearParameters();        // clear the parms
         pstmt3.setString(1, parm.lottName);
         pstmt3.setLong(2, newdate);       
         pstmt3.setInt(3, mm);
         pstmt3.setInt(4, dd);
         pstmt3.setInt(5, yy);
         pstmt3.setString(6, parm.day);
         pstmt3.setInt(7, hr);
         pstmt3.setInt(8, min);
         pstmt3.setInt(9, parm.time);
         pstmt3.setInt(10, parm.mins_before);
         pstmt3.setInt(11, parm.mins_after);
         pstmt3.setString(12, parm.player1);
         pstmt3.setString(13, parm.player2);
         pstmt3.setString(14, parm.player3);
         pstmt3.setString(15, parm.player4);
         pstmt3.setString(16, parm.player5);
         pstmt3.setString(17, parm.player6);
         pstmt3.setString(18, parm.player7);
         pstmt3.setString(19, parm.player8);
         pstmt3.setString(20, parm.player9);
         pstmt3.setString(21, parm.player10);
         pstmt3.setString(22, parm.player11);
         pstmt3.setString(23, parm.player12);
         pstmt3.setString(24, parm.player13);
         pstmt3.setString(25, parm.player14);
         pstmt3.setString(26, parm.player15);
         pstmt3.setString(27, parm.player16);
         pstmt3.setString(28, parm.player17);
         pstmt3.setString(29, parm.player18);
         pstmt3.setString(30, parm.player19);
         pstmt3.setString(31, parm.player20);
         pstmt3.setString(32, parm.player21);
         pstmt3.setString(33, parm.player22);
         pstmt3.setString(34, parm.player23);
         pstmt3.setString(35, parm.player24);
         pstmt3.setString(36, parm.player25);
         pstmt3.setString(37, parm.user1);
         pstmt3.setString(38, parm.user2);
         pstmt3.setString(39, parm.user3);
         pstmt3.setString(40, parm.user4);
         pstmt3.setString(41, parm.user5);
         pstmt3.setString(42, parm.user6);
         pstmt3.setString(43, parm.user7);
         pstmt3.setString(44, parm.user8);
         pstmt3.setString(45, parm.user9);
         pstmt3.setString(46, parm.user10);
         pstmt3.setString(47, parm.user11);
         pstmt3.setString(48, parm.user12);
         pstmt3.setString(49, parm.user13);
         pstmt3.setString(50, parm.user14);
         pstmt3.setString(51, parm.user15);
         pstmt3.setString(52, parm.user16);
         pstmt3.setString(53, parm.user17);
         pstmt3.setString(54, parm.user18);
         pstmt3.setString(55, parm.user19);
         pstmt3.setString(56, parm.user20);
         pstmt3.setString(57, parm.user21);
         pstmt3.setString(58, parm.user22);
         pstmt3.setString(59, parm.user23);
         pstmt3.setString(60, parm.user24);
         pstmt3.setString(61, parm.user25);
         pstmt3.setString(62, parm.pcw1);
         pstmt3.setString(63, parm.pcw2);
         pstmt3.setString(64, parm.pcw3);
         pstmt3.setString(65, parm.pcw4);
         pstmt3.setString(66, parm.pcw5);
         pstmt3.setString(67, parm.pcw6);
         pstmt3.setString(68, parm.pcw7);
         pstmt3.setString(69, parm.pcw8);
         pstmt3.setString(70, parm.pcw9);
         pstmt3.setString(71, parm.pcw10);
         pstmt3.setString(72, parm.pcw11);
         pstmt3.setString(73, parm.pcw12);
         pstmt3.setString(74, parm.pcw13);
         pstmt3.setString(75, parm.pcw14);
         pstmt3.setString(76, parm.pcw15);
         pstmt3.setString(77, parm.pcw16);
         pstmt3.setString(78, parm.pcw17);
         pstmt3.setString(79, parm.pcw18);
         pstmt3.setString(80, parm.pcw19);
         pstmt3.setString(81, parm.pcw20);
         pstmt3.setString(82, parm.pcw21);
         pstmt3.setString(83, parm.pcw22);
         pstmt3.setString(84, parm.pcw23);
         pstmt3.setString(85, parm.pcw24);
         pstmt3.setString(86, parm.pcw25);
         pstmt3.setString(87, parm.notes);
         pstmt3.setInt(88, parm.hide);
         pstmt3.setInt(89, parm.fb);
         pstmt3.setString(90, parm.course);
         pstmt3.setInt(91, proNew);
         pstmt3.setInt(92, memNew);
         pstmt3.setLong(93, lottid);
         pstmt3.setInt(94, parm.slots);
         pstmt3.setString(95, parm.p5);
         pstmt3.setInt(96, parm.players);
         pstmt3.setString(97, parm.userg[0]);
         pstmt3.setString(98, parm.userg[1]);
         pstmt3.setString(99, parm.userg[2]);
         pstmt3.setString(100, parm.userg[3]);
         pstmt3.setString(101, parm.userg[4]);
         pstmt3.setString(102, parm.userg[5]);
         pstmt3.setString(103, parm.userg[6]);
         pstmt3.setString(104, parm.userg[7]);
         pstmt3.setString(105, parm.userg[8]);
         pstmt3.setString(106, parm.userg[9]);
         pstmt3.setString(107, parm.userg[10]);
         pstmt3.setString(108, parm.userg[11]);
         pstmt3.setString(109, parm.userg[12]);
         pstmt3.setString(110, parm.userg[13]);
         pstmt3.setString(111, parm.userg[14]);
         pstmt3.setString(112, parm.userg[15]);
         pstmt3.setString(113, parm.userg[16]);
         pstmt3.setString(114, parm.userg[17]);
         pstmt3.setString(115, parm.userg[18]);
         pstmt3.setString(116, parm.userg[19]);
         pstmt3.setString(117, parm.userg[20]);
         pstmt3.setString(118, parm.userg[21]);
         pstmt3.setString(119, parm.userg[22]);
         pstmt3.setString(120, parm.userg[23]);
         pstmt3.setString(121, parm.userg[24]);
         pstmt3.setString(122, parm.orig_by);
         pstmt3.setInt(123, parm.p91);
         pstmt3.setInt(124, parm.p92);
         pstmt3.setInt(125, parm.p93);
         pstmt3.setInt(126, parm.p94);
         pstmt3.setInt(127, parm.p95);
         pstmt3.setInt(128, parm.p96);
         pstmt3.setInt(129, parm.p97);
         pstmt3.setInt(130, parm.p98);
         pstmt3.setInt(131, parm.p99);
         pstmt3.setInt(132, parm.p910);
         pstmt3.setInt(133, parm.p911);
         pstmt3.setInt(134, parm.p912);
         pstmt3.setInt(135, parm.p913);
         pstmt3.setInt(136, parm.p914);
         pstmt3.setInt(137, parm.p915);
         pstmt3.setInt(138, parm.p916);
         pstmt3.setInt(139, parm.p917);
         pstmt3.setInt(140, parm.p918);
         pstmt3.setInt(141, parm.p919);
         pstmt3.setInt(142, parm.p920);
         pstmt3.setInt(143, parm.p921);
         pstmt3.setInt(144, parm.p922);
         pstmt3.setInt(145, parm.p923);
         pstmt3.setInt(146, parm.p924);
         pstmt3.setInt(147, parm.p925);
         pstmt3.setInt(148, parm.checkothers);
         pstmt3.setString(149, parm.course);
         pstmt3.setInt(150, parm.guest_id1);
         pstmt3.setInt(151, parm.guest_id2);
         pstmt3.setInt(152, parm.guest_id3);
         pstmt3.setInt(153, parm.guest_id4);
         pstmt3.setInt(154, parm.guest_id5);
         pstmt3.setInt(155, parm.guest_id6);
         pstmt3.setInt(156, parm.guest_id7);
         pstmt3.setInt(157, parm.guest_id8);
         pstmt3.setInt(158, parm.guest_id9);
         pstmt3.setInt(159, parm.guest_id10);
         pstmt3.setInt(160, parm.guest_id11);
         pstmt3.setInt(161, parm.guest_id12);
         pstmt3.setInt(162, parm.guest_id13);
         pstmt3.setInt(163, parm.guest_id14);
         pstmt3.setInt(164, parm.guest_id15);
         pstmt3.setInt(165, parm.guest_id16);
         pstmt3.setInt(166, parm.guest_id17);
         pstmt3.setInt(167, parm.guest_id18);
         pstmt3.setInt(168, parm.guest_id19);
         pstmt3.setInt(169, parm.guest_id20);
         pstmt3.setInt(170, parm.guest_id21);
         pstmt3.setInt(171, parm.guest_id22);
         pstmt3.setInt(172, parm.guest_id23);
         pstmt3.setInt(173, parm.guest_id24);
         pstmt3.setInt(174, parm.guest_id25);
         pstmt3.setLong(175, recur_id);

         pstmt3.executeUpdate();        // execute the prepared stmt

         pstmt3.close();
       
         //
         //   Save this date in the list of new requests
         //
         dates = mm + "/" + dd + "/" + yy;            // save date of new request
      
         recurr_dates.add(dates);                                    
                  
      }
      catch (Exception e1) {
         dbError("Error building lottery request in Common_Lott.addRecurrRequests.  Error=", e1);
      }
      
      newdate = getNextDate(days, mm, dd, yy);     // jump to the next date
   }                                               // end of WHILE in date range
   
   return(recurr_dates);

 }  // end of addRecurrRequests

 
 //************************************************************************
 //  getNextDate
 //
 //   called by:  addRecrrRequests above
 //************************************************************************

 private static long getNextDate(int days, int mm, int dd, int yy) {

    long nextdate = 0;
    
    Calendar cal = new GregorianCalendar();       // get todays date

    cal.set(Calendar.YEAR, yy);                   // set current date being used
    cal.set(Calendar.MONTH, mm-1);             
    cal.set(Calendar.DAY_OF_MONTH, dd);        
    
    cal.add(Calendar.DATE,days);                     // get next date
    yy = cal.get(Calendar.YEAR);
    mm = cal.get(Calendar.MONTH) +1;
    dd = cal.get(Calendar.DAY_OF_MONTH);

    nextdate = (yy * 10000) + (mm * 100) + dd;     // create a date field of yyyymmdd
    
    return(nextdate);

 }  // end of getNextDate


 
 //************************************************************************
 //  setRecurId - put the req id in recur_id of the original request to tie
 //               all the requests together.
 //
 //   called by:  addRecrrRequests above
 //************************************************************************

 public static void setRecurId(long lottid, Connection con) {

   PreparedStatement pstmt = null;

   try {

      pstmt = con.prepareStatement (
         "UPDATE lreqs3 SET recur_id = ? WHERE id = ?");
      
      pstmt.clearParameters();     
      pstmt.setLong(1, lottid);
      pstmt.setLong(2, lottid);
       
      pstmt.executeUpdate();     // set the id in the original req

      pstmt.close();
   }
   catch (Exception e1) {
      dbError("Error setting recur_id in Common_Lott.setRecurId.  Error=", e1);
   }
          
 }
       
 
 
 //************************************************************************
 //  rechainRecurReq - we call this method on the lottid we just updated so
 //                    that all the requests coming after it are rechained 
 //                    to this lottid.
 //
 //   called by:  Member_lott and Proshop_lott
 //************************************************************************

 public static void rechainRecurReq(long lottid, Connection con) {

   PreparedStatement pstmt = null;
   ResultSet rs = null;

   int old_recur_id = 0;
   long date = 0;
      
   try {

      pstmt = con.prepareStatement (
         "SELECT recur_id, date " +
         "FROM lreqs3 WHERE id = ?");

      pstmt.clearParameters();      
      pstmt.setLong(1, lottid);      
      rs = pstmt.executeQuery();
      
      if (rs.next()) {
          old_recur_id = rs.getInt("recur_id");
          date = rs.getLong("date"); 
      }
      
      rs.close();
      
      pstmt = con.prepareStatement (
         "UPDATE lreqs3 SET recur_id = ? WHERE date >= ? AND recur_id = ?");
      
      pstmt.clearParameters();     
      pstmt.setLong(1, lottid);      
      pstmt.setLong(2, date);     
      pstmt.setLong(3, old_recur_id);
       
      pstmt.executeUpdate();

      pstmt.close();
      
   } catch (Exception e1) {
      dbError("Error resetting recur_id in Common_Lott.rechainRecurReq.  Error=", e1);
   }
   
 }
 
 
 //************************************************************************
 //  unchainRecurReq - clear the req id in recur_id of the request to remove
 //                    it from the chain of recurring requests.
 //
 //   called by:  Member_lott, Prosho_lott
 //************************************************************************

 public static void unchainRecurReq(long lottid, Connection con) {

   PreparedStatement pstmt = null;

   try {

      pstmt = con.prepareStatement (
         "UPDATE lreqs3 SET recur_id = 0 WHERE id = ?");
      
      pstmt.clearParameters();     
      pstmt.setLong(1, lottid);
       
      pstmt.executeUpdate();     // set the id in the original req

      pstmt.close();
   }
   catch (Exception e1) {
      dbError("Error clearing recur_id in Common_Lott.unchainRecurReq.  Error=", e1);
   }
          
 }

 
 //************************************************************************
 //  setParmValues - get the request data from the original request and place in parm
 //                  so we can build the recurring requests.
 //
 //   called by:  addRecrrRequests above
 //************************************************************************

 private static void setParmValues(parmLott parm, Connection con) {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   try {

      pstmt = con.prepareStatement (
         "SELECT * " +
         "FROM lreqs3 WHERE id = ?");

      pstmt.clearParameters();      
      pstmt.setLong(1, parm.lottid);      
      rs = pstmt.executeQuery();      // get the original request  

      while (rs.next()) {

         parm.lottName = rs.getString("name");
         parm.mm = rs.getInt("mm");
         parm.dd = rs.getInt("dd");
         parm.yy = rs.getInt("yy");
         parm.day = rs.getString("day");
         parm.time = rs.getInt("time");
         parm.mins_before = rs.getInt("minsbefore");
         parm.mins_after = rs.getInt("minsafter");
         parm.player1 = rs.getString("player1");
         parm.player2 = rs.getString("player2");
         parm.player3 = rs.getString("player3");
         parm.player4 = rs.getString("player4");
         parm.player5 = rs.getString("player5");
         parm.player6 = rs.getString("player6");
         parm.player7 = rs.getString("player7");
         parm.player8 = rs.getString("player8");
         parm.player9 = rs.getString("player9");
         parm.player10 = rs.getString("player10");
         parm.player11 = rs.getString("player11");
         parm.player12 = rs.getString("player12");
         parm.player13 = rs.getString("player13");
         parm.player14 = rs.getString("player14");
         parm.player15 = rs.getString("player15");
         parm.player16 = rs.getString("player16");
         parm.player17 = rs.getString("player17");
         parm.player18 = rs.getString("player18");
         parm.player19 = rs.getString("player19");
         parm.player20 = rs.getString("player20");
         parm.player21 = rs.getString("player21");
         parm.player22 = rs.getString("player22");
         parm.player23 = rs.getString("player23");
         parm.player24 = rs.getString("player24");
         parm.player25 = rs.getString("player25");
         parm.user1 = rs.getString("user1");
         parm.user2 = rs.getString("user2");
         parm.user3 = rs.getString("user3");
         parm.user4 = rs.getString("user4");
         parm.user5 = rs.getString("user5");
         parm.user6 = rs.getString("user6");
         parm.user7 = rs.getString("user7");
         parm.user8 = rs.getString("user8");
         parm.user9 = rs.getString("user9");
         parm.user10 = rs.getString("user10");
         parm.user11 = rs.getString("user11");
         parm.user12 = rs.getString("user12");
         parm.user13 = rs.getString("user13");
         parm.user14 = rs.getString("user14");
         parm.user15 = rs.getString("user15");
         parm.user16 = rs.getString("user16");
         parm.user17 = rs.getString("user17");
         parm.user18 = rs.getString("user18");
         parm.user19 = rs.getString("user19");
         parm.user20 = rs.getString("user20");
         parm.user21 = rs.getString("user21");
         parm.user22 = rs.getString("user22");
         parm.user23 = rs.getString("user23");
         parm.user24 = rs.getString("user24");
         parm.user25 = rs.getString("user25");
         parm.pcw1 = rs.getString("p1cw");
         parm.pcw2 = rs.getString("p2cw");
         parm.pcw3 = rs.getString("p3cw");
         parm.pcw4 = rs.getString("p4cw");
         parm.pcw5 = rs.getString("p5cw");
         parm.pcw6 = rs.getString("p6cw");
         parm.pcw7 = rs.getString("p7cw");
         parm.pcw8 = rs.getString("p8cw");
         parm.pcw9 = rs.getString("p9cw");
         parm.pcw10 = rs.getString("p10cw");
         parm.pcw11 = rs.getString("p11cw");
         parm.pcw12 = rs.getString("p12cw");
         parm.pcw13 = rs.getString("p13cw");
         parm.pcw14 = rs.getString("p14cw");
         parm.pcw15 = rs.getString("p15cw");
         parm.pcw16 = rs.getString("p16cw");
         parm.pcw17 = rs.getString("p17cw");
         parm.pcw18 = rs.getString("p18cw");
         parm.pcw19 = rs.getString("p19cw");
         parm.pcw20 = rs.getString("p20cw");
         parm.pcw21 = rs.getString("p21cw");
         parm.pcw22 = rs.getString("p22cw");
         parm.pcw23 = rs.getString("p23cw");
         parm.pcw24 = rs.getString("p24cw");
         parm.pcw25 = rs.getString("p25cw");
         parm.notes = rs.getString("notes");
         parm.hide = rs.getInt("hideNotes");
         parm.fb = rs.getInt("fb");
         parm.course = rs.getString("courseName");
         parm.slots = rs.getInt("groups");
         parm.p5 = rs.getString("p5");
         parm.players = rs.getInt("players");
         parm.userg[0] = rs.getString("userg1");
         parm.userg[1] = rs.getString("userg2");
         parm.userg[2] = rs.getString("userg3");
         parm.userg[3] = rs.getString("userg4");
         parm.userg[4] = rs.getString("userg5");
         parm.userg[5] = rs.getString("userg6");
         parm.userg[6] = rs.getString("userg7");
         parm.userg[7] = rs.getString("userg8");
         parm.userg[8] = rs.getString("userg9");
         parm.userg[9] = rs.getString("userg10");
         parm.userg[10] = rs.getString("userg11");
         parm.userg[11] = rs.getString("userg12");
         parm.userg[12] = rs.getString("userg13");
         parm.userg[13] = rs.getString("userg14");
         parm.userg[14] = rs.getString("userg15");
         parm.userg[15] = rs.getString("userg16");
         parm.userg[16] = rs.getString("userg17");
         parm.userg[17] = rs.getString("userg18");
         parm.userg[18] = rs.getString("userg19");
         parm.userg[19] = rs.getString("userg20");
         parm.userg[20] = rs.getString("userg21");
         parm.userg[21] = rs.getString("userg22");
         parm.userg[22] = rs.getString("userg23");
         parm.userg[23] = rs.getString("userg24");
         parm.userg[24] = rs.getString("userg25");
         parm.orig_by = rs.getString("orig_by");
         parm.p91 = rs.getInt("p91");
         parm.p92 = rs.getInt("p92");
         parm.p93 = rs.getInt("p93");
         parm.p94 = rs.getInt("p94");
         parm.p95 = rs.getInt("p95");
         parm.p96 = rs.getInt("p96");
         parm.p97 = rs.getInt("p97");
         parm.p98 = rs.getInt("p98");
         parm.p99 = rs.getInt("p99");
         parm.p910 = rs.getInt("p910");
         parm.p911 = rs.getInt("p911");
         parm.p912 = rs.getInt("p912");
         parm.p913 = rs.getInt("p913");
         parm.p914 = rs.getInt("p914");
         parm.p915 = rs.getInt("p915");
         parm.p916 = rs.getInt("p916");
         parm.p917 = rs.getInt("p917");
         parm.p918 = rs.getInt("p918");
         parm.p919 = rs.getInt("p919");
         parm.p920 = rs.getInt("p920");
         parm.p921 = rs.getInt("p921");
         parm.p922 = rs.getInt("p922");
         parm.p923 = rs.getInt("p923");
         parm.p924 = rs.getInt("p924");
         parm.p925 = rs.getInt("p925");
         parm.checkothers = rs.getInt("checkothers");
         parm.guest_id1 = rs.getInt("guest_id1");
         parm.guest_id2 = rs.getInt("guest_id2");
         parm.guest_id3 = rs.getInt("guest_id3");
         parm.guest_id4 = rs.getInt("guest_id4");
         parm.guest_id5 = rs.getInt("guest_id5");
         parm.guest_id6 = rs.getInt("guest_id6");
         parm.guest_id7 = rs.getInt("guest_id7");
         parm.guest_id8 = rs.getInt("guest_id8");
         parm.guest_id9 = rs.getInt("guest_id9");
         parm.guest_id10 = rs.getInt("guest_id10");
         parm.guest_id11 = rs.getInt("guest_id11");
         parm.guest_id12 = rs.getInt("guest_id12");
         parm.guest_id13 = rs.getInt("guest_id13");
         parm.guest_id14 = rs.getInt("guest_id14");
         parm.guest_id15 = rs.getInt("guest_id15");
         parm.guest_id16 = rs.getInt("guest_id16");
         parm.guest_id17 = rs.getInt("guest_id17");
         parm.guest_id18 = rs.getInt("guest_id18");
         parm.guest_id19 = rs.getInt("guest_id19");
         parm.guest_id20 = rs.getInt("guest_id20");
         parm.guest_id21 = rs.getInt("guest_id21");
         parm.guest_id22 = rs.getInt("guest_id22");
         parm.guest_id23 = rs.getInt("guest_id23");
         parm.guest_id24 = rs.getInt("guest_id24");
         parm.guest_id25 = rs.getInt("guest_id25");
      }
      pstmt.close();
   }
   catch (Exception e1) {
      dbError("Error getting lottery request in Common_Lott.setParmValues.  Error=", e1);
   }
    
 }  // end of setParmValues


 
 //************************************************************************
 //  checkRecurReq - get the recur_id from the request provided.  If exists,
 //                  check if there are any future recurring requests tied
 //                  to it (matching recur_id).
 //
 //  Called by:  Member_lott
 //              Proshop_lott
 //
 //************************************************************************
 
 public static int checkRecurReq(long lottid, Connection con) {
    
   PreparedStatement pstmt = null;
   ResultSet rs = null;

   int recur_count = 0;
   long recur_id = 0;
   long date = 0;
    
    
   try {    
    
      pstmt = con.prepareStatement (
         "SELECT date, recur_id " +
         "FROM lreqs3 WHERE id = ?");

      pstmt.clearParameters();      
      pstmt.setLong(1, lottid);      
      rs = pstmt.executeQuery();    

      if (rs.next()) {

         date = rs.getLong("date");  
         recur_id = rs.getLong("recur_id");   // get this request's recur_id
      }
    
      if (recur_id > 0) {       // if this request was part of a recurring request
         
         //  see if there are more after this one
         
         pstmt = con.prepareStatement (
            "SELECT COUNT(*) " +
            "FROM lreqs3 WHERE date > ? AND recur_id = ?");

         pstmt.clearParameters();      
         pstmt.setLong(1, date);      
         pstmt.setLong(2, recur_id);      
         rs = pstmt.executeQuery();      // get the original request  

         if (rs.next()) {

            recur_count = rs.getInt(1);   // get # of recurring requests after this one
         }
      }
      
      pstmt.close();
      
   }
   catch (Exception e1) {
      dbError("Error in Common_Lott.checkRecurReq.  Error=", e1);
   }
    
   return(recur_count);
    
 }   // end of checkRecurReq  
 
 
 
 
 //************************************************************************
 //  checkNoShow - check if member was a no-show for the tee time provided.
 //
 //  Called by:  get1WeightP above
 //
 //************************************************************************
 
 public static boolean checkNoShow(long date, int time, String course, String user, Connection con) {
    
   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean noShow = false;
   
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
 
 
   try {
   
      pstmt = con.prepareStatement (
         "SELECT username1, username2, username3, username4, username5, show1, show2, show3, show4, show5 " +
         "FROM teepast2 " +
         "WHERE date = ? AND time = ? AND courseName = ? AND " +
         "(username1 = ? || username2 = ? || username3 = ? || username4 = ? || username5 = ?)");

      pstmt.clearParameters();      
      pstmt.setLong(1, date);      
      pstmt.setInt(2, time);      
      pstmt.setString(3, course);
      pstmt.setString(4, user);
      pstmt.setString(5, user);
      pstmt.setString(6, user);
      pstmt.setString(7, user);
      pstmt.setString(8, user);
      rs = pstmt.executeQuery();     

      while (rs.next()) {     // check for multiple since we don't have the fb value and member may have played more than one round

         user1 = rs.getString("username1");
         user2 = rs.getString("username2");
         user3 = rs.getString("username3");
         user4 = rs.getString("username4");
         user5 = rs.getString("username5");
         show1 = rs.getInt("show1"); 
         show2 = rs.getInt("show2"); 
         show3 = rs.getInt("show3"); 
         show4 = rs.getInt("show4"); 
         show5 = rs.getInt("show5"); 
         
         // check for no-show for this member (show values: 0=no-show, 1=checked-in, 2=pre-check-in)
         
         if ((user1.equals(user) && show1 != 1) || (user2.equals(user) && show2 != 1) || (user3.equals(user) && show3 != 1) || 
             (user4.equals(user) && show4 != 1) || (user5.equals(user) && show5 != 1)) {
            
            noShow = true;     // indicate member was a no-show
         }
      }

   } catch (Exception exc) {
         logError("Common_Lott.checkNoShow ERR: " + exc.toString());
   } finally {

      try { rs.close(); }
      catch (Exception ignore) { }

      try { pstmt.close(); }
      catch (Exception ignore) { }
   }
         
   return(noShow);
    
 }   // end of checkNoShow  
 
 
 
 
 //************************************************************************
 //  logError - logs system messages to a db table (errorlog) in Vx db
 //
 //
 //   called by:  Nearly all servelts
 //
 //************************************************************************

 public static void logError(String msg) {

    Utilities.logError( msg );

 }  // end of logError


 // *********************************************************
 //  Database Error
 // *********************************************************

 private static void dbError(String errMsg, Exception e1) {
        
   Utilities.logError(errMsg + " Exception: " + e1.getMessage());
  
 }


 // *********************************************************
 //  Program Error
 // *********************************************************

 private static void pgError(String errMsg) {

   Utilities.logError(errMsg);

 }

}
