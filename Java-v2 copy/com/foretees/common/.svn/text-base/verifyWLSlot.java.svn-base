/***************************************************************************************
 *   verifyWLSlot:  This servlet will provide some common wait list specific methods.
 *
 *       called by:  Member_waitlist_slot
 *                   Proshop_waitlist_slot
 *
 *
 *   created:  4/19/2008   Paul S.
 *
 *
 *   last updated:
 *
 *   5/22/13   Add 'or equal to' an error message in checkReqWindow (if starting time is equal to the ending time).
 *   3/17/13   Comment out debug statement which interfered with json mode output when member notice was active on a waitlist signup
 *   4/27/10   Added guest_ids to checkInUse
 *
 *
 */

package com.foretees.common;

import java.io.*;
import java.sql.*;

 
public class verifyWLSlot {

   private static String rev = ProcessConstants.REV;

   
/**
 //************************************************************************
 //
 //  checkInUse - check if wait list entry is in use and if not, set it
 //               and return the parm block populated with the entry info
 //
 //************************************************************************
 **/

 public static int checkInUse(int signup_id, String user, parmSlot slotParms, Connection con, PrintWriter out)
         throws Exception {


    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;

    int in_use = 1; // default to busy
    int count = 0;

    //
    //  Verify the input parms, if absent then return as if the slot is busy
    //
    if (signup_id != 0 && user != null && !user.equals( "" )) {

        try {

            //out.println("<!-- Checking wait list signup #" + signup_id + " to see if it's busy by " + user + " -->");
            //
            //   Set the entry as busy, IF it is not already
            //
            pstmt = con.prepareStatement (
                "UPDATE wait_list_signups " +
                "SET in_use_by = ?, in_use_at = now() " +
                "WHERE wait_list_signup_id = ? AND " +
                    "(( in_use_by = '' || in_use_by = ? ) || " +
                    "  (UNIX_TIMESTAMP(in_use_at) < ( UNIX_TIMESTAMP() + (6 * 30) )) " +
                    ")");
                
            pstmt.clearParameters();
            pstmt.setString(1, user);
            pstmt.setInt(2, signup_id);
            pstmt.setString(3, user);
            count = pstmt.executeUpdate();
            pstmt.close();
         
            //
            //  If the above was successful, then we now own this notifcation
            //
            if (count > 0) {

                //out.println("<!-- Was not busy.  Making it busy now. -->");
            
                pstmt = con.prepareStatement (
                   "SELECT wls.*, wl.course, " +
                        "DATE_FORMAT(wls.created_datetime, '%m/%d/%Y at %l:%i %p') AS created_at, " +
                        "DATE_FORMAT(wls.date, '%Y%m%d') AS date1, " +
                        "DATE_FORMAT(wls.date, '%W') AS day_name, " +
                        "DATE_FORMAT(wls.date, '%e') AS dd, " +
                        "DATE_FORMAT(wls.date, '%c') AS mm, " +
                        "DATE_FORMAT(wls.date, '%Y') AS yy " +
                   "FROM wait_list_signups wls, wait_list wl " +
                   "WHERE " +
                        "wls.wait_list_id = wl.wait_list_id AND " +
                        "wls.wait_list_signup_id = ?");
/*

                   "SELECT wls.*, c.clubparm_id, wl.course, " +
                        "DATE_FORMAT(wls.created_datetime, '%m/%d/%Y at %l:%i %p') AS created_at, " +
                        "DATE_FORMAT(wls.date, '%Y%m%d') AS date1, " +
                        "DATE_FORMAT(wls.date, '%W') AS day_name, " +
                        "DATE_FORMAT(wls.date, '%e') AS dd, " +
                        "DATE_FORMAT(wls.date, '%c') AS mm, " +
                        "DATE_FORMAT(wls.date, '%Y') AS yy " +
                   "FROM wait_list_signups wls, wait_list wl, clubparm2 c " +
                   "WHERE " +
                        "wls.wait_list_id = wl.wait_list_id AND " +
                        "wl.course = c.courseName AND " +
                        "wls.wait_list_signup_id = ?"

*/
                pstmt.clearParameters();
                pstmt.setInt(1, signup_id);
                rs = pstmt.executeQuery();

                if (rs.next()) {

                   //slotParms.req_datetime = rs.getString( "req_datetime" );
                   slotParms.wait_list_id = rs.getInt( "wait_list_id" );
                   slotParms.dd = rs.getInt( "dd" );
                   slotParms.mm = rs.getInt( "mm" );
                   slotParms.yy = rs.getInt( "yy" );
                   slotParms.date = rs.getInt( "date1" );
                   slotParms.ok_stime = rs.getInt( "ok_stime" );
                   slotParms.ok_etime = rs.getInt( "ok_etime" );
                   //slotParms.course_id = rs.getInt( "clubparm_id" );
                   slotParms.course = rs.getString( "course" );
                   slotParms.last_user = rs.getString( "in_use_by" );
                   slotParms.in_use = (slotParms.last_user.equals("") || slotParms.last_user.equalsIgnoreCase( user )) ? 0 : 1; //rs.getInt( "in_use" );
                   slotParms.hideNotes = rs.getInt( "hideNotes" );
                   slotParms.notes = rs.getString( "notes" );
                   slotParms.converted = rs.getInt( "converted" );
                   slotParms.orig_by = rs.getString( "created_by" );
                   slotParms.orig_at = rs.getString( "created_at" );
                   slotParms.day = rs.getString( "day_name" );
                }

                //out.println("<!-- B4: in_use=" + in_use + " | slotParms.in_use="+ slotParms.in_use + " -->");
                
                in_use = slotParms.in_use;
                
                // if in use by self then allow
                //if (!slotParms.last_user.equalsIgnoreCase( user )) in_use = 1;
                        
                pstmt = con.prepareStatement (
                   "SELECT * " +
                   "FROM wait_list_signups_players " +
                   "WHERE wait_list_signup_id = ? " +
                   "ORDER BY pos");

                pstmt.clearParameters();
                pstmt.setInt(1, signup_id);
                rs = pstmt.executeQuery();

                if (rs.next()) {

                    slotParms.player1 = rs.getString( "player_name" );
                    slotParms.user1 = rs.getString( "username" );
                    slotParms.p1cw = rs.getString( "cw" );
                    slotParms.p91 = rs.getInt( "9hole" );
                    slotParms.guest_id1 = rs.getInt("guest_id");
                    slotParms.players = 1;
                }
                
                if (rs.next()) {

                    slotParms.player2 = rs.getString( "player_name" );
                    slotParms.user2 = rs.getString( "username" );
                    slotParms.p2cw = rs.getString( "cw" );
                    slotParms.p92 = rs.getInt( "9hole" );
                    slotParms.guest_id2 = rs.getInt("guest_id");
                    slotParms.players = 2;
                }
                
                if (rs.next()) {

                    slotParms.player3 = rs.getString( "player_name" );
                    slotParms.user3 = rs.getString( "username" );
                    slotParms.p3cw = rs.getString( "cw" );
                    slotParms.p93 = rs.getInt( "9hole" );
                    slotParms.guest_id3 = rs.getInt("guest_id");
                    slotParms.players = 3;
                }
                
                if (rs.next()) {

                    slotParms.player4 = rs.getString( "player_name" );
                    slotParms.user4 = rs.getString( "username" );
                    slotParms.p4cw = rs.getString( "cw" );
                    slotParms.p94 = rs.getInt( "9hole" );
                    slotParms.guest_id4 = rs.getInt("guest_id");
                    slotParms.players = 4;
                }
                
                if (rs.next()) {

                    slotParms.player5 = rs.getString( "player_name" );
                    slotParms.user5 = rs.getString( "username" );
                    slotParms.p5cw = rs.getString( "cw" );
                    slotParms.p95 = rs.getInt( "9hole" );
                    slotParms.guest_id5 = rs.getInt("guest_id");
                    slotParms.players = 5;
                }
                
            } else {
                //out.println("<!-- Unable to make busy -->");
            }// end if count > 0

            pstmt.close();

          }
          catch (SQLException e) {

             throw new Exception("Error checking in-use - verifyWLSlot.checkInUse - SQL Exception: " + e.getMessage());
          }
          catch (Exception e) {

             throw new Exception("Error checking in-use - verifyWLSlot.checkInUse - Exception: " + e.getMessage());
          }
        
       } // end if 

   return(in_use);
   
 } // end checkInUse

 
 
