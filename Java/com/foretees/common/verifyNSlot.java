/***************************************************************************************
 *   verifyNSlot:  This servlet will provide some common tee time request processing methods.
 *
 *       called by:  ProshopTLT_slot
 *                   Proshop_dsheet
 *                   MemberTLT_slot
 *
 *
 *   created:  9/4/2006   Paul S.
 *
 *
 *   last updated:
 *
 *       4/15/10  Updated parmClub calls
 *       4/14/10  Changes to checkMaxGuests/checkGuestQuote/checkMemNums/countGuests so guest types can be unlimited
 *       8/31/09  Changed mship proceessing to grab mships from mship5 instead of club5
 *       5/26/09  Added custom_disp fields and p9 fields to shiftUp method
 *      12/08/08  Added restriction suspension checking for member and guest restrictions
 *       6/26/07  When pulling a handicap from member2b get it from the g_hancap field instead of c_hancap
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

public class verifyNSlot {

   private static String rev = ProcessConstants.REV;   

 
/**
 //************************************************************************
 //
 //  checkInUse - check if slot is in use and if not, set it
 //
 //************************************************************************
 **/

 public static int checkInUse(int notify_id, String user, parmNSlot nSlotParms, Connection con, PrintWriter out)
         throws Exception {


    PreparedStatement pstmt = null;
    //Statement stmt = null;
    ResultSet rs = null;

    int in_use = 1; // default to busy
    int count = 0;

    //
    //  Verify the input parms, if absent then return as if the slot is busy
    //
    if (notify_id != 0 && user != null && !user.equals( "" )) {

        try {

            out.println("<!-- Checking notification #" + notify_id + " to see if it's busy by " + user + " -->");
            //
            //   Set the tee time as busy, IF it is not already (count will be zero if its already busy)
            //
            pstmt = con.prepareStatement (
                "UPDATE notifications SET in_use_by = ?, in_use_at = now() WHERE notification_id = ? AND in_use_by = ''");
                //"UPDATE notifications SET in_use_by = ?, in_use_at = now() WHERE notification_id = ? AND (in_use_by = '' || in_use_by = ?)");

            pstmt.clearParameters();
            pstmt.setString(1, user);
            pstmt.setInt(2, notify_id);
            //pstmt.setString(3, user);
            count = pstmt.executeUpdate();
            pstmt.close();
         
            //
            //  If the above was successful, then we now own this notifcation
            //
            if (count > 0) {

                out.println("<!-- Was not busy.  Making it busy now. -->");
            
                pstmt = con.prepareStatement (
                   "SELECT n.*, c.courseName, " +
                        "DATE_FORMAT(n.created_datetime, '%m/%d/%Y at %l:%i %p') AS created_at, " +
                        "DATE_FORMAT(n.req_datetime, '%W') AS day_name, " +
                        "DATE_FORMAT(n.req_datetime, '%Y%m%d') AS date, " +
                        "DATE_FORMAT(n.req_datetime, '%e') AS dd, " +
                        "DATE_FORMAT(n.req_datetime, '%c') AS mm, " +
                        "DATE_FORMAT(n.req_datetime, '%Y') AS yy, " +
                        "DATE_FORMAT(n.req_datetime, '%k%i') AS ntime " +
                   "FROM notifications n, clubparm2 c " +
                   "WHERE n.notification_id = ? AND c.clubparm_id = n.course_id");

                pstmt.clearParameters();
                pstmt.setInt(1, notify_id);
                rs = pstmt.executeQuery();

                if (rs.next()) {

                   nSlotParms.req_datetime = rs.getString( "req_datetime" );
                   nSlotParms.dd = rs.getInt( "dd" );
                   nSlotParms.mm = rs.getInt( "mm" );
                   nSlotParms.yy = rs.getInt( "yy" );
                   nSlotParms.date = rs.getInt( "date" );
                   nSlotParms.time = rs.getInt( "ntime" );
                   nSlotParms.course_id = rs.getInt( "course_id" );
                   nSlotParms.course = rs.getString( "courseName" );
                   nSlotParms.last_user = rs.getString( "in_use_by" );
                   nSlotParms.in_use = (nSlotParms.last_user.equals("") || nSlotParms.last_user.equalsIgnoreCase( user )) ? 0 : 1; //rs.getInt( "in_use" );
                   nSlotParms.hideNotes = rs.getInt( "hideNotes" );
                   nSlotParms.notes = rs.getString( "notes" );
                   nSlotParms.converted = rs.getInt( "converted" );
                   nSlotParms.orig_by = rs.getString( "created_by" );
                   nSlotParms.orig_at = rs.getString( "created_at" );
                   nSlotParms.day = rs.getString( "day_name" );
                }

                out.println("<!-- B4: in_use=" + in_use + " | nSlotParms.in_use="+ nSlotParms.in_use + " -->");
                
                in_use = nSlotParms.in_use;
                
                // if in use by self then allow
                //if (!nSlotParms.last_user.equalsIgnoreCase( user )) in_use = 1;
                        
                pstmt = con.prepareStatement (
                   "SELECT * " +
                   "FROM notifications_players " +
                   "WHERE notification_id = ? " +
                   "ORDER BY pos");

                pstmt.clearParameters();
                pstmt.setInt(1, notify_id);
                rs = pstmt.executeQuery();

                if (rs.next()) {

                    nSlotParms.player1 = rs.getString( "player_name" );
                    nSlotParms.user1 = rs.getString( "username" );
                    nSlotParms.userg1 = rs.getString( "userg" );
                    nSlotParms.p1cw = rs.getString( "cw" );
                    nSlotParms.p91 = rs.getInt( "9hole" );
                    nSlotParms.players = 1;
                }
                
                if (rs.next()) {

                    nSlotParms.player2 = rs.getString( "player_name" );
                    nSlotParms.user2 = rs.getString( "username" );
                    nSlotParms.userg2 = rs.getString( "userg" );
                    nSlotParms.p2cw = rs.getString( "cw" );
                    nSlotParms.p92 = rs.getInt( "9hole" );
                    nSlotParms.players = 2;
                }
                
                if (rs.next()) {

                    nSlotParms.player3 = rs.getString( "player_name" );
                    nSlotParms.user3 = rs.getString( "username" );
                    nSlotParms.userg3 = rs.getString( "userg" );
                    nSlotParms.p3cw = rs.getString( "cw" );
                    nSlotParms.p93 = rs.getInt( "9hole" );
                    nSlotParms.players = 3;
                }
                
                if (rs.next()) {

                    nSlotParms.player4 = rs.getString( "player_name" );
                    nSlotParms.user4 = rs.getString( "username" );
                    nSlotParms.userg4 = rs.getString( "userg" );
                    nSlotParms.p4cw = rs.getString( "cw" );
                    nSlotParms.p94 = rs.getInt( "9hole" );
                    nSlotParms.players = 4;
                }
                
                if (rs.next()) {

                    nSlotParms.player5 = rs.getString( "player_name" );
                    nSlotParms.user5 = rs.getString( "username" );
                    nSlotParms.userg5 = rs.getString( "userg" );
                    nSlotParms.p5cw = rs.getString( "cw" );
                    nSlotParms.p95 = rs.getInt( "9hole" );
                    nSlotParms.players = 5;
                }
                
            } else {
                out.println("<!-- Unable to make busy -->");
            }// end if count > 0

            pstmt.close();

          }
          catch (SQLException e) {

             throw new Exception("Error checking in-use - verifyNSlot.checkInUse - SQL Exception: " + e.getMessage());
          }
          catch (Exception e) {

             throw new Exception("Error checking in-use - verifyNSlot.checkInUse - Exception: " + e.getMessage());
          }
        
       } // end if 

   return(in_use);
 }


/**
 //************************************************************************
 //
 //  checkGuestQuota - checks for maximum number of guests exceeded per member 
 //                    or per membership during a specified period.
 //
 //************************************************************************
 **/

 public static boolean checkGuestQuota(parmNSlot nSlotParms, Connection con)
         throws Exception {


   //Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   boolean error = false;
   boolean check = false;

   int i = 0;
   int guests1 = 0;             // number of restricted guests in tee time
   int guests2 = 0;                   
   int guests3 = 0;
   int guests4 = 0;
   int guests5 = 0;
   int guests = 0;

   int stime = 0;
   int etime = 0;

   long sdate = 0;
   long edate = 0;

   String rcourse = "";
   String rest_fb = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String per = "";                        // per = 'Member' or 'Tee Time'
   //String errorMsg = "";                 

   //String [] rguest = new String [nSlotParms.MAX_Guests];    // array to hold the Guest Restriction's guest types
   ArrayList<String> rguest = new ArrayList<String>();


   if (nSlotParms.fb == 0) {                   // is Tee time for Front 9?

      nSlotParms.sfb = "Front";
   }

   if (nSlotParms.fb == 1) {                   // is it Back 9?

      nSlotParms.sfb = "Back";
   }

   try {

      PreparedStatement pstmt5 = con.prepareStatement (
         "SELECT * " +
         "FROM guestqta4 " +
         "WHERE sdate <= ? AND edate >= ? AND stime <= ? AND etime >= ? AND activity_id = 0");

      pstmt5.clearParameters();        // clear the parms
      pstmt5.setLong(1, nSlotParms.date);
      pstmt5.setLong(2, nSlotParms.date);
      pstmt5.setInt(3, nSlotParms.time);
      pstmt5.setInt(4, nSlotParms.time);
      rs = pstmt5.executeQuery();      // execute the prepared stmt

      loop1:
      while (rs.next()) {

         sdate = rs.getLong("sdate");
         stime = rs.getInt("stime");
         edate = rs.getLong("edate");
         etime = rs.getInt("etime");
         rcourse = rs.getString("courseName");
         rest_fb = rs.getString("fb");
         per = rs.getString("per");
         nSlotParms.grest_num = rs.getInt("num_guests");
         
         // now look up the guest types for this guest quota
         PreparedStatement pstmt2 = con.prepareStatement (
                 "SELECT guest_type FROM guestqta4_gtypes WHERE guestqta_id = ?");

         pstmt2.clearParameters();
         pstmt2.setInt(1, rs.getInt("id"));

         rs2 = pstmt2.executeQuery();

         while ( rs2.next() ) {

            rguest.add(rs2.getString("guest_type"));

         }

         pstmt2.close();
/*         
         rguest[0] = rs.getString("guest1");
         rguest[1] = rs.getString("guest2");
         rguest[2] = rs.getString("guest3");
         rguest[3] = rs.getString("guest4");
         rguest[4] = rs.getString("guest5");
         rguest[5] = rs.getString("guest6");
         rguest[6] = rs.getString("guest7");
         rguest[7] = rs.getString("guest8");
         rguest[8] = rs.getString("guest9");
         rguest[9] = rs.getString("guest10");
         rguest[10] = rs.getString("guest11");
         rguest[11] = rs.getString("guest12");
         rguest[12] = rs.getString("guest13");
         rguest[13] = rs.getString("guest14");
         rguest[14] = rs.getString("guest15");
         rguest[15] = rs.getString("guest16");
         rguest[16] = rs.getString("guest17");
         rguest[17] = rs.getString("guest18");
         rguest[18] = rs.getString("guest19");
         rguest[19] = rs.getString("guest20");
         rguest[20] = rs.getString("guest21");
         rguest[21] = rs.getString("guest22");
         rguest[22] = rs.getString("guest23");
         rguest[23] = rs.getString("guest24");
         rguest[24] = rs.getString("guest25");
         rguest[25] = rs.getString("guest26");
         rguest[26] = rs.getString("guest27");
         rguest[27] = rs.getString("guest28");
         rguest[28] = rs.getString("guest29");
         rguest[29] = rs.getString("guest30");
         rguest[30] = rs.getString("guest31");
         rguest[31] = rs.getString("guest32");
         rguest[32] = rs.getString("guest33");
         rguest[33] = rs.getString("guest34");
         rguest[34] = rs.getString("guest35");
         rguest[35] = rs.getString("guest36");
*/

         check = false;       // init 'check guests' flag
         userg1 = "";         // init usernames
         userg2 = "";
         userg3 = "";
         userg4 = "";
         userg5 = "";
/*
         //
         //   Change any guest types that are null - for tests below
         //
         i = 0;
         while (i < nSlotParms.MAX_Guests) {

            if (rguest[i].equals( "" )) {

               rguest[i] = "$@#!^&*";      // make so it won't match player name
            }
            i++;
         }         // end of while loop
*/
         //
         //  Check if course and f/b match that specified in restriction
         //
         if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( nSlotParms.course ))) {

            if ((rest_fb.equals( "Both" )) || (rest_fb.equals( nSlotParms.sfb ))) {  // if f/b matches

               //  compare guest types in tee time against those specified in restriction
               i = 0;
               ploop1:
               while (i < rguest.size()) {

                  //
                  //     nSlotParms.gx = guest types specified in player name fields
                  //     rguest[x] = guest types from restriction gotten above
                  //
                  if (!nSlotParms.g1.equals( "" )) {
                     if (nSlotParms.g1.equals( rguest.get(i) )) {
                        check = true;                  // indicate check num of guests
                        userg1 = nSlotParms.userg1;       // save member associated with this guest
                     }
                  }
                  if (!nSlotParms.g2.equals( "" )) {
                     if (nSlotParms.g2.equals( rguest.get(i) )) {
                        check = true;                  // indicate check num of guests
                        userg2 = nSlotParms.userg2;       // save member associated with this guest
                     }
                  }
                  if (!nSlotParms.g3.equals( "" )) {
                     if (nSlotParms.g3.equals( rguest.get(i) )) {
                        check = true;                  // indicate check num of guests
                        userg3 = nSlotParms.userg3;       // save member associated with this guest
                     }
                  }
                  if (!nSlotParms.g4.equals( "" )) {
                     if (nSlotParms.g4.equals( rguest.get(i) )) {
                        check = true;                  // indicate check num of guests
                        userg4 = nSlotParms.userg4;       // save member associated with this guest
                     }
                  }
                  if (!nSlotParms.g5.equals( "" )) {
                     if (nSlotParms.g5.equals( rguest.get(i) )) {
                        check = true;                  // indicate check num of guests
                        userg5 = nSlotParms.userg5;       // save member associated with this guest
                     }
                  }
                  i++;
               }
            }
         }      // end of IF course matches

         if (check == true) {   // if restriction exists for this day and time and there are guests in tee time

            //
            //  Determine the member assigned to the guest and calculate their quota count
            //
            guests1 = 0;          // init # of guests for each member
            guests2 = 0;       
            guests3 = 0;
            guests4 = 0;
            guests5 = 0;

            //
            //  Check each member for duplicates and count these guests first
            //
            if (!userg1.equals( "" )) {

               guests1++;               // count the guest
              
               if (userg1.equals( userg2 )) { 
                 
                  guests1++;            // count the guest
                  userg2 = "";          // remove dup
               }
               if (userg1.equals( userg3 )) {

                  guests1++;            // count the guest
                  userg3 = "";          // remove dup
               }
               if (userg1.equals( userg4 )) {

                  guests1++;            // count the guest
                  userg4 = "";          // remove dup
               }
               if (userg1.equals( userg5 )) {

                  guests1++;            // count the guest
                  userg5 = "";          // remove dup
               }
                  
               // go count the number of guests for this member
               guests = countGuests(con, userg1, nSlotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);
                 
               guests1 += guests;         // add to total
            }

            if (!userg2.equals( "" )) {

               guests2++;               // count the guest

               if (userg2.equals( userg3 )) {

                  guests2++;            // count the guest
                  userg3 = "";          // remove dup
               }
               if (userg2.equals( userg4 )) {

                  guests2++;            // count the guest
                  userg4 = "";          // remove dup
               }
               if (userg2.equals( userg5 )) {

                  guests2++;            // count the guest
                  userg5 = "";          // remove dup
               }

               // go count the number of guests for this member
               guests = countGuests(con, userg2, nSlotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

               guests2 += guests;         // add to total
            }

            if (!userg3.equals( "" )) {

               guests3++;               // count the guest

               if (userg3.equals( userg4 )) {

                  guests3++;            // count the guest
                  userg4 = "";          // remove dup
               }
               if (userg3.equals( userg5 )) {

                  guests3++;            // count the guest
                  userg5 = "";          // remove dup
               }

               // go count the number of guests for this member
               guests = countGuests(con, userg3, nSlotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

               guests3 += guests;         // add to total
            }
              
            if (!userg4.equals( "" )) {

               guests4++;               // count the guest

               if (userg4.equals( userg5 )) {

                  guests4++;            // count the guest
                  userg5 = "";          // remove dup
               }

               // go count the number of guests for this member
               guests = countGuests(con, userg4, nSlotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

               guests4 += guests;         // add to total
            }

            if (!userg5.equals( "" )) {

               guests5++;               // count the guest

               // go count the number of guests for this member
               guests = countGuests(con, userg5, nSlotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

               guests5 += guests;         // add to total
            }

            //
            //  Process according to the 'per' value; member or member number
            //
            if (per.startsWith( "Membership" )) {

               if (!userg1.equals( "" )) {

                  // go count the number of guests for this member number
                  guests = checkMnums(con, userg1, nSlotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

                  guests1 += guests;         // add to total
               }
               if (!userg2.equals( "" )) {

                  // go count the number of guests for this member number
                  guests = checkMnums(con, userg2, nSlotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

                  guests2 += guests;         // add to total
               }
               if (!userg3.equals( "" )) {

                  // go count the number of guests for this member number
                  guests = checkMnums(con, userg3, nSlotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

                  guests3 += guests;         // add to total
               }
               if (!userg4.equals( "" )) {

                  // go count the number of guests for this member number
                  guests = checkMnums(con, userg4, nSlotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

                  guests4 += guests;         // add to total
               }
               if (!userg5.equals( "" )) {

                  // go count the number of guests for this member number
                  guests = checkMnums(con, userg5, nSlotParms, sdate, edate, stime, etime, rest_fb, rcourse, rguest);

                  guests5 += guests;         // add to total
               }
            }

            // if num of guests in quota count (guests_) > num allowed (grest_num) per member
            //
            //       to get here guests_ is = # of guests accumulated for member
            //       grest_num is 0 - 999 (restriction quota)
            //       per is 'Member' or 'Membership Number'
            //
            if (guests1 > nSlotParms.grest_num || guests2 > nSlotParms.grest_num || guests3 > nSlotParms.grest_num || 
                guests4 > nSlotParms.grest_num || guests5 > nSlotParms.grest_num) {

               error = true;                 // set error flag
               nSlotParms.grest_per = per;    // save this rest's per value
                 
               if (guests1 > nSlotParms.grest_num) {
  
                  nSlotParms.player = nSlotParms.player1;   // save member name
               }
               if (guests2 > nSlotParms.grest_num) {

                  nSlotParms.player = nSlotParms.player2;   // save member name
               }
               if (guests3 > nSlotParms.grest_num) {

                  nSlotParms.player = nSlotParms.player3;   // save member name
               }
               if (guests4 > nSlotParms.grest_num) {

                  nSlotParms.player = nSlotParms.player4;   // save member name
               }
               if (guests5 > nSlotParms.grest_num) {

                  nSlotParms.player = nSlotParms.player5;   // save member name
               }
               break loop1;                  // done checking - exit while loop
            }

         }    // end of IF true
      }       // end of loop1 while loop

      pstmt5.close();

   }
   catch (Exception e) {

      throw new Exception("Error Checking Guest Rest - verifyNSlot: " + e.getMessage());
   }

   return(error);

 }


/**
 //************************************************************************
 //
 //  checkDaysAdv - checks members for exceeding 'Days in Advance'
 //                   based on their membership type.
 //
 //************************************************************************
 **/

 public static boolean checkDaysAdv(parmNSlot nSlotParms, Connection con)
         throws Exception {


    //Statement stmt = null;
    //ResultSet rs = null;

    int year = 0;
    int month = 0;
    int day = 0;
    int days = 0;
    int dayNum = 0;
    //int count = 0;
    int i = 0;
    int ind = 0;

    String player = "";
    String mship = "";
    //String mtype = "";
    //String user = "";

    String [] userA = new String [5];      // array to hold the players' usernames
    String [] mshipA = new String [5];     // array to hold the players' membership types
    String [] mtypeA = new String [5];     // array to hold the players' member types
    String [] mstypeA = new String [5];    // array to hold the players' member sub-types
    String [] mNumA = new String [5];      // array to hold the players' member numbers
    String [] playerA = new String [5];    // array to hold the players' names
    String [] oldplayerA = new String [5];    // array to hold the old players' names
    int [] daysA = new int [7];            // array to hold the days in adv for each day of the week

    boolean error = false;
    //boolean skip = false;

    //
    //  parm block to hold the club parameters
    //
    parmClub parm = new parmClub(0, con);   // since this is a notification site we can hard code a zero for the root_activity_id

    try {
        
        //
        //  put player info in arrays for processing below
        //
        userA[0] = nSlotParms.user1;
        userA[1] = nSlotParms.user2;
        userA[2] = nSlotParms.user3;
        userA[3] = nSlotParms.user4;
        userA[4] = nSlotParms.user5;
        mshipA[0] = nSlotParms.mship1;
        mshipA[1] = nSlotParms.mship2;
        mshipA[2] = nSlotParms.mship3;
        mshipA[3] = nSlotParms.mship4;
        mshipA[4] = nSlotParms.mship5;
        mtypeA[0] = nSlotParms.mtype1;
        mtypeA[1] = nSlotParms.mtype2;
        mtypeA[2] = nSlotParms.mtype3;
        mtypeA[3] = nSlotParms.mtype4;
        mtypeA[4] = nSlotParms.mtype5;
        mstypeA[0] = nSlotParms.mstype1;
        mstypeA[1] = nSlotParms.mstype2;
        mstypeA[2] = nSlotParms.mstype3;
        mstypeA[3] = nSlotParms.mstype4;
        mstypeA[4] = nSlotParms.mstype5;
        mNumA[0] = nSlotParms.mNum1;
        mNumA[1] = nSlotParms.mNum2;
        mNumA[2] = nSlotParms.mNum3;
        mNumA[3] = nSlotParms.mNum4;
        mNumA[4] = nSlotParms.mNum5;
        playerA[0] = nSlotParms.player1;
        playerA[1] = nSlotParms.player2;
        playerA[2] = nSlotParms.player3;
        playerA[3] = nSlotParms.player4;
        playerA[4] = nSlotParms.player5;
        oldplayerA[0] = nSlotParms.oldPlayer1;
        oldplayerA[1] = nSlotParms.oldPlayer2;
        oldplayerA[2] = nSlotParms.oldPlayer3;
        oldplayerA[3] = nSlotParms.oldPlayer4;
        oldplayerA[4] = nSlotParms.oldPlayer5;

        //
        //  Get the tee time's date values
        //
        month = nSlotParms.mm;                    // get month
        day = nSlotParms.dd;                      // get day
        year = nSlotParms.yy;                     // get year

        //
        // Calculate the number of days between today and the date requested (=> ind)
        // and get the day of the week (for the requested tee time)
        //
        BigDate today = BigDate.localToday();                 // get today's date
        BigDate thisdate = new BigDate(year, month, day);     // get requested date

        dayNum = thisdate.getDayOfWeek();                     // get req'd date's day of week (0 - 6)

        ind = (thisdate.getOrdinal() - today.getOrdinal());   // number of days between

        //
        //   Get the 'days in adv' parms specified for this club
        //
        getClub.getParms(con, parm);        // get the club parms

        //
        //  Check each player that is a member for 'days in adv' violation
        //
        i = 0;
        loop1:
        while (i < 5) {           // do each player

            if (!mshipA[i].equals( "" )) {

                mship = mshipA[i];
                player = playerA[i];

                //
                //  use the member's mship type to determine which 'days in advance' parms to use
                //
                getDaysInAdv(con, parm, mship);        // get the days in adv data for this member

                daysA[0] = parm.advdays1;     // get days in adv for this type
                daysA[1] = parm.advdays2;     // Monday
                daysA[2] = parm.advdays3;
                daysA[3] = parm.advdays4;
                daysA[4] = parm.advdays5;
                daysA[5] = parm.advdays6;
                daysA[6] = parm.advdays7;     // Saturday

                days = daysA[dayNum];            // get the value based on the day of the week

                if (ind > days) {                // if tee time's days in adv is more than allowed for player

                    //
                    //  ok if player was already on tee time (already approved by pro)
                    //
                    if (!player.equals( oldplayerA[0] ) && !player.equals( oldplayerA[1] ) &&
                        !player.equals( oldplayerA[2] ) && !player.equals( oldplayerA[3] ) &&
                        !player.equals( oldplayerA[4] )) {

                        error = true;                 // error
                        nSlotParms.player = player;
                        break loop1;                  // exit loop
                    }
                
                }

            }   // end of IF mship not null

            i++;

        }       // end of WHILE

    }
    catch (Exception e) {

        throw new Exception("Error checking days in advance - verifyNSlot.checkDaysAdv " + e.getMessage());
    }

    return(error);

 }


/**
 //************************************************************************
 //
 //  Count the number of guests that a member has scheduled in the specified time.
 //
 //    Called by:  checkGuestQuota and checkMnums above
 //
 //    Check teecurr and tee past for all specified guest types that are
 //    associated with this member or member number.
 //
 //************************************************************************
 **/

 public static int countGuests(Connection con, String user, parmNSlot nSlotParms, long sdate, long edate, int stime, int etime,
                               String rfb, String rcourse, ArrayList<String> rguest)
                           throws Exception {

   ResultSet rs = null;

   int guests = 0;
   int time = 0;
   int fb = 0;
   int rest_fb = 0;
   int i = 0;

   long date = 0;

   //String sfb = "";
   String course = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   //String errorMsg = "";
  
   //  convert restriction's f/b
   if (rfb.equalsIgnoreCase( "Front" )) {      
      rest_fb = 0;
   } else {
      rest_fb = 1;
   }


   //
   //  Count all guests with matching guest types that are associated with this member (teecurr)
   //
   try {
     
      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT date, time, courseName, player1, player2, player3, player4, fb, player5, " +
         "userg1, userg2, userg3, userg4, userg5 " +
         "FROM teecurr2 " +
         "WHERE date <= ? AND date >= ? AND time <= ? AND time >= ? AND " +
         "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

      pstmt1.clearParameters();        // clear the parms and check player 1
      pstmt1.setLong(1, edate);
      pstmt1.setLong(2, sdate);
      pstmt1.setInt(3, etime);
      pstmt1.setInt(4, stime);
      pstmt1.setString(5, user);
      pstmt1.setString(6, user);
      pstmt1.setString(7, user);
      pstmt1.setString(8, user);
      pstmt1.setString(9, user);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         date = rs.getLong("date");
         time = rs.getInt("time");
         course = rs.getString("courseName");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         fb = rs.getInt("fb");
         player5 = rs.getString("player5");
         userg1 = rs.getString("userg1");
         userg2 = rs.getString("userg2");
         userg3 = rs.getString("userg3");
         userg4 = rs.getString("userg4");
         userg5 = rs.getString("userg5");

         //
         //  matching tee time found in teecurr - check if course, date and time match
         //  Make sure this is not this tee time before changes,
         //
         if ((date != nSlotParms.date) || (time != nSlotParms.time) || (!course.equals( nSlotParms.course ))) {

            //
            //  Check if course and f/b match that specified in restriction
            //
            if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( course ))) {

               if ((rfb.equals( "Both" )) || (rest_fb == fb )) {  // if f/b matches

                  // check if any players from the tee time are a restricted guest
                  if (user.equalsIgnoreCase( userg1 )) {
                     i = 0;
                     loop1:
                     while (i < nSlotParms.MAX_Guests) {
                        if (player1.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                           guests++;             // bump count of guests for quota check
                           break loop1;          // exit loop
                        }
                        i++;
                     }
                  }
                  if (user.equalsIgnoreCase( userg2 )) {
                     i = 0;
                     loop2:
                     while (i < nSlotParms.MAX_Guests) {
                        if (player2.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                           guests++;             // bump count of guests for quota check
                           break loop2;          // exit loop
                        }
                        i++;
                     }
                  }
                  if (user.equalsIgnoreCase( userg3 )) {
                     i = 0;
                     loop3:
                     while (i < nSlotParms.MAX_Guests) {
                        if (player3.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                           guests++;             // bump count of guests for quota check
                           break loop3;          // exit loop
                        }
                        i++;
                     }
                  }
                  if (user.equalsIgnoreCase( userg4 )) {
                     i = 0;
                     loop4:
                     while (i < nSlotParms.MAX_Guests) {
                        if (player4.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                           guests++;             // bump count of guests for quota check
                           break loop4;          // exit loop
                        }
                        i++;
                     }
                  }
                  if (user.equalsIgnoreCase( userg5 )) {
                     i = 0;
                     loop5:
                     while (i < nSlotParms.MAX_Guests) {
                        if (player5.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                           guests++;             // bump count of guests for quota check
                           break loop5;          // exit loop
                        }
                        i++;
                     }
                  }
               }
            }
         }   // end of IF tee time not 'this tee time'
      }   // end of WHILE

      pstmt1.close();

      //
      //  Count all guests with matching guest types that are associated with this member (teepast)
      //
      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT courseName, player1, player2, player3, player4, fb, player5, " +
         "userg1, userg2, userg3, userg4, userg5 " +
         "FROM teepast2 " +
         "WHERE date <= ? AND date >= ? AND time <= ? AND time >= ? AND " +
         "(userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?)");

      pstmt2.clearParameters();        // clear the parms and check player 1
      pstmt2.setLong(1, edate);
      pstmt2.setLong(2, sdate);
      pstmt2.setInt(3, etime);
      pstmt2.setInt(4, stime);
      pstmt2.setString(5, user);
      pstmt2.setString(6, user);
      pstmt2.setString(7, user);
      pstmt2.setString(8, user);
      pstmt2.setString(9, user);
      rs = pstmt2.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         course = rs.getString("courseName");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         fb = rs.getInt("fb");
         player5 = rs.getString("player5");
         userg1 = rs.getString("userg1");
         userg2 = rs.getString("userg2");
         userg3 = rs.getString("userg3");
         userg4 = rs.getString("userg4");
         userg5 = rs.getString("userg5");

         //
         //  Check if course and f/b match that specified in restriction
         //
         if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( course ))) {

            if ((rfb.equals( "Both" )) || (rest_fb == fb )) {  // if f/b matches

               //
               //  matching tee time found in teecurr
               //  check if any players from the tee time are a restricted guest
               //
               if (user.equalsIgnoreCase( userg1 )) {
                  i = 0;
                  loop11:
                  while (i < nSlotParms.MAX_Guests) {
                     if (player1.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                        guests++;             // bump count of guests for quota check
                        break loop11;          // exit loop
                     }
                     i++;
                  }
               }
               if (user.equalsIgnoreCase( userg2 )) {
                  i = 0;
                  loop12:
                  while (i < nSlotParms.MAX_Guests) {
                     if (player2.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                        guests++;             // bump count of guests for quota check
                        break loop12;          // exit loop
                     }
                     i++;
                  }
               }
               if (user.equalsIgnoreCase( userg3 )) {
                  i = 0;
                  loop13:
                  while (i < nSlotParms.MAX_Guests) {
                     if (player3.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                        guests++;             // bump count of guests for quota check
                        break loop13;          // exit loop
                     }
                     i++;
                  }
               }
               if (user.equalsIgnoreCase( userg4 )) {
                  i = 0;
                  loop14:
                  while (i < nSlotParms.MAX_Guests) {
                     if (player4.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                        guests++;             // bump count of guests for quota check
                        break loop14;          // exit loop
                     }
                     i++;
                  }
               }
               if (user.equalsIgnoreCase( userg5 )) {
                  i = 0;
                  loop15:
                  while (i < nSlotParms.MAX_Guests) {
                     if (player5.startsWith( rguest.get(i) )) {    // if matching guest type & associated with this user
                        guests++;             // bump count of guests for quota check
                        break loop15;          // exit loop
                     }
                     i++;
                  }
               }
            }
         }
      }   // end of WHILE

      pstmt2.close();

   }
   catch (Exception e) {

      throw new Exception("Error Counting Guests for Guest Rest - verifyNSlot: " + e.getMessage());
   }

   return(guests);
 }

 
