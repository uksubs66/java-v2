/***************************************************************************************
 *   getParms:  This servlet will provide some common methods to get Course parms.
 *
 *
 *
 *   created:  2/06/2004   Bob P.
 *
 *
 *   last updated:
 *
 *     12/05/11 PTS  Update getCourse to better report error and ensure db objects are released
 *      2/04/09 BSK  Tweak to getCourseTrans so tOpt is updated if existing entry is found.
 *      6/16/08 BSK  Methods now populate tOpt array in parmCourse as well
 *      2/07/07 RDP  If Greeley CC, make sure that GC is first mode of trans listed.
 *     11/05/04 RDP  Allow for course to be -ALL-, get all modes of trans.
 *
 ***************************************************************************************
 */


package com.foretees.common;

import java.sql.*;


public class getParms {


/**
 //************************************************************************
 //
 //  Get the Course parms
 //
 //************************************************************************
 **/

 public static void getCourse(Connection con, parmCourse parm, String course)
         throws Exception {


   PreparedStatement pstmtc = null;
   ResultSet rs = null;

   String err = "DB Error retrieving tmode info";

   //
   //  get the Course's parameters
   //
   try {

      if (course.equals( "-ALL-" ) || course.equals( "" )) {

         pstmtc = con.prepareStatement (
            "SELECT * " +
            "FROM clubparm2");

         pstmtc.clearParameters();        // clear the parms

      } else {

         pstmtc = con.prepareStatement (
            "SELECT * " +
            "FROM clubparm2 WHERE courseName = ?");

         pstmtc.clearParameters();        // clear the parms
         pstmtc.setString(1, course);
      }

      rs = pstmtc.executeQuery();      // execute the prepared stmt

      if (rs.next()) {                        

         parm.courseName = rs.getString("courseName");
         parm.first_hr = rs.getInt("first_hr");
         parm.first_min = rs.getInt("first_min");
         parm.last_hr = rs.getInt("last_hr");
         parm.last_min = rs.getInt("last_min");
         parm.betwn = rs.getInt("betwn");
         parm.xx = rs.getInt("xx");
         parm.alt = rs.getInt("alt");
         parm.fives = rs.getInt("fives");       
         
         for (int i=0; i < parm.tmode_limit; i++) {
             
             parm.tmode[i] = rs.getString("tmode" + String.valueOf(i + 1));
             parm.tmodea[i] = rs.getString("tmodea" + String.valueOf(i + 1));
             parm.t9pos[i] = rs.getString("t9pos" + String.valueOf(i + 1));
             parm.tpos[i] = rs.getString("tpos" + String.valueOf(i + 1));
             parm.tOpt[i] = rs.getInt("tOpt" + String.valueOf(i + 1));
             
             // if a Pro-Only tmode is, set boolean flag in parmCourse object
             if (parm.tOpt[i] == 1) {
                 parm.hasProOnlyTmodes = true;
             }
         }
      }

      err = "Error parsing tmode results";

      //
      //  determine how many tmodes are actually specified - save count
      //
      parm.tmode_count = 0;
        
      for (int i = 0; i < parm.tmode_limit; i++) {
        
         if (!parm.tmodea[i].equals( "" )) {    // if specified
           
            parm.tmode_count++;
         }
      }

   } catch (Exception e) {

      throw new Exception(err + " - getParms.getCourse " + e.toString());

   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmtc.close(); }
        catch (Exception ignore) {}

   }

 }   // end of getCourse method


/**
 //************************************************************************
 //
 //  Get the Transportation Modes for a single course
 //
 //************************************************************************
 **/

 public static void getTmodes(Connection con, parmCourse parm, String course)
         throws Exception {


   ResultSet rs = null;
   PreparedStatement pstmtc = null;

   //
   //  get the Course's tmode parameters 
   //
   try {

      if (course.equals( "-ALL-" ) || course.equals( "" )) {
        
         pstmtc = con.prepareStatement (
            "SELECT tmode1, tmodea1, tmode2, tmodea2, tmode3, tmodea3, tmode4, tmodea4, tmode5, tmodea5, " +
            "tmode6, tmodea6, tmode7, tmodea7, tmode8, tmodea8, tmode9, tmodea9, tmode10, tmodea10, tmode11, tmodea11, " +
            "tmode12, tmodea12, tmode13, tmodea13, tmode14, tmodea14, tmode15, tmodea15, tmode16, tmodea16, " +
            "tOpt1, tOpt2, tOpt3, tOpt4, tOpt5, tOpt6, tOpt7, tOpt8, tOpt9, tOpt10, tOpt11, tOpt12, tOpt13, tOpt14, tOpt15, tOpt16 " +
            "FROM clubparm2");

         pstmtc.clearParameters();        // clear the parms

      } else {

         pstmtc = con.prepareStatement (
            "SELECT tmode1, tmodea1, tmode2, tmodea2, tmode3, tmodea3, tmode4, tmodea4, tmode5, tmodea5, " +
            "tmode6, tmodea6, tmode7, tmodea7, tmode8, tmodea8, tmode9, tmodea9, tmode10, tmodea10, tmode11, tmodea11, " +
            "tmode12, tmodea12, tmode13, tmodea13, tmode14, tmodea14, tmode15, tmodea15, tmode16, tmodea16, " +
            "tOpt1, tOpt2, tOpt3, tOpt4, tOpt5, tOpt6, tOpt7, tOpt8, tOpt9, tOpt10, tOpt11, tOpt12, tOpt13, tOpt14, tOpt15, tOpt16 " +
            "FROM clubparm2 WHERE courseName = ?");
              
         pstmtc.clearParameters();        // clear the parms
         pstmtc.setString(1, course);
      }

      rs = pstmtc.executeQuery();      // execute the prepared stmt

      if (rs.next()) { 
          
          for (int i=0; i < parm.tmode_limit; i++) {
             
             parm.tmode[i] = rs.getString("tmode" + String.valueOf(i + 1));
             parm.tmodea[i] = rs.getString("tmodea" + String.valueOf(i + 1));
             parm.tOpt[i] = rs.getInt("tOpt" + String.valueOf(i + 1));
             
             // if a Pro-Only tmode is, set boolean flag in parmCourse object
             if (parm.tOpt[i] == 1) {
                 parm.hasProOnlyTmodes = true;
             }
         }
      }
      pstmtc.close();

   }
   catch (Exception e) {

      throw new Exception("Error getting guest info - getParms.getTmodes " + e.getMessage());
   }

   if (course.equals( "Greeley CC" )) {      // Greeley CC course is at Fort Collins (they share)

      String tmode = parm.tmode[0];          // save first entry          
      String tmodea = parm.tmodea[0];
        
      //
      //  Make sure the GC is first in list so it will be the defualt
      //
      if (!tmodea.equals( "GC" )) {       // if not GC
        
         loop1:  
         for (int i=1; i<16; i++) {       // loop through list starting with #2
           
            if (parm.tmodea[i].equals( "GC" )) {

               parm.tmode[0] = parm.tmode[i];          // put the full name in #1
               parm.tmodea[0] = "GC";                  // set short name 
                 
               parm.tmodea[i] = tmodea;                // put #1 here (swap)
               parm.tmode[i] = tmode;             
                 
               break loop1;
            }
         }
      }
   }

 }   // end of getTmodes method


