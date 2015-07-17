/***************************************************************************************
 *   DaysAdv:  This class defines an array of 'days in advance' that a member can make
 *             tee times.  This is set a login time and used whenever the calendars are
 *             built.
 *
 *   created:  1/08/2004   Bob P.
 *
 *   last updated:       ******* keep this accurate *******
 * 
 *     11/04/09  Added maxview int to save the days member can view sheets (so we don't have to go get this again).
 *
 ***************************************************************************************
 */

package com.foretees.common;

import java.io.*;
import com.foretees.common.Connect;

import java.sql.*;          // mysql
import java.util.*;

/**
 ***************************************************************************************
 *
 *  This class is a utility that contains an array for Days in Advance for members to make tee times.
 *
 ***************************************************************************************
 **/

public class DaysAdv {

  // max days in advance for calendars
  public static int MAXDAYS = 366;                    // allow for 365 days !!
    
  // max days in advance member can view sheets
  public static int maxview = 0;                    // set according to mship type
    
  // array to hold the indicators for days in advance
  public int [] days = new int [MAXDAYS];                  // one per day 
  
  public String last_error;
  
  public DaysAdv(){}
  
  public DaysAdv(String club, String mship, String mtype, String user, int activity_id, Connection con){
      
   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;
   ResultSet rs4 = null;

   int root_activity_id = 0;

   try { root_activity_id = getActivity.getRootIdFromActivityId(activity_id, con); }
   catch (Exception ignore) { }

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(root_activity_id, con);          // allocate a parm block

   String lottery1 = "";
   String lottery2 = "";
   String msubtype = "";

   int index = 0;
   int lott = 0;
   int days = 0;
   int days1 = 0;               // days in advance that members can make tee times
   int days2 = 0;               //         one per day of week (Sun - Sat)
   int days3 = 0;
   int days4 = 0;
   int days5 = 0;
   int days6 = 0;
   int days7 = 0;
   int sdays = 0;
   int cal_time = 0;            // calendar time for compares
   //int count = 0;               // init day counter
   //int col = 0;                 // init column counter
   //int d = 0;                   // 'days in advance' value for current day of week

   long date = 0;

   //
   //  Array to hold the 'Days in Advance' value for each day of the week
   //
   int [] advdays = new int [7];                        // 0=Sun, 6=Sat
   int [] advtime = new int [7];                        // adv time for each

   //
   //  init the array (1 entry per day, relative to today)
   //
   int max = this.MAXDAYS;           // get max days in advance (length of array)

   for (index = 0; index < max; index++) {

      this.days[index] = 0;
   }

   boolean reconnect = false;
   try {
       stmt = con.createStatement();
   } catch (Exception exp) {
       Utilities.logError("SystemUtils.daysInAdv: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exp));
       reconnect = true;
   }

   if (reconnect) {
       try {
           con = Connect.getCon(club);
           //SystemUtils.logError("SystemUtils.daysInAdv for club: " + club + ". Reconnected to db.");
       } catch (Exception exp) {
           //SystemUtils.logError("SystemUtils.daysInAdv for club: " + club + ". Failed to reconnect to db.");
       }
   }

   //
   // Get the Lottery Option, days in advance and time for advance from the club db
   //
   try {

      getClub.getParms(con, parm, activity_id);        // get the club parms

   }
   catch (Exception e1) {

      last_error = "Error1 in SystemUtils.daysInAdv for club: " + club + ". Exception: " +e1.getMessage();
      Utilities.logError(last_error);                           // log it
      return;
   }

   //
   //  use the member's mship type to determine which 'days in advance' parms to use
   //
   verifySlot.getDaysInAdv(con, parm, mship, activity_id);        // get the days in adv data for this member

   days1 = parm.advdays1;           // get days in adv for this type
   days2 = parm.advdays2;           // Monday
   days3 = parm.advdays3;
   days4 = parm.advdays4;
   days5 = parm.advdays5;
   days6 = parm.advdays6;
   days7 = parm.advdays7;           // Saturday

   advtime[0] = parm.advtime1;      // get time values
   advtime[1] = parm.advtime2;
   advtime[2] = parm.advtime3;
   advtime[3] = parm.advtime4;
   advtime[4] = parm.advtime5;
   advtime[5] = parm.advtime6;
   advtime[6] = parm.advtime7;


   lott = parm.lottery;

   this.maxview = parm.memviewdays;    // days this member can view tee sheets


/*
   //
   //  If Jonathan's Landing and an certain member types - change days in advance to 6 - Case# 1328
   //
   if (club.equals( "jonathanslanding" ) &&
           (mship.equals( "Golf" ) || mship.equals( "Golf Asc" ) || mship.equals( "Golf Sr" )) ) {

      days1 = 6;                    // change to 6 days
      days2 = 6;
      days3 = 6;
      days4 = 6;
      days5 = 6;
      days6 = 6;
      days7 = 6;
   }
*/
   //
   //  If El Niguel and an Adult Female - change the Tuesday Days and Time
   //
   if (club.equals( "elniguelcc" ) && mtype.equals( "Adult Female" ) && activity_id == 0) {

      days3 = 4;                    // Tues = 4 (normally is 2)
      advtime[2] = 1300;            // at 1:00 PM
   }

   //
   //   Scioto Custom - change the days in adv for Spouses - Sun, Mon, Thur, Fri, Sat = 2, Tue, Wed = 3
   //
   if (club.equals( "sciotocc" ) && mtype.startsWith( "Spouse" ) && activity_id == 0) {

      days1 = 3;          // Sun = 3 days in advance (starting at 7:30 AM)
      days2 = 3;          // Mon = 3 days in advance (starting at 7:30 AM)
      days3 = 4;          // Tue = 4 days in advance (starting at 7:30 AM)
      days4 = 4;          // Wed = 4 days in advance (starting at 7:30 AM)
      days5 = 4;          // Thu = 4 days in advance (starting at 7:30 AM)
      days6 = 3;          // Fri = 3 days in advance (starting at 7:30 AM)
      days7 = 3;          // Sat = 3 days in advance (starting at 7:30 AM)

      //advtime[5] = 1200;  // Changed back to 7:30
   }

   //
   //  If Hazeltine, check if days in adv should change
   //
   if ( club.equals( "hazeltine" ) && activity_id == 0 ) {

      //
      //  Get the member's sub-type to determine if change is needed
      //
      try {
         PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT msub_type " +
            "FROM member2b WHERE username = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, user);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            msubtype = rs.getString("msub_type");
         }
         pstmt1.close();
      }
      catch (Exception e) {

          Utilities.logError("Error in SystemUtils.daysInAdv (msub_type) for club: " + club + ", user (" + user + "). Exception: " +e.getMessage());                           // log it
      }

      //
      //  If a Female and sub-type is 'After Hours', '9 holer', or combo, then set Tuesdays to 14 days adv.
      //
      //    Time Limits are enforced in Member_sheet !!!!!!!!!!!
      //
      if ((mtype.equals("Adult Female")) && (msubtype.equals("After Hours") || msubtype.equals("9 Holer") ||
          msubtype.startsWith("AH-") || msubtype.equals("9/18 Holer"))) {

         days3 = 14;      // set 14 days in advance for Tuesdays (all 'After Hours' and 9-Holers)
      }

      if ((mtype.equals("Adult Female")) && (msubtype.equals("18 Holer") || msubtype.startsWith("AH-9/18") ||
          msubtype.startsWith("AH-18") || msubtype.equals("9/18 Holer"))) {

         days5 = 14;      // set 14 days in advance for Thursdays (all 18-Holers)
      }
   }

   //
   //  Get today's date and setup parms
   //
   Calendar cal = new GregorianCalendar();             // get todays date
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);
   int cal_am_pm = cal.get(Calendar.AM_PM);            // current time
   int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);
   int cal_min = cal.get(Calendar.MINUTE);
   int day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07, Sun - Sat)

   cal_time = (cal_hourDay * 100) + cal_min;

   cal_time = Utilities.adjustTime(con, cal_time);   // adjust the time

   if (cal_time < 0) {          // if negative, then we went back or ahead one day

      cal_time = 0 - cal_time;        // convert back to positive value - ok for compare below

      if (cal_time < 100) {           // if hour is zero, then we rolled ahead 1 day

         //
         // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
         //
         cal.add(Calendar.DATE,1);                       // get next day's date
         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);
         day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         cal.add(Calendar.DATE,-1);                      // get yesterday's date
         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);
         day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)
      }
   }

   month++;                            //  adjust month

   //
   //  if its earlier than the time specified for days in advance, do not allow the last day_in_advance
   //
   //  Must check this again when building the calendars!!!!!!!!!
   //
   if (advtime[0] > cal_time) {

      days1--;
   }
   if (advtime[1] > cal_time) {

      days2--;
   }
   if (advtime[2] > cal_time) {

      days3--;
   }
   if (advtime[3] > cal_time) {

      days4--;
   }
   if (advtime[4] > cal_time) {

      days5--;
   }
   if (advtime[5] > cal_time) {

      days6--;
   }
   if (advtime[6] > cal_time) {

      days7--;
   }


     /*    // removed per Mike Scully's request 5/05/09
   //
   //   Medinah Custom - if Monday (today) and days in adv = 30, change to 29 (proshop closed on Mondays)  Case #1225
   //
   if (club.equals( "medinahcc" )) {     // if Medinah and today is Monday

       if ( day_num == 2 || (day_num == 3 && cal_time < 600) ) {

          if (days1 == 30) {
             days1 = 29;
          }
          if (days2 == 30) {
             days2 = 29;
          }
          if (days3 == 30) {
             days3 = 29;
          }
          if (days4 == 30) {
             days4 = 29;
          }
          if (days5 == 30) {
             days5 = 29;
          }
          if (days6 == 30) {
             days6 = 29;
          }
          if (days7 == 30) {
             days7 = 29;
          }

       }
   }
      */


