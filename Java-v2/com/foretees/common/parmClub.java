/***************************************************************************************
 *   parmClub:  This class will define a paramter block object to be used for Club Parms.
 *
 *
 *   called by:  several
 *
 *   created: 12/08/2003   Bob P.
 *
 *   last updated:
 *
 *     4/27/10  If no guest types found within constructor, initialize arrays to 1 instead of 0
 *     1/04/10  Add gDb array (int) to hold use_guestdb values for guest types
 *     7/07/08  Add 'Default Course' fields for customs to default course to a specific on or ALL (case 1513).
 *     5/14/08  Add 'Member Cutoff' options - days and time to cutoff tee time access to members (case 1480).
 *     4/17/08  Add Revenue option array for guest types (case 1400).
 *    10/24/07  Add new club option 'max_originations' for number of rounds that can be booked (originated) by a member
 *     5/08/07  Add 'viewdays' array for new member calendars (# of days members can view tee sheets).
 *     2/15/07  Add 'club' and 'course' strings for Greeley/Fort Collins custom.
 *     7/21/06  Add new club option no_reservations
 *     6/12/06  Add new club option - paceofplay
 *     2/26/05  Ver 5 - add new club option - precheckin
 *     1/05/05  Ver 5 - add new club options - constimesm & constimesp.
 *    11/16/04  Ver 5 - add new club options.
 *     9/15/04  Ver 5 - add more mem and mship types.
 *
 ***************************************************************************************
 */

package com.foretees.common;

import java.sql.*;

public class parmClub {

   public boolean error = false;
     
   public int MAX_Guests;// = Labels.MAX_GUESTS;         // IN Labels
   public int MAX_Mems = Labels.MAX_MEMS;
   public int MAX_Mships = Labels.MAX_MSHIPS;
   public int MAX_Tmodes = Labels.MAX_TMODES;
   //public int MAX_Courses = Labels.MAX_COURSES;

   public int multi = 0;
   public int lottery = 0;
   public int x = 0;
   public int xhrs = 0;
   public int adv_hr = 0;
   public int adv_min = 0;
   public int emailOpt = 0;
   public int hotel = 0;
   public int userlock = 0;
   public int unacompGuest = 0;
   public int hndcpProSheet = 0;
   public int hndcpProEvent = 0;
   public int hndcpMemSheet = 0;
   public int hndcpMemEvent = 0;
   public int rnds = 0;
   public int hrsbtwn = 0;
   public int forceg = 0;
   public int hiden = 0;
   public int constimesm = 0;         // consecutive tee times allowed for members
   public int constimesp = 0;         // consecutive tee times allowed for pros
   public int precheckin = 0;         // pre check-in flag
   public int paceofplay = 0;         // pace of play flag
   public int no_reservations = 0;    // TLT System flag
   public int max_originations = 0;   // number of tee times that can be originated by a member
   public int cutoffdays = 0;
   public int cutofftime = 0;

   public int foretees_mode = 0;
   public int genrez_mode = 0;
   
   public long lottid = 0;

   public String clubName = "";            // long name
   public String club = "";                // short name (site name)

   public String course = "";              // course name for Greeley & Fort Collins - to identify which
       
   public String default_course_mem = "";
   public String default_course_pro = "";

   public String contact = "";
   public String email = "";
   public String adv_ampm = "";
   public String adv_zone = "";
   public String posType = "";

   //
   //  The following 'days in adv' parms are used by verifySlot.getDaysInAdv
   //
   public int advdays1 = 0;
   public int advdays2 = 0;
   public int advdays3 = 0;
   public int advdays4 = 0;
   public int advdays5 = 0;
   public int advdays6 = 0;
   public int advdays7 = 0;
   public int advhr1 = 0;
   public int advhr2 = 0;
   public int advhr3 = 0;
   public int advhr4 = 0;
   public int advhr5 = 0;
   public int advhr6 = 0;
   public int advhr7 = 0;
   public int advmin1 = 0;
   public int advmin2 = 0;
   public int advmin3 = 0;
   public int advmin4 = 0;
   public int advmin5 = 0;
   public int advmin6 = 0;
   public int advmin7 = 0;
   public int advtime1 = 0;
   public int advtime2 = 0;
   public int advtime3 = 0;
   public int advtime4 = 0;
   public int advtime5 = 0;
   public int advtime6 = 0;
   public int advtime7 = 0;
   public int memviewdays = 0;
   public String advam1 = "";
   public String advam2 = "";
   public String advam3 = "";
   public String advam4 = "";
   public String advam5 = "";
   public String advam6 = "";
   public String advam7 = "";
   //  end of 'days in adv' parms

