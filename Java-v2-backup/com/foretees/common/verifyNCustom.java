/***************************************************************************************
 *   verifyCustom:  This servlet will provide some Custom tee time request processing methods.
 *
 *       called by:  ProshopTLT_slot
 *                   MemberTLT_slot
 *
 *
 *   created:  4/13/2007   For Notification Systems
 *
 *
 *   last updated:
 *
 *      3/27/10  Winged Foot - add changes to guest report for 2010 (case 1096).
 *      9/04/09  Added minor db cleanup code
 *      7/28/09  Winged Foot (wingedfoot) - Added additional guest quotas
 *      7/17/09  Added checkWFNotifications custom to determine if any member in a submitted notification 
 *               already has 2 or more notifications originated by them that are waiting to be processed (case 1666).
 *      6/18/09  Change checkWFGuests method to call out to Utilities.checkWingedFootGuestTypes() for all wingedfoot guest name checks.
 *      5/26/09  Add 'wfgc guest day' guest type to checkWFGuests for 2009 (case 1096).
 *      3/31/09  Correct the bookend dates and guest types in checkWFGuests for 2009 (case 1096).
 *      8/11/08  Correct the end date in checkWFGuests.
 *      6/18/08  Fix checkWFguestsHour - plus add debug code, change default to fail
 *      5/07/08  In checkWFguests for Winged Foot - send email to WF if quota within 6 guests (case #1096).
 *      4/22/08  Update checkWFguestsHour - change to 9 guests per hour from x:30 to y:29 (case 1419).
 *      4/12/08  Add checkWFguestsHour for Winged Foot - check total number of guests per hour (case 1419).
 *      8/20/07  In checkWFguests for Winged Foot - send email to WF if quota within 3 guests (case #1096).
 *      4/13/07  Add checkWFLguests for Winged Foot - check Legacy guest times (case #1097).
 *      4/13/07  Add checkWFlegacy for Winged Foot - check Legacy mship times (case #1097).
 *      4/13/07  Add checkWFguests for Winged Foot - check guest quotas (case #1096).
 *
 ***************************************************************************************
 */


package com.foretees.common;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;


// foretees imports
import com.foretees.common.Utilities;


public class verifyNCustom {

   private static String rev = ProcessConstants.REV;

   //
   //  Holidays for custom codes that may require them
   //
   //   Must change them in ProcessConstants...
   //     also, refer to SystemUtils !!!!!!!!!
   //
   private static long Hdate1 = ProcessConstants.memDay;     // Memorial Day
   private static long Hdate2 = ProcessConstants.july4;      // 4th of July - Monday
   private static long Hdate2b = ProcessConstants.july4b;    // 4th of July - other
   private static long Hdate3 = ProcessConstants.laborDay;   // Labor Day
   private static long Hdate7 = ProcessConstants.tgDay;      // Thanksgiving Day
   private static long Hdate8 = ProcessConstants.colDay;     // Columbus Day
     
   private static long Hdate4 = ProcessConstants.Hdate4;     // October 1st
   private static long Hdate5 = ProcessConstants.Hdate5;     // Junior Fridays Start (start on Thurs.)
   private static long Hdate6 = ProcessConstants.Hdate6;     // Junior Fridays End  (end on Sat.)
     



/**
 //************************************************************************
 //
 //  Winged Foot - special Guest processing.  (Notification System)
 //
 //     Restrictions:
 //
 //       The number of guests allowed per family viaries on the membership type
 //       and the time of year.
 //
 //          Regular Family - 24 guests per Golf Year
 //          Regular & Regualr Senior - 20 guests per Golf Year
 //          all others - 12 guests per Golf Year
 //
 //          Golf Year = 5/01 - 10/31
 //          Off Season = 11/01 - 4/30 (no limits)
 //
 //
 //      NOTE:  do not include guest types of Son/Daughter, Event Guest or comp
 //
 //************************************************************************
 **/