/*
   //
   //   Los Coyotes Custom - change the days in adv for Secondary Members from to 3 days
   //
   if (club.equals( "loscoyotes" ) && mtype.startsWith( "Secondary" )) {

      days1 = 3;
      days2 = 3;
      days3 = 3;
      days4 = 3;
      days5 = 3;
      days6 = 3;
      days7 = 3;
   }
*/

   //
   //  put the 'days in advance' values in an array to be used below
   //
   advdays[0] = days1;
   advdays[1] = days2;
   advdays[2] = days3;
   advdays[3] = days4;
   advdays[4] = days5;
   advdays[5] = days6;
   advdays[6] = days7;

   //
   //  Set value in daysArray for each day up to max
   //
   day_num--;                           // convert today's day_num to index (0 - 6)

   for (index = 0; index < max; index++) {

      days = advdays[day_num];             // get days in advance for day of the week

      day_num++;                           // bump to next day of week

      if (day_num > 6) {                   // if wrapped past end of week

         day_num = 0;
      }

      date = (year * 10000) + (month * 100) + day;     // create date (yyyymmdd) for this day

      //
      // roll cal ahead 1 day for next time thru here
      //
      cal.add(Calendar.DATE,1);                       // get next day's date
      year = cal.get(Calendar.YEAR);
      month = cal.get(Calendar.MONTH) +1;
      day = cal.get(Calendar.DAY_OF_MONTH);

      //
      // check if this day can be accessed by members
      //
      //    0 = No, 1 = Yes, 2 = Yes for Lottery only
      //
      if (days >= index) {               // if ok for this day (use index since today is automatic)

         this.days[index] = 1;        // set ok in array

      } else {

         // default to no access, then check for lottery and set to 2 if needed
         this.days[index] = 0;        // set to no access in array

         //
         //  determine if a lottery is setup for this day, and if the signup is longer than 'd' days
         //
         if (activity_id == 0 && lott != 0) {                 // if lottery supported by this club

            int found = 0;      // init skip switch
            int tmp_err = 0;    // failure location check

            //
            //  Look for any lotteries on this date (any course) - up to 3 of them for one day
            //
            try {
               PreparedStatement pstmt1 = con.prepareStatement (
                  "SELECT lottery " +
                  "FROM teecurr2 WHERE date = ? AND lottery != ''");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setLong(1, date);
               rs = pstmt1.executeQuery();      // execute the prepared stmt
               tmp_err = 1;

               if (rs.next()) {

                  lottery1 = rs.getString(1);

                  PreparedStatement pstmt1b = con.prepareStatement (
                     "SELECT sdays " +
                     "FROM lottery3 WHERE name = ?");

                  pstmt1b.clearParameters();        // clear the parms
                  pstmt1b.setString(1, lottery1);
                  rs2 = pstmt1b.executeQuery();      // execute the prepared stmt
                  tmp_err = 2;

                  if (rs2.next()) {

                     sdays = rs2.getInt(1);    // get days in advance to start taking requests

                  }  // end of IF lottery days 1
                  pstmt1b.close();

                  if (sdays >= index || club.equals("invernessclub")) {       // if ok for this day

                     found = 1;                // indicate found

                  } else {         // check for another (different) lottery on this date

                     PreparedStatement pstmt2 = con.prepareStatement (
                        "SELECT lottery " +
                        "FROM teecurr2 WHERE date = ? AND lottery != ? AND lottery != ''");

                     pstmt2.clearParameters();        // clear the parms
                     pstmt2.setLong(1, date);
                     pstmt2.setString(2, lottery1);
                     rs3 = pstmt2.executeQuery();      // execute the prepared stmt
                     tmp_err = 3;

                     if (rs3.next()) {

                        lottery2 = rs3.getString(1);

                        PreparedStatement pstmt2b = con.prepareStatement (
                           "SELECT sdays " +
                           "FROM lottery3 WHERE name = ?");

                        pstmt2b.clearParameters();        // clear the parms
                        pstmt2b.setString(1, lottery2);
                        rs2 = pstmt2b.executeQuery();      // execute the prepared stmt
                        tmp_err = 4;

                        if (rs2.next()) {

                           sdays = rs2.getInt(1);    // get days in advance to start taking requests

                        }  // end of IF lottery days 1
                        pstmt2b.close();

                        if (sdays >= index || club.equals("invernessclub")) {       // if ok for this day

                           found = 1;                // indicate found

                        } else {         // check for another (different) lottery on this date

                           PreparedStatement pstmt3 = con.prepareStatement (
                              "SELECT lottery " +
                              "FROM teecurr2 WHERE date = ? AND lottery != ? AND lottery != ? AND lottery != ''");

                           pstmt3.clearParameters();        // clear the parms
                           pstmt3.setLong(1, date);
                           pstmt3.setString(2, lottery1);
                           pstmt3.setString(3, lottery2);
                           rs4 = pstmt3.executeQuery();      // execute the prepared stmt
                           tmp_err = 5;

                           if (rs4.next()) {

                              lottery2 = rs4.getString(1);

                              PreparedStatement pstmt3b = con.prepareStatement (
                                 "SELECT sdays " +
                                 "FROM lottery3 WHERE name = ?");

                              pstmt3b.clearParameters();        // clear the parms
                              pstmt3b.setString(1, lottery2);
                              rs2 = pstmt3b.executeQuery();      // execute the prepared stmt
                              tmp_err = 6;

                              if (rs2.next()) {

                                 sdays = rs2.getInt(1);    // get days in advance to start taking requests

                              }  // end of IF lottery days 1
                              pstmt3b.close();

                              if (sdays >= index || club.equals("invernessclub")) {       // if ok for this day

                                 found = 1;                // indicate found
                              }
                           }  // end of IF lottery 3
                           pstmt3.close();

                        }  // end of IF found

                     }  // end of IF lottery 2
                     pstmt2.close();

                  }  // end of IF found

               }  // end of IF lottery 1
               pstmt1.close();
            }
            catch (Exception e1) {

               last_error = "Error2 (lott check " + tmp_err + ") in SystemUtils.daysInAdv for club: " + club + ". Exception: " +e1.getMessage();
               Utilities.logError(last_error);
               return;
            }

            if (found != 0) {                   // if a lottery was found for this day

               this.days[index] = 2;       // set ok for lottery in array
            }

         }        // end of IF lottery supported

      }          // end of IF days check

   }  // end of FOR max

      
  }

}
