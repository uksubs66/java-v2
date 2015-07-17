/***************************************************************************************
 *   getItem:  This class will define an event, lottery, wait list or other occurance
 *              that needs to be handled when building a tee sheet
 *
 *
 *   called by:  several
 *
 *   created: 06/18/2008   Paul
 *
 *   last updated:
 *
 *          12/09/09   When looking for events only check those that are active.
 *          10/22/09   getEvents - get the actual star time of the event (for shotgun events).
 *          12/08/08   Added restriction suspension checking to getRestrictions
 *          10/16/08   Changed unused db/parm field auto_assign to member_view_teesheet 
 *
 *  notes:
 *          verifySlot.adjustTime doesn't have AZ or HI timezone support
 *          events override lotteries
 *
 ***************************************************************************************
 */

package com.foretees.common;

import java.sql.*;
import java.util.*;

import com.foretees.common.parmItem;


public class getItem {
    
    
 public static void getLotteries (long date, int index, String courseName1, String day_name, parmItem parm, Connection con) 
         throws Exception {
     
     
    //parmItem parm = new parmItem();          // allocate a parm block
   
    ResultSet rs = null;
    
    int curr_time = 0;
    int lskip = 0;
    int lstate = 0;           // lottery state
    int templstate = 0;       // temp lottery state
    
    int sdays = 0;
    int sdtime = 0;
    int edays = 0;
    int edtime = 0;
    int pdays = 0;
    int ptime = 0;
    int slots = 0;
   
    int stime = 0;
    int etime = 0;
    int fb = 0;
    
    String tmp_fb = "";
    //String day_name = "";
    String lottery = "";
    String lottery_color = "";
    String lottery_recurr = "";

    String lott1 = "";        // name
    String lcolor1 = "";      // color
    int sdays1 = 0;           // days in advance to start taking requests
    int sdtime1 = 0;          // time of day to start taking requests
    int edays1 = 0;           // days in advance to stop taking requests
    int edtime1 = 0;          // time of day to stop taking requests
    int pdays1 = 0;           // days in advance to process the lottery
    int ptime1 = 0;           // time of day to process the lottery
    int slots1 = 0;           // # of consecutive groups allowed
    int lskip1 = 0;           // skip tee time displays 
    int lstate1 = 0;          // lottery state
                               //    1 = before time to take requests (too early for requests)
                               //    2 = after start time, before stop time (ok to take requests)
                               //    3 = after stop time, before process time (late, but still ok for pro)
                               //    4 = requests have been processed but not approved (no new tee times now)
                               //    5 = requests have been processed & approved (ok for all tee times now)
                               //

    String lott2 = "";        // ditto for 2nd lottery on this day
    String lcolor2 = "";
    int sdays2 = 0;
    int sdtime2 = 0;
    int edays2 = 0;
    int edtime2 = 0;
    int pdays2 = 0;
    int ptime2 = 0;
    int slots2 = 0;
    int lskip2 = 0;         
    int lstate2 = 0;            

    String lott3 = "";        // ditto for 3rd lottery on this day 
    String lcolor3 = "";
    int sdays3 = 0;
    int sdtime3 = 0;
    int edays3 = 0;
    int edtime3 = 0;
    int pdays3 = 0;
    int ptime3 = 0;
    int slots3 = 0;
    int lskip3 = 0;         
    int lstate3 = 0;

    String lott4 = "";        // ditto for 4th lottery on this day (max of 4 for now)!!!!!!
    String lcolor4 = "";
    int sdays4 = 0;
    int sdtime4 = 0;
    int edays4 = 0;
    int edtime4 = 0;
    int pdays4 = 0;
    int ptime4 = 0;
    int slots4 = 0;
    int lskip4 = 0;
    int lstate4 = 0;
   
    int count = 0;
    
    String sql = "";
    
    if (courseName1.equals( "-ALL-" )) {
    sql = "SELECT name, recurr, color, sdays, sdtime, edays, edtime, pdays, ptime, slots, stime, etime, fb " +
          "FROM lottery3 WHERE sdate <= ? AND edate >= ? ORDER BY stime";
    } else {
    sql = "SELECT name, recurr, color, sdays, sdtime, edays, edtime, pdays, ptime, slots, stime, etime, fb " +
          "FROM lottery3 WHERE sdate <= ? AND edate >= ? " +
           "AND (courseName = ? OR courseName = '-ALL-') ORDER BY stime";
    }

    try {
        
      PreparedStatement pstmt7d = con.prepareStatement (sql);
    
      //
      //  check for lotteries
      //
      pstmt7d.clearParameters();          // clear the parms
      pstmt7d.setLong(1, date);
      pstmt7d.setLong(2, date);

      if (!courseName1.equals( "-ALL-" )) {
         pstmt7d.setString(3, courseName1);
      }

      rs = pstmt7d.executeQuery();      // find all matching lotteries, if any

      while (rs.next()) {

         lottery = rs.getString(1);
         lottery_recurr = rs.getString(2);
         lottery_color = rs.getString(3);
         sdays = rs.getInt(4);
         sdtime = rs.getInt(5);
         edays = rs.getInt(6);
         edtime = rs.getInt(7);
         pdays = rs.getInt(8);
         ptime = rs.getInt(9);
         slots = rs.getInt(10);
         stime = rs.getInt(11);
         etime = rs.getInt(12);
         tmp_fb = rs.getString(13);
         
         if (tmp_fb.equalsIgnoreCase("Both")) {
             fb = 2;
         } else if (tmp_fb.equalsIgnoreCase("Front")) {
             fb = 0;
         } else if (tmp_fb.equalsIgnoreCase("Back")) {
             fb = 1;
         }

         //
         //  We must check the recurrence for this day (Monday, etc.)
         //
         if ((lottery_recurr.equals( "Every " + day_name )) ||          // if this day
             (lottery_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
             ((lottery_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
               (!day_name.equalsIgnoreCase( "saturday" )) &&
               (!day_name.equalsIgnoreCase( "sunday" ))) ||
             ((lottery_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
              (day_name.equalsIgnoreCase( "saturday" ))) ||
             ((lottery_recurr.equalsIgnoreCase( "all weekends" )) &&
              (day_name.equalsIgnoreCase( "sunday" )))) {


            if ((!lottery.equals( lott1 )) && (lott1.equals( "" ))) {

               lott1 = lottery;
               lcolor1 = lottery_color;
               sdays1 = sdays;
               sdtime1 = sdtime;
               edays1 = edays;
               edtime1 = edtime;
               pdays1 = pdays;
               ptime1 = ptime;
               slots1 = slots;

               if (lottery_color.equalsIgnoreCase( "default" )) {

                  lcolor1 = "#F5F5DC";
               }               
               
                parm.name[0] = lottery;
                parm.stime[0] = stime;
                parm.etime[0] = etime;
                parm.courseName[0] = courseName1;
                parm.fb[0] = fb;
                parm.color[0] = lottery_color;
                parm.count++;
    
            } else {

               if ((!lottery.equals( lott1 )) && (!lottery.equals( lott2 )) && (lott2.equals( "" ))) {

                  lott2 = lottery;
                  lcolor2 = lottery_color;
                  sdays2 = sdays;
                  sdtime2 = sdtime;
                  edays2 = edays;
                  edtime2 = edtime;
                  pdays2 = pdays;
                  ptime2 = ptime;
                  slots2 = slots;

                  if (lottery_color.equalsIgnoreCase( "default" )) {

                     lcolor2 = "#F5F5DC";
                  }
                  
                    parm.name[1] = lottery;
                    parm.stime[1] = stime;
                    parm.etime[1] = etime;
                    parm.courseName[1] = courseName1;
                    parm.fb[1] = fb;
                    parm.color[1] = lottery_color;
                    parm.count++;
    
               } else {

                  if ((!lottery.equals( lott1 )) && (!lottery.equals( lott2 )) && (!lottery.equals( lott3 )) && (lott3.equals( "" ))) {

                     lott3 = lottery;
                     lcolor3 = lottery_color;
                     sdays3 = sdays;
                     sdtime3 = sdtime;
                     edays3 = edays;
                     edtime3 = edtime;
                     pdays3 = pdays;
                     ptime3 = ptime;
                     slots3 = slots;

                     if (lottery_color.equalsIgnoreCase( "default" )) {

                        lcolor3 = "#F5F5DC";
                     }
                     
                    parm.name[2] = lottery;
                    parm.stime[2] = stime;
                    parm.etime[2] = etime;
                    parm.courseName[2] = courseName1;
                    parm.fb[2] = fb;
                    parm.color[2] = lottery_color;
                    parm.count++;
                    
                  } else {

                     if ((!lottery.equals( lott1 )) && (!lottery.equals( lott2 )) && (!lottery.equals( lott3 )) && (!lottery.equals( lott4 )) && (lott4.equals( "" ))) {

                        lott4 = lottery;
                        lcolor4 = lottery_color;
                        sdays4 = sdays;
                        sdtime4 = sdtime;
                        edays4 = edays;
                        edtime4 = edtime;
                        pdays4 = pdays;
                        ptime4 = ptime;
                        slots4 = slots;

                        if (lottery_color.equalsIgnoreCase( "default" )) {

                           lcolor4 = "#F5F5DC";
                        }
                        
                        parm.name[3] = lottery;
                        parm.stime[3] = stime;
                        parm.etime[3] = etime;
                        parm.courseName[3] = courseName1;
                        parm.fb[3] = fb;
                        parm.color[3] = lottery_color;
                        parm.count++;
               
                     }
                  }
               }
            }
         }
       }                  // end of while
       pstmt7d.close();

    } catch (Exception exc) {

         throw new Exception("getItem.getLotteries - getting lottery details, Error=" + exc.getMessage());
    }
      


    try {
      
      //
      //  Process the lotteries if there are any for this day
      //
      //    Determine which state we are in (before req's, during req's, before process, after process)
      //
      
      sql = "SELECT state FROM lreqs3 WHERE name = ? AND date = ?";
      
      if (!courseName1.equals( "-ALL-" )) {
         sql += " AND courseName = ?";
      }

      if (!lott1.equals( "" )) {

         //
         //  Get the current time
         //
         Calendar cal3 = new GregorianCalendar();    // get todays date
         int cal_hour = cal3.get(Calendar.HOUR_OF_DAY);  // 00 - 23 (military time)
         int cal_min = cal3.get(Calendar.MINUTE);
           
         curr_time = (cal_hour * 100) + cal_min;

         curr_time = verifySlot.adjustTime(con, curr_time);   // adjust the time

         if (curr_time < 0) {          // if negative, then we went back or ahead one day

            curr_time = 0 - curr_time;        // convert back to positive value
         }
           
         //
         //  now check the day and time values        
         //
         if (index > sdays1) {       // if we haven't reached the start day yet
           
            lstate1 = 1;                    // before time to take requests

         } else {

            if (index == sdays1) {   // if this is the start day

               if (curr_time >= sdtime1) {   // have we reached the start time?

                  lstate1 = 2;              // after start time, before stop time to take requests

               } else {

                  lstate1 = 1;              // before time to take requests
               }
            } else {                        // we are past the start day

               lstate1 = 2;                 // after start time, before stop time to take requests
            }

            if (index == edays1) {   // if this is the stop day

               if (curr_time >= edtime1) {   // have we reached the stop time?

                  lstate1 = 3;              // after stop time, before process time 
               }
            }

            if (index < edays1) {   // if we are past the stop day

               lstate1 = 3;                // after stop time, before process time
            }
         }

         if (lstate1 == 3) {                // if we are now in state 3, check for state 4
          
            if (index == pdays1) {   // if this is the process day
                                                                    
               if (curr_time >= ptime1) {    // have we reached the process time?

                  lstate1 = 4;              // after process time
               }
            }

            if (index < pdays1) {   // if we are past the process day

               lstate1 = 4;                // after process time
            }
         }

         if (lstate1 == 4) {                // if we are now in state 4, check for pending approval

            PreparedStatement pstmt12 = con.prepareStatement (sql);

            pstmt12.clearParameters();        // clear the parms
            pstmt12.setString(1, lott1);
            pstmt12.setLong(2, date);

            if (!courseName1.equals( "-ALL-" )) {
               pstmt12.setString(3, courseName1);
            }

            rs = pstmt12.executeQuery();

            if (!rs.next()) {             // if none waiting approval

               lstate1 = 5;              // state 5 - after process & approval time

            } else {                     // still some reqs waiting

               templstate = rs.getInt(1);  // get its state

               if (templstate == 5) {      // if we processed already (some not assigned)

                  lstate1 = 5;
               }
            }
            pstmt12.close();

         }
      }   // end of if lott1
        
      if (!lott2.equals( "" )) {

         //
         //  check the day and time values
         //
         if (index > sdays2) {       // if we haven't reached the start day yet

            lstate2 = 1;                    // before time to take requests

         } else {

            if (index == sdays2) {   // if this is the start day

               if (curr_time >= sdtime2) {   // have we reached the start time?

                  lstate2 = 2;              // after start time, before stop time to take requests

               } else {

                  lstate2 = 1;              // before time to take requests
               }
            } else {                        // we are past the start day

               lstate2 = 2;                 // after start time, before stop time to take requests
            }

            if (index == edays2) {   // if this is the stop day

               if (curr_time >= edtime2) {   // have we reached the stop time?

                  lstate2 = 3;              // after stop time, before process time
               }
            }

            if (index < edays2) {   // if we are past the stop day

               lstate2 = 3;                // after stop time, before process time
            }
         }

         if (lstate2 == 3) {                // if we are now in state 3, check for state 4

            if (index == pdays2) {   // if this is the process day

               if (curr_time >= ptime2) {    // have we reached the process time?

                  lstate2 = 4;              // after process time
               }
            }

            if (index < pdays2) {   // if we are past the process day

               lstate2 = 4;                // after process time
            }
         }

         if (lstate2 == 4) {                // if we are now in state 4, check for pending approval

            PreparedStatement pstmt12 = con.prepareStatement (sql);

            pstmt12.clearParameters();        // clear the parms
            pstmt12.setString(1, lott2);
            pstmt12.setLong(2, date);

            if (!courseName1.equals( "-ALL-" )) {
               pstmt12.setString(3, courseName1);
            }

            rs = pstmt12.executeQuery();

            if (!rs.next()) {             // if none waiting approval

               lstate2 = 5;              // state 5 - after process & approval time

            } else {                     // still some reqs waiting

               templstate = rs.getInt(1);  // get its state

               if (templstate == 5) {      // if we processed already (some not assigned)

                  lstate2 = 5;
               }
            }
            pstmt12.close();

         }
      }   // end of if lott2

      if (!lott3.equals( "" )) {

         //
         //  check the day and time values
         //
         if (index > sdays3) {       // if we haven't reached the start day yet

            lstate3 = 1;                    // before time to take requests

         } else {

            if (index == sdays3) {   // if this is the start day

               if (curr_time >= sdtime3) {   // have we reached the start time?

                  lstate3 = 2;              // after start time, before stop time to take requests

               } else {

                  lstate3 = 1;              // before time to take requests
               }
            } else {                        // we are past the start day

               lstate3 = 2;                 // after start time, before stop time to take requests
            }

            if (index == edays3) {   // if this is the stop day

               if (curr_time >= edtime3) {   // have we reached the stop time?

                  lstate3 = 3;              // after stop time, before process time
               }
            }

            if (index < edays3) {   // if we are past the stop day

               lstate3 = 3;                // after stop time, before process time
            }
         }

         if (lstate3 == 3) {                // if we are now in state 3, check for state 4

            if (index == pdays3) {   // if this is the process day

               if (curr_time >= ptime3) {    // have we reached the process time?

                  lstate3 = 4;              // after process time
               }
            }

            if (index < pdays3) {   // if we are past the process day

               lstate3 = 4;                // after process time
            }
         }

         if (lstate3 == 4) {                // if we are now in state 4, check for pending approval

            PreparedStatement pstmt12 = con.prepareStatement (sql);

            pstmt12.clearParameters();        // clear the parms
            pstmt12.setString(1, lott3);
            pstmt12.setLong(2, date);

            if (!courseName1.equals( "-ALL-" )) {
               pstmt12.setString(3, courseName1);
            }

            rs = pstmt12.executeQuery();

            if (!rs.next()) {             // if none waiting approval

               lstate3 = 5;              // state 5 - after process & approval time

            } else {                     // still some reqs waiting

               templstate = rs.getInt(1);  // get its state

               if (templstate == 5) {      // if we processed already (some not assigned)

                  lstate3 = 5;
               }
            }
            pstmt12.close();

         }
      }   // end of if lott3

      if (!lott4.equals( "" )) {

         //
         //  check the day and time values
         //
         if (index > sdays4) {       // if we haven't reached the start day yet

            lstate4 = 1;                    // before time to take requests

         } else {

            if (index == sdays4) {   // if this is the start day

               if (curr_time >= sdtime4) {   // have we reached the start time?

                  lstate4 = 2;              // after start time, before stop time to take requests

               } else {

                  lstate4 = 1;              // before time to take requests
               }
            } else {                        // we are past the start day

               lstate4 = 2;                 // after start time, before stop time to take requests
            }

            if (index == edays4) {   // if this is the stop day

               if (curr_time >= edtime4) {   // have we reached the stop time?

                  lstate4 = 3;              // after stop time, before process time
               }
            }

            if (index < edays4) {   // if we are past the stop day

               lstate4 = 3;                // after stop time, before process time
            }
         }

         if (lstate4 == 3) {                // if we are now in state 3, check for state 4

            if (index == pdays4) {   // if this is the process day

               if (curr_time >= ptime4) {    // have we reached the process time?

                  lstate4 = 4;              // after process time
               }
            }

            if (index < pdays4) {   // if we are past the process day

               lstate4 = 4;                // after process time
            }
         }

         if (lstate4 == 4) {                // if we are now in state 4, check for pending approval

            PreparedStatement pstmt12 = con.prepareStatement (sql);

            pstmt12.clearParameters();        // clear the parms
            pstmt12.setString(1, lott4);
            pstmt12.setLong(2, date);
              
            if (!courseName1.equals( "-ALL-" )) {
               pstmt12.setString(3, courseName1);
            }

            rs = pstmt12.executeQuery();

            if (!rs.next()) {             // if none waiting approval

               lstate4 = 5;              // state 5 - after process & approval time

            } else {                     // still some reqs waiting

               templstate = rs.getInt(1);  // get its state

               if (templstate == 5) {      // if we processed already (some not assigned)

                  lstate4 = 5;
               }
            }
            pstmt12.close();

         }
      }   // end of if lott4

 } catch (Exception exc) {
    
      throw new Exception("getItem.getLotteries - computing lottery state, Error=" + exc.getMessage());
 }
 

    //
    // Save the lottery states we computed in the parm block
    //
    parm.lstate[0] = lstate1;
    parm.lstate[1] = lstate2;
    parm.lstate[2] = lstate3;
    parm.lstate[3] = lstate4;

 }   
    
 
 public static void getEvents (long date, String courseName1, parmItem parm, Connection con) 
         throws Exception {
     

    int count = 0;
    
    String ecolor = "";
    String tmp_fb = "";
    int act_hr = 0;
    int act_min = 0;
    
    String sql = "SELECT name, stime, etime, courseName, fb, stime2, etime2, fb2, color, season, type, act_hr, act_min " +
                 "FROM events2b WHERE date = ? AND inactive = 0 ";

    if (!courseName1.equals( "-ALL-" )) {
        sql += "AND (courseName = ? OR courseName = '-ALL-') ";
    }
    
    sql += "ORDER BY stime";
    
    try {

        PreparedStatement pstmt = con.prepareStatement (sql);

        pstmt.clearParameters();
        pstmt.setLong(1, date);

        if (!courseName1.equals( "-ALL-" )) {
            pstmt.setString(2, courseName1);
        }

        ResultSet rs = pstmt.executeQuery();

        while (rs.next() && count < parm.MAX) {
               
            parm.name[count] = rs.getString("name");
            parm.courseName[count] = rs.getString("courseName");
            parm.season_long[count] = (rs.getInt("season") == 1);
            parm.event_type[count] = rs.getInt("type");
            
            parm.stime[count] = rs.getInt("stime");
            parm.etime[count] = rs.getInt("etime");
            tmp_fb = rs.getString("fb");
            
            if (tmp_fb.equalsIgnoreCase("Both")) {
                parm.fb[count] = 2;
            } else if (tmp_fb.equalsIgnoreCase("Front")) {
                parm.fb[count] = 0;
            } else if (tmp_fb.equalsIgnoreCase("Back")) {
                parm.fb[count] = 1;
            }
            
            parm.stime2[count] = rs.getInt("stime2");
            parm.etime2[count] = rs.getInt("etime2");
            tmp_fb = rs.getString("fb2");
            
            if (tmp_fb.equalsIgnoreCase("Both")) {
                parm.fb2[count] = 2;
            } else if (tmp_fb.equalsIgnoreCase("Front")) {
                parm.fb2[count] = 0;
            } else if (tmp_fb.equalsIgnoreCase("Back")) {
                parm.fb2[count] = 1;
            }
            
            ecolor = rs.getString("color");
            if (ecolor.equalsIgnoreCase( "default" )) ecolor = "#F5F5DC";
            parm.color[count] = ecolor;

            act_hr = rs.getInt("act_hr");
            act_min = rs.getInt("act_min");

            parm.act_time[count] = (act_hr * 100) + act_min;      // get the actual star time
            
            count++;

        }
        
        parm.count = count;

        pstmt.close();

    } catch (Exception exc) {

        throw new Exception("getItem.getEvents - gathering event information, Error=" + exc.getMessage());
    }
    
 }
 
 
 public static void getRestrictions (long date, String courseName1, parmItem parm, Connection con) 
         throws Exception {
     
   ResultSet rs = null;
   
   String day_name = "";
           
   String rest = "";
   String rcolor = "";
   String rest_recurr = "";
   String rest1 = "";
   String rcolor1 = "";
   String rest2 = "";
   String rcolor2 = "";
   String rest3 = "";
   String rcolor3 = "";
   String rest4 = "";
   String rcolor4 = "";
   String rest05 = "";
   String rcolor5 = "";
   String rest6 = "";
   String rcolor6 = "";
   String rest7 = "";
   String rcolor7 = "";
   String rest8 = "";
   String rcolor8 = "";
   
   
    //
    //   Statements to find any restrictions, events or lotteries for today
    //
    String sql = "";

    if (courseName1.equals( "-ALL-" )) {
        sql = "SELECT name, recurr, color, id, stime, etime FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
              "AND showit = 'Yes' ORDER BY stime";
    } else {
        sql = "SELECT name, recurr, color, id, stime, etime FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
              "AND (courseName = ? OR courseName = '-ALL-') AND showit = 'Yes' ORDER BY stime";
    }
    
    try {
        
      PreparedStatement pstmt7b = con.prepareStatement (sql);
     
      //
      //  Scan the events, restrictions and lotteries to build the legend
      //
      pstmt7b.clearParameters();          // clear the parms
      pstmt7b.setLong(1, date);
      pstmt7b.setLong(2, date);
        
      if (!courseName1.equals( "-ALL-" )) {
         pstmt7b.setString(3, courseName1);
      }

      rs = pstmt7b.executeQuery();      // find all matching restrictions, if any

      while (rs.next()) {

         rest = rs.getString(1);
         rest_recurr = rs.getString(2);
         rcolor = rs.getString(3);

         boolean showRest = getRests.showRest(rs.getInt("id"), -99, rs.getInt("stime"), rs.getInt("etime"), date, day_name, courseName1, con);
         
         if (showRest) {    // Only display on legend if not suspended for entire day
             //
             //  We must check the recurrence for this day (Monday, etc.)
             //
             if ((rest_recurr.equals( "Every " + day_name )) ||          // if this day
                 (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
                 ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
                   (!day_name.equalsIgnoreCase( "saturday" )) &&
                   (!day_name.equalsIgnoreCase( "sunday" ))) ||
                 ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
                  (day_name.equalsIgnoreCase( "saturday" ))) ||
                 ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
                  (day_name.equalsIgnoreCase( "sunday" )))) {


                if ((!rest.equals( rest1 )) && (rest1.equals( "" ))) {

                   rest1 = rest;
                   rcolor1 = rcolor;

                   if (rcolor.equalsIgnoreCase( "default" )) rcolor1 = "#F5F5DC";

                } else {

                   if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (rest2.equals( "" ))) {

                      rest2 = rest;
                      rcolor2 = rcolor;

                      if (rcolor.equalsIgnoreCase( "default" )) rcolor2 = "#F5F5DC";

                   } else {

                      if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rest.equals( rest3 )) && (rest3.equals( "" ))) {

                         rest3 = rest;
                         rcolor3 = rcolor;

                         if (rcolor.equalsIgnoreCase( "default" )) rcolor3 = "#F5F5DC";

                      } else {

                         if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rest.equals( rest3 )) &&
                             (!rest.equals( rest4 )) && (rest4.equals( "" ))) {

                            rest4 = rest;
                            rcolor4 = rcolor;

                            if (rcolor.equalsIgnoreCase( "default" )) rcolor4 = "#F5F5DC"; 

                         } else {

                             if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rest.equals( rest3 )) && (!rest.equals( rest4 )) &&
                                 (!rest.equals( rest05 )) && (rest05.equals( "" ))) {

                                rest05 = rest;
                                rcolor5 = rcolor;

                                if (rcolor.equalsIgnoreCase( "default" )) rcolor5 = "#F5F5DC";

                             } else {

                                 if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rest.equals( rest3 )) && (!rest.equals( rest4 )) && (!rest.equals( rest05 )) &&
                                     (!rest.equals( rest6 )) && (rest6.equals( "" ))) {

                                    rest6 = rest;
                                    rcolor6 = rcolor;

                                    if (rcolor.equalsIgnoreCase( "default" )) rcolor6 = "#F5F5DC";

                                 } else {

                                     if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rest.equals( rest3 )) && (!rest.equals( rest4 )) && (!rest.equals( rest05 )) && (!rest.equals( rest6 )) &&
                                         (!rest.equals( rest7 )) && (rest7.equals( "" ))) {

                                        rest7 = rest;
                                        rcolor7 = rcolor;

                                        if (rcolor.equalsIgnoreCase( "default" )) rcolor7 = "#F5F5DC";

                                     } else {

                                         if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rest.equals( rest3 )) && (!rest.equals( rest4 )) && (!rest.equals( rest05 )) && (!rest.equals( rest6 )) && (!rest.equals( rest7 )) &&
                                             (!rest.equals( rest8 )) && (rest8.equals( "" ))) {

                                            rest8 = rest;
                                            rcolor8 = rcolor;

                                            if (rcolor.equalsIgnoreCase( "default" )) rcolor8 = "#F5F5DC";

                                         }
                                     }
                                 }
                             }
                         }
                      }
                   }
                }
             }
         }
      }                  // end of while
      pstmt7b.close();
          
    } catch (Exception exc) {

        // handle error
    }
    
 }
 
 
 public static void getWaitLists (long date, String courseName, String day_name, parmItem parm, Connection con) 
         throws Exception {
     

    String color = "";
    int count = 0;
    
    try {
        
        PreparedStatement pstmt = con.prepareStatement (
            "SELECT wait_list_id, name, course, color, member_access, member_view_teesheet, " +
                "DATE_FORMAT(sdatetime, '%H%i') AS stime, " +
                "DATE_FORMAT(edatetime, '%H%i') AS etime, " +
                "(SELECT COUNT(*) FROM wait_list_signups wls WHERE wls.wait_list_id = wl.wait_list_id AND wls.date = ?) AS signups1, " +
                "(SELECT COUNT(*) FROM wait_list_signups wls WHERE wls.wait_list_id = wl.wait_list_id AND wls.date = ? AND wls.converted = 0) AS signups2 " +
            "FROM wait_list wl " +
            "WHERE " +
                "DATE_FORMAT(sdatetime, '%Y%m%d') <= ? AND DATE_FORMAT(edatetime, '%Y%m%d') >= ? AND " +
                ((!courseName.equals("-ALL-") ? "(course = ? OR course = '-ALL-') AND " : "")) +
                "" + day_name + " = 1 AND enabled = 1");

        pstmt.clearParameters();
        pstmt.setLong(1, date);
        pstmt.setLong(2, date);
        pstmt.setLong(3, date);
        pstmt.setLong(4, date);
        if (!courseName.equals("-ALL-")) pstmt.setString(5, courseName);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            
            parm.id[count] = rs.getInt("wait_list_id");
            parm.name[count] = rs.getString("name");
            parm.courseName[count] = rs.getString("course");
            parm.stime[count] = rs.getInt("stime");
            parm.etime[count] = rs.getInt("etime");
            parm.signups[count] = rs.getInt("signups1");
            parm.unc_signups[count] = rs.getInt("signups2");
            parm.member_view_teesheet[count] = rs.getInt("member_view_teesheet");
            color = rs.getString("color");
            if (color.equalsIgnoreCase( "default" )) color = "#F5F5DC";
            parm.color[count] = color;
            
            count++;
        }
        
        pstmt.close();

        parm.count = count;
        
    } catch (Exception exc) {

        throw new Exception("getItem.getWaitLists - gathering wait list information, Error=" + exc.getMessage());
    }
    
 }
 
}