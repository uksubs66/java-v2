/***************************************************************************************
 *   verifyActSlot:  This servlet will provide some common tee time request processing methods.
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
 *         3/04/14  Philly Cricket Club (philcricket) - Added custom to prevent members from the same family mNum from being able to book overlapping reservations (case 2364).
 *         1/06/14  Added additional run_mode to checkInUseM to facilitate finding the next available time(s) when a user's originally selected time is not available.
 *         9/25/13  Added checkMaxOrigBy() method to check a member's current count of originated times for a given day against their allowed max.
 *         1/18/13  Fixed an issue which prevented players being removed from the middle of a reservation. After a player has been shifted, all oldPlayer values will now be checked to see if they were previously there.
 *        12/17/12  Updated 9/11 fix to make the code work when 1 or 3 max_players is selected as well as 2 or 4.
 *         9/11/12  Added temporary fix in checkSched for courts which have a max_players value of 2 instead of 4. ****** FIND A PERMANENT SOLUTION FOR THIS ASAP ******
 *         5/24/12  Added checkMemberHasAccess() method to check if a sheet_id has players in it, and if it does, check if the current user is a part of that reservation.
 *        12/08/11  Switched order of conditionals/loops in checkSched so we don't even enter the second loop if the player slot is blank or hasn't changed.
 *        12/06/11  Ballantyne CC (ballantyne) - Added custom to checkSched so lesson bookings are treated as normal reservations when it comes to overlaping reservations and time-between requirements (case 2087).
 *        11/07/11  Updated checkInUse to set slotParms.in_slots equal to the single slot_id if no existing related_ids were found, and the user selected a single time slot.
 *         9/21/11  Updated checkInUseM to no longer search later in the time sheet for available times, but return what times were available even if the
 *                  entire requested time could not be found (old method of running is still there in case we'd like to use that as an alternate eventually)
 *         7/20/11  Added the checking of restrictions to checkInUseM processing.
 *         2/25/11  Updated checkSched to properly track how many rounds a player has played on a given day, and to flag a member
 *                  if they are already part of a reservation that overlaps times with the current reservation.
 *         2/17/11  Converted all references to disallow_joins over to the new force_singles field.
 *         7/12/10  Added checkSlotHasPlayers method for performing a quick check for existing players for a given sheet_id
 *         4/15/10  Updated parmClub calls
 *        12/28/09  Added checkInUseM for handling requests for consecutive times
 *
 */

package com.foretees.common;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.joda.time.*;
import org.joda.time.format.*;

public class verifyActSlot {


/**
 //************************************************************************
 //
 //  checkInUse - check if time slot is in use and if not mark it in use
 //               and return the parm block populated with the entry info
 //
 //************************************************************************
 **/