 public static boolean checkWFguests(parmNSlot slotParms, Connection con) {


   PreparedStatement pstmt = null;
   PreparedStatement pstmt4 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   boolean error = false;

   int countg = 0;         // number of guests for month
   int counts = 0;         // number of guests for season
   int maxs = 0;
   int maxg = 0;

   String user = "";
   String mNum = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";

   //
   //  break down date of tee time
   //
   long yy = slotParms.date / 10000;         // get year

   long sdate = (yy * 10000) + 501;          // yyyy0501     Golf Year bookends for 2010
   long edate = (yy * 10000) + 1031;         // yyyy1031

   int i = 0;


   String [] usergA = new String [5];       // array to hold the members' usernames
   String [] userA = new String [5];        // array to hold the usernames
   String [] playerA = new String [5];      // array to hold the player's names
   String [] mnumA = new String [5];        // array to hold the players' member numbers
   String [] mshipA = new String [5];       // array to hold the players' membership types

//   int [] countsA = new int [5];       // temp - for debug
//   int [] countgA = new int [5];       // temp - for debug

   playerA[0] = slotParms.player1;
   playerA[1] = slotParms.player2;
   playerA[2] = slotParms.player3;
   playerA[3] = slotParms.player4;
   playerA[4] = slotParms.player5;

   userA[0] = slotParms.user1;
   userA[1] = slotParms.user2;
   userA[2] = slotParms.user3;
   userA[3] = slotParms.user4;
   userA[4] = slotParms.user5;

   usergA[0] = slotParms.userg1;                  // copy userg values into array
   usergA[1] = slotParms.userg2;
   usergA[2] = slotParms.userg3;
   usergA[3] = slotParms.userg4;
   usergA[4] = slotParms.userg5;

   mnumA[0] = slotParms.mNum1;
   mnumA[1] = slotParms.mNum2;
   mnumA[2] = slotParms.mNum3;
   mnumA[3] = slotParms.mNum4;
   mnumA[4] = slotParms.mNum5;

   mshipA[0] = slotParms.mship1;
   mshipA[1] = slotParms.mship2;
   mshipA[2] = slotParms.mship3;
   mshipA[3] = slotParms.mship4;
   mshipA[4] = slotParms.mship5;


   //
   //  Only check quota if tee time is within the Golf Year
   //
   if (slotParms.date >= sdate && slotParms.date <= edate) {

      //
      //  Remove any duplicate family members - only check one user for the family
      //
      if (!mnumA[0].equals( "" )) {        // if mnum exists

         if (mnumA[1].equals( mnumA[0] )) {        // if mnum is the same

            mnumA[1] = "";
            userA[1] = "";
         }
         if (mnumA[2].equals( mnumA[0] )) {        // if mnum is the same

            mnumA[2] = "";
            userA[2] = "";
         }
         if (mnumA[3].equals( mnumA[0] )) {        // if mnum is the same

            mnumA[3] = "";
            userA[3] = "";
         }
         if (mnumA[4].equals( mnumA[0] )) {        // if mnum is the same

            mnumA[4] = "";
            userA[4] = "";
         }
      }

      if (!mnumA[1].equals( "" )) {        // if mnum exists

         if (mnumA[2].equals( mnumA[1] )) {        // if mnum is the same

            mnumA[2] = "";
            userA[2] = "";
         }
         if (mnumA[3].equals( mnumA[1] )) {        // if mnum is the same

            mnumA[3] = "";
            userA[3] = "";
         }
         if (mnumA[4].equals( mnumA[1] )) {        // if mnum is the same

            mnumA[4] = "";
            userA[4] = "";
         }
      }

      if (!mnumA[2].equals( "" )) {        // if mnum exists

         if (mnumA[3].equals( mnumA[2] )) {        // if mnum is the same

            mnumA[3] = "";
            userA[3] = "";
         }
         if (mnumA[4].equals( mnumA[2] )) {        // if mnum is the same

            mnumA[4] = "";
            userA[4] = "";
         }
      }

      if (!mnumA[3].equals( "" )) {        // if mnum exists

         if (mnumA[4].equals( mnumA[3] )) {        // if mnum is the same

            mnumA[4] = "";
            userA[4] = "";
         }
      }


      try {

         //
         //  Check each player
         //
         loop1:
         for (i = 0; i < 5; i++) {

            if (!userA[i].equals( "" )) {       // if member

               countg = 0;

               //
               //  count # of guests for this user in this tee time
               //
               if (usergA[0].equals( userA[i] ) && Utilities.checkWingedFootGuestTypes(playerA[0], mshipA[i])) {

                  countg++;           // count # of guests
               }
               if (usergA[1].equals( userA[i] ) && Utilities.checkWingedFootGuestTypes(playerA[1], mshipA[i])) {

                  countg++;           // count # of guests
               }
               if (usergA[2].equals( userA[i] ) && Utilities.checkWingedFootGuestTypes(playerA[2], mshipA[i])) {

                  countg++;           // count # of guests
               }
               if (usergA[3].equals( userA[i] ) && Utilities.checkWingedFootGuestTypes(playerA[3], mshipA[i])) {

                  countg++;           // count # of guests
               }
               if (usergA[4].equals( userA[i] ) && Utilities.checkWingedFootGuestTypes(playerA[4], mshipA[i])) {

                  countg++;           // count # of guests
               }

               if (countg > 0) {

                  //  get this user's mNum

                  mNum = mnumA[i];

                  if (!mNum.equals( "" )) {     // if there is one specified

                     //
                     //  get all users with matching mNum
                     //
                     pstmt4 = con.prepareStatement (
                        "SELECT username FROM member2b WHERE memNum = ?");

                     pstmt4.clearParameters();        // clear the parms
                     pstmt4.setString(1, mNum);
                     rs2 = pstmt4.executeQuery();      // execute the prepared stmt

                     while (rs2.next()) {

                        user = rs2.getString(1);       // get the username

                        //
                        //   Check teecurr and teepast for other guest times for this member for the Golf Year
                        //
                        pstmt = con.prepareStatement (
                           "SELECT player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5 " +
                           "FROM teepast2 " +
                           "WHERE date >= ? AND date <= ? AND " +
                           "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

                        pstmt.clearParameters();
                        pstmt.setLong(1, sdate);          
                        pstmt.setLong(2, edate);         
                        pstmt.setString(3, user);
                        pstmt.setString(4, user);
                        pstmt.setString(5, user);
                        pstmt.setString(6, user);
                        pstmt.setString(7, user);
                        rs = pstmt.executeQuery();

                        while (rs.next()) {

                           player1 = rs.getString(1);
                           player2 = rs.getString(2);
                           player3 = rs.getString(3);
                           player4 = rs.getString(4);
                           player5 = rs.getString(5);
                           userg1 = rs.getString(6);
                           userg2 = rs.getString(7);
                           userg3 = rs.getString(8);
                           userg4 = rs.getString(9);
                           userg5 = rs.getString(10);

                           if (userg1.equals( user ) && Utilities.checkWingedFootGuestTypes(player1, mshipA[i])) {

                              countg++;     // bump # of guests
                           }
                           if (userg2.equals( user ) && Utilities.checkWingedFootGuestTypes(player2, mshipA[i])) {

                              countg++;     // bump # of guests
                           }
                           if (userg3.equals( user ) && Utilities.checkWingedFootGuestTypes(player3, mshipA[i])) {

                              countg++;     // bump # of guests
                           }
                           if (userg4.equals( user ) && Utilities.checkWingedFootGuestTypes(player4, mshipA[i])) {

                              countg++;     // bump # of guests
                           }
                           if (userg5.equals( user ) && Utilities.checkWingedFootGuestTypes(player5, mshipA[i])) {

                              countg++;     // bump # of guests
                           }
                        }      // end of WHILE

                        pstmt.close();

                        pstmt = con.prepareStatement (
                           "SELECT player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5 " +
                           "FROM teecurr2 " +
                           "WHERE date >= ? AND date <= ? AND date != ? AND time != ? AND " +
                           "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

                        pstmt.clearParameters();
                        pstmt.setLong(1, sdate);               
                        pstmt.setLong(2, edate);              
                        pstmt.setLong(3, slotParms.date);        // not this tee time
                        pstmt.setInt(4, slotParms.time);
                        pstmt.setString(5, user);
                        pstmt.setString(6, user);
                        pstmt.setString(7, user);
                        pstmt.setString(8, user);
                        pstmt.setString(9, user);
                        rs = pstmt.executeQuery();

                        while (rs.next()) {

                           player1 = rs.getString(1);
                           player2 = rs.getString(2);
                           player3 = rs.getString(3);
                           player4 = rs.getString(4);
                           player5 = rs.getString(5);
                           userg1 = rs.getString(6);
                           userg2 = rs.getString(7);
                           userg3 = rs.getString(8);
                           userg4 = rs.getString(9);
                           userg5 = rs.getString(10);

                           if (userg1.equals( user ) && Utilities.checkWingedFootGuestTypes(player1, mshipA[i])) {

                              countg++;     // bump # of guests
                           }
                           if (userg2.equals( user ) && Utilities.checkWingedFootGuestTypes(player2, mshipA[i])) {

                              countg++;     // bump # of guests
                           }
                           if (userg3.equals( user ) && Utilities.checkWingedFootGuestTypes(player3, mshipA[i])) {

                              countg++;     // bump # of guests
                           }
                           if (userg4.equals( user ) && Utilities.checkWingedFootGuestTypes(player4, mshipA[i])) {

                              countg++;     // bump # of guests
                           }
                           if (userg5.equals( user ) && Utilities.checkWingedFootGuestTypes(player5, mshipA[i])) {

                              countg++;     // bump # of guests
                           }
                        }      // end of WHILE

                        pstmt.close();

                     }
                     pstmt4.close();

                     //
                     //  Check counts based on mship type for this family
                     //
                     maxs = 8;        // defaults
                     maxg = 12;

                     if (mshipA[i].equals("Regular Family") || mshipA[i].equals("Senior Family")) {
                         
                         maxs = 20;
                         maxg = 24;
                         
                     } else if (mshipA[i].equals("Regular") || mshipA[i].equals("Regular Senior")
                             || mshipA[i].equals("Junior C Family") || mshipA[i].equals("Junior Family D")) {
                         
                         maxs = 14;
                         maxg = 20;
                         
                     } else if (mshipA[i].equals("Junior B")) {
                         
                         maxs = 10;
                         maxg = 15;
                         
                     } else if (mshipA[i].equals("Junior C") || mshipA[i].equals("Junior D")) {
                         
                         maxs = 12;
                         maxg = 18;
                         
                     } else if (mshipA[i].equals("Family Playing Privileges")) {
                         
                         maxg = 9;
                     }

                     //
                     //  Too many guests???
                     //
                     if (countg > maxg) {          // if guest count puts user over the limit

                        error = true;                          // indicate error
                        slotParms.player = playerA[i];         // save player name for error message
                        break loop1;
                     }


                     //
                     //  Send an email to club if membership has 6 or less guests remaining in quota
                     //
                     int quotaspace = 0;

                     quotaspace = maxg - countg;       // number of guests remaining in yearly quota

                     if (quotaspace < 7) {

                        sendEmail.sendWFemail(quotaspace, maxg, playerA[i], "year");    // go send an email to club
                     }
                  }
               }
               
               // save for debug
            //   countsA[i] = counts;
            //   countgA[i] = countg;
               
            }
         }              // end of FOR loop (do each player)

      } catch (Exception e) {

          Utilities.logError("Error checking for Winged Foot guests - verifyCustom.checkWFguests " + e.getMessage());
      
      } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { rs2.close(); }
          catch (Exception ignore) {}

          try { pstmt.close(); }
          catch (Exception ignore) {}

          try { pstmt4.close(); }
          catch (Exception ignore) {}

      }
  
   }

   // errorMsg = "DEBUG - Winged Foot checkWFguests: date = " + slotParms.date + ", sdate = " + sdate + ", edate = " + edate + ", counts1 = " + countsA[0] + ", counts2 = " + countsA[1] + ", counts3 = " + countsA[2] + ", counts4 = " + countsA[3] + ", counts5 = " + countsA[4] + ", countg1 = " + countgA[0] + ", countg2 = " + countgA[1] + ", countg3 = " + countgA[2] + ", countg4 = " + countgA[3] + ", countg5 = " + countgA[4] + ".";
   // Utilities.logError(errorMsg);        // log the error message
      
   return(error);

 }



/**
 //************************************************************************
 //
 //  Winged Foot - special Guest processing.  (Notification System)
 //
 //     Restrictions:
 //
 //       There can only be up to 9 guests total between x:30 and x+1:29 each hour of the day.
 //
 //************************************************************************
 **/

 public static boolean checkWFguestsHour(parmNSlot slotParms, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean error = true;

   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";

   //
   //  get the hour requested and create start and end times for queries
   //
   int hr = slotParms.time/100;               // get hour
   int min = slotParms.time - (hr * 100);     // get min

   int stime = 0;                             // start time
   int etime = 0;                             // end time 
   
   if (min < 30) {                           // if earlier than x:30 adjust hr for start time
      
      etime = (hr * 100) + 29;               // end time = x:29
      hr--;                                  // back up one hr
      stime = (hr * 100) + 30;               // start time = x:30 (i.e.  8:30 - 9:29)
      
   } else {                                  // x:30 or later - adjust the hr for end time
      
      stime = (hr * 100) + 30;               // start time = x:30
      hr++;
      etime = (hr * 100) + 29;               // end time = x:29      
   }
   
   int countg = 0;              // number of guests for hour (start with this request)

   int tmp1 = slotParms.guests; // in this request
   int tmp2 = 0;                // tee times
   int tmp3 = 0;                // notifications
           
   //
   //  Count the number of guests already scheduled for the requested hour
   //
   try {

      //
      //  Check any tee times during this hour - WF does not allow unaccompanied guests so we should be able to check userg!
      //
      pstmt = con.prepareStatement (
         "SELECT userg1, userg2, userg3, userg4, userg5 " +
         "FROM teecurr2 " +
         "WHERE date = ? AND time >= ? AND time <= ? AND player1 <> ''");

      pstmt.clearParameters();
      pstmt.setLong(1, slotParms.date);        // not this tee time
      pstmt.setInt(2, stime);
      pstmt.setInt(3, etime);
      rs = pstmt.executeQuery();

      while (rs.next()) {

         userg1 = rs.getString(1);
         userg2 = rs.getString(2);
         userg3 = rs.getString(3);
         userg4 = rs.getString(4);
         userg5 = rs.getString(5);

         if (!userg1.equals( "" )) {

            tmp2++;       // bump # of guests
         }
         if (!userg2.equals( "" )) {

            tmp2++;       // bump # of guests
         }
         if (!userg3.equals( "" )) {

            tmp2++;       // bump # of guests
         }
         if (!userg4.equals( "" )) {

            tmp2++;       // bump # of guests
         }
         if (!userg5.equals( "" )) {

            tmp2++;       // bump # of guests
         }
      }      // end of WHILE

      pstmt.close();

      
      //
      //  Now check the notifications for guests (do not check this one again if it exists)
      //
        
      pstmt = con.prepareStatement (
            "SELECT COUNT(*) " + 
            "FROM notifications n, notifications_players np " + 
            "WHERE " + 
             "n.notification_id = np.notification_id AND " + 
             "DATE_FORMAT(n.req_datetime, '%Y%m%d') = ? AND " + 
             "DATE_FORMAT(n.req_datetime, '%H%i') >= ? AND " + 
             "DATE_FORMAT(n.req_datetime, '%H%i') <= ? AND " + 
             "np.username = '' AND " +
             "n.notification_id <> ?;");

      pstmt.clearParameters();
      pstmt.setLong(1, slotParms.date);
      pstmt.setInt(2, stime);
      pstmt.setInt(3, etime);
      pstmt.setInt(4, slotParms.notify_id);
      rs = pstmt.executeQuery();

      if (rs.next()) {
          
          //countg = countg + rs.getInt(1);
          tmp3 = rs.getInt(1);
      }
      
      pstmt.close();
      
      countg = tmp1 + tmp2 + tmp3;
      
      //
      //  Check if this request will exceed the max allowed guests for this time period
      //
      if (countg <= 9) {
         
         error = false;
      }
              

   } catch (Exception e) {

      Utilities.logError("Error checking for Winged Foot guests - verifyCustom.checkWFguestsHour " + e.getMessage());
   
   } finally {

       try { rs.close(); }
       catch (Exception ignore) {}

       try { pstmt.close(); }
       catch (Exception ignore) {}

   }

   //errorMsg = "DEBUG - Winged Foot checkWFguestsHour found " + countg + " guests from " + stime + " to " + etime + " on " + slotParms.date + " (" + tmp1 + ", " + tmp2 + ", " + tmp3 + ").";
   //Utilities.logError(errorMsg);        // log the error message
      
   return(error);

 }


/**
 //************************************************************************
 //
 //  Winged Foot - special Legacy mship processing. (Notification System)
 //
 //     Restrictions:
 //
 //       Legacy Preferred Associates mships can only play 2 rounds per month
 //       on Friday, Saturday and Holidays.
 //
 //   NOTE:  Only primary members can play, so there will be no other family members to check.
 //
 //************************************************************************
 **/

 public static boolean checkWFlegacy(parmNSlot slotParms, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean error = false;

   int count = 0;         
   int rounds = 0;

   String user = "";

   //
   //  break down date of tee time
   //
   long yy = slotParms.date / 10000;                             // get year
   long mm = (slotParms.date - (yy * 10000)) / 100;              // get month
   long dd = (slotParms.date - (yy * 10000)) - (mm * 100);       // get day

   int imm = (int)mm;
   int iyy = (int)yy;
   int i = 0;


   String [] userA = new String [5];        // array to hold the usernames
   String [] playerA = new String [5];      // array to hold the player's names
   String [] mshipA = new String [5];       // array to hold the players' membership types

   playerA[0] = slotParms.player1;
   playerA[1] = slotParms.player2;
   playerA[2] = slotParms.player3;
   playerA[3] = slotParms.player4;
   playerA[4] = slotParms.player5;

   userA[0] = slotParms.user1;
   userA[1] = slotParms.user2;
   userA[2] = slotParms.user3;
   userA[3] = slotParms.user4;
   userA[4] = slotParms.user5;

   mshipA[0] = slotParms.mship1;
   mshipA[1] = slotParms.mship2;
   mshipA[2] = slotParms.mship3;
   mshipA[3] = slotParms.mship4;
   mshipA[4] = slotParms.mship5;


   //
   //  Only check quota if tee time is for a affected day (Fri, Sat, or holiday)
   //
   if (slotParms.date == Hdate1 || slotParms.date == Hdate2b || slotParms.date == Hdate3 || 
       slotParms.day.equals( "Friday" ) || slotParms.day.equals( "Saturday" )) {

      try {

         //
         //  Check each player
         //
         loop1:
         for (i = 0; i < 5; i++) {

            if (!userA[i].equals( "" ) && mshipA[i].equals( "Legacy Preferred Associates" )) {    // if Legacy member

               count = 0;

               user = userA[i];

               //
               //   Check teecurr and teepast for other tee times for this member for this month
               //
               pstmt = con.prepareStatement (
                  "SELECT time " +
                  "FROM teepast2 " +
                  "WHERE mm = ? AND yy = ? AND " +
                  "(date = ? OR date = ? OR date = ? OR day = 'Friday' OR day = 'Saturday') AND " +
                  "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

               pstmt.clearParameters();
               pstmt.setInt(1, imm);
               pstmt.setInt(2, iyy);
               pstmt.setLong(3, Hdate1);
               pstmt.setLong(4, Hdate2b);
               pstmt.setLong(5, Hdate3);
               pstmt.setString(6, user);
               pstmt.setString(7, user);
               pstmt.setString(8, user);
               pstmt.setString(9, user);
               pstmt.setString(10, user);
               rs = pstmt.executeQuery();

               while (rs.next()) {

                  count++;                   // bump # of w/e tee times this month
               }    

               pstmt.close();

               pstmt = con.prepareStatement (
                  "SELECT time " +
                  "FROM teecurr2 " +
                  "WHERE mm = ? AND yy = ? AND date != ? AND time != ? AND " +
                  "(date = ? OR date = ? OR date = ? OR day = 'Friday' OR day = 'Saturday') AND " +
                  "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

               pstmt.clearParameters();
               pstmt.setInt(1, imm);
               pstmt.setInt(2, iyy);
               pstmt.setLong(3, slotParms.date);        // not this tee time
               pstmt.setInt(4, slotParms.time);
               pstmt.setLong(5, Hdate1);
               pstmt.setLong(6, Hdate2b);
               pstmt.setLong(7, Hdate3);
               pstmt.setString(8, user);
               pstmt.setString(9, user);
               pstmt.setString(10, user);
               pstmt.setString(11, user);
               pstmt.setString(12, user);
               rs = pstmt.executeQuery();

               while (rs.next()) {

                  count++;                // bump # of w/e tee times this month
               }    

               pstmt.close();

               //
               //  Now check the Notifications
               //
               pstmt = con.prepareStatement (
                  "SELECT COUNT(*) AS rounds " +
                  "FROM notifications_players np, notifications n " + 
                  "WHERE n.notification_id = np.notification_id AND np.username = ? AND " +
                  "(DATE_FORMAT(n.req_datetime, '%c') = ? AND DATE_FORMAT(n.req_datetime, '%Y') = ?) AND " +
                  "(DATE_FORMAT(n.req_datetime, '%Y%m%d') <> ? AND DATE_FORMAT(n.req_datetime, '%H%i') <> ?) AND " +
                  "(DATE_FORMAT(n.req_datetime, '%Y%m%d') = ? OR " +
                  "DATE_FORMAT(n.req_datetime, '%Y%m%d') = ? OR " +
                  "DATE_FORMAT(n.req_datetime, '%Y%m%d') = ? OR " +
                  "DATE_FORMAT(n.req_datetime, '%w') = 5 OR " +
                  "DATE_FORMAT(n.req_datetime, '%w') = 6) AND n.converted = 0");

               pstmt.clearParameters();
               pstmt.setString(1, user);
               pstmt.setInt(2, imm);
               pstmt.setInt(3, iyy);
               pstmt.setLong(4, slotParms.date);        // not this tee time
               pstmt.setInt(5, slotParms.time);
               pstmt.setLong(6, Hdate1);
               pstmt.setLong(7, Hdate2b);
               pstmt.setLong(8, Hdate3);
                 
               rs = pstmt.executeQuery();

               if (rs.next()) {

                  rounds = rs.getInt("rounds");
                    
                  count += rounds;          // bump # of w/e tee times this month
               }    

               pstmt.close();

               //
               //  Too many tee times this month???
               //
               if (count > 1) {                          // only 2 w/e times allowed (we didn't count this one)

                  error = true;                          // indicate error
                  slotParms.player = playerA[i];         // save player name for error message
                  break loop1;
               }
            }
         }              // end of FOR loop (do each player)

      } catch (Exception e) {

         Utilities.logError("Error checking for Winged Foot Legacy mships - verifyCustom.checkWFlegacy " + e.getMessage());
         
      } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { pstmt.close(); }
         catch (Exception ignore) {}

      }
   }

   return(error);

 }


/**
 //************************************************************************
 //
 //  Winged Foot - special Legacy mship processing. (Notification System)
 //
 //     Restrictions:
 //
 //       Legacy Preferred Associates mships can only have guests before
 //       11:30 AM and after 2:00 PM on Tues, Wed, & Thurs (and not Holidays).   
 //
 //
 //************************************************************************
 **/

 public static boolean checkWFLguests(parmNSlot slotParms, Connection con) {

     
   boolean error = false;

   String user = "";
   String errorMsg = "";

   String [] userA = new String [5];        // array to hold the usernames
   String [] usergA = new String [5];        // array to hold the usernames for guests
   String [] playerA = new String [5];      // array to hold the player's names
   String [] mshipA = new String [5];       // array to hold the players' membership types

   playerA[0] = slotParms.player1;
   playerA[1] = slotParms.player2;
   playerA[2] = slotParms.player3;
   playerA[3] = slotParms.player4;
   playerA[4] = slotParms.player5;

   userA[0] = slotParms.user1;
   userA[1] = slotParms.user2;
   userA[2] = slotParms.user3;
   userA[3] = slotParms.user4;
   userA[4] = slotParms.user5;

   usergA[0] = slotParms.userg1;
   usergA[1] = slotParms.userg2;
   usergA[2] = slotParms.userg3;
   usergA[3] = slotParms.userg4;
   usergA[4] = slotParms.userg5;

   mshipA[0] = slotParms.mship1;
   mshipA[1] = slotParms.mship2;
   mshipA[2] = slotParms.mship3;
   mshipA[3] = slotParms.mship4;
   mshipA[4] = slotParms.mship5;


   //
   //  Look for a Legacy Preferred Associates mship type with a guest
   //
   loop1:
   for (int i = 0; i < 5; i++) {

      if (!userA[i].equals( "" ) && mshipA[i].equals( "Legacy Preferred Associates" )) {    // if Legacy member

         user = userA[i];
           
         if (usergA[0].equals( user ) || usergA[1].equals( user ) || usergA[2].equals( user ) ||
             usergA[3].equals( user ) || usergA[4].equals( user )) {                               // if any guests for this user 

            //
            //  Check if request is for a holiday or other restricted day
            //
            if (slotParms.date == Hdate1 || slotParms.date == Hdate2b || slotParms.date == Hdate3 ||
                slotParms.day.equals( "Friday" ) || slotParms.day.equals( "Saturday" ) || slotParms.day.equals( "Sunday" ) ||
                slotParms.day.equals( "Monday" )) {

               error = true;                          // indicate error
               slotParms.player = playerA[i];         // save player name for error message
               break loop1;

            } else {

               //
               //  Must be a Tues, Wed or Thu - check for invalid times
               //
               if (slotParms.time > 1129 && slotParms.time < 1401) {      // if between 11:30 and 2:00
     
                  error = true;                          // indicate error
                  slotParms.player = playerA[i];         // save player name for error message
                  break loop1;
               }  
            }
         }
      }
   }              // end of FOR loop (do each player)

   return(error);
 }

 /**
  * Checks to see if any members of the current notification already have 2 notifications waiting to be authorized
  * and placed on the tee sheet.
  *
  * @param user - current user's username
  * @param slotParms - id of the current notification
  * @param con - Connection to club database
  *
  * @return - error - true if user already has 2+ unprocessed notifications submitted, false otherwise
  */
 public static boolean checkWFNotifications(String user, int notify_id, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     boolean error = false;

     int count = 0;

     try {
         
         // Check user to see if they aalready have 2 or more submitted notifications that have yet to be processed
         count = 0;

         pstmt = con.prepareStatement("SELECT count(*) FROM notifications WHERE converted = 0 AND req_datetime > CURDATE() AND created_by = ? AND notification_id <> ?");
         pstmt.clearParameters();
         pstmt.setString(1, user);
         pstmt.setInt(2, notify_id);

         rs = pstmt.executeQuery();

         if (rs.next()) {
             count = rs.getInt(1);

             if (count >= 2) {
                 error = true;
             }
         } else {
             error = true;
         }

         pstmt.close();
         
     } catch (Exception exc) {

         error = true;
         
     } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { pstmt.close(); }
         catch (Exception ignore) {}

     }

     return error;
 }

 //************************************************************************
 //
 //  logError - logs system error messages to a text file
 //
 //************************************************************************

 public static void logError(String msg) {

    // now passing this call to Utilities
    Utilities.logError( msg );

 }  // end of logError


}  // end of verifyNCustom class

