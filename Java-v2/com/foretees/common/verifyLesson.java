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
 *   12/11/12   Updated checkActivityTimes to properly check all restrictions that apply to a time, and also check for suspensions for those restrictions.
 *    7/12/12   Boathouse MV (boathousemv) - Added custom to search through the courts in reverse order when looking for space for a lesson time (case 2171).
 *   12/08/11   Added 'GROUP BY date' to the date lookup query of checkActivityTimes. It was pulling back hundreds of duplicates and was likely the reason this method always ran so slow.
 *   11/01/11   Charlotte CC (charlottecc) - Updated 30 min buffer custom to only apply it on the golf side (case 2017).
 *   09/26/11   Moved getLessonInfo method from Proshop_gensheets to verifyLesson so it's accessible from a common, public location.
 *   09/02/11   Fixed issue where time was being considered in use due to a member name being in the slot, even when it was the same member who owned the lesson.
 *   08/31/11   Charlotte CC (charlottecc) - Add a 30 min buffer time to the end time of a lesson when looking for available times to allow for an automatic break between lessons (case 2017).
 *   08/30/11   When checking if consecutive times are in use, consider in use if memname is populated (was ignoring before).
 *   07/05/11   Boulder CC (boulder) - Search through courts in reverse order when looking for space for a lesson time (case 1999).
 *   02/24/11   Elmcrest CC (elmcrestcc) - Search through courts in reverse order when looking for space for a lesson time (case 1947).
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

 public synchronized static int checkInUse(String club, long date, int time, int id, int lesson_id, int activity_id, String ltype, String user, Connection con)
         throws Exception {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   int in_use = 0;
   int found = 0;
   int length = 0;
   int length_max = 0;
   int etime = 0;

   String memname = "";
   String memid = "";

   boolean ok = true;

   try {
       
      if (user.startsWith( "proshop" )) {      // if proshop

          try {
              //  Proshop user - do all day
              pstmt = con.prepareStatement(
                      "SELECT in_use "
                      + "FROM lessonbook5 WHERE recid = ?");    // check this specific time

              pstmt.clearParameters();        // clear the parms
              pstmt.setInt(1, lesson_id);
              rs = pstmt.executeQuery();      // execute the prepared stmt

              if (rs.next()) {

                  in_use = rs.getInt(1);
              }

          } catch (Exception e) {
              in_use = 1;
                  Utilities.logError("verifyLesson.checkInUse - Error checking if selected lesson type is in use - Error: " + e.toString());
          } finally {
              Connect.close(rs, pstmt);
          }
         
          if (in_use == 0) {              // if time Lesson NOT already in use - set it now (all day)

              String dateClause = "";

              try {
                  pstmt = con.prepareStatement("SELECT MAX(length) AS length_max FROM lessontype5 WHERE proid = ? AND activity_id = ?");

                  pstmt.clearParameters();
                  pstmt.setInt(1, id);
                  pstmt.setInt(2, activity_id);

                  rs = pstmt.executeQuery();

                  if (rs.next()) {
                      length_max = rs.getInt("length_max");
                  }

              } catch (Exception e) {
                  length_max = 0;
                  Utilities.logError("verifyLesson.checkInUse - Error looking up max lesson type length - Error: " + e.toString());
              } finally {
                  Connect.close(rs, pstmt);
              }
              
              if (length_max > 0) {
                  etime = getEndTime(time, length_max);
                  dateClause = " AND time >= ? AND time < ?";
              }

              try {
                  pstmt = con.prepareStatement(
                          "UPDATE lessonbook5 "
                          + "SET in_use = 1 "
                          + "WHERE proid = ? AND activity_id = ? AND date = ?"
                          + dateClause);

                  pstmt.clearParameters();          // clear the parms
                  pstmt.setInt(1, id);
                  pstmt.setInt(2, activity_id);
                  pstmt.setLong(3, date);
                  
                  if (!dateClause.equals("")) {
                      pstmt.setInt(4, time);
                      pstmt.setInt(5, etime);
                  }

                  pstmt.executeUpdate();            // execute the prepared stmt

              } catch (Exception e) {
                  Utilities.logError("verifyLesson.checkInUse - Error setting lesson times in use - Error: " + e.toString());
              } finally {
                  Connect.close(pstmt);
              }
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

         // If Charlotte CC, add 30 minutes to the lesson length since they want us to block a 30 minute buffer time after each lesson.
         // Adding 30 mins will prevent members from booking a lesson that butts right up to an existing lesson later in the day.
         if (club.equals("charlottecc") && activity_id == 0) {
             
             length += 30;
         }

         //
         //  Now determine end time
         //
         etime = getEndTime(time, length);   // get end of lesson time

         ok = true;           // init flag
         
         //
         //  Check each time slot
         //
         pstmt = con.prepareStatement (
            "SELECT in_use, memname, memid " +
            "FROM lessonbook5 WHERE proid = ? AND activity_id = ? AND date = ? AND time >= ? AND time < ?");

         pstmt.clearParameters();     
         pstmt.setInt(1, id);
         pstmt.setInt(2, activity_id);
         pstmt.setLong(3, date);
         pstmt.setInt(4, time);
         pstmt.setInt(5, etime);
         rs = pstmt.executeQuery();   

         while (rs.next()) {

            in_use = rs.getInt("in_use");
            memname = rs.getString("memname");
            memid = rs.getString("memid");
              
            if (in_use > 0 || (!memname.equals("") && !memid.equalsIgnoreCase(user))) {             // if busy or already a member in that lesson
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

   while (min > 59 && hr < 23) {             // if exceeded next hour

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
 public static Map<Integer, List<Integer>> checkActivityTimes(int lesson_id, int activity_id, String locations_csv, long sdate, long edate, int[] days, int stime, 
         int etime, boolean bookAllSheets, boolean autoSelectLocation, Connection con, PrintWriter out) {

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
     String activityOrder = "";
     String club = getClub.getClubName(con);

     int curr_activity_id = 0;
     int curr_date = 0;
     int curr_time = 0;
     int end_time = 0;
     int curr_interval = 0;

     boolean eo_week = false;
     boolean foundTimes = false;
     boolean restriction = false;
     
     parmRest parmr = new parmRest();

     Map<Integer, List<Integer>> result_map = new LinkedHashMap<Integer, List<Integer>>();
     List<Integer> sheet_ids_total = new ArrayList<Integer>();     // Holds the sheet_ids for all dates in the search
     List<Integer> sheet_ids_day = new ArrayList<Integer>();       // Holds the sheet_ids for a single date

     if (club.equals("elmcrestcc") || club.equals("boulder") || club.equals("boathousemv") || club.equals("islandcc")) {
         activityOrder = " DESC";
     }

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
                     "SELECT DATE_FORMAT(date_time,'%Y%m%d') as date, DATE_FORMAT(date_time,'%W') as day_name "
                     + "FROM activity_sheets "
                     + "WHERE DATE_FORMAT(date_time,'%Y%m%d') >= ? AND DATE_FORMAT(date_time,'%Y%m%d') <= ? "
                     + recurrCheck
                     + "GROUP BY date ORDER BY date");
             pstmt.clearParameters();
             pstmt.setLong(1, sdate);
             pstmt.setLong(2, edate);
             if (eo_week) pstmt.setLong(3, sdate);

             rs = pstmt.executeQuery();
             
             // Loop through the dates and check each one for the needed time slots.  If the required slots cannot be found on a given date, reject the submission
             date_loop:
             while (rs.next()) {

                 foundTimes = true;
                 curr_date = rs.getInt("date");
                 day_name = rs.getString("day_name");
                 
                 // Get all the restrictions and suspensions for this date
                 parmr.user = "proshop";
                 parmr.mship = "";
                 parmr.mtype = "";
                 parmr.date = curr_date;
                 parmr.day = day_name;
                 parmr.course = "";
                 parmr.activity_id = activity_id;     // use Root id for now
                 
                 try {
                     getRests.getAll(con, parmr);              // get the restrictions
                 } catch (Exception exc) {
                     Utilities.logError("verifyLesson.checkActivityTimes - getRests.getAll failed - date=" + curr_date + ", day_name=" + day_name + ", activity_id=" + activity_id + ", err=" + exc.toString());
                 }
                 
                 try {
                     pstmt2 = con.prepareStatement(
                             "SELECT activity_id "
                             + "FROM activity_sheets "
                             + "WHERE activity_id IN (" + activity_ids + ") AND DATE_FORMAT(date_time,'%Y%m%d') = ? "
                             + "GROUP BY activity_id "
                             + "ORDER BY activity_id" + activityOrder);

                     pstmt2.clearParameters();
                     pstmt2.setInt(1, curr_date);

                     rs2 = pstmt2.executeQuery();

                     // Loop through the activity ids for this date and check for the needed time slots.  If the required slots cannot be found on
                     // a given timesheet, move to the next
                     activity_id_loop: 
                     while (rs2.next()) {

                         foundTimes = true;
                         curr_activity_id = rs2.getInt("activity_id");

                         try {
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
                                     "SELECT sheet_id, DATE_FORMAT(date_time,'%H%i') as time, event_id, lesson_id, rest_id, blocker_id, auto_blocked, in_use_by, "
                                     + "IF((UNIX_TIMESTAMP(in_use_at) + (6 * 60)) < UNIX_TIMESTAMP(),1,0) AS in_use_expired "
                                     + "FROM activity_sheets "
                                     + "WHERE activity_id = ? AND DATE_FORMAT(date_time,'%Y%m%d') = ? "
                                     + "AND DATE_FORMAT(date_time,'%H%i') < ? "
                                     + "ORDER BY time DESC");

                             pstmt3.clearParameters();
                             pstmt3.setInt(1, curr_activity_id);
                             pstmt3.setInt(2, curr_date);
                             pstmt3.setString(3, etimeStr);

                             
                             rs3 = pstmt3.executeQuery();

                             // Loop through the times for this timesheet and check for the needed time slots.
                             timesheet_loop:
                             while (rs3.next()) {

                                 foundTimes = false;
                                 restriction = false;
                                 curr_time = rs3.getInt("time");
                                 end_time = getEndTime(curr_time, curr_interval);                             

                                 // Only need to worry about checking this time slot if its end time is later than the start time of the lesson, meaning there's overlap
                                 if (end_time >= stime) {

                                     // Check whether or not a membership type restrictions apply to this time.  If so, see if they allows lessons to override it.
                                     // If overrides are not allowed, check to see if the restriction is suspsended at this time.
                                     if (rs3.getInt("rest_id") != 0 && !getRestData(parmr, curr_time, curr_activity_id)) {

                                         restriction = true;
                                     }

                                     // Check whether or not other items are already present in this time slot that would prevent this lesson from taking place there
                                     if (restriction || rs3.getInt("event_id") != 0 || (rs3.getInt("lesson_id") != 0 && rs3.getInt("lesson_id") != lesson_id) ||
                                         rs3.getInt("blocker_id") != 0 || rs3.getInt("auto_blocked") != 0 || (rs3.getInt("in_use_by") == 1 && rs3.getInt("in_use_expired") == 0)) {                      
                                         sheet_ids_day.clear();
                                         break timesheet_loop;

                                     } else {

                                         // Add sheet_id to tally for this day
                                         sheet_ids_day.add(0, rs3.getInt("sheet_id"));

                                         try {
                                             // Check to see if there are already players in this time
                                             pstmt4 = con.prepareStatement("SELECT activity_sheets_player_id FROM activity_sheets_players WHERE activity_sheet_id = ? LIMIT 1");
                                             pstmt4.clearParameters();
                                             pstmt4.setInt(1, sheet_ids_day.get(0));

                                             rs4 = pstmt4.executeQuery();

                                             if (rs4.next()) {
                                                 
                                                 // This timesheet won't work, clear ArrayList and move to next sheet
                                                 sheet_ids_day.clear();
                                                 break timesheet_loop;
                                             }

                                             pstmt4.close();
                                         } catch (Exception e) {
                                             Utilities.logError("verifyLesson.checkActivityTimes - " + club + " - Error checking for existing players for act_id (" + curr_activity_id + ") - Error: " + e.toString());
                                         } finally {
                                             Connect.close(rs4, pstmt4);
                                         }

                                         // If the time we're currently on matches or is earlier than the start time of the lesson,
                                         // mark that we're done and break out of the loop for this timeshet
                                         if (curr_time <= stime) {

                                             foundTimes = true;
                                             break timesheet_loop;
                                         }

                                         foundTimes = true;
                                     }
                                 } else {
                                     foundTimes = true;
                                     break timesheet_loop;
                                 }

                             }  // end of rs3 loop

                         } catch (Exception e) {
                             Utilities.logError("verifyLesson.checkActivityTimes - " + club + " - Error looking up timesheet for act_id (" + curr_activity_id + ") - Error: " + e.toString());
                         } finally {
                             Connect.close(rs3, pstmt3);
                         }

                         // If we were able to find times on this timesheet, move them over to the overall list and move proceed to next day
                         // Otherwise, move on to next timesheet on this day
                         if (foundTimes) {

                             if (autoSelectLocation) {

                                 // Move all sheet_ids from the tally for this day to the total tally
                                 for (int i=0; i<sheet_ids_day.size(); i++) {
                                     sheet_ids_total.add(sheet_ids_day.get(i));
                                 }

                                 // Clear the sheet_ids stored for this day
                                 sheet_ids_day.clear();

                             } else {

                                 // If no times were returned, but no issue was found, this means the lesson takes place entirely before or after the time sheets, mark this court as N/A so it's not displayed as an option.
                                 if (sheet_ids_day.size() == 0 && foundTimes) {
                                     sheet_ids_day.add(-99);
                                 }
                                 
                                 // Place the List object containing the sheet ids for this activity_id into a Map for use in displaying all available options later
                                 result_map.put(curr_activity_id, sheet_ids_day);

                                 // Point the old List object to a brand new List, so the one put in the Map is not modified further
                                 sheet_ids_day = new ArrayList<Integer>();
                             }

                             // Done with this day, if only booking one sheet break out of activity_id loop and move to next day
                             if (!bookAllSheets && autoSelectLocation) {
                                 break activity_id_loop;
                             }

                         } else {

                             // If no times were found on this sheet and we need to book times on all provided activity_sheet ids,
                             // clear ArrayList and break out of loop
                             if (bookAllSheets) {
                                 sheet_ids_total.clear();
                                 break activity_id_loop;
                             }
                         }
                     }  // end of rs2 loop
                 } catch (Exception e) {
                     Utilities.logError("verifyLesson.checkActivityTimes - " + club + " - Error looking up activity_ids for this date (" + curr_date + ") - Error: " + e.toString());
                 } finally {
                     Connect.close(rs2, pstmt2);
                 }
                 
                 // If no times were found on this day, there's no point in continuing.  Clear total tally and break out
                 if (!foundTimes) {
                     sheet_ids_total.clear();
                     break date_loop;
                 }
             }  // end of rs loop

         } catch (Exception e) {
             out.println("<!-- Error encountered:  " + e.getMessage() + " -->");
             Utilities.logError("verifyLesson.checkActivityTimes - " + club + " - Error looking up applicable dates for lesson booking request - Error: " + e.toString());
             sheet_ids_total.clear();
         } finally {
             Connect.close(rs, pstmt);
         }
     }

     if (sheet_ids_total.size() == 0 && foundTimes) {
         sheet_ids_total.add(-99);
     }
     
     if (autoSelectLocation && sheet_ids_total.size() > 0) {
         result_map.put(0, sheet_ids_total);
     }

     return result_map;
 }  // end of checkActivityTimes method
 
 
 public static String getLessonInfo(int lesson_id, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String result = "";

    try {

        // if negative it's a lesson group, if positive then it's a individual lesson
        if (lesson_id < 0) {

            // lesson group (go to lessongrp5 - bind lesson_id to one another and get lname)
            pstmt = con.prepareStatement("" +
                    "SELECT lg.lname, lg.color, CONCAT(lp.fname, ' ', IF(lp.mi<>'', CONCAT(lp.mi, ' '), ''), lp.lname) AS proname " +
                    "FROM lessongrp5 lg " +
                    "LEFT OUTER JOIN lessonpro5 lp ON lp.id = lg.proid " +
                    "WHERE lg.lesson_id = ?");
            lesson_id = Math.abs(lesson_id);

            pstmt.clearParameters();
            pstmt.setInt(1, lesson_id);
            rs = pstmt.executeQuery();

            if ( rs.next() ) {
                result = rs.getString("proname") + " - " + rs.getString("lg.lname") + "|" + rs.getString("lg.color");
            }

        } else {

            // individual lesson (go to lessonbook5 - bind lesson_id to recid and get ltype)
            pstmt = con.prepareStatement("" +
                    "SELECT lb.ltype, lb.memname, lb.color, CONCAT(lp.fname, ' ', IF(lp.mi<>'', CONCAT(lp.mi, ' '), ''), lp.lname) AS proname " +
                    "FROM lessonbook5 lb " +
                    "LEFT OUTER JOIN lessonpro5 lp ON lp.id = lb.proid " +
                    "WHERE lb.recid = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, lesson_id);
            rs = pstmt.executeQuery();

            if ( rs.next() ) {
                result = rs.getString("proname") + " - " + rs.getString("lb.ltype") + " - " + rs.getString("lb.memname") + "|" + rs.getString("lb.color");
            }
        }

    } catch (Exception exc) {

        Utilities.logError("<p>ERROR GETTING LESSON NAME:" + exc.toString() + "</p>");

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return result;

 }

 /**
  * Determines whether or not a particular time slot is covered by restriction that would prevent a lesson from being booked there.
  * 
  * @param parmr Parm block holding restriction data
  * @param time Time of the time slot in question
  * @param activity_id activity_id of the time slot in question
  * @return boolean - True if lesson booking is allowed, False if not.
  */
 public static boolean getRestData(parmRest parmr, int time, int activity_id) {

    boolean allow = true;
    boolean suspend = false;

    int ind = 0;

    // Check all restrinctions for this day to see if any affect this time
    while (ind < parmr.MAX && allow && !parmr.restName[ind].equals("")) {           // loop over possible restrictions
    
        if (parmr.allow_lesson[ind] == 0 && parmr.stime[ind] <= time && parmr.etime[ind] >= time) {      // matching time ?

            // Check to make sure no suspensions apply
            suspend = false;

            loop2:
            for (int k=0; k<parmr.MAX; k++) {

                if (parmr.susp[ind][k][0] == 0 && parmr.susp[ind][k][1] == 0) {

                    k = parmr.MAX;   // don't bother checking any more

                } else if (parmr.susp[ind][k][0] <= time && parmr.susp[ind][k][1] >= time) {    // time falls within a suspension

                    // check to see if this activity_id is in the locations csv for this suspension
                    StringTokenizer tok = new StringTokenizer( parmr.susp_locations[ind][k][0], "," );

                    while (tok.hasMoreTokens()) {

                        if ( activity_id == Integer.parseInt(tok.nextToken()) ) {

                            suspend = true;
                            break loop2;     // don't bother checking any more

                        } // end if restriction applies to this activity_id (court level)

                    } // end while loop of the locations csv
                }
            }

            if (!suspend) {
 
                // check to see if this activity_id is in the locations csv for this restriction
                StringTokenizer tok = new StringTokenizer( parmr.locations[ind], "," );

                while (tok.hasMoreTokens() && allow) {

                    if ( activity_id == Integer.parseInt(tok.nextToken()) ) {

                        allow = false;

                    } // end if restriction applies to this activity_id (court level)

                } // end while loop of the locations csv

            } // end if not suspended

        } // end if time matches

       ind++;

    } // end of while loop of all restrictions
  
    return allow;
}
 
 public static void clearLessonFromActSheets(int lesson_id, HttpServletRequest req) {
     clearLessonFromActSheets(lesson_id, null, req);
 }
 
 public static void clearLessonFromActSheets(int lesson_id, List<Integer> sheet_ids, HttpServletRequest req) {
     
     PreparedStatement pstmt = null;
     
     Connection con = Connect.getCon(req);
     
     String in_string = "";
     
     // If a list of sheet_ids was provided, these are the new sheet_ids that were booked, so we do NOT want to clear the lesson_id from these sheet_ids!
     if (sheet_ids != null && sheet_ids.size() > 0) {
         for (int sheet_id : sheet_ids) {
             in_string += sheet_id + ",";
         }
         
         if (in_string.endsWith(",")) in_string = in_string.substring(0, in_string.length() - 1);
     }
     
     try {
         
         pstmt = con.prepareStatement("UPDATE activity_sheets SET lesson_id = 0 WHERE lesson_id = ?" + (!in_string.equals("") ? " AND sheet_id NOT IN (" + in_string + ")" : ""));
         pstmt.clearParameters();
         pstmt.setInt(1, lesson_id);
         
         pstmt.executeUpdate();
         
     } catch (Exception e) {
         Utilities.logError("verifyLesson.clearLessonFromActSheets - Error clearing out lesson (id " + lesson_id + ") from activity_sheets - Error: " + e.toString());
     } finally {
         Connect.close(pstmt);
     }
 }

}  // end of verifyLesson class

