/***************************************************************************************
 *   getClub:  This servlet will provide some common tee time request processing methods.
 *
 *       called by:  Proshop_slot
 *                   Proshop_dsheet
 *
 *
 *   created:  1/12/2004   Bob P.
 *
 *
 *   last updated:
 *
 *      8/02/10  Fort Collins CC (fortcollins) - Updated customs to include Fox Hill CC
 *      4/19/10  Commented out initialization of indexes 23-36 of guest array for Fortcollins/Greeley CC
 *      4/19/10  Do not populate parm.gDb values if guest tracking system is not active.
 *      1/04/10  Change processing to also populate gDb array with use_guestdb values out of guest5 table
 *      9/04/09  Change processing to grab guest types from guest5 table and mships from mship5 table instead of club5
 *      8/22/09  Make sure getParms is always working with a root activity
 *      6/12/09  Modified getParms method to return ativity specific values
 *      2/26/09  Change getPOS to allow for an empty course name when multi-course club.
 *     10/03/08  Add getLotteryText method to get the verbiage for 'lottery' replacement.
 *      7/17/08  Add pos_paynow flag in getPOS (case 1429).
 *      7/07/08  Add 'Default Course' fields for customs to default course to a specific on or ALL (case 1513).
 *      5/14/08  Add 'Member Cutoff' options - days and time to cutoff tee time access to members (case 1480).
 *      4/17/08  Add Revenue option array for guest types (case 1400).
 *      3/17/08  Added an additional guest type for Greeley
 *     10/24/07  Added new club option max_originations
 *      5/08/07  getParms - get the 'viewdays' for each mship type.
 *      3/19/07  Added two additional guest types for Greeley
 *      2/15/07  Custom for Greeley/Fort Collins - seperate the guest types for each course/club.
 *      7/21/06  Added new club option no_reservations
 *      6/12/06  Added new club option paceofplay
 *      2/26/05  Ver 5 - added new club option precheckin
 *      1/24/05  RDP  Ver 5 - change club2 to club5.
 *     11/16/04  Ver 5 - add new club options.
 *
 ***************************************************************************************
 */


package com.foretees.common;

//import java.io.*;
//import java.util.*;
import java.sql.*;
import javax.servlet.*;


public class getClub {



   
/**
 //************************************************************************
 //
 //  Get the club name (name of database) - used when 'club' is not available
 //
 //************************************************************************
 **/

 public static String getClubName(Connection con) {


   Statement stmt = null;
   ResultSet rs = null;
   
   String club = "";
   
   try {

      stmt = con.createStatement();

      rs = stmt.executeQuery("SELECT DATABASE()");

      if (rs.next()) club = rs.getString(1);        // get the database name (club name)
       
   } catch (Exception e) {

      Utilities.logError("Error getting database name - getClub.getClubName " + e.getMessage());

   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

   }

   return( club );

 }
   
   
   
   
/**
 //************************************************************************
 //
 //  Get the club parms
 //
 //************************************************************************
 **/

 public static void getParms(Connection con, parmClub parm)
         throws Exception {


     getParms(con, parm, 0); // default to golf

 }