 public static int checkInUse(int slot_id, String user, parmSlot slotParms, Connection con, PrintWriter out)
         throws Exception {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int in_use = 1; // default to busy
    int count = 0;

    //
    //  Verify the input parms, if absent then return as if the slot is busy
    //
    if (slot_id != 0 && user != null && !user.equals( "" )) {
        
        //int slotHoldTime = getSlotHoldTime(session); // we need to pass the session here to do this

        try {

            // out.println("<!-- Checking activity signup #" + slot_id + " to see if it's busy by " + user + " -->");
            //
            //   Set the entry as busy, IF it is not already
            //
            pstmt = con.prepareStatement (
                "UPDATE activity_sheets " +
                "SET in_use_by = ?, in_use_at = now() " +
                "WHERE sheet_id = ? AND " +
                    "( (in_use_by = '' || in_use_by = ? ) || " +
                    " (UNIX_TIMESTAMP(in_use_at) + (6 * 60)) < UNIX_TIMESTAMP() " + // "  (UNIX_TIMESTAMP(in_use_at) < ( UNIX_TIMESTAMP() + (6 * 30) )) " +
                    ")");

            pstmt.clearParameters();
            pstmt.setString(1, user);
            pstmt.setInt(2, slot_id);
            pstmt.setString(3, user);
            count = pstmt.executeUpdate();
            pstmt.close();

            // out.println("<!-- Was not busy.  Making it busy now. -->");

            // Look up details on the slot in question regardless of whether it was free or not, so we can offer them alternate times based on this info
            pstmt = con.prepareStatement (
               "SELECT *, " +
                    "DATE_FORMAT(date_time, '%Y%m%d') AS date1, " +
                    "DATE_FORMAT(date_time, '%k%i') AS time, " +
                    "DATE_FORMAT(date_time, '%W') AS day_name, " +
                    "DATE_FORMAT(date_time, '%e') AS dd, " +
                    "DATE_FORMAT(date_time, '%c') AS mm, " +
                    "DATE_FORMAT(date_time, '%Y') AS yy " +
               "FROM activity_sheets " +
               "WHERE " +
                    "sheet_id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, slot_id);
            rs = pstmt.executeQuery();

            if (rs.next()) {

               slotParms.slot_id = slot_id;
               slotParms.activity_id = rs.getInt( "activity_id" );
               slotParms.dd = rs.getInt( "dd" );
               slotParms.mm = rs.getInt( "mm" );
               slotParms.yy = rs.getInt( "yy" );
               slotParms.date = rs.getInt( "date1" );
               slotParms.time = rs.getInt( "time" );
               slotParms.last_user = rs.getString( "in_use_by" );
               slotParms.in_use = (slotParms.last_user.equals("") || slotParms.last_user.equalsIgnoreCase( user )) ? 0 : 1; //rs.getInt( "in_use" );
               slotParms.hideNotes = rs.getInt( "hideNotes" );
               slotParms.notes = rs.getString( "notes" );
               slotParms.day = rs.getString( "day_name" );
               slotParms.force_singles = rs.getInt("force_singles");
               slotParms.in_slots = rs.getString("related_ids");
               slotParms.report_ignore = rs.getInt("report_ignore");
            }

            // out.println("<!-- B4: in_use=" + in_use + " | slotParms.in_use="+ slotParms.in_use + " -->");

            // If the time was successfully set in use earlier, continue with additional processing
            if (count > 0) {
                
                in_use = slotParms.in_use;

                // detremin the slotParms.slots value if in_slots is specified
                if (!slotParms.in_slots.equals("")) {

                    StringTokenizer tok = new StringTokenizer( slotParms.in_slots, "," );

                    while ( tok.hasMoreTokens() ) {
                        slotParms.sheet_ids.add(Integer.parseInt(tok.nextToken()));
                        slotParms.slots++;
                    }

                    slotParms.hit = true;       // use this existing boolean to indicate that we are here to modify an existing time (this will only happen if editing a time that is part of a block of consec times)

                    // if this slot is part of a block of consecutive times
                    // then set the time parameter to the first time (slot_id too??)
                    slotParms.time = getSlotTime(slotParms.sheet_ids.get(0), con);

                } else {
                    slotParms.in_slots = String.valueOf(slot_id);
                }

                // if in use by self then allow
                //if (!slotParms.last_user.equalsIgnoreCase( user )) in_use = 1;

                pstmt = con.prepareStatement (
                   "SELECT * " +
                   "FROM activity_sheets_players " +
                   "WHERE activity_sheet_id = ? " +
                   "ORDER BY pos");

                pstmt.clearParameters();
                pstmt.setInt(1, slot_id);
                rs = pstmt.executeQuery();

                if (rs.next()) {

                    slotParms.player1 = rs.getString( "player_name" );
                    slotParms.user1 = rs.getString( "username" );
                    slotParms.userg1 = rs.getString( "userg" );
                    slotParms.show1 = rs.getShort( "show" );
                    slotParms.players = 1;
                }

                if (rs.next()) {

                    slotParms.player2 = rs.getString( "player_name" );
                    slotParms.user2 = rs.getString( "username" );
                    slotParms.userg2 = rs.getString( "userg" );
                    slotParms.show2 = rs.getShort( "show" );
                    slotParms.players = 2;
                }

                if (rs.next()) {

                    slotParms.player3 = rs.getString( "player_name" );
                    slotParms.user3 = rs.getString( "username" );
                    slotParms.userg3 = rs.getString( "userg" );
                    slotParms.show3 = rs.getShort( "show" );
                    slotParms.players = 3;
                }

                if (rs.next()) {

                    slotParms.player4 = rs.getString( "player_name" );
                    slotParms.user4 = rs.getString( "username" );
                    slotParms.userg4 = rs.getString( "userg" );
                    slotParms.show4 = rs.getShort( "show" );
                    slotParms.players = 4;
                }

                if (rs.next()) {

                    slotParms.player5 = rs.getString( "player_name" );
                    slotParms.user5 = rs.getString( "username" );
                    slotParms.userg5 = rs.getString( "userg" );
                    slotParms.show5 = rs.getShort( "show" );
                    slotParms.players = 5;
                }

            } else {
                //out.println("<!-- Unable to make busy -->");
            }

            pstmt.close();

          }
          catch (SQLException e) {

             throw new Exception("Error checking in-use - verifyActSlot.checkInUse - SQL Exception: " + e.getMessage());
          }
          catch (Exception e) {

             throw new Exception("Error checking in-use - verifyActSlot.checkInUse - Exception: " + e.getMessage());
          }

       } // end if

   return(in_use);

 } // end checkInUse


 /**
  *
  * @param run_mode:
  *      1 = Preferred Only - Check selected court for the exact slots requested.  Return as many sheet_ids as are available at that time.
  *      2 = Nearest Available Search - Search activities in parent_id and search_group for closest set of the selected number of time slots, searching down the time sheet row by row.
  * @throws Exception
  */
 public static int checkInUseM(int slot_id, String user, parmSlot slotParms, int run_mode, Connection con, PrintWriter out)
         throws Exception {
     
    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    PreparedStatement pstmt3 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    int status = 0;     // return variable
    int count = 0;      // number of consecutive times found
    int rest_id = 0;
    int parent_id = 0;
    int interval = 0;
    
    String search_group = "";

    boolean fail = false; // flag for detecting is we lost a time slot
    boolean preferred = true;   // flag for knowing if we're still attempting to get the user's preferred times.

    //  parm block to hold the member restrictions for this date and member
    parmRest parmr = new parmRest();          // allocate a parm block for restrictions

    if (slot_id != 0 && user != null && !user.equals( "" )) {

        // load up the first time choosen and populate the slotParm as usual
        // then using the date/time/activity of this first time, load up the next x# of
        // time slots for this activity (court level) and see if they are emtpy and available

        try {

            pstmt = con.prepareStatement (
               "SELECT *, " +
                    "DATE_FORMAT(date_time, '%Y%m%d') AS date1, " +
                    "DATE_FORMAT(date_time, '%k%i') AS time, " +
                    "DATE_FORMAT(date_time, '%W') AS day_name, " +
                    "DATE_FORMAT(date_time, '%e') AS dd, " +
                    "DATE_FORMAT(date_time, '%c') AS mm, " +
                    "DATE_FORMAT(date_time, '%Y') AS yy, " +
                    "IF(( in_use_by = '' || in_use_by = ? ) || (UNIX_TIMESTAMP(in_use_at) + (6 * 60)) < UNIX_TIMESTAMP(), 0, 1) AS in_use " +
               "FROM activity_sheets " +
               "WHERE " +
                    "sheet_id = ?");

            pstmt.clearParameters();
            pstmt.setString(1, user);
            pstmt.setInt(2, slot_id);
            rs = pstmt.executeQuery();

            if (rs.next()) {

               slotParms.slot_id = slot_id;
               slotParms.activity_id = rs.getInt( "activity_id" );
               slotParms.dd = rs.getInt( "dd" );
               slotParms.mm = rs.getInt( "mm" );
               slotParms.yy = rs.getInt( "yy" );
               slotParms.date = rs.getInt( "date1" );
               slotParms.time = rs.getInt( "time" );
               slotParms.last_user = rs.getString( "in_use_by" );
//               slotParms.in_use = (slotParms.last_user.equals("")) ? 0 : 1; // || slotParms.last_user.equalsIgnoreCase( user )
               slotParms.in_use = rs.getInt("in_use");
               slotParms.hideNotes = rs.getInt( "hideNotes" );
               slotParms.notes = rs.getString( "notes" );
               slotParms.day = rs.getString( "day_name" );
               slotParms.force_singles = rs.getInt("force_singles");

               if (run_mode == 1 && slotParms.in_use == 0) slotParms.sheet_ids.add( slot_id );      // the inital time requested to the array

               parmr.user = user;
               parmr.mship = slotParms.mship;
               parmr.mtype = slotParms.mtype;
               parmr.date = slotParms.date;
               parmr.day = slotParms.day;
               parmr.course = "";
               parmr.activity_id = slotParms.root_activity_id;     // use Root id for now

               try {

                  getRests.getAll(con, parmr);              // get the restrictions

               } catch (Exception exc) {

                  Utilities.logError("Member_gensheets: getRests failed. user=" + user + ", mship=" + slotParms.mship + ", mtype=" + slotParms.mtype + ", date=" +
                           slotParms.date + ", day_name=" + slotParms.day + ", activity_id=" + slotParms.root_activity_id + ", err=" + exc.toString());
               }
           }

        } catch (Exception exc) {

            Utilities.logError("Error1 in verifyActSlot.checkInUseM (run_mode: " + run_mode + "): slot_id=" + slot_id + ", Error=" + exc.getMessage());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

        // Use the run_mode value to determine processing as follows:
        //     1 = Preferred Only - Check selected court for the exact slots requested.  Return as many sheet_ids as are available at that time.
        //     2 = Nearest Available Search - Search activities in parent_id and search_group for closest set of the selected number of time slots, searching down the time sheet row by row.
        switch (run_mode) {
            
            // Preferred Only - Search for the exact times requested
            case 1:
                
                // If couldn't get the initially clicked time, ignore the rest
                if (slotParms.sheet_ids.size() == 0) {

                    status = 1;

                } else {        // gather all times on the same 'court' after the initial time requsted

                    try {
                        
                        pstmt = con.prepareStatement(
                            "SELECT sheet_id, in_use_by, blocker_id, rest_id, event_id, lesson_id, player_name, "
                                + "DATE_FORMAT(date_time, '%Y%m%d') AS date1, "
                                + "DATE_FORMAT(date_time, '%k%i') AS time, "
                                + "DATE_FORMAT(date_time, '%W') AS day_name, "
                                + "DATE_FORMAT(date_time, '%e') AS dd, "
                                + "DATE_FORMAT(date_time, '%c') AS mm, "
                                + "DATE_FORMAT(date_time, '%Y') AS yy "
                            + "FROM activity_sheets t1 "
                            + "LEFT OUTER JOIN activity_sheets_players t2 ON t1.sheet_id = t2.activity_sheet_id "
                            + "WHERE "
                                + "activity_id = ? AND "
                                + "date_time > ? AND date_time <= ? "
                                //+ "date_time BETWEEN ? AND ? "
                                //+ "DATE_FORMAT(date_time, '%Y%m%d') = ? AND "
                                //+ "DATE_FORMAT(date_time, '%k%i') > ? "
                            + "ORDER BY date_time "
                            + "LIMIT 30;");

                        pstmt.clearParameters();
                        pstmt.setInt(1, slotParms.activity_id);
                        pstmt.setString(2, Utilities.get_mysql_timestamp((int)slotParms.date, slotParms.time));
                        pstmt.setString(3, Utilities.get_mysql_timestamp((int)slotParms.date, 2359));
                        //pstmt.setLong(2, slotParms.date);
                        //pstmt.setInt(3, slotParms.time);
                        rs = pstmt.executeQuery();

                        while (rs.next()) {

                            // If rest_id is populated, check to see if any restrictions are blocking this member from entering this time
                            rest_id = rs.getInt("rest_id");

                            // If no restrictions apply to this member, or are suspended, clear the rest_id flag.
                            if (rest_id != 0 && getRestData(parmr, rs.getInt("time"), slotParms.activity_id, rest_id)) {

                                rest_id = 0;
                            }

                            // only proceed if the time is not in use, not blocked, not covered by
                            // an event or lesson and has no players assigned to it
                            if ((rs.getString("in_use_by").equals("") || rs.getString("in_use_by").equals(user)) && rs.getInt("blocker_id") == 0 && rs.getInt("event_id") == 0 &&
                                    rs.getInt("lesson_id") == 0 && rs.getString("player_name") == null && rest_id == 0) {

                                // this time is available

                                // now we could check here to check to see if the booking user
                                // is retricted from playing during this time but what if they are
                                // making this time on the behalf of another member?  Also I don't think we
                                // do this type of check on the golf side - Let's wait and see if this is requested

                                //if (slotParms.time != rs.getInt("time")) { // previous fix for mysql between 
                                    slotParms.sheet_ids.add( rs.getInt("sheet_id") );
                                //}

                                //debug
                                //out.println("<!-- FOUND " + slotParms.sheet_ids.size() + " CONSECUTIVE TIMES (added sheet_id " + rs.getInt("sheet_id") + " to array) -->");

                            } else {

                                // found a time that is not available...
                                // We only want the partial block of times that was available at the original time selected
                                break;

                            }

                            // see if we've found enough times
                            if (slotParms.sheet_ids.size() == slotParms.slots) {

                                break;

                            }
                        
                        } // end while loop

                        // again, see if we've found enough times
                        if (slotParms.sheet_ids.size() == slotParms.slots || slotParms.sheet_ids.size() >= 1) {

                            // we did - so now lets mark each of them as busy
                            for (int i = 0; i < slotParms.sheet_ids.size(); i++) {

                                pstmt = con.prepareStatement (
                                    "UPDATE activity_sheets " +
                                    "SET in_use_by = ?, in_use_at = now() " +
                                    "WHERE sheet_id = ? AND (SELECT count(*) FROM activity_sheets_players WHERE activity_sheet_id = ?) = 0 AND " +
                                        "(( in_use_by = '' || in_use_by = ? ) || " +
                                        " (UNIX_TIMESTAMP(in_use_at) + (6 * 60)) < UNIX_TIMESTAMP() " +
                                        ")");

                                pstmt.clearParameters();
                                pstmt.setString(1, user);
                                pstmt.setInt(2, slotParms.sheet_ids.get(i));
                                pstmt.setInt(3, slotParms.sheet_ids.get(i));
                                pstmt.setString(4, user);

                                count = pstmt.executeUpdate();

                                pstmt.close();

                                if (count == 0) {

                                    // we lost one of the times
                                    //debug
                                    //out.println("<!-- FAILED Locking sheet id " + slotParms.sheet_ids.get(i) + " -->");
                                    fail = true;
                                    break;

                                } else {

                                    //debug
                                    //out.println("<!-- SUCCESS: Locked sheet_id " + slotParms.sheet_ids.get(i) + " -->");

                                }

                            } // end for loop


                            // build an 'in' string and store it in the slotParms
                            String in = "";

                            for (int i = 0; i < slotParms.sheet_ids.size(); i++) {

                                in += slotParms.sheet_ids.get(i) + ",";
                            }

                            in = in.substring(0, in.length() - 1);

                            slotParms.in_slots = in;

                            // if marking them all as busy failed then release them
                            if ( fail ) {

                                //debug
                                //out.println("<!-- FAILED: Releasing these sheet_ids (" + in + ") -->");

                                // we were not able to get all the times so lets release them all
                                pstmt = con.prepareStatement (
                                    "UPDATE activity_sheets " +
                                    "SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' " +
                                    "WHERE sheet_id IN (" + in + ") AND 1 = ?");

                                pstmt.clearParameters();
                                pstmt.setInt(1, 1);

                                pstmt.executeUpdate();

                                slotParms.sheet_ids.clear();
                                slotParms.in_slots = "";

                                status = 1;

                            } else if (slotParms.sheet_ids.size() < slotParms.slots) {

                                status = 2;
                            }

                        } else {

                            // we did NOT find enough times to satisfy the consec request
                            status = 1;

                        }

                    } catch (Exception exc) {

                        Utilities.logError("Error2 in verifyActSlot.checkInUseM (run_mode: " + run_mode + "): initial slot_id=" + slot_id + ", Error=" + exc.getMessage());
                        //out.println("Error2 in verifyActSlot.checkInUseM: initial slot_id=" + slot_id + ", Error=" + exc.toString());

                    } finally {

                        try { rs.close(); }
                        catch (Exception ignore) {}

                        try { pstmt.close(); }
                        catch (Exception ignore) {}

                    }
                }
                
                break;    // end case 1
            
            // Nearst Available Search - Search activities in parent_id and search_group for closest set of the selected number of time slots, searching down the time sheet row by row.
            case 2:
                            
                List<Integer> search_activity_ids = new ArrayList<Integer>();       // Stores all activity_ids to loop over
                Map<Integer, Integer> next_available_time = new HashMap<Integer, Integer>();    // Used to store the last time value for a given activity_id that we got cut off at.
                
                int curr_time = 0;
                int next_available = 0;
                
                // Look up the parent_id and search_group for the activity of the selected time slot, to be used when searching horizontally for another court.
                try {

                    pstmt = con.prepareStatement("SELECT parent_id, search_group, a.interval FROM activities a WHERE activity_id = ?");
                    pstmt.clearParameters();
                    pstmt.setInt(1, slotParms.activity_id);

                    rs = pstmt.executeQuery();

                    if (rs.next()) {
                        parent_id = rs.getInt("parent_id");
                        search_group = rs.getString("search_group");
                        interval = rs.getInt("a.interval");
                    }

                } catch (Exception exc) {

                    Utilities.logError("Error1 in verifyActSlot.checkInUseM (run_mode: " + run_mode + "): Error looking up search_group and parent_id data - ERR: " + exc.toString());

                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt.close(); }
                    catch (Exception ignore) {}
                }
                
                try {

                    // Get list of relevant activity_ids to loop through and search
                    pstmt = con.prepareStatement(""
                            + "SELECT activity_id "
                            + "FROM activities a "
                            + "WHERE parent_id = ? AND search_group = ? AND a.interval = ? AND enabled = 1 "
                            + "ORDER BY activity_id = ? DESC, sort_by ASC, activity_name ASC");
                    pstmt.clearParameters();
                    pstmt.setInt(1, parent_id);
                    pstmt.setString(2, search_group);
                    pstmt.setInt(3, interval);
                    pstmt.setInt(4, slotParms.activity_id);

                    rs = pstmt.executeQuery();
                    
                    while (rs.next()) {
                        search_activity_ids.add(rs.getInt("activity_id"));
                    }
                    
                } catch (Exception exc) {
                    Utilities.logError("verifyActSlot.checkInUseM (run_mode: " + run_mode + "): Error generating list of activity_ids - ERR: " + exc.toString());
                } finally {
                    
                    try { rs.close(); }
                    catch (Exception ignore) {}
                    
                    try { pstmt.close(); }
                    catch (Exception ignore) {}
                }
                
                try {

                    // Pull up a list of time values from the selected parent_id/search_group to use a list of rows of times to check
                    pstmt = con.prepareStatement(
                            "SELECT DATE_FORMAT(date_time, '%k%i') AS time "
                            + "FROM activity_sheets ash "
                            + "LEFT OUTER JOIN activities a ON ash.activity_id = a.activity_id "
                          //+ "WHERE parent_id = ? AND search_group = ? AND a.interval = ? AND DATE_FORMAT(date_time, '%Y%m%d') = ? AND DATE_FORMAT(date_time, '%k%i') >= ? "
                            + "WHERE parent_id = ? AND date_time BETWEEN ? AND ? AND search_group = ? AND a.interval = ? "
                            + "GROUP BY time "
                            + "ORDER BY date_time LIMIT 30");
                    pstmt.clearParameters();
                    pstmt.setInt(1, parent_id);
                    pstmt.setString(2, Utilities.get_mysql_timestamp((int)slotParms.date, slotParms.time));
                    pstmt.setString(3, Utilities.get_mysql_timestamp((int)slotParms.date, 2359));
                    pstmt.setString(4, search_group);
                    pstmt.setInt(5, interval);
                    //pstmt.setLong(4, slotParms.date);
                    //pstmt.setInt(5, slotParms.time);
                    
                    rs = pstmt.executeQuery();
                    
                    time_loop:
                    while (rs.next()) {
                        
                        curr_time = rs.getInt("time");
                        
                        // For Lakewood Ranch, if it's before 1:00pm, 8:30, 10:00, and 11:30 are the only start-times for reservations. Skip all others when searching for next available time.
                        if (slotParms.club.equals("lakewoodranch") && parent_id == 1 && curr_time < 1300 && curr_time != 830 && curr_time != 1000 && curr_time != 1130) {
                            continue time_loop;
//                        } else if (slotParms.club.equals("gallerygolf")) {
//
//                            int tempTime = rs.getInt("intTime");
//
//                            if (slotParms.day.equalsIgnoreCase("Saturday") || slotParms.day.equalsIgnoreCase("Sunday")) {
//
//                                if (curr_time != 700 && curr_time != 830 && curr_time != 1000 && curr_time != 1130
//                                        && curr_time != 1300 && curr_time != 1430 && curr_time != 1600 && curr_time != 1730) {
//                                    continue;
//                                }
//
//                            } else {
//
//                                if (curr_time != 600 && curr_time != 730 && curr_time != 900 && curr_time != 1030 && curr_time != 1200
//                                        && curr_time != 1330 && curr_time != 1500 && curr_time != 1630 && curr_time != 1800) {
//                                    continue;
//                                }
//                            }
                        }
                    
                        activity_id_loop:
                        for (int curr_activity_id : search_activity_ids) {

                            status = 0;
                            
                            // Don't bother checking this activity_id for this time if we checked it earlier and determined its next available time to be later than this.
                            // Also skip if we're checking the originally selected time value, and we're on the activity_id that they originally selected.
                            if ((!rs.isFirst() && next_available_time.containsKey(curr_activity_id) && curr_time < next_available_time.get(curr_activity_id)) 
                                    || (rs.isFirst() && curr_activity_id == slotParms.activity_id)) {
                                
                                status = 1;
                                continue activity_id_loop;
                            }
                            
                            try {

                                // Look up the details for this activity_id
                                pstmt2 = con.prepareStatement(
                                    "SELECT activity_id, sheet_id, in_use_by, blocker_id, rest_id, event_id, lesson_id, player_name, "
                                        + "DATE_FORMAT(date_time, '%Y%m%d') AS date1, "
                                        + "DATE_FORMAT(date_time, '%k%i') AS time, "
                                        + "DATE_FORMAT(date_time, '%W') AS day_name, "
                                        + "DATE_FORMAT(date_time, '%e') AS dd, "
                                        + "DATE_FORMAT(date_time, '%c') AS mm, "
                                        + "DATE_FORMAT(date_time, '%Y') AS yy "
                                    + "FROM activity_sheets t1 "
                                    + "LEFT OUTER JOIN activity_sheets_players t2 ON t1.sheet_id = t2.activity_sheet_id "
                                    + "WHERE "
                                        + "activity_id = ? AND "
                                        + "date_time BETWEEN ? AND ? "
                                    + "ORDER BY date_time "
                                    + "LIMIT 30;");

                                pstmt2.clearParameters();
                                pstmt2.setInt(1, curr_activity_id);
                                pstmt2.setString(2, timeUtil.getDbDate((int)slotParms.date) + " " + timeUtil.get24HourTime(curr_time));
                                pstmt2.setString(3, timeUtil.getDbDate((int)slotParms.date) + " 23:59:00");
                                //pstmt2.setInt(3, curr_time);
                                rs2 = pstmt2.executeQuery();

                                time_slots_loop:
                                while (rs2.next()) {

                                    if (rs2.isFirst() && rs2.getInt("time") != curr_time) {
                                  
                                        // The first time we pulled back wasn't the same as the one we're currently checking. Skip this activity.
                                        status = 1;
                                        slotParms.sheet_ids.clear();
                                        continue activity_id_loop;
                                    } 

                                    // If rest_id is populated, check to see if any restrictions are blocking this member from entering this time
                                    rest_id = rs2.getInt("rest_id");

                                    // If no restrictions apply to this member, or are suspended, clear the rest_id flag.
                                    if (rest_id != 0 && getRestData(parmr, rs2.getInt("time"), curr_activity_id, rest_id)) {

                                        rest_id = 0;
                                    }

                                    // only proceed if the time is not in use, not blocked, not covered by
                                    // an event or lesson and has no players assigned to it
                                    if ((rs2.getString("in_use_by").equals("") || rs2.getString("in_use_by").equals(user)) && rs2.getInt("blocker_id") == 0 && rs2.getInt("event_id") == 0 &&
                                            rs2.getInt("lesson_id") == 0 && rs2.getString("player_name") == null && rest_id == 0) {

                                        // this time is available
                                        slotParms.sheet_ids.add( rs2.getInt("sheet_id") );
                                        next_available_time.put(curr_activity_id, rs2.getInt("time"));

                                    } else {

                                        // found a time that is not available...
                                        // Clear out any potential time slots we'd found and move on to the next activity_id
                                        slotParms.sheet_ids.clear();
                                        
                                        next_available = rs2.getInt("time");
                                        
                                        // Loop through and find the next available time slot on this activity_id, so we don't waste time hitting the DB on already occupied times.
                                        next_available_loop:
                                        while (rs2.next()) {
                                            
                                            next_available = rs2.getInt("time");
                                            
                                            // Once we hit the first available time, break out of the loop since we want to save this as the next time to check on this activity_id
                                            if ((rs2.getString("in_use_by").equals("") || rs2.getString("in_use_by").equals(user)) && rs2.getInt("blocker_id") == 0 && rs2.getInt("event_id") == 0 &&
                                            rs2.getInt("lesson_id") == 0 && rs2.getString("player_name") == null && rs2.getInt("rest_id") == 0) { 
                                                
                                                break next_available_loop;
                                            }
                                        }
                                        
                                        next_available_time.put(curr_activity_id, next_available);

                                        status = 1;
                                        slotParms.sheet_ids.clear();
                                        continue activity_id_loop;                                    
                                    }
                                    
                                    // see if we've found enough times
                                    if (slotParms.sheet_ids.size() == slotParms.slots) {

                                        slotParms.activity_id = curr_activity_id;
                                        break time_slots_loop;
                                    }

                                } // end while loop


                                // again, see if we've found enough times
                                if (slotParms.sheet_ids.size() == slotParms.slots) {

                                    // we did - so now lets mark each of them as busy
                                    set_in_use_loop:
                                    for (int i = 0; i < slotParms.sheet_ids.size(); i++) {

                                        try {
                                            pstmt3 = con.prepareStatement(
                                                    "UPDATE activity_sheets "
                                                    + "SET in_use_by = ?, in_use_at = now() "
                                                    + "WHERE sheet_id = ? AND (SELECT count(*) FROM activity_sheets_players WHERE activity_sheet_id = ?) = 0 AND "
                                                    + "(( in_use_by = '' || in_use_by = ? ) || "
                                                    + " (UNIX_TIMESTAMP(in_use_at) + (6 * 60)) < UNIX_TIMESTAMP() "
                                                    + ")");

                                            pstmt3.clearParameters();
                                            pstmt3.setString(1, user);
                                            pstmt3.setInt(2, slotParms.sheet_ids.get(i));
                                            pstmt3.setInt(3, slotParms.sheet_ids.get(i));
                                            pstmt3.setString(4, user);

                                            count = pstmt3.executeUpdate();

                                        } catch (Exception exc) {
                                            Utilities.logError("verifyActSlot.checkInUseM - Error setting slots in use - ERR: " + exc.toString());
                                        } finally {

                                            try { pstmt3.close(); }
                                            catch (Exception ignore) {}
                                        }

                                        if (count == 0) {
                                            
                                            // we lost one of the times
                                            //debug
                                            //out.println("<!-- FAILED Locking sheet id " + slotParms.sheet_ids.get(i) + " -->");
                                            fail = true;
                                            break set_in_use_loop;

                                        } else {

                                            //debug
                                            //out.println("<!-- SUCCESS: Locked sheet_id " + slotParms.sheet_ids.get(i) + " -->");

                                        }

                                    } // end for loop


                                    // build an 'in' string and store it in the slotParms
                                    String in = "";

                                    for (int i = 0; i < slotParms.sheet_ids.size(); i++) {

                                        in += slotParms.sheet_ids.get(i) + ",";
                                    }

                                    in = in.substring(0, in.length() - 1);

                                    slotParms.in_slots = in;

                                    // if marking them all as busy failed then release them
                                    if ( fail ) {

                                        //debug
                                        //out.println("<!-- FAILED: Releasing these sheet_ids (" + in + ") -->");

                                        try {
                                            // we were not able to get all the times so lets release them all
                                            pstmt3 = con.prepareStatement (
                                                "UPDATE activity_sheets " +
                                                "SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' " +
                                                "WHERE sheet_id IN (" + in + ")");

                                            pstmt3.clearParameters();

                                            pstmt3.executeUpdate();
                                            
                                        } catch (Exception exc) {
                                            Utilities.logError("verifyActSlot.checkInUseM - Error freeing up slots after failing to acquire them. - ERR: " + exc.toString());
                                        } finally {

                                            try { pstmt3.close(); }
                                            catch (Exception ignore) {}
                                        }

                                        status = 1;
                                        slotParms.sheet_ids.clear();
                                        slotParms.in_slots = "";     
                                        continue activity_id_loop;

                                    } else {

                                        status = 0;
                                        break time_loop;
                                    }

                                } else {

                                    // we did NOT find enough times to satisfy the consec request
                                    status = 1;
                                    slotParms.sheet_ids.clear();
                                    slotParms.in_slots = "";
                                    continue activity_id_loop;

                                }

                            } catch (Exception exc) {

                                Utilities.logError("Error2 in verifyActSlot.checkInUseM (run_mode: " + run_mode + "): initial slot_id=" + slot_id + ", Error=" + exc.getMessage());

                            } finally {

                                try { rs2.close(); }
                                catch (Exception ignore) {}

                                try { pstmt2.close(); }
                                catch (Exception ignore) {}

                            }
                        }
                    }

                } catch (Exception exc) {
                    Utilities.logError("verifyActSlot.checkInUseM (run_mode: " + run_mode + "): Error looking up list of times to act as rows to check - ERR: " + exc.toString());

                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt.close(); }
                    catch (Exception ignore) {}
                }

                break;    // end case 3
              
        }    // end switch conditional
    } // end if required parameters are here


    // determine our return status
    // 0 = sucess
    // 1 = failure to find enough times or get locks on times we did find
    // 2 = found partial slots available at original time
    // 9 = found slots but at different time

    if ( fail ) {

        // we found times but were not able to lock them all - should rarely happen
        status = 1;

    } else if ( status == 1) {

        // if already set then we were not able to find enough times to satisfy the request
        // do nothing - this is mainly to avoid an error trying to get(0) from empty array in next else if

    }/* else if ( run_mode == 1 && slot_id != slotParms.sheet_ids.get(0) ) {

        // we found enough slots to fullfill their consec request but the
        // slots found are not at the exact time requested - user must approve these times
        status = 9;

    }*/

    return (status);

 }


