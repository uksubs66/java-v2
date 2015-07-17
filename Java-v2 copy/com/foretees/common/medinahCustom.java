/**************************************************************************************************************
 *   medinahCustom:  This will provide the common methods for processing custom requests for Medinah CC.
 *
 *       called by:  Proshop_slot
 *                   Proshop_sheet
 *                   Member_slot
 *                   Member_sheet
 *
 *
 *   created:  5/18/2005   Bob P.
 *
 *
 *   last updated:
 *
 *             5/06/2009  Make adjustments to walk-up times based on new tee time intervals for weekdays.
 *             4/03/2009  Remove 7:36 and 9:00 from Member Times list on weekdays, change 2:00 on weekdays
 *                        to an Outside Time - all on Course 3 (case 1631).
 *             6/03/2008  Change 4th of July date from the 3rd to the 4th.
 *             5/03/2007  Change the member time grid per Medinah's request.
 *             4/17/2006  Change the member time grid per Medinah's request.
 *             4/17/2006  Remove the use of ARR's per Medinah's request.
 *             9/10/2005  Add 'Social Regular Probationary' to list of memberships that can use ARR's.
 *             8/25/2005  Add 'Regular Probationary' to list of memberships that can use ARR's.
 *             6/01/2005  Add 'Regular Probationary Member' to list of member types that can use ARR's
 *                        (per Mr. Scully's request).
 *
 **************************************************************************************************************
 */


package com.foretees.common;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class medinahCustom {

   private static String rev = ProcessConstants.REV;

   //
   //  Course names
   //
   private static String course1 = "No 1";
   private static String course2 = "No 2";
   private static String course3 = "No 3";

   //
   //  Event names - those to exclude in Guest Quota Counts
   //
   private static String event1 = "Classic";
   private static String event2 = "Camel Trail";
   private static String event3 = "Father-Son";
   private static String event4 = "Father-Daughter";
   private static String event5 = "White Fez";
   private static String event6 = "Ladies Guest Day";
   private static String event7 = "Husband and Wife Invitational";

   //
   //  Holidays for Medinah
   //
   private static long Hdate1 = ProcessConstants.memDay;     // Memorial Day
   private static long Hdate2 = ProcessConstants.july4b;     // 4th of July 
   private static long Hdate3 = ProcessConstants.laborDay;   // Labor Day

   //
   //  Member Types
   //
   private static String Rmem = "Regular Member";
   private static String Rspouse = "Regular Spouse";
   private static String Jrmem = "Jr. Member";
   private static String Jrspouse = "Jr. Spouse";
   private static String NRmem = "NR Member";
   private static String NRspouse = "NR Spouse";
   private static String RPmem = "Reg Prob Member";
   private static String RPspouse = "Reg Prob Spouse";
   private static String Socmem = "Soc Member";
   private static String Socspouse = "Soc Spouse";
   private static String FM17o = "FM 17 and over";
   private static String FM14t16 = "FM 14 thru 16";
   private static String FM12and13 = "FM 12 and 13";
   private static String FM8t11 = "FM 8 thru 11";
   private static String FMu8 = "Fam Member under 8";

   //
   //  Membership Types
   //
   private static String Regular = "Regular";
   private static String Perpetual = "Perpetual";
   private static String CourtPP = "Courtesy Past President";
   private static String Honorary = "Honorary";
   private static String JrMem = "Junior Member";
   private static String JrProb = "Junior Probationary";
   private static String NonRes = "Non Resident";
   private static String RegProb = "Regular Probationary";
   private static String RegSS = "Regular Surviving Spouse";
   private static String Social = "Social";
   private static String SocProb = "Social Probationary";
   private static String SocReg = "Social Regular";
   private static String SocRegProb = "Social Regular Probation";
   private static String SurvSS = "Surviving Spouse Social";
   private static String Staff = "Staff";
   private static String MT = "Empty";



/**
 //********************************************************************************************************
 //
 //  checkMemTime - for ALL Courses 
 //
 //        Check if the day & time specified is to be a 'Member Time' (walk-up time).
 //
 //    called by:  Member_sheet
 //                Proshop_sheet
 //
 //********************************************************************************************************
 **/

 public static boolean checkMemTime(String course, String day, int time, long date) {


   boolean memTime = false;
   
   int wetime1 = 1101;         // weekend time limit for walk-up times (time to check)
   int wetime2 = 1100;         // flag all times prior to 11:00 AM (do not flag 11:00)


   //  temp times for weekends and holidays while Course 3 is shut down for rennovations
   if (date < 20100615) {  
      wetime1 = 1201;       // catch all times noon and earlier
      wetime2 = 1200;       // flag all times prior to noon
   }
      
   
   //
   //  First check for w/e or holiday.  All times before 11:00 AM are Member Times. (ALL courses)
   //
   if ((day.equals( "Saturday" ) || day.equals( "Sunday" ) || date == Hdate1 ||
       date == Hdate2 || date == Hdate3) && time < wetime1) {

      if (time < wetime2) {            // do not do 11:00 on w/e and holidays, only all times before 11:00 and some after
        
         memTime = true;                
      }


   } else {            // not a w/e or holiday, or After 11:00 AM

      //
      //  Only check if Course #1 or #3 (check Course #2 while Course 3 is down for repairs - 2009-2010)
      //
      if (course.equals( course1 ) || (course.equals( course2 ) && date < 20100615)) {     // course #1 - same everyday

         if (time == 630 || time == 640 || time == 650 || time == 700 || time == 710 || time == 800 || time == 830 || time == 900 || time == 930 ||
             time == 1000 || time == 1100 || time == 1150 || time == 1200 || time == 1230 || time == 1240 || time == 1300 ||
             time == 1310 || time == 1320 || time == 1330 || time == 1400 || time == 1430 || time == 1500 || time == 1520 ||
             time == 1540) {

            memTime = true;
         }

      } else {

         if (course.equals( course3 )) {

            //
            //  First check for w/e or holiday.     (NOTE:  No 3 has 12 min ints on w/e & Mon, 10 min ints on weekdays)
            //
            if (day.equals( "Saturday" ) || day.equals( "Sunday" ) || date == Hdate1 ||
                date == Hdate2 || date == Hdate3) {
               
               //  check day in case July 4th on weekday !!   (not an issue until 2012 !!!!!!!!!!!!!!!!!!)
              
               //if (day.equals( "Saturday" ) || day.equals( "Sunday" ) || day.equals( "Monday" )) {

                  //    check for 12 min intervals
                  if (time == 1112 || time == 1136 || time == 1200 || time == 1224 || time == 1248 ||
                      time == 1312 || time == 1336 || time == 1400 || time == 1424 || time == 1512 || time == 1536 ||
                      time == 1600) {

                     memTime = true;
                  }
                  
               /*
               } else {         // Tues - Fri - 10 minute intervals on No 3           *************** update these times ****************
                  
                  if (time == 1112 || time == 1136 || time == 1200 || time == 1224 || time == 1248 ||
                      time == 1312 || time == 1336 || time == 1400 || time == 1424 || time == 1512 || time == 1536 ||
                      time == 1600) {

                     memTime = true;
                  }
               }
                */

            } else {    // Course 3 - NOT a holiday or w/e

               if (day.equals( "Monday" )) {

                  if (time == 636 || time == 648 || time == 700 || time == 800 || time == 812 || 
                      time == 912 || time == 924 || time == 1000 || time == 1048 || time == 1100 || time == 1112 ||
                      time == 1224 || time == 1236 || time == 1248 ||time == 1300 ||
                      time == 1312 || time == 1324 || time == 1336 || time == 1400 || time == 1436 || time == 1512) {

                     memTime = true;
                  }
                  
               } else {       // Tues - Fri use 10 min intervals on No 3           *************** update these times ****************            
                  
                  if (time == 630 || time == 640 || time == 650 || time == 800 || time == 810 || 
                      time == 910 || time == 920 || time == 1000 || time == 1050 || time == 1110 ||
                      time == 1220 || time == 1230 || time == 1240 || time == 1250 || time == 1300 || time == 1310 ||
                      time == 1320 || time == 1440 || time == 1510) {

                     memTime = true;
                  }
               }
            }
         }
      }
   }

   return(memTime);
 }


/**
 //********************************************************************************************************
 //
 //  checkARRmem - for Course #1 and #3 - check Membership Type & Mem Type for ARR
 //
 //   ************ NOT USED ANY LONGER 4/2006 ****************
 //
 //       Primary members of golf mships have Advanced Reservation Rights (ARR)
 //
 //    called by:  Member_select
 //                Member_sheet
 //                Member_slot
 //                Proshop_slot
 //
 //********************************************************************************************************
 **/

 public static boolean checkARRmem(String user, Connection con, String mship, String mtype) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean arr = false;


   if (!user.equals( "" ) && user != null) {

      if (mship.equals( "" ) || mship == null || mtype.equals( "" ) || mtype == null) {

         try {

            pstmt = con.prepareStatement (
               "SELECT m_ship, m_type FROM member2b WHERE username = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, user);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               mship = rs.getString(1);
               mtype = rs.getString(2);
            }
            pstmt.close();

         }
         catch (Exception e) {
            arr = false;
         }
      }

      if (mship.equals( Regular ) || mship.equals( Perpetual ) || mship.equals( CourtPP ) ||
          mship.equals( Honorary ) || mship.equals( JrMem ) || mship.equals( SocReg ) ||
          mship.equals( SocRegProb ) || mship.equals( RegProb ) || mship.equals( NonRes )) {

         if (mtype.equals( Rmem ) || mtype.equals( RPmem ) || mtype.equals( Jrmem ) || mtype.equals( Socmem )) {

            arr = true;
         }
      }
   }

   return(arr);
 }


