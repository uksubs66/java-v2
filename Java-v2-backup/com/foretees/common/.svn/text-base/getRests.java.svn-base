/***************************************************************************************
 *   getRests:  This servlet will locate and save all member restrictions for the given
 *              date and user.  This will prevent Member_sheet from having to check for
 *              a restriction at each tee time.
 *
 *   called by:  Member_sheet
 *
 *   created:  8/02/2004   Bob P.
 *
 *
 *   last updated:
 *
 *      8/06/13 BSK  Added additional processing to showRest for FlxRez which takes locations into account to properly display legend items when sub-activities are used.
 *      3/27/13 RDP  Trim the mship and mtype values so they compare correctly - in case either contains extra spaces or tabs.
 *      3/25/13 RDP  Add 'lifted' boolean for custom to lift restrictions x days in advance (this boolean will prevent the restriction from showing on the tee sheet).
 *      3/16/13 RDP  Pass parm.day to verifyCustom.checkRestLift.
 *      3/14/13 RDP  Add custom to getAll for Interlachen to skip restriction if within 48 hours (calls verifyCustom.checkRestLift).
 *     12/07/12 BSK  Updated getAll() to also populate the allow_lesson array in parmRest
 *      3/22/11 BSK  Updated getAll() to also populate the new showit array in parmRest
 *      3/17/11 BSK  Updated getAll() to also populate the new susp_locations array in parmRest
 *     10/19/10 BSK  Fix for single course suspsensions not causing the Tee Sheet Legend button to be hidden when viewing -ALL- courses
 *      3/28/10 RDP  Palo Alto Hills - do not include restriction if subtype is 18 Holer (case 1785).
 *      3/29/10 BSK  Added courseName to rest_suspend query so suspensions that only apply to one course don't get pulled for all courses.
 *     10/07/09 RDP  Add check for Activities
 *      1/10/05 RDP  Add checkRests to check if a restriction exists for ALL member types.
 *     11/05/04 RDP  Allow for parm.course to be -ALL-, get all restrictions.
 *
 ***************************************************************************************
 */


package com.foretees.common;

import java.util.*;
import java.sql.*;
import javax.servlet.*;


public class getRests {


/**
 //************************************************************************
 //
 //  Get the Rests parms
 //
 //************************************************************************
 **/

