/***************************************************************************************
 *   Common_Lott_Orig:  This servlet will process common Lottery functions.
 *
 *
 *   ***** NOTICE *****
 * 
 *      These methods were copied from SystemUtils to perform the original lottery processing
 *      until the new method is fully tested and has been utilized by all clubs.  The new method
 *      uses a different format of parmLottC and assigns tee times based on time requested versus
 *      course requested.  This file can be removed once all clubs have been switched over to the
 *      new methods in Common_Lott.  The switch is made in SystemUtils.checkTime2.
 * 
 *   *********************
 *
 * 
 * 
 *      !!!!!!!!!!!!!!! NO LONGER USED - REFER TO COMMON_LOTT !!!!!!!!!!!!!!!!!!!!!!!
 * 
 * 
 * 
 * 
 * 
 *   *********************
 *
 *   created: 8/21/2012   Bob P.
 *
 *   last updated:       ******* keep this accurate *******
 *
 *       2/26/13   Updated moveReqs call in processLott to also pass a username value.
 *
 *
 ***************************************************************************************
 */

//import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.Utilities;


public class Common_Lott_Orig {


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
      errorMsg = "Error in SystemUtils processLott (get actlott3): ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg

      Utilities.logError(errorMsg);                                       // log it
      ok = false;                                               // not ok to continue
   }

   if (ok == true) {          // if requests still exist

      errorMsg = "Error in SystemUtils processLott (get lottery info): ";
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

         Utilities.logError(errorMsg);                                       // log it
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

      errorMsg = "Error in SystemUtils processLott (count courses): ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg

      Utilities.logError(errorMsg);                                       // log it
      ok = false;                                               // not ok to continue
   }

   //
   //  Arrays to hold course information
   //
   String [] courseA = new String [courseCount];           // course names
   parmLottC [] parmcA = new parmLottC [courseCount];      // course parm blocks

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
            parmc = new parmLottC();           // get a new course parm block
            parmcA[i] = parmc;                 // save it
            i++;
         }
         stmt.close();

      } else {

         courseA[0] = parm.course;             // set course name
         parmc = new parmLottC();              // get a new course parm block
         parmcA[0] = parmc;                    // save it
      }

   }
   catch (Exception e1) {

      errorMsg = "Error in SystemUtils processLott (get course names): ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg

      Utilities.logError(errorMsg);                                       // log it
      ok = false;                                               // not ok to continue
   }

   //
   //  If still ok to proceed, then process according to the lottery type (Proshop, Random or Weighted)
   //
   if (ok == true) {       // if lottery still exist (if still ok to proceed)

      parm.sfb = fb;            // save parms
      parm.stime = stime;
      parm.etime = etime;
      parm.ltype = type;        // lottery type

      //
      //  New as of 11/18/2010 - log the number of requests for this lottery along with the number of available tee times (for reporting)
      //
      Common_Lott.logLottStats(parm, con);



      if (type.equals( "Proshop" )) {       // if proshop to process the requests manually

         errorMsg = "Error in SystemUtils processLott (type=Proshop): ";

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

            Utilities.logError(errorMsg);                                       // log it
         }

      } else {        // Weighted or Random

         //**************************************************************************************
         //  Type = Weighted or Random
         //
         //  Setup the tee time arrays for processing below
         //**************************************************************************************
         //
         try {

            //
            //  Now setup the arrays for each course
            //
            for (i=0; i < courseCount; i++) {        // do all courses

               errorMsg = "Error in SystemUtils processLott - buildArrays (type=Weighted or Random): ";

               parmc = parmcA[i];                    // get parm block for course

               parmc.course = courseA[i];            // get course name

               buildArrays(con, parm, parmc);        // build arrays

               errorMsg = "Error in SystemUtils processLott - order requests (type=Weighted or Random): ";

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

            i = 0;     // reset

            errorMsg = "Error in SystemUtils processLott (type=Random or Weighted, set state): ";

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

            errorMsg = "Error in SystemUtils processLott (type=Random or Weighted, assign times): ";

            //**************************************************************************************
            //  Type = Random or Weighted
            //
            //  Assign Times - process the requests for each course in the order listed
            //
            //**************************************************************************************
            //
            for (index=0; index < courseCount; index++) {     // do all courses

               parmc = parmcA[index];                         // get parm block for course

               assignTime(con, parm, parmc);                  // assign a tee time for each request
            }


            //*********************************************************************************
            //
            //  The requests for all courses (if more than one) have been processed and possibly assigned.
            //
            //  Now check for any requests that didn't get assigned and try other courses if possible.
            //
            //*********************************************************************************
            //
            if (courseCount > 1) {                      // if more than one course specified for this lottery

               assignTime2(con, parm, parmcA, courseCount);  // try to assign a tee time for each remaining request
            }


            errorMsg = "Error in SystemUtils processLott (type=Random or Weighted, move requests): ";

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

                  Common_Lott.moveReqs(name, date, course, "lottautoprocess", true, con);    // move the requests for this course
               }
            }

         }
         catch (Exception e2) {
            //
            //  save error message in /v_x/error.txt
            //
            errorMsg = errorMsg + "Exception2= " +e2.getMessage();                  // build error msg

            Utilities.logError(errorMsg);                                       // log it
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
      errorMsg = "Error in SystemUtils processLott (delete actlott3): ";
      errorMsg = errorMsg + e3.getMessage();                                 // build error msg

      Utilities.logError(errorMsg);                                       // log it
   }

 }  // end of processLott


 
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

   ResultSet rs = null;

   int i = 0;
   int tfb = 0;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";

   String errorMsg = "Error in SystemUtils buildArrays (type=Random or Weighted, get info): ";


   //
   //   Locate all available tee times to be used in this lottery on this course.
   //
   //   NOTE:  include any tee times that already have players so that the assign process will still find
   //          a matching tee time.  This is for the case where a pro pre-books a tee time, but other members
   //          have already submitted a lottery request for that time.
   //
   try {
      if (parm.sfb.equals( "Both" )) {

         PreparedStatement pstmt5 = con.prepareStatement (
            "SELECT time, fb, player1, player2, player3, player4, player5 " +
            "FROM teecurr2 WHERE date = ? AND courseName = ? AND time >= ? AND time <= ? AND fb < 2 AND " +
            "event = '' AND blocker = '' " +
            "ORDER BY time, fb");

         pstmt5.clearParameters();        // clear the parms
         pstmt5.setLong(1, parm.date);
         pstmt5.setString(2, parmc.course);
         pstmt5.setInt(3, parm.stime);
         pstmt5.setInt(4, parm.etime);

         rs = pstmt5.executeQuery();      // execute the prepared stmt

         i = 0;

         while (rs.next() && i < 100) {

            parmc.timeA[i] = rs.getInt(1);        // put tee time in array
            parmc.fbA[i] = rs.getShort(2);        // put f/b in array
            player1 = rs.getString(3);            // get players
            player2 = rs.getString(4);
            player3 = rs.getString(5);
            player4 = rs.getString(6);
            player5 = rs.getString(7);

            parmc.busyA[i] = false;

            if (!player1.equals("") || !player2.equals("") || !player3.equals("") || !player4.equals("") || !player5.equals("")) {

               parmc.busyA[i] = true;     // flag this time as busy so it is not assigned
            }

            i++;
         }
         pstmt5.close();

      } else {

         tfb = 0;         // default = Front

         if (parm.sfb.equals( "Back" )) {

            tfb = 1;         // back tee
         }

         PreparedStatement pstmt6 = con.prepareStatement (
            "SELECT time, fb, player1, player2, player3, player4, player5 " +
            "FROM teecurr2 WHERE date = ? AND courseName = ? AND fb = ? AND time >= ? AND time <= ? AND " +
            "event = '' AND blocker = '' " +
            "ORDER BY time");

         pstmt6.clearParameters();        // clear the parms
         pstmt6.setLong(1, parm.date);
         pstmt6.setString(2, parmc.course);
         pstmt6.setInt(3, tfb);
         pstmt6.setInt(4, parm.stime);
         pstmt6.setInt(5, parm.etime);

         rs = pstmt6.executeQuery();      // execute the prepared stmt

         i = 0;

         while (rs.next() && i < 100) {

            parmc.timeA[i] = rs.getInt(1);        // put tee time in array
            parmc.fbA[i] = rs.getShort(2);        // put f/b in array
            player1 = rs.getString(3);            // get players
            player2 = rs.getString(4);
            player3 = rs.getString(5);
            player4 = rs.getString(6);
            player5 = rs.getString(7);

            parmc.busyA[i] = false;

            if (!player1.equals("") || !player2.equals("") || !player3.equals("") || !player4.equals("") || !player5.equals("")) {

               parmc.busyA[i] = true;     // flag this time as busy so it is not assigned
            }
            i++;
         }
         pstmt6.close();

      }      // end of IF fb (common weighted and random processing)

   }
   catch (Exception e3) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e3.getMessage();                                 // build error msg
      Utilities.logError(errorMsg);                                       // log it
   }
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
 //               parmc  = lottery parm block for this course
 //************************************************************************

 public static void orderReqsRan(Connection con, parmLott parm, parmLottC parmc) {

   ResultSet rs = null;

   int i = 0;

   long id = 0;


   String errorMsg = "Error in SystemUtils orderReqsRan (type=Random, order Reqs): ";

   try {

      String course = parmc.course;           // get course name

      //
      //  Get the lottery requests for the lottery name and date that were passed
      //
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT id " +
         "FROM lreqs3 " +
         "WHERE name = ? AND date = ? AND courseName = ? " +
         "ORDER BY RAND()");                                  // Random order

      pstmt3.clearParameters();        // clear the parms
      pstmt3.setString(1, parm.lottName);
      pstmt3.setLong(2, parm.date);
      pstmt3.setString(3, course);
      rs = pstmt3.executeQuery();

      while (rs.next() && i < 100) {       // get all lottery requests up to 100

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
      Utilities.logError(errorMsg);                                       // log it
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
 //               parmc  = lottery parm block for this course
 //************************************************************************

 public static void orderReqsWBR(Connection con, parmLott parm, parmLottC parmc) {

   ResultSet rs = null;


   long [] idA = new long [100];          // request id array
   int [] wghtA = new int [100];          // weight of this request
   int [] playersA = new int [100];       // # of players in this request

   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int i4 = 0;
   int weight = 0;

   long id = 0;


   String errorMsg = "Error in SystemUtils orderReqsWBR (type=Weighted By Rounds, order Reqs): ";
   String club = "";

   try {
       
       club = con.getCatalog();           // get db (club) name
       
   }
   catch (Exception e) {       
   }

   
   try {

      String course = parmc.course;           // get course name

      //
      //  Get the lottery requests for the lottery name and date that were passed
      //
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT id, players " +
         "FROM lreqs3 " +
         "WHERE name = ? AND date = ? AND courseName = ?");
//         "ORDER BY players DESC");                    // process larger groups first

      pstmt3.clearParameters();        // clear the parms
      pstmt3.setString(1, parm.lottName);
      pstmt3.setLong(2, parm.date);
      pstmt3.setString(3, course);
      rs = pstmt3.executeQuery();

      while (rs.next() && i < 100) {        // get all lottery requests up to 100

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
      while (i < 100) {

         id = idA[i];             // get an id
         weight = wghtA[i];       // get its weight

         if (id > 0 && playersA[i] > 1) {     // if id exists and more than one player

            i3 = i;               // save for move

            i2 = i + 1;           // start at next spot

            while (i2 < 100) {                   // compare against the rest

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
      //  Now move any single payers from the temp arrays above into the parm block (order by weight)
      //
      i = 0;                      // start at beginning

      while (i < 100) {

         id = idA[i];             // get an id
         weight = wghtA[i];       // get its weight

         if (id > 0) {     // if id exists

            i3 = i;               // save for move

            i2 = i + 1;           // start at next spot

            while (i2 < 100) {                   // compare against the rest

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
      Utilities.logError(errorMsg);                                       // log it
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
 //               parmc  = lottery parm block for this course
 //************************************************************************

 public static void orderReqsWBP(Connection con, parmLott parm, parmLottC parmc) {

   ResultSet rs = null;


   long [] idA = new long [100];          // request id array
   int [] wghtA = new int [100];          // weight of this request
   int [] playersA = new int [100];       // # of players in this request

   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int i4 = 0;
   int weight = 0;

   long id = 0;


   String errorMsg = "Error in SystemUtils orderReqsWBP (type=Weighted By Proximity, order Reqs): ";

   try {

      String course = parmc.course;           // get course name

      //
      //  Get the lottery requests for the lottery name and date that were passed
      //
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT id, players " +
         "FROM lreqs3 " +
         "WHERE name = ? AND date = ? AND courseName = ?");
//         "ORDER BY players DESC");                    // process larger groups first

      pstmt3.clearParameters();        // clear the parms
      pstmt3.setString(1, parm.lottName);
      pstmt3.setLong(2, parm.date);
      pstmt3.setString(3, course);
      rs = pstmt3.executeQuery();

      while (rs.next() && i < 100) {              // get all lottery requests up to 100

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
      while (i < 100) {

         id = idA[i];             // get an id
         weight = wghtA[i];       // get its weight

         if (id > 0 && playersA[i] > 1) {     // if id exists and more than one player

            i3 = i;               // save for move

            i2 = i + 1;           // start at next spot

            while (i2 < 100) {                   // compare against the rest

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
      //  Now move any single payers from the temp arrays above into the parm block (order by weight)
      //
      i = 0;                      // start at beginning

      while (i < 100) {

         id = idA[i];             // get an id
         weight = wghtA[i];       // get its weight

         if (id > 0) {     // if id exists

            i3 = i;               // save for move

            i2 = i + 1;           // start at next spot

            while (i2 < 100) {                   // compare against the rest

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
      Utilities.logError(errorMsg);                                       // log it
   }
 }  // end of orderReqsWBP


 //************************************************************************
 //  assignTime - assign a Tee Time to the lottery requests for the course passed
 //
 //    For Random and Weighted lottery types
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = the lottery parameter block
 //               con    = db connection
 //               parmc  = lottery parm block for this course
 //************************************************************************

 public static void assignTime(Connection con, parmLott parm, parmLottC parmc) {


   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int i4 = 0;
   int weight = 0;
   int ttime = 0;
   short tfb = 0;
   long id = 0;

   int fail = 0;   // init the assign indicator

   String errorMsg = "Error in SystemUtils assignTime: ";


   for (i=0; i < 100; i++) {         // do all requests for this course

      id = parmc.idA[i];               // get an id
      weight = parmc.wghtA[i];         // get its weight

      if (id > 0) {                     // if exists

         fail = assign1Time(con, parm, parmc, id, weight);          // try to assign a time

         //
         //  If req failed, then we must update the weight (Weighted type only) and save the fail code
         //
         // if (fail > 0 && parm.ltype.startsWith( "Weighted" )) {
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
               Utilities.logError(errorMsg);                                       // log it
            }
         }

      }
   }


   //*****************************************************************************
   //  Lottery for this course has been processed - set the assigned times in the requests
   //*****************************************************************************
   //
   i = 0;

   loop5:
   while (i < 100) {

      ttime = parmc.timeA[i];        // get tee time from array

      if (ttime == 0) {              // if reached end of tee times

         break loop5;                // exit loop
      }

      tfb = parmc.fbA[i];            // get the f/b for this time slot
      id = parmc.id2A[i];            // get the request id assigned to this time slot
      weight = parmc.wght2A[i];      // get the weight for this time slot

      if (id != 0) {                 // if assigned

         parmc.id2A[i] = 999999;     // mark it done
         i2 = i + 1;                 // get next slot
         i3 = i2 + 11;               // furthest we should have to search for matching id
         i4 = 1;
         parmc.atimeA[0] = ttime;    // init time values - set 1st one
         parmc.atimeA[1] = 0;
         parmc.atimeA[2] = 0;
         parmc.atimeA[3] = 0;
         parmc.atimeA[4] = 0;

         while (i2 < i3 && i2 < 100 && i4 < 5) {    // look for matching id's (more than 1 tee time)

            if (parmc.id2A[i2] == id) {               // if match

               parmc.atimeA[i4] = parmc.timeA[i2];    // save time value
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
         errorMsg = "Error in SystemUtils assignTime (type=Random/Weighted, set assigned): ";

         try {

            PreparedStatement pstmt9 = con.prepareStatement (
                "UPDATE lreqs3 SET type = ?, state = 2, atime1 = ?, atime2 = ?, atime3 = ?, atime4 = ?, " +
                "atime5 = ?, afb = ?, weight = ?, afb2 = ?, afb3 = ?, afb4 = ?, afb5 = ?, fail_code = 0 " +
                "WHERE id = ?");

            pstmt9.clearParameters();        // clear the parms
            pstmt9.setString(1, parm.ltype);       // weighted or random
            pstmt9.setInt(2, parmc.atimeA[0]);
            pstmt9.setInt(3, parmc.atimeA[1]);
            pstmt9.setInt(4, parmc.atimeA[2]);
            pstmt9.setInt(5, parmc.atimeA[3]);
            pstmt9.setInt(6, parmc.atimeA[4]);
            pstmt9.setShort(7, tfb);
            pstmt9.setInt(8, weight);
            pstmt9.setShort(9, tfb);
            pstmt9.setShort(10, tfb);
            pstmt9.setShort(11, tfb);
            pstmt9.setShort(12, tfb);
            pstmt9.setLong(13, id);

            pstmt9.executeUpdate();

            pstmt9.close();

         }
         catch (Exception e3) {
            //
            //  save error message in /v_x/error.txt
            //
            errorMsg = errorMsg + e3.getMessage();                                 // build error msg
            Utilities.logError(errorMsg);                                       // log it
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
 //               parmc  = lottery parm block for this course
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

   int failCode = 0;               // status to return - init to success

   short tfb = 0;
//   short save_fb = 0;

   boolean fail = true;               // init to failed status
   boolean restricted = false;

   String p5 = "";

   String errorMsg = "Error in SystemUtils assign1Time: ";
   
   String save_course = "";


   try {

      //
      //  get the request info for id passed
      //
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT time, minsbefore, minsafter, " +
         "fb, groups, p5, players " +
         "FROM lreqs3 " +
         "WHERE id = ?");

      pstmt3.clearParameters();
      pstmt3.setLong(1, id);
      rs = pstmt3.executeQuery();

      if (rs.next()) {

         time = rs.getInt(1);
         before = rs.getInt(2);
         after = rs.getInt(3);
         parm.rfb = rs.getShort(4);
         groups = rs.getInt(5);
         p5 = rs.getString(6);
         players = rs.getInt(7);

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
//         save_fb = parm.rfb;        // save f/b from request
         parm.fb_after = parm.rfb;    // init f/b for after times
         parm.fb_before = parm.rfb;   // init f/b for before times
         // fail = false;             // changed to init fail to true and set to false only if assigned
         failCode = 1;                // default failure reason = time not available

         //
         //  Process this request
         //
         loop1:
         while (i < 100) {               // loop through arrays for a matching time

            ttime = parmc.timeA[i];       // get tee time from array
            tfb = parmc.fbA[i];           // get associated f/b

            if (ttime == time2 && tfb == parm.rfb) {   // if matching time/fb found

               grps = groups;         // # of groups requested - temp for work below
               i3 = i;                // temp index for work below
               parm.beforei = i;      // init before and after indexes
               parm.afteri = i;
               i2 = 999;              // init index save
               restricted = false;    // init restricted flag

               //
               //  Time found that matches the requested time - check if available times for all groups
               //
               while (grps > 0) {

                  if (astat == 0 || bstat == 0) {   // if before or after still open to check

                     if (parmc.id2A[i3] == 0 && parmc.busyA[i3] == false && parmc.timeA[i3] > 0 && restricted == false) {  // if spot open (check associated entry in Id Array)

                        error = 0;                     // init

                        if (i2 == 999) {

                           i2 = i3;           // save first open spot
                        }

                        i3++;                 // next spot
                        grps--;               // next grp

                        if (grps > 0) {          // if still more groups

                           tfb = parmc.fbA[i3];          // get associated f/b of next slot

                           if (tfb != parm.rfb) {        // if not matching f/b

                              i3++;                      // next spot
                              tfb = parmc.fbA[i3];       // get associated f/b of next slot

                              if (tfb != parm.rfb) {     // if not matching f/b

                                 error = 1;              // error!! only need to check 2 slots
                                 i2 = 999;
                              }
                           }
                        }

                     } else {

                        error = 1;                        // indicate error if not available
                        i2 = 999;                         // reset first available index

                        if (restricted == true) {
                           failCode = 2;                  // reason = member restricted
                        }

                     }

                     if (error > 0) {                     // if error - try another time slot

                        if (astat == 0 || bstat == 0) {   // if before or after still open to check

                           if (toggle == 0 && astat == 0) {  // ok to check after time ?                    
                           //if (toggle == 0) {             // check after time ?

                              toggle = 1;                 // check before time next

                              //if (astat == 0) {           // if after still open to check

                                 parm.groups = groups;        // save current parms

                                 astat = checkAfter(parm, parmc);   // go check for an after time

                                 if (astat == 1 && bstat == 0) {     // if 'after' failed and 'before' ok to check

                                    bstat = checkBefore(parm, parmc);    // go check for a before time
                                 }

                                 i3 = parm.i3;
                                 grps = parm.grps;
                              //}                    // end of IF astat = 0

                           } else {                // check 'after' times

                              toggle = 0;                 // check after time next

                              if (bstat == 0) {           // if before still open to check

                                 parm.groups = groups;        // save current parms

                                 bstat = checkBefore(parm, parmc);    // go check for a before time

                                 if (bstat == 1) {                   // if 'before' failed

                                    if (astat == 0) {                   // if 'after' ok to check

                                       astat = checkAfter(parm, parmc);    // go check for an after time

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

                              Common_Lott.getParmValues(parm, con);    // go set parm values needed for restriction processing

                              parm.time = parmc.timeA[i3];      // override time and fb
                              parm.fb = parm.rfb;
                              parm.ind = 1;                     // index not pertinent for this - use 1 to get through restrictions
                              
                              save_course = parm.course;        // save the lottery course
                              parm.course = parmc.course;       // use this specific course for restriction processing

                              restricted = Common_Lott.checkRests(0, parm, con);  // check restrictions (returns true if a member is restricted)
                                                                                  // if true, forces a new search (above)
                              
                              parm.course = save_course;       // restore the lottery course
                              
                              if (restricted == true) {

                                 grps = groups;          // start over if restricted (the restricted=true will force the logic above to keep trying)
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

                        Common_Lott.getParmValues(parm, con);    // go set parm values needed for restriction processing

                        parm.time = parmc.timeA[i2];      // override time and fb
                        parm.fb = parm.rfb;
                        parm.ind = 1;                     // index not pertinent for this - use 1 to get through restrictions

                        save_course = parm.course;        // save the lottery course
                        parm.course = parmc.course;       // use this specific course for restriction processing

                        restricted = Common_Lott.checkRests(0, parm, con);  // check restrictions (returns true if a member is restricted)
                                                                            // if true, forces a new search (above)
                        parm.course = save_course;       // restore the lottery course
                              
                        if (restricted == true) {

                           grps = groups;          // start over if restricted (the restricted=true will force the logic above to keep trying)
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

                  while (grps > 0 && i2 < 100) {       // make sure we don't go past end of array

                     tfb = parmc.fbA[i2];              // get associated f/b

                     if (tfb == parm.rfb) {            // if matching f/b found

                        if (players > full) {           // determine # of players for this group
                           count = full;                // 4 or 5
                           players = players - full;    // new count
                        } else {
                           count = players;
                           players = 0;
                        }

                        parmc.id2A[i2] = id;            // put this id in array to reserve this spot
                        parmc.players2A[i2] = count;    // set # of players in this tee time
                        parmc.wght2A[i2] = weight;      // set weight for this tee time
                        weight = 9999;                  // weight for susequent grps (only 1st has weight)
                        grps--;
                     }
                     i2++;
                  }

                  fail = false;               // success!!!
                  break loop1;                // done with this request - exit while loop

               } else {                       // request failed - try other tees?

                  //  Just do this now after commenting out the code below!!  (testing new f/b processing - see checkBefore & checkAfter)
                  fail = true;              // this req failed
                  break loop1;              // done with this request - exit while loop

/*
                  if (parm.sfb.equals( "Both" )) {   // if both tees used for this lottery

                     if (parm.rfb == save_fb) {     // if we just tried the first f/b

                        if (parm.rfb == 0) {        // if front

                           parm.rfb = 1;            // try back tees
                        } else {
                           parm.rfb = 0;            // try front;
                        }
                        stat = 0;              // init status = ok
                        astat = 0;             // init after times status = ok
                        bstat = 0;             // init before times status = ok
                        toggle = 0;            // init before/after toggle
                        i = 999;               // start over

                     } else {

                        fail = true;           // this req failed
                        break loop1;           // done with this request - exit while loop
                     }
                  } else {

                     fail = true;              // this req failed
                     break loop1;              // done with this request - exit while loop
                  }
*/

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
      errorMsg = errorMsg + e3.getMessage();                    // build error msg
      Utilities.logError(errorMsg);                                       // log it
      fail = true;                                              // this req failed
      failCode = 99;                                            // indicate System Problem
   }

   if (fail == false) failCode = 0;              // make sure the failure reason code is zero if assign was successful

   if (fail == true && failCode == 0) failCode = 9;   // failed but no reason code - use 'Other'
   
   if (failCode == 2 && parm.club.equals("mesaverdecc")) {      // if failed because of restrictions and Mesa verde - log it for debug
       
       Utilities.logDebug("BP", "Lottery Req Failed - Member Restricted (Mesa Verde). Req Id = " +id+ ". Error = " +parm.error_hdr+ ". Msg = " +parm.error_msg+ ".");                                   
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

   String errorMsg = "Error in SystemUtils assignTime2: ";


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
                        errorMsg = "Error in SystemUtils assignTime2 (type=Random/Weighted, set assigned): ";

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
                           Utilities.logError(errorMsg);                                       // log it
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

         Utilities.logError(errorMsg);                                       // log it
      }

   }

 }  // end of assignTime2


 //************************************************************************
 // checkBefore - Look for an available time before the requested time
 // checkAfter - Look for an available time after the requested time
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = lottery parm block
 //
 //************************************************************************

 public static int checkBefore(parmLott parm, parmLottC parmc) {


    int bstat = 0;
    int i3 = 0;
    int grps = 0;
    int ttime = 0;


    i3 = parm.beforei;                  // get latest before index
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

       loop2:
       while (i3 > 0 && grps > 0) {     // while room to back up

          i3--;                         // back up one tee time slot
          ttime = parmc.timeA[i3];      // get tee time from array

          if (ttime >= parm.ftime) {    // if tee time is acceptable

             if (parmc.fbA[i3] == parm.fb_before && parmc.busyA[i3] == false) {   // if matching f/b found & not busy

                grps--;                 // match found, continue to back up
             }

          } else {

             bstat = 1;                // can't go back any further
             grps = parm.groups;       // reset # of groups
             i3++;                     // went too far - go back so check fails
             break loop2;              // exit while loop
          }
       }                               // end of loop2 WHILE

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

    parm.i3 = i3;           // set new values for return
    parm.grps = grps;

    return(bstat);

 }  // end of checkBefore


 public static int checkAfter(parmLott parm, parmLottC parmc) {

    int astat = 0;
    int i3 = 0;
    int grps = 0;
    int ttime = 0;
    int i4 = 999;                      // init start location

    parm.afteri++;                     // bump to next after spot
    i3 = parm.afteri;
    grps = parm.groups;                // restore # of groups


    if ((i3 + grps) < 100) {              // if room to go ahead

       if (parm.sfb.equals( "Both" )) {   // if both tees used for this lottery

          //
          //  Use both tees - switch tees if both are defined (double tees for this day)
          //
          if (parmc.fbA[i3] != parm.fb_after) {     // if different f/b found than last checked

             parm.fb_after = parmc.fbA[i3];        // then switch
          }
       }

       loop3:
       while (i3 < 100 && grps > 0) {   // while room to search ahead

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
             while (i3 < 100 && grps > 0) {   // while room to search ahead

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
       }                 // end of IF i3 > groups

    } else {

       astat = 1;           // can't go ahead any more
    }                 // end of IF i3 < 100

    parm.i3 = i3;           // set new values for return
    parm.grps = grps;

    return(astat);

 }  // end of checkAfter


 
 //************************************************************************
 // movePlayers - Move players from a partial lottery request to a lottery
 //               request with space available.
 //
 //
 //   called by:  processLott above
 //
 //       parms:  con   = db connection
 //               id    = id of the lottery request to move from
 //               tid   = id of the lottery request to move to
 //
 //************************************************************************

 private static void movePlayers(Connection con, long id, long tid) {


   ResultSet rs = null;

   int i = 0;
   int i2 = 0;

   String [] playersA = new String [25];       // players in the 'to' request
   String [] playersB = new String [4];        // players in the 'from' request

   //
   //  Get the players from the 'from' request (can't be more than 4)
   //
   try {
      PreparedStatement pstmt8 = con.prepareStatement (
         "SELECT player1, player2, player3, player4 " +
         "FROM lreqs3 " +
         "WHERE id = ?");

      pstmt8.clearParameters();        // clear the parms
      pstmt8.setLong(1, id);
      rs = pstmt8.executeQuery();

      if (rs.next()) {          // get all of them

         playersB[0] = rs.getString(1);
         playersB[1] = rs.getString(2);
         playersB[2] = rs.getString(3);
         playersB[3] = rs.getString(4);
      }
      pstmt8.close();

      PreparedStatement pstmt9 = con.prepareStatement (
         "SELECT player1, player2, player3, player4, player5, player6, player7, player8, " +
         "player9, player10, player11, player12, player13, player14, player15, player16, " +
         "player17, player18, player19, player20, player21, player22, player23, player24, player25 " +
         "FROM lreqs3 " +
         "WHERE id = ?");

      pstmt9.clearParameters();        // clear the parms
      pstmt9.setLong(1, tid);
      rs = pstmt9.executeQuery();

      if (rs.next()) {          // get all of them

         playersA[0] = rs.getString(1);
         playersA[1] = rs.getString(2);
         playersA[2] = rs.getString(3);
         playersA[3] = rs.getString(4);
         playersA[4] = rs.getString(5);
         playersA[5] = rs.getString(6);
         playersA[6] = rs.getString(7);
         playersA[7] = rs.getString(8);
         playersA[8] = rs.getString(9);
         playersA[9] = rs.getString(10);
         playersA[10] = rs.getString(11);
         playersA[11] = rs.getString(12);
         playersA[12] = rs.getString(13);
         playersA[13] = rs.getString(14);
         playersA[14] = rs.getString(15);
         playersA[15] = rs.getString(16);
         playersA[16] = rs.getString(17);
         playersA[17] = rs.getString(18);
         playersA[18] = rs.getString(19);
         playersA[19] = rs.getString(20);
         playersA[20] = rs.getString(21);
         playersA[21] = rs.getString(22);
         playersA[22] = rs.getString(23);
         playersA[23] = rs.getString(24);
         playersA[24] = rs.getString(25);
      }
      pstmt9.close();

      //
      //  Find the available spots and move the players
      //
      loop1:
      while (i < 25 && i2 < 5) {

         if (playersA[i].equals( "" )) {

            playersA[i] = playersB[i2];       // move player
            i2++;                             // next player
         }
         i++;
      }
      //
      //  Put all the players into the 'to' request
      //
      PreparedStatement pstmt2 = con.prepareStatement (
          "UPDATE lreqs3 " +
          "SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, player5 = ?, player6 = ?, player7 = ?, player8 = ?, " +
          "player9 = ?, player10 = ?, player11 = ?, player12 = ?, player13 = ?, player14 = ?, player15 = ?, player16 = ?, " +
          "player17 = ?, player18 = ?, player19 = ?, player20 = ?, player21 = ?, player22 = ?, player23 = ?, " +
          "player24 = ?, player25 = ? " +
          "WHERE id = ?");

      pstmt2.clearParameters();        // clear the parms
      pstmt2.setString(1, playersA[0]);
      pstmt2.setString(2, playersA[1]);
      pstmt2.setString(3, playersA[2]);
      pstmt2.setString(4, playersA[3]);
      pstmt2.setString(5, playersA[4]);
      pstmt2.setString(6, playersA[5]);
      pstmt2.setString(7, playersA[6]);
      pstmt2.setString(8, playersA[7]);
      pstmt2.setString(9, playersA[8]);
      pstmt2.setString(10, playersA[9]);
      pstmt2.setString(11, playersA[10]);
      pstmt2.setString(12, playersA[11]);
      pstmt2.setString(13, playersA[12]);
      pstmt2.setString(14, playersA[13]);
      pstmt2.setString(15, playersA[14]);
      pstmt2.setString(16, playersA[15]);
      pstmt2.setString(17, playersA[16]);
      pstmt2.setString(18, playersA[17]);
      pstmt2.setString(19, playersA[18]);
      pstmt2.setString(20, playersA[19]);
      pstmt2.setString(21, playersA[20]);
      pstmt2.setString(22, playersA[21]);
      pstmt2.setString(23, playersA[22]);
      pstmt2.setString(24, playersA[23]);
      pstmt2.setString(25, playersA[24]);
      pstmt2.setLong(26, tid);

      pstmt2.executeUpdate();

      pstmt2.close();

      //
      // delete the request after players have been moved
      //
      PreparedStatement pstmt1 = con.prepareStatement (
               "DELETE FROM lreqs3 WHERE id = ?");

      pstmt1.clearParameters();               // clear the parms
      pstmt1.setLong(1, id);
      pstmt1.executeUpdate();

      pstmt1.close();

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils movePlayer: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      Utilities.logError(errorMsg);                                       // log it
   }

 }  // end of movePlayers


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
      String errorMsg = "Error in SystemUtils getWeight: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      Utilities.logError(errorMsg);                                       // log it
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
      String errorMsg = "Error in SystemUtils get1Weight: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      Utilities.logError(errorMsg);                                       // log it
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
      String errorMsg = "Error in SystemUtils getWeightP: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      Utilities.logError(errorMsg);                                       // log it
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

           String errorMsg = "Error in SystemUtils getWeight: ";
           errorMsg = errorMsg + exc.getMessage();                                 // build error msg
           Utilities.logError(errorMsg);                                       // log it
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

            if (guest == 0 || (club.equals("dataw") && playerA[i].startsWith("Blank"))) {           // if guest don't count

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

   int count = 0;
   int mins = 0;
   int total = 0;
   int weight = 0;
   
   boolean thisLottOnly = false;


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
   //  calculate the weight for this user based on the parms passed and his/her past lottery results
   //
   try {

      if (thisLottOnly == true) {       // if only using this lotterry

         query = "SELECT mins " +
                   "FROM lassigns5 WHERE username = ? AND date >= ? AND date <= ? AND lname = ?";

      } else {

         query = "SELECT mins " +
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

         mins = rs.getInt(1);        // minutes from requested time

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
      String errorMsg = "Error in SystemUtils get1WeightP: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      Utilities.logError(errorMsg);                                       // log it

      weight = 20;               // default weight
   }

   return(weight);

 }  // end of get1WeightP

 
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
      String errorMsg = "Error in SystemUtils saveWeights: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      Utilities.logError(errorMsg);                                       // log it
   }

 }  // end of saveWeights
 


}