/**
 //********************************************************************************************************
 //
 //  checkNonRes1 - for Course #1 - check Non Resident Membership Types
 //
 //       "Non Resident" membership types can only have 12 guests between 6/01 and 8/31 per mship.
 //
 //    called by:  Member_slot
 //                Proshop_slot
 //
 //   *** Do not have to worry about 5-somes ***
 //
 //********************************************************************************************************
 **/

 public static boolean checkNonRes1(parmSlot slotParms, Connection con) {


   boolean error = false;
     
   int guests = 0;
   int guests1 = 0;
   int guests2 = 0;
   int guests3 = 0;
   int guests4 = 0;


   //
   //  Only check if Course #1 and any guests included in tee time
   //
   if (slotParms.course.equals( course1 ) && slotParms.guests > 0) {

      //
      //  See if any players are 'Non Resident' membership types
      //
      if (slotParms.mship1.equals( NonRes )) {
     
         //
         //  Non resident found - see if he has any guests
         //
         if (slotParms.user1.equals( slotParms.userg2 )) {

            guests1++;
         }
         if (slotParms.user1.equals( slotParms.userg3 )) {

            guests1++;
         }
         if (slotParms.user1.equals( slotParms.userg4 )) {

            guests1++;
         }

         //
         //  Count guests of any other family member in this request
         //
         if (slotParms.mNum1.equals( slotParms.mNum2 )) {         // if player 2 is a fmaily member - check them

            if (slotParms.user2.equals( slotParms.userg3 )) {

               guests1++;
            }
            if (slotParms.user2.equals( slotParms.userg4 )) {

               guests1++;
            }
         }
           
         if (slotParms.mNum1.equals( slotParms.mNum3 )) {           // player 3 match?

            if (slotParms.user3.equals( slotParms.userg4 )) {

               guests1++;
            }
         }
           
         if (guests1 > 0) {     // if any found in this request
           
            //
            //  Get number of guests already requested (not including this tee time)
            //
            guests = countGuests(slotParms, slotParms.mNum1, course1, con);   // count # of guests for this mship (mNum)

            guests += guests1;     // combine counts

            if (guests > 12) {     // if this exceeds the max allowed

               error = true;
               slotParms.player = slotParms.player1;
            }
         }
      }
        
      if (error == false) {
        
         //
         //   Check Player 2 if Non Resident and not same family as Player 1
         //
         if (slotParms.mship2.equals( NonRes ) && !slotParms.mNum2.equals( slotParms.mNum1 )) {

            //
            //  Non resident found - see if he has any guests
            //
            if (slotParms.user2.equals( slotParms.userg3 )) {

               guests2++;
            }
            if (slotParms.user2.equals( slotParms.userg4 )) {

               guests2++;
            }

            //
            //  Count guests of any other family member in this request
            //
            if (slotParms.mNum2.equals( slotParms.mNum3 )) {           // player 3 match?

               if (slotParms.user3.equals( slotParms.userg4 )) {

                  guests2++;
               }
            }

            if (guests2 > 0) {     // if any found in this request

               //
               //  Get number of guests already requested (not including this tee time)
               //
               guests = countGuests(slotParms, slotParms.mNum2, course1, con);   // count # of guests for this mship (mNum)

               guests += guests2;    // combine counts
                 
               if (guests > 12) {     // if this exceeds the max allowed

                  error = true;
                  slotParms.player = slotParms.player2;
               }
            }
         }
      }

      if (error == false) {

         //
         //   Check Player 3 if Non Resident and not same family as Player 1 or 2
         //
         if (slotParms.mship3.equals( NonRes ) && !slotParms.mNum3.equals( slotParms.mNum1 ) &&
             !slotParms.mNum3.equals( slotParms.mNum2 )) {

            //
            //  Non resident found - see if he has any guests
            //
            if (slotParms.user3.equals( slotParms.userg4 )) {

               //
               //  Get number of guests already requested (not including this tee time)
               //
               guests = countGuests(slotParms, slotParms.mNum3, course1, con);   // count # of guests for this mship (mNum)

               guests++;              // combine counts (allow for this guest)

               if (guests > 12) {     // if this exceeds the max allowed

                  error = true;
                  slotParms.player = slotParms.player3;
               }
            }
         }
      }           // no need to check player 4 - can't have any guests
   }

   return(error);
 }


/**
 //********************************************************************************************************
 //
 //  checkSpouseGuest - for Course #1 & #3 - check if Spouse has a guest (not allowed on weekdays after 4 PM)
 //
 //
 //    called by:  Member_slot
 //
 //   *** Do not have to worry about 5-somes ***
 //
 //********************************************************************************************************
 **/

 public static boolean checkSpouseGuest(parmSlot slotParms, Connection con) {


   boolean error = false;

   String day = slotParms.day;

   long date = slotParms.date;

   //
   //  Only check if Course #1 or Course #3 and any guests included in tee time
   //
   if ((slotParms.course.equals( course1 ) || slotParms.course.equals( course3 )) && slotParms.guests > 0) {

      //
      //  Check if a weekday and not a holiday
      //
      if (!day.equals( "Saturday" ) && !day.equals( "Sunday" ) && date != Hdate1 &&
          date != Hdate2 && date != Hdate3) {

         //
         //  See if any players are a Spouse
         //
         if (slotParms.mtype1.equals( Rspouse ) || slotParms.mtype1.equals( Jrspouse ) ||
             slotParms.mtype1.equals( NRspouse ) || slotParms.mtype1.equals( RPspouse ) ||
             slotParms.mtype1.equals( Socspouse )) {

            //
            //  Spouse found - see if she has a guest
            //
            if (slotParms.user1.equals( slotParms.userg2 ) || slotParms.user1.equals( slotParms.userg3 ) ||
                slotParms.user1.equals( slotParms.userg4 )) {

               error = true;
               slotParms.player = slotParms.player1;
            }            
         }

         if (error == false) {

            if (slotParms.mtype2.equals( Rspouse ) || slotParms.mtype2.equals( Jrspouse ) ||
                slotParms.mtype2.equals( NRspouse ) || slotParms.mtype2.equals( RPspouse ) ||
                slotParms.mtype2.equals( Socspouse )) {

               //
               //  Spouse found - see if she has a guest
               //
               if (slotParms.user2.equals( slotParms.userg3 ) || slotParms.user2.equals( slotParms.userg4 )) {

                  error = true;
                  slotParms.player = slotParms.player2;
               }
            }
         }

         if (error == false) {

            if (slotParms.mtype3.equals( Rspouse ) || slotParms.mtype3.equals( Jrspouse ) ||
                slotParms.mtype3.equals( NRspouse ) || slotParms.mtype3.equals( RPspouse ) ||
                slotParms.mtype3.equals( Socspouse )) {

               //
               //  Spouse found - see if she has a guest
               //
               if (slotParms.user3.equals( slotParms.userg4 )) {

                  error = true;
                  slotParms.player = slotParms.player3;
               }
            }
         }
      }           // no need to check player 4 - can't have any guests
   }

   return(error);
 }