 public static void getAll(Connection con, parmRest parm)
         throws Exception {


   PreparedStatement pstmt1 = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int ind = 0;
   int stime = 0;
   int etime = 0;
   int count = 0;
   int mrest_id = 0;
   int allow_lesson = 0;
   //int applies = 0;
   //int act_id = 0;

   int memLimit = Labels.MAX_MEMS;
   int mshipLimit = Labels.MAX_MSHIPS;

   String rest_name = "";
   String rest_recurr = "";
   String rest_color = "";
   String rest_course = "";
   String rest_fb = "";
   String locations = "";
   String showit = "";
   String mship = "";
   String mtype = "";

   String [] mtypeA = new String [Labels.MAX_MEMS];               // array to hold the member types
   String [] mshipA = new String [Labels.MAX_MSHIPS];             // array to hold the membership types
   
   boolean skipit = false;
   boolean lifted = false;
   
   
   //
   //  First, init the parm block
   //
   for (i=0; i<parm.MAX; i++) {
     
      parm.restName[i] = "";
      parm.courseName[i] = "";
      parm.color[i] = "";
      parm.fb[i] = "";
      parm.locations[i] = "";
      parm.showit[i] = "";
      parm.stime[i] = 0;
      parm.etime[i] = 0;
      parm.applies[i] = 0;
      parm.rest_id[i] = 0;
      parm.allow_lesson[i] = 0;
      
      for (int j=0; j<parm.MAX; j++) {
          parm.susp[i][j][0] = 0;
          parm.susp[i][j][1] = 0;
          parm.susp_locations[i][j][0] = "";
      }
   }
   
   //
   //  Trim the user's mtype and mship to make sure they compare correctly agains the values in the restriction
   //
   parm.mship = parm.mship.trim();
   parm.mtype = parm.mtype.trim();
   

   //
   //  get the Rests's for this date and user
   //
   try {

      if (parm.activity_id > 0) {       // if NOT golf, then check for all restrictions for this parm.activity_id (sess_activity_id)
         
            pstmt1 = con.prepareStatement (
               "SELECT * FROM restriction2 WHERE sdate <= ? AND edate >= ? AND activity_id = '" + parm.activity_id + "'");
         
      } else {

         if (parm.course.equals( "-ALL-" )) {

            pstmt1 = con.prepareStatement (
               "SELECT * FROM restriction2 WHERE sdate <= ? AND edate >= ? AND activity_id = 0");

         } else {

            pstmt1 = con.prepareStatement (
               "SELECT * FROM restriction2 WHERE sdate <= ? AND edate >= ? AND activity_id = 0 AND " +
               "(courseName = ? OR courseName = '-ALL-')");
         }
      }

      pstmt1.clearParameters();          // clear the parms
      pstmt1.setLong(1, parm.date);
      pstmt1.setLong(2, parm.date);

      if (!parm.course.equals( "-ALL-" ) && parm.activity_id == 0) {
         pstmt1.setString(3, parm.course);
      }

      rs = pstmt1.executeQuery();      // find all matching restrictions, if any

      i = 0;                           // init index for restrictions in parm block

      while (rs.next() && count < parm.MAX) {   // check all matching restrictions for this day, mship, mtype (max of 40)

         stime = rs.getInt("stime");
         etime = rs.getInt("etime");
         rest_recurr = rs.getString("recurr");
         i2 = 1;
         for (i3=0; i3<memLimit; i3++) {
            mtype = rs.getString("mem" +i2);
            mtypeA[i3] = mtype.trim();        // trim for compare below
            i2++;
         }
         i2 = 1;
         for (i3=0; i3<mshipLimit; i3++) {
            mship = rs.getString("mship" +i2);
            mshipA[i3] = mship.trim();        // trim for compare below
            i2++;
         }       
         rest_color = rs.getString("color");
         rest_name = rs.getString("name");
         rest_course = rs.getString("courseName");
         rest_fb = rs.getString("fb");
         mrest_id = rs.getInt("id");
         locations = rs.getString("locations");
         showit = rs.getString("showit");
         allow_lesson = rs.getInt("allow_lesson");
         
         skipit = false;
         lifted = false; 
         
         //
         //  Check for any customs that lift this restriction x hours prior to the date requested
         //
         skipit = verifyCustom.checkRestLift(parm.date, parm.day, rest_name, parm.club, parm.activity_id, con); 

         if (skipit == true && !parm.club.equals("interlachen") && !parm.club.equals("spokane")) {
             
             lifted = true;      // restriction has been lifted - do not show it on the tee sheet or enforce it
         }
         
         if (skipit == true && parm.club.equals("spokane") && (parm.mtype.equals("Adult Female") || parm.mtype.equals("Male Spouse"))) {
             
             lifted = true;      // restriction has been lifted - do not show it on the tee sheet or enforce it
         }

         //
         //  Custom for Palo Alto Hills - skip rest if 18 Holer (subtype)
         //
         if (!parm.user.startsWith("proshop") && parm.club.equals("paloaltohills") && 
              parm.msubtype.equals("18 Holer") && rest_name.equals("18 Hole Ladies")) {
            
            skipit = true;    // skip this restriction - does not apply to this member
         }
         
         
         if (lifted == false) {        // if restriction has not been lifted (custom)

            //
            //  We must check the recurrence for this day (Monday, etc.)
            //
            if ((rest_recurr.equalsIgnoreCase( "Every " + parm.day )) ||          // if this day
                (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
                ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
                (!parm.day.equalsIgnoreCase( "saturday" )) &&
                (!parm.day.equalsIgnoreCase( "sunday" ))) ||
                ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
                (parm.day.equalsIgnoreCase( "saturday" ))) ||
                ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
                (parm.day.equalsIgnoreCase( "sunday" )))) {


                if (!parm.user.startsWith("proshop") && skipit == false) {

                    //
                    //  Found a restriction that matched course/root activity & date (in query) and day (directly above)
                    //  now check mtype & mship of this member
                    //
                    ind = 0;

                    loop1:
                    while (ind < memLimit) {

                        if (parm.mship.equals( mshipA[ind] ) || parm.mtype.equals( mtypeA[ind] )) {

                            parm.applies[i] = 1;

                            break loop1;
                        }
                        ind++;
                    }
                }

                //
                //  restriction found - save it in the rest parm block
                //                
                parm.restName[i] = rest_name;
                parm.courseName[i] = rest_course;
                parm.fb[i] = rest_fb;
                parm.color[i] = rest_color;
                parm.stime[i] = stime;
                parm.etime[i] = etime;
                parm.locations[i] = locations;
                parm.showit[i] = showit;
                parm.rest_id[i] = mrest_id;
                parm.allow_lesson[i] = allow_lesson;

                //
                //  Check for and store all start/end times of suspensions that apply to this restriction and this day
                //
                if (!parm.course.equals("-ALL-") && parm.activity_id == 0) {
                    
                    pstmt2 = con.prepareStatement(
                        "SELECT stime, etime, courseName, locations FROM rest_suspend WHERE mrest_id = ? AND sdate <= ? AND " +
                        "edate >= ? AND " + parm.day.toLowerCase() + "=1 AND " +
                        "(eo_week = 0 OR (MOD(DATE_FORMAT(sdate, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2))) AND " +
                        "(courseName = ? OR courseName = '-ALL-')");
                    
                } else {
                    
                    pstmt2 = con.prepareStatement(
                        "SELECT stime, etime, courseName, locations FROM rest_suspend WHERE mrest_id = ? AND sdate <= ? AND " +
                        "edate >= ? AND " + parm.day.toLowerCase() + "=1 AND " +
                        "(eo_week = 0 OR (MOD(DATE_FORMAT(sdate, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2)))");
                }

                pstmt2.clearParameters();
                pstmt2.setInt(1, mrest_id);
                pstmt2.setInt(2, (int)parm.date);
                pstmt2.setInt(3, (int)parm.date);
                pstmt2.setInt(4, (int)parm.date);
                if (!parm.course.equals("-ALL-") && parm.activity_id == 0) {
                    pstmt2.setString(5, parm.course);
                }
                rs2 = pstmt2.executeQuery();

                int j=0;
                while (rs2.next() && j<parm.MAX) {
                    
                    parm.susp[i][j][0] = rs2.getInt("stime");
                    parm.susp[i][j][1] = rs2.getInt("etime");
                    if (parm.activity_id == 0) {
                        parm.susp_locations[i][j][0] = rs2.getString("courseName");
                    } else {
                        parm.susp_locations[i][j][0] = rs2.getString("locations");
                    }
                    j++;
                }

                pstmt2.close();

                i++;     // next rest
                count++;

            }     // end of 'day' if
            
         }        // end of IF lifted
         
      }       // end of while (no more restrictions)