 /*
 // 
 // return an empty string is no error, else return the problem.
 public static String checkReqWindow(parmSlot slotParms, parmWaitList parmWL) {
     
     String answer = "";
     
     if ( !(parmSlot.ok_stime >= parmWL.start_time) ) {
        answer = "Your start time is earlier than the wait list starting time.";
     } else if ( !(parmSlot.ok_etime <= parmWL.end_time) ) {
        answer = "Your end time is after the wait list will of ended.";
     } else if ( !(parmSlot.ok_stime < parmSlot.ok_etime) ) {
        answer = "Your starting time is after your ending time.";
     }
         
     return answer;
     
 }
 */
 
 
 public static String checkReqWindow(int ok_stime, int ok_etime, int start_time, int end_time) {
 
     String answer = "";
     
     if ( !(ok_stime >= start_time) ) {
        answer = "Your start time is earlier than the wait list starting time.";
     } else if ( !(ok_etime <= end_time) ) {
        answer = "Your end time is after the wait list will of ended.";
     } else if ( !(ok_stime < ok_etime) ) {
        answer = "Your starting time is after or equal to your ending time.";
     }
         
     return answer;    
     
 } // end checkReqWindow
 
 
 public static void checkPlayersAgainstList(parmSlot slotParms, Connection con, PrintWriter out) {
    
    boolean found = false;
    
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    String sql = "" +
            "SELECT player_name " +
            "FROM wait_list_signups_players wlsp, wait_list_signups wls, wait_list wl " +
            "WHERE wl.wait_list_id = ? AND wls.date = ? AND wlsp.username = ? " +
                "AND wl.wait_list_id = wls.wait_list_id " +
                "AND wlsp.wait_list_signup_id = wls.wait_list_signup_id " +
            ( (slotParms.signup_id != 0) ? "AND wlsp.wait_list_signup_id <> ? " : "" ) + 
            "LIMIT 1";

    /*
    out.println("<!-- sql=" + sql + " -->");
    out.println("<!-- slotParms.wait_list_id=" + slotParms.wait_list_id + " -->");
    out.println("<!-- slotParms.signup_id=" + slotParms.signup_id + " -->");
    out.println("<!-- slotParms.date=" + slotParms.date + " -->");
    out.println("<!-- slotParms.user1=" + slotParms.user1 + " -->");
    out.println("<!-- slotParms.user2=" + slotParms.user2 + " -->");
    out.println("<!-- slotParms.user3=" + slotParms.user3 + " -->");
    out.println("<!-- slotParms.user4=" + slotParms.user4 + " -->");
    out.println("<!-- slotParms.user5=" + slotParms.user5 + " -->");
    */
    
    try {
        
        if (!slotParms.user1.equals("")) {
        
            pstmt = con.prepareStatement( sql );
            pstmt.clearParameters();
            pstmt.setInt(1, slotParms.wait_list_id);
            pstmt.setLong(2, slotParms.date);
            pstmt.setString(3, slotParms.user1);
            if (slotParms.signup_id != 0) pstmt.setInt(4, slotParms.signup_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                slotParms.player = rs.getString(1);
                found = true;
            }
        }
        
        if (!found && !slotParms.user2.equals("")) {

            pstmt = con.prepareStatement( sql );
            pstmt.clearParameters();
            pstmt.setInt(1, slotParms.wait_list_id);
            pstmt.setLong(2, slotParms.date);
            pstmt.setString(3, slotParms.user2);
            if (slotParms.signup_id != 0) pstmt.setInt(4, slotParms.signup_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                slotParms.player = rs.getString(1);
                found = true;
            }
        }
        
        if (!found && !slotParms.user3.equals("")) {

            pstmt = con.prepareStatement( sql );
            pstmt.clearParameters();
            pstmt.setInt(1, slotParms.wait_list_id);
            pstmt.setLong(2, slotParms.date);
            pstmt.setString(3, slotParms.user3);
            if (slotParms.signup_id != 0) pstmt.setInt(4, slotParms.signup_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                slotParms.player = rs.getString(1);
                found = true;
            } 
        }
        
        if (!found && !slotParms.user4.equals("")) {

            pstmt = con.prepareStatement( sql );
            pstmt.clearParameters();
            pstmt.setInt(1, slotParms.wait_list_id);
            pstmt.setLong(2, slotParms.date);
            pstmt.setString(3, slotParms.user4);
            if (slotParms.signup_id != 0) pstmt.setInt(4, slotParms.signup_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                slotParms.player = rs.getString(1);
                found = true;
            }
        }
        
        if (!found && !slotParms.user5.equals("")) {

            pstmt = con.prepareStatement( sql );
            pstmt.clearParameters();
            pstmt.setInt(1, slotParms.wait_list_id);
            pstmt.setLong(2, slotParms.date);
            pstmt.setString(3, slotParms.user5);
            if (slotParms.signup_id != 0) pstmt.setInt(4, slotParms.signup_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                slotParms.player = rs.getString(1);
            }
        }
        
        pstmt.close();

    } catch (Exception exc) {
        
        verifySlot.logError("verifyWLSlot.checkPlayersAgainstList: club=" + slotParms.club + ", Error=" + exc.getMessage());
    }
    
 } // end checkPlayersAgainstList
 
}