 public static void getParms(Connection con, parmClub parm, int activity_id)
         throws Exception {

   // make sure we are loading data for a root activity
   if (activity_id != 0) activity_id = getActivity.getRootIdFromActivityId(activity_id, con);

   Statement stmt = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   
   int i = 0;

   boolean use_guestdb = Utilities.isGuestTrackingConfigured(activity_id, con);

   try {
   
   // init guest & mship arrays to eliminate null values
   for (i = 0; i < parm.MAX_Guests; i++) {
       parm.guest[i] = "";
       parm.gOpt[i] = 0;
       parm.gRev[i] = 0;
       parm.gDb[i] = 0;
   }
   
   // just init the strings for mship array
   for (i = 0; i < parm.MAX_Mships; i++) {

     parm.mship[i] = "";
     parm.period[i] = "";
     parm.advamd1[i] = "";
     parm.advamd2[i] = "";
     parm.advamd3[i] = "";
     parm.advamd4[i] = "";
     parm.advamd5[i] = "";
     parm.advamd6[i] = "";
     parm.advamd7[i] = "";
   }
   
   } catch (Exception exc) {
       
      throw new Exception("Error preparing variables - getClub.getParms, Error=" + exc.getMessage());
      
   }
   
   
   //
   //  get the club's parameters
   //
   try {

      stmt = con.createStatement();        // create a statement
      
      rs = stmt.executeQuery("SELECT * FROM club5");
      
      if (rs.next()) {

         parm.clubName = rs.getString("clubName");
         parm.multi = rs.getInt("multi");
         parm.lottery = rs.getInt("lottery");
           

         //
         // ONLY FORT COLLINS STILL USES THE GUESTS FROM THE CLUB5 TABLE!!! ALL OTHERS LOAD FROM GUEST5
         //
         if (parm.club.equals( "fortcollins" )) {     // if Fort Collins or Greeley (shared site)
           
            if (parm.course.equals( "Greeley CC" )) {     // if Greeley

               parm.MAX_Guests = 11;
               parm.guest[0] = rs.getString("guest15");      // guest 15 - 24 are for Greeley
               parm.guest[1] = rs.getString("guest16");
               parm.guest[2] = rs.getString("guest17");
               parm.guest[3] = rs.getString("guest18");
               parm.guest[4] = rs.getString("guest19");
               parm.guest[5] = rs.getString("guest20");
               parm.guest[6] = rs.getString("guest21");
               parm.guest[7] = rs.getString("guest22");     // added two new guest types for Greeley (03/19/07)
               parm.guest[8] = rs.getString("guest23");
               parm.guest[9] = rs.getString("guest24");     // added one new guest type for Greeley (04/05/07)
               parm.guest[10] = rs.getString("guest13");    // added new guest type for Geeley (03/17/08)
               parm.guest[11] = "";
               parm.guest[12] = "";
               parm.guest[13] = "";
               parm.guest[14] = "";
               parm.guest[15] = "";
               parm.guest[16] = "";
               parm.guest[17] = "";
               parm.guest[18] = "";
               parm.guest[19] = "";
               parm.guest[20] = "";
               parm.guest[21] = "";
               parm.guest[22] = "";
               /*
               parm.guest[23] = "";
               parm.guest[24] = "";
               parm.guest[25] = "";
               parm.guest[26] = "";
               parm.guest[27] = "";
               parm.guest[28] = "";
               parm.guest[29] = "";
               parm.guest[30] = "";
               parm.guest[31] = "";
               parm.guest[32] = "";
               parm.guest[33] = "";
               parm.guest[34] = "";
               parm.guest[35] = "";
               */

            } else if (parm.course.equals( "Fox Hill CC" )) {

               parm.MAX_Guests = 12;
               parm.guest[0] = rs.getString("guest25");
               parm.guest[1] = rs.getString("guest26");
               parm.guest[2] = rs.getString("guest27");
               parm.guest[3] = rs.getString("guest28");
               parm.guest[4] = rs.getString("guest29");
               parm.guest[5] = rs.getString("guest30");
               parm.guest[6] = rs.getString("guest31");
               parm.guest[7] = rs.getString("guest32");
               parm.guest[8] = rs.getString("guest33");
               parm.guest[9] = rs.getString("guest34");
               parm.guest[10] = rs.getString("guest35");
               parm.guest[11] = rs.getString("guest36");
               parm.guest[12] = "";
               parm.guest[13] = "";
               parm.guest[14] = "";
               parm.guest[15] = "";
               parm.guest[16] = "";
               parm.guest[17] = "";
               parm.guest[18] = "";
               parm.guest[19] = "";
               parm.guest[20] = "";
               parm.guest[21] = "";
               parm.guest[22] = "";
               /*
               parm.guest[23] = "";
               parm.guest[24] = "";
               parm.guest[25] = "";
               parm.guest[26] = "";
               parm.guest[27] = "";
               parm.guest[28] = "";
               parm.guest[29] = "";
               parm.guest[30] = "";
               parm.guest[31] = "";
               parm.guest[32] = "";
               parm.guest[33] = "";
               parm.guest[34] = "";
               parm.guest[35] = "";
               */
            } else {

               parm.MAX_Guests = 13;
               parm.guest[0] = rs.getString("guest1");       // guest 1 - 14 are for Fort Collins
               parm.guest[1] = rs.getString("guest2");
               parm.guest[2] = rs.getString("guest3");
               parm.guest[3] = rs.getString("guest4");
               parm.guest[4] = rs.getString("guest5");
               parm.guest[5] = rs.getString("guest6");
               parm.guest[6] = rs.getString("guest7");
               parm.guest[7] = rs.getString("guest8");
               parm.guest[8] = rs.getString("guest9");
               parm.guest[9] = rs.getString("guest10");
               parm.guest[10] = rs.getString("guest11");
               parm.guest[11] = rs.getString("guest12");
               parm.guest[12] = rs.getString("guest14");    // was 13 changed to 14 to remove from Fort Collins and added to Greeley?
               parm.guest[13] = ""; //rs.getString("guest14");
               parm.guest[14] = "";
               parm.guest[15] = "";
               parm.guest[16] = "";
               parm.guest[17] = "";
               parm.guest[18] = "";
               parm.guest[19] = "";
               parm.guest[20] = "";
               parm.guest[21] = "";
               parm.guest[22] = "";
               /*
               parm.guest[23] = "";
               parm.guest[24] = "";
               parm.guest[25] = "";
               parm.guest[26] = "";
               parm.guest[27] = "";
               parm.guest[28] = "";
               parm.guest[29] = "";
               parm.guest[30] = "";
               parm.guest[31] = "";
               parm.guest[32] = "";
               parm.guest[33] = "";
               parm.guest[34] = "";
               parm.guest[35] = "";
               */
            }

         } else {     // all other clubs


            pstmt = con.prepareStatement("SELECT guest, gOpt, revenue, use_guestdb FROM guest5 WHERE activity_id = ?"); //  LIMIT " + parm.MAX_Guests
            pstmt.clearParameters();
            pstmt.setInt(1, activity_id);
            rs2 = pstmt.executeQuery();

            i = 0; // force reset

            while ( rs2.next() ) {

                parm.guest[i] = rs2.getString("guest");
                parm.gOpt[i] = rs2.getInt("gOpt");
                parm.gRev[i] = rs2.getInt("revenue");
                if (use_guestdb) parm.gDb[i] = rs2.getInt("use_guestdb");       // Only plug in use_guestdb values if system is active

                i++;
            }

            parm.MAX_Guests = i;
            
         }

         parm.mem[0] = rs.getString("mem1");
         parm.mem[1] = rs.getString("mem2");
         parm.mem[2] = rs.getString("mem3");
         parm.mem[3] = rs.getString("mem4");
         parm.mem[4] = rs.getString("mem5");
         parm.mem[5] = rs.getString("mem6");
         parm.mem[6] = rs.getString("mem7");
         parm.mem[7] = rs.getString("mem8");
         parm.mem[8] = rs.getString("mem9");
         parm.mem[9] = rs.getString("mem10");
         parm.mem[10] = rs.getString("mem11");
         parm.mem[11] = rs.getString("mem12");
         parm.mem[12] = rs.getString("mem13");
         parm.mem[13] = rs.getString("mem14");
         parm.mem[14] = rs.getString("mem15");
         parm.mem[15] = rs.getString("mem16");
         parm.mem[16] = rs.getString("mem17");
         parm.mem[17] = rs.getString("mem18");
         parm.mem[18] = rs.getString("mem19");
         parm.mem[19] = rs.getString("mem20");
         parm.mem[20] = rs.getString("mem21");
         parm.mem[21] = rs.getString("mem22");
         parm.mem[22] = rs.getString("mem23");
         parm.mem[23] = rs.getString("mem24");

         // specific to activity
         parm.contact = rs.getString("contact");
         parm.email = rs.getString("email");
         parm.x = rs.getInt("x");
         parm.xhrs = rs.getInt("xhrs");
         parm.rnds = rs.getInt("rndsperday");
         parm.hrsbtwn = rs.getInt("hrsbtwn");
         parm.emailOpt = rs.getInt("emailOpt");
         parm.unacompGuest = rs.getInt("unacompGuest");
         parm.forceg = rs.getInt("forcegnames");
         parm.hndcpProSheet = rs.getInt("hndcpProSheet");
         parm.hndcpMemSheet = rs.getInt("hndcpMemSheet");
         parm.hndcpProEvent = rs.getInt("hndcpProEvent");
         parm.hndcpMemEvent = rs.getInt("hndcpMemEvent");

         // general club settings
         parm.adv_zone = rs.getString("adv_zone");
         parm.lottid = rs.getLong("lottid");
         parm.hotel = rs.getInt("hotel");
         parm.userlock = rs.getInt("userlock");
         parm.posType = rs.getString("posType");
         parm.hiden = rs.getInt("hidenames");
         parm.constimesm = rs.getInt("constimesm");
         parm.constimesp = rs.getInt("constimesp");
         parm.precheckin = rs.getInt("precheckin");
         parm.paceofplay = rs.getInt("paceofplay");
         parm.no_reservations = rs.getInt("no_reservations");
         parm.max_originations = rs.getInt("max_originations");
         parm.cutoffdays = rs.getInt("cutoffDays");
         parm.cutofftime = rs.getInt("cutoffTime");
         parm.default_course_mem = rs.getString("default_course_mem");
         parm.default_course_pro = rs.getString("default_course_pro");

         parm.foretees_mode = rs.getInt("foretees_mode");
         parm.genrez_mode = rs.getInt("genrez_mode");

      }


      pstmt = con.prepareStatement (
              "SELECT * FROM mship5 WHERE activity_id = ? LIMIT " + parm.MAX_Mships);

      pstmt.clearParameters();
      pstmt.setInt(1, activity_id);
      rs = pstmt.executeQuery();

      i = 0;  // force reset

      while (rs.next()) {

          parm.mship[i] = rs.getString("mship");
          parm.mtimes[i] = rs.getInt("mtimes");
          parm.period[i] = rs.getString("period");
          parm.days1[i] = rs.getInt("days1");
          parm.days2[i] = rs.getInt("days2");
          parm.days3[i] = rs.getInt("days3");
          parm.days4[i] = rs.getInt("days4");
          parm.days5[i] = rs.getInt("days5");
          parm.days6[i] = rs.getInt("days6");
          parm.days7[i] = rs.getInt("days7");
          parm.advhrd1[i] = rs.getInt("advhrd1");
          parm.advmind1[i] = rs.getInt("advmind1");
          parm.advamd1[i] = rs.getString("advamd1");
          parm.advhrd2[i] = rs.getInt("advhrd2");
          parm.advmind2[i] = rs.getInt("advmind2");
          parm.advamd2[i] = rs.getString("advamd2");
          parm.advhrd3[i] = rs.getInt("advhrd3");
          parm.advmind3[i] = rs.getInt("advmind3");
          parm.advamd3[i] = rs.getString("advamd3");
          parm.advhrd4[i] = rs.getInt("advhrd4");
          parm.advmind4[i] = rs.getInt("advmind4");
          parm.advamd4[i] = rs.getString("advamd4");
          parm.advhrd5[i] = rs.getInt("advhrd5");
          parm.advmind5[i] = rs.getInt("advmind5");
          parm.advamd5[i] = rs.getString("advamd5");
          parm.advhrd6[i] = rs.getInt("advhrd6");
          parm.advmind6[i] = rs.getInt("advmind6");
          parm.advamd6[i] = rs.getString("advamd6");
          parm.advhrd7[i] = rs.getInt("advhrd7");
          parm.advmind7[i] = rs.getInt("advmind7");
          parm.advamd7[i] = rs.getString("advamd7");
          parm.viewdays[i] = rs.getInt("viewdays");

          i++;
      }

   } catch (Exception e) {

      throw new Exception("Error getting guest info - getClub.getParms, Error=" + e.getMessage());

   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { rs2.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

   }

 }


/**
 //************************************************************************
 //
 //  Get the club and course POS parms
 //
 //************************************************************************
 **/
 public static void getPOS(Connection con, parmPOS parm, String course)
         throws Exception {

     getPOS(con, parm, course, 0);
 }

 public static void getPOS(Connection con, parmPOS parm, String course, int activity_id)
         throws Exception {
  
   Statement stmt1 = null;
   ResultSet rs = null;
   PreparedStatement pstmt1 = null;

   int i = 0;
     

   //
   //  get the club's parameters
   //
   try {

      stmt1 = con.createStatement();        // create a statement

      rs = stmt1.executeQuery("SELECT * FROM club5");

      if (rs.next()) {
         parm.posType = rs.getString("posType");
         parm.pos_paynow = rs.getInt("pos_paynow");
      }
      stmt1.close();
           
      //
      // Now get the guest parms for each guest type specified (different table)
      //
      pstmt1 = con.prepareStatement (
              "SELECT * FROM guest5 WHERE activity_id = ? LIMIT " + parm.MAX_Guests);

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setInt(1, activity_id);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      i = 0;

      while (rs.next()) {

          parm.gtype[i] = rs.getString("guest");
          parm.gpos[i] = rs.getString("gpos");
          parm.g9pos[i] = rs.getString("g9pos");
          parm.gstI[i] = rs.getString("gstItem");
          parm.gst9I[i] = rs.getString("gst9Item");

          i++;
      }
      pstmt1.close();
  
      //
      // Now get the mship parms for each mship type specified (different table)
      //
      pstmt1 = con.prepareStatement (
              "SELECT * FROM mship5 WHERE activity_id = ? LIMIT " + parm.MAX_Mships);

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setInt(1, activity_id);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      i = 0;

      while (rs.next()) {

          parm.mship[i] = rs.getString("mship");
          parm.mpos[i] = rs.getString("mpos");
          parm.mposc[i] = rs.getString("mposc");
          parm.m9posc[i] = rs.getString("m9posc");
          parm.mshipI[i] = rs.getString("mshipItem");
          parm.mship9I[i] = rs.getString("mship9Item");

          i++;
      }
      pstmt1.close();
  
      //
      //  Get the course parms
      //
      PreparedStatement pstmtc = null;

      if (course.equals("")) {               // if no course provded
         
         pstmtc = con.prepareStatement (
            "SELECT * " +
            "FROM clubparm2");               // just grab first course if multi and no course requested

         pstmtc.clearParameters();       
         
      } else {
         
         pstmtc = con.prepareStatement (
            "SELECT * " +
            "FROM clubparm2 WHERE courseName = ?");

         pstmtc.clearParameters();       
         pstmtc.setString(1, course);
      }
      
      rs = pstmtc.executeQuery();      // execute the prepared stmt

      if (rs.next()) {                  

         parm.tmode[0] = rs.getString("tmode1");
         parm.tmodea[0] = rs.getString("tmodea1");
         parm.tmode[1] = rs.getString("tmode2");
         parm.tmodea[1] = rs.getString("tmodea2");
         parm.tmode[2] = rs.getString("tmode3");
         parm.tmodea[2] = rs.getString("tmodea3");
         parm.tmode[3] = rs.getString("tmode4");
         parm.tmodea[3] = rs.getString("tmodea4");
         parm.tmode[4] = rs.getString("tmode5");
         parm.tmodea[4] = rs.getString("tmodea5");
         parm.tmode[5] = rs.getString("tmode6");
         parm.tmodea[5] = rs.getString("tmodea6");
         parm.tmode[6] = rs.getString("tmode7");
         parm.tmodea[6] = rs.getString("tmodea7");
         parm.tmode[7] = rs.getString("tmode8");
         parm.tmodea[7] = rs.getString("tmodea8");
         parm.tmode[8] = rs.getString("tmode9");
         parm.tmodea[8] = rs.getString("tmodea9");
         parm.tmode[9] = rs.getString("tmode10");
         parm.tmodea[9] = rs.getString("tmodea10");
         parm.tmode[10] = rs.getString("tmode11");
         parm.tmodea[10] = rs.getString("tmodea11");
         parm.tmode[11] = rs.getString("tmode12");
         parm.tmodea[11] = rs.getString("tmodea12");
         parm.tmode[12] = rs.getString("tmode13");
         parm.tmodea[12] = rs.getString("tmodea13");
         parm.tmode[13] = rs.getString("tmode14");
         parm.tmodea[13] = rs.getString("tmodea14");
         parm.tmode[14] = rs.getString("tmode15");
         parm.tmodea[14] = rs.getString("tmodea15");
         parm.tmode[15] = rs.getString("tmode16");
         parm.tmodea[15] = rs.getString("tmodea16");
         parm.t9pos[0] = rs.getString("t9pos1");
         parm.tpos[0] = rs.getString("tpos1");
         parm.t9pos[1] = rs.getString("t9pos2");
         parm.tpos[1] = rs.getString("tpos2");
         parm.t9pos[2] = rs.getString("t9pos3");
         parm.tpos[2] = rs.getString("tpos3");
         parm.t9pos[3] = rs.getString("t9pos4");
         parm.tpos[3] = rs.getString("tpos4");
         parm.t9pos[4] = rs.getString("t9pos5");
         parm.tpos[4] = rs.getString("tpos5");
         parm.t9pos[5] = rs.getString("t9pos6");
         parm.tpos[5] = rs.getString("tpos6");
         parm.t9pos[6] = rs.getString("t9pos7");
         parm.tpos[6] = rs.getString("tpos7");
         parm.t9pos[7] = rs.getString("t9pos8");
         parm.tpos[7] = rs.getString("tpos8");
         parm.t9pos[8] = rs.getString("t9pos9");
         parm.tpos[8] = rs.getString("tpos9");
         parm.t9pos[9] = rs.getString("t9pos10");
         parm.tpos[9] = rs.getString("tpos10");
         parm.t9pos[10] = rs.getString("t9pos11");
         parm.tpos[10] = rs.getString("tpos11");
         parm.t9pos[11] = rs.getString("t9pos12");
         parm.tpos[11] = rs.getString("tpos12");
         parm.t9pos[12] = rs.getString("t9pos13");
         parm.tpos[12] = rs.getString("tpos13");
         parm.t9pos[13] = rs.getString("t9pos14");
         parm.tpos[13] = rs.getString("tpos14");
         parm.t9pos[14] = rs.getString("t9pos15");
         parm.tpos[14] = rs.getString("tpos15");
         parm.t9pos[15] = rs.getString("t9pos16");
         parm.tpos[15] = rs.getString("tpos16");
         parm.courseid = rs.getString("courseid");
         parm.t9posc[0] = rs.getString("t9posc1");
         parm.tposc[0] = rs.getString("tposc1");
         parm.t9posc[1] = rs.getString("t9posc2");
         parm.tposc[1] = rs.getString("tposc2");
         parm.t9posc[2] = rs.getString("t9posc3");
         parm.tposc[2] = rs.getString("tposc3");
         parm.t9posc[3] = rs.getString("t9posc4");
         parm.tposc[3] = rs.getString("tposc4");
         parm.t9posc[4] = rs.getString("t9posc5");
         parm.tposc[4] = rs.getString("tposc5");
         parm.t9posc[5] = rs.getString("t9posc6");
         parm.tposc[5] = rs.getString("tposc6");
         parm.t9posc[6] = rs.getString("t9posc7");
         parm.tposc[6] = rs.getString("tposc7");
         parm.t9posc[7] = rs.getString("t9posc8");
         parm.tposc[7] = rs.getString("tposc8");
         parm.t9posc[8] = rs.getString("t9posc9");
         parm.tposc[8] = rs.getString("tposc9");
         parm.t9posc[9] = rs.getString("t9posc10");
         parm.tposc[9] = rs.getString("tposc10");
         parm.t9posc[10] = rs.getString("t9posc11");
         parm.tposc[10] = rs.getString("tposc11");
         parm.t9posc[11] = rs.getString("t9posc12");
         parm.tposc[11] = rs.getString("tposc12");
         parm.t9posc[12] = rs.getString("t9posc13");
         parm.tposc[12] = rs.getString("tposc13");
         parm.t9posc[13] = rs.getString("t9posc14");
         parm.tposc[13] = rs.getString("tposc14");
         parm.t9posc[14] = rs.getString("t9posc15");
         parm.tposc[14] = rs.getString("tposc15");
         parm.t9posc[15] = rs.getString("t9posc16");
         parm.tposc[15] = rs.getString("tposc16");
      }
      pstmtc.close();

   }
   catch (Exception e) {

      throw new Exception("Error getting guest info - getClub.getPOS " + e.getMessage());
   }

 }


/**
 //************************************************************************
 //
 //  Get the Membership and Member Types Only
 // 
 //************************************************************************
 **/

 public static mTypeArrays getMtypes(mTypeArrays mArrays, Connection con)
         throws Exception {

     getMtypes(mArrays, 0, con);

     return mArrays;
 }

 public static mTypeArrays getMtypes(mTypeArrays mArrays, int activity_id, Connection con)
         throws Exception {


   Statement stmt1 = null;
   PreparedStatement pstmt1 = null;
   ResultSet rs = null;

   int i = 0;

   // init the mship array to eliminate nulls
   for (i = 0; i < Labels.MAX_MSHIPS; i++) {

     mArrays.mship[i] = "";
   }
   
   //
   //  get the club's parameters
   //
   try {

      stmt1 = con.createStatement();        // create a statement

      rs = stmt1.executeQuery("SELECT * FROM club5");

      if (rs.next()) {

         mArrays.mem[0] = rs.getString("mem1");
         mArrays.mem[1] = rs.getString("mem2");
         mArrays.mem[2] = rs.getString("mem3");
         mArrays.mem[3] = rs.getString("mem4");
         mArrays.mem[4] = rs.getString("mem5");
         mArrays.mem[5] = rs.getString("mem6");
         mArrays.mem[6] = rs.getString("mem7");
         mArrays.mem[7] = rs.getString("mem8");
         mArrays.mem[8] = rs.getString("mem9");
         mArrays.mem[9] = rs.getString("mem10");
         mArrays.mem[10] = rs.getString("mem11");
         mArrays.mem[11] = rs.getString("mem12");
         mArrays.mem[12] = rs.getString("mem13");
         mArrays.mem[13] = rs.getString("mem14");
         mArrays.mem[14] = rs.getString("mem15");
         mArrays.mem[15] = rs.getString("mem16");
         mArrays.mem[16] = rs.getString("mem17");
         mArrays.mem[17] = rs.getString("mem18");
         mArrays.mem[18] = rs.getString("mem19");
         mArrays.mem[19] = rs.getString("mem20");
         mArrays.mem[20] = rs.getString("mem21");
         mArrays.mem[21] = rs.getString("mem22");
         mArrays.mem[22] = rs.getString("mem23");
         mArrays.mem[23] = rs.getString("mem24");
      }


      //
      // Now get the mship parms for each mship type specified (different table)
      //
      pstmt1 = con.prepareStatement (
              "SELECT mship FROM mship5 WHERE activity_id = ? LIMIT " + Labels.MAX_MSHIPS);

      pstmt1.clearParameters();
      pstmt1.setInt(1, activity_id);
      rs = pstmt1.executeQuery();

      i = 0;

      while (rs.next()) {

          mArrays.mship[i] = rs.getString("mship");
          i++;
      }

   } catch (Exception e) {

      throw new Exception("Error getting guest info - getClub.getMtypes " + e.getMessage());

   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt1.close(); }
        catch (Exception ignore) {}

        try { pstmt1.close(); }
        catch (Exception ignore) {}

   }

   return(mArrays);
 }


/**
 //************************************************************************
 //
 //  Get the club's replacement text for the word 'Lottery' 
 //
 //   This is for clubs that do not like the word 'lottery'
 //
 //************************************************************************
 **/

 public static String getLotteryText(Connection con) {


   Statement stmt2 = null;
   ResultSet rs = null;
   
   String text = "";
   
   try {

      stmt2 = con.createStatement();              // create a statement

      rs = stmt2.executeQuery("SELECT lottery_text FROM club5");

      if (rs.next()) {

         text = rs.getString(1);        
      }
      stmt2.close();
       
   }
   catch (Exception e) {

      verifySlot.logError("Error getting lottery text - getClub.getLotteryText " + e.getMessage());   // log the error message
      text = "";
   }

   return(text);
 }
   
   
}  // end of getClub class