/**
 //************************************************************************
 //
 //  Find any other members with the same Member Number and count their guests.
 //
 //    Called by:  checkGuestQuota above
 //
 //************************************************************************
 **/

 public static int checkMnums(Connection con, String user, parmNSlot nSlotParms, long sdate, long edate, int stime, int etime,
                               String rfb, String rcourse, ArrayList<String> rguest)
                           throws Exception {

   ResultSet rs = null;

   int guests = 0;
   int guests2 = 0;
   int i = 0;

   String mNum = "";
   String tuser = "";


   //****************************************************************************
   //  per = Membership Number  -  check all members with the same Member Number
   //****************************************************************************

   try {

      //  get this user's mNum
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT memNum FROM member2b WHERE username = ?");

      pstmt3.clearParameters();        // clear the parms
      pstmt3.setString(1, user);
      rs = pstmt3.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         mNum = rs.getString(1);       // get this user's member number
      }
      pstmt3.close();

      if (!mNum.equals( "" )) {     // if there is one specified

         //  get all users with matching mNum and put in userm array
         PreparedStatement pstmt4 = con.prepareStatement (
            "SELECT username FROM member2b WHERE memNum = ?");

         pstmt4.clearParameters();        // clear the parms
         pstmt4.setString(1, mNum);
         rs = pstmt4.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            tuser = rs.getString(1);       // get the username
            if (!tuser.equals( "" ) && !tuser.equalsIgnoreCase( user )) {   // if exists and not this user

               // go count the number of guests for this member
               guests += countGuests(con, tuser, nSlotParms, sdate, edate, stime, etime, rfb, rcourse, rguest);

            }
         }
         pstmt4.close();
      }

   }
   catch (Exception e) {

      throw new Exception("Error Checking Mnums for Guest Rest - verifyNSlot: " + e.getMessage());
   }

   return(guests);
 }


/**
  //************************************************************************
  //  getDaysInAdv - get the 'days in adv' data for the specified membership type.
  //
  //
  //   receives:  a club parm block and mship type
  //
  //   returns: void (parms are saved in the parm block)
  //
  //************************************************************************
 **/

 public static void getDaysInAdv(Connection con, parmClub parm, String mship) {


   int hour = 0;
     
   loop1:
   for (int i = 0; i < parm.MAX_Mships; i++) {   // check all mship types for a match
     
      if (mship.equals( parm.mship[i] )) {

         parm.advdays1 = parm.days1[i];     // get days in adv for this type
         parm.advdays2 = parm.days2[i];
         parm.advdays3 = parm.days3[i];
         parm.advdays4 = parm.days4[i];
         parm.advdays5 = parm.days5[i];
         parm.advdays6 = parm.days6[i];
         parm.advdays7 = parm.days7[i];

         parm.advhr1 = parm.advhrd1[i];
         parm.advhr2 = parm.advhrd2[i];
         parm.advhr3 = parm.advhrd3[i];
         parm.advhr4 = parm.advhrd4[i];
         parm.advhr5 = parm.advhrd5[i];
         parm.advhr6 = parm.advhrd6[i];
         parm.advhr7 = parm.advhrd7[i];

         parm.advmin1 = parm.advmind1[i];
         parm.advmin2 = parm.advmind2[i];
         parm.advmin3 = parm.advmind3[i];
         parm.advmin4 = parm.advmind4[i];
         parm.advmin5 = parm.advmind5[i];
         parm.advmin6 = parm.advmind6[i];
         parm.advmin7 = parm.advmind7[i];

         parm.advam1 = parm.advamd1[i];
         parm.advam2 = parm.advamd2[i];
         parm.advam3 = parm.advamd3[i];
         parm.advam4 = parm.advamd4[i];
         parm.advam5 = parm.advamd5[i];
         parm.advam6 = parm.advamd6[i];
         parm.advam7 = parm.advamd7[i];
           
         break loop1;        // exit loop
      }
   }

   //
   //  Calculate the time value for the advance parms
   //
   hour = parm.advhr1;
     
   if (hour == 12) {

      hour = 0;                      // if midnight or noon, change to 0 hours
   }
     
   parm.advtime1 = (hour * 100) + parm.advmin1;     // create military time

   if (parm.advam1.equals( "PM" )) {

      parm.advtime1 += 1200;                // if PM, adjust for military (noon becomes 1200)
   }

   hour = parm.advhr2;        // do next day

   if (hour == 12) {

      hour = 0;                   
   }

   parm.advtime2 = (hour * 100) + parm.advmin2;    

   if (parm.advam2.equals( "PM" )) {

      parm.advtime2 += 1200;                
   }

   hour = parm.advhr3;        // do next day

   if (hour == 12) {

      hour = 0;
   }

   parm.advtime3 = (hour * 100) + parm.advmin3;

   if (parm.advam3.equals( "PM" )) {

      parm.advtime3 += 1200;
   }

   hour = parm.advhr4;        // do next day

   if (hour == 12) {

      hour = 0;
   }

   parm.advtime4 = (hour * 100) + parm.advmin4;

   if (parm.advam4.equals( "PM" )) {

      parm.advtime4 += 1200;
   }

   hour = parm.advhr5;        // do next day

   if (hour == 12) {

      hour = 0;
   }

   parm.advtime5 = (hour * 100) + parm.advmin5;

   if (parm.advam5.equals( "PM" )) {

      parm.advtime5 += 1200;
   }

   hour = parm.advhr6;        // do next day

   if (hour == 12) {

      hour = 0;
   }

   parm.advtime6 = (hour * 100) + parm.advmin6;

   if (parm.advam6.equals( "PM" )) {

      parm.advtime6 += 1200;
   }

   hour = parm.advhr7;        // do next day

   if (hour == 12) {

      hour = 0;
   }

   parm.advtime7 = (hour * 100) + parm.advmin7;

   if (parm.advam7.equals( "PM" )) {

      parm.advtime7 += 1200;
   }

 }  // end of getDaysInAdv

 