/**
 //************************************************************************
 //
 //  Get the Transportation Modes for all courses
 //
 //************************************************************************
 **/

 public static void getCourseTrans(Connection con, parmCourse parm)
         throws Exception {


   ResultSet rs = null;

   String tmode = "";
   String tmodea = "";
     
   int i = 0;
   int i2 = 0;
   int tOpt = 0;
   int zero = 0;

   boolean found = false;

   //
   //  init the parm save areas
   //
   for (i2 = 0; i2 < parm.tmode_limit; i2++) {

       parm.tmode[i2] = "";
       parm.tmodea[i2] = "";
   }

   //
   //  get all Course's Mode of Trans Options 
   //
   try {

      PreparedStatement pstmtc = con.prepareStatement (
         "SELECT tmode1, tmodea1, tmode2, tmodea2, tmode3, tmodea3, tmode4, tmodea4, tmode5, tmodea5, " +
         "tmode6, tmodea6, tmode7, tmodea7, tmode8, tmodea8, tmode9, tmodea9, tmode10, tmodea10, tmode11, tmodea11, " +
         "tmode12, tmodea12, tmode13, tmodea13, tmode14, tmodea14, tmode15, tmodea15, tmode16, tmodea16, " +
         "tOpt1, tOpt2, tOpt3, tOpt4, tOpt5, tOpt6, tOpt7, tOpt8, tOpt9, tOpt10, tOpt11, tOpt12, tOpt13, tOpt14, tOpt15, tOpt16 " +
         "FROM clubparm2 WHERE first_hr > ?");

      pstmtc.clearParameters();        // clear the parms
      pstmtc.setInt(1, zero);
      rs = pstmtc.executeQuery();      // execute the prepared stmt

      while (rs.next()) {
           
         for (i = 0; i < parm.tmode_limit; i++) {       // get all 16 tmode pairs

            tmode = rs.getString("tmode" + String.valueOf(i + 1));
            tmodea = rs.getString("tmodea" + String.valueOf(i + 1));
            tOpt = rs.getInt("tOpt" + String.valueOf(i + 1));
            

            if (!tmodea.equals( "" )) {                     // if specified
              
               found = false;
                 
               //
               //  check if tmode already exists in parm block
               //
               for (i2 = 0; i2 < parm.tmode_limit; i2++) {

                  if (tmodea.equals( parm.tmodea[i2] )) {             // if already exists

                     found = true;
                     
                     if (tOpt == 1) {
                         parm.tOpt[i2] = 1;
                         parm.hasProOnlyTmodes = true;
                     }
                  }
               }

               if (found == false) {          // if not found

                  i2 = 0;
                  loop1:
                  while (i2 < parm.tmode_limit) {

                     if (parm.tmode[i2].equals( "" )) {        // if spot is open

                        parm.tmode[i2] = tmode;
                        parm.tmodea[i2] = tmodea;
                        parm.tOpt[i2] = tOpt;
                        
                        // if a Pro-Only tmode is, set boolean flag in parmCourse object
                        if (tOpt == 1) {
                            parm.hasProOnlyTmodes = true;
                        }
                        
                        break loop1;
                     }
                     i2++;
                  }
               }
            }
         }                // end of FOR
      }                   // end of WHILE
      pstmtc.close();

   }
   catch (Exception e) {

      throw new Exception("Error getting guest info - getParms.getCourseTrans " + e.getMessage());
   }

 }   // end of getCourseTrans method

}  // end of getParms class