 //
 // Check any members in this reservation to make sure they are not exceed
 // the allowable play for this activity based upon their membership type
 //
 public static boolean checkMaxRounds(parmSlot slotParms, Connection con)
         throws Exception {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    //
    //  parm block to hold the club parameters
    //
    parmClub parm = new parmClub(slotParms.root_activity_id, con);

    boolean error = false;                  // default

    int year = 0;
    int month = 0;
    int dayNum = 0;
    int count = 0;
    int mtimes = 0;
    int ind = 0;
    int i = 0;

    long dateStart = 0;
    long dateEnd = 0;

    String mperiod = "";

    String [] mshipA = new String [parm.MAX_Mships+1];     // array to hold the membership names
    int [] mtimesA = new int [parm.MAX_Mships+1];          // array to hold the mship max # of rounds value
    String [] periodA = new String [parm.MAX_Mships+1];    // array to hold the mship periods (week, month, year)

    String [] user = new String [5];        // usernames
    String [] mship = new String [5];       // mships
    String [] player = new String [5];      // player name

    user[0] = slotParms.user1;
    user[1] = slotParms.user2;
    user[2] = slotParms.user3;
    user[3] = slotParms.user4;
    user[4] = slotParms.user5;

    mship[0] = slotParms.mship1;
    mship[1] = slotParms.mship2;
    mship[2] = slotParms.mship3;
    mship[3] = slotParms.mship4;
    mship[4] = slotParms.mship5;

    player[0] = slotParms.player1;
    player[1] = slotParms.player2;
    player[2] = slotParms.player3;
    player[3] = slotParms.player4;
    player[4] = slotParms.player5;

    String in = "";

    try { in = getActivity.buildInString(slotParms.root_activity_id, 1, con); }
    catch (Exception exc) { verifySlot.logError("Error in verifyActSlot.checkMaxRounds getting root activities. ERR=" + exc.toString()); }


    //
    //  init the string arrays
    //
    for (i=0; i<parm.MAX_Mships+1; i++) {
        mshipA[i] = "";
        periodA[i] = "";
        mtimesA[i] = 0;
    }

    //
    // statement to check activities play history
     //
     String sql = ""
             + "SELECT COUNT(t2.activity_sheet_id) "
             + "  FROM activity_sheets t1 "
             + "    INNER JOIN activity_sheets_players t2 "
             + "      ON t1.sheet_id = t2.activity_sheet_id "
             + "  WHERE "
             + "    `show` = 1 "
             + "    AND t2.username = ? "
             + "    AND activity_id IN (" + in + ") "
             + "    AND date_time NOT BETWEEN ? AND ? "; // exclude today
             //+ "    AND DATE_FORMAT(date_time, '%Y%m%d') != ? "; // exclude today

     String where_week = ""
             //+ "    AND DATE_FORMAT(date_time, '%Y%m%d') >= ? "
             //+ "    AND DATE_FORMAT(date_time, '%Y%m%d') <= ?";
             + "    AND date_time BETWEEN ? AND ? ";

     String where_month = ""
             //+ "    AND DATE_FORMAT(date_time, '%Y') = ? "
             //+ "    AND DATE_FORMAT(date_time, '%m') = ?";
             + "    AND date_time BETWEEN ? AND ? ";

     String where_year = ""
             //+ "    AND DATE_FORMAT(date_time, '%Y') = ?";
             + "    AND date_time BETWEEN ? AND ? ";


    try {

        //
        //  Get this date's calendar and then determine start and end of week.
        //
        int calmm = slotParms.mm -1;                    // adjust month value for cal

        Calendar cal = new GregorianCalendar();         // get todays date

        //
        //  set cal to tee time's date
        //
        cal.set(Calendar.YEAR,slotParms.yy);                    // set year in cal
        cal.set(Calendar.MONTH,calmm);                          // set month in cal
        cal.set(Calendar.DAY_OF_MONTH,slotParms.dd);            // set day in cal

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

        month = month + 1;                                // month starts at zero

        dateEnd = year * 10000;                           // create a date field of yyyymmdd
        dateEnd = dateEnd + (month * 100);
        dateEnd = dateEnd + dayNum;                       // date = yyyymmdd (for comparisons)

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
        //  Get membership types, number of rounds and time periods (week, month, year)
        //
        //stmt = con.createStatement();

        pstmt = con.prepareStatement("SELECT mship, mtimes, period FROM mship5 WHERE activity_id = ? LIMIT 24");
        pstmt.clearParameters();
        pstmt.setInt(1, slotParms.root_activity_id);
        rs = pstmt.executeQuery();

        i = 0; // force reset

        while ( rs.next() ) {

            mshipA[i] = rs.getString("mship");
            mtimesA[i] = rs.getInt("mtimes");
            periodA[i] = rs.getString("period");

            i++;
        }

        loop1:
        for (i = 0; i < 5; i++) {

        if (!mship[i].equals( "" )) {          // check if mship specified

            ind = 0;             // init fields
            count = 0;
            mtimes = 0;
            mperiod = "";

            loop2:
            while (ind < parm.MAX_Mships + 1) {

               if (mship[i].equals( mshipA[ind] )) {

                  mtimes = mtimesA[ind];            // match found - get number of rounds
                  mperiod = periodA[ind];           //               and period (week, month, year)
                  break loop2;
               }
               ind++;
            }

            if (mtimes != 0) {             // if match found for this player and there is a limit

               count = 0;
               int p = 1;

               if (mperiod.equals( "Week" )) {       // if WEEK

                  pstmt = con.prepareStatement (sql + where_week);
                  pstmt.clearParameters();
                  
                  pstmt.setString(p++, user[i]);     // first player in this slot
                  pstmt.setString(p++, Utilities.get_mysql_timestamp((int)slotParms.date, 0));     // start of today
                  pstmt.setString(p++, Utilities.get_mysql_timestamp((int)slotParms.date, 2359));     // end of today
                  pstmt.setString(p++, Utilities.get_mysql_timestamp((int)dateStart, 0));     // start the week
                  pstmt.setString(p++, Utilities.get_mysql_timestamp((int)dateEnd, 2359));     // end of the week
                  //pstmt.setLong(2, slotParms.date);        // today
                  //pstmt.setLong(3, dateStart);             // start of the week
                  //pstmt.setLong(4, dateEnd);               // end of the week

               } else if (mperiod.equals( "Month" )) {      // if MONTH
                   
                  int lastDayOfMonth = new LocalDate(slotParms.yy, slotParms.mm, 1).dayOfMonth().getMaximumValue();

                  pstmt = con.prepareStatement (sql + where_month);
                  pstmt.clearParameters();
                  pstmt.setString(p++, user[i]);     // first player in this slot
                  pstmt.setString(p++, Utilities.get_mysql_timestamp((int)slotParms.date, 0));     // start of today
                  pstmt.setString(p++, Utilities.get_mysql_timestamp((int)slotParms.date, 2359));     // end of today
                  pstmt.setString(p++, Utilities.get_mysql_timestamp(timeUtil.buildIntDate(slotParms.yy, slotParms.mm, 1), 0));     // start of month
                  pstmt.setString(p++, Utilities.get_mysql_timestamp(timeUtil.buildIntDate(slotParms.yy, slotParms.mm, lastDayOfMonth), 2359));     // end of month
                  //pstmt.setLong(2, slotParms.date);        // today
                  //pstmt.setInt(3, slotParms.mm);           // month for this reservation
                  //pstmt.setInt(4, slotParms.yy);           // year for this reservation

               } else if (mperiod.equals( "Year" )) {            // if Year
                   
                   int lastDayOfYear = new LocalDate(slotParms.yy, 12, 1).dayOfMonth().getMaximumValue();

                  pstmt = con.prepareStatement (sql + where_year);
                  pstmt.clearParameters();
                  pstmt.setString(p++, user[i]);     // first player in this slot
                  pstmt.setString(p++, Utilities.get_mysql_timestamp((int)slotParms.date, 0));     // start of today
                  pstmt.setString(p++, Utilities.get_mysql_timestamp((int)slotParms.date, 2359));     // end of today
                  pstmt.setString(p++, Utilities.get_mysql_timestamp(timeUtil.buildIntDate(slotParms.yy, 1, 1), 0));     // start of year
                  pstmt.setString(p++, Utilities.get_mysql_timestamp(timeUtil.buildIntDate(slotParms.yy, 12, lastDayOfYear), 2359));     // end of year
                  //pstmt.setLong(2, slotParms.date);        // today
                  //pstmt.setInt(3, slotParms.yy);           // year for this reservation

               } // end of IF mperiod = Year

               rs = pstmt.executeQuery();

               if ( rs.next() ) count = rs.getInt(1);

               //
               //  Compare # of tee times in this period with max allowed for membership type
               //
               if (count >= mtimes)  {

                   if (!player[i].equals( slotParms.oldPlayer1 ) &&
                       !player[i].equals( slotParms.oldPlayer2 ) &&
                       !player[i].equals( slotParms.oldPlayer3 ) &&
                       !player[i].equals( slotParms.oldPlayer4 ) &&
                       !player[i].equals( slotParms.oldPlayer5 )) {   // if name not already accepted by pro

                        // reject this member
                        error = true;
                        slotParms.mship = mship[i];
                        slotParms.player = player[i];
                        slotParms.period = mperiod;

                        break loop1; // stop the checking

                   } // end if already accepted by pro

               } // end if over quota

            } // end of if limit for this mship

         } // end of player is a member

        } // end player loop


    }
    catch (SQLException e1) {

        throw new Exception("SQL Error Checking Max Rounds - verifyActSlot.checkMaxRounds " + e1.getMessage());
    }

    catch (Exception e) {

        throw new Exception("Exception Checking Max Rounds - verifyActSlot.checkMaxRounds " + e.getMessage());
    }

    finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return(error);

 }
 
