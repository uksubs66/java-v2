/**************************************************************************************************************
 *   congressionalCustom.java:  This will provide the common methods for processing custom requests for Congressional CC.
 *
 *       called by:  Hotel_sheet
 *                   Member_sheet
 *
 *
 *   created:  3/20/2007   Paul S.
 *
 *
 *   last updated:
 *
 *      2/13/12  getFullCourseName no longer needed - removed.
 *      3/03/11  Updated getCourseLabel to opt out the Jones course instead of the Hills course.
 *      3/10/10  Add 2010 exceptions to check2Somes (case 1060).
 *      4/17/09  Allow for 3rd course (Hills Course) to be used during course renovation.
 *      3/07/08  Remove custom dates in getCourseLabel - not needed this year.
 *      4/25/07  Add custom dates in getCourseLabel (case #1134).
 *      4/11/07  Add checkAdvTimes. Check max allowed advanced guest times per year.
 *      4/10/07  Add checkCertGuests. Check guest types for Dependents.
 *      4/10/07  Add checkJrAGuests. Check number of guests for 'Junior A' mship types.
 *             
 *
 **************************************************************************************************************
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


public class congressionalCustom {

    //
    // Returns a String containing the physical course name (gold or blue)
    //
    // Case #: 00001061 - Tee sheet labeling
    // The Open Course will be Blue Course on Even Days and Gold Course on Odd Days
    // The Club Course will be Blue Course on Odd Days and the Gold Course on Even days
    //
    //
    //   2/13/2012 NO LONGER USED !!!!!!!!!!!!!!!!!!!!!!!!!!!
    //
    //
 public static String getCourseLabel(long date, int day, String courseName) {
    
    String courseLabel = "";
    
   
    //  long shortDate = date - ((date / 10000) * 10000);       // get mmdd 


    //  if (!courseName.equalsIgnoreCase("-ALL-") && !courseName.equalsIgnoreCase("Jones Course")) {
        
        //  if (day % 2 == 0) {

            // Even days Open Course is Blue and Club Course is Gold
            //  if (courseName.equals("Open Course")) {
             
               /*
               if (shortDate == 518) {
                 
                  courseLabel = "Gold";           // opposite on 5/18/2007
  
               } else {
               */
         //           courseLabel = "Blue";
             //   }
                     
        //      } else {

               /*
               if (shortDate == 518) {

                  courseLabel = "Blue";            // opposite on 5/18/2007

               } else {
               */
               
        //            courseLabel = "Gold";
             // }
        //      }

      //    } else {

            // Odd days Open Course is Gold and Club Course is Blue
         //     if (courseName.equals("Open Course")) {

               /*
               if (shortDate == 517 || shortDate == 519) {

                  courseLabel = "Blue";           // opposite on 5/17, 5/19/2007

               } else {
               */
               
       //             courseLabel = "Gold";
            // }
      //        } else {

               /*
               if (shortDate == 517 || shortDate == 519) {

                  courseLabel = "Gold";           // opposite on 5/17, 5/19/2007

               } else {
               */
               
          //          courseLabel = "Blue";
             // }
       //       }
     //     }
   //   }
    
    return courseLabel;
    
 } // end getCourseLabel
 
 
 //
 // Returns a String containing the coure name w/ the course label appended to it
 //
 //   2/13/2012 NO LONGER USED - change to simply return the name passed !!!!!!!!!!!!!!!!!!!!!!!!!!!
 //
 public static String getFullCourseName(long date, int day, String courseName) {
    
     String courseLabel = courseName;
     
     /*
     if (!courseName.equalsIgnoreCase("-ALL-") && !courseName.equalsIgnoreCase("Hills Course")) {
     
        StringBuffer tmp = new StringBuffer(courseName);
        tmp.append(" ");
        tmp.append(getCourseLabel(date, day, courseName));
     
        courseLabel = tmp.toString();
     }
      */
     
     return courseLabel;
     
 } // end getFullCourseName


 // *********************************************************
 //
 //  Congressional CC - check if guest type is allowed
 //
 //     Certified Dependent Member Types can only have one
 //     'Cert Jr Guest' guest per month (and 1 per tee time). 
 //
 //     There is a Guest Quota restriction in place to check the monthly quota!
 //
 // *********************************************************

 public static boolean checkCertGuests(parmSlot slotParms) {


   boolean error = false;


   //
   //  'Cert Jr Guest' must follow a Certified Dependent only
   //
   if (slotParms.player1.startsWith( "Cert Jr Guest" )) {

      error = true;         // illegal

   } else {

      if (slotParms.player2.startsWith( "Cert Jr Guest" )) {

         if (!slotParms.mtype1.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player3.startsWith( "Cert Jr Guest" )) {     // if player3 is a Cert Jr Guest

         if (!slotParms.mtype2.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player4.startsWith( "Cert Jr Guest" )) {     // if player4 is a Cert Jr Guest

         if (!slotParms.mtype3.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }

      if (error == false && slotParms.player5.startsWith( "Cert Jr Guest" )) {     // if player5 is a Cert Jr Guest

         if (!slotParms.mtype4.equals( "Certified Dependent" )) {

            error = true;         // illegal
         }
      }
   }

   return(error);
 }


 // *********************************************************
 //
 //  Congressional CC - check number of guests per 'Junior A' mships.
 //
 //     'Junior A' Membership Types can only have one
 //     guest per member on the Open Course Tues - Fri.
 //
 // *********************************************************

 public static boolean checkJrAGuests(parmSlot slotParms) {


   boolean error = false;

   /*        No longer used as of 3/07/12 BP
    * 
   String day = slotParms.day;

   String mship = "Junior A";


   //
   //  Check if Open Course and Tues - Fri
   //
   if (slotParms.course.equals( "Open Course" ) && (day.equals( "Tuesday" ) || day.equals( "Wednesday" ) ||
       day.equals( "Thursday" ) || day.equals( "Friday" ))) {

      //
      //  Now check if any members are 'Junior A' mship types
      //
      if (slotParms.mship1.equals( mship )) {
     
         if (slotParms.userg2.equals( slotParms.user1 ) &&
             (slotParms.userg3.equals( slotParms.user1 ) || slotParms.userg4.equals( slotParms.user1 ) || 
              slotParms.userg5.equals( slotParms.user1 ))) {   // if more than one guest for this user

            error = true;
         }
      }

      if (slotParms.mship2.equals( mship ) && error == false) {

         if (slotParms.userg3.equals( slotParms.user2 ) &&
             (slotParms.userg4.equals( slotParms.user2 ) || slotParms.userg5.equals( slotParms.user2 ))) {  // if more than one guest for this user

            error = true;
         }
      }

      if (slotParms.mship3.equals( mship ) && error == false) {

         if (slotParms.userg4.equals( slotParms.user3 ) && slotParms.userg5.equals( slotParms.user3 )) {  // if more than one guest for this user

            error = true;
         }
      }
   }
    */

   return(error);
 }


 // *********************************************************
 //  Congressional CC - check if member has already booked 4
 //                     advance times this year.
 // *********************************************************

 public static boolean checkAdvTimes(parmSlot slotParms, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean error = false;

   /*        No longer used as of 3/07/12 BP
    * 
   String [] mNumA = new String [5];        // array to hold the player's mnums
   String [] playerA = new String [5];      // array to hold the player's names

   String course = "Open Course";        // only enforce on this course

   long date = 0;
   int time = 0;
   int fb = 0;
   int count = 0;
   int days = 9;                        // allowed days in adv - more than this is an advance time


   //
   //  Put mNums in array for loop
   //
   mNumA[0] = slotParms.mNum1;
   mNumA[1] = slotParms.mNum2;
   mNumA[2] = slotParms.mNum3;
   mNumA[3] = slotParms.mNum4;
   mNumA[4] = slotParms.mNum5;
   playerA[0] = slotParms.player1;
   playerA[1] = slotParms.player2;
   playerA[2] = slotParms.player3;
   playerA[3] = slotParms.player4;
   playerA[4] = slotParms.player5;


   //
   //  Count the number of advance times for this family this year
   //
   try {

      loop1:
      for (int i = 0; i < 5; i++) {

         if (!mNumA[i].equals( "" )) {           // if this player is a member and member number exists

            count = 0;

            pstmt = con.prepareStatement (
               "SELECT date, time, fb " +
               "FROM teecurr2 " +
               "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND yy = ? AND courseName = ? AND custom_int > ?");

            pstmt.clearParameters();
            pstmt.setString(1, mNumA[i]);
            pstmt.setString(2, mNumA[i]);
            pstmt.setString(3, mNumA[i]);
            pstmt.setString(4, mNumA[i]);
            pstmt.setString(5, mNumA[i]);
            pstmt.setInt(6, slotParms.yy);
            pstmt.setString(7, course);
            pstmt.setInt(8, days);                 // check all times created after max allowed days in adv
            rs = pstmt.executeQuery();

            while (rs.next()) {

               date = rs.getLong("date");
               time = rs.getInt("time");
               fb = rs.getInt("fb");

               if (date != slotParms.date || time != slotParms.time || fb != slotParms.fb) {   // if not this tee time

                  count++;          // count it 
               }
            }

            pstmt.close();

            pstmt = con.prepareStatement (
               "SELECT date " +
               "FROM teepast2 " +
               "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND yy = ? AND courseName = ? AND custom_int > ?");

            pstmt.clearParameters();
            pstmt.setString(1, mNumA[i]);
            pstmt.setString(2, mNumA[i]);
            pstmt.setString(3, mNumA[i]);
            pstmt.setString(4, mNumA[i]);
            pstmt.setString(5, mNumA[i]);
            pstmt.setInt(6, slotParms.yy);
            pstmt.setString(7, course);
            pstmt.setInt(8, days);                 // check all times created after max allowed days in adv
            rs = pstmt.executeQuery();

            while (rs.next()) {

               count++;                    // count it 
            }

            pstmt.close();

            if (count > 3) {                     // if 4 or more times found

               error = true;
               slotParms.player = playerA[i];      // save player's name for error message
               break loop1;
            }
         }
      }

   }
   catch (Exception e) {

      String errorMsg = "Error checking for Advance Time - congressionalCustom.checkAdvTimes: ";
      errorMsg = errorMsg + e.getMessage();
      verifySlot.logError(errorMsg);        // log the error message
   }
    */

   return(error);
 }


 // *********************************************************
 // Custom Processing (check time of day for 2-some only time)
 // *********************************************************

 public static boolean check2Somes(long date, int time) {


   boolean status = false;

   long shortDate = date - ((date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000 = 512)


   //
   //        ************* See also SystemUtils ********************
   //
   //   2-some ONLY times from 8:00 AM to 10:48 AM on Tuesdays on the Gold Course
   //
   if (shortDate > 430 && shortDate < 1101) {            
     
      if (time > 759 && time < 931) {         // if tee time is between 8:00 and 9:30 AM

         status = true;                        // 2-some only time
      }
   }

   return(status);         // true = 2-somes only time
 }



}  // end of congressionalCustom class