/**
 //************************************************************************
 //
 //  Check Member Number restrictions
 //
 //     First, find all restrictions within date & time constraints
 //     Then, find the ones for this day
 //     Then, check all players' member numbers against all others in the time period
 //
 //************************************************************************
 **/

 public static boolean checkMemNum(parmNSlot nSlotParms, Connection con)
         throws Exception {


   //Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs7 = null;

   boolean error = false;

   int ind = 0;
   int rest_stime = 0;
   int rest_etime = 0;
   int mems = 0;
   int time2 = 0;
   int t_fb = 0;

   String course2 = "";
   String sfb2 = "";
   String rest_fb = "";
   String rest_course = "";
   String rest_recurr = "";
   String rmNum1 = "";
   String rmNum2 = "";
   String rmNum3 = "";
   String rmNum4 = "";
   String rmNum5 = "";

   try {
      PreparedStatement pstmt7b = con.prepareStatement (
         "SELECT name, stime, etime, recurr, courseName, fb, num_mems " +
         "FROM mnumres2 WHERE sdate <= ? AND edate >= ? AND " +
         "stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-')");


      pstmt7b.clearParameters();          // clear the parms
      pstmt7b.setLong(1, nSlotParms.date);
      pstmt7b.setLong(2, nSlotParms.date);
      pstmt7b.setInt(3, nSlotParms.time);
      pstmt7b.setInt(4, nSlotParms.time);
      pstmt7b.setString(5, nSlotParms.course);

      rs = pstmt7b.executeQuery();      // find all matching restrictions, if any

      error = false;                    // init 'hit' flag
      ind = 0;                          // init matching member count

      if (nSlotParms.fb == 0) {                    // is Tee time for Front 9?

         nSlotParms.sfb = "Front";
      }

      if (nSlotParms.fb == 1) {                    // is it Back 9?

         nSlotParms.sfb = "Back";
      }

      loop3:
      while (rs.next()) {              // check all matching restrictions for this day & F/B

         nSlotParms.rest_name = rs.getString("name");    // get name for error message
         rest_stime = rs.getInt("stime");
         rest_etime = rs.getInt("etime");
         rest_recurr = rs.getString("recurr");
         rest_course = rs.getString("courseName");
         rest_fb = rs.getString("fb");
         mems = rs.getInt("num_mems");

         //
         //  We must check the recurrence for this day (Monday, etc.)
         //
         if ((rest_recurr.equals( "Every " + nSlotParms.day )) ||               // if this day
             (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
             ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
               (!nSlotParms.day.equalsIgnoreCase( "saturday" )) &&
               (!nSlotParms.day.equalsIgnoreCase( "sunday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
              (nSlotParms.day.equalsIgnoreCase( "saturday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
              (nSlotParms.day.equalsIgnoreCase( "sunday" )))) {

            //
            //  Now check if F/B matches this tee time
            //
            if (rest_fb.equals( "Both" ) || rest_fb.equals( nSlotParms.sfb )) {

               //
               //  Found a restriction that matches date, time, day, course & F/B - check each member player
               //
               //   Check Player 1
               //
               if (!nSlotParms.mNum1.equals( "" )) {           // if this player is a member and member number exists

                  PreparedStatement pstmt7c = con.prepareStatement (
                     "SELECT time, fb, courseName, mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 " +
                     "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND date = ? " +
                     "AND time <= ? AND time >= ?");

                  ind = 0;                           // init fields

                  pstmt7c.clearParameters();        // clear the parms and check player 1
                  pstmt7c.setString(1, nSlotParms.mNum1);
                  pstmt7c.setString(2, nSlotParms.mNum1);
                  pstmt7c.setString(3, nSlotParms.mNum1);
                  pstmt7c.setString(4, nSlotParms.mNum1);
                  pstmt7c.setString(5, nSlotParms.mNum1);
                  pstmt7c.setLong(6, nSlotParms.date);
                  pstmt7c.setInt(7, rest_etime);
                  pstmt7c.setInt(8, rest_stime);
                  rs7 = pstmt7c.executeQuery();      // execute the prepared stmt

                  while (rs7.next()) {

                     time2 = rs7.getInt("time");
                     t_fb = rs7.getInt("fb");
                     course2 = rs7.getString("courseName");
                     rmNum1 = rs7.getString("mNum1");
                     rmNum2 = rs7.getString("mNum2");
                     rmNum3 = rs7.getString("mNum3");
                     rmNum4 = rs7.getString("mNum4");
                     rmNum5 = rs7.getString("mNum5");

                     //
                     //  matching member number found in teecurr - check if course and f/b match
                     //
                     if (t_fb == 0) {                   // is Tee time for Front 9?

                        sfb2 = "Front";
                     }

                     if (t_fb == 1) {                   // is it Back 9?

                        sfb2 = "Back";
                     }

                     //
                     //  First make sure this is not this tee time before changes,
                     //  Then check if it matches the criteria for the restriction.
                     //
                     if ((time2 != nSlotParms.time) || (!course2.equals( nSlotParms.course ))) {  // either time or course is diff

                        if ((rest_fb.equals( "Both" ) || rest_fb.equals( sfb2 )) &&
                            (rest_course.equals( "-ALL-" ) || rest_course.equals( course2 ))) {

                           if (nSlotParms.mNum1.equals( rmNum1 )) {  // count number of players with this member number already scheduled
                              ind++;
                           }
                           if (nSlotParms.mNum1.equals( rmNum2 )) {
                              ind++;
                           }
                           if (nSlotParms.mNum1.equals( rmNum3 )) {
                              ind++;
                           }
                           if (nSlotParms.mNum1.equals( rmNum4 )) {
                              ind++;
                           }
                           if (nSlotParms.mNum1.equals( rmNum5 )) {
                              ind++;
                           }
                        }
                     }

                  } // end of while members

                  nSlotParms.pnum1 = "";
                  nSlotParms.pnum2 = "";
                  nSlotParms.pnum3 = "";
                  nSlotParms.pnum4 = "";
                  nSlotParms.pnum5 = "";

                  //
                  //  Now check if any other members in this tee time match
                  //
                  nSlotParms.pnum1 = nSlotParms.player1;  // save this player name for error msg

                  if (nSlotParms.mNum1.equals( nSlotParms.mNum2 )) {

                     ind++;
                     nSlotParms.pnum2 = nSlotParms.player2;  // match found for player - save for error msg
                  }
                  if (nSlotParms.mNum1.equals( nSlotParms.mNum3 )) {

                     ind++;
                     nSlotParms.pnum3 = nSlotParms.player3;  // match found for player - save for error msg
                  }
                  if (nSlotParms.mNum1.equals( nSlotParms.mNum4 )) {

                     ind++;
                     nSlotParms.pnum4 = nSlotParms.player4;  // match found for player - save for error msg
                  }
                  if (nSlotParms.mNum1.equals( nSlotParms.mNum5 )) {

                     ind++;
                     nSlotParms.pnum5 = nSlotParms.player5;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     if (!nSlotParms.player1.equals( nSlotParms.oldPlayer1 ) &&
                         !nSlotParms.player1.equals( nSlotParms.oldPlayer2 ) &&
                         !nSlotParms.player1.equals( nSlotParms.oldPlayer3 ) &&
                         !nSlotParms.player1.equals( nSlotParms.oldPlayer4 ) &&
                         !nSlotParms.player1.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                        error = true;      // max # exceeded - reject
                     }
                  }

                  pstmt7c.close();

               }  // end of member 1 restrictions if

               //
               //   Check Player 2
               //
               if ((error == false) && (!nSlotParms.mNum2.equals( "" ))) {   // if this player is a member

                  PreparedStatement pstmt7c = con.prepareStatement (
                     "SELECT time, fb, courseName, mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 " +
                     "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND date = ? " +
                     "AND time <= ? AND time >= ?");

                  ind = 0;                           // init fields
                  nSlotParms.pnum1 = "";
                  nSlotParms.pnum2 = "";
                  nSlotParms.pnum3 = "";
                  nSlotParms.pnum4 = "";
                  nSlotParms.pnum5 = "";

                  pstmt7c.clearParameters();        // clear the parms and check player 2
                  pstmt7c.setString(1, nSlotParms.mNum2);
                  pstmt7c.setString(2, nSlotParms.mNum2);
                  pstmt7c.setString(3, nSlotParms.mNum2);
                  pstmt7c.setString(4, nSlotParms.mNum2);
                  pstmt7c.setString(5, nSlotParms.mNum2);
                  pstmt7c.setLong(6, nSlotParms.date);
                  pstmt7c.setInt(7, rest_etime);
                  pstmt7c.setInt(8, rest_stime);
                  rs7 = pstmt7c.executeQuery();      // execute the prepared stmt

                  while (rs7.next()) {

                     time2 = rs7.getInt("time");
                     t_fb = rs7.getInt("fb");
                     course2 = rs7.getString("courseName");
                     rmNum1 = rs7.getString("mNum1");
                     rmNum2 = rs7.getString("mNum2");
                     rmNum3 = rs7.getString("mNum3");
                     rmNum4 = rs7.getString("mNum4");
                     rmNum5 = rs7.getString("mNum5");

                     //
                     //  matching member number found in teecurr - check if course and f/b match
                     //
                     if (t_fb == 0) {                   // is Tee time for Front 9?

                        sfb2 = "Front";
                     }

                     if (t_fb == 1) {                   // is it Back 9?

                        sfb2 = "Back";
                     }

                     //
                     //  First make sure this is not this tee time before changes,
                     //  Then check if it matches the criteria for the restriction.
                     //
                     if ((time2 != nSlotParms.time) || (!course2.equals( nSlotParms.course ))) {  // either time or course is diff

                        if ((rest_fb.equals( "Both" ) || rest_fb.equals( sfb2 )) &&
                            (rest_course.equals( "-ALL-" ) || rest_course.equals( course2 ))) {

                           if (nSlotParms.mNum2.equals( rmNum1 )) {  // count number of players with this member number already scheduled
                              ind++;
                           }
                           if (nSlotParms.mNum2.equals( rmNum2 )) {
                              ind++;
                           }
                           if (nSlotParms.mNum2.equals( rmNum3 )) {
                              ind++;
                           }
                           if (nSlotParms.mNum2.equals( rmNum4 )) {
                              ind++;
                           }
                           if (nSlotParms.mNum2.equals( rmNum5 )) {
                              ind++;
                           }
                        }
                     }

                  } // end of while members

                  //
                  //  Now check if any other members in this tee time match
                  //
                  nSlotParms.pnum2 = nSlotParms.player2;  // save this player name for error msg

                  if (nSlotParms.mNum2.equals( nSlotParms.mNum1 )) {

                     ind++;
                     nSlotParms.pnum1 = nSlotParms.player1;  // match found for player - save for error msg
                  }
                  if (nSlotParms.mNum2.equals( nSlotParms.mNum3 )) {

                     ind++;
                     nSlotParms.pnum3 = nSlotParms.player3;  // match found for player - save for error msg
                  }
                  if (nSlotParms.mNum2.equals( nSlotParms.mNum4 )) {

                     ind++;
                     nSlotParms.pnum4 = nSlotParms.player4;  // match found for player - save for error msg
                  }
                  if (nSlotParms.mNum2.equals( nSlotParms.mNum5 )) {

                     ind++;
                     nSlotParms.pnum5 = nSlotParms.player5;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     if (!nSlotParms.player2.equals( nSlotParms.oldPlayer1 ) &&
                         !nSlotParms.player2.equals( nSlotParms.oldPlayer2 ) &&
                         !nSlotParms.player2.equals( nSlotParms.oldPlayer3 ) &&
                         !nSlotParms.player2.equals( nSlotParms.oldPlayer4 ) &&
                         !nSlotParms.player2.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                        error = true;      // max # exceeded - reject
                     }
                  }

                  pstmt7c.close();

               }  // end of member 2 restrictions if

               //
               //   Check Player 3
               //
               if ((error == false) && (!nSlotParms.mNum3.equals( "" ))) {           // if this player is a member

                  PreparedStatement pstmt7c = con.prepareStatement (
                     "SELECT time, fb, courseName, mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 " +
                     "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND date = ? " +
                     "AND time <= ? AND time >= ?");

                  ind = 0;                           // init fields
                  nSlotParms.pnum1 = "";
                  nSlotParms.pnum2 = "";
                  nSlotParms.pnum3 = "";
                  nSlotParms.pnum4 = "";
                  nSlotParms.pnum5 = "";

                  pstmt7c.clearParameters();        // clear the parms and check player 2
                  pstmt7c.setString(1, nSlotParms.mNum3);
                  pstmt7c.setString(2, nSlotParms.mNum3);
                  pstmt7c.setString(3, nSlotParms.mNum3);
                  pstmt7c.setString(4, nSlotParms.mNum3);
                  pstmt7c.setString(5, nSlotParms.mNum3);
                  pstmt7c.setLong(6, nSlotParms.date);
                  pstmt7c.setInt(7, rest_etime);
                  pstmt7c.setInt(8, rest_stime);
                  rs7 = pstmt7c.executeQuery();      // execute the prepared stmt

                  while (rs7.next()) {

                     time2 = rs7.getInt("time");
                     t_fb = rs7.getInt("fb");
                     course2 = rs7.getString("courseName");
                     rmNum1 = rs7.getString("mNum1");
                     rmNum2 = rs7.getString("mNum2");
                     rmNum3 = rs7.getString("mNum3");
                     rmNum4 = rs7.getString("mNum4");
                     rmNum5 = rs7.getString("mNum5");

                     //
                     //  matching member number found in teecurr - check if course and f/b match
                     //
                     if (t_fb == 0) {                   // is Tee time for Front 9?

                        sfb2 = "Front";
                     }

                     if (t_fb == 1) {                   // is it Back 9?

                        sfb2 = "Back";
                     }

                     //
                     //  First make sure this is not this tee time before changes,
                     //  Then check if it matches the criteria for the restriction.
                     //
                     if ((time2 != nSlotParms.time) || (!course2.equals( nSlotParms.course ))) {  // either time or course is diff

                        if ((rest_fb.equals( "Both" ) || rest_fb.equals( sfb2 )) &&
                            (rest_course.equals( "-ALL-" ) || rest_course.equals( course2 ))) {

                           if (nSlotParms.mNum3.equals( rmNum1 )) {  // count number of players with this member number already scheduled
                              ind++;
                           }
                           if (nSlotParms.mNum3.equals( rmNum2 )) {
                              ind++;
                           }
                           if (nSlotParms.mNum3.equals( rmNum3 )) {
                              ind++;
                           }
                           if (nSlotParms.mNum3.equals( rmNum4 )) {
                              ind++;
                           }
                           if (nSlotParms.mNum3.equals( rmNum5 )) {
                              ind++;
                           }
                        }
                     }

                  } // end of while members

                  //
                  //  Now check if any other members in this tee time match
                  //
                  nSlotParms.pnum3 = nSlotParms.player3;  // save this player name for error msg

                  if (nSlotParms.mNum3.equals( nSlotParms.mNum1 )) {

                     ind++;
                     nSlotParms.pnum1 = nSlotParms.player1;  // match found for player - save for error msg
                  }
                  if (nSlotParms.mNum3.equals( nSlotParms.mNum2 )) {

                     ind++;
                     nSlotParms.pnum2 = nSlotParms.player2;  // match found for player - save for error msg
                  }
                  if (nSlotParms.mNum3.equals( nSlotParms.mNum4 )) {

                     ind++;
                     nSlotParms.pnum4 = nSlotParms.player4;  // match found for player - save for error msg
                  }
                  if (nSlotParms.mNum3.equals( nSlotParms.mNum5 )) {

                     ind++;
                     nSlotParms.pnum5 = nSlotParms.player5;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     if (!nSlotParms.player3.equals( nSlotParms.oldPlayer1 ) &&
                         !nSlotParms.player3.equals( nSlotParms.oldPlayer2 ) &&
                         !nSlotParms.player3.equals( nSlotParms.oldPlayer3 ) &&
                         !nSlotParms.player3.equals( nSlotParms.oldPlayer4 ) &&
                         !nSlotParms.player3.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                        error = true;      // max # exceeded - reject
                     }
                  }

                  pstmt7c.close();

               }  // end of member 3 restrictions if

               //
               //   Check Player 4
               //
               if ((error == false) && (!nSlotParms.mNum4.equals( "" ))) {           // if this player is a member

                  PreparedStatement pstmt7c = con.prepareStatement (
                     "SELECT time, fb, courseName, mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 " +
                     "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND date = ? " +
                     "AND time <= ? AND time >= ?");

                  ind = 0;                           // init fields
                  nSlotParms.pnum1 = "";
                  nSlotParms.pnum2 = "";
                  nSlotParms.pnum3 = "";
                  nSlotParms.pnum4 = "";
                  nSlotParms.pnum5 = "";

                  pstmt7c.clearParameters();        // clear the parms and check player 2
                  pstmt7c.setString(1, nSlotParms.mNum4);
                  pstmt7c.setString(2, nSlotParms.mNum4);
                  pstmt7c.setString(3, nSlotParms.mNum4);
                  pstmt7c.setString(4, nSlotParms.mNum4);
                  pstmt7c.setString(5, nSlotParms.mNum4);
                  pstmt7c.setLong(6, nSlotParms.date);
                  pstmt7c.setInt(7, rest_etime);
                  pstmt7c.setInt(8, rest_stime);
                  rs7 = pstmt7c.executeQuery();      // execute the prepared stmt

                  while (rs7.next()) {

                     time2 = rs7.getInt("time");
                     t_fb = rs7.getInt("fb");
                     course2 = rs7.getString("courseName");
                     rmNum1 = rs7.getString("mNum1");
                     rmNum2 = rs7.getString("mNum2");
                     rmNum3 = rs7.getString("mNum3");
                     rmNum4 = rs7.getString("mNum4");
                     rmNum5 = rs7.getString("mNum5");

                     //
                     //  matching member number found in teecurr - check if course and f/b match
                     //
                     if (t_fb == 0) {                   // is Tee time for Front 9?

                        sfb2 = "Front";
                     }

                     if (t_fb == 1) {                   // is it Back 9?

                        sfb2 = "Back";
                     }

                     //
                     //  First make sure this is not this tee time before changes,
                     //  Then check if it matches the criteria for the restriction.
                     //
                     if ((time2 != nSlotParms.time) || (!course2.equals( nSlotParms.course ))) {  // either time or course is diff

                        if ((rest_fb.equals( "Both" ) || rest_fb.equals( sfb2 )) &&
                            (rest_course.equals( "-ALL-" ) || rest_course.equals( course2 ))) {

                           if (nSlotParms.mNum4.equals( rmNum1 )) {  // count number of players with this member number already scheduled
                              ind++;
                           }
                           if (nSlotParms.mNum4.equals( rmNum2 )) {
                              ind++;
                           }
                           if (nSlotParms.mNum4.equals( rmNum3 )) {
                              ind++;
                           }
                           if (nSlotParms.mNum4.equals( rmNum4 )) {
                              ind++;
                           }
                           if (nSlotParms.mNum4.equals( rmNum5 )) {
                              ind++;
                           }
                        }
                     }

                  } // end of while members

                  //
                  //  Now check if any other members in this tee time match
                  //
                  nSlotParms.pnum4 = nSlotParms.player4;  // save this player name for error msg

                  if (nSlotParms.mNum4.equals( nSlotParms.mNum1 )) {

                     ind++;
                     nSlotParms.pnum1 = nSlotParms.player1;  // match found for player - save for error msg
                  }
                  if (nSlotParms.mNum4.equals( nSlotParms.mNum2 )) {

                     ind++;
                     nSlotParms.pnum2 = nSlotParms.player2;  // match found for player - save for error msg
                  }
                  if (nSlotParms.mNum4.equals( nSlotParms.mNum3 )) {

                     ind++;
                     nSlotParms.pnum3 = nSlotParms.player3;  // match found for player - save for error msg
                  }
                  if (nSlotParms.mNum4.equals( nSlotParms.mNum5 )) {

                     ind++;
                     nSlotParms.pnum5 = nSlotParms.player5;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     if (!nSlotParms.player4.equals( nSlotParms.oldPlayer1 ) &&
                         !nSlotParms.player4.equals( nSlotParms.oldPlayer2 ) &&
                         !nSlotParms.player4.equals( nSlotParms.oldPlayer3 ) &&
                         !nSlotParms.player4.equals( nSlotParms.oldPlayer4 ) &&
                         !nSlotParms.player4.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                        error = true;      // max # exceeded - reject
                     }
                  }

                  pstmt7c.close();

               }  // end of member 4 restrictions if

               //
               //   Check Player 5
               //
               if ((error == false) && (!nSlotParms.mNum5.equals( "" ))) {           // if this player is a member

                  PreparedStatement pstmt7c = con.prepareStatement (
                     "SELECT time, fb, courseName, mNum1, mNum2, mNum3, mNum4, mNum5 FROM teecurr2 " +
                     "WHERE (mNum1 = ? OR mNum2 = ? OR mNum3 = ? OR mNum4 = ? OR mNum5 = ?) AND date = ? " +
                     "AND time <= ? AND time >= ?");

                  ind = 0;                           // init fields
                  nSlotParms.pnum1 = "";
                  nSlotParms.pnum2 = "";
                  nSlotParms.pnum3 = "";
                  nSlotParms.pnum4 = "";
                  nSlotParms.pnum5 = "";

                  pstmt7c.clearParameters();        // clear the parms and check player 2
                  pstmt7c.setString(1, nSlotParms.mNum5);
                  pstmt7c.setString(2, nSlotParms.mNum5);
                  pstmt7c.setString(3, nSlotParms.mNum5);
                  pstmt7c.setString(4, nSlotParms.mNum5);
                  pstmt7c.setString(5, nSlotParms.mNum5);
                  pstmt7c.setLong(6, nSlotParms.date);
                  pstmt7c.setInt(7, rest_etime);
                  pstmt7c.setInt(8, rest_stime);
                  rs7 = pstmt7c.executeQuery();      // execute the prepared stmt

                  while (rs7.next()) {

                     time2 = rs7.getInt("time");
                     t_fb = rs7.getInt("fb");
                     course2 = rs7.getString("courseName");
                     rmNum1 = rs7.getString("mNum1");
                     rmNum2 = rs7.getString("mNum2");
                     rmNum3 = rs7.getString("mNum3");
                     rmNum4 = rs7.getString("mNum4");
                     rmNum5 = rs7.getString("mNum5");

                     //
                     //  matching member number found in teecurr - check if course and f/b match
                     //
                     if (t_fb == 0) {                   // is Tee time for Front 9?

                        sfb2 = "Front";
                     }

                     if (t_fb == 1) {                   // is it Back 9?

                        sfb2 = "Back";
                     }

                     //
                     //  First make sure this is not this tee time before changes,
                     //  Then check if it matches the criteria for the restriction.
                     //
                     if ((time2 != nSlotParms.time) || (!course2.equals( nSlotParms.course ))) {  // either time or course is diff

                        if ((rest_fb.equals( "Both" ) || rest_fb.equals( sfb2 )) &&
                            (rest_course.equals( "-ALL-" ) || rest_course.equals( course2 ))) {

                           if (nSlotParms.mNum5.equals( rmNum1 )) {  // count number of players with this member number already scheduled
                              ind++;
                           }
                           if (nSlotParms.mNum5.equals( rmNum2 )) {
                              ind++;
                           }
                           if (nSlotParms.mNum5.equals( rmNum3 )) {
                              ind++;
                           }
                           if (nSlotParms.mNum5.equals( rmNum4 )) {
                              ind++;
                           }
                           if (nSlotParms.mNum5.equals( rmNum5 )) {
                              ind++;
                           }
                        }
                     }

                  } // end of while members

                  //
                  //  Now check if any other members in this tee time match
                  //
                  nSlotParms.pnum5 = nSlotParms.player5;  // save this player name for error msg

                  if (nSlotParms.mNum5.equals( nSlotParms.mNum1 )) {

                     ind++;
                     nSlotParms.pnum1 = nSlotParms.player1;  // match found for player - save for error msg
                  }
                  if (nSlotParms.mNum5.equals( nSlotParms.mNum2 )) {

                     ind++;
                     nSlotParms.pnum2 = nSlotParms.player2;  // match found for player - save for error msg
                  }
                  if (nSlotParms.mNum5.equals( nSlotParms.mNum3 )) {

                     ind++;
                     nSlotParms.pnum3 = nSlotParms.player3;  // match found for player - save for error msg
                  }
                  if (nSlotParms.mNum5.equals( nSlotParms.mNum4 )) {

                     ind++;
                     nSlotParms.pnum4 = nSlotParms.player4;  // match found for player - save for error msg
                  }
                  //
                  //  Check if number of matches exceeds the max allowed
                  //
                  if (ind >= mems) {

                     if (!nSlotParms.player5.equals( nSlotParms.oldPlayer1 ) &&
                         !nSlotParms.player5.equals( nSlotParms.oldPlayer2 ) &&
                         !nSlotParms.player5.equals( nSlotParms.oldPlayer3 ) &&
                         !nSlotParms.player5.equals( nSlotParms.oldPlayer4 ) &&
                         !nSlotParms.player5.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                        error = true;      // max # exceeded - reject
                     }
                  }

                  pstmt7c.close();

               }  // end of member 5 restrictions if

               if (error == true ) {          // if restriction hit

                  break loop3;
               }
            }     // end of IF F/B matches
         }     // end of 'day' if
      }       // end of while (no more restrictions)

      pstmt7b.close();
       
   }
   catch (Exception e) {

      throw new Exception("Error checking Member Number Restrictions - verifyNSlot.checkMemNum " + e.getMessage());
   }

   return(error);

 }

 
/**
 //************************************************************************
 //
 //  Check member restrictions
 //
 //     First, find all restrictions within date & time constraints on this course.
 //     Then, find the ones for this day.
 //     Then, find any for this member type or membership type (all 5 players).
 //
 //
 //    **** Hazeltine Custom Processing *********
 //    Hazeltine will setup member restrictions for junior members.  However,
 //    if the junior(s) are playing with one or more parent, its ok.
 //
 //************************************************************************
 **/

 public static boolean checkMemRests(parmNSlot nSlotParms, Connection con)
         throws Exception {


   //Statement stmt = null;
   ResultSet rs = null;

   boolean error = false;
   //boolean ok = false;
   boolean jrOK = false;

   int ind = 0;
   int i = 0;
   int memLimit = Labels.MAX_MEMS;
   int mshipLimit = Labels.MAX_MSHIPS;

   String rest_fb = "";
   String rest_recurr = "";

   String [] mtypeA = new String [Labels.MAX_MEMS];     // array to hold the member type names

   String [] mshipA = new String [Labels.MAX_MSHIPS];   // array to hold the membership names


   try {

      PreparedStatement pstmt7 = con.prepareStatement (
         "SELECT * FROM restriction2 WHERE sdate <= ? AND edate >= ? AND " +
         "stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-')");


      pstmt7.clearParameters();          // clear the parms
      pstmt7.setLong(1, nSlotParms.date);
      pstmt7.setLong(2, nSlotParms.date);
      pstmt7.setInt(3, nSlotParms.time);
      pstmt7.setInt(4, nSlotParms.time);
      pstmt7.setString(5, nSlotParms.course);

      rs = pstmt7.executeQuery();      // find all matching restrictions, if any

      error = false;                     // init 'hit' flag

      if (nSlotParms.fb == 0) {                   // is Tee time for Front 9?

         nSlotParms.sfb = "Front";
      }

      if (nSlotParms.fb == 1) {                   // is it Back 9?

         nSlotParms.sfb = "Back";
      }

      loop2:
      while (rs.next()) {              // check all matching restrictions for this day, mship, mtype & F/B

         nSlotParms.rest_name = rs.getString("name");
         rest_recurr = rs.getString("recurr");
         for (i=0; i<memLimit; i++) {
            mtypeA[i] = rs.getString("mem" +(i+1));
         }
         for (i=0; i<mshipLimit; i++) {
            mshipA[i] = rs.getString("mship" +(i+1));
         }
         rest_fb = rs.getString("fb");

         //
         //  We must check the recurrence for this day (Monday, etc.)
         //
         if ((rest_recurr.equals( "Every " + nSlotParms.day )) ||               // if this day
             (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
             ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
               (!nSlotParms.day.equalsIgnoreCase( "saturday" )) &&
               (!nSlotParms.day.equalsIgnoreCase( "sunday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
              (nSlotParms.day.equalsIgnoreCase( "saturday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
              (nSlotParms.day.equalsIgnoreCase( "sunday" )))) {

            //
            //  Now check if F/B matches
            //
            if ((rest_fb.equals( "Both" )) || (rest_fb.equals( nSlotParms.sfb ))) {

               error = false;
               
               if (!verifySlot.checkRestSuspend(rs.getInt("id"), -99, (int)nSlotParms.date, nSlotParms.time, nSlotParms.day, nSlotParms.course, con)) {
                   
                   //
                   //  Found a restriction that matches date, time, day & F/B - check mtype & mship of each member player
                   //
                   if (!nSlotParms.mship1.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      nSlotParms.player = nSlotParms.player1;                  // save current player name

                      while (ind < memLimit) {

                         if ((nSlotParms.mship1.equalsIgnoreCase( mshipA[ind] )) || (nSlotParms.mtype1.equalsIgnoreCase( mtypeA[ind] ))) {

                            if (!nSlotParms.player1.equals( nSlotParms.oldPlayer1 ) &&
                                !nSlotParms.player1.equals( nSlotParms.oldPlayer2 ) &&
                                !nSlotParms.player1.equals( nSlotParms.oldPlayer3 ) &&
                                !nSlotParms.player1.equals( nSlotParms.oldPlayer4 ) &&
                                !nSlotParms.player1.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                                  error = true;          // match found - member is restricted
                                  break loop2;

                            }
                         }
                         ind++;
                      }
                   }  // end of member 1 restrictions if

                   if (!nSlotParms.mship2.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      nSlotParms.player = nSlotParms.player2;                  // save current player name

                      while (ind < memLimit) {

                         if ((nSlotParms.mship2.equalsIgnoreCase( mshipA[ind] )) || (nSlotParms.mtype2.equalsIgnoreCase( mtypeA[ind] ))) {

                            if (!nSlotParms.player2.equals( nSlotParms.oldPlayer1 ) &&
                                !nSlotParms.player2.equals( nSlotParms.oldPlayer2 ) &&
                                !nSlotParms.player2.equals( nSlotParms.oldPlayer3 ) &&
                                !nSlotParms.player2.equals( nSlotParms.oldPlayer4 ) &&
                                !nSlotParms.player2.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                                  error = true;          // match found - member is restricted
                                  break loop2;
                            }
                         }
                         ind++;
                      }
                   }  // end of member 2 restrictions if

                   if (!nSlotParms.mship3.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      nSlotParms.player = nSlotParms.player3;                  // save current player name

                      while (ind < memLimit) {

                         if ((nSlotParms.mship3.equalsIgnoreCase( mshipA[ind] )) || (nSlotParms.mtype3.equalsIgnoreCase( mtypeA[ind] ))) {

                            if (!nSlotParms.player3.equals( nSlotParms.oldPlayer1 ) &&
                                !nSlotParms.player3.equals( nSlotParms.oldPlayer2 ) &&
                                !nSlotParms.player3.equals( nSlotParms.oldPlayer3 ) &&
                                !nSlotParms.player3.equals( nSlotParms.oldPlayer4 ) &&
                                !nSlotParms.player3.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                                  error = true;          // match found - member is restricted
                                  break loop2;
                            }
                         }
                         ind++;
                      }
                   }  // end of member 3 restrictions if

                   if (!nSlotParms.mship4.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      nSlotParms.player = nSlotParms.player4;                  // save current player name

                      while (ind < memLimit) {

                         if ((nSlotParms.mship4.equalsIgnoreCase( mshipA[ind] )) || (nSlotParms.mtype4.equalsIgnoreCase( mtypeA[ind] ))) {

                            if (!nSlotParms.player4.equals( nSlotParms.oldPlayer1 ) &&
                                !nSlotParms.player4.equals( nSlotParms.oldPlayer2 ) &&
                                !nSlotParms.player4.equals( nSlotParms.oldPlayer3 ) &&
                                !nSlotParms.player4.equals( nSlotParms.oldPlayer4 ) &&
                                !nSlotParms.player4.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                                  error = true;          // match found - member is restricted
                                  break loop2;
                            }
                         }
                         ind++;
                      }
                   }  // end of member 4 restrictions if

                   if (!nSlotParms.mship5.equals( "" )) {           // if this player is a member

                      ind = 0;                           // init fields
                      nSlotParms.player = nSlotParms.player5;                  // save current player name

                      while (ind < memLimit) {

                         if ((nSlotParms.mship5.equalsIgnoreCase( mshipA[ind] )) || (nSlotParms.mtype5.equalsIgnoreCase( mtypeA[ind] ))) {

                            if (!nSlotParms.player5.equals( nSlotParms.oldPlayer1 ) &&
                                !nSlotParms.player5.equals( nSlotParms.oldPlayer2 ) &&
                                !nSlotParms.player5.equals( nSlotParms.oldPlayer3 ) &&
                                !nSlotParms.player5.equals( nSlotParms.oldPlayer4 ) &&
                                !nSlotParms.player5.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                                  error = true;          // match found - member is restricted
                                  break loop2;
                            }
                         }
                         ind++;
                      }
                   }  // end of member 5 restrictions if
               }
            }     // end of IF F/B matches
         }     // end of 'day' if
      }       // end of while (no more restrictions)

      pstmt7.close();
   }
   catch (Exception e) {
      throw new Exception("Error Checking Member Restrictions - verifyNSlot.checkMemRests " + e.getMessage());
   }

   return(error);
 }

 
/**
 //************************************************************************
 //
 //  Parse the names to separate first, last & mi
 //
 //      sets:  last name
 //             first name
 //             middle initial
 //
 //************************************************************************
 **/

 public static boolean parseNames(parmNSlot nSlotParms, String caller)
         throws Exception {


   boolean error = false;


   if ((!nSlotParms.player1.equals( "" )) && (nSlotParms.g1.equals( "" ))) {   // specified and not a guest

      StringTokenizer tok = new StringTokenizer( nSlotParms.player1 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         error = true;
         return(error);
      }

      if (caller.equalsIgnoreCase( "mem" )) {
        
         if (tok.countTokens() == 1 ) {    //  X or single names not allowed in player1

            error = true;
            return(error);
         }
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         nSlotParms.fname1 = tok.nextToken();
         nSlotParms.lname1 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         nSlotParms.fname1 = tok.nextToken();
         nSlotParms.mi1 = tok.nextToken();
         nSlotParms.lname1 = tok.nextToken();
      }
   }


   if ((!nSlotParms.player2.equals( "" )) && (nSlotParms.g2.equals( "" ))) {                  // specified but not guest

      StringTokenizer tok = new StringTokenizer( nSlotParms.player2 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         error = true;
         return(error);
      }

      if (caller.equalsIgnoreCase( "mem" )) {

         if ((tok.countTokens() == 1 ) && (!nSlotParms.player2.equalsIgnoreCase( "X"))) {    // if not X

            error = true;
            return(error);
         }
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         nSlotParms.fname2 = tok.nextToken();
         nSlotParms.lname2 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         nSlotParms.fname2 = tok.nextToken();
         nSlotParms.mi2 = tok.nextToken();
         nSlotParms.lname2 = tok.nextToken();
      }
   }

   if ((!nSlotParms.player3.equals( "" )) && (nSlotParms.g3.equals( "" ))) {                  // specified but not guest

      StringTokenizer tok = new StringTokenizer( nSlotParms.player3 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         error = true;
         return(error);
      }

      if (caller.equalsIgnoreCase( "mem" )) {

         if ((tok.countTokens() == 1 ) && (!nSlotParms.player3.equalsIgnoreCase( "X"))) {    // if not X

            error = true;
            return(error);
         }
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         nSlotParms.fname3 = tok.nextToken();
         nSlotParms.lname3 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         nSlotParms.fname3 = tok.nextToken();
         nSlotParms.mi3 = tok.nextToken();
         nSlotParms.lname3 = tok.nextToken();
      }
   }

   if ((!nSlotParms.player4.equals( "" )) && (nSlotParms.g4.equals( "" ))) {                  // specified but not guest

      StringTokenizer tok = new StringTokenizer( nSlotParms.player4 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         error = true;
         return(error);
      }

      if (caller.equalsIgnoreCase( "mem" )) {

         if ((tok.countTokens() == 1 ) && (!nSlotParms.player4.equalsIgnoreCase( "X"))) {    // if not X

            error = true;
            return(error);
         }
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         nSlotParms.fname4 = tok.nextToken();
         nSlotParms.lname4 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         nSlotParms.fname4 = tok.nextToken();
         nSlotParms.mi4 = tok.nextToken();
         nSlotParms.lname4 = tok.nextToken();
      }
   }

   if ((!nSlotParms.player5.equals( "" )) && (nSlotParms.g5.equals( "" ))) {                  // specified but not guest

      StringTokenizer tok = new StringTokenizer( nSlotParms.player5 );     // space is the default token

      if ( tok.countTokens() > 3 ) {          // too many name fields

         error = true;
         return(error);
      }

      if (caller.equalsIgnoreCase( "mem" )) {

         if ((tok.countTokens() == 1 ) && (!nSlotParms.player5.equalsIgnoreCase( "X"))) {    // if not X

            error = true;
            return(error);
         }
      }

      if ( tok.countTokens() == 2 ) {         // first name, last name

         nSlotParms.fname5 = tok.nextToken();
         nSlotParms.lname5 = tok.nextToken();
      }

      if ( tok.countTokens() == 3 ) {         // first name, mi, last name

         nSlotParms.fname5 = tok.nextToken();
         nSlotParms.mi5 = tok.nextToken();
         nSlotParms.lname5 = tok.nextToken();
      }
   }

   return(error);

 }


/**
 //************************************************************************
 //
 //  checkMaxRounds - checks members for max rounds per week, month or year
 //                   based on their membership type.
 //
 //************************************************************************
 **/

 public static boolean checkMaxRounds(parmNSlot nSlotParms, Connection con)
         throws Exception {


   //Statement stmt = null;
   ResultSet rs = null;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con);   // since this is a notification site we can hard code a zero for the root_activity_id

   int year = 0;
   int month = 0;
   int dayNum = 0;
   int count = 0;
   int mtimes = 0;
   int ind = 0;
   int i = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;

   long dateStart = 0;
   long dateEnd = 0;

   String mperiod = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";

   String [] mshipA = new String [parm.MAX_Mships+1];     // array to hold the membership names
   int [] mtimesA = new int [parm.MAX_Mships+1];          // array to hold the mship max # of rounds value
   String [] periodA = new String [parm.MAX_Mships+1];    // array to hold the mship periods (week, month, year)

   boolean error = false;

   //
   //  init the string arrays
   //
   for (i=0; i<parm.MAX_Mships+1; i++) {
      mshipA[i] = "";
      mtimesA[i] = 0;
      periodA[i] = "";
   }

   try {

      //
      //  Get this date's calendar and then determine start and end of week.
      //
      int calmm = nSlotParms.mm -1;                    // adjust month value for cal

      Calendar cal = new GregorianCalendar();         // get todays date

      //
      //  set cal to tee time's date
      //
      cal.set(Calendar.YEAR,nSlotParms.yy);                    // set year in cal
      cal.set(Calendar.MONTH,calmm);                          // set month in cal
      cal.set(Calendar.DAY_OF_MONTH,nSlotParms.dd);            // set day in cal

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
      Statement stmt1 = null;
      
      stmt1 = con.createStatement();

      rs = stmt1.executeQuery("SELECT mship, mtimes, period FROM mship5 WHERE activity_id = 0 LIMIT " + nSlotParms.MAX_Mships);

      i = 1;

      while (rs.next()) {

          mshipA[i] = rs.getString("mship");
          mtimesA[i] = rs.getInt("mtimes");
          periodA[i] = rs.getString("period");

          i++;
      }

      stmt1.close();

      //
      // statements for week
      //
      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT dd FROM teecurr2 WHERE date != ? AND date >= ? AND date <= ? AND " +
                    "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT username1, username2, username3, username4, show1, show2, show3, show4, username5, show5 " +
         "FROM teepast2 WHERE date != ? AND date >= ? AND date <= ? AND " +
                    "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");
      //
      // statements for month
      //
      PreparedStatement pstmt2m = con.prepareStatement (
         "SELECT dd FROM teecurr2 WHERE date != ? AND mm = ? AND yy = ? AND " +
                    "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

      PreparedStatement pstmt3m = con.prepareStatement (
         "SELECT username1, username2, username3, username4, show1, show2, show3, show4, username5, show5 " +
         "FROM teepast2 WHERE date != ? AND mm = ? AND yy = ? AND " +
                    "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");
      //
      // statements for year
      //
      PreparedStatement pstmt2y = con.prepareStatement (
         "SELECT dd FROM teecurr2 WHERE date != ? AND yy = ? AND " +
                    "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

      PreparedStatement pstmt3y = con.prepareStatement (
         "SELECT username1, username2, username3, username4, show1, show2, show3, show4, username5, show5 " +
         "FROM teepast2 WHERE date != ? AND yy = ? AND " +
                    "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

      if (!nSlotParms.mship1.equals( "" )) {          // check if player 1 name specified

         ind = 1;             // init fields
         count = 0;
         mtimes = 0;
         mperiod = "";

         loop1:
         while (ind < parm.MAX_Mships+1) {

            if (nSlotParms.mship1.equals( mshipA[ind] )) {

               mtimes = mtimesA[ind];            // match found - get number of rounds
               mperiod = periodA[ind];           //               and period (week, month, year)
               break loop1;
            }
            ind++;
         }

         if (mtimes != 0) {             // if match found for this player and there is a limit

            if (mperiod.equals( "Week" )) {       // if WEEK

               pstmt2.clearParameters();        // get count from teecurr
               pstmt2.setLong(1, nSlotParms.date);
               pstmt2.setLong(2, dateStart);
               pstmt2.setLong(3, dateEnd);
               pstmt2.setString(4, nSlotParms.user1);
               pstmt2.setString(5, nSlotParms.user1);
               pstmt2.setString(6, nSlotParms.user1);
               pstmt2.setString(7, nSlotParms.user1);
               pstmt2.setString(8, nSlotParms.user1);
               rs = pstmt2.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this week
               }

               pstmt3.clearParameters();        // get count from teepast
               pstmt3.setLong(1, nSlotParms.date);
               pstmt3.setLong(2, dateStart);
               pstmt3.setLong(3, dateEnd);
               pstmt3.setString(4, nSlotParms.user1);
               pstmt3.setString(5, nSlotParms.user1);
               pstmt3.setString(6, nSlotParms.user1);
               pstmt3.setString(7, nSlotParms.user1);
               pstmt3.setString(8, nSlotParms.user1);
               rs = pstmt3.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( nSlotParms.user1 ) && show1 == 1) {

                     count++;                      // count number or tee times in this week

                  } else {

                     if (user2.equalsIgnoreCase( nSlotParms.user1 ) && show2 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user3.equalsIgnoreCase( nSlotParms.user1 ) && show3 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user4.equalsIgnoreCase( nSlotParms.user1 ) && show4 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user5.equalsIgnoreCase( nSlotParms.user1 ) && show5 == 1) {

                                 count++;                      // count number or tee times in this week
                              }
                           }
                        }
                     }
                  }
               }
            }       // end of IF mperiod = week

            if (mperiod.equals( "Month" )) {      // if MONTH

               pstmt2m.clearParameters();        // get count from teecurr
               pstmt2m.setLong(1, nSlotParms.date);
               pstmt2m.setInt(2, nSlotParms.mm);
               pstmt2m.setInt(3, nSlotParms.yy);
               pstmt2m.setString(4, nSlotParms.user1);
               pstmt2m.setString(5, nSlotParms.user1);
               pstmt2m.setString(6, nSlotParms.user1);
               pstmt2m.setString(7, nSlotParms.user1);
               pstmt2m.setString(8, nSlotParms.user1);
               rs = pstmt2m.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this month
               }

               pstmt3m.clearParameters();        // get count from teepast
               pstmt3m.setLong(1, nSlotParms.date);
               pstmt3m.setInt(2, nSlotParms.mm);
               pstmt3m.setInt(3, nSlotParms.yy);
               pstmt3m.setString(4, nSlotParms.user1);
               pstmt3m.setString(5, nSlotParms.user1);
               pstmt3m.setString(6, nSlotParms.user1);
               pstmt3m.setString(7, nSlotParms.user1);
               pstmt3m.setString(8, nSlotParms.user1);
               rs = pstmt3m.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( nSlotParms.user1 ) && show1 == 1) {

                     count++;                      // count number or tee times in this week

                  } else {

                     if (user2.equalsIgnoreCase( nSlotParms.user1 ) && show2 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user3.equalsIgnoreCase( nSlotParms.user1 ) && show3 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user4.equalsIgnoreCase( nSlotParms.user1 ) && show4 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user5.equalsIgnoreCase( nSlotParms.user1 ) && show5 == 1) {

                                 count++;                      // count number or tee times in this week
                              }
                           }
                        }
                     }
                  }
               }
            }       // end of IF mperiod = Month

            if (mperiod.equals( "Year" )) {            // if Year

               pstmt2y.clearParameters();             // get count from teecurr
               pstmt2y.setLong(1, nSlotParms.date);
               pstmt2y.setInt(2, nSlotParms.yy);
               pstmt2y.setString(3, nSlotParms.user1);
               pstmt2y.setString(4, nSlotParms.user1);
               pstmt2y.setString(5, nSlotParms.user1);
               pstmt2y.setString(6, nSlotParms.user1);
               pstmt2y.setString(7, nSlotParms.user1);
               rs = pstmt2y.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this year
               }

               pstmt3y.clearParameters();        // get count from teepast
               pstmt3y.setLong(1, nSlotParms.date);
               pstmt3y.setInt(2, nSlotParms.yy);
               pstmt3y.setString(3, nSlotParms.user1);
               pstmt3y.setString(4, nSlotParms.user1);
               pstmt3y.setString(5, nSlotParms.user1);
               pstmt3y.setString(6, nSlotParms.user1);
               pstmt3y.setString(7, nSlotParms.user1);
               rs = pstmt3y.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( nSlotParms.user1 ) && show1 == 1) {

                     count++;                      // count number or tee times in this week

                  } else {

                     if (user2.equalsIgnoreCase( nSlotParms.user1 ) && show2 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user3.equalsIgnoreCase( nSlotParms.user1 ) && show3 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user4.equalsIgnoreCase( nSlotParms.user1 ) && show4 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user5.equalsIgnoreCase( nSlotParms.user1 ) && show5 == 1) {

                                 count++;                      // count number or tee times in this week
                              }
                           }
                        }
                     }
                  }
               }
            }       // end of IF mperiod = Year

            //
            //  Compare # of tee times in this period with max allowed for membership type
            //
            if (count >= mtimes)  {

               if (!nSlotParms.player1.equals( nSlotParms.oldPlayer1 ) &&
                   !nSlotParms.player1.equals( nSlotParms.oldPlayer2 ) &&
                   !nSlotParms.player1.equals( nSlotParms.oldPlayer3 ) &&
                   !nSlotParms.player1.equals( nSlotParms.oldPlayer4 ) &&
                   !nSlotParms.player1.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                  error = true;                // reject this member
                  nSlotParms.mship = nSlotParms.mship1;
                  nSlotParms.player = nSlotParms.player1;
                  nSlotParms.period = mperiod;
               }
            }
         }          // end of IF match found for player
      }          // end of player 1 if

      if (!nSlotParms.mship2.equals( "" )) {          // check if player 2 name specified

         ind = 1;             // init fields
         count = 0;
         mtimes = 0;
         mperiod = "";

         loop2:
         while (ind < parm.MAX_Mships+1) {

            if (nSlotParms.mship2.equals( mshipA[ind] )) {

               mtimes = mtimesA[ind];            // match found - get number of rounds
               mperiod = periodA[ind];           //               and period (week, month, year)
               break loop2;
            }
            ind++;
         }

         if (mtimes != 0) {             // if match found for this player and there is a limit

            if (mperiod.equals( "Week" )) {       // if WEEK

               pstmt2.clearParameters();        // get count from teecurr
               pstmt2.setLong(1, nSlotParms.date);
               pstmt2.setLong(2, dateStart);
               pstmt2.setLong(3, dateEnd);
               pstmt2.setString(4, nSlotParms.user2);
               pstmt2.setString(5, nSlotParms.user2);
               pstmt2.setString(6, nSlotParms.user2);
               pstmt2.setString(7, nSlotParms.user2);
               pstmt2.setString(8, nSlotParms.user2);
               rs = pstmt2.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this week
               }

               pstmt3.clearParameters();        // get count from teepast
               pstmt3.setLong(1, nSlotParms.date);
               pstmt3.setLong(2, dateStart);
               pstmt3.setLong(3, dateEnd);
               pstmt3.setString(4, nSlotParms.user2);
               pstmt3.setString(5, nSlotParms.user2);
               pstmt3.setString(6, nSlotParms.user2);
               pstmt3.setString(7, nSlotParms.user2);
               pstmt3.setString(8, nSlotParms.user2);
               rs = pstmt3.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( nSlotParms.user2 ) && show1 == 1) {

                     count++;                      // count number or tee times in this week

                  } else {

                     if (user2.equalsIgnoreCase( nSlotParms.user2 ) && show2 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user3.equalsIgnoreCase( nSlotParms.user2 ) && show3 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user4.equalsIgnoreCase( nSlotParms.user2 ) && show4 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user5.equalsIgnoreCase( nSlotParms.user2 ) && show5 == 1) {

                                 count++;                      // count number or tee times in this week
                              }
                           }
                        }
                     }
                  }
               }
            }       // end of IF mperiod = week

            if (mperiod.equals( "Month" )) {      // if MONTH

               pstmt2m.clearParameters();        // get count from teecurr
               pstmt2m.setLong(1, nSlotParms.date);
               pstmt2m.setInt(2, nSlotParms.mm);
               pstmt2m.setInt(3, nSlotParms.yy);
               pstmt2m.setString(4, nSlotParms.user2);
               pstmt2m.setString(5, nSlotParms.user2);
               pstmt2m.setString(6, nSlotParms.user2);
               pstmt2m.setString(7, nSlotParms.user2);
               pstmt2m.setString(8, nSlotParms.user2);
               rs = pstmt2m.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this month
               }

               pstmt3m.clearParameters();        // get count from teepast
               pstmt3m.setLong(1, nSlotParms.date);
               pstmt3m.setInt(2, nSlotParms.mm);
               pstmt3m.setInt(3, nSlotParms.yy);
               pstmt3m.setString(4, nSlotParms.user2);
               pstmt3m.setString(5, nSlotParms.user2);
               pstmt3m.setString(6, nSlotParms.user2);
               pstmt3m.setString(7, nSlotParms.user2);
               pstmt3m.setString(8, nSlotParms.user2);
               rs = pstmt3m.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( nSlotParms.user2 ) && show1 == 1) {

                     count++;                      // count number or tee times in this week

                  } else {

                     if (user2.equalsIgnoreCase( nSlotParms.user2 ) && show2 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user3.equalsIgnoreCase( nSlotParms.user2 ) && show3 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user4.equalsIgnoreCase( nSlotParms.user2 ) && show4 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user5.equalsIgnoreCase( nSlotParms.user2 ) && show5 == 1) {

                                 count++;                      // count number or tee times in this week
                              }
                           }
                        }
                     }
                  }
               }
            }       // end of IF mperiod = Month

            if (mperiod.equals( "Year" )) {            // if Year

               pstmt2y.clearParameters();             // get count from teecurr
               pstmt2y.setLong(1, nSlotParms.date);
               pstmt2y.setInt(2, nSlotParms.yy);
               pstmt2y.setString(3, nSlotParms.user2);
               pstmt2y.setString(4, nSlotParms.user2);
               pstmt2y.setString(5, nSlotParms.user2);
               pstmt2y.setString(6, nSlotParms.user2);
               pstmt2y.setString(7, nSlotParms.user2);
               rs = pstmt2y.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this year
               }

               pstmt3y.clearParameters();        // get count from teepast
               pstmt3y.setLong(1, nSlotParms.date);
               pstmt3y.setInt(2, nSlotParms.yy);
               pstmt3y.setString(3, nSlotParms.user2);
               pstmt3y.setString(4, nSlotParms.user2);
               pstmt3y.setString(5, nSlotParms.user2);
               pstmt3y.setString(6, nSlotParms.user2);
               pstmt3y.setString(7, nSlotParms.user2);
               rs = pstmt3y.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( nSlotParms.user2 ) && show1 == 1) {

                     count++;                      // count number or tee times in this week

                  } else {

                     if (user2.equalsIgnoreCase( nSlotParms.user2 ) && show2 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user3.equalsIgnoreCase( nSlotParms.user2 ) && show3 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user4.equalsIgnoreCase( nSlotParms.user2 ) && show4 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user5.equalsIgnoreCase( nSlotParms.user2 ) && show5 == 1) {

                                 count++;                      // count number or tee times in this week
                              }
                           }
                        }
                     }
                  }
               }
            }       // end of IF mperiod = Year

            //
            //  Compare # of tee times in this period with max allowed for membership type
            //
            if (count >= mtimes)  {

               if (!nSlotParms.player2.equals( nSlotParms.oldPlayer1 ) &&
                   !nSlotParms.player2.equals( nSlotParms.oldPlayer2 ) &&
                   !nSlotParms.player2.equals( nSlotParms.oldPlayer3 ) &&
                   !nSlotParms.player2.equals( nSlotParms.oldPlayer4 ) &&
                   !nSlotParms.player2.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                  error = true;                // reject this member
                  nSlotParms.mship = nSlotParms.mship2;
                  nSlotParms.player = nSlotParms.player2;
                  nSlotParms.period = mperiod;
               }
            }
         }          // end of IF match found for player
      }          // end of player 2 if

      if (!nSlotParms.mship3.equals( "" )) {          // check if player 3 name specified

         ind = 1;             // init fields
         count = 0;
         mtimes = 0;
         mperiod = "";

         loop3:
         while (ind < parm.MAX_Mships+1) {

            if (nSlotParms.mship3.equals( mshipA[ind] )) {

               mtimes = mtimesA[ind];            // match found - get number of rounds
               mperiod = periodA[ind];           //               and period (week, month, year)
               break loop3;
            }
            ind++;
         }

         if (mtimes != 0) {             // if match found for this player and there is no limit

            if (mperiod.equals( "Week" )) {       // if WEEK

               pstmt2.clearParameters();        // get count from teecurr
               pstmt2.setLong(1, nSlotParms.date);
               pstmt2.setLong(2, dateStart);
               pstmt2.setLong(3, dateEnd);
               pstmt2.setString(4, nSlotParms.user3);
               pstmt2.setString(5, nSlotParms.user3);
               pstmt2.setString(6, nSlotParms.user3);
               pstmt2.setString(7, nSlotParms.user3);
               pstmt2.setString(8, nSlotParms.user3);
               rs = pstmt2.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this week
               }

               pstmt3.clearParameters();        // get count from teepast
               pstmt3.setLong(1, nSlotParms.date);
               pstmt3.setLong(2, dateStart);
               pstmt3.setLong(3, dateEnd);
               pstmt3.setString(4, nSlotParms.user3);
               pstmt3.setString(5, nSlotParms.user3);
               pstmt3.setString(6, nSlotParms.user3);
               pstmt3.setString(7, nSlotParms.user3);
               pstmt3.setString(8, nSlotParms.user3);
               rs = pstmt3.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( nSlotParms.user3 ) && show1 == 1) {

                     count++;                      // count number or tee times in this week

                  } else {

                     if (user2.equalsIgnoreCase( nSlotParms.user3 ) && show2 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user3.equalsIgnoreCase( nSlotParms.user3 ) && show3 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user4.equalsIgnoreCase( nSlotParms.user3 ) && show4 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user5.equalsIgnoreCase( nSlotParms.user3 ) && show5 == 1) {

                                 count++;                      // count number or tee times in this week
                              }
                           }
                        }
                     }
                  }
               }
            }       // end of IF mperiod = week

            if (mperiod.equals( "Month" )) {      // if MONTH

               pstmt2m.clearParameters();        // get count from teecurr
               pstmt2m.setLong(1, nSlotParms.date);
               pstmt2m.setInt(2, nSlotParms.mm);
               pstmt2m.setInt(3, nSlotParms.yy);
               pstmt2m.setString(4, nSlotParms.user3);
               pstmt2m.setString(5, nSlotParms.user3);
               pstmt2m.setString(6, nSlotParms.user3);
               pstmt2m.setString(7, nSlotParms.user3);
               pstmt2m.setString(8, nSlotParms.user3);
               rs = pstmt2m.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this month
               }

               pstmt3m.clearParameters();        // get count from teepast
               pstmt3m.setLong(1, nSlotParms.date);
               pstmt3m.setInt(2, nSlotParms.mm);
               pstmt3m.setInt(3, nSlotParms.yy);
               pstmt3m.setString(4, nSlotParms.user3);
               pstmt3m.setString(5, nSlotParms.user3);
               pstmt3m.setString(6, nSlotParms.user3);
               pstmt3m.setString(7, nSlotParms.user3);
               pstmt3m.setString(8, nSlotParms.user3);
               rs = pstmt3m.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( nSlotParms.user3 ) && show1 == 1) {

                     count++;                      // count number or tee times in this week

                  } else {

                     if (user2.equalsIgnoreCase( nSlotParms.user3 ) && show2 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user3.equalsIgnoreCase( nSlotParms.user3 ) && show3 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user4.equalsIgnoreCase( nSlotParms.user3 ) && show4 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user5.equalsIgnoreCase( nSlotParms.user3 ) && show5 == 1) {

                                 count++;                      // count number or tee times in this week
                              }
                           }
                        }
                     }
                  }
               }
            }       // end of IF mperiod = Month

            if (mperiod.equals( "Year" )) {            // if Year

               pstmt2y.clearParameters();             // get count from teecurr
               pstmt2y.setLong(1, nSlotParms.date);
               pstmt2y.setInt(2, nSlotParms.yy);
               pstmt2y.setString(3, nSlotParms.user3);
               pstmt2y.setString(4, nSlotParms.user3);
               pstmt2y.setString(5, nSlotParms.user3);
               pstmt2y.setString(6, nSlotParms.user3);
               pstmt2y.setString(7, nSlotParms.user3);
               rs = pstmt2y.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this year
               }

               pstmt3y.clearParameters();        // get count from teepast
               pstmt3y.setLong(1, nSlotParms.date);
               pstmt3y.setInt(2, nSlotParms.yy);
               pstmt3y.setString(3, nSlotParms.user3);
               pstmt3y.setString(4, nSlotParms.user3);
               pstmt3y.setString(5, nSlotParms.user3);
               pstmt3y.setString(6, nSlotParms.user3);
               pstmt3y.setString(7, nSlotParms.user3);
               rs = pstmt3y.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( nSlotParms.user3 ) && show1 == 1) {

                     count++;                      // count number or tee times in this week

                  } else {

                     if (user2.equalsIgnoreCase( nSlotParms.user3 ) && show2 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user3.equalsIgnoreCase( nSlotParms.user3 ) && show3 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user4.equalsIgnoreCase( nSlotParms.user3 ) && show4 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user5.equalsIgnoreCase( nSlotParms.user3 ) && show5 == 1) {

                                 count++;                      // count number or tee times in this week
                              }
                           }
                        }
                     }
                  }
               }
            }       // end of IF mperiod = Year

            //
            //  Compare # of tee times in this period with max allowed for membership type
            //
            if (count >= mtimes)  {

               if (!nSlotParms.player3.equals( nSlotParms.oldPlayer1 ) &&
                   !nSlotParms.player3.equals( nSlotParms.oldPlayer2 ) &&
                   !nSlotParms.player3.equals( nSlotParms.oldPlayer3 ) &&
                   !nSlotParms.player3.equals( nSlotParms.oldPlayer4 ) &&
                   !nSlotParms.player3.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                  error = true;                // reject this member
                  nSlotParms.mship = nSlotParms.mship3;
                  nSlotParms.player = nSlotParms.player3;
                  nSlotParms.period = mperiod;
               }
            }
         }          // end of IF match found for player
      }          // end of player 3 if

      if (!nSlotParms.mship4.equals( "" )) {          // check if player 4 name specified

         ind = 1;             // init fields
         count = 0;
         mtimes = 0;
         mperiod = "";

         loop4:
         while (ind < parm.MAX_Mships+1) {

            if (nSlotParms.mship4.equals( mshipA[ind] )) {

               mtimes = mtimesA[ind];            // match found - get number of rounds
               mperiod = periodA[ind];           //               and period (week, month, year)
               break loop4;
            }
            ind++;
         }

         if (mtimes != 0) {             // if match found for this player and there is no limit

            if (mperiod.equals( "Week" )) {       // if WEEK

               pstmt2.clearParameters();        // get count from teecurr
               pstmt2.setLong(1, nSlotParms.date);
               pstmt2.setLong(2, dateStart);
               pstmt2.setLong(3, dateEnd);
               pstmt2.setString(4, nSlotParms.user4);
               pstmt2.setString(5, nSlotParms.user4);
               pstmt2.setString(6, nSlotParms.user4);
               pstmt2.setString(7, nSlotParms.user4);
               pstmt2.setString(8, nSlotParms.user4);
               rs = pstmt2.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this week
               }

               pstmt3.clearParameters();        // get count from teepast
               pstmt3.setLong(1, nSlotParms.date);
               pstmt3.setLong(2, dateStart);
               pstmt3.setLong(3, dateEnd);
               pstmt3.setString(4, nSlotParms.user4);
               pstmt3.setString(5, nSlotParms.user4);
               pstmt3.setString(6, nSlotParms.user4);
               pstmt3.setString(7, nSlotParms.user4);
               pstmt3.setString(8, nSlotParms.user4);
               rs = pstmt3.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( nSlotParms.user4 ) && show1 == 1) {

                     count++;                      // count number or tee times in this week

                  } else {

                     if (user2.equalsIgnoreCase( nSlotParms.user4 ) && show2 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user3.equalsIgnoreCase( nSlotParms.user4 ) && show3 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user4.equalsIgnoreCase( nSlotParms.user4 ) && show4 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user5.equalsIgnoreCase( nSlotParms.user4 ) && show5 == 1) {

                                 count++;                      // count number or tee times in this week
                              }
                           }
                        }
                     }
                  }
               }
            }       // end of IF mperiod = week

            if (mperiod.equals( "Month" )) {      // if MONTH

               pstmt2m.clearParameters();        // get count from teecurr
               pstmt2m.setLong(1, nSlotParms.date);
               pstmt2m.setInt(2, nSlotParms.mm);
               pstmt2m.setInt(3, nSlotParms.yy);
               pstmt2m.setString(4, nSlotParms.user4);
               pstmt2m.setString(5, nSlotParms.user4);
               pstmt2m.setString(6, nSlotParms.user4);
               pstmt2m.setString(7, nSlotParms.user4);
               pstmt2m.setString(8, nSlotParms.user4);
               rs = pstmt2m.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this month
               }

               pstmt3m.clearParameters();        // get count from teepast
               pstmt3m.setLong(1, nSlotParms.date);
               pstmt3m.setInt(2, nSlotParms.mm);
               pstmt3m.setInt(3, nSlotParms.yy);
               pstmt3m.setString(4, nSlotParms.user4);
               pstmt3m.setString(5, nSlotParms.user4);
               pstmt3m.setString(6, nSlotParms.user4);
               pstmt3m.setString(7, nSlotParms.user4);
               pstmt3m.setString(8, nSlotParms.user4);
               rs = pstmt3m.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( nSlotParms.user4 ) && show1 == 1) {

                     count++;                      // count number or tee times in this week

                  } else {

                     if (user2.equalsIgnoreCase( nSlotParms.user4 ) && show2 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user3.equalsIgnoreCase( nSlotParms.user4 ) && show3 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user4.equalsIgnoreCase( nSlotParms.user4 ) && show4 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user5.equalsIgnoreCase( nSlotParms.user4 ) && show5 == 1) {

                                 count++;                      // count number or tee times in this week
                              }
                           }
                        }
                     }
                  }
               }
            }       // end of IF mperiod = Month

            if (mperiod.equals( "Year" )) {            // if Year

               pstmt2y.clearParameters();             // get count from teecurr
               pstmt2y.setLong(1, nSlotParms.date);
               pstmt2y.setInt(2, nSlotParms.yy);
               pstmt2y.setString(3, nSlotParms.user4);
               pstmt2y.setString(4, nSlotParms.user4);
               pstmt2y.setString(5, nSlotParms.user4);
               pstmt2y.setString(6, nSlotParms.user4);
               pstmt2y.setString(7, nSlotParms.user4);
               rs = pstmt2y.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this year
               }

               pstmt3y.clearParameters();        // get count from teepast
               pstmt3y.setLong(1, nSlotParms.date);
               pstmt3y.setInt(2, nSlotParms.yy);
               pstmt3y.setString(3, nSlotParms.user4);
               pstmt3y.setString(4, nSlotParms.user4);
               pstmt3y.setString(5, nSlotParms.user4);
               pstmt3y.setString(6, nSlotParms.user4);
               pstmt3y.setString(7, nSlotParms.user4);
               rs = pstmt3y.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( nSlotParms.user4 ) && show1 == 1) {

                     count++;                      // count number or tee times in this week

                  } else {

                     if (user2.equalsIgnoreCase( nSlotParms.user4 ) && show2 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user3.equalsIgnoreCase( nSlotParms.user4 ) && show3 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user4.equalsIgnoreCase( nSlotParms.user4 ) && show4 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user5.equalsIgnoreCase( nSlotParms.user4 ) && show5 == 1) {

                                 count++;                      // count number or tee times in this week
                              }
                           }
                        }
                     }
                  }
               }
            }       // end of IF mperiod = Year

            //
            //  Compare # of tee times in this period with max allowed for membership type
            //
            if (count >= mtimes)  {

               if (!nSlotParms.player4.equals( nSlotParms.oldPlayer1 ) &&
                   !nSlotParms.player4.equals( nSlotParms.oldPlayer2 ) &&
                   !nSlotParms.player4.equals( nSlotParms.oldPlayer3 ) &&
                   !nSlotParms.player4.equals( nSlotParms.oldPlayer4 ) &&
                   !nSlotParms.player4.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                  error = true;                // reject this member
                  nSlotParms.mship = nSlotParms.mship4;
                  nSlotParms.player = nSlotParms.player4;
                  nSlotParms.period = mperiod;
               }
            }
         }          // end of IF match found for player
      }          // end of player 4 if

      if (!nSlotParms.mship5.equals( "" )) {          // check if player 5 name specified

         ind = 1;             // init fields
         count = 0;
         mtimes = 0;
         mperiod = "";

         loop5:
         while (ind < parm.MAX_Mships+1) {

            if (nSlotParms.mship5.equals( mshipA[ind] )) {

               mtimes = mtimesA[ind];            // match found - get number of rounds
               mperiod = periodA[ind];           //               and period (week, month, year)
               break loop5;
            }
            ind++;
         }

         if (mtimes != 0) {             // if match found for this player and there is no limit

            if (mperiod.equals( "Week" )) {       // if WEEK

               pstmt2.clearParameters();        // get count from teecurr
               pstmt2.setLong(1, nSlotParms.date);
               pstmt2.setLong(2, dateStart);
               pstmt2.setLong(3, dateEnd);
               pstmt2.setString(4, nSlotParms.user5);
               pstmt2.setString(5, nSlotParms.user5);
               pstmt2.setString(6, nSlotParms.user5);
               pstmt2.setString(7, nSlotParms.user5);
               pstmt2.setString(8, nSlotParms.user5);
               rs = pstmt2.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this week
               }

               pstmt3.clearParameters();        // get count from teepast
               pstmt3.setLong(1, nSlotParms.date);
               pstmt3.setLong(2, dateStart);
               pstmt3.setLong(3, dateEnd);
               pstmt3.setString(4, nSlotParms.user5);
               pstmt3.setString(5, nSlotParms.user5);
               pstmt3.setString(6, nSlotParms.user5);
               pstmt3.setString(7, nSlotParms.user5);
               pstmt3.setString(8, nSlotParms.user5);
               rs = pstmt3.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( nSlotParms.user5 ) && show1 == 1) {

                     count++;                      // count number or tee times in this week

                  } else {

                     if (user2.equalsIgnoreCase( nSlotParms.user5 ) && show2 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user3.equalsIgnoreCase( nSlotParms.user5 ) && show3 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user4.equalsIgnoreCase( nSlotParms.user5 ) && show4 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user5.equalsIgnoreCase( nSlotParms.user5 ) && show5 == 1) {

                                 count++;                      // count number or tee times in this week
                              }
                           }
                        }
                     }
                  }
               }
            }       // end of IF mperiod = week

            if (mperiod.equals( "Month" )) {      // if MONTH

               pstmt2m.clearParameters();        // get count from teecurr
               pstmt2m.setLong(1, nSlotParms.date);
               pstmt2m.setInt(2, nSlotParms.mm);
               pstmt2m.setInt(3, nSlotParms.yy);
               pstmt2m.setString(4, nSlotParms.user5);
               pstmt2m.setString(5, nSlotParms.user5);
               pstmt2m.setString(6, nSlotParms.user5);
               pstmt2m.setString(7, nSlotParms.user5);
               pstmt2m.setString(8, nSlotParms.user5);
               rs = pstmt2m.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this month
               }

               pstmt3m.clearParameters();        // get count from teepast
               pstmt3m.setLong(1, nSlotParms.date);
               pstmt3m.setInt(2, nSlotParms.mm);
               pstmt3m.setInt(3, nSlotParms.yy);
               pstmt3m.setString(4, nSlotParms.user5);
               pstmt3m.setString(5, nSlotParms.user5);
               pstmt3m.setString(6, nSlotParms.user5);
               pstmt3m.setString(7, nSlotParms.user5);
               pstmt3m.setString(8, nSlotParms.user5);
               rs = pstmt3m.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( nSlotParms.user5 ) && show1 == 1) {

                     count++;                      // count number or tee times in this week

                  } else {

                     if (user2.equalsIgnoreCase( nSlotParms.user5 ) && show2 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user3.equalsIgnoreCase( nSlotParms.user5 ) && show3 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user4.equalsIgnoreCase( nSlotParms.user5 ) && show4 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user5.equalsIgnoreCase( nSlotParms.user5 ) && show5 == 1) {

                                 count++;                      // count number or tee times in this week
                              }
                           }
                        }
                     }
                  }
               }
            }       // end of IF mperiod = Month

            if (mperiod.equals( "Year" )) {            // if Year

               pstmt2y.clearParameters();             // get count from teecurr
               pstmt2y.setLong(1, nSlotParms.date);
               pstmt2y.setInt(2, nSlotParms.yy);
               pstmt2y.setString(3, nSlotParms.user5);
               pstmt2y.setString(4, nSlotParms.user5);
               pstmt2y.setString(5, nSlotParms.user5);
               pstmt2y.setString(6, nSlotParms.user5);
               pstmt2y.setString(7, nSlotParms.user5);
               rs = pstmt2y.executeQuery();

               count = 0;

               while (rs.next()) {

                  count++;                      // count number or tee times in this year
               }

               pstmt3y.clearParameters();        // get count from teepast
               pstmt3y.setLong(1, nSlotParms.date);
               pstmt3y.setInt(2, nSlotParms.yy);
               pstmt3y.setString(3, nSlotParms.user5);
               pstmt3y.setString(4, nSlotParms.user5);
               pstmt3y.setString(5, nSlotParms.user5);
               pstmt3y.setString(6, nSlotParms.user5);
               pstmt3y.setString(7, nSlotParms.user5);
               rs = pstmt3y.executeQuery();

               while (rs.next()) {

                  user1 = rs.getString("username1");
                  user2 = rs.getString("username2");
                  user3 = rs.getString("username3");
                  user4 = rs.getString("username4");
                  show1 = rs.getInt("show1");
                  show2 = rs.getInt("show2");
                  show3 = rs.getInt("show3");
                  show4 = rs.getInt("show4");
                  user5 = rs.getString("username5");
                  show5 = rs.getInt("show5");

                  if (user1.equalsIgnoreCase( nSlotParms.user5 ) && show1 == 1) {

                     count++;                      // count number or tee times in this week

                  } else {

                     if (user2.equalsIgnoreCase( nSlotParms.user5 ) && show2 == 1) {

                        count++;                      // count number or tee times in this week

                     } else {

                        if (user3.equalsIgnoreCase( nSlotParms.user5 ) && show3 == 1) {

                           count++;                      // count number or tee times in this week

                        } else {

                           if (user4.equalsIgnoreCase( nSlotParms.user5 ) && show4 == 1) {

                              count++;                      // count number or tee times in this week

                           } else {

                              if (user5.equalsIgnoreCase( nSlotParms.user5 ) && show5 == 1) {

                                 count++;                      // count number or tee times in this week
                              }
                           }
                        }
                     }
                  }
               }
            }       // end of IF mperiod = Year

            //
            //  Compare # of tee times in this period with max allowed for membership type
            //
            if (count >= mtimes)  {

               if (!nSlotParms.player5.equals( nSlotParms.oldPlayer1 ) &&
                   !nSlotParms.player5.equals( nSlotParms.oldPlayer2 ) &&
                   !nSlotParms.player5.equals( nSlotParms.oldPlayer3 ) &&
                   !nSlotParms.player5.equals( nSlotParms.oldPlayer4 ) &&
                   !nSlotParms.player5.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                  error = true;                // reject this member
                  nSlotParms.mship = nSlotParms.mship5;
                  nSlotParms.player = nSlotParms.player5;
                  nSlotParms.period = mperiod;
               }
            }
         }          // end of IF match found for player
      }          // end of player 5 if

      pstmt2.close();
      pstmt3.close();
      pstmt2m.close();
      pstmt3m.close();
      pstmt2y.close();
      pstmt3y.close();

   }
   catch (SQLException e1) {

      throw new Exception("SQL Error Checking Max Rounds - verifyNSlot.checkMaxRounds " + e1.getMessage());
   }
     
   catch (Exception e) {

      throw new Exception("Exception Checking Max Rounds - verifyNSlot.checkMaxRounds " + e.getMessage());
   }

   return(error);

 }