 public static boolean checkMaxOrigBy(String user, long date, int max_originations, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     int count = 0;
     
     boolean result = false;
     
     try {
         
         //PreparedStatement pstmt = con.prepareStatement("SELECT COUNT(*) FROM teecurr2 WHERE orig_by = ? AND date = ? AND related_id = '';");
         pstmt = con.prepareStatement(""
                 + "SELECT COUNT(*) AS 'orig_count' FROM "
                    + "(SELECT * FROM activity_sheets "
                  //+ "WHERE DATE_FORMAT(date_time, '%Y%m%d') = ? AND orig_by = ? "
                    + "WHERE date_time BETWEEN ? AND ? AND orig_by = ? "
                    + "GROUP BY related_ids) "
                 + "AS orig_query");
         
                //                + "date_time BETWEEN ? AND ? "
         pstmt.clearParameters();
         //pstmt.setLong(1, date);
         pstmt.setString(1, Utilities.get_mysql_timestamp((int)date, 0));
         pstmt.setString(2, Utilities.get_mysql_timestamp((int)date, 2359));
         pstmt.setString(3, user);
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {
             count = rs.getInt("orig_count");
         }
         
         if (count >= max_originations) {
             result = true;
         }
         
     } catch (Exception exc) {
         
         Utilities.logError("verifyActSlot.checkMaxOrigBy - Error looking up max originations for user - ERR: " + exc.toString());
         result = false;
         
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) {}
         
         try { pstmt.close(); }
         catch (Exception ignore) {}
     }
     