/**
 //********************************************************************************************************
 //
 //  checkARRmax - for Course #1 & #3 - check each Member to see if they already used up their ARR
 //
 //   ************ NOT USED ANY LONGER 4/2006 ****************
 //
 //       Some membership types can use Advanced Reservation Requests to book tee times more than 2 days in adv.
 //
 //       "Social Regular Probationary",
 //       "Regular", "Perpetual", "Junior Member", "Honorary", "Social Regular" and "Courtesy Past President" 
 //        & "Non Resident" membership types (member only - no spouse) can use 1 ARR every 14 days and a set total
 //        between April 1 and Oct 30.  Don't worry about spouses here, they are filtered elsewhere. 
 //
 //    called by:  Member_slot
 //                Proshop_slot
 //
 //   *** Do not have to worry about 5-somes ***
 //
 //********************************************************************************************************
 **/

 public static int checkARRmax(parmSlot slotParms, Connection con)
                                   throws Exception {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   int numARRs = 0;

   String mship1 = slotParms.mship1;
   String mship2 = slotParms.mship2;
   String mship3 = slotParms.mship3;
   String mship4 = slotParms.mship4;

   int max = 0;
   int year = 0;
   int month = 0;
   int day = 0;
     
   long thisYear = 0;
   long date = 0;
   long sdate = 0;
   long edate = 0;
   long pastDate = 0;
   long futureDate = 0;


   //
   //  Only check if Course #1 or #3, more than 2 days in advance and within date range (4/01 - 10/31)
   //
   if ((slotParms.course.equals( course1 ) || slotParms.course.equals( course3 )) && slotParms.ind > 2 &&
        slotParms.mm > 3 && slotParms.mm < 11) {

      //
      //  See if any players must be checked for ARRs
      //
      if (!mship1.equals( Regular ) && !mship1.equals( Perpetual ) && !mship1.equals( JrMem ) &&
          !mship1.equals( CourtPP ) && !mship1.equals( Honorary ) && !mship1.equals( SocReg ) &&
          !mship1.equals( NonRes ) && !mship1.equals( SocRegProb )) {

         mship1 = "";      // do not check
      }
      if (!mship2.equals( Regular ) && !mship2.equals( Perpetual ) && !mship2.equals( JrMem ) &&
          !mship2.equals( CourtPP ) && !mship2.equals( Honorary ) && !mship2.equals( SocReg ) &&
          !mship2.equals( NonRes ) && !mship2.equals( SocRegProb )) {

         mship2 = "";      // do not check
      }
      if (!mship3.equals( Regular ) && !mship3.equals( Perpetual ) && !mship3.equals( JrMem ) &&
          !mship3.equals( CourtPP ) && !mship3.equals( Honorary ) && !mship3.equals( SocReg ) &&
          !mship3.equals( NonRes ) && !mship3.equals( SocRegProb )) {

         mship3 = "";      // do not check
      }
      if (!mship4.equals( Regular ) && !mship4.equals( Perpetual ) && !mship4.equals( JrMem ) &&
          !mship4.equals( CourtPP ) && !mship4.equals( Honorary ) && !mship4.equals( SocReg ) &&
          !mship4.equals( NonRes ) && !mship4.equals( SocRegProb )) {

         mship4 = "";      // do not check
      }

      //
      //  See if any players must be checked for ARRs.  If so, then gather some information for tests.
      //
      if (!mship1.equals( "" ) || !mship2.equals( "" ) || !mship3.equals( "" ) || !mship4.equals( "" )) {

         //
         //  Get this date's calendar and then determine 13 days ago.
         //
         Calendar cal = new GregorianCalendar();         // get todays date

         //
         //  set cal to tee time's date
         //
         cal.set(Calendar.YEAR,slotParms.yy);                    // set year in cal
         cal.set(Calendar.MONTH,slotParms.mm-1);                 // set month in cal
         cal.set(Calendar.DAY_OF_MONTH,slotParms.dd);            // set day in cal

         //
         // roll cal back 13 days to get past date (for range)
         //
         cal.add(Calendar.DATE,-13);          

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH) +1;
         day = cal.get(Calendar.DAY_OF_MONTH);

         pastDate = (year * 10000) + (month * 100) + day;    // create a date field of yyyymmdd

         thisYear = (long)year;                              // get current year
         sdate = (thisYear * 10000) + 401;                   // April 1st 
         edate = (thisYear * 10000) + 1031;                  // Oct 31st
           
         //
         // roll ahead now to tee time plus 13 days
         //
         cal.add(Calendar.DATE,26);                    // roll ahead 26 days from past date

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH) +1;
         day = cal.get(Calendar.DAY_OF_MONTH);

         futureDate = (year * 10000) + (month * 100) + day;    // create a date field of yyyymmdd
      }

      //
      //  See if any players must be checked for ARRs
      //
      if (!mship1.equals( "" )) {

         //
         //  Must check member - see if he has used any ARRs in the past 13 days or next 13 days (1 per 14 days)
         //
         try {
           
            pstmt = con.prepareStatement (
               "SELECT fb " +
               "FROM medinaharr WHERE username = ? AND date >= ? AND date <= ? AND course = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, slotParms.user1);
            pstmt.setLong(2, pastDate);
            pstmt.setLong(3, futureDate);
            pstmt.setString(4, slotParms.course);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               numARRs = 1;
               slotParms.player = slotParms.player1;
            }
               
            pstmt.close();
              
            if (numARRs == 0) {                // if ok to continue

               // 
               //  get total count of ARRs for year (4/01 - 10/31) on this course
               //
               pstmt = con.prepareStatement (
                  "SELECT fb " +
                  "FROM medinaharr WHERE username = ? AND date >= ? AND date <= ? AND date != ? AND course = ?");

               pstmt.clearParameters();        // clear the parms
               pstmt.setString(1, slotParms.user1);
               pstmt.setLong(2, sdate);
               pstmt.setLong(3, edate);
               pstmt.setLong(4, slotParms.date);           // don't count today in case this already entered
               pstmt.setString(5, slotParms.course);
               rs = pstmt.executeQuery();    

               while (rs.next()) {

                  numARRs++;
               }
                 
               pstmt.close();

               //
               //  Check ARRs against max allowed for mship type
               //
               if (numARRs > 0) {

                  if (slotParms.course.equals( course1 )) {
                    
                     max = 9;               // allowed 9 ARRs
                       
                     if (mship1.equals( SocReg ) || mship1.equals( SocRegProb )) {
                       
                        max = 6;             // unless Social Regular
                     }
                       
                  } else {                  // course 3

                     max = 6;               // allowed 6 ARRs

                     if (mship1.equals( SocReg ) || mship1.equals( SocRegProb )) {

                        max = 0;             // unless Social Regular - none
                     }
                  }
                    
                  if (mship1.equals( NonRes )) {

                     max = 2;             // Non Resident gets 2 on each course
                  }

                  if (numARRs >= max) {
                    
                     slotParms.player = slotParms.player1;

                     if (numARRs == 1) {       // would have to be a Soc Reg
                       
                        numARRs = 99;          // change to let _slot know its not a 14 day violation
                     }
                       
                  } else {
                    
                     numARRs = 0;             // indicate ok
                  }
               }
            }
         }
         catch (Exception e) {

            throw new UnavailableException("Error Checking ARR - medinahCustom.checkARRmax: " + e.getMessage());
         }
      }

      if (numARRs == 0 && !mship2.equals( "" )) {

         //
         //   Check Player 2 for ARRs
         //
         try {

            pstmt = con.prepareStatement (
               "SELECT fb " +
               "FROM medinaharr WHERE username = ? AND date >= ? AND date <= ? AND course = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, slotParms.user2);
            pstmt.setLong(2, pastDate);
            pstmt.setLong(3, futureDate);
            pstmt.setString(4, slotParms.course);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               numARRs = 1;
               slotParms.player = slotParms.player2;
            }

            pstmt.close();

            if (numARRs == 0) {                // if ok to continue

               //
               //  get total count of ARRs for year (4/01 - 10/31)
               //
               pstmt = con.prepareStatement (
                  "SELECT fb " +
                  "FROM medinaharr WHERE username = ? AND date >= ? AND date <= ? AND date != ? AND course = ?");

               pstmt.clearParameters();        // clear the parms
               pstmt.setString(1, slotParms.user2);
               pstmt.setLong(2, sdate);
               pstmt.setLong(3, edate);
               pstmt.setLong(4, slotParms.date);           // don't count today in case this already entered
               pstmt.setString(5, slotParms.course);
               rs = pstmt.executeQuery();

               while (rs.next()) {

                  numARRs++;
               }

               pstmt.close();

               //
               //  Check ARRs against max allowed for mship type
               //
               if (numARRs > 0) {

                  if (slotParms.course.equals( course1 )) {

                     max = 9;               // allowed 9 ARRs

                     if (mship2.equals( SocReg ) || mship2.equals( SocRegProb )) {

                        max = 6;             // unless Social Regular
                     }

                  } else {                  // course 3

                     max = 6;               // allowed 6 ARRs

                     if (mship2.equals( SocReg ) || mship2.equals( SocRegProb )) {

                        max = 0;             // unless Social Regular - none
                     }
                  }

                  if (mship2.equals( NonRes )) {

                     max = 2;             // Non Resident gets 2 on each course
                  }

                  if (numARRs >= max) {

                     slotParms.player = slotParms.player2;

                     if (numARRs == 1) {       // would have to be a Soc Reg

                        numARRs = 99;          // change to let _slot know its not a 14 day violation
                     }

                  } else {

                     numARRs = 0;             // indicate ok
                  }
               }
            }
         }
         catch (Exception e) {

            throw new UnavailableException("Error Checking ARR - medinahCustom.checkARRmax: " + e.getMessage());
         }
      }

      if (numARRs == 0 && !mship3.equals( "" )) {

         //
         //   Check Player 3 for ARRs
         //
         try {

            pstmt = con.prepareStatement (
               "SELECT fb " +
               "FROM medinaharr WHERE username = ? AND date >= ? AND date <= ? AND course = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, slotParms.user3);
            pstmt.setLong(2, pastDate);
            pstmt.setLong(3, futureDate);
            pstmt.setString(4, slotParms.course);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               numARRs = 1;
               slotParms.player = slotParms.player3;
            }

            pstmt.close();

            if (numARRs == 0) {                // if ok to continue

               //
               //  get total count of ARRs for year (4/01 - 10/31)
               //
               pstmt = con.prepareStatement (
                  "SELECT fb " +
                  "FROM medinaharr WHERE username = ? AND date >= ? AND date <= ? AND date != ? AND course = ?");

               pstmt.clearParameters();        // clear the parms
               pstmt.setString(1, slotParms.user3);
               pstmt.setLong(2, sdate);
               pstmt.setLong(3, edate);
               pstmt.setLong(4, slotParms.date);           // don't count today in case this already entered
               pstmt.setString(5, slotParms.course);
               rs = pstmt.executeQuery();

               while (rs.next()) {

                  numARRs++;
               }

               pstmt.close();

               //
               //  Check ARRs against max allowed for mship type
               //
               if (numARRs > 0) {

                  if (slotParms.course.equals( course1 )) {

                     max = 9;               // allowed 9 ARRs

                     if (mship3.equals( SocReg ) || mship3.equals( SocRegProb )) {

                        max = 6;             // unless Social Regular
                     }

                  } else {                  // course 3

                     max = 6;               // allowed 6 ARRs

                     if (mship3.equals( SocReg ) || mship3.equals( SocRegProb )) {

                        max = 0;             // unless Social Regular - none
                     }
                  }

                  if (mship3.equals( NonRes )) {

                     max = 2;             // Non Resident gets 2 on each course
                  }

                  if (numARRs >= max) {

                     slotParms.player = slotParms.player3;

                     if (numARRs == 1) {       // would have to be a Soc Reg

                        numARRs = 99;          // change to let _slot know its not a 14 day violation
                     }

                  } else {

                     numARRs = 0;             // indicate ok
                  }
               }
            }
         }
         catch (Exception e) {

            throw new UnavailableException("Error Checking ARR - medinahCustom.checkARRmax: " + e.getMessage());
         }
      }

      if (numARRs == 0 && !mship4.equals( "" )) {

         //
         //   Check Player 4 for ARRs
         //
         try {

            pstmt = con.prepareStatement (
               "SELECT fb " +
               "FROM medinaharr WHERE username = ? AND date >= ? AND date <= ? AND course = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, slotParms.user4);
            pstmt.setLong(2, pastDate);
            pstmt.setLong(3, futureDate);
            pstmt.setString(4, slotParms.course);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               numARRs = 1;
               slotParms.player = slotParms.player4;
            }

            pstmt.close();

            if (numARRs == 0) {                // if ok to continue

               //
               //  get total count of ARRs for year (4/01 - 10/31)
               //
               pstmt = con.prepareStatement (
                  "SELECT fb " +
                  "FROM medinaharr WHERE username = ? AND date >= ? AND date <= ? AND date != ? AND course = ?");

               pstmt.clearParameters();        // clear the parms
               pstmt.setString(1, slotParms.user4);
               pstmt.setLong(2, sdate);
               pstmt.setLong(3, edate);
               pstmt.setLong(4, slotParms.date);           // don't count today in case this already entered
               pstmt.setString(5, slotParms.course);
               rs = pstmt.executeQuery();

               while (rs.next()) {

                  numARRs++;
               }

               pstmt.close();

               //
               //  Check ARRs against max allowed for mship type
               //
               if (numARRs > 0) {

                  if (slotParms.course.equals( course1 )) {

                     max = 9;               // allowed 9 ARRs

                     if (mship4.equals( SocReg ) || mship4.equals( SocRegProb )) {

                        max = 6;             // unless Social Regular
                     }

                  } else {                  // course 3

                     max = 6;               // allowed 6 ARRs

                     if (mship4.equals( SocReg ) || mship4.equals( SocRegProb )) {

                        max = 0;             // unless Social Regular - none
                     }
                  }

                  if (mship4.equals( NonRes )) {

                     max = 2;             // Non Resident gets 2 on each course
                  }

                  if (numARRs >= max) {

                     slotParms.player = slotParms.player4;

                     if (numARRs == 1) {       // would have to be a Soc Reg

                        numARRs = 99;          // change to let _slot know its not a 14 day violation
                     }

                  } else {

                     numARRs = 0;             // indicate ok
                  }
               }
            }
         }
         catch (Exception e) {

            throw new UnavailableException("Error Checking ARR - medinahCustom.checkARRmax: " + e.getMessage());
         }
      }

   }

   return(numARRs);
 }


