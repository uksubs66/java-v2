/***************************************************************************************
 *   verifyLesson:  This servlet will provide some common tee time request processing methods.
 *
 *       called by:  Proshop_lesson
 *                   Member_lesson
 *
 *
 *   created:  10/26/2004   Bob P.
 *
 *
 *   last updated:
 *
 *   02/24/10   Change to include restrictions when looking for open time slots for lessons to take place on.  If the only time slot open falls within
 *              a restriction, it will only be allowed if that restriction has the "allow-lessons" property set to true.
 *   11/06/09   Change to accomodate booking lesson times that fall completely outside the time-sheets for non-golf activities
 *   11/02/09   Added use of locations_csv to checkActivityTimes to search specific time-sheets, as well as a bookAllSheets boolean
 *              for booking group lessons, where more than one court needs to be booked
 *   10/27/09   Changes to include activity_id when checking/setting in_use flags
 *   10/14/09   Added checkActivityTimes method to check activity sheets for matching and available time slots to house a given lesson time (needed
 *              because of the fact that lessons will take place on the same space as normal member use)
 *
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


public class verifyLesson {

   private static String rev = ProcessConstants.REV;


/**
 //************************************************************************
 //
 //  checkInUse - check if Lesson Time is in use and if not, set it
 //
 //************************************************************************
 **/

 public synchronized static int checkInUse(long date, int time, int id, int lesson_id, int activity_id, String ltype, String user, Connection con)
         throws Exception {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   int in_use = 0;
   int found = 0;
   int length = 0;
   int etime = 0;

   boolean ok = true;

   try {

      if (user.startsWith( "proshop" )) {      // if proshop

         //
         //  Proshop user - do all day
         //
         pstmt = con.prepareStatement (
            "SELECT in_use " +
            "FROM lessonbook5 WHERE recid = ?");    // check this specific time

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, lesson_id);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            in_use = rs.getInt(1);
         }

         pstmt.close();

         if (in_use == 0) {              // if time Lesson NOT already in use - set it now (all day)

            pstmt = con.prepareStatement (
               "UPDATE lessonbook5 SET in_use = 1 WHERE proid = ? AND activity_id = ? AND date = ?");

            pstmt.clearParameters();          // clear the parms
            pstmt.setInt(1, id);
            pstmt.setInt(2, activity_id);
            pstmt.setLong(3, date);
            pstmt.executeUpdate();            // execute the prepared stmt

            pstmt.close();
         }

      } else {     // Members - just do the lesson time(s)

         //
         //  First we need to determine how many time slots to check/set (based on lesson type)
         //
         pstmt = con.prepareStatement (
                 "SELECT length FROM lessontype5 WHERE proid = ? AND activity_id = ? AND ltname = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, id);
         pstmt.setInt(2, activity_id);
         pstmt.setString(3, ltype);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            length = rs.getInt(1);
         }
         pstmt.close();

         //
         //  Now determine end time
         //
         etime = getEndTime(time, length);   // get end of lesson time

         ok = true;           // init flag

         //
         //  Check each time slot
         //
         pstmt = con.prepareStatement (
            "SELECT in_use " +
            "FROM lessonbook5 WHERE proid = ? AND activity_id = ? AND date = ? AND time >= ? AND time < ?");

         pstmt.clearParameters();     
         pstmt.setInt(1, id);
         pstmt.setInt(2, activity_id);
         pstmt.setLong(3, date);
         pstmt.setInt(4, time);
         pstmt.setInt(5, etime);
         rs = pstmt.executeQuery();   

         while (rs.next()) {

            in_use = rs.getInt(1);
              
            if (in_use > 0) {             // if busy
              
               ok = false;
            }
         }
         pstmt.close();

         if (ok == true) {              // if time Lesson NOT already in use - set it now

            pstmt = con.prepareStatement (
               "UPDATE lessonbook5 SET in_use = 1 WHERE proid = ? AND activity_id = ? AND date = ? AND time >= ? AND time < ?");

            pstmt.clearParameters();          // clear the parms
            pstmt.setInt(1, id);
            pstmt.setInt(2, activity_id);
            pstmt.setLong(3, date);
            pstmt.setInt(4, time);
            pstmt.setInt(5, etime);
            pstmt.executeUpdate();            // execute the prepared stmt

            pstmt.close();

            in_use = 0;                       // return 'Good' value

         } else {

            in_use = 1;                       // return 'Busy' value
         }
      }
   }
   catch (Exception e) {

      throw new UnavailableException("Error checking in-use - verifyLesson.checkInUse " + e.getMessage());
   }

   return(in_use);
 }


 // *********************************************************
 //  determine time value for lesson length
 // *********************************************************

 private static int getEndTime(int time, int ltlength) {


   int hr = 0;
   int min = 0;

   //
   //  add the length field to the time field
   //
   hr = time / 100;            // get hr
   min = time - (hr * 100);    // get minute

   min += ltlength;            // add length to minutes

   if (min > 59) {             // if exceeded next hour

      hr++;                    // get next hour (most likely won't exceed midnight)
      min -= 60;               // adjust minute
   }

   time = (hr * 100) + min;    // get new time

   return(time);

 }   // end of getEndTime

 /**
  * checkActivityTimes - Checks activity time sheets to see if any contain the needed time slots to house a certain lesson booking
  *
  * @param lesson_id ID of the lessonbook entry
  * @param activity_id Current activity id
  * @param locations_csv List of possible/required locations for this lesson
  * @param sdate Start date of the lesson booking
  * @param edate End date of the lesson booking (same as start date for normal lesson bookings)
  * @param days Days of recurrence for group lessons
  * @param stime Start time of the lesson booking
  * @param etime End time of the lesson booking
  * @param boolean bookAllSheets True if a chunk of time must be booked on all provided activity_sheets, false if only one successful sheet is needed
  * @param con Connection to club database
  * @param out Output stream
  *
  * @return sheet_ids - contains the ids of all activity sheet slots needed to house this lesson
  */
 public static ArrayList<Integer> checkActivityTimes(int lesson_id, int activity_id, String locations_csv, long sdate, long edate, int[] days, int stime, int etime, boolean bookAllSheets, Connection con, PrintWriter out) {

     // Declare/Init variables
     PreparedStatement pstmt = null;
     PreparedStatement pstmt2 = null;
     PreparedStatement pstmt3 = null;
     PreparedStatement pstmt4 = null;
     PreparedStatement pstmt5 = null;
     ResultSet rs = null;
     ResultSet rs2 = null;
     ResultSet rs3 = null;
     ResultSet rs4 = null;
     ResultSet rs5 = null;

     String activity_ids = "";
     String recurrCheck = "";
     String day_name = "";

     int curr_activity_id = 0;
     int curr_date = 0;
     int curr_time = 0;
     int end_time = 0;
     int curr_interval = 0;

     boolean eo_week = false;
     boolean foundTimes = false;
     boolean restriction = false;

     ArrayList<Integer> sheet_ids_total = new ArrayList<Integer>();     // Holds the sheet_ids for all dates in the search
     ArrayList<Integer> sheet_ids_day = new ArrayList<Integer>();       // Holds the sheet_ids for a single date

     // If no list of activity_ids was passed, get an ArrayList of all children activity ids for the current activity,
     // otherwise use the passed list
     if (locations_csv.equals("")) {
         activity_ids = getActivity.buildInString(activity_id, 1, con);
     } else {
         activity_ids = locations_csv;
     }

     // Only proceed if at least one child found, otherwise no sheets are going to be present
     if (!activity_ids.equals("")) {

         // If sdate and edate are different, then this is a group lesson (clinic)
         // get the days of the week values and eo_week from the lessongrp5 table
         if (sdate != edate) {

             if (days[0] == 1) {    // eo_week check
                 recurrCheck += "AND (MOD(DATE_FORMAT(date_time,'%U'), 2) = MOD(DATE_FORMAT(?,'%U'), 2)) ";
                 eo_week = true;
             }

             String dayStr = "";
             String prefix = "AND (";

             if (days[1] == 1) {    // sunday check
                 dayStr += prefix + "DATE_FORMAT(date_time, '%w') = '0' ";
                 prefix = "OR ";
             }
             if (days[2] == 1) {    // monday check
                 dayStr += prefix + "DATE_FORMAT(date_time, '%w') = '1' ";
                 prefix = "OR ";
             }
             if (days[3] == 1) {    // tuesday check
                 dayStr += prefix + "DATE_FORMAT(date_time, '%w') = '2' ";
                 prefix = "OR ";
             }
             if (days[4] == 1) {    // wednesday check
                 dayStr += prefix + "DATE_FORMAT(date_time, '%w') = '3' ";
                 prefix = "OR ";
             }
             if (days[5] == 1) {    // thursday check
                 dayStr += prefix + "DATE_FORMAT(date_time, '%w') = '4' ";
                 prefix = "OR ";
             }
             if (days[6] == 1) {    // friday check
                 dayStr += prefix + "DATE_FORMAT(date_time, '%w') = '5' ";
                 prefix = "OR ";
             }
             if (days[7] == 1) {    // saturday check
                 dayStr += prefix + "DATE_FORMAT(date_time, '%w') = '6'";
                 prefix = "OR ";
             }
             if (!dayStr.equals("")) {
                 dayStr += ") ";
             }

             recurrCheck += dayStr;
         }

         String stimeStr = String.valueOf(stime);
         String etimeStr = String.valueOf(etime);

         if (stime < 1000) stimeStr = "0" + stimeStr;
         if (etime < 1000) etimeStr = "0" + etimeStr;


         // Build query to grab all relevant times
         try {
             // First, grab all the necessary dates
             pstmt = con.prepareStatement(
                     "SELECT DATE_FORMAT(date_time,'%Y%m%d') as date, DATE_FORMAT(date_time,'%W') as day_name " +
                     "FROM activity_sheets " +
                     "WHERE DATE_FORMAT(date_time,'%Y%m%d') >= ? AND DATE_FORMAT(date_time,'%Y%m%d') <= ? " +
                     recurrCheck +
                     "ORDER BY date");
             pstmt.clearParameters();
             pstmt.setLong(1, sdate);
             pstmt.setLong(2, edate);
             if (eo_week) pstmt.setLong(3, sdate);

             rs = pstmt.executeQuery();
             
             // Loop through the dates and check each one for the needed time slots.  If the required slots cannot be found on a given date, reject the submission
             while (rs.next()) {

                 foundTimes = true;
                 curr_date = rs.getInt("date");
                 day_name = rs.getString("day_name");

                 pstmt2 = con.prepareStatement(
                         "SELECT activity_id " +
                         "FROM activity_sheets " +
                         "WHERE activity_id IN (" + activity_ids + ") AND DATE_FORMAT(date_time,'%Y%m%d') = ? " +
                         "GROUP BY activity_id " +
                         "ORDER BY activity_id");
                 
                 pstmt2.clearParameters();
                 pstmt2.setInt(1, curr_date);

                 rs2 = pstmt2.executeQuery();

                 // Loop through the activity ids for this date and check for the needed time slots.  If the required slots cannot be found on
                 // a given timesheet, move to the next
                 while (rs2.next()) {

                     foundTimes = true;
                     curr_activity_id = rs2.getInt("activity_id");

                     // Get the standard slot interval for this activity_id
                     pstmt3 = con.prepareStatement("SELECT minutesbtwn FROM activities WHERE activity_id = ?");
                     pstmt3.clearParameters();
                     pstmt3.setInt(1, curr_activity_id);

                     rs3 = pstmt3.executeQuery();

                     if (rs3.next()) {
                         curr_interval = rs3.getInt("minutesbtwn");
                     }

                     pstmt3.close();

                     pstmt3 = con.prepareStatement(
                             "SELECT sheet_id, DATE_FORMAT(date_time,'%H%i') as time, event_id, lesson_id, rest_id, blocker_id, auto_blocked " +
                             "FROM activity_sheets " +
                             "WHERE activity_id = ? AND DATE_FORMAT(date_time,'%Y%m%d') = ? " +
                             "AND DATE_FORMAT(date_time,'%H%i') < ? " +
                             "ORDER BY time DESC");

                     pstmt3.clearParameters();
                     pstmt3.setInt(1, curr_activity_id);
                     pstmt3.setInt(2, curr_date);
                     pstmt3.setString(3, etimeStr);

                     rs3 = pstmt3.executeQuery();

                     // Loop through the times for this timesheet and check for the needed time slots.
                     while (rs3.next()) {

                         foundTimes = false;
                         restriction = false;
                         curr_time = rs3.getInt("time");
                         end_time = getEndTime(curr_time, curr_interval);

                         // Only need to worry about checking this time slot if its end time is later than the start time of the lesson, meaning there's overlap
                         if (end_time >= stime) {

                             // Check whether or not a membership type restriction applies to this time.  If so, see if it allows lessons to override it.
                             // If overrides are not allowed, check to see if the restriction is suspsended at this time.
                             if (rs3.getInt("rest_id") != 0) {

                                 int rest_id = rs3.getInt("rest_id");

                                 // Check restriction2 to see if this restriction allows lesson overrides
                                 pstmt4 = con.prepareStatement("SELECT allow_lesson FROM restriction2 WHERE id = ?");
                                 pstmt4.clearParameters();
                                 pstmt4.setInt(1, rest_id);

                                 rs4 = pstmt4.executeQuery();

                                 if (rs4.next()) {

                                     // If allow_lesson is set to 0, we now need to check whether this restriction is suspended or not
                                     if (rs4.getInt("allow_lesson") == 0 ) {

                                         restriction = true;

/*      GET BACK TO THIS WHEN SUSPENSIONS ARE IMPLEMENTED FOR ACTIVITIES!!
                                         pstmt5 = con.prepareStatement("SELECT * FROM rest_suspend WHERE " +
                                                 "mrest_id = ? AND " + day_name.toLowerCase() + " = 1 AND sdate <= ? AND edate >= ? AND stime <= ? AND etime >= ? " +
                                                 "AND (courseName = '-ALL-' OR courseName = ?) AND (eo_week = 0 OR (MOD(DATE_FORMAT(sdate, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2)))");
                                         pstmt5.clearParameters();
                                         pstmt5.setInt(1, rest_id);
                                         pstmt5.setInt(2, curr_date);
                                         pstmt5.setInt(3, curr_date);
                                         pstmt5.setInt(4, curr_time);
                                         pstmt5.setInt(5, curr_time);
 */
                                     }
                                 }

                                 pstmt4.close();
                             }

                             // Check whether or not other items are already present in this time slot that would prevent this lesson from taking place there
                             if (restriction || rs3.getInt("event_id") != 0 || (rs3.getInt("lesson_id") != 0 && rs3.getInt("lesson_id") != lesson_id) ||
                                 rs3.getInt("blocker_id") != 0 || rs3.getInt("auto_blocked") != 0) {

                                 sheet_ids_day.clear();
                                 break;

                             } else {

                                 // Add sheet_id to tally for this day
                                 sheet_ids_day.add(0, rs3.getInt("sheet_id"));

                                 // Check to see if there are already players in this time
                                 pstmt4 = con.prepareStatement("SELECT activity_sheets_player_id FROM activity_sheets_players WHERE activity_sheet_id = ? LIMIT 1");
                                 pstmt4.clearParameters();
                                 pstmt4.setInt(1, sheet_ids_day.get(0));

                                 rs4 = pstmt4.executeQuery();

                                 if (rs4.next()) {

                                     // This timesheet won't work, clear ArrayList and move to next sheet
                                     sheet_ids_day.clear();
                                     break;
                                 }

                                 pstmt4.close();

                                 // If the time we're currently on matches or is earlier than the start time of the lesson,
                                 // mark that we're done and break out of the loop for this timeshet
                                 if (curr_time <= stime) {

                                     foundTimes = true;
                                     break;
                                 }

                                 foundTimes = true;
                             }
                         } else {
                             foundTimes = true;
                             break;
                         }

                     }  // end of rs3 loop

                     // If we were able to find times on this timesheet, move them over to the overall list and move proceed to next day
                     // Otherwise, move on to next timesheet on this day
                     if (foundTimes) {

                         // Move all sheet_ids from the tally for this day to the total tally
                         for (int i=0; i<sheet_ids_day.size(); i++) {
                             sheet_ids_total.add(sheet_ids_day.get(i));
                         }

                         // Clear the sheet_ids stored for this day
                         sheet_ids_day.clear();
                         
                         // Done with this day, if only booking one sheet break out of activity_id loop and move to next day
                         if (!bookAllSheets) {
                             break;
                         }
                     } else {

                         // If no times were found on this sheet and we need to book times on all provided activity_sheet ids,
                         // clear ArrayList and break out of loop
                         if (bookAllSheets) {
                             sheet_ids_total.clear();
                             break;
                         }
                     }

                     pstmt3.close();
                 }  // end of rs2 loop

                 // If no times were found on this day, there's no point in continuing.  Clear total tally and break out
                 if (!foundTimes) {
                     sheet_ids_total.clear();
                     break;
                 }

                 pstmt2.close();
             }  // end of rs loop

             pstmt.close();

         } catch (Exception exc) {
             out.println("<!-- Error encountered:  " + exc.getMessage() + " -->");
             sheet_ids_total.clear();
         }
     }

     if (sheet_ids_total.size() == 0 && foundTimes) {
         sheet_ids_total.add(-99);
     }

     return sheet_ids_total;
 }  // end of checkActivityTimes method

}  // end of verifyLesson class