     return (result);
 }


 //
 // For now this method is only checking activites (not tee times or events, we can call verifySlot's version
 // if we want that checking done as well
 //
 public static void checkSched(parmSlot slotParms, Connection con)
         throws Exception {

    parmActivity parmAct = new parmActivity();              // allocate a parm block

    parmAct.activity_id = slotParms.activity_id;            // pass in the slot id so we can determin which activity to load parms for

    try {

        getActivity.getParms(con, parmAct);                 // get the activity config

    } catch (Exception e) {

        throw new Exception("verifyActSlot.checkSched: (getActivity.getParms) ERR=" + e.getMessage());

    }


    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    String in = "";
    String club = Utilities.getClubDbName(con);

    try { in = getActivity.buildInString(slotParms.root_activity_id, 1, con); }
    catch (Exception exc) { verifySlot.logError("Error in verifyActSlot.checkSched getting root activities. ERR=" + exc.toString()); }

    String [] user = new String [parmAct.max_players];        // usernames - parmAct.max_players
    String [] player = new String [parmAct.max_players];      // player name
    String [] oldPlayer = new String [parmAct.max_players];   // old player name
    String [] mship = new String [parmAct.max_players];       // membership type

    // init to get rid of null values
    for (int i = 0; i < parmAct.max_players; i++) {
        user[i] = "";
        player[i] = ""; 
        oldPlayer[i] = "";
        mship[i] = "";
    }


    user[0] = slotParms.user1;
    player[0] = slotParms.player1;
    oldPlayer[0] = slotParms.oldPlayer1;
    mship[0] = slotParms.mship1;
    
    
    if (parmAct.max_players >= 2) {
        user[1] = slotParms.user2;
        player[1] = slotParms.player2;
        oldPlayer[1] = slotParms.oldPlayer2;
        mship[1] = slotParms.mship2;
    }
    
    if (parmAct.max_players >= 3) {
        user[2] = slotParms.user3;
        player[2] = slotParms.player3;
        oldPlayer[2] = slotParms.oldPlayer3;
        mship[2] = slotParms.mship3;
    }
    if (parmAct.max_players == 4) {
        user[3] = slotParms.user4;
        player[3] = slotParms.player4;
        oldPlayer[3] = slotParms.oldPlayer4;
        mship[3] = slotParms.mship4;
    }


    // mysql date/time format containing the desired time slot date & time
    //int hr = slotParms.time / 100;                    // 00 - 23
    //int min = slotParms.time - (hr * 100);            // 00 - 59

    //String tmp = hr + ":" + min + ":" + "00";

    //
    // statement to check activities play history for today are return the minutes
    // between the desired time and any existing times
    //
    /*
    String sql = "" +
            "SELECT activity_id, date_time, related_ids, DATE_FORMAT(date_time, '%k%i') AS time2, " +
            "ABS(TIME_TO_SEC(TIMEDIFF(DATE_FORMAT(date_time, '%k:%i:00'), '" + tmp + "')) / 60) AS minsbtwn " +
            "FROM activity_sheets t1 " +
            "LEFT OUTER JOIN activity_sheets_players t2 ON t1.sheet_id = t2.activity_sheet_id " +
            "WHERE " +
                "activity_id IN (" + in + ") AND " +
                "t2.username = ? AND " +
                "DATE_FORMAT(t1.date_time, '%Y%m%d') = DATE_FORMAT(?, '%Y%m%d') " +
            "ORDER BY related_ids";
*/

    int count = 0;
    int minsbtwn = 0;
    int lesson_id = 0;
    int curr_lesson_id = 0;
    
    String temptime = "";
    String related_ids = "";
    String usernameQuery = "";

    boolean first = true;
    boolean found = false;

    int i = -1;

    try {

        for (i = 0; i < parmAct.max_players; i++) {

            // only check for members that are new to this reservation
            if (!user[i].equals("") && !player[i].equals(oldPlayer[i])) {
                
                found = false;
                
                // check if player was previously in a different player slot
                for (int j = 0; j < parmAct.max_players; j++) {
                    if (player[i].equals(oldPlayer[j])) {
                        found = true;
                    }
                }
                
                if (!found) {
                    
                    for (int j = 0; j < slotParms.sheet_ids.size(); j++) {

                        count = 0;      // reset
                        minsbtwn = 0;
                        lesson_id = 0;
                        curr_lesson_id = 0;
                        temptime = "";
                        related_ids = "";
                        usernameQuery = "t2.username = ? AND ";
                        first = true;
                        
                        if (slotParms.club.equals("ballantyne")) {
                            usernameQuery = "(t2.username = ? OR lesson_id != 0) AND ";
                        } else if (slotParms.club.equals("philcricket") && slotParms.root_activity_id == 3) {
                            
                            String userList = "";
                            
                            usernameQuery = "(t2.username = ?";
                            
                            try {
                                
                                pstmt = con.prepareStatement("SELECT username FROM member2b WHERE memNum = (SELECT memNum FROM member2b WHERE username = ?) AND username != ?");
                                pstmt.clearParameters();
                                pstmt.setString(1, user[i]);
                                pstmt.setString(2, user[i]);
                                
                                rs = pstmt.executeQuery();
                                
                                while (rs.next()) {
                                    
                                    if (!userList.equals("")) userList += ",";
                                    
                                    userList += "'" + rs.getString("username") + "'";
                                }
                                
                            } catch (Exception exc) {
                                Utilities.logError("verifyActSlot.checkSched (custom) - " + slotParms.club + " - Error looking up family members - ERR: " + exc.toString());
                            } finally {
                                
                                try { rs.close(); }
                                catch (Exception ignore) {}
                                
                                try { pstmt.close(); }
                                catch (Exception ignore) {}
                            }
                            
                            if (!userList.equals("")) {
                                usernameQuery += " OR t2.username IN (" + userList + ")";
                            }
                            
                            usernameQuery += ") AND ";
                        }

                        pstmt = con.prepareStatement (
                                "SELECT DATE_FORMAT(date_time, '%k:%i:00') AS time " +
                                "FROM activity_sheets " +
                                "WHERE sheet_id = ?");

                        pstmt.clearParameters();
                        pstmt.setInt(1, slotParms.sheet_ids.get(j));

                        rs = pstmt.executeQuery();

                        if (rs.next()) {
                            temptime = rs.getString("time");
                        }

                        pstmt.close();

                        pstmt = con.prepareStatement(""
                                + "SELECT activity_id, date_time, related_ids, lesson_id, DATE_FORMAT(date_time, '%k%i') AS time2, "
                                + "    ABS(TIME_TO_SEC(TIMEDIFF(DATE_FORMAT(date_time, '%k:%i:00'), '" + temptime + "')) / 60) AS minsbtwn "
                                + "  FROM activity_sheets t1 "
                                + "    LEFT JOIN activity_sheets_players t2"
                                + "     ON t1.sheet_id = t2.activity_sheet_id "
                                + "  WHERE "
                                + "    activity_id IN (" + in + ") AND "
                                + usernameQuery
                                + "    date_time BETWEEN ? AND ? "
                                //+ "DATE_FORMAT(t1.date_time, '%Y%m%d') = DATE_FORMAT(?, '%Y%m%d') "
                                + "    ORDER BY related_ids");

                        pstmt.clearParameters();
                        pstmt.setString(1, user[i]);
                        pstmt.setString(2, Utilities.get_mysql_timestamp((int)slotParms.date, 1));
                        pstmt.setString(3, Utilities.get_mysql_timestamp((int)slotParms.date, 2359));
                        //pstmt.setLong(2, slotParms.date);

                        rs = pstmt.executeQuery();

                        while ( rs.next() ) {

                            // If Ballantyne and a lesson_id was present, check to see if it's the current player we're checking, and treat it as a normal reservation if so.
                            if (slotParms.club.equals("ballantyne") && rs.getInt("lesson_id") != 0) {

                                lesson_id = rs.getInt("lesson_id");

                                if (lesson_id > 0) {        // Individual Lesson - ID is stored as a positive value in activity slots

                                    try {

                                        pstmt2 = con.prepareStatement("SELECT memid FROM lessonbook5 WHERE recid = ? AND memid = ?");
                                        pstmt2.clearParameters();
                                        pstmt2.setInt(1, lesson_id);
                                        pstmt2.setString(2, user[i]);

                                        rs2 = pstmt2.executeQuery();

                                        if (!rs2.next()) {
                                            continue;
                                        }

                                    } catch (Exception exc) {
                                        Utilities.logError("verifyActSlot.checkSched - " + slotParms.club + " - Error looking up individual lesson details - ERR: " + exc.toString());
                                    } finally {

                                        try { rs2.close(); }
                                        catch (Exception ignore) { }

                                        try { pstmt2.close(); }
                                        catch (Exception ignore) { }
                                    }

                                } else {        // Group Lesson/Clinic - ID is stored as a negative value in activity slots

                                    try {

                                        pstmt2 = con.prepareStatement("SELECT memid FROM lgrpsignup5 WHERE lesson_id = ? AND date = ? AND memid = ?");
                                        pstmt2.clearParameters();
                                        pstmt2.setInt(1, Math.abs(lesson_id));
                                        pstmt2.setLong(2, slotParms.date);
                                        pstmt2.setString(3, user[i]);

                                        rs2 = pstmt2.executeQuery();

                                        // If we didn't find a signup by the member we're looking for, skip this slot
                                        if (!rs2.next()) {
                                            continue;
                                        }

                                    } catch (Exception exc) {
                                        Utilities.logError("verifyActSlot.checkSched - " + slotParms.club + " - Error looking up group lesson details - ERR: " + exc.toString());
                                    } finally {

                                        try { rs2.close(); }
                                        catch (Exception ignore) { }

                                        try { pstmt2.close(); }
                                        catch (Exception ignore) { }
                                    }
                                }

                                // Make sure we only count each lesson_id once.  It can be assumed if we reach this point that a relevant round was found.
                                if (curr_lesson_id != lesson_id) {
                                    count++;
                                    curr_lesson_id = lesson_id;
                                }

                            } else if (!related_ids.equals(rs.getString("related_ids")) || rs.getString("related_ids").equals("")) {     // Only increment count if this set of related_ids has not been counted already
                                count++;
                                related_ids = rs.getString("related_ids");
                            }

                            if (rs.getInt("minsbtwn") < minsbtwn || first) {

                                first = false;
                                slotParms.time2 = rs.getInt("time2");   // last time in rs should be the closest to existing times
                                minsbtwn = rs.getInt("minsbtwn");
                            }
                        }

                        if (count != 0) {

                            if (parmAct.rndsperday != 0 && count >= parmAct.rndsperday && (((!club.equals("islandcc") || !mship[i].equals("Ball Machine")) && (!club.equalsIgnoreCase("sawgrass") && !user[i].equals("12121212"))))) {        // if we are over the allowed times
                                slotParms.hit = true;                   // indicate we found them on this days time sheet
                                slotParms.player = player[i];           // store their name for reporting back to user

                                break;

                            } else if (minsbtwn == 0) {        // if player is booked in another time that overlaps this

                                slotParms.hit2 = true;
                                slotParms.player = player[i];

                                break;

                            } else if (parmAct.minutesbtwn != 0 && minsbtwn < parmAct.minutesbtwn && ((!club.equals("islandcc") || !mship[i].equals("Ball Machine")) && (!club.equalsIgnoreCase("sawgrass") && !user[i].equals("12121212")))) { // if an existing time is too close
                                slotParms.hit3 = true;                  // indicate we found them on this days time sheet
                                slotParms.player = player[i];           // store their name for reporting back to user

                                //break;

                            }

                        } // end if skip rndsperday & minutesbtwn checks

                    } // end loop over sheet_ids
                    
                } // end check if player was found in the oldPlayer of a different slot

            } // end if we are going to check this member

        } // end for next player loop

        // debug
        //verifySlot.logError("verifyActSlot.checkSched: " + parmAct.rndsperday + ", " + parmAct.minutesbtwn + ", count=" + count + ", minsbtwn=" + minsbtwn + ", time2=" + slotParms.time2 + ", slotParms.date=" + slotParms.date); // , sql=" + sql);

    } catch (Exception exc) {

        verifySlot.logError("verifyActSlot.checkSched: (main) ERR=" + exc.toString() + ", i=" + i + ", max_players=" + parmAct.max_players + ", club=" + slotParms.club + ", activity_id=" + slotParms.activity_id + ", strace=" + Utilities.getStackTraceAsString(exc));

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }


/*

    SELECT activity_id, date_time,
        DATE_FORMAT(date_time, '%k%i') AS time2,
        TIMEDIFF('10:00:00', DATE_FORMAT(date_time, '%k:%i:00')) AS time_diff1,
        ABS(TIME_TO_SEC(TIMEDIFF(DATE_FORMAT(date_time, '%k:%i:00'), '10:00:00')) / 60) AS time_diff2
    FROM activity_sheets t1
    LEFT OUTER JOIN activity_sheets_players t2 ON t1.sheet_id = t2.activity_sheet_id
    WHERE activity_id IN (09,17,18,19,20,10,21,22,23,24,11,25,26,27,1) AND
        t2.username = '6700' AND
        DATE_FORMAT(date_time, '%Y%m%d') = DATE_FORMAT(20090828, '%Y%m%d')

*/

 }


 public static int getSlotTime(int slot_id, Connection con) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int time = 0;

    try {

        pstmt = con.prepareStatement ( "" +
                "SELECT DATE_FORMAT(date_time, '%k%i') AS time2 " +
                "FROM activity_sheets " +
                "WHERE sheet_id = ?" );

        pstmt.clearParameters();
        pstmt.setInt(1, slot_id);

        rs = pstmt.executeQuery();

        if ( rs.next() ) time = rs.getInt("time2");

    } catch (Exception exc) {

        Utilities.logError("verifyActSlot.getSlotTime: ERR=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return (time);

 }


 /**
  * checkSlotHasPlayers - Checks a given activity sheet_id for any associated players and returns the result
  *
  * @param sheet_id sheet_id for the slot in question
  * @param con Connection to club database
  *
  * @return hasPlayers - True if at least one player was found, false if no players were found
  */
 public static boolean checkSlotHasPlayers(int sheet_id, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     boolean hasPlayers = false;

     try {

         pstmt = con.prepareStatement("SELECT activity_sheets_player_id FROM activity_sheets_players WHERE activity_sheet_id = ? LIMIT 1");
         pstmt.clearParameters();
         pstmt.setInt(1, sheet_id);

         rs = pstmt.executeQuery();

         if (rs.next()) {
             hasPlayers = true;
         }

         pstmt.close();

     } catch (Exception exc) {
         
         Utilities.logError("verifyActSlot.checkSlotHasPlayers: ERR=" + exc.toString());
         hasPlayers = false;
     }

     return hasPlayers;
 }
 
 public static boolean checkSlotInUse(int sheet_id, String user, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     boolean hasAccess = false;
     
     try {
         
         pstmt = con.prepareStatement("SELECT sheet_id FROM activity_sheets WHERE sheet_id = ? AND ((in_use_by = '' || in_use_by = ? ) || (UNIX_TIMESTAMP(in_use_at) + (6 * 60)) < UNIX_TIMESTAMP())");
         pstmt.clearParameters();
         pstmt.setInt(1, sheet_id);
         pstmt.setString(2, user);
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {           
             hasAccess = true;
         }
         
     } catch (Exception exc) {
         Utilities.logError("verifyActSlot.checkSlotInUse: ERR=" + exc.toString());         
     } finally {
         Connect.close(rs, pstmt);
     }
     
     return hasAccess;
 }
 
 
 /**
  * checkMemberAccess - Checks a given activity sheet_id to see if the passed member should have access to this time.  This is only to be run if disallow joins is turned on!
  *
  * @param sheet_id sheet_id for the slot in question
  * @param user Username of the currently logged in member
  * @param con Connection to club database
  *
  * @return boolean - True if no players in time slot, or if players found, but current user is one of them
  */
 public static boolean checkMemberHasAccess(int sheet_id, String user, Connection con) {

     PreparedStatement pstmt = null;
     PreparedStatement pstmt2 = null;
     ResultSet rs = null;
     ResultSet rs2 = null;
     
     int activity_id = 0;

     boolean hasAccess = false;
     boolean disallow_joins = false;
     
     if (checkSlotHasPlayers(sheet_id, con)) {    // Only procede if the slot has players

         try {

             pstmt = con.prepareStatement(""
                     + "SELECT act.disallow_joins FROM activity_sheets ash "
                     + "LEFT OUTER JOIN activities act ON ash.activity_id = act.activity_id "
                     + "WHERE sheet_id = ?");
             pstmt.clearParameters();
             pstmt.setInt(1, sheet_id);

             rs = pstmt.executeQuery();

             if (rs.next()) {
                 if (rs.getInt(1) == 1) disallow_joins = true;
             }

             if (disallow_joins) {

                 try {

                     pstmt2 = con.prepareStatement("SELECT username as user FROM activity_sheets_players WHERE activity_sheet_id = ? "
                             + "UNION ALL SELECT last_mod_by AS user FROM activity_sheets WHERE sheet_id = ?");
                     pstmt2.clearParameters();
                     pstmt2.setInt(1, sheet_id);
                     pstmt2.setInt(2, sheet_id);

                     rs2 = pstmt2.executeQuery();

                     while (rs2.next()) {
                         if (user.equalsIgnoreCase(rs2.getString(1))) {
                             hasAccess = true;
                             break;
                         }
                     }

                 } catch (Exception exc) {
                     hasAccess = false;
                     Utilities.logDebug("BK", "TEST 1 - ERR: " + exc.toString());
                 } finally {
                     try { rs2.close(); }
                     catch (Exception ignore) { }

                     try { pstmt2.close(); }
                     catch (Exception ignore) { }
                 }
             }

         } catch (Exception exc) {
             hasAccess = false;
             Utilities.logDebug("BK", "TEST 2 - ERR: " + exc.toString());
         } finally {
             try { rs.close(); }
             catch (Exception ignore) { }

             try { pstmt.close(); }
             catch (Exception ignore) { }
         }
     } else {
         hasAccess = true;
     }
     
     if (hasAccess && !checkSlotInUse(sheet_id, user, con)) {
         hasAccess = false;
     }

     return hasAccess;
 }
 


 // *********************************************************
 //  Check for a member restriction for the time selected
 // *********************************************************
 public static boolean getRestData(parmRest parmr, int time, int activity_id, int rest_id) {

    boolean allow = true;
    boolean suspend = false;

    int ind = 0;

    // Check all restrinctions for this day to see if any affect this time
    while (ind < parmr.MAX && allow && !parmr.restName[ind].equals("")) {           // loop over possible restrictions

        if (parmr.applies[ind] == 1 && parmr.stime[ind] <= time && parmr.etime[ind] >= time) {      // matching time ?

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
 
 
 public static void clearInUse(String user, Connection con) {
     
     PreparedStatement pstmt = null;
     
     try {
         pstmt = con.prepareStatement("UPDATE activity_sheets SET in_use_by = '', in_use_at = 0 WHERE in_use_at <> 0 AND in_use_by = ?");
         pstmt.clearParameters();
         
         pstmt.setString(1, user);
         
         pstmt.executeUpdate();
         
     } catch (Exception exc) {
         Utilities.logError("verifyActSlot.clearInUse - Error freeing up locked up times for user: " + user + " - ERR: " + exc.toString());
     } finally {
         
         try { pstmt.close(); }
         catch (Exception ignore) {}
     }
 }
}