/**
 //********************************************************************************************************
 //
 //  addARR - for Course #1 & #3 - add an entry to the ARR table if tee time request is new
 //
 //   ************ NOT USED ANY LONGER 4/2006 ****************
 //
 //       Some membership types can use Advanced Reservation Requests to book tee times more than 2 days in adv.
 //
 //       "Social Regular Probationary",
 //       "Regular", "Perpetual", "Junior Member", "Honorary", "Social Regular" and "Courtesy Past President"
 //        membership types (member only - no spouse) can use 1 ARR every 14 days and a set total between
 //        April 1 and Oct 30.  Don't worry about spouses here, they are filtered elsewhere.
 //
 //    called by:  Member_slot
 //                Proshop_slot
 //
 //   *** Do not have to worry about 5-somes ***
 //
 //********************************************************************************************************
 **/

 public static void addARR(parmSlot slotParms, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   String mship1 = slotParms.mship1;
   String mship2 = slotParms.mship2;
   String mship3 = slotParms.mship3;
   String mship4 = slotParms.mship4;


   //
   //  Only check if Course #1 or #3, more than 2 days in advance and within date range (4/01 - 10/31)
   //
   if ((slotParms.course.equals( course1 ) || slotParms.course.equals( course3 )) && slotParms.ind > 2 &&
        slotParms.mm > 3 && slotParms.mm < 11) {

      //
      //  See if any players must be checked for ARRs
      //
      if (!mship1.equals( Regular ) && !mship1.equals( Perpetual ) && !mship1.equals( JrMem ) &&
          !mship1.equals( CourtPP ) && !mship1.equals( Honorary ) && !mship1.equals( SocReg ) &&
          !mship1.equals( SocRegProb )) {

         mship1 = "";      // do not check
      }
      if (!mship2.equals( Regular ) && !mship2.equals( Perpetual ) && !mship2.equals( JrMem ) &&
          !mship2.equals( CourtPP ) && !mship2.equals( Honorary ) && !mship2.equals( SocReg ) &&
          !mship2.equals( SocRegProb )) {

         mship2 = "";      // do not check
      }
      if (!mship3.equals( Regular ) && !mship3.equals( Perpetual ) && !mship3.equals( JrMem ) &&
          !mship3.equals( CourtPP ) && !mship3.equals( Honorary ) && !mship3.equals( SocReg ) &&
          !mship3.equals( SocRegProb )) {

         mship3 = "";      // do not check
      }
      if (!mship4.equals( Regular ) && !mship4.equals( Perpetual ) && !mship4.equals( JrMem ) &&
          !mship4.equals( CourtPP ) && !mship4.equals( Honorary ) && !mship4.equals( SocReg ) &&
          !mship4.equals( SocRegProb )) {

         mship4 = "";      // do not check
      }

      //
      //  See if any players must register ARRs
      //
      if (!mship1.equals( "" )) {

         if (!slotParms.user1.equals( slotParms.oldUser1 )) {      // if member was not already in the tee time
           
            //
            //  Add an ARR for this member
            //
            try {

               pstmt = con.prepareStatement (
                 "INSERT INTO medinaharr (username, date, time, " +
                 "course, fb) VALUES " +
                 "(?,?,?,?,?)");

               pstmt.clearParameters();        // clear the parms
               pstmt.setString(1, slotParms.user1);
               pstmt.setLong(2, slotParms.date);
               pstmt.setInt(3, slotParms.time);
               pstmt.setString(4, slotParms.course);
               pstmt.setInt(5, slotParms.fb);
               pstmt.executeUpdate();          // execute the prepared stmt

               pstmt.close();

            }
            catch (Exception e) {
            }
         }
      }

      if (!mship2.equals( "" )) {

         if (!slotParms.user2.equals( slotParms.oldUser2 )) {      // if member was not already in the tee time

            //
            //  Add an ARR for this member
            //
            try {

               pstmt = con.prepareStatement (
                 "INSERT INTO medinaharr (username, date, time, " +
                 "course, fb) VALUES " +
                 "(?,?,?,?,?)");

               pstmt.clearParameters();        // clear the parms
               pstmt.setString(1, slotParms.user2);
               pstmt.setLong(2, slotParms.date);
               pstmt.setInt(3, slotParms.time);
               pstmt.setString(4, slotParms.course);
               pstmt.setInt(5, slotParms.fb);
               pstmt.executeUpdate();          // execute the prepared stmt

               pstmt.close();

            }
            catch (Exception e) {
            }
         }
      }

      if (!mship3.equals( "" )) {

         if (!slotParms.user3.equals( slotParms.oldUser3 )) {      // if member was not already in the tee time

            //
            //  Add an ARR for this member
            //
            try {

               pstmt = con.prepareStatement (
                 "INSERT INTO medinaharr (username, date, time, " +
                 "course, fb) VALUES " +
                 "(?,?,?,?,?)");

               pstmt.clearParameters();        // clear the parms
               pstmt.setString(1, slotParms.user3);
               pstmt.setLong(2, slotParms.date);
               pstmt.setInt(3, slotParms.time);
               pstmt.setString(4, slotParms.course);
               pstmt.setInt(5, slotParms.fb);
               pstmt.executeUpdate();          // execute the prepared stmt

               pstmt.close();

            }
            catch (Exception e) {
            }
         }
      }

      if (!mship4.equals( "" )) {

         if (!slotParms.user4.equals( slotParms.oldUser4 )) {      // if member was not already in the tee time

            //
            //  Add an ARR for this member
            //
            try {

               pstmt = con.prepareStatement (
                 "INSERT INTO medinaharr (username, date, time, " +
                 "course, fb) VALUES " +
                 "(?,?,?,?,?)");

               pstmt.clearParameters();        // clear the parms
               pstmt.setString(1, slotParms.user4);
               pstmt.setLong(2, slotParms.date);
               pstmt.setInt(3, slotParms.time);
               pstmt.setString(4, slotParms.course);
               pstmt.setInt(5, slotParms.fb);
               pstmt.executeUpdate();          // execute the prepared stmt

               pstmt.close();

            }
            catch (Exception e) {
            }
         }
      }
   }

   return;
 }


/**
 //********************************************************************************************************
 //
 //  checkGuests3 - for Course #3 - check each Membership Type for Guest Quota
 //
 //       "Non Resident" membership types can only have 6 guests between 6/01 and 8/31 per mship.
 //
 //       "Regular", "Perpetual", "Junior Member", "Honorary", and "Courtesy Past President"
 //          membership types can only have 18 guests between 6/01 and 8/31 per mship.
 //
 //    called by:  Member_slot
 //                Proshop_slot
 //
 //   *** Do not have to worry about 5-somes ***
 //
 //********************************************************************************************************
 **/

 public static boolean checkGuests3(parmSlot slotParms, Connection con) {


   boolean error = false;

   String mship1 = slotParms.mship1;
   String mship2 = slotParms.mship2;
   String mship3 = slotParms.mship3;

   int max = 0;
   int guests = 0;
   int guests1 = 0;
   int guests2 = 0;
   int guests3 = 0;
   int guests4 = 0;


   //
   //  Only check if Course #3 and any guests included in tee time
   //
   if (slotParms.course.equals( course3 ) && slotParms.guests > 0) {

      //
      //  See if any players must be checked for guest restrictions
      //
      if (!mship1.equals( NonRes ) && !mship1.equals( Regular ) && !mship1.equals( Perpetual ) &&
          !mship1.equals( CourtPP ) && !mship1.equals( Honorary ) && !mship1.equals( JrMem )) {

         mship1 = "";      // do not check
      }
      if (!mship2.equals( NonRes ) && !mship2.equals( Regular ) && !mship2.equals( Perpetual ) &&
          !mship2.equals( CourtPP ) && !mship2.equals( Honorary ) && !mship2.equals( JrMem )) {

         mship2 = "";      // do not check
      }
      if (!mship3.equals( NonRes ) && !mship3.equals( Regular ) && !mship3.equals( Perpetual ) &&
          !mship3.equals( CourtPP ) && !mship3.equals( Honorary ) && !mship3.equals( JrMem )) {

         mship3 = "";      // do not check
      }

      //
      //  See if any players must be checked for guest restrictions
      //
      if (!mship1.equals( "" )) {

         //
         //  Must check member - see if he has any guests
         //
         if (slotParms.user1.equals( slotParms.userg2 )) {

            guests1++;
         }
         if (slotParms.user1.equals( slotParms.userg3 )) {

            guests1++;
         }
         if (slotParms.user1.equals( slotParms.userg4 )) {

            guests1++;
         }

         //
         //  Count guests of any other family member in this request
         //
         if (slotParms.mNum1.equals( slotParms.mNum2 )) {         // if player 2 is a fmaily member - check them

            if (slotParms.user2.equals( slotParms.userg3 )) {

               guests1++;
            }
            if (slotParms.user2.equals( slotParms.userg4 )) {

               guests1++;
            }
         }

         if (slotParms.mNum1.equals( slotParms.mNum3 )) {           // player 3 match?

            if (slotParms.user3.equals( slotParms.userg4 )) {

               guests1++;
            }
         }

         if (guests1 > 0) {     // if any found in this request

            //
            //  Get number of guests already requested (not including this tee time)
            //
            guests = countGuests(slotParms, slotParms.mNum1, course3, con);   // count # of guests for this mship (mNum)

            guests += guests1;     // combine counts

            if (mship1.equals( NonRes )) {         // if Non Resident

               max = 6;                            // max of 6 guests for period

            } else {

               max = 18;                           // others can have 18 per period
            }

            if (guests > max) {               // if total exceeds the max allowed

               error = true;
               slotParms.player = slotParms.player1;
            }
         }
      }

      if (error == false) {

         //
         //   Check Player 2 if not same family as Player 1
         //
         if (!mship2.equals( "" ) && !slotParms.mNum2.equals( slotParms.mNum1 )) {

            //
            //  see if he has any guests
            //
            if (slotParms.user2.equals( slotParms.userg3 )) {

               guests2++;
            }
            if (slotParms.user2.equals( slotParms.userg4 )) {

               guests2++;
            }

            //
            //  Count guests of any other family member in this request
            //
            if (slotParms.mNum2.equals( slotParms.mNum3 )) {           // player 3 match?

               if (slotParms.user3.equals( slotParms.userg4 )) {

                  guests2++;
               }
            }

            if (guests2 > 0) {     // if any found in this request

               //
               //  Get number of guests already requested (not including this tee time)
               //
               guests = countGuests(slotParms, slotParms.mNum2, course3, con);   // count # of guests for this mship (mNum)

               guests += guests2;    // combine counts

               if (mship2.equals( NonRes )) {         // if Non Resident

                  max = 6;                            // max of 6 guests for period

               } else {

                  max = 18;                           // others can have 18 per period
               }

               if (guests > max) {               // if total exceeds the max allowed

                  error = true;
                  slotParms.player = slotParms.player2;
               }
            }
         }
      }

      if (error == false) {

         //
         //   Check Player 3 if not same family as Player 1 or 2
         //
         if (!mship3.equals( "" ) && !slotParms.mNum3.equals( slotParms.mNum1 ) &&
             !slotParms.mNum3.equals( slotParms.mNum2 )) {

            //
            //  see if he has any guests
            //
            if (slotParms.user3.equals( slotParms.userg4 )) {

               //
               //  Get number of guests already requested (not including this tee time)
               //
               guests = countGuests(slotParms, slotParms.mNum3, course3, con);   // count # of guests for this mship (mNum)

               guests++;              // combine counts (allow for this guest)

               if (mship3.equals( NonRes )) {         // if Non Resident

                  max = 6;                            // max of 6 guests for period

               } else {

                  max = 18;                           // others can have 18 per period
               }

               if (guests > max) {               // if total exceeds the max allowed

                  error = true;
                  slotParms.player = slotParms.player3;
               }
            }
         }
      }           // no need to check player 4 - can't have any guests
   }

   return(error);
 }


