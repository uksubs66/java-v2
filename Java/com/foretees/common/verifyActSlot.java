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

        try {

            out.println("<!-- Checking activity signup #" + slot_id + " to see if it's busy by " + user + " -->");
            //
            //   Set the entry as busy, IF it is not already
            //
            pstmt = con.prepareStatement (
                "UPDATE activity_sheets " +
                "SET in_use_by = ?, in_use_at = now() " +
                "WHERE sheet_id = ? AND " +
                    "( in_use_by = '' || " +
                    " (UNIX_TIMESTAMP(in_use_at) + (6 * 60)) < UNIX_TIMESTAMP() " + // "  (UNIX_TIMESTAMP(in_use_at) < ( UNIX_TIMESTAMP() + (6 * 30) )) " +
                    ")");
                
            pstmt.clearParameters();
            pstmt.setString(1, user);
            pstmt.setInt(2, slot_id);
            count = pstmt.executeUpdate();
            pstmt.close();
         
            //
            //  If the above was successful, then we now own this sign-up
            //
            if (count > 0) {

                out.println("<!-- Was not busy.  Making it busy now. -->");
            
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
                   slotParms.disallow_joins = rs.getInt("disallow_joins");
                   slotParms.in_slots = rs.getString("related_ids");
                   slotParms.report_ignore = rs.getInt("report_ignore");
                }

                out.println("<!-- B4: in_use=" + in_use + " | slotParms.in_use="+ slotParms.in_use + " -->");
                
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
                out.println("<!-- Unable to make busy -->");
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


 public static int checkInUseM(int slot_id, String user, parmSlot slotParms, Connection con, PrintWriter out)
         throws Exception {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int status = 0;     // return variable
    int count = 0;      // number of consecutive times found
    
    boolean fail = false; // flag for detecting is we lost a time slot

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
               slotParms.disallow_joins = rs.getInt("disallow_joins");

               slotParms.sheet_ids.add( slot_id );      // the inital time requested to the array

            }

        } catch (Exception exc) {

            Utilities.logError("Error1 in verifyActSlot.checkInUseM: slot_id=" + slot_id + ", Error=" + exc.getMessage());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }



        // gather all times on the same 'court' after the initial time requsted
        try {

            pstmt = con.prepareStatement (
               "SELECT sheet_id, in_use_by, blocker_id, event_id, lesson_id, player_name, " +
                    "DATE_FORMAT(date_time, '%Y%m%d') AS date1, " +
                    "DATE_FORMAT(date_time, '%k%i') AS time, " +
                    "DATE_FORMAT(date_time, '%W') AS day_name, " +
                    "DATE_FORMAT(date_time, '%e') AS dd, " +
                    "DATE_FORMAT(date_time, '%c') AS mm, " +
                    "DATE_FORMAT(date_time, '%Y') AS yy " +
               "FROM activity_sheets t1 " +
               "LEFT OUTER JOIN activity_sheets_players t2 ON t1.sheet_id = t2.activity_sheet_id " +
               "WHERE " +
                    "activity_id = ? AND " +
                    "DATE_FORMAT(date_time, '%Y%m%d') = ? AND " +
                    "DATE_FORMAT(date_time, '%k%i') > ? " +
               "ORDER BY date_time " +
               "LIMIT 30;");

            pstmt.clearParameters();
            pstmt.setInt(1, slotParms.activity_id);
            pstmt.setLong(2, slotParms.date);
            pstmt.setInt(3, slotParms.time);
            rs = pstmt.executeQuery();

            while (rs.next()) {

                // only proceed if the time is not in use, not blocked, not covered by
                // an event or lesson and has no players assigned to it
                if (rs.getString("in_use_by").equals("") && rs.getInt("blocker_id") == 0 && rs.getInt("event_id") == 0 &&
                    rs.getInt("lesson_id") == 0 && rs.getString("player_name") == null) {

                    // this time is available

                    // now we could check here to check to see if the booking user
                    // is retricted from playing during this time but what if they are
                    // making this time on the behalf of another member?  Also I don't think we
                    // do this type of check on the golf side - Let's wait and see if this is requested

                    slotParms.sheet_ids.add( rs.getInt("sheet_id") );

                    //debug
                    out.println("<!-- FOUND " + slotParms.sheet_ids.size() + " CONSECUTIVE TIMES (added sheet_id " + rs.getInt("sheet_id") + " to array) -->");

                } else {

                    // found a time that is not available - reset and keep trying - at best we can find the next block of availble times
                    slotParms.sheet_ids.clear();

                    //debug
                    out.println("<!-- STARTING OVER... -->");

                }

                // see if we've found enough times
                if (slotParms.sheet_ids.size() == slotParms.slots) {

                    break;

                }

            } // end while loop

            

            // again, see if we've found enough times
            if (slotParms.sheet_ids.size() == slotParms.slots) {

                // we did - so now lets mark each of them as busy
                for (int i = 0; i < slotParms.slots; i++) {

                    pstmt = con.prepareStatement (
                        "UPDATE activity_sheets " +
                        "SET in_use_by = ?, in_use_at = now() " +
                        "WHERE sheet_id = ? AND " +
                            "( in_use_by = '' || " +
                            " (UNIX_TIMESTAMP(in_use_at) + (6 * 60)) < UNIX_TIMESTAMP() " + 
                            ")");

                    pstmt.clearParameters();
                    pstmt.setString(1, user);
                    pstmt.setInt(2, slotParms.sheet_ids.get(i));

                    count = pstmt.executeUpdate();

                    pstmt.close();

                    if (count == 0) {

                        // we lost one of the times
                        //debug
                        out.println("<!-- FAILED Locking sheet id " + slotParms.sheet_ids.get(i) + " -->");
                        fail = true;
                        break;

                    } else {

                        //debug
                        out.println("<!-- SUCCESS: Locked sheet_id " + slotParms.sheet_ids.get(i) + " -->");

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
                    out.println("<!-- FAILED: Releasing these sheet_ids (" + in + ") -->");

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
                    
                }

            } else {

                // we did NOT find enough times to satisfy the consec request
                status = 1;

            }

        } catch (Exception exc) {

            Utilities.logError("Error2 in verifyActSlot.checkInUseM: initial slot_id=" + slot_id + ", Error=" + exc.getMessage());
            //out.println("Error2 in verifyActSlot.checkInUseM: initial slot_id=" + slot_id + ", Error=" + exc.toString());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

    } // end if required parameters are here


    // determin our return status
    // 0 = sucess
    // 1 = failure to find enough times or get locks on times we did find
    // 9 = found slost but at different time
    
    if ( fail ) {

        // we found times but were not able to lock them all - should rarely happen
        status = 1;
        
    } else if ( status == 1) {
        
        // if already set then we were not able to find enough times to satisfy the request
        // do nothing - this is mainly to avoid an error trying to get(0) from empty array in next else if
        
    } else if ( slot_id != slotParms.sheet_ids.get(0) ) {

        // we found enough slots to fullfill their consec request but the
        // slots found are not at the exact time requested - user must approve these times
        status = 9;
    
    }

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
    String sql = "" + 
            "SELECT COUNT(t2.activity_sheet_id) " +
            "FROM activity_sheets t1 " +
            "LEFT OUTER JOIN activity_sheets_players t2 ON t1.sheet_id = t2.activity_sheet_id " +
            "WHERE " +
                "`show` = 1 AND " +
                "t2.username = ? AND " +
                "activity_id IN (" + in + ") AND " +
                "DATE_FORMAT(date_time, '%Y%m%d') != ? AND "; // exclude today

    String where_week = "" +
            "DATE_FORMAT(date_time, '%Y%m%d') >= ? AND " +
            "DATE_FORMAT(date_time, '%Y%m%d') <= ?";

    String where_month = "" + 
            "DATE_FORMAT(date_time, '%Y') = ? AND " +
            "DATE_FORMAT(date_time, '%m') = ?";

    String where_year = "" + 
            "DATE_FORMAT(date_time, '%Y') = ?";


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
               
               if (mperiod.equals( "Week" )) {       // if WEEK

                  pstmt = con.prepareStatement (sql + where_week);
                  pstmt.clearParameters();
                  pstmt.setString(1, user[i]);     // first player in this slot
                  pstmt.setLong(2, slotParms.date);        // today
                  pstmt.setLong(3, dateStart);             // start of the week
                  pstmt.setLong(4, dateEnd);               // end of the week

               } else if (mperiod.equals( "Month" )) {      // if MONTH

                  pstmt = con.prepareStatement (sql + where_month);
                  pstmt.clearParameters();
                  pstmt.setString(1, user[i]);     // first player in this slot
                  pstmt.setLong(2, slotParms.date);        // today
                  pstmt.setInt(3, slotParms.mm);           // month for this reservation
                  pstmt.setInt(4, slotParms.yy);           // year for this reservation
                  
               } else if (mperiod.equals( "Year" )) {            // if Year

                  pstmt = con.prepareStatement (sql + where_year);
                  pstmt.clearParameters();
                  pstmt.setString(1, user[i]);     // first player in this slot
                  pstmt.setLong(2, slotParms.date);        // today
                  pstmt.setInt(3, slotParms.yy);           // year for this reservation
                  
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


 //
 // For now this method is only checking activites (not tee times or events, we can call verifySlot's version
 // if we want that checking done as well
 //
 public static void checkSched(parmSlot slotParms, Connection con)
         throws Exception {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    parmActivity parmAct = new parmActivity();              // allocate a parm block

    parmAct.activity_id = slotParms.activity_id;            // pass in the slot id so we can determin which activity to load parms for

    try {

        getActivity.getParms(con, parmAct);                 // get the activity config

    } catch (Exception e) {

        throw new Exception("verifyActSlot.checkSched: ERR=" + e.getMessage());

    }

    String in = "";

    try { in = getActivity.buildInString(slotParms.root_activity_id, 1, con); }
    catch (Exception exc) { verifySlot.logError("Error in verifyActSlot.checkSched getting root activities. ERR=" + exc.toString()); }

    String [] user = new String [5];        // usernames - parmAct.max_players
    String [] player = new String [5];      // player name
    String [] oldPlayer = new String [5];   // old player name

    user[0] = slotParms.user1;
    user[1] = slotParms.user2;
    user[2] = slotParms.user3;
    user[3] = slotParms.user4;
    //user[4] = slotParms.user5;

    player[0] = slotParms.player1;
    player[1] = slotParms.player2;
    player[2] = slotParms.player3;
    player[3] = slotParms.player4;
    //player[4] = slotParms.player5;
    
    oldPlayer[0] = slotParms.oldPlayer1;
    oldPlayer[1] = slotParms.oldPlayer2;
    oldPlayer[2] = slotParms.oldPlayer3;
    oldPlayer[3] = slotParms.oldPlayer4;
    //oldPlayer[4] = slotParms.oldPlayer5;


    // mysql date/time format containing the desired time slot date & time
    int hr = slotParms.time / 100;                    // 00 - 23
    int min = slotParms.time - (hr * 100);            // 00 - 59

    String tmp = hr + ":" + min + ":" + "00";

    //
    // statement to check activities play history for today are return the minutes
    // between the desired time and any existing times
    //
    String sql = "" +
            "SELECT activity_id, date_time, DATE_FORMAT(date_time, '%k%i') AS time2, " +
            "ABS(TIME_TO_SEC(TIMEDIFF(DATE_FORMAT(date_time, '%k:%i:00'), '" + tmp + "')) / 60) AS minsbtwn " +
            "FROM activity_sheets t1 " +
            "LEFT OUTER JOIN activity_sheets_players t2 ON t1.sheet_id = t2.activity_sheet_id " +
            "WHERE " +
                "activity_id IN (" + in + ") AND " +
                "t2.username = ? AND " +
                "DATE_FORMAT(t1.date_time, '%Y%m%d') = DATE_FORMAT(?, '%Y%m%d') " +
            "ORDER BY minsbtwn DESC";

    int count = 0;
    int minsbtwn = 0;

    try {

        for (int i = 0; i < parmAct.max_players; i++) {

            // only check for members that are new to this reservation
            if ( !user[i].equals("") && !player[i].equals(oldPlayer[i]) ) {

                count = 0;      // reset
                minsbtwn = 0;

                pstmt = con.prepareStatement ( sql );
                pstmt.clearParameters();
                pstmt.setString(1, user[i]);
                pstmt.setLong(2, slotParms.date);

                rs = pstmt.executeQuery();

                while ( rs.next() ) {

                    count++;
                    slotParms.time2 = rs.getInt("time2");   // last time in rs should be the closest to existing times
                    minsbtwn = rs.getInt("minsbtwn");
                }

                if (count != 0) {

                    if ( count >= parmAct.rndsperday ) {        // if we are over the allowed times

                        slotParms.hit = true;                   // indicate we found them on this days time sheet
                        slotParms.player = player[i];           // store their name for reporting back to user

                        break;

                    } else if ( parmAct.minutesbtwn != 0 && minsbtwn < parmAct.minutesbtwn ) { // if an existing time is too close

                        slotParms.hit3 = true;                  // indicate we found them on this days time sheet
                        slotParms.player = player[i];           // store their name for reporting back to user

                        break;

                    }

                } // end if skip rndsperday & minutesbtwn checks

            } // end if we are going to check this member

        } // end for next player loop

        // debug
        //verifySlot.logError("verifyActSlot.checkSched: " + parmAct.rndsperday + ", " + parmAct.minutesbtwn + ", count=" + count + ", minsbtwn=" + minsbtwn + ", time2=" + slotParms.time2 + ", slotParms.date=" + slotParms.date); // , sql=" + sql);

    } catch (Exception exc) {

        verifySlot.logError("verifyActSlot.checkSched: ERR=" + exc.toString());

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
         hasPlayers = false;
     }

     return hasPlayers;
 }

}