/**
 //************************************************************************
 //
 //  checkMaxGuests - checks for maximum number of guests exceeded per member or tee time
 //
 //************************************************************************
 **/

 public static boolean checkMaxGuests(parmNSlot nSlotParms, Connection con)
         throws Exception {


   //Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   boolean error = false;
   boolean check = false;
   boolean check1 = false;
   boolean check2 = false;
   boolean check3 = false;
   boolean check4 = false;
   boolean check5 = false;

   int i = 0;
   int guests = 0;        // number of restricted guests in tee time
   int grest_id = 0;

   String rcourse = "";
   String rest_fb = "";
   String grest_recurr = "";
   String per = "";                           // per = 'Member' or 'Tee Time'
     
   //String [] rguest = new String [nSlotParms.MAX_Guests];       // array to hold the Guest Restriction's guest types
   ArrayList<String> rguest = new ArrayList<String>();

   String [] oldPlayers = new String [5];    // array to hold the old player values


   if (nSlotParms.fb == 0) {                   // is Tee time for Front 9?

      nSlotParms.sfb = "Front";
   }

   if (nSlotParms.fb == 1) {                   // is it Back 9?

      nSlotParms.sfb = "Back";
   }

   try {

      PreparedStatement pstmt5 = con.prepareStatement (
         "SELECT * " +
         "FROM guestres2 " +
         "WHERE sdate <= ? AND edate >= ? AND stime <= ? AND etime >= ? AND activity_id = 0");

      pstmt5.clearParameters();        // clear the parms
      pstmt5.setLong(1, nSlotParms.date);
      pstmt5.setLong(2, nSlotParms.date);
      pstmt5.setInt(3, nSlotParms.time);
      pstmt5.setInt(4, nSlotParms.time);
      rs = pstmt5.executeQuery();      // execute the prepared stmt

      loop1:
      while (rs.next()) {

         nSlotParms.rest_name = rs.getString("name");    // get name for error message
         grest_recurr = rs.getString("recurr");
         grest_id = rs.getInt("id");
         rcourse = rs.getString("courseName");
         rest_fb = rs.getString("fb");
         per = rs.getString("per");
         nSlotParms.grest_num = rs.getInt("num_guests");

         // now look up the guest types for this restriction
         PreparedStatement pstmt2 = con.prepareStatement (
                 "SELECT guest_type FROM guestres2_gtypes WHERE guestres_id = ?");

         pstmt2.clearParameters();
         pstmt2.setInt(1, grest_id);

         rs2 = pstmt2.executeQuery();

         rguest.clear();

         while ( rs2.next() ) {

            rguest.add(rs2.getString("guest_type"));

         }
         pstmt2.close();

         check = false;       // init 'check guests' flag
         check1 = false;    
         check2 = false;
         check3 = false;
         check4 = false;
         check5 = false;

         guests = 0;          // reset # of guests in tee time
         
         //
         //  Check if course matches that specified in restriction
         //
         if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( nSlotParms.course ))) {

            if (!verifySlot.checkRestSuspend(-99, grest_id, (int)nSlotParms.date, nSlotParms.time, nSlotParms.day, nSlotParms.course, con)) {
                
                //
                //  We must check the recurrence for this day (Monday, etc.) and guest types
                //
                //     gx = guest types specified in player name fields
                //     rguest[x] = guest types from restriction gotten above
                //
                if (grest_recurr.equalsIgnoreCase( "every " + nSlotParms.day )) {

                   if ((rest_fb.equals( "Both" )) || (rest_fb.equals( nSlotParms.sfb ))) {  // if f/b matches

                      i = 0;
                      ploop1:
                      while (i < rguest.size()) {

                         if (!nSlotParms.g1.equals( "" )) {
                            if (nSlotParms.g1.equals( rguest.get(i) )) {
                               check1 = true;                 // indicate player1 is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g2.equals( "" )) {
                            if (nSlotParms.g2.equals( rguest.get(i) )) {
                               check2 = true;                 // indicate player2 is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g3.equals( "" )) {
                            if (nSlotParms.g3.equals( rguest.get(i) )) {
                               check3 = true;                 // indicate player3 is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g4.equals( "" )) {
                            if (nSlotParms.g4.equals( rguest.get(i) )) {
                               check4 = true;                 // indicate player4 is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g5.equals( "" )) {
                            if (nSlotParms.g5.equals( rguest.get(i) )) {
                               check5 = true;                 // indicate player5 is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         i++;
                      }
                   }
                }

                //
                //  if any day
                //
                if (grest_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                   if ((rest_fb.equals( "Both" )) || (rest_fb.equals( nSlotParms.sfb ))) {  // if f/b matches

                      i = 0;
                      ploop2:
                      while (i < rguest.size()) {

                         if (!nSlotParms.g1.equals( "" )) {
                            if (nSlotParms.g1.equals( rguest.get(i) )) {
                               check1 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g2.equals( "" )) {
                            if (nSlotParms.g2.equals( rguest.get(i) )) {
                               check2 = true;                  // indicate check num of guests
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g3.equals( "" )) {
                            if (nSlotParms.g3.equals( rguest.get(i) )) {
                               check3 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g4.equals( "" )) {
                            if (nSlotParms.g4.equals( rguest.get(i) )) {
                               check4 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g5.equals( "" )) {
                            if (nSlotParms.g5.equals( rguest.get(i) )) {
                               check5 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         i++;
                      }
                   }
                }

                //
                //  if a Weekday
                //
                if ((grest_recurr.equalsIgnoreCase( "all weekdays" )) &&
                    (!nSlotParms.day.equalsIgnoreCase( "saturday" )) &&
                    (!nSlotParms.day.equalsIgnoreCase( "sunday" ))) {

                   if ((rest_fb.equals( "Both" )) || (rest_fb.equals( nSlotParms.sfb ))) {  // if f/b matches

                      i = 0;
                      ploop3:
                      while (i < rguest.size()) {

                         if (!nSlotParms.g1.equals( "" )) {
                            if (nSlotParms.g1.equals( rguest.get(i) )) {
                               check1 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g2.equals( "" )) {
                            if (nSlotParms.g2.equals( rguest.get(i) )) {
                               check2 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g3.equals( "" )) {
                            if (nSlotParms.g3.equals( rguest.get(i) )) {
                               check3 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g4.equals( "" )) {
                            if (nSlotParms.g4.equals( rguest.get(i) )) {
                               check4 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g5.equals( "" )) {
                            if (nSlotParms.g5.equals( rguest.get(i) )) {
                               check5 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         i++;
                      }
                   }
                }

                //
                //  if Weekends and its Saturday
                //
                if ((grest_recurr.equalsIgnoreCase( "all weekends" )) &&
                    (nSlotParms.day.equalsIgnoreCase( "saturday" ))) {

                   if ((rest_fb.equals( "Both" )) || (rest_fb.equals( nSlotParms.sfb ))) {  // if f/b matches

                      i = 0;
                      ploop4:
                      while (i < rguest.size()) {

                         if (!nSlotParms.g1.equals( "" )) {
                            if (nSlotParms.g1.equals( rguest.get(i) )) {
                               check1 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g2.equals( "" )) {
                            if (nSlotParms.g2.equals( rguest.get(i))) {
                               check2 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g3.equals( "" )) {
                            if (nSlotParms.g3.equals( rguest.get(i) )) {
                               check3 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g4.equals( "" )) {
                            if (nSlotParms.g4.equals( rguest.get(i) )) {
                               check4 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g5.equals( "" )) {
                            if (nSlotParms.g5.equals( rguest.get(i) )) {
                               check5 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         i++;
                      }
                   }
                }

                //
                //  if Weekends and its Sunday
                //
                if ((grest_recurr.equalsIgnoreCase( "all weekends" )) &&
                    (nSlotParms.day.equalsIgnoreCase( "sunday" ))) {

                   if ((rest_fb.equals( "Both" )) || (rest_fb.equals( nSlotParms.sfb ))) {  // if f/b matches

                      i = 0;
                      ploop5:
                      while (i < rguest.size()) {

                         if (!nSlotParms.g1.equals( "" )) {
                            if (nSlotParms.g1.equals( rguest.get(i) )) {
                               check1 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g2.equals( "" )) {
                            if (nSlotParms.g2.equals( rguest.get(i) )) {
                               check2 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g3.equals( "" )) {
                            if (nSlotParms.g3.equals( rguest.get(i) )) {
                               check3 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g4.equals( "" )) {
                            if (nSlotParms.g4.equals( rguest.get(i) )) {
                               check4 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         if (!nSlotParms.g5.equals( "" )) {
                            if (nSlotParms.g5.equals( rguest.get(i) )) {
                               check5 = true;                 // indicate player is guest
                               guests++;                      // bump number of restricted guests in tee time
                            }
                         }
                         i++;
                      }
                   }
                }
            }
         }      // end of IF course matches

         //
         //  If any player position contains a restricted guest, then check if it already was in the
         //  tee time.  If so, then we do not have to check them as they might have been approved by
         //  the pro.  Allow for the old players being shifted up if a player was removed.  The order
         //  of the original guests cannot be changed in order for this to work.
         //
         oldPlayers[0] = nSlotParms.oldPlayer1;
         oldPlayers[1] = nSlotParms.oldPlayer2;
         oldPlayers[2] = nSlotParms.oldPlayer3;
         oldPlayers[3] = nSlotParms.oldPlayer4;
         oldPlayers[4] = nSlotParms.oldPlayer5;
           
         i = 0;

         if (check1 == true) {   // if player contains guest that is restricted

            check = true;       // init as 'check the guests'

            loopg1:
            while (i < 5) {
              
               if (nSlotParms.player1.equals( oldPlayers[i] )) {   // if already approved
              
                  check = false;            // ok to skip this guest
                  i++;                      // bump to next position
                  break loopg1;             // exit loop
               }
               i++;
            }
         }
         if (check2 == true && check == false) {   // if player contains guest that is restricted

            check = true;       // init as 'check the guests'

            loopg2:
            while (i < 5) {    // DO NOT reset i (so we start we we left off above)

               if (nSlotParms.player2.equals( oldPlayers[i] )) {   // if already approved

                  check = false;            // ok to skip this guest
                  i++;                      // bump to next position
                  break loopg2;             // exit loop
               }
               i++;
            }
         }
         if (check3 == true && check == false) {   // if player contains guest that is restricted

            check = true;       // init as 'check the guests'

            loopg3:
            while (i < 5) {    // DO NOT reset i (so we start we we left off above)

               if (nSlotParms.player3.equals( oldPlayers[i] )) {   // if already approved

                  check = false;            // ok to skip this guest
                  i++;                      // bump to next position
                  break loopg3;             // exit loop
               }
               i++;
            }
         }
         if (check4 == true && check == false) {   // if player contains guest that is restricted

            check = true;       // init as 'check the guests'

            loopg4:
            while (i < 5) {    // DO NOT reset i (so we start we we left off above)

               if (nSlotParms.player4.equals( oldPlayers[i] )) {   // if already approved

                  check = false;            // ok to skip this guest
                  i++;                      // bump to next position
                  break loopg4;             // exit loop
               }
               i++;
            }
         }
         if (check5 == true && check == false) {   // if player contains guest that is restricted

            check = true;       // init as 'check the guests'

            loopg5:
            while (i < 5) {    // DO NOT reset i (so we start we we left off above)

               if (nSlotParms.player5.equals( oldPlayers[i] )) {   // if already approved

                  check = false;            // ok to skip this guest
                  i++;                      // bump to next position
                  break loopg5;             // exit loop
               }
               i++;
            }
         }

         //
         //  Now see if we must check for max number of guests exceeded
         //
         if (check == true) {   // if restriction exists for this day and time

            // if num of guests req'd (guests) > num allowed (grest_num) per member
            //
            //       to get here guests is > 0
            //       grest_num is 0 - 3
            //       members is 0 - 5
            //       per is 'Member' or 'Tee Time'
            //
            if (nSlotParms.grest_num == 0) {      // no guests allowed

               error = true;                 // set error flag
               nSlotParms.grest_per = per;    // save this rest's per value
               break loop1;                  // done checking - exit while loop
            }

            if (per.equals( "Member" )) {        // if restriction is 'per member'

               if (nSlotParms.members < 2) {

                  if (guests > nSlotParms.grest_num) {     // if 1 member (or none) and more guests than allowed

                     error = true;              // set error flag
                     nSlotParms.grest_per = per;    // save this rest's per value
                     break loop1;               // done checking - exit while loop
                  }
               }
               if (nSlotParms.members == 2) {              // if > 2, then must be < 1 guest per mem

                  if (nSlotParms.grest_num == 1) {

                     if (guests > 2) {                   // if 1 allowed and more than 1 each

                        error = true;           // set error flag
                        nSlotParms.grest_per = per;    // save this rest's per value
                        break loop1;            // done checking - exit while loop
                     }
                  }
               }

            } else {      // per Tee Time

               if (guests > nSlotParms.grest_num) {      // if more guests than allowed per tee time
                 
                  error = true;      // set error flag
                  nSlotParms.grest_per = per;    // save this rest's per value
                  break loop1;            // done checking - exit while loop
               }
            }
         }
      }   // end of loop1 while loop

      pstmt5.close();

   }
   catch (Exception e) {

      throw new Exception("Error Checking Guest Rest - verifyNSlot: " + e.getMessage());
   }

   return(error);

 }

 
/**
 //************************************************************************
 //
 //   getUsers - Get the usernames, membership types and hndcp's for players if matching name found
 //
 //     sets:  username
 //            membership type
 //            member type
 //            member number
 //            handicap
 //            number of members
 //
 //************************************************************************
 **/

 public static void getUsers(parmNSlot nSlotParms, Connection con)
         throws Exception {


   //Statement stmt = null;
   ResultSet rs = null;


   //
   //  Init the values in slotParm
   //
   nSlotParms.user1 = "";
   nSlotParms.user2 = "";
   nSlotParms.user3 = "";
   nSlotParms.user4 = "";
   nSlotParms.user5 = "";
   nSlotParms.mship1 = "";
   nSlotParms.mship2 = "";
   nSlotParms.mship3 = "";
   nSlotParms.mship4 = "";
   nSlotParms.mship5 = "";
   nSlotParms.mtype1 = "";
   nSlotParms.mtype2 = "";
   nSlotParms.mtype3 = "";
   nSlotParms.mtype4 = "";
   nSlotParms.mtype5 = "";
   nSlotParms.mNum1 = "";
   nSlotParms.mNum2 = "";
   nSlotParms.mNum3 = "";
   nSlotParms.mNum4 = "";
   nSlotParms.mNum5 = "";
   nSlotParms.mstype1 = "";
   nSlotParms.mstype2 = "";
   nSlotParms.mstype3 = "";
   nSlotParms.mstype4 = "";
   nSlotParms.mstype5 = "";
                
   nSlotParms.inval1 = 0;    // init invalid indicators
   nSlotParms.inval2 = 0;
   nSlotParms.inval3 = 0;
   nSlotParms.inval4 = 0;
   nSlotParms.inval5 = 0;
   nSlotParms.members = 0;   // init member counter

   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT username, m_ship, m_type, g_hancap, memNum, msub_type " +
         "FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

      if ((!nSlotParms.fname1.equals( "" )) && (!nSlotParms.lname1.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, nSlotParms.lname1);
         pstmt1.setString(2, nSlotParms.fname1);
         pstmt1.setString(3, nSlotParms.mi1);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            nSlotParms.user1 = rs.getString(1);
            nSlotParms.mship1 = rs.getString(2);
            nSlotParms.mtype1 = rs.getString(3);
            nSlotParms.hndcp1 = rs.getFloat(4);
            nSlotParms.mNum1 = rs.getString(5);
            nSlotParms.mstype1 = rs.getString(6);

            nSlotParms.members++;         // increment number of members this res.
              
            //
            //  Make sure member has required fields
            //
            if (nSlotParms.user1 == null || nSlotParms.user1.equals( "" ) ||
                nSlotParms.mship1 == null || nSlotParms.mship1.equals( "" ) ||
                nSlotParms.mtype1 == null || nSlotParms.mtype1.equals( "" )) {

               nSlotParms.inval1 = 2;        // indicate incomplete member record
            }
              
            if (nSlotParms.mNum1 == null) {
              
               nSlotParms.mNum1 = "";
            }

         } else {

            if (!nSlotParms.player1.equals( nSlotParms.oldPlayer1 ) &&
                !nSlotParms.player1.equals( nSlotParms.oldPlayer2 ) &&
                !nSlotParms.player1.equals( nSlotParms.oldPlayer3 ) &&
                !nSlotParms.player1.equals( nSlotParms.oldPlayer4 ) &&
                !nSlotParms.player1.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro
                  
               nSlotParms.inval1 = 1;        // indicate invalid name entered
            }
         }
      }

      if ((!nSlotParms.fname2.equals( "" )) && (!nSlotParms.lname2.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, nSlotParms.lname2);
         pstmt1.setString(2, nSlotParms.fname2);
         pstmt1.setString(3, nSlotParms.mi2);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            nSlotParms.user2 = rs.getString(1);
            nSlotParms.mship2 = rs.getString(2);
            nSlotParms.mtype2 = rs.getString(3);
            nSlotParms.hndcp2 = rs.getFloat(4);
            nSlotParms.mNum2 = rs.getString(5);
            nSlotParms.mstype2 = rs.getString(6);

            nSlotParms.members++;         // increment number of members this res.

            //
            //  Make sure member has required fields
            //
            if (nSlotParms.user2 == null || nSlotParms.user2.equals( "" ) ||
                nSlotParms.mship2 == null || nSlotParms.mship2.equals( "" ) ||
                nSlotParms.mtype2 == null || nSlotParms.mtype2.equals( "" )) {

               nSlotParms.inval2 = 2;        // indicate incomplete member record
            }

            if (nSlotParms.mNum2 == null) {

               nSlotParms.mNum2 = "";
            }

         } else {

            if (!nSlotParms.player2.equals( nSlotParms.oldPlayer1 ) &&
                !nSlotParms.player2.equals( nSlotParms.oldPlayer2 ) &&
                !nSlotParms.player2.equals( nSlotParms.oldPlayer3 ) &&
                !nSlotParms.player2.equals( nSlotParms.oldPlayer4 ) &&
                !nSlotParms.player2.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

               nSlotParms.inval2 = 1;        // indicate invalid name entered
            }
         }
      }

      if ((!nSlotParms.fname3.equals( "" )) && (!nSlotParms.lname3.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, nSlotParms.lname3);
         pstmt1.setString(2, nSlotParms.fname3);
         pstmt1.setString(3, nSlotParms.mi3);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            nSlotParms.user3 = rs.getString(1);
            nSlotParms.mship3 = rs.getString(2);
            nSlotParms.mtype3 = rs.getString(3);
            nSlotParms.hndcp3 = rs.getFloat(4);
            nSlotParms.mNum3 = rs.getString(5);
            nSlotParms.mstype3 = rs.getString(6);

            nSlotParms.members++;         // increment number of members this res.

            //
            //  Make sure member has required fields
            //
            if (nSlotParms.user3 == null || nSlotParms.user3.equals( "" ) ||
                nSlotParms.mship3 == null || nSlotParms.mship3.equals( "" ) ||
                nSlotParms.mtype3 == null || nSlotParms.mtype3.equals( "" )) {

               nSlotParms.inval3 = 2;        // indicate incomplete member record
            }

            if (nSlotParms.mNum3 == null) {

               nSlotParms.mNum3 = "";
            }

         } else {

            if (!nSlotParms.player3.equals( nSlotParms.oldPlayer1 ) &&
                !nSlotParms.player3.equals( nSlotParms.oldPlayer2 ) &&
                !nSlotParms.player3.equals( nSlotParms.oldPlayer3 ) &&
                !nSlotParms.player3.equals( nSlotParms.oldPlayer4 ) &&
                !nSlotParms.player3.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

               nSlotParms.inval3 = 1;        // indicate invalid name entered
            }
         }
      }

      if ((!nSlotParms.fname4.equals( "" )) && (!nSlotParms.lname4.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, nSlotParms.lname4);
         pstmt1.setString(2, nSlotParms.fname4);
         pstmt1.setString(3, nSlotParms.mi4);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            nSlotParms.user4 = rs.getString(1);
            nSlotParms.mship4 = rs.getString(2);
            nSlotParms.mtype4 = rs.getString(3);
            nSlotParms.hndcp4 = rs.getFloat(4);
            nSlotParms.mNum4 = rs.getString(5);
            nSlotParms.mstype4 = rs.getString(6);

            nSlotParms.members++;         // increment number of members this res.

            //
            //  Make sure member has required fields
            //
            if (nSlotParms.user4 == null || nSlotParms.user4.equals( "" ) ||
                nSlotParms.mship4 == null || nSlotParms.mship4.equals( "" ) ||
                nSlotParms.mtype4 == null || nSlotParms.mtype4.equals( "" )) {

               nSlotParms.inval4 = 2;        // indicate incomplete member record
            }

            if (nSlotParms.mNum4 == null) {

               nSlotParms.mNum4 = "";
            }

         } else {

            if (!nSlotParms.player4.equals( nSlotParms.oldPlayer1 ) &&
                !nSlotParms.player4.equals( nSlotParms.oldPlayer2 ) &&
                !nSlotParms.player4.equals( nSlotParms.oldPlayer3 ) &&
                !nSlotParms.player4.equals( nSlotParms.oldPlayer4 ) &&
                !nSlotParms.player4.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

               nSlotParms.inval4 = 1;        // indicate invalid name entered
            }
         }
      }

      if ((!nSlotParms.fname5.equals( "" )) && (!nSlotParms.lname5.equals( "" ))) {

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, nSlotParms.lname5);
         pstmt1.setString(2, nSlotParms.fname5);
         pstmt1.setString(3, nSlotParms.mi5);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            nSlotParms.user5 = rs.getString(1);
            nSlotParms.mship5 = rs.getString(2);
            nSlotParms.mtype5 = rs.getString(3);
            nSlotParms.hndcp5 = rs.getFloat(4);
            nSlotParms.mNum5 = rs.getString(5);
            nSlotParms.mstype5 = rs.getString(6);

            nSlotParms.members++;         // increment number of members this res.

            //
            //  Make sure member has required fields
            //
            if (nSlotParms.user5 == null || nSlotParms.user5.equals( "" ) ||
                nSlotParms.mship5 == null || nSlotParms.mship5.equals( "" ) ||
                nSlotParms.mtype5 == null || nSlotParms.mtype5.equals( "" )) {

               nSlotParms.inval5 = 2;        // indicate incomplete member record
            }

            if (nSlotParms.mNum5 == null) {

               nSlotParms.mNum5 = "";
            }

         } else {

            if (!nSlotParms.player5.equals( nSlotParms.oldPlayer1 ) &&
                !nSlotParms.player5.equals( nSlotParms.oldPlayer2 ) &&
                !nSlotParms.player5.equals( nSlotParms.oldPlayer3 ) &&
                !nSlotParms.player5.equals( nSlotParms.oldPlayer4 ) &&
                !nSlotParms.player5.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

               nSlotParms.inval5 = 1;        // indicate invalid name entered
            }
         }
      }
      pstmt1.close();

   }
   catch (Exception e) {

      throw new Exception("Error getting user info - verifyNSlot.getUsers " + e.getMessage());
   }

   return;

 }

 
/**
 //************************************************************************
 //
 //  Determine if player names are guests
 //
 //      sets:  nSlotParms.gX (X = 1 - 5) - guest type for player, if guest
 //             nSlotParms.guests (# of guests)
 //             nSlotParms.userg(1-5) - member username assoc'd with guest (Proshop only)
 //
 //************************************************************************
 **/

 public static void parseGuests(parmNSlot nSlotParms, Connection con)
         throws Exception {


   PreparedStatement pstmt1 = null;
   //Statement stmt = null;
   ResultSet rs = null;

   int i = 0;
   int i2 = 0;

   boolean invalid = false;

   StringTokenizer tok = null;
   String fname = "";
   String lname = "";
   String mi = "";
   String user = "";

   String club = nSlotParms.club;

   String [] oldPlayers = new String [5];    // array to hold the old player values

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con);   // since this is a notification site we can hard code a zero for the root_activity_id

   //
   //   Get the guest names specified for this club
   //
   try {

      getClub.getParms(con, parm);        // get the club parms

      //
      //   Remove any guest types that are null - for tests below
      //
      i = 0;
      while (i < parm.MAX_Guests) {

         if (parm.guest[i].equals( "" )) {

            parm.guest[i] = "$@#!^&*";      // make so it won't match player name
         }
         i++;
      }         // end of while loop

      //
      //  init guest parms
      //
      nSlotParms.g1 = "";
      nSlotParms.g2 = "";
      nSlotParms.g3 = "";
      nSlotParms.g4 = "";
      nSlotParms.g5 = "";
      nSlotParms.gstA[0] = "";
      nSlotParms.gstA[1] = "";
      nSlotParms.gstA[2] = "";
      nSlotParms.gstA[3] = "";
      nSlotParms.gstA[4] = "";
      nSlotParms.gplayer = "";
      nSlotParms.hit3 = false;

      oldPlayers[0] = nSlotParms.oldPlayer1;       // put original player values in array
      oldPlayers[1] = nSlotParms.oldPlayer2;
      oldPlayers[2] = nSlotParms.oldPlayer3;
      oldPlayers[3] = nSlotParms.oldPlayer4;
      oldPlayers[4] = nSlotParms.oldPlayer5;

      //
      //    Check if player is a guest
      //
      if (!nSlotParms.player1.equals( "" )) {

         i = 0;
         loop1:
         while (i < parm.MAX_Guests) {

            if (nSlotParms.player1.startsWith( parm.guest[i] )) {

               nSlotParms.g1 = parm.guest[i];       // indicate player is a guest name and save name
               nSlotParms.gstA[0] = nSlotParms.player1;    // save guest value
               nSlotParms.guests++;             // increment number of guests this slot

               if (parm.gOpt[i] > 0) {                        // if Proshop-only guest type

                  invalid = true;                 // default to invalid guest type
                  i2 = 0;
                  loopg1:
                  while (i2 < 5) {

                     if (nSlotParms.player1.equals( oldPlayers[i2] )) {   // if already approved

                        invalid = false;          // ok to skip this guest
                        break loopg1;             // exit loop
                     }
                     i2++;
                  }

                  if (invalid == true) {                       // if new or changed player name

                     nSlotParms.gplayer = nSlotParms.player1;    // indicate error (ok if it was already entered by pro)
                  }
               }
                 
               //
               //  Check for guest name if requested for this club
               //
               if (invalid == false && parm.forceg > 0) {

                  invalid = checkGstName(nSlotParms.player1, parm.guest[i], club);      // go check for a name

                  if (invalid == true) {         // if name not specified
                 
                     if (nSlotParms.player1.equals( oldPlayers[0] )) {   // if already approved

                        invalid = false;          // ok to skip this guest

                     } else {

                        nSlotParms.gplayer = nSlotParms.player1;    // indicate error 
                        nSlotParms.hit3 = true;                    // error is guest name not specified
                     }
                  }
               }
                 
               break loop1;     // matching guest type - exit loop
            }
            i++;
         }         // end of while loop
      }
      if (!nSlotParms.player2.equals( "" )) {

         i = 0;
         loop2:
         while (i < parm.MAX_Guests) {

            if (nSlotParms.player2.startsWith( parm.guest[i] )) {

               nSlotParms.g2 = parm.guest[i];       // indicate player is a guest name and save name
               nSlotParms.gstA[1] = nSlotParms.player2;    // save guest value
               nSlotParms.guests++;             // increment number of guests this slot

               if (parm.gOpt[i] > 0) {                        // if Proshop-only guest type

                  invalid = true;                 // default to invalid guest type
                  i2 = 0;
                  loopg2:
                  while (i2 < 5) {

                     if (nSlotParms.player2.equals( oldPlayers[i2] )) {   // if already approved

                        invalid = false;          // ok to skip this guest
                        break loopg2;             // exit loop
                     }
                     i2++;
                  }

                  if (invalid == true) {                       // if new or changed player name

                     nSlotParms.gplayer = nSlotParms.player2;    // indicate error (ok if it was already entered by pro)
                  }
               }

               //
               //  Check for guest name if requested for this club
               //
               if (invalid == false && parm.forceg > 0) {

                  invalid = checkGstName(nSlotParms.player2, parm.guest[i], club);      // go check for a name

                  if (invalid == true) {         // if name not specified

                     if (nSlotParms.player2.equals( oldPlayers[1] )) {   // if already approved

                        invalid = false;          // ok to skip this guest

                     } else {

                        nSlotParms.gplayer = nSlotParms.player2;    // indicate error
                        nSlotParms.hit3 = true;                    // error is guest name not specified
                     }
                  }
               }

               break loop2;
            }
            i++;
         }         // end of while loop
      }
      if (!nSlotParms.player3.equals( "" )) {

         i = 0;
         loop3:
         while (i < parm.MAX_Guests) {

            if (nSlotParms.player3.startsWith( parm.guest[i] )) {

               nSlotParms.g3 = parm.guest[i];       // indicate player is a guest name and save name
               nSlotParms.gstA[2] = nSlotParms.player3;    // save guest value
               nSlotParms.guests++;             // increment number of guests this slot

               if (parm.gOpt[i] > 0) {                        // if Proshop-only guest type

                  invalid = true;                 // default to invalid guest type
                  i2 = 0;
                  loopg3:
                  while (i2 < 5) {

                     if (nSlotParms.player3.equals( oldPlayers[i2] )) {   // if already approved

                        invalid = false;          // ok to skip this guest
                        break loopg3;             // exit loop
                     }
                     i2++;
                  }

                  if (invalid == true) {                       // if new or changed player name

                     nSlotParms.gplayer = nSlotParms.player3;                   // indicate error (ok if it was already entered by pro)
                  }
               }

               //
               //  Check for guest name if requested for this club
               //
               if (invalid == false && parm.forceg > 0) {

                  invalid = checkGstName(nSlotParms.player3, parm.guest[i], club);      // go check for a name

                  if (invalid == true) {         // if name not specified

                     if (nSlotParms.player3.equals( oldPlayers[2] )) {   // if already approved

                        invalid = false;          // ok to skip this guest

                     } else {

                        nSlotParms.gplayer = nSlotParms.player3;    // indicate error
                        nSlotParms.hit3 = true;                    // error is guest name not specified
                     }
                  }
               }

               break loop3;
            }
            i++;
         }         // end of while loop
      }
      if (!nSlotParms.player4.equals( "" )) {

         i = 0;
         loop4:
         while (i < parm.MAX_Guests) {

            if (nSlotParms.player4.startsWith( parm.guest[i] )) {

               nSlotParms.g4 = parm.guest[i];       // indicate player is a guest name and save name
               nSlotParms.gstA[3] = nSlotParms.player4;    // save guest value
               nSlotParms.guests++;             // increment number of guests this slot

               if (parm.gOpt[i] > 0) {                        // if Proshop-only guest type

                  invalid = true;                 // default to invalid guest type
                  i2 = 0;
                  loopg4:
                  while (i2 < 5) {

                     if (nSlotParms.player4.equals( oldPlayers[i2] )) {   // if already approved

                        invalid = false;          // ok to skip this guest
                        break loopg4;             // exit loop
                     }
                     i2++;
                  }

                  if (invalid == true) {                       // if new or changed player name

                     nSlotParms.gplayer = nSlotParms.player4;                   // indicate error (ok if it was already entered by pro)
                  }
               }

               //
               //  Check for guest name if requested for this club
               //
               if (invalid == false && parm.forceg > 0) {

                  invalid = checkGstName(nSlotParms.player4, parm.guest[i], club);      // go check for a name

                  if (invalid == true) {         // if name not specified

                     if (nSlotParms.player4.equals( oldPlayers[3] )) {   // if already approved

                        invalid = false;          // ok to skip this guest

                     } else {

                        nSlotParms.gplayer = nSlotParms.player4;    // indicate error
                        nSlotParms.hit3 = true;                    // error is guest name not specified
                     }
                  }
               }

               break loop4;
            }
            i++;
         }         // end of while loop
      }
      if (!nSlotParms.player5.equals( "" )) {

         i = 0;
         loop5:
         while (i < parm.MAX_Guests) {

            if (nSlotParms.player5.startsWith( parm.guest[i] )) {

               nSlotParms.g5 = parm.guest[i];       // indicate player is a guest name and save name
               nSlotParms.gstA[4] = nSlotParms.player5;    // save guest value
               nSlotParms.guests++;             // increment number of guests this slot

               if (parm.gOpt[i] > 0) {                        // if Proshop-only guest type

                  invalid = true;                 // default to invalid guest type
                  i2 = 0;
                  loopg5:
                  while (i2 < 5) {

                     if (nSlotParms.player5.equals( oldPlayers[i2] )) {   // if already approved

                        invalid = false;          // ok to skip this guest
                        break loopg5;             // exit loop
                     }
                     i2++;
                  }

                  if (invalid == true) {                       // if new or changed player name

                     nSlotParms.gplayer = nSlotParms.player5;                   // indicate error (ok if it was already entered by pro)
                  }
               }

               //
               //  Check for guest name if requested for this club
               //
               if (invalid == false && parm.forceg > 0) {

                  invalid = checkGstName(nSlotParms.player5, parm.guest[i], club);      // go check for a name

                  if (invalid == true) {         // if name not specified

                     if (nSlotParms.player5.equals( oldPlayers[4] )) {   // if already approved

                        invalid = false;          // ok to skip this guest

                     } else {

                        nSlotParms.gplayer = nSlotParms.player5;    // indicate error
                        nSlotParms.hit3 = true;                    // error is guest name not specified
                     }
                  }
               }

               break loop5;
            }
            i++;
         }         // end of while loop
      }
        
      //
      //  Check for Unaccompanied Guests - see if Associated Member Name provided
      //
      if (!nSlotParms.mem1.equals( "" )) {     // if name provided, then find username

         tok = new StringTokenizer( nSlotParms.mem1 );     // space is the default token

         if ( tok.countTokens() == 2 ) {         // first name, last name

            fname = tok.nextToken();
            lname = tok.nextToken();
            mi = "";
         }

         if ( tok.countTokens() == 3 ) {         // first name, mi, last name

            fname = tok.nextToken();
            mi = tok.nextToken();
            lname = tok.nextToken();
         }

         if (!lname.equals( "" )) {

            pstmt1 = con.prepareStatement (
               "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, lname);
            pstmt1.setString(2, fname);
            pstmt1.setString(3, mi);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               nSlotParms.userg1 = rs.getString(1);
               user = nSlotParms.userg1;               // save
            }
            pstmt1.close();
         }
      }
      if (!nSlotParms.mem2.equals( "" )) {     // if name provided, then find username

         tok = new StringTokenizer( nSlotParms.mem2 );     // space is the default token

         if ( tok.countTokens() == 2 ) {         // first name, last name

            fname = tok.nextToken();
            lname = tok.nextToken();
            mi = "";
         }

         if ( tok.countTokens() == 3 ) {         // first name, mi, last name

            fname = tok.nextToken();
            mi = tok.nextToken();
            lname = tok.nextToken();
         }

         if (!lname.equals( "" )) {

            pstmt1 = con.prepareStatement (
               "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, lname);
            pstmt1.setString(2, fname);
            pstmt1.setString(3, mi);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               nSlotParms.userg2 = rs.getString(1);
               user = nSlotParms.userg2;               // save
            }
            pstmt1.close();
         }
      } else {

         if (!nSlotParms.g2.equals( "" ) && nSlotParms.userg2.equals( "" )) {  // if guest but not assigned

            nSlotParms.userg2 = user;               // assign to last assigned, if any
         }
      }

      if (!nSlotParms.mem3.equals( "" )) {     // if name provided, then find username

         tok = new StringTokenizer( nSlotParms.mem3 );     // space is the default token

         if ( tok.countTokens() == 2 ) {         // first name, last name

            fname = tok.nextToken();
            lname = tok.nextToken();
            mi = "";
         }

         if ( tok.countTokens() == 3 ) {         // first name, mi, last name

            fname = tok.nextToken();
            mi = tok.nextToken();
            lname = tok.nextToken();
         }

         if (!lname.equals( "" )) {

            pstmt1 = con.prepareStatement (
               "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, lname);
            pstmt1.setString(2, fname);
            pstmt1.setString(3, mi);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               nSlotParms.userg3 = rs.getString(1);
               user = nSlotParms.userg3;               // save
            }
            pstmt1.close();
         }
      } else {

         if (!nSlotParms.g3.equals( "" ) && nSlotParms.userg3.equals( "" )) {  // if guest but not assigned

            nSlotParms.userg3 = user;               // assign to last assigned, if any
         }
      }

      if (!nSlotParms.mem4.equals( "" )) {     // if name provided, then find username

         tok = new StringTokenizer( nSlotParms.mem4 );     // space is the default token

         if ( tok.countTokens() == 2 ) {         // first name, last name

            fname = tok.nextToken();
            lname = tok.nextToken();
            mi = "";
         }

         if ( tok.countTokens() == 3 ) {         // first name, mi, last name

            fname = tok.nextToken();
            mi = tok.nextToken();
            lname = tok.nextToken();
         }

         if (!lname.equals( "" )) {

            pstmt1 = con.prepareStatement (
               "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, lname);
            pstmt1.setString(2, fname);
            pstmt1.setString(3, mi);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               nSlotParms.userg4 = rs.getString(1);
               user = nSlotParms.userg4;               // save
            }
            pstmt1.close();
         }
      } else {

         if (!nSlotParms.g4.equals( "" ) && nSlotParms.userg4.equals( "" )) {  // if guest but not assigned

            nSlotParms.userg4 = user;               // assign to last assigned, if any
         }
      }

      if (!nSlotParms.mem5.equals( "" )) {     // if name provided, then find username

         tok = new StringTokenizer( nSlotParms.mem5 );     // space is the default token

         if ( tok.countTokens() == 2 ) {         // first name, last name

            fname = tok.nextToken();
            lname = tok.nextToken();
            mi = "";
         }

         if ( tok.countTokens() == 3 ) {         // first name, mi, last name

            fname = tok.nextToken();
            mi = tok.nextToken();
            lname = tok.nextToken();
         }

         if (!lname.equals( "" )) {

            pstmt1 = con.prepareStatement (
               "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, lname);
            pstmt1.setString(2, fname);
            pstmt1.setString(3, mi);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               nSlotParms.userg5 = rs.getString(1);
            }
            pstmt1.close();
         }
      } else {

         if (!nSlotParms.g5.equals( "" ) && nSlotParms.userg5.equals( "" )) {   // if guest but not assigned

            nSlotParms.userg5 = user;               // assign to last assigned, if any
         }
      }

   }
   catch (Exception e) {

      throw new Exception("Error getting guest info - verifyNSlot.parseGuests " + e.getMessage());
   }

 }

 
/**
 //************************************************************************
 //
 //  checkGuestNames - checks for guest names - if required for this club
 //
 //  called by:  above and Member_slotm and Member_lott
 //
 //************************************************************************
 **/

 public static boolean checkGstName(String name, String gType, String club) {

    boolean error = false;

    int count = 0;
    int count2 = 0;


    if (name.equals( gType )) {   // if matches then can't be name

        error = true;

    } else {

        //
        //  Use tokens to determine the number of words in each string.
        //  There must be at least 2 extra words in the player name.
        //
        StringTokenizer tok = new StringTokenizer( name, " " );          // delimiter is a space
        count = tok.countTokens();                                       // number of words in player name

        StringTokenizer tok2 = new StringTokenizer( gType, " " );        // guest type
        count2 = tok2.countTokens();                                     // number of words in guest type

        if (count > count2) {

            count = count - count2;          // how many more words in player name than guest type

            // must be at least 2
            if (count < 2) error = true;

        } else { error = true; }
        
    }

   return(error);
 }
 
 
/**
 //************************************************************************
 //
 //  shiftUp - shifts players so they start in position 1
 //
 //************************************************************************
 **/

 public static void shiftUp(parmNSlot nSlotParms) {


   if (nSlotParms.player1.equals( "" )) {    // if empty

      if (!nSlotParms.player2.equals( "" )) {    // if not empty

         nSlotParms.player1 = nSlotParms.player2;
         nSlotParms.p1cw = nSlotParms.p2cw;
         nSlotParms.show1 = nSlotParms.show2;
         nSlotParms.pos1 = nSlotParms.pos2;
         nSlotParms.p91 = nSlotParms.p92;
         nSlotParms.custom_disp1 = nSlotParms.custom_disp2;
         nSlotParms.player2 = "";
         nSlotParms.show2 = 0;
         nSlotParms.pos2 = 0;
         nSlotParms.p92 = 0;
         nSlotParms.custom_disp2 = "";

      } else {

         if (!nSlotParms.player3.equals( "" )) {    // if not empty

            nSlotParms.player1 = nSlotParms.player3;
            nSlotParms.p1cw = nSlotParms.p3cw;
            nSlotParms.show1 = nSlotParms.show3;
            nSlotParms.pos1 = nSlotParms.pos3;
            nSlotParms.p91 = nSlotParms.p93;
            nSlotParms.custom_disp1 = nSlotParms.custom_disp3;
            nSlotParms.player3 = "";
            nSlotParms.show3 = 0;
            nSlotParms.pos3 = 0;
            nSlotParms.p93 = 0;
            nSlotParms.custom_disp3 = "";

         } else {

            if (!nSlotParms.player4.equals( "" )) {    // if not empty

               nSlotParms.player1 = nSlotParms.player4;
               nSlotParms.p1cw = nSlotParms.p4cw;
               nSlotParms.show1 = nSlotParms.show4;
               nSlotParms.pos1 = nSlotParms.pos4;
               nSlotParms.p91 = nSlotParms.p94;
               nSlotParms.custom_disp1 = nSlotParms.custom_disp4;
               nSlotParms.player4 = "";
               nSlotParms.show4 = 0;
               nSlotParms.pos4 = 0;
               nSlotParms.p94 = 0;
               nSlotParms.custom_disp4 = "";

            } else {

               if (!nSlotParms.player5.equals( "" )) {    // if not empty

                  nSlotParms.player1 = nSlotParms.player5;
                  nSlotParms.p1cw = nSlotParms.p5cw;
                  nSlotParms.show1 = nSlotParms.show5;
                  nSlotParms.pos1 = nSlotParms.pos5;
                  nSlotParms.p91 = nSlotParms.p95;
                  nSlotParms.custom_disp1 = nSlotParms.custom_disp5;
                  nSlotParms.player5 = "";
                  nSlotParms.show5 = 0;
                  nSlotParms.pos5 = 0;
                  nSlotParms.p95 = 0;
                  nSlotParms.custom_disp5 = "";
               }
            }
         }
      }
   }
   if (nSlotParms.player2.equals( "" )) {    // if empty

      if (!nSlotParms.player3.equals( "" )) {    // if not empty

         nSlotParms.player2 = nSlotParms.player3;
         nSlotParms.p2cw = nSlotParms.p3cw;
         nSlotParms.show2 = nSlotParms.show3;
         nSlotParms.pos2 = nSlotParms.pos3;
         nSlotParms.p92 = nSlotParms.p93;
         nSlotParms.custom_disp2 = nSlotParms.custom_disp3;
         nSlotParms.player3 = "";
         nSlotParms.show3 = 0;
         nSlotParms.pos3 = 0;
         nSlotParms.p93 = 0;
         nSlotParms.custom_disp3 = "";

      } else {

         if (!nSlotParms.player4.equals( "" )) {    // if not empty

            nSlotParms.player2 = nSlotParms.player4;
            nSlotParms.p2cw = nSlotParms.p4cw;
            nSlotParms.show2 = nSlotParms.show4;
            nSlotParms.pos2 = nSlotParms.pos4;
            nSlotParms.p92 = nSlotParms.p94;
            nSlotParms.custom_disp2 = nSlotParms.custom_disp4;
            nSlotParms.player4 = "";
            nSlotParms.show4 = 0;
            nSlotParms.pos4 = 0;
            nSlotParms.p94 = 0;
            nSlotParms.custom_disp4 = "";

         } else {

            if (!nSlotParms.player5.equals( "" )) {    // if not empty

               nSlotParms.player2 = nSlotParms.player5;
               nSlotParms.p2cw = nSlotParms.p5cw;
               nSlotParms.show2 = nSlotParms.show5;
               nSlotParms.pos2 = nSlotParms.pos5;
               nSlotParms.p92 = nSlotParms.p95;
               nSlotParms.custom_disp2 = nSlotParms.custom_disp5;
               nSlotParms.player5 = "";
               nSlotParms.show5 = 0;
               nSlotParms.pos5 = 0;
               nSlotParms.p95 = 0;
               nSlotParms.custom_disp5 = "";
            }
         }
      }
   }
   if (nSlotParms.player3.equals( "" )) {    // if empty

      if (!nSlotParms.player4.equals( "" )) {    // if not empty

         nSlotParms.player3 = nSlotParms.player4;
         nSlotParms.p3cw = nSlotParms.p4cw;
         nSlotParms.show3 = nSlotParms.show4;
         nSlotParms.pos3 = nSlotParms.pos4;
         nSlotParms.p93 = nSlotParms.p94;
         nSlotParms.custom_disp3 = nSlotParms.custom_disp4;
         nSlotParms.player4 = "";
         nSlotParms.show4 = 0;
         nSlotParms.pos4 = 0;
         nSlotParms.p94 = 0;
         nSlotParms.custom_disp4 = "";

      } else {

         if (!nSlotParms.player5.equals( "" )) {    // if not empty

            nSlotParms.player3 = nSlotParms.player5;
            nSlotParms.p3cw = nSlotParms.p5cw;
            nSlotParms.show3 = nSlotParms.show5;
            nSlotParms.pos3 = nSlotParms.pos5;
            nSlotParms.p93 = nSlotParms.p95;
            nSlotParms.custom_disp3 = nSlotParms.custom_disp5;
            nSlotParms.player5 = "";
            nSlotParms.show5 = 0;
            nSlotParms.pos5 = 0;
            nSlotParms.p95 = 0;
            nSlotParms.custom_disp5 = "";
         }
      }
   }
   if (nSlotParms.player4.equals( "" )) {    // if empty

      if (!nSlotParms.player5.equals( "" )) {    // if not empty

         nSlotParms.player4 = nSlotParms.player5;
         nSlotParms.p4cw = nSlotParms.p5cw;
         nSlotParms.show4 = nSlotParms.show5;
         nSlotParms.pos4 = nSlotParms.pos5;
         nSlotParms.p94 = nSlotParms.p95;
         nSlotParms.custom_disp4 = nSlotParms.custom_disp5;
         nSlotParms.player5 = "";
         nSlotParms.show5 = 0;
         nSlotParms.pos5 = 0;
         nSlotParms.p95 = 0;
         nSlotParms.custom_disp5 = "";
      }
   }

 }

/**
 //************************************************************************
 //
 //  checkSched
 //
 //    Check if any of the players are already scheduled today 
 //
 //************************************************************************
 **/

 public static void checkSched(parmNSlot nSlotParms, Connection con)
         throws Exception {


   //Statement stmt = null;
   ResultSet rs = null;

   int time2 = 0;
   int fb2 = 0;
   int count = 0;
     
   int max = nSlotParms.rnds;           // max allowed rounds per day for members (club option)      
   int hrsbtwn = nSlotParms.hrsbtwn;    // minumum hours between tee times (club option when rnds > 1)

   String course2 = "";

   boolean hit1 = false;
   boolean hit2 = false;

   try {

      nSlotParms.hit = false;
      nSlotParms.hit2 = false;        // lottery time scheduled for this date
      nSlotParms.hit3 = false;        // multiple tee times too close together
      nSlotParms.player = "";
      nSlotParms.time2 = 0;
      count = 0;

      if ((!nSlotParms.player1.equals( "" )) && (!nSlotParms.player1.equalsIgnoreCase( "x" )) && (nSlotParms.g1.equals( "" ))) {

         PreparedStatement pstmt21 = con.prepareStatement (
            "SELECT time, fb, courseName FROM teecurr2 " +
            "WHERE date = ? AND (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ?) " +
            "ORDER BY time");

         pstmt21.clearParameters();        // clear the parms and check player 1
         pstmt21.setLong(1, nSlotParms.date);
         pstmt21.setString(2, nSlotParms.player1);
         pstmt21.setString(3, nSlotParms.player1);
         pstmt21.setString(4, nSlotParms.player1);
         pstmt21.setString(5, nSlotParms.player1);
         pstmt21.setString(6, nSlotParms.player1);
         rs = pstmt21.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            time2 = rs.getInt("time");
            fb2 = rs.getInt("fb");
            course2 = rs.getString("courseName");

            if ((time2 != nSlotParms.time) || (fb2 != nSlotParms.fb) || (!course2.equals( nSlotParms.course ))) {      // if not this tee time

               if (!nSlotParms.player1.equals( nSlotParms.oldPlayer1 ) &&
                   !nSlotParms.player1.equals( nSlotParms.oldPlayer2 ) &&
                   !nSlotParms.player1.equals( nSlotParms.oldPlayer3 ) &&
                   !nSlotParms.player1.equals( nSlotParms.oldPlayer4 ) &&
                   !nSlotParms.player1.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                  hit1 = false;                               // init hit indicator

/*                  
                  //
                  //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                  //
                  if (nSlotParms.club.equals( "northridge" ) || nSlotParms.club.equals( "roguevalley" )) {

                     if (course2.equals( nSlotParms.course )) {       // error if same course

                        hit1 = true;                       // player already scheduled on this date
                     }

                  } else {        // not North Ridge or Rogue Valley
*/
                     hit1 = true;                       // player already scheduled on this date
 //                 }

                  if (hit1 == true) {                 // if player already scheduled
                    
                     count++;                                    // add to tee time counter for member
                     nSlotParms.player = nSlotParms.player1;       // get player for message
                     nSlotParms.time2 = time2;                    // save time for message
                     nSlotParms.course2 = course2;                // save course for message
                       
                     //
                     //  check if requested tee time is too close to this one
                     //
                     if (max > 1 && hrsbtwn > 0) {

                        if (time2 < nSlotParms.time) {            // if this tee time is before the time requested

                           if (nSlotParms.time < (time2 + (hrsbtwn * 100))) {     // if this tee time is within range

                              nSlotParms.hit3 = true;                       // tee times not far enough apart
                           }

                        } else {                                 // this time is after the requested time

                           if (time2 < (nSlotParms.time + (hrsbtwn * 100))) {     // if this tee time is within range

                              nSlotParms.hit3 = true;                       // tee times not far enough apart
                           }
                        }
                     }
                  }
               }
            }
         }
         pstmt21.close();

         //
         //  check if player already on a lottery request
         //
         PreparedStatement pstmt22 = con.prepareStatement (
            "SELECT courseName FROM lreqs3 " +
            "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ? OR " +
                   "player6 = ? OR player7 = ? OR player8 = ? OR player9 = ? OR player10 = ? OR " +
                   "player11 = ? OR player12 = ? OR player13 = ? OR player14 = ? OR player15 = ? OR " +
                   "player16 = ? OR player17 = ? OR player18 = ? OR player19 = ? OR player20 = ? OR " +
                   "player21 = ? OR player22 = ? OR player23 = ? OR player24 = ? OR player25 = ?) AND date = ? " +
            "ORDER BY time");

         pstmt22.clearParameters();        // clear the parms and check player1
         pstmt22.setString(1, nSlotParms.player1);
         pstmt22.setString(2, nSlotParms.player1);
         pstmt22.setString(3, nSlotParms.player1);
         pstmt22.setString(4, nSlotParms.player1);
         pstmt22.setString(5, nSlotParms.player1);
         pstmt22.setString(6, nSlotParms.player1);
         pstmt22.setString(7, nSlotParms.player1);
         pstmt22.setString(8, nSlotParms.player1);
         pstmt22.setString(9, nSlotParms.player1);
         pstmt22.setString(10, nSlotParms.player1);
         pstmt22.setString(11, nSlotParms.player1);
         pstmt22.setString(12, nSlotParms.player1);
         pstmt22.setString(13, nSlotParms.player1);
         pstmt22.setString(14, nSlotParms.player1);
         pstmt22.setString(15, nSlotParms.player1);
         pstmt22.setString(16, nSlotParms.player1);
         pstmt22.setString(17, nSlotParms.player1);
         pstmt22.setString(18, nSlotParms.player1);
         pstmt22.setString(19, nSlotParms.player1);
         pstmt22.setString(20, nSlotParms.player1);
         pstmt22.setString(21, nSlotParms.player1);
         pstmt22.setString(22, nSlotParms.player1);
         pstmt22.setString(23, nSlotParms.player1);
         pstmt22.setString(24, nSlotParms.player1);
         pstmt22.setString(25, nSlotParms.player1);
         pstmt22.setLong(26, nSlotParms.date);
         rs = pstmt22.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            course2 = rs.getString("courseName");

            if (!nSlotParms.player1.equals( nSlotParms.oldPlayer1 ) &&
                !nSlotParms.player1.equals( nSlotParms.oldPlayer2 ) &&
                !nSlotParms.player1.equals( nSlotParms.oldPlayer3 ) &&
                !nSlotParms.player1.equals( nSlotParms.oldPlayer4 ) &&
                !nSlotParms.player1.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

               hit1 = false;                               // init hit indicator
/*
               //
               //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
               //
               if (nSlotParms.club.equals( "northridge" ) || nSlotParms.club.equals( "roguevalley" )) {

                  if (course2.equals( nSlotParms.course )) {       // error if same course

                     hit1 = true;                       // player already scheduled on this date
                  }

               } else {        // not North Ridge or Rogue Valley
*/
                  hit1 = true;                       // player already scheduled on this date
//             }

               if (hit1 == true) {                 // if player already scheduled

                  count++;                                           // add to tee time counter for member
                  hit2 = true;
                  nSlotParms.player = nSlotParms.player1;              // get player for message
               }
            }
         }
         pstmt22.close();
      }
        
      //
      //  See if we exceeded max allowed for day - if so, set indicator
      //
      if (count >= max) {

         nSlotParms.hit = true;                       // player already scheduled on this date (max times allowed)

         if (hit2 == true) {     // if we hit on lottery

            nSlotParms.hit2 = true;                    // player has a lottery request scheduled on this date
         }
      }
        
      if (nSlotParms.hit == false && nSlotParms.hit3 == false) {   // if we haven't already hit an error

         count = 0;             // init counter
         hit2 = false;

         //
         // check player 2
         //
         if ((!nSlotParms.player2.equals( "" )) && (!nSlotParms.player2.equalsIgnoreCase( "x" )) && (nSlotParms.g2.equals( "" )) && (nSlotParms.hit == false)) {

            PreparedStatement pstmt21 = con.prepareStatement (
               "SELECT time, fb, courseName FROM teecurr2 " +
               "WHERE date = ? AND (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ?) " +
               "ORDER BY time");

            pstmt21.clearParameters();        // clear the parms
            pstmt21.setLong(1, nSlotParms.date);
            pstmt21.setString(2, nSlotParms.player2);
            pstmt21.setString(3, nSlotParms.player2);
            pstmt21.setString(4, nSlotParms.player2);
            pstmt21.setString(5, nSlotParms.player2);
            pstmt21.setString(6, nSlotParms.player2);
            rs = pstmt21.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               time2 = rs.getInt("time");
               fb2 = rs.getInt("fb");
               course2 = rs.getString("courseName");

               if ((time2 != nSlotParms.time) || (fb2 != nSlotParms.fb) || (!course2.equals( nSlotParms.course ))) {      // if not this tee time

                  if (!nSlotParms.player2.equals( nSlotParms.oldPlayer1 ) &&
                      !nSlotParms.player2.equals( nSlotParms.oldPlayer2 ) &&
                      !nSlotParms.player2.equals( nSlotParms.oldPlayer3 ) &&
                      !nSlotParms.player2.equals( nSlotParms.oldPlayer4 ) &&
                      !nSlotParms.player2.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     hit1 = false;                               // init hit indicator
/*
                     //
                     //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                     //
                     if (nSlotParms.club.equals( "northridge" ) || nSlotParms.club.equals( "roguevalley" )) {

                        if (course2.equals( nSlotParms.course )) {       // error if same course

                           hit1 = true;                       // player already scheduled on this date
                        }

                     } else {        // not North Ridge or Rogue Valley
*/
                        hit1 = true;                       // player already scheduled on this date
//                   }

                     if (hit1 == true) {                 // if player already scheduled

                        count++;                                    // add to tee time counter for member
                        nSlotParms.player = nSlotParms.player2;              // get player for message
                        nSlotParms.time2 = time2;                    // save time for message
                        nSlotParms.course2 = course2;                // save course for message

                        //
                        //  check if requested tee time is too close to this one
                        //
                        if (max > 1 && hrsbtwn > 0) {

                           if (time2 < nSlotParms.time) {            // if this tee time is before the time requested

                              if (nSlotParms.time < (time2 + (hrsbtwn * 100))) {     // if this tee time is within range

                                 nSlotParms.hit3 = true;                       // tee times not far enough apart
                              }

                           } else {                                 // this time is after the requested time

                              if (time2 < (nSlotParms.time + (hrsbtwn * 100))) {     // if this tee time is within range

                                 nSlotParms.hit3 = true;                       // tee times not far enough apart
                              }
                           }
                        }
                     }
                  }
               }
            }
            pstmt21.close();

            //
            //  check if player already on a lottery request
            //
            PreparedStatement pstmt22 = con.prepareStatement (
               "SELECT courseName FROM lreqs3 " +
               "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ? OR " +
                      "player6 = ? OR player7 = ? OR player8 = ? OR player9 = ? OR player10 = ? OR " +
                      "player11 = ? OR player12 = ? OR player13 = ? OR player14 = ? OR player15 = ? OR " +
                      "player16 = ? OR player17 = ? OR player18 = ? OR player19 = ? OR player20 = ? OR " +
                      "player21 = ? OR player22 = ? OR player23 = ? OR player24 = ? OR player25 = ?) AND date = ? " +
               "ORDER BY time");

            pstmt22.clearParameters();        // clear the parms and check player2
            pstmt22.setString(1, nSlotParms.player2);
            pstmt22.setString(2, nSlotParms.player2);
            pstmt22.setString(3, nSlotParms.player2);
            pstmt22.setString(4, nSlotParms.player2);
            pstmt22.setString(5, nSlotParms.player2);
            pstmt22.setString(6, nSlotParms.player2);
            pstmt22.setString(7, nSlotParms.player2);
            pstmt22.setString(8, nSlotParms.player2);
            pstmt22.setString(9, nSlotParms.player2);
            pstmt22.setString(10, nSlotParms.player2);
            pstmt22.setString(11, nSlotParms.player2);
            pstmt22.setString(12, nSlotParms.player2);
            pstmt22.setString(13, nSlotParms.player2);
            pstmt22.setString(14, nSlotParms.player2);
            pstmt22.setString(15, nSlotParms.player2);
            pstmt22.setString(16, nSlotParms.player2);
            pstmt22.setString(17, nSlotParms.player2);
            pstmt22.setString(18, nSlotParms.player2);
            pstmt22.setString(19, nSlotParms.player2);
            pstmt22.setString(20, nSlotParms.player2);
            pstmt22.setString(21, nSlotParms.player2);
            pstmt22.setString(22, nSlotParms.player2);
            pstmt22.setString(23, nSlotParms.player2);
            pstmt22.setString(24, nSlotParms.player2);
            pstmt22.setString(25, nSlotParms.player2);
            pstmt22.setLong(26, nSlotParms.date);
            rs = pstmt22.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               course2 = rs.getString("courseName");

               if (!nSlotParms.player2.equals( nSlotParms.oldPlayer1 ) &&
                   !nSlotParms.player2.equals( nSlotParms.oldPlayer2 ) &&
                   !nSlotParms.player2.equals( nSlotParms.oldPlayer3 ) &&
                   !nSlotParms.player2.equals( nSlotParms.oldPlayer4 ) &&
                   !nSlotParms.player2.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                  hit1 = false;                               // init hit indicator
/*
                  //
                  //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                  //
                  if (nSlotParms.club.equals( "northridge" ) || nSlotParms.club.equals( "roguevalley" )) {

                     if (course2.equals( nSlotParms.course )) {       // error if same course

                        hit1 = true;                       // player already scheduled on this date
                     }

                  } else {        // not North Ridge or Rogue Valley
*/
                     hit1 = true;                       // player already scheduled on this date
//                }

                  if (hit1 == true) {                 // if player already scheduled

                     count++;                                           // add to tee time counter for member
                     hit2 = true;
                     nSlotParms.player = nSlotParms.player2;              // get player for message
                  }
               }
            }
            pstmt22.close();
         }

         //
         //  See if we exceeded max allowed for day - if so, set indicator
         //
         if (count >= max) {

            nSlotParms.hit = true;                       // player already scheduled on this date (max times allowed)

            if (hit2 == true) {     // if we hit on lottery

               nSlotParms.hit2 = true;                    // player has a lottery request scheduled on this date
            }
         }
      }
        
      if (nSlotParms.hit == false && nSlotParms.hit3 == false) {   // if we haven't already hit an error

         count = 0;             // init counter
         hit2 = false;

         //
         // check player 3
         //
         if ((!nSlotParms.player3.equals( "" )) && (!nSlotParms.player3.equalsIgnoreCase( "x" )) && (nSlotParms.g3.equals( "" )) && (nSlotParms.hit == false)) {

            PreparedStatement pstmt21 = con.prepareStatement (
               "SELECT time, fb, courseName FROM teecurr2 " +
               "WHERE date = ? AND (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ?) " +
               "ORDER BY time");

            pstmt21.clearParameters();        // clear the parms
            pstmt21.setLong(1, nSlotParms.date);
            pstmt21.setString(2, nSlotParms.player3);
            pstmt21.setString(3, nSlotParms.player3);
            pstmt21.setString(4, nSlotParms.player3);
            pstmt21.setString(5, nSlotParms.player3);
            pstmt21.setString(6, nSlotParms.player3);
            rs = pstmt21.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               time2 = rs.getInt("time");
               fb2 = rs.getInt("fb");
               course2 = rs.getString("courseName");

               if ((time2 != nSlotParms.time) || (fb2 != nSlotParms.fb) || (!course2.equals( nSlotParms.course ))) {      // if not this tee time

                  if (!nSlotParms.player3.equals( nSlotParms.oldPlayer1 ) &&
                      !nSlotParms.player3.equals( nSlotParms.oldPlayer2 ) &&
                      !nSlotParms.player3.equals( nSlotParms.oldPlayer3 ) &&
                      !nSlotParms.player3.equals( nSlotParms.oldPlayer4 ) &&
                      !nSlotParms.player3.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     hit1 = false;                               // init hit indicator
/*
                     //
                     //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                     //
                     if (nSlotParms.club.equals( "northridge" ) || nSlotParms.club.equals( "roguevalley" )) {

                        if (course2.equals( nSlotParms.course )) {       // error if same course

                           hit1 = true;                       // player already scheduled on this date
                        }

                     } else {        // not North Ridge or Rogue Valley
*/
                        hit1 = true;                       // player already scheduled on this date
//                   }

                     if (hit1 == true) {                 // if player already scheduled

                        count++;                                    // add to tee time counter for member
                        nSlotParms.player = nSlotParms.player3;              // get player for message
                        nSlotParms.time2 = time2;                    // save time for message
                        nSlotParms.course2 = course2;                // save course for message

                        //
                        //  check if requested tee time is too close to this one
                        //
                        if (max > 1 && hrsbtwn > 0) {

                           if (time2 < nSlotParms.time) {            // if this tee time is before the time requested

                              if (nSlotParms.time < (time2 + (hrsbtwn * 100))) {     // if this tee time is within range

                                 nSlotParms.hit3 = true;                       // tee times not far enough apart
                              }

                           } else {                                 // this time is after the requested time

                              if (time2 < (nSlotParms.time + (hrsbtwn * 100))) {     // if this tee time is within range

                                 nSlotParms.hit3 = true;                       // tee times not far enough apart
                              }
                           }
                        }
                     }
                  }
               }
            }
            pstmt21.close();

            //
            //  check if player already on a lottery request
            //
            PreparedStatement pstmt22 = con.prepareStatement (
               "SELECT courseName FROM lreqs3 " +
               "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ? OR " +
                      "player6 = ? OR player7 = ? OR player8 = ? OR player9 = ? OR player10 = ? OR " +
                      "player11 = ? OR player12 = ? OR player13 = ? OR player14 = ? OR player15 = ? OR " +
                      "player16 = ? OR player17 = ? OR player18 = ? OR player19 = ? OR player20 = ? OR " +
                      "player21 = ? OR player22 = ? OR player23 = ? OR player24 = ? OR player25 = ?) AND date = ? " +
               "ORDER BY time");

            pstmt22.clearParameters();        // clear the parms and check player3
            pstmt22.setString(1, nSlotParms.player3);
            pstmt22.setString(2, nSlotParms.player3);
            pstmt22.setString(3, nSlotParms.player3);
            pstmt22.setString(4, nSlotParms.player3);
            pstmt22.setString(5, nSlotParms.player3);
            pstmt22.setString(6, nSlotParms.player3);
            pstmt22.setString(7, nSlotParms.player3);
            pstmt22.setString(8, nSlotParms.player3);
            pstmt22.setString(9, nSlotParms.player3);
            pstmt22.setString(10, nSlotParms.player3);
            pstmt22.setString(11, nSlotParms.player3);
            pstmt22.setString(12, nSlotParms.player3);
            pstmt22.setString(13, nSlotParms.player3);
            pstmt22.setString(14, nSlotParms.player3);
            pstmt22.setString(15, nSlotParms.player3);
            pstmt22.setString(16, nSlotParms.player3);
            pstmt22.setString(17, nSlotParms.player3);
            pstmt22.setString(18, nSlotParms.player3);
            pstmt22.setString(19, nSlotParms.player3);
            pstmt22.setString(20, nSlotParms.player3);
            pstmt22.setString(21, nSlotParms.player3);
            pstmt22.setString(22, nSlotParms.player3);
            pstmt22.setString(23, nSlotParms.player3);
            pstmt22.setString(24, nSlotParms.player3);
            pstmt22.setString(25, nSlotParms.player3);
            pstmt22.setLong(26, nSlotParms.date);
            rs = pstmt22.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               course2 = rs.getString("courseName");

               if (!nSlotParms.player3.equals( nSlotParms.oldPlayer1 ) &&
                   !nSlotParms.player3.equals( nSlotParms.oldPlayer2 ) &&
                   !nSlotParms.player3.equals( nSlotParms.oldPlayer3 ) &&
                   !nSlotParms.player3.equals( nSlotParms.oldPlayer4 ) &&
                   !nSlotParms.player3.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                  hit1 = false;                               // init hit indicator
/*
                  //
                  //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                  //
                  if (nSlotParms.club.equals( "northridge" ) || nSlotParms.club.equals( "roguevalley" )) {

                     if (course2.equals( nSlotParms.course )) {       // error if same course

                        hit1 = true;                       // player already scheduled on this date
                     }

                  } else {        // not North Ridge or Rogue Valley
*/
                     hit1 = true;                       // player already scheduled on this date
//                }

                  if (hit1 == true) {                 // if player already scheduled

                     count++;                                    // add to tee time counter for member
                     hit2 = true;
                     nSlotParms.player = nSlotParms.player3;              // get player for message
                  }
               }
            }
            pstmt22.close();
         }

         //
         //  See if we exceeded max allowed for day - if so, set indicator
         //
         if (count >= max) {

            nSlotParms.hit = true;                       // player already scheduled on this date (max times allowed)

            if (hit2 == true) {     // if we hit on lottery

               nSlotParms.hit2 = true;                    // player has a lottery request scheduled on this date
            }
         }
      }
      if (nSlotParms.hit == false && nSlotParms.hit3 == false) {   // if we haven't already hit an error

         count = 0;             // init counter
         hit2 = false;

         //
         // check player 4
         //
         if ((!nSlotParms.player4.equals( "" )) && (!nSlotParms.player4.equalsIgnoreCase( "x" )) && (nSlotParms.g4.equals( "" )) && (nSlotParms.hit == false)) {

            PreparedStatement pstmt21 = con.prepareStatement (
               "SELECT time, fb, courseName FROM teecurr2 " +
               "WHERE date = ? AND (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ?) " +
               "ORDER BY time");

            pstmt21.clearParameters();        // clear the parms
            pstmt21.setLong(1, nSlotParms.date);
            pstmt21.setString(2, nSlotParms.player4);
            pstmt21.setString(3, nSlotParms.player4);
            pstmt21.setString(4, nSlotParms.player4);
            pstmt21.setString(5, nSlotParms.player4);
            pstmt21.setString(6, nSlotParms.player4);
            rs = pstmt21.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               time2 = rs.getInt("time");
               fb2 = rs.getInt("fb");
               course2 = rs.getString("courseName");

               if ((time2 != nSlotParms.time) || (fb2 != nSlotParms.fb) || (!course2.equals( nSlotParms.course ))) {      // if not this tee time

                  if (!nSlotParms.player4.equals( nSlotParms.oldPlayer1 ) &&
                      !nSlotParms.player4.equals( nSlotParms.oldPlayer2 ) &&
                      !nSlotParms.player4.equals( nSlotParms.oldPlayer3 ) &&
                      !nSlotParms.player4.equals( nSlotParms.oldPlayer4 ) &&
                      !nSlotParms.player4.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     hit1 = false;                               // init hit indicator
/*
                     //
                     //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                     //
                     if (nSlotParms.club.equals( "northridge" ) || nSlotParms.club.equals( "roguevalley" )) {

                        if (course2.equals( nSlotParms.course )) {       // error if same course

                           hit1 = true;                       // player already scheduled on this date
                        }

                     } else {        // not North Ridge or Rogue Valley
*/
                        hit1 = true;                       // player already scheduled on this date
//                   }

                     if (hit1 == true) {                 // if player already scheduled

                        count++;                                    // add to tee time counter for member
                        nSlotParms.player = nSlotParms.player4;              // get player for message
                        nSlotParms.time2 = time2;                    // save time for message
                        nSlotParms.course2 = course2;                // save course for message

                        //
                        //  check if requested tee time is too close to this one
                        //
                        if (max > 1 && hrsbtwn > 0) {

                           if (time2 < nSlotParms.time) {            // if this tee time is before the time requested

                              if (nSlotParms.time < (time2 + (hrsbtwn * 100))) {     // if this tee time is within range

                                 nSlotParms.hit3 = true;                       // tee times not far enough apart
                              }

                           } else {                                 // this time is after the requested time

                              if (time2 < (nSlotParms.time + (hrsbtwn * 100))) {     // if this tee time is within range

                                 nSlotParms.hit3 = true;                       // tee times not far enough apart
                              }
                           }
                        }
                     }
                  }
               }
            }
            pstmt21.close();

            //
            //  check if player already on a lottery request
            //
            PreparedStatement pstmt22 = con.prepareStatement (
               "SELECT courseName FROM lreqs3 " +
               "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ? OR " +
                      "player6 = ? OR player7 = ? OR player8 = ? OR player9 = ? OR player10 = ? OR " +
                      "player11 = ? OR player12 = ? OR player13 = ? OR player14 = ? OR player15 = ? OR " +
                      "player16 = ? OR player17 = ? OR player18 = ? OR player19 = ? OR player20 = ? OR " +
                      "player21 = ? OR player22 = ? OR player23 = ? OR player24 = ? OR player25 = ?) AND date = ? " +
               "ORDER BY time");

            pstmt22.clearParameters();        // clear the parms and check player4
            pstmt22.setString(1, nSlotParms.player4);
            pstmt22.setString(2, nSlotParms.player4);
            pstmt22.setString(3, nSlotParms.player4);
            pstmt22.setString(4, nSlotParms.player4);
            pstmt22.setString(5, nSlotParms.player4);
            pstmt22.setString(6, nSlotParms.player4);
            pstmt22.setString(7, nSlotParms.player4);
            pstmt22.setString(8, nSlotParms.player4);
            pstmt22.setString(9, nSlotParms.player4);
            pstmt22.setString(10, nSlotParms.player4);
            pstmt22.setString(11, nSlotParms.player4);
            pstmt22.setString(12, nSlotParms.player4);
            pstmt22.setString(13, nSlotParms.player4);
            pstmt22.setString(14, nSlotParms.player4);
            pstmt22.setString(15, nSlotParms.player4);
            pstmt22.setString(16, nSlotParms.player4);
            pstmt22.setString(17, nSlotParms.player4);
            pstmt22.setString(18, nSlotParms.player4);
            pstmt22.setString(19, nSlotParms.player4);
            pstmt22.setString(20, nSlotParms.player4);
            pstmt22.setString(21, nSlotParms.player4);
            pstmt22.setString(22, nSlotParms.player4);
            pstmt22.setString(23, nSlotParms.player4);
            pstmt22.setString(24, nSlotParms.player4);
            pstmt22.setString(25, nSlotParms.player4);
            pstmt22.setLong(26, nSlotParms.date);
            rs = pstmt22.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               course2 = rs.getString("courseName");

               if (!nSlotParms.player4.equals( nSlotParms.oldPlayer1 ) &&
                   !nSlotParms.player4.equals( nSlotParms.oldPlayer2 ) &&
                   !nSlotParms.player4.equals( nSlotParms.oldPlayer3 ) &&
                   !nSlotParms.player4.equals( nSlotParms.oldPlayer4 ) &&
                   !nSlotParms.player4.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                  hit1 = false;                               // init hit indicator
/*
                  //
                  //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                  //
                  if (nSlotParms.club.equals( "northridge" ) || nSlotParms.club.equals( "roguevalley" )) {

                     if (course2.equals( nSlotParms.course )) {       // error if same course

                        hit1 = true;                       // player already scheduled on this date
                     }

                  } else {        // not North Ridge or Rogue Valley
*/
                     hit1 = true;                       // player already scheduled on this date
//                }

                  if (hit1 == true) {                 // if player already scheduled

                     count++;                                    // add to tee time counter for member
                     hit2 = true;
                     nSlotParms.player = nSlotParms.player4;              // get player for message
                  }
               }
            }
            pstmt22.close();
         }

         //
         //  See if we exceeded max allowed for day - if so, set indicator
         //
         if (count >= max) {

            nSlotParms.hit = true;                       // player already scheduled on this date (max times allowed)

            if (hit2 == true) {     // if we hit on lottery

               nSlotParms.hit2 = true;                    // player has a lottery request scheduled on this date
            }
         }
      }
      if (nSlotParms.hit == false && nSlotParms.hit3 == false) {   // if we haven't already hit an error

         count = 0;             // init counter
         hit2 = false;

         //
         // check player 5
         //
         if ((!nSlotParms.player5.equals( "" )) && (!nSlotParms.player5.equalsIgnoreCase( "x" )) && (nSlotParms.g5.equals( "" )) && (nSlotParms.hit == false)) {

            PreparedStatement pstmt21 = con.prepareStatement (
               "SELECT time, fb, courseName FROM teecurr2 " +
               "WHERE date = ? AND (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ?) " +
               "ORDER BY time");

            pstmt21.clearParameters();        // clear the parms
            pstmt21.setLong(1, nSlotParms.date);
            pstmt21.setString(2, nSlotParms.player5);
            pstmt21.setString(3, nSlotParms.player5);
            pstmt21.setString(4, nSlotParms.player5);
            pstmt21.setString(5, nSlotParms.player5);
            pstmt21.setString(6, nSlotParms.player5);
            rs = pstmt21.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               time2 = rs.getInt("time");
               fb2 = rs.getInt("fb");
               course2 = rs.getString("courseName");

               if ((time2 != nSlotParms.time) || (fb2 != nSlotParms.fb) || (!course2.equals( nSlotParms.course ))) {      // if not this tee time

                  if (!nSlotParms.player5.equals( nSlotParms.oldPlayer1 ) &&
                      !nSlotParms.player5.equals( nSlotParms.oldPlayer2 ) &&
                      !nSlotParms.player5.equals( nSlotParms.oldPlayer3 ) &&
                      !nSlotParms.player5.equals( nSlotParms.oldPlayer4 ) &&
                      !nSlotParms.player5.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                     hit1 = false;                               // init hit indicator
/*
                     //
                     //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                     //
                     if (nSlotParms.club.equals( "northridge" ) || nSlotParms.club.equals( "roguevalley" )) {

                        if (course2.equals( nSlotParms.course )) {       // error if same course

                           hit1 = true;                       // player already scheduled on this date
                        }

                     } else {        // not North Ridge or Rogue Valley
*/
                        hit1 = true;                       // player already scheduled on this date
//                   }

                     if (hit1 == true) {                 // if player already scheduled

                        count++;                                    // add to tee time counter for member
                        nSlotParms.player = nSlotParms.player5;              // get player for message
                        nSlotParms.time2 = time2;                    // save time for message
                        nSlotParms.course2 = course2;                // save course for message

                        //
                        //  check if requested tee time is too close to this one
                        //
                        if (max > 1 && hrsbtwn > 0) {

                           if (time2 < nSlotParms.time) {            // if this tee time is before the time requested

                              if (nSlotParms.time < (time2 + (hrsbtwn * 100))) {     // if this tee time is within range

                                 nSlotParms.hit3 = true;                       // tee times not far enough apart
                              }

                           } else {                                 // this time is after the requested time

                              if (time2 < (nSlotParms.time + (hrsbtwn * 100))) {     // if this tee time is within range

                                 nSlotParms.hit3 = true;                       // tee times not far enough apart
                              }
                           }
                        }
                     }
                  }
               }
            }
            pstmt21.close();

            //
            //  check if player already on a lottery request
            //
            PreparedStatement pstmt22 = con.prepareStatement (
               "SELECT courseName FROM lreqs3 " +
               "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ? OR " +
                      "player6 = ? OR player7 = ? OR player8 = ? OR player9 = ? OR player10 = ? OR " +
                      "player11 = ? OR player12 = ? OR player13 = ? OR player14 = ? OR player15 = ? OR " +
                      "player16 = ? OR player17 = ? OR player18 = ? OR player19 = ? OR player20 = ? OR " +
                      "player21 = ? OR player22 = ? OR player23 = ? OR player24 = ? OR player25 = ?) AND date = ? " +
               "ORDER BY time");

            pstmt22.clearParameters();        // clear the parms and check player5
            pstmt22.setString(1, nSlotParms.player5);
            pstmt22.setString(2, nSlotParms.player5);
            pstmt22.setString(3, nSlotParms.player5);
            pstmt22.setString(4, nSlotParms.player5);
            pstmt22.setString(5, nSlotParms.player5);
            pstmt22.setString(6, nSlotParms.player5);
            pstmt22.setString(7, nSlotParms.player5);
            pstmt22.setString(8, nSlotParms.player5);
            pstmt22.setString(9, nSlotParms.player5);
            pstmt22.setString(10, nSlotParms.player5);
            pstmt22.setString(11, nSlotParms.player5);
            pstmt22.setString(12, nSlotParms.player5);
            pstmt22.setString(13, nSlotParms.player5);
            pstmt22.setString(14, nSlotParms.player5);
            pstmt22.setString(15, nSlotParms.player5);
            pstmt22.setString(16, nSlotParms.player5);
            pstmt22.setString(17, nSlotParms.player5);
            pstmt22.setString(18, nSlotParms.player5);
            pstmt22.setString(19, nSlotParms.player5);
            pstmt22.setString(20, nSlotParms.player5);
            pstmt22.setString(21, nSlotParms.player5);
            pstmt22.setString(22, nSlotParms.player5);
            pstmt22.setString(23, nSlotParms.player5);
            pstmt22.setString(24, nSlotParms.player5);
            pstmt22.setString(25, nSlotParms.player5);
            pstmt22.setLong(26, nSlotParms.date);
            rs = pstmt22.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               course2 = rs.getString("courseName");

               if (!nSlotParms.player5.equals( nSlotParms.oldPlayer1 ) &&
                   !nSlotParms.player5.equals( nSlotParms.oldPlayer2 ) &&
                   !nSlotParms.player5.equals( nSlotParms.oldPlayer3 ) &&
                   !nSlotParms.player5.equals( nSlotParms.oldPlayer4 ) &&
                   !nSlotParms.player5.equals( nSlotParms.oldPlayer5 )) {   // if name not already accepted by pro

                  hit1 = false;                               // init hit indicator
/*
                  //
                  //  If club is North Ridge or Rogue Valley, skip if different courses (1 is practice)
                  //
                  if (nSlotParms.club.equals( "northridge" ) || nSlotParms.club.equals( "roguevalley" )) {

                     if (course2.equals( nSlotParms.course )) {       // error if same course

                        hit1 = true;                       // player already scheduled on this date
                     }

                  } else {        // not North Ridge or Rogue Valley
*/
                     hit1 = true;                       // player already scheduled on this date
//                }

                  if (hit1 == true) {                 // if player already scheduled

                     count++;                                    // add to tee time counter for member
                     hit2 = true;
                     nSlotParms.player = nSlotParms.player5;              // get player for message
                  }
               }
            }
            pstmt22.close();
         }

         //
         //  See if we exceeded max allowed for day - if so, set indicator
         //
         if (count >= max) {

            nSlotParms.hit = true;                       // player already scheduled on this date (max times allowed)

            if (hit2 == true) {     // if we hit on lottery

               nSlotParms.hit2 = true;                    // player has a lottery request scheduled on this date
            }
         }
      }

   }
   catch (Exception e) {

      throw new Exception("Error checking if Player Already Scheduled - verifyNSlot.checkSched " + e.getMessage());
   }

   return;
 }

}