/**
 //********************************************************************************************************
 //
 //  countGuests  - Count # of guests for the Member Number provided (whole family)
 //
 //        Count number of guests for the family between June 1st and Aug 31st (inclusive).
 //        Exclude guests that participated in the 7 designated events.
 //
 //    called by:  checkNonRes1 (above)
 //                checkGuests3 (above)
 //
 //********************************************************************************************************
 **/

 private static int countGuests(parmSlot slotParms, String mNum, String course, Connection con) {


   PreparedStatement pstmt1 = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String user = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String event = "";
     
   int guests = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;

   long date1 = 601;       // June 1st
   long date2 = 831;       // Aug 31st


   //
   //  Get this year
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int year = cal.get(Calendar.YEAR);            // get the year

   year = year * 10000;
   date1 = date1 + year;        // create date fields using current year
   date2 = date2 + year;

   try {

      //
      //  get each user with matching mNum
      //
      pstmt1 = con.prepareStatement (
         "SELECT username " +
         "FROM member2b WHERE memNum = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, mNum);
      rs = pstmt1.executeQuery();     

      while (rs.next()) {

         user = rs.getString(1);

         //
         //  Count the number of guests for this user between 6/01 and 8/31 on course provided
         //
         pstmt2 = con.prepareStatement (
            "SELECT event, userg1, userg2, userg3, userg4, show1, show2, show3, show4 " +
            "FROM teepast2 WHERE date >= ? AND date <= ? AND courseName = ? AND " +
                    "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ?)");

         pstmt2.clearParameters();        // get count from teepast
         pstmt2.setLong(1, date1);
         pstmt2.setLong(2, date2);
         pstmt2.setString(3, course);
         pstmt2.setString(4, user);
         pstmt2.setString(5, user);
         pstmt2.setString(6, user);
         pstmt2.setString(7, user);
         rs2 = pstmt2.executeQuery();

         while (rs2.next()) {

            event = rs2.getString("event");
            userg1 = rs2.getString("userg1");
            userg2 = rs2.getString("userg2");
            userg3 = rs2.getString("userg3");
            userg4 = rs2.getString("userg4");
            show1 = rs2.getInt("show1");
            show2 = rs2.getInt("show2");
            show3 = rs2.getInt("show3");
            show4 = rs2.getInt("show4");

            if (!event.startsWith( event1 ) && !event.startsWith( event2 ) && !event.startsWith( event3 ) && 
                !event.startsWith( event4 ) && !event.startsWith( event5 ) && !event.startsWith( event6 ) &&
                !event.startsWith( event7 )) { 

               if (userg1.equals( user ) && show1 == 1) {

                  guests++;           // increment guest count if user had a guest that played
               }

               if (userg2.equals( user ) && show2 == 1) {

                  guests++;           // increment guest count if user had a guest that played
               }

               if (userg3.equals( user ) && show3 == 1) {

                  guests++;           // increment guest count if user had a guest that played
               }

               if (userg4.equals( user ) && show4 == 1) {

                  guests++;           // increment guest count if user had a guest that played
               }
            }

         }
         pstmt2.close();

         //
         //  Now check teecurr (exclude this request)
         //
         pstmt2 = con.prepareStatement (
            "SELECT event, userg1, userg2, userg3, userg4 " +
            "FROM teecurr2 WHERE date >= ? AND date <= ? AND date != ? AND courseName = ? AND " +
                    "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ?)");

         pstmt2.clearParameters();        // get count from teecurr
         pstmt2.setLong(1, date1);
         pstmt2.setLong(2, date2);
         pstmt2.setLong(3, slotParms.date);    // this request's date
         pstmt2.setString(4, course);
         pstmt2.setString(5, user);
         pstmt2.setString(6, user);
         pstmt2.setString(7, user);
         pstmt2.setString(8, user);
         rs2 = pstmt2.executeQuery();

         while (rs2.next()) {

            event = rs2.getString("event");
            userg1 = rs2.getString("userg1");
            userg2 = rs2.getString("userg2");
            userg3 = rs2.getString("userg3");
            userg4 = rs2.getString("userg4");

            if (!event.startsWith( event1 ) && !event.startsWith( event2 ) && !event.startsWith( event3 ) &&
                !event.startsWith( event4 ) && !event.startsWith( event5 ) && !event.startsWith( event6 ) &&
                !event.startsWith( event7 )) {

               if (userg1.equals( user )) {

                  guests++;           // increment guest count if user had a guest that played
               }

               if (userg2.equals( user )) {

                  guests++;           // increment guest count if user had a guest that played
               }

               if (userg3.equals( user )) {

                  guests++;           // increment guest count if user had a guest that played
               }

               if (userg4.equals( user )) {

                  guests++;           // increment guest count if user had a guest that played
               }
            }

         }
         pstmt2.close();

      }
      pstmt1.close();

   }
   catch (Exception ignore) {
   }

   return(guests);
 }


/**
 //********************************************************************************************************
 //
 //  checkMG2 - for Course #2
 //
 //        Check Member/Guest times on weekends and holidays
 //
 //    called by:  Member_slot
 //                Proshop_slot
 //
 //********************************************************************************************************
 **/

 public static boolean checkMG2(parmSlot slotParms, Connection con) {


   boolean error = false;

   String day = slotParms.day;
     
   long date = slotParms.date;


   //
   //  Only check if Course #2 and a weekend or holiday
   //
   if (slotParms.course.equals( course2 )) {

      //
      //  First check for w/e or holiday. 
      //
      if (day.equals( "Saturday" ) || day.equals( "Sunday" ) || date == Hdate1 ||
          date == Hdate2 || date == Hdate3) {

        //
        //  Must be members and guests - (M G G) or (M G G G) or (M G G M) or (M M G G)
        //
        if (slotParms.guests < 2) {      // if less than 2 guests were included

           error = true;

        } else {

           if (!slotParms.g1.equals( "" )) {         // if player 1 is a guest - must be member

              error = true;

           } else {                                  // Player 1 is a member

              if (!slotParms.g2.equals( "" )) {      // if player 2 is a guest

                 if (slotParms.g3.equals( "" )) {    // then player 3 must be a guest

                    error = true;                    // error if not
                 }

              } else {                               // Player 2 is also a member

                 if (slotParms.g3.equals( "" ) || slotParms.g4.equals( "" )) { // then players 3 & 4 must be guests

                    error = true;                    // error if not
                 }
              }
           }
        }
      }
   }

   return(error);
 }


/**
 //********************************************************************************************************
 //
 //  checkMG2b - for Course #2
 //
 //        Check Member/Guest times on weekends and holidays - 1 guest per tee time at 1:00 and 2:00 PM
 //
 //    called by:  Member_slot
 //                Proshop_slot
 //
 //********************************************************************************************************
 **/

 public static boolean checkMG2b(parmSlot slotParms, Connection con) {


   boolean error = false;

   String day = slotParms.day;

   long date = slotParms.date;


   //
   //  Only check if Course #2 and a weekend or holiday at 1 or 2 PM
   //
   if (slotParms.course.equals( "No 2" ) && (slotParms.time == 1300 || slotParms.time == 1400)) {

      //
      //  First check for w/e or holiday.
      //
      if (day.equals( "Saturday" ) || day.equals( "Sunday" ) || date == Hdate1 ||
          date == Hdate2 || date == Hdate3) {

        //
        //  Must be at least 1 guest
        //
        if (slotParms.guests == 0) {      // if no guests were included

           error = true;
        }
      }
   }

   return(error);
 }


/**
 //********************************************************************************************************
 //
 //  checkContingent  - Contingent Restrictions per Course
 //
 //        Check for custom Member Type Restrictions.
 //
 //    called by:  Member_slot
 //                Proshop_slot
 //
 //********************************************************************************************************
 **/

 public static int checkContingent(parmSlot slotParms) {


   int error = 0;

   boolean ok = false;


   //
   //  Course #1 Restrictions
   //
   if (slotParms.course.equals( course1 )) {

      //
      //  Check all contingent restrictions for course #1 
      //
      if (slotParms.day.equals( "Monday" )) {

         if (slotParms.mtype1.equals( FM8t11 ) || slotParms.mtype2.equals( FM8t11 ) ||
             slotParms.mtype3.equals( FM8t11 ) || slotParms.mtype4.equals( FM8t11 ) ||
             slotParms.mtype5.equals( FM8t11 )) {       // if any 'FM 8 thru 11' mtypes included
       
            //
            //  'FM 8 thru 11' must be accompanied by an adult
            //
            ok = false; 
              
            if (slotParms.mtype1.equals( Rmem ) || slotParms.mtype2.equals( Rmem ) ||
                slotParms.mtype3.equals( Rmem ) || slotParms.mtype4.equals( Rmem ) ||
                slotParms.mtype5.equals( Rmem )) {
              
               ok = true;      // then he/she's ok
            }
            if (slotParms.mtype1.equals( Rspouse ) || slotParms.mtype2.equals( Rspouse ) ||
                slotParms.mtype3.equals( Rspouse ) || slotParms.mtype4.equals( Rspouse ) ||
                slotParms.mtype5.equals( Rspouse )) {

               ok = true;      // then he/she's ok
            }
            if (slotParms.mtype1.equals( Jrmem ) || slotParms.mtype2.equals( Jrmem ) ||
                slotParms.mtype3.equals( Jrmem ) || slotParms.mtype4.equals( Jrmem ) ||
                slotParms.mtype5.equals( Jrmem )) {

               ok = true;      // then he/she's ok
            }
            if (slotParms.mtype1.equals( Jrspouse ) || slotParms.mtype2.equals( Jrspouse ) ||
                slotParms.mtype3.equals( Jrspouse ) || slotParms.mtype4.equals( Jrspouse ) ||
                slotParms.mtype5.equals( Jrspouse )) {

               ok = true;      // then he/she's ok
            }
            if (slotParms.mtype1.equals( RPmem ) || slotParms.mtype2.equals( RPmem ) ||
                slotParms.mtype3.equals( RPmem ) || slotParms.mtype4.equals( RPmem ) ||
                slotParms.mtype5.equals( RPmem )) {

               ok = true;      // then he/she's ok
            }
            if (slotParms.mtype1.equals( RPspouse ) || slotParms.mtype2.equals( RPspouse ) ||
                slotParms.mtype3.equals( RPspouse ) || slotParms.mtype4.equals( RPspouse ) ||
                slotParms.mtype5.equals( RPspouse )) {

               ok = true;      // then he/she's ok
            }
  
            if (ok == false) {

               error = 1;            // set return error code

               if (slotParms.mtype1.equals( FM8t11 )) {      // find player in error
                  
                  slotParms.player = slotParms.player1;      // save player name
                    
               } else {

                  if (slotParms.mtype2.equals( FM8t11 )) {

                     slotParms.player = slotParms.player2;      // save player name

                  } else {

                     if (slotParms.mtype3.equals( FM8t11 )) {

                        slotParms.player = slotParms.player3;      // save player name

                     } else {

                        if (slotParms.mtype4.equals( FM8t11 )) {

                           slotParms.player = slotParms.player4;      // save player name

                        } else {

                           slotParms.player = slotParms.player5;      // save player name
                        }
                     }
                  }
               }
            }
         }
      }
        
      //
      //  Continue checks if still ok 
      //
      if (error == 0) {
        
         //
         //   if Tues - Sun and after 2:29 PM
         //
         if (!slotParms.day.equals( "Monday" ) && slotParms.time > 1429) {  // if NOT Monday and after 2:29 PM

            if (slotParms.mtype1.equals( FM12and13 ) || slotParms.mtype2.equals( FM12and13 ) ||
                slotParms.mtype3.equals( FM12and13 ) || slotParms.mtype4.equals( FM12and13 ) ||
                slotParms.mtype5.equals( FM12and13 )) {       // if any 'FM 12 and 13' mtypes included

               //
               //  'FM 12 and 13' must be accompanied by an adult
               //
               ok = false;

               if (slotParms.mtype1.equals( Rmem ) || slotParms.mtype2.equals( Rmem ) ||
                   slotParms.mtype3.equals( Rmem ) || slotParms.mtype4.equals( Rmem ) ||
                   slotParms.mtype5.equals( Rmem )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( Rspouse ) || slotParms.mtype2.equals( Rspouse ) ||
                   slotParms.mtype3.equals( Rspouse ) || slotParms.mtype4.equals( Rspouse ) ||
                   slotParms.mtype5.equals( Rspouse )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( Jrmem ) || slotParms.mtype2.equals( Jrmem ) ||
                   slotParms.mtype3.equals( Jrmem ) || slotParms.mtype4.equals( Jrmem ) ||
                   slotParms.mtype5.equals( Jrmem )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( Jrspouse ) || slotParms.mtype2.equals( Jrspouse ) ||
                   slotParms.mtype3.equals( Jrspouse ) || slotParms.mtype4.equals( Jrspouse ) ||
                   slotParms.mtype5.equals( Jrspouse )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( RPmem ) || slotParms.mtype2.equals( RPmem ) ||
                   slotParms.mtype3.equals( RPmem ) || slotParms.mtype4.equals( RPmem ) ||
                   slotParms.mtype5.equals( RPmem )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( RPspouse ) || slotParms.mtype2.equals( RPspouse ) ||
                   slotParms.mtype3.equals( RPspouse ) || slotParms.mtype4.equals( RPspouse ) ||
                   slotParms.mtype5.equals( RPspouse )) {

                  ok = true;      // then he/she's ok
               }

               if (ok == false) {

                  error = 2;            // set return error code

                  if (slotParms.mtype1.equals( FM12and13 )) {      // find player in error

                     slotParms.player = slotParms.player1;      // save player name

                  } else {

                     if (slotParms.mtype2.equals( FM12and13 )) {

                        slotParms.player = slotParms.player2;      // save player name

                     } else {

                        if (slotParms.mtype3.equals( FM12and13 )) {

                           slotParms.player = slotParms.player3;      // save player name

                        } else {

                           if (slotParms.mtype4.equals( FM12and13 )) {

                              slotParms.player = slotParms.player4;      // save player name

                           } else {

                              slotParms.player = slotParms.player5;      // save player name
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      //
      //  Continue checks if still ok
      //
      if (error == 0) {

         //
         //   if Tues - Fri and before 11:00 AM
         //
         if (!slotParms.day.equals( "Saturday" ) && !slotParms.day.equals( "Sunday" ) &&
             !slotParms.day.equals( "Monday" ) && slotParms.time < 1100) {

            if (slotParms.mtype1.equals( FM14t16 ) || slotParms.mtype2.equals( FM14t16 ) ||
                slotParms.mtype3.equals( FM14t16 ) || slotParms.mtype4.equals( FM14t16 ) ||
                slotParms.mtype5.equals( FM14t16 )) {       // if any 'FM 14 thru 16' mtypes included

               //
               //  'FM 14 thru 16' must be accompanied by an adult male
               //
               ok = false;

               if (slotParms.mtype1.equals( Rmem ) || slotParms.mtype2.equals( Rmem ) ||
                   slotParms.mtype3.equals( Rmem ) || slotParms.mtype4.equals( Rmem ) ||
                   slotParms.mtype5.equals( Rmem )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( Jrmem ) || slotParms.mtype2.equals( Jrmem ) ||
                   slotParms.mtype3.equals( Jrmem ) || slotParms.mtype4.equals( Jrmem ) ||
                   slotParms.mtype5.equals( Jrmem )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( RPmem ) || slotParms.mtype2.equals( RPmem ) ||
                   slotParms.mtype3.equals( RPmem ) || slotParms.mtype4.equals( RPmem ) ||
                   slotParms.mtype5.equals( RPmem )) {

                  ok = true;      // then he/she's ok
               }

               if (ok == false) {

                  error = 3;            // set return error code

                  if (slotParms.mtype1.equals( FM14t16 )) {      // find player in error

                     slotParms.player = slotParms.player1;      // save player name

                  } else {

                     if (slotParms.mtype2.equals( FM14t16 )) {

                        slotParms.player = slotParms.player2;      // save player name

                     } else {

                        if (slotParms.mtype3.equals( FM14t16 )) {

                           slotParms.player = slotParms.player3;      // save player name

                        } else {

                           if (slotParms.mtype4.equals( FM14t16 )) {

                              slotParms.player = slotParms.player4;      // save player name

                           } else {

                              slotParms.player = slotParms.player5;      // save player name
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      //
      //  Continue checks if still ok
      //
      if (error == 0) {

         //
         //   if a Sunday or a Holiday and between 10:00 AM & 1:59 PM
         //
         if ((slotParms.date == Hdate1 || slotParms.date == Hdate2 || slotParms.date == Hdate3 ||
              slotParms.day.equals( "Sunday" )) && slotParms.time > 959 && slotParms.time < 1400) {

            if (slotParms.mtype1.equals( Rspouse ) || slotParms.mtype2.equals( Rspouse ) ||
                slotParms.mtype3.equals( Rspouse ) || slotParms.mtype4.equals( Rspouse ) ||
                slotParms.mtype5.equals( Rspouse ) ||
                slotParms.mtype1.equals( Jrspouse ) || slotParms.mtype2.equals( Jrspouse ) ||
                slotParms.mtype3.equals( Jrspouse ) || slotParms.mtype4.equals( Jrspouse ) ||
                slotParms.mtype5.equals( Jrspouse ) ||
                slotParms.mtype1.equals( RPspouse ) || slotParms.mtype2.equals( RPspouse ) ||
                slotParms.mtype3.equals( RPspouse ) || slotParms.mtype4.equals( RPspouse ) ||
                slotParms.mtype5.equals( RPspouse )) {

               //
               //  Spouses must be accompanied by an adult male
               //
               ok = false;

               if (slotParms.mtype1.equals( Rmem ) || slotParms.mtype2.equals( Rmem ) ||
                   slotParms.mtype3.equals( Rmem ) || slotParms.mtype4.equals( Rmem ) ||
                   slotParms.mtype5.equals( Rmem )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( Jrmem ) || slotParms.mtype2.equals( Jrmem ) ||
                   slotParms.mtype3.equals( Jrmem ) || slotParms.mtype4.equals( Jrmem ) ||
                   slotParms.mtype5.equals( Jrmem )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( RPmem ) || slotParms.mtype2.equals( RPmem ) ||
                   slotParms.mtype3.equals( RPmem ) || slotParms.mtype4.equals( RPmem ) ||
                   slotParms.mtype5.equals( RPmem )) {

                  ok = true;      // then he/she's ok
               }

               if (ok == false) {

                  error = 4;            // set return error code

                  if (slotParms.mtype1.equals( Rspouse ) || slotParms.mtype1.equals( Jrspouse ) ||
                      slotParms.mtype1.equals( RPspouse )) {      // find player in error

                     slotParms.player = slotParms.player1;      // save player name

                  } else {

                     if (slotParms.mtype2.equals( Rspouse ) || slotParms.mtype2.equals( Jrspouse ) ||
                         slotParms.mtype2.equals( RPspouse )) {      // find player in error

                        slotParms.player = slotParms.player2;      // save player name

                     } else {

                        if (slotParms.mtype3.equals( Rspouse ) || slotParms.mtype3.equals( Jrspouse ) ||
                            slotParms.mtype3.equals( RPspouse )) {      // find player in error

                           slotParms.player = slotParms.player3;      // save player name

                        } else {

                           if (slotParms.mtype4.equals( Rspouse ) || slotParms.mtype4.equals( Jrspouse ) ||
                               slotParms.mtype4.equals( RPspouse )) {      // find player in error

                              slotParms.player = slotParms.player4;      // save player name

                           } else {

                              slotParms.player = slotParms.player5;      // save player name
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      //
      //  Continue checks if still ok
      //
      if (error == 0) {

         //
         //   if a Holiday OR Sunday and between 10:00 AM & 1:59 PM
         //
         if ((slotParms.date == Hdate1 || slotParms.date == Hdate2 || slotParms.date == Hdate3 ||
              slotParms.day.equals( "Sunday" )) && slotParms.time > 959 && slotParms.time < 1400) {

            if (slotParms.mtype1.equals( FM17o ) || slotParms.mtype2.equals( FM17o ) ||
                slotParms.mtype3.equals( FM17o ) || slotParms.mtype4.equals( FM17o ) ||
                slotParms.mtype5.equals( FM17o )) {

               //
               //  "FM 17 and over" must be accompanied by an adult male
               //
               ok = false;

               if (slotParms.mtype1.equals( Rmem ) || slotParms.mtype2.equals( Rmem ) ||
                   slotParms.mtype3.equals( Rmem ) || slotParms.mtype4.equals( Rmem ) ||
                   slotParms.mtype5.equals( Rmem )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( Jrmem ) || slotParms.mtype2.equals( Jrmem ) ||
                   slotParms.mtype3.equals( Jrmem ) || slotParms.mtype4.equals( Jrmem ) ||
                   slotParms.mtype5.equals( Jrmem )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( RPmem ) || slotParms.mtype2.equals( RPmem ) ||
                   slotParms.mtype3.equals( RPmem ) || slotParms.mtype4.equals( RPmem ) ||
                   slotParms.mtype5.equals( RPmem )) {

                  ok = true;      // then he/she's ok
               }

               if (ok == false) {

                  error = 5;            // set return error code
                    
                  if (slotParms.mtype1.equals( FM17o )) {      // find player in error

                     slotParms.player = slotParms.player1;      // save player name

                  } else {

                     if (slotParms.mtype2.equals( FM17o )) {

                        slotParms.player = slotParms.player2;      // save player name

                     } else {

                        if (slotParms.mtype3.equals( FM17o )) {

                           slotParms.player = slotParms.player3;      // save player name

                        } else {

                           if (slotParms.mtype4.equals( FM17o )) {

                              slotParms.player = slotParms.player4;      // save player name

                           } else {

                              slotParms.player = slotParms.player5;      // save player name
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }         // end of Course #1


   //
   //  Course #2 Restrictions
   //
   if (slotParms.course.equals( course2 )) {

      //
      //  Check all contingent restrictions for course #2
      //
      //
      //   if a Holiday OR W/E and after Noon
      //
      if ((slotParms.date == Hdate1 || slotParms.date == Hdate2 || slotParms.date == Hdate3 ||
           slotParms.day.equals( "Saturday" ) || slotParms.day.equals( "Sunday" )) && slotParms.time > 1159) {

         if (slotParms.mtype1.equals( FM12and13 ) || slotParms.mtype2.equals( FM12and13 ) ||
             slotParms.mtype3.equals( FM12and13 ) || slotParms.mtype4.equals( FM12and13 ) ||
             slotParms.mtype5.equals( FM12and13 )) {

            //
            //  'FM 12 & 13' must be accompanied by an adult male
            //
            ok = false;

            if (slotParms.mtype1.equals( Rmem ) || slotParms.mtype2.equals( Rmem ) ||
                slotParms.mtype3.equals( Rmem ) || slotParms.mtype4.equals( Rmem ) ||
                slotParms.mtype5.equals( Rmem )) {

               ok = true;      // then he/she's ok
            }
            if (slotParms.mtype1.equals( Jrmem ) || slotParms.mtype2.equals( Jrmem ) ||
                slotParms.mtype3.equals( Jrmem ) || slotParms.mtype4.equals( Jrmem ) ||
                slotParms.mtype5.equals( Jrmem )) {

               ok = true;      // then he/she's ok
            }
            if (slotParms.mtype1.equals( RPmem ) || slotParms.mtype2.equals( RPmem ) ||
                slotParms.mtype3.equals( RPmem ) || slotParms.mtype4.equals( RPmem ) ||
                slotParms.mtype5.equals( RPmem )) {

               ok = true;      // then he/she's ok
            }

            if (ok == false) {

               error = 6;            // set return error code

               if (slotParms.mtype1.equals( FM12and13 )) {      // find player in error

                  slotParms.player = slotParms.player1;      // save player name

               } else {

                  if (slotParms.mtype2.equals( FM12and13 )) {

                     slotParms.player = slotParms.player2;      // save player name

                  } else {

                     if (slotParms.mtype3.equals( FM12and13 )) {

                        slotParms.player = slotParms.player3;      // save player name

                     } else {

                        if (slotParms.mtype4.equals( FM12and13 )) {

                           slotParms.player = slotParms.player4;      // save player name

                        } else {

                           slotParms.player = slotParms.player5;      // save player name
                        }
                     }
                  }
               }
            }
         }
      }

      //
      //  Continue checks if still ok
      //
      if (error == 0) {

         if (slotParms.mtype1.equals( FM8t11 ) || slotParms.mtype2.equals( FM8t11 ) ||
             slotParms.mtype3.equals( FM8t11 ) || slotParms.mtype4.equals( FM8t11 ) ||
             slotParms.mtype5.equals( FM8t11 )) {

            //
            //  "FM 8 thru 11" must be always accompanied by an adult 
            //
            ok = false;

            if (slotParms.mtype1.equals( Rmem ) || slotParms.mtype2.equals( Rmem ) ||
                slotParms.mtype3.equals( Rmem ) || slotParms.mtype4.equals( Rmem ) ||
                slotParms.mtype5.equals( Rmem )) {

               ok = true;      // then he/she's ok
            }
            if (slotParms.mtype1.equals( Rspouse ) || slotParms.mtype2.equals( Rspouse ) ||
                slotParms.mtype3.equals( Rspouse ) || slotParms.mtype4.equals( Rspouse ) ||
                slotParms.mtype5.equals( Rspouse )) {

               ok = true;      // then he/she's ok
            }
            if (slotParms.mtype1.equals( Jrmem ) || slotParms.mtype2.equals( Jrmem ) ||
                slotParms.mtype3.equals( Jrmem ) || slotParms.mtype4.equals( Jrmem ) ||
                slotParms.mtype5.equals( Jrmem )) {

               ok = true;      // then he/she's ok
            }
            if (slotParms.mtype1.equals( Jrspouse ) || slotParms.mtype2.equals( Jrspouse ) ||
                slotParms.mtype3.equals( Jrspouse ) || slotParms.mtype4.equals( Jrspouse ) ||
                slotParms.mtype5.equals( Jrspouse )) {

               ok = true;      // then he/she's ok
            }
            if (slotParms.mtype1.equals( RPmem ) || slotParms.mtype2.equals( RPmem ) ||
                slotParms.mtype3.equals( RPmem ) || slotParms.mtype4.equals( RPmem ) ||
                slotParms.mtype5.equals( RPmem )) {

               ok = true;      // then he/she's ok
            }
            if (slotParms.mtype1.equals( RPspouse ) || slotParms.mtype2.equals( RPspouse ) ||
                slotParms.mtype3.equals( RPspouse ) || slotParms.mtype4.equals( RPspouse ) ||
                slotParms.mtype5.equals( RPspouse )) {

               ok = true;      // then he/she's ok
            }

            if (ok == false) {

               error = 7;            // set return error code

               if (slotParms.mtype1.equals( FM8t11 )) {      // find player in error

                  slotParms.player = slotParms.player1;      // save player name

               } else {

                  if (slotParms.mtype2.equals( FM8t11 )) {

                     slotParms.player = slotParms.player2;      // save player name

                  } else {

                     if (slotParms.mtype3.equals( FM8t11 )) {

                        slotParms.player = slotParms.player3;      // save player name

                     } else {

                        if (slotParms.mtype4.equals( FM8t11 )) {

                           slotParms.player = slotParms.player4;      // save player name

                        } else {

                           slotParms.player = slotParms.player5;      // save player name
                        }
                     }
                  }
               }
            }
         }
      }
   }         // end of Course #2


   //
   //  Course #3 Restrictions
   //
   if (slotParms.course.equals( course3 )) {

      //
      //  Check all contingent restrictions for course #3
      //
      //
      //   if Saturday and after 2:00 PM
      //
      if (slotParms.day.equals( "Saturday" ) && slotParms.time > 1400) {

         if (slotParms.mtype1.equals( Rspouse ) || slotParms.mtype2.equals( Rspouse ) ||
             slotParms.mtype3.equals( Rspouse ) || slotParms.mtype4.equals( Rspouse ) ||
             slotParms.mtype5.equals( Rspouse ) ||
             slotParms.mtype1.equals( Jrspouse ) || slotParms.mtype2.equals( Jrspouse ) ||
             slotParms.mtype3.equals( Jrspouse ) || slotParms.mtype4.equals( Jrspouse ) ||
             slotParms.mtype5.equals( Jrspouse ) ||
             slotParms.mtype1.equals( RPspouse ) || slotParms.mtype2.equals( RPspouse ) ||
             slotParms.mtype3.equals( RPspouse ) || slotParms.mtype4.equals( RPspouse ) ||
             slotParms.mtype5.equals( RPspouse )) {

            //
            //  All spouses must be accompanied by an adult male
            //
            ok = false;

            if (slotParms.mtype1.equals( Rmem ) || slotParms.mtype2.equals( Rmem ) ||
                slotParms.mtype3.equals( Rmem ) || slotParms.mtype4.equals( Rmem ) ||
                slotParms.mtype5.equals( Rmem )) {

               ok = true;      // then he/she's ok
            }
            if (slotParms.mtype1.equals( Jrmem ) || slotParms.mtype2.equals( Jrmem ) ||
                slotParms.mtype3.equals( Jrmem ) || slotParms.mtype4.equals( Jrmem ) ||
                slotParms.mtype5.equals( Jrmem )) {

               ok = true;      // then he/she's ok
            }
            if (slotParms.mtype1.equals( RPmem ) || slotParms.mtype2.equals( RPmem ) ||
                slotParms.mtype3.equals( RPmem ) || slotParms.mtype4.equals( RPmem ) ||
                slotParms.mtype5.equals( RPmem )) {

               ok = true;      // then he/she's ok
            }

            if (ok == false) {

               error = 8;            // set return error code

               if (slotParms.mtype1.equals( Rspouse ) || slotParms.mtype1.equals( Jrspouse ) ||
                   slotParms.mtype1.equals( RPspouse )) {      // find player in error

                  slotParms.player = slotParms.player1;      // save player name

               } else {

                  if (slotParms.mtype2.equals( Rspouse ) || slotParms.mtype2.equals( Jrspouse ) ||
                      slotParms.mtype2.equals( RPspouse )) {      // find player in error

                     slotParms.player = slotParms.player2;      // save player name

                  } else {

                     if (slotParms.mtype3.equals( Rspouse ) || slotParms.mtype3.equals( Jrspouse ) ||
                         slotParms.mtype3.equals( RPspouse )) {      // find player in error

                        slotParms.player = slotParms.player3;      // save player name

                     } else {

                        if (slotParms.mtype4.equals( Rspouse ) || slotParms.mtype4.equals( Jrspouse ) ||
                            slotParms.mtype4.equals( RPspouse )) {      // find player in error

                           slotParms.player = slotParms.player4;      // save player name

                        } else {

                           slotParms.player = slotParms.player5;      // save player name
                        }
                     }
                  }
               }
            }
         }
      }

      //
      //  Continue checks if still ok
      //
      if (error == 0) {

         //
         //   if Sunday and between 2:00 PM and 2:30 PM
         //
         if (slotParms.day.equals( "Sunday" ) && slotParms.time > 1400 && slotParms.time < 1431) {

            if (slotParms.mtype1.equals( Rspouse ) || slotParms.mtype2.equals( Rspouse ) ||
                slotParms.mtype3.equals( Rspouse ) || slotParms.mtype4.equals( Rspouse ) ||
                slotParms.mtype5.equals( Rspouse ) ||
                slotParms.mtype1.equals( Jrspouse ) || slotParms.mtype2.equals( Jrspouse ) ||
                slotParms.mtype3.equals( Jrspouse ) || slotParms.mtype4.equals( Jrspouse ) ||
                slotParms.mtype5.equals( Jrspouse ) ||
                slotParms.mtype1.equals( RPspouse ) || slotParms.mtype2.equals( RPspouse ) ||
                slotParms.mtype3.equals( RPspouse ) || slotParms.mtype4.equals( RPspouse ) ||
                slotParms.mtype5.equals( RPspouse )) {

               //
               //  All spouses must be accompanied by an adult male
               //
               ok = false;

               if (slotParms.mtype1.equals( Rmem ) || slotParms.mtype2.equals( Rmem ) ||
                   slotParms.mtype3.equals( Rmem ) || slotParms.mtype4.equals( Rmem ) ||
                   slotParms.mtype5.equals( Rmem )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( Jrmem ) || slotParms.mtype2.equals( Jrmem ) ||
                   slotParms.mtype3.equals( Jrmem ) || slotParms.mtype4.equals( Jrmem ) ||
                   slotParms.mtype5.equals( Jrmem )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( RPmem ) || slotParms.mtype2.equals( RPmem ) ||
                   slotParms.mtype3.equals( RPmem ) || slotParms.mtype4.equals( RPmem ) ||
                   slotParms.mtype5.equals( RPmem )) {

                  ok = true;      // then he/she's ok
               }

               if (ok == false) {

                  error = 9;            // set return error code

                  if (slotParms.mtype1.equals( Rspouse ) || slotParms.mtype1.equals( Jrspouse ) ||
                      slotParms.mtype1.equals( RPspouse )) {      // find player in error

                     slotParms.player = slotParms.player1;      // save player name

                  } else {

                     if (slotParms.mtype2.equals( Rspouse ) || slotParms.mtype2.equals( Jrspouse ) ||
                         slotParms.mtype2.equals( RPspouse )) {      // find player in error

                        slotParms.player = slotParms.player2;      // save player name

                     } else {

                        if (slotParms.mtype3.equals( Rspouse ) || slotParms.mtype3.equals( Jrspouse ) ||
                            slotParms.mtype3.equals( RPspouse )) {      // find player in error

                           slotParms.player = slotParms.player3;      // save player name

                        } else {

                           if (slotParms.mtype4.equals( Rspouse ) || slotParms.mtype4.equals( Jrspouse ) ||
                               slotParms.mtype4.equals( RPspouse )) {      // find player in error

                              slotParms.player = slotParms.player4;      // save player name

                           } else {

                              slotParms.player = slotParms.player5;      // save player name
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      //
      //  Continue checks if still ok
      //
      if (error == 0) {

         //
         //   if a Holiday OR W/E and after 2:00 PM
         //
         if ((slotParms.date == Hdate1 || slotParms.date == Hdate2 || slotParms.date == Hdate3 ||
              slotParms.day.equals( "Saturday" ) || slotParms.day.equals( "Sunday" )) && slotParms.time > 1400) {

            if (slotParms.mtype1.equals( FM17o ) || slotParms.mtype2.equals( FM17o ) ||
                slotParms.mtype3.equals( FM17o ) || slotParms.mtype4.equals( FM17o ) ||
                slotParms.mtype5.equals( FM17o )) {

               //
               //  'FM 12 & 13' must be accompanied by an adult male
               //
               ok = false;

               if (slotParms.mtype1.equals( Rmem ) || slotParms.mtype2.equals( Rmem ) ||
                   slotParms.mtype3.equals( Rmem ) || slotParms.mtype4.equals( Rmem ) ||
                   slotParms.mtype5.equals( Rmem )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( Jrmem ) || slotParms.mtype2.equals( Jrmem ) ||
                   slotParms.mtype3.equals( Jrmem ) || slotParms.mtype4.equals( Jrmem ) ||
                   slotParms.mtype5.equals( Jrmem )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( RPmem ) || slotParms.mtype2.equals( RPmem ) ||
                   slotParms.mtype3.equals( RPmem ) || slotParms.mtype4.equals( RPmem ) ||
                   slotParms.mtype5.equals( RPmem )) {

                  ok = true;      // then he/she's ok
               }

               if (ok == false) {

                  error = 10;            // set return error code

                  if (slotParms.mtype1.equals( FM17o )) {      // find player in error

                     slotParms.player = slotParms.player1;      // save player name

                  } else {

                     if (slotParms.mtype2.equals( FM17o )) {

                        slotParms.player = slotParms.player2;      // save player name

                     } else {

                        if (slotParms.mtype3.equals( FM17o )) {

                           slotParms.player = slotParms.player3;      // save player name

                        } else {

                           if (slotParms.mtype4.equals( FM17o )) {

                              slotParms.player = slotParms.player4;      // save player name

                           } else {

                              slotParms.player = slotParms.player5;      // save player name
                           }
                        }
                     }
                  }
               }
            }
         }
      }
        
      //
      //  Continue checks if still ok
      //
      if (error == 0) {

         //
         //   if a Holiday OR Tues - Sun, and after 4:00 PM
         //
         if ((slotParms.date == Hdate1 || slotParms.date == Hdate2 || slotParms.date == Hdate3 ||
              !slotParms.day.equals( "Monday" )) && slotParms.time > 1600) {

            if (slotParms.mtype1.equals( FM14t16 ) || slotParms.mtype2.equals( FM14t16 ) ||
                slotParms.mtype3.equals( FM14t16 ) || slotParms.mtype4.equals( FM14t16 ) ||
                slotParms.mtype5.equals( FM14t16 )) {

               //
               //  'FM 17 & Over' must be accompanied by an adult
               //
               ok = false;

               if (slotParms.mtype1.equals( Rmem ) || slotParms.mtype2.equals( Rmem ) ||
                   slotParms.mtype3.equals( Rmem ) || slotParms.mtype4.equals( Rmem ) ||
                   slotParms.mtype5.equals( Rmem )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( Rspouse ) || slotParms.mtype2.equals( Rspouse ) ||
                   slotParms.mtype3.equals( Rspouse ) || slotParms.mtype4.equals( Rspouse ) ||
                   slotParms.mtype5.equals( Rspouse )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( Jrmem ) || slotParms.mtype2.equals( Jrmem ) ||
                   slotParms.mtype3.equals( Jrmem ) || slotParms.mtype4.equals( Jrmem ) ||
                   slotParms.mtype5.equals( Jrmem )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( Jrspouse ) || slotParms.mtype2.equals( Jrspouse ) ||
                   slotParms.mtype3.equals( Jrspouse ) || slotParms.mtype4.equals( Jrspouse ) ||
                   slotParms.mtype5.equals( Jrspouse )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( RPmem ) || slotParms.mtype2.equals( RPmem ) ||
                   slotParms.mtype3.equals( RPmem ) || slotParms.mtype4.equals( RPmem ) ||
                   slotParms.mtype5.equals( RPmem )) {

                  ok = true;      // then he/she's ok
               }
               if (slotParms.mtype1.equals( RPspouse ) || slotParms.mtype2.equals( RPspouse ) ||
                   slotParms.mtype3.equals( RPspouse ) || slotParms.mtype4.equals( RPspouse ) ||
                   slotParms.mtype5.equals( RPspouse )) {

                  ok = true;      // then he/she's ok
               }

               if (ok == false) {

                  error = 11;            // set return error code

                  if (slotParms.mtype1.equals( FM14t16 )) {      // find player in error

                     slotParms.player = slotParms.player1;      // save player name

                  } else {

                     if (slotParms.mtype2.equals( FM14t16 )) {

                        slotParms.player = slotParms.player2;      // save player name

                     } else {

                        if (slotParms.mtype3.equals( FM14t16 )) {

                           slotParms.player = slotParms.player3;      // save player name

                        } else {

                           if (slotParms.mtype4.equals( FM14t16 )) {

                              slotParms.player = slotParms.player4;      // save player name

                           } else {

                              slotParms.player = slotParms.player5;      // save player name
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }         // end of Course #3

   return(error);       // error indicates which error
 }


}  // end of medinahCustom class