   //
   //  guest arrays
   //
   public String [] guest;          // guest types
   public int [] gOpt;              // guest type options
   public int [] gRev;              // guest type Revenue options
   public int [] gDb;               // guest type guest db option
   public String [] newguest;       // new guest types
/*
   public String [] guest = new String [MAX_Guests];     // guest types
   public int [] gOpt = new int [MAX_Guests];            // guest type options
   public int [] gRev = new int [MAX_Guests];            // guest type Revenue options
   public int [] gDb = new int [MAX_Guests];             // guest type guest db option
*/

   //
   //  Modes of Transportation (from clubparm2 table)
   //
   public String [] tmode = new String [MAX_Tmodes];        // current trans modes
   public String [] tmodeNew = new String [MAX_Tmodes];     // new trans modes (when changed in config)

   //
   //  Member types
   //
   public String [] mem = new String [MAX_Mems];          // new member types

   //
   //  Mship types
   //
   public String [] mship = new String [MAX_Mships];        // new mship types
   public String [] period = new String [MAX_Mships];
   public String [] advamd1 = new String [MAX_Mships];
   public String [] advamd2 = new String [MAX_Mships];
   public String [] advamd3 = new String [MAX_Mships];
   public String [] advamd4 = new String [MAX_Mships];
   public String [] advamd5 = new String [MAX_Mships];
   public String [] advamd6 = new String [MAX_Mships];
   public String [] advamd7 = new String [MAX_Mships];
   public int [] mtimes = new int [MAX_Mships];
   public int [] days1 = new int [MAX_Mships];
   public int [] days2 = new int [MAX_Mships];
   public int [] days3 = new int [MAX_Mships];
   public int [] days4 = new int [MAX_Mships];
   public int [] days5 = new int [MAX_Mships];
   public int [] days6 = new int [MAX_Mships];
   public int [] days7 = new int [MAX_Mships];
   public int [] advhrd1 = new int [MAX_Mships];
   public int [] advhrd2 = new int [MAX_Mships];
   public int [] advhrd3 = new int [MAX_Mships];
   public int [] advhrd4 = new int [MAX_Mships];
   public int [] advhrd5 = new int [MAX_Mships];
   public int [] advhrd6 = new int [MAX_Mships];
   public int [] advhrd7 = new int [MAX_Mships];
   public int [] advmind1 = new int [MAX_Mships];
   public int [] advmind2 = new int [MAX_Mships];
   public int [] advmind3 = new int [MAX_Mships];
   public int [] advmind4 = new int [MAX_Mships];
   public int [] advmind5 = new int [MAX_Mships];
   public int [] advmind6 = new int [MAX_Mships];
   public int [] advmind7 = new int [MAX_Mships];
   public int [] viewdays = new int [MAX_Mships];

   //
   //  save areas for new types
   //
   //public String [] newguest = new String [MAX_Guests];     // new guest types
   public String [] newmem = new String [MAX_Mems];         // new member types
   public String [] newmship = new String [MAX_Mships];     // new mship types
   
   // Hack for dining guests until proshop configuration for dining guests is complette
   public String [] dining_guests = new String[]{"Guest","Child","TBD"};
   
   public boolean fivesomes = false;
   public boolean use_memberPhotos = false;


   public parmClub(int activity_id, Connection con) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {

        pstmt = con.prepareStatement("SELECT COUNT(*) FROM guest5 WHERE activity_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);
        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            MAX_Guests = rs.getInt(1);
        
        }

        if(MAX_Guests == 0 && activity_id == ProcessConstants.DINING_ACTIVITY_ID && dining_guests.length > 0){
           MAX_Guests = dining_guests.length;
        } else if (MAX_Guests == 0) {
            MAX_Guests = 1;
        }

        newguest = new String [MAX_Guests];
        guest = new String [MAX_Guests];
        gOpt = new int [MAX_Guests];
        gRev = new int [MAX_Guests];
        gDb = new int [MAX_Guests];


    } catch (Exception e) {

        //Utilities.logError("Error during init of parmClub for " + getClub.getClubName(con) + ", err=" + e.getMessage());
        Utilities.logError("Error during init of parmClub for unknown club, club=" + club + ", err=" + e.getMessage() + ", strace=" + Utilities.getStackTraceAsString(e));

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
    

    
   }
   
   
   
   
}  // end of class