      pstmt1.close();

   }
   catch (Exception e) {

      throw new UnavailableException("Error getting restriction info - getRests.getAll " + e.getMessage());
   }
 }


/**
 //************************************************************************
 //
 //  Check Rests for ALL member types or ALL membership types (NOT USED ??????)
 //
 //************************************************************************
 **/

 public static boolean checkRests(long date, int time, int fb, String course, String day, Connection con)
         throws Exception {


   PreparedStatement pstmt1 = null;
   ResultSet rs = null;

   boolean status = false;
   //boolean found = false;
   boolean suspend = false;

   int i = 0;
   int i2 = 0;
   //int ind = 0;
   //int count = 0;
   int mrest_id = 0;
   int memLimit = Labels.MAX_MEMS;
   int mshipLimit = Labels.MAX_MSHIPS;

   String rest_recurr = "";
   String rfb = "";
   String bfb = "Both";

   String [] mtypeA = new String [Labels.MAX_MEMS];             //  member types allowed
   String [] mtypeS = new String [Labels.MAX_MEMS];             //  member types specified
   String [] mshipA = new String [Labels.MAX_MSHIPS];           //  membership types allowed
   String [] mshipS = new String [Labels.MAX_MSHIPS];           //  membership types specified

   //
   //   Check for any events during this time
   //
   rfb = "Front";
   if (fb == 1) {       // if back requested

      rfb = "Back";
   }

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // this method doesn't seem to be used by FlxRez so we'll pass a zero for the root activity_id

   //
   //   Get the guest names specified for this club
   //
   try {

      getClub.getParms(con, parm);        // get the club parms

   }
   catch (Exception e2) {

      throw new UnavailableException("Error getting club parms - getRests.checkRests " + e2.getMessage());
   }

   for (i=0; i<memLimit; i++) {         // get the member types and mship types allowed
      mtypeA[i] = parm.mem[i];
   }
   for (i=0; i<mshipLimit; i++) {
      mshipA[i] = parm.mship[i];
   }

   //
   //  get the Rests's for this date and time
   //
   try {

      pstmt1 = con.prepareStatement (
         "SELECT * FROM restriction2 WHERE sdate <= ? AND edate >= ? AND stime <= ? AND etime >= ? AND " +
         "(courseName = ? OR courseName = '-ALL-') AND (fb = ? OR fb = ?)");

      pstmt1.clearParameters();          // clear the parms
      pstmt1.setLong(1, date);
      pstmt1.setLong(2, date);
      pstmt1.setInt(3, time);
      pstmt1.setInt(4, time);
      pstmt1.setString(5, course);
      pstmt1.setString(6, rfb);
      pstmt1.setString(7, bfb);

      rs = pstmt1.executeQuery();      // find all matching restrictions, if any

      loop1:
      while (rs.next()) {

         rest_recurr = rs.getString("recurr");
         mrest_id = rs.getInt("id");
         
         for (i=0; i<memLimit; i++) {
            mtypeS[i] = rs.getString("mem" +(i+1));
         }
         for (i=0; i<mshipLimit; i++) {
            mshipS[i] = rs.getString("mship" +(i+1));
         }

         //
         //  We must check the recurrence for this day (Monday, etc.)
         //
         if ((rest_recurr.equals( "Every " + day )) ||          // if this day
             (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
             ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
               (!day.equalsIgnoreCase( "saturday" )) &&
               (!day.equalsIgnoreCase( "sunday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
              (day.equalsIgnoreCase( "saturday" ))) ||
             ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
              (day.equalsIgnoreCase( "sunday" )))) {

            
            //  
            //  Check for any suspensions
            //
            PreparedStatement pstmtSusp = con.prepareStatement(
                    "SELECT id FROM rest_suspend WHERE mrest_id = ? AND sdate <= ? AND edate >= ? " +
                    "AND stime <= ? AND etime >= ? AND " + day.toLowerCase() + " = 1 " +
                    "AND (eo_week = 0 OR (MOD(DATE_FORMAT(sdate, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2)))");
            
            pstmtSusp.clearParameters();
            pstmtSusp.setInt(1, mrest_id);
            pstmtSusp.setInt(2, (int)date);
            pstmtSusp.setInt(3, (int)date);
            pstmtSusp.setInt(4, time);
            pstmtSusp.setInt(5, time);
            pstmtSusp.setInt(6, (int)date);
            
            ResultSet rsSusp = pstmtSusp.executeQuery();
            
            if (rsSusp.next()) {
               
                suspend = true;
                status = false;
            }

            if (!suspend) {
                //
                //  Found a restriction that matches - check mtypes & mships
                //
                status = true;       // default to all specified

                loop2:
                for (i=0; i < mshipLimit; i++) {    // check each allowed mship type to see if it was specified

                   if (!mshipA[i].equals( "" )) {

                      loop3:
                      for (i2=0; i2 < mshipLimit; i2++) {    

                         if (!mshipA[i].equals( mshipS[i2] )) {

                            status = false;             // found one that does NOT match (was not set)
                            break loop3;
                         }
                      }

                      if (status == false) {

                         break loop2;
                      }
                   }
                }

                if (status == false) {         // if not all matched - check Mem types

                   status = true;              // default to all specified

                   loop4:
                   for (i=0; i < memLimit; i++) {    // check each allowed mtype type to see if it was specified

                      if (!mtypeA[i].equals( "" )) {

                         loop5:
                         for (i2=0; i2 < memLimit; i2++) {

                            if (!mtypeA[i].equals( mtypeS[i2] )) {

                               status = false;
                               break loop5;
                            }
                         }

                         if (status == false) {

                            break loop4;
                         }
                      }
                   }
                }
            }

            if (status == true) {         // if all match

               break loop1;               // exit and return the status
            }
         }     // end of 'day' if
      }       // end of while (no more restrictions)

      pstmt1.close();

   }
   catch (Exception e) {

      throw new UnavailableException("Error checking restrictions - getRests.checkRests " + e.getMessage());
   }

   return(status);
 }

 
/**
 //************************************************************************
 //
 //  determine whether or not to display restrictions based on if the restriction is
 //  suspended for the entire day or not.
 //  
 //  *NOTE*  When using mrest, pass -99 for grest and vice versa
 //
 //************************************************************************
 **/
 public static boolean showRest(int mrest_id, int grest_id, int stimeRest, int etimeRest, long date, String day, String courseName, Connection con) throws Exception {
     
     return showRest(mrest_id, grest_id, stimeRest, etimeRest, date, day, courseName, "", 0, 0, con);
 }
 
 public static boolean showRest(int mrest_id, int grest_id, int stimeRest, int etimeRest, long date, String day, String courseName, String locations, int sess_activity_id, int activity_id, Connection con) throws Exception {
              
     int stimeSusp = 0;
     int etimeSusp = 0;
     int rest_id = 0;
     
     String rest_type = "";
     String bind_query = "";
     String course_query = "";
     String[] locationsA = locations.split(",");
     
     if (grest_id == -99) { 
         rest_type = "mrest";
         rest_id = mrest_id;
     } else {
         rest_type = "grest";
         rest_id = grest_id;
     }

     if (rest_type.equals("mrest")) {
         bind_query = "LEFT OUTER JOIN restriction2 r ON r.id = s.mrest_id ";
     } else if (rest_type.equals("grest")) {
         bind_query = "LEFT OUTER JOIN guestres2 r ON r.id = s.grest_id ";
     }

     // Only use this clause when current viewing course is -ALL-
     if (courseName.equals("-ALL-")) {
         course_query = " OR (s.courseName = r.courseName)";
     }


     boolean showRest = true;
     
     // For FlxRez, see if this restriction applies to any locations in this activity/subactivity
     if (sess_activity_id > 0) {
         
         showRest = false;

         for (int i = 0; i < locationsA.length; i++) {
             if (getActivity.getParentIdFromActivityId(Integer.parseInt(locationsA[i]), con) == activity_id) {
                 showRest = true;
                 break;
             }
         }
     }
     
     if (showRest) {
         
         if (sess_activity_id == 0) {
             
             // Golf processing
             try {
                 ResultSet rsSusp = null;
                 PreparedStatement pstmtSusp = con.prepareStatement(
                         "SELECT s.stime, s.etime FROM rest_suspend s " +
                         bind_query +
                         "WHERE s." + rest_type + "_id = ? AND s." + day.toLowerCase() + "=1 " +
                         "AND s.sdate <= ? AND s.edate >= ? " +
                         "AND (s.eo_week = 0 OR (MOD(DATE_FORMAT(s.sdate, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2))) " +
                         "AND (s.courseName = ? OR s.courseName = '-ALL-'" + course_query + ")");

                 pstmtSusp.clearParameters();
                 pstmtSusp.setInt(1, rest_id);
                 pstmtSusp.setInt(2, (int)date);
                 pstmtSusp.setInt(3, (int)date);
                 pstmtSusp.setInt(4, (int)date);
                 pstmtSusp.setString(5, courseName);
                 rsSusp = pstmtSusp.executeQuery();

                 while (rsSusp.next()) {        // If restriction is suspended for the entire day, do not display it on the legend
                     stimeSusp = rsSusp.getInt("s.stime");
                     etimeSusp = rsSusp.getInt("s.etime");

                     if (stimeSusp <= stimeRest && etimeSusp >= etimeRest) {      // If suspension covers entire restriction period
                         showRest = false;
                     }
                 }

                 pstmtSusp.close();

             } catch (Exception exc) {
                 throw new UnavailableException("Error getting restriction suspension parms (golf) - getRests.showRest " + exc.getMessage());
             }
             
         } else {
             
             // FlxRez processing
             try {
                 ResultSet rsSusp = null;
                 PreparedStatement pstmtSusp = con.prepareStatement(
                         "SELECT s.stime, s.etime, s.locations FROM rest_suspend s " +
                         bind_query +
                         "WHERE s." + rest_type + "_id = ? AND s." + day.toLowerCase() + "=1 " +
                         "AND s.sdate <= ? AND s.edate >= ? " +
                         "AND (s.eo_week = 0 OR (MOD(DATE_FORMAT(s.sdate, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2)))");

                 pstmtSusp.clearParameters();
                 pstmtSusp.setInt(1, rest_id);
                 pstmtSusp.setInt(2, (int)date);
                 pstmtSusp.setInt(3, (int)date);
                 pstmtSusp.setInt(4, (int)date);
                 rsSusp = pstmtSusp.executeQuery();

                 while (rsSusp.next()) {        // If restriction is suspended for the entire day, do not display it on the legend
                     stimeSusp = rsSusp.getInt("s.stime");
                     etimeSusp = rsSusp.getInt("s.etime");

                     if (stimeSusp <= stimeRest && etimeSusp >= etimeRest) {      // If suspension covers entire restriction period
                         
                         showRest = false;
                         
                         ArrayList<Integer> activity_child_ids = getActivity.getChildrenForActivity(activity_id, con);
                         String[] suspLocationsA = rsSusp.getString("s.locations").split(",");

                         // Loop through children of this activity and see if they are affected by this restriction, and if so, if they're affected by this suspension.
                         mainLoop:
                         for (int i = 0; i < activity_child_ids.size(); i++) {
                             
                             subLoop1:
                             for (int j = 0; j < locationsA.length; j++) {
                              
                                 if (activity_child_ids.get(i) == Integer.parseInt(locationsA[j])) {
                                     
                                     if (!rsSusp.getString("s.locations").equals("")) {
                                         
                                         subLoop2:
                                         for (int k = 0; k < suspLocationsA.length; k++) {

                                             if (activity_child_ids.get(i) == Integer.parseInt(suspLocationsA[k])) {
                                                 continue mainLoop;
                                             }
                                         }
                                     }
                                     
                                     // If we hit on subLoop1, but fell through subLoop2, we know at least one location under this (sub)activity 
                                     // is covered by the restriction, but not suspended, meaning the restriction should be shown in the legend.
                                     showRest = true;
                                     break mainLoop;
                                 }
                             }                             
                         }
                     }
                 }

                 pstmtSusp.close();

             } catch (Exception exc) {
                 throw new UnavailableException("Error getting restriction suspension parms (FlxRez act_id: " + sess_activity_id + " - getRests.showRest " + exc.getMessage());
             }
         }
     }
     
     return showRest;
 }
 
}  // end of getRests class
