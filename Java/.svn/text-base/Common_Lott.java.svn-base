/***************************************************************************************
 *   Common_Lott:  This servlet will process common Lottery functions.
 *
 *
 *
 *   created: 7/12/2006   Bob P.
 *
 *   last updated:       ******* keep this accurate *******
 *
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
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

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



 // *********************************************************
 //
 //  Process member restrictions for lottery request.
 //
 //
 //     Called by:  Member_lott (calls individual methods below)
 //                 Proshop_lott (calls individual methods below)
 //                 Proshop_mlottery
 //                 SystemUtils
 //
 //
 // *********************************************************

 public static boolean checkRests(int skip, parmLott parm, Connection con) {


   //Statement stmt = null;
   //ResultSet rs = null;

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

      if (!parm.player1.equals( "" ) && parm.g[0].equals( "" )) {

         player = parm.player1;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player2.equals( "" ) && parm.g[1].equals( "" ) && hit == 0) {

         player = parm.player2;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player3.equals( "" ) && parm.g[2].equals( "" ) && hit == 0) {

         player = parm.player3;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player4.equals( "" ) && parm.g[3].equals( "" ) && hit == 0) {

         player = parm.player4;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player5.equals( "" ) && parm.g[4].equals( "" ) && hit == 0) {

         player = parm.player5;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player6.equals( "" ) && parm.g[5].equals( "" ) && hit == 0) {

         player = parm.player6;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player7.equals( "" ) && parm.g[6].equals( "" ) && hit == 0) {

         player = parm.player7;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player8.equals( "" ) && parm.g[7].equals( "" ) && hit == 0) {

         player = parm.player8;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player9.equals( "" ) && parm.g[8].equals( "" ) && hit == 0) {

         player = parm.player9;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player10.equals( "" ) && parm.g[9].equals( "" ) && hit == 0) {

         player = parm.player10;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player11.equals( "" ) && parm.g[10].equals( "" ) && hit == 0) {

         player = parm.player11;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player12.equals( "" ) && parm.g[11].equals( "" ) && hit == 0) {

         player = parm.player12;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player13.equals( "" ) && parm.g[12].equals( "" ) && hit == 0) {

         player = parm.player13;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player14.equals( "" ) && parm.g[13].equals( "" ) && hit == 0) {

         player = parm.player14;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player15.equals( "" ) && parm.g[14].equals( "" ) && hit == 0) {

         player = parm.player15;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player16.equals( "" ) && parm.g[15].equals( "" ) && hit == 0) {

         player = parm.player16;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player17.equals( "" ) && parm.g[16].equals( "" ) && hit == 0) {

         player = parm.player17;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player18.equals( "" ) && parm.g[17].equals( "" ) && hit == 0) {

         player = parm.player18;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player19.equals( "" ) && parm.g[18].equals( "" ) && hit == 0) {

         player = parm.player19;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player20.equals( "" ) && parm.g[19].equals( "" ) && hit == 0) {

         player = parm.player20;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player21.equals( "" ) && parm.g[20].equals( "" ) && hit == 0) {

         player = parm.player21;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player22.equals( "" ) && parm.g[21].equals( "" ) && hit == 0) {

         player = parm.player22;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player23.equals( "" ) && parm.g[22].equals( "" ) && hit == 0) {

         player = parm.player23;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player24.equals( "" ) && parm.g[23].equals( "" ) && hit == 0) {

         player = parm.player24;              // get player for message

         hit = chkPlayer(con, player, date, time, fb, course, lottid);
      }

      if (!parm.player25.equals( "" ) && parm.g[24].equals( "" ) && hit == 0) {

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
   if (skip < 8) {

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

         //
         //  Check if course matches that specified in restriction
         //
         if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( parm.course ))) {

            
            //
            // Make sure restriction isn't suspended
            //
            if (!verifySlot.checkRestSuspend(-99, grest_id, (int)parm.date, parm.time, parm.day, parm.course, con)) {

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
               if (!verifySlot.checkRestSuspend(mrest_id, -99, (int)parm.date, parm.time, parm.day, parm.course, con)) {

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
      if (!playerA[i2].equals( "" )) {

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
      String errorMsg = "Error in SystemUtils getParmValues: ";
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

      throw new UnavailableException("Error Checking Mnums for Guest Rest - Member_lott: " + e.getMessage());
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

      throw new UnavailableException("Error Counting Guests for Guest Rest - Member_lott: " + e.getMessage());
   }

   return(guests);
 }                      // end of countGuests


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
